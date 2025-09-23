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

public class FillEmployeeTypeTask extends Task {
	private Pattern[] error = {
			Pattern.compile(".*exception:.*", Pattern.CASE_INSENSITIVE),
			Pattern.compile("Populate of Employee Type table failed",
					Pattern.CASE_INSENSITIVE) };

	@Override
	public CommandResultInfo run() {
		String command;
		CommandLogger.resetTempLog();
		CommandLogger.setLogPath(this.logPath);
		int commandResult = 0;
		
		if (Constants.OS_WINDOWS.equals(this.osType)) {
			command = WORKDIR + FS + "fill_emp_type.bat";
			CommandLogger.log("Execute [" + command + "]");
			commandResult = CommandExecuter.run(command, null, this.env, this.logPath);
		} else {
			CommandExecuter.run(new String[] { "chmod", "+x",
					WORKDIR + FS + "fill_emp_type.sh" }, null, this.env,
					this.logPath);
			command = WORKDIR + FS + "fill_emp_type.sh";
			CommandLogger.log("Execute [" + command + "]");
			commandResult = CommandExecuter.run(command, null, this.env, this.logPath);
		}
		
		return TDILogAnalysis.analyze(commandResult, null, error, null);
	}
}
