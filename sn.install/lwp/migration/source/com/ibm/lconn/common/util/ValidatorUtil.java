/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.util;

import java.io.File;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class ValidatorUtil {
	public static final String INVALID_INPUT_GENERAL = "$L(com.ibm.wps.install.CommonMsg, Input.invalid.general.simple)";
	public static final String INVALID_INPUT_FILE = "$L(com.ibm.wps.install.CommonMsg, Input.invalid.file.location)";
	public static final String INVALID_INPUT_PORT = "$L(com.ibm.wps.install.CommonMsg, Input.invalid.port)";

	private static final String TYPE_NUMBER = "NUMBER";
	private static final String TYPE_NORMAL = "NORMAL";
	private static final String TYPE_IP = "IP";
	private static final String TYPE_HOSTNAME = "HOSTNAME";
	private static final String TYPE_FILE = "FILE";
	private static final String TYPE_PORT = "PORT";
	
	public static boolean validate(String value, String type){
		if (TYPE_PORT.equals(type))
			return ValidatorUtil.validPort(value);
		if (TYPE_FILE.equals(type))
			return ValidatorUtil.validFile(value);
		if (TYPE_HOSTNAME.equals(type))
			return ValidatorUtil.validHostname(value);
		if (TYPE_IP.equals(type))
			return ValidatorUtil.validIP(value);
		if (TYPE_NORMAL.equals(type))
			return ValidatorUtil.validNormalStr(value);
		if (TYPE_NUMBER.equals(type))
			return ValidatorUtil.validNumber(value);
		return validNonEmpty(value);
	}
	
	public static boolean validNonEmpty(String str){
		return !"".equals(str.trim());
	}
	public static boolean validNumber(String port) {
		return port.matches("\\d+");
	}

	public static boolean validNormalStr(String str) {
		boolean matches = str.matches("[A-Za-z0-9]+");
		return matches;
	}

	public static boolean validIP(String ip){
		String IP_VALID = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		return ip.matches(IP_VALID);
	}
	
	public static boolean validPort(String port){
		try{
			int portVal = Integer.parseInt(port);
			return portVal>1 && portVal<65536;
		}catch (Exception e) {
			return false;
		}
	}
	
	public static boolean validHostname(String hostName){
		hostName = hostName.trim();
		if(hostName.length()>63) return false;
		if(validIP(hostName)) return true;
		String evalStr = hostName;
		if(evalStr.endsWith(".")) return false;
		while(evalStr.length()!=0){
			int dotIndex = evalStr.indexOf('.');
			if(dotIndex==-1) return validHost(evalStr);
			else{
				boolean valid = validHost(evalStr.substring(0, dotIndex));
				if(!valid) return false;
				evalStr = evalStr.substring(dotIndex+1);
			}
		}
		return true;
	}
	
	public static boolean validHost(String host){
		if(host.matches("\\d*")) return false;
		return host.matches("[a-zA-Z0-9][a-zA-Z0-9\\-]*");
	}

	public static boolean validFile(String fileLocation) {
		File f = new File(new File(fileLocation.trim()).getAbsolutePath());
		if (f.exists())
			return true;
		if (f.getParent() == null)
			return false;
		if (isWin()) {
			String name = f.getName();
			if (!name.matches("[^ \\t][^\\/:*?\"<>|]+")) {
				return false;
			}
			return validFile(f.getParent());
		}
		return true;
	}

	private static boolean isWin() {
		return -1 != System.getProperty("os.name").toLowerCase().indexOf("win");
	}

}
