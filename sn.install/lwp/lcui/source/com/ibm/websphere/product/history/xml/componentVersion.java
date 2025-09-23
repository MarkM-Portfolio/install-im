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
 * Component Version Bean
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
public class componentVersion extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public componentVersion()
    {
        super();
    }
    
    // Basic field access ...

    protected String componentName;

    /**
	 * @param componentName  the componentName to set
	 * @uml.property  name="componentName"
	 */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }
    
    protected String selectiveUpdate;
    /**
	 * @param selectiveUpdate  the selectiveUpdate to set
	 */
    public void setSelectiveUpdate(String selectiveUpdate)
    {
    	this.selectiveUpdate = selectiveUpdate;
    }
    /**
	 * @return  the selectiveUpdate
	 */
    public String getSelectiveUpdate()
    {
    	return selectiveUpdate;
    }
    
    /**
	 * @return  the componentName
	 * @uml.property  name="componentName"
	 */
    public String getComponentName()
    {
        return componentName;
    }
    
    protected String specVersion;

    /**
	 * @param specVersion  the specVersion to set
	 * @uml.property  name="specVersion"
	 */
    public void setSpecVersion(String specVersion)
    {
        this.specVersion = specVersion;
    }
    
    /**
	 * @return  the specVersion
	 * @uml.property  name="specVersion"
	 */
    public String getSpecVersion()
    {
        return specVersion;
    }
    
    protected String buildVersion;

    /**
	 * @param buildVersion  the buildVersion to set
	 * @uml.property  name="buildVersion"
	 */
    public void setBuildVersion(String buildVersion)
    {
        this.buildVersion = buildVersion;
    }
    
    /**
	 * @return  the buildVersion
	 * @uml.property  name="buildVersion"
	 */
    public String getBuildVersion()
    {
        return buildVersion;
    }
    
    protected String buildDate;

    /**
	 * @param buildDate  the buildDate to set
	 * @uml.property  name="buildDate"
	 */
    public void setBuildDate(String buildDate)
    {
        this.buildDate = buildDate;
    }

    public void setBuildDate(Calendar buildDate)
    {
        this.buildDate = calendarToString(buildDate);
    }
    
    /**
	 * @return  the buildDate
	 * @uml.property  name="buildDate"
	 */
    public String getBuildDate()
    {
        return buildDate;
    }

    public Calendar getBuildDateAsCalendar()
    {
        return stringToCalendar(buildDate);
    }    
}
