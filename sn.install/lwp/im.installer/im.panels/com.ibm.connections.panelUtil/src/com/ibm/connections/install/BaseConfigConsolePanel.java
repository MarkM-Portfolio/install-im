/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technology. 2012, 2020                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.cic.agent.core.api.IAgent;
import com.ibm.cic.agent.core.api.IAgentJob;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.agent.core.api.TextCustomPanel;
import com.ibm.cic.common.core.model.IFeature;
import com.ibm.cic.common.core.model.IOffering;

public abstract class BaseConfigConsolePanel extends TextCustomPanel {
	protected static Hashtable OFFERING_LIST = new Hashtable();

	protected String OFFERING_ID = "";
	protected String OFFERING_VERSION = "";
	protected boolean nossc = true;

	public static final int ACTIVITIES = 0;
	public static final int BLOGS = 1;
	public static final int DOGEAR = 2;
	public static final int COMMUNITIES = 3;
	public static final int FILES = 4;
	public static final int FORUM = 5;
	public static final int HOMEPAGE = 6;
	public static final int CCM = 7;
	public static final int METRICS = 8;
	public static final int MOBILE = 9;
	public static final int MODERATION = 10;
	public static final int NEWS = 11;
	public static final int PROFILES = 12;
	public static final int SEARCH = 13;
	public static final int WIKIS = 14;
	public static final int RTE = 15;
	public static final int COMMON = 16;
	public static final int WIDGET_CONTAINER = 17;
	public static final int PUSH_NOTIFICATION = 18;
	public static final int IC360 = 19;
	//public static final int QUICK_RESULTS = 20;
	// private final ILogger log = IMLogger
	// .getLogger(com.ibm.connections.install.BaseConfigConsolePanel.class);

	/**
	 * RER profile to save data.
	 */
	private IProfile profile = null;

	protected boolean nextEnabled = false;

	public BaseConfigConsolePanel(String panelId) {
		super(panelId);
	}

	@Override
	public void perform() {

	}

	/**
	 * return whether the 3 core features SEARCH, NEWS and HOMEPAGE are all
	 * selected
	 */
	public boolean isCoreFeatureSelected() {
		if (!getIsFeatureSelected(SEARCH) || !getIsFeatureSelected(NEWS)
				|| !getIsFeatureSelected(HOMEPAGE))
			return false;
		return true;
	}

	/**
	 * return user's selection of features without considering what sort of job
	 * he/she chooses(e.g.Modify/Update/Install)
	 */
	public boolean isCoreFeatureSelectedAll() {
		return isFeatureSelectedAll("search") && isFeatureSelectedAll("news")
				&& isFeatureSelectedAll("homepage");
	}

	/**
	 * Get the profile for this offering. The profile is used to store data.
	 * 
	 * @return The profile for this offering or null if it does not yet exist.
	 */
	protected IProfile getProfile() {
		{
			IAgentJob job = findJobAndOfferingId();

			if (job != null) {
				IProfile tempProfile = job.getAssociatedProfile();
				if (tempProfile.getProfileKind().equals("product")) {
					profile = tempProfile;
				}
			}
		}
		return profile;
	}

	/**
	 * Find the job for the given offering id.
	 * 
	 * @param offeringId
	 *            The offering id.
	 * @return The job for the given offering id or null if no job can be found.
	 */
	public IAgentJob findJobAndOfferingId() {
		IAgentJob offeringJob = null;
		IAgentJob[] jobs = getCustomPanelData().getAllJobs();
		int numJobs = jobs.length;
		for (int i = 0; i < numJobs; i++) {
			IAgentJob job = jobs[i];
			IOffering offering = job.getOffering();

			if (offering != null) {
				OFFERING_ID = offering.getIdentity().getId();
				OFFERING_VERSION = offering.getVersion().toString();

				IProfile tempProfile = job.getAssociatedProfile();
				IProfile profile = null;
				if (tempProfile.getProfileKind().equals("product")) {
					profile = tempProfile;

					IOffering[] installed_offerings = profile.getInstalledOfferings();
					int num2 = installed_offerings.length;
					if (num2 == 0) {
						profile.setUserData("user.connections.offering.installed", Constants.VERSION_NOGA_INSTALLED);
					} else {
						for (int j = 0; j < num2; j++) {
							IOffering off = installed_offerings[j];
							if (off != null && off.getIdentity().getId().contains("connections")) {
								profile.setUserData("user.connections.offering.installed", off.getInformation().getVersion());
							}
						}	
					}
					
					profile.setUserData("user.connections.offering.installing", offering.getInformation().getVersion());
				}
				
				if (OFFERING_VERSION.charAt(4) == '0'
						& OFFERING_VERSION.length() >= 7) {
					OFFERING_VERSION = OFFERING_VERSION.substring(0, 6).concat(
							"0");
					// log.info("OFFERING_VERSION "+OFFERING_VERSION);
				}
				OFFERING_LIST.put(offering.getIdentity().getId(),
						OFFERING_VERSION);
				offeringJob = job;
			}
		}
		return offeringJob;
	}

	/**
	 * Determine whether the specified feature is installed.
	 * 
	 * @param featureId
	 *            The feature id that will be checked for install status.
	 * @return True if the feature with the given feature id is installed, false
	 *         otherwise.
	 */
	protected IFeature[] getInstalledFeatures() {
		IFeature[] features = null;

		IAgentJob job = findJobAndOfferingId();
		if (job.isModify()) {
			IProfile profile = getProfile();
			if (profile != null) {
				IAgent agent = (IAgent) getCustomPanelData().getAgent();
				features = agent.getInstalledFeatures(profile,
						findJobAndOfferingId().getOffering());

			}
		} else if (job.isUpdate()) {
			IAgent agent = (IAgent) getCustomPanelData().getAgent();
			IProfile[] profiles = agent.getAllProfiles();
			int num = profiles.length;

			for (int i = 0; i < num; i++) {
				IProfile profile = profiles[i];
				if (profile != null) {
					// log.info("profiles id " + profile.getProfileId());
					IOffering[] installed_offerings = profile
							.getInstalledOfferings();
					int num2 = installed_offerings.length;
					// log.info("installed_offerings num " + num2);

					for (int j = 0; j < num2; j++) {
						IOffering off = installed_offerings[j];
						if (off != null
								&& off.getIdentity().getId()
										.contains("connections")) {
							features = agent.getInstalledFeatures(profile, off);
						}

					}
				}
			}
		}
		return features;
	}

	protected boolean isFeatureInstalled(String featureId) {
		IFeature[] features = getInstalledFeatures();

		if (features != null && features.length > 0) {
			int numInstalledFeatures = features.length;
			// log.info("installedFeatures size " + numInstalledFeatures);

			for (int i = 0; i < numInstalledFeatures; i++) {
				// log.info("installedFeatures id " +
				// features[i].getIdentity().getId());
				if (featureId.equals(features[i].getIdentity().getId())) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isUpdate() {
		IAgentJob job = findJobAndOfferingId();
		return job.isUpdate();
	}

	protected boolean isRollback() {
		IAgentJob job = findJobAndOfferingId();
		return job.isRollback();
	}

	protected boolean isModify() {
		IAgentJob job = findJobAndOfferingId();
		return job.isModify();
	}

	protected boolean isUninstall() {
		IAgentJob job = findJobAndOfferingId();
		return job.isUninstall();
	}

	protected boolean isInstall() {
		IAgentJob job = findJobAndOfferingId();
		return job.isInstall();
	}

	protected boolean isFeatureSelected(String featureId) {
		IAgentJob job = findJobAndOfferingId();
		if (job.isModify() || job.isUpdate()) {
			return isFeatureNewAdded(featureId);
		} else
			return getOffering().contains(featureId);
	}

	protected boolean isFeatureSelectedAll(String featureId) {
		return getOffering().contains(featureId);
	}

	protected boolean isFeatureNewAdded(String featureId) {
		IAgentJob job = findJobAndOfferingId();
		IFeature[] features = job.getFeaturesArray();

		for (int i = 0; i < features.length; i++) {
			String id = features[i].getIdentity().getId();
			if (featureId.equalsIgnoreCase(id)
					&& !isFeatureInstalled(featureId)) {
				return true;
			}
		}
		return false;
	}

	public boolean isNewFeatureAdded() {
		IAgentJob job = findJobAndOfferingId();
		boolean flag = false;
		if (job.isModify() == false && job.isUpdate() == false)
			return flag;

		if (profile == null)
			getProfile();

		if (profile != null) {
			IFeature[] features = job.getFeaturesArray();
			// log.info("FeaturesArray size = " + features.length);
			for (int i = 0; i < features.length; i++) {
				String featureID = features[i].getIdentity().getId();
				if (isFeatureInstalled(featureID)) {
					// log.info("Feature " + featureID +
					// " is already installed.");
				} else {
					// log.info("Feature " + featureID +
					// " is NOT already installed.");
					flag = true;
				}
			}
		} else {
			// log.info("Could not obtain association profile object. Not showing panel.");
			return false;
		}

		return flag;
	}

	public List<String> getRemovedFeatures() {
		IAgentJob job = findJobAndOfferingId();
		List<String> results = new ArrayList<String>();
		if (job.isInstall())
			return null;
		if (profile == null)
			getProfile();
		if (profile != null) {
			List<String> allfeatures = new ArrayList<String>();
			for (int featureID = ACTIVITIES; featureID <= WIKIS; featureID++)
				allfeatures.add(getFeatureName(featureID));

			IFeature[] features = job.getFeaturesArray();
			List<String> selectfeatures = new ArrayList<String>();
			for (int i = 0; i < features.length; i++) {
				selectfeatures.add(features[i].getIdentity().getId());
			}
			for (String cur : allfeatures) {
				if (isFeatureInstalled(cur) && !selectfeatures.contains(cur)) {
					results.add(cur);
				}
			}
		}
		return results.size() > 0 ? results : null;
	}
	
	public boolean isOnlyModifyAddExistingCCM() {
		if(isOnlyModifyAddFeature(getFeatureName(CCM))) {
			if(profile != null) {
				String existFN = profile.getUserData("user.ccm.existingDeployment");
				if(existFN != null && existFN.equalsIgnoreCase("true")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isOnlyModifyAddExistingCCMAndModeration() {
		String features[] = {getFeatureName(CCM),getFeatureName(MODERATION)};
		String existFN = profile.getUserData("user.ccm.existingDeployment");
		if(existFN != null && existFN.equalsIgnoreCase("true"))
			return isOnlyModifyAddFeature(features);
		return false;
	}
	
	// they don't need db panel
	public boolean isOnlyModifyAddFeature(String [] features) {
		IAgentJob job = findJobAndOfferingId();
		if (job.isModify()) {
			boolean isModifyAdd = isNewFeatureAdded();
			if (isModifyAdd == true) {
				if(profile==null)
					getProfile();
				if(profile != null) {
					int count = 0;
					for(int featureID=ACTIVITIES;featureID <=WIKIS;featureID++)
						if(isFeatureNewAdded(getFeatureName(featureID))) {
							count++;
						}
					
					if(count == features.length) {
						for(int i=0;i<features.length;i++) {
							if(isFeatureNewAdded(features[i]) == false) {
								return false;
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isOnlyModifyAddFeature(String feature){
		IAgentJob job = findJobAndOfferingId();
		if (job.isModify()) {
			boolean isModifyAdd = isNewFeatureAdded();
			if (isModifyAdd == true) {
				if(profile==null)
					getProfile();
				if(profile != null) {
					int count = 0;
					for(int featureID=ACTIVITIES;featureID <=WIKIS;featureID++)
						if(isFeatureNewAdded(getFeatureName(featureID))) {
							count++;
						}
					if(count == 1) {
						return isFeatureNewAdded(feature);
					} else
						return false;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.cic.agent.ui.extensions.CustomPanel#shouldSkip()
	 */
	@Override
	public boolean shouldSkip() {
		IAgentJob job = findJobAndOfferingId();
		if (job.isModify()) {
			if (getFeatureId().equals(Constants.FEATURE_ID_WAS_PANEL))
				return false;
			boolean isModifyAdd = isNewFeatureAdded();
			if (isModifyAdd == true) {
				if (getFeatureId().equals(Constants.FEATURE_ID_COGNOS_PANEL)
						&& !isFeatureNewAdded("metrics"))
					return true;
				if (getFeatureId().equals(Constants.FEATURE_ID_CCM_PANEL)
						&& !isFeatureNewAdded("ccm"))
					return true;
				if (getFeatureId().equals(Constants.FEATURE_ID_CONTENT_STORE_PANEL)
						|| getFeatureId().equals(Constants.FEATURE_ID_NOTIFICATION_PANEL))
					return true;
				if (getFeatureId().equals(Constants.FEATURE_ID_TOPOLOGY_PANEL)) {
					if (isOnlyModifyAddExistingCCM()) {
				  	  return true;
				  }
				}
				if (getFeatureId().equals(Constants.FEATURE_ID_DATABASE_PANEL)) {
					if (isFeatureNewAdded("moderation")) {
					  IFeature[] features = job.getFeaturesArray();
					  int count = 0;
					  for (int i = 0; i < features.length; i++) {
						  String id = features[i].getIdentity().getId();
						  if (isFeatureNewAdded(id)
								  && !id.equals("moderation"))
							  count++;
					  }
					  // only homepage is added which will use same DB as news and
					  // search
					  if (count == 0)
						  return true;
				  }
				  if (isOnlyModifyAddExistingCCMAndModeration() || isOnlyModifyAddExistingCCM()) {
				  	return true;
				  }
				}
				if (getFeatureId().equals(Constants.FEATURE_ID_TOPOLOGY_PANEL)) {
					if (isFeatureNewAdded("icec")) {
					  IFeature[] features = job.getFeaturesArray();
					  int count = 0;
					  for (int i = 0; i < features.length; i++) {
						  String id = features[i].getIdentity().getId();
						  if (isFeatureNewAdded(id) && !id.equals("icec"))
							  count++;
					  }
					  if (count == 0)
						  return true;
				  }
				}
				return false;
			} else
				return true;
		} else if (job.isUpdate()) {
			if (getFeatureId().equals(Constants.FEATURE_ID_WAS_PANEL))
				return false;
			boolean isUpdateAdd = isNewFeatureAdded();
			if (isUpdateAdd == true) {
				if (getFeatureId().equals(Constants.FEATURE_ID_COGNOS_PANEL) && !isFeatureNewAdded("metrics"))
					return true;
				if (getFeatureId().equals(Constants.FEATURE_ID_CCM_PANEL)
						&& !isFeatureNewAdded("ccm"))
					return true;
				if (getFeatureId().equals(Constants.FEATURE_ID_CONTENT_STORE_PANEL)
						|| getFeatureId().equals(Constants.FEATURE_ID_NOTIFICATION_PANEL))
					return true;
				if (getFeatureId().equals(Constants.FEATURE_ID_DATABASE_PANEL)
						&& (isFeatureNewAdded("homepage")
								|| isFeatureNewAdded("mobile") || isFeatureNewAdded("moderation"))) {
					IFeature[] features = job.getFeaturesArray();
					int count = 0;
					for (int i = 0; i < features.length; i++) {
						String id = features[i].getIdentity().getId();
						if (isFeatureNewAdded(id) && !id.equals("mobile")
								&& !id.equals("moderation"))
							count++;
					}
					// only homepage is added which will use same DB as news and
					// search
					if (count == 0)
						return true;
				}
				if (getFeatureId().equals(Constants.FEATURE_ID_TOPOLOGY_PANEL) && isFeatureNewAdded("icec")) {
					IFeature[] features = job.getFeaturesArray();
					int count = 0;
					for (int i = 0; i < features.length; i++) {
						String id = features[i].getIdentity().getId();
						if (isFeatureNewAdded(id) && !id.equals("icec"))
							count++;
					}
					if (count == 0)
						return true;
				}
				return false;
			} else
				return true;
		} else if (job.isInstall()) {
			if (getFeatureId().equals(Constants.FEATURE_ID_COGNOS_PANEL)
					&& !isFeatureNewAdded("metrics"))
				return true;
			if (getFeatureId().equals(Constants.FEATURE_ID_CCM_PANEL)
					&& !isFeatureNewAdded("ccm"))
				return true;
		}
		return false;
	}

	public List<String> getOffering() {
		List<String> list = new ArrayList<String>();
		IAgentJob job = findJobAndOfferingId();
		IFeature[] features = job.getFeaturesArray();
		int numFeatures = features.length;
		for (int i = 0; i < numFeatures; i++) {
			list.add(features[i].getIdentity().getId());
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.cic.agent.ui.extensions.BaseWizard.Panel#isPageComplete()
	 */
	public boolean isPageComplete() {
		return nextEnabled;
	}

	/**
	 * Get the feature id for this install panel.
	 * 
	 * @return The feature id for this install panel.
	 */
	public abstract String getFeatureId();

	public boolean isNossc() {
		return nossc;
	}

	/**
	 * "Escapes" dollar sign "$" with extra "$", ie "$$"
	 *
	 * @return Replaces any "$" occurance with "$$"
	 */
	public String escapeDollarSign(String path) {
		if (path != null) {
			return path.replace("$", "$$");
		} else {
			return path;
		}
	}
	
	public String transferPath(String path) {
		if (path.startsWith("\\\\")) {
			return path.replace("\\", "\\\\");
		} else {
			return path.replace("\\", "/");
		}
	}
	
	public String convertPathToForwardSlash(String path) {
		String convertedPath = null;
		if (path != null) {
			convertedPath = path.replace("\\", "/");
		}
		return convertedPath;
	}

	public String transferToFileSeparator(String path) {
		return path.replace("\\", "${file.separator}");
	}

	public String transferForWinPath(String path) {
		return path.replace("\\", "\\\\");
	}

	public String transferForWinPathTwice(String path) {
		return path.replace("\\", "\\\\\\\\");
	}
	
	public String transferWinPath(String path){
		return path.replace("\\", "\\\\");
	}
	
	public String transferWinConStorePath(String path){
		StringBuffer con_store = new StringBuffer(path);
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			con_store.append("\\\\messageStores");
		}
		else {
			con_store.append("/messageStores");
		}
		return con_store.toString();
	}

	public String getFileExtension() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			return "bat";
		} else if (osName.toLowerCase().startsWith("os/400")) {
			// OS400_Enablement
			return "";
		} else {
			return "sh";
		}
	}

	public String getCellName(String dmProfilePath) {

		if (profile == null)
			getProfile();

		String cellName = profile.getUserData("user.was.dmCellName");
		// log.info("Cell Name : " + cellName);
		return cellName;

	}

	public String getClusterList(String dmProfilePath) {
		String cellName = getCellName(dmProfilePath);
		if (cellName != null) {

			try {
				StringBuffer sbPath = new StringBuffer(
						normalizePath(dmProfilePath));
				sbPath.append(File.separator);
				sbPath.append("config");
				sbPath.append(File.separator);
				sbPath.append("cells");
				sbPath.append(File.separator);
				sbPath.append(cellName);
				sbPath.append(File.separator);
				sbPath.append("clusters");

				File clusterFile = new File(sbPath.toString());

				// log.info("Search file path : " + sbPath.toString());

				if (clusterFile.exists()) {
					ArrayList clusterXmlPathList = searchFile("cluster.xml",
							clusterFile.getAbsolutePath());

					StringBuffer clusterInfo = new StringBuffer();
					if (null != clusterXmlPathList) {
						for (int i = 0; i < clusterXmlPathList.size(); i++) {
							String clusterXmlPath = (String) clusterXmlPathList
									.get(i);
							if (null != clusterXmlPath
									&& !"".equals(clusterXmlPath)) {
								String clusterStr = parseNodeXMLOnlyClusterName(clusterXmlPath);
								clusterInfo.append(clusterStr);
								clusterInfo.append(",");
							}
						}
						return clusterInfo.toString();
					}

				} else {
					// log.info("clusters file can not be found.");
				}

			} catch (Exception e) {
				// e.printStackTrace();
				// log.error(e);
			}
		}

		return null;
	}

	public String getClusterFullInfo(String dmProfilePath) {
		String cellName = getCellName(dmProfilePath);
		if (cellName != null) {

			try {
				StringBuffer sbPath = new StringBuffer(
						normalizePath(dmProfilePath));
				sbPath.append(File.separator);
				sbPath.append("config");
				sbPath.append(File.separator);
				sbPath.append("cells");
				sbPath.append(File.separator);
				sbPath.append(cellName);
				sbPath.append(File.separator);
				sbPath.append("clusters");

				File clusterFile = new File(sbPath.toString());
				// log.info("Search file path : " + sbPath.toString());

				if (clusterFile.exists()) {
					ArrayList clusterXmlPathList = searchFile("cluster.xml",
							clusterFile.getAbsolutePath());

					StringBuffer clusterFullInfo = new StringBuffer();
					if (null != clusterXmlPathList) {
						for (int i = 0; i < clusterXmlPathList.size(); i++) {
							String clusterXmlPath = (String) clusterXmlPathList
									.get(i);
							if (null != clusterXmlPath
									&& !"".equals(clusterXmlPath)) {
								String clusterStr = parseNodeXML(clusterXmlPath);
								clusterFullInfo.append(clusterStr);
								clusterFullInfo.append(";");
							}
						}
						return clusterFullInfo.toString();
					}

				} else {
					// log.info("clusters file can not be found.");
				}

			} catch (Exception e) {
				// e.printStackTrace();
				// log.error(e);
			}
		}

		return null;
	}

	public String parseNodeXML(String nodeXmlPath) {

		List clusters = new ArrayList();
		StringBuffer clusterInfo = new StringBuffer();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(nodeXmlPath));
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc
					.getElementsByTagName("topology.cluster:ServerCluster");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String nodeName = attrs.getNamedItem("name").getNodeValue();
				clusters.add(nodeName);

			}// end of if clause

			clusterInfo.append(clusters.get(0));
			clusterInfo.append(":");
			NodeList memberEntrys = doc.getElementsByTagName("members");

			for (int i = 0; i < memberEntrys.getLength(); i++) {
				Node entryNode = memberEntrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String memberName = attrs.getNamedItem("memberName")
						.getNodeValue();
				String nodeName = attrs.getNamedItem("nodeName").getNodeValue();
				clusterInfo.append(nodeName);
				clusterInfo.append("#");
				clusterInfo.append(memberName);
				clusterInfo.append(",");
			}// end of if clause

		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		return clusterInfo.toString();
	}

	public String parseNodeXMLOnlyClusterName(String nodeXmlPath) {

		StringBuffer clusterInfo = new StringBuffer();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(nodeXmlPath));
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc
					.getElementsByTagName("topology.cluster:ServerCluster");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String nodeName = attrs.getNamedItem("name").getNodeValue();
				clusterInfo.append(nodeName);
			}// end of if clause
		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		return clusterInfo.toString();
	}

	public String parseNodeInfoXML(String nodeXmlPath, String filterNodeName) {

		StringBuffer nodeInfo = new StringBuffer();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(nodeXmlPath));
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("members");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String nodeName = attrs.getNamedItem("nodeName").getNodeValue();
				if (!nodeName.equalsIgnoreCase(filterNodeName)) {
					nodeInfo.append(nodeName);
					nodeInfo.append(",");
				}
			}// end of if clause
		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		return nodeInfo.toString();
	}

	public String parseNodeXMLSecurity(String nodeXmlPath, String securityType) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(nodeXmlPath));
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("security:Security");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				if (securityType.equals("APP")) {
					String nodeName = attrs.getNamedItem("appEnabled")
							.getNodeValue();
					return nodeName;
				} else {
					String nodeName = attrs.getNamedItem("enabled")
							.getNodeValue();
					return nodeName;
				}
			}// end of if clause
		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		return null;
	}

	private String normalizePath(String wasLoc) {
		if (null == wasLoc)
			return null;

		wasLoc = wasLoc.trim();

		if (wasLoc.lastIndexOf(File.separator) == wasLoc.length() - 1)
			return wasLoc.substring(0, wasLoc.length() - 1);
		else
			return wasLoc;
	}

	public ArrayList searchFile(String fileName, String findDir) {

		ArrayList nodeXmlPathList = new ArrayList();

		if (null == fileName || "".equals(fileName)) {
			return null;
		}
		String path = "";
		File fFindDir = new File(findDir);
		File[] fileDirs = fFindDir.listFiles();

		for (int i = 0; i < fileDirs.length; i++) {
			File file = fileDirs[i];
			if (!file.isFile()) {
				ArrayList filePath = searchFile(fileName, file.getPath());
				if (null != filePath) {
					nodeXmlPathList.addAll(filePath);
				}
			} else {
				String curName = file.getName();
				if (fileName.equals(curName)) {
					path = file.getAbsolutePath();
					nodeXmlPathList.add(path);
				}
			}

		}
		return nodeXmlPathList;
	}

	public String getAppSecurity(String dmProfilePath, String securityType) {

		String cellName = getCellName(dmProfilePath);
		if (cellName != null) {

			try {
				StringBuffer sbPath = new StringBuffer(
						normalizePath(dmProfilePath));
				sbPath.append(File.separator);
				sbPath.append("config");
				sbPath.append(File.separator);
				sbPath.append("cells");
				sbPath.append(File.separator);
				sbPath.append(cellName);
				sbPath.append(File.separator);
				sbPath.append("security.xml");

				File clusterFile = new File(sbPath.toString());
				if (clusterFile.exists()) {

					String securityEnable = parseNodeXMLSecurity(
							sbPath.toString(), securityType);
					return securityEnable;

				} else {
					// log.info("security file can not be found.");
				}

			} catch (Exception e) {
				// e.printStackTrace();
				// log.error(e);
			}
		}

		return null;
	}

	public String getFeatureName(int feature) {
		switch (feature) {
		case ACTIVITIES:
			return "activities";
		case BLOGS:
			return "blogs";
		case COMMUNITIES:
			return "communities";
		case DOGEAR:
			return "dogear";
		case HOMEPAGE:
			return "homepage";
		case PROFILES:
			return "profiles";
		case FILES:
			return "files";
		case FORUM:
			return "forums";
		case WIKIS:
			return "wikis";
		case MOBILE:
			return "mobile";
		case MODERATION:
			return "moderation";
		case SEARCH:
			return "search";
		case NEWS:
			return "news";
		case METRICS:
			return "metrics";
		case CCM:
			return "ccm";
		case RTE:
			return "rte";
		case COMMON:
			return "common";
		case WIDGET_CONTAINER:
			return "widgetContainer";
		case PUSH_NOTIFICATION:
			return "pushNotification";
		case IC360:
			return "ic360";
		//case QUICK_RESULTS:
		//	return "quickResults";
		default:
			return "notICFeature";
		}
	}
	
	public String getConsoleFeatureName(int feature){
		switch (feature) {
		case ACTIVITIES:
			return Messages.ACTIVITIES;
		case BLOGS:
			return Messages.BLOGS;
		case COMMUNITIES:
			return Messages.COMMUNITIES;
		case DOGEAR:
			return Messages.DOGEAR;
		case HOMEPAGE:
			return Messages.HOMEPAGE;
		case PROFILES:
			return Messages.PROFILES;
		case FILES:
			return Messages.FILES;
		case FORUM:
			return Messages.FORUMS;
		case WIKIS:
			return Messages.WIKIS;
		case MOBILE:
			return Messages.MOBILE;
		case MODERATION:
			return Messages.MODERATION;
		case SEARCH:
			return Messages.SEARCH;
		case NEWS:
			return Messages.NEWS;
		case METRICS:
			return Messages.METRICS;
		case CCM:
			return Messages.CCM;
		case RTE:
			return Messages.RTE;
		case COMMON:
			return Messages.COMMON;
		case WIDGET_CONTAINER:
			return Messages.WIDGET_CONTAINER;
		case PUSH_NOTIFICATION:
			return Messages.PUSH_NOTIFICATION;
		case IC360:
			return Messages.IC360;
		//case QUICK_RESULTS:
		//	return Messages.QUICK_RESULTS;
			// NEWS
		default:
			return Messages.NOTICFEATURE;
		}
	}

	public String getFeaturesMessageText(int feature) {
		switch (feature) {
		case ACTIVITIES:
			return Messages.ACTIVITIES;
		case BLOGS:
			return Messages.BLOGS;
		case COMMUNITIES:
			return Messages.COMMUNITIES;
		case DOGEAR:
			return Messages.DOGEAR;
		case HOMEPAGE:
			return Messages.HOMEPAGE;
		case PROFILES:
			return Messages.PROFILES;
		case FILES:
			return Messages.FILES;
		case FORUM:
			return Messages.FORUMS;
		case WIKIS:
			return Messages.WIKIS;
		case MOBILE:
			return Messages.MOBILE;
		case MODERATION:
			return Messages.MODERATION;
		case SEARCH:
			return Messages.SEARCH;
		case METRICS:
			return Messages.METRICS;
		case CCM:
			return Messages.CCM;
		case RTE:
			return Messages.RTE;
			// NEWS
		case COMMON:
			return Messages.COMMON;
		case WIDGET_CONTAINER:
			return Messages.WIDGET_CONTAINER;
		case PUSH_NOTIFICATION:
			return Messages.PUSH_NOTIFICATION;
		case IC360:
			return Messages.IC360;
		//case QUICK_RESULTS:
		//	return Messages.QUICK_RESULTS;
			// NEWS
		default:
			return Messages.NEWS;
		}
	}

	public int getFeaturesID(String feature) {
		if (feature.equalsIgnoreCase("activities"))
			return ACTIVITIES;
		else if (feature.equalsIgnoreCase("blogs"))
			return BLOGS;
		else if (feature.equalsIgnoreCase("communities"))
			return COMMUNITIES;
		else if (feature.equalsIgnoreCase("dogear"))
			return DOGEAR;
		else if (feature.equalsIgnoreCase("homepage"))
			return HOMEPAGE;
		else if (feature.equalsIgnoreCase("profiles"))
			return PROFILES;
		else if (feature.equalsIgnoreCase("files"))
			return FILES;
		else if (feature.equalsIgnoreCase("forums"))
			return FORUM;
		else if (feature.equalsIgnoreCase("wikis"))
			return WIKIS;
		else if (feature.equalsIgnoreCase("mobile"))
			return MOBILE;
		else if (feature.equalsIgnoreCase("moderation"))
			return MODERATION;
		else if (feature.equalsIgnoreCase("search"))
			return SEARCH;
		else if (feature.equalsIgnoreCase("metrics"))
			return METRICS;
		else if (feature.equalsIgnoreCase("ccm"))
			return CCM;
		else if (feature.equalsIgnoreCase("rte"))
			return RTE;
		else if (feature.equalsIgnoreCase("common"))
			return COMMON;
		else if (feature.equalsIgnoreCase("widgetContainer"))
			return WIDGET_CONTAINER;
		else if (feature.equalsIgnoreCase("pushNotification"))
			return PUSH_NOTIFICATION;
		else if (feature.equalsIgnoreCase("ic360"))
			return IC360;
		//else if (feature.equalsIgnoreCase("quickResults"))
		//	return QUICK_RESULTS;
		else
			return NEWS;
	}

	public boolean getIsFeatureSelected(int feature) {
		String featureName = getFeatureName(feature);
		if (featureName.equals("notICFeature")) {
			featureName = "news";
		}
		return isFeatureSelected(featureName);
	}

	protected void returnToTop() {
	}

}
