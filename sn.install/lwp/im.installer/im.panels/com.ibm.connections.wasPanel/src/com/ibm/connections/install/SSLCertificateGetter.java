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

	public static String getSSLCertificate(String profilePath, String dmUserid,
			String dmPassword) {

		// String profilePath = System.getProperty("profilePath");
		// String dmUserid = System.getProperty("dm.userid");
		// String dmPassword = System.getProperty("dm.password");
		String extension = null;

		String command = null;
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			command = "\""
					+ profilePath
					+ "/bin/retrieveSigners.bat\" CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username "
					+ dmUserid + " -password " + dmPassword;
		} else if (osName.toLowerCase().startsWith("os/400")) {
			// OS400_Enablement
			command = profilePath
					+ "/bin/retrieveSigners"
					+ " CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username "
					+ dmUserid + " -password " + dmPassword;
		} else
			command = profilePath
					+ "/bin/retrieveSigners.sh"
					+ " CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username "
					+ dmUserid + " -password " + dmPassword;

		// String command = "\"" + profilePath + "/bin/retrieveSigners." +
		// extension +
		// "\" CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username "
		// + dmUserid + " -password " + dmPassword;
		// String command = profilePath + "/bin/retrieveSigners." + extension +
		// " CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username "
		// + dmUserid + " -password " + dmPassword;
		ExternalCommandAction eca = new ExternalCommandAction();
		eca.setWorkingDirectory(profilePath);
		eca.setCommand(command);
		eca.execute();

		System.out.println("SSLCertificateGetter successfully");

		return eca.getReturnCode();
	}

	public static void main(String[] args) throws Exception {
		String profilePath = args[0];
		String wasUserId = args[1];
		String wasPasswd = args[2];
		String sslReturnCode = SSLCertificateGetter.getSSLCertificate(
				profilePath, wasUserId, wasPasswd);
		if (sslReturnCode.equals("0")) {
			System.out.println("Success!!");
		} else {
			System.out.println("fail!");
			throw new Exception("Authentication fail");
		}
	}

}
