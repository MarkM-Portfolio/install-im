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

package com.ibm.websphere.update.harness;

import java.io.*;
import java.util.*;


/**
 * Class: WASUpdateHarnessManager.java Abstract: Enables an eFix/FixPack update to be applied to a WAS-lite/IHS/WAS-plugin product. History 1.3, 9/27/03 19-Sep-2003 Initial Version
 */

public abstract class WPUpdateHarnessManager implements UpdateHarnessManager {

	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugPropertyName = "com.ibm.websphere.update.harness.debug" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugTrueValue = "true" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
	public static final String debugFalseValue = "false" ;

	// Debugging support ...
	protected static boolean debug;

	static {
		String debugValue = System.getProperty(debugPropertyName);
		//System.out.println(debugValue);

		debug = ((debugValue != null) && debugValue.equals(debugTrueValue));
      if (debug) {
         System.out.println( "UpdateHarness debugging enabled." );
      }
	}

	/**
	 * @return  the debug
	 * @uml.property  name="debug"
	 */
	public static boolean isDebug() {
		return debug;
	}

	public static void debug(String arg) {
		if (!debug)
			return;

		System.out.println(arg);
	}

	public static void debug(String arg1, String arg2) {
		if (!debug)
			return;

		System.out.print(arg1);
		System.out.println(arg2);
	}

	//********************************************************
	//  Program Versioning
	//********************************************************	
    public static final String pgmVersion = "1.3" ;
	//********************************************************
	//  Program Versioning
	//********************************************************	
    public static final String pgmUpdate = "9/27/03" ;

	//********************************************************
	//  Instance State
	//********************************************************
	protected String productDir;
	
	protected UpdateStrategy updateStrategy;

	public WPUpdateHarnessManager(String productDir){		
		this.productDir = productDir;		
	}

   protected abstract UpdateStrategy getUpdateStrategy( String productDir );

	//********************************************************
	//  Method Definition
	//********************************************************
	public boolean harness(){
      updateStrategy = getUpdateStrategy( this.productDir );
	
		debug("Target Product Directory --->" + productDir);
		debug("WPUpdateHarnessMananger invoking executeStrategy()...");			
		updateStrategy.executeStrategy();
		
		if(!updateStrategy.conformsToStrategy()){
			debug("Failure to conform to strategy...harness process unsuccessful");
			debug("Cleaning up directories...");
			if(updateStrategy.cleanUp())
				debug("Clean up successful.");
			else
			 	debug("Clean up failed.");
			return false;
		}
	
		if(updateStrategy.numConsumedExceptions() > 0){
			debug("Cleaning up directories...");
			if(updateStrategy.cleanUp())
				debug("Clean up successful.");
			else
			 	debug("Clean up failed.");
		}
		
		debug("Harness process completed without error");
		return true;
		
	}
	
	public boolean cleanUp(){
		return updateStrategy.cleanUp();
	}
}
