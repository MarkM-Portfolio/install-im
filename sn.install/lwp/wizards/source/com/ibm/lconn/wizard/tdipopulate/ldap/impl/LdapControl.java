/* ***************************************************************** */
/*                                                                   */
/* HCL Technologies Limited                                          */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited 2006, 2020                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.tdipopulate.ldap.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.exceptions.ErrorCode;
import com.ibm.lconn.wizard.common.exceptions.LdapException;
import com.ibm.lconn.wizard.common.exceptions.LdapOperationException;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.tdipopulate.ldap.GeneralLdapControl;
import com.ibm.lconn.wizard.tdipopulate.ldap.ObjectClass;
import com.ibm.lconn.wizard.tdipopulate.ldap.ServerConfig;

/**
 * 
 * @author Bai Jian Su (Subaij@cn.ibm.com)
 */
public class LdapControl extends GeneralLdapControl {
	public static final String PROVIDE_ROOT_ENTRIES_PROPERTY = "LdapControl.provideRootEntries";
	
	// whether to provide root entries if no namingContexts in root DSE are found, default is false
	public static boolean provideRootEntriesIfNoNamingContextsFound = Boolean.parseBoolean(System.getProperty(PROVIDE_ROOT_ENTRIES_PROPERTY, "false"));
	private static final Logger logger = LogUtil.getLogger(LdapControl.class);
	// value for IBM Directory Server
	public static String VENDOR_IBM_DIRECTORY_SERVER = Constants.LDAP_TIVOLI;
	// public static String VENDOR_IBM_DIRECTORY_SERVER = "1.3.18.0.2";
	// value for IBM Lotus Domino LDAP Server
	public static String VENDOR_IBM_DOMINO_LDAP = Constants.LDAP_DOMINO;
	// public static String VENDOR_IBM_DOMINO_LDAP = "2.16.840.1.113678.2.2.2";
	// value for Microsoft Active Directory
	public static String VENDOR_MICROSOFT_ACTIVE_DIRECTORY = Constants.LDAP_AD;
	public static String VENDOR_MICROSOFT_ACTIVE_DIRECTORY_ADAM = Constants.LDAP_ADAM;
	// public static String VENDOR_MICROSOFT_ACTIVE_DIRECTORY =
	// "1.2.840.113556.1.4.800";
	public static String VENDOR_SUN_ONE_DIRECTORY = Constants.LDAP_ORACLE;
	public static String VENDOR_NOVEL_DIRECTORY_SERVIICES = Constants.LDAP_NDS;

	private final static String ACTIVE_DIRECTORY_OID = "1.2.840.113556.1.4.800";
	private final static String ACTIVE_DIRECTORY_ADAM_OID = "1.2.840.113556.1.4.1851";
	private final static String Vendor_Name_IBM = "International Business Machines (IBM)";
	private final static String Vendor_Name_Lotus = "IBM Lotus Software";
	private final static String Vendor_Name_Hcl = "HCL Software";
	private final static String Vendor_Name_Sun = "Sun";
	private final static String Vender_Name_Novell = "Novell";
	private final static String Vendor_Name_Oracle = "Oracle";
	// vendor name for current ctx
	private String vendorName = null;

	public static String ID_OBJECT_CLASS = "objectclass";
	public static final String AD_BUILTIN_AUX_CLASS = "SecurityPrincipal";
	public static final String AD_USER_CLASS = "User";
	private static int countLimit = 1000;
	// parser for this namespace
	private NameParser parser = null;
	
	static {
		try {
			countLimit = Integer.parseInt(System.getProperty(Constants.LDAP_SEARCH_LIMIT));
		} catch(NumberFormatException e) {
			// default to 1000
			countLimit = 1000;
		}
	}
	
	public LdapControl(ServerConfig sc) {
		super(sc);
	}

	private static final Pattern[] IgnoredNamingContexts = {
			// AD
			Pattern.compile("^CN=CONFIGURATION(,.*)?", Pattern.CASE_INSENSITIVE), // AD
			Pattern.compile("^CN=SCHEMA(,.*)?", Pattern.CASE_INSENSITIVE), // AD,
			// ITDS
			Pattern.compile("^CN=LOCALHOST(,.*)?", Pattern.CASE_INSENSITIVE), // ITDS
			Pattern.compile("^CN=PWDPOLICY(,.*)?", Pattern.CASE_INSENSITIVE), // ITDS
			Pattern.compile("^CN=IBMPOLICIES(,.*)?", Pattern.CASE_INSENSITIVE), // ITDS
			Pattern.compile("^CN=CHANGELOG(,.*)?", Pattern.CASE_INSENSITIVE), // ITDS,
			Pattern.compile("^CN=ROOT(,.*)?", Pattern.CASE_INSENSITIVE), // ITDS,
			// Sun
			Pattern.compile("^CN=CONFIG(,.*)?", Pattern.CASE_INSENSITIVE), // Sun
			Pattern.compile("^CN=MONITOR(,.*)?", Pattern.CASE_INSENSITIVE), // Sun
			// NDS
			Pattern.compile("^CN=SECURITY(,.*)?", Pattern.CASE_INSENSITIVE) // NDS
	};

	/**
	 * Detect the base DN from LDAP server.
	 * 
	 * @return
	 * @throws LdapException
	 */
	public List<String> getBaseDNs() throws LdapException {

		List<String> baseDNs = new ArrayList<String>();
		final String attrNames[] = new String[] { "defaultNamingContext",
				"namingContexts" };
		Attributes attrs = getAttributes("", attrNames);
		try {
			// Add defaultNamingContext (Active Directory schema) first if it
			// exists
			String defaultNamingContext = null;
			Attribute a = attrs.get("defaultNamingContext");
			if (a != null) {
				defaultNamingContext = (String) a.get();
				baseDNs.add(defaultNamingContext);
			}
			// Add remaining naming namingContext values
			a = attrs.get("namingContexts");
			if (a != null) {
				NamingEnumeration<?> vals = a.getAll();
				while (vals.hasMore()) {
					String val = (String) vals.next();

					// Domino implements this odd value to indicate the root!
					if (val.length() > 0 && val.charAt(0) == '\0') {
						val = "";
						// Don't duplicate defaultNamingContext
					} else if (val.equalsIgnoreCase(defaultNamingContext)) {
						val = null;
						// Omit naming contexts matching NotSuggestedBaseDNs
						// patterns
					} else {
						for (int i = 0; i < IgnoredNamingContexts.length; i++) {
							Matcher m = IgnoredNamingContexts[i].matcher(val);
							if (m.matches()) {
								val = null;
								break;
							}
						}
					}

					// Add non-empty val to baseDNs vector
					if (val != null && !"".equals(val)) {
						baseDNs.add(val);
					}
				}
				vals.close(); 
			}
			
			// if no namingContexts is found from root DSE, trying to get root entries
			if(baseDNs.size() == 0 && provideRootEntriesIfNoNamingContextsFound) {
				logger.log(Level.WARNING, "tdipopulate.warning.no_namingcontexts_found");
				List<String> rootEntries = getRootEntries();
				for(String entry : rootEntries) {
					for (int i = 0; i < IgnoredNamingContexts.length; i++) {
						Matcher m = IgnoredNamingContexts[i].matcher(entry);
						if (m.matches()) {
							entry = null;
							break;
						}
					}
					if(entry != null) {
						baseDNs.add(entry);
					}
				}
			}
		} catch (NamingException e) {
			logger.log(Level.SEVERE,
					"tdipopulate.severe.ldap.operation.get_basedn_failed");
			throw new LdapOperationException(e,
					ErrorCode.ERROR_LDAP_OPERATION_GETBASEDN,
					"err.ldap.operation.getbaseDN", sc.getHostURL());
		}
		return baseDNs;
	}

	/**
	 * detect the type of LDAP server
	 * 
	 * @return LDAP Server type string
	 * @throws LdapException
	 */
	public String getVendorName() throws LdapException {
		String vendor = null;

		try {
			// Get root DSE for baseDNs values
			final String attrNames[] = new String[] { "vendorName",
					"supportedCapabilities" };
			Attributes attrs = getAttributes("", attrNames);
			// Determine vendor via vendorName
			Attribute att = attrs.get("vendorName");
			if (null != att) {
				vendor = (String) att.get();
				if (Vendor_Name_IBM.equalsIgnoreCase(vendor)) {
					vendor = VENDOR_IBM_DIRECTORY_SERVER;
				} else if (Vendor_Name_Lotus.equalsIgnoreCase(vendor) || Vendor_Name_Hcl.equalsIgnoreCase(vendor)) {
					vendor = VENDOR_IBM_DOMINO_LDAP;
				} else if ((vendor.toLowerCase().indexOf(Vendor_Name_Sun.toLowerCase()) != -1)||(vendor.toLowerCase().indexOf(Vendor_Name_Oracle.toLowerCase()) != -1)) {
					vendor = VENDOR_SUN_ONE_DIRECTORY;
				} else if (vendor.indexOf(Vender_Name_Novell) != -1) {
					vendor = VENDOR_NOVEL_DIRECTORY_SERVIICES;
				}
				// Determine vendor via supportedCapabillities
			} else {
				att = attrs.get("supportedCapabilities");
				NamingEnumeration<?> caps = att.getAll();
				while (caps.hasMore()) {
					String cap = (String) caps.next();
					if (ACTIVE_DIRECTORY_OID.equalsIgnoreCase(cap)) {
						vendor = VENDOR_MICROSOFT_ACTIVE_DIRECTORY;
						break;
					} else if (ACTIVE_DIRECTORY_ADAM_OID.equalsIgnoreCase(cap)) {
						vendor = VENDOR_MICROSOFT_ACTIVE_DIRECTORY_ADAM;
						break;
					}
				}
				caps.close(); // Prevents Notes "Java Agent: Error cleaning up
				// agent threads"
			}
		} catch (NamingException e) {
			logger.log(Level.SEVERE,
					"tdipopulate.severe.ldap.operation.get_vendorname_failed");
			throw new LdapOperationException(e,
					ErrorCode.ERROR_LDAP_OPERATION_GETVENDORNAME,
					"err.ldap.operation.getVendorName", sc.getHostURL());
		}
		// update the value to field vendorName
		vendorName = vendor;
		logger.log(Level.INFO, "**********vendor name*******"+vendor);
		return vendor;
	}

	/**
	 * retrieve all the object from the given ojbectclass
	 * 
	 * @param objectClass
	 * @return A list contains all the object
	 * @throws LdapException
	 */
	public List<Attributes> getObjectFromObjectClass(String objectClass)
			throws LdapException {
		return this.getObjectFromObjectClass("", objectClass);
	}

	/**
	 * retrieve all the object belong to the DN and objectclass
	 * 
	 * @param dnName
	 * @param objectClass
	 * @return A list contains all the object
	 * @throws LdapException
	 */
	public List<Attributes> getObjectFromObjectClass(String dnName, String objectClass)
			throws LdapException {
		String filter = null;
		if (null == objectClass || "".equalsIgnoreCase(objectClass))
			filter = "(objectclass=*)";
		else
			filter = "(objectclass=" + objectClass + ")";
		return this.getObjects(dnName, filter);
	}

	/**
	 * Retrieve the objects that satisfy the given DN and filter.
	 * 
	 * @param dnName
	 * @param filter
	 * @return
	 * @throws LdapException
	 */
	public List<Attributes> getObjects(String dnName, String filter) throws LdapException {
		List<Attributes> objs = new ArrayList<Attributes>();
		Name parsedName = null;
		if (null == vendorName || "".equals(vendorName)) {
			this.getVendorName();
		}
		try {
			if (null == parser) {
				parser = ctx.getNameParser("");
			}
			// parse the dn Name
			parsedName = parser.parse(dnName);
			logger.log(Level.INFO,
					"tdipopulate.info.ldap.operation.search.filter",
					new Object[] { dnName, filter });
			if (null == filter || "".equalsIgnoreCase(filter))
				filter = "(objectclass=*)";
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> neObjs = ctx.search(parsedName, filter,
					searchControls);
			while (neObjs.hasMoreElements()) {
				// objects
				SearchResult sr = neObjs.nextElement();
				//
				Attributes attrs = sr.getAttributes();
				logger.log(Level.FINEST,
						"tdipopulate.finest.ldap.operation.getObject",
						new Object[] { sr.getName() });
				objs.add(attrs);
			}
			neObjs.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,
					"tdipopulate.severe.ldap.operation.get_object_failed");
			throw new LdapOperationException(e,
					ErrorCode.ERROR_LDAP_OPERATION_GETOBJECTS,
					"err.ldap.operation.getObjects", sc.getHostURL());
		}
		return objs;
	}

	/**
	 * Retrieve the objectClass that satisfy the given DN.
	 * 
	 * @param DN
	 * @return A Hashtable that contains the objectClass
	 * @throws LdapException
	 */
	public Map<String, ObjectClass> getObjectClasses(String DN, String filter)
			throws LdapException {
		Map<String, ObjectClass> htObjectClasses = new Hashtable<String, ObjectClass>();
		
		if(filter == null || "".equals(filter.trim())) {
			filter = GeneralLdapControl.ALL_OBJECT_CLASS;
		}
		
		try {
			// ctx.getSchema(name)
			// ctx.getSchema("");
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// disables returning objects returned as part of the result.
			searchControls.setReturningObjFlag(false);
			// indicates that only attribute classobject will be returned.
			searchControls
					.setReturningAttributes(new String[] { LdapControl.ID_OBJECT_CLASS });
			searchControls.setCountLimit(countLimit);
			NamingEnumeration<SearchResult> objs = ctx.search(DN,
					filter, searchControls);
			List<String> objClasses = new ArrayList<String>();
			int index = 0;
			while (objs.hasMoreElements()) {
				// objects
				SearchResult sr = objs.nextElement();
				Attributes attrs = sr.getAttributes();
				logger
						.log(
								Level.FINEST,
								"tdipopulate.finest.ldap.operation.getObjectClasses.objectName",
								new Object[] { sr.getName() });
				NamingEnumeration<?> neAttrs = attrs.getAll();
				
				while (neAttrs.hasMoreElements()) {
					Attribute attr = (Attribute) neAttrs.nextElement();
					String attrID = attr.getID();
					logger
							.log(
									Level.FINEST,
									"tdipopulate.finest.ldap.operation.getObjectClasses.attributeID",
									new Object[] { attrID });
					// it's a object class attribute
					if (LdapControl.ID_OBJECT_CLASS.equalsIgnoreCase(attrID)) {
						NamingEnumeration<?> objectClasses = attr.getAll();
						
						while (objectClasses.hasMoreElements()) {
							String obj = (String)objectClasses.next();
							if (htObjectClasses.get(obj) == null) {
								logger
										.log(
												Level.INFO,
												"tdipopulate.finest.ldap.operation.getObjectClasses.objectClasses",
												new Object[] { obj });
								objClasses.add(obj);
							}
						}
						objectClasses.close();
						// get the object class defination
						for(; index <objClasses.size(); index++) {
							ObjectClass obc = getClassDefination(objClasses, index);
							htObjectClasses.put(objClasses.get(index), obc);
						}
					}
				}
				neAttrs.close();
			}
			objs.close();

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger
					.log(Level.SEVERE,
							"tdipopulate.severe.ldap.operation.get_objectclass_defination_failed");
			throw new LdapOperationException(e,
					ErrorCode.ERROR_LDAP_OPERATION_GETOBJECTCLASSES,
					"err.ldap.operation.getObjectClasses", sc.getHostURL());
		}
		return htObjectClasses;
	}

	/**
	 * 
	 * @return
	 * @throws LdapOperationException
	 */
	public List<ObjectClass> getAllClassDefination() throws LdapOperationException {
		List<ObjectClass> oList = new ArrayList<ObjectClass>();
		// set returnning attributes
		final String returnningAttrNames[] = new String[] { "NAME", "DESC",
				"MAY", "MUST" };
		try {
			DirContext schemaCtx = ctx.getSchema("");
			SearchControls cons = new SearchControls();
			cons.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// specify the attributes that will be returned
			cons.setReturningAttributes(returnningAttrNames);
			NamingEnumeration<SearchResult> ne = schemaCtx.search("ClassDefinition",
					"(NUMERICOID=*)", cons);
			while (ne.hasMoreElements()) {
				SearchResult sr = ne.nextElement();
				// attributes
				Attributes attrs = sr.getAttributes();

				NamingEnumeration<? extends Attribute> neAttrs = attrs.getAll();

				ObjectClass obc = new ObjectClass();
				while (neAttrs.hasMoreElements()) {
					Attribute attr = neAttrs.nextElement();
					String attrID = attr.getID();
					// objectclass name
					if (("NAME").equalsIgnoreCase(attrID)) {
						NamingEnumeration<?> obcNames = attr.getAll();
						if (obcNames.hasMoreElements()) {
							String obcName = (String)obcNames.next();
							obc.setName(obcName);
						}
						obcNames.close();

						// objectclass description
					} else if (("DESC").equalsIgnoreCase(attrID)) {
						NamingEnumeration<?> obcDescs = attr.getAll();
						if (obcDescs.hasMoreElements()) {
							String obcDesc = (String) obcDescs.next();
							obc.setDesc(obcDesc);
						}
						obcDescs.close();
					}

					// objectclass attributes
					else if (("MAY").equalsIgnoreCase(attrID)
							|| ("MUST").equalsIgnoreCase(attrID)) {
						NamingEnumeration<?> obcAttrs = attr.getAll();
						while (obcAttrs.hasMoreElements()) {
							String obcAttr = (String) obcAttrs.next();
							obc.addAttrbute(obcAttr);
						}
						obcAttrs.close();
					}
				}

				// add this objcec classes
				oList.add(obc);
				// close the neAttrs enumeration
				neAttrs.close();
			}
			// close the ne enumeration
			ne.close();
		} catch (NamingException e) {
			logger
					.log(Level.SEVERE,
							"tdipopulate.severe.ldap.operation.get_objectclass_defination_failed");
			throw new LdapOperationException(e,
					ErrorCode.ERROR_LDAP_OPERATION_GETOBJECTCLASSDEFINATION,
					"err.ldap.operation.getObjectClassDefination", sc
							.getHostURL());
		}
		return oList;
	}

	/**
	 * Retrieve a objectClass from the specified name.
	 * 
	 * @param name
	 * @return
	 * @throws LdapOperationException
	 */
	public ObjectClass getClassDefination(List<String> classes, int index)
			throws LdapOperationException {

		String name = classes.get(index);
		String obcIndex = "ClassDefinition/" + name;
		ObjectClass obc = new ObjectClass();
		// set returnning attributes
		final String returnningAttrNames[] = new String[] { "NAME", "DESC",
				"MAY", "MUST", "SUP"};
		
		if(vendorName == null || vendorName.trim().equals("")) {
			try {
				vendorName = getVendorName();
			} catch (LdapException e) {
				// ignore
			}
		}
		// securityPrincipal is the system built in auxiliary-class to User in AD
		if(VENDOR_MICROSOFT_ACTIVE_DIRECTORY.equals(vendorName)
			|| VENDOR_MICROSOFT_ACTIVE_DIRECTORY_ADAM.equals(vendorName)) {
			if(AD_USER_CLASS.equalsIgnoreCase(name)) {
				classes.add(AD_BUILTIN_AUX_CLASS);
			}
		}
		try {
			// Get the schema tree root
			DirContext schema = (DirContext) ctx.getSchema("");
			// look up the object class schema
			DirContext obcSchema = (DirContext) schema.lookup(obcIndex);

			// get the attributes
			Attributes atts = obcSchema.getAttributes("", returnningAttrNames);
			// Attributes atts = obcSchema.getAttributes("");

			NamingEnumeration<?> neAttrs = atts.getAll();

			while (neAttrs.hasMoreElements()) {

				Attribute attr = (Attribute) neAttrs.nextElement();
				String attrID = attr.getID();
				// objectclass name
				if (("NAME").equalsIgnoreCase(attrID)) {
					NamingEnumeration<?> attrNames = attr.getAll();
					if (attrNames.hasMoreElements()) {
						String obcName = (String) attrNames.next();
						obc.setName(obcName);
					}
					// close the Enumeration attrNames
					attrNames.close();

					// objectclass description

				} else if (("DESC").equalsIgnoreCase(attrID)) {
					NamingEnumeration<?> attrDescs = attr.getAll();
					if (attrDescs.hasMoreElements()) {
						String obcDesc = (String) attrDescs.next();
						obc.setDesc(obcDesc);
					}
					// close the Enumeration attrDescs
					attrDescs.close();
				}

				// objectclass attributes
				else if (("MAY").equalsIgnoreCase(attrID)
						|| ("MUST").equalsIgnoreCase(attrID)) {
					NamingEnumeration<?> attrDescs = attr.getAll();
					while (attrDescs.hasMoreElements()) {
						String obcAttr = (String) attrDescs.next();
						obc.addAttrbute(obcAttr);
					}
					// close the Enumeration attrDescs
					attrDescs.close();
				}
				// mark upper level classes to be processes
				else if ("SUP".equalsIgnoreCase(attrID)) {
					NamingEnumeration<?> attrClasses = attr.getAll();
					while (attrClasses.hasMoreElements()) {
						String superClass = (String) attrClasses.next();
						
						if(!classes.contains(superClass)) {
							classes.add(superClass);
						}
					}
					// close the Enumeration attrNames
					attrClasses.close();
				}
			}
			// close the Enumeration neAttrs
			neAttrs.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.severe("Exception while getting a ObjectClass's Defination:"
					+ e);
			throw new LdapOperationException(e,
					ErrorCode.ERROR_LDAP_OPERATION_GETOBJECTCLASSDEFINATION,
					"err.ldap.operation.getObjectClassDefination", sc
							.getHostURL());
		}
		return obc;

	}
	
	public boolean hasSearchResult(String DN, String filter) throws LdapException{
		boolean hasResult = false;
		if(filter == null || "".equals(filter.trim())) {
			filter = GeneralLdapControl.ALL_OBJECT_CLASS;
		}
		
		try {
			// ctx.getSchema(name)
			// ctx.getSchema("");
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// disables returning objects returned as part of the result.
			searchControls.setReturningObjFlag(false);
			// indicates that only attribute classobject will be returned.
			searchControls
					.setReturningAttributes(new String[] { LdapControl.ID_OBJECT_CLASS });
			searchControls.setCountLimit(countLimit);
			NamingEnumeration<SearchResult> objs = ctx.search(DN,
					filter, searchControls);
			while (objs.hasMoreElements()) {
				hasResult = true;
				break;
			}
			objs.close();

		} catch (NamingException e) {
			logger
					.log(Level.SEVERE,
							"tdipopulate.severe.ldap.operation.get_objectclass_defination_failed");
			throw new LdapOperationException(e,
					ErrorCode.ERROR_LDAP_OPERATION_GETOBJECTCLASSES,
					"err.ldap.operation.getObjectClasses", sc.getHostURL());
		}
		return hasResult;	
	}
		
}
