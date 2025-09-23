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
package com.ibm.lconn.wizard.dbconfig.ui.event.listener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.OperationOptionWizardPage;

/**
 * @author joey (pengzsh@cn.ibm.com)
 *
 */
public class OperationOptionPageListener implements SelectionListener {
	private OperationOptionWizardPage page = null;

	/**
	 * @param page
	 */
	public OperationOptionPageListener(OperationOptionWizardPage page) {
		this.page = page;
	}
	
	public void widgetSelected(SelectionEvent e) {
		page.performSelection(e.widget);
	}
	public void widgetDefaultSelected(SelectionEvent e) {
		page.performSelection(e.widget);
	}
}
