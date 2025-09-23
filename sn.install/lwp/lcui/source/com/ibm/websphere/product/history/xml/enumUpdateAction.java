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
 * Update Action Enum
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 *
 * 25-Oct-2002 Defect 152480: Corrected selective action constants.
 */

/**
 *  
 */
public class enumUpdateAction
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/26/03" ;

    public static final int INSTALL_UPDATE_ACTION_VALUE = 0 ;
    public static final int UNINSTALL_UPDATE_ACTION_VALUE = 1 ;
    public static final int SELECTIVE_INSTALL_UPDATE_ACTION_VALUE = 2 ;
    public static final int SELECTIVE_UNINSTALL_UPDATE_ACTION_VALUE = 3 ;

    // These are used in file names and cannot have spaces.

    public static final String INSTALL_UPDATE_ACTION_TEXT = "install" ;
    // These are used in file names and cannot have spaces.

    public static final String UNINSTALL_UPDATE_ACTION_TEXT = "uninstall" ;
    // These are used in file names and cannot have spaces.

    public static final String CONFIG_UPDATE_ACTION_TEXT = "config" ;
    // These are used in file names and cannot have spaces.

    public static final String UNCONFIG_UPDATE_ACTION_TEXT = "unconfig" ;
    // These are used in file names and cannot have spaces.

    public static final String SELECTIVE_INSTALL_UPDATE_ACTION_TEXT = "selective-install" ;
    // These are used in file names and cannot have spaces.

    public static final String SELECTIVE_UNINSTALL_UPDATE_ACTION_TEXT = "selective-uninstall" ;

    public static final enumUpdateAction INSTALL_UPDATE_ACTION = new enumUpdateAction ( INSTALL_UPDATE_ACTION_VALUE ) ;
    public static final enumUpdateAction UNINSTALL_UPDATE_ACTION = new enumUpdateAction ( UNINSTALL_UPDATE_ACTION_VALUE ) ;
    public static final enumUpdateAction SELECTIVE_INSTALL_UPDATE_ACTION = new enumUpdateAction ( SELECTIVE_INSTALL_UPDATE_ACTION_VALUE ) ;
    public static final enumUpdateAction SELECTIVE_UNINSTALL_UPDATE_ACTION = new enumUpdateAction ( SELECTIVE_UNINSTALL_UPDATE_ACTION_VALUE ) ;

    public static enumUpdateAction selectUpdateAction(String updateActionValue)
    {
        if      ( updateActionValue == null )
            return null;
        else if ( updateActionValue.equalsIgnoreCase(INSTALL_UPDATE_ACTION_TEXT) )
            return INSTALL_UPDATE_ACTION;
        else if ( updateActionValue.equalsIgnoreCase(UNINSTALL_UPDATE_ACTION_TEXT) )
            return UNINSTALL_UPDATE_ACTION;
        else if ( updateActionValue.equalsIgnoreCase(CONFIG_UPDATE_ACTION_TEXT) )
            return INSTALL_UPDATE_ACTION;
        else if ( updateActionValue.equalsIgnoreCase(UNCONFIG_UPDATE_ACTION_TEXT) )
            return UNINSTALL_UPDATE_ACTION;
        else if ( updateActionValue.equalsIgnoreCase(SELECTIVE_INSTALL_UPDATE_ACTION_TEXT) )
            return SELECTIVE_INSTALL_UPDATE_ACTION;
        else if ( updateActionValue.equalsIgnoreCase(SELECTIVE_UNINSTALL_UPDATE_ACTION_TEXT) )
            return SELECTIVE_UNINSTALL_UPDATE_ACTION;
        else
            return null;
    }

    public static enumUpdateAction selectUpdateAction(int updateAction)
    {
        if      ( updateAction == INSTALL_UPDATE_ACTION_VALUE )
            return INSTALL_UPDATE_ACTION;
        else if ( updateAction == UNINSTALL_UPDATE_ACTION_VALUE )
            return UNINSTALL_UPDATE_ACTION;
        else if ( updateAction == SELECTIVE_INSTALL_UPDATE_ACTION_VALUE )
            return SELECTIVE_INSTALL_UPDATE_ACTION;
        else if ( updateAction == SELECTIVE_UNINSTALL_UPDATE_ACTION_VALUE )
            return SELECTIVE_UNINSTALL_UPDATE_ACTION;
        else
            return null;
    }

    protected int updateAction;

    protected enumUpdateAction()
    {
        updateAction = INSTALL_UPDATE_ACTION_VALUE;
    }

    protected enumUpdateAction(int updateAction)
    {
        this.updateAction = updateAction;
    }

    public String toString()
    {
        if ( this == INSTALL_UPDATE_ACTION )
            return INSTALL_UPDATE_ACTION_TEXT;
        else if ( this == UNINSTALL_UPDATE_ACTION )
            return UNINSTALL_UPDATE_ACTION_TEXT;
        else if ( this == SELECTIVE_INSTALL_UPDATE_ACTION )
            return SELECTIVE_INSTALL_UPDATE_ACTION_TEXT;
        else if ( this == SELECTIVE_UNINSTALL_UPDATE_ACTION )
            return SELECTIVE_UNINSTALL_UPDATE_ACTION_TEXT;
        else
            return null;
    }

    public int getValue()
    {
        return updateAction;
    }
}
