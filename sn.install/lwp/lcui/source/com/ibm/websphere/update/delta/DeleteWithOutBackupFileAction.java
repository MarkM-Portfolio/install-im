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
 * Custom class to Delete a file without backing up the file. This will delete the file permanantly from user machine and NOT restored during Uninstall.
 *
 * @arg[0] (String) source file
 * @arg[1] (boolean) failOnError?
 *
 * Sample filter-file entries to use this class:
 * 
 *      EntryScript deleteSomeFile
 *      class com.ibm.websphere.update.delta.DeleteWithOutBackupFileAction,/installableApps/some.ear,true=0
 *      class com.ibm.websphere.update.delta.DeleteWithOutBackupFileAction,$<target>/installableApps/some_other.ear,true=0
 *
 * History 1.0, 06/07/04
 */

import java.io.*;
import java.util.Vector;

import com.ibm.websphere.update.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.util.*;

public class DeleteWithOutBackupFileAction extends ExtendedUpdateAction
{
    public static final String pgmVersion = "1.0" ;
    public static final String pgmUpdate = "06/07/04" ;

    public static final String debugPropertyName = "com.ibm.websphere.update.delta.debug" ;
    public static final String debugTrueValue = "true" ;
    public static final String debugFalseValue = "false" ;

    protected static boolean debug;

    static {
        String debugValue = System.getProperty(debugPropertyName);
        debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
    }

    protected static void debug(String arg)
    {
        if ( !debug )
            return;

        System.out.println(arg);
    }

    private File srcFile;
    private String srcFileName;
    private boolean continueOnError = true;

    protected String getInstallLocation(){ 
        return WPConfig.getProperty( WPConstants.PROP_WP_HOME ); 
    }

    public DeleteWithOutBackupFileAction(){
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
                       boolean debug,
                       Vector args)
    {

        int retVal = 0; 
        FileInputStream  srcStream  = null;  
        
        try {
            assignArgs(args);
            
            if ( -1 != srcFileName.indexOf( "$<" )) { 
            srcFileName = extractor.ResolveMacro( srcFileName );
            } else {
                srcFileName = getInstallLocation() + File.separator + srcFileName;
            }

            debug( this.toString() + ".checkSrcFile::srcFileName == " + srcFileName );
            srcFile  = new File( srcFileName  );

            if (srcFile.exists())  {
                srcFile.deleteOnExit();
                messages.append( "Deleted " + srcFileName );
            } else {
                messages.append( srcFileName + " does not exist no action taken.");
            }

        } catch ( Exception e1 ) {

            errorMessage.append( "Failed to delete " + srcFileName );
            errorMessage.append( System.getProperty( "line.separator" ) );
            errorMessage.append( getStackTraceAsString(e1) );

            if (!continueOnError) {
                retVal = -1;
            }

        } 

        return retVal;
    }

    /**
     *
     *  get input from args[], store in fields with meaningful names
     *
     */
    private void assignArgs( Vector args ) throws ClassCastException, ArrayIndexOutOfBoundsException {

        srcFileName      = (String) args.get(0); 
        // test4Boolean returns false if it does not recognize the string passed to it,
        //     so the conservative choice is !overwrite and !continueOnError
        continueOnError  = BooleanUtils.test4Boolean( (String) args.get(1), this.toString() + "::continueOnError (boolean)" );

    }

}
