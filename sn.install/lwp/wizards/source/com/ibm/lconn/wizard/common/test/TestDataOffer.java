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
package com.ibm.lconn.wizard.common.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.tdipopulate.backend.AttributeMapping;
import com.ibm.lconn.wizard.tdipopulate.ldap.ObjectClass;

public class TestDataOffer {
//	public static void openModifier(){
//		Shell shell = new Shell(ResourcePool.getDisplay());
//		Combo combo = new Combo(shell, SWT.NONE);
//		Text value = new Text(shell, SWT.NONE);
//		Field[] fields = ClusterConstant.class.getFields();
//		for (int i = 0; i < fields.length; i++) {
//			
//		}
//		shell.open();
//	}
	
	public static void setLocale(){
		if(!Constants.BOOL_TRUE.equals(MessageUtil.getSetting("global.enableGlobalization"))){
			Locale.setDefault(Locale.ENGLISH);
		}
	}
	
	public static void setLDAPAttributeList() {
		List<ObjectClass> classes = new ArrayList<ObjectClass>();
		ObjectClass objectClass = new ObjectClass("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		objectClass.addAttrbute("a1");
		objectClass.addAttrbute("a2");
		objectClass.addAttrbute("a3");
		classes.add(objectClass);
		objectClass = new ObjectClass("aaa");
		objectClass.addAttrbute("a1");
		objectClass.addAttrbute("a2");
		objectClass.addAttrbute("a1");
		objectClass.addAttrbute("a2");
		objectClass.addAttrbute("a1");
		objectClass.addAttrbute("a2");
		objectClass.addAttrbute("a1");
		objectClass.addAttrbute("a2");
		objectClass.addAttrbute("a1");
		objectClass.addAttrbute("a2fffffffffffffffffffffffffffffffffffffffff");
		classes.add(objectClass);
		for (int i = 0; i< 100; i++) {
		objectClass = new ObjectClass("aaa");
		classes.add(objectClass);
		}
		DataPool.setComplexData(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.TDI_LDAP_OBJECTCLASSES, classes);
	}
	
	public static void setSummaryStyle(){
		List<StyleRange> a = new ArrayList<StyleRange>();
		a.add(new StyleRange(0, 3, null, null, SWT.BOLD));
		a.add(new StyleRange(9, 12, null, null, SWT.BOLD));
//		StyleRange sr  = new StyleRange(0, 3, null, null, SWT.BOLD);
		
		DataPool.setComplexData(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_TDI_SUMMARY, a);
	}

	public static void setLDAPDNDropdown() {
		String[] dn = { "dn1", "dn2", "dn3" };
		DataPool.setComplexData(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_TDI_LDAP_SEARCH_BASE, Arrays.asList(dn));
	}
	
	public static void setKeystoreType(){
		String[] types = {"JKS","PKCS12"};
		DataPool.setComplexData(Constants.WIZARD_ID_TDIPOPULATE,
				Constants.INPUT_TDI_SSL_TYPE, Arrays.asList(types));
	}

	public static CommandResultInfo getDefaultCommandInfo() {
		CommandResultInfo info = new CommandResultInfo();
		info.setExecState(CommandResultInfo.COMMAND_ERROR);
		info.setExitMessage("Exited with error");
		info.setLogPath("C:/log.txt");
		return info;
	}
	
	public static void setMappingTable(){
		loadAttributeMapping(Constants.defaultMappingTIVOLI, Constants.validationRules);
	}
	
	private static void loadAttributeMapping(String defaultMapping, String validationRules) {
		Properties mappings = new Properties();
		Properties vRules = new Properties();
		try {
			mappings.load(new FileInputStream(defaultMapping));
			vRules.load(new FileInputStream(validationRules));
		} catch(IOException e) {
		}
		
		List<AttributeMapping> m = new ArrayList<AttributeMapping>();
		
		Enumeration<Object> e = mappings.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String vRule = vRules.getProperty(key);
			String map = mappings.getProperty(key, "null");
			
			m.add(new AttributeMapping(key, map, vRule));
		}
		
		Collections.sort(m);
		DataPool.setComplexData(Constants.WIZARD_ID_TDIPOPULATE, Constants.INPUT_TDI_MAPPING_TABLE, m);
	}
}
