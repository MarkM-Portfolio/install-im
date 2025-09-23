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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

public class CommandLogger {
	private static Pattern[] remove;
	private static String[] replace;
	private static final Logger logger = LogUtil.getLogger(CommandLogger.class);
	private static final String FS = System.getProperty("file.separator");
	private static String logPath;
	public static final String tempLogPath = Constants.LOG_ROOT + FS + "temp.log";
	private static File logFile;
	public static File tempLog;
	public static boolean isOracle = false;

	public static void setLogPath(String path) {
		logFile = new File(path);
		setLogFile(logFile);
	}

	public static void setLogFile(File log) {

		logFile = log;

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		logPath = logFile.getAbsolutePath();
		logger.log(Level.INFO, "common.info.set.logFile", logPath);
	}

	public static void setLogPath(String path, boolean rewrite) {
		logFile = new File(path);
		setLogFile(logFile, rewrite);
	}

	public static void setLogFile(File log, boolean rewrite) {

		logFile = log;
		if (rewrite) {
			if (logFile.exists())
				try {
					logFile.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		logPath = logFile.getAbsolutePath();
		logger.log(Level.INFO, "common.info.set.logFile", logPath);
	}

	public static void resetTempLog() {
		File temp = new File(Constants.LOG_ROOT);
		if (!temp.exists()) {
			temp.mkdirs();
		}

		tempLog = new File(tempLogPath);
		if (tempLog.exists())
			tempLog.delete();
		try {
			tempLog.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getLogPath() {
		return logPath;
	}

	public static void log(String line) {

		if ("".equals(line.trim()))
			return;

		/*final String dialogLine = line;
		Display display = Display.getDefault();
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					CommonHelper.logDialogAppend(dialogLine);
				}
			});
		}*/

		if (logFile == null || !logFile.exists()) {
			logger.log(Level.SEVERE, "common.error.no_set_logFile");
		}

		if (!logFile.canWrite()) {
			logger.log(Level.SEVERE, "common.error.logFile.cannot.write", logPath);
		}

		try {
			PrintStream output = new PrintStream(new FileOutputStream(logFile, true), true, "UTF-8");

			if (isOracle) {
				if (Pattern.compile(".*CREATE USER.*IDENTIFIED BY.*", Pattern.CASE_INSENSITIVE).matcher(line).matches())
					return;
			}

			if (remove != null && remove.length > 0) {
				for (Pattern r : remove) {
					if (r.matcher(line).matches())
						return;
				}
			}

			if (replace != null && replace.length > 0) {
				for (String regx : replace) {
					line = line.replaceAll(regx, "REMOVED");
				}
			}

			output.println(line);
			output.close();
			if (tempLog != null) {
				PrintStream tempOut = new PrintStream(new FileOutputStream(tempLog, true), true, "UTF-8");
				tempOut.println(line);
				tempOut.close();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.logFileWritter.exception", e);
		}
	}

	public static Pattern[] getRemove() {
		return remove;
	}

	public static void setRemove(Pattern[] remove) {
		CommandLogger.remove = remove;
	}

	public static String[] getReplace() {
		return replace;
	}

	public static void setReplace(String[] replace) {
		CommandLogger.replace = replace;
	}
}
