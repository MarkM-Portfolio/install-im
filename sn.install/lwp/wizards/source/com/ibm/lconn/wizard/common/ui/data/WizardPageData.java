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
import com.ibm.lconn.wizard.common.ui.CommonHelper;


public class WizardPageData extends AbstractData{
	private String id, name, className, wizardId, title, desc;
	private String[] inputsId;
	private List<Validator> validatorList;
	
	public WizardPageData(String wizardId, String id){
		this.wizardId = wizardId;
		this.id = id;
		DefaultWizardDataLoader.initWizardPage(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getInputsId() {
		return inputsId;
	}

	public void setInputsId(String[] inputsId) {
		if(inputsId.length==1 && CommonHelper.isEmpty(inputsId[0])){
			inputsId = null;
			return;
		}
		this.inputsId = inputsId;
		for (String input : inputsId) {
			DefaultWizardDataLoader.addInput(wizardId, input);
		}
	}

	public String getWizardId() {
		return wizardId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void addValidator(Validator valid) {
		if(this.validatorList==null) validatorList = new ArrayList<Validator>(); 
		validatorList.add(valid);
	}

	/**
	 * @return the validatorList
	 */
	public List<Validator> getValidatorList() {
		return validatorList;
	}
	
}
