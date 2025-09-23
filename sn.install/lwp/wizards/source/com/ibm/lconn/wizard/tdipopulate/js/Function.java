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
package com.ibm.lconn.wizard.tdipopulate.js;


public class Function {
	private String name;
	private String body;

	public Function(String name, String body) {
		this.name = name;
		this.body = body;
	}

	public String getName() {
		return this.name;
	}

	public String getBody() {
		return this.body;
	}
}
