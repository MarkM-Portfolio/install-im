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
 * EFix Driver Bean
 *
 * History 1.2, 9/26/03
 *
 * 12-Sep-2002 Initial Version
 */

import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import java.util.*;

/**
 *  
 */
public class configTask extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    private String  configurationRequired = "false";
    private String  unconfigurationRequired = "false";

    private String  configTaskName = null;
    private String  unconfigTaskName = null;

    public configTask() {
        super();
    }

    public boolean isConfigurationRequiredAsBoolean() { return stringToBoolean(configurationRequired); }
    public String  isConfigurationRequired() { return configurationRequired; }

    public boolean isUnconfigurationRequiredAsBoolean() { return stringToBoolean(unconfigurationRequired); }
    public String  isUnconfigurationRequired() { return unconfigurationRequired; }

    public void setConfigurationRequired( boolean c ) { configurationRequired = booleanToString(c); }
    /**
	 * @param configurationRequired  the configurationRequired to set
	 * @uml.property  name="configurationRequired"
	 */
    public void setConfigurationRequired( String c ) { configurationRequired = c; }

    /**
	 * @param unconfigurationRequired  the unconfigurationRequired to set
	 * @uml.property  name="unconfigurationRequired"
	 */
    public void setUnconfigurationRequired( String c ) { unconfigurationRequired = c; }
    public void setUnconfigurationRequired( boolean c ) { unconfigurationRequired = booleanToString(c); }

    public String getConfigurationTaskName() { return configTaskName; }
    public void   setConfigurationTaskName( String ct ) { configTaskName = ct; }

    public String getUnconfigurationTaskName() { return unconfigTaskName; }
    public void   setUnconfigurationTaskName( String ct ) { unconfigTaskName = ct; }
}
