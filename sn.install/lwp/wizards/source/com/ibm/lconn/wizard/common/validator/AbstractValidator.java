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
package com.ibm.lconn.wizard.common.validator;

import org.eclipse.jface.dialogs.MessageDialog;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Validator;
import com.ibm.lconn.wizard.common.msg.Messages;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public abstract class AbstractValidator implements Validator {
	public static boolean isSilentMode = false;
	protected static final String MSG_PREFIX = "validator";
	protected String title;
	protected String message;
	protected int level;
	// wizard is initialized by 'lconn.wizard' system property
	protected String wizard = System.getProperty(Constants.LCONN_WIZARD_PROP);
	private Object[] parameters;

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public int getLevel() {
		return level;
	}

	protected String eval(String valueKey) {
		wizard = System.getProperty(Constants.LCONN_WIZARD_PROP);
		String val = DataPool.getValue(wizard, valueKey);
		return val == null ? "" : val;
	}

	protected String getLable(String valueKey) {
		wizard = System.getProperty(Constants.LCONN_WIZARD_PROP);
		String label = DataPool.getWizardPageInput(wizard, valueKey).getName();
		return label;
	}

	public String getWizard() {
		return wizard;
	}

	public void setWizard(String wizard) {
		this.wizard = wizard;
	}

	protected void logError(String key) {
		level = MessageDialog.ERROR;
		log(key);
	}

	protected void logError(String key, Object... objects) {
		level = MessageDialog.ERROR;
		log(key, objects);
	}

	protected void logWarning(String key) {
		level = MessageDialog.WARNING;
		log(key);
	}

	protected void logWarning(String key, Object... objects) {
		level = MessageDialog.WARNING;
		log(key, objects);
	}

	private void log(String key, Object... objects) {
		if (isSilentMode) {
			System.out.println(Messages.getString(MSG_PREFIX + "." + key
					+ ".message", objects));

		} else {
			title = Messages.getString(MSG_PREFIX + "." + key + ".title");
			message = Messages.getString(MSG_PREFIX + "." + key + ".message",
					objects);
		}
	}

	private void log(String key) {
		if (isSilentMode) {
			System.out.println(Messages.getString(MSG_PREFIX + "." + key
					+ ".message"));
		} else {
			title = Messages.getString(MSG_PREFIX + "." + key + ".title");
			message = Messages.getString(MSG_PREFIX + "." + key + ".message");
		}
	}

	public void setParameters(String... parameters) {
		this.parameters = parameters;
	}

	protected String getTite(String key) {
		return Messages.getString(MSG_PREFIX + "." + key + ".title");
	}

	protected String getMessage(String key) {
		return Messages.getString(MSG_PREFIX + "." + key + ".message");
	}

	protected String getMessage(String key, Object... objects) {
		return Messages.getString(MSG_PREFIX + "." + key + ".message", objects);
	}

	/**
	 * @return the parameters
	 */
	protected Object[] getParameters() {
		return parameters;
	}

	public void reset() {
		title = null;
		message = null;
	}

}
