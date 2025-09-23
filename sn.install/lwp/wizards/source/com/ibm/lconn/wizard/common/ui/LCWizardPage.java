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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.ui.data.DefaultWizardDataLoader;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;
import com.ibm.lconn.wizard.common.ui.ext.LCWizardInput;

public class LCWizardPage extends WizardPage {

	private WizardPageData data;
	private List<Listener> pageRenderListener;
	private int disableStyle = Constants.DISABLE_NONE;
	private Label imageLabel;
	private Label descLabel;
	private String descTxt;

	public LCWizardPage(WizardPageData data) {
		super(CommonHelper.isEmpty(data.getName()) ? data.getId() : data
				.getName());
		this.data = data;
	}

	public void setDescription(String description) {
		if (this.descLabel != null)
			setDescriptionText(description);
		super.setDescription("");
	}

	public void createControl(Composite parent) {
		Composite rootPanel = new Composite(parent, SWT.NONE);
		rootPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		CommonHelper.setColumn(rootPanel, 2);

		Composite rootPanelInner;
		if (data.getInputsId() != null
				&& data.getInputsId().length > 0
				&& DataPool.getWizardPageInput(data.getWizardId(),
						data.getInputsId()[0]).getClassName().equals(
						"STYLED_TEXT_BUTTON")) {
			rootPanelInner = new Composite(rootPanel, SWT.NONE);
		} else {
			rootPanelInner = CommonHelper.createScrollableControl(
					Composite.class, rootPanel, SWT.NONE, SWT.H_SCROLL
							| SWT.V_SCROLL);

		}

		CommonHelper.setColumn(rootPanelInner, 2);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumWidth = 200;
		gridData.widthHint = 200;
		gridData.grabExcessHorizontalSpace = true;
		rootPanelInner.setLayoutData(gridData);

		createControls(rootPanelInner);

		createImage(rootPanel);

		setValues();

		super.setDescription("");

		setControl(rootPanel);
	}

	protected Composite createControls(Composite rootPanel) {
		final Composite composite = new Composite(rootPanel, SWT.NONE);
		CommonHelper.setColumn(composite, 1);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gridData.horizontalIndent = 10;
		composite.setLayoutData(gridData);

		createDescLabel(composite);

		String[] inputsId = data.getInputsId();
		Composite control = DefaultWizardDataLoader.createWidget(composite,
				data.getWizardId(), inputsId);
		return control;
	}

	protected void createDescLabel(final Composite composite) {
		descLabel = new Label(composite, SWT.WRAP);
		final GridData gridData_2 = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		descLabel.setLayoutData(gridData_2);
		String desc = getData().getDesc();
		if (desc != null) {
			setDescriptionText(desc);
		}
		final Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("\n");
	}

	private void setDescriptionText(String description) {
		setDescTxt(description);
		descLabel.setText(description);
	}

	protected void createImage(Composite rootPanel) {
		imageLabel = new Label(rootPanel, SWT.NONE);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, true);
		gridData.horizontalIndent = 10;
		imageLabel.setLayoutData(gridData);
		imageLabel.setText("imageHere");
	}

	private void setValues() {
		try {
			imageLabel.setImage(ResourcePool.getWizardSideImage());
		} catch (Exception e) {

		}
	}

	public LCWizard getWizard() {
		return (LCWizard) super.getWizard();
	}

	public void setControl(Control com) {
		com.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event) {
				firePageRenderAction();
				updatePageData();
			}

		});
		super.setControl(com);
	}

	public void collectInput() {
		DefaultWizardDataLoader.collectPageInput(this);
	}

	/**
	 * @return the data
	 */
	public WizardPageData getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(WizardPageData data) {
		this.data = data;
	}

	protected void updatePageData() {
		// Control control = getControl();
		// Composite parent = control.getParent();
		// control.dispose();
		// createControl(parent);
		// parent.layout();
		setTitle(data.getTitle());
		setDescription(data.getDesc());
		// update inputs
		String[] inputsId = data.getInputsId();
		if (inputsId != null) {
			for (String inputId : inputsId) {
				LCWizardInput input = DataPool.getWizardInputWidget(data
						.getWizardId(), inputId);
				if (input != null)
					input.updateData();
			}
		}
		this.getControl().getParent().layout(true, true);
	}

	public void addPageRenderListener(Listener listener) {
		if (this.pageRenderListener == null)
			this.pageRenderListener = new ArrayList<Listener>();
		this.pageRenderListener.add(listener);
	}

	public void firePageRenderAction() {
		if (pageRenderListener != null) {
			Iterator<Listener> iterator = pageRenderListener.iterator();
			while (iterator.hasNext()) {
				Event event = new Event();
				event.data = this;
				iterator.next().handleEvent(event);
			}
		}
	}

	public IWizardPage getNextPage() {
		if (CommonHelper.isStyle(disableStyle, Constants.DISABLE_NEXT))
			return null;
		return super.getNextPage();
	}

	public IWizardPage getPreviousPage() {
		if (CommonHelper.isStyle(disableStyle, Constants.DISABLE_BACK))
			return null;
		return super.getPreviousPage();
	}

	protected String getDescTxt() {
		return descTxt;
	}

	protected void setDescTxt(String descTxt) {
		this.descTxt = descTxt;
	}

	// public void disableButton(int disable){
	// disableStyle |= disable;
	// if(CommonHelper.isStyle(disableStyle, Constants.DISABLE_CANCEL)){
	// getWizard().getParentDialog().getTheCancelButton().setEnabled(false);
	// }
	// }

}
