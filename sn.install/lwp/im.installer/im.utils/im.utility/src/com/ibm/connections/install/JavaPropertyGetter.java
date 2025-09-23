package com.ibm.connections.install;
/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class JavaPropertyGetter {
	private File file;
	private static Properties properties;
	
	public JavaPropertyGetter() {
		super();
	}

	public JavaPropertyGetter(File file){
		super();
		this.file = file;
		 properties = new Properties();
		try {
			properties.load(new FileInputStream(this.file));
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}

	public String getProperty(String name){
		
		return properties.getProperty(name);
	}

}
