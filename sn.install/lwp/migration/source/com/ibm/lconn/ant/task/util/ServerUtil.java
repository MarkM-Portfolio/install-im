/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.ant.task.util;
import java.io.File;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */
public class ServerUtil {
	private static final String serverindexPathTemplete = "{0}/profiles/{1}/config/cells/{2}/nodes/{3}/serverindex.xml";
	private static String portXPathTemplete = "serverEntries[@serverName=''{0}'']/specialEndpoints[@endPointName=''{1}'']/endPoint/@port";

	public static void main(String[] args) {
		String wasHome = args[0]; 
		String profileName = args[1];
		String cellName = args[2];
		String nodeName = args[3];
		String serverName = args[4];
		String portName = args[5];
		String port = readPortValue(wasHome, profileName, cellName, nodeName,
				serverName, portName);
		System.out.println(port);
	}

	public static String readPortValue(String wasHome, String profileName,
			String cellName, String nodeName, String serverName, String portName) {
		String val = "";
		Object[] paraForPath = { wasHome, profileName, cellName, nodeName };
		File serverIndexFile = new File(MessageFormat.format(
				serverindexPathTemplete, paraForPath));

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(serverIndexFile);
			Element rootElement = document.getDocumentElement();
			String xpath = MessageFormat.format(portXPathTemplete , serverName, portName);
			Node n = XPathAPI.selectSingleNode(rootElement, xpath);
			val = n.getTextContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
}
