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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Convert '\' to '/' for use by ant
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class SlashConvert extends BaseTask {
	private String sourceProperty, targetProperty;
	public String getSourceProperty() {
		return sourceProperty;
	}
	public void setSourceProperty(String sourceProperty) {
		this.sourceProperty = sourceProperty;
	}
	@Override
	public void execute() throws BuildException {
		String source = getSourceProperty();
		String dest = getTargetProperty();
		Project p = getProject();
		String val = p.getProperty(source);
		val = val.replace('\\', '/');
		setProperty(dest, val);
	}
	public String getTargetProperty() {
		return targetProperty;
	}
	public void setTargetProperty(String targetProperty) {
		this.targetProperty = targetProperty;
	}
}
