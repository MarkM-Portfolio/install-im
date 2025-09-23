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

import org.eclipse.swt.widgets.Shell;

import com.ibm.lconn.wizard.common.ui.LCDialog;
import com.ibm.lconn.wizard.common.ui.data.WizardPageData;

public class ClusterNodeDialog extends LCDialog {

	public ClusterNodeDialog(Shell pareShell) {
		this(pareShell, new WizardPageData("cluster", "NodeInfoDialog"));
		getData().setDesc("Specify the node information");
		getData().setTitle("Node Information");
	}

	public ClusterNodeDialog(Shell pareShell, WizardPageData pageData) {
		super(pareShell, pageData);
	}
	
	public static void main(String[] args) {
		new ClusterNodeDialog(null).open();
	}

}
