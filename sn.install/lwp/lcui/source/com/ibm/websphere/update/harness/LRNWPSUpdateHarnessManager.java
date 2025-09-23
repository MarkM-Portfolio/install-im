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

//import java.util.*;
//import java.io.*;


/**********************************************************************************************************
 * Class: LRNWPSUpdateHarnessManager
 * Abstract: Enables an eFix/FixPack update to be applied to LRNWPS product.
 * 
 *
 * History %I, %G
 *
 * 
 * 
 * 
 * 5-Apr-2006 Initial Version
 **********************************************************************************************************/

public class LRNWPSUpdateHarnessManager extends WPUpdateHarnessManager {

	public LRNWPSUpdateHarnessManager(String productDir){		
      super( productDir );
	}

   protected UpdateStrategy getUpdateStrategy( String productDir ) {
      return new LRNWPSUpdateStrategy( productDir );
   }
}
