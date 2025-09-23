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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class JDBCLibraryFolderValidator extends FilePathValidator {
	private static final Logger logger = LogUtil
			.getLogger(JDBCLibraryFolderValidator.class);

	private String dbType = null;

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
		if (Constants.DB_DB2.equals(_dbType)) {
			int i = processFile(Constants.JDBC_DB2_LIB, _filepath);
			if(i != 0) {
				return i;
			}
			i = processFile(Constants.JDBC_DB2_LICENSE, _filepath);	
			return i;
		} else if (Constants.DB_ORACLE.equals(_dbType)) {
			return processFile(Constants.JDBC_ORACLE_LIB, _filepath);
		} else if (Constants.DB_SQLSERVER.equals(_dbType)) {
			return processFile(Constants.JDBC_SQLSERVER_LIB, _filepath);
		}

		return -1;
	}

	private int processFile(String name, String folder) {
		File f = new File(folder, name);
		if (!f.exists()) {
			logError("jdbc_library_not_exists", f.getName(), folder);
			return 15;
		}

		String tdiPath = DataPool.getValue(System
				.getProperty(Constants.LCONN_WIZARD_PROP),
				Constants.INPUT_TDI_INSTALL_DIR);
		File target = new File(tdiPath + Constants.FS
				+ Constants.TDI_JRE_EXT_LIB + Constants.FS + name);
		if (target.exists() && !target.canWrite()) {
			// ignore
			logger.log(Level.WARNING, "validator.warning.skip_existing_file", target.getAbsolutePath());
			return 0;
		} else {
			// try to do copy
			String osType = CommonHelper.getPlatformType();
			ProcessBuilder pb = null;
			if (Constants.OS_WINDOWS.equals(osType)) {
				pb = new ProcessBuilder("xcopy", "/Y", f.getAbsolutePath(),
						target.getParent());
			} else {
				pb = new ProcessBuilder("cp", "-f", f.getAbsolutePath(), target
						.getParent());
			}
			pb.redirectErrorStream(true);
			
			Process p;
			boolean succeed = false;
			try {
				p = pb.start();
				
				LineNumberReader lnr = new LineNumberReader(new InputStreamReader(p.getInputStream()));
				String l = null;
				while((l = lnr.readLine()) != null) {
					logger.log(Level.FINEST, "command.finest.command_output", l);
				}
				
				p.waitFor();
				if (p.exitValue() == 0) {
					succeed = true;
				}
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, "validator.severe.copy_fail", e);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, "validator.severe.copy_fail", e);
			}
			if(!succeed) {
				logError("copy_fail", f.getName(), tdiPath + Constants.FS
						+ Constants.TDI_JRE_EXT_LIB);
			} else {
				return 0;
			}
		}
		return 15;
	}

}
