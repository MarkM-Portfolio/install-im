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
 * Update Type Enum
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 */

/**
 *  
 */
public class enumUpdateType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    public static final int ADD_VALUE = 0 ;
    public static final int REPLACE_VALUE = 1 ;
    public static final int REMOVE_VALUE = 2 ;
    public static final int PATCH_VALUE = 3 ;
    public static final int COMPOSITE_VALUE = 4 ;

    public static final String ADD_TEXT = "add" ;
    public static final String REPLACE_TEXT = "replace" ;
    public static final String REMOVE_TEXT = "remove" ;
    public static final String PATCH_TEXT = "patch" ;
    public static final String COMPOSITE_TEXT = "composite" ;

    public static final enumUpdateType ADD_UPDATE_TYPE = new enumUpdateType ( ADD_VALUE ) ;
    public static final enumUpdateType REPLACE_UPDATE_TYPE = new enumUpdateType ( REPLACE_VALUE ) ;
    public static final enumUpdateType REMOVE_UPDATE_TYPE = new enumUpdateType ( REMOVE_VALUE ) ;
    public static final enumUpdateType PATCH_UPDATE_TYPE = new enumUpdateType ( PATCH_VALUE ) ;
    public static final enumUpdateType COMPOSITE_UPDATE_TYPE = new enumUpdateType ( COMPOSITE_VALUE ) ;

    public static enumUpdateType selectUpdateType(String updateTypeText)
    {
        if      ( updateTypeText == null )
            return null;
        else if ( updateTypeText.equalsIgnoreCase(ADD_TEXT) )
            return ADD_UPDATE_TYPE;
        else if ( updateTypeText.equalsIgnoreCase(REPLACE_TEXT) )
            return REPLACE_UPDATE_TYPE;
        else if ( updateTypeText.equalsIgnoreCase(REMOVE_TEXT) )
            return REMOVE_UPDATE_TYPE;
        else if ( updateTypeText.equalsIgnoreCase(PATCH_TEXT) )
            return PATCH_UPDATE_TYPE;
        else if ( updateTypeText.equalsIgnoreCase(COMPOSITE_TEXT) )
            return COMPOSITE_UPDATE_TYPE;
        else
            return null;
    }

    public static enumUpdateType selectUpdateType(int updateTypeValue)
    {
        if      ( updateTypeValue == ADD_VALUE )
            return ADD_UPDATE_TYPE;
        else if ( updateTypeValue == REPLACE_VALUE )
            return REPLACE_UPDATE_TYPE;
        else if ( updateTypeValue == REMOVE_VALUE )
            return REMOVE_UPDATE_TYPE;
        else if ( updateTypeValue == PATCH_VALUE )
            return PATCH_UPDATE_TYPE;
        else if ( updateTypeValue == COMPOSITE_VALUE )
            return COMPOSITE_UPDATE_TYPE;
        else
            return null;
    }

    protected int updateTypeValue;

    protected enumUpdateType()
    {
        updateTypeValue = ADD_VALUE;
    }

    protected enumUpdateType(int updateTypeValue)
    {
        this.updateTypeValue = updateTypeValue;
    }

    public String toString()
    {
        if ( this == ADD_UPDATE_TYPE )
            return ADD_TEXT;
        else if ( this == REPLACE_UPDATE_TYPE )
            return REPLACE_TEXT;
        else if ( this == REMOVE_UPDATE_TYPE )
            return REMOVE_TEXT;
        else if ( this == PATCH_UPDATE_TYPE )
            return PATCH_TEXT;
        else if ( this == COMPOSITE_UPDATE_TYPE )
            return COMPOSITE_TEXT;
        else
            return null;
    }

    public int getValue()
    {
        return updateTypeValue;
    }
}
