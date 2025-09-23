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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class NetStore {
	private static final String netstore_executer_win = Constants.TDI_WORK_DIR
			+ Constants.FS + "netstore.bat";
	private static final String netstore_executer_lin = Constants.TDI_WORK_DIR
			+ Constants.FS + "netstore";
	private static final String derby_executer_win = Constants.TDI_WORK_DIR
			+ Constants.FS + "derby.bat";
	private static final String derby_executer_lin = Constants.TDI_WORK_DIR
			+ Constants.FS + "derby.sh";
	private static final int netstore = 1;
	private static final int derby = 2;
	private static Map<String, String> environment;

	public static void start(Map<String, String> env) {
		environment = env;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				String[] command = new String[] { getExecuter(netstore),
						"start", ">NUL", "2>NUL" };
				getResult(command, environment);
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void stop(Map<String, String> env) {
		environment = env;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				String[] command = new String[] { getExecuter(netstore), "stop" , ">NUL", "2>NUL" };
				getResult(command, environment);
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean isStarted(Map<String, String> env) {
		/*
		 * String[] command = new String[] { getExecuter(derby), "ping", "-h",
		 * env.get("TDI_CS_HOST"), "-p", env.get("TDI_CS_PORT") }; return
		 * getResult(command, env);
		 */
		return false;
	}

	private static String getExecuter(int type) {
		String str = null;
		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			switch (type) {
			case netstore:
				str = netstore_executer_win;
				break;
			case derby:
				str = derby_executer_win;
				break;
			}

		} else {
			switch (type) {
			case netstore:
				str = netstore_executer_lin;
				break;
			case derby:
				str = derby_executer_lin;
				break;
			}
		}
		return str;
	}

	private static boolean getResult(String[] command, Map<String, String> env) {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		if (env != null)
			builder.environment().putAll(env);
		Process process = null;
		try {
			process = builder.start();
			if (process != null) {

				InputStream is = process.getInputStream();
				LineNumberReader lnr = new LineNumberReader(
						new InputStreamReader(is));
				if (getExecuter(derby).equals(command[0])) {
					String line;
					int value = 0;
					while ((line = lnr.readLine()) != null) {
						if (line.indexOf("Could not connect") != -1) {
							value = -1;
						}
					}

					return value == 0;
				} else {
					/*
					 * while (lnr.readLine() != null) { }
					 */
				}
				process.waitFor();
				is.close();

				int exitValue = process.exitValue();
				return exitValue == 0;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] a) {
		Map<String, String> env = new HashMap<String, String>();
		env.put("TDI_CS_PORT", "1527");
		env.put("TDI_CS_HOST", "localhost");
		env.put("TDIPATH", "c:/ibm/tdi/v6.1");

		System.out.println(NetStore.isStarted(env));
	}
}
