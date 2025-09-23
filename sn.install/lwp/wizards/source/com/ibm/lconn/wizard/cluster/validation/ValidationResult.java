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

package com.ibm.lconn.wizard.cluster.validation;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class ValidationResult {
	private int fieldNumb;
	private String message;

	ValidationResult(int f, String m) {
		this.fieldNumb = f;
		this.message = m;
	}
	
	public int getInvalidField() {
		return this.fieldNumb;
	}
	
	public String getMessage() {
		return this.message;
	}
}
