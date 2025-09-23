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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class LdapSSLConfirmDialog extends MessageDialog {

	public LdapSSLConfirmDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String dialogButtonLabels[], int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
	}

	public static int openSecurityConfirmDialog(Shell parent, String title,
			String message, String[] btn_labels, boolean enableAlwaysBtn) {

		LdapSSLConfirmDialog dialog = new LdapSSLConfirmDialog(parent, title,
				null, message, MessageDialog.WARNING, btn_labels, 1);

		dialog.create();
		Button btn = dialog.getButton(0);
		btn.setEnabled(enableAlwaysBtn);
		return dialog.open();
	}
}
