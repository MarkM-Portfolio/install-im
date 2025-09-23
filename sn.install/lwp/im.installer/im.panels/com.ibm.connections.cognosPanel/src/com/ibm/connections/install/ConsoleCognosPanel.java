/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.connections.install;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;

public class ConsoleCognosPanel extends BaseConfigConsolePanel {
	String className = ConsoleCognosPanel.class.getName();
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());
	private Map<String, String> nodeHostnameMap = new HashMap<String, String>();
	private Map<String, List<String>> nodeServernameMap = new HashMap<String, List<String>>();
	private String cognosUserid = "";
	private String cognosPassword = null;
	private List<String> nodesList = new ArrayList<String>();
	private List<String> serverList = new ArrayList<String>();
	private int selectNodeIndex = -1;
	private int selectServerIndex = -1;
	private String hostName = "";
	private String cognosContextRoot = "cognos";
	private String cognosPort = "9080";
	private boolean isLoadNodeInfo = false;
	private IProfile profile = null; // profile to save data in
	/** 1 - configure cognos now, 2 - configure cognos later */
	private int option = 1;

	public ConsoleCognosPanel() {
		super(Messages.COGNOS_CONFIGURATION);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_COGNOS_PANEL;
	}

	@Override
	public void perform() {
		if (shouldSkip())
			return;
		log.info("Cognos Configuration Panel :: Entered");
		TextCustomPanelUtils.setLogPanel(log, "Cognos panel");
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);
		
		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG,
				Messages.COGNOS_CONFIGURATION);
		
		TextCustomPanelUtils.showText(Messages.COGNOS_DO_LATER_INFO);
			String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
					Messages.NEXT_INDEX, new String[] {
							Messages.PREVIOUS_INPUT_INDEX,
							Messages.BACK_TO_TOP_INDEX, Messages.NEXT_INDEX });
			if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)
					|| input.trim()
							.equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
				perform();
			}
	}

	private void goToNext() {
		String input = TextCustomPanelUtils
				.getInput(Messages.COGNOS_NEXT_OR_REVALIDATE,
						Messages.NEXT_INDEX, new String[] {
								Messages.NEXT_INDEX,
								Messages.BACK_TO_TOP_INDEX});
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			perform();
		}
	}

	@Override
	public void returnToTop() {
		String input = TextCustomPanelUtils.getInput(
				Messages.BACK_TO_TOP_OR_REVALIDATE, null, new String[] {
						Messages.BACK_TO_TOP_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX))
			perform();
		
	}


}
