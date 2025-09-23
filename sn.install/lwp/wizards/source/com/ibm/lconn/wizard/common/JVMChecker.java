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


/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 */
public class JVMChecker {
	public static final String VENDOR_IBM = "IBM Corporation";
	public static final String VENDOR_SUN = "Sun";
	public static final String VERDOR_ORACLE = "Orace";

	public static final int[] VERSION_150 = new int[] { 1, 5, 0 };
	public static final int[] VERSION_140 = new int[] { 1, 4, 0 };
	public static final int[] VERSION_142 = new int[] { 1, 4, 2 };
	public static final int[] VERSION_160 = new int[] { 1, 6, 0 };
	
	public static final String JAVA_VENDOR = "java.vendor";
	public static final String JAVA_VERSION = "java.version";

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage:");
			System.out
					.println("java com.ibm.lconn.wizard.common.JVMChecker java_vendor java_version");
			System.exit(1);
		}
		System.out.println(JAVA_VENDOR + "=" + System.getProperty(JAVA_VENDOR));
		System.out.println(JAVA_VERSION + "=" + System.getProperty(JAVA_VERSION));
		// java vendor is not as required
		if (!System.getProperty(JAVA_VENDOR).contains(args[0])) {
			System.exit(1);
		}

		String version = System.getProperty(JAVA_VERSION);
		if (version == null) {
			System.exit(1);
		}

		String[] expectVersion = args[1].split("\\.");
		String[] currentVersion = System.getProperty(JAVA_VERSION).split("\\.");
		boolean compat = true;
		int length = expectVersion.length < currentVersion.length ? expectVersion.length
				: currentVersion.length;
		for (int i = 0; i < length; i++) {
			int expectedDigit = Integer.parseInt(expectVersion[i]);
			int currentDigit = Integer.parseInt(currentVersion[i]);
			if(currentDigit > expectedDigit) {
				break;
			} else if(currentDigit < expectedDigit) {
				System.exit(1);
			}
		}

		if (!compat) {
			System.exit(1);
		}
		
		System.out.println("Current Java Virtual Machine is compatible.");
		System.exit(0);
	}
}
