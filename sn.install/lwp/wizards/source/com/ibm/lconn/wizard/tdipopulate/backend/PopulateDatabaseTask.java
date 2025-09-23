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
package com.ibm.lconn.wizard.tdipopulate.backend;

import java.util.regex.Pattern;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandExecuter;
import com.ibm.lconn.wizard.common.command.CommandLogger;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.command.TDILogAnalysis;

public class PopulateDatabaseTask extends Task {
	private Pattern[] error = {
			Pattern.compile(".*exception:.*", Pattern.CASE_INSENSITIVE),
			Pattern.compile("Collection of LDAP DNs failed",
					Pattern.CASE_INSENSITIVE),
			Pattern.compile("Populate of database repository failed",
					Pattern.CASE_INSENSITIVE) };
	private Pattern[] success = {
			Pattern.compile(".*CLFRN0027I.*", Pattern.CASE_INSENSITIVE),
			};

	@Override
	public CommandResultInfo run() {
		CommandLogger.resetTempLog();
		CommandLogger.setLogPath(this.logPath);
		String command;
		int commandResult = 0;
		String popLogPath = WORKDIR + FS + "logs" + FS + "PopulateDBFromDNFile.log";
		if (Constants.OS_WINDOWS.equals(this.osType)) {
			command = WORKDIR + FS + "collect_dns.bat";			
			CommandLogger.log("Execute [" + command + "]");
			commandResult = CommandExecuter.run(command, null, this.env, this.logPath);
			command = WORKDIR + FS + "populate_from_dn_file.bat";
			CommandLogger.log("Execute [" + command + "]");
			commandResult = CommandExecuter.run(command, null, this.env, this.logPath);
		} else {
			CommandExecuter.run(new String[] { "chmod", "+x",
					WORKDIR + FS + "collect_dns.sh" }, null, this.env,
					this.logPath);
			command = WORKDIR + FS + "collect_dns.sh";
			CommandLogger.log("Execute [" + command + "]");
			commandResult = CommandExecuter.run(command, null, this.env, this.logPath);
			CommandExecuter.run(new String[] { "chmod", "+x",
					WORKDIR + FS + "populate_from_dn_file.sh" }, null,
					this.env, this.logPath);
			command = WORKDIR + FS + "populate_from_dn_file.sh";
			CommandLogger.log("Execute [" + command + "]");
			commandResult = CommandExecuter.run(command, null, this.env, this.logPath);
		}		
		return TDILogAnalysis.analyze(popLogPath, commandResult, null, error, success);
	}
}
