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

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.util;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class DatabaseUtil {
	private DatabaseUtil() {}
		
	public static final String JDBC_DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver"; //$NON-NLS-1$
	public static final String JDBC_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver"; //$NON-NLS-1$
	public static final String JDBC_DRIvER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //$NON-NLS-1$

	public static final String DBMS_ORACLE = "oracle";
	public static final String DBMS_SQLSERVER = "sqlserver";
	public static final String DBMS_DB2 = "db2";
	
//	private static final String DB_URL_ORACLE = "jdbc:oracle:thin:@{0}:{1}:{2}";
//	private static final String DB_URL_DB2 = "jdbc:db2://{0}:{1}/{2}";
//	private static final String DB_URL_SQL = "jdbc:sqlserver://{0}:{1};databaseName={2}";

//	private static final String DB_JAR_FILE_DB2 = "";
//	private static final String DB_JAR_FILE_ORACLE = "";
//	private static final String DB_JAR_FILE_SQLSERVER = "";

//	private static final int ID_NONE = -1;
//	private static final int ID_DB2 = 0;
//	private static final int ID_ORACLE = 1;
//	private static final int ID_SQLSERVER = 2;
//	private static final String[] DB_URL = { DB_URL_DB2, DB_URL_ORACLE,
//		DB_URL_SQL };
//	private static final String[] DB_JAR_FIE = { DB_JAR_FILE_DB2,
//		DB_JAR_FILE_ORACLE, DB_JAR_FILE_SQLSERVER };


	public static String getJDBCDriver(String dbType) {
		if(DBMS_DB2.equals(dbType)) {
			return JDBC_DRIVER_DB2;
		}
		if(DBMS_ORACLE.equals(dbType)) {
			return JDBC_DRIVER_ORACLE;
		}
		if(DBMS_SQLSERVER.equals(dbType)) {
			return JDBC_DRIvER_SQLSERVER;
		}
		return null;
	}
	
//	private static int getDbTypeId(String dbType) {
//		if ("db2".equals(dbType))
//			return ID_DB2;
//		else if ("sqlserver".equals(dbType))
//			return ID_SQLSERVER;
//		else if ("oracle".equals(dbType))
//			return ID_ORACLE;
//		return ID_NONE;
//	}
//	
//	
//	
//	public static String getDBUrl(String dbType, String dbHostname, String dbPort, String dbName) {
//		int dbId = getDbTypeId(dbType);
//		return MessageFormat.format(DB_URL[dbId], dbHostname, dbPort, dbName);
//	}
	public static String getDBUrl(String dbType, String dbHostname, String dbPort, String dbName) {
		StringBuffer sb = new StringBuffer();
		if(DBMS_DB2.equals(dbType)) {
			sb.append("jdbc:db2://").
				append(dbHostname).
				append(":").
				append(dbPort).
				append("/").
				append(dbName);
		}
		if(DBMS_ORACLE.equals(dbType)) {
			sb.append("jdbc:oracle:thin:@").
				append(dbHostname).
				append(":").
				append(dbPort).
				append("/").
				append(dbName);
		}
		if(DBMS_SQLSERVER.equals(dbType)) {
			sb.append("jdbc:sqlserver://").
				append(dbHostname).
				append(":").
				append(dbPort).
				append(";databaseName=").
				append(dbName);
		}
		return sb.toString();
	}
}
