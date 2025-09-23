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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LCUtil {

	public static final String LC_HOME = "Constants.GLOBAL.LC_HOME";
//	public static final String LC_ID = "LotusConnections";

	private static LCInfo lc;

	public static void setLCHome(String lcHome) {
		File f = new File(lcHome);
		lcHome = f.getAbsolutePath();
		System.setProperty(LC_HOME, lcHome);
		resetResultFormatTxt();
	}

	private static void resetResultFormatTxt() {
		try {
			File resultFormatPatterFile = getResultFormatPatterFile();
			if (resultFormatPatterFile.exists())
				resultFormatPatterFile.delete();
			InputStream is = LCUtil.class
					.getResourceAsStream("updateResultPattern.txt");
			OutputStream out = new FileOutputStream(resultFormatPatterFile);
			byte[] b = new byte[2048];
			int read = is.read(b);
			while (read != -1) {
				out.write(b, 0, read);
				read = is.read(b);
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	public static void main(String[] args) {
		System.setProperty(LC_HOME, "c:/temp");
		resetResultFormatTxt();
	}

	public static File getResultFormatPatterFile() {
		File resultFormatPatterFile = new File(getVersionFolder(),
				"updateResultPattern.txt");
		return resultFormatPatterFile;
	}

	public static String getLCHome() {
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

	public static boolean featureInstalled(String featureName) {
		return null != getFeature(featureName);
	}

	public static FeatureInfo getFeature(String featureName) {
		LCInfo lc = getLCInfo();
		FeatureInfo feature = lc.getFeature(featureName);
		return feature;
	}

	public static LCInfo getLCInfo() {
		return new LCInfo(getLCHome());
	}

	public static String getFilepath(String filePath) {
		return new File(filePath + "/").getAbsolutePath();
	}

	public static String getLCSupportURL() {
		return "http://www-306.ibm.com/software/lotus/products/connections/support";
	}

	/**
	 * 
	 */
	public static String getVersionFolder() {
		return new File(getLCHome(), "version").getAbsolutePath();
	}
}
