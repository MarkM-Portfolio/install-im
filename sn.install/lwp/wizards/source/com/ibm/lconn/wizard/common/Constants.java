/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited. 2008, 2022                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import java.util.ResourceBundle; //6.0CR1, HZ

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class Constants {
	public static final int RE_VALIDATE = -9999;
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String FS = System.getProperty("file.separator");
	public static final String CRLF = System.getProperty("line.separator");
	public static final String UI_LINE_SEPARATOR = "\n";

	public static final String PATH_SEPARATOR = Constants.OS_WINDOWS
			.equals(DefaultValue.getPlatform()) ? ";" : ":";

	public static final String WIZARD_HOME = USER_HOME + FS + "lcWizard";
	public static final String LOG_ROOT = WIZARD_HOME + FS + "log";
	public static final String RESPONSE_ROOT = WIZARD_HOME + FS + "response";
	public static final String OUTPUT_ROOT = WIZARD_HOME + FS + "output";

	static {
		File file = new File(Constants.WIZARD_HOME);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.LOG_ROOT);
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
		}
	}
	public static final String DB_SQLSERVER_XADLL = "sqljdbc_xa.dll";
    public static final String DB_SQLSERVER_2008_DIR_32 = FS + "100"+ FS + "Tools" + FS + "Binn" + FS;
    public static final String DB_SQLSERVER_2008_DIR_64 = FS+ "100"+ FS + "Tools" + FS + "Binn" + FS;
    public static final String DB_SQLSERVER_2012_DIR_32 = FS + "110"+ FS + "Tools" + FS + "Binn" + FS;
    public static final String DB_SQLSERVER_2012_DIR_64 = FS+ "110"+ FS + "Tools" + FS + "Binn" + FS;
	public static final String DB_SQLSERVER_2016_DIR_32 = FS + "130"+ FS  + "Tools" + FS + "Binn" + FS;
    public static final String DB_SQLSERVER_2016_DIR_64 = FS + "130"+ FS  + "Tools" + FS + "Binn" + FS;
	public static final String DB_SCRIPT_PATH = "connections.sql";
	public static final String DB_SCRIPT_S390_PATH = "connections.s390.sql";
	public static final String LIB_DIR = "lib";
	public static final String SCRIPT_DIR = "script";
	public static final String DEPCHECK_DIR = "depcheck";
	public static final String WIZARD_JAR = "Wizards.jar";
	public static final String WIZARD_JAR_LOC = LIB_DIR + FS + WIZARD_JAR;
	public static final String TDIPOPULATION_LOC = "TDIPopulation";
	public static final String TDI_WORK_DIR = TDIPOPULATION_LOC + FS + CommonHelper.getPlatformShortType()+ FS + "TDI";
	public static final String TDI_LAST_SESSION_MAPPING = TDIPOPULATION_LOC
			+ FS + "mappings.properties";
	public static final String TDI_LAST_SESSION_VALIDATION_RULE = TDIPOPULATION_LOC
			+ FS + "vRules.properties";

	public static final String COLOR_WHITE = "COLOR_WHITE";

	// default mapping
	public static final String defaultMappingAD = TDIPOPULATION_LOC + FS
			+ "defaultMapping_ad.properties";
	public static final String defaultMappingADAM = TDIPOPULATION_LOC + FS
			+ "defaultMapping_adam.properties";
	public static final String defaultMappingDOMINO = TDIPOPULATION_LOC + FS
			+ "defaultMapping_domino.properties";
	public static final String defaultMappingSUN = TDIPOPULATION_LOC + FS
			+ "defaultMapping_sun.properties";
	public static final String defaultMappingTIVOLI = TDIPOPULATION_LOC + FS
			+ "defaultMapping_tivoli.properties";
	public static final String defaultMappingNDS = TDIPOPULATION_LOC + FS
			+ "defaultMapping_nds.properties";
	// default valicaton rules
	public static final String validationRules = TDI_WORK_DIR + FS
			+ "validate_dbrepos_fields.properties";

	//TODO modify by Maxi, add new db for feature!
	public static final String DB_NAME_LIBRARY = "FILENET"; //$NON-NLS-1$
	public static final String DB_NAME_LIBRARY_GCD = "FNGCD"; //$NON-NLS-1$
	public static final String DB_NAME_LIBRARY_OS = "FNOS"; //$NON-NLS-1$
	//TODO end!
	

	// added by Jia, for Oracle to check whether liabraries are installed
	public static final String DB_ORCL_USERNAME_LIBRARY_GCD = "FNGCDUSER"; //$NON-NLS-1$
	public static final String DB_ORCL_USERNAME_LIBRARY_OS = "FNOSUSER"; //$NON-NLS-1$
	
	
	public static final String DB_NAME_ACTIVITIES = "OPNACT"; //$NON-NLS-1$
	public static final String DB_NAME_BLOGS = "BLOGS"; //$NON-NLS-1$
	public static final String DB_NAME_COMMUNITIES = "SNCOMM"; //$NON-NLS-1$
	public static final String DB_NAME_DOGEAR = "DOGEAR"; //$NON-NLS-1$
	public static final String DB_NAME_PROFILES = "PEOPLEDB"; //$NON-NLS-1$
	public static final String DB_NAME_HOMEPAGE = "HOMEPAGE";//$NON-NLS-1$
	public static final String DB_NAME_WIKIS = "WIKIS";//$NON-NLS-1$
	public static final String DB_NAME_FILES = "FILES";//$NON-NLS-1$
	public static final String DB_NAME_FORUM = "FORUM";//$NON-NLS-1$
	public static final String DB_NAME_METRICS= "METRICS";//$NON-NLS-1$
	public static final String DB_NAME_COGNOS= "COGNOS";//$NON-NLS-1$
	public static final String DB_NAME_MOBILE= "MOBILE";//$NON-NLS-1$
	public static final String DB_NAME_PNS= "PNS";//$NON-NLS-1$

	//TODO modify by Maxi, add new schema for feature!
	public static final String SCHEMA_NAME_LIBRARY = "FILENET"; //$NON-NLS-1$
	public static final String SCHEMA_NAME_LIBRARY_GCD = "FNGCD"; //$NON-NLS-1$
	public static final String SCHEMA_NAME_LIBRARY_OS = "FNOS"; //$NON-NLS-1$
	//TODO end!

	public static final String SCHEMA_NAME_ACTIVITIES = "ACTIVITIES"; //$NON-NLS-1$
	public static final String SCHEMA_NAME_BLOGS = "BLOGS"; //$NON-NLS-1$
	public static final String SCHEMA_NAME_COMMUNITIES = "SNCOMM"; //$NON-NLS-1$
	public static final String SCHEMA_NAME_DOGEAR = "DOGEAR"; //$NON-NLS-1$
	public static final String SCHEMA_NAME_PROFILES = "EMPINST"; //$NON-NLS-1$
	public static final String SCHEMA_NAME_HOMEPAGE = "HOMEPAGE";//$NON-NLS-1$
	public static final String SCHEMA_NAME_WIKIS = "WIKIS";//$NON-NLS-1$
	public static final String SCHEMA_NAME_FILES = "FILES";//$NON-NLS-1$
	public static final String SCHEMA_NAME_FORUM = "FORUM";//$NON-NLS-1$
	public static final String SCHEMA_NAME_METRICS = "METRICS";//$NON-NLS-1$
	public static final String SCHEMA_NAME_COGNOS = "COGNOS";//$NON-NLS-1$
	public static final String SCHEMA_NAME_MOBILE = "MOBILE";//$NON-NLS-1$
	public static final String SCHEMA_NAME_PNS = "PNS";//$NON-NLS-1$

	public static final String SECTOR_PASSWORD_STORE = "PASSWORD_STORE";//$NON-NLS-1$

	// Feature type
	//TODO modify by Maxi, add new feature item!
	//FEATURE_LIBRARY only used on show, and in all the feature lists or arrays, there is no FEATURE_LIBRARY
	//In data set, there are two new features called FEATURE_LIBRARY_GCD, FEATURE_LIBRARY_OS 
	public static final String FEATURE_LIBRARY = "library"; //$NON-NLS-1$
	public static final String FEATURE_LIBRARY_GCD = "library.gcd"; //$NON-NLS-1$
	public static final String FEATURE_LIBRARY_OS = "library.os"; //$NON-NLS-1$
	//TODO end

	public static final String FEATURE_ACTIVITIES = "activities"; //$NON-NLS-1$
	public static final String FEATURE_BLOGS = "blogs"; //$NON-NLS-1$
	public static final String FEATURE_COMMUNITIES = "communities"; //$NON-NLS-1$
	public static final String FEATURE_DOGEAR = "dogear"; //$NON-NLS-1$
	public static final String FEATURE_PROFILES = "profiles"; //$NON-NLS-1$
	public static final String FEATURE_HOMEPAGE = "homepage"; //$NON-NLS-1$
	public static final String FEATURE_WIKIS = "wikis"; //$NON-NLS-1$
	public static final String FEATURE_FILES = "files"; //$NON-NLS-1$
	public static final String FEATURE_FORUM = "forum"; //$NON-NLS-1$
	public static final String FEATURE_METRICS = "metrics"; //$NON-NLS-1$
	public static final String FEATURE_COGNOS = "cognos"; //$NON-NLS-1$
	public static final String FEATURE_MOBILE = "mobile"; //$NON-NLS-1$
	public static final String FEATURE_PNS = "pushnotification"; //$NON-NLS-1$
	public static final String FEATURE_ACTIVITIESTHEME = "activitiestheme"; //$NON-NLS-1$
	public static final String FEATURE_BLOGSTHEME = "blogstheme"; //$NON-NLS-1$
	public static final String FEATURE_WIKISTHEME = "wikistheme";

	public static final String WIZARD_PAGE_WELCOME = "WELCOME_PANEL";
	public static final String WIZARD_PAGE_DB_SUMMARY = "SummaryPanel";//$NON-NLS-1$
	public static final String WIZARD_PAGE_DB_ACTION_TYPE = "CreateDeleteOption";//$NON-NLS-1$
	public static final String WIZARD_PAGE_DB_TYPE_SELECTION = "DBMSInstallDirSelection";//$NON-NLS-1$
	public static final String WIZARD_PAGE_DB_INSTANCE = "DBInstance";//$NON-NLS-1$
	public static final String WIZARD_PAGE_EXECUTION = "ExecutionPanel";//$NON-NLS-1$
	public static final String WIZARD_PAGE_FEATURE_SELECTION = "FeatureSelection";//$NON-NLS-1$
	public static final String WIZARD_PAGE_FINISH_PANEL = "FinishPanel";//$NON-NLS-1$
	public static final String WIZARD_PAGE_FEATURE_DB_INFORMATION = "featureDBInfo"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_SQLSERVER_FILE = "sqlserverFile"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_JDBC_CONNECTION_INFO = "JDBCConnectionInfo"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_PROFILE_CONNECTION_INFO = "ProfileConnectionInfo";//$NON-NLS-1$
	public static final String WIZARD_PAGE_ENABLE_COMMUNITIES_OPTIONAL_TASK = "enableCommunitiesOptionalTask"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_CONTENT_STORE_MIGRATE = "contentStoreMigrate";
	public static final String WIZARD_PAGE_EXECUTION_NOTE = "ExecutionNotePanel";
	public static final String WIZARD_PAGE_EXECUTION_DETAILDED_COMMAND = "ExecutionDetailedCommandPanel";
	public static final String WIZARD_PAGE_EXECUTION_DETAILDED_COMMAND_EXPORT = "ExecutionDetailedCommandExportPanel";

	// DBMS type
	public static final String DBMS_SQLSERVER = "sqlserver"; //$NON-NLS-1$
	public static final String DBMS_ORACLE = "oracle"; //$NON-NLS-1$
	public static final String DBMS_DB2 = "db2"; //$NON-NLS-1$

	// operating system type
	public static final String OS_WINDOWS = "win";//$NON-NLS-1$
	public static final String OS_AIX = "aix";//$NON-NLS-1$
	public static final String OS_LINUX_SUSE = "sles";//$NON-NLS-1$
	public static final String OS_LINUX_REDHAT = "redhat";//$NON-NLS-1$
	public static final String OS_LINUX = "linux";//$NON-NLS-1$
	public static final String OS_ZLINUX_S390 = "s390";//$NON-NLS-1$

	// java executable
	public static final String JAVA_EXECUTABLE = "java"; //$NON-NLS-1$
	public static final String JAVA_EXECUTABLE_WIN = "java.exe"; //$NON-NLS-1$
	// oracle executable
	public static final String ORACLE_EXECUTABLE = "sqlplus"; //$NON-NLS-1$
	public static final String ORACLE_EXECUTABLE_WIN = "sqlplus.exe"; //$NON-NLS-1$
	// tdi executable
	public static final String TDI_EXECUTABLE = "tdisrvctl"; //$NON-NLS-1$
	public static final String TDI_EXECUTABLE_WIN = "tdisrvctl.bat"; //$NON-NLS-1$
	// tid ext library
	public static final String TDI_JRE_EXT_LIB = "jvm" + FS + "jre" + FS + "lib" + FS + "ext"; //$NON-NLS-1$

	// IBM connection wizard system property
	public static final String LCONN_WIZARD_PROP = "lconn.wizard"; //$NON-NLS-1$

	// Input type
	public static final String INPUT_DB_ACTION_NAME = "actionName"; //$NON-NLS-1$
	public static final String INPUT_DB_ADMIN_PASSWORD = "dbAdminPassword";//$NON-NLS-1$
	public static final String INPUT_FORUM_CONTENT_STORE = "forumContentStore";
	public static final String INPUT_DB_ADMIN_NAME = "accountName"; //$NON-NLS-1$
	public static final String INPUT_DB_INSTANCE_NAME = "instanceName"; //$NON-NLS-1$
	public static final String INPUT_DB_DBAUSER_NAME = "dbaUserName"; //$NON-NLS-1$
	public static final String INPUT_DB_PDB_NAME = "PDBNameInput"; //$NON-NLS-1$
	public static final String INPUT_DB_SYSDBA_PASSWORD = "dbaPasswordInput"; //$NON-NLS-1$
	public static final String INPUT_DB_AS_SYSDBA = "isRunAsSYSDBA"; //$NON-NLS-1$
	public static final String INPUT_DB_USE_PDB = "setPDBNameSelection"; //$NON-NLS-1$
	public static final String INPUT_DB_PDBTEXT_ENABLE = "setPDBNameTextEnabled"; //$NON-NLS-1$
	public static final String INPUT_DB_INSTALL_DIR = "installDir"; //$NON-NLS-1$
	public static final String INPUT_DB_TYPE = "dbType"; //$NON-NLS-1$
	public static final String INPUT_DB_VERSION = "dbVersion"; //$NON-NLS-1$
	public static final String INPUT_DB_USER_PASSWORD = "dbUserPassword";
	public static final String INPUT_DB_USER_PASSWORD_CONFIRM = "INPUT_DB_USER_PASSWORD_CONFIRM";
	public static final String INPUT_DB_USER_SAME_PASSWORD = "samedbUserPassword";
	public static final String INPUT_DB_USER_SAME_PASSWORD_CONFIRM = "sameINPUT_DB_USER_PASSWORD_CONFIRM";
	public static final String INPUT_DB_USER_NAME = "dbUserName"; //$NON-NLS-1$
	public static final String INPUT_DB_SELECTED_FEATURE = "featureName"; //$NON-NLS-1$
	public static final String INPUT_DB_FILE_PATH = "createDbFilePath"; //$NON-NLS-1$
	public static final String INPUT_DB_SAME_FILE_PATH = "createDbSameFilePath"; //$NON-NLS-1$
	public static final String INPUT_DB_IS_REMOTE = "IS_REMOTE";//$NON-NLS-1$
	public static final String INPUT_DB_USE_SAME_PATH = "use_same_path";//$NON-NLS-1$
	public static final String INPUT_DB_USE_SAME_PASSWORD = "use_same_password";//$NON-NLS-1$
	public static final String INPUT_DB_JDBC_HOSTNAME = "jdbc_hostname"; //$NON-NLS-1$
	public static final String INPUT_DB_JDBC_PORT = "jdbc_prot"; //$NON-NLS-1$
	public static final String INPUT_DB_JDBC_DBNAME = "jdbc_dbname"; //$NON-NLS-1$
	public static final String INPUT_DB_JDBC_LIB_PATH = "jdbc_lib_path"; //$NON-NLS-1$
	public static final String INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK = "upgradeCommunitiesUuid"; //$NON-NLS-1$
	public static final String INPUT_DB_NANNY_MODE = "NannyMode"; //$NON-NLS-1$
	public static final String INPUT_DB_EXPORT_ONLY = "ExportOnly"; //$NON-NLS-1$
	public static final String INPUT_FEATURES_SELECT_ALL = "FeaturesSelectAll"; //$NON-NLS-1$
	
	public static final String VARIABLE_DB_UPGRADE_VERSION = "db_upgrade_version"; //$NON-NLS-1$
	public static final String VARIABLE_COMMAND_COUNT = "CommandCount"; //$NON-NLS-1$
	public static final String VARIABLE_FEATURE_COUNT = "FeatureCount"; //$NON-NLS-1$
	public static final String VARIABLE_FEATURE_NAME_TMP = "FeatureNameTmp"; //$NON-NLS-1$
	public static final String VARIABLE_COMMAND_TMP = "CommandTmp";
	public static final String VARIABLE_FEATURE_COMMAND_MAP = "FeatureCommandMap";
	public static final String VARIABLE_FEATURE_RESULT_MAP = "FeatureResultMap";
	public static final String VARIABLE_SQL_SCRIPT_MAP = "SqlScriptMap";
	public static final String VARIABLE_EXECUTE_PAGE_ENTERED_FLAG = "ExecutePageEnteredFlag";
	
	public static final String COMMAND_RETURN_ERROR = "command has errors";//$NON-NLS-1$
	public static final String COMMAND_RETURN_WARNING = "command has warnings";//$NON-NLS-1$
	public static final String COMMAND_RETURN_SUCCESS = "command is successful";//$NON-NLS-1$
	public static final String COMMAND_RETURN_EXCEPTION = "command is terminated with exception";//$NON-NLS-1$

	public static final String[] DBMS_DB_TYPE_ALL = new String[] { DBMS_DB2,
			DBMS_ORACLE, DBMS_SQLSERVER };

	public static final String LOG_MESSAGE = "LOG_MESSAGE";//$NON-NLS-1$

	public static final String BOOL_TRUE = "yes";

	public static final String BOOL_FALSE = "no";

	public static final int FEATURE_COUNT_MAX = 12;
	public static String[] FEATURE_ALL = new String[] {
		FEATURE_ACTIVITIES,
		FEATURE_BLOGS,
		FEATURE_DOGEAR,
		FEATURE_COMMUNITIES,
		FEATURE_FILES,
		FEATURE_FORUM,
		FEATURE_HOMEPAGE,
		FEATURE_METRICS,
		FEATURE_MOBILE,
		FEATURE_PROFILES,
		FEATURE_PNS,
		FEATURE_WIKIS,
		};
	
	public static final String OPERATION_TYPE_DROP_DB = "delete"; //$NON-NLS-1$
	public static final String OPERATION_TYPE_CREATE_DB = "create"; //$NON-NLS-1$
	public static final String OPERATION_TYPE_UPGRADE_DB = "upgrade"; //$NON-NLS-1$

	public static final String ID_UI = "ID_UI";//$NON-NLS-1$

	public static final String CHECK_RESULT_DB_INSTALL_DIR_EMPTY = "CHECK_RESULT_DB_INSTALL_DIR_EMPTY";

	public static final String CHECK_RESULT_DB_INSTANCE_NAME_EMPTY = "CHECK_RESULT_DB_INSTANCE_NAME_EMPTY";

	public static final String CHECK_RESULT_DB_ADMIN_NAME_EMPTY = "CHECK_RESULT_DB_ADMIN_NAME_EMPTY";

	public static final String CHECK_RESULT_DB_ADMIN_PASSWORD_EMPTY = "CHECK_RESULT_DB_ADMIN_PASSWORD_EMPTY";

	public static final String CHECK_RESULT_DB_USER_NAME_EMPTY = "CHECK_RESULT_DB_USER_NAME_EMPTY";

	public static final String CHECK_RESULT_DB_USER_PASSWORD_EMPTY = "CHECK_RESULT_DB_USER_PASSWORD_EMPTY";

	public static final String CHECK_RESULT_DB_USER_PASSWORD_NOT_MATCH = "CHECK_RESULT_DB_USER_PASSWORD_NOT_MATCH";

	public static final String TEXT_EMPTY_STRING = "";

	public static final String TAG_DEFAULT_VALUE = ".defaultValue";

	public static final String TAG_LABEL = ".label";

	public static final String TAG_TOOLTIP = ".tooltip";

	public static final String TAG_BUTTON_IDS = ".buttonIds";

	public static final String TAG_BUTTON_TXT = ".buttonTxt";

	public static final String TAG_DIR_BROWSER_MSG = ".dirBrowserMsg";

	public static final String DEFAULT_DIR_BROWSER_MSG = "default.dirBrowser.msg";
	public static final String DEFAULT_FILE_BROWSER_MSG = "default.fileBrowser.msg";

	public static final String CELLEDITOR_DROPDOWN = "DROPDOWN";

	public static final String CELLEDITOR_TEXT = "TEXT";

	public static final String WIDGET_TYPE_RADIO = "RADIO";

	public static final String WIDGET_TYPE_LABEL = "LABEL";

	public static final String WIDGET_TYPE_TEXT = "TEXT";

	public static final String WIDGET_TYPE_CHECK = "CHECK";

	public static final Object WIDGET_TYPE_TABLE = "TABLE";

	public static final String WIDGET_TYPE_CHECKGROUP = "CHECK_GROUP";

	public static final String WIDGET_TYPE_DIR_BROWSER = "DIR_BROWSER";

	public static final String WIDGET_PROP_DIR_BROWSER_DIALOG_MSG = "WIDGET_PROP_DIR_BROWSER_DIALOG_MSG";
	public static final String WIDGET_PROP_DIR_BROWSER_BUTTON_TEXT = "WIDGET_PROP_DIR_BROWSER_BUTTON_TEXT";
	public static final String WIDGET_PROP_FILE_BROWSER_DIALOG_MSG = "WIDGET_PROP_FILE_BROWSER_DIALOG_MSG";
	public static final String WIDGET_PROP_FILE_BROWSER_BUTTON_TEXT = "WIDGET_PROP_FILE_BROWSER_BUTTON_TEXT";

	public static final String INPUT_TDI_ACTION_TYPE = "INPUT_TDI_ACTION_TYPE";
	public static final String INPUT_TDI_INSTALL_DIR = "INPUT_TDI_INSTALL_DIR";
	public static final String INPUT_TDI_DB_CHOOSER = "INPUT_TDI_DB_CHOOSER";
	public static final String INPUT_TDI_DB_HOSTNAME = "INPUT_TDI_DB_HOSTNAME";
	public static final String INPUT_TDI_DB_PORT = "INPUT_TDI_DB_PORT";
	public static final String INPUT_TDI_DB_NAME = "INPUT_TDI_DB_NAME";
	public static final String INPUT_TDI_DB_USER = "INPUT_TDI_DB_USER";
	public static final String INPUT_TDI_DB_PASSWD = "INPUT_TDI_DB_PASSWD";
	public static final String INPUT_TDI_LDAP_TYPE = "INPUT_TDI_LDAP_TYPE";
	public static final String INPUT_TDI_LDAP_SERVER_NAME = "INPUT_TDI_LDAP_SERVER_NAME";
	public static final String INPUT_TDI_LDAP_SERVER_PORT = "INPUT_TDI_LDAP_SERVER_PORT";
	public static final String INPUT_TDI_LDAP_LOGIN_USER = "INPUT_TDI_LDAP_LOGIN_USER";
	public static final String INPUT_TDI_LDAP_LOGIN_PASSWD = "INPUT_TDI_LDAP_LOGIN_PASSWD";
	public static final String INPUT_TDI_LDAP_ANONYMOUS_ACCESS = "INPUT_TDI_LDAP_ANONYMOUS_ACCESS";
	public static final String INPUT_TDI_LDAP_SEARCH_BASE = "INPUT_TDI_LDAP_SEARCH_BASE";
	public static final String INPUT_TDI_LDAP_SERVER_FILTER = "INPUT_TDI_LDAP_SERVER_FILTER";
	public static final String INPUT_TDI_LDAP_USE_SSL = "INPUT_TDI_LDAP_USE_SSL";
	public static final String INPUT_TDI_MAPPING_TABLE = "INPUT_TDI_MAPPING_TABLE"; //$NON-NLS-1$
	public static final String INPUT_TDI_OPTIONAL_TASK = "INPUT_TDI_OPTIONAL_TASK";
	public static final String INPUT_OS_TYPE = "INPUT_OS_TYPE";
	public static final String INPUT_TDI_SUMMARY = "INPUT_TDI_SUMMARY";
	public static final String INPUT_TDI_FINISH = "INPUT_TDI_FINISH";
	public static final String INPUT_TDI_SSL_KEY_STORE = "INPUT_TDI_SSL_KEY_STORE";
	public static final String INPUT_TDI_SSL_PASSWORD = "INPUT_TDI_SSL_PASSWORD";
	public static final String INPUT_TDI_SSL_TYPE = "INPUT_TDI_SSL_TYPE";
	public static final String INPUT_TDI_DB_JDBC_LIB = "INPUT_TDI_DB_JDBC_LIB";
	public static final String INPUT_TDI_MARK_MANGER_CHECK = "INPUT_TDI_OPTIONAL_MARK_MANAGER";

	// Common welcome panel
	public static final String WIZARD_PAGE_Welcome = "Welcome"; //$NON-NLS-1$

	// Wizard pages for TDI populationg
	public static final String WIZARD_PAGE_ActionChooser = "ActionChooser"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_TDIInstallDir = "TDIInstallDir";//$NON-NLS-1$
	public static final String WIZARD_PAGE_TDIChooser = "TDIChooser"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_DBChooser = "DBChooser"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_DBInfo = "DBInfo"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_LDAPChooser = "LDAPChooser"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_LDAPInfo = "LDAPInfo"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_LDAPSSLInfo = "LDAPSSLInfo"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_LDAPSearch = "LDAPSearchInfo"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_AttributeMapping = "AttributeMapping"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_OptionalTasks = "OptionalTask"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_Summary = "Summary"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_Result = "Result"; //$NON-NLS-1$
	public static final String WIZARD_PAGE_TDI_SUMMARY = "TDI_SUMMARY_PANEL";//$NON-NLS-1$
	public static final String WIZARD_PAGE_TDI_FINISH = "TDI_FINISH_PANEL";//$NON-NLS-1$
	public static final String WIZARD_PAGE_LDAPLoginInfo = "LDAPLoginInfo";//$NON-NLS-1$
	// fake page id identifying 'Exit'
	public static final String WIZARD_PAGE_Exit = "Exit"; //$NON-NLS-1$

	// Wizard action type
	public static final String WIZARD_ACTION_PREVIOUS = "Previous"; //$NON-NLS-1$
	public static final String WIZARD_ACTION_NEXT = "Next"; //$NON-NLS-1$
	public static final String WIZARD_ACTION_CANCEL = "Cancel"; //$NON-NLS-1$
	public static final String WIZARD_ACTION_FINISH = "Finish"; //$NON-NLS-1$

	// Wizard pages

	public static final String TDI_UI_VALUES_PROP = "values.properties";
	public static final String TDI_FROM_SOURCE_PROP = "map_dbrepos_from_source.properties";
	public static final String TDI_VALIDATE_PROP = "validate_dbrepos_fields.properties";
	public static final String TDI_PROP = "profiles_tdi.properties";
	public static final String ERROR_VALIDATOR_LOAD_FAIL = "ERROR_VALIDATOR_LOAD_FAIL";
	public static final String ERROR_TDI_WIZARD_DEFINE_LOAD_FAIL = "ERROR_TDI_WIZARD_DEFINE_LOAD_FAIL";
	public static final String ERROR_FILE_NOT_EXIST = "ERROR_FILE_NOT_EXIST";
	public static final String WARN_WIZARD_ICON_IMAGE_LOADING_FAIL = "WARN_WIZARD_ICON_IMAGE_LOADING_FAIL";
	public static final String WARN_DB_WIZARD_SETTING_MISSING = "WARN_DB_WIZARD_SETTING_MISSING";

	public static final String WIZARD_ID_TDIPOPULATE = "tdipopulate";
	public static final String WIZARD_ID_CLUSTER = "cluster";

	public static final String DB_DB2 = "db2";
	public static final String DB_ORACLE = "oracle";
	public static final String DB_SQLSERVER = "sqlserver";
	public static final String LDAP_DOMINO = "IBM Domino Directory Server";
	public static final String LDAP_TIVOLI = "IBM Tivoli Directory Server";
	public static final String LDAP_AD = "Microsoft Active Directory";
	public static final String LDAP_ADAM = "Microsoft Active Directory Application Mode";
	public static final String LDAP_ORACLE = "Oracle One Directory";
	public static final String LDAP_NDS = "Novell Directory Services";

	public static final String LDAP_OPTIONAL_TASK_MARK_MANAGER = "LDAP_OPTIONAL_TASK_MARK_MANAGER";
	public static final String LDAP_OPTIONAL_TASK_FILL_COUNTRIES = "LDAP_OPTIONAL_TASK_FILL_COUNTRIES";
	public static final String LDAP_OPTIONAL_TASK_FILL_DEPARTMENT = "LDAP_OPTIONAL_TASK_FILL_DEPARTMENT";
	public static final String LDAP_OPTIONAL_TASK_FILL_ORGANIZATION = "LDAP_OPTIONAL_TASK_FILL_ORGANIZATION";
	public static final String LDAP_OPTIONAL_TASK_FILL_EMPLOYEE = "LDAP_OPTIONAL_TASK_FILL_EMPLOYEE";
	public static final String LDAP_OPTIONAL_TASK_FILL_WORK_LOCATION = "LDAP_OPTIONAL_TASK_FILL_WORK_LOCATION";

	public static final Object WIDGET_TYPE_TEXTAREA = "TEXTAREA";

	// public static final String INPUT_TDI_LDAP_SEARCH_BASE =
	// "TDI_LDAP_BASEDNES";
	public static final String TDI_LDAP_OBJECTCLASSES = "TDI_LDAP_OBJECTCLASSES";
	public static final String MSG_LOG_PATH = "MSG.LOG_PATH";

	// use -DldapSearchLimit=1000 to set the value
	public static final String LDAP_SEARCH_LIMIT = "ldapSearchLimit"; //$NON-NLS-1$
	// use -Dloglevel=ALL to set the value
	public static final String LOG_LEVEL = "loglevel"; //$NON-NLS-1$
	public static final String WELCOME_IMAGE = "WELCOME_IMAGE";
	public static final String WELCOME_INTRO = "WELCOME_INTRO";
	public static final String WELCOME_LICENCE = "WELCOME_LICENCE";
	public static final String WELCOME_INFOCENTER = "WELCOME_INFOCENTER";
	public static final String WIZARD_PAGE_COMMON_WELCOME = "WIZARD_PAGE_COMMON_WELCOME";
	public static final String WELCOME_CLASS = "com.ibm.lconn.wizard.common.ui.LCWelcomePanel";
	public static final String WIZARD_PAGE_COMMON_EXECUTION = "WIZARD_PAGE_COMMON_EXECUTION";
	public static final String WIZARD_PAGE_COMMON_SUMMARY = "WIZARD_PAGE_COMMON_SUMMARY";
	public static final Object WIDGET_TYPE_DROPDOWN = "DROPDOWN";
	public static final String WIZARD_PAGE_COMMON_FINISH = "WIZARD_PAGE_COMMON_FINISH";
	public static final String WIDGET_TYPE_FILEBROWSERCHECKGROUP = "FILEBROWSERCHECKGROUP";

	public static final int DISABLE_BACK = 1 << 2;
	public static final int DISABLE_NEXT = 1 << 3;
	public static final int DISABLE_CANCEL = 1 << 4;
	public static final int DISABLE_NONE = 0;

	public static final List<String> BUILT_IN_LDAP_ATTRIBUTE = Collections
			.unmodifiableList(Arrays.asList(new String[] { "$dn",
					"$secretary_uid", "$manager_uid" }));
	public static final String TIVOLI_UUID = "ibm-entryUUID";
	public static final String DOMINO_UUID = "dominoUNID";
	public static final String AD_UUID = "externalId";
	public static final String ORACLE_UUID = "nsuniqueid";
	public static final String NULL = "null";
	public static final String TDI_DEFAULT_TASK = "tdipopulate";

	public static final String TDI_START_FROM_LAST_SESSION = "LastSession";
	public static final Object WIDGET_TYPE_PASSWORD = "PASSWORD";
	public static final Object WIDGET_TYPE_FILE_BROWSER = "FILE_BROWSER";
	public static final String WIDGET_PROP_FILE_BROWSER_EXT_FILTER = "WIDGET_PROP_FILE_BROWSER_EXT_FILTER";

	//public static final String JDBC_DB2_LIB = "db2jcc.jar";
	public static final String JDBC_DB2_LIB = "db2jcc4.jar";
	public static final String JDBC_DB2_LICENSE = "db2jcc_license_cu.jar";
	public static final String JDBC_ORACLE_LIB = "ojdbc7.jar";
	public static final String JDBC_SQLSERVER_LIB = "sqljdbc41.jar";

	public static final String KEY_CASTORE = "javax.net.ssl.trustStore";
	public static final String KEY_CATYPE = "javax.net.ssl.trustStoreType";
	public static final String KEY_CAPASSWORD = "javax.net.ssl.trustStorePassword";

	public static final String TDI_KEY_CPASSWORD = "{protect}-"
			+ KEY_CAPASSWORD;
	public static final String TDI_SOL_PROPS = "solution.properties";
	public static final String WARN_SAVE_SESSION_FAIL = "WARN_SAVE_SESSION_FAIL";
	public static final String WARN_LOAD_SESSION_FAIL = "LastSessionLoadFail";
	public static String BUTTON_TEXT_FINISH = Messages
			.getString("button.finish.text");
	public static String BUTTON_TEXT_NEXT = Messages
			.getString("button.next.text");
	public static String BUTTON_TEXT_BACK = Messages
			.getString("button.prev.text");
	public static String BUTTON_TEXT_CANCEL = Messages
			.getString("button.cancel.text");
	public static final String BUTTON_TEXT_VIEWLOG = Messages
			.getString("button.viewlog.text");
	public static final String BUTTON_TEXT_OPENLOG = Messages
			.getString("button.openlog.text");
	public static final String BUTTON_TEXT_OPENSQL = Messages
	.getString("button.opensql.text");
	public static final String BUTTON_TEXT_VIEWJAR = Messages
	.getString("button.viewjar.text");

	public static String BUTTON_TEXT_OK = Messages.getString("button.OK.text");

	public static String BUTTON_TEXT_YES = Messages
			.getString("button.YES.text");
	public static String BUTTON_TEXT_NO = Messages.getString("button.NO.text");
	public static String BUTTON_TEXT_CONFIG = Messages
			.getString("button.config.text");

	public static final String LDAP_DEFAULT_PORT = "389";
	public static final String LDAP_DEFAULT_SSL_PORT = "636";
	public static final String NETSTORE_NEED_STOP = "NETSTORE_NEED_STOP";

	public static final Set<String> TDI_SUPPORTED_LDAP_SERVER = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[] {
					LDAP_AD, LDAP_DOMINO, LDAP_NDS, LDAP_TIVOLI, LDAP_ORACLE,
					LDAP_ADAM })));
	static final String PREFIX_GLOBAL_SCRIPT = "global.script";
	static final String OS_OTHER = "other";
	static final String FILE_COPY_DIR = "cpdir";
	static final String FILE_REMOVE_DIR = "rmdir";
	static final String FILE_MAKE_DIR = "mkdir";
	static final String FILE_REMOVE_FILE = "rmfile";
	public static final String FILE_COPY_FILE = "cpfile";
	public static final String INPUT_PARAM_PROP_FILE = "PARAM_PROP_FILE";
	public static final String WIZARD_LAUNCH_MODE = "WIZARD_LAUNCH_MODE";
	public static final String LAUNCH_MODE_CONSOLE = "LAUNCH_MODE_CONSOLE";
	public static final int EXIT_STATUS_PARAMETER_ERROR = 1;
	public static final int EXIT_STATUS_VALIDATION_ERROR = 7;
	public static final int EXIT_STATUS_NORMAL = 0;
	public static final int EXIT_STATUS_PROPERTY_FILE_ERROR = 3;
	public static final int EXIT_STATUS_OTHER = 99;
	public static final String STYLE_READ_ONLY = "STYLE_READ_ONLY";

	public static final String VERSION_10X = "1.0.x"; //$NON-NLS-1$
	public static final String VERSION_100 = "1.0.0"; //$NON-NLS-1$
	public static final String VERSION_101 = "1.0.1"; //$NON-NLS-1$
	public static final String VERSION_102 = "1.0.2"; //$NON-NLS-1$
	public static final String VERSION_200 = "2.0.0"; //$NON-NLS-1$
	public static final String VERSION_201 = "2.0.1"; //$NON-NLS-1$
	public static final String VERSION_250 = "2.5.0"; //$NON-NLS-1$
	public static final String VERSION_300 = "3.0.0"; //$NON-NLS-1$
	public static final String VERSION_301 = "3.0.1"; //$NON-NLS-1$
	public static final String VERSION_400 = "4.0.0"; //$NON-NLS-1$
	public static final String VERSION_40CR2 = "4.0CR2"; //$NON-NLS-1$
	public static final String VERSION_40CR3 = "4.0CR3"; //$NON-NLS-1$
	//TODO modify by Maxi, add new version id
	public static final String VERSION_450 = "4.5.0"; //$NON-NLS-1$
	public static final String VERSION_45CR4 = "4.5CR4"; //$NON-NLS-1$
	public static final String VERSION_50 = "5.0.0"; //$NON-NLS-1$
	public static final String VERSION_550 = "5.5.0"; //$NON-NLS-1$
	public static final String VERSION_60 = "6.0.0"; //$NON-NLS-1$
	
	//6.0CR1 HongZhu, get top version from cmdContent.properties
	//public static final String VERSION_TOP = VERSION_60; //$NON-NLS-1$	
	protected final static ResourceBundle resource = ResourceBundle.getBundle("com.ibm.lconn.wizard.dbconfig.backend.cmdContent");
	public static final String full_ver_list = resource.getString("upgrade.path");
	public static final String VERSION_TOP = full_ver_list.substring(full_ver_list.lastIndexOf(",")+1);
	
	//TODO end

	public static final Map<String, Map<String, String>> featureDBMapping;
	public static final String WIDGET_TYPE_STYLEDTEXT = "STYLEDTEXT";
	public static final String EXT_HTML = "html";
	public static final String EXT_TXT = "txt";

	static {
		Map<String, Map<String, String>> temp = new HashMap<String, Map<String, String>>();

		Map<String, String> feature2db = new HashMap<String, String>();
		//TODO modify by Maxi, map new features with dbs!
		feature2db.put(Constants.FEATURE_LIBRARY_GCD, Constants.DB_NAME_LIBRARY_GCD);
		feature2db.put(Constants.FEATURE_LIBRARY_OS, Constants.DB_NAME_LIBRARY_OS);
		//TODO end!
		
		feature2db.put(Constants.FEATURE_ACTIVITIES,
				Constants.DB_NAME_ACTIVITIES);
		feature2db.put(Constants.FEATURE_BLOGS, Constants.DB_NAME_BLOGS);
		feature2db.put(Constants.FEATURE_COMMUNITIES,
				Constants.DB_NAME_COMMUNITIES);
		feature2db.put(Constants.FEATURE_DOGEAR, Constants.DB_NAME_DOGEAR);
		feature2db.put(Constants.FEATURE_PROFILES, Constants.DB_NAME_PROFILES);
		feature2db.put(Constants.FEATURE_HOMEPAGE, Constants.DB_NAME_HOMEPAGE);
		feature2db.put(Constants.FEATURE_WIKIS, Constants.DB_NAME_WIKIS);
		feature2db.put(Constants.FEATURE_FILES, Constants.DB_NAME_FILES);
		feature2db.put(Constants.FEATURE_FORUM, Constants.DB_NAME_FORUM);
		feature2db.put(Constants.FEATURE_METRICS, Constants.DB_NAME_METRICS);
		feature2db.put(Constants.FEATURE_COGNOS, Constants.DB_NAME_COGNOS);
		feature2db.put(Constants.FEATURE_MOBILE, Constants.DB_NAME_MOBILE);
		feature2db.put(Constants.FEATURE_PNS, Constants.DB_NAME_PNS);
		feature2db = Collections.unmodifiableMap(feature2db);

		temp.put(Constants.DB_DB2, feature2db);
		temp.put(Constants.DB_SQLSERVER, feature2db);

		Map<String, String> feature2schema = new HashMap<String, String>();
		//TODO modify by Maxi, map new features with schemas!
		feature2schema.put(Constants.FEATURE_LIBRARY_GCD, Constants.SCHEMA_NAME_LIBRARY_GCD);
		feature2schema.put(Constants.FEATURE_LIBRARY_OS, Constants.SCHEMA_NAME_LIBRARY_OS);
		//TODO end!
		
		feature2schema.put(Constants.FEATURE_ACTIVITIES,
				Constants.SCHEMA_NAME_ACTIVITIES);
		feature2schema
				.put(Constants.FEATURE_BLOGS, Constants.SCHEMA_NAME_BLOGS);
		feature2schema.put(Constants.FEATURE_COMMUNITIES,
				Constants.SCHEMA_NAME_COMMUNITIES);
		feature2schema.put(Constants.FEATURE_DOGEAR,
				Constants.SCHEMA_NAME_DOGEAR);
		feature2schema.put(Constants.FEATURE_PROFILES,
				Constants.SCHEMA_NAME_PROFILES);
		feature2schema.put(Constants.FEATURE_HOMEPAGE,
				Constants.SCHEMA_NAME_HOMEPAGE);
		feature2schema
				.put(Constants.FEATURE_WIKIS, Constants.SCHEMA_NAME_WIKIS);
		feature2schema
				.put(Constants.FEATURE_FILES, Constants.SCHEMA_NAME_FILES);
		feature2schema
		.put(Constants.FEATURE_FORUM, Constants.SCHEMA_NAME_FORUM);
		feature2schema
		.put(Constants.FEATURE_METRICS, Constants.SCHEMA_NAME_METRICS);
		feature2schema
		.put(Constants.FEATURE_COGNOS, Constants.SCHEMA_NAME_COGNOS);
		feature2schema
		.put(Constants.FEATURE_MOBILE, Constants.SCHEMA_NAME_MOBILE);
		feature2schema
		.put(Constants.FEATURE_PNS, Constants.SCHEMA_NAME_PNS);
		feature2schema = Collections.unmodifiableMap(feature2schema);

		temp.put(Constants.DB_ORACLE, feature2schema);

		featureDBMapping = Collections.unmodifiableMap(temp);
	}

	public static final String TEXT_BUTTON_DETAIL = "TEXT_BUTTON_DETAIL";
	public static final String TEXT_BUTTON_ACTION = "TEXT_BUTTON_ACTION";
	public static final String TEXT_BUTTON_LABEL = "TEXT_BUTTON_LABEL";
	public static final String BUTTON_TEXT_SYMBOL = "\uFFFC";

	public static final String JAR_FILE_EXTENSION = "*.jar";
	public static final String LOCALHOST = "localhost";
	public static final String LOOPBACK_ADDRESS = "127.0.0.1";

	public static final int ERROR_NO_JDBC_DRIVER = 50;
	public static final int ERROR_INVALID_AUTHENTICATION = 10;
	public static final int ERROR_DB_NOT_EXIST = 20;
	public static final int ERROR_COMMUNICATION_ERROR = 30;
	public static final int ERROR_COMMUNICATION_ERROR_TDI = 301;
	public static final int ERROR_UNKNOWN = 15;
	public static final String OPTION_MARKMANGER_YES = "OPTION_MARK_MANGER_YES";
	public static final String OPTION_MARKMANGER_NO = "OPTION_MARK_MANGER_NO";

	public static final String OPTION_YES = "yes";
	public static final String OPTION_NO = "no";

	public static final Map<String, String> optionalTaskFileMap;
	static {
		Map<String, String> map = new HashMap<String, String>();
 		map.put(Constants.LDAP_OPTIONAL_TASK_FILL_DEPARTMENT, "deptinfo.csv");
 		map.put(Constants.LDAP_OPTIONAL_TASK_FILL_ORGANIZATION, "orginfo.csv");
 		map.put(Constants.LDAP_OPTIONAL_TASK_FILL_EMPLOYEE, "emptype.csv");
		map.put(Constants.LDAP_OPTIONAL_TASK_FILL_WORK_LOCATION, "workloc.csv");
		map.put(Constants.LDAP_OPTIONAL_TASK_FILL_COUNTRIES, "isocc.csv");
		optionalTaskFileMap = Collections.unmodifiableMap(map);
	}

}
