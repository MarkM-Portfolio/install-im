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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SessionPool {
	private static Map<String, Session> sessionMap;
	public final static String SESSION_ROOT = "session";

	public static Session getSession(String sessionID) {
		if (sessionMap == null) {
			initializeSessionMap();
		}

		Session session = sessionMap.get(sessionID);

		if (session == null)
			session = initializeSession(sessionID);

		return session;
	}

	private static synchronized void initializeSessionMap() {
		sessionMap = new HashMap<String, Session>();
		File root = new File(SESSION_ROOT);

		if (!root.exists()) {
			root.mkdir();
		}
	}

	public static void clean() {
		if (sessionMap != null) {
			Iterator<String> it = sessionMap.keySet().iterator();
			while (it.hasNext())
				sessionMap.get(it.next()).clean();
			sessionMap = null;
		}
	}

	public static synchronized void destroy() {
		File root = new File(SESSION_ROOT);
		if (root.exists()) {
			root.delete();
		}
	}

	private static synchronized Session initializeSession(String sessionID) {
		return new Session(sessionID);
	}
}
