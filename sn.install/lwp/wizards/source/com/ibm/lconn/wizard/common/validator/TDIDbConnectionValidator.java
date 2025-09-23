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

public class TDIDbConnectionValidator extends DbConnectionValidator {
	private String tdiInstallLoc;
	
	public TDIDbConnectionValidator(String platform, String tdiInstallLoc, String dbType, String dbHostname, String dbPort, String dbName, String dbUser, String dbPassword) {
		super(platform, null, dbType, dbHostname, dbPort, dbName, dbUser,
				dbPassword);
		this.tdiInstallLoc = tdiInstallLoc;
	}
	
	protected String getJrePath() {
		return eval(tdiInstallLoc) + Constants.FS + "jvm" + Constants.FS + "jre" + Constants.FS + "bin";
	}

}
