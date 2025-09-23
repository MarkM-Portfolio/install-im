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
public class ExtendedWCMEarPreActor extends ExtendedWCMEarAction
{
    public final static String pgmVersion = "1.0" ;
    public final static String pgmUpdate = "02/22/05" ;

    public ExtendedWCMEarPreActor() {
        super();
    }

    
    protected boolean basicProcess(ExtendedEARActor actor) {
        return actor.preProcess();
    }

}
