/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */

package com.ibm.websphere.product;

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.product.filters.ComponentFilenameFilter;
import com.ibm.websphere.product.filters.EFixFilenameFilter;
import com.ibm.websphere.product.filters.ExtensionFilenameFilter;
import com.ibm.websphere.product.filters.PTFFilenameFilter;
import com.ibm.websphere.product.filters.ProductFilenameFilter;
import com.ibm.websphere.product.xml.BaseFactory;
import com.ibm.websphere.product.xml.component.ComponentHandler;
import com.ibm.websphere.product.xml.component.ComponentWriter;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.EFixHandler;
import com.ibm.websphere.product.xml.efix.EFixWriter;
import com.ibm.websphere.product.xml.efix.efix;
import com.ibm.websphere.product.xml.efix.ptf;
import com.ibm.websphere.product.xml.extension.ExtensionHandler;
import com.ibm.websphere.product.xml.extension.extension;
import com.ibm.websphere.product.xml.product.ProductHandler;
import com.ibm.websphere.product.xml.product.ProductWriter;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.product.xml.websphere.WebSphereHandler;
import com.ibm.websphere.product.xml.websphere.websphere;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.SAXParseException;

/**
 * WPProduct provides an interface into the product version information.
 * The file set contains information about Websphere Portal
 * e.g Product Name, Edition, Build Number, Build Date etc.
 *
 * Note:
 *    If this class is used outside the confines of the WebSphere product then
 *    the System property "wps.install.root" must be set to point to the directory
 *    that Websphere Portal is installed in. e.g "c:\websphere\portalserver"
 *
 * @author Steven Pritko, IBM
 * @version 5.0
 */

public class WPProduct extends WASProduct {

   /** Constants holding CMVC version information.  This information
    *  is dynamic, changing with each source update. */
   public static final String pgmVersion = "1.3" ;
   /** Constants holding CMVC version information.  This information
    *  is dynamic, changing with each source update. */
   public static final String pgmUpdate = "10/14/03" ;

   protected static String getString(String s)
   {
      if (msgs == null)
         return "com.ibm.websphere.product.WPProductNLS:" + s;
      try {
         return msgs.getString(s);
      } catch (MissingResourceException missingresourceexception) {
         return "com.ibm.websphere.product.WPProductNLS:" + s;
      }
   }


   /**
    * <p>Determine and return the product directory from
    * system properties.  An exception will be thrown if
    * the product directory cannot be determined.</p>
    *
    * @return String The computed product directory name.
    *
    * @exception WASProductException An exception which is thrown
    *                                if the product directory cannot
    *                                be determined.
    */

   public static String computeProductDirName() throws WASProductException {
      String s = System.getProperty( INSTALL_ROOT_PROPERTY_NAME );
      if (s == null) {
         throw createException("WVER0001E", null, null);
      } else {
         s = preparePath(s, false);
         return s;
      }
   }

   /**
    * <p>Determine and return the version directory from
    * system properties.  The argument product directory will
    * be used, with adjustment, if no version directory
    * property is set.  An exception is thrown if the
    * version directory cannot be determined.</p>
    *
    * @param productDirName The base directory from which to compute
    *                       the version directory, used in case no
    *                       version directory property is set.
    *
    * @return String The computed version directory name.
    */
   public static String computeVersionDirName(String s) {
      String s1 = System.getProperty( VERSION_DIR_PROPERTY_NAME );
      if (s1 == null)
         s1 = getVersionDirName(s);
      else
         s1 = preparePath(s1, false);
      return s1;
   }

   /**
    * <p>Answer a version directory based on the argument product
    * directory.</p>
    *
    * @return String The computed version directory name.
    */
   public static String getVersionDirName(String s) {
      s = preparePath(s, false);
      return s + File.separator + "version";
   }

   /**
    * <p>Determine and return the DTD directory from
    * system properties.  The argument version directory will
    * be used, with adjustment, if no DTD directory
    * property is set.  An exception is thrown if the
    * DTD directory cannot be determined.</p>
    *
    * @param versionDirName The base directory from which to compute
    *                       the DTD directory, used in case no
    *                       DTD directory property is set.
    *
    * @return String The computed DTD directory name.
    *
    * @exception WASProductException An exception which is thrown
    *                                if the DTD directory cannot
    *                                be determined.
    */

   public static String computeDTDDirName(String s) throws WASProductException {
      String s1 = System.getProperty( DTD_DIR_PROPERTY_NAME );
      if (s1 == null) {
         if (s == null)
            throw createException("WVER0019", null, null);
         s1 = getDTDDirName(s);
      } else {
         s1 = preparePath(s1, false);
      }
      return s1;
   }

   /**
    * <p>Answer a DTD directory based on the argument version
    * directory.</p>
    *
    * @return String The computed DTD directory name.
    */
   public static String getDTDDirName(String s)
   {
      s = preparePath(s, false);
      return s + File.separator + "dtd";
   }

   /**
    * <p>Determine and return the log directory from
    * system properties.  The argument version directory will
    * be used, with adjustment, if no log directory
    * property is set.  An exception is thrown if the
    * log directory cannot be determined.</p>
    *
    * @param versionDirName The base directory from which to compute
    *                       the log directory, used in case no
    *                       log directory property is set.
    *
    * @return String The computed log directory name.
    *
    * @exception WASProductException An exception which is thrown
    *                                if the log directory cannot
    *                                be determined.
    */
   public static String computeLogDirName(String s) throws WASProductException {
      String s1 = System.getProperty(LOG_DIR_PROPERTY_NAME);
      if (s1 == null) {
         if (s == null)
            throw createException("WVER0017", null, null);
         s1 = getLogDirName(s);
      } else {
         s1 = preparePath(s1, false);
      }
      return s1;
   }

   /**
    * <p>Answer a log directory based on the argument version
    * directory.</p>
    *
    * @return String The computed log directory name.
    */
   public static String getLogDirName(String s)
   {
      s = preparePath(s, false);
      return s + File.separator + "log";
   }

   /**
    * <p>Determine and return the backup directory from
    * system properties.  The argument version directory will
    * be used, with adjustment, if no backup directory
    * property is set.  An exception is thrown if the
    * backup directory cannot be determined.</p>
    *
    * @param versionDirName The base directory from which to compute
    *                       the backup directory, used in case no
    *                       backup directory property is set.
    *
    * @return String The computed backup directory name.
    *
    * @exception WASProductException An exception which is thrown
    *                                if the backup directory cannot
    *                                be determined.
    */

   public static String computeBackupDirName(String s) throws WASProductException {
      String s1 = System.getProperty(BACKUP_DIR_PROPERTY_NAME);
      if (s1 == null) {
         if (s == null)
            throw createException("WVER0018", null, null);
         s1 = getBackupDirName(s);
      } else {
         s1 = preparePath(s1, false);
      }
      return s1;
   }

   /**
    * <p>Answer a backup directory based on the argument version
    * directory.</p>
    *
    * @return String The computed backup directory name.
    */

   public static String getBackupDirName(String s)
   {
      s = preparePath(s, false);
      return s + File.separator + "backup";
   }

   /**
    * <p>Determine and return the TMP directory from
    * system properties.  The TMP_DIR_PROPERTY_NAME
    * system property is tested, then the JAVA_TMP_DIR_PROPERTY_NAME
    * is tested.  If neither is set, then DEFAULT_TMP_DIR_NAME
    * is used.
    *
    * @return String The computed TMP directory name.
    */

   public static String getTmpDirName()
   {
      String s = System.getProperty(TMP_DIR_PROPERTY_NAME);
      if (s == null) {
         s = System.getProperty("java.io.tmpdir");
         if (s == null)
            s = "tmp";
      }
      s = preparePath(s, false);
      return s;
   }


   /**
    * <p>Default constructor.  The new instance is initialized
    * wholly from system properties.  Exceptions may be registed
    * in the new instance.</p>
    * <p>
    * The location of the base product directory is taken form the wps.install.root 
    * System property.
    * </p>
    */

   public WPProduct() {
      super();
      // Since Initialize has already been run by WASProduct
      //  Defaulted to WAS default dirs, which may not exist and may have created an exception.
      //  We can ignore those errors and reinitialize ourself.
      clearExceptions();  
      try
      {
          String s = computeProductDirName();
          String s1 = computeVersionDirName(s);
          String s2 = computeDTDDirName(s1);
          String s3 = computeLogDirName(s1);
          String s4 = computeBackupDirName(s1);
          initialize(s, s1, s2, s3, s4);
      } catch(WASProductException wasproductexception) {
          addException(wasproductexception);
      }
   }

   /**
    * <p>Parameterized constructor.  The product directories are
    * computed from the specified directory.  No system properties
    * are used.  Exceptions may be registed in the new instance.</p>
    *
    * @param productDirName The base product directory.
    */

   public WPProduct(String s) {
      super( s );
      clearExceptions();  // Since Initialize has already been run, default WAS dir may not exist, we can ignore those errors
      try
      {
          String s1 = computeVersionDirName(s);
          String s2 = computeDTDDirName(s1);
          String s3 = computeLogDirName(s1);
          String s4 = computeBackupDirName(s1);
          initialize(s, s1, s2, s3, s4);
      } catch(WASProductException wasproductexception) {
          addException(wasproductexception);
      }
   }

   /**
    * <p>Parameterized constructor.  All product directories are
    * assigned directly from arguments.  No system properties are
    * used.  Exceptions may be registed in the new instance.</p>
    *
    * @param productDirName The product directory for use.
    * @param versionDirName The version directory for use.
    * @param dtdDirName The DTD directory for use.
    * @param logDirName The log directory for use.
    * @param backupDirName The backup directory for use.
    */
   public WPProduct(String s, String s1, String s2, String s3, String s4) {
      super( s,s1, s2, s3, s4 );
   }

   public String toString()
   {
      return "WPProduct: " + getProductDirName() + " :: " + getVersionDirName();
   }

   /** Constants for the known product ids. */
   public static final String PRODUCTID_EXPRESS_PLUS = "EXPRESS_PLUS" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_EXPRESS = "EXPRESS" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_ENABLE = "ENABLE" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_EXTEND = "EXTEND" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_MP = "MP" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_ISERIES = "ISERIES" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_ZOS = "ZOS" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_ISC = "ISC" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_WCS = "WCS" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_TOOLKIT = "TOOLKIT" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_TMS = "TMS" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_WSE = "WSE" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_WEMP = "WEMP" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_PDM = "PDM" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_WCM = "WCM" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_WBCR = "WBCR" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_PZN = "PZN" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_WPCP = "WPCP" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_WBSE = "WBSE" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_TS = "TS" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_DOC = "DOC" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_LRNWPS = "LRNWPS" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_LRNSRVR = "LRNSRVR" ;
   /** Constants for the known product ids. */
   public static final String PRODUCTID_ISERIES_HOTFIX = "ISeries Hotfix" ;

   /** The array of known product ids. */
   public static final String PRODUCT_IDS[] = {
      PRODUCTID_EXPRESS, PRODUCTID_EXPRESS_PLUS, 
      PRODUCTID_MP, PRODUCTID_ENABLE, PRODUCTID_EXTEND, 
      PRODUCTID_ISC, 
      PRODUCTID_TOOLKIT, 
      PRODUCTID_ISERIES,
      PRODUCTID_ZOS,
      PRODUCTID_TS,
      PRODUCTID_DOC,
      PRODUCTID_LRNWPS, 
      PRODUCTID_LRNSRVR,
      PRODUCTID_WCS,
      PRODUCTID_TMS,                  // 11 (10 in talbe talk)
      PRODUCTID_WSE,                  // 12 (11 in talbe talk)
      PRODUCTID_WEMP,                 // 13 (12 in talbe talk)
      PRODUCTID_PDM,                  // 14 (13 in talbe talk)
      PRODUCTID_WCM,                  // 15 (14 in talbe talk)
      PRODUCTID_WBCR,                 // 16 (15 in talbe talk)
      PRODUCTID_PZN,                  // 17 (16 in talbe talk)
      PRODUCTID_WPCP,                 // 18 (17 in talbe talk)
      PRODUCTID_ISERIES_HOTFIX,       // 19 (18 in talbe talk)
      PRODUCTID_WBSE,                  // 20 (19 in talbe talk)
      LCUtil.PRODUCTID_LC_ACTIVITIES,
      LCUtil.PRODUCTID_LC_BLOGS,
      LCUtil.PRODUCTID_LC_COMMUNITIES,
      LCUtil.PRODUCTID_LC_DOGEAR,
      LCUtil.PRODUCTID_LC_PROFILES,
      LCUtil.PRODUCTID_LC_HOMEPAGE,
      LCUtil.PRODUCTID_LC_WIKIS,
      LCUtil.PRODUCTID_LC_SEARCH,
      LCUtil.PRODUCTID_LC_FILES,
      LCUtil.PRODUCTID_LC_NEWS,
      LCUtil.PRODUCTID_LC_MOBILE,
      LCUtil.PRODUCTID_LC_FORUM,
      LCUtil.PRODUCTID_LC_MODERATION,
      LCUtil.PRODUCTID_LC_METRICS,
      LCUtil.PRODUCTID_LC_CCM
   };

   /** The id of the message bundle use by WPProduct. */
   public static final String bundleId = "com.ibm.websphere.product.WPProductNLS";

   /** The initialized resource bundle.  This is public for shared access. */
   public static final ResourceBundle msgs;


   /** Constants for providing product directories. */
   public static final String INSTALL_ROOT_PROPERTY_NAME = "wps.install.root" ;
   /** Constants for providing product directories. */
   public static final String VERSION_DIR_PROPERTY_NAME = "wps.version.dir" ;
   /** Constants for providing product directories. */
   public static final String DTD_DIR_PROPERTY_NAME = "wps.version.dtd.dir" ;
   /** Constants for providing product directories. */
   public static final String LOG_DIR_PROPERTY_NAME = "wps.version.log.dir" ;
   /** Constants for providing product directories. */
   public static final String BACKUP_DIR_PROPERTY_NAME = "wps.version.backup.dir" ;
   /** Constants for providing product directories. */
   public static final String TMP_DIR_PROPERTY_NAME = "wps.version.tmp.dir" ;

   static 
   {
      ResourceBundle resourcebundle;
      try {
         resourcebundle = ResourceBundle.getBundle( bundleId );
      } catch (MissingResourceException missingresourceexception) {
         resourcebundle = null;
      }
      msgs = resourcebundle;
   }
}