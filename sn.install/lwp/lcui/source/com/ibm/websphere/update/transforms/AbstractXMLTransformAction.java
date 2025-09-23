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
package com.ibm.websphere.update.transforms;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;

import javax.xml.transform.*;
import java.util.*;
import java.net.*;
import java.io.*;

/* 
 * ClassName: AbstractXMLTransformAction
 * Abstract: An abstraction for transformation updates
 * 
 * History 1.1, 11/14/02
 *
 * 30-Aug-2002 Initial Version
 */

public abstract class AbstractXMLTransformAction implements Action {

	/**
	 * Returns an implementation specific transformerFactory object
	 */
	public TransformerFactory getTransformerFactory(){
		TransformerFactory tFactory = TransformerFactory.newInstance();
		return tFactory;
	}
	
	/**
	 * Returns the transformer URL identifier
	 */
	public abstract URL getTransformerId();
	
	
	/**
	 * Returns the list of file identifiers
	 */
	public abstract List getFileIds();


}