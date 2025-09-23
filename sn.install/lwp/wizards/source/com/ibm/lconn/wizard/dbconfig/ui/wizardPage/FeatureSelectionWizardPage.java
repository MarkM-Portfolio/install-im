/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ValuePool;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.GroupUtil;
import com.ibm.lconn.wizard.dbconfig.backend.Checker;
import com.ibm.lconn.wizard.dbconfig.backend.SchemaVersionDetector;
import com.ibm.lconn.wizard.dbconfig.backend.UpgradePath;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.dbconfig.ui.event.listener.FeatureSelectionPageCheckboxListener;
//hz need to remove
import java.util.logging.Level;
import java.util.logging.Logger;
import com.ibm.lconn.wizard.common.logging.LogUtil;

public class FeatureSelectionWizardPage extends CommonPage {
	private List<String> selectedFeatures = null;
	private List<String> validFeatures = null;
	private List<String> installedFeatures = null;
	private String[] features = Constants.FEATURE_ALL;
	private Group checkBoxGroup;
	private String action;
	private String dbType;
	private String instance;
	private String location;
	private boolean reload = false;
	private Button checkBoxAll;
	private static final Logger logger = LogUtil.getLogger(FeatureSelectionWizardPage.class);

	/**
	 * Create the wizard
	 */
	public FeatureSelectionWizardPage() {
		super(Constants.WIZARD_PAGE_FEATURE_SELECTION);
		setTitle(Messages.getString("dbWizard.featureSelectionWizardPage.title")); //$NON-NLS-1$
	}

	/**
	 * Create contents of the wizard
	 * 
	 * @param parent
	 */
	public void onShow(Composite parent) {
		// Create Layout and components.
		Composite container = parent;
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		container.setLayout(gridLayout);

		// Put the core component that will contain the features selection.
		Composite composite = CommonHelper.createScrollableControl(Composite.class, container, SWT.NONE, SWT.V_SCROLL);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout compLayout = new GridLayout();
		composite.setLayout(compLayout);

		// Put the panel description into the layout.
		Label dec = new Label(composite, SWT.WRAP);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		dec.setText(Messages.getString("dbWizard.featureSelectionWizardPage.description." + DBWizardInputs.getActionId(this.getWizard()))); //$NON-NLS-1$

		// Get the last panel inputs from session(DBWizardInputs).
		String actionId = DBWizardInputs.getActionId(this.getWizard());
		String dbTypeId = DBWizardInputs.getDbType(this.getWizard());
		String dbInstance = DBWizardInputs.getDbInstanceName(this.getWizard());
		String dbLocation = DBWizardInputs.getDbInstallDir(this.getWizard());

		// First come to this page, we need to reload.
//		if (count == 0)
//			reload = true;
//		else
//			reload = false;
		
		// Record the operation. When user back to this page, if the operation
		// is as before, we don't reload the data. If the inputs changes from
		// the beginning, We need to reload the data.
		// First come to this page, we need to reload.
		if ((!actionId.equals(this.action) || !dbTypeId.equals(this.dbType) || !dbInstance.equals(this.instance) || !dbLocation.equals(this.location))){
			reload = true;
		}else{
			reload = false;
		}
		
		this.dbType = dbTypeId;
		this.action = actionId;
		this.instance = dbInstance;
		this.location = dbLocation;			
		// Create, delete, update database?
		boolean isCreation = Constants.OPERATION_TYPE_CREATE_DB.equals(actionId);

		if (reload) {
			if (((DBWizardInputs.fake) ) || (DBWizardInputs.isExportOnly(this.getWizard()))){
				this.validFeatures = this.getValidFeatures_fake(isCreation, this.installedFeatures);
				this.installedFeatures = this.validFeatures;
			} else {
				this.installedFeatures = this.getInstalledFeatures();
				this.validFeatures = this.getValidFeatures(isCreation, this.installedFeatures);

			}
		}

		// For upgrade database.
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			this.checkBoxGroup = new Group(composite, SWT.CHECK);
			this.checkBoxGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 3;
			this.checkBoxGroup.setLayout(groupLayout);
			Map<String, String> versions;
			if (this.reload) {
//#Oracle12C_PDB_disable#			versions = SchemaVersionDetector.getFeatureVersions(DBWizardInputs.getDbType(this.getWizard()), DBWizardInputs.getDbInstallDir(this.getWizard()), DBWizardInputs.getDbInstanceName(this.getWizard()),DBWizardInputs.getPDBNameValue(this.getWizard()),DBWizardInputs.getDbaPasswordValue(this.getWizard()), DBWizardInputs.getDbVersion(this.getWizard()),(String[]) installedFeatures.toArray(new String[0]));
			versions = SchemaVersionDetector.getFeatureVersions(DBWizardInputs.getDbType(this.getWizard()), DBWizardInputs.getDbInstallDir(this.getWizard()), DBWizardInputs.getDbInstanceName(this.getWizard()),DBWizardInputs.getDbVersion(this.getWizard()),(String[]) installedFeatures.toArray(new String[0]));
				DBWizardInputs.setDbUpgradeVersion(this.getWizard(), versions);
			} else {
				versions = DBWizardInputs.getDbUpgradeVersion(this.getWizard());
			}

			GridData griddata = new GridData(SWT.LEFT, SWT.FILL, false, true);
			griddata.widthHint = 200;
			griddata.heightHint = 25;

			boolean wrongUser = false;
			if (0 == installedFeatures.size()) {
				new Label(this.checkBoxGroup, SWT.NONE).setText(Messages.getString("dbWizard.featureSelectionWizardPage.no.install"));
			} else {
				Label label = new Label(this.checkBoxGroup, SWT.NONE);
				label.setText("  " + Messages.getString("dbWizard.featureSelectionWizardPage.upgrade.select.feature"));
				label.setLayoutData(griddata);

				label = new Label(this.checkBoxGroup, SWT.NONE);
				label.setText(Messages.getString("dbWizard.featureSelectionWizardPage.upgrade.current.version"));
				label.setLayoutData(griddata);

				label = new Label(this.checkBoxGroup, SWT.NONE);
				label.setText(Messages.getString("dbWizard.featureSelectionWizardPage.upgrade.top.version"));
				label.setLayoutData(griddata);

				for (int i = 0; i < installedFeatures.size(); i++) {
					String feature = installedFeatures.get(i);
					String version;
					if (DBWizardInputs.fake)
						version = Constants.VERSION_201;
					else
						version = versions.get(feature);
					
					// Not all of the databases has schema changed in the latest release, for
					// those features which has not changed since certain release, we shall
					// consider they are up-to-date already, and do not show them
					if (version.equals("6.0CR1")) {
						if (feature.equals(Constants.FEATURE_ACTIVITIES)) {
							continue;
						}
					} else if (version.equals("6.0CR4")) {
						if (feature.equals(Constants.FEATURE_COMMUNITIES) ||
							feature.equals(Constants.FEATURE_HOMEPAGE) ||
							feature.equals(Constants.FEATURE_METRICS) ) {
								continue;
							}
					} else if (version.equals("6.0CR5")) {
						if (feature.equals(Constants.FEATURE_FILES) ||
							feature.equals(Constants.FEATURE_WIKIS)) {
								continue;
						}
					} else if (version.equals("6.0.0") || version.equals(Constants.VERSION_TOP)) {
						continue;
					}

					Button featureButton = new Button(this.checkBoxGroup, SWT.CHECK);
					String buttonText = Messages.getString(feature + ".name");
					
					//TODO modify by Maxi, getString(String str) in Messages return "" if there is no such property!
					if (buttonText == null || buttonText.equals(""))
						buttonText = feature;
					//TODO end!
					
					featureButton.setText(buttonText);
					featureButton.addSelectionListener(new FeatureSelectionPageCheckboxListener(this));
					CommonHelper.setWidgetID(featureButton, feature);
					featureButton.setLayoutData(griddata);


					label = new Label(this.checkBoxGroup, SWT.NONE);
					
					//TODO modify by Maxi, refact the following code!
					if (null == version) {
						version = "null";
						wrongUser = true;
					} else if (version.startsWith("1.0")) {
						version = Constants.VERSION_10X;
					} else if (version.equals(Constants.VERSION_250) || version.equals(Constants.VERSION_201)) {
						wrongUser = true;
					}
					//TODO end!
					
					label.setText(version);
					label.setLayoutData(griddata);

					label = new Label(this.checkBoxGroup, SWT.NONE);
					if (feature.equals(Constants.FEATURE_COGNOS)){
						label.setText(Constants.FEATURE_COGNOS);
					}else{
						label.setText(Constants.VERSION_TOP);
					}
					label.setLayoutData(griddata);

					if (CommonHelper.equals(version, Constants.VERSION_TOP)){
						featureButton.setEnabled(false);
						this.validFeatures.remove(feature);
					}
					/*
					if (feature.equals(Constants.FEATURE_HOMEPAGE) && 
							!CommonHelper.equals(version, Constants.VERSION_40CR2) &&
							!CommonHelper.equals(version, Constants.VERSION_400)){
						featureButton.setEnabled(false);
						this.validFeatures.remove(feature);
					}else if(feature.equals(Constants.FEATURE_METRICS) && 
							!CommonHelper.equals(version, Constants.VERSION_40CR3) &&
							!CommonHelper.equals(version, Constants.VERSION_400)){
						featureButton.setEnabled(false);
						this.validFeatures.remove(feature);
					}else if (!feature.equals(Constants.FEATURE_HOMEPAGE) && !feature.equals(Constants.FEATURE_METRICS) && !CommonHelper.equals(version, Constants.VERSION_400)) {
						featureButton.setEnabled(false);
						this.validFeatures.remove(feature);
					}
					*/
				}
				
			}
			if (wrongUser){
				boolean status = new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.featureSelectionWizardPage.wronguser"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text") }, 0).open() == 0;
			}
		} else {
			this.checkBoxGroup = GroupUtil.createCheckGroup(this.features, composite, SWT.NONE, SWT.NONE, new FeatureSelectionPageCheckboxListener(this), new ValuePool() {
				// for UI display.
				public String getString(String key) {
					return Messages.getString(key + ".name");
				}
			});
			this.checkBoxGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		}
		checkBoxAll = new Button(composite,SWT.CHECK);
		checkBoxAll.setText(Messages.getString("button.selectall.text"));
		GridData griddata = new GridData(SWT.LEFT, SWT.TOP, false, true);
		griddata.horizontalIndent = 8;
		griddata.verticalIndent = 8;
		checkBoxAll.setLayoutData(griddata);
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
						performSelecet(getValidFeatures());
					} else {
						performSelecet(new ArrayList());
					}			
					DBWizardInputs.setFeaturesSelectAll(getWizard(),checkBoxAll.getSelection());
				}
			}
			
		});
		
		if(reload){
			checkBoxAll.setSelection(true);
			DBWizardInputs.setFeaturesSelectAll(getWizard(),checkBoxAll.getSelection());
		}else{
			checkBoxAll.setSelection(DBWizardInputs.getFeaturesSelectAll(getWizard()));
		}
		
		performDefaultSelect(reload);
		
//		System.out.println("Count1: " + count);
//		count++;
//		System.out.println("Count2: " + count);
	}

	private void performDefaultSelect(boolean reload) {
		disableInvalidFeatures();

		if (this.selectedFeatures == null || reload) {
			this.selectedFeatures = null;
			performSelecet(this.validFeatures);
		} else {
			performSelecet(this.selectedFeatures);
		}
	}

	private void disableInvalidFeatures() {
		Control[] children = checkBoxGroup.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button button = (Button) children[i];
				String Id = CommonHelper.getWidgetID(button);
				//TODO modify by Maxi, special action of library
				if (Constants.FEATURE_LIBRARY.equals(Id)) {
					if (!this.validFeatures.contains(Constants.FEATURE_LIBRARY_GCD) && !this.validFeatures.contains(Constants.FEATURE_LIBRARY_OS)) {
						button.setSelection(false);
						if (this.selectedFeatures != null) {
							this.selectedFeatures.remove(Constants.FEATURE_LIBRARY_GCD);
							this.selectedFeatures.remove(Constants.FEATURE_LIBRARY_OS);
						}
					}
					button.setEnabled(this.validFeatures.contains(Constants.FEATURE_LIBRARY_GCD) && this.validFeatures.contains(Constants.FEATURE_LIBRARY_OS));
					continue;
				}
				//TODO end
				if (!this.validFeatures.contains(Id)) {
					button.setSelection(false);
					if (this.selectedFeatures != null) {
						this.selectedFeatures.remove(Id);
					}
				}
				button.setEnabled(this.validFeatures.contains(Id));
			}
		}
		if(validFeatures.size() == 0){
			checkBoxAll.setEnabled(false);
			checkBoxAll.setSelection(false);
		}
	}

	public void performSelecet(List<String> selected) {
		if (this.selectedFeatures == null)
			this.selectedFeatures = new ArrayList<String>();
		Control[] children = checkBoxGroup.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button button = (Button) children[i];
				String Id = CommonHelper.getWidgetID(button);
				//TODO modify by Maxi, special action of library
				if (Constants.FEATURE_LIBRARY.equals(Id)) {
					if (selected.contains(Constants.FEATURE_LIBRARY_GCD) || selected.contains(Constants.FEATURE_LIBRARY_OS)) {
						button.setSelection(true);
						if (!selectedFeatures.contains(Constants.FEATURE_LIBRARY_GCD))
							this.selectedFeatures.add(Constants.FEATURE_LIBRARY_GCD);
						if (!selectedFeatures.contains(Constants.FEATURE_LIBRARY_OS))
							this.selectedFeatures.add(Constants.FEATURE_LIBRARY_OS);
					} else {
						button.setSelection(false);
						if (selectedFeatures.contains(Constants.FEATURE_LIBRARY_GCD))
							this.selectedFeatures.remove(Constants.FEATURE_LIBRARY_GCD);
						if (selectedFeatures.contains(Constants.FEATURE_LIBRARY_OS))
							this.selectedFeatures.remove(Constants.FEATURE_LIBRARY_OS);
					}
					continue;
				}
				//TODO end
				if (selected.contains(Id)) {
					button.setSelection(true);
					if (!this.selectedFeatures.contains(Id))
						this.selectedFeatures.add(Id);
				} else {
					button.setSelection(false);
					if (this.selectedFeatures.contains(Id))
						this.selectedFeatures.remove(Id);
				}
			}
		}
		DBWizardInputs.setFeatures(this.getWizard(), this.selectedFeatures);
	}

	private ArrayList<String> getInstalledFeatures() {
//#Oracle12C_PDB_disable#		Checker dbCheck = Checker.getChecker(DBWizardInputs.getDbInstallDir(this.getWizard()), CommonHelper.getPlatformType(), DBWizardInputs.getDbType(this.getWizard()),DBWizardInputs.getPDBNameValue(this.getWizard()),DBWizardInputs.getDbaPasswordValue(this.getWizard()));
		Checker dbCheck = Checker.getChecker(DBWizardInputs.getDbInstallDir(this.getWizard()), CommonHelper.getPlatformType(), DBWizardInputs.getDbType(this.getWizard()));
		dbCheck.setInstance(DBWizardInputs.getDbInstanceName(this.getWizard()));
		String[] installedFeaturesArr = dbCheck.getFeatures(DBWizardInputs.getDbVersion(this.getWizard()));

		ArrayList<String> features = new ArrayList<String>();
		for (String feature : installedFeaturesArr) {
			features.add(feature);
		}
		return features;
	}

	private ArrayList<String> getValidFeatures_fake(boolean isCreation, List<String> installed) {
		ArrayList<String> features = new ArrayList<String>();
		
		/*   The old code Not sure why it's only 6 or 8 features. change to Constants.FEATURE_ALL.length modify by Shaoli
		 * 
		features.add(this.features[0]);
		features.add(this.features[1]);
		features.add(this.features[2]);
		features.add(this.features[3]);
		features.add(this.features[4]);
		features.add(this.features[5]);
		if (!Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.action)) {
			features.add(this.features[6]);
			features.add(this.features[7]);
		} */
		
		for(int i=0;i<Constants.FEATURE_ALL.length;i++){
			features.add(Constants.FEATURE_ALL[i]); 
		}
		
		return features;
	}

	// the file should in both folder
    private boolean validateFile(String folder, String folder2, String name) {
        logger.info("****folder*****" + folder);
        File f = new File(folder, name);
        File f2 = new File(folder2, name);
        logger.info("****file*****" + f);
        if (!(f.exists() && f2.exists())) {
            return false;
        }
        return true;
    }
	private ArrayList<String> getValidFeatures(boolean isCreation, List<String> installed) {
		ArrayList<String> features = new ArrayList<String>();
		if (!isCreation && installed != null) {
			for (int i = 0; i < installed.size(); i++)
				features.add(installed.get(i));
		}

		if (isCreation) {
			for (int i = 0; i < this.features.length; i++)
				features.add(this.features[i]);

			for (int i = 0; i < installed.size(); i++)
				features.remove(installed.get(i));
		}

		return features;
	}

	public void performUnSelect(String id) {
		if (this.selectedFeatures == null)
			this.selectedFeatures = new ArrayList<String>();
		//TODO modify by Maxi, special action of library_gcd and library_os
		if (Constants.FEATURE_LIBRARY.equals(id) || Constants.FEATURE_LIBRARY_GCD.equals(id) || Constants.FEATURE_LIBRARY_OS.equals(id)) {
			selectedFeatures.remove(Constants.FEATURE_LIBRARY_GCD);
			selectedFeatures.remove(Constants.FEATURE_LIBRARY_OS);
		} else
		//TODO end
		if (this.selectedFeatures.contains(id))
			selectedFeatures.remove(id);
		DBWizardInputs.setFeatures(this.getWizard(), this.selectedFeatures);
		checkBoxAll.setSelection(false);
		DBWizardInputs.setFeaturesSelectAll(getWizard(),checkBoxAll.getSelection());
	}

	public void performSelect(String id) {
		if (this.selectedFeatures == null)
			this.selectedFeatures = new ArrayList<String>();
		//TODO modify by Maxi, special action of library_gcd and library_os
		if (Constants.FEATURE_LIBRARY.equals(id) || Constants.FEATURE_LIBRARY_GCD.equals(id) || Constants.FEATURE_LIBRARY_OS.equals(id)) {
			if (!selectedFeatures.contains(Constants.FEATURE_LIBRARY_GCD))
				selectedFeatures.add(Constants.FEATURE_LIBRARY_GCD);
			if (!selectedFeatures.contains(Constants.FEATURE_LIBRARY_OS))
				selectedFeatures.add(Constants.FEATURE_LIBRARY_OS);
		} else 
		//TODO end
		if (!this.selectedFeatures.contains(id))
			selectedFeatures.add(id);
		DBWizardInputs.setFeatures(this.getWizard(), this.selectedFeatures);
	}

	public void isInputValid_fake() {
		this.showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.no.feature.selected"));
		DBWizardInputs.setFeatures(this.getWizard(), this.validFeatures);

		new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.featureSelectionWizardPage.drop.homepage"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open();
	}

	public boolean isInputValid() {
		if (this.selectedFeatures == null || this.selectedFeatures.size() == 0) {

			this.showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.no.feature.selected"));

			return false;
		}
		 String dbLocation = DBWizardInputs.getDbInstallDir(this.getWizard());
		 String title = Messages.getString("dbWizard.window.title");
		 String message_sqlxa = Messages.getString("dbWizard.featureSelectionWizardPage.ccmsqlserverxa");
		 String message_oraclxa = Messages.getString("dbWizard.featureSelectionWizardPage.ccmoraclxa");
        if ((!DBWizardInputs.isExportOnly(this.getWizard()))&&(Constants.OPERATION_TYPE_CREATE_DB.equals(this.action))&&(selectedFeatures.contains(Constants.FEATURE_LIBRARY_OS)||selectedFeatures.contains(Constants.FEATURE_LIBRARY_GCD))){
            // sql server: if dll not exsist, popup error message, else popup warning message of xa
            if (Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard()))) {              
                if (!validateFile(dbLocation + Constants.DB_SQLSERVER_2008_DIR_32, dbLocation + Constants.DB_SQLSERVER_2008_DIR_64, Constants.DB_SQLSERVER_XADLL)
                 && !validateFile(dbLocation + Constants.DB_SQLSERVER_2012_DIR_32, dbLocation + Constants.DB_SQLSERVER_2012_DIR_64, Constants.DB_SQLSERVER_XADLL)&& !validateFile(dbLocation + Constants.DB_SQLSERVER_2016_DIR_32, dbLocation + Constants.DB_SQLSERVER_2016_DIR_64, Constants.DB_SQLSERVER_XADLL)) {
                    this.showInputCheckErrorMsg(ErrorMsg
                            .getString("dbwizard.error.sqlserverxadll.not.exist"));
                    return false;
                }else{
                    new MessageDialog(getShell(), title, null, message_sqlxa, WARNING, new String[] { Messages.getString("button.OK.text") }, 0).open();
                }

            }
        }

		if (Constants.OPERATION_TYPE_DROP_DB.equals(this.action) && this.selectedFeatures.contains(Constants.FEATURE_HOMEPAGE)) {
			boolean status = new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.featureSelectionWizardPage.drop.homepage"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
			if(!status){
				Control[] children = checkBoxGroup.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (children[i] instanceof Button) {
						Button button = (Button) children[i];
						String Id = CommonHelper.getWidgetID(button);
						if (Constants.FEATURE_HOMEPAGE.equals(Id)) {
							button.setSelection(false);
						}
					}
				}
				performUnSelect(Constants.FEATURE_HOMEPAGE);
			}
			return status;
		}
//		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.action) && this.selectedFeatures.contains(Constants.FEATURE_COMMUNITIES)) {
//			boolean status = new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.featureSelectionWizardPage.upgrade.communities"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
//			if(!status){
//				Control[] children = checkBoxGroup.getChildren();
//				for (int i = 0; i < children.length; i++) {
//					if (children[i] instanceof Button) {
//						Button button = (Button) children[i];
//						String Id = CommonHelper.getWidgetID(button);
//						if (Constants.FEATURE_COMMUNITIES.equals(Id)) {
//							button.setSelection(false);
//						}
//					}
//				}
//				performUnSelect(Constants.FEATURE_COMMUNITIES);
//			}
//			return status;
//		}
		
		return true;
	}

	public IWizardPage getNextPage() {
		if (this.validFeatures.size() == 0) {
			if (!DBWizardInputs.fake) {
				return null;
			}
		}

		if (Constants.OPERATION_TYPE_CREATE_DB.equals(action)) {
			if (Constants.DB_DB2.equals(DBWizardInputs.getDbType(this.getWizard()))) {
				// for create database on DB2, no additional panel is needed
				this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
			} else {
				setNextPage(Pages.getPage(Pages.FEATURE_INFO_PAGE));
			}
		} else if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(action)) {
//			if (Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(this.getWizard()))) {
//				this.setNextPage(Pages.getPage(Pages.FEATURE_INFO_PAGE));
//			} else if(Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())) 
//					&& DBWizardInputs.getFeatures(this.getWizard()).contains(Constants.FEATURE_COMMUNITIES)){
//				this.setNextPage(Pages.getPage(Pages.FEATURE_INFO_PAGE));
//		    }else{
				// If java update is required, show the JDBC_CONNECTION_INFO panel
			if(DBWizardInputs.getDbType(this.getWizard()).equals(Constants.DB_SQLSERVER) && DBWizardInputs.getFeatures(this.getWizard()).contains(Constants.FEATURE_HOMEPAGE)){
				this.setNextPage(Pages.getPage(Pages.FEATURE_INFO_PAGE));
			}else{
				List<String> list = UpgradePath.containJava(DBWizardInputs.getDbType(this.getWizard()), 
						DBWizardInputs.getDbVersion(this.getWizard()),
						DBWizardInputs.getFeatures(this.getWizard()), 
						DBWizardInputs.getDbUpgradeVersion(this.getWizard()));
//				if (DBWizardInputs.getFeatures(this.getWizard()).contains(Constants.FEATURE_COMMUNITIES)){
//					list.add(Constants.FEATURE_COMMUNITIES);
//				}
				if (list != null && list.size() > 0) {
					this.setNextPage(Pages.getPage(Pages.JDBC_CONNECTION_INFO));
				}else{
					this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
			    }
			    }
		} else {
			// no additional panel is needed to delete database
			setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
		}

		return super.getNextPage();
	}
	public List<String> getSelectedFeatures() {
		return selectedFeatures;
	}
	public void setSelectedFeatures(List<String> selectedFeatures) {
		this.selectedFeatures = selectedFeatures;
	}
	public List<String> getValidFeatures() {
		return validFeatures;
	}
	public void setValidFeatures(List<String> validFeatures) {
		this.validFeatures = validFeatures;
	}
	public class StoryLifetimeInDaysDialog extends Dialog{
		
		Text text;

		protected StoryLifetimeInDaysDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.marginRight = 20;
			gridLayout.marginLeft = 20;
			parent.setLayout(gridLayout);
			
			Label infoMsg = new Label(parent, SWT.WRAP);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 10;
			gd.widthHint = 600;
			gd.horizontalSpan = 2;
			infoMsg.setLayoutData(gd);
			infoMsg.setText(Messages.getString("dbWizard.featureSelectionWizardPage.upgrade.homepage"));
			
			Label label = new Label(parent, SWT.NONE);
			GridData labelData = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
			gd.verticalIndent = 10;
			label.setLayoutData(labelData);
			label.setText(Messages.getString("dbWizard.featureSelectionWizardPage.upgrade.homepage.storyLifetimeInDays"));
			
			text = new Text(parent, SWT.BORDER);
			GridData textData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
			text.setLayoutData(textData);
			text.setText("30");
			text.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						int storyLifetimeInDays = Integer.parseInt(text.getText().trim());
						if (storyLifetimeInDays >=1){
							getButton(IDialogConstants.OK_ID).setEnabled(true);
						}else{
							getButton(IDialogConstants.OK_ID).setEnabled(false);
						}
					}catch(NumberFormatException nfe){
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}			
				}
				
			});
			return null;
		}

		@Override
		protected void initializeBounds() {
			Composite comp = (Composite) super.getButtonBar();
			super.createButton(comp, IDialogConstants.OK_ID, Messages.getString("button.OK.text"), true);
			super.initializeBounds();
		}

		@Override
		protected void okPressed() {
			String storyLifetimeInDays = text.getText();
			System.setProperty("story_life_time_in_days", storyLifetimeInDays);
			System.out.println("story_life_time_in_days = " + System.getProperty("story_life_time_in_days"));
			super.okPressed();
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			// Dialog Title
			newShell.setText(Messages.getString("dbWizard.window.title"));
		}

		@Override
		protected Button createButton(Composite parent, int id, String label,
				boolean defaultButton) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
