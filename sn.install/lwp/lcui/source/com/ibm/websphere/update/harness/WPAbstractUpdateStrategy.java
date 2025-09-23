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
import java.net.*;
import java.util.*;

/**
 * Class: WPUpdateStrategy.java Abstract: Enables WP  to be updated within the current PTF strategy as a stand-alone product. History 1.1, 9/19/03 19-Sep-2003 Initial Version
 */

public abstract class WPAbstractUpdateStrategy implements UpdateStrategy {


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

		debug = ((debugValue != null) && debugValue.equals(debugTrueValue));
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
	public static final String pgmVersion = "1.2" ;
	//********************************************************
	//  Program Versioning
	//********************************************************	
	public static final String pgmUpdate = "9/18/03" ;

	protected Map harnessFiles;

	protected String productDir;
	protected List consumedExceptions;

	public WPAbstractUpdateStrategy(String productDir) {

      this.productDir = productDir;
		consumedExceptions = new ArrayList();
	}

	/**
	 * 
	 * evaluation of post executeStrategy() state
	 * 
	 * For now nothing to do.  WPCheckFIles, ensure we have our version/dtd files.
	 *   Leave this process so we have an easy place to add process if necessary
	 * 
	 */
	public boolean conformsToStrategy() {

		debug("Verifying that the process conforms to strategy...begin");

      /*
		verifyVersionContents(constructUpdateVersionFileSet(), 0);
		if (!ensureVersionContents()) {
			debug("The harness files in the filesystem does not match the harness files in the resource jar");
			debug("Does not conform to strategy");
			return false;
		}

		if (!conformsToStrategy) {
			debug("Does not conform to strategy");
			return false;
		}
      */
		if (consumedExceptions.size() > 0) {
			debug("Process ended with exceptions");
			debug("Does not conform to strategy");
			return false;
		}

		return true;

	}

	public boolean cleanUp() {
      debug("Nothing to clean up");
		return true;
	}

	/**
	 * 
	 *  executeStrategy()
	 * 
	 * For now nothing to do.  WPCheckFIles, ensure we have our version/dtd files.
	 *   Leave this process so we have an easy place to add process if necessary
	 * 
	 */
	public void executeStrategy() {
		debug("executeStrategy(" + productDir + ")...entered");
      debug("Update harness files already present...continue");
   }

	/**
	 * 
	 * Exception handler methods
	 * 
	 */
	public int numConsumedExceptions() {
      return consumedExceptions.size();
   }

	/**
	 * @param consumedExceptions  the consumedExceptions to set
	 * @uml.property  name="consumedExceptions"
	 */
	public void setConsumedExceptions(List consumedExceptions) {
      this.consumedExceptions = consumedExceptions;
   }

	/**
	 * @return  the consumedExceptions
	 * @uml.property  name="consumedExceptions"
	 */
	public List getConsumedExceptions() {
      return consumedExceptions;
   }


}
