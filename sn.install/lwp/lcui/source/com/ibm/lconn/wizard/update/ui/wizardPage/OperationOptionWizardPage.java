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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.lconn.common.LCUtil;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.update.action.FixInfoDetect;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.lconn.wizard.update.ui.UpdateWizardInputs;

public class OperationOptionWizardPage extends CommonPage {
	
	public static final Logger logger = LogUtil.getLogger(OperationOptionWizardPage.class);
	
	private Composite container;
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private Button fixesInstallButton;
	private Button fixesUninstallButton;
	private Text fixLocationText;
	private Button fixLocationButton;
	
//	private Composite fixSectionStackContainer;
//	private StackLayout stackLayout;
//	private Composite installFixComposite;
	/**
	 * Create the wizard
	 */
	public OperationOptionWizardPage() {
		super(Constants.WIZARD_PAGE_ACTION_TYPE);
		setTitle(Messages.getString("label.wizard.operation.selection.title"));
	}

	/**
	 * Create contents of the wizard
	 * 
	 * @param parent
	 */
	public void onShow(Composite parent) {
		logger.log(Level.INFO,"Enter Operation Option Page");
		container = CommonHelper.createScrollableControl(Composite.class,
				parent, SWT.BORDER, SWT.V_SCROLL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 10;
		gridLayout.horizontalSpacing = 5;
		container.setLayout(gridLayout);
		
		toolkit = new FormToolkit(container.getDisplay());
		form = toolkit.createScrolledForm(container);
		gridLayout = new GridLayout();
		form.getBody().setLayout(gridLayout);
		
		/* operation action section */
		Section operationSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		operationSection.setText(Messages.getString("label.specify.fixes.selection"));
		final Composite operationComposite = toolkit.createComposite(operationSection);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		operationComposite.setLayout(gridLayout);
		GridData gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 5;
		operationComposite.setLayoutData(gd);
		
		container.setBackground(operationComposite.getBackground());
		
/*		Label fixLabel = new Label(operationComposite, SWT.FILL);
		fixLabel.setText(Messages.getString("label.action.select"));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 1;
	
		fixLabel.setLayoutData(gd);
*/		
		this.fixesInstallButton = new Button(operationComposite, SWT.RADIO);
		this.fixesInstallButton.setBackground(operationComposite.getBackground());
		this.fixesInstallButton.setText(Messages.getString("label.action.select.install"));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fixesInstallButton.setLayoutData(gd);
		
		this.fixesUninstallButton = new Button(operationComposite, SWT.RADIO);
		this.fixesUninstallButton.setBackground(operationComposite.getBackground());
		this.fixesUninstallButton.setText(Messages.getString("label.action.select.uninstall"));
		fixesUninstallButton.setLayoutData(gd);
		
		operationSection.setClient(operationComposite);
		
//		fixSectionStackContainer = new Composite(form.getBody(), SWT.NONE);
//		stackLayout = new StackLayout();
//		fixSectionStackContainer.setLayout(stackLayout);
//		fixSectionStackContainer.setBackground(operationComposite.getBackground());
//		
//		installFixComposite = new Composite(fixSectionStackContainer, SWT.NONE);
//		installFixComposite.setBackground(operationComposite.getBackground());
		
		/*Fixes or Fix Packs Location*/
		final Section fixLocationSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		fixLocationSection.setText(Messages.getString("label.fixes.loction"));
		final Composite fixLocationContainer = toolkit.createComposite(fixLocationSection);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		fixLocationContainer.setLayout(gridLayout);
		gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 10;
		gd.horizontalIndent = 5;
		fixLocationContainer.setLayoutData(gd);
		
		Label fixLocationLabel = new Label(fixLocationContainer, SWT.FILL);
		fixLocationLabel.setText(Messages.getString("label.specify.fixes.loction"));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fixLocationLabel.setLayoutData(gd);
		
		fixLocationText = new Text(fixLocationContainer, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		gd.widthHint = 230;
		fixLocationText.setLayoutData(gd);
		
		if(UpdateWizardInputs.getFixLocation(getWizard()) == null){
			if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformShortType())){
				fixLocationText.setText(System.getProperty(LCUtil.LC_HOME) + "\\updateInstaller\\fixes");
			}else{
				fixLocationText.setText(System.getProperty(LCUtil.LC_HOME) + "/updateInstaller/fixes");
			}
					
			UpdateWizardInputs.setFixLocation(getWizard(), fixLocationText.getText().trim());
		}else{
			fixLocationText.setText(UpdateWizardInputs.getFixLocation(getWizard()));
		}
		
		fixLocationButton = new Button(fixLocationContainer, SWT.NONE);
		fixLocationButton.setText(Messages.getString("button.browse"));
		
		fixLocationSection.setClient(fixLocationContainer);
		
		if(UpdateWizardInputs.getActionId(getWizard()) == null){
			fixesInstallButton.setSelection(true);
			UpdateWizardInputs.setActionId(getWizard(), Constants.OPERATION_TYPE_INSTALL_FIXES);
		}else{
			if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
				fixesInstallButton.setSelection(true);
				fixLocationSection.setVisible(true);
			}
			if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
				fixesUninstallButton.setSelection(true);
				fixLocationSection.setVisible(false);
			}
		}
		
//		stackLayout.topControl = installFixComposite;
//		fixSectionStackContainer.layout(true, true);
		
		
		
		fixesInstallButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				fixLocationSection.setVisible(true);
				UpdateWizardInputs.setActionId(getWizard(), Constants.OPERATION_TYPE_INSTALL_FIXES);			
//				stackLayout.topControl = installFixComposite;
//				fixSectionStackContainer.layout(true, true);
			}
		});
		
		fixesUninstallButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
//				stackLayout.topControl = null;
//				fixSectionStackContainer.layout(true, true);
				fixLocationSection.setVisible(false);
				UpdateWizardInputs.setActionId(getWizard(), Constants.OPERATION_TYPE_UNINSTALL_FIXES);
			}
		});
		
		fixLocationText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				UpdateWizardInputs.setFixLocation(getWizard(), fixLocationText.getText().trim());
			}
		});
		
		fixLocationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(fixLocationContainer.getShell());
				if(fixLocationText.getText() != null){
					dialog.setFilterPath(fixLocationText.getText());
				}
				dialog.setMessage(Messages.getString("label.specify.fixes.loction"));
				String dir = dialog.open();
				if(dir != null){
					fixLocationText.setText(dir);
					UpdateWizardInputs.setFixLocation(getWizard(), fixLocationText.getText().trim());
				}
			}
		});
		
	}
	
	public boolean isInputValid() {
		
		List<EFixComponent> efixComponents = new ArrayList<EFixComponent>();
		
		if(fixesInstallButton.getSelection() == true){
			
			if(fixLocationText.getText().trim() == null || "".equals(fixLocationText.getText().trim())){
				showInputCheckWarningMsg(ErrorMsg.getString("label.enter.path"));
				return false;
			}
			
			try{
				efixComponents = FixInfoDetect.getNotInstalledFixesInfo(fixLocationText.getText().trim());
			}catch(Exception e){
				logger.log(Level.SEVERE, e.getStackTrace().toString());
			}
			
			if(efixComponents.size() == 0){
				showInputCheckWarningMsg(ErrorMsg.getString("label.unable.to.locate.images"));
				fixLocationText.setFocus();
				return false;
			}
			
			UpdateWizardInputs.setActionId(getWizard(), Constants.OPERATION_TYPE_INSTALL_FIXES);
			sortFixesById(efixComponents);
			UpdateWizardInputs.setAllFixes(getWizard(), efixComponents);
		}
		
		if(fixesUninstallButton.getSelection() == true){
			try{
				efixComponents = FixInfoDetect.getInstalledFixesInfo();
			}catch(Exception e){
				logger.log(Level.SEVERE, e.getStackTrace().toString());
			}
			
			if(efixComponents.size() == 0){
				showInputCheckWarningMsg(ErrorMsg.getString("label.unable.to.locate.uninstallable.images"));
				return false;
			}	
			
			UpdateWizardInputs.setActionId(getWizard(), Constants.OPERATION_TYPE_UNINSTALL_FIXES);
			sortFixesByInstalledTime(efixComponents);
			UpdateWizardInputs.setAllFixes(getWizard(), efixComponents);
		}
		
		return true;
	}

	/**
	 * Sort the fixes according to the installed time
	 * @param efixComponents
	 */
	private void sortFixesByInstalledTime(List<EFixComponent> efixComponents){
		for (int i = 0; i < efixComponents.size(); i++) {
			for (int j = efixComponents.size() - 1; j > i; j--) {
				EFixComponent efixComponent1 = efixComponents.get(j);
				long installedTime1 = Long.valueOf(efixComponent1.getInstalledTime());
				EFixComponent efixComponent2 = efixComponents.get(j-1);
				long installedTime2 = Long.valueOf(efixComponent2.getInstalledTime());
				if (installedTime1 > installedTime2){
					Collections.swap(efixComponents, j, j-1);
				}
			}
		}
	}
	
	/**
	 * Sort the fixes according to the fix's id
	 * @param efixComponents
	 */
	private void sortFixesById(List<EFixComponent> efixComponents){
		for (int i = 0; i < efixComponents.size(); i++) {
			for (int j = efixComponents.size() - 1; j > i; j--) {
				EFixComponent efixComponent1 = efixComponents.get(j);
				int id1 = efixComponent1.getId();
				EFixComponent efixComponent2 = efixComponents.get(j-1);
				int id2 = efixComponent2.getId();
				if (id1 > id2){
					Collections.swap(efixComponents, j, j-1);
				}
			}
		}
	}
}
