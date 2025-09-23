/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.wizard.cluster.task;

import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.interfaces.LCAction;

public class ClusterWizardAction implements LCAction {

	AbstractTask currentTask;
	public String execute() {
		String task = eval(ClusterConstant.INPUT_clusterTask);
		if(ClusterConstant.OPTION_clusterTaskCreate.equals(task)){
			currentTask = new AddPrimaryNodeTask();
			currentTask.run();
			//Primary node
		} else if(ClusterConstant.OPTION_clusterTaskAdd.equals(task)){
			//Subsequent node
		}else if(ClusterConstant.OPTION_clusterTaskRemove.equals(task)){
			//uninstall 
		}
		return null;
	}
	
	private String eval(String inputId){
		return DataPool.getValue(ClusterConstant.WIZARD_ID_CLUSTER, inputId);
	}

	public String getLogPath() {
		if(currentTask!=null) return currentTask.getLog();
		return "";
	}

}
