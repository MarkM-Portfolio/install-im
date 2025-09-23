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
 * Custom class to make a copy of an existing file.
 *
 * @arg[0] (String) source file
 * @arg[1] (String) target file
 * @arg[2] (boolean) overwrite?
 * @arg[3] (boolean) failOnError?
 *
 * Sample filter-file entries to use this class:
 * 
 *      EntryScript backupSomeEar
 *      class com.ibm.websphere.update.delta.CopyFileAction,/installableApps/some.ear,/installableApps/some_old.ear,true,true=0
 *      class com.ibm.websphere.update.delta.CopyFileAction,$<target>/installableApps/some_other.ear,$<target>/installableApps/some_other_old.ear,true,true=0
 *
 * History 1.2, 3/29/04
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import com.ibm.websphere.update.util.BooleanUtils;
import com.ibm.websphere.update.util.WPConfig;

public class CopyFileAction extends ExtendedUpdateAction
{
    public static final String pgmVersion = "1.2" ;
    public static final String pgmUpdate = "3/29/04" ;

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
    private File destFile;
    private String srcFileName;
    private String destFileName;
    private boolean overwrite = false;
    private boolean continueOnError = true; 

    protected String getInstallLocation(){ 
        //return WPConfig.getProperty( WPConstants.PROP_WP_HOME );
    	return System.getProperty(com.ibm.lconn.common.LCUtil.LC_HOME);
    }

    public CopyFileAction(){
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
        FileOutputStream destStream = null;

        try {

            assignArgs(args);
            checkSrcFile();
            checkDestFile();

            srcStream  = new FileInputStream( srcFile );
            destStream = new FileOutputStream( destFile );

            byte[] bytes = new byte[1024];

            for ( int i = 0; (i=srcStream.read(bytes)) != -1; ) {
                destStream.write( bytes, 0, i );
            }                                   

            messages.append( "Copied " + srcFileName + " to " + destFileName );

        } catch ( Exception e1 ) {

            errorMessage.append( "Failed to copy " + srcFileName );
            errorMessage.append( System.getProperty( "line.separator" ) );
            errorMessage.append( getStackTraceAsString(e1) );

            if (!continueOnError) {
                retVal = -1;
            }

        } finally {

            try {
                if ( null != srcStream )  { srcStream.close(); }
                if ( null != destStream ) { destStream.close(); }
            } catch ( Exception e2 ) {}

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
        destFileName     = (String) args.get(1);

        // test4Boolean returns false if it does not recognize the string passed to it,
        //     so the conservative choice is !overwrite and !continueOnError
        overwrite        = BooleanUtils.test4Boolean( (String) args.get(2), this.toString() + "::overwrite (boolean)" );
        continueOnError  = BooleanUtils.test4Boolean( (String) args.get(3), this.toString() + "::continueOnError (boolean)" );

    }


    /**
     *
     *  evaluate source file
     *      must exist
     *      must be readable
     *
     */
    private void checkSrcFile() throws FileNotFoundException, IOException {

        if ( -1 != srcFileName.indexOf( "$<" )) { 
            srcFileName = extractor.ResolveMacro( srcFileName );
        } else {
            srcFileName = getInstallLocation() + File.separator + srcFileName;
        }

        debug( this.toString() + ".checkSrcFile::srcFileName == " + srcFileName );
        srcFile  = new File( srcFileName  );

        if (!srcFile.exists())  { throw new FileNotFoundException( "File does not exist: " + srcFileName ); }
        if (!srcFile.canRead()) { throw new IOException( "Cannot read file: " + srcFileName ); }
                          
    }

    /**
     *
     *  evaluate dest file
     *      if exists then check overwrite
     *      if exists and overwrite == true then file must be writeable
     *      if !exists then parent_dir must be writeable
     *
     */
    private void checkDestFile() throws IOException {

        if ( -1 != destFileName.indexOf( "$<" )) { 
            destFileName = extractor.ResolveMacro( destFileName ); 
        } else {
            destFileName = getInstallLocation() + File.separator + destFileName;
        }

        debug( this.toString() + ".checkDestFile::destFileName == " + destFileName );
        destFile  = new File( destFileName  );

        if ( destFile.exists() ) { 
            if (overwrite) {
                if (!destFile.canWrite()) {
                    throw new IOException( "File exists, and file permissions do not allow overwriting: " + destFileName ); 
                }
            } else { // !overwrite
                throw new IOException( "File exists but is marked 'doNotOverwrite' by the service package: " + destFileName ); 
            }
        } else { 
        	// !destFile.exists()
        	File parentDir =new File(destFile.getParent());
        	if(!parentDir.exists())
        		parentDir.mkdirs();
            if ( !(new File(destFile.getParent()).canWrite()) ) {
                throw new IOException( "File system permissions do not permit writing to directory: " + destFile.getParent() ); 
            }
        }
                          
    }

}
