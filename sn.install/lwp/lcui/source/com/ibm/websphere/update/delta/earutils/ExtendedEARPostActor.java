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
 * Class for coordinating the operations to post-process EAR files.
 *
 * This concrete class used filter file argument to create the
 * command data.
 *
 *  @author     venkataraman
 *  @created    November 20, 2002
 */

public class ExtendedEARPostActor extends ExtendedEARAction
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Answer a new extended EAR post actor.

    public ExtendedEARPostActor()
    {
        super();
    }

    protected boolean basicProcess(ExtendedEARActor actor)
    {
        return actor.postProcess(getDeploymentFlag());
    }
}
