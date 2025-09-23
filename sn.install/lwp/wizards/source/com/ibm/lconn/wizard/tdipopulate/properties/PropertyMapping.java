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
package com.ibm.lconn.wizard.tdipopulate.properties;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.Locale;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DatabaseUtil;
import com.ibm.lconn.wizard.common.LdapUtil;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.property.Property;

public class PropertyMapping {
	private static void log(String msg) {
		LogUtil.getLogger(PropertyMapping.class).log(Level.FINEST, msg);
	}
	
	public static void mapProperties(Properties uiProp) {
		TDIPropertyLoader.setLoadType(LoaderType.LOAD_WORK_DIR);
		Property tdiProp = TDIPropertyLoader.load(Constants.TDI_PROP);
		Map<String, String> tdiMap = tdiProp.getAllProperty();

		log("=== ui properties ==");
		Iterator keyIt = uiProp.keySet().iterator();
		while(keyIt.hasNext()) {
			String key = (String)keyIt.next();
			log(key+"="+uiProp.getProperty(key));
		}
		log("=== ui properties end ==");
		if(Locale.getDefault().toString().equalsIgnoreCase("tr_TR") || Locale.getDefault().toString().equalsIgnoreCase("tr")) {
			// LDAP
			tdiMap.put("source_ldap_url", LdapUtil.getUrl(uiProp.getProperty("ldap.hostname").toLowerCase(new Locale ("en")), uiProp.getProperty("ldap.port")));
			tdiMap.put("source_ldap_user_login", uiProp.getProperty("ldap.user").toLowerCase(new Locale ("en")));
			tdiMap.put("source_ldap_search_base", uiProp.getProperty("ldap.dn.base").toLowerCase(new Locale ("en")));
			tdiMap.put("source_ldap_search_filter", uiProp.getProperty("ldap.filter").toLowerCase(new Locale ("en")));
			tdiMap.put("source_ldap_use_ssl", String.valueOf(Constants.BOOL_TRUE.equalsIgnoreCase(uiProp.getProperty("ldap.enable.ssl"))));
			tdiMap.put("{protect}-source_ldap_user_password", uiProp.getProperty("ldap.password"));
			tdiMap.remove("source_ldap_user_password");
		} else {
			// LDAP
			tdiMap.put("source_ldap_url", LdapUtil.getUrl(uiProp.getProperty("ldap.hostname"), uiProp.getProperty("ldap.port")));
			tdiMap.put("source_ldap_user_login", uiProp.getProperty("ldap.user"));
			tdiMap.put("source_ldap_search_base", uiProp.getProperty("ldap.dn.base"));
			tdiMap.put("source_ldap_search_filter", uiProp.getProperty("ldap.filter"));
			tdiMap.put("source_ldap_use_ssl", String.valueOf(Constants.BOOL_TRUE.equalsIgnoreCase(uiProp.getProperty("ldap.enable.ssl"))));
			tdiMap.put("{protect}-source_ldap_user_password", uiProp.getProperty("ldap.password"));
			tdiMap.remove("source_ldap_user_password");
		}

		// database
		tdiMap.put("dbrepos_jdbc_url", DatabaseUtil.getDBUrl(uiProp.getProperty("db.type"), uiProp.getProperty("db.hostname"), uiProp.getProperty("db.port"), uiProp.getProperty("db.name")));
		tdiMap.put("dbrepos_jdbc_driver", DatabaseUtil.getJDBCDriver(uiProp.getProperty("db.type")));
		tdiMap.put("dbrepos_username", uiProp.getProperty("db.user"));
		tdiMap.put("{protect}-dbrepos_password", uiProp.getProperty("db.password"));
		tdiMap.remove("dbrepos_password");
		
		tdiProp.setProperty(false, tdiMap);

		Property solutionProp = TDIPropertyLoader.load(Constants.TDI_SOL_PROPS);
		Map<String, String> solutionMap = solutionProp.getAllProperty();
		solutionMap.put(Constants.KEY_CASTORE, uiProp.getProperty("ssl.keyStore"));
		solutionMap.put(Constants.KEY_CATYPE, uiProp.getProperty("ssl.type"));
		solutionMap.put(Constants.TDI_KEY_CPASSWORD, uiProp.getProperty("ssl.password"));
		solutionProp.setProperty(false, solutionMap);
	}

	public static void setValidateRuleProperty(Properties validateRules) {
		TDIPropertyLoader.setLoadType(LoaderType.LOAD_WORK_DIR);
		Property uiProp = TDIPropertyLoader.load(Constants.TDI_VALIDATE_PROP);
		uiProp.setProperty(false, validateRules);
	}

	public static void setFromSourceProperty(Properties sources) {
		TDIPropertyLoader.setLoadType(LoaderType.LOAD_WORK_DIR);
		Property uiProp = TDIPropertyLoader.load(Constants.TDI_FROM_SOURCE_PROP);
		uiProp.setProperty(false, sources);
	}
}
