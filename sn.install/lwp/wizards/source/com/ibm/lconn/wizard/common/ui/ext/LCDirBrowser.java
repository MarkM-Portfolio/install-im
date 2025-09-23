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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCDirBrowser extends LCWizardInput {

	private Text text;
	private Label label;
	private Button button;
	
	public LCDirBrowser(Composite parent, WizardPageInputData data) {
		super(parent, data);
	}
	
	private void fireButtonSelected() {
		DirectoryDialog directoryDialog = new DirectoryDialog(Display
				.getCurrent().getActiveShell());
		if (text.getText() != null)
			directoryDialog.setFilterPath(text.getText());
		String dirBrowsePanelMsg = getData().getValue(Constants.WIDGET_PROP_DIR_BROWSER_DIALOG_MSG);
		if (CommonHelper.isEmpty(dirBrowsePanelMsg))
			dirBrowsePanelMsg = Messages
					.getString(Constants.DEFAULT_DIR_BROWSER_MSG);
		directoryDialog.setMessage(dirBrowsePanelMsg); //$NON-NLS-1$

		String dir = directoryDialog.open();
		if (dir != null) {
			setValue(dir);
		}
	}
	
	public void setValue(String val){
		if(val!=null) text.setText(val);
		super.setValue(val);
	}

	@Override
	public void createControl(Composite parent) {
		label = new Label(parent, SWT.WRAP | getLabelStyle());
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Composite emptyCon = CommonHelper.createEmptyPanel(parent, new GridData(SWT.FILL, SWT.CENTER, true, false), 2);
		text = new Text(emptyCon, getTextStyle());
		GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		text.setLayoutData(textGridData);		
		button = new Button(emptyCon, SWT.NONE);
		GridData buttonGridData = new GridData();
		buttonGridData.horizontalIndent = 10;
		button.setLayoutData(buttonGridData);
		
		WizardPageInputData data = getData();
		String label = data.getLabel();
		if(label!=null) this.label.setText(label);
		
		String buttonTxt = data.getValue(Constants.WIDGET_PROP_DIR_BROWSER_BUTTON_TEXT);
		if(CommonHelper.isEmpty(buttonTxt)){
			this.button.setText(MessageUtil.getLabel("dir.browser.button"));
		}else{
			this.button.setText(buttonTxt);
		}
		
		addListener();
	}

	private void addListener() {
		button.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event arg0) {
				fireButtonSelected();
			}});
	}

	private int getTextStyle() {
		return SWT.BORDER;
	}

	private int getLabelStyle() {
		return SWT.NONE;
	}

	@Override
	public void setVisible(boolean visible) {
		button.setVisible(visible);
		text.setVisible(visible);
		button.setVisible(visible);
	}

	@Override
	public String getValue() {
		String text2 = text.getText();
		if(CommonHelper.isEmpty(text2)) text2 = Constants.TEXT_EMPTY_STRING;
		return text2;
	}

	@Override
	public void updateData() {
		WizardPageInputData data = getData();
		if(label!=null){
			label.setText(MessageUtil.getLabel(data.getId()));
			label.getParent().layout();
		}
		String dir = DataPool.getValue(data.getWizardId(), data.getId());
		if(dir==null) dir="";
		text.setText(dir);
		
	}

	@Override
	public void setEnable(boolean enable, String... data) {
		this.setEnabledValue(enable);
		if(this.text!=null) this.text.setEnabled(enable);
		if(this.label!=null) this.label.setEnabled(enable);
		if(this.button!=null) this.label.setEnabled(enable);
		
	}

}
