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
package com.ibm.lconn.wizard.common.validator;

import java.util.List;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.tdipopulate.backend.AttributeMapping;
import com.ibm.lconn.wizard.tdipopulate.js.FunctionOperator;
import com.ibm.lconn.wizard.tdipopulate.ldap.ObjectClass;

/**
 * This class validates whether the LDAP attribute mapping is valid
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 */
public class LDAPMappingValidator extends AbstractValidator {
	@SuppressWarnings("unused")
	private static final Logger logger = LogUtil
			.getLogger(LDAPMappingValidator.class);

	private String ldapMap = null;
	private String ldapType = null;

	public LDAPMappingValidator(String ldapMap, String ldapType) {
		this.ldapMap = ldapMap;
		this.ldapType = ldapType;
	}

	@SuppressWarnings("unchecked")
	public int validate() {
		reset();
		message = "";
		List<AttributeMapping> mappings = (List<AttributeMapping>) DataPool
				.getComplexData(wizard, ldapMap);
		List<ObjectClass> objectClasses = (List<ObjectClass>) DataPool
				.getComplexData(wizard, Constants.TDI_LDAP_OBJECTCLASSES);
		List<String> functions = FunctionOperator.getNameList();

		String _ldapType = eval(ldapType);
		
		for (AttributeMapping m : mappings) {
			String mAttr = m.getAttribute();
			if (m.isRequired() && Constants.NULL.equals(mAttr)) {
				// required mapping cannot be null
				message = message
						+ getMessage("required_field_is_null", m.getDbField())
						+ Constants.CRLF;
			} else if (!Constants.NULL.equals(mAttr)) {
				// check ldap attributes
				boolean found = false;
				for (ObjectClass cl : objectClasses) {
					for (String attr : cl.getAttrbutes()) {
						// ldap attribute is case insensitive
						if (mAttr.equalsIgnoreCase(attr)) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					// check functions 
					for (String f : functions) {
						if (mAttr.equals("{" + f + "}")) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					// check build in attributes
					for(String b : Constants.BUILT_IN_LDAP_ATTRIBUTE) {
						if(mAttr.equals(b)) {
							found = true;
							break;
						}
					}
				}
				if(!found) {
					// finally check ldap uuid
					if(Constants.LDAP_AD.equals(_ldapType)) {
						found = Constants.AD_UUID.equalsIgnoreCase(mAttr);
					} else if (Constants.LDAP_DOMINO.equals(_ldapType)) {
						found = Constants.DOMINO_UUID.equalsIgnoreCase(mAttr);
					} else if (Constants.LDAP_ORACLE.equals(_ldapType)) {
						found = Constants.ORACLE_UUID.equalsIgnoreCase(mAttr);
					} else if (Constants.LDAP_TIVOLI.equals(_ldapType)) {
						found = Constants.TIVOLI_UUID.equalsIgnoreCase(mAttr);
					}
				}
				
				if (!found) {
					message = message
							+ getMessage("no_attr_or_func_found", m
									.getAttribute(), m.getDbField())
							+ Constants.CRLF;
				}
			}
		}

		if (!"".equals(message)) {
			title = getTite("mapping_invalid");
			return 1;
		}

		return 0;
	}
}
