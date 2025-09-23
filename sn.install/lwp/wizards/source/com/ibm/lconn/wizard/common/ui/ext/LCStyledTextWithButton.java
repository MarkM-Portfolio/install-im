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
import org.eclipse.swt.widgets.Composite;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.interfaces.EmbeddedAction;
import com.ibm.lconn.wizard.common.interfaces.LCAction;
import com.ibm.lconn.wizard.common.ui.BIDIStyledText;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public class LCStyledTextWithButton extends LCWizardInput {

	private Composite parent;
	private String actionLabel;

	public LCStyledTextWithButton(Composite parent, WizardPageInputData data) {
		super(parent, data);
	}

	public void createControl(Composite parent) {
		createUI(parent);
	}

	
	public static void setTextAndButton(String wizardId, String inputId,
			String detailText, String actionLabel, LCAction action) {
		DataPool.setValue(wizardId,
				inputId + "." + Constants.TEXT_BUTTON_LABEL, actionLabel);
		DataPool.setValue(wizardId, inputId + "."
				+ Constants.TEXT_BUTTON_DETAIL, detailText);
		DataPool.setComplexData(wizardId, inputId + "."
				+ Constants.TEXT_BUTTON_ACTION, action);
	}
	

	private void createUI(Composite parent) {		
		WizardPageInputData data = getData();
		String wizardId = data.getWizardId();
		String inputId = data.getId();

		String detailText = DataPool.getValue(wizardId, inputId + "."
				+ Constants.TEXT_BUTTON_DETAIL);
		final LCAction superAction = (LCAction) DataPool.getComplexData(wizardId, inputId + "."
				+ Constants.TEXT_BUTTON_ACTION);
		actionLabel = DataPool.getValue(wizardId,
				inputId + "." + Constants.TEXT_BUTTON_LABEL);
		EmbeddedAction action = new EmbeddedAction() {

			public String execute() {
				superAction.execute();
				return superAction.getLogPath();
			}

			public String getLogPath() {
				return superAction.getLogPath();
			}

			public String getActionLabel() {				
				return actionLabel;
			}

			public int getHorizontalAlignment() {
				return SWT.LEFT;
			}

			public int getVerticalAlignment() {
				return SWT.TOP;
			}
		};

		this.parent = parent;
		if (this.parent.getData("summary") != null) {
			BIDIStyledText text = (BIDIStyledText)this.parent.getData("summary");
			text.dispose();
		}
		BIDIStyledText text = new BIDIStyledText(parent, null, detailText, null, action, null);
		this.parent.setData("summary", text);
	}

	public void updateData() {
		createUI(parent);
		parent.layout();
	}

	@Override
	public String getValue() {
		return DataPool.getValue(getData().getWizardId(),
				Constants.TEXT_BUTTON_DETAIL);
	}

	/* (non-Javadoc)
	 * @see com.ibm.lconn.wizard.common.ui.ext.LCInput#setEnable(boolean)
	 */
	@Override
	public void setEnable(boolean enable, String... data) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.lconn.wizard.common.ui.ext.LCInput#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}
}
