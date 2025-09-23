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
package com.ibm.lconn.wizard.common.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.interfaces.EmbeddedAction;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;

/**
 * @author Jun Jing Zhang
 * 
 */
public class LCWelcomePanel extends LCWizardPage {

	public LCWelcomePanel(WizardPageData data) {
		super(data);
	}

	public void createControl(Composite parent) {
		Composite rootPanel = new Composite(parent, SWT.NONE);
		rootPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		CommonHelper.setColumn(rootPanel, 2);

		
		/*final Composite rootPanelInner = CommonHelper.createScrollableControl(
				Composite.class, rootPanel, SWT.NONE, SWT.V_SCROLL);*/
		Composite rootPanelInner = new Composite(rootPanel, SWT.None);
		String id = getWizard().getId();
		
		Label imageLabel = new Label(rootPanel, SWT.NONE);
		GridData imageGridData = new GridData(SWT.LEFT, SWT.TOP, false, true);
		imageGridData.horizontalIndent = 10;
		imageLabel.setLayoutData(imageGridData);
		imageLabel.setImage(ResourcePool.getWizardSideImage());
		
		
		rootPanelInner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout rootLayout = new GridLayout();
		rootLayout.verticalSpacing = 0;
		rootLayout.marginWidth = 0;
		rootLayout.horizontalSpacing = 0;
		rootPanelInner.setLayout(rootLayout);

		String licenceTxt = MessageUtil.getMsg(id, "WIZARD_PAGE_COMMON_WELCOME", "WELCOME_LICENCE");
		
		String detailText = MessageUtil.getMsgWithParameter(id+".welcome.message",Constants.BUTTON_TEXT_SYMBOL);
		EmbeddedAction action = new EmbeddedAction(){
			
			public String execute() {
				String infocenter = MessageUtil.getMsg("URL."+getData().getWizardId()+".infocenter");
				return "" + CommonHelper.openHTML(infocenter);
			}

			public String getLogPath() {
				return null;
			}

			public String getActionLabel() {
				// TODO Auto-generated method stub
				return MessageUtil.getMsg(getWizard().getId(), "WIZARD_PAGE_COMMON_WELCOME", "WELCOME_INFOCENTER");
			}

			public int getHorizontalAlignment() {
				return SWT.CENTER;
			}

			public int getVerticalAlignment() {
				return SWT.CENTER;
			}

			public String getTDILogPath() {
				return null;
			}
		};
		
		new BIDIStyledText(rootPanelInner, null, detailText, licenceTxt, action, null);
		setControl(rootPanel);
	}
	
	@Override
	protected void updatePageData() {
		LCWizard wizard2 = getWizard();
		int buttonStyle = wizard2.getButtonStyle() |  Constants.DISABLE_BACK;
		wizard2.setButtonStyle(buttonStyle);
		
		// TODO Auto-generated method stub
		super.updatePageData();
	}

}
