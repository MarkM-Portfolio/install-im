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
 * Event Type Enum
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 */

/**
 *  
 */
public class enumEventType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    public static final int EFIX_VALUE = 0 ;
    public static final int PTF_VALUE = 1 ;
    public static final int COMPONENT_VALUE = 2 ;
    public static final int CONFIG_VALUE = 3 ;

    public static final String EFIX_TEXT = "efix" ;
    public static final String PTF_TEXT = "ptf" ;
    public static final String COMPONENT_TEXT = "component" ;
    public static final String CONFIG_TEXT = "config" ;

    public static final enumEventType EFIX_EVENT_TYPE = new enumEventType ( EFIX_VALUE ) ;
    public static final enumEventType PTF_EVENT_TYPE = new enumEventType ( PTF_VALUE ) ;
    public static final enumEventType COMPONENT_EVENT_TYPE = new enumEventType ( COMPONENT_VALUE ) ;
    public static final enumEventType CONFIG_EVENT_TYPE = new enumEventType ( CONFIG_VALUE ) ;

    public static enumEventType selectEventType(String eventTypeValue)
    {
        if      ( eventTypeValue == null )
            return null;
        else if ( eventTypeValue.equalsIgnoreCase(EFIX_TEXT) )
            return EFIX_EVENT_TYPE;
        else if ( eventTypeValue.equalsIgnoreCase(PTF_TEXT) )
            return PTF_EVENT_TYPE;
        else if ( eventTypeValue.equalsIgnoreCase(COMPONENT_TEXT) )
            return COMPONENT_EVENT_TYPE;
        else if ( eventTypeValue.equalsIgnoreCase(CONFIG_TEXT) )
            return CONFIG_EVENT_TYPE;
        else
            return null;
    }

    public static enumEventType selectEventType(int eventType)
    {
        if      ( eventType == EFIX_VALUE )
            return EFIX_EVENT_TYPE;
        else if ( eventType == PTF_VALUE )
            return PTF_EVENT_TYPE;
        else if ( eventType == COMPONENT_VALUE )
            return COMPONENT_EVENT_TYPE;
        else if ( eventType == CONFIG_VALUE )
            return CONFIG_EVENT_TYPE;
        else
            return null;
    }

    protected int eventType;

    protected enumEventType()
    {
        eventType = EFIX_VALUE;
    }

    protected enumEventType(int eventType)
    {
        this.eventType = eventType;
    }

    public String toString()
    {
        if      ( this == EFIX_EVENT_TYPE )
            return EFIX_TEXT;
        else if ( this == PTF_EVENT_TYPE )
            return PTF_TEXT;
        else if ( this == COMPONENT_EVENT_TYPE )
            return COMPONENT_TEXT;
        else if ( this == CONFIG_EVENT_TYPE )
            return CONFIG_TEXT;
        else
            return null;
    }

    public int getValue()
    {
        return eventType;
    }
}
