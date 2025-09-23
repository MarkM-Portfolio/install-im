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
package com.ibm.websphere.update.ioservices.standard;

/*
 * StandardLogFile
 *
 * History 1.1, 9/6/02
 *
 * 09-Jul-2002 Initial Version
 */

import com.ibm.websphere.update.ioservices.LogFile;
import java.io.*;

/**
 *  
 */
public class StandardLogFile implements LogFile
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/6/02" ;

    // Instantor ...

    protected String logFileName;
    protected boolean appendFlag;

    protected PrintWriter logWriter;

    public StandardLogFile(String logFileName)
    {
        this(logFileName, true); // Append
    }

    public StandardLogFile(String logFileName, boolean append)
    {
        this.logFileName = logFileName;
        this.appendFlag = append;

        this.logWriter = null;
    }

    // State access ...

    public String getName()
    {
        return logFileName;
    }

    /**
	 * @return  the logFileName
	 * @uml.property  name="logFileName"
	 */
    public String getLogFileName()
    {
        return logFileName;
    }

    /**
	 * @return  the appendFlag
	 * @uml.property  name="appendFlag"
	 */
    public boolean getAppendFlag()
    {
        return appendFlag;
    }

    // Printing ...

    public void println(Object arg)
    {
        if ( logWriter == null )
            return;

        logWriter.write( (arg == null) ? NULL_TEXT : arg.toString() );
        logWriter.write(crlf);
    }

    public void println(Object arg1, Object arg2)
    {
        if ( logWriter == null )
            return;

        println( ((arg1 == null) ? NULL_TEXT : arg1.toString()) +
                 ((arg2 == null) ? NULL_TEXT : arg2.toString()) );
    }

    public void flush()
    {
        if ( logWriter == null )
            return;

        logWriter.flush();
    }

    public void flush(Object arg1)
    {
        println(arg1);
        flush();
    }

    public void flush(Object arg1, Object arg2)
    {
        println(arg1, arg2);
        flush();
    }

    // Open/Close ...

    public void open()
        throws IOException
    {
        if ( logWriter != null )
            return;

        String useFileName = getLogFileName();

        int slashLoc = useFileName.lastIndexOf(File.separator);
        if ( (slashLoc != -1)  && (slashLoc != 0) ) {
            String logDirName = useFileName.substring(0, slashLoc);

            File logDir = new File(logDirName);
            if ( !logDir.exists() ) {
                logDir.mkdirs();
                // Will get back 'false' if the directories could
                // not be created.  In that case, the attempt
                // to open the file writer will fail.
            }
        }
        FileOutputStream fos = new FileOutputStream(useFileName, getAppendFlag());
        OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF8");
//        FileWriter writer = new FileWriter(useFileName, getAppendFlag());
                            // throws IOException

        logWriter = new PrintWriter(writer);
    }

    public void close() throws IOException
    {
        if ( logWriter == null )
            return;

        PrintWriter useWriter = logWriter;

        logWriter = null;

        useWriter.close();
    }
}
