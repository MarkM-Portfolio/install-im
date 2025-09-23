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

package com.ibm.connections.install;

import com.ibm.cic.agent.core.api.IAgentJob;
import com.ibm.cic.common.core.model.IOffering;

public class Util {
	private Util() {
	}
	
	/**
	 * Iterate through IAgentJob and return an offering instance from a job
	 * found in that array, which matches the specified offeringId. 
	 * 
	 * @param jobs
	 * @param offeringId
	 * @return IAgentJob that refers to the specified offeringId
	 */
	public static IOffering findOffering(IAgentJob[] jobs, String offeringId) {
		for(IAgentJob job : jobs) {
			IOffering offering = job.getOffering();
			if(offering != null && offering.getIdentity().getId().equals(offeringId) == true) {
				return offering;
			}
		}
		return null;
	}
}
