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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.interfaces.EmbeddedAction;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.DB2ConfigProp;
import com.ibm.lconn.wizard.dbconfig.backend.Task;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardDialog;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.DbTypeSelectionWizardPage;

public class FinishWizardPage extends CommonPage {
	
	public static final Logger logger = LogUtil.getLogger(FinishWizardPage.class);
	public static int num = 0;
	public static int[] boldLines = new int[Constants.FEATURE_COUNT_MAX];
	public static int line = 0;
	public static int baseLine = 0;
	private static String SPACE = " ";

	//TODO modify by Maxi, feature merge flag (librarygcd, libraryos) -> library
	private boolean flagLibraryMerged = false;
	//TODO end
	
	/**
	 * Create the wizard
	 */
	public FinishWizardPage() {
		super(Constants.WIZARD_PAGE_FINISH_PANEL);
	}

	public IWizardPage getNextPage() {
		this.setNextPage(Pages.getPage(Pages.OPERATION_SELECT));
		return super.getNextPage();
	}

	public boolean isInputValid() {
		getShell().dispose();
		this.setNextPage(null);
		return true;
	}
	
	public void isInputValid_fake() {
		getShell().dispose();
		this.setNextPage(null);
	}

	public IWizardPage getPreviousPage() {
		return null;
	}

	protected void updateDialogButtons() {
		DBWizard wizard = this.getWizard();
		DBWizardDialog parentDialog = wizard.getParentDialog();
		try {
			parentDialog.getTheCancelButton().setText(Messages.getString("button.cancel.text"));
			parentDialog.getTheCancelButton().setEnabled(false);
			parentDialog.getTheNextButton().setText(Messages.getString("button.finish.text"));

			parentDialog.getThePrevButton().setText(Messages.getString("button.prev.text"));
			parentDialog.getThePrevButton().setEnabled(false);
		} catch (Exception e) {
		}
	}

	public void onShow(Composite parent) {
		String title = Messages.getString("dbWizard.finishWizardPage.title");
		this.setTitle(title);
		String actionId = DBWizardInputs.getActionId(this.getWizard());
		String description = Messages.getString("dbWizard.finishWizardPage.description." + actionId) + "\n";
		StringBuilder detail = new StringBuilder();
		detail.append(getShowText());
		detail.append(Constants.UI_LINE_SEPARATOR);
		baseLine++;

		CommandResultInfo result = (CommandResultInfo) getWizard().getExecResult();
//		detail += result.getExitMessage();
		detail.append(getShowTextPart2());
		detail.append( Constants.UI_LINE_SEPARATOR);
		if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())){
			detail.append("\uFFFC");
		}else{
			detail.append("");
		}
		detail.append(Constants.UI_LINE_SEPARATOR);
		detail.append(Constants.UI_LINE_SEPARATOR);
		detail.append(Messages.getString("dbWizard.finishWizardPage.content.end"));

	/*	if (DBWizardInputs.fake) {
			detail = detail.replaceFirst(Messages.getString("dbWizard.backend.task.error." + actionId), Messages.getString("dbWizard.backend.task.success." + actionId));
			detail = detail.replaceFirst(Messages.getString("dbWizard.backend.task.error." + actionId), Messages.getString("dbWizard.backend.task.warn." + actionId));
			detail = detail.replaceFirst(Messages.getString("dbWizard.backend.task.error." + actionId), Messages.getString("dbWizard.backend.task.exception." + actionId));
		}*/
		if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())){
		EmbeddedAction action = new EmbeddedAction() {

			public String execute() {
				CommandResultInfo result = (CommandResultInfo) getWizard().getExecResult();
				String logPath = result.getLogPath();
//				String logPath = Task.logFile.getAbsolutePath();
				logPath = logPath.substring(0,logPath.indexOf("dbWizard") + "dbWizard".length());
				if (!CommonHelper.openHTML(logPath))
					showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.cannot.open.directory"));
				return result.getLogPath();
			}

			public String getLogPath() {
				CommandResultInfo result = (CommandResultInfo) getWizard().getExecResult();
				return result.getLogPath();
			}

			public String getActionLabel() {
				return Constants.BUTTON_TEXT_OPENLOG;
			}

			public int getHorizontalAlignment() {
				return SWT.LEFT;
			}

			public int getVerticalAlignment() {
				return SWT.TOP;
			}
		};

		int[] bold = new int[num];
		for (int i = 0; i < num; i++) {
			bold[i] = boldLines[i] + baseLine;
		}
			new BIDIStyledText(parent, description, detail.toString(), null, action, bold);
		}else{
			int[] bold = new int[num];
			for (int i = 0; i < num; i++) {
				bold[i] = boldLines[i] + baseLine;
			}
			new BIDIStyledText(parent, description, detail.toString(), null, null, bold);
		}

		if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
			super.setMessage(Messages.getString("dbWizard.finishWizardPage.task.warning") + result.getExitMessage(), IMessageProvider.WARNING);
		}
		if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
			super.setMessage(Messages.getString("dbWizard.finishWizardPage.task.error") + result.getExitMessage(), IMessageProvider.ERROR);
		}
	}

	private String getShowText() {
		DBWizard wizard = this.getWizard();
		String actionId = DBWizardInputs.getActionId(wizard);
		String dbTypeId = DBWizardInputs.getDbType(wizard);
		String dbInstallDir = DBWizardInputs.getDbInstallDir(wizard);
		String dbInstanceName = DBWizardInputs.getDbInstanceName(wizard);
		if (CommonHelper.isEmpty(dbInstanceName))
			dbInstanceName = "";
		else if (!dbInstanceName.equals("\\") && dbInstanceName.startsWith("\\") && Constants.DB_SQLSERVER.equals(dbTypeId))
			dbInstanceName = dbInstanceName.substring(1);
		List<String> features = DBWizardInputs.getFeatures(wizard);
		
		//*****************jia****************

		StringBuffer sb = new StringBuffer();
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part1")).append(SPACE);
		sb.append(Messages.getString("dbWizard.action.db." + actionId + ".name")).append(Constants.UI_LINE_SEPARATOR);
		baseLine++;
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part2")).append(SPACE);
		sb.append(Messages.getString(dbTypeId + ".name")).append(Constants.UI_LINE_SEPARATOR);
		baseLine++;
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part3")).append(SPACE);
		sb.append(dbInstallDir).append(Constants.UI_LINE_SEPARATOR);
		baseLine++;
		sb.append(Messages.getString("dbWizard.summaryWizardPage.show.text.part4")).append(SPACE);
		sb.append(dbInstanceName).append(Constants.UI_LINE_SEPARATOR);
		baseLine++;
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
				//TODO end
				sb.append(" ").append(Messages.getString(feature + ".name")).append(",");
			}	
		}
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
		sb.deleteCharAt(sb.length() - 1);
		sb.append(Constants.UI_LINE_SEPARATOR);
		baseLine++;
		sb.append(Constants.UI_LINE_SEPARATOR);
		baseLine++;

		return sb.toString();
	}
	
	private String getShowTextPart2(){
		
		StringBuffer message = new StringBuffer();
		
		DBWizard wizard = this.getWizard();
		String operationType = DBWizardInputs.getActionId(wizard);
		String dbType = DBWizardInputs.getDbType(wizard);
		String dbInstanceName = DBWizardInputs.getDbInstanceName(wizard);
//#Oracle12C_PDB_disable#		String PDBNameValue = DBWizardInputs.getPDBNameValue(wizard);
		List<String> features = DBWizardInputs.getFeatures(wizard);
		
		//*****************jia****************
		
		Map<String, List<List<String>>> map = DBWizardInputs.getFeaturesCommandsMap(this.getWizard());
		Map<String, String> versions = DBWizardInputs.getDbUpgradeVersion(wizard);
		Map<String, CommandResultInfo> resultMap = DBWizardInputs.getFeaturesResultsMap(this.getWizard());
		
		Map<String, String> dbFilePaths = DBWizardInputs.getCreateDbFilePath(wizard);
		if (Constants.OPERATION_TYPE_CREATE_DB.equals(operationType)
				&& Constants.DB_SQLSERVER.equals(operationType)
				&& DBWizardInputs.isCreateDbFilePathSame(wizard)) {
			for (String feature : dbFilePaths.keySet()) {
				dbFilePaths.put(feature, DBWizardInputs.getSameCreateDbFilePath(wizard));
			}
		}
		
		if (dbFilePaths != null) {
			Set<String> keys = dbFilePaths.keySet();
			for (String key : keys) {
				String value = new File(dbFilePaths.get(key)).getAbsolutePath();
				dbFilePaths.put(key, value);
			}
		}
			
		
		for(String feature: features){
			if(feature.indexOf("theme") == -1){
				//TODO modify by Maxi, special action of library_gcd and library_os
				if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
					if (!flagLibraryMerged) {
						FinishWizardPage.boldLines[FinishWizardPage.num] = FinishWizardPage.line;
						message.append(Messages.getString(Constants.FEATURE_LIBRARY + ".name"));
						FinishWizardPage.num++;
						
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						
						message.append(Messages.getString(Constants.FEATURE_LIBRARY_GCD + ".name"));
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1")).append(" ");
						if (Constants.DB_ORACLE.equals(dbType)) {
//#Oracle12C_PDB_disable#							if (CommonHelper.isEmpty(PDBNameValue)){
								message.append(dbInstanceName);
//#Oracle12C_PDB_disable#							}else{
//#Oracle12C_PDB_disable#								message.append(PDBNameValue);
//#Oracle12C_PDB_disable#							}  
							
						} else {
							message.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_GCD + ".dbName." + dbType));
						}
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a")).append(" ");
						message.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_GCD + ".dbUserName." + dbType)).append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						
						if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType)) {
							
						}else{
							message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_2")).append(SPACE);
							for(String element : DBWizardInputs.getSQLScriptsMap(this.getWizard()).get(Constants.FEATURE_LIBRARY_GCD)){
								message.append(element).append(", ");
							}
							message.deleteCharAt(message.length()-1);
							message.deleteCharAt(message.length()-1);
						}
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						
						if (dbType.equals(Constants.DB_SQLSERVER) && operationType.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
							message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3")).append(" ");
							message.append(dbFilePaths.get(Constants.FEATURE_LIBRARY_GCD)).append(Constants.UI_LINE_SEPARATOR);
							FinishWizardPage.line++;
						}
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part4")).append(" ");
						
						CommandResultInfo result = null;
						StringBuilder sqlFileNameStr = new StringBuilder();
						List<List<String>> cmds = map.get(Constants.FEATURE_LIBRARY_GCD);
						for(List<String> cmd : cmds){
							String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
							result = resultMap.get(Constants.FEATURE_LIBRARY_GCD + "_" + sqlFileName);
							
							if (result.getExecState() != CommandResultInfo.COMMAND_SUCCEED) {
								sqlFileNameStr.append(Constants.FEATURE_LIBRARY_GCD).append(".").append(sqlFileName).append(".jar").append(", ");
							} 
						}
						
						int i = 0;
						for(List<String> cmd : cmds){
							String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
							result = resultMap.get(Constants.FEATURE_LIBRARY_GCD + "_" + sqlFileName);
							if(result.getExecState() != CommandResultInfo.COMMAND_SUCCEED){
								sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
								sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
								if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
									message.append(Messages.getString("dbWizard.backend.task.warn." + operationType + ".new", sqlFileNameStr));
								} else if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
									message.append(Messages.getString("dbWizard.backend.task.error." + operationType + ".new", sqlFileNameStr));
								} else {
									message.append(Messages.getString("dbWizard.backend.task.exception." + operationType + ".new", sqlFileNameStr));
								}
								break;
							}else{
								i++;
							}
						}
						if(i==cmds.size()){
							message.append(Messages.getString("dbWizard.backend.task.success." + operationType));
						}
						
						
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						//TODO modify by Maxi, end of part gcd
						message.append(Messages.getString(Constants.FEATURE_LIBRARY_OS + ".name"));
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1")).append(" ");
						if (Constants.DB_ORACLE.equals(dbType)) {
//#Oracle12C_PDB_disable#							if (CommonHelper.isEmpty(PDBNameValue)){
								message.append(dbInstanceName);
//#Oracle12C_PDB_disable#							}else{
//#Oracle12C_PDB_disable#								message.append(PDBNameValue);
//#Oracle12C_PDB_disable#							} 
						} else {
							message.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_OS + ".dbName." + dbType));
						}
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a")).append(" ");
						message.append(DB2ConfigProp.getProperty(Constants.FEATURE_LIBRARY_OS + ".dbUserName." + dbType)).append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						
						if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType)) {
							
						}else{
							message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_2")).append(SPACE);
							for(String element : DBWizardInputs.getSQLScriptsMap(this.getWizard()).get(Constants.FEATURE_LIBRARY_OS)){
								message.append(element).append(", ");
							}
							message.deleteCharAt(message.length()-1);
							message.deleteCharAt(message.length()-1);
						}
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						
						if (dbType.equals(Constants.DB_SQLSERVER) && operationType.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
							message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3")).append(" ");
							message.append(dbFilePaths.get(Constants.FEATURE_LIBRARY_OS)).append(Constants.UI_LINE_SEPARATOR);
							FinishWizardPage.line++;
						}
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part4")).append(" ");
						
						sqlFileNameStr = new StringBuilder();
						cmds = map.get(Constants.FEATURE_LIBRARY_OS);
						for(List<String> cmd : cmds){
							String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
							result = resultMap.get(Constants.FEATURE_LIBRARY_OS + "_" + sqlFileName);
							
							if (result.getExecState() != CommandResultInfo.COMMAND_SUCCEED) {
								sqlFileNameStr.append(Constants.FEATURE_LIBRARY_OS).append(".").append(sqlFileName).append(".jar").append(", ");
							} 
						}
						
						i = 0;
						for(List<String> cmd : cmds){
							String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
							result = resultMap.get(Constants.FEATURE_LIBRARY_OS + "_" + sqlFileName);
							if(result.getExecState() != CommandResultInfo.COMMAND_SUCCEED){
								sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
								sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
								if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
									message.append(Messages.getString("dbWizard.backend.task.warn." + operationType + ".new", sqlFileNameStr));
								} else if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
									message.append(Messages.getString("dbWizard.backend.task.error." + operationType + ".new", sqlFileNameStr));
								} else {
									message.append(Messages.getString("dbWizard.backend.task.exception." + operationType + ".new", sqlFileNameStr));
								}
								break;
							}else{
								i++;
							}
						}
						if(i==cmds.size()){
							message.append(Messages.getString("dbWizard.backend.task.success." + operationType));
						}
						
						
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						flagLibraryMerged = true;
					}
					continue;
				}
				//TODO end
			
			FinishWizardPage.boldLines[FinishWizardPage.num] = FinishWizardPage.line;
			message.append(Messages.getString(feature + ".name"));
			FinishWizardPage.num++;
			
			message.append(Constants.UI_LINE_SEPARATOR);
			FinishWizardPage.line++;
			message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part1")).append(" ");
			if (Constants.DB_ORACLE.equals(dbType)) {
//#Oracle12C_PDB_disable#				if (CommonHelper.isEmpty(PDBNameValue)){
					message.append(dbInstanceName);
//#Oracle12C_PDB_disable#				}else{
//#Oracle12C_PDB_disable#					message.append(PDBNameValue);
//#Oracle12C_PDB_disable#				} 
			} else {
				message.append(DB2ConfigProp.getProperty(feature + ".dbName." + dbType));
			}
			message.append(Constants.UI_LINE_SEPARATOR);
			FinishWizardPage.line++;
			
			message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a")).append(" ");
			message.append(DB2ConfigProp.getProperty(feature + ".dbUserName." + dbType)).append(Constants.UI_LINE_SEPARATOR);
			FinishWizardPage.line++;
			
			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(operationType)) {
				message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_b")).append(" ");
				String version = versions.get(feature);
				if (version.startsWith("1.0"))
					version = Constants.VERSION_10X;
				
				message.append(version).append("->").append(Constants.VERSION_TOP).append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
				message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_2")).append(SPACE);
				List<String> elements = DBWizardInputs.getSQLScriptsMap(this.getWizard()).get(feature);
				for(String element : elements){
//					if(element.indexOf("../../forum") != -1){
//						element = element.replace("../../", "");
//					}
					if(element.startsWith("$java")){
						continue;
					}
					message.append(element).append(", ");
				}
				message.deleteCharAt(message.length()-1);		
				message.deleteCharAt(message.length()-1);
				message.append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
				String element = "";
//				for(int i=1;i<elements.size()-1;i++){
//					element = elements.get(i);
//					if(element.startsWith("$java")){
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3")).append(SPACE);
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
								message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_2",element,elements.get(i-1),elements.get(i+2))).append(", ");
								i++;	
							}else{
						message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_3_2",element,elements.get(i-1),elements.get(i+1))).append(", ");	
							}
					}
				}
				message.deleteCharAt(message.length()-1);	
				for(int i=1;i<elements.size()-1;i++){
					element = elements.get(i);
					if(element.startsWith("$java")){
						message.deleteCharAt(message.length()-1);
						break;
					}
				}
				
			}else{
				message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part2_a_2")).append(SPACE);
				for(String element : DBWizardInputs.getSQLScriptsMap(this.getWizard()).get(feature)){
					message.append(element).append(", ");
				}
				message.deleteCharAt(message.length()-1);
				message.deleteCharAt(message.length()-1);
			}
			message.append(Constants.UI_LINE_SEPARATOR);
			FinishWizardPage.line++;
			
			if (dbType.equals(Constants.DB_SQLSERVER) && operationType.equals(Constants.OPERATION_TYPE_CREATE_DB)) {
				message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part3")).append(" ");
				message.append(dbFilePaths.get(feature)).append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
			}
			message.append(Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part4")).append(" ");
			
			CommandResultInfo result = null;
			StringBuilder sqlFileNameStr = new StringBuilder();
			List<List<String>> cmds = map.get(feature);
			for(List<String> cmd : cmds){
				String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
				result = resultMap.get(feature + "_" + sqlFileName);
				
				if (result.getExecState() != CommandResultInfo.COMMAND_SUCCEED) {
					if(sqlFileName.indexOf("migrate") == -1){
						if(sqlFileName.indexOf("forum_") != -1){
//							sqlFileNameStr.append(sqlFileName.substring(sqlFileName.indexOf("forum_") + "forum_".length())).append(".sql").append(", ");
							sqlFileNameStr.append(sqlFileName.replace("forum_","forum.")).append(".sql").append(", ");
						}else{		
							sqlFileNameStr.append(sqlFileName).append(".sql").append(", ");
						}
					}else{
						if(sqlFileName.indexOf("forum_") != -1){
							sqlFileNameStr.append(sqlFileName.replace("forum_","forum.")).append(".jar").append(", ");
						}else if(sqlFileName.indexOf("news_email_") != -1){
								sqlFileNameStr.append(sqlFileName.replace("news_email_","news.")).append(".jar").append("(EmailDigestMigrationFrom25To30.class), ");
						}else if(sqlFileName.indexOf("news_") != -1){
							    if(sqlFileName.indexOf("45CR4-50") != -1){
							    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom45To50.class), ");
							    }
							    if(sqlFileName.indexOf("40CR2-45") != -1){
							    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom40To45.class), ");
							    }
							    if(sqlFileName.indexOf("301-40") != -1){
							    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom301To40.class), ");
							    }
							    if(sqlFileName.indexOf("25-30a") != -1){
							    	sqlFileNameStr.append(sqlFileName.replace("news_","news.")).append(".jar").append("(NewsMigrationFrom25To30.class), ");
							    }
						}else{
							sqlFileNameStr.append(feature).append(".").append(sqlFileName).append(".jar").append(", ");
						}
						
					}
					
				} 
			}
			
			int i = 0;
			for(List<String> cmd : cmds){
				String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
				result = resultMap.get(feature + "_" + sqlFileName);
				if(result.getExecState() != CommandResultInfo.COMMAND_SUCCEED){
					sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
					sqlFileNameStr.deleteCharAt(sqlFileNameStr.length() - 1);
					if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
						message.append(Messages.getString("dbWizard.backend.task.warn." + operationType + ".new", sqlFileNameStr));
					} else if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
						if(result.getFeatures().contains(feature)){
							message.append(Messages.getString("dbWizard.backend.task.abort.message", feature.substring(0, 1).toUpperCase() + feature.substring(1)));
						}else{
							message.append(Messages.getString("dbWizard.backend.task.error." + operationType + ".new", sqlFileNameStr));
						}
					} else {
						message.append(Messages.getString("dbWizard.backend.task.exception." + operationType + ".new", sqlFileNameStr));
					}
					break;
				}else{
					i++;
				}
			}
			if(i==cmds.size()){
				message.append(Messages.getString("dbWizard.backend.task.success." + operationType));
			}
			
			
			message.append(Constants.UI_LINE_SEPARATOR);
			FinishWizardPage.line++;
			message.append(Constants.UI_LINE_SEPARATOR);
			FinishWizardPage.line++;
			}
		}
		
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
		
		for(String feature: features){
			if(feature.indexOf("theme") != -1){
				CommandResultInfo result = null;
				//TODO modify by Maxi, special action of library_gcd and library_os
				if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
					if (!flagLibraryMerged) {
						List<List<String>> cmds = map.get(Constants.FEATURE_LIBRARY_GCD); 
						int i = 0;
						for(List<String> cmd : cmds){
							String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
							result = resultMap.get(Constants.FEATURE_LIBRARY_GCD + "_" + sqlFileName);
							if (result.getExecState() == CommandResultInfo.COMMAND_SUCCEED) {
								i++;
							}
						}
						if (i== cmds.size()) {
							message.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString(Constants.FEATURE_LIBRARY_GCD + ".name")));
						}
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						cmds = map.get(Constants.FEATURE_LIBRARY_OS); 
						i = 0;
						for(List<String> cmd : cmds){
							String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
							result = resultMap.get(Constants.FEATURE_LIBRARY_OS + "_" + sqlFileName);
							if (result.getExecState() == CommandResultInfo.COMMAND_SUCCEED) {
								i++;
							}
						}
						if (i== cmds.size()) {
							message.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString(Constants.FEATURE_LIBRARY_OS + ".name")));
						}
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						message.append(Constants.UI_LINE_SEPARATOR);
						FinishWizardPage.line++;
						flagLibraryMerged = true;
					}
					continue;
				}
				//TODO end
				List<List<String>> cmds = map.get(feature); 
				int i = 0;
				for(List<String> cmd : cmds){
					String sqlFileName = CommonHelper.getSqlFileName(cmd, DBWizardInputs.getDbType(this.getWizard()));
					result = resultMap.get(feature + "_" + sqlFileName);
					if (result.getExecState() != CommandResultInfo.COMMAND_SUCCEED) {
						if (result.getExecState() == CommandResultInfo.COMMAND_WARNING) {
							if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
							message.append(Messages.getString("dbWizard.backend.task.theme.warn", "MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("activities.name")));
							}
							if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
								message.append(Messages.getString("dbWizard.backend.task.theme.warn", "MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("blogs.name")));
							}
							if(Constants.FEATURE_WIKISTHEME.equals(feature)){
								message.append(Messages.getString("dbWizard.backend.task.theme.warn", "MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("wikis.name")));
							}
						} else if (result.getExecState() == CommandResultInfo.COMMAND_ERROR) {
							if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
							message.append(Messages.getString("dbWizard.backend.task.theme.error","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar", Messages.getString("activities.name")));
							}
							if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
								message.append(Messages.getString("dbWizard.backend.task.theme.error","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar", Messages.getString("blogs.name")));
							}
							if(Constants.FEATURE_WIKISTHEME.equals(feature)){
								message.append(Messages.getString("dbWizard.backend.task.theme.error","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar", Messages.getString("wikis.name")));
							}
						} else {
							if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
							message.append(Messages.getString("dbWizard.backend.task.theme.exception","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("activities.name")));
							}
							if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
								message.append(Messages.getString("dbWizard.backend.task.theme.exception","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("blogs.name")));
							}
							if(Constants.FEATURE_WIKISTHEME.equals(feature)){
								message.append(Messages.getString("dbWizard.backend.task.theme.exception","MigrateCommunityTheme_2_5_To_3_0",
										"sncomm.migrate.jar",  Messages.getString("wikis.name")));
							}
						}
					}else{  
						i++;
					}
				}
				if(i==cmds.size()){
					if(Constants.FEATURE_ACTIVITIESTHEME.equals(feature)){
						message.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString("activities.name")));
					}
					if(Constants.FEATURE_BLOGSTHEME.equals(feature)){
						message.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString("blogs.name")));
					}
					if(Constants.FEATURE_WIKISTHEME.equals(feature)){
						message.append(Messages.getString("dbWizard.backend.task.theme.success", Messages.getString("wikis.name")));
					}
				}
				message.append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
				message.append(Constants.UI_LINE_SEPARATOR);
				FinishWizardPage.line++;
			}
		}
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
		
		String logPath = Task.logFile.getAbsolutePath();
		logPath = logPath.substring(0,logPath.indexOf("dbWizard") + "dbWizard".length());
		message.append(Messages.getString("dbWizard.finishWizardPage.content.view.log.new", logPath));
		
		return message.toString();
	}
	
		
		
			
//			indexStart = sb.toString().lastIndexOf("lib") + "lib".length() + 1;
		
			
}
