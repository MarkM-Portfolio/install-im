/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.ant.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class LoadProperties extends BaseTask {
	private List<FileSet> fsList;
	public FileSet createFileSet(){
		FileSet fs = new FileSet();
		getFileSetList().add(fs);
		return fs;
	}
	private List<FileSet> getFileSetList() {
		if(fsList==null) fsList = new ArrayList<FileSet>();
		return fsList;
	}
	@Override
	public void execute() throws BuildException {
		Iterator<FileSet> iter = getFileSetList().iterator();
		while (iter.hasNext()) {
			FileSet fs = (FileSet) iter.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++) {
				File pFile = new File(fs.getDir(getProject()), files[i]);
				log("Loading properties from: {0}", pFile.getAbsolutePath());
				Property property = new Property();
				property.setProject(getProject());
				property.setFile(pFile);
				property.execute();
			}
		}
	}
	public static void main(String[] args) {
		LoadProperties lp = new LoadProperties();
		lp.setProject(new Project());
		FileSet set = lp.createFileSet();
		set.setDir(new File("d:/IBM/WebSphere/LC201"));
		set.setIncludes("ConfigEngine/profiles/*/wkplc_comp.properties");
		lp.execute();
	}
}
