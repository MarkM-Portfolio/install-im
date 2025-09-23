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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class ClusterNameListener implements ModifyListener{
	private TopologyPanel deploymentPanel;
	
	public ClusterNameListener(TopologyPanel deploymentPanel) {
		super();
		this.deploymentPanel = deploymentPanel;
	}

	public void modifyText(ModifyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
