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

public class ConsoleUserMappingPanel extends BaseConfigConsolePanel {
	
	String className = ConsoleUserMappingPanel.class.getName();
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	
	public ConsoleUserMappingPanel() {
		super(Messages.WEB_SERVER);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_USER_MAPPING_PANEL;
	}

	@Override
	public void perform() {
		
		log.info("Console User Mapping Panel :: Entered");
		if (shouldSkip())
			return;

		profile = getProfile();
		
		TextCustomPanelUtils.setLogPanel(log, "User Mapping Panel");
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);

		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG, Messages.USER_MAPPING);
		addUserMapping();
	}

	private void addUserMapping() {
		
		TextCustomPanelUtils.showSubTitle1(Messages.USER_MAPPING_ADMINISTRATIVE_USERS_NAME);
		String administrativeUsersText = TextCustomPanelUtils.getInputWithoutValidation(Messages.USER_MAPPING_ADMINISTRATIVE_USERS_DESC.replaceAll("\n", ""), null);
		profile.setUserData("user.user.mapping.administrative.users", administrativeUsersText);
		
		log.info("Console User Mapping Panel administrativeUsersText :: "+administrativeUsersText);
		
		TextCustomPanelUtils.showSubTitle1(Messages.USER_MAPPING_GLOBAL_MODERATOR_USERS_NAME);
		String globalModeraterUsersText = TextCustomPanelUtils.getInputWithoutValidation(Messages.USER_MAPPING_GLOBAL_MODERATOR_USERS_DESC.replaceAll("\n", ""), null);
		profile.setUserData("user.user.mapping.global.moderater.users", globalModeraterUsersText);

		log.info("Console User Mapping Panel globalModeraterUsersText :: "+globalModeraterUsersText);
		
		setRoleMappingForModify();
		
		goToNext();
	}

	private void goToNext() {
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] { Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			addUserMapping();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			addUserMapping();
		}
	}
	
	private void setRoleMappingForModify(){
		
		if(isModify()){
			String admin = profile.getUserData("user.user.mapping.administrative.users");
			String glbModerater = profile.getUserData("user.user.mapping.global.moderater.users");
			
			StringBuffer sb = new StringBuffer();
			if(admin != null && !"".equals(admin))
				sb.append("\"admin\": \"").append(admin).append("\",");
			if(admin != null && !"".equals(admin))
				sb.append("\"global-moderator\": \"").append(glbModerater).append("\",");
			
			profile.setUserData("user.profiles.role.mapping", sb.toString());
			profile.setUserData("user.activities.role.mapping", sb.toString());
			profile.setUserData("user.blogs.role.mapping", sb.toString());
			profile.setUserData("user.communities.role.mapping", sb.toString());
			profile.setUserData("user.ccm.role.mapping", sb.toString());
			profile.setUserData("user.dogear.role.mapping", sb.toString());
			profile.setUserData("user.forums.role.mapping", sb.toString());
			profile.setUserData("user.metrics.role.mapping", sb.toString());
			profile.setUserData("user.wikis.role.mapping", sb.toString());
			profile.setUserData("user.mobile.role.mapping", sb.toString());
			profile.setUserData("user.moderation.role.mapping", sb.toString());
		}
	}
	
}
