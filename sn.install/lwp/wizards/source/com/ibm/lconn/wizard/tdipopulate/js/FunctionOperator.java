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
package com.ibm.lconn.wizard.tdipopulate.js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.lconn.wizard.common.code.parser.Parser;
import com.ibm.lconn.wizard.common.code.parser.impl.JSParser;

public class FunctionOperator {
	private static Map<String, Function> map;
	public static List<String> getNameList() {
		if(map == null) {
			map = new HashMap<String, Function>();
			Parser p = new JSParser();
			Map<String, String> funMap = p.getFunctionList(FunctionFile.read());
			Iterator<String> it = funMap.keySet().iterator();
			while(it.hasNext()) {
				String name = it.next();
				String body = funMap.get(name);
				map.put(name, new Function(name, body));
			}
		}
		List<String> list = new ArrayList<String>();
		list.addAll(map.keySet());
		return sort(list);
	}

	public static Function getFunction(String name) {
		return map.get(name);
	}

	public static void editFunction(String name, Function func) {
		map.put(name, func);
	}

	public static List<String> addFunction(String function) {
		Parser p = new JSParser();
		Map<String, String> funMap = p.getFunctionList(function.getBytes());
		Iterator<String> it = funMap.keySet().iterator();
		List<String> nameList = new ArrayList<String>();
		while(it.hasNext()) {
			String name = it.next();
			String body = funMap.get(name);
			nameList.add(name);
			map.put(name, new Function(name, body));
		}
		return nameList;
	}
	
	public static void deleteFunction(String name) {
		map.remove(name);
	}
	public static void save() {
		reWrite();
	}
	
	public static void reWrite() {
		String content = "";
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String name = it.next();
			content += map.get(name).getBody();
		}
		FunctionFile.write(content.getBytes());		
	}
	
	private static List<String> sort(List<String> list) {
		Object[] arr = list.toArray();
		java.util.Arrays.sort(arr);
		int length = arr.length;
		list = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			list.add((String)arr[i]);
		}
		
		return list;
	}
	
	public static void main(String[] a) {
		
			List<String> names = getNameList();
			Iterator<String> it = names.iterator();

			while (it.hasNext()) {
				//String name = it.next();
				//System.out.println(name);
				//System.out.println(getFunction(name).getBody());
			}
			
		
	}
}
