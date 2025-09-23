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

public class SSLCertificateGetter {
	
	public static String getSSLCertificate(String profilePath,String dmUserid,String dmPassword){
		
//		String profilePath = System.getProperty("profilePath");
//		String dmUserid = System.getProperty("dm.userid");
//		String dmPassword = System.getProperty("dm.password");
		
	    String command = profilePath + "/bin/retrieveSigners.bat CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username " + dmUserid + " -password " + dmPassword;
		ExternalCommandAction eca = new ExternalCommandAction();
		eca.setWorkingDirectory(profilePath);
		eca.setCommand(command);
		eca.execute();
		
		System.out.println(eca.getReturnCode());
		System.out.println(command);
		System.out.println("SSLCertificateGetter succefully");
		
		return eca.getReturnCode();
	}

}
