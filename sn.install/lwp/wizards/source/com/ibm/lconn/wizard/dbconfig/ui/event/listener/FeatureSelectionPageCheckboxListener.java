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
package com.ibm.lconn.wizard.dbconfig.ui.event.listener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import com.ibm.lconn.wizard.common.ui.GroupUtil;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.FeatureSelectionWizardPage;

/**
 * @author joey (pengzsh@cn.ibm.com)
 *
 */
public class FeatureSelectionPageCheckboxListener implements SelectionListener {
	private FeatureSelectionWizardPage page = null;

	/**
	 * @param page
	 */
	public FeatureSelectionPageCheckboxListener(FeatureSelectionWizardPage page) {
		this.page = page;
	}
	
	public void widgetSelected(SelectionEvent event) {
		if (event.widget == null)
			return;
		if (event.widget instanceof Button) {
			Button button = (Button)event.widget;
			boolean isChecked = button.getSelection();
			if (isChecked) {
				page.performSelect(GroupUtil.getButtonId(button));
			} else {
				page.performUnSelect(GroupUtil.getButtonId(button));
			}			
		}
	}
	public void widgetDefaultSelected(SelectionEvent event) {
		if (event.widget == null)
			return;
		if (event.widget instanceof Button) {
			Button button = (Button)event.widget;
			boolean isChecked = button.getSelection();
			if (isChecked) {
				page.performSelect(GroupUtil.getButtonId(button));
			} else {
				page.performUnSelect(GroupUtil.getButtonId(button));
			}			
		}
	}
}
