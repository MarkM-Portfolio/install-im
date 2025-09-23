/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.migration.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import com.ibm.lconn.common.feature.LCInfo;
import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.task.CommandExec;
import com.ibm.lconn.common.util.StringResolver;
import com.ibm.lconn.common.was.WasVariableLoader;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class ActivitiesRemoveTAI extends LogOperator{
	
	public boolean execute(String[] args) {
		String taskresource = "/com/ibm/lconn/migration/migration_util.xml";
		String jaclresource = "/com/ibm/lconn/migration/removeActivitiesTai.jacl";
		String includesPath = "$<activities.lc.home>/ConfigEngine/config/includes";
		String cmd = "$<activities.lc.home>/ConfigEngine/ConfigEngine.bat,action-migration-disable-tai-activities,-Dremove.activities.tai.jacl=\"{0}\"";
		taskresource = StringResolver.resolveMacro(taskresource, getMacroProperties());
		includesPath = StringResolver.resolveMacro(includesPath, getMacroProperties());
		cmd = StringResolver.resolveMacro(cmd, getMacroProperties());
		try {
			String jacl = extractResource(jaclresource, null);
			cmd = MessageFormat.format(cmd, jacl);
			extractResource(taskresource, includesPath);
			CommandExec.executeCommand(cmd, null, getOutput(), getMacroProperties());
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private String extractResource(String resource, String tmpFolder) throws IOException {
		if(tmpFolder==null) tmpFolder = System.getProperty("java.io.tmpdir");
		String filePath = MessageFormat.format("ERROR extracting file: {0}", resource); 
			InputStream is = WasVariableLoader.class
					.getResourceAsStream(resource);
			String name = getName(resource);
			String ext = "";
			int dotIndex = name.lastIndexOf('.');
			if(dotIndex!=-1 && dotIndex+1<name.length()) {
				ext = name.substring(dotIndex);
				name = name.substring(0, dotIndex);
			}
			File tmpFile = File.createTempFile(name, ext, new File(
					tmpFolder));
			tmpFile.deleteOnExit();
			OutputStream out = new FileOutputStream(tmpFile);
			byte[] b = new byte[2048];
			int read = is.read(b);
			while (read != -1) {
				out.write(b, 0, read);
				read = is.read(b);
			}
			out.flush();
			out.close();
			filePath = tmpFile.getAbsolutePath();
			return filePath;
	}

	private String getName(String resourceStr) {
		int lastSlash = resourceStr.lastIndexOf('/');
		String name = "";
		if(lastSlash!=-1) name = resourceStr.substring(lastSlash+1);
		return name;
	}
	
	public static void main(String[] args) {
		LCInfo lc = new LCInfo("d:\\IBM\\WebSphere\\IBM-Connections\\");
		ActivitiesRemoveTAI d = new ActivitiesRemoveTAI();
		StringBuffer stringBuffer = new StringBuffer();
		d.setOutput(stringBuffer);
		d.setOutput(System.out);
		d.setMacroProperties(lc.getLCProperties());
		d.execute(args);
		System.out.println(stringBuffer.toString());
	}
}
