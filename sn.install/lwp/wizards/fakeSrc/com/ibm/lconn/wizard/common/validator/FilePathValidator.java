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


/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class FilePathValidator extends AbstractValidator {
	private static final int msgCount = 3;

	private static int count = 0;

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
		String val = eval(filepath);
		count++;
		if (count == 1) {
			logError("file_not_exist", val);
			return 1;
		}
		if (count == 2) {
			logError("dir_not_exist", val);
			return 2;
		}
		if (count == 3) {
			logError("path_not_exist", val);
			return 3;
		}
		return 0;
	}

}
