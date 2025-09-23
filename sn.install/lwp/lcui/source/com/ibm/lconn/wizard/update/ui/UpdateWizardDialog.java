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
package com.ibm.lconn.wizard.update.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.msg.Messages;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class UpdateWizardDialog extends WizardDialog {
	private Point initialSize = new Point(800, 600);

	public UpdateWizardDialog(Shell parentShell, UpdateWizard newWizard) {
		super(parentShell, newWizard);
		newWizard.setParentDialog(this);
		setShellStyle(SWT.MIN | SWT.MODELESS | SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.LEFT_TO_RIGHT);
	}

	public boolean close() {
		if (this.getTheCancelButton() != null && this.getTheCancelButton().getEnabled()) {
			boolean status = new MessageDialog(getShell(), Messages.getString("updateWizard.window.title"), null, Messages.getString("wizard.close.question.message"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
			if (status)
				return super.close();
			else
				return status;
		}

		if (this.getTheFinishButton() != null && this.getTheFinishButton().getEnabled()) {
			boolean status = new MessageDialog(getShell(), Messages.getString("updateWizard.window.title"), null, Messages.getString("wizard.close.question.message"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
			if (status)
				return super.close();
			else
				return status;
		}
		return false;
	}

	public Button getThePrevButton() {
		return this.getButton(IDialogConstants.BACK_ID);
	}

	public Button getTheNextButton() {
		return this.getButton(IDialogConstants.NEXT_ID);
	}

	public Button getTheCancelButton() {
		return this.getButton(IDialogConstants.CANCEL_ID);
	}

	public Button getTheFinishButton() {
		return this.getButton(IDialogConstants.FINISH_ID);
	}

	protected void nextPressed() {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				UpdateWizard wizard = (UpdateWizard) getWizard();
				IWizardPage nextPage = wizard.getNextPage();
				wizard.firePageRenderAction(nextPage);
				showPage(nextPage);
			}
		});
		// show the next page

	}

	protected void createButtonsForButtonBar(Composite parent) {
		/*
		 * parent.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		 * CommonHelper.setColumn(parent, 1);
		 */
		super.createButtonsForButtonBar(parent);
	}
	
	protected void setButtonLayoutData(Button button) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = convertHorizontalDLUsToPixels(90);

		// On large fonts this can make this dialog huge
		widthHint = Math.min(widthHint,
				button.getDisplay().getBounds().width / 5);
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);

		button.setLayoutData(data);
	}
	

	protected void backPressed() {
		super.backPressed();
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	public Point getInitialSize() {
		return this.initialSize;
	}
}
