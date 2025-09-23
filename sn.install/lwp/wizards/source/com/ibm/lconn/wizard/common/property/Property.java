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
package com.ibm.lconn.wizard.common.property;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.logging.LogUtil;

public class Property {
	private static final Logger logger = LogUtil.getLogger(Property.class);
	private String propertyPath;
	private Properties properties;

	public Property(String propertyPath) {
		this.propertyPath = propertyPath;
		File propFile = new File(this.propertyPath);
		if (!propFile.exists()) {
			try {
				propFile.createNewFile();
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"commom.property.create.file.exception", e);
			}
		}
		try {
			FileInputStream in = new FileInputStream(propertyPath);
			properties = new SortedProperties();
			properties.load(in);
			in.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "commom.property.file.not.found", e);
		}
	}

	public String getProperty(String key) {
		if (key == null)
			key = "null";
		return properties.getProperty(key);
	}

	public Map<String, String> getProperty(String[] keys) {
		if (keys == null)
			return null;

		Map<String, String> values = new HashMap<String, String>();
		int length = keys.length;
		for (int i = 0; i < length; i++) {
			values.put(keys[i], getProperty(keys[i]));
		}
		return values;
	}

	public Map<String, String> getAllProperty() {
		Iterator<Object> it = properties.keySet().iterator();
		if (!it.hasNext())
			return null;

		Map<String, String> map = new HashMap<String, String>();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = properties.getProperty(key);
			map.put(key, value);
		}
		return map;
	}

	public void setProperty(String key, String value) {
		try {
			logger.log(Level.INFO, key + "=" + value);
			FileOutputStream out = new FileOutputStream(propertyPath);
			if (value == null) {
				if (key == null)
					key = "null";
				properties.remove(key);
			} else {

				if (key == null)
					key = "null";
				properties.setProperty(key, value);

			}

			properties.store(out, null);
			out.close();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "commom.property.file.not.found", e);
		}
	}

	public void setProperty(Map<String, String> values) {
		setProperty(values, false);
	}

	public void setProperty(Properties values) {
		setProperty(values, false);
	}

	public void setProperty(Map<String, String> values, boolean flush) {
		if (flush)
			this.clean();

		if (values != null) {
			String[] keys = values.keySet().toArray(new String[0]);
			Arrays.sort(keys);
			for (String key : keys) {
				String value = values.get(key);
				this.setProperty(key, value);
			}
		}
	}

	public void setProperty(Properties values, boolean flush) {
		if (flush)
			this.clean();

		if (values != null) {
			String[] keys = values.keySet().toArray(new String[0]);
			Arrays.sort(keys);
			for (String key : keys) {
				String value = values.getProperty(key);
				this.setProperty(key, value);
			}
		}
	}

	public void setProperty(boolean transfer, Map<String, String> values) {

		if (transfer)
			this.setProperty(values);
		else {
			try {
				File propFile = new File(this.propertyPath);
				if (!propFile.exists()) {

					propFile.createNewFile();

				}
				BufferedWriter propBr = new BufferedWriter(new FileWriter(
						propFile));
				String[] keys = values.keySet().toArray(new String[0]);
				Arrays.sort(keys);
				for (String key : keys) {
					String value = values.get(key);
					propBr.write(key + "=" + value);
					propBr.newLine();
				}
				propBr.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"commom.property.create.file.exception", e);
			}
		}
	}

	public void setProperty(boolean transfer, Properties values) {

		if (transfer)
			this.setProperty(values);
		else {
			try {
				// Make copy of _values_ because we'll work through what needs updating
				// by removing ones already updated
				Properties props = new Properties();
				for (Enumeration propertyNames = values.propertyNames(); propertyNames.hasMoreElements(); ) {
					Object key = propertyNames.nextElement();
					props.put(key, values.get(key));
				}
				
				// Read in from source file; write out to temp; rename temp to source file
				File propFile = new File(this.propertyPath);
				File tmpPropFile = File.createTempFile("props", null);
				BufferedReader br = new BufferedReader(new FileReader(propFile));
				BufferedWriter propBw = new BufferedWriter(new FileWriter(tmpPropFile));
				String line;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("#") || line.startsWith("!") || line.equals("")) {
						// Copy comments and blank lines verbatim
						propBw.write(line);
						propBw.newLine();
					} else {
						String key = line.split("=", 2)[0].trim();
						String value;
						if (props.containsKey(key)) {
							value = (String) props.get(key);
							// Since we've now handled this updated property, let's remove from the set
							props.remove(key);
						} else {
							// This is a property not already defined but need to be careful to check if it's empty value
							// e.g. property=
							String[] parts = line.split("=", 2);
							if (parts.length > 1) {
								value = (String) parts[1].trim();
							} else {
								value = "";
							}
						}
						propBw.write(key + "=" + value);
						propBw.newLine();
					}
				}
				br.close();
				if (props.size() > 0) {
					// Handle remaining properties
					String[] keys = props.keySet().toArray(new String[0]);
					Arrays.sort(keys);
					for (String key : keys) {
						String value = (String) props.get(key);
						propBw.write(key + "=" + value);
						propBw.newLine();
					}
				}
				propBw.close();
				if (!propFile.delete()) {
					throw new IOException();
				}
				// Below code is not working with java 8 on rhel 7.4 vm
				//if (!tmpPropFile.renameTo(propFile.getCanonicalFile())) {
				//	throw new IOException();
				//}
				
				// Files.renameTo is platform-dependent, not safe to do this map_dbrepos_from_source.properties 
				// rename operation, since our build still uses java 6, so have to use the low level way.
				InputStream in = new BufferedInputStream(new FileInputStream(tmpPropFile.getCanonicalPath()));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(propFile.getCanonicalPath()));
				try {
					byte[] buffer = new byte[1024];
					int lengthRead;
					while ((lengthRead = in.read(buffer)) > 0) {
						out.write(buffer, 0, lengthRead);
					}
					out.flush();
				} catch (IOException e) {
					throw e;
				} finally {
					in.close();
					out.close();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "commom.property.create.file.exception", e);
			}
		}
	}

	public void setProperty(Property prop) {
		setProperty(prop, false);
	}

	public void setProperty(Property prop, boolean flush) {
		if (flush)
			this.clean();
		this.setProperty(prop.getAllProperty(), true);
	}

	public void clean() {
		properties.clear();
		this.setProperty(null, null);

	}

	public void remove(String key) {
		this.setProperty(key, null);
	}

	public static void main(String[] a) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("abd", "haha");

		Property p = PropertyLoader.load("haha");
		p.setProperty(m);
	}
}
