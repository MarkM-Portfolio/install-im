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

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.DbTypeSelectionWizardPage;

/**
 * @author joey (pengzsh@cn.ibm.com)
 *
 */
public class DBTypeSelectionPageRadioButtonListener implements SelectionListener {
	private DbTypeSelectionWizardPage page = null;

	/**
	 * @param page
	 */
	public DBTypeSelectionPageRadioButtonListener(DbTypeSelectionWizardPage page) {
		this.page = page;
	}
	
	public void widgetSelected(SelectionEvent e) {
		page.performSelection(e.widget);
		
	}
	public void widgetDefaultSelected(SelectionEvent e) {
		page.performSelection(e.widget);
	}
}
