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
 *  @ (#) ExtendedWARPostActor.java
 *
 * Class for coordinating the operations to post-process EAR files.
 *
 * This concrete class used filter file argument to create the
 * command data.
 *
 *  @author     eedavis
 *  @created    March 2002
 */

public class ExtendedWARPostActor extends ExtendedWARAction
{
   public final static String pgmVersion = "1.1" ;
   public final static String pgmUpdate = "4/6/05" ;

    // Answer a new extended EAR post actor.

    public ExtendedWARPostActor()
    {
        super();
    }

    protected boolean basicProcess(ExtendedWARActor actor)
    {
        return actor.postProcess();
    }
}
