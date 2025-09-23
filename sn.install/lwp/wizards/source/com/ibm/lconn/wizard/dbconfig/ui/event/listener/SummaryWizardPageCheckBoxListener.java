/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
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
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.SummaryWizardPage;

public class SummaryWizardPageCheckBoxListener implements SelectionListener{
	private SummaryWizardPage page;

	public SummaryWizardPageCheckBoxListener(SummaryWizardPage page) {
		super();
		this.page = page;
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		Object obj = event.widget;
		if(obj == null)
			return;
		if(obj instanceof Button){
			Button button = (Button) obj;
				DBWizardInputs.setNannyMode(page.getWizard(),button.getSelection());
		}
	}

	public void widgetSelected(SelectionEvent event) {
		Object obj = event.widget;
		if(obj == null)
			return;
		if(obj instanceof Button){
			Button button = (Button) obj;
				DBWizardInputs.setNannyMode(page.getWizard(),button.getSelection());
		}
	}

}
