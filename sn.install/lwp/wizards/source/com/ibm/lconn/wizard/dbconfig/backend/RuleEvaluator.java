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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.ColumnExists;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.Equals;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.IndexExists;
import com.ibm.lconn.wizard.dbconfig.backend.CheckRule.TableExists;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public abstract class RuleEvaluator {
	private static final Logger logger = LogUtil.getLogger(RuleEvaluator.class);
	
	public static final String DECORATOR = "&&";
	public static final String STRING_TYPE = "VARCHAR";
	protected static PropertyResourceBundle properties = 
		(PropertyResourceBundle)ResourceBundle.getBundle("com.ibm.lconn.wizard.dbconfig.backend.cmdContent");	
	
	protected String installLoc;
	protected String instance;
	protected String feature;
	protected String dbVersion;
//#Oracle12C_PDB_disable#	protected String PDBNameValue=null;
//#Oracle12C_PDB_disable#	protected String dbaPasswordValue=null;
	
	protected RuleEvaluator(String installLoc, String instance,String dbVersion, String feature) {
		this.installLoc = installLoc;
		this.instance = instance;
		this.feature = feature;
		this.dbVersion = dbVersion;
	}
	
//#Oracle12C_PDB_disable#	protected RuleEvaluator(String installLoc, String instance,String PDBNameValue,String dbaPasswordValue,String dbVersion, String feature) {
//#Oracle12C_PDB_disable#		this.installLoc = installLoc;
//#Oracle12C_PDB_disable#		this.instance = instance;
//#Oracle12C_PDB_disable#		this.PDBNameValue = PDBNameValue;
//#Oracle12C_PDB_disable#		this.dbaPasswordValue = dbaPasswordValue;
//#Oracle12C_PDB_disable#		this.feature = feature;
//#Oracle12C_PDB_disable#		this.dbVersion = dbVersion;
//#Oracle12C_PDB_disable#	}

//#Oracle12C_PDB_disable#	public static RuleEvaluator getInstance(String dbType, String installLoc, String instance, String PDBNameValue,String dbaPasswordValue, String dbVersion, String feature) {
	public static RuleEvaluator getInstance(String dbType, String installLoc, String instance,String dbVersion, String feature) {
		if(Constants.DB_DB2.equals(dbType)) {
			return new DB2RuleEvaluator(installLoc, instance, dbVersion, feature);
		} else if (Constants.DB_ORACLE.equals(dbType)) {
//#Oracle12C_PDB_disable#			return new OracleRuleEvaluator(installLoc, instance,PDBNameValue,dbaPasswordValue,dbVersion, feature);
			return new OracleRuleEvaluator(installLoc, instance,dbVersion, feature);
		} else if (Constants.DB_SQLSERVER.equals(dbType)) {
			return new SQLServerRuleEvaluator(installLoc, instance, dbVersion, feature);
		}
		throw new IllegalArgumentException("Invalid database type");
	}
	
	public boolean evaluate(CheckRule rule) {
		// compose SQL file
		List<String> sql = new ArrayList<String>();
		addSQL(sql, rule);
		String filename = createSQLFile(sql);
		// excute
		List<String> output = executeSQL(filename);
		
		return rule.analyzeOutput(output);
	}
	
	private final void addSQL(List<String> sb, CheckRule rule) {
		if(rule instanceof CheckRule.And) {
			addSQL(sb, ((CheckRule.And)rule).getLeft());
			addSQL(sb, ((CheckRule.And)rule).getRight());
		} else if (rule instanceof CheckRule.Or) {
			addSQL(sb, ((CheckRule.Or)rule).getLeft());
			addSQL(sb, ((CheckRule.Or)rule).getRight());
		} else if (rule instanceof CheckRule.Not) {
			addSQL(sb, ((CheckRule.Not)rule).getRule());
		} else if (rule instanceof CheckRule.TableExists) {
			sb.add(getSQL((CheckRule.TableExists)rule));
		} else if (rule instanceof CheckRule.ColumnExists) {
			sb.add(getSQL((CheckRule.ColumnExists)rule));
		} else if (rule instanceof CheckRule.IndexExists) {
			sb.add(getSQL((CheckRule.IndexExists)rule));
		} else if (rule instanceof CheckRule.Equals) {
			sb.add(getSQL((CheckRule.Equals)rule));
		}
	}
	
	protected final void addSQL(List<String> sb, List<CheckRule> rules) {
		for (CheckRule rule : rules) {
			addSQL(sb, rule);
		}
	}
	
	public  boolean[] evaluate(List<VersionCheckRules> vcrs) {
		// compose SQL file
		boolean[] results = new boolean[vcrs.size()];
		
		for (int i = 0; i < results.length; i++) {
			results[i] = true;
		}
		
		List<String> sql = new ArrayList<String>();
		for (VersionCheckRules vcr : vcrs) {
			for (CheckRule checkRule : vcr.getRules()) {
				addSQL(sql, checkRule);
			}			
		}
		// create SQL File
		String filename = createSQLFile(sql);
		// excute SQL
		List<String> output = executeSQL(filename);
		
		for(int i=0; i<vcrs.size(); i++) {
			VersionCheckRules vcr = vcrs.get(i);
			boolean result = true;
			for (CheckRule checkRule : vcr.getRules()) {
				result = result && checkRule.analyzeOutput(output);
			}
			vcr.setMatched(result);
		}
		
		return results;
	}
	
	protected abstract String getSQL(Equals equals);

	protected abstract String getSQL(IndexExists exists);

	protected abstract String getSQL(ColumnExists exists);

	protected abstract String getSQL(TableExists exists);

	protected boolean eval(CheckRule.And rule) {
		return evaluate(rule.getLeft()) && evaluate(rule.getRight());
	}
	
	protected boolean eval(CheckRule.Or rule) {
		return evaluate(rule.getLeft()) && evaluate(rule.getRight());
	}
	
	protected boolean eval(CheckRule.Not rule) {
		return !evaluate(rule.getRule());
	}
	
	public static String toDatabaseString(String value) {
		String rt = value.replaceAll("'", "''");
		return rt = "'" + rt + "'";
	}
	
	protected static boolean analyzeOutput(List<String> output, String uid, String expectedValue) {
		Pattern p = Pattern.compile(uid + DECORATOR + "(.+)" + DECORATOR);
		for (String line : output) {
			Matcher m = p.matcher(line);
			if(m.find()) {
				if(m.group(1).equals(expectedValue)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected abstract String createSQLFile(List<String> sql);
	
	protected abstract List<String> executeSQL(String sqlFile) ;
	
	protected File getTempSQLFile(String prefix) {
		File temp = null;
		try {
			temp = File.createTempFile(prefix, ".sql");
			temp.deleteOnExit();
		} catch(IOException e) {
			logger.log(Level.SEVERE, "dbconfig.severe.cannot_create_temp_sql", e);
		}
		return temp;
	}
}
