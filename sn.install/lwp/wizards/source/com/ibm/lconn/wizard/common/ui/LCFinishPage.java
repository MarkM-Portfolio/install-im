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
package com.ibm.lconn.wizard.common.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.interfaces.LCAction;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;

public class LCFinishPage extends LCWizardPage {
	
	public LCFinishPage(WizardPageData data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	LCAction action = null;

	public void createControl(Composite parent) {
		Composite composite = CommonHelper.createEmptyPanel(parent,
				new GridData());
		composite.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event arg0) {
				String finishPageName = action.execute();
				showFinishPage(finishPageName);
			}
		});

		ProgressBar pb = new ProgressBar(composite, SWT.HORIZONTAL
				| SWT.INDETERMINATE);
		pb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setControl(composite);
	}

	protected void execute() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				if(action!=null)
					action.execute();
			}
		});
		thread.start();
	}
	
	public IWizardPage getPreviousPage(){
		return null;
	}
	
	public IWizardPage getNextPage(){
		return null;
	}
	
	protected void showFinishPage(String page) {
		getShell().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						IWizardPage finishPage = getWizard()
								.getPage(Constants.WIZARD_PAGE_FINISH_PANEL);
						getWizard().getContainer().showPage(finishPage);
					}
				});
	}
}

