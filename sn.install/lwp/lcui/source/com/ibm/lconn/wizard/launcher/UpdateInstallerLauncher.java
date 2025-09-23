/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.launcher;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.update.ui.UpdateWizard;
import com.ibm.lconn.wizard.update.ui.UpdateWizardDialog;

public class UpdateInstallerLauncher {
	public static void main(String[] args) {
		TestDataOffer.setLocale();
		new Shell(ResourcePool.getDisplay(), SWT.MIN | SWT.MODELESS | SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL | SWT.RESIZE | UpdateWizardDialog.getDefaultOrientation());
		UpdateWizard wizard = new UpdateWizard();
		
		UpdateWizardDialog dialog = new UpdateWizardDialog(null, wizard);
		wizard.setParentDialog(dialog);
		
		dialog.open();    
	}
}
