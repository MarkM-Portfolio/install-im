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
package com.ibm.lconn.wizard.common.interfaces;

import com.ibm.lconn.wizard.common.test.TestDataOffer;

/**
 * 
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 * 
 */
public class TDIPopulatePageController extends DefaultPageController {
	private static boolean populated = false;

	public TDIPopulatePageController(String wizardId) {
		super(wizardId);
	}

	public String performAction(String basePage, String actionName) {
		TestDataOffer.setMappingTable();
		TestDataOffer.setLDAPAttributeList();
		populateData();
		String nextPage = super.performAction(basePage, actionName);
		return nextPage;
	}

	private void populateData() {
		if (populated) {
			return;
		}

		return;
	}
}
