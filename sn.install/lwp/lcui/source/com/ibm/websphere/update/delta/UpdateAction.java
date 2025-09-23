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
package com.ibm.websphere.update.delta;

/*
 * Script custom class supertype.
 *
 * History 1.2, 9/26/03
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Vector;

import com.ibm.websphere.update.*;

public class UpdateAction
{
   public static final String pgmVersion = "1.2" ;
   public static final String pgmUpdate = "9/26/03" ;

    public UpdateAction()
    {
	super();
    }

    // Answer the vector of files which are to be backed up.
    // This is provided to allow the process step to modify
    // files which are in addition to those which are modified
    // by the normal extractor application.
    //
    // The arguments to 'file2Backup' are the same as to the
    // 'process' method, so to allow the same level of detail
    // in detecting the files to backup as during processing.
    //
    // 'file2Backup' is sent to a different instance than 'process'.
    //
    // The arguments are:
    //
    //    'root'           the target directory for the update
    //    'components'     obsolete
    //    'po'             the options process from the extractor
    //    'messages'       a buffer to messages to be logged
    //    'errorMessage'   a buffer for error emssages
    //    'debug'          debug enablement flag from the extractor
    //
    // The result vector is a list of files which are to be backed up
    // before performing processing.

    public Vector file2Backup(String root,
                              String[] components,
                              POProcessor po,
                              StringBuffer messages,
                              StringBuffer errorMessage,
                              boolean debug)
    {
        return null;
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
        return 0;
    }

    // Helper for use during file2Backup and process.

    protected String getStackTraceAsString(Throwable t)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        t.printStackTrace(printWriter);

        return stringWriter.getBuffer().toString();
    }
}
