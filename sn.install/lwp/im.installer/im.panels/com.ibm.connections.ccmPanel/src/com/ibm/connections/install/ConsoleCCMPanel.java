/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2013, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.common.core.api.utils.EncryptionUtils;
import com.ibm.connections.install.CCMPanel.WASProgressMonitor;

import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class ConsoleCCMPanel extends BaseConfigConsolePanel{
	String className = ConsoleCCMPanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());
	
	private IProfile profile = null; // profile to save data in
	
	private String ccmUserid = "";
	private String ccmPassword = null;
	private String anonymous_user = "";
	private String anonymous_pass = null;
	private String ccmURL= "";
	private String ccmHttpsURL= "";
	private String ccmPath = "";

	private boolean isRecordingMode = false;
	public static final String FS = System.getProperty("file.separator");
	
	/** 1 - Use new deployment of CCM, 2 - Use existing Filent deployment */
	private int option = 1;
	
	private Properties fnInstallers = null;
	
	public ConsoleCCMPanel() {
		super(Messages.CCM);
		fnInstallers = new Properties();
		CCMPanel.setFNInstallerProps(fnInstallers);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_CCM_PANEL;
	}

	@Override
	public void perform() {
		if (shouldSkip())
			return;
		TextCustomPanelUtils.setLogPanel(log, "CCM panel");
		log.info("CCM Panel :: Entered");

		if(profile == null) profile = getProfile();
		
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);
		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG,
				Messages.CCM);
		TextCustomPanelUtils.showNotice(Messages.CCM_DES);
				
		// OS400_Enablement,On IBM i, CCM will be integrated with the existingDeployment of FileNet on other platform.
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")){
		    option = TextCustomPanelUtils.singleSelect(Messages.CCM_OPTION_DES, new String[] {
				Messages.CCM_OPTION_EXISTING_DEPLOYMENT },1,null, null);
		    option = 2;
		}
		else {		
		    option = TextCustomPanelUtils.singleSelect(Messages.CCM_OPTION_DES, new String[] {
				Messages.CCM_OPTION_NEW_DEPLOYMENT, Messages.CCM_OPTION_EXISTING_DEPLOYMENT }, option,
				null, null);
		}

		TextCustomPanelUtils.logInput("choosing installing new CCM deployment or not", option == 1 ? "installing new CCM deployment"
				: "Using existing CCM");
		if (option == 1) {
			profile.setUserData("user.ccm.install.now", "true");
			TextCustomPanelUtils.showNotice(Messages.CCM_NEW_DEPLOYMENT_VALIDATE_DES);
			getAnonymousUser();
		} else {
			profile.setUserData("user.ccm.install.now", "false");
			TextCustomPanelUtils.showSubTitle1(Messages.CCM_EXISTING_DEPLOYMENT_Credential);
			getAdminUser();
		}
	}
	
	private String getAndCopyLCscriptPath(String fileName) {
		// application file
		String sysTempPath = System.getProperty("java.io.tmpdir");
		String appDir = sysTempPath + File.separator + LC_SCRIPT_Name;

		// set the application data for future use
		profile = this.getProfile();
		if (profile != null) {
			profile.setUserData("user.lcinstallscript.path", appDir);
		}

		File appFile = new File(appDir);
		if (!appFile.exists()) {
			appFile.mkdirs();
		}
		String toFilePath = appDir + File.separator + fileName;
		log.info(this.className +": toFilePath: " + toFilePath);

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
	
	private boolean copyFile(InputStream is, String toFile) {
		try {
			// InputStream is = new FileInputStream(fromFile);
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
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
			return false;
		}
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
	

	private static final String LC_SCRIPT_Name = "LCInstallScript";
	private static final String MISSING_SCRIPT = "Script_Missing";
	private Map nodeAgentList = new HashMap();
	private String nodeAgentsStr = "";
	
	private void verifyInstallerPath() {
		
		getNodeAgentStatus();
		
		//verify Node Agents
		String firstNodeName = profile.getUserData("user.ccm.firstNodeName");
		String secondNodeName = profile.getUserData("user.ccm.secondaryNodesNames");
		
		log.info("CCMPanel verifyNodeAgent - firstNodeName: " + firstNodeName + " secondNodeName: "+secondNodeName);
		log.info("CCMPanel verifyNodeAgent - nodeAgents : " + nodeAgentsStr);
		boolean started = isNodeAgentsStarted(nodeAgentsStr);
		log.info("CCMPanel verifyNodeAgent - started : " + started);
		
		if (!started) {
			TextCustomPanelUtils.showError(Messages.bind(Messages.WAS_NODEAGENT_UNSTARTED_ERROR, unstartedNodesAgents.toString()));
			//System.exit(0);
			getInstallPath();
			return;
		}
		
		TextCustomPanelUtils.showNotice(Messages.CCM_NEW_DEPLOYMENT_VALIDATE_DES);
				
		// Check if IATEMPDIR is set; use it if set, else use system temp folder
		String tmpdirPath = System.getenv("IATEMPDIR");
		if (tmpdirPath == null) {
			tmpdirPath = System.getProperty("java.io.tmpdir");
		}
		
		String thePath = ccmPath;
		log.info("Searching for installers in: "+ thePath);
		
		if (thePath != null && !equals("")) {
			// Check for override properties file
			File fnOverrideFile = new File(thePath, CCMConstants.FN_INSTALLERS_OVERRIDE_PROPS);
			log.info("Check for override properties file exists: "+ fnOverrideFile.exists());
			if (fnOverrideFile.exists()) {
				Properties fnOverrideProps = new Properties();
				InputStream input = null;
				try {
					input = new FileInputStream(fnOverrideFile);
					fnOverrideProps.load(input);
					fnInstallers.setProperty("ccm.ce.installer", fnOverrideProps.getProperty("ccm.ce.installer"));
					fnInstallers.setProperty("ccm.ce.fp.installer", fnOverrideProps.getProperty("ccm.ce.fp.installer"));
					fnInstallers.setProperty("ccm.fncs.installer", fnOverrideProps.getProperty("ccm.fncs.installer"));
					fnInstallers.setProperty("ccm.fncs.fp.installer", fnOverrideProps.getProperty("ccm.fncs.fp.installer"));
					fnInstallers.setProperty("ccm.ceclient.installer", fnOverrideProps.getProperty("ccm.ceclient.installer"));
				} catch (IOException ex) {
					log.info(ex);
				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							log.info(e);
						}
					}
				}
			}
		}
		
		String ceInstallerName = fnInstallers.getProperty("ccm.ce.installer");
		String ceFPInstallerName = fnInstallers.getProperty("ccm.ce.fp.installer");
		String fncsInstallerName = fnInstallers.getProperty("ccm.fncs.installer");
		String fncsPFInstallerName = fnInstallers.getProperty("ccm.fncs.fp.installer");
		String ceClientInstallerName = fnInstallers.getProperty("ccm.ceclient.installer");
		
		boolean isFailedVerification = false;
		StringBuffer errMsg = new StringBuffer();

		isFailedVerification = !isAllFNInstallersExist(thePath, fnInstallers, errMsg);

		File tmpdir = new File(tmpdirPath);
		try {
			long usableSpace = tmpdir.getUsableSpace();
			if (usableSpace < 6442450944L) { // Need 6GB temp disk space
				log.error(Messages.INSUFFICIENT_TMP_DISK_SPACE);
				errMsg.append("\n"+Messages.INSUFFICIENT_TMP_DISK_SPACE);
				isFailedVerification = true;
			}
		} catch (SecurityException e) {
			log.error(Messages.UNABLE_TO_ACCESS_TMP_DIR);
			errMsg.append("\n"+Messages.UNABLE_TO_ACCESS_TMP_DIR);
			isFailedVerification = true;
		}

		if (isFailedVerification) {
			TextCustomPanelUtils.showError(errMsg.toString());
			getInstallPath();
			return;
		}
		
		TextCustomPanelUtils.logUserData("user.ccm.installers.path", thePath.replaceAll("\\\\", "/"));
	
		profile = getProfile();
		profile.setUserData("user.ccm.installers.original.path", thePath.trim());
		profile.setUserData("user.ccm.installers.path", thePath.replaceAll("\\\\", "/"));
		
		profile.setUserData("user.ccm.ce.installer", ceInstallerName);
		profile.setUserData("user.ccm.ce.fp.installer", ceFPInstallerName);
		profile.setUserData("user.ccm.ceclient.installer", ceClientInstallerName);
		profile.setUserData("user.ccm.fncs.installer", fncsInstallerName);
		profile.setUserData("user.ccm.fncs.fp.installer", fncsPFInstallerName);
		
		TextCustomPanelUtils.showNotice(Messages.VALIDATION_SUCCESSFUL);
		
		setDataForNewCCMConfig();
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] {
						Messages.PREVIOUS_INPUT_INDEX,
						Messages.BACK_TO_TOP_INDEX, Messages.NEXT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)){
			getInstallPath();
		}else if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			perform();
		}
	}
	
	private void getNodeAgentStatus() {
		
		String params = profile.getData("ccmParams");
		String[] arrParams = params.split("\\|");
		
		log.info("arrParams size : "+ arrParams.length );
		
		//check Redhat 7 if supports 32bit app.
		Linux32AppSupportCheck check = new Linux32AppSupportCheck();
		if(!check.linux32AppSupport(getAndCopyLCscriptPath("check.linux.32"))){
			log.error(Messages.CCM_32BIT_RUNTIME_SUPPORT_FAIL);
			TextCustomPanelUtils.showError(Messages.CCM_32BIT_RUNTIME_SUPPORT_FAIL + " " + CCMPanel.TINS_LINUXSETUP_RHEL7);
			returnToTop();
			return;
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
		TextCustomPanelUtils.showProgress(Messages.WAS_DETECT_NODE_AGENTS);
		String returnCode = "1";
		try {
			returnCode = DMValidator.getDMInfo(arrParams[0], arrParams[1], arrParams[2],
					arrParams[3], arrParams[4], arrParams[5]);
			log.info("Get DM info return code : " + returnCode);
		
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

		nodeAgentList = (Map) DMValidator.detectNodeAgents();

		nodeAgentsStr = parseNodeServerMapToString(nodeAgentList);

		log.info("nodeAgentsStr : "+nodeAgentsStr);
		
	}

	/* List sub dirs of the given dir */
	private File[] listSubdirs(File dir) {
		if(dir == null || dir.exists() == false)
			return null;
		File files[] = dir.listFiles();
		ArrayList<File> list = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				list.add(files[i]);
			}
		}
		return list.toArray(new File[list.size()]);
	}

	class InstallerFilter implements FilenameFilter {
		private String installerName;
		
		public InstallerFilter(String installerName) {
			this.installerName = installerName;
		}
		public boolean accept(File dir, String name) {
			return name.equals(installerName) ? true : false;
		}
	}
	
	private boolean isAllFNInstallersExist(String pathToSearch, Properties fnInstallers, StringBuffer errMsg) {
		boolean installersExist = true;
		if (pathToSearch == null || pathToSearch.equals("")) return false;
		File dir = new File(pathToSearch.trim());
		File[] files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.ce.installer")));
		if (files == null || files.length == 0) {
			log.error(Messages.FAIL_TO_FIND_CE);
			errMsg.append(Messages.FAIL_TO_FIND_CE+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.ce.fp.installer")));
		if (files == null || files.length == 0) {
			log.error(Messages.FAIL_TO_FIND_CE_FP);
			errMsg.append(Messages.FAIL_TO_FIND_CE_FP+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.fncs.installer")));
		if (files == null || files.length == 0) {
			log.error(Messages.FAIL_TO_FIND_FNCS);
			errMsg.append("\n"+Messages.FAIL_TO_FIND_FNCS+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.fncs.fp.installer")));
		if (files == null || files.length == 0) {
			log.error(Messages.FAIL_TO_FIND_FNCS_FP);
			errMsg.append("\n"+Messages.FAIL_TO_FIND_FNCS_FP+" ");
			installersExist = false;
		}
		files = dir.listFiles(new InstallerFilter(fnInstallers.getProperty("ccm.ceclient.installer")));
		if (files == null || files.length == 0) {
			log.error(Messages.FAIL_TO_FIND_CE_CLIENT);
			errMsg.append("\n"+Messages.FAIL_TO_FIND_CE_CLIENT+" ");
			installersExist = false;
		}
		return installersExist;
	}

	private void getInstallPath() {
		TextCustomPanelUtils.showNotice(Messages.bind(Messages.CCM_NEW_DEPLOYMENT_NOTE, CCMPanel.setExamples(fnInstallers)));
		TextCustomPanelUtils.showNotice(Messages.CCM_NEW_DEPLOYMENT_POST_INSTALL_NOTE);
		String input = TextCustomPanelUtils.getInput(Messages.CCM_NEW_DEPLOYMENT_INSTALLERS_LOCATION, ccmPath);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			TextCustomPanelUtils.showError(Messages.warning_not_use_p_as_input);
			log.error("Invalid input 'P'");
			perform();
			return;
		}
		TextCustomPanelUtils.logInput("ccm installer path", input);
		ccmPath = input.trim();
		confirmTheInstallers();
	}

	private void confirmTheInstallers() {
		
		String input = TextCustomPanelUtils.getInputWithoutValidation(Messages.CCM_BASE_CPE, fnInstallers.getProperty("ccm.ce.installer"));
		TextCustomPanelUtils.logInput("ccm.ce.installer : ", input);
		if (!input.equalsIgnoreCase(fnInstallers.getProperty("ccm.ce.installer"))) {
			TextCustomPanelUtils.showError(Messages.UNABLE_TO_MODIFY_ERROR);
		}
		
		input = TextCustomPanelUtils.getInput(Messages.CCM_CPE_FIXPACK, fnInstallers.getProperty("ccm.ce.fp.installer"));
		TextCustomPanelUtils.logInput("ccm.ce.fp.installer : ", input);
		fnInstallers.setProperty("ccm.ce.fp.installer", input);
		
		input = TextCustomPanelUtils.getInput(Messages.CCM_CPE_FIXPACK_CLIENT, fnInstallers.getProperty("ccm.ceclient.installer"));
		TextCustomPanelUtils.logInput("ccm.ceclient.installer : ", input);
		fnInstallers.setProperty("ccm.ceclient.installer", input);
		
		input = TextCustomPanelUtils.getInputWithoutValidation(Messages.CCM_BASE_ICN, fnInstallers.getProperty("ccm.fncs.installer"));
		TextCustomPanelUtils.logInput("ccm.fncs.installer : ", input);
		if (!input.equalsIgnoreCase(fnInstallers.getProperty("ccm.fncs.installer"))) {
			TextCustomPanelUtils.showError(Messages.UNABLE_TO_MODIFY_ERROR);
		}
		
		input = TextCustomPanelUtils.getInput(Messages.CCM_ICN_FIXPACK, fnInstallers.getProperty("ccm.fncs.fp.installer"));
		TextCustomPanelUtils.logInput("ccm.fncs.fp.installer : ", input);
		fnInstallers.setProperty("ccm.fncs.fp.installer", input);
		
		showingTheModifiedInstallers();
		
	}

	private void showingTheModifiedInstallers() {
		
		int maxLen = TextCustomPanelUtils.getMaxLength(new String[] {fnInstallers.getProperty("ccm.ce.installer"),
				fnInstallers.getProperty("ccm.ce.fp.installer"),
				fnInstallers.getProperty("ccm.ceclient.installer"),
				fnInstallers.getProperty("ccm.fncs.installer"),
				fnInstallers.getProperty("ccm.fncs.fp.installer")}) + 4;
		
		TextCustomPanelUtils.printTitleRow(new String[]{Messages.CCM_INSTALLERS}, new int[]{maxLen});
		int i = 1;
		TextCustomPanelUtils.printSingleLineRow(i++,new String[]{fnInstallers.getProperty("ccm.ce.installer")}, new int[]{maxLen});
		TextCustomPanelUtils.printSingleLineRow(i++,new String[]{fnInstallers.getProperty("ccm.ce.fp.installer")}, new int[]{maxLen});
		TextCustomPanelUtils.printSingleLineRow(i++,new String[]{fnInstallers.getProperty("ccm.ceclient.installer")}, new int[]{maxLen});
		TextCustomPanelUtils.printSingleLineRow(i++,new String[]{fnInstallers.getProperty("ccm.fncs.installer")}, new int[]{maxLen});
		TextCustomPanelUtils.printSingleLineRow(i++,new String[]{fnInstallers.getProperty("ccm.fncs.fp.installer")}, new int[]{maxLen});
		
		TextCustomPanelUtils.getInputNull(Messages.CONFIRM_VALIDATION);
		
		verifyInstallerPath();
	}
	
	private StringBuffer unstartedNodesAgents = new StringBuffer();
	
	private boolean isNodeAgentsStarted(String nodeAgents){
		boolean started = true;
		unstartedNodesAgents = new StringBuffer();
		if(nodeAgents == null) return false;
		String[] nodeAgentsPairs = clearEmptyValues(nodeAgents.split(";"));
		for(String s: nodeAgentsPairs){
			String[] pair = s.split(":");
			if(!Boolean.valueOf(pair[1])){
				unstartedNodesAgents.append("["+pair[0]+"] ");
				started = false;
			}
		}
		return started;
	}
	
	private String[] clearEmptyValues(String[] array){
		if(array == null) return null;
		if(array.length == 0 ) return array;
		ArrayList<String> list = new ArrayList<String>();
		for(String s:array){
			if(s != null && !"".equals(s))
				list.add(s);
		}
		return list.toArray(new String[0]);
	}

	private void getAdminUser() {
		String input = TextCustomPanelUtils.getInput(
				Messages.CCM_EXISTING_DEPLOYMENT_USER_ID, ccmUserid);
		TextCustomPanelUtils.logInput("ccm user id", input);
		if (!verifyUserNameComplete(input)) {
			TextCustomPanelUtils.showError(Messages.warning_user_empty);
			log.error("User ID is required");
			getAdminUser();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			TextCustomPanelUtils.showError(Messages.warning_not_use_p_as_input);
			log.error("Invalid input 'P'");
			perform();
			return;
		}
		ccmUserid = input.trim();
		getAdminPasswd();
	}

	private void getAdminPasswd() {
		String input = TextCustomPanelUtils.getInput(
				Messages.CCM_EXISTING_DEPLOYMENT_PASSW0RD, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			getAdminUser();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.warning_password_empty);
			log.error("Password can not be empty");
			getAdminPasswd();
			return;
		}
		ccmPassword = input.trim();
		getCCMURL();
	}
	
	private void getAnonymousUser() {
		TextCustomPanelUtils.showNotice(Messages.CCM_ANONYMOUS_DES);
		TextCustomPanelUtils.showNotice(Messages.CCM_ANONYMOUS_DES_LABEL);
		String input = TextCustomPanelUtils.getInputWithoutValidation(
				Messages.CCM_ANONYMOUS_USER_LABEL, anonymous_user);
		if (input == null || input.trim().length() == 0){
		   input = "";
		}
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			perform();
			return;
		}
		TextCustomPanelUtils.logInput("ccm anonymous user id", input);
		if (input.trim().equals("")) {
			getInstallPath();
		} else {
			if (isAnonCcmAdmin(input)) {
				TextCustomPanelUtils.showError(Messages.CCM_ADMIN_CANNOT_BE_ANON_USER);
				log.error("Cannot set the administrator as the anonymous user");
				getAnonymousUser();
				return;
			}
			anonymous_user = input.trim();
			getAnonymousPasswd();
		}
	}
	
	private boolean isAnonCcmAdmin(String userid) {
		String anonUser = (userid == null ? "" : userid.trim());
		if (profile != null) {
			String ccmAdmin = profile.getUserData("user.ccm.adminuser.id");
			if (ccmAdmin == null) ccmAdmin = "";
			if (anonUser.equals(ccmAdmin)) {
				return true;
			}
		}
		return false;
	}
	
	private void getAnonymousPasswd() {
		String input = TextCustomPanelUtils.getInput(
				Messages.CCM_ANONYMOUS_PASSWORD_LABEL, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			getAnonymousUser();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.warning_password_empty);
			log.error("Password can not be empty");
			getAnonymousPasswd();
			return;
		}
		anonymous_pass = input.trim();
		getInstallPath();
	}

	private void getCCMURL() {
		String input = TextCustomPanelUtils.getInput(
				Messages.CCM_EXISTING_DEPLOYMENT_URL, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			getAdminPasswd();
			return;
		}
		
		if (input.trim() == null || !input.trim().startsWith("http")) {
			log.error("Invalid CCM url: " + input.trim());
			TextCustomPanelUtils.showError(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_CONNECTION_INVALID_HTTP_URL_MSG);
			getCCMURL();
			return;
		}
		ccmURL = input.trim();
		getCCMHttpsURL();
	}

	private void getCCMHttpsURL() {
		String input = TextCustomPanelUtils.getInput(Messages.CCM_EXISTING_DEPLOYMENT_URL_HTTPS, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			getCCMURL();
			return;
		}
		
		if (input.trim() == null || !input.trim().startsWith("https")) {
			log.error("Invalid CCM url: " + input.trim());
			TextCustomPanelUtils.showError(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_CONNECTION_INVALID_HTTPS_URL_MSG);
			getCCMHttpsURL();
			return;
		}
		ccmHttpsURL = input.trim();
		verify();
	}

	private void verify() {
		TextCustomPanelUtils.showNotice(Messages.CCM_EXISTING_DEPLOYMENT_VALIDATE_DES);
		// https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/86359
		// Allowing bypassing panel even if validation fails
		if (verifyHTTPURLComplete(ccmURL, ccmUserid, ccmPassword) && verifyHTTPSURLComplete(ccmHttpsURL, ccmUserid, ccmPassword)) {
			TextCustomPanelUtils.showText(Messages.VALIDATION_SUCCESSFUL);
		}
		setDataForExistingCCMConfig();
		goToNext();
	}

	@Override
	protected void returnToTop() {
		String input = TextCustomPanelUtils.getInput(
				Messages.BACK_TO_TOP_OR_REVALIDATE, null, new String[] {
						Messages.PREVIOUS_INPUT_INDEX,
						Messages.BACK_TO_TOP_INDEX, Messages.VALIDATE_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX))
			perform();
		else if (input.trim().equalsIgnoreCase(Messages.VALIDATE_INDEX))
			verify();
		else
			getCCMURL();
	}

	private void goToNext() {
		String input = TextCustomPanelUtils
		.getInput(Messages.CCM_NEXT_OR_REVALIDATE,
				Messages.NEXT_INDEX, new String[] {
						Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.VALIDATE_INDEX,
						Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			getCCMURL();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.VALIDATE_INDEX)) {
			verify();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			perform();
		}
	}

	private void setDataForNewCCMConfig() {
		profile = getProfile();
		StringBuffer ceInstaller =  new StringBuffer().append(ccmPath);
		StringBuffer fncsInstaller = new StringBuffer().append(ccmPath);
		StringBuffer ceClientInstaller = new StringBuffer().append(ccmPath);
		TextCustomPanelUtils.logUserData("user.ccm.existingDeployment", "false");
		TextCustomPanelUtils.logUserData("user.ccm.anonymous.user", anonymous_user);
		
		profile.setUserData("user.ccm.existingDeployment", "false");
		profile.setUserData("user.ccm.anonymous.user", anonymous_user);
		profile.setUserData("user.ccm.anonymous.password", anonymous_pass == null ? "" : EncryptionUtils.encrypt(Util.xor(anonymous_pass.trim())));
		
		String WasAdminId = profile.getUserData("user.was.adminuser.id");
		String WasAdminPW = profile.getUserData("user.was.adminuser.password");
		
		StringBuffer filenetAdmin = new StringBuffer();
		if (ccmUserid != null && ccmUserid.trim() != ""){
			filenetAdmin.append("\"filenetAdmin\": \"").append(ccmUserid);
			filenetAdmin.append("\",");			
		}
		else{
			filenetAdmin.append("\"filenetAdmin\": \"").append(WasAdminId);
			filenetAdmin.append("\",");	
		}
		
		StringBuffer filenetPwd = new StringBuffer();
		if (ccmPassword != null && ccmPassword.trim() != ""){
			filenetPwd.append("\"filenetAdminPassword\": \"").append(ccmPassword);
			filenetPwd.append("\",");	
		}
		
		profile.setUserData("user.ccm.userName", ccmUserid == "" ? "" : filenetAdmin.toString());
		profile.setUserData("user.ccm.password", ccmPassword == null ? "" : filenetPwd.toString());	
		TextCustomPanelUtils.logUserData("user.ccm.userName", ccmUserid == "" ? WasAdminId : filenetAdmin.toString());
	}
	
	private void setDataForExistingCCMConfig(){
		StringBuffer filenetAdmin = new StringBuffer();
		filenetAdmin.append("\"filenetAdmin\": \"").append(ccmUserid.trim());
		filenetAdmin.append("\",");
		
		StringBuffer filenetPwd = new StringBuffer();
		filenetPwd.append("\"filenetAdminPassword\": \"").append(ccmPassword.trim());
		filenetPwd.append("\",");
		
		profile = getProfile();
		TextCustomPanelUtils.logUserData("user.ccm.existingDeployment", "true");
		TextCustomPanelUtils.logUserData("user.ccm.userName", filenetAdmin.toString());
		TextCustomPanelUtils.logUserData("user.ccm.url", removeDmAppendix(removeLastSlash(ccmURL)));
		TextCustomPanelUtils.logUserData("user.ccm.url.https", removeDmAppendix(removeLastSlash(ccmHttpsURL)));
		profile.setUserData("user.ccm.existingDeployment", "true");
		profile.setUserData("user.ccm.userName", filenetAdmin.toString());
		profile.setUserData("user.ccm.password", filenetPwd.toString());
		profile.setUserData("user.ccm.url", removeDmAppendix(removeLastSlash(ccmURL)));
		profile.setUserData("user.ccm.url.https", removeDmAppendix(removeLastSlash(ccmHttpsURL)));
	}
	
	private String removeLastSlash(String url) {
		String realURL = null;
		if (url == null) return null;
		if (url.lastIndexOf("/") == url.length() - 1){
			realURL = url.substring(0, url.lastIndexOf("/"));
		}else{
			realURL = url;
		}
		log.info("After removed last slash the CCM URL is " + realURL);
		return realURL;
	}
	
	private String removeDmAppendix(String url) {
		String realURL = null;
		if (url == null) return null;
		if (url.lastIndexOf("/dm") == url.length() - 3){
			realURL = url.substring(0, url.lastIndexOf("/dm"));
		}else{
			realURL = url;
		}
		log.info("After removed the /dm appendix the CCM URL is " + realURL);
		return realURL;
	}

	private boolean verifyUserNameComplete(String userid) {
		if (userid == null || userid.length() == 0) {
			return false;
		}
		return true;
	}
	
	private boolean verifyPasswordComplete(String ccmpw) {
		if (ccmpw == null || ccmpw.length() == 0) {
			return false;
		}
		return true;
	}
	
	private boolean verifyHTTPSURLComplete(String url, String username, String password){
		HttpURLConnection connection = null;
		try {
			String version = null;
			username = (username == null ? "" : username.trim());
			password = (password == null ? "" : password.trim());
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {
					}
				}
			};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
				
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			connection = (HttpsURLConnection) new URL(url.trim()).openConnection();
			
			String userPass = username+":"+password;
			byte[] encodedBytes = Base64.encodeBase64(userPass.getBytes());
			connection.setRequestProperty("Authorization", "Basic "+new String(encodedBytes));
			connection.setRequestProperty("Accept-Language", "en-US,en;");
			connection.connect();
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error("Failed to connect to CCM server: " + url);
				log.error("HTTP response code: " + connection.getResponseCode());
				//log.error("Basic "+new String(encodedBytes));
				
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
					TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_UNAUTHORIZED_ERROR_MSG);
				}
				if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
					TextCustomPanelUtils.showWarning(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_FORBIDDEN_ERROR_MSG, username));
				} else {
					TextCustomPanelUtils.showWarning(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTPS_RESPONSE_ERROR_MSG, responseCode));
				}
				return false;
			} else {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.contains("Product version")) {
						version = (inputLine.replaceAll("<tr><td>Product version</td><td>v", "")).replaceAll("</tr>", "");
					}
				}
				in.close();

				if (!isVersionOk(version, "2.0.0.0")) {
					log.error("Collaboration Services version 2.0.0 required to install and use CCM.");
					TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_VERSION_ERROR_MSG);
					return false;
				}

				return true;
			}
		} catch (MalformedURLException e) {
			log.error("Failed to connect to CCM server - malformed URL: " + url);
			log.error(e.getLocalizedMessage());
			TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTPS_CONNECTION_FAIL_ERROR_MSG);
			return false;
		} catch (IOException e) {
			log.error("Failed to connect to CCM server - IO exception: " + url);
			log.error(e.getLocalizedMessage());
			TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTPS_CONNECTION_FAIL_ERROR_MSG);
			return false;
		} catch (GeneralSecurityException e) {
			log.error("Failed to connect to CCM server - General security exception: " + url);
			log.error(e.getLocalizedMessage());
			TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTPS_CONNECTION_FAIL_ERROR_MSG);
			return false;
		}catch (IllegalArgumentException e){
			log.error("Failed to connect to CCM server - Illegal argument: " + url);
			log.error(e.getLocalizedMessage());
			TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG);
			return false;
		}
	}
	
	private boolean verifyHTTPURLComplete(String url, String username, String password) {
		HttpURLConnection connection = null;
		try {
			String version = null;
			username = (username == null ? "" : username.trim());
			password = (password == null ? "" : password.trim());
			connection = (HttpURLConnection) new URL(url.trim()).openConnection();
			
			String userPass = username+":"+password;
			byte[] encodedBytes = Base64.encodeBase64(userPass.getBytes());
			connection.setRequestProperty("Authorization", "Basic "+new String(encodedBytes));
			connection.setRequestProperty("Accept-Language", "en-US,en;");
			connection.connect();
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error("Failed to connect to CCM server: " + url);
				log.error("HTTP response code: " + connection.getResponseCode());
				//log.error("Basic "+new String(encodedBytes));
				
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
					TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_UNAUTHORIZED_ERROR_MSG);
				}
				if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
					TextCustomPanelUtils.showWarning(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_FORBIDDEN_ERROR_MSG, username));
				} else {
					TextCustomPanelUtils.showWarning(Messages.bind(Messages.CCM_EXISTING_DEPLOYMENT__HTTP_RESPONSE_ERROR_MSG, responseCode));
				}
				return false;
			} else {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.contains("Product version")) {
						version = (inputLine.replaceAll("<tr><td>Product version</td><td>v", "")).replaceAll("</tr>", "");
						log.info("Product Version = " + version);
					}
				}
				in.close();

				if (!isVersionOk(version, "2.0.0.0")) {
					log.error("Collaboration Services version 2.0.0 required to install and use CCM.");
					TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_VERSION_ERROR_MSG);
					return false;
				}

				return true;
			}
		} catch (MalformedURLException e) {
			log.error("Failed to connect to CCM server - malformed URL: " + url);
			log.error(e.getLocalizedMessage());
			TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG);
			return false;
		} catch (IOException e) {
			log.error("Failed to connect to CCM server - IO exception: " + url);
			log.error(e.getLocalizedMessage());
			TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG);
			return false;
		} catch (IllegalArgumentException e){
			log.error("Failed to connect to CCM server - Illegal argument: " + url);
			log.error(e.getLocalizedMessage());
			TextCustomPanelUtils.showWarning(Messages.CCM_EXISTING_DEPLOYMENT__SERVER_HTTP_CONNECTION_FAIL_ERROR_MSG);
			return false;
		}
	}
	
	boolean isVersionOk(String version, String versionReq) {
		// versionReq needs to be in full a.b.c.d format
		if (version == null || version.equals("")){
			return false;
		}
		String[] vInfo = version.split(".");
		String[] vInfoReq = versionReq.split(".");
		boolean isOk = true;
		for (int x = 0; x < vInfo.length; x++) {
			if (Integer.parseInt(vInfo[x]) < Integer.parseInt(vInfoReq[x])) {
				log.info("vInfo[x] = " + vInfo[x]);
				isOk = false;
				break;
			}
		}
		return isOk;
	}
}
