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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TransferFeatureNodes {
	protected FileWriter fWriter;
	protected PrintWriter pWriter;

	public void saveFeatureNodes(String file, String line) {
		StringTokenizer st;
		st = new StringTokenizer(line, ";");
		fWriter = null;
		pWriter = null;
		try {
			fWriter = new FileWriter(file, true);
			pWriter = new PrintWriter(fWriter);
			pWriter.println();
			while (st.hasMoreTokens()) {
				String entry = st.nextToken();
				if (entry.startsWith("news.")) {
					// Work item 68491
					pWriter.println(entry.replaceFirst("news", "commonear"));
					pWriter.println(entry.replaceFirst("news", "widgetcontainer"));
				}
				pWriter.println(entry);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pWriter != null) {
					pWriter.close();
				}
				if (fWriter != null) {
					fWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected static String normalizePath(String wasLoc) {
		// TODO Auto-generated method stub

		if (null == wasLoc)
			return null;

		wasLoc = wasLoc.trim();

		if (wasLoc.lastIndexOf(File.separator) == wasLoc.length() - 1)
			return wasLoc.substring(0, wasLoc.length() - 1);
		else
			return wasLoc;
	}

	public static String getProfilePath(String profileName, String profileLoc) {

		String profileXml = "profileRegistry.xml";
		StringBuffer sbPath = new StringBuffer(normalizePath(profileLoc));
		sbPath.append(File.separator);
		sbPath.append("properties");
		sbPath.append(File.separator);
		sbPath.append(profileXml);

		File fFindDir = new File(sbPath.toString());
		if (fFindDir.isFile()) {
			String profilePath = getProfilePathXML(fFindDir, profileName);
			return profilePath;

		} else {
			System.out.println("the profileRegistry.xml can not be found.");
			return null;
		}
	}

	private static String getProfilePathXML(File profileFile, String profileName) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(profileFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("profile");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String profileNameTem = attrs.getNamedItem("name")
						.getNodeValue();
				String profilePath = attrs.getNamedItem("path").getNodeValue();
				if (profileNameTem.equals(profileName)) {
					return profilePath;
				}
			}// end of for clause

			return null;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;

	}

	public static void main(String[] args) throws Exception {
		TransferFeatureNodes tf = new TransferFeatureNodes();
		if (args.length == 2)
			tf.saveFeatureNodes(args[0], args[1]);
		else if (args.length == 7) {
			String profilePath = args[0];
			String pyPath = args[1];
			String wasUserId = args[2];
			String wasPasswd = args[3];
			String wasHost = args[4];
			String wasPortNum = args[5];
			String returnCode = null;
			boolean isWindows = args[6].equals("T");
			try {
				returnCode = DMValidator.getDMInfo(profilePath, pyPath,
						wasUserId, wasPasswd, wasHost, wasPortNum, isWindows);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Error getting DM info");
			}
			if (!returnCode.equals("0")) {
				System.out.println("Error getting DM info");
				throw new Exception("Error getting DM info");
			} else {
				System.out.println("Getting DM info successful");
			}
		}
	}

}
