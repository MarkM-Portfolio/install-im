/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */

package com.ibm.lconn.wizard.update.data;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**********************************************************************************************************
 * Class: EFixDataModel.java
 * Abstract: The eFix table content model which includes a selector, eFix ID, installDate, and
 * installStatus.
 * 
 * 
 * Selector | eFix ID | installDate | installStatus
 *    
 * 
 *
 * Component Name: WAS.ptf
 * Release: ASV50X
 * 
 * History 1.2, 1/29/04
 *
 * 
 * 
 * 
 * 01-Nov-2002 Initial Version
 **********************************************************************************************************/

public class EFixDataModel extends AbstractTableModel {

	//***********************************************************
	// Program Versioning
	//***********************************************************
	public static final String pgmVersion = "1.2" ;
	//***********************************************************
	// Program Versioning
	//***********************************************************
	public static final String pgmUpdate = "1/29/04" ;

	//***********************************************************
	// Instance State
	//***********************************************************
	private static String actionType;

	//table display properties
	public final static int SELECT = 0;
	public final static int EFIX_ID = 1;
	public final static int INSTALL_DATE = 2;
	public final static int INSTALL_STATE = 3;

	private final static boolean SELECT_STATE_OFF = false;
	private final static boolean SELECT_STATE_ON = true;

	private String selectInstall =
		InstallerMessages.getString("label.column.install");
	private String selectUninstall =
		InstallerMessages.getString("label.column.uninstall");
	private String name = InstallerMessages.getString("label.column.name");
	private String date = InstallerMessages.getString("label.column.date");
	private String status = InstallerMessages.getString("label.column.status");

	public EFixDataModel(String actionType) {
		this.actionType = actionType;
		m_vector = new Vector();
	}

	private final String[] columnNames = { selectInstall, name, date, status };

	public static boolean emptyList = true;

	private static Vector m_vector;

	protected int m_sortCol = 0;
	protected boolean m_sortAsc = true;

	//***********************************************************
	// Method Definitions
	//***********************************************************

	public int getRowCount() {
		return m_vector.size();
	}

	public int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int row, int column) {

		if (row < 0 || row >= getRowCount())
			return "";

		UpdateListingData uld = (UpdateListingData) m_vector.elementAt(row);
		switch (column) {
			case 0 :
				return uld.getSelectState();
			case 1 :
				return uld.getUpdateComponent().getIdStr();
			case 2 :
				return com.ibm.websphere.update.ioservices.CalendarUtil.formatEfixBuildDate( uld.getUpdateComponent().getInstallDate() );
			case 3 :
				return new IconIdentifier(
					uld.getUpdateComponent().getInstallState());
		}
		return "";
	}

	public static void resetTableDataVector() {
		m_vector.removeAllElements();
	}

	public static Vector getTableDataVector() {
		return m_vector;
	}

	//Use only to toggle the check box selections
	public void setValueAt(Object value, int row, int column) {
		UpdateListingData uld = (UpdateListingData) m_vector.elementAt(row);
		switch (column) {
			case 0 :
				uld.setSelectState((Boolean) value);
		}
	}

	public void setEfixData(Object efixData) {
		m_vector.addElement(efixData);
	}

	public boolean isCellEditable(int row, int column) {
		if (column == SELECT) {
			return true;
		} else {
			return false;
		}
	}

	public String getColumnName(int column) {
		if (column == 0 && actionType.equals("uninstaller")) {
			columnNames[column] = selectUninstall;
		} else if (column == 0 && actionType.equals("installer")) {
			columnNames[column] = selectInstall;
		}

		String colName = columnNames[column];

		//if (column == m_sortCol)
		//	colName += m_sortAsc ? " #" : " #";

		return colName;
	}

	public Class getColumnClass(int column) {
		Class dataType = super.getColumnClass(column);
		if (column == SELECT) {
			dataType = Boolean.class;
		} else if (column == INSTALL_STATE) {
			dataType = IconIdentifier.class;
		}

		return dataType;
	}

	public MouseAdapter getColumnListener(JTable table){
		return	new ColumnListener(table);
	}

	class ColumnListener extends MouseAdapter {
		protected JTable m_table;

		public ColumnListener(JTable table) {
			m_table = table;
		}

		public void mouseClicked(MouseEvent e) {
			TableColumnModel colModel = m_table.getColumnModel();
			int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
			int modelIndex =
				colModel.getColumn(columnModelIndex).getModelIndex();

			if (modelIndex < 0)
				return;
			if (m_sortCol == modelIndex)
				m_sortAsc = !m_sortAsc;
			else
				m_sortCol = modelIndex;

			for (int i = 0; i < 4; i++) {
				TableColumn column = colModel.getColumn(i);
				column.setHeaderValue(getColumnName(column.getModelIndex()));
			}
			m_table.getTableHeader().repaint();

			Collections.sort(
				m_vector,
				new EFixListingComparator(modelIndex, m_sortAsc));
			m_table.tableChanged(new TableModelEvent(EFixDataModel.this));
			m_table.repaint();
		}
	}
}

/**
 * Utility ClassName; EFixListingComparator
 * Abstract: Comparator implementation that provides sort definition
 * for the eFix display listing.
 * 
 */
class EFixListingComparator implements Comparator {
	protected int m_sortCol;
	protected boolean m_sortAsc;

	public EFixListingComparator(int sortCol, boolean sortAsc) {
		m_sortCol = sortCol;
		m_sortAsc = sortAsc;
	}

	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof UpdateListingData)
			|| !(o2 instanceof UpdateListingData))
			return 0;

		UpdateListingData s1 = (UpdateListingData) o1;
		UpdateListingData s2 = (UpdateListingData) o2;

		int result = 0;
		switch (m_sortCol) {
			case 0 : // selections
				int s1_val = 0;
				if (((Boolean) s1.getSelectState()).booleanValue())
					s1_val = 1;

				int s2_val = 0;
				if (((Boolean) s2.getSelectState()).booleanValue())
					s2_val = 1;

				if (s1_val == s2_val)
					result = 0;
				if (s1_val < s2_val)
					result = -1;
				if (s1_val > s2_val)
					result = 1;
				break;
			case 1 : // eFix name
				String efixId1 =
					((EFixComponent) s1.getUpdateComponent()).getIdStr();
				String efixId2 =
					((EFixComponent) s2.getUpdateComponent()).getIdStr();
				result = efixId1.compareTo(efixId2);
				break;
			case 2 : // build date
				String date1 =
					((EFixComponent) s1.getUpdateComponent()).getInstallDate();
				String date2 =
					((EFixComponent) s2.getUpdateComponent()).getInstallDate();
				result = date1.compareTo(date2);
				break;
			case 3 : // install status
				String i1_s =
					((EFixComponent) s1.getUpdateComponent()).getInstallState();
				String i2_s =
					((EFixComponent) s2.getUpdateComponent()).getInstallState();

				result = i1_s.compareTo(i2_s);
				break;
		}

		if (!m_sortAsc)
			result = -result;
		return result;
	}

	public boolean equals(Object obj) {
		if (obj instanceof EFixListingComparator) {
			EFixListingComparator compObj = (EFixListingComparator) obj;
			return (compObj.m_sortCol == m_sortCol)
				&& (compObj.m_sortAsc == m_sortAsc);
		}
		return false;
	}
}
