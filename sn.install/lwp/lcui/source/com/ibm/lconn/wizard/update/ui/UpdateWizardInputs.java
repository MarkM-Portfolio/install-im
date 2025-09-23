/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.update.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.wizard.IWizard;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.update.data.EFixComponent;
import com.ibm.websphere.product.history.xml.updateEvent;
import com.ibm.websphere.update.ptf.EFixImage;

public class UpdateWizardInputs {
	
	public static boolean fake = false;
	
	
	public static String getActionId(IWizard wizard) {
		String actionId = CommonHelper.getVariable(wizard, Constants.WIZARD_PAGE_ACTION_TYPE, Constants.INPUT_FIX_ACTION_NAME);
		return actionId;
	}

	public static void setActionId(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard, Constants.WIZARD_PAGE_ACTION_TYPE,
				Constants.INPUT_FIX_ACTION_NAME, value);
	}
	
	public static String getFixLocation(IWizard wizard) {
		String actionId = CommonHelper.getVariable(wizard, Constants.WIZARD_PAGE_ACTION_TYPE, Constants.INPUT_FIX_LOCATION);
		return actionId;
	}

	public static void setFixLocation(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard, Constants.WIZARD_PAGE_ACTION_TYPE,
				Constants.INPUT_FIX_LOCATION, value);
	}
	
	 public static void setFixesSelectAll(IWizard wizard, boolean value){
			CommonHelper.setObject(Constants.WIZARD_PAGE_FIX_INFO,
					Constants.INPUT_FIX_SELECT_ALL, Boolean.valueOf(value));
	}
	 
    public static boolean getFixesSelectAll(IWizard wizard){
    	return ((Boolean)CommonHelper.getObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.INPUT_FIX_SELECT_ALL)).booleanValue();
	}
	
    @SuppressWarnings("unchecked")
	public static List<String> getFixIDs(IWizard wizard) {
		return (List<String>) CommonHelper.getObject(
				Constants.WIZARD_PAGE_FIX_INFO,
				Constants.INPUT_FIX_SELECTED);
	}

	public static void setFixIDs(IWizard wizard, List<String> list) {
		String[] sort = list.toArray(new String[0]);
//		Arrays.sort(sort);
		list = new ArrayList<String>();
		for (String value : sort)
			list.add(value);
		CommonHelper.setObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.INPUT_FIX_SELECTED, list);
	}
    
	public static void setEFixImages(IWizard wizard, Vector<EFixImage> eFixImages){
		CommonHelper.setObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_SELECTTED_EFIXIMAGES , eFixImages);
	}
	
    @SuppressWarnings("unchecked")
	public static Vector<EFixImage> getEFixImages(IWizard wizard){
    	return (Vector<EFixImage>)CommonHelper.getObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_SELECTTED_EFIXIMAGES );
	}
    
    @SuppressWarnings("unchecked")
	public static Vector<String> getFixIDsForUnstall(IWizard wizard) {
		return (Vector<String>) CommonHelper.getObject(
				Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_SELECTED_FIX_IDS);
	}

	public static void setFixIDsForUnstall(IWizard wizard, Vector<String> fixIDs) {
		CommonHelper.setObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_SELECTED_FIX_IDS, fixIDs);
	}
	
	public static void setAllFixes(IWizard wizard, List<EFixComponent> eFixComponens){
		CommonHelper.setObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_ALL_FIXES , eFixComponens);
	}
	
    @SuppressWarnings("unchecked")
	public static List<EFixComponent> getAllFixes(IWizard wizard){
    	return (List<EFixComponent>)CommonHelper.getObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_ALL_FIXES );
	}
    
    public static void setSelectedFixes(IWizard wizard, List<EFixComponent> eFixComponens){
		CommonHelper.setObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_SELECTTED_FIXES , eFixComponens);
	}
	
    @SuppressWarnings("unchecked")
	public static List<EFixComponent> getSelectedFixes(IWizard wizard){
    	return (List<EFixComponent>)CommonHelper.getObject(Constants.WIZARD_PAGE_FIX_INFO,
				Constants.VARIABLE_SELECTTED_FIXES );
	}
	
	public static void setUpdateEvens(IWizard wizard, Vector<updateEvent> updateEvens){
		CommonHelper.setObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_SELECTTED_FIXES , updateEvens);
	}
	
    @SuppressWarnings("unchecked")
	public static Vector<updateEvent> getUpdateEvens(IWizard wizard){
    	return (Vector<updateEvent>)CommonHelper.getObject(Constants.WIZARD_PAGE_EXECUTION,
				Constants.VARIABLE_SELECTTED_FIXES );
	}
    
	public static String getWASUserid(IWizard wizard) {
		String userid = CommonHelper.getVariable(wizard, Constants.WIZARD_PAGE_WAS_AUTH_INFO, Constants.INPUT_WAS_USERID);
		return userid;
	}
	public static void setWASUserid(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard, Constants.WIZARD_PAGE_WAS_AUTH_INFO,
				Constants.INPUT_WAS_USERID, value);
	}
	public static String getWASPassword(IWizard wizard) {
		String password = CommonHelper.getVariable(wizard, Constants.WIZARD_PAGE_WAS_AUTH_INFO, Constants.INPUT_WAS_PASSWORD);
		return password;
	}
	public static void setWASPassword(IWizard wizard, String value) {
		CommonHelper.setVariable(wizard, Constants.WIZARD_PAGE_WAS_AUTH_INFO,
				Constants.INPUT_WAS_PASSWORD, value);
	}
    
}
