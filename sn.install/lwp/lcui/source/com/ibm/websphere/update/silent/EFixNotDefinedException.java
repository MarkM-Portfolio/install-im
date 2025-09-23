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

package com.ibm.websphere.update.silent;

import java.util.*;

/**
 * Exception to be thrown upon attempted access of a ProductTitle instance that
 * was not defined in the configuration file.
 */
public class EFixNotDefinedException extends java.lang.Exception {

	Object efix;

    /**
     * Creates a new instance of <code>ProductTitleNotDefinedException</code> without detail message.
     */
    public EFixNotDefinedException() {
    }


    /**
     * Constructs an instance of <code>ProductTitleNotDefinedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public EFixNotDefinedException(String msg) {
        super(msg);
    }
    
    public EFixNotDefinedException(String msg, Object efix) {
        super(msg);
    	this.efix = efix;    
    }   
    
    public Object getEfixErrors(){
    	return efix;	
    }
    
     
}


