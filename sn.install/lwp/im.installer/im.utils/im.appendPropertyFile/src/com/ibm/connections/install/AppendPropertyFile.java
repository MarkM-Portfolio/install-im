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

package com.ibm.connections.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class AppendPropertyFile {

	public void append(String propertiesFile, String[] args) {
		try {
			File f = new File(propertiesFile);

			FileInputStream fis = new FileInputStream(propertiesFile);
			SortedProperties props = new SortedProperties();
			props.load(fis);

			for (int i = 1; i < args.length; i++) {
				props.setProperty(args[i], args[++i]);
			}
			//f.getParentFile().mkdirs();

			FileOutputStream fileOutputStream = new FileOutputStream(f);
			props.store(fileOutputStream, null);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//String propertiesFile = "C:/config.properties";
		//String params[] = { "activities.ClusterExist", "false", "activities.ClusterName", "lccluster", "activities.FirstNodeName", "sirloinNode01" };
		if (args.length > 2 && args.length % 2 == 1) {
			AppendPropertyFile apf = new AppendPropertyFile();
			apf.append(args[0], args);
		}
	}

	public void run(String[] args, PrintWriter writer) {
		writer.write("Goodbye.java initiated.\n");
		if (args.length > 2 && args.length % 2 == 1) {
			AppendPropertyFile apf = new AppendPropertyFile();
			apf.append(args[0], args);
		}
	}

	class SortedProperties extends Properties {
		public Enumeration keys() {
			Enumeration keysEnum = super.keys();
			Vector<String> keyList = new Vector<String>();
			while (keysEnum.hasMoreElements()) {
				keyList.add((String) keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}
	}
}
