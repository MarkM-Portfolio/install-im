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
package com.ibm.lconn.wizard.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class TextHelper {
	private static final String AUTO_VALIDATION_TEXT_VALIDATE_FOR_ALL = "AUTO_VALIDATION_TEXT_VALIDATE_FOR_ALL";
	private static final String AUTO_VALIDATION_TEXT_VALIDATOR_LIST = "AUTO_VALIDATION_TEXT_VALIDATOR_LIST";
	private static final String AUTO_VALIDATION_TEXT_AUTO_VALIDATION = "AUTO_VALIDATION_TEXT_AUTO_VALIDATION";
	private static Color invalidColor;
	private static Color validColor;

	public static Text newAutoValidationText(Composite parent, int style) {
		final Text text = new Text(parent, style);
		makeAutoValidate(text);
		return text;
	}

	public static void makeAutoValidate(final Widget widget) {
		if (widget instanceof Text || widget instanceof Combo
				|| widget instanceof Spinner){
			widget.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event event) {
					boolean valid = isValid(widget);
					if(isAutoValidation(widget))
						setValidationStatus(widget, valid);
				}
			});
		}
		
	}

	private static void setValidationStatus(Widget widget, boolean valid) {
		if (widget instanceof Text) {
			Text text = (Text) widget;
			if (!valid) {
				text.setBackground(getInvalidColor());
			} else {
				text.setBackground(getValidColor());
			}
		}
	}

	private static Color getInvalidColor() {
		if (invalidColor == null)
			invalidColor = new Color(ResourcePool.getDisplay(), 255, 255, 0);
		return invalidColor;
	}

	private static Color getValidColor() {
		if (validColor == null)
			validColor = new Color(ResourcePool.getDisplay(), 255, 255, 255);
		return validColor;
	}

	public static void addValidator(Text text, Validator validator) {
		getValidatorList(text).add(validator);
		setValidationStatus(text, isValid(text));
	}

	@SuppressWarnings("unchecked")
	private static List<Validator> getValidatorList(Widget widget) {
		Object validatorListObject = widget
				.getData(AUTO_VALIDATION_TEXT_VALIDATOR_LIST);
		List<Validator> validatorList;
		if (validatorListObject == null) {
			validatorList = new ArrayList<Validator>();
			widget.setData(AUTO_VALIDATION_TEXT_VALIDATOR_LIST, validatorList);
		} else
			validatorList = (List<Validator>) validatorListObject;
		return validatorList;
	}

	public static boolean isValid(Widget widget) {
		List<Validator> validatorList = getValidatorList(widget);
		if (validatorList.size() == 0)
			return true;
		if (isValidForAll(widget)) {
			for (Validator validator : validatorList) {
				if (validator.isValid(widget)) {
				} else {
					return false;
				}
			}
			return true;
		} else {
			for (Validator validator : validatorList) {
				if (validator.isValid(widget)) {
					return true;
				}
			}
			return false;
		}
	}
	
	

	public static boolean isValidForAll(Widget widget) {
		return null != widget.getData(AUTO_VALIDATION_TEXT_VALIDATE_FOR_ALL);
	}

	public static void setValidateForAll(Widget widget, boolean validateForAll) {
		Object o = (validateForAll ? AUTO_VALIDATION_TEXT_VALIDATE_FOR_ALL
				: null);
		widget.setData(AUTO_VALIDATION_TEXT_VALIDATE_FOR_ALL, o);
	}
	
	public static void setAutoValidation(Widget widget, boolean autoValidation){
		Object o = (autoValidation ? AUTO_VALIDATION_TEXT_AUTO_VALIDATION
				: null);
		widget.setData(AUTO_VALIDATION_TEXT_AUTO_VALIDATION, o);
	}
	
	public static boolean isAutoValidation(Widget widget) {
		return null != widget.getData(AUTO_VALIDATION_TEXT_AUTO_VALIDATION);
	}
}
