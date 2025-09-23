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
package com.ibm.websphere.update.ioservices;

/*
 * Support for comples IO operations.
 *
 * 19-Feb-2003 Initial Version
 */

public class StandardIOServiceFactory implements IOServiceFactory {
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "2/20/03" ;

    public IOService createIOService() {
        if ( IOService.isDebug )
            System.out.println("StandardIOServiceFactory: createIOService ...");

        IOService newIOService = new IOService();

        if ( IOService.isDebug )
            System.out.println("StandardIOServiceFactory: createIOService ... complete");

        return newIOService;
    }
}
