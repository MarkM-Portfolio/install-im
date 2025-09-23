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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.wps.depcheck.PortalValidation;
import com.ibm.wps.depcheck.VersionInfo;

public class DetectiveProfileAction {

	public static PortalValidation validator = null;
	public final static String TYPE_DEPLOY_MANAGEMENT = "dmgr";
	public final static String TYPE_DEPLOY_MANAGEMENT1 = "management";
	
	private static final ILogger log = IMLogger
	.getLogger(com.ibm.connections.install.DetectiveProfileAction.class);

	// for cache
	public static Hashtable ht = new Hashtable();

	public static ArrayList getDMProfile(String wasLoc) {

		// ArrayList result = (ArrayList)ht.get(wasLoc);
		// if(result == null){
		ArrayList wasProfiles = getValidator().getWASProfiles(wasLoc);

		return wasProfiles;
		// System.out.println(wasProfiles);
		// result = (ArrayList)filterProfile(wasProfiles,wasLoc);
		// ht.put(wasLoc, result);
		// }
		//
		// return result;
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

	public static ArrayList getDMProfileNew(String wasLoc) {

		ArrayList result = (ArrayList) ht.get(wasLoc);

		if (result == null) {
			result = getWasProfile(wasLoc);
			ht.put(wasLoc, result);
		}
		return result;

	}

	private static ArrayList getWasProfile(String wasLoc) {
		// OS400_Enablement
		// On OS400, the wasLoc here is the WAS User Home dir, e.g.
		// 		/IBM/Userdata/Websphere/AppServer/v8/nd 
		StringBuffer sbPath = null;
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			String profileXml = "profileRegistry.xml";
			sbPath = new StringBuffer(normalizePath(wasLoc));
			sbPath.append(File.separator);
			sbPath.append("profileRegistry");
			sbPath.append(File.separator);
			sbPath.append(profileXml);
		} else {
			String profileXml = "profileRegistry.xml";
			sbPath = new StringBuffer(normalizePath(wasLoc));
			sbPath.append(File.separator);
			sbPath.append("properties");
			sbPath.append(File.separator);
			sbPath.append(profileXml);
		}

		File fFindDir = new File(sbPath.toString());
		if (fFindDir.isFile()) {
			ArrayList DMProfileList = checkProfileXMLNew(fFindDir);
			return DMProfileList;

		} else {
			System.out.println("the profileRegistry.xml can not be found.");
			return null;
		}

	}

	private static boolean isDMProfile(String profileName, String profileLoc) {

		StringBuffer sbPath = null;
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			// OS400_Enablement
			String profileXml = "profileRegistry.xml";
			sbPath = new StringBuffer(normalizePath(profileLoc));
			sbPath.append(File.separator);
			sbPath.append("profileRegistry");
			sbPath.append(File.separator);
			sbPath.append(profileXml);
		} else {
			String profileXml = "profileRegistry.xml";
			sbPath = new StringBuffer(normalizePath(profileLoc));
			sbPath.append(File.separator);
			sbPath.append("properties");
			sbPath.append(File.separator);
			sbPath.append(profileXml);
		}

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

		StringBuffer sbPath = null;
		if (System.getProperty("os.name").toLowerCase().startsWith("os/400")) {
			// OS400_Enablement
			String profileXml = "profileRegistry.xml";
			sbPath = new StringBuffer(normalizePath(profileLoc));
			sbPath.append(File.separator);
			sbPath.append("profileRegistry");
			sbPath.append(File.separator);
			sbPath.append(profileXml);
		} else {
			String profileXml = "profileRegistry.xml";
			sbPath = new StringBuffer(normalizePath(profileLoc));
			sbPath.append(File.separator);
			sbPath.append("properties");
			sbPath.append(File.separator);
			sbPath.append(profileXml);
		}

		File fFindDir = new File(sbPath.toString());
		if (fFindDir.isFile()) {
			String profilePath = getProfilePathXML(fFindDir, profileName);
			return profilePath;

		} else {
			System.out.println("the profileRegistry.xml can not be found.");
			return null;
		}
	}

	public static ArrayList checkProfileXMLNew(File profileFile) {

		try {
			ArrayList dmProfileList = new ArrayList();
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
				String templateName = attrs.getNamedItem("template")
						.getNodeValue();

				if (templateName.endsWith(TYPE_DEPLOY_MANAGEMENT)
						|| templateName.endsWith(TYPE_DEPLOY_MANAGEMENT1)) {
					dmProfileList.add(profileNameTem);
				}

			}// end of if clause

			return dmProfileList;

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

	public static boolean checkProfileXMLTemp(File profileFile,
			String profileName) {

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
				String templateName = attrs.getNamedItem("template")
						.getNodeValue();
				if (profileNameTem.equals(profileName)) {
					if (templateName.endsWith(TYPE_DEPLOY_MANAGEMENT)
							|| templateName.endsWith(TYPE_DEPLOY_MANAGEMENT1)) {
						return true;
					}
				}
			}// end of if clause

			return false;

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
		return false;
	}

	/** return the soap port number of the specified DM profile */
	public static String getProfilePort(String profilePath) {
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

			log.info("getProfilePort path = " + path);

			// OS400_Enablement  bug fix for all platform, change dirs to dirs1.
			File[] dirs1 = new File(path).listFiles();
			if (dirs1 == null || dirs1.length == 0)
				return null;
			for (File curFile : dirs1) {
				if (!curFile.isDirectory())
					continue;
				File indexFile = new File(curFile.getAbsolutePath()
						+ File.separator + "serverindex.xml");

				log.info("getProfilePort file = " + indexFile.getAbsolutePath());
				
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

	public boolean checkProfileXML(File profileFile, String profileName) {

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
			// e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
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
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(profileFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("id");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				String id = entryNode.getFirstChild().getNodeValue();
				if (id.equals(TYPE_DEPLOY_MANAGEMENT)
						|| id.equals(TYPE_DEPLOY_MANAGEMENT1)) {
					return true;
				}
			}// end of if clause

			return false;

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

			VersionInfo versionInfo = new VersionInfo(vRelease, major, minor,
					maint, update);

			validator = new PortalValidation(versionInfo);
		}

		return validator;
	}

	public static boolean isWasLocValid(String wasLoc) {
		String wasVersion = "WAS.product";
		StringBuffer sbPath = new StringBuffer(normalizePath(wasLoc));
		sbPath.append(File.separator);
		sbPath.append("properties");
		sbPath.append(File.separator);
		sbPath.append("version");
		sbPath.append(File.separator);
		sbPath.append(wasVersion);
		File f = new File(sbPath.toString());
		return f.exists();
	}

	// OS400_Enablement
	//	Validate the WAS User Data dir on OS400, since it is not installed in the 
	//	WAS Install location by default. Here is an example.
	//		WAS Install Location: /QIBM/Proddata/Websphere/AppServer/V8/ND
	//		WAS UserData Location: /QIBM/Userdata/Websphere/AppServer/V8/ND
	public static boolean isWasUserDataLocValid(String wasUserHomeLoc) {
		String wasProfReg = "profileregistry.xml";
		StringBuffer sbPath = new StringBuffer(normalizePath(wasUserHomeLoc));
		sbPath.append(File.separator);
		sbPath.append("profileRegistry");
		sbPath.append(File.separator);
		sbPath.append(wasProfReg);
		File f = new File(sbPath.toString());
		return f.exists();
	}
	
	public static boolean isWasVersionValid(String wasLoc, String wasVersionPath)
			throws Exception {

		String wasVersion = "WAS.product";
		StringBuffer sbPath = new StringBuffer(normalizePath(wasLoc));
		sbPath.append(File.separator);
		sbPath.append("properties");
		sbPath.append(File.separator);
		sbPath.append("version");
		sbPath.append(File.separator);
		sbPath.append(wasVersion);
		ILogger log = IMLogger
				.getLogger(com.ibm.connections.install.DetectiveProfileAction.class);
		log.info("*** isWasVersionValid " + sbPath.toString());
		return checkWasVersion(new File(sbPath.toString()),
				getSupportVersion(wasVersionPath));

	}

	public static boolean getSystemAppsCheckResult(String wasLoc)
			throws Exception {
		StringBuffer sbPath = new StringBuffer(normalizePath(wasLoc));
		sbPath.append(File.separator);
		sbPath.append("systemApps");
		sbPath.append(File.separator);
		sbPath.append("SchedulerCalendars.ear");
		ILogger log = IMLogger
				.getLogger(com.ibm.connections.install.DetectiveProfileAction.class);
		log.info("getSystemAppsCheckResult = " + sbPath.toString());
		File file = new File(sbPath.toString());
		log.info("***" + file.exists());
		log.info("***" + file.isDirectory());
		if (file.exists() && file.isDirectory()) {
			return true;
		} else
			return false;
	}

	public static boolean getOauthProviderEarCheckResult(String wasLoc)
			throws Exception {
		StringBuffer sbPath = new StringBuffer(normalizePath(wasLoc));
		sbPath.append(File.separator);
		sbPath.append("installableApps");
		sbPath.append(File.separator);
		sbPath.append("WebSphereOauth20SP.ear");
		ILogger log = IMLogger
				.getLogger(com.ibm.connections.install.DetectiveProfileAction.class);
		log.info("getOauthProviderEarCheckResult = " + sbPath.toString());
		File file = new File(sbPath.toString());
		log.info("***" + file.exists());
		if (file.exists()) {
			return true;
		} else
			return false;
	}

	public static ArrayList getSupportVersion(String wasVersionPath)
			throws Exception {
		File file = new File(wasVersionPath);

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		ArrayList versionList = new ArrayList();
		while (line != null) {
			versionList.add(line);
			line = br.readLine();
			ILogger log = IMLogger
					.getLogger(com.ibm.connections.install.DetectiveProfileAction.class);
			log.info("*** line " + line);
		}

		return versionList;
	}

	private static boolean checkWasVersion(File wasVersionFile,
			ArrayList versionList) throws Exception {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					if (systemId.contains("product.dtd")) {
						return new InputSource(new StringReader(""));
					} else {
						return null;
					}
				}
			});
			if (!wasVersionFile.exists())
				return false;
			Document doc = docBuilder.parse(wasVersionFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("version");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				String version = entryNode.getFirstChild().getNodeValue();
				boolean isValid = compareVersion(versionList, version);
				return isValid;

			}// end of if clause

			return false;
		} catch (Exception e) {
			throw e;
		}

	}

	public static List<String> getWasVersion(String wasLoc,
			String wasVersionPath) throws Exception {
		String wasVersion = "WAS.product";
		StringBuffer sbPath = new StringBuffer(normalizePath(wasLoc));
		sbPath.append(File.separator);
		sbPath.append("properties");
		sbPath.append(File.separator);
		sbPath.append("version");
		sbPath.append(File.separator);
		sbPath.append(wasVersion);
		ILogger log = IMLogger
				.getLogger(com.ibm.connections.install.DetectiveProfileAction.class);
		log.info("*** isWasVersionValid " + sbPath.toString());
		File wasVersionFile = new File(sbPath.toString());

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					if (systemId.contains("product.dtd")) {
						return new InputSource(new StringReader(""));
					} else {
						return null;
					}
				}
			});
			if (!wasVersionFile.exists())
				return null;
			List<String> result = new ArrayList<String>();
			Document doc = docBuilder.parse(wasVersionFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList entrys = doc.getElementsByTagName("version");
			for (int i = 0; i < entrys.getLength(); i++) {
				Node entryNode = entrys.item(i);
				String version = entryNode.getFirstChild().getNodeValue();
				result.add(version);
			}// end of if clause
			return result;
		} catch (Exception e) {
			throw e;
		}

	}

	private static boolean compareVersion(ArrayList supportVersions,
			String userWasVersion) {

		for (int i = 0; i < supportVersions.size(); i++) {
			String versionScope = (String) supportVersions.get(i);
			String[] versions = versionScope.split(",");
			// just 2 version to compare
			boolean isValidVersion = isValidVersion(versions[0],
					userWasVersion, versions[1]);
			if (isValidVersion) {
				return true;
			}
		}
		return false;

	}

	private static boolean isValidVersion(String lowVersion,
			String userWasVersion, String highVersion) {

		if (lowVersion.equals(userWasVersion)) {
			return true;
		}

		if (highVersion.equals(userWasVersion)) {
			return true;
		}

		String[] lowVe = lowVersion.split("\\.");
		String[] userVe = userWasVersion.split("\\.");

		boolean isHight = false;

		for (int i = 0; i < lowVe.length; i++) {
			int supportVeInt = Integer.parseInt(lowVe[i]);
			int userVeInt = Integer.parseInt(userVe[i]);

			if (userVeInt > supportVeInt) {
				isHight = true;
				break;

			} else if (userVeInt < supportVeInt) {
				return false;
			}
		}

		if (highVersion.equals("~")) {
			return true;
		}

		boolean isLow = false;

		String[] highVe = highVersion.split("\\.");
		for (int i = 0; i < highVe.length; i++) {
			int supportVeInt = Integer.parseInt(highVe[i]);
			int userVeInt = Integer.parseInt(userVe[i]);

			if (userVeInt < supportVeInt) {
				isLow = true;
				break;
			} else if (userVeInt > supportVeInt) {
				return false;
			}
		}

		if (isHight && isLow) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		DetectiveProfileAction dp = new DetectiveProfileAction();
		System.out.println(dp
				.getDMProfile("C:\\Program Files\\IBM\\WebSphere\\AppServer1"));
	}

}
