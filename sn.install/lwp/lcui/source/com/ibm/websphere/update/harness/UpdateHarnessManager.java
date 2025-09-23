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

import java.util.*;
import java.io.*;


/**********************************************************************************************************
 * Class: UpdateHarnessManager.java
 * Abstract: Defines the basic manager interface to enable Non-WAS product updates
 * 
 *     harness()
 * 
 *
 * Component Name: WAS.ptf
 * Release: ASV50X
 * 
 * History 1.3, 3/13/03
 *
 * 
 * 
 * 
 * 01-Feb-2003 Initial Version
 **********************************************************************************************************/

public interface UpdateHarnessManager {
	
	/**
	 * 
	 * Harnesses the target product to the current update strategy
	 * 
	 */
	public boolean harness();	
	
	/**
	 * 
	 * Cleans up any harness directories and files that were created on error
	 * 
	 */
	public boolean cleanUp();
	
}
