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

package com.ibm.websphere.product.xml.component;

/*
 * Component Bean
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class component extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    public component()
    {
        super();
    }

    // Basic field access ...

    protected String name;

    /**
	 * @return  the name
	 * @uml.property  name="name"
	 */
    public String getName()
    {
        return name;
    }

    /**
	 * @param name  the name to set
	 * @uml.property  name="name"
	 */
    public void setName(String name)
    {
        this.name = name;
    }

    protected String specVersion;

    /**
	 * @return  the specVersion
	 * @uml.property  name="specVersion"
	 */
    public String getSpecVersion()
    {
        return specVersion;
    }

    /**
	 * @param specVersion  the specVersion to set
	 * @uml.property  name="specVersion"
	 */
    public void setSpecVersion(String specVersion)
    {
        this.specVersion = specVersion;
    }

    protected String buildVersion;

    /**
	 * @return  the buildVersion
	 * @uml.property  name="buildVersion"
	 */
    public String getBuildVersion()
    {
        return buildVersion;
    }

    /**
	 * @param buildVersion  the buildVersion to set
	 * @uml.property  name="buildVersion"
	 */
    public void setBuildVersion(String buildVersion)
    {
        this.buildVersion = buildVersion;
    }

    protected String buildDate;

    public Calendar getBuildDateAsCaldendar()
    {
        return ( (buildDate == null) ? null : stringToCalendar(buildDate) );
    }

    /**
	 * @return  the buildDate
	 * @uml.property  name="buildDate"
	 */
    public String getBuildDate()
    {
        return buildDate;
    }

    public void setBuildDate(Calendar buildDate)
    {
        this.buildDate = ( (buildDate == null) ? null : calendarToString(buildDate) );
    }

    /**
	 * @param buildDate  the buildDate to set
	 * @uml.property  name="buildDate"
	 */
    public void setBuildDate(String buildDate)
    {
        this.buildDate = buildDate;
    }
}
