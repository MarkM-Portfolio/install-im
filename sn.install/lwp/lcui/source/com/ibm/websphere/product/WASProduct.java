/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.websphere.product;

/*
 * Product Information
 *
 * History 1.3, 8/6/04
 *
 * 25-Jun-2002 Added standard header.
 *             Messaging and error handling update.
 *
 * 13-Jan-2003 Updated to allow the addition and removal of
 *             component and product files.
 */

import com.ibm.websphere.product.filters.ComponentFilenameFilter;
import com.ibm.websphere.product.filters.EFixFilenameFilter;
import com.ibm.websphere.product.filters.ExtensionFilenameFilter;
import com.ibm.websphere.product.filters.PTFFilenameFilter;
import com.ibm.websphere.product.filters.ProductFilenameFilter;
import com.ibm.websphere.product.xml.BaseFactory;
import com.ibm.websphere.product.xml.BaseHandler;
import com.ibm.websphere.product.xml.BaseHandlerException;
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
 * WASProduct provides an interface into the product version information. The file set contains information about the Websphere Application Server e.g Product Name, Edition, Build Number, Build Date etc. Note: If this class is used outside the confines of the WebSphere product then the System property "server.root" must be set to point to the directory that Websphere is installed in. e.g "c:\websphere\appserver"
 * @author  Jason R McGee, IBM
 * @version  5.0
 */

public class WASProduct
{
    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

    public static final String pgmVersion = "1.3" ;
    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

    public static final String pgmUpdate = "8/6/04" ;

    /** Constants for the known product ids. */

    public static final String PRODUCTID_EMBEDDED_EXPRESS = "embeddedEXPRESS" ;
    /** Constants for the known product ids. */

    public static final String PRODUCTID_EXPRESS = "EXPRESS" ;
    /** Constants for the known product ids. */

    public static final String PRODUCTID_BASE = "BASE" ;
    /** Constants for the known product ids. */

    public static final String PRODUCTID_ND = "ND" ;
    /** Constants for the known product ids. */

    public static final String PRODUCTID_XD = "XD" ;
    // Vapor
 /** Constants for the known product ids. */

    public static final String PRODUCTID_PME = "PME" ;
    /** Constants for the known product ids. */

    public static final String PRODUCTID_CLIENT = "CLIENT" ;

    /** The array of known product ids. */

    public static final String[] PRODUCT_IDS = new String[] {
        PRODUCTID_EMBEDDED_EXPRESS,
        PRODUCTID_EXPRESS,
        PRODUCTID_BASE,
        PRODUCTID_ND,
        PRODUCTID_XD,
        PRODUCTID_PME,
        PRODUCTID_CLIENT
    };

    // Messaging constants ...

    /** The id of the message bundle use by WASProduct. */

    public static final String bundleId = "com.ibm.websphere.product.WASProductNLS";

    /** The initialized resource bundle.  This is public for shared access. */

    public static final ResourceBundle msgs;

    static {
        ResourceBundle retrievedBundle;

        try { 
            retrievedBundle = ResourceBundle.getBundle(bundleId);
            // throws MissingResourceException

        } catch ( MissingResourceException e ) {
            retrievedBundle = null;
        }

        msgs = retrievedBundle;
    }

    // Message access ...
    //
    //    static String getString(String);
    //    static String getString(String, Object);
    //    static String getString(String, Object, Object);
    //    static String getString(String, Object[]);

    protected static String getString(String msgCode)
    {
        if ( msgs == null )
            return bundleId + ":" + msgCode;

        try {
            return WASProduct.msgs.getString(msgCode);

        } catch ( MissingResourceException ex ) {
            return bundleId + ":" + msgCode;
        }
    }

    protected static String getString(String msgCode, Object arg)
    {
        String rawMessage = getString(msgCode);
        Object[] msgArgs = new Object[] { arg };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getString(String msgCode, Object arg1, Object arg2)
    {
        String rawMessage = getString(msgCode);
        Object[] msgArgs = new Object[] { arg1, arg2 };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getString(String msgCode, Object[] msgArgs)
    {
        String rawMessage = getString(msgCode);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Error handling ...
    //
    //    void addException(WASProductException);
    //    public int numExceptions();
    //    public Iterator getExceptions();
    //
    //    void addRecoverableError(SAXParseException);
    //    public int numRecoverableErrors();
    //    public Iterator getRecoverableErrors();
    //
    //    void addWarning(SAXParseException);
    //    public int numWarnings();
    //    public Iterator getWarnings();
    //
    //    void transferErrors(BaseFactory);
    //
    //    void clearExceptions();
    //
    //    void addException(String);
    //    void addException(String, Exception);
    //    void addException(String, Object[], Exception);
    //
    //    static WASProductException createException(String, Object[], Exception);

    protected ArrayList boundExceptions;
    protected ArrayList recoverableErrors;
    protected ArrayList warnings;

    protected void addException(WASProductException e)
    {
        boundExceptions.add(e);
    }

    /**
     * <p>Answer the number of exceptions which were registered
     * within the last operation.</p>
     *
     * @return int The number of registered exceptions.
     */

    public int numExceptions()
    {
        return boundExceptions.size();
    }

    /**
     * <p>Answer an iterator across the registered exceptions.</p>
     *
     * @return Iterator An iterator across the registered exceptions.
     */

    public Iterator getExceptions()
    {
        return boundExceptions.iterator();
    }

    protected void addRecoverableError(SAXParseException e)
    {
        recoverableErrors.add(e);
    }

    /**
     * <p> Answer the number of recoverable errors which were
     * registered within the last operation.</p>
     *
     * @return int The number of registered recoverable errors.
     */

    public int numRecoverableErrors()
    {
        return recoverableErrors.size();
    }

    /**
	 * <p>Answer an iterator across the registered recoverable errors.</p>
	 * @return  Iterator An iterator across the registered recoverable  errors.
	 * @uml.property  name="recoverableErrors"
	 */

    public Iterator getRecoverableErrors()
    {
        return recoverableErrors.iterator();
    }

    protected void addWarning(SAXParseException e)
    {
        warnings.add(e);
    }

    /**
     * <p> Answer the number of warnings which were
     * registered within the last operation.</p>
     *
     * @return int The number of registered warnings.
     */

    public int numWarnings()
    {
        return warnings.size();
    }

    /**
	 * <p>Answer an iterator across the registered warnings.</p>
	 * @return  Iterator An iterator across the registered warnings.
	 * @uml.property  name="warnings"
	 */

    public Iterator getWarnings()
    {
        return warnings.iterator();
    }

    protected void transferErrors(BaseFactory factory)
    {
        Iterator errors = factory.getRecoverableErrors();

        while ( errors.hasNext() ) {
            SAXParseException nextError =
                (SAXParseException) errors.next();
            addRecoverableError(nextError);
        }

        Iterator warnings = factory.getWarnings();

        while ( warnings.hasNext() ) {
            SAXParseException nextWarning =
                (SAXParseException) warnings.next();
            addWarning(nextWarning);
        }
    }

    protected void clearExceptions()
    {
        boundExceptions = new ArrayList();
        recoverableErrors = new ArrayList();
        warnings = new ArrayList();
    }

    protected void addException(String msgKey)
    {
        addException(createException(msgKey, null, null));
    }

    protected void addException(String msgKey, Exception e)
    {
        addException(createException(msgKey, null, e));
    }

    protected void addException(String msgKey, Object[] msgArgs, Exception e)
    {
        addException(createException(msgKey, msgArgs, e));
    }

    protected static WASProductException createException(String msgKey, Object[] msgArgs, Exception e)
    {
        return new WASProductException(msgKey, msgArgs, e);
    }

    // Version directory selection ...
    //
    //    public static String computeVersionDirName() throws WASProductException;
    //
    //    public static String computeDTDDirName(String) throws WASProductException;
    //    public static String getDTDDirName(String);
    //
    //    public static String computeLogDirName(String) throws WASProductException;
    //    public static String getLogDirName(String);
    //
    //    public static String computeBackupDirName(String) throws WASProductException;
    //    public static String getBackupDirName(String);
    //
    //    public static String getTmpDirName();
    //
    //    public static String preparePath(String, boolean);

    /** Constants for providing product directories. */

    public static final String INSTALL_ROOT_PROPERTY_NAME = "was.install.root" ;
    // Version directory selection ...
    //
    //    public static String computeVersionDirName() throws WASProductException;
    //
    //    public static String computeDTDDirName(String) throws WASProductException;
    //    public static String getDTDDirName(String);
    //
    //    public static String computeLogDirName(String) throws WASProductException;
    //    public static String getLogDirName(String);
    //
    //    public static String computeBackupDirName(String) throws WASProductException;
    //    public static String getBackupDirName(String);
    //
    //    public static String getTmpDirName();
    //
    //    public static String preparePath(String, boolean);

    /** Constants for providing product directories. */

    public static final String VERSION_DIR_PROPERTY_NAME = "was.version.dir" ;
    // Version directory selection ...
    //
    //    public static String computeVersionDirName() throws WASProductException;
    //
    //    public static String computeDTDDirName(String) throws WASProductException;
    //    public static String getDTDDirName(String);
    //
    //    public static String computeLogDirName(String) throws WASProductException;
    //    public static String getLogDirName(String);
    //
    //    public static String computeBackupDirName(String) throws WASProductException;
    //    public static String getBackupDirName(String);
    //
    //    public static String getTmpDirName();
    //
    //    public static String preparePath(String, boolean);

    /** Constants for providing product directories. */

    public static final String DTD_DIR_PROPERTY_NAME = "was.version.dtd.dir" ;
    // Version directory selection ...
    //
    //    public static String computeVersionDirName() throws WASProductException;
    //
    //    public static String computeDTDDirName(String) throws WASProductException;
    //    public static String getDTDDirName(String);
    //
    //    public static String computeLogDirName(String) throws WASProductException;
    //    public static String getLogDirName(String);
    //
    //    public static String computeBackupDirName(String) throws WASProductException;
    //    public static String getBackupDirName(String);
    //
    //    public static String getTmpDirName();
    //
    //    public static String preparePath(String, boolean);

    /** Constants for providing product directories. */

    public static final String LOG_DIR_PROPERTY_NAME = "was.version.log.dir" ;
    // Version directory selection ...
    //
    //    public static String computeVersionDirName() throws WASProductException;
    //
    //    public static String computeDTDDirName(String) throws WASProductException;
    //    public static String getDTDDirName(String);
    //
    //    public static String computeLogDirName(String) throws WASProductException;
    //    public static String getLogDirName(String);
    //
    //    public static String computeBackupDirName(String) throws WASProductException;
    //    public static String getBackupDirName(String);
    //
    //    public static String getTmpDirName();
    //
    //    public static String preparePath(String, boolean);

    /** Constants for providing product directories. */

    public static final String BACKUP_DIR_PROPERTY_NAME = "was.version.backup.dir" ;
    // Version directory selection ...
    //
    //    public static String computeVersionDirName() throws WASProductException;
    //
    //    public static String computeDTDDirName(String) throws WASProductException;
    //    public static String getDTDDirName(String);
    //
    //    public static String computeLogDirName(String) throws WASProductException;
    //    public static String getLogDirName(String);
    //
    //    public static String computeBackupDirName(String) throws WASProductException;
    //    public static String getBackupDirName(String);
    //
    //    public static String getTmpDirName();
    //
    //    public static String preparePath(String, boolean);

    /** Constants for providing product directories. */

    public static final String TMP_DIR_PROPERTY_NAME = "was.version.tmp.dir" ;
    // Version directory selection ...
    //
    //    public static String computeVersionDirName() throws WASProductException;
    //
    //    public static String computeDTDDirName(String) throws WASProductException;
    //    public static String getDTDDirName(String);
    //
    //    public static String computeLogDirName(String) throws WASProductException;
    //    public static String getLogDirName(String);
    //
    //    public static String computeBackupDirName(String) throws WASProductException;
    //    public static String getBackupDirName(String);
    //
    //    public static String getTmpDirName();
    //
    //    public static String preparePath(String, boolean);

    /** Constants for providing product directories. */

    public static final String JAVA_TMP_DIR_PROPERTY_NAME = "java.io.tmpdir" ;

    /** Default names for product directories. */

    public static final String PROPERTIES_DIR_NAME = "properties" ;
    /** Default names for product directories. */

    public static final String VERSION_DIR_NAME = "version" ;
    /** Default names for product directories. */

    public static final String DTD_DIR_NAME = "dtd" ;
    /** Default names for product directories. */

    public static final String LOG_DIR_NAME = "log" ;
    /** Default names for product directories. */

    public static final String BACKUP_DIR_NAME = "backup" ;
    /** Default names for product directories. */

    public static final String DEFAULT_TMP_DIR_NAME = "tmp" ;

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

    public static String computeProductDirName()
        throws WASProductException
    {
        String productDirName = System.getProperty(INSTALL_ROOT_PROPERTY_NAME);

        if ( productDirName == null )
            throw createException("WVER0001E", null, null);

        productDirName = preparePath(productDirName, false);

        return productDirName;
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

    public static String computeVersionDirName(String productDirName)
    {
        String versionDirName = System.getProperty(VERSION_DIR_PROPERTY_NAME);

        if ( versionDirName == null )
            versionDirName = getVersionDirName(productDirName);
        else
            versionDirName = preparePath(versionDirName, false);

        return versionDirName;
    }

    /**
     * <p>Answer a version directory based on the argument product
     * directory.</p>
     *
     * @return String The computed version directory name.
     */

    public static String getVersionDirName(String productDirName)
    {
        productDirName = preparePath(productDirName, false);

        return
            productDirName + File.separator +
            PROPERTIES_DIR_NAME + File.separator +
            VERSION_DIR_NAME;
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

    public static String computeDTDDirName(String versionDirName)
        throws WASProductException
    {
        String dtdDirName = System.getProperty(DTD_DIR_PROPERTY_NAME);

        if ( dtdDirName == null ) {
            if ( versionDirName == null )
                throw createException("WVER0019", null, null); // throws WASProductException

            dtdDirName = getDTDDirName(versionDirName);

        } else {
            dtdDirName = preparePath(dtdDirName, false);
        }

        return dtdDirName;
    }

    /**
     * <p>Answer a DTD directory based on the argument version
     * directory.</p>
     *
     * @return String The computed DTD directory name.
     */

    public static String getDTDDirName(String versionDirName)
    {
        versionDirName = preparePath(versionDirName, false);

        return versionDirName + File.separator + DTD_DIR_NAME;
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

    public static String computeLogDirName(String versionDirName)
        throws WASProductException
    {
        String logDirName = System.getProperty(LOG_DIR_PROPERTY_NAME);

        if ( logDirName == null ) {
            if ( versionDirName == null )
                throw createException("WVER0017", null, null); // throws WASProductException

            logDirName = getLogDirName(versionDirName);

        } else {
            logDirName = preparePath(logDirName, false);
        }

        return logDirName;
    }

    /**
     * <p>Answer a log directory based on the argument version
     * directory.</p>
     *
     * @return String The computed log directory name.
     */

    public static String getLogDirName(String versionDirName)
    {
        versionDirName = preparePath(versionDirName, false);

        return versionDirName + File.separator + LOG_DIR_NAME;
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

    public static String computeBackupDirName(String versionDirName)
        throws WASProductException
    {
        String backupDirName = System.getProperty(BACKUP_DIR_PROPERTY_NAME);

        if ( backupDirName == null ) {
            if ( versionDirName == null )
                throw createException("WVER0018", null, null); // throws WASProductException

            backupDirName = getBackupDirName(versionDirName);

        } else {
            backupDirName = preparePath(backupDirName, false);
        }

        return backupDirName;
    }

    /**
     * <p>Answer a backup directory based on the argument version
     * directory.</p>
     *
     * @return String The computed backup directory name.
     */

    public static String getBackupDirName(String versionDirName)
    {
        versionDirName = preparePath(versionDirName, false);

        return versionDirName + File.separator + BACKUP_DIR_NAME;
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
        String tmpDirName = System.getProperty(TMP_DIR_PROPERTY_NAME);

        if ( tmpDirName == null ) {
            tmpDirName = System.getProperty(JAVA_TMP_DIR_PROPERTY_NAME);

            if ( tmpDirName == null )
                tmpDirName = DEFAULT_TMP_DIR_NAME;
        }

        tmpDirName = preparePath(tmpDirName, false);

        return tmpDirName;
    }

    /**
     * <p>Utility method for replacing slashes in the argument path
     * with the file separator character.  Optionally, a trailing
     * separator is added to (or kept off of) the result path.</p>
     *
     * @param path The argument path to be processed.
     * @param trailSeparator A flag specifying if the result path is to
     *                       have a trailing separator character.
     *
     * @return String The processed path.
     */

    public static String preparePath(String path, boolean trailSeparator)
    {
        path = path.replace('/',  File.separatorChar);
        path = path.replace('\\', File.separatorChar);

        if ( path.endsWith(File.separator) ) {
            if ( !trailSeparator )
                path = path.substring(0, path.length() - File.separator.length());

        } else { // ( !path.endsWith(File.separator) )
            if ( trailSeparator )
                path += File.separator;
        }

        return path;
    }

    // Instance state ...
    //
    //    public WASProduct();
    //    public WASProduct(String);
    //    public WASProduct(String, String, String, String);
    //
    //    void initialize(String, String, String, String);

    // Upon construction, the WASProduct instance may have a
    // bound exception.

    // Set the directories according to properties and according to
    // standard computations.

    /**
     * <p>Default constructor.  The new instance is initialized
     * wholly from system properties.  Exceptions may be registed
     * in the new instance.</p>
     */

    public WASProduct()
    {
        clearExceptions();

        try {
            String
                productDirName = computeProductDirName(),               // throws WASProductException
                versionDirName = computeVersionDirName(productDirName), // throws WASProductException
                dtdDirName     = computeDTDDirName(versionDirName),     // throws WASProductException
                logDirName     = computeLogDirName(versionDirName),     // throws WASProductException
                backupDirName  = computeBackupDirName(versionDirName);  // throws WASProductException

            initialize(productDirName, versionDirName, dtdDirName, logDirName, backupDirName);

        } catch ( WASProductException e ) {
            addException(e);
        }
    }

    /**
     * <p>Parameterized constructor.  The product directories are
     * computed from the specified directory.  No system properties
     * are used.  Exceptions may be registed in the new instance.</p>
     *
     * @param productDirName The base product directory.
     */

    public WASProduct(String productDirName)
    {
        clearExceptions();

        try {
            String
                versionDirName = computeVersionDirName(productDirName), // throws WASProductException
                dtdDirName     = computeDTDDirName    (versionDirName), // throws WASProductException
                logDirName     = computeLogDirName    (versionDirName), // throws WASProductException
                backupDirName  = computeBackupDirName (versionDirName); // throws WASProductException

            initialize(productDirName, versionDirName, dtdDirName, logDirName, backupDirName);

        } catch ( WASProductException e ) {
            addException(e);
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

    // None of the dir names may be null.

    public WASProduct(String productDirName,
                      String versionDirName,
                      String dtdDirName,
                      String logDirName,
                      String backupDirName)
    {
        clearExceptions();

        initialize(productDirName, versionDirName, dtdDirName, logDirName, backupDirName);
    }

    protected void initialize(String useProductDirName,
                              String useVersionDirName,
                              String useDTDDirName,
                              String useLogDirName,
                              String useBackupDirName)
    {
        productDir     = new File(useProductDirName);
        productDirName = productDir.getAbsolutePath();

        if ( !productDir.exists() )
            addException("WVER0048E", new Object[] { productDirName }, null);
        else if ( !productDir.isDirectory() )
            addException("WVER0049E", new Object[] { productDirName }, null);

        versionDir     = new File(useVersionDirName);
        versionDirName = versionDir.getAbsolutePath();

        if ( !versionDir.exists() )
            addException("WVER0040E", new Object[] { versionDirName }, null);
        else if ( !versionDir.isDirectory() )
            addException("WVER0041E", new Object[] { versionDirName }, null);

        dtdDir         = new File(useDTDDirName);
        dtdDirName     = dtdDir.getAbsolutePath();

        if ( !dtdDir.exists() )
            addException("WVER0042E", new Object[] { dtdDirName }, null);
        else if ( !dtdDir.isDirectory() )
            addException("WVER0043E", new Object[] { dtdDirName }, null);

        // Don't do any checking on these; they will not
        // be needed unless updates are performed.

        logDir         = new File(useLogDirName);
        logDirName     = logDir.getAbsolutePath();

        backupDir      = new File(useBackupDirName);
        backupDirName  = backupDir.getAbsolutePath();

        versionInfo    = new HashMap();
    }

    /**
     * <p>Initialize the log and backup directories associated
     * with the receiver.  If necessary, these directories are
     * created, and are verified to exist as directories.</p>
     *
     * <p>This method clears and will register new exceptions,
     * as needed.</p>
     *
     * <p>The result indicates whether the log and backup
     * directories were successfully initialized.</p>
     *
     * @return boolean A flag telling if the log and backup
     *                 directories were successfully initialized.
     */

    public boolean initializeLogAndBackup()
    {
        clearExceptions();

        boolean result = true;

        if ( !logDir.exists() ) {
            if ( !logDir.mkdirs() ) {
                addException("WVER0044E", new Object[] { logDirName }, null);
                result = false;
            }
        } else {
            if ( !logDir.isDirectory() ) {
                addException("WVER0045E", new Object[] { logDirName }, null);
                result = false;
            }
        }

        if ( !backupDir.exists() ) {
            if ( !backupDir.mkdirs() ) {
                addException("WVER0046E", new Object[] { backupDirName }, null);
                result = false;
            }
        } else {
            if ( !backupDir.isDirectory() ) {
                addException("WVER0047E", new Object[] { backupDirName }, null);
                result = false;
            }
        }
            
        return result;
    }

    // Version Information Retrieval ...
    //
    //    public websphere getPlatform();
    //
    //    public product getProductByFilename(String);
    //    public product getProductById(String);
    //    public boolean productPresent(String);
    //
    //    public boolean addProduct(product);
    //    public boolean removeProduct(product);
    //
    //    public component getComponentByFilename(String);
    //    public component getComponentByName(String);
    //    public boolean componentPresent(String);
    //
    //    public boolean addComponent(component);
    //    public boolean removeComponent(component);
    //
    //    public efix getEFixByFilename(String);
    //    public efix getEFixById(String);
    //    public boolean efixPresent(String);
    //
    //    public ptf getPTFByFilename(String);
    //    public ptf getPTFById(String);
    //    public boolean ptfPresent(String);
    //
    //    public extension getExtensionByFilename(String);
    //    public extension getExtensionByName(String);
    //    public boolean extensionPresent(String);

    /**
     * <p>Answer the websphere object, which is representative of
     * a websphere installation.</p>
     *
     * @return websphere The websphere object associated with the
     *                   receiver.
     */

    public websphere getPlatform()
    {
        return (websphere) getVersionInfo(WebSphereHandler.WEBSPHERE_FILE_NAME);
    }

    /**
     * <p>Answer the product stored in the named product file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding a product
     *                 object.
     *
     * @return product The product in the named file.
     */

    public product getProductByFilename(String basename)
    {
        if ( !ProductHandler.accepts(basename) )
            return null;
        else
            return (product) getVersionInfo(basename);
    }

    /**
     * <p>Answer the product having the specified id.  Answer
     * null if no matching product is available.</p>
     *
     * @param id The id of the product to be returned.
     *
     * @return product The product having the specified id.
     */

    public product getProductById(String id)
    {
        product selectedProduct = null;

        Iterator products = getProducts();

        while ( (selectedProduct == null) && products.hasNext() ) {
            product nextProduct = (product) products.next();
            if ( nextProduct.getId().equals(id) )
                selectedProduct = nextProduct;
        }

        return selectedProduct;
    }

    public product getProductByIdTest(String id)
    {
        product selectedProduct = null;

        Iterator products = getProducts();

        while ( products.hasNext() ) {
            product nextProduct = (product) products.next();
            if ( nextProduct.getId().equals(id) )
                selectedProduct = nextProduct;
        }

         return selectedProduct;
    }


    /**
     * <p>Answer true or false, telling if a product having the
     * specified id is present.</p>
     *
     * @param id The id of the product which is tested.
     *
     * @return boolean True or false, telling if the product having
     *                 the specified id is present.
     */

    public boolean productPresent(String id)
    {
        return ( getProductById(id) != null );
    }

    /**
     * <p>Save the argument product into the bound product directory.
     * Answer true or false, telling if the product was successfully
     * saved.</p>
     *
     * @param aProduct A product to be saved.
     *
     * @return True or false, telling if the product was successfully saved.
     */

    public boolean addProduct(product aProduct)
    {
        clearExceptions();
	
        String baseName = aProduct.getId() + ".product";
        String fullName = getVersionFileName( baseName );

        BaseHandlerException exception =
            BaseFactory.saveSingleton(aProduct, new ProductWriter(), fullName);

        if ( exception == null ) {
            addProductName(baseName);
            basicAddProduct(aProduct);
            return true;

        } else {
            addException("WVER0021E",
                         new String[] { aProduct.getId(), fullName },
                         exception);
            return false;
        }
    }

    public boolean addProduct(List productList)
    {
        boolean bOK = true;
        for(int i=0; i < productList.size(); i++)
        {
            if(!addProduct((product) productList.get(i)))
                bOK = false;
        }
         return bOK;
    }
    /**
     * <p>Remove the argument product from the bound product directory.
     * Answer true or false, telling if the product was successfully
     * removed.</p>
     *
     * @param aProduct A product to be removed.
     *
     * @return True or false, telling if the product was successfully removed.
     */

    public boolean removeProduct(product aProduct)
    {
        clearExceptions();

        String baseName = aProduct.getId() + ".product";
        String fullName = getVersionFileName(baseName);

        File productFile = new File(fullName);
        boolean result = productFile.delete();

        if ( result ) {
            removeProductName(baseName);
            basicRemoveProduct(aProduct);

        } else {
            addException("WVER0029E",
                         new String[] { aProduct.getId(), fullName },
                         null);
        }

        return result;
    }

    /**
     * <p>Answer the component stored in the named component file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding a
     *                 component object.
     *
     * @return component The component stored in the specified file.
     */

    public component getComponentByFilename(String basename)
    {
        if ( !ComponentHandler.accepts(basename) )
            return null;
        else
            return (component) getVersionInfo(basename);
    }

    /**
     * <p>Answer the component having the specified name.  Answer
     * null if no matching component is available.</p>
     *
     * @param name The name of the component to be returned.
     *
     * @return component The component having the specified name.
     */

    public component getComponentByName(String name)
    {
        component selectedComponent = null;

        Iterator components = getComponents();

        while ( (selectedComponent == null) && components.hasNext() ) {
            component nextComponent = (component) components.next();
            if ( nextComponent.getName().equals(name) )
                selectedComponent = nextComponent;
        }

        return selectedComponent;
    }

    /**
     * <p>Answer true or false, telling if a component having the
     * specified name is present.</p>
     *
     * @param name The name of the component which is tested.
     *
     * @return boolean True or false, telling if the component having
     *                 the specified name is present.
     */

    public boolean componentPresent(String name)
    {
        return ( getComponentByName(name) != null );
    }

    /**
     * <p>Save the argument component into the bound component directory.
     * Answer true or false, telling if the component was successfully
     * saved.</p>
     *
     * @param aComponent A component to be saved.
     *
     * @return True or false, telling if the component was successfully saved.
     */

    public boolean addComponent(component aComponent)
    {
        clearExceptions();

        String baseName = aComponent.getName() + ".component";
        String fullName = getVersionFileName( baseName );

        BaseHandlerException exception =
            BaseFactory.saveSingleton(aComponent, new ComponentWriter(), fullName);

        if ( exception == null ) { 
            boolean alreadyExists = false;
       	    Iterator componentNames = getComponentNames();
       	    while(componentNames.hasNext() && !alreadyExists){
       	    	String aComponentName = (String) componentNames.next();	
       	    	if(aComponentName.equals(baseName)){
       	    	    alreadyExists = true;	
       	    	}
       	    }
       	    
       	    if(!alreadyExists){
  		addComponentName(baseName);
            	basicAddComponent(aComponent);            
            }
            
            return true;

        } else {
            addException("WVER0021E",
                         new String[] { aComponent.getName(), fullName },
                         exception);
            
            return false;
        }
    }

    /**
     * <p>Remove the argument component from the bound component directory.
     * Answer true or false, telling if the component was successfully
     * removed.</p>
     *
     * @param aComponent A component to be removed.
     *
     * @return True or false, telling if the component was successfully removed.
     */

    public boolean removeComponent(component aComponent)
    {
        clearExceptions();

        String baseName = aComponent.getName() + ".component";
        String fullName = getVersionFileName(baseName);

        File componentFile = new File(fullName);
        boolean result = componentFile.delete();

        if ( result ) {
            removeComponentName(baseName);
            basicRemoveComponent(aComponent);

        } else {
            addException("WVER0029E",
                         new String[] { aComponent.getName(), fullName },
                         null);
        }

        return result;
    }

    /**
     * <p>Answer the efix stored in the named efix file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding an efix
     *                 object.
     *
     * @return efix The efix stored in the specified file.
     */

    public efix getEFixByFilename(String basename)
    {
        if ( !EFixHandler.acceptsEFixFileName(basename) )
            return null;
        else
            return (efix) getVersionInfo(basename);
    }

    /**
     * <p>Answer the efix having the specified id.  Answer
     * null if no matching efix is available.</p>
     *
     * @param id The id of the efix to be returned.
     *
     * @return efix The efix having the specified id.
     */

    public efix getEFixById(String id)
    {
        efix selectedEFix = null;

        Iterator efixes = getEFixes();

        while ( (selectedEFix == null) && efixes.hasNext() ) {
            efix nextEFix = (efix) efixes.next();
            if ( nextEFix.getId().equals(id) )
                selectedEFix = nextEFix;
        }

        return selectedEFix;
    }

    /**
     * <p>Answer true or false, telling if a efix having the
     * specified name is present.</p>
     *
     * @param name The name of the efix which is tested.
     *
     * @return boolean True or false, telling if the efix having
     *                 the specified name is present.
     */

    public boolean efixPresent(String id)
    {
        return ( getEFixById(id) != null );
    }

    /**
     * <p>Answer the PTF stored in the named PTF file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding an PTF
     *                 object.
     *
     * @return PTF The PTF stored in the specified file.
     */

    public ptf getPTFByFilename(String basename)
    {
        if ( !EFixHandler.acceptsPTFFileName(basename) )
            return null;
        else
            return (ptf) getVersionInfo(basename);
    }

    /**
     * <p>Answer the PTF having the specified id.  Answer
     * null if no matching PTF is available.</p>
     *
     * @param id The id of the PTF to be returned.
     *
     * @return PTF The PTF having the specified id.
     */

    public ptf getPTFById(String id)
    {
        ptf selectedPTF = null;

        Iterator ptfs = getPTFs();

        while ( (selectedPTF == null) && ptfs.hasNext() ) {
            ptf nextPTF = (ptf) ptfs.next();
            if ( nextPTF.getId().equals(id) )
                selectedPTF = nextPTF;
        }

        return selectedPTF;
    }

    /**
     * <p>Answer true or false, telling if a PTF having the
     * specified name is present.</p>
     *
     * @param name The name of the PTF which is tested.
     *
     * @return boolean True or false, telling if the PTF having
     *                 the specified name is present.
     */

    public boolean ptfPresent(String id)
    {
        return ( getPTFById(id) != null );
    }

    /**
     * <p>Answer the extension stored in the named extension file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding an extension
     *                 object.
     *
     * @return extension The extension stored in the specified file.
     */

    public extension getExtensionByFilename(String basename)
    {
        if ( !ExtensionHandler.accepts(basename) )
            return null;
        else
            return (extension) getVersionInfo(basename);
    }

    /**
     * <p>Answer the extension having the specified name.  Answer
     * null if no matching extension is available.</p>
     *
     * @param name The name of the extension to be returned.
     *
     * @return extension The extension having the specified name.
     */

    public extension getExtensionByName(String name)
    {
        extension selectedExtension = null;

        Iterator extensions = getExtensions();

        while ( (selectedExtension == null) && extensions.hasNext() ) {
            extension nextExtension = (extension) extensions.next();
            if ( nextExtension.getName().equals(name) )
                selectedExtension = nextExtension;
        }

        return selectedExtension;
    }

    /**
     * <p>Answer true or false, telling if an extension having the
     * specified name is present.</p>
     *
     * @param name The name of the extension which is tested.
     *
     * @return boolean True or false, telling if the extension having
     *                 the specified name is present.
     */

    public boolean extensionPresent(String name)
    {
        return ( getExtensionByName(name) == null );
    }

    // VersionInfo entry lists ...
    //
    //    public Iterator getProducts();
    //    ArrayList basicGetProducts();
    //    void basicAddProduct(product);
    //    void basicRemoveProduct(product);
    //    public Iterator getProductNames();
    //    ArrayList basicGetProductNames();
    //    void addProductName();
    //    void removeProductName();
    //
    //    public Iterator getComponents();
    //    ArrayList basicGetComponents();
    //    void basicAddComponent(component);
    //    void basicRemoveComponent(component);
    //    public Iterator getComponentNames();
    //    ArrayList basicGetComponentNames();
    //    void addComponentName();
    //    void removeComponentName();
    //
    //    public boolean saveEFix(efix);
    //    public boolean removeEFix(efix);
    //    void addEFix(efix);
    //    void basicRemoveEFix(efix);
    //    public Iterator getEFixes();
    //    ArrayList basicGetEFixes();
    //    void addEFixName(String);
    //    void removeEFixName(String);
    //    public Iterator getEFixNames();
    //    ArrayList basicGetEFixNames();
    //
    //    public boolean savePTF(ptf);
    //    public boolean removePTF(ptf);
    //    void addPTF(ptf);
    //    void basicRemovePTF(ptf);
    //    public Iterator getPTFs();
    //    ArrayList basicGetPTFs();
    //    void addPTFName(String);
    //    void removePTFName(String);
    //    public Iterator getPTFNames();
    //    ArrayList basicGetPTFNames();
    //
    //    public Iterator getExtensions();
    //    ArrayList basicGetExtensions();
    //    public Iterator getExtensionNames();
    //    ArrayList basicGetExtensionNames();

    // Note that the returned lists will be partial
    // if an error occurred during the load.

    protected ArrayList products = null;

    /**
	 * <p>Answer an iterator across the products in the associated product directory.</p>
	 * @return  Iterator An iterator across the products in the  associated product directory.
	 * @uml.property  name="products"
	 */

    public Iterator getProducts()
    {
        if ( products == null )
            products = basicGetProducts();

        return products.iterator();
    }

    protected ArrayList basicGetProducts()
    {
        Iterator productNames = getProductNames();

        return collectVersionInfo(productNames);
    }

    protected void basicAddProduct(product aProduct)
    {
        products.add(aProduct);
    }

    protected void basicRemoveProduct(product aProduct)
    {
        products.remove( products.indexOf(aProduct) );
    }

    protected ArrayList productNames = null;

    /**
	 * <p>Answer an iterator across the names of the products in the associated product directory.</p>
	 * @return  Iterator An iterator across the names of the products  in the associated product directory.
	 * @uml.property  name="productNames"
	 */

    public Iterator getProductNames()
    {
        if ( productNames == null )
            productNames = basicGetProductNames();

        return productNames.iterator();
    }

    protected ArrayList basicGetProductNames()
    {
        return listFileNames(new ProductFilenameFilter());
    }

    protected void addProductName(String productName)
    {
        productNames.add(productName);
    }

    protected void removeProductName(String productName)
    {
        productNames.remove( productNames.indexOf(productName) );
    }

    protected ArrayList components = null;

    /**
	 * <p>Answer an iterator across the components in the associated product directory.</p>
	 * @return  Iterator An iterator across the components in the  associated product directory.
	 * @uml.property  name="components"
	 */

    public Iterator getComponents()
    {
        if ( components == null )
            components = basicGetComponents();

        return components.iterator();
    }

    protected void basicAddComponent(component aComponent)
    {
        components.add(aComponent);
    }

    protected void basicRemoveComponent(component aComponent)
    {
        components.remove( components.indexOf(aComponent) );
    }

    protected ArrayList basicGetComponents()
    {
        Iterator componentNames = getComponentNames();

        return collectVersionInfo(componentNames);
    }

    protected ArrayList componentNames = null;

    /**
	 * <p>Answer an iterator across the names of the components in the associated product directory.</p>
	 * @return  Iterator An iterator across the names of the components  in the associated product directory.
	 * @uml.property  name="componentNames"
	 */

    public Iterator getComponentNames()
    {
        if ( componentNames == null )
            componentNames = basicGetComponentNames();

        return componentNames.iterator();
    }

    protected ArrayList basicGetComponentNames()
    {
        return listFileNames(new ComponentFilenameFilter());
    }

    protected void addComponentName(String componentName)
    {
        componentNames.add(componentName);
    }

    protected void removeComponentName(String componentName)
    {
        componentNames.remove( componentNames.indexOf(componentName) );
    }

    /**
     * <p>Save the argument efix into the bound product directory.
     * Answer true or false, telling if the efix was successfully
     * saved.</p>
     *
     * @param anEFix A efix to be saved.
     *
     * @return True or false, telling if the efix was successfully saved.
     */

    public boolean saveEFix(efix anEFix)
    {
        clearExceptions();

        String baseName = anEFix.getStandardFileName();
        String fullName = getVersionFileName( baseName );

        BaseHandlerException exception =
            BaseFactory.saveSingleton(anEFix, new EFixWriter(), fullName);

        if ( exception == null ) {
            addEFixName(baseName);
            addEFix(anEFix);
            return true;

        } else {
            addException("WVER0021E",
                         new String[] { anEFix.getId(), fullName },
                         exception);
            return false;
        }
    }

    /**
     * <p>Remove the argument efix from the bound product directory.
     * Answer true or false, telling if the efix was successfully
     * removed.</p>
     *
     * @param anEFix A efix to be removed.
     *
     * @return True or false, telling if the efix was successfully removed.
     */

    public boolean removeEFix(efix anEFix)
    {
        clearExceptions();

        String baseName = anEFix.getStandardFileName();
        String fullName = getVersionFileName(baseName);

        File efixFile = new File(fullName);
        boolean result = efixFile.delete();

        if ( result ) {
            removeEFixName(baseName);
            basicRemoveEFix(anEFix);

        } else {
            addException("WVER0029E",
                         new String[] { anEFix.getId(), fullName },
                         null);
        }

        return result;
    }

    protected ArrayList efixes = null;

    protected void addEFix(efix anEFix)
    {
        efixes.add(anEFix);
    }

    protected void basicRemoveEFix(efix anEFix)
    {
        efixes.remove( efixes.indexOf(anEFix) );
    }

    /**
     * <p>Answer an iterator across the efixes in the
     * associated product directory.</p>
     *
     * @return Iterator An iterator across the efixes in the
     *                  associated product directory.
     */

    public Iterator getEFixes()
    {
        if ( efixes == null )
            efixes = basicGetEFixes();

        return efixes.iterator();
    }

    /**
     * <p>Answer the count of efixes in the bound product
     * directory.</p>
     *
     * @return int The count of efixes in the bound product
     *             directory.</p>
     */

    public int getEFixCount()
    {
        return efixes.size();
    }

    protected ArrayList basicGetEFixes()
    {
        Iterator efixNames = getEFixNames();

        return collectVersionInfo(efixNames);
    }

    protected ArrayList efixNames = null;

    protected void addEFixName(String efixName)
    {
        efixNames.add(efixName);
    }

    protected void removeEFixName(String efixName)
    {
        efixNames.remove( efixNames.indexOf(efixName) );
    }

    /**
     * <p>Answer an iterator across the ids of the efixes
     * in the associated product directory.</p>
     *
     * @return Iterator An iterator across the ids of the efixes
     *                  in the associated product directory.
     */

    public Iterator getEFixNames()
    {
        if ( efixNames == null )
            efixNames = basicGetEFixNames();

        return efixNames.iterator();
    }

    protected ArrayList basicGetEFixNames()
    {
        return listFileNames(new EFixFilenameFilter());
    }

    /**
     * <p>Save the argument PTF into the bound product directory.
     * Answer true or false, telling if the PTF was successfully
     * saved.</p>
     *
     * @param aPTF A PTF to be saved.
     *
     * @return True or false, telling if the PTF was successfully saved.
     */

    public boolean savePTF(ptf aPTF)
    {
        clearExceptions();

        String baseName = aPTF.getStandardFileName();
        String fullName = getVersionFileName(baseName);

        BaseHandlerException exception =
            BaseFactory.saveSingleton(aPTF, new EFixWriter(), fullName);

        if ( exception == null ) {
            addPTFName(baseName);
            addPTF(aPTF);
            return true;

        } else {
            addException("WVER0030E",
                         new String[] { aPTF.getId(), fullName },
                         exception);
            return false;
        }
    }

    /**
     * <p>Remove the argument PTF from the bound product directory.
     * Answer true or false, telling if the PTF was successfully
     * removed.</p>
     *
     * @param aPTF A PTF to be removed.
     *
     * @return True or false, telling if the PTF was successfully removed.
     */

    public boolean removePTF(ptf aPTF)
    {
        clearExceptions();

        String baseName = aPTF.getStandardFileName();
        String fullName = getVersionFileName(baseName);

        File ptfFile = new File(fullName);
        boolean result = ptfFile.delete();

        if ( result ) {
            removePTFName(baseName);
            basicRemovePTF(aPTF);

        } else {
            addException("WVER0035E",
                         new String[] { aPTF.getId(), fullName },
                         null);
        }

        return result;
    }

    protected ArrayList ptfs = null;

    protected void addPTF(ptf aPTF)
    {
        ptfs.add(aPTF);
    }

    protected void basicRemovePTF(ptf aPTF)
    {
        ptfs.remove( ptfs.indexOf(aPTF) );
    }

    /**
     * <p>Answer an iterator across the PTFs in the
     * associated product directory.</p>
     *
     * @return Iterator An iterator across the PTFs in the
     *                  associated product directory.
     */

    public Iterator getPTFs()
    {
        if ( ptfs == null )
            ptfs = basicGetPTFs();

        return ptfs.iterator();
    }

    protected ArrayList basicGetPTFs()
    {
        Iterator ptfNames = getPTFNames();

        return collectVersionInfo(ptfNames);
    }

    protected ArrayList ptfNames = null;

    protected void addPTFName(String ptfName)
    {
        ptfNames.add(ptfName);
    }

    protected void removePTFName(String ptfName)
    {
        ptfNames.remove( ptfNames.indexOf(ptfName) );
    }

    /**
     * <p>Answer an iterator across the ids of the PTFs
     * in the associated product directory.</p>
     *
     * @return Iterator An iterator across the ids of the PTFs
     *                  in the associated product directory.
     */

    public Iterator getPTFNames()
    {
        if ( ptfNames == null )
            ptfNames = basicGetPTFNames();

        return ptfNames.iterator();
    }

    protected ArrayList basicGetPTFNames()
    {
        return listFileNames(new PTFFilenameFilter());
    }

    protected ArrayList extensions = null;

    /**
	 * <p>Answer an iterator across the extensions in the associated product directory.</p>
	 * @return  Iterator An iterator across the extensions in the  associated product directory.
	 * @uml.property  name="extensions"
	 */

    public Iterator getExtensions()
    {
        if ( extensions == null )
            extensions = basicGetExtensions();

        return extensions.iterator();
    }

    protected ArrayList basicGetExtensions()
    {
        Iterator extensionNames = getExtensionNames();

        return collectVersionInfo(extensionNames);
    }

    protected ArrayList extensionNames = null;

    /**
	 * <p>Answer an iterator across the names of the extensions in the associated product directory.</p>
	 * @return  Iterator An iterator across the names of the extensions  in the associated product directory.
	 * @uml.property  name="extensionNames"
	 */

    public Iterator getExtensionNames()
    {
        if ( extensionNames == null )
            extensionNames = basicGetExtensionNames();

        return extensionNames.iterator();
    }

    protected ArrayList basicGetExtensionNames()
    {
        return listFileNames(new ExtensionFilenameFilter());
    }

    // VersionInfo cache access ...
    //
    //    ArrayList collectVersionInfo(Iterator);
    //    Object getVersionInfo(String);
    //    Object loadVersionInfo(String);

    protected HashMap versionInfo;

    protected ArrayList collectVersionInfo(Iterator names)
    {
        ArrayList collectedInfo = new ArrayList();
        boolean encounteredError = false;

        while ( !encounteredError && names.hasNext() ) {
            String nextName = (String) names.next();

            // The information will be null if an error occurrs.

            Object nextInfo = getVersionInfo(nextName);
            if ( nextInfo != null )
                collectedInfo.add(nextInfo);
        }

        return collectedInfo;
    }

    protected Object getVersionInfo(String filename)
    {
        Object info = versionInfo.get(filename);

        if ( info == null ) {
            info = loadVersionInfo(filename);

            // Will be null if an error occurred during the load.

            if ( info != null )
                versionInfo.put(filename, info);
        }

        return info;
    }

    // Load and return the version information from the specified file
    // in the version directory.
    //
    // Answer null if an error occurred during the load.
    // In this case, an exception will be bound.

    protected Object loadVersionInfo(String filename)
    {
        BaseHandler handler;

        if ( WebSphereHandler.accepts(filename) )
            handler = new WebSphereHandler();
        else if ( ProductHandler.accepts(filename) )
            handler = new ProductHandler();
        else if ( ComponentHandler.accepts(filename) )
            handler = new ComponentHandler();
        else if ( EFixHandler.acceptsEFixFileName(filename) ||
                  EFixHandler.acceptsPTFFileName(filename) )
            handler = new EFixHandler();
        else if ( ExtensionHandler.accepts(filename) )
            handler = new ExtensionHandler();
        else
            handler = null;

        if ( handler == null ) {
            addException("WVER0002E", new Object[] { filename }, null);
            return null;
        }

        // Don't need a writer in this handler.
        BaseFactory factory = new BaseFactory(handler, null);

        //        System.out.println(">> Loading with DTD: " + getDTDDirName());
        //        System.out.println(">> Loading with version dir name: " + getVersionDirName());
        //        System.out.println(">> Loading with file name: " + filename);

        List loaded = factory.load(getDTDDirName(), getVersionDirName(), filename);

        transferErrors(factory);

        BaseHandlerException handlerException = factory.getException();

        if ( handlerException != null ) {
            addException("WVER0013E", new Object[] { filename }, handlerException);
            return null;
        }

        return loaded.iterator().next();
    }

    // Basic version directory access ...
    //
    //    public String[] loadAllVersionInfoByType(String);
    //    public String loadVersionInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    // Don't cache these ... this is special purpose access,
    // which the invoker may cache if desired.

    /**
     * <p>Typing constants for loading version info.</p>
     */

    public static final String PRODUCT_TYPE = "product" ;
    // Basic version directory access ...
    //
    //    public String[] loadAllVersionInfoByType(String);
    //    public String loadVersionInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    // Don't cache these ... this is special purpose access,
    // which the invoker may cache if desired.

    /**
     * <p>Typing constants for loading version info.</p>
     */

    public static final String COMPONENT_TYPE = "component" ;
    // Basic version directory access ...
    //
    //    public String[] loadAllVersionInfoByType(String);
    //    public String loadVersionInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    // Don't cache these ... this is special purpose access,
    // which the invoker may cache if desired.

    /**
     * <p>Typing constants for loading version info.</p>
     */

    public static final String EFIX_TYPE = "efix" ;
    // Basic version directory access ...
    //
    //    public String[] loadAllVersionInfoByType(String);
    //    public String loadVersionInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    // Don't cache these ... this is special purpose access,
    // which the invoker may cache if desired.

    /**
     * <p>Typing constants for loading version info.</p>
     */

    public static final String PTF_TYPE = "ptf" ;
    // Basic version directory access ...
    //
    //    public String[] loadAllVersionInfoByType(String);
    //    public String loadVersionInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    // Don't cache these ... this is special purpose access,
    // which the invoker may cache if desired.

    /**
     * <p>Typing constants for loading version info.</p>
     */

    public static final String EXTENSION_TYPE = "extension" ;

    /**
     * <p>Answer a string array containing the contents of
     * all files in the bound product directory having the
     * specified type.</p>
     *
     * @param type The type of file to load; see the constants
     *             'PRODUCT_TYPE', and others.
     *
     * @return String[] An array holding the contents of the
     *                  files of the specified type.  Each file
     *                  is loaded as a single string.
     */

    public String[] loadAllVersionInfoByType(String type)
    {
        FilenameFilter filter;

        if ( type.equalsIgnoreCase(PRODUCT_TYPE) )
            filter = new ProductFilenameFilter();
        else if ( type.equalsIgnoreCase(COMPONENT_TYPE) )
            filter = new ComponentFilenameFilter();

        else if ( type.equalsIgnoreCase(EFIX_TYPE) )
            filter = new EFixFilenameFilter();
        else if ( type.equalsIgnoreCase(PTF_TYPE) )
            filter = new PTFFilenameFilter();

        else if ( type.equalsIgnoreCase(EXTENSION_TYPE) )
            filter = new ExtensionFilenameFilter();

        else
            filter = null;

        if ( filter == null )
            return null;

        File[] files = listFiles(filter);

        String[] data = new String[files.length];
        for ( int fileNo = 0; fileNo < files.length; fileNo++ )
            data[fileNo] = loadVersionInfoAsXMLString(files[fileNo].getName());

        return data;
    }

    /**
     * <p>Load the contents of the argument file as a string.
     * The entire contents of the file are loaded.  Answer an
     * empty string if the file does not exist.</p>
     *
     * @param filename The name of the file to load.
     *
     * @return String The contents of the named file.
     */

    public String loadVersionInfoAsXMLString(String filename)
    {
        File versionFile = new File(getVersionDir(), filename);
        if ( !versionFile.exists() )
            return "";

        try {
            return loadFile(versionFile, filename);

        } catch ( FileNotFoundException e ) {
            addException("WVER0014E", new Object[] { filename }, e);
            return "";

        } catch ( IOException e ) {
            addException("WVER0014E", new Object[] { filename }, e);
            return "";
        }
    }

    protected String loadFile(File file, String fileName)
        throws FileNotFoundException, IOException
    {
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);

        FileReader fileReader =
            new FileReader(file); // throws FileNotFoundException, IOException
        BufferedReader reader =
            new BufferedReader(fileReader);

        try {
            String line;
            while ( (line = reader.readLine()) != null )
                pwriter.println(line);

        } finally {
            reader.close(); // throws IOException
        }

        pwriter.close();
        return writer.toString();
    }

    // Bottommost state: The version directory ...
    //
    //    File getProductDir();
    //    public String getProductDirName();
    //
    //    File getVersionDir();
    //    public String getVersionDirName();
    //    public String getVersionFileName(String;)
    //
    //    public String getDTDDirName();
    //    public String getLogDirName();
    //    public String getBackupDirName();
    //
    //    ArrayList listFileNames(FilenameFilter);
    //    ArrayList listFileNames(File[]);
    //    File[] listFiles(final FilenameFilter);

    protected File productDir;
    protected String productDirName;

    protected File versionDir;
    protected String versionDirName;

    protected File dtdDir;
    protected String dtdDirName;

    protected File logDir;
    protected String logDirName;

    protected File backupDir;
    protected String backupDirName;

    /**
	 * @return  the productDir
	 * @uml.property  name="productDir"
	 */
    protected File getProductDir()
    {
        return productDir;
    }

    /**
	 * <p>Answer the product directory name bound into the receiver.</p>
	 * @return  String The name of the product directory bound  into the receiver.
	 * @uml.property  name="productDirName"
	 */

    public String getProductDirName()
    {
        return productDirName;
    }

    /**
	 * @return  the versionDir
	 * @uml.property  name="versionDir"
	 */
    protected File getVersionDir()
    {
        return versionDir;
    }

    /**
	 * <p>Answer the version directory name bound into the receiver.</p>
	 * @return  String The name of the version directory bound  into the receiver.
	 * @uml.property  name="versionDirName"
	 */

    public String getVersionDirName()
    {
        return versionDirName;
    }

    /**
     * <p>Answer the name of the argument file qualified to be within
     * the bound version directory.</p>
     *
     * @param baseName The base file name, to be qualified.
     *
     * @return String The base file name qualified as a file within
     *                the bound version directory.
     */

    public String getVersionFileName(String baseName)
    {
        String fullName = getVersionDirName() + File.separator + baseName;

        File fullFile = new File(fullName);

        return fullFile.getAbsolutePath();
    }

    /**
     * <p>Answer the DTD directory name bound into the
     * receiver.</p>
     *
     * @return String The name of the DTD directory bound
     *                into the receiver.
     */

    public String getDTDDirName()
    {
        return dtdDirName;
    }

    /**
	 * <p>Answer the log directory name bound into the receiver.</p>
	 * @return  String The name of the log directory bound  into the receiver.
	 * @uml.property  name="logDirName"
	 */

    public String getLogDirName()
    {
        return logDirName;
    }

    /**
	 * <p>Answer the backup directory name bound into the receiver.</p>
	 * @return  String The name of the backup directory bound  into the receiver.
	 * @uml.property  name="backupDirName"
	 */

    public String getBackupDirName()
    {
        return backupDirName;
    }

    protected ArrayList listFileNames(FilenameFilter filter)
    {
        File[] files = listFiles(filter);

        return listFileNames(files);
    }

    protected ArrayList listFileNames(File[] files)
    {
        ArrayList fileNames = new ArrayList();

        for ( int fileNo = 0; fileNo < files.length; fileNo++ )
        {
            // System.out.println("product file name is " + files[fileNo].getName());
            if ( files[fileNo].getName().endsWith("itlm.product") ) {
                System.out.println("excluding product file name is " + files[fileNo].getName());
            }
            else 
            {
                fileNames.add(files[fileNo].getName());

            }
        }
         

        return fileNames;
    }

    protected File[] listFiles(final FilenameFilter filter)
    {
        return getVersionDir().listFiles(filter);
    }

    // Text ...
    //
    // public String toString();

    /**
     * <p>Answer a string representation of the receiver.
     * This representation currently displays the bound product
     * directory name and the bound version directory name.</p>
     *
     * @return String A string representation of the receiver.
     */

    public String toString()
    {
        return "WASProduct: " + getProductDirName() +
               " :: " + getVersionDirName();
    }
}
