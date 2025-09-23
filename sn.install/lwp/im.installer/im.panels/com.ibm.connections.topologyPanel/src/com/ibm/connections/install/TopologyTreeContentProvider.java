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

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TopologyTreeContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		TopologyClusterInfo entry = (TopologyClusterInfo) parentElement;
		List list = entry.getChildren();
		if (list == null)
			return new Object[0];
		return list.toArray();
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		TopologyClusterInfo entry = (TopologyClusterInfo) element;
		List list = entry.getChildren();
		return !(list == null || list.isEmpty());
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			List input = (List) inputElement;
			return input.toArray();
		}
		return new Object[0];
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
