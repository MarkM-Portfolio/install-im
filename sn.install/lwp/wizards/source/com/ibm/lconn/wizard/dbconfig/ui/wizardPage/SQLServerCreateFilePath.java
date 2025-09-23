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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class SQLServerCreateFilePath extends CommonPage implements SelectionListener, ModifyListener {
	private static final boolean HIDE = false;
	private boolean buttonOneChecked = true;
	private boolean buttonTwoChecked = false;
	private Map<String, Text> textMap;
	private Map<String, Label> labelMap;
	private Map<String, Button> browserMap;
	private ModifyListener textListener = this;
	private SelectionListener checkListener = this;
	private GridData spaceData1;
	private GridData spaceData2;

	//TODO modify by Maxi, feature merge flag (librarygcd, libraryos) -> library
	private boolean flagLibraryMerged = false;
	//TODO end
	
	public SQLServerCreateFilePath() {
		super(Constants.WIZARD_PAGE_SQLSERVER_FILE);
		setTitle(Messages.getString("dbWizard.SQLServerCreateFilePath.title")); //$NON-NLS-1$
	}

	public void onShow(Composite parent) {
		Composite container = CommonHelper.createHVScrollableControl(Composite.class, parent, SWT.NONE, SWT.H_SCROLL | SWT.V_SCROLL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		container.setLayout(gridLayout);

		Label dec = new Label(container, SWT.NONE);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		dec.setText(Messages.getString("dbWizard.SQLServerCreateFilePath.description")); //$NON-NLS-1$

		textMap = new HashMap<String, Text>();
		labelMap = new HashMap<String, Label>();
		browserMap = new HashMap<String, Button>();
		List<String> selectedFeatures = DBWizardInputs.getFeatures(this.getWizard());
		
		List<String> showFeatures = new ArrayList<String>();
		if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())))){
			showFeatures = selectedFeatures;
		}
		
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& selectedFeatures.contains(Constants.FEATURE_HOMEPAGE)
				&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
			showFeatures.add(Constants.FEATURE_HOMEPAGE);
		}

		Text text;
		Label label;
		// ////////////////////////////button 1
		// part//////////////////////////////////
		Button button1 = new Button(container, SWT.RADIO);
		button1.setData("name", "one");
		button1.setText(Messages.getString("dbWizard.SQLServerCreateFilePath.use.same"));
		button1.addSelectionListener(checkListener);
		button1.setSelection(this.buttonOneChecked);
		button1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));

		spaceData1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		Text space1 = new Text(container, SWT.NONE);
		space1.setVisible(false);
		space1.setText("space");
		space1.setLayoutData(spaceData1);

		label = new Label(container, SWT.PUSH);
		label.setText(Messages.getString("dbWizard.SQLServerCreateFilePath.dbFilePath.label"));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		labelMap.put("same", label);

		text = new Text(container, SWT.BORDER);
		// gridData.widthHint = 200;
		text.setEditable(true);
		text.addModifyListener(textListener);
		// text.addFocusListener(this);
		// text.setLayoutData(data);
		GridData data1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		// data1.widthHint = 300;
		text.setLayoutData(data1);
		textMap.put("same", text);

		final Button browse = new Button(container, SWT.NONE);
		browse.addSelectionListener(this);
		browse.setData("type", "browse");
		browse.setData("feature", "same");
		browse.setText(Messages.getString("dbWizard.SQLServerCreateFilePath.button.browse")); //$NON-NLS-1$
		browse.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		this.browserMap.put("same", browse);

		// ////////////////////////////button 2
		// part///////////////////////////////////

		Button button2 = new Button(container, SWT.RADIO);
		button2.setData("name", "two");
		button2.setText(Messages.getString("dbWizard.SQLServerCreateFilePath.use.different"));
		button2.addSelectionListener(checkListener);
		button2.setSelection(this.buttonTwoChecked);
		button2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));

		spaceData2 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);

		Text space = new Text(container, SWT.NONE);
		space.setText("space");
		space.setVisible(false);
		space.setLayoutData(spaceData2);

		label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("dbWizard.feature.label"));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		Font initialFont = label.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setStyle(SWT.BOLD);
		}
		Font newFont = new Font(Display.getCurrent(), fontData);
		label.setFont(newFont);

		label = new Label(container, SWT.PUSH);
		label.setText(Messages.getString("dbWizard.SQLServerCreateFilePath.dbFilePath.label"));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		labelMap.put("feature", label);

		//***************jia**************
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
				for (int i = 0; i < fontData.length; i++) {
					fontData[i].setStyle(SWT.BOLD);
				}
				label.setFont(newFont);
				labelMap.put(feature, label);

				text = new Text(container, SWT.BORDER);
				text.setEditable(true);
				text.addModifyListener(textListener);
				GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
				// data.widthHint = 200;
				text.setLayoutData(data);
				textMap.put(feature, text);

				final Button browseButton = new Button(container, SWT.NONE);
				browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				browseButton.addSelectionListener(this);
				browseButton.setData("type", "browse");
				browseButton.setData("feature", feature);
				browseButton.setText(Messages.getString("dbWizard.SQLServerCreateFilePath.button.browse")); //$NON-NLS-1$
				this.browserMap.put(feature, browseButton);
			}
		}
		
		performSelection();
	}

	private void performSelection() {
		List<String> selectedFeatures = DBWizardInputs.getFeatures(this.getWizard());

		selectedFeatures = DBWizardInputs.getFeatures(this.getWizard());
		
		List<String> showFeatures = new ArrayList<String>();
		if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())))){
			showFeatures = selectedFeatures;
		}
		
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& selectedFeatures.contains(Constants.FEATURE_HOMEPAGE)
				&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
			showFeatures.add(Constants.FEATURE_HOMEPAGE);
		}
		
		Text text;
		Label label;
		Button button;
		label = this.labelMap.get("feature");
		((GridData) (label.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
		label.setVisible(this.buttonTwoChecked || (!HIDE));

		this.spaceData2.exclude = (!this.buttonTwoChecked) && HIDE;

		Map<String, String> paths = DBWizardInputs.getCreateDbFilePath(this.getWizard());
		for (String feature : showFeatures) {
			label = this.labelMap.get(feature);
			((GridData) (label.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
			label.setVisible(this.buttonTwoChecked || (!HIDE));

			text = this.textMap.get(feature);
			((GridData) (text.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
			text.setVisible(this.buttonTwoChecked || (!HIDE));
			text.setEnabled(this.buttonTwoChecked);
			if (paths != null && paths.get(feature) != null)
				text.setText(paths.get(feature));
			
			button = this.browserMap.get(feature);
			((GridData) (button.getLayoutData())).exclude = (!this.buttonTwoChecked) && HIDE;
			button.setVisible(this.buttonTwoChecked || (!HIDE));
			button.setEnabled(this.buttonTwoChecked);
		}

		this.spaceData1.exclude = (!this.buttonOneChecked) && HIDE;
		text = this.textMap.get("same");
		text.removeModifyListener(textListener);
		String p = DBWizardInputs.getSameCreateDbFilePath(this.getWizard());
		if (p == null)
			p = "";
		text.setText(p);

		((GridData) (text.getLayoutData())).exclude = (!this.buttonOneChecked) && HIDE;
		text.setVisible(this.buttonOneChecked || (!HIDE));
		text.addModifyListener(textListener);
		text.setEnabled(this.buttonOneChecked);

		label = this.labelMap.get("same");
		((GridData) (label.getLayoutData())).exclude = (!this.buttonOneChecked) && HIDE;
		label.setVisible(this.buttonOneChecked || (!HIDE));

		button = this.browserMap.get("same");
		((GridData) (button.getLayoutData())).exclude = (!this.buttonOneChecked) && HIDE;
		button.setVisible(this.buttonOneChecked || (!HIDE));
		button.setEnabled(this.buttonOneChecked);
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
		/*
		 * if (!this.buttonOneChecked) return;
		 * 
		 * if (e.widget == null) return; if (e.widget instanceof Text) { Text
		 * text = (Text) e.widget; List<String> selectedFeatures =
		 * DBWizardInputs.getFeatures(this .getWizard()); String changing =
		 * text.getText(); for (String feature : selectedFeatures) { text =
		 * this.textMap.get(feature); if (!(text == e.widget)) {
		 * text.removeModifyListener(textListener); text.setText(changing);
		 * text.addModifyListener(textListener); } } }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		this.widgetSelected(event);
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

			if ("browse".equals(button.getData("type"))) {
				DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell());

				Text text = this.textMap.get((String) button.getData("feature"));
				if (text.getText() != null)
					directoryDialog.setFilterPath(text.getText());
				directoryDialog.setMessage(Messages.getString("dbWizard.SQLServerCreateFilePath.dirSelection.message")); //$NON-NLS-1$

				String dir = directoryDialog.open();
				if (dir != null) {
					text.setText(dir);
				}
			} else {
				if ("one".equals(button.getData("name"))) {
					this.buttonOneChecked = button.getSelection();
				//	DBWizardInputs.setDbUserPasswordSame(this.getWizard(), true);
				}
				if ("two".equals(button.getData("name"))) {
					this.buttonTwoChecked = button.getSelection();
				//	DBWizardInputs.setDbUserPasswordSame(this.getWizard(), false);
				}
				saveInputs();
				performSelection();
			}
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
		if (DBWizardInputs.getDbType(this.getWizard()).equals(Constants.DBMS_SQLSERVER)){
			if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard()))
					&& (DBWizardInputs.getFeatures(this.getWizard()).contains(Constants.FEATURE_HOMEPAGE))){
				this.setNextPage(Pages.getPage(Pages.JDBC_CONNECTION_INFO));
			}else{
				this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
			}	
		}else
			this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
		saveInputs();
		return super.getNextPage();
	}

	public void isInputValid_fake() {
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.filepath.not.exist"));

		List<String> featureList = DBWizardInputs.getFeatures(this.getWizard());
		for (String feature : featureList) {
			//TODO modify by Maxi, special action of library_gcd and library_os
			if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
				if (!flagLibraryMerged) {
					showInputCheckErrorMsg(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + ":" + ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
					flagLibraryMerged = true;
				}
				continue;
			}
			//TODO end
			showInputCheckErrorMsg(Messages.getString(feature + ".name") + ":" + ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
		}
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
	}

	public boolean isInputValid() {
		boolean result = true;

		if (this.buttonOneChecked) {
			String text = textMap.get("same").getText();
			if (text == null || text.equals("") || !new File(text).exists()) {
				result = false;
				showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
				textMap.get("same").setFocus();
				result = false;
			}
			return result;
		}
		List<String> featureList = DBWizardInputs.getFeatures(this.getWizard());
		List<String> showFeatures = new ArrayList<String>();
		if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())))){
			showFeatures = featureList;
		}
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& featureList.contains(Constants.FEATURE_HOMEPAGE)
				&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
			showFeatures.add(Constants.FEATURE_HOMEPAGE);
		}
		for (String feature : showFeatures) {
			//TODO modify by Maxi, special action of library_gcd and library_os
			if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
				if (!flagLibraryMerged) {
					String text = textMap.get(Constants.FEATURE_LIBRARY_GCD).getText();
					if (text == null || text.equals("") || !new File(text).exists()) {
						result = false;
						showInputCheckErrorMsg(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + "." + Messages.getString(Constants.FEATURE_LIBRARY_GCD + ".shortname") + ":" + ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
						textMap.get(Constants.FEATURE_LIBRARY_GCD).setFocus();
						break;
					}
					text = textMap.get(Constants.FEATURE_LIBRARY_OS).getText();
					if (text == null || text.equals("") || !new File(text).exists()) {
						result = false;
						showInputCheckErrorMsg(Messages.getString(Constants.FEATURE_LIBRARY + ".name") + "." + Messages.getString(Constants.FEATURE_LIBRARY_OS + ".shortname") + ":" + ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
						textMap.get(Constants.FEATURE_LIBRARY_OS).setFocus();
						break;
					}
					flagLibraryMerged = true;
				}
				continue;
			}
			//TODO end
			String text = textMap.get(feature).getText();
			if (text == null || text.equals("") || !new File(text).exists()) {
				result = false;
				showInputCheckErrorMsg(Messages.getString(feature + ".name") + ":" + ErrorMsg.getString("dbwizard.error.filepath.not.exist"));
				textMap.get(feature).setFocus();
				break;
			}
		}
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
		return result;
	}

	private void saveInputs() {
		Map<String, String> pathes = new HashMap<String, String>();
		List<String> selected = DBWizardInputs.getFeatures(this.getWizard());
		List<String> showFeatures = new ArrayList<String>();
		if(!(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())))){
			showFeatures = selected;
		}
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& selected.contains(Constants.FEATURE_HOMEPAGE)
				&& !showFeatures.contains(Constants.FEATURE_HOMEPAGE)){
			showFeatures.add(Constants.FEATURE_HOMEPAGE);
		}
		for (String feature : showFeatures) {
			pathes.put(feature, textMap.get(feature).getText());
		}
		
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& selected.contains(Constants.FEATURE_HOMEPAGE)){
			System.setProperty("homepage_upgradedb_file_path", pathes.get(Constants.FEATURE_HOMEPAGE));
			System.setProperty("homepage_upgradedb_file_path", textMap.get("same").getText());
		}
		
		DBWizardInputs.setCreateDbFilePath(this.getWizard(), pathes);
		DBWizardInputs.setSameCreateDbFilePath(this.getWizard(), textMap.get("same").getText());
		DBWizardInputs.setCreateDbFilePathSame(this.getWizard(), this.buttonOneChecked);
	}
}
