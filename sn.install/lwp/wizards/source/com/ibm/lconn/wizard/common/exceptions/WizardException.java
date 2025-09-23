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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class WizardException extends Exception {

	private static final long serialVersionUID = -2817585878845884780L;

	private static final String RESOURCE_BUNDLE_NAME = "com.ibm.lconn.wizard.common.exceptions.messages";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
	
	private String errorMsg;
	private ErrorCode errorCode;

	private Object[] parameters;

	public WizardException(Exception e, ErrorCode errorCode, String errorMsg, Object... parameter) {
		super(e);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.parameters = parameter;
	}

	public WizardException(ErrorCode errorCode, String errorMsg, Object... parameter) {
		super();
		this.errorCode = errorCode;
		this.errorMsg= errorMsg;
		this.parameters = parameter;
	}

	public WizardException(Exception e, ErrorCode errorCode, String errorMsg) {
		this(errorCode, errorMsg, e, new Object[0]);
	}

	public WizardException(ErrorCode errorCode, String errorMsg) {
		this(errorCode, errorMsg, new Object[0]);
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}
	
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

	public Object[] getParameters() {
		return this.parameters;
	}

	public String getMessage() {
		String text = null; 
		try {
			text = RESOURCE_BUNDLE.getString(errorMsg);
			text = text.replace("'", "''");
		} catch (MissingResourceException e) {
			text = errorMsg;
		}
		if("".equals(text)) {
			text = errorMsg;
		}
		return MessageFormat.format(text, parameters);
	}
}
