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
package com.ibm.lconn.wizard.dbconfig.backend;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class PropertyRuleLoader implements RuleLoader {
	private static final Logger logger = LogUtil.getLogger(PropertyRuleLoader.class);
	private Properties ruleProp = null;
	private List<VersionCheckRules> rules = new ArrayList<VersionCheckRules>();
	private String dbType = null;
	
	public PropertyRuleLoader(String dbType) {
		this.dbType = dbType;
	}
	
	private List<CheckRule> loadFeatureRule(String feature, String version) {
		String versions = ruleProp.getProperty(feature + "." + dbType + "." + version + "." + RULES);
		if(versions == null) {
			versions = ruleProp.getProperty(feature + "." + version + "." + RULES);
		}
		List<CheckRule> ruleList = new ArrayList<CheckRule>();
		
		if( versions != null) {
			String[] rules = versions.split(",");
		
			for(int i = 0; i< rules.length; i++) {
				ruleList.add(loadRule(feature, rules[i].trim()));
			}
		}
		return ruleList;
	}
	
	private CheckRule loadRule(String feature, String ruleNo) {
		String ruleType = ruleProp.getProperty(feature + "." + ruleNo + "." + RULE_TYPE);
		String ruleParam = ruleProp.getProperty(feature + "." + ruleNo + "." + RULE_PARAM);
		String[] params = ruleParam.split(",");
		
		CheckRule cr = null;
		Class<?> clazz = null;
		Constructor<?> constr = null;
		
		try {
			clazz = Class.forName(CheckRule.class.getName() + "$"+ ruleType);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "dbconfig.severe.cannot_load_rule_class", e);
		}
		
		if(clazz == null) {
			return null;
		}

		// find a match constructor
		Class<?>[] paramClasses = new Class[params.length];
		Object[] paramObjects = new Object[params.length];
		for(int i =0 ; i < params.length; i++) {
			String param = params[i].trim();
			if(param.startsWith(RULE_REF)) {
				paramClasses[i] = CheckRule.class;
				paramObjects[i] = loadRule(feature, param);
			} else {
				paramClasses[i] = String.class;
				paramObjects[i] = param;
			}
		}
		
		try {
			constr = clazz.getConstructor(paramClasses);
		} catch (SecurityException e) {
			logger.log(Level.SEVERE, "dbconfig.severe.cannot_find_constructor", e);
		} catch (NoSuchMethodException e) {
			logger.log(Level.SEVERE, "dbconfig.severe.cannot_find_constructor", e);
		}
	
		if(constr == null) {
			return null;
		}
		
		try {
			cr = (CheckRule)constr.newInstance(paramObjects);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "dbconfig.severe.cannot_init_checkrule", e);
		}
		
		return cr;
	}
	
	public List<VersionCheckRules> getCheckRules() {
		return rules;
	}

	public void load(String ruleFile, String feature) {
		ruleProp = new Properties();
		try {
			ruleProp.load(new FileInputStream(ruleFile));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "dbconfig.severe.cannot_load_rules", e);
		}
		
		// first try db specific rules, if not found, use generic rules
		String ruleString = ruleProp.getProperty(feature + "." + dbType + "." + VERSIONS);
		if(ruleString == null) {
			ruleString = ruleProp.getProperty(feature + "." + VERSIONS);
		}
		String[] versions = ruleString.split(",");
		
		for (int i = 0; i< versions.length; i++) {
			VersionCheckRules vcr = new VersionCheckRules();
			vcr.setRules(loadFeatureRule(feature, versions[i]));
			vcr.setVersion(versions[i]);
			vcr.setMatched(false);
			rules.add(vcr);
		}
	}

}
