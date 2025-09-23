/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product.xml;

/*
 * Base Handler Exception
 *
 * History 1.2, 9/4/02
 *
 * 25-Jun-2002 Initial Version
 */

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 *  
 */
public class BaseHandlerException extends Exception
{  
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/4/02" ;

    // Message access ...

    protected static final String bundleId = "com.ibm.websphere.product.xml.BaseHandlerNLS";
    protected static final ResourceBundle msgs;

    static {
        ResourceBundle retrievedBundle;

        try {
            retrievedBundle = ResourceBundle.getBundle(bundleId);
            // MissingResourceException
        } catch ( MissingResourceException e ) {
            retrievedBundle = null;
        }

        msgs = retrievedBundle;
    }

    protected static String getString(String msgCode)
    {
        if ( msgs == null )
            return "!" + bundleId + ":" + msgCode;

        try {
            return msgs.getString(msgCode);

        } catch ( MissingResourceException ex ) {
            return bundleId + ":!" + msgCode;
        }
    }

    protected static String getString(String msgCode, Object[] msgArgs)
    {
        String rawMessage = getString(msgCode);

        return MessageFormat.format(rawMessage, msgArgs);
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

    // Constructors ...

    public BaseHandlerException(String msgKey)
    {
        this(msgKey, null, null);
    }

    public BaseHandlerException(String msgKey, Exception exception)
    {
        this(msgKey, null, exception);
    }

    public BaseHandlerException(String msgKey, Object[] msgArgs, Exception exception)
    {
        text = getString(msgKey);

        if ( msgArgs != null )
            text = MessageFormat.format(text, msgArgs);

        this.boundException = exception;
    }
}
