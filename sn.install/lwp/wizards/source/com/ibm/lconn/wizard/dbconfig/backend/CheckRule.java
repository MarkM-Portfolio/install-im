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

import java.util.List;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */

public abstract class CheckRule {

	public static final String VALUE_OPT_COLUMN_TYPE = "columnType";
	public static final String VALUE_OPT_COLUMN_LENGTH = "columnLength";
	public static final String VALUE_OPT_COLUMN_VALUE = "columnValue";
	public static final String VALUE_OPT_COLUMN_NAME = "columnName";

	private String uniqueID = generateUID();
	private static volatile int BASE_NUM = 1;
	protected String expectedValue = null;

	public CheckRule(){}

	private static String generateUID() {
		StringBuffer sb = new StringBuffer();
		long currentTime = BASE_NUM++;
		while(currentTime > 0 ) {
			int dividend = (int)(currentTime%34);
			char c = dividend< 10 ? (char)('0' + dividend) : (char)('A' + dividend -10);
			currentTime = currentTime/34;
			sb.append(c);
		}

		return sb.toString();
	}

	

	public static final class TableExists extends CheckRule {

		private String schema;
		private String table;

		public TableExists(String schema, String table) {
			this.schema = schema;
			this.table = table;
			this.expectedValue = table;
		}

		public String getSchema() {
			return schema;
		}

		public String getTable() {
			return table;
		}
	}

	
	public static final class ColumnExists extends CheckRule {
		private String schema;
		private String table;
		private String column;
		private String option;

		public ColumnExists(String schema, String table, String column, String option, String expectedValue) {
			this.schema = schema;
			this.table = table;
			this.column = column;
			this.option = option;
			this.expectedValue = expectedValue;
		}
		
		public ColumnExists(String schema, String table, String column) {
			this.schema = schema;
			this.table = table;
			this.column = column;
			this.option = VALUE_OPT_COLUMN_NAME;
			this.expectedValue = column;
		}

		public String getSchema() {
			return schema;
		}

		public String getTable() {
			return table;
		}

		public String getColumn() {
			return column;
		}

		public String getOption() {
			return option;
		}	
	}

	public static final class IndexExists extends CheckRule {
		private String schema;
		private String index;

		public IndexExists(String schema, String index) {
			this.schema = schema;
			this.index = index;
			this.expectedValue = index;
		}	

		public String getSchema() {
			return schema;
		}	

		public String getIndex() {
			return index;
		}
	}

	

	public static final class Equals extends CheckRule {
		private String schema;
		private String table;
		private String column;
		private String filter;
		public Equals(String schema, String table, String column, String expectedValue, String filter) {
			this.schema = schema;
			this.table = table;
			this.column = column;
			this.expectedValue = expectedValue;
			this.filter = filter;
		}
		
		public Equals(String schema, String table, String column, String expectedValue) {
			this(schema, table, column, expectedValue, "");
		}

		public String getSchema() {
			return schema;
		}

		public String getTable() {
			return table;
		}

		public String getColumn() {
			return column;
		}

		public String getExpectedValue() {
			return expectedValue;
		}
		
		public String getFilter() {
			return filter;
		}
	}	

	public static final class And extends CheckRule{
		private CheckRule left, right;
		
		public And (CheckRule left, CheckRule right) {
			this.left = left;
			this.right = right;
		}

		public CheckRule getLeft() {
			return left;
		}


		public CheckRule getRight() {
			return right;
		}

		

		public boolean analyzeOutput(List<String> output) {
			return left.analyzeOutput(output) && right.analyzeOutput(output);
		}

	}

	

	public static final class Or extends CheckRule {
		private CheckRule left, right;
		
		public Or (CheckRule left, CheckRule right) {
			this.left = left;
			this.right = right;
		}



		public CheckRule getLeft() {
			return left;
		}



		public CheckRule getRight() {
			return right;
		}	

		

		public boolean analyzeOutput(List<String> output) {
			return left.analyzeOutput(output) || right.analyzeOutput(output);
		}

	}

	

	public static final class Not extends CheckRule{
		private CheckRule rule;
		public Not(CheckRule rule) {
			this.rule = rule;
		}

		public CheckRule getRule() {
			return rule;
		}

		

		public boolean analyzeOutput(List<String> output) {
			return !rule.analyzeOutput(output);
		}
	}



	public String getUniqueID() {
		return uniqueID;
	}

	

	public boolean analyzeOutput(List<String> output) {
		Pattern p = Pattern.compile(uniqueID + RuleEvaluator.DECORATOR + "(.+)" + RuleEvaluator.DECORATOR);
		for (String line : output) {
			Matcher m = p.matcher(line);
			if(m.find()) {
				if(m.group(1).trim().equalsIgnoreCase(expectedValue)) {
					return true;
				}
			}
		}
		return false;
	}



	public String getExpectedValue() {
		return expectedValue;
	}



	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}
}
