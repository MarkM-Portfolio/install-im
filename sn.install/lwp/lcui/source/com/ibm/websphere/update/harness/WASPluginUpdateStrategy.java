/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */

package com.ibm.websphere.update.harness;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class: WASLiteStrategy.java Abstract: Enables IHS to be updated within the current PTF strategy as a WAS-LITE product Component Name: WAS.ptf Release: ASV50X History 1.4, 3/17/03 01-Feb-2003 Initial Version
 */

public class WASPluginUpdateStrategy extends WASAbstractUpdateStrategy {

	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugPropertyName = "com.ibm.websphere.update.harness.debug" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugTrueValue = "true" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugFalseValue = "false" ;

	// Debugging support ...
	protected static boolean debug;

	static {
		String debugValue = System.getProperty(debugPropertyName);

		debug = ((debugValue != null) && debugValue.equals(debugTrueValue));
	}

	/**
	 * @return  the debug
	 * @uml.property  name="debug"
	 */
	public static boolean isDebug() {
		return debug;
	}

	public static void debug(String arg) {
		if (!debug)
			return;

		System.out.println(arg);
	}

	public static void debug(String arg1, String arg2) {
		if (!debug)
			return;

		System.out.print(arg1);
		System.out.println(arg2);
	}

	//********************************************************
	//  Program Versioning
	//********************************************************	
	public static final String pgmVersion = "1.4" ;
	//********************************************************
	//  Program Versioning
	//********************************************************	
	public static final String pgmUpdate = "3/17/03" ;

	//********************************************************
	//  Instance State
	//********************************************************	
	private String productDir;
	private boolean conformsToStrategy = false;

	private Map harnessFiles;

	private static boolean strategyExecuted = false;

	private List consumedExceptions;

	private String dtdKey = "DTD";
	private String dtdRepository = "dtdFiles/";

	private String productKey = "Product";
	private String productFileRepository = "productFiles/";

	private String verifyToken = "conf" + File.separator + "httpd.conf";

	private static int fc = 0;

	private String[] dtdFiles =
		{
			"applied.dtd",
			"applied.xsd",
			"component.dtd",
			"component.xsd",
			"eventHistory.dtd",
			"eventHistory.xsd",
			"extension.dtd",
			"extension.xsd",
			"product.dtd",
			"product.xsd",
			"update.dtd",
			"update.xsd",
			"websphere.dtd",
			"websphere.xsd" };
			

	//********************************************************
	//  Method Definitions
	//********************************************************

	public WASPluginUpdateStrategy(String productDir) {
		super(productDir);
		super.productFile = "BASE.product";
	}

	//********************************************************
	//  Method Definitions
	//********************************************************

	/**
	 * @return  the harnessFiles
	 * @uml.property  name="harnessFiles"
	 */
	public Map getHarnessFiles() {

		debug("getHarnessFiles()...entered");

		if (harnessFiles == null) {
			List dtdFileSet = new ArrayList();
			String finalProductFile = "";

			harnessFiles = new HashMap();

			debug("Retrieving DTD files...begin");

			//retrieve the DTD files
			for (int i = 0; i < dtdFiles.length; i++) {
				debug("Retrieving dtd file: " + dtdRepository + dtdFiles[i] + " from resource jar");
				URL aDtdFile = getClass().getResource(dtdRepository + dtdFiles[i]);

				if (aDtdFile == null) {
					debug("Could not locate " + dtdRepository + dtdFiles[i] + " from the resource jar");
					consumedExceptions.add(new UpdateHarnessException("The DTD file [ " + dtdRepository + dtdFiles[i] + " ] does not exist in the resource jar"));
					setConformsToStrategy(false);
				} else {
					String normalizedDtdFile = normalizeURLResource(aDtdFile.getFile(), 44);
					debug("Adding " + normalizedDtdFile + " to the harnessFile collection");
					dtdFileSet.add(dtdRepository + dtdFiles[i]);
				}
			}

			debug("Adding DTD files to the harness collection map...");
			harnessFiles.put(dtdKey, dtdFileSet);
			debug("Successfully added DTD files to the harness collection map");

			debug("Retrieving DTD files...completed");

			debug("Retrieving product file...begin");

			//retrieve the BASE product file
			debug("Retrieving product file: " + productFileRepository + super.productFile + " from resource jar");
			URL productFileURL = getClass().getResource(productFileRepository + super.productFile);
			if (productFileURL == null) {
				debug("Could not locate " + productFileRepository + super.productFile + " from the resource jar");
				consumedExceptions.add(
					new UpdateHarnessException("The BASE product file [ " + productFileRepository + super.productFile + " ] does not exist in the resource jar"));
				setConformsToStrategy(false);
			} else {
				String normalizedProductFile = normalizeURLResource(productFileURL.getFile(), 48);
				debug("Adding " + normalizedProductFile + " to the harnessFile collection");
				//productFile = normalizedProductFile;
				finalProductFile = productFileRepository + super.productFile;
			}

			debug("Adding Product file to the harness collection map...");
			harnessFiles.put(productKey, finalProductFile);
			debug("Successfully added Product file to the harness collection map...");

			debug("getHarnessFiles()...exited");
			debug("returned a harnessFile set of size : " + harnessFiles.size());
			return harnessFiles;

		} else {
			return harnessFiles;
		}

	}

	public void executeStrategy() {

		debug("executeStrategy(" + productDir + ")...entered");

		debug("Strategy execution already performed: " + strategyExecuted);

		if (!strategyExecuted) {

			File updatePropertiesFileSet = new File(constructUpdatePropertiesFileSet());

			if (!updatePropertiesFileSet.exists()) {
				debug("Attempting to create directory " + constructUpdatePropertiesFileSet());

				//create the update directories
				boolean createdUpdatePropertiesDirs = updatePropertiesFileSet.mkdir();

				if (!createdUpdatePropertiesDirs) {
					debug("Failed to create /properties directory...returning");
					setConformsToStrategy(false);
					return;
				} else {
					debug("Created /properties directory successfully");
				}
			} else {
				debug("/properties directory already exists...continue");
			}

			File updateVersionFileSet = new File(constructUpdateVersionFileSet());
			if (!updateVersionFileSet.exists()) {

				debug("Attempting to create directory " + constructUpdateVersionFileSet());
				boolean createdUpdateVersionDir = updateVersionFileSet.mkdir();

				if (!createdUpdateVersionDir) {
					debug("Failed to create /version directory...returning");
					setConformsToStrategy(false);
					return;
				} else {
					debug("Created /version directory successfully");
				}
			} else {
				debug("/version directory already exists...continue");
			}

			File updateDTDFileSet = new File(constructUpdateDTDFileSet());
			if (!updateDTDFileSet.exists()) {
				
				debug("Attempting to create directory " + constructUpdateDTDFileSet());
				boolean createdUpdateDTDDir = updateDTDFileSet.mkdir();

				if (!createdUpdateDTDDir) {
					debug("Failed to create /dtd directory...returning");
					setConformsToStrategy(false);
					return;
				} else {
					debug("Created /dtd directory successfully");
				}
				
			} else {
				debug("/dtd directory already exists...continue");
			}

			createHarnessFiles();
			debug("Creating harness files...completed");
			setConformsToStrategy(true);
			
		}

	}

}
