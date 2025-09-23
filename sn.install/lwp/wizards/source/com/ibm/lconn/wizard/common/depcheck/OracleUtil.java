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
package com.ibm.lconn.wizard.common.depcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * OracleUtil is used to work with depchecker to detect Oracle installation
 * directory
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 */
public class OracleUtil {
	private static final Logger logger = LogUtil.getLogger(OracleUtil.class);
	public static final String FS = System.getProperty("file.separator");

	public static final String HOME_TAG = "HOME";
	public static final String LOCATION_ATTR = "LOC";
	public static final String COMP_TAG = "COMP";
	public static final String NAMR_ATTR = "NAME";
	public static final String VER_ATTR = "VER";
	public static final String SERVER_STRING = "oracle.server";
	private static final String INVENTORY_LOC_KEY = "inventory_loc";
	private static final String ORAINST_LOC1 = "/etc/oraInst.loc";
	private static final String ORAINST_LOC2 = "/var/opt/oraInst.loc";
	private static final String ORATAB_LOC1 = "/etc/oratab";
	private static final String ORATAB_LOC2 = "/var/opt/oratab";
	private static final String SAX_PARSER = "org.apache.xerces.parsers.SAXParser";

	/**
	 * Gets Oracle home from oracle inventory
	 * 
	 * @param inventoryDir
	 *            Oracle inventroy location
	 * @return the first Oracle database home directory
	 */
	public static String getOracleHome(String inventoryDir) {
		XMLReader parser = null;
		logger.log(Level.FINER, "dbconfig.finer.get_oracle_home_from_inventory", new String[] {inventoryDir});
		String inventoryFile = inventoryDir + FS + "ContentsXML" + FS
				+ "inventory.xml";
		if(!new File(inventoryFile).canRead()) {
			logger.log(Level.INFO, "common.info.cannot_read_orainventory", inventoryFile);
			return "";
		}
		OracleInvetoryHandler ch = new OracleInvetoryHandler();
		try {
			parser = (XMLReaderFactory.createXMLReader(SAX_PARSER));

			parser.setContentHandler(ch);
			parser.parse(new InputSource(new FileReader(inventoryFile)));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		List<String> homes = ch.getOracleHomes();
		for (String oracleHome : homes) {
			logger.log(Level.FINER, "dbconfig.finer.get_oracle_home_version", new String[] {oracleHome});
			if (getOracleVersion(oracleHome) != null) {
				return oracleHome;
			}
		}

		return "";
	}

	/**
	 * Gets Oracle version from oracle home, the version information is in
	 * $ORACLE_HOME$/inventory/ContentsXML/comps.xml
	 * 
	 * @param oracleHome
	 * @return Oracle version
	 */
	public static String getOracleVersion(String oracleHome) {
		XMLReader parser = null;
		String compFile = oracleHome + FS + "inventory" + FS + "ContentsXML"
				+ FS + "comps.xml";

		OracleProductHandler ph = new OracleProductHandler();

		try {
			parser = (XMLReaderFactory.createXMLReader(SAX_PARSER));
			parser.setContentHandler(ph);
			parser.parse(new InputSource(new FileReader(compFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return ph.getVersion();
	}

	private static class OracleInvetoryHandler extends DefaultHandler {
		private List<String> homes = new ArrayList<String>();

		public void startElement(String uri, String name, String qname,
				Attributes atts) {
			if (HOME_TAG.equals(name)) {
				String oracleHome = atts.getValue(LOCATION_ATTR);
				logger.log(Level.FINER, "dbconfig.finer.get_oracle_home", new String[] {oracleHome});
				File oh = new File(oracleHome);
				if(oh.exists() && oh.isDirectory() && oh.canRead()) {
					homes.add(oracleHome);
				}
			}
		}

		public List<String> getOracleHomes() {
			return homes;
		}
	}

	private static class OracleProductHandler extends DefaultHandler {
		private String version = null;

		private String type = null;

		private boolean detected = false;

		public void startElement(String uri, String name, String qname,
				Attributes atts) {
			if (!detected) {
				logger.log(Level.FINER, "dbconfig.finer.oracle_home_attr", 
						new String[] {});
				if (COMP_TAG.equals(name)
						&& SERVER_STRING.equals(atts.getValue(NAMR_ATTR))) {
					type = atts.getValue(NAMR_ATTR);
					version = atts.getValue(VER_ATTR);
					logger.log(Level.FINER, "dbconfig.finer.oracle_home_attr", 
							new String[] {type, version});
					detected = true;
				}
			}
		}

		public boolean detected() {
			return detected;
		}

		public String getType() {
			return type;
		}

		public String getVersion() {
			return version;
		}
	}

	/**
	 * Gets Oracle inventory location on Linux/AIX, inventroy location is
	 * specified in /etc/oraInst.loc or /var/opt/oraInst.loc
	 * 
	 * @return oracle inventory location
	 */
	public static String getInventoryLoc() {
		String oraInst = "";
		if (new File(ORAINST_LOC1).exists()) {
			oraInst = ORAINST_LOC1;
		} else if (new File(ORAINST_LOC2).exists()) {
			oraInst = ORAINST_LOC2;
		} else {
			return "";
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(oraInst));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				int ch = line.indexOf('=');
				if (ch > 0) {
					String key = line.substring(0, ch);
					if (INVENTORY_LOC_KEY.equals(key)) {
						return line.substring(ch + 1);
					}
				}
			}
		} catch (IOException e) {
			// ignore
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		return "";
	}

	private static List<String> parseOratab(String path) {
		List<String> oracleHomes = new ArrayList<String>();
		
		try {
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
				new FileInputStream(path)));
		String line = null;

		while ((line = lnr.readLine()) != null) {
			if(line.trim().startsWith("#")) {
				continue;
			}
			String[] tokens = line.split(":");
			if(tokens.length==3) {
				File oh = new File(tokens[1]);
				if(oh.exists() && oh.isDirectory() && oh.canRead()) {
					if(!oracleHomes.contains(tokens[1])) {
						oracleHomes.add(tokens[1]);
					}
				}
			}
		}
		}catch (IOException e) {
			logger.log(Level.SEVERE, "common.severe.fail_to_parse_oratab", path);
		}

		return oracleHomes;
	}
	
	public static String getOracleHome2() {
		// try to find oratab first
		String oratab = null;
		File poll = new File(ORATAB_LOC1);
		if(poll.exists() && poll.isFile()) {
			oratab = ORATAB_LOC1;
		} else {
			poll = new File(ORATAB_LOC2);
			if(poll.exists() && poll.isFile()) {
				oratab = ORATAB_LOC2;
			}
		}		
		if(oratab != null) {
			List<String> oraHomes = parseOratab(oratab);
			if(oraHomes.size() != 0) {
				return oraHomes.get(0);
			}
		}
		// try to locate oraInst.loc
		String oraInventoryLoc = getInventoryLoc();
		if(!"".equals(oraInventoryLoc)) {
			getOracleHome(oraInventoryLoc);
		}
		
		return "";
	}

	public static void main(String[] args) {
		String inventoryLoc = getInventoryLoc();
		System.out.println("InventoryLoc = " + inventoryLoc);
		String oracleHome = getOracleHome(inventoryLoc);
		System.out.println("OracleHome = " + oracleHome);
		System.out.println("OracleVersion = " + getOracleVersion(oracleHome));
		System.out.println("OraclegetHome2 = " + getOracleHome2());
	}

}
