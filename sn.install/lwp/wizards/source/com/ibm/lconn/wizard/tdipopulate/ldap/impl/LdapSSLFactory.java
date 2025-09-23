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
package com.ibm.lconn.wizard.tdipopulate.ldap.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * A custom ssl socket factory used to connect to LDAP with SSL protocal.
 * @author Bai Jian Su (Subaij@cn.ibm.com)
 */

public class LdapSSLFactory extends SSLSocketFactory {

	private static final Logger logger = LogUtil
			.getLogger(LdapSSLFactory.class);

	// static {
	// Security.addProvider(new Provider());
	// }

	private static SSLSocketFactory sslSocketFactory = null;

	private static LdapSSLFactory fatory = null;
	private static LdapTrustManager trustManager = null;
	public LdapSSLFactory() {

		try {
			logger.log(Level.FINEST,
					"tdipopulate.finest.ldap.ssl.factory.initiation");

			String caStore = System.getProperty(Constants.KEY_CASTORE);
			String caType = System.getProperty(Constants.KEY_CATYPE);
			String caPassword = System.getProperty(Constants.KEY_CAPASSWORD);
			
			logger
					.log(
							Level.FINEST,
							"tdipopulate.finest.ldap.ssl.factory.initiation.trustStore",
							caStore);
			logger
					.log(
							Level.FINEST,
							"tdipopulate.finest.ldap.ssl.factory.initiation.trustStoreType",
							caType);

			// set SSLv3 to the interoperable protocol
			SSLContext sslcontext = SSLContext.getInstance("SSLv3", "IBMJSSE2");
			
			trustManager = new LdapTrustManager(caStore, caType, caPassword);
			
			// initiate ssl context with custom turstManger
			sslcontext.init(null,
					new TrustManager[] { trustManager },
					new java.security.SecureRandom());

			sslSocketFactory = (SSLSocketFactory) sslcontext.getSocketFactory();

		} catch (Exception ex) {
			logger.log(Level.SEVERE,
					"tdipopulate.severe.ldap.ssl.factory.initated_failed");
		}
	}

	public static synchronized SocketFactory getDefault() {
		if (null == fatory || trustManager.isNeedReload()) {
			fatory = new LdapSSLFactory();
		}
		return fatory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.ssl.SSLSocketFactory#createSocket(java.net.Socket,
	 *      java.lang.String, int, boolean)
	 */
	public Socket createSocket(Socket s, String host, int port,
			boolean autoClose) throws IOException {
		return sslSocketFactory.createSocket(s, host, port, autoClose);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.ssl.SSLSocketFactory#getDefaultCipherSuites()
	 */
	public String[] getDefaultCipherSuites() {
		return sslSocketFactory.getDefaultCipherSuites();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.ssl.SSLSocketFactory#getSupportedCipherSuites()
	 */
	public String[] getSupportedCipherSuites() {
		// TODO Auto-generated method stub
		return sslSocketFactory.getSupportedCipherSuites();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.SocketFactory#createSocket(java.lang.String, int)
	 */
	public Socket createSocket(String host, int port) throws IOException,
			UnknownHostException {
		return sslSocketFactory.createSocket(host, port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
	 */
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return sslSocketFactory.createSocket(host, port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.SocketFactory#createSocket(java.lang.String, int,
	 *      java.net.InetAddress, int)
	 */
	public Socket createSocket(String address, int port,
			InetAddress localAddress, int localPort) throws IOException,
			UnknownHostException {
		return sslSocketFactory.createSocket(address, port, localAddress,
				localPort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int,
	 *      java.net.InetAddress, int)
	 */
	public Socket createSocket(InetAddress address, int port,
			InetAddress localAddress, int localPort) throws IOException {
		return sslSocketFactory.createSocket(address, port, localAddress,
				localPort);
	}

	// private static KeyManager[] createKeyManagers(final KeyStore keystore,
	// final String password) throws KeyStoreException,
	// NoSuchAlgorithmException, UnrecoverableKeyException {
	//
	// if (keystore == null) {
	// throw new IllegalArgumentException("Keystore may not be null");
	// }
	//
	// KeyManagerFactory kmfactory = KeyManagerFactory
	// .getInstance(KeyManagerFactory.getDefaultAlgorithm());
	// kmfactory.init(keystore, password != null ? password.toCharArray()
	// : null);
	// return kmfactory.getKeyManagers();
	// }
	//
	// private static TrustManager[] createTrustManagers(final String
	// trustStore,
	// final String password) throws KeyStoreException,
	// NoSuchAlgorithmException {
	// if (trustStore == null) {
	// throw new IllegalArgumentException("trustStore may not be null");
	// }
	//
	// return new TrustManager[] { (TrustManager) new LdapTrustManager(null) };
	//
	// }

}
