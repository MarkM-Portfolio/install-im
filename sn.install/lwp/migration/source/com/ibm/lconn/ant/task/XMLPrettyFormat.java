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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.w3c.dom.Document;

import com.ibm.lconn.common.xml.XMLOperator;

/**
 * @author jjzhang@cn.ibm.com
 * 
 */
public class XMLPrettyFormat extends BaseTask {
	private String todir;
	private List<XMLFileset> fsList;
	public class XMLFileset extends FileSet {
		
	}
	
	public XMLFileset createXMLFileset(){
		XMLFileset fileset = new XMLFileset();
		getFsList().add(fileset);
		return fileset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		List<XMLFileset> fsList = getFsList();
		for (Iterator<XMLFileset> iterator = fsList.iterator(); iterator.hasNext();) {
			XMLFileset fs = (XMLFileset) iterator.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++) {
				File pFile = new File(fs.getDir(getProject()), files[i]);
				formatFile(pFile);
			}
		}
	}

	/**
	 * @param file
	 */
	private void formatFile(File file) {
		File destFile = new File(getTodir(), file.getName());
		XMLOperator operator = new XMLOperator();
		operator.setOutput(getOutput());
		try {
			Document doc = operator.getDocument(file.getAbsolutePath());
			if(doc==null) throw new IOException();
			operator.saveXMLPretty(destFile.getAbsolutePath(), doc);
			log("{0} is pretty formatted. ", file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			log("Warning: {0} is not well formated XML. Directly copied to the destination. ", file.getAbsolutePath());
			copyFile(file, destFile, true);
		}
	}
	
	private void copyFile(File sourceFile, File toFile, boolean overwrite) {
		Copy copy = new Copy();
		copy.setProject(getProject());
		copy.setTaskName(getTaskName());
		copy.setTofile(toFile);
		copy.setFile(sourceFile);
		copy.setOverwrite(overwrite);
		copy.setVerbose(true);
		copy.execute();
	}

	public String getTodir() {
		return todir;
	}

	public void setTodir(String todir) {
		this.todir = todir;
	}

	private List<XMLFileset> getFsList() {
		if(fsList==null) fsList = new ArrayList<XMLFileset>();
		return fsList;
	}
}
