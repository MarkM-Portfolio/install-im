/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.update.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ibm.lconn.common.FeatureInfo;
import com.ibm.lconn.common.LCInfo;
import com.ibm.lconn.wizard.update.data.InstallerMessages;
import com.ibm.websphere.product.history.xml.customProperty;
import com.ibm.websphere.product.history.xml.efixDriver;
import com.ibm.websphere.product.history.xml.efixPrereq;
import com.ibm.websphere.product.history.xml.platformPrereq;
import com.ibm.websphere.product.history.xml.productPrereq;
import com.ibm.websphere.product.history.xml.ptfDriver;
import com.ibm.websphere.update.ptf.EFixImage;
import com.ibm.websphere.update.ptf.OSUtil;
import com.ibm.websphere.update.silent.UpdateInstallerArgs;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

public class LCUtil {
	private static final String EFIX_PROFILE_INFO_COLLECTOR_TITLE = "EFixProfileInfoCollector.title";
	private static final String EFIX_PROFILE_INFO_COLLECTOR_DESCRIPTION = "EFixProfileInfoCollector.description";
	private static final String EFIX_PROFILE_INFO_COLLECTOR_HIDDEN_FEATURES = "EFixProfileInfoCollector.hiddenFeatures";
	private static final String EFIX_PROFILE_INFO_COLLECTOR_LABEL_0 = "EFixProfileInfoCollector.label.{0}";
	private static final String EFIX_PROFILE_INFO_COLLECTOR_FEATURE_0 = "EFixProfileInfoCollector.feature.{0}";
	private static final String EFIX_PROFILE_INFO_COLLECTOR_DISABLED_FIELDS = "EFixProfileInfoCollector.disabledFields";

	public static final String PRODUCTID_LC_BLOGS = LCInfo.FEATURE_BLOGS;
	public static final String PRODUCTID_LC_FULL_BLOGS = "IBM Connections Blogs";
	public static final String PRODUCTID_LC_COMMUNITIES = LCInfo.FEATURE_COMMUNITIES;
	public static final String PRODUCTID_LC_ACTIVITIES = LCInfo.FEATURE_ACTIVITIES;
	public static final String PRODUCTID_LC_FULL_ACTIVITIES = "IBM connections Activities";
	public static final String PRODUCTID_LC_FULL_COMMUNITIES = "IBM Connections Communities";
	public static final String PRODUCTID_LC_DOGEAR = LCInfo.FEATURE_DOGEAR;
	public static final String PRODUCTID_LC_FULL_DOGEAR = "IBM Connections Dogear";
	public static final String PRODUCTID_LC_PROFILES = LCInfo.FEATURE_PROFILES;
	public static final String PRODUCTID_LC_FULL_RPOFILES = "IBM Connections Profiles";
	public static final String PRODUCTID_LC_HOMEPAGE = LCInfo.FEATURE_HOMEPAGE;
	public static final String PRODUCTID_LC_FULL_HOMEPAGE = "IBM Connections Homepage";
	public static final String PRODUCTID_LC_WIKIS = LCInfo.FEATURE_WIKIS;
	public static final String PRODUCTID_LC_FULL_WIKIS = "IBM Connections Wikis";
	public static final String PRODUCTID_LC_FULL_FILES = "IBM Connections Files";
	public static final String PRODUCTID_LC_FILES = LCInfo.FEATURE_FILES;
	public static final String PRODUCTID_LC_FULL_NEWS = "IBM Connections News";
	public static final String PRODUCTID_LC_NEWS = LCInfo.FEATURE_NEWS;
	public static final String PRODUCTID_LC_SEARCH = LCInfo.FEATURE_SEARCH;
	public static final String PRODUCTID_LC_FULL_SEARCH = "IBM Connections Search";
	public static final String PRODUCTID_LC_MOBILE = LCInfo.FEATURE_MOBILE;
	public static final String PRODUCTID_LC_FULL_MOBILE = "IBM Connections Mobile";
	public static final String PRODUCTID_LC_FORUM = LCInfo.FEATURE_FORUM;
	public static final String PRODUCTID_LC_FULL_FORUM = "IBM Connections Forum";
	public static final String PRODUCTID_LC_MODERATION = LCInfo.FEATURE_MODERATION;
	public static final String PRODUCTID_LC_FULL_MODERATION = "IBM Connections Moderation";
	public static final String PRODUCTID_LC_METRICS = LCInfo.FEATURE_METRICS;
	public static final String PRODUCTID_LC_FULL_METRICS = "IBM Connections Metrics";
	public static final String PRODUCTID_LC_CONFIGENGINE = "ConfigEngine";
	public static final String PRODUCTID_LC_CCM = LCInfo.FEATURE_CCM;
	public static final String PRODUCTID_LC_FULL_CCM = "IBM Connections CCM";

	public static final int ACTION_PTF_INSTALL = 6;
	public static final int ACTION_PTF_UNINSTALL = 1;
	public static final int ACTION_FIX_INSTALL = 2;
	public static final int ACTION_FIX_UNINSTALL = 3;

	private static Properties macroProps = new Properties();
	private static boolean commonMacroInitialized = false;
	private static String installingFeature;
	private static int _actionType;
	private static HashMap efixDriverMap = new HashMap();

	public static Icon ICON_ERROR;
	public static Icon ICON_WARN;
	private static Vector prereqFailEfixes = new Vector();
	private static String customInstallSequence;

	static {
		try {
			ICON_ERROR = new ImageIcon(LCUtil.class
					.getResource("/com/ibm/lconn/update/images/error.gif"));
			ICON_WARN = new ImageIcon(LCUtil.class
					.getResource("/com/ibm/lconn/update/images/warn.gif"));
		} catch (Exception e) {
		}
	}

	// Check IBM Connections product directory, if exist, it means the product
	// is installed
	public static String productExist(String installDir, String product) {
		File productDir = new File(installDir + File.separator + "version"+ File.separator + product + ".product");

		if (productDir.exists())
			return productDir.toString();

		return null;
	}

	/** The array of known product ids. */
	public static final String LC_IDS[] = { PRODUCTID_LC_ACTIVITIES,
			PRODUCTID_LC_BLOGS, PRODUCTID_LC_COMMUNITIES, PRODUCTID_LC_DOGEAR,
			PRODUCTID_LC_PROFILES, PRODUCTID_LC_HOMEPAGE, PRODUCTID_LC_WIKIS, PRODUCTID_LC_FILES, PRODUCTID_LC_SEARCH, PRODUCTID_LC_MOBILE, PRODUCTID_LC_NEWS,PRODUCTID_LC_FORUM,PRODUCTID_LC_MODERATION, PRODUCTID_LC_METRICS, PRODUCTID_LC_CCM};

	public static boolean isConnectionsInstallDir(String installDir) {
		installDir = new File(installDir.trim()).getAbsolutePath();
		LCInfo lcInfo = new LCInfo(installDir);
		return lcInfo.isValidLCHome();
	}

	public static void setLCRelatedInfo() {
		String lc_home = com.ibm.lconn.common.LCUtil.getLCHome();
		WPConfig.setProperty(WPConstants.PROP_WP_HOME, lc_home);
		WPConfig.setProperty(WPConstants.PROP_USER_INSTALL_ROOT, lc_home);
		WPConfig.setProperty(LCUtil.PROP_LC_HOME, lc_home);
	}

	public static String getProfileName(String prodPath) {
		File profileDir = new File(prodPath + File.separator + "uninstall"
				+ File.separator + "profiles");
		File curItem;
		String[] list = profileDir.list();

		// there should be only one sub-directory in profiles, and that is the
		// name
		// of the WAS profile the feature is installed to
		for (int i = 0; i < list.length; i++) {
			curItem = new File(profileDir, list[i]);
			if (curItem.isDirectory()) {
				return curItem.getName();
			}
		}
		return null;
	}
	
	public static String getProfileName2(String prodPath) {
/*		File cfgFile = new File(prodPath + File.separator + "properties" + File.separator + "wkplc.properties");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(cfgFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props.getProperty("ProfileName");*/
		return "Dmgr01";
	}
	
	public static String getDMProfilePath(String prodPath) {
/*		File cfgFile = new File(prodPath + File.separator + "properties" + File.separator + "wkplc.properties");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(cfgFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return System.getProperty("user.install.root"); //props.getProperty("WasUserHome_FullPath");
	}

	public static String getDefaultLCHome() {
		if (true) {
			if (OSUtil.isWindows())
				return "c:\\Program Files\\IBM\\Connections";
			else
				return "/opt/HCL/Connections";
		}
		return InstallerMessages.getString("label.product.directory.prompt");
	}

	public static boolean isLCHome(String installLocation) {
		return isConnectionsInstallDir(installLocation);
	}

	public static void prepareProfileInfoCollector(Vector images) {
		int actionType = LCUtil.getActionType();
		HashMap productNeeded = null;
		switch (actionType) {
		case ACTION_FIX_INSTALL:
		case ACTION_FIX_UNINSTALL:
			productNeeded = collectNeededFeatureForEFix(images);
			break;
		case ACTION_PTF_INSTALL:
		case ACTION_PTF_UNINSTALL:
			productNeeded = collectNeededFeatureForPTF();
			break;

		}

		StringBuffer hidenFields = new StringBuffer();
		StringBuffer disableFields = new StringBuffer();
		HashMap profileMap = new HashMap();

		for (int i = 0; i < LC_IDS.length; i++) {
			String feature = LC_IDS[i];
			boolean needed = productNeeded.containsKey(feature);
			if (!needed) {
				appendWithDot(hidenFields, feature);
				continue;
			}
			String profileName = com.ibm.lconn.common.LCUtil
					.getFeature(feature).getProfileName();
			if (profileMap.containsKey(profileName)) {
				appendWithDot(disableFields, getUserFieldName(feature));
				appendWithDot(disableFields, getPasswordFieldName(feature));
			} else {
				profileMap.put(profileName, profileName);
			}
			String featureLabel = getFeatureLabel(feature);
			System.setProperty(MessageFormat.format(
					LCUtil.EFIX_PROFILE_INFO_COLLECTOR_FEATURE_0,
					new Object[] { feature }), featureLabel);
			System.setProperty(MessageFormat.format(
					LCUtil.EFIX_PROFILE_INFO_COLLECTOR_LABEL_0,
					new Object[] { feature }), profileName);
		}

		System.setProperty(LCUtil.EFIX_PROFILE_INFO_COLLECTOR_DESCRIPTION,
				InstallerMessages.getString("desc.was.info.collector"));
		System.setProperty(LCUtil.EFIX_PROFILE_INFO_COLLECTOR_TITLE,
				"title here");
		System.setProperty(LCUtil.EFIX_PROFILE_INFO_COLLECTOR_HIDDEN_FEATURES,
				hidenFields.toString());
		System.setProperty(LCUtil.EFIX_PROFILE_INFO_COLLECTOR_DISABLED_FIELDS,
				disableFields.toString());

	}

	private static HashMap collectNeededFeatureForPTF() {
		HashMap productNeeded;
		productNeeded = new HashMap();
		for (int i = 0; i < LC_IDS.length; i++) {
			String feature = LC_IDS[i];
			boolean installed = com.ibm.lconn.common.LCUtil
					.featureInstalled(feature);
			if (installed) {
				productNeeded.put(feature, feature);
			}
		}
		return productNeeded;
	}

	private static boolean needOrNot(String[] array,
			String str) {
		for (int j = 0; j < array.length; j++) {
			if(str.equals(array[j])) return true;
		}
		return false;
	}

	private static String getPasswordFieldName(String feature) {
//		return feature + "ProfilePassword";
		return "password";
	}

	private static String getUserFieldName(String feature) {
//		return feature + "ProfileUserID";
		return "userid";
	}

	public static String getFeatureLabel(String feature) {
		return InstallerMessages.getString(feature + ".capitalized");
	}

	public static void appendWithDot(StringBuffer sb, String feature) {
		appendWith(sb, feature, ",");
	}

	public static void appendWith(StringBuffer sb, String feature, String delim) {
		if (sb.length() == 0)
			sb.append(feature);
		else
			sb.append(delim + feature);
	}

	public static HashMap collectNeededFeatureForEFix(Vector images) {
		HashMap productNeeded = new HashMap();
		for (int i = 0; i < images.size(); i++) {
			Object img = images.elementAt(i);
			efixDriver fixDriver;
			if (img instanceof String) {
				fixDriver = (efixDriver) LCUtil.efixDriverMap.get(img);
			} else {
				EFixImage image = (EFixImage) img;
				fixDriver = image.getEFixDriver();
			}
			for (int j = 0; j < fixDriver.getProductPrereqCount(); j++) {
				productPrereq productPrereq = fixDriver.getProductPrereq(j);
				String productId = productPrereq.getProductId();
				productNeeded.put(productId, productId);
			}
		}
		return productNeeded;
	}

	public static void collectProfileInfoCollector() {

	}

	public static String resolveMarcro(String macroName) {
		String val = (String) macroProps.get(macroName);
		if (val != null && !val.equals(""))
			return val;
		return null;
	}

	public static boolean fixpackEnabled() {
		return true;
	}

	public static boolean fixpackGUIEnabled() {
		return true;
	}

	// WAS profiles name where each of the features installed to
	public static final String PROP_ACTIVITIES_PROFILE_NAME = "ActivitiesProfileName";
	public static final String PROP_BLOGS_PROFILE_NAME = "BlogsProfileName";
	public static final String PROP_COMMUNITIES_PROFILE_NAME = "CommunitiesProfileName";
	// Lotus Connections installdir
	public static final String PROP_LC_HOME = "LcHome";
	public static final String PROP_LC_PROFILE_NAME = "LCProfileName";
	
	public static final String PROP_DOGEAR_PROFILE_NAME = "DogearProfileName";
	public static final String PROP_PROFILES_PROFILE_NAME = "ProfilesProfileName";
	public static final String PROP_HOMEPAGE_PROFILE_NAME = "HomepageProfileName";
	public static final String PROP_WIKIS_PROFILE_NAME = "WikisProfileName";
	public static final String PROP_FILES_PROFILE_NAME = "FilesProfileName";
	public static final String PROP_SEARCH_PROFILE_NAME = "SearchProfileName";
	public static final String PROP_MOBILE_PROFILE_NAME = "MobileProfileName";
	public static final String PROP_NEWS_PROFILE_NAME = "NewsName";
	public static final String PROP_FORUM_PROFILE_NAME = "ForumName";
	public static final String PROP_MODERATION_PROFILE_NAME = "ModerationName";
	public static final String PROP_METRICS_PROFILE_NAME = "MetricsName";
	public static final String PROP_ISERIES_PROFILE_NAME = "ProfileName";
	public static final String PROP_CCM_PROFILE_NAME = "CCMName";
	

	public static String getFeatureLabelFull(String feature) {
		return getFeatureLabel(feature);
	}

	public static void setInstallImage(efixDriver nextImage) {
		if(nextImage==null) return;
		String productId = nextImage.getProductPrereq(0)
				.getProductId();
		setInstallingFeature(productId);
	}

	public static void setInstallingFeature(String productId) {
		installingFeature = productId;
		if (!commonMacroInitialized) {
			LCInfo info = com.ibm.lconn.common.LCUtil.getLCInfo();
			for (int i = 0; i < LC_IDS.length; i++) {
				String featureName = LC_IDS[i];
				FeatureInfo feature = info.getFeature(featureName);
				if (feature != null) {
					macroProps.setProperty(featureName + "_profile_name",
							feature.getProfileName());
					/*macroProps.setProperty(featureName + "_node_name", feature
							.getNodeName());
					macroProps.setProperty(featureName + "_server_name",
							feature.getServerName());*/
					macroProps.setProperty(featureName + "_was_home", feature
							.getWasHome());
					// macroProps.setProperty(feature+"_soar_port",
					// feature.getServerSOAP());
				}
			}
		}
		System.out.println("installingFeature=" + installingFeature);
		LCInfo info = com.ibm.lconn.common.LCUtil.getLCInfo();
		FeatureInfo feature = info.getFeature(productId);
		macroProps.setProperty("lc_home", com.ibm.lconn.common.LCUtil
				.getLCHome());
		macroProps.setProperty("was_home", feature.getWasHome());
		String wasUser = macroProps.getProperty(productId + "WasAdminUser");
		String wasPassword = macroProps.getProperty(productId
				+ "WasAdminPassword");
		//System.out.println("wasUser=" + wasUser);
		//System.out.println("wasPassword=" + wasPassword);
		String wasUserID = System.getProperty("was_username");
		String wasPwd = System.getProperty("was_password");
		if(wasUserID!=null){
			macroProps.setProperty("was_username", wasUserID);
		}else{
			macroProps.setProperty("was_username", WPConfig.getProperty(WPConstants.PROP_WAS_USER));
		}
		if(wasPwd!=null){
			macroProps.setProperty("was_password", wasPwd);
		}else{
			macroProps.setProperty("was_password", WPConfig.getProperty(WPConstants.PROP_WAS_PASS));
		}

		String commonWasUser = macroProps.getProperty("was_username");
		String commonWasPassword = macroProps.getProperty("was_password");
		if (!isEmpty(commonWasUser) && isEmpty(wasUser)) {
			macroProps.setProperty(productId + "WasAdminUser", commonWasUser);
		}
		if (!isEmpty(commonWasPassword) && isEmpty(wasPassword)) {
			macroProps.setProperty(productId + "WasAdminPassword",
					commonWasPassword);
		}
		if (wasUser != null && !"".equals(wasUser))
			macroProps.setProperty("was_username", wasUser);
		if (wasPassword != null && !"".equals(wasPassword))
			macroProps.setProperty("was_password", wasPassword);
	}

	private static boolean isEmpty(String str) {
		return null == str || "".equals(str);
	}

	public static void setActionType(int actionType) {
		_actionType = actionType;
		// TODO Auto-generated method stub
	}

	public static int getActionType() {
		return _actionType;
	}

	public static void registEfixDriver(String id, efixDriver nextDriver) {
		efixDriverMap.put(id, nextDriver);
	}

	public static void setInstallImage(String efixId) {
		setInstallImage((efixDriver) efixDriverMap.get(efixId));
	}

	public static String extraInputValidation(String nextArg, String[] args,
			int argNo, UpdateInstallerArgs updateInstallerArgs) {
		boolean valid = false;
		for (int i = 0; i < LC_IDS.length; i++) {
			String feature = LC_IDS[i];
			String key = feature + "WasAdminUser";
			if (("-" + key).equalsIgnoreCase(nextArg)) {
				valid = true;
				macroProps.setProperty(key, args[argNo]);
				updateInstallerArgs.bWasUserId = true;
			}
			key = feature + "WasAdminPassword";
			if (("-" + key).equalsIgnoreCase(nextArg)) {
				valid = true;
				macroProps.setProperty(key, args[argNo]);
				updateInstallerArgs.bWasPassword = true;
			}
		}
		if (!valid)
			return "WUPD0024E";
		return null;
	}

	public static efixDriver getEfixDriver(String efixId) {
		return (efixDriver) efixDriverMap.get(efixId);
	}



	public static Vector getPrereqFailEfixes() {
		if (prereqFailEfixes == null)
			prereqFailEfixes = new Vector();
		return prereqFailEfixes;
	}

	public static Vector getPrereqFailEfixdrivers() {
		Vector vector = new Vector();
		Vector fails = getPrereqFailEfixes();
		int size = fails.size();
		if (size == 0) {
			return vector;
		}
		for (int i = 0; i < size; i++) {
			Object ele = fails.elementAt(i);
			efixDriver driver = null;
			if (ele instanceof String) {
				driver = getEfixDriver((String) ele);
			} else {
				driver = (efixDriver) ele;
			}
			vector.remove(driver);
			vector.add(driver);
		}
		return vector;
	}

	private static String getEfixPrereq(efixDriver eFix) {
		int count = eFix.getEFixPrereqCount();
		StringBuffer resultBuffer = new StringBuffer();

		String negativeEfix = InstallerMessages
				.getString("label.negative.efix");
		String efixSeparator = InstallerMessages
				.getString("label.efix.separator");

		for (int i = 0; i < count; i++) {
			efixPrereq nextPrereq = eFix.getEFixPrereq(i);

			if (i != 0)
				resultBuffer.append(efixSeparator);
			if (nextPrereq.getIsNegativeAsBoolean())
				resultBuffer.append(negativeEfix);

			resultBuffer.append(nextPrereq.getEFixId());
		}

		return resultBuffer.toString();
	}

	private static String getProductPrereqWithVersion(efixDriver eFix) {
		int prereqCount = eFix.getProductPrereqCount();
		StringBuffer resultBuffer = new StringBuffer();

		String efixSeparator = InstallerMessages
				.getString("label.efix.separator");
		String productPreqWithVersion = "{0}({1})";

		for (int prereqNo = 0; prereqNo < prereqCount; prereqNo++) {

			productPrereq nextPrereq = eFix.getProductPrereq(prereqNo);
			if (prereqNo != 0)
				resultBuffer.append(efixSeparator);

			resultBuffer.append(MessageFormat.format(productPreqWithVersion,
					new Object[] { nextPrereq.getProductId(),
							nextPrereq.getBuildVersion() }));
		}

		return resultBuffer.toString();
	}

	private static String getPlatformPrereq(efixDriver eFix) {

		int prereqCount = eFix.getPlatformPrereqCount();

		StringBuffer resultBuffer = new StringBuffer();

		for (int prereqNo = 0; prereqNo < prereqCount; prereqNo++) {
			platformPrereq nextPrereq = eFix.getPlatformPrereq(prereqNo);

			if (prereqNo != 0)
				resultBuffer.append(" ");

			resultBuffer.append(nextPrereq.getOSPlatform());
			resultBuffer.append(nextPrereq.getArchitecture());
			resultBuffer.append(nextPrereq.getOSVersion());
		}

		return resultBuffer.toString();
	}

	public static String constructPrereqDisplay(efixDriver eFix) {
		String productPrereqLabel = InstallerMessages
				.getString("label.product.prerequisite");
		String platformPrereqLabel = InstallerMessages
				.getString("label.platform.prerequisite");
		String efixPrereqLabel = InstallerMessages
				.getString("label.efix.prerequisite");
		String efixPrereq = getEfixPrereq(eFix);
		String productPrereq = getProductPrereqWithVersion(eFix);
		String platformPrereq = getPlatformPrereq(eFix);
		if ("***".equals(efixPrereq))
			efixPrereq = "";
		if ("***".equals(productPrereq))
			productPrereq = "";
		if ("***".equals(platformPrereq))
			platformPrereq = "";
		StringBuffer sb = new StringBuffer();
		if (!"".equals(efixPrereq)) {
			String efixPrereqDetail = MessageFormat.format(efixPrereqLabel,
					new Object[] { efixPrereq });
			sb.append(efixPrereqDetail + "\n");
		}
		if (!"".equals(productPrereq)) {
			String productPrereqDetail = MessageFormat.format(
					productPrereqLabel, new Object[] { productPrereq });
			sb.append(productPrereqDetail + "\n");
		}
		if (!"".equals(platformPrereq)) {
			String platformPrereqDetail = MessageFormat.format(
					platformPrereqLabel, new Object[] { platformPrereq });
			sb.append(platformPrereqDetail + "\n");
		}

		return sb.toString();
	}

	public static String getProfileCollectionSequenceId() {
		String panelId = null;
		int actionType = getActionType();
		switch (actionType) {
		case ACTION_FIX_INSTALL:
			panelId = "ProfileInfoCollector";
			break;
		case ACTION_FIX_UNINSTALL:
			panelId = "ProfileInfoCollector2";
			break;
		case ACTION_PTF_INSTALL:
			panelId = "ptfProfileInfoCollector";
			break;
		case ACTION_PTF_UNINSTALL:
			panelId = "ptfProfileInfoCollectorUninstall";
			break;
		}
		return panelId;
	}

	public static String getProfileCollectionPanelId() {
		String panelId = null;
		int actionType = getActionType();
		switch (actionType) {
		case ACTION_FIX_INSTALL:
			panelId = "EFixAdminUser";//"EFixProfileInfoCollector";
			break;
		case ACTION_FIX_UNINSTALL:
			panelId = "EFixAdminUser2";//"EFixProfileInfoCollector2";
			break;
		case ACTION_PTF_INSTALL:
			panelId = "PTFAdminUser";//"PTFProfileInfoCollector";
			break;
		case ACTION_PTF_UNINSTALL:
			panelId = "PTFAdminUserUninstall";//"PTFProfileInfoCollectorUninstall";
			break;
		}
		return panelId;
	}

	public static void setCustomInstallSequence(ptfDriver usePTFDriver) {
		int customPropertyCount = usePTFDriver.getCustomPropertyCount();
	    for (int i = 0; i < customPropertyCount; i++) {
			customProperty customProperty = usePTFDriver.getCustomProperty(i);
			String name = customProperty.getPropertyName();
			if("customInstallSequence".equals(name)){
				LCUtil.setCustomInstallSequence(customProperty.getPropertyValue());
			}
		}
	}

	private static void setCustomInstallSequence(String propertyValue) {
		customInstallSequence = propertyValue;
	}
	
	public static boolean hasError(String logPath){
		File resultFormatPatterFile = com.ibm.lconn.common.LCUtil.getResultFormatPatterFile();
		if(!resultFormatPatterFile.exists() || !resultFormatPatterFile.isFile()) return true;
		FileInputStream is;
		try {
			is = new FileInputStream(resultFormatPatterFile);
		} catch (FileNotFoundException e) {
			return true;
		}
		CommandResultInfo info = LogAnalysis.analyze(logPath, 1, is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(info.getExecState()==CommandResultInfo.COMMAND_ERROR) return true;
		return false;
	}
	
	public static Vector updateInstallSequence(Vector v){
		if(null == customInstallSequence) return v;
		String[] sequences = customInstallSequence.split(",");
		Vector result = new Vector();
		for (int i = 0; i < sequences.length; i++) {
			boolean removed = v.remove(sequences[i]);
			if(removed){
				result.add(sequences[i]);
			}
		}
		result.addAll(v);
		return result;
	}
}
