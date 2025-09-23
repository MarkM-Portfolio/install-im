/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited 2010, 2020                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;  // OS400_Enablement
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * The msg key used in this class: db.validation.illegalArgument.dbtype
 * db.validation.illegalArgument.dbHost db.validation.illegalArgument.dbPort
 * db.validation.illegalArgument.dbName db.validation.illegalArgument.dbUser
 * db.validation.illegalArgument.dbPassword db.validation.fail.authentication
 * db.validation.fail.databaseNotExist db.validation.fail.communicationError
 * db.validation.fail.launchValidation db.validation.fail.jdbcDriver
 */

/**
 * 
 * @author Jun Jin Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class DbConnectionValidator {
	private static final String DB_VALIDATION_ERROR_MSG_KEY = "validation.result";
	private static final String DB2_EXCEPTION_PATTERN = "\\[(\\d+)\\]";
	public static final int NO_JDBC_DRIVER = 50;
	public static final int INVALID_AUTHENTICATION = 10;
	public static final int DB_NOT_EXIST = 20;
	public static final int COMMUNICATION_ERROR = 30;
	public static final int UNKNOW_ERROR = 15;
	public static final int ILLEGAL_ARGUMENT = 60;
	public static final int LOGIN_TIMEOUT = 60;

	private static final String DBMS_ORACLE = DatabaseUtil.DBMS_ORACLE;// "oracle";
	private static final String DBMS_SQLSERVER = DatabaseUtil.DBMS_SQLSERVER;// "sqlserver";
	private static final String DBMS_DB2 = DatabaseUtil.DBMS_DB2;// "db2";
	// OS400_Enablement
	private static final String DBMS_DB2_ISERIES = DatabaseUtil.DBMS_DB2_ISERIES;// "db2_iseries";

	private String dbType, dbHostname, dbPort, dbName, dbUser, dbPassword, appName;

	// private static PrintStream ps = null;
	// static {
	// try {
	// ps = new PrintStream(new FileOutputStream("C:/xxx.log"));
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	// public static void log(Object o){
	// ps.print(o);
	// }
	//	
	// public static void log(DbConnectionValidator v){
	// ps.print("String dbType=\""+v.dbType+"\";"+"\n");
	// ps.print("String dbHostname=\""+v.dbHostname+";"+"\n");
	// ps.print("String dbPort=\""+v.dbPort+"\";"+"\n");
	// ps.print("String dbName=\""+v.dbName+"\";"+"\n");
	// ps.print("String dbUser=\""+v.dbUser+"\";"+"\n");
	// ps.print("String dbPassword=\""+v.dbPassword+"\";"+"\n");
	// }

	public DbConnectionValidator(String dbType, String dbHostname, String dbPort, String dbName, String dbUser,
			String dbPassword, String propertiesFile, String appName) {
		StringBuffer sb = new StringBuffer();
		String[] keys = { "dbType", " dbHostname", " dbPort", " dbName", " dbUser", " dbPassword", " propertiesFile", "appName" };
		String[] list = { dbType, dbHostname, dbPort, dbName, dbUser, dbPassword, propertiesFile, appName };
		for (int i = 0; i < list.length; i++) {
			sb.append("\t\t\t" + keys[i] + ": " + list[i] + "\n");
		}
		this.dbType = dbType;
		this.dbHostname = dbHostname;
		this.dbPort = dbPort;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.appName = appName;
		// log(this);
		//checkInput();
	}

	public void checkInput() {
		if ("".equals(dbType)) {
			throw new IllegalArgumentException("db.validation.illegalArgument.dbtype"); //$NON-NLS-1$
		}
		if ("".equals(dbHostname)) {
			throw new IllegalArgumentException("db.validation.illegalArgument.dbHost"); //$NON-NLS-1$
		}
		if ("".equals(dbPort) || (!dbPort.matches("\\d{2,5}"))) {
			throw new IllegalArgumentException("db.validation.illegalArgument.dbPort"); //$NON-NLS-1$
		}
		if ("".equals(dbName)) {
			throw new IllegalArgumentException("db.validation.illegalArgument.dbName"); //$NON-NLS-1$
		}
		if ("".equals(dbUser)) {
			throw new IllegalArgumentException("db.validation.illegalArgument.dbUser"); //$NON-NLS-1$
		}
		if ("".equals(dbPassword)) {
			throw new IllegalArgumentException("db.validation.illegalArgument.dbPassword"); //$NON-NLS-1$
		}
	}

	public static String getMsgKey(int returnCode) {
		switch (returnCode) {
		case NO_JDBC_DRIVER:
			return "db.validation.fail.jdbcDriver";
		case INVALID_AUTHENTICATION:
			return "db.validation.fail.authentication";
		case DB_NOT_EXIST:
			return "db.validation.fail.databaseNotExist";
		case COMMUNICATION_ERROR:
			return "db.validation.fail.communicationError";
		case UNKNOW_ERROR:
			return "db.validation.fail.launchValidation";
		case 0:
			return "PASS";
		default:
			return "db.validation.fail.launchValidation";
		}
	}

	/**
	 * Main method to validate database connection
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String dbType = args[0];
			String dbHostname = args[1];
			String dbPort = args[2];
			String dbName = args[3];
			String dbUser = args[4];
			String dbPassword = args[5];
			String propertiesFile = args[6];
			String appName = args[7];
			Properties props = new Properties();

			try {
				DbConnectionValidator validator = new DbConnectionValidator(dbType, dbHostname, dbPort, dbName, dbUser,
						dbPassword, propertiesFile, appName);
				int returnCode = validator.run();
				props.setProperty(DB_VALIDATION_ERROR_MSG_KEY, getMsgKey(returnCode));
				saveProps(props, propertiesFile);
				System.exit(returnCode);
			} catch (IllegalArgumentException e) {
				String msg = e.getMessage();
				props.setProperty(DB_VALIDATION_ERROR_MSG_KEY, msg);
				saveProps(props, propertiesFile);
				System.exit(ILLEGAL_ARGUMENT);
			} catch (Exception e) {
				props.setProperty(DB_VALIDATION_ERROR_MSG_KEY, getMsgKey(UNKNOW_ERROR));
				saveProps(props, propertiesFile);
				System.exit(UNKNOW_ERROR);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	private static void saveProps(Properties props, String propertiesFile) {
		try {
			File f = new File(propertiesFile);
			f.getParentFile().mkdirs();
			if (f.exists())
				f.delete();
			FileOutputStream fileOutputStream = new FileOutputStream(f);
			props.store(fileOutputStream, null);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			
		}
	}

	private int run() {
		// OS400_Enablement
		// Metrics on OS400 will use DB2 on AIX. Starting from Connections 5.0, we start to use the LUW Jcc JDBC driver.
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400") && 
			dbName.equalsIgnoreCase("METRICS")) {
			return validateOS400MetricsDBConnection(dbHostname, dbPort, dbName, dbUser, dbPassword);
		} else {
			String driver = DatabaseUtil.getJDBCDriver(dbType, appName);
			String dbUrl = DatabaseUtil.getDBUrl(dbType, dbHostname, dbPort, dbName, appName);

			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				return NO_JDBC_DRIVER;
			}

			Connection conn = null;

			try {
				DriverManager.setLoginTimeout(LOGIN_TIMEOUT); // fail after LOGIN_TIMEOUT seconds
				conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			} catch (SQLException e) {
				e.printStackTrace();
				return processSQLException(dbType, appName, e);
			} catch (Exception e) {
				e.printStackTrace();
				return UNKNOW_ERROR;
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		// valid
		return 0;
	}

	private static int processSQLException(String dbType, String appName, SQLException e) {
		String sqlState = e.getSQLState();
		int errorCode = e.getErrorCode();

		// OS400_Enablement 
		if (DBMS_DB2_ISERIES.equals(dbType)) {
			/**
			System.out.println("======================================");
			System.out.println("======================================");
			System.out.println("SQLState: " + sqlState + ", SQL ErrorCode: " + errorCode);
			System.out.println("SQL Error Message:");
			System.out.println(""+e.getMessage());
			System.out.println("======================================");
			System.out.println("======================================");
			**/
			if ("08001".equals(sqlState)) {
				return COMMUNICATION_ERROR;
			} else if ("08004".equals(sqlState)) {
				return INVALID_AUTHENTICATION;
			}
			return UNKNOW_ERROR;
		}

		if (DBMS_DB2.equals(dbType)) {
			String c1 = null;
			String c2 = null;
			Pattern p = Pattern.compile(DB2_EXCEPTION_PATTERN);
			Matcher m = p.matcher(e.getMessage());
			if (m.find()) {
				c1 = m.group(1);
			}
			if (m.find()) {
				c2 = m.group(1);
			}
			if ("2043".equals(c1) && "11550".equals(c2)) {
				return COMMUNICATION_ERROR;
			} else if ("2057".equals(c1) && "11264".equals(c2)) {
				return DB_NOT_EXIST;
			} else if ("2013".equals(c1) && "11249".equals(c2)) {
				return INVALID_AUTHENTICATION;
			}
		}
		if (DBMS_ORACLE.equals(dbType)) {
			if (17002 == errorCode) {
				return COMMUNICATION_ERROR;
			} else if (0 == errorCode) {
				return DB_NOT_EXIST;
			} else if (1017 == errorCode || 17443 == errorCode) {
				return INVALID_AUTHENTICATION;
			}
		}
		if (DBMS_SQLSERVER.equals(dbType)) {
			if ("08S01".equals(sqlState) && 0 == errorCode) {
				return COMMUNICATION_ERROR;
			} else if ("S0001".equals(sqlState) && 4060 == errorCode) {
				return DB_NOT_EXIST;
			} else if ("S0001".equals(sqlState) && 18456 == errorCode) {
				return INVALID_AUTHENTICATION;
			}
		}
		return UNKNOW_ERROR;
	}

	// OS400_Enablement
	private static int validateOS400MetricsDBConnection(String dbHostname, String dbPort, String dbName, String dbUser, String dbPassword) {
		Connection c = null;
		Statement s = null;
		try {
			Integer nDBPort = Integer.valueOf(dbPort);
			javax.sql.DataSource ds = new com.ibm.db2.jcc.DB2SimpleDataSource();
			((com.ibm.db2.jcc.DB2SimpleDataSource) ds).setServerName(dbHostname);
			((com.ibm.db2.jcc.DB2SimpleDataSource) ds).setPortNumber(nDBPort.intValue());
			((com.ibm.db2.jcc.DB2SimpleDataSource) ds).setDatabaseName(dbName);
			((com.ibm.db2.jcc.DB2SimpleDataSource) ds).setDriverType(4);

			c = ds.getConnection(dbUser, dbPassword);
			s = c.createStatement();
		} catch (SQLException e) {
			String sqlState = e.getSQLState();
			// int errorCode = e.getErrorCode();
			if ("08001".equals(sqlState)) {
				return COMMUNICATION_ERROR;
			} else if ("08004".equals(sqlState)) {
				return DB_NOT_EXIST;
			} else if ("28000".equals(sqlState)) {
				return INVALID_AUTHENTICATION;
			}
		} catch (Exception e) {
				return UNKNOW_ERROR;
		} finally {
			try {
				if (s!=null)
					s.close();
				if (c!=null)
					c.close();
			} catch (SQLException e) {
			}
		}

		return 0;
	}

}
