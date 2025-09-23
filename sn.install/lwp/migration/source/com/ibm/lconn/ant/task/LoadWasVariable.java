/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.ant.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

import com.ibm.lconn.common.task.CommandExec;
import com.ibm.lconn.common.util.OSUtil;
import com.ibm.lconn.common.util.ObjectUtil;

/**
 * <LoadWasVariable wasHome="D:/IBM/WebSphere/AppServer_ZJJ"
 * profileName="MyTestProfile" cellName="zhangjunjingNode01Cell"
 * nodeName="zhangjunjingNode01" serverName="server1"/>
 */

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class LoadWasVariable extends BaseTask {
	private static final String ERROR_LOAD_WAS_VARIABLE = "Load WebSphere Application Server variable fail. With the following setting: \nWAS home: {0}\nProfile name: {1}\nCell name: {2}\nNode name: {3}\nServer name: {4}";
	private static final String cmdWin = "\"$<was_home>/bin/wsadmin.bat\", -profileName, $<profile_name>, -lang, jython, -conntype, NONE, -f, \"{0}\", $<target_scope>, list";
	private static final String cmdLinux = "$<was_home>/bin/wsadmin.sh, -profileName, $<profile_name>, -lang, jython, -conntype, NONE, -f, {0}, $<target_scope>, list";
	private static final String STR_DOT = ".";
	private String wasHome;
	private String cellName;
	private String nodeName;
	private String serverName;
	private String profileName;
	private String prefix = "";

	@Override
	public void execute() throws BuildException {
		log("Loading WAS variable...");
		String wasHome2 = getWasHome();
		String profileName2 = getProfileName();
		String cellName2 = getCellName();
		String nodeName2 = getNodeName();
		String serverName2 = getServerName();
		log("WasHome={0}", wasHome2);
		log("Profile={0}", profileName2);
		log("Cell={0}", cellName2);
		log("Node={0}", nodeName2);
		if (serverName2 != null)
			log("Server={0}", serverName2);
		Properties loadWasVariables = loadWasVariables(wasHome2, profileName2,
				cellName2, nodeName2, serverName2);
		Enumeration<?> propertyNames = loadWasVariables.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String key = (String) propertyNames.nextElement();
			String val = loadWasVariables.getProperty(key);
			String propertyKey = (ObjectUtil.isEmpty(getPrefix()) ? key
					: getPrefix() + STR_DOT + key);
			if (!ObjectUtil.isEmpty(val)) {
				setProperty(propertyKey, val);
				log("Loaded: {0}={1}", propertyKey, val);
			}
		}
	}

	public static final Pattern WS_VARIABLE_REGEXP = Pattern
			.compile("^\\[(.+)\\] = \\[(.*)\\]");

	public Properties loadWasVariables(String wasHome, String profileName,
			String cellName, String nodeName, String serverName) {
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
			InputStream is = LoadWasVariable.class
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
			log(ERROR_LOAD_WAS_VARIABLE, wasHome, profileName, cellName,
					nodeName, serverName);
			return props;
		}
		String cmd = OSUtil.isWindows() ? cmdWin : cmdLinux;
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
		LoadWasVariable wasVariableLoader = new LoadWasVariable();
		wasVariableLoader.setOutput(System.out);
		Properties loadWasVariables = wasVariableLoader.loadWasVariables(
				"d:/IBM/WebSphere/AppServer", "AppSrv02", "CharleyNode05Cell",
				"CharleyNode05", null);
		loadWasVariables.list(System.out);
	}

	public String getWasHome() {
		return wasHome;
	}

	public void setWasHome(String wasHome) {
		this.wasHome = wasHome;
	}

	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
