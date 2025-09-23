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
package com.ibm.lconn.wizard.common.validator;

import java.io.StringReader;
import java.util.logging.Logger;

import com.ibm.jscript.ASTTree.ASTFunction;
import com.ibm.jscript.ASTTree.ASTNode;
import com.ibm.jscript.parser.FBScript2;
import com.ibm.jscript.parser.ParseException;
import com.ibm.lconn.wizard.common.logging.LogUtil;

public class IBMJSValidator extends AbstractValidator {
	@SuppressWarnings("unused")
	private static final Logger logger = LogUtil.getLogger(IBMJSValidator.class);
	
	private String scriptContent;
	public IBMJSValidator(String scriptContent) {
		this.scriptContent = scriptContent;
	}
	
	public int validate() {
		return validate(true);
	}
	
	public int validate(boolean needEval) {
		String content =needEval ? eval(scriptContent) : scriptContent;
		
		FBScript2 parser = new FBScript2(new StringReader(content));
		ASTNode node = null;
		try {
			node = parser.program();
		} catch (ParseException e) {
			title = getTite("cannot_validate_js");
			message = e.getLocalizedMessage();
			return 4;
		}
		int count = node.getSlotCount();
		if(count != 1) {
			logError("require_a_function");
			return 1;
		} 
		ASTNode child = node.readSlotAt(0);
		if(!(child instanceof ASTFunction)) {
			logError("require_a_function");
			return 2;
		}
		
		ASTFunction f = (ASTFunction)child;
		if(f.getParameterCount()!=1) {
			logError("func_need_one_parameter");
			return 8;
		}
		
		return 0;
	}

}
