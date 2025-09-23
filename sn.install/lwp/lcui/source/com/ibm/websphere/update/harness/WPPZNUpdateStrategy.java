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
import java.net.*;

/**********************************************************************************************************
 * Class: WPPZNUpdateStrategy.java
 * Abstract: Enables PZN to be updated within the current PTF strategy as a stand-alone product.
 * 
 *
 * History 1.1, 3/02/05
 *
 * 
 * 
 * 
 * 02-Mar-2005 Initial Version
 **********************************************************************************************************/

public class WPPZNUpdateStrategy extends WPAbstractUpdateStrategy {

	//********************************************************
	//  Method Definitions
	//********************************************************

	public WPPZNUpdateStrategy(String productDir) {
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
