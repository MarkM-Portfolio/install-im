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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.lconn.wizard.update.ui.UpdateWizardDialog;
import com.ibm.lconn.wizard.update.ui.UpdateWizardInputs;

public class SummaryWizardPage extends CommonPage {
	
	public static final Logger logger = LogUtil.getLogger(SummaryWizardPage.class);
	
	private Set<String> features = new HashSet<String>();
	public static final int MAX = 500;
	private int[] line = new int[MAX];
	private int num = 0;
	private static String SPACE = " ";
	private Button button;
	private int count = 0;
	
	private String logPath;
//	public static final Logger logger = LogUtil.getLogger(ExecutionWizardPage.class);


	/**
	 * Create the wizard
	 */
	public SummaryWizardPage() {
		super(Constants.WIZARD_PAGE_SUMMARY);	
	}

	protected void updateDialogButtons() {
		UpdateWizardDialog parentDialog = getWizard().getParentDialog();
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			parentDialog.getTheNextButton().setText(Messages.getString("button.install.text"));
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			parentDialog.getTheNextButton().setText(Messages.getString("button.uninstall.text"));
		}	
	}

	@Override
	public void onShow(Composite parent) {
		logger.log(Level.INFO,"Enter Summary Page");
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			setTitle(Messages.getString("label.wizard.summary.title.install"));
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			setTitle(Messages.getString("label.wizard.summary.title.uninstall"));
		}
		String text = getShowText();
		int[] bold = new int[this.num];
		for (int i = 0; i < this.num; i++) {
			bold[i] = this.line[i];
		}

		String desc = "";
		if(Constants.OPERATION_TYPE_INSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			desc = Messages.getString("label.wizard.summary.install.desc"); 
		}
		if(Constants.OPERATION_TYPE_UNINSTALL_FIXES.equals(UpdateWizardInputs.getActionId(getWizard()))){
			desc = Messages.getString("label.wizard.summary.uninstall.desc"); 
		}
		new BIDIStyledText(parent, desc, text, null, null, bold);
		
	}

	private String getShowText() {
		StringBuilder message = new StringBuilder();
		int line = 0;
		this.num = 0;
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
		
		List<EFixComponent> efixes = UpdateWizardInputs.getSelectedFixes(getWizard());
		
		CommonHelper.resetFeaturesStr(features, efixes);
		Object[] featuresTmp = features.toArray();
		Arrays.sort(featuresTmp);
//		List<String> featuresTmp = (List<String>)  Arrays.asList(features.toArray());
//		Collections.sort(featuresTmp);
		for(Object featureObj : featuresTmp){
			String feature = (String) featureObj;
			this.line[this.num] = line;
			message.append(Messages.getString(feature.toLowerCase() + ".capitalized"));
			this.num++;
			message.append(Constants.UI_LINE_SEPARATOR);
			line++;
			message.append(SPACE).append(Messages.getString("update.wizard.show.text.fix")).append(SPACE);
			for(EFixComponent efix : efixes){	
				if(feature.equalsIgnoreCase(efix.getInstallDescShort().substring(0, efix.getInstallDescShort().indexOf(":")) + "")){				
					message.append(efix.getIdStr()).append(",").append(SPACE);		
				}		
			}
			message.deleteCharAt(message.length() - 1);
			message.deleteCharAt(message.length() - 1);
			message.append(Constants.UI_LINE_SEPARATOR);
			line++;
			message.append(Constants.UI_LINE_SEPARATOR);
			line++;
		}	
		
		return message.toString();
	}

}































