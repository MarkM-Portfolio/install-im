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
 * Created on Feb 22, 2005
 *
 * @author smp
 * 
 */
public abstract class ExtendedWCMEarAction extends ExtendedWPEarAction
{
    public final static String pgmVersion = "1.0" ;
    public final static String pgmUpdate = "02/22/05" ;

    // Answer a new extended webui post actor.

    public ExtendedWCMEarAction() {
        super();
    }

    // Answer the EARActor instance to be used by the receiver.

    // Want this to return 'WPEARActor', but java doesn't allow
    // specialization of the return type.  Bummer.

    public ExtendedEARActor createActor(String installPath,
                                        StringBuffer messages,
                                        StringBuffer errors,
                                        String earName,
                                        boolean doDeploy,
                                        boolean doPluginDeploy,
                                        boolean installableOnly,
                                        boolean applicationOnly) {
       return new WCMEarActor( getWasHomeDir(),
                               messages, errors,
                               earName,
                               doDeploy, doPluginDeploy,
                               installableOnly, applicationOnly);
    }


}
