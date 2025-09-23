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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.lconn.wizard.common.Util;

public class DMInfo {
	private String hostName, soapPort, profileName,wasAdmin,wasPassword;
	private String cellName, nodeName;
	private String[] existingClusters;
	private Map<String, List<String>> clusterFeatures = new HashMap<String, List<String>>();
	
	public DMInfo(String hostName, String  soapPort, String  profileName, String wasAdmin, String wasPassword){
		init(hostName, soapPort, profileName,wasAdmin,wasPassword);
	}
	
	private void init(String hostName2, String soapPort2, String profileName2,
			String wasAdmin2, String wasPassword2) {
		setHostName(hostName);
		setSoapPort(soapPort);
		setProfileName(profileName);
		setWasAdmin(wasAdmin);
		setWasPassword(wasPassword);
	}
	
	public int validate(){
		return -1;
	}
	
	public int collect(){
		return -1;
	}
	
	protected boolean hasCluster(String clusterName){
		return Util.indexOf(existingClusters, clusterName) != -1;
	}
	
	protected boolean hasFeatureInstalled(String featureName){
		for (int i = 0; i < existingClusters.length; i++) {
			String cluster = existingClusters[i];
			List<String> featureList = getClusterFeatures(cluster);
			if(-1 != Util.indexOf(featureList, featureName)){
				return true;
			}
		}
		return false;
	}
	
	public String[] getFeatureInstalledClusters(String featureName){
		List<String> clusterList = new ArrayList<String>();
		for (int i = 0; i < existingClusters.length; i++) {
			String cluster = existingClusters[i];
			List<String> featureList = getClusterFeatures(cluster);
			if(-1 != Util.indexOf(featureList, featureName)){
				clusterList.add(cluster);
			}
		}
		return null;
	}
	
	private List<String> getClusterFeatures(String cluster) {
		List<String> list = clusterFeatures.get(cluster);
		if(list==null) return new ArrayList<String>();
		return list;
	}
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getSoapPort() {
		return soapPort;
	}
	public void setSoapPort(String soapPort) {
		this.soapPort = soapPort;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getWasAdmin() {
		return wasAdmin;
	}
	public void setWasAdmin(String wasAdmin) {
		this.wasAdmin = wasAdmin;
	}
	public String getWasPassword() {
		return wasPassword;
	}
	public void setWasPassword(String wasPassword) {
		this.wasPassword = wasPassword;
	}
	public String getCellName() {
		return cellName;
	}
	public String getNodeName() {
		return nodeName;
	}
	public String[] getExistingClusters() {
		return existingClusters;
	}
	
	
}
