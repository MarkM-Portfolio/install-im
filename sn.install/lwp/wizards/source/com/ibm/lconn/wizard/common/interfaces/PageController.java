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
package com.ibm.lconn.wizard.common.interfaces;

import java.util.List;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.Validator;
import com.ibm.lconn.wizard.common.validator.ValidationMessage;

/**
 * This class is to control the logic between wizard pages
 * @author Jun Jing Zhang
 *
 */
public interface PageController {
	/**
	 * Let the wizard know that based on the basePage, after 
	 * action actionName, show which wizard page. 
	 * @param basePage
	 * @param actionName the action name, please refer to {@link Constants} to see
	 * @return target page id, null if validation is fail
	 */
	public String performAction(String basePage, String actionName);
	
	/**
	 * Registers validator to base page
	 * @param basePage
	 * @param validator
	 */
	public void  registerValidator(String basePage, Validator validator);
	
	/**
	 * Get the messages generated during performAction
	 * @return
	 */
	public List<ValidationMessage> getMessages();
}
