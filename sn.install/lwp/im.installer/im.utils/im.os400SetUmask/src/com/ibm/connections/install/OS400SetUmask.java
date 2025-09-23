/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2013, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.connections.install;

//OS400_Enablement
//On OS400, need to set the umask to 000, otherwise the WAS QEJBSVR user will 
//not be authorized to write in some dynamically created DIRs.
import com.ibm.cic.common.core.api.utils.PlatformUtils;
import java.io.File;
import java.io.PrintWriter;

public class OS400SetUmask {

	public static void main(String[] args) {
	}
	
	public void run(String[] args, PrintWriter writer) {
		// OS400_Enablement 
		// OS400 support, to set the umask to 000, otherwise the WAS QEJBSVR user will
		// not be able to write in some dynamically created Dirs.
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			PlatformUtils.setUmask(0);
		}
	}
}
