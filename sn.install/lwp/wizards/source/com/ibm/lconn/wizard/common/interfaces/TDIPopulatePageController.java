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
package com.ibm.lconn.wizard.common.interfaces;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.DefaultValue;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.exceptions.LdapException;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.ext.LCWizardInput;
import com.ibm.lconn.wizard.common.validator.TDIInstallLocValidator;
import com.ibm.lconn.wizard.tdipopulate.backend.AttributeMapping;
import com.ibm.lconn.wizard.tdipopulate.backend.NetStore;
import com.ibm.lconn.wizard.tdipopulate.backend.TDIEnv;
import com.ibm.lconn.wizard.tdipopulate.ldap.ObjectClass;
import com.ibm.lconn.wizard.tdipopulate.ldap.ServerConfig;
import com.ibm.lconn.wizard.tdipopulate.ldap.impl.LdapControl;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class TDIPopulatePageController extends DefaultPageController {
	private static final Logger logger = LogUtil
			.getLogger(TDIPopulatePageController.class);
	private  static String oldDBType = "";

	public TDIPopulatePageController(String wizardId) {
		super(wizardId);
	}

	public String performAction(String basePage, String actionName) {
		String nextPage = super.performAction(basePage, actionName);

		// if (Constants.LAUNCH_MODE_CONSOLE.equals(DataPool.getValue(
		// Constants.WIZARD_ID_TDIPOPULATE, Constants.WIZARD_LAUNCH_MODE))) {
		// return nextPage;
		// }
		// passed validation
		if (basePage != null && !basePage.equals(nextPage)) {
			// change the tdienv.bat or tdienv.sh

			if (Constants.WIZARD_PAGE_TDIInstallDir.equals(basePage)) {
				String tdiHome = DataPool.getValue(
						Constants.WIZARD_ID_TDIPOPULATE,
						Constants.INPUT_TDI_INSTALL_DIR);
				Map<String, String> env = DefaultWizardDataLoader
						.loadTDIEnv(tdiHome);
				TDIEnv.writeTDIEnv(env);
				if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
					startDerby();
				}
			}

			if (Constants.WIZARD_PAGE_ActionChooser.equals(basePage)) {
				String action = DataPool.getValue(wizardId,
						Constants.INPUT_TDI_ACTION_TYPE);
				if (CommonHelper.equals(action,
						Constants.TDI_START_FROM_LAST_SESSION)) {
					DefaultWizardDataLoader.loadLastSession(wizardId,
							Constants.TDI_DEFAULT_TASK);
					String newValue = DataPool.getValue(wizardId, Constants.INPUT_TDI_OPTIONAL_TASK);
					if (-1 != Util.indexOf(Util.delimStr(newValue ),
							Constants.LDAP_OPTIONAL_TASK_MARK_MANAGER)) {
						DataPool.setValue(wizardId, Constants.INPUT_TDI_MARK_MANGER_CHECK, Constants.OPTION_MARKMANGER_YES);
					}else{
						DataPool.setValue(wizardId, Constants.INPUT_TDI_MARK_MANGER_CHECK, Constants.OPTION_MARKMANGER_NO);
					}
				} else {
					DefaultWizardDataLoader.loadNewSession(wizardId);
				}
			}
			if (Constants.WIZARD_PAGE_DBChooser.equals(basePage)) {
				populateDbFields();
			}
			if (Constants.WIZARD_PAGE_DBInfo.equals(basePage)) {
				String port = DataPool.getValue(wizardId,
						Constants.INPUT_TDI_LDAP_SERVER_PORT);
				if (port == null || port.trim().equals("")) {
					DataPool.setValue(wizardId,
							Constants.INPUT_TDI_LDAP_SERVER_PORT,
							Constants.LDAP_DEFAULT_PORT);
				}
			} else if (Constants.WIZARD_PAGE_LDAPInfo.equals(basePage)) {
				String wizardId = Constants.WIZARD_ID_TDIPOPULATE;
				String[] SSLWidgets = { Constants.INPUT_TDI_SSL_KEY_STORE,
						Constants.INPUT_TDI_SSL_PASSWORD,
						Constants.INPUT_TDI_SSL_TYPE };
				LCWizardInput sslCheck = DataPool.getWizardInputWidget(
						wizardId, Constants.INPUT_TDI_LDAP_USE_SSL);
				if (sslCheck != null) {
					boolean enabled = CommonHelper.equals(sslCheck.getValue(),
							Constants.BOOL_TRUE);
					if (enabled) {
						for (String sslWidgetId : SSLWidgets) {
							LCWizardInput sslWidget = DataPool
									.getWizardInputWidget(wizardId, sslWidgetId);
							sslWidget.setEnable(enabled);
						}
					} else {
						nextPage = getNextPage(nextPage);
					}
				}
			} else if (Constants.WIZARD_PAGE_LDAPLoginInfo.equals(basePage)) {
				populateBaseDNs();
			} else if (Constants.WIZARD_PAGE_LDAPSearch.equals(basePage)) {
				populateObjectClasses();
				// if (DataPool.getValue(wizardId,
				// Constants.INPUT_TDI_ACTION_TYPE)
				// .equals(Constants.TDI_START_FROM_LAST_SESSION)
				// || CommonHelper.equals(DataPool.getValue(wizardId,
				// "defaultMapping.loaded"), Constants.BOOL_TRUE)
				// ){
				// //do nothing
				// }else{
				if (null == DataPool.getComplexData(wizardId,
						Constants.INPUT_TDI_MAPPING_TABLE)) {
					populateDefaultMapping();
				}
			} else if ((Constants.WIZARD_ID_TDIPOPULATE + "." + Constants.WIZARD_PAGE_COMMON_WELCOME)
					.equals(basePage)) {
				if (!DefaultWizardDataLoader.hasLastSession(
						Constants.WIZARD_ID_TDIPOPULATE,
						Constants.TDI_DEFAULT_TASK)) {
					// don't have last session, skip last session selection
					DataPool.resetAll(Constants.WIZARD_ID_TDIPOPULATE);
					nextPage = getNextPage(nextPage);
				}
			}
		}
		if (Constants.WIZARD_PAGE_TDIInstallDir.equals(nextPage)) {
			TDIInstallLocValidator validator = new TDIInstallLocValidator(
					Constants.INPUT_TDI_INSTALL_DIR, CommonHelper
							.getPlatformType());
			if (0 == validator.validate()) {
				String tdiHome = DataPool.getValue(
						Constants.WIZARD_ID_TDIPOPULATE,
						Constants.INPUT_TDI_INSTALL_DIR);
				Map<String, String> env = DefaultWizardDataLoader
						.loadTDIEnv(tdiHome);
				TDIEnv.writeTDIEnv(env);
				if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
					startDerby();
				}
				nextPage = getNextPage(nextPage);
			}

		}
		return nextPage;
	}

	private void startDerby() {
		String tdiHome = DataPool.getValue(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_TDI_INSTALL_DIR);
		Map<String, String> env = DefaultWizardDataLoader.loadTDIEnv(tdiHome);
		if (NetStore.isStarted(env)) {
			DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE,
					Constants.NETSTORE_NEED_STOP, Constants.BOOL_TRUE);
		} else {
			NetStore.start(env);
		}
	}

	private void populateDefaultMapping() {
		// if start from last session and ldap type is not changed
		// if(DataPool.getValue(wizardId,
		// Constants.INPUT_TDI_ACTION_TYPE).equals(Constants.TDI_START_FROM_LAST_SESSION)&&
		// (
		// !DataPool.getDirtyFlag(wizardId, Constants.INPUT_TDI_LDAP_TYPE))
		// ) {
		// return;
		// }
		String ldapType = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_TYPE);
		if (Constants.LDAP_AD.equals(ldapType)) {
			loadAttributeMapping(Constants.defaultMappingAD,
					Constants.validationRules);
		} else if (Constants.LDAP_DOMINO.equals(ldapType)) {
			loadAttributeMapping(Constants.defaultMappingDOMINO,
					Constants.validationRules);
		} else if (Constants.LDAP_ORACLE.equals(ldapType)) {
			loadAttributeMapping(Constants.defaultMappingSUN,
					Constants.validationRules);
		} else if (Constants.LDAP_TIVOLI.equals(ldapType)) {
			loadAttributeMapping(Constants.defaultMappingTIVOLI,
					Constants.validationRules);
		} else if (Constants.LDAP_NDS.equals(ldapType)) {
			loadAttributeMapping(Constants.defaultMappingNDS,
					Constants.validationRules);
		} else if (Constants.LDAP_ADAM.equals(ldapType)) {
			loadAttributeMapping(Constants.defaultMappingADAM,
					Constants.validationRules);
		}
	}

	private void loadAttributeMapping(String defaultMapping,
			String validationRules) {
		Properties mappings = new Properties();
		Properties vRules = new Properties();
		try {
			mappings.load(new FileInputStream(defaultMapping));
			vRules.load(new FileInputStream(validationRules));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "", e);
		}

		List<AttributeMapping> m = new ArrayList<AttributeMapping>();

		Enumeration<Object> e = mappings.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String vRule = vRules.getProperty(key);
			String map = mappings.getProperty(key, "null");

			m.add(new AttributeMapping(key, map, vRule));
		}

		Collections.sort(m);
		DataPool.setComplexData(wizardId, Constants.INPUT_TDI_MAPPING_TABLE, m);
	}

	private int populateObjectClasses() {
		String hostname = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_SERVER_NAME);
		int port = Integer.parseInt(DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_SERVER_PORT));
		String login = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_LOGIN_USER);
		String password = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_LOGIN_PASSWD);
		boolean sslEnabled = Constants.BOOL_TRUE.equals(DataPool.getValue(
				wizardId, Constants.INPUT_TDI_LDAP_USE_SSL));
		boolean anonymousAccess = Constants.BOOL_TRUE.equals(DataPool.getValue(
				wizardId, Constants.INPUT_TDI_LDAP_ANONYMOUS_ACCESS));

		String baseDN = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_SEARCH_BASE);
		String searchFileter = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_SERVER_FILTER);

		ServerConfig sc = new ServerConfig(hostname, port, login, password,
				sslEnabled, anonymousAccess);
		LdapControl lc = new LdapControl(sc);
		List<ObjectClass> objectClasses = new ArrayList<ObjectClass>();
		Map<String, ObjectClass> map = null;
		try {
			lc.connect();
			map = lc.getObjectClasses(baseDN, searchFileter);
		} catch (LdapException e) {
			logger.log(Level.SEVERE,
					"tdipopulate.severe.cannot_get_objectclasses", e);
		}

		if (map != null) {
			Iterator<String> e = map.keySet().iterator();
			while (e.hasNext()) {
				String key = e.next();
				if ("top".equalsIgnoreCase(key)) {
					// ignore the base object
					continue;
				}
				objectClasses.add(map.get(key));
			}
		}

		DataPool.setComplexData(wizardId, Constants.TDI_LDAP_OBJECTCLASSES,
				objectClasses);
		if (map == null) {
			return 0;
		}
		return map.keySet().size();
	}

	private void populateBaseDNs() {
		if (DataPool.getValue(wizardId, Constants.INPUT_TDI_ACTION_TYPE)
				.equals(Constants.TDI_START_FROM_LAST_SESSION)
				&& (!DataPool.getDirtyFlag(wizardId,
						Constants.INPUT_TDI_LDAP_SERVER_NAME)
						&& !DataPool.getDirtyFlag(wizardId,
								Constants.INPUT_TDI_LDAP_SERVER_PORT) && !DataPool
						.getDirtyFlag(wizardId,
								Constants.INPUT_TDI_LDAP_LOGIN_USER))) {

		} else {
			// should always populate BaseDNs
			String ldapType = DataPool.getValue(wizardId,
					Constants.INPUT_TDI_LDAP_TYPE);
			DataPool.setValue(wizardId, Constants.INPUT_TDI_LDAP_SERVER_FILTER,
					DefaultValue.getLDAPSearchFilter(ldapType));
		}
		String hostname = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_SERVER_NAME);
		int port = Integer.parseInt(DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_SERVER_PORT));
		String login = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_LOGIN_USER);
		String password = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_LDAP_LOGIN_PASSWD);
		boolean sslEnabled = Constants.BOOL_TRUE.equals(DataPool.getValue(
				wizardId, Constants.INPUT_TDI_LDAP_USE_SSL));
		boolean anonymousAccess = Constants.BOOL_TRUE.equals(DataPool.getValue(
				wizardId, Constants.INPUT_TDI_LDAP_ANONYMOUS_ACCESS));

		ServerConfig sc = new ServerConfig(hostname, port, login, password,
				sslEnabled, anonymousAccess);
		LdapControl lc = new LdapControl(sc);
		List<String> baseDNs = new ArrayList<String>();
		try {
			lc.connect();
			baseDNs = lc.getBaseDNs();
		} catch (LdapException e) {
			logger
					.log(Level.SEVERE, "tdipopulate.severe.cannot_get_basedns",
							e);

		}
		DataPool.setComplexData(wizardId, Constants.INPUT_TDI_LDAP_SEARCH_BASE,
				baseDNs);

	}

	// not used any more
	// private void populateLDAPFields() {
	// // if start from last session and ldap type is not changed
	// if (DataPool.getValue(wizardId, Constants.INPUT_TDI_ACTION_TYPE)
	// .equals(Constants.TDI_START_FROM_LAST_SESSION)
	// && (!DataPool.getDirtyFlag(wizardId,
	// Constants.INPUT_TDI_LDAP_TYPE))) {
	// return;
	// }
	//
	// String ldapType = DataPool.getValue(wizardId,
	// Constants.INPUT_TDI_LDAP_TYPE);
	// DataPool.setValue(wizardId, Constants.INPUT_TDI_LDAP_SERVER_PORT,
	// DefaultValue.getLDAPPort(ldapType, false));
	// DataPool.setValue(wizardId, Constants.INPUT_TDI_LDAP_LOGIN_USER,
	// DefaultValue.getLDAPAdminDN(ldapType));
	// }

	private void populateDbFields() {
		// if start from last session and database type is not changed
		if (DataPool.getValue(wizardId, Constants.INPUT_TDI_ACTION_TYPE)
				.equals(Constants.TDI_START_FROM_LAST_SESSION)
				&& (!DataPool.getDirtyFlag(wizardId, Constants.INPUT_DB_TYPE))) {
			return;
		}
		String dbType = DataPool.getValue(wizardId,
				Constants.INPUT_TDI_DB_CHOOSER);
		if (!oldDBType.equals(dbType) || CommonHelper.isEmpty(DataPool.getValue(wizardId, Constants.INPUT_TDI_DB_PORT)))
		{
			DataPool.setValue(wizardId, Constants.INPUT_TDI_DB_PORT, DefaultValue
					.getDatabasePort(dbType));
		}
		if (!oldDBType.equals(dbType) || CommonHelper.isEmpty(DataPool.getValue(wizardId, Constants.INPUT_TDI_DB_NAME)))
		{
			DataPool.setValue(wizardId, Constants.INPUT_TDI_DB_NAME, DefaultValue
					.getFeatureDatabaseName(Constants.FEATURE_PROFILES, dbType));
		}
		if (!oldDBType.equals(dbType) || CommonHelper.isEmpty(DataPool.getValue(wizardId, Constants.INPUT_TDI_DB_USER)))
		{
			DataPool.setValue(wizardId, Constants.INPUT_TDI_DB_USER, DefaultValue
				.getFeatureDatabaseUser(Constants.FEATURE_PROFILES, dbType));
		}
		oldDBType = dbType;
	}
}
