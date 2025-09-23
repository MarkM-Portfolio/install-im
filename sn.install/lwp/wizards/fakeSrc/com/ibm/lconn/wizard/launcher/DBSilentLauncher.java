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
package com.ibm.lconn.wizard.launcher;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;
import com.ibm.lconn.wizard.common.DefaultValue;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.property.Property;
import com.ibm.lconn.wizard.common.property.PropertyLoader;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.DB2ConfigProp;
import com.ibm.lconn.wizard.dbconfig.backend.Checker;
import com.ibm.lconn.wizard.dbconfig.backend.SchemaVersionDetector;
import com.ibm.lconn.wizard.dbconfig.interfaces.impl.DatabaseOperation;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.tdipopulate.backend.JDBCConnectionInfo;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class DBSilentLauncher {
	private static final Logger logger = LogUtil.getLogger(DBSilentLauncher.class);
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("com.ibm.lconn.wizard.common.logging.messages");
	private static final String defaultResponse = Constants.RESPONSE_ROOT + Constants.FS + "dbWizard" + Constants.FS + "response.properties";
	private final static String DB_CONN_VALIDATOR_MAIN = "com.ibm.lconn.wizard.common.validator.DbConnectionValidatorMain";
	// private static final String UPGRADE_COMMUNITIES_UUID =
	// Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK;
	private static String response;
	private static Map<String, String> versions;
	private static Map<String, String> sqlPath;
	private static List<String> validFeatures;

	public static void main(String[] args) {
		TestDataOffer.setLocale();
		log(Level.INFO, "dbconfig.info.silent.start");

		Property property;
		if (args == null || args.length == 0) {
			response = defaultResponse;
		} else if (args.length == 1) {
			response = args[0];
		} else {
			log(Level.SEVERE, "dbconfig.severe.silent.input.argument.invalid");
			return;
		}

		if (!new File(response).exists()) {
			log(Level.SEVERE, "dbconfig.severe.silent.response.invalid", response);
			return;
		}

		log(Level.INFO, "dbconfig.info.silent.response", response);
		property = PropertyLoader.load(response);

		String actionId = property.getProperty("action").trim();
		if (!contain(actionId, new String[] { Constants.OPERATION_TYPE_CREATE_DB, Constants.OPERATION_TYPE_DROP_DB, Constants.OPERATION_TYPE_UPGRADE_DB })) {
			log(Level.SEVERE, "dbconfig.severe.silent.invalid.action", actionId);
			return;
		}
		log(Level.INFO, "dbconfig.info.action.db." + actionId + ".name");

		String dbType = property.getProperty("dbtype").trim();
		if (!contain(dbType, new String[] { Constants.DB_DB2, Constants.DB_ORACLE, Constants.DB_SQLSERVER })) {
			log(Level.SEVERE, "dbconfig.severe.silent.invalid.dbType", dbType);
			return;
		}

		String dbInstallDir = property.getProperty("dbHome").trim();

		if (!validateDBHome(dbType, dbInstallDir))
			return;
		String dbInstanceName = property.getProperty("dbInstance").trim();
		if (!validateDBInstance(dbType, dbInstallDir, dbInstanceName))
			return;

		List<String> features = getValidFeatures(actionId, dbType, dbInstallDir, dbInstanceName, property.getProperty("features").trim());
		if (features == null || features.size() == 0)
			return;

		validFeatures = features;

		Map<String, String> createDbFilePath = null;
		Map<String, String> dbUserPassword = null;
		if (Constants.OPERATION_TYPE_CREATE_DB.equals(actionId)||(Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)&& Constants.DB_ORACLE.equals(dbType))) {
			dbUserPassword = getPassword(features, property);
			if (!Constants.DB_DB2.equals(dbType) && dbUserPassword == null)
				return;
			createDbFilePath = getPath(features, property);
			if (Constants.DB_SQLSERVER.equals(dbType) && createDbFilePath == null)
				return;

			sqlPath = createDbFilePath;
		}

		JDBCConnectionInfo connInfo = new JDBCConnectionInfo();
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			for (String feature : features) {
				if (Constants.FEATURE_PROFILES.equals(feature) || Constants.FEATURE_COMMUNITIES.equals(feature)) {
					String port = property.getProperty("port").trim();
					String administrator = property.getProperty("administrator").trim();
					String adminPassword = property.getProperty("adminPassword").trim();
					String jdbcLibPath = null;

					if (!Constants.DB_SQLSERVER.equals(dbType)) {
						jdbcLibPath = DefaultValue.getJDBCLibraryPath(dbType, dbInstallDir);
					} else {
						// validate jdbcLibPath
						jdbcLibPath = property.getProperty("jdbcLibPath").trim();
						boolean valid = false;
						if (jdbcLibPath != null) {
							File jarFile = new File(jdbcLibPath);
							if (jarFile.exists() && jarFile.isFile()) {
								valid = true;
							}
						}
						if (!valid) {
							log(Level.SEVERE, ErrorMsg.getString("dbwizard.error.jdbclib.not.exist"));
							return;
						}
					}

					// load jdbc connection info
					connInfo.setHostName(Constants.LOCALHOST);
					connInfo.setPort(port);
					connInfo.setDbType(dbType);
					connInfo.setUsername(administrator);
					connInfo.setPassword(adminPassword);
					connInfo.setJdbcLibPath(jdbcLibPath);

					if (Constants.DB_ORACLE.equals(dbType)) {
						connInfo.setDbName(dbInstanceName);
					} else {
						connInfo.setDbName(Constants.featureDBMapping.get(dbType).get(feature));
					}

					if (!validateJDBCConnection(connInfo)) {
						return;
					}
				}
			}
		}

		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId) && validFeatures.contains(Constants.FEATURE_COMMUNITIES)) {
			connInfo.setContentStore(Constants.FEATURE_COMMUNITIES, property.getProperty("forum.contentStore").trim());
		}

		/*
		 * String upgradeCommunitiesUuid = property
		 * .getProperty(UPGRADE_COMMUNITIES_UUID); upgradeCommunitiesUuid =
		 * upgradeCommunitiesUuid == null ? Constants.BOOL_FALSE :
		 * upgradeCommunitiesUuid.trim();
		 */

		DatabaseOperation dbExec = new DatabaseOperation();
		/*
		 * dbExec.setProperties(
		 * Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK,
		 * upgradeCommunitiesUuid);
		 */

		String sum = null;
		try {
			sum = summary();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		log(Level.INFO, "dbconfig.info.silent.summary", sum);
		log(Level.INFO, "dbconfig.info.silent.executing");
		CommandResultInfo result = dbExec.execute(dbType, dbInstallDir, dbInstanceName, dbUserPassword, createDbFilePath, CommonHelper.getPlatformType(), features, versions, actionId, connInfo);
		log(Level.INFO, "dbconfig.info.silent.result", result.getExitMessage());
	}

	private static boolean validateJDBCConnection(JDBCConnectionInfo connInfo) {
		String port = connInfo.getPort();
		int i = -1;
		try {
			i = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			// ignore
		}
		if (i < 1 || i > 65535) {
			log(Level.SEVERE, ErrorMsg.getString("dbwizard.error.invalid.db.port"));
			return false;
		}

		// validate administrator name
		String dbAdmin = connInfo.getUsername();
		if (dbAdmin == null || "".equals(dbAdmin.trim())) {
			log(Level.SEVERE, ErrorMsg.getString("dbwizard.error.invalid.db.dbadmin"));
			return false;
		}
		String adminPwd = connInfo.getPassword();
		// validate administrator password
		if (adminPwd == null || "".equals(adminPwd.trim())) {
			log(Level.SEVERE, ErrorMsg.getString("dbwizard.error.invalid.db.adminpwd"));
			return false;
		}

		String dbType = connInfo.getDbType();
		String hostname = connInfo.getHostName();
		String jdbcLibPath = connInfo.getJdbcLibPath();
		String dbName = connInfo.getDbName();

		String javaExecutable = DefaultValue.getDefaultJREPath() + Constants.FS + "bin" + Constants.FS;
		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			javaExecutable = javaExecutable + Constants.JAVA_EXECUTABLE_WIN;
		} else {
			javaExecutable = javaExecutable + Constants.JAVA_EXECUTABLE;
		}
		String classpath = jdbcLibPath + Constants.PATH_SEPARATOR + Constants.WIZARD_JAR_LOC;

		ProcessBuilder pb = new ProcessBuilder(javaExecutable, "-classpath", classpath, DB_CONN_VALIDATOR_MAIN, dbType, hostname, port, dbName, dbAdmin, adminPwd);
		List<String> cmds = new ArrayList<String>();
		for (String cmd : pb.command()) {
			cmds.add(cmd);
		}
		cmds.set(cmds.size() - 1, "******");
		logger.log(Level.FINER, "validator.finer.db_validation_process_command", cmds);
		String dbUrl = DatabaseUtil.getDBUrl(dbType, hostname, port, dbName);
		logger.log(Level.INFO, "validator.info.db_url", dbUrl);
		String jdbcDriver = DatabaseUtil.getJDBCDriver(dbType);
		logger.log(Level.INFO, "validator.info.jdbc_driver", jdbcDriver);
		Process p = null;

		try {
			p = pb.start();
			p.waitFor();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "dbpopulation.severe.db_validation_process_fail", e);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "dbpopulation.severe.db_validation_process_fail", e);
		}

		if (p == null) {
			log(Level.SEVERE, Messages.getString("validator.cannot_validation_process.message"));
			return false;
		}

		logger.log(Level.FINER, "validator.finer.process_exit_value", new Integer(p.exitValue()));

		int exitValue = p.exitValue();
		if (exitValue == 0) {
			return true;
		}
		switch (p.exitValue()) {
		case 0:
			return true;
		case Constants.ERROR_NO_JDBC_DRIVER:
			log(Level.SEVERE, Messages.getString("validator.no_jdbc_driver.message", jdbcDriver));
			break;
		case Constants.ERROR_COMMUNICATION_ERROR:
			log(Level.SEVERE, Messages.getString("validator.communication_error.message", hostname, port));
			break;
		case Constants.ERROR_DB_NOT_EXIST:
			log(Level.SEVERE, Messages.getString("validator.db_not_exist.message", dbName));
			break;
		case Constants.ERROR_INVALID_AUTHENTICATION:
			log(Level.SEVERE, Messages.getString("validator.invalid_authentication.message"));
			break;
		case Constants.ERROR_UNKNOWN:
			log(Level.SEVERE, Messages.getString("validator.cannot_connect_db.message", dbUrl));
			break;
		default:
			log(Level.SEVERE, Messages.getString("validator.cannot_validation_process.message"));
		}

		return false;
	}

	private static boolean validateDBHome(String dbType, String dbHome) {
		log(Level.INFO, "dbconfig.info.silent.validation.dbLocation");
		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType);
		if (!DC.validateInstallLoc()) {
			log(Level.INFO, "dbconfig.info.silent.validation.dbLocation.fail");
			return false;
		}
		log(Level.INFO, "dbconfig.info.silent.validation.dbLocation.succeed");
		return true;
	}

	private static boolean validateDBInstance(String dbType, String dbHome, String instance) {
		log(Level.INFO, "dbconfig.info.silent.validation.instance");
		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType);
		DC.setInstance(instance);
		if (!DC.validateInstance()) {
			log(Level.INFO, "dbconfig.info.silent.validation.instance.fail");
			return false;
		}
		log(Level.INFO, "dbconfig.info.silent.validation.instance.succeed");
		return true;
	}

	private static List<String> getValidFeatures(String action, String dbType, String dbHome, String instance, String featureStr) {
		log(Level.INFO, "dbconfig.info.silent.validation.features");
		Checker dbCheck = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType);
		dbCheck.setInstance(instance);
		String[] installedFeatures = dbCheck.getFeatures();
		List<String> selectedFeatures = getFeatures(featureStr);
		List<String> validFeatures = getFeatures(featureStr);

		boolean isCreation = Constants.OPERATION_TYPE_CREATE_DB.equals(action) ? true : false;

		for (String feature : selectedFeatures) {
			if (contain(feature, installedFeatures)) {
				if (isCreation) {
					validFeatures.remove(feature);
					log(Level.INFO, "dbconfig.info.silent.validation.features.installed", feature);
				}
			} else {
				if (!isCreation) {
					validFeatures.remove(feature);
					log(Level.INFO, "dbconfig.info.silent.validation.features.uninstalled", feature);
				}
			}
		}

		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(action) && validFeatures != null && validFeatures.size() != 0) {
			versions = SchemaVersionDetector.getFeatureVersions(dbType, dbHome, instance, (String[]) validFeatures.toArray(new String[0]));
			List<String> temp = new ArrayList<String>(validFeatures);
			for (String f : temp) {
				if (Constants.VERSION_TOP.equals(versions.get(f))) {
					validFeatures.remove(f);
					log(Level.INFO, "dbconfig.info.silent.validation.features.topVersion", f);
				}
			}
		}

		if (validFeatures == null || validFeatures.size() == 0) {
			log(Level.INFO, "dbconfig.info.silent.no.features." + action);
			validFeatures = null;
		}

		return validFeatures;
	}

	private static Map<String, String> getPassword(List<String> features, Property prop) {
		String dbType = prop.getProperty("dbtype").trim();
		if (Constants.DB_DB2.equals(dbType))
			return null;

		log(Level.INFO, "dbconfig.info.silent.validation.password");
		Map<String, String> map = new HashMap<String, String>();
		for (String feature : features) {
			String password = prop.getProperty(feature + ".password").trim();
			if (password == null || "".equals(password.trim())) {
				log(Level.INFO, "dbconfig.info.silent.validation.password.empty", feature);
				return null;
			}
			map.put(feature, password);
		}
		log(Level.INFO, "dbconfig.info.silent.validation.password.succeed");
		return map;
	}

	private static Map<String, String> getPath(List<String> features, Property prop) {
		String dbType = prop.getProperty("dbtype").trim();
		if (!Constants.DB_SQLSERVER.equals(dbType))
			return null;

		log(Level.INFO, "dbconfig.info.silent.validation.path");

		Map<String, String> map = new HashMap<String, String>();
		for (String feature : features) {
			String path = prop.getProperty(feature + ".filepath").trim();
			if (!(new File(path).exists())) {
				log(Level.INFO, "dbconfig.info.silent.validation.path.fail", feature);
				return null;
			}
			map.put(feature, path);
		}

		return map;
	}

	private static List<String> getFeatures(String featureStr) {
		String[] arr = featureStr.split(",");
		List<String> list = new ArrayList<String>();
		if (arr == null)
			return null;
		for (String feature : arr) {
			feature = feature.trim();
			if (contain(feature, Constants.FEATURE_ALL)) {
				list.add(feature);
			} else {
				log(Level.INFO, "dbconfig.info.silent.validation.features.name.invalid", feature);
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

	private static String summary() {
		Property property = PropertyLoader.load(response);
		String actionId = property.getProperty("action").trim();
		String dbTypeId = property.getProperty("dbtype").trim();
		String dbInstallDir = property.getProperty("dbHome").trim();
		String dbInstanceName = property.getProperty("dbInstance").trim();
		if (CommonHelper.isEmpty(dbInstanceName))
			dbInstanceName = "";
		else if (!dbInstanceName.equals("\\") && dbInstanceName.startsWith("\\") && Constants.DB_SQLSERVER.equals(dbTypeId))
			dbInstanceName = dbInstanceName.substring(1);

		List<String> features = validFeatures;

		StringBuffer sb = new StringBuffer();
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part1"));
		sb.append(Messages.getString("dbWizard.action.db." + actionId + ".name")).append(".").append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part2"));
		sb.append(Messages.getString(dbTypeId + ".name")).append(".").append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part3"));
		sb.append(dbInstallDir).append(".").append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part4"));
		sb.append(dbInstanceName).append(".").append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part5"));
		if (features == null || features.size() == 0) {
			sb.append(" ").append("NONE");

			return sb.toString();
		}

		for (String feature : features) {
			sb.append(" ").append(Messages.getString(feature + ".name")).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(".").append(Constants.CRLF);
		sb.append(Constants.CRLF);

		for (String feature : features) {
			sb.append(Messages.getString(feature + ".name")).append(Constants.CRLF);
			sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1"));
			if (Constants.DB_ORACLE.equals(dbTypeId)) {
				sb.append(dbInstanceName);
			} else {
				sb.append(DB2ConfigProp.getProperty(feature + ".dbName." + dbTypeId).trim());
			}
			sb.append(Constants.CRLF);

			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
				if (feature.equals(Constants.FEATURE_COMMUNITIES)) {
					sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_c")).append(property.getProperty("forum.contentStore").trim()).append(Constants.UI_LINE_SEPARATOR);
				}
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_b"));
				String version = versions.get(feature);
				if (version.startsWith("1.0")) {
					version = Constants.VERSION_10X;
				}
				sb.append(version).append("->").append(Constants.VERSION_TOP).append(Constants.CRLF);
			} else {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a"));
				sb.append(DB2ConfigProp.getProperty(feature + ".dbUserName." + dbTypeId).trim()).append(Constants.CRLF);
			}

			if (dbTypeId.equals(Constants.DB_SQLSERVER) && actionId.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3"));
				sb.append(sqlPath.get(feature)).append(Constants.CRLF);
			}
			sb.append(Constants.CRLF);
		}

		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			if (features.contains(Constants.FEATURE_COMMUNITIES) || features.contains(Constants.FEATURE_PROFILES)) {
				String dbHost = Constants.LOCALHOST;
				String dbPort = property.getProperty("port").trim();
				String dbAdmin = property.getProperty("administrator").trim();
				// jdbc connection information
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbHost"));
				sb.append(dbHost);
				sb.append(Constants.CRLF);
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbPort"));
				sb.append(dbPort);
				sb.append(Constants.CRLF);
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbAdmin"));
				sb.append(dbAdmin);
				sb.append(Constants.CRLF);
				if (Constants.DB_SQLSERVER.equals(dbTypeId)) {
					String jdbcLib = property.getProperty("jdbcLibPath").trim();
					sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.jdbcLib"));
					sb.append(jdbcLib);
					sb.append(Constants.CRLF);
				}
			}
			/*
			 * if (features.contains(Constants.FEATURE_COMMUNITIES) &&
			 * versions.get(Constants.FEATURE_COMMUNITIES).startsWith( "1.0")) {
			 * // upgradeCommunitiesUuid task String upgradeCommunitiesUuid =
			 * property.getProperty( UPGRADE_COMMUNITIES_UUID).trim(); sb
			 * .append(Messages.getString(
			 * "dbWizard.summaryWizardPage.show.text.upgradeCommunitiesUuid"));
			 * sb .append(Messages
			 * .getString("dbWizard.CommunitiesOptionalTaskWizardPage.option." +
			 * upgradeCommunitiesUuid + ".label")); sb.append(Constants.CRLF); }
			 */
		}

		return sb.toString();
	}

	private static void log(Level level, String label, Object... params) {
		logger.log(level, label, params);

		if (params == null)
			try {
				System.out.println(RESOURCE_BUNDLE.getString(label));
			} catch (Exception e) {
				System.out.println(label);
			}
		else
			System.out.println(MessageFormat.format(RESOURCE_BUNDLE.getString(label), params));
	}

	/*
	 * private static void log(Level level, String message) { logger.log(level,
	 * message); System.out.println(message); }
	 */
}
