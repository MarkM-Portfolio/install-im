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
package com.ibm.lconn.wizard.common.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Validator;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;
import com.ibm.lconn.wizard.common.validator.ValidationMessage;

/**
 * Provides default implementation for PageController
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 */
public class DefaultPageController implements PageController {
	private static final Logger logger = LogUtil.getLogger(DefaultPageController.class);

	// pages controls by this controller
	protected List<String> pages = Arrays.asList(new String[] {});

	// page validators
	private Map<String, List<Validator>> map = new HashMap<String, List<Validator>>();

	private List<ValidationMessage> messages = null;

	protected String wizardId;

	/**
	 * Gets message generated during performAction
	 * 
	 * @return
	 */
	public List<ValidationMessage> getMessages() {
		return messages;
	}

	public DefaultPageController(String wizardId) {
		this.wizardId = wizardId;

		String[] pagesArry = DataPool.getWizard(wizardId).getPages();
		this.pages = Collections.unmodifiableList(Arrays.asList(pagesArry));

		System.setProperty(Constants.LCONN_WIZARD_PROP, wizardId);
	}

	public void registerValidator(String basePage, Validator validator) {
		if (validator == null || basePage == null) {
			throw new NullPointerException();
		}
		if (!map.containsKey(basePage)) {
			map.put(basePage, new ArrayList<Validator>());
		}
		map.get(basePage).add(validator);
	}

	protected int validate(String basePage) {
		int result = 0;
		// List<Validator> validators = map.get(basePage);

		WizardPageData data = DataPool.getWizardPage(wizardId, basePage);
		List<Validator> validators = data.getValidatorList();

		if (validators != null) {
			for (Validator validator : validators) {
				int v = validator.validate();
				if (v != 0) {
					// validation fails
					ValidationMessage m = new ValidationMessage(validator.getLevel(), validator.getTitle(), validator.getMessage());
					messages.add(m);
					result = result == 0 ? v : result;
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.lconn.wizard.common.interfaces.PageController#performAction(java
	 * .lang.String, java.lang.String)
	 */
	public String performAction(String basePage, String actionName) {
		messages = new ArrayList<ValidationMessage>();

		if (Constants.WIZARD_ACTION_PREVIOUS.equals(actionName)) {
			return processPrevious(basePage);
		} else if (Constants.WIZARD_ACTION_NEXT.equals(actionName)) {
			return processNext(basePage);
		} else if (Constants.WIZARD_ACTION_FINISH.equals(actionName)) {
			return processFinish(basePage);
		} else if (Constants.WIZARD_ACTION_CANCEL.equals(actionName)) {
			return processCancel(basePage);
		}
		logger.log(Level.SEVERE, "common.severe.action_illegal", actionName);
		// do nothing, stay at the same page
		return basePage;
	}

	protected String processPrevious(String basePage) {
		int index = pages.indexOf(basePage);
		if (index == 0) {
			return basePage;
		} else if (index > 0) {
			return pages.get(index - 1);
		}
		logger.log(Level.SEVERE, "common.severe.page_not_found", basePage);
		throw new IllegalArgumentException("Wizard page with ID " + basePage + " is not found ");
	}

	protected String processNext(String basePage) {
		if (basePage == null) {
			return pages.get(0);
		}

		int index = pages.indexOf(basePage);
		if (index == -1) {
			logger.log(Level.SEVERE, "common.severe.page_not_found", basePage);
			throw new IllegalArgumentException("Wizard page with ID " + basePage + " is not found ");
		}

		messages.clear();
		int result = validate(basePage);
		while (result == Constants.RE_VALIDATE) {
			logger.log(Level.WARNING, "Into validation loop.");
			messages.clear();
			result = validate(basePage);
			if (result != Constants.RE_VALIDATE)
				logger.log(Level.WARNING, "Out validation loop.");
		}
		if (result != 0) {
			return basePage;
		}
		// validation passed
		return index == pages.size() - 1 ? pages.get(index) : pages.get(index + 1);
	}

	protected String getNextPage(String basePage) {
		if (basePage == null) {
			return pages.get(0);
		}

		int index = pages.indexOf(basePage);
		if (index == -1) {
			logger.log(Level.SEVERE, "common.severe.page_not_found", basePage);
			throw new IllegalArgumentException("Wizard page with ID " + basePage + " is not found ");
		}

		return index == pages.size() - 1 ? pages.get(index) : pages.get(index + 1);
	}

	protected String processCancel(String basePage) {
		// simply return wizard exit
		return Constants.WIZARD_PAGE_Exit;
	}

	protected String processFinish(String basePage) {
		// simply return wizard exit
		return Constants.WIZARD_PAGE_Exit;
	}

}
