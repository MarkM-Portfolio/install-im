/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */

package com.ibm.websphere.update.silent;

import com.ibm.websphere.update.ioservices.CalendarUtil;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This is a utility class which provides methods for logging events.
 */

public class Logger {

	private static String logFileName = "";

	private static boolean debugOn = false;

	private static boolean progressDotsOn = false;

	private static String endLine = System.getProperty("line.separator");

	/**
	 *  Main program mainly used for testing
	 */
	public static void main(String args[]) {

		String file =
			System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "logfile";
		Logger.logEnvironment(file);

	}

	/**
	 * Constructor
	 */
	public Logger() {
	}

	/**
	 * @param logFileName  the logFileName to set
	 * @uml.property  name="logFileName"
	 */
	public static void setLogFileName(String fileName) {
		logFileName = fileName;
	}

	private static void printProgressDot() {
		if (progressDotsOn) {
			System.out.print(".");
			System.out.flush();
		}
	}

	/**
	 * @param debugOn  the debugOn to set
	 * @uml.property  name="debugOn"
	 */
	public static void setDebugOn(boolean tf) {
		debugOn = tf;
	}

	private static String getDateSpecificFilename() {
		return CalendarUtil.fileFormat(CalendarUtil.getTimeStamp()) + "_silent_check.log";
	}

	public static void log(String message) {
		printProgressDot();
		if (logFileName.equals("")) {

			File logFile = new File(getDateSpecificFilename());
			logFile.deleteOnExit();
			logFileName = logFile.getAbsolutePath();

		}
		if (!logFileName.equals("")) {
			log(logFileName, message);
		}
	}

	public static void debug(String message) {
		printProgressDot();
		if (debugOn) {
			if (!logFileName.equals("")) {
				log(logFileName, "[DEBUG] " + message);
			}
		}
	}

	/**
	 * Method which logs the given message into the given log file
	 * @param String - logFileName - The log file name in which to record event
	 * @param String - message - The message to record in the log file
	 */

	public static void log(String logFileName, String message) {

		Logger.logFileName = logFileName;

		PrintWriter logFile = Logger.openFile(logFileName);

		if (logFile == null)
			return;

		try {
			logFile.println(message);
			logFile.close();
		} catch (Exception e) {
			System.out.println("Could not write to the file: " + logFileName);
			e.printStackTrace();
		}

	}

	public static void logEnvironment(String logFileName) {
		PrintWriter logFile = Logger.openFile(logFileName);

		if (logFile == null)
			return;

		String timeStamp = "Current Date = " + CalendarUtil.getTimeStampAsString();

		try {

			logFile.println(
				"#################################################################");
			logFile.println("Logger Version " + Logger.getVersion());
			logFile.println(timeStamp);
			logFile.println(
				"Operating System Name = " + System.getProperty("os.name"));
			logFile.println(
				"Operating System Architecture = "
					+ System.getProperty("os.arch"));
			logFile.println(
				"Operating System Level = " + System.getProperty("os.version"));
			logFile.println(
				"Java Version = " + System.getProperty("java.version"));
			logFile.println(
				"Java Vendor = " + System.getProperty("java.vendor"));
			logFile.println("User Name = " + System.getProperty("user.name"));
			logFile.println(
				"Current Directory = " + System.getProperty("user.dir"));
			logFile.println();
			logFile.println();

			logFile.close();
		} catch (Exception e) {
			System.out.println("Could not write to the file : " + logFileName);
			e.printStackTrace();
		}

	}

	/**
	 * Returns the version of the Logger class
	 * @return String - version of the Logger class
	 */
	public static String getVersion() {
		return "1.0";
	}

	/**
	 * Opens the log file and returns the File object
	 * @param - String - logFileName
	 * @return - PrintWriter - logFile
	 */
	private static PrintWriter openFile(String logFileName) {

		try {
			FileOutputStream fos = new FileOutputStream(logFileName, true);
			PrintWriter printWriter = new PrintWriter(fos, true);
			return printWriter;
		} catch (FileNotFoundException e) {
			System.out.println("File not found : " + logFileName);
			e.printStackTrace();
		}

		return null;

	}
}
