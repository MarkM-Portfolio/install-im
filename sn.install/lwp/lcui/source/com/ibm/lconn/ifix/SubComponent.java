/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;

import java.io.File;

import com.ibm.websphere.update.delta.Logger;


public class SubComponent extends Component{
	
	public SubComponent(String componentName, String appName, File earFolder,  Logger logStream){
		this.componentName = componentName;
		this.appName = appName;
		this.earFolder = earFolder;
		
		this.setLogStream(logStream);
		
		this.updatedEARFileList = FileUtil.getAllFilesRelPath(earFolder);
		logStream.Both("subComponent-"+appName+"-updatedEARFileList=" + updatedEARFileList);
		configureEARName();
		createUpdatePackage(earFolder);
		
		logStream.Both("subComponent-"+appName+"-deleteEARFileList=" + this.deleteEARFileList);
	}
}
