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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class ResetXXFeature extends BaseTask {
	private String featureName;
	private String properties;
	private String prefix = "xx.";
	private final String DEFAULT_PROPS = "ProfileName,CellName,NodeName,DMgrHostName,DMgrSoapPort,ServerName,ProfilePath";

	@Override
	public void execute() throws BuildException {
		try {
			String featureName = getFeatureName();
			String properties = getProperties();
			String prefix = getPrefix();

			log("Reset current feature to {0}", featureName);
			if (properties == null || "".equals(properties)) {
				properties = DEFAULT_PROPS;
			}
			String[] props = properties.split(",");
			Project p = getProject();
			boolean managedNode = "true".equals(p.getProperty("IsManagedNode"));
			for (int i = 0; i < props.length; i++) {
				String key = featureName + "." + props[i];
				String value = p.getProperty(key);
				if (value == null)
					throw new NullPointerException(key);
				String xxKey = prefix + props[i];
				setProperty(xxKey, value);
				if ("CellName".equals(props[i]) ){
					String dmCell = p.getProperty(featureName+".DMgrCellName");
					if(managedNode && null!=dmCell && !"".equals(dmCell))
						value = dmCell;
					setProperty(prefix+"CellName.auto", value);
				}
				if ("ServerName".equals(props[i])){
					String dmMemberName = p.getProperty(featureName+".ClusterMemberName");
					if(managedNode && null!=dmMemberName && !"".equals(dmMemberName))
						value = dmMemberName;
					setProperty(prefix+"ServerName.auto", value);
				}
			}
		} catch (Exception e) {
			log("error");
			throw new BuildException(e);
		}
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
