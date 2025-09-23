/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.interfaces.DbCreationInterface;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardDialog;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.tdipopulate.backend.JDBCConnectionInfo;

public class ExecutionNoteWizardPage extends CommonPage {
	private int[] line = new int[Constants.FEATURE_COUNT_MAX];
	private int num = 0;
	private DbCreationInterface DBExe;
	private String logPath;
	private int count = 0;
//	public static final Logger logger = LogUtil.getLogger(ExecutionWizardPage.class);
	
	public ExecutionNoteWizardPage() {
		super(Constants.WIZARD_PAGE_EXECUTION_NOTE);
	}
	
	public static final Logger logger = LogUtil.getLogger(ExecutionWizardPage.class);

	public void onShow(Composite parent) {
		DBWizardDialog parentDialog = this.getWizard().getParentDialog();
		
		DBExe = DBWizard.getDBCreationInterface();
//		logPath = DBExe.createLogForTask();
		
		List<String> features = DBWizardInputs.getFeatures(this.getWizard());
//		Map<String, List<List<String>>> map = getExecuteCommands();
//		DBWizardInputs.setFeaturesCommandsMap(this.getWizard(), map);
		
		Map<String, List<List<String>>> map = DBWizardInputs.getFeaturesCommandsMap(this.getWizard());
		
		if(count==0){
			DBWizardInputs.setFeatureCount(this.getWizard(), 0);
			DBWizardInputs.setFeatureNameTmp(this.getWizard(), features.get(0));
			DBWizardInputs.setCommandCount(this.getWizard(), 0);
			DBWizardInputs.setCommandTmp(this.getWizard(), map.get(features.get(0)).get(0));
		}
		
		int featureCount = DBWizardInputs.getFeatureCount(this.getWizard());
		int commandCount = DBWizardInputs.getCommandCount(getWizard());
		if(commandCount < map.get(features.get(featureCount)).size()){
			DBWizardInputs.setCommandTmp(this.getWizard(), map.get(features.get(featureCount)).get(commandCount));	
		}else{
			DBWizardInputs.setFeatureCount(this.getWizard(), DBWizardInputs.getFeatureCount(this.getWizard()) + 1);
			DBWizardInputs.setCommandCount(this.getWizard(), 0);
			featureCount = DBWizardInputs.getFeatureCount(this.getWizard());
			DBWizardInputs.setCommandTmp(this.getWizard(), map.get(features.get(featureCount)).get(0));
			DBWizardInputs.setFeatureNameTmp(this.getWizard(), features.get(featureCount));
		}
		
		String actionId = DBWizardInputs.getActionId(this.getWizard());
		setTitle(Messages.getString("dbWizard.executionNoteWizardPage.title." + actionId)); //$NON-NLS-1$

		Composite container = CommonHelper.createScrollableControl(
				Composite.class, parent, SWT.NONE, SWT.V_SCROLL);
		container.setLayout(new GridLayout());
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		container.setLayout(gridLayout);
		
		StringBuilder sb = new StringBuilder();
		List<String> command = DBWizardInputs.getCommandTmp(this.getWizard());
		for(String cmd : command){
			sb.append(cmd).append(" ");
		}
		sb.deleteCharAt(sb.length()-1);
		
		String filepath_tmp = "";
		if(sb.toString().indexOf("connections") !=-1 && sb.toString().indexOf(".sql") !=-1){
			filepath_tmp = sb.toString().substring(sb.toString().indexOf("connections"),sb.toString().lastIndexOf(".sql")+".sql".length());
		}
		final String filepath = filepath_tmp;
		
		final String textStrCon = sb.toString();
		String textStr = sb.toString();
		if(Constants.DB_SQLSERVER.equals(DBWizardInputs.getDbType(this.getWizard())) 
				&& ((Constants.OPERATION_TYPE_CREATE_DB.equals(DBWizardInputs.getActionId(this.getWizard()))
				&& textStr.indexOf("-classpath") == -1)
				||
				(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard()))
						&& textStr.indexOf("forum")!=-1 && textStr.indexOf("createDb")!=-1	))
				){
			textStr = textStr.replace(textStr.substring(textStr.indexOf("password=")),"password=\"******\"");
		}
		if(Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(this.getWizard())) 
				&& ((Constants.OPERATION_TYPE_CREATE_DB.equals(DBWizardInputs.getActionId(this.getWizard())) 
				&& textStr.indexOf("-classpath") == -1)
				|| 
				(Constants.OPERATION_TYPE_UPGRADE_DB.equals(DBWizardInputs.getActionId(this.getWizard()))
					&& textStr.indexOf("forum")!=-1 && textStr.indexOf("createDb")!=-1	))
				){
			textStr = textStr.substring(0,textStr.lastIndexOf(".sql") + ".sql".length()) + " ******";
		}
		if(textStr.indexOf("-classpath") != -1){
			String tmpStr = textStr.substring(textStr.indexOf("-classpath") + "-classpath".length() + 1, textStr.indexOf("migrate.jar") + "migrate.jar".length());
			textStr = textStr.replace(tmpStr, "\"" + tmpStr + "\"");
			if(textStr.indexOf("-src") == -1){
				if(textStr.indexOf("-dbpassword") != -1){
				textStr = textStr.replace(textStr.substring(textStr.indexOf("-dbpassword")),"-dbpassword ******");	
				}
			}else{
				if(textStr.indexOf("-dbpassword") != -1){
				textStr = textStr.replace(textStr.substring(textStr.indexOf("-dbpassword") + "-dbpassword".length() + 1,textStr.indexOf("-src") - 1),"******");
				}
			}
			if(textStr.indexOf("-srcdbpassword") != -1){
				textStr = textStr.replace(textStr.substring(textStr.indexOf("-srcdbpassword")),"-srcdbpassword ******");	
			}
			if(textStr.indexOf("MigrateCommunityTheme_2_5_To_3_0") != -1){
				if(!Constants.DB_ORACLE.equals(DBWizardInputs.getDbType(this.getWizard()))){
				tmpStr = textStr.substring(textStr.indexOf("-source"), textStr.indexOf("-target") - 1);
				textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
				tmpStr = textStr.substring(textStr.indexOf("-target"));
				textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
				}else{
					tmpStr = textStr.substring(textStr.indexOf("-source"), textStr.indexOf("-SID") - 1);
					textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
					tmpStr = textStr.substring(textStr.indexOf("-target"), textStr.lastIndexOf("-SID") - 1);
					textStr = textStr.replace(tmpStr,tmpStr.substring(0, tmpStr.lastIndexOf(" ") + 1) + "******");
				}
			}
		}
		textStr = textStr.replace("\\\\", "\\");
		Label message = new Label(container, SWT.WRAP);
		message.setText(Messages
				.getString("dbWizard.executionNoteWizardPage.description", textStr));
		message.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		final Label space = new Label(container, SWT.NONE);
		space.setText("\n\n");
		
		
		Button button = new Button(container, SWT.NONE);
		if(textStrCon.indexOf("-classpath") == -1){
			button.setText(Constants.BUTTON_TEXT_OPENSQL);			
		}else{
			button.setText(Constants.BUTTON_TEXT_VIEWJAR);					
		}
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(textStrCon.indexOf("-classpath") == -1){
					if (!CommonHelper.openLog(filepath))
						showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.cannot.launch.file"));		
				}else{
					
					if (!CommonHelper.openHTML(new File("lib").getAbsolutePath()))
						showInputCheckErrorMsg(ErrorMsg.getString("dbwizard.error.cannot.open.directory"));					
				}
				

			}
		});

//		Label message = new Label(container, SWT.WRAP);
//		message.setText(Messages
//				.getString("dbWizard.executionNoteWizardPage.description")
//				+ "\n\n" + sb.toString());
//		message.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
//		String decription = Messages.getString("dbWizard.executionNoteWizardPage.description") + "\n\n" + sb.toString() + "\n" + "\uFFFC";
//		Label dec = new Label(parent, SWT.WRAP);
//		dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
//		dec.setText(decription); //$NON-NLS-1$

//		Composite composite = CommonHelper.createEmptyPanel(container, new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		
		
		

		
		
//		String text = getShowText(filepath);
//		int[] bold = new int[this.num];
//		for (int i = 0; i < this.num; i++) {
//			bold[i] = this.line[i];
//		}
//		
//		System.out.print(line.length + " : " +  num);
//		
//		new BIDIStyledText(parent, dec, text, null, null, bold);
		
//		DBWizardDialog parentDialog = this.getWizard().getParentDialog();
		count ++;
		
		Display display = getWizard().getParentDialog().getShell().getDisplay();
		if(!display.isDisposed()){
			Runnable run = new Runnable(){
				public void run() {
					DBWizardDialog parentDialog = getWizard().getParentDialog();
					boolean executePageEnteredFlag = DBWizardInputs.isExecutePageEnteredFlag(getWizard());
					if(executePageEnteredFlag){
						parentDialog.getThePrevButton().setEnabled(false);
					}else{
						parentDialog.getThePrevButton().setEnabled(true);
					}
				}
			};
			display.asyncExec(run);
		}
		
	}

	public String getShowText(String filepath){
		int line = 0;
		this.num = 0;
		this.line[this.num] = line;
		this.num++;
		StringBuilder sb = new StringBuilder();
		File file = new File(".",filepath);
		LineNumberReader reader = null;
		try {
			FileInputStream fis = new FileInputStream(filepath);
//			FileChannel fc = fis.getChannel();
//			MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//			sb.append(buffer.asCharBuffer().toString());
			reader = new LineNumberReader(new InputStreamReader(fis));
			String tmpStr = "";
			while((tmpStr=reader.readLine())!=null){
				sb.append(tmpStr).append(Constants.UI_LINE_SEPARATOR);
				line++;
			}
		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		return sb.toString();
	}
	

	public IWizardPage getPreviousPage() {
		boolean executePageEnteredFlag = DBWizardInputs.isExecutePageEnteredFlag(this.getWizard());
		if(executePageEnteredFlag){
			return this;
		}else{
			return getWizard().getPage(Constants.WIZARD_PAGE_DB_SUMMARY);
		}
		
	}
	
	public IWizardPage getNextPage() {
		
		return getWizard().getPage(Constants.WIZARD_PAGE_EXECUTION);
		
	}
	
	protected void updateDialogButtons() {
		DBWizard wizard = this.getWizard();
		DBWizardDialog parentDialog = wizard.getParentDialog();
		try {
			
//			parentDialog.getTheCancelButton().setText(Messages.getString("button.cancel.text"));
			parentDialog.getTheCancelButton().setEnabled(true);
			parentDialog.getTheNextButton().setText(Messages.getString("button.execute.text"));

			parentDialog.getThePrevButton().setText(Messages.getString("button.prev.text"));
//			boolean executePageEnteredFlag = DBWizardInputs.isExecutePageEnteredFlag(this.getWizard());
//			if(executePageEnteredFlag){
//				parentDialog.getThePrevButton().setEnabled(false);
//			}else{
//				parentDialog.getThePrevButton().setEnabled(true);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
