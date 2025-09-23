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
package com.ibm.lconn.wizard.cluster.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.ResourcePool;
import com.ibm.lconn.wizard.common.ui.data.LCTableEntry;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;
import com.ibm.lconn.wizard.common.ui.data.WizardTableData;
import com.ibm.lconn.wizard.common.ui.ext.LCTable;

public class ClusterNodeListTable extends LCTable {

	private static final int ACTIVATE_ATTRIBUTE_EDITOR_KEY = SWT.F4;

	private LDAPMappingTableManager provider;

	public ClusterNodeListTable(Composite parent, WizardPageInputData data) {
		super(parent, data);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		final TableViewer tv = getTableViewer();
		provider = new LDAPMappingTableManager(getData(), tv);
		tv.setContentProvider(provider);
		tv.setLabelProvider(provider);
		tv.setCellModifier(provider);

		// Add accessibility
		tv.getTable().addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if (ACTIVATE_ATTRIBUTE_EDITOR_KEY == event.keyCode) {
					activateAttriEditor();
				}
			}
		});

		final Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		composite_1.setLayout(gridLayout_2);

		final Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		composite_2.setLayout(new GridLayout());

		final Composite composite = new Composite(composite_1, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.makeColumnsEqualWidth = true;
		gridLayout_1.numColumns = 3;
		composite.setLayout(gridLayout_1);

		Button removeNodeButton;

		final Button addNodeButton = new Button(composite, SWT.NONE);
		addNodeButton.setText("Add node");
		addNodeButton.addListener(SWT.Selection, new Listener(){

			public void handleEvent(Event event) {
				ClusterNodeDialog dialog = new ClusterNodeDialog(ResourcePool.getActiveShell());
				dialog.open();
			}});

		final Button editButton = new Button(composite, SWT.NONE);
		editButton.setText("Edit node");
		editButton.addListener(SWT.Selection, new Listener(){

			public void handleEvent(Event event) {
				ClusterNodeDialog dialog = new ClusterNodeDialog(ResourcePool.getActiveShell());
				dialog.open();
			}});
		removeNodeButton = new Button(composite, SWT.NONE);
		removeNodeButton.setText("Remove node");
		// tv.addSelectionChangedListener(new ISelectionChangedListener() {
		//
		// public void selectionChanged(SelectionChangedEvent event) {
		// TableViewer tv = getTableViewer();
		// TableItem[] selection = tv.getTable().getSelection();
		// if (selection != null && selection.length > 0) {
		// Object data = selection[0].getData();
		// if (data instanceof LCTableEntry) {
		// LCTableEntry mapping = (LCTableEntry) data;
		// }
		// }
		//
		// }
		// });

	}

	private void activateAttriEditor() {
		TableViewer tv = getTableViewer();
		TableItem[] selection = tv.getTable().getSelection();
		if (selection != null && selection.length > 0) {
			Object data = selection[0].getData();
			tv.editElement(data, 1);
		}
	}

	@Override
	public void updateData() {
		WizardTableData data = getData();
		TableViewer tv = getTableViewer();

		provider = new LDAPMappingTableManager(getData(), tv);
		tv.setContentProvider(provider);
		tv.setLabelProvider(provider);
		Object tableInput = DataPool.getComplexData(data.getWizardId(),
				"INPUT_clusterNodeTable");
		if (tableInput != null)
			tv.setInput(tableInput);
		else {
			tv.setInput(new ArrayList<LCTableEntry>());
		}

		tv.setCellModifier(provider);
		tv.refresh(true, true);
	}

}

class LDAPMappingTableManager implements ITableLabelProvider,
		IStructuredContentProvider, ICellModifier {

	private WizardTableData data;
	private TableViewer tv;

	public LDAPMappingTableManager(WizardTableData data, TableViewer tv) {
		this.data = data;
		this.tv = tv;
	}

	// ------------IStructuredContentProvider-----------
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		List<LCTableEntry> elements = (List<LCTableEntry>) DataPool
				.getComplexData(data.getWizardId(),
					"INPUT_clusterNodeTable");
		if (elements != null)
			return elements.toArray(new LCTableEntry[] {});
		else
			return new LCTableEntry[] {};
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	// ---------IStructuredContentProvider-----------

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element == null)
			return "";
		return getStrValue(element, columnIndex);
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

	// ============================================================================
	public boolean canModify(Object element, String property) {
		return CommonHelper.equals(data.getColumns()[1], property);
	}

	public Object getValue(Object element, String property) {
		int columnIndex = data.indexOfColumn(property);
		return getStrValue(element, columnIndex);
	}

	private String getStrValue(Object element, int columnIndex) {
		LCTableEntry ele = (LCTableEntry) element;
		return ele.getProperty(tv.getTable().getColumn(columnIndex).getText());
	}

	private void setStrValue(Object element, int columnIndex, String val) {
		LCTableEntry ele = (LCTableEntry) element;
		ele.setProperty(tv.getTable().getColumn(columnIndex).getText(), val);
	}

	public void modify(Object element, String property, Object value) {
		TableItem ti = (TableItem) element;
		int index = data.indexOfColumn(property);
		Object mappingData = ti.getData();
		setStrValue(mappingData, index, (String) value);
		// DefaultWizardDataLoader.loadMappingData().list(System.out);
		tv.update(mappingData, null);
	}

}
