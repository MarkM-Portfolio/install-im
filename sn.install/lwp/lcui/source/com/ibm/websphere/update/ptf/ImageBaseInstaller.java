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
 * Common image installer.
 *
 * History 1.8, 3/24/03
 *
 * 09-Jul-2002 Initial Version
 *
 * 16-Dec-2002 Split from 'efixBaseImageInstaller' to 'ImageBaseInstaller'.
 *             Modified to mesh with PTF function.
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import com.ibm.websphere.update.silent.UpdateReporter;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.util.*;
import com.ibm.websphere.update.delta.PasswordRemover;
/**
 *  
 */
public abstract class ImageBaseInstaller
    extends BaseInstaller
{
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "@BUILD_VERSION@" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "@BUILD_DATE@" ;

    // Log level support ...
    //   public static int readLogLevel();
    //   public static int getLogLevel();
    //
    // This is a debugging hook; the default, '2', will be
    // used in most cases.
    //
    // Note that the log level is not final.  This value is allowed
    // to be updated by later processing.

    public static final String
        logLevelPropertyName = "com.ibm.websphere.update.ptf.log.level";

    public static final int minLogLevel = 0 ;
    public static final int maxLogLevel = 9 ;
    public static final int defaultLogLevel = 3 ;

    protected static int logLevel = readLogLevel();

    public static int readLogLevel()
    {
        String logLevelValue = System.getProperty(logLevelPropertyName);

        if ( logLevelValue == null )
            return defaultLogLevel;

        int useLogLevel;

        try {
            useLogLevel = Integer.parseInt(logLevelValue);

        } catch ( NumberFormatException e ) {
            System.out.println("Warning: Log level is not a number: " + logLevelValue);
            System.out.println("Using default log level: " + defaultLogLevel);

            return defaultLogLevel;
        }

        if ( useLogLevel < minLogLevel ) {
            System.out.println("Warning: Log level is too low: " + logLevelValue);
            System.out.println("Using minimum log level: " + minLogLevel);

            return minLogLevel;
        }

        if ( useLogLevel > maxLogLevel ) {
            System.out.println("Warning: Log level is too high: " + logLevelValue);
            System.out.println("Using maximum log level: " + maxLogLevel);

            return maxLogLevel;
        }

        return useLogLevel;
    }

    /**
	 * @return  the logLevel
	 * @uml.property  name="logLevel"
	 */
    public static int getLogLevel()
    {
        return logLevel;
    }

    // Instantor ...
    //
    // public ImageBaseInstaller(WPProduct, WPHistory, Notifier, IOService);

    // Create a base image installer/uninstaller.
    //
    // The product and history objects provide the context for installation activity.
    // The notifier is used to provide live notification of installation activity.

    public ImageBaseInstaller(WPProduct wpsProduct, WPHistory wpsHistory,
                              Notifier notifier, IOService ioService)
    {
        super(wpsProduct, wpsHistory, notifier, ioService);

        setSelectedComponents(null);
        setIgnoreErrors(false);
    }

    // Selection override ...
    //
    // void setSelectedComponents(Vector);
    // boolean isSelective();
    // Vector getSelectedComponents();
    // boolean isSelected(String);
    // Vector getSelectedComponents();
    // boolean isSelected(String);

    // Selected components may be set for installation.
    // If set, then ignore the usual component selection
    // and use these instead.

    protected Vector selectedComponents;
    protected Hashtable selectionMap;

    /**
	 * @param selectedComponents  the selectedComponents to set
	 * @uml.property  name="selectedComponents"
	 */
    protected void setSelectedComponents(Vector selectedComponents)
    {
        this.selectedComponents = selectedComponents;

        if ( this.selectedComponents == null ) {
            selectionMap = null;
        } else {
            selectionMap = new Hashtable();

            int numSelected = selectedComponents.size();

            for ( int selectionNo = 0; selectionNo < numSelected; selectionNo++ ) {
                if ( selectedComponents.elementAt(selectionNo) instanceof String ){
                    String nextComponentName = (String)
                        selectedComponents.elementAt(selectionNo);
                    selectionMap.put(nextComponentName, nextComponentName);
                }else {
                    ExtendedComponent ec = (ExtendedComponent) selectedComponents.elementAt(selectionNo);
                    String nextComponentName = ec.getBaseComponent().getName();
                    selectionMap.put(nextComponentName, ec);
                }


            }
        }
    }

    protected boolean isSelective()
    {
        return ( selectedComponents != null );
    }

    /**
	 * @return  the selectedComponents
	 * @uml.property  name="selectedComponents"
	 */
    protected Vector getSelectedComponents()
    {
        return selectedComponents;
    }

    protected boolean isSelected(String componentName)
    {
        return ( (selectionMap != null) && selectionMap.containsKey(componentName) );
    }

    // Forcing ...
    //
    // void setIgnoreErrors(boolean);
    // boolean getIgnoreErrors();

    protected boolean ignoreErrors;

    /**
	 * @param ignoreErrors  the ignoreErrors to set
	 * @uml.property  name="ignoreErrors"
	 */
    protected void setIgnoreErrors(boolean ignoreErrors)
    {
        this.ignoreErrors = ignoreErrors;
    }

    /**
	 * @return  the ignoreErrors
	 * @uml.property  name="ignoreErrors"
	 */
    protected boolean getIgnoreErrors()
    {
        return ignoreErrors;
    }

    // Event handling ...
    //
    //    updateEvent createUpdateEvent();
    //    void completeEvent(updateEvent, boolean);
    //    void failEvent(updateEvent);
    //
    //    boolean saveEvents(boolean);

    // Create and return an update event for the active efix.
    // Make the efix, the time, and the action, and set the log name
    // for this event.

    protected abstract updateEvent createUpdateEvent(); 

    protected static final boolean WAS_CANCELLED = true ;
    protected static final boolean WAS_NOT_CANCELLED = false ;

    protected void completeEvent(updateEvent event, boolean wasCancelled)
    {
        event.setEndTimeStamp( getTimeStamp() );

        enumEventResult result;
        String resultMessage;

        Iterator useExceptions = consumeExceptions();

        if ( useExceptions.hasNext() ) {
            event.setFailed();
            resultMessage = collateExceptions(useExceptions);

        } else if ( wasCancelled ) {
            event.setCancelled();
            resultMessage = getCancelledMessage();

        } else {
            event.setSucceeded();
            resultMessage = getSucceededMessage();
        }

        event.setResultMessage(resultMessage);
    }

    protected void failEvent(updateEvent event)
    {
        Iterator useExceptions = consumeExceptions();
        String newMessage = collateExceptions(useExceptions);

        if ( event.getResultAsEnum() == enumEventResult.FAILED_EVENT_RESULT ) {
            newMessage =
                event.getResultMessage() +
                System.getProperty("line.separator") +
                newMessage;
        } else {
            event.setFailed();
        }

        event.setResultMessage(newMessage);
    }

    public static final boolean CREATE_BACKUP = true ;
    public static final boolean OMIT_BACKUP = false ;

    protected boolean saveEvents(boolean createBackup)
    {
        if ( !doHistory )
            return true;

        logFlush("Saving History ...");

        WPHistory useHistory = getWPHistory();

        boolean didSave = useHistory.save(createBackup);

        if ( !didSave ) {
            Iterator historyExceptions = useHistory.getExceptions();

            while ( historyExceptions.hasNext() ) {
                WASHistoryException nextException = (WASHistoryException)
                    useHistory.getExceptions().next();

                addException("WUPD0200E",
                             new String[] { useHistory.getHistoryFileName() },
                             nextException);
            }

            log("Saving History ... Failed");

        } else {
            log("Saving History ... Done");
        }

        logFlush("");

        return didSave;
    }

    // Logging ...
    //
    //    boolean initializeLogAndBackup();
    //    boolean openLog(updateEvent);
    //
    //    void writeStartingLog(updateEvent);
    //    void writeEndingLog(updateEvent);
    //
    //    void writeStartingComponentLog(updateEvent);
    //    void writeEndingComponentLog(updateEvent);

    protected boolean initializeLogAndBackup()
    {
        WPProduct useProduct = getWPProduct();

        log("Initializing log directory   : ", useProduct.getLogDirName());
        log("Initializing backup directory: ", useProduct.getBackupDirName());

        if ( !useProduct.initializeLogAndBackup() ) {
            Iterator productExceptions = useProduct.getExceptions();

            while ( productExceptions.hasNext() ) {
                WASProductException nextException = (WASProductException)
                    productExceptions.next();

                addException("WUPD0249E", new String[] { getUpdateId() }, nextException);
            }

            log("Placing directories ... failed");
            logFlush("");

            return false;

        } else {
            log("Placing directories ... done");
            logFlush("");

            return true;
        }
    }

    protected boolean openLog(updateEvent event)
    {
        if( !doLogging )
            return true;

        String logFileName = event.getLogName();

        try {
            openLog(logFileName); // throws IOException
            return true;

        } catch ( IOException e ) {
            addException("WUPD0201E", new String[] { logFileName }, e);
            return false;
        }
    }

    protected void writeStartingLog(updateEvent event)
    {
        String useTypeName = getImageTypeName();

        log("IBM WebSphere Portal " + useTypeName + " Activity Log");
        logDashes();

        logPrefix("Installer Version",  pgmVersion);
        logPrefix("Installer Date",     pgmUpdate);
        logDashes();

        logPrefix("Log Name",           event.getLogName());
        logDashes();

        logPrefix("Product Directory",  getWPProduct().getVersionDirName());
        logPrefix("History Directory",  getWPHistory().getHistoryDirName());
        logDashes();

        logPrefix("TMP Directory",      getWPProduct().getTmpDirName());
        logDashes();

        log("");
        log(useTypeName + " Activity:");
        logDashes();

        String useJarName = getUpdateJarName();
        if ( useJarName != null )
            logPrefix(useTypeName + " Jar",    useJarName);
        
        logPrefix(useTypeName + " ID",     getUpdateId());
        logPrefix(useTypeName + " Action", event.getUpdateAction() );
        logPrefix("Time Stamp (Start)", event.getStartTimeStamp());
        logDashes();
        logFlush("");
    }

    protected void writeEndingLog(updateEvent event)
    {
        String useTypeName = getImageTypeName();

        log(useTypeName + " Results:");
        logDashes();

        logPrefix("Time Stamp (End)",      event.getEndTimeStamp());
        logPrefix(useTypeName + " Result", event.getResult());

        log(useTypeName + " Result Message:");
        logDashes();
        log(event.getResultMessage());
        logDashes();

        logFlush("");
    }

    protected void writeStartingComponentLog(updateEvent childEvent)
    {
        log("");
        log(getImageTypeName() + " Component Activity:");
        logDashes();

        logPrefix("Component Name",     childEvent.getId());
        logPrefix("Action",             childEvent.getUpdateActionAsEnum().toString());
        logPrefix("Time Stamp (Start)", childEvent.getStartTimeStamp());
        logDashes();

        logPrefix("Log File Name",      childEvent.getLogName());
        logPrefix("Backup File Name",   childEvent.getBackupName());
        logDashes();
        logFlush("");
        
   }

    // Remove plain text passwords from the log files
    private void removePassword(String logfile)
    {
        // space at the end is needed, don't remove it
        String searchStr = "property: WasPassword already set to: ";
        
        searchAndReplaceLog(searchStr, logfile);
        
    }
    
    // this function goes thru an input file line-by-line to search for
    // a specific string, if it is found, replace its value with PASSWORD_REMOVED
    // and then write the new line to an temp output file.
    // finally, when it is done, delete the original file and rename the tmp
    // output file with the original file name.
    protected void searchAndReplaceLog(String searchStr, String logFile)
    {
        File fin = new File(logFile);
        
        File fout = new File(logFile+"_tmp");
        
        FileInputStream fis;
        FileOutputStream fos;
        try
        {
            fis = new FileInputStream(fin);
            fos = new FileOutputStream(fout);

            BufferedReader in = new BufferedReader(
                           new InputStreamReader(fis));
            BufferedWriter out = new BufferedWriter(
                          new OutputStreamWriter(fos));
            
            String aLine = null;
            try
            {
                while((aLine = in.readLine()) != null) 
                {
                    String output = PasswordRemover.removePassword(aLine, searchStr);
                    
                    out.write(output);
                    out.newLine();
                }
                in.close();
                out.close();
                
                // delete the original
                if(fin.delete())
                {
                    // rename the tmp with the original file name
                    fout.renameTo(fin);
                }
                
                
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

            
         } catch (FileNotFoundException e)
        {
             System.out.println(e.getMessage());
        }
    }
    
    protected void writeEndingComponentLog(updateEvent event)
    {
        String useTypeName = getImageTypeName();

        log("Results:");
        logDashes();
        logPrefix("Time Stamp (End)", event.getEndTimeStamp());
        logPrefix(useTypeName + " Component Result", event.getResult());

        log(useTypeName + " Component Result Message:");
        logDashes();
        log(event.getResultMessage());
        logDashes();
        log("");
        log(useTypeName + " Component Installation ... Done");
        logFlush("");
        
        // password scrubbing
        removePassword(event.getLogName());
        
    }

    // Component installation ...
    //
    //    updateEvent createComponentEvent(updateEvent, String);
    //    boolean bypassComponentAction();

    protected updateEvent createComponentEvent(updateEvent parentEvent,
                                               componentUpdate childUpdate)
    {
        updateEvent childEvent = parentEvent.addUpdateEvent();

        childEvent.setEventType(enumEventType.COMPONENT_EVENT_TYPE);
        childEvent.setParentId( getUpdateId() );
        childEvent.setId( childUpdate.getComponentName() );

        childEvent.setUpdateAction( getComponentUpdateAction() );

        childEvent.setIsCustom( childUpdate.getIsCustom() );
        childEvent.setPrimaryContent( childUpdate.getPrimaryContent() );

        childEvent.setUpdateType( childUpdate.getUpdateTypeAsEnum() );

        childEvent.setIsExternal( childUpdate.getIsExternal() );
        childEvent.setRootPropertyFile( childUpdate.getRootPropertyFile() );
        childEvent.setRootPropertyName( childUpdate.getRootPropertyName() );
        childEvent.setRootPropertyValue( childUpdate.getRootPropertyValue() );

        // Need to set the time stamp before setting the log and backup file names.

        childEvent.setStartTimeStamp();

        childEvent.setStandardLogName(getWPProduct().getLogDirName());
        childEvent.setStandardBackupName(getWPProduct().getBackupDirName());

        return childEvent;
    }

    public static final String SKIP_COMPONENTS_PROPERTY_NAME = "update.skip.components" ;
    public static final String SKIP_COMPONENTS_PROPERTY_VALUE = "true" ;
    public static final String DO_COMPONENTS_PROPERTY_VALUE = "false" ;

    protected boolean bypassComponentAction()
    {
        String skipValue = System.getProperty(SKIP_COMPONENTS_PROPERTY_NAME);

        if ( (skipValue != null) && skipValue.equals(SKIP_COMPONENTS_PROPERTY_VALUE) ) {
            log("Bypassing component action!");
            logFlush("");

            return true;
        } else {
            return false;
        }
    }

    // Subclass Protocol ... specialized by the type of
    // update.

    // Update Abstract Access ... Derived Layer ...
    //
    //    abstract boolean updateIsPresent();
    //    abstract boolean updateApplicationIsPresent();
    //
    //    abstract Object getUpdateById();
    //    abstract Object getUpdateDriverById();

    protected abstract boolean updateIsPresent();
    protected abstract boolean updateApplicationIsPresent();

    protected abstract Object getUpdateById();
    protected abstract Object getUpdateDriverById();

    // Labelling Abstract Access ...
    //
    // Answer a label to describe the image type; either 'EFix' or 'PTF'.
    //
    //    abstract String getUpdateTypeName();

    protected abstract String getImageTypeName();

    // Subclass Protocol ... filled in by the concrete class:
    // one of 'efixInstaller', 'efixUninstaller',
    // 'ptfInstaller', 'ptfUninstaller' ...

    // Update Abstract Access ... Base Layer ...
    //
    //    abstract String getUpdateId();
    //    abstract String getUpdateJarName();

    protected abstract String getUpdateId();
    protected abstract String getUpdateJarName();

    // Action Abstract Access ...
    //
    //    abstract enumAction getUpdateAction();
    //    abstract enumAction getComponentUpdateAction();

    protected abstract enumUpdateAction getUpdateAction();
    protected abstract enumUpdateAction getComponentUpdateAction();

    // Status Messaging ...
    //
    //    abstract String getCancelledMessage();
    //    abstract String getSucceededMessage();

    protected abstract String getCancelledMessage();
    protected abstract String getSucceededMessage();

    protected boolean checkPUILevel( customProperty custom, String errorMsgId ) {
       String buildLevel = System.getProperty( WPConstants.PROP_WP_BLD_LEVEL );
       String reqLevel   = custom.getPropertyValue();
       // Assume buildLevel is of the form yyyymmdd_hhmm ( all in numeric digits )
       // TODO: If the build level is different from above this may need to change\
       boolean puiOK = buildLevel.compareTo( reqLevel ) >= 0;

       //log("Level of Portal UpdateInsatller " + buildLevel + " Required level " + reqLevel + " is OK? " + puiOK );
       //puiOK = false;
       if (!puiOK) {
          log("Level of Portal UpdateInsatller " + buildLevel + " is too old.  The level is required to be " + reqLevel + " or later." );
          log("  Please download the latest level." );
          logFlush("");
          addException( errorMsgId, new String[] {buildLevel, reqLevel}, null );
       }
       return puiOK;
    }

}
