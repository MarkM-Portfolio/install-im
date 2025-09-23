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

/**
 * ExecCmd -- Execute a Command and gather the output.
 * 
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/ExecCmd.java, wps.base.fix, wps6.fix
 *
 * @version 1.7
 * Date: 6/6/07
 *
 * String : The command to execute.
 *          If a DOS internal command such as "dir d:\*",
 *          enter "CMD.EXE /c dir d:\*".
 * boolean: If to print standard output.
 * boolean: If to print standard error.
 * Vector : A vector to save the data in, or null.
 *
 * This class will execute a command and optionally return the output.
 * It starts two threads, one for STDOUT and one for STDERR and
 * monitors the threads.  Reading the output queues is blocking, hence
 * the two threads.  When the two output streams are closed this class
 * will terminate.  Optionally, the output is placed into a user
 * supplied vector, which may be accessed before the task has
 * finished.
 *
 * Any lines collected from standard error will have the literal "StdErr: "
 * prepended.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Vector;

import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.PuiProfileToken;

public class ExecCmd extends Thread
{
   public static final String pgmVersion = "1.7" ;
   public static final String pgmUpdate = "6/6/07" ;

    // ExecCmd is schizo:
    //
    // One constructor is used to launch the exec as a whole;
    // The other is used within ExecCmd to capture output.

    // Top level state:

    public static final boolean DO_ADJUST_FOR_PLATFORM = true ;
    // ExecCmd is schizo:
    //
    // One constructor is used to launch the exec as a whole;
    // The other is used within ExecCmd to capture output.

    // Top level state:

    public static final boolean DONT_ADJUST_FOR_PLATFORM = false ;
    // ExecCmd is schizo:
    //
    // One constructor is used to launch the exec as a whole;
    // The other is used within ExecCmd to capture output.

    // Top level state:

    public static final boolean DO_ECHO_LOG = true ;
    // ExecCmd is schizo:
    //
    // One constructor is used to launch the exec as a whole;
    // The other is used within ExecCmd to capture output.

    // Top level state:

    public static final boolean DONT_ECHO_LOG = false ;

    protected boolean adjustForPlatform; // If to add "cmd.exe" to x86 platforms.

    protected Vector captureBuffer;      // a Vector to which the lines are added to
    protected boolean echoLog;           // If to echo log statements to standard output.
    
    private final static String WAS_PASS = "WasPassword=";


    /**
     * Simple constructor.  User must adjust command syntax for platform
     * idiosyncratic differences.
     */

    public ExecCmd()
    {
        this(DONT_ADJUST_FOR_PLATFORM, DO_ECHO_LOG);
    }

    /**
     * Normal constructor.  If requested, this constructor will adjust
     * for platform idiosyncratic differences.
     */

    public ExecCmd(boolean adjustForPlatform)
    {
        this(adjustForPlatform, DO_ECHO_LOG);
    }

    /**
     * If requested this constructor will adjust for platform
     * idiosyncratic differences.  You have the ability to display or
     * not display a launched message.
     */

    public ExecCmd(boolean adjustForPlatform, boolean echoLog)
    {
        this.adjustForPlatform = adjustForPlatform;

        this.captureBuffer = null;

        this.echoLog = echoLog;
    }

    // Common Logging ...

    protected void logError(int errNum, String msg, Exception ex)
    {
        msg += ": " + ex.getMessage();

        logError(errNum, msg);
    }

    protected void logError(int errNum, String msg)
    {
        msg = "Error ( " + errNum + " ): " + msg;

        basicLog(msg, IS_ERROR_MESSAGE);
    }

    protected void logMessage(String msg)
    {
        msg = "Log: " + msg;

        basicLog(msg, IS_NOT_ERROR_MESSAGE);
    }

    protected void transferResults(Vector results)
    {
        int numResults = results.size();

        for ( int resultNo = 0; resultNo < numResults; resultNo++ ) {
            String nextResult = (String) results.elementAt(resultNo);

            nextResult = "Result: " + nextResult;

            basicLog(nextResult, IS_NOT_ERROR_MESSAGE);
        }
    }

    protected static final boolean IS_ERROR_MESSAGE = true ;
    protected static final boolean IS_NOT_ERROR_MESSAGE = false ;

    protected void basicLog(String msg, boolean isError)
    {
        if ( captureBuffer != null )
            captureBuffer.add(msg);

        if ( echoLog ) {
            if ( !isError )
                System.out.println(msg);
            else
                System.out.println(msg);
        }
    }

    /** Execute a system command, with a single String input.
     *
     * <DL>
     * <DT>execText</DT>
     * <DD>Is the command as it would be entered on the command line.
     * The length of this string may not be greater than what the
     * operating system can support.
     * </DD>
     * <BR>
     * <DT>echoStandardOutput</DT>
     * <DD>If this process should echo the standard out lines.
     * </DD>
     * <BR>
     * <DT>echoStandardError</DT>
     * <DD>If this process should echo the standard error lines.
     * </DD>
     * <BR>
     * <DT>vBuffer</DT>
     * <DD>An optional vector to receive all output lines.
     * </DD>
     * </DL>
     * <BR>
     *
     * Returned is the return code from the task.
     * <BR>
     * Note: For Windows and OS/2, the command line must be prefixed
     * with "CMD.EXE /C ".  If adjustForPlatform = true on the constructor then
     * this will be accomplished on your behalf.
     */

    public int Execute(String execText,
                       boolean echoStandardOutput, boolean echoStandardError,
                       Vector combinedCaptureBuffer, Vector logBuffer)
    {
        return launch(execText, null, null,
                      echoStandardOutput, echoStandardError,
                      combinedCaptureBuffer, logBuffer);
    }
    //added by hongjunz@cn.ibm.com for ouputing process log in time
    public int Execute(String execText,
            boolean echoStandardOutput, boolean echoStandardError, Logger logStream)
    {
    	return launch(execText, null, null,
    				echoStandardOutput, echoStandardError, logStream);
    }

    /** Execute a system command, with a single String input, and an
     *  array of environment overrides.
     */

    public int Execute(String execText, String[] envArray,
                       boolean echoStandardOutput, boolean echoStandardError,
                       Vector combinedCaptureBuffer, Vector logBuffer)
    {
        return launch(execText, null, envArray,
                      echoStandardOutput, echoStandardError,
                      combinedCaptureBuffer, logBuffer);
    }

    /** Execute a system command, with a String array as input.
     *
     * <DL>
     *
     * <DT>execText</DT>
     * <DD>Is the command as it would be entered on the command line
     * with each element of the array to contain a single command line
     * token, and only one token. This method will address command
     * line limitation on some platforms.
     * </DD>
     *
     * <DT>echoStandardOutput</DT>
     * <DD>If this process should echo the standard out lines.
     * </DD>
     *
     * <DT>echoStandardError</DT>
     * <DD>If this process should echo the standard error lines.
     * </DD>
     *
     * <DT>combinedCaptureBuffer</DT>
     * <DD>Is an optional vector to receive all output lines. This may be null.
     * </DD>
     * </DL>
     * <BR>
     * Returned is the return code from the task.
     * <BR>

     * Note: For Windows and OS/2 the command line must be prefixed
     *       with "CMD.EXE /C ". If adjustForPlatform = true on the constructor
     *      then this will be accomplished on your behalf.
     */

    public static final boolean DO_ECHO_STDOUT = true ;
    /** Execute a system command, with a String array as input.
     *
     * <DL>
     *
     * <DT>execText</DT>
     * <DD>Is the command as it would be entered on the command line
     * with each element of the array to contain a single command line
     * token, and only one token. This method will address command
     * line limitation on some platforms.
     * </DD>
     *
     * <DT>echoStandardOutput</DT>
     * <DD>If this process should echo the standard out lines.
     * </DD>
     *
     * <DT>echoStandardError</DT>
     * <DD>If this process should echo the standard error lines.
     * </DD>
     *
     * <DT>combinedCaptureBuffer</DT>
     * <DD>Is an optional vector to receive all output lines. This may be null.
     * </DD>
     * </DL>
     * <BR>
     * Returned is the return code from the task.
     * <BR>

     * Note: For Windows and OS/2 the command line must be prefixed
     *       with "CMD.EXE /C ". If adjustForPlatform = true on the constructor
     *      then this will be accomplished on your behalf.
     */

    public static final boolean DONT_ECHO_STDOUT = false ;
    /** Execute a system command, with a String array as input.
     *
     * <DL>
     *
     * <DT>execText</DT>
     * <DD>Is the command as it would be entered on the command line
     * with each element of the array to contain a single command line
     * token, and only one token. This method will address command
     * line limitation on some platforms.
     * </DD>
     *
     * <DT>echoStandardOutput</DT>
     * <DD>If this process should echo the standard out lines.
     * </DD>
     *
     * <DT>echoStandardError</DT>
     * <DD>If this process should echo the standard error lines.
     * </DD>
     *
     * <DT>combinedCaptureBuffer</DT>
     * <DD>Is an optional vector to receive all output lines. This may be null.
     * </DD>
     * </DL>
     * <BR>
     * Returned is the return code from the task.
     * <BR>

     * Note: For Windows and OS/2 the command line must be prefixed
     *       with "CMD.EXE /C ". If adjustForPlatform = true on the constructor
     *      then this will be accomplished on your behalf.
     */

    public static final boolean DO_ECHO_STDERR = true ;
    /** Execute a system command, with a String array as input.
     *
     * <DL>
     *
     * <DT>execText</DT>
     * <DD>Is the command as it would be entered on the command line
     * with each element of the array to contain a single command line
     * token, and only one token. This method will address command
     * line limitation on some platforms.
     * </DD>
     *
     * <DT>echoStandardOutput</DT>
     * <DD>If this process should echo the standard out lines.
     * </DD>
     *
     * <DT>echoStandardError</DT>
     * <DD>If this process should echo the standard error lines.
     * </DD>
     *
     * <DT>combinedCaptureBuffer</DT>
     * <DD>Is an optional vector to receive all output lines. This may be null.
     * </DD>
     * </DL>
     * <BR>
     * Returned is the return code from the task.
     * <BR>

     * Note: For Windows and OS/2 the command line must be prefixed
     *       with "CMD.EXE /C ". If adjustForPlatform = true on the constructor
     *      then this will be accomplished on your behalf.
     */

    public static final boolean DONT_ECHO_STDERR = false ;

    public int Execute(String[] execArray,
                       boolean echoStandardOutput, boolean echoStandardError,
                       Vector combinedCaptureBuffer, Vector logBuffer)
    {
        return launch(null, execArray, null,
                      echoStandardOutput, echoStandardError,
                      combinedCaptureBuffer, logBuffer);
    }

    /** Execute a system command, with a String array as inputand an
     *  array of environment overrides.
     */

    public int Execute(String[] execArray, String[] envArray,
                       boolean echoStandardOutput, boolean echoStandardError,
                       Vector combinedCaptureBuffer, Vector logBuffer)
    {
        return launch(null, execArray, envArray,
                      echoStandardOutput, echoStandardError,
                      combinedCaptureBuffer, logBuffer);
    }

    protected int launch(String execText, String[] execArray,
                         String[] envArray,
                         boolean echoStandardOutput, boolean echoStandardError,
                         Vector combinedCaptureBuffer, Vector logBuffer)
    {
        this.captureBuffer = logBuffer;

        logMessage("ExecCmd::launch");

        if ( (execText == null) && (execArray == null) ) {
            logError(1, "The commands to execute are null. ");
            return 889;
        }

        if ( adjustForPlatform && !isCaseSensitive() ) {

            if ( execText != null ) {
                execText = "CMD.EXE /C "+ execText;
            } else {
                String[] newExecArray = new String[execArray.length + 2];
                newExecArray[0] = "CMD.EXE";
                newExecArray[1] = "/C";

                for (int i = 0; i < execArray.length; i++ )
                    newExecArray[i+2] = execArray[i];

                execArray = newExecArray;
            }
        }

        String outExecText = PasswordRemover.removePassword(execText, WAS_PASS);
        showArguments("Launching", outExecText, execArray);
        showEnvironment("Environment", envArray);

        Process aProcess = null;
        
        if ( PlatformUtils.isISeries() ) {
            System.out.println("ExecCmd: Switch to original user credentials");
            PuiProfileToken.getToken();
            PuiProfileToken.swapToken();
        }

            try {
                Runtime aRuntime = Runtime.getRuntime();

                if ( execText != null )
                    aProcess = aRuntime.exec(execText, envArray);   // throws IOException
                else
                    aProcess = aRuntime.exec(execArray, envArray);  // throws IOException

            } catch ( IOException ex ) {
                logError(2, "IOException creating RunTime()", ex);
                return 888;
            }

        if ( PlatformUtils.isISeries() ) {
            System.out.println("ExecCmd: Switch to QEJBSVR");
            PuiProfileToken.getToken();
            PuiProfileToken.swapToken();
        }
        
        outExecText = PasswordRemover.removePassword(execText, WAS_PASS);
        
        showArguments("Launched", outExecText, execArray);

        BufferedReader standardOutputTap =
            new BufferedReader( new InputStreamReader(aProcess.getInputStream()) );
        ExecCmd standardOutputSink =
            new ExecCmd(standardOutputTap, echoStandardOutput, null, combinedCaptureBuffer);
        standardOutputSink.start();

        BufferedReader standardErrorTap =
            new BufferedReader( new InputStreamReader(aProcess.getErrorStream()) );
        ExecCmd standardErrorSink =
            new ExecCmd(standardErrorTap, echoStandardError, stderrPrefix, combinedCaptureBuffer);
        standardErrorSink.start();

        boolean
            outputSinkIsAlive = true,
            errorSinkIsAlive  = true;

        logMessage("Waiting for threads");

        // Removing the 500ms sleep from this loop, as it was far too
        // slow for *nix PTF installs.  Leaving most of this structure
        // here, since this appears to have solved a problem with this
        // code at some point in the past.

        int rc = 0;

        try {
            rc = aProcess.waitFor(); // throws InterruptedException
            standardOutputSink.join();
            logMessage("  Standard output has completed.");
            standardErrorSink.join();
            logMessage("  Standard error has completed.");
        }
        catch (InterruptedException e) {
            logError(56, "Thread interrupted while waiting for process: " + e.getMessage());
        }

        transferResults(combinedCaptureBuffer);

        logMessage("ExecCmd::launch returns " + rc);


        try {
            standardOutputTap.close();
        }
        catch (Exception e) {
            logMessage("ExecCmd : Failed to close standardOutputTap ");
            
        }
        
        try {
            standardErrorTap.close();
        }
        catch (Exception e) {
            logMessage("ExecCmd : Failed to close standardErrorTap ");
        }
        
        aProcess.destroy();

        return rc;
    }
    //added by hongjunz@cn.ibm.com for ouputing process log info in time
    protected int launch(String execText, String[] execArray,
            String[] envArray,
            boolean echoStandardOutput, boolean echoStandardError, Logger logStream)
		{
    	logStream.Both("ExecCmd::launch");
		if ( (execText == null) && (execArray == null) ) {
			logStream.Both("Error(" + 1 + "): The commands to execute are null. ");
			return 889;
		}
		if ( adjustForPlatform && !isCaseSensitive() ) {
		if ( execText != null ) {
		   execText = "CMD.EXE /C "+ execText;
		} else {
		   String[] newExecArray = new String[execArray.length + 2];
		   newExecArray[0] = "CMD.EXE";
		   newExecArray[1] = "/C";
		   for (int i = 0; i < execArray.length; i++ )
		       newExecArray[i+2] = execArray[i];
		   execArray = newExecArray;
		}
		}
		String outExecText = PasswordRemover.removePassword(execText, WAS_PASS);
		showArguments("Launching", outExecText, execArray, logStream);
		showEnvironment("Environment", envArray, logStream);
		Process aProcess = null;
		if ( PlatformUtils.isISeries() ) {
			System.out.println("ExecCmd: Switch to original user credentials");
			PuiProfileToken.getToken();
			PuiProfileToken.swapToken();
		}
		try {
		   Runtime aRuntime = Runtime.getRuntime();
		   if ( execText != null )
		       aProcess = aRuntime.exec(execText, envArray);   // throws IOException
		   else
		       aProcess = aRuntime.exec(execArray, envArray);  // throws IOException
		} catch ( IOException ex ) {
			logStream.Both("Error(" + 2 + "): IOException creating RunTime()" + ex);
		   return 888;
		}
		if ( PlatformUtils.isISeries() ) {
			System.out.println("ExecCmd: Switch to QEJBSVR");
			PuiProfileToken.getToken();
			PuiProfileToken.swapToken();
		}
		outExecText = PasswordRemover.removePassword(execText, WAS_PASS);
		showArguments("Launched", outExecText, execArray, logStream);
		logStream.Both("Waiting for threads");
		int rc = 0;
		try {
			InputStream is = aProcess.getInputStream();
			LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(is));
			String message = null;
			while((message = lineReader.readLine()) != null){
				if(message.indexOf("wasPwd =") == -1){
				logStream.Both(message);
				logStream.flush();
				}
			}
			rc = aProcess.waitFor(); // throws InterruptedException
			is.close();
		}
		catch (InterruptedException e) {
			logStream.Both("Error(" + 56 + "): Thread interrupted while waiting for process: " + e.getMessage());
		} catch (IOException e) {
			logStream.Both("Error(" + 56 + "): IOException getting InputStream Process: " + e.getMessage());
		}
		logStream.Both("ExecCmd::launch returns " + rc);
		aProcess.destroy();
		return rc;
	}

    void showArguments(String prefix, String execText, String[] execArray)
    {
        logMessage(prefix + ": ");

        if ( execText != null ) {
        	//System.out.println("use execText");
            logMessage("  [ " + execText + " ]");

        } else {
        	//System.out.println("use execArray");
            for (int tagNo = 0; tagNo < execArray.length; tagNo++ )
                logMessage("  " + tagNo + ": [ " + execArray[tagNo] + " ]");
        }
    }
    //added by hongjunz@cn.ibm.com for ouputing process log info in time
    void showArguments(String prefix, String execText, String[] execArray, Logger logStream)
    {
    	logStream.Both(prefix + ": ");
    	if ( execText != null ) {
    		logStream.Both("  [ " + execText + " ]");
    	} else {
    		for (int tagNo = 0; tagNo < execArray.length; tagNo++ )
    			logStream.Both("  " + tagNo + ": [ " + execArray[tagNo] + " ]");
    	}
    }

    void showEnvironment(String prefix, String[] envArray)
    {
        logMessage(prefix + ": ");

        if ( envArray == null ) {
            logMessage("  [ NULL Environment Overrides ]");

        } else {
            for (int tagNo = 0; tagNo < envArray.length; tagNo++ )
                logMessage("  " + tagNo + ": [ " + envArray[tagNo] + " ]");
        }
    }
    //added by hongjunz@cn.ibm.com for ouputing process log in time
    void showEnvironment(String prefix, String[] envArray, Logger logStream)
    {
    	logStream.Both(prefix + ": ");
    	if ( envArray == null ) {
    		logStream.Both("  [ NULL Environment Overrides ]");
    	} else {
    		for (int tagNo = 0; tagNo < envArray.length; tagNo++ )
    			logStream.Both("  " + tagNo + ": [ " + envArray[tagNo] + " ]");
        }
    }

    void siesta(long snoozeTime)
    {
        try {
            sleep(snoozeTime, 0);

        } catch (InterruptedException ex) {
            logError(3, "Suffering insomnia", ex);
        }
    }

    protected boolean isCaseSensitive()
    {
        String osName = System.getProperty("os.name");

        if (osName.startsWith( "Windows" ) )
           return false;
        /*
        if ( osName.equals("Windows NT") )
            return false;
        else if ( osName.equals("Windows 2000") )
            return false;
        else if ( osName.equals("Windows XP") )
            return false;
            */
        else if ( osName.equals("OS/2") )
            return false;

        else if ( osName.equals("Linux") )
            return true;
        else if ( osName.equals("Solaris") )
            return true;
        else if ( osName.equals("SunOS") )
            return true;
        else if ( osName.equals("AIX") )
            return true;
        else if ( osName.equals("HP-UX") )
            return true;
        else if ( osName.equals("s390") )   // Linux on os/390
            return true;
        else if ( osName.equals("OS/390") ) // OE OS/390
            return true;
        else if ( osName.equals("OS/400") ) // Add OS/400 support (iSeries)
            return true;

        logError(4, "Unrecognized os.name in ExecCmd v" + pgmVersion +
                    " (" + osName + ")");
        return true;   // assume true
    }

    // Internal usage: Use the ExecCmd also to capture output.

    // Capture state:

    public static final String
        stderrPrefix = "StdErr: ";

    protected BufferedReader execOutput;
    protected String linePrefix;     // a prefix added to the begining of the line

    /**
     * Internal constructor.  Used internally to capture output.
     */
 
    protected ExecCmd(BufferedReader execOutput,
                      boolean echoLog,
                      String linePrefix,
                      Vector captureBuffer)
    {
        this(DONT_ADJUST_FOR_PLATFORM, echoLog);

        this.captureBuffer = captureBuffer;

        this.execOutput = execOutput;
        this.linePrefix = linePrefix;
    }

    /**
     * This is an internal class, but must be defined public as the
     * super class is public.
     */

    public void run()
    {
        try {
            String dataLine;

            while ( (dataLine = execOutput.readLine()) != null ) {
                if ( linePrefix != null )
                    dataLine = linePrefix + dataLine;

                basicLog(dataLine, IS_NOT_ERROR_MESSAGE);
                sleep(100);
            }

        } catch ( IOException ex ) {
            logError(5, "IOException reading CaptureBuffer", ex);
        } catch ( InterruptedException ie ) {
            logError(5, "InterruptedException reading CaptureBuffer", ie);
        }
    }
}
