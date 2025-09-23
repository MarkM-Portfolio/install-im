/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.logging;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.ibm.lconn.common.config.CommonConstants;
import com.ibm.lconn.common.msg.MessageUtil;
import com.ibm.lconn.common.util.ObjectUtil;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class LogUtil {
	private static final String MESSAGE_RESOUCEBUNDLE = "com.ibm.lconn.common.logging.LoggingMessages";
	private static final String LOG_NAME = MessageUtil
			.getSetting("log.file.name");
	private static final int LIMIT = 1024 * 1024;
	private static final int COUNT = 10;
	public static boolean LOG_ENABLED = ObjectUtil.isTrue(MessageUtil
			.getSetting("log.enabled"));

	private LogUtil() {
	}

	static {
		String logLevel = System.getProperty(CommonConstants.LOG_LEVEL);
		Level level = Level.INFO;
		try {
			if (null != logLevel) {
				level = Level.parse(logLevel);
			}
		} catch (Exception e) {
			// ignore
		}

		// won't use the settings in logging.properties
		LogManager.getLogManager().reset();
		Logger defaultLogger = Logger.getLogger("");
		defaultLogger.setLevel(level);

		try {
			if (!LOG_ENABLED) {
				Handler h = new ConsoleHandler();
				h.setLevel(level);
				h.setFormatter(new LogFormatter());
				defaultLogger.addHandler(h);
			} else {
				File logDir = new File(CommonConstants.LOG_ROOT);
				if (!logDir.exists()) {
					logDir.mkdirs();
				}
				Handler fh = new FileHandler(CommonConstants.LOG_ROOT
						+ CommonConstants.FS + LOG_NAME + "_%g.log", LIMIT,
						COUNT, false);
				fh.setLevel(level);
				fh.setFormatter(new LogFormatter());
				defaultLogger.addHandler(fh);
			}
		} catch (Exception e) {
			Handler h = new ConsoleHandler();
			h.setLevel(level);
			h.setFormatter(new LogFormatter());
			defaultLogger.addHandler(h);
		}
	}

	public static Logger getLogger(Class<? extends Object> cl) {
		Logger logger = Logger.getLogger(cl.toString(), MESSAGE_RESOUCEBUNDLE);
		return logger;
	}

	// public static void log(Class<? extends Object> cl, Level level, String
	// msgKey){
	// String msg = ErrorMsg.getString(msgKey+".msg");
	// getLogger(cl).log(level, msg);
	// }
	//	
	// public static void log(Class<? extends Object> cl, Level level, String
	// msgKey, Throwable thrown){
	// String msg = ErrorMsg.getString(msgKey+".msg");
	// getLogger(cl).log(level, msg, thrown);
	// }
}
