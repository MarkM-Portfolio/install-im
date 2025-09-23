/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.dbconfig.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.command.LogAnalysis;
import com.ibm.lconn.wizard.common.logging.LogUtil;

public class SQLServerCommandTask extends Task {
	private static final Logger logger = LogUtil
			.getLogger(SQLServerCommandTask.class);

	protected CommandResultInfo getResult() {
		if (this.exitValue == Task.JAVA_ERROR || this.exitValue == Task.EXCEPTION) {
			CommandResultInfo cri = new CommandResultInfo();
			cri.setExecState(CommandResultInfo.COMMAND_ERROR);
			return cri; 
		}
		
		Pattern[] success = null;
		Pattern[] warnings = null;
		Pattern[] errors = new Pattern[] {
				Pattern.compile(".*Start Exception.*"),
				Pattern.compile(".*", Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\[BLOGS\\] Migration failed, data may be inconsistent\\."),
				Pattern.compile("\\[Bookmarks\\]Migration failed, data may be inconsistent\\.")};
		return LogAnalysis.analyze(this.exitValue, warnings, errors, success);
	}

	protected void combineCMD() {
		this.applicationType = Task.COMMAND_SCRIPT;
		String commandLog = "";
		this.command = new ArrayList<String>();
		List<String> output = null;
		if (script.startsWith("$")) {
			String[] cmds = script.split(" ");
			if(cmds[0].equals(JAVA_CMD)) {
				output = combineJdbcCmd(cmds, this.featureName);
			}
			int size = output.size();
			for (int i = 0; i < size; i++) {
				String parameter = output.get(i);
				commandLog += parameter + " ";
			}
		} else {
			if (this.osName.equalsIgnoreCase(Constants.OS_WINDOWS)) {
				commandLog += this.dbDirRoot + getExecutable(dbVersion);
				this.command.add(this.dbDirRoot + getExecutable(dbVersion));
				
				if(Constants.OPERATION_TYPE_CREATE_DB.equals(this.operationType)){
					this.command.add("-m");
					this.command.add("1");
				}
				
				// get error code
//				this.command.add("-b");
				this.command.add("-V");
				this.command.add("1");
				
				if (null != dbInstanceName && !"".equals(dbInstanceName)) {
					// always pass -S param no mater what version is used, if default instance, value is .\, if not default instance, value is .\instancename
					command.add("-S");
					
					if ("\\".equals(dbInstanceName.trim())) { // default instance
						commandLog += " -S .\\";
						command.add(".\\");
					}
					else {
						commandLog += " -S " + dbInstanceName;
						command.add(".\\"+dbInstanceName);
					}
				}
	
				/*
				 * if (!isLocal) { commandLog += " -U " + dbLoginUser + " -P
				 * ******"; command.add("-U"); command.add(dbLoginUser);
				 * command.add("-P"); command.add(dbLoginPassword); }
				 */
	
				commandLog += " -i "
						+ new File(this.scriptPath+this.script).getPath();
				this.command.add("-i");
				this.command.add(new File(this.scriptPath+this.script).getPath());
				if (Constants.OPERATION_TYPE_CREATE_DB.equals(this.operationType)) {
					commandLog += " -v " + " filepath=\"" + this.dbFilePath + "\""
							+ " password=\"******\"";
					this.command.add("-v");
					String[] filepath = ("filepath=\"" + this.dbFilePath.trim() + "\"")
							.split(" ");
					for (int i = 0; i < filepath.length; i++)
						this.command.add(filepath[i]);
					this.command.add("password=\"" + this.dbUserPassword + "\"");
				}
//				if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.operationType) && Constants.FEATURE_COMMUNITIES.equals((this.featureName)) 
//						&& this.script.indexOf("forum")!=-1 && this.script.indexOf("createDb")!=-1) {
//					
//					commandLog += " -v " + " filepath=\"" + this.dbFilePath + "\""
//						+ " password=\"******\"";
//					this.command.add("-v");
//					String[] filepath = ("filepath=\"" + this.dbFilePath.trim() + "\"").split(" ");
//					for (int i = 0; i < filepath.length; i++)
//					this.command.add(filepath[i]);
//					this.command.add("password=\"" + System.getProperty("communities_forum_createdb_password") + "\"");
//				}
				
				if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.operationType) && Constants.FEATURE_HOMEPAGE.equals(this.featureName)
						&& Constants.DB_SQLSERVER.equals(this.dbType)) {
					commandLog += " -v " + " password=\"******\"";
					this.command.add("-v");
					this.command.add("password=\"" + System.getProperty("homepage_upgradedb_password") + "\"");
				}
				
			}
		}
		// log the command line.
		logger.log(Level.INFO, "common.info.command.print", "Execute ["
				+ commandLog + "]");
//		CommandLogger.log("Execute [" + commandLog + "]");
	}
	private String getExecutable(String dbVersion){
		String path = "";
		if(dbVersion == null){
			return path;
		}
		if (dbVersion.startsWith("10")){
			path = "\\100\\tools\\binn\\sqlcmd";
		}else if(dbVersion.startsWith("11")){
			path = "\\110\\tools\\binn\\sqlcmd";		
		}else{
			path = "\\Client SDK\\ODBC\\130\\tools\\binn\\sqlcmd";
		}
		
		return path;
	}
}
