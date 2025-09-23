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

import java.net.*;
import java.util.*;


/* 
 * ClassName: XMLTransformManager
 * Abstract: Defines the contract for XML transformation updates
 * 
 * History 1.1, 11/14/02
 *
 * 30-Aug-2002 Initial Version
 */
 
public interface XMLTransformManager {
	
	/**
	 * Return the list of transformer files
	 */
	public List getTransformDataFiles();
	
	/**
	 * Return a list of transform URL identifiers
	 */
	public List getTransformDataURLS();
	
	/**
	 * Return the list of search patterns to 
	 * collect the appropriate data files
	 */
	public List getSearchPatterns();
		
	/**
	 * Return the map containing data source
	 * and transformer associations
	 */
	public Map getTransformMap();

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
