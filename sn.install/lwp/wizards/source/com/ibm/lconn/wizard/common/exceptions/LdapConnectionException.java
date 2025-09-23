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
 * @author Bai Jian Su (subaij@cn.ibm.com)
 * 
 */
public class LdapConnectionException extends LdapException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8638129280476508025L;

	/**
	 * @param errorCode
	 * @param errorMsg
	 */
	public LdapConnectionException(ErrorCode errorCode, String errorMsg) {
		super(errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param errorMsg
	 * @param parameter
	 */
	public LdapConnectionException(ErrorCode errorCode, String errorMsg,
			Object... parameter) {
		super(errorCode, errorMsg, parameter);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param e
	 * @param errorCode
	 * @param errorMsg
	 * @param parameter
	 */
	public LdapConnectionException(Exception e, ErrorCode errorCode,
			String errorMsg, Object... parameter) {
		super(e, errorCode, errorMsg, parameter);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param e
	 * @param errorCode
	 * @param errorMsg
	 */
	public LdapConnectionException(Exception e, ErrorCode errorCode,
			String errorMsg) {
		super(e, errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

}
