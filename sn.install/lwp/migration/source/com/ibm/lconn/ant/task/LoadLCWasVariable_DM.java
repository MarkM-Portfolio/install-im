/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.ant.task;

import java.io.File;
import java.util.HashMap;

import com.ibm.lconn.common.feature.LCInfo;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.util.Util;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class LoadLCWasVariable_DM extends BaseTask {
	private String feature;
	private String wasHome;
	private String level = "server"; 

	@Override
	public void execute() {
		log("Starting loading Lotus Connections WAS variables...");
		log("WAS Home = {0}", getWasHome());
		log("Features = {0}", getFeature());
		String lcHome = getProject().getProperty("lc.home");
		if (ObjectUtil.isEmpty(lcHome)) {
			log("ERROR: LC home not set, load WAS variable failed. ");
			return;
		}
		File lcHomeFile = Util.getFile(lcHome);
		log("LC Home = {0}", lcHomeFile.getAbsolutePath());
		String[] features = LCInfo.FEATURE_ALL;
		if (!ObjectUtil.isEmpty(feature)) {
			features = Util.delimStr(getFeature());
		}
		HashMap<String, String> profileNamesMap = new HashMap<String, String>();
		for (int j = 0; j < features.length; j++) {
			String feature = features[j];
			log("Current feature = {0}", feature);

			File featureDir = new File(lcHomeFile, feature);
			if (!featureDir.exists() || !featureDir.isDirectory()) {
				log("Feature folder {0} not installed, skip...", feature);
				continue;
			}
			String profile = getProject().getProperty("ProfileName");
			log("Profile name = {0}", profile);
			String loadLevel = getLevel().toLowerCase();
			String cell = getProject().getProperty("CellName");
			String node = null;
			String server = null;
			log("Cell name = {0}", cell);
			if(!"cell".equals(loadLevel)){
				node = getProject().getProperty("NodeName");
				log("Node name = {0}", node);
				if(!"node".equals(loadLevel)){
					server = getProject().getProperty("ServerName");
					log("Server name = {0}", server);
				}
			}
			if (profileNamesMap.get(getIdentity(profile, node, server)) != null) {
				log("Variables of profile {0} already loaded, skip...", profile);
				continue;
			}
			if (notSet(profile)) {
				log("The profile of feature {0} not set, skip...", profile);
				continue;
			}
			
			LoadWasVariable lwv = new LoadWasVariable();
			lwv.setProject(getProject());
			lwv.setTaskName(getTaskName());
			lwv.setWasHome(getWasHome());
			lwv.setProfileName(profile);
			lwv.setCellName(cell);
			lwv.setNodeName(node);
			lwv.setServerName(server);
			lwv.setPrefix(profile);
			try {
				lwv.execute();
			} catch (Exception e) {
				log(e);
				return;
			}
			log("Variables of profile {0} loaded", profile);
			profileNamesMap.put(getIdentity(profile, node, server), profile);
		}
	}

	private String getIdentity(String profile, String node, String server) {
		return profile+"."+node+"."+server;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getWasHome() {
		return wasHome;
	}

	public void setWasHome(String wasHome) {
		this.wasHome = wasHome;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		if(level==null) return;
		level = level.toLowerCase();
		if("cell".equals(level) || "node".equals(level) || "server".equals(level)){
			this.level = level;
		}
	}
}
