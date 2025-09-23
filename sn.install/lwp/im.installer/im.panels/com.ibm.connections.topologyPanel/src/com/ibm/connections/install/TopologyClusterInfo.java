/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.util.ArrayList;
import java.util.List;

public class TopologyClusterInfo {
	private Long id;
	private String applicationName;
	private String clusterName;
	private boolean isNodeSelected;
	private String nodeName;
	private String serverName;
	private List<TopologyClusterInfo> subClusterInfoList = new ArrayList<TopologyClusterInfo>();
	private boolean isFirstOne = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setChildren(List<TopologyClusterInfo> children) {
		this.subClusterInfoList = children;
	}

	public List<TopologyClusterInfo> getChildren() {
		return subClusterInfoList;
	}
	
	public void setIsFirstOne(boolean isFirstOne) {
		this.isFirstOne = isFirstOne;
	}
	
	public boolean getIsFirstOne() {
		return isFirstOne;
	}

	public boolean isNodeSelected() {
		return isNodeSelected;
	}

	public void setNodeSelected(boolean isNodeSelected) {
		this.isNodeSelected = isNodeSelected;
	}
}
