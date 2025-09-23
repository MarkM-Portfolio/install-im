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
package com.ibm.lconn.wizard.common.validator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class OptionalTaskValidator extends AbstractValidator {
	private static final Logger logger = LogUtil.getLogger(OptionalTaskValidator.class);
	private static final Map<String, String> taskFileMap = Constants.optionalTaskFileMap;

	private String task;

	public OptionalTaskValidator(String task) {
		this.task = task;
	}

	public int validate() {
		reset();
		String val = eval(task);
		int rc = 0;
		String[] tasks = val.split(",");
		for (int i = 0; i < tasks.length; i++) {
			String task = tasks[i];
			
			// exclude mark manager task
			if(Constants.LDAP_OPTIONAL_TASK_MARK_MANAGER.equals(task)
					|| CommonHelper.isEmpty(task)) {
				continue;
			}
			
			String source = DataPool.getValue(System.getProperty(Constants.LCONN_WIZARD_PROP),
					this.task + "." + task + ".path");
			
			if(!fileExists(source)) {
				logError("file_not_exist", source);
				return 1;
			}
			
			String requiredFile = Constants.TDI_WORK_DIR + Constants.FS + taskFileMap.get(task);
			try {
				copyFile(source, requiredFile);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "cluster.severe.cannot_copy_file", e);
				logError("cannot_copy_file", new File(source).getAbsolutePath(), new File(requiredFile).getAbsolutePath());
				return 2;				
			}
//			if (requiredFile != null) {
//				File f = new File(Constants.TDI_WORK_DIR + Constants.FS
//						+ requiredFile);
//				if (!f.exists()) {
//					title = getTite("missing_files");
//					rc++;
//					if (message == null || "".equals(message)) {
//						message = "";
//					} else {
//						message = message + Constants.CRLF;
//					}
//					message = message
//							+ getMessage("missing_files", MessageUtil.getLabel(task), f.getAbsolutePath());
//				}				
//			}
		}
		return rc;
	}
	
	private void copyFile(String source, String target) throws IOException{
		
		// skip of source file and target file are pointing to the same file
		File sourceFile = new File(source);
		File targetFile = new File(target);
		if(sourceFile.getAbsoluteFile().equals(targetFile.getAbsoluteFile())) {
			return;
		}
		// copy file
		byte[] buffer = new byte[1024*8];
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BufferedInputStream(new FileInputStream(source));
			os = new BufferedOutputStream(new FileOutputStream(target));
			int count=-1;
			while((count = is.read(buffer))!=-1) {
				os.write(buffer, 0, count);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	private boolean fileExists(String filename) {
		File file = new File(filename);
		return file.exists() && file.isFile();
	}

}
