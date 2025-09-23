/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                   */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common.validator;

import java.util.Map;

import com.ibm.lconn.wizard.common.depcheck.DepChecker;
import com.ibm.lconn.wizard.common.depcheck.ProductInfo;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.backend.Checker;

public class DbInstallLocValidator extends FilePathValidator {
	
	private String dbType;
	private String platform;
	private String PDBName;
	private String dbaPassword;
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
//#Oracle12C_PDB_disable#		Checker chk = Checker.getChecker(eval(filepath), eval(platform), eval(dbType),PDBName,dbaPassword);		
		Checker chk = Checker.getChecker(eval(filepath), eval(platform), eval(dbType));
		DepChecker dc = new DepChecker(DepChecker.PRODUCT_DB, CommonHelper.getPlatformType());
		Map<String, ProductInfo> detectedDBMap = dc.check();
		if(!chk.validateInstallLoc(detectedDBMap.get(dbType).getVersion())) {
			logError("db_install_loc_invalid", eval(filepath));
			return 1;
		}

		return 0;
	}

}
