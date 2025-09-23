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
package com.ibm.lconn.wizard.dbconfig.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
//import java.util.UUID;
import java.util.Random;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandLogger;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.command.LogAnalysis;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.DbTypeSelectionWizardPage;

public class OracleCommandTask extends Task {
	private static final Logger logger = LogUtil
			.getLogger(OracleCommandTask.class);

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
				Pattern.compile("ORA-\\d{5}.*",Pattern.CASE_INSENSITIVE),
				Pattern.compile("SP2-\\d{4}.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile("exception in thread.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\[BLOGS\\] Migration failed, data may be inconsistent\\."),
				Pattern.compile("\\[Bookmarks\\]Migration failed, data may be inconsistent\\.")};

		return LogAnalysis.analyze(2, warnings, errors, success);
	}

	protected void combineCMD() {
		this.applicationType = Task.COMMAND_SCRIPT;
		this.evironment.put("ORACLE_SID", this.dbInstanceName);
		this.evironment.put("ORACLE_HOME", this.dbDirRoot);

		String commandLog = "";
		this.command = new ArrayList<String>();
		List<String> output = null;
		
		if (script.startsWith("$")) {
			String[] cmds = script.split(" ");
			if(cmds[0].equals(JAVA_CMD)) {
				this.applicationType = Task.JAVA_APPLICATION;
				output = combineJdbcCmd(cmds, this.featureName);
			}
			int size = output.size();
			for (int i = 0; i < size; i++) {
				String parameter = output.get(i);
				commandLog += parameter + " ";
			}
		} else {
		
			commandLog += this.dbDirRoot + "/bin/sqlplus";
			this.command.add(this.dbDirRoot + "/bin/sqlplus");
			commandLog += " -L";
			command.add("-L");
			if ("".equals(dbInstanceName) || null == dbInstanceName || isLocal) {
				commandLog += " / as sysdba";
				command.add("/ as sysdba");
			} else {
				// if (null == dbLoginUser || "".equals(dbLoginUser)) {
				commandLog += " /@" + dbInstanceName + " as sysdba";
				command.add("/@" + dbInstanceName + " as sysdba");
			}
		
//#Oracle12C_PDB_disable#			commandLog += this.dbDirRoot + "/bin/sqlplus";
//#Oracle12C_PDB_disable#			this.command.add(this.dbDirRoot + "/bin/sqlplus");
//#Oracle12C_PDB_disable#			commandLog += " -L";
//#Oracle12C_PDB_disable#			command.add("-L");
			
//#Oracle12C_PDB_disable#			commandLog += " SYS/******@PDBNameValue";
//#Oracle12C_PDB_disable#			if (CommonHelper.isEmpty(PDBNameValue)){
//#Oracle12C_PDB_disable#				command.add("SYS" + "/" + dbaPasswordValue + "@" + dbInstanceName + " as sysdba");
//#Oracle12C_PDB_disable#			}else{
//#Oracle12C_PDB_disable#				command.add("SYS" + "/" + dbaPasswordValue + "@" + PDBNameValue + " as sysdba");
//#Oracle12C_PDB_disable#			}  
			

			commandLog += " @" + new File(this.scriptPath+this.script).getPath();
			this.command.add("@" + new File(this.scriptPath+this.script).getPath());
		
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(this.operationType) || this.script.startsWith("upgrade25.sql") || this.script.startsWith("upgrade25a.sql")) {
				commandLog += " ******";
				this.command.add(this.dbUserPassword);
			}
			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.operationType) && Constants.FEATURE_COMMUNITIES.equals((this.featureName)) 
					&& this.script.indexOf("calendar")!=-1 && this.script.indexOf("createDb")!=-1) {
				commandLog += " ******";
				this.command.add(System.getProperty("communities_upgradedb_password"));
			}	
			
			//add password for push notification upgrade to generate a random password automatically, modified on 18th July, 2014 by Dong Lin
			//if( Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.operationType) && Constants.FEATURE_FILES.equals(this.featureName)  )
			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.operationType) && Constants.FEATURE_FILES.equals(this.featureName)
					&& this.script.indexOf("pns")!=-1 && this.script.indexOf("createDb")!=-1) {
				String pwd = getRandomString(16);
				//String pwd = "`~!@#$%^&*-_5kPs";
				commandLog += " ******";
				this.command.add(pwd);
			}
			//end
			
			
		}
		// log the command line.
		logger.log(Level.INFO, "common.info.command.print", "Execute ["
				+ commandLog + "]");
//		CommandLogger.log("Execute [" + commandLog + "]");
	}
	
	
	private String getRandomString(int length) {
		//String uuid = UUID.randomUUID().toString().substring(0,8);   
	    //return uuid;
	    //String base = "abcdefghijklmnopqrstuvwxyz0123456789";
	    String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789`~!@#$%^&*-_";
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString(); 
	 } 
}
