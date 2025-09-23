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
package com.ibm.websphere.update.ptf;

/*
 * History 1.1, 12/20/02
 *
 * 17-Dec-2002 Initial version.
 */

import com.ibm.websphere.product.history.xml.componentVersion;
import com.ibm.websphere.product.xml.component.component;

/**
 *  
 */
public class ExtendedComponent
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "12/20/02" ;

    // Instantor ...
    //
    // public ExtendedComponent(component, String, componentVersion);

    public ExtendedComponent(component baseComponent,
                             String location,
                             componentVersion priorVersion)
    {
        this.baseComponent = baseComponent;
        this.location      = location;
        this.priorVersion  = priorVersion;
    }

    protected component baseComponent;

    /**
	 * @return  the baseComponent
	 * @uml.property  name="baseComponent"
	 */
    public component getBaseComponent()
    {
        return baseComponent;
    }

    protected String location;

    /**
	 * @return  the location
	 * @uml.property  name="location"
	 */
    public String getLocation()
    {
        return location;
    }

    protected componentVersion priorVersion;

    /**
	 * @return  the priorVersion
	 * @uml.property  name="priorVersion"
	 */
    public componentVersion getPriorVersion()
    {
        return priorVersion;
    }
}
