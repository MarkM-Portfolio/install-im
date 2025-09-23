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

public class ServerConfig {

	private String hostName = "";

	private int port = -1;

	private String uid = "";

	private String password = "";

	private boolean ssl = false;
	
	private boolean anonymousAccess = false;

	private int timeOut = -1;

	public static final int ALWAYS_TRUST_SERVER = 0;
	public static final int DO_NOT_TRUST_SERVER = 1;

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	public boolean isAnonymousAccess() {
		return anonymousAccess;
	}
	
	public void setAnonymousAccess(boolean anonymousAccess) {
		this.anonymousAccess = anonymousAccess;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public ServerConfig(String hostName, int port, String uid, String password,
			boolean ssl, boolean anonymousAccess, int timeOut) {
		super();
		this.hostName = hostName;
		this.port = port;
		this.uid = uid;
		this.password = password;
		this.ssl = ssl;
		this.anonymousAccess = anonymousAccess;
		this.timeOut = timeOut;
	}

	public ServerConfig(String hostName, int port, String uid, String password,
			boolean ssl, boolean anonymousAccess) {
		this(hostName, port, uid, password, ssl, anonymousAccess, -1);
	}
	
	public ServerConfig(String hostName, int port, String uid, String password,
			boolean ssl) {
		this(hostName, port, uid, password, ssl, false, -1);
	}

	public String getHostURL() {
		String url = "ldap://" + getHostName() + ":" + getPort();
		return url;
	}

}
