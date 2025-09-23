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

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.config;//$NON-NLS-1$

import com.ibm.lconn.common.msg.CommonMessages;
import com.ibm.lconn.common.msg.MessageUtil;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class CommonConstants {

	public static final String USER_HOME = System.getProperty("user.home");//$NON-NLS-1$
	public static final String FS = System.getProperty("file.separator");//$NON-NLS-1$
	public static final String CRLF = System.getProperty("line.separator");//$NON-NLS-1$

	public static final String LOG_LEVEL = MessageUtil.getSetting("log.level");//$NON-NLS-1$
	public static final String LOG_ROOT = MessageUtil.getSetting("log.root");//$NON-NLS-1$
	public static final String LOG_MESSAGE = MessageUtil.getSetting("log.msg");//$NON-NLS-1$

	public static String BUTTON_TEXT_FINISH = CommonMessages
			.getString("button.finish.text");//$NON-NLS-1$
	public static String BUTTON_TEXT_NEXT = CommonMessages
			.getString("button.next.text");//$NON-NLS-1$
	public static String BUTTON_TEXT_BACK = CommonMessages
			.getString("button.prev.text");//$NON-NLS-1$
	public static String BUTTON_TEXT_CANCEL = CommonMessages
			.getString("button.cancel.text");//$NON-NLS-1$
	public static final String BUTTON_TEXT_VIEWLOG = CommonMessages
			.getString("button.viewlog.text");//$NON-NLS-1$
	public static String BUTTON_TEXT_OK = CommonMessages.getString("button.OK.text");//$NON-NLS-1$

	public static String BUTTON_TEXT_YES = CommonMessages
			.getString("button.YES.text");//$NON-NLS-1$
	public static String BUTTON_TEXT_NO = CommonMessages.getString("button.NO.text");//$NON-NLS-1$
	public static String BUTTON_TEXT_CONFIG = CommonMessages
			.getString("button.config.text");//$NON-NLS-1$
	
	// operating system type
	public static final String OS_WINDOWS = "win";//$NON-NLS-1$
	public static final String OS_AIX = "aix";//$NON-NLS-1$
	public static final String OS_LINUX_SUSE = "sles";//$NON-NLS-1$
	public static final String OS_LINUX_REDHAT = "redhat";//$NON-NLS-1$
	public static final String OS_LINUX = "linux";//$NON-NLS-1$
	public static final String OS_OTHER = "other";//$NON-NLS-1$
	public static final String PREFIX_GLOBAL_SCRIPT = "global.script";//$NON-NLS-1$
	
	public static final String FILE_COPY_DIR = "cpdir";
	public static final String FILE_REMOVE_DIR = "rmdir";
	public static final String FILE_MAKE_DIR = "mkdir";
	public static final String FILE_REMOVE_FILE = "rmfile";
	public static final String FILE_COPY_FILE = "cpfile";
}
