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

package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;

public class ConstentStoreMigratePanel extends CommonPage {

	private Text forumInput;

	public ConstentStoreMigratePanel() {
		super(Constants.WIZARD_PAGE_CONTENT_STORE_MIGRATE);
		setTitle(Messages.getString("dbWizard.ConstentStoreMigratePanel.title")); //$NON-NLS-1$
	}

	@Override
	public void onShow(Composite parent) {
		Composite container = CommonHelper.createScrollableControl(Composite.class, parent, SWT.NONE, SWT.V_SCROLL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;

		container.setLayout(gridLayout);

		Label dec = new Label(container, SWT.WRAP);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		dec.setText(Messages.getString("dbWizard.ConstentStoreMigratePanel.description")); //$NON-NLS-1$

		final Label forumLabel = new Label(container, SWT.NONE);
		forumLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		forumLabel.setText(Messages.getString("dbWizard.ConstentStoreMigratePanel.forumContentPath.label"));

		forumInput = new Text(container, SWT.BORDER);
		forumInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button browseButton = new Button(container, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
				if (forumInput.getText() != null)
					directoryDialog.setFilterPath(forumInput.getText());
				directoryDialog.setMessage(Messages.getString("dbWizard.dbTypeSelectionWizardPage.dirSelection.message")); //$NON-NLS-1$

				String dir = directoryDialog.open();
				if (dir != null) {
					forumInput.setText(dir);
				}
			}
		});
		browseButton.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.button.browse")); //$NON-NLS-1$

	}

	public IWizardPage getNextPage() {
		BusyIndicator.showWhile(getWizard().getShell().getDisplay(), new Runnable() {
			public void run() {
				saveInputs();
			}
		});
		this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
		return super.getNextPage();
	}

	public IWizardPage getPreviousPage() {
		saveInputs();
		return super.getPreviousPage();
	}

	private void saveInputs() {
		DBWizardInputs.setForumContentStorePath(this.getWizard(), forumInput.getText());
	}

	public boolean isInputValid() {
		String contentStore = DBWizardInputs.getForumContentStorePath(this.getWizard());
		File path = new File(contentStore);
		if (path.exists() && path.isDirectory()) {
			boolean status = new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.ConstentStoreMigratePanel.question.message"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
			return status;
		} else {
			this.showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
			forumInput.setFocus();
			return false;
		}
	}

	public void isInputValid_fake() {
		this.showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
		new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.ConstentStoreMigratePanel.question.message"), MessageDialog.WARNING, new String[] {Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open();
	}
}
