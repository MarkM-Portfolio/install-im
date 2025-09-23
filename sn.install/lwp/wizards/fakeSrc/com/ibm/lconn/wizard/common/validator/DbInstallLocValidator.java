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


public class DbInstallLocValidator extends FilePathValidator {
	private static int count = 0;
	private String dbType;
	private String platform;
	public  DbInstallLocValidator(String filepath, String dbType, String platform) {
		super(filepath, MODE_DIR);
		this.dbType = dbType;
		this.platform = platform;
	}
	
	public int validate() {
		
		int retVal = super.validate();
		if(retVal != 0) {
			return retVal;
		}
		count++;
		
		if(count ==1) {
			logError("db_install_loc_invalid", eval(filepath));
			return 1;
		}

		return 0;
	}

}
