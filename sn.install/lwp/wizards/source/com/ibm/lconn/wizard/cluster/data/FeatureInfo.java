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
package com.ibm.lconn.wizard.cluster.data;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Properties;

import com.ibm.lconn.wizard.common.LCUtil;

class IlleagalLCFeature extends Exception{

	private static final long serialVersionUID = 1L;

	public IlleagalLCFeature(String lcHome, String featureName) {
	}
	
}

public class FeatureInfo {
	private String lcHome = "";
	private String wasHome = "";
	private String profileName = "";
	private String cellName = "";
	private String nodeName = "";
	private String serverName = "";
	private String name;
	private String productHome;
	private String soapPort;
	
	public FeatureInfo(String lcHome, String featureName) throws IlleagalLCFeature{
		this.lcHome = new File(lcHome).getAbsolutePath();
		this.name = featureName;
		this.productHome = MessageFormat.format("{0}/{1}", this.lcHome, this.name);
		String profileName = LCUtil.getProfileName(this.productHome);
		if(profileName==null){
			throw new IlleagalLCFeature(lcHome, featureName);
		}
		this.profileName = profileName;
		String uninstallInfoPropsFile = MessageFormat.format("{0}/{1}/uninstall/profiles/{2}/uninstallInfo.txt", this.lcHome, this.name, this.profileName);
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
	}

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
}
