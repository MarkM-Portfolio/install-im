/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2007, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.common.ui.data;

import com.ibm.lconn.wizard.common.Util;

/*
/*
 * @author Jun Jing Zhang
 *
 */
public class WizardDropDownData {
	private String[] labels, values;
	private String id, defaultValue;
	private int selectedIndex;
	
	public WizardDropDownData(String id){
		this.id = id;
		DefaultWizardDataLoader.initWizardDropDown(this);
	}
	/**
	 * @return the labels
	 */
	public String[] getLabels() {
		return labels;
	}
	/**
	 * @param labels the labels to set
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
	}
	/**
	 * @return the values
	 */
	public String[] getValues() {
		return values;
	}
	/**
	 * @param values the values to set
	 */
	public void setValues(String[] values) {
		this.values = values;
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @return the selectedValue
	 */
	public String getSelectedValue() {
		return this.values[selectedIndex];
	}
	/**
	 * @param selectedValue the selectedValue to set
	 */
	public void setSelectedValue(String defaultValue) {
		int index = indexOfValue(defaultValue);
		this.selectedIndex = index;
	}
	
	
	
	public int indexOfValue(String val){
		return Util.indexOf(this.values, val);
	}
	public int indexOfLabel(String label){
		return Util.indexOf(this.labels, label);
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
