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
package com.ibm.websphere.update.ptf;


/*
 * Base Installer
 *
 * History 1.10, 3/26/03
 *
 * 05-Aug-2002 Initial Version
 *
 * 16-Aug-2002 Renamed from 'efixBaseInstaller' to 'BaseInstaller';
 *             Added PTF Function
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.CalendarUtil;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.product.xml.product.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import com.ibm.websphere.update.silent.UpdateInstallerArgs;
import java.io.*;
import java.util.*;

/**
 *  
 */
public abstract class BaseInstaller
{
    // Program versioning ...

    // Top Level Method Index:
    //
    // Messaging (static)
    // Time Stamp Access (static)
    // Instantor
    // WASProduct, WASHistory Access
    // IOService Access
    // Notification Access
    // Log Handling
    // Error Handling

    public static final String pgmVersion = "1.10" ;
    // Program versioning ...

    // Top Level Method Index:
    //
    // Messaging (static)
    // Time Stamp Access (static)
    // Instantor
    // WASProduct, WASHistory Access
    // IOService Access
    // Notification Access
    // Log Handling
    // Error Handling

    public static final String pgmUpdate = "3/26/03" ;

    // Messaging ...
    //
    // static String getString(String);
    // static String getString(String, String)
    // static String getString(String, String, String)
    // static String getString(String, Object[]);

    // Answer the NLS text for the specified message id.

    protected static String getString(String msgCode)
    {
        return InstallerException.getString(msgCode);
    }

    // Answer the NLS text for the specified message id, substituting
    // in the argument to the message text.

    protected static String getString(String msgCode, String arg)
    {
        return InstallerException.getString(msgCode, arg);
    }

    protected static String getString(String msgCode, String arg1, String arg2)
    {
        return InstallerException.getString(msgCode, arg1, arg2);
    }

    protected static String getString(String msgCode, Object[] args)
    {
        return InstallerException.getString(msgCode, args);
    }

    // Time Stamp access ...
    //
    // static String getTimeStamp();

    // Answer the current date and time

    protected static String getTimeStamp()
    {
        return CalendarUtil.getTimeStampAsString();
    }

    // Instantor ...
    //
    // public BaseInstaller(WASProduct, WASHistory, Notifier, IOService);

    protected WPProduct wpsProduct;           // Provides access to product installation status.
    protected WPHistory wpsHistory;           // Provides access to product installation history.
    protected boolean didSetIsJ2EEClient;      
    protected boolean isJ2EEClient;      
    protected boolean didSetIsClient;      
    protected boolean isClient;      

    protected Notifier notifier;               // Accepts notification of install progress.
    protected IOService ioService;             // Passed in for use during component updates.
    protected boolean doBackup = true;		// Determines if component backup jars are created
    protected boolean doHistory = true;		// Determines if history updates are recorded
    protected boolean doLogging = true;		// Determines if logging occurs

    // Create an installer.  The product and history objects provide
    // the context for installation activity.  The notifier is used to
    // provide live notification of installation activity.  The IO
    // services and update refector are needed for component installation.

    public BaseInstaller(WPProduct wpsProduct, WPHistory wpsHistory,
                         Notifier notifier, IOService ioService)
    {
        this.computedDebugMode = false;
        this.debugIsEnabled = false;

        this.wpsProduct = wpsProduct;
        this.wpsHistory = wpsHistory;
        this.didSetIsJ2EEClient = false;
        this.isJ2EEClient = false;
        this.didSetIsClient = false;
        this.isClient = false;

        this.notifier = notifier;
        this.ioService = ioService;

        this.clearExceptions();

        this.logFile = null;
        this.unconsumedLogData = new Vector();

        doBackup = UpdateInstallerArgs.doBackup();
        doHistory = UpdateInstallerArgs.doHistory();
        doLogging = UpdateInstallerArgs.doLogging();
    }
    
    public BaseInstaller(WPProduct wpsProduct, WPHistory wpsHistory, IOService ioService)
    {
		this.computedDebugMode = false;
		this.debugIsEnabled = false;
		
		this.wpsProduct = wpsProduct;
		this.wpsHistory = wpsHistory;
		this.didSetIsJ2EEClient = false;
		this.isJ2EEClient = false;
		this.didSetIsClient = false;
		this.isClient = false;
		
		this.notifier = null;
		this.ioService = ioService;
		
		this.clearExceptions();
		
		this.logFile = null;
		this.unconsumedLogData = new Vector();
		
		doBackup = UpdateInstallerArgs.doBackup();
		doHistory = UpdateInstallerArgs.doHistory();
		doLogging = UpdateInstallerArgs.doLogging();	
    }

    // WASProduct and WASHistry access ...
    //
    //    public WASProduct getWASProduct();
    //    boolean initializeLogAndBackup();
    //
    //    boolean componentIsPresent(String);
    //
    //    boolean efixIsPresent(String);
    //    efix getEfixById(String);
    //
    //    boolean ptfIsPresent(String);
    //    ptf getPTFById(String);
    //
    //    public WASHistory getWASHistory();
    //
    //    efixDriver getEFixDriverById(String);
    //    boolean componentWasUpdatedByEFix(String, String);
    //    boolean efixApplicationIsPresent(String);
    //
    //    ptfDriver getPTFDriverById(String);
    //    boolean componentWasUpdatedByPTF(String, String);
    //    boolean ptfApplicationIsPresent(String);
     /*
    public WPProduct getWASProduct()
    {
        return wpsProduct;
    }
      */
    public WPProduct getWPProduct()
    {
        return wpsProduct;
    }

    /**
	 * @return  the isJ2EEClient
	 * @uml.property  name="isJ2EEClient"
	 */
    public boolean getIsJ2EEClient() {
        if (!didSetIsJ2EEClient) {
            didSetIsJ2EEClient = true;

            isJ2EEClient = 
                ( (getCurrentProduct(WASProduct.PRODUCTID_CLIENT) != null) &&
                  componentIsPresent("prereq.wccm") );
        }

        return isJ2EEClient;
    }
    /**
	 * @return  the isClient
	 * @uml.property  name="isClient"
	 */
    public boolean getIsClient() {
        if (!didSetIsClient) {
            didSetIsClient = true;
            if (getCurrentProduct(WASProduct.PRODUCTID_CLIENT) != null) {
                isClient = true;
            }
        }

        return isClient;
    }

    protected boolean componentIsPresent(String componentName)
    {
        return getWPProduct().componentPresent(componentName);
    }

    protected boolean efixIsPresent(String efixId)
    {
        return getWPProduct().efixPresent(efixId);
    }

    protected efix getEFixById(String efixId)
    {
        return getWPProduct().getEFixById(efixId);
    }

    protected boolean ptfIsPresent(String ptfId)
    {
        return getWPProduct().ptfPresent(ptfId);
    }

    protected ptf getPTFById(String ptfId)
    {
        return getWPProduct().getPTFById(ptfId);
    }

    /*
    public WPHistory getWASHistory()
    {
        return wpsHistory;
    }
    */

    public WPHistory getWPHistory()
    {
        return wpsHistory;
    }

    protected efixDriver getEFixDriverById(String efixId)
    {
        return getWPHistory().getEFixDriverById(efixId);
    }

    protected boolean componentWasUpdatedByEFix(String efixId, String componentName)
    {
        return getWPHistory().efixComponentAppliedPresent(efixId, componentName);
    }

    protected boolean efixApplicationIsPresent(String efixId)
    {
        return getWPHistory().efixAppliedPresent(efixId);
    }

    protected ptfDriver getPTFDriverById(String ptfId)
    {
        return getWPHistory().getPTFDriverById(ptfId);
    }

    protected boolean componentWasUpdatedByPTF(String ptfId, String componentName)
    {
        return getWPHistory().ptfComponentAppliedPresent(ptfId, componentName);
    }

    protected boolean ptfApplicationIsPresent(String ptfId)
    {
        return getWPHistory().ptfAppliedPresent(ptfId);
    }


	protected product getCurrentProduct(String productId) {
		product currentProduct = null;

		product nextProduct = wpsProduct.getProductById(productId);

		//scan for product file existence
		if (nextProduct != null) {
			currentProduct = nextProduct;
		}

		return currentProduct;
	}

	protected boolean isIHS(String dir) {
		return (new File(dir + File.separator + "conf" + File.separator + "httpd.conf").exists());
	}

	protected boolean isWASLite(String dir) {
		return (new File(dir + File.separator + "java").exists()) && !(new File(dir + File.separator + "config").exists());
	}

	protected boolean isWASPlugin(String dir) {	
		String[] pluginFiles = new File(dir + File.separator + "bin").list();
		String[] baseLibFiles = new File(dir + File.separator + "lib").list();
		int numTokens = 0;

		if (pluginFiles != null) {
	
			for (int i = 0; i < pluginFiles.length; i++) {		
				if (pluginFiles[i].startsWith("mod_app_server_http_eapi")) {		
					numTokens++;
				}

				if (pluginFiles[i].startsWith("mod_ibm_app_server_http")) {		
					numTokens++;
				}

				if (pluginFiles[i].startsWith("ns41_http")) {		
					numTokens++;
				}
			}
		}
		
		if (baseLibFiles != null) {		
			for (int i = 0; i < baseLibFiles.length; i++) {		
				if (baseLibFiles[i].equals("base.jar")) {		
					numTokens++;
				}
				
				if (baseLibFiles[i].equals("dynacache.jar")) {		
					numTokens++;
				}
				
			}
		}
				
		if (numTokens == 3) {
			return true;
		}

		return false;
	}


    // IO Services access ...
    //
    //    public IOServices getIOService();

    // Answer the io services available in the receiver.

    public IOService getIOService()
    {
        return ioService;
    }

    // Notification ...
    //
    //    public Notifier getNotifier();
    //
    //    void setBanner(String);
    //    void setTaskCount(int);
    //    boolean wasCancelled();
    //    void pushBanner(String);
    //    String popBanner();
    //    void beginTask();
    //    void endTask();
    //
    //    void beginTaskGroup(String);
    //    String nextTaskInGroup(String);
    //    String completedTaskGroup();

    /**
	 * @return  the notifier
	 * @uml.property  name="notifier"
	 */
    public Notifier getNotifier()
    {
        return notifier;
    }

    protected void setTaskCount(int taskCount)
    {
        getNotifier().setTaskCount(taskCount);
    }

    protected boolean wasCancelled()
    {
        return getNotifier().wasCancelled();
    }

    protected void pushBanner(String taskBanner)
    {
        getNotifier().pushBanner(taskBanner);
    }

    protected String popBanner()
    {
        return getNotifier().popBanner();
    }

    protected void beginTask()
    {
        String notificationText = getNotifier().beginTask();

        logFlush("Notification: ", notificationText);
    }

    protected void endTask()
    {
        String notificationText = getNotifier().endTask();

        logFlush("Notification: ", notificationText);
    }

    protected void beginTaskGroup(String banner)
    {
        pushBanner(banner);

        beginTask();
    }

    protected String nextTaskInGroup(String banner)
    {
        endTask();

        String oldBanner = getNotifier().replaceBanner(banner);

        beginTask();

        return oldBanner;
    }

    protected String completeTaskGroup()
    {
        endTask();

        return popBanner();
    }


    // Log handling ...
    //
    //    LogFile openLog(String) throws IOException;
    //    void closeLog();
    //    LogFile getLogFile();
    //    void log(Object);
    //    void log(Object, Object);
    //    void logTime();
    //    void logDashes();
    //    void logFlush();
    //    void logFlush(Object);
    //    void logFlush(Object, Object);
    //    void pendLogData(Object);
    //    void pendLogData(Object, Object);
    //    void consumeLogData();

    protected Vector unconsumedLogData;
    protected LogFile logFile;

    protected void openLog(String logName)
        throws IOException
    {
        LogFile useLog = new StandardLogFile(logName);
        useLog.open(); // throws IOException

        logFile = useLog;

        consumeLogData();
    }

    protected void closeLog()
    {
        try {
        	if(logFile != null)
            logFile.close(); // throws IOException

        } catch ( IOException e ) {
            // Don't know what to do at this point: the log can no
            // longer be used, and the state of the update has
            // been consumed!
        }

        logFile = null;
    }

    /**
	 * @return  the logFile
	 * @uml.property  name="logFile"
	 */
    protected LogFile getLogFile()
    {
        return logFile;
    }

    protected void log(Object arg)
    {
        if(!doLogging)
            return;
        LogFile useLogFile = getLogFile();

        if ( useLogFile == null )
            pendLogData(arg);
        else
            useLogFile.println(arg);
    }

    protected void log(Object arg1, Object arg2)
    {
        if(!doLogging)
    		return;
        LogFile useLogFile = getLogFile();

        if ( useLogFile == null )
            pendLogData(arg1, arg2);
        else
            useLogFile.println(arg1, arg2);
    }

    protected void logTime()
    {
        log(getTimeStamp());
    }

    protected void logDashes()
    {
    	if(!doLogging)
    		return;
        log("=========================================================");
    }

    protected void logFlush()
    {
    	if(!doLogging)
    		return;
        LogFile useLogFile = getLogFile();
        if ( useLogFile != null )
            useLogFile.flush();
    }

    protected void logFlush(Object arg)
    {
    	if(!doLogging)
    		return;
        LogFile useLogFile = getLogFile();

        if ( useLogFile == null )
            pendLogData(arg);
        else
            useLogFile.flush(arg);
    }

    protected void logFlush(Object arg1, Object arg2)
    {
    	if(!doLogging)
    		return;
        LogFile useLogFile = getLogFile();

        if ( useLogFile == null )
            pendLogData(arg1, arg2);
        else
            useLogFile.flush(arg1, arg2);
    }

    protected void pendLogData(Object arg)
    {
        unconsumedLogData.addElement(new Object[] { arg } );
    }

    protected void pendLogData(Object arg1, Object arg2)
    {
        unconsumedLogData.addElement(new Object[] { arg1, arg2 } );
    }

    protected void consumeLogData()
    {
        LogFile useLogFile = getLogFile();

        int pendCount = unconsumedLogData.size();

        for ( int pendNo = 0; pendNo < pendCount; pendNo++ ) {
            Object[] nextPendingData = (Object[])
                unconsumedLogData.elementAt(pendNo);

            if ( nextPendingData.length == 1 ) {
                useLogFile.println(nextPendingData[0]);
            } else {
                useLogFile.println(nextPendingData[0],
                                   nextPendingData[1]);
            }
        }

        useLogFile.flush();

        unconsumedLogData = new Vector();
    }

    // Error handling ...
    //
    // boolean debugIsEnabled();
    //
    // String collateExceptions(Iterator);
    // Iterator consumeExceptions();
    // Iterator getExceptions();
    //
    // void clearExceptions();
    // void addException(InstallerException);
    //
    // void addException(String);
    // void addException(String, Exception);
    // void addException(String, Object[], Exception);
    //
    // InstallerException createException(String, Object[], Exception);

    public static final String DEBUG_MODE_PROPERTY_NAME = "com.ibm.ws.update.debug" ;
    // Error handling ...
    //
    // boolean debugIsEnabled();
    //
    // String collateExceptions(Iterator);
    // Iterator consumeExceptions();
    // Iterator getExceptions();
    //
    // void clearExceptions();
    // void addException(InstallerException);
    //
    // void addException(String);
    // void addException(String, Exception);
    // void addException(String, Object[], Exception);
    //
    // InstallerException createException(String, Object[], Exception);

    public static final String DEBUG_MODE_ENABLED_VALUE = "true" ;

    protected boolean computedDebugMode;
    protected boolean debugIsEnabled;

    protected boolean debugIsEnabled()
    {
        if ( !computedDebugMode ) {
            computedDebugMode = true;

            String modeValue = System.getProperty(DEBUG_MODE_PROPERTY_NAME);

            debugIsEnabled = ( (modeValue != null) &&
                               modeValue.equals(DEBUG_MODE_ENABLED_VALUE) );
        }

        return debugIsEnabled;
    }

    protected Vector boundExceptions;

    // Collate the text of the argument exceptions into a single string.
    // Answer that string.

    protected String collateExceptions(Iterator exceptions)
    {
        StringBuffer messageBuffer = new StringBuffer();

        boolean onFirst = true;
        String lineSeparator = System.getProperty("line.separator");

        while ( exceptions.hasNext() ) {
            InstallerException nextException =
                (InstallerException) exceptions.next();

            String nextMessage;

            try {
                nextMessage = nextException.toString();
            } catch ( Exception e ) {
                nextMessage =
                    "Unable to retrieve message for exception of type " +
                    nextException.getClass().getName();
            }

            if ( !onFirst )
                messageBuffer.append(lineSeparator);
            else
                onFirst = false;

            messageBuffer.append(nextMessage);
        }

        return messageBuffer.toString();
    }

    // Answer the bound exceptions, but clear them as well.
    // The exceptions will be successfully returned.

    protected Iterator consumeExceptions()
    {
        Iterator exceptions = getExceptions();

        clearExceptions();

        return exceptions;
    }

    // Answer the bound exceptions.

    protected Iterator getExceptions()
    {
        return boundExceptions.iterator();
    }

    // Clear the exceptions bound into the receiver.

    protected void clearExceptions()
    {
        boundExceptions = new Vector();
    }

    // Add the exception into the receiver.

    protected void addException(InstallerException e)
    {
        boundExceptions.addElement(e);

        log("");
        log("Exception: ", e.toString());
        logFlush("");

        if ( debugIsEnabled() )
            e.printStackTrace(System.out);
    }

    // Create an installer exception with NLS text for the specified message
    // id, and add that exception to the receiver.

    protected void addException(String msgKey)
    {
        addException( createException(msgKey, null, null) );
    }

    // Create an installer exception with NLS text for the specified message
    // id and wrapping the specified exception, and add the new installer
    // exception to the receiver.

    protected void addException(String msgKey, Exception e)
    {
        addException( createException(msgKey, null, e) );
    }

    // Create an installer exception with NLS text for the specified message
    // id, with the specified substitutions, and wrapping the specified
    // exception, and add the new installer exception to the receiver.

    protected void addException(String msgKey, Object[] msgArgs, Exception e)
    {
        addException( createException(msgKey, msgArgs, e) );
    }

    // Create and return an installer exception with NLS text for the specified
    // message id, with the specified substitutions, and wrapping the specified
    // exception.  The substitutions and the exception may be null.

    protected InstallerException
        createException(String msgKey, Object[] msgArgs, Exception e)
    {
        return new InstallerException(msgKey, msgArgs, e);
    }

    // Component testing ...
    //
    //// protected boolean isEFixComponentInstallable(String efixId, String componentName)
    //// protected boolean isEFixComponentUninstallable(String efixId, String componentName)
    //
    //// protected boolean isPTFComponentInstallable(String ptfId, String componentName)
    //// protected boolean isPTFComponentUninstallable(String ptfId, String componentName)

    protected boolean isEFixComponentInstallable(String efixId, String componentName)
    {
        return ( componentIsPresent(componentName) &&
                 !componentWasUpdatedByEFix(efixId, componentName) );
    }

    protected boolean isEFixComponentUninstallable(String efixId, String componentName)
    {
        return ( componentIsPresent(componentName) &&
                 componentWasUpdatedByEFix(efixId, componentName) );
    }

    protected boolean isPTFComponentInstallable(String ptfId, String componentName)
    {
        return ( componentIsPresent(componentName) &&
                 !componentWasUpdatedByPTF(ptfId, componentName) );
    }

    protected boolean isPTFComponentUninstallable(String ptfId, String componentName)
    {
        return ( componentIsPresent(componentName) &&
                 componentWasUpdatedByPTF(ptfId, componentName) );
    }

    // Common constants for error handling ...

    public static final boolean DO_IGNORE_ERRORS = true ;
    // Common constants for error handling ...

    public static final boolean DO_NOT_IGNORE_ERRORS = false ;

    // String helper ...
    //
    // String fill(String, int);

    public static final String fillSpaces =
        "                                                                                ";

    public static final int maxFillLength = fillSpaces.length();

    protected String fill(String initial, int targetLength)
    {
        int initialLength = initial.length();

        if ( initialLength >= targetLength )
            return initial;

        int missing = targetLength - initialLength;

        if ( missing > maxFillLength )
            missing = maxFillLength;

        return initial + fillSpaces.substring(0, missing);
    }

    public static final int prefixWidth = 22;

    protected void logPrefix(String prefix, String value)
    {
        log( fill(prefix, prefixWidth) + ": " + value );
    }
}
