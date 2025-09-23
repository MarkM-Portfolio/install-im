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
package com.ibm.lconn.wizard.tdipopulate.backend;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 */
public class AttributeMapping implements Comparable<AttributeMapping>{
	private String dbField;
	private String attribute;
	private String validationRule;
	private boolean required;
	
	public AttributeMapping(String dbField, String attribute, String validationRule, boolean required) {
		this.dbField = dbField;
		this.attribute = attribute;
		this.validationRule = validationRule;
		this.required = required;
	}

	

	public AttributeMapping(String dbField, String attribute, String validationRule) {
		this(dbField, attribute, validationRule, false);
	}
	public String getAttribute() {
		return this.attribute;
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}
	public String getValidationRule() {
		return validationRule;
	}
	
	public void setValidationRule(String validationRule) {
		this.validationRule = validationRule;
	}









	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}


	public int compareTo(AttributeMapping o) {
		return this.dbField.compareTo(o.dbField);
	}
}
