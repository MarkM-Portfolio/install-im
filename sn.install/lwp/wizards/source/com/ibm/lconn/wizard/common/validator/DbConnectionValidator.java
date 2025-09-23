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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class DbConnectionValidator extends AbstractValidator {
	public static final Logger logger = LogUtil.getLogger(DbConnectionValidator.class);
	private final static String DB_CONN_VALIDATOR_MAIN = "com.ibm.lconn.wizard.common.validator.DbConnectionValidatorMain";
	
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
		
		ProcessBuilder pb = new ProcessBuilder(javaExecutable, "-classpath", 
				Constants.WIZARD_JAR_LOC, DB_CONN_VALIDATOR_MAIN, 
				_dbType, 
				_dbHostname, 
				_dbPort, 
				_dbName, 
				_dbUser, 
				_dbPassword);
		
		// remove password and logging
		List<String> cmds = new ArrayList<String>();
		for(String cmd : pb.command()) {
			cmds.add(cmd);
		}
		cmds.set(cmds.size()-1, "******");
		logger.log(Level.FINER, "validator.finer.db_validation_process_command", cmds);
		
		String dbUrl = DatabaseUtil.getDBUrl(
				_dbType, 
				_dbHostname, 
				_dbPort, 
				_dbName);
		logger.log(Level.INFO, "validator.info.db_url", dbUrl);
		String jdbcDriver = DatabaseUtil.getJDBCDriver(eval(dbType));
		logger.log(Level.INFO, "validator.info.jdbc_driver", jdbcDriver);
		Process p = null;
		
		try {
			p = pb.start();
			p.waitFor();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "dbpopulation.severe.db_validation_process_fail", e);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "dbpopulation.severe.db_validation_process_fail", e);
		}
		
		if(p==null) {
			logError("cannot_validation_process");
			return -1;
		}
		
		logger.log(Level.FINER, "validator.finer.process_exit_value", new Integer(p.exitValue()));
		
		int i = p.exitValue();
		String os = System.getProperty("os.name");
		os = os.toLowerCase();
		
		if (((os.indexOf("win") != -1)&&os.indexOf("2016") != -1)
				&&(Constants.DB_SQLSERVER.equals(eval(dbType)))
				&&(i==Constants.ERROR_COMMUNICATION_ERROR)){
			i=Constants.ERROR_COMMUNICATION_ERROR_TDI;
		}	

		switch(i) {
			case 0:
				break;
			case Constants.ERROR_NO_JDBC_DRIVER: 
				logError("no_jdbc_driver", jdbcDriver);
				break;
			case Constants.ERROR_COMMUNICATION_ERROR_TDI:
				logError("communication_error", _dbHostname, _dbPort+ ". "  + Messages.getString("validator.communication_error.message_tdi"));
				break;
			case Constants.ERROR_COMMUNICATION_ERROR:
				logError("communication_error", _dbHostname, _dbPort);
				break;
			case Constants.ERROR_DB_NOT_EXIST:
				logError("db_not_exist", _dbName);
				break;
			case Constants.ERROR_INVALID_AUTHENTICATION:
				logError("invalid_authentication");
				break;
			case Constants.ERROR_UNKNOWN:
				logError("cannot_connect_db", dbUrl);
				break;
			default:
				logError("cannot_validation_process");
				return -1;
		}
		return p.exitValue();
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
