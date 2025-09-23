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

public class TDIPropertySaver {
	public static final String PATH_WORK_AREA = Constants.TDI_WORK_DIR;
	public static final String PATH_LAST_SUCCESS_SESSION = "session";
	private static final String FILE_PATH_SEPERATOR = System
			.getProperty("file.separator");

	public static void save() {
		Iterator<String> loadIterator = PropertyLoader.load(
				PATH_WORK_AREA + FILE_PATH_SEPERATOR + "loadList.properties")
				.getAllProperty().values().iterator();
		while (loadIterator.hasNext()) {
			String propertyName = loadIterator.next();
			Property sessionProp = PropertyLoader.load(PATH_LAST_SUCCESS_SESSION
					+ FILE_PATH_SEPERATOR + propertyName);
			Property sourceProp = PropertyLoader.load(PATH_WORK_AREA
					+ FILE_PATH_SEPERATOR + propertyName);
			sessionProp.setProperty(sourceProp, true);
		}
	}
}
