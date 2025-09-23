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

import org.apache.tools.ant.BuildException;

public class OSTypeGetter extends BaseTask{
	private static String nameProperty;
	 
	@Override
	public void execute() throws BuildException {
		
		String name = getNameProperty();
		
		String osname = System.getProperty("os.name");
		if(osname.startsWith("Windows")){
			setProperty(name, "windows");
		}else{
			setProperty(name, "unix");
		}	
	}

	public static String getNameProperty() {
		return nameProperty;
	}

	public static void setNameProperty(String nameProperty) {
		OSTypeGetter.nameProperty = nameProperty;
	}
	
}
