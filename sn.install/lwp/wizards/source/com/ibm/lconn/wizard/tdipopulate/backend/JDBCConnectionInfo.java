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

package com.ibm.lconn.wizard.tdipopulate.backend;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class JDBCConnectionInfo {
	private String hostName, port, dbName, username, password, jdbcLibPath, dbType;
	private Map<String, String> contentStore = new HashMap<String, String>();

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getJdbcLibPath() {
		return jdbcLibPath;
	}

	public void setJdbcLibPath(String jdbcLibPath) {
		this.jdbcLibPath = jdbcLibPath;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContentStore(String feature) {
		return contentStore.get(feature);
	}

	public void setContentStore(String feature, String value) {
		if (value != null)
			contentStore.put(feature, value);
	}
}
