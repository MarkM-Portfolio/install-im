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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class DB2Checker extends Checker {
	public static final Logger logger = LogUtil.getLogger(DB2Checker.class);
	private static final String[] EXECUTABLE_FOLDER = { "bin" };
	private static final String DB2_WIN = "db2cmd.exe";
	private static final String DB2_LINUX = "db2";
	private static final String DB2_AIX = "db2";
	private static final String DB2_COMMAND = "LIST DATABASE DIRECTORY";
	private static final String DB2INSTANCEID = "DB2INSTANCE";
	private static Map<String, String> dbMapping = null;

	public DB2Checker(String dbInstallLoc, String platform) {
		super(dbInstallLoc, platform);
	}

	@Override
	protected String getExecutable(String version) {
		StringBuffer path = new StringBuffer().append(dbInstallLoc).append(FS);
		for (int i = 0; i < EXECUTABLE_FOLDER.length; i++) {
			path.append(EXECUTABLE_FOLDER[i]).append(FS);
		}

		if (Constants.OS_WINDOWS.equals(platform)) {
			path.append(DB2_WIN);
		} else if (Constants.OS_AIX.equals(platform)) {
			path.append(DB2_AIX);
		} else if (Constants.OS_LINUX_SUSE.equals(platform)) {
			path.append(DB2_LINUX);
		} else if (Constants.OS_LINUX_REDHAT.equals(platform)) {
			path.append(DB2_LINUX);
		}
		
		return path.toString();
	}

	@Override
	protected String[] parseListDbOutput(Process p) throws IOException {
		List<String> databases = new ArrayList<String>();
		int pos = 0;
		int entryNumber = -1;
		InputStream is = p.getInputStream();

		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		String content = null;

		while ((content = lnr.readLine()) != null) {
			if ("".equals(content.trim())) {
				continue;
			}
			logger.info(content);
			if (entryNumber == -1) {
				// get entry count
				int i = content.indexOf('=');
				if (i > 0) {
					entryNumber = Integer.valueOf(content.substring(i + 1).trim());
				}
			} else {
				int i = content.indexOf('=');

				if (i > 0) {
					String value = content.substring(i + 1).trim();
					if (pos == 0) {
						// cataloged database alias name
						databases.add(value);
					}
					if (pos == 1) {
						// TODO process dataabase name
					}
					if (pos == 2) {
						// check whether the node is local or remote
						// if node name doesn't match remove the added one
						boolean isLocalNode = new File(value).exists();
						if (isLocal && !isLocalNode) {
							databases.remove(databases.size() - 1);
						} else if (!isLocal) {
							if (!value.equalsIgnoreCase(instance)) {
								databases.remove(databases.size() - 1);
							}
						}
					}
					pos++;
				} else {
					pos = 0;
				}
			}
		}

		lnr.close();
		return databases.toArray(new String[] {});
	}

	@Override
	protected List<String> getCommandArgs(String version) {
		String[] args = null;
		if (Constants.OS_WINDOWS.equals(platform)) {
			args = new String[] { getExecutable(version), "-i", "-w", "-c", "DB2", DB2_COMMAND };
		} else {
			args = new String[] { getExecutable(version), DB2_COMMAND };
		}

		commandArgs = Arrays.asList(args);
		return commandArgs;
	}

	@Override
	protected void addEnvironment(ProcessBuilder pb) {
		if (null != instance && !"".equals(instance.trim())) {
			logger.log(Level.INFO, "dbconfig.info.add_db2_env", instance);
			pb.environment().put(DB2INSTANCEID, instance);
		}
	}

	@Override
	protected Map<String, String> getDBMapping() {
		if (dbMapping == null) {
			dbMapping = new HashMap<String, String>();

			dbMapping.put(Constants.DB_NAME_ACTIVITIES, Constants.FEATURE_ACTIVITIES);
			dbMapping.put(Constants.DB_NAME_BLOGS, Constants.FEATURE_BLOGS);
			dbMapping.put(Constants.DB_NAME_COMMUNITIES, Constants.FEATURE_COMMUNITIES);
			dbMapping.put(Constants.DB_NAME_DOGEAR, Constants.FEATURE_DOGEAR);
			dbMapping.put(Constants.DB_NAME_PROFILES, Constants.FEATURE_PROFILES);
			dbMapping.put(Constants.DB_NAME_HOMEPAGE, Constants.FEATURE_HOMEPAGE);
			dbMapping.put(Constants.DB_NAME_WIKIS, Constants.FEATURE_WIKIS);
			dbMapping.put(Constants.DB_NAME_FILES, Constants.FEATURE_FILES);
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
		return true;
	}
	@Override
	public boolean validateInstance(String version) {
		try {

			String[] args = null;
			String attachCmd = "ATTACH";
			if (getInstance() != null && !"".equals(getInstance().trim())) {
				attachCmd += " TO " + getInstance();
			} else {
				attachCmd += " TO " + System.getenv("DB2INSTANCE");
			}
			int exitCode = -1;

			if (Constants.OS_WINDOWS.equals(platform)) {
				args = new String[] { getExecutable(version), "-i", "-w", "-c", "DB2", attachCmd };
			} else {
				args = new String[] { getExecutable(version), attachCmd };
			}
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			try {
				Process p = pb.start();
				LineNumberReader lnr = new LineNumberReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = lnr.readLine()) != null) {
					logger.info(line);
				}
				p.waitFor();
				exitCode = p.exitValue();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			if (exitCode == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getVersionInfo() {
		StringBuffer path = new StringBuffer().append(dbInstallLoc).append(FS);
		for (int i = 0; i < EXECUTABLE_FOLDER.length; i++) {
			path.append(EXECUTABLE_FOLDER[i]).append(FS);
		}

		path.append("db2level");
		String command = path.toString();

		logger.log(Level.FINER, "dbconfig.finer.command_arguments", command);
		String version = null;

		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);

			try {
				Process p = pb.start();
				version = parseVersionOutput(p);

				logger.log(Level.INFO, "dbconfig.info.detected_databases", version);
				p.waitFor();
				exitValue = p.exitValue();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "dbconfig.severe.dbcheck_exception", e);
			}

			if (0 != exitValue) {
				logger.severe("The process doesn't exit normally");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return version;
		}

		return version;
	}

	private String parseVersionOutput(Process p) throws IOException {
		InputStream is = p.getInputStream();
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		String content = null;
		String version = null;
		while ((content = lnr.readLine()) != null) {
			if ("".equals(content.trim())) {
				continue;
			}
			logger.info(content);
			String[] values = getQuoteValues(content);
			if (values == null) continue;
			for (String value : values) {
				if (Pattern.compile("db2 v[0-9]+\\.[0-9]+(\\.[0-9]+)*", Pattern.CASE_INSENSITIVE).matcher(value).matches()){
					version = value;
				}
			}
		}
		lnr.close();
		return version;
	}

	private String[] getQuoteValues(String content) {
		return content.split("\"");
	}
}
