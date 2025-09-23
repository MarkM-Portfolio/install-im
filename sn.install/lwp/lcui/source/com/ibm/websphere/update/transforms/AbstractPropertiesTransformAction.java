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
import java.net.*;
import java.io.*;

/* 
 * ClassName: AbstractPropertiesTransformAction
 * Abstract: An abstraction for transformation updates for property files.
 * 
 * History 1.1, 11/19/02
 *
 * 30-Aug-2002 Initial Version
 */

public abstract class AbstractPropertiesTransformAction implements Action {

	/**
	 * A singleton Properties instance
	 */
	public static Properties propsFile = new Properties();

	/**
	 * Returns a Properties handler as a point of entry for updates
	 */
	public static Properties getPropertiesHandler(){
		return propsFile;
	}
	
	/**
	 * Returns the transformer Map identifier
	 */
	public abstract Map getTransformMap();
	
	
	/**
	 * Returns the list of file identifiers
	 */
	public abstract List getFileIds();

}