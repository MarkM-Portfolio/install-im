/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                   */
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
import com.ibm.lconn.wizard.dbconfig.interfaces.DbCreationInterface;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
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
	private static String dbType;
	private static String dbVersion;
	private static String dbInstallDir;
	private static String dbInstanceName;
	private static String PDBNameValue;
	private static String dbaPasswordValue;
	private static String dbaUserNameValue;
	private static boolean isRunAsSYSDBA;

	private static String actionId;
	private static Map<String, String> dbUpgradeVersions;
	private static Map<String, String> sqlPath;
	private static List<String> validFeatures;
	public static boolean executeFlag = true;
	private static Map<String, String> createDbFilePath;
	private static Map<String, String> dbUserPassword;
	private static boolean isJDBCNeeded;
	private static String port;
	private static String jdbcLibPath;
	private static String administrator;
	private static String adminPassword;
	private static JDBCConnectionInfo connInfo;
	private static JDBCConnectionInfo profileConnInfo;
	private static String profileDbHostName;
	private static String profileDbPort;
	private static String profileDbAdmin;
	private static String profileDbPassword;
	private static String profileDbInstanceName;
	private static String storyLifetimeInDays;
	private static DbCreationInterface DBExe = DBWizard.getDBCreationInterface();
	private static Map<String,List<List<String>>> commands;
	private static Map<String,List<String>> sqlScripts;
	private static Map<String, CommandResultInfo> resultMap = new HashMap<String, CommandResultInfo>();
	private static String logPath;
	private static String SPACE = " ";
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

		actionId = property.getProperty("action").trim();
		if (!contain(actionId, new String[] { Constants.OPERATION_TYPE_CREATE_DB, Constants.OPERATION_TYPE_DROP_DB, Constants.OPERATION_TYPE_UPGRADE_DB })) {
			log(Level.SEVERE, "dbconfig.severe.silent.invalid.action", actionId);
			return;
		}
		log(Level.INFO, "dbconfig.info.action.db." + actionId + ".name");

		dbType = property.getProperty("dbtype").trim();
		if (!contain(dbType, new String[] { Constants.DB_DB2, Constants.DB_ORACLE, Constants.DB_SQLSERVER })) {
			log(Level.SEVERE, "dbconfig.severe.silent.invalid.dbType", dbType);
			return;
		}

		dbVersion = property.getProperty("dbVersion").trim();
		dbInstallDir = property.getProperty("dbHome").trim();

		if (!validateDbVersion(dbType, dbInstallDir, dbVersion)) {
			log(Level.SEVERE, "dbconfig.info.silent.validation.dbVersion.fail");
			return;
		}
		if (!validateDBHome(dbType, dbInstallDir))
			return;
		
		dbInstanceName = property.getProperty("dbInstance").trim();
	
//#Oracle12C_PDB_disable#		if (Constants.DB_ORACLE.equals(dbType)) {
//#Oracle12C_PDB_disable#		PDBNameValue = property.getProperty("PDBNameValue").trim();
//#Oracle12C_PDB_disable#		dbaPasswordValue = property.getProperty("dbaPasswordValue").trim();
//#Oracle12C_PDB_disable#		dbaUserNameValue = property.getProperty("dbaUserNameValue").trim();
//#Oracle12C_PDB_disable#		isRunAsSYSDBA    = Boolean.getBoolean(property.getProperty("isRunAsSYSDBA").trim());
//#Oracle12C_PDB_disable#		}
		
		if (!validateDBInstance(dbType, dbInstallDir, dbInstanceName))
			return;
//#Oracle12C_PDB_disable# 		validFeatures = getValidFeatures(actionId, dbType, dbInstallDir, dbInstanceName,PDBNameValue,dbaPasswordValue, dbVersion, property.getProperty("features").trim());
		validFeatures = getValidFeatures(actionId, dbType, dbInstallDir, dbInstanceName,dbVersion, property.getProperty("features").trim());
		if (validFeatures == null || validFeatures.size() == 0)
			return;

		if (Constants.OPERATION_TYPE_CREATE_DB.equals(actionId)) {

			dbUserPassword = getPassword(validFeatures, property);
			if (!Constants.DB_DB2.equals(dbType) && dbUserPassword == null)
				return;
			createDbFilePath = getPath(validFeatures, property);
			if (Constants.DB_SQLSERVER.equals(dbType) && createDbFilePath == null)
				return;

			sqlPath = createDbFilePath;
		}
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)&& validFeatures.contains(Constants.FEATURE_HOMEPAGE)&& Constants.DB_SQLSERVER.equals(dbType)) {
			String homepageFilePath = property.getProperty(Constants.FEATURE_HOMEPAGE + ".filepath");
			String homepagePassword = property.getProperty(Constants.FEATURE_HOMEPAGE + ".password");
			if(homepageFilePath == null || "".equals(homepageFilePath)){
				return;
			}else{
				System.setProperty("homepage_upgradedb_file_path", homepageFilePath.trim());
			}	
			if(homepagePassword == null || "".equals(homepagePassword)){
				return;
			}else{
				System.setProperty("homepage_upgradedb_password", homepagePassword.trim());
			}	
			
		}
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)&& validFeatures.contains(Constants.FEATURE_COMMUNITIES)&& Constants.DB_ORACLE.equals(dbType)) {
			String communitiesPassword = property.getProperty(Constants.FEATURE_COMMUNITIES + ".password");
			if(communitiesPassword == null || "".equals(communitiesPassword)){
				return;
			}else{
				System.setProperty("communities_upgradedb_password", communitiesPassword.trim());
			}	
		}
		connInfo = new JDBCConnectionInfo();
		profileConnInfo= new JDBCConnectionInfo();
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			for (String feature : validFeatures) {
				if (Constants.FEATURE_HOMEPAGE.equals(feature)){
					isJDBCNeeded = true;
					port = property.getProperty("port").trim();
					administrator = property.getProperty("administrator").trim();
					adminPassword = property.getProperty("adminPassword").trim();
					jdbcLibPath = null;

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
							logError(Level.SEVERE, "dbwizard.error.jdbclib.not.exist");
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
					
//					if (Constants.FEATURE_HOMEPAGE.equals(feature)){
//						profileDbHostName = property.getProperty("profiles.db.hostname").trim();
//						profileDbPort = property.getProperty("profiles.db.port").trim();
//						profileDbAdmin = property.getProperty("profiles.db.admin").trim();
//						profileDbPassword = property.getProperty("profiles.db.adminPassword").trim();
//						profileDbInstanceName = property.getProperty("profiles.db.name").trim();
//						storyLifetimeInDays=property.getProperty("storyLifetimeInDays").trim();
//						if(storyLifetimeInDays == null || "".equals(storyLifetimeInDays)){
//							return;
//						}else{
//							System.setProperty("story_life_time_in_days", storyLifetimeInDays);
//						}	
//						profileConnInfo.setHostName(profileDbHostName);
//						profileConnInfo.setPort(profileDbPort);
//						profileConnInfo.setDbType(dbType);
//						profileConnInfo.setUsername(profileDbAdmin);
//						profileConnInfo.setJdbcLibPath(jdbcLibPath);
//						profileConnInfo.setPassword(profileDbPassword);
//						if (Constants.DB_ORACLE.equals(dbType)){
//							profileConnInfo.setDbName(profileDbInstanceName);
//						}else{
//							profileConnInfo.setDbName(Constants.featureDBMapping.get(dbType).get(feature));
//						}
//					}

					if (!validateJDBCConnection(connInfo)) {
						return;
					}
				}
			}
		}

//		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId) && validFeatures.contains(Constants.FEATURE_COMMUNITIES)) {
//			connInfo.setContentStore(Constants.FEATURE_COMMUNITIES, property.getProperty("forum.contentStore").trim());
//		}

		/*
		 * String upgradeCommunitiesUuid = property
		 * .getProperty(UPGRADE_COMMUNITIES_UUID); upgradeCommunitiesUuid =
		 * upgradeCommunitiesUuid == null ? Constants.BOOL_FALSE :
		 * upgradeCommunitiesUuid.trim();
		 */

//		DatabaseOperation dbExec = new DatabaseOperation();
		/*
		 * dbExec.setProperties(
		 * Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK,
		 * upgradeCommunitiesUuid);
		 */

//		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)
//				&& validFeatures.contains(Constants.FEATURE_COMMUNITIES)){
//			if(validFeatures.contains(Constants.FEATURE_ACTIVITIES) && !validFeatures.contains(Constants.FEATURE_ACTIVITIESTHEME)){
//				validFeatures.add(Constants.FEATURE_ACTIVITIESTHEME);	
//			}
//			if(validFeatures.contains(Constants.FEATURE_BLOGS) && !validFeatures.contains(Constants.FEATURE_BLOGSTHEME)){
//				validFeatures.add(Constants.FEATURE_BLOGSTHEME);	
//			}
//			if(validFeatures.contains(Constants.FEATURE_WIKIS) && !validFeatures.contains(Constants.FEATURE_WIKISTHEME)){
//				validFeatures.add(Constants.FEATURE_WIKISTHEME);	
//			}
//		}
		sqlScripts = getExecuteSQLScripts();
//#Oracle12C_PDB_disable#		commands = DBExe.getExecuteCommands(dbType, dbVersion, dbInstallDir, dbInstanceName,PDBNameValue,dbaPasswordValue, dbaUserNameValue, isRunAsSYSDBA,dbUserPassword, createDbFilePath, CommonHelper
		commands = DBExe.getExecuteCommands(dbType, dbVersion, dbInstallDir, dbInstanceName,dbUserPassword, createDbFilePath, CommonHelper
				.getPlatformType(), validFeatures, dbUpgradeVersions, actionId, connInfo, profileConnInfo);
		String sum = null;
		try {sum = summary();} catch (Exception e) {e.printStackTrace(); return; }
		log(Level.INFO, "dbconfig.info.silent.summary", sum);
		
		execute();
		
		try {sum = finish();} catch (Exception e) {e.printStackTrace(); return; }
		log(Level.INFO, "dbconfig.info.silent.summary", sum);
	}
	private static void execute(){
		log(Level.INFO, "dbconfig.info.silent.executing");
		CommandResultInfo result = null;
		for(String feature : validFeatures){
			List<List<String>> cmds = commands.get(feature);
			for(List<String> cmd : cmds){
				String sqlFileName = CommonHelper.getSqlFileName(cmd, dbType);
                if (executeFlag == true) {
                	logPath = DBExe.createLogForTask(feature, sqlFileName);
//#Oracle12C_PDB_disable#                	result = DBExe.execute(dbType, dbVersion, dbInstallDir, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,
                	result = DBExe.execute(dbType, dbVersion, dbInstallDir, dbInstanceName,
                			 dbUserPassword, createDbFilePath, CommonHelper.getPlatformType(), feature, 
                			 dbUpgradeVersions, actionId, connInfo, cmd, validFeatures, profileConnInfo);
                	if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)){
		                if (result.getExecState() != 0) {
		                	executeFlag = false;
		                	result.setBlockedFeature(feature);
		                	logger.log(Level.WARNING, Messages.getString("dbWizard.silent.exitMessage.detail", result.getExitMessage(), feature.substring(0,1).toUpperCase() + feature.substring(1), sqlFileName));
		                	result.setExitMessage(Messages.getString("dbWizard.executionWizardPage.exitMessage.detail", result.getExitMessage(), feature.substring(0,1).toUpperCase() + feature.substring(1), sqlFileName));
		                }
                	}
                }else{
                	result.getFeatures().add(feature);
                	if(null != result.getBlockedFeature() && null != result.getFeatures()){
                		result.getFeatures().remove(result.getBlockedFeature());
                	}
                }
                
                resultMap.put(feature + "_" + sqlFileName, result);
            }
		}
	}
	
	private static String summary() {
		StringBuffer sb = new StringBuffer();
		sb.append(getHeader());
		for (String feature : validFeatures) {
				if(feature.indexOf("theme") == -1){
			sb.append(Messages.getString(feature + ".name"));
			sb.append(Constants.CRLF);
			sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1")).append(SPACE);
			if (Constants.DB_ORACLE.equals(dbType)) {
				sb.append(dbInstanceName);
			} else {
				sb.append(DB2ConfigProp.getProperty(feature + ".dbName." + dbType));
			}
			sb.append(Constants.CRLF);
			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_b")).append(SPACE);
				String version = dbUpgradeVersions.get(feature);
				if (version.startsWith("1.0"))
					version = Constants.VERSION_10X;
				sb.append(version).append("->").append(Constants.VERSION_TOP);
				sb.append(Constants.CRLF);
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_1")).append(SPACE);
				List<String> elements = sqlScripts.get(feature);
				for(String element : elements){
					if(element.startsWith("$java")){
						continue;
					}
					sb.append(element).append(", ");
				}
				sb.deleteCharAt(sb.length()-1);		
				sb.deleteCharAt(sb.length()-1);
				sb.append(Constants.CRLF);
				String element = "";
						sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3")).append(SPACE);
				for(int i=1;i<elements.size()-1;i++){
					element = elements.get(i);
					if(element.startsWith("$java")){
						int indexStart = element.lastIndexOf("lib") + "lib".length() + 1;
						int indexEnd = element.lastIndexOf("migrate.jar") + "migrate.jar".length();
						element = element.substring(indexStart,indexEnd);
								if(elements.get(i+1).startsWith("$java")){
									sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_1",element,elements.get(i-1),elements.get(i+2))).append(", ");
									i++;	
								}else{
						sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_1",element,elements.get(i-1),elements.get(i+1))).append(", ");	
								}
					}
				}
				sb.deleteCharAt(sb.length()-1);		
				for(int i=1;i<elements.size()-1;i++){
					element = elements.get(i);
					if(element.startsWith("$java")){
						sb.deleteCharAt(sb.length()-1);
						break;
					}
				}
			} else {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a")).append(SPACE);
				sb.append(DB2ConfigProp.getProperty(feature + ".dbUserName." + dbType));
				sb.append(Constants.CRLF);
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_1")).append(SPACE);
				for(String element : sqlScripts.get(feature)){
					sb.append(element).append(", ");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append(Constants.CRLF);
			if (dbType.equals(Constants.DB_SQLSERVER) && actionId.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3")).append(SPACE);
				sb.append(createDbFilePath.get(feature)).append(Constants.CRLF);
			}
			sb.append(Constants.CRLF);
				}//end theme
		}
//		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId) && (validFeatures.contains(Constants.FEATURE_COMMUNITIES) && 
//				(validFeatures.contains(Constants.FEATURE_ACTIVITIES) || validFeatures.contains(Constants.FEATURE_BLOGS) || validFeatures.contains(Constants.FEATURE_WIKIS)))) {
//			sb.append(getThemeStr(validFeatures));
//			sb.append(Constants.CRLF).append(Constants.CRLF);	
//		}
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			if (isJDBCNeeded) {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbHost")).append(SPACE);
				sb.append(Constants.LOCALHOST);
				sb.append(Constants.CRLF);
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbPort")).append(SPACE);
				sb.append(port);
				sb.append(Constants.CRLF);
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbAdmin")).append(SPACE);
				sb.append(administrator);
				sb.append(Constants.CRLF);
				if (Constants.DB_SQLSERVER.equals(dbType)) {
					sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.jdbcLib")).append(SPACE);
					sb.append(jdbcLibPath);
					sb.append(Constants.CRLF);
				}
			}
		}
		return sb.toString();
	}
	public static String finish(){
		StringBuffer sb = new StringBuffer();
		sb.append(getHeader());
		for (String feature : validFeatures) {
			if(feature.indexOf("theme") == -1){
			sb.append(Messages.getString(feature + ".name"));
			sb.append(Constants.CRLF);
			sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1")).append(SPACE);
			if (Constants.DB_ORACLE.equals(dbType)) {
				sb.append(dbInstanceName);
			} else {
				sb.append(DB2ConfigProp.getProperty(feature + ".dbName." + dbType));
			}
			sb.append(Constants.CRLF);
			sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a")).append(SPACE);
			sb.append(DB2ConfigProp.getProperty(feature + ".dbUserName." + dbType)).append(Constants.CRLF);	
			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_b")).append(SPACE);
				String version = dbUpgradeVersions.get(feature);
				if (version.startsWith("1.0"))
					version = Constants.VERSION_10X;
				sb.append(version).append("->").append(Constants.VERSION_TOP).append(Constants.CRLF);
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_2")).append(SPACE);
				List<String> elements = sqlScripts.get(feature);
				for(String element : elements){
					if(element.startsWith("$java")){
						continue;
					}
					sb.append(element).append(", ");
				}
				sb.deleteCharAt(sb.length()-1);		
				sb.deleteCharAt(sb.length()-1);
				sb.append(Constants.CRLF);
				String element = "";
						sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3")).append(SPACE);
				for(int i=1;i<elements.size()-1;i++){
					element = elements.get(i);
					if(element.startsWith("$java")){
						int indexStart = element.lastIndexOf("lib") + "lib".length() + 1;
						int indexEnd = element.lastIndexOf("migrate.jar") + "migrate.jar".length();
						element = element.substring(indexStart,indexEnd);
							if(elements.get(i+1).startsWith("$java")){
								sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_2",element,elements.get(i-1),elements.get(i+2))).append(", ");
								i++;	
							}else{
						sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_2",element,elements.get(i-1),elements.get(i+1))).append(", ");	
							}
					}
				}
				sb.deleteCharAt(sb.length()-1);	
				for(int i=1;i<elements.size()-1;i++){
					element = elements.get(i);
					if(element.startsWith("$java")){
						sb.deleteCharAt(sb.length()-1);
						break;
					}
				}	
			}else{
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_2")).append(SPACE);
				for(String element : sqlScripts.get(feature)){
					sb.append(element).append(", ");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append(Constants.CRLF);
			if (dbType.equals(Constants.DB_SQLSERVER) && actionId.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3")).append(SPACE);
				sb.append(createDbFilePath.get(feature)).append(Constants.CRLF);	
			}
			sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part4")).append(SPACE);
			CommandResultInfo result = null;
			StringBuilder sqlFileNameStr = new StringBuilder();
			List<List<String>> cmds = commands.get(feature);
			for(List<String> cmd : cmds){
				String sqlFileName = CommonHelper.getSqlFileName(cmd, dbType);
				result = resultMap.get(feature + "_" + sqlFileName);
				if (result.getExecState() != CommandResultInfo.COMMAND_SUCCEED) {
					if(sqlFileName.indexOf("migrate") == -1){
						if(sqlFileName.indexOf("forum_") != -1){
							sqlFileNameStr.append(sqlFileName.replace("forum_","forum.")).append(".sql").append(", ");
						}else{		
							sqlFileNameStr.append(sqlFileName).append(".sql").append(", ");
						}
					}else{
						if(sqlFileName.indexOf("forum_") != -1){
							sqlFileNameStr.append(sqlFileName.replace("forum_","forum.")).append(".jar").append(", ");
						}else if(sqlFileName.indexOf("news_email_") != -1){
							sqlFileNameStr.append(sqlFileName.replace("news_email_","news.")).append(".jar").append("(EmailDigestMigrationFrom25To30.class), ");
						}else if(sqlFileName.indexOf("news_") != -1){
						    if(sqlFileName.indexOf("45CR4-50") != -1){
						    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom45To50.class), ");
						    }
						    if(sqlFileName.indexOf("40CR2-45") != -1){
						    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom40To45.class), ");
						    }
						    if(sqlFileName.indexOf("301-40") != -1){
						    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom301To40.class), ");
						    }
						    if(sqlFileName.indexOf("25-30a") != -1){
						    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom25To30.class), ");
						    }
						}else{
							sqlFileNameStr.append(feature).append(".").append(sqlFileName).append(".jar").append(", ");
						}
					}
				} 
			}
			int i = 0;
			for(List<String> cmd : cmds){
				String sqlFileName = CommonHelper.getSqlFileName(cmd, dbType);
				result = resultMap.get(feature + "_" + sqlFileName);
				if(result.getExecState() != CommandResultInfo.COMMAND_SUCCEED){
					sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
					sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
					if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
						sb.append(Messages.getString("dbWizard.backend.task.warn." + actionId + ".new", sqlFileNameStr));
					} else if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
						if(result.getFeatures().contains(feature)){
							sb.append(Messages.getString("dbWizard.backend.task.abort.message", feature.substring(0, 1).toUpperCase() + feature.substring(1)));
						}else{
							sb.append(Messages.getString("dbWizard.backend.task.error." + actionId + ".new", sqlFileNameStr));
						}
					} else {
						sb.append(Messages.getString("dbWizard.backend.task.exception." + actionId + ".new", sqlFileNameStr));
					}
					break;
				}else{
					i++;
				}
			}
			if(i==cmds.size()){
				sb.append(Messages.getString("dbWizard.backend.task.success." + actionId));
			}		
			sb.append(Constants.CRLF).append(Constants.CRLF);		
			}
		}
		for(String feature: validFeatures){
			if(feature.indexOf("theme") != -1){
				CommandResultInfo result = null;
				List<List<String>> cmds = commands.get(feature); 
				int i = 0;
				for(List<String> cmd : cmds){
					String sqlFileName = CommonHelper.getSqlFileName(cmd, dbType);
					result = resultMap.get(feature + "_" + sqlFileName);
					if (result.getExecState() != CommandResultInfo.COMMAND_SUCCEED) {
						if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
							if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
							sb.append(Messages.getString("dbWizard.backend.task.theme.warn", "MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("activities.name")));
							}
							if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
								sb.append(Messages.getString("dbWizard.backend.task.theme.warn", "MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("blogs.name")));
							}
							if(Constants.FEATURE_WIKISTHEME.equals(feature)){
								sb.append(Messages.getString("dbWizard.backend.task.theme.warn", "MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("wikis.name")));
							}
						} else if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
							if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
							sb.append(Messages.getString("dbWizard.backend.task.theme.error","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar", Messages.getString("activities.name")));
							}
							if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
								sb.append(Messages.getString("dbWizard.backend.task.theme.error","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar", Messages.getString("blogs.name")));
							}
							if(Constants.FEATURE_WIKISTHEME.equals(feature)){
								sb.append(Messages.getString("dbWizard.backend.task.theme.error","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar", Messages.getString("wikis.name")));
							}
						} else {
							if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
							sb.append(Messages.getString("dbWizard.backend.task.theme.exception","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("activities.name")));
							}
							if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
								sb.append(Messages.getString("dbWizard.backend.task.theme.exception","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("blogs.name")));
							}
							if(Constants.FEATURE_WIKISTHEME.equals(feature)){
								sb.append(Messages.getString("dbWizard.backend.task.theme.exception","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("wikis.name")));
							}
						}
					}else{  
						i++;
					}
				}
				if(i==cmds.size()){
					if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
						sb.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString("activities.name")));
					}
					if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
						sb.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString("blogs.name")));
					}
					if(Constants.FEATURE_WIKISTHEME.equals(feature)){
						sb.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString("wikis.name")));
					}
				}
				sb.append(Constants.CRLF).append(Constants.CRLF);
			}
		}
		logPath = logPath.substring(0,logPath.indexOf("dbWizard") + "dbWizard".length());
		sb.append(Messages.getString("dbWizard.finishWizardPage.content.view.log.new", logPath));
		return sb.toString();
	}
	public static String getHeader(){
		StringBuffer sb = new StringBuffer();
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part1")).append(SPACE);
		sb.append(Messages.getString("dbWizard.action.db." + actionId + ".name"));
		sb.append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part2")).append(SPACE);
		sb.append(Messages.getString(dbType + ".name"));
		sb.append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part3")).append(SPACE);
		sb.append(dbInstallDir);
		sb.append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part4")).append(SPACE);
		sb.append(dbInstanceName);
		sb.append(Constants.CRLF);
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part5")).append(SPACE);
		for (String feature : validFeatures) {
			if(feature.indexOf("theme") == -1){
				sb.append(SPACE).append(Messages.getString(feature + ".name")).append(",");
			}		
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(Constants.CRLF).append(Constants.CRLF);
		return sb.toString();
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
			logError(Level.SEVERE, "dbwizard.error.invalid.db.port");
			return false;
		}

		// validate administrator name
		String dbAdmin = connInfo.getUsername();
		if (dbAdmin == null || "".equals(dbAdmin.trim())) {
			logError(Level.SEVERE, "dbwizard.error.invalid.db.dbadmin");
			return false;
		}
		String adminPwd = connInfo.getPassword();
		// validate administrator password
		if (adminPwd == null || "".equals(adminPwd.trim())) {
			logError(Level.SEVERE, "dbwizard.error.invalid.db.adminpwd");
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
			logMessage(Level.SEVERE, "validator.cannot_validation_process.message");
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
			logMessage(Level.SEVERE, "validator.no_jdbc_driver.message", jdbcDriver);
			break;
		case Constants.ERROR_COMMUNICATION_ERROR:
			logMessage(Level.SEVERE, "validator.communication_error.message", hostname, port);
			break;
		case Constants.ERROR_DB_NOT_EXIST:
			logMessage(Level.SEVERE, "validator.db_not_exist.message", dbName);
			break;
		case Constants.ERROR_INVALID_AUTHENTICATION:
			logMessage(Level.SEVERE, "validator.invalid_authentication.message");
			break;
		case Constants.ERROR_UNKNOWN:
			logMessage(Level.SEVERE, "validator.cannot_connect_db.message", dbUrl);
			break;
		default:
			logMessage(Level.SEVERE, "validator.cannot_validation_process.message");
		}

		return false;
	}

	private static boolean validateDBHome(String dbType, String dbHome) {
		log(Level.INFO, "dbconfig.info.silent.validation.dbLocation");
//#Oracle12C_PDB_disable#		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType,PDBNameValue,dbaPasswordValue);	
		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType);
		if (!DC.validateInstallLoc(dbVersion)) {
			log(Level.INFO, "dbconfig.info.silent.validation.dbLocation.fail");
			return false;
		}
		log(Level.INFO, "dbconfig.info.silent.validation.dbLocation.succeed");
		return true;
	}

	private static boolean validateDBInstance(String dbType, String dbHome, String instance) {
		log(Level.INFO, "dbconfig.info.silent.validation.instance");
//#Oracle12C_PDB_disable#		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType,PDBNameValue,dbaPasswordValue);
		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType);
		DC.setInstance(instance);
		if (!DC.validateInstance(dbVersion)) {
			log(Level.INFO, "dbconfig.info.silent.validation.instance.fail");
			return false;
		}
		log(Level.INFO, "dbconfig.info.silent.validation.instance.succeed");
		return true;
	}

//#Oracle12C_PDB_disable#	private static List<String> getValidFeatures(String action, String dbType, String dbHome, String instance,String PDBNameValue,String dbaPasswordValue, String dbVersion, String featureStr) {	
	private static List<String> getValidFeatures(String action, String dbType, String dbHome, String instance,String dbVersion, String featureStr) {
		log(Level.INFO, "dbconfig.info.silent.validation.features");
//#Oracle12C_PDB_disable#		Checker dbCheck = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType,PDBNameValue,dbaPasswordValue);
		Checker dbCheck = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType);
		dbCheck.setInstance(instance);
		String[] installedFeatures = dbCheck.getFeatures(dbVersion);
		// add by Jia, for user to add just library instead of library.gcd and library.os
		featureStr = featureStr.replace("library","library.gcd, library.os");
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
//#Oracle12C_PDB_disable#   			dbUpgradeVersions = SchemaVersionDetector.getFeatureVersions(dbType, dbHome, instance,PDBNameValue,dbaPasswordValue, dbVersion, (String[]) validFeatures.toArray(new String[0]));
			dbUpgradeVersions = SchemaVersionDetector.getFeatureVersions(dbType, dbHome, instance,dbVersion, (String[]) validFeatures.toArray(new String[0]));
			List<String> temp = new ArrayList<String>(validFeatures);
			for (String f : temp) {
				if (Constants.VERSION_TOP.equals(dbUpgradeVersions.get(f))) {
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
			// add by Jia, for user to add just library instead of library.gcd and library.os
			String password;
			if (feature.equals("library.gcd") || feature.equals("library.os")) {
				password = prop.getProperty("library.password").trim();
			} else {
				password = prop.getProperty(feature + ".password").trim();
			}
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
			// add by Jia, for user to add just library instead of library.gcd and library.os
			String path;
			if (feature.equals("library.gcd") || feature.equals("library.os")) {
				path = prop.getProperty("library.filepath").trim();
			} else {			
				path = prop.getProperty(feature + ".filepath").trim();
			}
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
	private static boolean validateDbVersion(String dbType, String dbHome, String dbVersion){
		log(Level.INFO, "dbconfig.info.silent.validation.dbVersion");
//#Oracle12C_PDB_disable#		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType,PDBNameValue,dbaPasswordValue);
		Checker DC = Checker.getChecker(dbHome, CommonHelper.getPlatformType(), dbType);
		if (!DC.validateVersion(dbVersion)) {
			return false;
		}
		log(Level.INFO, "dbconfig.info.silent.validation.dbVersion.succeed");
		return true;
	}

	private static Map<String,List<String>> getExecuteSQLScripts() {

		Map<String,List<String>> map = new HashMap<String,List<String>>();
		try{	
//#Oracle12C_PDB_disable#			map = DBExe.getExecuteSQLScripts(dbType, dbVersion, dbInstallDir, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, createDbFilePath, CommonHelper
			map = DBExe.getExecuteSQLScripts(dbType, dbVersion, dbInstallDir, dbInstanceName,dbUserPassword, createDbFilePath, CommonHelper
						.getPlatformType(), validFeatures, dbUpgradeVersions, actionId, connInfo, profileConnInfo);
			return map;
		} catch (Exception e) {
//			logger.log(Level.SEVERE, "dbconfig.severe.execute.task", e);

			e.printStackTrace();
			return null;
		}

		}

	
	private static String getThemeStr(List<String> features){
		if(features.contains(Constants.FEATURE_ACTIVITIES) && !features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_1", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"));
				}
		if(features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_ACTIVITIES) && !features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_1", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("blogs.name"));
				}
		if(features.contains(Constants.FEATURE_WIKIS) && !features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_ACTIVITIES)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_1", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("wikis.name"));
			}
		if(features.contains(Constants.FEATURE_ACTIVITIES) && features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_2", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"), Messages.getString("blogs.name"));
			}
		if(features.contains(Constants.FEATURE_ACTIVITIES) && features.contains(Constants.FEATURE_WIKIS) && !features.contains(Constants.FEATURE_BLOGS) ){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_2", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"), Messages.getString("wikis.name"));
		}

		if(features.contains(Constants.FEATURE_BLOGS) && features.contains(Constants.FEATURE_WIKIS) && !features.contains(Constants.FEATURE_ACTIVITIES)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_2", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("blogs.name"), Messages.getString("wikis.name"));
				// jdbc connection information
				}
		if(features.contains(Constants.FEATURE_ACTIVITIES) && features.contains(Constants.FEATURE_BLOGS) && features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_3", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"), Messages.getString("blogs.name"), Messages.getString("wikis.name"));
			}
		return "";
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
	private static void logMessage(Level level, String label, Object... params) {
		logger.log(level, label, params);

		if (params == null)
			try {
				System.out.println(Messages.getString(label));
			} catch (Exception e) {
				System.out.println(label);
			}
		else
			System.out.println(MessageFormat.format(Messages.getString(label), params));
	}
	private static void logError(Level level, String label, Object... params) {
		logger.log(level, label, params);
		if (params == null)
			try {
				System.out.println(ErrorMsg.getString(label));
			} catch (Exception e) {
				System.out.println(label);
			}
		else
			System.out.println(MessageFormat.format(ErrorMsg.getString(label), params));
	}
	/*
	 * private static void log(Level level, String message) { logger.log(level,
	 * message); System.out.println(message); }
	 */
}
