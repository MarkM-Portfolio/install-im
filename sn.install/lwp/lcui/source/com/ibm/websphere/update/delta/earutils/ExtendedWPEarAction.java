/*
********************************************************************
* IBM Confidential                                                 *
*                                                                  *
* OCO Source Materials                                             *
*                                                                  *
*                                                                  *
* Copyright IBM Corp. 2003, 2015                                   *
*                                                                  *
* The source code for this program is not published or otherwise   *
* divested of its trade secrets, irrespective of what has been     *
* deposited with the U.S. Copyright Office.                        *
********************************************************************
*/
package com.ibm.websphere.update.delta.earutils;

import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

/*
 *  @ (#) ExtendedWebuiAction.java
 *
 *  Overload superclass 'ExtendedEARAction' to redefine
 *  'createActor' to return a WpsEarEARActor.
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.*;

public abstract class ExtendedWPEarAction extends ExtendedEARAction
{
   public final static String pgmVersion = "1.3" ;
   public final static String pgmUpdate = "2/27/04" ;

    // Answer a new extended webui post actor.

    public ExtendedWPEarAction() {
        super();
    }

    protected String getWasHomeDir() {
        // need to get WasHome and use that for installPath.
       String wasHomeDirName  = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT );

       File wasHomeFile = new File(wasHomeDirName);
       wasHomeDirName = wasHomeFile.getAbsolutePath();
       return wasHomeDirName;
    }

    protected abstract boolean basicProcess(ExtendedEARActor actor);

    public boolean getDeploymentFlag() {
        return true;
    }


    // Answer the vector of files which are to be backed up.
    // This is provided to allow the process step to modify
    // files which are in addition to those which are modified
    // by the normal extractor application.
    //
    // The arguments to 'file2Backup' are the same as to the
    // 'process' method, so to allow the same level of detail
    // in detecting the files to backup as during processing.
    //
    // 'file2Backup' is sent to a different instance than 'process'.
    //
    // The arguments are:
    //
    //    'root'           the target directory for the update
    //    'components'     obsolete
    //    'po'             the options process from the extractor
    //    'messages'       a buffer to messages to be logged
    //    'errorMessage'   a buffer for error emssages
    //    'debug'          debug enablement flag from the extractor
    //
    // The result vector is a list of files which are to be backed up
    // before performing processing.  

    // This is only valid for EntryScripts, and all returnd files are 
    // expected to be absolutePaths
    public Vector file2Backup(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errors,
                       boolean debug,
                       Vector args)
    {
        String installPath = root;

        WPEarActor actor = (WPEarActor)createActor(installPath, messages, errors, args);

        Vector backupFiles = null;
        if ( actor != null ) {
            String earFileName = actor.getEarInstalledLocation() + File.separator + args.elementAt(0);
            // Make sure the Ear exists before requesting it to be backedup.
            File earFile = new File( earFileName );
            if ( earFile.exists() ) {
                backupFiles = new Vector();
                backupFiles.add( earFileName );
            }
        }
        return backupFiles;
    }



}
