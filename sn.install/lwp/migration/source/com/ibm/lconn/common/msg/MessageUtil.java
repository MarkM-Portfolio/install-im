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

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.msg;

import java.util.Locale;

import com.ibm.lconn.common.config.CommonConstants;
import com.ibm.lconn.common.config.CommonSetting;
import com.ibm.lconn.common.util.PlatformUtils;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class MessageUtil {

	private static final int SETTING = 0;
	private static final int MESSAGES = 1;
	private static final String DOT = ".";

	public static String getWizardTitle(String wizardId){
		return MessageUtil.getMsg("WIZARD.title", wizardId);
	}
	

	public static String getSettingWithParameter(String key,
			Object... parameter) {
		return getMsgForAll(SETTING, key, parameter);
	}

	public static String getMsgWithParameter(String key, Object... parameter) {
		return getMsgForAll(MESSAGES, key, parameter);
	}

	public static String getMsg(String... key) {
		String keyAll = MessageUtil.getKeyAll(key);
		return getMsgForAll(MESSAGES, keyAll);
	}

	public static String getSetting(String... key) {
		String keyAll = MessageUtil.getKeyAll(key);
		String setting = getMsgForAll(SETTING, keyAll);
		return setting;
	}

	public static String getKeyAll(String... key) {
		StringBuffer sb = new StringBuffer();
		for (String str : key) {
			sb.append(str);
			sb.append(DOT);
		}
		sb.setLength(sb.length() - 1);
		String keyAll = sb.toString();
		return keyAll;
	}

	public static String getSettingAccordingOS(String... key) {
		String keys = getKeyAll(key);
		String keyAll = getKeyAll(keys, PlatformUtils.getSimplePlatformType());
		String setting = getSetting(keyAll);
		if (setting != null)
			return setting;
		return getSetting(keys, CommonConstants.OS_OTHER);
	}

	public static String getMessageAccordingTask(String task, String... key) {
		String keys = getKeyAll(key);
		String keyAll = getKeyAll(keys, task);
		String setting = getSetting(keyAll);
		if (setting != null)
			return setting;
		return getSetting(keys);
	}

	private static String getMsgForAll(int type, String key,
			Object... parameter) {
		return getMsgInner(type, key, parameter);
	}

	private static String getMsgInner(int type, String key, Object... parameter) {
		switch (type) {
		case SETTING:
			if (parameter == null || parameter.length == 0) {
				return CommonSetting.getString(key);
			}
			return CommonSetting.getString(key, parameter);
		default:// case MESSAGES:
		// try {
			if (parameter == null || parameter.length == 0) {
				return CommonMessages.getString(key);
			}
			return CommonMessages.getString(key, parameter);
			// } catch (Exception e) {
			// System.out.println("xx");
			// return CommonMessages.getString(key, parameter);
			// }
		}
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		System.out.println(getWizardTitle("cluster"));
	}
}
