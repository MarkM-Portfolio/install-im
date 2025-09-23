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
package com.ibm.lconn.wizard.common.validator;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class NumberValidator extends AbstractValidator {
	private String num;
	private boolean allowEmpty;
	
	public NumberValidator(String num, boolean allowEmpty) {
		this.num = num;
		this.allowEmpty = allowEmpty;
	}
	
	public NumberValidator(String num) {
		this(num, false);
	}
	
	public int validate() {
		String val = eval(num);
		if(allowEmpty && ( val == null || "".equals(val.trim()))) {
			return 0;
		}
		
		try {
			Integer.parseInt(val);
			return 0;
		} catch(NumberFormatException e) {
			logError("not_a_number", val, getLable(num));
			return 1;
		}
	}
}
