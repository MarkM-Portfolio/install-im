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

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.interfaces.PageController;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.data.WizardData;
import com.ibm.lconn.wizard.common.validator.ValidationMessage;

public abstract class LCWizard extends Wizard{
	protected LCWizardDialog parentDialog;
	private PageController pageController;
	private WizardData data;
	private String id;
	private boolean forcePreviousAndNextButtons;
	private int buttonStyle;
	
	public LCWizard(String id){
		this.id = id;
		this.data = new WizardData(id);
	}

	public LCWizardDialog getParentDialog() {
		return parentDialog;
	}
	
	public void setParentDialog(LCWizardDialog parent){
		this.parentDialog = parent;
	}
	
	public boolean needsPreviousAndNextButtons() {
        return forcePreviousAndNextButtons;
    }
	
	/**
     * Controls whether the wizard needs Previous and Next buttons even if it
     * currently contains only one page.
     * <p>
     * This flag should be set on wizards where the first wizard page adds
     * follow-on wizard pages based on user input.
     * </p>
     * 
     * @param b
     *            <code>true</code> to always show Next and Previous buttons,
     *            and <code>false</code> to suppress Next and Previous buttons
     *            for single page wizards
     */
    public void setForcePreviousAndNextButtons(boolean b) {
        forcePreviousAndNextButtons = b;
    }

	public void updateData(){
		 IWizardPage[] pages = getPages();
		 for (IWizardPage iWziardPage : pages) {
			LCWizardPage page = (LCWizardPage) iWziardPage;
			page.updatePageData();
		}
	}
	

	public IWizardPage getNextPage() {
		if (getPageCount() == 0) {
			return null;
		}
		LCWizardPage current = (LCWizardPage) getContainer().getCurrentPage();
		String currentPage = current.getName();
		current.collectInput();
		PageController controller = getPageController();
		IWizardPage page;
		if(controller!=null){
			String nextPage = controller.performAction(currentPage, Constants.WIZARD_ACTION_NEXT);
			processValidationErrors(controller);
			page = getPage(nextPage);
		}else{
			page = current.getNextPage();
		}
		return page;
	}
	
	protected void processValidationErrors(PageController controller) {
		List<ValidationMessage> messages = controller.getMessages();
		if(messages!=null && !messages.isEmpty()){
			ValidationMessage validationMessage = messages.get(0);
			validationMessage.getMessageType();
			MessageDialog dialog = new MessageDialog(getShell(), MessageUtil.getWizardTitle(getData().getId()), null, validationMessage.getMessge(), validationMessage.getMessageType(), new String[] { Messages.getString("button.OK.text") }, 0); // ok
		    dialog.open();
		}
	}
	
	public PageController getPageController(){
		return this.pageController;
	}

	public void setPageController(PageController pageController) {
		this.pageController = pageController;
	}

	/**
	 * @return the data
	 */
	public WizardData getData() {
		return data;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public int getButtonStyle() {
		return buttonStyle;
	}

	public void setButtonStyle(int buttonStyle) {
		this.buttonStyle = buttonStyle;
	}
	
}
