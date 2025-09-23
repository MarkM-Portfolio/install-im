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
import org.apache.tools.ant.types.FileSet;
import org.w3c.dom.Document;

import com.ibm.lconn.ant.task.XMLReplaceHandler.SourceFileset;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.xml.XMLOperator;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class XMLFormatter extends BaseTask{
	private String todir;
	private List<File> xmls;
	private List<FileSet> filesets;
	public class XMLFileset extends FileSet{
	}
	public XMLFileset createXMLFileset() {
		XMLFileset set = new XMLFileset();
		getFilesets().add(set);
		return set;
	}
	public String getTodir() {
		return todir;
	}
	public void setTodir(String todir) {
		this.todir = todir;
	}
	public List<File> getXmls() {
		if(xmls==null) xmls=new ArrayList<File>();
		return xmls;
	}
	public void setXmls(List<File> xmls) {
		this.xmls = xmls;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		initFiles();
		XMLOperator xo = new XMLOperator();
		xo.setOutput(getOutput());
		Iterator<File> iter = getXmls().iterator();
		while (iter.hasNext()) {
			File xml = (File) iter.next();
			Document doc = xo.getDocument(xml.getAbsolutePath());
			File file = new File(todir, xml.getName());
			String todir = getTodir();
			if(ObjectUtil.isEmpty(todir)){
				file = xml;
			}
			try {
				xo.saveXMLPretty(file.getAbsolutePath(), doc);
			} catch (FileNotFoundException e) {
				log("Failed to save file: {0}", file.getAbsolutePath());
				e.printStackTrace();
			} catch (IOException e) {
				log("Failed to save file: {0}", file.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 */
	private void initFiles() {
		Iterator<FileSet> iter = getFilesets().iterator();
		while (iter.hasNext()) {
			FileSet fs = (FileSet) iter.next();
			boolean isSource = fs instanceof SourceFileset;
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++) {
				File pFile = new File(fs.getDir(getProject()), files[i]);
				log("Loading {0}", pFile);
				String name = pFile.getName();
				String ext = ".xml";
				if (name.toLowerCase().endsWith(ext)) {
					getXmls().add(pFile);
				}
			}
		}
	}
	public List<FileSet> getFilesets() {
		if(filesets==null) filesets=new ArrayList<FileSet>();
		return filesets;
	}
	public void setFilesets(List<FileSet> filesets) {
		this.filesets = filesets;
	}
	
	
}
