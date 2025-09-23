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
 * Class: WASUpdateHarnessManager.java
 * Abstract: Enables an eFix/FixPack update to be applied to a WAS-lite/IHS/WAS-plugin product.
 * 
 *
 * History 1.1, 9/19/03
 *
 * 
 * 
 * 
 * 19-Sep-2003 Initial Version
 **********************************************************************************************************/

public class WPExpressUpdateHarnessManager extends WPUpdateHarnessManager {

	public WPExpressUpdateHarnessManager(String productDir){		
      super( productDir );
	}

   protected UpdateStrategy getUpdateStrategy( String productDir ) {
      return new WPExpressUpdateStrategy( productDir );
   }
}
