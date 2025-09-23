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
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.ValuePool;
import com.ibm.lconn.wizard.common.msg.Messages;

public class GroupUtil {

	/**
	 * Create a group on parent Composite, with the specified styles. <br>
	 * Buttons with internal id will be created in the group.
	 * 
	 * @param id
	 * @param parent
	 * @param groupStyle
	 * @param buttonStyle
	 * @param data
	 * @return the created group.
	 */
	public static Group createGroup(String[] id, Composite parent, int groupStyle,
			int buttonStyle, SelectionListener listener, ValuePool valuePool) {
		return createGroup(id, parent, groupStyle, buttonStyle, listener, valuePool, null);
	}
	
	public static Group createGroup(String[] id, Composite parent, int groupStyle,
			int buttonStyle, SelectionListener listener, ValuePool valuePool, String labelKey) {
		Group group = new Group(parent, groupStyle);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		group.setLayout(new GridLayout());
		
		if(labelKey != null) {
			String labelValue = Messages.getString(labelKey);
			if(labelValue != null && !"".equals(labelValue.trim())) {
				Label label = new Label(group, SWT.WRAP);
				label.setText(labelValue);
				label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		
		//*****************jia****************
		
		for (int i = 0; i < id.length; i++) {
			Button featureButton = new Button(group, buttonStyle);
			String buttonText = valuePool.getString(id[i]);
			if (buttonText == null)
				buttonText = id[i];
			featureButton.setText(buttonText);
			if (listener != null) {
				featureButton.addSelectionListener(listener);
			}
			CommonHelper.setWidgetID(featureButton, id[i]);
		}
		return group;
	}

	/**
	 * Create a group on parent Composite, with the specified styles. <br>
	 * Buttons with internal id will be created in the group.
	 * 
	 * @param id
	 * @param parent
	 * @param groupStyle
	 * @param buttonStyle
	 * @param data
	 * @return the created group.
	 */
	public static Group createRadioGroup(String[] id, Composite parent,
			int groupStyle, int buttonStyle, SelectionListener listener, ValuePool valuePool) {
		return createGroup(id, parent, groupStyle, buttonStyle | SWT.RADIO, listener, valuePool);
	}
	
	public static Group createRadioGroup(String[] id, Composite parent,
			int groupStyle, int buttonStyle, SelectionListener listener, ValuePool valuePool, String label) {
		return createGroup(id, parent, groupStyle, buttonStyle | SWT.RADIO, listener, valuePool, label);
	}

	/**
	 * Create a group on parent Composite, with the specified styles. <br>
	 * Buttons with internal id will be created in the group.
	 * 
	 * @param id
	 * @param parent
	 * @param groupStyle
	 * @param buttonStyle
	 * @param data
	 * @return the created group.
	 */
	public static Group createCheckGroup(String[] id, Composite parent,
			int groupStyle, int buttonStyle, SelectionListener listener, ValuePool valuePool) {
		return createGroup(id, parent, groupStyle, buttonStyle | SWT.CHECK, listener, valuePool);
	}
	
	public static Group createCheckGroup(String[] id, Composite parent,
			int groupStyle, int buttonStyle, SelectionListener listener, ValuePool valuePool, String label) {
		return createGroup(id, parent, groupStyle, buttonStyle | SWT.CHECK, listener, valuePool, label);
	}

	/**
	 * Get a button with the specified id in the group.
	 * 
	 * @param group
	 *            The target group.
	 * @param id
	 *            The specified id.
	 * @return the button with the specified id.
	 */
	public static Button getGroupButton(Group group, String id) {
		Control[] children = group.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Object childId = CommonHelper.getWidgetID(children[i]);
				if (childId != null && childId.equals(id))
					return (Button) children[i];
			}
		}
		return null;
	}

	/**
	 * Set a group's selection. A null will be returned if none of the buttons
	 * is selected.
	 * 
	 * @param group
	 *            the group whose selection will be set
	 * @param id
	 *            The id of target radio button, a first available button will
	 *            be set selected if this parameter is null.
	 * @param mustEnabled
	 *            The target radio button will not be selected if this value is
	 *            true
	 * @param mustSelectOne
	 *            One of the radio buttons must be selected, the target radio
	 *            button or the first button available.
	 * @param clearOld
	 *            The selected button will all set not selected before this set
	 *            selection.
	 * @return The selected button
	 */
	public static Button setSelectedButton(Group group, String id,
			boolean mustEnabled, boolean mustSelectOne, boolean clearOld) {
		if (clearOld)
			setSelectionAll(group, false);

		if (id == null)
			setFirstEnabledSelected(group, mustEnabled);

		Control[] children = group.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button child = (Button) children[i];
				String childId = CommonHelper.getWidgetID(child);
				if (childId != null && childId.equals(id)) {
					if (!mustEnabled || child.getEnabled()) {
						child.setSelection(true);
						return child;
					} else
						break;
				}
			}
		}

		if (mustSelectOne)
			return setFirstEnabledSelected(group, mustEnabled);

		return null;
	}

	/**
	 * Set a group's selection. The number of selected button will be returned.
	 * 
	 * @param group
	 *            the group whose selection will be set
	 * @param id
	 *            The id of target radio button, a first available button will
	 *            be set selected if this parameter is null.
	 * @param mustEnabled
	 *            The target radio button will not be selected if this value is
	 *            true
	 * @param mustSelectOne
	 *            One of the radio buttons must be selected, the target radio
	 *            button or the first button available.
	 * @param clearOld
	 *            The selected button will all set not selected before this set
	 *            selection.
	 * @return The number of selected button will be returned.
	 */
	public static int setMultiSelectedButton(Group group, String[] id,
			boolean mustEnabled, boolean mustSelectOne, boolean clearOld) {
		if (clearOld)
			setSelectionAll(group, false);

		if (id == null)
			setFirstEnabledSelected(group, mustEnabled);

		int selectedButtonCount = 0;

		Control[] children = group.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button child = (Button) children[i];
				String childId = CommonHelper.getWidgetID(child);
				if (childId != null && childId.equals(id)) {
					if (!mustEnabled || child.getEnabled()) {
						child.setSelection(true);
						selectedButtonCount++;
					}
				}
			}
		}

		if (selectedButtonCount == 0 && mustSelectOne) {
			setFirstEnabledSelected(group, mustEnabled);
			return 1;
		}

		return selectedButtonCount;
	}

	/**
	 * Get the internal id of a radio button.
	 * 
	 * @param button
	 * @return
	 */
	public static String getButtonId(Button button) {
		return CommonHelper.getWidgetID(button);
	}

	private static Button setFirstEnabledSelected(Group group, boolean mustEnabled) {
		Control[] children = group.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button child = (Button) children[i];
				if (!mustEnabled || child.getEnabled()) {
					child.setSelection(false);
					return child;
				}
			}
		}
		return null;
	}

	private static void setSelectionAll(Group group, boolean selection) {
		Control[] children = group.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button child = (Button) children[i];
				child.setSelection(false);
			}
		}
	}

	/**
	 * Get the first selected button in the group.
	 * 
	 * @param group
	 * @param mustEnable
	 * @return
	 */
	public static Button getSelectedButton(Group group, boolean mustEnable) {
		Control[] children = group.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button child = (Button) children[i];
				if (child.getSelection()) {
					if (mustEnable) {
						if (child.getEnabled())
							return child;
						else
							continue;
					} else {
						return child;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get a list of selected button in the group.
	 * 
	 * @param group
	 * @param mustEnable
	 * @return
	 */
	public static List<Button> getSelectedButtonList(Group group, boolean mustEnable) {
		ArrayList<Button> result = new ArrayList<Button>();
		Control[] children = group.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Button) {
				Button child = (Button) children[i];
				if (child.getSelection()) {
					if (!mustEnable || child.getEnabled()) {
						result.add(child);
					} else
						continue;
				}
			}
		}
		return result;
	}
}
