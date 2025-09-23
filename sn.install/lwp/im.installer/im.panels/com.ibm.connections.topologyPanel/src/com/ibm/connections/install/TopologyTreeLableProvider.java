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

import java.awt.Toolkit;
import java.io.InputStream;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TopologyTreeLableProvider implements ITableLabelProvider {
	private TopologyTreeViewer topology;
	InputStream s0 = TopologyTreeLableProvider.class.getResourceAsStream("/icons/checked.gif");
	InputStream s1 = TopologyTreeLableProvider.class.getResourceAsStream("/icons/unchecked.gif");
	InputStream s2 = TopologyTreeLableProvider.class.getResourceAsStream("/icons/checked_disable.gif");
	InputStream s3 = TopologyTreeLableProvider.class.getResourceAsStream("/icons/unchecked_disable.gif");

	private Image[] images = new Image[] { new Image(null, s0), new Image(null, s1), new Image(null, s2), new Image(null, s3) };

	public TopologyTreeLableProvider(TopologyTreeViewer topology) {
		super();
		this.topology = topology;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		TopologyClusterInfo tc = (TopologyClusterInfo) element;
		if (columnIndex == 2) {
			if (topology.isExistedCluster(tc.getClusterName())) {
				if (tc.isNodeSelected())
					return images[2];
				else
					return images[3];
			}
			if (!tc.getNodeName().endsWith("..."))
				if (tc.isNodeSelected())
					return images[0];
				else
					return images[1];

		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		TopologyClusterInfo tc = (TopologyClusterInfo) element;
		switch (columnIndex) {
		case 0:
			if (tc.getIsFirstOne())
				return topology.getApplicationNameText(tc.getApplicationName());
			else
				return "";
		case 1:
			if (tc.getIsFirstOne())
				return tc.getClusterName();
			else
				return "";
		case 2:
			// return Boolean.toString(tc.isNodeSelected());
			return "";
		case 3:
			if (tc.getIsFirstOne() && tc.getChildren().size() > 0)
				return tc.getNodeName();
			else
				return tc.getNodeName();
		default:
			if (tc.getIsFirstOne() && tc.getChildren().size() > 0)
				return tc.getServerName();
			else
				return tc.getServerName();
		}
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		for (Image image : images) {
			image.dispose();
		}
	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
