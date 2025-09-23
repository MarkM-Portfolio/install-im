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
package com.ibm.lconn.wizard.common.logging;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.msg.ErrorMsg;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 *
 */
public class LogUtil {
	private static final String MESSAGE_RESOUCEBUNDLE = "com.ibm.lconn.update.msg.messages";
	private static final String LOG_NAME = "wizard";
	private static final int LIMIT = 1024*1024;
	private static final int COUNT = 10;
	private LogUtil() {}
	
    static {
    	String logLevel = System.getProperty(Constants.LOG_LEVEL);
    	Level level = Level.INFO;
    	try {
    		if(null != logLevel) {
    			level = Level.parse(logLevel);
    		}
    	} catch(Exception e) {
    		// ignore
    	}

    	// won't use the settings in logging.properties
        LogManager.getLogManager().reset();
        Logger defaultLogger = Logger.getLogger("");
        defaultLogger.setLevel(level);
        File logDir = new File(Constants.LOG_ROOT);
        if(!logDir.exists()) {
        	logDir.mkdirs();  
        }
        
        try {
        	Handler fh = new FileHandler(Constants.LOG_ROOT + Constants.FS + LOG_NAME + "_%g.log", 
        			LIMIT, COUNT, false);
        	fh.setEncoding("utf-8");
        	fh.setLevel(level);
        	fh.setFormatter(new LogFormatter());
        	defaultLogger.addHandler(fh);
        } catch(Exception e) {
            Handler h = new ConsoleHandler();
            h.setLevel(level);
            h.setFormatter(new LogFormatter());
            defaultLogger.addHandler(h);
        }
    }
	
	public static Logger getLogger(Class<? extends Object> cl) {
		Logger logger = Logger.getLogger(cl.toString(),MESSAGE_RESOUCEBUNDLE);
		return logger;
	}
	
	public static void log(Class<? extends Object> cl, Level level, String msgKey){
		String msg = ErrorMsg.getString(msgKey+".msg");
		getLogger(cl).log(level, msg);
	}
	
	public static void log(Class<? extends Object> cl, Level level, String msgKey, Throwable thrown){
		String msg = ErrorMsg.getString(msgKey+".msg");
		getLogger(cl).log(level, msg, thrown);
	}
}
