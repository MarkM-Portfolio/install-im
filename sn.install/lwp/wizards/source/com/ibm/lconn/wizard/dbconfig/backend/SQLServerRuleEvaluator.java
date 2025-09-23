/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.dbconfig.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.ColumnExists;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.Equals;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.IndexExists;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.TableExists;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class SQLServerRuleEvaluator extends RuleEvaluator {
	public static final Logger logger = LogUtil.getLogger(SQLServerRuleEvaluator.class);
	
	private static final String[] EXECUTABLE_FOLDER_2005 = { "90", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2008 = { "100", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2012 = { "110", "Tools", "Binn" };
	private static final String[] EXECUTABLE_FOLDER_2016 = { "Client SDK","ODBC","130", "Tools", "Binn" };
	private static final String SQLSERVER_EXECUTABLE = "SQLCMD.EXE";
	protected SQLServerRuleEvaluator( String installLoc, String instance, String dbVersion, String feature) {
		super(installLoc, instance, dbVersion, feature);
	}

	@Override
	protected String getSQL(IndexExists rule) {
		StringBuffer sql = new StringBuffer();
		MessageFormat mf  = new MessageFormat(properties.getString("sqlserver.indexExists.sql"));
		mf.format(new String[] {
				toDatabaseString(rule.getUniqueID()),
				toDatabaseString(DECORATOR), 
				toDatabaseString(rule.getSchema()), 
				toDatabaseString(rule.getIndex())},
				sql, null);
		return sql.toString();
	}

	@Override
	protected String getSQL(ColumnExists rule) {
		StringBuffer sql = new StringBuffer();
		MessageFormat mf  = new MessageFormat(properties.getString("sqlserver.columnExists.sql"));
		
		String colTabCol = rule.getColumn();
		
		if(CheckRule.VALUE_OPT_COLUMN_LENGTH.equals(rule.getOption())){
			colTabCol = "char(c.max_length)";
		} else if (CheckRule.VALUE_OPT_COLUMN_NAME.equals(rule.getOption())) {
			colTabCol = "c.name";
		} else if (CheckRule.VALUE_OPT_COLUMN_TYPE.equals(rule.getOption())) {
			colTabCol = "st.name";
		} 
		
		mf.format(new String[] {
				toDatabaseString(rule.getUniqueID()),
				toDatabaseString(DECORATOR), 
				toDatabaseString(rule.getSchema()), 
				toDatabaseString(rule.getTable()),
				toDatabaseString(rule.getColumn()),
				colTabCol},
				sql, null);
		return sql.toString();
	}


	@Override
	protected String getSQL(TableExists rule) {
		StringBuffer sql = new StringBuffer();
		MessageFormat mf  = new MessageFormat(properties.getString("sqlserver.tableExists.sql"));
		mf.format(new String[] {
				toDatabaseString(rule.getUniqueID()),
				toDatabaseString(DECORATOR), 
				toDatabaseString(rule.getSchema()), 
				toDatabaseString(rule.getTable())},
				sql, null);
		return sql.toString();
	}

	private List<String> getExecutable() {
		List<String> executable = new ArrayList<String>();
		StringBuffer path = new StringBuffer().append(installLoc).append(Constants.FS);
		if (this.dbVersion.startsWith("13") || this.dbVersion.startsWith("14")){
			for (int i = 0; i < EXECUTABLE_FOLDER_2016.length; i++) {
				path.append(EXECUTABLE_FOLDER_2016[i]).append(Constants.FS);
			}
		}else if(this.dbVersion.startsWith("10")){
			for (int i = 0; i < EXECUTABLE_FOLDER_2008.length; i++) {
				path.append(EXECUTABLE_FOLDER_2008[i]).append(Constants.FS);
			}
		}else{
			for (int i = 0; i < EXECUTABLE_FOLDER_2012.length; i++) {
				path.append(EXECUTABLE_FOLDER_2012[i]).append(Constants.FS);
			}
		}
		path.append(SQLSERVER_EXECUTABLE);
		executable.add(path.toString());
		return executable;
	}

	protected String createSQLFile(List<String> sql) {
		// create a sql file for DB2 to call
		File tmp = getTempSQLFile(Constants.DB_SQLSERVER);
		PrintStream ps;
		try {
			ps = new PrintStream(new FileOutputStream(tmp));
			ps.println("USE " + CommonHelper.getFeatureDatabase(Constants.DB_SQLSERVER, feature));
			for (String str : sql) {
				ps.println(str);
				ps.println("GO");
				ps.println("USE " + CommonHelper.getFeatureDatabase(Constants.DB_SQLSERVER, feature));
			}
			ps.println("GO");
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tmp.getAbsolutePath();
	}

	protected List<String> executeSQL(String sqlFile) {
		List<String> output = new ArrayList<String>();
		List<String> executable = getExecutable();

		executable.add("-d");
		executable.add("master");
		if (null != instance && !"".equals(instance.trim())) {
			// always pass -S param no mater what version is used, if default instance, value is .\, if not default instance, value is .\instancename
			executable.add("-S");
			if ("\\".equals(instance.trim())) {		// default instance	
				executable.add(".\\");
			}
			else {
				executable.add(".\\"+instance);
			}
		}
		
		executable.add("-i");
		executable.add(sqlFile);
		ProcessBuilder pb = new ProcessBuilder(executable);
		pb.redirectErrorStream(true);
		try {
			Process p = pb.start();

			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(p
					.getInputStream()));
			
			String l = null;
			while((l=lnr.readLine()) != null) {
				if(!"".equals(l.trim())) {
					output.add(l);
					logger.log(Level.FINEST, "dbconfig.finest.query_output", l);
				}
			}
			
			lnr.close();			
		} catch (IOException e) {
			logger.log(Level.SEVERE, "dbconfig.severe.fail_query_version", e);
		}
		return output;
	}

	@Override
	protected String getSQL(Equals equals) {
		StringBuffer sql = new StringBuffer();
		MessageFormat mf  = new MessageFormat(properties.getString("sqlserver.col_val_equals.sql"));
		mf.format(new String[] {
				toDatabaseString(equals.getUniqueID()),
				toDatabaseString(DECORATOR), 
				equals.getSchema(), 
				equals.getTable(),
				equals.getColumn(),
				equals.getFilter()},
				sql, null);
		return sql.toString();
	}
}
