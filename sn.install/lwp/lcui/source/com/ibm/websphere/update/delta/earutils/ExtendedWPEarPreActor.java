/*
********************************************************************
* IBM Confidential                                                 *
*                                                                  *
* OCO Source Materials                                             *
*                                                                  *
*                                                                  *
* Copyright IBM Corp. 2003, 2015                                   *
*                                                                  *
* The source code for this program is not published or otherwise   *
* divested of its trade secrets, irrespective of what has been     *
* deposited with the U.S. Copyright Office.                        *
********************************************************************
*/
package com.ibm.websphere.update.delta.earutils;

/*
 *  @ (#) ExtendedEARPreActor.java
 *
 * Class for coordinating the operations to post-process webui files.
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

public class ExtendedWPEarPreActor extends ExtendedWPEarAction {
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Answer a new extended EAR post actor.

    public ExtendedWPEarPreActor() {
        super();
    }

    protected boolean basicProcess(ExtendedEARActor actor) {
        return actor.preProcess();
    }
}
