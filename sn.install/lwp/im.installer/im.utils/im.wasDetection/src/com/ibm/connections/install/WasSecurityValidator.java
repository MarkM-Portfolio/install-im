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

import com.ibm.wps.depcheck.PortalValidation;

public class WasSecurityValidator {
	
	public static final String ADMIN_SECURITY_ERROR="no admin security";
	public static final String APP_SECURITY_ERROR="no application security";
	
	public static boolean validateAdminSecurity(String profilePath){
		boolean securityEnabled = false;
		PortalValidation validator = PortalValidateAction.getValidator();
		securityEnabled = validator.getWASSecurityEnabled(profilePath);
	
		return securityEnabled;
	}
	
	public static boolean validateAppSecurity(String profilePath,String configEnginePath){
		boolean result = false;
		
//		String tempDir = System.getProperty("tempDir");
		String tempDir = profilePath + "/bin";
		File file = new File(tempDir + "/WasAppSecurity.properties");
		deleteFile(file);
		
//		String profilePath = System.getProperty("profilePath");
//		String cdLocation = System.getProperty("cdLocation");
		
		String command = profilePath + "/bin/wsadmin.bat -conntype NONE -lang jython -f " + configEnginePath + "/ConfigEngine/config/was/wkplc_CheckAppSecurity.py " + tempDir + "/WasAppSecurity.properties";
		ExternalCommandAction eca = new ExternalCommandAction();
		eca.setWorkingDirectory(profilePath);
		eca.setCommand(command);
		eca.execute();
		System.out.println("app security : result "+eca.getReturnCode());
		JavaPropertyGetter propertyGetter = new JavaPropertyGetter(file);
		String appsecurity = propertyGetter.getProperty("was.appsecurity");
		
		if("true".equals(appsecurity)){
			result = true;
		}
		
		return result;
	}
	
	public static boolean deleteFile(File file){
		if(file.exists()){
			return file.delete();
		}
		return false;
	}
	
    public static void main(String[] args) {
    	
    	String profilePath = "C:\\PROGRA~1\\IBM\\WebSphere\\AppServer1\\profiles\\Dmgr01";
//    	String command = "C:\\Program Files\\IBM\\WebSphere\\AppServer1\\profiles\\Dmgr01/bin/retrieveSigners.bat CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username wasadmin -password password";
//    	System.out.println("ok");
////    	System.out.println(WasSecurityValidator.validateAdminSecurity(profilePath));
//    	System.out.println("test here");
    	System.out.println(WasSecurityValidator.validateAppSecurity(profilePath, "C:\\PROGRA~1\\IBM\\Connections"));
//    	
//    	DMValidator.getDMInfo(profilePath, "C:\\PROGRA~1\\IBM\\LotusConnections", "wasadmin", "password");
//    	System.out.println(DMValidator.validateHeapsize());
//    	System.out.println(DMValidator.validteNodesNumber());
//    	System.out.println(DMValidator.detectNodes());
//    	System.out.println(DMValidator.detectClusters());
    	
//    	System.out.println(SSLCertificateGetter.getSSLCertificate(profilePath, "wasadmin", "password"));
//    	System.out.println(WasInstalledFeaturesValidator.validate(profilePath,"news,homepage,search" ,"C:\\PROGRA~1\\IBM\\LotusConnections", "wasadmin", "password"));
    }

}
