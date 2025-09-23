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

public class ConsoleWebServerPanel extends BaseConfigConsolePanel {
	
	String className = ConsoleWebServerPanel.class.getName();
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	
	private int operationType = 0;
	
	public ConsoleWebServerPanel() {
		super(Messages.WEB_SERVER);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_WEB_SERVER_PANEL;
	}

	@Override
	public void perform() {
		
		log.info("Console WebServer Panel :: Entered");
		if (shouldSkip())
			return;

		profile = getProfile();
		
		TextCustomPanelUtils.setLogPanel(log, "WebServer Panel");
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);
		profile = getProfile();

		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG, Messages.WEB_SERVER);
		chooseDoLaterOrDoNow();
		
	}

	private void chooseDoLaterOrDoNow() {
		//returnFlag = false;
		TextCustomPanelUtils.showSubTitle1(Messages.WEB_SERVER_SELECTION);
		operationType = TextCustomPanelUtils
				.singleSelect(Messages.WEB_SERVER_SELECTION,
						new String[] { Messages.WEB_SERVER_DO_LATER,
								Messages.WEB_SERVER_DO_NOW}, 0,
						null, null);
		TextCustomPanelUtils.logInput(Messages.WEB_SERVER_SELECTION, operationType == 1 ? "DO LATER" :  "DO NOW");
		
		if (operationType == 2)
			chooseWebServer();
		else{
			if(profile.getUserData("user.web.server.name") != null && !"".equals(profile.getUserData("user.web.server.name")))
				profile.setUserData("user.web.server.name", "");
			if(profile.getUserData("user.web.server.node") != null && !"".equals(profile.getUserData("user.web.server.node")))
				profile.setUserData("user.web.server.node", "");
			goToNext();
		}
	}
	
	private void chooseWebServer() {
		
		String webServersString = profile.getData("webServers");
		log.info("ConsoleWebServerPanel:: web servers: " + webServersString);
		String[] webServers = webServersString.split(";");
		
		log.info("ConsoleWebServerPanel:: web servers length: " + webServers.length);
		if(webServers.length == 1 && "".equals(webServers[0])){
			TextCustomPanelUtils.showNotice(Messages.NO_WEB_SERVER_DETECTED);
			chooseDoLaterOrDoNow();
			return;
		}
		
		int idx = TextCustomPanelUtils
				.singleSelect(Messages.WEB_SERVER_EXISTED_SELECTION, webServers, 0,
						null, null) - 1;
		
		log.info("ConsoleWebServerPanel:: web servers index: " + idx );
		
		if (idx >= 0 && idx < webServers.length){
			log.info("ConsoleWebServerPanel:: webServerItem: "+webServers[idx]);
			setUpWebServerInfo(webServers[idx]);
		}
		else
			chooseWebServer();
	}

	private void goToNext() {
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] { Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			chooseDoLaterOrDoNow();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			chooseDoLaterOrDoNow();
		}
	}
	
	private void setUpWebServerInfo(String webServerItem) {
		if(webServerItem != null && webServerItem.contains(",")){
			String[] webServerItemArray = webServerItem.split(",");
			profile.setUserData("user.web.server.name", webServerItemArray[0]);
			profile.setUserData("user.web.server.node", webServerItemArray[1]);
		}
		goToNext();
	}
	
}
