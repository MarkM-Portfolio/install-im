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
package com.ibm.lconn.wizard.dbconfig.backend;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;
import com.ibm.lconn.wizard.common.DefaultValue;
import com.ibm.lconn.wizard.common.command.CommandExecuter;
import com.ibm.lconn.wizard.common.command.CommandLogger;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.tdipopulate.backend.JDBCConnectionInfo;

public abstract class Task {
	public static final int JAVA_ERROR = -2;
	public static final int EXCEPTION = -1;

	public static final int JAVA_APPLICATION = 1;
	public static final int COMMAND_SCRIPT = 2;

	public static final String FS = System.getProperty("file.separator");
	public static final String LOGROOT = "dbWizard";

	public static final int TYPE_DB2 = 1;
	public static final int TYPE_ORACLE = 2;
	public static final int TYPE_SQLSERVER = 3;

	public static final String $DBNAME = "${DBNAME}";
	public static final String $SIDDBNAME = "-SID=${DBNAME}";
	public static final String $DBHOST = "${DBHOST}";
	public static final String $PORT = "${PORT}";
	public static final String $USERNAME = "${USERNAME}";
	public static final String $PASSWORD = "${PASSWORD}";
	public static final String $PROFILEUSERNAME = "${SRCUSERNAME}";
	public static final String $PROFLEPASSWORD = "${SRCPASSWORD}";
	public static final String $DBURL = "${DBURL}";
	public static final String $PROFILEDBURL = "${PROFILES_URL}";
	public static final String $STORYLIFETIMEINDAYS = "${STORYLIFETIMEINDAYS}";
	public static final String $DBTYPE = "${DBTYPE}";
	public static final String $CONTENTSTORE = "${CONTENTSTORE}";
	public static final String JAVA_CMD = "$java";

	protected final static ResourceBundle resource = ResourceBundle.getBundle("com.ibm.lconn.wizard.dbconfig.backend.cmdContent");

	protected String scriptPath;
	protected String dbType;
	protected String dbVersion;
	protected String dbDirRoot;
	protected String dbInstanceName;
	protected String dbaPasswordValue;
	protected String PDBNameValue;
	protected String dbUserPassword;
	protected String dbaUserNameValue;
	protected boolean isRunAsSYSDBA;
	protected String dbFilePath;
	protected String osName;
	protected String featureName;
	protected String featureVersion;
	protected ArrayList<String> command;
	protected String workDirectory;
	protected Map<String, String> evironment = new HashMap<String, String>();
	protected boolean isLocal = true;
	protected String operationType;
	protected String script;

	protected int exitValue = 0;
	protected JDBCConnectionInfo jdbcConnInfo;
	protected JDBCConnectionInfo profileConnInfo;
	protected int applicationType;

	protected Properties props = new Properties();

	public static File logFile = null;
	
	public Map<String, String> getEvironment() {
		return evironment;
	}

	public void setEvironment(Map<String, String> evironment) {
		this.evironment = evironment;
	}

	public static Task loadTask(int taskType) {
		Task task;
		switch (taskType) {
		case TYPE_DB2:
			task = new DB2CommandTask();
			break;
		case TYPE_ORACLE:
			task = new OracleCommandTask();
			break;
		case TYPE_SQLSERVER:
			task = new SQLServerCommandTask();
			break;
		default:
			task = null;
			break;
		}

		File logDir = new File(Constants.LOG_ROOT + FS + LOGROOT);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		return task;
	}

	public CommandResultInfo run() {
		CommandLogger.resetTempLog();
		CommandLogger.setLogFile(logFile);

		this.setScriptPath();

		String[] scriptArr = genScriptPath();

		int count = scriptArr.length;
		// String tempPath = this.relativePath;

		this.exitValue = 0;
		for (int i = 0; i < count; i++) {
			// this.relativePath = tempPath + scriptArr[i];
			this.script = scriptArr[i].trim();

			this.combineCMD();

			int eValue = CommandExecuter.run(this.command, this.workDirectory, this.evironment, logFile, this.dbType);

			if (this.applicationType == JAVA_APPLICATION && eValue > 0) {
				this.exitValue = JAVA_ERROR;
			}

			if (eValue < 0)
				this.exitValue = eValue;
			else
				this.exitValue = (eValue > this.exitValue) ? eValue : this.exitValue;
		}
		return getResult();
	}
	
	public CommandResultInfo run(List<String> command) {
		CommandLogger.resetTempLog();
		CommandLogger.setLogFile(logFile);
		
		this.exitValue = 0;
		int eValue = CommandExecuter.run(command, this.workDirectory,this.evironment, logFile, this.dbType);

		for(String commandStr : command){
			if("-classpath".equals(commandStr)){
				this.applicationType = JAVA_APPLICATION;
				break;
			}
		}
		
		if (this.applicationType == JAVA_APPLICATION && eValue > 0) {
			this.exitValue = JAVA_ERROR;
			return getResult();
		}

		if (eValue < 0)
			this.exitValue = eValue;
		else
			this.exitValue = (eValue > this.exitValue) ? eValue : this.exitValue;
		
		return getResult();
	}
	
	public List<List<String>> getCommands(){
		List<List<String>> list = new ArrayList<List<String>>();
//		StringBuilder sb = new StringBuilder();
		this.setScriptPath();
		String[] scriptArr = genScriptPath();
		for (int i = 0; i <scriptArr.length; i++) {
			// this.relativePath = tempPath + scriptArr[i];
			this.script = scriptArr[i].trim();
			this.combineCMD();
			list.add(command);
//			sb = new StringBuilder("");
//			for(String cmd : command){
//				sb.append(cmd).append(" ");
//			}
//			sb.deleteCharAt(sb.length()-1);
//			list.add(sb.toString());
		}
		
		return list;
	}
	
	public List<String> getSQLScripts(){
		List<String> list = new ArrayList<String>();
		this.setScriptPath();
		String[] scriptArr = genScriptPath();
		for (int i = 0; i < scriptArr.length; i++) {
			// this.relativePath = tempPath + scriptArr[i];
			if(scriptArr[i].trim().startsWith("@")){
				scriptArr[i] = scriptArr[i].trim().substring(1);
			}
			list.add(scriptArr[i]);
		}
		
		return list;
	}

	private String[] genScriptPath() {
		String[] scriptArr;
		// upgrade, create or delete
		if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.operationType)) {
			boolean optional = false;
			/*
			 * if (Constants.FEATURE_COMMUNITIES.equals(featureName)) { if
			 * (CommonHelper .isTrue(props
			 * .getProperty(Constants.INPUT_DB_ENABLE_COMMUNITIES_OPTIONAL_TASK
			 * ))) { optional = true; } }
			 */
			if(featureName.indexOf("theme") != -1){
				featureVersion = "2.5.0";
			}
			scriptArr = new UpgradePath(this.featureName, this.featureVersion, this.dbType, this.dbVersion).getUpgradeActions(Constants.VERSION_TOP, optional);
		} else {
			String scripts = null;

			try {
				scripts = resource.getString(this.featureName + "." + this.dbType + this.dbVersion + "." + this.operationType);
			} catch (RuntimeException e) {
				// ignore;
			}
			if (scripts == null) {
				try {
					scripts = resource.getString(this.featureName + "." + this.dbType + "." + this.operationType);
				} catch (RuntimeException e) {
					// ignore;
				}
			}

			if (scripts == null) {
				scripts = resource.getString(this.featureName + "." + this.operationType);
			}
			scriptArr = scripts.split(",");
		}

		return scriptArr;
	}

	protected abstract void combineCMD();

	protected abstract CommandResultInfo getResult();

	protected List<String> combineJdbcCmd(String[] cmdArgs, String feature) {
		String className = "";
		String extraJars = "";
		List<String> output = new ArrayList<String>();
		command = new ArrayList<String>();
		if (feature.equals(Constants.FEATURE_HOMEPAGE)){
			className = cmdArgs[4];
			extraJars = cmdArgs[3];
			command.add(DefaultValue.getDefaultJavaExecutablePath());
			command.add(cmdArgs[2]);
			command.add(cmdArgs[1]);
			command.add("-classpath");
		}else{
			className = cmdArgs[2];
			extraJars = cmdArgs[1];
			command.add(DefaultValue.getDefaultJavaExecutablePath());
			command.add("-classpath");
		}

		String classpath = jdbcConnInfo.getJdbcLibPath();
		String[] jars = extraJars.split(":");
		for (String jar : jars) {
			classpath = classpath + Constants.PATH_SEPARATOR + jar;
		}
		command.add(classpath);
		command.add(className);

		output.addAll(command);
		int index = 0;
		if (feature.equals(Constants.FEATURE_HOMEPAGE)){
			index = 5;
		}else{
			index = 3;
		}
		
		for (int i = index; i < cmdArgs.length; i++) {
			command.add(getSymbolValue(cmdArgs[i]));
			if ($PASSWORD.equals(cmdArgs[i])) {
				output.add("******");
			} else {
				output.add(getSymbolValue(cmdArgs[i]));
			}
		}

		return output;
	}

	private String getSymbolValue(String symbol) {
		if ($DBNAME.equals(symbol)) {
			if (Constants.DB_ORACLE.equals(jdbcConnInfo.getDbType())) {
				return dbInstanceName;
			} else {
				return Constants.featureDBMapping.get(dbType).get(featureName);
			}
		}
		if ($SIDDBNAME.equals(symbol) && Constants.DB_ORACLE.equals(jdbcConnInfo.getDbType())) {	
			return "-SID=" + dbInstanceName;	
		}
		if ($DBTYPE.equals(symbol)) {
			return jdbcConnInfo.getDbType();
		}
		if ($DBHOST.equals(symbol)) {
			return jdbcConnInfo.getHostName();
		}
		if ($PORT.equals(symbol)) {
			return jdbcConnInfo.getPort();
		}
		if ($USERNAME.equals(symbol)) {
			return jdbcConnInfo.getUsername();
		}
		if ($PROFILEUSERNAME.equals(symbol)){
			return getProfileJDBCConnInfo().getUsername();
		}
		if($PROFLEPASSWORD.equals(symbol)){
			return getProfileJDBCConnInfo().getPassword();
		}
		if ($PASSWORD.equals(symbol)) {
			return jdbcConnInfo.getPassword();
		}
		if ($DBURL.equals(symbol)) {
			String dbName = null;
			if (Constants.DB_ORACLE.equals(jdbcConnInfo.getDbType())) {
				dbName = dbInstanceName;
			} else {
				dbName = Constants.featureDBMapping.get(dbType).get(featureName);
			}

			String dburl = DatabaseUtil.getDBUrl(jdbcConnInfo.getDbType(), jdbcConnInfo.getHostName(), jdbcConnInfo.getPort(), dbName);
			
//			if (Constants.OPERATION_TYPE_UPGRADE_DB.equals(this.operationType) && Constants.FEATURE_COMMUNITIES.equals((this.featureName)) 
//					&& script.indexOf("forum.migrate")!=-1){
//				if(Constants.DB_SQLSERVER.equals(jdbcConnInfo.getDbType())){
//					dburl = dburl.replace(dburl.substring(dburl.indexOf("databaseName=")), "databaseName=<dbAlias>");
//				}
//				if(Constants.DB_DB2.equals(jdbcConnInfo.getDbType())){
//					dburl = dburl.replace(dburl.substring(dburl.lastIndexOf("/")), "/<dbAlias>");
//				}
//			}
			
			return dburl;
		}
		if ($PROFILEDBURL.equals(symbol)) {
			String dbName = null;
			if (Constants.DB_ORACLE.equals(jdbcConnInfo.getDbType())) {
				dbName = dbInstanceName;
			} else {
				dbName = Constants.featureDBMapping.get(dbType).get(Constants.FEATURE_PROFILES);
			}

			String dburl = DatabaseUtil.getDBUrl(getProfileJDBCConnInfo().getDbType(), getProfileJDBCConnInfo().getHostName(), getProfileJDBCConnInfo().getPort(), dbName);
			
			return dburl;
		}
		if ($STORYLIFETIMEINDAYS.equals(symbol)){
			return System.getProperty("story_life_time_in_days");
		}
		if ($CONTENTSTORE.equals(symbol)) {
			return jdbcConnInfo.getContentStore(featureName);
		}

		return symbol;
	}

	public static String openLog() {
		logFile = new File(Constants.LOG_ROOT + FS + LOGROOT + FS + "dbConfig" + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".log");

		return logFile.getAbsolutePath();
	}
	
	public static String openLog(String featureName, String sqlFileName) {
		logFile = new File(Constants.LOG_ROOT + FS + LOGROOT + FS + "dbConfig" + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + featureName + "_" + sqlFileName +  ".log");

		return logFile.getAbsolutePath();
	}

	protected void setScriptPath() {
		String arch = System.getProperty("os.arch"); 
		arch = arch.toLowerCase();
		if(arch.indexOf("s390") != -1) {
			this.scriptPath = Constants.DB_SCRIPT_S390_PATH + Constants.FS + this.featureName + Constants.FS + this.dbType + Constants.FS;
		}else{
			this.scriptPath = Constants.DB_SCRIPT_PATH + Constants.FS + this.featureName + Constants.FS + this.dbType + Constants.FS;
		}
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public void setDbDirRoot(String dbDirRoot) {
		this.dbDirRoot = dbDirRoot;
	}

	public void setDbInstanceName(String dbInstanceName) {
		this.dbInstanceName = dbInstanceName;
	}

	
//#Oracle12C_PDB_disable#	public void setPDBNameValue(String PDBNameValue) {
//#Oracle12C_PDB_disable#		this.PDBNameValue = PDBNameValue;
	//#Oracle12C_PDB_disable#	}	
	
//#Oracle12C_PDB_disable#	public void setDbaPasswordValue(String dbaPasswordValue) {
//#Oracle12C_PDB_disable#		this.dbaPasswordValue = dbaPasswordValue;
//#Oracle12C_PDB_disable#	}
	
//#Oracle12C_PDB_disable#	public void setDbaUserNameValue(String dbaUserNameValue) {
//#Oracle12C_PDB_disable#		this.dbaUserNameValue = dbaUserNameValue;
//#Oracle12C_PDB_disable#	}	
	
//#Oracle12C_PDB_disable#	public void isRunAsSYSDBA(boolean isRunAsSYSDBA) {
//#Oracle12C_PDB_disable#		this.isRunAsSYSDBA = isRunAsSYSDBA;
//#Oracle12C_PDB_disable#	}

	public void setDbUserPassword(String dbUserPassword) {
		this.dbUserPassword = dbUserPassword;
	}

	public void setDbFilePath(String dbFilePath) {
		this.dbFilePath = dbFilePath;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public void setFeatureVersion(String version) {		
		this.featureVersion = version;
	}

	public JDBCConnectionInfo getJdbcConnInfo() {
		return jdbcConnInfo;
	}

	public void setJdbcConnInfo(JDBCConnectionInfo jdbcConnInfo) {
		this.jdbcConnInfo = jdbcConnInfo;
	}
	
	

	public void setProperties(Properties props) {
		this.props = props;
	}

	public void setDbVersion(String version) {
	/*	if (version != null) {
			version = version.split(" ")[1];
			version = version.replaceFirst("\\.", "");
			version = version.split("\\.")[0];
		}*/
		this.dbVersion = version;
	}

	public void setProfileJdbcConnInfo(JDBCConnectionInfo profileConnInfo) {
		this.profileConnInfo = profileConnInfo;
		
	}
	
	public JDBCConnectionInfo getProfileJDBCConnInfo(){
		return this.profileConnInfo;
	}
}
