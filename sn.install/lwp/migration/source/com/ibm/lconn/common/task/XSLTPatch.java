/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.task;

import java.io.File;

import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.taskdefs.Replace.Replacefilter;

import com.ibm.lconn.ant.task.BaseTask;
/**
 * This file store the special treatment on configuration files during XSL transformation. 
 *
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class XSLTPatch extends BaseTask {

	public void patchPost(String xmlSymbol, File currentVersionFile,
			String fromVersion, String toVersion) {
		if("widgets-config".equals(xmlSymbol) && "201".equals(fromVersion) && "25b1".equals(toVersion)){
			Replace rep = new Replace();
			setTask(rep);
			Replacefilter filter = rep.createReplacefilter();
			rep.setFile(currentVersionFile);
			filter.setToken("xmlns:pns=\"http://www.ibm.com/profiles-config\"");
			filter.setValue("");
			filter = rep.createReplacefilter();
			filter.setToken("namespace=\"http://www.ibm.com/widgets-config\"");
			filter.setValue("xmlns=\"http://www.ibm.com/widgets-config\"");
			filter = rep.createReplacefilter();
			filter.setToken("xmlns=\"\"");
			filter.setValue("");
			rep.execute();
		}
	}

	public void patchBefore(String xmlSymbol, File currentVersionFile,
			String fromVersion, String toVersion) {
		
	}

	
}
