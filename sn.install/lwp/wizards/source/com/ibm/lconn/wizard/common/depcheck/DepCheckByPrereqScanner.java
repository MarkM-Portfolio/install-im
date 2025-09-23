/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2016                                   */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.common.depcheck;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class DepCheckByPrereqScanner {
	// private boolean isFound = false;
	public final static Logger logger = LogUtil.getLogger(DepCheckByPrereqScanner.class);
	public final static String CONFIG_FILENAME = "KUD";
	public final static String STR_NOT_FOUND = "[Not Found]";
	public final static String STR_PASS = "PASS";
	public final static String STR_FAIL = "FAIL";

	// CommonInformation for IBM Scanner
	public final static String COMMON_FOLDER = "Prereq_Check_Scanner";
	public final static String FOLDER_WIN = "precheck_windows";
	public final static String USER_DIR = System.getProperty("user.dir");

	// For windows platform
	public final static String PATH_WIN = USER_DIR + Constants.FS + COMMON_FOLDER + Constants.FS + FOLDER_WIN + Constants.FS;
	public final static String BAT_WIN = "prereq_checker.bat";
	// public final static String CMDLINE_WIN = "cmd /c " +PATH_WIN + BAT_WIN;
	public final static String CMDLINE_WIN = PATH_WIN + BAT_WIN;
	public final static String RESULTFILE_WIN = PATH_WIN + "result.txt";
	public final static String PRECHECKLOGFILE_WIN = PATH_WIN + "precheck.log";

	// For linux platform
	public final static String FOLDER_LINUX = "precheck_unix";
	public final static String PATH_LINUX = USER_DIR + Constants.FS	+ COMMON_FOLDER + Constants.FS + FOLDER_LINUX + Constants.FS;
	public final static String SH_LINUX = "prereq_checker.sh";
	public final static String CMDLINE_LINUX = PATH_LINUX + SH_LINUX;
	public final static String RESULTFILE_LINUX = PATH_LINUX + "result.txt";
	public final static String PRECHECKLOGFILE_LINUX = PATH_LINUX + "precheck.log";

	// For check properties
	public final static String SPACE = "  ";
	public final static String DB_TYPE = "DBType" + SPACE; 
	public final static String DB_TYPE_DETAILS = "DBTypeDetails" + SPACE;
	public final static String MSSQL_SERVER = "mssql.Server" + SPACE;
	public final static String MSSQL_SERVER_LOCATION = "mssql.Server.Location" + SPACE;
	public final static String DB_DB2 = "DB2";
	public final static String DB_ORACLE = "ORACLE";
	public final static String DB_MSSQL = "MSSQL";
//		public final static String PROPERTIES[] = new String[] { DB_TYPE,DB_TYPE_DETAILS, MSSQL_SERVER, MSSQL_SERVER_LOCATION };
	public final static String PROPERTIES[] = new String[] { DB_TYPE,DB_TYPE_DETAILS};
	public final static String DICTIONARY = "Dictionary";
	public final static String LEFT_BRACKET = "]";
	public final static String EQUAL = "=";

	public final static String DB_NAME = "DbName";
	public final static String DB_VERSION = "DbVersion";
	public final static String DB_INSTALLLOC = "DbInstallLoc";
	public final static String EXPECTED_VALUE = "any";
	public final static String UNKNOWN="Unknown";
	private static final String[] EXECUTABLE_FOLDER_2017 = { "Client SDK","ODBC","130", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2016 = { "Client SDK","ODBC","130", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2012 = { "110", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2008 = { "100", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2005 = { "90", "Tools", "Binn" };
	private static final char WHITE_SPACE = ' ';
	
	public static void main(String args[]) throws IOException,
			InterruptedException {
		//System.out.println(DepCheckByPrereqScanner.check());
	}
	public static Map<String, ProductInfo> check()throws IOException,
	InterruptedException {
		String platform = CommonHelper.getPlatformType();
		if(platform == Constants.OS_WINDOWS){
			return checkByParsePrecheckLogFile();
		}else{
			return checkByParseResultFile();
		}
		
	}
	
	public static Map<String, ProductInfo> checkByParsePrecheckLogFile()throws IOException,
	InterruptedException {
		Map<String, ProductInfo> parseResultMap = new HashMap<String, ProductInfo>();
		Map<String, String> checkResultMap = checkPrecheckLogFile(PROPERTIES, "KUD");
		
		String str_dbTypes = checkResultMap.get(DB_TYPE);
		String str_dbTypeDetails = checkResultMap.get(DB_TYPE_DETAILS);
		String dbTypes[] = null;
		String dbTypeDetails[] = null;
		String dbTypeDetail[] = null;

		if (str_dbTypes != null
				&& !str_dbTypes.trim().equalsIgnoreCase(STR_NOT_FOUND)) {
			ProductInfo productInfo = null;
			dbTypes = str_dbTypes.split(",");
			dbTypeDetails = str_dbTypeDetails.split(";");
			for (int i = 0; i < dbTypes.length; i++) {
				if (dbTypes[i].equalsIgnoreCase(DB_DB2)) {
					dbTypeDetail = dbTypeDetails[i].split(",");
					productInfo = new ProductInfo();
					productInfo.setName(Constants.DB_DB2);
					productInfo.setInstallLoc(dbTypeDetail[1]);
					productInfo.setVersion(dbTypeDetail[2]);
					parseResultMap.put(productInfo.getName().toLowerCase(),
							productInfo);
				} else if (dbTypes[i].equalsIgnoreCase(DB_ORACLE)) {
					dbTypeDetail = dbTypeDetails[i].split(",");
					productInfo = new ProductInfo();
					productInfo.setName(Constants.DB_ORACLE);
					productInfo.setInstallLoc(dbTypeDetail[1]);
					productInfo.setVersion(dbTypeDetail[2]);
					parseResultMap.put(productInfo.getName().toLowerCase(),
							productInfo);
				} else if (dbTypes[i].equalsIgnoreCase(DB_MSSQL)) {
					dbTypeDetail = dbTypeDetails[i].split(",");
					productInfo = new ProductInfo();
					productInfo.setName(Constants.DB_SQLSERVER);
					productInfo.setVersion(dbTypeDetail[2]);
					productInfo.setInstallLoc(getSQLServerInstallLoc(dbTypeDetail[1],productInfo.getVersion()));
					parseResultMap.put(productInfo.getName().toLowerCase(),
							productInfo);
				}
			}
		}
		return parseResultMap;
	}
	
	public static Map<String, ProductInfo> checkByParseResultFile() throws IOException,
			InterruptedException {
		Map<String, ProductInfo> parseResultMap = new HashMap<String, ProductInfo>();
		Map<String, String> checkResultMap = checkResultFile(PROPERTIES, "KUD");

		String str_dbTypes = checkResultMap.get(DB_TYPE);
		String str_dbTypeDetails = checkResultMap.get(DB_TYPE_DETAILS);
		String dbTypes[] = null;
		String dbTypeDetails[] = null;
		String dbTypeDetail[] = null;

		if (str_dbTypes != null
				&& !str_dbTypes.trim().equalsIgnoreCase(STR_NOT_FOUND)) {
			ProductInfo productInfo = null;
			dbTypes = str_dbTypes.split(",");
			dbTypeDetails = str_dbTypeDetails.split(";");
			for (int i = 0; i < dbTypes.length; i++) {
				if (dbTypes[i].equalsIgnoreCase(DB_DB2)) {
					dbTypeDetail = dbTypeDetails[i].split(",");
					productInfo = new ProductInfo();
					productInfo.setName(Constants.DB_DB2);
					productInfo.setInstallLoc(dbTypeDetail[1]);
					productInfo.setVersion(dbTypeDetail[2]);
					parseResultMap.put(productInfo.getName().toLowerCase(),
							productInfo);
				} else if (dbTypes[i].equalsIgnoreCase(DB_ORACLE)) {
					dbTypeDetail = dbTypeDetails[i].split(",");
					productInfo = new ProductInfo();
					productInfo.setName(Constants.DB_ORACLE);
					productInfo.setInstallLoc(dbTypeDetail[1]);
					productInfo.setVersion(dbTypeDetail[2]);
					parseResultMap.put(productInfo.getName().toLowerCase(),
							productInfo);
				} else if (dbTypes[i].equalsIgnoreCase(DB_MSSQL)) {
					// dbTypeDetail = dbTypeDetails[i].split(",");
					productInfo = new ProductInfo();
					productInfo.setName(Constants.DB_SQLSERVER);
					// productInfo.setInstallLoc(dbTypeDetail[1]);
					// productInfo.setVersion(dbTypeDetail[2]);
					productInfo.setVersion(checkResultMap.get(MSSQL_SERVER));
					productInfo.setInstallLoc(getSQLServerInstallLoc(
							checkResultMap.get(MSSQL_SERVER_LOCATION)
									.split(",")[1], productInfo.getVersion()));
					productInfo.setVersion(checkResultMap.get(MSSQL_SERVER));
					parseResultMap.put(productInfo.getName().toLowerCase(),
							productInfo);
				}
			}
		}
		return parseResultMap;
	}

	private static Map<String, String> checkPrecheckLogFile(String properties[],
			String checkFileName) throws IOException, InterruptedException {
		Map<String, String> checkResultMap = new HashMap<String, String>();

		Process proc = null;
		ProcessBuilder pb = null;

		String precheckLogFile = null;
		String platform = CommonHelper.getPlatformType();

		if (platform == Constants.OS_WINDOWS) {
			precheckLogFile = PRECHECKLOGFILE_WIN;
			pb = new ProcessBuilder("cmd", "/c", CMDLINE_WIN + SPACE
					+ checkFileName);
		} else if (platform == Constants.OS_LINUX_REDHAT) {
			precheckLogFile = PRECHECKLOGFILE_LINUX;
			pb = new ProcessBuilder("bash", "-c", "cd " + PATH_LINUX + ";"
					+ "." + Constants.FS + SH_LINUX + SPACE + checkFileName);
		} else if (platform == Constants.OS_LINUX_SUSE) {
			precheckLogFile = PRECHECKLOGFILE_LINUX;
			pb = new ProcessBuilder("bash", "-c", "cd " + PATH_LINUX + ";"
					+ "." + Constants.FS + SH_LINUX + SPACE + checkFileName);
		} else if (platform == Constants.OS_AIX) {
			// The command for AIX is TBD
			precheckLogFile = PRECHECKLOGFILE_LINUX;
			pb = new ProcessBuilder("bash", "-c", "cd " + PATH_LINUX + ";"
					+ "." + Constants.FS + SH_LINUX + SPACE + checkFileName);
		}
		proc = pb.start();
		proc.waitFor();
		File file = new File(precheckLogFile);
		FileInputStream fis = null;
		DataInputStream in = null;
		BufferedReader br = null;
		String strLine = null;
		boolean findStart = false;
		boolean findEnd = false;
		if (file.exists()) {
			fis = new FileInputStream(file);
			in = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(in));
			while ((strLine = br.readLine()) != null) {
		//		
				if (strLine.startsWith(DICTIONARY)) {
					findStart = true;
					continue;
				}
				if (findStart == true && findEnd == false
						&& strLine.startsWith(LEFT_BRACKET)) {
					break;
				}
				if (findStart == true && findEnd == false) {
					
					for (int i = 0; i < properties.length; i++) {
						String property = properties[i];
						if (strLine.contains(property.trim()+EQUAL)) {
							if (!strLine.contains(UNKNOWN)) {
								int startPos = strLine.indexOf(EQUAL);
								String value = strLine.substring(startPos+1);
								checkResultMap.put(property, value);
							} else {
								checkResultMap.put(property, UNKNOWN);
							}
						}
					}
				}	
			}
			in.close();
		} else {
			logger.log(Level.SEVERE, "the result.txt file was not generated",
					"");
		}
		return checkResultMap;
	}

	private static Map<String, String> checkResultFile (
			String properties[], String checkFileName) throws IOException,
			InterruptedException {
		Map<String, String> checkResultMap = new HashMap<String, String>();
		Process proc = null;
		ProcessBuilder pb = null;

		String resultFile = null;
		String platform = CommonHelper.getPlatformType();
		if (platform == Constants.OS_WINDOWS) {
			resultFile = RESULTFILE_WIN;
			pb = new ProcessBuilder("cmd", "/c", CMDLINE_WIN + SPACE
					+ checkFileName);
		} else if (platform == Constants.OS_LINUX_REDHAT) {
			resultFile = RESULTFILE_LINUX;
			pb = new ProcessBuilder("bash", "-c", "cd " + PATH_LINUX + ";"
					+ "." + Constants.FS + SH_LINUX + SPACE + checkFileName);
			
		} else if (platform == Constants.OS_LINUX_SUSE) {
			resultFile = RESULTFILE_LINUX;
			pb = new ProcessBuilder("bash", "-c", "cd " + PATH_LINUX + ";"
					+ "." + Constants.FS + SH_LINUX + SPACE + checkFileName);
		} else if (platform == Constants.OS_AIX) {
			// The command for AIX is TBD
			resultFile = RESULTFILE_LINUX;
			pb = new ProcessBuilder("bash", "-c", "cd " + PATH_LINUX + ";"
					+ "." + Constants.FS + SH_LINUX + SPACE + checkFileName);
		}	
		proc = pb.start();

		proc.waitFor();
		File file = new File(resultFile);
		FileInputStream fis = null;
		DataInputStream in = null;
		BufferedReader br = null;
		String strLine = null;
		if (file.exists()) {
			fis = new FileInputStream(file);
			in = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(in));
			while ((strLine = br.readLine()) != null) {
				for (int i = 0; i < properties.length; i++) {
					String property = properties[i];
					if (strLine.startsWith(property)) {
						if (!strLine.contains(STR_NOT_FOUND)) {
							int startPos = -1;
							int startPos_Pass = strLine.indexOf(STR_PASS);
							int startPos_Fail = strLine.indexOf(STR_FAIL);
							int endPos = -1;
							int length = strLine.length();
							startPos = (startPos_Pass > startPos_Fail) ? startPos_Pass
									+ STR_PASS.length()
									: startPos_Fail + STR_FAIL.length();
							if (startPos != -1) {
								String value = null;
								while (strLine.charAt(startPos) == WHITE_SPACE) {
									startPos++;
								}
								endPos = startPos;
								char pre_tempCharacter;
								char post_tempCharacter;
								while (endPos < length - 1) {
									pre_tempCharacter = strLine.charAt(endPos);
									endPos++;
									post_tempCharacter = strLine.charAt(endPos);
									if (pre_tempCharacter == ' ') {
										if (post_tempCharacter == ' '
												|| strLine.substring(endPos,
														endPos + 3)
														.equalsIgnoreCase(
																EXPECTED_VALUE)) {
											break;
										}
									}
								}
								value = strLine.substring(startPos, endPos - 1);
								checkResultMap.put(property, value);
							} else {
								// System.out.println("Not Found");
							}
						} else {
							// System.out.println("Not Found");
							checkResultMap.put(property, STR_NOT_FOUND);
						}
						// break;
					}
				}
			}
			in.close();
		} else {
			// System.out.println("the result.txt file was not generated");
			logger.log(Level.SEVERE, "the result.txt file was not generated",
					"");
		}
		// System.out.println("running finished");

		return checkResultMap;
	}

	private static String getSQLServerInstallLoc(String path, String version) {
		String installLoc = path;
		String currentPath = path + Constants.FS;
		String mainVersionNum = version;
		
		if(version.startsWith("11")){
			mainVersionNum = EXECUTABLE_FOLDER_2012[0];
		}else if (version.startsWith("10")) {
			mainVersionNum = EXECUTABLE_FOLDER_2008[0];
		}else if (version.startsWith("13")) {
			mainVersionNum = EXECUTABLE_FOLDER_2016[0];
		}else if (version.startsWith("14")) {
			mainVersionNum = EXECUTABLE_FOLDER_2017[0];
			// Instead of give the SQL installation location, the prereq_checker.bat
			// puts the instance home in precheck.log for SQL Server 2017, to avoid
			// change the VBS script, try to use the parent directory as the install
			// location.
			// Example of what's found in the precheck.lgo file:
			// DBTypeDetails=MSSQL MSSQLSERVER,C:\Program Files\Microsoft SQL Server\MSSQL14.MSSQLSERVER\MSSQL,14.0.1000.169
			File instance = new File(currentPath);
			currentPath = instance.getParentFile().getPath();
			installLoc = currentPath;
		}else {
			int pos = version.indexOf(".");
			if (pos != -1) {
				mainVersionNum = version.substring(0, pos) + "0";
			} else {
				mainVersionNum = version + "0";
			}
		}


		int lastIndexofSlash = -1;
		File file = null;
		do {
			lastIndexofSlash = currentPath.lastIndexOf(Constants.FS);
			if (lastIndexofSlash != -1) {
				currentPath = currentPath.substring(0, lastIndexofSlash);
			}
			file = new File(currentPath + Constants.FS + (mainVersionNum));

			if (file.exists()) {
				installLoc = currentPath;
				break;
			}
		} while (lastIndexofSlash != -1);
		return installLoc;
	}
}
