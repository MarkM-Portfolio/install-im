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

import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import java.io.*;
import java.util.*;

/**
 * Class: StandardNotifier.java Abstract: Notification implementation to provide meaningful install process messages. Component Name: WAS.ptf Release: ASV50X History 1.1, 1/9/03 01-Nov-2002 Initial Version
 */
public class StandardNotifier implements Notifier {

	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "1.1" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "1/9/03" ;

	//********************************************************
	//  Instance State
	//********************************************************
	private final int action;

	private int taskCount;
	private int taskNumber;
	private Vector bannerStack;
	
	public static final boolean BEGIN_TASK = false ;
	public static final boolean END_TASK = true ;

	public StandardNotifier(int action) {

		this.taskCount = 0;
		this.taskNumber = 0;
		this.action = action;
		this.bannerStack = new Vector();
	}

	//********************************************************
	//  Method Definitions
	//********************************************************
	/**
	 * @param taskCount  the taskCount to set
	 * @uml.property  name="taskCount"
	 */
	public void setTaskCount(int taskCount) {
		this.taskNumber = 0;
		this.taskCount = taskCount;
	}

	/**
	 * @return  the taskCount
	 * @uml.property  name="taskCount"
	 */
	public int getTaskCount() {
		return taskCount;
	}

	/**
	 * @return  the taskNumber
	 * @uml.property  name="taskNumber"
	 */
	public int getTaskNumber() {
		return taskNumber;
	}

	private int incrementTaskNumber() {
		return (++taskNumber);
	}

	/**
	 * @return  the bannerStack
	 * @uml.property  name="bannerStack"
	 */
	private Vector getBannerStack() {
		return bannerStack;
	}

	public void pushBanner(String banner) {
		getBannerStack().addElement(banner);
	}

	public String popBanner() {
		Vector useBannerStack = getBannerStack();
		int bannerLength = useBannerStack.size();

		if (bannerLength == 0) {
			return null;

		} else {
			String result = (String) useBannerStack.elementAt(bannerLength - 1);
			useBannerStack.removeElementAt(bannerLength - 1);
			return result;
		}
	}

	public String collateBanners() {
		Vector useBannerStack = getBannerStack();
		int numBanners = useBannerStack.size();

		StringBuffer collation = new StringBuffer();

		for (int bannerNo = 0; bannerNo < numBanners; bannerNo++) {
			String nextBanner = (String) useBannerStack.elementAt(bannerNo);
			collation.append(nextBanner);
		}

		return collation.toString();
	}

	public String replaceBanner(String banner) {
		String oldBanner = popBanner();
		pushBanner(banner);
		return oldBanner;
	}

	public String beginTask() {
		incrementTaskNumber();

		return performTaskNotification(BEGIN_TASK);
	}

	public String endTask() {
		return performTaskNotification(END_TASK);
	}

	public boolean wasCancelled() {
		return false;
	}

	private void println(String text) {
		if (action == BaseInstaller.INSTALL)
			UpdateReporter.printInstallerMessage(text);
		 else 
			UpdateReporter.printUninstallerMessage(text);
			
	}

	private String performTaskNotification(boolean taskStatus) {
		StringBuffer outputBuffer = new StringBuffer();

		String msgId = ((taskStatus == BEGIN_TASK) ? "begin.task.out.of" : "end.task.out.of");
		String[] msgArgs = new String[] { Integer.toString(getTaskNumber()), Integer.toString(getTaskCount())};
		String taskMsg = UpdateReporter.getSilentString(msgId, msgArgs);

		outputBuffer.append(taskMsg);
		outputBuffer.append(collateBanners());

		String notificationText = outputBuffer.toString();

		println(notificationText);

		return notificationText;
	}

}
