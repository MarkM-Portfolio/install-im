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

package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizard;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardDialog;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;

public class ExecutionDetailedCommandExportWizardPage extends CommonPage{
	private int[] line = new int[Constants.FEATURE_COUNT_MAX];
	private int num = 0;
	private static String SPACE = " ";
	private String text = "";

	//TODO modify by Maxi, feature merge flag (librarygcd, libraryos) -> library
	private boolean flagLibraryMerged = false;
	//TODO end
	
	public static final Logger logger = LogUtil.getLogger(ExecutionDetailedCommandExportWizardPage.class);
	
	public ExecutionDetailedCommandExportWizardPage() {
		super(Constants.WIZARD_PAGE_EXECUTION_DETAILDED_COMMAND_EXPORT); 
	}

	protected void updateDialogButtons() {
		DBWizard wizard = this.getWizard();
		DBWizardDialog parentDialog = wizard.getParentDialog();
		try {
			parentDialog.getTheCancelButton().setText(Messages.getString("button.cancel.text"));
			parentDialog.getTheCancelButton().setEnabled(false);
			parentDialog.getTheNextButton().setText(Messages.getString("button.finish.text"));

			parentDialog.getThePrevButton().setText(Messages.getString("button.prev.text"));
			parentDialog.getThePrevButton().setEnabled(true);
		} catch (Exception e) {
		}
	}

	

	public IWizardPage getNextPage() {
		this.setNextPage(Pages.getPage(Pages.EXECUTION_DETAILED_COMMAND_EXPORT_PAGE));
		return super.getNextPage();
	}

	public boolean isInputValid() {
		getShell().dispose();
	//	this.setNextPage(null);
		return true;
	}
	
	public void isInputValid_fake() {
		getShell().dispose();
	//	this.setNextPage(null);
	}

	public IWizardPage getPreviousPage() {
		this.setPreviousPage(Pages.getPage(Pages.SUMMARY_PAGE));
		return super.getPreviousPage();
	}
	
	
	
	@Override
	public void onShow(Composite parent) {
		setTitle(Messages.getString("dbWizard.executionNoteWizardPage.title." + DBWizardInputs.getActionId(getWizard()))); 
		text = getShowText();
		int[] bold = new int[this.num];
		for (int i = 0; i < this.num; i++) {
			bold[i] = this.line[i];
		}
	
		DBWizard wizard = this.getWizard();
		DBWizardDialog parentDialog = wizard.getParentDialog();
		parentDialog.getTheNextButton().setEnabled(false);	
		
		String dec = Messages.getString("dbWizard.executionDetailedCommandExportWizardPage.description");
		new BIDIStyledText(parent, dec, text, null, null, bold);
	
		Button saveButton = new Button(parent, SWT.NONE);
		saveButton.setText(Messages.getString("button.saveas.text"));
		saveButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
				}
				public void widgetSelected(SelectionEvent arg0) {
					FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
					fileDialog.setFilterPath(new File(".").getAbsolutePath());
//					fileDialog.setFileName("New Text Document.txt");
					fileDialog.setFilterExtensions(new String[]{"*.txt","*.*"});
//					fileDialog.setFilterNames(new String[]{"Text Documents(*.txt)", "All Files"});	
					String filePath = fileDialog.open();
					if(null != filePath && !"".equals(filePath)){
						OutputStreamWriter writer = null;
						try {
				//			boolean isWrite;
							File file = new File(filePath);
							while(file.exists())
							{
								boolean status =new MessageDialog(getShell(), Messages.getString("dbWizard.window.title"), null, Messages.getString("dbWizard.executionDetailedCommandExportWizardPage.fileExist"), MessageDialog.WARNING, new String[] { Messages.getString("button.YES.text"), Messages.getString("button.NO.text") }, 0).open() == 0;
								if(!status) {
									filePath = fileDialog.open();
									file = new File(filePath);
									continue;
								}else{
									break;
								}
							}	
				
							writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"));
							writer.write(text);
							writer.flush();
							writer.close();
							logger.log(Level.INFO, "The commands are saved to the file " + filePath);
					 	} catch (FileNotFoundException e) {
							try {
								writer.close();
							} catch (IOException e1) {
								logger.log(Level.SEVERE, e.getStackTrace().toString());
							} catch (NullPointerException e2){
								logger.log(Level.SEVERE, "Make sure the db user running dbWizard has the write access into the folder you choose.");
							}				
//							e.printStackTrace();
						} catch (IOException e) {
							try {writer.close();} catch (IOException e1) {}
							logger.log(Level.SEVERE, e.getStackTrace().toString());
//							e.printStackTrace();
						}
					}
				}
			});
	}
	
	private String getShowText(){
		int line = 0;
		this.num = 0;
		StringBuilder sb = new StringBuilder();
		DBWizard wizard = this.getWizard();
		String dbTypeId = DBWizardInputs.getDbType(wizard);
		List<String> features = DBWizardInputs.getFeatures(wizard);
		
		//*************jia****************
		
		Map<String, List<List<String>>> cmdMap = DBWizardInputs.getFeaturesCommandsMap(this.getWizard());
		for (String feature : features) {
			this.line[this.num] = line;
			//TODO modify by Maxi, special action of library_gcd and library_os
			if (Constants.FEATURE_LIBRARY_GCD.equals(feature) || Constants.FEATURE_LIBRARY_OS.equals(feature)) {
				if (!flagLibraryMerged) {
					sb.append(Messages.getString(Constants.FEATURE_LIBRARY + ".name"));
					this.num++;
					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;
					
					if (Constants.DB_ORACLE.equals(dbTypeId)) {
						
					} else {
						sb.append(Messages.getString(Constants.FEATURE_LIBRARY_GCD + ".name"));
						sb.append(Constants.UI_LINE_SEPARATOR);
						line++;
					}
					int i = 1;
					List<List<String>> commands = cmdMap.get(Constants.FEATURE_LIBRARY_GCD);
					for(List<String> command : commands){				
						if(commands.size() > 1){
							sb.append("  " + i + ".  ");
						}else{
							sb.append("   " + "   ");
						}
						sb.append(CommonHelper.getCommandStr(command, wizard));
						sb.append(Constants.UI_LINE_SEPARATOR); 
						line++;
						i++;
					}
					if (Constants.DB_ORACLE.equals(dbTypeId)) {
						commands = cmdMap.get(Constants.FEATURE_LIBRARY_OS);
						for(List<String> command : commands){				
							if(commands.size() > 1){
								sb.append("  " + i + ".  ");
							}else{
								sb.append("   " + "   ");
							}
							sb.append(CommonHelper.getCommandStr(command, wizard));
							sb.append(Constants.UI_LINE_SEPARATOR); 
							line++;
							i++;
						}
					}
					sb.append(Constants.UI_LINE_SEPARATOR);
					line++;
					
					if (Constants.DB_ORACLE.equals(dbTypeId)) {
						
					} else {
						sb.append(Messages.getString(Constants.FEATURE_LIBRARY_OS + ".name"));
						sb.append(Constants.UI_LINE_SEPARATOR);
						line++;
						i = 1;
						commands = cmdMap.get(Constants.FEATURE_LIBRARY_OS);
						for(List<String> command : commands){				
							if(commands.size() > 1){
								sb.append("  " + i + ".  ");
							}else{
								sb.append("   " + "   ");
							}
							sb.append(CommonHelper.getCommandStr(command, wizard));
							sb.append(Constants.UI_LINE_SEPARATOR); 
							line++;
							i++;
						}
						sb.append(Constants.UI_LINE_SEPARATOR);
						line++;
					}
					flagLibraryMerged = true;
				}
				continue;
			}
			//TODO end
			sb.append(Messages.getString(feature + ".name"));
			this.num++;
			sb.append(Constants.UI_LINE_SEPARATOR);
			line++;
			
			int i = 1;
			List<List<String>> commands = cmdMap.get(feature);
			for(List<String> command : commands){				
				if(commands.size() > 1){
					sb.append("  " + i + ".  ");
				}else{
					sb.append("   " + "   ");
				}
				sb.append(CommonHelper.getCommandStr(command, wizard));
				sb.append(Constants.UI_LINE_SEPARATOR); 
				line++;
				i++;
			}
			sb.append(Constants.UI_LINE_SEPARATOR);
			line++;
		}
		//TODO modify by Maxi, reset flag
		flagLibraryMerged = false;
		//TODO end
		return sb.toString();
	}
}
