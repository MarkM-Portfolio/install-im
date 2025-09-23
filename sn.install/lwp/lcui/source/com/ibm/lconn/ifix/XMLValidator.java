/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLValidator {
    public static final void validate(File xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            DefaultHandler handler = new DefaultHandler() {
                public void warning(SAXParseException e) throws SAXException {
                    throw e;
                }

                public void error(SAXParseException e) throws SAXException {
                    throw e;
                }

                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }
            };
            parser.setErrorHandler(handler);
            parser.parse(xml);

        } catch (Exception e) {
        	System.out.println("Failed to validate XML with XML Schema...");
            throw e;
        }
    }
}
