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
package com.ibm.websphere.update.delta.warutils;

/*
 *  @ (#) ExtendedWARAction.java
 *
 * This concrete class used filter file argument to create the
 * command data.  This is a support class containing common
 * code for the two extended actor classes.
 *
 *  @author     eedavis
 *  @created    March, 2005
 */

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.File;
import java.util.*;

/**
 *  
 */
public abstract class ExtendedWARAction extends ExtendedUpdateAction
{
    public final static String pgmVersion = "1.1" ;
    public final static String pgmUpdate = "4/6/05" ;
    
    protected Extractor extractor;


    public ExtendedWARAction()
    {
        super();
        extractor = new Extractor();
    }

    //  Answer the WARActor instance to be used by the receiver.
    /*
     * as the 'warutils' pkg does not support them, this constructor does not support:
     *                                  doDeploy,
     *                                  doPluginDeploy,
     *                                  installableOnly,
     *                                  applicationOnly
     * 
     * if those functions are ever supported, they will need to be handled and passed
     * to the ExtendedWarActor constructor. ExtendedWarActor will need to be modified
     * to accept the new arg(s) as well.
     * 
     */
    public ExtendedWARActor createActor(String installPath,
                                        StringBuffer messages,
                                        StringBuffer errors,
                                        String warName,
                                        String warInstallPath)
    {
        return new ExtendedWARActor(installPath,
                                    messages, 
                                    errors,
                                    warName,
                                    warInstallPath);
    }

    public ExtendedWARActor createActor(String installPath,
                                        StringBuffer messages,
                                        StringBuffer errors,
                                        Vector args)
    {
        if ( args.size() == 0 ) {
            errors.append("No WAR name argument is present.");
            return null;
        }

        Object argument0 = args.elementAt(0);

        String warName;

        try { 
            warName = (String) argument0;
        } catch ( ClassCastException e ) {
            errors.append("WAR actor argument one is not a WAR name: " + argument0 + ".\n");
            return null;
        }

        if ( args.size() < 2 ) {
            errors.append("No WAR install location argument is present.");
            return null;
        }

        Object argument1 = args.elementAt(1);

        String warInstallPath;

        try { 
            warInstallPath = (String) argument1;
        } catch ( ClassCastException e ) {
            errors.append("WAR actor argument one is not a WAR install location: " + argument1 + ".\n");
            return null;
        }
        // resolve $< macros before continuing (we can see 'extractor' from here)
        if ( -1 != warInstallPath.indexOf( "$<" )) { 
            warInstallPath = extractor.ResolveMacro( warInstallPath );
        } else {
            warInstallPath = WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + warInstallPath;
        }

        
        return createActor(installPath, 
                           messages, 
                           errors,
                           warName,
                           warInstallPath);
    }
    
    // Answer true or false, telling if deployment is to be performed
    // during post processing.  This flag, if false, prevents any
    // deployment, regardless of settings in the actor."
    //
    // Default this to false; subclasses must override to enable
    // deployment.

    public boolean getDeploymentFlag()
    {
        return false;
    }

    // Perform processing.  This is delegated to the actor.

    public int process(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errors,
                       boolean debug,
                       Vector args)
    {
        String installPath = root;

        ExtendedWARActor actor = createActor(installPath, messages, errors, args);

        if ( (actor != null) && basicProcess(actor) )
            return getSuccessExitCode();
//        else
            return getErrorExitCode();
    }

    protected int getErrorExitCode()
    {
        return WARActor.ERROR_EXIT_CODE;
    }

    protected int getSuccessExitCode()
    {
        return WARActor.SUCCESS_EXIT_CODE;
    }

    protected abstract boolean basicProcess(ExtendedWARActor actor);
}
