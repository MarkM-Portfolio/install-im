/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

public class DatabaseUtil {
	private DatabaseUtil() {
	}

	public static final String JDBC_DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver"; //$NON-NLS-1$
	public static final String JDBC_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver"; //$NON-NLS-1$
	public static final String JDBC_DRIvER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //$NON-NLS-1$
	// OS400_Enablement
	public static final String JDBC_DRIVER_DB2_ISERIES = "com.ibm.as400.access.AS400JDBCDriver"; //$NON-NLS-1$

	public static final String DBMS_ORACLE = "Oracle";
	public static final String DBMS_SQLSERVER = "SQL Server";
	public static final String DBMS_DB2 = "DB2";
	// OS400_Enablement
	public static final String DBMS_DB2_ISERIES = "DB2_I";
	
	public static final String JDBC_DB2_LIB = "db2jcc4.jar";
	public static final String JDBC_DB2_LICENSE = "db2jcc_license_cu.jar";
	public static final String JDBC_ORACLE_LIB = "ojdbc8.jar";
	public static final String JDBC_SQLSERVER_LIB = "sqljdbc42.jar";
	// OS400_Enablement
	public static final String JDBC_DB2_ISERIES_LIB = "jt400.jar";

	public static String getJDBCDriver(String dbType, String appName) {
		if (DBMS_DB2.equals(dbType)) {
			return JDBC_DRIVER_DB2;
		}
		if (DBMS_ORACLE.equals(dbType)) {
			return JDBC_DRIVER_ORACLE;
		}
		if (DBMS_SQLSERVER.equals(dbType)) {
			return JDBC_DRIvER_SQLSERVER;
		}
		// OS400_Enablement
		if (DBMS_DB2_ISERIES.equals(dbType)) {
			if (! appName.equalsIgnoreCase("METRICS")) {
				return JDBC_DRIVER_DB2_ISERIES;
			} else {
				return JDBC_DRIVER_DB2;
			}
		}
		return null;
	}

	public static String getDBUrl(String dbType, String dbHostname, String dbPort, String dbName, String appName) {
		StringBuffer sb = new StringBuffer();
		if (DBMS_DB2.equals(dbType)) {
			sb.append("jdbc:db2://").append(dbHostname).append(":").append(dbPort).append("/").append(dbName);
		}
		if (DBMS_ORACLE.equals(dbType)) {
			sb.append("jdbc:oracle:thin:@").append(dbHostname).append(":").append(dbPort).append("/").append(dbName);
		}
		if (DBMS_SQLSERVER.equals(dbType)) {
			sb.append("jdbc:sqlserver://").append(dbHostname).append(":").append(dbPort).append(";databaseName=").append(dbName);
		}
		// OS400_Enablement
		if (DBMS_DB2_ISERIES.equals(dbType)) {
			if (! appName.equalsIgnoreCase("METRICS")) {
				sb.append("jdbc:as400://").append(dbHostname).append(":").append(dbPort).append("/").append(dbName);
			} else {
				//return getOS400MetricsDBUrl(dbType, dbHostname, dbPort, dbName);
				sb.append("jdbc:db2://").append(dbHostname).append(":").append(dbPort).append("/").append(dbName);
			}
		}
		return sb.toString();
	}

	public static String getClasspath(String dbType, String jdbcPath, String appName) {
		StringBuffer sb = new StringBuffer();
		if (DBMS_DB2.equals(dbType)) {
			sb.append("./;").append(jdbcPath).append("/").append(JDBC_DB2_LIB).append(";").append(jdbcPath).append("/")
					.append(JDBC_DB2_LICENSE);
		}
		if (DBMS_ORACLE.equals(dbType)) {
			sb.append("./;").append(jdbcPath).append("/").append(JDBC_ORACLE_LIB);
		}
		if (DBMS_SQLSERVER.equals(dbType)) {
			sb.append("./;").append(jdbcPath).append("/").append(JDBC_SQLSERVER_LIB);
		}
		// OS400_Enablement
		if (DBMS_DB2_ISERIES.equals(dbType)) {
			if (appName.equalsIgnoreCase("metrics")) {
				//sb.append("./;").append(jdbcPath).append("/").append(JDBC_DB2_ISERIES_NATIVE_LIB);
				sb.append("./;").append(jdbcPath).append("/").append(JDBC_DB2_LIB).append(";").append(jdbcPath).append("/")
					.append(JDBC_DB2_LICENSE);
			} else {
				sb.append("./;").append(jdbcPath).append("/").append(JDBC_DB2_ISERIES_LIB);
			}
		}
		return sb.toString();
	}
}
