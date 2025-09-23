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

import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.property.Property;
import com.ibm.lconn.wizard.common.property.PropertyLoader;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public abstract class AbstractTask {
	protected String log = null;
	public static String LOG_DIR = Constants.LOG_ROOT + Constants.FS
			+ "Cluster";
	static {
		File dir = new File(LOG_DIR);
		if (!dir.exists())
			dir.mkdirs();
	}

	public abstract int run();

	public String getLog() {
		return log;
	}

	/**
	 * <code>
	 * For add first node. 
	 * <feature_name>.PrimaryNode=true
	 * <feature_name>.ClusterName=<cluster_name>
	 * <feature_name>.ClusterMemberName=<server_name>
	 * <feature_name>.DMgrHostName=<server_host_name>
	 * <feature_name>.DMgrSoapPort=<port_number>
	 * <feature_name>.DMgrCellName=<cell_name>
	 * <feature_name>.DMgrProfileName=<profile_name>
	 * 
	 * add subsequent node
	 * <feature_name>.PrimaryNode=false
	 * <feature_name>.ClusterName=<cluster_member_name> 
	 * <feature_name>.ClusterMemberName=<server_name>
	 * IMPORTANT: The cluster member name must be unique within the cluster. Otherwise, 
	 * clustering will not work. If you named the first cluster member server1, 
	 * name this cluster member server2, for example.
	 * <feature_name>.DMgrHostName=<server_host_name>
	 * <feature_name>.DMgrSoapPort=<soap_port_number>
	 * <feature_name>.DMgrCellName=<cell_name>
	 * <feature_name>.DMgrProfileName=<profile_name>
	 * </code>
	 */
	protected void writeProperties(String feature, String task) {
		String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;
		String propFile = DataPool.getValue(wizardId,
				ClusterConstant.INPUT_LC_HOME)
				+ Constants.FS
				+ "ConfigEngine"
				+ Constants.FS
				+ "profiles"
				+ Constants.FS
				+ LCUtil.getFeature(feature).getProfileName()
				+ Constants.FS + "wkplc_comp.properties";

		Property prop = PropertyLoader.load(propFile);

		if (ClusterConstant.TASK_ADD_FIRST_NODE.equals(task)
				|| ClusterConstant.TASK_ADD_SUBSEQUENT_NODE.equals(task)) {
			if (ClusterConstant.TASK_ADD_FIRST_NODE.equals(task)) {
				prop.setProperty(feature + ".PrimaryNode", "true");
			} else {
				prop.setProperty(feature + ".PrimaryNode", "false");
			}
			prop.setProperty(feature + ".ClusterMemberName", DataPool.getValue(
					wizardId, feature + ".memberName"));
			prop.setProperty(feature + ".ClusterName", LCUtil
					.getClusterName(feature));
			prop.setProperty(feature + ".DMgrHostName", DataPool.getValue(
					wizardId, ClusterConstant.INPUT_clusterDMHostName));
			prop.setProperty(feature + ".DMgrSoapPort", DataPool.getValue(
					wizardId, ClusterConstant.INPUT_clusterDMSoapPort));
			prop.setProperty(feature + ".DMgrProfileName", DataPool.getValue(
					wizardId, ClusterConstant.INPUT_clusterDMProfileName));

			prop.setProperty(feature + ".DMgrCellName", DataPool.getValue(
					wizardId, ClusterConstant.INPUT_clusterDMCellName));
		}

	}
}
