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
package com.ibm.lconn.wizard.common.depcheck;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class ProductInfo {
	private String name, vendor, version, installLoc;
	private List<String> availableInstances = new ArrayList<String>();

	public List<String> getAvailableInstances() {
		return availableInstances;
	}
	
	public void setAvailableInstances(List<String> availableInstances) {
		this.availableInstances = availableInstances;
	}
	
	public String getInstallLoc() {
		return installLoc;
	}

	public void setInstallLoc(String installLoc) {
		this.installLoc = installLoc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Name:").
			append(name).
			append(" Location:").
			append(installLoc).
			append(" Version:").
			append(version);
		return sb.toString();
	}
}
