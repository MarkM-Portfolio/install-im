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
package com.ibm.lconn.wizard.cluster.data;

import java.io.File;
import java.util.HashMap;

public class LCInfo {
	private static final String featureFolderName = "activities,blogs,communities,dogear,profiles,homepage";
	private static final String[] featureFolderNames = featureFolderName.split(",");
	private HashMap<String, FeatureInfo> featureInfo = new HashMap<String, FeatureInfo>();
	
	public LCInfo(String lcHome) {
		lcHome = new File(lcHome).getAbsolutePath();
		File lcHomeFile = new File(lcHome);
		for (int i = 0; i < featureFolderNames.length; i++) {
			String curFeature = featureFolderNames[i];
			File featureFile = new File(lcHomeFile, curFeature);
			if (featureFile.exists()) {
				FeatureInfo feature;
				try {
					feature = new FeatureInfo(lcHome, featureFolderNames[i]);
				} catch (IlleagalLCFeature e) {
					continue;
				}
				featureInfo.put(curFeature, feature);
			}
		}
	}
	
	public FeatureInfo getFeature(String name){
		return featureInfo.get(name);
	}
	
	public static void main(String[] args) {
		String lcHome = "C:\\Program Files\\IBM\\IBM-Connections\\";
		LCInfo lc = new LCInfo(lcHome);
		System.out.println(lc.featureInfo.size());
	}
	
	
}
