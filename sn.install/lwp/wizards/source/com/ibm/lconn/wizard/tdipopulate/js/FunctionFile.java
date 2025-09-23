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
package com.ibm.lconn.wizard.tdipopulate.js;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

public class FunctionFile {
	private static final Logger logger = LogUtil.getLogger(FunctionFile.class);
	private static final String FILE_PATH_SEPERATOR = System
			.getProperty("file.separator");
	private static final String jsPath = Constants.TDI_WORK_DIR
			+ FILE_PATH_SEPERATOR + "profiles_functions.js";

	public static byte[] read() {
		InputStream is;
		byte[] arr = null;
		try {
			is = new FileInputStream(jsPath);
			int available = is.available();
			arr = new byte[available];
			is.read(arr);
			is.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "commom.property.file.not.found", e);
		}
		return arr;
	}

	public static void write(byte[] bytes) {
		try {
			OutputStream o = new FileOutputStream(jsPath);
			o.write(bytes);
			o.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "commom.property.file.not.write", e);
		}
	}

}
