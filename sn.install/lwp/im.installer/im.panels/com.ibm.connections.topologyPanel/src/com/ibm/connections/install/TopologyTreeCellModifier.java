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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class TopologyTreeCellModifier implements ICellModifier {
	private TreeViewer tv;
	private TopologyTreeViewer topology;

	public TopologyTreeCellModifier(TopologyTreeViewer topology, TreeViewer tv) {
		this.topology = topology;
		this.tv = tv;
	}

	public boolean canModify(Object element, String property) {
		TopologyClusterInfo tc = (TopologyClusterInfo) element;
		if (!property.equals("cluster") && topology.isExistedCluster(tc.getClusterName()))
			return false;

		if (property.equals("server")) {
			if (tc.isNodeSelected() == false)
				return false;
			if (tc.getServerName().endsWith("..."))
				return false;
		} else if (property.equals("cluster") && !tc.getIsFirstOne()) {
			return false;
		}
		return true;
	}

	public Object getValue(Object element, String property) {
		TopologyClusterInfo tc = (TopologyClusterInfo) element;
		if (property.equals("cluster")) {
			return tc.getClusterName();
		} else if (property.equals("server")) {
			return tc.getServerName();
		} else if (property.equals("nodeSelect")) {

			return tc.isNodeSelected();
		}

		return null;
	}

	public void modify(Object element, String property, Object value) {
		TreeItem item = (TreeItem) element;
		TopologyClusterInfo tc = (TopologyClusterInfo) item.getData();
		if (property.equals("cluster")) {
			String newValue = (String) value;
			List<String> list = Arrays.asList(topology.getExisted_Clusters());

			tc.setClusterName(newValue);
			if (list.contains(newValue))
				topology.fireClusterNameChanges(tc, true);
			else
				topology.fireClusterNameChanges(tc, false);
		} else if (property.equals("nodeSelect")) {
			// the value of CheckboxCellEditor is bool
			Boolean newValue = (Boolean) value;
			tc.setNodeSelected(newValue);
			if (newValue == false) {
				Color grayColor = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
				item.setForeground(3, grayColor);
				item.setForeground(4, grayColor);
			} else {
				Color blackColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
				item.setForeground(3, blackColor);
				item.setForeground(4, blackColor);
			}
			topology.fireCheckboxChanges(tc);
		} else if (property.equals("server")) {
			String newValue = (String) value;
			if (newValue.equals(""))
				return;
			tc.setServerName(newValue);
			topology.fireServerNameChanges(tc);
		}
	}
}
