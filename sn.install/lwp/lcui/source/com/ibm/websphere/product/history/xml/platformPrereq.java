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
 * EFix Platform PreRequisite Bean
 *
 * History 1.2, 9/26/03
 *
 * 01-Jul-2002 Initial Version
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class platformPrereq extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // The values, above, are taken from those which java makes available
    // through system properties.

    // Use the wildcard, below, to indicate that the field is not
    // to be checked.

    public static final String WILDCARD_SPECIFICATION = "*";

    // Instantor ...

    public platformPrereq()
    {
        super();
    }
    
    // Basic field access ...

    protected String architecture;

    /**
	 * @param architecture  the architecture to set
	 * @uml.property  name="architecture"
	 */
    public void setArchitecture(String architecture)
    {
        this.architecture = architecture;
    }
    
    /**
	 * @return  the architecture
	 * @uml.property  name="architecture"
	 */
    public String getArchitecture()
    {
        return architecture;
    }

    protected String osPlatform;
    
    public void setOSPlatform(String osPlatform)
    {
        this.osPlatform = osPlatform;
    }
    
    public String getOSPlatform()
    {
        return osPlatform;
    }

    protected String osVersion;
    
    public void setOSVersion(String osVersion)
    {
        this.osVersion = osVersion;
    }
    
    public String getOSVersion()
    {
        return osVersion;
    }
}
