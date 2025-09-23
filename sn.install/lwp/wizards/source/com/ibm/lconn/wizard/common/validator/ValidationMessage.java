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

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class ValidationMessage {
	private int messageType;
	private String title, messge;
	
	public ValidationMessage(int type, String title, String message) {
		this.messageType = type;
		this.title = title;
		this.messge = message;
	}
	
	public int getMessageType() {
		return messageType;
	}

	public String getMessge() {
		return messge;
	}

	public String getTitle() {
		return title;
	}

}
