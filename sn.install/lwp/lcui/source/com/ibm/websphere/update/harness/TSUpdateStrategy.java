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
//import java.io.*;
//import java.net.*;

/**********************************************************************************************************
 * Class: TSUpdateStrategy
 * Abstract: Enables TS to be updated within the current PTF strategy as a stand-alone product.
 * 
 *
 * History %I, %G
 *
 * 
 * 
 * 
 * 05-Apr-2006 Initial Version
 **********************************************************************************************************/

public class TSUpdateStrategy extends WPAbstractUpdateStrategy {

	//********************************************************
	//  Method Definitions
	//********************************************************

	public TSUpdateStrategy(String productDir) {
		super(productDir);
	}

	//********************************************************
	//  Method Definitions
	//********************************************************

	public Map getHarnessFiles() {

		debug("getHarnessFiles()...entered");
      harnessFiles = new HashMap();
      return harnessFiles;

	}
}
