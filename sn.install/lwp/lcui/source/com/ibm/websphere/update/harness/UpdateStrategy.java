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

package com.ibm.websphere.update.harness;

import java.io.*;
import java.util.*;


/**
 * Class: UpdateStrategy.java Abstract: Defines the basic strategy interface to execute for enabling updates conformsToStrategy() executeStrategy() Component Name: WAS.ptf Release: ASV50X History 1.3, 3/13/03 01-Feb-2003 Initial Version
 */

public interface UpdateStrategy {
	
	/**
	 * 
	 * Determines the validity of the target product against
	 * the defined strategy framework.
	 * 
	 */
	public boolean conformsToStrategy();
	
	
	/**
	 * Defines the steps to execute and harness the target product
	 * into the current update strategy
	 * 
	 */
	public void executeStrategy();	


	/**
	 * 
	 * Cleans up any harness directories and files that were created on error
	 * 
	 */
	public boolean cleanUp();
	
	/**
	 * 
	 * Exception handler methods
	 * 
	 */
	public int numConsumedExceptions();

	/**
	 * @param consumedExceptions
	 * @uml.property  name="consumedExceptions"
	 */
	public void setConsumedExceptions(List consumedExceptions);

	/**
	 * @return
	 * @uml.property  name="consumedExceptions"
	 */
	public List getConsumedExceptions();
	
	
}

