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
 *  @ (#) ExtendedEARPostActor.java
 *
 * Class for coordinating the operations to post-process webui files.
 *
 *  @author     Thomas F. Bitonti
 *  @created    02-Mar-2003
 */

public class ExtendedWebuiPostActor extends ExtendedWebuiAction
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Answer a new webui post actor.

    public ExtendedWebuiPostActor()
    {
        super();
    }

    protected boolean basicProcess(ExtendedEARActor actor)
    {
        return actor.postProcess( getDeploymentFlag() );
    }
}
