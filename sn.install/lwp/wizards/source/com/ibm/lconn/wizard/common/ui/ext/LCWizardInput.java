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
package com.ibm.lconn.wizard.common.ui.ext;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;

public abstract class LCWizardInput{
	private WizardPageInputData data;
	private Control parent;
//	private Control control;
	private boolean visible;
	private int style;
	private boolean enabled;
	

	public LCWizardInput(Composite parent, WizardPageInputData data){
		this(parent, data, data.getStyle());
	}
	
	public void addListener(int eventTYpe, Listener listener){
		
	}
	
	public LCWizardInput(Composite parent, WizardPageInputData data, int style){
		this.parent = parent;
		this.data = data;
		this.style = style;
		createControl(parent);
		setDataPoolValue(data.getDefaultValue());
	}

	protected void setDataPoolValue(String val) {
		DataPool.setValue(data.getWizardId(), data.getId(), val);
	}
	
	protected String getDataPoolValue(){
		return DataPool.getValue(data.getWizardId(), data.getId());
	}

	public abstract void createControl(Composite parent);
	public abstract void updateData();

	public WizardPageInputData getData() {
		return data;
	}
	
	
	public Control getParent() {
		return parent;
	}
	
	
	public void setValue(String value){

	}

	public boolean isVisible(){
		return this.visible;
	}
	
	public abstract void setEnable(boolean enable, String... data);
	
	public boolean isEnabled(){
		return this.enabled;
	}
	
	protected void setVisibleValue(boolean visible){
		this.visible = visible;
	}
	
	protected void setEnabledValue(boolean enabled){
		this.enabled = enabled;
	}

	public abstract void setVisible(boolean visible);
	public abstract String getValue();

	/**
	 * @return the style
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(int style) {
		this.style = style;
	}
}
