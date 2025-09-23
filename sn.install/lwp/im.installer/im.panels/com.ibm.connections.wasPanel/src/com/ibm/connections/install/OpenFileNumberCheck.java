/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
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

public class OpenFileNumberCheck {
	public final static int WINDOWS = -999;
	public final static int GREAT_THAN_8192 = 1; 
	public final static int LESS_THAN_8192 = -1;
	public final static int UNKNOWN_ERROR = -2;
	
	private final ILogger log = IMLogger.getLogger(com.ibm.connections.install.OpenFileNumberCheck.class);
	
	public int getOpenFileNumberCheck() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows"))
			return WINDOWS;

		try {
			//String[] cmd = new String[] {"sh","-c", "ulimit -n > a.txt"};
			// OS400_Enablement 
			// For os400 use sh or qsh, they both work but return different values. 
			// According to the OS team's suggestion, choose to use sh for os400 too.
			String[] cmd = new String[] {"sh","-c", "ulimit -n"};
			Process ps = Runtime.getRuntime().exec(cmd);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					ps.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String result = sb.toString();
			log.info("OpenFileNumberCheck : " + result);
			if("unlimited".equals(result))
				return GREAT_THAN_8192;
			int number = Integer.parseInt(result);
			if(number >= 8192)
				return GREAT_THAN_8192;
			else
				return LESS_THAN_8192;
			
		} catch(NumberFormatException ne) {
			log.info("OpenFileNumberCheck parse number error : " + ne.getMessage());
//			ne.printStackTrace();
			return UNKNOWN_ERROR;
		}catch (Exception e) {
			log.info("OpenFileNumberCheck unknown error : " + e.getMessage());
//			e.printStackTrace();
			return UNKNOWN_ERROR;
		}
	}
}
