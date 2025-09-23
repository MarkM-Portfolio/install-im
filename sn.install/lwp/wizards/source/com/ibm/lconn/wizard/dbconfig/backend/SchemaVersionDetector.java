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
package com.ibm.lconn.wizard.dbconfig.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.lconn.wizard.common.Constants;

/**
 * This class is used to detect IBM Connections feature schema versions
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class SchemaVersionDetector {
	public static final String RULE_FILE= "DBWizard" + Constants.FS + "chekRules.properties";

//#Oracle12C_PDB_disable#		public static Map<String, String> getFeatureVersions(String dbType, String installLoc, String instance,String PDBNameValue,String dbaPasswordValue, String dbVersion, String[] features) {
	public static Map<String, String> getFeatureVersions(String dbType, String installLoc, String instance, String dbVersion, String[] features) {
		String[] versions = new String[features.length];
		for(int i = 0; i< features.length; i++) {
			//REVISIT: Do we use multi-thread to accelerate the detection?
			String feature = features[i];
//#Oracle12C_PDB_disable# 			RuleEvaluator re = RuleEvaluator.getInstance(dbType, installLoc, instance,PDBNameValue,dbaPasswordValue,dbVersion, features[i]);
			RuleEvaluator re = RuleEvaluator.getInstance(dbType, installLoc, instance,dbVersion, features[i]);
			PropertyRuleLoader prl = new PropertyRuleLoader(dbType);
			prl.load(RULE_FILE, features[i]);
			List<VersionCheckRules> vcrs = prl.getCheckRules();
			if (feature.equals(Constants.FEATURE_COGNOS)){
				versions[i] = Constants.FEATURE_COGNOS;
				
			}else if(feature.equals(Constants.FEATURE_LIBRARY_GCD) || feature.equals(Constants.FEATURE_LIBRARY_OS)){
				versions[i] = Constants.FEATURE_LIBRARY;
			}else{
				re.evaluate(vcrs);
				for (VersionCheckRules vcr : vcrs) {
					if(vcr.isMatched()) {
						versions[i] = vcr.getVersion();
						break;
					}
				}
			}
		}
		
		Map<String, String> result = new HashMap<String, String>();
		for(int i = 0 ; i< features.length; i++) {
			result.put(features[i], versions[i]);
		}
		
		return result;
	}
}
