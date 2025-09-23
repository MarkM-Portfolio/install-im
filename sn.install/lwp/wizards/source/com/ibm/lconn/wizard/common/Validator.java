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
package com.ibm.lconn.wizard.common;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public interface Validator {
	/**
	 * Validates the user input
	 * @return 0 if validation passed, else positive values
	 */
	public int validate();
	
	/**
	 * Gets validation error message
	 * @return
	 */
	public String getTitle();
	
	/**
	 * Gets validation error description
	 * @return
	 */
	public String getMessage();
	
	/**
	 * Returns the level, ERROR or WARNING
	 * @return
	 */
	public int getLevel();
	
	/**
	 * Set the validation parameters
	 */
	public void setParameters(String... parameters);
}
