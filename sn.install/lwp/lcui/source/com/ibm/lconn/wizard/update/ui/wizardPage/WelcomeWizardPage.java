/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited. 2008, 2022                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.update.ui.wizardPage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.common.LCUtil;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.interfaces.EmbeddedAction;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.websphere.update.ptf.ImageBaseInstaller;

public class WelcomeWizardPage extends CommonPage {
	
	public static final Logger logger = LogUtil.getLogger(WelcomeWizardPage.class);
	
	public static final int MAX = 3;
	private int[] line = new int[MAX];
	private int num = 0;
	private static String SPACE = " ";
	
	/**
	 * Create the wizard
	 */
	public WelcomeWizardPage() {
		super(Constants.WIZARD_PAGE_WELCOME);
		setTitle(Messages.getString("label.wizard.welcome.title"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.dbconfig.ui.wizardPage.CommonPage#onShow(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void onShow(Composite parent) {
		logger.log(Level.INFO,"Enter Welcome Page");
		String showText = getShowText();
		int[] bold = new int[this.num];
		for (int i = 0; i < this.num; i++) {
			bold[i] = this.line[i];
		}
		new BIDIStyledText(parent, null, showText, getCopyRight(), null, null);
	}
	
	private String getShowText(){
		StringBuilder showText = new StringBuilder();
		int line = 0;
		this.num = 0;
		
//		showText.append(Constants.UI_LINE_SEPARATOR);
//		line++;
//		showText.append("Lotus Connections");
//		showText.append(Constants.UI_LINE_SEPARATOR);
//		line++;
//		showText.append(Constants.UI_LINE_SEPARATOR);
//		line++;
		
		showText.append(Messages.getString("label.update.introduction.install.p1"));
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		
		this.line[this.num] = line;
		showText.append(Messages.getString("label.update.introduction.install.p3"));
		this.num++;
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		this.line[this.num] = line;
		showText.append("https://help.hcltechsw.com/connections/v8/admin/welcome/welcome_admin.html");
		this.num++;
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		this.line[this.num] = line;
		showText.append(Messages.getString("label.update.introduction.install.p4"));
		this.num++;
		showText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		
		return showText.toString();
	}
	private String getCopyRight(){
		StringBuilder copyRightText = new StringBuilder();
		int line = 0;
		copyRightText.append(Messages.getString("label.update.introduction.license.p1"));
		copyRightText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		copyRightText.append(Constants.UI_LINE_SEPARATOR);
		line++;
		
		copyRightText.append(Messages.getString("label.update.introduction.license.p2"));
		
		
		return copyRightText.toString();
	}
}
