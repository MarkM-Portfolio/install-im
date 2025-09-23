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

package com.ibm.lconn.wizard.cluster.validation;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public abstract class ValidatorImpl implements Validator {
	protected int executeCode;

	public ValidationResult vaidate() {
		String file = runCommand();
		ValidationResult vr = analyzeOutput(file);
		return vr;
	}

	protected abstract String runCommand();

	protected abstract ValidationResult analyzeOutput(String file);
}
