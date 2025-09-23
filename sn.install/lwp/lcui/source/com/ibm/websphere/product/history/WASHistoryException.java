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

package com.ibm.websphere.product.history;

/*
 * WASHistory Exception
 *
 * History 1.2, 10/1/02
 *
 * 30-Jun-2002 Initial Version
 *
 * 01-Oct-2002 Added javadoc
 */

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 *  
 */
public class WASHistoryException extends Exception
{  
    // Program versioning ...

    /** Constants holding CMVC version information. */

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    /** Constants holding CMVC version information. */

    public static final String pgmUpdate = "10/1/02" ;

    // Wrappered exception ...

    protected Exception boundException;

    /**
	 * <p>Answer the exception bound into the receiver. This exception may be null.</p>
	 * @return  Excetpion The exception bound into the receiver.
	 * @uml.property  name="boundException"
	 */

    public Exception getBoundException()
    {
        return boundException;
    }


    // Exception text ...

    protected String text;

    /**
	 * <p>Answer the text bound into the receiver.  The text will never be null, but it may be an empty string.</p>
	 * @return  String The text bound into the receiver.
	 * @uml.property  name="text"
	 */

    public String getText()
    {
        return ( (text == null) ? "" : text );
    }


    // Exception API ...

    // Exception API: Provide conversion to string ...

    /**
     * <p>Answer a string representation of the receiver.  This
     * string combines the text of the receiver with the text
     * of the bound exception (if present).</p>
     *
     * @return String A string representation of the receiver.
     */

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

    /**
     * <p>Print the stack trace of the receiver to the argument
     * print stream.  Append the stack trace of the bound
     * exception, if present.</p>
     *
     * @param output A print stream onto which to print the
     *               stack trace of the receiver.
     */

    public void printStackTrace(PrintStream output)
    {
        super.printStackTrace(output);

        if ( boundException != null )
            boundException.printStackTrace(output);
    }


    // Constructors ...

    /**
     * <p>Parameterizied constructor: make a new WASHistoryException
     * instance, binding in the NLS text associated with the argument
     * message key.</p>
     *
     * @param msgKey The key to an NLS enabled message.
     */

    public WASHistoryException(String msgKey)
    {
        this(msgKey, null);
    }

    /**
     * <p>Parameterizied constructor: make a new WASHistoryException
     * instance, binding in the NLS text associated with the argument
     * message key, and binding in the argument exception.  The
     * exception may be null.</p>
     *
     * @param msgKey The key to an NLS enabled message.
     * @param exception An exception to bind into the receiver.
     */

    public WASHistoryException(String msgKey, Exception exception)
    {
        this(msgKey, null, exception);
    }

    /**
     * <p>Parameterizied constructor: make a new WASProductException
     * instance, binding in the NLS text associated with the argument
     * message key, and binding in the argument exception.  The message
     * arguments are substituted into the NLS text.  The exception may
     * be null.</p>
     *
     * @param msgKey The key to an NLS enabled message.
     * @param msgArgs An array of arguments to be substituted into the
     *                NLS message.
     * @param exception An exception to bind into the receiver.
     */

    public WASHistoryException(String msgKey, Object[] msgArgs, Exception exception)
    {
        this(WASHistory.msgs, msgKey, msgArgs, exception);
    }

    /**
     * <p>Parameterizied constructor: make a new WASProductException
     * instance, binding in the NLS text associated with the argument
     * message key, and binding in the argument exception.  The NLS
     * message is retrieved from the argument resource bundle.  The
     * message arguments are substituted into the NLS text.  The
     * exception may be null.</p>
     *
     * @param msgs The resource bundle which is the source for the
     *             NLS message.
     * @param msgKey The key to an NLS enabled message.
     * @param msgArgs An array of arguments to be substituted into the
     *                NLS message.
     * @param exception An exception to bind into the receiver.
     */

    public WASHistoryException(ResourceBundle msgs,
                               String msgKey, Object[] msgArgs,
                               Exception exception)
    {
        text = msgs.getString(msgKey);

        if ( msgArgs != null )
            text = MessageFormat.format(text, msgArgs);

        this.boundException = exception;
    }
}
