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
package com.ibm.lconn.wizard.common;

import java.util.Locale;

import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.WizardSetting;

public class MessageUtil {
	public static final String BOOLEAN_TRUE_LABEL = "LABEL.COMMON_YES";
	public static final String BOOLEAN_FALSE_LABEL = "LABEL.COMMON_NO";
	public static final String TAG_LABEL = "LABEL";
	public static final String TAG_NAME = "NAME";
	public static final String DOT = ".";
	private static final String[] needTranslation = Util.delimStr(getSetting(
			"global", "needTranslation"));
	public static final String TAG_TOOLTIP = "TOOLTIP";
	private static final int WIZARD_SETTING = 0;
	private static final int MESSAGES = 1;
	private static final String TAG_TRANSLATION = "TRANSLATION";

	public static String getWizardTitle(String wizardId){
		return MessageUtil.getMsg("WIZARD.title", wizardId);
	}
	
	public static String getLabel(String inputId) {
		String task = DataPool.getTask();
		if (!CommonHelper.isEmpty(task)) {
			String labelForTask = getLabelInner(inputId + "." + task);
			if (!CommonHelper.isEmpty(labelForTask)) {
				return labelForTask;
			}
		}

		String labelInner = getLabelInner(inputId);
		if (CommonHelper.isEmpty(labelInner))
			return inputId;
		return labelInner;
	}

	private static String getLabelInner(String inputId) {
		String value = Constants.TEXT_EMPTY_STRING;
		String fieldValueInMsg = MessageUtil.getMsg(MessageUtil.TAG_LABEL,
				inputId);
		String fieldValueInSetting = MessageUtil.getSetting(
				MessageUtil.TAG_LABEL, inputId);
		if (!CommonHelper.isEmpty(fieldValueInMsg)) {
			value = fieldValueInMsg;
		} else if (!CommonHelper.isEmpty(fieldValueInSetting)) {
			value = fieldValueInSetting;
		}
		return value;
	}
	
	private static String getTranslationInner(String inputId) {
		String value = Constants.TEXT_EMPTY_STRING;
		String fieldValueInMsg = MessageUtil.getMsg(MessageUtil.TAG_TRANSLATION,
				inputId);
		String fieldValueInSetting = MessageUtil.getSetting(
				MessageUtil.TAG_TRANSLATION, inputId);
		if (!CommonHelper.isEmpty(fieldValueInMsg)) {
			value = fieldValueInMsg;
		} else if (!CommonHelper.isEmpty(fieldValueInSetting)) {
			value = fieldValueInSetting;
		}
		return value;
	}

	public static String getSettingWithParameter(String key,
			Object... parameter) {
		return getMsgForAll(WIZARD_SETTING, key, parameter);
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
		String setting = getMsgForAll(WIZARD_SETTING, keyAll);
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

	public static String translate(String value) {
		// special handling for BOOLEAN values
		if(Constants.BOOL_FALSE.equalsIgnoreCase(value)) {
			return getMsg(BOOLEAN_FALSE_LABEL);
		} else if(Constants.BOOL_TRUE.equalsIgnoreCase(value)) {
			return getMsg(BOOLEAN_TRUE_LABEL);
		}
		
		if (value.indexOf(Util.DELIM) != -1) {
			String[] delimStr = Util.delimStr(value);
			for (int i = 0; i < delimStr.length; i++) {
				delimStr[i] = translateWithCheck(delimStr[i]);
			}
			return Util.joinWithDelim(delimStr);
		} else {
			return translateWithCheck(value);
		}
	}

	private static String translateWithCheck(String value) {
		if (Util.indexOf(needTranslation, value) == -1)
			return value;
		String translation = getTranslation(value);
		if (CommonHelper.isEmpty(translation)) {
			return getLabel(value);
		} else {
			return translation;
		}
	}

	private static String getTranslation(String inputId) {
		String task = DataPool.getTask();
		if (!CommonHelper.isEmpty(task)) {
			String translationForTask = getTranslationInner(inputId + "." + task);
			if (!CommonHelper.isEmpty(translationForTask)) {
				return translationForTask;
			}
		}

		String labelInner = getTranslationInner(inputId);
		if (CommonHelper.isEmpty(labelInner))
			return inputId;
		return labelInner;
	}

	public static String getSettingAccordingOS(String... key) {
		String keys = getKeyAll(key);
		String keyAll = getKeyAll(keys, CommonHelper.getPlatformType());
		String setting = getSetting(keyAll);
		if (setting != null)
			return setting;
		return getSetting(keys, Constants.OS_OTHER);
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
		String msgInner = getMsgInner(type, key + DataPool.getTask(), parameter);
		if (!CommonHelper.isEmpty(msgInner))
			return msgInner;
		return getMsgInner(type, key, parameter);

	}

	private static String getMsgInner(int type, String key, Object... parameter) {
		switch (type) {
		case WIZARD_SETTING:
			if (parameter == null || parameter.length == 0) {
				return WizardSetting.getString(key);
			}
			return WizardSetting.getString(key, parameter);
		default:// case MESSAGES:
		// try {
			if (parameter == null || parameter.length == 0) {
				return Messages.getString(key);
			}
			return Messages.getString(key, parameter);
			// } catch (Exception e) {
			// System.out.println("xx");
			// return Messages.getString(key, parameter);
			// }
		}
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		System.out.println(getWizardTitle("cluster"));
	}
}
