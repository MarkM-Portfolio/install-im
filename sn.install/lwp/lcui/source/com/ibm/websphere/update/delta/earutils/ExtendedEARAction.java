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
 *  @ (#) ExtendedEARAction.java
 *
 * This concrete class used filter file argument to create the
 * command data.  This is a support class containing common
 * code for the two extended actor classes.
 *
 *  @author     venkataraman
 *  @created    November 20, 2002
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.*;

public abstract class ExtendedEARAction extends ExtendedUpdateAction
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Answer a new extended EAR post actor.

    public ExtendedEARAction()
    {
        super();
    }

    //  Answer the EARActor instance to be used by the receiver.

    public ExtendedEARActor createActor(String installPath,
                                        StringBuffer messages,
                                        StringBuffer errors,
                                        String earName,
                                        boolean doDeploy,
                                        boolean doPluginDeploy,
					boolean installableOnly,
					boolean applicationOnly)
    {
        return new ExtendedEARActor(installPath,
				    messages, errors,
                                    earName,
				    doDeploy, doPluginDeploy,
				    installableOnly, applicationOnly);
    }

    public ExtendedEARActor createActor(String installPath,
                                        StringBuffer messages,
                                        StringBuffer errors,
                                        Vector args)
    {
        if ( args.size() == 0 ) {
            errors.append("No EAR name argument is present.");
            return null;
        }

        Object argument0 = args.elementAt(0);

        String earName;

        try { 
            earName = (String) argument0;
        } catch ( ClassCastException e ) {
            errors.append("EAR actor argument one is not an EAR name: " + argument0 + ".\n");
            return null;
        }

        boolean doDeploy;

        if ( args.size() > 1 ) {
            Object argument1 = args.elementAt(1);

            String deployText;

            try { 
                deployText = (String) argument1;
            } catch ( ClassCastException e ) {
                errors.append("EAR actor argument two is not text for a boolean value: " + argument1 + ".\n");
                return null;
            }

            if ( ExtendedEARActor.isTrue(deployText) ) {
                doDeploy = true;
            } else if ( ExtendedEARActor.isFalse(deployText) ) {
                doDeploy = false;
            } else {
                errors.append("EAR actor argument two is not a valid boolean value: " + argument1 + ".\n");
                return null;
            }

        } else {
            doDeploy = false;
        }


        boolean doPluginDeploy;

        if ( args.size() > 2 ) {
            Object argument2 = args.elementAt(2);

            String pluginDeployText;

            try { 
                pluginDeployText = (String) argument2;
            } catch ( ClassCastException e ) {
                errors.append("EAR actor argument three is not text for a boolean value: " + argument2 + ".\n");
                return null;
            }

            if ( ExtendedEARActor.isTrue(pluginDeployText) ) {
                doPluginDeploy = true;
            } else if ( ExtendedEARActor.isFalse(pluginDeployText) ) {
                doPluginDeploy = false;
            } else {
                errors.append("EAR actor argument three is not a valid boolean value: " + argument2 + ".\n");
                return null;
            }

        } else {
            doPluginDeploy = false;
        }

	boolean installableOnly;
	boolean applicationOnly;

	if ( args.size() > 3 ) {
	    String limitTag = (String) args.elementAt(3);

	    if ( ExtendedEARActor.isInstallableOnly(limitTag) ) {
		installableOnly = true;
		applicationOnly = false;
	    } else if ( ExtendedEARActor.isApplicationOnly(limitTag) ) {
		installableOnly = false;
		applicationOnly = true;
	    } else {
		installableOnly = false;
		applicationOnly = false;

		errors.append("EAR actor argument four is not a valid limit;" +
			      " one of " + ExtendedEARActor.INSTALLABLE_ONLY_TAG +
			      " or " + ExtendedEARActor.APPLICATION_ONLY_TAG +
			      " is required.");
	    }
	} else {
	    installableOnly = false;
	    applicationOnly = false;
	}

        return createActor(installPath, messages, errors,
                           earName,
			   doDeploy, doPluginDeploy,
			   installableOnly, applicationOnly);
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

        ExtendedEARActor actor = createActor(installPath, messages, errors, args);

        if ( (actor != null) && basicProcess(actor) )
            return getSuccessExitCode();
        else
            return getErrorExitCode();
    }

    protected int getErrorExitCode()
    {
        return EARActor.ERROR_EXIT_CODE;
    }

    protected int getSuccessExitCode()
    {
        return EARActor.SUCCESS_EXIT_CODE;
    }

    protected abstract boolean basicProcess(ExtendedEARActor actor);
}
