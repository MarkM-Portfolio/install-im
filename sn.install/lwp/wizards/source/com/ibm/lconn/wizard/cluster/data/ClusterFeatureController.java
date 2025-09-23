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

package com.ibm.lconn.wizard.cluster.data;

import java.util.List;
import java.util.Map;

import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.ui.data.LCGroupController;

public class ClusterFeatureController implements LCGroupController {

	@SuppressWarnings("unchecked")
	public boolean enable(String option) {
		String taskType = DataPool.getValue(ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_clusterTask);
		String featureName = getFeatureName(option);
		// checkbox is only enable when feature is installed and not clustered
		Map<String, List<String>> map = (Map<String, List<String>>) DataPool
				.getComplexData(LCUtil.CONSTANTS_GLOBAL,
						LCUtil.FEATURE_CLUSTERED_NODES);
		FeatureInfo info = LCUtil.getFeature(featureName);
		if (info == null) {
			// if feature is not installed, disable checkbox
			return false;
		}
		List<String> clusteredNodes = map.get(featureName);

		// if node is already clustered, disable checkbox
		if(ClusterConstant.OPTION_clusterTaskCreate.equals(taskType)) {
			return clusteredNodes == null || clusteredNodes.isEmpty();
		} else if (ClusterConstant.OPTION_clusterTaskAdd.equals(taskType)) {
			return clusteredNodes != null && !clusteredNodes.contains(info.getNodeName());
		} else {
			return clusteredNodes != null && clusteredNodes.contains(info.getNodeName());
		}
	}

	public static String getFeatureName(String featureOption) {
		if (ClusterConstant.OPTION_featureAct.equals(featureOption)) {
			return Constants.FEATURE_ACTIVITIES;
		}
		if (ClusterConstant.OPTION_featureBlogs.equals(featureOption)) {
			return Constants.FEATURE_BLOGS;
		}
		if (ClusterConstant.OPTION_featureComm.equals(featureOption)) {
			return Constants.FEATURE_COMMUNITIES;
		}
		if (ClusterConstant.OPTION_featureDogear.equals(featureOption)) {
			return Constants.FEATURE_DOGEAR;
		}
		if (ClusterConstant.OPTION_featureProf.equals(featureOption)) {
			return Constants.FEATURE_PROFILES;
		}
		if (ClusterConstant.OPTION_featureHomepage.equals(featureOption)) {
			return Constants.FEATURE_HOMEPAGE;
		}
		return null;
	}

}
