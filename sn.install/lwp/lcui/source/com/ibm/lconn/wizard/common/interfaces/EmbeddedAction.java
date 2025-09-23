/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common.interfaces;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public interface EmbeddedAction extends LCAction {
	public String execute();
	public String getLogPath();
	public String getActionLabel();
	public int getVerticalAlignment();
	public int getHorizontalAlignment();
}
