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
package com.ibm.lconn.wizard.dbconfig.interfaces;

import java.util.List;
import java.util.Map;

import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.tdipopulate.backend.JDBCConnectionInfo;

/**
 * @author Peng Zhang (pengzsh@cn.ibm.com)
 * 
 */
public interface DbCreationInterface {
	
	public String createLogForTask();
	public String createLogForTask(String featureName, String sqlFileName);
	/**
	 * @param dbType
	 * @param dbDirRoot
	 * @param dbInstanceName
	 * @param PDBNameValue
	 * @param dbaPasswordValue
     * @param dbaUserNameValue
	 * @param isRunAsSYSDBA
	 * @param dbLoginUser
	 * @param dbLoginPassword
	 * @param dbUser
	 * @param dbUserPassword
	 * @param dbFilePath
	 * @param osName
	 * @param featureName
	 * @param dbHostName
	 * @param isLocal
	 * @param operationType
	 * @return null if the creation succeeds, else return the error message.
	 * 
	 */
	public CommandResultInfo execute(String dbType, String dbVersion, String dbDirRoot,
//#Oracle12C_PDB_disable#			String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, Map<String, String> dbUserPasswords,
			String dbInstanceName, Map<String, String> dbUserPasswords,		
			Map<String, String> dbFilePaths, String osName,
			List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo);
	
	public CommandResultInfo execute(String dbType, String dbVersion, String dbDirRoot,
//#Oracle12C_PDB_disable#			String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, Map<String, String> dbUserPasswords,
			String dbInstanceName,Map<String, String> dbUserPasswords,
			Map<String, String> dbFilePaths, String osName,
			String feature, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, List<String> command, List<String> features, JDBCConnectionInfo profileConnInfo);
	
	public void setProperties(String name, String value);
	
	public Map<String,List<String>> getExecuteSQLScripts(String dbType, String dbVersion, String dbDirRoot,
//#Oracle12C_PDB_disable#			String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, Map<String, String> dbUserPasswords,
			String dbInstanceName,Map<String, String> dbUserPasswords,
			Map<String, String> dbFilePaths, String osName,
			List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo,JDBCConnectionInfo profileConnInfo);
	
	public Map<String,List<List<String>>> getExecuteCommands(String dbType, String dbVersion, String dbDirRoot,
//#Oracle12C_PDB_disable#			String dbInstanceName,String PDBNameValue,String dbaPasswordValue,String dbaUserNameValue, boolean isRunAsSYSDBA, Map<String, String> dbUserPasswords,	
			String dbInstanceName,Map<String, String> dbUserPasswords,
			Map<String, String> dbFilePaths, String osName,
			List<String> features, Map<String, String> versions, String operationType, JDBCConnectionInfo connInfo, JDBCConnectionInfo profileConnInfo);
}
