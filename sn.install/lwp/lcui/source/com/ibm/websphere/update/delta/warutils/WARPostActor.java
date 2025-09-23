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
 *  @ (#) WARPostActor.java
 *
 * Class for coordinating the operations to post-process EAR files.
 * This is an abstract class: A subclass must implement 'getActor'
 * to answer the proper actor instance.
 *
 *  @author     eedavis
 *  @created    March 2005
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.*;

public abstract class WARPostActor extends UpdateAction
{
   public final static String pgmVersion = "1.1" ;
   public final static String pgmUpdate = "4/6/05" ;

    // Answer a new EAR post actor.

    public WARPostActor()
    {
        super();
    }

    //  Answer the WARActor instance to be used by the receiver.

    public abstract WARActor getActor(String installPath,
                                      StringBuffer messages,
                                      StringBuffer errors);

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

    // Perform processing.  This is deferred to the actor.

    public int process(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errors,
                       boolean debug)
    {
        String installPath = root;

        WARActor actor = getActor(installPath, messages, errors);

        if (actor.postProcess(getDeploymentFlag()))
            return getSuccessExitCode();
        else
            return getErrorExitCode();
    }

    // Answer the exit code to be displayed if an error occurred.

    protected int getErrorExitCode()
    {
        return WARActor.ERROR_EXIT_CODE;
    }

    // Answer the exit code to be displayed if operations were
    // successful.

    protected int getSuccessExitCode()
    {
        return WARActor.SUCCESS_EXIT_CODE;
    }
}
