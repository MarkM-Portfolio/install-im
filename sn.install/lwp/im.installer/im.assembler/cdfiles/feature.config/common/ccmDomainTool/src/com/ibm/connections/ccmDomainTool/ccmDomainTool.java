/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.ccmDomainTool;

import javax.security.auth.Subject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.security.DomainCombiner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.*;
import java.net.*;

import com.filenet.api.admin.CmDirectoryConfigurationConnections;
import com.filenet.api.action.Create;
import com.filenet.api.admin.AddOnInstallationRecord;
import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.FileStorageArea;
import com.filenet.api.admin.IsolatedRegion;
import com.filenet.api.admin.PEConnectionPoint;
import com.filenet.api.admin.AddOn;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionObject;
import com.filenet.api.admin.ServerCacheConfiguration;
import com.filenet.api.admin.StorageArea;
import com.filenet.api.admin.StoragePolicy;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.AddOnInstallationRecordList;
import com.filenet.api.collection.DirectoryConfigurationList;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.ServerCacheConfigurationList;
import com.filenet.api.collection.StringList;
import com.filenet.api.collection.StorageAreaSet;
import com.filenet.api.collection.SubsystemConfigurationList;
import com.filenet.api.collection.UserSet;
import com.filenet.api.constants.*;
import com.filenet.api.core.Annotation;
import com.filenet.api.core.Connection;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.Factory;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.security.Realm;
import com.filenet.api.security.User;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.filenet.apiimpl.property.PropertiesImpl;
import com.filenet.api.constants.SystemAddOnId;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ErrorRecord;
import com.filenet.api.exception.ExceptionCode;

import org.apache.commons.codec.binary.Base64;

public class ccmDomainTool {
	public static final String CMD_CREATEP8DOMAIN = "createP8Domain";
	public static final String CMD_REGISTERPEREGIONIDANDCONNECTIONPOINT = "registerPERegionIdAndConnectionPoint";
	public static final String CMD_CREATEOS = "createOS";
	public static final String CMD_GENERATE_SID = "generateSID";
	public static final String CMD_CCMUPDATE = "ccmUpdate";
	public static final String CMD_ACTIVITIY_STREAM = "activityStream";
	public static final String P8_REALM = "dc=collaborationrealm";

	public static final String LOG_NAME = "ccmDomainTool.log";

	public static Map<String, String> nodeHostnameMap = new HashMap<String, String>();
	public static Map<String, List<String>> nodeServernameMap = new HashMap<String, List<String>>();
	public static JavaPropertyGetter dm_propertyGetter;
	public static JavaPropertyGetter config_propertyGetter;
	public static JavaPropertyGetter homepage_updateConfig_propertyGetter;
	public static List<String> uriList = new ArrayList<String>();
	public static String groupName = "";
	public static String P8AdminUser = "";
	public static String password = "";
	public static String DCdisplayName = "";
	public static String P8DomainName = "";
	public static String CEWsiStanza = "";
	public static String P8OSDefaultAdmin = "";
	public static String numOS = "1";
	public static String displayName = "";
	public static String JNDIDataSource = "";
	public static String JNDIXADataSource = "";
	public static String symbolicName = "";
	public static String rootPath = "";
	public static String existingCCM = "";
	public static String cmd = "";
	public static String addonsURI = "";
	public static final String COLLABORATION_CONFIG_LOCAL_OBJECTSTORE_OBJECT_ID = "{AAAD8AA6-0590-4BB3-A426-CC9EFB84A73C}";

	public static void main(String[] args) {
		cleanLog();
		printVersion();
		writeOnlyLogln(args[0]);

		try {
			if (args[0].equalsIgnoreCase(CMD_CREATEP8DOMAIN)) {
				cmd = CMD_CREATEP8DOMAIN;
				createP8Domain();
			} else if (args[0]
					.equalsIgnoreCase(CMD_REGISTERPEREGIONIDANDCONNECTIONPOINT))
				registerPERegionIdAndConnectionPoint(args[1]);
			else if (args[0].equalsIgnoreCase(CMD_CREATEOS)) {
				cmd = CMD_CREATEOS;
				createOSreadPropFile();
			} else if (args[0].equalsIgnoreCase(CMD_GENERATE_SID)) {
				cmd = CMD_GENERATE_SID;
				generateUserSID();
			} else if (args[0].equalsIgnoreCase(CMD_CCMUPDATE)) {
				cmd = CMD_CCMUPDATE;
				ccmUpdate();
			} else if (args[0].equalsIgnoreCase(CMD_ACTIVITIY_STREAM)) {
				configureActivityStreamProducer();
			}
		} catch (EngineRuntimeException e) {
			try {
				System.out.println("Error: Unable to complete command - check log for further details: "+ LOG_NAME);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
				RandomAccessFile raf = new RandomAccessFile(LOG_NAME, "rw");
				raf.seek(raf.length());
				raf.write((sdf.format(new Date())).getBytes("ISO-8859-1"));
				ErrorRecord[] errors = e.getAsErrorStack().getErrorRecords();
				for (int i = 0; i < errors.length; i++) {
					raf.write(errors[i].toString().getBytes("ISO-8859-1"));
				}
				raf.write(System.getProperty( "line.separator" ).getBytes("ISO-8859-1"));
				raf.close();
			} catch (FileNotFoundException ex) {
				e.printStackTrace();
			} catch (IOException ex) {
				e.printStackTrace();
			} finally {
				System.exit(1);
			}
		}
	}

	public static void printVersion() {
		File file = new File("version_qualifier.txt");
		if (file.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String tempString = reader.readLine();
				if (tempString != null) {
					writeLogln("");
					writeLogln("*** ccmDomainTool Version: " + tempString + " ***");
					writeLogln("");
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
					}
				}
			}
		}
	}

	public static void createP8Domain() {
		File finishFile = new File("gcd_success");
		if (finishFile.exists()) {
			finishFile.delete();
		}
		loadCCMInfo();
		// test();
		Connection conn = null;
		for (int i = 0; i < uriList.size(); i++) {
			writeLogln("");
			writeLogln("*** Retrieving connection to the Content Platform Engine URI: "
					+ uriList.get(i) + " ...");
			try {
				conn = Factory.Connection.getConnection(uriList.get(i));
				if (uriList != null && uriList.size() > 0) {
					saveProperty(uriList.get(i));
				}
			} catch (Exception e) {
				continue;
			}
			break;
		}

		if (conn == null) {
			writeLogln("Cannot connect to any Content Platform Engine URI. Check whether the Connections Content Manager server has started.");
			System.exit(1);
		}

		writeLogln("*** Creating Domain and GCD ...");
		Subject subj = UserContext.createSubject(conn, P8AdminUser, password,
				null);
		UserContext uc = UserContext.get();
		uc.pushSubject(subj);

		// Create the restricted mode domain
		Domain dom = Factory.Domain.getInstance(conn, P8DomainName);
		dom.addPendingAction(new Create(ClassNames.DOMAIN, null, null, null,
				null, null));
		dom.set_Name(P8DomainName);
		try {
			dom.save(RefreshMode.REFRESH);
		} catch (EngineRuntimeException e) {
			if (e.getExceptionCode() == ExceptionCode.GCD_DOMAIN_ALREADY_EXISTS) {
				writeLogln("Warning - Domain already exists: "+ P8DomainName);
				writeLogln("Re-using the existing domain");
				dom.clearPendingActions();
			} else {
				throw e;
			}
		}

		// Create DirectoryConfiguration object
		CmDirectoryConfigurationConnections vmm = Factory.CmDirectoryConfigurationConnections
				.createInstance();
		if (vmm == null) {
			writeLogln("FileNet fix pack needs to be applied first!");
			System.exit(1);
		} else {
			vmm.set_DisplayName(DCdisplayName);
		}

		// Add DirectoryConfiguration objects
		DirectoryConfigurationList dirConfigs = Factory.DirectoryConfiguration
				.createList();
		dirConfigs.add(vmm);
		dom.set_DirectoryConfigurations(dirConfigs);
		dom.save(RefreshMode.REFRESH);

		setPermissions(dom, conn);

		
		writeLogln("*** Creating Domain and GCD finished.\n");
		try {
			finishFile.createNewFile();
		} catch (Exception e) {
			writeLogln("Error: Could not create file: gcd_success");
			writeLogln(e.getMessage());
			//e.printStackTrace();
		}
		
		// Register FNCS addons
		boolean runFNCSAddonManually = false;
		String logMsgManualInstall = null;
		String finallyMsg = null;
		try {
			File toolFile = new File(ccmDomainTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			File fncsDir = new File(toolFile.getParent()+File.separator+".."+File.separator+"FNCS");

			String fncsAddonsCommandString = null;
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				fncsAddonsCommandString = fncsDir.getPath() + File.separator + "addon.bat";
			} else {
				fncsAddonsCommandString = fncsDir.getCanonicalPath() + File.separator + "addon.sh";
			}

			File fncsAddonsCommand = new File(fncsAddonsCommandString);
			if(fncsAddonsCommand.exists() && !fncsAddonsCommand.isDirectory()) {
				writeLogln("*** Registering FNCS addons with the system ...");
				finallyMsg = "*** ... completed registering FNCS addons";
				logMsgManualInstall = "You must manually run the addon.bat | addon.sh script in the FNCS home \nto complete the FNCS addons registration";
				Properties uriProp = new Properties();
				uriProp.load(new FileInputStream(toolFile.getParent()+File.separator+"uri.txt"));

				List<String> cmd = new ArrayList<String>();
				if (osName.startsWith("Windows")) {
					cmd.add("CMD.EXE");
					cmd.add("/C");
					cmd.add("addon.bat");
				} else {
					// Using absolute path in case . (ie current working directory) is not set in PATH
					cmd.add(fncsDir.getCanonicalPath() + File.separator + "addon.sh");
				}
				cmd.add(fncsDir.getCanonicalPath() + File.separator + "CE_API");
				cmd.add(uriProp.getProperty("uri"));

				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.directory(fncsDir);
				pb.redirectErrorStream(true);
				Process process = pb.start();

				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);
				String line = null;
				boolean userPromptDone = false;
				boolean pwdPromptDone = false;
				while ((line = in.readLine()) != null) {
					if (!userPromptDone) { // do username
						writeOnlyLogln(line);
						out.println(P8AdminUser);
						writeOnlyLogln(P8AdminUser);
						userPromptDone = true;
						line = in.readLine(); // read in empty line
						continue;
					} else if (!pwdPromptDone) { // do password
						writeOnlyLogln(line);
						out.println(password);
						writeOnlyLogln("******");
						pwdPromptDone = true;
						continue;
					}
					writeLogln(line);
				}
				in.close();
				out.close();
				process.waitFor();
				process.destroy();
			}
			else {
			}
		} catch (Exception e) {
			writeLogln("Error: Encountered problem registering FNCS addons:");
			writeLogln(e.getMessage());
			runFNCSAddonManually = true;
		} finally {
			if (runFNCSAddonManually) {
				writeLogln("==========================================================================");
				writeLogln(logMsgManualInstall);
				writeLogln("==========================================================================");
			} else {
				if (finallyMsg != null) {
					writeLogln(finallyMsg);
				}
			}
		}
	}

	private static void setPermissions(Domain dom, Connection conn) {
		writeLogln("");
		writeLogln("==================================================");
		writeLogln("Highly recommended: provide a group which is granted administrative access to the FileNet system.");
		writeLogln("If you do not provide a group, it will be more difficult to recover administrative access to your system should the administrative user account be deleted or become inactive.");
		writeLogln("If you do not add a group now, you should do so after configuration is completed using the task 'Setting an LDAP group to be domain administrator instead of specific user' from the product documentation.");
		writeLogln("==================================================");
		writeLogln("");
		
		boolean validGroupName = false;
		do {
			groupName = getUserInputCanBeEmpty("Enter group name (you may leave blank):");
			if (groupName.equals("")) {
				validGroupName = true;
			} else {
				try {
					Factory.Group.fetchInstance(conn, groupName, null);
					validGroupName = true;
				} catch (EngineRuntimeException e) {
					// groupName does not exist
					writeLogln("Warning - unable to retrieve group name from directory");
				}
			}
		} while (!validGroupName);

		AccessPermissionList apl = Factory.AccessPermission.createList();
		apl.add(newPermission(SpecialPrincipal.AUTHENTICATED_USERS.getValue(),
				AccessType.ALLOW, 0, AccessLevel.USE_DOMAIN_AS_INT));

		apl.add(newPermission(P8AdminUser, AccessType.ALLOW, 0, AccessLevel.FULL_CONTROL_DOMAIN_AS_INT));
		if (groupName != null && !groupName.equals("")) {
			apl.add(newPermission(groupName, AccessType.ALLOW, 0, AccessLevel.FULL_CONTROL_DOMAIN_AS_INT));
		}

		dom.set_Permissions(apl);
		dom.save(RefreshMode.REFRESH);
	}

	private static AccessPermission newPermission(String grantee,
			AccessType type, int depth, int access) {
		AccessPermission perm = Factory.AccessPermission.createInstance();
		perm.set_GranteeName(grantee);
		perm.set_AccessType(type);
		perm.set_InheritableDepth(new Integer(depth));
		perm.set_AccessMask(new Integer(access));
		return perm;
	}

	private static void updateServerCache(Domain dom) {
		writeLogln("*** Updating Server Cache ...");
		dom.refresh();
		SubsystemConfigurationList scList = dom.get_SubsystemConfigurations();
		ServerCacheConfiguration scc = null;
		for (int i = 0; i < scList.size(); i++) {
			if (scList.get(i) instanceof ServerCacheConfiguration) {
				scc = (ServerCacheConfiguration) scList.get(i);
				if (scc.get_UserTokenCacheEntryTTL() < new Integer(3600)) {
					scc.set_UserTokenCacheEntryTTL(new Integer(3600));
				}
				if (scc.get_SubjectCacheEntryTTL() < new Integer(3600)) {
					scc.set_SubjectCacheEntryTTL(new Integer(3600));
				}
				break;
			}
		}
		dom.save(RefreshMode.REFRESH);
	}

	public static void createOSreadPropFile() {
		loadCCMInfo();
		// test();
		createOS(displayName, symbolicName, JNDIDataSource, JNDIXADataSource,
				P8AdminUser, password, null, P8OSDefaultAdmin, P8DomainName,
				CEWsiStanza);
	}

	public static void createOS(String displayName, String symbolicName,
			String JNDIDataSource, String JNDIXADataSource, String P8AdminUser,
			String passwd, String CEConnectionURI, String P8OSDefaultAdmin,
			String P8DomainName, String CEWsiStanza) {

		Connection conn = null;
		for (int i = 0; i < uriList.size(); i++) {
			writeLogln("");
			writeLogln("*** Retrieving connection to the Content Platform Engine URI: "
					+ uriList.get(i) + " ...");
			try {
				conn = Factory.Connection.getConnection(uriList.get(i));
				if (uriList != null && uriList.size() > 0) {
					saveProperty(uriList.get(i));
					addonsURI = uriList.get(i);
					addonsURI = addonsURI.replace("/wsi/FNCEWS40MTOM", "/dm/jsp/addons.jsp?submit=install");
					writeLogln("addonsURI = " + addonsURI);
				}
			} catch (Exception e) {
				continue;
			}
			break;
		}

		if (conn == null) {
			writeLogln("Cannot connect to any Content Platform Engine URI. Check whether the Connections Content Manager server has started.");
			System.exit(1);
		}

		Subject subj = UserContext.createSubject(conn, P8AdminUser, passwd, CEWsiStanza);
		UserContext uc = UserContext.get();
		uc.pushSubject(subj);

		writeLogln("");
		writeLogln("==================================================");
		writeLogln("Highly recommended: provide a group which is granted administrative access to the FileNet system.");
		writeLogln("If you do not provide a group, it will be more difficult to recover administrative access to your system should the administrative user account be deleted or become inactive.");
		writeLogln("If you do not add a group now, you should do so after configuration is completed using the task 'Setting an LDAP group to be domain administrator instead of specific user' from the product documentation.");
		writeLogln("==================================================");
		writeLogln("");
		
		boolean validGroupName = false;
		do {
			groupName = getUserInputCanBeEmpty("Enter group name (you may leave blank):");
			if (groupName.equals("")) {
				validGroupName = true;
			} else {
				try {
					Factory.Group.fetchInstance(conn, groupName, null);
					validGroupName = true;
				} catch (EngineRuntimeException e) {
					// groupName does not exist
					writeLogln("Warning - unable to retrieve group name from directory");
					writeOnlyLogln(e.getMessage());
				}
			}
		} while (!validGroupName);
		
		P8OSDefaultAdmin = P8OSDefaultAdmin +":"+ groupName;
		
		// Jay, defect 90218
		// String[] users = { "#AUTHENTICATED-USERS" };
		String[] users = P8OSDefaultAdmin.split(":");
		String[] admins = P8OSDefaultAdmin.split(":");

		writeLogln("");
		writeLogln("*** Creating Object Store (this will take some time) ...");

		Domain domain = Factory.Domain.fetchInstance(conn, P8DomainName, null);
		
		ObjectStore os = null;
		ObjectStoreSet oss = domain.get_ObjectStores();
		Iterator itr = oss.iterator();
		boolean osExists = false;
		while (itr.hasNext()) {
			os = (ObjectStore) itr.next();
			if (os.get_SymbolicName().equals(symbolicName)) {
				osExists = true;
				break;
			}
		}
		if (!osExists) {
			os = Factory.ObjectStore.createInstance(domain, admins, users);
			os.set_DisplayName(displayName);
			os.set_JNDIDataSource(JNDIDataSource);
			os.set_JNDIXADataSource(JNDIXADataSource);
			os.set_SymbolicName(symbolicName);
			os.save(RefreshMode.REFRESH);
		} else {
			writeLogln("Warning - Object store already exists: "+ symbolicName);
			writeLogln("Re-using the existing object store");
		}

		setFileStorage(os);
		update_permissions(os);
		
		// Register FNCS 2.0.3 and later addons
		boolean runFNCSAddonManually = false;
		String logMsgManualInstall = null;
		String finallyMsg = null;
		try {
			File toolFile = new File(ccmDomainTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			File fncsDir = new File(toolFile.getParent()+File.separator+".."+File.separator+"FNCS");

			String fncsAddonsCommandString = null;
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				fncsAddonsCommandString = fncsDir.getPath() + File.separator + "addon.bat";
			} else {
				fncsAddonsCommandString = fncsDir.getCanonicalPath() + File.separator + "addon.sh";
			}

			File fncsAddonsCommand = new File(fncsAddonsCommandString);
			if(fncsAddonsCommand.exists() && !fncsAddonsCommand.isDirectory()) {
			}
			else {
				writeLogln("*** Registering FNCS addons with the system ...");
				finallyMsg = "*** ... completed registering FNCS addons";
				logMsgManualInstall = "You must manually register FNCS addons in a browser with http://hostname:port/dm/jsp/addons.jsp";
				HttpURLConnection connection = null;			
				URL addonsUrl = new URL(addonsURI);
				connection = (HttpURLConnection) addonsUrl.openConnection();			
				String userPass = P8AdminUser + ":" + password;
				byte[] encodedBytes = Base64.encodeBase64(userPass.getBytes());
				connection.setRequestProperty("Authorization", "Basic "+new String(encodedBytes));

				connection.connect();
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					writeLogln("HTTP response code: " + connection.getResponseCode());
					throw new Exception("Error: Encountered problem registering FNCS addons - URL: " + addonsURI);
				}
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				System.out.println("============================="); 
				System.out.println("Contents of response");
				System.out.println("=============================");
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					if (line.contains("ERROR")) {
						throw new Exception(line);
					}
				}
			}
		} catch (MalformedURLException e) {
			writeLogln("Error: Encountered problem registering FNCS addons - malformed URL: " + addonsURI);
			writeLogln(e.getMessage());
			runFNCSAddonManually = true;
		} catch (Exception e) {
			writeLogln("Error: Encountered problem registering FNCS addons:");
			writeLogln(e.getMessage());
			runFNCSAddonManually = true;
		} finally {
			if (runFNCSAddonManually) {
				writeLogln("==========================================================================");
				writeLogln(logMsgManualInstall);
				writeLogln("==========================================================================");
			} else {
				if (finallyMsg != null) {
					writeLogln(finallyMsg);
				}
			}
		}
		
		installSocialCollaborationAddOns(os, true, true, true);

		configureActivityStreamProducer(os);

		ccmUpdateServerCache(domain, os);
		writeLogln("*** Creating Object Store finished.");
	}

	public static void setFileStorage(ObjectStore os) {
		writeLogln("*** Setting File Storage Area ...");
		if (rootPath == null || rootPath.length() < 0) {
			writeLogln("Cannot find the Connections Content Manager shared content path");
			System.exit(1);
		}
		StorageArea sa = null;
		StorageAreaSet sas = os.get_StorageAreas();
		Iterator itr = sas.iterator();
		boolean saExists = false;
		while (itr.hasNext()) {
			sa = (StorageArea) itr.next();
			if (sa.getClassName().equals("FileStorageArea") && sa.get_DisplayName().equals("ICObjectStore")) {
				saExists = true;
				break;
			}
		}

		if (saExists) {
			writeLogln("File Storage Area already exists: "+((FileStorageArea)sa).get_RootDirectoryPath());
			writeLogln("Skipping setting File Storage Area");
			return;
		}

		FileStorageArea myStorageArea = Factory.FileStorageArea.createInstance(os, "FileStorageArea");
		myStorageArea.set_DisplayName("ICObjectStore");
		myStorageArea.set_DescriptiveText("ICObjectStore");
		myStorageArea.set_DirectoryStructure(DirectoryStructure.DIRECTORY_STRUCTURE_LARGE);
		myStorageArea.set_RootDirectoryPath(rootPath);
		myStorageArea.save(RefreshMode.REFRESH);

		StoragePolicy myStoragePolicy = Factory.StoragePolicy
				.createInstance(os);
		com.filenet.api.property.Properties properties = myStoragePolicy
				.getProperties();
		properties.putValue("DisplayName", "ICObjectStore_sp");
		properties.putValue("DescriptiveText", "ICObjectStore_sp");
		String filter = myStorageArea.get_Id().toString();
		writeLogln(filter);
		myStoragePolicy.set_FilterExpression("id=" + filter);
		myStoragePolicy.save(RefreshMode.REFRESH);

		// FileStorageArea myStorageArea =
		// Factory.FileStorageArea.fetchInstance(os, new
		// Id("{599DE6A1-C7F1-4344-83ED-5B1F2AD80502}"), null);
		// StoragePolicy myStoragePolicy =
		// Factory.StoragePolicy.fetchInstance(os, new
		// Id("{EA4692FB-DAAB-4965-A72F-4E296DC125A9}"), null);
		try {
			// Get the ClassDefinition
			ClassDefinition cd = Factory.ClassDefinition.fetchInstance(os,
					"Document", null);

			// Find the PropertyDefinition within the ClassDefinition, for the
			// StoragePolicy
			PropertyDefinitionObject pdoSP = (PropertyDefinitionObject) locatePropertyDefinition(
					cd, PropertyNames.STORAGE_POLICY);

			// Make my storage policy the default for this class
			pdoSP.set_PropertyDefaultObject(myStoragePolicy);

			// Find the PropertyDefinition within the ClassDefinition, for the
			// StorageArea
			PropertyDefinitionObject pdoSA = (PropertyDefinitionObject) locatePropertyDefinition(
					cd, PropertyNames.STORAGE_AREA);

			// Make my storage policy the default for this class
			pdoSA.set_PropertyDefaultObject(myStorageArea);

			cd.save(RefreshMode.REFRESH);
		} catch (Exception e) {
			writeLogln("Error in setting File Storage Area: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Locate a PropertyDefinition with a given SymbolicName.
	 */
	private static PropertyDefinition locatePropertyDefinition(
			ClassDefinition cd, String symbolicName) throws Exception {
		PropertyDefinition pd = null;
		PropertyDefinitionList propDefList = cd.get_PropertyDefinitions();
		Iterator iter = propDefList.iterator();
		boolean found = false;

		while (iter.hasNext()) {
			pd = (PropertyDefinition) iter.next();
			if (pd.get_SymbolicName().equals(symbolicName)) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new Exception("Could not locate PropertyDefinition '"
					+ symbolicName + "'");
		}
		return pd;

	}

	private static ObjectStore getObjectStore() {
		String hostname = getUserInput("Enter the Content Platform Engine (CPE) host name:");
		String port = getUserInput("Enter the CPE port:");
		String objStoreName = getUserInput("Enter the object store name:", "ICObjectStore");
		P8AdminUser = getUserInput("Use the domain administrator user name (The user name entered in the Connections WAS configuration install panel) in the below inputs:");
		password = getUserInput("Enter domain administrator user password (leave blank, if it is a group user):", true);
		uriList.add(getURI(hostname, port));

		Connection conn = null;
		for (int i = 0; i < uriList.size(); i++) {
			writeLogln("\n");
			writeLogln("Retrieving connection to the CE URI: "
					+ uriList.get(i) + " ...");
			try {
				conn = Factory.Connection.getConnection(uriList.get(i));
				if (uriList != null && uriList.size() > 0) {
					saveProperty(uriList.get(i));
				}
			} catch (Exception e) {
				continue;
			}
			break;
		}

		if (conn == null) {
			writeLogln("Cannot connect to any Content Platform Engine URI. Check whether the Connections Content Manager server has started.");
			System.exit(1);
		}

		Subject subj = UserContext.createSubject(conn, P8AdminUser, password,
				null);
		UserContext uc = UserContext.get();
		uc.pushSubject(subj);

		Domain dom = Factory.Domain.fetchInstance(conn, null, null);
		ObjectStore os = Factory.ObjectStore.fetchInstance(dom, objStoreName,
				null);
		return os;
	}

	/*
	 * Grant #AUTHENTICATED_USERS use access to the object store
	 */
	public static void adjustObjectStorePermissions(ObjectStore os) {
		AccessPermissionList apl = null;
		apl = os.get_Permissions();

		AccessPermission ap = Factory.AccessPermission.createInstance();
		ap.set_AccessType(AccessType.ALLOW);
		ap.set_AccessMask(new Integer(AccessLevel.USE_OBJECT_STORE_AS_INT));
		ap.set_InheritableDepth(new Integer(0));
		ap.set_GranteeName("#AUTHENTICATED-USERS");
		apl.add(ap);
		os.set_Permissions(apl);
		os.save(RefreshMode.REFRESH);
	}

	// Add Default Instance Permissions to the specified ClassDefinition
	// grants the permissions specified in the mask, to #AUTHENTICATED_USER
	public static void addAUDIPermissionToClass(ObjectStore os,
			String className, int mask) {
		AccessPermissionList apl = null;
		ClassDefinition cd = Factory.ClassDefinition.fetchInstance(os,
				className, null);

		try {
			apl = cd.get_DefaultInstancePermissions();
		} catch (Exception e) {
		}

		if (apl == null)
			apl = Factory.AccessPermission.createList();

		AccessPermission ap = Factory.AccessPermission.createInstance();
		ap.set_AccessType(AccessType.ALLOW);
		ap.set_AccessMask(new Integer(mask));
		ap.set_InheritableDepth(new Integer(-1));
		ap.set_GranteeName("#AUTHENTICATED-USERS");

		apl.add(ap);
		cd.set_DefaultInstancePermissions(apl);
		cd.save(RefreshMode.REFRESH);
	}

	// Add permissions to the specified ClassDefinition
	// grants the permissions specified in the mask, to #AUTHENTICATED_USER
	public static void addAUPermissionToClass(ObjectStore os, String className,
			int mask) {
		AccessPermissionList apl = null;
		ClassDefinition cd = Factory.ClassDefinition.fetchInstance(os,
				className, null);

		try {
			apl = cd.get_Permissions();
		} catch (Exception e) {
		}

		if (apl == null)
			apl = Factory.AccessPermission.createList();

		AccessPermission ap = Factory.AccessPermission.createInstance();
		ap.set_AccessType(AccessType.ALLOW);
		ap.set_AccessMask(new Integer(mask));
		ap.set_InheritableDepth(new Integer(-1));
		ap.set_GranteeName("#AUTHENTICATED-USERS");
		apl.add(ap);
		cd.set_Permissions(apl);
		cd.save(RefreshMode.REFRESH);
	}

	/**
	 * Update permissions on system classes as required by Connections
	 * environments
	 */
	private static boolean update_permissions(ObjectStore os) {
		writeLogln("*** Updating Object Store permissions");
		adjustObjectStorePermissions(os);

		// Grant CreateInstance permissions for the base business object classes
		// to all users (#AUTHENTICATED_USER)
		int mask = AccessRight.READ_AS_INT + AccessRight.CREATE_INSTANCE_AS_INT;
		writeLogln("*** Updating permissions on business object Class Definition");
		addAUPermissionToClass(os, ClassNames.DOCUMENT, mask);
		addAUPermissionToClass(os, ClassNames.FOLDER, mask);
		addAUPermissionToClass(os, ClassNames.CUSTOM_OBJECT, mask);
		addAUPermissionToClass(os,
				ClassNames.REFERENTIAL_CONTAINMENT_RELATIONSHIP, mask);
		addAUPermissionToClass(os,
				ClassNames.DYNAMIC_REFERENTIAL_CONTAINMENT_RELATIONSHIP, mask);
		addAUPermissionToClass(os, ClassNames.CHOICE_LIST, mask);
		addAUPermissionToClass(os, ClassNames.CM_TASK, mask);
		addAUPermissionToClass(os, ClassNames.CM_ABSTRACT_PERSISTABLE, mask);
		addAUPermissionToClass(os, ClassNames.CM_ABSTRACT_QUEUE_ENTRY, mask);
		addAUPermissionToClass(os, ClassNames.CM_RECOVERY_BIN, mask);
		addAUPermissionToClass(os, ClassNames.CM_RECOVERY_ITEM, mask);

		// Grant Read access to ChoiceList instances, to all users
		// (#AUTHENTICATED_USERS)
		mask = AccessRight.READ_AS_INT;
		writeLogln("*** Updating DefaultInstance permissions on ChoiceList Class Definition");
		addAUDIPermissionToClass(os, ClassNames.CHOICE_LIST, mask);

		// And finally, grant Read access to each of the eight PropertyTemplate
		// class definitions so that Collaboration Services will be able to
		// retrieve property templates
		writeLogln("*** Updating DefaultInstance permissions on PropertyTemplate Class Definition");
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_BINARY, mask);
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_BOOLEAN, mask);
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_DATE_TIME,
				mask);
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_FLOAT64, mask);
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_ID, mask);
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_INTEGER32,
				mask);
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_OBJECT, mask);
		addAUDIPermissionToClass(os, ClassNames.PROPERTY_TEMPLATE_STRING, mask);

		mask = AccessRight.READ_AS_INT;
		addAUDIPermissionToClass(os, ClassNames.CM_TASK_RELATIONSHIP, mask);
		writeLogln("*** All permissions updated successfully!");
		return true;
	}

	/**
	 * Installs the addOns necessary for Social Collaboration functionality.
	 * Prior to this, the application has obtained user input for the three
	 * optional addOns - denoted by the boolean parameters.
	 * 
	 * @param os
	 * @param installIndexingAddOn
	 * @param installNotificationAddOn
	 * @param installDocumentReviewAddOn
	 */
	private static void installSocialCollaborationAddOns(ObjectStore os,
			boolean installIndexingAddOn, boolean installNotificationAddOn,
			boolean installDocumentReviewAddOn) {
		installAddOn(os, SystemAddOnId.CONTENT_ENGINE_BASE);
		installAddOn(os, SystemAddOnId.APPLICATION_BASE);
		installAddOn(os, SystemAddOnId.TEAMSPACE_EXTENSIONS);
		installAddOn(os, SystemAddOnId.CUSTOM_ROLE_EXTENSIONS);
		installAddOn(os, SystemAddOnId.SOCIAL_COLLABORATION_ROLE_EXTENSIONS);
		installAddOn(os, SystemAddOnId.SOCIAL_COLLABORATION_BASE_EXTENSIONS);

		if (installIndexingAddOn) {
			installAddOn(
					os,
					SystemAddOnId.SOCIAL_COLLABORATION_SEARCH_INDEXING_EXTENSIONS);
		}

		if (installNotificationAddOn) {
			installAddOn(os,
					SystemAddOnId.SOCIAL_COLLABORATION_NOTIFICATION_EXTENSIONS);
		}

		if (installDocumentReviewAddOn) {
			installAddOn(
					os,
					SystemAddOnId.SOCIAL_COLLABORATION_DOCUMENT_REVIEW_EXTENSIONS);
		}

		// Installing the IBM FileNet Services for Lotus Quickr 1.1 Extensions
		// AddOn
		Id addonId = new Id("{E81AC848-0B89-4AF7-8836-51A4CD095709}");
		installAddOn(os, addonId);

		// Installing the IBM FileNet Services for Lotus Quickr 1.1 Supplemental
		// Metadata AddOn
		addonId = new Id("{E81AC848-0B89-4AF7-8836-51A4CD095711}");
		installAddOn(os, addonId);
	}

	/**
	 * Installs the AddOn associated with addOnId into the corresponding
	 * ObjectStore.
	 * 
	 * NOTE: If the output of the AddOn's DisplayName is not required, then
	 * using Factory.AddOn.getInstance(domain, aoId) is preferred over
	 * fetchInstance since getInstance will prevent a server round trip.
	 * 
	 * @param os
	 * @param addOnId
	 */
	private static void installAddOn(ObjectStore os, Id addOnId) {
		AddOn ao = Factory.AddOn.fetchInstance(os.get_Domain(), addOnId, null);

		if (ao != null) {
			writeLog("*** Installing the "+ ao.get_DisplayName() +" AddOn ... ");
			if (os.isAddOnInstalled(addOnId)) {
				writeLogln("AddOn already installed; skipping");
				return;
			}
			os.installAddOn(ao);
			os.save(RefreshMode.REFRESH);
			writeLogln("done");
		}
	}

	private static void ccmUpdateServerCache(Domain dom, ObjectStore os) {
		writeLogln("Start updating CCM ...");
		updateServerCache(dom);
		Id addonId = SystemAddOnId.USER_IDENTITY_MAPPING_EXTENSIONS;
		installAddOn(os, addonId);
		writeLogln("CCM update finished.");
	}

	private static void ccmUpdate() {
		if (isExistingCCMDeployment()) {
			String hostname = getUserInput("Enter the Content Platform Engine (CPE) host name:");
			String port = getUserInput("Enter the CPE port:");
			String objStoreName = getUserInput("Enter the object store name:");
			P8AdminUser = getUserInput("Use the domain administrator user name (The user name entered in the Connections WAS configuration install panel) or group name  in the below inputs:");
			password = getUserInput("Enter domain administrator user password:", true);
			uriList.add(getURI(hostname, port));

			Connection conn = getConnection();
			Domain dom = Factory.Domain.fetchInstance(conn, null, null);
			ObjectStore os = Factory.ObjectStore.fetchInstance(dom,
					objStoreName, null);
			ccmUpdateServerCache(dom, os);
		} else {
			loadCCMInfo();
			Connection conn = getConnection();
			Domain dom = Factory.Domain.fetchInstance(conn, null, null);
			ObjectStore os = Factory.ObjectStore.fetchInstance(dom,
					"ICObjectStore", null);
			ccmUpdateServerCache(dom, os);
		}
	}

	public static void listAddOns() {
		uriList.add("http://9.119.44.89:9081/wsi/FNCEWS40MTOM");
		Connection conn = null;
		for (int i = 0; i < uriList.size(); i++) {
			writeLogln("\n");
			writeLogln("Retrieving connection to the Content Platform Engine URI: "
					+ uriList.get(i) + " ...");
			try {
				conn = Factory.Connection.getConnection(uriList.get(i));
				if (uriList != null && uriList.size() > 0) {
					saveProperty(uriList.get(i));
				}
			} catch (Exception e) {
				continue;
			}
			break;
		}

		if (conn == null) {
			writeLogln("Cannot connect to any Content Platform Engine URI. Check whether the Connections Content Manager server has started.");
			System.exit(1);
		}
		writeLogln("Creating Domain and GCD ...");
		Subject subj = UserContext.createSubject(conn, "wpsadmin", "wpsadmin",
				null);
		UserContext uc = UserContext.get();
		uc.pushSubject(subj);

		Domain dom = Factory.Domain.fetchInstance(conn, null, null);
		ObjectStore os = Factory.ObjectStore.fetchInstance(dom,
				"ICObjectStore", null);

		AddOnInstallationRecordList list = os.get_AddOnInstallationRecords();
		for (int i = 0; i < list.size(); i++) {
			AddOnInstallationRecord record = (AddOnInstallationRecord) list
					.get(i);
			AddOn ad = record.get_AddOn();
			writeLogln(ad.get_DisplayName() + " - " + ad.get_Id());
		}
	}

	private static Connection getConnection() {
		Connection conn = null;
		for (int i = 0; i < uriList.size(); i++) {
			writeLogln("\n");
			writeLogln("*** Retrieving connection to the Content Platform Engine URI: "
					+ uriList.get(i) + " ...");
			try {
				conn = Factory.Connection.getConnection(uriList.get(i));
				if (uriList != null && uriList.size() > 0) {
					saveProperty(uriList.get(i));
				}
			} catch (Exception e) {
				continue;
			}
			break;
		}

		if (conn == null) {
			writeLogln("Cannot connect to any Content Platform Engine URI. Check whether the Connections Content Manager server has started.");
			System.exit(1);
		}

		Subject subj = UserContext.createSubject(conn, P8AdminUser, password,
				null);
		UserContext uc = UserContext.get();
		uc.pushSubject(subj);

		Domain dom = Factory.Domain.fetchInstance(conn, null, null);
		ObjectStore os = Factory.ObjectStore.fetchInstance(dom,
				"ICObjectStore", null);

		return conn;
	}

	private static Connection getConnectionExistingCCM(String objStoreName) {
		Connection conn = null;
		for (int i = 0; i < uriList.size(); i++) {
			writeLogln("\n");
			writeLogln("*** Retrieving connection to the Content Platform Engine URI: "
					+ uriList.get(i) + " ...");
			try {
				conn = Factory.Connection.getConnection(uriList.get(i));
				if (uriList != null && uriList.size() > 0) {
					saveProperty(uriList.get(i));
				}
			} catch (Exception e) {
				continue;
			}
			break;
		}

		if (conn == null) {
			writeLogln("Cannot connect to any Content Platform Engine URI. Check whether the Connections Content Manager server has started.");
			System.exit(1);
		}

		Subject subj = UserContext.createSubject(conn, P8AdminUser, password,
				null);
		UserContext uc = UserContext.get();
		uc.pushSubject(subj);

		Domain dom = Factory.Domain.fetchInstance(conn, null, null);
		ObjectStore os = Factory.ObjectStore.fetchInstance(dom, objStoreName,
				null);

		return conn;
	}

	public static void generateUserSID() {
		writeLogln("*** Generate User SID ...");
		Connection conn = null;
		if (isExistingCCMDeployment()) {
			String hostname = getUserInput("Enter the Content Platform Engine (CPE) host name:");
			String port = getUserInput("Enter the CPE port:");
			String objStoreName = getUserInput("Enter the object store name:");
			P8AdminUser = getUserInput("Enter the domain administrator user ID:");
			password = getUserInput("Enter domain administrator password:", true);
			uriList.add(getURI(hostname, port));
			conn = getConnectionExistingCCM(objStoreName);
		} else {
			loadCCMInfo();
			conn = getConnection();
		}
		
		String userName = getUserInput("Enter the user ID to generate the SID:");
		User user = Factory.User.fetchInstance(conn, userName, null);

		if (user != null) {
			writeLogln("SID: " + user.get_Id());
		} else {
			writeLogln("Cannot find any users matching given input: "+userName);
		}
	}

	public static void registerPERegionIdAndConnectionPoint(String path) {
		Connection conn = null;

		String P8AdminUser = "";
		String passwd = "";
		String CEConnectionURI = "";
		String DNSName = "";
		String BrokerPort = "";
		int intBrokerPort = 32776;
		String RegionNumber = "";
		int intRegionNumber = 1;
		String ConnectionPointName = "";
		String ConnectionPointDesc = "";
		String P8DomainName = "";
		String CEWsiStanza = "";

		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(path));
			P8AdminUser = properties.getProperty("P8ADMINUSER");
			passwd = properties.getProperty("PASSWD");
			CEConnectionURI = properties.getProperty("CECONNECTIONURI");
			DNSName = properties.getProperty("PE_DNSNAME");
			BrokerPort = properties.getProperty("PE_BROKERPORT");
			intBrokerPort = Integer.parseInt(BrokerPort);

			writeLogln("PE_BROKERPORT = " + Integer.toString(intBrokerPort));
			RegionNumber = properties.getProperty("PE_REGIONNUMBER");
			intRegionNumber = Integer.parseInt(RegionNumber);

			writeLogln("PE_REGIONNUMBER = " + Integer.toString(intRegionNumber));
			ConnectionPointName = properties.getProperty("PE_CONNPTNAME");
			ConnectionPointDesc = properties
					.getProperty("PE_CONNPTDESCRIPTION");
			P8DomainName = properties.getProperty("P8DOMAINNAME");
			CEWsiStanza = properties.getProperty("CEWSISTANZA");
			conn = Factory.Connection.getConnection(CEConnectionURI);

		} catch (IOException e) {
			writeLogln("Error: " + e.getLocalizedMessage());
		}

		Subject subj = UserContext.createSubject(conn, P8AdminUser, passwd,
				CEWsiStanza);
		UserContext uc = UserContext.get();
		uc.pushSubject(subj);

		Domain dom = Factory.Domain.getInstance(conn, P8DomainName);

		Id id = Id.createId();
		IsolatedRegion ir = Factory.IsolatedRegion.createInstance(dom, id);
		ir.set_DNSName(DNSName);
		ir.set_BrokerPort(intBrokerPort);
		ir.set_IsolatedRegionNumber(intRegionNumber);
		ir.set_RegionPassword(passwd.getBytes());
		ir.save(RefreshMode.REFRESH);

		id = Id.createId();
		PEConnectionPoint cp = Factory.PEConnectionPoint
				.createInstance(dom, id);
		cp.set_Name(ConnectionPointName);
		cp.set_DescriptiveText(ConnectionPointDesc);
		cp.set_IsolatedRegion(ir);
		cp.save(RefreshMode.REFRESH);
	}

	public static void callSleep(String durationSec) {
		int numOfMilSec = 0;
		int durationInt = 20;
		int numOfSec = 0;
		try {
			numOfSec = Integer.parseInt(durationSec);
			if (numOfSec < 0 || numOfSec > 60)
				numOfSec = 20;
		} catch (Exception e) {
			numOfSec = 20;
		}
		try {
			writeLogln("sleep: " + Integer.toString(numOfSec));
			numOfMilSec = numOfSec * 1000;
			Thread.sleep(numOfMilSec);
		} catch (InterruptedException e) {
			writeLogln("Exception in sleep: " + e.toString());
		}
	}

	private static void loadCCMInfo2() {
		P8AdminUser = "wasadmin";
		password = "passw0rd";
		DCdisplayName = "HCL Connections Directory Service";
		P8DomainName = "ICDomain";
		CEWsiStanza = "FileNetP8WSI";
		P8OSDefaultAdmin = P8AdminUser;
		displayName = "ICObjectStore";
		JNDIDataSource = "FNOSDS";
		JNDIXADataSource = "FNOSDSXA";
		symbolicName = "ICObjectStore";
		uriList.add("http://fn.cn.ibm.com:9080/wsi/FNCEWS40MTOM");
	}

	public static void saveProperty(String uri) {
		try {
			writeLogln("Saving to uri.txt: "+uri);
			File f = new File("uri.txt");
			if (f.exists()) {
				f.delete();
			} else {
				f.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write("uri=" + uri);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isExistingCCMDeployment() {
		String configPath = "../config.properties";
		File config_file = new File(configPath);
		if (config_file.exists()) {
			writeLogln(config_file.getAbsolutePath());
			config_propertyGetter = new JavaPropertyGetter(config_file);
			existingCCM = config_propertyGetter
					.getProperty("ccm.existingDeployment");
			if (existingCCM != null)
				writeLogln("Is existing CCM deployment: " + existingCCM);
			if (existingCCM != null && existingCCM.equalsIgnoreCase("true"))
				return true;
			else
				return false;
		} else {
			writeLogln("Cannot find the <Connections_Home>/config.properties.");
			System.exit(1);
		}
		return false;
	}

	// validate the directory
	private static boolean detectDirectoryWritable(String dir) {
		boolean flag = true;
		File file = new File(dir);
		if (file.exists() && !isDirectoryWritable(file))
			return false;

		List lDirs = new ArrayList();
		String curFolder = dir;
		while (null != curFolder && !file.exists()) {
			lDirs.add(curFolder);
			curFolder = file.getParent();
			if (curFolder != null)
				file = new File(curFolder);
		}

		// push the non-exist directory into the list
		// try to see if exception to create those directory that doesn't
		// exist
		for (int i = lDirs.size() - 1; i >= 0; i--) {
			File tempFile = new File((String) lDirs.get(i));
			flag = tempFile.mkdir();
			if (flag == false)
				break;
		}

		// delete those directories which are created temoporarily
		for (int i = 0; i < lDirs.size(); i++) {
			File tempFile = new File((String) lDirs.get(i));
			flag = tempFile.delete();
			if (flag == false)
				break;
		}

		return flag;
	}

	private static boolean isDirectoryWritable(File file) {
		String filePath = file.getAbsolutePath();
		return isDirectoryWritable(filePath);
	}

	private static boolean isDirectoryWritable(String dirpath) {
		String tempPath = dirpath + File.separator
				+ (int) (Math.random() * 10000000D) + ".tmp";
		File tempFile = new File(tempPath);
		return tempFile.mkdir() && tempFile.delete();
	}

	private static List loadCCMInfo() {
		try {
			writeLogln("*** Loading Connections Content Manager information ...");
			writeLogln("*** Reading WAS information from <Connections_Home>/config.properties");
			String configPath = "../config.properties";
			File config_file = new File(configPath);

			if (config_file.exists()) {
				config_propertyGetter = new JavaPropertyGetter(config_file);
				String profilePath = config_propertyGetter
						.getProperty("WasUserHome");
				String firstNode = config_propertyGetter
						.getProperty("ccm.FirstNodeName");
				String secondaryNodes = config_propertyGetter
						.getProperty("ccm.SecondaryNodeNames");
				String dmUserid = config_propertyGetter
						.getProperty("ccm.adminuser.id");
				rootPath = config_propertyGetter
						.getProperty("ccm.contentStore.shared.path");
				String dmHostname = config_propertyGetter
						.getProperty("WasRemoteHostName");
				String soapPort = config_propertyGetter
						.getProperty("WasSoapPort");

				existingCCM = config_propertyGetter.getProperty("ccm.existingDeployment");

				if (rootPath != null
						&& rootPath.indexOf("${file.separator}") != -1) {
					rootPath = rootPath.replaceAll("\\$\\{file.separator\\}",
							"/");
					rootPath = rootPath.replaceAll("\\\\\\\\", "/");
				}

				writeLogln("");
				writeLogln("WasUserHome: " + profilePath);
				writeLogln("WasRemoteHostName: " + dmHostname);
				writeLogln("WasSoapPort: " + soapPort);
				writeLogln("ccm.FirstNodeName: " + firstNode);
				writeLogln("ccm.SecondaryNodeNames: " + secondaryNodes);
				writeLogln("ccm.adminuser.id: " + dmUserid);
				writeLogln("ccm.contentStore.shared.path: " + rootPath);
				writeLogln("");

				if (cmd.equalsIgnoreCase(CMD_CREATEOS)
						&& !detectDirectoryWritable(rootPath)) {
					writeLogln("You do not have write access to the specified path "
							+ rootPath
							+ ". This path is the shared path configured for content store. Write access to this path is required when creating an object store since documents will be stored in subdirectories of this path.");
					System.exit(1);
				}

				P8AdminUser = getUserInput("Enter the Deployment Manager administrator user ID (from the Connections WAS configuration install panel):", dmUserid);
				password = getUserInput("Enter the Deployment Manager administrator password:", true);
				
				// Re-generate dminfo.properties to obtain latest details
				File dm_file = new File(profilePath + "/bin/dminfo.properties");
				String pyPath = new File("wkplc_GetDMInfo.py").getAbsolutePath();
				
				// For debugging during dev:
				// writeLog("profilePath = " + profilePath);
				// writeLog("pyPath = " + pyPath);
				// writeLog("P8AdminUser = " + P8AdminUser);
				// writeLog("password = " + password);
				// writeLog("dmHostname = " + dmHostname);
				// writeLog("soapPort = " + soapPort);
				
				String doRegen = "";
				writeLogln("");
				writeLogln("*** Confirm information to be used in creating the FileNet domain and object store in:");
				writeLogln(dm_file.getCanonicalPath());
				while (!(doRegen.equalsIgnoreCase("Y") || doRegen.equalsIgnoreCase("S") || doRegen.equalsIgnoreCase("E"))) {
					doRegen = getUserInput("Regenerate dminfo.properties file - type Y to continue; S to skip regeneration; or E to exit the tool and edit the properties file above with the correct information?", "Y");
					if (doRegen.equalsIgnoreCase("E")) {
						System.exit(2);  // Not an error exit, but not completed process
					}
				}
				if (doRegen.equalsIgnoreCase("Y")) {
					writeLogln("");
					writeLogln("*** Re-generating the dminfo.properties file ...");
					writeLogln("Deleting file: "+ dm_file.getCanonicalPath());
					if ((dm_file.exists()) && (!dm_file.delete())) {
						writeLogln("Failed to delete the dminfo.properties file");
						System.exit(1);
					}
				}
				
				if (doRegen.equalsIgnoreCase("Y") || (doRegen.equalsIgnoreCase("N") && !dm_file.exists())) {
					writeLogln("");
					writeLogln("*** Creating dminfo.properties file ...");
					writeLogln(dm_file.getCanonicalPath());
					String retVal = getDMInfo(profilePath, pyPath, P8AdminUser, password, dmHostname, soapPort);
					
					// Check return code
					if (retVal != null && !retVal.equals("0")) {
						if (retVal.equals("105")) {
							writeLogln("Insufficient credentials to perform operation");
							System.exit(1);
						}
						writeLogln("Encountered problems creating dminfo.properties file; return code: "+ retVal);
						System.exit(1);
					}
				}

				writeLogln("");
				writeLogln("*** Reading Deployment Manager information from "+ dm_file.getCanonicalPath());
				if (dm_file.exists()) {
					dm_propertyGetter = new JavaPropertyGetter(dm_file);

					String nodeListStr = parseToString((ArrayList) detectNodes());
					String nodesHostnameStr = parseNodeHostNameMapToString((ArrayList) detectNodes());

					if (nodeHostnameMap.size() == 0) {
						nodeServernameMap.clear();
						if (nodesHostnameStr != null && nodeListStr != null
								&& !nodeListStr.trim().equals("")) {
							String[] hostnames = nodesHostnameStr.split(",");
							String[] nodenames = nodeListStr.split(",");
							for (int i = 0; i < nodenames.length; i++) {
								if (!nodeHostnameMap.containsKey(nodenames[i])) {
									nodeHostnameMap.put(nodenames[i],
											hostnames[i]);
									writeLogln("nodename: " + nodenames[i]);
									writeLogln("hostname: " + hostnames[i]);
								}
							}
						}
					}

					if (firstNode != null && firstNode.length() > 0) {
						writeLogln("firstnode: " + firstNode);
						String hostname = nodeHostnameMap.get(firstNode);
						String server = config_propertyGetter.getProperty("ccm."+ firstNode +".ServerName");
						writeLogln("");
						writeLogln("host name: " + hostname);
						writeLogln("server: " + server);
						String port = getPort(profilePath, server);
						writeLogln("port: " + port);
						writeLogln("");
						if (hostname != null && port != null) {
							writeLogln("Generated URI:");
							uriList.add(getURI(hostname, port));
						}
						writeLogln("");
					}
					if (secondaryNodes != null && secondaryNodes.length() > 0) {
						StringTokenizer tokenizer = new StringTokenizer(
								secondaryNodes, ";");
						while (tokenizer.hasMoreTokens()) {
							String node = tokenizer.nextToken();
							String hostname = nodeHostnameMap.get(node);
							String port = getPort(
									profilePath,
									config_propertyGetter.getProperty("ccm."+ node +".ServerName"));
							if (hostname != null && port != null) {
								uriList.add(getURI(hostname, port));
							}
						}
					}

					DCdisplayName = "HCL Connections Directory Service";
					P8DomainName = "ICDomain";
					CEWsiStanza = "FileNetP8WSI";
					P8OSDefaultAdmin = P8AdminUser;
					displayName = "ICObjectStore";
					JNDIDataSource = "FNOSDS";
					JNDIXADataSource = "FNOSDSXA";
					symbolicName = "ICObjectStore";
					writeLogln("");
					writeLogln("==================================================");
					writeLogln("Domain name                        : "
							+ P8DomainName);
					writeLogln("Domain administrator user          : "
							+ P8AdminUser);
					writeLogln("Directory service provider name    : "
							+ DCdisplayName);
					writeLogln("Content Platform Engine webservice : "
							+ CEWsiStanza);
					writeLogln("JNDI data source                   : "
							+ JNDIDataSource);
					writeLogln("JNDI XA data source                : "
							+ JNDIXADataSource);
					writeLogln("Object store display name          : "
							+ displayName);
					writeLogln("Object store name                  : "
							+ symbolicName);
					writeLogln("Object store administrator user    : "
							+ P8OSDefaultAdmin);
					writeLogln("File Storage Area root path        : "
							+ rootPath);
					writeLogln("Possible Content Platform Engine Connection URIs: ");
					for (int i = 0; i < uriList.size(); i++) {
						writeLogln("    " + uriList.get(i));
					}
					writeLogln("==================================================");
				} else {
					writeLogln("File does not exist: "+ profilePath +"/bin/dminfo.properties");
					System.exit(1);
				}
			} else {
				writeLogln("Cannot find the <Connections_Home>/config.properties.");
				System.exit(1);
			}
		} catch (Exception e) {
			writeLogln("Error: " + e.getLocalizedMessage());
		}

		return uriList;
	}

	public static void test() {
		DCdisplayName = "HCL Connections Directory Service";
		P8DomainName = "ICDomain";
		CEWsiStanza = "FileNetP8WSI";
		P8OSDefaultAdmin = P8AdminUser;
		displayName = "ICObjectStore";
		JNDIDataSource = "FNOSDS";
		JNDIXADataSource = "FNOSDSXA";
		symbolicName = "ICObjectStore";
		uriList.add("http://fn.cn.ibm.com:9080/wsi/FNCEWS40MTOM");
		P8AdminUser = "wasadmin";
		password = "passw0rd";
	}

	public static String getUserInput(String msg) {
		return getUserInput(msg, false);
	}
	
	public static String getUserInput(String msg, boolean maskInput) {
		String userInput = null;
		if (msg != null && msg.length() != 0)
			writeLogln(msg);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			userInput = br.readLine();
			// block any null input
			while (userInput == null || userInput.trim().length() == 0) {
				writeLogln("Invalid input.");
				if (msg != null && msg.length() != 0)
					writeLogln(msg);
				userInput = br.readLine();
			}
			writeOnlyLogln(maskInput ? "******": userInput);
			return userInput.trim();
		} catch (IOException e) {
			writeLogln("Error: " + e.getLocalizedMessage());
			System.exit(1);
		}
		return userInput;
	}

	public static String getUserInputCanBeEmpty(String msg) {
		String userInput = null;
		if (msg != null && msg.length() != 0)
			writeLogln(msg);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			userInput = br.readLine();
			if (userInput == null || userInput.trim().length() == 0) {
				userInput = "";
			}
			writeOnlyLogln(userInput);
			return userInput.trim();
		} catch (IOException e) {
			writeLogln("Error: " + e.getLocalizedMessage());
			System.exit(1);
		}
		return userInput;
	}

	public static String getUserInput(String msg, String defaultValue) {
		String userInput = null;
		if (msg != null && msg.length() != 0 && defaultValue != null
				&& defaultValue.length() != 0)
			writeLogln(msg + " [" + defaultValue + "]");
		else
			writeLogln(msg);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			userInput = br.readLine();
			if ((userInput == null || userInput.trim().length() == 0)
					&& defaultValue != null) {
				writeOnlyLogln(defaultValue);
				return defaultValue;
			}
			// block any null input
			while (userInput == null || userInput.trim().length() == 0) {
				writeLogln("Invalid input.");
				if (msg != null && msg.length() != 0)
					writeLogln(msg);
				userInput = br.readLine();
			}
			writeOnlyLogln(userInput);
			return userInput.trim();
		} catch (IOException e) {
			writeLogln("Error: " + e.getLocalizedMessage());
			System.exit(1);
		}
		return userInput;
	}

	private static String getURI(String hostname, String port) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(hostname);
		sb.append(":");
		sb.append(port);
		sb.append("/wsi/FNCEWS40MTOM");
		writeLogln(sb.toString());
		return sb.toString();
	}

	private static String parseToString(ArrayList target) {

		if (target != null) {
			Iterator it = target.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String content = (String) it.next();
				result.append(content + ",");
			}
			return result.toString();
		}
		return null;
	}

	private static String parseNodeHostNameMapToString(ArrayList target) {
		if (target != null) {
			Iterator it = target.iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				String content = (String) it.next();
				try {
					result.append(detectNodeHostName(content) + ",");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result.toString();
		}
		return null;
	}

	public static List detectNodes() {
		ArrayList list = new ArrayList();
		String dmnodeStr = dm_propertyGetter.getProperty("dm.nodename");
		String nodesStr = dm_propertyGetter.getProperty("dm.nodes");
		String[] nodes = nodesStr.split(",");
		for (int i = 0; i < nodes.length; i++) {
			if (!dmnodeStr.equals(nodes[i])) {
				list.add(nodes[i]);
			}
		}
		return list;
	}

	public static Map detectServers() {
		Map result = new HashMap();
		String nodeServersStr = dm_propertyGetter
				.getProperty("dm.nodes.servername");
		Map map = FieldMappingParser.parseSemicolon(nodeServersStr);
		Set nodeSet = map.keySet();
		Iterator it = nodeSet.iterator();
		String node, server = new String();
		while (it.hasNext()) {
			server = new String();
			Object o = it.next();
			if (o != null) {
				node = (String) o;
				String servers = (String) map.get(o);
				if (servers != null && servers.trim().length() > 0) {
					String serverList[] = servers.split(",");
					for (int i = 0; i < serverList.length; i++) {
						int index = serverList[i].indexOf("(");
						if (!serverList[i].substring(0, index)
								.equalsIgnoreCase("nodeagent")) {
							server += serverList[i].substring(0, index) + ",";
						}
					}
				}

				if (server.trim().length() > 0) {
					if (server.endsWith(","))
						server = server.substring(0, server.length() - 1);
					result.put(node, server);
				}
			}
		}
		return result;
	}

	public static String detectNodeHostName(String nodeName) {
		String nodes = dm_propertyGetter.getProperty("dm.nodes.hostname");
		Map map = FieldMappingParser.parseSemicolon(nodes);
		Set nodesSet = map.keySet();
		Iterator it = nodesSet.iterator();
		while (it.hasNext()) {
			String keyValue = (String) it.next();
			if (nodeName.equalsIgnoreCase(keyValue)) {
				return (String) map.get(keyValue);
			}
		}
		return "";
	}

	public static String getPort(String profilePath, String servername) {

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

			dirs = new File(path).listFiles();
			if (dirs == null || dirs.length == 0)
				return null;
			for (File curFile : dirs) {
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
					for (int j = 0; j < entrys.getLength(); j++) {
						Node entryNode = entrys.item(j);
						NamedNodeMap attrs = entryNode.getAttributes();
						String serverName = attrs.getNamedItem("serverName")
								.getNodeValue();
						if (null == servername || servername.equals("")) {
							return null;
						}

						if (!serverName.equals(servername))
							continue;

						NodeList endPointsEntrys = entryNode.getChildNodes();
						if (endPointsEntrys == null
								|| endPointsEntrys.getLength() == 0)
							break;
						String id = "";

						for (int i = 0; i < endPointsEntrys.getLength(); i++) {
							Node curEntry = endPointsEntrys.item(i);
							if (null == curEntry) {
								continue;
							}
							attrs = curEntry.getAttributes();
							if (null == attrs
									|| null == attrs
											.getNamedItem("endPointName")) {
								continue;
							} else if (!attrs.getNamedItem("endPointName")
									.getNodeValue().equals("WC_defaulthost")) {
								continue;
							}
							id = attrs.getNamedItem("xmi:id").getNodeValue();

							NodeList pointsEntrys = curEntry.getChildNodes();
							Node portEntry = pointsEntrys.item(1);
							attrs = portEntry.getAttributes();

							return attrs.getNamedItem("port").getNodeValue();
						}
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

	public static void parseMapping(String slaveFieldMapping) {
		nodeServernameMap.clear();
		StringTokenizer tokenizer = new StringTokenizer(slaveFieldMapping, ";");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			StringTokenizer tokenizer2 = new StringTokenizer(token, ":");
			if (tokenizer2.hasMoreTokens()) {
				String nodeName = tokenizer2.nextToken().trim();
				writeLogln("nodeName: " + nodeName);
				if (nodeName != null && nodeName.length() > 0) {
					if (tokenizer2.hasMoreTokens()) {
						String slaveFields = tokenizer2.nextToken();
						StringTokenizer tokenizer3 = new StringTokenizer(
								slaveFields, ",");
						List<String> serverNameList = new ArrayList<String>();
						while (tokenizer3.hasMoreTokens()) {
							String slaveField = tokenizer3.nextToken();
							serverNameList.add(slaveField);
						}

						if (!nodeServernameMap.containsKey(nodeName))
							nodeServernameMap.put(nodeName, serverNameList);
						else {
							((List<String>) nodeServernameMap.get(nodeName))
									.addAll(serverNameList);
						}

					}
				}
			}
		}
	}

	public static String transferPath(String path) {
		return path.replace("\\", "/");
	}

	public static String getDMInfo(String profilePath, String pyPath,
			String dmUserid, String dmPassword, String hostname, String port)
			throws Exception {

		try {
			String profileBinPath = profilePath + "/bin";
			String dmInfoProperties = transferPath(profileBinPath + "/dminfo.properties");
			File file = new File(dmInfoProperties);
			if (file.exists())
				file.delete();

			File pyfile = new File(pyPath);
			if (!pyfile.exists()) {
				writeLogln("Cannot find the <Connections_Home>/ccmDomainTool/wkplc_GetDMInfo.py");
				System.exit(1);
			}

			String extension = null;
			String command = null;
			String commands[] = null;
			ExternalCommandAction eca = new ExternalCommandAction();
			eca.setWorkingDirectory(profilePath);

			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				pyPath = "\"" + pyPath + "\"";
				command = "\"" + profilePath
						+ "/bin/wsadmin.bat\" -conntype SOAP -host " + hostname
						+ " -port " + port + " -lang jython -username \""
						+ dmUserid + "\" -password " + dmPassword + " -f "
						+ pyPath + " \"" + dmInfoProperties + "\"";
				eca.setCommand(command);

				// for log
				String temp = "\"" + profilePath
						+ "/bin/wsadmin.bat\" -conntype SOAP -host " + hostname
						+ " -port " + port + " -lang jython -username \""
						+ dmUserid + "\" -password PASSWORD_REMOVED -f "
						+ pyPath + " \"" + dmInfoProperties + "\"";
				writeLogln("Running DM Info command: " + temp);
			} else if (osName.toLowerCase().startsWith("os/400")) {
				// OS400_Enablement
				commands = new String[16];
				commands[0] = profilePath + "/bin/wsadmin";
				commands[1] = "-conntype";
				commands[2] = "SOAP";
				commands[3] = "-host";
				commands[4] = hostname;
				commands[5] = "-port";
				commands[6] = port;
				commands[7] = "-lang";
				commands[8] = "jython";
				commands[9] = "-username";
				commands[10] = dmUserid;
				commands[11] = "-password";
				commands[12] = dmPassword;
				commands[13] = "-f";
				commands[14] = pyPath;
				commands[15] = dmInfoProperties;
				eca.setArrayCmds(true);
				eca.setCommands(commands);

				// for log
				String[] commandTemp = (String[]) commands.clone();
				commandTemp[12] = "PASSWORD_REMOVED";
				writeLogln("Running DM Info command: " + commandTemp.toString());

			} else {
				commands = new String[16];
				commands[0] = profilePath + "/bin/wsadmin.sh";
				commands[1] = "-conntype";
				commands[2] = "SOAP";
				commands[3] = "-host";
				commands[4] = hostname;
				commands[5] = "-port";
				commands[6] = port;
				commands[7] = "-lang";
				commands[8] = "jython";
				commands[9] = "-username";
				commands[10] = dmUserid;
				commands[11] = "-password";
				commands[12] = dmPassword;
				commands[13] = "-f";
				commands[14] = pyPath;
				commands[15] = dmInfoProperties;
				eca.setArrayCmds(true);
				eca.setCommands(commands);

				// for log
				writeLog("Running DM Info command: ");
				for (int i = 0; i < commands.length; i++) {
					if (i == 12) {
						writeLog("PASSWORD_REMOVED" + " ");
					} else {
						writeLog(commands[i] + " ");
					}
				}
				writeLogln("");
			}

			eca.execute();

			return eca.getReturnCode();
		} catch (Exception e) {
			throw e;
		}
	}

	public static void configureActivityStreamProducer() {
		ObjectStore os = getObjectStore();
		configureActivityStreamProducer(os);
	}

	private static String removeLastSlash(String url) {
		String realURL = null;
		if (url.lastIndexOf("/") == url.length() - 1) {
			realURL = url.substring(0, url.lastIndexOf("/"));
		} else {
			realURL = url;
		}
		return realURL;
	}

	public static void configureActivityStreamProducer(ObjectStore os) {
		writeLogln("*** Configure collaborative features (including Activity Stream, Download Ignored and Anonymous Users) ...");
		File config_file = new File("../config.properties");
		if (!config_file.exists()) {
			writeLogln("Cannot find <Connections_Home>/config.properties");
			System.exit(1);
		}
		config_propertyGetter = new JavaPropertyGetter(config_file);

		String ccmAdmin = config_propertyGetter.getProperty("ccm.adminuser.id");
		String anonUser = config_propertyGetter.getProperty("ccm.anonymous.user");

		File homepage_file = new File(
				"../../../homepage/homepage/homepage/updateConfig.properties");
		String homepage_ssl = null;
		if (homepage_file.exists()) {
			homepage_updateConfig_propertyGetter = new JavaPropertyGetter(
					homepage_file);
			homepage_ssl = homepage_updateConfig_propertyGetter
					.getProperty("homepage.ssl.href");
		}

		writeLogln("Important: Must use HTTPS. This should use the host and port of your HTTP server.");
		writeLogln("If you want to test Activity Stream without the HTTP server, this must be the port of the application server hosting the Connections News application, eg, https://webserver.example.com or https://connections.example.com:9443");
		String clbActivityStreamHTTPEndpointURL = getUserInput("Activity Stream HTTP endpoint URL:", homepage_ssl);
		if (clbActivityStreamHTTPEndpointURL != null)
			removeLastSlash(clbActivityStreamHTTPEndpointURL);
			
//		writeLogln("");
//		String ignoredActivityStreamUsers = getUserInputCanBeEmpty("Enter Activity Stream ignored user IDs (as comma separated list eg: user1, user2). It may be left empty:");
		String ignoredActivityStreamUsers = "";
		
//		writeLogln("");
//		String ignoredDownloadUsers = getUserInput("Enter the download count ignored user IDs (as comma separated list; this list must include the user used by Connections to index Connections Content Manager libraries into Connections search. By default, for a new FileNet deployment, this user is the same as the Connections administrative user):", ccmAdmin);
		String ignoredDownloadUsers = ccmAdmin;
		
//		writeLogln("");
//		String anonymousDownloadUsers = getUserInput("Enter the download count anonymous user IDs (as comma separated list):", anonUser);
//		writeLogln("");
		String anonymousDownloadUsers = anonUser == null ? "" : anonUser;

		try {
		createCollaborationConfiguration(os,
				clbActivityStreamHTTPEndpointURL,
				"{ecm_files}",
				"{connections}/resources/web/com.ibm.social.ee/ConnectionsEE.xml",
				stringToArrays(ignoredActivityStreamUsers),
				stringToArrays(ignoredDownloadUsers), stringToArrays(anonymousDownloadUsers));
		} catch (Exception e) {
			writeLogln("Error in configuring collaborative features: "+ e.getMessage());
			System.exit(1);
		}
		writeLogln("*** Configured collaborative features successfully");
	}

	public static String[] stringToArrays(String s) {
		ArrayList list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		if (tokenizer.countTokens() == 0 && s.length() > 0)
			list.add(s);
		while (tokenizer.hasMoreTokens()) {
			String tmp = tokenizer.nextToken();
			list.add(tmp);
		}
		String[] arrays = new String[list.size()];
		for (int i = 0; i < arrays.length; i++) {
			arrays[i] = (String) list.get(i);
		}
		return arrays;
	}

	public static void createCollaborationConfiguration(ObjectStore os,
			String clbActivityStreamHTTPEndpointURL,
			String clbActivityStreamRetrievalURL,
			String clbActivityStreamGadgetURL,
			String[] ignoredActivityStreamUsers,
			String[] ignoredDownloadUsers, String[] anonymousDownloadUsers)
			throws Exception {
		String sid = null;
		CustomObject cc = Factory.CustomObject.fetchInstance(os,
				new Id(COLLABORATION_CONFIG_LOCAL_OBJECTSTORE_OBJECT_ID), null);

		writeLogln("Activity Stream Retrieval URL: "+ clbActivityStreamRetrievalURL);
		writeLogln("Activity Stream HTTP Endpoint URL: "+ clbActivityStreamHTTPEndpointURL);
		writeLogln("Activity Stream Gadget URL: "+ clbActivityStreamGadgetURL);
		
		cc.getProperties().putValue("ClbActivityStreamHTTPEndpointURL", clbActivityStreamHTTPEndpointURL);
		cc.getProperties().putValue("ClbActivityStreamRetrievalURL", clbActivityStreamRetrievalURL);
		cc.getProperties().putValue("ClbActivityStreamGadgetURL", clbActivityStreamGadgetURL);

		StringList ignoredActivityStreamUserIds = Factory.StringList.createList();
		writeLogln("Activity Stream Ignored User IDs: ");
		for (int i = 0; i < ignoredActivityStreamUsers.length; i++) {
			sid = getUserSID(os, ignoredActivityStreamUsers[i]);
			if (sid != null) {
				ignoredActivityStreamUserIds.add(sid);
				writeLog(ignoredActivityStreamUsers[i]);
				if (i < ignoredActivityStreamUsers.length - 1)
					writeLog(",");
			}
		}
		cc.getProperties().putValue("ClbActivityStreamIgnoredUserIds", ignoredActivityStreamUserIds);
		writeLogln("");
		
		StringList ignoredDownloadUserIds = Factory.StringList.createList();
		writeLogln("Download Count Ignored User IDs: ");
		for (int i = 0; i < ignoredDownloadUsers.length; i++) {
			sid = getUserSID(os, ignoredDownloadUsers[i]);
			if (sid != null) {
				ignoredDownloadUserIds.add(sid);
				writeLog(ignoredDownloadUsers[i]);
				if (i < ignoredDownloadUsers.length - 1)
					writeLog(",");
			}
		}
		cc.getProperties().putValue("ClbDownloadCountIgnoredUserIds", ignoredDownloadUserIds);
		writeLogln("");
		
		StringList anonymousDownloadUserIds = Factory.StringList.createList();
		writeLogln("Download Count Anonymous User IDs: ");
		for (int i = 0; i < anonymousDownloadUsers.length; i++) {
			sid = getUserSID(os, anonymousDownloadUsers[i]);
			if (sid != null) {
				anonymousDownloadUserIds.add(sid);
				writeLog(anonymousDownloadUsers[i]);
				if (i < anonymousDownloadUsers.length - 1)
					writeLog(",");
			}
		}
		cc.getProperties().putValue("ClbDownloadCountAnonymousUserIds", anonymousDownloadUserIds);
		writeLogln("");

		cc.save(RefreshMode.REFRESH);
	}

	public static String getUserSID(ObjectStore os, String userName) {
		User user = Factory.User.fetchInstance(os.getConnection(), userName, null);
		if (user == null) {
			writeOnlyLogln("Cannot find any users matching given input: "+userName);
			return null;
		} else {
			return user.get_Id();
		}
	}

	public static void cleanLog() {
		try {
			File log = new File(LOG_NAME);
			// clean log, if it is more than 10mb
			if (log.exists() && log.length() > 10485760l)
				log.delete();
			else
				log.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLog(String content) {
		System.out.print(content);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
			RandomAccessFile raf = new RandomAccessFile(LOG_NAME, "rw");
			String contents = sdf.format(new Date()) + content;
			raf.seek(raf.length());
			raf.write(contents.getBytes("ISO-8859-1"));
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLogln(String content) {
		System.out.println(content);
		writeOnlyLogln(content);
	}
	
	public static void writeOnlyLogln(String content) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
			RandomAccessFile raf = new RandomAccessFile(LOG_NAME, "rw");
			String contents = sdf.format(new Date()) + content + "\r\n";
			raf.seek(raf.length());
			raf.write(contents.getBytes("ISO-8859-1"));
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
