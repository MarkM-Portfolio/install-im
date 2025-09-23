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

package com.ibm.lconn.wizard.cluster.backend;

import java.util.ResourceBundle;


/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class DataPoolForTest {
	public static ResourceBundle rc = ResourceBundle.getBundle("com.ibm.lconn.wizard.cluster.command.variableForTest");
	public static String getValue(String key) {
		return rc.getString(key);
	}

}
