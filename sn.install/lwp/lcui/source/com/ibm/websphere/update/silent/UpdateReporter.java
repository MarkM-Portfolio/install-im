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

import java.io.*;
import java.util.*;
import java.text.MessageFormat;

import org.xml.sax.SAXParseException;

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;

/**********************************************************************************************************
 * Class: UpdateReporter.java
 * Abstract: IO/Print/Exception Handler class for Update Silent Installer
 * 
 * 
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/silent/UpdateReporter.java, wps.base.fix, wps6.fix
 *
 * History 1.6, 9/7/06
 * 
 * 
 * 
 * 01-Nov-2002 Initial Version
 **********************************************************************************************************/

public class UpdateReporter {

	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "@BUILD_VERSION@" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "@BUILD_DATE@" ;

	//***********************************************************
	// Reporting Utilities
	//***********************************************************.
	
    // Copyright display ...
    public static void printCopyright()
    {
        System.out.println(
            getDefaultedString(
                "websphere.copyright.statement",
                "websphere.copyright.statement: Copyright (c) IBM Corporation 2002,2010; All rights reserved.") );
        System.out.println(
            getDefaultedString(
                "websphere.version.release",
                "HCL Connections") );
        System.out.println(
            getDefaultedString(
                "websphere.update.install.cmdline.info",
                "websphere.update.install.cmdline.info: Update Installer Version {0}, Dated {1}",
                new Object[] { pgmVersion, getUpdateStamp() }) );
        System.out.println();
    }	
	
    public static final String getUpdateStamp()
    {
        return com.ibm.websphere.update.ioservices.CalendarUtil.formatCMVCDate(pgmUpdate);
    }

    // Section for retriving base installer messages ...

    public static final String baseBundleId =
        "com.ibm.lconn.update.msg.messages";

    public static final ResourceBundle baseMsgs;

    static {
        ResourceBundle retrievedBundle;

        try {
            retrievedBundle = ResourceBundle.getBundle(baseBundleId);
            // throws MissingResourceException

        } catch (MissingResourceException e) {
            retrievedBundle = null;
        }

        baseMsgs = retrievedBundle;
    }

    public static ResourceBundle getBaseBundle() {
        return baseMsgs;
    }

    public static String getBaseString(String msgCode) {

        if (baseMsgs == null)
            return baseBundleId + ":" + msgCode;

        try {
            return baseMsgs.getString(msgCode);

        } catch (MissingResourceException ex) {
            return baseBundleId + ":" + msgCode;
        }
    }

    public static String getBaseString(String msgCode, Object arg) {

        String rawMessage = getBaseString(msgCode);
        Object[] msgArgs = new Object[] { arg };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    public static String getBaseString(String msgCode, Object arg1, Object arg2) {

        String rawMessage = getBaseString(msgCode);
        Object[] msgArgs = new Object[] { arg1, arg2 };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    public static String getBaseString(String msgCode, Object[] msgArgs) {

        String rawMessage = getBaseString(msgCode);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Section for silent installer specific messages.

    public static final String silentBundleId =
        "com.ibm.websphere.update.silent.SilentInstaller";

    public static final ResourceBundle silentMsgs;

    static {
        ResourceBundle retrievedBundle;

        try {
            retrievedBundle = ResourceBundle.getBundle(silentBundleId);
            // throws MissingResourceException

        } catch (MissingResourceException e) {
            retrievedBundle = null;
        }

        silentMsgs = retrievedBundle;
    }

    public static ResourceBundle getSilentBundle() {
        return silentMsgs;
    }

    public static String getDefaultedString(String msgCode, String defaultMsg)
    {
        if (silentMsgs == null)
            return defaultMsg;

        try {
            return silentMsgs.getString(msgCode);
        } catch (MissingResourceException ex) {
            return defaultMsg;
        }
    }

    public static String getDefaultedString(String msgCode, String defaultMsg, Object[] msgArgs)
    {
        String rawMessage = getDefaultedString(msgCode, defaultMsg);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    public static String getSilentString(String msgCode)
    {
        if (silentMsgs == null)
            return silentBundleId + ":" + msgCode;

        try {
            return silentMsgs.getString(msgCode);

        } catch (MissingResourceException ex) {
            return silentBundleId + ":" + msgCode;
        }
    }

    public static String getSilentString(String msgCode, Object arg)
    {
        String rawMessage = getSilentString(msgCode);
        Object[] msgArgs = new Object[] { arg };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    public static String getSilentString(String msgCode, Object arg1, Object arg2)
    {
        String rawMessage = getSilentString(msgCode);
        Object[] msgArgs = new Object[] { arg1, arg2 };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    public static String getSilentString(String msgCode, Object[] msgArgs) {

        String rawMessage = getSilentString(msgCode);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Messaging utility ...

    public static void printInstallerMessage(String msg, PrintStream message) {
        message.println(msg);
    }

    public static void printUninstallerMessage(String msg, PrintStream message) {
        message.println(msg);
    }

    public static void printInstallerErrorMessage(String errMsg, PrintStream message) {
        message.println(errMsg);
    }

    public static void printUninstallerErrorMessage(String errMsg, PrintStream message) {
        message.println(errMsg);
    }

    public static void printInstallerMessage(String msg) {
        printInstallerMessage(msg, System.out);
    }

    public static void printUninstallerMessage(String msg) {
        printUninstallerMessage(msg, System.out);
    }

    public static void printInstallerErrorMessage(String errMsg) {
        printInstallerErrorMessage(errMsg, System.err);
    }
    
    public static void printUninstallerErrorMessage(String errMsg) {
        printUninstallerErrorMessage(errMsg, System.err);
    }

	//***********************************************************
	// Logging Utilities
	//***********************************************************.
    public static void log(String msg) {
        Logger.setDebugOn(true);
        Logger.log(msg);
    }

	//***********************************************************
	// Exception / Error Handling
	//***********************************************************.
    public static void handleRecoverableErrors( Iterator recoverableErrors,
                                                 PrintStream output,
                                                 boolean printStack )
    {
        while (recoverableErrors.hasNext()) {
            SAXParseException nextError =
                (SAXParseException) recoverableErrors.next();

            String errorText = getSilentString
                ("WUPD0017E", new Object[] { nextError.getSystemId(),
                                             nextError.getPublicId(),
                                             new Integer(nextError.getLineNumber()),
                                             new Integer(nextError.getColumnNumber()),
                                             nextError.toString()});
            output.println(errorText);

            if (printStack)
                nextError.printStackTrace(output);
        }
    }

    public static void handleWarnings(Iterator warnings,
                                       PrintStream output, boolean printStack )
    {
        while (warnings.hasNext()) {
            SAXParseException nextWarning = (SAXParseException) warnings.next();

            String warningText = getSilentString
                ("WUPD0018E", new Object[] { nextWarning.getSystemId(),
                                             nextWarning.getPublicId(),
                                             new Integer(nextWarning.getLineNumber()),
                                             new Integer(nextWarning.getColumnNumber()),
                                             nextWarning.toString()});
            output.println(warningText);

            if (printStack)
                nextWarning.printStackTrace(output);
        }
    }

    public static int handleFatalExceptions(int errorNo,
                                             Iterator fatalExceptions,
                                             PrintStream output, boolean printStack )
    {
        while (fatalExceptions.hasNext()) {
            Exception nextFatalException = (Exception) fatalExceptions.next();
            errorNo++;

            String errorMessage = getSilentString("listing.nested.error",
                                                  Integer.toString(errorNo),
                                                  nextFatalException.toString());

            output.println(errorMessage);

            if (printStack)
                nextFatalException.printStackTrace(output);
        }

        return errorNo;
    }

    public static boolean handleErrors(WPProduct wasProduct, WPHistory wasHistory,
                                        PrintStream output, boolean printStack )
    {
        int numProductErrors = wasProduct.numRecoverableErrors(),
            numHistoryErrors = wasHistory.numRecoverableErrors();

        if ((numProductErrors > 0) || (numHistoryErrors > 0)) {
            output.println( getSilentString("WUPD0019E") );

            handleRecoverableErrors(
                wasProduct.getRecoverableErrors(),
                output,
                printStack);
            handleRecoverableErrors(
                wasHistory.getRecoverableErrors(),
                output,
                printStack);
        }

        int numProductWarnings = wasProduct.numWarnings(),
            numHistoryWarnings = wasHistory.numWarnings();

        if ((numProductWarnings > 0) || (numHistoryWarnings > 0)) {
            output.println( getSilentString("WUPD0020E") );

            handleWarnings(wasProduct.getWarnings(), output, printStack);
            handleWarnings(wasHistory.getWarnings(), output, printStack);
        }

        int numProductFatalExceptions = wasProduct.numExceptions(),
            numHistoryFatalExceptions = wasHistory.numExceptions();

        int numFatalExceptions =
            numProductFatalExceptions + numHistoryFatalExceptions;

        if (numFatalExceptions == 0)
            return true;

        output.println( getBaseString("label.product.directory.error"));
        output.println( getSilentString((numFatalExceptions == 1) ? "WUPD0015E" : "WUPD0016E") );

        int errorNo = 0;

        errorNo =
            handleFatalExceptions(
                errorNo,
                wasProduct.getExceptions(),
                output,
                printStack);
        errorNo =
            handleFatalExceptions(
                errorNo,
                wasHistory.getExceptions(),
                output,
                printStack);

        return false;
    }

}
