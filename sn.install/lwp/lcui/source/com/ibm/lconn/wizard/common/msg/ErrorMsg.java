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
package com.ibm.lconn.wizard.common.msg;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ErrorMsg {
	private static final String BUNDLE_NAME = "com.ibm.lconn.update.msg.errorMsg"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private ErrorMsg() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return "";
		}
	}
	
	public static String getString(String key, String...objects){
		String msg = getString(key);
		for (int i = 0; i < objects.length; i++) {
			msg = msg.replace("{"+i+"}", objects[i]);
		}
		return msg;
	}
	
//	public static void setString(String key, String value){
//		Properties p = new Properties();
//		try {
//			p.load(new FileInputStream("com/msg/messages.properties"));
//			p.setProperty(key, value);
//			p.store(new FileOutputStream("com/msg/messages.properties"), null);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
}
