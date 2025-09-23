/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */


package com.ibm.prs.common.exception;

/**
 * PRSApiException class provides methods to throw exceptions for the XML query API.
 * 
 * @author jichen
 * @version 1.0
 *
 */

public class PRSApiException extends Exception {

    
    private static final long serialVersionUID = 1L;

    /**
     * Overrides {@link java.lang.Exception#Exception() Exception()}
     */
    public PRSApiException ()
    {
        super();
    }
    
    /**
     * Overrides {@link java.lang.Exception#Exception(String) Exception(String)}
     */
    public PRSApiException(String msg)
    {
        super(msg);
    }
    
    /**
     * Overrides {@link java.lang.Exception#Exception(Throwable) Exception(Throwable)}
     */
    public PRSApiException(Throwable cause)
    {
        super(cause);
    }
    
    /**
     * Overrides {@link java.lang.Exception#Exception(String, Throwable) Exception(String, Throwable)}
     */
    public PRSApiException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
