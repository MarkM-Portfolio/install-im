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

import java.io.File;

import com.ibm.lconn.wizard.common.Constants;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class FilePathValidator extends AbstractValidator {
	public static final int MODE_FILE = 1;
	public static final int MODE_DIR = 2;
	public static final int MODE_ALL = 3;
	
	protected String filepath;
	private int mode;
	private String enabled = null;
	
	public FilePathValidator(String filepath, int mode) {
		super();
		this.filepath = filepath;
		this.mode = mode;
	}
	
	public FilePathValidator(String filepath, String mode) {
		this(filepath, Integer.parseInt(mode));
	}
	
	public FilePathValidator(String filepath, String mode, String enabled) {
		this(filepath, mode);
		this.enabled = enabled;
	}
	
	public int validate() {
		if(enabled!=null && !eval(enabled).equals(Constants.BOOL_TRUE)) {
			return 0;
		}
		
		String val = eval(filepath);
		val = val == null ? "" : val;
		File f = new File(val);
		switch(mode) {
		case 1:
			if(!f.exists() || !f.isFile()) {
				logError("file_not_exist", val);
				return 1;
			}
			break;
		case 2:
			if(!f.exists() || !f.isDirectory()) {
				logError("dir_not_exist", val);
				return 2;
			}
			break;
		case 3:
			if(!f.exists()) {
				logError("path_not_exist", val);
				return 3;
			}
			break;
		}
		return 0;
	}
	
}
