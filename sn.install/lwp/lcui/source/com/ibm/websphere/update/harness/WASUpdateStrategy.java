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
 * Class: WASUpdateStrategy.java Abstract: Enables WAS-Lite + IHS  to be updated within the current PTF strategy as a stand-alone product. Component Name: WAS.ptf Release: ASV50X History 1.2, 3/13/03 01-Feb-2003 Initial Version
 */

public class WASUpdateStrategy implements UpdateStrategy {

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
	public static final String pgmVersion = "1.2" ;
	//********************************************************
	//  Program Versioning
	//********************************************************	
	public static final String pgmUpdate = "3/13/03" ;

	//********************************************************
	//  Instance State
	//********************************************************
	private String productDir;
	private boolean conformsToStrategy = false;

	private static boolean strategyExecuted = false;

	private List consumedExceptions;

	private String dtdKey = "DTD";
	private String dtdRepository = "dtdFiles/";

	private String productKey = "Product";
	private String productFileRepository = "productFiles/";

	private String verifyToken = "config";

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

	private String baseProductFile = "BASE.product";

	public WASUpdateStrategy(String productDir) {
		this.productDir = productDir;
		consumedExceptions = new ArrayList();
	}

	//********************************************************
	//  Method Definitions
	//********************************************************

	/**
	 * 
	 * evaluation of post executeStrategy() state
	 * 
	 */
	public boolean conformsToStrategy() {

		debug("Verifying that the process conforms to strategy...begin");
							
		if(!ensureVersionContents(constructUpdateVersionFileSet())){
			debug("The harness files in the filesystem does not match the harness files in the resource jar");
			debug("Does not conform to strategy");
			return false;
		}

		if (!conformsToStrategy){
			debug("Does not conform to strategy");
			return false;
		}
		
		if (consumedExceptions.size() > 0) {
			debug("Process ended with exceptions");
			debug("Does not conform to strategy");
			return false;
		}

		return true;

	}

	public boolean isWASLite() {
		return !(new File(this.productDir + File.separator + verifyToken).exists());
	}

	/**
	 * @param conformsToStrategy  the conformsToStrategy to set
	 * @uml.property  name="conformsToStrategy"
	 */
	private void setConformsToStrategy(boolean conformsToStrategy) {
		this.conformsToStrategy = conformsToStrategy;
	}

	private String normalizePath(String path) {
		String normalizedPath = "";
		if ((path.lastIndexOf("\\") == path.length())) {
			normalizedPath = path.substring(0, path.lastIndexOf("\\"));
		} else if ((path.lastIndexOf("/") == path.length())) {
			normalizedPath = path.substring(0, path.lastIndexOf("/"));
		} else {
			normalizedPath = path;
		}

		return normalizedPath;

	}

	private String constructUpdatePropertiesFileSet() {
		debug("Constructed update properties file set: " + normalizePath(this.productDir) + File.separator + "properties");
		return normalizePath(this.productDir) + File.separator + "properties";
	}

	private String constructUpdateVersionFileSet() {
		debug("Constructed update version file set: " + normalizePath(this.productDir) + File.separator + "properties" + File.separator + "version");
		return normalizePath(this.productDir) + File.separator + "properties" + File.separator + "version";
	}

	private String constructUpdateDTDFileSet() {
		debug("Constructed update dtd file set: " + normalizePath(this.productDir) + File.separator + "properties" + File.separator + "version" + File.separator + "dtd");
		return normalizePath(this.productDir) + File.separator + "properties" + File.separator + "version" + File.separator + "dtd";
	}
	
	private String normalizeURLResource(String target, int prefixLength) {
		int bangIndex = target.indexOf("!");
		return target.substring(bangIndex + prefixLength, target.length());
	}

	private Map getHarnessFiles() {

		debug("getHarnessFiles()...entered");

		List dtdFileSet = new ArrayList();
		String productFile = "";

		Map harnessFiles = new HashMap();

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
				dtdFileSet.add(normalizedDtdFile);
			}
		}

		debug("Adding DTD files to the harness collection map...");
		harnessFiles.put(dtdKey, dtdFileSet);
		debug("Successfully added DTD files to the harness collection map");

		debug("Retrieving DTD files...completed");

		debug("Retrieving product file...begin");

		//retrieve the IHS product file
		debug("Retrieving product file: " + productFileRepository + baseProductFile + " from resource jar");
		URL baseProductFileURL = getClass().getResource(productFileRepository + baseProductFile);
		if (baseProductFileURL == null) {
			debug("Could not locate " + productFileRepository + baseProductFile + " from the resource jar");
			consumedExceptions.add(
				new UpdateHarnessException("The IHS product file [ " + productFileRepository + baseProductFile + " ] does not exist in the resource jar"));
			setConformsToStrategy(false);
		} else {
			String normalizedProductFile = normalizeURLResource(baseProductFileURL.getFile(), 50);
			debug("Adding " + normalizedProductFile + " to the harnessFile collection");
			productFile = normalizedProductFile;
		}

		debug("Adding Product file to the harness collection map...");
		harnessFiles.put(productKey, productFile);
		debug("Successfully added Product file to the harness collection map...");

		debug("getHarnessFiles()...exited");
		debug("returned a harnessFile set of size : " + harnessFiles.size());

		return harnessFiles;

	}

	public boolean locatedHarnessFiles() {
		return (getHarnessFiles().size() > 0);
	}

	public int numConsumedExceptions() {
		return consumedExceptions.size();
	}

	/**
	 * @param consumedExceptions  the consumedExceptions to set
	 * @uml.property  name="consumedExceptions"
	 */
	public void setConsumedExceptions(List consumedExceptions) {
		this.consumedExceptions = consumedExceptions;
	}

	/**
	 * @return  the consumedExceptions
	 * @uml.property  name="consumedExceptions"
	 */
	public List getConsumedExceptions() {
		return consumedExceptions;
	}
	
	public boolean cleanUp(){
		return cleanUp(constructUpdatePropertiesFileSet());	
	}

	public boolean cleanUp(String dir) {

		boolean result = false;

		if (new File(dir).exists()) {

			debug("Cleaning up directory: " + dir);
			boolean deletedHarnessDir = deleteDir(new File(dir));

			if (deletedHarnessDir) {
				debug("Successfully deleted directory");
				result = true;
			} else
				debug("Failed to delete directory");
		} else {
			result = true;
			debug("Nothing to clean up");
		}

		return result;
	}
	
	public boolean createDir(String dir){
		debug("Recreating directory: " + dir);
		return new File(dir).mkdir();
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns false.
	public boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public void createHarnessFiles() {
		Map harnessFiles = getHarnessFiles();

		debug("Laying down DTD harness files...begin");
		List dtdHarnessFiles = (ArrayList) harnessFiles.get(dtdKey);

		int numDtdHarnessFiles = dtdHarnessFiles.size();
		boolean dtdIOError = false;
		for (int i = 0; i < numDtdHarnessFiles && !dtdIOError; i++) {
			String aDtdHarnessFilePath = (String) dtdHarnessFiles.get(i);
			File aDtdHarnessFile = new File(constructUpdateDTDFileSet() + File.separator + aDtdHarnessFilePath);
			debug("Attempting to create DTD file " + constructUpdateDTDFileSet() + File.separator + aDtdHarnessFilePath);
			try {
				boolean createdDtdHarnessFile = aDtdHarnessFile.createNewFile();

				if (!createdDtdHarnessFile) {
					debug("Failed to create DTD file " + constructUpdateDTDFileSet() + File.separator + aDtdHarnessFilePath);
					dtdIOError = true;
				} else {
					debug("Created DTD file successfully");
				}

			} catch (IOException ie) {
				debug("Failed to create DTD file " + constructUpdateDTDFileSet() + File.separator + aDtdHarnessFilePath + " with an IOException");
				consumedExceptions.add(ie);
			}

		}

		if (dtdIOError) {
			debug("Failed to create DTD files...returning on error");
			setConformsToStrategy(false);
			cleanUp();
			return;
		}

		boolean productIOError = false;
		String productHarnessFilePath = (String) harnessFiles.get(productKey);
		File ProductHarnessFile = new File(constructUpdateVersionFileSet() + File.separator + productHarnessFilePath);
		debug("Attempting to create Product file " + constructUpdateVersionFileSet() + File.separator + productHarnessFilePath);
		try {
			boolean createdProductHarnessFile = ProductHarnessFile.createNewFile();

			if (!createdProductHarnessFile) {
				debug("Failed to create Product file " + constructUpdateVersionFileSet() + File.separator + productHarnessFilePath);
				productIOError = true;
			} else {
				debug("Created Product file successfully");
			}

		} catch (IOException ie) {
			debug("Failed to create Product file " + constructUpdateVersionFileSet() + File.separator + productHarnessFilePath + " with an IOException");
			consumedExceptions.add(ie);
		}

		if (productIOError) {
			debug("Failed to create DTD files...returning on error");
			setConformsToStrategy(false);
			cleanUp();
			return;
		}

		setConformsToStrategy(true);

	}

	public boolean ensureVersionContents(String dir) {
		int totalFileCount = 0;
		boolean scanDTDDir = false, foundProductFile = false;

		File versionFileSet = null;
	
		if(dir.equals(constructUpdateVersionFileSet())){		
			debug("Scanning through directory: " + constructUpdateVersionFileSet());
			versionFileSet = new File(constructUpdateVersionFileSet());
		}else{
			debug("Scanning through directory: " + dir);
			versionFileSet = new File(dir);				
		}

		String[] children = versionFileSet.list();
		for (int i = 0; i < children.length; i++) {
			File locatedFile = new File(versionFileSet, children[i]);
			boolean fileExists = locatedFile.exists();

			//find the /dtd directory								
			if (fileExists && locatedFile.isDirectory() && (locatedFile.getPath().indexOf("dtd") >= 0)) {
				debug("Located /dtd directory");
				scanDTDDir = true;
			} else if(fileExists && locatedFile.isFile() && (locatedFile.getPath().indexOf("IHS.product") >= 0)){
				totalFileCount++;
				foundProductFile = true;
				debug("Located IHS.product file, totalFileCount:"+totalFileCount);
			} else if(fileExists && locatedFile.isFile() && (locatedFile.getPath().indexOf(".dtd") >= 0 || locatedFile.getPath().indexOf(".xsd") >= 0) ){
				totalFileCount++;
				debug("Located a dtd/xsd file, totalFileCount:"+totalFileCount);				
			}
			
			if(scanDTDDir && foundProductFile){
				ensureVersionContents(locatedFile.getAbsolutePath());	
			}
		}
		
		//count the total # of harness files in the resource jar
		List dtdFiles = (ArrayList) getHarnessFiles().get(dtdKey);
		int dtdFileCount = dtdFiles.size();
		int productFileCount = 1;
		int resourceFileCount = dtdFileCount + productFileCount;							
												
		debug("fileSystemFileCount:"+totalFileCount + " | resourceJarFileCount:"+ resourceFileCount);
		if(totalFileCount == resourceFileCount){
			return true;	
		}								
		
		return false;
	}

	public void executeStrategy() {

		debug("executeStrategy(" + productDir + ")...entered");

		debug("Strategy execution already performed: " + strategyExecuted);

		if (!strategyExecuted) {

			if (!isWASLite())
				return;

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

			if (!ensureVersionContents(constructUpdateVersionFileSet())) {
				//clean up only the contents of the version dir
				//and re-populate w/ files
				debug("Filesystem harness files not in sync with the resource jar");
				debug("Cleaning up /version directory");
				if(cleanUp(constructUpdateVersionFileSet()) && createDir(constructUpdateVersionFileSet()) && createDir(constructUpdateDTDFileSet())){
					createHarnessFiles();				
					debug("Creating harness files...completed");
				}else{
					setConformsToStrategy(false);					
					debug("Failed to perform cleanup of /version directory");
				}
			} else {
				debug("Update harness files already present...continue");
				setConformsToStrategy(true);
				return;
			}

		}

	}
}
