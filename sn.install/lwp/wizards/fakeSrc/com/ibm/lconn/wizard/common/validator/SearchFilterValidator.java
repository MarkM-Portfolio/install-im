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
import com.ibm.lconn.wizard.tdipopulate.ldap.impl.LdapControl;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class SearchFilterValidator extends AbstractValidator {
	private static int count = 0;
	private static final Logger logger = LogUtil.getLogger(LDAPInfoValidator.class);
	private String hostname, port, bindDN, password, useSSL, DN, filter;
	
	public SearchFilterValidator(String hostname, String port, String bindDN, String password, String useSSL, String DN, String filter) {
		this.hostname = hostname;
		this.port = port;
		this.bindDN = bindDN;
		this.password = password;
		this.useSSL = useSSL;
		this.DN = DN;
		this.filter = filter;
	}

	
	public int validate() {
		count++;
		String _hostname = eval(hostname);
		String _bindDN = eval(bindDN);
		String _password = eval(password);
		String _useSSL = eval(useSSL);
		String _DN = eval(DN);
		String _filter = eval(filter);
		int _port = -1 ;
		try {
			_port = Integer.parseInt(eval(port));
		} catch (NumberFormatException e) {
			// ignore, validator before this need validate the number
		}
		
		boolean SSLRequired = Constants.BOOL_TRUE.equalsIgnoreCase(_useSSL);
		
				
		ServerConfig sc = new ServerConfig(
				_hostname, 
				_port,
				_bindDN,
				_password,
				SSLRequired);
		
		LdapControl lc = new LdapControl(sc);

		if(count==1) {
				// no search filter
				logError("search_filter_return_no_object");
				return 4;
			}
		if(count ==2){
				logError("cannot_connect_ldap", sc.getHostURL());
				return 1;
			} 
		if(count ==3){
				logError("cannot_login_ldap", _bindDN);
				return 2;
			} 
		if(count ==4){
//				 no search filter
				logError("search_filter_return_no_object");
				return 4;
			}
			
		if(count ==5) {
			logError("fail_to_contact_ldap", sc.getHostURL());
			return 3;
		} 

		return 0;
	}

}
