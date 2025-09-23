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
 * LogFile
 *
 * History 1.1, 9/6/02
 *
 * 09-Jul-2002 Initial Version
 */

import java.io.*;

public interface LogFile
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/6/02" ;

    // State access ...

    public String getName();
    public String getLogFileName();

    public boolean getAppendFlag();

    // Printing ...

    public static final String crlf = System.getProperty("line.separator");
    public static final String NULL_TEXT = "Null";

    public void println(Object arg);
    public void println(Object arg1, Object arg2);

    public void flush();
    public void flush(Object arg);
    public void flush(Object arg1, Object arg2);

    // Open/Close ...

    public void open() throws IOException;
    public void close() throws IOException;
}
