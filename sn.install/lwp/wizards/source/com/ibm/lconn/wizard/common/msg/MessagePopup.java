/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.console.ConsoleDialog;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.ResourcePool;

/**
 * 
 * @author Jun Jin Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class MessagePopup {

	/**
	 * Constant for a dialog with no image (value 0).
	 */
	public final static int NONE = 0;

	/**
	 * Constant for a dialog with an error image (value 1).
	 */
	public final static int ERROR = 2;

	/**
	 * Constant for a dialog with an info image (value 2).
	 */
	public final static int INFORMATION = 1 << 2;

	/**
	 * Constant for a dialog with a question image (value 3).
	 */
	public final static int QUESTION = 1 << 3;

	/**
	 * Constant for a dialog with a warning image (value 4).
	 */
	public final static int WARNING = 1 << 4;
	public final static int CONFIRM = 1 << 9;
	public final static int OK = 1 << 5;
	public final static int CANCEL = 1 << 6;
	public final static int YES = 1 << 7;
	public final static int NO = 1 << 8;

	public final static int NO_LOG = 1 << 10;
	public final static int MESSAGE_LOG = 1 << 11;
	
	public static String commonTitle;

	
	private final static int[] needRemove = { NO_LOG, MESSAGE_LOG };

	private static final String MSG_DIALOG_QUESTION = MessageUtil.getMsg("popup.title.QUESTION");

	private static final String MSG_DIALOG_CONFIRM = MessageUtil.getMsg("popup.title.CONFIRM");

	private static final String MSG_DIALOG_INFO = MessageUtil.getMsg("popup.title.INFORMATION");

	private static final String MSG_DIALOG_WARNING = MessageUtil.getMsg("popup.title.WARNING");
	
	private static final String MSG_DIALOG_ERROR = MessageUtil.getMsg("popup.title.ERROR");

	private static String[] COMMON_MSGS = {MSG_DIALOG_QUESTION, MSG_DIALOG_CONFIRM, MSG_DIALOG_INFO, MSG_DIALOG_WARNING, MSG_DIALOG_ERROR};

	private static int showMessage(Object source, String title, String message,
			int messageType) {
		return showMessage(source, title, message, messageType, -1);
	}

	private static int showMessage(Object source, String title, String message,
			int messageType, int defaultButton) {
		if(commonTitle!=null && !commonTitle.equals("")){
			if(Util.indexOf(COMMON_MSGS , title)!=-1)
				title = commonTitle;
		}
		int logMessageType = messageType;
		log(source, title, message, logMessageType);

		messageType = CommonHelper.removeStyle(messageType, needRemove);
		String[] dialogButtonLabels = getButtonLabels(messageType);
		if (dialogButtonLabels == null)
			dialogButtonLabels = getButtonLabels(OK);
		int defaultButtonIndex = getDefaultButtonIndex(dialogButtonLabels,
				messageType, defaultButton);
		int dialogImageType = getDialogImageType(messageType);

		int open = open(title, message, dialogButtonLabels, defaultButtonIndex,
				dialogImageType);

		String result = getUserInput(messageType, open);
		if (result != null) {
			log(source, title, result, INFORMATION);
		}
		return open;
	}

	private static int open(String title, String message,
			String[] dialogButtonLabels, int defaultButtonIndex,
			int dialogImageType) {
		int open;
		if (CommonHelper.isConsoleMode()) {
			ConsoleDialog dialog = new ConsoleDialog(title, message, dialogImageType, dialogButtonLabels, defaultButtonIndex);
			open = dialog.open();
		} else {
			Shell activeShell = ResourcePool.getActiveShell();
			Image wizardTitleIcon = ResourcePool.getWizardTitleIcon();
			
			MessageDialog dialog = new MessageDialog(activeShell, title,
					wizardTitleIcon, message, dialogImageType,
					dialogButtonLabels, defaultButtonIndex);
			open = dialog.open();
		}
		return open;
	}

	private static int getDialogImageType(int messageType) {
		if (CommonHelper.isStyle(messageType, WARNING)) {
			return MessageDialog.WARNING;
		}
		if (CommonHelper.isStyle(messageType, ERROR)) {
			return MessageDialog.ERROR;
		}
		if (CommonHelper.isStyle(messageType, CONFIRM)) {
			return MessageDialog.QUESTION;
		}
		if (CommonHelper.isStyle(messageType, QUESTION)) {
			return MessageDialog.QUESTION;
		}
		if (CommonHelper.isStyle(messageType, INFORMATION)) {
			return MessageDialog.INFORMATION;
		}
		if (CommonHelper.isStyle(messageType, QUESTION)) {
			return MessageDialog.QUESTION;
		}
		return MessageDialog.NONE;
	}

	private static String getUserInput(int messageType, int open) {
		switch (messageType) {
		case QUESTION:
		case CONFIRM:
			if (open == Window.OK)
				return "USER_INPUT: YES";
			else
				return "USER_INPUT: NO";
		}
		return null;
	}

	public static void log(Object source, String title, String message,
			int messageType) {
		Level logLevel = getLogLevel(messageType);
		if (logLevel == null)
			return;
		LogUtil.getLogger(source.getClass()).log(logLevel,
				title + ": " + message);
	}

	private static Level getLogLevel(int messageType) {
		if (CommonHelper.isStyle(messageType, NO_LOG))
			return null;
		if (CommonHelper.isStyle(messageType, MESSAGE_LOG))
			return getLogLevel(INFORMATION);
		switch (messageType) {
		case ERROR:
			return Level.SEVERE;
		case WARNING:
			return Level.WARNING;
		case INFORMATION:
			return Level.INFO;
		case QUESTION:
		case CONFIRM:
			return getLogLevel(INFORMATION);
		}
		return getLogLevel(INFORMATION);
	}

	private static int getDefaultButtonIndex(String[] dialogButtonLabels,
			int messageType, int defaultButton) {
		if (defaultButton > 0 && defaultButton < dialogButtonLabels.length)
			return defaultButton;
		String[] buttonLabels = getButtonLabels(defaultButton);
		if (buttonLabels != null) {
			int indexOf = Util.indexOf(dialogButtonLabels, buttonLabels[0]);
			if (indexOf != -1)
				return indexOf;
		}
		switch (messageType) {
		case ERROR:
		case WARNING:
		case INFORMATION:
			return 0;
		case QUESTION:
			return 1;
		case CONFIRM:
			return 1;
		}
		return dialogButtonLabels.length - 1;
	}

	private static String[] getButtonLabels(int messageType) {
		switch (messageType) {
		case ERROR:
		case WARNING:
		case INFORMATION:
			return getButtonLabels(OK);
		case QUESTION:
			return getButtonLabels(YES | NO);
		case CONFIRM:
			return getButtonLabels(OK | CANCEL);
		}
		List<String> buttonLabelList = new ArrayList<String>();
		if (CommonHelper.isStyle(messageType, YES)) {
			buttonLabelList.add(Constants.BUTTON_TEXT_YES);
		}
		if (CommonHelper.isStyle(messageType, NO)) {
			buttonLabelList.add(Constants.BUTTON_TEXT_NO);
		}
		if (CommonHelper.isStyle(messageType, OK)) {
			buttonLabelList.add(Constants.BUTTON_TEXT_OK);
		}
		if (CommonHelper.isStyle(messageType, CANCEL)) {
			buttonLabelList.add(Constants.BUTTON_TEXT_CANCEL);
		}
		String[] result = {};
		result = Util.toArray(buttonLabelList, result);
		return result;
	}

	public static void showErrorMessage(Object source, String title,
			String message) {
		showMessage(source, title, message, ERROR);
	}

	public static void showInfoMessage(Object source, String title,
			String message) {
		showMessage(source, title, message, INFORMATION);
	}

	public static void showWarningMessage(Object source, String title,
			String message) {
		showMessage(source, title, message, WARNING);
	}

	public static int showConfirmMessage(Object source, String title,
			String message) {
		return showMessage(source, title, message, CONFIRM);
	}

	public static int showQuestionMessage(Object source, String title,
			String message) {
		return showMessage(source, title, message, QUESTION);
	}
	
	public static void showErrorMessage(Object source, 
			String message) {
		showMessage(source, MSG_DIALOG_ERROR, message, ERROR);
	}

	public static void showInfoMessage(Object source, 
			String message) {
		showMessage(source, MSG_DIALOG_INFO, message, INFORMATION);
	}

	public static void showWarningMessage(Object source, 
			String message) {
		showMessage(source, MSG_DIALOG_WARNING, message, WARNING);
	}

	public static int showConfirmMessage(Object source, 
			String message) {
		return showMessage(source, MSG_DIALOG_CONFIRM, message, CONFIRM);
	}

	public static int showQuestionMessage(Object source, 
			String message) {
		return showMessage(source, MSG_DIALOG_QUESTION, message, QUESTION);
	}
	

	public static void main(String[] args) {
		CommonHelper.setConsoleMode(true);
//		new Display();
//		new Shell().open();
		Object source = "source";
		String title = "title";
		String message = "message";
		showConfirmMessage(source, title, message);
		showErrorMessage(source, title, message);
		showInfoMessage(source, title, message);
		showQuestionMessage(source, title, message);
		showWarningMessage(source, title, message);
	}
}
