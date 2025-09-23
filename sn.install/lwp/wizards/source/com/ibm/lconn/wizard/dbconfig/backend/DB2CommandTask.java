/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
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
import com.ibm.lconn.wizard.common.command.CommandLogger;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.command.LogAnalysis;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class DB2CommandTask extends Task {
	private static final Logger logger = LogUtil
			.getLogger(DB2CommandTask.class);

	protected CommandResultInfo getResult() {
		if (this.exitValue == Task.JAVA_ERROR || this.exitValue == Task.EXCEPTION) {
			CommandResultInfo cri = new CommandResultInfo();
			cri.setExecState(CommandResultInfo.COMMAND_ERROR);
			return cri;
		}

		Pattern[] success = null;
		Pattern[] warnings = null; /*new Pattern[] { Pattern.compile(
				".*sql[0-9]{4,5}w.*", Pattern.CASE_INSENSITIVE) };*/

		Pattern[] errors = new Pattern[] {
				Pattern.compile(".*Start Exception.*"),
				Pattern.compile(".*sql[0-9]{4,5}(n|c).*",
						Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*db[0-9]{4,5}e.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile("exception in thread.*",
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\[BLOGS\\] Migration failed, data may be inconsistent\\."),
				Pattern.compile("\\[Bookmarks\\]Migration failed, data may be inconsistent\\.")};
		Pattern[] ignore = new Pattern[] {
				Pattern.compile(".*SQL1363W.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL1482W.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL0061W.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL0100W.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL0091N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL4091N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL0440N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL0139W.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL3104N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL3105N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL3109N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL3110N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL3149N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL3150N.*", Pattern.CASE_INSENSITIVE),
				Pattern.compile(".*SQL3153N.*", Pattern.CASE_INSENSITIVE)
				};

		return LogAnalysis.analyze(2, warnings, errors, success, ignore);
	}

	protected void combineCMD() {
		this.applicationType = Task.COMMAND_SCRIPT;
		if (null != dbInstanceName && !"".equals(dbInstanceName)) {
			this.evironment.put("DB2INSTANCE", this.dbInstanceName);
		}
		List<String> output = null;
		if (script.startsWith("$")) {
			// run command
			String[] cmds = script.split(" ");
			if (cmds[0].equals("$db2move")) {
				cmds[0] = this.dbDirRoot + "bin" + Constants.FS + "db2move";
				if (CommonHelper.getPlatformType().equals(Constants.OS_WINDOWS)) {
					cmds[0] = cmds[0] + ".exe";
				}

				this.command = new ArrayList<String>();
				for (int i = 0; i < cmds.length; i++) {
					this.command.add(cmds[i]);
				}
				this.workDirectory = System.getProperty("java.io.tmpdir");
			} else if (cmds[0].equals(JAVA_CMD)) {
				this.applicationType = Task.JAVA_APPLICATION;
				output = combineJdbcCmd(cmds, this.featureName);
				this.workDirectory = System.getProperty("user.dir");
			}
		} else {
			// run SQL script
			this.command = new ArrayList<String>();
			if (this.osName.equalsIgnoreCase(Constants.OS_WINDOWS)) {
				// For Profiles, working directory is always $DB2_HOME$/bnd
				if (this.featureName
						.equalsIgnoreCase(Constants.FEATURE_PROFILES)) {
					this.workDirectory = this.dbDirRoot + "/bnd";
				}
				this.command.add(new StringBuffer().append(this.dbDirRoot)
						.append(FS).append("bin").append(FS).append("db2cmd")
						.toString());
				this.command.add("-c");
				this.command.add("-w");
				this.command.add("-i");
				this.command.add("db2");
			} else {
				// For Profiles, working directory is always $DB2_HOME$/bnd
				if (this.featureName
						.equalsIgnoreCase(Constants.FEATURE_PROFILES)) {
					this.workDirectory = this.dbDirRoot + "/bnd";
				}
				this.command.add(new StringBuffer().append(this.dbDirRoot)
						.append(FS).append("bin").append(FS).append("db2")
						.toString());

			}

			if (this.script.startsWith("@")) {
				this.command.add("-td@");
				this.command.add("-vf");				
				this.script = this.script.substring(1);
			} else {
				this.command.add("-tvf");
			}

			String scriptPath;
			if (script.startsWith(JAVA_CMD) && script.indexOf("/") != -1) {
				scriptPath = new File(script).getPath();
			} else {
				// For Profiles, always use absolute path to SQL scripts
				if (Constants.FEATURE_PROFILES.equals(this.featureName))
					scriptPath = new File(this.scriptPath+this.script).getAbsolutePath();
				else
					scriptPath = new File(this.scriptPath+this.script).getPath();
			}

			if (this.osName.equalsIgnoreCase(Constants.OS_WINDOWS)) {
				scriptPath = scriptPath.replaceFirst(" ", "\\\\\" \"");
			}

			command.add(scriptPath);
		}
		// log the command line.
		if (!script.startsWith(JAVA_CMD)) {
			output = this.command;
		}
		String commandLog = "";
		int size = output.size();
		for (int i = 0; i < size; i++) {
			String parameter = output.get(i);
			commandLog += parameter + " ";
		}
		logger.log(Level.INFO, "common.info.command.print", "Execute ["
				+ commandLog + "]");
//		CommandLogger.log("Execute [" + commandLog + "]");
	}
}
