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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.DB2ConfigProp;
import com.ibm.lconn.wizard.dbconfig.interfaces.DbCreationInterface;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardDialog;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.dbconfig.ui.event.listener.SummaryWizardPageCheckBoxListener;
import com.ibm.lconn.wizard.tdipopulate.backend.JDBCConnectionInfo;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.OperationOptionWizardPage;

public class SummaryWizardPage extends CommonPage {
	private int[] line = new int[Constants.FEATURE_COUNT_MAX];
	private int num = 0;
	private static String SPACE = " ";
	private Button button;
	private int count = 0;
	
	private DbCreationInterface DBExe;
//	private String logPath;
//	public static final Logger logger = LogUtil.getLogger(ExecutionWizardPage.class);

	//TODO modify by Maxi, feature merge flag (librarygcd, libraryos) -> library
	private boolean flagLibraryMerged = false;
	//TODO end

	/**
	 * Create the wizard
	 */
	public SummaryWizardPage() {
		super(Constants.WIZARD_PAGE_DB_SUMMARY);
	}

	protected void updateDialogButtons() {
		DBWizard wizard = this.getWizard();
		DBWizardDialog parentDialog = wizard.getParentDialog();
		try {
			Object o = parentDialog.getTheFinishButton().getLayoutData();
			GridData g = (GridData) o;
			g.exclude = true;
			parentDialog.getTheFinishButton().setVisible(false);

			parentDialog.getTheCancelButton().setText(Messages.getString("button.cancel.text"));
			parentDialog.getTheCancelButton().setEnabled(true);
			parentDialog.getThePrevButton().setText(Messages.getString("button.prev.text"));

			String actionId = DBWizardInputs.getActionId(wizard);
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(actionId)) {
				parentDialog.getTheNextButton().setText(Messages.getString("button.create.text"));
			}

			if (Constants.OPERATION_TYPE_DROP_DB.equals(actionId)) {
				parentDialog.getTheNextButton().setText(Messages.getString("button.delete.text"));
			}

			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
				parentDialog.getTheNextButton().setText(Messages.getString("button.upgrade.text"));
			}

		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.lconn.wizard.dbconfig.ui.wizardPage.CommonPage#onShow(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	public void onShow(Composite parent) {
		this.setTitle(Messages.getString("dbWizard.summaryWizardPage.title"));
		
		DBExe = DBWizard.getDBCreationInterface();
//		logPath = DBExe.createLogForTask();

		String text = getShowText();
		int[] bold = new int[this.num];
		for (int i = 0; i < this.num; i++) {
			bold[i] = this.line[i];
		}
		
		if(DBWizardInputs.isExportOnly(this.getWizard())){
				String dec = Messages.getString("dbWizard.summaryWizardPage.description.exportOnly") + Constants.UI_LINE_SEPARATOR;	
				new BIDIStyledText(parent, dec, text, null, null, bold);
		}else{
		     	String dec = Messages.getString("dbWizard.summaryWizardPage.description." + DBWizardInputs.getActionId(this.getWizard())) + Constants.UI_LINE_SEPARATOR;
		     	new BIDIStyledText(parent, dec, text, null, null, bold);
		     }
		
		
		
//		final Label space = new Label(parent, SWT.NONE);
//		space.setText("\n\n");
		button = new Button(parent, SWT.CHECK);
		button.setText(Messages.getString("dbWizard.summaryWizardPage.button.check"));		
		button.addSelectionListener(new SummaryWizardPageCheckBoxListener(this));
		
		if(count==0){
			button.setSelection(true);
			DBWizardInputs.setNannyMode(this.getWizard(),button.getSelection());
		}else{
			button.setSelection(DBWizardInputs.isNannyMode(this.getWizard()));
		}
		
		DBWizardInputs.setFeaturesCommandsMap(this.getWizard(), getExecuteCommands());
		
		DBWizardInputs.setSQLScriptsMap(this.getWizard(), getExecuteSQLScripts());
		
		count++;
	}

	private String getShowText() {
		int line = 0;
		this.num = 0;
		DBWizard wizard = this.getWizard();
		String actionId = DBWizardInputs.getActionId(wizard);
		String dbTypeId = DBWizardInputs.getDbType(wizard);
		String dbInstallDir = DBWizardInputs.getDbInstallDir(wizard);
		String dbInstanceName = DBWizardInputs.getDbInstanceName(wizard);
	//	String PDBNameTemp=DbTypeSelectionWizardPage.getPDBNameText();
		if (CommonHelper.isEmpty(dbInstanceName))
			dbInstanceName = "";
		else if (!dbInstanceName.equals("\\") && dbInstanceName.startsWith("\\") && Constants.DB_SQLSERVER.equals(dbTypeId))
			dbInstanceName = dbInstanceName.substring(1);
		List<String> features = DBWizardInputs.getFeatures(wizard);
		
		
//#Oracle12C_PDB_disable#		String PDBNameValue = DBWizardInputs.getPDBNameValue(wizard);

		
		
		//**************jia**********

		
//		if(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard()))
//				&& features.contains(Constants.FEATURE_COMMUNITIES)){
//			if(features.contains(Constants.FEATURE_ACTIVITIES) && !features.contains(Constants.FEATURE_ACTIVITIESTHEME)){
//				features.add(Constants.FEATURE_ACTIVITIESTHEME);	
//			}
//			if(features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_BLOGSTHEME)){
//				features.add(Constants.FEATURE_BLOGSTHEME);	
//			}
//			if(features.contains(Constants.FEATURE_WIKIS) && !features.contains(Constants.FEATURE_WIKISTHEME)){
//				features.add(Constants.FEATURE_WIKISTHEME);	
//			}
//			DBWizardInputs.setFeatures(wizard, features);
//		}
		Map<String, String> Path = DBWizardInputs.getCreateDbFilePath(wizard);

		StringBuffer sb = new StringBuffer();
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part1")).append(SPACE);
		sb.append(Messages.getString("dbWizard.action.db." + actionId + ".name"));
		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part2")).append(SPACE);
		sb.append(Messages.getString(dbTypeId + ".name"));
		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part3")).append(SPACE);
		sb.append(dbInstallDir);
		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part4")).append(SPACE);
		sb.append(dbInstanceName);
		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part5"));
		for (String feature : features) {
			if(feature.indexOf("theme") == -1){
				//TODO modify by Maxi, special action of library_gcd and library_os
				if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
					if (!flagLibraryMerged) {
						sb.append(" ").append(Messages.getString(Constants.FEATURE_LIBRARY + ".name")).append(",");
						flagLibraryMerged = true;
					}
					continue;
				}
				//TODO
				sb.append(" ").append(Messages.getString(feature + ".name")).append(",");
			}		
		}
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
		sb.deleteCharAt(sb.length() - 1);
		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;
		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;
		//TODO modify by Maxi, temp merge flag for lable info
		boolean flagTempMerged = false;
		//TODO end
		for (String feature : features) {
			if(feature.indexOf("theme") == -1){
				
			//TODO modify by Maxi, can not upgrade feature library from old version
			if ((Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) 
					&& Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId))
				continue;
			//TODO end

			this.line[this.num] = line;
			//// TODO modify by Maxi, special check for feature library
			if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
				if (!flagTempMerged) {
					sb.append(Messages.getString(Constants.FEATURE_LIBRARY + ".name"));
					this.num++;

					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;
					flagTempMerged = true;
				}
			} else {
				sb.append(Messages.getString(feature + ".name"));
				this.num++;

				sb.append(Constants.UI_LINE_SEPARATOR);
				line++;
			}
			//TODO end

			// TODO modify by Maxi, special check for feature library
			if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
				if (!flagLibraryMerged) {
					sb.append(
							Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1"))
							.append(SPACE);
					if (Constants.DB_ORACLE.equals(dbTypeId)) {
					
//#Oracle12C_PDB_disable#						if (CommonHelper.isEmpty(PDBNameValue)){
							sb.append(dbInstanceName);
//#Oracle12C_PDB_disable#						}else{
//#Oracle12C_PDB_disable#						    sb.append(PDBNameValue);
//#Oracle12C_PDB_disable#						}
					} else {
						sb.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_GCD
										+ ".dbName." + dbTypeId));
					}
					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;
					sb.append(
							Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a"))
							.append(SPACE);
					sb.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_GCD + ".dbUserName." + dbTypeId));
					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;

					sb.append(
							Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_1"))
							.append(SPACE);
					List<String> elements = this.getExecuteSQLScripts().get(Constants.FEATURE_LIBRARY_GCD);
					boolean flagTmp = false;
					if (null != elements) {
						flagTmp = true;
						for (String element : elements) {
							sb.append(element).append(", ");
						}
					}
					if (Constants.DB_ORACLE.equals(dbTypeId)) {
						elements = this.getExecuteSQLScripts().get(Constants.FEATURE_LIBRARY_OS);
						if (null != elements) {
							flagTmp = true;
							for (String element : elements) {
								sb.append(element).append(", ");
							}
						}
					}
					if (flagTmp) {
						sb.deleteCharAt(sb.length() - 1);
						sb.deleteCharAt(sb.length() - 1);
					}
					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;

					//TODO modify by Maxi, special action for sqlserver
					if (dbTypeId.equals(Constants.DB_SQLSERVER)
							&& actionId.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
						sb.append(
								Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3"))
								.append(SPACE);
						if (DBWizardInputs.isCreateDbFilePathSame(wizard)) {
							sb.append(DBWizardInputs.getSameCreateDbFilePath(wizard))
									.append(Constants.UI_LINE_SEPARATOR);
							line++;
						} else {
							sb.append(Path.get(Constants.FEATURE_LIBRARY_GCD)).append(
									Constants.UI_LINE_SEPARATOR);
							line++;
						}
					}
					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;
					
					if (Constants.DB_ORACLE.equals(dbTypeId)) {
						
					} else {
						sb.append(
								Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1"))
								.append(SPACE);
						sb.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_OS
								+ ".dbName." + dbTypeId));
						sb.append(Constants.UI_LINE_SEPARATOR);
						line++;
						sb.append(
								Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a"))
								.append(SPACE);
						sb.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_OS + ".dbUserName." + dbTypeId));
						sb.append(Constants.UI_LINE_SEPARATOR);
						line++;
						sb.append(
								Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_1"))
								.append(SPACE);
						for (String element : this.getExecuteSQLScripts().get(Constants.FEATURE_LIBRARY_OS)) {
							sb.append(element).append(", ");
						}
						sb.deleteCharAt(sb.length() - 1);
						sb.deleteCharAt(sb.length() - 1);
						sb.append(Constants.UI_LINE_SEPARATOR);
						line++;
						//TODO modify by Maxi, special action for sqlserver
						if (dbTypeId.equals(Constants.DB_SQLSERVER)
								&& actionId.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
							sb.append(
									Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3"))
									.append(SPACE);
							if (DBWizardInputs.isCreateDbFilePathSame(wizard)) {
								sb.append(DBWizardInputs.getSameCreateDbFilePath(wizard))
										.append(Constants.UI_LINE_SEPARATOR);
								line++;
							} else {
								sb.append(Path.get(Constants.FEATURE_LIBRARY_OS)).append(
										Constants.UI_LINE_SEPARATOR);
								line++;
							}
						}
						sb.append(Constants.UI_LINE_SEPARATOR);
						line++;
					}
					flagLibraryMerged = true;
				}
				continue;
			}
			// TODO end!
			
			sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1")).append(SPACE);
			if (Constants.DB_ORACLE.equals(dbTypeId)) {
//#Oracle12C_PDB_disable#				if (CommonHelper.isEmpty(PDBNameValue)){
					sb.append(dbInstanceName);
//#Oracle12C_PDB_disable#				}else{
//#Oracle12C_PDB_disable#				    sb.append(PDBNameValue);
//#Oracle12C_PDB_disable#				}
			} else {
				sb.append(DB2ConfigProp.getProperty(feature + ".dbName." + dbTypeId));
			}
			sb.append(Constants.UI_LINE_SEPARATOR);
			line++;

			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {

				//This is for upgrade from 201 to 25
			/*	if (feature.equals(Constants.FEATURE_COMMUNITIES)) {
					sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_c")).append(SPACE).append(DBWizardInputs.getForumContentStorePath(wizard)).append(Constants.UI_LINE_SEPARATOR);
					;
					line++;
				}*/
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_b")).append(SPACE);
				Map<String, String> versions = DBWizardInputs.getDbUpgradeVersion(this.getWizard());
				String version = versions.get(feature);
				if (version.startsWith("1.0"))
					version = Constants.VERSION_10X;
				sb.append(version).append("->").append(Constants.VERSION_TOP);
				sb.append(Constants.UI_LINE_SEPARATOR);
				line++;
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_1")).append(SPACE);
				List<String> elements = this.getExecuteSQLScripts().get(feature);
				for(String element : elements){
//					if(element.indexOf("../../forum") != -1){
//						element = element.replace("../../", "");
//					}
					if(element.startsWith("$java")){
						continue;
					}
					sb.append(element).append(", ");
				}
				sb.deleteCharAt(sb.length()-1);		
				sb.deleteCharAt(sb.length()-1);
				sb.append(Constants.UI_LINE_SEPARATOR);
				line++;
				String element = "";
//				for(int i=1;i<elements.size()-1;i++){
//					element = elements.get(i);
//					if(element.startsWith("$java")){
						sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3")).append(SPACE);
//						break;
//					}
//				}
				for(int i=1;i<elements.size()-1;i++){
					element = elements.get(i);
					if(element.startsWith("$java")){
						int indexStart = element.lastIndexOf("lib") + "lib".length() + 1;
						int indexEnd = element.lastIndexOf("migrate.jar") + "migrate.jar".length();
						element = element.substring(indexStart,indexEnd);
								if(elements.get(i+1).startsWith("$java")){
									sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_1",element,elements.get(i-1),elements.get(i+2))).append(", ");
									i++;	
								}else{
						sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_1",element,elements.get(i-1),elements.get(i+1))).append(", ");	
								}
					}
				}
				sb.deleteCharAt(sb.length()-1);		
				for(int i=1;i<elements.size()-1;i++){
					element = elements.get(i);
					if(element.startsWith("$java")){
						sb.deleteCharAt(sb.length()-1);
						break;
					}
				}
			} else {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a")).append(SPACE);
				sb.append(DB2ConfigProp.getProperty(feature + ".dbUserName." + dbTypeId));
				sb.append(Constants.UI_LINE_SEPARATOR);
				line++;
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_1")).append(SPACE);
				for(String element : this.getExecuteSQLScripts().get(feature)){
					sb.append(element).append(", ");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append(Constants.UI_LINE_SEPARATOR);
			line++;

			if (dbTypeId.equals(Constants.DB_SQLSERVER) && actionId.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3")).append(SPACE);
				if (DBWizardInputs.isCreateDbFilePathSame(wizard)) {
					sb.append(DBWizardInputs.getSameCreateDbFilePath(wizard)).append(Constants.UI_LINE_SEPARATOR);
					line++;
				} else {
					sb.append(Path.get(feature)).append(Constants.UI_LINE_SEPARATOR);
					line++;
				}
			}
			sb.append(Constants.UI_LINE_SEPARATOR);
			line++;
				}

		}
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
//		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId) && (features.contains(Constants.FEATURE_COMMUNITIES) && 
//				(features.contains(Constants.FEATURE_ACTIVITIES) || features.contains(Constants.FEATURE_BLOGS) || features.contains(Constants.FEATURE_WIKIS)))) {
//			sb.append(getThemeStr(features));
//			sb.append(Constants.UI_LINE_SEPARATOR);
//			line++;
//			sb.append(Constants.UI_LINE_SEPARATOR);
//			line++;
//		}

		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			if (DBWizardInputs.isJdbcNeeded(wizard)) {
				String dbHost = DBWizardInputs.getDbHostName(getWizard());
				String dbPort = DBWizardInputs.getDbPort(getWizard());
				String dbAdmin = DBWizardInputs.getDbAdminName(getWizard());
				// jdbc connection information
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbHost")).append(SPACE);
				sb.append(dbHost);
				sb.append(Constants.UI_LINE_SEPARATOR);
				line++;
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbPort")).append(SPACE);
				sb.append(dbPort);
				sb.append(Constants.UI_LINE_SEPARATOR);
				line++;
				sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.dbAdmin")).append(SPACE);
				sb.append(dbAdmin);
				sb.append(Constants.UI_LINE_SEPARATOR);
				line++;
				if (Constants.DB_SQLSERVER.equals(dbTypeId)) {
					String jdbcLib = DBWizardInputs.getJDBCLibPath(getWizard());
					sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.connection.jdbcLib")).append(SPACE);
					sb.append(jdbcLib);
					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;
				}
			}

			/*
			 * if (features.contains(Constants.FEATURE_COMMUNITIES) &&
			 * DBWizardInputs
			 * .getDbVersion(this.getWizard()).get(Constants.FEATURE_COMMUNITIES
			 * ).startsWith("1.0")) {
			 * 
			 * // upgradeCommunitiesUuid task
			 * sb.append(Constants.UI_LINE_SEPARATOR); line++;
			 * 
			 * String upgradeCommunitiesUuid =
			 * DBWizardInputs.getEnableCommunitiesOptionalTask(getWizard()); sb
			 * .append(Messages.getString(
			 * "dbWizard.summaryWizardPage.show.text.upgradeCommunitiesUuid"
			 * )).append(SPACE); sb.append(Messages.getString(
			 * "dbWizard.CommunitiesOptionalTaskWizardPage.option." +
			 * upgradeCommunitiesUuid + ".label"));
			 * sb.append(Constants.UI_LINE_SEPARATOR); line++; }
			 */
		}

		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;
		sb.append(Constants.UI_LINE_SEPARATOR);
		line++;

		String buttonText = null;
		if (Constants.OPERATION_TYPE_CREATE_DB.equals(actionId)) {
			buttonText = Messages.getString("dbWizard.summaryWizardPage.button.create.text");
		}

		if (Constants.OPERATION_TYPE_DROP_DB.equals(actionId)) {
			buttonText = Messages.getString("dbWizard.summaryWizardPage.button.delete.text");
		}

		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			buttonText = Messages.getString("dbWizard.summaryWizardPage.button.upgrade.text");
		}
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.end", buttonText));
		return sb.toString();
	}
	
	private Map<String,List<String>> getExecuteSQLScripts() {
		Map<String,List<String>> map = new HashMap<String,List<String>>();

		try {
			DBWizard wizard = this.getWizard();
			String action = DBWizardInputs.getActionId(wizard);
			String dbType = DBWizardInputs.getDbType(wizard);
			String dbVersion = DBWizardInputs.getDbVersion(wizard);
			String dbInstallDir = DBWizardInputs.getDbInstallDir(wizard);
			String dbInstanceName = DBWizardInputs.getDbInstanceName(wizard);
//#Oracle12C_PDB_disable#			String PDBNameValue = DBWizardInputs.getPDBNameValue(wizard);
//#Oracle12C_PDB_disable#			String dbaPasswordValue = DBWizardInputs.getDbaPasswordValue(wizard);
//#Oracle12C_PDB_disable#			String dbaUserNameValue = DBWizardInputs.getDbaUserName(wizard);
//#Oracle12C_PDB_disable#			boolean isRunAsSYSDBA=DBWizardInputs.isRunAsSYSDBA(wizard);
		//	String dbaPasswordValue = DBWizardInputs.getDbaPasswordValue(wizard);
			List<String> features = DBWizardInputs.getFeatures(wizard);

			// suppports for java upgrade
			JDBCConnectionInfo connInfo = new JDBCConnectionInfo();
			connInfo.setHostName(DBWizardInputs.getDbHostName(wizard));
			connInfo.setPort(DBWizardInputs.getDbPort(wizard));
			connInfo.setDbName(DBWizardInputs.getJDBCDbName(wizard));
			connInfo.setJdbcLibPath(DBWizardInputs.getJDBCLibPath(wizard));
			connInfo.setUsername(DBWizardInputs.getDbAdminName(wizard));
			connInfo.setPassword(DBWizardInputs.getDbAdminPassword(wizard));
			connInfo.setDbType(dbType);
			connInfo.setContentStore(Constants.FEATURE_COMMUNITIES, DBWizardInputs.getForumContentStorePath(wizard));

			JDBCConnectionInfo profileConnInfo = new JDBCConnectionInfo();
			if (features.contains(Constants.FEATURE_HOMEPAGE)){
				profileConnInfo.setHostName(DBWizardInputs.getProfileDbHostName(wizard));
				profileConnInfo.setPort(DBWizardInputs.getProfileDbPort(wizard));
				profileConnInfo.setDbName(DBWizardInputs.getProfileJDBCDbName(wizard));
				profileConnInfo.setJdbcLibPath(DBWizardInputs.getProfileJDBCLibPath(wizard));
				profileConnInfo.setUsername(DBWizardInputs.getProfileDbAdminName(wizard));
				profileConnInfo.setPassword(DBWizardInputs.getProfileDbAdminPassword(wizard));
				profileConnInfo.setDbType(dbType);
			}
			
			
			Map<String, String> dbUserPassword = DBWizardInputs
					.getDbUserPassword(wizard);
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(action)
					&& !Constants.DB_DB2.equals(dbType)
					&& DBWizardInputs.isDbUserPasswordSame(wizard)) {
				for (String feature : dbUserPassword.keySet()) {
					dbUserPassword.put(feature, DBWizardInputs
							.getSameDbUserPassword(wizard));
				}
			}
			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(action)
					&& Constants.DB_ORACLE.equals(dbType)) {
				if (null == dbUserPassword) {
					dbUserPassword = new HashMap<String, String>();
					for (String feature : dbUserPassword.keySet()) {
						dbUserPassword.put(feature, "password");
					}
				}				
			}
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(action)
//					&& Constants.DB_SQLSERVER.equals(dbType)
//					&& DBWizardInputs.isDbUserPasswordSame(wizard)) {
//				for (String feature : dbUserPassword.keySet()) {
//					dbUserPassword.put(feature, DBWizardInputs
//							.getSameDbUserPassword(wizard));
//				}
//			}

			Map<String, String> createDbFilePath = DBWizardInputs
					.getCreateDbFilePath(wizard);
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(action)
					&& Constants.DB_SQLSERVER.equals(dbType)
					&& DBWizardInputs.isCreateDbFilePathSame(wizard)) {
				for (String feature : createDbFilePath.keySet()) {
					createDbFilePath.put(feature, DBWizardInputs
							.getSameCreateDbFilePath(wizard));
				}
			}
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(action)
//					&& Constants.DB_SQLSERVER.equals(dbType)
//					&& DBWizardInputs.isCreateDbFilePathSame(wizard)) {
//				for (String feature : createDbFilePath.keySet()) {
//					createDbFilePath.put(feature, DBWizardInputs
//							.getSameCreateDbFilePath(wizard));
//				}
//			}

			Map<String, String> versions = DBWizardInputs.getDbUpgradeVersion(wizard);
			/*DBExe.setProperties(
					Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK,
					DBWizardInputs.getEnableCommunitiesOptionalTask(wizard));*/
//#Oracle12C_PDB_disable#					map = DBExe.getExecuteSQLScripts(dbType, dbVersion, dbInstallDir, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA, dbUserPassword,createDbFilePath, CommonHelper		
				map = DBExe.getExecuteSQLScripts(dbType, dbVersion, dbInstallDir, dbInstanceName,dbUserPassword,createDbFilePath, CommonHelper
						.getPlatformType(), features, versions, action, connInfo,profileConnInfo);	
			return map;
		} catch (Exception e) {
//			logger.log(Level.SEVERE, "dbconfig.severe.execute.task", e);
			e.printStackTrace();
			return null;
		}

	}
	
	private Map<String,List<List<String>>> getExecuteCommands() {
		
		Map<String,List<List<String>>> map = new HashMap<String,List<List<String>>>();

		try {
			DBWizard wizard = this.getWizard();
			String action = DBWizardInputs.getActionId(wizard);
			String dbType = DBWizardInputs.getDbType(wizard);
			String dbVersion = DBWizardInputs.getDbVersion(wizard);
			String dbInstallDir = DBWizardInputs.getDbInstallDir(wizard);
			String dbInstanceName = DBWizardInputs.getDbInstanceName(wizard);
//#Oracle12C_PDB_disable#			String PDBNameValue = DBWizardInputs.getPDBNameValue(wizard);
//#Oracle12C_PDB_disable#			String dbaPasswordValue = DBWizardInputs.getDbaPasswordValue(wizard);
//#Oracle12C_PDB_disable#			String dbaUserNameValue = DBWizardInputs.getDbaUserName(wizard);
//#Oracle12C_PDB_disable#			boolean isRunAsSYSDBA=DBWizardInputs.isRunAsSYSDBA(wizard);
//			String dbaPasswordValue = DBWizardInputs.getDbaPasswordValue(wizard);
			List<String> features = DBWizardInputs.getFeatures(wizard);

			// suppports for java upgrade
			JDBCConnectionInfo connInfo = new JDBCConnectionInfo();
			connInfo.setHostName(DBWizardInputs.getDbHostName(wizard));
			connInfo.setPort(DBWizardInputs.getDbPort(wizard));
			connInfo.setDbName(DBWizardInputs.getJDBCDbName(wizard));
			connInfo.setJdbcLibPath(DBWizardInputs.getJDBCLibPath(wizard));
			connInfo.setUsername(DBWizardInputs.getDbAdminName(wizard));
			connInfo.setPassword(DBWizardInputs.getDbAdminPassword(wizard));
			connInfo.setDbType(dbType);
			connInfo.setContentStore(Constants.FEATURE_COMMUNITIES, DBWizardInputs.getForumContentStorePath(wizard));

			JDBCConnectionInfo profileConnInfo = new JDBCConnectionInfo();
			if (features.contains(Constants.FEATURE_HOMEPAGE)){
				profileConnInfo.setHostName(DBWizardInputs.getProfileDbHostName(wizard));
				profileConnInfo.setPort(DBWizardInputs.getProfileDbPort(wizard));
				profileConnInfo.setDbName(DBWizardInputs.getProfileJDBCDbName(wizard));
				profileConnInfo.setJdbcLibPath(DBWizardInputs.getProfileJDBCLibPath(wizard));
				profileConnInfo.setUsername(DBWizardInputs.getProfileDbAdminName(wizard));
				profileConnInfo.setPassword(DBWizardInputs.getProfileDbAdminPassword(wizard));
				profileConnInfo.setDbType(dbType);
			}
			Map<String, String> dbUserPassword = DBWizardInputs
					.getDbUserPassword(wizard);
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(action)
					&& !Constants.DB_DB2.equals(dbType)
					&& DBWizardInputs.isDbUserPasswordSame(wizard)) {
				for (String feature : dbUserPassword.keySet()) {
					dbUserPassword.put(feature, DBWizardInputs
							.getSameDbUserPassword(wizard));
				}
			}
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(action)
//					&& Constants.DB_SQLSERVER.equals(dbType)
//					&& DBWizardInputs.isDbUserPasswordSame(wizard)) {
//				for (String feature : dbUserPassword.keySet()) {
//					dbUserPassword.put(feature, DBWizardInputs
//							.getSameDbUserPassword(wizard));
//				}
//			}

			Map<String, String> createDbFilePath = DBWizardInputs
					.getCreateDbFilePath(wizard);
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(action)
					&& Constants.DB_SQLSERVER.equals(dbType)
					&& DBWizardInputs.isCreateDbFilePathSame(wizard)) {
				for (String feature : createDbFilePath.keySet()) {
					createDbFilePath.put(feature, DBWizardInputs
							.getSameCreateDbFilePath(wizard));
				}
			}
			
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(action)
//					&& Constants.DB_SQLSERVER.equals(dbType)
//					&& DBWizardInputs.isCreateDbFilePathSame(wizard)) {
//				for (String feature : createDbFilePath.keySet()) {
//					createDbFilePath.put(feature, DBWizardInputs
//							.getSameCreateDbFilePath(wizard));
//				}
//			}
			
			Map<String, String> versions = DBWizardInputs.getDbUpgradeVersion(wizard);
			/*DBExe.setProperties(
					Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK,
					DBWizardInputs.getEnableCommunitiesOptionalTask(wizard));*/

//#Oracle12C_PDB_disable#				map = DBExe.getExecuteCommands(dbType, dbVersion, dbInstallDir, dbInstanceName, PDBNameValue,dbaPasswordValue,dbaUserNameValue,isRunAsSYSDBA,dbUserPassword, createDbFilePath, CommonHelper					
				map = DBExe.getExecuteCommands(dbType, dbVersion, dbInstallDir, dbInstanceName,dbUserPassword, createDbFilePath, CommonHelper
						.getPlatformType(), features, versions, action, connInfo,profileConnInfo);	
			return map;
		} catch (Exception e) {
//			logger.log(Level.SEVERE, "dbconfig.severe.execute.task", e);
			e.printStackTrace();
			return null;
		}

	}

	public boolean isInputValid() {
		DBWizard wizard = this.getWizard();
		String actionId = DBWizardInputs.getActionId(wizard);
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)) {
			boolean status = new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.summaryWizardPage.upgrade.question.message"), MessageDialog.WARNING,
					new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
			return status;
		}
		return true;
	}

	public void isInputValid_fake() {
		new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.summaryWizardPage.upgrade.question.message"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open();
	}
	
	public IWizardPage getNextPage() {
		
		if(DBWizardInputs.isExportOnly(this.getWizard())){
			return getWizard().getPage(Constants.WIZARD_PAGE_EXECUTION_DETAILDED_COMMAND_EXPORT);
	       }	
	   else if(DBWizardInputs.isNannyMode(this.getWizard())){
			return getWizard().getPage(Constants.WIZARD_PAGE_EXECUTION_DETAILDED_COMMAND);
		}else{
			return getWizard().getPage(Constants.WIZARD_PAGE_EXECUTION);
		}
	}
	
	private String getThemeStr(List<String> features){
		if(features.contains(Constants.FEATURE_ACTIVITIES) && !features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_1", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"));
		}
		if(features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_ACTIVITIES) && !features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_1", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("blogs.name"));
		}
		if(features.contains(Constants.FEATURE_WIKIS) && !features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_ACTIVITIES)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_1", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("wikis.name"));
		}
		if(features.contains(Constants.FEATURE_ACTIVITIES) && features.contains(Constants.FEATURE_BLOGS) && !features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_2", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"), Messages.getString("blogs.name"));
		}
		if(features.contains(Constants.FEATURE_ACTIVITIES) && features.contains(Constants.FEATURE_WIKIS) && !features.contains(Constants.FEATURE_BLOGS) ){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_2", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"), Messages.getString("wikis.name"));
		}
		if(features.contains(Constants.FEATURE_BLOGS) && features.contains(Constants.FEATURE_WIKIS) && !features.contains(Constants.FEATURE_ACTIVITIES)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_2", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("blogs.name"), Messages.getString("wikis.name"));
		}
		if(features.contains(Constants.FEATURE_ACTIVITIES) && features.contains(Constants.FEATURE_BLOGS) && features.contains(Constants.FEATURE_WIKIS)){
			return Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_4_3", "MigrateCommunityTheme_2_5_To_3_0","sncomm.migrate.jar", Messages.getString("activities.name"), Messages.getString("blogs.name"), Messages.getString("wikis.name"));
		}
		return "";
	}
}
