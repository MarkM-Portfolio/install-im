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

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.ibm.lconn.wizard.common.Assert;
import com.ibm.lconn.wizard.common.MessageUtil;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;
import com.ibm.lconn.wizard.common.ui.data.WizardTableData;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class LCTable extends LCWizardInput {

	private TableViewer tv;


	public LCTable(Composite parent, WizardPageInputData data) {
		super(parent, data);
		Assert.isTrue(data instanceof WizardTableData);
		setDataPoolValue(data.getId());
	}
	
	public WizardTableData getData(){
		return (WizardTableData)super.getData();
	}

	/* (non-Javadoc)
	 * @see com.ibm.lconn.wizard.common.ui.ext.LCInput#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		createTableViewer(parent);
//		LCTableProvider wizardTableProvider = new LCTableProvider(getData());
//		tv.setContentProvider(wizardTableProvider);
//		tv.setLabelProvider(wizardTableProvider);
//		tv.setInput(wizardTableProvider.getInput());
	}

	private void createTableViewer(Composite parent) {
		tv = new TableViewer(parent, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);

		final Table table = tv.getTable();
		GridData gridData = new GridData(GridData.FILL_BOTH);
		
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableLayout tLayout = new TableLayout();
		table.setLayout(tLayout);
		
		

		String[] columnsArry = getData().getColumns();
		tv.setColumnProperties(columnsArry);
		for (int i = 0; i < columnsArry.length; i++) {
			String columnName = MessageUtil.getLabel(columnsArry[i]);
			addColumn(table, columnName);
		}
	}
	
	private TableColumn addColumn(Table table, String columnName) {
		int width = 20;//columnName.length()*18;
		ColumnWeightData columnWeightData = new ColumnWeightData(10);
		((TableLayout) table.getLayout()).addColumnData(columnWeightData);
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setText(columnName);
		tableColumn.setWidth(width);
		return tableColumn;
	}
	
	public TableViewer getTableViewer(){
		return tv;
	}


	/* (non-Javadoc)
	 * @see com.ibm.lconn.wizard.common.ui.ext.LCInput#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		this.tv.getTable().setVisible(visible);
	}

	@Override
	public String getValue() {
		return getData().getId();
	}

	@Override
	public void updateData() {
		
	}

	@Override
	public void setEnable(boolean enable, String... data) {
		this.setEnabledValue(enable);
		if(this.tv!=null) this.tv.getTable().setEnabled(enable);
	}
}
