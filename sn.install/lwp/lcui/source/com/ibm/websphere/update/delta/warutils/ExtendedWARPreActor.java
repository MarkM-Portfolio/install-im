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
 *  @ (#) ExtendedWARPreActor.java
 *
 * Class for coordinating the operations to post-process WAR files.
 * This is an abstract class: A subclass must implement 'getActor'
 * to answer the proper actor instance.
 *
 *  @author     eedavis
 *  @created    March, 2005
 */

public class ExtendedWARPreActor extends ExtendedWARAction
{
   public final static String pgmVersion = "1.1" ;
   public final static String pgmUpdate = "4/6/05" ;

    // Answer a new extended WAR pre actor.

    public ExtendedWARPreActor()
    {
        super();
    }

    protected boolean basicProcess(ExtendedWARActor actor)
    {
        return actor.preProcess();
    }
}
