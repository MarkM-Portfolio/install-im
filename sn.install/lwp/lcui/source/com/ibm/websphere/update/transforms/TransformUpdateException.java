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
package com.ibm.websphere.update.transforms;

import java.util.*;

/* 
 * ClassName: TransformUpdateException
 * Abstract: Encapsulates any runtime transformation exceptions that are thrown
 * during update processing.
 * 
 * History 1.1, 11/19/02
 *
 * 30-Aug-2002 Initial Version
 */

public class TransformUpdateException extends java.lang.Exception {


    public TransformUpdateException() {
    }

    public TransformUpdateException(String msg) {
        super(msg);
    }
    
    public TransformUpdateException(Exception e) {
        super(e.toString());   
    }   
    
   
    
     
}


