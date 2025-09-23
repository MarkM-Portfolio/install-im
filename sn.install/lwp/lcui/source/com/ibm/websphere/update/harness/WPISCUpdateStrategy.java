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
 * Class: WPISCUpdateStrategy.java
 * Abstract: Enables ISC to be updated within the current PTF strategy as a stand-alone product.
 * 
 *
 * History 1.1, 10/14/03
 *
 * 
 * 
 * 
 **********************************************************************************************************/

public class WPISCUpdateStrategy extends WPAbstractUpdateStrategy {

    public WPISCUpdateStrategy(String productDir) {
        super( productDir );
    }
    
    public Map getHarnessFiles() {
        debug("WPISCUpdateStrategy::getHarnessFiles()...entered");
        harnessFiles = new HashMap();
        return harnessFiles;
    }

}
