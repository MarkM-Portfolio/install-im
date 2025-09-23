/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                  */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ValuePool;
import com.ibm.lconn.wizard.common.depcheck.DepChecker;
import com.ibm.lconn.wizard.common.depcheck.ProductInfo;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.GroupUtil;
import com.ibm.lconn.wizard.common.ui.TextHelper;
import com.ibm.lconn.wizard.dbconfig.backend.Checker;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.OperationOptionWizardPage;
import com.ibm.lconn.wizard.dbconfig.ui.event.listener.DBTypeSelectionPageRadioButtonListener;
import com.ibm.lconn.wizard.dbconfig.ui.event.listener.OperationOptionPageListener;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class DbTypeSelectionWizardPage extends CommonPage {

	private Text dbLocationText;
	private Text dbInstanceText;
	private String dbType;
	private String dbVersion;
	private Group group;
	private Map<String, ProductInfo> detectedDBMap = null;
	private int count = 0;
	private Text dbaUserText;
	private Label dbaUserLabel;
	private Button PDBNameButton;
	private Button isRunAsSYSDBAButton;
	private Text PDBNameText;
	private Text dbaPasswordText;
	private Label dbaPasswordLabel;

	

	public static final Logger logger = LogUtil.getLogger(DbTypeSelectionWizardPage.class);
	/**
	 * Create the wizard
	 */
	public DbTypeSelectionWizardPage() {
		super(Constants.WIZARD_PAGE_DB_TYPE_SELECTION);
		setTitle(Messages.getString("dbWizard.dbTypeSelectionWizardPage.title")); //$NON-NLS-1$
		DepChecker dc = new DepChecker(DepChecker.PRODUCT_DB, CommonHelper.getPlatformType());
		detectedDBMap = dc.check();

		this.setNextPage(Pages.getPage(Pages.FEATURE_SELECT));
	}

	/**
	 * Create contents of the wizard
	 * 
	 * @param parent
	 */
	public void onShow(Composite parent) {
		Composite container = CommonHelper.createHVScrollableControl(Composite.class, parent, SWT.NONE, SWT.H_SCROLL | SWT.V_SCROLL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;

		container.setLayout(gridLayout);

		Label dec = new Label(container, SWT.WRAP);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		dec.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.description")); //$NON-NLS-1$

		String[] dbTypeArr = null;
		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			dbTypeArr = Constants.DBMS_DB_TYPE_ALL;
		} else {
			dbTypeArr = new String[] { Constants.DBMS_DB2, Constants.DBMS_ORACLE };
		}
		// radio button group
		group = GroupUtil.createRadioGroup(dbTypeArr, container, SWT.NONE, SWT.NONE, new DBTypeSelectionPageRadioButtonListener(this), new ValuePool() {
			public String getString(String key) {
				return Messages.getString(key + ".name"); //$NON-NLS-1$   Is this get the three types DB name from messages_*.properties?
			}
		});
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		// Db location lable
		final Label databaseInstallationDirectoryLabel = new Label(container, SWT.WRAP);
		databaseInstallationDirectoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		databaseInstallationDirectoryLabel.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.dbDir.label")); //$NON-NLS-1$

		// Db location input
		dbLocationText = TextHelper.newAutoValidationText(container, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// gridData.widthHint = 250;
		dbLocationText.setLayoutData(gridData);

		// Db location browse
		final Button browseButton = new Button(container, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
				if (dbLocationText.getText() != null)
					directoryDialog.setFilterPath(dbLocationText.getText());
				directoryDialog.setMessage(Messages.getString("dbWizard.dbTypeSelectionWizardPage.dirSelection.message")); //$NON-NLS-1$

				String dir = directoryDialog.open();
				if (dir != null) {
					dbLocationText.setText(dir);
				}
			}
		});
		browseButton.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.button.browse")); //$NON-NLS-1$

		// db instance label
		final Label databaseInstanceSpecifyLabel = new Label(container, SWT.NONE);
		databaseInstanceSpecifyLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		databaseInstanceSpecifyLabel.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.dbInstance.label")); //$NON-NLS-1$

		// db instance input
		dbInstanceText = TextHelper.newAutoValidationText(container, SWT.BORDER);
		dbInstanceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

//#Oracle12C_PDB_disable#  BEGIN
/*		
		// PDB name label
		PDBNameButton = new Button(container,SWT.CHECK);
		PDBNameButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,3,1));
		PDBNameButton.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.PDB.check")); //$NON-NLS-1$

		
		//PDB name input
		PDBNameText = TextHelper.newAutoValidationText(container, SWT.BORDER);
		PDBNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		
		// dbaUser label
		dbaUserLabel = new Label(container, SWT.NONE);
		dbaUserLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,3,1));
		dbaUserLabel.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.dbaUser.label")); //$NON-NLS-1$
	
		
		//dbaUser input
		dbaUserText = TextHelper.newAutoValidationText(container, SWT.BORDER);
		dbaUserText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		isRunAsSYSDBAButton = new Button(container,SWT.CHECK);
		isRunAsSYSDBAButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		isRunAsSYSDBAButton.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.sysdbaUser.check")); //$NON-NLS-1$
		isRunAsSYSDBAButton.addSelectionListener(new DBTypeSelectionPageRadioButtonListener(this));
		if(count==0){
			isRunAsSYSDBAButton.setSelection(true);
			PDBNameButton.setSelection(true);
			PDBNameText.setEnabled(true);
			dbaUserText.setText("sys");
			DBWizardInputs.setRunAsSYSDBA(this.getWizard(),isRunAsSYSDBAButton.getSelection());
			DBWizardInputs.setPDBNameSelection(this.getWizard(),PDBNameButton.getSelection());
			DBWizardInputs.setPDBNameTextEnabled(this.getWizard(),PDBNameText.getEnabled());
			DBWizardInputs.setDbaUserName(this.getWizard(),dbaUserText.getText());
			count++;
		}else{
			isRunAsSYSDBAButton.setSelection(DBWizardInputs.isRunAsSYSDBA(this.getWizard()));
			PDBNameButton.setSelection(DBWizardInputs.isUsePDBName(this.getWizard()));
			PDBNameText.setEnabled(DBWizardInputs.isPDBNameTextEnabled(this.getWizard()));
			dbaUserText.setText(DBWizardInputs.getDbaUserName(this.getWizard()));
		}
		
		count++;
		
		
		// dba password label
		dbaPasswordLabel = new Label(container, SWT.NONE);
		dbaPasswordLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		dbaPasswordLabel.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.dbaPassword.label")); //$NON-NLS-1$
	//	dbaPasswordLabel.setVisible(false);
		
		//dba password input
		dbaPasswordText = TextHelper.newAutoValidationText(container, SWT.BORDER | SWT.PASSWORD);
		dbaPasswordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		if(!(DBWizardInputs.getDbType(this.getWizard()).equalsIgnoreCase("oracle")))
		
		
		{
			oraComponentSetVisible(false);
		}else{
			oraComponentSetVisible(true);
		}
		
	
   	//	dbaPasswordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
    //  dbaPasswordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

*/
//#Oracle12C_PDB_disable#  END	
	
	
		String installDir = DBWizardInputs.getDbInstallDir(this.getWizard());
		String dbType = DBWizardInputs.getDbType(this.getWizard());
		logger.info("performSelection(Widget widget) the getSelect Button id is: " +dbType);
		String instance = DBWizardInputs.getDbInstanceName(this.getWizard());
//#Oracle12C_PDB_disable#		String PDBName = DBWizardInputs.getPDBNameValue(this.getWizard());
//#Oracle12C_PDB_disable#		String dbaPassword = DBWizardInputs.getDbaPasswordValue(this.getWizard());
//#Oracle12C_PDB_disable#		String dbaUser = DBWizardInputs.getDbaUserName(this.getWizard());
         
		setDir(installDir);
		setInstance(instance);
//#Oracle12C_PDB_disable#		setPDBName(PDBName);
//#Oracle12C_PDB_disable#		setDbaPassword(dbaPassword);
//#Oracle12C_PDB_disable#		setDbaUser(dbaUser);
		
		// if OS is non-win, the sqlserver must be disabled. But the session may
		// keep sqlserver as
		// the default selection. So change from sqlserver to db2 as default.
		if (!Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			if (Constants.DB_SQLSERVER.equals(dbType))
				dbType = Constants.DB_DB2;
		}

		// If there is no session, then the first detected database should be
		// selected by default.
		// If no database is deceted, select db2 as default.
		// if there is session, select the session as default.
		ProductInfo pi = detectedDBMap.get(dbType);
		if (null != pi && pi.getInstallLoc() != null) {
			setDbType(dbType);
		} else {
			ProductInfo pin = detectedDBMap.get(Constants.DB_DB2);
			if (null != pin && pin.getInstallLoc() != null) {
				setDbType(Constants.DB_DB2);
				//#Oracle12C_PDB_disable#				oraComponentSetVisible(false);
			} else {
				ProductInfo pinf = detectedDBMap.get(Constants.DB_ORACLE);
				if (null != pinf && pinf.getInstallLoc() != null) {
					setDbType(Constants.DB_ORACLE);
					//#Oracle12C_PDB_disable#					oraComponentSetVisible(true);
				} else {
					if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
						ProductInfo pinfo = detectedDBMap.get(Constants.DB_SQLSERVER);
						if (null != pinfo && pinfo.getInstallLoc() != null) {
							setDbType(Constants.DB_SQLSERVER);
							//#Oracle12C_PDB_disable#							oraComponentSetVisible(false);
						} else {
							setDbType(dbType);
						}
					} else {
						setDbType(dbType);
					}
				}
			}
		}
	}

	public void performSelection(Widget widget) {
		String id = GroupUtil.getButtonId(GroupUtil.getSelectedButton(group, true));
		if(id.equalsIgnoreCase("oracle")){	
			//#Oracle12C_PDB_disable#			oraComponentSetVisible(true);	
		}else{
			//#Oracle12C_PDB_disable#			oraComponentSetVisible(false);	
		}
	//	DBWizardInputs.setDbaUserName(getWizard(),dbaUserText.getText());
	//	DBWizardInputs.setRunAsSYSDBA(getWizard(),isRunAsSYSDBAButton.getSelection());
	//	DBWizardInputs.setPDBNameSelection(getWizard(),PDBNameButton.getSelection());
	//#Oracle12C_PDB_disable#
	/*	
		PDBNameButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				PDBNameText.setEnabled(PDBNameButton.getSelection());
				if(!PDBNameButton.getSelection()){
					PDBNameText.setText("");
				}
			}
		});
	*/	
		performSelection(id);
	}

	private void performSelection(String id) {
		String oldType = this.dbType;
		this.dbType = id;
	//	DBWizardInputs.setDbType(this.getWizard(), this.dbType);
	//	DBWizardInputs.setRunAsSYSDBA(getWizard(),isRunAsSYSDBAButton.getSelection());
	//	DBWizardInputs.setPDBNameSelection(getWizard(),PDBNameButton.getSelection());
	//	DBWizardInputs.setPDBNameTextEnabled(this.getWizard(),PDBNameText.getEnabled());
	//	DBWizardInputs.setDbaUserName(getWizard(),dbaUserText.getText());
//		logger.info("now isRunAsSysdba" + DBWizardInputs.isRunAsSYSDBA(this.getWizard()));
//#Oracle12C_PDB_disable#		PDBNameButton.addSelectionListener(new SelectionAdapter() {
//#Oracle12C_PDB_disable#			public void widgetSelected(final SelectionEvent e) {
//#Oracle12C_PDB_disable#				PDBNameText.setEnabled(PDBNameButton.getSelection());	
//#Oracle12C_PDB_disable#			}
//#Oracle12C_PDB_disable#		});

		// populate detected database installation loc
		ProductInfo pi = detectedDBMap.get(this.dbType);
		if (null != pi) {
			this.dbLocationText.setText(pi.getInstallLoc() != null ? pi.getInstallLoc() : "");

			// populate detected database instance
			if (!CommonHelper.equals(this.dbType, oldType)) {
				if (CommonHelper.equals(this.dbType, Constants.DBMS_DB2)){
					//#Oracle12C_PDB_disable#					oraComponentSetVisible(false);
					this.dbInstanceText.setText(System.getenv("DB2INSTANCE") == null ? "" : System.getenv("DB2INSTANCE"));
				}
					
				if (CommonHelper.equals(this.dbType, Constants.DBMS_ORACLE)){
					this.dbInstanceText.setText(System.getenv("ORACLE_SID") == null ? "" : System.getenv("ORACLE_SID"));
					//#Oracle12C_PDB_disable#					oraComponentSetVisible(true);
				 	}
				if (CommonHelper.equals(this.dbType, Constants.DBMS_SQLSERVER)){
					//#Oracle12C_PDB_disable#					oraComponentSetVisible(false);
					this.dbInstanceText.setText("\\");
				}
					
			}
		} else {
			if (!CommonHelper.equals(this.dbType, oldType)) {
				this.dbLocationText.setText("");
				this.dbInstanceText.setText("");
			}
		}
		DBWizardInputs.setDbType(this.getWizard(), getDbType());
	}

	public IWizardPage getPreviousPage() {
		this.setDir(this.getDir());
		this.setInstance(this.getDbInstance());
		//#Oracle12C_PDB_disable#		oraComponentSetValues();
		return super.getPreviousPage();
		
	}
	
	public IWizardPage getNextPage() {
		//#Oracle12C_PDB_disable#		oraComponentSetValues();
		return super.getNextPage();
	}
	
	public void updateBeforeShown() {
		//#Oracle12C_PDB_disable#		oraComponentSetValues();
	}

//#Oracle12C_PDB_disable#	BEGIN
/*
	public void oraComponentSetVisible(boolean bool){
		PDBNameButton.setVisible(bool);
		PDBNameText.setVisible(bool);
		dbaPasswordLabel.setVisible(bool);
		dbaPasswordText.setVisible(bool);
		dbaUserLabel.setVisible(bool);
		isRunAsSYSDBAButton.setVisible(bool);
		dbaUserText.setVisible(bool);
	}
	
	public void oraComponentSetValues(){
		DBWizardInputs.setRunAsSYSDBA(getWizard(),isRunAsSYSDBAButton.getSelection());
		DBWizardInputs.setPDBNameSelection(getWizard(),PDBNameButton.getSelection());
		DBWizardInputs.setPDBNameTextEnabled(getWizard(),PDBNameText.getEnabled());
		DBWizardInputs.setDbaUserName(getWizard(),dbaUserText.getText());
		DBWizardInputs.setPDBNameValue(getWizard(), PDBNameText.getText());
		DBWizardInputs.setDbaPasswordValue(getWizard(),dbaPasswordText.getText());
	}
*/	
//#Oracle12C_PDB_disable#	END
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.lconn.wizard.common.ui.Validator#isValid(org.eclipse.swt.widgets
	 * .Widget)
	 */
	public boolean isInputValid() {
		if (DBWizardInputs.isExportOnly(this.getWizard())){
			this.setDir(this.getDir());
			this.setInstance(this.getDbInstance());
			if (null == detectedDBMap.get(dbType)){
				this.setDbVersion("");
			}else{
				this.setDbVersion(detectedDBMap.get(dbType).getVersion());
			}
			boolean result = true;
			return result;
		}
		else{
			this.setDir(this.getDir());
			this.setInstance(this.getDbInstance());
//#Oracle12C_PDB_disable#			this.setPDBName(this.getPDBNameText());
//#Oracle12C_PDB_disable#			this.setDbaPassword(this.getDbaPasswordText());
			if (null == detectedDBMap.get(dbType)){
				this.setDbVersion("");
			}else{
				this.setDbVersion(detectedDBMap.get(dbType).getVersion());
			}
			boolean result = true;
			String dbType = this.getDbType();
			String dbInstallDir = this.getDir();
//#Oracle12C_PDB_disable#			String PDBName = this.getPDBNameText();
//#Oracle12C_PDB_disable#			String SYSPassword = this.getDbaPasswordText();

//#Oracle12C_PDB_disable#			Checker DC = Checker.getChecker(dbInstallDir, CommonHelper.getPlatformType(), dbType,PDBName,SYSPassword);
			Checker DC = Checker.getChecker(dbInstallDir, CommonHelper.getPlatformType(), dbType);

			
			
         
			if (DBWizardInputs.isExportOnly(this.getWizard())) {
				result = true;
			}else if (CommonHelper.isEmpty(dbInstallDir)) {
				showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.empty.db.location"));
				result = false;
				this.dbLocationText.setFocus();
			}
        
			if (DBWizardInputs.isExportOnly(this.getWizard())) {
				result = true;
			} else if (!CommonHelper.isEmpty(dbInstallDir) && !DC.validateInstallLoc(this.getDbVersion())) {
				showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.location"));
				result = false;
				this.dbLocationText.setFocus();
			}

			if (result) {
				// validate the input of database instance.
				String dbInstanceName = this.getDbInstance();
				if (DBWizardInputs.isExportOnly(this.getWizard())) {
					result = true;
				} else if (CommonHelper.isEmpty(dbInstanceName)) {
					showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.empty.db.instance"));
					result = false;
					this.dbInstanceText.setFocus();
				}

				if (DBWizardInputs.isExportOnly(this.getWizard())) {
					result = true;
				} else if (!CommonHelper.isEmpty(dbInstanceName)) {
					DC.setInstance(dbInstanceName);
					if (!DC.validateInstance(this.getDbVersion())) {
						showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.instanceOrUser"));
						result = false;
						this.dbInstanceText.setFocus();
					}
				}
			}
			return result;
		}		
	}

	public void isInputValid_fake() {
		this.setDir(this.getDir());
		this.setInstance(this.getDbInstance());
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.empty.db.location"));
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.location"));
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.empty.db.instance"));
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.instanceOrUser"));
	}

	private String getAvailableDbType() {

		if (detectedDBMap.get(Constants.DBMS_DB2).getInstallLoc() != null) {
			return Constants.DBMS_DB2;
		}

		if (detectedDBMap.get(Constants.DBMS_ORACLE).getInstallLoc() != null) {
			return Constants.DBMS_ORACLE;
		}

		if (detectedDBMap.get(Constants.DBMS_SQLSERVER).getInstallLoc() != null) {
			return Constants.DBMS_SQLSERVER;
		}

		return Constants.DBMS_DB2;
	}

	private void setDbType(String dbType) {
		if (CommonHelper.isEmpty(dbType))
			dbType = getAvailableDbType();
		GroupUtil.setSelectedButton(group, dbType, true, true, true);
		performSelection(dbType);
	}
	private void setDbVersion(String dbVersion) {
		if (CommonHelper.isEmpty(dbType))
			dbVersion = "";
		DBWizardInputs.setDbVersion(this.getWizard(), dbVersion);
		this.dbVersion = dbVersion;
	}

	private void setDir(String installDir) {
		if (CommonHelper.isEmpty(installDir))
			installDir = "";
		dbLocationText.setText(installDir.trim());
		DBWizardInputs.setDbInstallDir(this.getWizard(), installDir.trim());
	}

	private void setInstance(String instance) {
		DBWizardInputs.setDbInstanceName(this.getWizard(), instance);
		if (CommonHelper.isEmpty(instance))
			instance = "";
		/*
		 * else if (instance.startsWith("\\") &&
		 * Constants.DB_SQLSERVER.equals(this.dbType)) instance =
		 * instance.substring(1);
		 */
		dbInstanceText.setText(instance);
	}

//#Oracle12C_PDB_disable#  BEGIN
/*	
	private void setDbaUser(String dbaUser) {
		DBWizardInputs.setDbaUserName(this.getWizard(), dbaUser);
		dbaUserText.setText(dbaUser);
	}
	
	private void setPDBName(String PDBName) {
		DBWizardInputs.setPDBNameValue(this.getWizard(), PDBName);
		if (CommonHelper.isEmpty(PDBName))
			PDBName = "";
		
		PDBNameText.setText(PDBName);
	}
	
	
	private void setDbaPassword(String dbaPassword) {
		DBWizardInputs.setDbaPasswordValue(this.getWizard(), dbaPassword);
		if (CommonHelper.isEmpty(dbaPassword))
			dbaPassword = "";
		
		dbaPasswordText.setText(dbaPassword);
	}
*/
//#Oracle12C_PDB_disable#	END
	
	public String getDbType() {
		String value = GroupUtil.getButtonId(GroupUtil.getSelectedButton(group, true));
		return value;
	}

	private String getDir() {
		return dbLocationText.getText().trim();
	}

	private String getDbInstance() {
		String instance = dbInstanceText.getText();
		/*
		 * if (Constants.DB_SQLSERVER.equals(this.dbType)) instance = "\\" +
		 * instance;
		 */
		return instance;
	}

	

//#Oracle12C_PDB_disable#	BEGIN
/*
	private  String getPDBNameText() {
		String PDBName = PDBNameText.getText().trim();
		return PDBName;
	}
	
	private  String getDbaPasswordText() {
		String dbaPassword = dbaPasswordText.getText().trim();
		return dbaPassword;
	}
*/
//#Oracle12C_PDB_disable#	END	
	
	
	private String getDbVersion() {
		return this.dbVersion;
	}
}
