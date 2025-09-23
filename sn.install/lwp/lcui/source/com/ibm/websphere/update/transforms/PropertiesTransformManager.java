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

import java.util.*;
import java.io.*;


/* 
 * ClassName: PropertiesTransformManager
 * Abstract: Manages the transformation updates for properties files
 * 
 * Defines three major update actions
 * 
 *   ADD: This action adds a new key/value pair in an existing properties file.
 *   DELETE: This action deletes a specified key in an existing properties file.
 *   REPLACE: This action replaces an already existing value with a new value 
 *   that corresponds to the same key.
 * 
 * 
 * 
 * History 1.1, 11/19/02
 *
 * 30-Aug-2002 Initial Version
 */
 
public interface PropertiesTransformManager {
	
	/**
	 * Property update actions
	 */
	public static final int ADD = 0;
	public static final int DELETE = 1;
	public static final int REPLACE = 2;
	
	
	/**
	 * Return the map containing data source
	 * and transformer associations
	 */
	public Map getTransformMap();
		
	
	/**
	 * Return the map containing update instructions
	 * for the associated configuration file collection
	 */
	public Map getTransformDefinitionMap(String searchPattern);
		
		
	/**
	 * Return the list of search patterns to 
	 * collect the appropriate data files
	 */
	public List getSearchPatterns();

	/**
	 * Define the update associations
	 * between the data source and transformer
	 */
	public void defineUpdates();
	
	/**
	 * Prepare updates 
	 */
	public void loadUpdates();
	
	/**
	 * Process the transformations
	 */
	public boolean processUpdates();

}
