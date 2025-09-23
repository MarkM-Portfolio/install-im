/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */

/*
 * PlatformUtils -- Platform utilities for EAR processing.
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/util/BooleanUtils.java, wps.base.fix, wps6.fix
 *
 * History 1.1, 3/29/04
 *
 * 02-Dec-2002 Initial Version
 */

package com.ibm.websphere.update.util;

import java.util.*;
import java.io.*;

public class BooleanUtils
{

    public final static String pgmVersion = "1.1" ;
    public final static String pgmUpdate = "3/29/04" ;


    public static final String meTrue        = "true";
    public static final String meFalse       = "false";

    public static boolean test4Boolean(String value)
    {
        return test4Boolean(value, "");
    }

    public static boolean test4Boolean(String value, String errorMessage)
    {
        boolean state;

        value = value.trim();
    
        if ( value.equalsIgnoreCase("true") ||
             value.equalsIgnoreCase("yes")  ||
             value.equals("1")              ||
             value.equalsIgnoreCase("on") ) {

            state = true;

        } else if ( value.equalsIgnoreCase("false") ||
                    value.equalsIgnoreCase("no") ||
                    value.equals("0") ||
                    value.equalsIgnoreCase("off") ) {

            state = false;

        } else {
            logErr(1, "The string (" + value + ") cannot be converted to boolean. " + errorMessage);

            state = false;
        }
    
        return state;
    }

    public String bool2String(boolean value)
    {
        if ( value )
            return meTrue;
        else
            return meFalse;
    }

    private static void logErr ( int errCode, String errMsg ) 
    {
        System.err.println( "BooleanUtils: ERROR " + errCode + " -- " + errMsg );
    }

}
