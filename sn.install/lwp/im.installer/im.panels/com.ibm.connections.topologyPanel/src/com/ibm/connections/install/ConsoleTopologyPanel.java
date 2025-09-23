/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;

public class ConsoleTopologyPanel extends BaseConfigConsolePanel {
	String className = ConsoleTopologyPanel.class.getName();

	private HashMap<String, List<Integer>> clusterNodePairMedium = new HashMap<String, List<Integer>>();
	private HashMap<String, List<Integer>> clusterNodePairLarge = new HashMap<String, List<Integer>>();

	private ArrayList<Integer> selectFeaturesList = new ArrayList<Integer>();
	private ArrayList<String> selectNodeList = new ArrayList<String>();
	private ArrayList<String> originalNodeList = new ArrayList<String>();
	private int[] selectNodeIndices = null;// e.g.{0,1},0 for uncheck,1 for
											// check
	private String[] selectServerMemberList = null;
	private String selectCluster = null;

	private boolean isLoadCluster = false;
	// private boolean isLoadSmall = false;
	private boolean isLoadMedium = false;
	private boolean isLoadLarge = false;
	private boolean isReset = false;

	private boolean chooseExistingClusterSmall = false;
	/**
	 * control whether to return to the table display when configuration of an
	 * application is completed
	 */
	private boolean returnFlag = false;
	/** application index of the current medium application configuration */
	private int curConfigIndexMedium = 0;
	/** application index of the current large application configuration */
	private int curConfigIndexLarge = 0;
	/** index of the current server member. */
	private int curServerMemberIndex = 0;

	private ArrayList<String> LCClusterList = new ArrayList<String>();
	private ArrayList<String> allClusterList = new ArrayList<String>();
	private Map<String, List<NodeServerPair>> existClusterInfoMap = new HashMap<String, List<NodeServerPair>>();

	InstallValidator installValidator = new InstallValidator();

	public static final int SMALL_TOPOLOGY = 1;
	public static final int MEDIUM_TOPOLOGY = 2;
	public static final int LARGE_TOPOLOGY = 3;

	private int topologyType = SMALL_TOPOLOGY;

	private ArrayList<NodeServerPair> activitiesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> blogsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> communitiesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> dogearSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> homepageSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> profilesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> filesSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> forumSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> wikisSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> mobileSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> moderationSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> searchSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> newsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> metricsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ccmSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> rteSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> commonSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> widgetContainerSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> pushNotificationSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ic360SelectNodeServerPairList = new ArrayList<NodeServerPair>();
	//private ArrayList<NodeServerPair> quickResultsSelectNodeServerPairList = new ArrayList<NodeServerPair>();
	
	// for large
	private ArrayList<NodeServerPair> activitiesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> blogsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> communitiesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> dogearSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> homepageSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> profilesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> filesSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> forumSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> wikisSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> mobileSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> moderationSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> searchSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> newsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> metricsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ccmSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> rteSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> commonSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> widgetContainerSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> pushNotificationSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	private ArrayList<NodeServerPair> ic360SelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	//private ArrayList<NodeServerPair> quickResultsSelectNodeServerPairListLarge = new ArrayList<NodeServerPair>();
	
	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in

	public ConsoleTopologyPanel() {
		super(Messages.DEPOLOGY_PANEL);
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_TOPOLOGY_PANEL;
	}

	@Override
	public void perform() {
		if (shouldSkip())
			return;
		log.info("Deployment Panel :: Entered");

		TextCustomPanelUtils.setLogPanel(log, "Deployment panel");
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);
		profile = getProfile();
		loadSelectFeaturesList();
		loadClusterInfo();

		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG, Messages.DEPOLOGY_PANEL);
		chooseDeplymentType();
	}

	private void loadSelectFeaturesList() {
		for (int feature = ACTIVITIES; feature <= IC360; feature++) {
			if (getIsFeatureSelected(feature)){
				if (feature == CCM && profile.getUserData("user.ccm.existingDeployment").equals("true")){
					continue;
				} 
				selectFeaturesList.add(feature);
			}
		}
	}

	private void chooseDeplymentType() {
		returnFlag = false;
		TextCustomPanelUtils.showSubTitle1(Messages.DEPOLOGY_PANEL_INFO);
		topologyType = TextCustomPanelUtils
				.singleSelect(Messages.DEPOLOGY_SELECTION_INFO_CONSOLE,
						new String[] { Messages.SMALL_DEPOLOGY_SELECTION_INFO,
								Messages.MEDIUM_DEPOLOGY_SELECTION_INFO,
								Messages.LARGE_DEPOLOGY_SELECTION_INFO }, 0,
						null, null);
		TextCustomPanelUtils.logInput("topology type",
				topologyType == 1 ? "small" : topologyType == 2 ? "medium"
						: "large");
		if (topologyType == 1)
			smallDeplyment();
		else if (topologyType == 2)
			mediumDeplyment();
		else if (topologyType == 3)
			largeDeplyment();
	}

	private void smallDeplyment() {
		TextCustomPanelUtils.showSubTitle1(Messages.DEPOLOGY_CLUSTERS);
		// If there is no cluster installed, you have to create a new one
		if (LCClusterList.size() <= 0) {
			configClusterNameSmall();
			return;
		}
		int input = TextCustomPanelUtils.singleSelect(
				Messages.DEPOLOGY_INPUT_CLUSTER_INFO_CONSOLE, new String[] {
						Messages.DEPOLOGY_SPECIFY_CLUSTER_NAME,
						Messages.DEPOLOGY_CHOOSE_EXISTING_CLUSTER }, 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX },
				Messages.DEPOLOGY_CLUSTER_NAME_INFO_SMALL);
		if (input < 0) {
			chooseDeplymentType();
			return;
		}
		if (input == 1) {
			TextCustomPanelUtils.logInput("user input mode",
					"specify cluster name");
			configClusterNameSmall();
		} else if (input == 2) {
			chooseClusterNameSmall();
		}
	}

	private void configClusterNameSmall() {
		TextCustomPanelUtils
				.logInput("user input mode", "specify cluster name");

		String input = TextCustomPanelUtils.getInput(Messages.DEPOLOGY_CLUSTER,
				selectCluster == null ? "ICCluster" : selectCluster );
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			chooseDeplymentType();
			return;
		}
		TextCustomPanelUtils.logInput("cluster name", input);
		if ((installValidator.containsSpace(input.trim()) || installValidator.containsInvalidCharForClusterName(input.trim()))) {
//				|| (!TextCustomPanelUtils.containsIgnoreCase(LCClusterList, input.trim()) && TextCustomPanelUtils.containsIgnoreCase(allClusterList, input.trim()))) {
			TextCustomPanelUtils.showError(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
			log.error("Cluster name is invalid.");
			configClusterNameSmall();
			return;
		}
		
		//check if do fresh install, can't install a feature on an existing cluster, but "modify add" can
		if(isInstall()){
			if(TextCustomPanelUtils.containsIgnoreCase(allClusterList, input.trim())){
				TextCustomPanelUtils.showError(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
				log.error("Cluster name is invalid.");
				configClusterNameSmall();
				return;
			}
		}
		
		// cluster name changes,load default server member list
		if (selectCluster == null
				|| !selectCluster.equalsIgnoreCase(input.trim())) {
			selectCluster = input.trim();
			selectServerMemberList = new String[originalNodeList.size()];
			// set default serverMemberList Value
			for (int i = 0; i < originalNodeList.size(); i++) {
				selectServerMemberList[i] = input + "_server"
						+ (i + 1);
			}
		}
		smallClusterModifyAction();
		if (!chooseExistingClusterSmall)
			selectNodesSmall();
		else {
			displayChosenClusterNodeServerSmall();
			saveDataSmall();
		}

	}

	private void displayChosenClusterNodeServerSmall() {
		for (int i = 0; i < selectNodeIndices.length; i++) {
			if (selectNodeIndices[i] == 0)
				continue;
			TextCustomPanelUtils.showText(Messages.DEPOLOGY_NODE_NAME_CONSOLE
					+ originalNodeList.get(i));
			TextCustomPanelUtils
					.showText(Messages.DEPOLOGY_SERVERMEMBER_NAME_CONSOLE
							+ selectServerMemberList[i]);
		}
	}

	private void chooseClusterNameSmall() {
		TextCustomPanelUtils.logInput("user input mode",
				"choose cluster name from existing clusters");

		if (LCClusterList == null || LCClusterList.size() == 0 || isInstall()) {
			TextCustomPanelUtils.showError(Messages.DEPOLOGY_NO_CLUSTER_ERROR);
			log.error("No existing clusters");
			configClusterNameSmall();
			return;
		}
		String[] existingClusters = new String[LCClusterList.size()];
		for (int i = 0; i < existingClusters.length; i++)
			existingClusters[i] = LCClusterList.get(i).toString();
		int input = TextCustomPanelUtils.singleSelect(
				Messages.DEPOLOGY_EXISTING_NODES, existingClusters, 0,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (input < 0) {
			configClusterNameSmall();
			return;
		}
		selectCluster = existingClusters[input - 1];
		selectServerMemberList = new String[originalNodeList.size()];

		TextCustomPanelUtils.logInput("select cluster name", selectCluster);
		smallClusterModifyAction();
		// if choose existing cluster, you cannot configure the node selection
		// and server member name
		displayChosenClusterNodeServerSmall();
		saveDataSmall();
	}

	private void selectNodesSmall() {
		TextCustomPanelUtils
				.showSubTitle1(Messages.DEPOLOGY_NODE_SELECTION_CONSOLE);
		int[] inputs = TextCustomPanelUtils.multiSelect(
				Messages.DEPOLOGY_NODE_SELECTION_INFO,
				convertArrayToStringList(originalNodeList), selectNodeIndices,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (inputs != null && inputs[0] < 0) {
			chooseClusterNameSmall();
			return;
		}
		boolean selectNodes = false;
		if (inputs == null) {// prepare to move on to next step
			int i = 0;
			for (; i < selectNodeIndices.length; i++) {
				if (selectNodeIndices[i] == 1) {
					selectNodes = true;
					break;
				}
			}
			if (!selectNodes) {
				TextCustomPanelUtils
						.showError(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
				log.error("CLFRP0018E: Node selection must not be empty.");
				curServerMemberIndex = 0;
				selectNodesSmall();
				return;
			}
			for (i = 0; i < selectNodeIndices.length; i++)
				TextCustomPanelUtils.logInput("select node index " + i,
						Boolean.toString(selectNodeIndices[i] == 1));
			configServerMembersSmall();
		} else {
			selectNodeIndices = inputs;
			curServerMemberIndex = 0;
			selectNodesSmall();
		}
	}

	private void smallClusterModifyAction() {
		String cluster = selectCluster;

		if (TextCustomPanelUtils.containsIgnoreCase(LCClusterList, cluster)) {
			chooseExistingClusterSmall = true;
			cluster = selectCluster = getOriginalFormClusterSmall(cluster);
			List<NodeServerPair> existPairList = getExistNodeServerPairList(cluster);
			log.debug("smallClusterComboAction existPairList.size = "
					+ existPairList.size());
			selectNodeList.clear();

			for (int i = 0; i < selectNodeIndices.length; i++)
				selectNodeIndices[i] = 0;

			boolean isExistNode = false;
			for (int i = 0; i < originalNodeList.size(); i++) {
				String nodeNameTemp = originalNodeList.get(i);
				log.debug("smallClusterComboAction nodeNameTemp = "
						+ nodeNameTemp);
				inner: for (int j = 0; j < existPairList.size(); j++) {
					NodeServerPair pair = existPairList.get(j);
					if (pair.getNodeName().equals(nodeNameTemp)) {
						selectNodeIndices[i] = 1;
						selectNodeList.add(nodeNameTemp);
						selectServerMemberList[i] = pair.getServerMemberName();
						isExistNode = true;
						break inner;
					}
					log.debug("smallClusterComboAction pair.getNodeName() = "
							+ pair.getNodeName());
				}
				if (!isExistNode) {
					//
				}
				isExistNode = false;
			}

		} else {
			chooseExistingClusterSmall = false;
			selectNodeList.clear();
		}
	}

	/** configure server member names one by one */
	private void configServerMembersSmall() {
		if (selectNodeIndices != null
				&& curServerMemberIndex < selectNodeIndices.length) {
			// if the current node is not selected, skip the server member name
			// configuration
			if (selectNodeIndices[curServerMemberIndex] == 0) {
				curServerMemberIndex++;
				configServerMembersSmall();
				return;
			}

			String text = Messages.DEPOLOGY_ENTER_SERVER_MEMBER
					+ originalNodeList.get(curServerMemberIndex);
			String input = TextCustomPanelUtils.getInput(text,
					selectServerMemberList[curServerMemberIndex]);

			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				curServerMemberIndex = 0;
				selectNodesSmall();
				return;
			}
			TextCustomPanelUtils.logInput("config server member index "
					+ curServerMemberIndex, input);
			// decide if the server member name exists in the existing cluster
			// or other newly created clusters
			if (installValidator.containsSpace(input.trim())
					|| (isInstall() && serverMemberNameDuplicateSmall(input.trim()))) {
				TextCustomPanelUtils
						.showError(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
				log.error("Server name is invalid.");
				configServerMembersSmall();
				return;
			}

			selectServerMemberList[curServerMemberIndex] = input.trim();
			curServerMemberIndex++;
			configServerMembersSmall();
		} else {
			curServerMemberIndex = 0;
			saveDataSmall();
		}
	}

	/** return whether server member names duplicate for small topology */
	private boolean serverMemberNameDuplicateSmall(String input) {
		String curNode = originalNodeList.get(curServerMemberIndex);
		for (String curCluster : allClusterList) {
			List<NodeServerPair> existPairList = getExistNodeServerPairList(curCluster);
			for (NodeServerPair tmp : existPairList) {
				log.info("servermember name:" + tmp.getServerMemberName()
						+ ",node name:" + tmp.getNodeName() + ";");
				if (tmp.isSelected()
						&& tmp.getServerMemberName().equalsIgnoreCase(input)
						&& curNode.equals(tmp.getNodeName()))
					return true;
			}
		}
		for (int i = 0; i < selectServerMemberList.length; i++) {
			if (i != curServerMemberIndex && selectNodeIndices[i] == 1
					&& selectServerMemberList[i].equalsIgnoreCase(input))
				return true;
		}
		return false;
	}

	private void mediumDeplyment() {
		TextCustomPanelUtils.showSubTitle1(Messages.DEPOLOGY_INPUT_CLUSTER);
		TextCustomPanelUtils
				.showText(Messages.DEPOLOGY_MEDIUM_INPUT_CLUSTER_INFO);
		loadNodeForMedium();
		configDeplymentMedium();
	}

	private void largeDeplyment() {
		TextCustomPanelUtils.showSubTitle1(Messages.DEPOLOGY_INPUT_CLUSTER);
		TextCustomPanelUtils
				.showText(Messages.DEPOLOGY_LARGE_INPUT_CLUSTER_INFO);
		loadNodeForLarge();
		configDeplymentLarge();
	}

	private void configDeplymentMedium() {
		if (selectFeaturesList.size() <= 0)
			return;
		// new install should not enable choose existing clusters
		configClusterNameMedium();
	}

	private void configClusterNameMedium() {
		TextCustomPanelUtils.logInput("feature",
				getFeatureName(selectFeaturesList.get(curConfigIndexMedium)));
		TextCustomPanelUtils.showSubTitle1(Messages.DEPOLOGY_CLUSTER_TABLE_FEATURE + ": " + getConsoleFeatureName(selectFeaturesList.get(curConfigIndexMedium)));
		
		TextCustomPanelUtils.showSubTitle2(Messages.DEPOLOGY_CLUSTER_TITLE);

		if (LCClusterList.size() <= 0) {
			specifyClusterNameMedium();
			return;
		}

		if (LCClusterList.size() > 0) {
			int input = TextCustomPanelUtils.singleSelect(
					Messages.DEPOLOGY_INPUT_CLUSTER_INFO_CONSOLE, new String[] {
							Messages.DEPOLOGY_INPUT_SPECIFY_CLUSTER_NAME,
							Messages.DEPOLOGY_INPUT_CHOOSE_CLUSTER }, 1,
					new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
			if (input < 0) {
				// if user wants to go back and the return flag is set, reset
				// return flag and return to the table display
				if (returnFlag) {
					returnFlag = false;
					showDeplymentTableMedium();
					return;
				}
				// if user wants to go back when he is in the first cluster, go
				// back to deployment choosing
				if (curConfigIndexMedium == 0) {
					chooseDeplymentType();
				}
				// if user wants to go back in other clusters, reduce the
				// configuration index and re-enter the function
				else {
					curConfigIndexMedium--;
					configClusterNameMedium();
				}
				return;
			}
			if (input == 1) {
				specifyClusterNameMedium();
			} else if (input == 2) {

				chooseClusterNameFromExistingClustersMedium();
				return;
			}
		} else {

			specifyClusterNameMedium();
		}
	}

	private void chooseClusterNameFromExistingClustersMedium() {
		TextCustomPanelUtils.logInput("user input mode",
				"choose cluster name from existing clusters");

		int input = TextCustomPanelUtils.singleSelect(
				Messages.DEPOLOGY_CLUSTER,
				convertArrayToStringList(LCClusterList), 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (input < 0) {
			configClusterNameMedium();
			return;
		}
		List<NodeServerPair> curPair = getFeatureNodeServerPairList(selectFeaturesList
				.get(curConfigIndexMedium));
		String cluster = LCClusterList.get(input - 1);

		List<NodeServerPair> existPairList = getExistNodeServerPairList(cluster);
		curPair.clear();
		for (int i = 0; i < existPairList.size(); i++) {
			NodeServerPair tmp = existPairList.get(i).clone();
			int nodeIndex = originalNodeList.indexOf(tmp.getNodeName());
			if (nodeIndex < 0)
				continue;
			tmp.setNodeIndex(nodeIndex);
			tmp.setApplicationName(getFeatureName(selectFeaturesList
					.get(curConfigIndexMedium)));
			curPair.add(tmp);
		}
		TextCustomPanelUtils.logInput("select cluster", cluster);
		displayNodesForCluster(cluster);
		configNextApplication();
	}

	private void specifyClusterNameMedium() {
		TextCustomPanelUtils
				.logInput("user input mode", "specify cluster name");
		List<NodeServerPair> curPair = getFeatureNodeServerPairList(selectFeaturesList
				.get(curConfigIndexMedium));
		if (curPair == null || curPair.size() == 0) {
			TextCustomPanelUtils.showError(Messages.DEPOLOGY_NO_NODE_ERROR);
			log.error("No node available for the selected cluster.");
			chooseDeplymentType();
			return;
		}
		String curCluster = curPair.get(0).getClusterName();
		String input = TextCustomPanelUtils.getInput(Messages.DEPOLOGY_CLUSTER,
				curCluster);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			if (returnFlag) {
				returnFlag = false;
				showDeplymentTableMedium();
				return;
			}
			if (curConfigIndexMedium == 0) {
				chooseDeplymentType();
			} else {
				curConfigIndexMedium--;
				configClusterNameMedium();
			}
			return;
		}
		String cluster = input.trim();
		TextCustomPanelUtils.logInput("cluster name", cluster);
		// check input validity
		if (!verifyInputValid(cluster)) {
//				|| (!TextCustomPanelUtils.containsIgnoreCase(LCClusterList, cluster) && TextCustomPanelUtils.containsIgnoreCase(allClusterList, cluster))) {
			TextCustomPanelUtils.showError(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
			log.error("Cluster name is invalid.");
			specifyClusterNameMedium();
			return;
		}
		
		//check if do fresh install, can't install a feature on an existing cluster, but "modify add" can
		if(isInstall()){
			if(TextCustomPanelUtils.containsIgnoreCase(allClusterList, cluster)){
				TextCustomPanelUtils.showError(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
				log.error("Cluster name is invalid.");
				specifyClusterNameMedium();
				return;
			}
		}
		
		// cluster name changes, or the current cluster name itself is existing
		// cluster
		if (!curCluster.equalsIgnoreCase(cluster)
				|| TextCustomPanelUtils.containsIgnoreCase(LCClusterList,
						cluster)) {
			// if the cluster is already installed cluster,load the node
			// selection info and server member name and display, then configure
			// the next application
			if (TextCustomPanelUtils.containsIgnoreCase(LCClusterList, cluster)) {
				List<NodeServerPair> existPairList = getExistNodeServerPairList(cluster);
				curPair.clear();
				for (int i = 0; i < existPairList.size(); i++) {
					NodeServerPair tmp = existPairList.get(i).clone();
					int nodeIndex = originalNodeList.indexOf(tmp.getNodeName());
					if (nodeIndex < 0)
						continue;
					tmp.setNodeIndex(nodeIndex);
					tmp.setApplicationName(getFeatureName(selectFeaturesList
							.get(curConfigIndexMedium)));
					curPair.add(tmp);
				}
				displayNodesForCluster(cluster);
				configNextApplication();
				return;
			}
			// if cluster name has been created, load existing nodes and server
			// member
			// names
			if (!isNewCreatedCluster(cluster)) {
				cluster = getOriginalFormCluster(cluster);
				// load selected nodes' indices and server members for the user
				// created cluster
				List<Integer> curNodes = getSelectedNodesForNewCreatedCluster(cluster);
				for (int i = 0; i < curPair.size(); i++) {
					NodeServerPair curnodeserver = curPair.get(i);
					curnodeserver.setClusterName(cluster);
					if (curNodes != null)
						curnodeserver.setSelected(curNodes
								.contains(new Integer(i)));
					else
						curnodeserver.setSelected(false);

					for (int j = 0; j < selectFeaturesList.size(); j++) {
						List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
								.get(j));
						for (int k = 0; k < list.size(); k++) {
							NodeServerPair cur = list.get(k);
							if (cur.getClusterName().equalsIgnoreCase(cluster)
									&& cur.getNodeName().equals(
											originalNodeList.get(curnodeserver
													.getNodeIndex())))
								curnodeserver.setServerMemberName(cur
										.getServerMemberName());
						}
					}
				}
			}
			// new cluster name, the first node is selected. add the cluster and
			// node to cluster node pair
			else {
				addClusterNodePair(cluster, 0);
				for (int i = 0; i < curPair.size(); i++) {
					NodeServerPair curnode = curPair.get(i);
					curnode.setClusterName(cluster);
					curnode.setSelected(i == 0);
					curnode.setServerMemberName(cluster + "_server"
							+ (curnode.getNodeIndex() + 1));

				}
			}
		}
		chooseNodesForClusterMedium(cluster);
	}

	private void displayNodesForCluster(String cluster) {
		// DEPOLOGY_NODE_NAME_SERVERMEMBER_NAME
		List<NodeServerPair> existPairList = getExistNodeServerPairList(cluster);
		for (NodeServerPair curPair : existPairList) {
			if (curPair.isSelected()) {
				TextCustomPanelUtils
						.showText(Messages.DEPOLOGY_NODE_NAME_CONSOLE
								+ curPair.getNodeName());
				TextCustomPanelUtils
						.showText(Messages.DEPOLOGY_SERVERMEMBER_NAME_CONSOLE
								+ curPair.getServerMemberName());
			}
		}
	}

	private void chooseNodesForClusterMedium(String cluster) {
		List<Integer> curOpts = getSelectedNodesForNewCreatedCluster(cluster);
		int[] opts = new int[originalNodeList.size()];
		for (int i = 0; i < opts.length; i++) {
			if (curOpts != null && curOpts.contains(new Integer(i)))
				opts[i] = 1;
			else
				opts[i] = 0;
		}
		int[] input = TextCustomPanelUtils.multiSelect(
				Messages.DEPOLOGY_EXISTING_NODES,
				convertArrayToStringList(originalNodeList), opts,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (input != null && input[0] < 0) {
			configClusterNameMedium();
			return;
		}
		if (input == null) {// prepare to move on
			boolean hasNode = false;
			for (int i = 0; i < opts.length; i++) {
				if (opts[i] == 1) {
					hasNode = true;
					break;
				}
			}
			if (!hasNode) {
				TextCustomPanelUtils
						.showError(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
				log.error("CLFRP0018E: Node selection must not be empty.");
				chooseNodesForClusterMedium(cluster);
				return;
			}
			for (int i = 0; i < opts.length; i++)
				TextCustomPanelUtils.logInput("choose node index " + i,
						Boolean.toString(opts[i] == 1));
			configServerMemberNameMedium();
		} else {
			curOpts = new ArrayList<Integer>();
			for (int i = 0; i < opts.length; i++) {
				if (opts[i] == 1)
					curOpts.add(new Integer(i));
			}
			updateClusterNodeInfo(cluster, curOpts);
			synchronizeClusterNodeInfo(cluster, curOpts);
			chooseNodesForClusterMedium(cluster);
		}
	}

	/** configure the server member name for the specified application */
	private void configServerMemberNameMedium() {
		List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
				.get(curConfigIndexMedium));
		for (int i = 0; i < list.size(); i++) {
			NodeServerPair curnode = list.get(i);
			if (curnode.isSelected()) {
				String input = TextCustomPanelUtils.getInput(
						Messages.DEPOLOGY_ENTER_SERVER_MEMBER
								+ curnode.getNodeName(),
						curnode.getServerMemberName());
				if (input.trim().toUpperCase()
						.equals(Messages.PREVIOUS_INPUT_INDEX)) {
					chooseNodesForClusterMedium(curnode.getClusterName());
					return;
				}
				TextCustomPanelUtils.logInput("server member name for node "
						+ curnode.getNodeName(), input);
				// check input validity
				if (!verifyInputValid(input.trim())) {
					TextCustomPanelUtils
							.showError(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
					log.error("Server name is invalid.");
					i--;
					continue;
				}

				// check server member name duplication
				if (isInstall() && !synchronizeServerMemberName(curnode.getClusterName(),
						curnode.getNodeName(), input.trim())) {
					TextCustomPanelUtils
							.showError(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
					log.error("Server name is invalid.");
					i--;
					continue;
				}
				curnode.setServerMemberName(input.trim());
			}
		}
		configNextApplication();
	}

	private void configNextApplication() {
		// When configuration index hasn't reached the border, add the index by
		// one
		if (curConfigIndexMedium < selectFeaturesList.size() - 1) {
			curConfigIndexMedium++;

			if (returnFlag) {
				returnFlag = false;
				curConfigIndexMedium = 0;
				showDeplymentTableMedium();
				return;
			}
			// configure the next application
			configClusterNameMedium();
		}
		// When configuration index has reached the border, reset the
		// configuration index and go to table display
		else {
			curConfigIndexMedium = 0;
			showDeplymentTableMedium();
		}
	}

	private void showDeplymentTableMedium() {
		int maxLenApplication = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DEPOLOGY_CLUSTER_TABLE_FEATURE, Messages.HOMEPAGE,
				Messages.NEWS, Messages.SEARCH, Messages.ACTIVITIES,
				Messages.BLOGS, Messages.COMMUNITIES, Messages.DOGEAR,
				Messages.PROFILES, Messages.WIKIS, Messages.FILES,
				Messages.FORUMS, Messages.MOBILE, Messages.MODERATION,
				Messages.METRICS, Messages.CCM,
				Messages.IC360 });
		// The max length of each cell depends on the max length of all elements
		// of the same column including the title.
		int maxLenClustername = getMaxLenClusterName();
		int maxLen = TextCustomPanelUtils
				.getMaxLength(convertArrayToStringList(originalNodeList));
		int maxLenNodename = maxLen > 4 ? maxLen : 4;
		int maxLenServerMembername = getMaxLenServerMemberName();
		Iterator<List<Integer>> it = clusterNodePairMedium.values().iterator();
		while (it.hasNext()) {
			List<Integer> cur = it.next();
			for (int i = 0; i < cur.size(); i++) {
				if (cur.get(i) > maxLen)
					maxLen = cur.get(i);
			}
		}
		int lenApp = maxLenApplication + 4;
		int lenCluster = maxLenClustername + 4;
		int lenNode = maxLenNodename + 4;
		int lenServerMember = maxLenServerMembername + 4;

		TextCustomPanelUtils.showText("\n"
				+ Messages.DEPOLOGY_MEDIUM_INPUT_CLUSTER_INFO_CONSOLE);

		TextCustomPanelUtils.printTitleRow(new String[] {
				Messages.DEPOLOGY_CLUSTER_TABLE_FEATURE,
				Messages.DEPOLOGY_CLUSTER_TABLE_CLUSTER,
				Messages.DEPOLOGY_CLUSTER_TABLE_NODES,
				Messages.DEPOLOGY_CLUSTER_TABLE_SERVERS }, new int[] { lenApp,
				lenCluster, lenNode, lenServerMember });

		for (int i = 0; i < selectFeaturesList.size(); i++) {
			List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
					.get(i));
			List<Integer> selectedNodes = null;

			if (list == null || list.size() == 0) {
				TextCustomPanelUtils.showError("No server info for feature:"
						+ selectFeaturesList.get(i));
				log.error("No server info for feature:"
						+ selectFeaturesList.get(i));
				continue;
			} else
				selectedNodes = getSelectedNodesForNewCreatedCluster(list
						.get(0).getClusterName());
			if (selectedNodes == null)
				selectedNodes = getSelectedNodesForExistingCluster(list.get(0)
						.getClusterName());

			// print application with only one node selected
			if (selectedNodes.size() == 1) {
				for (int j = 0; j < list.size(); j++) {
					NodeServerPair curnode = list.get(j);
					if (curnode.isSelected()) {
						TextCustomPanelUtils.printSingleLineRow(
								i + 1,
								new String[] { curnode.getApplicationName(),
										curnode.getClusterName(),
										curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
						break;
					}
				}
			}
			// print application with multiple nodes selected
			else if (selectedNodes.size() > 1) {
				boolean isFirst = true;
				int lastSelectIndex = 0;
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).isSelected())
						lastSelectIndex = j;
				}
				for (int j = 0; j < list.size(); j++) {
					NodeServerPair curnode = list.get(j);
					if (curnode.isSelected() && isFirst) {
						TextCustomPanelUtils.printSingleLineRowNoBottom(
								i + 1,
								new String[] { curnode.getApplicationName(),
										curnode.getClusterName(),
										curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
						isFirst = false;
					} else if (curnode.isSelected() && j < lastSelectIndex) {
						TextCustomPanelUtils.printSingleLineRowMiddle(0,
								new String[] { "", "", curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
					} else if (curnode.isSelected() && j == lastSelectIndex) {
						TextCustomPanelUtils.printSingleLineRow(0,
								new String[] { "", "", curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
					}
				}
			}
		}
		String[] indices = new String[selectFeaturesList.size() + 3];
		for (int i = 0; i < selectFeaturesList.size(); i++) {
			indices[i + 3] = (i + 1) + "";
		}
		indices[0] = Messages.PREVIOUS_INPUT_INDEX;
		indices[1] = "R";
		indices[2] = "";
		String input = TextCustomPanelUtils.getInput("", "", indices).trim();
		if (input.toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			chooseDeplymentType();
			return;
		}
		if (input.toUpperCase().equals("R")) {
			resetDefault();
			showDeplymentTableMedium();
			return;
		}
		if (input.length() > 0) {
			// go to the specified application
			curConfigIndexMedium = Integer.parseInt(input) - 1;
			returnFlag = true;
			configClusterNameMedium();
			return;
		}
		saveDataMediumLarge();
		goToNextMedium();
	}

	private void goToNextMedium() {
		String input1 = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] { Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.PREVIOUS_INPUT_INDEX });
		if (input1.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			showDeplymentTableMedium();
			return;
		}
		if (input1.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			chooseDeplymentType();
		}
	}

	// --------------Large Deployment--------------

	private void configClusterNameLarge() {
		TextCustomPanelUtils.logInput("feature",
				getFeatureName(selectFeaturesList.get(curConfigIndexLarge)));
		TextCustomPanelUtils.showSubTitle1(Messages.DEPOLOGY_CLUSTER_TABLE_FEATURE + ": "
					+ getConsoleFeatureName(selectFeaturesList.get(curConfigIndexLarge)));
		
		List<NodeServerPair> curPair = getFeatureNodeServerPairList(selectFeaturesList
				.get(curConfigIndexLarge));
		if (curPair == null || curPair.size() == 0) {
			TextCustomPanelUtils.showError(Messages.DEPOLOGY_NO_NODE_ERROR);
			log.error("No node available for the selected cluster.");
			chooseDeplymentType();
			return;
		}
		String curCluster = curPair.get(0).getClusterName();
		String input = TextCustomPanelUtils.getInput(Messages.DEPOLOGY_CLUSTER,
				curCluster);
		if (input.trim().toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			if (returnFlag) {
				returnFlag = false;
				showDeplymentTableLarge();
				return;
			}
			if (curConfigIndexLarge == 0) {
				chooseDeplymentType();
				return;
			} else {
				curConfigIndexLarge--;
				configClusterNameLarge();
				return;
			}
		}
		String cluster = input.trim();
		TextCustomPanelUtils.logInput("cluster name", cluster);
		// check input validity, check cluster duplication
		if (!verifyInputValid(cluster)) {
//				|| (!TextCustomPanelUtils.containsIgnoreCase(LCClusterList, cluster) && TextCustomPanelUtils.containsIgnoreCase(allClusterList, cluster))) {
			TextCustomPanelUtils.showError(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
			log.error("Cluster name is invalid.");
			configClusterNameLarge();
			return;
		}

		// cluster name changes
		if (!curCluster.equalsIgnoreCase(cluster) || TextCustomPanelUtils.containsIgnoreCase(allClusterList, cluster)) {
			// if the cluster name already exists or is already installed
			// cluster, show error
			if (clusterNameExistLarge(cluster)
					|| TextCustomPanelUtils.containsIgnoreCase(allClusterList, cluster) || !isNewCreatedCluster(cluster)) {
				TextCustomPanelUtils.showError(Messages.DEPOLOGY_CLUSTER_INPUT_NO_VALID);
				log.error("Cluster name is invalid.");
				configClusterNameLarge();
				return;
			}

			// new cluster name, the first node is selected. add the cluster and
			// node to cluster node pair
			else {
				// remove the old cluster
				removeClusterNodePair(curCluster);
				// add a new cluster and select the first node by default
				addClusterNodePair(cluster, 0);
				for (int i = 0; i < curPair.size(); i++) {
					NodeServerPair curnode = curPair.get(i);
					curnode.setClusterName(cluster);
					curnode.setSelected(i == 0);
					curnode.setServerMemberName(cluster + "_server"
							+ (curnode.getNodeIndex() + 1));

				}
			}
		}
		chooseNodesForClusterLarge(cluster);
	}

	private boolean clusterNameExistLarge(String cluster) {
		for (int i = 0; i < selectFeaturesList.size(); i++) {
			List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
					.get(i));
			if (list == null || list.size() == 0)
				continue;
			NodeServerPair cur = list.get(0);
			if (cur.getClusterName().equalsIgnoreCase(cluster)) {
				return true;
			}
		}
		return false;
	}

	private void chooseNodesForClusterLarge(String cluster) {
		List<Integer> curOpts = getSelectedNodesForNewCreatedCluster(cluster);
		int[] opts = new int[originalNodeList.size()];
		for (int i = 0; i < opts.length; i++) {
			if (curOpts != null && curOpts.contains(new Integer(i)))
				opts[i] = 1;
			else
				opts[i] = 0;
		}
		int[] input = TextCustomPanelUtils.multiSelect(
				Messages.DEPOLOGY_EXISTING_NODES,
				convertArrayToStringList(originalNodeList), opts,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (input != null && input[0] < 0) {
			configClusterNameLarge();
			return;
		}
		if (input == null) {// prepare to move on
			boolean hasNode = false;
			for (int i = 0; i < opts.length; i++) {
				if (opts[i] == 1) {
					hasNode = true;
					break;
				}
			}
			if (!hasNode) {
				TextCustomPanelUtils
						.showError(Messages.DEPOLOGY_NODE_SELECTION_ERROR);
				log.error("CLFRP0018E: Node selection must not be empty.");
				chooseNodesForClusterLarge(cluster);
				return;
			}
			for (int i = 0; i < opts.length; i++) {
				TextCustomPanelUtils.logInput("choose node index " + i,
						Boolean.toString(opts[i] == 1));
			}
			configServerMemberNameLarge();
		} else {
			curOpts = new ArrayList<Integer>();
			for (int i = 0; i < opts.length; i++) {
				if (opts[i] == 1)
					curOpts.add(new Integer(i));
			}
			updateClusterNodeInfo(cluster, curOpts);
			synchronizeClusterNodeInfo(cluster, curOpts);
			chooseNodesForClusterLarge(cluster);
		}
	}

	/** configure the server member name for the specified application */
	private void configServerMemberNameLarge() {
		List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
				.get(curConfigIndexLarge));
		for (int i = 0; i < list.size(); i++) {
			NodeServerPair curnode = list.get(i);
			if (curnode.isSelected()) {
				String input = TextCustomPanelUtils.getInput(
						Messages.DEPOLOGY_ENTER_SERVER_MEMBER
								+ curnode.getNodeName(),
						curnode.getServerMemberName());
				if (input.trim().toUpperCase()
						.equals(Messages.PREVIOUS_INPUT_INDEX)) {
					chooseNodesForClusterLarge(curnode.getClusterName());
					return;
				}
				TextCustomPanelUtils.logInput("server member name for node "
						+ curnode.getNodeName(), input);
				// check input validity
				if (!verifyInputValid(input.trim())) {
					TextCustomPanelUtils
							.showError(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
					log.error("Server name is invalid.");
					i--;
					continue;
				}

				// check server member name duplication
				if (isInstall() && !synchronizeServerMemberName(curnode.getClusterName(),
						curnode.getNodeName(), input.trim())) {
					TextCustomPanelUtils
							.showError(Messages.DEPOLOGY_SERVER_INPUT_NO_VALID);
					log.error("Server name is invalid.");
					i--;
					continue;
				}
				curnode.setServerMemberName(input.trim());
			}
		}
		if (curConfigIndexLarge < selectFeaturesList.size() - 1) {
			curConfigIndexLarge++;
			// return to the table display rather than move on to the next
			// application
			if (returnFlag) {
				returnFlag = false;
				curConfigIndexLarge = 0;
				showDeplymentTableLarge();
				return;
			}
			// configure the next
			configClusterNameLarge();
		} else {
			curConfigIndexLarge = 0;
			showDeplymentTableLarge();
		}
	}

	private void showDeplymentTableLarge() {
		int maxLenApplication = TextCustomPanelUtils.getMaxLength(new String[] {
				Messages.DEPOLOGY_CLUSTER_TABLE_FEATURE, Messages.HOMEPAGE,
				Messages.NEWS, Messages.SEARCH, Messages.ACTIVITIES,
				Messages.BLOGS, Messages.COMMUNITIES, Messages.DOGEAR,
				Messages.PROFILES, Messages.WIKIS, Messages.FILES,
				Messages.FORUMS, Messages.MOBILE, Messages.MODERATION,
				Messages.METRICS, Messages.CCM,
				Messages.IC360 });
		int maxLenClustername = getMaxLenClusterName();
		int maxLen = TextCustomPanelUtils
				.getMaxLength(convertArrayToStringList(originalNodeList));
		int maxLenNodename = maxLen > 4 ? maxLen : 4;
		int maxLenServerMembername = getMaxLenServerMemberName();
		Iterator<List<Integer>> it = clusterNodePairLarge.values().iterator();
		while (it.hasNext()) {
			List<Integer> cur = it.next();
			for (int i = 0; i < cur.size(); i++) {
				if (cur.get(i) > maxLen)
					maxLen = cur.get(i);
			}
		}
		int lenApp = maxLenApplication + 4;
		int lenCluster = maxLenClustername + 4;
		int lenNode = maxLenNodename + 4;
		int lenServerMember = maxLenServerMembername + 4;

		TextCustomPanelUtils.showText("\n"
				+ Messages.DEPOLOGY_LARGE_INPUT_CLUSTER_INFO_CONSOLE);

		TextCustomPanelUtils.printTitleRow(new String[] {
				Messages.DEPOLOGY_CLUSTER_TABLE_FEATURE,
				Messages.DEPOLOGY_CLUSTER_TABLE_CLUSTER,
				Messages.DEPOLOGY_CLUSTER_TABLE_NODES,
				Messages.DEPOLOGY_CLUSTER_TABLE_SERVERS }, new int[] { lenApp,
				lenCluster, lenNode, lenServerMember });

		for (int i = 0; i < selectFeaturesList.size(); i++) {
			List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
					.get(i));
			List<Integer> selectedNodes = getSelectedNodesForNewCreatedCluster(list
					.get(0).getClusterName());
			// print application with only one node selected
			if (selectedNodes.size() == 1) {
				for (int j = 0; j < list.size(); j++) {
					NodeServerPair curnode = list.get(j);
					if (curnode.isSelected()) {
						TextCustomPanelUtils.printSingleLineRow(
								i + 1,
								new String[] { curnode.getApplicationName(),
										curnode.getClusterName(),
										curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
						break;
					}
				}
			}
			// print application with multiple nodes selected
			else if (selectedNodes.size() > 1) {
				int lastSelectNodeIndex = 0;
				for (int j = 0; j < list.size(); j++)
					if (list.get(j).isSelected())
						lastSelectNodeIndex = j;

				boolean isFirst = true;
				for (int j = 0; j < list.size(); j++) {
					NodeServerPair curnode = list.get(j);
					if (curnode.isSelected() && isFirst) {
						TextCustomPanelUtils.printSingleLineRowNoBottom(
								i + 1,
								new String[] { curnode.getApplicationName(),
										curnode.getClusterName(),
										curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
						isFirst = false;
					} else if (curnode.isSelected() && j < lastSelectNodeIndex) {
						TextCustomPanelUtils.printSingleLineRowMiddle(0,
								new String[] { "", "", curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
					} else if (curnode.isSelected() && j == lastSelectNodeIndex) {
						TextCustomPanelUtils.printSingleLineRow(0,
								new String[] { "", "", curnode.getNodeName(),
										curnode.getServerMemberName() },
								new int[] { lenApp, lenCluster, lenNode,
										lenServerMember });
					}
				}
			}
		}
		String[] indices = new String[selectFeaturesList.size() + 3];
		for (int i = 0; i < selectFeaturesList.size(); i++) {
			indices[i + 3] = (i + 1) + "";
		}
		indices[0] = Messages.PREVIOUS_INPUT_INDEX;
		indices[1] = "R";
		indices[2] = "";
		String input = TextCustomPanelUtils.getInput("", "", indices).trim();
		if (input.toUpperCase().equals(Messages.PREVIOUS_INPUT_INDEX)) {
			chooseDeplymentType();
			return;
		}
		if (input.toUpperCase().equals("R")) {
			resetDefault();
			showDeplymentTableLarge();
			return;
		}
		if (input.length() > 0) {
			// go to the specified application
			curConfigIndexLarge = Integer.parseInt(input) - 1;
			returnFlag = true;
			configClusterNameLarge();
			return;
		}
		saveDataMediumLarge();
		goToNextLarge();
	}

	private void goToNextLarge() {
		String input1 = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] { Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.PREVIOUS_INPUT_INDEX });
		if (input1.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			showDeplymentTableLarge();
			return;
		}
		if (input1.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			chooseDeplymentType();
		}
	}

	// --------------------------------------------
	public void resetDefault() {
		isReset = true;
		if (topologyType == MEDIUM_TOPOLOGY)
			loadNodeForMedium();
		else if (topologyType == LARGE_TOPOLOGY)
			loadNodeForLarge();
	}

	private void configDeplymentLarge() {
		if (selectFeaturesList.size() <= 0)
			return;

		configClusterNameLarge();
	}

	private void loadClusterInfo() {
		profile = getProfile();
		String clusterListStr = profile.getUserData("user.clusterlist");
		String nodeListStr = profile.getUserData("user.nodeslist");
		String clusteFullinfo = profile.getUserData("user.clusterfullinfo");

		log.info("loadClusterInfo clusterListStr : " + clusterListStr);
		log.info("loadClusterInfo nodeListStr : " + nodeListStr);
		log.info("loadClusterInfo clusteFullinfo : " + clusteFullinfo);

		if (!isLoadCluster) {
			// first load for all cluster including lc cluster and others
			if (allClusterList.size() == 0) {

				if (clusterListStr != null && !clusterListStr.trim().equals("")) {
					String[] clusters = clusterListStr.split(",");
					for (int i = 0; i < clusters.length; i++) {
						if (!TextCustomPanelUtils.containsIgnoreCase(
								allClusterList, clusters[i])) {
							allClusterList.add(clusters[i]);
						}
					}
				}
			}

			log.info("loadClusterInfo all cluster in was : "
					+ allClusterList.toString());
			// for lc cluster

			String lcclusteStr = this.readClusterInfoProperties("clusterList");

			// first load for cluster
			if (LCClusterList.size() == 0) {

				if (lcclusteStr != null && !lcclusteStr.trim().equals("")) {
					String[] clusters = lcclusteStr.split(",");
					for (int i = 0; i < clusters.length; i++) {
						if (!TextCustomPanelUtils.containsIgnoreCase(
								LCClusterList, clusters[i])
								&& TextCustomPanelUtils.containsIgnoreCase(
										allClusterList, clusters[i])) {
							LCClusterList.add(clusters[i]);
						}
					}
				}
			}

			log.info("loadClusterInfo lc clusters : "
					+ LCClusterList.toString());

			originalNodeList.clear();

			// first load for nodes info
			if (originalNodeList.size() == 0) {

				if (nodeListStr != null && !nodeListStr.trim().equals("")) {
					String[] nodes = nodeListStr.split(",");

					for (int i = 0; i < nodes.length; i++) {
						if (!originalNodeList.contains(nodes[i])) {
							this.originalNodeList.add(nodes[i]);
						}
					}
					selectNodeIndices = new int[originalNodeList.size()];
					for (int i = 0; i < nodes.length; i++)
						selectNodeIndices[i] = 0;
				}
			}

			log.info("loadClusterInfo original nodes in was : "
					+ originalNodeList.toString());

			isLoadCluster = true;

			// first load exist cluster info
			if (clusteFullinfo == null || clusteFullinfo.trim().equals("")) {
				return;
			}
			if (existClusterInfoMap.size() == 0) {
				existClusterInfoMap = this.getExistedClusterInfo(clusteFullinfo);
			}
		}
		deleteRemovedClusters();
	}

	private boolean verifyInputValid(String input) {
		boolean result = true;
		try {
			result = !installValidator.containsInvalidChar(input)
					&& !installValidator.containsSpace(input);
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
		return result;
	}

	/**
	 * If one server member name changes, change other server members with the
	 * same node name and Cluster name accordingly. If the serverMemberName
	 * duplicates, return false and change nothing.
	 */
	private boolean synchronizeServerMemberName(String cluster, String node,
			String serverMember) {
		// check exist nodes
		for (String curCluster : allClusterList) {
			List<NodeServerPair> existPairList = getExistNodeServerPairList(curCluster);
			for (NodeServerPair tmp : existPairList) {
				log.info("servermember name:" + tmp.getServerMemberName()
						+ ",node name:" + tmp.getNodeName() + ";");
				if (tmp.isSelected()
						&& tmp.getServerMemberName().equalsIgnoreCase(
								serverMember) && tmp.getNodeName().equals(node))
					return false;
			}
		}
		// check new nodes
		for (int i = 0; i < selectFeaturesList.size(); i++) {
			List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
					.get(i));
			for (int j = 0; j < list.size(); j++) {
				NodeServerPair curnode = list.get(j);
				if (curnode.isSelected()
						&& curnode.getServerMemberName().equalsIgnoreCase(
								serverMember)) {
					if (curnode.getClusterName().equalsIgnoreCase(cluster)
							&& !(curnode.getNodeName().equals(node))) {
						return false;
					} else if (!curnode.getClusterName().equalsIgnoreCase(
							cluster)
							&& curnode.getNodeName().equals(node))
						return false;
				}
			}
		}
		// synchronize
		for (int i = 0; i < selectFeaturesList.size(); i++) {
			List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
					.get(i));
			for (int j = 0; j < list.size(); j++) {
				NodeServerPair curnode = list.get(j);
				if (curnode.getClusterName().equalsIgnoreCase(cluster)
						&& curnode.getNodeName().equals(node))
					curnode.setServerMemberName(serverMember);
			}
		}
		return true;
	}

	/**
	 * If one cluster has changed the node selection, then synchronize all
	 * features' node server pairs accordingly
	 */
	private void synchronizeClusterNodeInfo(String cluster,
			List<Integer> curOpts) {
		updateClusterNodeInfo(cluster, curOpts);
		for (int i = 0; i < selectFeaturesList.size(); i++) {
			List<NodeServerPair> list = getFeatureNodeServerPairList(selectFeaturesList
					.get(i));
			for (int j = 0; j < list.size(); j++) {
				NodeServerPair curnode = list.get(j);
				if (curnode.getClusterName().equalsIgnoreCase(cluster))
					curnode.setSelected(curOpts.contains(new Integer(j)));
			}
		}
	}

	private void loadNodeForMedium() {
		if (!isLoadMedium || isReset) {
			log.debug("load cluster for medium ");
			log.debug("setExisted_Clusters ");
			log.info("LCClusterList : " + LCClusterList.toString());

			log.debug("original node list : " + originalNodeList.toString());

			if (isReset)
				clusterNodePairMedium.clear();
			
			for (int i = 0; i < originalNodeList.size(); i++) {
				String nodetemp = (String) originalNodeList.get(i);
				String serverTemp = "server" + (i + 1);
				for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
					if (isReset && i == 0) {
						getFeatureNodeServerPairList(featureID).clear();

					}
					if (getIsFeatureSelected(featureID)) {
						if (featureID == CCM && profile.getUserData("user.ccm.existingDeployment").equals("true")){
							continue;
						}
						getFeatureNodeServerPairList(featureID)
								.add(new NodeServerPair(
										getConsoleFeatureName(featureID),
										getDefaultClusterName(featureID),
										nodetemp, i,
										getDefaultClusterName(featureID) + "_"
												+ serverTemp, i == 0 ? true
												: false, i == 0 ? true : false));

						if (i == 0
								&& !TextCustomPanelUtils.containsIgnoreCase(
										allClusterList,
										getDefaultClusterName(featureID)))
							addClusterNodePair(
									getDefaultClusterName(featureID), i);

						log.debug("getFeatureNodeServerPairList("
								+ getFeatureName(featureID)
								+ ", MEDIUM_TOPOLOGY) "
								+ getFeatureNodeServerPairList(featureID)
										.size());
					}
				}

			}

			isLoadMedium = true;
			isReset = false;
		}

	}

	private void loadNodeForLarge() {
		if (!isLoadLarge || isReset) {
			log.debug("load cluster for large ");
			log.debug("setExisted_Clusters ");
			log.debug("LCClusterList : " + LCClusterList.toString());
			log.debug("original node list : " + originalNodeList.toString());

			if (isReset)
				clusterNodePairLarge.clear();

			for (int i = 0; i < originalNodeList.size(); i++) {

				String nodetemp = (String) originalNodeList.get(i);
				String serverTemp = "server" + (i + 1);

				for (int feature = ACTIVITIES; feature <= IC360; feature++) {
					if (isReset && i == 0) {
						getFeatureNodeServerPairList(feature).clear();
					}
					if (getIsFeatureSelected(feature)) {
						if (feature == CCM && profile.getUserData("user.ccm.existingDeployment").equals("true")){
							continue;
						}
					getFeatureNodeServerPairList(feature).add(
							new NodeServerPair(getConsoleFeatureName(feature),
									getDefaultClusterName(feature), nodetemp,
									i, getDefaultClusterName(feature) + "_"
											+ serverTemp,
									i == 0 ? true : false, i == 0 ? true
											: false));
					if (i == 0
							&& !TextCustomPanelUtils.containsIgnoreCase(
									allClusterList,
									getDefaultClusterName(feature)))
						addClusterNodePair(getDefaultClusterName(feature), i);
					}
				}

			}
			isLoadLarge = true;
			isReset = false;
		}
	}

	private String readClusterInfoProperties(String infoType) {

		profile = this.getProfile();
		if (profile != null) {
			try {

				String appDir = profile
						.getUserData("user.connections.install.location");
				String clusterInfoPath = appDir + File.separator
						+ "config.properties";
				log.debug("config property path : " + clusterInfoPath);
				File clusterProFile = new File(clusterInfoPath);
				if (!clusterProFile.exists()) {
					return null;
				}

				InputStream insrc = new BufferedInputStream(
						new FileInputStream(clusterProFile));
				Properties props = new Properties();
				props.load(insrc);

				insrc.close();
				ArrayList<String> temClusterList = new ArrayList<String>();
				if (infoType.equals("clusterList")) {
					for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
						String clusterName = props
								.getProperty(getFeatureName(featureID)
										+ ".ClusterName");
						if (clusterName != null
								&& !clusterName.trim().equals("")) {
							if (!temClusterList.contains(clusterName)) {
								temClusterList.add(clusterName);
							}
						}
					}

					StringBuffer temp = new StringBuffer();
					for (int i = 0; i < temClusterList.size(); i++) {
						temp.append(temClusterList.get(i) + ",");
					}
					log.debug(temp.toString());
					return temp.toString();
				} else {

					StringBuffer clusterInfoTemp = new StringBuffer();

					String clusterName = props.getProperty("news.ClusterName");
					String firstNodeInfo = props
							.getProperty("news.FirstNodeName");
					String secondaryNodeInfo = props
							.getProperty("news.SecondaryNodesNames");

					String fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("search.ClusterName");
					firstNodeInfo = props.getProperty("search.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("search.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("profiles.ClusterName");
					firstNodeInfo = props.getProperty("profiles.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("profiles.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("communities.ClusterName");
					firstNodeInfo = props
							.getProperty("communities.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("communities.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("dogear.ClusterName");
					firstNodeInfo = props.getProperty("dogear.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("dogear.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("wikis.ClusterName");
					firstNodeInfo = props.getProperty("wikis.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("wikis.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("files.ClusterName");
					firstNodeInfo = props.getProperty("files.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("files.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("forums.ClusterName");
					firstNodeInfo = props.getProperty("forums.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("forums.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("mobile.ClusterName");
					firstNodeInfo = props.getProperty("mobile.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("mobile.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("moderation.ClusterName");
					firstNodeInfo = props
							.getProperty("moderation.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("moderation.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("activities.ClusterName");
					firstNodeInfo = props
							.getProperty("activities.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("activities.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("blogs.ClusterName");
					firstNodeInfo = props.getProperty("blogs.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("blogs.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("homepage.ClusterName");
					firstNodeInfo = props.getProperty("homepage.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("homepage.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("metrics.ClusterName");
					firstNodeInfo = props.getProperty("metrics.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("metrics.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}
					
					clusterName = props.getProperty("ccm.ClusterName");
					firstNodeInfo = props.getProperty("ccm.FirstNodeName");
					secondaryNodeInfo = props.getProperty("ccm.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					clusterName = props.getProperty("ic360.ClusterName");
					firstNodeInfo = props.getProperty("ic360.FirstNodeName");
					secondaryNodeInfo = props
							.getProperty("ic360.SecondaryNodesNames");

					fullNodeInfo = firstNodeInfo + ",";
					if (secondaryNodeInfo != null
							&& !secondaryNodeInfo.trim().equals("")) {
						fullNodeInfo = firstNodeInfo + "," + secondaryNodeInfo;
					}

					if (clusterName != null && !clusterName.trim().equals("")) {
						if (!temClusterList.contains(clusterName)) {
							clusterInfoTemp.append(clusterName + ":"
									+ fullNodeInfo + ";");
						}
					}

					log.debug(clusterInfoTemp.toString());
					return clusterInfoTemp.toString();

				}
			} catch (Exception e) {
				log.error(e);
				// e.printStackTrace();
			}
		}
		return null;
	}

	private String getDefaultClusterName(int feature) {
		if (topologyType == MEDIUM_TOPOLOGY) {
			switch (feature) {
			case ACTIVITIES:
				return "AppsCluster";
			case BLOGS:
				return "AppsCluster";
			case COMMUNITIES:
				return "InfraCluster";
			case DOGEAR:
				return "AppsCluster";
			case HOMEPAGE:
				return "UtilCluster";
			case PROFILES:
				return "InfraCluster";
			case FILES:
				return "AppsCluster";
			case FORUM:
				return "AppsCluster";
			case WIKIS:
				return "AppsCluster";
			case MOBILE:
				return "AppsCluster";
			case MODERATION:
				return "UtilCluster";
			case SEARCH:
				return "InfraCluster";
			case METRICS:
				return "AppsCluster";
			case CCM:
				return "CCMCluster";
			case RTE:
				return "UtilCluster";
			case COMMON:
				return "InfraCluster";
			case WIDGET_CONTAINER:
				return "InfraCluster";
			case PUSH_NOTIFICATION:
				return "PushCluster";
			case IC360:
			    return "IC360Cluster";
			//case QUICK_RESULTS:
			//	return "Cluster2";
				// NEWS
			default:
				return "InfraCluster";
			}
		} else {
			switch (feature) {
			case ACTIVITIES:
				return "ActivitiesCluster";
			case BLOGS:
				return "BlogsCluster";
			case COMMUNITIES:
				return "CommunitiesCluster";
			case DOGEAR:
				return "DogearCluster";
			case HOMEPAGE:
				return "HomepageCluster";
			case PROFILES:
				return "ProfilesCluster";
			case FILES:
				return "FilesCluster";
			case FORUM:
				return "ForumCluster";
			case WIKIS:
				return "WikisCluster";
			case MOBILE:
				return "MobileCluster";
			case MODERATION:
				return "ModerationCluster";
			case SEARCH:
				return "SearchCluster";
			case METRICS:
				return "MetricsCluster";
			case CCM:
				return "CCMCluster";
				// NEWS
			case RTE:
				return "RTECluster";
			case COMMON:
				return "CommonCluster";
			case WIDGET_CONTAINER:
				return "WidgetContainerCluster";
			case PUSH_NOTIFICATION:
				return "PushNotificationCluster";
			case IC360:
				return "IC360Cluster";
			//case QUICK_RESULTS:
			//	return "QuickResultsCluster";
			default:
				return "NewsCluster";
			}
		}
	}

	private String getTopologyTypeName(int type) {
		switch (type) {
		case SMALL_TOPOLOGY:
			return "small";
		case MEDIUM_TOPOLOGY:
			return "medium";
		default:
			return "large";
		}
	}

	private List<NodeServerPair> getFeatureNodeServerPairList(int feature) {
		switch (feature) {
		case ACTIVITIES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return activitiesSelectNodeServerPairList;
			else
				return activitiesSelectNodeServerPairListLarge;
		case BLOGS:
			if (topologyType == MEDIUM_TOPOLOGY)
				return blogsSelectNodeServerPairList;
			else
				return blogsSelectNodeServerPairListLarge;
		case COMMUNITIES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return communitiesSelectNodeServerPairList;
			else
				return communitiesSelectNodeServerPairListLarge;
		case DOGEAR:
			if (topologyType == MEDIUM_TOPOLOGY)
				return dogearSelectNodeServerPairList;
			else
				return dogearSelectNodeServerPairListLarge;
		case HOMEPAGE:
			if (topologyType == MEDIUM_TOPOLOGY)
				return homepageSelectNodeServerPairList;
			else
				return homepageSelectNodeServerPairListLarge;
		case PROFILES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return profilesSelectNodeServerPairList;
			else
				return profilesSelectNodeServerPairListLarge;
		case FILES:
			if (topologyType == MEDIUM_TOPOLOGY)
				return filesSelectNodeServerPairList;
			else
				return filesSelectNodeServerPairListLarge;
		case FORUM:
			if (topologyType == MEDIUM_TOPOLOGY)
				return forumSelectNodeServerPairList;
			else
				return forumSelectNodeServerPairListLarge;
		case WIKIS:
			if (topologyType == MEDIUM_TOPOLOGY)
				return wikisSelectNodeServerPairList;
			else
				return wikisSelectNodeServerPairListLarge;
		case MOBILE:
			if (topologyType == MEDIUM_TOPOLOGY)
				return mobileSelectNodeServerPairList;
			else
				return mobileSelectNodeServerPairListLarge;
		case MODERATION:
			if (topologyType == MEDIUM_TOPOLOGY)
				return moderationSelectNodeServerPairList;
			else
				return moderationSelectNodeServerPairListLarge;
		case SEARCH:
			if (topologyType == MEDIUM_TOPOLOGY)
				return searchSelectNodeServerPairList;
			else
				return searchSelectNodeServerPairListLarge;
		case METRICS:
			if (topologyType == MEDIUM_TOPOLOGY)
				return metricsSelectNodeServerPairList;
			else
				return metricsSelectNodeServerPairListLarge;
		case CCM:
			if (topologyType == MEDIUM_TOPOLOGY)
				return ccmSelectNodeServerPairList;
			else
				return ccmSelectNodeServerPairListLarge;
		case RTE:
			if (topologyType == MEDIUM_TOPOLOGY)
				return rteSelectNodeServerPairList;
			else
				return rteSelectNodeServerPairListLarge;
		case COMMON:
			if (topologyType == MEDIUM_TOPOLOGY)
				return commonSelectNodeServerPairList;
			else
				return commonSelectNodeServerPairListLarge;
		case WIDGET_CONTAINER:
			if (topologyType == MEDIUM_TOPOLOGY)
				return widgetContainerSelectNodeServerPairList;
			else
				return widgetContainerSelectNodeServerPairListLarge;
		case PUSH_NOTIFICATION:
			if (topologyType == MEDIUM_TOPOLOGY)
				return pushNotificationSelectNodeServerPairList;
			else
				return pushNotificationSelectNodeServerPairListLarge;
		case IC360:
			if (topologyType == IC360)
				return ic360SelectNodeServerPairList;
			else
				return ic360SelectNodeServerPairListLarge;
		//case QUICK_RESULTS:
		//	if (topologyType == MEDIUM_TOPOLOGY)
		//		return quickResultsSelectNodeServerPairList;
		//	else
		//		return quickResultsSelectNodeServerPairListLarge;
			// NEWS
		default:
			if (topologyType == MEDIUM_TOPOLOGY)
				return newsSelectNodeServerPairList;
			else
				return newsSelectNodeServerPairListLarge;

		}
	}

	private void removeClusterNodePair(String cluster) {
		if (topologyType == MEDIUM_TOPOLOGY) {
			if (clusterNodePairMedium.containsKey(cluster))
				clusterNodePairMedium.remove(cluster);

		} else {
			if (clusterNodePairLarge.containsKey(cluster))
				clusterNodePairLarge.remove(cluster);
		}
	}

	/**
	 * create cluster-index pair if cluster is newly created, or append index to
	 * the existing cluster-index pair
	 */
	private void addClusterNodePair(String cluster, int nodeIndex) {
		if (topologyType == MEDIUM_TOPOLOGY) {
			if (clusterNodePairMedium.containsKey(cluster)) {
				List<Integer> list = clusterNodePairMedium.get(cluster);
				if (!list.contains(new Integer(nodeIndex)))
					list.add(nodeIndex);
				clusterNodePairMedium.put(cluster, list);
			} else {
				List<Integer> list = new ArrayList<Integer>();
				list.add(nodeIndex);
				clusterNodePairMedium.put(cluster, list);
			}
		} else {
			if (clusterNodePairLarge.containsKey(cluster)) {
				List<Integer> list = clusterNodePairLarge.get(cluster);
				if (!list.contains(new Integer(nodeIndex)))
					list.add(nodeIndex);
				clusterNodePairLarge.put(cluster, list);
			} else {
				List<Integer> list = new ArrayList<Integer>();
				list.add(nodeIndex);
				clusterNodePairLarge.put(cluster, list);
			}
		}
	}

	/** update the cluster-node selection */
	private void updateClusterNodeInfo(String cluster, List<Integer> nodeIndices) {
		if (topologyType == MEDIUM_TOPOLOGY)
			clusterNodePairMedium.put(cluster, nodeIndices);
		else
			clusterNodePairLarge.put(cluster, nodeIndices);
	}

	/** return whether the cluster is created(not installed yet) by the user */
	private boolean isNewCreatedCluster(String cluster) {
		if (topologyType == MEDIUM_TOPOLOGY)
			return !TextCustomPanelUtils.containsKeyIgnoreCase(
					clusterNodePairMedium, cluster);
		else
			return !TextCustomPanelUtils.containsKeyIgnoreCase(
					clusterNodePairLarge, cluster);
	}

	/**
	 * return cluster with the original case. e.g.User has created cluster
	 * 'ABCCluster', then user inputs 'abccluster', then 'abccluster' is
	 * transformed back to 'ABCCluster'
	 */
	private String getOriginalFormCluster(String cluster) {
		if (topologyType == MEDIUM_TOPOLOGY)
			return TextCustomPanelUtils.getOriginalKey(clusterNodePairMedium,
					cluster);
		else
			return TextCustomPanelUtils.getOriginalKey(clusterNodePairLarge,
					cluster);
	}

	/**
	 * return existing cluster for small deployment with the original case.
	 * e.g.Cluster 'ABCCluster' exists. If user inputs 'abccluster', then
	 * 'abccluster' is transformed back to 'ABCCluster'
	 */
	private String getOriginalFormClusterSmall(String cluster) {
		for (String cur : LCClusterList) {
			if (cur.equalsIgnoreCase(cluster))
				return cur;
		}
		return cluster;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getSelectedNodesForNewCreatedCluster(String cluster) {
		if (topologyType == MEDIUM_TOPOLOGY)
			return (List<Integer>) TextCustomPanelUtils.getValueIgnoreCase(
					clusterNodePairMedium, cluster);
		else
			return (List<Integer>) TextCustomPanelUtils.getValueIgnoreCase(
					clusterNodePairLarge, cluster);
	}

	private List<Integer> getSelectedNodesForExistingCluster(String cluster) {
		List<NodeServerPair> existPairList = getExistNodeServerPairList(cluster);
		List<Integer> selectedNodes = new ArrayList<Integer>();
		for (NodeServerPair curPair : existPairList) {
			String curNode = curPair.getNodeName();
			selectedNodes.add(originalNodeList.indexOf(curNode));
		}
		return selectedNodes;
	}

	private String[] convertArrayToStringList(List<String> array) {
		String[] str = new String[array.size()];
		for (int i = 0; i < array.size(); i++)
			str[i] = array.get(i).toString();
		return str;
	}

	private void saveDataSmall() {
		chooseExistingClusterSmall = false;
		profile = getProfile();
		saveWasAdmin();

		String isClusterExist = "false";
		if (TextCustomPanelUtils.containsIgnoreCase(LCClusterList,
				selectCluster)) {
			isClusterExist = "true";
		}

		selectNodeList.clear();
		for (int i = 0; i < selectNodeIndices.length; i++) {
			if (selectNodeIndices[i] == 1)
				selectNodeList.add(originalNodeList.get(i));
		}

		Collections.sort(selectNodeList);

		StringBuffer secondaryNodeNames = new StringBuffer();
		if (selectNodeList.size() > 1) {
			for (int i = 1; i < selectNodeList.size(); i++) {
				secondaryNodeNames.append(selectNodeList.get(i) + ",");
			}
		}

		// generate cluster info to store in config.properties

		log.debug("cluster name in small deplyment : " + selectCluster);
		log.debug("clusterExist in small deplyment : " + isClusterExist);
		log.debug("firstNodeName in small deplyment : " + selectNodeList.get(0));
		log.debug("secondaryNodesNames in small deplyment : "
				+ secondaryNodeNames.toString());

		StringBuffer serverNames = new StringBuffer();
		StringBuffer clusterInfo = new StringBuffer();
		TextCustomPanelUtils.logUserData("user.deployment.type", "small");
		profile.setUserData("user.deployment.type", "small");
		
		String ccmServerName = null;

		for (int feature = ACTIVITIES; feature <= IC360; feature++) {
			if (getIsFeatureSelected(feature)) {
				if (feature == CCM && this.profile.getUserData("user.ccm.existingDeployment").equals("true")){
					profile.setUserData("user.ccm.cluster.key.value", "");
					continue;
				}
				TextCustomPanelUtils.logUserData("user."
						+ getFeatureName(feature) + ".clusterExist",
						isClusterExist);
				TextCustomPanelUtils.logUserData("user."
						+ getFeatureName(feature) + ".clusterName",
						selectCluster);
				TextCustomPanelUtils.logUserData("user."
						+ getFeatureName(feature) + ".firstNodeName",
						selectNodeList.get(0));
				TextCustomPanelUtils.logUserData("user."
						+ getFeatureName(feature) + ".secondaryNodesNames",
						secondaryNodeNames.toString());

				profile.setUserData("user." + getFeatureName(feature)
						+ ".clusterExist", isClusterExist);
				profile.setUserData("user." + getFeatureName(feature)
						+ ".clusterName", selectCluster);
				profile.setUserData("user." + getFeatureName(feature)
						+ ".firstNodeName", selectNodeList.get(0));
				profile.setUserData("user." + getFeatureName(feature)
						+ ".secondaryNodesNames", secondaryNodeNames.toString());
				// int serverMemberIndex = 0;
				log.info("selectNodeList size : " + selectNodeList.size());
				if (selectNodeList.size() > 0) {
					clusterInfo.append("[");
					for (int i = 0; i < selectNodeList.size(); i++) {
						inner: for (int j = 0; j < originalNodeList.size(); j++) {
							String node = (String) selectNodeList.get(i);
							if (originalNodeList.get(j).trim().equals(node)) {
								serverNames.append(getFeatureName(feature) +"."+ node +".ServerName="+ selectServerMemberList[j] +";");
								clusterInfo.append("{\"node\": \""+ node +"\", \"name\": \""+ selectServerMemberList[j] +"\"},");
								log.info("serverNames info: " + serverNames);
								log.info("cluster info: " + clusterInfo + " in node: " + node);
								if (feature == CCM){
									log.info("ccm server name : " + selectServerMemberList[j]);
									ccmServerName = selectServerMemberList[j];
								}
								break inner;
							}
						}
					}
					clusterInfo.append("]");
					log.info("final cluster info: " + clusterInfo);
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(feature) +".serverInfo", serverNames.toString());
					profile.setUserData("user."+ getFeatureName(feature) +".serverInfo", serverNames.toString());
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(feature) +".clusterInfo", clusterInfo.toString());
					profile.setUserData("user."+ getFeatureName(feature) +".clusterInfo", clusterInfo.toString());
					if (ccmServerName != null){
					    profile.setUserData("user."+ getFeatureName(feature) +".serverName", ccmServerName);
					}
					if (feature == CCM){
						profile.setUserData("user.ccm.cluster.key.value", "\""+selectCluster+"\" : " + clusterInfo.toString() + ",");
					}
				}
				serverNames.delete(0, serverNames.length());
				clusterInfo.delete(0, clusterInfo.length());
			}
		}
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] { Messages.NEXT_INDEX,
						Messages.BACK_TO_TOP_INDEX,
						Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			chooseDeplymentType();
			return;
		} else if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			chooseDeplymentType();
		}
	}

	public void saveDataMediumLarge() {
		profile = getProfile();

		if (profile != null && topologyType == MEDIUM_TOPOLOGY ? isLoadMedium
				: isLoadLarge) {
			log.debug("saveDataMediumLarge topologyType " + topologyType);

			log.info("saveDataMediumLarge");

			saveWasAdmin();

			TextCustomPanelUtils.logUserData("user.deployment.type",
					getTopologyTypeName(topologyType));
			profile.setUserData("user.deployment.type",
					getTopologyTypeName(topologyType));

			log.info("user.deployment.type : "
					+ getTopologyTypeName(topologyType));

			String firstNodeName = "", isClusterExist = "false";
			StringBuffer secondaryNodeNames = null, serverNames = null, clusterInfo = null, firstCCMServerName = null, secondCCMServerName = null;

			for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
				secondaryNodeNames = new StringBuffer();
				firstCCMServerName = new StringBuffer();
				secondCCMServerName = new StringBuffer();
				serverNames = new StringBuffer();
				clusterInfo = new StringBuffer();
				if (getIsFeatureSelected(featureID)) {
					if (featureID == CCM && this.profile.getUserData("user.ccm.existingDeployment").equals("true")){
						profile.setUserData("user.ccm.cluster.key.value", "");
						continue;
					}
					List<NodeServerPair> pairList = getFeatureNodeServerPairList(featureID);
					int num = 0;
					if (pairList == null || pairList.size() == 0) {
						TextCustomPanelUtils
								.showError("No node server info for feature:"
										+ featureID);
						log.error("No node server info for feature:"
								+ featureID);
						continue;
					}
					// find first node
					while (pairList.get(num).isSelected() == false) {
						num++;
					}
					String clusterName = pairList.get(num).getClusterName();
					if (TextCustomPanelUtils.containsIgnoreCase(LCClusterList,
							clusterName))
						isClusterExist = "true";

					firstNodeName = pairList.get(num).getNodeName();

					if (pairList.size() > 1) {
						for (int i = num + 1; i < pairList.size(); i++) {
							if (pairList.get(i).isSelected() == true)
								secondaryNodeNames.append(pairList.get(i)
										.getNodeName() + ",");
						}
					}

					clusterInfo.append("[");
					if (pairList.size() == 1){
						profile.setUserData("user."+ getFeatureName(featureID) + ".secondaryNodeNames.ServerName", "");
					}
					for (int i = 0; i < pairList.size(); i++) {
						NodeServerPair pair = pairList.get(i);
						if (pair.isSelected() == true) {
							serverNames.append(getFeatureName(featureID) +"."+ pair.getNodeName() +".ServerName="+ pair.getServerMemberName() +";");
							log.debug("pair.isSelected() = "+ pair.isSelected());
							log.debug("test node = "+ pair.getNodeName() +"   "+ pair.getServerMemberName());
							clusterInfo.append("{\"node\": \""+ pair.getNodeName() +"\", \"name\": \""+ pair.getServerMemberName() +"\"},");
							if (featureID == CCM){
								//log.info("ccm server name : " + selectServerMemberList[j]);
								//ccmServerName = selectServerMemberList[j];
								
								if (i == 0){
									log.info("first ccm server name : " + pair.getServerMemberName());
									firstCCMServerName.append(pair.getServerMemberName());
									profile.setUserData("user."+ getFeatureName(featureID) +".serverName", firstCCMServerName.toString());
								}
								
								if (i == 1){
									log.info("second ccm server name : " + pair.getServerMemberName());
									secondCCMServerName.append(pair.getServerMemberName());
									//profile.setUserData("user."+ getFeatureName(featureID) +".secondServerName", secondCCMServerName.toString());
									profile.setUserData("user."+ getFeatureName(featureID) + ".secondaryNodeNames.ServerName", "user." + getFeatureName(featureID) + "." + secondaryNodeNames + ".ServerName=" + secondCCMServerName.toString());
								}
							}
						}
					}
					clusterInfo.append("]");
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(featureID) +".clusterExist", isClusterExist);
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(featureID) +".clusterName", clusterName);
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(featureID) +".firstNodeName", firstNodeName);
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(featureID) +".secondaryNodesNames", secondaryNodeNames.toString());
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(featureID) +".serverInfo", serverNames.toString());
					TextCustomPanelUtils.logUserData("user."+ getFeatureName(featureID) +".clusterInfo", clusterInfo.toString());

					profile.setUserData("user."+ getFeatureName(featureID) +".clusterExist", isClusterExist);
					profile.setUserData("user."+ getFeatureName(featureID) +".clusterName", clusterName);
					profile.setUserData("user."+ getFeatureName(featureID) +".firstNodeName", firstNodeName);
					profile.setUserData("user."+ getFeatureName(featureID) +".secondaryNodesNames", secondaryNodeNames.toString());
					profile.setUserData("user."+ getFeatureName(featureID) +".serverInfo", serverNames.toString());
					profile.setUserData("user."+ getFeatureName(featureID) +".clusterInfo", clusterInfo.toString());

					if (featureID == CCM){
						profile.setUserData("user.ccm.cluster.key.value", "\""+clusterName+"\" : " + clusterInfo.toString() + ",");
					}
					
					log.info(getFeatureName(featureID) +": cluster name : "+ clusterName);
					log.info(getFeatureName(featureID) +": clusterExist : "+ isClusterExist);
					log.info(getFeatureName(featureID) +": firstNodeName : "+ firstNodeName);
					log.info(getFeatureName(featureID) +": secondaryNodesNames : "+ secondaryNodeNames.toString());
					log.info(getFeatureName(featureID) +": serverInfo : "+ serverNames.toString());
					log.info(getFeatureName(featureID) +": clusterInfo : "+ clusterInfo.toString());

					serverNames.delete(0, serverNames.length());
					clusterInfo.delete(0, clusterInfo.length());
				}
			}
		}
	}

	public void saveWasAdmin() {
		log.debug("saveWasAdmin");
		String id = profile.getUserData("user.was.adminuser.id");
		String pw = profile.getUserData("user.was.adminuser.password");
		if (id == null) {
			id = profile.getUserData("user.news.adminuser.id");
		}
		if (pw == null) {
			pw = profile.getUserData("user.news.adminuser.password");
		}

		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
				TextCustomPanelUtils.logUserData("user." + getFeatureName(featureID) + ".adminuser.id", id);
				profile.setUserData("user." + getFeatureName(featureID) + ".adminuser.id", id);
				profile.setUserData("user." + getFeatureName(featureID)	+ ".adminuser.password", pw);
		}
	}

	public List<TopologyClusterInfo> generateExistedTreeData() {
		log.debug("generateExistedTreeData");
		List<TopologyClusterInfo> topoInfoList = new ArrayList<TopologyClusterInfo>();

		if (existClusterInfoMap.size() > 0) {
			for (Iterator iter = existClusterInfoMap.keySet().iterator(); iter
					.hasNext();) {
				String clusterName = (String) iter.next();
				log.debug("generateExistedTreeData clusterName is "
						+ clusterName);
				List<NodeServerPair> list = (List<NodeServerPair>) existClusterInfoMap
						.get(clusterName);
				log.debug("generateExistedTreeData list size " + list.size());
				if (list.size() > 0) {
					TopologyClusterInfo t0 = new TopologyClusterInfo();
					t0.setApplicationName(list.get(0).getApplicationName());
					t0.setClusterName(clusterName);
					t0.setNodeSelected(true);
					t0.setIsFirstOne(true);
					t0.setNodeName(list.get(0).getNodeName());
					t0.setServerName(list.get(0).getServerMemberName());

					log.debug("generateExistedTreeData t0 ApplicationName = "
							+ list.get(0).getApplicationName());
					log.debug("generateExistedTreeData t0 ClusterName = "
							+ clusterName);
					log.debug("generateExistedTreeData t0 NodeName = "
							+ list.get(0).getNodeName());
					log.debug("generateExistedTreeData t0 ServerName = "
							+ list.get(0).getServerMemberName());

					List list0 = new ArrayList();
					if (list.size() > 1) {
						for (int i = 1; i < list.size(); i++) {
							NodeServerPair pair = list.get(i);
							TopologyClusterInfo subt = new TopologyClusterInfo();
							subt.setApplicationName(pair.getApplicationName());
							subt.setClusterName(clusterName);
							subt.setNodeSelected(true);
							subt.setIsFirstOne(false);
							subt.setNodeName(pair.getNodeName());
							subt.setServerName(pair.getServerMemberName());
							list0.add(subt);
							log.debug("generateExistedTreeData t" + i
									+ " ApplicationName = "
									+ pair.getApplicationName());
							log.debug("generateExistedTreeData t" + i
									+ " ClusterName = " + clusterName);
							log.debug("generateExistedTreeData t" + i
									+ " NodeName = " + pair.getNodeName());
							log.debug("generateExistedTreeData t" + i
									+ " ServerName = "
									+ pair.getServerMemberName());
						}
					}
					t0.setChildren(list0);
					topoInfoList.add(t0);
				}
			}
		}

		log.debug("existClusterInfoMap size is " + existClusterInfoMap.size());
		return topoInfoList;
	}

	/**
	 * In modify-remove or update-remove, if one feature is to be removed and
	 * the removal of that feature will cause the existing cluster to be removed
	 * together,then remove the cluster from the existing cluster list
	 */
	private void deleteRemovedClusters() {
		List<String> features = getRemovedFeatures();
		if (features == null || features.size() == 0)
			return;
		List<String> clusters = new ArrayList<String>();
		for (String feature : features) {
			List<NodeServerPair> pair = getExistFeatureNodeServerPairList(feature);
			if (pair == null || pair.size() == 0)
				continue;
			String clustername = pair.get(0).getClusterName();
			if (clustername == null)
				continue;
			if (clusters.contains(clustername))
				continue;
			clusters.add(clustername);
		}
		if (clusters.size() == 0)
			return;
		// 0 for delete the cluster from LCClusterList, 1 for keep the cluster
		// in LCClusterList
		List<Integer> clusterFeatureCnt = new ArrayList<Integer>();
		outer: for (int i = 0; i < clusters.size(); i++) {
			String cluster = clusters.get(i);
			List<NodeServerPair> pair = getAllExistNodeServerPairList(cluster);
			for (NodeServerPair cur : pair) {

				if (TextCustomPanelUtils.containsIgnoreCase(features,
						cur.getApplicationName())) {
					continue;
				}
				clusterFeatureCnt.add(1);
				continue outer;
			}
			clusterFeatureCnt.add(0);
		}
		List<String> removeClusters = new ArrayList<String>();
		for (int i = 0; i < clusters.size(); i++) {
			if (clusterFeatureCnt.get(i) == 0)
				removeClusters.add(clusters.get(i));
		}

		for (String cur : removeClusters) {
			if (LCClusterList.contains(cur))
				LCClusterList.remove(cur);
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<NodeServerPair> getExistNodeServerPairList(
			String clusterName) {
		log.debug("getExistNodeServerPairList(String)");
		ArrayList<NodeServerPair> nodeList = new ArrayList<NodeServerPair>();
		List<NodeServerPair> nodeinfoList = (ArrayList<NodeServerPair>) TextCustomPanelUtils
				.getValueIgnoreCase(existClusterInfoMap, clusterName);
		log.debug("getExistNodeServerPairList existClusterInfoMap size = "
				+ existClusterInfoMap.size());
		nodeList.addAll(nodeinfoList);
		return nodeList;
	}

	private List<NodeServerPair> getAllExistNodeServerPairList() {
		List<NodeServerPair> results = new ArrayList<NodeServerPair>();
		for (int i = 0; i < LCClusterList.size(); i++) {
			String curCluster = LCClusterList.get(i);
			List<String> features = findFeatures(curCluster);
			List<NodeServerPair> list = getExistNodeServerPairList(curCluster);
			for (int j = 0; j < list.size(); j++) {
				for (int k = 0; k < features.size(); k++) {
					NodeServerPair cur = list.get(j).clone();
					cur.setApplicationName(features.get(k));
					results.add(cur);
				}
			}
		}
		return results;
	}

	/**
	 * function getExistNodeServerPair(String cluster) does not really get all
	 * the existing NodeServerPairs. Instead, it gets one existing
	 * NodeServerPair for each cluster. This function gets all the existing
	 * feature-cluster-node-server pairs.
	 */
	private List<NodeServerPair> getAllExistNodeServerPairList(String cluster) {
		List<NodeServerPair> results = new ArrayList<NodeServerPair>();
		for (int i = 0; i < LCClusterList.size(); i++) {
			String curCluster = LCClusterList.get(i);
			List<String> features = findFeatures(curCluster);
			List<NodeServerPair> list = getExistNodeServerPairList(curCluster);
			for (int j = 0; j < list.size(); j++) {
				for (int k = 0; k < features.size(); k++) {
					NodeServerPair cur = list.get(j).clone();
					cur.setApplicationName(features.get(k));
					if (cur.getClusterName().equalsIgnoreCase(cluster))
						results.add(cur);
				}
			}
		}
		return results;
	}

	/** get existing NodeServerPairs for the existing feature */
	private List<NodeServerPair> getExistFeatureNodeServerPairList(
			String feature) {
		List<NodeServerPair> all = getAllExistNodeServerPairList();
		List<NodeServerPair> result = new ArrayList<NodeServerPair>();
		for (NodeServerPair cur : all) {
			if (cur.getApplicationName().equalsIgnoreCase(feature))
				result.add(cur);
		}
		return result;
	}

	private Map<String, List<NodeServerPair>> getExistedClusterInfo(
			String clusterfullInfo) {
		log.debug("getExistedClusterInfo");
		Map<String, List<NodeServerPair>> map = new HashMap<String, List<NodeServerPair>>();
		List<NodeServerPair> list = new ArrayList<NodeServerPair>();
		String featureName = null, serverName = null, nodeName = null, clusterName = null;
		boolean isFirstNode = true;
		// cluster30:yanyunNode02#cluster30_server1,yanyunNode03#cluster30_server2
		// ,;test1:yanyunNode02#test1_server1,yanyunNode04#test1_server3,;
		StringTokenizer clusterfullInfoTokenizer = new StringTokenizer(
				clusterfullInfo, ";");
		while (clusterfullInfoTokenizer.hasMoreTokens()) {
			// cluster30:yanyunNode02#cluster30_server1,yanyunNode03#
			// cluster30_server2,
			String clusterinfo = clusterfullInfoTokenizer.nextToken();
			StringTokenizer clusterinfoTokenizer = new StringTokenizer(
					clusterinfo, ":");
			if (clusterinfoTokenizer.hasMoreTokens()) {
				// cluster30
				clusterName = clusterinfoTokenizer.nextToken();
				log.debug("getExistedClusterInfo clusterName : " + clusterName);
				if (clusterinfoTokenizer.hasMoreTokens()) {
					// yanyunNode02#cluster30_server1,yanyunNode03#
					// cluster30_server2,
					String nodeserverFullInfo = clusterinfoTokenizer
							.nextToken();
					StringTokenizer nodeserverFullInfoTokenizer = new StringTokenizer(
							nodeserverFullInfo, ",");
					isFirstNode = true;
					int nodeIndex = 0;
					while (nodeserverFullInfoTokenizer.hasMoreTokens()) {
						// yanyunNode02#cluster30_server1
						String nodeserverInfo = nodeserverFullInfoTokenizer
								.nextToken();
						StringTokenizer nodeserverInfoTokenizer = new StringTokenizer(
								nodeserverInfo, "#");
						if (nodeserverInfoTokenizer.hasMoreTokens()) {
							// yanyunNode02
							nodeName = nodeserverInfoTokenizer.nextToken();
							log.debug("getExistedClusterInfo nodeName : "
									+ nodeName);
							if (nodeserverInfoTokenizer.hasMoreTokens()) {
								// cluster30_server1
								serverName = nodeserverInfoTokenizer
										.nextToken();
								log.debug("getExistedClusterInfo serverName : "
										+ serverName);
							}
							featureName = getFeatureName(findFeature(clusterName));
							log.debug("getExistedClusterInfo featureName : "
									+ featureName);
							log.debug("getExistedClusterInfo isFirstNode : "
									+ isFirstNode);
							list.add(new NodeServerPair(featureName,
									clusterName, nodeName, nodeIndex++,
									serverName, true, isFirstNode));
							isFirstNode = false;
						}
					}
				}
				map.put(clusterName, list);
				list = new ArrayList<NodeServerPair>();
			}
		}

		return map;
	}

	private int getMaxLenClusterName() {
		int maxLen = (Messages.DEPOLOGY_CLUSTER_TABLE_CLUSTER).length();

		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
			if (getIsFeatureSelected(featureID)){
				if (featureID == CCM && profile.getUserData("user.ccm.existingDeployment").equals("true")) {
					continue;
				}
				List<NodeServerPair> pairList = getFeatureNodeServerPairList(featureID);
				if (pairList == null || pairList.size() == 0)
					continue;
				String clusterName = pairList.get(0).getClusterName();
				if (clusterName == null || clusterName.length() == 0)
					continue;
				int curLen = clusterName.length();
				if (curLen > maxLen)
					maxLen = curLen;
			}
		}
		return maxLen;
	}

	private int getMaxLenServerMemberName() {
		int maxLen = (Messages.DEPOLOGY_CLUSTER_TABLE_SERVERS).length();

		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
			if (getIsFeatureSelected(featureID)) {
				if (featureID == CCM && profile.getUserData("user.ccm.existingDeployment").equals("true")) {
					continue;
				}
				List<NodeServerPair> pairList = getFeatureNodeServerPairList(featureID);
				if (pairList == null || pairList.size() == 0)
					continue;
				for (int i = 0; i < pairList.size(); i++) {
					NodeServerPair cur = pairList.get(i);
					int curLen = 0;
					if (cur.isSelected()) {
						String servMemname = cur.getServerMemberName();
						if (servMemname == null || servMemname.length() == 0)
							continue;
						curLen = servMemname.length();
						if (curLen > maxLen)
							maxLen = curLen;
					}
				}
			}
		}
		return maxLen;
	}

	private int findFeature(String clusterName) {
		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
				String tmpClusterName = profile.getUserData("user."
					+ getFeatureName(featureID) + ".clusterName");
				if (tmpClusterName != null && !tmpClusterName.trim().equals("")) {
					if (tmpClusterName.equalsIgnoreCase(clusterName)) {
						return featureID;
					}
				}	
		}
		return -1;
	}

	private List<String> findFeatures(String clusterName) {
		List<String> features = new ArrayList<String>();
		for (int featureID = ACTIVITIES; featureID <= IC360; featureID++) {
				String tmpClusterName = profile.getUserData("user."
					+ getFeatureName(featureID) + ".clusterName");
				if (tmpClusterName != null && !tmpClusterName.trim().equals("")) {
					if (tmpClusterName.equalsIgnoreCase(clusterName)) {
						features.add(getFeatureName(featureID));
					}
				}
		}
		return features;
	}

	private class NodeServerPair implements Cloneable {
		public String applicationName;
		public String clusterName;
		public String nodeName;
		public String serverMemberName;
		public int nodeIndex = 0;
		public boolean isSelected;
		public boolean isFirstNode;

		@Override
		public NodeServerPair clone() {
			try {
				return (NodeServerPair) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}

		public NodeServerPair(String applicationName, String clusterName,
				String nodeName, int nodeIndex, String serverMemberName,
				boolean isSelected, boolean isFirstNode) {
			super();
			this.applicationName = applicationName;
			this.clusterName = clusterName;
			this.nodeName = nodeName;
			this.nodeIndex = nodeIndex;
			this.serverMemberName = serverMemberName;
			this.isSelected = isSelected;
			this.isFirstNode = isFirstNode;
		}

		public NodeServerPair(String nodeName, String serverMemberName) {
			super();
			this.nodeName = nodeName;
			this.serverMemberName = serverMemberName;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getClusterName() {
			return clusterName;
		}

		public void setClusterName(String clusterName) {
			this.clusterName = clusterName;
		}

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public int getNodeIndex() {
			return nodeIndex;
		}

		public void setNodeIndex(int index) {
			this.nodeIndex = index;
		}

		public String getServerMemberName() {
			return serverMemberName;
		}

		public void setServerMemberName(String serverMemberName) {
			this.serverMemberName = serverMemberName;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public boolean isFirstNode() {
			return isFirstNode;
		}

		public void setFirstNode(boolean isFirstNode) {
			this.isFirstNode = isFirstNode;
		}
	}
}
