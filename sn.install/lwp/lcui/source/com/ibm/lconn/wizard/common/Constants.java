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


import com.ibm.lconn.common.LCUtil;
import com.ibm.lconn.ifix.Contants;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.websphere.update.ptf.ImageBaseInstaller;

public class Constants {
	
	static {
		String user_install_root = System.getProperty(Contants.USER_INSTALL_ROOT); 	
		String was_cell = System.getProperty(Contants.WAS_CELL);
 	
		String cell_Path = user_install_root + File.separator + "config" + File.separator + "cells" + File.separator +  was_cell;
		File variables = new File(cell_Path + File.separator + Contants.VARIABLES_FILE);
		String news_home = Contants.loadWASVariable("NEWS_HOME", variables);
		System.out.println("NEWS_HOME: " + news_home);
		String LCHome = null;
		if(news_home != null){
			LCHome = new File(news_home).getParent();			
		}else{		
			LCHome = new File(new File(".").getAbsolutePath()).getParent();
			LCHome = new File(LCHome).getParent();
		}
		System.out.println("IBM Connections Home: " + LCHome);
		System.setProperty(LCUtil.LC_HOME, LCHome);
		System.setProperty(ImageBaseInstaller.logLevelPropertyName, "5");
	}
	
	public static final int RE_VALIDATE = -9999;
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String FS = System.getProperty("file.separator");
	public static final String CRLF = System.getProperty("line.separator");
	public static final String UI_LINE_SEPARATOR = "\n";

	public static final String PATH_SEPARATOR = Constants.OS_WINDOWS
			.equals(DefaultValue.getPlatform()) ? ";" : ":";

	
	public static final String WIZARD_HOME = System.getProperty(LCUtil.LC_HOME) + FS + "/version/log/wizard";
	public static final String LOG_ROOT = WIZARD_HOME + FS + "log";
	public static final String LOG_ROOT_FAKE = "C:/Documents and Settings/hongjun/lcWizard" + FS + "log";
	public static final String RESPONSE_ROOT = WIZARD_HOME + FS + "response";
	public static final String OUTPUT_ROOT = WIZARD_HOME + FS + "output";
	
	static {
		File file = new File(Constants.WIZARD_HOME);
		if (!file.exists()) {
			file.mkdirs();
		}

		/*file = new File(Constants.LOG_ROOT);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.RESPONSE_ROOT);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.OUTPUT_ROOT);
		if (!file.exists()) {
			file.mkdirs();
		}*/
	}

	
	public static final String WIZARD_PAGE_WELCOME = "WELCOME_PAGE";
	public static final String WIZARD_PAGE_ACTION_TYPE = "INSTALL_UNINSATLL_OPTION";
	public static final String WIZARD_PAGE_FIX_INFO = "FIX_INFO_PAGE";
	public static final String WIZARD_PAGE_WAS_AUTH_INFO = "WAS_AUTH_INFO_PAGE";
	public static final String WIZARD_PAGE_SUMMARY = "SUMMARY_PAGE";
	public static final String WIZARD_PAGE_EXECUTION = "EXECUTION_PAGE";
	public static final String WIZARD_PAGE_FINISH = "FINISH_PAGE";
	
	public static final String VARIABLE_ALL_FIXES = "ALL_FIXES";
	public static final String VARIABLE_SELECTTED_FIXES = "SELECTTED_FIXES";
	public static final String VARIABLE_SELECTTED_EFIXIMAGES = "SELECTTED_EFIXIMAGES";
	public static final String VARIABLE_SELECTED_FIX_IDS = "SELECTED_FIX_IDS";
	
	public static final String INPUT_FIX_ACTION_NAME = "FIX_ACTION_NAME";
	public static final String INPUT_FIX_LOCATION = "FIX_LOCATION";
	public static final String INPUT_FIX_SELECT_ALL = "FIX_SELECT_ALL"; 
	public static final String INPUT_FIX_SELECTED = "FIX_SELECTED";
	public static final String INPUT_WAS_USERID = "WAS_USERID";
	public static final String INPUT_WAS_PASSWORD = "WAS_PASSWORD";
	
	public static final String OPERATION_TYPE_INSTALL_FIXES = "INSTALL_FIXES"; 
	public static final String OPERATION_TYPE_UNINSTALL_FIXES = "UNINSTALL_FIXES"; 
	
	public static final String LOG_LEVEL = "loglevel"; //$NON-NLS-1$
	
	public static final String FEATURE_ACTIVITIES = "activities"; //$NON-NLS-1$
	public static final String FEATURE_BLOGS = "blogs"; //$NON-NLS-1$
	public static final String FEATURE_COMMUNITIES = "communities"; //$NON-NLS-1$
	public static final String FEATURE_DOGEAR = "dogear"; //$NON-NLS-1$
	public static final String FEATURE_PROFILES = "profiles"; //$NON-NLS-1$
	public static final String FEATURE_HOMEPAGE = "homepage"; //$NON-NLS-1$
	public static final String FEATURE_WIKIS = "wikis"; //$NON-NLS-1$
	public static final String FEATURE_FILES = "files"; //$NON-NLS-1$
	public static final String FEATURE_FORUM = "forum";
	
	public static final String FEATURES_ALL = "activities,blogs,communities,dogear,files,forum,homepage,profiles,wikis";

	// operating system type
	public static final String OS_WINDOWS = "win";//$NON-NLS-1$
	public static final String OS_AIX = "aix";//$NON-NLS-1$
	public static final String OS_LINUX_SUSE = "sles";//$NON-NLS-1$
	public static final String OS_LINUX_REDHAT = "redhat";//$NON-NLS-1$
	public static final String OS_LINUX = "linux";//$NON-NLS-1$
	public static final String OS_OTHER = "other";

	public static final String RETURN_CODE_0 = "0";
	
	public static final String COLOR_WHITE = "COLOR_WHITE";

	public static final String TEXT_EMPTY_STRING = "";
	
	public static final String ID_UI = "ID_UI";
	
	public static final String VARIABLE_EXECUTE_PAGE_ENTERED_FLAG = "ExecutePageEnteredFlag";
	
	public static final String BOOL_TRUE = "yes";
	public static final String BOOL_FALSE = "no";
	
	
	public static String BUTTON_TEXT_FINISH = Messages.getString("button.finish.text");
	public static String BUTTON_TEXT_NEXT = Messages.getString("button.next.text");
	public static String BUTTON_TEXT_BACK = Messages.getString("button.prev.text");
	public static String BUTTON_TEXT_CANCEL = Messages.getString("button.cancel.text");
	public static final String BUTTON_TEXT_VIEWLOG = Messages.getString("button.viewlog.text");
	public static final String BUTTON_TEXT_OPENLOG = Messages.getString("button.openlog.text");
	public static final String BUTTON_TEXT_OPENSQL = Messages.getString("button.opensql.text");
	public static final String BUTTON_TEXT_VIEWJAR = Messages.getString("button.viewjar.text");
	public static String BUTTON_TEXT_OK = Messages.getString("button.OK.text");
	public static String BUTTON_TEXT_YES = Messages.getString("button.YES.text");
	public static String BUTTON_TEXT_NO = Messages.getString("button.NO.text");
	public static String BUTTON_TEXT_CONFIG = Messages.getString("button.config.text");
	
	public static final String ERROR_FILE_NOT_EXIST = "ERROR_FILE_NOT_EXIST";
	public static final String WARN_WIZARD_ICON_IMAGE_LOADING_FAIL = "WARN_WIZARD_ICON_IMAGE_LOADING_FAIL";
	public static final String WARN_DB_WIZARD_SETTING_MISSING = "WARN_DB_WIZARD_SETTING_MISSING";


	// Wizard action type
	public static final String WIZARD_ACTION_PREVIOUS = "Previous"; //$NON-NLS-1$
	public static final String WIZARD_ACTION_NEXT = "Next"; //$NON-NLS-1$
	public static final String WIZARD_ACTION_CANCEL = "Cancel"; //$NON-NLS-1$
	public static final String WIZARD_ACTION_FINISH = "Finish"; //$NON-NLS-1$

	
	public static final String LOG_MESSAGE = "LOG_MESSAGE";//$NON-NLS-1$

	

	// java executable
	public static final String JAVA_EXECUTABLE = "java"; //$NON-NLS-1$
	public static final String JAVA_EXECUTABLE_WIN = "java.exe"; //$NON-NLS-1$
	
	

	public static final int DISABLE_BACK = 1 << 2;
	public static final int DISABLE_NEXT = 1 << 3;
	public static final int DISABLE_CANCEL = 1 << 4;
	public static final int DISABLE_NONE = 0;

	

	

	public static final String VERSION_10X = "1.0.x"; //$NON-NLS-1$
	public static final String VERSION_100 = "1.0.0"; //$NON-NLS-1$
	public static final String VERSION_101 = "1.0.1"; //$NON-NLS-1$
	public static final String VERSION_102 = "1.0.2"; //$NON-NLS-1$
	public static final String VERSION_200 = "2.0.0"; //$NON-NLS-1$
	public static final String VERSION_201 = "2.0.1"; //$NON-NLS-1$
	public static final String VERSION_250 = "2.5.0"; //$NON-NLS-1$
	public static final String VERSION_300 = "3.0.0"; //$NON-NLS-1$
	public static final String VERSION_3000 = "3.0.0.0"; //$NON-NLS-1$
	public static final String VERSION_3010 = "3.0.1.0"; //$NON-NLS-1$
	public static final String VERSION_3011 = "3.0.1.1"; //$NON-NLS-1$
	public static final String VERSION_4000 = "4.0.0.0"; //$NON-NLS-1$
	public static final String VERSION_4500 = "4.5.0.0"; //$NON-NLS-1$
	public static final String VERSION_5010 = "5.0.1.0"; //$NON-NLS-1$
	public static final String VERSION_5500 = "5.5.0.0"; //$NON-NLS-1$
	public static final String VERSION_5600 = "5.6.0.0"; //$NON-NLS-1$
	public static final String VERSION_6010 = "6.0.1.0"; //$NON-NLS-1$
	public static final String VERSION_6510 = "6.5.1.0"; //$NON-NLS-1$
	public static final String VERSION_7000 = "7.0.0.0"; //$NON-NLS-1$
	public static final String VERSION_TOP = VERSION_7000; //$NON-NLS-1$

	public static final String WIDGET_TYPE_STYLEDTEXT = "STYLEDTEXT";
	public static final String EXT_HTML = "html";
	public static final String EXT_TXT = "txt";


	public static final String BUTTON_TEXT_SYMBOL = "\uFFFC";

	public static final String JAR_FILE_EXTENSION = "*.jar";
	
	
	public static final String OPTION_YES = "yes";
	public static final String OPTION_NO = "no";

}
