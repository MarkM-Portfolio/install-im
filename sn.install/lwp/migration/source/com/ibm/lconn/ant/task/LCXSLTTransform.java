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

import com.ibm.lconn.ant.task.util.XSLTTransformerFileFilter;
import com.ibm.lconn.common.util.Util;

public class LCXSLTTransform extends BaseTask {
	private String xslt = "";
	private String source = "";
	private String dest = "";
	private boolean overwrite = true;
	private boolean silent = false;
	private String oldVersion = "", newVersion = "";

	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}

	public String getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public void execute() {
		XSLTTransformerFileFilter filter = new XSLTTransformerFileFilter(getOldVersion(), getNewVersion());
		String source2 = getXslt();
		File sourceFile = Util.getFile(source2);
		File[] xslts = sourceFile.listFiles(filter);
		
		for (int i = 0; i < xslts.length; i++) {
			String xsltName = xslts[i].getName();
			String name = xsltName.substring(0, xsltName.lastIndexOf("-update"));
			String xmlName = name+".xml";
			XSLTTransform trans = new XSLTTransform();
			trans.setXslt(xslts[i].getAbsolutePath());
			trans.setSource(new File(getSource(), xmlName).getAbsolutePath());
			trans.setDest(new File(getDest(), xmlName).getAbsolutePath());
			trans.setOverwrite(overwrite);
			trans.setFailonerror(isFailonerror());
			trans.setSilent(silent);
			trans.execute();
		}		
	}


	@Override
	public void log(String msg) {
		if (!isSilent())
			super.log(msg);
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
