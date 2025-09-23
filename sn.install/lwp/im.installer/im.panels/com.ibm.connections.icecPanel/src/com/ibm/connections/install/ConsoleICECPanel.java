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

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;

public class ConsoleICECPanel extends BaseConfigConsolePanel {
	
	String className = ConsoleICECPanel.class.getName();
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	
	public ConsoleICECPanel() {
		super(Messages.ICEC);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_ICEC_INFO_PANEL;
	}

	@Override
	public void perform() {
		
		log.info("Console ICEC Panel :: Entered");
		if (shouldSkip())
			return;

		profile = getProfile();
		
		TextCustomPanelUtils.setLogPanel(log, "ICEC Warning Panel");
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);

		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG, Messages.ICEC);
		addICECInfo();
	}

	private void addICECInfo() {
		TextCustomPanelUtils.showSubTitle1(Messages.ICEC);
		TextCustomPanelUtils.getInputWithoutValidation(Messages.ICEC_WARNING_MSG, null);

		log.info("Console ICEC Panel displaying warning message");
		
		goToNext();
	}

	private void goToNext() {
		String input = TextCustomPanelUtils.getInput(
				Messages.GOTO_NEXT, Messages.NEXT_INDEX,
				new String[] { Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.PREVIOUS_INPUT_INDEX,
						Messages.VALIDATE_INDEX });
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			addICECInfo();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			addICECInfo();
		}
	}
	
}
