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
package com.ibm.lconn.wizard.tdipopulate.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.ibm.lconn.wizard.common.exceptions.ErrorCode;
import com.ibm.lconn.wizard.common.exceptions.LdapConnectionException;
import com.ibm.lconn.wizard.common.exceptions.LdapException;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.tdipopulate.ldap.impl.LdapSSLFactory;
import com.sun.jndi.ldap.LdapCtxFactory;

/**
 * 
 * @author Bai Jian Su (subaij@cn.ibm.com)
 * 
 */
public class GeneralLdapControl {

	private static final Logger logger = LogUtil
			.getLogger(GeneralLdapControl.class);

	public static final String ALL_OBJECT_CLASS = "(objectclass=*)";

	public static final String AUTHENTICATION_SIMPLE = "simple";
	public static final String AUTHENTICATION_NONE = "none";

	private String batch_size = "0";

	private String referral_follow = "follow";

	protected int version;

	protected String timeoutPropName = "";

	protected ServerConfig sc = null;

	protected InitialDirContext ctx = null;

	public void connect() throws LdapConnectionException {

		connect(sc.getHostName(), sc.getPort(), sc.getUid(), sc.getPassword(),
				sc.isSsl(), sc.isAnonymousAccess(), sc.getTimeOut());
	}

	protected void connect(String host, int port, String username,
			String password, boolean ssl, boolean anonymousAccess) throws LdapConnectionException {
		connect(host, port, username, password, ssl, anonymousAccess, -1);
	}

	public void closeConection() throws LdapConnectionException {
		try {
			if (null != ctx) {
				logger.log(Level.INFO, "tdipopulate.info.ldap.operation.close",
						new Object[] { sc.getHostURL() });
				ctx.close();
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,
					"tdipopulate.severe.ldap.operation.close_failed");
			throw new LdapConnectionException(ErrorCode.ERROR_LDAP_CLOSE,
					"err.ldap.initation.close", new Object[] {
							sc.getHostName(), sc.getPort() });
		}
	}

	protected void connect(String host, int port, String userName,
			String password, boolean ssl, boolean anonymousAccess, int timeOut)
			throws LdapConnectionException {
		// Initialize the basic connection properties
		logger.log(Level.INFO, "tdipopulate.info.ldap.operation.open",
				new Object[] { sc.getHostURL() });
		Hashtable<String, Comparable<? extends Object>> env = new Hashtable<String, Comparable<? extends Object>>();
		env
				.put(Context.INITIAL_CONTEXT_FACTORY, LdapCtxFactory.class
						.getName());
		env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);
		if (timeOut != -1) {
			env.put(timeoutPropName, new Integer(timeOut));
		}
		// Initialize the option SSL property
		if (ssl) {
			env.put(Context.SECURITY_PROTOCOL, "SSL");
			env.put("java.naming.ldap.factory.socket", LdapSSLFactory.class
					.getName());
			env.put(Context.BATCHSIZE, batch_size);
		}
		if(anonymousAccess) {
			env.put(Context.SECURITY_AUTHENTICATION, AUTHENTICATION_NONE);
		} else {
			env.put(Context.SECURITY_AUTHENTICATION, AUTHENTICATION_SIMPLE);
			env.put(Context.SECURITY_PRINCIPAL, userName);
			env.put(Context.SECURITY_CREDENTIALS, password);
		}
		env.put(Context.REFERRAL, referral_follow);

		// Connect to LDAP server
		try {
			ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			logger.log(Level.SEVERE,
					"tdipopulate.severe.ldap.operation.initiateContext",
					new Object[] { sc.getHostURL() });
			throw new LdapConnectionException(e, ErrorCode.ERROR_LDAP_OPEN,
					"err.ldap.operation.open", new Object[] { sc.getHostURL() });
		}
	}

	protected Attributes getAttributes(String name, String[] attrIds)
			throws LdapException {
		if (null == ctx)
			throw new LdapConnectionException(ErrorCode.ERROR_LDAP_OPERATION,
					"err.ldap.not_initiated", new Object[] { sc.getHostName(),
							sc.getPort() });
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);
		if (-1 != sc.getTimeOut())
			ctls.setTimeLimit(sc.getTimeOut());
		ctls.setReturningAttributes(attrIds);
		try {
			NamingEnumeration<SearchResult> results = ctx.search(name,
					GeneralLdapControl.ALL_OBJECT_CLASS, ctls);
			SearchResult sr = results.next();
			results.close();
			// agent threads"
			return sr.getAttributes();
		} catch (NamingException e) {
			throw new LdapConnectionException(e,
					ErrorCode.ERROR_LDAP_OPERATION,
					"err.ldap.operation.getAttributes", new Object[] { sc
							.getHostURL() });
		}
	}
	
	protected List<String> getRootEntries() throws LdapException  {
		List<String> rootEntries = new ArrayList<String>();
		if (null == ctx)
			throw new LdapConnectionException(ErrorCode.ERROR_LDAP_OPERATION,
					"err.ldap.not_initiated", new Object[] { sc.getHostName(),
							sc.getPort() });
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		if (-1 != sc.getTimeOut())
			ctls.setTimeLimit(sc.getTimeOut());
		ctls.setReturningObjFlag(true);
		try {
			NamingEnumeration<SearchResult> results = ctx.search("",
					GeneralLdapControl.ALL_OBJECT_CLASS, ctls);
			while(results.hasMore()) {
				SearchResult sr = results.next();
				rootEntries.add(sr.getName());
			}
			results.close();
		} catch (NamingException e) {
			throw new LdapConnectionException(e,
					ErrorCode.ERROR_LDAP_OPERATION,
					"err.ldap.operation.getRootEntries", new Object[] { sc
							.getHostURL() });
		}
		
		return rootEntries;
	}


	public GeneralLdapControl(ServerConfig sc) {
		this.sc = sc;
	}

}
