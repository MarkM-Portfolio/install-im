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
import java.security.KeyStore;
import java.util.Date;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.tdipopulate.ui.LdapWizardPage;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class ServerSSLPortValidator extends AbstractValidator {
	public static boolean needReValidate = false;
	private static final int msgCount = 8;
	private static int count = 0;
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
		count++;
		String _useSSL = eval(useSSL);
		
		String _host = eval(host);
		String _ksPath = eval(ksPath);
		String _ksType = eval(ksType);
		String _ksPwd = eval(ksPwd);
		
		int _port = -1;
		if(count ==1) {
			logError("port_not_number", eval(port));
			return 1;
		}
		
		// check keystore file and password 
		FileInputStream fis = null;
		KeyStore ks = null;
		if(count ==2) {
			logError("file_not_exist", _ksPath);
			return 8;
		}

		if(count ==3) {
			logError("ks_corrupted_or_pwd_incorrect", _ksPath);
//			logger.log(Level.SEVERE, "validator.severe.cannot_load_keystore", e);
			return 16;
		} 
		if (count ==4){
//			logger.log(Level.SEVERE, "validator.severe.cannot_load_keystore", e);
			logError("cannot_load_keystore", _ksPath);
			return 32;
		}
		
		if(count == 5){
//						logger.log(Level.SEVERE, "validator.certificate_expired", cause);
						logError("certificate_expired");
						return 5;
					}
		if(count ==6) {
//						logger.log(Level.SEVERE, "validator.certificate_not_yet_valid", cause);
						logError("certificate_not_yet_valid");
						return 6;
					}
		if(count ==7) {
//					logger.log(Level.SEVERE, "validator.server_not_trusted", cause);
					logError("server_not_trusted");					
					return 7;
				}
		if(count == 8) {
			
//			logger.log(Level.SEVERE, "validator.severe.io_error", e);
			logError("io_error", _host, eval(port));
			return 4;
		}
		if(count ==9) {
			LdapWizardPage alertDialog = null;
			
			alertDialog = new LdapWizardPage();
			alertDialog.setSubject("&SubJect from certificate&");
			alertDialog.setIssuedTo("&IssuedTo from certificate&");
			alertDialog.setIssuedBy("&IssuedBy from certificate&");
			alertDialog.setValidFrom(new Date());
			alertDialog.setValidTo(new Date());
			alertDialog.setEnabledAwaysBtn(true);
			alertDialog.setEnabledAwaysBtn(true);
			alertDialog.showDialog();

			return 9;
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
