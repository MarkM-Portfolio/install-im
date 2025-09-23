/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.dbconfig.backend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 */
public abstract class Checker {
	public static final Logger logger = LogUtil.getLogger(Checker.class);
	public static final String FS = Constants.FS;
	protected String dbInstallLoc = null;
	protected String platform = null;
	protected String username = null;
	protected String password = null;
	protected String instance = null;
	protected String[] databases = new String[0];
	protected List<String> commandArgs = new ArrayList<String>();
	protected boolean isLocal = true;
	protected int exitValue = -1;
	protected String PDBName = null;
	protected String dbaPassword = null;
	

//#Oracle12C_PDB_disable#	
/*
	public Checker(String dbInstallLoc, String platform,String PDBName,String dbaPassword) {
		this.dbInstallLoc = dbInstallLoc;
		this.platform = platform;
		setPDBName(PDBName);
		setdbaPassword(dbaPassword);
	}
*/

	public Checker(String dbInstallLoc, String platform) {
		this.dbInstallLoc = dbInstallLoc;
		this.platform = platform;
	}

	public String getDbInstallLoc() {
		return this.dbInstallLoc;
	}

	public String getPlatform() {
		return this.platform;
	}
 
//#Oracle12C_PDB_disable#	
/* 
	public String getPDBName() {
		logger.info("Checker  gtPDBName(String PDBName) " + PDBName );
		return this.PDBName;
	 }
 */   
	public String getdbaPassword() {
		logger.info("Checker  setdbaPassword " + dbaPassword );
		return this.dbaPassword;
	}
	
	public boolean getIsLocal() {
		return this.isLocal;
	}

	protected abstract String getExecutable(String version);

	protected abstract Map<String, String> getDBMapping();

	public String[] getFeatures(String version) {
		try {
			getCommandArgs(version);
			logger.log(Level.FINER, "dbconfig.finer.command_arguments", commandArgs
					.toString());

			ProcessBuilder pb = new ProcessBuilder(commandArgs);
			pb.redirectErrorStream(true);
			addEnvironment(pb);
			try {
				Process p = pb.start();
				databases = parseListDbOutput(p);

				logger.log(Level.INFO, "dbconfig.info.detected_databases", Arrays
						.toString(databases));
				p.waitFor();
				exitValue = p.exitValue();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "dbconfig.severe.dbcheck_exception", e);
			}

			if (0 != exitValue) {
				logger.severe("The process doesn't exit normally");
			}

			// map to features
			List<String> dbs = new ArrayList<String>();
			Map<String, String> dbMapping = getDBMapping();
			for (int i = 0; i < databases.length; i++) {
				String feature = dbMapping.get(databases[i]);
				if (null != feature) {
					dbs.add(feature);
				}
			}
			//**************jia**************
			if (dbs.contains(Constants.FEATURE_DOGEAR)) {
				dbs.remove(Constants.FEATURE_DOGEAR);
				dbs.add("bookmarks");
			}
			Collections.sort(dbs);
			int tempIndex = -1;
			if ((tempIndex = dbs.indexOf("bookmarks")) != -1) {
				dbs.set(tempIndex, Constants.FEATURE_DOGEAR);
			}
			
			logger.log(Level.INFO, "dbconfig.info.detected_features", dbs);
			return dbs.toArray(new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void addEnvironment(ProcessBuilder pb) {
		return;
	}

	protected abstract String[] parseListDbOutput(Process p) throws IOException;

	protected abstract List<String> getCommandArgs(String version);
	public abstract boolean validateVersion(String dbVersion);

	public boolean validateInstallLoc(String version) {
		File f = new File(getExecutable(version));
		return f.exists();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public void setPDBName(String PDBName) {
		this.PDBName = PDBName;
	}
    
	public void setdbaPassword(String dbaPassword) {
		this.dbaPassword = dbaPassword;
	}
	
	public void setIsLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public String getInstance() {
		return this.instance;
	}

//#Oracle12C_PDB_disable#	
/*
	public static Checker getChecker(String dbInstallLoc, String platform,
			String dbType, String PDBName, String dbaPassword) {
		logger.entering(Checker.class.toString(), "getChecker", new String[] {
				dbInstallLoc, platform, dbType });
		Checker dc = null;
		if (Constants.DBMS_DB2.equals(dbType)) {
			dc = new DB2Checker(dbInstallLoc, platform);
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			dc = new OracleChecker(dbInstallLoc, platform,PDBName,dbaPassword);
		} else if (Constants.DBMS_SQLSERVER.equals(dbType)) {
			dc = new SQLServerChecker(dbInstallLoc, platform);
		} else {
			throw new IllegalArgumentException("Unrecoganized database type : "
					+ dbType);
		}
		logger.exiting(Checker.class.toString(), "getChecker");
		return dc;
	}
	
*/
	public static Checker getChecker(String dbInstallLoc, String platform,
			String dbType) {
		logger.entering(Checker.class.toString(), "getChecker", new String[] {
				dbInstallLoc, platform, dbType });
		Checker dc = null;
		if (Constants.DBMS_DB2.equals(dbType)) {
			dc = new DB2Checker(dbInstallLoc, platform);
		} else if (Constants.DBMS_ORACLE.equals(dbType)) {
			dc = new OracleChecker(dbInstallLoc, platform);
		} else if (Constants.DBMS_SQLSERVER.equals(dbType)) {
			dc = new SQLServerChecker(dbInstallLoc, platform);
		} else {
			throw new IllegalArgumentException("Unrecoganized database type : "
					+ dbType);
		}
		logger.exiting(Checker.class.toString(), "getChecker");
		return dc;
	}

	public abstract boolean validateInstance(String version);

	public boolean contains(String v, String[] arr) {
		int length = arr.length;
		for (int i = 0; i < length; i++) {
			if (v.equalsIgnoreCase(arr[i]))
				return true;
		}

		return false;
	}
	
	public abstract String getVersionInfo();
}
