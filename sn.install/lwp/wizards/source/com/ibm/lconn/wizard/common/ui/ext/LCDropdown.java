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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCDropdown extends LCWizardInput {

	private Combo dropDown;
	private Label label;

	public LCDropdown(Composite parent, WizardPageInputData data, int style) {
		super(parent, data, style);
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageInputData data = getData();
		Assert.isTrue(data.isDropDown());

		label = new Label(parent, getLabelStyle());
		String labelValue = data.getLabel();
		if (labelValue != null)
			label.setText(labelValue);

		dropDown = new Combo(parent, getDropDownStyle());
		dropDown.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		dropDown.setLayout(new GridLayout());
	}

	@SuppressWarnings("unchecked")
	private void updateOptions(WizardPageInputData data) {
		List<String> optionList = (List<String>) DataPool.getComplexData(data
				.getWizardId(), data.getId());
		if(optionList==null) optionList = new ArrayList<String>();
		List<String> actOptionList = new ArrayList<String>();
		for (int i = 0; i < optionList.size(); i++) {
			String option = optionList.get(i);
			if (CommonHelper.isEmpty(option))
				continue;
			actOptionList.add(option);
		}
		if (optionList == null) {
			return;
		}
		String[] options = (String[]) actOptionList.toArray(new String[0]);
		data.setOptions(options);
		dropDown.removeAll();
		for (String optionId : options) {
			if (optionId == null)
				continue;
			dropDown.add(optionId);
		}
	}

	private int getLabelStyle() {
		return getStyle();
	}

	private int getDropDownStyle() {
		return getStyle();
	}

	@Override
	public void setVisible(boolean visible) {
		this.dropDown.setVisible(visible);
		setVisibleValue(visible);
	}

	@Override
	public String getValue() {
		int index = dropDown.getSelectionIndex();
		if (index == -1)
			return dropDown.getText();
		
		if (Constants.OS_LINUX.equals(CommonHelper.getPlatformShortType()))
			return dropDown.getText();
		
		return getData().getOptions()[index];
	}

	@Override
	public void updateData() {
		WizardPageInputData data = getData();

		updateOptions(data);

		String value = DataPool.getValue(data.getWizardId(), data.getId());
		String[] options = data.getOptions();
		boolean optionsListNULL = options == null || options.length == 0;
		for (int i = 0; i < options.length; i++) {
			String optionId = options[i];
			if (selectionFound(data, value, optionId)) {
				dropDown.select(i);
				return;
			}
		}
		if (CommonHelper.isEmpty(value)
				|| CommonHelper.isStyle(getStyle(), SWT.READ_ONLY)) {
			if(!optionsListNULL){
				dropDown.select(0);
			}
		} else
			dropDown.setText(value);
		dropDown.update();
	}

	private boolean selectionFound(WizardPageInputData data, String value,
			String optionId) {
		if (data.isCheckGroup() && value.indexOf(Util.DELIM) != -1) {
			String[] values = Util.delimStr(value);
			for (String val : values) {
				if (selectionFound(data, val, optionId))
					return true;
			}
			return false;
		}
		return CommonHelper.equals(value, optionId)
				|| CommonHelper.equals(value, DefaultWizardDataLoader
						.evalValue(optionId));
	}

	@Override
	public void setEnable(boolean enable, String... data) {
		this.setEnabledValue(enable);
		if (this.dropDown != null)
			this.dropDown.setEnabled(enable);
		if (this.label != null)
			this.label.setEnabled(enable);
	}
}
