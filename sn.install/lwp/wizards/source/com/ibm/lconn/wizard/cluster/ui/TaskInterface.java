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
package com.ibm.lconn.wizard.cluster.ui;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.cluster.data.ClusterFeatureController;
import com.ibm.lconn.wizard.cluster.data.FeatureInfo;
import com.ibm.lconn.wizard.cluster.task.AbstractTask;
import com.ibm.lconn.wizard.cluster.task.AddPrimaryNodeTask;
import com.ibm.lconn.wizard.cluster.task.AddSubsequentNodeTask;
import com.ibm.lconn.wizard.cluster.task.ClusterRestartTask;
import com.ibm.lconn.wizard.cluster.task.ClusterStopTask;
import com.ibm.lconn.wizard.cluster.task.CopePYFileTask;
import com.ibm.lconn.wizard.cluster.task.FullSyncTask;
import com.ibm.lconn.wizard.cluster.task.SetWasVariable;
import com.ibm.lconn.wizard.cluster.task.UninstallNodeTask;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.property.Property;
import com.ibm.lconn.wizard.common.property.PropertyLoader;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class TaskInterface {
	private static String output;
	private static String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;
	private static final Logger logger = LogUtil.getLogger(TaskInterface.class);

	public static String getOutput() {
		return output;
	}

	public static int addPrimaryNode() {
		output = Messages.getString("cluster.finish.content");

		String featureSelected = eval(ClusterConstant.INPUT_clusterFeatureSelection);
		logger.log(Level.INFO, "cluster.info.selectedFeature", featureSelected);

		String[] features = Util.delimStr(featureSelected);

		AbstractTask task;
		int exitValue = 0;
		int tempValue;

		String content = "";
		for (String feature : features) {

			String featureName = ClusterFeatureController
					.getFeatureName(feature);
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					featureName.trim());

			logger
					.log(Level.INFO, "cluster.info.start.addPrimary",
							featureName);

			task = new AddPrimaryNodeTask();
			tempValue = task.run();

			int v;
			if (tempValue != 0) {
				v = tempValue;
			} else {
				v = setWasAriable(feature);
			}

			String result = "";
			if (tempValue == 0) {
				logger.log(Level.INFO, "cluster.info.task.addPrimary.success");
				result = Messages
						.getString("cluster.finish.content.task.success");
				if (v != 0)
					result += Messages
							.getString("cluster.finish.content.setShare.fail");
			} else {
				logger.log(Level.INFO, "cluster.info.task.addPrimary.fail");
				result += Messages
						.getString("cluster.finish.content.task.fail");
			}
			content += Messages.getString(
					"cluster.finish.content.addPrimary.part", featureName,
					LCUtil.getClusterName(featureName), result);
			exitValue = tempValue == 0 ? exitValue : tempValue;
		}

		String isFullSync = eval(ClusterConstant.INPUT_clusterDoFullSync);

		if (ClusterConstant.OPTION_clusterSyncYes.equals(isFullSync)) {
			task = new FullSyncTask();
			tempValue = task.run();

			String result = "";
			if (tempValue == 0) {
				result += Messages
						.getString("cluster.finish.content.task.success");
			} else {
				logger.log(Level.INFO, "cluster.info.task.sync.fail");
				result += Messages
						.getString("cluster.finish.content.sync.fail");
			}
			content += Messages.getString("cluster.finish.content.sync.part",
					result);

			task = new ClusterRestartTask();
			result = "";
			tempValue = task.run();
			if (tempValue == 0) {
				result += Messages
						.getString("cluster.finish.content.task.success");
			} else {
				logger.log(Level.INFO, "cluster.info.task.restart.fail");
				result += Messages
						.getString("cluster.finish.content.restart.fail");
			}
			content += Messages.getString(
					"cluster.finish.content.restart.part", result);

		}

		output = MessageFormat.format(output.replace("'", "''"), content);
		genResponse();
		return exitValue;
	}

	public static int addSubSequentNode() {
		output = Messages.getString("cluster.finish.content");
		String featureSelected = eval(ClusterConstant.INPUT_clusterFeatureSelection);
		logger.log(Level.INFO, "cluster.info.selectedFeature", featureSelected);

		String[] features = Util.delimStr(featureSelected);

		AbstractTask task;
		int exitValue = 0;
		int tempValue;

		String content = "";
		for (String feature : features) {
			String featureName = ClusterFeatureController
					.getFeatureName(feature);
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					featureName.trim());
			logger.log(Level.INFO, "cluster.info.start.addSubsequent",
					featureName);

			task = new AddSubsequentNodeTask();
			tempValue = task.run();

			int v;
			if (tempValue != 0) {
				v = tempValue;
			} else {
				v = setWasAriable(feature);
			}

			String result = "";
			if (tempValue == 0) {
				logger.log(Level.INFO,
						"cluster.info.task.addSubsequent.success");
				result += Messages
						.getString("cluster.finish.content.task.success");
				if (v != 0)
					result += Messages
							.getString("cluster.finish.content.setShare.fail");
			} else {
				logger.log(Level.INFO, "cluster.info.task.addSubsequent.fail");
				result += Messages
						.getString("cluster.finish.content.task.fail");
			}
			content += Messages.getString(
					"cluster.finish.content.addSubsequent.part", featureName,
					LCUtil.getClusterName(featureName), result);
			exitValue = tempValue == 0 ? exitValue : tempValue;
		}

		String isFullSync = eval(ClusterConstant.INPUT_clusterDoFullSync);

		if (ClusterConstant.OPTION_clusterSyncYes.equals(isFullSync)) {
			task = new FullSyncTask();
			tempValue = task.run();

			String result = "";
			if (tempValue == 0) {
				result += Messages
						.getString("cluster.finish.content.task.success");
			} else {
				logger.log(Level.INFO, "cluster.info.task.sync.fail");
				result += Messages
						.getString("cluster.finish.content.sync.fail");
			}
			content += Messages.getString("cluster.finish.content.sync.part",
					result);

			task = new ClusterRestartTask();
			result = "";
			tempValue = task.run();
			if (tempValue == 0) {
				result += Messages
						.getString("cluster.finish.content.task.success");
			} else {
				logger.log(Level.INFO, "cluster.info.task.restart.fail");
				result += Messages
						.getString("cluster.finish.content.restart.fail");
			}
			content += Messages.getString(
					"cluster.finish.content.restart.part", result);

		}

		output = MessageFormat.format(output.replace("'", "''"), content);
		genResponse();
		return exitValue;
	}

	public static int uninstallNode() {
		output = Messages.getString("cluster.finish.content");
		String featureSelected = eval(ClusterConstant.INPUT_clusterFeatureSelection);
		logger.log(Level.INFO, "cluster.info.selectedFeature", featureSelected);

		String[] features = Util.delimStr(featureSelected);

		AbstractTask task;
		int exitValue = 0;
		int tempValue;

		Map<String, List<String>> m = LCUtil.getMemberNameFromDM();

		String content = "";
		for (String feature : features) {
			feature = ClusterFeatureController.getFeatureName(feature);
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					feature.trim());
			logger.log(Level.INFO, "cluster.info.start.uninstall", feature);

			if (m.get(feature).size() == 1) {
				DataPool.setValue(wizardId, "INPUT_DisLastNode", "true");
			} else {
				DataPool.setValue(wizardId, "INPUT_DisLastNode", "false");
			}

			task = new ClusterStopTask();
			tempValue = task.run();
			if (tempValue != 0) {
				logger.log(Level.INFO, "cluster.info.stopCluster.fail");
				content += Messages.getString(
						"cluster.finish.content.stop.fail", LCUtil
								.getClusterName(feature));
			}
			exitValue = tempValue == 0 ? exitValue : tempValue;
			if (tempValue != 0) {
				content += Messages.getString(
						"cluster.finish.content.removeNode.part", feature,
						LCUtil.getClusterName(feature), Messages
								.getString("cluster.finish.content.task.fail"));
				continue;
			}

			task = new UninstallNodeTask();
			tempValue = task.run();
			if (tempValue == 0) {
				logger.log(Level.INFO, "cluster.info.uninstall.succeed");
				content += Messages
						.getString(
								"cluster.finish.content.removeNode.part",
								feature,
								LCUtil.getClusterName(feature),
								Messages
										.getString("cluster.finish.content.task.success"));

			} else {
				logger.log(Level.INFO, "cluster.info.uninstall.fail");
				content += Messages.getString(
						"cluster.finish.content.removeNode.part", feature,
						LCUtil.getClusterName(feature), Messages
								.getString("cluster.finish.content.task.fail"));
			}
			exitValue = tempValue == 0 ? exitValue : tempValue;
		}

		output = MessageFormat.format(output.replace("'", "''"), content);
		genResponse();
		return exitValue;
	}

	public static int setWasAriable(String feature) {
		logger.log(Level.INFO, "cluster.info.start.setShareFolder", feature);
		String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;
		AbstractTask task;
		int tempValue, exitValue;
		exitValue = 0;
		String oldPath, newPath;

		if (ClusterConstant.OPTION_featureAct.equals(feature)) {
			oldPath = LCUtil.getFolderConfig("ACTIVITIES_CONTENT_DIR",
					ClusterConstant.SCOPE_NODE);
			newPath = DataPool
					.getValue(
							wizardId,
							ClusterConstant.INPUT_clusterActivitiesContentStoreLocation);

			DataPool.setValue(wizardId, "WAS_VARIABLE",
					"ACTIVITIES_CONTENT_DIR," + newPath);

			tempValue = copyDir(oldPath, newPath);
			exitValue = tempValue == 0 ? exitValue : tempValue;

			task = new SetWasVariable();
			tempValue = task.run();
			if (tempValue != 0) {
				logger.log(Level.SEVERE, "cluster.severe.setShareFolder.fail");
			}
			exitValue = tempValue == 0 ? exitValue : tempValue;
		}

		if (ClusterConstant.OPTION_featureBlogs.equals(feature)) {
			oldPath = LCUtil.getFolderConfig("BLOGS_CONTENT_DIR",
					ClusterConstant.SCOPE_NODE);
			newPath = DataPool.getValue(wizardId,
					ClusterConstant.INPUT_clusterBlogsContentStoreLocation);

			DataPool.setValue(wizardId, "WAS_VARIABLE", "BLOGS_CONTENT_DIR,"
					+ newPath);

			tempValue = copyDir(oldPath, newPath);
			exitValue = tempValue == 0 ? exitValue : tempValue;

			task = new SetWasVariable();
			tempValue = task.run();
			if (tempValue != 0) {
				logger.log(Level.SEVERE, "cluster.severe.setShareFolder.fail");
			}
			exitValue = tempValue == 0 ? exitValue : tempValue;

			oldPath = LCUtil.getFolderConfig("BLOGS_INDEX_DIR",
					ClusterConstant.SCOPE_NODE);
			newPath = DataPool.getValue(wizardId,
					ClusterConstant.INPUT_clusterBlogsIndexLocation);

			DataPool.setValue(wizardId, "WAS_VARIABLE", "BLOGS_INDEX_DIR,"
					+ newPath);

			tempValue = copyDir(oldPath, newPath);
			exitValue = tempValue == 0 ? exitValue : tempValue;

			task = new SetWasVariable();
			tempValue = task.run();
			if (tempValue != 0) {
				logger.log(Level.SEVERE, "cluster.severe.setShareFolder.fail");
			}
			exitValue = tempValue == 0 ? exitValue : tempValue;
		}

		if (ClusterConstant.OPTION_featureComm.equals(feature)) {
			oldPath = LCUtil.getFolderConfig("FORUM_CONTENT_DIR",
					ClusterConstant.SCOPE_NODE);
			newPath = DataPool.getValue(wizardId,
					ClusterConstant.INPUT_clusterCommunitiesForum);

			DataPool.setValue(wizardId, "WAS_VARIABLE", "FORUM_CONTENT_DIR,"
					+ newPath);

			tempValue = copyDir(oldPath, newPath);
			exitValue = tempValue == 0 ? exitValue : tempValue;

			task = new SetWasVariable();
			tempValue = task.run();
			if (tempValue != 0) {
				logger.log(Level.SEVERE, "cluster.severe.setShareFolder.fail");
			}
			exitValue = tempValue == 0 ? exitValue : tempValue;
		}

		if (ClusterConstant.OPTION_featureHomepage.equals(feature)) {
			oldPath = LCUtil.getFolderConfig("HOMEPAGE_INDEX_DIR",
					ClusterConstant.SCOPE_NODE);
			newPath = DataPool.getValue(wizardId,
					ClusterConstant.INPUT_clusterHomepageIndexLocation);

			DataPool.setValue(wizardId, "WAS_VARIABLE", "HOMEPAGE_INDEX_DIR,"
					+ newPath);

			tempValue = copyDir(oldPath, newPath);
			exitValue = tempValue == 0 ? exitValue : tempValue;

			task = new SetWasVariable();
			tempValue = task.run();
			if (tempValue != 0) {
				logger.log(Level.SEVERE, "cluster.severe.setShareFolder.fail");
			}
			exitValue = tempValue == 0 ? exitValue : tempValue;
		}

		return exitValue;
	}

	public static String getSummaryString() {
		String features = eval(ClusterConstant.INPUT_clusterFeatureSelection);
		String content = Messages.getString("cluster.summary.content");
		String taskInfoStr = null;

		String taskSelected = eval(ClusterConstant.INPUT_clusterTask);
		if (ClusterConstant.OPTION_clusterTaskCreate.equals(taskSelected)) {
			String taskInfo = Messages
					.getString("cluster.summary.content.task.addPrimary");
			String isSync;
			String isFullSync = eval(ClusterConstant.INPUT_clusterDoFullSync);

			if (ClusterConstant.OPTION_clusterSyncYes.equals(isFullSync)) {
				isSync = Messages.getString("LABEL.OPTION_clusterSyncYes");
			} else {
				isSync = Messages.getString("LABEL.OPTION_clusterSyncNo");
			}
			taskInfoStr = MessageFormat.format(taskInfo.replace("'", "''"),
					translateLabel(features), isSync);

		} else if (ClusterConstant.OPTION_clusterTaskAdd.equals(taskSelected)) {
			String taskInfo = Messages
					.getString("cluster.summary.content.task.addSubsequent");
			String isSync;
			String isFullSync = eval(ClusterConstant.INPUT_clusterDoFullSync);

			if (ClusterConstant.OPTION_clusterSyncYes.equals(isFullSync)) {
				isSync = Messages.getString("LABEL.OPTION_clusterSyncYes");
			} else {
				isSync = Messages.getString("LABEL.OPTION_clusterSyncNo");
			}
			taskInfoStr = MessageFormat.format(taskInfo,
					translateLabel(features), isSync);
		} else if (ClusterConstant.OPTION_clusterTaskRemove
				.equals(taskSelected)) {
			String taskInfo = Messages
					.getString("cluster.summary.content.task.removeNode");
			taskInfoStr = MessageFormat.format(taskInfo,
					translateLabel(features));
		}

		String featureInfo = Messages
				.getString("cluster.summary.content.part.common");

		StringBuffer sb = new StringBuffer();
		String[] featureAry = Util.delimStr(features);
		for (int i = 0; i < featureAry.length; i++) {
			try {
				String feature = ClusterFeatureController
						.getFeatureName(featureAry[i]);
				FeatureInfo fi = LCUtil.getFeature(feature);
				String featureLabel = getFeatureLabel(feature);
				String featureInfoAdd = Messages
						.getString("cluster.summary.content.part." + feature
								+ ".add");

				String profileName = fi.getProfileName();
				String clusterMemberName;
				if (equals(ClusterConstant.INPUT_clusterTask,
						ClusterConstant.OPTION_clusterTaskCreate)) {
					clusterMemberName = fi.getServerName();

				} else if (equals(ClusterConstant.INPUT_clusterTask,
						ClusterConstant.OPTION_clusterTaskAdd)) {
					clusterMemberName = LCUtil.getSuggestedMemberName(feature);

				} else {
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
					clusterMemberName = prop.getProperty(feature
							+ ".ClusterMemberName");
				}

				DataPool.setValue(wizardId, feature + ".memberName",
						clusterMemberName);

				sb.append(MessageFormat.format(featureInfo, featureLabel,
						profileName, fi.getCellName(), fi.getNodeName(),
						clusterMemberName));
				if (!equals(ClusterConstant.INPUT_clusterTask,
						ClusterConstant.OPTION_clusterTaskRemove)) {
					if (Constants.FEATURE_ACTIVITIES.equals(feature)) {
						sb
								.append(MessageFormat
										.format(
												featureInfoAdd,
												eval(ClusterConstant.INPUT_clusterActivitiesContentStoreLocation)));
					}
					if (Constants.FEATURE_BLOGS.equals(feature)) {
						sb
								.append(MessageFormat
										.format(
												featureInfoAdd,
												eval(ClusterConstant.INPUT_clusterBlogsIndexLocation),
												eval(ClusterConstant.INPUT_clusterBlogsContentStoreLocation)));
					}
					if (Constants.FEATURE_COMMUNITIES.equals(feature)) {
						sb
								.append(MessageFormat
										.format(
												featureInfoAdd,
												eval(ClusterConstant.INPUT_clusterCommunitiesForum)));
					}
					if (Constants.FEATURE_HOMEPAGE.equals(feature)) {
						sb
								.append(MessageFormat
										.format(
												featureInfoAdd,
												eval(ClusterConstant.INPUT_clusterHomepageIndexLocation)));
					}

				}
				sb.append("\n");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		String wasHome = eval(ClusterConstant.INPUT_WAS_HOME);
		if (Constants.OS_WINDOWS.equals((CommonHelper.getPlatformType()))) {
			wasHome = wasHome.replaceAll("/", "\\\\");
		}
		String summaryStr = MessageFormat.format(content, taskInfoStr, wasHome,
				eval(ClusterConstant.INPUT_LC_HOME),
				eval(ClusterConstant.INPUT_clusterDMHostName),
				eval(ClusterConstant.INPUT_clusterDMSoapPort),
				eval(ClusterConstant.INPUT_clusterDMProfileName),
				eval(ClusterConstant.INPUT_clusterDMWasUser), sb.toString());
		return summaryStr;
	}

	public static String eval(String key) {
		return DataPool.getValue(ClusterConstant.WIZARD_ID_CLUSTER, key);
	}

	public static boolean equals(String key, String targetVal) {
		return CommonHelper.equals(eval(key), targetVal);
	}

	public static String translateLabel(String features) {
		StringBuffer sb = new StringBuffer();
		String[] featureAry = Util.delimStr(features);
		for (int i = 0; i < featureAry.length; i++) {
			String feature = ClusterFeatureController
					.getFeatureName(featureAry[i]);
			sb.append(getFeatureLabel(feature) + ", ");
		}
		return sb.substring(0, sb.length() - 2);
	}

	public static String getFeatureLabel(String featureName) {
		return MessageUtil.getMsg(featureName + ".name");
	}

	private static int copyDir(String oldPath, String newPath) {
		logger.log(Level.INFO, "cluster.info.copy.file", new String[] {
				oldPath, newPath });
		if (oldPath == null || newPath == null) {
			logger.log(Level.SEVERE, "cluster.severe.copy.fail");
			return 1;
		}
		if (new File(oldPath).exists() && new File(oldPath).isDirectory()
				&& new File(newPath).exists()
				&& new File(newPath).isDirectory()) {

			CopePYFileTask task = new CopePYFileTask();
			/*List<String> variables = new ArrayList<String>();
			variables.add("\"" + oldPath + "\"");
			variables.add("\"" + newPath + "\"");
			DataPool.setComplexData(ClusterConstant.WIZARD_ID_CLUSTER,
					ClusterConstant.INPUT_clusterCommandVariables, variables);
					*/
			if (task.copyFolder(oldPath, newPath) == true) {
				return 0;
			} else {
				logger.log(Level.SEVERE, "cluster.severe.copy.fail");
				return 1;
			}
		}
		logger.log(Level.SEVERE, "cluster.severe.copy.fail");
		return 1;
	}

	public static void genResponse() {
		try {
			final String response = Constants.RESPONSE_ROOT + Constants.FS
					+ "cluster" + Constants.FS + "response.properties";
			File dir = new File(Constants.RESPONSE_ROOT + Constants.FS
					+ "cluster");
			if (!dir.exists())
				dir.mkdirs();
			File file = new File(response);
			if (!file.exists())
				file.createNewFile();

			Property property = PropertyLoader.load(response);
			Map<String, String> map = property.getAllProperty();
			if (map == null)
				map = new HashMap<String, String>();
			map.put("DM.hostname",
					eval(ClusterConstant.INPUT_clusterDMHostName));
			map.put("DM.soap.port",
					eval(ClusterConstant.INPUT_clusterDMSoapPort));
			map.put("DM.profileName",
					eval(ClusterConstant.INPUT_clusterDMProfileName));
			map.put("DM.wasUser", eval(ClusterConstant.INPUT_clusterDMWasUser));
			map.put("DM.wasPassword", "*****");
			String task = DataPool.getValue(wizardId,
					ClusterConstant.INPUT_clusterTask);
			if (ClusterConstant.OPTION_clusterTaskCreate.equals(task)) {
				map.put("task", "create");
			}
			if (ClusterConstant.OPTION_clusterTaskAdd.equals(task)) {
				map.put("task", "add");
			}
			if (ClusterConstant.OPTION_clusterTaskRemove.equals(task)) {
				map.put("task", "remove");
			}

			String selectedFeatures = eval(ClusterConstant.INPUT_clusterFeatureSelection);
			String[] features = Util.delimStr(selectedFeatures);
			String featuresStr = "";
			for (String feature : features) {
				featuresStr += ","
						+ ClusterFeatureController.getFeatureName(feature);
			}
			featuresStr = featuresStr.substring(1);

			map.put("feature", featuresStr);

			if (!ClusterConstant.OPTION_clusterTaskRemove.equals(task)) {
				if (contain(ClusterConstant.OPTION_featureAct, features)) {
					map
							.put(
									"activities.content.dir",
									eval(ClusterConstant.INPUT_clusterActivitiesContentStoreLocation));
				}
				if (contain(ClusterConstant.OPTION_featureComm, features)) {
					map
							.put(
									"forum.content.dir",
									eval(ClusterConstant.INPUT_clusterCommunitiesForum));
				}

				if (contain(ClusterConstant.OPTION_featureBlogs, features)) {
					map
							.put(
									"blogs.content.dir",
									eval(ClusterConstant.INPUT_clusterBlogsContentStoreLocation));
					map
							.put(
									"blogs.index.dir",
									eval(ClusterConstant.INPUT_clusterBlogsIndexLocation));
				}
				if (contain(ClusterConstant.OPTION_featureHomepage, features)) {
					map
							.put(
									"homepage.index.dir",
									eval(ClusterConstant.INPUT_clusterHomepageIndexLocation));
				}
			}
			property.setProperty(map);
		} catch (Exception e) {
		}
	}

	private static boolean contain(String s, String[] arr) {
		if (arr == null)
			return false;

		for (String v : arr) {
			if (CommonHelper.equals(s, v))
				return true;
		}

		return false;
	}
}
