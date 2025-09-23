/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;

public class WasInstalledFeaturesValidator {
	
	public static boolean validate(String profilePath,String selectedFeatures,String installLocation,String dmUserid,String dmPassword){
		boolean result = true;
		
		String tempDir = profilePath + "/bin";
		File file = new File(tempDir + "/lcInstalledFeatures.properties");
		deleteFile(file);
		
//		String profilePath = System.getProperty("profilePath");
//		String installLocation = System.getProperty("installLocation");
//		String dmUserid = System.getProperty("dm.userid");
//		String dmPassword = System.getProperty("dm.password");
		
		String command = profilePath + "/bin/wsadmin.bat -conntype SOAP -lang jython -username " + dmUserid + " -password " + dmPassword + " -f " + installLocation + "/ConfigEngine/config/was/lc_GetInstalledFeatures.py " +  tempDir + "/lcInstalledFeatures.properties";
		ExternalCommandAction eca = new ExternalCommandAction();
		eca.setCommand(command);
		eca.setWorkingDirectory(profilePath);
		eca.execute();
		System.out.println(eca.getReturnCode());
		JavaPropertyGetter propertyGetter = new JavaPropertyGetter(file);
		String installedFeatures = propertyGetter.getProperty("installedFeatures");
		
//		String selectedFeatures = System.getProperty("selectedFeatures");
		GetOverlappedFeaturesAction golfActoin = new GetOverlappedFeaturesAction();
		golfActoin.execute(selectedFeatures,installedFeatures);
		
		if(!"".equals(golfActoin.getOverlappedFeatures())){
			result = false;
		}

		return result;
	}
	
	public static boolean deleteFile(File file){
		if(file.exists()){
			return file.delete();
		}
		return false;
	}

}
