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
package com.ibm.lconn.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class ObjectUtil {
	public static boolean isEmpty(String str) {
		return null == str || "".equals(str);
	}

	public static boolean isTrue(String boolVal) {
		boolVal = normalize(boolVal, true);
		return "true".equals(boolVal);
	}

	public static String normalize(String str, boolean toLowerCase) {
		if (isEmpty(str))
			return "";
		str = str.trim();
		if (toLowerCase)
			str.toLowerCase();
		return str;
	}

	public static String normalizePath(String path) {
		File f = new File(path);
		return f.getAbsolutePath();
	}

	public static File getFile(String path) {
		return new File(normalizePath(path));
	}

	public static boolean isStyle(int style, int SWT_STYLE) {
		return (style & SWT_STYLE) != 0;
	}

	public static int removeStyle(int style, int... needRemove) {
		for (int i = 0; i < needRemove.length; i++) {
			style &= ~needRemove[i];
		}
		return style;
	}

	public static boolean equals(Object o1, Object o2) {
		return null != o1 && o1.equals(o2);
	}

	public static Object[] str2Obj(String[] split) {
		Object[] re = new Object[split.length];
		for (int i = 0; i < re.length; i++) {
			re[i] = split[i];
		}
		return re;
	}

	public static boolean evalBool(String para) {
		return isTrue(para);
	}

	@SuppressWarnings("unchecked")
	public static Object loadObject(String className)
			throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class loadClass = ClassLoader.getSystemClassLoader().loadClass(
				className);
		Constructor constructors = loadClass.getConstructor();
		Object newInstance = constructors.newInstance();
		return newInstance;
	}

	public static String trimFirst(String str, String trimSymbol) {
		if (isEmpty(str))
			return "";
		int index = str.indexOf(trimSymbol);
		int tailStart = index + trimSymbol.length();
		if (index == 0) {
			str = (tailStart == str.length() ? "" : str.substring(tailStart));
			return trimFirst(str, trimSymbol);
		} else {
			return str;
		}
	}

	public static String trimLast(String str, String trimSymbol) {
		if (isEmpty(str))
			return "";
		int index = str.lastIndexOf(trimSymbol);
		int tailStart = index + trimSymbol.length();
		if (tailStart == str.length()) {
			str = (index == 0 ? "" : str.substring(0, index));
			return trimLast(str, trimSymbol);
		} else {
			return str;
		}
	}

	public static Properties loadProperties(String fileLocation) throws IOException {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(fileLocation));
		} catch (Exception e) {
			try {
				p.load(ObjectUtil.class.getResourceAsStream(fileLocation));
			} catch (Exception e2) {
				String msg = MessageFormat.format("There''s no such file or resource: {0}", fileLocation);
				throw new IOException(msg);
			}
		}
		return p;
	}

	public static String trim(String str, String trimSymbol) {
		str = trimFirst(str, trimSymbol);
		str = trimLast(str, trimSymbol);
		return str;
	}

	public static boolean isDigit(String header) {
		if (isEmpty(header))
			return false;
		return header.matches("\\d+");
	}

	public static void copyProperties(Properties source, Properties dest,
			String prefix, String postfix) {
		Set<Entry<Object, Object>> entrySet = source.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			String key = (String) entry.getKey();
			if (!isEmpty(prefix))
				key = prefix + "." + key;
			if (!isEmpty(postfix))
				key = key + "." + postfix;
			dest.setProperty(key, (String) entry.getValue());
		}
	}
}
