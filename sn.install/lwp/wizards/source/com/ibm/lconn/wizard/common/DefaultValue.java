/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.common;

import java.io.File;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author Xiao Feng Yu (yuxiaof@cn.ibm.com)
 */

public class DefaultValue {

	private DefaultValue() {

	}

	private static final PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle

	.getBundle(DefaultValue.class.getName());

	/**
	 * Gets default database port
	 * 
	 * @param dbType
	 * @return
	 */

	public static String getDatabasePort(String dbType) {

		if (Constants.DB_DB2.equals(dbType)) {
			return prb.getString("db2.port");
		} else if (Constants.DB_ORACLE.equals(dbType)) {
			return prb.getString("oracle.port");
		} else if (Constants.DB_SQLSERVER.equals(dbType)) {
			return prb.getString("sqlserver.port");
		}

		return null;
	}
	
	/**
	 * Gets default database administrator account
	 * @param dbType
	 * @param osType
	 * @return
	 */
	public static String getDatabaseAdmin(String dbType, String osType) {
		String admin = null;

		try {
			admin = prb.getString(dbType + "." + osType + ".admin");
		} catch(MissingResourceException e) {
			admin = prb.getString(dbType + ".admin"); 
		}
		return admin;
	}
	
	/**
	 * Gets default database administrator account
	 * @param dbType
	 * @return
	 */
	public static String getDatabaseAdmin(String dbType) {
		return getDatabaseAdmin(dbType, CommonHelper.getPlatformType());
	}

	/**
	 * Gets default ldap server port
	 * 
	 * @param ldapType
	 * @param sslEnabled
	 * @return
	 */

	public static String getLDAPPort(boolean sslEnabled) {
		return sslEnabled? "636" : "389";
	}

	/**
	 * Gets default ldap admin DN
	 * 
	 * @param ldapType
	 * @return
	 */

	public static String getLDAPAdminDN(String ldapType) {

		if (Constants.LDAP_AD.equals(ldapType)) {
			return prb.getString("ad.admindn");
		} else if (Constants.LDAP_DOMINO.equals(ldapType)) {
			return prb.getString("domino.admindn");
		} else if (Constants.LDAP_TIVOLI.equals(ldapType)) {
			return prb.getString("tivoli.admindn");
		} else if (Constants.LDAP_ORACLE.equals(ldapType)) {
			return prb.getString("sun.admindn");
		} else if (Constants.LDAP_NDS.equals(ldapType)) {
			return prb.getString("nds.admindn");
		} else if (Constants.LDAP_ADAM.equals(ldapType)) {
			return prb.getString("adam.admindn");
		}


		return null;
	}

	/**
	 * Gets default ldap search filter
	 * 
	 * @param ldapType
	 * @return
	 */

	public static String getLDAPSearchFilter(String ldapType) {

		if (Constants.LDAP_AD.equals(ldapType)) {
			return prb.getString("ad.searchfilter");
		} else if (Constants.LDAP_DOMINO.equals(ldapType)) {
			return prb.getString("domino.searchfilter");
		} else if (Constants.LDAP_TIVOLI.equals(ldapType)) {
			return prb.getString("tivoli.searchfilter");
		} else if (Constants.LDAP_ORACLE.equals(ldapType)) {
			return prb.getString("sun.searchfilter");
		} else if (Constants.LDAP_NDS.equals(ldapType)) {
			return prb.getString("nds.searchfilter");
		} else if (Constants.LDAP_ADAM.equals(ldapType)) {
			return prb.getString("adam.searchfilter");
		}

		return null;
	}

	/**
	 * Gets feature database name
	 * 
	 * @param feature
	 * @param dbType
	 * @return
	 */

	public static String getFeatureDatabaseName(String feature, String dbType) {

		if (Constants.DB_DB2.equals(dbType)
				|| Constants.DB_SQLSERVER.equals(dbType)) {
			if (Constants.FEATURE_ACTIVITIES.equals(feature)) {
				return Constants.DB_NAME_ACTIVITIES;
			} else if (Constants.FEATURE_BLOGS.equals(feature)) {
				return Constants.DB_NAME_BLOGS;
			} else if (Constants.FEATURE_COMMUNITIES.equals(feature)) {
				return Constants.DB_NAME_COMMUNITIES;
			} else if (Constants.FEATURE_DOGEAR.equals(feature)) {
				return Constants.DB_NAME_DOGEAR;
			} else if (Constants.FEATURE_PROFILES.equals(feature)) {
				return Constants.DB_NAME_PROFILES;
			}
		} else if (Constants.DB_ORACLE.equals(dbType)) {
			if (Constants.FEATURE_ACTIVITIES.equals(feature)) {
				return "LSCONN";
			} else if (Constants.FEATURE_BLOGS.equals(feature)) {
				return "LSCONN";
			} else if (Constants.FEATURE_COMMUNITIES.equals(feature)) {
				return "LSCONN";
			} else if (Constants.FEATURE_DOGEAR.equals(feature)) {
				return "LSCONN";
			} else if (Constants.FEATURE_PROFILES.equals(feature)) {
				return "";
			}
		}

		return null;
	}

	/**
	 * Gets feature database user
	 * 
	 * @param feature
	 * @param dbType
	 * @return
	 */

	public static String getFeatureDatabaseUser(String feature, String dbType) {

		if (Constants.DB_SQLSERVER.equals(dbType)) {
			if (Constants.FEATURE_ACTIVITIES.equals(feature)) {
				return "OAUSER";
			} else if (Constants.FEATURE_BLOGS.equals(feature)) {
				return "BLOGSUSER";
			} else if (Constants.FEATURE_COMMUNITIES.equals(feature)) {
				return "SNCOMMUSER";
			} else if (Constants.FEATURE_DOGEAR.equals(feature)) {
				return "DOGEARUSER";
			} else if (Constants.FEATURE_PROFILES.equals(feature)) {
				return "PROFUSER";
			}
		} else if (Constants.DB_DB2.equals(dbType)) {
			if (Constants.FEATURE_ACTIVITIES.equals(feature)) {
				return "LCUSER";
			} else if (Constants.FEATURE_BLOGS.equals(feature)) {
				return "LCUSER";
			} else if (Constants.FEATURE_COMMUNITIES.equals(feature)) {
				return "LCUSER";
			} else if (Constants.FEATURE_DOGEAR.equals(feature)) {
				return "LCUSER";
			} else if (Constants.FEATURE_PROFILES.equals(feature)) {
				return "LCUSER";
			}
		} else if (Constants.DB_ORACLE.equals(dbType)) {
			if (Constants.FEATURE_ACTIVITIES.equals(feature)) {
				return "OAUSER";
			} else if (Constants.FEATURE_BLOGS.equals(feature)) {
				return "BLOGS";
			} else if (Constants.FEATURE_COMMUNITIES.equals(feature)) {
				return "SNCOMMUSER";
			} else if (Constants.FEATURE_DOGEAR.equals(feature)) {
				return "DOGEARUSER";
			} else if (Constants.FEATURE_PROFILES.equals(feature)) {
				return "PROFUSER";
			}
		}
		return null;
	}
	
	public static String getDefaultJREPath() {
		File root = new File("jvm");
		File jrePath = new File(root, getPlatformForJRE() + Constants.FS + "jre");		
		return jrePath.getAbsolutePath();
	}
	
	public static String getDefaultJavaExecutablePath() {
		String jrePath = getDefaultJREPath();
		if(Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			return new File(jrePath, "bin" + Constants.FS + Constants.JAVA_EXECUTABLE_WIN).
				getAbsolutePath();
		} else {
			return new File(jrePath, "bin" + Constants.FS + Constants.JAVA_EXECUTABLE).
				getAbsolutePath();
		}
	}
	
	public static String getPlatform() {
		String platform = CommonHelper.getPlatformType();
		if(Constants.OS_WINDOWS.equals(platform) || Constants.OS_AIX.equals(platform)) {
			return platform;
		} else if (Constants.OS_LINUX_REDHAT.equals(platform) || Constants.OS_LINUX_SUSE.equals(platform)) {
			return Constants.OS_LINUX;
		} else {
			return Constants.OS_OTHER;
		}
	}
	public static String getPlatformForJRE() {
		String platform = CommonHelper.getPlatformTypeForJRE();
		if(Constants.OS_WINDOWS.equals(platform) || Constants.OS_AIX.equals(platform) || Constants.OS_ZLINUX_S390.equals(platform)) {
			return platform;
		} else if (Constants.OS_LINUX_REDHAT.equals(platform) || Constants.OS_LINUX_SUSE.equals(platform)) {
			return Constants.OS_LINUX;
		} else {
			return Constants.OS_OTHER;
		}
	}
	
	public static String getJDBCLibraryPath(String dbType, String dbHome) {
	
		if(dbHome.endsWith(Constants.FS)) {
			dbHome = dbHome.substring(0, dbHome.length()-1);
		}
		if(Constants.DB_DB2.equals(dbType)) {
			StringBuffer jars = new StringBuffer();
			jars.append(dbHome)
				.append(Constants.FS)
				.append("java")
				.append(Constants.FS)
				.append("db2jcc4.jar")
				.append(Constants.PATH_SEPARATOR)
				.append(dbHome)
				.append(Constants.FS)
				.append("java")
				.append(Constants.FS)
				.append("db2jcc_license_cu.jar");
			return jars.toString();
		} else if(Constants.DB_ORACLE.equals(dbType)) {
			StringBuffer jars = new StringBuffer();
			jars.append(dbHome)
				.append(Constants.FS)
				.append("jdbc")
				.append(Constants.FS)
				.append("lib")
				.append(Constants.FS)
				.append(Constants.JDBC_ORACLE_LIB);
			return jars.toString();
		} 
		return null;
	}
}
