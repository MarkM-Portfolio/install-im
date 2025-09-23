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
package com.ibm.lconn.common;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Properties;

import com.ibm.lconn.update.util.LCUtil;

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
	
	private String efix ="efix";
	public FeatureInfo(String lcHome, String featureName) throws IlleagalLCFeature{
		this.lcHome = new File(lcHome).getAbsolutePath();
		this.name = featureName;
		this.productHome = MessageFormat.format("{0}/{1}/{2}", new Object[]{this.lcHome, this.efix, this.name});
		//String profileName = "";//LCUtil.getProfileName2(this.lcHome + File.separator + LCUtil.PRODUCTID_LC_CONFIGENGINE );
		//if(profileName==null){
		//	throw new IlleagalLCFeature(lcHome, featureName);
		//}
		this.profileName = "Dmgr01";
		String uninstallInfoPropsFile = MessageFormat.format("{0}/{1}/properties/wkplc.properties", new Object[]{ this.lcHome, LCUtil.PRODUCTID_LC_CONFIGENGINE});
		Properties featureProps = new Properties();
		try {
			//featureProps.load(new FileInputStream(uninstallInfoPropsFile));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IlleagalLCFeature(lcHome, featureName);
		}
//		if(!this.productHome.equals(featureProps.getProperty("extension.location"))
//				|| !this.name.equals(featureProps.getProperty("extension.name"))) 
//			throw new IlleagalLCFeature(lcHome, featureName);
		this.wasHome = System.getProperty("was.home");//featureProps.getProperty("WasHome");
		this.cellName = featureProps.getProperty("CellName");
		this.nodeName = featureProps.getProperty("NodeName");
//		this.serverName = featureProps.getProperty("WasAdminServer");
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
	
	public String toString(){
		return "LcHome: " + lcHome + " WASHome: " + wasHome + " ProfileName: " + profileName +
		       " CellName: " + cellName + " NodeName: " + nodeName + " ServerName: " + serverName +
		       " SoapPort: " + soapPort;
	}
}
