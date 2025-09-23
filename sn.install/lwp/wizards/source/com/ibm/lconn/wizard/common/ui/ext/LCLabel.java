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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCLabel extends LCWizardInput {

	private Label label;

	public LCLabel(Composite parent, WizardPageInputData data) {
		this(parent, data, data.getStyle());
	}

	public LCLabel(Composite parent, WizardPageInputData data, int style) {
		super(parent, data, data.getStyle() | style);
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageInputData data = getData();
		Assert.isTrue(data.isLabel());
		createLabel(parent, data);
	}

	private void createLabel(Composite parent, WizardPageInputData data) {
		if (!data.isLabel())
			return;
		label = new Label(parent, SWT.WRAP | getLabelStyle());
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label.setToolTipText(data.getTooltip());
		String labelValue = data.getLabel();
		if (labelValue != null)
			label.setText(labelValue);
	}

	private int getLabelStyle() {
		return SWT.NONE;
	}

	@Override
	public void setVisible(boolean visible) {
			this.label.setVisible(visible);
		setVisibleValue(visible);
	}

	@Override
	public String getValue() {
		return label.getText();
	}

	private void setText(String txt) {
		if(!CommonHelper.isEmpty(txt))label.setText(txt);
	}

	@Override
	public void updateData() {
		WizardPageInputData data = getData();
		String val = DataPool.getValue(data.getWizardId(), data.getId());
		if(label!=null){
			label.setText(MessageUtil.getLabel(data.getId()));
			label.getParent().layout();
		}
		if (val != null){
			setText(val);
		}else{
			setText("");
		}
	}

	@Override
	public void setEnable(boolean enable, String... data) {
			this.label.setEnabled(enable);
		this.setEnabledValue(enable);
	}
}
