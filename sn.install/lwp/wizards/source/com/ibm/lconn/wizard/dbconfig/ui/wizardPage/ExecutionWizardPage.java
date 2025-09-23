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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.interfaces.DbCreationInterface;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardDialog;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.tdipopulate.backend.JDBCConnectionInfo;

public class ExecutionWizardPage extends CommonPage {
	private DbCreationInterface DBExe;
	private String logPath;
	private boolean nannyFlag;
	private int featureNum;
	private int commandCount = 0;
	private static boolean FlipFlag = false; 
	private int count = 0;
	private Label message;
    public static boolean executeFlag = true;
	private ProgressBar pb;
	public static final Logger logger = LogUtil.getLogger(ExecutionWizardPage.class);

	public ExecutionWizardPage() {
		super(Constants.WIZARD_PAGE_EXECUTION);
	}

	public boolean canFlipToNextPage() {
		return false;
	}

	public void onShow(Composite parent) {
		
		DBWizardInputs.setNannyMode(this.getWizard(), false);
		DBWizardDialog parentDialog = getWizard().getParentDialog();
		
		String actionId = DBWizardInputs.getActionId(this.getWizard());
		setTitle(Messages
				.getString("dbWizard.executionWizardPage.title." + actionId)); //$NON-NLS-1$

		final Composite container = CommonHelper.createScrollableControl(
				Composite.class, parent, SWT.NONE, SWT.V_SCROLL);
		container.setLayout(new GridLayout());
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		container.setLayout(gridLayout);

		DBExe = DBWizard.getDBCreationInterface();
		
		this.message = new Label(container, SWT.WRAP);
		message.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		if(DBWizardInputs.isNannyMode(this.getWizard())){
			String featureName = DBWizardInputs.getFeatureNameTmp(this.getWizard());
			List<String> command = DBWizardInputs.getCommandTmp(this.getWizard());
			logPath = DBExe.createLogForTask(featureName, CommonHelper.getSqlFileName(command, DBWizardInputs.getDbType(this.getWizard())));

		message.setText(Messages
				.getString("dbWizard.executionWizardPage.description."
						+ actionId, logPath) + "\n\n");

			pb = new ProgressBar(container, SWT.SMOOTH);
		pb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		pb.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				String string = (pb.getSelection() * 1.0
						/ (pb.getMaximum() - pb.getMinimum()) * 100)
						+ "%";
				Point point = pb.getSize();
				Font font = new Font(getWizard().getParentDialog().getShell().getDisplay(), "Courier", 10,
						SWT.BOLD);
				e.gc.setFont(font);
				e.gc.setForeground(getWizard().getParentDialog().getShell().getDisplay().getSystemColor(
						SWT.COLOR_RED));
				FontMetrics fontMetrics = e.gc.getFontMetrics();
				int stringWidth = fontMetrics.getAverageCharWidth() * 4;
				int stringHeight = fontMetrics.getHeight();
				e.gc.drawString(string, (point.x - stringWidth) / 2,
						(point.y - stringHeight) / 2, true);
				font.dispose();
			}
		});
			pb.setMaximum(100);
			pb.setSelection(20);
		}else{
			message.setText(Messages
					.getString("dbWizard.executionWizardPage.description."
							+ actionId, "") + "\n\n");
			pb = new ProgressBar(container, SWT.INDETERMINATE);
			pb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		final Label space = new Label(container, SWT.NONE);
		space.setText("\n\n");

		if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())){
		Button button = new Button(container, SWT.NONE);
		button.setText(Constants.BUTTON_TEXT_OPENLOG);
		
		final Label result = new Label(container, SWT.WRAP);
//		result.setText("\n\n\n\n\n" + Messages
//				.getString("dbWizard.summaryWizardPage.show.text.reuse.part4")
//				+ "\n\n" + "	There were some errors");
		result.setLayoutData(new GridData(SWT.FILL, SWT.MEDIUM, true, false));
		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				getWizard().getParentDialog().getShell().getDisplay().asyncExec(new Runnable() {
					public void run(){
						if(!DBWizardInputs.isNannyMode( getWizard())){
							logPath = logPath.substring(0,logPath.indexOf("dbWizard") + "dbWizard".length());
							if (!CommonHelper.openHTML(logPath))
								showInputCheckErrorMsg(ErrorMsg
										.getString("dbwizard.error.cannot.open.directory"));
						}else{
							if (!CommonHelper.openLog(logPath))
								showInputCheckErrorMsg(ErrorMsg
										.getString("dbwizard.error.cannot.launch.file"));
						}
						
					}
				});
				
				

			}
		});
		}else{
		}
		
		
		final List<String> features = DBWizardInputs.getFeatures(this.getWizard());
		final Map<String, List<List<String>>> map = DBWizardInputs.getFeaturesCommandsMap(this.getWizard());
		
//		new Thread(){
//			public void run() {
//				
//				execteDbCreation(pb);
//				
//				getWizard().getParentDialog().getShell().getDisplay().asyncExec(new Runnable() {
//		            public void run() {
//		            	pb.setSelection(100);
//		            	
////		      		if(pb.getSelection()==100){
////		      			Label result = new Label(container, SWT.WRAP);
//		      			result.setText("\n\n\n\n\n" + Messages
//		      					.getString("dbWizard.summaryWizardPage.show.text.reuse.part4")
//		      					+ "\n\n" + "There were some errors");
////		      			result.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
//		      			if(nannyFlag){
//		      				boolean status = new MessageDialog(getShell(), Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part4"), null, "There were some errors, please check the SQL script.", MessageDialog.WARNING, new String[] { Messages.getString("button.OK.text")}, 0).open() == 0;
//		      			}
////		      			if (status)
////		      			System.out.println("????????????????????????????????status: " + status);
//		      			DBWizardDialog parentDialog = getWizard().getParentDialog();
//		    				parentDialog.getTheNextButton().setEnabled(true);;
////		    			else
////		    				return status;
////		      		}
//		      		if (pb.isDisposed())
//		                return;
//		            }
//		          });
//			}
//		}.start();
//		 pb.setSelection(100);
//		executionStart(pb);
//		pb.setSelection(100);
		
		if(DBWizardInputs.isNannyMode(this.getWizard())){
			DBWizardInputs.setExecutePageEnteredFlag(this.getWizard(), true);
		}
		new Thread(new Runnable(){
			public void run() {
				final DBWizardDialog parentDialog = getWizard().getParentDialog();
		Display display = getWizard().getParentDialog().getShell().getDisplay();
		if(!display.isDisposed()){
			Runnable run = new Runnable(){

				public void run() {
					parentDialog.getThePrevButton().setEnabled(false);
						}};
					display.asyncExec(run);
				}			
					
					execteDbCreation(pb);
				if(DBWizardInputs.isNannyMode(getWizard())){
					if(!display.isDisposed()){
						Runnable run = new Runnable(){
							public void run() {
					pb.setSelection(60);
					for(int i=70;i<101;){
						try {
							Thread.sleep(300);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						pb.setSelection(i);
						i += 10;
								}
							}};
						display.asyncExec(run);
					}
				}			
//					pb.dispose();
				final CommandResultInfo result = (CommandResultInfo) getWizard().getExecResult();
					if(DBWizardInputs.isNannyMode(getWizard())){
						String feature = DBWizardInputs.getFeatureNameTmp(getWizard());
						List<String> command = DBWizardInputs.getCommandTmp(getWizard());
					String strTmp = CommonHelper.getSqlFileName(command, DBWizardInputs.getDbType(getWizard()));
						String sqlFileName = "";
						if(strTmp.indexOf("migrate") == -1){
							if(strTmp.indexOf("forum_") != -1){
								sqlFileName = strTmp.replace("forum_","forum.") + ".sql";
							}else{
								sqlFileName = strTmp + ".sql";
							}
							
						}else{
							if(strTmp.indexOf("forum_") != -1){
								sqlFileName = strTmp.replace("forum_","forum.") + ".jar";
							}else{
								sqlFileName = feature + "." + strTmp + ".jar";
							}
						}
						String messageStr = "";
						int dialogType = 0;
						if(result.getExecState()==0){
							messageStr = Messages.getString("dbWizard.backend.task.success");
							dialogType = MessageDialog.INFORMATION;
						}
						if(result.getExecState()==1){
							messageStr = Messages.getString("dbWizard.backend.task.warn", sqlFileName);
							dialogType = MessageDialog.WARNING;
						}
						if(result.getExecState()==2){	
							messageStr = Messages.getString("dbWizard.backend.task.error", sqlFileName);
							dialogType = MessageDialog.ERROR;
						}
					final String messageStrTmp = messageStr;
					final int dialogTypeTmp = dialogType;
						if(!display.isDisposed()){
							Runnable run = new Runnable(){
								public void run() {
									boolean status = new MessageDialog(getShell(), Messages.getString("dbWizard.summaryWizardPage.show.text.reuse.part4"), null, messageStrTmp, dialogTypeTmp, new String[] { Messages.getString("button.OK.text")}, 0).open() == 0;
//									if (status) {
							parentDialog.getTheCancelButton().setEnabled(true);
							parentDialog.getTheNextButton().setEnabled(true);
							if(result.getExecState()==0){
								parentDialog.getThePrevButton().setEnabled(false);
								parentDialog.getTheNextButton().setFocus();
							}else{
								parentDialog.getThePrevButton().setEnabled(true);
								parentDialog.getThePrevButton().setFocus();
							
						}
								}};
							display.asyncExec(run);
						}							
					}else{
					if(!display.isDisposed()){
						Runnable run = new Runnable(){
							public void run() {
						Wizard parentWizard = getWizard();
						IWizardPage finishPage = parentWizard.getPage(Constants.WIZARD_PAGE_FINISH_PANEL);
						parentWizard.getContainer().showPage(finishPage);
							}};
						display.asyncExec(run);
					}
				}
				
		}
		}).start();
		
		
	}

//	private void executionStart(ProgressBar pb) {
////		setCancelPrevButtonEnable(false);
//		System.out.println("######################Selection: " + pb.getSelection());
//		pb.setSelection(30);
//		Thread thread = new Thread(){
//			public void run() {
//				execteDbCreation();
//				getWizard().getParentDialog().getShell().getDisplay().asyncExec(new Runnable() {
//		            public void run() {
//		              if (pb.isDisposed())
//		                return;
//		              pb.setSelection(60);
//		            }
//		          });
//			}
//		};
//		thread.start();
//		pb.setSelection(40);
//		System.out.println("######################## 1 execute end #######################");
//	}

	private void execteDbCreation(ProgressBar pb) {

		try {
			DBWizard wizard = this.getWizard();
			String actionId = DBWizardInputs.getActionId(wizard);
			String dbTypeId = DBWizardInputs.getDbType(wizard);
			String dbVersion = DBWizardInputs.getDbVersion(wizard);
			String dbInstallDir = DBWizardInputs.getDbInstallDir(wizard);
			String dbInstanceName = DBWizardInputs.getDbInstanceName(wizard);
//#Oracle12C_PDB_disable#			String PDBNameValue = DBWizardInputs.getPDBNameValue(wizard);
//#Oracle12C_PDB_disable#			String dbaPasswordValue = DBWizardInputs.getDbaPasswordValue(wizard);
//#Oracle12C_PDB_disable#		    String dbaUserNameValue = DBWizardInputs.getDbaUserName(wizard);
//#Oracle12C_PDB_disable#			boolean isRunAsSYSDBA=DBWizardInputs.isRunAsSYSDBA(wizard);
			
		
				
			List<String> features = DBWizardInputs.getFeatures(wizard);

			// suppports for java upgrade
			JDBCConnectionInfo connInfo = new JDBCConnectionInfo();
			connInfo.setHostName(DBWizardInputs.getDbHostName(wizard));
			connInfo.setPort(DBWizardInputs.getDbPort(wizard));
			connInfo.setDbName(DBWizardInputs.getJDBCDbName(wizard));
			connInfo.setJdbcLibPath(DBWizardInputs.getJDBCLibPath(wizard));
			connInfo.setUsername(DBWizardInputs.getDbAdminName(wizard));
			connInfo.setPassword(DBWizardInputs.getDbAdminPassword(wizard));
			connInfo.setDbType(dbTypeId);
			connInfo.setContentStore(Constants.FEATURE_COMMUNITIES, DBWizardInputs.getForumContentStorePath(wizard));

			JDBCConnectionInfo profileConnInfo = new JDBCConnectionInfo();
			if (features.contains(Constants.FEATURE_HOMEPAGE)){
				profileConnInfo.setHostName(DBWizardInputs.getProfileDbHostName(wizard));
				profileConnInfo.setPort(DBWizardInputs.getProfileDbPort(wizard));
				profileConnInfo.setDbName(DBWizardInputs.getProfileJDBCDbName(wizard));
				profileConnInfo.setJdbcLibPath(DBWizardInputs.getProfileJDBCLibPath(wizard));
				profileConnInfo.setUsername(DBWizardInputs.getProfileDbAdminName(wizard));
				profileConnInfo.setPassword(DBWizardInputs.getProfileDbAdminPassword(wizard));
				profileConnInfo.setDbType(dbTypeId);
			}
			Map<String, String> dbUserPassword = DBWizardInputs
					.getDbUserPassword(wizard);
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(actionId)
					&& !Constants.DB_DB2.equals(dbTypeId)
					&& DBWizardInputs.isDbUserPasswordSame(wizard)) {
				for (String feature : dbUserPassword.keySet()) {
					dbUserPassword.put(feature, DBWizardInputs
							.getSameDbUserPassword(wizard));
				}
			}
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)
//					&& Constants.DB_ORACLE.equals(dbTypeId)
//					&& DBWizardInputs.isDbUserPasswordSame(wizard)) {
//				for (String feature : dbUserPassword.keySet()) {
//					dbUserPassword.put(feature, DBWizardInputs
//							.getSameDbUserPassword(wizard));
//				}
//			}
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)
//					&& Constants.DB_SQLSERVER.equals(dbTypeId)
//					&& DBWizardInputs.getFeatures(this.getWizard()).contains(Constants.FEATURE_COMMUNITIES)
//					&& DBWizardInputs.isDbUserPasswordSame(wizard)) {
//				for (String feature : dbUserPassword.keySet()) {
//					dbUserPassword.put(feature, DBWizardInputs
//							.getSameDbUserPassword(wizard));
//				}
//			}
				
			
			Map<String, String> createDbFilePath = DBWizardInputs
					.getCreateDbFilePath(wizard);
			if (Constants.OPERATION_TYPE_CREATE_DB.equals(actionId)
					&& Constants.DB_SQLSERVER.equals(dbTypeId)
					&& DBWizardInputs.isCreateDbFilePathSame(wizard)) {
				for (String feature : createDbFilePath.keySet()) {
					createDbFilePath.put(feature, DBWizardInputs
							.getSameCreateDbFilePath(wizard));
				}
			}

			Map<String, String> versions = DBWizardInputs.getDbUpgradeVersion(wizard);
			/*DBExe.setProperties(
					Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK,
					DBWizardInputs.getEnableCommunitiesOptionalTask(wizard));*/
			CommandResultInfo result = null;
			if(DBWizardInputs.isNannyMode(this.getWizard())){
				String feature = DBWizardInputs.getFeatureNameTmp(this.getWizard());
				List<String> command = DBWizardInputs.getCommandTmp(this.getWizard());
//#Oracle12C_PDB_disable#						result = DBExe.execute(dbTypeId, dbVersion, dbInstallDir, dbInstanceName,PDBNameValue,dbaPasswordValue,dbaUserNameValue, isRunAsSYSDBA,		
				result = DBExe.execute(dbTypeId, dbVersion, dbInstallDir, dbInstanceName,
						dbUserPassword, createDbFilePath, CommonHelper
								.getPlatformType(), feature, versions,
						actionId, connInfo, command, features,profileConnInfo);
		
//					if(null == DBWizardInputs.getCommandCount(getWizard())){
//						DBWizardInputs.setCommandCount(getWizard(), 0);	
//					}
				
				Map<String, CommandResultInfo> resultMap = DBWizardInputs.getFeaturesResultsMap(this.getWizard());
				if(resultMap==null){
					resultMap = new HashMap<String, CommandResultInfo>();
				}
				resultMap.put(feature + "_" + CommonHelper.getSqlFileName(command, DBWizardInputs.getDbType(this.getWizard())), result);
				DBWizardInputs.setFeaturesResultsMap(this.getWizard(),resultMap);
					
			}else{
//				pb.setSelection(40);
//				result = DBExe.execute(dbTypeId, dbInstallDir, dbInstanceName,PDBNameValue,dbaPasswordValue
//						dbUserPassword, createDbFilePath, CommonHelper
//								.getPlatformType(), features, versions,
//						actionId, connInfo);
				Map<String, List<List<String>>> map = DBWizardInputs.getFeaturesCommandsMap(this.getWizard());
				for(String feature : features){
                    logger.info("feature======" + feature);
					List<List<String>> cmds = map.get(feature);
					for(List<String> cmd : cmds){
                            String sqlFileName = CommonHelper.getSqlFileName(cmd,
                                    DBWizardInputs.getDbType(this.getWizard()));
                        if (executeFlag == true) {
                            logger.info("begine execute======" + sqlFileName);
                            logPath = DBExe.createLogForTask(feature, sqlFileName);

                            final String actionIdTmp = actionId;
                            Display display = getWizard().getParentDialog().getShell().getDisplay();
                            if (!display.isDisposed()) {
                                Runnable run = new Runnable() {
                                    public void run() {
                                        message.setText(Messages.getString(
                                                "dbWizard.executionWizardPage.description."
                                                        + actionIdTmp, logPath)
                                                + "\n\n");
                                    }
                                };
                                display.asyncExec(run);
                            }

                            result = DBExe.execute(dbTypeId, dbVersion, dbInstallDir,
//#Oracle12C_PDB_disable#										dbInstanceName,PDBNameValue,dbaPasswordValue, dbaUserNameValue, isRunAsSYSDBA,dbUserPassword,createDbFilePath,
									dbInstanceName,dbUserPassword,createDbFilePath,
                                    CommonHelper.getPlatformType(), feature, versions, actionId,
                                    connInfo, cmd, features, profileConnInfo);
                        
	                        logger.info(result.getExitMessage() + "+++after execute======" + result.getExecState());
	                        if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(actionId)){
		                        if (result.getExecState() != 0) {
		                            executeFlag = false;
		                            result.setBlockedFeature(feature);
		                            result.setExitMessage(Messages.getString("dbWizard.executionWizardPage.exitMessage.detail", result.getExitMessage(), feature.substring(0,1).toUpperCase() + feature.substring(1), sqlFileName));
		                        }
	                        }
                        }else{
                        	result.getFeatures().add(feature);
                        	if(null != result.getBlockedFeature() && null != result.getFeatures()){
                        		result.getFeatures().remove(result.getBlockedFeature());
                        	}
                        }
                        
                        Map<String, CommandResultInfo> resultMap = DBWizardInputs
                                .getFeaturesResultsMap(this.getWizard());
                        if (resultMap == null) {
                            resultMap = new HashMap<String, CommandResultInfo>();
                        }
          
                        resultMap.put(feature + "_" + sqlFileName, result);
                        DBWizardInputs.setFeaturesResultsMap(this.getWizard(), resultMap);
                    
                    }
                }
            }
			logger.info("+++wizard result======" + result.getExitMessage());			
			getWizard().setExecResult(result);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "dbconfig.severe.execute.task", e);
		}
		
		if(!DBWizardInputs.isNannyMode(this.getWizard())){
			executionFinish();
		}
	}

	public IWizardPage getPreviousPage() {
		
		if(!DBWizardInputs.isNannyMode(this.getWizard())){
			return null;
		}else{
			return this.getWizard().getPage(Constants.WIZARD_PAGE_EXECUTION_NOTE);
		}
		
	}
	
	public IWizardPage getNextPage() {
		
		Wizard parentWizard = getWizard();
		IWizardPage finishPage = null;
		
		if(!DBWizardInputs.isNannyMode(this.getWizard())){
			;
		}else{
					
			DBWizardInputs.setCommandCount(getWizard(), DBWizardInputs.getCommandCount(getWizard()) + 1);
	
			int featureCount = DBWizardInputs.getFeatureCount(parentWizard);
			int featuresSize = DBWizardInputs.getFeatures(parentWizard).size();
			int commandCount = DBWizardInputs.getCommandCount(parentWizard);
			Map<String, List<List<String>>> map = DBWizardInputs.getFeaturesCommandsMap(parentWizard);
			String featureStr = DBWizardInputs.getFeatures(parentWizard).get(featureCount);
			int featureCmdsSize = map.get(featureStr).size();
			
			if(DBWizardInputs.isNannyMode(parentWizard) && 
					(featureCount < featuresSize -1 || (featureCount == featuresSize - 1 && commandCount < featureCmdsSize))){

				finishPage = parentWizard.getPage(Constants.WIZARD_PAGE_EXECUTION_NOTE);
			}else{
				finishPage = parentWizard.getPage(Constants.WIZARD_PAGE_FINISH_PANEL);
			}
		}
		
		return finishPage;
	}

	private void executionFinish() {
	    logger.info("===========enter excution Finish=======");
		getWizard().getParentDialog().getShell().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
			            logger.info("===========enter asyncExec=======");
//			          setCancelPrevButtonEnable(true);
			            Wizard parentWizard = getWizard();
			            logger.info("===========parentWizard======="+parentWizard);
			            IWizardPage finishPage = parentWizard.getPage(Constants.WIZARD_PAGE_FINISH_PANEL);
			            logger.info("===========finish page======="+finishPage);
			           // parentWizard.getPage("WIZARD_PAGE_EXECUTION").dispose();
			           // logger.info("===========can currentfinish page======="+ parentWizard.getPage("WIZARD_PAGE_EXECUTION").isPageComplete());
			          //  logger.info("===========can finish page======="+parentWizard.canFinish());
			              parentWizard.getContainer().showPage(finishPage);
			              logger.info("===========end asyncExec=======");
					}
				});
	     logger.info("===========succ excution Finish=======");
	}

	protected void updateDialogButtons() {
		DBWizard wizard = this.getWizard();
		DBWizardDialog parentDialog = wizard.getParentDialog();
		try {
			parentDialog.getTheCancelButton().setEnabled(false);
			parentDialog.getTheNextButton().setText(Messages.getString("button.next.text"));
			parentDialog.getTheNextButton().setEnabled(false);
			parentDialog.getThePrevButton().setEnabled(false);
		} catch (Exception e) {
		}
	}
	
	private void setCancelPrevButtonEnable(boolean enable) {
		DBWizardDialog parentDialog = getWizard().getParentDialog();
		parentDialog.getTheCancelButton().setEnabled(enable);
		parentDialog.getThePrevButton().setEnabled(enable);
	}
	
		
		
			
//			indexStart = sb.toString().lastIndexOf("lib") + "lib".length() + 1;
		
			
	
}
