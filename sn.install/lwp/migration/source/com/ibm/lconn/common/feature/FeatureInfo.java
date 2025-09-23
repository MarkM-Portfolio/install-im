/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;

import com.ibm.lconn.common.file.FileUtil;
import com.ibm.lconn.common.msg.MessageUtil;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.util.StringResolver;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class FeatureInfo {
	
	private static final String KEY_FOR_FEATURE = "$<FEATURE>";
	private String lcHome = "";
	private String wasHome = "";
	private String profileName = "";
	private String cellName = "";
	private String nodeName = "";
	private String serverName = "";
	private String name = "";
	private String productHome;
	private String soapPort;
	private String version = "";
	private Properties featureProperties = new Properties();
	
	private static final String UNINSTALL_INFO_TXT_TEMPLETE = "{0}/{1}/uninstall/profiles/{2}/uninstallInfo.txt";
	private static final String CELL_CONFIG_LOCATION_TEMPLETE = MessageUtil.getSetting("feature.cell.configuration");
	private static final String NODE_CONFIG_LOCATION_TEMPLETE = MessageUtil.getSetting("feature.node.configuration");
	private static final String CELL_CONFIG_LOCATION_FEDERATED_TEMPLETE = MessageUtil.getSetting("feature.cell.configuration.federated");
	private static final String NODE_CONFIG_LOCATION_FEDERATED_TEMPLETE = MessageUtil.getSetting("feature.node.configuration.federated");
	private static final String FEATURE_HOME_TEMPLETE = MessageUtil.getSetting("feature.home");
	
	private static String[] featureAvailableKeys = MessageUtil.getSetting("feature.available.property.20").split(" ");
	
	public FeatureInfo(String lcHome, String featureName) throws IlleagalLCFeature, DOMException, TransformerException{
		this.lcHome = new File(lcHome).getAbsolutePath();
		this.name = featureName;
		this.productHome = MessageFormat.format("{0}/{1}", new Object[]{this.lcHome, this.name});
//		setVersion();
		String profileName = LCUtil.getProfileName(this.productHome);
		if(profileName==null){
			throw new IlleagalLCFeature(lcHome, featureName);
		}
		this.profileName = profileName;
		String uninstallInfoPropsFile = MessageFormat.format(UNINSTALL_INFO_TXT_TEMPLETE, new Object[]{ this.lcHome, this.name, this.profileName});
		Properties featureProps = new Properties();
		try {
			featureProps.load(new FileInputStream(uninstallInfoPropsFile));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IlleagalLCFeature(lcHome, featureName);
		}
//		if(!this.productHome.equals(featureProps.getProperty("extension.location"))
//				|| !this.name.equals(featureProps.getProperty("extension.name"))) 
//			throw new IlleagalLCFeature(lcHome, featureName);
		this.wasHome = featureProps.getProperty("was.location");
		this.cellName = featureProps.getProperty("wasCellNode.cellName");
		this.nodeName = featureProps.getProperty("wasCellNode.nodeName");
		this.serverName = featureProps.getProperty("server.name");
		
		this.featureProperties = new Properties();
		
		addProperty("{0}.installed", name, "true");
		addProperty("{0}.version", name, getVersion());
		addProperty("{0}.was.home", name, getWasHome());
		addProperty("{0}.cell.name", name, getCellName());
		addProperty("{0}.node.name", name, getNodeName());
		addProperty("{0}.profile.name", name, getProfileName());
		addProperty("{0}.server.name", name, getServerName());
		addProperty("{0}.lc.home", name, getLcHome());
		addProperty("{0}.cell.name.federated", name, getFederatedCellName());
		addProperty("{0}.cell.config.home.federated", name, getCellConfigHomeFederated());
		addProperty("{0}.node.config.home.federated", name, getNodeConfigHomeFederated());
		addProperty("{0}.cell.config.home", name, getCellConfigHome());
		addProperty("{0}.node.config.home", name, getNodeConfigHome());
		addProperty("{0}.home", name, getFeatureHome());
		readProperties();
	}
	
	private void readProperties() {
		File f = new File(getFeatureHome(), this.name + ".properties");
		if(f.exists() && f.isFile()){
			try {
				Properties props = ObjectUtil.loadProperties(f.getAbsolutePath());
				for (int i = 0; i < featureAvailableKeys.length; i++) {
					
					String key = featureAvailableKeys[i];
					if(key.startsWith(KEY_FOR_FEATURE)){
						key = this.name + key.substring(KEY_FOR_FEATURE.length());
					}
					String val = props.getProperty(key);
					if(null!=val){
						addProperty(key, val);
					}
				}
			} catch (IOException e) {
			}
		}
	}

	private String getFeatureHome() {
		return evalValue(FEATURE_HOME_TEMPLETE);
	}

	private String getNodeConfigHomeFederated() {
		return evalValue(NODE_CONFIG_LOCATION_FEDERATED_TEMPLETE);
	}

	private void addProperty(String string, String feature,
			String value) {
		String key = MessageFormat.format(string, feature);
		addProperty(key, value);
	}

	private void addProperty(String key, String value) {
		this.featureProperties.setProperty(key, value);
	}
	
	private String evalValue(String msg) {
		String cellConfigDir = MessageFormat.format(msg, name);
		cellConfigDir = StringResolver.resolveMacro(cellConfigDir, featureProperties);
		return cellConfigDir;
	}
	
	public String getCellConfigHome(){
		return evalValue(CELL_CONFIG_LOCATION_TEMPLETE);
	}
	
	public String getNodeConfigHome(){
		return evalValue(NODE_CONFIG_LOCATION_TEMPLETE);
	}
	
	public String getCellConfigHomeFederated(){
		return evalValue(CELL_CONFIG_LOCATION_FEDERATED_TEMPLETE);
	}
	
//	private void setVersion() throws TransformerException {
//		String productFile = MessageFormat.format("{0}/version/{1}.product", this.lcHome, this.name);
//		Document document = XMLUtil.getDocument(FileUtil.getAbsoluteFile(productFile).getAbsolutePath());
//		Node selectSingleNode = XPathAPI.selectSingleNode(document.getDocumentElement(), "version/text()");
//		this.version = selectSingleNode.getNodeValue();
//	}

	public String getLcHome() {
		return lcHome;
	}

//	public void setLcHome(String lcHome) {
//		this.lcHome = lcHome;
//	}

	public String getWasHome() {
		return wasHome;
	}

	public String getProfileName() {
		return profileName;
	}

	public String getCellName() {
		return cellName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerSOAP(String soapPort) {
		this.soapPort = soapPort;
	}
	
	public String getServerSOAP(){
		return this.soapPort;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return this.version;
	}
	
	public String getFederatedCellName() {
		// TODO Auto-generated method stub
		String old = getCellName();
		String node = getNodeName();
		String wasHome = getWasHome();
		String profile = getProfileName();
		String cellBackupFolder = "{0}/profiles/{1}/config/cells";
		String nodesFolder = "{0}/nodes/{1}/servers";
		cellBackupFolder = MessageFormat.format(cellBackupFolder, wasHome, profile);
		File cellBackupFolderFile = FileUtil.getAbsoluteFile(cellBackupFolder);
		File[] cellFiles = cellBackupFolderFile.listFiles();
		if(cellFiles==null) return old;
		String newCellName = null;
		for (File cellFile : cellFiles) {
			String format = MessageFormat.format(nodesFolder, cellFile.getAbsolutePath(), node);
			File nodeFolderFile = FileUtil.getAbsoluteFile(format);
			if(nodeFolderFile.exists()){
				newCellName = cellFile.getName();
				if(ObjectUtil.equals(newCellName, old)){
					return old;
				}
			}
		}
		if(newCellName!=null){
			return newCellName;
		}
		
		return old;
	}

	public Properties getFeatureProperties() {
		return featureProperties;
	}

	public void setFeatureProperties(Properties featureProperties) {
		this.featureProperties = featureProperties;
	}		
}
