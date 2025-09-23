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
package com.ibm.lconn.wizard.common.ui.data;

import java.util.Properties;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class WizardTableData extends WizardPageInputData{

	private String[] columns;
	private Properties props = new Properties();
	private int rowCount;
	
	public WizardTableData(String wizardId, String id) {
		super(wizardId, id);
	}
	
	public int indexOfColumn(String columnName){
		return Util.indexOf(this.columns, columnName);
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	
	public String getValue(String columnId){
		String property = props.getProperty(columnId);
		if(CommonHelper.isEmpty(property)) property = Constants.TEXT_EMPTY_STRING;
		return property;
	}
	
	public void setValue(String columnId, String value){
		props.setProperty(columnId, value);
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	/**
	 * @return the rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}
	
	public void setValue(int col, int row, String value){
		this.props.setProperty(columns[col]+"."+row, value);
	}
	
	public String getValue(int col, int row){
		 String val = this.props.getProperty(columns[col]+"."+row);
		 if(val==null) return Constants.TEXT_EMPTY_STRING;
		 return val;
	}
	
	public Properties getUserInput(String[]rowName, int colIndex){
		Properties resultProps = new Properties();
		for (int i = 0; i < rowName.length; i++) {
			String key = rowName[i];
			String value = getValue(columns[colIndex]+"."+i);
			resultProps.put(key, value);
		}
		return resultProps;
	}
	
	public String[] getColumnValues(int col){
		String[] columnValues = new String[rowCount];
		for (int i = 0; i < rowCount; i++) {
			columnValues[i] = getValue(col, i);
		}
		return columnValues;
	}
	
	public void clearColumn(int colIndex){
		for (int i = 0; i < rowCount; i++) {
			this.props.remove(columns[colIndex]+"."+i);
		}
	}
	
	public void clearValue(int col, int row){
		this.props.remove(columns[col]+"."+row);
	}
}
