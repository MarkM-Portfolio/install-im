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

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class ServerPortValidator extends AbstractValidator {
	private static final int msgCount = 3;
	private static int count = 0;
	public static final Logger logger = LogUtil
			.getLogger(ServerPortValidator.class);
	public static final int SOCKET_TIMEOUT = 15000;
	// fake ldap bind message
	public static final byte[] testData = new byte[] {
		0x30,0x1B,0x02,0x01,0x01,0x60,0x16,0x02,
		0x01,0x03,0x04,0x07,0x63,0x6E,0x3D,0x72,
		0x6F,0x6F,0x74,-128,0x08,0x70,0x61,0x73,
		0x73,0x77,0x30,0x72,0x64
	};
	private String host, port;

	public ServerPortValidator(String host, String port) {
		this.host = host;
		this.port = port;
	}

	public int validate() {
		count++;
		String _host = eval(host);
		int _port = -1;
		if(count == 1){
			logError("port_not_number", eval(port));
			return 1;
		}

		if(count ==2) {
			logger.log(Level.SEVERE, "validator.severe.unkown_host", new Exception());
			logError("unknow_host", _host);
			return 2;
		} 
		if (count == 3){
			logger.log(Level.SEVERE, "validator.severe.io_error", new Exception());
			logError("io_error", _host, eval(port));
			return 4;
		}

		return 0;
	}

	public static void main(String[] args) {
		String WID = "123";
		DataPool.setValue(WID, "host", "9.186.10.126");
		DataPool.setValue(WID, "port", "638");
		DataPool.setValue(WID, "ksPath", "/opt/ibm/TDI/V6.1/jvm/jre/lib/security/cacerts");
		DataPool.setValue(WID, "ksType", "JKS");
		DataPool.setValue(WID, "ksPwd", "changeit");
		System.setProperty(Constants.LCONN_WIZARD_PROP, WID);
		ServerPortValidator v = new ServerPortValidator("host", "port");
		System.out.println(v.validate());
	}
}
