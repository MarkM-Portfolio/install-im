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
package com.ibm.connections.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.ibm.wps.depcheck.LocationAndVersionInfo;
import com.ibm.wps.depcheck.PortalValidation;
import com.ibm.wps.depcheck.VersionInfo;

public class WasDetectAction {

	public static final String NO_WAS_ERROR = "no_was_detective";

	public static final String TITLE = "$L(com.ibm.wps.install.CommonMsg, Simpletext.warning)";

	public static final String MESSAGE_NOWAS = "$L(com.ibm.wps.install.CommonMsg, Install.check.NoWasInstance)";

	public static final String MESSAGE_NEED_UPGRADE_WAS = "$L(com.ibm.wps.install.CommonMsg, Install.check.UpgradeWasInstance)";

	private static String message = "";

	public static String getMessage() {
		return message;
	}

	public static void setMessage(String msg) {
		message = msg;
	}

	public static ArrayList execute() throws Exception {
		// TODO Auto-generated method stub

		int major = 6;
		int minor = 0;
		int maint = 0;
		int update = 0;

		String vRelease = PortalValidation.PORTAL_MP_PRODUCT;

		VersionInfo versionInfo = new VersionInfo(vRelease, major, minor, maint, update);

		PortalValidation validator = new PortalValidation(versionInfo);
		ArrayList currentWas = validator.getCurrentLocationsAndVersions(PortalValidation.COMPONENT_WEBSPHERE_APPLICATION_SERVER);
		// check to see if any WAS installed has previous LC versions installed
		// on it
		// no null values
		//		ArrayList previousLcInstalledWas = getLcInstalledWasLocations();

		//		int previousLcInstalledWasCount = previousLcInstalledWas.size();

		int currentWasCount = currentWas.size();
		// SPR
		if (currentWasCount == 0) {
			ArrayList previousWas = validator
					.getPreviousLocationsAndVersions((PortalValidation.COMPONENT_WEBSPHERE_APPLICATION_SERVER));
			int previousWasCount = previousWas.size();
			if (previousWasCount > 0) {
				setMessage(WasDetectAction.TITLE + WasDetectAction.MESSAGE_NEED_UPGRADE_WAS);

				/*logEvent(
						this,
						Log.WARNING,
						"Intaller has detected a lower version of WebSphere Application Server instance(s) installed on the machine, please upgrade it to version 6.1.0.13 or higher and try again.");
				*/} else {
				setMessage(WasDetectAction.TITLE + WasDetectAction.MESSAGE_NOWAS);

				/*logEvent(
						this,
						Log.WARNING,
						"WebSphere Application Server Version 6.1.0.13 instance has not been detected on the machine. You may need to install WebSphere Application Server version 6.1.0.13 first.");
				*/}
		} else {

			ArrayList wasLocationList = new ArrayList();
			for (int i = 0; i < currentWasCount; i++) {
				wasLocationList.add(((LocationAndVersionInfo) (currentWas.get(i))).getLocation());
			}
			return wasLocationList;
			// for (int j=0; j < previousLcInstalledWasCount; j++)
			// {
			// String currentPreviousLcInstalledWas =
			// previousLcInstalledWas.get(j).toString();
			// for (int k=currentWasCount - 1; k >= 0 ; k--)
			// {
			// String currentWasLocationString =
			// ((LocationAndVersionInfo)currentWas.get(k)).getLocation();
			//	        	        			        
			// //if match found remove from list
			// if
			// (currentWasLocationString.equals(currentPreviousLcInstalledWas))
			// {
			// currentWas.remove(k);
			// }
			// }
			// }
		}
		return null;
	}

	/***************************************************************************
	 * @return empty ArrayList if no file found or now was exists.
	 */
	//	private static ArrayList getLcInstalledWasLocations() {
	//		ArrayList returnValue = new ArrayList();
	//
	//		String log = "$N($D(temp))" + CheckVPDRegistry.PREVIOUS_VERSIONS_FILE;
	//
	//		try {
	//			BufferedReader bfr = new BufferedReader(new FileReader(
	//					new File(log)));
	//
	//			/*logEvent(this, Log.MSG1,
	//					"Reading previous WAS locations that has Lotus Connections installed from "
	//							+ log + " file.");*/
	//
	//			String currentLine = bfr.readLine();
	//
	//			while (currentLine != null) {
	//				// System.out.println(">>>>>>>>>>"+currentLine);
	//				returnValue.add(currentLine);
	//				currentLine = bfr.readLine();
	//			}
	//		} catch (FileNotFoundException e) {
	//			/*logEvent(
	//					this,
	//					Log.MSG1,
	//					"File ("
	//							+ log
	//							+ ") containing previous WAS locations that has Lotus Connections installed is not found.");
	//*/
	//		} catch (IOException e) {
	//			/*logEvent(
	//					this,
	//					Log.ERROR,
	//					"Error occured reading file ("
	//							+ log
	//							+ ") containing previous WAS locations that has Lotus Connections installed.");
	//			logEvent(
	//					this,
	//					Log.WARNING,
	//					"Make sure a WAS locations that has previous version of Lotus Connections installed is not selected as install location.");
	//		*/}
	//
	//		return returnValue;
	//	}
	public static PrintWriter getWriter(String file) throws IOException {
		File f = new File(file);
		if (f.exists())
			f.delete();
		PrintWriter pWriter = null;
		pWriter = new PrintWriter(new FileWriter(file, false));
		return pWriter;
	}

	private static Set profileSet = new HashSet();
	public static void main(String[] args) {
		PrintWriter pw = null;
		if (args.length == 1) {
			try {
				ArrayList wasLoaTem = new ArrayList();
				pw = getWriter(args[0]);
				wasLoaTem = WasDetectAction.execute();
				
				for (int i = 0; i < wasLoaTem.size(); i++) {
					profileSet.clear();
					String wasLoc = wasLoaTem.get(i).toString();
					pw.println(wasLoc);
					ArrayList wasProfileNames = DetectiveProfileAction.getDMProfileNew(wasLoc);
					for (int j = 0; j < wasProfileNames.size(); j++) {
						String name = (String) wasProfileNames.get(j);
						
						if(!profileSet.contains(name)){
							pw.println("--" + name);
							profileSet.add(name);
						}
					}
				}
			} catch (Exception e1) {
//				e1.printStackTrace();
			} finally {
				pw.close();
			}
		}
	}
}
