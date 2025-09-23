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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.DbTypeSelectionWizardPage;
import com.ibm.lconn.wizard.dbconfig.ui.wizardPage.OperationOptionWizardPage;
import com.ibm.lconn.wizard.dbconfig.ui.DBWizardInputs;
import com.ibm.lconn.wizard.common.ui.CommonHelper; 

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class OracleChecker extends Checker {
	public static final Logger logger = LogUtil.getLogger(OracleChecker.class);
	private static final String[] EXECUTABLE_FOLDER = { "bin" };
	private static final String ORACLE_HOME = "ORACLE_HOME";
	private static final String ORACLE_SID = "ORACLE_SID";
	private static Map<String,String> dbMapping = null;

	private static final String ORACLE_CHECKER_SCRIPT = "querySchema_ora.sql";
	
	private static final Set<String> SYSTEM_DB = new HashSet<String>(Arrays
			.asList(new String[] { "SYS", "SYSTEM", "OUTLN", "MDSYS", "ORDSYS",
					"CTXSYS", "EXFSYS", "DMSYS", "DBSNMP", "WMSYS", "SYSMAN",
					"XDB", "OLAPSYS", "TSMSYS", "MGMT_VIEW", "ORDPLUGINS",
					"ANONYMOUS", "MDDATA", "SI_INFORMTN_SCHEMA", "PERFSTAT", "DIP" }));

//#Oracle12C_PDB_disable#	public OracleChecker(String dbInstallLoc, String platform, String pDBName, String dbaPassword) {					
	public OracleChecker(String dbInstallLoc, String platform) {
//#Oracle12C_PDB_disable#		super(dbInstallLoc, platform,pDBName,dbaPassword);
		super(dbInstallLoc, platform);
	}

	@Override
	protected void addEnvironment(ProcessBuilder pb) {
		pb.environment().put(ORACLE_HOME, dbInstallLoc);
		if(null != instance) {
			logger.log(Level.FINER, "dbconfig.finer.add_oracel_sid_env", instance);
			pb.environment().put(ORACLE_SID, instance);
		}
	}
	
	@Override
	protected String[] parseListDbOutput(Process p) throws IOException {
		
		List<String> databases = new ArrayList<String>();
		InputStream is = p.getInputStream();
		
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));		
		String content = null;
		while ((content = lnr.readLine()) != null) {
			logger.info(content);
			content = content.trim();

			if (content.startsWith("$") && content.endsWith("$")) {
				String db = content.substring(1, content.length() - 1);
				if (!SYSTEM_DB.contains(db)) {
					databases.add(db);
				}
			}

		}
		lnr.close();
		
		return databases.toArray(new String[] {});
	}

	@Override
	protected String getExecutable(String version) {
		StringBuffer path = new StringBuffer().append(dbInstallLoc).append(FS);
		for (int i = 0; i < EXECUTABLE_FOLDER.length; i++) {
			path.append(EXECUTABLE_FOLDER[i]).append(FS);
		}

		if (Constants.OS_WINDOWS.equals(platform)) {
			path.append(Constants.ORACLE_EXECUTABLE_WIN);
		} else if (Constants.OS_AIX.equals(platform)) {
			path.append(Constants.ORACLE_EXECUTABLE);
		} else if (Constants.OS_LINUX_SUSE.equals(platform)) {
			path.append(Constants.ORACLE_EXECUTABLE);
		} else if (Constants.OS_LINUX_REDHAT.equals(platform)) {
			path.append(Constants.ORACLE_EXECUTABLE);
		}

		return path.toString();
	}

	@Override
	protected List<String> getCommandArgs(String version) {	

//#Oracle12C_PDB_disable#		String PDBName=super.getPDBName();
//#Oracle12C_PDB_disable#		String dbaPassword=super.getdbaPassword();
		logger.info("getCommandArgs(String version) " + version);
		commandArgs = new ArrayList<String>();
		commandArgs.add(getExecutable(version));
		commandArgs.add("-L");
		if (null == instance || isLocal) {
			commandArgs.add("/ as sysdba");
		} else {
			if (null == username) {				
					commandArgs.add("/@" + instance + " as sysdba");
			} else {
				if("sys".equalsIgnoreCase(username)) {
					commandArgs.add(username + "/" + password + "@" + instance
							+ " as sysdba");
				} else {
					commandArgs.add(username + "/" + password + "@" + instance);
				}
			}
		}

//#Oracle12C_PDB_disable#		if (CommonHelper.isEmpty(PDBName)){
//#Oracle12C_PDB_disable#			commandArgs.add("SYS" + "/" + dbaPassword + "@" + instance + " as sysdba");
//#Oracle12C_PDB_disable#		}else{
//#Oracle12C_PDB_disable#			commandArgs.add("SYS" + "/" + dbaPassword + "@" + PDBName + " as sysdba");
//#Oracle12C_PDB_disable#		} 
		

		// get absolut path
		String path = Constants.SCRIPT_DIR + Constants.FS + ORACLE_CHECKER_SCRIPT;
		commandArgs.add("@" + path);
		return commandArgs;
	}

	@Override
	protected Map<String,String> getDBMapping() {
		if(dbMapping == null) {
			dbMapping = new HashMap<String, String>();

			dbMapping.put(Constants.SCHEMA_NAME_ACTIVITIES, Constants.FEATURE_ACTIVITIES);
			dbMapping.put(Constants.SCHEMA_NAME_BLOGS, Constants.FEATURE_BLOGS);
			dbMapping.put(Constants.SCHEMA_NAME_COMMUNITIES, Constants.FEATURE_COMMUNITIES);
			dbMapping.put(Constants.SCHEMA_NAME_DOGEAR, Constants.FEATURE_DOGEAR);
			dbMapping.put(Constants.SCHEMA_NAME_PROFILES, Constants.FEATURE_PROFILES);
			dbMapping.put(Constants.SCHEMA_NAME_HOMEPAGE, Constants.FEATURE_HOMEPAGE);
			dbMapping.put(Constants.SCHEMA_NAME_WIKIS, Constants.FEATURE_WIKIS);
			dbMapping.put(Constants.SCHEMA_NAME_FILES, Constants.FEATURE_FILES);
			//TODO the following code may need modify! not belong to Maxi
			dbMapping.put(Constants.DB_NAME_FORUM, Constants.FEATURE_FORUM);
			dbMapping.put(Constants.DB_NAME_MOBILE, Constants.FEATURE_MOBILE);
			dbMapping.put(Constants.DB_NAME_METRICS, Constants.FEATURE_METRICS);
			dbMapping.put(Constants.DB_NAME_COGNOS, Constants.FEATURE_COGNOS);
			dbMapping.put(Constants.DB_NAME_PNS, Constants.FEATURE_PNS);
			//TODO Modified by Jia: to fix the bug that cannot check whether library  is installed.
			dbMapping.put(Constants.DB_ORCL_USERNAME_LIBRARY_GCD, Constants.FEATURE_LIBRARY_GCD);
			dbMapping.put(Constants.DB_ORCL_USERNAME_LIBRARY_OS, Constants.FEATURE_LIBRARY_OS);
			//TODO end
			
			
			dbMapping = Collections.unmodifiableMap(dbMapping);
		}
		return dbMapping;
	}
	
	@Override
	public boolean validateVersion(String dbVersion){
		return true;
	}
	@Override
	public boolean validateInstance(String version) {
		try {
			getFeatures(version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0 == exitValue;
	}

	public String getVersionInfo() {
		return null;
	}
}
