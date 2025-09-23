/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */

package com.ibm.websphere.update.delta.earutils;

/*
 *  @ (#) WebuiEARActor.java
 *
 *  Perform Webui EAR pre and post processing operations.
 *
 *  This class modifes the superclass defined operations to
 *  provide a webui specific implementation.
 *
 *  The processing for webui is:
 *
 *  In order of processing steps:
 *
 *      * When a deployment is present, skip expanding the
 *        installable copy of the webui EAR.
 *
 *      * When no deployment is present, expand the installable
 *        application to a temporary location.
 *
 *      * The core update function updates the expanded installable
 *        webui application (if present), and any deployed webui
 *        applications (in the installed location).
 *
 *      * When a deployment is present, the post processing steps
 *        are to perform plugin-processing on the updated deployed
 *        application, then compress that application into the
 *        configuration based applications directory, then copy that
 *        compressed application into the 'installable' directory.
 *
 *      * When no deployment is present, the post processing steps
 *        are to run plugin-processing on the updated installable
 *        application, then to compress that application from the
 *        temporary working location into the installable location.
 * 
 *  Split 'not deployed' and 'deployed':
 *
 *  Deployed:
 *
 *      Pre: None
 *      Core: Update 'installed'.
 *      Post: Update 'installed';
 *            Plugin process 'installed';
 *            Compress 'installed' to 'applications';
 *            Copy 'applications' to 'installable'.
 *
 *  Not Deployed:
 *
 *      Pre: Expand 'installable' to a temporary directory.
 *      Core: Update the temporary 'installable'.
 *      Post: Plugin process the temporary 'installable';
 *            Compress the temporary 'installable to 'installable'.
 *
 *  @author     venkataraman
 *  @created    November 20, 2002
 */

import java.io.*;
import java.util.*;

public class WebuiEARActor extends ExtendedEARActor
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Top of the world: instiator
    //
    // public WebuiEARActor(String, StringBuffer, StringBuffer,
    //                      String, boolean, boolean);
    // public WebuiEARActor(String, StringBuffer, StringBuffer,
    //                      String, boolean, boolean, boolean, boolean);

    public WebuiEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName,
                         boolean doDeploy, boolean doPluginDeploy)
    {
        this(installPath,
             messages, errors,
             earName,
             doDeploy, doPluginDeploy,
             false, false);
    }
    
    public WebuiEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName,
                         boolean doDeploy, boolean doPluginDeploy,
                         boolean installableOnly, boolean applicationOnly)
    {
        super(installPath, messages, errors,
              earName,
              doDeploy, doPluginDeploy,
              installableOnly, applicationOnly);
    }

    // Deployment helper:
    //
    // boolean isDeployed(String);

    protected boolean isDeployed(String earName)
    {
        String binariesLocation = getCorrectedBinariesLocation(earName);

        return ( (binariesLocation != null) &&
                 earIsPresent(binariesLocation) );
    }

    // Pre-processing overrides:
    //
    // boolean preProcessWithRule(String, boolean, boolean);
    // boolean preProcess(String, boolean, boolean);

    protected boolean preProcessWithRule(String earName,
                                         boolean installableOnly,
                                         boolean applicationOnly)
    {
        log("Strange: Noted rule on EAR " + earName + " for WebUI pre-processing.");
        return true;
    }

    protected boolean preProcess(String earName,
                                 boolean installableOnly,
                                 boolean applicationOnly)
    {
        if ( isDeployed(earName) ) {
            log("EAR " + earName + " is deployed; skipping WebUI pre-processing.");
            return true;

        } else if ( uncompress(earName, INSTALLABLE_EAR) ) {
            log("Preprocessing WebUI EAR: " + earName + ": OK");
            return true;

        } else {
            logError("Preprocessing WebUI EAR: " + earName + ": Failed");
            return false;
        }
    }

    // Post-processing overrides:
    //
    // boolean basicPostProcess(boolean);

    protected boolean basicPostProcess(boolean doDeploy)
    {
        Iterator instances = getInstances();
        if ( instances == null ) {
            log("No instances; failing post-processing.");
            return false;
        }

        Vector earCommands = getCommandData();
        int numEarCommands = earCommands.size();

        boolean stillOK = true;

        for ( int commandNo = 0; stillOK && (commandNo < numEarCommands); commandNo++ ) {
            EARCommandData nextCommand =
                (EARCommandData) earCommands.elementAt(commandNo); 

            while ( stillOK && instances.hasNext() ) {
                InstanceData nextInstance = (InstanceData) instances.next();
                setInstance(nextInstance); 

                String nextEarName = nextCommand.earName;

                if ( isDeployed(nextEarName) ) {
                    stillOK =
                        invokePPOnInstalled(nextEarName) &&
                        compressToApplications(nextEarName) &&
                        copyToInstallable(nextEarName);

                    if ( !stillOK )
                        log("Failed on WebUI post processing for deployed " + nextEarName);

                } else {
                    stillOK =
                        invokePPOnInstallable(nextEarName) &&
                        postProcessCompress(nextCommand);

                    if ( !stillOK )
                        log("Failed on WebUI post processing for installable " + nextEarName);
                }
            }
        }

        if ( stillOK )
            log("Post-processing succeeded.");

        return stillOK;
    }

    protected boolean compressToApplications(String earName)
    {
        String uncompressedPath = getCorrectedBinariesLocation(earName);
        String compressedPath = getCompressedPath(earName, EARActor.APPLICATION_EAR);

        log("Removing original compressed EAR");

        if ( !removeFully(compressedPath) ) {
            logError("Unable to remove original compressed EAR: " + compressedPath);
            return false;

        } else {
            log("Removing original compressed EAR: OK");
        }

        return performCompressCommand(compressedPath, uncompressedPath);
    }

    protected boolean copyToInstallable(String earName)
    {
        String sourcePath = getCompressedPath(earName, EARActor.APPLICATION_EAR);
        String targetPath = getCompressedPath(earName, EARActor.INSTALLABLE_EAR);

        return performCopyCommand(sourcePath, targetPath);
    }

    protected boolean copyDelete(String sourcePath, String targetPath)
    {
        if ( !performCopyCommand(sourcePath, targetPath) )
            return false;

        return performDeleteCommand(sourcePath);
    }
}
