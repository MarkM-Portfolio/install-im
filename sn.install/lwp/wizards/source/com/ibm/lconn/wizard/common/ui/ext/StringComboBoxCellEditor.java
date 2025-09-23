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

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.ibm.lconn.wizard.common.Assert;

public class StringComboBoxCellEditor extends ComboBoxCellEditor{
	String oldValue;
    public StringComboBoxCellEditor() {
        super();
    }

    public StringComboBoxCellEditor(Composite parent, String[] items) {
        super(parent, items);
    }

    public StringComboBoxCellEditor(Composite parent, String[] items, int style) {
        super(parent, items, style);
    }
    
    public void cancelComboSelection(){
    	CCombo combo = getControl();
		combo.select(combo.indexOf(oldValue));
		combo.setText(oldValue);
    }
    
    public void applyComboSelection(){
    	oldValue = getControl().getText();
    }


	@Override
	protected Object doGetValue() {
		CCombo combo = getControl();
		return combo.getText();
	}
    
    public CCombo getControl(){
    	Control superCombo = super.getControl();
		Assert.isTrue(superCombo!=null);
    	return (CCombo) superCombo;
    }

	@Override
	protected void doSetValue(Object value) {
		CCombo combo = getControl();
		if(value instanceof String){
			String strVal = (String) value;
			strVal = "".equals(strVal) ? "null" : strVal;
			combo.setText(strVal);
			oldValue = strVal;
		}else if(value instanceof Integer){
			int intVal = (Integer) value;
			combo.select(intVal);
			oldValue = combo.getText();
		}else{
			Assert.isTrue(value instanceof String || value instanceof Integer);
		}
	}
    
}
