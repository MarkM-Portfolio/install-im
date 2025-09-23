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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.update.ui.UpdateWizard;
import com.ibm.lconn.wizard.update.ui.UpdateWizardDialog;
import com.ibm.lconn.wizard.update.ui.UpdateWizardInputs;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public abstract class CommonPage extends WizardPage {
	private Composite leftPanel;
	private boolean leaving = false;
	private IWizardPage nextPage = null;

	protected void updateDialogButtons() {
		UpdateWizard wizard = this.getWizard();
		UpdateWizardDialog parentDialog = wizard.getParentDialog();
		try {
			Object o = parentDialog.getTheFinishButton().getLayoutData();
			GridData g = (GridData) o;
			g.exclude = true;
			parentDialog.getTheFinishButton().setVisible(false);

			parentDialog.getTheCancelButton().setText(Messages.getString("button.cancel.text"));
			parentDialog.getThePrevButton().setText(Messages.getString("button.prev.text"));
			parentDialog.getTheNextButton().setText(Messages.getString("button.next.text"));
		} catch (Exception e) {
		}
	}

	public abstract void onShow(Composite parent);

	public void createControl(Composite parent) {
		updateDialogButtons();
		Composite control = createTheLayout(parent);
		onShow(leftPanel);
		addEventToControl(control);
		setControl(control);
	}

	public void addEventToControl(Control control) {
		control.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event) {
				updateDialogButtons();
				Composite c = (Composite) event.widget;
				Composite parent = c.getParent();
				c.dispose();
				Composite control = createTheLayout(parent);
				onShow(leftPanel);
				addEventToControl(control);
				setControl(control);
				parent.layout(true, true);
			}
		});
	}

	private Composite createTheLayout(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout);

		// add the left part.
		this.leftPanel = new Composite(container, SWT.NONE);
		GridData leftGrid = new GridData(10, 40);
		leftGrid.grabExcessHorizontalSpace = true;
		leftGrid.grabExcessVerticalSpace = true;
		leftGrid.horizontalAlignment = SWT.FILL;
		leftGrid.verticalAlignment = SWT.FILL;
		leftPanel.setLayoutData(leftGrid);
		GridLayout leftLayout = new GridLayout();
		leftLayout.marginHeight = 0;
		leftLayout.marginWidth = 0;
		leftPanel.setLayout(leftLayout);

		// add the right part.
		GridData rightGrid = new GridData(SWT.FILL, SWT.TOP, false, true);
		rightGrid.verticalAlignment = SWT.TOP;
		Label imageLabel = new Label(container, SWT.NONE);

		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			imageLabel.setImage(ResourcePool.getImage(CommonPage.class, "/icons/LeftSideImage.JPG"));
			rightGrid.heightHint = 400;

		} else {
			imageLabel.setImage(ResourcePool.getImage(CommonPage.class, "/icons/LeftSideImage_short.JPG"));
		}
		imageLabel.setLayoutData(rightGrid);

		return container;
	}

	protected void setControl(Control control) {
		super.setControl(control);
	}
	
	public void showInputCheckInfoMsg(String message) {
		String title = Messages.getString("updateWizard.window.title");
		if (CommonHelper.isEmpty(message)) {
			message = ErrorMsg.getString("error.default.message");
		}
		new MessageDialog(getShell(), title, null, message, MessageDialog.INFORMATION, new String[] { Messages.getString("button.OK.text") }, 0).open();
	}

	public void showInputCheckWarningMsg(String message) {
		String title = Messages.getString("updateWizard.window.title");
		if (CommonHelper.isEmpty(message)) {
			message = ErrorMsg.getString("error.default.message");
		}
		new MessageDialog(getShell(), title, null, message, MessageDialog.WARNING, new String[] { Messages.getString("button.OK.text") }, 0).open();
	}
	
	public void showInputCheckErrorMsg(String message) {
		String title = Messages.getString("updateWizard.window.title");
		if (CommonHelper.isEmpty(message)) {
			message = ErrorMsg.getString("error.default.message");
		}
		new MessageDialog(getShell(), title, null, message, MessageDialog.ERROR, new String[] { Messages.getString("button.OK.text") }, 0).open();
	}
	
	public void showValidationSuccessMessageDialog(String message){
		String title = Messages.getString("updateWizard.window.title");
		new MessageDialog(getControl().getShell(), title, null, message, MessageDialog.INFORMATION, new String[]{Messages.getString("button.OK.text")}, 0).open();
	}

	protected CommonPage(String pageName) {
		super(pageName);
	}

	protected CommonPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public UpdateWizard getWizard() {
		return (UpdateWizard) super.getWizard();
	}

	public void setNextPage(IWizardPage page) {
		this.nextPage = page;
	}

	public IWizardPage getNextPage() {
		if (this.leaving == true) {
			if (UpdateWizardInputs.fake) {
				isInputValid_fake();
				this.leaving = false;
				if (this.nextPage == null)
					return super.getNextPage();
				else
					return this.nextPage;
			}

			if (isInputValid()) {
				this.leaving = false;
				if (this.nextPage == null)
					return super.getNextPage();
				else
					return this.nextPage;
			} else {
				this.leaving = true;
				return this;
			}
		} else {
			this.leaving = true;
			if (this.nextPage == null)
				return super.getNextPage();
			else
				return this.nextPage;
		}
	}

	public IWizardPage getPreviousPage() {
		this.leaving = false;
		return super.getPreviousPage();
	}

	protected boolean isInputValid() {
		return true;
	}

	protected void isInputValid_fake() {
	}

	public void updateBeforeShown() {

	}
}
