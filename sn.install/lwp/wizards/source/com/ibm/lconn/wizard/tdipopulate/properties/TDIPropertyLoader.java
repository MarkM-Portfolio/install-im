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
package com.ibm.lconn.wizard.tdipopulate.properties;

import java.util.Iterator;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.property.Property;
import com.ibm.lconn.wizard.common.property.PropertyLoader;

public class TDIPropertyLoader {

	public static final String PATH_WORK_AREA = Constants.TDI_WORK_DIR;
	public static final String PATH_LAST_SUCCESS_SESSION = "session";
	public static final String PATH_TEMPLATE = "template";
	private static final String FILE_PATH_SEPERATOR = System
			.getProperty("file.separator");

	public static void setLoadType(int loadType) {
		String sourcePath;
		if (loadType == LoaderType.LOAD_LAST_SUCCESS) {
			sourcePath = PATH_LAST_SUCCESS_SESSION;
		} else if (loadType == LoaderType.LOAD_TEMPLATE){
			sourcePath = PATH_TEMPLATE;
		} else {
			return;
		}
		Iterator<String> loadIterator = load("loadList.properties")
				.getAllProperty().values().iterator();
		while (loadIterator.hasNext()) {
			String propertyName = loadIterator.next();
			Property workProp = PropertyLoader.load(PATH_WORK_AREA
					+ FILE_PATH_SEPERATOR + propertyName);
			Property sourceProp = PropertyLoader.load(sourcePath
					+ FILE_PATH_SEPERATOR + propertyName);
			workProp.setProperty(sourceProp, true);
		}
	}

	public static Property load(String propertyName) {
		return PropertyLoader.load(PATH_WORK_AREA + FILE_PATH_SEPERATOR
				+ propertyName);
	}

}
