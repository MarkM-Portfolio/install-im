/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited. 2006, 2021                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.*;
import java.lang.Exception;			//OS400_Enablement
import java.util.Enumeration;
import java.util.Properties;
import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;

public class DbInstallValidator {
	private String propertiesFile;
	private String message = "";
	private final ILogger log = IMLogger.getLogger(com.ibm.connections.install.DatabasePanel.class);

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		message = msg;
	}

	public boolean JDBCLibraryFolderValidate(String filePath, String dbType) {
		InstallValidator iv = new InstallValidator();
		boolean pass = false;
		if ("".equals(dbType) || "0".equals(dbType)) {
			setMessage(Messages.DB_TYPE_SELECT);
			return false;
		} else if ("".equals(filePath)) {
			setMessage(Messages.DB_JDBCDriver_EMPTY);
			return false;
		} else {
			String result;
			if (DatabaseUtil.DBMS_DB2.equals(dbType) && iv.detectFileExists(DatabaseUtil.JDBC_DB2_LIB, filePath)
					&& iv.detectFileExists(DatabaseUtil.JDBC_DB2_LICENSE, filePath)) {
				pass = true;
			} else if (DatabaseUtil.DBMS_ORACLE.equals(dbType) && iv.detectFileExists(DatabaseUtil.JDBC_ORACLE_LIB, filePath)) {
				pass = true;
			} else if (DatabaseUtil.DBMS_SQLSERVER.equals(dbType) && iv.detectFileExists(DatabaseUtil.JDBC_SQLSERVER_LIB,
					filePath)) {
				pass = true;
			} 
			// OS400_Enablement
			else if (DatabaseUtil.DBMS_DB2_ISERIES.equals(dbType) && iv.detectFileExists(DatabaseUtil.JDBC_DB2_ISERIES_LIB, filePath)) {
				pass = true;
			}

			if (!pass && !DatabaseUtil.DBMS_SQLSERVER.equals(dbType))
				setMessage(Messages.DB_LOAD_DRIVER_ERROR);
			else if (!pass && DatabaseUtil.DBMS_SQLSERVER.equals(dbType))
				setMessage(Messages.DB_LOAD_DRIVER_ERROR);
			return pass;
		}
	}	
	
	/* 
	 * OS400_Enablement
	 * Metrics on OS400 will use the DB2 on AIX.  Startting from Connections 5.0, 
	 * we will use the DB2 LUW JCC JDBC driver to connect to METRICS Db on AIX. 
	 * *********************************************************************************************************
	 * This function verifies the JDBC driver specifically for Metrics on OS400.
	 * *********************************************************************************************************
	 */	
	public boolean OS400MetricsJDBCLibraryFolderValidate(String filePath, String dbType) {
		InstallValidator iv = new InstallValidator();

		boolean pass = false;
		if ("".equals(filePath)) {
			setMessage(Messages.DB_JDBCDriver_EMPTY);
			return false;
		} else {
			// Using the DB2 JDBC lib here for metrics
			if (DatabaseUtil.DBMS_DB2_ISERIES.equals(dbType) && iv.detectFileExists(DatabaseUtil.JDBC_DB2_LIB, filePath)) {
				pass = true;
			}

			if (!pass )
				setMessage(Messages.DB_LOAD_DRIVER_ERROR);
			return pass;
		}
	}

	public void dbConnection(IProfile profile,String dbType, String dbHostname, String dbPort, String dbName, String dbUser, String dbPassword,
			String propertiesFile, String jdbclibStr, String appName) {
		String cmd[] = new String[12];
		Process process = null;
		
		try {
			String osName = System.getProperty("os.name");
			File file = new File(propertiesFile);
			this.propertiesFile = file.getAbsolutePath();
									
			String classpath = DatabaseUtil.getClasspath(dbType, jdbclibStr, appName);
			// OS400_Enablement
			//  OS400 need to use the OS provided JVM.
			if (osName.toLowerCase().startsWith("os/400")){
				cmd[0] = getOS400WASJavaHome(profile) + "/bin/java";
			} else {
				cmd[0] = profile.getUserData("user.was.install.location") + "/java/jre/bin/java";
			}

			cmd[1] = "-cp";
			String classpathStr = DbInstallValidator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if (osName.startsWith("Windows")) {
				classpathStr = DbInstallValidator.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "");
			}
			cmd[2] = classpath + ";" + classpathStr;
			if (!osName.startsWith("Windows")) {
				cmd[2] = cmd[2].replace(";",":");
			}
			cmd[3] = "com.ibm.connections.install.DbConnectionValidator";
			cmd[4] = dbType;
			cmd[5] = dbHostname;
			cmd[6] = dbPort;
			cmd[7] = dbName;
			cmd[8] = dbUser;
			cmd[9] = dbPassword;
			cmd[10] = this.propertiesFile;
			cmd[11] = appName;
			log.info(cmd[0] + " " + cmd[1] + " " + cmd[2]+ " " + cmd[3] + " " + cmd[4] + " " + cmd[5] + " " + 
					cmd[6] + " " + cmd[7] + " " + cmd[8] + " PASSWORD_REMOVED  " + cmd[10] + " " + cmd[11]);  
			log.info("CurrentPath: " + new File(".").getAbsolutePath());

 			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
				log.info(line);
			process.waitFor();
		} catch (InterruptedException e) {
			log.error(e);  
		}   catch (Exception e) {
			log.error(e);  
		}
	}

	public String getJREHome() {
		String path = "jre_5.0.1.sr8a_20080811a"; // zlinux
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			path = "jre_5.0.3.sr8a_20080811b";
		} else if (osName.equals("Linux")) {
			path = "jre_5.0.3.sr8a_20080811b";
		} else if (osName.equals("AIX")) {
			path = "jre_5.0.1.sr8a_20080811c";
		// OS400_Enablement
		} else if (osName.toLowerCase().startsWith("os/400")) {
			// This function is not used anywhere, but set it to the defaule one.
			// path = getOS400WASJavaHome(profile);
			path = "/QOpenSys/QIBM/ProdData/JavaVM/jdk60/64bit";
		}

		return path;
	}

	// OS400_Enablement
	// The WAS JAVA_HOME can be determined from the <WAS Home>/bin/setupCmdLine fileor the
	// <WAS Home>/properties/os400.properties file.
	// For WAS 8, should we switch to use the managesdk command to check the JVM home? 
	//       Since it can changed later by the managesdk command.
	//       No, we decided only to support the 64bit JVM for Connections 4.5 with WAS 8.
	public String getOS400WASJavaHome(IProfile profile) {
		/******
		Properties prop = new Properties();
		String	confName = profile.getUserData("user.was.install.location") + "/properties/os400.properties";
		try {
			File conf = new File(confName);
			prop.load(new FileInputStream(conf));
		} catch (Exception e) {
			// Return below value as default, if can not find the right one.
			// e.printStackTrace();
			return "/QOpenSys/QIBM/ProdData/JavaVM/jdk60/64bit";
		}
		
		return	prop.getProperty("JAVA_HOME");
		*******/
		return "/QOpenSys/QIBM/ProdData/JavaVM/jdk60/64bit";
	}


	public int loadResult() {
		String result = null;
		try {
			FileInputStream fis = new FileInputStream(this.propertiesFile);
			Properties props = new Properties();
			props.load(fis);
			Enumeration propertyNames = props.propertyNames();
			while (propertyNames.hasMoreElements()) {
				String nextElement = (String) propertyNames.nextElement();
				String value = props.getProperty(nextElement);
				if (nextElement.equalsIgnoreCase("validation.result"))
					result = value;
			}
			fis.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		if (result != null) {
			if (result.equalsIgnoreCase("PASS"))
				return 0;
			else
				setMessage(getShowMsg(result));
		} else
			setMessage(Messages.DB_UNKNOW_DATABASE_NEW);

		return -1;
	}
	
	private String getShowMsg(String msgKey){
		
		if(msgKey.equalsIgnoreCase("db.validation.fail.jdbcDriver")){
			return Messages.DB_fail_jdbcDriver;
		
		} else if(msgKey.equalsIgnoreCase("db.validation.fail.authentication")){
			return Messages.DB_fail_authentication;
		} else if(msgKey.equalsIgnoreCase("db.validation.fail.databaseNotExist")){
			return Messages.DB_fail_databaseNotExist;
		} else if(msgKey.equalsIgnoreCase("db.validation.fail.communicationError")){
			return Messages.DB_fail_communicationError;
		} else if(msgKey.equalsIgnoreCase("db.validation.fail.launchValidation")){
			return Messages.DB_fail_launchValidation;
		} else if(msgKey.equalsIgnoreCase("PASS")){
			return Messages.DB_PASS;
		} else {
			return Messages.DB_fail_launchValidation;
		}
		
	}
	
	public static void main(String args[]) {
		DbInstallValidator iv = new DbInstallValidator();
		//iv.dbConnection();
		//System.out.println(iv.loadResult());
	}
}
