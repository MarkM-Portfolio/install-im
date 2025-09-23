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
 * Event Result Enum
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 */

/**
 *  
 */
public class enumEventResult
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    public static final int SUCCEEDED_VALUE = 0 ;
    public static final int FAILED_VALUE = 1 ;
    public static final int CANCELLED_VALUE = 2 ;

    public static final String SUCCEEDED_TEXT = "succeeded" ;
    public static final String FAILED_TEXT = "failed" ;
    public static final String CANCELLED_TEXT = "cancelled" ;

    public static final enumEventResult SUCCEEDED_EVENT_RESULT = new enumEventResult ( SUCCEEDED_VALUE ) ;
    public static final enumEventResult FAILED_EVENT_RESULT = new enumEventResult ( FAILED_VALUE ) ;
    public static final enumEventResult CANCELLED_EVENT_RESULT = new enumEventResult ( CANCELLED_VALUE ) ;

    public static enumEventResult selectEventResult(String eventResultValue)
    {
        if ( eventResultValue == null )
            return null;
        else if ( eventResultValue.equalsIgnoreCase(SUCCEEDED_TEXT) )
            return SUCCEEDED_EVENT_RESULT;
        else if ( eventResultValue.equalsIgnoreCase(FAILED_TEXT) )
            return FAILED_EVENT_RESULT;
        else if ( eventResultValue.equalsIgnoreCase(CANCELLED_TEXT) )
            return CANCELLED_EVENT_RESULT;
        else
            return null;
    }

    public static enumEventResult selectEventResult(int eventResult)
    {
        if ( eventResult == SUCCEEDED_VALUE )
            return SUCCEEDED_EVENT_RESULT;
        else if ( eventResult == FAILED_VALUE )
            return FAILED_EVENT_RESULT;
        else if ( eventResult == CANCELLED_VALUE )
            return CANCELLED_EVENT_RESULT;
        else
            return null;
    }

    protected int eventResult;

    protected enumEventResult()
    {
        eventResult = SUCCEEDED_VALUE;
    }

    protected enumEventResult(int eventResult)
    {
        this.eventResult = eventResult;
    }

    public String toString()
    {
        if ( this == SUCCEEDED_EVENT_RESULT )
            return SUCCEEDED_TEXT;
        else if ( this == FAILED_EVENT_RESULT )
            return FAILED_TEXT;
        else if ( this == CANCELLED_EVENT_RESULT )
            return CANCELLED_TEXT;
        else
            return null;
    }

    public int getValue()
    {
        return eventResult;
    }
}
