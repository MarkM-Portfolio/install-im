/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class JavaPropertyGetter {
	
	private static final ILogger log = IMLogger
			.getLogger(com.ibm.connections.install.JavaPropertyGetter.class);
	
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
			for(Object key:properties.keySet()){
				log.info("JavaPropertyGetter: " + key +" : " + properties.getProperty(key.toString()));
			}
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
