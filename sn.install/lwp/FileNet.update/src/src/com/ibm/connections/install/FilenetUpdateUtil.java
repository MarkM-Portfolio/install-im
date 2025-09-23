/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FilenetUpdateUtil {
	
	/** return the soap port number of the specified node profile */
	public static String getDMNodePort(String profilePath) {
		String path = profilePath + File.separator + "config" + File.separator
				+ "cells";
		
		File[] dirs = new File(path).listFiles();
		if (dirs == null || dirs.length == 0)
			return null;
		File cur = null;
		for (File tmp : dirs) {
			if (!tmp.isDirectory())
				continue;
			cur = tmp;
			path = cur.getAbsolutePath() + File.separator + "nodes";

			// OS400_Enablement  bug fix for all platform, change dirs to dirs1.
			File[] dirs1 = new File(path).listFiles();
			if (dirs1 == null || dirs1.length == 0)
				return null;
			for (File curFile : dirs1) {
				if (!curFile.isDirectory())
					continue;
				File indexFile = new File(curFile.getAbsolutePath()
						+ File.separator + "serverindex.xml");
				
				try {
					DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docBuilderFactory
							.newDocumentBuilder();
					Document doc = docBuilder.parse(indexFile);
					// normalize text representation
					doc.getDocumentElement().normalize();
					NodeList entrys = doc.getElementsByTagName("serverEntries");
					if (entrys == null || entrys.getLength() == 0)
						continue;
					Node entryNode = entrys.item(0);
					NamedNodeMap attrs = entryNode.getAttributes();
					String serverType = attrs.getNamedItem("serverType")
							.getNodeValue();
					if (!serverType.equals("DEPLOYMENT_MANAGER"))
						continue;
					entrys = doc.getElementsByTagName("specialEndpoints");
					if (entrys == null || entrys.getLength() == 0)
						continue;
					String id = "";
					for (int i = 0; i < entrys.getLength(); i++) {
						Node curEntry = entrys.item(i);
						attrs = curEntry.getAttributes();
						if (!attrs.getNamedItem("endPointName").getNodeValue()
								.equals("SOAP_CONNECTOR_ADDRESS"))
							continue;
						id = attrs.getNamedItem("xmi:id").getNodeValue();
						break;
					}

					entrys = doc.getElementsByTagName("endPoint");
					for (int i = 0; i < entrys.getLength(); i++) {
						Node curEntry = entrys.item(i);
						attrs = curEntry.getAttributes();
						String curId = attrs.getNamedItem("xmi:id")
								.getNodeValue();
						if (id.indexOf(curId) < 0)
							continue;
						return attrs.getNamedItem("port").getNodeValue();
					}
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		
		if ( args.length == 0 || args.length > 1 ) {
			System.out.println("Usage: com.ibm.connections.install.FileNetUtil dmProfilePath");
			System.exit(1);
		}
		
		String port = getDMNodePort(args[0]);
		
		if ( port == null ) {
			System.out.println("Error: A problem occurs in runtime of the program, the following are possible reasons:");
			System.out.println("1. The parameter is not the location of WAS Deployment Manager profile folder;");
			System.out.println("2. Some other reasons that make the serverindex.xml of the DM profile unaccessible.");
			System.exit(1);
		}
		
		System.out.println(port);
	}
	
}
