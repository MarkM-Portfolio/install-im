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
package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;

public class WelcomeWizardPage extends CommonPage {
	/**
	 * Create the wizard
	 */
	public WelcomeWizardPage() {
		super(Constants.WIZARD_PAGE_WELCOME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.dbconfig.ui.wizardPage.CommonPage#onShow(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void onShow(Composite parent) {
		this.setTitle(Messages.getString("dbWizard.window.title"));
		
		StringBuilder showText = new StringBuilder();
//		if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())){
//			showText.append(Messages.getString("dbWizard.welcome.content1")).append(Constants.UI_LINE_SEPARATOR)
//			.append(Constants.UI_LINE_SEPARATOR).append("\uFFFC");
//		}else{
		showText.append(Messages.getString("dbWizard.welcome.content1_1")).append(Constants.UI_LINE_SEPARATOR)
			.append(Messages.getString("URL.dbWizard.infocenter"));
//		}
		showText.append(Constants.UI_LINE_SEPARATOR);	
		showText.append(Messages.getString("dbWizard.welcome.content2")).append(Constants.UI_LINE_SEPARATOR);
		String copyright = Messages.getString("dbWizard.license")
				+ Constants.UI_LINE_SEPARATOR;
//		if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())){
//		EmbeddedAction c = new EmbeddedAction() {
//			public String execute() {
//				if (!CommonHelper.openHTML(Messages
//						.getString("URL.dbWizard.infocenter")))
//					showInputCheckErrorMsg(ErrorMsg
//							.getString("dbwizard.error.cannot.launch.file"));
//				return Messages.getString("URL.dbWizard.infocenter");
//			}
//
//			public String getActionLabel() {
//				return Messages.getString("dbWizard.welcome.launch.infocenter");
//			}
//
//			public String getLogPath() {
//				return null;
//			}
//
//			public int getHorizontalAlignment() {
//				return SWT.CENTER;
//			}
//
//			public int getVerticalAlignment() {
//				return SWT.CENTER;
//			}
//
//		};
//		new BIDIStyledText(parent, null, showText.toString(), copyright, c, null);
//		}else{
		new BIDIStyledText(parent, null, showText.toString(), copyright, null, null);
//		}
	}
}
