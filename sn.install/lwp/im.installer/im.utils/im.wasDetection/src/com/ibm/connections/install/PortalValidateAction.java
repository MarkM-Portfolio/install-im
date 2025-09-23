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
import java.util.ArrayList;

import com.ibm.wps.depcheck.LocationAndVersionInfo;
import com.ibm.wps.depcheck.PortalValidation;
import com.ibm.wps.depcheck.VersionInfo;

public class PortalValidateAction {

	protected static PortalValidation validator = null;


		
	public static PortalValidation getValidator() {

			if (null == validator) {

				int major = 6;
				int minor = 0;
				int maint = 0;
				int update = 0;
				String vRelease = PortalValidation.PORTAL_MP_PRODUCT;

				VersionInfo versionInfo = new VersionInfo(vRelease, major, minor,maint, update);

				validator = new PortalValidation(versionInfo);
			}

			return validator;
	}

	
	public static boolean validateWasVersion() {
		
		int major = 6;
		int minor = 0;
		int maint = 0;
		int update = 0;

		boolean isISeries = false;
		// try {
		// OS400Service os = (OS400Service) getServices().getService(
		// OS400Service.NAME);
		// if (os.installType() == OS400Service.REMOTE
		// || os.installType() == OS400Service.NATIVE) {
		// isISeries = true;
		// }
		// } catch (Exception e) {
		//
		// logEvent(this, Log.ERROR, e);
		// }

		String vRelease = "";
		/*
		 * for (int i = 0; i < projectRelease.length; i++) { if
		 * (release.equals(projectRelease[i])) { vRelease =
		 * validationRelease[i]; break; } }
		 */

		/***********************************************************************
		 * if (resolveString("$W(defaults.familyName)").equals("Toolkit")) { //
		 * public final static String PORTAL_TOOLKIT_PRODUCT = "wpstk";
		 * logEvent(this, Log.MESSAGE, "Setting toolkit family"); //vRelease =
		 * "wpstk"; vRelease = PortalValidation.PORTAL_TOOLKIT_PRODUCT; }
		 **********************************************************************/

		if (vRelease.equals("")) {

		}

		VersionInfo versionInfo = new VersionInfo(vRelease, major, minor,
				maint, update);

		String rulesDirectory = "";
		if (!rulesDirectory.equals("")) {
			boolean depcheckFilesExists = false;
			File customRulesDirectory = new File(rulesDirectory);
			if (customRulesDirectory.exists()) {
				if (customRulesDirectory.isDirectory()) {
					File DepCheckBaseFile = new File(rulesDirectory
							+ System.getProperty("file.separator")
							+ "DepCheckBase.arl");
					File DiscoveryFile = new File(rulesDirectory
							+ System.getProperty("file.separator")
							+ "Discovery.rules");
					File PortalValidationFile = new File(rulesDirectory
							+ System.getProperty("file.separator")
							+ "PortalValidation.properties");
					File SoftwareWP6File = new File(rulesDirectory
							+ System.getProperty("file.separator")
							+ "SoftwareWP6.rules");

					if (DepCheckBaseFile.exists() && DiscoveryFile.exists()
							&& PortalValidationFile.exists()
							&& SoftwareWP6File.exists()) {
						// custom rules directory is a directory, and all files
						// exist
						depcheckFilesExists = true;
					} else {
						// all depcheck files do not exist in custom rules
						// directory
						depcheckFilesExists = false;
					}
				} else {
					// custom rules directory is not a directory
					depcheckFilesExists = false;

				}
			} else {
				// custom rules directory does not exist
				depcheckFilesExists = false;
			}

			if (!depcheckFilesExists) {
				// if (event.getUserInterface() != null) {
				// This is not silent mode, so we can display a warning

				validator = new PortalValidation(versionInfo);
				// } else {
				// This is silent mode, so log the error and exit

				System.exit(-1);
				// }
			} else {
				validator = new PortalValidation(versionInfo, rulesDirectory);
			}
		} else {
			validator = new PortalValidation(versionInfo);
		}

		boolean previousWasDetected = false;
		boolean currentWasDetected = false;
		boolean anyWasDetected = false;
		ArrayList currentWas = validator
				.getCurrentLocationsAndVersions(PortalValidation.COMPONENT_WEBSPHERE_APPLICATION_SERVER);
		if (currentWas.size() > 0) {
			for (int i = 0; i < currentWas.size(); i++) {
				LocationAndVersionInfo wasInfo = (LocationAndVersionInfo) currentWas
						.get(i);
			}
			currentWasDetected = true;
		}
		ArrayList previousWas = validator
				.getPreviousLocationsAndVersions(PortalValidation.COMPONENT_WEBSPHERE_APPLICATION_SERVER);
		if (previousWas.size() > 0) {
			for (int i = 0; i < previousWas.size(); i++) {
				LocationAndVersionInfo wasInfo = (LocationAndVersionInfo) previousWas
						.get(i);
			}
			previousWasDetected = true;
		}
		anyWasDetected = previousWasDetected || currentWasDetected;
		if (!anyWasDetected && !isISeries) {

		}

		/***********************************************************************
		 * // Defect 41404 areeves if (!isISeries) { setWasRequiredSpace( "" +
		 * validator.getDiskSpaceRequirement(PortalValidation.
		 * COMPONENT_WEBSPHERE_APPLICATION_SERVER) ); if (
		 * getWasRequiredSpace().equals("") ||
		 * getWasRequiredSpace().equals("-1")) { logEvent(this, Log.ERROR,
		 * "ERROR Invalid disk space requirement for WAS"); } else {
		 * logEvent(this, Log.MESSAGE, "Space required for WAS =
		 * "+getWasRequiredSpace()+" KB");
		 * setWasRequiredSpaceDisplay(String.valueOf
		 * (Math.round(((Double.parseDouble(getWasRequiredSpace()) *
		 * 0.0009765625) * 10.0))/10)); logEvent(this, Log.MESSAGE, "Space
		 * required for WAS = "+getWasRequiredSpaceDisplay()+" MB"); }
		 * 
		 * setWpsRequiredSpace( "" +
		 * validator.getDiskSpaceRequirement(PortalValidation
		 * .COMPONENT_PORTAL_SERVER) ); if ( getWpsRequiredSpace().equals("") ||
		 * getWpsRequiredSpace().equals("-1")) { logEvent(this, Log.ERROR,
		 * "ERROR Invalid disk space requirement for WPS"); } else {
		 * logEvent(this, Log.MESSAGE, "Space required for WPS =
		 * "+getWpsRequiredSpace()+" KB");
		 * setWpsRequiredSpaceDisplay(String.valueOf
		 * (Math.round(((Double.parseDouble(getWpsRequiredSpace()) *
		 * 0.0009765625) * 10.0))/10)); logEvent(this, Log.MESSAGE, "Space
		 * required for WPS = "+getWpsRequiredSpaceDisplay()+" MB"); }
		 * 
		 * setIhsRequiredSpace( "" +
		 * validator.getDiskSpaceRequirement(PortalValidation
		 * .COMPONENT_IBM_HTTP_SERVER) ); if ( getIhsRequiredSpace().equals("")
		 * || getIhsRequiredSpace().equals("-1")) { logEvent(this, Log.ERROR,
		 * "ERROR Invalid disk space requirement for IHS"); } else {
		 * logEvent(this, Log.MESSAGE, "Space required for IHS =
		 * "+getIhsRequiredSpace()+" KB"); } // Defect 46619
		 * setTempWasfpRequiredSpace( "" +
		 * validator.getTempDiskSpaceRequirement(
		 * PortalValidation.COMPONENT_WAS_FIXPACKS) ); if (
		 * getTempWasfpRequiredSpace().equals("") ||
		 * getTempWasfpRequiredSpace().equals("-1")) { logEvent(this, Log.ERROR,
		 * "ERROR Invalid disk space requirement for TEMPWASFP"); } else {
		 * logEvent(this, Log.MESSAGE, "Space required for TEMPWASFP =
		 * "+getTempWasfpRequiredSpace()+" KB"); }
		 * 
		 * logEvent(this, Log.MESSAGE, "useValidation="+useValidation);
		 * 
		 * logEvent(this, Log.MESSAGE, "Checking prereqs.."); checkPrereqs(); }
		 **********************************************************************/
		if (!isISeries) {
			checkPrereqs();
		}

		return true;
	}

	protected static void checkPrereqs() {
		VersionInfo versionInfo = validator
				.getInstalledVersion(PortalValidation.COMPONENT_OPERATING_SYSTEM);
		boolean osCurrent = validator
				.getInstalledVersionIsCurrent(PortalValidation.COMPONENT_OPERATING_SYSTEM);

		if (!osCurrent) {
			// SPR ZNJN7B45ST
			// if the os is Red Hat 5.1.0.0, than
			if ("redhat".equals(versionInfo.getLabel())) {
				if (versionInfo.getMajor() >= 5) {
					
					return;
				}
			} else {
			
				// InstallAndConfigLogger logger = MsgLogAction.getLogger();
				// logger.logrb(Level.SEVERE, getClass().getName(),
				// "checkPrereqs", null,
				// resolveString("$L(com.ibm.wps.install.CommonMsg, EJPI0007E,
				// $N($D(temp)/$W(defaults.messageLog)))"));
		
				/*if (resolveString("$W(defaults.isSilent)").equalsIgnoreCase(
						"true")) {
					// can't prompt the user in silent mode..
					
				} else {
					setMessage("");
				}*/
			}
		}
	}

}
