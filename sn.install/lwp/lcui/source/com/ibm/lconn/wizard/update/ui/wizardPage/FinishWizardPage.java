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
package com.ibm.lconn.wizard.update.ui.wizardPage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.common.LCUtil;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.lconn.wizard.update.ui.UpdateWizard;
import com.ibm.lconn.wizard.update.ui.UpdateWizardDialog;
import com.ibm.lconn.wizard.update.ui.UpdateWizardInputs;
import com.ibm.websphere.product.history.xml.updateEvent;

public class FinishWizardPage extends CommonPage {
	
	public static final Logger logger = LogUtil.getLogger(FinishWizardPage.class);
	
	private Set<String> features = new HashSet<String>();
	public static final int MAX = 500;
	private int[] line = new int[MAX];
	private int num = 0;
	private static String SPACE = " ";
	private Button button;
	private int count = 0;

	/**
	 * Create the wizard
	 */
	public FinishWizardPage() {
		super(Constants.WIZARD_PAGE_FINISH);
	}

	public void onShow(Composite parent) {
		logger.log(Level.INFO,"Enter Finish Page");
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			setTitle(Messages.getString("label.wizard.finish.title.install"));
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			setTitle(Messages.getString("label.wizard.finish.title.uninstall"));
		}
		String text = getShowText();
		
		int[] bold = new int[this.num];
		for (int i = 0; i < this.num; i++) {
			bold[i] = this.line[i];
		}
		
		String desc = "";
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			desc = Messages.getString("label.wizard.finish.install.desc"); 
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			desc = Messages.getString("label.wizard.finish.uninstall.desc"); 
		}
		new BIDIStyledText(parent, desc, text, null, null, bold);
	}

	private String getShowText() {
		try{
		StringBuilder message = new StringBuilder();
		int line = 0;
		this.num = 0;
		
		Vector<updateEvent> updatesEvents = UpdateWizardInputs.getUpdateEvens(getWizard());
		logger.log(Level.INFO, "updatesEvents: " + updatesEvents);
		
		String actionStr = "";
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			actionStr = Messages.getString("label.action.select.install");
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			actionStr = Messages.getString("label.action.select.uninstall");
		}
		message.append(Messages.getString("update.wizard.show.text.actoin")).append(SPACE).append(actionStr);
		message.append(Constants.UI_LINE_SEPARATOR);
		line++;
		message.append(Messages.getString("update.wizard.show.text.lc.location")).append(SPACE).append(System.getProperty(com.ibm.lconn.common.LCUtil.LC_HOME));
		message.append(Constants.UI_LINE_SEPARATOR);
		line++;
		message.append(Constants.UI_LINE_SEPARATOR);
		line++;
		
		List<EFixComponent> efixes = UpdateWizardInputs.getSelectedFixes((getWizard()));
		
		CommonHelper.resetFeaturesStr(features, efixes);
		Object[] featuresTmp = features.toArray();
		Arrays.sort(featuresTmp);
		for(Object featureObj : featuresTmp){
			String feature = (String) featureObj;
			this.line[this.num] = line;
			message.append(Messages.getString(feature.toLowerCase() + ".capitalized"));
			this.num++;
			message.append(Constants.UI_LINE_SEPARATOR);
			line++;
			message.append(SPACE).append(Messages.getString("update.wizard.show.text.fix")).append(SPACE);
			for(EFixComponent efix : efixes){	
				if(feature.equalsIgnoreCase(efix.getInstallDescShort().substring(0, efix.getInstallDescShort().indexOf(":")))){				
					message.append(efix.getIdStr()).append(",").append(SPACE);		
				}		
			}
			message.deleteCharAt(message.length() - 1);
			message.deleteCharAt(message.length() - 1);
			message.append(Constants.UI_LINE_SEPARATOR);
			line++;
			message.append(SPACE).append(Messages.getString("update.wizard.show.text.result")).append(SPACE);
			StringBuilder tmpId = new StringBuilder("");
			for(EFixComponent efix : efixes){
				if(feature.equalsIgnoreCase(efix.getInstallDescShort().substring(0, efix.getInstallDescShort().indexOf(":")) + "")){	
					for(updateEvent ue : updatesEvents){
						if(efix.getIdStr().equals(ue.getId()) && "failed".equals(ue.getResult())){
							tmpId.append(ue.getId()).append(",").append(SPACE);
						}
					}
				}		
			}
			if(!"".equals(tmpId.toString())){
				tmpId.deleteCharAt(tmpId.length() - 1);
				tmpId.deleteCharAt(tmpId.length() - 1);
			}	
			
			if(!"".equals(tmpId.toString())){
				if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
					message.append(Messages.getString("update.wizard.show.text.result.install.error", tmpId));
				}
				if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
					message.append(Messages.getString("update.wizard.show.text.result.uninstall.error", tmpId));
				}	
			}else{
				if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
					message.append(Messages.getString("update.wizard.show.text.result.insatll.success"));
				}
				if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
					message.append(Messages.getString("update.wizard.show.text.result.uninsatll.success"));
				}
			}
			message.append(Constants.UI_LINE_SEPARATOR);
			line++;
			message.append(Constants.UI_LINE_SEPARATOR);
			line++;	
		}	
		message.append(Constants.UI_LINE_SEPARATOR);
		line++;
		
		message.append(Messages.getString("update.wizard.show.text.view.log", CommonHelper.getWizardLogFilesPath()));
		message.append(Constants.UI_LINE_SEPARATOR);
		line++;
		
		return message.toString();
		}catch(Exception e){
			logger.log(Level.SEVERE, e.getStackTrace().toString());
//			e.printStackTrace();
			return "";
		}
	}
	
	protected void updateDialogButtons() {
		UpdateWizard wizard = this.getWizard();
		UpdateWizardDialog parentDialog = wizard.getParentDialog();
		try {
			parentDialog.getTheCancelButton().setEnabled(false);
			parentDialog.getTheNextButton().setEnabled(true);
			parentDialog.getTheNextButton().setText(Messages.getString("button.finish.text"));
			parentDialog.getThePrevButton().setEnabled(false);
		} catch (Exception e) {
		}
	}
	
	public boolean isInputValid() {
		getShell().dispose();
		this.setNextPage(null);
		return true;
	}
	
	public IWizardPage getPreviousPage() {
		return null;
	}
		
	public IWizardPage getNextPage() {
		return super.getNextPage();
	}

}
