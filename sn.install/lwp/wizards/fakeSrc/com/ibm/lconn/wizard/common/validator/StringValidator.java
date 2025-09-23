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

import com.ibm.lconn.wizard.common.Constants;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class StringValidator extends AbstractValidator {
	private static int count =0;
	private String str;
	private String enabled = null;
	
	public StringValidator(String str) {
		this.str = str;
	}
	
	public StringValidator(String str, String enabled) {
		this.str = str;
		this.enabled = enabled;
	}
	
	public int validate() {
		count++;
		
		String val = eval(str);
		if(count ==1) {
			logError("empty_string", getLable(str));
			return 1;
		}
		return 0;
	}

}
