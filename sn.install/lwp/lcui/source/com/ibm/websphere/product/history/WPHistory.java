/*
********************************************************************
* IBM Confidential                                                 *
*                                                                  *
* OCO Source Materials                                             *
*                                                                  *
*                                                                  *
* Copyright IBM Corp. 2003, 2015                                   *
*                                                                  *
* The source code for this program is not published or otherwise   *
* divested of its trade secrets, irrespective of what has been     *
* deposited with the U.S. Copyright Office.                        *
********************************************************************
*/

package com.ibm.websphere.product.history;

import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.WASProductException;
import com.ibm.websphere.product.history.filters.EFixAppliedFilenameFilter;
import com.ibm.websphere.product.history.filters.EFixDriverFilenameFilter;
import com.ibm.websphere.product.history.filters.PTFAppliedFilenameFilter;
import com.ibm.websphere.product.history.filters.PTFDriverFilenameFilter;
import com.ibm.websphere.product.history.xml.AppliedHandler;
import com.ibm.websphere.product.history.xml.AppliedWriter;
import com.ibm.websphere.product.history.xml.EventHandler;
import com.ibm.websphere.product.history.xml.EventWriter;
import com.ibm.websphere.product.history.xml.componentApplied;
import com.ibm.websphere.product.history.xml.efixApplied;
import com.ibm.websphere.product.history.xml.efixDriver;
import com.ibm.websphere.product.history.xml.eventHistory;
import com.ibm.websphere.product.history.xml.ptfApplied;
import com.ibm.websphere.product.history.xml.ptfDriver;
import com.ibm.websphere.product.xml.BaseFactory;
import com.ibm.websphere.product.xml.BaseHandlerException;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.SAXParseException;

// Referenced classes of package com.ibm.websphere.product.history:
//            WASHistoryException

/**
 * EventHistory provides an interface into the product version history
 * information.  The file set contains information about the Websphere
 *  Portal updates, that is, eFixes and PTFs.
 *
 * Note:
 *    If this class is used outside the confines of the WebSphere product then
 *    the System property "wps.install.root" must be set to point to the directory
 *    that Websphere Portal is installed in. e.g "c:\websphere\portalserver"
 *
 * @author Steven Pritko, IBM
 * @version 5.0
 */
public class WPHistory extends WASHistory {

   /** Constants holding CMVC version information.  This information
    *  is dynamic, changing with each source update. */

   public static final String pgmVersion = "1.4" ;
   /** Constants holding CMVC version information.  This information
    *  is dynamic, changing with each source update. */

   public static final String pgmUpdate = "10/14/03" ;

   protected static String getString(String s)
   {
      if (msgs == null)
         return "com.ibm.websphere.product.history.WPHistoryNLS:" + s;
      try {
         return msgs.getString(s);
      } catch (MissingResourceException missingresourceexception) {
         return "com.ibm.websphere.product.history.WPHistoryNLS:" + s;
      }
   }

   /**
    * <p>An array of history directory names; the result array includes the
    * product directory name, the version directory name, and the history
    * directory name.  An exception is thrown if the directories cannot be
    * determined.</p>
    *
    * @return String[] An array holding the product directory name,
    *                  the version directory name, and the history
    *                  directory name.
    *
    * @exception WASHistoryException An exception which is thrown
    *                                if the directories cannot be
    *                                determined.
    */

   public static String[] determineHistoryDirNames()
   throws WASHistoryException
   {
      String s;
      try {
         s = WPProduct.computeProductDirName();
      } catch (WASProductException wasproductexception) {
         throw createException("WVER201E", null, wasproductexception);
      }
      String s1 = WPProduct.computeVersionDirName(s);
      String s2 = determineHistoryDirName(s1);
      return(new String[] {
                s, s1, s2
             });
   }

   /**
    * <p>Determine and return the history directory from
    * system properties.  The argument verstion directory will
    * be used, with adjustment, if no history directory
    * property is set.  An exception is thrown if the
    * history directory cannot be determined.</p>
    *
    * @param versionDirName The base directory from which to compute
    *                       the history directory, used in case no
    *                       history directory property is set.
    *
    * @return String The computed history directory name.
    */
   public static String determineHistoryDirName(String s)
   {
      String s1 = System.getProperty("wps.history.dir");
      if (s1 == null)
         s1 = getHistoryDirName(s);
      else
         s1 = WPProduct.preparePath(s1, false);
      return s1;
   }

   /**
    * <p>Answer a history directory based on the argument version
    * directory.</p>
    *
    * @return String The computed history directory name.
    */
   public static String getHistoryDirName(String s)
   {
      s = WPProduct.preparePath(s, false);
      return s + File.separator + "history";
   }

   /**
    * <p>Parameterized constructor.  All history directories are
    * assigned directly from arguments.  No system properties are
    * used.  Exceptions may be registed in the new instance.</p>
    *
    * @param dirs A 3 element array containing the following directories:
    * <ul>
    * <li>productDirName - The product directory for use
    * <li>versionDirName -  The version directory for use
    * <li>historyDirName - The history directory for use
    * </ul>
    */
   public WPHistory( String[] dirs ) {
      this( dirs[0], dirs[1], dirs[2] );
   }

   /**
    * <p>Default constructor.  The new instance is initialized
    * wholly from system properties.  Exceptions may be registed
    * in the new instance.</p>
    */
   public WPHistory() throws WASHistoryException {
      this( determineHistoryDirNames() );
      /*
      super();
      clearExceptions();
      try {
          String as[] = determineHistoryDirNames();
          initialize(as[0], as[1], as[2]);
      }
      catch(WASHistoryException washistoryexception) {
          addException(washistoryexception);
      }
      */
   }

   /**
    * <p>Parameterized constructor.  All history directories are
    * assigned directly from arguments.  No system properties are
    * used.  Exceptions may be registed in the new instance.</p>
    *
    * @param productDirName The product directory for use.
    * @param versionDirName The version directory for use.
    * @param historyDirName The history directory for use.
    */

   public WPHistory(String s, String s1, String s2) {
      super(s, s1, s2);
   }

   protected void initialize(String s, String s1, String s2) {
       productDir = new File(s);
       productDirName = productDir.getAbsolutePath();
       versionDir = new File(s1);
       versionDirName = versionDir.getAbsolutePath();
       dtdDirName = WPProduct.getDTDDirName(versionDirName);
       dtdDir = new File(dtdDirName);
       dtdDirName = dtdDir.getAbsolutePath();
       historyDir = new File(s2);
       historyDirName = historyDir.getAbsolutePath();
       String s3 = historyDirName + File.separator + "event.history";
       historyFile = new File(s3);
       historyFileName = historyFile.getAbsolutePath();
       String s4 = s3 + ".backup";
       backupFile = new File(s4);
       backupFileName = backupFile.getAbsolutePath();
       historyInfo = new HashMap();
       loadHistory();
   }


   /**
    * <p>Answer a string representation of the receiver.
    * This representation currently displays the bound product
    * directory name, the bound version directory name, and
    * the bound history directory name.</p>
    *
    * @return String A string representation of the receiver.
    */

   public String toString() {
      return "WPHistory: " + getProductDirName() + " :: " + getVersionDirName() + " :: " + getHistoryDirName();
   }

   /** The id of the message bundle use by WPHistory. */
   public static final String bundleId = "com.ibm.websphere.product.history.WPHistoryNLS";

   /** The initialized resource bundle.  This is public for shared access. */
   public static final ResourceBundle msgs;

   /**
    * <p>The name of the property which is used to specify an
    * alternate history directory.</p>
    */
   public static final String HISTORY_DIR_PROPERTY_NAME = "wps.history.dir";

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