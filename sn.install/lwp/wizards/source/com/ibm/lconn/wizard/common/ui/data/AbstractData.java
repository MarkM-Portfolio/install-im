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

public class AbstractData {
	public Properties props = new Properties();
	
	public void setValue(String key, String value){
		if(key==null) return ;
		if(value==null) value = Constants.TEXT_EMPTY_STRING;
		props.put(key, value);
	}
	
	public String getValue(String key){
		if(key==null) return null;
		return props.getProperty(key);
	}
}
