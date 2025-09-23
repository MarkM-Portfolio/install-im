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

// Usage:
//
//     logger = new Logger("myLogFile", false, Logger.v5);
//              // Log to 'myLogFile, don't append, set the verbosity level to v5,
//              // which is high.
//
//     // Open the named log file.
//     logger.Open();
//
//     // Log to the log file; raw means don't display a timestamp:
//     logger.Log("text");
//     logger.LogRaw("text");
//
//     // Log to the standard output:
//     logger.Screen("text");
//     logger.ScreenRaw("text");
//
//     // Log to both the log file and to standard output:
//     logger.Both("text");
//     logger.BothRaw("text");
//
//     // Log the text "Error " + errorNum + " -- " + text
//     // When a throwable is also provided, add " : " + throwable.getMessage()
//
//     logger.Err(int, String, Throwable);
//     logger.Err(int, String);
//
//     // Close the named log file.
//     logger.Close();
//

// The methods with the DataOutputStream are for jCom proxy support.

import com.ibm.websphere.update.ioservices.CalendarUtil;
import java.io.*;
import java.text.*;
import java.util.*;

/*
 * History 1.2, 9/26/03
 *
 * 03-Nove-2002 Consolidated code; reformatted;
 *              Added quiet mode.
 */

/**
 * The logger class provides basic logging and display capablities. Which include logging to a file and optionally displaying to the screen. The default is to prepended the current timestamp to each line. The primary purpose of this class is to provide a easy logging ability and to provide conformity in messaging and error presentation. The methods that end in "Raw" omit the date time stamp prefixed to all other messages.
 */

public class Logger
{
    /** The version and last update of this program. */

   public static final String pgmVersion = "1.2" ;
    /** The version and last update of this program. */

   public static final String pgmUpdate = "9/26/03" ;

    /** The system defined line separator. */

    public static final String crlf = System.getProperty("line.separator");

    /** The levels of verbosity used.  Higher means more output. */

    public static final int v0 = 0 ;
    /** The levels of verbosity used.  Higher means more output. */

    public static final int v1 = 1 ;
    /** The levels of verbosity used.  Higher means more output. */

    public static final int v2 = 2 ;
    /** The levels of verbosity used.  Higher means more output. */

    public static final int v3 = 3 ;
    /** The levels of verbosity used.  Higher means more output. */

    public static final int v4 = 4 ;
    /** The levels of verbosity used.  Higher means more output. */

    public static final int v5 = 5 ;

    protected static final int VERBOSITY_MAX = 9;

    /** Codes used for error handling. */

    public static final int ABEND_EXIT_CODE = 8;

    public static final boolean DO_EXIT = true ;
    public static final boolean DONT_EXIT = false ;

    public static final boolean BE_QUIET = true ;
    public static final boolean DONT_BE_QUIET = false ;

    /** A count of errors issued. */

    // TBD: Make this non-static.

    public static int errorCount = 0;

    protected int         verbosity;

    protected boolean     quiet;

    protected String      logFileName;
    protected boolean     appendFlag;

    protected PrintWriter logWriter;
    protected PrintStream systemWriter;

    /**
     * Basic constructors.
     *
     * The log file name may be null, in which case no file
     * logging can be performed.
     *
     * The append flag tells if the log file is to be opened in
     * append mode, or truncated.
     *
     * A verbosity setting, if set, acts as a filter to the
     * logging messages that are provided with a verbosity
     * level.  The verbosity set in the constructor tells
     * the maximum verbosity of messages that are logged.
     *
     * By default, the verbosity level is set to a maximum
     * value.
     */

    public Logger()
    {
        this(null, true, VERBOSITY_MAX);
    }

    public Logger(String logFileName, boolean appendFlag)
    {
        this(logFileName, appendFlag, VERBOSITY_MAX);
    }

    public Logger(String logFileName, boolean appendFlag, int verbosity)
    {
        this(logFileName, appendFlag, verbosity, BE_QUIET);
    }

    public Logger(String logFileName, boolean appendFlag, int verbosity, boolean quiet)
    {
        this.verbosity    = verbosity;

        this.logFileName  = logFileName;
        this.appendFlag   = appendFlag;
        this.logWriter    = null;   // Set by the open method.

        this.quiet        = quiet;

        this.systemWriter = System.out;
    }

    /**
	 * @return  the quiet
	 * @uml.property  name="quiet"
	 */
    public boolean isQuiet()
    {
        return quiet;
    }

    /**
	 * @param quiet  the quiet to set
	 * @uml.property  name="quiet"
	 */
    public void setQuiet(boolean quiet)
    {
        this.quiet = quiet;
    }

    /**
     * Open/close protocol.  When a log file name is specified, before
     * logging may be performed to the the log file, the log file must
     * be opened.
     * 
     * At the completion of processing, if a log file was opened, the
     * log file should be closed.
     *
     * Answer true or false, telling if the log file is already open,
     * or was successfully opened.
     */

    public boolean Open()
    {
        if ( logFileName == null )
            return false;

        if ( logWriter != null ) {
            Screen("Warning -- Double open of " + logFileName);
            return true;
        }

        try {
            logWriter = new PrintWriter( new FileWriter(logFileName, appendFlag) );

        } catch ( IOException ex ) {
            Screen("Error -- Unable to open " + logFileName + " : " + ex.getMessage());
            return false;
        }

        Log("Opened log file: " + logFileName);

        return true;
    }

    public void Close()
    {
        if ( logWriter != null ) {
            Log("Closing log file: " + logFileName);

            logWriter.close();
            logWriter = null;
        }
    }

    /*
     * Flush the log file (if open).  The log writer is not flushed
     * on every log statement, so use this method if immediately
     * output is necessary.
     */
     
    public void flush()
    {
        if ( logWriter != null )
            logWriter.flush();
    }

    /**
     * Log an error message.  The message is the pattern:
     *   "Error " + errNum + " -- " + shortDesc.
     *
     * Increment the errorCounter.
     */

    public void Err(int errNum, String shortDesc)
    {
        Err(errNum, shortDesc, null, DONT_EXIT);
    }

    public void Err(int errNum, String shortDesc, boolean toAbend)
    {
        Err(errNum, shortDesc, null, toAbend);
    }

    public void Err(int errNum, String shortDesc, Throwable th)
    {
        Err(errNum, shortDesc, th, DONT_EXIT);
    }

    /**
     * Log an Error message, with text from the argument resource
     * bundle, retrived using the argument key.  After retrieving the
     * message, handle as in other cases.
     *
     * As a special case, a block of messages may be retrieved.
     *
     * Several flavors are available, each handling a different
     * number of arguments.
     */

    public void Err(int errNum, ResourceBundle bundle, String key)
    {
        if ( key.endsWith("LINE_1") ) {
            Vector block = getBlock(bundle, key);
            for ( int lineNo = 0; lineNo < block.size(); lineNo++)
                Err(errNum, (String) block.elementAt(lineNo), null, DONT_EXIT);

        } else {
            Err(errNum, bundle.getString(key), null, DONT_EXIT);
        }
    }

    public void Err(int errNum, ResourceBundle bundle, String key, String arg)
    {
        String[] args = new String[] { arg };

        Err(errNum, bundle, key, args);
    }

    public void Err(int errNum,
                    ResourceBundle bundle,
                    String key, String arg1, String arg2)
    {
        String[] args = new String[] { arg1, arg2 };

        Err(errNum, bundle, key, args);
    }

    public void Err(int errNum,
                    ResourceBundle bundle,
                    String key, String arg1, String arg2, String arg3)
    {
        String[] args = new String[] { arg1, arg2, arg3 };

        Err(errNum, bundle, key, args);
    }

    private Object[] toObjectAry(String[] ary){
	    Object[] obj = new Object[ary.length];
	    for(int i=0; i<obj.length; i++){
		    obj[i] = ary[i];
	    }
	    return obj;
    }

    public void Err(int errNum, ResourceBundle bundle, String key, String[] args)
    {
        if ( key.endsWith("LINE_1") ) {
            Vector block = getBlock(bundle, key);
            for ( int lineNo = 0; lineNo < block.size(); lineNo++ ) {
                String nextLine = (String) block.elementAt(lineNo);
                nextLine = MessageFormat.format(nextLine, toObjectAry(args));

                Err(errNum, nextLine, null, DONT_EXIT);
            }

        } else {
            String nextLine = bundle.getString(key);
            nextLine = MessageFormat.format(nextLine, toObjectAry(args));

            Err(errNum, nextLine, null, DONT_EXIT);
        }
    }

    public void Err(int errNum, String shortDesc, Throwable th, boolean toAbend)
    {
        errorCount++;

        StringBuffer messageBuffer = new StringBuffer();

        messageBuffer.append("Error ");
        messageBuffer.append(Integer.toString(errNum));
        messageBuffer.append(" -- ");
        messageBuffer.append(shortDesc);

        if ( th != null ) {
            messageBuffer.append(" : ");
            messageBuffer.append(th.getMessage());
        }

        Both( messageBuffer.toString() );

        if ( th != null )
            Both( getStackTraceAsString(th) );

        if ( toAbend ) {
            Close();
            System.exit(ABEND_EXIT_CODE);
        }
    }

    public String getStackTraceAsString(Throwable th)
    {
        StringWriter resultWriter = new StringWriter();

        PrintWriter resultPrintWriter = new PrintWriter(resultWriter);

        th.printStackTrace(resultPrintWriter);

        return resultWriter.getBuffer().toString();
    }

    /**
     * Combined Logging methods;
     *
     * Write to the log file and to standard output.
     *
     * Don't write to the log file if it isn't open.
     *
     * Always log unless a verbosity argument is specified.
     *
     * Display a time stamp first, unless using one of the
     * raw logging methods.
     */

    public void Both(int prolix, String text)
    {
        if ( prolix > verbosity )
            return;

        Both(text);
    }

    public void Both(ResourceBundle bundle, String key)
    {
        if ( quiet && (logWriter == null) )
            return;

        Both( bundle.getString(key) );
    }

    public void Both(ResourceBundle bundle, String key, String arg)
    {
        Both(bundle, key, new String[] { arg });
    }

    public void Both(ResourceBundle bundle, String key, String arg1, String arg2)
    {
        Both(bundle, key, new String[] { arg1, arg2 });
    }

    public void Both(ResourceBundle bundle, String key, String arg1, String arg2, String arg3)
    {
        Both(bundle, key, new String[] { arg1, arg2, arg3 });
    }

    public void Both(ResourceBundle bundle, String key, Object[] args)
    {
        if ( quiet && (logWriter == null) )
            return;

        String baseMessage = bundle.getString(key);
        String fullMessage = MessageFormat.format(baseMessage, args);

        Both(fullMessage);
    }

    public void Both(String text)
    {
        if ( quiet && (logWriter == null) )
            return;

        BothRaw(CurrentTimeStamp() + " " + text);
    }

    public void BothRaw(ResourceBundle bundle, String key)
    {
        if ( quiet && (logWriter == null) )
            return;

        if ( key.endsWith("LINE_1") ) {
            Vector block = getBlock(bundle, key);

            for ( int lineNo = 0; lineNo < block.size(); lineNo++ )
                BothRaw( (String) block.elementAt(lineNo) );

        } else {
            BothRaw( bundle.getString(key) );
        }
    }

    public void BothRaw(ResourceBundle bundle, String key, Object[] args)
    {
        if ( quiet && (logWriter == null) )
            return;

        String baseMessage = bundle.getString(key);
        String fullMessage = MessageFormat.format(baseMessage, args);

        BothRaw(fullMessage);
    }

    public void BothRaw(String text)
    {
        ScreenRaw(text);
        LogRaw(text);
    }

    /**
     * Log File Logging methods;
     *
     * Write to the log file and not to standard output.
     *
     * Don't write at all if the log file isn't open.
     *
     * Always log unless a verbosity argument is specified.
     *
     * Display a time stamp first, unless using one of the
     * raw logging methods.
     */

    public void Log(int prolix, String text)
    {
        if ( prolix <= verbosity )
            Log(text);
    }

    public void Log(ResourceBundle bundle, String key)
    {
        if ( logWriter == null )
            return;

        Log( bundle.getString(key) );
    }

    public void Log(ResourceBundle bundle, String key, String arg)
    {
        if ( logWriter == null )
            return;

        Log(bundle, key, new String[] { arg });
    }

    public void Log(ResourceBundle bundle, String key, String arg1, String arg2)
    {
        if ( logWriter == null )
            return;

        Log(bundle, key, new String[] { arg1, arg2 });
    }

    public void Log(ResourceBundle bundle, String key, String arg1, String arg2, String arg3)
    {
        if ( logWriter == null )
            return;

        Log(bundle, key, new String[] { arg1, arg2, arg3 });
    }

    public void Log(ResourceBundle bundle, String key, Object[] args)
    {
        if ( logWriter == null )
            return;

        String baseMessage = bundle.getString(key);
        String fullMessage = MessageFormat.format(baseMessage, args);

        Log(fullMessage);
    }

    public void Log(String text)
    {
        if ( logWriter == null )
            return;

        LogRaw(CurrentTimeStamp() + " " + text);
    }

    public void LogRaw(String text)
    {
        if ( logWriter != null )
            logWriter.write(text + crlf);
    }

    /**
     * Screen Logging Methods;
     *
     * Write to standard output but not to the log file.
     *
     * Always log unless a verbosity argument is specified.
     *
     * Display a time stamp first, unless using one of the
     * raw logging methods.
     */

    public void Screen(int prolix, String text)
    {
        if ( prolix <= verbosity )
            Screen(text);
    }

    public void Screen(String text)
    {
        if ( !quiet )
            ScreenRaw(CurrentTimeStamp() + " " + text);
    }

    public void ScreenRaw(String text)
    {
        if ( !quiet )
            systemWriter.println(text);
    }


    /** 
     * Retrieve the current time formatted using
     * the XML format.
     */

    public String CurrentTimeStamp()
    {
        return CalendarUtil.getTimeStampAsString();
    }

    /**
     * Get a block of  resource lines.
     *
     * The key must end with "LINE_1".
     */

    public Vector getBlock(ResourceBundle bundle, String key)
    {
        Vector block = new Vector();

        String strippedKey = key.substring(0, key.length() - 1);

        int lineNum = 1;
        String line = "";

        do {
            String nextKey = strippedKey + lineNum++;

            try {
                line = bundle.getString(nextKey);

            } catch ( MissingResourceException ex ) {
                if ( lineNum < 3 ) // this is a real error
                    Err(998, "Failed to load [" + nextKey + "]", ex, DONT_EXIT);

                line = null;
            }

            if ( line != null )
                block.add(line);

        } while ( line != null );

        return block;
    }

    /**
     * Extended logging methods ... use these
     * to perform the base logging function, then
     * log to the added output stream.
     */

    // These methods tie into the raw versions of the
    // base logging methods, as the time stamp must
    // be resolved just once.

    public void Both(String text, DataOutputStream output)
    {
        text = CurrentTimeStamp() + " " + text;

        BothRaw(text);

        SomeWhere(text, output);
    }

    public void Log(String text, DataOutputStream output)
    {
        text = CurrentTimeStamp() + " " + text;

        LogRaw(text);

        SomeWhere(text, output);
    }

    public void Screen(String text, DataOutputStream output)
    {
        text = CurrentTimeStamp() + " " + text;

        ScreenRaw(text);

        SomeWhere(text, output);
    }

    public void SomeWhere(String text, DataOutputStream output)
    {
        if ( output == null )
            return;

        try {
            output.writeUTF(text);

        } catch ( IOException e ) {
            Both("Error 03 in Logger: " + e.getMessage());
        }
    }
}
