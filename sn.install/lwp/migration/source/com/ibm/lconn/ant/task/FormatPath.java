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

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class FormatPath extends BaseTask {
	private String path, property, newSymbol, oldSymbol;
	@Override
	public void execute() throws BuildException {
		String path = getPath();
		String propertyName = getProperty();
		String newSymbol = getNewSymbol();
		String oldSymbol = getOldSymbol();
		String value = path.replace(oldSymbol, newSymbol);
		setProperty(propertyName, value);
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getNewSymbol() {
		return newSymbol;
	}
	public void setNewSymbol(String newSymbol) {
		this.newSymbol = newSymbol;
	}
	public String getOldSymbol() {
		return oldSymbol;
	}
	public void setOldSymbol(String oldSymbol) {
		this.oldSymbol = oldSymbol;
	}
}
