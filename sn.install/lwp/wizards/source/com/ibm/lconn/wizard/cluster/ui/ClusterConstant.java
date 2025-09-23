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
package com.ibm.lconn.wizard.cluster.ui;

public class ClusterConstant {
	public static final String WIZARD_ID_CLUSTER = "cluster";
	public static final String PAGE_TaskChooserPage = "TaskChooserPage";
	public static final String PAGE_DMInfoPage = "DMInfoPage";
	public static final String PAGE_FeatureSelectionPage = "FeatureSelectionPage";
	public static final String PAGE_FeatureInfoPage = "FeatureInfoPage";
	public static final String PAGE_DMInfoPageUninstall = "DMInfoPageUninstall";
	public static final String PAGE_NodeInfoSubsequent = "NodeInfoSubsequent";
	public static final String PAGE_featureHomepageInfoPage = "PAGE_featureHomepageInfoPage";
	public static final String PAGE_featureActInfoPage = "featureActInfoPage";
	public static final String PAGE_featureBlogsInfoPage = "featureBlogsInfoPage";
	public static final String PAGE_featureCommunitiesInfoPage = "featureCommInfoPage";
	public static final String PAGE_featureDogearInfoPage = "featureDogearInfoPage";
	public static final String PAGE_featureProfilesInfoPage = "featureProfilesInfoPage";
	public static final String PAGE_featureHomePage = "featureHomePage";
	public static final String PAGE_featureIndexInfoPage = "featureIndexInfoPage";
	public static final String PAGE_fullSyncPage = "fullSyncPage";
	public static final String PAGE_memberNamePage = "memberNamePage";
	public static final String PAGE_clusterSummaryPage = "clusterSummaryPage";
	public static final String PAGE_clusterFinishPage = "clusterFinishPage";

	public static final String INPUT_WAS_HOME = "INPUT_wasHome";
	public static final String INPUT_PROFILE_HOME = "INPUT_profileHome";
	public static final String INPUT_LC_HOME = "INPUT_LCHome";
	public static final String INPUT_WIZAR_HOME = "INPUT_wizardHome";
	public static final String INPUT_RUNNING_FEATURE = "INPUT_runingfeature";
	public static final String INPUT_clusterTask = "INPUT_clusterTask";
	public static final String INPUT_clusterFeatureSelection = "INPUT_clusterFeatureSelection";
	public static final String INPUT_clusterFeatureName = "INPUT_clusterFeatureName";
	public static final String INPUT_clusterFeatureClusterName = "INPUT_clusterFeatureClusterName";
	public static final String INPUT_clusterFeaturePort = "INPUT_clusterFeaturePort";
	public static final String INPUT_clusterDMHostName = "INPUT_clusterDMHostName";
	public static final String INPUT_clusterDMSoapPort = "INPUT_clusterDMSoapName";
	public static final String INPUT_clusterDMCellName = "INPUT_clusterDMCellName";
	public static final String INPUT_clusterDMProfileName = "INPUT_clusterDMProfileName";
	public static final String INPUT_clusterDMWasUser = "INPUT_clusterDMWasUser";
	public static final String INPUT_clusterDMWasPassword = "INPUT_clusterDMWasPassword";
	public static final String INPUT_clusterNodeHostName = "INPUT_clusterNodeHostName";
	public static final String INPUT_clusterNodeMememberName = "INPUT_clusterNodeMememberName";
	public static final String INPUT_clusterNodeProfile = "INPUT_clusterNodeProfile";
	public static final String INPUT_clusterNodeWasUser = "INPUT_clusterNodeWasUser";
	public static final String INPUT_clusterNodeWasPassword = "INPUT_clusterNodeWasPassword";
	public static final String INPUT_clusterAddPrimaryNodeChecked = "INPUT_clusterAddPrimaryNodeChecked";
	public static final String INPUT_clusterActivitiesContentStoreLocation = "INPUT_clusterActivitiesContentStoreLocation";
	public static final String INPUT_clusterBlogsIndexLocation = "INPUT_clusterBlogsIndexLocation";
	public static final String INPUT_clusterBlogsContentStoreLocation = "INPUT_clusterBlogsContentStoreLocation";
	public static final String INPUT_clusterCommunitiesForum = "INPUT_clusterCommunitiesForum";
	public static final String INPUT_clusterHomepageIndexLocation = "INPUT_clusterHomepageIndexLocation";
	public static final String INPUT_clusterSummaryContent = "INPUT_clusterSummary";
	public static final String INPUT_clusterCommandVariables = "INPUT_clusterCommandVariables";
	public static final String INPUT_clusterDoFullSync = "INPUT_clusterDoFullSync";
	public static final String INPUT_disLastNodePage = "INPUT_disLastNodePage";
	public static final String INPUT_scope = "INPUT_scope";
	public static final String INPUT_MEMBER_NODE_NAME = "INPUT_MEMBER_NODE_NAME";
	public static final String INPUT_MEMBER_ORIGINAL_SERVER_NAME = "INPUT_MEMBER_ORIGINAL_SERVER_NAME";

	public static final String OPTION_featureAct = "featureAct";
	public static final String OPTION_featureBlogs = "featureBlogs";
	public static final String OPTION_featureComm = "featureComm";
	public static final String OPTION_featureDogear = "featureDogear";
	public static final String OPTION_featureProf = "featureProf";
	public static final String OPTION_featureHomepage = "featureHomepage";
	public static final String OPTION_clusterTaskCreate = "clusterTaskCreate";
	public static final String OPTION_clusterTaskRemove = "clusterTaskRemove";
	public static final String OPTION_clusterTaskAdd = "clusterTaskAdd";
	public static final String OPTION_clusterSyncYes = "OPTION_clusterSyncYes";
	public static final String OPTION_clusterSyncNo = "OPTION_clusterSyncNo";
	public static final String OPTION_LastNodeTrue = "OPTION_LastNodeTrue";
	public static final String OPTION_LastNodeFalse = "OPTION_LastNodeFalse";

	public static final String TASK_ADD_FIRST_NODE = "task.add.first.node";
	public static final String TASK_ADD_SUBSEQUENT_NODE = "task.add.subsequent.node";
	public static final String TASK_DELETE_NODE = "task.delete.node";
	public static final String TASK_VALIDATE_DM = "task.validate.dm";
	public static final String TASK_VALIDATE_NODE = "task.validate.node";
	public static final String TASK_LIST_PROFILES = "task.list.profiles";
	public static final String TASK_LIST_REMOTECLUSTER = "task.list.remoteClusters";
	public static final String TASK_DETECT_MEMBERNAME = "task.detect.memebername";
	public static final String TASK_DETECT_DMCELLNAME = "task.detect.dmcellname";
	public static final String TASK_COPY_PY = "task.copy.py";
	public static final String TASK_FULL_SYNC_NODES = "task.fullSync.nodes";
	public static final String TASK_RESTART_CLUSTER = "task.restart.cluster";
	public static final String TASK_STOP_CLUSTER = "task.stopCluster";
	public static final String TASK_GET_WS_VARIABLE = "task.getWsVaraible";
	public static final String TASK_GET_WS_VARAIBLE_FROM_DM = "task.getWsVaraibleFromDM";
	public static final String TASK_SET_WS_VARIABLE = "task.setWsVaraible";
	public static final String TASK_SET_WS_VARIABLE_TO_DM = "task.setWsVaraibleToDM";
	public static final String TASK_REMOVE_OLD_SERVER = "task.deleteOldServer";

	
	public static final String LC_INFO = "LC_INFO";
	
	// websphere variables
/*	public static final String ACTIVITIES_STATS_DIR = "ACTIVITIES_STATS_DIR";
	public static final String ACTIVITIES_CONTENT_DIR = "ACTIVITIES_CONTENT_DIR";
	public static final String BLOGS_INDEX_DIR = "BLOGS_INDEX_DIR";
	public static final String BLOGS_CONTENT_DIR = "BLOGS_CONTENT_DIR";
	public static final String COMMUNITIES_INDEX_DIR = "COMMUNITIES_INDEX_DIR";
	public static final String COMMUNITIES_STATS_DIR = "COMMUNITIES_STATS_DIR";
	public static final String FORUM_STATS_DIR = "FORUM_STATS_DIR";
	public static final String FORUM_CONTENT_DIR = "FORUM_CONTENT_DIR";
	public static final String DOGEAR_INDEX_DIR = "DOGEAR_INDEX_DIR";
	public static final String DOGEAR_FAVICON_DIR = "DOGEAR_FAVICON_DIR";
	public static final String PROFILES_INDEX_DIR = "PROFILES_INDEX_DIR";
	public static final String PROFILES_STATS_DIR = "PROFILES_STATS_DIR";*/

	// WebSphere variable scope
	public static final int SCOPE_CELL = 1;
	public static final int SCOPE_NODE = 2;
	public static final int SCOPE_SERVER = 3;
}
