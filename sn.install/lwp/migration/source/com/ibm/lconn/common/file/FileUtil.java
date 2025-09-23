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
import java.io.OutputStream;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class FileUtil {
	private static FileOperator fo = new FileOperator();


	public static int copy(File source, File dest, boolean override) {
		return fo.copy(source, dest, override);
	}
	
	public static int copy(String source, String dest, boolean override) {
		return fo.copy(source, dest, override, false);
	}
	
	public static boolean delete(File dest){
		return fo.delete(dest);
	}
	
	public static boolean delete(String dest){
		return delete(getAbsoluteFile(dest));
	}
	
	public static boolean mkdir(File dest) {
		return fo.mkdir(dest);
	}
	
	public static boolean mkdir(String dest) {
		return mkdir(getAbsoluteFile(dest));
	}
	
	public static boolean touch(File file) {
		return fo.touch(file);
	}
	
	public static boolean touch(String file) {
		return touch(getAbsoluteFile(file));
	}
	
	public static void setOutput(OutputStream os){
		fo.setOutput(os);
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		setOutput(sb);
		mkdir("cc/1/1.1");
		mkdir("dd/1/1.2");
		touch("cc");
		touch("dd");
		int copy = copy("cc", "dd", true);
		System.out.println(copy);
		System.out.println(sb);
		// FileOperator.copyDir("c:/t1", "c:/t2");
	}
	public static void setOutput(StringBuffer sb) {
		fo.setOutput(sb);
	}

	public static File getRelative(String parentFolder, String relativePath) {
		File sourcefolderFile = ((parentFolder == null) ? new File(".")
				: new File(parentFolder));
		return new File(sourcefolderFile, relativePath);
	}

	public static File getAbsoluteFile(String filepath) {
		return new File(filepath).getAbsoluteFile();
	}
}
