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
package com.ibm.lconn.wizard.common;

public class LdapUtil {
	public static final String getUrl(String hostName, String port) {
		StringBuffer sb = new StringBuffer();
		sb.append("ldap://").append(hostName).append(":").append(port);
		return sb.toString();
	}
}
