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
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.command.LogAnalysis;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class AddPrimaryNodeTask extends AbstractTask {

	private String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;

	public int run() {
		String feature = DataPool.getValue(wizardId,
				ClusterConstant.INPUT_RUNNING_FEATURE);
		DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterNodeProfile,
				LCUtil.getFeature(feature).getProfileName());

		writeProperties(feature, ClusterConstant.TASK_ADD_FIRST_NODE);

		CommandExec ct = CommandExec
				.create(ClusterConstant.TASK_ADD_FIRST_NODE);

		File f = new File(LOG_DIR);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdir();
		}

		log = LOG_DIR + Constants.FS + "action-cluster-setup-" + feature
				+ ".log";
		ct.setOutput(log);
		ct.setReplace(new String[] { DataPool.getValue(
				ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_clusterDMWasPassword) });

		int exitCode = ct.execute();

		if (exitCode == 0) {
			Pattern[] errors = new Pattern[] {
					Pattern.compile(".*error.*", Pattern.CASE_INSENSITIVE),
					Pattern.compile(".*fail.*", Pattern.CASE_INSENSITIVE) };
			exitCode = LogAnalysis.analyze(log, 2, null, errors, null)
					.getExecState();
		}

		return exitCode;
	}

}
