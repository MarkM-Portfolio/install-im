/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2016                                   */
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class SQLServerChecker extends Checker {
	public static final Logger logger = LogUtil
			.getLogger(SQLServerChecker.class);
	private static final String[] EXECUTABLE_FOLDER_2016 = { "Client SDK","ODBC","130", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2012 = { "110", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2008 = { "100", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2005 = { "90", "Tools", "Binn" };
	private static final String SQLSERVER_EXECUTABLE = "SQLCMD.EXE";
	private static final String SQLSEVER_COMMAND = "select '$'+name+'$' from sys.databases";
	private static final Set<String> SYSTEM_DB = new HashSet<String>();
	private static Map<String, String> dbMapping = null;

	static {
		SYSTEM_DB.add("master");
		SYSTEM_DB.add("tempdb");
		SYSTEM_DB.add("model");
		SYSTEM_DB.add("msdb");
	};

	public SQLServerChecker(String dbInstallLoc, String platform) {
		super(dbInstallLoc, platform);
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
		// SQL 2017 and 2016 have SQLCMD.EXE in same place
		if (version.startsWith("13") || version.startsWith("14")){
			for (int i = 0; i < EXECUTABLE_FOLDER_2016.length; i++) {
				path.append(EXECUTABLE_FOLDER_2016[i]).append(FS);
			}			
		} else if (version.startsWith("10")) {
			for (int i = 0; i < EXECUTABLE_FOLDER_2008.length; i++) {
				path.append(EXECUTABLE_FOLDER_2008[i]).append(FS);
			}
		} else {
			for (int i = 0; i < EXECUTABLE_FOLDER_2012.length; i++) {
				path.append(EXECUTABLE_FOLDER_2012[i]).append(FS);
			}
		}
		path.append(SQLSERVER_EXECUTABLE);
		return path.toString();
	}

	@Override
	protected List<String> getCommandArgs(String version) {
		commandArgs = new ArrayList<String>();
		commandArgs.add(getExecutable(version));
		commandArgs.add("-d");
		commandArgs.add("master");
		if (null != instance && !"".equals(instance.trim())) {
			// always pass -S param no mater what version is used, if default instance, value is .\, if not default instance, value is .\instancename
			commandArgs.add("-S");
			if ("\\".equals(instance.trim())) { // default instance
				commandArgs.add(".\\");
			}
			else {
				commandArgs.add(".\\"+instance);
			}
		}

		commandArgs.add("-Q");
		commandArgs.add(SQLSEVER_COMMAND);
		return commandArgs;
	}

	@Override
	protected Map<String, String> getDBMapping() {
		if (dbMapping == null) {
			dbMapping = new HashMap<String, String>();

			dbMapping.put(Constants.DB_NAME_ACTIVITIES,
					Constants.FEATURE_ACTIVITIES);
			dbMapping.put(Constants.DB_NAME_BLOGS, Constants.FEATURE_BLOGS);
			dbMapping.put(Constants.DB_NAME_COMMUNITIES,
					Constants.FEATURE_COMMUNITIES);
			dbMapping.put(Constants.DB_NAME_DOGEAR, Constants.FEATURE_DOGEAR);
			dbMapping.put(Constants.DB_NAME_PROFILES,
					Constants.FEATURE_PROFILES);
			dbMapping.put(Constants.DB_NAME_HOMEPAGE,
					Constants.FEATURE_HOMEPAGE);
			dbMapping.put(Constants.DB_NAME_WIKIS,
					Constants.FEATURE_WIKIS);
			dbMapping.put(Constants.DB_NAME_FILES,
					Constants.FEATURE_FILES);
			dbMapping.put(Constants.DB_NAME_FORUM, Constants.FEATURE_FORUM);
			dbMapping.put(Constants.DB_NAME_MOBILE, Constants.FEATURE_MOBILE);
			dbMapping.put(Constants.DB_NAME_METRICS, Constants.FEATURE_METRICS);
			dbMapping.put(Constants.DB_NAME_COGNOS, Constants.FEATURE_COGNOS);
			dbMapping.put(Constants.DB_NAME_PNS, Constants.FEATURE_PNS);

			//TODO Modify by Maxi, add database library_gcd and library_os in dbMapping
			dbMapping.put(Constants.DB_NAME_LIBRARY_GCD, Constants.FEATURE_LIBRARY_GCD);
			dbMapping.put(Constants.DB_NAME_LIBRARY_OS, Constants.FEATURE_LIBRARY_OS);
			//TODO end
			
			dbMapping = Collections.unmodifiableMap(dbMapping);
		}
		return dbMapping;
	}

	@Override
	public boolean validateVersion(String dbVersion){
		if(null == dbVersion || "".equals(dbVersion)){
			logger.severe("dbconfig.info.silent.validation.dbVersion.empty");
			return false;
		}else if(dbVersion.startsWith("11") || dbVersion.startsWith("10")){
			return true;
		}else{
			logger.severe("dbconfig.info.silent.validation.dbVersion.fail");
			logger.severe("dbconfig.info.silent.validation.dbVersion.sqlserver");
			return false;
		}
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
