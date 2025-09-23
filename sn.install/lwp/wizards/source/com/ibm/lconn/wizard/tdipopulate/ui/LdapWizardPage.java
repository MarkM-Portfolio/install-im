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
package com.ibm.lconn.wizard.tdipopulate.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.ResourcePool;

public class LdapWizardPage {

	private String subject = null;
	private String issuedTo = null;
	private String issuedBy = null;
	private Date validFrom = null;
	private Date validTo = null;
	private boolean enabledAwaysBtn = false;

	public LdapWizardPage() {

	}

	public boolean isEnabledAwaysBtn() {
		return enabledAwaysBtn;
	}

	public void setEnabledAwaysBtn(boolean enabledAwaysBtn) {
		this.enabledAwaysBtn = enabledAwaysBtn;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public String getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(String issuedTo) {
		this.issuedTo = issuedTo;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public int showDialog() {
		// shell.setText("Send Message");

		Locale.setDefault(Locale.ENGLISH);
		Shell shell = new Shell(ResourcePool.getDisplay());
		String title = MessageUtil.getWizardTitle(Constants.WIZARD_ID_TDIPOPULATE);//Messages.getString("LdapWizardPage.title");
		SimpleDateFormat sdf = new SimpleDateFormat();
		
		StringBuffer infoMsg = new StringBuffer();
		infoMsg.append(Messages.getString("LdapWizardPage.info.message"));
		infoMsg.append(Constants.CRLF);
		infoMsg.append(Constants.CRLF);
		infoMsg.append(Messages.getString("LdapWizardPage.info.summary"));
		infoMsg.append(Constants.CRLF);
//		infoMsg.append(Messages.getString("LdapWizardPage.info.subject", subject));
//		infoMsg.append(Constants.CRLF);
		infoMsg.append(Messages.getString("LdapWizardPage.info.issuedTo", issuedTo));
		infoMsg.append(Constants.CRLF);
		infoMsg.append(Messages.getString("LdapWizardPage.info.issuedBy", issuedBy));
		infoMsg.append(Constants.CRLF);
		infoMsg.append(Messages.getString("LdapWizardPage.info.issuedOn", sdf.format(validFrom)));
		infoMsg.append(Constants.CRLF);
		infoMsg.append(Messages.getString("LdapWizardPage.info.expiresOn", sdf.format(validTo)));

		String label_always = Messages.getString("LdapWizardPage.label.always");
//		String label_this_time_only = Messages
//				.getString("LdapWizardPage.label.this_time_only");
		String label_no = Messages.getString("LdapWizardPage.label.no");

		String[] labels = new String[] { label_always,
				label_no };

		int result = LdapSSLConfirmDialog.openSecurityConfirmDialog(shell,
				title, infoMsg.toString(), labels, enabledAwaysBtn);

		return result;

	}
}
