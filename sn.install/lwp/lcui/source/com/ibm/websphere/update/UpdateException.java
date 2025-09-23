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
package com.ibm.websphere.update;


import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;

/**
 *  
 */
public class UpdateException
    extends Exception
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/30/02" ;

    // Messaging ...

    public static ResourceBundle retrieveBundle(final String bundleId)
    {
        ResourceBundle retrievedBundle;

        try {
            retrievedBundle = (ResourceBundle) AccessController.doPrivileged
                (new PrivilegedExceptionAction() {
                     public Object run() throws MissingResourceException {
                         return ResourceBundle.getBundle(bundleId);
                         // throws MissingResourceException
                     }
                 }); // throws PrivilegedActionException

        } catch ( PrivilegedActionException e ) {
            retrievedBundle = null;

        } catch ( MissingResourceException e ) {
            retrievedBundle = null;
        }

        return retrievedBundle;
    }

    // Wrappered exception ...

    protected Exception boundException;

    /**
	 * @return  the boundException
	 * @uml.property  name="boundException"
	 */
    public Exception getBoundException()
    {
        return boundException;
    }

    // Exception text ...

    protected String text;

    /**
	 * @return  the text
	 * @uml.property  name="text"
	 */
    public String getText()
    {
        return ( (text == null) ? "" : text );
    }

    // Exception API ...

    // Exception API: Provide conversion to string ...

    public String toString()
    {
        String fullText = getText();

        if ( boundException != null ) {
            String nestedText;

            try {
                nestedText = boundException.toString();
            } catch ( Throwable th ) {
                nestedText = boundException.getClass().getName();
            }

            fullText += ": " + nestedText;
        }

        return fullText;
    }

    // Exception API: Provide stack dumping ...

    public void printStackTrace(PrintStream output)
    {
        super.printStackTrace(output);

        if ( boundException != null )
            boundException.printStackTrace(output);
    }
}
