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
package com.ibm.lconn.wizard.common;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author Jun Jing Zhang
 * 
 */
public class FileOperator {

	private static Logger logger = LogUtil.getLogger(FileOperator.class);

	public static boolean copyFile(String source, String dest) {
		File sourceFile = new File(source);
		File destFile = new File(dest);
		source = sourceFile.getAbsolutePath();
		dest = destFile.getAbsolutePath();
		String actionName = Constants.FILE_COPY_FILE;
		return execute(actionName, source, dest);
	}
	
	public static boolean removeFile(String filepath){
		filepath = getAbsolutePath(filepath);
		return execute(Constants.FILE_REMOVE_FILE, filepath);
	}

	private static String getAbsolutePath(String filepath) {
		return new File(filepath).getAbsolutePath();
	}
	
	public static boolean mkdir(String folderpath){
		String absolutePath = getAbsolutePath(folderpath);
		return execute(Constants.FILE_MAKE_DIR, absolutePath);
	}
	
	public static boolean rmdir(String folderpath){
		String path = getAbsolutePath(folderpath);
		return execute(Constants.FILE_REMOVE_DIR, path);
	}
	
	public static boolean copyDir(String folderpath){
		String path = getAbsolutePath(folderpath);
		return execute(Constants.FILE_COPY_DIR, path);
	}
	
	public static File getRelative(String parentFolder, String relativePath){
		 File sourcefolderFile = ((parentFolder==null)? new File("."):new File(parentFolder));
		 return new File(sourcefolderFile, relativePath);
	}
	

	private static boolean execute(String actionName, String... para){
		try {
			String cmd = getCommand(actionName, para);
			boolean result = executeCommand(cmd);
			return result;
		} catch (Exception e) {
			logger.log(Level.WARNING, MessageUtil.getKeyAll("FileOperation",actionName), e);
			return false;
		}
	}
	

	private static boolean executeCommand(String cmd) throws IOException,
			InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(Util.delimStr(cmd));
		pb.redirectErrorStream(true);
		Process p;
		boolean succeed = false;
		p = pb.start();
		p.waitFor();
		if (p.exitValue() == 0) {
			succeed = true;
		}
		return succeed;
	}

	private static String getCommand(String actionName, String... param) {
		String osType = CommonHelper.getPlatformType();
		String cmd;
		if (Constants.OS_WINDOWS.equals(osType)) {
			String cmdKey = MessageUtil.getKeyAll(Constants.PREFIX_GLOBAL_SCRIPT, actionName,
					osType);
			cmd = MessageUtil.getSettingWithParameter(cmdKey,
					quote(param[0]), quote(param[1]));
		} else {
			String cmdKey = MessageUtil.getKeyAll(Constants.PREFIX_GLOBAL_SCRIPT, actionName,
					Constants.OS_OTHER);
			cmd = MessageUtil.getSettingWithParameter(cmdKey, param[0], param[1]);
		}
		return cmd;
	}

	private static String quote(String source) {
		return "\"" + source + "\"";
	}

}
