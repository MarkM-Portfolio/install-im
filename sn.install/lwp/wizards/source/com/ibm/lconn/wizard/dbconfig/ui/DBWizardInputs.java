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
package com.ibm.lconn.wizard.dbconfig.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.IWizard;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class DBWizardInputs {
	public static int panelHeight = 0;
	public static boolean fake = false;
	private static boolean jdbcNeed = false;

	public static boolean isJdbcNeeded(IWizard wizard) {
		return jdbcNeed;
	}

	public static void setJdbcNeeded(IWizard wizard, boolean val) {
		jdbcNeed = val;
	}	
	
	public static String getForumContentStorePath(IWizard wizard) {
		String path = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_CONTENT_STORE_MIGRATE,
				Constants.INPUT_FORUM_CONTENT_STORE);
		return path;
	}

	public static void setForumContentStorePath(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_CONTENT_STORE_MIGRATE,
				Constants.INPUT_FORUM_CONTENT_STORE, value);
	}

	public static String getDbAdminPassword(IWizard wizard) {
		String dbAdminPwd = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_PASSWORD);
		return dbAdminPwd;
	}

	public static void setDbAdminPassword(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_PASSWORD, value);
	}

	public static String getDbHostName(IWizard wizard) {
		String hostName = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_HOSTNAME);
		return hostName;
	}

	public static void setDbHostName(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_HOSTNAME, value);
	}

	public static String getDbPort(IWizard wizard) {
		String port = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_PORT);
		return port;
	}

	public static void setDbPort(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_PORT, value);
	}

	public static String getJDBCDbName(IWizard wizard) {
		String dbName = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_DBNAME);
		return dbName;
	}

	public static void setJDBCDbName(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_DBNAME, value);
	}

	public static String getJDBCLibPath(IWizard wizard) {
		String jdbcLibPath = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_LIB_PATH);
		return jdbcLibPath;
	}

	public static void setJDBCLibPath(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_LIB_PATH, value);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getDbUpgradeVersion(IWizard wizard) {
		return (Map<String, String>) CommonHelper.getObject(
				Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.VARIABLE_DB_UPGRADE_VERSION);
	}

	public static void setDbUpgradeVersion(IWizard wizard, Map<String, String> value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.VARIABLE_DB_UPGRADE_VERSION, value);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getDbUserPassword(IWizard wizard) {
		return (Map<String, String>) CommonHelper.getObject(
				Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_PASSWORD);
	}

	public static void setDbUserPassword(IWizard wizard,
			Map<String, String> value) {
		CommonHelper.setObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_PASSWORD, value);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getDbUserPasswordConfirm(IWizard wizard) {
		return (Map<String, String>) CommonHelper.getObject(
				Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_PASSWORD_CONFIRM);
	}

	public static void setDbUserPasswordConfirm(IWizard wizard,
			Map<String, String> value) {
		CommonHelper.setObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_PASSWORD_CONFIRM, value);
	}

	@SuppressWarnings("unchecked")
	public static String getSameDbUserPassword(IWizard wizard) {
		return (String) CommonHelper.getObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_SAME_PASSWORD);
	}

	public static void setSameDbUserPassword(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_SAME_PASSWORD, value);
	}

	@SuppressWarnings("unchecked")
	public static String getSameDbUserPasswordConfirm(IWizard wizard) {
		return (String) CommonHelper.getObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_SAME_PASSWORD_CONFIRM);
	}

	public static void setSameDbUserPasswordConfirm(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USER_SAME_PASSWORD_CONFIRM, value);
	}

	@SuppressWarnings("unchecked")
	public static boolean isDbUserPasswordSame(IWizard wizard) {
		return ((Boolean) CommonHelper.getObject(
				Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USE_SAME_PASSWORD)).booleanValue();
	}

	public static void setDbUserPasswordSame(IWizard wizard, boolean value) {
		CommonHelper.setObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USE_SAME_PASSWORD, Boolean.valueOf(value));
	}

	@SuppressWarnings("unchecked")
	public static boolean isCreateDbFilePathSame(IWizard wizard) {
		return ((Boolean) CommonHelper.getObject(
				Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USE_SAME_PATH)).booleanValue();
	}

	public static void setCreateDbFilePathSame(IWizard wizard, boolean value) {
		CommonHelper.setObject(Constants.SECTOR_PASSWORD_STORE,
				Constants.INPUT_DB_USE_SAME_PATH, Boolean.valueOf(value));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getCreateDbFilePath(IWizard wizard) {
		return (Map<String, String>) CommonHelper.getObject(
				Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_FILE_PATH);
	}

	public static void setCreateDbFilePath(IWizard wizard,
			Map<String, String> value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_FILE_PATH, value);
	}

	@SuppressWarnings("unchecked")
	public static String getSameCreateDbFilePath(IWizard wizard) {
		return (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_SAME_FILE_PATH);
	}

	public static void setSameCreateDbFilePath(IWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_SAME_FILE_PATH, value);
	}

	public static String getDbUserName(IWizard wizard) {
		String dbUserName = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_USER_NAME);
		return dbUserName;
	}

	public static void setDbUserName(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_USER_NAME, value);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getFeatures(IWizard wizard) {
		return (List<String>) CommonHelper.getObject(
				Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_SELECTED_FEATURE);
	}

	public static void setFeatures(IWizard wizard, List<String> list) {
		//**************jia**************
		if (list.contains(Constants.FEATURE_DOGEAR)) {
			list.remove(Constants.FEATURE_DOGEAR);
			list.add("bookmarks");
		}
		Collections.sort(list);
		int tempIndex = -1;
		if ((tempIndex = list.indexOf("bookmarks")) != -1) {
			list.set(tempIndex, Constants.FEATURE_DOGEAR);
		}
		CommonHelper.setObject(Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_DB_SELECTED_FEATURE, list);
	}

	public static String getDbAdminName(IWizard wizard) {
		String dbAdminName = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_NAME);
		return dbAdminName;
	}

	public static void setDbAdminName(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_NAME, value);
	}

	public static String getDbInstanceName(IWizard wizard) {
		String dbInstanceName = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_INSTANCE_NAME);
		return dbInstanceName;
	}

	public static void setDbInstanceName(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_INSTANCE_NAME, value);
	}

//#Oracle12C_PDB_disable#	 BEGIN
/*
	public static String getDbaUserName(IWizard wizard) {
		String dbaUserName = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_DBAUSER_NAME);
		return dbaUserName;
	}

	public static void setDbaUserName(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_DBAUSER_NAME, value);
	}
	
	
	public static String getPDBNameValue(IWizard wizard) {
		String PDBNameValue = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_PDB_NAME);
		return PDBNameValue;
	}
	
	public static void setPDBNameValue(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_PDB_NAME, value);
	}
	
	public static String getDbaPasswordValue(IWizard wizard) {
		String dbaPasswordValue = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_SYSDBA_PASSWORD);
		return dbaPasswordValue;
	}
	
	public static void setDbaPasswordValue(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_SYSDBA_PASSWORD, value);
	}
*/	
//#Oracle12C_PDB_disable#  END
	public static String getDbInstallDir(IWizard wizard) {
		String dbInstallDir = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_INSTALL_DIR);
		return dbInstallDir;
	}

	public static void setDbInstallDir(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_INSTALL_DIR, value);
	}

	public static String getDbType(IWizard wizard) {
		String dbTypeId = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_TYPE);
		return dbTypeId;
	}

	public static void setDbType(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_TYPE, value);
	}
	public static String getDbVersion(IWizard wizard) {
		String dbTypeId = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_VERSION);
		return dbTypeId;
	}
	public static void setDbVersion(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_VERSION, value);
	}

	public static String getActionId(IWizard wizard) {
		String actionId = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_DB_ACTION_TYPE,
				Constants.INPUT_DB_ACTION_NAME);
		return actionId;
	}

	public static void setActionId(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard, Constants.WIZARD_PAGE_DB_ACTION_TYPE,
				Constants.INPUT_DB_ACTION_NAME, value);
	}

	public static String getEnableCommunitiesOptionalTask(IWizard wizard) {
		String enableTask = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_ENABLE_COMMUNITIES_OPTIONAL_TASK,
				Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK);
		return enableTask;
	}

	public static void setEnableCommunitiesOptionalTask(IWizard wizard,
			String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_ENABLE_COMMUNITIES_OPTIONAL_TASK,
				Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK, value);
	}
	
	public static void setNannyMode(IWizard wizard, boolean value){
		CommonHelper.setObject(Constants.WIZARD_PAGE_DB_SUMMARY,
				Constants.INPUT_DB_NANNY_MODE, Boolean.valueOf(value));
	}
	
    public static boolean isNannyMode(IWizard wizard){
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_DB_SUMMARY,
				Constants.INPUT_DB_NANNY_MODE)).booleanValue();
	}
    
    
	public static void setExportOnly(IWizard wizard, boolean value){
		CommonHelper.setObject(Constants.WIZARD_PAGE_DB_ACTION_TYPE,
				Constants.INPUT_DB_EXPORT_ONLY, Boolean.valueOf(value));
	}
	
    public static boolean isExportOnly(IWizard wizard){
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_DB_ACTION_TYPE,
				Constants.INPUT_DB_EXPORT_ONLY)).booleanValue();
	}
 //#Oracle12C_PDB_disable#   BEGIN
 /*
	public static void setRunAsSYSDBA(IWizard wizard, boolean value){
		CommonHelper.setObject(Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_AS_SYSDBA, Boolean.valueOf(value));
	}
	
    public static boolean isRunAsSYSDBA(IWizard wizard){
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_AS_SYSDBA)).booleanValue();
	}
    
	public static void setPDBNameSelection(IWizard wizard, boolean value){
		CommonHelper.setObject(Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_USE_PDB, Boolean.valueOf(value));
	}
	
    public static boolean isUsePDBName(IWizard wizard){
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_USE_PDB)).booleanValue();
	}
    
	public static void setPDBNameTextEnabled(IWizard wizard, boolean value){
		CommonHelper.setObject(Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_PDBTEXT_ENABLE, Boolean.valueOf(value));
	}
	
    public static boolean isPDBNameTextEnabled(IWizard wizard){
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_DB_TYPE_SELECTION,
				Constants.INPUT_DB_PDBTEXT_ENABLE)).booleanValue();
	}
*/	
//#Oracle12C_PDB_disable# END    
    public static void setFeaturesSelectAll(IWizard wizard, boolean value){
		CommonHelper.setObject(Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_FEATURES_SELECT_ALL, Boolean.valueOf(value));
	}
    public static boolean getFeaturesSelectAll(IWizard wizard){
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_FEATURE_SELECTION,
				Constants.INPUT_FEATURES_SELECT_ALL)).booleanValue();
	}
    public static void setCommandCount(IWizard wizard, Integer commandCount){
		CommonHelper.setObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_COMMAND_COUNT , commandCount);
	}
	
    public static Integer getCommandCount(IWizard wizard){
    	return (Integer)CommonHelper.getObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_COMMAND_COUNT );
	}
    
    public static void setCommandTmp(IWizard wizard, List<String> commandTmp){
		CommonHelper.setObject(Constants.WIZARD_PAGE_EXECUTION_NOTE,
				Constants.VARIABLE_COMMAND_TMP , commandTmp);
	}
	
    public static List<String> getCommandTmp(IWizard wizard){
    	return (List<String>)CommonHelper.getObject(Constants.WIZARD_PAGE_EXECUTION_NOTE,
				Constants.VARIABLE_COMMAND_TMP );
	}
    
    public static void setFeatureNameTmp(IWizard wizard, String featureNameTmp){
		CommonHelper.setVariable(wizard, Constants.WIZARD_PAGE_EXECUTION_NOTE,
				Constants.VARIABLE_FEATURE_NAME_TMP, featureNameTmp);
	}
	
    public static String getFeatureNameTmp(IWizard wizard){
    	return CommonHelper.getVariable(wizard, Constants.WIZARD_PAGE_EXECUTION_NOTE,
				Constants.VARIABLE_FEATURE_NAME_TMP);
	}
    
    public static void setFeatureCount(IWizard wizard, Integer featureCount){
		CommonHelper.setObject(Constants.WIZARD_PAGE_EXECUTION_NOTE,
				Constants.VARIABLE_FEATURE_COUNT , featureCount);
	}
	
    public static Integer getFeatureCount(IWizard wizard){
    	return (Integer)CommonHelper.getObject(Constants.WIZARD_PAGE_EXECUTION_NOTE,
				Constants.VARIABLE_FEATURE_COUNT );
	}
    
    public static void setFeaturesCommandsMap(IWizard wizard, Map<String, List<List<String>>> commandTmp){
		CommonHelper.setObject(Constants.WIZARD_PAGE_DB_SUMMARY,
				Constants.VARIABLE_FEATURE_COMMAND_MAP , commandTmp);
	}
	
    public static Map<String, List<List<String>>> getFeaturesCommandsMap(IWizard wizard){
    	return (Map<String, List<List<String>>>)CommonHelper.getObject(Constants.WIZARD_PAGE_DB_SUMMARY,
				Constants.VARIABLE_FEATURE_COMMAND_MAP );
	}
    
    public static void setFeaturesResultsMap(IWizard wizard, Map<String, CommandResultInfo> resultTmp){
		CommonHelper.setObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_FEATURE_RESULT_MAP , resultTmp);
	}
	
    public static Map<String, CommandResultInfo> getFeaturesResultsMap(IWizard wizard){
    	return (Map<String, CommandResultInfo>)CommonHelper.getObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_FEATURE_RESULT_MAP );
	}
    
    public static void setSQLScriptsMap(IWizard wizard, Map<String, List<String>> sqlScriptTmp){
		CommonHelper.setObject(Constants.WIZARD_PAGE_DB_SUMMARY,
				Constants.VARIABLE_SQL_SCRIPT_MAP , sqlScriptTmp);
	}
	
    public static Map<String, List<String>> getSQLScriptsMap(IWizard wizard){
    	return (Map<String, List<String>>)CommonHelper.getObject(Constants.WIZARD_PAGE_DB_SUMMARY,
				Constants.VARIABLE_SQL_SCRIPT_MAP );
	}
    
    public static void setExecutePageEnteredFlag(IWizard wizard, boolean value){
		CommonHelper.setObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_EXECUTE_PAGE_ENTERED_FLAG, Boolean.valueOf(value));
	}
	
    public static boolean isExecutePageEnteredFlag(IWizard wizard){
    	if(null == CommonHelper.getObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_EXECUTE_PAGE_ENTERED_FLAG)){
    		setExecutePageEnteredFlag(wizard, false);
		}
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_EXECUTE_PAGE_ENTERED_FLAG)).booleanValue();
	}

	public static void setProfileDbHostName(DBWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_HOSTNAME, value);
		
	}
	
	public static String getProfileDbHostName(IWizard wizard){
		String hostName = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_HOSTNAME);
		return hostName;
	}

	public static void setProfileDbPort(DBWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_PORT, value);
		
	}
	
	public static String getProfileDbPort(IWizard wizard){
		String port = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_PORT);
		return port;
	}

	public static void setProfileJDBCDbName(DBWizard wizard, String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_DBNAME, value);
		
	}
	
	public static String getProfileJDBCDbName(IWizard wizard){
		String dbName = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_DBNAME);
		return dbName;
	}

	public static void setProfileJDBCLibPath(DBWizard wizard,
			String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_LIB_PATH, value);
		
	}

	public static String getProfileJDBCLibPath(IWizard wizard){
		String jdbcLibPath = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_JDBC_LIB_PATH);
		return jdbcLibPath;
	}
	
	public static void setProfileDbAdminName(DBWizard wizard, String value) {
		CommonHelper.setVariable(wizard,
				Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_NAME, value);
		
	}
	
	public static String getProfileDbAdminName(IWizard wizard){
		String dbAdminName = CommonHelper.getVariable(wizard,
				Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_NAME);
		return dbAdminName;
	}

	public static void setProfileDbAdminPassword(DBWizard wizard,
			String value) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_PASSWORD, value);
		
	}
    
	public static String getProfileDbAdminPassword(IWizard wizard){
		String dbAdminPwd = (String) CommonHelper.getObject(
				Constants.WIZARD_PAGE_PROFILE_CONNECTION_INFO,
				Constants.INPUT_DB_ADMIN_PASSWORD);
		return dbAdminPwd;
	}
    
}
