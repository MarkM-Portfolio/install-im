/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                  */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.depcheck.DepChecker;
import com.ibm.lconn.wizard.common.depcheck.ProductInfo;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.backend.Checker;
import com.ibm.lconn.wizard.dbconfig.interfaces.impl.DatabaseOperation;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * @deprecated
 */
public class DBConsoleLauncher {
	public static final Logger logger = LogUtil
			.getLogger(DBConsoleLauncher.class);
	private static InputStreamReader is;
	private static BufferedReader br;
	private static final String option_one = "1";
	private static final String option_two = "2";
	private static final String option_three = "3";
	private static final String option_four = "4";
	private static final String option_five = "5";
	private static final String option_six = "6";
	private static final String YES = "y";
	private static final String NO = "n";
	private static String dbVersion="";

	public static String getDbVersion(String dbType) {
		if (dbVersion.equals("")){
			//Get the database version
			Map<String, ProductInfo> detectedDBMap = null;
			DepChecker dc = new DepChecker(DepChecker.PRODUCT_DB, CommonHelper.getPlatformType());
			detectedDBMap = dc.check();
			dbVersion = detectedDBMap.get(dbType).getVersion();
		}
		return dbVersion;
	}

	public static void main(String[] args) {
		TestDataOffer.setLocale();
		logger.log(Level.INFO, "dbconfig.info.console.start");

		System.out.println(Messages.getString("dbWizard.console.description"));

		openInput();

		String actionId = getActionIdInput();
		if (actionId == null)
			return;
		logger.log(Level.INFO, Messages.getString("dbWizard.action.db."
				+ actionId + ".name"));

		String dbType = getDBTypeInput();
		if (dbType == null)
			return;

		String dbVersion = getDbVersion(dbType);
		if (dbVersion == null)
			return;
		String dbInstallDir = getDBLocationInput(dbType);
		if (dbInstallDir == null)
			return;

		String dbInstanceName = getDBInstanceInput(dbType, dbInstallDir);
		if (dbInstanceName == null)
			return;

		List<String> features = getFeatureInput(actionId, dbType, dbInstallDir,
				dbInstanceName);
		if (features == null)
			return;

		Map<String, String> createDbFilePath = null;
		Map<String, String> dbUserPassword = null;
		if (Constants.OPERATION_TYPE_CREATE_DB.equals(actionId)) {
			if (!Constants.DB_DB2.equals(dbType)) {
				dbUserPassword = getPasswordInput(features);
				if (dbUserPassword == null)
					return;
			}

			if (Constants.DB_SQLSERVER.equals(dbType)) {
				createDbFilePath = getPathInput(features);
				if (createDbFilePath == null)
					return;
			}
		}

		closeInput();

		System.out.println(summary(actionId, dbType, dbInstallDir,
				dbInstanceName, features, dbUserPassword, createDbFilePath));
		logger.log(Level.INFO, "dbconfig.info.silent.summary", summary(
				actionId, dbType, dbInstallDir, dbInstanceName, features,
				dbUserPassword, createDbFilePath));
		logger.log(Level.INFO, "dbconfig.info.silent.executing");
		System.out.println("executing...");
//		CommandResultInfo result = new DatabaseOperation().execute(dbType, dbVersion, 
//				dbInstallDir, dbInstanceName, dbUserPassword, createDbFilePath,
//				CommonHelper.getPlatformType(), features, null, actionId, null);
//		System.out.println(result.getExitMessage());
//		logger.log(Level.INFO, "dbconfig.info.silent.result", result
//				.getExitMessage());
	}

	private static void openInput() {
		is = new InputStreamReader(System.in);
		br = new BufferedReader(is);
	}

	private static void closeInput() {
		try {
			is.close();
			br.close();
		} catch (IOException e) {
			System.out.println(Messages
					.getString("dbWizard.console.error.stream.close"));
			e.printStackTrace();
		}
	}

	/*
	 * Console format: label string:{wait for your input}
	 */
	private static String getInput(String label) {
		System.out.println(label + ":");
		String value = null;
		try {
			value = br.readLine();
		} catch (IOException e) {
			System.out.println(Messages
					.getString("dbWizard.console.error.stream.read"));
			e.printStackTrace();
		}
		return value.trim();
	}

	private static String getActionIdInput() {
		String actionId = null;
		while (actionId == null) {
			String input = getInput("input action [1]create, [2]delete, [any other]exit");
			if (option_one.equals(input))
				actionId = Constants.OPERATION_TYPE_CREATE_DB;
			else if (option_two.equals(input))
				actionId = Constants.OPERATION_TYPE_DROP_DB;
			else
				return null;
		}
		return actionId;
	}

	private static String getDBTypeInput() {
		String dbType = null;
		while (dbType == null) {
			String input = getInput("select database [1]db2, [2]oracle,[3]sqlserver, [any other]exit");
			if (option_one.equals(input))
				dbType = Constants.DB_DB2;
			else if (option_two.equals(input))
				dbType = Constants.DB_ORACLE;
			else if (option_three.equals(input))
				dbType = Constants.DB_SQLSERVER;
			else
				return null;
		}

		return dbType;
	}

	private static String getDBLocationInput(String dbType) {
		String dbInstallDir = null;

		DepChecker dc = new DepChecker(DepChecker.PRODUCT_DB, CommonHelper
				.getPlatformType());
		Map<String, ProductInfo> detectedDBMap = dc.check();
		if (null != detectedDBMap) {
			ProductInfo pi = detectedDBMap.get(dbType);
			if (null != pi)
				dbInstallDir = pi.getInstallLoc();
		}

		if (dbInstallDir != null && !"".equals(dbInstallDir.trim())) {
			String input = getInput("detect the dir \"" + dbInstallDir
					+ "\", use it?(y/n)");
			if ("y".equals(input.trim())) {
				if (!validateDBHome(dbType, dbInstallDir)) {
					System.out.println("invalid dir");
					dbInstallDir = null;
				}
			}
		} else {
			dbInstallDir = null;
		}
		while (dbInstallDir == null) {
			String input = getInput("input database location");
			if (!validateDBHome(dbType, input)) {
				System.out.println("invalid dir");
				dbInstallDir = null;
			} else {
				dbInstallDir = input;
			}
		}
		return dbInstallDir;
	}

	private static String getDBInstanceInput(String dbType, String dbInstallDir) {
		String dbInstanceName = null;
		while (dbInstanceName == null) {
			String input = getInput("instance:");
			if (!validateDBInstance(dbType, dbInstallDir, input.trim())) {
				System.out.println("invalid instance.");
				continue;
			}
			dbInstanceName = input;
		}

		return dbInstanceName;
	}

	private static List<String> getFeatureInput(String actionId, String dbType,
			String dbInstallDir, String dbInstanceName) {
		String featureStr = getInput("select features, [1]activities, [2]blogs, [3]communities, [4]dogear, [5]profiles, [6]homepage");
		List<String> features = getValidFeatures(actionId, dbType,
				dbInstallDir, dbInstanceName, featureStr);
		if (features == null) {
			System.out.println("no feature selected.");
			return null;
		}

		return features;
	}

	private static Map<String, String> getPasswordInput(List<String> features) {
		logger.log(Level.INFO, "dbconfig.info.silent.validation.password");
		Map<String, String> map = new HashMap<String, String>();
		String useSame = getInput("use same?(y/n)");
		if (YES.equalsIgnoreCase(useSame)) {
			String password = getPassword("");
			if (password == null)
				return null;
			for (String feature : features) {
				map.put(feature, password);
			}
		} else {
			for (String feature : features) {
				String password = getPassword(feature);
				if (password == null)
					return null;
				map.put(feature, password);
			}
		}

		logger.log(Level.INFO,
				"dbconfig.info.silent.validation.password.succeed");
		return map;
	}

	private static String getPassword(String feature) {
		String password = null;
		while (password == null) {
			String input = getInput(feature + " password");
			if (input == null || "".equals(input)) {
				System.out.println("invalid password.");
			} else {
				password = input;
			}
		}

		String confirm = getInput(feature);
		if (!password.equals(confirm)) {
			password = getPassword(feature);
		}

		return password;
	}

	private static Map<String, String> getPathInput(List<String> features) {
		logger.log(Level.INFO, "dbconfig.info.silent.validation.path");

		Map<String, String> map = new HashMap<String, String>();
		String useSame = getInput("use same?(y/n)");
		if (YES.equalsIgnoreCase(useSame)) {
			String path = null;
			while (path == null) {
				path = getInput("path");
				if (!(new File(path).exists())) {
					System.out.println("invalid path");
					path = null;
				}
			}

			for (String feature : features) {
				map.put(feature, path);
			}
		} else {
			for (String feature : features) {
				String path = null;
				while (path == null) {
					path = getInput("path");
					if (!(new File(path).exists())) {
						System.out.println("invalid path");
						path = null;
					}
				}
				map.put(feature, path);
			}
		}

		logger.log(Level.INFO, "dbconfig.info.silent.validation.path.succeed");
		return map;
	}

	private static boolean validateDBHome(String dbType, String dbHome) {
		logger.log(Level.INFO, "dbconfig.info.silent.validation.dbLocation");
		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(),dbType);
		if (!DC.validateInstallLoc(getDbVersion(dbType))) {
			logger.log(Level.INFO,
					"dbconfig.info.silent.validation.dbLocation.fail");
			return false;
		}
		logger.log(Level.INFO,
				"dbconfig.info.silent.validation.dbLocation.succeed");
		return true;
	}

	private static boolean validateDBInstance(String dbType, String dbHome,
			String instance) {
		logger.log(Level.INFO, "dbconfig.info.silent.validation.instance");
		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(),
				dbType);
		DC.setInstance(instance);
		if (!DC.validateInstance(getDbVersion(dbType))) {
			logger.log(Level.INFO,
					"dbconfig.info.silent.validation.instance.fail");
			return false;
		}
		logger.log(Level.INFO,
				"dbconfig.info.silent.validation.instance.succeed");
		return true;
	}

	private static List<String> getValidFeatures(String action, String dbType,
			String dbHome, String instance, String featureStr) {
		logger.log(Level.INFO, "dbconfig.info.silent.validation.features");
		Checker dbCheck = Checker.getChecker(dbHome, CommonHelper
				.getPlatformType(), dbType);
		dbCheck.setInstance(instance);
		String[] installedFeatures = dbCheck.getFeatures(getDbVersion(dbType));
		List<String> selectedFeatures = getFeatures(featureStr);
		List<String> validFeatures = getFeatures(featureStr);

		boolean isCreation = Constants.OPERATION_TYPE_CREATE_DB.equals(action) ? true
				: false;

		for (String feature : selectedFeatures) {
			if (contain(feature, installedFeatures)) {
				if (isCreation) {
					validFeatures.remove(feature);
					System.out.println(feature + " is already installed.");
					logger
							.log(
									Level.INFO,
									"dbconfig.info.silent.validation.features.installed",
									feature);
				}
			} else {
				if (!isCreation) {
					validFeatures.remove(feature);
					System.out.println(feature + " is already uninstalled.");
					logger
							.log(
									Level.INFO,
									"dbconfig.info.silent.validation.features.uninstalled",
									feature);
				}
			}
		}

		if (validFeatures == null || validFeatures.size() == 0) {
			logger
					.log(Level.INFO, "dbconfig.info.silent.no.features."
							+ action);
			validFeatures = null;
		}
		return validFeatures;
	}

	private static List<String> getFeatures(String featureStr) {
		String[] arr = featureStr.split(",");
		List<String> list = new ArrayList<String>();
		if (arr == null)
			return null;
		for (String feature : arr) {
			feature = feature.trim();
			if (option_one.equals(feature)) {
				list.add(Constants.FEATURE_ACTIVITIES);
			} else if (option_two.equals(feature)) {
				list.add(Constants.FEATURE_BLOGS);
			} else if (option_three.equals(feature)) {
				list.add(Constants.FEATURE_COMMUNITIES);
			} else if (option_four.equals(feature)) {
				list.add(Constants.FEATURE_DOGEAR);
			} else if (option_five.equals(feature)) {
				list.add(Constants.FEATURE_PROFILES);
			} else if (option_six.equals(feature)) {
				list.add(Constants.FEATURE_HOMEPAGE);
			} else {
				logger
						.log(
								Level.INFO,
								"dbconfig.info.silent.validation.features.name.invalid",
								feature);
			}
		}
		if (list.size() == 0)
			return null;
		return list;
	}

	private static boolean contain(String str, String[] arr) {
		if (arr == null)
			return false;
		for (String s : arr) {
			if (CommonHelper.equals(s, str))
				return true;
		}
		return false;
	}

	private static String summary(String actionId, String dbTypeId,
			String dbInstallDir, String dbInstanceName, List<String> features,
			Map<String, String> dbUserPassword, Map<String, String> path) {

		StringBuffer sb = new StringBuffer();
		sb.append(Messages
				.getString("dbWizard.summaryWizardPage.show.text.part1"));
		sb.append(
				Messages.getString("dbWizard.action.db." + actionId + ".name"))
				.append(".").append(Constants.CRLF);
		sb.append(Messages
				.getString("dbWizard.summaryWizardPage.show.text.part2"));
		sb.append(Messages.getString(dbTypeId + ".name")).append(".").append(
				Constants.CRLF);
		sb.append(Messages
				.getString("dbWizard.summaryWizardPage.show.text.part3"));
		sb.append(dbInstallDir).append(".").append(Constants.CRLF);
		sb.append(Messages
				.getString("dbWizard.summaryWizardPage.show.text.part4"));
		sb.append(dbInstanceName).append(".").append(Constants.CRLF);
		sb.append(Messages
				.getString("dbWizard.summaryWizardPage.show.text.part5"));
		for (String feature : features) {
			sb.append(" ").append(Messages.getString(feature + ".name"))
					.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(".").append(Constants.CRLF);
		sb.append(Constants.CRLF);
		for (String feature : features) {
			sb.append(Messages.getString(feature + ".name")).append(
					Constants.CRLF);
			sb
					.append(Messages
							.getString("dbWizard.summaryWizardPage.show.text.reuse.part1"));
			sb.append(Messages.getString(feature + ".dbName." + dbTypeId))
					.append(".").append(Constants.CRLF);
			sb
					.append(Messages
							.getString("dbWizard.summaryWizardPage.show.text.reuse.part2"));
			sb.append(Messages.getString(feature + ".dbUserName." + dbTypeId))
					.append(".").append(Constants.CRLF);
			if (dbTypeId.equals(Constants.DB_SQLSERVER)
					&& Constants.OPERATION_TYPE_CREATE_DB.equals(dbTypeId)) {
				sb
						.append(Messages
								.getString("dbWizard.summaryWizardPage.show.text.reuse.part3"));
				sb.append(path.get(feature)).append(".").append(Constants.CRLF);
			}
			sb.append(Constants.CRLF);
		}

		return sb.toString();
	}

}
