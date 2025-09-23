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

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

public class CommandExecuter {
	private static final Logger logger = LogUtil
			.getLogger(CommandExecuter.class);
	private static boolean isOracle = false;

	public static int run(List<String> command, String workDir,
			Map<String, String> environment, String outputPath) {
		int size = command.size();
		String[] commandArr = new String[size];
		for (int i = 0; i < size; i++) {
			commandArr[i] = command.get(i);
		}
		return run(commandArr, workDir, environment, outputPath);
	}
	
	public static int run(List<String> command, String workDir,
			Map<String, String> environment, String outputPath, List<String> input) {
		int size = command.size();
		String[] commandArr = new String[size];
		for (int i = 0; i < size; i++) {
			commandArr[i] = command.get(i);
		}
		
		size = input.size();
		String[] commandInput = new String[size];
		for (int i = 0; i < size; i++) {
			commandInput[i] = input.get(i);
		}
		return run(commandArr, workDir, environment, outputPath, commandInput);
	}

	public static int run(List<String> command, String workDir,
			Map<String, String> evironment, File output) {
		return run(command, workDir, evironment, output.getAbsolutePath());
	}

	public static int run(List<String> command, String workDir,
			Map<String, String> evironment, File output, String dbType) {
		/*logger.log(Level.INFO, "Start to loop the command");
		for (String a : command)
		logger.log(Level.INFO, "command = " + a);
		logger.log(Level.INFO, "End to loop the command");*/
		
		if (Constants.DB_ORACLE.equals(dbType))
			isOracle = true;
		int res = run(command, workDir, evironment, output.getAbsolutePath());
		isOracle = false;
		return res;
	}

	public static int run(String[] command, String workDir,
			Map<String, String> environment, File output) {
		return run(command, workDir, environment, output.getAbsolutePath());
	}

	public static int run(String[] command, String workDir,
			Map<String, String> environment, String outputPath) {
		CommandLogger.isOracle = isOracle;
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		// set working directory
		if (workDir != null)
			builder.directory(new File(workDir));
		if (environment != null)
			builder.environment().putAll(environment);
		Process process = null;
		try {
			process = builder.start();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "common.error.command.start", e);
			CommandLogger.log("Start Exception:" + e.getMessage());
		}

		int exitValue = -1;
		if (process != null) {
			try {
				InputStream is = process.getInputStream();
				LineNumberReader lnr = new LineNumberReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = lnr.readLine()) != null) {
					CommandLogger.log(line);
				}

				process.waitFor();
				is.close();

				exitValue = process.exitValue();
				CommandLogger.log("Exit value: " + exitValue);
				CommandLogger.log("Quit.");
			} catch (Exception e) {
				logger.log(Level.SEVERE, "common.error.command.running", e);
			}
		}
		return exitValue;
	}
	
	public static int run(String[] command, String workDir,
			Map<String, String> environment, String outputPath, String[] input) {
		CommandLogger.isOracle = isOracle;
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		// set working directory
		if (workDir != null)
			builder.directory(new File(workDir));
		if (environment != null)
			builder.environment().putAll(environment);
		Process process = null;
		try {
			process = builder.start();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "common.error.command.start", e);
			CommandLogger.log("Start Exception:" + e.getMessage());
		}

		int exitValue = -1;
		if (process != null) {
			try {
				InputStream is = process.getInputStream();
				LineNumberReader lnr = new LineNumberReader(
						new InputStreamReader(is, "UTF-8"));
				
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(),"UTF-8"));
				for (String in : input) {
					bw.write(in);
					bw.newLine();
				}
				bw.flush();
				bw.close();
				
				String line = null;
				while ((line = lnr.readLine()) != null) {
					CommandLogger.log(line);
				}

				process.waitFor();
				is.close();

				exitValue = process.exitValue();
				CommandLogger.log("Exit value: " + exitValue);
				CommandLogger.log("Quit.");
			} catch (Exception e) {
				logger.log(Level.SEVERE, "common.error.command.running", e);
			}
		}
		return exitValue;
	}

	public static int run(String command, String workDir,
			Map<String, String> environment, String outputPath) {
		CommandLogger.isOracle = isOracle;
		CommandLogger.setLogPath(outputPath);

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		// set working directory
		if (workDir != null) {
			logger.log(Level.INFO, "set work dir to " + workDir);
			builder.directory(new File(workDir));
		}
		if (environment != null) {
			logger.log(Level.INFO, "set environment to " + environment);
			builder.environment().putAll(environment);
		}
		Process process = null;
		try {
			process = builder.start();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "common.error.command.start", e);
			CommandLogger.log("Start Exception:" + e.getMessage());
		}

		int exitValue = -1;
		if (process != null) {
			try {
				InputStream is = process.getInputStream();
				LineNumberReader lnr = new LineNumberReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = lnr.readLine()) != null) {
					CommandLogger.log(line);
				}
				process.waitFor();
				is.close();

				exitValue = process.exitValue();
				CommandLogger.log("Exit value: " + exitValue);
				CommandLogger.log("Quit.");
			} catch (Exception e) {
				logger.log(Level.SEVERE, "common.error.command.running", e);
			}
		}
		return exitValue;
	}

	public int run(String command, String workDir,
			Map<String, String> environment, File output) {
		return run(command, workDir, environment, output.getAbsolutePath());
	}
}
