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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.data.LCGroupController;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCGroup extends LCWizardInput {

	protected Group group;
	
	protected LCGroupController controller;

	public LCGroupController getController() {
		return controller;
	}

	public void setController(LCGroupController controller) {
		this.controller = controller;
	}

	public LCGroup(Composite parent, WizardPageInputData data, int style) {
		super(parent, data, style);
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageInputData data = getData();
		Assert.isTrue(data.isGroup());
		group = new Group(parent, getGroupStyle());
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		group.setLayout(new GridLayout());
		
		String labelText = Messages.getString("LABEL." + data.getId());
		if(labelText != null && !"".equals(labelText.trim())) {
			Label label = new Label(group, SWT.WRAP);
			label.setText(labelText);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		String[] options = data.getOptions();
		for (String optionId : options) {
			final Button option = new Button(group, getButtonStyle());
			String optionText = Messages.getString("LABEL." + optionId);
			option.setText(optionText);
		}
	}

	protected int getButtonStyle() {
		return getStyle();
	}

	protected int getGroupStyle() {
		return getStyle();
	}

	@Override
	public void setVisible(boolean visible) {
		this.group.setVisible(visible);
		setVisibleValue(visible);
	}

	@Override
	public String getValue() {
		Control[] children = getChildren();
		boolean isCheck = CommonHelper.isStyle(getStyle(), SWT.CHECK);
		String value = null;
		for (int i = 0; i < children.length; i++) {
			Button child = (Button) children[i];
			if (child.getSelection()) {
				String val = getData().getOptions()[i];
				if (isCheck) {
					if (value == null)
						value = val;
					else
						value += "," + val;
				} else {
					value = val;
					break;
				}
			}
		}
		if (value == null)
			value = Constants.TEXT_EMPTY_STRING;
		return value;
	}

	public Control[] getChildren() {
		Control[] children = group.getChildren();
		ArrayList<Control> c = new ArrayList<Control>();
		for(Control ctrl : children) {
			if(ctrl instanceof Button) {
				c.add(ctrl);
			}
		}
		children = c.toArray(new Control[0]);
		return children;
	}

	@Override
	public void updateData() {
		WizardPageInputData data = getData();
		String value = DataPool.getValue(data.getWizardId(), data.getId());
		String[] options = data.getOptions();
		Control[] children = getChildren();
		boolean selected = false;
		for (int i = 0; i < options.length; i++) {
			String optionId = options[i];
			Button button = (Button) children[i];
			boolean selectionFound = selectionFound(data, value, optionId);
			button.setSelection(selectionFound);
			if (selectionFound) {
				selected = true;
			}
			LCGroupController controller = getController();
			if(controller!=null) button.setEnabled(controller.enable(optionId));
		}
		boolean isCheck = CommonHelper.isStyle(getStyle(), SWT.CHECK);
		if(!selected && !isCheck) {
			Button firstButton= (Button) children[0];
			firstButton.setSelection(true);
		}
	}

	protected boolean selectionFound(WizardPageInputData data, String value,
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
		if(data==null || data.length==0) this.group.setEnabled(enable);
		
		
	}
}

