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

package com.ibm.lconn.wizard.cluster.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class CopePYFileTask extends AbstractTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.cluster.task.AbstractTask#run()
	 */
	@Override
	public int run() {
		
		CommandExec ct = CommandExec.create(ClusterConstant.TASK_COPY_PY);

		File f = new File(LOG_DIR);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdir();
		}
		ct.setOutput(LOG_DIR + Constants.FS + "copy_py" + ".log");
		return ct.execute();
		
		
	}
	
	public boolean copyFolder(String oldPath, String newPath) {
		try {
			File newFolder = new File(newPath);
			File oldFolder = new File(oldPath);

			if (newFolder.getAbsolutePath().equalsIgnoreCase(
					oldFolder.getAbsolutePath())) {
				return true; // The new folder is the old folder, no need to copy.
			}

			if (!newFolder.exists() || !newFolder.isDirectory()) {
				newFolder.mkdirs();
			}

			String[] file = oldFolder.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {
					if (!copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i])) return false;
				}
			}
			return true;
		} catch (Exception e) {
			System.out.println("copy file from " + oldPath + " to " + newPath
					+ " fail with Exception.");
			e.printStackTrace();
			return false;
		}
	}
}
