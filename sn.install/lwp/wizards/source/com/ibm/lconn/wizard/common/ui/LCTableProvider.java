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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.ibm.lconn.wizard.common.ui.data.WizardTableData;

public class LCTableProvider implements ITableLabelProvider, IStructuredContentProvider {

	private WizardTableData data;

	public LCTableProvider(WizardTableData data){
		this.data = data;
	}
	

	public int eval(Object element){
		return (Integer)element;
	}
	
	public WizardTableData getInput(){
		return data;
	}
	

	//-----------------------IStructuredContentProvider -------------------------
	public Object[] getElements(Object inputElement) {
		Integer[] rowId = new Integer[data.getRowCount()];
		for (int i = 0; i < rowId.length; i++) {
			rowId[i] = i;
		}
		return rowId;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	//-----------------------IStructuredContentProvider -------------------------
	
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		int row = eval(element);
		return data.getValue(columnIndex, row);
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	

}
