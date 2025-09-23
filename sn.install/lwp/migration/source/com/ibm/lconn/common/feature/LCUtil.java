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
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class LCUtil {
	
	private static final String LC_HOME = "Constants.GLOBAL.LC_HOME";
	
	private static LCInfo lc;

	public static void setLCHome(String lcHome) {
		File f = new File(lcHome);
		lcHome = f.getAbsolutePath();
		System.setProperty(LC_HOME, lcHome);
	}
	
	public static String getLCHome(){
		return System.getProperty(LC_HOME);
	}
	
	public static String getProfileName(String prodPath) {
		// System.out.println(prodPath);
		File profileDir = new File(prodPath + File.separator + "uninstall"
				+ File.separator + "profiles");
		File curItem;
		// System.out.println(profileDir.toString());
		String[] list = profileDir.list();

		// there should be only one sub-directory in profiles, and that is the
		// name
		// of the WAS profile the feature is installed to
		for (int i = 0; i < list.length; i++) {
			curItem = new File(profileDir, list[i]);
			if (curItem.isDirectory()) {
				return curItem.getName();
			}
		}
		return null;
	}

	public static String getClusterName(String featureName) {
		return featureName.toUpperCase().charAt(0)
				+ featureName.toLowerCase().substring(1) + "Cluster";
	}
	
	public static boolean featureInstalled(String featureName){
		return null == getFeature(featureName);
	}

	public static FeatureInfo getFeature(String featureName) {
		LCInfo lc = getLCInfo();
		FeatureInfo feature = lc.getFeature(featureName);
		return feature;
	}

	public static LCInfo getLCInfo() {
		if(lc==null) 
			lc = new LCInfo(getLCHome());
		return lc;
	}

	public static String getLCSupportURL() {
		return "http://www-306.ibm.com/software/lotus/products/connections/support";
	}
}
