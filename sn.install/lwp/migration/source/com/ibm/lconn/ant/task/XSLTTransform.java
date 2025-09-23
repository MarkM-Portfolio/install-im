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
package com.ibm.lconn.ant.task;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.Project;

import com.ibm.lconn.common.util.Util;

public class XSLTTransform extends BaseTask {
	private String xslt = "";

	private String source = "";

	private String dest = "";

	private boolean overwrite = true;

	private boolean failonerror = false;

	private boolean silent = false;

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public void execute() {
		String xsl = getXslt();
		String source = getSource();
		String dest = getDest();
		File xsltFile = Util.getFile(xsl);
		File sourceFile = Util.getFile(source);
		File destFile = Util.getFile(dest);
		log("XSLT transformation: ");
		log("\tXSLT: {0}", xsltFile.getAbsolutePath());
		log("\tSource: {0}", sourceFile.getAbsolutePath());
		log("\tDestination: {0}", destFile.getAbsolutePath());
		if (!xsltFile.exists()) {
			fail("XSLT transformation skipped. File not exist: {0}. ", xsltFile
					.getAbsolutePath());
		}
		if (!sourceFile.exists()) {
			fail("XSLT transformation skipped. File not exist: {0}. ",
					sourceFile.getAbsolutePath());
		}
		if (destFile.exists()) {
			if (isOverwrite()) {
				boolean deleted = destFile.delete();
				if (!deleted) {
					fail(
							"XSLT transformation skipped. Failed to delete file: {0}. ",
							destFile.getAbsolutePath());
				}
			} else {
				fail(
						"XSLT transformation skipped. Warning: File already exists: {0}. ",
						destFile.getAbsolutePath());
			}
		}
		
		File sourceTmpFile = null;
		File destTmpFile = null;
		try {
			sourceTmpFile = File.createTempFile("LocalImport", sourceFile
					.getName());
			destTmpFile = File
					.createTempFile("LocalImport", destFile.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		copyFile(sourceFile, sourceTmpFile, "&amp;", "@AND@", true);
		
		// Create transformer factory
		TransformerFactory factory = TransformerFactory.newInstance();
		// factory.setAttribute("indent-number", new Integer(2));
		try {
			// Use the factory to create a template containing the xsl file
			Templates template = factory
					.newTemplates(new StreamSource(xsltFile));
			// Use the template to create a transformer
			Transformer xformer = template.newTransformer();
			xformer.setParameter("indent-number", new Integer(2));
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			// Prepare the input file
			Source source_ = new StreamSource(sourceTmpFile);
			Result result = new StreamResult(destTmpFile);
			xformer.transform(source_, result);

			if (destFile.getName().startsWith("businessCardInfo") || 
					destFile.getName().startsWith("profileEdit") || 
					destFile.getName().startsWith("profileDetails") || 
					destFile.getName().startsWith("searchResults")){
				log("\tCopyFile {0}", destFile.getName());
				copyFile(destTmpFile, destFile, "@AND@", "&", true);
			}else{
				copyFile(destTmpFile, destFile, "@AND@", "&amp;", true);
			}
		} catch (Exception e) {
			log("Failed to execute XSLT transformation for Exception. ");
			e.printStackTrace();
			log(e.getMessage(), Project.MSG_ERR);
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

	public String getXslt() {
		return xslt;
	}

	public void setXslt(String xslt) {
		this.xslt = xslt;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

}
