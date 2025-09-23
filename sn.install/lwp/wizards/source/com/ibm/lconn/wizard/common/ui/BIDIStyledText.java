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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ibm.lconn.wizard.common.interfaces.EmbeddedAction;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class BIDIStyledText {
	private final String BUTTON_MAKR = "\uFFFC";
	private final String LINE_SPEPARATOR = "\n";
	private EmbeddedAction action;
	private Label description;
	private Composite panel;

	public BIDIStyledText(Composite parent, String description, String topText,
			String bottomText, EmbeddedAction theAction, int[] boldLines) {
		this.action = theAction;
		if (description != null && !"".equals(description)) {
			this.description = new Label(parent, SWT.WRAP);
			this.description.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false));
			((GridData) (this.description.getLayoutData())).horizontalIndent = 5;
			((GridData) (this.description.getLayoutData())).verticalIndent = 5;
			this.description.setText(description);
		}

		this.panel = CommonHelper.createScrollableControl(Composite.class,
				parent, SWT.BORDER, SWT.V_SCROLL);
		
		Listener listener = CommonHelper.scrollListener;
		/*
		 * ScrollBar vBar = panel.getVerticalBar(); ScrollBar hBar =
		 * panel.getHorizontalBar();
		 */
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 1;
		panel.setLayout(layout);

		StyledText style = new StyledText(panel, SWT.READ_ONLY);
		GridData styleGrid = new GridData();
		styleGrid.exclude = true;
		style.setVisible(false);
		style.setLayoutData(styleGrid);
		Color color = style.getBackground();
		CommonHelper.setTextBackgroundColor(color);
		panel.setBackground(color);

		String[] lines = topText.split(LINE_SPEPARATOR);

		int i = 0;
		for (String line : lines) {
			if (exists(BUTTON_MAKR, line) && action != null) {
				Button button = new Button(panel, SWT.NONE);
				button.setText(action.getActionLabel());

				button.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						action.execute();
					}
				});
				
				button.addListener(SWT.Activate, listener);

				button.setLayoutData(new GridData(this.action
						.getHorizontalAlignment(), this.action
						.getVerticalAlignment(), true, false));
			} else {
				Text text = new Text(panel, SWT.WRAP);
				text.setEditable(false);
				text.setBackground(color);
				text.setText(line);
				text.addListener(SWT.Activate, listener);
				if (exists(i, boldLines)) {
					text.setFont(ResourcePool.getBoldFont(text));
				}
				text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
						false));
			}

			i++;
		}

		if (bottomText != null && !"".equals(bottomText)) {
			Text text = new Text(panel, SWT.WRAP);
			text.setEditable(false);
			text.setBackground(color);
			text.setText(bottomText);
			text.addListener(SWT.Activate, listener);
			text.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
			text.setFont(new Font(null, "Arial", 8, SWT.ITALIC));
		} else {
			// add a bottom line to the text.
			Text text = new Text(panel, SWT.WRAP);
			text.setEditable(false);
			text.setBackground(color);
			text.setText("");
			text.addListener(SWT.Activate, listener);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					false));
		}

	}

	public void dispose() {
		if (this.description != null) {
			this.description.dispose();
		}
		if (this.panel != null) {
			this.panel.getParent().dispose();
		}
	}

	private boolean exists(int v, int[] arr) {
		if (arr == null)
			return false;

		for (int t : arr) {
			if (v == t)
				return true;
		}

		return false;
	}

	private boolean exists(String s, String t) {

		if (t.indexOf(s) >= 0)
			return true;

		return false;
	}
}
