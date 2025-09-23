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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;
import com.ibm.lconn.wizard.common.Validator;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * @deprecated
 */
public abstract class DatabaseValidator implements Validator {
	public static final Logger logger = LogUtil.getLogger(DatabaseValidator.class);
	
	public static final String JAR_LOCATION = "lib/Wizards.jar"; //$NON-NLS-1$
	public static final String TDI_INSTALL_LOC = "TDIInstallLoc"; //$NON-NLS-1$
	public static final String PLATFORM = "Platform"; //$NON-NLS-1$
	public static final String DBTYPE = "DBType"; //$NON-NLS-1$
	public static final String DBHOSTNAME = "Hostname"; //$NON-NLS-1$
	public static final String DBPORT = "Port"; //$NON-NLS-1$
	public static final String DBNAME = "DatabaseName"; //$NON-NLS-1$
	public static final String DBUSER = "User"; //$NON-NLS-1$
	public static final String DBPASSWORD = "Password"; //$NON-NLS-1$
	public static final String FS = System.getProperty("file.separator"); //$NON-NLS-1$
	
	private static final String JAVA_EXECUTABLE = "java"; //$NON-NLS-1$
	private static final String JAVA_EXECUTABLE_WIN = "java.exe"; //$NON-NLS-1$
	
	private String platform, tdiInstallLoc, dbType, dbHostname, dbPort, dbName, dbUser, dbPassword;
	
	/* <code>
	 * 	Properties props = new Properties();
	 *	props.setProperty(DatabaseValidator.TDI_INSTALL_LOC, "D:\\IBM\\TDI\\V6.1" );
	 *	props.setProperty(DatabaseValidator.DBTYPE, Constants.DBMS_SQLSERVER);
	 *	props.setProperty(DatabaseValidator.DBHOSTNAME, "localhost");
	 *	props.setProperty(DatabaseValidator.DBPORT, "1433");
	 *	props.setProperty(DatabaseValidator.DBNAME, "DOGEAR");
	 *	props.setProperty(DatabaseValidator.DBUSER, "sa");
	 *	props.setProperty(DatabaseValidator.DBPASSWORD, "passw0rd");
	 *	props.setProperty(DatabaseValidator.PLATFORM, Constants.OS_WINDOWS);		
	 *	DatabaseValidator dv = new DatabaseValidator();
	 *	System.out.println(dv.validate(props));
	 * </code>
	 * (non-Javadoc)
	 * @see com.ibm.lconn.wizard.common.Validator#validate(java.util.Properties)
	 */
	public int validate(Properties props) {
		processProperties(props);
		// find JRE under TDI installation location
		String javaExecutable = tdiInstallLoc + FS + "jvm" + FS + "jre" + FS + "bin" + FS;
		if(Constants.OS_WINDOWS.equals(platform)) {
			javaExecutable = javaExecutable + JAVA_EXECUTABLE_WIN;
		} else {
			javaExecutable = javaExecutable + JAVA_EXECUTABLE;
		}
		
		ProcessBuilder pb = new ProcessBuilder(javaExecutable, "-classpath", 
				JAR_LOCATION, DatabaseValidator.class.getName(), dbType, dbHostname, dbPort, dbName, dbUser, dbPassword);
		logger.log(Level.FINER, "tdipopulate.finer.db_validation_process_command", pb.command());
		logger.log(Level.INFO, "tdipopulate.info.db_url", DatabaseUtil.getDBUrl(dbType, dbHostname, dbPort, dbName));
		logger.log(Level.INFO, "tdipopulate.info.jdbc_driver", DatabaseUtil.getJDBCDriver(dbType));
		Process p = null;
		
		try {
			p = pb.start();
			p.waitFor();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "dbpopulation.severe.db_validation_process_fail", e);
		} catch (InterruptedException e) {
			// ignore
		}
		
		return p!=null? p.exitValue(): -1;
	}
	
	private void processProperties(Properties props) {
		platform = props.getProperty(PLATFORM, "");
		if("".equals(platform)) {
			throw new IllegalArgumentException("Platform type is not specified");
		}
		
		tdiInstallLoc = props.getProperty(TDI_INSTALL_LOC, "");
		if("".equals(tdiInstallLoc)) {
			throw new IllegalArgumentException("TDI installation location is not specified"); //$NON-NLS-1$
		}
		
		dbType = props.getProperty(DBTYPE, "");
		if("".equals(dbType)) {
			throw new IllegalArgumentException("Database type is not specified"); //$NON-NLS-1$
		}
		
		dbHostname = props.getProperty(DBHOSTNAME, "");
		if("".equals(dbHostname)) {
			throw new IllegalArgumentException("Database host name is not specified"); //$NON-NLS-1$
		}
		
		dbPort = props.getProperty(DBPORT, "");
		if("".equals(dbPort)) {
			throw new IllegalArgumentException("Database port is not specified"); //$NON-NLS-1$
		}
		
		dbName = props.getProperty(DBNAME, "");
		if("".equals(dbName)) {
			throw new IllegalArgumentException("Database name is not specified"); //$NON-NLS-1$
		}
		
		dbUser = props.getProperty(DBUSER, "");
		if("".equals(dbUser)) {
			throw new IllegalArgumentException("Database user is not specified"); //$NON-NLS-1$
		}
		
		dbPassword = props.getProperty(DBPASSWORD, "");
		if("".equals(dbPassword)) {
			throw new IllegalArgumentException("Database user password is not specified"); //$NON-NLS-1$
		}
		
		return;
	}
	
	
	/**
	 * Main method to validate database connection
	 * @param args
	 */
	public static void main(String[] args) {
		String dbType = args[0];
		String dbHostname = args[1];
		String dbPort = args[2];
		String dbName = args[3];
		String dbUser = args[4];
		String dbPassword = args[5];
		
		String driver = DatabaseUtil.getJDBCDriver(dbType);
		String dbUrl = DatabaseUtil.getDBUrl(dbType, dbHostname, dbPort, dbName);
		
		try {
			Class.forName(driver);
		} catch(ClassNotFoundException e) {
			// cannot find driver
			System.exit(1);
		}
		
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		} catch (SQLException e) {
			// cannot connect to database
			if(Constants.DBMS_ORACLE.equals(dbType)) {
				String oracle11gUrl = DatabaseUtil.getOracle11gDBUrl(dbHostname, dbPort, dbName);
				try {
					conn = DriverManager.getConnection(oracle11gUrl, dbUser, dbPassword);
				} catch (SQLException e1) {
					System.exit(2);
				}
				catch (Exception e2) {
					System.exit(Constants.ERROR_UNKNOWN);
				}
				System.exit(0);
			}
			System.exit(2);
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
		// valid
		System.exit(0);
	}

}
