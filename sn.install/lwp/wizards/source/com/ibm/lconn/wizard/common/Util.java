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

import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class Util {
	private static final Logger logger = LogUtil.getLogger(Util.class);
	
	@SuppressWarnings("unchecked")
	public static InputStream getInputStream(Class clazz, String path){
		if (path.length() > 0 && path.charAt(0) == '/') {
			String newPath = path.substring(1, path.length());
			return new BufferedInputStream(clazz.getClassLoader().getResourceAsStream(newPath));
		} else {
			return clazz.getResourceAsStream(path);
		}
	}
	
	public static void printProperties(Properties props){
		Set<Entry<Object,Object>> entrySet = props.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
	}
	
	/**
	 * Return the index of str in strArry
	 * @param strArry
	 * @param str
	 * @return -1 if str does not exist in strArry
	 */
	public static int indexOf(String[] strArry, String str){
		if(strArry==null || strArry.length == 0) return -1;
		for (int i = 0; i < strArry.length; i++) {
			if(CommonHelper.equals(strArry[i], str))
				return i;
		}
		return -1;
	}
	/**
	 * Return the index of <T> in tList
	 * @param tList
	 * @param t
	 * @return -1 if t does not exist in strArry
	 */
	public static<T> int indexOf(List<T> tList, T t){
		if(tList==null || tList.size() == 0) return -1;
		for (int i = 0; i < tList.size(); i++) {
			if(CommonHelper.equals(tList.get(i), t))
				return i;
		}
		return -1;
	}
	
	
	/**
	 * Reads content from file
	 * @param fileName
	 * @return file content
	 */
	public static String readFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		
		char[] buffer = new char[1024];
		int count = -1;
		
		try {
		FileReader rf = new FileReader(fileName);
		while((count=rf.read(buffer))!=-1) {
			sb.append(buffer, 0, count);
		}
		} catch(IOException e) {
			logger.log(Level.SEVERE,"common.severe.fail_to_get_content", e);
		}
		return sb.toString();
	}

	public static String[] delimStr(String sourceStr) {
		
		String[] split = sourceStr.split(Util.DELIM);
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		return split;
	}

	public static final String DELIM = ",";

	public static String joinWithDelim(String[] delimStr) {
		String value = null;
		for (String str : delimStr) {
			if(value==null) value = str;
			else value += DELIM + str;
		}
		return value;
	}

	public static<T> T[] toArray(List<T> buttonLabelList,
			T[] result) {
		if(buttonLabelList==null || buttonLabelList.size()==0) return null;
		return buttonLabelList.toArray(result);
	}

	public static String addSelection(String value,
			String addingSelection) {
		if(CommonHelper.isEmpty(value)) return addingSelection;
		String[] delimStr = delimStr(value);
		for (int i = 0; i < delimStr.length; i++) {
			if(delimStr[i].equals(addingSelection)){
				return value;
			}
		}
		value += DELIM + addingSelection;
		return value;
		
	}
	
	public static int indexOf(String options, String targetOption){
		return indexOf(delimStr(options), targetOption);
	}
	
}
