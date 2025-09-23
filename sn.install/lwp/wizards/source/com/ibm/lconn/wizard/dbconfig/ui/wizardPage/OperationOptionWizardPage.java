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
package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Button;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ValuePool;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.GroupUtil;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.dbconfig.ui.event.listener.OperationOptionPageListener;
import com.ibm.lconn.wizard.dbconfig.ui.event.listener.SummaryWizardPageCheckBoxListener;

public class OperationOptionWizardPage extends CommonPage {
	private Group group;
//	private static Button exportSQLButton;
	private Button exportSQLButton;
	private int count = 0;
	

	public static final Logger logger = LogUtil.getLogger(OperationOptionWizardPage.class);
	/**
	 * Create the wizard
	 */
	public OperationOptionWizardPage() {
		super(Constants.WIZARD_PAGE_DB_ACTION_TYPE);
		setTitle(Messages.getString("dbWizard.operationOptionWizardPage.title")); //$NON-NLS-1$
	}

	/**
	 * Create contents of the wizard
	 * 
	 * @param parent
	 */
	public void onShow(Composite parent) {
		Composite container = CommonHelper.createHVScrollableControl(Composite.class, parent, SWT.NONE, SWT.H_SCROLL | SWT.V_SCROLL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		container.setLayout(gridLayout);

		Label dec = new Label(container, SWT.WRAP);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dec.setText(Messages.getString("dbWizard.operationOptionWizardPage.description")); //$NON-NLS-1$

		group = GroupUtil.createRadioGroup(new String[] { Constants.OPERATION_TYPE_CREATE_DB, Constants.OPERATION_TYPE_DROP_DB, Constants.OPERATION_TYPE_UPGRADE_DB }, container, SWT.NONE, SWT.NONE, new OperationOptionPageListener(this), new ValuePool() {
			public String getString(String key) {
				return Messages.getString("dbWizard.action.db." + key + ".label"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		String actionName = DBWizardInputs.getActionId(this.getWizard());
		boolean mustSelectOne = true;
		GroupUtil.setSelectedButton(group, actionName, mustSelectOne, mustSelectOne, mustSelectOne);
		//Export SQL commands list only
		exportSQLButton = new Button(container, SWT.CHECK);
		exportSQLButton.setText(Messages.getString("dbWizard.operationOptionWizardPage.ExportSQL.button"));
		exportSQLButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		exportSQLButton.addSelectionListener(new OperationOptionPageListener(this));
		
		exportSQLButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(exportSQLButton.getSelection()){
					new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.operationOptionWizardPage.ExportSQL.button.warning"), MessageDialog.INFORMATION, new String[] { Messages.getString("button.OK.text") }, 0).open();
					(GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).setEnabled(false);
					//while export cmd list is check, disable the update button
					(GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).setSelection(false);
				}
			}
		});			
		
			
		if(count==0){
			exportSQLButton.setSelection(false);
			DBWizardInputs.setExportOnly(this.getWizard(),exportSQLButton.getSelection());
		}else{
			exportSQLButton.setSelection(DBWizardInputs.isExportOnly(this.getWizard()));
		}        
        count++;
	 
	}

	public void updateBeforeShown() {
		String id = GroupUtil.getButtonId(GroupUtil.getSelectedButton(group, true));
		DBWizardInputs.setActionId(this.getWizard(), id);
		DBWizardInputs.setExportOnly(getWizard(),exportSQLButton.getSelection());
		(GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).setEnabled(!(DBWizardInputs.isExportOnly(this.getWizard())));
		if((GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).getSelection())
		{
			DBWizardInputs.setExportOnly(getWizard(),false);
			exportSQLButton.setEnabled(false);
		}else{
			exportSQLButton.setEnabled(true);
		}
	}

	public void performSelection(Widget widget) {
		if (widget != null) {
		//	String id = CommonHelper.getWidgetID(widget);
			String id = GroupUtil.getButtonId(GroupUtil.getSelectedButton(group, true));
			performSelection(id);
			
		//	Object obj = widget;
		//	if(obj instanceof Button){
		//		Button button = (Button) obj;
					DBWizardInputs.setExportOnly(getWizard(),exportSQLButton.getSelection());
					(GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).setEnabled(!(DBWizardInputs.isExportOnly(this.getWizard())));
					
					if((GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).getSelection())
					{
						DBWizardInputs.setExportOnly(getWizard(),false);
						exportSQLButton.setEnabled(false);
					}else{
						exportSQLButton.setEnabled(true);
					}
		      }
		     
	      }


	private void performSelection(String id) {
		DBWizardInputs.setActionId(this.getWizard(), id);
	}

	public IWizardPage getPreviousPage() {
		(GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).setEnabled(!(DBWizardInputs.isExportOnly(this.getWizard())));
		if((GroupUtil.getGroupButton(group, Constants.OPERATION_TYPE_UPGRADE_DB)).getSelection())
		{
			DBWizardInputs.setExportOnly(getWizard(),false);
			exportSQLButton.setEnabled(false);
		}else{
			exportSQLButton.setEnabled(true);
		}
		return Pages.getPage(Pages.WELCOME_PAGE);
	}
}
