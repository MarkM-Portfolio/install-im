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

package com.ibm.websphere.update.delta;

/**
 * @author  guminy  To change this generated comment edit the template variable "typecomment":  Window>Preferences>Java>Templates.  To enable and disable the creation of type comments go to  Window>Preferences>Java>Code Generation.
 */
public interface PermissionHelper {

	public void setFilename(String fileName);
	
	/**
	 * @param owner
	 * @return
	 * @uml.property  name="owner"
	 */
	public int setOwner(String owner);
	
	/**
	 * @param permissions
	 * @return
	 * @uml.property  name="permissions"
	 */
	public int setPermissions(String permissions);
	
	/**
	 * @param group
	 * @return
	 * @uml.property  name="group"
	 */
	public int setGroup(String group);
	
	/**
	 * @return
	 * @uml.property  name="permissions"
	 */
	public String getPermissions();
	
	/**
	 * @return
	 * @uml.property  name="group"
	 */
	public String getGroup();
	
	/**
	 * @return
	 * @uml.property  name="owner"
	 */
	public String getOwner();
	
}
