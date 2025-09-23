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
package com.ibm.lconn.wizard.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.ibm.lconn.wizard.common.interfaces.ValueListener;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.WizardData;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;
import com.ibm.lconn.wizard.common.ui.ext.LCWizardInput;

/**
 * This class is to handle runtime user input of all the wizards
 * 
 * @author Jun Jing Zhang
 * 
 */
public class DataPool {
	private static HashMap<String, String> props = new HashMap<String, String>();
	private static HashMap<String, Object> dataMap = new HashMap<String, Object>();
	private static HashMap<String, WizardPageInputData> inputMap = new HashMap<String, WizardPageInputData>();
	private static HashMap<String, WizardPageData> pageMap = new HashMap<String, WizardPageData>();
	private static HashMap<String, LCWizardInput> inputWidgetMap = new HashMap<String, LCWizardInput>();
	private static HashMap<String, WizardData> wizardMap = new HashMap<String, WizardData>();
	private static HashMap<String, List<ValueListener>> listeners = new HashMap<String, List<ValueListener>>();

	/**
	 * Get the attribute value of inputId in the wizard with wizardId
	 * 
	 * @param wizardId
	 * @param inputId
	 * @return
	 */
	public static String getValue(String wizardId, String inputId) {
		String property = props.get(getPropKey(wizardId, inputId));
		if (property == null)
			property = Constants.TEXT_EMPTY_STRING;
		
		log("getValue:"+wizardId+"#"+inputId+"="+property);
		return property;
	}

	private static String getPropKey(String wizardId, String inputId) {
		return wizardId + "." + inputId;
	}

	/**
	 * Set the attribute value of inputId in the wizard with wizardId
	 * 
	 * @param wizardId
	 * @param inputId
	 * @return old value or null if the value do not exist.
	 */
	public static String setValue(String wizardId, String inputId, String value) {
		log("setValue:"+wizardId+"#"+inputId+"="+value);
		String propKey = getPropKey(wizardId, inputId);
		String oldValue = props.get(propKey);
		if (value == null)
			value = Constants.TEXT_EMPTY_STRING;
		value = value.trim();
		props.put(propKey, value);
		value = performValueChange(wizardId, inputId, value, oldValue);
		return oldValue;
	}

	private static String performValueChange(String wizardId, String inputId,
			String value, String oldValue) {
		List<ValueListener> listeners = getListeners(wizardId);
		for (Iterator<ValueListener> iterator = listeners.iterator(); iterator
				.hasNext();) {
			ValueListener listener = (ValueListener) iterator.next();
			value = listener.valueChanged(inputId, oldValue, value);
		}
		return value;
	}

	private static List<ValueListener> getListeners(String wizardId) {
		List<ValueListener> result = listeners.get(wizardId);
		if (result == null) {
			result = new ArrayList<ValueListener>();
			listeners.put(wizardId, result);
		}
		return result;
	}

	/**
	 * Get the complex data, e.g. table data, of inputId in the wizard with
	 * wizardId
	 * 
	 * @param wizardId
	 * @param inputId
	 * @return
	 */
	public static Object getComplexData(String wizardId, String inputId) {
		Object data = dataMap.get(getPropKey(wizardId, inputId));
		return data;
	}

	/**
	 * Set the complex data, e.g. table data, of inputId in the wizard with
	 * wizardId
	 * 
	 * @param wizardId
	 * @param inputId
	 * @return
	 */
	public static Object setComplexData(String wizardId, String inputId,
			Object data) {
		Object oldData = getComplexData(wizardId, inputId);
		dataMap.put(getPropKey(wizardId, inputId), data);
		return oldData;
	}

	public static WizardPageInputData getWizardPageInput(String wizardId,
			String inputId) {
		return inputMap.get(getPropKey(wizardId, inputId));
	}

	public static WizardPageInputData setWizardPageInput(String wizardId,
			String inputId, WizardPageInputData input) {
		WizardPageInputData oldInput = getWizardPageInput(wizardId, inputId);
		inputMap.put(getPropKey(wizardId, inputId), input);
		return oldInput;
	}

	public static WizardPageData getWizardPage(String wizardId, String pageId) {
		return pageMap.get(getPropKey(wizardId, pageId));
	}

	public static WizardPageData setWizardPage(String wizardId, String pageId,
			WizardPageData input) {
		WizardPageData oldInput = getWizardPage(wizardId, pageId);
		pageMap.put(getPropKey(wizardId, pageId), input);
		return oldInput;
	}

	public static WizardData getWizard(String wizardId) {
		return wizardMap.get(wizardId);
	}

	public static WizardData setWizard(String wizardId, WizardData wizard) {
		WizardData oldInput = getWizard(wizardId);
		wizardMap.put(wizardId, wizard);
		return oldInput;
	}

	public static LCWizardInput getWizardInputWidget(String wizardId,
			String inputId) {
		return inputWidgetMap.get(getPropKey(wizardId, inputId));
	}

	public static LCWizardInput setWizardInputWidget(String wizardId,
			String inputId, LCWizardInput input) {
		LCWizardInput oldInput = getWizardInputWidget(wizardId, inputId);
		inputWidgetMap.put(getPropKey(wizardId, inputId), input);
		return oldInput;
	}

	public static void printInputs() {
		System.out.println();
		Set<String> keySet = props.keySet();
		for (String object : keySet) {
			System.out.print(object + ",");
		}
		System.out.println();
		for (Object o : props.values()) {
			System.out.print(o + ",");
		}
		System.out.println();
	}

	private static void setDirtyFlag(String wizardId, String inputId,
			String dirty) {
		DataPool.setValue(wizardId, inputId + ".DIRTY", dirty);
	}

	public static boolean getDirtyFlag(String wizardId, String inputId) {
		String value = DataPool.getValue(wizardId, inputId + ".DIRTY");
		return Constants.BOOL_TRUE.equals(value);

	}

	public static void setValueWithDirtyFlag(String wizardId, String inputId,
			String value) {
		
		String oldValue = setValue(wizardId, inputId, value);
		if (!CommonHelper.equals(oldValue, value)) {
			setDirtyFlag(wizardId, inputId, Constants.BOOL_TRUE);
		}
	}

	public static void resetAll(String wizardId) {
		removeKeys(wizardId, props);
		removeKeys(wizardId, dataMap);
		DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_OS_TYPE, CommonHelper.getPlatformType());
		DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_TDI_INSTALL_DIR, MessageUtil
						.getSettingAccordingOS(Constants.INPUT_TDI_INSTALL_DIR,
								"DEFAULTVALUE"));
		for(String key : Constants.optionalTaskFileMap.keySet()) {
			String relativePath = Constants.TDI_WORK_DIR + Constants.FS + Constants.optionalTaskFileMap.get(key);
			DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE, 
					Constants.INPUT_TDI_OPTIONAL_TASK + "." + key + ".path", 
					new File(relativePath).getAbsolutePath());
		}
		TestDataOffer.setKeystoreType();
	}

	private static void removeKeys(String wizardId,
			HashMap<String, ? extends Object> props) {
		List<String> propsKeyRemoveList = new ArrayList<String>();
		for (String object : props.keySet()) {
			String key = (String) object;
			if (key.startsWith(wizardId)) {
				propsKeyRemoveList.add(key);
			}
		}
		for (String key : propsKeyRemoveList) {
			props.remove(key);
		}
	}

	public static String getTask() {
		String task = System.getProperty("com.ibm.lconn.wizard.task");
		if (task == null)
			return "";
		return task;
	}

	public static void setTask(String task) {
		log("Switch to task: " + task);
		System.setProperty("com.ibm.lconn.wizard.task", task);
	}

	private static void log(String msg) {
		LogUtil.getLogger(DataPool.class).log(Level.FINEST, msg);
	}

	public static void addValueListener(String wizardId,
			ValueListener valueListener) {
		List<ValueListener> listeners = getListeners(wizardId);
		listeners.remove(valueListener);
		listeners.add(valueListener);
	}

}
