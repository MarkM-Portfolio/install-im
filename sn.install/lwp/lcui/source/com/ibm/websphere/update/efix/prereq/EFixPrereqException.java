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
package com.ibm.websphere.update.efix.prereq;

/*
 * EFix prerequisite exception
 *
 * History 1.1, 10/6/02
 *
 * 19-Sep-2002 Initial Version
 */

import java.io.*;
import java.util.*;

import java.text.MessageFormat;

import com.ibm.websphere.update.UpdateException;

public class EFixPrereqException
    extends UpdateException
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "10/6/02" ;

    // Messaging ...
    // 
    // static String getString(String);
    // static String getString(String, Object);
    // static String getString(String, Object, Object);
    // static String getString(String, Object[] msgArgs);

    protected static final String bundleId = "com.ibm.lconn.update.msg.errorMsg";
    protected static final ResourceBundle msgs = UpdateException.retrieveBundle(bundleId);

    // Answer the NLS text for the specified message id.

    protected static String getString(String msgCode)
    {
        if ( msgs != null ) {
            try {
                return msgs.getString(msgCode);
            } catch ( MissingResourceException ex ) {
            }
        }

        return bundleId + ":" + msgCode;
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

    protected static String getString(String msgCode, Object arg1, Object arg2, Object arg3)
    {
        String rawMessage = getString(msgCode);
        Object[] msgArgs = new Object[] { arg1, arg2, arg3 };

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

    public EFixPrereqException(String msgKey)
    {
        this(msgKey, null, null);
    }

    public EFixPrereqException(String msgKey, Exception exception)
    {
        this(msgKey, null, exception);
    }

    public EFixPrereqException(String msgKey, Object[] msgArgs, Exception exception)
    {
        this.text = getString(msgKey, msgArgs);

        this.boundException = exception;
    }
}
