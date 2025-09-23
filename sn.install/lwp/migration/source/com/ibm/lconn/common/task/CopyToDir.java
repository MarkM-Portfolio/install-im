/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import com.ibm.lconn.ant.task.BaseTask;

public class CopyToDir extends BaseTask{
	public static void main(String[] args) {
		CopyToDir ctd = new CopyToDir();
		ctd.setProject(new Project());
		FileSet fs = new FileSet();
		fs.setDir(new File("c:/temp/work/export/config"));
		fs.setIncludes("**/*.xml");
		ctd.addFileSet(fs);
		ctd.setTodir(new File("c:/temp/work/import/level0"));
		ctd.setOutput(System.out);
		ctd.execute();
	}
	List<FileSet> fsList = new ArrayList<FileSet>();
	private File todir;
	public File getTodir() {
		return todir;
	}
	public void setTodir(File todir) {
		this.todir = todir;
	}
	public void addFileSet(FileSet fs){
		getFileSetList().add(fs);
	}
	private List<FileSet> getFileSetList() {
		return fsList;
	}
	@Override
	public void execute() throws BuildException {
		for (FileSet fs : getFileSetList()) {
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] files = ds.getIncludedFiles();
			for (String file : files) {
				File f = new File(fs.getDir(getProject()), file);
				File parentFile = f.getParentFile();
				if(parentFile.getAbsolutePath().equals(getTodir().getAbsolutePath())){
					continue;
				}else{
					copyfile(f, getTodir());
				}
			}
		}
	}
	private void copyfile(File source, File dest) {
		Copy copy = new Copy();
		copy.setProject(getProject());
		copy.setTaskName(getTaskName());
		copy.setFile(source);
		copy.setTodir(getTodir());
		copy.setVerbose(false);
		copy.execute();
	}
	
}
