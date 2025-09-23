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
	private static int count = 0;
	
	public NumberValidator(String num, boolean allowEmpty) {
		this.num = num;
		this.allowEmpty = allowEmpty;
	}
	
	public NumberValidator(String num) {
		this(num, false);
	}
	
	public int validate() {
		count++;
		String val = eval(num);
		if(count ==1){
			logError("not_a_number", val, getLable(num));
			return 1;
		}
		
		return 0;
	}
}
