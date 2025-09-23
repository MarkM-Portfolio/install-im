/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.xml;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */

public class XMLUtil {
	private static XMLOperator op = new XMLOperator();

	public static void execReplaceElements(String sourceXML, String destXML,
			String[] sourceAry, String[] destAry) {
		op.execReplaceElements(sourceXML, destXML, sourceAry, destAry);
	}

	public static void execSetValueTask(String sourceXML, String destXML,
			String xpathLevelsStr, String xpathLevelForDest,
			String targetValueIndex) {
		op.execSetValueTask(sourceXML, destXML, xpathLevelsStr,
				xpathLevelForDest, targetValueIndex);
	}

	public static Document getDocument(String xmlSource) {
		return op.getDocument(xmlSource);
	}

	public static void replaceElement(Document sourceDoc, Document destDoc,
			String elementSourceXpath, String elementDestXpath)
			throws TransformerException {
		op.replaceElement(sourceDoc, destDoc, elementSourceXpath,
				elementDestXpath);
	}

	public static void saveXML(String destXML, Document destDoc)
			throws FileNotFoundException, IOException {
		op.saveXML(destXML, destDoc);
	}

	public static void saveXMLPretty(String destXML, Document destDoc)
			throws FileNotFoundException, IOException {
		op.saveXMLPretty(destXML, destDoc);
	}
}
