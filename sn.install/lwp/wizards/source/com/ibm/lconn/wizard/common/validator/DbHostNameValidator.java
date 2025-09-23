/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.common.validator;

import com.ibm.lconn.wizard.common.Constants;

public class DbHostNameValidator extends AbstractValidator{

	public int validate() {
		if(enabled != null && !eval(enabled).equals(Constants.BOOL_TRUE)) {
			return 0;
		}
		String val = eval(str);
		if(val == null || "".equals(val.trim())) {
			logError("empty_string", getLable(str));
			return 1;
		}
		if(val != null && val.contains("_")) {
			logError("underscore_string", getLable(str));
			return 1;
		}
		return 0;
	}
	
	
	
	private String str;
	private String enabled = null;
	
	public DbHostNameValidator(String str) {
		this.str = str;
	}
	
	public DbHostNameValidator(String str, String enabled) {
		this.str = str;
		this.enabled = enabled;
	}

}
