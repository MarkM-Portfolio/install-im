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
package com.ibm.lconn.common;

import java.io.File;
import java.util.HashMap;

public class LCInfo {
	private static final String featureFolderName = "activities,blogs,communities,dogear,profiles,homepage,wikis,files,forum,search,mobile,news,moderation,metrics,ccm";
	private static final String[] featureFolderNames = featureFolderName.split(",");
	public static final String FEATURE_ACTIVITIES = "activities"; //$NON-NLS-1$
	public static final String FEATURE_BLOGS = "blogs"; //$NON-NLS-1$
	public static final String FEATURE_COMMUNITIES = "communities"; //$NON-NLS-1$
	public static final String FEATURE_DOGEAR = "dogear"; //$NON-NLS-1$
	public static final String FEATURE_PROFILES = "profiles"; //$NON-NLS-1$
	public static final String FEATURE_HOMEPAGE = "homepage"; //$NON-NLS-1$
	public static final String FEATURE_WIKIS = "wikis"; //$NON-NLS-1$
	public static final String FEATURE_FILES = "files"; //$NON-NLS-1$
	public static final String FEATURE_NEWS = "news"; //$NON-NLS-1$
	public static final String FEATURE_SEARCH = "search"; //$NON-NLS-1$
	public static final String FEATURE_MOBILE = "mobile"; //$NON-NLS-1$
	public static final String FEATURE_FORUM = "forum"; //$NON-NLS-1$
	public static final String FEATURE_MODERATION = "moderation"; //$NON-NLS-1$
	public static final String FEATURE_METRICS="metrics";//$NON-NLS-1$
	public static final String FEATURE_CCM="ccm"; //$NON-NLS-1$

	private HashMap featureInfo = new HashMap();
	private int featureCount;
	
	public LCInfo(String lcHome) {
		lcHome = new File(lcHome).getAbsolutePath();
		File lcHomeFile = new File(lcHome);
		for (int i = 0; i < featureFolderNames.length; i++) {
			String curFeature = featureFolderNames[i];
			File featureFile = new File(lcHomeFile, "/version/" + curFeature +".product");
			if (featureFile.exists()) {
				FeatureInfo feature;
				try {
					feature = new FeatureInfo(lcHome, featureFolderNames[i]);
				} catch (Exception e) {
					continue;
				}
				addFeature(curFeature, feature);
			}
		}
	}

	private void addFeature(String curFeature, FeatureInfo feature) {
		featureCount++;
		featureInfo.put(curFeature, feature);
	}
	
	public int getFeatureCount(){
		return featureCount;
	}
	
	public boolean isValidLCHome(){
		return getFeatureCount()!=0;
	}
	
	public FeatureInfo getFeature(String name){
//		if(FEATURE_HOMEPAGE.equals(name))
//			return (FeatureInfo) featureInfo.get(featureFolderNames[5]);
		return (FeatureInfo) featureInfo.get(name);
	}
	
	public static void main(String[] args) {
		String lcHome = "C:\\Program Files\\IBM\\IBM-Connections\\";
		LCInfo lc = new LCInfo(lcHome);
		System.out.println(lc.featureInfo.size());
	}
	
	
}

