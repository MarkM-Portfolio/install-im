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
package com.ibm.lconn.wizard.common.session;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.property.Property;
import com.ibm.lconn.wizard.common.property.PropertyLoader;

public class Session {
	private static final Logger logger = LogUtil.getLogger(Session.class);
	private final String DEFAULT_FILE = "main.properties";
	private final String PATH;
	private final String FILE_PATH_SEPERATOR = System
			.getProperty("file.separator");
	private Property property;
	private final String sessionID;

	public Session(String sessionID) {
		this.sessionID = sessionID;
		if (sessionID == null) {
			PATH = SessionPool.SESSION_ROOT + FILE_PATH_SEPERATOR + "default";
		} else {
			PATH = SessionPool.SESSION_ROOT + FILE_PATH_SEPERATOR + sessionID;
		}
		File path = new File(PATH);
		if (!path.exists()) {
			path.mkdir();
		}
		File propFile = new File(PATH + FILE_PATH_SEPERATOR + DEFAULT_FILE);
		if (!propFile.exists()) {
			try {
				propFile.createNewFile();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "commom.property.create.file.exception", e);
			}
		}
		property = PropertyLoader.load(PATH + FILE_PATH_SEPERATOR
				+ DEFAULT_FILE);
	}

	public void clean() {
		property.clean();
	}

	public String getSessionID() {
		return this.sessionID;
	}

	public void setValue(String key, String value) {
		property.setProperty(key, value);
	}

	public void setValue(Map<String, String> values) {
		property.setProperty(values);
	}

	public String getValue(String key) {
		return property.getProperty(key);
	}

	public Map<String, String> getValue(String[] keys) {
		return property.getProperty(keys);
	}

	public Map<String, String> getValue(List<String> keys) {
		String[] keysStr = (String[]) keys.toArray(new String[0]);
		return getValue(keysStr);
	}
}
