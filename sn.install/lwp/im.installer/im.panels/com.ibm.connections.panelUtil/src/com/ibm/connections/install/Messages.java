/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.ibm.connections.install.messages"; //$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

	public static String CCMPanelName;
	public static String CognosPanelName;
	public static String ContentStorePanelName;
    public static String DatabasePanelName;
    public static String NotificationPanelName;
    public static String TopologyPanelName;
    public static String WasPanelName;
    public static String WebServerPanelName;
    
    ////
	public static String NOTE;// =Note:
	// common
	public static String COMMON_CONFIG;// =sCommon Configurations
	public static String HOST_NAME; // =Host Name
	public static String HOST_PORT; // =Port
	public static String ST;
	public static String VERIFIED; // =Host verified
	public static String VERIFICATION; // =Verification
	public static String HOST_VERIFICATION; // =Click Verify to verify the Host
	public static String VALIDATE; // =Validate
	public static String SKIP_VALIDATION; // =Skip Validation
	public static String GOTO_NEXT;// = Enter 'N' to go to next panel
	public static String INPUT_WAS_INFO; // =Input WAS Info
	public static String VALIDATED; // =Validated
	public static String TRUE; // =True
	public static String FALSE; // =False
	public static String PREVIOUS_INPUT_INDEX;// =P

	public static String BACK_TO_TOP_INDEX;// =B
	public static String VALIDATE_INDEX;// =V
	public static String NEXT_INDEX;// =N

	public static String INVALID_INPUT_ERROR;// Invalid input!
	public static String UNABLE_TO_MODIFY_ERROR;// Invalid input!
	public static String CONFIRM_VALIDATION;// =Press 'Enter' to start
	public static String BACK_TO_TOP_NULL;
	public static String BACK_TO_TOP; // = B. Back to Top
	public static String BACK_TO_TOP_OR_REVALIDATE;
	
	public static String NOTICE_PREVIOUS;
	
	public static String MULTI_CHOICE_INFO;// =Enter a number from the list
	
	public static String SINGLE_CHOICE_INFO;// =Enter a number from the list
	public static String install_type_pn; // =Primary Node
	public static String install_type_dm; // =Deployment Manager
	public static String install_type_sn; // =Secondary Node
	public static String install_type_cell; // =Cell
	public static String install_type_stnode; // =Standard Node
	public static String status_complete; // =Complete
	public static String component_type_ps; // =Packet Switcher
	public static String component_type_pr; // =Proxy Registrar
	public static String component_type_cf; // =Conference Focus
	public static String component_type_complete; // =Packet Switcher, Proxy
													// Registrar, Conference
													// Focus

	public static String PROGRESS_INFORMATION;
	
	// for features
	public static String HOMEPAGE;
	public static String NEWS;
	public static String SEARCH;
	public static String ACTIVITIES;
	public static String BLOGS;
	public static String COMMUNITIES;
	public static String DOGEAR;
	public static String PROFILES;
	public static String WIKIS;
	public static String FILES;
	public static String FORUMS;
	public static String MOBILE;
	public static String MODERATION;
	public static String METRICS;
	public static String CCM;
	public static String RTE;
	public static String COMMON;
	public static String WIDGET_CONTAINER;
	public static String PUSH_NOTIFICATION;
	public static String IC360;
	public static String ICEC;
	public static String ICEC_WARNING_MSG;
	//public static String QUICK_RESULTS;
	public static String GCD;
	public static String OBJECTSTORE;
	public static String NOTICFEATURE;
	
	public static String ERROR_CORE_FEATURE_NOT_SELECTED;
	public static String ERROR_COMMUNITIES_NOT_SELECTED;
	// for was panel
	public static String WAS_INFO_MSG1;
	public static String WAS_INSTALL_PANEL1;
	public static String WAS_SELECTION;
	public static String WAS_DEPLOY_MANAGER;
	public static String WAS_DEPLOY_MANAGER_INFO;
	
	public static String WAS_DEPLOY_MANAGER_DESC;
	public static String WAS_LOCATION;
	public static String WAS_LOCATION_BROWSER_MSG;
	public static String WAS_HOST;
	public static String dmSecurity_msg1;
	public static String dmSecurity_info;
	public static String dmSecurity_username;
	public static String dmSecuirty_passwd;
	public static String dmSecuirty_port;
	public static String dmDetect_was_btn;
	public static String DETECT_WAS_VERSION_ERROR;
	public static String WAS_VERSION_NOT_SUPPORT;

	// warning for was panel

	public static String NO_WAS_ERROR;
	public static String WAS_SELECTION_WARNING;
	public static String WAS_PROFILE_SELECTION_WARNING;
	public static String WAS_GET_PROFILE_PATH_WARNING;
	public static String WAS_GET_SSL_CERTIFICATE_ERROR;
	public static String WAS_NO_ADMIN_SECURITY_ERROR;
	public static String WAS_JAVA2_SECURITY_ERROR;
	public static String WAS_NO_APP_SECURITY_ERROR;
	public static String WAS_GET_PY_PATH_ERROR;
	public static String WAS_CONNECT_DM_ERROR;
	public static String WAS_NODEAGENT_UNSTARTED_ERROR;
	public static String WAS_NODE_NUM_ERROR;
	public static String Existing_Feature_Selected_Error;
	public static String WAS_BTN_DETECT_CONTINUE_WARNING;
	public static String WAS_BTN_VALIDATE_CONTINUE_WARNING;
	public static String WAS_OPEN_FILE_WARNING1;
	public static String WAS_OPEN_FILE_WARNING2;
	
	public static String WAS_GET_PORT_ERROR;

	public static String WAS_DETECT_START;
	public static String WAS_DETECT_SYSTEM_APP;
	public static String WAS_DETECT_OAUTH_PROVIDER_EAR;
	public static String WAS_DETECT_NODE_AGENTS;
	public static String WAS_GET_CERT;
	public static String WAS_CHECK_ADMIN_SECURITY;
	public static String WAS_CHECK_JAVA2_SECURITY;
	public static String WAS_CHECK_APP_SECURITY;
	public static String WAS_GET_DM_INFO;
	public static String WAS_CHECK_INSTALL_FE;
	public static String WAS_START_DETECT_WAS_LOC;
	public static String WAS_DETECT_WAS_LOC;
	public static String WAS_PY_SCRIPT_MISSING_ERROR;
	public static String WAS_VALIDATION_NOTICE_INFO;

	public static String INPUT_WAS_INFO_SECTION;
	public static String INPUT_WAS_INFO_SECTION_DES;
	public static String INPUT_WAS_INFO_WAS_LOC;
	public static String INPUT_WAS_INFO_DM_LOC;
	public static String INPUT_WAS_INFO_DM_NAME;
	public static String INPUT_WAS_INFO_CELL;
	public static String INPUT_NODES_INFO_SECTION;
	public static String INPUT_NODES_INFO_SECTION_DES;
	public static String INPUT_NODES_INFO_NODE_NAME;
	public static String INPUT_NODES_INFO_HOST_NAME;
	public static String INPUT_NODES_INFO_NODE_NAME2;
	public static String INPUT_NODES_INFO_HOST_NAME2;
	public static String INPUT_NODES_INFO_ADD_BUTTON;
	public static String INPUT_NODES_INFO_REMOVE_BUTTON;

	
	public static String DEPOLOGY_NO_NODE_ERROR;
	// for depology panel
	public static String DEPOLOGY_PANEL;
	
	public static String DEPOLOGY_NODE_NAME;
	
	public static String DEPOLOGY_HOST_NAME;
	public static String DEPOLOGY_PANEL_INFO;
	public static String DEPOLOGY_SELECTION_INFO;
	
	public static String DEPOLOGY_SELECTION_INFO_CONSOLE;
	
	public static String DEPOLOGY_CLUSTERS;
	public static String SMALL_DEPOLOGY_SELECTION_INFO;
	public static String MEDIUM_DEPOLOGY_SELECTION_INFO;
	public static String LARGE_DEPOLOGY_SELECTION_INFO;
	public static String DEPOLOGY_INPUT_CLUSTER;
	public static String DEPOLOGY_INPUT_CLUSTER_INFO_INSTALL;
	public static String DEPOLOGY_INPUT_CLUSTER_INFO;
	
	public static String DEPOLOGY_INPUT_CLUSTER_INFO_CONSOLE;
	public static String DEPOLOGY_SMALL_INPUT_CLUSTER_INFO;
	public static String DEPOLOGY_MEDIUM_INPUT_CLUSTER_INFO;
	public static String DEPOLOGY_MEDIUM_INPUT_CLUSTER_INFO2;
	public static String DEPOLOGY_LARGE_INPUT_CLUSTER_INFO;
	public static String DEPOLOGY_LARGE_INPUT_CLUSTER_INFO2;
	public static String DEPOLOGY_INPUT_CLUSTER_NOTE;
	
	public static String DEPOLOGY_SPECIFY_CLUSTER_NAME;
	
	public static String DEPOLOGY_CHOOSE_EXISTING_CLUSTER;
	
	public static String DEPOLOGY_CLUSTER_NAME_INFO_SMALL;
	
	public static String DEPOLOGY_EXISTING_NODES;
	
	public static String DEPOLOGY_NO_CLUSTER_ERROR;

	
	public static String DEPOLOGY_CLUSTER_TITLE;
	
	public static String DEPOLOGY_INPUT_SPECIFY_CLUSTER_NAME;
	
	public static String DEPOLOGY_INPUT_CHOOSE_CLUSTER;
	
	public static String DEPOLOGY_CHOOSE_NODE_INFO;
	
	public static String DEPOLOGY_MEDIUM_INPUT_CLUSTER_INFO_CONSOLE;
	
	public static String DEPOLOGY_LARGE_INPUT_CLUSTER_INFO_CONSOLE;
	public static String DEPOLOGY_CLUSTER;
	
	public static String DEPOLOGY_NODE_NAME_CONSOLE;
	
	public static String DEPOLOGY_SERVERMEMBER_NAME_CONSOLE;
	public static String DEPOLOGY_NODE_SELECTION;
	
	public static String DEPOLOGY_NODE_SELECTION_CONSOLE;
	public static String DEPOLOGY_NODE_SELECTION_INFO;

	public static String DEPOLOGY_CHECK_BTN_INFO;

	public static String DEPOLOGY_CLUSTER_TABLE_CLUSTER2;
	public static String DEPOLOGY_CLUSTER_TABLE_SERVERS2;
	
	public static String DEPOLOGY_ENTER_SERVER_MEMBER;
	public static String DEPOLOGY_EXPAND_ALL_BTN;
	public static String DEPOLOGY_COLLAPSE_ALL_BTN;

	public static String DEPOLOGY_CLUSTER_TABLE_FEATURE;
	public static String DEPOLOGY_CLUSTER_TABLE_CLUSTER;
	public static String DEPOLOGY_CLUSTER_TABLE_NODES;
	public static String DEPOLOGY_CLUSTER_TABLE_SERVERS;

	public static String DEPOLOGY_CLUSTER_TABLE_NODES_BTN;
	public static String DEPOLOGY_RESET_BTN;

	// for depology panel warning

	public static String DEPOLOGY_CLUSTER_INPUT_WARNING;
	public static String DEPOLOGY_NODE_SELECTION_ERROR;
	public static String DEPOLOGY_CLUSTER_INPUT_WARNING_MSG;
	public static String DEPOLOGY_CLUSTER_INPUT_NO_VALID;
	public static String DEPOLOGY_SERVER_INPUT_NO_VALID;
	public static String DEPOLOGY_SERVER_INPUT_WARNING;
	public static String DEPOLOGY_NODE_INPUT_WARNING_MSG;

	// for database panel
	public static String DB_TYPE_SELECT;
	public static String DB_DATABASE;
	public static String DB_SELECTION;
	public static String DB_SELECTION_INFO;
	public static String DB_RADIO_SAME;
	public static String DB_RADIO_DIFF;

	public static String DB_TYPE;
	public static String DB_TYPE_INFO;

	public static String DB_TYPE_DB2;
	public static String DB_TYPE_Oracle;
	public static String DB_TYPE_SQL_Server;
	public static String DB_TYPE_DB2_ISERIES; // OS400_Enablement

	public static String DB_BTN_Browse;
	public static String DB_SELECT_DRIVER_LIB;
	public static String DB_CHECK_BTN_SAME;

	public static String DB_PROPERTIES;
	public static String DB_HOST_NAME;

	public static String DB_PORT;
	public static String DB_DRIVER_LIB;
	// OS400_Enablement  A String to indicate Metrics on IBM i uses DB2 on AIX where cognas located.  
	public static String DB_DRIVER_LIB_ISERIES_METRICS; 
	public static String DB_FEATURE_INFO;
	public static String DB_TABLE_FEATURE;
	public static String DB_TABLE_DB_NAME;
	public static String DB_TABLE_DB_HOST;
	public static String DB_TABLE_USER_ID;
	public static String DB_TABLE_PWD;

	
	public static String DB_TABLE_CONFIRM;
	// for databse panel warning
	public static String DB_JDBCDriver_EMPTY;
	public static String DB_LOAD_DRIVER_ERROR;
	public static String DB_UNKNOW_DATABASE;
	public static String DB_UNKNOW_DATABASE_NEW;
	public static String DB_USERNAME_CHARS_INVALID;
	public static String DB_PWD_CHARS_INVALID;

	
	public static String DB_HOSTNAME_INVALID_CONSOLE;
	
	public static String DB_NAME_INVALID_CONSOLE;
	
	public static String DB_PORT_INVALID_CONSOLE;
	public static String DB_START_VALID;
	public static String DB_VALID;
	
	public static String DB_NEXT_OR_REVALIDATE;
	
	public static String DB_decide_to_go_back;

	public static String DB_fail_jdbcDriver;
	public static String DB_fail_authentication;
	public static String DB_fail_databaseNotExist;
	public static String DB_fail_communicationError;
	public static String DB_fail_launchValidation;
	public static String DB_PASS;
	public static String SET_CORRECT_VALUE_CCM;
	
	
	//for Web Server panel
	public static String WEB_SERVER;
	public static String WEB_SERVER_NAME;
	public static String WEB_SERVER_SELECTION;
	public static String WEB_SERVER_TITLE;
	public static String WEB_SERVER_DO_LATER;
	public static String WEB_SERVER_DO_NOW;
	public static String WEB_SERVER_EXISTED_SELECTION;
	public static String NO_WEB_SERVER_DETECTED;
	public static String WEB_SERVER_INFOMATION_TITLE;
	public static String WEB_SERVER_INFOMATION;
	
	//for User Mapping panel
	public static String USER_MAPPING;
	public static String USER_MAPPING_TITLE;
	public static String USER_MAPPING_ADMINISTRATIVE_USERS_NAME;
	public static String USER_MAPPING_ADMINISTRATIVE_USERS_DESC;
	public static String USER_MAPPING_GLOBAL_MODERATOR_USERS_NAME;
	public static String USER_MAPPING_GLOBAL_MODERATOR_USERS_DESC;
	public static String USER_MAPPING_BUTTON_ADD_USER;
	public static String USER_MAPPING_BUTTON_DO_LATER;
	
	//for CCM panel
	public static String CCM_DES;
	public static String CCM_PROMPT_PART_1;
	public static String CCM_PROMPT_PART_2;
	public static String CCM_PROMPT_PART_3;
	public static String CCM_PROMPT_PART_4;
	public static String CCM_PROMPT_DOCUMENTATION;
	public static String CCM_OPTION_DES;
	public static String CCM_OPTION_NEW_DEPLOYMENT;
	public static String CCM_OPTION_EXISTING_DEPLOYMENT;
	
	public static String CCM_ANONYMOUS_DES;
	public static String CCM_ANONYMOUS_DES_LABEL;
	public static String CCM_ANONYMOUS_USER_LABEL;
	public static String CCM_ANONYMOUS_PASSWORD_LABEL;
	public static String CCM_ADMIN_CANNOT_BE_ANON_USER;
	
	public static String CCM_EXISTING_DEPLOYMENT_Credential;
	public static String CCM_EXISTING_DEPLOYMENT_USER_ID;
	public static String CCM_EXISTING_DEPLOYMENT_PASSW0RD;
	public static String CCM_EXISTING_DEPLOYMENT;
	public static String CCM_EXISTING_DEPLOYMENT_URL;
	public static String CCM_EXISTING_DEPLOYMENT_EXAMPLE;
	public static String CCM_EXISTING_DEPLOYMENT_URL_HTTPS;
	public static String CCM_EXISTING_DEPLOYMENT_HTTPS_EXAMPLE;
	public static String CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG;
	public static String CCM_EXISTING_DEPLOYMENT__SERVER_HTTPS_CONNECTION_FAIL_ERROR_MSG;
	public static String CCM_EXISTING_DEPLOYMENT__HTTP_UNAUTHORIZED_ERROR_MSG;
	public static String CCM_EXISTING_DEPLOYMENT__HTTP_FORBIDDEN_ERROR_MSG;
	public static String CCM_EXISTING_DEPLOYMENT__HTTP_RESPONSE_ERROR_MSG;
	public static String CCM_EXISTING_DEPLOYMENT__HTTPS_RESPONSE_ERROR_MSG;
	public static String CCM_EXISTING_DEPLOYMENT_VALIDATE_DES;
	public static String CCM_EXISTING_DEPLOYMENT__SERVER_CONNECTION_INVALID_HTTP_URL_MSG;
	public static String CCM_EXISTING_DEPLOYMENT__SERVER_CONNECTION_INVALID_HTTPS_URL_MSG;
	public static String CCM_EXISTING_DEPLOYMENT__SERVER_VERSION_ERROR_MSG;
	public static String CCM_NEXT_OR_REVALIDATE;
	public static String CCM_NEW_DEPLOYMENT_INSTALLERS_LOCATION;
	public static String CCM_NEW_DEPLOYMENT_NOTE;
	public static String FAIL_TO_FIND_CE;
	public static String FAIL_TO_FIND_CE_FP;
	public static String FAIL_TO_FIND_FNCS;
	public static String FAIL_TO_FIND_FNCS_FP;
	public static String FAIL_TO_FIND_CE_CLIENT;
	public static String CCM_NEW_DEPLOYMENT_VALIDATE_DES;
	public static String NONE_INSTALLERS_FOUND;
	public static String CCM_NEW_DEPLOYMENT_MODIFY_NOTE;
	public static String CCM_NEW_DEPLOYMENT_POST_INSTALL_NOTE;
	public static String CCM_EXISTING_DEPLOYMENT_SKIP_TOPOLOGY_DATABASE_PANEL;
	public static String CCM_EXISTING_DEPLOYMENT_SKIP_DATABASE_PANEL;
	public static String INSUFFICIENT_TMP_DISK_SPACE;
	public static String UNABLE_TO_ACCESS_TMP_DIR;
	public static String CCM_BASE_CPE;
	public static String CCM_CPE_FIXPACK;
	public static String CCM_CPE_FIXPACK_CLIENT;
	public static String CCM_BASE_ICN;
	public static String CCM_ICN_FIXPACK;
	public static String CCM_DOCUMENTATION_URL;
	public static String CCM_INSTALLERS;
	
	// for content store panel
	public static String CONTENT_STORE;
	public static String SHARED_CONTENT_STORE;
	public static String SHARED_CONTENT_STORE_LOC;
	public static String LOCAL_CONTENT_STORE;
	public static String LOCAL_CONTENT_STORE_LOC;
	
	public static String CONTENT_STORE_NEXT_OR_REVALIDATE;
	
	// for Cognos Configuration panel
	public static String COGNOS_SET_TITLE;
	public static String COGNOS_SET_NOW;
	public static String COGNOS_SET_LATER;
	public static String COGNOS_CONFIGURATION;
	public static String COGNOS_ADMIN_USER;
	public static String COGNOS_ADMIN_PASSWORD;
	public static String COGNOS_CONTEXT_ROOT;
	public static String COGNOS_INFORMATION; 
	public static String COGNOS_INFORMATION_DES;
	public static String COGNOS_EAR_CONFIGURATION;
	public static String COGNOS_EAR_NODE_SELECTION;
	public static String COGNOS_EAR_CONFIG_DES;
	public static String COGNOS_EAR_NODE_SELECTION_DES;
	public static String COGNOS_EAR_NODE_SELECTION_INPUT_DESC;
	public static String COGNOS_EAR_NODE_SELECTION_HOSTNAME;
	public static String COGNOS_EAR_NODE_SELECTION_NODENAME;
	public static String COGNOS_EAR_NODE_SELECTION_SERVERNAME;
	public static String COGNOS;
	public static String COGNOS_LOAD_NODE_INFO;
	public static String COGNOS_DO_LATER_INFO;
	
	public static String COGNOS_NEXT_OR_REVALIDATE;
	public static String COGNOS_PORT;

	// for files viewer
	public static String FileViewerPanelName;
	public static String FILE_VIEWER_INFORMATION;
	public static String FILE_VIEWER_DO_LATER_INFO;
	public static String FILE_VIEWER_NEXT_OR_REVALIDATE;
	public static String FILE_VIEWER_SET_TITLE;
	
	// for content store panel warning
	public static String VALIDATE_BTN_WARNING;

	// for validate path
	public static String PATH_NOT_VALIDATE;
	public static String PATH_NOT_WRITABLE;
	public static String PATH_CONTAIN_INVALID_CHA;
	public static String PATH_VALID;

	// for notification panel

	public static String Notification_TITLE;
	public static String Notification_DES;
	public static String Notification_CHOOSE_INFO;
	public static String Notification_MAIL_INFO;
	public static String Notification_ENABLE_NOTIFICATION_ONLY;
	public static String Notification_ENABLE_NOTIFICATION_AND_REPLYTO;
	public static String Notification_WAS_JAVA_MAIL;
	public static String Notification_DNS_INFO;
	public static String Notification_NOT_ENABLED;
	public static String Notification_JAVA_MAIL_TITLE;
	public static String Notification_JAVA_MAIL_INFO;
	public static String Notification_SMTP_HOST_NAME;
	public static String Notification_SMTP_SPECIFY_DNS_SERVER_INFO;
	public static String Notification_SMTP_AUTH_INFO;
	public static String Notification_SMTP_USER_ID;

	public static String Notification_SMTP_PASSWORD;
	public static String Notification_SMTP_ENCRYPT;
	public static String Notification_SMTP_PORT_INFO;
	public static String Notification_DNS_MX;
	public static String Notification_DNS_MSG;
	public static String Notification_DNS_DOMAIN_NAME;
	public static String Notification_DNS_SERVER;
	public static String Notification_DNS_PORT;

	public static String Notification_REPLYTO_TITLE;
	public static String Notificaton_REPLYTO_DOMAIN_NAME;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_TITLE;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_DES;
	public static String Notification_REPLYTO_NONE;
	public static String Notification_REPLYTO_Prefix;
	public static String Notification_REPLYTO_Suffix;
	public static String Notification_REPLYTO_NONE_LABEL;
	public static String Notification_REPLYTO_Prefix_LABEL;
	public static String Notification_REPLYTO_Suffix_LABEL;
	public static String Notification_REPLYTO_MAIL_FILE_TITLE;
	public static String Notification_REPLYTO_MAIL_FILE_DES;
	public static String Notification_REPLYTO_MAIL_FILE_SERVER;
	public static String Notification_REPLYTO_MAIL_FILE_USER_ID;
	public static String Notification_REPLYTO_MAIL_FILE_PW;
	public static String Notification_REPLYTO_EMPTY_DOMAIN_NAME;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_DOMAIN_INPUT_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_PREFIX_INPUT_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_SUFFIX_INPUT_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_DOMAIN_NAME_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_SERVER_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_USER_ID_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_PW_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_PREFIX_INPUT_WARNING;
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_SUFFIX_INPUT_WARNING;
	public static String Notificaton_JAVAEMAIL_INVALID_SMTP_HOSTNAME_INPUT_WARNING;
	public static String Notificaton_JAVAEMAIL_INVALID_SMTP_USERID_INPUT_WARNING;
	public static String Notificaton_JAVAEMAIL_INVALID_SMTP_PW_INPUT_WARNING;
	public static String Notificaton_JAVAEMAIL_INVALID_SMTP_PORT_INPUT_WARNING;
	public static String Notificaton_DNS_INVALID_HOSTNAME_INPUT_WARNING;
	public static String Notificaton_DNS_INVALID_PORT_INPUT_WARNING;
	public static String Notificaton_DNS_INVALID_SMTP_USERID_INPUT_WARNING;
	public static String Notificaton_DNS_INVALID_SMTP_PW_INPUT_WARNING;
	public static String Notificaton_DNS_INVALID_SMTP_PORT_INPUT_WARNING;

	
	public static String Notificaton_REPLYTO_EMAIL_ADDRESS_TOO_LONG_WARNING;
	// for notification panel warning

	// for result panel
	public static String RESULT_install_STEP;
	public static String RESULT_utilityJar_STEP;
	public static String RESULT_ceBasicConfig_STEP;
	public static String RESULT_install_successfully;
	public static String RESULT_install;
	public static String RESULT_no_log_file;
	public static String RESULT_empty_log_file;
	public static String RESULT_install_failure;

	public static String RESULT_ceBasicConfig_logchecker_STEP;
	public static String RESULT_modify;
	public static String RESULT_uninstall;
	public static String RESULT_ceuninstall_STEP;
	public static String RESULT_ceunregistry_STEP;
	public static String RESULT_ceuninstall_logchecker_STEP;
	public static String RESULT_removeCluster_STEP;
	public static String RESULT_uninstall_successfully;

	public static String RESULT_update;
	public static String RESULT_updateEAR_STEP;
	public static String RESULT_updateEAR_logchecker_STEP;
	public static String RESULT_update_successfully;
	public static String RESULT_rollback;
	public static String RESULT_rollbackEAR_STEP;
	public static String RESULT_rollbackEAR_logchecker_STEP;
	public static String RESULT_rollback_successfully;

	// warning
	public static String warning_message;
	public static String warning_serverConnect; // =Couldn't connect the server
	public static String warning_serverUnknow; // Couldn't reach the Host :
												// Unknow Host
	public static String warning_serverTimeout; // Couldn't reach the Host :
												// Time Out
	public static String warning_port_empty; // port number is required
	public static String warning_port_invalid; // port number is invalid
	public static String warning_host_empty; // Host is required
	public static String warning_host_invalid; // Host Name is invalid
	public static String warning_invalid_user_pwd; // Invalid username or
													// password
	public static String warning_cellname_empty; // cellname is required
	public static String warning_nodename_empty; // nodename is required
	public static String warning_user_empty; // user ID is required
	public static String warning_database_name_empty; // database name is required
	
	public static String warning_not_use_p_as_input;// invalid input 'P'
	public static String warning_password_empty; // password is required
	public static String warning_pwdconfirm_empty; // password confirm is
													// required
	public static String warning_context_root; // context root is required
	public static String warning_password_unmatch; // passwords do not match
	public static String warning_dmhost_empty; // D
	public static String warning_port_not_open; // =Error opening socket on port
												// {0}
	public static String warning_ip_host; // =Use fully qualified host names
	public static String warning_password_invalid_chars;
	
	public static String warning_password_invalid;
	public static String warning_baseDN_empty; // base DN is required
	public static String warning_baseDN_not_found; // =Couldn't find the baseDN
	public static String warning_bindDN_empty; // bind DN is required
	public static String warning_bindpwd_empty; // bindpassword is required
	public static String warning_displayname_empty; // displayname is required
	public static String warning_loginfield_empty; // loginfield is required
	public static String warning_loginfield_baseDN; // =Can't get loginfield in
													// the baseDN
	public static String warning_field_empty; // Field Value is require
	public static String warning_field_invalid; // Field Value is invalid
	public static String warning_field_invalid_space; // Field Value has invalid
														// Space
	public static String warning_field_invalid_char; // Field Value contains
														// invalid CharString
	public static String warning_ldapconnect_fail; // Failed to connect LDAP
	public static String warning_required_mail; // =mail is required at the
												// beginning of Login Field

	public static String warning_dbName_empty; // DBname is required
	public static String warning_dbName_invalid; // databasename must be a valid
													// one
	public static String warning_dbName_not_found; // =An attempt was made to
													// access a database, {0},
													// which was not found
	public static String warning_app_userid_empty; // Application userid is
													// required
	public static String warning_app_pwd_empty; // Application password is
												// required
	public static String warning_dblogin_fail; // could not login to databse
	public static String warning_dbconnect_fail; // could not connect to databse
	public static String warning_dbhostname_fail; // DB HOST NAME ERROR : cannot
													// be resolved
	public static String warning_driveload_fail; // Error loading driver class:
	public static String warning_host_toolong;

	public static String warning_input_invalid;

	// error
	public static String exception; // =Exception:
	public static String error; // =Error:
	public static String error_connect_timeout; // =connect timed out

	public static String LC_DB_TYPE;
	public static String LC_DB_INFO_MSG;
	public static String LC_DB_TYPE_INFO;

	public static String INFORMATION_DIALOG;
	public static String BUTTON_MSG_OK;
	public static String VALIDATION_SUCCESSFUL;
	public static String WAS_NO_SYSTEM_APP;
	public static String WAS_NO_OAUTH_PROVIDER_EAR;
	
	public static String WAS_DETECT_PORT_ERROR;

	public static String CCM_32BIT_RUNTIME_SUPPORT_FAIL;
	
	public static String COGNOS_SERVER_EMPTY_ERROR_MSG;
	
	public static String COGNOS_NODE_WITHOUT_SERVER_ERROR_MSG;
	public static String COGNOS_NODE_DUPLICATE_ERROR_MSG;
	public static String COGNOS_NODE_SELECTION_ERROR_MSG;
	public static String COGNOS_SERVER_SELECTION_ERROR_MSG;
	public static String COGNOS_SERVER_CONNECTION_FAIL_ERROR_MSG;
}
