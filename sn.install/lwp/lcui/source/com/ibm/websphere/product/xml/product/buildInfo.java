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

package com.ibm.websphere.product.xml.product;

/*
 * Build Information Bean
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
public class buildInfo extends BaseType
{
    // Program versioning ...
    
    public static final String pgmVersion = "1.2" ;
    // Program versioning ...
    
    public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public buildInfo()
    {
        super();
    }
    
    // Basic field access ...

    // See the field tags, above ...

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
    
    public Calendar getDateAsCalendar()
    {
        return simpleStringToCalendar(date);
    }

    /**
	 * @return  the date
	 * @uml.property  name="date"
	 */
    public String getDate()
    {
        return date;
    }
    
    protected String level;

    /**
	 * @param level  the level to set
	 * @uml.property  name="level"
	 */
    public void setLevel(String level)
    {
        this.level = level;
    }
    
    /**
	 * @return  the level
	 * @uml.property  name="level"
	 */
    public String getLevel()
    {
        return level;
    }
}
