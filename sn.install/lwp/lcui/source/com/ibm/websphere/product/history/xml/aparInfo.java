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
 * APAR Info Bean
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
public class aparInfo extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public aparInfo()
    {
        super();

        this.customProperties = new ArrayList();
    }

    protected String number;

    /**
	 * @param number  the number to set
	 * @uml.property  name="number"
	 */
    public void setNumber(String number)
    {
        this.number = number;
    }
    
    /**
	 * @return  the number
	 * @uml.property  name="number"
	 */
    public String getNumber()
    {
        return number;
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

    protected String longDescription;

    /**
	 * @param longDescription  the longDescription to set
	 * @uml.property  name="longDescription"
	 */
    public void setLongDescription(String longDescription)
    {
        this.longDescription = longDescription;
    }
    
    /**
	 * @return  the longDescription
	 * @uml.property  name="longDescription"
	 */
    public String getLongDescription()
    {
        return longDescription;
    }

    protected String date;

    public void setDate(Calendar date)
    {
        this.date = calendarToSimpleString(date);
    }

    /**
	 * @param date  the date to set
	 * @uml.property  name="date"
	 */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
	 * @return  the date
	 * @uml.property  name="date"
	 */
    public String getDate()
    {
        return date;
    }

    public Calendar getDateAsCalendar()
    {
        return simpleStringToCalendar(date);
    }

    // Custom property access ...

    protected ArrayList customProperties;

    public void addCustomProperty(customProperty customProperty)
    {
        customProperties.add(customProperty);
    }

    public customProperty getCustomProperty(int index)
    {
        return (customProperty) customProperties.get(index);
    }

    public int getCustomPropertyCount()
    {
        return customProperties.size();
    }

    public void removeCustomProperty(int index)
    {
        customProperties.remove(index);
    }
}
