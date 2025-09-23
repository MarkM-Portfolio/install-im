/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Vector;
import com.ibm.websphere.update.delta.Logger;

public class ExecCmd extends Thread {

	public static final boolean DO_ADJUST_FOR_PLATFORM = true;
	public static final boolean DONT_ADJUST_FOR_PLATFORM = false;

	public static final boolean DO_ECHO_LOG = true;
	public static final boolean DONT_ECHO_LOG = false;

	protected boolean adjustForPlatform;
	protected Vector<String> captureBuffer;
	protected boolean echoLog;

	protected static final boolean IS_ERROR_MESSAGE = true;
	protected static final boolean IS_NOT_ERROR_MESSAGE = false;

	public static final boolean DONT_ECHO_STDOUT = false;
	public static final boolean DO_ECHO_STDERR = true;
	public static final boolean DONT_ECHO_STDERR = false;

	public ExecCmd() {
		this(DONT_ADJUST_FOR_PLATFORM, DO_ECHO_LOG);
	}


	public ExecCmd(boolean adjustForPlatform) {
		this(adjustForPlatform, DO_ECHO_LOG);
	}

	public ExecCmd(boolean adjustForPlatform, boolean echoLog) {
		this.adjustForPlatform = adjustForPlatform;

		this.captureBuffer = null;

		this.echoLog = echoLog;
	}

	public int Execute(String execText, boolean echoStandardOutput,
			boolean echoStandardError, Logger logStream) {
		return launch(execText, null, null, echoStandardOutput,
				echoStandardError, logStream);
	}

	public int Execute(String[] execArray, boolean echoStandardOutput,
			boolean echoStandardError, Logger logStream) {
		return launch(null, execArray, null, echoStandardOutput,
				echoStandardError, logStream);
	}
	protected int launch(String execText, String[] execArray,
			String[] envArray, boolean echoStandardOutput,
			boolean echoStandardError, Logger logStream) {
		
		logStream.Both("ExecCmd::launch");
		if ((execText == null) && (execArray == null)) {
			logStream.Both("Error(" + 1
					+ "): The commands to execute are null. ");
			return 889;
		}
		if (adjustForPlatform) {
			if (execText != null) {
				execText = "CMD.EXE /C " + execText;
			} else {
				String[] newExecArray = new String[execArray.length + 2];
				newExecArray[0] = "CMD.EXE";
				newExecArray[1] = "/C";
				for (int i = 0; i < execArray.length; i++)
					newExecArray[i + 2] = execArray[i];
				execArray = newExecArray;
			}
		}

		Process aProcess = null;

		try {
			
			Runtime aRuntime = Runtime.getRuntime();
			if (execText != null){
				logStream.Both("ExecCmd execText::" + ExecCmd.removePassword(execText, "-password "));
				aProcess = aRuntime.exec(execText, envArray); 
			}
			else{
				String temp ="";
				for (int i=0;i<execArray.length;i++){
					temp +=execArray[i] + " ";
				}
				logStream.Both("ExecCmd execArry::" + ExecCmd.removePassword(temp, "-password "));
				aProcess = aRuntime.exec(execArray, envArray); 
			}
		} catch (IOException ex) {
			logStream.Both("Error(" + 2 + "): IOException creating RunTime()"
					+ ex);
			return 888;
		}
		int rc = 0;
		try {
			InputStream is = aProcess.getInputStream();
			LineNumberReader lineReader = new LineNumberReader(
					new InputStreamReader(is));
			String message = null;
			while ((message = lineReader.readLine()) != null) {
				
				if (message.indexOf("wasPwd =") == -1) {
					logStream.Both(message);
					logStream.flush();
				}
			}
			rc = aProcess.waitFor(); 
			is.close();
		} catch (InterruptedException e) {
			logStream.Both("Error(" + 56
					+ "): Thread interrupted while waiting for process: "
					+ e.getMessage());
		} catch (IOException e) {
			logStream.Both("Error(" + 56
					+ "): IOException getting InputStream Process: "
					+ e.getMessage());
		}
		logStream.Both("ExecCmd::launch returns " + rc);
		aProcess.destroy();
		return rc;
	}
	
    public static String removePassword(String input, String searchStr) 
    {
    	
    	// add a space to the end of the input, this makes it easier
    	// to look for the password when it is at the end of the line
    	// then instead of lookin for System.getProperty("line.separator")
    	// we can just search for the SPACE.
    	String tmp = input + " ";
    	
		int pre_index = tmp.indexOf(searchStr);
		
		if(pre_index != -1)
		{
			String pre_sub = tmp.substring(0, pre_index+searchStr.length());
			
			// find the SPACE
			int post_index = tmp.indexOf(" ", pre_index+searchStr.length());
			String post_sub = tmp.substring(post_index, tmp.length());
			
			return pre_sub + PWD_REMOVED + post_sub;
		}
		return input;
	}
    public static final String PWD_REMOVED = "PASSWORD_REMOVED";
	public static final String stderrPrefix = "StdErr: ";
	protected BufferedReader execOutput;
	protected String linePrefix;

	protected ExecCmd(BufferedReader execOutput, boolean echoLog,
			String linePrefix, Vector<String> captureBuffer) {
		this(DONT_ADJUST_FOR_PLATFORM, echoLog);

		this.captureBuffer = captureBuffer;

		this.execOutput = execOutput;
		this.linePrefix = linePrefix;
	}

	protected void logError(int errNum, String msg, Exception ex) {
		msg += ": " + ex.getMessage();

		logError(errNum, msg);
	}

	protected void logError(int errNum, String msg) {
		msg = "Error ( " + errNum + " ): " + msg;

		basicLog(msg, IS_ERROR_MESSAGE);
	}

	protected void logMessage(String msg) {
		msg = "Log: " + msg;

		basicLog(msg, IS_NOT_ERROR_MESSAGE);
	}

	protected void basicLog(String msg, boolean isError) {
		if (captureBuffer != null)
			captureBuffer.add(msg);

		if (echoLog) {
			if (!isError)
				System.out.println(msg);
			else
				System.out.println(msg);
		}
	}

	public void run() {
		try {
			String dataLine;

			while ((dataLine = execOutput.readLine()) != null) {
				if (linePrefix != null)
					dataLine = linePrefix + dataLine;

				basicLog(dataLine, IS_NOT_ERROR_MESSAGE);
				sleep(100);
			}

		} catch (IOException ex) {
			logError(5, "IOException reading CaptureBuffer", ex);
		} catch (InterruptedException ie) {
			logError(5, "InterruptedException reading CaptureBuffer", ie);
		}
	}
	   public static void main(String[] args)throws Exception{   
	        
	    	
	        String input ="C:/IBM/WebSphere/AppServer/profiles/Dmgr01/bin/wsadmin.bat -lang jython -username wasadmin -password passw0rd -f C:/Program Files/IBM/Connections/efix/activities/work/../config/LO86705_activities_post_update.py install ";

            String output= ExecCmd.removePassword(input, "-password ");
            System.out.print(output);
	     }   
}
