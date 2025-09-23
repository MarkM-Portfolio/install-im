/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class Resultpanel {

	private static final String PLUGIN_ID = "com.ibm.connections.install.resultPanel";
    public final static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
    ILogger logger = IMLogger.getLogger(getClass().getCanonicalName());
    
    public void run(String[] args) throws CoreException {
    	logger.info("Resultpanel - enter ... " );
    	String installLogPath = args[0];
    	String cfyPath = args[1];
    	logger.info("Resultpanel - installLogPath: " + installLogPath);
    	logger.info("Resultpanel - cfyPath: " + cfyPath);
    	
    	delete_removed_feature(cfyPath);
    	replacePassword(cfyPath);
    	
    	//Replace PWD for cfg_update.py
    	String cfgUpdatePath = cfyPath.replace("cfg.py","cfg_update.py");
    	File file = new File(cfgUpdatePath);
    	if (file.exists()){
    		replacePassword(cfgUpdatePath); 
    	}
    	
    	File f = new File(args[0].trim());
    	IStatus status = new Status(IStatus.INFO, PLUGIN_ID, 0, readLastLine(f), null);
		throw new CoreException(status);
    }
    
    private static void delete_removed_feature(String path){
    	try {
    		StringBuilder content = new StringBuilder();
    		BufferedReader br = new BufferedReader(new FileReader(path));
    		BufferedReader br2 = new BufferedReader(new FileReader(path));
    		String line= null, tmp=null;
    		int flag = 0;
    		
			while ((line = br.readLine()) != null ){
				flag = 0;
				tmp = br2.readLine();
				if(line.matches("^\"(activities|blogs|profiles|dogear|communities|rte|ccm|forums|wikis|mobile|mobile.admin|moderation|metrics).*$")) {
					flag = 1;
					while (!(tmp = br2.readLine()).equals("},"))
						if(tmp.contains("'replace':'remove'"))
							flag = 2;
				}
				
				if(flag == 2)
					while(!(line = br.readLine()).equals("},")) {
					//if 'replace':'none' exists, skip these lines
					}
				else if (flag == 1){
					//if no 'replace':'none', add these lines
					content.append(line+"\n");
					while((line = br.readLine())!=null){
						content.append(line+"\n");
						if(line.equals("},")) 
							break;
					}
				}
				else if (flag == 0)
					//if not in the feature configuration part, add normally
					content.append(line+"\n");
			}
			br.close();
			br2.close();
			
			FileOutputStream fs = new FileOutputStream(path);
			fs.write(content.toString().getBytes());
			fs.close();
    	}
    	catch(FileNotFoundException e){
    		e.printStackTrace();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
	private void replacePassword(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			StringBuilder content = new StringBuilder();
			int fstColon, secColon;
			String password = null;
			while (br.ready() != false) {
				String line = br.readLine();
				if(line != null && line.contains("connectionsAdminPassword")){
					fstColon = line.indexOf("\"", line.indexOf(":"));
					secColon = line.indexOf("\"", fstColon + 1);
					password = line.substring(fstColon + 1, secColon);
					if(password != null && !password.trim().equals("") &&  !password.trim().startsWith("{xor}")){
						String xorPassword = com.ibm.connections.install.Util.xor(password);
						line = line.replaceFirst(password, xorPassword);
						logger.info("Resultpanel - connectionsAdminPassword : "+ xorPassword);
					}
				}
				if(line != null && line.contains("dbPassword")){
					fstColon = line.indexOf("\"", line.indexOf(":"));
					secColon = line.indexOf("\"", fstColon + 1);
					password = line.substring(fstColon + 1, secColon);
					if(password != null && !password.trim().equals("") &&  !password.trim().startsWith("{xor}")){
						String xorPassword = com.ibm.connections.install.Util.xor(password);
						line = line.replaceFirst(password, xorPassword);
						logger.info("Resultpanel - dbPassword : "+ xorPassword);
					}
				}
				if(line != null && line.contains("gcdDbPassword")){
					fstColon = line.indexOf("\"", line.indexOf(":"));
					secColon = line.indexOf("\"", fstColon + 1);
					password = line.substring(fstColon + 1, secColon);
					if(password != null && !password.trim().equals("") &&  !password.trim().startsWith("{xor}")){
						String xorPassword = com.ibm.connections.install.Util.xor(password);
						line = line.replaceFirst(password, xorPassword);
						logger.info("Resultpanel - gcdDbPassword : "+ xorPassword);
					}
				}
				if(line != null && line.contains("osDbPassword")){
					fstColon = line.indexOf("\"", line.indexOf(":"));
					secColon = line.indexOf("\"", fstColon + 1);
					password = line.substring(fstColon + 1, secColon);
					if(password != null && !password.trim().equals("") &&  !password.trim().startsWith("{xor}")){
						String xorPassword = com.ibm.connections.install.Util.xor(password);
						line = line.replaceFirst(password, xorPassword);
						logger.info("Resultpanel - osDbPassword : "+ xorPassword);
					}
				}
				content.append(line).append("\n");
			}
			br.close();
			FileOutputStream fs = new FileOutputStream(path);
			fs.write(content.toString().getBytes());
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public static String readLastLine(File file){
		String result = null, line = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) result = line;
			br.close();
		}
		catch(IOException e){
		}
		return result;
	}
	
}