/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                            */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.dbconfig.interfaces.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.property.Property;
import com.ibm.lconn.wizard.common.property.PropertyLoader;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.DB2ConfigProp;
import com.ibm.lconn.wizard.dbconfig.backend.Checker;
import com.ibm.lconn.wizard.dbconfig.backend.Task;
import com.ibm.lconn.wizard.dbconfig.interfaces.DbCreationInterface;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.FinishWizardPage;
import com.ibm.lconn.wizard.tdipopulate.backend.JDBCConnectionInfo;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class DatabaseOperation implements DbCreationInterface {
	public static final Logger logger = LogUtil.getLogger(DatabaseOperation.class);

	public String createLogForTask() {
		return Task.openLog();
	}
	
	public String createLogForTask(String featureName, String sqlFileName){
		return Task.openLog(featureName, sqlFileName);
	}

	private Properties props = new Properties();

//#Oracle12C_PDB_disable#	public CommandResultInfo execute(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo) {
	public CommandResultInfo execute(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo) {	 
		dbDirRoot = new File(dbDirRoot).getAbsolutePath();

		if (dbFilePaths != null) {
			Set<String> keys = dbFilePaths.keySet();
			for (String key : keys) {
				String value = new File(dbFilePaths.get(key)).getAbsolutePath();
				dbFilePaths.put(key, value);
			}
		}

//#Oracle12C_PDB_disable#	generateResponseFile(dbType, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, dbFilePaths, features, operationType, connInfo, profileConnInfo);
        generateResponseFile(dbType, dbDirRoot, dbInstanceName,dbFilePaths, features, operationType, connInfo, profileConnInfo);
		if (Task.logFile == null) {
			Task.openLog();
		}

		CommandResultInfo result = new CommandResultInfo();
		try {
			StringBuffer message = new StringBuffer();
			String dbUserPassword = "";
			String dbFilePath = "";
			CommandResultInfo temp;

			int exitCode = CommandResultInfo.COMMAND_SUCCEED;

			for (String feature : features) {
				if (Constants.OPERATION_TYPE_CREATE_DB.equals(operationType) && !Constants.DB_DB2.equals(dbType)) {
					dbUserPassword = dbUserPasswords.get(feature);
					dbFilePath = "";
					if (dbType.equals(Constants.DB_SQLSERVER)) {
						dbFilePath = dbFilePaths.get(feature);
					}
				}
				if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType) && Constants.DB_ORACLE.equals(dbType)) {
					dbUserPassword = dbUserPasswords.get(feature);
				}

				FinishWizardPage.boldLines[FinishWizardPage.num] = FinishWizardPage.line;
				message.append(Messages.getString(feature + ".name"));
				FinishWizardPage.num++;

				message.append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
				message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1")).append(" ");
				if (Constants.DB_ORACLE.equals(dbType)) {
					message.append(dbInstanceName);
				} else {
					message.append(DB2ConfigProp.getProperty(feature + ".dbName." + dbType));
				}
				message.append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;

				message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a")).append(" ");
				message.append(DB2ConfigProp.getProperty(feature + ".dbUserName." + dbType)).append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;

				if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType)) {
					message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_b")).append(" ");
					String version = versions.get(feature);
					if (version.startsWith("1.0"))
						version = Constants.VERSION_10X;

					message.append(version).append("->").append(Constants.VERSION_TOP).append(Constants.UI_LINE_SEPARATOR);
					FinishWizardPage.line++;
				}

				if (dbType.equals(Constants.DB_SQLSERVER) && operationType.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
					message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3")).append(" ");
					message.append(dbFilePaths.get(feature)).append(Constants.UI_LINE_SEPARATOR);
					FinishWizardPage.line++;
				}
				message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part4")).append(" ");

				if (CommonHelper.equals(Constants.OPERATION_TYPE_CREATE_DB, operationType)) {
//#Oracle12C_PDB_disable#	temp = create(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, dbFilePath, osName, feature, connInfo);
					temp = create(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, feature, connInfo);					
					message.append(temp.getExitMessage());
					if (temp.getExecState() == CommandResultInfo.COMMAND_WARNING && exitCode == CommandResultInfo.COMMAND_SUCCEED)
						exitCode = CommandResultInfo.COMMAND_WARNING;

					if (temp.getExecState() == CommandResultInfo.COMMAND_ERROR && (exitCode == CommandResultInfo.COMMAND_SUCCEED || exitCode == CommandResultInfo.COMMAND_WARNING))
						exitCode = CommandResultInfo.COMMAND_ERROR;
				}

				if (CommonHelper.equals(Constants.OPERATION_TYPE_DROP_DB, operationType)) {
//#Oracle12C_PDB_disable#					temp = delete(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,osName, feature, connInfo);
					temp = delete(dbType, dbVersion, dbDirRoot, dbInstanceName,osName, feature, connInfo);
					message.append(temp.getExitMessage());

					if (temp.getExecState() == CommandResultInfo.COMMAND_WARNING && exitCode == CommandResultInfo.COMMAND_SUCCEED)
						exitCode = CommandResultInfo.COMMAND_WARNING;

					if (temp.getExecState() == CommandResultInfo.COMMAND_ERROR && (exitCode == CommandResultInfo.COMMAND_SUCCEED || exitCode == CommandResultInfo.COMMAND_WARNING))
						exitCode = CommandResultInfo.COMMAND_ERROR;
				}

				if (CommonHelper.equals(Constants.OPERATION_TYPE_UPGRADE_DB, operationType)) {
//#Oracle12C_PDB_disable#					temp = upgrade(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, profileConnInfo);
					temp = upgrade(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, profileConnInfo);
					message.append(temp.getExitMessage());
					if (temp.getExecState() == CommandResultInfo.COMMAND_WARNING && exitCode == CommandResultInfo.COMMAND_SUCCEED)
						exitCode = CommandResultInfo.COMMAND_WARNING;

					if (temp.getExecState() == CommandResultInfo.COMMAND_ERROR && (exitCode == CommandResultInfo.COMMAND_SUCCEED || exitCode == CommandResultInfo.COMMAND_WARNING))
						exitCode = CommandResultInfo.COMMAND_ERROR;
				}

				message.append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
				message.append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
			}
			String logPath = Task.logFile.getAbsolutePath();
			message.append(Messages.getString("dbWizard.finishWizardPage.content.view.log", logPath));

			result.setExitMessage(message.toString());
			result.setLogPath(logPath);
			result.setExecState(exitCode);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "dbconfig.severe.execute.task", e);
			result.setExitMessage(Messages.getString("dbWizard.backend.task.error.default"));

		}
		return result;
	}
	
//#Oracle12C_PDB_disable#	public CommandResultInfo execute(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, String feature, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, List<String> command, List<String> features, JDBCConnectionInfo profileConnInfo) {
	public CommandResultInfo execute(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, String feature, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, List<String> command, List<String> features, JDBCConnectionInfo profileConnInfo) {
		dbDirRoot = new File(dbDirRoot).getAbsolutePath();
	
		List<String> featuresTmp = new ArrayList<String>();
		for(String featureTmp : features){
			if(featureTmp.indexOf("theme") == -1){
				featuresTmp.add(featureTmp);
			}
		}
		
//#Oracle12C_PDB_disable#		generateResponseFile(dbType, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbFilePaths, featuresTmp, operationType, connInfo,dbVersion, profileConnInfo);
		generateResponseFile(dbType, dbDirRoot, dbInstanceName,dbFilePaths, featuresTmp, operationType, connInfo,dbVersion, profileConnInfo);		
		CommandResultInfo result = new CommandResultInfo();
		try {
			StringBuffer message = new StringBuffer();
			String dbUserPassword = "";
			String dbFilePath = "";
			CommandResultInfo temp;	
			int exitCode = CommandResultInfo.COMMAND_SUCCEED;
			
			if (CommonHelper.equals(Constants.OPERATION_TYPE_CREATE_DB, operationType)) {
//#Oracle12C_PDB_disable#				temp = create(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, dbFilePath, osName, feature, connInfo, command);
				temp = create(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, feature, connInfo, command);				
				message.append(temp.getExitMessage());
				if (temp.getExecState() == CommandResultInfo.COMMAND_WARNING && exitCode == CommandResultInfo.COMMAND_SUCCEED)
					exitCode = CommandResultInfo.COMMAND_WARNING;
				
				if (temp.getExecState() == CommandResultInfo.COMMAND_ERROR && (exitCode == CommandResultInfo.COMMAND_SUCCEED || exitCode == CommandResultInfo.COMMAND_WARNING))
					exitCode = CommandResultInfo.COMMAND_ERROR;
				
				
			}
			
			if (CommonHelper.equals(Constants.OPERATION_TYPE_DROP_DB, operationType)) {
//#Oracle12C_PDB_disable#				temp = delete(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,osName, feature, connInfo, command);
				temp = delete(dbType, dbVersion, dbDirRoot, dbInstanceName,osName, feature, connInfo, command);			
				message.append(temp.getExitMessage());
				if (temp.getExecState() == CommandResultInfo.COMMAND_WARNING && exitCode == CommandResultInfo.COMMAND_SUCCEED)
					exitCode = CommandResultInfo.COMMAND_WARNING;
				
				if (temp.getExecState() == CommandResultInfo.COMMAND_ERROR && (exitCode == CommandResultInfo.COMMAND_SUCCEED || exitCode == CommandResultInfo.COMMAND_WARNING))
					exitCode = CommandResultInfo.COMMAND_ERROR;
				
				
			}
			
			if (CommonHelper.equals(Constants.OPERATION_TYPE_UPGRADE_DB, operationType)) {
//#Oracle12C_PDB_disable#				temp = upgrade(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, command, profileConnInfo);
				temp = upgrade(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, command, profileConnInfo);				
				message.append(temp.getExitMessage());
				if (temp.getExecState() == CommandResultInfo.COMMAND_WARNING && exitCode == CommandResultInfo.COMMAND_SUCCEED)
					exitCode = CommandResultInfo.COMMAND_WARNING;
				
				if (temp.getExecState() == CommandResultInfo.COMMAND_ERROR && (exitCode == CommandResultInfo.COMMAND_SUCCEED || exitCode == CommandResultInfo.COMMAND_WARNING))
					exitCode = CommandResultInfo.COMMAND_ERROR;
				
				
			}
			
		
			String logPath = Task.logFile.getAbsolutePath();
			
			result.setExitMessage(message.toString());
			result.setLogPath(logPath);
			result.setExecState(exitCode);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "dbconfig.severe.execute.task", e);
			result.setExitMessage(Messages.getString("dbWizard.backend.task.error.default"));
			
		}
		return result;
	}

//#Oracle12C_PDB_disable#	public CommandResultInfo create(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo) {
	public CommandResultInfo create(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo) {
//#Oracle12C_PDB_disable#		CommandResultInfo result = getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, dbFilePath, osName, featureName, connInfo).run();
	    CommandResultInfo result = getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, featureName, connInfo).run();
//#Oracle12C_PDB_disable#		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,osName, featureName, Constants.OPERATION_TYPE_CREATE_DB);
		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, Constants.OPERATION_TYPE_CREATE_DB);
	}

//#Oracle12C_PDB_disable#	public CommandResultInfo upgrade(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo) {
	public CommandResultInfo upgrade(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo) {		
//#Oracle12C_PDB_disable#		CommandResultInfo result = getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,  dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).run();
		CommandResultInfo result = getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).run();
//#Oracle12C_PDB_disable#		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, osName, featureName, Constants.OPERATION_TYPE_UPGRADE_DB);
		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, Constants.OPERATION_TYPE_UPGRADE_DB);
	}

//#Oracle12C_PDB_disable#	public CommandResultInfo delete(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName, String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String osName, String featureName, JDBCConnectionInfo connInfo) {
	public CommandResultInfo delete(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName, String osName, String featureName, JDBCConnectionInfo connInfo) {
//#Oracle12C_PDB_disable#		CommandResultInfo result = getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,osName, featureName, connInfo).run();
		CommandResultInfo result = getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, connInfo).run();
//#Oracle12C_PDB_disable#		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,   osName, featureName, Constants.OPERATION_TYPE_DROP_DB);		
		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, Constants.OPERATION_TYPE_DROP_DB);
	}
	
//#Oracle12C_PDB_disable#	public CommandResultInfo create(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo, List<String> command) {
	public CommandResultInfo create(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo, List<String> command) {
//#Oracle12C_PDB_disable#		CommandResultInfo result = getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, dbFilePath, osName, featureName, connInfo).run(command);
		CommandResultInfo result = getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, featureName, connInfo).run(command);
//#Oracle12C_PDB_disable#		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,   osName, featureName, Constants.OPERATION_TYPE_CREATE_DB);
		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, Constants.OPERATION_TYPE_CREATE_DB);
	}

//#Oracle12C_PDB_disable#	public CommandResultInfo upgrade(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, List<String> command, JDBCConnectionInfo profileConnInfo) {
	public CommandResultInfo upgrade(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, List<String> command, JDBCConnectionInfo profileConnInfo) {		
//#Oracle12C_PDB_disable#		CommandResultInfo result = getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,  dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).run(command);
		CommandResultInfo result = getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).run(command);
//#Oracle12C_PDB_disable#		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,   osName, featureName, Constants.OPERATION_TYPE_UPGRADE_DB);
		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, Constants.OPERATION_TYPE_UPGRADE_DB);
	}

//#Oracle12C_PDB_disable#	public CommandResultInfo delete(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String osName, String featureName, JDBCConnectionInfo connInfo, List<String> command) {
	public CommandResultInfo delete(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String osName, String featureName, JDBCConnectionInfo connInfo, List<String> command) {
//#Oracle12C_PDB_disable#		CommandResultInfo result = getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, osName, featureName, connInfo).run(command);
		CommandResultInfo result = getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, connInfo).run(command);
//#Oracle12C_PDB_disable#		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,  PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,  osName, featureName, Constants.OPERATION_TYPE_DROP_DB);
		return getResult(result, dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, Constants.OPERATION_TYPE_DROP_DB);
	}
	
//#Oracle12C_PDB_disable#	public Map<String,List<List<String>>> getExecuteCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
	public Map<String,List<List<String>>> getExecuteCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){		
		Map<String,List<List<String>>> map = new HashMap<String,List<List<String>>>();
		
//		Checker dbCheck = Checker.getChecker(dbDirRoot, CommonHelper.getPlatformType(), dbType);
//		String versionInfo = dbCheck.getVersionInfo();
		
		if (dbFilePaths != null) {
			Set<String> keys = dbFilePaths.keySet();
			for (String key : keys) {
				String value = new File(dbFilePaths.get(key)).getAbsolutePath();
				dbFilePaths.put(key, value);
			}
		}
		
		String dbUserPassword = "";
		String dbFilePath = "";
		
		for (String feature : features) {
			
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(operationType) && !Constants.DB_DB2.equals(dbType)) {
				dbUserPassword = dbUserPasswords.get(feature);
				dbFilePath = "";
				if (dbType.equals(Constants.DB_SQLSERVER)) {
					dbFilePath = dbFilePaths.get(feature);
				}
			}
			
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType) && Constants.DB_SQLSERVER.equals(dbType) && null!= dbFilePaths) {
//				dbUserPassword = dbUserPasswords.get(feature);
//				dbFilePath = dbFilePaths.get(feature);
//			}
			
			if (CommonHelper.equals(Constants.OPERATION_TYPE_CREATE_DB, operationType)) {
//#Oracle12C_PDB_disable#			map.put(feature, getCreateCommands(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,   dbUserPassword, dbFilePath, osName, feature, connInfo));	
				map.put(feature, getCreateCommands(dbType, dbVersion, dbDirRoot, dbInstanceName, dbUserPassword, dbFilePath, osName, feature, connInfo));	
			}

			if (CommonHelper.equals(Constants.OPERATION_TYPE_DROP_DB, operationType)) {
//#Oracle12C_PDB_disable#				map.put(feature, getDeleteCommands(dbType, dbVersion, dbDirRoot, dbInstanceName,  PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,  osName, feature, connInfo));
		    	map.put(feature, getDeleteCommands(dbType, dbVersion, dbDirRoot, dbInstanceName, osName, feature, connInfo));
			}

			if (CommonHelper.equals(Constants.OPERATION_TYPE_UPGRADE_DB, operationType)) {
//#Oracle12C_PDB_disable#				map.put(feature, getUpgradeCommands(dbType,dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,   dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, profileConnInfo));
				map.put(feature, getUpgradeCommands(dbType,dbVersion, dbDirRoot, dbInstanceName, dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, profileConnInfo));
			}
		}
		
		return map;
	}

	
//#Oracle12C_PDB_disable#	public Map<String,List<String>> getExecuteSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
	public Map<String,List<String>> getExecuteSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,Map<String, String> dbUserPasswords, Map<String, String> dbFilePaths, String osName, List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
		
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		
//		Checker dbCheck = Checker.getChecker(dbDirRoot, CommonHelper.getPlatformType(), dbType);
//		String versionInfo = dbCheck.getVersionInfo();
		
		if (dbFilePaths != null) {
			Set<String> keys = dbFilePaths.keySet();
			for (String key : keys) {
				String value = new File(dbFilePaths.get(key)).getAbsolutePath();
				dbFilePaths.put(key, value);
			}
		}
		
		String dbUserPassword = "";
		String dbFilePath = "";
		
		for (String feature : features) {
			
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(operationType) && !Constants.DB_DB2.equals(dbType)) {
				dbUserPassword = dbUserPasswords.get(feature);
				dbFilePath = "";
				if (dbType.equals(Constants.DB_SQLSERVER)) {
					dbFilePath = dbFilePaths.get(feature);
				}
			}
			
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType) && Constants.DB_SQLSERVER.equals(dbType) && null != dbFilePaths) {
//				dbUserPassword = dbUserPasswords.get(feature);
//				dbFilePath = dbFilePaths.get(feature);
//			}
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType) && Constants.DB_ORACLE.equals(dbType)) {
//				dbUserPassword = dbUserPasswords.get(feature);
//			}
			
			if (CommonHelper.equals(Constants.OPERATION_TYPE_CREATE_DB, operationType)) {
//#Oracle12C_PDB_disable#				map.put(feature, getCreateSQLScripts(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,   dbUserPassword, dbFilePath, osName, feature, connInfo));	
				map.put(feature, getCreateSQLScripts(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, feature, connInfo));	
			}

			if (CommonHelper.equals(Constants.OPERATION_TYPE_DROP_DB, operationType)) {
//#Oracle12C_PDB_disable#				map.put(feature, getDeleteSQLScripts(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,  osName, feature, connInfo));
				map.put(feature, getDeleteSQLScripts(dbType, dbVersion, dbDirRoot, dbInstanceName, osName, feature, connInfo));
			}

			if (CommonHelper.equals(Constants.OPERATION_TYPE_UPGRADE_DB, operationType)) {
//#Oracle12C_PDB_disable#				map.put(feature, getUpgradeSQLScripts(dbType,dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,  dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, profileConnInfo));
				map.put(feature, getUpgradeSQLScripts(dbType,dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, feature, versions.get(feature), connInfo, profileConnInfo));
			}
		}
		
		return map;
	}
	
//#Oracle12C_PDB_disable#	public List<List<String>> getCreateCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo){
	public List<List<String>> getCreateCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo){
//#Oracle12C_PDB_disable#		return getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, dbFilePath, osName, featureName, connInfo).getCommands();		
		return getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, featureName, connInfo).getCommands();
	}
	
//#Oracle12C_PDB_disable#	public List<List<String>> getDeleteCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String osName, String featureName, JDBCConnectionInfo connInfo){
	public List<List<String>> getDeleteCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String osName, String featureName, JDBCConnectionInfo connInfo){
//#Oracle12C_PDB_disable#		return getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, osName, featureName, connInfo).getCommands();	
		return getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName,osName, featureName, connInfo).getCommands();
	}
	
//#Oracle12C_PDB_disable#	public List<List<String>> getUpgradeCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
	public List<List<String>> getUpgradeCommands(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
//#Oracle12C_PDB_disable#		return getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).getCommands();	
		return getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName, dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).getCommands();
	}
	
//#Oracle12C_PDB_disable#	public List<String> getCreateSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo){
	public List<String> getCreateSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo){
//#Oracle12C_PDB_disable#		return getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, dbFilePath, osName, featureName, connInfo).getSQLScripts();	
		return getCreateTask(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, featureName, connInfo).getSQLScripts();
	}
	
//#Oracle12C_PDB_disable#	public List<String> getDeleteSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String osName, String featureName, JDBCConnectionInfo connInfo){
	public List<String> getDeleteSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String osName, String featureName, JDBCConnectionInfo connInfo){	
//#Oracle12C_PDB_disable#		return getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,osName, featureName, connInfo).getSQLScripts();	
		return getDeleteTask(dbType, dbVersion, dbDirRoot, dbInstanceName, osName, featureName, connInfo).getSQLScripts();
	}
	
//#Oracle12C_PDB_disable#	public List<String> getUpgradeSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
	public List<String> getUpgradeSQLScripts(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){	
//#Oracle12C_PDB_disable#		return getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).getSQLScripts();	
		return getUpgradeTask(dbType, dbVersion, dbDirRoot, dbInstanceName,dbUserPassword, dbFilePath, osName, featureName, version, connInfo, profileConnInfo).getSQLScripts();
	}
	
//#Oracle12C_PDB_disable#	public Task getCreateTask(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName, String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo){
	public Task getCreateTask(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, JDBCConnectionInfo connInfo){		
		
		int taskType;
		if (Constants.DBMS_DB2.equals(dbType)) {
			taskType = Task.TYPE_DB2;
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			taskType = Task.TYPE_ORACLE;
		} else {
			taskType = Task.TYPE_SQLSERVER;
		}

		Task execute = Task.loadTask(taskType);
		execute.setDbDirRoot(dbDirRoot);
		execute.setDbVersion(dbVersion);
		execute.setDbFilePath(dbFilePath);
		execute.setDbInstanceName(dbInstanceName);
//#Oracle12C_PDB_disable#		execute.setPDBNameValue(PDBNameValue); 
//#Oracle12C_PDB_disable#		execute.setDbaPasswordValue(dbaPasswordValue); 
//#Oracle12C_PDB_disable#		execute.setDbaUserNameValue(dbaUserNameValue); 
//#Oracle12C_PDB_disable#		execute.isRunAsSYSDBA(isRunAsSYSDBA);  
		execute.setDbType(dbType);
		execute.setDbUserPassword(dbUserPassword);
		execute.setOsName(osName);
		execute.setFeatureName(featureName);
		execute.setOperationType(Constants.OPERATION_TYPE_CREATE_DB);
		// execute.setJdbcConnInfo(connInfo);
		if (Constants.DBMS_DB2.equals(dbType)) {
			if (null != dbInstanceName && !"".equals(dbInstanceName)) {
				execute.getEvironment().put("DB2INSTANCE", dbInstanceName);
			}
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			execute.getEvironment().put("ORACLE_SID", dbInstanceName);
			execute.getEvironment().put("ORACLE_HOME", dbDirRoot);
		} else {
			
		}
		return execute;
	}
	
//#Oracle12C_PDB_disable#	public Task getDeleteTask(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String osName, String featureName, JDBCConnectionInfo connInfo){
	public Task getDeleteTask(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String osName, String featureName, JDBCConnectionInfo connInfo){	
	
		int taskType;
		if (Constants.DBMS_DB2.equals(dbType)) {
			taskType = Task.TYPE_DB2;
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			taskType = Task.TYPE_ORACLE;
		} else {
			taskType = Task.TYPE_SQLSERVER;
		}

		Task execute = Task.loadTask(taskType);
		execute.setDbDirRoot(dbDirRoot);
		execute.setDbVersion(dbVersion);
		execute.setDbInstanceName(dbInstanceName);
//#Oracle12C_PDB_disable#		execute.setPDBNameValue(PDBNameValue); 
//#Oracle12C_PDB_disable#		execute.setDbaPasswordValue(dbaPasswordValue); 
//#Oracle12C_PDB_disable#		execute.setDbaUserNameValue(dbaUserNameValue); 
//#Oracle12C_PDB_disable#		execute.isRunAsSYSDBA(isRunAsSYSDBA);  
		execute.setDbType(dbType);
		execute.setOsName(osName);
		execute.setFeatureName(featureName);
		execute.setOperationType(Constants.OPERATION_TYPE_DROP_DB);
		// execute.setJdbcConnInfo(connInfo);
		if (Constants.DBMS_DB2.equals(dbType)) {
			if (null != dbInstanceName && !"".equals(dbInstanceName)) {
				execute.getEvironment().put("DB2INSTANCE", dbInstanceName);
			}
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			execute.getEvironment().put("ORACLE_SID", dbInstanceName);
			execute.getEvironment().put("ORACLE_HOME", dbDirRoot);
		} else {
			
		}
		return execute;
	}

//#Oracle12C_PDB_disable#	public Task getUpgradeTask(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo,JDBCConnectionInfo profileConnInfo){
	public Task getUpgradeTask(String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String dbUserPassword, String dbFilePath, String osName, String featureName, String version, JDBCConnectionInfo connInfo,JDBCConnectionInfo profileConnInfo){
	
		int taskType;
		if (Constants.DBMS_DB2.equals(dbType)) {
			taskType = Task.TYPE_DB2;
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			taskType = Task.TYPE_ORACLE;
		} else {
			taskType = Task.TYPE_SQLSERVER;
		}

		Task execute = Task.loadTask(taskType);
		execute.setDbDirRoot(dbDirRoot);
		execute.setDbVersion(dbVersion);
		execute.setDbInstanceName(dbInstanceName);
//#Oracle12C_PDB_disable#		execute.setPDBNameValue(PDBNameValue); 
//#Oracle12C_PDB_disable#		execute.setDbaPasswordValue(dbaPasswordValue); 
//#Oracle12C_PDB_disable#		execute.setDbaUserNameValue(dbaUserNameValue); 
//#Oracle12C_PDB_disable#		execute.isRunAsSYSDBA(isRunAsSYSDBA);  
		execute.setDbUserPassword(dbUserPassword);
		execute.setDbFilePath(dbFilePath);
		execute.setDbType(dbType);
		execute.setOsName(osName);
		execute.setFeatureName(featureName);
		execute.setOperationType(Constants.OPERATION_TYPE_UPGRADE_DB);
		execute.setFeatureVersion(version);
		execute.setJdbcConnInfo(connInfo);
		execute.setProfileJdbcConnInfo(profileConnInfo);
		execute.setProperties(props);
//		if(Constants.FEATURE_COMMUNITIES.equals(featureName)){
//			execute.setDbFilePath(System.getProperty("communities_forum_createdb_file_path"));
//		}
		if (Constants.DBMS_DB2.equals(dbType)) {
			if (null != dbInstanceName && !"".equals(dbInstanceName)) {
				execute.getEvironment().put("DB2INSTANCE", dbInstanceName);
			}
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			execute.getEvironment().put("ORACLE_SID", dbInstanceName);
			execute.getEvironment().put("ORACLE_HOME", dbDirRoot);
		} else {
			
		}
		
		return execute;
	}
	

//#Oracle12C_PDB_disable#	public void generateResponseFile(String dbType, String dbDirRoot,String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA,Map<String, String> dbFilePaths, List<String> features, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
	public void generateResponseFile(String dbType, String dbDirRoot,String dbInstanceName,Map<String, String> dbFilePaths, List<String> features, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo){
		try {

			final String response = Constants.RESPONSE_ROOT + Constants.FS + "dbWizard" + Constants.FS + "response.properties";
			File dir = new File(Constants.RESPONSE_ROOT + Constants.FS + "dbWizard");
			if (!dir.exists())
				dir.mkdirs();
			File file = new File(response);
			if (!file.exists())
				file.createNewFile();

			Property property = PropertyLoader.load(response);
			Map<String, String> map = property.getAllProperty();
			if (map == null)
				map = new HashMap<String, String>();
			// add by Jia, for user to add just library instead of library.gcd and library.os
			String tempString = features.toString().substring(1, features.toString().length() - 1).replace("library.gcd, library.os","library");
			map.put("features", tempString);
			map.put("action", operationType);
			map.put("dbtype", dbType);
			map.put("dbHome", dbDirRoot);
			map.put("dbInstance", dbInstanceName);
			
//#Oracle12C_PDB_disable#			if (Constants.DB_ORACLE.equals(dbType)) {					
//#Oracle12C_PDB_disable#				map.put("PDBNameValue",PDBNameValue);
//#Oracle12C_PDB_disable#				map.put("dbaPasswordValue","{PASSWORD_REMOVED}");
//#Oracle12C_PDB_disable#				map.put("dbaUserNameValue",dbaUserNameValue);
//#Oracle12C_PDB_disable#				map.put("isRunAsSYSDBA",new Boolean(isRunAsSYSDBA).toString());	
//#Oracle12C_PDB_disable#		}

			if (Constants.OPERATION_TYPE_CREATE_DB.equals(operationType)) {
				for (String feature : features) {
					if (Constants.DB_SQLSERVER.equals(dbType)) {
						String path = dbFilePaths.get(feature);
						// add by Jia, for user to add just library instead of library.gcd and library.os
						if (feature.equals("library.gcd") || feature.equals("library.os")) {
							map.put("library.filepath", path);
						} else {
							map.put(feature + ".filepath", path);
						}
					}
					// add by Jia, for user to add just library instead of library.gcd and library.os
					if (feature.equals("library.gcd") || feature.equals("library.os")) {
						map.put("library.password", "{PASSWORD_REMOVED}");
					} else {
						map.put(feature + ".password", "{PASSWORD_REMOVED}");
					}
				}
			}

			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType)) {
				if (features.contains(Constants.FEATURE_BLOGS) || features.contains(Constants.FEATURE_ACTIVITIES) || features.contains(Constants.FEATURE_DOGEAR)) {
					// save jdbc infomation
					map.put("port", connInfo.getPort());
					map.put("administrator", connInfo.getUsername());
					map.put("adminPassword", "{PASSWORD_REMOVED}");
					if (Constants.DB_SQLSERVER.equals(dbType)) {
						map.put("jdbcLibPath", connInfo.getJdbcLibPath());
					}
				}
				if (features.contains(Constants.FEATURE_HOMEPAGE)){
					map.put("port", connInfo.getPort());
					map.put("administrator", connInfo.getUsername());
					map.put("adminPassword", "{PASSWORD_REMOVED}");
					map.put("profiles.db.hostname", profileConnInfo.getHostName());
					map.put("profiles.db.name", profileConnInfo.getDbName());
					map.put("profiles.db.port", profileConnInfo.getPort());
					map.put("profiles.db.admin", profileConnInfo.getUsername());
					map.put("profiles.db.adminPassword", "{PASSWORD_REMOVED}");
					map.put("storyLifetimeInDays", System.getProperty("story_life_time_in_days"));
					if (Constants.DB_SQLSERVER.equals(dbType)) {
						map.put("jdbcLibPath", connInfo.getJdbcLibPath());
						map.put(Constants.FEATURE_HOMEPAGE + ".filepath", System.getProperty("homepage_upgradedb_file_path"));
						map.put(Constants.FEATURE_HOMEPAGE + ".password", "{PASSWORD_REMOVED}");
					}
				}
				/*
				 * if(features.contains(Constants.FEATURE_COMMUNITIES)) {
				 * map.put(Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK,
				 * props
				 * .getProperty(Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK
				 * )); }
				 */

//				if (Constants.DB_SQLSERVER.equals(dbType) && features.contains(Constants.FEATURE_COMMUNITIES)) {					
//					map.put(Constants.FEATURE_FORUM + ".filepath", System.getProperty("communities_forum_createdb_file_path"));
//					}
				if (Constants.DB_ORACLE.equals(dbType) && features.contains(Constants.FEATURE_COMMUNITIES)) {					
					map.put(Constants.FEATURE_COMMUNITIES + ".password", "{PASSWORD_REMOVED}");
			}

			}

			property.setProperty(map);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "dbconfig.severe.create.response", e);
		}
	}
	
//#Oracle12C_PDB_disable#	public void generateResponseFile(String dbType, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, Map<String, String> dbFilePaths, List<String> features, String operationType, JDBCConnectionInfo connInfo,String dbVersion, JDBCConnectionInfo profileConnInfo){
	public void generateResponseFile(String dbType, String dbDirRoot, String dbInstanceName, Map<String, String> dbFilePaths, List<String> features, String operationType, JDBCConnectionInfo connInfo,String dbVersion, JDBCConnectionInfo profileConnInfo){
		try {
			
			final String response = Constants.RESPONSE_ROOT + Constants.FS + "dbWizard" + Constants.FS + "response.properties";
			File dir = new File(Constants.RESPONSE_ROOT + Constants.FS + "dbWizard");
			if (!dir.exists())
				dir.mkdirs();
			File file = new File(response);
			if (!file.exists())
				file.createNewFile();

			Property property = PropertyLoader.load(response);
			Map<String, String> map = property.getAllProperty();
			if (map == null)
				map = new HashMap<String, String>();
			// add by Jia, for user to add just library instead of library.gcd and library.os
			String tempString = features.toString().substring(1, features.toString().length() - 1).replace("library.gcd, library.os","library");
			map.put("features", tempString);
			map.put("action", operationType);
			map.put("dbtype", dbType);
			map.put("dbHome", dbDirRoot);
			map.put("dbInstance", dbInstanceName);
			
//#Oracle12C_PDB_disable#			if (Constants.DB_ORACLE.equals(dbType)) {					
//#Oracle12C_PDB_disable#				map.put("PDBNameValue",PDBNameValue);
//#Oracle12C_PDB_disable#				map.put("dbaPasswordValue","{PASSWORD_REMOVED}");
//#Oracle12C_PDB_disable#				map.put("dbaUserNameValue",dbaUserNameValue);
//#Oracle12C_PDB_disable#				map.put("isRunAsSYSDBA",new Boolean(isRunAsSYSDBA).toString());	
//#Oracle12C_PDB_disable#		}

			if (Constants.OPERATION_TYPE_CREATE_DB.equals(operationType)) {
				for (String feature : features) {
					if (Constants.DB_SQLSERVER.equals(dbType)) {
						String path = dbFilePaths.get(feature);
						// add by Jia, for user to add just library instead of library.gcd and library.os
						if (feature.equals("library.gcd") || feature.equals("library.os")) {
							map.put("library.filepath", path);
						} else {
							map.put(feature + ".filepath", path);
						}
					}
					// add by Jia, for user to add just library instead of library.gcd and library.os
					if (feature.equals("library.gcd") || feature.equals("library.os")) {
						map.put("library.password", "{PASSWORD_REMOVED}");
					} else {
						map.put(feature + ".password", "{PASSWORD_REMOVED}");
					}
				}
			}

			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType)) {
				if (features.contains(Constants.FEATURE_BLOGS) || features.contains(Constants.FEATURE_ACTIVITIES) || features.contains(Constants.FEATURE_DOGEAR)) {
					// save jdbc infomation
					map.put("port", connInfo.getPort());
					map.put("administrator", connInfo.getUsername());
					map.put("adminPassword", "{PASSWORD_REMOVED}");
					if (Constants.DB_SQLSERVER.equals(dbType)) {
						map.put("jdbcLibPath", connInfo.getJdbcLibPath());
					}
//					if (features.contains(Constants.FEATURE_COMMUNITIES)) {
//						map.put("forum.contentStore", connInfo.getContentStore(Constants.FEATURE_COMMUNITIES));
//					}
				}
				
				if(features.contains(Constants.FEATURE_HOMEPAGE)){
					// save jdbc infomation
					map.put("port", connInfo.getPort());
					map.put("administrator", connInfo.getUsername());
					map.put("adminPassword", "{PASSWORD_REMOVED}");
					map.put("profiles.db.hostname", profileConnInfo.getHostName());
					map.put("profiles.db.name", profileConnInfo.getDbName());
					map.put("profiles.db.port", profileConnInfo.getPort());
					map.put("profiles.db.admin", profileConnInfo.getUsername());
					map.put("profiles.db.adminPassword", "{PASSWORD_REMOVED}");
					map.put("storyLifetimeInDays", System.getProperty("story_life_time_in_days"));
					if (Constants.DB_SQLSERVER.equals(dbType)) {
						map.put("jdbcLibPath", connInfo.getJdbcLibPath());
						map.put(Constants.FEATURE_HOMEPAGE + ".filepath", System.getProperty("homepage_upgradedb_file_path"));
						map.put(Constants.FEATURE_HOMEPAGE + ".password", "{PASSWORD_REMOVED}");
					}
				}
				
				/*
				 * if(features.contains(Constants.FEATURE_COMMUNITIES)) {
				 * map.put(Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK,
				 * props
				 * .getProperty(Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK
				 * )); }
				 */

//				if (Constants.DB_SQLSERVER.equals(dbType) && features.contains(Constants.FEATURE_COMMUNITIES)) {					
//					map.put(Constants.FEATURE_FORUM + ".filepath", System.getProperty("communities_forum_createdb_file_path"));
//				}
				if (Constants.DB_ORACLE.equals(dbType) && features.contains(Constants.FEATURE_COMMUNITIES)) {					
						map.put(Constants.FEATURE_COMMUNITIES + ".password", "{PASSWORD_REMOVED}");
				}

			}
			/*
			 * Fix SPR #JNGH8CK62Q Start
			 * Add "dbVersion=***" to the response file.
			 * @author Deliang (dljiang@cn.ibm.com)
			 */
			map.put("dbVersion", dbVersion);
			
			/*
			 * Fix SPR #JNGH8CK62Q End
			 */
			
			property.setProperty(map);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "dbconfig.severe.create.response", e);
		}
	}
	
	private boolean contain(String s, String[] arr) {
		if (arr == null)
			return false;

		for (String v : arr) {
			if (CommonHelper.equals(s, v))
				return true;
		}

		return false;
	}
	

	public void setProperties(String name, String value) {
		props.setProperty(name, value);
	}
	
//#Oracle12C_PDB_disable#	private CommandResultInfo getResult(CommandResultInfo result, String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, String osName, String feature, String operationType) {
	private CommandResultInfo getResult(CommandResultInfo result, String dbType, String dbVersion, String dbDirRoot, String dbInstanceName,String osName, String feature, String operationType) {
		String detail = null;
//#Oracle12C_PDB_disable#		Checker dbCheck = Checker.getChecker(dbDirRoot, osName, dbType,PDBNameValue,dbaPasswordValue);
        Checker dbCheck = Checker.getChecker(dbDirRoot, osName, dbType);
		dbCheck.setInstance(dbInstanceName);
		String[] installedFeatures = dbCheck.getFeatures(dbVersion);

		boolean pass = true;
		if (!contain(feature, installedFeatures) && CommonHelper.equals(operationType, Constants.OPERATION_TYPE_CREATE_DB)) {
			pass = false;
		}

		if (contain(feature, installedFeatures) && CommonHelper.equals(operationType, Constants.OPERATION_TYPE_DROP_DB)) {
			pass = false;
		}

		if (pass) {
			if (result.getExecState() == CommandResultInfo.COMMAND_SUCCEED) {
				detail = Messages.getString("dbWizard.backend.task.success." + operationType);
			} else if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
				detail = Messages.getString("dbWizard.backend.task.warn." + operationType);
			} else if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
				detail = Messages.getString("dbWizard.backend.task.error." + operationType);
			} else {
				detail = Messages.getString("dbWizard.backend.task.exception." + operationType);
			}
		} else {
			detail = Messages.getString("dbWizard.backend.task.error." + operationType);
		}

		result.setExitMessage(detail);
		return result;
	}
}
