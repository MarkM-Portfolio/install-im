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

import java.util.regex.Pattern;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.LogAnalysis;
import com.ibm.lconn.wizard.common.msg.Messages;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class NodeValidator extends ValidatorImpl {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.cluster.validation.impl.ValidatorImpl#analyzeOutput(java.lang.String)
	 */
	@Override
	protected ValidationResult analyzeOutput(String file) {
		boolean value;
		Pattern[] p;

		// for hostname
		p = new Pattern[] { Pattern.compile(".*ADMU0522E.*") };
		value = LogAnalysis.containsAnd(file, p);
		if (value)
			return new ValidationResult(1, Messages.getString("server.invalid"));

		// for hostname
		p = new Pattern[] { Pattern.compile(".*ADMU0509I.*") };
		value = LogAnalysis.containsAnd(file, p);
		if (value)
			return new ValidationResult(1, Messages.getString("server.stopped"));

		
		// for username and password
		p = new Pattern[] { Pattern.compile(".*ADMN0022E.*") };
		value = LogAnalysis.containsAnd(file, p);
		if (value)
			return new ValidationResult(2, Messages
					.getString("account.invalid"));

		// pass
		return new ValidationResult(0, Messages.getString("succeed"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.cluster.validation.impl.ValidatorImpl#runCommand()
	 */
	@Override
	protected String runCommand() {
		CommandExec ct = CommandExec
				.create(ClusterConstant.TASK_VALIDATE_NODE);
		String output = Constants.OUTPUT_ROOT + Constants.FS
				+ "validate_node.txt";
		ct.setOutput(output);
		ct.execute();
		return output;
	}

}
