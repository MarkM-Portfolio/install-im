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
 * EFix Applied Bean
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
public class configApplied extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    private String  configured          = "false";
    private String  configurationActive = "false";
    private String  configName          = "";

    // Instantor ...

    public configApplied() {
        super();
    }

    public String  isConfigured() { return configured; }
    public boolean isConfiguredAsBoolean() { return stringToBoolean(configured); }

    public String  isConfigurationActive() { return configurationActive; }
    public boolean isConfigurationActiveAsBoolean() { return stringToBoolean(configurationActive); }

    public void setConfigured( boolean c ) { configured = booleanToString(c); }
    /**
	 * @param configured  the configured to set
	 * @uml.property  name="configured"
	 */
    public void setConfigured( String  c ) { configured = c; }

    public void setConfigurationActive( boolean c ) { configurationActive = booleanToString(c); }
    /**
	 * @param configurationActive  the configurationActive to set
	 * @uml.property  name="configurationActive"
	 */
    public void setConfigurationActive( String  c ) { configurationActive = c; }

    /**
	 * @return  the configName
	 * @uml.property  name="configName"
	 */
    public String getConfigName() { return configName; }

    /**
	 * @param configName  the configName to set
	 * @uml.property  name="configName"
	 */
    public void setConfigName( String cn ) { configName = cn; }

}
