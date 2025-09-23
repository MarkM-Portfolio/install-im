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

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @ (#) StreamCollectionThread.java
 * 
 * $1.1,$11/20/03
 * 
 * This class collects the contents from a given InputStream into
 * its buffer in a seperate thread.
 * <p>
 * @author Rohit V. Kapoor
 */
public class StreamCollectionThread	// sct
	extends Thread
{	
	/**
	 * Creates a new StreamCollectionThread object
	 * <p>
	 * @param InputStream 	The stream to collect
	 */
	public StreamCollectionThread( InputStream inputstream )
	{
		m_inputstream = inputstream;
		
		return;
	}

	/**
	 * Runs this thread and starts the collection from the given input
	 * stream
	 */	
	public void run()
	{
		BufferedReader bufferedreaderIn = new BufferedReader( 
				new InputStreamReader( m_inputstream ) );

		String sIn = null;
		
		while ( ( !shouldWeStopCollecting() ) || ( sIn != null ) )
		{
			try
			{
				sIn = bufferedreaderIn.readLine();
			}
			catch ( IOException e )
			{}
			
			if ( sIn != null )
			{
				m_sBuffer = m_sBuffer + sIn + S_EOLN;
			}
		}
		
		return;
	}
	
	/**
	 * Call this function to start the collection
	 */
	public void startCollecting()
	{
		m_fStopFlag = false;
		
		this.start();
		
		return;
	}

	/**
	 * Stops the collection
	 */	
	public void stopCollecting()
		throws InterruptedException
	{
		m_fStopFlag = true;
		
		this.join();
		
		return;
	}

	/**
	 * @return The string representation of this object
	 */
	public String toString()
	{
		return m_sBuffer.toString();
	}

	/**
	 * @return <code>true</code> if we should stop collecting
	 */
	private boolean shouldWeStopCollecting() 
	{
		return m_fStopFlag;
	}

	
	/** Class variables follow */
	
	/** @serial Holds the input stream to collect from */
	private InputStream m_inputstream = null;
	
	/** Holds the collection string buffer */
	private String m_sBuffer = new String();
	
	/** Holds the stopping flag */
	private boolean m_fStopFlag = false;
	
	
	/** Class constants follow */
	private static final String S_EOLN = "\n";	
}
