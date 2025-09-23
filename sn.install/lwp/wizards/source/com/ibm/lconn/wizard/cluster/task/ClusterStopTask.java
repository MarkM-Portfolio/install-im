/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * 
 */
package com.ibm.lconn.wizard.cluster.task;

import java.io.File;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class ClusterStopTask extends AbstractTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.cluster.task.AbstractTask#run(java.lang.String)
	 */
	@Override
	public int run() {
		String feature = DataPool.getValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_RUNNING_FEATURE);

		String clusterName = LCUtil.getClusterName(feature);

		DataPool.setValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_clusterFeatureClusterName, clusterName);

		CommandExec ct = CommandExec.create(ClusterConstant.TASK_STOP_CLUSTER);

		File f = new File(LOG_DIR);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdir();
		}

		log = LOG_DIR + Constants.FS + "stopCluster-" + clusterName + ".log";
		ct.setOutput(log);
		return ct.execute();
	}
}
