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
import com.ibm.lconn.wizard.cluster.data.ClusterFeatureController;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.Util;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class ClusterRestartTask extends AbstractTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.cluster.task.AbstractTask#run(java.lang.String)
	 */
	@Override
	public int run() {
		String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;

		String featureSelected = DataPool.getValue(wizardId,
				ClusterConstant.INPUT_clusterFeatureSelection);
		String[] features = Util.delimStr(featureSelected);

		String clusterNames = "";
		for (String feature : features) {
			String featureName = ClusterFeatureController.getFeatureName(feature);
			clusterNames += LCUtil.getClusterName(featureName).trim() + ",";
		}

		DataPool.setValue(wizardId,
				ClusterConstant.INPUT_clusterFeatureClusterName, clusterNames);

		CommandExec ct = CommandExec
				.create(ClusterConstant.TASK_RESTART_CLUSTER);

		File f = new File(LOG_DIR);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdir();
		}

		log = LOG_DIR + Constants.FS + "restart-clusters" + ".log";
		ct.setOutput(log);
		return ct.execute();
	}

	public static void main(String[] a) {
		AbstractTask t = new ClusterRestartTask();
		System.out.println(t.run());
	}
}
