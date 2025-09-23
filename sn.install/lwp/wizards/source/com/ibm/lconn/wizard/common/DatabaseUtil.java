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
package com.ibm.lconn.wizard.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtil {
	private DatabaseUtil() {}
		
	public static final String JDBC_DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver"; //$NON-NLS-1$
	public static final String JDBC_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver"; //$NON-NLS-1$
	public static final String JDBC_DRIVER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //$NON-NLS-1$
	
	private static Map<String,String> oracle11gUrlMap = Collections.synchronizedMap(new HashMap<String,String>());
	
	public static void putOracle11gUrlToMap(String oracle11gUrl)
	{
		oracle11gUrlMap.put("oracle11gUrl", oracle11gUrl);
	}

	public static String getJDBCDriver(String dbType) {
		if(Constants.DBMS_DB2.equals(dbType)) {
			return JDBC_DRIVER_DB2;
		}
		if(Constants.DBMS_ORACLE.equals(dbType)) {
			return JDBC_DRIVER_ORACLE;
		}
		if(Constants.DBMS_SQLSERVER.equals(dbType)) {
			return JDBC_DRIVER_SQLSERVER;
		}
		return null;
	}
	
	public static String getOracle11gDBUrl(String dbHostname, String dbPort, String dbName) {
		StringBuffer sb = new StringBuffer();
			sb.append("jdbc:oracle:thin:@").
				append(dbHostname).
				append(":").
				append(dbPort).
				append(":").
				append(dbName);
		return sb.toString();
	}
	
	public static String getDBUrl(String dbType, String dbHostname, String dbPort, String dbName) {
		StringBuffer sb = new StringBuffer();
		if(Constants.DBMS_DB2.equals(dbType)) {
			sb.append("jdbc:db2://").
				append(dbHostname).
				append(":").
				append(dbPort).
				append("/").
				append(dbName);
		}
		if(Constants.DBMS_ORACLE.equals(dbType)) {
			if(null!=oracle11gUrlMap.get("oracle11gUrl"))
			{
				return oracle11gUrlMap.get("oracle11gUrl");
			}
			sb.append("jdbc:oracle:thin:@//").
				append(dbHostname).
				append(":").
				append(dbPort).
				append("/").
				append(dbName);
		}
		if(Constants.DBMS_SQLSERVER.equals(dbType)) {
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
