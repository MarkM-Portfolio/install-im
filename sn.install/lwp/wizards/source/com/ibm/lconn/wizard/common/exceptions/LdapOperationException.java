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

public class LdapOperationException extends LdapException {

	/**
	 * @author Bai Jian Su (subaij@cn.ibm.com)
	 */
	private static final long serialVersionUID = 8090810807957447457L;

	public LdapOperationException(ErrorCode errorCode, String errorMsg) {
		super(errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

	public LdapOperationException(ErrorCode errorCode, String errorMsg,
			Object... parameter) {
		super(errorCode, errorMsg, parameter);
		// TODO Auto-generated constructor stub
	}

	public LdapOperationException(Exception e, ErrorCode errorCode,
			String errorMsg, Object... parameter) {
		super(e, errorCode, errorMsg, parameter);
		// TODO Auto-generated constructor stub
	}

	public LdapOperationException(Exception e, ErrorCode errorCode,
			String errorMsg) {
		super(e, errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

}
