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
package com.ibm.websphere.update.ptf;

/*
 * Installer Exception
 *
 * History 1.1, 12/20/02
 *
 * 12-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
 */

import com.ibm.websphere.update.UpdateException;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 *  
 */
public class InstallerException
    extends UpdateException
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "12/20/02" ;

    // Messaging ...
    //
    // static String getString(String);
    // static String getString(String, Object);
    // static String getString(String, Object, Object);
    // static String getString(String, Object[] msgArgs);

    protected static final String bundleId = "com.ibm.websphere.update.ptf.InstallerNLS";

    protected static boolean retrievedMessages = false;
    protected static ResourceBundle messages = null;

    /**
	 * @return  the messages
	 * @uml.property  name="messages"
	 */
    protected static ResourceBundle getMessages()
    {
        if ( !retrievedMessages ) {
            retrievedMessages = true;
            messages = UpdateException.retrieveBundle(bundleId);
        }

        return messages;
    }

    // Answer the NLS text for the specified message id.

    protected static String getString(String msgCode)
    {
        ResourceBundle useMessages = getMessages();

        if ( useMessages == null )
            return bundleId + ":" + msgCode;

        try {
            return useMessages.getString(msgCode);
        } catch ( MissingResourceException ex ) {
            return bundleId + "::" + msgCode;
        }
    }

    // Answer the NLS text for the specified message id, substituting
    // in the argument to the message text.

    protected static String getString(String msgCode, Object arg)
    {
        String rawMessage = getString(msgCode);
        Object[] msgArgs = new Object[] { arg };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Answer the NLS text for the specified message id, substituting
    // in the arguments to the message text.

    protected static String getString(String msgCode, Object arg1, Object arg2)
    {
        String rawMessage = getString(msgCode);
        Object[] msgArgs = new Object[] { arg1, arg2 };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Answer the NLS text for the specified message id, substituting
    // in the arguments to the message text.

    protected static String getString(String msgCode, Object[] msgArgs)
    {
        String rawMessage = getString(msgCode);
        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Constructors ...

    public InstallerException(String msgKey)
    {
        this(msgKey, null, null);
    }

    public InstallerException(String msgKey, Exception exception)
    {
        this(msgKey, null, exception);
    }

    public InstallerException(String msgKey, Object[] msgArgs, Exception exception)
    {
        this.text = getString(msgKey, msgArgs);

        this.boundException = exception;
    }
}
