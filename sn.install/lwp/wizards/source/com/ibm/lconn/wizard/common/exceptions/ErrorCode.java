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
package com.ibm.lconn.wizard.common.exceptions;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public enum ErrorCode {
	unknown, ERROR_LDAP_OPEN, ERROR_LDAP_CLOSE, ERROR_LDAP_OPERATION, ERROR_LDAP_OPERATION_GETBASEDN, ERROR_LDAP_OPERATION_GETVENDORNAME, ERROR_LDAP_OPERATION_GETOBJECTS, ERROR_LDAP_OPERATION_GETOBJECTCLASSES, ERROR_LDAP_SSL_NOTSUPPORT_IBMX509, ERROR_LDAP_CERTIFICATE_FAILED, ERROR_LDAP_OPERATION_GETOBJECTCLASSDEFINATION;

}
