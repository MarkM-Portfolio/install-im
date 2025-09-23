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
package com.ibm.lconn.wizard.launcher;

import java.io.File;
import java.text.MessageFormat;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;

public class ConsoleInstallerParser {
	private boolean isComplete = false;
	String[] helpStrs;
	String[] validParams;
	String[] usageStrs;
	String wizardId;
	String[] args;
	int argNo;
	private boolean needExit;

	public ConsoleInstallerParser(String wizardId) {
		TestDataOffer.setLocale();
		this.wizardId = wizardId;
		String validParamStr = MessageUtil.getSetting(wizardId,
				PARAM_ACCEPTABLE).toLowerCase();
		String helpMsgStr = MessageUtil.getSetting(wizardId, "console.parameters.helpmsg").toLowerCase();
		String usageMsgStr = MessageUtil.getSetting(wizardId, "console.parameters.usagemsg").toLowerCase();
		validParams = Util.delimStr(validParamStr);
		helpStrs = Util.delimStr(helpMsgStr);
		usageStrs = Util.delimStr(usageMsgStr);
	}

	private static final String PARAM_ACCEPTABLE = "console.parameters.acceptable";

	public int parse(String[] args) {
		this.argNo = 0;
		this.args = args;
		if(args==null || args.length==0 || CommonHelper.isEmpty(args[0])){
			printUsage(wizardId);
			exitAfterParse();
			return Constants.EXIT_STATUS_NORMAL;
		}
		String current = read();
		if(Util.indexOf(helpStrs, current.toLowerCase())!=-1){
			printHelp(wizardId);
			exitAfterParse();
			return Constants.EXIT_STATUS_NORMAL;
		}
		if(Util.indexOf(usageStrs, current.toLowerCase())!=-1){
			printUsage(wizardId);
			exitAfterParse();
			return Constants.EXIT_STATUS_NORMAL;
		}
		String value = current;
		String valueKey = Constants.INPUT_PARAM_PROP_FILE;
		File propFile = new File(value);
		int propsLoadResult = loadPropertiesFile(propFile);
		if(propsLoadResult!=0){
			String msg = "Properties file load error, make sure the properties file: {0} exist and has correct format. \n";
			String absolutePath = propFile.getAbsolutePath();
			msg = MessageFormat.format(msg, (Object)absolutePath);
			output(msg);
			exitAfterParse();
			return Constants.EXIT_STATUS_PROPERTY_FILE_ERROR;
		}
		try {
			while (!isComplete && (argNo < args.length)) {
				current = read();
				if('-'!=current.charAt(0)){
					printUsage(wizardId);
					exitAfterParse();
					return Constants.EXIT_STATUS_PARAMETER_ERROR;
				}
				current = current.substring(1).toLowerCase(); 	//remove '-'
				value = read();
				
				if (Util.indexOf(validParams, current) != -1) {
					valueKey = MessageUtil.getSetting(wizardId,
							"console.parameters.mapping", current);
					DataPool.setValue(wizardId, valueKey, value);
				}else{
					printUsage(wizardId);
					exitAfterParse();
					return Constants.EXIT_STATUS_PARAMETER_ERROR;
				}
			}
		} catch (Exception e) {
			printUsage(wizardId);
			exitAfterParse();
			return Constants.EXIT_STATUS_OTHER;
		}
		return Constants.EXIT_STATUS_NORMAL;
	}

	private int loadPropertiesFile(File propFile) {
		if(!propFile.exists()){
			return 2;
		}else{
			try{
				DefaultWizardDataLoader.loadProperties(wizardId, wizardId+".console", propFile.getAbsolutePath());
			}catch (Exception e) {
				return 3;
			}
		}
		return 0;
	}

	private void exitAfterParse() {
		this.needExit = true;
	}

	private String read() {
		return args[argNo++].trim();
	}

	public static void printUsage(String wizardId) {
		output(MessageUtil.getMsg(wizardId, "console.usage"));
	}

	public static void printHelp(String wizardId) {
		output(MessageUtil.getMsg(wizardId, "console.help"));
	}

	private static void output(String msg) {
		System.out.print(msg);
	}
	
	public boolean needExit(){
		return this.needExit;
	}
	
	public static void main(String[] args) {
		ConsoleInstallerParser parser = new ConsoleInstallerParser(Constants.WIZARD_ID_TDIPOPULATE);
		System.out.println("-----------: " + parser.parse("".split(",")));
		System.out.println("-----------: " + parser.parse("xxx".split(",")));
		System.out.println("-----------: " + parser.parse("--help".split(",")));
		System.out.println("-----------: " + parser.parse("/?".split(",")));
		System.out.println("-----------: " + parser.parse("-usage".split(",")));
		parser.parse("".split(","));
		Assert.isTrue(true);
	}

}
