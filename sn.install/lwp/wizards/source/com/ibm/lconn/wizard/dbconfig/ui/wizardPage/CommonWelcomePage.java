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
package com.ibm.lconn.wizard.dbconfig.ui.wizardPage;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public abstract class CommonWelcomePage extends CommonPage {
	public static int INFORCENTER = 1;
	public static int VIEW_LOG = 2;
	public static int NONE = 3;
	public static final Logger logger = LogUtil
			.getLogger(CommonWelcomePage.class);

	static int MARGIN = 5;
	static int offset = 0;
	static Button button;
	private String title;
	private String description;
	private String[] text;
	private int actionType;
	private String path;
	private String actionLabel;
	private int[] boldStart;
	private int[] boldLength;
	private int actionLine;

	/**
	 * Create the wizard
	 */
	protected CommonWelcomePage(String pageName) {
		super(pageName);
	}

	protected void combineYourPanel(String title, String description,
			String[] text, int actionType, String path, String actionLabel,
			int actionLine, int[] boldStart, int[] boldLength) {
		this.title = title;
		this.description = description;
		this.text = text;
		this.actionType = actionType;
		this.path = path;
		this.actionLabel = actionLabel;
		this.actionLine = actionLine;
		this.boldStart = boldStart;
		this.boldLength = boldLength;
	}

	protected abstract void show();

	/**
	 * Create contents of the wizard
	 * 
	 * @param parent
	 */
	public void onShow(Composite parent) {
		show();
		super.setTitle(this.title);
		if (this.description != null) {
			Label dec = new Label(parent, SWT.WRAP);
			dec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			((GridData) (dec.getLayoutData())).horizontalIndent = 5;
			((GridData) (dec.getLayoutData())).verticalIndent = 5;
			dec.setText(this.description);
		}

		final Composite panel = CommonHelper.createScrollableControl(
				Composite.class, parent, SWT.BORDER, SWT.V_SCROLL);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		panel.setLayout(layout);

		int length = this.text.length;
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				StyledText text = new StyledText(panel, SWT.MULTI
						| SWT.READ_ONLY | SWT.WRAP);
				text.setCursor(this.getWizard().getParentDialog().getShell()
						.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
				GridData grid = new GridData(SWT.FILL, SWT.FILL, true, true);
				text.setLayoutData(grid);
				String showText = this.text[i];
				text.setText(showText);
				// text.setFont(new Font(this.getShell().getDisplay(), "Tahoma",
				// 8,
				// SWT.NORMAL));			

				if (this.actionType != NONE) {
					button = new Button(text, SWT.PUSH);
					button.setText(this.actionLabel);
					if (this.actionType == INFORCENTER) {
						button.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent event) {
								CommonHelper.openHTML(path);
							}
						});
					} else if (this.actionType == VIEW_LOG) {
						button.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent event) {
								org.eclipse.swt.program.Program.launch(path);
							}
						});
					}
					offset = showText.indexOf("\uFFFC");
					StyleRange style = new StyleRange();
					style.start = offset;
					style.length = 1;
					button.pack();
					Rectangle rect = button.getBounds();
					int ascent = 2 * rect.height / 3;
					int descent = rect.height - ascent;
					style.metrics = new GlyphMetrics(ascent + MARGIN, descent
							+ MARGIN, rect.width + 2 * MARGIN);
					text.setStyleRange(style);
					if (this.actionLine > 0)
						text.setLineAlignment(this.actionLine, 1, SWT.CENTER);
					button.setVisible(false);
					button.setEnabled(false);
				}

				if (this.boldStart != null && this.boldStart.length > 0)
					for (int j = 0; j < this.boldStart.length; j++) {
						StyleRange boldstyle = new StyleRange();
						boldstyle.start = this.boldStart[j];
						boldstyle.length = this.boldLength[j];
						boldstyle.fontStyle = SWT.BOLD;
						text.setStyleRange(boldstyle);
					}

				// reposition widgets on paint event
				text.addPaintObjectListener(new PaintObjectListener() {
					public void paintObject(PaintObjectEvent event) {
						StyleRange style = event.style;
						int start = style.start;
						if (start == offset) {
							button.setVisible(true);
							button.setEnabled(true);
							Point pt = button.getSize();
							int x = event.x + MARGIN;
							int y = event.y + event.ascent - 2 * pt.y / 3;
							button.setLocation(x, y);
						}
					}
				});
			} else {
				StyledText copyRightText = new StyledText(panel, SWT.MULTI
						| SWT.READ_ONLY | SWT.WRAP);
				copyRightText.setCursor(this.getWizard().getParentDialog()
						.getShell().getDisplay().getSystemCursor(
								SWT.CURSOR_ARROW));

				GridData grid = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
				grid.verticalIndent = 0;
				copyRightText.setLayoutData(grid);
				String copyright = this.text[i];				
				StyleRange styleRange = new StyleRange();
				styleRange.start = 0;
				styleRange.length = copyright.length();
				styleRange.fontStyle = SWT.BOLD;
				copyRightText.setStyleRange(styleRange);
				copyRightText.setText(copyright);
			}
		}
	}
}
