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
package com.ibm.websphere.update.delta.earutils;

/*
 * Script custom class supertype.
 *
 * History 1.1, 11/18/03
 */

import java.io.*;
import java.util.Vector;

import com.ibm.websphere.update.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.util.*;

public class WmmEarUpdateAction extends UpdateAction
{
   public static final String pgmVersion = "1.1" ;
   public static final String pgmUpdate = "11/18/03" ;

   protected String getEarName()                { return "wmm.ear"; }
   protected String getDestEarName()            { return "wmm_500.ear"; }
   protected String getEarAppName()             { return "wmmApp"; }
   protected String getEarInstalledLocation()   { return WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + "installableApps"; }

    public WmmEarUpdateAction()
    {
	super();
    }

    // Actual processing; the arguments are the same as for 'file2Backup'.
    //
    // The result integer is a result code which may be used to indicate
    // the success or failure of processing.  When this callable class
    // is used in a virtual script a result code may be provided, in which
    // case the value returned from this method must match that value
    // else the processing will be considered to be in error.

    // -1 is a semi-standard result indicating an error.
    // 0 is a semi-standard result indicating success.

    public int process(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errorMessage,
                       boolean debug)
    {

        try {
            File srcEar  = new File( getEarInstalledLocation() + File.separator + getEarName() );
            File destEar = new File( getEarInstalledLocation() + File.separator + getDestEarName() );

            FileInputStream  srcStream  = new FileInputStream( srcEar );
            FileOutputStream destStream = new FileOutputStream( destEar );

            byte[] bytes = new byte[1024];

            for ( int i = 0; (i=srcStream.read(bytes)) != -1; ) {
                destStream.write( bytes, 0, i );
            }                                   

            srcStream.close();
            destStream.close();

        } catch ( Throwable t ) {
            errorMessage.append( "Failed to back up " + getEarName() );
            errorMessage.append( getStackTraceAsString(t) );
            return -1;
        }

        messages.append( "Backed up " + getEarInstalledLocation() + File.separator + getEarName() +
                         " as " + getEarInstalledLocation() + File.separator + getDestEarName() );
        return 0;
    }

    // Helper for use during file2Backup and process.
    /*
    protected String getStackTraceAsString(Throwable t)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        t.printStackTrace(printWriter);

        return stringWriter.getBuffer().toString();
    }
    */
}
