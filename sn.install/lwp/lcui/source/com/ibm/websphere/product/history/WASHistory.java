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

package com.ibm.websphere.product.history;

/*
 * Product History Information
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 *
 * 01-Oct-2002 Added javadoc.
 *
 */

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.lconn.common.DateFormatUtil;
import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.filters.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.SAXParseException;

/**
 * EventHistory provides an interface into the product version history information.  The file set contains information about the Websphere Application Server updates, that is, eFixes and PTFs. Note: If this class is used outside the confines of the WebSphere product then the System property "server.root" must be set to point to the directory that Websphere is installed in. e.g "c:\websphere\appserver"
 * @author  Thomas F. Bitonti, IBM
 * @version  5.0
 */

public class WASHistory
{
    // Master Index ...
    //
    // Program versioning ...
    // Message catalog ...
    // Message access ...
    // Error handling ...
    //
    // History directory selection ...
    // Instantor ...
    // History access ...
    // History update ...
    //
    // Top level EFix driver access ...
    // Top level PTF driver access ...
    //
    // EFix driver operations ...
    // EFix Driver storage ...
    // EFix driver name storage ...
    //
    // PTF driver Operations ...
    // PTF driver storage ...
    // PTF driver name storage ...
    //
    // EFix applied top level access ...
    // PTF applied top level access ...
    //
    // EFix application operations ...
    // EFix application mapping ...
    // EFix applied name storage ...
    //
    // PTF application operations ...
    // PTF application mapping ...
    // PTF applied name storage ...
    //
    // History directory access ...
    // Raw loading ...
    // History info cache ...
    // Text ...
    // Location storage ...

    //
    // Program versioning ...

    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

    public static final String pgmVersion = "1.2" ;
    // Master Index ...
    //
    // Program versioning ...
    // Message catalog ...
    // Message access ...
    // Error handling ...
    //
    // History directory selection ...
    // Instantor ...
    // History access ...
    // History update ...
    //
    // Top level EFix driver access ...
    // Top level PTF driver access ...
    //
    // EFix driver operations ...
    // EFix Driver storage ...
    // EFix driver name storage ...
    //
    // PTF driver Operations ...
    // PTF driver storage ...
    // PTF driver name storage ...
    //
    // EFix applied top level access ...
    // PTF applied top level access ...
    //
    // EFix application operations ...
    // EFix application mapping ...
    // EFix applied name storage ...
    //
    // PTF application operations ...
    // PTF application mapping ...
    // PTF applied name storage ...
    //
    // History directory access ...
    // Raw loading ...
    // History info cache ...
    // Text ...
    // Location storage ...

    //
    // Program versioning ...

    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

    public static final String pgmUpdate = "9/26/03" ;

    // Message catalog ...

    /** The id of the message bundle use by WASHistory. */

    public static final String bundleId = "com.ibm.websphere.product.history.WASHistoryNLS";

    /** The initialized resource bundle.  This is public for shared access. */

    public static final ResourceBundle msgs;
    private ArrayList<String> installedTime = new ArrayList<String>();
    private Calendar calendar = Calendar.getInstance();
    /**
     * Get the date of the fix has been installed according to the efixDriver file's date
     * @return
     */
    public ArrayList<String> getInstalledTime() {
		return installedTime;
	}

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
            return msgs.getString(msgCode);

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
    //    void addException(WASHistoryException)
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
    //    void addException(String);
    //    void addException(String, Exception);
    //    void addException(String, Object[], Exception);
    //
    //    static WASHistoryException createException(String, Object[], Exception);

    protected ArrayList boundExceptions;
    protected ArrayList recoverableErrors;
    protected ArrayList warnings;

    protected void addException(WASHistoryException e)
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

    protected static WASHistoryException createException(String msgKey, Object[] msgArgs, Exception e)
    {
        return new WASHistoryException(msgKey, msgArgs, e);
    }

    // History directory selection ...
    //
    //    public static String[] determineHistoryDirNames()
    //    public static String determineHistoryDirName(String);
    //    public static String getHistoryDirName(String);

    /**
     * <p>The name of the property which is used to specify an
     * alternate history directory.</p>
     */

    public static final String HISTORY_DIR_PROPERTY_NAME = "was.history.dir";

    /**
     * <p>The default history directory name, relative to the product version
     * directory.</p>
     */

    public static final String DEFAULT_LOCAL_HISTORY_DIR_NAME = "history";

    protected static final int PRODUCT_DIR_OFFSET = 0 ;
    protected static final int VERSION_DIR_OFFSET = 1 ;
    protected static final int HISTORY_DIR_OFFSET = 2 ;

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
        String productDirName;

        try {
            productDirName = WASProduct.computeProductDirName(); // throws WASProductException
        } catch ( WASProductException e ) {
            throw createException("WVER201E", null, e);
        }

        String versionDirName = WASProduct.computeVersionDirName(productDirName);

        String historyDirName = determineHistoryDirName(versionDirName);

        return new String[] { productDirName, versionDirName, historyDirName };
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

    public static String determineHistoryDirName(String versionDirName)
    {
        String historyDirName = System.getProperty(HISTORY_DIR_PROPERTY_NAME);

        if ( historyDirName == null )
            historyDirName = getHistoryDirName(versionDirName);
        else
            historyDirName = WASProduct.preparePath(historyDirName, false);

        return historyDirName;
    }

    /**
     * <p>Answer a history directory based on the argument version
     * directory.</p>
     *
     * @return String The computed history directory name.
     */

    public static String getHistoryDirName(String versionDirName)
    {
        versionDirName = WASProduct.preparePath(versionDirName, false);

        return versionDirName + File.separator + DEFAULT_LOCAL_HISTORY_DIR_NAME;
    }

    // Instantor ...
    //
    //    public WASHistory();
    //    public WASHistory(String, String, String);
    //    void initialize(String, String, String);

    // Upon construction, the WASProduct instance may have a
    // bound exception.

    /**
     * <p>Default constructor.  The new instance is initialized
     * wholly from system properties.  Exceptions may be registed
     * in the new instance.</p>
     */

    public WASHistory()
    {
        clearExceptions();

        try {
            String[] dirNames = determineHistoryDirNames(); // throws WASHistoryException

            initialize(dirNames[PRODUCT_DIR_OFFSET],
                       dirNames[VERSION_DIR_OFFSET],
                       dirNames[HISTORY_DIR_OFFSET]);

        } catch ( WASHistoryException e ) {
            addException(e);
        }
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

    public WASHistory(String productDirName, String versionDirName, String historyDirName)
    {
        clearExceptions();

        initialize(productDirName, versionDirName, historyDirName);
    }

    /** The name of the history file (relative to the history directory). */

    public static final String
        HISTORY_FILE_NAME = "event.history";

    /**
     * <p>The file name extension (including a '.') which is used to name
     * backups of the event history file.</p>
     */

    public static final String
        BACKUP_SUFFIX = ".backup";

    protected void initialize(String useProductDirName, String useVersionDirName, String useHistoryDirName)
    {
        productDir = new File(useProductDirName);
        productDirName = productDir.getAbsolutePath();

        versionDir = new File(useVersionDirName);
        versionDirName = versionDir.getAbsolutePath();

        dtdDirName = WASProduct.getDTDDirName(versionDirName);
        dtdDir = new File(dtdDirName);
        dtdDirName = dtdDir.getAbsolutePath();

        historyDir = new File(useHistoryDirName);
        historyDirName = historyDir.getAbsolutePath();

        String useFileName = historyDirName + File.separator + HISTORY_FILE_NAME;

        historyFile = new File(useFileName);
        historyFileName = historyFile.getAbsolutePath();

        String useBackupName = useFileName + BACKUP_SUFFIX;

        backupFile = new File(useBackupName);
        backupFileName = backupFile.getAbsolutePath();

        historyInfo = new HashMap();

        loadHistory();
    }

    // History access ...
    //
    //    public eventHistory getHistory();
    //    void loadHistory();
    //    public String loadHistoryInfoAsXMLString();

    protected eventHistory history;

    /**
	 * <p>Answer the event history which was read from the bound history directory.</p>
	 * @uml.property  name="history"
	 */

    public eventHistory getHistory()
    {
        return history;
    }

    // Note that the history will be incomplete
    // if an error occurred during the load.

    protected void loadHistory()
    {
        if ( !ensureHistoryDirectory() ) {
            addException("WVER0204E", new Object[] { historyDirName }, null);
            return;
        }

        if ( !historyFile.exists() ) {
            history = new eventHistory();
        } else {
            // Don't need a writer in this handler.
            BaseFactory factory = new BaseFactory( new EventHandler(), null );

            List loaded = factory.load( getDTDDirName(), getHistoryDirName(), HISTORY_FILE_NAME );
            history = (eventHistory) loaded.iterator().next();

            transferErrors(factory);

            BaseHandlerException handlerException = factory.getException();

            if ( handlerException != null ) {
                history = null;
                addException("WVER0202E", new Object[] { historyFileName }, handlerException);
            }
        }
    }

    /**
     * <p>Answer the contents of the event history file as string.  The
     * entire contents of the history file are loaded and returned.</p>
     *
     * @return String The contents of the event history file.
     */

    public String loadHistoryInfoAsXMLString()
    {
        return loadHistoryInfoAsXMLString(historyFileName);
    }

    // History update ...
    //
    //    public boolean save(boolean);
    //    public boolean ensureHistoryDirectory();
    //    public void backupHistoryFile() throws IOException;
    //    BaseHandlerException saveHistory();

    /**
     * <p>Save the event history, possibly creating a backup.</p>
     *
     * @param createBackup A flag telling if a backup is to be
     *                     created before saving the event history.
     *
     * @return boolean True or false, telling if the the event history
     *                 was successfully saved.
     */

    public boolean save(boolean createBackup)
    {
        clearExceptions();

        if ( createBackup ) {
            try {
                backupHistoryFile(); // throws IOException
            } catch ( IOException e ) {
                addException("WVER0205E", new Object[] { historyFileName, backupFileName }, e);
                return false;
            }
        }

        BaseHandlerException handlerException = saveHistory();

        if ( handlerException != null ) {
            addException("WVER0206E", new Object[] { historyFileName }, handlerException);
            return false;
        } else {
            return true;
        }
    }

    /**
     * <p>Make sure that the history directory exists, creating it
     * if necessary.  Answer true or false, telling if the history
     * directory exists.</p>
     *
     * @return boolean True or false, telling if the history directory
     *                 exists.
     */

    public boolean ensureHistoryDirectory()
    {
        return ( historyDir.exists() || historyDir.mkdirs() );
    }

    /**
     * <p>Constant count of the number of backups which are to be
     * created.</p> */

    public static final int
        MAX_BACKUPS = 10;

    /**
     * <p>Create a backup of the history file.  An exception is thrown
     * if a backup cannot be created.</p>
     *
     * @throws IOException Thrown if the event history could not be
     *                     backed up.
     */

    public void backupHistoryFile()
        throws IOException
    {
        /*
        int sequenceNo = 0;
        boolean foundGap = false;

        Vector backupNames = new Vector();
        Vectory backupFiles = new Vector();

        while ( !foundGap && (sequenceNo < MAX_BACKUPS) ) {
            String nextBackupName = backupFileName + "_" + Integer.toString(sequenceNo);
            File nextBackupFile = new File(nextBackupFileName);

            backupNames.add(nextBackupName);
            backupFiles.add(nextBackupFile);

            foundGap = !nextBackupFile.exists();
        }

        if ( !foundGap ) {
        }

            while ( sequenceNo > 0 ) {
                int prevSequenceNo = sequenceNo - 1;

                String nextBackupFileName = backupFileName + "_" + Integer.toString(sequenceNo);
                File nextBackupFile = new File(nextBackupFileName);
                
            }
        }
        if ( !historyFile.exists() )
            return;

        // renameTo(File dest);
        // copy historyFile to backupFile
        */
    }

    protected BaseHandlerException saveHistory()
    {
        return BaseFactory.saveSingleton( getHistory(),
                                          new EventWriter(),
                                          getHistoryFileName() );
    }

    // Top level EFix driver access ...
    //
    //    public efixDriver getEFixDriverByFilename(String);
    //    public efixDriver getEFixDriverById(String);
    //    public boolean efixDriverPresent(String);

    /**
     * <p>Answer the efix driver stored in the named file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding an efix driver.
     *
     * @return efixDriver The efix driver in the named file.
     */

    public efixDriver getEFixDriverByFilename(String basename)
    {
        if ( !AppliedHandler.acceptsEFixDriverFileName(basename) )
            return null;
        else
            return (efixDriver) getHistoryInfo(basename);
    }

    /**
     * <p>Answer the efix driver having the specified id.  Answer
     * null if no matching driver is available.</p>
     *
     * @param id The id of the efix driver to be returned.
     *
     * @return efixDriver The efix driver having the specified id.
     */

    public efixDriver getEFixDriverById(String id)
    {
        efixDriver selectedEFixDriver = null;

        Iterator efixDrivers = getEFixDrivers();

        while ( (selectedEFixDriver == null) && efixDrivers.hasNext() ) {
            efixDriver nextEFixDriver = (efixDriver) efixDrivers.next();
            if ( nextEFixDriver.getId().equals(id) )
                selectedEFixDriver = nextEFixDriver;
        }

        return selectedEFixDriver;
    }

    /**
     * <p>Answer true or false, telling if an efix driver having the
     * specified id is present.</p>
     *
     * @param id The id of the efix driver which is tested.
     *
     * @return boolean True or false, telling if the efix driver having
     *                 the specified id is present.
     */

    public boolean efixDriverPresent(String id)
    {
        return ( getEFixDriverById(id) != null );
    }

    // Top level PTF driver access ...
    //
    //    public ptfDriver getPTFDriverByFilename(String);
    //    public ptfDriver getPTFDriverById(String);
    //    public boolean ptfDriverPresent(String);

    /**
     * <p>Answer the PTF driver stored in the named file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding an PTF driver.
     *
     * @return ptfDriver The PTF driver in the named file.
     */

    public ptfDriver getPTFDriverByFilename(String basename)
    {
        if ( !AppliedHandler.acceptsPTFDriverFileName(basename) )
            return null;
        else
            return (ptfDriver) getHistoryInfo(basename);
    }

    /**
     * <p>Answer the PTF driver having the specified id.  Answer
     * null if no matching driver is available.</p>
     *
     * @param id The id of the PTF driver to be returned.
     *
     * @return ptfDriver The PTF driver having the specified id.
     */

    public ptfDriver getPTFDriverById(String id)
    {
        ptfDriver selectedPTFDriver = null;

        Iterator ptfDrivers = getPTFDrivers();

        while ( (selectedPTFDriver == null) && ptfDrivers.hasNext() ) {
            ptfDriver nextPTFDriver = (ptfDriver) ptfDrivers.next();
            if ( nextPTFDriver.getId().equals(id) )
                selectedPTFDriver = nextPTFDriver;
        }

        return selectedPTFDriver;
    }

    /**
     * <p>Answer true or false, telling if an PTF driver having the
     * specified id is present.</p>
     *
     * @param id The id of the PTF driver which is tested.
     *
     * @return boolean True or false, telling if the PTF driver having
     *                 the specified id is present.
     */

    public boolean ptfDriverPresent(String id)
    {
        return ( getPTFDriverById(id) != null );
    }

    // EFix driver operations ...
    //
    //    public boolean saveEFixDriver(efixDriver);
    //    public boolean removeEFixDriver(efixDriver);

    // Answer true or false, telling if the efixDriver was saved.

    /**
     * <p>Save the argument efix driver.  Answer true or false,
     * telling if the driver was successfully saved.  An exception
     * is registed when the driver could not be saved.</p>
     *
     * @param anEFixDriver An efix driver to be saved.
     *
     * @return True or false, telling if the efix driver was saved.
     */

    public boolean saveEFixDriver(efixDriver anEFixDriver)
    {
        clearExceptions();

        String baseName = anEFixDriver.getStandardFileName();
        String fullName = getHistoryFileName( baseName );

        BaseHandlerException exception =
            BaseFactory.saveSingleton(anEFixDriver, new AppliedWriter(), fullName);

        if ( exception == null ) {
            addEFixDriverName(baseName);
            addEFixDriver(anEFixDriver);
            return true;

        } else {
            addException("WVER0260E",
                         new String[] { anEFixDriver.getId(), fullName },
                         exception);
            return false;
        }
    }

    /**
     * <p>Remove the argument efix driver.  Answer true or false,
     * telling if the driver was successfully removed.  An exception
     * is registed when the driver could not be removed .</p>
     *
     * @param anEFixDriver An efix driver to be removed.
     *
     * @return True or false, telling if the efix driver was removed.
     */

    public boolean removeEFixDriver(efixDriver anEFixDriver)
    {
        clearExceptions();

        String baseName = anEFixDriver.getStandardFileName();
        String fullName = getHistoryFileName(baseName);

        File efixDriverFile = new File(fullName);
        boolean result = efixDriverFile.delete();

        if ( result ) {
            removeEFixDriverName(baseName);
            basicRemoveEFixDriver(anEFixDriver);

        } else {
            addException("WVER0265E",
                         new String[] { anEFixDriver.getId(), fullName },
                         null);
        }

        return result;
    }

    // EFix Driver storage ...
    //
    //    void addEFixDriver(efixDriver);
    //    void basicRemoveEFixDriver(efixDriver);
    //    public Iterator getEFixDrivers();
    //    ArrayList basicGetEFixDrivers();

    protected ArrayList efixDrivers = null;

    protected void addEFixDriver(efixDriver anEFixDriver)
    {
        if ( efixDrivers == null )
            efixDrivers = basicGetEFixDrivers();

        efixDrivers.add(anEFixDriver);
    }

    protected void basicRemoveEFixDriver(efixDriver anEFixDriver)
    {
        if ( efixDrivers == null )
            efixDrivers = basicGetEFixDrivers();

        efixDrivers.remove( efixDrivers.indexOf(anEFixDriver) );
    }

    /**
     * <p>Answer an iterator across the efix drivers which are
     * available in the bound history directory.</p>
     *
     * @return Iterator An iterator across the efix drivers which
     *                  are available in the bound history directory.
     */

    public Iterator getEFixDrivers()
    {
        if ( efixDrivers == null )
            efixDrivers = basicGetEFixDrivers();

        return efixDrivers.iterator();
    }

    protected ArrayList basicGetEFixDrivers()
    {
        Iterator efixDriverNames = getEFixDriverNames();

        return collectHistoryInfo(efixDriverNames);
    }

    // EFix driver name storage ...
    //
    //    void addEFixDriverName(String);
    //    void removeEFixDriverName(String);
    //    public Iterator getEFixDriverNames();
    //    public int getEFixDriverCount();
    //    ArrayList basicGetEFixDriverNames();

    protected ArrayList efixDriverNames = null;

    protected void addEFixDriverName(String efixDriverName)
    {
        if ( efixDriverNames == null )
            efixDriverNames = basicGetEFixDriverNames();

        efixDriverNames.add(efixDriverName);
    }

    protected void removeEFixDriverName(String efixDriverName)
    {
        if ( efixDriverNames == null )
            efixDriverNames = basicGetEFixDriverNames();

        efixDriverNames.remove( efixDriverNames.indexOf(efixDriverName) );
    }

    /**
     * <p>Answer an iterator across the ids of the efix drivers which
     * are available in the bound history directory.</p>
     *
     * @return Iterator An iterator across the ids of the efix drivers
     *                  which are available in the bound history directory.
     */

    public Iterator getEFixDriverNames()
    {
        if ( efixDriverNames == null )
            efixDriverNames = basicGetEFixDriverNames();

        return efixDriverNames.iterator();
    }

    /**
     * <p>Answer the count of efix drivers in the bound history
     * directory.</p>
     *
     * @return int The count of efix drivers in the bound history
     *             directory.
     */

    public int getEFixDriverCount()
    {
        if ( efixDriverNames == null )
            efixDriverNames = basicGetEFixDriverNames();

        return efixDriverNames.size();
    }

    protected ArrayList basicGetEFixDriverNames()
    {
        return listFileNames(new EFixDriverFilenameFilter());
    }

    // PTF Driver Operations ...
    //
    //    public boolean savePTFDriver(ptfDriver);
    //    public boolean removePTFDriver(ptfDriver);

    /**
     * <p>Save the argument PTF driver.  Answer true or false,
     * telling if the driver was successfully saved.  An exception
     * is registed when the driver could not be saved.</p>
     *
     * @param aPTFDriver An PTF driver to be saved.
     *
     * @return True or false, telling if the PTF driver was saved.
     */

    public boolean savePTFDriver(ptfDriver aPTFDriver)
    {
        clearExceptions();

        String baseName = aPTFDriver.getStandardFileName();
        String fullName = getHistoryFileName(baseName);

        BaseHandlerException exception =
            BaseFactory.saveSingleton(aPTFDriver, new AppliedWriter(), fullName);

        if ( exception == null ) {
            addPTFDriverName(baseName);
            addPTFDriver(aPTFDriver);
            return true;

        } else {
            addException("WVER0266E",
                         new String[] { aPTFDriver.getId(), fullName },
                         exception);
            return false;
        }
    }

    /**
     * <p>Remove the argument PTF driver.  Answer true or false,
     * telling if the driver was successfully removed.  An exception
     * is registed when the driver could not be removed .</p>
     *
     * @param aPTFDriver An PTF driver to be removed.
     *
     * @return True or false, telling if the PTF driver was removed.
     */

    public boolean removePTFDriver(ptfDriver aPTFDriver)
    {
        clearExceptions();

        String baseName = aPTFDriver.getStandardFileName();
        String fullName = getHistoryFileName(baseName);

        File ptfDriverFile = new File(fullName);
        boolean result = ptfDriverFile.delete();

        if ( result ) {
            removePTFDriverName(baseName);
            basicRemovePTFDriver(aPTFDriver);

        } else {
            addException("WVER0270E",
                         new String[] { aPTFDriver.getId(), fullName },
                         null);
        }

        return result;
    }

    // PTF driver storage ...
    //
    //    void addPTFDriver(ptfDriver);
    //    void basicRemovePTFDriver(ptfDriver);
    //    public Iterator getPTFDrivers();
    //    ArrayList basicGetPTFDrivers();

    protected ArrayList ptfDrivers = null;

    protected void addPTFDriver(ptfDriver aPTFDriver)
    {
        if ( ptfDrivers == null )
            ptfDrivers = basicGetPTFDrivers();

        ptfDrivers.add(aPTFDriver);
    }

    protected void basicRemovePTFDriver(ptfDriver aPTFDriver)
    {
        if ( ptfDrivers == null )
            ptfDrivers = basicGetPTFDrivers();

        ptfDrivers.remove( ptfDrivers.indexOf(aPTFDriver) );
    }

    /**
     * <p>Answer an iterator across the PTF drivers which are
     * available in the bound history directory.</p>
     *
     * @return Iterator An iterator across the PTF drivers which
     *                  are available in the bound history directory.
     */

    public Iterator getPTFDrivers()
    {
        if ( ptfDrivers == null )
            ptfDrivers = basicGetPTFDrivers();

        return ptfDrivers.iterator();
    }

    protected ArrayList basicGetPTFDrivers()
    {
        Iterator ptfDriverNames = getPTFDriverNames();

        return collectHistoryInfo(ptfDriverNames);
    }

    // PTF driver name storage ...
    //
    //    void addPTFDriverName(String);
    //    void removePTFDriverName(String);
    //    public Iterator getPTFDriverNames();
    //    public int getPTFDriverCount();
    //    ArrayList basicGetPTFDriverNames();

    protected ArrayList ptfDriverNames = null;

    protected void addPTFDriverName(String ptfDriverName)
    {
        if ( ptfDriverNames == null )
            ptfDriverNames = basicGetPTFDriverNames();

        ptfDriverNames.add(ptfDriverName);
    }

    protected void removePTFDriverName(String ptfDriverName)
    {
        if ( ptfDriverNames == null )
            ptfDriverNames = basicGetPTFDriverNames();

        ptfDriverNames.remove( ptfDriverNames.indexOf(ptfDriverName) );
    }

    /**
     * <p>Answer an iterator across the ids of the PTF drivers which
     * are available in the bound history directory.</p>
     *
     * @return Iterator An iterator across the ids of the PTF drivers
     *                  which are available in the bound history directory.
     */

    public Iterator getPTFDriverNames()
    {
        if ( ptfDriverNames == null )
            ptfDriverNames = basicGetPTFDriverNames();

        return ptfDriverNames.iterator();
    }

    /**
     * <p>Answer the count of PTF drivers in the bound history
     * directory.</p>
     *
     * @return int The count of PTF drivers in the bound history
     *             directory.
     */

    public int getPTFDriverCount()
    {
        if ( ptfDriverNames == null )
            ptfDriverNames = basicGetPTFDriverNames();

        return ptfDriverNames.size();
    }

    protected ArrayList basicGetPTFDriverNames()
    {
        return listFileNames(new PTFDriverFilenameFilter());
    }

    // EFix applied top level access ...
    //
    //    public efixApplied getEFixAppliedByFilename(String);
    //    public efixApplied getEFixAppliedById(String);
    //    public componentApplied getEFixComponentAppliedById(String, String);
    //    public boolean efixAppliedPresent(String);
    //    public boolean efixComponentAppliedPresent(String, String);

    /**
     * <p>Answer the efix applied stored in the named file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding an efix applied.
     *
     * @return efixApplied The efix applied in the named file.
     */

    public efixApplied getEFixAppliedByFilename(String basename)
    {
        if ( !AppliedHandler.acceptsEFixAppliedFileName(basename) )
            return null;
        else
            return (efixApplied) getHistoryInfo(basename);
    }

    /**
     * <p>Answer the efix applied having the specified id.  Answer
     * null if no matching applied is available.</p>
     *
     * @param id The id of the efix applied to be returned.
     *
     * @return efixApplied The efix applied having the specified id.
     */

    public efixApplied getEFixAppliedById(String efixId)
    {
        return (efixApplied) getEFixAppliedMapping().get(efixId);
    }

    /**
     * <p>Answer the component applied having the specified efix id
     * and the specified component name.  Answer null if no matching
     * applied is available.</p>
     *
     * @param efixId The id of the parent efix applied.
     * @param componentName The name of the component applied to be returned.
     *
     * @return efixApplied The efix applied having the specified id.
     */

    public componentApplied getEFixComponentAppliedById(String efixId, String componentName)
    {
        efixApplied selectedEFixApplied = getEFixAppliedById(efixId);

        if ( selectedEFixApplied != null )
            return selectedEFixApplied.selectComponentApplied(componentName);
        else
            return null;
    }

    /**
     * <p>Answer true or false, telling if an efix applied having the
     * specified id is present.</p>
     *
     * @param id The id of the efix applied which is tested.
     *
     * @return boolean True or false, telling if the efix applied having
     *                 the specified id is present.
     */

    public boolean efixAppliedPresent(String efixId)
    {
        return ( getEFixAppliedById(efixId) != null );
    }

    /**
     * <p>Answer true or false, telling if a component applied 
     * having the specified efix id and having the specified
     * component name is present.</p>
     *
     * @param efixId The id of the parent efix applied.
     * @param componentName The component name of the component update
     *                      which is tested.
     *
     * @return boolean True or false, telling if the efix applied having
     *                 the specified efix id and the specified component
     *                 name is present.
     */

    public boolean efixComponentAppliedPresent(String efixId, String componentName)
    {
        return ( getEFixComponentAppliedById(efixId, componentName) != null );
    }

    // PTF applied top level access ...
    //
    //    public ptfApplied getPTFAppliedById(String);
    //    public componentApplied getPTFComponentAppliedById(String, String);
    //    public ptfApplied getPTFAppliedByFilename(String);
    //    public boolean ptfAppliedPresent(String);
    //    public boolean ptfAppliedPresent(String, String);

    /**
     * <p>Answer the PTF applied having the specified id.  Answer null
     * if no matching applied is available.</p>
     *
     * @param ptfId The id of the PTF to be returned.
     *
     * @return ptfApplied The PTF applied having the specified id.
     */

    public ptfApplied getPTFAppliedById(String ptfId)
    {
        return (ptfApplied) getPTFAppliedMapping().get(ptfId);
    }

    /**
     * <p>Answer the component applied having the specified PTF id
     * and the specified component name.  Answer null if no matching
     * applied is available.</p>
     *
     * @param ptfId The id of the parent PTF applied.
     * @param componentName The name of the component applied to be returned.
     *
     * @return ptfApplied The PTF applied having the specified id.
     */

    public componentApplied getPTFComponentAppliedById(String ptfId, String componentName)
    {
        ptfApplied selectedPTFApplied = getPTFAppliedById(ptfId);

        if ( selectedPTFApplied != null )
            return selectedPTFApplied.selectComponentApplied(componentName);
        else
            return null;
    }

    /**
     * <p>Answer the PTF applied stored in the named file.
     * Answer null if the named file does not exist or cannot be
     * loaded.</p>
     *
     * @param basename The name of the file holding an PTF applied.
     *
     * @return efixApplied The PTF applied in the named file.
     */

    public ptfApplied getPTFAppliedByFilename(String basename)
    {
        if ( !AppliedHandler.acceptsPTFDriverFileName(basename) )
            return null;
        else
            return (ptfApplied) getHistoryInfo(basename);
    }

    /**
     * <p>Answer true or false, telling if a PTF applied having the
     * specified id is present.</p>
     *
     * @param id The id of the PTF applied which is tested.
     *
     * @return boolean True or false, telling if the PTF applied having
     *                 the specified id is present.
     */

    public boolean ptfAppliedPresent(String ptfId)
    {
        return ( getPTFAppliedById(ptfId) != null );
    }

    /**
     * <p>Answer true or false, telling if a component applied 
     * having the specified PTF id and having the specified
     * component name is present.</p>
     *
     * @param ptfId The id of the parent PTF applied.
     * @param componentName The component name of the component update
     *                      which is tested.
     *
     * @return boolean True or false, telling if the PTF applied having
     *                 the specified PTF id and the specified component
     *                 name is present.
     */

    public boolean ptfComponentAppliedPresent(String ptfId, String componentName)
    {
        return ( getPTFComponentAppliedById(ptfId, componentName) != null );
    }

    // EFix application operations ...
    //
    //    public boolean recordEFixApplication(String, componentApplied);
    //    public boolean clearEFixApplication(String, componentApplied);

    // Answer true or false, telling if the recording was successful.

    /**
     * <p>Record the child application, in association with the
     * efix having the specified id.  If necessary, create a efix
     * applied.  Add the child applied to the parent efixapplied
     * and save that parent applied.</p>
     *
     * @return boolean Answer true or false, telling if the
     *                 application was successfully applied.
     */

    public boolean recordEFixApplication(String efixId, componentApplied childApplied)
    {
        clearExceptions();

        efixApplied parentApplied = getEFixAppliedById(efixId);
        boolean didCreate;

        if ( parentApplied == null ) {
            parentApplied = new efixApplied();
            parentApplied.setEFixId(efixId);
            didCreate = true;
        } else {
            didCreate = false;
        }

        parentApplied.addComponentApplied(childApplied);

        String baseName = parentApplied.getStandardFileName();
        String fullName = getHistoryFileName(baseName);

        BaseHandlerException exception =
            BaseFactory.saveSingleton(parentApplied, new AppliedWriter(), fullName);

        if ( exception == null ) {
            if ( didCreate ) {
                mapEFixApplied(parentApplied);
                addEFixAppliedName(baseName);
            }
            return true;
        } else {
            addException("WVER0261E",
                         new String[] { efixId,
                                        childApplied.getComponentName(),
                                        fullName },
                         exception);
            return false;
        }
    }

    // Answer true or false, telling if the application was successfully
    // removed.

    /**
     * <p>Clear the child application.  This application is removed
     * from the parent efix application, which is then saved.  If the
     * child application is the last application within it's parent
     * efix application, that parent application is removed as well.</p>
     *
     * <p>Answer true or false, telling if the application was
     * successfully removed.</p>
     *
     * @param efixId The ID of the parent efix application.
     * @param childApplied The child application which is to be removed.
     *
     * @return boolean True or false, telling if the the application
     *                 was successfully removed.
     */

    public boolean clearEFixApplication(String efixId, componentApplied childApplied)
    {
        clearExceptions();

        String componentName = childApplied.getComponentName();

        efixApplied parentApplied = getEFixAppliedById(efixId);

        if ( parentApplied == null ) {
            addException("WVER0264E",
                         new String[] { efixId, componentName },
                         null);
            return false;
        }

        componentApplied removedApplied =
            parentApplied.removeComponentApplied(componentName);

        if ( removedApplied == null ) {
            addException("WVER0264E",
                         new String[] { efixId, componentName },
                         null);
            return false;
        }

        String baseName = parentApplied.getStandardFileName();
        String fullName = getHistoryFileName(baseName);

        if ( parentApplied.getComponentAppliedCount() != 0 ) {
            BaseHandlerException exception =
                BaseFactory.saveSingleton(parentApplied, new AppliedWriter(), fullName);

            if ( exception != null ) {
                addException("WVER0262E",
                             new String[] { efixId, componentName, fullName },
                             exception);
                return false;
            } else {
                return true;
            }
        } else {
            File appliedFile = new File(fullName);
            boolean result = appliedFile.delete();

            if ( !result ) {
                addException("WVER0263E",
                             new String[] { efixId, componentName, fullName },
                             null);
            } else {
                unmapEFixApplied(parentApplied);
                removeEFixAppliedName(baseName);
            }

            return result;
        }
    }

    // EFix application mapping ...
    //
    //    public Iterator getEFixApplieds();
    //    HashMap getEFixAppliedMapping();
    //    void ensureEFixApplieds();
    //    void mapEFixApplied(efixApplied);
    //    void unmapEFixApplied(efixApplied);
    //    void computeEFixAppliedMapping(Iterator);
    //    ArrayList basicGetEFixApplieds();

    protected ArrayList efixApplieds = null;
    protected HashMap efixAppliedMapping = null;

    /**
     * <p>Answer an iterator across the efix applieds
     * within the receiver.</p>
     *
     * @return Iterator An iterator across the efix applieds
     *                  within the receiver.
     */

    public Iterator getEFixApplieds()
    {
        ensureEFixApplieds();

        return efixApplieds.iterator();
    }

    protected HashMap getEFixAppliedMapping()
    {
        ensureEFixApplieds();

        return efixAppliedMapping;
    }

    protected void ensureEFixApplieds()
    {
        if ( efixApplieds == null ) {
            efixApplieds = basicGetEFixApplieds();
            computeEFixAppliedMapping(efixApplieds.iterator());
        }
    }

    protected void mapEFixApplied(efixApplied anApplied)
    {
        efixAppliedMapping.put(anApplied.getEFixId(), anApplied);
    }

    protected void unmapEFixApplied(efixApplied anApplied)
    {
        efixAppliedMapping.remove(anApplied.getEFixId());
    }

    protected void computeEFixAppliedMapping(Iterator efixApplieds)
    {
        efixAppliedMapping = new HashMap();

        while ( efixApplieds.hasNext() ) {
            efixApplied nextApplied = (efixApplied) efixApplieds.next();

            efixAppliedMapping.put(nextApplied.getEFixId(), nextApplied);
        }
    }

    protected ArrayList basicGetEFixApplieds()
    {
        Iterator efixAppliedNames = getEFixAppliedNames();

        return collectHistoryInfo(efixAppliedNames);
    }

    // EFix applied name storage ...
    //
    //    void addEFixAppliedName(String);
    //    void removeEFixAppliedName(String);
    //    public Iterator getEFixAppliedNames();
    //    ArrayList basicGetEFixAppliedNames();

    protected ArrayList<String> efixAppliedNames = null;

    protected void addEFixAppliedName(String filename)
    {
        efixAppliedNames.add(filename);
    }

    protected void removeEFixAppliedName(String filename)
    {
        efixAppliedNames.remove( efixAppliedNames.indexOf(filename) );
    }

    /**
     * <p>Answer an iterator across the ids of the efix applieds
     * within the receiver.</p>
     *
     * @return Iterator An iterator across the ids of the efix
     *                  applieds within the receiver.
     */

    public Iterator<String> getEFixAppliedNames()
    {
        if ( efixAppliedNames == null )
            efixAppliedNames = basicGetEFixAppliedNames();

        return efixAppliedNames.iterator();
    }

    protected ArrayList<String> basicGetEFixAppliedNames()
    {
        return listFileNames( new EFixAppliedFilenameFilter() );
    }

    // PTF application operations ...
    //
    //    public boolean recordPTFApplication(ptfApplied);
    //    public boolean clearPTFApplication(ptfApplied);

    // Answer true or false, telling if record was made.

    /**
     * <p>Record the child application, in association with the
     * PTF having the specified id.  If necessary, create a PTF
     * applied.  Add the child applied to the parent PTF applied
     * and save that parent applied.</p>
     *
     * @return boolean Answer true or false, telling if the
     *                 application was successfully applied.
     */
    public boolean recordPTFApplication(String ptfId, List childAppliedList)
    {
        boolean bOK = true;
        for(int i=0; i < childAppliedList.size(); i++)
        {
            if(!recordPTFApplication(ptfId, (componentApplied)childAppliedList.get(i)))
                bOK = false;
        }
        return bOK;
    }

    public boolean recordPTFApplication(String ptfId, componentApplied childApplied)
    {
        clearExceptions();

        ptfApplied parentApplied = getPTFAppliedById(ptfId);
        boolean didCreate;

        if ( parentApplied == null ) {
            parentApplied = new ptfApplied();
            parentApplied.setPTFId(ptfId);
            didCreate = true;
        } else {
            didCreate = false;
        }

        parentApplied.addComponentApplied(childApplied);

        String baseName = parentApplied.getStandardFileName();
        String fullName = getHistoryFileName(baseName);

        BaseHandlerException exception =
            BaseFactory.saveSingleton(parentApplied, new AppliedWriter(), fullName);

        if ( exception == null ) {
            mapPTFApplied(parentApplied);
            addPTFAppliedName(baseName);
            return true;

        } else {
            addException("WVER0268E",
                         new String[] { ptfId,
                                        childApplied.getComponentName(),
                                        fullName },
                         exception);
            return false;
        }
    }

    // Answer true or false, telling if the application was successfully
    // removed.

    /**
     * <p>Clear the child application.  This application is removed
     * from the parent PTF application, which is then saved.  If the
     * child application is the last application within it's parent
     * PTF application, that parent application is removed as well.</p>
     *
     * <p>Answer true or false, telling if the application was
     * successfully removed.</p>
     *
     * @param ptfId The ID of the parent PTF application.
     * @param childApplied The child application which is to be removed.
     *
     * @return boolean True or false, telling if the the application
     *                 was successfully removed.
     */

    public boolean clearPTFApplication(String ptfId, componentApplied childApplied)
    {
        clearExceptions();

        String componentName = childApplied.getComponentName();

        ptfApplied parentApplied = getPTFAppliedById(ptfId);

        if ( parentApplied == null ) {
            addException("WVER0269E",
                         new String[] { ptfId, componentName },
                         null);
            return false;
        }

        componentApplied removedApplied =
            parentApplied.removeComponentApplied(componentName);

        if ( removedApplied == null ) {
            addException("WVER0269E",
                         new String[] { ptfId, componentName },
                         null);
            return false;
        }

        String baseName = parentApplied.getStandardFileName();
        String fullName = getHistoryFileName(baseName);

        if ( parentApplied.getComponentAppliedCount() != 0 ) {
            BaseHandlerException exception =
                BaseFactory.saveSingleton(parentApplied, new AppliedWriter(), fullName);

            if ( exception != null ) {
                addException("WVER0267E",
                             new String[] { ptfId, componentName, fullName },
                             exception);
                return false;
            } else {
                return true;
            }
        } else {
            File appliedFile = new File(fullName);
            boolean result = appliedFile.delete();

            if ( !result ) {
                addException("WVER0268E",
                             new String[] { ptfId, componentName, fullName },
                             null);
            } else {
                unmapPTFApplied(parentApplied);
                removePTFAppliedName(baseName);
            }

            return result;
        }
    }

    // PTF application mapping ...
    //
    //    public Iterator getPTFApplieds();
    //    HashMap getPTFAppliedMapping();
    //    void ensurePTFApplieds();
    //    void mapPTFApplied(ptfApplied);
    //    void unmapPTFApplied(ptfApplied);
    //    void computePTFAppliedMapping(Iterator);
    //    ArrayList basicGetPTFApplieds();

    protected ArrayList ptfApplieds = null;
    protected HashMap ptfAppliedMapping = null;

    /**
     * <p>Answer an iterator across the PTF applieds
     * within the receiver.</p>
     *
     * @return Iterator An iterator across the PTF applieds
     *                  within the receiver.
     */

    public Iterator getPTFApplieds()
    {
        ensurePTFApplieds();

        return ptfApplieds.iterator();
    }

    protected HashMap getPTFAppliedMapping()
    {
        ensurePTFApplieds();

        return ptfAppliedMapping;
    }

    protected void ensurePTFApplieds()
    {
        if ( ptfApplieds == null ) {
            ptfApplieds = basicGetPTFApplieds();
            computePTFAppliedMapping(ptfApplieds.iterator());
        }
    }

    protected void mapPTFApplied(ptfApplied anApplied)
    {
        ptfAppliedMapping.put(anApplied.getPTFId(), anApplied);
    }

    protected void unmapPTFApplied(ptfApplied anApplied)
    {
        ptfAppliedMapping.remove(anApplied.getPTFId());
    }

    protected void computePTFAppliedMapping(Iterator ptfApplieds)
    {
        ptfAppliedMapping = new HashMap();

        while ( ptfApplieds.hasNext() ) {
            ptfApplied nextApplied = (ptfApplied) ptfApplieds.next();

            ptfAppliedMapping.put(nextApplied.getPTFId(), nextApplied);
        }
    }

    protected ArrayList basicGetPTFApplieds()
    {
        Iterator ptfAppliedNames = getPTFAppliedNames();

        return collectHistoryInfo(ptfAppliedNames);
    }

    // PTF applied name storage ...
    //
    //    void addPTFAppliedName(String);
    //    void removePTFAppliedName(String);
    //    public Iterator getPTFAppliedNames();
    //    ArrayList basicGetPTFAppliedNames();

    protected ArrayList ptfAppliedNames = null;

    protected void addPTFAppliedName(String filename)
    {
        ptfAppliedNames.add(filename);
    }

    protected void removePTFAppliedName(String filename)
    {
        ptfAppliedNames.remove( ptfAppliedNames.indexOf(filename) );
    }

    /**
     * <p>Answer an iterator across the ids of the PTF applieds
     * within the receiver.</p>
     *
     * @return Iterator An iterator across the ids of the PTF
     *                  applieds within the receiver.
     */

    public Iterator getPTFAppliedNames()
    {
        if ( ptfAppliedNames == null )
            ptfAppliedNames = basicGetPTFAppliedNames();

        return ptfAppliedNames.iterator();
    }

    protected ArrayList basicGetPTFAppliedNames()
    {
        return listFileNames( new PTFAppliedFilenameFilter() );
    }

    // History info cache ...
    //
    //    ArrayList collectHistoryInfo(Iterator);
    //    Object getHistoryInfo(String);
    //    Object loadHistoryInfo(String);

    protected HashMap historyInfo;

    protected ArrayList collectHistoryInfo(Iterator names)
    {
        ArrayList collectedInfo = new ArrayList();
        boolean encounteredError = false;

        while ( !encounteredError && names.hasNext() ) {
            String nextName = (String) names.next();

            // The information will be null if an error occurrs.

            Object nextInfo = getHistoryInfo(nextName);
            if ( nextInfo != null )
                collectedInfo.add(nextInfo);
        }

        return collectedInfo;
    }

    protected Object getHistoryInfo(String filename)
    {
        Object info = historyInfo.get(filename);

        if ( info == null ) {
            info = loadHistoryInfo(filename);

            // Will be null if an error occurred during the load.

            if ( info != null )
                historyInfo.put(filename, info);
        }

        return info;
    }

    // Load and return the history information from the specified file
    // in the history directory.
    //
    // Answer null if an error occurred during the load.
    // In this case, an exception will be bound.

    protected Object loadHistoryInfo(String filename)
    {
        BaseHandler handler;

        if ( AppliedHandler.acceptsEFixDriverFileName(filename) ||
             AppliedHandler.acceptsEFixAppliedFileName(filename) ||
             AppliedHandler.acceptsPTFDriverFileName(filename) ||
             AppliedHandler.acceptsPTFAppliedFileName(filename) ) {
            handler = new AppliedHandler();
        } else {
            addException("WVER0271E", new Object[] { filename }, null);
            return null;
        }

        // Don't need a writer in this handler.
        BaseFactory factory = new BaseFactory(handler, null);

        //        System.out.println(">> Loading with DTD: " + getDTDDirName());
        //        System.out.println(">> Loading with history dir name: " + getHistoryDirName());
        //        System.out.println(">> Loading with file name: " + filename);

        List loaded = factory.load(getDTDDirName(), getHistoryDirName(), filename);

        transferErrors(factory);

        BaseHandlerException handlerException = factory.getException();

        if ( handlerException != null ) {
            addException("WVER0272E", new Object[] { filename }, handlerException);
            return null;
        }

        return loaded.iterator().next();
    }

    // History directory access ...
    //
    //    ArrayList listFileNames(FilenameFilter);
    //    ArrayList listFileNames(File[]);
    //    File[] listFiles(final FilenameFilter);

    protected ArrayList<String> listFileNames(FilenameFilter filter)
    {
        File[] files = listFiles(filter);

        return listFileNames(files);
    }

    protected ArrayList<String> listFileNames(File[] files)
    {
        ArrayList<String> fileNames = new ArrayList<String>();
        for ( int fileNo = 0; fileNo < files.length; fileNo++ ){
            fileNames.add(files[fileNo].getName());
            long time = files[fileNo].lastModified();
//            System.out.print(calendar.getTimeZone().getDisplayName());
//            System.out.print(calendar.getTime().toString());
//            System.out.print(DateFormatUtil.format(String.valueOf(calendar.getTime()) + "\n"));
        	installedTime.add(String.valueOf(time));
        }
        return fileNames;
    }

    protected File[] listFiles(final FilenameFilter filter)
    {
        return getHistoryDir().listFiles(filter);
    }

    // Raw loading ...
    //
    //    public String[] loadAllHistoryInfoByType(String);
    //    public String loadHistoryInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    /**
     * <p>Typing constants for loading history info.</p>
     */

    public static final String EFIX_DRIVER_TYPE = "efixDriver" ;
    // Raw loading ...
    //
    //    public String[] loadAllHistoryInfoByType(String);
    //    public String loadHistoryInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    /**
     * <p>Typing constants for loading history info.</p>
     */

    public static final String EFIX_APPLIED_TYPE = "efixApplied" ;
    // Raw loading ...
    //
    //    public String[] loadAllHistoryInfoByType(String);
    //    public String loadHistoryInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    /**
     * <p>Typing constants for loading history info.</p>
     */

    public static final String PTF_DRIVER_TYPE = "ptfDriver" ;
    // Raw loading ...
    //
    //    public String[] loadAllHistoryInfoByType(String);
    //    public String loadHistoryInfoAsXMLString(String);
    //    String loadFile(File, String) throws FileNotFoundException, IOException;

    /**
     * <p>Typing constants for loading history info.</p>
     */

    public static final String PTF_APPLIED_TYPE = "ptfApplied" ;

    /**
     * <p>Answer a string array containing the contents of
     * all files in the bound history directory having the
     * specified type.</p>
     *
     * @param type The type of file to load; see the constants
     *             'EFIX_DRIVER_TYPE', and others.
     *
     * @return String[] An array holding the contents of the
     *                  files of the specified type.  Each file
     *                  is loaded as a single string.
     */

    public String[] loadAllHistoryInfoByType(String type)
    {
        FilenameFilter filter;

        if ( type.equalsIgnoreCase(EFIX_APPLIED_TYPE) )
            filter = new EFixAppliedFilenameFilter();
        if ( type.equalsIgnoreCase(EFIX_DRIVER_TYPE) )
            filter = new EFixDriverFilenameFilter();
        else if ( type.equalsIgnoreCase(PTF_DRIVER_TYPE) )
            filter = new PTFDriverFilenameFilter();
        else if ( type.equalsIgnoreCase(PTF_APPLIED_TYPE) )
            filter = new PTFAppliedFilenameFilter();
        else
            filter = null;

        if ( filter == null )
            return null;

        File[] files = listFiles(filter);

        String[] data = new String[files.length];
        for ( int fileNo = 0; fileNo < files.length; fileNo++ )
            data[fileNo] = loadHistoryInfoAsXMLString(files[fileNo].getName());

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

    public String loadHistoryInfoAsXMLString(String filename)
    {
        File historyFile = new File(getHistoryDir(), filename);

        if ( !historyFile.exists() )
            return "";

        try {
            return loadFile(historyFile, filename);

        } catch ( FileNotFoundException e ) {
            addException("WVER0203E", new Object[] { filename }, e);
            return "";

        } catch ( IOException e ) {
            addException("WVER0203E", new Object[] { filename }, e);
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

    // Text ...
    //
    // public String toString();

    /**
     * <p>Answer a string representation of the receiver.
     * This representation currently displays the bound product
     * directory name, the bound version directory name, and
     * the bound history directory name.</p>
     *
     * @return String A string representation of the receiver.
     */

    public String toString()
    {
        return "WASHistory: " + getProductDirName() +
               " :: "         + getVersionDirName() +
               " :: "         + getHistoryDirName();
    }

    // Location storage ...
    //
    //    public String getProductDirName();
    //    public String getVersionDirName();
    //    public String getDTDDirName();
    //    File getHistoryDir();
    //    public String getHistoryDirName();
    //    public String getHistoryFileName();
    //    public String getBackupFileName();
    //
    //    public String getHistoryFileName(String;)

    protected File productDir;
    protected String productDirName;

    protected File versionDir;
    protected String versionDirName;

    protected File dtdDir;
    protected String dtdDirName;

    protected File historyDir;
    protected String historyDirName;

    protected File historyFile;
    protected String historyFileName;

    protected File backupFile;
    protected String backupFileName;

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
	 * <p>Answer the version directory name bound into the receiver.</p>
	 * @return  String The name of the version directory bound  into the receiver.
	 * @uml.property  name="versionDirName"
	 */

    public String getVersionDirName()
    {
        return versionDirName;
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
	 * @return  the historyDir
	 * @uml.property  name="historyDir"
	 */
    protected File getHistoryDir()
    {
        return historyDir;
    }

    /**
	 * <p>Answer the name of the event history file.</p>
	 * @return  String The name of the event history file.
	 * @uml.property  name="historyDirName"
	 */

    public String getHistoryDirName()
    {
        return historyDirName;
    }

    /**
	 * <p>Answer the name of the event history file.</p>
	 * @return  String The name of the event history file.
	 * @uml.property  name="historyFileName"
	 */

    public String getHistoryFileName()
    {
        return historyFileName;
    }

    /**
	 * <p>Answer the name of the first backup file, to be used when saving the event history.</p>
	 * @return  String The name of the first backup file for use when  saving the event history.
	 * @uml.property  name="backupFileName"
	 */

    public String getBackupFileName()
    {
        return backupFileName;
    }

    /**
     * <p>Answer the name of the argument file, qualified to within the
     * history directory.</p>
     *
     * @return String The argument name, qualified to within the history
     *                directory.
     */

    public String getHistoryFileName(String baseName)
    {
        String fullName = getHistoryDirName() + File.separator + baseName;

        File fullFile = new File(fullName);

        return fullFile.getAbsolutePath();
    }
}
