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
package com.ibm.lconn.ant.task.util;

import java.io.File;
import java.io.FileFilter;
import java.text.MessageFormat;

public class XSLTTransformerFileFilter implements FileFilter {
	private String sourceVersion = "2.0.0.1", destVersion = "2.5";

	public XSLTTransformerFileFilter(String sourceVersion, String destVersion){
		setSourceVersion(sourceVersion);
		setDestVersion(destVersion);
	}
	private final String POST_FIX_FORMAT = "-update-{0}-{1}.xsl";

	public String getPostfix() {
		String string = MessageFormat.format(POST_FIX_FORMAT, sourceVersion
				.replace(".", ""), destVersion.replace(".", ""));
		return string;
	}

	public boolean accept(File pathname) {
		String postfix = getPostfix();
		String name = pathname.getName();
		return name.endsWith(postfix);
	}

	public void testFF() throws Exception {
		accept(new File("."));
	}

	public static void main(String[] args) throws Exception {
		new XSLTTransformerFileFilter("2.0.0.1", "2.0.1").testFF();
	}

	public String getDestVersion() {
		return destVersion;
	}

	public void setDestVersion(String destVersion) {
		this.destVersion = destVersion;
	}

	public String getSourceVersion() {
		return sourceVersion;
	}

	public void setSourceVersion(String sourceVersion) {
		this.sourceVersion = sourceVersion;
	}
}
