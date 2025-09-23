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

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.was;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.task.CommandExec;
import com.ibm.lconn.common.util.OSUtil;
import com.ibm.lconn.common.util.ObjectUtil;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class WasVariableLoader extends LogOperator{
	private static final String ERROR_LOAD_WAS_VARIABLE = "Load WebSphere Application Server variable fail. With the following setting: \nWAS home: {0}\nProfile name: {1}\nCell name: {2}\nNode name: {3}\nServer name: {4}";
	private static final String cmdWin = "$<was_home>/profiles/$<profile_name>/bin/wsadmin.bat, -lang, jython, -conntype, NONE, -f, {0}, $<target_scope>, list";
	private static final String cmdLinux = "$<was_home>/profiles/$<profile_name>/bin/wsadmin.sh, -lang, jython, -conntype, NONE, -f, {0}, $<target_scope>, list";
	
	public static final Pattern WS_VARIABLE_REGEXP = Pattern
			.compile("^\\[(.+)\\] = \\[(.*)\\]");

	public Properties loadWasVariables(String wasHome,
			String profileName, String cellName, String nodeName,
			String serverName) {
		String scopeName = cellName;
		if (!ObjectUtil.isEmpty(nodeName)) {
			scopeName += "," + nodeName;
			if (!ObjectUtil.isEmpty(serverName))
				scopeName += "," + serverName;
		}
		Properties result = new Properties();
		Properties props = new Properties();

		String filePath = "ERROR writing variableProc.py";
		try {
			String tmpFolder = System.getProperty("java.io.tmpdir");
			InputStream is = WasVariableLoader.class
					.getResourceAsStream("variableProc.py");
			File tmpFile = File.createTempFile("variableProc", ".py", new File(
					tmpFolder));
			tmpFile.deleteOnExit();
			OutputStream out = new FileOutputStream(tmpFile);
			byte[] b = new byte[2048];
			int read = is.read(b);
			while (read != -1) {
				out.write(b, 0, read);
				read = is.read(b);
			}
			out.flush();
			out.close();
			filePath = tmpFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			log(ERROR_LOAD_WAS_VARIABLE, wasHome, profileName, cellName, nodeName, serverName);
			return props;
		}
		String cmd = OSUtil.isWindows()? cmdWin: cmdLinux;
		String command = MessageFormat.format(cmd, filePath);
		props.setProperty("was_home", wasHome);
		props.setProperty("profile_name", profileName);
		props.setProperty("target_scope", scopeName);
		StringBuffer sb = new StringBuffer();
		try {
			CommandExec.executeCommand(command, null, sb, props);

			String[] split = sb.toString().split("\n");
			for (int i = 0; i < split.length; i++) {
				CharSequence line = split[i];
				Matcher m = WS_VARIABLE_REGEXP.matcher(line);
				if (m.matches()) {
					String varName = m.group(1);
					String varValue = m.group(2);
					if (!ObjectUtil.isEmpty(varName)) {
						if (ObjectUtil.isEmpty(varValue))
							varValue = "";
						result.setProperty(varName, varValue);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		WasVariableLoader wasVariableLoader = new WasVariableLoader();
		wasVariableLoader.setOutput(System.out);
		Properties loadWasVariables = wasVariableLoader.loadWasVariables(
				"d:/IBM/WebSphere/AppServer", "AppSrv02", "CharleyNode05Cell",
				"CharleyNode05", null);
		loadWasVariables.list(System.out);
	}

}
