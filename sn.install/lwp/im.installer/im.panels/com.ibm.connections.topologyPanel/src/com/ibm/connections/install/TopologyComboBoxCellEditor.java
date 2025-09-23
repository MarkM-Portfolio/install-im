/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

public class TopologyComboBoxCellEditor extends ComboBoxCellEditor{
	
	
	public TopologyComboBoxCellEditor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TopologyComboBoxCellEditor(Composite parent, String[] items, int style) {
		super(parent, items, style);
		// TODO Auto-generated constructor stub
	}

	public TopologyComboBoxCellEditor(Composite parent, String[] items) {
		super(parent, items);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object doGetValue() {
	    return ((CCombo)this.getControl()).getText();
	}

	@Override
	public void doSetValue(final Object value) {
	    //Assert.isTrue(value instanceof String);
		
	    int selection = -1;
	    for (int i = 0; i < ((CCombo)this.getControl()).getItemCount(); i++) {
	        final String currentItem = ((CCombo)this.getControl()).getItem(i);
	        if (currentItem.equals(value.toString())) {
	            ((CCombo)this.getControl()).select(selection);	
	            break;
	        }
	    }
	    ((CCombo)this.getControl()).setText(value.toString());
	}
}
