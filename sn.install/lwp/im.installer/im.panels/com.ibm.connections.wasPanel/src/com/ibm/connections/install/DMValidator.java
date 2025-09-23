/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class DMValidator {

	public static final String HEAP_SIZE_ERROR = "heap_size_error";
	public static final String NODE_NUM_ERROR = "node_num_error";
	private static final ILogger log = IMLogger
			.getLogger(com.ibm.connections.install.DMValidator.class);

	protected static JavaPropertyGetter propertyGetter;

	// static {
	// String tempDir = System.getProperty("tempDir");
	// File file = new File(tempDir + "/dminfo.properties");
	// // file = new File("src/dminfo.properties");
	// deleteFile(file);
	//
	// String profilePath = System.getProperty("profilePath");
	// String cdLocation = System.getProperty("cdLocation");
	// String dmUserid = System.getProperty("dm.userid");
	// String dmPassword = System.getProperty("dm.password");
	//
	// String command = profilePath +
	// "/bin/wsadmin -conntype SOAP -lang jython -username " + dmUserid +
	// " -password " + dmPassword + " -f " + cdLocation +
	// "/ConfigEngine/config/was/wkplc_GetDMInfo.py " + tempDir +
	// "/dminfo.properties";
	// ExternalCommandAction eca = new ExternalCommandAction();
	// eca.setCommand(command);
	// eca.execute();
	//
	// propertyGetter = new JavaPropertyGetter(file);
	// }

	public static String getDMInfo(String profilePath, String pyPath,
			String dmUserid, String dmPassword, String hostname, String port)
			throws Exception {

		try {
			String tempDir = profilePath + "/bin";
			String proFileDir = WasSecurityValidator.transferPath(tempDir
					+ "/dminfo.properties");
			File file = new File(proFileDir);
			deleteFile(file);

			File pyfile = new File(pyPath);
			if (!pyfile.exists()) {
				return "2";
			}

			String extension = null;
			String command = null;
			String commands[] = null;
			ExternalCommandAction eca = new ExternalCommandAction();
			eca.setWorkingDirectory(profilePath);

			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				pyPath = "\"" + pyPath + "\"";
				command = "\"" + profilePath
						+ "/bin/wsadmin.bat\" -conntype SOAP -host " + hostname
						+ " -port " + port + " -lang jython -username \""
						+ dmUserid + "\" -password " + dmPassword + " -f "
						+ pyPath + " \"" + proFileDir + "\"";
				eca.setCommand(command);

				// for log
				String commandTemp = new String(command);
				String temp = commandTemp.replace(dmPassword,
						"PASSWORD_REMOVED");
				log.info("Get DM Info command : " + temp);
			} else if (osName.toLowerCase().startsWith("os/400")) {
			// OS400_Enablement
				commands = new String[16];
				commands[0] = profilePath + "/bin/wsadmin";
				commands[1] = "-conntype";
				commands[2] = "SOAP";
				commands[3] = "-host";
				commands[4] = hostname;
				commands[5] = "-port";
				commands[6] = port;
				commands[7] = "-lang";
				commands[8] = "jython";
				commands[9] = "-username";
				commands[10] = dmUserid;
				commands[11] = "-password";
				commands[12] = dmPassword;
				commands[13] = "-f";
				commands[14] = pyPath;
				commands[15] = tempDir + "/dminfo.properties";
				eca.setArrayCmds(true);
				eca.setCommands(commands);

				// for log
				String commandTemp = new String(commands.toString());
				String temp = commandTemp.replace(dmPassword,
						"PASSWORD_REMOVED");
				log.info("Get DM Info command : " + temp);
			
			} else {
				commands = new String[16];
				commands[0] = profilePath + "/bin/wsadmin.sh";
				commands[1] = "-conntype";
				commands[2] = "SOAP";
				commands[3] = "-host";
				commands[4] = hostname;
				commands[5] = "-port";
				commands[6] = port;
				commands[7] = "-lang";
				commands[8] = "jython";
				commands[9] = "-username";
				commands[10] = dmUserid;
				commands[11] = "-password";
				commands[12] = dmPassword;
				commands[13] = "-f";
				commands[14] = pyPath;
				commands[15] = tempDir + "/dminfo.properties";
				eca.setArrayCmds(true);
				eca.setCommands(commands);

				// for log
				String commandTemp = new String(commands.toString());
				String temp = commandTemp.replace(dmPassword,
						"PASSWORD_REMOVED");
				log.info("Get DM Info command : " + temp);
			}

			eca.execute();

			if (!file.exists()) {
				return "3";
			}

			propertyGetter = new JavaPropertyGetter(file);
			return eca.getReturnCode();
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean validateHeapsize() {
		boolean result = false;

		String dmHeapSizeTooLow = propertyGetter
				.getProperty("dm.heapSizeTooLow");

		if ("true".equals(dmHeapSizeTooLow)) {
			result = true;
		}

		return result;
	}

	public static boolean validteNodesNumber() {
		List list = detectNodes();
		if (list.size() > 0) {
			return true;
		}
		return false;
	}

	public static List detectNodes() {
		ArrayList list = new ArrayList();
		String dmnodeStr = detectDMNode();
		String nodesStr = propertyGetter.getProperty("dm.nodes");
		String[] nodes = nodesStr.split(",");
		for (int i = 0; i < nodes.length; i++) {
			if (!dmnodeStr.equals(nodes[i])) {
				list.add(nodes[i]);
			}
		}
		return list;
	}
	
	public static Map detectNodeAgents() {
		String nodeAgents = propertyGetter.getProperty("dm.node.agents");
		Map map = FieldMappingParser.parseSemicolon(nodeAgents);
		return map;
	}
	
	public static String detectDMNode() {

		return propertyGetter.getProperty("dm.nodename");
	}

	public static Map detectNodeHostNames() {
		String nodes = propertyGetter.getProperty("dm.nodes.hostname");
		Map map = FieldMappingParser.parseSemicolon(nodes);
		return map;
	}
	
	public static String detectNodeHostName(String nodeName) {
		String nodes = propertyGetter.getProperty("dm.nodes.hostname");
		Map map = FieldMappingParser.parseSemicolon(nodes);
		Set nodesSet = map.keySet();
		Iterator it = nodesSet.iterator();
		while (it.hasNext()) {
			String keyValue = (String)it.next();
			if(nodeName.equalsIgnoreCase(keyValue)) {
				return (String)map.get(keyValue);
			}
		}
		return "";
	}

	public static String detectDMCellName() {

		return propertyGetter.getProperty("dm.cellanme");
	}

	public static List detectClusters() {
		ArrayList list = new ArrayList();
		String clustersStr = propertyGetter.getProperty("dm.clusterlist");
		Map clustersMap = FieldMappingParser.parseSemicolon(clustersStr);
		Set clustersSet = clustersMap.keySet();
		Iterator it = clustersSet.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
	
	public static Map detectServers() {
		Map result = new HashMap();
		String nodeServersStr = propertyGetter.getProperty("dm.nodes.servername");
		Map map = FieldMappingParser.parseSemicolon(nodeServersStr);
		Set nodeSet = map.keySet();
		Iterator it = nodeSet.iterator();
		String node, server = new String();
		while (it.hasNext()) {
			server = new String();
			Object o = it.next();
			if (o != null) {
				node = (String)o;
				String servers = (String) map.get(o);
				if (servers != null && servers.trim().length() > 0) {
					String serverList[] = servers.split(",");
					for (int i = 0; i < serverList.length; i++) {
						int index = serverList[i].indexOf("(");
						if(!serverList[i].substring(0, index).equalsIgnoreCase("nodeagent")) {
							server += serverList[i].substring(0, index) + ",";
						}
					}
				}
				
				if(server.trim().length() > 0) {
					if(server.endsWith(","))
						server = server.substring(0, server.length()-1);
					result.put(node, server);
				}
			}
		}
		return result;
	}

	public static String getClusterFullInfo() {
		return propertyGetter.getProperty("dm.clusterlist");
	}

	public static boolean deleteFile(File file) {
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}

	public static void main(String[] args) {
		// System.out.println(detectClusters());
		// System.out.println(detectDMNode());
		// System.out.println(detectNodes());

	}
}
