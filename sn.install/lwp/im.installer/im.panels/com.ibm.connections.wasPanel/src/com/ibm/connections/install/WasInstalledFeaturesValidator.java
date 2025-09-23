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

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class WasInstalledFeaturesValidator {
	
	private static final ILogger log = IMLogger.getLogger(com.ibm.connections.install.WasInstalledFeaturesValidator.class);
	public static boolean validate(String profilePath,String selectedFeatures,String pyPath,String dmUserid,String dmPassword){
		boolean result = true;
		
		String tempDir = profilePath + "/bin";
		String proFileDir = WasSecurityValidator.transferPath(tempDir + "/lcInstalledFeatures.properties");
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
			command = "\"" + profilePath + "/bin/wsadmin.bat\" -conntype SOAP -lang jython -username \"" + dmUserid + "\" -password " + dmPassword + " -f " + pyPath + " \"" +  proFileDir + "\"";
			eca.setCommand(command);
			
			//for log
			String commandTemp = new String(command);
			String temp = commandTemp.replace(dmPassword, "PASSWORD_REMOVED");
			log.info("Check install features command : " + temp);
		} else if (osName.toLowerCase().startsWith("os/400")) {
		// OS400_Enablement
			command = profilePath + "/bin/wsadmin" + " -conntype SOAP -lang jython -username \"" + dmUserid + "\" -password " + dmPassword + " -f " + pyPath + " " +  tempDir + "/lcInstalledFeatures.properties";
			commands = new String[12];
			commands[0]= profilePath + "/bin/wsadmin";
			commands[1]= "-conntype";
			commands[2]= "SOAP";
			commands[3]= "-lang";
			commands[4]= "jython";
			commands[5]= "-username";
			commands[6]= dmUserid;
			commands[7]= "-password";
			commands[8]= dmPassword;
			commands[9]= "-f";
			commands[10]= pyPath;
			commands[11]= tempDir + "/lcInstalledFeatures.properties";
			eca.setArrayCmds(true);
			eca.setCommands(commands);
			
			//for log
			String commandTemp = new String(commands.toString());
			String temp = commandTemp.replace(dmPassword, "PASSWORD_REMOVED");
			log.info("Check install features command : " + temp);
		
		} else {
			command = profilePath + "/bin/wsadmin.sh" + " -conntype SOAP -lang jython -username \"" + dmUserid + "\" -password " + dmPassword + " -f " + pyPath + " " +  tempDir + "/lcInstalledFeatures.properties";
			commands = new String[12];
			commands[0]= profilePath + "/bin/wsadmin.sh";
			commands[1]= "-conntype";
			commands[2]= "SOAP";
			commands[3]= "-lang";
			commands[4]= "jython";
			commands[5]= "-username";
			commands[6]= dmUserid;
			commands[7]= "-password";
			commands[8]= dmPassword;
			commands[9]= "-f";
			commands[10]= pyPath;
			commands[11]= tempDir + "/lcInstalledFeatures.properties";
			eca.setArrayCmds(true);
			eca.setCommands(commands);
			
			//for log
			String commandTemp = new String(commands.toString());
			String temp = commandTemp.replace(dmPassword, "PASSWORD_REMOVED");
			log.info("Check install features command : " + temp);
		}
		
		eca.execute();
		
		boolean isFeaturesInstallFile = file.exists();
		log.info("features installed result file exist : " + isFeaturesInstallFile);
		
		if(!file.exists()){
			return false;
		}
		System.out.println(eca.getReturnCode());
		JavaPropertyGetter propertyGetter = new JavaPropertyGetter(file);
		String installedFeatures = propertyGetter.getProperty("installedFeatures");
		log.info("Was has installed features : " + installedFeatures);
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
