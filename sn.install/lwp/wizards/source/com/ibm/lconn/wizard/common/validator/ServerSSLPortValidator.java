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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.tdipopulate.ldap.impl.LdapSSLFactory;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class ServerSSLPortValidator extends AbstractValidator {
	public static boolean needReValidate = false;
	private static final Logger logger = LogUtil.getLogger(ServerSSLPortValidator.class);
	
	private String host,port,ksPath, ksPwd, ksType, useSSL;
	// fake ldap bind message cn=root/passw0rd
	public static final byte[] testData = new byte[] {
		0x30,0x1B,0x02,0x01,0x01,0x60,0x16,0x02,
		0x01,0x03,0x04,0x07,0x63,0x6E,0x3D,0x72,
		0x6F,0x6F,0x74,-128,0x08,0x70,0x61,0x73,
		0x73,0x77,0x30,0x72,0x64
	};
	
	public ServerSSLPortValidator(String host, String port, String ksPath, String ksType, String ksPwd, String useSSL) {
		this.host = host;
		this.port = port;
		this.ksPath = ksPath;
		this.ksType = ksType;
		this.ksPwd = ksPwd;
		this.useSSL = useSSL;
	}

	public int validate() {
		String _useSSL = eval(useSSL);
		if(!Constants.BOOL_TRUE.equals(_useSSL)) {
			// no validation is required
			return 0;
		}
		
		String _host = eval(host);
		String _ksPath = eval(ksPath);
		String _ksType = eval(ksType);
		String _ksPwd = eval(ksPwd);
		
		int _port = -1;
		try {
			_port = Integer.valueOf(eval(port));
		} catch (NumberFormatException e) {
			logError("port_not_number", eval(port));
			return 1;
		}
		
		// check keystore file and password 
		FileInputStream fis = null;
		KeyStore ks = null;
		try {
			fis = new FileInputStream(_ksPath);
		} catch (FileNotFoundException e1) {
			logError("file_not_exist", _ksPath);
			return 8;
		}

		// try to initiate key store
		try {
			ks = KeyStore.getInstance(_ksType);
			ks.load(fis, _ksPwd.toCharArray());
		} catch(IOException e) {
			logError("ks_corrupted_or_pwd_incorrect", _ksPath);
			logger.log(Level.SEVERE, "validator.severe.cannot_load_keystore", e);
			return 16;
		} catch(Exception e) {
			logger.log(Level.SEVERE, "validator.severe.cannot_load_keystore", e);
			logError("cannot_load_keystore", _ksPath);
			return 32;
		}
		finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		// connect to SSL port
		try {
			System.setProperty(Constants.KEY_CASTORE, _ksPath);
			System.setProperty(Constants.KEY_CATYPE, _ksType);
			System.setProperty(Constants.KEY_CAPASSWORD, _ksPwd);
			Socket so = LdapSSLFactory.getDefault().createSocket(_host, _port);

			//Revisit: whether it is the standard way
			InputStream is = so.getInputStream();
			PrintStream ps = new PrintStream(so.getOutputStream());
			ps.write(testData);
			is.read();
			ps.close();
			is.close();			
			so.close();
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "validator.severe.unkown_host", e);
			logError("unknow_host", _host);
			return 2;
		} catch (IOException e) {
			for(Throwable t=e, cause=e.getCause(); cause!=null && cause!=t; t=cause, cause=cause.getCause()) {
				if(cause instanceof CertificateException) {
					if(cause instanceof CertificateExpiredException) {
						logger.log(Level.SEVERE, "validator.certificate_expired", cause);
						logError("certificate_expired");
						return 5;
					}
					if(cause instanceof CertificateNotYetValidException) {
						logger.log(Level.SEVERE, "validator.certificate_not_yet_valid", cause);
						logError("certificate_not_yet_valid");
						return 6;
					}
					logger.log(Level.SEVERE, "validator.server_not_trusted", cause);
					logError("server_not_trusted");					
					return 7;
				}
			}
			logger.log(Level.SEVERE, "validator.severe.io_error", e);
			logError("io_error", _host, eval(port));
			if (needReValidate) {
				needReValidate = false;
				return Constants.RE_VALIDATE;
			}
			return 4;
		}
		
		return 0;
	}
	
//	public static void main(String[] args) {
//		String WID = "123";
//		DataPool.setValue(WID, "host", "9.33.133.99");
//		DataPool.setValue(WID, "port", "636");
//		DataPool.setValue(WID, "ksPath", "/opt/ibm/TDI/V6.1/jvm/jre/lib/security/cacerts");
//		DataPool.setValue(WID, "ksType", "JKS");
//		DataPool.setValue(WID, "ksPwd", "changeit");
//		DataPool.setValue(WID, "useSSL", Constants.BOOL_TRUE);
//		System.setProperty(Constants.LCONN_WIZARD_PROP, WID);
//		ServerSSLPortValidator v = new ServerSSLPortValidator("host", "port", "ksPath", "ksType", "ksPwd", "useSSL");
//		System.out.println(v.validate());
//	}

}
