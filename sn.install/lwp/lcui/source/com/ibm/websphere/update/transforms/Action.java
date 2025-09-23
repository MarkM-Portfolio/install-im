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
package com.ibm.websphere.update.transforms;


/* 
 * InterfaceName: Action
 * Abstract: A generic interface defined for performing update actions
 * 
 * History 1.2, 11/19/02
 *
 * 30-Aug-2002 Initial Version
 */
public interface Action {
		
	/**
	 *   Performs the action
	 */
	public void performAction() throws TransformUpdateException;
}
