/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.launcher;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;

import com.ibm.lconn.common.feature.LCInfo;
import com.ibm.lconn.common.file.FileUtil;
import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.operator.OperatorFactory;
import com.ibm.lconn.common.output.Output;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.migration.MigrationPrepare;

public class LCLauncher extends LogOperator {
	String resource, lc_home;
	
	public LCLauncher(){
		setOutput(System.out);
	}

	public static void main(String[] args) {
		LCLauncher launcher = new LCLauncher();
		launcher.log("Start executing LCLauncher: lc_home({0}, resource({1})", (Object[])args);
		try {
			String lc_home = args[0];
			String resource = args[1];
			launcher.setLCHome(lc_home);
			launcher.setResource(resource);
			launcher.execute();
		} catch (Exception e) {
			launcher.log(
					"Error detected in executing LCLauncher: lc_home({0}, resource({1})",
					launcher.getLCHome(), launcher.getResource());
			launcher.log(e);
		}
		launcher.log("Executing LCLauncher finished. ");
	}

	public void execute() {
		OperatorFactory operatorFactory = setLCProperties(getLCHome());
		operatorFactory.setOutput(new Output() {
			public void append(String str) throws IOException {
				appendLog(str);
			}
		});
		try {
			operatorFactory.executeFromResource(getResource());
		} catch (IOException e) {
			log(
					"Error detected in executing LCLauncher: lc_home({0}, resource({1})",
					getLCHome(), getResource());
			log(e);
		}
	}

	private OperatorFactory setLCProperties(String lc_home) {
		lc_home = FileUtil.getAbsoluteFile(lc_home).getAbsolutePath();
		LCInfo lc = new LCInfo(lc_home);
		Properties props = lc.getLCProperties();
		this.setMacroProperties(props);
		OperatorFactory operatorFactory = new OperatorFactory();
		Properties env = MigrationPrepare.buildEnv();
		ObjectUtil.copyProperties(env, props, "", "");
		operatorFactory.setMacroProperties(props);
		return operatorFactory;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getLCHome() {
		return lc_home;
	}

	public void setLCHome(String lc_home) {
		this.lc_home = lc_home;
	}

	private void appendLog(String msg) {
		System.out.append(msg);
		System.out.flush();
		String str = msg.toLowerCase();
		if (str.indexOf("fail") != -1 || str.indexOf("error") != -1
				|| str.indexOf("exception") != -1) {
			String currentTask = getCurrentTask();
			String errMsg = MessageFormat
					.format(
							"There''s some error during task ({0}), the message is: {1}",
							currentTask, msg);
			errorList.add(errMsg);
		}
	}

	private ArrayList<String> errorList = new ArrayList<String>();

}
