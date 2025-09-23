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

public class LCTableEntry{
	private Properties props = new Properties();
	
	public String getProperty(String key){
		return props.getProperty(key);
	}
	
	public void setProperty(String key, String value){
		props.setProperty(key, value);
	}
}
