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
 * Class: WASUpdateHarnessManager.java Abstract: Enables an eFix/FixPack update to be applied to a WAS-lite/IHS/WAS-plugin product. Component Name: WAS.ptf Release: ASV50X History 1.2, 3/13/03 01-Feb-2003 Initial Version
 */

public class WASUpdateHarnessManager implements UpdateHarnessManager {

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
		System.out.println(debugValue);

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
    public static final String pgmUpdate = "3/13/03" ;

	//********************************************************
	//  Instance State
	//********************************************************
	private String productDir;
	private UpdateProductType productType;
	
	private UpdateStrategy updateStrategy;

	public WASUpdateHarnessManager(String productDir, UpdateProductType productType){		
		this.productDir = productDir;		
		this.productType = productType;		
	}

	//********************************************************
	//  Method Definition
	//********************************************************
	public boolean harness(){
		
		if(productType.isWASLite()){			
			updateStrategy = new WASLiteUpdateStrategy(this.productDir);						
		} else if (productType.isIHS()){
			updateStrategy = new IHSUpdateStrategy(this.productDir);			
		} else if (productType.isWASPlugin()){
			updateStrategy = new WASPluginUpdateStrategy(this.productDir);				
		} else if (productType.isWPCP()){
			updateStrategy = new WPCPUpdateStrategy(this.productDir);				
		}
	
	
		debug("Target Product Directory --->" + productDir);
		debug("WASUpdateHarnessMananger invoking executeStrategy()...");			
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
	/*
	public static void main(String args[]){
		//UpdateHarnessManager wuhm = new WASUpdateHarnessManager("C:\\IBMHttpServer", UpdateProductType.IHS);
		//wuhm.harness();			
		
		new IHSUpdateStrategy("C:\\IBMHttpServer").cleanUp();
	}
	*/

}
