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

import java.io.File;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class TDIInstallLocValidator extends FilePathValidator {
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
		
		String val = eval(filepath);
		String pval = CommonHelper.getPlatformType();
		
		String tdiExe = val + Constants.FS + "bin" + Constants.FS;
		String javaExe = val + Constants.FS + "jvm" + Constants.FS + "jre" + Constants.FS + "bin" + Constants.FS;
		if(Constants.OS_WINDOWS.equals(pval)) {
			tdiExe = tdiExe + Constants.TDI_EXECUTABLE_WIN;
			javaExe = javaExe + Constants.JAVA_EXECUTABLE_WIN;
		} else {
			tdiExe = tdiExe + Constants.TDI_EXECUTABLE;
			javaExe = javaExe + Constants.JAVA_EXECUTABLE;
		}
		
		File f1 = new File(tdiExe);
		File f2 = new File(javaExe);

		if(!f1.exists() || !f2.exists() || !f1.isFile() || !f2.isFile()) {
			logError("tdi_loc_invalid", val);
			return 4;
		}
		return 0;
	}

}
