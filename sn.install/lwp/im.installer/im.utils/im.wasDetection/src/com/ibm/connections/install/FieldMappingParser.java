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
package com.ibm.connections.install;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class FieldMappingParser {

	public static Map parseMapping(String slaveFieldMapping) {
		Map allSlaveFields = new TreeMap();
		StringTokenizer tokenizer = new StringTokenizer(slaveFieldMapping, ";");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			StringTokenizer tokenizer2 = new StringTokenizer(token, ":");
			if (tokenizer2.hasMoreTokens()) {
				String masterField = tokenizer2.nextToken();
				if (tokenizer2.hasMoreTokens()) {
					String slaveFields = tokenizer2.nextToken();
					StringTokenizer tokenizer3 = new StringTokenizer(
							slaveFields, ",");
					List slaveFieldList = new ArrayList();
					while (tokenizer3.hasMoreTokens()) {
						String slaveField = tokenizer3.nextToken();
						slaveFieldList.add(slaveField);
					}
					allSlaveFields.put(masterField, slaveFieldList);
				}
			}
		}
		return allSlaveFields;
	}

	public static Map parseSemicolon(String slaveFieldMapping) {
		Map allSlaveFields = new TreeMap();
		StringTokenizer tokenizer = new StringTokenizer(slaveFieldMapping, ";");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			StringTokenizer tokenizer2 = new StringTokenizer(token, ":");
			if (tokenizer2.hasMoreTokens()) {
				String masterField = tokenizer2.nextToken();
				if (tokenizer2.hasMoreTokens()) {
					String slaveFields = tokenizer2.nextToken();
					allSlaveFields.put(masterField, slaveFields);
				}
			}
		}
		return allSlaveFields;
	}

	public static void main(String[] args) {
		Map map = parseMapping("a:b,c;");
		map = parseSemicolon("server1:activities;server2:blogs,comm,dogear");

		System.out.println(map);
	}
}
