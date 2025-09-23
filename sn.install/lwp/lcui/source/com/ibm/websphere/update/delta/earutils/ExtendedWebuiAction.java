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
 *  @ (#) ExtendedWebuiAction.java
 *
 *  Overload superclass 'ExtendedEARAction' to redefine
 *  'createActor' to return a WebuiEARActor.
 *
 *  @author     Thomas F. Bitonti
 *  @created    02-May-2003
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.*;

public abstract class ExtendedWebuiAction extends ExtendedEARAction
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Answer a new extended webui post actor.

    public ExtendedWebuiAction()
    {
        super();
    }

    // Answer the EARActor instance to be used by the receiver.

    // Want this to return 'WebuiEARActor', but java doesn't allow
    // specialization of the return type.  Bummer.

    public ExtendedEARActor createActor(String installPath,
                                        StringBuffer messages,
                                        StringBuffer errors,
                                        String earName,
                                        boolean doDeploy,
                                        boolean doPluginDeploy,
					boolean installableOnly,
					boolean applicationOnly)
    {
        return new WebuiEARActor(installPath,
				 messages, errors,
				 earName,
				 doDeploy, doPluginDeploy,
				 installableOnly, applicationOnly);
    }

    // 'basicProcess' is still abstract.

    protected abstract boolean basicProcess(ExtendedEARActor actor);
}
