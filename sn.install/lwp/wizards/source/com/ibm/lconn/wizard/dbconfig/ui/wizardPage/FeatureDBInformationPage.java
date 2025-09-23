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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.dbconfig.DB2ConfigProp;
import com.ibm.lconn.wizard.dbconfig.backend.UpgradePath;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class FeatureDBInformationPage extends CommonPage implements SelectionListener, ModifyListener, FocusListener {
	private static final boolean HIDE = false;
	private boolean buttonOneChecked = true;
	private boolean buttonTwoChecked = false;
	private boolean buttonOneEnable = true;
	private boolean buttonTwoEnable = false;
	private Map<String, Text> textMap;
	private Map<String, Label> labelMap;
	private ModifyListener textListener = this;
	private SelectionListener checkListener = this;
	private GridData spaceData1;
	private GridData spaceData2;
	private String currentDBType;
	private String lastDBType;

	//TODO modify by Maxi, feature merge flag (librarygcd, libraryos) -> library
	private boolean flagLibraryMerged = false;
	//TODO end

	// private SelectionListener textLeave = this;

	/**
	 * @param pageName
	 */
	protected FeatureDBInformationPage() {
		super(Constants.WIZARD_PAGE_FEATURE_DB_INFORMATION);
		setTitle(Messages.getString("dbWizard.featureInformationWizardPage.title")); //$NON-NLS-1$
	}

	public void onShow(Composite parent) {
		Composite container = CommonHelper.createHVScrollableControl(Composite.class, parent, SWT.NONE, SWT.H_SCROLL | SWT.V_SCROLL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		container.setLayout(gridLayout);

		Label dec = new Label(container, SWT.NONE);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
		dec.setText(Messages.getString("dbWizard.featureInformationWizardPage.description")); //$NON-NLS-1$

		textMap = new HashMap<String, Text>();
		labelMap = new HashMap<String, Label>();
		List<String> selectedFeatures = DBWizardInputs.getFeatures(this.getWizard());
		
		List<String> showFeatures = new ArrayList<String>();
		if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& (Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())) || Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(this.getWizard()))))){
			showFeatures = selectedFeatures;
		}
		
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard()))
				&& selectedFeatures.contains(Constants.FEATURE_HOMEPAGE)
				&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
			showFeatures.add(Constants.FEATURE_HOMEPAGE);
		}
		
		if (!this.buttonOneChecked && !this.buttonTwoChecked) {
			this.buttonOneChecked = true;
		}
		if (this.buttonOneChecked && this.buttonTwoChecked) {
			this.buttonTwoChecked = false;
		}
		this.buttonOneEnable = true;
		this.buttonTwoEnable = true;

		Text text;
		Label label;

		this.lastDBType = this.currentDBType;
		this.currentDBType = DBWizardInputs.getDbType(this.getWizard());
		// ////////////////////////////////////////////button 1
		// part/////////////////////////////////////
		Button button1 = new Button(container, SWT.RADIO);
		button1.setData("name", "one");
		button1.setText(Messages.getString("dbWizard.featureInformationWizardPage.use.same"));
		button1.addSelectionListener(checkListener);
		button1.setEnabled(this.buttonOneEnable);
		button1.setSelection(this.buttonOneChecked);
		button1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));

		spaceData1 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);

		Text space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData1);
		space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData1);

		label = new Label(container, SWT.PUSH);
		label.setText(Messages.getString("dbWizard.featureInformationWizardPage.dbPassword.label"));
		// label.setLayoutData(data);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		labelMap.put("same.password", label);

		label = new Label(container, SWT.PUSH);
		label.setText(Messages.getString("dbWizard.featureInformationWizardPage.dbConfirmPassword.label"));
		// label.setLayoutData(data);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		labelMap.put("same.confirm", label);
		space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData1);

		space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData1);
		space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData1);

		text = new Text(container, SWT.BORDER | SWT.PUSH | SWT.PASSWORD);
		text.setData("name", "password");
		text.setEditable(true);
		// text.addModifyListener(textListener);
		// text.setLayoutData(data);
		textMap.put("same.password", text);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		// data.widthHint = 140;
		text.setLayoutData(data);

		text = new Text(container, SWT.BORDER | SWT.PUSH | SWT.PASSWORD);
		text.setData("name", "confirm");
		text.setEditable(true);
		// text.addModifyListener(textListener);
		// text.addFocusListener(this);
		// text.setLayoutData(data);
		textMap.put("same.confirm", text);
		// data = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		// data.widthHint = 140;
		text.setLayoutData(data);

		space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData1);

		// ////////////////////////////button 2
		// part///////////////////////////////////
		spaceData2 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		Button button2 = new Button(container, SWT.RADIO);
		button2.setData("name", "two");
		button2.setText(Messages.getString("dbWizard.featureInformationWizardPage.use.different"));
		button2.addSelectionListener(checkListener);
		button2.setEnabled(this.buttonTwoEnable);
		button2.setSelection(this.buttonTwoChecked);
		button2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));

		space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData2);
		space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData2);
		label = new Label(container, SWT.PUSH);
		label.setText(Messages.getString("dbWizard.featureInformationWizardPage.dbUser.label"));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		labelMap.put("user", label);

		label = new Label(container, SWT.PUSH);
		label.setText(Messages.getString("dbWizard.featureInformationWizardPage.dbPassword.label"));
		// label.setLayoutData(data);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		labelMap.put("password", label);

		label = new Label(container, SWT.PUSH);
		label.setText(Messages.getString("dbWizard.featureInformationWizardPage.dbConfirmPassword.label"));
		// label.setLayoutData(data);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		labelMap.put("confirm", label);
		
		//*****************jia************************
		
		for (String feature : Constants.FEATURE_ALL) {
			if (showFeatures.contains(feature)) {
				space = new Text(container, SWT.NONE);
				space.setText("space");
				space.setVisible(false);
				space.setLayoutData(spaceData2);

				label = new Label(container, SWT.NONE);
				//TODO modify by Maxi, special action of library_gcd and library_os
				if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
					label.setText(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + "." + Messages.getString(feature + ".shortname"));
				} else {
					label.setText(Messages.getString(feature + ".name"));
				}
				//TODO end
				label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

				label.setFont(ResourcePool.getBoldFont(label));
				labelMap.put(feature + ".name", label);

				text = new Text(container, SWT.PUSH | SWT.BORDER);
				text.setEditable(false);
				text.setText(DB2ConfigProp.getProperty(feature + ".dbUserName." + DBWizardInputs.getDbType(this.getWizard())));
				// text.setLayoutData(data);
				// data = new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				// 1);
				// data.widthHint = 140;
				text.setLayoutData(data);
				textMap.put(feature + ".user", text);

				text = new Text(container, SWT.BORDER | SWT.PUSH | SWT.PASSWORD);
				text.setEditable(true);
				// text.addModifyListener(textListener);
				text.setData("feature", feature);
				text.setData("name", "password");
				// text.setLayoutData(data);
				textMap.put(feature + ".password", text);
				// data = new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				// 1);
				// data.widthHint = 140;
				text.setLayoutData(data);

				text = new Text(container, SWT.BORDER | SWT.PUSH | SWT.PASSWORD);
				text.setEditable(true);
				text.setData("name", "confirm");
				text.setData("feature", feature);
				// text.addModifyListener(textListener);
				// text.addFocusListener(this);
				// text.setLayoutData(data);
				textMap.put(feature + ".confirm", text);
				// data = new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				// 1);
				// data.widthHint = 140;
				text.setLayoutData(data);
			}
		}
		
		this.performSelection();
	}

	private void performSelection() {
		List<String> selectedFeatures = DBWizardInputs.getFeatures(this.getWizard());
		List<String> showFeatures = new ArrayList<String>();
		if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& (Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())) || Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(this.getWizard()))))){
			showFeatures = selectedFeatures;
		}
		
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard()))
				&& selectedFeatures.contains(Constants.FEATURE_HOMEPAGE)
				&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
			showFeatures.add(Constants.FEATURE_HOMEPAGE);
		}
		
		Text text;
		Label label;
		label = this.labelMap.get("user");
		((GridData) (label.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
		label.setVisible(this.buttonTwoChecked || (!HIDE));

		label = this.labelMap.get("password");
		((GridData) (label.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
		label.setVisible(this.buttonTwoChecked || (!HIDE));

		label = this.labelMap.get("confirm");
		((GridData) (label.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
		label.setVisible(this.buttonTwoChecked || (!HIDE));

		this.spaceData2.exclude = (!this.buttonTwoChecked) && HIDE;

		for (String feature : showFeatures) {
			label = this.labelMap.get(feature + ".name");
			((GridData) (label.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
			label.setVisible(this.buttonTwoChecked || (!HIDE));

			text = this.textMap.get(feature + ".user");
			((GridData) (text.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
			text.setVisible(this.buttonTwoChecked || (!HIDE));
			text.setEditable(false);

			text = this.textMap.get(feature + ".password");
			// text.removeModifyListener(textListener);
			// text.setText("");
			((GridData) (text.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
			text.setVisible(this.buttonTwoChecked || (!HIDE));
			text.setEditable(this.buttonTwoChecked);
			text.setEnabled(this.buttonTwoChecked);
			// text.addModifyListener(textListener);
			if (CommonHelper.equals(this.currentDBType, this.lastDBType)) {
				Map<String, String> p = DBWizardInputs.getDbUserPassword(this.getWizard());
				if (p != null && p.get(feature) != null)
					text.setText(p.get(feature));
			}

			text = this.textMap.get(feature + ".confirm");
			// text.removeModifyListener(textListener);
			// text.setText("");
			((GridData) (text.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
			text.setVisible(this.buttonTwoChecked || (!HIDE));
			text.setEditable(this.buttonTwoChecked);
			text.setEnabled(this.buttonTwoChecked);
			// text.addModifyListener(textListener);
			if (CommonHelper.equals(this.currentDBType, this.lastDBType)) {
				Map<String, String> p = DBWizardInputs.getDbUserPasswordConfirm(this.getWizard());
				if (p != null && p.get(feature) != null)
					text.setText(p.get(feature));
			}
		}

		text = this.textMap.get("same.password");
		// text.removeModifyListener(textListener);
		// text.setText("");
		((GridData) (text.getLayoutData())).exclude = (!this.buttonOneChecked) && HIDE;
		text.setVisible(this.buttonOneChecked || (!HIDE));
		text.setEditable(this.buttonOneChecked);
		text.setEnabled(this.buttonOneChecked);
		// text.addModifyListener(textListener);
		if (CommonHelper.equals(this.currentDBType, this.lastDBType)) {
			String p = DBWizardInputs.getSameDbUserPassword(this.getWizard());
			if (p == null)
				p = "";
			text.setText(p);
		} else {
			// text.setText("");
		}

		text = this.textMap.get("same.confirm");
		// text.removeModifyListener(textListener);
		// text.setText("");
		((GridData) (text.getLayoutData())).exclude = (!this.buttonOneChecked) && HIDE;
		text.setVisible(this.buttonOneChecked || (!HIDE));
		text.setEditable(this.buttonOneChecked);
		text.setEnabled(this.buttonOneChecked);
		// text.addModifyListener(textListener);
		if (CommonHelper.equals(this.currentDBType, this.lastDBType)) {
			String p = DBWizardInputs.getSameDbUserPasswordConfirm(this.getWizard());
			if (p == null)
				p = "";
			text.setText(p);
		} else {
			// text.setText("");
		}

		label = this.labelMap.get("same.password");
		((GridData) (label.getLayoutData())).exclude = (!this.buttonOneChecked) && HIDE;
		label.setVisible(this.buttonOneChecked || (!HIDE));

		label = this.labelMap.get("same.confirm");
		((GridData) (label.getLayoutData())).exclude = (!this.buttonOneChecked) && HIDE;
		label.setVisible(this.buttonOneChecked || (!HIDE));
		this.spaceData1.exclude = (!this.buttonOneChecked) && HIDE;

		text.getParent().layout(true, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events
	 * .ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		if (!this.buttonOneChecked)
			return;

		if (e.widget == null)
			return;
		if (e.widget instanceof Text) {
			Text text = (Text) e.widget;
			List<String> selectedFeatures = DBWizardInputs.getFeatures(this.getWizard());
			String changing = text.getText();
			String postfix = (String) text.getData("name");
			for (String feature : selectedFeatures) {
				text = this.textMap.get(feature + "." + postfix);
				if (!(text == e.widget)) {
					text.removeModifyListener(textListener);
					text.setText(changing);
					text.addModifyListener(textListener);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		if (event.widget == null)
			return;
		if (event.widget instanceof Button) {
			Button button = (Button) event.widget;
			if ("one".equals(button.getData("name")))
				this.buttonOneChecked = button.getSelection();
			if ("two".equals(button.getData("name")))
				this.buttonTwoChecked = button.getSelection();
			saveInputs();
			performSelection();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
	 * .events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget == null)
			return;
		if (event.widget instanceof Button) {
			Button button = (Button) event.widget;
			if ("one".equals(button.getData("name")))
				this.buttonOneChecked = button.getSelection();
			if ("two".equals(button.getData("name")))
				this.buttonTwoChecked = button.getSelection();
			saveInputs();
			performSelection();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events
	 * .FocusEvent)
	 */
	public void focusGained(FocusEvent event) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events
	 * .FocusEvent)
	 */
	public void focusLost(FocusEvent event) {
		if (event.widget == null)
			return;

		if (event.widget instanceof Text) {
			Text text = (Text) event.widget;
			String confirm = text.getText();
			String feature = (String) text.getData("feature");
			String password = textMap.get(feature + ".password").getText();
			if (!(password.equals(confirm))) {
				this.showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.confirm.password.different"));
				text.setText("");
				text.setFocus();
			}
		}
	}

	public IWizardPage getPreviousPage() {
		saveInputs();
		return super.getPreviousPage();
	}

	public IWizardPage getNextPage() {
		if (DBWizardInputs.getDbType(this.getWizard()).equals(Constants.DBMS_SQLSERVER) && Constants.OPERATION_TYPE_CREATE_DB.equals(DBWizardInputs.getActionId(this.getWizard())))
			this.setNextPage(Pages.getPage(Pages.SQLSERVER_FILE));
		else if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard()))) {
			this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
			// If java update is required, show the JDBC_CONNECTION_INFO panel
			List<String> list = UpgradePath.containJava(DBWizardInputs.getDbType(this.getWizard()), 
					DBWizardInputs.getDbVersion(this.getWizard()),
					DBWizardInputs.getFeatures(this.getWizard()), 
					DBWizardInputs.getDbUpgradeVersion(this.getWizard()));
			if (list != null && list.size() > 0) {
				this.setNextPage(Pages.getPage(Pages.JDBC_CONNECTION_INFO));
			}
		} else
			this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
		saveInputs();
		return super.getNextPage();
	}

	public void isInputValid_fake() {
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.empty.password"));
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.confirm.password.different"));
		List<String> featureList = DBWizardInputs.getFeatures(this.getWizard());
		for (String feature : featureList) {
			showInputCheckErrorMsg(Messages.getString(feature + ".name") + ":" + ErrorMsg.getString("dbwizard.error.empty.password"));
			showInputCheckErrorMsg(Messages.getString(feature + ".name") + ":" + ErrorMsg.getString("dbwizard.error.confirm.password.different"));
		}
	}

	public boolean isInputValid() {
		if (DBWizardInputs.getDbType(this.getWizard()).equals(Constants.DBMS_DB2))
			return true;

		boolean result = true;

		if (this.buttonOneChecked) {
			String password = textMap.get("same.password").getText();
			String confirm = textMap.get("same.confirm").getText();
			if (password == null || password.equals("")) {
				result = false;
				showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.empty.password"));
				textMap.get("same.password").setFocus();
			} else if (!password.equals(confirm)) {
				result = false;
				showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.confirm.password.different"));
				textMap.get("same.confirm").setFocus();
			}
		} else if (this.buttonTwoChecked) {
			List<String> featureList = DBWizardInputs.getFeatures(this.getWizard());
			List<String> showFeatures = new ArrayList<String>();
			if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
					&& (Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())) || Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(this.getWizard()))))){
				showFeatures = featureList;
			}
			if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
					&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard()))
					&& featureList.contains(Constants.FEATURE_HOMEPAGE)
					&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
				showFeatures.add(Constants.FEATURE_HOMEPAGE);
			}
			
			for (String feature : showFeatures) {
				//TODO modify by Maxi, special action of library_gcd and library_os
				if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
					if (!flagLibraryMerged) {
						String passwordGCD = textMap.get(Constants.FEATURE_LIBRARY_GCD + ".password").getText();
						String confirmGCD = textMap.get(Constants.FEATURE_LIBRARY_GCD + ".confirm").getText();
						String passwordOS = textMap.get(Constants.FEATURE_LIBRARY_OS + ".password").getText();
						String confirmOS = textMap.get(Constants.FEATURE_LIBRARY_OS + ".confirm").getText();
						if (passwordGCD == null || passwordGCD.equals("")) {
							result = false;
							showInputCheckErrorMsg(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + "." + Messages.getString(Constants.FEATURE_LIBRARY_GCD + ".shortname") + ":" + ErrorMsg.getString("dbwizard.error.empty.password"));
							textMap.get(Constants.FEATURE_LIBRARY_GCD + ".password").setFocus();
							break;
						}
						if (passwordOS == null || passwordOS.equals("")) {
							result = false;
							showInputCheckErrorMsg(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + "." + Messages.getString(Constants.FEATURE_LIBRARY_OS + ".shortname") + ":" + ErrorMsg.getString("dbwizard.error.empty.password"));
							textMap.get(Constants.FEATURE_LIBRARY_OS + ".password").setFocus();
							break;
						}
						if (!passwordGCD.equals(confirmGCD)) {
							result = false;
							showInputCheckErrorMsg(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + "." + Messages.getString(Constants.FEATURE_LIBRARY_GCD + ".shortname") + ":" + ErrorMsg.getString("dbwizard.error.confirm.password.different"));
							textMap.get(Constants.FEATURE_LIBRARY_GCD + ".confirm").setFocus();
							break;
						}
						if (!passwordOS.equals(confirmOS)) {
							result = false;
							showInputCheckErrorMsg(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + "." + Messages.getString(Constants.FEATURE_LIBRARY_OS + ".shortname") + ":" + ErrorMsg.getString("dbwizard.error.confirm.password.different"));
							textMap.get(Constants.FEATURE_LIBRARY_OS + ".confirm").setFocus();
							break;
						}
						flagLibraryMerged = true;
					}
					continue;
				}
				//TODO end
				String password = textMap.get(feature + ".password").getText();
				String confirm = textMap.get(feature + ".confirm").getText();
				if (password == null || password.equals("")) {
					result = false;
					showInputCheckErrorMsg(Messages.getString(feature + ".name") + ":" + ErrorMsg.getString("dbwizard.error.empty.password"));
					textMap.get(feature + ".password").setFocus();
					break;
				}
				if (!password.equals(confirm)) {
					result = false;
					showInputCheckErrorMsg(Messages.getString(feature + ".name") + ":" + ErrorMsg.getString("dbwizard.error.confirm.password.different"));
					textMap.get(feature + ".confirm").setFocus();
					break;
				}
			}
			
			//TODO modify by Maxi, reset flag
			flagLibraryMerged = false;
			//TODO end
		}
		return result;
	}

	private void saveInputs() {
		Map<String, String> passwords = new HashMap<String, String>();
		Map<String, String> confirms = new HashMap<String, String>();
		List<String> selected = DBWizardInputs.getFeatures(this.getWizard());
		List<String> showFeatures = new ArrayList<String>();
		if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& (Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())) || Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(this.getWizard()))))){
			showFeatures = selected;
		}
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard()))
				&& selected.contains(Constants.FEATURE_HOMEPAGE)
				&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
			showFeatures.add(Constants.FEATURE_HOMEPAGE);
		}
		
		for (String feature : showFeatures) {
			passwords.put(feature, textMap.get(feature + ".password").getText());
			confirms.put(feature, textMap.get(feature + ".confirm").getText());
		}
		
		if(passwords.get(Constants.FEATURE_HOMEPAGE) != null){
			System.setProperty("hompage_upgradedb_password", passwords.get(Constants.FEATURE_HOMEPAGE));
		}
		if(textMap.get("same.password").getText() != null){
			System.setProperty("homepage_upgradedb_password", textMap.get("same.password").getText());
		}
		
		DBWizardInputs.setDbUserPassword(this.getWizard(), passwords);
		DBWizardInputs.setDbUserPasswordConfirm(this.getWizard(), confirms);
		DBWizardInputs.setSameDbUserPassword(this.getWizard(), textMap.get("same.password").getText());
		DBWizardInputs.setSameDbUserPasswordConfirm(this.getWizard(), textMap.get("same.confirm").getText());
		DBWizardInputs.setDbUserPasswordSame(this.getWizard(), this.buttonOneChecked);
		
//		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
//				&& selected.contains(Constants.FEATURE_COMMUNITIES)){
//			Iterator it = selected.iterator();
//			while(it.hasNext()){
//				if(Constants.FEATURE_FORUM.equals(((String) it.next()))){
//					it.remove();
//				}
//			}
//		}
		
	
	}
}
