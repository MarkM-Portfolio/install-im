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

/**
 * Class: IconIdentifier.java Abstract: A bean to encapsulate install state for a single efix. Component Name: WAS.ptf Release: ASV50X History 1.2, 1/29/04 01-Nov-2002 Initial Version
 */

public class IconIdentifier {

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
    private String installState;

	//***********************************************************
	// Method Definitions
	//***********************************************************
    public IconIdentifier(String installState) {
        this.installState = installState;
    }

    /**
	 * @return  the installState
	 * @uml.property  name="installState"
	 */
    public String getInstallState() {
        return installState;
    }
}