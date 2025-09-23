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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ValuePool;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.GroupUtil;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class CommunitiesOptionalTaskWizardPage extends CommonPage{
	
	protected CommunitiesOptionalTaskWizardPage() {
		super(Constants.WIZARD_PAGE_ENABLE_COMMUNITIES_OPTIONAL_TASK);
		setTitle(Messages
				.getString("dbWizard.CommunitiesOptionalTaskWizardPage.title")); //$NON-NLS-1$
	}

	@Override
	public void onShow(Composite parent) {
		Composite container = CommonHelper.createScrollableControl(
				Composite.class, parent,SWT.NONE, SWT.V_SCROLL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		
		container.setLayout(gridLayout);
		
		Label dec = new Label(container, SWT.WRAP);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		dec.setText(Messages
				.getString("dbWizard.CommunitiesOptionalTaskWizardPage.description")); //$NON-NLS-1$
		
		final Group group = GroupUtil.createRadioGroup(new String[] {
				Constants.OPTION_YES,
				Constants.OPTION_NO,
				 }, container, SWT.NONE,
				SWT.NONE,  new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent event) {
						Button b = (Button)event.getSource();
						if(b.getSelection()) {
							DBWizardInputs.setEnableCommunitiesOptionalTask(getWizard(), GroupUtil.getButtonId(b));
						}
					}

					public void widgetSelected(SelectionEvent event) {
						Button b = (Button)event.getSource();
						if(b.getSelection()) {
							DBWizardInputs.setEnableCommunitiesOptionalTask(getWizard(), GroupUtil.getButtonId(b));
						}
					}
			
			},
				new ValuePool() {
					public String getString(String key) {
						return Messages
								.getString("dbWizard.CommunitiesOptionalTaskWizardPage.option." + key + ".label"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				},
		"dbWizard.CommunitiesOptionalTaskWizardPage.question.label"	//$NON-NLS-1$
		);
		String defaultSelection = DBWizardInputs.getEnableCommunitiesOptionalTask(getWizard());
		if(defaultSelection == null || "".equals(defaultSelection)) {
			defaultSelection = Constants.OPTION_YES;
			DBWizardInputs.setEnableCommunitiesOptionalTask(getWizard(), defaultSelection);
		}
		GroupUtil.setSelectedButton(group, defaultSelection, true, true, true);		
		setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
	}
	
}
