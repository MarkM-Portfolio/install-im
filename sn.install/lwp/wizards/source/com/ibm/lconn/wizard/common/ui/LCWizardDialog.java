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
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.msg.Messages;

/**
 * @author Jun Jing Zhang
 * 
 */
public class LCWizardDialog extends WizardDialog {

	private Point initialSize = new Point(800, 600);
	private String logPath;
	private Button logPathButton;
	
	@Override
	public boolean close() {
		Button cancelButton = this.getButton(IDialogConstants.CANCEL_ID);
		if(cancelButton!=null && cancelButton.getEnabled()) {
			boolean status = new MessageDialog(getShell(), Messages.getString("WIZARD.title.tdipopulate"), null, Messages.getString("wizard.close.question.message"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
			if (status)
				return super.close();
			else
				return status;
		}
		else return false;
	}

	/**
	 * @param parentShell
	 * @param newWizard
	 */
	public LCWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell,
				(Assert.isTrue(newWizard instanceof LCWizard) ? newWizard
						: null));
		setShellStyle(SWT.MIN | SWT.MODELESS | SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.LEFT_TO_RIGHT);
	}

	protected LCWizard getWizard() {
		return (LCWizard) super.getWizard();
	}

	public void updateButtons() {
		super.updateButtons();
		int buttonStyle = getWizard().getButtonStyle();
		setButtonEnabled(IDialogConstants.FINISH_ID, true);
		if (getWizard().canFinish()) {
			setButtonText(IDialogConstants.FINISH_ID,
					Constants.BUTTON_TEXT_FINISH);
		} else {
			setButtonText(IDialogConstants.FINISH_ID,
					Constants.BUTTON_TEXT_NEXT);
		}
		setButtonEnabled(IDialogConstants.BACK_ID, !CommonHelper.isStyle(
				buttonStyle, Constants.DISABLE_BACK));
		setButtonEnabled(IDialogConstants.FINISH_ID, !CommonHelper.isStyle(
				buttonStyle, Constants.DISABLE_NEXT));
		setButtonEnabled(IDialogConstants.CANCEL_ID, !CommonHelper.isStyle(
				buttonStyle, Constants.DISABLE_CANCEL));
		getWizard().setButtonStyle(Constants.DISABLE_NONE);
	}

	public void finishPressed() {
		if (getWizard().canFinish()) {
			getWizard().performFinish();
		} else {
			nextPressed();
		}
	}

	protected void buttonPressed(int buttonId) {
		setLogPath(null);
		CommonHelper.resetButtonText();
		try {
			((LCWizardPage) getWizard().getContainer().getCurrentPage())
					.collectInput();
		} catch (Exception e) {
		}
		switch (buttonId) {
		case IDialogConstants.HELP_ID: {
			helpPressed();
			break;
		}
		case IDialogConstants.BACK_ID: {
			backPressed();
			break;
		}
		case IDialogConstants.NEXT_ID: {
			nextPressed();
			break;
		}
		case IDialogConstants.FINISH_ID: {
			finishPressed();
			break;
		}
		
			// The Cancel button has a listener which calls cancelPressed
			// directly
		}
	}

	protected void nextPressed() {
		// show the next page
		BusyIndicator.showWhile(this.getShell().getDisplay(), new Runnable() {
			public void run() {
				IWizardPage nextPage = getWizard().getNextPage();
				showPage(nextPage);
			}
		});		
	}

	private void setButtonText(int id, String buttonText) {
		Button button = getButton(id);
		if (button != null)
			button.setText(buttonText);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		CommonHelper.setColumn(parent, 1);
		final Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayout(new GridLayout());
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	
		logPathButton = new Button(composite_1, SWT.PUSH);
		logPathButton.setText(MessageUtil.getLabel("VIEW_LOG"));
		logPathButton.addSelectionListener(new SelectionAdapter(){
			
			public void widgetSelected(SelectionEvent selectionevent) {
				String logPath2 = getLogPath();
				if(logPath2!=null && new File(logPath2).exists())
					CommonHelper.openLog(logPath2);
			}});
		logPathButton.setVisible(false);
		
		getShell().setImage(ResourcePool.getWizardTitleIcon());

		if (!getWizard().needsPreviousAndNextButtons()) {
			((GridLayout) parent.getLayout()).numColumns++;
			createButton(parent, IDialogConstants.BACK_ID,
					Messages.getString("button.prev.text"), false);
		}
		super.createButtonsForButtonBar(parent);
		setButtonText(IDialogConstants.BACK_ID, Constants.BUTTON_TEXT_BACK);
		setButtonEnabled(IDialogConstants.BACK_ID, false);
		setButtonText(IDialogConstants.CANCEL_ID, Constants.BUTTON_TEXT_CANCEL);
		setButtonText(IDialogConstants.FINISH_ID, Constants.BUTTON_TEXT_NEXT);
	}

	protected String getLogPath() {
		return this.logPath;
	}

	public void run(boolean fork, boolean cancelable,
			IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {
		setButtonEnabled(IDialogConstants.BACK_ID, false);
		setButtonEnabled(IDialogConstants.FINISH_ID, false);
		setButtonEnabled(IDialogConstants.CANCEL_ID, cancelable);
		super.run(fork, cancelable, runnable);
	}

	private void setButtonEnabled(int id, boolean enabled) {
		Button button = getButton(id);
		if (button != null)
			button.setEnabled(enabled);
	}

	public Point getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int width, int height) {
		this.initialSize = new Point(width, height);
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
		logPathButton.setVisible(logPath!=null);
	}

}
