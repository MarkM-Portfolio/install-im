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
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.logging.LogUtil;

public class KeyStorePasswordValidator extends AbstractValidator {
	private static final Logger logger = LogUtil.getLogger(KeyStorePasswordValidator.class);
	
	private String ksPath, ksType, ksPwd, enabled;
	public KeyStorePasswordValidator(String ksPath, String ksType, String ksPwd, String enabled) {
		this.ksPath = ksPath;
		this.ksType = ksType;
		this.ksPwd = ksPwd;
		this.enabled = enabled;
	}
	
	public KeyStorePasswordValidator(String ksPath, String ksType, String ksPwd) {
		this(ksPath, ksType, ksPwd, null);
	}

	public int validate() {
		if(enabled == null || ! eval(enabled).equals(Constants.BOOL_TRUE)) {
			return 0;
		}
		
		String _ksPath = eval(ksPath);
		String _ksType = eval(ksType);
		String _ksPwd = eval(ksPwd);
		
		FileInputStream fis = null;
		KeyStore ks = null;

		// read keystore file
		try {
			fis = new FileInputStream(_ksPath);
		} catch (FileNotFoundException e1) {
			logError("file_not_exist", _ksPath);
			return 1;
		}

		// try to initiate key store
		try {
			ks = KeyStore.getInstance(_ksType);
			ks.load(fis, _ksPwd.toCharArray());
		} catch(IOException e) {
			logError("ks_corrupted_or_pwd_incorrect", _ksPath);
			logger.log(Level.SEVERE, "validator.severe.cannot_load_keystore", e);
			return 2;
		} catch(Exception e) {
			logger.log(Level.SEVERE, "validator.severe.cannot_load_keystore", e);
			logError("cannot_load_keystore", _ksPath);
			return 4;
		}
		finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		return 0;
	}

}
