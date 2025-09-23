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

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCText extends LCWizardInput {

	private Label label;
	private Control control;
	private Text text;
	private StyledText styledText;

	public LCText(Composite parent, WizardPageInputData data) {
		this(parent, data, data.getStyle());
	}

	public LCText(Composite parent, WizardPageInputData data, int style) {
		super(parent, data, data.getStyle() | style);
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageInputData data = getData();
		Assert.isTrue(data.isText() || data.isTextArea());
		createLabel(parent, data);
		createText(parent, data);
	}

	private void createLabel(Composite parent, WizardPageInputData data) {
		if (!data.isText())
			return;
		label = new Label(parent, SWT.WRAP | getLabelStyle());
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label.setToolTipText(data.getTooltip());
		String labelValue = data.getLabel();
		if (labelValue != null)
			label.setText(labelValue);
	}

	private void createText(Composite parent, WizardPageInputData data) {
		int style = getTextStyle();
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		if (data.isTextArea() || data.isStyledText()) {
			style |= SWT.WRAP | SWT.V_SCROLL;
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		}
		if (data.isStyledText()) {
			styledText = new StyledText(parent, style);
			if (CommonHelper.isStyle(style, SWT.READ_ONLY) && label!=null) {
				styledText.setBackground(label.getBackground());
			}

			control = styledText;
		} else {
			text = new Text(parent, style);
			control = text;
		}

		control.setLayoutData(gridData);
		control.setToolTipText(data.getTooltip());
		Color bgColor = CommonHelper.parseColor(data.getBgColor());
		if(bgColor!=null) control.setBackground(bgColor);
	}

	private int getTextStyle() {
		return SWT.BORDER | getStyle();
	}

	private int getLabelStyle() {
		return SWT.NONE;
	}

	@Override
	public void setVisible(boolean visible) {
		if (getData().isText())
			this.label.setVisible(visible);
		this.control.setVisible(visible);
		setVisibleValue(visible);
	}

	@Override
	public String getValue() {
		String text2 = getText();
		if (text2 == null)
			text2 = Constants.TEXT_EMPTY_STRING;
		return text2;
	}

	private String getText() {
		if (getData().isStyledText())
			return styledText.getText();
		else
			return text.getText();
	}

	private void setText(String txt) {
		if (getData().isStyledText())
			styledText.setText(txt);
		else
			text.setText(txt);
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
		updateStyle();
		
	}

	private void updateStyle() {
		setTextStyle(null);
		WizardPageInputData data = getData();
		if (!data.isStyledText())
			return;
		Object styleObject = DataPool.getComplexData(data.getWizardId(), data
				.getId());
		if (styleObject instanceof List) {
			List<?> styleList = (List<?>) styleObject;
			Iterator<?> styleIterator = styleList.iterator();
			while (styleIterator.hasNext()) {
				StyleRange style = (StyleRange) styleIterator.next();
				setTextStyle(style);
			}
		} else if (styleObject instanceof StyleRange) {
			StyleRange style = (StyleRange) styleObject;
			setTextStyle(style);
		}
	}

	private void setTextStyle(StyleRange style) {
		if (styledText != null)
			styledText.setStyleRange(style);
	}

	@Override
	public void setEnable(boolean enable, String... data) {
		if (this.label != null)
			this.label.setEnabled(enable);
		if (this.control != null)
			this.control.setEnabled(enable);
		this.setEnabledValue(enable);
	}
}
