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
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class JDBCLibraryFolderValidator extends FilePathValidator {
	private static final Logger logger = LogUtil
			.getLogger(JDBCLibraryFolderValidator.class);

	private String dbType = null;
	private static final int msgCount = 2;
	private static int count = 0;
	public JDBCLibraryFolderValidator(String filepath, String dbType) {
		super(filepath, MODE_DIR);
		this.dbType = dbType;
	}

	public int validate() {
		int rtv = super.validate();
		if (0 != rtv) {
			return rtv;
		}
		String _filepath = eval(filepath);
		String _dbType = eval(dbType);
		String tdiPath = DataPool.getValue(System
				.getProperty(Constants.LCONN_WIZARD_PROP),
				Constants.INPUT_TDI_INSTALL_DIR);
		File target = new File(tdiPath + Constants.FS
				+ Constants.TDI_JRE_EXT_LIB + Constants.FS + Constants.JDBC_ORACLE_LIB);
		count++;
		if (count == 1) {
			logError("jdbc_library_not_exists", Constants.JDBC_ORACLE_LIB, _filepath);
			return 15;
		}
		if(count ==2) {
			logError("copy_fail", Constants.JDBC_ORACLE_LIB, tdiPath + Constants.FS
					+ Constants.TDI_JRE_EXT_LIB);
			return 16;
		}

		return 0;
	}

}
