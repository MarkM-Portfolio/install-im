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
package com.ibm.lconn.wizard.dbconfig.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class UpgradePath {
	private static final Logger logger = LogUtil.getLogger(UpgradePath.class);
	protected static PropertyResourceBundle properties = (PropertyResourceBundle) ResourceBundle.getBundle("com.ibm.lconn.wizard.dbconfig.backend.cmdContent");

	public static final String UPGRADE_PATH = "upgrade.path";

	private String feature;
	private String dbType;
	private String featureVersion;
	private String dbVersion;

	public String getFeatureVersion() {
		return featureVersion;
	}

	public String getDbType() {
		return dbType;
	}

	public String getFeature() {
		return feature;
	}

	public UpgradePath(String feature, String featureVersion, String dbType, String dbVersion) {
		this.feature = feature;
		this.dbType = dbType;
		this.featureVersion = featureVersion;
		this.dbVersion = dbVersion;
	}

	public String[] getUpgradePath(String targetVersion) {
		// try to find best match upgrade path
		String key = null;
		String fullPathStr = null;
		if (dbVersion != null) {
			key = feature + "." + dbType + dbVersion + "." + featureVersion + "." + UPGRADE_PATH;
			fullPathStr = getProperty(key);
		}
		if (fullPathStr == null) {
			key = feature + "." + dbType + "." + featureVersion + "." + UPGRADE_PATH;
			fullPathStr = getProperty(key);
		}
		if (fullPathStr == null) {
			key = feature + "." + dbType + "." + UPGRADE_PATH;
			fullPathStr = getProperty(key);
		}
		if (fullPathStr == null) {
			key = feature + "." + UPGRADE_PATH;
			fullPathStr = getProperty(key);
		}
		if (fullPathStr == null) {
			fullPathStr = getProperty(UPGRADE_PATH);
		}

		logger.log(Level.INFO, "dbconfig.info.full_upgradepath", fullPathStr);
		String[] fullPath = fullPathStr.split(",");
		int beginIndex = -1;
		int endIndex = -1;
		for (int i = 0; i < fullPath.length; i++) {
			if (fullPath[i].equals(featureVersion)) {
				beginIndex = i;
			}
			if (fullPath[i].equals(targetVersion)) {
				endIndex = i;
				break;
			}
		}
		if (beginIndex == -1 || endIndex == -1) {
			logger.log(Level.SEVERE, "dbconfig.severe.no_upgradepath", new String[] { feature, dbType, featureVersion, targetVersion });
			return null;
		}

		String[] result = new String[endIndex - beginIndex + 1];
		for (int i = 0; i < result.length; i++) {
			result[i] = fullPath[beginIndex + i];
		}

		logger.log(Level.INFO, "dbconfig.info.upgradepath", new String[] { feature, dbType, featureVersion, targetVersion, Arrays.toString(result) });

		return result;
	}

	public String[] getUpgradeActions(String targetVersion, boolean optional) {
		String[] upgradePath = getUpgradePath(targetVersion);
		if (upgradePath == null) {
			return null;
		}

		List<String> actions = new ArrayList<String>();
		for (int i = 0; i < upgradePath.length - 1; i++) {
			// not including the target version
			actions.addAll(getUpgradeAction(upgradePath[i], optional));
		}
		return actions.toArray(new String[] {});
	}

	public String[] getUpgradeActions(String targetVersion) {
		return getUpgradeActions(targetVersion, false);
	}

	private List<String> getUpgradeAction(String version, boolean optional) {
		String actionStr = null;
		if (dbVersion != null) {
			actionStr = getProperty(feature + "." + dbType + dbVersion + ".upgrade." + version);
		}
		if (actionStr == null) {
			actionStr = getProperty(feature + "." + dbType + ".upgrade." + version);
		}
		if (actionStr == null) {
			actionStr = getProperty(feature + ".upgrade." + version);
		}

		String actionStrOptional = getProperty(feature + "." + dbType + ".upgrade." + version + ".optional");
		if (actionStrOptional == null) {
			actionStrOptional = getProperty(feature + ".upgrade." + version + ".optional");
		}
		if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
			actionStr = getProperty(Constants.FEATURE_ACTIVITIESTHEME + "." + dbType + ".upgrade");
		}
		if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
			actionStr = getProperty(Constants.FEATURE_BLOGSTHEME + "." + dbType + ".upgrade");
		}
		if(Constants.FEATURE_WIKISTHEME.equals(feature)){
			actionStr = getProperty(Constants.FEATURE_WIKISTHEME + "." + dbType + ".upgrade");
		}
		actionStr = actionStr == null ? "" : actionStr;
		actionStr = actionStrOptional == null ? actionStr : actionStr + "," + actionStrOptional;
		// if the actionStr is empty, returns an empty list
		if ("".equals(actionStr)) {
			return new ArrayList<String>();
		}
		return Arrays.asList(actionStr.split(","));
	}

	private String getProperty(String key) {
		String value = null;
		try {
			value = properties.getString(key);
		} catch (RuntimeException e) {
			// ignore
		}
		return value;
	}
	public static List<String> containJava(String dbType, String dbVersion, List<String> features, Map<String, String> featureVersions) {
		List<String> list = new ArrayList<String>();

//		if(features.contains(Constants.FEATURE_FORUM)){
//			features.remove(Constants.FEATURE_FORUM);
//		}
		for (String feature : features) {
			String featureVersion = featureVersions.get(feature);
			if (DBWizardInputs.fake) {
				featureVersion = Constants.VERSION_300;
			}
			UpgradePath upgradePath = new UpgradePath(feature, featureVersion, dbType, dbVersion);
			String[] actions = upgradePath.getUpgradeActions(Constants.VERSION_TOP);
			for (String action : actions) {
				if (action.startsWith(Task.JAVA_CMD)) {
					list.add(feature);
					break;
				}
			}
		}
		return list;
	}
	public static List<String> containJava(Checker checker, String dbType, List<String> features, Map<String, String> featureVersions) {
		List<String> list = new ArrayList<String>();

		String dbVersion = checker.getVersionInfo();
		if (dbVersion != null) {
			dbVersion = dbVersion.split(" ")[1];
			dbVersion = dbVersion.replaceFirst("\\.", "");
			dbVersion = dbVersion.split("\\.")[0];
		}
//		if(features.contains(Constants.FEATURE_FORUM)){
//			features.remove(Constants.FEATURE_FORUM);
//		}
		for (String feature : features) {
			// for each feature get upgrade actions
			String featureVersion = featureVersions.get(feature);
			if (DBWizardInputs.fake) {
				featureVersion = Constants.VERSION_300;
			}
			UpgradePath upgradePath = new UpgradePath(feature, featureVersion, dbType, dbVersion);
			String[] actions = upgradePath.getUpgradeActions(Constants.VERSION_TOP);
			for (String action : actions) {
				// if action needs java command, show the
				// JDBC_CONNECTION_INFO panel
				if (action.startsWith(Task.JAVA_CMD)) {
					list.add(feature);
					break;
				}
			}

		}

//		features.add(Constants.FEATURE_FORUM);
		
		return list;
	}

	@Override
	public String toString() {
		return "dbtype: " + dbType + " dbVersion: " + dbVersion + " feature: " + feature + " featureVersion: " + featureVersion;
	}
	public static void main(String[] a) {
		System.out.println("a" + null + "b");
	}
}
