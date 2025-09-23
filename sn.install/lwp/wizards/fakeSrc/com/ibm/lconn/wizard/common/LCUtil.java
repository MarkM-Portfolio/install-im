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
package com.ibm.lconn.wizard.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.data.FeatureInfo;
import com.ibm.lconn.wizard.cluster.data.LCInfo;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.MessagePopup;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class LCUtil {
	private static final String CURRENT_PRODUCT = "Current_Product";
	private static final String WAS_HOME = "WAS_HOME";
	private static final String LC_HOME = "LC_HOME";
	private static final Logger logger = LogUtil.getLogger(LCUtil.class);
	private static final String LC_INFO = "LC_INFO";
	public static final String FEATURE_MEMBER_NAME = "FEATURE_MEMBER_NAME";
	public static final String FEATURE_CLUSTERED_NODES = "FEATURE_CLUSTERED_NODES";
	public static final String CONSTANTS_GLOBAL = "Constants.GLOBAL";
	private static final String MEMBER_NAME = "Member";

	public static final Pattern DM_MEMBER_NAME_REGEXP = Pattern
			.compile("^(\\w+)\\(cells/(\\w+)/clusters/(\\w+)\\|.*");
	public static final Pattern DM_CELL_NAME_REGEXP = Pattern
			.compile("^\\[\\[(\\w+)\\]\\]");
	public static final Pattern WS_VARIABLE_REGEXP = Pattern
			.compile("^\\[(.+)\\] = \\[(.*)\\]");

	private static boolean init = false;

	static {
		DataPool.setValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_WIZAR_HOME, new File("")
						.getAbsolutePath());
	}

	public static void initialize(String wizardId) {;
		String lcHome = "/fake/websphere/LotusConnections/";
		setLCHome(lcHome);
		DataPool.setValue(wizardId, ClusterConstant.INPUT_LC_HOME, getLCHome());
		String messageTitle = MessageUtil.getMsg("WIZARD.title", wizardId);
		DataPool.setValue(CONSTANTS_GLOBAL, CURRENT_PRODUCT, messageTitle);
		MessagePopup.commonTitle = messageTitle;
		if (init)
			return;
		boolean featureFound = false;
		String[] featureAll = Constants.FEATURE_ALL;
		for (int i = 0; i < featureAll.length; i++) {
			FeatureInfo feature = getFeature(featureAll[i]);
			if (feature != null) {
				featureFound = true;
				DataPool.setValue(CONSTANTS_GLOBAL, LC_HOME, feature
						.getLcHome());
				DataPool.setValue(CONSTANTS_GLOBAL, WAS_HOME, feature
						.getWasHome());
				DataPool.setValue(wizardId, ClusterConstant.INPUT_WAS_HOME,
						feature.getWasHome());
				DataPool.setValue(wizardId, ClusterConstant.INPUT_LC_HOME,
						feature.getLcHome());
	
				getMemeberOfFeatureProfile(featureAll[i]);
			}
		}
		if (!featureFound) {
			MessagePopup
					.showErrorMessage(
							LCUtil.class,
							MessageFormat
									.format(
											"No feature found in lotus connections home: \n\t{0}. \nThe wizard will exit. ",
											getLCHome()));
		}
	}

	public static String getProductName() {
		return DataPool.getValue(CONSTANTS_GLOBAL, CURRENT_PRODUCT);
	}

	public static void setLCHome(String lcHome) {
		File f = new File(lcHome);
		lcHome = f.getAbsolutePath();
		DataPool.setValue(CONSTANTS_GLOBAL, LC_HOME, lcHome);
	}

	public static String getLCHome() {
		return DataPool.getValue(CONSTANTS_GLOBAL, LC_HOME);
	}

	public static String getProfileName(String prodPath) {
		// System.out.println(prodPath);
		File profileDir = new File(prodPath + File.separator + "uninstall"
				+ File.separator + "profiles");
		File curItem;
		// System.out.println(profileDir.toString());
		String[] list = profileDir.list();

		// there should be only one sub-directory in profiles, and that is the
		// name
		// of the WAS profile the feature is installed to
		for (int i = 0; i < list.length; i++) {
			curItem = new File(profileDir, list[i]);
			// System.out.println(curItem.toString());
			if (curItem.isDirectory()) {
				return curItem.getName();
			}
		}
		return null;
	}

	public static String getClusterName(String featureName) {
		return featureName.toUpperCase().charAt(0)
				+ featureName.toLowerCase().substring(1) + "Cluster";
	}

	public static boolean featureInstalled(String featureName) {
		return null == getFeature(featureName);
	}

	public static FeatureInfo getFeature(String featureName) {
		LCInfo lc = (LCInfo) DataPool.getComplexData(CONSTANTS_GLOBAL, LC_INFO);
		if (lc == null) {
			lc = new LCInfo(getLCHome());
			DataPool.setComplexData(CONSTANTS_GLOBAL, LC_INFO, lc);
		}
		FeatureInfo feature = lc.getFeature(featureName);
		return feature;
	}

	public static String[] getMemeberOfFeatureProfile(String featureName) {
		FeatureInfo feature = getFeature(featureName);
		String serverindexPathTemplete = "{0}/profiles/{1}/config/cells/{2}/nodes/{3}/serverindex.xml";
		String serverIndexFile = MessageFormat.format(serverindexPathTemplete,
				feature.getWasHome(), feature.getProfileName(), feature
						.getCellName(), feature.getNodeName());

		List<String> servers = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(serverIndexFile);

			Element rootElement = document.getDocumentElement();
			NodeIterator nodes = XPathAPI.selectNodeIterator(rootElement,
					"serverEntries/@serverName");
			String xpath = MessageFormat
					.format(
							"serverEntries[@serverName=''{0}'']/specialEndpoints[@endPointName=''SOAP_CONNECTOR_ADDRESS'']/endPoint/@port",
							feature.getServerName());
			Node soapPort = XPathAPI.selectSingleNode(rootElement, xpath);
			feature.setServerSOAP(soapPort.getTextContent());
			Node node = nodes.nextNode();
			while (node != null) {
				try {
					String result = node.getTextContent();
					servers.add(result);
				} catch (Exception e) {
				}
				node = nodes.nextNode();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// update feature member info
		// addMemberName(featureName, servers);

		String[] a = new String[servers.size()];
		return servers.toArray(a);
	}

	public static Map<String, List<String>> getMemberNameFromDM() {

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		CommandExec ct = CommandExec
				.create(ClusterConstant.TASK_DETECT_MEMBERNAME);
		String output = Constants.OUTPUT_ROOT + Constants.FS
				+ "detect_membername.txt";
		ct.setOutput(output);

		int executeCode = ct.execute();
		if (executeCode != 0) {
			logger.log(Level.SEVERE, "common.error.command.exit.abnormal",
					executeCode);
			return map;
		}

		List<String> features = Arrays.asList(new String[] {
				Constants.FEATURE_ACTIVITIES, Constants.FEATURE_BLOGS,
				Constants.FEATURE_COMMUNITIES, Constants.FEATURE_DOGEAR,
				Constants.FEATURE_HOMEPAGE, Constants.FEATURE_PROFILES });

		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					output), "UTF-8");
			BufferedReader br = new BufferedReader(read);
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = DM_MEMBER_NAME_REGEXP.matcher(line);
				if (m.matches()) {
					String memberName = m.group(1);
					String cluster = m.group(3);

					for (String feature : features) {
						if (getClusterName(feature).equals(cluster)) {
							List<String> members = map.get(feature);
							if (members == null) {
								members = new ArrayList<String>();
								map.put(feature, members);
							}
							members.add(memberName);
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
			e.printStackTrace();
		}

		for (String feature : map.keySet()) {
			addMemberName(feature, map.get(feature));
		}
		return map;
	}

	public static void refreshDMMemberNames() {
		Map<String, List<String>> fmn = new HashMap<String, List<String>>();
		DataPool.setComplexData(CONSTANTS_GLOBAL, FEATURE_MEMBER_NAME, fmn);
		getMemberNameFromDM();
	}

	@SuppressWarnings("unchecked")
	private static void addMemberName(String featureName, List<String> members) {
		// update feature member info
		Map<?, List<String>> featureMemberNames = (Map<?, List<String>>) DataPool
				.getComplexData(CONSTANTS_GLOBAL, FEATURE_MEMBER_NAME);

		Map<String, List<String>> fmn = new HashMap<String, List<String>>();
		if (featureMemberNames != null) {
			for (Object key : featureMemberNames.keySet()) {
				fmn.put((String) key, featureMemberNames.get(key));
			}
		}

		List<String> memberNames = fmn.get(featureName);
		if (memberNames == null) {
			memberNames = new ArrayList<String>();
			fmn.put(featureName, memberNames);
		}
		for (String member : members) {
			if (!memberNames.contains(member)) {
				memberNames.add(member);
			}
		}
		DataPool.setComplexData(CONSTANTS_GLOBAL, FEATURE_MEMBER_NAME, fmn);
	}

	/**
	 * Gest the suggested member name, the member name is unique on DM and in
	 * local node
	 * 
	 * @param featureName
	 * @return suggested member name
	 */
	@SuppressWarnings("unchecked")
	public static String getSuggestedMemberName(String featureName) {
		Map<?, List<String>> featureMemberNames = (Map<?, List<String>>) DataPool
				.getComplexData(CONSTANTS_GLOBAL, FEATURE_MEMBER_NAME);

		List<String> memberNames = featureMemberNames.get(featureName);
		String[] localMember = getMemeberOfFeatureProfile(featureName);

		List<String> allMember = new ArrayList<String>();
		allMember.addAll(memberNames);
		allMember.addAll(Arrays.asList(localMember));

		int max = 0;
		String prefix = featureName + MEMBER_NAME;
		for (String name : allMember) {
			if (name.startsWith(prefix) && name.length() > prefix.length()) {
				String index = name.substring(prefix.length());
				try {
					int i = Integer.parseInt(index);
					max = i > max ? i : max;
				} catch (NumberFormatException e) {
					// ignore
				}
			}
		}
		return prefix + ++max;
	}

	public static String getDMCellName() {
		CommandExec ct = CommandExec
				.create(ClusterConstant.TASK_DETECT_DMCELLNAME);
		String output = Constants.OUTPUT_ROOT + Constants.FS
				+ "detect_dmcellname.txt";
		ct.setOutput(output);

		int executeCode = ct.execute();
		if (executeCode != 0) {
			logger.log(Level.SEVERE, "common.error.command.exit.abnormal",
					executeCode);
			logger.log(Level.SEVERE, "cluster.severe.cannot_get_dm_cell");
			return null;
		}

		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					output), "UTF-8");
			BufferedReader br = new BufferedReader(read);
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = DM_CELL_NAME_REGEXP.matcher(line);
				if (m.matches()) {
					String cellName = m.group(1);
					return cellName;
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
			e.printStackTrace();
		}

		logger.log(Level.SEVERE, "cluster.severe.cannot_get_dm_cell");
		return null;
	}

	@SuppressWarnings("unchecked")
	public static String getFolderConfig(String configName, int scope) {
		Map<String, List<String>> map = (Map<String, List<String>>) DataPool
				.getComplexData(LCUtil.CONSTANTS_GLOBAL,
						LCUtil.FEATURE_CLUSTERED_NODES);

		String feature = DataPool.getValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_RUNNING_FEATURE);
		FeatureInfo featureInfo = getFeature(feature);

		String scopeValue = featureInfo.getCellName();
		if (scope >= ClusterConstant.SCOPE_NODE) {
			scopeValue = scopeValue + "," + featureInfo.getNodeName();
		}
		if (scope >= ClusterConstant.SCOPE_SERVER) {
			scopeValue = scopeValue + "," + featureInfo.getServerName();
		}
		DataPool.setValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_scope, scopeValue);

		String taskId = ClusterConstant.TASK_GET_WS_VARAIBLE_FROM_DM;
		
		CommandExec ct = CommandExec.create(taskId);
		String output = Constants.OUTPUT_ROOT + Constants.FS
				+ "getWsVariable.txt";
		ct.setOutput(output);
		DataPool.setValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_PROFILE_HOME, LCUtil.getWASProfileHome());
		int executeCode = ct.execute();
		if (executeCode != 0) {
			logger.log(Level.SEVERE, "common.error.command.exit.abnormal",
					executeCode);
			return null;
		}
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					output), "UTF-8");
			BufferedReader br = new BufferedReader(read);
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = WS_VARIABLE_REGEXP.matcher(line);
				if (m.matches()) {
					String varName = m.group(1);
					String varValue = m.group(2);
					logger.log(Level.FINER, "cluster.finer.available_variable",
							new String[] { varName, varValue });
					if (varName.equals(configName)) {
						read.close();
						return varValue;
					}
				}
			}

			read.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
			e.printStackTrace();
		}
		return null;
	}

	public static String getWASProfileHome() {
		FeatureInfo feature = getFeature(DataPool.getValue(
				ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_RUNNING_FEATURE));
		String profile = feature.getProfileName();
		if (profile == null) {
			return null;
		}
		String profileHome = feature.getWasHome() + Constants.FS + "profiles"
				+ Constants.FS + profile;
		DataPool.setValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_PROFILE_HOME, profileHome);
		return profileHome;
	}
}
