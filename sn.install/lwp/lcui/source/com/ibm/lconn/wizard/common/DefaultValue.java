/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common;

import java.io.File;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.ibm.lconn.wizard.common.ui.CommonHelper;


public class DefaultValue {

	private DefaultValue() {

	}
	
	public static String getDefaultJREPath() {
		File root = new File("jvm");
		File jrePath = new File(root, getPlatform() + Constants.FS + "jre");		
		return jrePath.getAbsolutePath();
	}
	
	public static String getDefaultJavaExecutablePath() {
		String jrePath = getDefaultJREPath();
		if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			return new File(jrePath, "bin" + Constants.FS + Constants.JAVA_EXECUTABLE_WIN).
				getAbsolutePath();
		} else {
			return new File(jrePath, "bin" + Constants.FS + Constants.JAVA_EXECUTABLE).
				getAbsolutePath();
		}
	}
	
	public static String getPlatform() {
		String platform = CommonHelper.getPlatformType();
		if(Constants.OS_WINDOWS.equals(platform) || Constants.OS_AIX.equals(platform)) {
			return platform;
		} else if (Constants.OS_LINUX_REDHAT.equals(platform) || Constants.OS_LINUX_SUSE.equals(platform)) {
			return Constants.OS_LINUX;
		} else {
			return Constants.OS_OTHER;
		}
	}
	
}
