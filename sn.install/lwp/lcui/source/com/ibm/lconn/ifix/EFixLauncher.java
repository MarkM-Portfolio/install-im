/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;



public class EFixLauncher {

	

	private String icHome = null;
	private String dmgrHome = null;

	private String wasUserId = null;
	private String wasPassword = null;
	
	private String feature = null;
	private String apar = null;
	
	public boolean install = false;
	public String getIcHome() {
		return icHome;
	}

	public void setIcHome(String icHome) {
		this.icHome = icHome;
	}

	public String getDmgrHome() {
		return dmgrHome;
	}

	public void setDmgrHome(String dmgrHome) {
		this.dmgrHome = dmgrHome;
	}

	public String getWasUserId() {
		return wasUserId;
	}

	public void setWasUserId(String wasUserId) {
		this.wasUserId = wasUserId;
	}

	public String getWasPassword() {
		return wasPassword;
	}

	public void setWasPassword(String wasPassword) {
		this.wasPassword = wasPassword;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getApar() {
		return apar;
	}

	public void setApar(String apar) {
		this.apar = apar;
	}

	public boolean isInstall() {
		return install;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

	public boolean isUninstall() {
		return uninstall;
	}

	public void setUninstall(boolean uninstall) {
		this.uninstall = uninstall;
	}

	public boolean uninstall = false;


	public boolean isComplete = false;
	public String errorArg = null;
	public String errorCode = null;


	
	public void parse(String[] args) {
		int argNo = 0;

		while (!isComplete && (argNo < args.length)) {
			String nextArg = args[argNo++];

			if (nextArg.equalsIgnoreCase("-feature")) {
				
				if (argNo < args.length) {
					setFeature(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "feature.missing";
				}

			}  else if (nextArg.equalsIgnoreCase("-apar")) {
				if (argNo < args.length) {
					setApar(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "apar.missing";
				}

			}		
			else if (nextArg.equalsIgnoreCase("-wasPassword")) {
				if (argNo < args.length) {
					setWasPassword(args[argNo++]);
				}  else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "wasPassword.missing";
				}

			} else if (nextArg.equalsIgnoreCase("-wasUserId")) {
				if (argNo < args.length) {
					setWasUserId(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "wasUserId.missing";
				}

			} else if (nextArg.equalsIgnoreCase("-icHome")) {
				if (argNo < args.length) {
					setIcHome(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "icHome.missing";
				}

			}  else if (nextArg.equalsIgnoreCase("-dmgrHome")) {
				if (argNo < args.length) {
					setDmgrHome(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "dmgrHome.missing";
				}

			}
			else if (nextArg.equalsIgnoreCase("-install")) {
				setInstall(true);

			} else if (nextArg.equalsIgnoreCase("-uninstall")) {
				setUninstall(true);

			} 
		}

}
   
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
