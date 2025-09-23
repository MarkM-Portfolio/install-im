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

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.update.efix.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.table.*;

/**
 * Class: UpdateComponent.java Abstract: An abstract java bean that stores update component information. Component Name: WAS.ptf Release: ASV50X History 1.2, 1/29/04 01-Nov-2002 Initial Version
 */

public abstract class UpdateComponent {
	
	//***********************************************************
	// Program Versioning
	//***********************************************************
	public static final String pgmVersion = "1.2" ;
	//***********************************************************
	// Program Versioning
	//***********************************************************
	public static final String pgmUpdate = "1/29/04" ;
	
	//***********************************************************
	// Abstract Method Definitions
	//***********************************************************

	/**
	 * @param selectState
	 * @uml.property  name="selectState"
	 */
	public abstract void setSelectState(boolean selectState);

	/**
	 * @return
	 * @uml.property  name="selectState"
	 */
	public abstract boolean getSelectState();

	/**
	 * @param idStr
	 * @uml.property  name="idStr"
	 */
	public abstract void setIdStr(String idStr);

	/**
	 * @return
	 * @uml.property  name="idStr"
	 */
	public abstract String getIdStr();
	
	/**
	 * @param buildVer
	 * @uml.property  name="buildVersion"
	 */
	public abstract void setBuildVersion(String buildVer);

	/**
	 * @return
	 * @uml.property  name="buildVersion"
	 */
	public abstract String getBuildVersion();

	/**
	 * @param id
	 * @uml.property  name="id"
	 */
	public abstract void setId(int id);

	/**
	 * @return
	 * @uml.property  name="id"
	 */
	public abstract int getId();

	/**
	 * @param installDate
	 * @uml.property  name="installDate"
	 */
	public abstract void setInstallDate(String installDate);

	/**
	 * @return
	 * @uml.property  name="installDate"
	 */
	public abstract String getInstallDate();

	/**
	 * @param installState
	 * @uml.property  name="installState"
	 */
	public abstract void setInstallState(String installState);

	/**
	 * @return
	 * @uml.property  name="installState"
	 */
	public abstract String getInstallState();

	/**
	 * @param installDescShort
	 * @uml.property  name="installDescShort"
	 */
	public abstract void setInstallDescShort(String installDescShort);

	/**
	 * @return
	 * @uml.property  name="installDescShort"
	 */
	public abstract String getInstallDescShort();
	
	/**
	 * @param installDescLong
	 * @uml.property  name="installDescLong"
	 */
	public abstract void setInstallDescLong(String installDescLong);

	/**
	 * @return
	 * @uml.property  name="installDescLong"
	 */
	public abstract String getInstallDescLong();
}
