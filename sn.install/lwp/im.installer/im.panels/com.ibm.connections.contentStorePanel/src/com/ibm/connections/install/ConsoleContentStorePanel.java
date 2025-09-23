/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;

public class ConsoleContentStorePanel extends BaseConfigConsolePanel {
	String className = ConsoleContentStorePanel.class.getName();

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in

	private String sharedContentStorePath;
	private String localContentStorePath;

	public ConsoleContentStorePanel() {
		super(Messages.CONTENT_STORE);
	}

	@Override
	public void perform() {
		if (shouldSkip())
			return;
		log.info("Content Store Panel :: Entered");
		
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);
		profile = getProfile();
		TextCustomPanelUtils.setLogPanel(log, "Content store panel");
		loadDefaults();
		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG,
				Messages.CONTENT_STORE);
		getSharedContentStoreLocation();

	}

	private void loadDefaults() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			//sharedContentStorePath = profile.getInstallLocation()+"\\data\\shared";
			//localContentStorePath = profile.getInstallLocation()+"\\data\\local";
		} else if (osName.equals("Linux")) {
			//sharedContentStorePath = profile.getInstallLocation()+"/data/shared";
			//localContentStorePath = profile.getInstallLocation()+"/data/local";
		} else if (osName.equals("AIX")) {
			//sharedContentStorePath = profile.getInstallLocation()+"/data/shared";
			//localContentStorePath = profile.getInstallLocation()+"/data/local";
		} else if (osName.toLowerCase().startsWith("os/400")) {  
			// OS400_Enablement
			//sharedContentStorePath = profile.getInstallLocation()+"/data/shared";
			//localContentStorePath = profile.getInstallLocation()+"/data/local";
		}
	}

	private void getSharedContentStoreLocation() {
		TextCustomPanelUtils.showSubTitle1(Messages.SHARED_CONTENT_STORE);
		String input = TextCustomPanelUtils.getInput(
				Messages.SHARED_CONTENT_STORE_LOC, sharedContentStorePath);
		TextCustomPanelUtils.logInput("shared content store location: ", input);
		sharedContentStorePath = input.trim();
		getLocalContentStoreLocation();
	}

	private void getLocalContentStoreLocation() {
		TextCustomPanelUtils.showSubTitle1(Messages.LOCAL_CONTENT_STORE);
		String input = TextCustomPanelUtils.getInput(
				Messages.LOCAL_CONTENT_STORE_LOC, localContentStorePath);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			getSharedContentStoreLocation();
			return;
		}
		TextCustomPanelUtils.logInput("local content store location: ", input);
		localContentStorePath = input.trim();
		validateContentStore();
	}

	private void validateContentStore() {
		InstallValidator iv = new InstallValidator();
		log.info("local content store path: " + localContentStorePath);
		log.info("shared content store path: " + sharedContentStorePath);
		try {
			boolean localResult = iv.validatePath(localContentStorePath);
			if (!localResult) {
				log.error("local content store path is not valid: "
						+ iv.getMessage());
				TextCustomPanelUtils.showError(iv.getMessage());
				returnToTop();
				return;
			}
			boolean sharedResult = iv.validatePath(sharedContentStorePath);
			if (!sharedResult) {
				log.error("shared content store path is not valid: "
						+ iv.getMessage());
				TextCustomPanelUtils.showError(iv.getMessage());
				returnToTop();
				return;
			}
		} catch (Exception e) {
			log.error(e);
			TextCustomPanelUtils.showError(e.getMessage());
			returnToTop();
			return;
		}
		TextCustomPanelUtils.showProgress(Messages.VALIDATION_SUCCESSFUL);
		log.info("local and shared content store path is valid");
		if (localContentStorePath.endsWith("/")
				|| localContentStorePath.endsWith("\\")) {
			StringBuilder tempLocalPath = new StringBuilder(
					localContentStorePath);
			tempLocalPath.deleteCharAt(tempLocalPath.length() - 1);
			localContentStorePath = tempLocalPath.toString();
		}
		if (sharedContentStorePath.endsWith("/")
				|| sharedContentStorePath.endsWith("\\")) {
			StringBuilder tempLocalPath = new StringBuilder(
					sharedContentStorePath);
			tempLocalPath.deleteCharAt(tempLocalPath.length() - 1);
			sharedContentStorePath = tempLocalPath.toString();
		}
		TextCustomPanelUtils.logUserData("user.contentStore.local.path",
				transferForWinPath(localContentStorePath));
		TextCustomPanelUtils.logUserData("user.contentStore.shared.path",
				convertPathToForwardSlash(sharedContentStorePath));
		TextCustomPanelUtils.logUserData("user.messageStore.shared.path",
				transferForWinPathTwice(escapeDollarSign(sharedContentStorePath)));
		TextCustomPanelUtils.logUserData("user.connections.install.location",
				transferPath(profile.getInstallLocation()));
		
		this.profile.setUserData("user.contentStore.local.path", transferWinPath(localContentStorePath));
		String transfersharedContentStorePath = convertPathToForwardSlash(sharedContentStorePath);
		String sharedContentStorePath_ccm = sharedContentStorePath;
		String ConStorePath = null;
		if (transfersharedContentStorePath.contains(":")){
    		int index = transfersharedContentStorePath.indexOf(":");
    		ConStorePath = transfersharedContentStorePath.substring(0,index) + "\\" + transfersharedContentStorePath.substring(index);
    		log.info("shared content store path: " + ConStorePath);
    		sharedContentStorePath_ccm = convertPathToForwardSlash(sharedContentStorePath);
    		sharedContentStorePath_ccm = sharedContentStorePath_ccm + "/ccm";
    		//showValidationSuccessMessageDialog(ConStorePath);
    	}
		else{
			ConStorePath = convertPathToForwardSlash(sharedContentStorePath);
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				sharedContentStorePath_ccm = sharedContentStorePath_ccm + "\\ccm";
			}
			else {
				sharedContentStorePath_ccm = sharedContentStorePath_ccm + "/ccm";
			}
		}
		this.profile.setUserData("user.contentStore.shared.path", transferWinPath(sharedContentStorePath));
		this.profile.setUserData("user.contentStore.shared.path.configproperties", sharedContentStorePath_ccm);
		this.profile.setUserData("user.messageStore.shared.path", transferWinConStorePath(transferWinPath(escapeDollarSign(sharedContentStorePath))));
		this.profile.setUserData("user.connections.install.location", transferPath(profile.getInstallLocation()));
		this.profile.setUserData("user.connections.install.location.win32format", profile.getInstallLocation());
		
		String ceInstallDir = transferPath(profile.getInstallLocation()) + "/FileNet/ContentEngine";
		String ceclientInstallDir = transferPath(profile.getInstallLocation()) + "/FileNet/CEClient";
		String fncsInstallDir = transferPath(profile.getInstallLocation()) + "/FNCS";
		
		this.profile.setUserData("user.ce.install.location", ceInstallDir);
		this.profile.setUserData("user.ceclient.install.location", ceclientInstallDir);
		this.profile.setUserData("user.fncs.install.location", fncsInstallDir);

		goToNext();
	}

	private void goToNext() {
		String input = TextCustomPanelUtils
				.getInput(Messages.CONTENT_STORE_NEXT_OR_REVALIDATE,
						Messages.NEXT_INDEX, new String[] {
								Messages.NEXT_INDEX,
								Messages.BACK_TO_TOP_INDEX,
								Messages.VALIDATE_INDEX,
								Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			getLocalContentStoreLocation();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.VALIDATE_INDEX)) {
			validateContentStore();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			getSharedContentStoreLocation();
		}
	}

	@Override
	public void returnToTop() {
		TextCustomPanelUtils.getInputNull(Messages.BACK_TO_TOP_NULL);
		// back to top
		getSharedContentStoreLocation();
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_CONTENT_STORE_PANEL;
	}

}
