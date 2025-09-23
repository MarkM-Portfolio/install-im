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

package com.ibm.websphere.update.silent;

import com.ibm.websphere.update.launch.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

/**
 * Class: SilentArgsCollector Abstract: Collects silent installation arguments as a proxy for delegation. Component Name: WAS.ptf Release: ASV50X History 1.1, 3/11/03 01-Nov-2002 Initial Version
 */

public class SilentArgsCollector extends Launcher {

	private static String[] silentArgs;

	/**
	 * @return  the silentArgs
	 * @uml.property  name="silentArgs"
	 */
	public static String[] getSilentArgs(){
		return silentArgs;	
	}

	/**
	 * @param silentArgs  the silentArgs to set
	 * @uml.property  name="silentArgs"
	 */
	public static void setSilentArgs(String args[]){
		silentArgs = args;			
	}

	private static void launchISMPSilentMode(String ismpClass){

            try {
                setEncoding();
            } catch ( UnsupportedEncodingException ex ) {
                handleError(ex, "Unable to update encoding:");
                return;
            }
    
            Class targetClass;
    
            try {
                targetClass = locateTarget(ismpClass); 
            } catch ( Exception ex ) {
                handleError(ex, "Unable to load main class " + ismpClass);
                return;
            }
    
            Method mainMethod;
    
            try {
                mainMethod = locateMain(targetClass); 
            } catch ( Exception ex ) {
                handleError(ex, "Unable to locate main method");
                return;
            }
    
            Object result;
    		String[] newArgs = buildISMPArgs();
    
    
            try {
                result = invokeMain(mainMethod, newArgs); // throws Exception
            } catch ( Exception ex ) {
                handleError(ex, "Failure during invocation");
                return;
            }
    	
	}
  
  	private static String[] buildISMPArgs(){
		String[] ismpArgs = {"-silent", "-W", "PTFSilentInstall.active=true"};
  		return ismpArgs;
  	}
  
	public static void main(String args[]){
            System.out.println( "Starting SilentArgsCollector.main()" );

		String[] silentArgs = new String[args.length];
		for(int i=0; i<args.length; i++){
			silentArgs[i] = args[i];
                        System.out.println( "silentArgs[" + i + "] == \"" + silentArgs[i] + "\"" );
		}		
		
		setSilentArgs(silentArgs);				
		
		launchISMPSilentMode("run");							
                System.out.println( "Exiting SilentArgsCollector.main()" );
	}					 
}
