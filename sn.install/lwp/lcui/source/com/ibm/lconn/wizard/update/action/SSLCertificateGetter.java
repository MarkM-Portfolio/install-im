/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.update.action;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
public class SSLCertificateGetter {
	
	public static String getSSLCertificate(String profilePath,String dmUserid,String dmPassword){
		
//		String profilePath = System.getProperty("profilePath");
//		String dmUserid = System.getProperty("dm.userid");
//		String dmPassword = System.getProperty("dm.password");
		String extension = null;
		
		String command = null;
		if (Constants.OS_WINDOWS.equalsIgnoreCase(CommonHelper.getPlatformShortType()))
			command = "\"" + profilePath + "/bin/retrieveSigners.bat\" CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username " + dmUserid + " -password " + dmPassword;
		else
			command = profilePath + "/bin/retrieveSigners.sh" + " CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username " + dmUserid + " -password " + dmPassword;
	
	    //String command = "\"" + profilePath + "/bin/retrieveSigners." + extension + "\" CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username " + dmUserid + " -password " + dmPassword;
	 // String command = profilePath + "/bin/retrieveSigners." + extension + " CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username " + dmUserid + " -password " + dmPassword;
		ExternalCommandAction eca = new ExternalCommandAction();
		eca.setWorkingDirectory(profilePath);
		eca.setCommand(command);
		eca.execute();	
		return eca.getReturnCode();
	}

}
