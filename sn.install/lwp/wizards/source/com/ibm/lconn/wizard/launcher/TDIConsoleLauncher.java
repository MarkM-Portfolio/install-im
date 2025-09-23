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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Entry;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.interfaces.PageController;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.data.WizardData;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;
import com.ibm.lconn.wizard.common.validator.ValidationMessage;
import com.ibm.lconn.wizard.tdipopulate.backend.Task;

public class TDIConsoleLauncher {
	public static void main(String[] args) {
		TestDataOffer.setLocale();
		ConsoleInstallerParser parser = new ConsoleInstallerParser(Constants.WIZARD_ID_TDIPOPULATE);
		int returnCode = parser.parse(args);
		if(parser.needExit()){
			exit(returnCode);
		}
//		TDIPopulationWizard wizard = new TDIPopulationWizard(Constants.WIZARD_ID_TDIPOPULATE);
		WizardData wizardData = new WizardData(Constants.WIZARD_ID_TDIPOPULATE);
		for (String pageId : wizardData.getPages()) {
			new WizardPageData(wizardData.getId(), pageId);
		}
		setWizard(wizardData);
	}
	
	/**
	 * Set the controllers of the wizard here. 
	 * {@link PageController}
	 * @param wizardData 
	 * 
	 * @param wizard
	 */
	private static void setWizard(WizardData wizardData) {
		DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE, Constants.WIZARD_LAUNCH_MODE, Constants.LAUNCH_MODE_CONSOLE);
		PageController pageController = wizardData.getPageController();
		String wizardId = Constants.WIZARD_ID_TDIPOPULATE;
		String consolePageSequenceStr = DefaultWizardDataLoader.loadConsolePageSequence(wizardId);
		String[] consolePageSequence = Util.delimStr(consolePageSequenceStr);
		int pageNo = 0;
		
		output("The user inputs are as following: \n\n");
		
		output(getSummaryString(Constants.WIZARD_ID_TDIPOPULATE));
		
		output("\n Now validation user input... (This will take a few minutes to finish depending on the network status)\n\n");
		
		while(pageNo < consolePageSequence.length){
			pageController.performAction(consolePageSequence[pageNo++], Constants.WIZARD_ACTION_NEXT);
			List<ValidationMessage> messages = pageController.getMessages();
			if(!messages.isEmpty()){
				ValidationMessage validationMessage = messages.get(0);
				validationMessage.getMessageType();
				output(validationMessage.getTitle()+"\n");
				output(validationMessage.getMessge()+"\n\n");
			    exit(Constants.EXIT_STATUS_VALIDATION_ERROR);
			}else{
				continue;
			}
		}
		output("THe final input are as following: \n\n");
		output(getSummaryString(Constants.WIZARD_ID_TDIPOPULATE));
		
		output("Starting to execution the task, this will take a few minutes to finish ... ");
		CommandResultInfo resultInfo = execute(wizardData);
		output((resultInfo==null? "Internal error\n\n": resultInfo.getExitMessage()+"\n\n"));
		int exitStatus = (resultInfo==null? -1: resultInfo.getExecState());
		output("Exit with code: "+exitStatus);
		exit(exitStatus);
	}

	private static CommandResultInfo execute(WizardData wizardData) {
		WizardData data = wizardData;
		try {
			Properties mappingData = DefaultWizardDataLoader.loadMappingData();

			List<Entry<String, String>> userInputData = DefaultWizardDataLoader
					.collectTaskInput(data.getId(), Constants.TDI_DEFAULT_TASK);
			userInputData.add(new Entry<String, String>("populate.task.do",
					Constants.BOOL_TRUE));
			// mappingData.list(System.out);

			// if(true)throw new Exception();
			CommandResultInfo runCommand = Task.runCommand(
					DefaultWizardDataLoader.list2Properties(userInputData),
					mappingData);
			return runCommand;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getSummaryString(String wizardId) {
		List<com.ibm.lconn.wizard.common.Entry<String, String>> userInputData = DefaultWizardDataLoader
				.collectTaskInput(wizardId, wizardId+".console"
						+ ".summary");
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, String>> iterator = userInputData
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			if(!Constants.BOOL_TRUE.equals(DataPool.getValue(wizardId, Constants.INPUT_TDI_LDAP_USE_SSL))){
				if("ssl.type".equals(entry.getKey())) continue;
				if("ssl.keyStore".equals(entry.getKey())) continue;
			}
			String label = MessageUtil.getLabel(entry.getKey());
			if (CommonHelper.isEmpty(label))
				label = entry.getKey();
			sb.append(label + ": "
					+ MessageUtil.translate(entry.getValue()) + "\n");
		}
		return sb.toString();
	}

	private static void output(String string) {
		System.out.print(string);
	}

	private static void exit(int i) {
		System.exit(i);
	}
	
	
}
