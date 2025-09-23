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
 * Class: UpdateListingData.java Abstract: Abstract class that encapsulates update component information  Component Name: WAS.ptf Release: ASV50X History 1.2, 1/29/04 01-Nov-2002 Initial Version
 */
 
public class UpdateListingData {

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
	private Boolean m_selectState;
	private UpdateComponent m_comp;

	public UpdateListingData(Boolean selectState, UpdateComponent comp) {
		this.m_selectState = selectState;
		this.m_comp = comp;
	}

	//***********************************************************
	// Method Definitions
	//***********************************************************
	public UpdateComponent getUpdateComponent() {
		return m_comp;	
	}

	public Boolean getSelectState() {
		return m_selectState;
	}
	
	public void setSelectState(Boolean selectState){
		m_selectState = selectState;
	}	
}
