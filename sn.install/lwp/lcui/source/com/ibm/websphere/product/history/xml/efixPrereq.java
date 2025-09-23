/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product.history.xml;

/*
 * EFix PreRequisite Bean
 *
 * History 1.2, 9/26/03
 *
 * 20-Aug-2002 Initial version.
 *             
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class efixPrereq extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Note that a corequisite relationship may be specified by creating
    // a prerequisite loop.

    // Note that if A is a negative prerequisite of B, if A installed, then
    // B may not be installed, however, if B is installed, then A may
    // subsequently be installed.

    // Instantor ...

    public efixPrereq()
    {
        super();
    }
    
    protected String efixId;

    public void setEFixId(String efixId)
    {
        this.efixId = efixId;
    }
    
    public String getEFixId()
    {
        return efixId;
    }

    protected String isNegative;

    /**
	 * @param isNegative  the isNegative to set
	 * @uml.property  name="isNegative"
	 */
    public void setIsNegative(String isNegative)
    {
        this.isNegative = isNegative;
    }

    public void setIsNegative(boolean isNegative)
    {
        this.isNegative = booleanToString(isNegative);
    }
    
    /**
	 * @return  the isNegative
	 * @uml.property  name="isNegative"
	 */
    public String getIsNegative()
    {
        return isNegative;
    }
    
    public boolean getIsNegativeAsBoolean()
    {
        return stringToBoolean(isNegative);
    }

    protected String installIndex;

    /**
	 * @param installIndex  the installIndex to set
	 * @uml.property  name="installIndex"
	 */
    public void setInstallIndex(String installIndex)
    {
        this.installIndex = installIndex;
    }
    
    /**
	 * @return  the installIndex
	 * @uml.property  name="installIndex"
	 */
    public String getInstallIndex()
    {
        return installIndex;
    }
}
