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

import java.util.List;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public interface RuleLoader {
	public static final String RULES = "rules";
	public static final String VERSIONS = "versions";
	public static final String RULE_TYPE = "type";
	public static final String RULE_PARAM = "param";
	
	public static final String RULE_OR = "Or";
	public static final String RULE_AND = "And";
	public static final String RULE_NOT = "Not";
	
	public static final String RULE_REF = "$";
	
	public static final String RULE_TABLE_EXISTS = "TableExists";
	public static final String RULE_COLUMN_EXISTS = "ColumnExists";
	public static final String RULE_INDEX_EXISTS = "IndexExists";
	public static final String RULE_EQUAL = "Equals";
	
	public static final String ATTR_TABLE = "table";
	public static final String ATTR_COLUMN = "column";
	public static final String ATTR_INDEX = "index";
	public static final String ATTR_SCHEMA = "schema";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_EXPECTEDVALUE = "expectedValue";
	
	public void load(String ruleFile, String feature);
	
	public List<VersionCheckRules> getCheckRules();
}
