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
package com.ibm.lconn.wizard.tdipopulate.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.Util;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.common.ui.data.WizardDropDownData;
import com.ibm.lconn.wizard.common.ui.data.WizardPageInputData;
import com.ibm.lconn.wizard.common.ui.data.WizardTableData;
import com.ibm.lconn.wizard.common.ui.ext.LCTable;
import com.ibm.lconn.wizard.common.ui.ext.StringComboBoxCellEditor;
import com.ibm.lconn.wizard.tdipopulate.backend.AttributeMapping;

public class LDAPMappingTable extends LCTable {

	private static final int ACTIVATE_ATTRIBUTE_EDITOR_KEY = SWT.F4;
	private static final String INPUT_LDAP_ATTRI = "INPUT_LDAP_ATTRI";
	public static final String SELECTED_JS = "LDAPMappingTable.selectedJS";

	private LDAPMappingTableManager provider;
	protected boolean autoPopupMute;
	private StringComboBoxCellEditor comboEditor;
	private WizardDropDownData dropDown;
	protected String selectedAttribute;

	public LDAPMappingTable(Composite parent, WizardPageInputData data) {
		super(parent, data);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		final TableViewer tv = getTableViewer();
		dropDown = new WizardDropDownData(INPUT_LDAP_ATTRI);
		comboEditor = new StringComboBoxCellEditor(tv.getTable(), dropDown
				.getLabels(), SWT.READ_ONLY);
		setCombo(tv, dropDown, comboEditor);
		provider = new LDAPMappingTableManager(getData(), tv);
		tv.setContentProvider(provider);
		tv.setLabelProvider(provider);
		CellEditor[] editors = { null, comboEditor, null };
		tv.setCellEditors(editors);
		tv.setCellModifier(provider);

		// Add accessibility
		tv.getTable().addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if (ACTIVATE_ATTRIBUTE_EDITOR_KEY == event.keyCode) {
					activateAttriEditor();
				}
			}
		});
		

		tv.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				TableViewer tv = getTableViewer();
				TableItem[] selection = tv.getTable().getSelection();
				if (selection != null && selection.length > 0) {
					Object data = selection[0].getData();
					if (data instanceof AttributeMapping) {
						AttributeMapping mapping = (AttributeMapping) data;
						selectedAttribute = mapping.getAttribute();
					}
				}

			}
		});
		
		final boolean[] sortTable = new boolean[tv.getTable().getColumnCount()];
		int columnCount = tv.getTable().getColumnCount();
		for(int i=0; i< columnCount; i++) {
			 final int index = i;
			 TableColumn tc = tv.getTable().getColumn(i);
			 tc.addListener(SWT.Selection, new Listener() {
			      public void handleEvent(Event event) {
			        tv.setSorter(new MappingSort(sortTable[index], index));
			        sortTable[index] = !sortTable[index];
			      }
			 });
		}

	}
	
	private class MappingSort extends ViewerSorter {
		boolean order;
		int index;
		Collator collator = Collator.getInstance();
		public MappingSort(boolean order, int index) {
			this.order = order;
			this.index = index;
		}
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			String s1 = provider.getColumnText(e1, index);	
			String s2 = provider.getColumnText(e2, index);
			
			int factor = order? 1 : -1;
			
			return collator.compare(s1, s2) * factor;
		}

	}
	
	private void activateAttriEditor() {
		TableViewer tv = getTableViewer();
		TableItem[] selection = tv.getTable().getSelection();
		if (selection != null && selection.length > 0) {
			Object data = selection[0].getData();
			tv.editElement(data, 1);
		}
	}

	private void setCombo(final TableViewer tv,
			final WizardDropDownData dropDown,
			final StringComboBoxCellEditor comboEditor) {
		final CCombo combo = comboEditor.getControl();
		combo.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				switch (event.keyCode) {
				case SWT.ARROW_DOWN:
				case SWT.ARROW_UP:
					autoPopupMute = true;
					break;
				case SWT.CR:
					event.doit = !handleException();
				}

			}
		});

		combo.addListener(SWT.KeyUp, new Listener() {

			public void handleEvent(Event event) {
				switch (event.keyCode) {
				case SWT.ARROW_DOWN:
				case SWT.ARROW_UP:
					autoPopupMute = false;
					break;
				}
			}
		});
		combo.addListener(SWT.DefaultSelection, new Listener() {

			public void handleEvent(Event event) {
				//System.out.println("default");

			}
		});

		combo.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				/*System.out.println("Selected: "
						+ comboEditor.getControl().getText());*/
				handleSelection();
			}
		});
	}

	@Override
	public void updateData() {
		WizardTableData data = getData();
		TableViewer tv = getTableViewer();

		dropDown = new WizardDropDownData(INPUT_LDAP_ATTRI);
		comboEditor = new StringComboBoxCellEditor(tv.getTable(), dropDown
				.getLabels(), SWT.READ_ONLY);
		setCombo(tv, dropDown, comboEditor);
		provider = new LDAPMappingTableManager(getData(), tv);
		tv.setContentProvider(provider);
		tv.setLabelProvider(provider);
		Object tableInput = DataPool.getComplexData(data.getWizardId(),
				Constants.INPUT_TDI_MAPPING_TABLE);
		if (tableInput != null)
			tv.setInput(tableInput);
		else {

			tv.setInput(new ArrayList<AttributeMapping>());
		}

		CellEditor[] editors = { null, comboEditor, null };
		tv.setCellEditors(editors);
		tv.setCellModifier(provider);
		tv.refresh(true, true);
		
		tv.getTable().getColumns()[0].pack();
		tv.getTable().getColumns()[1].pack();
	}

	private void handleSelection() {
		CCombo combo = comboEditor.getControl();
		String val = combo.getText();
		int index = dropDown.indexOfLabel(val);
		if (index != -1)
			val = dropDown.getValues()[index];
		String[] exception = { "INPUT_LDAP_ATTRI.NULL",
				"INPUT_LDAP_ATTRI.MoreAttri", "INPUT_LDAP_ATTRI.MoreJS" };

		if (-1 == Util.indexOf(exception, val)) {
			comboEditor.applyComboSelection();
		}
		handleException();
	}

	private boolean handleException() {
		boolean handled = false;
		if (autoPopupMute)
			return handled;
		CCombo combo = comboEditor.getControl();
		String val = combo.getText();
		int index = dropDown.indexOfLabel(val);
		if (index != -1)
			val = dropDown.getValues()[index];

		if (CommonHelper.equals(val, "INPUT_LDAP_ATTRI.NULL")) {
			comboEditor.cancelComboSelection();
		} else if (CommonHelper.equals(val, "INPUT_LDAP_ATTRI.MoreAttri")) {
			// More Attri
			comboEditor.cancelComboSelection();
			String text = combo.getText();
			LDAPAttributesDialog dialog = new LDAPAttributesDialog(null);
			dialog.setSelectedAttri(text);
			int open = dialog.open();
			if (open == Window.OK) {
				if (dialog.getSelectedAttri() != null)
					combo.setText(dialog.getSelectedAttri());

			} else {
				combo.setText(selectedAttribute);
				getTableViewer().cancelEditing();
			}
			handled = true;
		} else if (CommonHelper.equals(val, "INPUT_LDAP_ATTRI.MoreJS")) {
			// More JS
			comboEditor.cancelComboSelection();
			String text = combo.getText();
			LDAPJSDialog dialog = new LDAPJSDialog(null);
			if (text.startsWith("{") && text.endsWith("}")) {
				text = text.substring(1, text.length() - 1);
			}
			dialog.setSelectedJS(text);
			int open = dialog.open();
			//System.out.println("@@" + combo.getText());
			if (Window.OK == open) {
				combo.setText("{" + dialog.getSelectedFunction() + "}");
			} else {
				combo.setText(selectedAttribute);
				getTableViewer().cancelEditing();
			}
			//System.out.println("JS? : " + combo.getText());
			handled = true;
		}

		updateTable(combo.getText());
		comboEditor.applyComboSelection();
		return handled;
	}

	private void updateTable(String val) {
		TableViewer tv = getTableViewer();
		Table table = tv.getTable();
		TableItem[] selections = table.getSelection();
		Object data2 = selections[0].getData();
		AttributeMapping am = (AttributeMapping) data2;
		am.setAttribute(val);
		//System.out.println("Attribute: " + am.getAttribute());
		tv.update(data2, null);
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
		List<AttributeMapping> elements = (List<AttributeMapping>) DataPool
				.getComplexData(data.getWizardId(),
						Constants.INPUT_TDI_MAPPING_TABLE);
		if (elements != null)
			return elements.toArray(new AttributeMapping[] {});
		else
			return new AttributeMapping[] {};
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
		AttributeMapping ele = (AttributeMapping) element;
		switch (columnIndex) {
		case 0:
			return ele.getDbField();
		case 1:
			String attr = ele.getAttribute();
			return "null".equals(attr)? "" : attr;
		case 2:
			// return ele.getValidationRule();
			return getDbFieldHint(ele.getDbField());
		default:
			return "";
		}
	}

	private void setStrValue(Object element, int columnIndex, String val) {
		AttributeMapping ele = (AttributeMapping) element;
		switch (columnIndex) {
		case 0:
			ele.setDbField(val);
			break;
		case 1:
			ele.setAttribute(val);
			break;
		case 2:
			// ele.setValidationRule(val);
			// do nothing, the column is not editable
			break;
		default:
		}

	}

	public void modify(Object element, String property, Object value) {
		TableItem ti = (TableItem) element;
		int index = data.indexOfColumn(property);
		Object mappingData = ti.getData();
		setStrValue(mappingData, index, (String) value);
		// DefaultWizardDataLoader.loadMappingData().list(System.out);
		tv.update(mappingData, null);
	}
	
	private String getDbFieldHint(String dbField) {
		
		return Messages.getString("profiles." + hintMap.get(dbField) + ".hint");
	}

	private static Map<String, String> hintMap = new HashMap<String, String>();
	static {
	  hintMap.put("alternateLastname","PROF_ALTERNATE_LAST_NAME");
	  hintMap.put("blogUrl","PROF_BLOG_URL");
	  hintMap.put("bldgId","PROF_BUILDING_IDENTIFIER");
	  hintMap.put("calendarUrl","PROF_CALENDAR_URL");
	  hintMap.put("courtesyTitle","PROF_COURTESY_TITLE");
	  hintMap.put("deptNumber","PROF_DEPARTMENT_NUMBER");
	  hintMap.put("description","PROF_DESCRIPTION");
	  hintMap.put("displayName","PROF_DISPLAY_NAME");
	  hintMap.put("employeeNumber","PROF_EMPLOYEE_NUMBER");
	  hintMap.put("employeeTypeCode","PROF_EMPLOYEE_TYPE");
	  hintMap.put("experience","PROF_EXPERIENCE");
	  hintMap.put("faxNumber","PROF_FAX_TELEPHONE_NUMBER");
	  hintMap.put("freeBusyUrl","PROF_FREEBUSY_URL");
	  hintMap.put("floor","PROF_FLOOR");
	  hintMap.put("groupwareEmail","PROF_GROUPWARE_EMAIL");
	  hintMap.put("guid","PROF_GUID");
	  hintMap.put("ipTelephoneNumber","PROF_IP_TELEPHONE_NUMBER");
	  hintMap.put("countryCode","PROF_ISO_COUNTRY_CODE");
	  hintMap.put("isManage","PROF_IS_MANAGER");
	  hintMap.put("jobResp","PROF_JOB_RESPONSIBILITIES");
	  hintMap.put("email","PROF_MAIL");
	  hintMap.put("managerUid","PROF_MANAGER_UID");
	  hintMap.put("mobileNumber","PROF_MOBILE");
	  hintMap.put("nativeFirstName","PROF_NATIVE_FIRST_NAME");
	  hintMap.put("nativeLastName","PROF_NATIVE_LAST_NAME");
	  hintMap.put("orgId","PROF_ORGANIZATION_IDENTIFIER");
	  hintMap.put("pagerNumber","PROF_PAGER");
	  hintMap.put("pagerId","PROF_PAGER_ID");
	  hintMap.put("pagerServiceProvider","PROF_PAGER_SERVICE_PROVIDER");
	  hintMap.put("pagerType","PROF_PAGER_TYPE");
	  hintMap.put("officeName","PROF_PHYSICAL_DELIVERY_OFFICE");
	  hintMap.put("preferredFirstName","PROF_PREFERRED_FIRST_NAME");
	  hintMap.put("preferredLanguage","PROF_PREFERRED_LANGUAGE");
	  hintMap.put("preferredLastName","PROF_PREFERRED_LAST_NAME");
	  hintMap.put("secretaryUid","PROF_SECRETARY_UID");
	  hintMap.put("shift","PROF_SHIFT");
	  hintMap.put("distinguishedName","PROF_SOURCE_UID");
	  hintMap.put("telephoneNumber","PROF_TELEPHONE_NUMBER");
	  hintMap.put("timezone","PROF_TIMEZONE");
	  hintMap.put("title","PROF_TITLE");
	  hintMap.put("uid","PROF_UID");
	  hintMap.put("workLocationCode","PROF_WORK_LOCATION");
	  hintMap.put("surname","PROF_SURNAME");
	  hintMap.put("surnames","PROF_SURNAMES");
	  hintMap.put("givenName","PROF_GIVEN_NAME");
	  hintMap.put("givenNames","PROF_GIVEN_NAMES");
	  hintMap.put("loginId","PROF_LOGIN");
	  hintMap.put("logins","PROF_LOGINS");
 
	}
}
