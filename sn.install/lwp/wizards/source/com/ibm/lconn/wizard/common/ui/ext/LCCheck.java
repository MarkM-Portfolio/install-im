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
package com.ibm.lconn.wizard.common.ui.ext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCCheck extends LCWizardInput {

	private Button checkButton;

	public LCCheck(Composite parent, WizardPageInputData data) {
		super(parent, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageInputData data = getData();
		Assert.isTrue(data.isCheck());
		
		checkButton = new Button(parent, getButtonStyle());
		checkButton.setText(data.getLabel());
	}
	
	public void addListener(int eventTYpe, Listener action){
		if(eventTYpe == SWT.Selection){
			this.checkButton.addListener(SWT.Selection, action);
		}
	}
	

	private int getButtonStyle() {
		return SWT.CHECK;
	}

	@Override
	public void setVisible(boolean visible) {
		this.checkButton.setVisible(visible);
		setVisibleValue(visible);
	}

	@Override
	public String getValue() {
		return (checkButton.getSelection() ? Constants.BOOL_TRUE
				: Constants.BOOL_FALSE);
	}

	@Override
	public void updateData() {
		WizardPageInputData data2 = getData();
		checkButton.setText(data2.getLabel());
		String value = DataPool.getValue(data2.getWizardId(), data2.getId());
		checkButton.setSelection(CommonHelper.equals(Constants.BOOL_TRUE,
				value ));
	}

	public void setEnable(boolean enable, String... data) {
		this.setEnabledValue(enable);
		this.checkButton.setEnabled(enable);
	}

}
