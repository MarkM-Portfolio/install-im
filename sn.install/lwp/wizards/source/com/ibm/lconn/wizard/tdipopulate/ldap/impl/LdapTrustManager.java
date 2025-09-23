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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.ibm.lconn.wizard.common.exceptions.ErrorCode;
import com.ibm.lconn.wizard.common.exceptions.NotSupportAlgorithmException;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.validator.ServerSSLPortValidator;
import com.ibm.lconn.wizard.tdipopulate.ldap.ServerConfig;
import com.ibm.lconn.wizard.tdipopulate.ui.LdapWizardPage;

//import javax.net.ssl.X509TrustManager;

/**
 * A custom trust manager for custom ldap connection
 * 
 * @author Bai Jian Su (Subaij@cn.ibm.com)
 */
public class LdapTrustManager implements X509TrustManager {

	private static final Logger logger = LogUtil.getLogger(LdapTrustManager.class);

	private X509TrustManager standardTrustManager = null;
	private KeyStore ks = null;
	private String caStore = null;
	private String caType = null;
	private String caStorePassword = null;
	private boolean canLoadKeyStore = true;
	private boolean needReload = false;

	/**
	 * confirm user with the LDAP server's certificate
	 * 
	 * @param certificate
	 * @param canWriteKeyStore
	 * @return
	 */
	protected int serverTrustSelection(X509Certificate certificate) {

		// user confirm dialog
		LdapWizardPage alertDialog = null;

		alertDialog = new LdapWizardPage();
		alertDialog.setSubject(certificate.getSubjectX500Principal().getName());
		alertDialog.setIssuedTo(certificate.getSubjectX500Principal().getName());
		alertDialog.setIssuedBy(certificate.getIssuerX500Principal().getName());
		alertDialog.setValidFrom(certificate.getNotBefore());
		alertDialog.setValidTo(certificate.getNotAfter());
		alertDialog.setEnabledAwaysBtn(canLoadKeyStore);
		alertDialog.setEnabledAwaysBtn(true);

		int choice = alertDialog.showDialog();

		return choice;
	}

	protected boolean loadTrustStore() {

		FileInputStream fis = null;

		boolean flag = true;
		// read keystore file
		try {
			fis = new FileInputStream(caStore);
		} catch (FileNotFoundException e1) {

			// Do not throw the file not found exception, trust manager will
			// assume there is no certificate in keystore.
			logger.log(Level.WARNING, "tdipopulate.warning.ldap.ssl.castore_not_found", caStore);
		}

		// try to initiate key store
		try {
			ks = KeyStore.getInstance(caType);
			ks.load(fis, caStorePassword.toCharArray());
		} catch (Exception e) {
			flag = false;
			if (e instanceof IOException) {
				// set flag as false.
				logger.log(Level.SEVERE, "tdipopulate.severe.ldap.ssl.keystore_corrupted");
			} else
				logger.log(Level.SEVERE, "tdipopulate.severe.ldap.ssl.keystore_init_failed");
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "tdipopulate.severe.ldap.ssl.file_error");
			}
		}
		return flag;
	}

	private void saveTrustStore(X509Certificate certificate) {

		FileOutputStream fos = null;

		try {

			File file = new File(caStore);
			// create the directory if neccessary
			String parent = file.getParent();
			File dir = new File(parent);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!file.exists()) {
				logger.log(Level.INFO, "tdipopulate.severe.ldap.ssl.create_keystore_file", file.toString());
				file.createNewFile();
			}
			// open the keyStore
			fos = new FileOutputStream(file);

			String aliasID = certificate.getSubjectDN().getName() + "_" + certificate.getSerialNumber();

			ks.setCertificateEntry(aliasID, certificate);

			ks.store(fos, caStorePassword.toCharArray());

		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				logger.log(Level.SEVERE, "tdipopulate.severe.ldap.ssl.file_error");
			} else
				logger.log(Level.SEVERE, "tdipopulate.severe.ldap.ssl.keystore_write_failed");
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "tdipopulate.severe.ldap.ssl.file_error");
				}
			}
		}

	}

	/**
	 * Keystore initiation
	 * 
	 * @param keyStore
	 * @param keyStoreType
	 * @param password
	 */
	protected void init(String keyStore, String keyStoreType, String password) {

		// fill the field
		this.caStore = keyStore;
		this.caType = keyStoreType;
		this.caStorePassword = password;

		if (null == caType || caType.trim().equals("")) {
			// set JKS as default Keystore Type
			caType = "JKS";
		}

		// load trust Store
		this.canLoadKeyStore = loadTrustStore();

	}

	public LdapTrustManager(String keyStore, String keyStoreType, String password) throws NotSupportAlgorithmException, KeyStoreException {
		// super();
		try {
			this.init(keyStore, keyStoreType, password);
			// alertDialog = new LdapSecurityAlertDialog();
			// get default algorithm
			String alg = KeyManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory factory = TrustManagerFactory.getInstance(alg);
			factory.init(ks);
			TrustManager[] trustmanagers = factory.getTrustManagers();
			if (trustmanagers.length == 0) {
				throw new NotSupportAlgorithmException(ErrorCode.ERROR_LDAP_SSL_NOTSUPPORT_IBMX509, "err.ldap.ssl.ibmx509_notsupported");
			}
			for (int i = 0; i < trustmanagers.length; i++) {
				if (trustmanagers[i] instanceof X509TrustManager) {
					this.standardTrustManager = (X509TrustManager) trustmanagers[i];
					return;
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			throw new NotSupportAlgorithmException(e, ErrorCode.ERROR_LDAP_SSL_NOTSUPPORT_IBMX509, "err.ldap.ssl.ibmx509_notsupported");
		}
	}

	/**
	 * check wheather the certificate chain is trusted on client side.
	 */
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub

		logger.log(Level.INFO, "tdipopulate.info.ldap.ssl.certificate.client");

		standardTrustManager.checkClientTrusted(chain, authType);

	}

	/**
	 * check wheather the certificate chain is trusted.
	 */
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		logger.log(Level.INFO, "tdipopulate.info.ldap.ssl.certificate.server");

		try {
			standardTrustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException e) {
			// if (e instanceof CertificateExpiredException) {
			// logger.log(Level.SEVERE,
			// "tdipopulate.severe.ldap.ssl.certificate_expired", e);
			// throw e;
			// } else if (e instanceof CertificateNotYetValidException) {
			// logger.log(Level.SEVERE,
			// "tdipopulate.severe.ldap.ssl.certificate_not_yet_valid", e);
			// throw e;
			// }
			logger.log(Level.WARNING, "tdipopulate.warning.ldap.ssl.cert_not_trusted", e);
			// check the chain
			if (chain.length == 0) {
				logger.log(Level.SEVERE, "tdipopulate.severe.ldap.ssl.ca_chain_is_null");
				throw e;
			}
			// else ask user to confirm with the server's CA certificate
			else {
				logger.log(Level.FINEST, "tdipopulation.finest.ldap.ssl.dump_cert_chain");

				// dumping certificate chain
				for (int i = 0; i < chain.length; i++) {
					logger.log(Level.FINEST, "tdipopulation.finest.ldap.ssl.dump_cert", chain[i]);
				}
				// checking the validity of certificate in the chain
				for (int i = 0; i < chain.length; i++) {
					X509Certificate cert = chain[i];
					cert.checkValidity();
				}

				// get the root certificate
				X509Certificate ca = chain[chain.length - 1];
				logger.log(Level.INFO, "tdipopulate.info.ldap.ssl.certificate.dn", ca.getSubjectDN());

				int i = serverTrustSelection(ca);
				switch (i) {
				// user has chosen always trust the server, so store the
				// certificate to keystore
				case ServerConfig.ALWAYS_TRUST_SERVER:
					this.saveTrustStore(ca);
					needReload = true;
					ServerSSLPortValidator.needReValidate = true;
					break;
				// trust for one time, do nothing except passing the trust
				// server process
				// do not trust the server, throws a CertificateException to
				// close the socket
				case ServerConfig.DO_NOT_TRUST_SERVER:
					throw new CertificateException("Server is not trusted");

					// this condition is for the user press close button.
				default:
					throw new CertificateException("Server is not trusted");
				}
			}
		}
	}

	public X509Certificate[] getAcceptedIssuers() {
		// get accepted issures.
		logger.log(Level.FINEST, "tdipopulate.finest.ldap.ssl.getAcceptedIssues");
		return standardTrustManager.getAcceptedIssuers();
	}

	public boolean isNeedReload() {
		return needReload;
	}

	public void setNeedReload(boolean needReload) {
		this.needReload = needReload;
	}

}
