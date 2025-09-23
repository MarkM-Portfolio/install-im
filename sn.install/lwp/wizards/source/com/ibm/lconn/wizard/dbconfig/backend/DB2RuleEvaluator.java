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
public class DB2RuleEvaluator extends RuleEvaluator {
	private static final Logger logger = LogUtil.getLogger(DB2RuleEvaluator.class);
	private static final String[] EXECUTABLE_FOLDER = { "bin" };
	private static final String DB2_WIN = "db2cmd.exe";
	private static final String DB2_LINUX = "db2";
	private static final String DB2_AIX = "db2";
	private static final String DB2INSTANCE = "DB2INSTANCE";

	protected DB2RuleEvaluator(String installLoc, String instance, String dbVersion, String feature) {
		super(installLoc, instance, dbVersion, feature);
	}


	@Override
	protected String getSQL(IndexExists rule) {
		StringBuffer sql = new StringBuffer();
		MessageFormat mf  = new MessageFormat(properties.getString("db2.indexExists.sql"));
		mf.format(new String[] {
				toDatabaseString(rule.getUniqueID()),
				toDatabaseString(DECORATOR), 
				toDatabaseString(rule.getSchema().toUpperCase()), 
				toDatabaseString(rule.getIndex().toUpperCase())},
				sql, null);
		return sql.toString();
	}

	@Override
	protected String getSQL(ColumnExists rule) {
		StringBuffer sql = new StringBuffer();
		MessageFormat mf  = new MessageFormat(properties.getString("db2.columnExists.sql"));
		
		String colTabCol = rule.getColumn();
		
		if(CheckRule.VALUE_OPT_COLUMN_LENGTH.equals(rule.getOption())){
			colTabCol = "LENGTH";
		} else if (CheckRule.VALUE_OPT_COLUMN_NAME.equals(rule.getOption())) {
			colTabCol = "COLNAME";
		} else if (CheckRule.VALUE_OPT_COLUMN_TYPE.equals(rule.getOption())) {
			colTabCol = "TYPENAME";
		} 
		
		mf.format(new String[] {
				toDatabaseString(rule.getUniqueID()),
				toDatabaseString(DECORATOR), 
				toDatabaseString(rule.getSchema().toUpperCase()), 
				toDatabaseString(rule.getTable().toUpperCase()),
				toDatabaseString(rule.getColumn().toUpperCase()),
				colTabCol},
				sql, null);
		return sql.toString();
	}


	@Override
	protected String getSQL(TableExists rule) {
		StringBuffer sql = new StringBuffer();
		MessageFormat mf  = new MessageFormat(properties.getString("db2.tableExists.sql"));
		mf.format(new String[] {
				toDatabaseString(rule.getUniqueID()),
				toDatabaseString(DECORATOR), 
				toDatabaseString(rule.getSchema().toUpperCase()), 
				toDatabaseString(rule.getTable().toUpperCase())},
				sql, null);
		return sql.toString();
	}

	private List<String> getExecutable() {
		List<String> executable = new ArrayList<String>();
		StringBuffer path = new StringBuffer().append(installLoc).append(
				Constants.FS);
		for (int i = 0; i < EXECUTABLE_FOLDER.length; i++) {
			path.append(EXECUTABLE_FOLDER[i]).append(Constants.FS);
		}

		String platform = CommonHelper.getPlatformType();
		if (Constants.OS_WINDOWS.equals(platform)) {
			path.append(DB2_WIN);
		} else if (Constants.OS_AIX.equals(platform)) {
			path.append(DB2_AIX);
		} else if (Constants.OS_LINUX_SUSE.equals(platform)) {
			path.append(DB2_LINUX);
		} else if (Constants.OS_LINUX_REDHAT.equals(platform)) {
			path.append(DB2_LINUX);
		}

		executable.add(path.toString());
		// special handling for windows
		if (Constants.OS_WINDOWS.equals(platform)) {
			executable.add("-i");
			executable.add("-w");
			executable.add("-c");
			executable.add("DB2");
		}
		return executable;
	}

	protected String createSQLFile(List<String> sql) {
		// create a sql file for DB2 to call
		File tmp = getTempSQLFile(Constants.DB_DB2);
		PrintStream ps;
		try {
			ps = new PrintStream(new FileOutputStream(tmp));
			ps.println("CONNECT TO " + CommonHelper.getFeatureDatabase(Constants.DB_DB2, feature) + ";");

			for (String str : sql) {
				ps.println(str + ";");
			}
			ps.println("DISCONNECT CURRENT;");
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tmp.getAbsolutePath();
	}

	protected List<String> executeSQL(String sqlFile) {
		List<String> output = new ArrayList<String>();
		List<String> executable = getExecutable();
		executable.add("-tvf");
		executable.add(sqlFile);
		ProcessBuilder pb = new ProcessBuilder(executable);
		pb.redirectErrorStream(true);
		
		if(instance != null && !"".equals(instance.trim())) {
			pb.environment().put(DB2INSTANCE, instance);
		}
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
		MessageFormat mf  = new MessageFormat(properties.getString("db2.col_val_equals.sql"));
		mf.format(new String[] {
				toDatabaseString(equals.getUniqueID()),
				toDatabaseString(DECORATOR), 
				equals.getSchema().toUpperCase(), 
				equals.getTable().toUpperCase(),
				equals.getColumn().toUpperCase(),
				equals.getFilter()},
				sql, null);
		return sql.toString();
	}

}
