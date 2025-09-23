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
package com.ibm.lconn.wizard.cluster.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.ibm.lconn.wizard.cluster.data.ClusterFeatureController;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.LCUtil;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.interfaces.LCAction;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.LCExecutionPage;
import com.ibm.lconn.wizard.common.ui.LCWizard;
import com.ibm.lconn.wizard.common.ui.LCWizardPage;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.data.WizardData;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;
import com.ibm.lconn.wizard.common.ui.ext.LCStyledTextWithButton;

public class ClusterWizard extends LCWizard implements LCAction {

	private boolean success = false;

	public ClusterWizard(String id) {
		super(id);
	}

	private ImageDescriptor logoImage;
	private Image iconImage;

	@Override
	public boolean performFinish() {
		if (success) {
			WizardData data = getData();
			DefaultWizardDataLoader.saveSession(data.getId(), "cluster");
		}
		getShell().dispose();
		return false;
	}

	public void addPages() {
		LCUtil.initialize(getData().getId());
		WizardData data = getData();
		String[] pages = data.getPages();
		for (String pageId : pages) {
			WizardPageData pageData = new WizardPageData(data.getId(), pageId);
			LCWizardPage page = DefaultWizardDataLoader
					.createWizardPage(pageData);
			addPage(page);
		}
		setWindowTitle(MessageUtil.getWizardTitle(data.getId()));
		setImages();
	}

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		specifyControls();
	}

	private void specifyControls() {
		setExecutionPage();
		setSummaryPage();
		setFinishPage();
		setPageDescription();
	}

	private void setPageDescription() {
		final LCWizardPage summaryPage = (LCWizardPage) getPage(ClusterConstant.PAGE_clusterSummaryPage);
		summaryPage.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				String task = DataPool.getValue(
						ClusterConstant.WIZARD_ID_CLUSTER,
						ClusterConstant.INPUT_clusterTask);
				if (ClusterConstant.OPTION_clusterTaskRemove
						.equals(task.trim()))
					summaryPage
							.getData()
							.setDesc(
									Messages
											.getString("WIZARD_PAGE.description.clusterSummaryPage.remove"));
				else
					summaryPage
							.getData()
							.setDesc(
									Messages
											.getString("WIZARD_PAGE.description.clusterSummaryPage.add"));
			}
		});
		final LCWizardPage featurePage = (LCWizardPage) getPage(ClusterConstant.PAGE_FeatureSelectionPage);
		featurePage.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				String task = DataPool.getValue(
						ClusterConstant.WIZARD_ID_CLUSTER,
						ClusterConstant.INPUT_clusterTask);
				if (ClusterConstant.OPTION_clusterTaskRemove
						.equals(task.trim()))
					featurePage
							.getData()
							.setDesc(
									Messages
											.getString("WIZARD_PAGE.description.FeatureSelectionPage.remove"));
				if (ClusterConstant.OPTION_clusterTaskCreate
						.equals(task.trim()))
					featurePage
							.getData()
							.setDesc(
									Messages
											.getString("WIZARD_PAGE.description.FeatureSelectionPage.create"));
				if (ClusterConstant.OPTION_clusterTaskAdd.equals(task.trim()))
					featurePage
							.getData()
							.setDesc(
									Messages
											.getString("WIZARD_PAGE.description.FeatureSelectionPage.add"));
			}
		});
	}

	private void setFinishPage() {
		final LCWizardPage page = (LCWizardPage) getPage(ClusterConstant.PAGE_clusterFinishPage);
		page.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				setButtonStyle(Constants.DISABLE_BACK
						| Constants.DISABLE_CANCEL);
			}
		});
	}

	private void setSummaryPage() {
		LCWizardPage page = (LCWizardPage) getPage(ClusterConstant.PAGE_clusterSummaryPage);
		page.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				String wizardId = ClusterConstant.WIZARD_ID_CLUSTER;
				DataPool
						.setValue(
								wizardId,
								ClusterConstant.INPUT_clusterSummaryContent,
								TaskInterface.getSummaryString()
										+ Messages
												.getString("cluster.summary.content.part.finish"));
			}

		});
	}

	private String getExecutionPageId() {
		return Constants.WIZARD_PAGE_COMMON_EXECUTION;
	}

	private void setExecutionPage() {
		String executionPanelId = getExecutionPageId();
		final LCExecutionPage page = (LCExecutionPage) getPage(executionPanelId);
		page.addPageRenderListener(new Listener() {
			public void handleEvent(Event event) {
				String runningTask = null;
				String taskSelected = DataPool.getValue(
						ClusterConstant.WIZARD_ID_CLUSTER,
						ClusterConstant.INPUT_clusterTask);

				if (ClusterConstant.OPTION_clusterTaskCreate
						.equals(taskSelected)) {
					runningTask = MessageUtil.getMsgWithParameter(
							"cluster.running.task.create",
							getFeaturesTranslated());
				}
				if (ClusterConstant.OPTION_clusterTaskAdd.equals(taskSelected)) {
					runningTask = MessageUtil
							.getMsgWithParameter("cluster.running.task.add",
									getFeaturesTranslated());

				}
				if (ClusterConstant.OPTION_clusterTaskRemove
						.equals(taskSelected)) {
					runningTask = MessageUtil.getMsgWithParameter(
							"cluster.running.task.remove",
							getFeaturesTranslated());

				}

				String msg = MessageUtil.getMsgWithParameter(
						"cluster.execution.description", runningTask,
						getLogPath());
				page.setTaskMessage(msg);
				page.refreshAsyn();

				WizardData data = DataPool.getWizard(getData().getId());
				for (String pageId : data.getPages()) {
					LCWizardPage page = (LCWizardPage) getPage(pageId);
					if (page == null) {
						continue;
					}
					DefaultWizardDataLoader.collectPageInput(page);
				}
			}
		});
		page.setAction(this);
	}

	private void setImages() {
		try {
			logoImage = ResourcePool.getWizardLogoIcon();
			setDefaultPageImageDescriptor(logoImage);
			iconImage = ResourcePool.getWizardTitleIcon();
			getShell().setImage(iconImage);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean canFinish() {
		return CommonHelper.equals(((LCWizardPage) getContainer()
				.getCurrentPage()).getData().getId(), "clusterFinishPage");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.common.interfaces.LCAction#execute()
	 */
	public String execute() {
		String finishResult = "";
		try {
			int exitValue = 0;

			String taskSelected = TaskInterface
					.eval(ClusterConstant.INPUT_clusterTask);

			if (ClusterConstant.OPTION_clusterTaskCreate.equals(taskSelected)) {
				exitValue = TaskInterface.addPrimaryNode();
				finishResult += TaskInterface.getOutput();
			}

			if (ClusterConstant.OPTION_clusterTaskAdd.equals(taskSelected)) {
				exitValue = TaskInterface.addSubSequentNode();
				finishResult += TaskInterface.getOutput();
			}

			if (ClusterConstant.OPTION_clusterTaskRemove.equals(taskSelected)) {
				exitValue = TaskInterface.uninstallNode();
				finishResult += TaskInterface.getOutput();
			}

			setFinishPanel(exitValue, finishResult);

		} catch (Exception e) {
			e.printStackTrace();
			finishResult += "Exception.\n\n";
			setFinishPanel(-1, finishResult);
		}
		String exeuctionPageId = getExecutionPageId();
		String[] pages = getData().getPages();
		int index = Util.indexOf(pages, exeuctionPageId);
		return pages[index + 1];
	}

	private void setFinishPanel(int exitValue, String content) {
		content += Messages.getString("cluster.finish.checklog", TaskInterface
				.eval(Constants.LOG_ROOT + Constants.FS + "cluster"),
				Constants.BUTTON_TEXT_SYMBOL);
		final String logPath = getLogPath();

		LCStyledTextWithButton.setTextAndButton(
				ClusterConstant.WIZARD_ID_CLUSTER, "INPUT_clusterFinish",
				content, Constants.BUTTON_TEXT_VIEWLOG, new LCAction() {
					public String execute() {
						return "" + Program.launch(logPath);
					}

					public String getLogPath() {
						return null;
					}
				});
		if (exitValue != 0) {
			LCWizardPage page = (LCWizardPage) getPage(ClusterConstant.PAGE_clusterFinishPage);
			page.setErrorMessage(MessageUtil
					.getMsg("cluster.finish.message.icon"));
		} else {
			success = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.lconn.wizard.common.interfaces.LCAction#getLogPath()
	 */
	public String getLogPath() {
		return Constants.LOG_ROOT + Constants.FS + "cluster";
	}

	public String getFeaturesTranslated() {
		String featuresLabel = DataPool.getValue(
				ClusterConstant.WIZARD_ID_CLUSTER,
				ClusterConstant.INPUT_clusterFeatureSelection);

		String[] featureLabs = featuresLabel.split(",");

		String result = "";
		String[] LABELS = new String[] { ClusterConstant.OPTION_featureAct,
				ClusterConstant.OPTION_featureBlogs,
				ClusterConstant.OPTION_featureComm,
				ClusterConstant.OPTION_featureDogear,
				ClusterConstant.OPTION_featureHomepage,
				ClusterConstant.OPTION_featureProf };
		for (String featureLab : featureLabs) {
			if (Util.indexOf(LABELS, featureLab.trim()) != -1) {
				String featureName = ClusterFeatureController
						.getFeatureName(featureLab);
				result += "," + MessageUtil.getMsg(featureName, "name");
			}
		}

		return result.substring(1);
	}
}
