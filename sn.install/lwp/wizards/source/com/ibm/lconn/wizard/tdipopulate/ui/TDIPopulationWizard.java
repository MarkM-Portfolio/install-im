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
package com.ibm.lconn.wizard.tdipopulate.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.DefaultValue;
import com.ibm.lconn.wizard.common.Entry;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.interfaces.LCAction;
import com.ibm.lconn.wizard.common.interfaces.TaskOutputter;
import com.ibm.lconn.wizard.common.test.TestDataOffer;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.LCExecutionPage;
import com.ibm.lconn.wizard.common.ui.LCWizard;
import com.ibm.lconn.wizard.common.ui.LCWizardPage;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.data.WizardData;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;
import com.ibm.lconn.wizard.common.ui.ext.LCCheck;
import com.ibm.lconn.wizard.common.ui.ext.LCStyledTextWithButton;
import com.ibm.lconn.wizard.common.ui.ext.LCText;
import com.ibm.lconn.wizard.common.ui.ext.LCWizardInput;
import com.ibm.lconn.wizard.tdipopulate.backend.NetStore;
import com.ibm.lconn.wizard.tdipopulate.backend.Task;

public class TDIPopulationWizard extends LCWizard implements LCAction {

	private static final String TDI_FINISH_PANEL = "TDI_FINISH_PANEL";
	private boolean success = false;
	private String logPath = null;

	public TDIPopulationWizard(String id) {
		super(id);
	}

	private ImageDescriptor logoImage;
	private Image iconImage;

	@Override
	public boolean performFinish() {
		if (success) {
			WizardData data = getData();
			DefaultWizardDataLoader.saveSession(data.getId(),
					Constants.TDI_DEFAULT_TASK);
		}
		getShell().dispose();
		return false;
	}

	private void setFinishPanel(CommandResultInfo runCommand) {
		if (runCommand != null) {
//			String preResult = MessageUtil
//					.getMsg("tdipopulate.finish.preResult");
//			String preDetail = MessageUtil
//					.getMsg("tdipopulate.finish.preDetail");
//			String toExit = MessageUtil
//					.getMsg("tdipopulate.finish.exit.message");
//			String logMsg = MessageUtil.getMsgWithParameter(
//					"tdipopulate.finish.logging.message", getLogPath());
//			String exitMessage = preResult + runCommand.getExitMessage()
//					+ "\n\n\n" + preDetail + getFinishString() + logMsg
//					+ "\n\n" + toExit;
//			exitMessage = preResult + runCommand.getExitMessage() + "\n\n"
//					+ logMsg + "\n" + Constants.BUTTON_TEXT_SYMBOL + "\n\n"
//					+ preDetail + getFinishString() + "\n\n" + toExit;
			String exitMessage = MessageUtil.getMsgWithParameter(
					"tdipopulate.finish.message",
					Constants.BUTTON_TEXT_SYMBOL,
					runCommand.getExitMessage(),
					getLogPath(),
					getFinishString(),
					new File(Constants.TDI_WORK_DIR + Constants.FS + "logs" + Constants.FS + "PopulateDBFromDNFile.log").getAbsolutePath());
			WizardData data = getData();
			String wizardId = data.getId();
			String inputId = Constants.INPUT_TDI_FINISH;

			final String logPath = Constants.TDI_WORK_DIR + Constants.FS + "logs" + Constants.FS
			+ "PopulateDBFromDNFile.log";
			LCStyledTextWithButton.setTextAndButton(wizardId, inputId,
					exitMessage, Constants.BUTTON_TEXT_VIEWLOG, new LCAction() {
						public String execute() {
							return "" + CommonHelper.openLog(logPath);
						}

						public String getLogPath() {
							return null;
						}
					});
			if (runCommand.getExecState() != CommandResultInfo.COMMAND_SUCCEED) {
				LCWizardPage page = (LCWizardPage) getPage(Constants.WIZARD_PAGE_TDI_FINISH);
				page.setErrorMessage(MessageUtil
						.getMsg("tdipopulate.finish.error.general"));
			} else {
				success = true;
			}
			if (CommonHelper.equals(Constants.BOOL_TRUE, DataPool.getValue(
					Constants.WIZARD_ID_TDIPOPULATE,
					Constants.NETSTORE_NEED_STOP))) {
				String tdiHome = DataPool.getValue(
						Constants.WIZARD_ID_TDIPOPULATE,
						Constants.INPUT_TDI_INSTALL_DIR);
				NetStore.stop(DefaultWizardDataLoader.loadTDIEnv(tdiHome));
			}
		} else {
			// Error occured...
		}
	}

	public void addPages() {

		WizardData data = getData();
		String[] pages = data.getPages();
		for (String pageId : pages) {
			WizardPageData pageData = new WizardPageData(data.getId(), pageId);
			addPage(DefaultWizardDataLoader.createWizardPage(pageData));
		}
		setWindowTitle(MessageUtil.getWizardTitle(data.getId()));
		setImages();

	}

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		TestDataOffer.setLDAPDNDropdown();
		specifyControls();
	}

	private void specifyControls() {
		setTestData();
		setLastSessionPane();
		addTDIInstallLocationErrorMessage();
		addTDIInstallLocationUpdate();
		addSummaryPanelValueUpdate();
		setExecutionPageTask();
		setFinishPage();
		setSSLWidgets();

	}

	private void addTDIInstallLocationErrorMessage() {
		LCWizardPage page = (LCWizardPage) getPage(Constants.WIZARD_PAGE_TDIInstallDir);
		page.setErrorMessage(MessageUtil.getMsg("tdipopulate.tdi.detect.fail"));
	}

	private void addTDIInstallLocationUpdate() {
		LCWizardPage page = (LCWizardPage) getPage(Constants.WIZARD_PAGE_DBInfo);
		page.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				LCWizardInput input = DataPool.getWizardInputWidget(getData()
						.getId(), Constants.INPUT_TDI_INSTALL_DIR);
				input.setValue(DataPool.getValue(getData().getId(),
						Constants.INPUT_TDI_INSTALL_DIR));
			}
		});
	}

	private void setTestData() {
		TestDataOffer.setKeystoreType();
	}

	private void setLastSessionPane() {
		if (DefaultWizardDataLoader.hasLastSession(
				Constants.WIZARD_ID_TDIPOPULATE, Constants.TDI_DEFAULT_TASK))
			DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE,
					Constants.INPUT_TDI_ACTION_TYPE,
					Constants.TDI_START_FROM_LAST_SESSION);
	}

	private void setSSLWidgets() {
		String wizardId = Constants.WIZARD_ID_TDIPOPULATE;
		final LCWizardInput sslCheck = DataPool.getWizardInputWidget(wizardId,
				Constants.INPUT_TDI_LDAP_USE_SSL);
		DataPool.setValue(wizardId, Constants.INPUT_TDI_LDAP_USE_SSL,
				Constants.BOOL_FALSE);
		sslCheck.updateData();
		refreshSSLWidgets();
		sslCheck.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				refreshSSLWidgets();
				LCText port = (LCText)DataPool.getWizardInputWidget(
						Constants.WIZARD_ID_TDIPOPULATE, Constants.INPUT_TDI_LDAP_SERVER_PORT);
				boolean sslEnabled = CommonHelper.equals(Constants.BOOL_TRUE,
						((LCCheck)sslCheck).getValue());
				DataPool.setValue(Constants.WIZARD_ID_TDIPOPULATE, 
						Constants.INPUT_TDI_LDAP_SERVER_PORT, 
						DefaultValue.getLDAPPort(sslEnabled));
				port.updateData();
			}
		});
	}

	private void setFinishPage() {
		final LCWizardPage page = (LCWizardPage) getPage(TDI_FINISH_PANEL);
		page.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				setButtonStyle(Constants.DISABLE_BACK
						| Constants.DISABLE_CANCEL);
			}
		});
	}

	private void setExecutionPageTask() {
		String executionPanelId = getExecutionPageId();
		final LCExecutionPage page = (LCExecutionPage) getPage(executionPanelId);
		TaskOutputter tOutput = new TaskOutputter() {
			public String output(String task, String logPath) {
				String msg = MessageUtil.getMsgWithParameter(
						"tdipopulate.execution.logging.message", logPath);
				page.setTaskMessage(msg);
				page.refreshAsyn();
				return msg;
			}
		};
		page.setAction(this);
		tOutput.output("", page.getLogPath());
		page.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				WizardData data = DataPool.getWizard(getData().getId());
				for (String pageId : data.getPages()) {
					LCWizardPage page = (LCWizardPage) getPage(pageId);
					if (page == null) {
						//System.out.println(pageId);
						continue;
					}
					DefaultWizardDataLoader.collectPageInput(page);
				}
			}
		});
	}

	private String getExecutionPageId() {
		return getData().getId() + "." + Constants.WIZARD_PAGE_COMMON_EXECUTION;
	}

	private void addSummaryPanelValueUpdate() {
		String summaryPanelId = getData().getId() + "."
				+ Constants.WIZARD_PAGE_COMMON_SUMMARY;
		LCWizardPage page = (LCWizardPage) getPage(summaryPanelId);
		page.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				Constants.BUTTON_TEXT_NEXT = Constants.BUTTON_TEXT_CONFIG;

				String wizardId = getData().getId();
				String summaryStr = getSummaryString();
				// DataPool.setValue(wizardId,
				// Constants.INPUT_TDI_SUMMARY,summaryStr);
				LCStyledTextWithButton.setTextAndButton(wizardId,
						Constants.INPUT_TDI_SUMMARY, summaryStr, "", null);
			}

		});
	}

	private void setImages() {
		try {
//			logoImage = ResourcePool.getWizardLogoIcon();
//			setDefaultPageImageDescriptor(logoImage);
			iconImage = ResourcePool.getWizardTitleIcon();
			getShell().setImage(iconImage);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private String getLog(String task) {
		if (this.logPath == null)
			this.logPath = Task.CreateLogForTasks();
		return this.logPath;
	}

	public boolean canFinish() {
		boolean canFinish = CommonHelper.equals(getContainer().getCurrentPage()
				.getName(), TDI_FINISH_PANEL);
		return canFinish;
	}

	public String execute() {
		WizardData data = getData();
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
			setFinishPanel(runCommand);

		} catch (Exception e) {
			e.printStackTrace();
			setFinishPanel(TestDataOffer.getDefaultCommandInfo());
		}
		String exeuctionPageId = data.getId() + "."
				+ Constants.WIZARD_PAGE_COMMON_EXECUTION;
		String[] pages = getData().getPages();
		int index = Util.indexOf(pages, exeuctionPageId);
		return pages[index + 1];
	}

	public String getLogPath() {
		return getLog(Constants.TDI_DEFAULT_TASK);
	}
	
//	public String getTDILogPath(){
//		if (this.TDIlogPath == null)
//			this.TDIlogPath = Task.CreateLogForTask2();
//		return this.TDIlogPath;
//	}

	private void refreshSSLWidgets() {
		String wizardId = Constants.WIZARD_ID_TDIPOPULATE;
		String[] SSLWidgets = { Constants.INPUT_TDI_SSL_KEY_STORE,
				Constants.INPUT_TDI_SSL_PASSWORD, Constants.INPUT_TDI_SSL_TYPE };
		LCWizardInput sslCheck = DataPool.getWizardInputWidget(wizardId,
				Constants.INPUT_TDI_LDAP_USE_SSL);
		boolean enabled = CommonHelper.equals(sslCheck.getValue(),
				Constants.BOOL_TRUE);
		for (String sslWidgetId : SSLWidgets) {
			LCWizardInput sslWidget = DataPool.getWizardInputWidget(wizardId,
					sslWidgetId);
			sslWidget.setEnable(enabled);
		}
	}

	private String getFinishString() {
		String wizardId = getData().getId();
		List<com.ibm.lconn.wizard.common.Entry<String, String>> userInputData = DefaultWizardDataLoader
				.collectTaskInput(wizardId, Constants.TDI_DEFAULT_TASK
						+ ".summary");
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, String>> iterator = userInputData.iterator();
		
		List<String> optinalTasks = 
			Arrays.asList(DataPool.getValue(wizardId, Constants.INPUT_TDI_OPTIONAL_TASK).split(","));
		
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			if (!Constants.BOOL_TRUE.equals(DataPool.getValue(wizardId,
					Constants.INPUT_TDI_LDAP_USE_SSL))) {
				if ("ssl.type".equals(entry.getKey()))
					continue;
				if ("ssl.keyStore".equals(entry.getKey()))
					continue;
			}
			String entryKey = entry.getKey();
			if("task.country.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_COUNTRIES)) {
				continue;
			}
			if("task.department.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_DEPARTMENT)) {
				continue;
			}
			if("task.organization.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_ORGANIZATION)) {
				continue;
			}
			if("task.empoyeetype.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_EMPLOYEE)) {
				continue;
			}
			if("task.worklocation.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_WORK_LOCATION)) {
				continue;
			}
			String label = MessageUtil.getLabel(entry.getKey());
			if (CommonHelper.isEmpty(label))
				label = entry.getKey();
			sb.append(label + ": " + MessageUtil.translate(entry.getValue())
					+ "\n");
		}
		String summaryStr = sb.toString();
		return summaryStr;
	}

	private String getSummaryString() {
		String wizardId = getData().getId();
		List<com.ibm.lconn.wizard.common.Entry<String, String>> userInputData = DefaultWizardDataLoader
				.collectTaskInput(wizardId, Constants.TDI_DEFAULT_TASK
						+ ".summary");
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, String>> iterator = userInputData.iterator();
		
		List<String> optinalTasks = 
			Arrays.asList(DataPool.getValue(wizardId, Constants.INPUT_TDI_OPTIONAL_TASK).split(","));
		
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			if (!Constants.BOOL_TRUE.equals(DataPool.getValue(wizardId,
					Constants.INPUT_TDI_LDAP_USE_SSL))) {
				if ("ssl.type".equals(entry.getKey()))
					continue;
				if ("ssl.keyStore".equals(entry.getKey()))
					continue;
			}
			String entryKey = entry.getKey();
			if("task.country.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_COUNTRIES)) {
				continue;
			}
			if("task.department.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_DEPARTMENT)) {
				continue;
			}
			if("task.organization.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_ORGANIZATION)) {
				continue;
			}
			if("task.empoyeetype.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_EMPLOYEE)) {
				continue;
			}
			if("task.worklocation.csv".equals(entryKey) && !optinalTasks.contains(Constants.LDAP_OPTIONAL_TASK_FILL_WORK_LOCATION)) {
				continue;
			}
			
			String label = MessageUtil.getLabel(entry.getKey());
			if (CommonHelper.isEmpty(label))
				label = entry.getKey();
			sb.append(label + ": " + MessageUtil.translate(entry.getValue())
					+ "\n");
		}
		String summaryStr = sb.toString();
		summaryStr = MessageUtil.getMsgWithParameter(
				"tdipopulate.summary.message", summaryStr);
		return summaryStr;
	}

}
