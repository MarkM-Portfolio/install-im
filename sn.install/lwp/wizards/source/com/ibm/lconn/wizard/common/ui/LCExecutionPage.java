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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.interfaces.LCAction;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;

public class LCExecutionPage extends LCWizardPage {

	public LCExecutionPage(WizardPageData data) {
		super(data);
	}

	LCAction action = null;
	private Button logPathButton;
	private Label descLabel;

	protected Composite createControls(Composite parent) {
		Composite composite = CommonHelper.createEmptyPanel(parent,
				new GridData(GridData.FILL_BOTH));
		super.createDescLabel(composite);

		descLabel = new Label(composite, SWT.NONE);
		descLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		descLabel.setText("");
		new Label(composite, SWT.NONE).setText("\n");
		ProgressBar pb = new ProgressBar(composite, SWT.HORIZONTAL
				| SWT.INDETERMINATE);
		pb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(composite, SWT.NONE).setText("\n");
		logPathButton = new Button(composite, SWT.PUSH);
		logPathButton.setText(MessageUtil.getLabel("VIEW_LOG"));
		logPathButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent selectionevent) {
				String logPath2 = getLogPath();
				if (logPath2 != null && new File(logPath2).exists()) {
					if (new File(logPath2).isDirectory()) {
						Program.launch(logPath2);
					} else {
						CommonHelper.openLog(logPath2);
					}
				}
			}
		});

		this.addPageRenderListener(new Listener() {

			public void handleEvent(Event arg0) {
				// LCWizardDialog parentDialog = getWizard().getParentDialog();
				// if (parentDialog != null)
				// parentDialog.setLogPath(action.getLogPath());
				getWizard().setButtonStyle(
						Constants.DISABLE_BACK | Constants.DISABLE_NEXT
								| Constants.DISABLE_CANCEL);
				execute();
			}
		});
		refresh();
		// setControl(composite);
		return composite;
	}

	public void setTaskMessage(String description) {
		DataPool.setValue(getData().getWizardId(), getData().getId()
				+ ".description", description);
	}

	public String getTaskMessage() {
		return DataPool.getValue(getData().getWizardId(), getData().getId()
				+ ".description");
	}

	public void refresh() {
		Composite control = (Composite) getControl();
		if (control == null)
			return;
		String execTxtLabel = getTaskMessage();
		descLabel.setText(execTxtLabel);
		(control).layout(true,true);
	}

	public void refreshAsyn() {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				refresh();
			}
		});
	}

	public String getLogPath() {
		return action.getLogPath();
	}

	protected void execute() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				if (action != null) {
					String finishPageName = action.execute();
					showFinishPage(finishPageName);
				}
			}
		});
		thread.start();
	}

	public IWizardPage getPreviousPage() {
		return null;
	}

	public IWizardPage getNextPage() {
		return null;
	}

	protected void showFinishPage(String finishPageName) {
		final String page = finishPageName;
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				// LCWizardDialog parentDialog = getWizard().getParentDialog();
				// if (parentDialog != null)
				// parentDialog.setLogPath(action.getLogPath());

				IWizardPage finishPage = getWizard().getPage(page);
				getWizard().getContainer().showPage(finishPage);
			}
		});
	}

	public void setAction(LCAction action) {
		this.action = action;
	}
}
