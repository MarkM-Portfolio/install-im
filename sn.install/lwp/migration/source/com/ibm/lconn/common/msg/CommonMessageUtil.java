/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.msg;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public abstract class CommonMessageUtil {
	private final String MSG_BUNDLE_NAME = getMsgBundle(); //$NON-NLS-1$
	private final String SETTING_BUNDLE_NAME = getSettingBundle();

	private final ResourceBundle MSG_BUNDLE = ResourceBundle
			.getBundle(MSG_BUNDLE_NAME);
	private final ResourceBundle SETTING_BUNDLE = ResourceBundle
	.getBundle(SETTING_BUNDLE_NAME);
	
	protected abstract String getMsgBundle();
	protected abstract String getSettingBundle();

	private String getString(ResourceBundle resourceBundle, String key, Object... para) {
		try {
			String val = resourceBundle.getString(key);
			if(val!=null) val = MessageFormat.format(val, para);
			return val;
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public String getMsg(String key, Object... para){
		return getString(MSG_BUNDLE, key, para); 
	}
	
	public String getSetting(String key, Object... para){
		return getString(SETTING_BUNDLE, key, para);
	}
	
}
