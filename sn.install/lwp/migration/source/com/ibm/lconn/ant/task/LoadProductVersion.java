/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.ant.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.ibm.lconn.common.util.Util;
import com.ibm.lconn.common.xml.XMLOperator;

/**
 * <LoadVersion prefix="source" versionfolder="${lc.home}/version"
 * productset="activities,blogs,communities,dogear,profiles,homepage" />
 */

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class LoadProductVersion extends BaseTask {
	private static final String STRING_DOT = ".";
	private static final String STR_EMPTY = "";
	private static final String LABEL_BUILD_DATE = "buildDate";
	private static final String LABEL_BUILD_LEVEL = "buildLevel";
	private static final String LABEL_VERSION = "version";
	private static final String PRODUCT_EXT = "product";
	private static final String CR_EXT = "efix";
	private static final String DTD_PRODUCT_FILE = "dtd/product.dtd";
	private static final String XPATH_PRODUCT_BUILD_LEVEL = "build-info/@level";
	private static final String XPATH_PRODUCT_BUILD_DATE = "build-info/@date";
	private static final String XPATH_PRODUCT_VERSION = "version/text()";
	private static final String XPATH_PRODUCT_ID = "id/text()";

	/**
	 * The version folder contains files end with <b>.product</b>, like
	 * activities.product specifying the activities product.
	 */
	private String versionFolder;
	private String productSet;

	public String getProductSet() {
		return productSet;
	}

	public void setProductSet(String productSet) {
		this.productSet = productSet;
	}

	@Override
	public void execute() throws BuildException {
		try {
			String prefix = getPrefix();
			String versionFolder = getVersionFolder();
			versionFolder = new File(versionFolder).getAbsolutePath();
			final String productSet = getProductSet();
			File versionDir = new File(versionFolder);
			log("Load product versions from {0}", versionFolder);
			log("Prefix = {0}", prefix);
			log("Product set = {0}",
					(productSet == null || STR_EMPTY.equals(productSet) ? "All"
							: productSet));
			File[] productFiles = versionDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (productSet == null
							|| STR_EMPTY.equals(productSet.trim()))
						return name.endsWith(PRODUCT_EXT);
					String[] products = Util.delimStr(productSet);
					for (int i = 0; i < products.length; i++) {
						String p = products[i];
						if (name.equals(p + PRODUCT_EXT)) {
							return true;
						}
					}
					return false;
				}
			});

			for (int i = 0; i < productFiles.length; i++) {
				try {
					// log("Parsing product file: {0}. ", productFiles[i]);
					Product product = new Product(productFiles[i]);
					String id = product.getId();
					log("Product id({0}), version({1}), build({2} - {3})", id,
							product.getVersion(), product.getBuildDate(),
							product.getBuildLevel());
					String prefixWithId = getPrefix() + product.getId()
							+ STRING_DOT;
					setProperty(prefixWithId + LABEL_VERSION,
							product.getVersion());
					setProperty(prefixWithId + LABEL_BUILD_LEVEL,
							product.getBuildLevel());
					setProperty(prefixWithId + LABEL_BUILD_DATE,
							product.getBuildDate());
					setProperty(getPrefix() + LABEL_VERSION,
							product.getVersion());

				} catch (TransformerException e) {
					log("Skip invalid prouct file: {0} ",
							productFiles[i].getAbsolutePath());
				}
			}
			log("Load product version finished. ");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public String getVersionFolder() {
		return versionFolder;
	}

	public void setVersionFolder(String versionFolder) {
		this.versionFolder = versionFolder;
	}

	class Product {
		String id, version, buildDate, buildLevel;
		File[] crFile;

		public Product(File productFile) throws TransformerException,
				ParserConfigurationException, FileNotFoundException,
				SAXException, IOException {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			Document doc = null;
			builder = factory.newDocumentBuilder();
			File dtdFile = new File(productFile.getParent(), DTD_PRODUCT_FILE);
			doc = builder.parse(new FileInputStream(productFile),
					dtdFile.getAbsolutePath());
			Element root = doc.getDocumentElement();
			XMLOperator op = new XMLOperator();
			id = op.readValue(root, XPATH_PRODUCT_ID);
			version = op.readValue(root, XPATH_PRODUCT_VERSION);
			buildDate = op.readValue(root, XPATH_PRODUCT_BUILD_DATE);
			buildLevel = op.readValue(root, XPATH_PRODUCT_BUILD_LEVEL);
			// check the cr version
			crFile = productFile.getParentFile().listFiles(
					new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.endsWith("efix");
						}
					});
			if(crFile!=null)
				System.out.println("crFile length: " + crFile.length);
			
			fixLCVersion();
		}

		private int checkCRVersion(String ICversion) {
			int maxCR = -1;
			// cr format LO77120-IC4500-CR03-Forums.efix
			for (int i = 0; crFile != null && i < crFile.length; i++) {
				String name = crFile[i].getName().toLowerCase();
				int ic_index = name.indexOf("-");
				String ic_version = name.substring(ic_index + 3, ic_index + 5);
				if (ic_version.endsWith(ICversion)) {
					int cr_index = name.lastIndexOf("-");
					int cr_version = new Integer(name.substring(cr_index - 2,
							cr_index));
					if (cr_version > maxCR)
						maxCR = cr_version;
				}
			}
			
			System.out.println("CR = " + maxCR);
			return maxCR;
		}

		private void fixLCVersion() {
			System.out.println("fixLCVersion version = " + version);
			if (version == null)
				throw new BuildException("Current version is not supported. ");
			if ("10/13/2008".equals(buildDate) || version.startsWith("2.0.1")) {// fix
																				// for
																				// 2.0.1
																				// Connections
				version = "201";
			} else if (version.startsWith("2.5.1")) {
				version = "251";
			} else if (version.startsWith("2.5")) {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					Date buildDateVal = df.parse(buildDate);
					Date beta1 = df.parse("04/15/2009");
					Date beta2 = df.parse("06/25/2009");
					int compareToBeta1 = buildDateVal.compareTo(beta1);
					int compareToBeta2 = buildDateVal.compareTo(beta2);
					if (compareToBeta1 <= 0) {
						version = "25b1";
					} else if (compareToBeta2 <= 0) {
						version = "25b2";
					} else {
						version = "25";
					}
				} catch (Exception e) {
					// Should not happen
				}
			} else if (version.startsWith("4.0.0")) {
				int crVersion = checkCRVersion("40");
				log("cr version111: {0} ", crVersion);
				if (crVersion > 0 && crVersion < 4)
					version = "40";
				else if (crVersion > 0 && crVersion == 4)
					version = "40CR4";
				else
					version = "40";
			} else if (version.startsWith("3.0.1")) {
				version = "301";
			} else if (version.startsWith("3.0")) {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					Date buildDateVal = df.parse(buildDate);
					Date beta1 = df.parse("07/01/2010");
					Date beta2 = df.parse("09/01/2010");
					int compareToBeta1 = buildDateVal.compareTo(beta1);
					int compareToBeta2 = buildDateVal.compareTo(beta2);
					if (compareToBeta1 <= 0) {
						version = "30b1";
					} else if (compareToBeta2 <= 0) {
						version = "30b2";
					} else {
						version = "30";
					}
				} catch (Exception e) {
					// Should not happen
				}
			} else if (version.startsWith("4.5")) {
				int crVersion = checkCRVersion("45");
				if (crVersion > 0 && crVersion < 4)
					version = "45";
				else if (crVersion > 0 && crVersion == 4)
					version = "45CR4";
				else if (crVersion > 0 && crVersion == 5)
					version = "45CR5";
				else
					version = "45";
			} else if (version.startsWith("5.0")) {
				version = "50";
			} else if (version.startsWith("5.5")) {
				version = "55";
			} else if (version.startsWith("6.0")) {
				version = "60";
			} else if (version.startsWith("6.5")) {
				version = "65";
			} else {
				throw new BuildException("Current version is not supported. ");
			}
			System.out.println("current version = " + version);
		}

		public String toString() {
			return MessageFormat.format(
					"Product id({0}), version({1}), build({2} - {3})", id,
					version, buildDate, buildLevel);
		}

		public String getId() {
			return id;
		}

		public String getVersion() {
			return version;
		}

		public String getBuildDate() {
			return buildDate;
		}

		public String getBuildLevel() {
			return buildLevel;
		}
	}
}
