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
package com.ibm.lconn.wizard.common.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.common.logging.LogUtil;

public class LogAnalysis {
	private static final Logger logger = LogUtil.getLogger(LogAnalysis.class);
	private static String messageLine;
			
	public static CommandResultInfo analyze(
			int commandExitValue, Pattern[] warnings, Pattern[] errors, Pattern[] success) {
		return analyze(CommandLogger.tempLog.getAbsolutePath(), commandExitValue, warnings,
				errors, success);
	}
	
	
	public static CommandResultInfo analyze(
			int commandExitValue, Pattern[] warnings, Pattern[] errors, Pattern[] success, Pattern[] ignore) {
		return analyze(CommandLogger.tempLog.getAbsolutePath(), commandExitValue, warnings,
				errors, success, ignore);
	}
	
	public static CommandResultInfo analyze(String logPath,
			int commandExitValue, Pattern[] warnings, Pattern[] errors, Pattern[] success) {
		CommandResultInfo cri = new CommandResultInfo();
		cri.setLogPath(logPath);

		if (commandExitValue == 0) {
			cri.setExecState(CommandResultInfo.COMMAND_SUCCEED);
			cri.setExitMessage("Successful.");
			return cri;
		}

		if (commandExitValue == -1) {
			cri.setExecState(CommandResultInfo.COMMAND_INTERRUPT);
			cri.setExitMessage("Exit with exception.");
			return cri;
		}
		
		if (containsOr(logPath, errors)) {
			cri.setExecState(CommandResultInfo.COMMAND_ERROR);
			cri.setExitMessage(messageLine);
			return cri;
		}

		if (containsOr(logPath, warnings)) {
			cri.setExecState(CommandResultInfo.COMMAND_WARNING);
			cri.setExitMessage(messageLine);
			return cri;
		}
		
		if (containsOr(logPath, success)) {
			cri.setExecState(CommandResultInfo.COMMAND_SUCCEED);
			cri.setExitMessage(messageLine);
			return cri;
		}

		cri.setExecState(CommandResultInfo.COMMAND_SUCCEED);
		cri.setExitMessage("Successful.");
		return cri;
	}
	
	public static CommandResultInfo analyze(String logPath,
			int commandExitValue, Pattern[] warnings, Pattern[] errors, Pattern[] success, Pattern[] ignore) {
		if (ignore == null) {
			return analyze(logPath, commandExitValue, warnings,
					errors, success);
		}
		
		CommandResultInfo cri = new CommandResultInfo();
		cri.setLogPath(logPath);

		if (commandExitValue == 0) {
			cri.setExecState(CommandResultInfo.COMMAND_SUCCEED);
			cri.setExitMessage("Successful.");
			return cri;
		}

		if (commandExitValue == -1) {
			cri.setExecState(CommandResultInfo.COMMAND_INTERRUPT);
			cri.setExitMessage("Exit with exception.");
			return cri;
		}
		
		if (containsOr(logPath, errors, ignore)) {
			cri.setExecState(CommandResultInfo.COMMAND_ERROR);
			cri.setExitMessage(messageLine);
			return cri;
		}

		if (containsOr(logPath, warnings, ignore)) {
			cri.setExecState(CommandResultInfo.COMMAND_WARNING);
			cri.setExitMessage(messageLine);
			return cri;
		}
		
		if (containsOr(logPath, success)) {
			cri.setExecState(CommandResultInfo.COMMAND_SUCCEED);
			cri.setExitMessage(messageLine);
			return cri;
		}

		cri.setExecState(CommandResultInfo.COMMAND_SUCCEED);
		cri.setExitMessage("Successful.");
		return cri;
	}

	public static CommandResultInfo analyze(File logFile, int commandExitValue,
			Pattern[] warnings, Pattern[] errors, Pattern[] success) {
		return analyze(logFile.getAbsolutePath(), commandExitValue, warnings,
				errors, success);
	}
	
	public static CommandResultInfo analyze(File logFile, int commandExitValue,
			Pattern[] warnings, Pattern[] errors, Pattern[] success, Pattern[] ignore) {
		return analyze(logFile.getAbsolutePath(), commandExitValue, warnings,
				errors, success, ignore);
	}

	public static boolean containsOr(String logPath, Pattern[] pattens) {
		if (pattens == null)
			return false;
		
		try {
			InputStreamReader read = new InputStreamReader (new FileInputStream(logPath),"UTF-8");
			BufferedReader br =new BufferedReader(read);
			
			String line;
			while ((line = br.readLine()) != null) {
				if (matches(line, pattens)) {
					br.close();
					messageLine = line;
					return true;
				}
			}
			br.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
		}
		
		return false;
	}
	
	public static boolean containsAnd(String logPath, Pattern[] patterns) {
		if (patterns == null)
			return false;
		
		int length = patterns.length;
		for (int i = 0; i < length; i++) {
			boolean eachContained = false;
			try {
				InputStreamReader read = new InputStreamReader (new FileInputStream(logPath),"UTF-8");
				BufferedReader br =new BufferedReader(read);
				
				String line;
				while ((line = br.readLine()) != null) {
					if (patterns[i].matcher(line).matches()) {
						eachContained = true;
						break;
					}
				}
				br.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "common.error.log.read", e);
			}
			if (eachContained) continue;
			else return false;
		}		
		return true;
	}
	
	private static boolean containsOr(String logPath, Pattern[] pattens, Pattern[] ignore) {
		if (pattens == null)
			return false;
		
		try {
			InputStreamReader read = new InputStreamReader (new FileInputStream(logPath),"UTF-8");
			BufferedReader br =new BufferedReader(read);
			String line;
			while ((line = br.readLine()) != null) {
				if (matches(line, pattens) && !matches(line, ignore)) {
					br.close();
					messageLine = line;
					return true;
				}
			}
			br.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
		}

		return false;
	}

	private static boolean matches(String sentence, Pattern[] patterns) {
		int length = patterns.length;
		for (int i = 0; i < length; i++) {
			if (patterns[i].matcher(sentence).matches())
				return true;
		}

		return false;
	}
}
