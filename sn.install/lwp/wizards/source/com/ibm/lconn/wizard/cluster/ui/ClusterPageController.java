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

import org.eclipse.jface.window.Window;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.Validator;
import com.ibm.lconn.wizard.common.interfaces.DefaultPageController;
import com.ibm.lconn.wizard.common.msg.MessagePopup;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class ClusterPageController extends DefaultPageController {

	public ClusterPageController(String wizardId) {
		super(wizardId);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getNextPage(String basePage) {
		// TODO Auto-generated method stub
		return super.getNextPage(basePage);
	}

	public String performAction(String basePage, String actionName) {
		if (eval(ClusterConstant.INPUT_clusterTask,
				ClusterConstant.OPTION_clusterTaskRemove)) {
			DataPool.setTask("uninstall");
		} else {
			DataPool.setTask("");
		}

		String nextPage = getNextPage(basePage);

		if (ClusterConstant.PAGE_DMInfoPage.equals(basePage)) {
			int status = MessagePopup
					.showQuestionMessage(
							this,
							MessageUtil
									.getMsg("cluster.dminfo.check.profile.title"),
							MessageUtil
									.getMsgWithParameter(
											"cluster.dminfo.check.profile.content",
											DataPool
													.getValue(
															wizardId,
															ClusterConstant.INPUT_clusterDMProfileName)));
			if (status == Window.CANCEL)
				return basePage;
		}

		if (ClusterConstant.PAGE_featureActInfoPage.equals(basePage)) {
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_ACTIVITIES);
		}

		if (ClusterConstant.PAGE_featureBlogsInfoPage.equals(basePage)) {
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_BLOGS);
		}

		if (ClusterConstant.PAGE_featureCommunitiesInfoPage.equals(basePage)) {
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_COMMUNITIES);
		}

		if (ClusterConstant.PAGE_featureHomepageInfoPage.equals(basePage)) {
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_HOMEPAGE);
		}

		String nextPageAfterValidation = super.performAction(basePage,
				actionName);
		if (!CommonHelper.equals(nextPage, nextPageAfterValidation)) {
			return nextPageAfterValidation;
		}
		// Pass validation
		return controlPage(actionName, nextPage);
	}

	private String controlPage(String actionName, String pageToRender) {

		/*
		 * if (ClusterConstant.PAGE_DMInfoPage.equals(pageToRender)) { if
		 * (eval(ClusterConstant.INPUT_clusterTask,
		 * ClusterConstant.OPTION_clusterTaskRemove)) { return
		 * controlPage(actionName, getNextPage(pageToRender)); } }
		 */

		if (ClusterConstant.PAGE_DMInfoPageUninstall.equals(pageToRender)) {
			if (!eval(ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskRemove)) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
		}

		if (ClusterConstant.PAGE_NodeInfoSubsequent.equals(pageToRender)) {
			// Only available when add subsequent node
			if (!eval(ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskAdd)) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
		}

		if (ClusterConstant.PAGE_featureActInfoPage.equals(pageToRender)) {
			if (eval(ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskRemove)) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_ACTIVITIES);
			String selectedFeatures = eval(ClusterConstant.INPUT_clusterFeatureSelection);
			if (selectedFeatures.indexOf(ClusterConstant.OPTION_featureAct) == -1) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
		}

		if (ClusterConstant.PAGE_featureBlogsInfoPage.equals(pageToRender)) {
			if (eval(ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskRemove)) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_BLOGS);
			String selectedFeatures = eval(ClusterConstant.INPUT_clusterFeatureSelection);
			if (selectedFeatures.indexOf(ClusterConstant.OPTION_featureBlogs) == -1) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
		}

		if (ClusterConstant.PAGE_featureCommunitiesInfoPage
				.equals(pageToRender)) {
			if (eval(ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskRemove)) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_COMMUNITIES);
			String selectedFeatures = eval(ClusterConstant.INPUT_clusterFeatureSelection);
			if (selectedFeatures.indexOf(ClusterConstant.OPTION_featureComm) == -1) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
		}

		if (ClusterConstant.PAGE_featureHomePage.equals(pageToRender)) {
			if (eval(ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskRemove)) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
			DataPool.setValue(wizardId, ClusterConstant.INPUT_RUNNING_FEATURE,
					Constants.FEATURE_HOMEPAGE);
			String selectedFeatures = eval(ClusterConstant.INPUT_clusterFeatureSelection);
			if (selectedFeatures
					.indexOf(ClusterConstant.OPTION_featureHomepage) == -1) {
				return controlPage(actionName, getNextPage(pageToRender));
			}
		}

		if (ClusterConstant.PAGE_fullSyncPage.equals(pageToRender)) {
			if (eval(ClusterConstant.INPUT_clusterTask,
					ClusterConstant.OPTION_clusterTaskRemove)) {
				return controlPage(actionName, getNextPage(pageToRender));

			}
		}

		if (ClusterConstant.PAGE_clusterSummaryPage.equals(pageToRender)) {
			Constants.BUTTON_TEXT_NEXT = Constants.BUTTON_TEXT_CONFIG;
		}

		if (ClusterConstant.PAGE_clusterFinishPage.equals(pageToRender)) {

		}

		return pageToRender;
	}

	private boolean eval(String key, String targetVal) {
		return CommonHelper.equals(eval(key), targetVal);
	}

	private String eval(String key) {
		return DataPool.getValue(ClusterConstant.WIZARD_ID_CLUSTER, key);
	}

	public void registerValidator(String basePage, Validator validator) {
		// TODO Auto-generated method stub

	}
}
