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
package com.ibm.websphere.update.efix;

/*
 * eFix Base Single Image Installer/Uninstaller
 *
 * History 1.3, 9/20/02
 *
 * 09-Jul-2002 Initial Version
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import java.io.*;
import java.security.*;
import java.util.*;


/**
 *  
 */
public abstract class efixBaseImageInstaller
    extends efixBaseInstaller
{
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "@BUILD_VERSION@" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "@BUILD_DATE@" ;

    // Instantor ...
    //
    // public efixBaseImageInstaller(WPProduct, WPHistory, Notifier, IOService);

    // Create a base efix image installer/uninstaller.  This superclass provides
    // common function for efix installation and uninstallation for a single
    // efix.
    //
    // The product and history objects provide the context for installation activity.
    // The notifier is used to provide live notification of installation activity.

    public efixBaseImageInstaller(WPProduct wpsProduct, WPHistory wpsHistory,
                                  Notifier notifier, IOService ioService)
    {
        super (wpsProduct, wpsHistory, notifier, ioService);

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
                String nextComponentName = (String)
                    selectedComponents.elementAt(selectionNo);

                selectionMap.put(nextComponentName, nextComponentName);
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
        return ( (selectionMap != null) && selectionMap.contains(componentName) );
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

    // EFix Access ...
    //
    //    abstract String getEFixId();
    //    abstract String getEFixJarName();
    //
    //    boolean efixIsPresent();
    //    boolean efixApplicationIsPresent();
    //
    //    efix getEFixById();
    //    efixDriver getEFixDriverById();

    protected abstract String getEFixId();
    protected abstract String getEFixJarName();

    protected boolean efixIsPresent()
    {
        return efixIsPresent( getEFixId() );
    }

    protected boolean efixApplicationIsPresent()
    {
        return efixApplicationIsPresent( getEFixId() );
    }

    protected efix getEFixById()
    {
        return getEFixById( getEFixId() );
    }

    protected efixDriver getEFixDriverById()
    {
        return getEFixDriverById( getEFixId() );
    }

    // Action access ...
    //
    //    abstract enumAction getUpdateAction();
    //    abstract enumAction getComponentUpdateAction();

    // Answer the action for the active component.
    // Whereas efixes may be installed, and selectively installed,
    // components may only be installed.

    // Answer the action for the current efix.

    protected abstract enumUpdateAction getUpdateAction();
    protected abstract enumUpdateAction getComponentUpdateAction();

    // Common event methods ...
    //
    //    updateEvent createEFixEvent();
    //    void completeEvent(updateEvent, boolean);
    //    abstract String getCancelledMessage();
    //    abstract String getSucceededMessage();
    //    void failEvent(updateEvent);
    //    boolean saveEvents(boolean);

    // Create and return an update event for the active efix.
    // Make the efix, the time, and the action, and set the log name
    // for this event.

    protected updateEvent createEFixEvent()
    {
        updateEvent event = getWPHistory().getHistory().addUpdateEvent();

        event.setEventType(enumEventType.EFIX_EVENT_TYPE);

        event.setId(getEFixId());
        event.setUpdateAction(getUpdateAction());
        event.setUpdateType(enumUpdateType.COMPOSITE_UPDATE_TYPE);

        event.setStartTimeStamp();
        event.setStandardLogName(getWPProduct().getLogDirName());

        return event;
    }

    protected static final boolean WAS_CANCELLED = true ;
    protected static final boolean WAS_NOT_CANCELLED = false ;

    protected void completeEvent(updateEvent event, boolean wasCancelled)
    {
        event.setEndTimeStamp(getTimeStamp());

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

    protected abstract String getCancelledMessage();
    protected abstract String getSucceededMessage();

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

    public final static boolean CREATE_BACKUP = true ;
    public final static boolean OMIT_BACKUP = false ;

    protected boolean saveEvents(boolean createBackup)
    {
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
    //    boolean openLog(updateEvent);
    //
    //    void writeStartingLog(updateEvent);
    //    void writeEndingLog(updateEvent);

    protected boolean openLog(updateEvent event)
    {
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
        log("IBM WebSphere Portal Update Activity Log");
        logDashes();
        log("Installer Version : ", pgmVersion);
        log("Installer Date    : ", pgmUpdate);
        logDashes();
        log("Log Name          : ", event.getLogName());
        logDashes();
        log("Product Directory : ", getWPProduct().getVersionDirName());
        log("History Directory : ", getWPHistory().getHistoryDirName());
        logDashes();
        log("TMP Directory     : ", getWPProduct().getTmpDirName());
        logDashes();
        log("");
        log("EFix Activity:");
        logDashes();

        String useJarName = getEFixJarName();
        if ( useJarName != null )
            log("EFix Jar          : ", getEFixJarName());

        log("EFix ID           : ", getEFixId());
        log("EFix Action       : ", event.getUpdateAction());
        log("Time Stamp (Start): ", event.getStartTimeStamp());
        logDashes();
        logFlush("");
    }

    protected void writeEndingLog(updateEvent event)
    {
        log("Results:");
        logDashes();
        log("Time Stamp (End)  : ", event.getEndTimeStamp());
        log("EFix Result: ", event.getResult());
        log("EFix Result Message:");
        logDashes();
        log(event.getResultMessage());
        logDashes();
        logFlush("");
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
        childEvent.setParentId( getEFixId() );
        childEvent.setId( childUpdate.getComponentName() );

        childEvent.setUpdateAction(getComponentUpdateAction());

        childEvent.setIsCustom( childUpdate.getIsCustom() );
        childEvent.setPrimaryContent( childUpdate.getPrimaryContent() );

        childEvent.setUpdateType(enumUpdateType.PATCH_UPDATE_TYPE);

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

    public static final String SKIP_COMPONENTS_PROPERTY_NAME = "efix.skip.components" ;
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

    // Logging ...
    //
    //     void writeStartingComponentLog(updateEvent);
    //     void writeEndingComponentLog(updateEvent);

    protected void writeStartingComponentLog(updateEvent childEvent)
    {
        log("");
        log("EFix Component Activity:");
        logDashes();
        log("Component Name    : ", childEvent.getId());
        log("Action            : ", childEvent.getUpdateActionAsEnum().toString());
        log("Time Stamp (Start): ", childEvent.getStartTimeStamp());
        logDashes();
        log("Log File Name     : ", childEvent.getLogName());
        log("Backup File Name  : ", childEvent.getBackupName());
        logDashes();
        logFlush("");
    }

    protected void writeEndingComponentLog(updateEvent event)
    {
        log("Results:");
        logDashes();
        log("Time Stamp (End)  : ", event.getEndTimeStamp());
        log("EFix Component Result: ", event.getResult());
        log("EFix Component Result Message:");
        logDashes();
        log(event.getResultMessage());
        logDashes();
        log("");
        log("EFix Component Installation ... Done");
        logFlush("");
    }

    protected boolean initializeLogAndBackup()
    {
        WPProduct useProduct = getWPProduct();

        log("Initializing log directory: ", useProduct.getLogDirName());
        log("Initializing backup directory: ", useProduct.getBackupDirName());

        if ( !useProduct.initializeLogAndBackup() ) {
            Iterator productExceptions = useProduct.getExceptions();

            while ( productExceptions.hasNext() ) {
                WASProductException nextException = (WASProductException)
                    productExceptions.next();

                addException("WUPD0236E", new String[] { getEFixId() }, nextException);
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
}
