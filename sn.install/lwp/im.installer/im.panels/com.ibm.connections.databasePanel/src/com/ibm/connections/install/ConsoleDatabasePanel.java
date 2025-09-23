/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2018                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.common.core.api.utils.EncryptionUtils;

public class ConsoleDatabasePanel extends BaseConfigConsolePanel {
	private InstallValidator installvalidator = new InstallValidator();
	private DbInstallValidator validator = new DbInstallValidator();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());
	private IProfile profile = null;

	public static String ACTIVITIES_DB_NAME = "";
	public static String BLOGS_DB_NAME = "";
	public static String COMMUNITIES_DB_NAME = "";
	public static String DOGEAR_DB_NAME = "";
	public static String FILES_DB_NAME = "";
	public static String FORUM_DB_NAME = "";
	public static String HOMEPAGE_DB_NAME = "";
	public static String PEOPLEDB_DB_NAME = "";
	public static String WIKIS_DB_NAME = "";
	public static String METRICS_DB_NAME = "";
	public static String MOBILE_DB_NAME = "";
	public static String PUSHNOTIFICATION_DB_NAME = "";
	public static String CCM_DB_NAME_GCD = "";
	public static String CCM_DB_NAME_OBJECT_STORE = "";
	public static String ICEC_DB_NAME = "";
	public static String IC360_DB_NAME = "";

	private int dbType = 1;// 1 for DB2, 2 for Oracle, 3 for SQL

	private String propertiesFile = "";

	private boolean hostNameFlag = false, portFlag = false, returnFlag = false,
			userNameFlag = false, passwordFlag = false, dbconnection = false, ccmFailed = false;

	private String hostname = ""; // field for path
	private String port = "";// text field for path
	private String jdbcDriver1 = ""; // text field for path
	private String jdbcDriver2 = ""; // text field for path
	private String jdbcDriverMetricsOS400 = ""; // OS400_Enablement

	private String activitiesUserid1 = "";
	private String activitiesPassword1 = "";
	private String activitiesDBName1 = "";
	private String blogsUserid1 = "";
	private String blogsPassword1 = "";
	private String blogsDBName1 = "";
	private String communitiesUserid1 = "";
	private String communitiesPassword1 = "";
	private String communitiesDBName1 = "";
	private String dogearUserid1 = "";
	private String dogearPassword1 = "";
	private String dogearDBName1 = "";
	private String profilesUserid1 = "";
	private String profilesPassword1 = "";
	private String profilesDBName1 = "";
	private String wikisUserid1 = "";
	private String wikisPassword1 = "";
	private String wikisDBName1 = "";
	private String filesUserid1 = "";
	private String filesPassword1 = "";
	private String filesDBName1 = "";
	private String forumUserid1 = "";
	private String forumPassword1 = "";
	private String forumDBName1 = "";
	private String homepageUserid1 = "";
	private String homepagePassword1 = "";
	private String homepageDBName1 = "";
	private String metricsUserid1 = "";
	private String metricsPassword1 = "";
	private String metricsDBName1 = "";
	private String mobileUserid1 = "";
	private String mobilePassword1 = "";
	private String mobileDBName1 = "";
	private String pushnotificationUserid1 = "";
	private String pushnotificationPassword1 = "";
	private String pushnotificationDBName1 = "";
	private String ccmGCDUserid1 = "";
	private String ccmGCDPassword1 = "";
	private String ccmGCDDBName1 = "";
	private String ccmOSUserid1 = "";
	private String ccmOSPassword1 = "";
	private String ccmOSDBName1 = "";
	private String icecUserid1 = "";
	private String icecPassword1 = "";
	private String icecDBName1 = "";
	private String ic360Userid1 = "";
	private String ic360Password1 = "";
	private String ic360DBName1 = "";

	private boolean checkbox1 = false;// whether to use same passwd for all apps

	private String activitiesUserid2 = "";
	private String activitiesPassword2 = "";
	private String activitiesDBName2 = "";
	private String activitiesDBHostName2 = "";
	private String activitiesDBPort2 = "";
	private String blogsUserid2 = "";
	private String blogsPassword2 = "";
	private String blogsDBName2 = "";
	private String blogsDBHostName2 = "";
	private String blogsDBPort2 = "";
	private String communitiesUserid2 = "";
	private String communitiesPassword2 = "";
	private String communitiesDBName2 = "";
	private String communitiesDBHostName2 = "";
	private String communitiesDBPort2 = "";
	private String dogearUserid2 = "";
	private String dogearPassword2 = "";
	private String dogearDBName2 = "";
	private String dogearDBHostName2 = "";
	private String dogearDBPort2 = "";
	private String profilesUserid2 = "";
	private String profilesPassword2 = "";
	private String profilesDBName2 = "";
	private String profilesDBHostName2 = "";
	private String profilesDBPort2 = "";
	private String wikisUserid2 = "";
	private String wikisPassword2 = "";
	private String wikisDBName2 = "";
	private String wikisDBHostName2 = "";
	private String wikisDBPort2 = "";
	private String filesUserid2 = "";
	private String filesPassword2 = "";
	private String filesDBName2 = "";
	private String filesDBHostName2 = "";
	private String filesDBPort2 = "";
	private String forumUserid2 = "";
	private String forumPassword2 = "";
	private String forumDBName2 = "";
	private String forumDBHostName2 = "";
	private String forumDBPort2 = "";
	private String homepageUserid2 = "";
	private String homepagePassword2 = "";
	private String homepageDBName2 = "";
	private String homepageDBHostName2 = "";
	private String homepageDBPort2 = "";
	private String metricsUserid2 = "";
	private String metricsPassword2 = "";
	private String metricsDBName2 = "";
	private String metricsDBHostName2 = "";
	private String metricsDBPort2 = "";
	private String mobileUserid2 = "";
	private String mobilePassword2 = "";
	private String mobileDBName2 = "";
	private String mobileDBHostName2 = "";
	private String mobileDBPort2 = "";
	private String pushnotificationUserid2 = "";
	private String pushnotificationPassword2 = "";
	private String pushnotificationDBName2 = "";
	private String pushnotificationDBHostName2 = "";
	private String pushnotificationDBPort2 = "";
	private String ccmGCDUserid2 = "";
	private String ccmGCDPassword2 = "";
	private String ccmGCDDBName2 = "";
	private String ccmGCDDBHostName2 = "";
	private String ccmGCDDBPort2 = "";
	private String ccmOSUserid2 = "";
	private String ccmOSPassword2 = "";
	private String ccmOSDBName2 = "";
	private String ccmOSDBHostName2 = "";
	private String ccmOSDBPort2 = "";
	private String icecUserid2 = "";
	private String icecPassword2 = "";
	private String icecDBName2 = "";
	private String icecDBHostName2 = "";
	private String icecDBPort2 = "";
	private String ic360Userid2 = "";
	private String ic360Password2 = "";
	private String ic360DBName2 = "";
	private String ic360DBHostName2 = "";
	private String ic360DBPort2 = "";

	private boolean checkbox2 = false;// whether to use same passwd for all apps

	private boolean sameDB = true;

	private boolean isFixpackInstall;
	private boolean isModifyInstall;

	private ArrayList<String> selectFeaturesList = new ArrayList<String>();

	private String jdbcDriver;
	private String activitiesHostName, activitiesPort, blogsHostName,
			blogsPort, communitiesHostName, communitiesPort, dogearHostName,
			dogearPort, profilesHostName, profilesPort, wikisHostName,
			wikisPort, filesHostName, filesPort, forumHostName, forumPort,
			homepageHostName, homepagePort, metricsHostName, metricsPort,
			mobileHostName, mobilePort, ccmGCDHostName, ccmGCDPort,
			ccmOSHostName, ccmOSPort, pushnotificationHostName, pushnotificationPort, activitiesUsername, activitiesPassword,
			blogsUsername, blogsPassword, communitiesUsername,
			communitiesPassword, dogearUsername, dogearPassword,
			profilesUsername, profilesPassword, wikisUsername, wikisPassword,
			filesUsername, filesPassword, forumUsername, forumPassword,
			homepageUsername, homepagePassword, metricsUsername,
			metricsPassword, mobileUsername, mobilePassword, ccmGCDUsername, ccmGCDPassword,
			ccmOSUsername, ccmOSPassword, pushnotificationUsername, pushnotificationPassword,
			ic360HostName, ic360Port, ic360Username, ic360Password,
			icecHostName, icecPort, icecUsername, icecPassword, icecDatabaseName;

	public ConsoleDatabasePanel() {
		super(Messages.DB_DATABASE);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_DATABASE_PANEL;
	}

	@Override
	public void perform() {
		if (shouldSkip())
			return;
		TextCustomPanelUtils.setLogPanel(log, "Database panel");
		log.info("Database Panel :: Entered");
		profile = getProfile();
		this.isFixpackInstall = isUpdate();
		this.isModifyInstall = isModify();
		log.info("isFixpackInstall = " + isFixpackInstall);
		log.info("isModifyInstall = " + isModifyInstall);
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);
		loadFeatureList();
		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG,
				Messages.DB_DATABASE);
		decideSameOrDifferentDB();
	}

	private void loadFeatureList() {
		selectFeaturesList = new ArrayList<String>();
		if (isFeatureSelected("activities"))
			selectFeaturesList.add("activities");
		if (isFeatureSelected("blogs"))
			selectFeaturesList.add("blogs");
		if (isFeatureSelected("communities"))
			selectFeaturesList.add("communities");
		if (isFeatureSelected("dogear"))
			selectFeaturesList.add("dogear");
		if (isFeatureSelected("metrics"))
			selectFeaturesList.add("metrics");
		if (isFeatureSelected("mobile"))
			selectFeaturesList.add("mobile");
		if (isFeatureSelected("files"))
			selectFeaturesList.add("files");
		if (isFeatureSelected("forums"))
			selectFeaturesList.add("forums");
		if (isFeatureSelected("pushNotification"))
			selectFeaturesList.add("pushnotification");
		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news"))
			selectFeaturesList.add("homepage");
		if (isFeatureSelected("profiles"))
			selectFeaturesList.add("profiles");
		if (isFeatureSelected("wikis"))
			selectFeaturesList.add("wikis");
		/* OS400_Enablement
		 * For OS400 filenet will be disabled from the console interface, so no need to care about it.
		 * */
		if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")){
			selectFeaturesList.add("ccm");
		}
		if (isFeatureSelected("icec"))
			selectFeaturesList.add("icec");
		if (isFeatureSelected("ic360"))
			selectFeaturesList.add("ic360");
	}

	private void decideSameOrDifferentDB() {
		returnFlag = false;
		TextCustomPanelUtils.showSubTitle1(Messages.DB_SELECTION);
		int opt = TextCustomPanelUtils
				.singleSelect(Messages.DB_SELECTION_INFO, new String[] {
						Messages.DB_RADIO_SAME, Messages.DB_RADIO_DIFF },
						sameDB ? 1 : 2, null, null);
		sameDB = opt == 1;
		TextCustomPanelUtils.logInput("choose same DB", Boolean.toString(sameDB));
		if (sameDB)
			selectDBTypeSameDB();
		else if (opt == 2)
			selectDBTypeDiffDB();
	}

	private void selectDBTypeSameDB() {
		TextCustomPanelUtils.showSubTitle1(Messages.DB_TYPE);
		/* OS400_Enablement
		 * For Connections on OS400, we only support DB2 on OS400, with Metrics as an exception.
		 * Metrics is using the the DB2 on AIX where we have the Cognos configed, though it
		 * is showing DB2_Iseries here.
		 * */
		int opt = -1;
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			opt = TextCustomPanelUtils.singleSelect(Messages.DB_TYPE_INFO,
					new String[] { Messages.DB_TYPE_DB2_ISERIES }, 1,
						new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
			if (opt < 0) {
				decideSameOrDifferentDB();
				return;
			}
			// OS400_Enablement, DBtype for DB2_Iseries is 4. So convert the input to the real DBtype.
			opt = 4;
		} else {
			// shuanghong, Non-OS400 platform will still use DB2, Oracle or SQL Server.
			int defaultDBType = 1;
			if (isModifyInstall || isFixpackInstall) {
				if (profile != null) {
					log.info("database type user selected: " + profile.getUserData("user.database.type"));
					log.info("database type index: " + getSelectedDBIndex(profile.getUserData("user.database.type")));
					defaultDBType = getSelectedDBIndex(profile.getUserData("user.database.type"));
				}
			}
			opt = TextCustomPanelUtils.singleSelect(Messages.DB_TYPE_INFO,
					new String[] { Messages.DB_TYPE_DB2, Messages.DB_TYPE_Oracle,
						Messages.DB_TYPE_SQL_Server }, defaultDBType,
					new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
			if (opt < 0) {
				decideSameOrDifferentDB();
				return;
			}
		}
		dbType = opt;
		loadDefaults();
		configDBServerInfoSameDB();
	}

	private int getSelectedDBIndex(String dbType) {
		int index = 0;
		if(DatabaseUtil.DBMS_ORACLE.equals(dbType)){
			index = 2;
		}else if(DatabaseUtil.DBMS_SQLSERVER.equals(dbType)){
			index = 3;
		}else if(DatabaseUtil.DBMS_DB2.equals(dbType)){
			index = 1;
		}
		return index;
	}

	private void configDBServerInfoSameDB() {
		TextCustomPanelUtils.showSubTitle1(Messages.DB_PROPERTIES);
		String input = TextCustomPanelUtils.getInput(Messages.DB_HOST_NAME,
				hostname);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			selectDBTypeSameDB();
			return;
		}
		TextCustomPanelUtils.logInput("DB server host name", input);
		if (!installvalidator.hostNameValidateForWasPanel(input.trim())
				|| installvalidator.containsSpace(input.trim())) {
			TextCustomPanelUtils.showError(installvalidator.getMessage());
			log.error("Host name is invalid");
			configDBServerInfoSameDB();
			return;
		}
		hostname = input.trim();
		configDBPortSameDB();
	}

	private void configDBPortSameDB() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_PORT, port);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDBServerInfoSameDB();
			return;
		}
		TextCustomPanelUtils.logInput("DB port", input);
		if (!installvalidator.portNumValidate(input.trim())) {
			TextCustomPanelUtils.showError(installvalidator.getMessage());
			log.error("Port number is invalid");
			configDBPortSameDB();
			return;
		}
		port = input.trim();
		configJDBCDriverLocSameDB();
	}

	private void configJDBCDriverLocSameDB() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_DRIVER_LIB,
				jdbcDriver1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDBPortSameDB();
			return;
		}
		TextCustomPanelUtils.logInput("jdbc driver location", input);
		if (!verifyJDBCDriverPathComplete(input.trim())) {
			TextCustomPanelUtils.showError(validator.getMessage());
			log.error("The JDBC Driver path for " + getSelectedDBType()
					+ " is NOT valid: " + jdbcDriver1);
			configJDBCDriverLocSameDB();
			return;
		}
		jdbcDriver1 = input.trim();

		if (System.getProperty("os.name").toLowerCase().startsWith("os/400") && isFeatureSelected("metrics")) {
			/* *
			 * OS400_Enablement
			 * Metrics on OS400 will use the DB2 on AIX which has Cognos configured even though DB2_ISERIES is selected,
			 * Starting from Connections 5.0, we are using the DB2 LUW JCC jdbc driver.  The following internal JDBC driver
			 * will not be used.
			 * WAS8:/qibm/proddata/WebSphere/AppServer/V8c/ND/deploytool/itp/plugins/com.ibm.datatools.db2_2.1.102.v20120412_2209/driver
			 */
			input = TextCustomPanelUtils.getInput(Messages.DB_DRIVER_LIB_ISERIES_METRICS,
					jdbcDriverMetricsOS400);
			if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				configDBPortSameDB();
				return;
			}
			TextCustomPanelUtils.logInput("Metrics jdbc driver location", input);
			if (!verifyOS400MetricsJDBCDriverPathComplete(input.trim())) {
				TextCustomPanelUtils.showError(validator.getMessage());
				log.error("The Metrics JDBC Driver path for " + getSelectedDBType()
						+ " is NOT valid: " + jdbcDriverMetricsOS400);
				configJDBCDriverLocSameDB();
				return;
			}
			jdbcDriverMetricsOS400 = input.trim();
		}

		log.info("features added size: " + selectFeaturesList.size());
		if ((isModifyInstall || isFixpackInstall) && selectFeaturesList.size() <= 1) {
			checkbox1 = true;
			configApplications();
		} else {
			decideUseSamePasswdSameDB();
		}
	}

	private void decideUseSamePasswdSameDB() {
		TextCustomPanelUtils.showSubTitle1(Messages.DB_FEATURE_INFO);
		String input = TextCustomPanelUtils.showYorN(
				Messages.DB_CHECK_BTN_SAME, checkbox1 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configJDBCDriverLocSameDB();
			return;
		}
		TextCustomPanelUtils.logInput("use same password", input);
		checkbox1 = input.trim().toUpperCase().equals("Y");
		configApplications();
	}

	private void configApplications() {
		if (sameDB)
			configActivities1();
		else
			configActivities2();
	}

	// ------------------------------------------------

	private void configActivities1() {
		if (isFeatureSelected("activities")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.ACTIVITIES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, activitiesDBName1);
			if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("activities");
				return;
			}
			TextCustomPanelUtils.logInput("activities db name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configActivities1();
				return;
			}
			activitiesDBName1 = input.trim();
			configActivitiesUserId1();
		} else
			configBlogs1();
	}

	private void configActivitiesUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				activitiesUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configActivities1();
			return;
		}
		TextCustomPanelUtils.logInput("activities user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.ACTIVITIES));
			log.error("CLFRP0024E:  User ID for Activities contains invalid characters.");
			configActivitiesUserId1();
			return;
		}
		activitiesUserid1 = input.trim();
		configActivitiesPassword1();
	}

	private void configActivitiesPassword1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configActivitiesUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.ACTIVITIES));
			log.error("CLFRP0025E: Password for Activities contains invalid characters.");
			configActivitiesPassword1();
			return;
		}
		if (checkbox1)// same password
			loadSamePasswords(input.trim());
		else
			activitiesPassword1 = input.trim();
		if (returnFlag) {
			returnFlag = false;
			displayAppDataSameDB();
			return;
		}
		goToNextApp("activities");
	}

	private void configBlogs1() {
		if (isFeatureSelected("blogs")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.BLOGS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, blogsDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("blogs");
				return;
			}
			TextCustomPanelUtils.logInput("blogs DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configBlogs1();
				return;
			}
			blogsDBName1 = input.trim();
			configBlogsUserId1();
		} else
			configCommunities1();
	}

	private void configBlogsUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				blogsUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configBlogs1();
			return;
		}
		TextCustomPanelUtils.logInput("blogs user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.BLOGS));
			log.error("CLFRP0024E:  User ID for Blogs contains invalid characters.");
			configBlogsUserId1();
			return;
		}
		blogsUserid1 = input.trim();
		configBlogsPassword1();
	}

	private void configBlogsPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("blogs") > 0) {
			goToNextApp("blogs");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configBlogsUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.BLOGS));
			log.error("CLFRP0025E: Password for Blogs contains invalid characters.");
			configBlogsPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("blogs") == 0)
			loadSamePasswords(input.trim());
		else
			blogsPassword1 = input.trim();
		goToNextApp("blogs");
	}

	private void configCommunities1() {
		if (isFeatureSelected("communities")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.COMMUNITIES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, communitiesDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("communities");
				return;
			}
			TextCustomPanelUtils.logInput("communities DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configCommunities1();
				return;
			}
			communitiesDBName1 = input.trim();
			configCommunitiesUserId1();
		} else
			configDogear1();
	}

	private void configCommunitiesUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				communitiesUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCommunities1();
			return;
		}
		TextCustomPanelUtils.logInput("communities user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.COMMUNITIES));
			log.error("CLFRP0024E:  User ID for Communities contains invalid characters.");
			configCommunitiesUserId1();
			return;
		}
		communitiesUserid1 = input.trim();
		configCommunitiesPassword1();
	}

	private void configCommunitiesPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("communities") > 0) {
			goToNextApp("communities");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCommunitiesUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.COMMUNITIES));
			log.error("CLFRP0025E: Password for Communities contains invalid characters.");
			configCommunitiesPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("communities") == 0)
			loadSamePasswords(input.trim());
		else
			communitiesPassword1 = input.trim();
		goToNextApp("communities");
	}

	private void configDogear1() {
		if (isFeatureSelected("dogear")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.DOGEAR);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, dogearDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("dogear");
				return;
			}
			TextCustomPanelUtils.logInput("dogear DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configDogear1();
				return;
			}
			dogearDBName1 = input.trim();
			configDogearUserId1();
		} else
			configMetrics1();
	}

	private void configDogearUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				dogearUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDogear1();
			return;
		}
		TextCustomPanelUtils.logInput("dogear user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.DOGEAR));
			log.error("CLFRP0024E:  User ID for Bookmarks contains invalid characters.");
			configDogearUserId1();
			return;
		}
		dogearUserid1 = input.trim();
		configDogearPassword1();
	}

	private void configDogearPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("dogear") > 0) {
			goToNextApp("dogear");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDogearUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.DOGEAR));
			log.error("CLFRP0025E: Password for Bookmarks contains invalid characters.");
			configDogearPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("dogear") == 0)
			loadSamePasswords(input.trim());
		else
			dogearPassword1 = input.trim();
		goToNextApp("dogear");
	}

	private void configMetrics1() {
		if (isFeatureSelected("metrics")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.METRICS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, metricsDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("metrics");
				return;
			}
			TextCustomPanelUtils.logInput("metrics DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configMetrics1();
				return;
			}
			metricsDBName1 = input.trim();
			configMetricsUserId1();
		} else
			configMobile1();
	}

	private void configMetricsUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				metricsUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMetrics1();
			return;
		}
		TextCustomPanelUtils.logInput("metrics user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.METRICS));
			log.error("CLFRP0024E:  User ID for Metrics contains invalid characters.");
			configMetricsUserId1();
			return;
		}
		metricsUserid1 = input.trim();
		configMetricsPassword1();
	}

	private void configMetricsPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("metrics") > 0) {
			goToNextApp("metrics");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMetricsUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.METRICS));
			log.error("CLFRP0025E: Password for Metrics contains invalid characters.");
			configMetricsPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("metrics") == 0)
			loadSamePasswords(input.trim());
		else
			metricsPassword1 = input.trim();
		goToNextApp("metrics");
	}

	private void configMobile1() {
		if (isFeatureSelected("mobile")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.MOBILE);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, mobileDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("mobile");
				return;
			}
			TextCustomPanelUtils.logInput("mobile DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configMobile1();
				return;
			}
			mobileDBName1 = input.trim();
			configMobileUserId1();
		} else
			configFiles1();
	}

	private void configMobileUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				mobileUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMobile1();
			return;
		}
		TextCustomPanelUtils.logInput("mobile user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.MOBILE));
			log.error("CLFRP0024E:  User ID for Mobile contains invalid characters.");
			configMobileUserId1();
			return;
		}
		mobileUserid1 = input.trim();
		configMobilePassword1();
	}

	private void configMobilePassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("mobile") > 0) {
			goToNextApp("mobile");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMobileUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.MOBILE));
			log.error("CLFRP0025E: Password for Mobile contains invalid characters.");
			configMobilePassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("mobile") == 0)
			loadSamePasswords(input.trim());
		else
			mobilePassword1 = input.trim();
		goToNextApp("mobile");
	}

	private void configFiles1() {
		if (isFeatureSelected("files")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.FILES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, filesDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("files");
				return;
			}
			TextCustomPanelUtils.logInput("files DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configFiles1();
				return;
			}
			filesDBName1 = input.trim();
			configFilesUserId1();
		} else
			configForum1();
	}

	private void configFilesUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				filesUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configFiles1();
			return;
		}
		TextCustomPanelUtils.logInput("files user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.FILES));
			log.error("CLFRP0024E:  User ID for Files contains invalid characters.");
			configFilesUserId1();
			return;
		}
		filesUserid1 = input.trim();
		configFilesPassword1();
	}

	private void configFilesPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("files") > 0) {
			goToNextApp("files");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configFilesUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.FILES));
			log.error("CLFRP0025E: Password for Files contains invalid characters.");
			configFilesPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("files") == 0)
			loadSamePasswords(input.trim());
		else
			filesPassword1 = input.trim();
		goToNextApp("files");
	}

	private void configForum1() {
		if (isFeatureSelected("forums")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.FORUMS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, forumDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("forums");
				return;
			}
			TextCustomPanelUtils.logInput("forum DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configForum1();
				return;
			}
			forumDBName1 = input.trim();
			configForumUserId1();
		} else
			configPushnotification1();
	}

	private void configForumUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				forumUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configForum1();
			return;
		}
		TextCustomPanelUtils.logInput("forum user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.FORUMS));
			log.error("CLFRP0024E:  User ID for Forums contains invalid characters.");
			configForumUserId1();
			return;
		}
		forumUserid1 = input.trim();
		configForumPassword1();
	}

	private void configForumPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("forums") > 0) {
			goToNextApp("forums");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configForumUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.FORUMS));
			log.error("CLFRP0025E: Password for Forums contains invalid characters.");
			configForumPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("forums") == 0)
			loadSamePasswords(input.trim());
		else
			forumPassword1 = input.trim();
		goToNextApp("forums");
	}

	private void configPushnotification1() {
		if (isFeatureSelected("pushNotification")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.PUSH_NOTIFICATION);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, pushnotificationDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("pushnotification");
				return;
			}
			TextCustomPanelUtils.logInput("pushnotification DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configPushnotification1();
				return;
			}
			pushnotificationDBName1 = input.trim();
			configPushnotificationUserId1();
		} else
			configHomePage1();
	}

	private void configPushnotificationUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				pushnotificationUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configPushnotification1();
			return;
		}
		TextCustomPanelUtils.logInput("pushnotification user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.PUSH_NOTIFICATION));
			log.error("CLFRP0024E:  User ID for pushnotification contains invalid characters.");
			configPushnotificationUserId1();
			return;
		}
		pushnotificationUserid1 = input.trim();
		configPushnotificationPassword1();
	}

	private void configPushnotificationPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("pushnotification") > 0) {
			goToNextApp("pushnotification");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configPushnotificationUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.PUSH_NOTIFICATION));
			log.error("CLFRP0025E: Password for Pushnotification contains invalid characters.");
			configPushnotificationPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("pushnotification") == 0)
			loadSamePasswords(input.trim());
		else
			pushnotificationPassword1 = input.trim();
		goToNextApp("pushnotification");
	}

	private void configHomePage1() {
		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.HOMEPAGE);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, homepageDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("homepage");
				return;
			}
			TextCustomPanelUtils.logInput("homepage DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configHomePage1();
				return;
			}
			homepageDBName1 = input.trim();
			configHomepageUserId1();
		} else
			configProfiles1();
	}

	private void configHomepageUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				homepageUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configHomePage1();
			return;
		}
		TextCustomPanelUtils.logInput("homepage user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.HOMEPAGE));
			log.error("CLFRP0024E:  User ID for Homepage contains invalid characters.");
			configHomepageUserId1();
			return;
		}
		homepageUserid1 = input.trim();
		configHomepagePassword1();
	}

	private void configHomepagePassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("homepage") > 0) {
			goToNextApp("homepage");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configHomepageUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.HOMEPAGE));
			log.error("CLFRP0025E: Password for Homepage contains invalid characters.");
			configHomepagePassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("homepage") == 0)
			loadSamePasswords(input.trim());
		else
			homepagePassword1 = input.trim();
		goToNextApp("homepage");
	}

	private void configProfiles1() {
		if (isFeatureSelected("profiles")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.PROFILES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, profilesDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("profiles");
				return;
			}
			TextCustomPanelUtils.logInput("profiles DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configProfiles1();
				return;
			}
			profilesDBName1 = input.trim();
			configProfilesUserId1();
		} else
			configWikis1();
	}

	private void configProfilesUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				profilesUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configProfiles1();
			return;
		}
		TextCustomPanelUtils.logInput("profiles user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.PROFILES));
			log.error("CLFRP0024E:  User ID for Profiles contains invalid characters.");
			configProfilesUserId1();
			return;
		}
		profilesUserid1 = input.trim();
		configProfilesPassword1();
	}

	private void configProfilesPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("profiles") > 0) {
			goToNextApp("profiles");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configProfilesUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.PROFILES));
			log.error("CLFRP0025E: Password for Profiles contains invalid characters.");
			configProfilesPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("profiles") == 0)
			loadSamePasswords(input.trim());
		else
			profilesPassword1 = input.trim();
		goToNextApp("profiles");
	}

	private void configWikis1() {
		if (isFeatureSelected("wikis")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.WIKIS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, wikisDBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("wikis");
				return;
			}
			TextCustomPanelUtils.logInput("wikis DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configWikis1();
				return;
			}
			wikisDBName1 = input.trim();
			configWikisUserId1();
		} else
			configCCMGCD1();
	}

	private void configIC3601() {
		if (isFeatureSelected("ic360")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.IC360);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, ic360DBName1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("ic360");
				return;
			}
			TextCustomPanelUtils.logInput("ic360 DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configIC3601();
				return;
			}
			ic360DBName1 = input.trim();
			configIC360UserId1();
		} else
			displayAppDataSameDB();
	}

	private void configWikisUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				wikisUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configWikis1();
			return;
		}
		TextCustomPanelUtils.logInput("wikis user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.WIKIS));
			log.error("CLFRP0024E:  User ID for Wikis contains invalid characters.");
			configWikisUserId1();
			return;
		}
		wikisUserid1 = input.trim();
		configWikisPassword1();
	}

	private void configIC360UserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				ic360Userid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configIC3601();
			return;
		}
		TextCustomPanelUtils.logInput("ic360 user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.IC360));
			log.error("CLFRP0024E:  User ID for ic360 contains invalid characters.");
			configIC360UserId1();
			return;
		}
		ic360Userid1 = input.trim();
		configIC360Password1();
	}

	private void configWikisPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("wikis") > 0) {
			goToNextApp("wikis");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configWikisUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.WIKIS));
			log.error("CLFRP0025E: Password for Wikis contains invalid characters.");
			configWikisPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("wikis") == 0)
			loadSamePasswords(input.trim());
		else
			wikisPassword1 = input.trim();
		goToNextApp("wikis");
	}

	private void configIC360Password1() {
		if (checkbox1 && selectFeaturesList.indexOf("ic360") > 0) {
			goToNextApp("ic360");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configIC360UserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.IC360));
			log.error("CLFRP0025E: Password for IC360 contains invalid characters.");
			configIC360Password1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("ic360") == 0)
			loadSamePasswords(input.trim());
		else
			ic360Password1 = input.trim();
		goToNextApp("ic360");
	}

	private void configCCMGCD1() {
		/* OS400_Enablement
		 * OS400 does not support Filenet and disables it from the console interface.
		 * so no need to care about it here for OS400 now.
		 * */
		if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "	+ Messages.CCM);
			String input = "";
			TextCustomPanelUtils.showSubTitle2(Messages.GCD);
			input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_NAME, ccmGCDDBName1);
			if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("ccm");
				return;
			}
			TextCustomPanelUtils.logInput("ccm GCD DB name", input);

			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configCCMGCD1();
				return;
			}
			ccmGCDDBName1 = input.trim();
			configCCMGCDUserId1();
		} else
			configICEC1();
	}

	private void configCCMGCDUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,	ccmGCDUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMGCD1();
			return;
		}
		TextCustomPanelUtils.logInput("ccm GCD user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.GCD));
			log.error("CLFRP0024E:  User ID for CCM GCD contains invalid characters.");
			configCCMGCDUserId1();
			return;
		}

		ccmGCDUserid1 = input.trim();
		configCCMGCDPassword1();
	}

	private void configCCMGCDPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("ccm") > 0) {
			configCCMObjStore1();
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,	null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMGCDUserId1();
			return;
		}
		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID, Messages.GCD));
			log.error("CLFRP0025E: Password for CCM GCD contains invalid characters.");
			configCCMGCDPassword1();
			return;
		}

		if (checkbox1 && selectFeaturesList.indexOf("ccm") == 0)
			loadSamePasswords(input.trim());
		else
			ccmGCDPassword1 = input.trim();

		configCCMObjStore1();
	}

	private void configCCMObjStore1() {
		TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "	+ Messages.CCM);
		TextCustomPanelUtils.showSubTitle1(Messages.OBJECTSTORE);
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_NAME, ccmOSDBName1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("ccm");
				return;
		}
		TextCustomPanelUtils.logInput("ccm Object Store DB name", input);
		if (!verifyDBNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
			log.error("Invalid database name.");
			configCCMObjStore1();
			return;
		}
		ccmOSDBName1 = input.trim();
		configCCMObjStoreUserId1();
	}

	private void configCCMObjStoreUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,	ccmOSUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMObjStore1();
			return;
		}
		TextCustomPanelUtils.logInput("ccm Object Store user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.OBJECTSTORE));
			log.error("CLFRP0024E:  User ID for CCM Object Store contains invalid characters.");
			configCCMObjStoreUserId1();
			return;
		}
		ccmOSUserid1 = input.trim();
		configCCMObjStorePassword1();
	}

	private void configCCMObjStorePassword1() {
		if (checkbox1) {
			goToNextApp("ccm");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMObjStoreUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.OBJECTSTORE));
			log.error("CLFRP0025E: Password for CCM Object Store contains invalid characters.");
			configCCMObjStorePassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("ccm") == 0)
			loadSamePasswords(input.trim());
		else
			ccmOSPassword1 = input.trim();

		goToNextApp("ccm");
	}

	// add icec section
	private void configICEC1() {
		if (isFeatureSelected("icec")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "+ Messages.ICEC);
			String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_NAME, icecDBName1);
			if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("icec");
				return;
			}
			TextCustomPanelUtils.logInput("community highlights DB name", input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configICEC1();
				return;
			}
			icecDBName1 = input.trim();
			configICECUserId1();
		} else
			configIC3601();
	}

	private void configICECUserId1() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID, icecUserid1);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configICEC1();
			return;
		}
		TextCustomPanelUtils.logInput("community highlights user id", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.BLOGS));
			log.error("CLFRP0024E:  User ID for Community Highlights contains invalid characters.");
			configICECUserId1();
			return;
		}
		icecUserid1 = input.trim();
		configICECPassword1();
	}

	private void configICECPassword1() {
		if (checkbox1 && selectFeaturesList.indexOf("icec") > 0) {
			goToNextApp("icec");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD, null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configICECUserId1();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID, Messages.ICEC));
			log.error("CLFRP0025E: Password for Community Highlight contains invalid characters.");
			configICECPassword1();
			return;
		}
		if (checkbox1 && selectFeaturesList.indexOf("icec") == 0)
			loadSamePasswords(input.trim());
		else
			icecPassword1 = input.trim();

		displayAppDataSameDB();
	}

	// -----------------------------------------------------------
	private void selectDBTypeDiffDB() {
		/* OS400_Enablement
		 * For Connections OS400, we only support DB2 on OS400, with Metrics as an exception.
		 * Metrics is using the the DB2 on AIX where we have the Cognos configed, 	though it
		 * is showing DB2_Iseries here.
		 **/
		int opt = -1;
		TextCustomPanelUtils.showSubTitle1(Messages.DB_TYPE);
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			opt = TextCustomPanelUtils.singleSelect(Messages.DB_TYPE_INFO,
					new String[] { Messages.DB_TYPE_DB2_ISERIES }, 1,
						new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
			if (opt < 0) {
				decideSameOrDifferentDB();
				return;
			}
			// OS400_Enablement, DBtype for DB2_Iseries is 4. So convert the input to the real DBtype.
			opt = 4;
		} else {
			opt = TextCustomPanelUtils.singleSelect(Messages.DB_TYPE_INFO,
					new String[] { Messages.DB_TYPE_DB2, Messages.DB_TYPE_Oracle,
							Messages.DB_TYPE_SQL_Server }, 1,
							new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
			if (opt < 0) {
				decideSameOrDifferentDB();
				return;
			}
		}
		dbType = opt;
		loadDefaults();
		configDBServerInfoDiffDB();
	}

	private void configDBServerInfoDiffDB() {
		TextCustomPanelUtils.showSubTitle1(Messages.DB_PROPERTIES);
		String input = TextCustomPanelUtils.getInput(Messages.DB_DRIVER_LIB,
				jdbcDriver2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			selectDBTypeDiffDB();
			return;
		}
		TextCustomPanelUtils.logInput("JDBC driver path", input);
		if (!verifyJDBCDriverPathComplete(input.trim())) {
			TextCustomPanelUtils.showError(validator.getMessage());
			log.info("The JDBC Driver path for " + getSelectedDBType()
					+ " is NOT valid: " + jdbcDriver2);
			configDBServerInfoDiffDB();
			return;
		}
		jdbcDriver2 = input.trim();

		if (System.getProperty("os.name").toLowerCase().startsWith("os/400") && isFeatureSelected("metrics")) {
			/***
			 * OS400_Enablement
			 * Metrics on OS400 will use the DB2 on AIX which has Cognos configured even though DB2_ISERIES is selected,
			 * Starting from Connections 5.0, we are using the DB2 LUW Jcc JDBC driver.
			 ***/
			input = TextCustomPanelUtils.getInput(Messages.DB_DRIVER_LIB_ISERIES_METRICS,
					jdbcDriverMetricsOS400);
			if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				selectDBTypeDiffDB();
				return;
			}
			TextCustomPanelUtils.logInput("Metrics jdbc driver location", input);
			if (!verifyOS400MetricsJDBCDriverPathComplete(input.trim())) {
				TextCustomPanelUtils.showError(validator.getMessage());
				log.error("The OS400 Metrics JDBC Driver path is NOT valid: " + jdbcDriverMetricsOS400);
				configDBServerInfoDiffDB();
				return;
			}
			jdbcDriverMetricsOS400 = input.trim();
		}

		log.info("features added size: " + selectFeaturesList.size());
		if ((isModifyInstall || isFixpackInstall) && selectFeaturesList.size() <= 1) {
			checkbox2 = true;
			configApplications();
		} else {
			decideUseSamePasswdDiffDB();
		}
	}

	private void decideUseSamePasswdDiffDB() {
		TextCustomPanelUtils.showSubTitle1(Messages.DB_FEATURE_INFO);
		String input = TextCustomPanelUtils.showYorN(
				Messages.DB_CHECK_BTN_SAME, checkbox2 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDBServerInfoDiffDB();
			return;
		}
		TextCustomPanelUtils.logInput("use same password",input);
		checkbox2 = input.trim().toUpperCase().equals("Y");
		configApplications();
	}

	private void configActivities2() {
		if (isFeatureSelected("activities")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.ACTIVITIES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, activitiesDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("activities");
				return;
			}
			TextCustomPanelUtils.logInput("activities DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configActivities2();
				return;
			}
			activitiesDBName2 = input.trim();
			configActivitiesHostName2();
		} else
			configBlogs2();
	}

	private void configActivitiesHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				activitiesDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configActivities2();
			return;
		}
		TextCustomPanelUtils.logInput("activities host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.ACTIVITIES));
			log.error("The hostname for Activities is not valid.");
			configActivitiesHostName2();
			return;
		}
		activitiesDBHostName2 = input.trim();
		configActivitiesPort2();
	}

	private void configActivitiesPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				activitiesDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configActivitiesHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("activities port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.ACTIVITIES));
			log.error("The port for Activities is not valid.");
			configActivitiesPort2();
			return;
		}
		activitiesDBPort2 = input.trim();
		configActivitiesUserId2();
	}

	private void configActivitiesUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				activitiesUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configActivitiesPort2();
			return;
		}
		TextCustomPanelUtils.logInput("activities user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.ACTIVITIES));
			log.error("CLFRP0024E:  User ID for Activities contains invalid characters.");
			configActivitiesUserId2();
			return;
		}
		activitiesUserid2 = input.trim();
		configActivitiesPassword2();
	}

	private void configActivitiesPassword2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configActivitiesUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.ACTIVITIES));
			log.error("CLFRP0025E: Password for Activities contains invalid characters.");
			configActivitiesPassword2();
			return;
		}
		if (checkbox2)// same password
			loadSamePasswords(input.trim());
		else
			activitiesPassword2 = input.trim();
		goToNextApp("activities");
	}

	private void configBlogs2() {
		if (isFeatureSelected("blogs")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.BLOGS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, blogsDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("blogs");
				return;
			}
			TextCustomPanelUtils.logInput("blogs DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configBlogs2();
				return;
			}
			blogsDBName2 = input.trim();
			configBlogsHostName2();
		} else
			configCommunities2();
	}

	private void configBlogsHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				blogsDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configBlogs2();
			return;
		}
		TextCustomPanelUtils.logInput("blogs host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.BLOGS));
			log.error("The hostname for Blogs is not valid.");
			configBlogsHostName2();
			return;
		}
		blogsDBHostName2 = input.trim();
		configBlogsPort2();
	}

	private void configBlogsPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				blogsDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configBlogsHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("blogs port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.BLOGS));
			log.error("The port for Blogs is not valid.");
			configBlogsPort2();
			return;
		}
		blogsDBPort2 = input.trim();
		configBlogsUserId2();
	}

	private void configBlogsUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				blogsUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configBlogsPort2();
			return;
		}
		TextCustomPanelUtils.logInput("blogs user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.BLOGS));
			log.error("CLFRP0024E:  User ID for Blogs contains invalid characters.");
			configBlogsUserId2();
			return;
		}
		blogsUserid2 = input.trim();
		configBlogsPassword2();
	}

	private void configBlogsPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("blogs") > 0) {
			goToNextApp("blogs");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configBlogsUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.BLOGS));
			log.error("CLFRP0025E: Password for Blogs contains invalid characters.");
			configBlogsPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("blogs") == 0)
			loadSamePasswords(input.trim());
		else
			blogsPassword2 = input.trim();
		goToNextApp("blogs");
	}

	private void configCommunities2() {
		if (isFeatureSelected("communities")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.COMMUNITIES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, communitiesDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("communities");
				return;
			}
			TextCustomPanelUtils.logInput("communities DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configCommunities2();
				return;
			}
			communitiesDBName2 = input.trim();
			configCommunitiesHostName2();
		} else
			configDogear2();
	}

	private void configCommunitiesHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				communitiesDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCommunities2();
			return;
		}
		TextCustomPanelUtils.logInput("communities host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.COMMUNITIES));
			log.error("The hostname for Activities is not valid.");
			configCommunitiesHostName2();
			return;
		}
		communitiesDBHostName2 = input.trim();
		configCommunitiesPort2();
	}

	private void configCommunitiesPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				communitiesDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCommunitiesHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("communities port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.COMMUNITIES));
			log.error("The port for Communities is not valid.");
			configCommunitiesPort2();
			return;
		}
		communitiesDBPort2 = input.trim();
		configCommunitiesUserId2();
	}

	private void configCommunitiesUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				communitiesUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCommunitiesPort2();
			return;
		}
		TextCustomPanelUtils.logInput("communities user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.COMMUNITIES));
			log.error("CLFRP0024E:  User ID for Communities contains invalid characters.");
			configCommunitiesUserId2();
			return;
		}
		communitiesUserid2 = input.trim();
		configCommunitiesPassword2();
	}

	private void configCommunitiesPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("communities") > 0) {
			goToNextApp("communities");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCommunitiesUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.COMMUNITIES));
			log.error("CLFRP0025E: Password for Communities contains invalid characters.");
			configCommunitiesPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("communities") == 0)
			loadSamePasswords(input.trim());
		else
			communitiesPassword2 = input.trim();
		goToNextApp("communities");
	}

	private void configDogear2() {
		if (isFeatureSelected("dogear")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.DOGEAR);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, dogearDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("dogear");
				return;
			}
			TextCustomPanelUtils.logInput("dogear DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configDogear2();
				return;
			}
			dogearDBName2 = input.trim();
			configDogearHostName2();
		} else
			configMetrics2();
	}

	private void configDogearHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				dogearDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDogear2();
			return;
		}
		TextCustomPanelUtils.logInput("dogear host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.DOGEAR));
			log.error("The hostname for Bookmarks is not valid.");
			configDogearHostName2();
			return;
		}
		dogearDBHostName2 = input.trim();
		configDogearPort2();
	}

	private void configDogearPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				dogearDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDogearHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("dogear port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.DOGEAR));
			log.error("The port for Bookmarks is not valid.");
			configDogearPort2();
			return;
		}
		dogearDBPort2 = input.trim();
		configDogearUserId2();
	}

	private void configDogearUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				dogearUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configDogearPort2();
			return;
		}
		TextCustomPanelUtils.logInput("dogear user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.DOGEAR));
			log.error("CLFRP0024E:  User ID for Bookmarks contains invalid characters.");
			configDogearUserId2();
			return;
		}
		dogearUserid2 = input.trim();
		configDogearPassword2();
	}

	private void configDogearPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("dogear") > 0) {
			goToNextApp("dogear");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCommunitiesUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.DOGEAR));
			log.error("CLFRP0025E: Password for Bookmarks contains invalid characters.");
			configDogearPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("dogear") == 0)
			loadSamePasswords(input.trim());
		else
			dogearPassword2 = input.trim();
		goToNextApp("dogear");
	}

	private void configMetrics2() {
		if (isFeatureSelected("metrics")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.METRICS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, metricsDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("metrics");
				return;
			}
			TextCustomPanelUtils.logInput("metrics DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configMetrics2();
				return;
			}
			metricsDBName2 = input.trim();
			configMetricsHostName2();
		} else
			configMobile2();
	}

	private void configMetricsHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				metricsDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMetrics2();
			return;
		}
		TextCustomPanelUtils.logInput("metrics host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.METRICS));
			log.error("The hostname for Metrics is not valid.");
			configMetricsHostName2();
			return;
		}
		metricsDBHostName2 = input.trim();
		configMetricsPort2();
	}

	private void configMetricsPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				metricsDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMetricsHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("metrics port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.METRICS));
			log.error("The port for Metrics is not valid.");
			configMetricsPort2();
			return;
		}
		metricsDBPort2 = input.trim();
		configMetricsUserId2();
	}

	private void configMetricsUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				metricsUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMetricsPort2();
			return;
		}
		TextCustomPanelUtils.logInput("metrics user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.METRICS));
			log.error("CLFRP0024E:  User ID for Metrics contains invalid characters.");
			configMetricsUserId2();
			return;
		}
		metricsUserid2 = input.trim();
		configMetricsPassword2();
	}

	private void configMetricsPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("metrics") > 0) {
			goToNextApp("metrics");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMetricsUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.METRICS));
			log.error("CLFRP0025E: Password for Metrics contains invalid characters.");
			configMetricsPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("metrics") == 0)
			loadSamePasswords(input.trim());
		else
			metricsPassword2 = input.trim();
		goToNextApp("metrics");
	}

	private void configMobile2() {
		if (isFeatureSelected("mobile")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.MOBILE);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, mobileDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("mobile");
				return;
			}
			TextCustomPanelUtils.logInput("mobile DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configMobile2();
				return;
			}
			mobileDBName2 = input.trim();
			configMobileHostName2();
		} else
			configFiles2();
	}

	private void configMobileHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				mobileDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMobile2();
			return;
		}
		TextCustomPanelUtils.logInput("mobile host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.MOBILE));
			log.error("The hostname for Mobile is not valid.");
			configMobileHostName2();
			return;
		}
		mobileDBHostName2 = input.trim();
		configMobilePort2();
	}

	private void configMobilePort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				mobileDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMobileHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("mobile port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.MOBILE));
			log.error("The port for Mobile is not valid.");
			configMobilePort2();
			return;
		}
		mobileDBPort2 = input.trim();
		configMobileUserId2();
	}

	private void configMobileUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				mobileUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMobilePort2();
			return;
		}
		TextCustomPanelUtils.logInput("mobile user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.MOBILE));
			log.error("CLFRP0024E:  User ID for Mobile contains invalid characters.");
			configMobileUserId2();
			return;
		}
		mobileUserid2 = input.trim();
		configMobilePassword2();
	}

	private void configMobilePassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("mobile") > 0) {
			goToNextApp("mobile");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configMobileUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.MOBILE));
			log.error("CLFRP0025E: Password for Mobile contains invalid characters.");
			configMobilePassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("mobile") == 0)
			loadSamePasswords(input.trim());
		else
			mobilePassword2 = input.trim();
		goToNextApp("mobile");
	}

	private void configFiles2() {
		if (isFeatureSelected("files")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.FILES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, filesDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("files");
				return;
			}
			TextCustomPanelUtils.logInput("files DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configFiles2();
				return;
			}
			filesDBName2 = input.trim();
			configFilesHostName2();
		} else
			configForum2();
	}

	private void configFilesHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				filesDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configFiles2();
			return;
		}
		TextCustomPanelUtils.logInput("files host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.FILES));
			log.error("The hostname for Files is not valid.");
			configFilesHostName2();
			return;
		}
		filesDBHostName2 = input.trim();
		configFilesPort2();
	}

	private void configFilesPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				filesDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configFilesHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("files port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.FILES));
			log.error("The port for Files is not valid.");
			configFilesPort2();
			return;
		}
		filesDBPort2 = input.trim();
		configFilesUserId2();
	}

	private void configFilesUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				filesUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configFilesPort2();
			return;
		}
		TextCustomPanelUtils.logInput("files user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.FILES));
			log.error("CLFRP0024E:  User ID for Files contains invalid characters.");
			configFilesUserId2();
			return;
		}
		filesUserid2 = input.trim();
		configFilesPassword2();
	}

	private void configFilesPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("files") > 0) {
			goToNextApp("files");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configFilesUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.FILES));
			log.error("CLFRP0025E: Password for Files contains invalid characters.");
			configFilesPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("files") == 0)
			loadSamePasswords(input.trim());
		else
			filesPassword2 = input.trim();
		goToNextApp("files");
	}

	private void configForum2() {
		if (isFeatureSelected("forums")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.FORUMS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, forumDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("forums");
				return;
			}
			TextCustomPanelUtils.logInput("forum DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configForum2();
				return;
			}
			forumDBName2 = input.trim();
			configForumHostName2();
		} else
			configPushnotification2();
	}

	private void configForumHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				forumDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configForum2();
			return;
		}
		TextCustomPanelUtils.logInput("forum host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.FORUMS));
			log.error("The hostname for Forums is not valid.");
			configForumHostName2();
			return;
		}
		forumDBHostName2 = input.trim();
		configForumPort2();
	}

	private void configForumPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				forumDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configForumHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("forum port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.FORUMS));
			log.error("The port for Forums is not valid.");
			configForumPort2();
			return;
		}
		forumDBPort2 = input.trim();
		configForumUserId2();
	}

	private void configForumUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				forumUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configForumPort2();
			return;
		}
		TextCustomPanelUtils.logInput("forum user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.FORUMS));
			log.error("CLFRP0024E:  User ID for Forums contains invalid characters.");
			configForumUserId2();
			return;
		}
		forumUserid2 = input.trim();
		configForumPassword2();
	}

	private void configForumPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("forums") > 0) {
			goToNextApp("forums");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configForumUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.FORUMS));
			log.error("CLFRP0025E: Password for Forums contains invalid characters.");
			configForumPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("forums") == 0)
			loadSamePasswords(input.trim());
		else
			forumPassword2 = input.trim();
		goToNextApp("forums");
	}

	private void configPushnotification2() {
		if (isFeatureSelected("pushNotification")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.PUSH_NOTIFICATION);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, pushnotificationDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("pushnotification");
				return;
			}
			TextCustomPanelUtils.logInput("pushnotification DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configPushnotification2();
				return;
			}
			pushnotificationDBName2 = input.trim();
			configPushnotificationHostName2();
		} else
			configHomePage2();
	}

	private void configPushnotificationHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				pushnotificationDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configPushnotification2();
			return;
		}
		TextCustomPanelUtils.logInput("pushnotification host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.PUSH_NOTIFICATION));
			log.error("The hostname for pushnotification is not valid.");
			configPushnotificationHostName2();
			return;
		}
		pushnotificationDBHostName2 = input.trim();
		configPushnotificationPort2();
	}

	private void configPushnotificationPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				pushnotificationDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configPushnotificationHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("pushnotification port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.PUSH_NOTIFICATION));
			log.error("The port for pushnotification is not valid.");
			configPushnotificationPort2();
			return;
		}
		pushnotificationDBPort2 = input.trim();
		configPushnotificationUserId2();
	}

	private void configPushnotificationUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				pushnotificationUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configPushnotificationPort2();
			return;
		}
		TextCustomPanelUtils.logInput("pushnotification user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.PUSH_NOTIFICATION));
			log.error("CLFRP0024E:  User ID for pushnotification contains invalid characters.");
			configPushnotificationUserId2();
			return;
		}
		pushnotificationUserid2 = input.trim();
		configPushnotificationPassword2();
	}

	private void configPushnotificationPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("pushnotification") > 0) {
			goToNextApp("pushnotification");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configPushnotificationUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.PUSH_NOTIFICATION));
			log.error("CLFRP0025E: Password for pushnotification contains invalid characters.");
			configPushnotificationPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("pushnotification") == 0)
			loadSamePasswords(input.trim());
		else
			pushnotificationPassword2 = input.trim();
		goToNextApp("pushnotification");
	}

	private void configHomePage2() {
		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.HOMEPAGE);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, homepageDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("homepage");
				return;
			}
			TextCustomPanelUtils.logInput("homepage DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configHomePage2();
				return;
			}
			homepageDBName2 = input.trim();
			configHomePageHostName2();
		} else
			configProfiles2();
	}

	private void configHomePageHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				homepageDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configHomePage2();
			return;
		}
		TextCustomPanelUtils.logInput("homepage host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.HOMEPAGE));
			log.error("The hostname for Homepage is not valid.");
			configHomePageHostName2();
			return;
		}
		homepageDBHostName2 = input.trim();
		configHomePagePort2();
	}

	private void configHomePagePort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				homepageDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configHomePageHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("homepage port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.HOMEPAGE));
			log.error("The port for Homepage is not valid.");
			configHomePagePort2();
			return;
		}
		homepageDBPort2 = input.trim();
		configHomepageUserId2();
	}

	private void configHomepageUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				homepageUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configHomePagePort2();
			return;
		}
		TextCustomPanelUtils.logInput("homepage user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.HOMEPAGE));
			log.error("CLFRP0024E:  User ID for Homepage contains invalid characters.");
			configHomepageUserId2();
			return;
		}
		homepageUserid2 = input.trim();
		configHomepagePassword2();
	}

	private void configHomepagePassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("homepage") > 0) {
			goToNextApp("homepage");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configForumUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.HOMEPAGE));
			log.error("CLFRP0025E: Password for Homepage contains invalid characters.");
			configHomepagePassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("homepage") == 0)
			loadSamePasswords(input.trim());
		else
			homepagePassword2 = input.trim();
		goToNextApp("homepage");
	}

	private void configProfiles2() {
		if (isFeatureSelected("profiles")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.PROFILES);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, profilesDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("profiles");
				return;
			}
			TextCustomPanelUtils.logInput("profiles DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configProfiles2();
				return;
			}
			profilesDBName2 = input.trim();
			configProfilesHostName2();
		} else
			configWikis2();
	}

	private void configProfilesHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				profilesDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configProfiles2();
			return;
		}
		TextCustomPanelUtils.logInput("profiles host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.PROFILES));
			log.error("The hostname for Profiles is not valid.");
			configProfilesHostName2();
			return;
		}
		profilesDBHostName2 = input.trim();
		configProfilesPort2();
	}

	private void configProfilesPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				profilesDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configProfilesHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("profiles port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.PROFILES));
			log.error("The port for Profiles is not valid.");
			configProfilesPort2();
			return;
		}
		profilesDBPort2 = input.trim();
		configProfilesUserId2();
	}

	private void configProfilesUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				profilesUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configProfilesPort2();
			return;
		}
		TextCustomPanelUtils.logInput("profiles user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.PROFILES));
			log.error("CLFRP0024E:  User ID for Profiles contains invalid characters.");
			configProfilesUserId2();
			return;
		}
		profilesUserid2 = input.trim();
		configProfilesPassword2();
	}

	private void configProfilesPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("profiles") > 0) {
			goToNextApp("profiles");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configProfilesUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.PROFILES));
			log.error("CLFRP0025E: Password for Profiles contains invalid characters.");
			configProfilesPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("profiles") == 0)
			loadSamePasswords(input.trim());
		else
			profilesPassword2 = input.trim();
		goToNextApp("profiles");
	}

	private void configWikis2() {
		if (isFeatureSelected("wikis")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.WIKIS);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, wikisDBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("wikis");
				return;
			}
			TextCustomPanelUtils.logInput("wikis DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configWikis2();
				return;
			}
			wikisDBName2 = input.trim();
			configWikisHostName2();
		} else
			configIC3602();
	}

	private void configIC3602() {
		if (isFeatureSelected("ic360")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "
					+ Messages.IC360);
			String input = TextCustomPanelUtils.getInput(
					Messages.DB_TABLE_DB_NAME, ic360DBName2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("ic360");
				return;
			}
			TextCustomPanelUtils.logInput("ic360 DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configIC3602();
				return;
			}
			ic360DBName2 = input.trim();
			configIC360HostName2();
		} else
			configCCMGCD2();
	}

	private void configWikisHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				wikisDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configWikis2();
			return;
		}
		TextCustomPanelUtils.logInput("wikis host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.WIKIS));
			log.error("The hostname for Wikis is not valid.");
			configWikisHostName2();
			return;
		}
		wikisDBHostName2 = input.trim();
		configWikisPort2();
	}

	private void configIC360HostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				ic360DBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configIC3602();
			return;
		}
		TextCustomPanelUtils.logInput("ic360 host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.IC360));
			log.error("The hostname for ic360 is not valid.");
			configIC360HostName2();
			return;
		}
		ic360DBHostName2 = input.trim();
		configIC360Port2();
	}

	private void configWikisPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				wikisDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configWikisHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("wikis port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.WIKIS));
			log.error("The port for Wikis is not valid.");
			configWikisPort2();
			return;
		}
		wikisDBPort2 = input.trim();
		configWikisUserId2();
	}

	private void configIC360Port2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT,
				ic360DBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configIC360HostName2();
			return;
		}
		TextCustomPanelUtils.logInput("ic360 port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.IC360));
			log.error("The port for ic360 is not valid.");
			configIC360Port2();
			return;
		}
		ic360DBPort2 = input.trim();
		configIC360UserId2();
	}

	private void configWikisUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				wikisUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configWikisPort2();
			return;
		}
		TextCustomPanelUtils.logInput("wikis user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.WIKIS));
			log.error("CLFRP0024E:  User ID for Wikis contains invalid characters.");
			configWikisUserId2();
			return;
		}
		wikisUserid2 = input.trim();
		configWikisPassword2();
	}

	private void configIC360UserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				ic360Userid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configIC360Port2();
			return;
		}
		TextCustomPanelUtils.logInput("ic360 user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.IC360));
			log.error("CLFRP0024E:  User ID for ic360 contains invalid characters.");
			configIC360UserId2();
			return;
		}
		ic360Userid2 = input.trim();
		configIC360Password2();
	}

	private void configWikisPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("wikis") > 0) {
			goToNextApp("wikis");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configWikisUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.WIKIS));
			log.error("CLFRP0025E: Password for Wikis contains invalid characters.");
			configWikisPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("wikis") == 0)
			loadSamePasswords(input.trim());
		else
			wikisPassword2 = input.trim();
		goToNextApp("wikis");
	}

	private void configIC360Password2() {
		if (checkbox2 && selectFeaturesList.indexOf("ic360") > 0) {
			goToNextApp("ic360");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configIC360UserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.IC360));
			log.error("CLFRP0025E: Password for ic360 contains invalid characters.");
			configIC360Password2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("ic360") == 0)
			loadSamePasswords(input.trim());
		else
			ic360Password2 = input.trim();
		goToNextApp("ic360");
	}

	private void configCCMGCD2() {
		/* OS400_Enablement
		 * OS400 does not support filenet and disables it from the console insterface.
		 * So no need to care about it here for OS400 now.
		 * */
		if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			String input = "";
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "	+ Messages.CCM);
			TextCustomPanelUtils.showSubTitle2(Messages.GCD);
			input = TextCustomPanelUtils.getInput(
						Messages.DB_TABLE_DB_NAME, ccmGCDDBName2);
			if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("ccm");
				return;
			}
			TextCustomPanelUtils.logInput("CCM GCD DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configCCMGCD2();
				return;
			}
			ccmGCDDBName2 = input.trim();
			configCCMGCDHostName2();
		} else
			configICEC2();

	}
	private void configCCMGCDHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,
				ccmGCDDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMGCD2();
			return;
		}
		TextCustomPanelUtils.logInput("ccm GCD host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
						Messages.GCD));
			log.error("The hostname for CCM GCD is not valid.");
			configCCMGCDHostName2();
			return;
		}
		ccmGCDDBHostName2 = input.trim();
		configCCMGCDPort2();
	}

	private void configCCMGCDPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT, ccmGCDDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMGCDHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("ccm GCD port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.GCD));
			log.error("The port for CCM GCD is not valid.");
			configCCMGCDPort2();
			return;
		}
		ccmGCDDBPort2 = input.trim();
		configCCMGCDUserId2();
	}

	private void configCCMGCDUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,
				ccmGCDUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMGCDPort2();
			return;
		}
		TextCustomPanelUtils.logInput("CCM GCD user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.GCD));
			log.error("CLFRP0024E:  User ID for CCM GCD contains invalid characters.");
			configCCMGCDUserId2();
			return;
		}
		ccmGCDUserid2 = input.trim();
		configCCMGCDPassword2();
	}

	private void configCCMGCDPassword2() {
		if (checkbox2 && selectFeaturesList.indexOf("ccm") > 0) {
			configCCMObjStore2();
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMGCDUserId2();
			return;
		}
		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.GCD));
			log.error("CLFRP0025E: Password for CCM GCD contains invalid characters.");
			configCCMGCDPassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("ccm") == 0)
			loadSamePasswords(input.trim());
		else
			ccmGCDPassword2 = input.trim();

		configCCMObjStore2();
	}

	private void configCCMObjStore2() {
		TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": "	+ Messages.CCM);
		TextCustomPanelUtils.showSubTitle2(Messages.OBJECTSTORE);
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_NAME, ccmOSDBName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMGCD2();
			return;
		}
		TextCustomPanelUtils.logInput("CCM Object Store DB name",input);
		if (!verifyDBNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
			log.error("Invalid database name.");
			configCCMObjStore2();
			return;
		}
		ccmOSDBName2 = input.trim();
		configCCMObjStoreHostName2();
	}

	private void configCCMObjStoreHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST,	ccmOSDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMObjStore2();
			return;
		}
		TextCustomPanelUtils.logInput("ccm Object Store host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE,
					Messages.OBJECTSTORE));
			log.error("The hostname for CCM Object Store is not valid.");
			configCCMObjStoreHostName2();
			return;
		}
		ccmOSDBHostName2 = input.trim();
		configCCMObjStorePort2();
	}

	private void configCCMObjStorePort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT, ccmOSDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMObjStoreHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("ccm Object Store port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE,
					Messages.OBJECTSTORE));
			log.error("The port for CCM Object Store is not valid.");
			configCCMObjStorePort2();
			return;
		}
		ccmOSDBPort2 = input.trim();
		configCCMObjStoreUserId2();
	}

	private void configCCMObjStoreUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID,	ccmOSUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMObjStorePort2();
			return;
		}
		TextCustomPanelUtils.logInput("CCM Object Store user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
					Messages.OBJECTSTORE));
			log.error("CLFRP0024E:  User ID for CCM Object Store contains invalid characters.");
			configCCMObjStoreUserId2();
			return;
		}
		ccmOSUserid2 = input.trim();
		configCCMObjStorePassword2();
	}

	private void configCCMObjStorePassword2() {
		if (checkbox2) {
			goToNextApp("ccm");
			return;
		}
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD,
				null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configCCMObjStoreUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
					Messages.OBJECTSTORE));
			log.error("CLFRP0025E: Password for CCM Object Store contains invalid characters.");
			configCCMObjStorePassword2();
			return;
		}
		if (checkbox2 && selectFeaturesList.indexOf("ccm") == 0)
			loadSamePasswords(input.trim());
		else
			ccmOSPassword2 = input.trim();
		goToNextApp("ccm");
	}

	// add ICEC section
	private void configICEC2() {
		if (isFeatureSelected("icec")) {
			TextCustomPanelUtils.showSubTitle1(Messages.DB_TABLE_FEATURE + ": " + Messages.ICEC);
			String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_NAME, icecDBName2);
			if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
				returnToLastApp("icec");
				return;
			}
			TextCustomPanelUtils.logInput("community highlights DB name",input);
			if (!verifyDBNameComplete(input.trim())) {
				TextCustomPanelUtils.showError(Messages.DB_NAME_INVALID_CONSOLE);
				log.error("Invalid database name.");
				configICEC2();
				return;
			}
			icecDBName2 = input.trim();
			configICECHostName2();
		} else
			displayAppDataDiffDB();
	}

	private void configICECHostName2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_DB_HOST, icecDBHostName2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configICEC2();
			return;
		}
		TextCustomPanelUtils.logInput("community highlights host name",input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_HOSTNAME_INVALID_CONSOLE, Messages.ICEC));
			log.error("The hostname for Community Highlights is not valid.");
			configICECHostName2();
			return;
		}
		icecDBHostName2 = input.trim();
		configICECPort2();
	}

	private void configICECPort2() {
		String input = TextCustomPanelUtils.getInput(Messages.HOST_PORT, icecDBPort2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configICECHostName2();
			return;
		}
		TextCustomPanelUtils.logInput("community highlights port",input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PORT_INVALID_CONSOLE, Messages.ICEC));
			log.error("The port for Community Highlights is not valid.");
			configICECPort2();
			return;
		}
		icecDBPort2 = input.trim();
		configICECUserId2();
	}

	private void configICECUserId2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_USER_ID, icecUserid2);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configICECPort2();
			return;
		}
		TextCustomPanelUtils.logInput("community highlights user id",input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID, Messages.ICEC));
			log.error("CLFRP0024E:  User ID for Community Highlights contains invalid characters.");
			configICECUserId2();
			return;
		}
		icecUserid2 = input.trim();
		configICECPassword2();
	}

	private void configICECPassword2() {
		String input = TextCustomPanelUtils.getInput(Messages.DB_TABLE_PWD, null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configICECUserId2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.DB_PWD_CHARS_INVALID, Messages.ICEC));
			log.error("CLFRP0025E: Password for Community Highlights contains invalid characters.");
			configICECPassword2();
			return;
		}
		if (checkbox2)// same password
			loadSamePasswords(input.trim());
		else
			icecPassword2 = input.trim();

		goToNextApp("icec");
	}

	// ------------------------------------------------------------
	private void displayAppDataSameDB() {
		returnFlag = false;
		int maxLenApplication = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_FEATURE, Messages.ACTIVITIES, Messages.BLOGS,
				Messages.NEWS, Messages.SEARCH, Messages.BLOGS,
				Messages.COMMUNITIES, Messages.DOGEAR, Messages.PROFILES,
				Messages.WIKIS, Messages.FILES, Messages.FORUMS,Messages.PUSH_NOTIFICATION,
				Messages.MOBILE, Messages.METRICS, Messages.GCD, Messages.OBJECTSTORE, Messages.ICEC,
				Messages.IC360 });
		int maxLenDBName = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_DB_NAME, activitiesDBName1, blogsDBName1,
				homepageDBName1, communitiesDBName1, dogearDBName1,
				profilesDBName1, wikisDBName1, filesDBName1, forumDBName1,pushnotificationDBName1,
				mobileDBName1, metricsDBName1, ccmGCDDBName1, ccmOSDBName1, icecDBName1,
				ic360DBName1 });
		int maxLenUserId = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_USER_ID, activitiesUserid1, blogsUserid1,
				homepageUserid1, communitiesUserid1, dogearUserid1,
				profilesUserid1, wikisUserid1, filesUserid1, forumUserid1,pushnotificationUserid1,
				mobileUserid1, metricsUserid1, ccmGCDUserid1, ccmOSUserid1, icecUserid1,
				ic360Userid1 });
		int maxLengPassword = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_PWD,
				TextCustomPanelUtils.convertPassword(activitiesPassword1),
				TextCustomPanelUtils.convertPassword(blogsPassword1),
				TextCustomPanelUtils.convertPassword(homepagePassword1),
				TextCustomPanelUtils.convertPassword(communitiesPassword1),
				TextCustomPanelUtils.convertPassword(dogearPassword1),
				TextCustomPanelUtils.convertPassword(profilesPassword1),
				TextCustomPanelUtils.convertPassword(wikisPassword1),
				TextCustomPanelUtils.convertPassword(filesPassword1),
				TextCustomPanelUtils.convertPassword(forumPassword1),
				TextCustomPanelUtils.convertPassword(pushnotificationPassword1),
				TextCustomPanelUtils.convertPassword(mobilePassword1),
				TextCustomPanelUtils.convertPassword(metricsPassword1),
				TextCustomPanelUtils.convertPassword(ccmGCDPassword1),
				TextCustomPanelUtils.convertPassword(ccmOSPassword1),
				TextCustomPanelUtils.convertPassword(icecPassword1),
				TextCustomPanelUtils.convertPassword(ic360Password1)
			});
		int lenApp = maxLenApplication + 4;
		int lenDBName = maxLenDBName + 4;
		int lenUserId = maxLenUserId + 4;
		int lenPasswd = maxLengPassword + 4;

		TextCustomPanelUtils.showText("\n" + Messages.DB_TABLE_CONFIRM);
		TextCustomPanelUtils.printTitleRow(new String[] {
				Messages.DB_TABLE_FEATURE, Messages.DB_TABLE_DB_NAME,
				Messages.DB_TABLE_USER_ID, Messages.DB_TABLE_PWD }, new int[] {
				lenApp, lenDBName, lenUserId, lenPasswd });
		int i = 1;
		if (selectFeaturesList.contains("activities"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.ACTIVITIES,
							activitiesDBName1,
							activitiesUserid1,
							TextCustomPanelUtils
									.convertPassword(activitiesPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("blogs"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.BLOGS, blogsDBName1, blogsUserid1,
					TextCustomPanelUtils.convertPassword(blogsPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("communities"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.COMMUNITIES,
							communitiesDBName1,
							communitiesUserid1,
							TextCustomPanelUtils
									.convertPassword(communitiesPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("dogear"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.DOGEAR, dogearDBName1, dogearUserid1,
					TextCustomPanelUtils.convertPassword(dogearPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("metrics"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.METRICS, metricsDBName1, metricsUserid1,
					TextCustomPanelUtils.convertPassword(metricsPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("mobile"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.MOBILE, mobileDBName1, mobileUserid1,
					TextCustomPanelUtils.convertPassword(mobilePassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("files"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.FILES, filesDBName1, filesUserid1,
					TextCustomPanelUtils.convertPassword(filesPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("forums"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.FORUMS, forumDBName1, forumUserid1,
					TextCustomPanelUtils.convertPassword(forumPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("pushnotification"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.PUSH_NOTIFICATION, pushnotificationDBName1, pushnotificationUserid1,
					TextCustomPanelUtils.convertPassword(pushnotificationPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("homepage"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.HOMEPAGE, homepageDBName1, homepageUserid1,
					TextCustomPanelUtils.convertPassword(homepagePassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("profiles"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.PROFILES, profilesDBName1, profilesUserid1,
					TextCustomPanelUtils.convertPassword(profilesPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("wikis"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.WIKIS, wikisDBName1, wikisUserid1,
					TextCustomPanelUtils.convertPassword(wikisPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")){
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
						Messages.GCD, ccmGCDDBName1, ccmGCDUserid1,
						TextCustomPanelUtils.convertPassword(ccmGCDPassword1) },
						new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
						Messages.OBJECTSTORE, ccmOSDBName1, ccmOSUserid1,
						TextCustomPanelUtils.convertPassword(ccmOSPassword1) },
						new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		}
		if (selectFeaturesList.contains("icec"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.ICEC,
							icecDBName1,
							icecUserid1,
							TextCustomPanelUtils
									.convertPassword(icecPassword1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });
		if (selectFeaturesList.contains("ic360"))
			TextCustomPanelUtils.printSingleLineRow(i++, new String[] {
					Messages.IC360, ic360DBName1, ic360Userid1,
					TextCustomPanelUtils.convertPassword(ic360Password1) },
					new int[] { lenApp, lenDBName, lenUserId, lenPasswd });

		String[] indices = new String[i + 2];
		for (int j = 0; j < i - 1; j++)
			indices[j] = (j + 1) + "";
		indices[i - 1] = "";
		indices[i] = "R";
		indices[i + 1] = Messages.PREVIOUS_INPUT_INDEX;
		String input = TextCustomPanelUtils.getInput("", "", indices).trim();
		if (input.trim().toUpperCase().equals("R")) {
			loadDefaults();
			displayAppDataSameDB();
			return;
		} else if (input.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			decideUseSamePasswdSameDB();
			return;
		}
		// confirm
		else if (input.length() == 0) {
			validateDB();
			return;
		}
		returnFlag = true;
		int idx = Integer.parseInt(input);
		if(idx > selectFeaturesList.size()) idx--;
		goToApp(selectFeaturesList.get(idx - 1));
	}

	private void displayAppDataDiffDB() {
		returnFlag = false;
		int maxLenApplication = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_FEATURE, Messages.ACTIVITIES, Messages.BLOGS,
				Messages.NEWS, Messages.SEARCH, Messages.BLOGS,
				Messages.COMMUNITIES, Messages.DOGEAR, Messages.PROFILES,
				Messages.WIKIS, Messages.FILES, Messages.FORUMS,Messages.PUSH_NOTIFICATION,
				Messages.MOBILE, Messages.METRICS, Messages.GCD, Messages.OBJECTSTORE, Messages.ICEC,
				Messages.IC360 });
		int maxLenDBName = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_DB_NAME, activitiesDBName2, blogsDBName2,
				homepageDBName2, communitiesDBName2, dogearDBName2,
				profilesDBName2, wikisDBName2, filesDBName2, forumDBName2,pushnotificationDBName2,
				mobileDBName2, metricsDBName2, ccmGCDDBName2, ccmOSDBName2, icecDBName2,
				ic360DBName2 });
		int maxLenUserId = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_USER_ID, activitiesUserid2, blogsUserid2,
				homepageUserid2, communitiesUserid2, dogearUserid2,
				profilesUserid2, wikisUserid2, filesUserid2, forumUserid2, pushnotificationUserid2,
				mobileUserid2, metricsUserid2, ccmGCDUserid2, ccmOSUserid2, icecUserid2,
				ic360Userid2 });
		int maxLengPassword = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_PWD, activitiesPassword2, blogsPassword2,
				homepagePassword2, communitiesPassword2, dogearPassword2,
				profilesPassword2, wikisPassword2, filesPassword2,pushnotificationPassword2,
				forumPassword2, mobilePassword2, metricsPassword2, ccmGCDPassword2, ccmOSPassword2, icecPassword2,
				ic360Password2 });
		int maxLenHostName = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DB_TABLE_DB_HOST, activitiesDBHostName2,
				blogsDBHostName2, homepageDBHostName2, communitiesDBHostName2,
				dogearDBHostName2, profilesDBHostName2, wikisDBHostName2,
				filesDBHostName2, forumDBHostName2,pushnotificationDBHostName2, mobileDBHostName2,
				metricsDBHostName2, ccmGCDDBHostName2, ccmOSDBHostName2, icecDBHostName2,
				ic360DBHostName2 });
		int lenApp = maxLenApplication + 4;
		int lenDBName = maxLenDBName + 4;
		int lenUserId = maxLenUserId + 4;
		int lenPasswd = maxLengPassword + 4;
		int lenHostName = maxLenHostName + 4;
		int lenPort = 8;

		TextCustomPanelUtils.showText("\n" + Messages.DB_TABLE_CONFIRM);
		TextCustomPanelUtils
				.printTitleRow(new String[] { Messages.DB_TABLE_FEATURE,
						Messages.DB_TABLE_DB_NAME, Messages.DB_TABLE_DB_HOST,
						Messages.HOST_PORT, Messages.DB_TABLE_USER_ID,
						Messages.DB_TABLE_PWD }, new int[] { lenApp, lenDBName,
						lenHostName, lenPort, lenUserId, lenPasswd });
		int i = 1;
		if (selectFeaturesList.contains("activities"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.ACTIVITIES,
							activitiesDBName2,
							activitiesDBHostName2,
							activitiesDBPort2,
							activitiesUserid2,
							TextCustomPanelUtils
									.convertPassword(activitiesPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("blogs"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.BLOGS,
							blogsDBName2,
							blogsDBHostName2,
							blogsDBPort2,
							blogsUserid2,
							TextCustomPanelUtils
									.convertPassword(blogsPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("communities"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.COMMUNITIES,
							communitiesDBName2,
							communitiesDBHostName2,
							communitiesDBPort2,
							communitiesUserid2,
							TextCustomPanelUtils
									.convertPassword(communitiesPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("dogear"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.DOGEAR,
							dogearDBName2,
							dogearDBHostName2,
							dogearDBPort2,
							dogearUserid2,
							TextCustomPanelUtils
									.convertPassword(dogearPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("metrics"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.METRICS,
							metricsDBName2,
							metricsDBHostName2,
							metricsDBPort2,
							metricsUserid2,
							TextCustomPanelUtils
									.convertPassword(metricsPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("mobile"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.MOBILE,
							mobileDBName2,
							mobileDBHostName2,
							mobileDBPort2,
							mobileUserid2,
							TextCustomPanelUtils
									.convertPassword(mobilePassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("files"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.FILES,
							filesDBName2,
							filesDBHostName2,
							filesDBPort2,
							filesUserid2,
							TextCustomPanelUtils
									.convertPassword(filesPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("forums"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.FORUMS,
							forumDBName2,
							forumDBHostName2,
							forumDBPort2,
							forumUserid2,
							TextCustomPanelUtils
									.convertPassword(forumPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("pushnotification"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.PUSH_NOTIFICATION,
							pushnotificationDBName2,
							pushnotificationDBHostName2,
							pushnotificationDBPort2,
							pushnotificationUserid2,
							TextCustomPanelUtils
							.convertPassword(pushnotificationPassword2) },
							new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("homepage"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.HOMEPAGE,
							homepageDBName2,
							homepageDBHostName2,
							homepageDBPort2,
							homepageUserid2,
							TextCustomPanelUtils
									.convertPassword(homepagePassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("profiles"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.PROFILES,
							profilesDBName2,
							profilesDBHostName2,
							profilesDBPort2,
							profilesUserid2,
							TextCustomPanelUtils
									.convertPassword(profilesPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("wikis"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.WIKIS,
							wikisDBName2,
							wikisDBHostName2,
							wikisDBPort2,
							wikisUserid2,
							TextCustomPanelUtils
									.convertPassword(wikisPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		/* OS400_Enablement
		 * OS400 does not support filenet and disables it from Console interface, so no need to care about it here for OS400 now.
		 * */
		if (selectFeaturesList.contains("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")){
			TextCustomPanelUtils.printSingleLineRow(
						i++,
						new String[] {
								Messages.GCD,
								ccmGCDDBName2,
								ccmGCDDBHostName2,
								ccmGCDDBPort2,
								ccmGCDUserid2,
								TextCustomPanelUtils.convertPassword(ccmGCDPassword2) },
						new int[] { lenApp, lenDBName, lenHostName, lenPort,
								lenUserId, lenPasswd });
			TextCustomPanelUtils.printSingleLineRow(
						i++,
						new String[] {
								Messages.OBJECTSTORE,
								ccmOSDBName2,
								ccmOSDBHostName2,
								ccmOSDBPort2,
								ccmOSUserid2,
								TextCustomPanelUtils.convertPassword(ccmOSPassword2) },
						new int[] { lenApp, lenDBName, lenHostName, lenPort,
								lenUserId, lenPasswd });
		}
		if (selectFeaturesList.contains("icec"))
			TextCustomPanelUtils.printSingleLineRow(
					i++,
					new String[] {
							Messages.ICEC,
							icecDBName2,
							icecDBHostName2,
							icecDBPort2,
							icecUserid2,
							TextCustomPanelUtils
									.convertPassword(icecPassword2) },
					new int[] { lenApp, lenDBName, lenHostName, lenPort,
							lenUserId, lenPasswd });
		if (selectFeaturesList.contains("ic360"))
			TextCustomPanelUtils.printSingleLineRow(
						i++,
						new String[] {
								Messages.IC360,
								ic360DBName2,
								ic360DBHostName2,
									ic360DBPort2,
								ic360Userid2,
								TextCustomPanelUtils
										.convertPassword(ic360Password2) },
						new int[] { lenApp, lenDBName, lenHostName, lenPort,
								lenUserId, lenPasswd });
		String[] indices = new String[i];
		for (int j = 0; j < i - 1; j++)
			indices[j] = (j + 1) + "";
		indices[i - 1] = "";
		String input = TextCustomPanelUtils.getInput("", "", indices).trim();
		if (input.trim().toUpperCase().equals("R")) {
			loadDefaults();
			displayAppDataDiffDB();
			return;
		} else if (input.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			decideUseSamePasswdDiffDB();
			return;
		}// confirm
		else if (input.length() == 0) {
			validateDB();
			return;
		}
		returnFlag = true;
		int idx = Integer.parseInt(input);
		if(idx > selectFeaturesList.size()) idx--;
		goToApp(selectFeaturesList.get(idx - 1));
	}

	private String getSelectedDBType() {
		switch (dbType) {
		case 2:
			return DatabaseUtil.DBMS_ORACLE;
		case 3:
			return DatabaseUtil.DBMS_SQLSERVER;
		// OS400_Enablement
		case 4:
			return DatabaseUtil.DBMS_DB2_ISERIES;
		default:
			return DatabaseUtil.DBMS_DB2;
		}
	}

	/** configure the specified application */
	private void goToApp(String appName) {
		if (sameDB) {
			if (appName.equals("activities"))
				configActivities1();
			else if (appName.equals("blogs"))
				configBlogs1();
			else if (appName.equals("communities"))
				configCommunities1();
			else if (appName.equals("dogear"))
				configDogear1();
			else if (appName.equals("metrics"))
				configMetrics1();
			else if (appName.equals("mobile"))
				configMobile1();
			else if (appName.equals("files"))
				configFiles1();
			else if (appName.equals("forums"))
				configForum1();
			else if (appName.equals("pushnotification"))
				configPushnotification1();
			else if (appName.equals("homepage"))
				configHomePage1();
			else if (appName.equals("profiles"))
				configProfiles1();
			else if (appName.equals("wikis"))
				configWikis1();
			else if (appName.equals("ccm")){
				configCCMGCD1();
			} else if (appName.equals("icec")) {
				configICEC1();
			} else if (appName.equals("ic360")) {
				configIC3601();
			}
		} else {
			if (appName.equals("activities"))
				configActivities2();
			else if (appName.equals("blogs"))
				configBlogs2();
			else if (appName.equals("communities"))
				configCommunities2();
			else if (appName.equals("dogear"))
				configDogear2();
			else if (appName.equals("metrics"))
				configMetrics2();
			else if (appName.equals("mobile"))
				configMobile2();
			else if (appName.equals("files"))
				configFiles2();
			else if (appName.equals("forums"))
				configForum2();
			else if (appName.equals("pushnotification"))
				configPushnotification2();
			else if (appName.equals("homepage"))
				configHomePage2();
			else if (appName.equals("profiles"))
				configProfiles2();
			else if (appName.equals("wikis"))
				configWikis2();
			else if (appName.equals("ccm"))
				configCCMGCD2();
			else if (appName.equals("icec"))
				configICEC2();
			else if (appName.equals("ic360"))
				configIC3602();
		}
	}

	/**
	 * move on to the next application or the table display if user reaches the
	 * end
	 */
	private void goToNextApp(String curApp) {
		int position = selectFeaturesList.indexOf(curApp);
		if (sameDB) {
			if (returnFlag) {
				returnFlag = false;
				displayAppDataSameDB();
				return;
			}
			if (position == selectFeaturesList.size() - 1) {
				displayAppDataSameDB();
				return;
			}
			String nextApp = selectFeaturesList.get(position + 1);
			if (nextApp.equals("blogs"))
				configBlogs1();
			else if (nextApp.equals("communities"))
				configCommunities1();
			else if (nextApp.equals("dogear"))
				configDogear1();
			else if (nextApp.equals("metrics"))
				configMetrics1();
			else if (nextApp.equals("mobile"))
				configMobile1();
			else if (nextApp.equals("files"))
				configFiles1();
			else if (nextApp.equals("forums"))
				configForum1();
			else if (nextApp.equals("pushnotification"))
				configPushnotification1();
			else if (nextApp.equals("homepage"))
				configHomePage1();
			else if (nextApp.equals("profiles"))
				configProfiles1();
			else if (nextApp.equals("wikis"))
				configWikis1();
			else if (nextApp.equals("ccm"))
				configCCMGCD1();
			else if (nextApp.equals("icec"))
				configICEC1();
			else if (nextApp.equals("ic360"))
				configIC3601();
		} else {
			if (returnFlag) {
				returnFlag = false;
				displayAppDataDiffDB();
				return;
			}
			if (position == selectFeaturesList.size() - 1) {
				displayAppDataDiffDB();
				return;
			}
			String nextApp = selectFeaturesList.get(position + 1);
			if (nextApp.equals("blogs"))
				configBlogs2();
			else if (nextApp.equals("communities"))
				configCommunities2();
			else if (nextApp.equals("dogear"))
				configDogear2();
			else if (nextApp.equals("metrics"))
				configMetrics2();
			else if (nextApp.equals("mobile"))
				configMobile2();
			else if (nextApp.equals("files"))
				configFiles2();
			else if (nextApp.equals("forums"))
				configForum2();
			else if (nextApp.equals("pushnotification"))
				configPushnotification2();
			else if (nextApp.equals("homepage"))
				configHomePage2();
			else if (nextApp.equals("profiles"))
				configProfiles2();
			else if (nextApp.equals("wikis"))
				configWikis2();
			else if (nextApp.equals("ccm"))
				configCCMGCD2();
			else if (nextApp.equals("icec"))
				configICEC2();
			else if (nextApp.equals("ic360"))
				configIC3602();
		}
	}

	/** configure the last application of the current application */
	private void returnToLastApp(String curApp) {
		int position = selectFeaturesList.indexOf(curApp);
		if (sameDB) {
			if (returnFlag) {
				returnFlag = false;
				displayAppDataSameDB();
				return;
			}
			if (position == 0)
				decideUseSamePasswdSameDB();
			else {
				String lastApp = selectFeaturesList.get(position - 1);
				if (lastApp.equals("activities"))
					configActivities1();
				else if (lastApp.equals("blogs"))
					configBlogs1();
				else if (lastApp.equals("communities"))
					configCommunities1();
				else if (lastApp.equals("dogear"))
					configDogear1();
				else if (lastApp.equals("metrics"))
					configMetrics1();
				else if (lastApp.equals("mobile"))
					configMobile1();
				else if (lastApp.equals("files"))
					configFiles1();
				else if (lastApp.equals("forums"))
					configForum1();
				else if (lastApp.equals("pushnotification"))
					configPushnotification1();
				else if (lastApp.equals("homepage"))
					configHomePage1();
				else if (lastApp.equals("profiles"))
					configProfiles1();
				else if (lastApp.equals("wikis"))
					configWikis1();
				else if (lastApp.equals("ccm"))
					configCCMGCD1();
				else if (lastApp.equals("icec"))
					configICEC1();
				else if (lastApp.equals("ic360"))
					configIC3601();
			}
		} else {
			if (returnFlag) {
				returnFlag = false;
				displayAppDataDiffDB();
				return;
			}
			if (position == 0)
				decideUseSamePasswdDiffDB();
			else {
				String lastApp = selectFeaturesList.get(position - 1);
				if (lastApp.equals("activities"))
					configActivities2();
				else if (lastApp.equals("blogs"))
					configBlogs2();
				else if (lastApp.equals("communities"))
					configCommunities2();
				else if (lastApp.equals("dogear"))
					configDogear2();
				else if (lastApp.equals("metrics"))
					configMetrics2();
				else if (lastApp.equals("mobile"))
					configMobile2();
				else if (lastApp.equals("files"))
					configFiles2();
				else if (lastApp.equals("forums"))
					configForum2();
				else if (lastApp.equals("homepage"))
					configHomePage2();
				else if (lastApp.equals("profiles"))
					configProfiles2();
				else if (lastApp.equals("wikis"))
					configWikis2();
				else if (lastApp.equals("ccm"))
					configCCMGCD2();
				else if (lastApp.equals("icec"))
					configICEC2();
				else if (lastApp.equals("ic360"))
					configIC3602();
			}
		}
	}

	private void loadSamePasswords(String password) {
		if (sameDB) {
			activitiesPassword1 = password;
			blogsPassword1 = password;
			communitiesPassword1 = password;
			dogearPassword1 = password;
			profilesPassword1 = password;
			wikisPassword1 = password;
			filesPassword1 = password;
			forumPassword1 = password;
			pushnotificationPassword1 = password;
			homepagePassword1 = password;
			metricsPassword1 = password;
			mobilePassword1 = password;
			ccmGCDPassword1 = password;
			ccmOSPassword1 = password;
			icecPassword1 = password;
			ic360Password1 = password;
		} else {
			activitiesPassword2 = password;
			blogsPassword2 = password;
			communitiesPassword2 = password;
			dogearPassword2 = password;
			profilesPassword2 = password;
			wikisPassword2 = password;
			filesPassword2 = password;
			forumPassword2 = password;
			pushnotificationPassword2 = password;
			homepagePassword2 = password;
			metricsPassword2 = password;
			mobilePassword2 = password;
			ccmGCDPassword2 = password;
			ccmOSPassword2 = password;
			icecPassword2 = password;
			ic360Password2 = password;
		}
	}

	// OS400_Enablement
	private String normalizePath(String fpath) {
		if (null == fpath)
			return null;

		fpath = fpath.trim();
		if (fpath.lastIndexOf(File.separator) == fpath.length() - 1)
			return fpath.substring(0, fpath.length() - 1);
		else
			return fpath;
	}

	private void loadDefaults() {
		String osName = System.getProperty("os.name");
		/* OS400_Enablement
		 * dbType for os400 is 4, and we use the same schema and table and dbuser name as DB2 on LUW.
		if (dbType == 1) {
		***/

		if(profile == null) profile = getProfile();
			profile.setUserData("user.database.type", getSelectedDBType());

		if ((dbType == 1) || (dbType == 4)) {
			String wasInstallLoc = null;
			if (osName.toLowerCase().startsWith("os/400")) {
				if (profile == null) {
					profile = getProfile();
				}
				wasInstallLoc = normalizePath(profile.getUserData("user.was.install.location"));
			}
			if (sameDB) {
				if (isModifyInstall || isFixpackInstall) {
					if (profile != null) {
						log.info("dbHost: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbHostName"));
						hostname = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbHostName");

						log.info("dbPort: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbPort"));
						port = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbPort");

						log.info("jdbcDriver: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath"));
						jdbcDriver1 = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath");
					}
				} else {
					port = "50000";
					if (osName.startsWith("Windows")) {
						jdbcDriver1 = "C:\\Program Files\\IBM\\SQLLIB\\java";
					} else if (osName.equals("Linux")) {
						jdbcDriver1 = "/opt/ibm/db2/V11.1/java";
					} else if (osName.equals("AIX")) {
						jdbcDriver1 = "/opt/ibm/db2/V11.1/java";
					} else if (/*osName.toLowerCase().startsWith("os/400") ||*/ (dbType == 4)) {
						/* OS400_Enablement
						 * Metrics on OS400 will use the DB2 on AIX, From Connections 5.0, Metrics starts to use DB2 LUW JCC JDBC driver.
						 * */
						jdbcDriver1 = "/QIBM/ProdData/HTTP/Public/jt400/lib";
						jdbcDriverMetricsOS400 = "/QIBM/ProdData/DB2LUW/JdbcDriver";
					}
				}
				if (activitiesPassword1 != null) {
					activitiesDBName1 = DBInfo.getProperty("activities.dbName.db2");
					activitiesUserid1 = DBInfo.getProperty("activities.dbUserName.db2");
				}
				if (blogsPassword1 != null) {
					blogsDBName1 = DBInfo.getProperty("blogs.dbName.db2");
					blogsUserid1 = DBInfo.getProperty("blogs.dbUserName.db2");
				}
				if (communitiesPassword1 != null) {
					communitiesDBName1 = DBInfo.getProperty("communities.dbName.db2");
					communitiesUserid1 = DBInfo.getProperty("communities.dbUserName.db2");
				}
				if (dogearPassword1 != null) {
					dogearDBName1 = DBInfo.getProperty("dogear.dbName.db2");
					dogearUserid1 = DBInfo.getProperty("dogear.dbUserName.db2");
				}
				if (metricsPassword1 != null) {
					metricsDBName1 = DBInfo.getProperty("metrics.dbName.db2");
					metricsUserid1 = DBInfo.getProperty("metrics.dbUserName.db2");
				}
				if (mobilePassword1 != null) {
					mobileDBName1 = DBInfo.getProperty("mobile.dbName.db2");
					mobileUserid1 = DBInfo.getProperty("mobile.dbUserName.db2");
				}
				if (profilesPassword1 != null) {
					profilesDBName1 = DBInfo.getProperty("profiles.dbName.db2");
					profilesUserid1 = DBInfo.getProperty("profiles.dbUserName.db2");
				}
				if (wikisPassword1 != null) {
					wikisDBName1 = DBInfo.getProperty("wikis.dbName.db2");
					wikisUserid1 = DBInfo.getProperty("wikis.dbUserName.db2");
				}
				if (filesPassword1 != null) {
					filesDBName1 = DBInfo.getProperty("files.dbName.db2");
					filesUserid1 = DBInfo.getProperty("files.dbUserName.db2");
				}
				if (forumPassword1 != null) {
					forumDBName1 = DBInfo.getProperty("forum.dbName.db2");
					forumUserid1 = DBInfo.getProperty("forum.dbUserName.db2");
				}
				if (homepagePassword1 != null) {
					homepageDBName1 = DBInfo.getProperty("homepage.dbName.db2");
					homepageUserid1 = DBInfo.getProperty("homepage.dbUserName.db2");
				}
				if (pushnotificationPassword1 != null) {
					pushnotificationDBName1 = DBInfo.getProperty("pushnotification.dbName.db2");
					pushnotificationUserid1 = DBInfo.getProperty("pushnotification.dbUserName.db2");
				}
				if (!osName.toLowerCase().startsWith("os/400") && isFeatureSelected("ccm")) {
					String existingDeployment = profile.getUserData("user.ccm.existingDeployment");
					if (ccmGCDPassword1 != null && null != existingDeployment && existingDeployment.equals("false")){
						ccmGCDDBName1 = DBInfo.getProperty("ccm.dbName.db2.gcd");
						ccmGCDUserid1 = DBInfo.getProperty("ccm.dbUserName.db2.gcd");
					}
					if (ccmOSPassword1 != null && null != existingDeployment && existingDeployment.equals("false")) {
						ccmOSDBName1 = DBInfo.getProperty("ccm.dbName.db2.object.store");
						ccmOSUserid1 = DBInfo.getProperty("ccm.dbUserName.db2.object.store");
					}
				}
				if (icecPassword1 != null) {
					icecDBName1 = DBInfo.getProperty("icec.dbName.db2");
					icecUserid1 = DBInfo.getProperty("icec.dbUserName.db2");
				}
				if (ic360Password1 != null) {
					ic360DBName1 = DBInfo.getProperty("ic360.dbName.db2");
					ic360Userid1 = DBInfo.getProperty("ic360.dbUserName.db2");
				}
			} else {
				if (isModifyInstall || isFixpackInstall) {
					if (profile != null) {
						log.info("jdbcDriver: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath"));
						jdbcDriver2 = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath");
					}
				} else {
					if (osName.startsWith("Windows")) {
						jdbcDriver2 = "C:\\Program Files\\IBM\\SQLLIB\\java";
					} else if (osName.equals("Linux")) {
						jdbcDriver2 = "/opt/ibm/db2/V11.1/java";
					} else if (osName.equals("AIX")) {
						jdbcDriver2 = "/opt/ibm/db2/V11.1/java";
					} else if (/*osName.toLowerCase().startsWith("os/400") ||*/ (dbType == 4)) {
						/* OS400_Enablement
						 * Metrics on OS400 will use the DB2 on AIX, Starting from Connections 5.0, Metrics start to use DB2 LUW JDBC driver.
						 * */
						jdbcDriver2 = "/QIBM/ProdData/HTTP/Public/jt400/lib";
						jdbcDriverMetricsOS400 = "/QIBM/ProdData/DB2LUW/JdbcDriver";
					}
				}

				if (activitiesPassword2 != null) {
					activitiesDBPort2 = "50000";
					activitiesDBName2 = DBInfo.getProperty("activities.dbName.db2");
					activitiesUserid2 = DBInfo.getProperty("activities.dbUserName.db2");
				}
				if (blogsPassword2 != null) {
					blogsDBPort2 = "50000";
					blogsDBName2 = DBInfo.getProperty("blogs.dbName.db2");
					blogsUserid2 = DBInfo.getProperty("blogs.dbUserName.db2");
				}
				if (communitiesPassword2 != null) {
					communitiesDBPort2 = "50000";
					communitiesDBName2 = DBInfo.getProperty("communities.dbName.db2");
					communitiesUserid2 = DBInfo.getProperty("communities.dbUserName.db2");
				}
				if (dogearPassword2 != null) {
					dogearDBPort2 = "50000";
					dogearDBName2 = DBInfo.getProperty("dogear.dbName.db2");
					dogearUserid2 = DBInfo.getProperty("dogear.dbUserName.db2");
				}
				if (metricsPassword2 != null) {
					metricsDBPort2 = "50000";
					metricsDBName2 = DBInfo.getProperty("metrics.dbName.db2");
					metricsUserid2 = DBInfo.getProperty("metrics.dbUserName.db2");
				}
				if (mobilePassword2 != null) {
					mobileDBPort2 = "50000";
					mobileDBName2 = DBInfo.getProperty("mobile.dbName.db2");
					mobileUserid2 = DBInfo.getProperty("mobile.dbUserName.db2");
				}
				if (filesPassword2 != null) {
					filesDBPort2 = "50000";
					filesDBName2 = DBInfo.getProperty("files.dbName.db2");
					filesUserid2 = DBInfo.getProperty("files.dbUserName.db2");
				}
				if (forumPassword2 != null) {
					forumDBPort2 = "50000";
					forumDBName2 = DBInfo.getProperty("forum.dbName.db2");
					forumUserid2 = DBInfo.getProperty("forum.dbUserName.db2");
				}
				if (homepagePassword2 != null) {
					homepageDBPort2 = "50000";
					homepageDBName2 = DBInfo.getProperty("homepage.dbName.db2");
					homepageUserid2 = DBInfo
							.getProperty("homepage.dbUserName.db2");
				}
				if (profilesPassword2 != null) {
					profilesDBPort2 = "50000";
					profilesDBName2 = DBInfo.getProperty("profiles.dbName.db2");
					profilesUserid2 = DBInfo.getProperty("profiles.dbUserName.db2");
				}
				if (wikisPassword2 != null) {
					wikisDBPort2 = "50000";
					wikisDBName2 = DBInfo.getProperty("wikis.dbName.db2");
					wikisUserid2 = DBInfo.getProperty("wikis.dbUserName.db2");
				}
				if (pushnotificationPassword2 != null) {
					pushnotificationDBPort2 = "50000";
					pushnotificationDBName2 = DBInfo.getProperty("pushnotification.dbName.db2");
					pushnotificationUserid2 = DBInfo.getProperty("pushnotification.dbUserName.db2");
				}
				/* OS400_Enablement
				 * OS400 does not support filenet and disables it from console interface, so no need to include it here for OS400.
				 * revisit this later when support Filenet.
				 * */
				if (!osName.toLowerCase().startsWith("os/400") && isFeatureSelected("ccm")) {
					String existingDeployment = profile.getUserData("user.ccm.existingDeployment");
					if (ccmGCDPassword2!= null && null != existingDeployment && existingDeployment.equals("false")){
						ccmGCDDBPort2 = "50000";
						ccmGCDDBName2 = DBInfo.getProperty("ccm.dbName.db2.gcd");
						ccmGCDUserid2 = DBInfo.getProperty("ccm.dbUserName.db2.gcd");
					}
					if (ccmOSPassword2 != null && null != existingDeployment && existingDeployment.equals("false")) {
						ccmOSDBPort2 = "50000";
						ccmOSDBName2 = DBInfo.getProperty("ccm.dbName.db2.object.store");
						ccmOSUserid2 = DBInfo.getProperty("ccm.dbUserName.db2.object.store");
					}
				}
				if (icecPassword2 != null) {
					icecDBPort2 = "50000";
					icecDBName2 = DBInfo.getProperty("icec.dbName.db2");
					icecUserid2 = DBInfo.getProperty("icec.dbUserName.db2");
				}
				if (ic360Password2 != null) {
					ic360DBPort2 = "50000";
					ic360DBName2 = DBInfo.getProperty("ic360.dbName.db2");
					ic360Userid2 = DBInfo.getProperty("ic360.dbUserName.db2");
				}
			}
		} else if (dbType == 2) {

			if (sameDB) {
				if (isModifyInstall || isFixpackInstall) {
					if (profile != null) {
						log.info("dbHost: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbHostName"));
						hostname = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbHostName");

						log.info("dbPort: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbPort"));
						port = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbPort");

						log.info("jdbcDriver: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath"));
						jdbcDriver1 = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath");
					}
				} else {
					port = "1521";

					if (osName.startsWith("Windows")) {
						jdbcDriver1 = "C:\\oracle\\product\\10.2.0\\db_1\\jdbc\\lib";
					} else if (osName.equals("Linux")) {
						jdbcDriver1 = "/opt/oracle/product/10.2.0/db_1/jdbc/lib";
					} else if (osName.equals("AIX")) {
						jdbcDriver1 = "/opt/oracle/product/10.2.0/db_1/jdbc/lib";
					}
				}

				if (activitiesPassword1 != null) {
					activitiesDBName1 = DBInfo.getProperty("activities.dbName.oracle");
					activitiesUserid1 = DBInfo.getProperty("activities.dbUserName.oracle");
				}
				if (blogsPassword1 != null) {
					blogsDBName1 = DBInfo.getProperty("blogs.dbName.oracle");
					blogsUserid1 = DBInfo.getProperty("blogs.dbUserName.oracle");
				}
				if (communitiesPassword1 != null) {
					communitiesDBName1 = DBInfo.getProperty("communities.dbName.oracle");
					communitiesUserid1 = DBInfo.getProperty("communities.dbUserName.oracle");
				}
				if (dogearPassword1 != null) {
					dogearDBName1 = DBInfo.getProperty("dogear.dbName.oracle");
					dogearUserid1 = DBInfo.getProperty("dogear.dbUserName.oracle");
				}
				if (metricsPassword1 != null) {
					metricsDBName1 = DBInfo.getProperty("metrics.dbName.oracle");
					metricsUserid1 = DBInfo.getProperty("metrics.dbUserName.oracle");
				}
				if (mobilePassword1 != null) {
					mobileDBName1 = DBInfo.getProperty("mobile.dbName.oracle");
					mobileUserid1 = DBInfo.getProperty("mobile.dbUserName.oracle");
				}
				if (filesPassword1 != null) {
					filesDBName1 = (DBInfo.getProperty("files.dbName.oracle"));
					filesUserid1 = DBInfo.getProperty("files.dbUserName.oracle");
				}
				if (forumPassword1 != null) {
					forumDBName1 = DBInfo.getProperty("forum.dbName.oracle");
					forumUserid1 = DBInfo.getProperty("forum.dbUserName.oracle");
				}
				if (homepagePassword1 != null) {
					homepageDBName1 = DBInfo.getProperty("homepage.dbName.oracle");
					homepageUserid1 = DBInfo.getProperty("homepage.dbUserName.oracle");
				}
				if (profilesPassword1 != null) {
					profilesDBName1 = DBInfo.getProperty("profiles.dbName.oracle");
					profilesUserid1 = DBInfo.getProperty("profiles.dbUserName.oracle");
				}
				if (wikisPassword1 != null) {
					wikisDBName1 = DBInfo.getProperty("wikis.dbName.oracle");
					wikisUserid1 = DBInfo.getProperty("wikis.dbUserName.oracle");
				}
				if (pushnotificationPassword1 != null) {
					pushnotificationDBName1 = DBInfo.getProperty("pushnotification.dbName.oracle");
					pushnotificationUserid1 = DBInfo.getProperty("pushnotification.dbUserName.oracle");
				}
				String existingDeployment = profile.getUserData("user.ccm.existingDeployment");
				if (ccmGCDPassword1 != null && null != existingDeployment && existingDeployment.equals("false")){
					ccmGCDDBName1 = DBInfo.getProperty("ccm.dbName.oracle");
					ccmGCDUserid1 = DBInfo.getProperty("ccm.dbUserName.oracle.gcd");
				}
				if (ccmOSPassword1 != null && null != existingDeployment && existingDeployment.equals("false")) {
					ccmOSDBName1 = DBInfo.getProperty("ccm.dbName.oracle");
					ccmOSUserid1 = DBInfo.getProperty("ccm.dbUserName.oracle.object.store");
				}
				if (icecPassword1 != null) {
					icecDBName1 = DBInfo.getProperty("icec.dbName.oracle");
					icecUserid1 = DBInfo.getProperty("icec.dbUserName.oracle");
				}
				if (ic360Password1 != null) {
					ic360DBName1 = DBInfo.getProperty("ic360.dbName.oracle");
					ic360Userid1 = DBInfo.getProperty("ic360.dbUserName.oracle");
				}
			} else {
				if (isModifyInstall || isFixpackInstall) {
					if (profile != null) {
						log.info("jdbcDriver: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath"));
						jdbcDriver2 = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath");
					}
				} else {
					if (osName.startsWith("Windows")) {
						jdbcDriver2 = "C:\\oracle\\product\\10.2.0\\db_1\\jdbc\\lib";
					} else if (osName.equals("Linux")) {
						jdbcDriver2 = "/opt/oracle/product/10.2.0/db_1/jdbc/lib";
					} else if (osName.equals("AIX")) {
						jdbcDriver2 = ("/opt/oracle/product/10.2.0/db_1/jdbc/lib");
					}
				}

				if (activitiesPassword2 != null) {
					activitiesDBPort2 = "1521";
					activitiesDBName2 = DBInfo.getProperty("activities.dbName.oracle");
					activitiesUserid2 = DBInfo.getProperty("activities.dbUserName.oracle");
				}
				if (blogsPassword2 != null) {
					blogsDBPort2 = "1521";
					blogsDBName2 = DBInfo.getProperty("blogs.dbName.oracle");
					blogsUserid2 = DBInfo.getProperty("blogs.dbUserName.oracle");
				}
				if (communitiesPassword2 != null) {
					communitiesDBPort2 = "1521";
					communitiesDBName2 = DBInfo.getProperty("communities.dbName.oracle");
					communitiesUserid2 = DBInfo.getProperty("communities.dbUserName.oracle");
				}
				if (dogearPassword2 != null) {
					dogearDBPort2 = "1521";
					dogearDBName2 = DBInfo.getProperty("dogear.dbName.oracle");
					dogearUserid2 = DBInfo.getProperty("dogear.dbUserName.oracle");
				}
				if (metricsPassword2 != null) {
					metricsDBPort2 = "1521";
					metricsDBName2 = DBInfo.getProperty("metrics.dbName.oracle");
					metricsUserid2 = DBInfo.getProperty("metrics.dbUserName.oracle");
				}
				if (mobilePassword2 != null) {
					mobileDBPort2 = "1521";
					mobileDBName2 = DBInfo.getProperty("mobile.dbName.oracle");
					mobileUserid2 = DBInfo.getProperty("mobile.dbUserName.oracle");
				}
				if (filesPassword2 != null) {
					filesDBPort2 = "1521";
					filesDBName2 = DBInfo.getProperty("files.dbName.oracle");
					filesUserid2 = DBInfo.getProperty("files.dbUserName.oracle");
				}
				if (forumPassword2 != null) {
					forumDBPort2 = "1521";
					forumDBName2 = DBInfo.getProperty("forum.dbName.oracle");
					forumUserid2 = DBInfo.getProperty("forum.dbUserName.oracle");
				}
				if (homepagePassword2 != null) {
					homepageDBPort2 = "1521";
					homepageDBName2 = DBInfo.getProperty("homepage.dbName.oracle");
					homepageUserid2 = DBInfo.getProperty("homepage.dbUserName.oracle");
				}
				if (profilesPassword2 != null) {
					profilesDBPort2 = "1521";
					profilesDBName2 = DBInfo.getProperty("profiles.dbName.oracle");
					profilesUserid2 = DBInfo.getProperty("profiles.dbUserName.oracle");
				}
				if (wikisPassword2 != null) {
					wikisDBPort2 = "1521";
					wikisDBName2 = DBInfo.getProperty("wikis.dbName.oracle");
					wikisUserid2 = DBInfo.getProperty("wikis.dbUserName.oracle");
				}
				if (pushnotificationPassword2 != null) {
					pushnotificationDBPort2 = "1521";
					pushnotificationDBName2 = DBInfo.getProperty("pushnotification.dbName.oracle");
					pushnotificationUserid2 = DBInfo.getProperty("pushnotification.dbUserName.oracle");
				}
				String existingDeployment = profile.getUserData("user.ccm.existingDeployment");
				if (ccmGCDPassword2!= null && null != existingDeployment && existingDeployment.equals("false")){
					ccmGCDDBPort2 = "1521";
					ccmGCDDBName2 = DBInfo.getProperty("ccm.dbName.oracle");
					ccmGCDUserid2 = DBInfo.getProperty("ccm.dbUserName.oracle.gcd");
				}
				if (ccmOSPassword2 != null && null != existingDeployment && existingDeployment.equals("false")) {
					ccmOSDBPort2 = "1521";
					ccmOSDBName2 = DBInfo.getProperty("ccm.dbUserName.oracle.gcd");
					ccmOSUserid2 = DBInfo.getProperty("ccm.dbUserName.oracle.object.store");
				}
				if (icecPassword2 != null) {
					icecDBPort2 = "1521";
					icecDBName2 = DBInfo.getProperty("icec.dbName.oracle");
					icecUserid2 = DBInfo.getProperty("icec.dbUserName.oracle");
				}
				if (ic360Password2 != null) {
					ic360DBPort2 = "1521";
					ic360DBName2 = DBInfo.getProperty("ic360.dbName.oracle");
					ic360Userid2 = DBInfo.getProperty("ic360.dbUserName.oracle");
				}
			}
		} else if (dbType == 3) {
			if (sameDB) {
				if (isModifyInstall || isFixpackInstall) {
					if (profile != null) {
						log.info("dbHost: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbHostName"));
						hostname = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbHostName");

						log.info("dbPort: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbPort"));
						port = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".dbPort");

						log.info("jdbcDriver: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath"));
						jdbcDriver1 = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath");
					}
				} else {
					port = "1433";
					jdbcDriver1 = "";
				}

				if (activitiesPassword1 != null) {
					activitiesDBName1 = DBInfo.getProperty("activities.dbName.sqlserver");
					activitiesUserid1 = DBInfo.getProperty("activities.dbUserName.sqlserver");
				}
				if (blogsPassword1 != null) {
					blogsDBName1 = DBInfo.getProperty("blogs.dbName.sqlserver");
					blogsUserid1 = DBInfo.getProperty("blogs.dbUserName.sqlserver");
				}
				if (communitiesPassword1 != null) {
					communitiesDBName1 = DBInfo.getProperty("communities.dbName.sqlserver");
					communitiesUserid1 = DBInfo.getProperty("communities.dbUserName.sqlserver");
				}
				if (dogearPassword1 != null) {
					dogearDBName1 = DBInfo.getProperty("dogear.dbName.sqlserver");
					dogearUserid1 = DBInfo.getProperty("dogear.dbUserName.sqlserver");
				}
				if (metricsPassword1 != null) {
					metricsDBName1 = DBInfo.getProperty("metrics.dbName.sqlserver");
					metricsUserid1 = DBInfo.getProperty("metrics.dbUserName.sqlserver");
				}
				if (mobilePassword1 != null) {
					mobileDBName1 = DBInfo.getProperty("mobile.dbName.sqlserver");
					mobileUserid1 = DBInfo.getProperty("mobile.dbUserName.sqlserver");
				}
				if (filesPassword1 != null) {
					filesDBName1 = DBInfo.getProperty("files.dbName.sqlserver");
					filesUserid1 = DBInfo.getProperty("files.dbUserName.sqlserver");
				}
				if (forumPassword1 != null) {
					forumDBName1 = DBInfo.getProperty("forum.dbName.sqlserver");
					forumUserid1 = DBInfo.getProperty("forum.dbUserName.sqlserver");
				}
				if (homepagePassword1 != null) {
					homepageDBName1 = DBInfo.getProperty("homepage.dbName.sqlserver");
					homepageUserid1 = DBInfo.getProperty("homepage.dbUserName.sqlserver");
				}
				if (profilesPassword1 != null) {
					profilesDBName1 = DBInfo.getProperty("profiles.dbName.sqlserver");
					profilesUserid1 = DBInfo.getProperty("profiles.dbUserName.sqlserver");
				}
				if (wikisPassword1 != null) {
					wikisDBName1 = DBInfo.getProperty("wikis.dbName.sqlserver");
					wikisUserid1 = DBInfo.getProperty("wikis.dbUserName.sqlserver");
				}
				if (pushnotificationPassword1 != null) {
					pushnotificationDBName1 = DBInfo.getProperty("pushnotification.dbName.sqlserver");
					pushnotificationUserid1 = DBInfo.getProperty("pushnotification.dbUserName.sqlserver");
				}
				String existingDeployment = profile.getUserData("user.ccm.existingDeployment");
				if (ccmGCDPassword1 != null && null != existingDeployment && existingDeployment.equals("false")){
					ccmGCDDBName1 = DBInfo.getProperty("ccm.dbName.sqlserver.gcd");
					ccmGCDUserid1 = DBInfo.getProperty("ccm.dbUserName.sqlserver.gcd");
				}
				if (ccmOSPassword1 != null && null != existingDeployment && existingDeployment.equals("false")) {
					ccmOSDBName1 = DBInfo.getProperty("ccm.dbName.sqlserver.object.store");
					ccmOSUserid1 = DBInfo.getProperty("ccm.dbUserName.sqlserver.object.store");
				}
				if (icecPassword1 != null) {
					icecDBName1 = DBInfo.getProperty("icec.dbName.sqlserver");
					icecUserid1 = DBInfo.getProperty("icec.dbUserName.sqlserver");
				}
				if (ic360Password1 != null) {
					ic360DBName1 = DBInfo.getProperty("ic360.dbName.sqlserver");
					ic360Userid1 = DBInfo.getProperty("ic360.dbUserName.sqlserver");
				}
			} else {
				if (isModifyInstall || isFixpackInstall) {
					if (profile != null) {
						log.info("jdbcDriver: "+ profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath"));
						jdbcDriver2 = profile.getUserData("user."+ Constants.FEATURE_ID_COMMUNITIES +".jdbcLibraryPath");
					}
				} else {
					jdbcDriver2 = "";
				}

				if (activitiesPassword2 != null) {
					activitiesDBPort2 = "1433";
					activitiesDBName2 = DBInfo
							.getProperty("activities.dbName.sqlserver");
					activitiesUserid2 = DBInfo
							.getProperty("activities.dbUserName.sqlserver");
				}
				if (blogsPassword2 != null) {
					blogsDBPort2 = "1433";
					blogsDBName2 = DBInfo.getProperty("blogs.dbName.sqlserver");
					blogsUserid2 = DBInfo
							.getProperty("blogs.dbUserName.sqlserver");
				}
				if (communitiesPassword2 != null) {
					communitiesDBPort2 = "1433";
					communitiesDBName2 = DBInfo
							.getProperty("communities.dbName.sqlserver");
					communitiesUserid2 = DBInfo
							.getProperty("communities.dbUserName.sqlserver");
				}
				if (dogearPassword2 != null) {
					dogearDBPort2 = "1433";
					dogearDBName2 = DBInfo
							.getProperty("dogear.dbName.sqlserver");
					dogearUserid2 = DBInfo
							.getProperty("dogear.dbUserName.sqlserver");
				}
				if (metricsPassword2 != null) {
					metricsDBPort2 = "1433";
					metricsDBName2 = DBInfo
							.getProperty("metrics.dbName.sqlserver");
					metricsUserid2 = DBInfo
							.getProperty("metrics.dbUserName.sqlserver");
				}
				if (mobilePassword2 != null) {
					mobileDBPort2 = "1433";
					mobileDBName2 = DBInfo
							.getProperty("mobile.dbName.sqlserver");
					mobileUserid2 = DBInfo
							.getProperty("mobile.dbUserName.sqlserver");
				}
				if (filesPassword2 != null) {
					filesDBPort2 = "1433";
					filesDBName2 = DBInfo.getProperty("files.dbName.sqlserver");
					filesUserid2 = DBInfo
							.getProperty("files.dbUserName.sqlserver");
				}
				if (forumPassword2 != null) {
					forumDBPort2 = "1433";
					forumDBName2 = DBInfo.getProperty("forum.dbName.sqlserver");
					forumUserid2 = DBInfo
							.getProperty("forum.dbUserName.sqlserver");
				}
				if (homepagePassword2 != null) {
					homepageDBPort2 = "1433";
					homepageDBName2 = DBInfo
							.getProperty("homepage.dbName.sqlserver");
					homepageUserid2 = DBInfo.getProperty("homepage.dbUserName.sqlserver");
				}
				if (profilesPassword2 != null) {
					profilesDBPort2 = "1433";
					profilesDBName2 = DBInfo.getProperty("profiles.dbName.sqlserver");
					profilesUserid2 = DBInfo.getProperty("profiles.dbUserName.sqlserver");
				}
				if (wikisPassword2 != null) {
					wikisDBPort2 = "1433";
					wikisDBName2 = DBInfo.getProperty("wikis.dbName.sqlserver");
					wikisUserid2 = DBInfo.getProperty("wikis.dbUserName.sqlserver");
				}
				if (pushnotificationPassword2 != null) {
					pushnotificationDBPort2 = "1433";
					pushnotificationDBName2 = DBInfo.getProperty("pushnotification.dbName.sqlserver");
					pushnotificationUserid2 = DBInfo.getProperty("pushnotification.dbUserName.sqlserver");
				}
				String existingDeployment = profile.getUserData("user.ccm.existingDeployment");
				if (ccmGCDPassword2!= null && null != existingDeployment && existingDeployment.equals("false")){
					ccmGCDDBPort2 = "1433";
					ccmGCDDBName2 = DBInfo.getProperty("ccm.dbName.sqlserver.gcd");
					ccmGCDUserid2 = DBInfo.getProperty("ccm.dbUserName.sqlserver.gcd");
				}
				if (ccmOSPassword2 != null && null != existingDeployment && existingDeployment.equals("false")) {
					ccmOSDBPort2 = "1433";
					ccmOSDBName2 = DBInfo.getProperty("ccm.dbName.sqlserver.object.store");
					ccmOSUserid2 = DBInfo.getProperty("ccm.dbUserName.sqlserver.object.store");
				}
				if (icecPassword2 != null) {
					icecDBPort2 = "1433";
					icecDBName2 = DBInfo.getProperty("icec.dbName.sqlserver");
					icecUserid2 = DBInfo.getProperty("icec.dbUserName.sqlserver");
				}
				if (ic360Password2 != null) {
					ic360DBPort2 = "1433";
					ic360DBName2 = DBInfo.getProperty("ic360.dbName.sqlserver");
					ic360Userid2 = DBInfo.getProperty("ic360.dbUserName.sqlserver");
				}
			}
		}
	}

	private String getUnknowDBMessage(String dbType, String dbName, String dbUserName, String dbUserPwd, String dbHostName, String dbPort){
		String url = "";
		if(DatabaseUtil.DBMS_ORACLE.equals(dbType)){
			url = "jdbc:oracle:thin:@"+dbHostName+":"+dbPort+":"+dbName;
		}else if(DatabaseUtil.DBMS_SQLSERVER.equals(dbType)){
			url = "jdbc:microsoft:sqlserver://"+dbHostName+":"+dbPort+";DatabaseName="+dbName;
		}else {
			url = "jdbc:db2://"+dbHostName+":"+dbPort+"/"+dbName;
		}
		return Messages.bind(Messages.DB_UNKNOW_DATABASE_NEW,new Object[]{ dbName, url, dbUserName, dbUserPwd.length()});
	}

	@Override
	public void returnToTop() {
		TextCustomPanelUtils.getInputNull(Messages.BACK_TO_TOP_NULL);
		// back to top
		decideSameOrDifferentDB();
	}

	private void validateDB() {
		TextCustomPanelUtils.showProgress(Messages.VALIDATE);

		verifyHostNameComplete();
		if (!hostNameFlag)
			log.info("hostname is invalid");
		verifyPortComplete();
		if (!portFlag)
			log.info("port is invalid");
		verifyUserNameComplete();
		if (!userNameFlag)
			log.info("user name is invalid");
		verifyPasswordComplete();
		if (!passwordFlag)
			log.info("password is invalid");
		verifyJDBCDriverPathComplete(sameDB ? jdbcDriver1 : jdbcDriver2);
		dbConnection();

		if ((hostNameFlag == true) & (portFlag == true)
				& (userNameFlag == true) & (passwordFlag == true)) {
			if (dbconnection == true){
				TextCustomPanelUtils
						.showProgress(Messages.VALIDATION_SUCCESSFUL);
				setData();
				String input = TextCustomPanelUtils.getInput(
						Messages.DB_NEXT_OR_REVALIDATE, Messages.NEXT_INDEX,
						new String[] { Messages.NEXT_INDEX,
								Messages.BACK_TO_TOP_INDEX,
								Messages.PREVIOUS_INPUT_INDEX,
								Messages.VALIDATE_INDEX });
				if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
					if (sameDB)
						displayAppDataSameDB();
					else
						displayAppDataDiffDB();
					return;
				}
				if (input.trim().equalsIgnoreCase(Messages.VALIDATE_INDEX)) {
					validateDB();
					return;
				}
				if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
					decideSameOrDifferentDB();
					return;
				}
			}else {
				String input = TextCustomPanelUtils.getInput(
						Messages.BACK_TO_TOP_OR_REVALIDATE, null,
						new String[] { Messages.BACK_TO_TOP_INDEX,
								Messages.VALIDATE_INDEX });
				if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX))
					decideSameOrDifferentDB();
				else if (input.trim().equalsIgnoreCase(Messages.VALIDATE_INDEX))
					validateDB();
			}
		} else {
			String input = TextCustomPanelUtils.getInput(
					Messages.BACK_TO_TOP_OR_REVALIDATE, null,
					new String[] { Messages.BACK_TO_TOP_INDEX,
							Messages.VALIDATE_INDEX });
			if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX))
				decideSameOrDifferentDB();
			else if (input.trim().equalsIgnoreCase(Messages.VALIDATE_INDEX))
				validateDB();
		}

	}

	private void dbConnection() {
		try {
			if (sameDB) {
				if (activitiesDBName1 != null)
					ACTIVITIES_DB_NAME = activitiesDBName1.trim();
				if (blogsDBName1 != null)
					BLOGS_DB_NAME = blogsDBName1.trim();
				if (communitiesDBName1 != null)
					COMMUNITIES_DB_NAME = communitiesDBName1.trim();
				if (dogearDBName1 != null)
					DOGEAR_DB_NAME = dogearDBName1.trim();
				if (metricsDBName1 != null)
					METRICS_DB_NAME = metricsDBName1.trim();
				if (mobileDBName1 != null)
					MOBILE_DB_NAME = mobileDBName1.trim();
				if (filesDBName1 != null)
					FILES_DB_NAME = filesDBName1.trim();
				if (forumDBName1 != null)
					FORUM_DB_NAME = forumDBName1.trim();
				if (homepageDBName1 != null)
					HOMEPAGE_DB_NAME = homepageDBName1.trim();
				if (profilesDBName1 != null)
					PEOPLEDB_DB_NAME = profilesDBName1.trim();
				if (wikisDBName1 != null)
					WIKIS_DB_NAME = wikisDBName1.trim();
				if (pushnotificationDBName1 != null)
					PUSHNOTIFICATION_DB_NAME = pushnotificationDBName1.trim();
				if (ccmGCDDBName1 != null)
					CCM_DB_NAME_GCD = ccmGCDDBName1.trim();
				if (ccmOSDBName1 != null)
					CCM_DB_NAME_OBJECT_STORE = ccmOSDBName1.trim();
				if (icecDBName1 != null)
					ICEC_DB_NAME = icecDBName1.trim();
				if (ic360DBName1 != null)
					IC360_DB_NAME = ic360DBName1.trim();

			} else {
				if (activitiesDBName2 != null)
					ACTIVITIES_DB_NAME = activitiesDBName2.trim();
				if (blogsDBName2 != null)
					BLOGS_DB_NAME = blogsDBName2.trim();
				if (communitiesDBName2 != null)
					COMMUNITIES_DB_NAME = communitiesDBName2.trim();
				if (dogearDBName2 != null)
					DOGEAR_DB_NAME = dogearDBName2.trim();
				if (metricsDBName2 != null)
					METRICS_DB_NAME = metricsDBName2.trim();
				if (mobileDBName2 != null)
					MOBILE_DB_NAME = mobileDBName2.trim();
				if (filesDBName2 != null)
					FILES_DB_NAME = filesDBName2.trim();
				if (forumDBName2 != null)
					FORUM_DB_NAME = forumDBName2.trim();
				if (homepageDBName2 != null)
					HOMEPAGE_DB_NAME = homepageDBName2.trim();
				if (profilesDBName2 != null)
					PEOPLEDB_DB_NAME = profilesDBName2.trim();
				if (wikisDBName2 != null)
					WIKIS_DB_NAME = wikisDBName2.trim();
				if (pushnotificationDBName2 != null)
					PUSHNOTIFICATION_DB_NAME = pushnotificationDBName2.trim();
				if (ccmGCDDBName2 != null)
					CCM_DB_NAME_GCD = ccmGCDDBName2.trim();
				if (ccmOSDBName2 != null)
					CCM_DB_NAME_OBJECT_STORE = ccmOSDBName2.trim();
				if (icecDBName2 != null)
					ICEC_DB_NAME = icecDBName2.trim();
				if (ic360DBName2 != null)
					IC360_DB_NAME = ic360DBName2.trim();
			}

			int result = -1;
			DbInstallValidator dv = new DbInstallValidator();
			String UI_LINE_SEPARATOR = "\n";
			StringBuffer sb = new StringBuffer();
			TextCustomPanelUtils.showProgress(Messages.DB_START_VALID);

			if (isFeatureSelected("activities")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, ACTIVITIES_DB_NAME));
				if (!sameDB) {
					hostname = activitiesHostName;
					port = activitiesPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, ACTIVITIES_DB_NAME, activitiesUsername,
						activitiesPassword, getPropertiesFile(), jdbcDriver, "activities");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), ACTIVITIES_DB_NAME, activitiesUsername, activitiesPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(),
								Messages.ACTIVITIES) + UI_LINE_SEPARATOR);
				} else {
					log.info(ACTIVITIES_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("blogs")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, BLOGS_DB_NAME));
				if (!sameDB) {
					hostname = blogsHostName;
					port = blogsPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, BLOGS_DB_NAME, blogsUsername, blogsPassword,
						getPropertiesFile(), jdbcDriver, "blogs");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), BLOGS_DB_NAME, blogsUsername, blogsPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.BLOGS)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(BLOGS_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("communities")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, COMMUNITIES_DB_NAME));
				if (!sameDB) {
					hostname = communitiesHostName;
					port = communitiesPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, COMMUNITIES_DB_NAME, communitiesUsername,
						communitiesPassword, getPropertiesFile(), jdbcDriver, "communities");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), COMMUNITIES_DB_NAME, communitiesUsername, communitiesPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(),
								Messages.COMMUNITIES) + UI_LINE_SEPARATOR);
				} else {
					log.info(COMMUNITIES_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("dogear")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, DOGEAR_DB_NAME));
				if (!sameDB) {
					hostname = dogearHostName;
					port = dogearPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, DOGEAR_DB_NAME, dogearUsername, dogearPassword,
						getPropertiesFile(), jdbcDriver, "dogear");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), DOGEAR_DB_NAME, dogearUsername, dogearPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.DOGEAR)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(DOGEAR_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("metrics")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, METRICS_DB_NAME));
				if (!sameDB) {
					hostname = metricsHostName;
					port = metricsPort;
				}
				if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
					// OS400_Enablement
					// Metrics on IBM i only supports DB2 on AIX
					dv.dbConnection(getProfile(), /*getSelectedDBType()*/"DB2", hostname,
							port, METRICS_DB_NAME, metricsUsername,
							metricsPassword, getPropertiesFile(), /*jdbcDriver*/ jdbcDriverMetricsOS400, "metrics");
				} else {
					dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
							port, METRICS_DB_NAME, metricsUsername,
							metricsPassword, getPropertiesFile(), jdbcDriver, "metrics");
				}
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), METRICS_DB_NAME, metricsUsername, metricsPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.METRICS)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(METRICS_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("mobile")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, MOBILE_DB_NAME));
				if (!sameDB) {
					hostname = mobileHostName;
					port = mobilePort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, MOBILE_DB_NAME, mobileUsername, mobilePassword,
						getPropertiesFile(), jdbcDriver, "mobile");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), MOBILE_DB_NAME, mobileUsername, mobilePassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.MOBILE)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(MOBILE_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("profiles")) {

				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, PEOPLEDB_DB_NAME));

				if (!sameDB) {
					hostname = profilesHostName;
					port = profilesPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, PEOPLEDB_DB_NAME, profilesUsername,
						profilesPassword, getPropertiesFile(), jdbcDriver, "profiles");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), PEOPLEDB_DB_NAME, profilesUsername, profilesPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.PROFILES)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(PEOPLEDB_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("wikis")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, WIKIS_DB_NAME));
				if (!sameDB) {
					hostname = wikisHostName;
					port = wikisPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, WIKIS_DB_NAME, wikisUsername, wikisPassword,
						getPropertiesFile(), jdbcDriver, "wikis");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), WIKIS_DB_NAME, wikisUsername, wikisPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.WIKIS)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(WIKIS_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("files")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, FILES_DB_NAME));
				if (!sameDB) {
					hostname = filesHostName;
					port = filesPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, FILES_DB_NAME, filesUsername, filesPassword,
						getPropertiesFile(), jdbcDriver, "files");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), FILES_DB_NAME, filesUsername, filesPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.FILES)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(FILES_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("forums")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, FORUM_DB_NAME));
				if (!sameDB) {
					hostname = forumHostName;
					port = forumPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, FORUM_DB_NAME, forumUsername, forumPassword,
						getPropertiesFile(), jdbcDriver, "forums");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), FORUM_DB_NAME, forumUsername, forumPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.FORUMS)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(FORUM_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("pushNotification")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, PUSHNOTIFICATION_DB_NAME));
				if (!sameDB) {
					hostname = pushnotificationHostName;
					port = pushnotificationPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, PUSHNOTIFICATION_DB_NAME, pushnotificationUsername, pushnotificationPassword,
						getPropertiesFile(), jdbcDriver, "pushnotification");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), PUSHNOTIFICATION_DB_NAME, pushnotificationUsername, pushnotificationPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.PUSH_NOTIFICATION)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(PUSHNOTIFICATION_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("homepage") || isFeatureSelected("search")
					|| isFeatureSelected("news")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, HOMEPAGE_DB_NAME));

				if (!sameDB) {
					hostname = homepageHostName;
					port = homepagePort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, HOMEPAGE_DB_NAME, homepageUsername,
						homepagePassword, getPropertiesFile(), jdbcDriver, "homepage");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), HOMEPAGE_DB_NAME, homepageUsername, homepagePassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.HOMEPAGE));
				} else {
					log.info(HOMEPAGE_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
				if (!sameDB) {
					hostname = ccmGCDHostName;
					port = ccmGCDPort;
				}
				TextCustomPanelUtils.showProgress(Messages.bind(
							Messages.DB_VALID, CCM_DB_NAME_GCD));
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
							port, CCM_DB_NAME_GCD, ccmGCDUsername, ccmGCDPassword,
							getPropertiesFile(), jdbcDriver, "ccm");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), CCM_DB_NAME_GCD, ccmGCDUsername, ccmGCDPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.GCD)
									+ UI_LINE_SEPARATOR);
				} else {
					log.info(CCM_DB_NAME_GCD
								+ " database connection validation: PASS");
				}

				if (!sameDB) {
					hostname = ccmOSHostName;
					port = ccmOSPort;
				}

				TextCustomPanelUtils.showProgress(Messages.bind(
							Messages.DB_VALID, CCM_DB_NAME_OBJECT_STORE));
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
							port, CCM_DB_NAME_OBJECT_STORE, ccmOSUsername, ccmOSPassword,
							getPropertiesFile(), jdbcDriver, "ccm");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), CCM_DB_NAME_OBJECT_STORE, ccmOSUsername, ccmOSPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.OBJECTSTORE) + UI_LINE_SEPARATOR);
				} else {
					log.info(CCM_DB_NAME_OBJECT_STORE
								+ " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("icec")) {
				TextCustomPanelUtils.showProgress(Messages.bind(Messages.DB_VALID, ICEC_DB_NAME));
				if (!sameDB) {
					hostname = icecHostName;
					port = icecPort;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, ICEC_DB_NAME, icecUsername,
						icecPassword, getPropertiesFile(), jdbcDriver, "icec");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), ICEC_DB_NAME, icecUsername, icecPassword, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.ICEC) + UI_LINE_SEPARATOR);
				} else {
					log.info(ICEC_DB_NAME + " database connection validation: PASS");
				}
			}

			if (isFeatureSelected("ic360")) {
				TextCustomPanelUtils.showProgress(Messages.bind(
						Messages.DB_VALID, IC360_DB_NAME));
				if (!sameDB) {
					hostname = ic360HostName;
					port = ic360Port;
				}
				dv.dbConnection(getProfile(), getSelectedDBType(), hostname,
						port, IC360_DB_NAME, ic360Username, ic360Password,
						getPropertiesFile(), jdbcDriver, "ic360");
				result = dv.loadResult();
				if (result != 0) {
					if(dv.getMessage().equals(Messages.DB_UNKNOW_DATABASE_NEW))
						sb.append(getUnknowDBMessage(getSelectedDBType(), IC360_DB_NAME, ic360Username, ic360Password, hostname, port));
					else
						sb.append(Messages.bind(dv.getMessage(), Messages.IC360)
								+ UI_LINE_SEPARATOR);
				} else {
					log.info(IC360_DB_NAME
							+ " database connection validation: PASS");
				}
			}

			String msg = sb.toString();
			if (msg.length() > 0 && (msg.indexOf(Messages.GCD) != -1 || msg.indexOf(Messages.OBJECTSTORE) != -1)) {
				dbconnection = false;
				ccmFailed = true;
				TextCustomPanelUtils.showError(msg + Messages.SET_CORRECT_VALUE_CCM);
				log.error("Database Connection Fail: " + msg + "CCM database information must be set correctly otherwise installation would fail.");
			}else if(msg.length() >0){
				dbconnection = false;
				ccmFailed = false;
				TextCustomPanelUtils.showError(msg);
				log.error("Database Connection Fail: " + msg);
			} else {
				dbconnection = true;
				ccmFailed = false;
				log.info("All Database connection validation: PASS");
			}
		} catch (Exception e) {
			log.error(e);
			return;
		}
	}

	private String getPropertiesFile() {
		if (propertiesFile.equals("")) {
			Random rd = new Random();
			String rdDirPath = System.getProperty("java.io.tmpdir")
					+ File.separator + String.valueOf(rd.nextInt(999999));
			propertiesFile = rdDirPath + File.separator + "dbValidation.txt";
			return propertiesFile;
		}
		return propertiesFile;
	}

	private boolean verifyJDBCDriverPathComplete(String jdbcDriver) {
		try {
			if (!validator.JDBCLibraryFolderValidate(jdbcDriver,
					getSelectedDBType())) {

				return false;
			}
		} catch (Exception e) {
			log.error(e);
			return false;
		}
		this.jdbcDriver = sameDB ? jdbcDriver1 : jdbcDriver2;
		log.info("The JDBC Driver path for " + getSelectedDBType()
				+ " is valid: " + jdbcDriver);
		return true;
	}

	/*
	 * OS400_Enablement
	 * Metrics on OS400 will use the DB2 on AIX which has Cognos configured even though DB2_ISERIES is selected,
	 * so need to use the JDBC driver for AIX.  Starting from Connections 5.0, Metrics use the LUW JCC jdbc driver.
	 * ************************
	 * This function verifies the JDBC driver existence specifically for Metrics on OS400.
	 */
	private boolean verifyOS400MetricsJDBCDriverPathComplete(String jdbcDriver) {
		try {
			if (!validator.OS400MetricsJDBCLibraryFolderValidate(jdbcDriver,
					getSelectedDBType())) {

				return false;
			}
		} catch (Exception e) {
			log.error(e);
			return false;
		}
		this.jdbcDriverMetricsOS400 = jdbcDriver;
		log.info("The OS400 Metrics JDBC Driver path is valid: " + jdbcDriver);
		return true;
	}

	private boolean verifyUserNameComplete(String username) {
		InstallValidator installvalidator = new InstallValidator();
		return !installvalidator.containsInvalidPassword(username)
				&& !installvalidator.containsSpace(username);
	}

	private void verifyUserNameComplete() {
		InstallValidator installvalidator = new InstallValidator();
		List<String> errorList = new ArrayList<String>();
		if (isFeatureSelected("activities")) {
			if (sameDB)
				activitiesUsername = activitiesUserid1.trim();
			else
				activitiesUsername = activitiesUserid2.trim();

			if (activitiesUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator
					.containsInvalidPassword(activitiesUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.ACTIVITIES));
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDB)
				blogsUsername = blogsUserid1.trim();
			else
				blogsUsername = blogsUserid2.trim();

			if (blogsUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator.containsInvalidPassword(blogsUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.BLOGS));
			}
		}
		if (isFeatureSelected("communities")) {
			if (sameDB)
				communitiesUsername = communitiesUserid1.trim();
			else
				communitiesUsername = communitiesUserid2.trim();
			if (communitiesUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator
					.containsInvalidPassword(communitiesUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.COMMUNITIES));
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDB)
				dogearUsername = dogearUserid1.trim();
			else
				dogearUsername = dogearUserid2.trim();
			if (dogearUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator.containsInvalidPassword(dogearUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.DOGEAR));
			}
		}
		if (isFeatureSelected("metrics")) {
			if (sameDB)
				metricsUsername = metricsUserid1.trim();
			else
				metricsUsername = metricsUserid2.trim();

			if (metricsUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator
					.containsInvalidPassword(metricsUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.METRICS));
			}
		}
		if (isFeatureSelected("mobile")) {
			if (sameDB)
				mobileUsername = mobileUserid1.trim();
			else
				mobileUsername = mobileUserid2.trim();

			if (mobileUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);

			} else if (installvalidator.containsInvalidPassword(mobileUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.MOBILE));
			}
		}
		if (isFeatureSelected("profiles")) {
			if (sameDB)
				profilesUsername = profilesUserid1.trim();
			else
				profilesUsername = profilesUserid2.trim();

			if (profilesUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);

			} else if (installvalidator
					.containsInvalidPassword(profilesUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.PROFILES));
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDB)
				wikisUsername = wikisUserid1.trim();
			else
				wikisUsername = wikisUserid2.trim();

			if (wikisUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);

			} else if (installvalidator.containsInvalidPassword(wikisUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.WIKIS));
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDB)
				filesUsername = filesUserid1.trim();
			else
				filesUsername = filesUserid2.trim();

			if (filesUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);

			} else if (installvalidator.containsInvalidPassword(filesUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.FILES));
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDB)
				forumUsername = forumUserid1.trim();
			else
				forumUsername = forumUserid2.trim();

			if (forumUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);

			} else if (installvalidator.containsInvalidPassword(forumUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.FORUMS));
			}
		}

		if (isFeatureSelected("pushNotification")) {
			if (sameDB)
				pushnotificationUsername = pushnotificationUserid1.trim();
			else
				pushnotificationUsername = pushnotificationUserid2.trim();

			if (pushnotificationUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);

			} else if (installvalidator.containsInvalidPassword(pushnotificationUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.PUSH_NOTIFICATION));
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDB)
				homepageUsername = homepageUserid1.trim();
			else
				homepageUsername = homepageUserid2.trim();

			if (homepageUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator
					.containsInvalidPassword(homepageUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.HOMEPAGE));
			}
		}
		/* OS400_Enablement
		 * OS400 does not support Filenet and disables it from Console Interface, so no need to care about it here.
		 * */
		if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDB){
				ccmGCDUsername = ccmGCDUserid1.trim();
				ccmOSUsername = ccmOSUserid1.trim();
			}else{
				ccmGCDUsername = ccmGCDUserid2.trim();
				ccmOSUsername = ccmOSUserid2.trim();
			}
			if (ccmGCDUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator.containsInvalidPassword(ccmGCDUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,	Messages.GCD));
			}

			if (ccmOSUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator.containsInvalidPassword(ccmOSUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,	Messages.OBJECTSTORE));
			}
		}

		if (isFeatureSelected("icec")) {
			if (sameDB)
				icecUsername = icecUserid1.trim();
			else
				icecUsername = icecUserid2.trim();
			if (icecUsername.length() == 0) {
				errorList.add(Messages.warning_user_empty);
			} else if (installvalidator
					.containsInvalidPassword(icecUsername)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.ICEC));
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDB)
				ic360Username = ic360Userid1.trim();
			else
				ic360Username = ic360Userid2.trim();

			if (ic360Username.length() == 0) {
				errorList.add(Messages.warning_user_empty);

			} else if (installvalidator.containsInvalidPassword(ic360Username)) {
				errorList.add(Messages.bind(Messages.DB_USERNAME_CHARS_INVALID,
						Messages.IC360));
			}
		}

		if (errorList.size() > 0)
			TextCustomPanelUtils.showErrorList(errorList);
		userNameFlag = true;

	}

	private boolean verifyDBNameComplete(String dbname) {
		InstallValidator installvalidator = new InstallValidator();
		return !installvalidator.containsInvalidPassword(dbname)
				&& !installvalidator.containsSpace(dbname);
	}

	private boolean verifyPasswordComplete(String password) {
		InstallValidator installvalidator = new InstallValidator();
		return !installvalidator.containsInvalidPassword(password)
				&& !installvalidator.containsSpace(password);
	}

	private void verifyPasswordComplete() {
		InstallValidator installvalidator = new InstallValidator();
		List<String> errorList = new ArrayList<String>();
		if (isFeatureSelected("activities")) {
			if (sameDB)
				activitiesPassword = activitiesPassword1;
			else
				activitiesPassword = activitiesPassword2;

			if (activitiesPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);
				return;
			} else if (installvalidator
					.containsInvalidPassword(activitiesPassword)
					|| installvalidator.containsSpace(activitiesPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.ACTIVITIES));
				return;
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDB)
				blogsPassword = blogsPassword1;
			else
				blogsPassword = blogsPassword2;
			if (blogsPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator.containsInvalidPassword(blogsPassword)
					|| installvalidator.containsSpace(blogsPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.BLOGS));

				return;
			}
		}
		if (isFeatureSelected("communities")) {
			if (sameDB)
				communitiesPassword = communitiesPassword1;
			else
				communitiesPassword = communitiesPassword2;

			if (communitiesPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator
					.containsInvalidPassword(communitiesPassword)
					|| installvalidator.containsSpace(communitiesPassword)) {

				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.COMMUNITIES));

				return;
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDB)
				dogearPassword = dogearPassword1;
			else
				dogearPassword = dogearPassword2;
			if (dogearPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator.containsInvalidPassword(dogearPassword)
					|| installvalidator.containsSpace(dogearPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.DOGEAR));

				return;
			}
		}

		if (isFeatureSelected("metrics")) {
			if (sameDB)
				metricsPassword = metricsPassword1;
			else
				metricsPassword = metricsPassword2;
			if (metricsPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator
					.containsInvalidPassword(metricsPassword)
					|| installvalidator.containsSpace(metricsPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.METRICS));

				return;
			}
		}

		if (isFeatureSelected("mobile")) {
			if (sameDB)
				mobilePassword = mobilePassword1;
			else
				mobilePassword = mobilePassword2;
			if (mobilePassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator.containsInvalidPassword(mobilePassword)
					|| installvalidator.containsSpace(mobilePassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.MOBILE));

				return;
			}
		}

		if (isFeatureSelected("profiles")) {
			if (sameDB)
				profilesPassword = profilesPassword1;
			else
				profilesPassword = profilesPassword2;
			if (profilesPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator
					.containsInvalidPassword(profilesPassword)
					|| installvalidator.containsSpace(profilesPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.PROFILES));

				return;
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDB)
				wikisPassword = wikisPassword1;
			else
				wikisPassword = wikisPassword2;
			if (wikisPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator.containsInvalidPassword(wikisPassword)
					|| installvalidator.containsSpace(wikisPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.WIKIS));

				return;
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDB)
				filesPassword = filesPassword1;
			else
				filesPassword = filesPassword2;

			if (filesPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator.containsInvalidPassword(filesPassword)
					|| installvalidator.containsSpace(filesPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.FILES));

				return;
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDB)
				forumPassword = forumPassword1;
			else
				forumPassword = forumPassword2;

			if (forumPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator.containsInvalidPassword(forumPassword)
					|| installvalidator.containsSpace(forumPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.FORUMS));

				return;
			}
		}

		if (isFeatureSelected("pushNotification")) {
			if (sameDB)
				pushnotificationPassword = pushnotificationPassword1;
			else
				pushnotificationPassword = pushnotificationPassword2;

			if (pushnotificationPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator.containsInvalidPassword(pushnotificationPassword)
					|| installvalidator.containsSpace(pushnotificationPassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.PUSH_NOTIFICATION));

				return;
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDB)
				homepagePassword = homepagePassword1;
			else
				homepagePassword = homepagePassword2;
			if (homepagePassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);

				return;
			} else if (installvalidator
					.containsInvalidPassword(homepagePassword)
					|| installvalidator.containsSpace(homepagePassword)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.HOMEPAGE));

				return;
			}
		}
			/* OS400_Enablement
			 * OS400 does not support Filenet and disables it from Console interface, so no need to care about it here.
			 * */
			if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
				if (sameDB){
					ccmGCDPassword = ccmGCDPassword1;
					ccmOSPassword = ccmOSPassword1;
				}else{
					ccmGCDPassword = ccmGCDPassword2;
					ccmOSPassword = ccmOSPassword2;
				}

				if (ccmGCDPassword.length() == 0) {
					errorList.add(Messages.warning_password_empty);
					return;
				} else if (installvalidator.containsInvalidPassword(ccmGCDPassword)
							|| installvalidator.containsSpace(ccmGCDPassword)) {
					errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID, Messages.GCD));
					return;
				}

				if (ccmOSPassword.length() == 0) {
					errorList.add(Messages.warning_password_empty);
					return;
				} else if (installvalidator.containsInvalidPassword(ccmOSPassword)
							|| installvalidator.containsSpace(ccmOSPassword)) {
					errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID, Messages.OBJECTSTORE));
					return;
				}
			}

		if (isFeatureSelected("icec")) {
			if (sameDB)
				icecPassword = icecPassword1;
			else
				icecPassword = icecPassword2;

			if (icecPassword.length() == 0) {
				errorList.add(Messages.warning_password_empty);
				return;
			} else if (installvalidator
					.containsInvalidPassword(icecPassword)
					|| installvalidator.containsSpace(icecPassword)) {

				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.ICEC));
				return;
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDB)
				ic360Password = ic360Password1;
			else
				ic360Password = ic360Password2;
			if (ic360Password.length() == 0) {
				errorList.add(Messages.warning_password_empty);
				return;
			} else if (installvalidator.containsInvalidPassword(ic360Password)
					|| installvalidator.containsSpace(ic360Password)) {
				errorList.add(Messages.bind(Messages.DB_PWD_CHARS_INVALID,
						Messages.IC360));
				return;
			}
		}

		if (errorList.size() > 0) {
			TextCustomPanelUtils.showErrorList(errorList);
		} else
			passwordFlag = true;
	}

	private boolean verifyHostNameComplete(String hostname) {
		InstallValidator installvalidator = new InstallValidator();
		return installvalidator.hostNameValidateForWasPanel(hostname)
				&& !installvalidator.containsSpace(hostname);
	}

	private void verifyHostNameComplete() {
		InstallValidator installvalidator = new InstallValidator();
		List<String> errorList = new ArrayList<String>();
		if (isFeatureSelected("activities")) {
			if (sameDB)
				activitiesHostName = hostname.trim();
			else
				activitiesHostName = activitiesDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(activitiesHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Activities is not valid: "
							+ activitiesHostName);
				} else
					log.info("The hostname for Activities is valid: "
							+ activitiesHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDB)
				blogsHostName = hostname.trim();
			else
				blogsHostName = blogsDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(blogsHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Blogs is not valid: "
							+ blogsHostName);
				} else
					log.info("The hostname for Blogs is valid: "
							+ blogsHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("communities")) {
			if (sameDB)
				communitiesHostName = hostname.trim();
			else
				communitiesHostName = communitiesDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(communitiesHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Communities is not valid: "
							+ communitiesHostName);
				} else
					log.info("The hostname for Communities is not valid: "
							+ communitiesHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDB)
				dogearHostName = hostname.trim();
			else
				dogearHostName = dogearDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(dogearHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Dogear is not valid: "
							+ dogearHostName);
				} else
					log.info("The hostname for Dogear is valid: "
							+ dogearHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("metrics")) {
			if (sameDB)
				metricsHostName = hostname.trim();
			else
				metricsHostName = metricsDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(metricsHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Metrics is not valid: "
							+ metricsHostName);
				} else
					log.info("The hostname for metrics is valid: "
							+ metricsHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("mobile")) {
			if (sameDB)
				mobileHostName = hostname.trim();
			else
				mobileHostName = mobileDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(mobileHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Mobile is not valid: "
							+ mobileHostName);
				} else
					log.info("The hostname for mobile is valid: "
							+ mobileHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("profiles")) {
			if (sameDB)
				profilesHostName = hostname.trim();
			else
				profilesHostName = profilesDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(profilesHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Profiles is not valid: "
							+ profilesHostName);
				} else
					log.info("The hostname for Profiles is valid: "
							+ profilesHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDB)
				wikisHostName = hostname.trim();
			else
				wikisHostName = wikisDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(wikisHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Wikis is not valid: "
							+ wikisHostName);
				} else
					log.info("The hostname for Wikis is valid: "
							+ wikisHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDB)
				filesHostName = hostname.trim();
			else
				filesHostName = filesDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(filesHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Files is not valid: "
							+ filesHostName);
				} else
					log.info("The hostname for Files is valid: "
							+ filesHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDB)
				forumHostName = hostname.trim();
			else
				forumHostName = forumDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(forumHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Forum is not valid: "
							+ forumHostName);
				} else
					log.info("The hostname for Forum is valid: "
							+ forumHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("pushNotification")) {
			if (sameDB)
				pushnotificationHostName = hostname.trim();
			else
				pushnotificationHostName = pushnotificationDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(pushnotificationHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for pushnotification is not valid: "
							+ pushnotificationHostName);
				} else
					log.info("The hostname for pushnotification is valid: "
							+ pushnotificationHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDB)
				homepageHostName = hostname.trim();
			else
				homepageHostName = homepageDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(homepageHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Homepage is not valid: "
							+ homepageHostName);
				} else
					log.info("The hostname for Homepage is valid: "
							+ homepageHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}
		/* OS400_Enablement
		 * OS400 does not support filenet and disables it from Console interface, so no need to care about it here.
		 * */
		if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDB){
				ccmGCDHostName = hostname.trim();
				ccmOSHostName = hostname.trim();
			}else{
				ccmGCDHostName = ccmGCDDBHostName2.trim();
				ccmOSHostName = ccmOSDBHostName2.trim();
			}

			try {
				if (!installvalidator.hostNameValidateForWasPanel(ccmGCDHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for CCM GCD is not valid: "
								+ ccmGCDHostName);
				} else
					log.info("The hostname for CCM GCD is valid: "
								+ ccmGCDHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
			try {
				if (!installvalidator.hostNameValidateForWasPanel(ccmOSHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for CCM Object Store is not valid: "
								+ ccmOSHostName);
				} else
					log.info("The hostname for CCM Object Store is valid: "
								+ ccmOSHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("icec")) {
			if (sameDB)
				icecHostName = hostname.trim();
			else
				icecHostName = icecDBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(icecHostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for Communities Highlights is not valid: "
							+ icecHostName);
				} else
					log.info("The hostname for Communities Highlights is valid: "
							+ icecHostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDB)
				ic360HostName = hostname.trim();
			else
				ic360HostName = ic360DBHostName2.trim();
			try {
				if (!installvalidator
						.hostNameValidateForWasPanel(ic360HostName)) {
					errorList.add(installvalidator.getMessage());
					log.info("The hostname for ic360 is not valid: "
							+ ic360HostName);
				} else
					log.info("The hostname for ic360 is valid: "
							+ ic360HostName);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}
		if (errorList.size() > 0) {
			TextCustomPanelUtils.showErrorList(errorList);
			hostNameFlag = false;
		} else {
			hostNameFlag = true;
			log.info("The hostname validation for all features:  PASS");
		}

	}

	private boolean verifyPortComplete(String port) {
		InstallValidator installvalidator = new InstallValidator();
		return installvalidator.portNumValidate(port);
	}

	private void verifyPortComplete() {
		InstallValidator installvalidator = new InstallValidator();
		List<String> errorList = new ArrayList<String>();
		if (isFeatureSelected("activities")) {
			if (sameDB)
				activitiesPort = port.trim();
			else
				activitiesPort = activitiesDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(activitiesPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Activities is not valid: "
							+ activitiesPort);
				} else
					log.info("The port for Activities is valid: "
							+ activitiesPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("blogs")) {
			if (sameDB)
				blogsPort = port.trim();
			else
				blogsPort = blogsDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(blogsPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Blogs is not valid: " + blogsPort);
				} else
					log.info("The port for Blogs is valid: " + blogsPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("communities")) {
			if (sameDB)
				communitiesPort = port.trim();
			else
				communitiesPort = communitiesDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(communitiesPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Communities is not valid: "
							+ communitiesPort);
				} else
					log.info("The port for Communities is valid: "
							+ communitiesPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("dogear")) {
			if (sameDB)
				dogearPort = port.trim();
			else
				dogearPort = dogearDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(dogearPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Dogear is not valid: " + dogearPort);
				} else
					log.info("The port for Dogear is valid: " + dogearPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("metrics")) {
			if (sameDB)
				metricsPort = port.trim();
			else
				metricsPort = metricsDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(metricsPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Metrics is not valid: "
							+ metricsPort);
				} else
					log.info("The port for metrics is valid: " + metricsPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("mobile")) {
			if (sameDB)
				mobilePort = port.trim();
			else
				mobilePort = mobileDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(mobilePort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Mobile is not valid: " + mobilePort);
				} else
					log.info("The port for mobile is valid: " + mobilePort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("profiles")) {
			if (sameDB)
				profilesPort = port.trim();
			else
				profilesPort = profilesDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(profilesPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Profiles is not valid: "
							+ profilesPort);
				} else
					log.info("The port for Profiles is valid: " + profilesPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("wikis")) {
			if (sameDB)
				wikisPort = port.trim();
			else
				wikisPort = wikisDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(wikisPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Wikis is not valid: " + wikisPort);
				} else
					log.info("The port for Wikis is valid: " + wikisPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("files")) {
			if (sameDB)
				filesPort = port.trim();
			else
				filesPort = filesDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(filesPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Files is not valid: " + filesPort);
				} else
					log.info("The port for Files is valid: " + filesPort);
			} catch (Exception e) {
				log.error(e);

				return;
			}
		}

		if (isFeatureSelected("forums")) {
			if (sameDB)
				forumPort = port.trim();
			else
				forumPort = forumDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(forumPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Forum is not valid: " + forumPort);
				} else
					log.info("The port for Forum is valid: " + forumPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("pushNotification")) {
			if (sameDB)
				pushnotificationPort = port.trim();
			else
				pushnotificationPort = pushnotificationDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(pushnotificationPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for pushnotification is not valid: " + pushnotificationPort);
				} else
					log.info("The port for pushnotification is valid: " + pushnotificationPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("homepage") || isFeatureSelected("search")
				|| isFeatureSelected("news")) {
			if (sameDB)
				homepagePort = port.trim();
			else
				homepagePort = homepageDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(homepagePort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Homepage is not valid: "
							+ homepagePort);
				} else
					log.info("The port for Homepage is valid: " + homepagePort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}
		/* OS400_Enablement
		 * OS400 does not support filenet and disables from the console interface, so no need to care about it for os400 here.
		 * */
		if (isFeatureSelected("ccm") && profile.getUserData("user.ccm.existingDeployment").equals("false")) {
			if (sameDB){
				ccmGCDPort = port.trim();
				ccmOSPort = port.trim();
			}else{
				ccmGCDPort = ccmGCDDBPort2.trim();
				ccmOSPort = ccmOSDBPort2.trim();
			}

			try {
				if (!installvalidator.portNumValidate(ccmGCDPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for CCM GCD is not valid: " + ccmGCDPort);
				} else
					log.info("The port for CCM GCD is valid: " + ccmGCDPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
			try {
				if (!installvalidator.portNumValidate(ccmOSPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for CCM Object Store is not valid: " + ccmOSPort);
				} else
					log.info("The port for CCM Object Store is valid: " + ccmOSPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("icec")) {
			if (sameDB)
				icecPort = port.trim();
			else
				icecPort = icecDBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(icecPort)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for Community Highlights is not valid: "
							+ icecPort);
				} else
					log.info("The port for Community Highlights is valid: "
							+ icecPort);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (isFeatureSelected("ic360")) {
			if (sameDB)
				ic360Port = port.trim();
			else
				ic360Port = ic360DBPort2.trim();
			try {
				if (!installvalidator.portNumValidate(ic360Port)) {
					errorList.add(installvalidator.getMessage());
					log.info("The port for ic360 is not valid: " + ic360Port);
				} else
					log.info("The port for ic360 is valid: " + ic360Port);
			} catch (Exception e) {
				log.error(e);
				return;
			}
		}

		if (errorList.size() > 0) {
			portFlag = false;
			TextCustomPanelUtils.showErrorList(errorList);
		} else {
			portFlag = true;
			log.info("The ports validation for all features:  PASS");
		}
	}

	private void setData() {
		profile = getProfile();
		log.info("db setData");
		if (sameDB) {
			if (activitiesDBName1 != null)
				ACTIVITIES_DB_NAME = activitiesDBName1.trim();
			if (blogsDBName1 != null)
				BLOGS_DB_NAME = blogsDBName1.trim();
			if (communitiesDBName1 != null)
				COMMUNITIES_DB_NAME = communitiesDBName1.trim();
			if (dogearDBName1 != null)
				DOGEAR_DB_NAME = dogearDBName1.trim();
			if (filesDBName1 != null)
				FILES_DB_NAME = filesDBName1.trim();
			if (forumDBName1 != null)
				FORUM_DB_NAME = forumDBName1.trim();
			if (homepageDBName1 != null)
				HOMEPAGE_DB_NAME = homepageDBName1.trim();
			if (profilesDBName1 != null)
				PEOPLEDB_DB_NAME = profilesDBName1.trim();
			if (wikisDBName1 != null)
				WIKIS_DB_NAME = wikisDBName1.trim();
			if (metricsDBName1 != null)
				METRICS_DB_NAME = metricsDBName1.trim();
			if (mobileDBName1 != null)
				MOBILE_DB_NAME = mobileDBName1.trim();
			if (pushnotificationDBName1 != null)
				PUSHNOTIFICATION_DB_NAME = pushnotificationDBName1.trim();
			if (ccmGCDDBName1 != null){
				CCM_DB_NAME_GCD = ccmGCDDBName1;
			}
			if (ccmOSDBName1 != null){
				CCM_DB_NAME_OBJECT_STORE = ccmOSDBName1;
			}
			if (icecDBName1 != null)
				ICEC_DB_NAME = icecDBName1.trim();
			if (ic360DBName1 != null)
				IC360_DB_NAME = ic360DBName1.trim();
		} else {
			if (activitiesDBName2 != null)
				ACTIVITIES_DB_NAME = activitiesDBName2.trim();
			if (blogsDBName2 != null)
				BLOGS_DB_NAME = blogsDBName2.trim();
			if (communitiesDBName2 != null)
				COMMUNITIES_DB_NAME = communitiesDBName2.trim();
			if (dogearDBName2 != null)
				DOGEAR_DB_NAME = dogearDBName2.trim();
			if (filesDBName2 != null)
				FILES_DB_NAME = filesDBName2.trim();
			if (forumDBName2 != null)
				FORUM_DB_NAME = forumDBName2.trim();
			if (homepageDBName2 != null)
				HOMEPAGE_DB_NAME = homepageDBName2.trim();
			if (profilesDBName2 != null)
				PEOPLEDB_DB_NAME = profilesDBName2.trim();
			if (wikisDBName2 != null)
				WIKIS_DB_NAME = wikisDBName2.trim();
			if (metricsDBName2 != null)
				METRICS_DB_NAME = metricsDBName2.trim();
			if (mobileDBName2 != null)
				MOBILE_DB_NAME = mobileDBName2.trim();
			if (pushnotificationDBName2 != null)
				PUSHNOTIFICATION_DB_NAME = pushnotificationDBName2.trim();
			if (ccmGCDDBName2 != null){
				CCM_DB_NAME_GCD = ccmGCDDBName2;
			}
			if (ccmOSDBName2 != null){
				CCM_DB_NAME_OBJECT_STORE = ccmOSDBName2;
			}
			if (icecDBName2 != null)
				ICEC_DB_NAME = icecDBName2.trim();
			if (ic360DBName2 != null)
				IC360_DB_NAME = ic360DBName2.trim();
		}

		if (isFeatureSelected(Constants.FEATURE_ID_ACTIVITIES)) {
			log.info("db setData : activities");
			TextCustomPanelUtils.logUserData("user.activities.dbHostName: ", activitiesHostName);
			TextCustomPanelUtils.logUserData("user.activities.dbPort: ", activitiesPort);
			TextCustomPanelUtils.logUserData("user.activities.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.activities.dbName: ", ACTIVITIES_DB_NAME);
			TextCustomPanelUtils.logUserData("user.activities.dbUser: ", activitiesUsername);
			TextCustomPanelUtils.logUserData("user.activities.dbType: ", getSelectedDBType());

			profile.setUserData("user.activities.dbHostName", activitiesHostName);
			profile.setUserData("user.activities.dbPort", activitiesPort);
			profile.setUserData("user.activities.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.activities.dbName", ACTIVITIES_DB_NAME);
			profile.setUserData("user.activities.dbUser", activitiesUsername);
			profile.setUserData("user.activities.dbUserPassword", EncryptionUtils.encrypt(Util.xor(activitiesPassword)));
			profile.setUserData("user.activities.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_BLOGS)) {
			log.info("db setData : blogs");
			TextCustomPanelUtils.logUserData("user.blogs.dbHostName: ", blogsHostName);
			TextCustomPanelUtils.logUserData("user.blogs.dbPort: ", blogsPort);
			TextCustomPanelUtils.logUserData("user.blogs.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.blogs.dbName: ", BLOGS_DB_NAME);
			TextCustomPanelUtils.logUserData("user.blogs.dbUser: ", blogsUsername);
			TextCustomPanelUtils.logUserData("user.blogs.dbType: ", getSelectedDBType());

			profile.setUserData("user.blogs.dbHostName", blogsHostName);
			profile.setUserData("user.blogs.dbPort", blogsPort);
			profile.setUserData("user.blogs.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.blogs.dbName", BLOGS_DB_NAME);
			profile.setUserData("user.blogs.dbUser", blogsUsername);
			profile.setUserData("user.blogs.dbUserPassword", EncryptionUtils.encrypt(Util.xor(blogsPassword)));
			profile.setUserData("user.blogs.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_COMMUNITIES)) {
			log.info("db setData : communities");
			TextCustomPanelUtils.logUserData("user.communities.dbHostName: ", communitiesHostName);
			TextCustomPanelUtils.logUserData("user.communities.dbPort: ", communitiesPort);
			TextCustomPanelUtils.logUserData("user.communities.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.communities.dbName: ", COMMUNITIES_DB_NAME);
			TextCustomPanelUtils.logUserData("user.communities.dbUser: ", communitiesUsername);
			TextCustomPanelUtils.logUserData("user.communities.dbType: ", getSelectedDBType());

			profile.setUserData("user.communities.dbHostName", communitiesHostName);
			profile.setUserData("user.communities.dbPort", communitiesPort);
			profile.setUserData("user.communities.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.communities.dbName", COMMUNITIES_DB_NAME);
			profile.setUserData("user.communities.dbUser", communitiesUsername);
			profile.setUserData("user.communities.dbUserPassword", EncryptionUtils.encrypt(Util.xor(communitiesPassword)));
			profile.setUserData("user.communities.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_BOOKMARKS)) {
			log.info("db setData : dogear");
			TextCustomPanelUtils.logUserData("user.dogear.dbHostName: ", dogearHostName);
			TextCustomPanelUtils.logUserData("user.dogear.dbPort: ", dogearPort);
			TextCustomPanelUtils.logUserData("user.dogear.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.dogear.dbName: ", DOGEAR_DB_NAME);
			TextCustomPanelUtils.logUserData("user.dogear.dbUser: ", dogearUsername);
			TextCustomPanelUtils.logUserData("user.dogear.dbType: ", getSelectedDBType());

			profile.setUserData("user.dogear.dbHostName", dogearHostName);
			profile.setUserData("user.dogear.dbPort", dogearPort);
			profile.setUserData("user.dogear.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.dogear.dbName", DOGEAR_DB_NAME);
			profile.setUserData("user.dogear.dbUser", dogearUsername);
			profile.setUserData("user.dogear.dbUserPassword", EncryptionUtils.encrypt(Util.xor(dogearPassword)));
			profile.setUserData("user.dogear.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_PROFILES)) {
			log.info("db setData : profiles");
			TextCustomPanelUtils.logUserData("user.profiles.dbHostName: ", profilesHostName);
			TextCustomPanelUtils.logUserData("user.profiles.dbPort: ", profilesPort);
			TextCustomPanelUtils.logUserData("user.profiles.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.profiles.dbName: ", PEOPLEDB_DB_NAME);
			TextCustomPanelUtils.logUserData("user.profiles.dbUser: ", profilesUsername);
			TextCustomPanelUtils.logUserData("user.profiles.dbType: ", getSelectedDBType());

			profile.setUserData("user.profiles.dbHostName", profilesHostName);
			profile.setUserData("user.profiles.dbPort", profilesPort);
			profile.setUserData("user.profiles.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.profiles.dbName", PEOPLEDB_DB_NAME);
			profile.setUserData("user.profiles.dbUser", profilesUsername);
			profile.setUserData("user.profiles.dbUserPassword", EncryptionUtils.encrypt(Util.xor(profilesPassword)));
			profile.setUserData("user.profiles.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_WIKIS)) {
			log.info("db setData : wikis");
			TextCustomPanelUtils.logUserData("user.wikis.dbHostName: ", wikisHostName);
			TextCustomPanelUtils.logUserData("user.wikis.dbPort: ", wikisPort);
			TextCustomPanelUtils.logUserData("user.wikis.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.wikis.dbName: ", WIKIS_DB_NAME);
			TextCustomPanelUtils.logUserData("user.wikis.dbUser: ", wikisUsername);
			TextCustomPanelUtils.logUserData("user.wikis.dbType: ", getSelectedDBType());

			profile.setUserData("user.wikis.dbHostName", wikisHostName);
			profile.setUserData("user.wikis.dbPort", wikisPort);
			profile.setUserData("user.wikis.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.wikis.dbName", WIKIS_DB_NAME);
			profile.setUserData("user.wikis.dbUser", wikisUsername);
			profile.setUserData("user.wikis.dbUserPassword", EncryptionUtils.encrypt(Util.xor(wikisPassword)));
			profile.setUserData("user.wikis.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_FILES)) {
			log.info("db setData : files");
			TextCustomPanelUtils.logUserData("user.files.dbHostName: ", filesHostName);
			TextCustomPanelUtils.logUserData("user.files.dbPort: ", filesPort);
			TextCustomPanelUtils.logUserData("user.files.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.files.dbName: ", FILES_DB_NAME);
			TextCustomPanelUtils.logUserData("user.files.dbUser: ", filesUsername);
			TextCustomPanelUtils.logUserData("user.files.dbType: ", getSelectedDBType());

			profile.setUserData("user.files.dbHostName", filesHostName);
			profile.setUserData("user.files.dbPort", filesPort);
			profile.setUserData("user.files.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.files.dbName", FILES_DB_NAME);
			profile.setUserData("user.files.dbUser", filesUsername);
			profile.setUserData("user.files.dbUserPassword", EncryptionUtils.encrypt(Util.xor(filesPassword)));
			profile.setUserData("user.files.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_FORUMS)) {
			log.info("db setData : forums");
			TextCustomPanelUtils.logUserData("user.forums.dbHostName: ", forumHostName);
			TextCustomPanelUtils.logUserData("user.forums.dbPort: ", forumPort);
			TextCustomPanelUtils.logUserData("user.forums.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.forums.dbName: ", FORUM_DB_NAME);
			TextCustomPanelUtils.logUserData("user.forums.dbUser: ", forumUsername);
			TextCustomPanelUtils.logUserData("user.forums.dbType: ", getSelectedDBType());

			profile.setUserData("user.forums.dbHostName", forumHostName);
			profile.setUserData("user.forums.dbPort", forumPort);
			profile.setUserData("user.forums.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.forums.dbName", FORUM_DB_NAME);
			profile.setUserData("user.forums.dbUser", forumUsername);
			profile.setUserData("user.forums.dbUserPassword", EncryptionUtils.encrypt(Util.xor(forumPassword)));
			profile.setUserData("user.forums.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_NEWS) || isFeatureSelected(Constants.FEATURE_ID_SEARCH) || isFeatureSelected(Constants.FEATURE_ID_HOMEPAGE)) {
			log.info("db setData : news");
			TextCustomPanelUtils.logUserData("user.news.dbHostName: ", homepageHostName);
			TextCustomPanelUtils.logUserData("user.news.dbPort: ", homepagePort);
			TextCustomPanelUtils.logUserData("user.news.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.news.dbName: ", HOMEPAGE_DB_NAME);
			TextCustomPanelUtils.logUserData("user.news.dbUser: ", homepageUsername);
			TextCustomPanelUtils.logUserData("user.news.dbType: ", getSelectedDBType());

			profile.setUserData("user.news.dbHostName", homepageHostName);
			profile.setUserData("user.news.dbPort", homepagePort);
			profile.setUserData("user.news.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.news.dbName", HOMEPAGE_DB_NAME);
			profile.setUserData("user.news.dbUser", homepageUsername);
			profile.setUserData("user.news.dbUserPassword", EncryptionUtils.encrypt(Util.xor(homepagePassword)));
			profile.setUserData("user.news.dbType", getSelectedDBType());

			log.info("db setData : search");
			TextCustomPanelUtils.logUserData("user.search.dbHostName: ", homepageHostName);
			TextCustomPanelUtils.logUserData("user.search.dbPort: ", homepagePort);
			TextCustomPanelUtils.logUserData("user.search.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.search.dbName: ", HOMEPAGE_DB_NAME);
			TextCustomPanelUtils.logUserData("user.search.dbUser: ", homepageUsername);
			TextCustomPanelUtils.logUserData("user.search.dbType: ", getSelectedDBType());

			profile.setUserData("user.search.dbHostName", homepageHostName);
			profile.setUserData("user.search.dbPort", homepagePort);
			profile.setUserData("user.search.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.search.dbName", HOMEPAGE_DB_NAME);
			profile.setUserData("user.search.dbUser", homepageUsername);
			profile.setUserData("user.search.dbUserPassword", EncryptionUtils.encrypt(Util.xor(homepagePassword)));
			profile.setUserData("user.search.dbType", getSelectedDBType());

			log.info("db setData : homepage");
			TextCustomPanelUtils.logUserData("user.homepage.dbHostName: ", homepageHostName);
			TextCustomPanelUtils.logUserData("user.homepage.dbPort: ", homepagePort);
			TextCustomPanelUtils.logUserData("user.homepage.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.homepage.dbName: ", HOMEPAGE_DB_NAME);
			TextCustomPanelUtils.logUserData("user.homepage.dbUser: ", homepageUsername);
			TextCustomPanelUtils.logUserData("user.homepage.dbType: ", getSelectedDBType());

			profile.setUserData("user.homepage.dbHostName", homepageHostName);
			profile.setUserData("user.homepage.dbPort", homepagePort);
			profile.setUserData("user.homepage.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.homepage.dbName", HOMEPAGE_DB_NAME);
			profile.setUserData("user.homepage.dbUser", homepageUsername);
			profile.setUserData("user.homepage.dbUserPassword", EncryptionUtils.encrypt(Util.xor(homepagePassword)));
			profile.setUserData("user.homepage.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_METRICS)) {
			log.info("db setData : metrics");
			if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
				// OS400_Enablement
				/* Metrics on OS400 will use the DB2 on AIX which has Cognos configured even though DB2_Iseries is selected,
				 * so need to use the JDBC driver for AIX.  Here set the default one to the the JDBC driver provided by WAS.
				 * WAS8:/qibm/proddata/WebSphere/AppServer/V8c/ND/deploytool/itp/plugins/com.ibm.datatools.db2_2.1.102.v20120412_2209/driver
				 * WAS7:/qibm/proddata/WebSphere/AppServer/V7c/ND/deploytool/itp/plugins/com.ibm.datatools.db2_2.1.102.v20100709_0407/driver
				 * */
				TextCustomPanelUtils.logUserData("user.metrics.dbHostName: ", metricsHostName);
				TextCustomPanelUtils.logUserData("user.metrics.dbPort: ", metricsPort);
				TextCustomPanelUtils.logUserData("user.metrics.jdbcLibraryPath: ", transferPath(jdbcDriverMetricsOS400));
				TextCustomPanelUtils.logUserData("user.metrics.dbName: ", METRICS_DB_NAME);
				TextCustomPanelUtils.logUserData("user.metrics.dbUser: ", metricsUsername);
				TextCustomPanelUtils.logUserData("user.metrics.dbType: ", /*getSelectedDBType()*/"DB2");
				TextCustomPanelUtils.logUserData("user.metrics.dataSourceTemplateName: ",
						/*getSelectedDataSourceTemplateName()*/"DB2 Universal JDBC Driver DataSource");
				TextCustomPanelUtils.logUserData("user.metrics.jdbcProviderTemplateName: ",
						/*getSelectedJdbcProviderTemplateName()*/"DB2 Universal JDBC Driver Provider");

				profile.setUserData("user.metrics.dbHostName", metricsHostName);
				profile.setUserData("user.metrics.dbPort", metricsPort);
				profile.setUserData("user.metrics.jdbcLibraryPath", transferPath(jdbcDriverMetricsOS400));
				profile.setUserData("user.metrics.dbName", METRICS_DB_NAME);
				profile.setUserData("user.metrics.dbUser", metricsUsername);
				profile.setUserData("user.metrics.dbUserPassword", EncryptionUtils.encrypt(Util.xor(metricsPassword)));
				profile.setUserData("user.metrics.dbType", /*getSelectedDBType()*/"DB2");
				profile.setUserData("user.metrics.dataSourceTemplateName",
						/*getSelectedDataSourceTemplateName()*/"DB2 Universal JDBC Driver DataSource");
				profile.setUserData("user.metrics.jdbcProviderTemplateName",
						/*getSelectedJdbcProviderTemplateName()*/"DB2 Universal JDBC Driver Provider");
			} else {
				TextCustomPanelUtils.logUserData("user.metrics.dbHostName: ", metricsHostName);
				TextCustomPanelUtils.logUserData("user.metrics.dbPort: ", metricsPort);
				TextCustomPanelUtils.logUserData("user.metrics.jdbcLibraryPath: ", transferPath(jdbcDriver));
				TextCustomPanelUtils.logUserData("user.metrics.dbName: ", METRICS_DB_NAME);
				TextCustomPanelUtils.logUserData("user.metrics.dbUser: ", metricsUsername);
				TextCustomPanelUtils.logUserData("user.metrics.dbType: ", getSelectedDBType());

				profile.setUserData("user.metrics.dbHostName", metricsHostName);
				profile.setUserData("user.metrics.dbPort", metricsPort);
				profile.setUserData("user.metrics.jdbcLibraryPath", transferPath(jdbcDriver));
				profile.setUserData("user.metrics.dbName", METRICS_DB_NAME);
				profile.setUserData("user.metrics.dbUser", metricsUsername);
				profile.setUserData("user.metrics.dbUserPassword", EncryptionUtils.encrypt(Util.xor(metricsPassword)));
				profile.setUserData("user.metrics.dbType", getSelectedDBType());
			}

		}
		if (isFeatureSelected(Constants.FEATURE_ID_MOBILE)) {
			log.info("db setData : mobile");
			TextCustomPanelUtils.logUserData("user.mobile.dbHostName: ", mobileHostName);
			TextCustomPanelUtils.logUserData("user.mobile.dbPort: ", mobilePort);
			TextCustomPanelUtils.logUserData("user.mobile.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.mobile.dbName: ", MOBILE_DB_NAME);
			TextCustomPanelUtils.logUserData("user.mobile.dbUser: ", mobileUsername);
			TextCustomPanelUtils.logUserData("user.mobile.dbType: ", getSelectedDBType());

			profile.setUserData("user.mobile.dbHostName", mobileHostName);
			profile.setUserData("user.mobile.dbPort", mobilePort);
			profile.setUserData("user.mobile.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.mobile.dbName", MOBILE_DB_NAME);
			profile.setUserData("user.mobile.dbUser", mobileUsername);
			profile.setUserData("user.mobile.dbUserPassword", EncryptionUtils.encrypt(Util.xor(mobilePassword)));
			profile.setUserData("user.mobile.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_PUSHNOTIFICATION)) {
			log.info("db setData : pushnotification");
			TextCustomPanelUtils.logUserData("user.pushNotification.dbHostName: ", pushnotificationHostName);
			TextCustomPanelUtils.logUserData("user.pushNotification.dbPort: ", pushnotificationPort);
			TextCustomPanelUtils.logUserData("user.pushNotification.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.pushNotification.dbName: ", PUSHNOTIFICATION_DB_NAME);
			TextCustomPanelUtils.logUserData("user.pushNotification.dbUser: ", pushnotificationUsername);
			TextCustomPanelUtils.logUserData("user.pushNotification.dbType: ", getSelectedDBType());

			profile.setUserData("user.pushNotification.dbHostName", pushnotificationHostName);
			profile.setUserData("user.pushNotification.dbPort", pushnotificationPort);
			profile.setUserData("user.pushNotification.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.pushNotification.dbName", PUSHNOTIFICATION_DB_NAME);
			profile.setUserData("user.pushNotification.dbUser", pushnotificationUsername);
			profile.setUserData("user.pushNotification.dbUserPassword", EncryptionUtils.encrypt(Util.xor(pushnotificationPassword)));
			profile.setUserData("user.pushNotification.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_CCM)) {
			log.info("db setData : ccm");
			if(profile.getUserData("user.ccm.existingDeployment").equals("false")){
				TextCustomPanelUtils.logUserData("user.ccm.dbType: ", getSelectedDBType());
				TextCustomPanelUtils.logUserData("user.ccm.jdbcLibraryPath: ", transferPath(jdbcDriver));
				TextCustomPanelUtils.logUserData("user.ccm.gcd.dbHostName: ", ccmGCDHostName);
				TextCustomPanelUtils.logUserData("user.ccm.gcd.dbPort: ", ccmGCDPort);
				TextCustomPanelUtils.logUserData("user.ccm.gcd.dbName: ", CCM_DB_NAME_GCD);
				TextCustomPanelUtils.logUserData("user.ccm.gcd.dbUser: ", ccmGCDUsername);
				TextCustomPanelUtils.logUserData("user.ccm.objstore.dbHostName: ", ccmOSHostName);
				TextCustomPanelUtils.logUserData("user.ccm.objstore.dbPort: ", ccmOSPort);
				TextCustomPanelUtils.logUserData("user.ccm.objstore.dbName: ", CCM_DB_NAME_OBJECT_STORE);
				TextCustomPanelUtils.logUserData("user.ccm.objstore.dbUser: ", ccmOSUsername);

				profile.setUserData("user.ccm.jdbcLibraryPath", transferPath(jdbcDriver));
				profile.setUserData("user.ccm.dbType", getSelectedDBType());
				profile.setUserData("user.ccm.gcd.dbHostName", ccmGCDHostName);
				profile.setUserData("user.ccm.gcd.dbPort", ccmGCDPort);
				profile.setUserData("user.ccm.gcd.dbName", CCM_DB_NAME_GCD );
				profile.setUserData("user.ccm.gcd.dbUser", ccmGCDUsername);
				profile.setUserData("user.ccm.gcd.dbUserPassword", EncryptionUtils.encrypt(Util.xor(ccmGCDPassword)));
				profile.setUserData("user.ccm.objstore.dbHostName", ccmOSHostName);
				profile.setUserData("user.ccm.objstore.dbPort", ccmOSPort);
				profile.setUserData("user.ccm.objstore.dbName", CCM_DB_NAME_OBJECT_STORE );
				profile.setUserData("user.ccm.objstore.dbUser", ccmOSUsername);
				profile.setUserData("user.ccm.objstore.dbUserPassword", EncryptionUtils.encrypt(Util.xor(ccmOSPassword)));
			} else {
				profile.setUserData("user.ccm.gcd.dbPort", "None");
				profile.setUserData("user.ccm.objstore.dbPort", "None");
			}
		}
		if (isFeatureSelected(Constants.FEATURE_ID_ICEC)) {
			log.info("db setData : icec");
			TextCustomPanelUtils.logUserData("user.icec.dbHostName: ", icecHostName);
			TextCustomPanelUtils.logUserData("user.icec.dbPort: ", icecPort);
			TextCustomPanelUtils.logUserData("user.icec.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.icec.dbName: ", ICEC_DB_NAME);
			TextCustomPanelUtils.logUserData("user.icec.dbUser: ", icecUsername);
			TextCustomPanelUtils.logUserData("user.icec.dbType: ", getSelectedDBType());

			profile.setUserData("user.icec.dbHostName", icecHostName);
			profile.setUserData("user.icec.dbPort", icecPort);
			profile.setUserData("user.icec.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.icec.dbName", ICEC_DB_NAME);
			profile.setUserData("user.icec.dbUser", icecUsername);
			profile.setUserData("user.icec.dbUserPassword", EncryptionUtils.encrypt(Util.xor(icecPassword)));
			profile.setUserData("user.icec.dbType", getSelectedDBType());
		}
		if (isFeatureSelected(Constants.FEATURE_ID_IC360)) {
			log.info("db setData : ic360");
			TextCustomPanelUtils.logUserData("user.ic360.dbHostName: ", ic360HostName);
			TextCustomPanelUtils.logUserData("user.ic360.dbPort: ", ic360Port);
			TextCustomPanelUtils.logUserData("user.ic360.jdbcLibraryPath: ", transferPath(jdbcDriver));
			TextCustomPanelUtils.logUserData("user.ic360.dbName: ", IC360_DB_NAME);
			TextCustomPanelUtils.logUserData("user.ic360.dbUser: ", ic360Username);
			TextCustomPanelUtils.logUserData("user.ic360.dbType: ", getSelectedDBType());

			profile.setUserData("user.ic360.dbHostName", ic360HostName);
			profile.setUserData("user.ic360.dbPort", ic360Port);
			profile.setUserData("user.ic360.jdbcLibraryPath", transferPath(jdbcDriver));
			profile.setUserData("user.ic360.dbName", IC360_DB_NAME);
			profile.setUserData("user.ic360.dbUser", ic360Username);
			profile.setUserData("user.ic360.dbUserPassword", EncryptionUtils.encrypt(Util.xor(ic360Password)));
			profile.setUserData("user.ic360.dbType", getSelectedDBType());
		}
	}
}
