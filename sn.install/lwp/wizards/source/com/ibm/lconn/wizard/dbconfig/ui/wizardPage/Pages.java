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
package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author joey (pengzsh@cn.ibm.com) 
 * 
 */
public class Pages {
	public final static int WELCOME_PAGE = 1;
	public final static int OPERATION_SELECT = 2;
	public final static int DATABASE_TYPE_SELECT = 3;
	public final static int FEATURE_SELECT = 4;
	public final static int JDBC_CONNECTION_INFO = 5;
	public final static int SUMMARY_PAGE = 6;
//	public final static int EXECUTION_NOTE_PAGE = 7;
	public final static int EXECUTION_DETAILED_COMMAND_EXPORT_PAGE = 7;
	public final static int EXECUTION_DETAILED_COMMAND_PAGE = 8;
	public final static int EXECUTION_PAGE = 9;
	public final static int FINISH_PAGE = 10;
	public final static int FEATURE_INFO_PAGE = 11;
	public final static int SQLSERVER_FILE = 12;
	public final static int COMMUNITIES_OPTIONAL_TASK = 13;
	public final static int CONTENT_MIGRATE_TASK=14;
//	public final static int PROFILE_CONNECTION_INFO=14;

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
		case OPERATION_SELECT:
			page = new OperationOptionWizardPage();
			break;
		case DATABASE_TYPE_SELECT:
			page = new DbTypeSelectionWizardPage();
			break;
		case FEATURE_SELECT:
			page = new FeatureSelectionWizardPage();
			break;
		case JDBC_CONNECTION_INFO:
			page = new JDBCConnectionWizardPage();
			break;
		case SUMMARY_PAGE:
			page = new SummaryWizardPage();
			break;
//		case EXECUTION_NOTE_PAGE:
//			page = new ExecutionNoteWizardPage();
		case EXECUTION_DETAILED_COMMAND_EXPORT_PAGE:
		   page = new ExecutionDetailedCommandExportWizardPage();
		   break;
		case EXECUTION_DETAILED_COMMAND_PAGE: 
			page = new ExecutionDetailedCommandWizardPage();
			break;
		case EXECUTION_PAGE:
			page = new ExecutionWizardPage();
			break;
		case FINISH_PAGE:
			page = new FinishWizardPage();
			break;
		case FEATURE_INFO_PAGE:
			page = new FeatureDBInformationPage();
			break;
		case SQLSERVER_FILE:
			page = new SQLServerCreateFilePath();
			break;
		case COMMUNITIES_OPTIONAL_TASK:
			page = new CommunitiesOptionalTaskWizardPage();
			break;
		case CONTENT_MIGRATE_TASK:
			page = new ConstentStoreMigratePanel();
			break;
//		case PROFILE_CONNECTION_INFO:
//			page = new ProfileConnectionInfoPanel();
		default:
			break;
		}

		return page;
	}

}
