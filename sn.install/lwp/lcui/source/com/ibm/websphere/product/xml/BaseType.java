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

package com.ibm.websphere.product.xml;

/*
 * Simple Type for wrapping XML data.
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import java.util.*;

public class BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    public BaseType()
    {
        super();
    }

    // Boolean formatting ...

    public static final String trueText = "true" ;
    // Boolean formatting ...

    public static final String falseText = "false" ;
    // Boolean formatting ...

    public static final String zeroText = "0" ;
    // Boolean formatting ...

    public static final String oneText = "1" ;

    public String booleanToString(boolean value)
    {
        return ( value ? trueText : falseText );
    }

    public boolean stringToBoolean(String text)
    {
        return ( (text != null) &&
                 (text.equalsIgnoreCase(trueText) ||
                  text.equalsIgnoreCase(oneText)) );
    }

    // Date/Time formatting ...

    public String calendarToString(Calendar timeStamp)
    {
        if ( timeStamp == null )
            return null;
        else
            return CalendarUtil.formatXMLTimeStamp(timeStamp);
    }

    public Calendar stringToCalendar(String xmlFormatTimeStamp)
    {
        if ( (xmlFormatTimeStamp == null) || xmlFormatTimeStamp.equals("") )
            return null;
        else
            return CalendarUtil.recoverCalendar(xmlFormatTimeStamp);
    }

    public String calendarToSimpleString(Calendar timeStamp)
    {
        if ( timeStamp == null )
            return null;
        else
            return CalendarUtil.formatSimple(timeStamp);
    }

    public Calendar simpleStringToCalendar(String simpleFormatTimeStamp)
    {
        if ( (simpleFormatTimeStamp == null) || simpleFormatTimeStamp.equals("") )
            return null;
        else
            return CalendarUtil.recoverSimple(simpleFormatTimeStamp);
    }

    public boolean setAttributeValue(String attributeName, String attributeValue)
    {
        return false;
    }
}
