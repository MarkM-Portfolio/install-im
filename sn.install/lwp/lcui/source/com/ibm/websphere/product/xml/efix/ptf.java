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

package com.ibm.websphere.product.xml.efix;

/*
 * PTF Bean
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 *             Added platform-prereq and version-prereq lists.
 *             Extended ptf information.
 *
 * 12-Sep-2002 Split off detail into history.
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class ptf extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public ptf()
    {
        super();

        this.componentNames = new ArrayList();
    }

    // Component access ...

    protected ArrayList componentNames;

    public void addComponentName(String componentName)
    {
        componentNames.add(componentName);
    }
    
    public String getComponentName(int index)
    {
        return (String) componentNames.get(index);
    }
    
    public int getComponentNameCount()
    {
        return componentNames.size();
    }

    public void removeComponentName(int index)
    {
        componentNames.remove(index);
    }

    protected String id;

    /**
	 * @param id  the id to set
	 * @uml.property  name="id"
	 */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
	 * @return  the id
	 * @uml.property  name="id"
	 */
    public String getId()
    {
        return id;
    }

    protected String shortDescription;

    /**
	 * @param shortDescription  the shortDescription to set
	 * @uml.property  name="shortDescription"
	 */
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    /**
	 * @return  the shortDescription
	 * @uml.property  name="shortDescription"
	 */
    public String getShortDescription()
    {
        return shortDescription;
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

    public void setBuildDate(Calendar buildDate)
    {
        this.buildDate = calendarToString(buildDate);
    }

    /**
	 * @param buildDate  the buildDate to set
	 * @uml.property  name="buildDate"
	 */
    public void setBuildDate(String buildDate)
    {
        this.buildDate = buildDate;
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

    /**
     * Answer the standard file name for storing the receiver.
     *
     * @return String The name for storage.
     */

    public String getStandardFileName()
    {
        return EFixHandler.getStandardPTFFileName( getId() );
    }
}
