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
package com.ibm.lconn.wizard.common.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandResultInfo {
	public static final int COMMAND_INTERRUPT = -1;
	public static final int COMMAND_SUCCEED = 0;
	public static final int COMMAND_WARNING = 1;
	public static final int COMMAND_ERROR = 2;
	
	private int execState;
	private String exitMessage;
	private String logPath;
	private List<String> features = new ArrayList<String>();
	private String blockedFeature;
	
	public CommandResultInfo() {
		
	}
	
	public void setExecState(int state) {
		this.execState = state;
	}
	
	public int getExecState() {
		return this.execState;
	}
	
	public String getExitMessage() {
		return this.exitMessage;
	}
	
	public void setExitMessage(String message) {
		this.exitMessage = message;
	}
	
	public void setLogPath(String logPath) {
		this.logPath = new File(logPath).getAbsolutePath();
	}
	
	public String getLogPath() {
		return this.logPath;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public String getBlockedFeature() {
		return blockedFeature;
	}

	public void setBlockedFeature(String blockedFeature) {
		this.blockedFeature = blockedFeature;
	}
}
