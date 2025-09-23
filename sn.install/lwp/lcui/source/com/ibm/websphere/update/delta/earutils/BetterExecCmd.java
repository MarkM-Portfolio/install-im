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

package com.ibm.websphere.update.delta.earutils;

import java.util.Vector;
import java.io.IOException;
import java.lang.InterruptedException;
import com.ibm.websphere.update.delta.POProcessor;
import com.ibm.websphere.update.delta.ExtendedUpdateAction;

/**
 * @ (#) ExecShellCmd.java
 * 
 * $1.1,$11/20/03
 * 
 * This is a generic class to execute an OS shell command. As
 * arguments it will accept the OS shell to launch, the command
 * to launch in that shell and the arguments for that command. It
 * returns the exit code received from the shell command as its
 * return value.
 * 
 * For macros available to filter files using this class to spawn processes,
 * please see this class:
 * 	<code>com.ibm.websphere.update.filteractions.MacroExpander</code>
 * <p>
 * @author Rohit V. Kapoor
 */
public class BetterExecCmd	// bec
{   
	/**
     * Executes the given incoming process with the specified arguments. 
     * This function waits for the process to finish before returning.
     * <p>
     * @param asArguments							The incoming vector of arguments from the filterfile
     * <p>
     * @return The return code from the process spawned or -1 on error
     */
    public int executeIncomingArguments( final String[] asArguments )
    {
    	return this.spawnProcess( asArguments );
    }
	 
	/**
     * Executes the given incoming process with the specified arguments. 
     * This function waits for the process to finish before returning.
     * <p>
     * @param vArgs							The incoming vector of arguments from the filterfile
     * <p>
     * @return The return code from the process spawned or -1 on error
     */
    public int executeIncomingArguments( final Vector vArgs )
    {
    	String[] asArguments = new String[ vArgs.size() ];
    	for ( int i = 0; i < vArgs.size(); i++ )
    	{
    		asArguments[ i ] = ( String )vArgs.elementAt( i );
    	}
    	
    	return this.spawnProcess( asArguments );
    }
    
    /**
     * @return The process' stdout
     */
    public String getProcessStdOut()
    {
    	return m_sOut;
    }
    
    /**
     * @return The process' stderr
     */
    public String getProcessStdErr()
    {
    	return m_sErr;
    }
    
    /**
     * @return The process' termination code
     */
    public int getProcessRetCode()
    {
    	return m_nRetVal;
    }
	
	/**
	 * Waits for the given process to terminate or time out
	 * <p>
	 * @param processThis The process to wait for
	 */
	protected void doWaitForProcessToTerminateOrTimeOut( Process processThis ) 
		throws InterruptedException
	{
		processThis.waitFor();
		
		return;
	}
	
	/**
	 * Makes sure that when asked this POProcessor instance can resolve
	 * the WAS home given its key: WAS_HOME
	 * <p>
	 * @param sRoot 	The incoming WAS root
	 * @param po		The incoming vector of arguments
	 */
	private void makeSurePOCanResolveWASHome( final String sRoot, 
		POProcessor po ) 
	{
		po.setDefault( S_PO_WAS_DIR_KEY, sRoot );
		
		return;
	}
    
    /**
	 * Spawns the given process, waits for it to finish and returns its exit value
	 * or -1 on error
	 * <p>
     * @param asArguments				The arguments that define the process to be launched
     * <p>
     * @return The return code from the process spawned or -1 on error
     */
	private int spawnProcess( final String[] asArguments) 
	{
		int nRetVal = -1;
		
		try
		{
			Process processThis = Runtime.getRuntime().exec( asArguments );
			
			StreamCollectionThread sctOutput = new StreamCollectionThread( processThis.getInputStream() );
			StreamCollectionThread sctError = new StreamCollectionThread( processThis.getErrorStream() );
			
			sctOutput.startCollecting();
			sctError.startCollecting();
			
			this.doWaitForProcessToTerminateOrTimeOut( processThis );
			
			sctOutput.stopCollecting();
			sctError.stopCollecting();
			
			m_sOut = sctOutput.toString();
			
			m_sErr = sctError.toString();			
			
			m_nRetVal = processThis.exitValue();
		}
		catch ( IOException e )
		{
			nRetVal = -1;
		}
		catch ( InterruptedException e )
		{
			nRetVal = -1;
		}
		catch ( IllegalThreadStateException e )
		{
			nRetVal = -1;
		}	
		
		return nRetVal;
	}

	
	/** Class variables follow */
	
	/** @serial Holds the stdout of the process */
	private String m_sOut = null;
	
	/** @serial Holds the stderr of the process */
	private String m_sErr = null;
	
	/** @serial Holds the return code of the process */
	private int m_nRetVal = -1;


    /** Class constants follow */
    private static final String S_PO_WAS_DIR_KEY = "WAS_HOME";
    private static final String S_SPAWNING_THIS_PROCESS = "Spawning this process: ";
    private static final String S_PROCESS_SPAWN_ERROR = 
    	"Error: Error in spawning the given process, returning an exit code of -1. The error was: ";
    private static final String S_PROCESS_OUTPUT_FOLLOWS = 
    	"Spawned process' output follows: \n";
    private static final String S_PROCESS_OUTPUT_ENDS = 
    	"End of output";
    private static final String S_PROCESS_ERRORS_FOLLOW = 
    	"Spawned process' errors follow: \n";
    private static final String S_PROCESS_ERRORS_END = 
    	"End of errors";
    private static final String S_PROCESS_EXIT_CODE_WAS = 
    	"The process exit code was: ";
}
