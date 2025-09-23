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
package com.ibm.lconn.wizard.cluster.detect;


public class WasDetector  extends AbstractDetector {

	/* (non-Javadoc)
	 * @see com.ibm.lconn.wizard.cluster.detect.AbstractDetector#getResult()
	 */
	@Override
	public String[] getResult() {
		// TODO Auto-generated method stub
		return null;
	}/* extends
		AsynchronousWizardActionWithDescription {

	public static final String TITLE = "$L(com.ibm.wps.install.CommonMsg, Simpletext.warning)";

	public static final String MESSAGE_NOWAS = "$L(com.ibm.wps.install.CommonMsg, Install.check.NoWasInstance)";

	public static final String MESSAGE_NEED_UPGRADE_WAS = "$L(com.ibm.wps.install.CommonMsg, Install.check.UpgradeWasInstance)";

	public void execute(WizardBeanEvent arg0) {
		// TODO Auto-generated method stub
		super.execute(arg0);

		int major = 6;
		int minor = 0;
		int maint = 0;
		int update = 0;

		String vRelease = PortalValidation.PORTAL_MP_PRODUCT;

		VersionInfo versionInfo = new VersionInfo(vRelease, major, minor,
				maint, update);

		PortalValidation validator = new PortalValidation(versionInfo);
		ArrayList currentWas = validator
				.getCurrentLocationsAndVersions(PortalValidation.COMPONENT_WEBSPHERE_APPLICATION_SERVER);

		// check to see if any WAS installed has previous LC versions installed
		// on it
		// no null values
		ArrayList previousLcInstalledWas = getLcInstalledWasLocations();

		int previousLcInstalledWasCount = previousLcInstalledWas.size();

		int currentWasCount = currentWas.size();
		// SPR
		if (currentWasCount == 0) {
			ArrayList previousWas = validator
					.getPreviousLocationsAndVersions((PortalValidation.COMPONENT_WEBSPHERE_APPLICATION_SERVER));
			int previousWasCount = previousWas.size();
			if (previousWasCount > 0) {
				logEvent(
						this,
						Log.WARNING,
						"Intaller has detected a lower version of WebSphere Application Server instance(s) installed on the machine, please upgrade it to version 6.1.0.13 or higher and try again.");
				this
						.getWizard()
						.getUI()
						.displayUserMessage(
								resolveString(WasDetector.TITLE),
								resolveString(WasDetector.MESSAGE_NEED_UPGRADE_WAS),
								UserInputRequest.WARNING);
			} else {
				logEvent(
						this,
						Log.WARNING,
						"WebSphere Application Server Version 6.1.0.13 instance has not been detected on the machine. You may need to install WebSphere Application Server version 6.1.0.13 first.");

				this.getWizard().getUI().displayUserMessage(
						resolveString(WasDetector.TITLE),
						resolveString(WasDetector.MESSAGE_NOWAS),
						UserInputRequest.WARNING);
			}
		} else {

			ArrayList wasLocationList = new ArrayList();
			for (int i = 0; i < currentWasCount; i++) {
				wasLocationList.add(((LocationAndVersionInfo) (currentWas
						.get(i))).getLocation());
			}

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
			setupTargetBean(wasLocationList);
		}
	}

	*//***************************************************************************
	 * @return empty ArrayList if no file found or now was exists.
	 *//*
	private ArrayList getLcInstalledWasLocations() {
		ArrayList returnValue = new ArrayList();

		String log = resolveString("$N($D(temp))")
				+ CheckVPDRegistry.PREVIOUS_VERSIONS_FILE;

		try {
			BufferedReader bfr = new BufferedReader(new FileReader(
					new File(log)));

			logEvent(this, Log.MSG1,
					"Reading previous WAS locations that has Lotus Connections installed from "
							+ log + " file.");

			String currentLine = bfr.readLine();

			while (currentLine != null) {
				// System.out.println(">>>>>>>>>>"+currentLine);
				returnValue.add(currentLine);
				currentLine = bfr.readLine();
			}
		} catch (FileNotFoundException e) {
			logEvent(
					this,
					Log.MSG1,
					"File ("
							+ log
							+ ") containing previous WAS locations that has Lotus Connections installed is not found.");

		} catch (IOException e) {
			logEvent(
					this,
					Log.ERROR,
					"Error occured reading file ("
							+ log
							+ ") containing previous WAS locations that has Lotus Connections installed.");
			logEvent(
					this,
					Log.WARNING,
					"Make sure a WAS locations that has previous version of Lotus Connections installed is not selected as install location.");
		}

		return returnValue;
	}

	// wasFound = currentWas.size();
	//        
	// UserInputFieldChoice[] choices = new UserInputFieldChoice[wasFound];
	// logEvent(this, Log.MSG2, "Number of currently installed WAS:"+wasFound);
	// for (int i = 0; i < wasFound; i++) {
	// String location = "";
	//
	// LocationAndVersionInfo wasInfo = (LocationAndVersionInfo)
	// currentWas.get(i - j);
	// logEvent(this, Log.MSG2, "installed WAS "+i+": "+wasInfo);
	// location = wasInfo.getLocation();
	// }
	// choices[i] = new UserInputFieldChoice();
	// choices[i].setDisplayName(location);
	// choices[i].setValue(location);
	// }

	private String targetBeanID = "";

	// private String targetField = "";

	public void setupTargetBean(List list) {

		WizardBean mywizard = null;
		MasterSlaveSelectionPanelWithArray uip = null;

		try {
			mywizard = getWizardTree().findWizardBean(targetBeanID);
			uip = (MasterSlaveSelectionPanelWithArray) mywizard;

			String[] array = new String[list.size()];
			array = (String[]) list.toArray(array);
			uip.setMasterItemList(array);

		} catch (Exception e) {
			// log.logException(e);
		}
	}

	public String getTargetBeanID() {
		return targetBeanID;
	}

	public void setTargetBeanID(String targetBeanID) {
		this.targetBeanID = targetBeanID;
	}*/

}
