/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.update.ui.wizardPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.icu.util.Calendar;
import com.ibm.lconn.common.DateFormatUtil;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.update.action.EFixInstallAction;
import com.ibm.lconn.wizard.update.action.EFixUninstallAction;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.lconn.wizard.update.ui.UpdateWizardInputs;

public class FixInfoWizardPage extends CommonPage {
	
	public static final Logger logger = LogUtil.getLogger(FixInfoWizardPage.class);
	
	private List<String> selectedFixes = new ArrayList<String>();
	private List<String> validFixes = new ArrayList<String>();
	private List<String> installedFixes = new ArrayList<String>();
	private List<String> allFixes = new ArrayList<String>();
	
	private List<EFixComponent> efixComponents = new ArrayList<EFixComponent>();
	
	private Composite container = null;
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private int initialSize_Fixes;
	private Button[] checkboxes_FixesInstall;
	private Label[] idLabels_FixesInstall;
	private Label[] featureLabels_FixesInstall;
	private Label[] dateLabels_FixesInstall;
	private Button[] detailButtons_FixesInstall;
	private FixesDetailedDialog[] fixesDialogsInstall;
	
	private Button checkBoxAll;
	
	private String fixAction;
	private String fixLocation;
	
	private boolean reload;
	private Calendar cal = Calendar.getInstance();

	public FixInfoWizardPage(){
		super(Constants.WIZARD_PAGE_FIX_INFO);
		setTitle(Messages.getString("label.wizard.fixinfo.title"));
	}

	@Override
	public void onShow(Composite parent) {
		logger.log(Level.INFO,"Enter Fix Info Page");
		
		String fixActionTmp = UpdateWizardInputs.getActionId(getWizard());
		String fixLocationTmp = UpdateWizardInputs.getFixLocation(getWizard());
		int initialSize_FixesTmp = UpdateWizardInputs.getAllFixes(getWizard()).size();
		if (!fixActionTmp.equals(this.fixAction) || !fixLocationTmp.equals(this.fixLocation) || initialSize_FixesTmp != this.initialSize_Fixes){
			reload = true;
		}else{
			reload = false;
		}	
		this.fixAction = fixActionTmp;
		this.fixLocation = fixLocationTmp;
		this.initialSize_Fixes = initialSize_FixesTmp;
		
		efixComponents = UpdateWizardInputs.getAllFixes(getWizard());
		for(int i=0; i<initialSize_Fixes; i++){
			allFixes.add(efixComponents.get(i).getIdStr());
		}
		if (reload) {
			installedFixes = getInstalledFixes();
			validFixes = getValidFixes(installedFixes);
		}
		
		container = CommonHelper.createScrollableControl(Composite.class, parent, SWT.BORDER, SWT.V_SCROLL);

//		container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 10;
		gridLayout.horizontalSpacing = 10;
		gridLayout.marginHeight = 1;
		gridLayout.marginWidth = 1;
		container.setLayout(gridLayout);
		
		toolkit = new FormToolkit(container.getDisplay());
		form = toolkit.createScrolledForm(container);
		gridLayout = new GridLayout();
		form.getBody().setLayout(gridLayout);

		/* fixes section */
		final Section fixesSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fixesSection.setLayoutData(gd);
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			fixesSection.setText(Messages.getString("label.available.efix.install")); 
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			fixesSection.setText(Messages.getString("label.available.efix.uninstall"));
		}
		final Composite fixContainer = toolkit.createComposite(fixesSection);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		fixContainer.setLayout(gridLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fixContainer.setLayoutData(gd);
		fixesSection.setClient(fixContainer);
		
		container.setBackground(fixContainer.getBackground());
		
		Label dec = new Label(fixContainer, SWT.WRAP);
		gd = new GridData(GridData.BEGINNING);
		gd.verticalIndent = 1;
		gd.horizontalSpan = 6;
		gd.widthHint = 430;
		dec.setLayoutData(gd);
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			dec.setText(Messages.getString("label.specify.efix.install")); 
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			dec.setText(Messages.getString("label.specify.efix.uninstall"));
		}
		
		gd.verticalIndent = 3;
		gd.horizontalSpan = 6;
		checkBoxAll = new Button(fixContainer, SWT.CHECK);
		checkBoxAll.setLayoutData(gd);
		checkBoxAll.setText(Messages.getString("button.select.all"));
		
		if(reload){
			if (Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs
					.getActionId(getWizard()))) {
				checkBoxAll.setSelection(false);
			}else{
				checkBoxAll.setSelection(true);
			}
			UpdateWizardInputs.setFixesSelectAll(getWizard(),
					checkBoxAll.getSelection());
		}else{
			checkBoxAll.setSelection(UpdateWizardInputs.getFixesSelectAll(getWizard()));
		}
//		count++;
		
		Label ifixSelectedLabel = new Label(fixContainer, SWT.NONE);
/*		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			ifixSelectedLabel.setText(Messages.getString("label.column.install")); 
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			ifixSelectedLabel.setText(Messages.getString("label.column.uninstall"));
		}
*/		ifixSelectedLabel.setText("");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.verticalIndent = 10;
		gd.widthHint = 30;
		ifixSelectedLabel.setLayoutData(gd);
		Label ifixIdLabel = new Label(fixContainer, SWT.NONE);
		ifixIdLabel.setText(Messages.getString("label.column.id"));
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.verticalIndent = 10;
		gd.widthHint = 125;
		ifixIdLabel.setLayoutData(gd);
		Label ifixFeatureLabel = new Label(fixContainer, SWT.NONE);
		ifixFeatureLabel.setText(Messages.getString("label.column.feature"));
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.verticalIndent = 10;
		gd.widthHint = 125;
		ifixFeatureLabel.setLayoutData(gd);
		Label ifixdDateLabel = new Label(fixContainer, SWT.NONE);
		ifixdDateLabel.setText(Messages.getString("label.column.date"));
		ifixdDateLabel.setLayoutData(gd);
		Label detailLabel_Fixes = new Label(fixContainer, SWT.NONE);
		detailLabel_Fixes.setText(Messages.getString("label.column.details"));
		gd.verticalIndent = 10;
		gd.widthHint = 125;
		detailLabel_Fixes.setLayoutData(gd);
		
		checkboxes_FixesInstall = new Button[initialSize_Fixes];
		idLabels_FixesInstall = new Label[initialSize_Fixes];
		featureLabels_FixesInstall = new Label[initialSize_Fixes];
		dateLabels_FixesInstall = new Label[initialSize_Fixes];
		detailButtons_FixesInstall = new Button[initialSize_Fixes];
		fixesDialogsInstall = new FixesDetailedDialog[initialSize_Fixes];
		for(int i=0; i<initialSize_Fixes; i++){
			checkboxes_FixesInstall[i] = new Button(fixContainer, SWT.CHECK);
			gd = new GridData(GridData.BEGINNING);
			gd.verticalIndent = 2;
			gd.widthHint = 30;
			checkboxes_FixesInstall[i].setLayoutData(gd);
//			checkboxes_FixesInstall[i].setBackground(fixSectionComposite.getBackground());
			idLabels_FixesInstall[i] = new Label(fixContainer, SWT.NONE);
			gd = new GridData(GridData.CENTER);
			gd.verticalIndent = 6;
			gd.widthHint = 200;
			idLabels_FixesInstall[i].setLayoutData(gd);
			featureLabels_FixesInstall[i] = new Label(fixContainer, SWT.NONE);
			gd = new GridData(GridData.CENTER);
			gd.verticalIndent = 6;
			gd.widthHint = 125;
			featureLabels_FixesInstall[i].setLayoutData(gd);
			dateLabels_FixesInstall[i] = new Label(fixContainer, SWT.WRAP);
			gd = new GridData(GridData.BEGINNING);
			gd.widthHint = 140;
			dateLabels_FixesInstall[i].setLayoutData(gd);
			detailButtons_FixesInstall[i] = new Button(fixContainer, SWT.NONE);
			gd = new GridData(GridData.END);
			gd.verticalIndent = 2;
			gd.widthHint = 70;	
			detailButtons_FixesInstall[i].setLayoutData(gd);
			detailButtons_FixesInstall[i].setText(Messages.getString("label.column.details"));
			
			fixesDialogsInstall[i] = new FixesDetailedDialog(new Shell());
			
		}
		
		for(int i=0; i<initialSize_Fixes; i++){
			final int j = i;
			detailButtons_FixesInstall[i].addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent arg0) {
					
				}
				public void widgetSelected(SelectionEvent arg0) {
					fixesDialogsInstall[j].open();
				}
				
			});
		}
		
		showFixes();
		performDefaultSelect(reload);
		
		form.pack();
		setControl(container);	
		
		for(int i=0; i<initialSize_Fixes; i++){
			checkboxes_FixesInstall[i].addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent event) {
				if (event.widget == null)
					return;
				if (event.widget instanceof Button) {
					Button button = (Button)event.widget;
					boolean isChecked = button.getSelection();
					if (isChecked) {
						performSelect(CommonHelper.getWidgetID(button));
					} else {
						performUnSelect(CommonHelper.getWidgetID(button));
					}			
					UpdateWizardInputs.setFixesSelectAll(getWizard(), checkBoxAll.getSelection());
				}
			}
			
			});
		}
		
		checkBoxAll.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent event) {
				if (event.widget == null)
					return;
				if (event.widget instanceof Button) {
					Button button = (Button)event.widget;
					boolean isChecked = button.getSelection();
					if (isChecked) {
						performSelecet(validFixes);
					} else {
						performSelecet(new ArrayList());
					}			
					UpdateWizardInputs.setFixesSelectAll(getWizard(), checkBoxAll.getSelection());
				}
			}
			
		});
		
	}
	
	private void showFixes(){
		
		for(int i=0; i<initialSize_Fixes; i++){
			EFixComponent efixComponent = efixComponents.get(i);
//			if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){	
//				if("not_installed".equals(efixComponent.getInstallState())){
//					checkboxes_FixesInstall[i].setSelection(true);
//				}
//				if("installed".equals(efixComponent.getInstallState())){
//					checkboxes_FixesInstall[i].setEnabled(false);
//				}
//			}
//			
//			if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){	
//				if("not_installed".equals(efixComponent.getInstallState())){
//					checkboxes_FixesInstall[i].setSelection(false);
//				}
//				if("installed".equals(efixComponent.getInstallState())){
//					checkboxes_FixesInstall[i].setEnabled(true);
//				}
//			}
			
			CommonHelper.setWidgetID(checkboxes_FixesInstall[i], efixComponent.getIdStr());
			
			idLabels_FixesInstall[i].setText(efixComponent.getIdStr());
			featureLabels_FixesInstall[i].setText(Messages.getString(efixComponent.getInstallDescShort().substring(0, efixComponent.getInstallDescShort().indexOf(":")).toLowerCase() + ".capitalized"));
			try{
				if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
					dateLabels_FixesInstall[i].setText(DateFormatUtil
							.format(efixComponent.getInstallDate()));
				}else{
					cal.setTimeInMillis(Long.valueOf(efixComponent.getInstalledTime()));
					dateLabels_FixesInstall[i].setText(cal.getTime().toString());
				}
			}catch(Exception e){
				dateLabels_FixesInstall[i].setText(efixComponent.getInstallDate());
			}
			
			fixesDialogsInstall[i].setId(efixComponent.getIdStr());
			fixesDialogsInstall[i].setDescription(efixComponent.getInstallDescLong());
			fixesDialogsInstall[i].setPrerequisite(efixComponent.getPrereqs());
			fixesDialogsInstall[i].setAparnum(efixComponent.getAparNum());
			try{
			fixesDialogsInstall[i].setBuilddate(DateFormatUtil.format(efixComponent.getInstallDate()));
			}catch(Exception e){
				fixesDialogsInstall[i].setBuilddate(efixComponent.getInstallDate());
			}
			fixesDialogsInstall[i].setBuildversion(efixComponent.getBuildVersion());
		}	
	}
	
	private void performDefaultSelect(boolean reload) {
		disableInvalidFixes();

		if (selectedFixes == null || reload) {
			selectedFixes = null;
			if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
				performSelecet(validFixes);
			}
		} else {
			performSelecet(selectedFixes);
		}
	}
	
	private void disableInvalidFixes() {
		
		for (int i = 0; i < checkboxes_FixesInstall.length; i++) {
			if (checkboxes_FixesInstall[i] instanceof Button) {
				Button button = (Button) checkboxes_FixesInstall[i];
				String Id = CommonHelper.getWidgetID(button);
				if (!validFixes.contains(Id)) {
					button.setSelection(false);
					if (selectedFixes != null) {
						selectedFixes.remove(Id);
					}
				}
				button.setEnabled(validFixes.contains(Id));
			}
		}
	}
	
	public void performSelecet(List<String> selected) {
		if (selectedFixes == null)
			selectedFixes = new ArrayList<String>();
		Control[] children = checkboxes_FixesInstall;
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button button = (Button) children[i];
				String Id = CommonHelper.getWidgetID(button);
				if (selected.contains(Id)) {
					button.setSelection(true);
					if (!selectedFixes.contains(Id))
						selectedFixes.add(Id);
				} else {
					button.setSelection(false);
					if (selectedFixes.contains(Id))
						selectedFixes.remove(Id);
				}
			}
		}
		UpdateWizardInputs.setFixIDs(this.getWizard(), selectedFixes);
	}
	
	public void performUnSelect(String id) {
		if (selectedFixes == null)
			selectedFixes = new ArrayList<String>();
		if (selectedFixes.contains(id))
			selectedFixes.remove(id);
		UpdateWizardInputs.setFixIDs(this.getWizard(), selectedFixes);
		checkBoxAll.setSelection(false);
		UpdateWizardInputs.setFixesSelectAll(getWizard(),checkBoxAll.getSelection());
	}

	public void performSelect(String id) {
		if (selectedFixes == null)
			selectedFixes = new ArrayList<String>();
		if (!selectedFixes.contains(id))
			selectedFixes.add(id);
		UpdateWizardInputs.setFixIDs(this.getWizard(), selectedFixes);
	}
	
	private ArrayList<String> getInstalledFixes() {
		ArrayList<String> installedFixes = new ArrayList<String>();
		for(int i=0; i<initialSize_Fixes; i++){
			EFixComponent efixComponent = efixComponents.get(i);
			if("installed".equals(efixComponent.getInstallState())){
				installedFixes.add(efixComponent.getIdStr());
			}else if("partially_installed".equals(efixComponent.getInstallState())){
				installedFixes.add(efixComponent.getIdStr());
			}
		}

		return installedFixes;
	}
	
	private ArrayList<String> getValidFixes(List<String> installed) {
		ArrayList<String> validFixes = new ArrayList<String>();
		boolean isInstall = Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()));
		if (!isInstall && installed != null) {
			for (int i = 0; i < installed.size(); i++)
				validFixes.add(installed.get(i));
		}

		if (isInstall) {
			for (int i = 0; i < allFixes.size(); i++)
				validFixes.add(allFixes.get(i));

			for (int i = 0; i < installed.size(); i++)
				validFixes.remove(installed.get(i));
		}

		return validFixes;
	}
	
	public boolean isInputValid() {
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){	
			EFixInstallAction eFixInstallAction = new EFixInstallAction();
			
			List<EFixComponent> selectedEfixComponents = new ArrayList<EFixComponent>();
			for(int i=0; i<initialSize_Fixes; i++){
				EFixComponent efixComponent = efixComponents.get(i);
				for(String efixID : selectedFixes){
					if(efixID.equals(efixComponent.getIdStr())){
						selectedEfixComponents.add(efixComponent);
					}
				}	
			}
	
			if(selectedEfixComponents.size() <=0 ){
				showInputCheckWarningMsg(ErrorMsg.getString("label.specify.efixes.install.check"));
				return false;
			}
			
			try{
				eFixInstallAction.checkInstallPrereqs(selectedEfixComponents);
			}catch(Exception e){
				logger.log(Level.SEVERE, e.getStackTrace().toString());
			}
			
			Vector<String> errors = eFixInstallAction.getErrors();			
			logger.log(Level.WARNING,"INSTALL::  Tha selected EFixComponent has errors: " + errors);	
			if(errors.size() != 0){
				String errorMsg = "";
				for(Object errorObj : errors){
					errorMsg += (String) errorObj + Constants.UI_LINE_SEPARATOR;
				}
				showInputCheckErrorMsg(errorMsg);
				return false;
			}
			
			Vector<String> supersededInfo = eFixInstallAction.getSupersededInfo();
			if(supersededInfo.size() != 0){
				String supersededInfoMsg = "";
				for(Object supersededInfoObj : supersededInfo){
					supersededInfoMsg += (String) supersededInfoObj + Constants.UI_LINE_SEPARATOR;
				}
				showInputCheckWarningMsg(supersededInfoMsg);
				return false;
			}
			
			selectedEfixComponents = eFixInstallAction.filterWithSuperseding(selectedEfixComponents);
	
			UpdateWizardInputs.setSelectedFixes(getWizard(), selectedEfixComponents);
			
			logger.log(Level.INFO,"INSTALL::  Selected EFixComponent are " + selectedEfixComponents);
			logger.log(Level.INFO,"INSTALL::  Selected EFixComponent size is " + selectedEfixComponents.size());
	
			
			logger.log(Level.INFO,"INSTALL::  Selected EFixImages are " + eFixInstallAction.getInstallOrder());
			UpdateWizardInputs.setEFixImages(getWizard(), eFixInstallAction.getInstallOrder());
		}
		
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			EFixUninstallAction eFixUninstallAction = new EFixUninstallAction();
			
			List<EFixComponent> selectedEfixComponents = new ArrayList<EFixComponent>();
			for(int i=0; i<initialSize_Fixes; i++){
				EFixComponent efixComponent = efixComponents.get(i);
				for(String efixID : selectedFixes){
					if(efixID.equals(efixComponent.getIdStr())){
						selectedEfixComponents.add(efixComponent);
					}
				}	
			}
			
			UpdateWizardInputs.setSelectedFixes(getWizard(), selectedEfixComponents);	
			logger.log(Level.INFO,"UNINSTALL::  Selected EFixComponent are " + selectedEfixComponents);
			logger.log(Level.INFO,"UNINSTALL::  Selected EFixComponent size is " + selectedEfixComponents.size());		
			if(selectedEfixComponents.size() <=0 ){
				showInputCheckWarningMsg(ErrorMsg.getString("label.specify.efixes.uninstall.check"));
				return false;
			}
			
			eFixUninstallAction.checkUninstallPrereq(selectedEfixComponents);
			
			Vector<String> errors = eFixUninstallAction.getErrors();
			logger.log(Level.WARNING,"UNINSTALL::  Tha selected EFixComponent has errors: " + errors);
			if(errors.size() != 0){
				String errorMsg = "";
				for(Object errorObj : errors){
					errorMsg += (String) errorObj + Constants.UI_LINE_SEPARATOR;
				}
				showInputCheckWarningMsg(errorMsg);
				return false;
			}
			UpdateWizardInputs.setFixIDsForUnstall(getWizard(), eFixUninstallAction.getUninstallOrder());
		}
		
		BackupCustomWarningDialog backupCustomWarningDialog = new BackupCustomWarningDialog(getShell());
		boolean status = backupCustomWarningDialog.open()== 0;
		logger.log(Level.INFO, "Backup customization selection: " + backupCustomWarningDialog.getSelectedOption());
		
		return status;
	}
	
	/* BackupCustomWarningDialog to warn customers to backup their customisation before continue */
	public class BackupCustomWarningDialog extends Dialog{
		
		Button noChangeButton;
		Button backedupButton;
		String featureCustomBackedUp;

		protected BackupCustomWarningDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			GridLayout gridLayout = new GridLayout();
//			gridLayout.numColumns = 4;
			gridLayout.marginRight = 20;
			gridLayout.marginLeft = 20;
			parent.setLayout(gridLayout);
			
			Label warningMsg = new Label(parent, SWT.WRAP);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.widthHint = 600;
			warningMsg.setLayoutData(gd);
			if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){	
				warningMsg.setText(Messages.getString("label.backup.custom.warning.msg", Messages.getString("label.backup.custom.warning.install")));
			}else{
				warningMsg.setText(Messages.getString("label.backup.custom.warning.msg", Messages.getString("label.backup.custom.warning.uninstall")));
			}
			
			noChangeButton = new Button(parent, SWT.RADIO);
			noChangeButton.setText(Messages.getString("label.backup.custom.warning.nochange"));
			noChangeButton.setSelection(true);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			noChangeButton.setLayoutData(gd);
			
			backedupButton = new Button(parent, SWT.RADIO);
			backedupButton.setText(Messages.getString("label.backup.custom.warning.backedup"));
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//			gd.verticalIndent = 10;
			gd.horizontalIndent = 5;
			backedupButton.setLayoutData(gd);
			
			return null;
		}
		@Override
		protected Button createButton(Composite parent, int id, String label,
				boolean defaultButton) {
			return null;
		}

		@Override
		protected void initializeBounds() {
			Composite comp = (Composite) super.getButtonBar();
			super.createButton(comp, IDialogConstants.OK_ID, Messages.getString("button.OK.text"), true);
			super.initializeBounds();
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			// Dialog Title
			newShell.setText(Messages.getString("label.backup.custom.warning.title"));
		}
		
		@Override
		protected void okPressed() {
			if (noChangeButton.getSelection()){
				featureCustomBackedUp = noChangeButton.getText();
			}else{
				featureCustomBackedUp = backedupButton.getText();
			}
			super.okPressed();
		}

		protected String getSelectedOption(){
			return featureCustomBackedUp;
		}
	}
	
	
	/* Fixes detailed dialog */
	public class FixesDetailedDialog extends Dialog{
		private String id;
		private String description;
		private String prerequisite;
		private String aparnum;
		private String builddate;
		private String buildversion;		

//		public FixDetailedDialog(String id, String description,
//				String prerequisite, String aparnumber, String builddate,
//				String buildversion, Shell parentShell) {
//			this.id = id;
//			this.description = description;
//			this.prerequisite = prerequisite;
//			this.aparnum = aparnum;
//			this.builddate = builddate;
//			this.buildversion = buildversion;
//			super(parentShell);
//		}
		
		protected FixesDetailedDialog(Shell parentShell) {
			super(parentShell);
		}

		protected Control createDialogArea(Composite parent) {
			GridLayout gridLayout = new GridLayout();
//			gridLayout.numColumns = 4;
			gridLayout.marginRight = 20;
			gridLayout.marginLeft = 20;
			parent.setLayout(gridLayout);
			/* fixes ID */
			Label fixesIDLabel = new Label(parent, SWT.NONE);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 200;
			fixesIDLabel.setLayoutData(gd);
			fixesIDLabel.setText(Messages.getString("label.specify.id_1") + " " + id);
			/* fiex description */
			Label descriptionLabel = new Label(parent, SWT.NONE);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 200;
			descriptionLabel.setLayoutData(gd);
			descriptionLabel.setText(Messages.getString("label.specify.detailed.description") + " ");	
			Text descriptionText = new Text(parent, SWT.MULTI);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 450;
			gd.heightHint = 100;
			descriptionText.setLayoutData(gd);
			descriptionText.setEditable(false);
			if(Constants.OS_AIX.equals(CommonHelper.getPlatformType())){
				descriptionText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
			}else{
			descriptionText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			}	
			descriptionText.setText(description);
			/* fixes prerequisite */
			Label prerequisiteLabel = new Label(parent, SWT.NONE);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 200;
			prerequisiteLabel.setLayoutData(gd);
			prerequisiteLabel.setText(Messages.getString("label.specify.prerequisites") + " ");
			Text prerequisiteText = new Text(parent, SWT.MULTI);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 450;
			gd.heightHint = 70;
			prerequisiteText.setLayoutData(gd);
			prerequisiteText.setEditable(false);
			if(Constants.OS_AIX.equals(CommonHelper.getPlatformType())){
				prerequisiteText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
			}else{
			prerequisiteText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			}
			prerequisiteText.setText(prerequisite);
			/* fixes APAR Number */
			Label aparLabel = new Label(parent, SWT.NONE);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 300;
			aparLabel.setLayoutData(gd);
			aparLabel.setText(Messages.getString("label.specify.apar.number") + " " + aparnum);
			/* fixes build date */
			Label buildDateLabel = new Label(parent, SWT.NONE);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 200;
			buildDateLabel.setLayoutData(gd);
			buildDateLabel.setText(Messages.getString("label.specify.build.date") + " " + builddate);
			/* fixes build version */
			Label buildVersionLabel = new Label(parent, SWT.NONE);
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = 10;
//			gd.horizontalIndent = 20;
			gd.widthHint = 200;
			buildVersionLabel.setLayoutData(gd);
			buildVersionLabel.setText(Messages.getString("label.specify.build.version") + buildversion);
			
			return null;
		}
		
		@Override
		protected Button createButton(Composite parent, int id, String label,
				boolean defaultButton) {
			return null;
		}
		
		protected void configureShell(Shell newShell) {

			super.configureShell(newShell);
			// Dialog Title
			newShell.setText(Messages.getString("label.efix.detail.title"));

		}
		@Override
		protected void initializeBounds(){
			Composite comp = (Composite) super.getButtonBar();
			super.createButton(comp, IDialogConstants.OK_ID, Messages.getString("button.OK.text"), true);
			super.initializeBounds();
		}	

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getPrerequisite() {
			return prerequisite;
		}

		public void setPrerequisite(String prerequisite) {
			this.prerequisite = prerequisite;
		}

		public String getAparnum() {
			return aparnum;
		}

		public void setAparnum(String aparnum) {
			this.aparnum = aparnum;
		}

		public String getBuilddate() {
			return builddate;
		}

		public void setBuilddate(String builddate) {
			this.builddate = builddate;
		}

		public String getBuildversion() {
			return buildversion;
		}

		public void setBuildversion(String buildversion) {
			this.buildversion = buildversion;
		}
				
	}

}
