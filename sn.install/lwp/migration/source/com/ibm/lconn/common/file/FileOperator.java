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
package com.ibm.lconn.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ibm.lconn.common.operator.LogOperator;

/**
 * @author Jun Jing Zhang
 * 
 */
public class FileOperator extends LogOperator {

	private final String FAILED_TO_COPY_FROM_0_TO_1_SOURCE_DOES_NOT_EXIST = "Failed to copy from {0} to {1}, source does not exist";
	private final String FAILED_TO_DELETE_FILE_0 = "Failed to delete file: {0}";
	private final String FAILED_TO_DELETE_0_THIS_LOCATION_DOES_NOT_EXIST = "Failed to delete: {0}. This location does not exist. ";
	private final String FAILED_TO_CREATE_DIR_0 = "Failed to create dir: {0}";
	private final String FAILED_TO_CREATE_DIR_0_DIRECTORY_ALREADY_EXISTS = "Failed to create dir: {0}. Directory already exists";
	private final String IGNORE_COPY_FROM_0_TO_1 = "Ignore copy from {0} to {1}";
	private final String COMPLETE_COPY_FROM_0_TO_1 = "Complete copy from {0} to {1}";
	private final String FAILED_TO_COPY_FROM_0_TO_1 = "Failed to copy from {0} to {1}";
	private final int CACHE_LENGTH = 8192;
	private final int FAIL_FILE_ALREADY_EXIST = 1;
	private final int SUCCESS = 0;
	private final int FAIL_TO_DELETE = 2;
	private final int FAIL_SOURCE_NOT_EXIST = 3;
	private final int FAIL_DEST_NOT_EXIST = 4;
	private final int FAIL_IO = 5;
	private final int FAIL_COMPLETE_WITH_ERROR = 6;
	
	public int copy(String sourceStr, String destStr, boolean override) {
		File source = new File(getAbsolutePath(sourceStr));
		File dest = new File(getAbsolutePath(destStr));
		return copy(source, dest, override);
	}
	
	public int copy(String sourceStr, String destStr, boolean override, boolean childOnly) {
		return copy(sourceStr, destStr, override);
	}

	public int copy(File source, File dest, boolean override) {
		if (!source.exists()) {
			sourceNotExist(source, dest);
			return FAIL_SOURCE_NOT_EXIST;
		} else {
			if (!dest.exists()) {
				if (source.isDirectory()) {
					if (mkdir(dest)) {
						return copy(source, dest, override);
					} else {
						failCopy(source, dest);
						return FAIL_FILE_ALREADY_EXIST;
					}
				} else {
					return copyFile(source, dest, override);
				}
			} else {
				if (source.isFile()) {
					if (dest.isDirectory()) {
						return copyFile(source,
								new File(dest, source.getName()), override);
					} else {
						if (!override) {
							ignoreCopy(source, dest);
							return SUCCESS;
						} else {
							if (delete(dest)) {
								return copyFile(source, dest, override);
							} else {
								failCopy(source, dest);
								return FAIL_TO_DELETE;
							}
						}
					}
				} else {
					if(!dest.exists()){
						mkdir(dest);
					}
					if (dest.isDirectory()) {
						// folder -> folder
						return copyFolder(source, dest, override);
					} else {
						// folder -> file
						if (!override) {
							ignoreCopy(source, dest);
							return SUCCESS;
						} else {
							if (delete(dest)) {
								return copy(source, dest, override);
							} else {
								failCopy(source, dest);
								return FAIL_TO_DELETE;
							}
						}
					}
				}
			}
		}
	}

	private int copyFolder(File source, File dest, boolean override) {
		File[] listFiles = source.listFiles();
		boolean hasError = false;
		for (int i = 0; i < listFiles.length; i++) {
			File child = listFiles[i];
			int copy = copy(child, new File(dest, child.getName()), override);
			if (copy != SUCCESS)
				hasError = true;
		}
		completeCopy(source, dest);
		if (hasError)
			return FAIL_COMPLETE_WITH_ERROR;
		return SUCCESS;
	}

	public boolean mkdir(File dest) {
		if (dest.exists()) {
			log(FAILED_TO_CREATE_DIR_0_DIRECTORY_ALREADY_EXISTS, dest
					.getAbsolutePath());
			return false;
		}
		if (dest.mkdirs()) {
			return true;
		} else {
			log(FAILED_TO_CREATE_DIR_0, dest.getAbsolutePath());
			return false;
		}
	}

	public boolean delete(String dest) {
		return delete(FileUtil.getAbsoluteFile(dest));
	}

	public boolean delete(File dest) {
		if (!dest.exists()) {
			log(FAILED_TO_DELETE_0_THIS_LOCATION_DOES_NOT_EXIST, dest
					.getAbsolutePath());
			return false;
		}
		if (dest.delete()) {
			return true;
		} else {
			log(FAILED_TO_DELETE_FILE_0, dest.getAbsolutePath());
			return false;
		}
	}

	private void sourceNotExist(File source, File dest) {
		log(FAILED_TO_COPY_FROM_0_TO_1_SOURCE_DOES_NOT_EXIST, source, dest);
	}

	private int copyFile(File source, File dest, boolean override) {
		File parent = dest.getParentFile();
		if (!parent.exists()) {
			if (!mkdir(parent)) {
				failCopy(source, dest);
				return FAIL_DEST_NOT_EXIST;
			}
		}
		try {
			copyFile(source, dest);
			completeCopy(source, dest);
			return SUCCESS;
		} catch (Exception e) {
			failCopy(source, dest);
			return FAIL_IO;
		}
	}

	private void copyFile(File source, File dest) throws FileNotFoundException,
			IOException {
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(dest);
		byte[] cache = new byte[CACHE_LENGTH];
		int a = fis.read(cache);
		while (a != -1) {
			fos.write(cache, 0, a);
			a = fis.read(cache);
		}
		fos.flush();
		fis.close();
		fos.close();
	}

	private void failCopy(File source, File dest) {
		log(FAILED_TO_COPY_FROM_0_TO_1, source.getAbsolutePath(), dest
				.getAbsolutePath());
	}

	private void ignoreCopy(File source, File dest) {
		log(IGNORE_COPY_FROM_0_TO_1, source.getAbsolutePath(), dest
				.getAbsolutePath());
	}

	private void completeCopy(File source, File dest) {
		log(COMPLETE_COPY_FROM_0_TO_1, source.getAbsolutePath(), dest
				.getAbsolutePath());
	}

	private String getAbsolutePath(String filepath) {
		return new File(filepath).getAbsolutePath();
	}

	public boolean mkdir(String folderpath) {
		return mkdir(FileUtil.getAbsoluteFile(folderpath));
	}

	public boolean touch(File file) {
		try {
			boolean re = File.createTempFile("touch_", "", file).createNewFile();
			if(re){
				log("Complete touch {0}", file.getAbsolutePath());
				return true;
			}else{
				log("Failed to touch {0}. ", file.getAbsolutePath());
				return false;
			}
		} catch (IOException e) {
			log("Failed to touch {0}. ", file.getAbsolutePath());
			return false;
		}
	}

	public boolean touch(String file) {
		return touch(FileUtil.getAbsoluteFile(file));
	}
	

}
