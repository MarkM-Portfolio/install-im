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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class DMValidator {
	
	public static final Logger logger = LogUtil.getLogger(DMValidator.class);
	protected static JavaPropertyGetter propertyGetter;
	
	public static String getDMInfo(String profilePath,String pyPath,String dmUserid,String dmPassword) throws Exception{
		
		try{
			String tempDir = profilePath + "/bin";
			File file = new File(tempDir + "/dminfo.properties");
			boolean deletedResult = deleteFile(file);
			logger.log(Level.INFO, "The file " + file.getAbsolutePath() +  " is deleted: " + deletedResult);
			File pyfile = new File(pyPath);
			if(!pyfile.exists()){
				return "2";
			}
			
			String command = null;
			String commands[] = new String[12];
			ExternalCommandAction eca = new ExternalCommandAction();
			eca.setWorkingDirectory(profilePath);
						
			if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformShortType())) {
//				pyPath = "\"" + pyPath + "\"";
//				command = "\"" + profilePath + "/bin/wsadmin.bat\" -conntype SOAP -lang jython -username " + dmUserid + " -password " + dmPassword + " -f " +  pyPath + " \"" + tempDir + "/dminfo.properties\"";
//				eca.setCommand(command);
//				logger.log(Level.INFO, "Get DM Info command : " + command);
//				logger.log(Level.INFO, "Get DM Info command : " + "\"" + profilePath + "/bin/wsadmin.bat\" -conntype SOAP -lang jython -username " + dmUserid + " -password " + "PASSWORD_REMOVED" + " -f " +  pyPath + " \"" + tempDir + "/dminfo.properties\"");
				commands[0] = profilePath + "/bin/wsadmin.bat";
			} else {
				commands[0]= profilePath + "/bin/wsadmin.sh";
			}
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
				commands[11]= tempDir + "/dminfo.properties";
				eca.setArrayCmds(true);
				eca.setCommands(commands);
				
				logger.log(Level.INFO, "Get DM Info command : " + commands[0] + " " +  commands[1] + " " + commands[2] + " " + commands[3] + " "
						+ commands[4]  + " " + commands[5] + " " +  commands[6] + " " + commands[7] + " " 
						+ "PASSWORD_REMOVED" + " " + commands[9]  + " " + commands[10] + " " +  commands[11]);
						
			eca.execute();
			
			if(!file.exists()){
				return "3";
			}
			
			return eca.getReturnCode();
		}catch(Exception e){
			throw e;
		}
	}

	public static String detectDMCellName(){
		
		return propertyGetter.getProperty("dm.cellanme");
	}
	
	public static boolean deleteFile(File file){
		if(file.exists()){
			return file.delete();
		}
		return false;
	}
	
	public static void main(String[] args) {
		try {
			String result = DMValidator.getDMInfo("C:/Program Files/IBM/WebSphere/AppServer/profiles/Dmgr01", "wasAuthValidate.py", "wasadmin", "passw0rd");
			System.out.println("Result: " + result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(detectClusters());
//		System.out.println(detectDMNode());
//		System.out.println(detectNodes());

	}
}
