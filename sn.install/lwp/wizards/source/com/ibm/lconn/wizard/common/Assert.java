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

public class Assert {
	/** Asserts that the given boolean is <code>true</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 *
	 * @param expression the outcode of the check
	 * @return <code>true</code> if the check passes (does not return
	 *    if the check fails)
	 */
	public static boolean isTrue(boolean expression) {
		return isTrue(expression, ""); //$NON-NLS-1$
	}

	/** Asserts that the given boolean is <code>true</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 * The given message is included in that exception, to aid debugging.
	 *
	 * @param expression the outcode of the check
	 * @param message the message to include in the exception
	 * @return <code>true</code> if the check passes (does not return
	 *    if the check fails)
	 */
	public static boolean isTrue(boolean expression, String message) {
		if (!expression)
			throw new AssertionFailedException("assertion failed: " + message); //$NON-NLS-1$
		return expression;
	}
	
}

class AssertionFailedException extends RuntimeException {

	/**
	 * All serializable objects should have a stable serialVersionUID
	 */
	private static final long serialVersionUID = 122345257303206333L;

	/** 
	 * Constructs a new exception with the given message.
	 * 
	 * @param detail the message
	 */
	public AssertionFailedException(String detail) {
		super(detail);
	}
}
