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
package com.ibm.lconn.common.feature;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class LCInfo {
	private static final String featureFolderName = "activities,blogs,communities,dogear,profiles,homepage";
	private static final String[] featureFolderNames = featureFolderName.split(",");
	public static final String FEATURE_ACTIVITIES = "activities"; //$NON-NLS-1$
	public static final String FEATURE_BLOGS = "blogs"; //$NON-NLS-1$
	public static final String FEATURE_COMMUNITIES = "communities"; //$NON-NLS-1$
	public static final String FEATURE_DOGEAR = "dogear"; //$NON-NLS-1$
	public static final String FEATURE_PROFILES = "profiles"; //$NON-NLS-1$
	public static final String FEATURE_HOMEPAGE = "homepage"; //$NON-NLS-1$
	private static final String FEATURE_MOBILE = "mobile";//$NON-NLS-1$
	private static final String FEATURE_SEARCH = "search";//$NON-NLS-1$
	private static final String FEATURE_WIKIS = "wikis";//$NON-NLS-1$
	private static String FEATURE_FILES = "files";//$NON-NLS-1$
	private static String FEATURE_FORUMS = "forums";//$NON-NLS-1$
	public static final String[] FEATURE_ALL = {FEATURE_ACTIVITIES, FEATURE_BLOGS, FEATURE_COMMUNITIES, FEATURE_DOGEAR, FEATURE_PROFILES, FEATURE_HOMEPAGE, FEATURE_WIKIS, FEATURE_FILES, FEATURE_FORUMS, FEATURE_SEARCH, FEATURE_MOBILE};
	
	private HashMap<String, FeatureInfo> featureInfo = new HashMap<String, FeatureInfo>();
	private int featureCount;
	
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
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				addFeature(curFeature, feature);
			}
		}
	}
	
	public Properties getLCProperties(){
		Properties result = new Properties();
		for (int i = 0; i < FEATURE_ALL.length; i++) {
			String feature = FEATURE_ALL[i];
			FeatureInfo info = getFeature(feature);
			if(info==null) continue;
			Properties featureProps = info.getFeatureProperties();
			Enumeration<?> propertyNames = featureProps.propertyNames();
			while(propertyNames.hasMoreElements()){
				String ele = (String) propertyNames.nextElement();
				result.setProperty(ele, featureProps.getProperty(ele));
			}
		}
		return result;
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
		if(FEATURE_HOMEPAGE.equals(name))
			return (FeatureInfo) featureInfo.get(featureFolderNames[5]);
		return (FeatureInfo) featureInfo.get(name);
	}
	
	public static void main(String[] args) {
		String lcHome = "d:\\IBM\\WebSphere\\IBM-Connections Latest20\\";
		LCInfo lc = new LCInfo(lcHome);
		System.out.println(lc.featureInfo.size());
		lc.getLCProperties().list(System.out);
	}
	
	
}
