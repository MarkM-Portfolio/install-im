/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common.depcheck;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.commerce.depchecker.common.ChildProcess;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class DB2Util {
	public static final String GLOBAL_REG = "/var/db2/global.reg";
	public static final String GLOBAL_REG_HP = "/var/opt/db2/global.reg";
	public static final String STRINGS_EXECUTABLE = "/usr/bin/strings";
	public static final String DB2LEVEL_EXECUTABLE = "/bin/db2level";
	public static final String DB2_VERSION_PATTERN = "\"DB2 v\\d+\\.\\d+\\.\\d+\\.\\d+\"";
	
	public static String getDB2Loc() {
		String regFilePath = null;
		File f = new File(GLOBAL_REG);
		if (f.exists() && f.isFile()) {
			regFilePath = GLOBAL_REG;
		}
		f = new File(GLOBAL_REG_HP);
		if (f.exists() && f.isFile()) {
			regFilePath = GLOBAL_REG_HP;
		}

		try {
			ChildProcess cp = new ChildProcess(STRINGS_EXECUTABLE + " "
					+ regFilePath);
			
			if (cp.run() == 0) {
				String out = cp.getStdOut();
				String[] paths = out.split("\n");
				for(String path : paths) {
					String db2home = path.trim();
					// valid db2home must be an existing directory
					if(!(new File(db2home).exists() && new File(db2home).isDirectory())) {
						continue;
					}
					// db2home should not end with /sqllib
					if(db2home.endsWith("/sqllib")) {
						continue;
					}
					if(db2home.endsWith("/das")) {
						int end = db2home.lastIndexOf("/das");
						return db2home.substring(0, end);
					} else {
						return db2home;
					}
				}				
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return "";
	}
	
	public static String getDB2Version(String db2loc) {
		try {
			ChildProcess cp = new ChildProcess(db2loc + DB2LEVEL_EXECUTABLE);
			if(cp.run() == 0) {
				String out = cp.getStdOut();
				Pattern p = Pattern.compile(DB2_VERSION_PATTERN);
				Matcher m = p.matcher(out);
				if(m.find()) {
					String vStr = m.group();
					return vStr.substring(6, vStr.length()-1);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) {
		String installLoc = getDB2Loc();
		System.out.println("DB2Home = " + installLoc);
		System.out.println("DB2Version = " + getDB2Version(installLoc));
	}
}
