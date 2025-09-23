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

public class LdapCertificateException extends LdapException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2817850354469630592L;

	public LdapCertificateException(ErrorCode errorCode, String errorMsg) {
		super(errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

	public LdapCertificateException(ErrorCode errorCode, String errorMsg,
			Object... parameter) {
		super(errorCode, errorMsg, parameter);
		// TODO Auto-generated constructor stub
	}

	public LdapCertificateException(Exception e, ErrorCode errorCode,
			String errorMsg, Object... parameter) {
		super(e, errorCode, errorMsg, parameter);
		// TODO Auto-generated constructor stub
	}

	public LdapCertificateException(Exception e, ErrorCode errorCode,
			String errorMsg) {
		super(e, errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

}
