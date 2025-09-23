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
package com.ibm.lconn.wizard.common.ui.data;

import java.util.ArrayList;
import java.util.List;

import com.ibm.lconn.wizard.common.Validator;
import com.ibm.lconn.wizard.common.interfaces.PageController;

public class WizardData extends AbstractData {
	String id;
	String[] pages;
	PageController pageController;
	
	List<Validator> validatorList;
	public WizardData(String id){
		this.id = id;
		DefaultWizardDataLoader.initWizard(this);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String[] getPages() {
		return pages;
	}
	public void setPages(String[] pages) {
		this.pages = pages;
	}
	
	public void addValidator(String page, Validator validate){
		getValidatorList().add(validate);
	}
	
	public boolean removeValidator(Validator validate){
		return getValidatorList().remove(validate);
	}
	
	public void clearValidator(){
		validatorList = null; 
	}
	
	/**
	 * @return the validatorList
	 */
	public List<Validator> getValidatorList() {
		if(validatorList==null) validatorList = new ArrayList<Validator>();
		return validatorList;
	}
	/**
	 * @return the pageController
	 */
	public PageController getPageController() {
		return pageController;
	}
	/**
	 * @param pageController the pageController to set
	 */
	public void setPageController(PageController pageController) {
		this.pageController = pageController;
	}
	
	
}
