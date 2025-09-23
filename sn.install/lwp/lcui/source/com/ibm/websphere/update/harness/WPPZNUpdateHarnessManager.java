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
 * Class: WPCPUpdateHarnessManager.java
 * Abstract: Enables an eFix/FixPack update to be applied to a PZN Standalone product.
 * 
 *
 * History 1.1, 3/02/05
 *
 * 
 * 
 * 
 * 02-Mar-2005 Initial Version
 **********************************************************************************************************/

public class WPPZNUpdateHarnessManager extends WPUpdateHarnessManager {

	public WPPZNUpdateHarnessManager(String productDir){		
      super( productDir );
	}

   protected UpdateStrategy getUpdateStrategy( String productDir ) {
      return new WPPZNUpdateStrategy( productDir );
   }
}
