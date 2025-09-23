/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;     //OS400_Enablement
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;	// OS400_Enablement

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.common.core.api.utils.EncryptionUtils;
import com.ibm.connections.install.BaseConfigConsolePanel;
import com.ibm.connections.install.Messages;
import com.ibm.connections.install.TextCustomPanelUtils;

//OS400 Enablement
// On OS400, need to set the umask to 000, otherwise the WAS QEJBSVR user will 
// not be authorized to write in some dynamically created DIRs.
import com.ibm.cic.common.core.api.utils.PlatformUtils;	

/**
 *
 */
public class ConsoleWasPanel extends BaseConfigConsolePanel {
	public String className = ConsoleWasPanel.class.getName();
	
	private IProfile profile = null; // profile to save data in
	private Map serverList = new HashMap();
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private boolean isNewInstall;// whether the panel deals with new install
	private String wasInstallLoc = null;
	private String wasUserDataLoc = null;// OS400_Enablement
	private List<String> dmList = null;// all available dms
	private int dmSelection = 0;// current selection of dm
	private String wasHost = null;
	private String wasUserId = "wasadmin";
	private String wasPasswd = null;
	private String wasPortNum = "";
	private String wasProfileName = null;
	private String profilePath = null;

	private ArrayList nodeList = new ArrayList();
	private Map nodeAgentList = new HashMap();
	private ArrayList clusterList = new ArrayList();
	private String clusterInfoFull = "";
	private String dmCellName = "";
	private String clustersStr = "";
	private String nodesStr = "";
	private String nodeAgentsStr = "";
	private String hostnameStr = "";
	private String servernameStr = "";
	private String checkAppFile = "wkplc_CheckAppSecurity.py";
	private String installFeFile = "lc_GetInstalledFeatures.py";

	private static final String LC_SCRIPT_Name = "LCInstallScript";
	private static final String MISSING_SCRIPT = "Script_Missing";

    /**
    *
    */
   public ConsoleWasPanel() {
       super(Messages.WasPanelName); //NON-NLS-1
   }

	@Override
	public void perform() {
		isNewInstall = !isUpdate() && !isRollback() && !isModify()
				&& !isUninstall();
		
		/**
		 * In GUI, when the installation directory is invalid, the next button 
		 * is disabled to prevent installation, but in console mode, there is no
		 * stopping for this, so add logic to handle this.
		 */
		if(profile == null) profile = getProfile();
		String installLocation = profile.getInstallLocation();
		log.info("Installation directory : " + installLocation);
		
		InstallValidator iv = new InstallValidator();
		boolean localResult = iv.validatePath(installLocation);
		log.info("localResult : " + localResult);
		
		if (!localResult) {
			TextCustomPanelUtils.showHaltMessage(iv.getMessage());
			System.exit(0);
		}
		
		/*
		 * In GUI, user cannot unselect the core features News, Search and
		 * Homepage. But in Console Mode: can unselect the core features, which
		 * may lead to installation failure and other problems Workround:
		 * ConsoleWasPanel blocks the user from proceeding to next panel if
		 * he/she does not select all the core features. A while loop is created
		 * here so that user has to enter Ctrl+C to exit IM and reselect the
		 * features to install
		 */
		TextCustomPanelUtils.setLogPanel(log, "Was panel");
		log.info("isNewInstall is " + String.valueOf(isNewInstall));
		log.info("isCoreFeatureSelected is " + String.valueOf(isCoreFeatureSelected()));
		log.info("isCoreFeatureSelectedAll is " + String.valueOf(isCoreFeatureSelectedAll()));
		if ((isNewInstall && !isCoreFeatureSelected()) || ((isModify() || isUpdate()) && !isCoreFeatureSelectedAll())) {
			TextCustomPanelUtils.showHaltMessage(Messages.ERROR_CORE_FEATURE_NOT_SELECTED);
			System.exit(0);
		}
		/*
		 * In GUI, if user selected CCM, Communities will be selected automatically.
		 * But console mode can not has the same functionality. As the work around if we 
		 * found customer selected CCM but didn't select Communities, we will block 
		 * them from continue.
		 */
		log.info("CCM Added = " + isFeatureNewAdded("ccm"));
		log.info("Communities installed = " + isFeatureInstalled("communities"));
		log.info("Communities selected = " + isFeatureSelected("communities"));
		log.info("Communities isFeatureNewAdded = " + isFeatureNewAdded("communities"));
		log.info("Communities isFeatureSelectedAll = " + isFeatureSelectedAll("communities"));
		log.info("Communities getOffering = " + getOffering());
		if (isFeatureNewAdded("ccm") && !isFeatureSelectedAll("communities")){
			TextCustomPanelUtils.showHaltMessage(Messages.ERROR_COMMUNITIES_NOT_SELECTED);
			System.exit(0);
		}
		
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);

		log.info("In...waspanel..createControl");
		log.info("isFixpackInstall = " + isUpdate());
		log.info("isFixpackUninstall = " + isRollback());
		log.info("isModifyInstall = " + isModify());
		log.info("isUninstall = " + isUninstall());

		// OS400_Enablement 
		// OS400 support, to set the umask to 000, otherwise the WAS QEJBSVR user will
		// not be able to write in some dynamically created Dirs.
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			PlatformUtils.setUmask(0);
		}

		// load default values for uninstall/modify/update/rollback
		if (!isNewInstall)
			loadDefaults();
		// input was location
		startConfiguration();
	}

	private void loadDefaults() {
		dmSelection = 1;
		if (profile != null) {
			wasInstallLoc = profile.getUserData("user.was.install.location");
			dmList = new ArrayList<String>();
			dmList.add(profile.getUserData("user.was.profileName"));
			wasHost = profile.getUserData("user.was.dmHostname");
			wasUserId = profile.getUserData("user.was.adminuser.id");
			if (wasUserId == null)
				wasUserId = profile.getUserData("user.news.adminuser.id");
			wasPortNum = profile.getUserData("user.was.wasSoapPort");
			profilePath = profile.getUserData("user.was.userhome");
		}
	}

	/**
	 * get and verify was install location. If not in the install mode, just
	 * display was info
	 */
	private void startConfiguration() {
		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG,
				Messages.WAS_INSTALL_PANEL1);
		TextCustomPanelUtils.showSubTitle1(Messages.WAS_SELECTION);
		if (isNewInstall) {
			String input = TextCustomPanelUtils.getInput(
					Messages.WAS_LOCATION_BROWSER_MSG, wasInstallLoc);
			TextCustomPanelUtils.logInput("was location", input);
			// check if the path specified exists
			if (!pathExist(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.DETECT_WAS_VERSION_ERROR);
				log.error("CLFRP0015E: The WebSphere Application Server location is not correct.");
				startConfiguration();
				return;
			}
			// check was version
			String wasVersionPath = getAndCopyLCscriptPath("wasVersion.txt");
			log.info("Was version Path : " + wasVersionPath);
			if (wasVersionPath.equals(MISSING_SCRIPT)) {
				TextCustomPanelUtils
						.showError(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
				log.error("CLFRP0014E: HCL Connections script is missing, please check your installation image! ");
				startConfiguration();
				return;
			}
			if (!DetectiveProfileAction.isWasLocValid(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.DETECT_WAS_VERSION_ERROR);
				log.error("CLFRP0015E: The WebSphere Application Server location is not correct.");
				startConfiguration();
				return;
			} else {
				try {

					boolean isValidWasVersion = DetectiveProfileAction
							.isWasVersionValid(input.trim(), wasVersionPath);
					log.info("is was version valid : " + isValidWasVersion);
					if (!isValidWasVersion) {
						TextCustomPanelUtils
								.showError(Messages.WAS_VERSION_NOT_SUPPORT);
						log.error("CLFRP0016E: The WebSphere Application Server version is not supported. ");
						compareVersion(input, wasVersionPath);
						startConfiguration();
						return;
					}

				} catch (Exception e) {
					TextCustomPanelUtils
							.showError(Messages.DETECT_WAS_VERSION_ERROR);
					log.error("CLFRP0015E: The WebSphere Application Server location is not correct.");
					startConfiguration();
					return;
				}
			}
			wasInstallLoc = input.trim();
		}
		// display the information
		else {
			TextCustomPanelUtils.showText(Messages.WAS_LOCATION_BROWSER_MSG);
			TextCustomPanelUtils.showText(wasInstallLoc + "\n");
		}

		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			// OS400_Enablement
			//  OS400 need to get the WAS UserData Dir, since by default it is not installed under the 
			//  Product install Dir. Here is an example.
			//		WAS Install Location: /QIBM/Proddata/Websphere/AppServer/V8/ND
			//		WAS UserData Location: /QIBM/Userdata/Websphere/AppServer/V8/ND
			//  For WAS 8, this information can be retrieved from below property.
			//		/QIBM/Proddata/Websphere/AppServer/V8/ND/properties/os400.properties
			Properties prop = new Properties();
			try {
				File conf = new File(wasInstallLoc+"/properties/os400.properties");
				prop.load(new FileInputStream(conf));
				wasUserDataLoc = prop.getProperty("WAS_USERDATA").trim();
			} catch (Exception e) {
				e.printStackTrace();
				// By default the WAS UserData Dir on OS400 is the WAS install location with the "ProdData" 
				// replaced by "Userdata".  So set it this way, to avoid the install failure.
				wasUserDataLoc = wasInstallLoc.replaceAll("[pP][rR][oO][dD][dD][aA][tT][aA]","UserData");
			}
			// Validate WAS UserData Dir
			if (!DetectiveProfileAction.isWasUserDataLocValid(wasUserDataLoc)) {
				TextCustomPanelUtils
				.showError(Messages.DETECT_WAS_VERSION_ERROR);
			log.error("CLFRP0015E: The WebSphere Application Server User Data location is not correct.");
			startConfiguration();
			return;
			}
		}

		log.info("Select was location : " + wasInstallLoc);
		// OS400_Enablement
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			log.info("WAS UserData location : " + wasUserDataLoc);
		}

		selectDM();
	}

	/** compare and display the versions */
	private void compareVersion(String input, String wasVersionPath)
			throws Exception {
		List<String> versionList = DetectiveProfileAction.getWasVersion(
				input.trim(), wasVersionPath);
		String curVersions = "";
		if (versionList != null && versionList.size() != 0)
			for (String version : versionList)
				curVersions += version;
		@SuppressWarnings("unchecked")
		List<String> supportVersions = DetectiveProfileAction
				.getSupportVersion(wasVersionPath);
		String supportVer = "";
		if (supportVersions != null && supportVersions.size() != 0)
			for (String cur : supportVersions)
				supportVer += cur;
		TextCustomPanelUtils.showError("Support versions:" + supportVer
				+ " Current versions:" + curVersions);
		log.error("Support versions:" + supportVer + " Current versions:"
				+ curVersions);
	}

	@SuppressWarnings("unchecked")
	private void selectDM() {
		int input = 0;
		String path = null;
		int defaultSelection = dmSelection;
		if (isNewInstall) {
			// OS400_Enablement
			if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
				dmList = (List<String>) DetectiveProfileAction
					.getDMProfileNew(wasUserDataLoc);
			} else {
				dmList = (List<String>) DetectiveProfileAction
					.getDMProfileNew(wasInstallLoc);
			}
		}
		log.info("profile list : " + dmList);
		if (dmList != null && isNewInstall) {
			String[] tmpList = new String[dmList.size()];
			for (int i = 0; i < dmList.size(); i++)
				tmpList[i] = dmList.get(i).toString();
			input = TextCustomPanelUtils.singleSelect(
					Messages.WAS_DEPLOY_MANAGER, tmpList, dmSelection,
					new String[] { Messages.PREVIOUS_INPUT_INDEX },
					Messages.WAS_DEPLOY_MANAGER_DESC);
			if (input < 0) {// previous input
				startConfiguration();
				return;
			} else
				dmSelection = input;
			TextCustomPanelUtils.logInput("select DM",
					dmList.get(dmSelection - 1));
		}
		if (dmList != null) {
			TextCustomPanelUtils.showText(Messages.WAS_DEPLOY_MANAGER);
			TextCustomPanelUtils.showText(dmList.get(dmSelection - 1) + "\n");
			// OS400_Enablement
			if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
				path = DetectiveProfileAction.getProfilePath(
						dmList.get(dmSelection - 1), wasUserDataLoc);
			} else { 
				path = DetectiveProfileAction.getProfilePath(
						dmList.get(dmSelection - 1), wasInstallLoc);
			}
		}
		if (path == null)
			path = profilePath;
		String tmpPort = DetectiveProfileAction.getProfilePort(path);
		if (tmpPort == null || tmpPort.length() == 0) {
			TextCustomPanelUtils.showError(Messages.WAS_DETECT_PORT_ERROR);
			log.error("Was cannot detect the port number of the specified deployment manager.");
			dmSelection = defaultSelection;
			selectDM();
			return;
		}
		wasPortNum = tmpPort;
		getWasHost();
	}

	private void getWasHost() {
		if (!isNewInstall) {
			TextCustomPanelUtils.showText(Messages.WAS_HOST);
			TextCustomPanelUtils.showText(wasHost + "\n");
			TextCustomPanelUtils
					.showSubTitle1(Messages.WAS_DEPLOY_MANAGER_INFO);
			TextCustomPanelUtils.showText(Messages.dmSecurity_info);
			getWasUserId();
			return;
		}
		String input = TextCustomPanelUtils
				.getInput(Messages.WAS_HOST, wasHost);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			selectDM();
			return;
		}
		TextCustomPanelUtils.logInput("was host", input);
		if (!verifyHostNameComplete(input.trim())) {
			getWasHost();
			return;
		}
		wasHost = input.trim();
		TextCustomPanelUtils.showSubTitle1(Messages.WAS_DEPLOY_MANAGER_INFO);
		TextCustomPanelUtils.showText(Messages.dmSecurity_info);
		getWasUserId();

	}

	private void getWasUserId() {
		String input = TextCustomPanelUtils.getInput(
				Messages.dmSecurity_username, wasUserId);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			getWasHost();
			return;
		}
		TextCustomPanelUtils.logInput("was user id", input);
		if (!verifyUserNameComplete(input)) {
			getWasUserId();
			return;
		}
		wasUserId = input.trim();
		getWasPassword();
	}

	private void getWasPassword() {
		String input = TextCustomPanelUtils.getInput(
				Messages.dmSecuirty_passwd, null);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			getWasUserId();
			return;
		}
		if (!verifyPasswordComplete(input)) {
			getWasPassword();
			return;
		}
		wasPasswd = input.trim();
		//TextCustomPanelUtils.showText(Messages.dmSecuirty_port);
		//TextCustomPanelUtils.showText(wasPortNum + "\n");
		TextCustomPanelUtils.getInputNull(Messages.CONFIRM_VALIDATION);
		validateWas();
	}

	private void validateWas() {
		TextCustomPanelUtils.showProgress(Messages.VALIDATE);
		String dmpwd = this.wasPasswd == null ? "" : this.wasPasswd.trim();
		String dmuserId = this.wasUserId == null ? "" : this.wasUserId.trim();
		String dmport = this.wasPortNum == null ? "" : this.wasPortNum.trim();
		String hn = this.wasHost == null ? "" : this.wasHost.trim();
		if (dmList != null)
			wasProfileName = dmList.get(dmSelection - 1);
		if (isNewInstall) {
			try {
				// OS400_Enablement
				if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
					profilePath = DetectiveProfileAction.getProfilePath(
						wasProfileName, this.wasUserDataLoc);
				} else {
					profilePath = DetectiveProfileAction.getProfilePath(
						wasProfileName, this.wasInstallLoc);
				}
				if (profilePath == null) {
					TextCustomPanelUtils
							.showError(Messages.WAS_GET_PROFILE_PATH_WARNING);
					log.error("CLFRP0004E: Cannot find Deployment Manager profile.");
					returnToTop();
					return;
				}
			} catch (Exception e) {
				TextCustomPanelUtils
						.showError(Messages.WAS_GET_PROFILE_PATH_WARNING);
				log.error("CLFRP0004E: Cannot find Deployment Manager profile.");
				returnToTop();
				return;
			}
		} else {
			profilePath = profile.getUserData("user.was.userhome");
		}

		List<String> selectFeatures = this.getOffering();
		StringBuffer features = new StringBuffer();
		if (selectFeatures != null) {
			for (int i = 0; i < selectFeatures.size(); i++) {
				features.append(selectFeatures.get(i));
				features.append(",");
			}
		}
		TextCustomPanelUtils.showProgress(Messages.WAS_DETECT_START);

		TextCustomPanelUtils.showProgress(Messages.WAS_DETECT_SYSTEM_APP);

		try {
			boolean hasSystemApps = DetectiveProfileAction
					.getSystemAppsCheckResult(wasInstallLoc);
			log.info("hasSystemApps = " + hasSystemApps);
			if (!hasSystemApps) {
				TextCustomPanelUtils.showError(Messages.WAS_NO_SYSTEM_APP);
				log.error("CLFRP0044E: The system application SchedulerCalendars.ear cannot be found on WebSphere Application Server. Please refer to the WebSphere information center to deploy the application before continuing the installation.");
				return;
			}
		} catch (Exception e) {
			log.error(e);
			TextCustomPanelUtils.showError(Messages.WAS_NO_SYSTEM_APP);
			log.error("CLFRP0044E: The system application SchedulerCalendars.ear cannot be found on WebSphere Application Server. Please refer to the WebSphere information center to deploy the application before continuing the installation.   ");
			returnToTop();
			return;
		}

		// Check OAuth provider app availability
		TextCustomPanelUtils.showProgress(Messages.WAS_DETECT_OAUTH_PROVIDER_EAR);

		try {
			boolean hasOauthProviderEar = DetectiveProfileAction.getOauthProviderEarCheckResult(wasInstallLoc);
			log.info("hasOauthProviderEar = " + hasOauthProviderEar);
			if (!hasOauthProviderEar) {
				TextCustomPanelUtils.showError(Messages.WAS_NO_OAUTH_PROVIDER_EAR);
				log.error("CLFRP0045E: The WebSphereOauth20SP.ear application cannot be found on WebSphere Application Server. See the HCL Connections System Requirements topic in the HCL Connections Information Center for further details.");
				return;
			}
		} catch (Exception e) {
			log.error(e);
			TextCustomPanelUtils.showError(Messages.WAS_NO_OAUTH_PROVIDER_EAR);
			log.error("CLFRP0045E: The WebSphereOauth20SP.ear application cannot be found on WebSphere Application Server. See the HCL Connections System Requirements topic in the HCL Connections Information Center for further details.");
			returnToTop();
			return;
		}

		TextCustomPanelUtils.showProgress(Messages.WAS_GET_CERT);

		String sslReturnCode = SSLCertificateGetter.getSSLCertificate(
				profilePath, wasUserId, wasPasswd);
		log.info("Get ssl certificate return code : " + sslReturnCode);
		if (!sslReturnCode.equals("0")) {
			TextCustomPanelUtils
					.showError(Messages.WAS_GET_SSL_CERTIFICATE_ERROR);
			log.error("CLFRP0005E: Cannot get SSL certificate from the Deployment Manager.");
			returnToTop();
			return;
		}
		// check app security
		if (isNewInstall) {
			String checkApppyPath = "";
			try {
				checkApppyPath = getAndCopyLCscriptPath(checkAppFile);
				if (checkApppyPath.equals(MISSING_SCRIPT)) {
					TextCustomPanelUtils
							.showError(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
					log.error("CLFRP0014E: HCL Connections script is missing, please check your installation image! ");
					returnToTop();
					return;
				}
			} catch (Exception e1) {
				TextCustomPanelUtils.showError(Messages.WAS_GET_PY_PATH_ERROR);
				log.error("CLFRP0008E: Security checking script is missing, please check your installation image.");
				returnToTop();
				return;
			}
			TextCustomPanelUtils.showProgress(Messages.WAS_CHECK_APP_SECURITY);
			boolean isAppSecurity = WasSecurityValidator.validateAppSecurity(
					profilePath, checkApppyPath);
			log.info("is application security enabled : " + isAppSecurity);
			if (!isAppSecurity) {
				TextCustomPanelUtils
						.showError(Messages.WAS_NO_APP_SECURITY_ERROR);
				log.error("CLFRP0007E: Application security is not enabled on the Deployment Manager profile.");
				returnToTop();
				return;
			}
			// Java2 Security check
			TextCustomPanelUtils.showProgress(Messages.WAS_CHECK_JAVA2_SECURITY);
			boolean isJava2Sec = WasSecurityValidator.getJava2Security();
			log.info("is java2 security enabled : " + isJava2Sec);
			if (isJava2Sec) {
				TextCustomPanelUtils.showError(Messages.WAS_JAVA2_SECURITY_ERROR);
				log.error("CLFRP0046E: Java 2 security is enabled on the Deployment Manager profile.  It must be disabled.");
				returnToTop();
				return;
			}
			TextCustomPanelUtils
					.showProgress(Messages.WAS_CHECK_ADMIN_SECURITY);
			// Admin Security check
			boolean isAdminSec = WasSecurityValidator.getAdminSecurity();
			log.info("is administator security enabled : " + isAdminSec);
			if (!isAdminSec) {
				TextCustomPanelUtils
						.showError(Messages.WAS_NO_ADMIN_SECURITY_ERROR);
				log.error("CLFRP0006E: Administrative security is not enabled on the Deployment Manager profile.");
				returnToTop();
				return;
			}
		}
		// check DM info for heap size
		String pyPath = "";
		try {
			pyPath = getAndCopyLCscriptPath("wkplc_GetDMInfo.py");
			if (pyPath.equals(MISSING_SCRIPT)) {
				TextCustomPanelUtils
						.showError(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
				log.error("CLFRP0014E: HCL Connections script is missing, please check your installation image! ");
				returnToTop();
				return;
			}
		} catch (Exception e1) {
			TextCustomPanelUtils.showError(Messages.WAS_GET_PY_PATH_ERROR);
			log.error("CLFRP0008E: Security checking script is missing, please check your installation image. ");
			returnToTop();
			return;
		}
		TextCustomPanelUtils.showProgress(Messages.WAS_GET_DM_INFO);
		String returnCode = "1";
		try {
			
			returnCode = DMValidator.getDMInfo(profilePath, pyPath, wasUserId,
					wasPasswd, wasHost, wasPortNum);
			
			StringBuffer params = new StringBuffer();
			params.append(profilePath).append("|")
				.append(pyPath).append("|")
				.append(wasUserId).append("|")
				.append(wasPasswd).append("|")
				.append(wasHost).append("|")
				.append(wasPortNum);
			profile.setData("ccmParams", params.toString());
			
			log.info("Get DM info return code : " + returnCode);
			
			//get web server info and put them into tmp dir
			WebServerHelper webServerHelper = new WebServerHelper();
			String result = webServerHelper.getWebServerInfo(profilePath, wasUserId, wasPasswd);
			log.info("GetWebServerInfo return : " + result);
			
			if("success".equals(result)){
				List<WebServer> webServers = webServerHelper.parseIhsTxt();
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<webServers.size();i++){
					WebServer webServer = webServers.get(i);
					sb.append(webServer.toString());
					if(i != webServers.size() - 1) sb.append(";");
				}
				profile.setData("webServers", sb.toString());
				log.info("webServers: "+sb.toString());
			}
		
		} catch (Exception e) {
			log.error(e);
			returnCode = "1";
		}
		if (!returnCode.equals("0")) {
			TextCustomPanelUtils.showError(Messages.WAS_CONNECT_DM_ERROR);
			log.error("CLFRP0009E: Cannot connect to the Deployment Manager.");
			returnToTop();
			return;
		}

		nodeList = (ArrayList) DMValidator.detectNodes();
		nodeAgentList = (Map) DMValidator.detectNodeAgents();
		clusterList = (ArrayList) DMValidator.detectClusters();

		try {
			serverList = (Map) DMValidator.detectServers();
		} catch (Exception e) {
			log.error(e);
		}

		clusterInfoFull = DMValidator.getClusterFullInfo();
		dmCellName = DMValidator.detectDMCellName();
		clustersStr = parseToString(clusterList);
		nodesStr = parseToString(nodeList);
		nodeAgentsStr = parseNodeServerMapToString(nodeAgentList);
		hostnameStr = parseNodeHostNameMapToString(nodeList);
		servernameStr = parseNodeServerMapToString(serverList);

		log.info("nodeAgentsStr : "+nodeAgentsStr);
		
		// check nodes num
		boolean valideNodesNumber = DMValidator.validteNodesNumber();
		if (!valideNodesNumber) {
			TextCustomPanelUtils.showError(Messages.WAS_NODE_NUM_ERROR);
			log.error("CLFRP0010E: No application server node detected under the Deployment Manager. HCL Connections requires at least 1 node to proceed.");
			returnToTop();
			return;
		}
		if (isNewInstall) {
			// check install feature
			String checkInstallFePath = "";
			try {
				checkInstallFePath = getAndCopyLCscriptPath(installFeFile);
				if (checkInstallFePath.equals(MISSING_SCRIPT)) {
					TextCustomPanelUtils
							.showError(Messages.WAS_PY_SCRIPT_MISSING_ERROR);
					log.error("CLFRP0014E: HCL Connections script is missing, please check your installation image! ");
					returnToTop();
					return;
				}
			} catch (Exception e1) {
				TextCustomPanelUtils.showError(Messages.WAS_GET_PY_PATH_ERROR);
				log.error("CLFRP0008E: Security checking script is missing, please check your installation image.");
			}
			TextCustomPanelUtils.showProgress(Messages.WAS_CHECK_INSTALL_FE);
			boolean isvalidFeature = WasInstalledFeaturesValidator.validate(
					profilePath, features.toString(), checkInstallFePath,
					wasUserId, wasPasswd);
			log.info("is valid feature : " + isvalidFeature);
			if (!isvalidFeature) {
				TextCustomPanelUtils
						.showError(Messages.Existing_Feature_Selected_Error);
				log.error("CLFRP0011E: One or more applications you selected already exist in the WebSphere Application Server. Please go back and reselect applications.");
				returnToTop();
				return;
			}
		}
		// check open file number
		OpenFileNumberCheck openFileNumberCheck = new OpenFileNumberCheck();
		int code = openFileNumberCheck.getOpenFileNumberCheck();
		log.info("OpenFileNumberCheck return code = " + code);
		if (code == OpenFileNumberCheck.LESS_THAN_8192) {
		    if (System.getProperty("os.name").toLowerCase().startsWith("os/400"))
			    log.info("OpenFileNumberCheck return LESS_THAN_8192 ");
            else
			{
			    log.info("OpenFileNumberCheck return LESS_THAN_8192 ");
			    TextCustomPanelUtils.showWarning(Messages.WAS_OPEN_FILE_WARNING1);
			}
		} else if (code == OpenFileNumberCheck.UNKNOWN_ERROR) {
			log.info("OpenFileNumberCheck return UNKNOWN_ERROR ");
			TextCustomPanelUtils.showWarning(Messages.WAS_OPEN_FILE_WARNING2);
		}
		TextCustomPanelUtils.showText(Messages.VALIDATION_SUCCESSFUL);

		TextCustomPanelUtils.logUserData("user.clusterlist", clustersStr);
		TextCustomPanelUtils.logUserData("user.nodeslist", nodesStr);
		TextCustomPanelUtils.logUserData("user.clusterfullinfo",
				clusterInfoFull);
		TextCustomPanelUtils.logUserData("user.nodesServerlist", servernameStr);
		TextCustomPanelUtils.logUserData("user.nodesHostnamelist", hostnameStr);

		profile.setUserData("user.clusterlist", clustersStr);
		profile.setUserData("user.nodeslist", nodesStr);
		profile.setUserData("user.nodeAgentList", nodeAgentsStr);
		profile.setUserData("user.clusterfullinfo", clusterInfoFull);
		profile.setUserData("user.nodesServerlist", servernameStr);
		profile.setUserData("user.nodesHostnamelist", hostnameStr);
		this.setDMUserData(wasInstallLoc, profilePath, dmport, wasProfileName,
				dmCellName, hn, dmuserId, dmpwd);

		goToNext();
	}

	private void goToNext() {
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] { Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			getWasPassword();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			startConfiguration();
		}
	}

	/**
	 * Enter 'b' for back to top, or enter 'v' to validate again. Enter 'p' to
	 * go back to previous input.
	 */
	@Override
	public void returnToTop() {
		String input = TextCustomPanelUtils.getInput(
				Messages.BACK_TO_TOP_OR_REVALIDATE, null, new String[] {
						Messages.PREVIOUS_INPUT_INDEX,
						Messages.BACK_TO_TOP_INDEX, Messages.VALIDATE_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX))
			startConfiguration();
		else if (input.trim().equalsIgnoreCase(Messages.VALIDATE_INDEX)) {
			validateWas();
		} else
			getWasPassword();
	}

	private boolean verifyHostNameComplete(String washost) {
		InstallValidator installvalidator = new InstallValidator();
		String host = washost == null ? "" : washost.trim();
		try {
			if (!installvalidator.hostNameValidateForWasPanel(host)) {
				TextCustomPanelUtils.showError(installvalidator.getMessage());
				log.error("Host name is invalid");
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean verifyUserNameComplete(String input) {
		String dmuserId = input == null ? "" : input.trim();
		if (dmuserId == null || dmuserId.length() == 0) {
			TextCustomPanelUtils.showError(Messages.warning_user_empty);
			log.error("User ID is required");
			return false;
		}
		return true;
	}

	private boolean verifyPasswordComplete(String pswd) {
		InstallValidator validator = new InstallValidator();
		String dmuserpw = pswd == null ? "" : pswd.trim();
		if (dmuserpw == null || dmuserpw.length() == 0) {
			TextCustomPanelUtils.showError(Messages.warning_password_empty);
			log.error("Password is required");
			return false;
		}
		if (validator.containsSpace(dmuserpw)) {
			TextCustomPanelUtils.showError(Messages.warning_password_invalid);
			log.error("Username and password contains invalid characters");
			return false;
		}
		return true;
	}

	private String getAndCopyLCscriptPath(String fileName) {
		// application file
		String sysTempPath = System.getProperty("java.io.tmpdir");
		String appDir = sysTempPath + File.separator + LC_SCRIPT_Name;
		// set the application data for future use
		profile = this.getProfile();
		if (profile != null) {
			TextCustomPanelUtils.logUserData("user.lcinstallscript.path", appDir);
			profile.setUserData("user.lcinstallscript.path", appDir);
		}

		File appFile = new File(appDir);
		if (!appFile.exists()) {
			appFile.mkdirs();
		}
		String toFilePath = appDir + File.separator + fileName;
		log.info(this.className +": toFilePath: "+ toFilePath);
		File toFile = new File(toFilePath);
		if (toFile.exists()) {
			toFile.delete();
		}

		// copy script to system temp folder
		InputStream fileInput = DetectiveProfileAction.class.getClassLoader().getResourceAsStream(fileName);
		copyFile(fileInput, toFilePath);
		log.info(this.className +": script file path: " + toFile.getAbsolutePath());
		return toFile.getAbsolutePath();
	}

	private String parseNodeHostNameMapToString(ArrayList target) {
		if (target != null) {
			Iterator it = target.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String content = (String) it.next();
				try {
					result.append(DMValidator.detectNodeHostName(content) + ",");
				} catch (Exception e) {
					log.error(e);
				}
			}
			return result.toString();
		}
		return null;
	}

	private String parseToString(ArrayList target) {

		if (target != null) {
			Iterator it = target.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String content = (String) it.next();
				result.append(content + ",");
			}
			return result.toString();
		}
		return null;
	}

	private String parseNodeServerMapToString(Map target) {
		if (target != null) {
			Set nodeSet = target.keySet();
			Iterator it = nodeSet.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String node = (String) it.next();
				String serverStr = (String) target.get(node);
				result.append(node + ":" + serverStr + ";");
			}
			return result.toString();
		}
		return null;
	}

	private boolean copyFile(InputStream is, String toFile) {
		try {
			FileOutputStream fos = new FileOutputStream(toFile);
			log.info("Was panel : is == null : " + is == null);
			for (int b = is.read(); b != -1; b = is.read()) {
				fos.write(b);
			}
			is.close();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			log.error(e);
			return false;
		} catch (IOException e) {
			log.error(e);
			return false;
		}
	}

	private void setDMUserData(String wasLocation, String wasProfilePath,
			String port, String dmProfileName, String dmCellName,
			String hostname, String adminUser, String password) {
		log.info("Set user data");
		log.info("was location : " + wasLocation);
		log.info("was profile bin path : " + wasProfilePath);
		log.info("DM host : " + hostname);
		log.info("DM soap port : " + port);
		log.info("DM profile name : " + dmProfileName);
		log.info("DM Cell name : " + dmCellName);
		log.info("Admin user name : " + adminUser);
		log.info("Admin password : PASSWORD_REMOVED");

		TextCustomPanelUtils.logUserData("user.isGUIMode", "F");
		profile.setUserData("user.isGUIMode", "F");
		if (isNewInstall) {
			TextCustomPanelUtils.logUserData("user.was.install.location",
					transferPath(wasLocation));
			TextCustomPanelUtils.logUserData("user.was.userhome",
					transferPath(wasProfilePath));
			TextCustomPanelUtils.logUserData("user.was.wasSoapPort", port);
			TextCustomPanelUtils.logUserData("user.was.profileName",
					dmProfileName);
			TextCustomPanelUtils.logUserData("user.was.dmCellName", dmCellName);
			TextCustomPanelUtils.logUserData("user.was.dmHostname", hostname);
			
			String transferWASPath = transferPath(wasLocation);
			String wasHomeLocation = null;
			if (transferWASPath.contains(":")){
	    		int index = transferWASPath.indexOf(":");
	    		wasHomeLocation = transferWASPath.substring(0,index) + "\\" + transferWASPath.substring(index);
	    		log.info("was location: " + wasHomeLocation);
	    		//showValidationSuccessMessageDialog(wasHomeLocation);
	    	}
			else{
				wasHomeLocation = transferPath(wasLocation);
			}

			profile.setUserData("user.was.install.location", transferPath(wasLocation));
			profile.setUserData("user.was.install.location.configproperties", wasHomeLocation);
			profile.setUserData("user.was.install.location.win32format", wasLocation);
			
			String transferProfilePath = transferPath(wasProfilePath);
			String wasUserHome = null;
			if (transferProfilePath.contains(":")){
	    		int index = transferProfilePath.indexOf(":");
	    		wasUserHome = transferProfilePath.substring(0,index) + "\\" + transferProfilePath.substring(index);
	    		log.info("was profile home: " + wasUserHome);
	    		//showValidationSuccessMessageDialog(wasUserHome);
	    	}
			else{
				wasUserHome = transferPath(wasProfilePath);
			}
			
			profile.setUserData("user.was.userhome.original", wasProfilePath);
			profile.setUserData("user.was.userhome", transferPath(wasProfilePath));
			profile.setUserData("user.was.userhome.configproperties", wasUserHome);
			profile.setUserData("user.was.wasSoapPort", port);
			profile.setUserData("user.was.profileName", dmProfileName);
			profile.setUserData("user.was.dmCellName", dmCellName);
			profile.setUserData("user.was.dmHostname", hostname);
			String domainname = hostname.substring(hostname.indexOf("."));
			profile.setUserData("user.was.domainName", domainname);
		}
		TextCustomPanelUtils.logUserData("user.was.adminuser.id", adminUser);

		profile.setUserData("user.was.adminuser.id", adminUser);

		profile.setUserData("user.was.adminuser.password",
				EncryptionUtils.encrypt(password));
		
		profile.setUserData("user.was.adminuser.xor.encrypted.password",
				Util.xor(password));
		
		log.info("user.was.adminuser.xor.encrypted.password : " + Util.xor(password));
		
		for (int featureID = ACTIVITIES; featureID <= NEWS; featureID++) {
			TextCustomPanelUtils.logUserData("user."
					+ getFeatureName(featureID) + ".adminuser.id", adminUser);

			profile.setUserData("user." + getFeatureName(featureID)
					+ ".adminuser.id", adminUser);
			profile.setUserData("user." + getFeatureName(featureID)
					+ ".adminuser.password", EncryptionUtils.encrypt(password));
		}
	}

	private boolean pathExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_WAS_PANEL;
	}
}
