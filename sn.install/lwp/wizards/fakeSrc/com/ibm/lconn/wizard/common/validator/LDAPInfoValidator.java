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

import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.tdipopulate.ldap.ServerConfig;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class LDAPInfoValidator extends AbstractValidator {
	private static final int msgCount = 1;
	private static int count = 0;
	private static final Logger logger = LogUtil.getLogger(LDAPInfoValidator.class);
	private String hostname, port, bindDN, password, useSSL;
	
	public LDAPInfoValidator(String hostname, String port, String bindDN, String password, String useSSL) {
		this.hostname = hostname;
		this.port = port;
		this.bindDN = bindDN;
		this.password = password;
		this.useSSL = useSSL;
	}

	
	public int validate() {
		count++;
		String _hostname = eval(hostname);
		String _bindDN = eval(bindDN);
		String _password = eval(password);
		String _useSSL = eval(useSSL);
		
		int _port = -1 ;
		try {
			_port = Integer.parseInt(eval(port));
		} catch (NumberFormatException e) {
			// ignore, validator before this need validate the number
		}
		
		boolean SSLRequired = Constants.BOOL_TRUE.equalsIgnoreCase(_useSSL);
		
		if(SSLRequired) {
			// if ssl required try to get SSL config
			String ksPath = eval(Constants.INPUT_TDI_SSL_KEY_STORE);
			String ksType = eval(Constants.INPUT_TDI_SSL_TYPE);
			String ksPwd = eval(Constants.INPUT_TDI_SSL_PASSWORD);
			System.setProperty(Constants.KEY_CASTORE, ksPath);
			System.setProperty(Constants.KEY_CATYPE, ksType);
			System.setProperty(Constants.KEY_CAPASSWORD, ksPwd);
		}
		
		
		ServerConfig sc = new ServerConfig(
				_hostname, 
				_port,
				_bindDN,
				_password,
				SSLRequired);
		
		if(count ==1) {
				logError("cannot_connect_ldap", sc.getHostURL());
				return 1;
			} 
		if (count ==2){
				logError("cannot_login_ldap", _bindDN);
				return 2;
			}
			
		if(count ==3) {
			logError("fail_to_contact_ldap", sc.getHostURL());
			return 3;
		}

		String serverType = Constants.LDAP_ADAM;
		if(count==4) {			
				logError("not_supported_ldap", serverType);
				return 4;
			}
		if(count ==5) {
//			logger.log(Level.SEVERE, "validator.severe.failed_to_get_ldap_type", e);
			logWarning("failed_to_get_ldap_type");
			return -1;
		}


		return 0;
	}

}
