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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class LCDialog extends Dialog {

	private WizardPageData data;

	/**
	 * @param parentShell
	 */
	public LCDialog(Shell parentShell) {
		super(parentShell);
	}

	public LCDialog(Shell pareShell, WizardPageData pageData) {
		super(pareShell);
		this.data = pageData;
	}

	/**
	 * Create contents of the dialog
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setImage(ResourcePool.getWizardTitleIcon());
		if (this.data != null) {
			Composite rootPanel = new Composite(parent, SWT.NONE);
			Label descLabel = new Label(rootPanel, SWT.NONE);
			if (this.data != null)
				descLabel.setText(data.getDesc());
			new Label(rootPanel, SWT.NONE).setText("\n");
			rootPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			rootPanel.setLayout(new GridLayout());
			DefaultWizardDataLoader.createWidget(rootPanel, this.data
					.getWizardId(), data.getInputsId());
		}
		return super.createDialogArea(parent);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (this.data != null)
			newShell.setText(data.getTitle()); //$NON-NLS-1$
	}

	/**
	 * @param parentShell
	 */
	public LCDialog(IShellProvider parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	public WizardPageData getData() {
		return data;
	}

}
