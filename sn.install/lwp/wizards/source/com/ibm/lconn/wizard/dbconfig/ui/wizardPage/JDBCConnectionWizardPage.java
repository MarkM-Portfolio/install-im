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

package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;
import com.ibm.lconn.wizard.common.DefaultValue;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.backend.Checker;
import com.ibm.lconn.wizard.dbconfig.backend.UpgradePath;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class JDBCConnectionWizardPage extends CommonPage {
	private final static Logger logger = LogUtil.getLogger(JDBCConnectionWizardPage.class);

	private final static String DB_CONN_VALIDATOR_MAIN = "com.ibm.lconn.wizard.common.validator.DbConnectionValidatorMain";

	private Text hostNameInput, portInput, dbNameInput, jdbcLibPathInput, dbAdminInput, adminPwdInput;

	private String port, jdbcLibPath, dbAdmin, adminPwd, dbType;

	protected JDBCConnectionWizardPage() {
		super(Constants.WIZARD_PAGE_JDBC_CONNECTION_INFO);
		setTitle(Messages.getString("dbWizard.JDBCConnectionWizardPage.title")); //$NON-NLS-1$
	}

	@Override
	public void onShow(Composite parent) {
		List<String> features = DBWizardInputs.getFeatures(getWizard());
		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard()))){
//			if(features.contains(Constants.FEATURE_FORUM)){
//				features.remove(Constants.FEATURE_FORUM);	
//			}
			if(features.contains(Constants.FEATURE_ACTIVITIESTHEME)){
				features.remove(Constants.FEATURE_ACTIVITIESTHEME);	
			}
			if(features.contains(Constants.FEATURE_BLOGSTHEME)){
				features.remove(Constants.FEATURE_BLOGSTHEME);	
			}
			if(features.contains(Constants.FEATURE_WIKISTHEME)){
				features.remove(Constants.FEATURE_WIKISTHEME);	
			}
		}
		DBWizardInputs.setFeatures(getWizard(), features);
		Composite container = CommonHelper.createScrollableControl(Composite.class, parent, SWT.NONE, SWT.V_SCROLL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;

		container.setLayout(gridLayout);

		Label dec = new Label(container, SWT.WRAP);
		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		dec.setText(Messages.getString("dbWizard.JDBCConnectionWizardPage.description")); //$NON-NLS-1$

		final Label dbNameLabel = new Label(container, SWT.NONE);
		dbNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		dbNameLabel.setText(Messages.getString("dbWizard.JDBCConnectionWizardPage.name.label"));

		dbNameInput = new Text(container, SWT.BORDER);
		dbNameInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		final Label hostnameLabel = new Label(container, SWT.NONE);
		hostnameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		hostnameLabel.setText(Messages.getString("dbWizard.JDBCConnectionWizardPage.hostname.label"));

		hostNameInput = new Text(container, SWT.BORDER);
		hostNameInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		final Label portLabel = new Label(container, SWT.NONE);
		portLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		portLabel.setText(Messages.getString("dbWizard.JDBCConnectionWizardPage.port.label"));

		portInput = new Text(container, SWT.BORDER);
		portInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		if (Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(getWizard()))) {
			// Only SQL server needs explicitly specify the jdbc library jar
			final Label jdbcLibPathLabel = new Label(container, SWT.NONE);
			jdbcLibPathLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			jdbcLibPathLabel.setText(Messages.getString("dbWizard.JDBCConnectionWizardPage.jdbclibpath.label"));

			jdbcLibPathInput = new Text(container, SWT.BORDER);
			jdbcLibPathInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

			final Button jdbcLibPathBrowse = new Button(container, SWT.NONE);
			jdbcLibPathBrowse.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			jdbcLibPathBrowse.setText(Messages.getString("dbWizard.dbTypeSelectionWizardPage.button.browse"));

			jdbcLibPathBrowse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent e) {
					FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell());
					fileDialog.setFilterExtensions(new String[] { Constants.JAR_FILE_EXTENSION });
					if (jdbcLibPathInput.getText() != null) {
						fileDialog.setFileName(jdbcLibPathInput.getText());
					}
					String filepath = fileDialog.open();
					if (filepath != null) {
						jdbcLibPathInput.setText(filepath);
					}
				}
			});
		}

		final Label dbAdminLabel = new Label(container, SWT.NONE);
		dbAdminLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		dbAdminLabel.setText(Messages.getString("dbWizard.JDBCConnectionWizardPage.dbadmin.label"));

		dbAdminInput = new Text(container, SWT.BORDER);
		dbAdminInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		final Label adminPwdLabel = new Label(container, SWT.NONE);
		adminPwdLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		adminPwdLabel.setText(Messages.getString("dbWizard.JDBCConnectionWizardPage.adminpwd.label"));

		adminPwdInput = new Text(container, SWT.BORDER | SWT.PASSWORD);
		adminPwdInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		// For forum content store.
		List<String> selectedFeatures = DBWizardInputs.getFeatures(getWizard());

		if (selectedFeatures.contains(Constants.FEATURE_COMMUNITIES)) {
			this.setNextPage(Pages.getPage(Pages.CONTENT_MIGRATE_TASK));
		} else {
			this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
		}

		initValue();
	}

	private void initValue() {
		if(null == DBWizardInputs.getDbHostName(getWizard()) || "".equals(DBWizardInputs.getDbHostName(getWizard()))){
			hostNameInput.setText(Constants.LOCALHOST);
		}else{
			hostNameInput.setText(DBWizardInputs.getDbHostName(getWizard()));
		}
		hostNameInput.setEditable(true);

		if (DBWizardInputs.getDbType(getWizard()).equals(dbType)) {
			// db type is not changes, restore saved input
			portInput.setText(port);
			if (jdbcLibPathInput != null) {
				jdbcLibPathInput.setText(jdbcLibPath);
			}
			dbAdminInput.setText(dbAdmin);
			adminPwdInput.setText(adminPwd);
		} else {
			dbType = DBWizardInputs.getDbType(getWizard());
			portInput.setText(DefaultValue.getDatabasePort(dbType));
			dbAdminInput.setText(DefaultValue.getDatabaseAdmin(dbType));
		}

		if (Constants.DB_ORACLE.equals(dbType)) {
			dbNameInput.setText(DBWizardInputs.getDbInstanceName(getWizard()));
		} else {
			
			List<String> list = UpgradePath.containJava(DBWizardInputs.getDbType(this.getWizard()), 
					DBWizardInputs.getDbVersion(this.getWizard()),
					DBWizardInputs.getFeatures(this.getWizard()), 
					DBWizardInputs.getDbUpgradeVersion(this.getWizard()));
//			if (DBWizardInputs.getFeatures(this.getWizard()).contains(Constants.FEATURE_COMMUNITIES)){
//				list.add(Constants.FEATURE_COMMUNITIES);
//			}
			String dbName = "";			
			for (String feature : list) {
				dbName = dbName + ", " + Constants.featureDBMapping.get(dbType).get(feature);
			}
			if (!"".equals(dbName)) {
				dbName = dbName.substring(2);
			}
			dbNameInput.setText(dbName);
		}
		dbNameInput.setEditable(false);
	}

	public IWizardPage getNextPage() {
		BusyIndicator.showWhile(getWizard().getShell().getDisplay(), new Runnable() {
			public void run() {
				DBWizardInputs.setJdbcNeeded(getWizard(), true);
				saveInputs();
			}
		});
		this.setNextPage(Pages.getPage(Pages.SUMMARY_PAGE));
		return super.getNextPage();
	}

	public IWizardPage getPreviousPage() {
		saveInputs();
		DBWizardInputs.setJdbcNeeded(getWizard(), false);
		return super.getPreviousPage();
	}

	private void saveInputs() {
		port = portInput.getText().trim();
		jdbcLibPath = jdbcLibPathInput == null ? null : jdbcLibPathInput.getText().trim();
		dbAdmin = dbAdminInput.getText().trim();
		adminPwd = adminPwdInput.getText().trim();
		dbType = DBWizardInputs.getDbType(getWizard());

		// save value to datapool
		DBWizardInputs.setDbHostName(getWizard(), hostNameInput.getText());
		DBWizardInputs.setDbPort(getWizard(), port);
		DBWizardInputs.setJDBCDbName(getWizard(), dbNameInput.getText());
		DBWizardInputs.setJDBCLibPath(getWizard(), getJdbcLibraryJars());
		DBWizardInputs.setDbAdminName(getWizard(), dbAdmin);
		DBWizardInputs.setDbAdminPassword(getWizard(), adminPwd);
	}

	public void isInputValid_fake() {
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.port"));
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.jdbclib.not.exist"));
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.dbadmin"));
		showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.adminpwd"));
		showInputCheckErrorMsg(Messages.getString("validator.cannot_validation_process.message"));

		String[] dbs = dbNameInput.getText().split(",");
		for (String dbName : dbs) {
			dbName = dbName.trim();
			String dbUrl = DatabaseUtil.getDBUrl(dbType, hostNameInput.getText(), port, dbName);
			String jdbcDriver = DatabaseUtil.getJDBCDriver(dbType);
			showInputCheckErrorMsg(Messages.getString("validator.no_jdbc_driver.message", jdbcDriver));
			showInputCheckErrorMsg(Messages.getString("validator.communication_error.message", hostNameInput.getText(), port));
			showInputCheckErrorMsg(Messages.getString("validator.db_not_exist.message", dbNameInput.getText()));
			showInputCheckErrorMsg(Messages.getString("validator.invalid_authentication.message"));
			showInputCheckErrorMsg(Messages.getString("validator.cannot_connect_db.message", dbUrl));
			showInputCheckErrorMsg(Messages.getString("validator.cannot_validation_process.message"));

		}
	}

	protected boolean isInputValid() {
		// vaidate port
		int i = -1;
		try {
			i = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			// ignore
		}
		if (i < 1 || i > 65535) {
			showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.port"));
			portInput.setFocus();
			return false;
		}
		// validate jdbc library path if database is sqlserver
		if (Constants.DB_SQLSERVER.equals(dbType)) {
			if (jdbcLibPath == null || !new File(jdbcLibPath).exists()) {
				showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.jdbclib.not.exist"));
				jdbcLibPathInput.setFocus();
				return false;
			}
		}

		// validate administrator name
		if (dbAdmin == null || "".equals(dbAdmin.trim())) {
			showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.dbadmin"));
			dbAdminInput.setFocus();
			return false;
		}

		// validate administrator password
		if (adminPwd == null || "".equals(adminPwd.trim())) {
			showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.invalid.db.adminpwd"));
			adminPwdInput.setFocus();
			return false;
		}
		// validate database connection
		String[] dbs = dbNameInput.getText().split(",");
		for (String dbName : dbs) {
			if (!validateJDBCConnection(dbName.trim())) {
				return false;
			}
		}

		return true;
	}

	private boolean validateJDBCConnection(String dbName) {
		String javaExecutable = DefaultValue.getDefaultJREPath() + Constants.FS + "bin" + Constants.FS;
		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformTypeForJRE())) {
			javaExecutable = javaExecutable + Constants.JAVA_EXECUTABLE_WIN;
		} else {
			javaExecutable = javaExecutable + Constants.JAVA_EXECUTABLE;
		}
		String classpath = getJdbcLibraryJars() + Constants.PATH_SEPARATOR + Constants.WIZARD_JAR_LOC;

		ProcessBuilder pb = new ProcessBuilder(javaExecutable, "-classpath", classpath, DB_CONN_VALIDATOR_MAIN, dbType, hostNameInput.getText(), port, dbName, dbAdmin, adminPwd);
		List<String> cmds = new ArrayList<String>();
		for (String cmd : pb.command()) {
			cmds.add(cmd);
		}
		cmds.set(cmds.size() - 1, "******");
		logger.log(Level.FINER, "validator.finer.db_validation_process_command", cmds);
		String dbUrl = DatabaseUtil.getDBUrl(dbType, hostNameInput.getText(), port, dbName);
		logger.log(Level.INFO, "validator.info.db_url", dbUrl);
		String jdbcDriver = DatabaseUtil.getJDBCDriver(dbType);
		logger.log(Level.INFO, "validator.info.jdbc_driver", jdbcDriver);
		Process p = null;

		try {
			p = pb.start();
			p.waitFor();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "dbpopulation.severe.db_validation_process_fail", e);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "dbpopulation.severe.db_validation_process_fail", e);
		}

		if (p == null) {
			showInputCheckErrorMsg(Messages.getString("validator.cannot_validation_process.message"));
			return false;
		}
		int i;

		logger.log(Level.FINER, "validator.finer.process_exit_value", new Integer(p.exitValue()));

		i = p.exitValue();

		switch (i) {
		case 0:
			return true;
		case Constants.ERROR_NO_JDBC_DRIVER:
			showInputCheckErrorMsg(Messages.getString("validator.no_jdbc_driver.message", jdbcDriver));
			break;
		case Constants.ERROR_COMMUNICATION_ERROR:
			showInputCheckErrorMsg(Messages.getString("validator.communication_error.message", hostNameInput.getText(), port));
			portInput.setFocus();
			break;
		case Constants.ERROR_DB_NOT_EXIST:
			showInputCheckErrorMsg(Messages.getString("validator.db_not_exist.message", dbNameInput.getText()));
			break;
		case Constants.ERROR_INVALID_AUTHENTICATION:
			showInputCheckErrorMsg(Messages.getString("validator.invalid_authentication.message"));
			dbAdminInput.setFocus();
			break;
		case Constants.ERROR_UNKNOWN:
			showInputCheckErrorMsg(Messages.getString("validator.cannot_connect_db.message", dbUrl));
			break;
		default:
			showInputCheckErrorMsg(Messages.getString("validator.cannot_validation_process.message"));
		}

		return false;
	}

	private String getJdbcLibraryJars() {
		String dbInstallLoc = DBWizardInputs.getDbInstallDir(getWizard());

		if (Constants.DB_SQLSERVER.equals(dbType)) {
			return jdbcLibPath;
		} else {
			return DefaultValue.getJDBCLibraryPath(dbType, dbInstallLoc);
		}
	}

}
