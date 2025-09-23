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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class DbConnectionValidator extends AbstractValidator {
	public static final Logger logger = LogUtil.getLogger(DbConnectionValidator.class);
	private final static String DB_CONN_VALIDATOR_MAIN = "com.ibm.lconn.wizard.common.validator.DbConnectionValidatorMain";
	
	private static final int msgCount = 7;
	private static int count = 0;
	
	private String platform, jreInstallLoc, dbType, dbHostname, dbPort, dbName, dbUser, dbPassword;
	protected String getJrePath() {
		return eval(jreInstallLoc) + Constants.FS + "bin";
	}
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
	public int validate() {
		count++;
		
		String _dbType = eval(dbType);
		String _dbHostname = eval(dbHostname);
		String _dbPort = eval(dbPort);
		String _dbName = eval(dbName);
		String _dbUser = eval(dbUser);
		String _dbPassword = eval(dbPassword);
		String javaExecutable = getJrePath() + Constants.FS;
		if(Constants.OS_WINDOWS.equals(eval(platform))) {
			javaExecutable = javaExecutable + Constants.JAVA_EXECUTABLE_WIN;
		} else {
			javaExecutable = javaExecutable + Constants.JAVA_EXECUTABLE;
		}
		
		String dbUrl = DatabaseUtil.getDBUrl(
				_dbType, 
				_dbHostname, 
				_dbPort, 
				_dbName);
		logger.log(Level.INFO, "validator.info.db_url", dbUrl);
		String jdbcDriver = DatabaseUtil.getJDBCDriver(eval(dbType));
		logger.log(Level.INFO, "validator.info.jdbc_driver", jdbcDriver);

		
		switch(count) {
			case 0:
				break;
			case 2: 
				logError("no_jdbc_driver", jdbcDriver);
				break;
			case 3:
				logError("communication_error", _dbHostname, _dbPort);
				break;
			case 4:
				logError("db_not_exist", _dbName);
				break;
			case 5:
				logError("invalid_authentication");
				break;
			case 6:
				logError("cannot_connect_db", dbUrl);
				break;
			case 7:
				logError("cannot_validation_process");
				return -1;
		}
		return count > msgCount ? 0 : 1;
	}
	
	public DbConnectionValidator(String platform, String jreInstallLoc, String dbType, String dbHostname, String dbPort,
			String dbName, String dbUser, String dbPassword) {
		if("".equals(platform)) {
			throw new IllegalArgumentException("Platform type is not specified");
		}
		this.platform = platform;
		
		if("".equals(jreInstallLoc)) {
			throw new IllegalArgumentException("TDI installation location is not specified"); //$NON-NLS-1$
		}
		this.jreInstallLoc = jreInstallLoc;
		
		if("".equals(dbType)) {
			throw new IllegalArgumentException("Database type is not specified"); //$NON-NLS-1$
		}
		this.dbType = dbType;
		
		if("".equals(dbHostname)) {
			throw new IllegalArgumentException("Database host name is not specified"); //$NON-NLS-1$
		}
		this.dbHostname = dbHostname;
		
		if("".equals(dbPort)) {
			throw new IllegalArgumentException("Database port is not specified"); //$NON-NLS-1$
		}
		this.dbPort = dbPort;
		
		if("".equals(dbName)) {
			throw new IllegalArgumentException("Database name is not specified"); //$NON-NLS-1$
		}
		this.dbName = dbName;
		
		if("".equals(dbUser)) {
			throw new IllegalArgumentException("Database user is not specified"); //$NON-NLS-1$
		}
		this.dbUser = dbUser;
		
		if("".equals(dbPassword)) {
			throw new IllegalArgumentException("Database user password is not specified"); //$NON-NLS-1$
		}
		this.dbPassword = dbPassword;
		
		return;
	}

}
