/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */
package com.ibm.websphere.update.transforms;

import java.io.*;
import java.util.*;


/* 
 * ClassName: ConfigInstancesDescriptor
 * Abstract: Contains WebSphere configuration instances information.  
 * Maintains the set of nodeNames and their corresponding config locations
 * 
 * 
 * History 1.1, 11/14/02
 *
 * 30-Aug-2002 Initial Version
 */
 
/**
 *  
 */
public class ConfigInstancesDescriptor {

	private static final String WS_INSTANCE_DB = "wsinstance.config";
	private static String configInstanceFilePath;
	private String wsRoot;
	
	private static Map configInstancesMap;
	private static Set configInstanceLocations;
	private static Set configInstanceNodeNames;

	public ConfigInstancesDescriptor(String wsRoot){
		this.wsRoot = wsRoot;
		configInstanceFilePath = wsRoot + File.separator + "properties" + File.separator + WS_INSTANCE_DB;		
		
	}

	/**
	 * @param configInstances  the configInstances to set
	 * @uml.property  name="configInstances"
	 */
	public static void setConfigInstancesMap(Map cMap) {
		configInstancesMap = cMap;
	}

	/**
	 * @param configInstanceLocations  the configInstanceLocations to set
	 * @uml.property  name="configInstanceLocations"
	 */
	public static void setConfigInstanceLocations(Set cLocations) {
		configInstanceLocations = cLocations;
	}

	/**
	 * @param configInstanceNodeNames  the configInstanceNodeNames to set
	 * @uml.property  name="configInstanceNodeNames"
	 */
	public static void setConfigInstanceNodeNames(Set cNodeNames) {
		configInstanceNodeNames = cNodeNames;
	}

	/**
	 * @return  the configInstances
	 * @uml.property  name="configInstances"
	 */
	public static Map getConfigInstancesMap() {
		return configInstancesMap;
	}

	/**
	 * @return  the configInstanceLocations
	 * @uml.property  name="configInstanceLocations"
	 */
	public static Set getConfigInstanceLocations() {
		return configInstanceLocations;
	}

	/**
	 * @return  the configInstanceNodeNames
	 * @uml.property  name="configInstanceNodeNames"
	 */
	public static Set getConfigInstanceNodeNames() {
		return configInstanceNodeNames;
	}

	public static void parseConfigInstanceFile(String configInstanceFile)
		throws IOException {

		//locate was instances - query wsinstance.config
		Properties configInstances = new Properties();

		InputStream fs = new FileInputStream(getConfigInstanceFile());
		configInstances.load(fs);
		fs.close();

		Set numInstances = configInstances.keySet();
		Iterator instanceIter = numInstances.iterator();
		Set wsInstanceNodeNames = new HashSet();
		Set wsInstanceLocations = new HashSet();
		Map wsInstancesMap = new HashMap();

		while (instanceIter.hasNext()) {
			String wsNodeName = (String) instanceIter.next();
			String wsInstanceLocation =
				(String) configInstances.getProperty(wsNodeName);
			wsInstanceNodeNames.add(wsNodeName);
			wsInstanceLocations.add(wsInstanceLocation);
			wsInstancesMap.put(wsNodeName, wsInstanceLocation);
		}

		setConfigInstanceNodeNames(configInstanceNodeNames);
		setConfigInstanceLocations(configInstanceLocations);
		setConfigInstancesMap(configInstancesMap);

	}

	public static String getConfigInstanceFile() {
		return configInstanceFilePath;
	}

	public static boolean configInstanceFileExists() {
		File configInstanceFile = new File(getConfigInstanceFile());
		return configInstanceFile.exists();
	}

}
