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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.ibm.lconn.wizard.common.interfaces.ValueListener;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class DataPool {
	private static HashMap<String, String> props = new HashMap<String, String>();
	private static HashMap<String, Object> dataMap = new HashMap<String, Object>();
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
