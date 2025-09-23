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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class GetOverlappedFeaturesAction {

	private String overlappedFeatures = "";

	public String getOverlappedFeatures() {
		return overlappedFeatures;
	}
	
	public void execute(String featureList1, String featureList2) {
		List result = new ArrayList();

		String featureList2Resolved = featureList2;
		String featureList1Resolved = featureList1;
		List featureList2Array = parseArray(featureList2Resolved);
		List featureList1Array = parseArray(featureList1Resolved);

		for (int i = featureList1Array.size() - 1; i >= 0; i--) {
			for (int j = 0; j < featureList2Array.size(); j++) {
				if (featureList2Array.get(j).equals(featureList1Array.get(i))) {
					result.add(featureList2Array.get(j));
					break;
				}
			}
		}

		this.overlappedFeatures = generateArrayString(result);
	}

	public String generateArrayString(List list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			// localize the feature string, modified by Bai Jian Su

			result += "$L(com.ibm.wps.install.CommonMsg, "
					+ list.get(i) + ".capitalized)";

			if (i < list.size() - 1)
				result += ",";
		}
		return result;
	}

	public List parseArray(String arrayString) {
		List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(arrayString, ",");
		for (; tokenizer.hasMoreTokens();) {
			list.add(tokenizer.nextToken());
		}
		return list;
	}

}
