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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.NamingSecurityException;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.tdipopulate.ldap.ServerConfig;
import com.ibm.lconn.wizard.tdipopulate.ldap.impl.LdapControl;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class SearchFilterValidator extends AbstractValidator {
	private static final Logger logger = LogUtil.getLogger(LDAPInfoValidator.class);
	private String hostname, port, bindDN, password, useSSL, useAnonymous, DN, filter;
	
	public SearchFilterValidator(String hostname, String port, String bindDN, String password, String useSSL, String useAnonymous, String DN, String filter) {
		this.hostname = hostname;
		this.port = port;
		this.bindDN = bindDN;
		this.password = password;
		this.useSSL = useSSL;
		this.DN = DN;
		this.filter = filter;
		this.useAnonymous = useAnonymous;
	}

	public SearchFilterValidator(String hostname, String port, String bindDN, String password, String useSSL, String DN, String filter) {
		this(hostname, port, bindDN, password, useSSL, null, DN, filter);
	}
	
	public int validate() {
		String _hostname = eval(hostname);
		String _bindDN = eval(bindDN);
		String _password = eval(password);
		String _useSSL = eval(useSSL);
		// in default validator, always need authentication, unless useAnonymous flag explicitly set to true
		String _useAnonymous = useAnonymous == null ? Constants.BOOL_FALSE : eval(useAnonymous);
		String _DN = eval(DN);
		String _filter = eval(filter);
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
				SSLRequired,
				Constants.BOOL_TRUE.equalsIgnoreCase(_useAnonymous));
		
		LdapControl lc = new LdapControl(sc);

		try {
			lc.connect();
			if(!lc.hasSearchResult(_DN, _filter)) {
				// no search filter
				logError("search_filter_return_no_object");
				return 4;
			}
		} catch (Exception e) {	
			logger.log(Level.SEVERE, "validator.severe.cannot_connect_ldap", e);
			if(e.getCause() instanceof CommunicationException) {
				logError("cannot_connect_ldap", sc.getHostURL());
				return 1;
			} else if(e.getCause() instanceof NamingSecurityException) {
				logError("cannot_login_ldap", _bindDN);
				return 2;
			} else if(e.getCause() instanceof NamingException) {
//				 no search filter
				logError("search_filter_return_no_object");
				return 4;
			}
			
			// others 
			logError("fail_to_contact_ldap", sc.getHostURL());
			return 3;
		} finally {
			try {
				lc.closeConection();
			} catch (Exception e) {
				// ignore
			}
		}

		return 0;
	}

}
