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
package com.ibm.websphere.update.ptf;

/*
 * PTF Install Image
 *
 * This PTFImage object represents a single ptf as it
 * is shipped to a customer.
 *
 * History 1.7, 3/26/03
 *
 * 09-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
 */

/**
 * <p>Expected ptf jar contents.  One or more ptf may be
 * present, as follows:</p>
 * <pre>
 *     ptfs/
 *
 *     ptfs/ptf1/
 *     ptfs/ptf1/ptf1.ptf
 *     ptfs/ptf1/components/
 *     ptfs/ptf1/components/component1/
 *     ptfs/ptf1/components/component1/update.jar
 *     ptfs/ptf1/components/component2/
 *     ptfs/ptf1/components/component2/update.jar
 *
 *     ptfs/ptf2/
 *     ptfs/ptf2/ptf2.ptf
 *     ptfs/ptf2/components/
 *     ptfs/ptf2/components/component1/
 *     ptfs/ptf2/components/component1/update.jar
 *     ptfs/ptf2/components/component2/
 *     ptfs/ptf2/components/component2/update.jar
 *
 */

import java.io.*;
import java.util.*;

import org.xml.sax.InputSource;

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.history.xml.*;

import com.ibm.websphere.update.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.ioservices.*;

public class PTFImage extends UpdateImage {
	// Program versioning ...

	public static final String pgmVersion = "1.7" ;
	// Program versioning ...

	public static final String pgmUpdate = "3/26/03" ;

	// Instantor ...

	public PTFImage(IOService ioService, String dtdDirName, String jarName, String ptfId) {
		super(ioService, dtdDirName, jarName, ptfId);
	}

	public String getPTFId() {
		return getUpdateId();
	}

	// PTF Entry Identification ...

	public static final String PTFS_DIR = "ptfs";

	public String getUpdatesDir() {
		return PTFS_DIR;
	}

	public String getDriverExtension() {
		return AppliedHandler.PTF_DRIVER_FILE_EXTENSION;
	}

	// PTF Access ...

	public ptfDriver getPTFDriver() {
		return (ptfDriver) getDriver();
	}




	public void prepareExtendedComponentImages(WASProduct wasp, String extendedComponent)
		throws IOException, IOServicesException {

		File backupDir = new File(wasp.getBackupDirName());
		Map componentImages = getComponentImages();
		int numComponents = componentImages.size();
		IOService useIOService = getIOService();

		if (componentImages.containsKey(extendedComponent)) {
			ComponentImage compImage = (ComponentImage) componentImages.get(extendedComponent);

			List jarEntryNames = compImage.getPrimaryContentEntryName();
			int numJarEntryNames = jarEntryNames.size();

			for (int i = 0; i < numJarEntryNames; i++) {
				String aJarEntryName = (String) jarEntryNames.get(i);

				if (OSUtil.isWindows()) {
					// Windows machine primary jar entry syntax --> CSD!Setup
					// the wmservice.exe resides in Setup --> Setup/wmservice.exe
					// this will be backed up and used to perform the uninstallation
					if (aJarEntryName.indexOf("Setup") >= 0) {

						Vector entries =
							useIOService.getChildEntryNames(compImage.getJarName(), aJarEntryName);
						int numEntries = entries.size();
						for (int j = 0; j < numEntries; j++) {
							String aChildEntryName = (String) entries.elementAt(j);
							String fileToExtract = aJarEntryName + File.separator + aChildEntryName;

							String anExtractedFile =
								useIOService.extractJarEntry(
									compImage.getJarName(),
									fileToExtract,
									wasp.getBackupDirName() + File.separator + extendedComponent);

						}
					}
				} else if (OSUtil.isAIX() || OSUtil.isLinux() || OSUtil.isSolaris()) {
					// Unix machine primary jar entry syntax --> CSD
					// the wmservice script resides in CSD --> CSD/wmservice.exe
					// this will be backed up and used to perform the uninstallation
					if (aJarEntryName.indexOf("CSD") >= 0) {

						Vector entries =
							useIOService.getChildEntryNames(compImage.getJarName(), aJarEntryName);
						int numEntries = entries.size();
						for (int j = 0; j < numEntries; j++) {
							String aChildEntryName = (String) entries.elementAt(j);
							if (!aChildEntryName.endsWith("images")) {
								String fileToExtract = aJarEntryName + File.separator + aChildEntryName;
					
								if(!backupDir.exists()){
									backupDir.mkdir();				
								}

								String anExtractedFile =
									useIOService.extractJarEntry(
										compImage.getJarName(),
										fileToExtract,
										wasp.getBackupDirName() + File.separator + extendedComponent);

							//	setScriptPermissions(wasp.getBackupDirName() + File.separator + extendedComponent + File.separator + "wmservice", "755");
							}

						}
					}

				}
			}

		}
	}
	
	protected boolean setScriptPermissions(String file, String chmodValue) {
		boolean taskResult = true;

		String task = "chmod " + chmodValue + " " + file;

		Vector results = new Vector();
		Vector logBuffer = new Vector();

		ExecCmd exec = new ExecCmd();
		int rc = exec.Execute(task,
							  ExecCmd.DONT_ECHO_STDOUT, ExecCmd.DONT_ECHO_STDERR,
							  results, logBuffer);

		if (rc != 0)
			taskResult = false;

		return taskResult;
	}
	

	protected ComponentImage createComponentImage(UpdateImage parentImage, String componentName) {
		return new PTFComponentImage((PTFImage) parentImage, componentName);
	}
}
