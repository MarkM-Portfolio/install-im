/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.cluster.ui;

import com.ibm.lconn.wizard.cluster.detect.AbstractDetector;
import com.ibm.lconn.wizard.cluster.detect.LocalProfileDetector;
import com.ibm.lconn.wizard.cluster.task.AbstractTask;
import com.ibm.lconn.wizard.cluster.task.AddPrimaryNodeTask;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class UnitTest {
	public static void main(String[] a) {
		// for task
		AbstractTask t = new AddPrimaryNodeTask();
		// exitCode=0 success, else failed.
		int exitCode = t.run();
		
		// for detect
		AbstractDetector d = new LocalProfileDetector();
		String[] result = d.getResult();
		
		
	}
}
