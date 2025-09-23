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

package com.ibm.lconn.wizard.common.validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;

public class DbConnectionValidatorMain {
	private static final String DB2_EXCEPTION_PATTERN = "\\[(\\d+)\\]";
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
			System.exit(Constants.ERROR_NO_JDBC_DRIVER);
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
					System.exit(processSQLException(dbType, e1));
				}
				catch (Exception e2) {
					System.exit(Constants.ERROR_UNKNOWN);
				}
				System.exit(0);
			}
			System.exit(processSQLException(dbType, e));
		} catch (Exception e) {
			if(Constants.DBMS_ORACLE.equals(dbType)) {
				String oracle11gUrl = DatabaseUtil.getOracle11gDBUrl(dbHostname, dbPort, dbName);
				try {
					conn = DriverManager.getConnection(oracle11gUrl, dbUser, dbPassword);
				} catch (SQLException e1) {
					System.exit(processSQLException(dbType, e1));
				}
				catch (Exception e2) {
					System.exit(Constants.ERROR_UNKNOWN);
				}
				System.exit(0);
			}
			System.exit(Constants.ERROR_UNKNOWN);
		}
		finally {
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
	
	private static int processSQLException(String dbType, SQLException e) {
		String sqlState = e.getSQLState();
		int errorCode = e.getErrorCode();
		
		if(Constants.DBMS_DB2.equals(dbType)) {
			String c1 = null;
			String c2 = null;
			Pattern p  = Pattern.compile(DB2_EXCEPTION_PATTERN);
			Matcher m = p.matcher(e.getMessage());
			if(m.find()) {
				c1 = m.group(1);
			}
			if(m.find()) {
				c2 = m.group(1);
			}
			if("2043".equals(c1) && "11550".equals(c2)) {
				return Constants.ERROR_COMMUNICATION_ERROR;
			} else if ("2057".equals(c1) && "11264".equals(c2)) {
				return Constants.ERROR_DB_NOT_EXIST;
			} else if ("2013".equals(c1) && "11249".equals(c2)) {
				return Constants.ERROR_INVALID_AUTHENTICATION;
			}
		}
		if(Constants.DBMS_ORACLE.equals(dbType)) {
			if(17002 == errorCode) {
				return Constants.ERROR_COMMUNICATION_ERROR;
			} else if(0 == errorCode) {
				return Constants.ERROR_DB_NOT_EXIST;
			} else if(1017 == errorCode || 17443 == errorCode) {
				return Constants.ERROR_INVALID_AUTHENTICATION;
			}
		}
		if(Constants.DBMS_SQLSERVER.equals(dbType)) {
			if("08S01".equals(sqlState) && 0 == errorCode) {
				return Constants.ERROR_COMMUNICATION_ERROR;
			} else if ("S0001".equals(sqlState) && 4060 == errorCode) {
				return Constants.ERROR_DB_NOT_EXIST;
			} else if ("S0001".equals(sqlState) && 18456 == errorCode) {
				return Constants.ERROR_INVALID_AUTHENTICATION;
			}
		}
		return Constants.ERROR_UNKNOWN;
	}
}
