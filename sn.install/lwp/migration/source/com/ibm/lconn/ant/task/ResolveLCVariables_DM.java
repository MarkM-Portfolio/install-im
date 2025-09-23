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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.ibm.lconn.common.feature.LCInfo;
import com.ibm.lconn.common.util.Util;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class ResolveLCVariables_DM extends BaseTask {
	private List<Element> eleList;
	public Element createElement(){
		Element e = new Element();
		getElementList().add(e);
		return e;
	}
	private List<Element> getElementList() {
		if(eleList==null) eleList = new ArrayList<Element>();
		return eleList;
	}
	public class Element{
		private String feature,variable;

		public String getFeature() {
			return feature;
		}

		public void setFeature(String feature) {
			this.feature = feature;
		}

		public String getVariable() {
			return variable;
		}

		public void setVariable(String variable) {
			this.variable = variable;
		}
	}
	@Override
	public void execute() throws BuildException {
		Iterator<Element> iter = getElementList().iterator();
		while (iter.hasNext()) {
			ResolveLCVariables_DM.Element entry = (ResolveLCVariables_DM.Element) iter
					.next();
			String feature = entry.getFeature();
			boolean isAll = "ALL".equals(feature);
			String variable = entry.getVariable();
			String[] variables = Util.delimStr(variable);
			for (int i = 0; i < variables.length; i++) {
				String v = variables[i];
				try{
				if(isAll){
					String[] features = getFeatures();
					for (int j = 0; j < features.length; j++) {
						String property = readProperty(features[j], v);
						setProperty(features[j]+"."+v , property );
					}
				}else{
					setProperty(v, readProperty(feature, v));
				}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String[] getFeatures() {
		return LCInfo.FEATURE_ALL;
	}
	
	private String readProperty(String feature, String variable) {
		Project p = getProject();
		String featureProfile = p.getProperty("ProfileName");
		String key = getPrefix() + featureProfile + "." + variable;
		String property = p.getProperty(key);
		return property;
	}
}
