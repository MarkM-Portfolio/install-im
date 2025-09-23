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
import com.ibm.lconn.wizard.cluster.data.FeatureInfo;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class SetWasVariable extends AbstractTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.cluster.task.AbstractTask#run(java.lang.String)
	 */
	@Override
	public int run() {
		String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;
		String featureName = DataPool.getValue(wizardId,
				ClusterConstant.INPUT_RUNNING_FEATURE);
		FeatureInfo featureInfo = LCUtil.getFeature(featureName);
		String scope = DataPool.getValue(wizardId,
				ClusterConstant.INPUT_clusterDMCellName)
				+ "," + featureInfo.getNodeName();

		DataPool.setValue(wizardId, ClusterConstant.INPUT_PROFILE_HOME, LCUtil
				.getWASProfileHome());
		DataPool.setValue(wizardId, ClusterConstant.INPUT_scope, scope);

		CommandExec ct = CommandExec
				.create(ClusterConstant.TASK_SET_WS_VARIABLE_TO_DM);

		File f = new File(LOG_DIR);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdir();
		}

		log = LOG_DIR + Constants.FS + "setWASVariable-" + featureName
				+ ".log";
		ct.setOutput(log);
		return ct.execute();
	}
}
