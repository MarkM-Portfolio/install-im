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
import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class WasSecurityValidator {
	
	public static final String ADMIN_SECURITY_ERROR="no admin security";
	public static final String APP_SECURITY_ERROR="no application security";
	private static boolean isAdminSecurity = false;
	private static boolean isJava2Security = false;
	private static final ILogger log = IMLogger.getLogger(com.ibm.connections.install.WasSecurityValidator.class);
	
	public static boolean validateAdminSecurity(String profilePath){
		boolean securityEnabled = false;
		PortalValidation validator = PortalValidateAction.getValidator();
		securityEnabled = validator.getWASSecurityEnabled(profilePath);
	
		return securityEnabled;
	}
	
	public static boolean validateAppSecurity(String profilePath,String pyPath){
		boolean result = false;
		
		String tempDir = profilePath + "/bin";
		log.info("before transfer : " + tempDir);
		String proFileDir = transferPath(tempDir + "/WasAppSecurity.properties");
		
		log.info("property path : " + proFileDir);
		
		File file = new File(proFileDir);
		deleteFile(file);
		
		File pyfile = new File(pyPath);
		if(!pyfile.exists()){
			return false;
		}
		
		String extension = null;
		String command = null;
		String commands[]=null;
		ExternalCommandAction eca = new ExternalCommandAction();
		eca.setWorkingDirectory(profilePath);
		
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			pyPath = "\"" + pyPath + "\"";
			command =  "\"" + profilePath + "/bin/wsadmin.bat\" -conntype NONE -lang jython -f " + pyPath + " \"" + proFileDir + "\"";
			eca.setCommand(command);
			log.info("Validate application security command : " + command );
		} else if (osName.toLowerCase().startsWith("os/400")) {
		// OS400_Enablement
			commands = new String[8];
			commands[0]= profilePath + "/bin/wsadmin";
			commands[1]= "-conntype";
			commands[2]= "NONE";
			commands[3]= "-lang";
			commands[4]= "jython";
			commands[5]= "-f";
			commands[6]= pyPath;
			commands[7]= tempDir + "/WasAppSecurity.properties";
			eca.setArrayCmds(true);
			eca.setCommands(commands);
			log.info("Validate application security command : " + commands.toString());
		} else {
			commands = new String[8];
			commands[0]= profilePath + "/bin/wsadmin.sh";
			commands[1]= "-conntype";
			commands[2]= "NONE";
			commands[3]= "-lang";
			commands[4]= "jython";
			commands[5]= "-f";
			commands[6]= pyPath;
			commands[7]= tempDir + "/WasAppSecurity.properties";
			eca.setArrayCmds(true);
			eca.setCommands(commands);
			log.info("Validate application security command : " + commands.toString());
		}
		
		eca.execute();
		
		boolean isResultExist = file.exists();
		log.info("Security result file exist : " + isResultExist);
		
		if(!file.exists()){
			return false;
		}
		JavaPropertyGetter propertyGetter = new JavaPropertyGetter(file);
		String appsecurity = propertyGetter.getProperty("was.appsecurity");
		String adminsecurity = propertyGetter.getProperty("was.adminsecurity");
		String java2security = propertyGetter.getProperty("was.java2security");
		
		if("true".equals(appsecurity)){
			result = true;
		}
		
		if("true".equals(adminsecurity)){
			isAdminSecurity = true;
		}
		
		if ("true".equals(java2security)) {
			isJava2Security = true;
		}
		return result;
	}
	
	public static boolean getAdminSecurity(){
		return isAdminSecurity;
	}
	
	public static boolean getJava2Security() {
		return isJava2Security;
	}
	
	public static boolean deleteFile(File file){
		if(file.exists()){
			return file.delete();
		}
		return false;
	}
	
	public static String transferPath(String path) {
		return path.replace("\\","/");
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
