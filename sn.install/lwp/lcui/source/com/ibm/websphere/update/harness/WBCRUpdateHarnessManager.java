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
 * Class: WBCRUpdateHarnessManager
 * Abstract: Enables an eFix/FixPack update to be applied to a WBCR Standalone product.
 * 
 *
 * History 1.1, 3/25/05
 *
 * 
 * 
 * 
 * 25-Mar-2005 Initial Version
 **********************************************************************************************************/

public class WBCRUpdateHarnessManager extends WPUpdateHarnessManager {

	public WBCRUpdateHarnessManager(String productDir){		
      super( productDir );
	}

   protected UpdateStrategy getUpdateStrategy( String productDir ) {
      return new WBCRUpdateStrategy( productDir );
   }
}
