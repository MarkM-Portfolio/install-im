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
package com.ibm.lconn.wizard.tdipopulate.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class TDIEnv {
	public static final Logger logger = LogUtil.getLogger(TDIEnv.class);
	private static final String tdienv_win = Constants.TDI_WORK_DIR
			+ Constants.FS + "tdienv.bat";
	private static final String tdienv_lin = Constants.TDI_WORK_DIR
			+ Constants.FS + "tdienv.sh";

	public static void writeTDIEnv(Map<String, String> env) {
		File file = new File(getFile());
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			for (String key : env.keySet()) {
				if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
					output.write("set " + key + "=" + env.get(key));
				} else {
					output.write("export " + key + "=" + env.get(key));
				}

				output.newLine();
			}
			output.close();

		} catch (IOException e) {
			logger.log(Level.SEVERE, "tdipopulate.severe.backend.tdi_cannot_write", e);
		}

	}

	private static String getFile() {
		String str = null;
		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			str = tdienv_win;
		} else {
			str = tdienv_lin;
		}
		return str;
	}
}
