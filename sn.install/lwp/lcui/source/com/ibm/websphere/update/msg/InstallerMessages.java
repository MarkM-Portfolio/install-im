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
package com.ibm.websphere.update.msg;

/*
 * Installation UI Messages
 * 
 * History 1.1, 9/6/02
 * 
 * 30-Aug-2002 Initial Version
 */

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ibm.websphere.update.UpdateException;

public class InstallerMessages {
	// Program versioning ...

	public static final String pgmVersion = "1.1";
	// Program versioning ...

	public static final String pgmUpdate = "9/6/02";

	// Messaging ...
	//
	// static String getString(String);
	// static String getString(String, Object);
	// static String getString(String, Object, Object);
	// static String getString(String, Object[] msgArgs);

	protected static final String bundleId = "com.ibm.lconn.update.msg.messages";
	protected static final String LCbundleId = "com.ibm.lconn.update.msg.messages";
	protected static ResourceBundle msgs = null;
	protected static ResourceBundle LCmsgs = null;
	protected static boolean LCdidInitializeBundle = false;

	protected static boolean didInitializeBundle = false;
	protected static boolean s_isOnDemand = false;

	public static void setIsOnDemand(boolean isOnDemand) {
		s_isOnDemand = isOnDemand;
	}

	public static boolean isOnDemand() {
		return s_isOnDemand;
	}

	protected static ResourceBundle getBundle(String msgCode) {
		// (!didInitializeBundle && s_isOnDemand){
		if (!didInitializeBundle) {
			didInitializeBundle = true;
			msgs = UpdateException.retrieveBundle(bundleId);
		}

		return msgs;
	}

	protected static ResourceBundle getLCBundle(String msgCode) {
		if (!LCdidInitializeBundle) {
			LCdidInitializeBundle = true;
			LCmsgs = UpdateException.retrieveBundle(LCbundleId);
		}

		return LCmsgs;
	}

	// Answer the NLS text for the specified message id.

	public static String getStringLC(String msgCode) {
		return getLCBundle(msgCode).getString(msgCode);
	}

	public static String getString(String msgCode) {
		try {
			return getStringLC(msgCode);
		} catch (Exception e) {
			return getStringOld(msgCode);
		}
	}

	public static String getStringOld(String msgCode) {
		if (getBundle(msgCode) != null) {
			try {
				return getBundle(msgCode).getString(msgCode);
			} catch (MissingResourceException ex) {
			}
		}

		return bundleId + ":" + msgCode;
	}

	// Answer the NLS text for the specified message id, substituting
	// in the argument to the message text.

	public static String getString(String msgCode, Object arg) {
		String rawMessage = getString(msgCode);
		Object[] msgArgs = new Object[] { arg };

		return MessageFormat.format(rawMessage, msgArgs);
	}

	// Answer the NLS text for the specified message id, substituting
	// in the arguments to the message text.

	public static String getString(String msgCode, Object arg1, Object arg2) {
		String rawMessage = getString(msgCode);
		Object[] msgArgs = new Object[] { arg1, arg2 };

		return MessageFormat.format(rawMessage, msgArgs);
	}

	// Answer the NLS text for the specified message id, substituting
	// in the arguments to the message text.

	public static String getString(String msgCode, Object[] msgArgs) {
		String rawMessage = getString(msgCode);
		return MessageFormat.format(rawMessage, msgArgs);
	}

	public static final String DEBUG_MODE_PROPERTY = "com.ibm.websphere.update.debug";
	public static final String DEBUG_MODE_ENABLE_VALUE = "true";

	public static boolean debugEnabled;

	static {
		String propValue = System.getProperty(DEBUG_MODE_PROPERTY);

		debugEnabled = ((propValue != null) && propValue.equals(DEBUG_MODE_ENABLE_VALUE));
	}

	public static void debug(String text) {
		if (debugEnabled)
			System.out.println(text);
	}

	public static void debug(String text1, String text2) {
		if (debugEnabled) {
			System.out.print(text1);
			System.out.println(text2);
		}
	}
}
