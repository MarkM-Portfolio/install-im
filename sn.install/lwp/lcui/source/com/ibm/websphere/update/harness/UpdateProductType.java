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
 * Class: UpdateProductType.java Abstract: Encapsulates product type information Component Name: WAS.ptf Release: ASV50X History 1.2, 3/13/03 01-Feb-2003 Initial Version
 */
public class UpdateProductType {

	private String productId;

	public static UpdateProductType WAS_LITE = new UpdateProductType("WAS_LITE");
	public static UpdateProductType IHS = new UpdateProductType("IHS");
	public static UpdateProductType WAS_PLUGIN = new UpdateProductType("WAS_PLUGIN");
	public static UpdateProductType WPCP = new UpdateProductType("WPCP");

	private UpdateProductType(String productId){
		this.productId = productId;	
	}
	
	public String toString() {
		return productId;	
	}
	
	public String getProductType(){
		return productId;	
	}
	
	public boolean isWASLite(){
		return this.productId.equals("WAS_LITE");
		
	}
	
	public boolean isIHS(){
		return this.productId.equals("IHS");
		
	}
	
	public boolean isWASPlugin(){
		return this.productId.equals("WAS_PLUGIN");	
	}

	public boolean isWPCP(){
		return this.productId.equals("WPCP");	
	}
}
