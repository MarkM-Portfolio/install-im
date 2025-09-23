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

package com.ibm.lconn.wizard.cluster.validation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.command.LogAnalysis;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.validator.AbstractValidator;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class DMValidator extends AbstractValidator {
	private static final Logger logger = LogUtil.getLogger(DMValidator.class);
	private int executeCode;
	private String clusterDMSoapName, clusterDMHostName, clusterDMProfileName,
			clusterDMWasUser, clusterDMWasPassword;

	private static final List<String> features = Collections
			.unmodifiableList(Arrays.asList(new String[] {
					Constants.FEATURE_ACTIVITIES, Constants.FEATURE_BLOGS,
					Constants.FEATURE_COMMUNITIES, Constants.FEATURE_DOGEAR,
					Constants.FEATURE_HOMEPAGE, Constants.FEATURE_PROFILES }));

	public DMValidator(String wasHome, String clusterDMSoapName,
			String clusterDMHostName, String clusterDMProfileName,
			String clusterDMWasUser, String clusterDMWasPassword) {

		this.clusterDMHostName = clusterDMHostName;
		this.clusterDMProfileName = clusterDMProfileName;
		this.clusterDMSoapName = clusterDMSoapName;
		this.clusterDMWasPassword = clusterDMWasPassword;
		this.clusterDMWasUser = clusterDMWasUser;
	}

	public DMValidator(String wasHome, String clusterDMSoapName,
			String clusterDMHostName, String clusterDMWasUser,
			String clusterDMWasPassword) {

		this.clusterDMHostName = clusterDMHostName;
		this.clusterDMSoapName = clusterDMSoapName;
		this.clusterDMWasPassword = clusterDMWasPassword;
		this.clusterDMWasUser = clusterDMWasUser;
		this.clusterDMProfileName = ClusterConstant.INPUT_clusterDMProfileName;
	}

	public DMValidator() {

		this.clusterDMHostName = ClusterConstant.INPUT_clusterDMHostName;
		this.clusterDMSoapName = ClusterConstant.INPUT_clusterDMSoapPort;
		this.clusterDMWasPassword = ClusterConstant.INPUT_clusterDMWasPassword;
		this.clusterDMWasUser = ClusterConstant.INPUT_clusterDMWasUser;
		this.clusterDMProfileName = ClusterConstant.INPUT_clusterDMProfileName;
	}

	protected int analyzeOutput(String file) {
		boolean value;
		Pattern[] p;
		// for process
		if (this.executeCode == -1) {
			logError("dm.process.not.start");
			return -1;
		}

		// for hostname
		p = new Pattern[] { Pattern
				.compile(".*java.net.UnknownHostException.*") };
		value = LogAnalysis.containsAnd(file, p);
		if (value) {
			logError("dm.host.invalid");
			return 1;
		}

		// for port
		p = new Pattern[] { Pattern.compile(".*ADMC0016E.*") };
		value = LogAnalysis.containsAnd(file, p);
		if (value) {
			logError("dm.port.invalid");
			return 2;
		}

		// for profile name
		p = new Pattern[] { Pattern.compile(".*WCMD0003E.*") };
		value = LogAnalysis.containsAnd(file, p);
		if (value) {
			logError("dm.profile.invalid");
			return 3;
		}

		// for username and password
		p = new Pattern[] { Pattern.compile(".*WASX7246E.*") };
		value = LogAnalysis.containsAnd(file, p);
		if (value) {
			logError("dm.account.invalid");
			return 4;
		}

		// pass
		getDMCellName(file);
		gatherClusterMember(file);
		gatherClusteredNodes(file);

		return 0;
	}

	private void getDMCellName(String file) {
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "UTF-8");
			BufferedReader br = new BufferedReader(read);
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = LCUtil.DM_CELL_NAME_REGEXP.matcher(line);
				if (m.matches()) {
					String cellName = m.group(1);
					logger.log(Level.INFO, "INPUT_clusterDMCellName="
							+ cellName);
					DataPool.setValue(ClusterConstant.WIZARD_ID_CLUSTER,
							ClusterConstant.INPUT_clusterDMCellName, cellName);
					break;
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
			e.printStackTrace();
		}
	}

	private void gatherClusterMember(String file) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "UTF-8");
			BufferedReader br = new BufferedReader(read);
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = LCUtil.DM_MEMBER_NAME_REGEXP.matcher(line);
				if (m.matches()) {
					String memberName = m.group(1);
					String cluster = m.group(3);

					for (String feature : features) {
						if (LCUtil.getClusterName(feature).equals(cluster)) {
							List<String> members = map.get(feature);
							if (members == null) {
								members = new ArrayList<String>();
								map.put(feature, members);
							}
							members.add(memberName);
							logger.log(Level.INFO,
									"cluster.info.found_clustered_member",
									new Object[] { feature, memberName });
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
			e.printStackTrace();
		}
		DataPool.setComplexData(LCUtil.CONSTANTS_GLOBAL,
				LCUtil.FEATURE_MEMBER_NAME, map);
	}

	private void gatherClusteredNodes(String file) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "UTF-8");
			BufferedReader br = new BufferedReader(read);
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = LCUtil.WS_VARIABLE_REGEXP.matcher(line);
				if (m.matches()) {
					String cluster = m.group(1);
					String nodeName = m.group(2);

					for (String feature : features) {
						if (LCUtil.getClusterName(feature).equals(cluster)) {
							List<String> nodes = map.get(feature);
							if (nodes == null) {
								nodes = new ArrayList<String>();
								map.put(feature, nodes);
							}
							nodes.add(nodeName);
							logger.log(Level.INFO,
									"cluster.info.found_clustered_node",
									new Object[] { feature, nodeName });
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "common.error.log.read", e);
			e.printStackTrace();
		}
		DataPool.setComplexData(LCUtil.CONSTANTS_GLOBAL,
				LCUtil.FEATURE_CLUSTERED_NODES, map);
	}

	protected String runCommand() {
		CommandExec ct = CommandExec.create(ClusterConstant.TASK_VALIDATE_DM);
		String output = Constants.OUTPUT_ROOT + Constants.FS
				+ "validate_dm.txt";
		ct.setOutput(output);
		this.executeCode = ct.execute();
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.common.Validator#validate()
	 */
	public int validate() {
		if (eval(clusterDMHostName) == null
				|| "".equals(eval(clusterDMHostName.trim()))) {
			logError("empty.input");
			return 1;
		}

		if (eval(clusterDMProfileName) == null
				|| "".equals(eval(clusterDMProfileName.trim()))) {
			logError("empty.input");
			return 2;
		}

		if (eval(clusterDMSoapName) == null
				|| "".equals(eval(clusterDMSoapName.trim()))) {
			logError("empty.input");
			return 3;
		}

		if (eval(clusterDMWasPassword) == null
				|| "".equals(eval(clusterDMWasPassword.trim()))) {
			logError("empty.input");
			return 4;
		}

		if (eval(clusterDMWasUser) == null
				|| "".equals(eval(clusterDMWasUser.trim()))) {
			logError("empty.input");
			return 5;
		}

		String file = runCommand();
		return analyzeOutput(file);
	}

}
