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
package com.ibm.lconn.wizard.common.test;

import java.util.Locale;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.MessageUtil;

public class TestDataOffer {
//	public static void openModifier(){
//		Shell shell = new Shell(ResourcePool.getDisplay());
//		Combo combo = new Combo(shell, SWT.NONE);
//		Text value = new Text(shell, SWT.NONE);
//		Field[] fields = ClusterConstant.class.getFields();
//		for (int i = 0; i < fields.length; i++) {
//			
//		}
//		shell.open();
//	}
	
	public static void setLocale(){
		if(!Constants.BOOL_TRUE.equals(MessageUtil.getSetting("global.enableGlobalization"))){
			Locale.setDefault(Locale.ENGLISH);
		}
	}
	
}
