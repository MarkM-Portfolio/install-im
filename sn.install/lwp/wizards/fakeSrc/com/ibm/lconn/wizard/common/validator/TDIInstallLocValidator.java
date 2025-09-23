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
public class TDIInstallLocValidator extends FilePathValidator {
	private static final int msgCount =1;
	private static int count = 0;
	// private String platform;
	public TDIInstallLocValidator(String filepath, String platform) {
		super(filepath, MODE_DIR);
		// this.platform = platform;
	}
	
	public int validate() {
		int result = super.validate();
		if(result != 0) {
			return result;
		}
		count++;
		String val = eval(filepath);
		if(count == 1) {
			logError("tdi_loc_invalid", val);
			return 4;
		}

		return 0;
	}

}
