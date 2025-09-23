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

package com.ibm.websphere.update.harness;

import java.util.*;
import java.io.*;


/**********************************************************************************************************
 * Class: UpdateHarnessException.java
 * Abstract: Exception class for harness operations
 * 
 *
 * Component Name: WAS.ptf
 * Release: ASV50X
 * 
 * History 1.3, 3/13/03
 *
 * 
 * 
 * 
 * 01-Feb-2003 Initial Version
 **********************************************************************************************************/


public class UpdateHarnessException extends Exception {


    /**
     * Creates a new instance of <code>UpdateHarnessException</code> without detail message.
     */
    public UpdateHarnessException() {
    }


    /**
     * Constructs an instance of <code>UpdateHarnessException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UpdateHarnessException(String msg) {
        super(msg);
    }
}

