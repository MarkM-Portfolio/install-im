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
package com.ibm.lconn.wizard.update.data;

import javax.swing.ImageIcon;

import com.ibm.websphere.update.ptf.EFixImage;

/**
 * Class: EFixComponent.java Abstract: A java bean that stores eFix information. Component Name: WAS.ptf Release: ASV50X History 1.2, 1/29/04 01-Nov-2002 Initial Version
 */

public class EFixComponent extends UpdateComponent {
	
	//***********************************************************
	// Program Versioning
	//***********************************************************
	public static final String pgmVersion = "1.2" ;
	//***********************************************************
	// Program Versioning
	//***********************************************************
	public static final String pgmUpdate = "1/29/04" ;
	
	//***********************************************************
	// Instance State
	//***********************************************************	
	private boolean selectState = false;

	private static ImageIcon icon = new ImageIcon();

	private int efixId;
	private EFixImage efi;
	private String apar;
	private String pmr;
	private String buildVer;
	private String efixIdStr;
	private String installDate;
	private String installState;
	private String installDescShort;
	private String installDescLong;
	private String prereqs; 
	private String installedTime;

	//***********************************************************
	// Method Definitions
	//***********************************************************
	public ImageIcon getInstallIcon() {
		return icon;
	}

	/**
	 * @param selectState  the selectState to set
	 * @uml.property  name="selectState"
	 */
	public void setSelectState(boolean selectState) {
		this.selectState = selectState;
	}

	/**
	 * @return  the selectState
	 * @uml.property  name="selectState"
	 */
	public boolean getSelectState() {
		return selectState;
	}

	public void setIdStr(String efixIdStr) {
		this.efixIdStr = efixIdStr;
	}

	public String getIdStr() {
		return efixIdStr;
	}

	public void setEFixImage(EFixImage efi) {
		this.efi = efi;
	}

	public EFixImage getEFixImage() {
		return efi;
	}

	public void setBuildVersion(String buildVer) {
		this.buildVer = buildVer;
	}

	public String getBuildVersion() {
		return buildVer;
	}

	public void setId(int efixId) {
		this.efixId = efixId;
	}

	public int getId() {
		return efixId;
	}

	/**
	 * @param installDate  the installDate to set
	 * @uml.property  name="installDate"
	 */
	public void setInstallDate(String installDate) {
		this.installDate = installDate;
	}

	/**
	 * @return  the installDate
	 * @uml.property  name="installDate"
	 */
	public String getInstallDate() {
		return installDate;
	}

	/**
	 * @param installState  the installState to set
	 * @uml.property  name="installState"
	 */
	public void setInstallState(String installState) {
		this.installState = installState;
	}

	/**
	 * @return  the installState
	 * @uml.property  name="installState"
	 */
	public String getInstallState() {
		return installState;
	}

	/**
	 * @param installDescShort  the installDescShort to set
	 * @uml.property  name="installDescShort"
	 */
	public void setInstallDescShort(String installDescShort) {
		this.installDescShort = installDescShort;
	}

	/**
	 * @return  the installDescShort
	 * @uml.property  name="installDescShort"
	 */
	public String getInstallDescShort() {
		return installDescShort;
	}
	
	/**
	 * @param installDescLong  the installDescLong to set
	 * @uml.property  name="installDescLong"
	 */
	public void setInstallDescLong(String installDescLong) {
		this.installDescLong = installDescLong;
	}

	/**
	 * @return  the installDescLong
	 * @uml.property  name="installDescLong"
	 */
	public String getInstallDescLong() {
		return installDescLong;
	}

	/**
	 * @param prereqs  the prereqs to set
	 * @uml.property  name="prereqs"
	 */
	public void setPrereqs(String prereqs) {
		this.prereqs = prereqs;
	}

	/**
	 * @return  the prereqs
	 * @uml.property  name="prereqs"
	 */
	public String getPrereqs() {
		return prereqs;
	}

	public void setAparNum(String apar) {
		this.apar = apar;
	}

	public String getAparNum() {
		return apar;
	}

	public void setPmrNum(String pmr) {
		this.pmr = pmr;
	}

	public String getPmrNum() {
		return pmr;
	}
	
	 /**
     * Record the time the ifix has been installed
     * @param installedTime
     */
	public void setInstalledTime(String installedTime){
		this.installedTime = installedTime;
	}
    
    public String getInstalledTime(){
    	return installedTime;
    }
	public String toString(){
    	return " efixId:" + efixId + " efi:" + efi + " apar:" + apar + 
    	       " pmr:" + pmr + " buildVer:" + buildVer + " efixIdStr:" + efixIdStr +
    	       " installDate:" + installDate +  " installState:" + installState + 
    	       " installDescShort:" + installDescShort + " installDescLong:" + installDescLong + 
    	       " prereqs:" + prereqs; 
    }
}
