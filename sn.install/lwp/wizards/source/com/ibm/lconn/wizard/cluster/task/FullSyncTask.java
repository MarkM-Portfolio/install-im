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

package com.ibm.lconn.wizard.cluster.task;

import java.io.File;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class FullSyncTask extends AbstractTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.cluster.task.AbstractTask#run(java.lang.String)
	 */
	@Override
	public int run() {
		CommandExec ct = CommandExec
				.create(ClusterConstant.TASK_FULL_SYNC_NODES);

		File f = new File(LOG_DIR);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdir();
		}

		log = LOG_DIR + Constants.FS + "fullSync-nodes" + ".log";
		ct.setOutput(log);
		return ct.execute();
	}
	
	public static void main(String[] a) {
		AbstractTask t = new FullSyncTask();
		System.out.println(t.run());
	}
}
