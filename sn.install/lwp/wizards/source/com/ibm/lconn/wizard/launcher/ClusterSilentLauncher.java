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
package com.ibm.lconn.wizard.launcher;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.cluster.ui.TaskInterface;
import com.ibm.lconn.wizard.cluster.validation.DMValidator;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.property.Property;
import com.ibm.lconn.wizard.common.property.PropertyLoader;
import com.ibm.lconn.wizard.common.validator.AbstractValidator;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class ClusterSilentLauncher {
	public static void main(String[] args) {
		System.setProperty(Constants.LCONN_WIZARD_PROP,
				ClusterConstant.WIZARD_ID_CLUSTER);
		AbstractValidator.isSilentMode = true;
		log(Level.INFO, "cluster.info.silent.start");

		Property property;
		if (args == null || args.length == 0) {
			response = defaultResponse;
		} else if (args.length == 1) {
			response = args[0];
		} else {
			log(Level.SEVERE, "cluster.severe.silent.input.argument.invalid");
			log(Level.SEVERE, "cluster.info.silent.usage");
			return;
		}

		if (!new File(response).exists()) {
			log(Level.SEVERE, "cluster.severe.silent.response.invalid",
					response);
			return;
		}

		log(Level.INFO, "cluster.info.silent.response", response);
		property = PropertyLoader.load(response);

		String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;
		LCUtil.initialize(wizardId);

		if ("create".equalsIgnoreCase(property.getProperty("task")))
			DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskCreate);
		else if ("add".equalsIgnoreCase(property.getProperty("task")))
			DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskAdd);
		else if ("remove".equalsIgnoreCase(property.getProperty("task")))
			DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskRemove);
		else {
			log(Level.SEVERE, "cluster.silent.task.invalid");
			return;
		}

		DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterDMHostName,
				property.getProperty("DM.hostname"));
		DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterDMSoapPort,
				property.getProperty("DM.soap.port"));
		DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterDMProfileName,
				property.getProperty("DM.profileName"));
		DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterDMWasUser,
				property.getProperty("DM.wasUser"));
		DataPool.setValue(wizardId, ClusterConstant.INPUT_clusterDMWasPassword,
				property.getProperty("DM.wasPassword"));

		log(Level.INFO, "cluster.silent.task.dm.validation");
		if (new DMValidator().validate() != 0) {
			log(Level.INFO, "cluster.silent.task.validation.fail");
			return;
		} else {
			log(Level.INFO, "cluster.silent.task.validation.success");
		}

		String featureNames = property.getProperty("feature");
		String[] features = featureNames.split(",");
		List<String> featureIds = new ArrayList<String>();
		for (String feature : features) {
			if (Constants.FEATURE_ACTIVITIES.equalsIgnoreCase(feature.trim())) {
				featureIds.add(ClusterConstant.OPTION_featureAct);
			}
			if (Constants.FEATURE_BLOGS.equalsIgnoreCase(feature.trim())) {
				featureIds.add(ClusterConstant.OPTION_featureBlogs);
			}
			if (Constants.FEATURE_COMMUNITIES.equalsIgnoreCase(feature.trim())) {
				featureIds.add(ClusterConstant.OPTION_featureComm);
			}
			if (Constants.FEATURE_DOGEAR.equalsIgnoreCase(feature.trim())) {
				featureIds.add(ClusterConstant.OPTION_featureDogear);
			}
			if (Constants.FEATURE_HOMEPAGE.equalsIgnoreCase(feature.trim())) {
				featureIds.add(ClusterConstant.OPTION_featureHomepage);
			}
			if (Constants.FEATURE_PROFILES.equalsIgnoreCase(feature.trim())) {
				featureIds.add(ClusterConstant.OPTION_featureProf);
			}
		}

		log(Level.INFO, "cluster.silent.selected.features", featureNames);
		if (featureIds.size() == 0) {
			log(Level.SEVERE, "cluster.silent.selected.features.empty");
			return;
		}
		String featureIDStr = "";
		for (String featureId : featureIds) {
			featureIDStr += "," + featureId;
		}
		featureIDStr = featureIDStr.substring(1);
		DataPool.setValue(wizardId,
				ClusterConstant.INPUT_clusterFeatureSelection, featureIDStr);

		if (!"remove".equalsIgnoreCase(property.getProperty("task"))) {
			if (featureIDStr.indexOf(ClusterConstant.OPTION_featureAct) > -1) {
				DataPool
						.setValue(
								wizardId,
								ClusterConstant.INPUT_clusterActivitiesContentStoreLocation,
								property.getProperty("activities.content.dir"));
			}
			if (featureIDStr.indexOf(ClusterConstant.OPTION_featureBlogs) > -1) {
				DataPool.setValue(wizardId,
						ClusterConstant.INPUT_clusterBlogsContentStoreLocation,
						property.getProperty("blogs.content.dir"));
				DataPool.setValue(wizardId,
						ClusterConstant.INPUT_clusterBlogsIndexLocation,
						property.getProperty("blogs.index.dir"));
			}
			if (featureIDStr.indexOf(ClusterConstant.OPTION_featureComm) > -1) {
				DataPool.setValue(wizardId,
						ClusterConstant.INPUT_clusterCommunitiesForum, property
								.getProperty("forum.content.dir"));
			}
			if (featureIDStr.indexOf(ClusterConstant.OPTION_featureHomepage) > -1) {
				DataPool.setValue(wizardId,
						ClusterConstant.INPUT_clusterHomepageIndexLocation,
						property.getProperty("homepage.index.dir"));
			}

		}

		log(Level.INFO, "cluster.silent.summary", TaskInterface
				.getSummaryString());
		log(Level.INFO, "cluster.silent.task.running");
		if ("create".equalsIgnoreCase(property.getProperty("task")))
			TaskInterface.addPrimaryNode();
		else if ("add".equalsIgnoreCase(property.getProperty("task")))
			TaskInterface.addSubSequentNode();
		else if ("remove".equalsIgnoreCase(property.getProperty("task")))
			TaskInterface.uninstallNode();
		log(Level.INFO, "cluster.silent.task.result", TaskInterface.getOutput());
	}

	public static void log(Level level, String label, Object... params) {
		logger.log(level, label, params);

		if (params == null)
			System.out.println(RESOURCE_BUNDLE.getString(label));
		else
			System.out.println(MessageFormat.format(RESOURCE_BUNDLE
					.getString(label), params));
	}

	public static void log(Level level, String message) {
		logger.log(level, RESOURCE_BUNDLE.getString(message));
		System.out.println(RESOURCE_BUNDLE.getString(message));
	}

	private static final Logger logger = LogUtil
			.getLogger(ClusterSilentLauncher.class);
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle("com.ibm.lconn.wizard.common.logging.messages");
	private static final String defaultResponse = Constants.RESPONSE_ROOT
			+ Constants.FS + "cluster" + Constants.FS + "response.properties";
	private static String response;
}
