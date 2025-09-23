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
 * Custom Property Bean
 *
 * History 1.2, 9/26/03
 *
 * 20-Aug-2002 Initial Version
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class customProperty extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public customProperty()
    {
        super();
    }
    
    // Basic field access ...

    protected String propertyName;

    /**
	 * @param propertyName  the propertyName to set
	 * @uml.property  name="propertyName"
	 */
    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    /**
	 * @return  the propertyName
	 * @uml.property  name="propertyName"
	 */
    public String getPropertyName()
    {
        return propertyName;
    }

    protected String propertyType;

    /**
	 * @param propertyType  the propertyType to set
	 * @uml.property  name="propertyType"
	 */
    public void setPropertyType(String propertyType)
    {
        this.propertyType = propertyType;
    }

    /**
	 * @return  the propertyType
	 * @uml.property  name="propertyType"
	 */
    public String getPropertyType()
    {
        return propertyType;
    }

    protected String propertyValue;

    /**
	 * @param propertyValue  the propertyValue to set
	 * @uml.property  name="propertyValue"
	 */
    public void setPropertyValue(String propertyValue)
    {
        this.propertyValue = propertyValue;
    }

    /**
	 * @return  the propertyValue
	 * @uml.property  name="propertyValue"
	 */
    public String getPropertyValue()
    {
        return propertyValue;
    }
}
