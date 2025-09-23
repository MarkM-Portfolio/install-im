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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;

import com.ibm.lconn.common.util.Util;
import com.ibm.lconn.common.xml.XMLOperator;
import com.ibm.lconn.common.xml.XMLUtil;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class XMLReplace extends BaseTask {
	private String source, dest, listfile;

	public void execute() throws BuildException {
		File source = Util.getFile(getSource());
		File dest = Util.getFile(getDest());
		File templete = Util.getFile(getListfile());
		handleListFile(source, dest, templete);
	}

	private void handleListFile(File source, File dest, File templete) {
		log("Source: {0}. ", source.getAbsolutePath());
		log("Destination: {0}. ", dest.getAbsolutePath());
		
		try {
			File sourceTmpFile = File.createTempFile("LocalImport", source
					.getName());
			File destTmpFile = File
					.createTempFile("LocalImport", dest.getName());
			copyFile(source, sourceTmpFile, "&amp;", "@AND@", true);
			copyFile(dest, destTmpFile, "&amp;", "@AND@", true);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(templete)));
			String line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (!"".equals(line)) {
					execute(sourceTmpFile, destTmpFile, line);
				}
				line = br.readLine();
			}
			
			copyFile(sourceTmpFile, source, "@AND@", "&amp;", true);
			copyFile(destTmpFile, dest, "@AND@", "&amp;", true);
			
			log("Done.", "");
		} catch (Exception e) {
			log("Fail.", "");
			e.printStackTrace();
		}
	}
	
	private void execute(File source, File dest, String xpath) {
		log("XPath: {0}", xpath);
		XMLOperator oper = new XMLOperator();
		oper.setOutput(System.out);
		Document sourceDoc = oper.getDocument(source.getAbsolutePath());
		Document destDoc = oper.getDocument(dest.getAbsolutePath());
		oper.replace(sourceDoc, destDoc, xpath);
		try {
			XMLUtil.saveXMLPretty(dest.getAbsolutePath(),
					destDoc);
		} catch (Exception e) {
			log("Error: failed to save {0}. ");
		}
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getListfile() {
		return listfile;
	}

	public void setListfile(String listfile) {
		this.listfile = listfile;
	}
	
}
