/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

public class GetValidationInfo {

	public static void main(String[] args) throws Exception {
		String profilePath = args[0];
		String pyPath = args[1];
		String wasUserId = args[2];
		String wasPasswd = args[3];
		String wasHost = args[4];
		String wasPortNum = args[5];
		String returnCode = null;
		boolean isWindows = args[6].equals("T");
		try {
			returnCode = DMValidator.getDMInfo(profilePath, pyPath, wasUserId,
					wasPasswd, wasHost, wasPortNum, isWindows);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error getting DM info");
		}
		if (!returnCode.equals("0")) {
			System.out.println("Error getting DM info");
			throw new Exception("Error getting DM info");
		} else {
			System.out.println("Getting DM info successful");
		}
	}
}
