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
package com.ibm.lconn.wizard.dbconfig;

import java.util.ResourceBundle;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class DB2ConfigProp {
	private static final ResourceBundle resource = ResourceBundle
			.getBundle("com.ibm.lconn.wizard.dbconfig.properties.config");
	
	public static String getProperty(String key) {
		return resource.getString(key);
	}

}
