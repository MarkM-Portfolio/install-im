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
package com.ibm.lconn.wizard.common.code.format.impl;

/**
 * @author joey (pengzsh@cn.ibm.com)
 *
 */
public class Element {
	public static final int PARENLEFT = 1;  // (
	public static final int PARENRIGHT = 2; // )
	public static final int BRACELEFT = 3;  // {
	public static final int BRACERIGHT = 4; // }
	public static final int SEMICOLON = 5;  // ;
	public static final int WORD = 6;
	public static final int OTHER = 7;
	public static final int SPACE = 8;
	public static final int START = 9;
	public static final int COMMENT = 10;
	public static final int DOCUMENT = 11;

	public int type;
	public String content;

	public Element(int type, String content) {
		this.type = type;
		this.content = content;
	}

}
