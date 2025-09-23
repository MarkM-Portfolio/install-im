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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.wps.depcheck.PortalValidation;
import com.ibm.wps.depcheck.VersionInfo;

public class DetectiveProfileAction {

	public static PortalValidation validator = null;
	public final static String TYPE_DEPLOY_MANAGEMENT = "dmgr";
	public final static String TYPE_DEPLOY_MANAGEMENT1 = "management";

	//for cache
	public static Hashtable ht = new Hashtable();

	public static ArrayList getDMProfile(String wasLoc) {

		//		ArrayList result = (ArrayList)ht.get(wasLoc);
		//		if(result == null){
		ArrayList wasProfiles = getValidator().getWASProfiles(wasLoc);

		return wasProfiles;
		//			System.out.println(wasProfiles);
		//			result = (ArrayList)filterProfile(wasProfiles,wasLoc);
		//			ht.put(wasLoc, result);
		//		}
		//		
		//		return result;
	}

	private static ArrayList filterProfile(ArrayList wasProfiles, String wasLoc) {

		ArrayList newProfileList = new ArrayList();
		Iterator it = wasProfiles.iterator();
		while (it.hasNext()) {
			String profileName = (String) it.next();
			if (isDMProfile(profileName, wasLoc)) {
				newProfileList.add(profileName);
			}
		}
		return newProfileList;

	}

	private static boolean isDMProfile(String profileName, String profileLoc) {

		String profileXml = "profileRegistry.xml";
		StringBuffer sbPath = new StringBuffer(normalizePath(profileLoc));
		sbPath.append(File.separator);
		sbPath.append("properties");
		sbPath.append(File.separator);
		sbPath.append(profileXml);

		File fFindDir = new File(sbPath.toString());
		if (fFindDir.isFile()) {
			boolean isDMProfile = checkProfileXMLTemp(fFindDir, profileName);
			return isDMProfile;

		} else {
			System.out.println("the profileRegistry.xml can not be found.");
			return false;
		}
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

	public static boolean checkProfileXMLTemp(File profileFile, String profileName) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(profileFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("profile");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String profileNameTem = attrs.getNamedItem("name").getNodeValue();
				String templateName = attrs.getNamedItem("template").getNodeValue();
				if (profileNameTem.equals(profileName)) {
					if (templateName.endsWith(TYPE_DEPLOY_MANAGEMENT) || templateName.endsWith(TYPE_DEPLOY_MANAGEMENT1)) {
						return true;
					}
				}
			}// end of if clause

			return false;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static String getProfilePathXML(File profileFile, String profileName) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(profileFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("profile");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String profileNameTem = attrs.getNamedItem("name").getNodeValue();
				String profilePath = attrs.getNamedItem("path").getNodeValue();
				if (profileNameTem.equals(profileName)) {
					return profilePath;
				}
			}// end of for clause

			return null;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public boolean checkProfileXML(File profileFile, String profileName) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(profileFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("profile");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				NamedNodeMap attrs = entryNode.getAttributes();
				// get the server name and server type attribute
				String name = attrs.getNamedItem("name").getNodeValue();
				if (name.equals(profileName)) {
					String path = attrs.getNamedItem("path").getNodeValue();
					System.out.println(this.getValidator().getWASNodes(path));
					return checkVersionfile(path);
				}

			}// end of if clause

			return false;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkVersionfile(String profilePath) {

		String profileVersion = "profile.version";
		StringBuffer sbPath = new StringBuffer(normalizePath(profilePath));
		sbPath.append(File.separator);
		sbPath.append("properties");
		sbPath.append(File.separator);
		sbPath.append("version");
		sbPath.append(File.separator);
		sbPath.append(profileVersion);
		return this.isDM(new File(sbPath.toString()));
	}

	public boolean isDM(File profileFile) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(profileFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("id");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				String id = entryNode.getFirstChild().getNodeValue();
				if (id.equals(TYPE_DEPLOY_MANAGEMENT) || id.equals(TYPE_DEPLOY_MANAGEMENT1)) {
					return true;
				}
			}// end of if clause

			return false;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * nomalize the path
	 * 
	 * @param wasLoc
	 * @return
	 */
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

	public ArrayList searchFile(String fileName, String findDir) {

		ArrayList nodeXmlPathList = new ArrayList();

		if (null == fileName || "".equals(fileName)) {
			return null;
		}
		String path = "";
		File fFindDir = new File(findDir);
		File[] fileDirs = fFindDir.listFiles();

		for (int i = 0; i < fileDirs.length; i++) {
			File file = fileDirs[i];
			if (!file.isFile()) {
				ArrayList filePath = searchFile(fileName, file.getPath());
				if (null != filePath) {
					nodeXmlPathList.addAll(filePath);
				}
			} else {
				String curName = file.getName();
				if (fileName.equals(curName)) {
					path = file.getAbsolutePath();
					nodeXmlPathList.add(path);
				}
			}

		}
		return nodeXmlPathList;

	}

	/**
	 * get version info
	 * 
	 * @return
	 */
	public static PortalValidation getValidator() {

		if (null == validator) {

			int major = 6;
			int minor = 0;
			int maint = 0;
			int update = 0;
			String vRelease = PortalValidation.PORTAL_MP_PRODUCT;

			VersionInfo versionInfo = new VersionInfo(vRelease, major, minor, maint, update);

			validator = new PortalValidation(versionInfo);
		}

		return validator;
	}

	public static void main(String[] args) {
		DetectiveProfileAction dp = new DetectiveProfileAction();
		System.out.println(dp.getDMProfile("C:\\Program Files\\IBM\\WebSphere\\AppServer1"));
	}

}
