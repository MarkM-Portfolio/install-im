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
import java.io.InputStreamReader;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class Linux32AppSupportCheck {
	
	private final ILogger log = IMLogger.getLogger(Linux32AppSupportCheck.class);
	
	public boolean linux32AppSupport(String app) {
		
		String osName = System.getProperty("os.name");
		String arch = System.getProperty("os.arch").toLowerCase();
		if (osName.startsWith("Windows"))
			return true;
		if (osName.toLowerCase().indexOf("aix") != -1 || arch.indexOf("s390") != -1){
			log.info("running on zLinux or AIX, no need to check 32bit library.");
			return true;
		}
		
		boolean result = false;
		log.info("Linux32AppSupportCheck app : " + app);
		try {
			String[] cmd = new String[] {"sh","-c", "cat /etc/redhat-release" };
			Process ps = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line);
			String release = sb.toString();
			String version = "";
			if(!"".equals(release)){
				version = release.split(" ")[6].split("\\.")[0];
				log.info("Linux32AppSupportCheck os version : " + version);
			}
			log.info("Linux32AppSupportCheck os releae : " + release);
			ps.waitFor();
			ps.destroy();
			
			if("7".equals(version)){ // only redhat 7 do the 32 bit app support check
				cmd = new String[] {"sh","-c", "chmod +x " + app};
				ps = Runtime.getRuntime().exec(cmd);
				ps.waitFor();
				ps.destroy();
				cmd = new String[] {"sh","-c", app};
				ps = Runtime.getRuntime().exec(cmd);
				br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
				sb = new StringBuffer();
				while ((line = br.readLine()) != null)
					sb.append(line);
				ps.waitFor();
				log.info("Linux32AppSupportCheck exit code : " + ps.exitValue());
				if(ps.exitValue() == 0)
					result = true;
				ps.destroy();
			} else {
				result = true;
			}
		} catch (Exception e) {
			log.error(e);
			log.error("Linux32AppSupportCheck unknown error : " + e.getMessage());
		}
		return result;
	}
}
