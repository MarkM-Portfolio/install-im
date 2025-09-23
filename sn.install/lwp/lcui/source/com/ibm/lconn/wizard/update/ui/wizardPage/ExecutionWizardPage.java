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
package com.ibm.lconn.wizard.update.ui.wizardPage;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.update.action.EFixInstallAction;
import com.ibm.lconn.wizard.update.action.EFixUninstallAction;
import com.ibm.lconn.wizard.update.ui.UpdateWizard;
import com.ibm.lconn.wizard.update.ui.UpdateWizardDialog;
import com.ibm.lconn.wizard.update.ui.UpdateWizardInputs;

public class ExecutionWizardPage extends CommonPage {
	private String logPath;
	private Label message;
	
	private ProgressBar pb;
	public static final Logger logger = LogUtil.getLogger(ExecutionWizardPage.class);

	public ExecutionWizardPage() {
		super(Constants.WIZARD_PAGE_EXECUTION);
	}

	public boolean canFlipToNextPage() {
		setCancelPrevButtonEnable(false);
		return false;
	}

	public void onShow(Composite parent) {
		logger.log(Level.INFO,"Enter Execution Page");
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			setTitle(Messages.getString("label.wizard.execution.title.install"));
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			setTitle(Messages.getString("label.wizard.execution.title.uninstall"));
		}
		final Composite container = CommonHelper.createScrollableControl(
				Composite.class, parent, SWT.NONE, SWT.V_SCROLL);
		container.setLayout(new GridLayout());
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		container.setLayout(gridLayout);
		
		this.message = new Label(container, SWT.WRAP);
		message.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			message.setText(Messages.getString("label.wizard.execution.log.dec.install", CommonHelper.getWizardLogFilesPath()) + "\n\n");
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			message.setText(Messages.getString("label.wizard.execution.log.dec.uninstall", CommonHelper.getWizardLogFilesPath()) + "\n\n");
		}
		pb = new ProgressBar(container, SWT.INDETERMINATE);
		pb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	

		new Thread(){
			public void run(){	
				if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
					EFixInstallAction efixInstallAction = new  EFixInstallAction();
					try {
						efixInstallAction.install(UpdateWizardInputs.getEFixImages(getWizard()));
					} catch (InterruptedException e) {
						logger.log(Level.SEVERE, "INSTALL:: " + e.getStackTrace().toString());
					}
					UpdateWizardInputs.setUpdateEvens(getWizard(), efixInstallAction.getUpdates());
				}
				if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
					EFixUninstallAction efixUninstallAction = new  EFixUninstallAction();
					try {
						efixUninstallAction.uninstall(UpdateWizardInputs.getFixIDsForUnstall(getWizard()));
					} catch (InterruptedException e) {
						logger.log(Level.SEVERE, "UNINSTALL:: " + e.getStackTrace().toString());
					}
					UpdateWizardInputs.setUpdateEvens(getWizard(), efixUninstallAction.getUpdates());
				}
	
				enterFinishPage();
			}
		}.start();
		
	}
	
	protected void updateDialogButtons() {
		UpdateWizardDialog parentDialog = getWizard().getParentDialog();
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			parentDialog.getTheNextButton().setText(Messages.getString("button.install.text"));
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			parentDialog.getTheNextButton().setText(Messages.getString("button.uninstall.text"));
		}	
	}
	
	private void setCancelPrevButtonEnable(boolean enable) {
		UpdateWizardDialog parentDialog = getWizard().getParentDialog();
		parentDialog.getTheCancelButton().setEnabled(enable);
		parentDialog.getThePrevButton().setEnabled(enable);
	}
	
	private void enterFinishPage(){
		
		getWizard().getParentDialog().getShell().getDisplay().asyncExec(new Runnable(){

			public void run() {
				UpdateWizard parentWizard = getWizard();
				parentWizard.getContainer().showPage(parentWizard.getPage(Constants.WIZARD_PAGE_FINISH));
				parentWizard.getParentDialog().getTheNextButton().setEnabled(true);
			}
			
		});
	}
	
	private void installFix(){
		
	}
	
}
