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
package com.ibm.lconn.wizard.launcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardDialog;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;

public class DBWizardLauncher {
	public static void main(String[] args) {
		if (args != null && args.length > 0 && "-walkThrough".equals(args[0])) {
			DBWizardInputs.fake = true;
		}
		TestDataOffer.setLocale();
		new Shell(ResourcePool.getDisplay(), SWT.MIN | SWT.MODELESS | SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL | SWT.RESIZE | DBWizardDialog.getDefaultOrientation());
		DBWizard wizard = new DBWizard();
		
		DBWizardDialog dialog = new DBWizardDialog(null, wizard);
		wizard.setParentDialog(dialog);
		
		dialog.open();
	}
}
