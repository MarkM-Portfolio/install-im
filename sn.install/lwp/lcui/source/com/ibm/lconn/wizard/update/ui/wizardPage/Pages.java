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
package com.ibm.lconn.wizard.update.ui.wizardPage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;

public class Pages {
	public final static int WELCOME_PAGE = 1;
	public final static int ACTION_SELECT = 2;
	public final static int FIX_INFO_SELECT = 3;
	public final static int WAS_AUTH_INFO = 4;
	public final static int SUMMARY_PAGE = 5;
	public final static int EXECUTION_PAGE = 6;
	public final static int FINISH_PAGE = 7;

	private static Map<Integer, IWizardPage> pageMap = new HashMap<Integer, IWizardPage>();

	public static IWizardPage getPage(int pageId) {
		IWizardPage page = pageMap.get(new Integer(pageId));
		if (page == null) {
			page = createNewPage(pageId);
			pageMap.put(new Integer(pageId), page);
		}
		return page;
	}

	public static IWizardPage getCurrentPage(IWizard wizard) {
		return wizard.getContainer().getCurrentPage();
	}

	private static IWizardPage createNewPage(int pageId) {
		IWizardPage page = null;
		switch (pageId) {
		case WELCOME_PAGE:
			page = new WelcomeWizardPage();
			break;
		case ACTION_SELECT:
			page = new OperationOptionWizardPage();
			break;
		case FIX_INFO_SELECT:
			page = new FixInfoWizardPage();
			break;
		case WAS_AUTH_INFO:
			page = new WASAuthInfoWizardPage();
			break;
		case SUMMARY_PAGE:
			page = new SummaryWizardPage();
			break;
		case EXECUTION_PAGE:
			page = new ExecutionWizardPage();
			break;
		case FINISH_PAGE:
			page = new FinishWizardPage();
			break;
		default:
			break;
		}

		return page;
	}

}
