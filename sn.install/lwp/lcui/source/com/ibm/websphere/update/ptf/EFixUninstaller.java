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
 * EFix Uninstaller
 *
 * History "1.3", "2/24/03"
 *
 * 09-Jul-2002 Initial Version
 *
 * 03-Nov-2002 Added in quiet mode setting to extractor.
 *
 * 16-Dec-2002 Updated to mesh with PTF installation;
 *             Added support for alternate banners.
 */

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class EFixUninstaller
    extends EFixBaseInstaller
{
    // Program versioning ...

    public static final String pgmVersion = "1.3" ;
    // Program versioning ...

    public static final String pgmUpdate = "2/24/03" ;

    // Instantor ...
    //
    // public EFixUninstaller(WASProduct, WASHistory, Notifier, IOService);
    //
    // Create an efix uninstaller.  The product and history objects provide
    // the context for installation activity.  The notifier is used to
    // provide live notification of installation activity.

    public EFixUninstaller(WPProduct wpsProduct, WPHistory wpsHistory,
                           Notifier notifier, IOService ioService)
    {
        super(wpsProduct, wpsHistory, notifier, ioService);
        //System.out.println("ptf.efixUninstaller.java");
        setUpdateId(null);
        enableAlternateBanners(false);
    }

    // Alternate banners ...
    // 
    // public boolean usingAlternateBanners();
    // public void enableAlternateBanners(boolean);
    //
    // String getPrepareEFixBanner();
    // String getUninstallingEFixBanner(String);
    // String getCompleteEFixBanner();

    protected boolean useAlternateBanners;

    public boolean usingAlternateBanners()
    {
        return useAlternateBanners;
    }

    public void enableAlternateBanners(boolean enablement)
    {
        useAlternateBanners = enablement;
    }

    protected String getPrepareEFixBanner()
    {
        return getString("prepare.efix.banner", getUpdateId());
    }

    protected String getUninstallingEFixBanner(String componentName)
    {
        return getString("uninstalling.efix.banner", getUpdateId(), componentName);
    }

    protected String getCompleteEFixBanner()
    {
        return getString("complete.efix.banner", getUpdateId());
    }

    // Active efix handling ...
    //
    //    void setUpdateId(String);
    //    String getUpdateId();
    //    String getUpdateJarName();
    //    efix getEFix();
    //    boolean readEFix();
    //    efixDriver getEFixDriver();
    //    boolean readEFixDriver();

    // Set an active efix image.  This means that the
    // action is invalid.  If necessary, close the efix log.

    // Set during an installation, cleared after installation.
    // Means that an efix installer can only install one efix
    // at a time.

    protected String efixId;

    protected efix activeEFix;
    protected efixDriver activeEFixDriver;

    protected void setUpdateId(String efixId)
    {
        this.efixId = efixId;
        LCUtil.setInstallImage(efixId);
        this.activeEFix = null;
        this.activeEFixDriver = null;
    }

    protected String getUpdateId()
    {
        return efixId;
    }

    protected String getUpdateJarName()
    {
        return null;
    }

    protected efix getEFix()
    {
        return activeEFix;
    }

    protected boolean readEFix()
    {
        logFlush("Reading efix file ...");

        activeEFix = (efix) getUpdateById();

        if ( activeEFix != null ) {
            log("Reading efix file ... done");
            logFlush("");

            return true;
        }

        String useEFixId = getUpdateId();

        Iterator productExceptions = getWPProduct().getExceptions();

        if ( !productExceptions.hasNext() ) {
            addException("WUPD0220E", new String[] { useEFixId }, null);

            log("Reading efix file ... efix is absent");
            logFlush("");

            return false;
        }

        while ( productExceptions.hasNext() ) {
            WASProductException nextException = (WASProductException)
                productExceptions.next();

            addException("WUPD0221E", new String[] { useEFixId }, nextException);
        }

        log("Reading efix file ... failed with exception");
        logFlush("");

        return false;
    }

    protected efixDriver getEFixDriver()
    {
        return activeEFixDriver;
    }

    protected boolean readEFixDriver()
    {
        logFlush("Reading efix driver file ...");

        activeEFixDriver = (efixDriver) getUpdateDriverById();

        if ( activeEFixDriver != null ) {
            log("Reading efix driver file ... done");
            logFlush("");

            return true;
        }

        String useEFixId = getUpdateId();

        Iterator historyExceptions = getWPHistory().getExceptions();

        if ( !historyExceptions.hasNext() ) {
            addException("WUPD0220E", new String[] { useEFixId }, null);

            log("Reading efix file ... efix is absent");
            logFlush("");

            return false;
        }

        while ( historyExceptions.hasNext() ) {
            WASHistoryException nextException = (WASHistoryException)
                historyExceptions.next();

            addException("WUPD0221E", new String[] { useEFixId }, nextException);
        }

        log("Reading efix file ... failed with exception");
        logFlush("");

        return false;
    }

    protected String configTaskMessages = null;

    protected boolean checkConfigTasks() {
       boolean usingAlternateBanners = usingAlternateBanners();

       boolean canUninstall = true;
       efixDriver efixDriver = getEFixDriver();
       int configCount = efixDriver.getConfigTaskCount();
       if (configCount > 0) {
          // See if any config are still active.
          efixApplied applied =  getWPHistory().getEFixAppliedById(efixId);
          int appliedCount = applied.getConfigAppliedCount();

          StringBuffer buffer = new StringBuffer();

          for ( int i=0; i<appliedCount; i++ ) {
             configApplied thisConfig = applied.getConfigApplied( i );
             if ( thisConfig.isConfiguredAsBoolean() ) {
                if ( thisConfig.isConfigurationActiveAsBoolean() ) {
                   canUninstall = false;
                   buffer.append( "\n" + getString( "uninstall.configtask.active.banner", thisConfig.getConfigName() ) );
                } else {
                   buffer.append( "\n" + getString( "uninstall.configtask.configured.banner", thisConfig.getConfigName() ) );
                }
             }
          }
          if (buffer.length() > 0) {
             configTaskMessages = buffer.toString();
          }
       }
       return canUninstall;
    }


    // Component access ...
    //
    // Vector getComponentNames();
    // Vector getActiveComponentNames();
    // componentUpdate getComponentUpdate(String);
    // boolean prepareComponents();
    // boolean testComponent(String);

    protected Vector componentNames;
    protected Vector activeComponentNames;
    protected Hashtable componentUpdates;

    /**
	 * @return  the componentNames
	 * @uml.property  name="componentNames"
	 */
    protected Vector getComponentNames()
    {
        return componentNames;
    }

    /**
	 * @return  the activeComponentNames
	 * @uml.property  name="activeComponentNames"
	 */
    protected Vector getActiveComponentNames()
    {
        return activeComponentNames;
    }

    protected componentUpdate getComponentUpdate(String componentName)
    {
        return (componentUpdate) componentUpdates.get(componentName);
    }

    protected boolean prepareComponents()
    {
        log("Preparing component information ...");
        logFlush("");

        componentNames = getEFixDriver().getComponentNames();

        activeComponentNames = new Vector();
        componentUpdates = new Hashtable();

        int componentCount = componentNames.size();

        for ( int componentNo = 0; componentNo < componentCount; componentNo++ ) {
            String nextComponentName = (String)
                componentNames.elementAt(componentNo);

            if ( testComponent(nextComponentName) )
                activeComponentNames.addElement(nextComponentName);
        }

        efixDriver useEFixDriver = getEFixDriver();

        int numUpdates = useEFixDriver.getComponentUpdateCount();

        for ( int compNo = 0; compNo < numUpdates; compNo++ ) {
            componentUpdate nextUpdate = useEFixDriver.getComponentUpdate(compNo);
            String nextComponentName = nextUpdate.getComponentName();

            componentUpdates.put(nextComponentName, nextUpdate);
        }

        log("Component Statistics:");
        logDashes();
        log("Potential Updates: ", Integer.toString(componentCount));
        log("Actual Updates   : ", Integer.toString(activeComponentNames.size()));
        log("EFix Updates     : ", Integer.toString(componentUpdates.size()));
        logDashes();
        log("");
        log("Preparing component information ... done");
        logFlush("");

        return true;
    }

    protected boolean testComponent(String componentName)
    {
        String logPrefix = "Testing component [ " + componentName + " ] ...";

        logFlush(logPrefix);

        boolean result;

        if ( !componentIsPresent(componentName) ) {
            log(logPrefix + " absent!");
            result = false;

        } else {
            if ( isSelective() ) {
                if ( isSelected(componentName) ) {
                    log(logPrefix + " present and explicitly selected");
                    result = true;
                } else {
                    log(logPrefix + " present and explicitly omitted");
                    result = false;
                }

            } else {
                if ( componentWasUpdatedByEFix(getUpdateId(), componentName) ) {
                    log(logPrefix + " present and already updated");
                    result = true;
                } else {
                    log(logPrefix + " present and not yet updated");
                    result = false;
                }
            }
        }

        logFlush("");

        return result;
    }

    // Action access ...
    //
    //    enumAction getUpdateAction();
    //    enumAction getComponentUpdateAction();

    // Answer the action for the active component.
    // Whereas efixes may be installed, and selectively installed,
    // components may only be installed.

    // Answer the action for the current efix.

    protected enumUpdateAction getUpdateAction()
    {
        if ( isSelective() )
            return enumUpdateAction.SELECTIVE_UNINSTALL_UPDATE_ACTION;
        else
            return enumUpdateAction.UNINSTALL_UPDATE_ACTION;
    }

    protected enumUpdateAction getComponentUpdateAction()
    {
        return enumUpdateAction.UNINSTALL_UPDATE_ACTION;
    }

    // Installation ...
    //
    //    public updateEvent uninstall(String);
    //    public updateEvent uninstall(String, Vector, boolean);
    //    updateEvent baseUninstall();
    //
    //    String getCancelledMessage();
    //    String getSucceededMessage();
    //
    //    efix removeEFix(efix);

    public updateEvent uninstall(String efixId)
    {
        return uninstall(efixId, null, DO_IGNORE_ERRORS);
    }

    public updateEvent uninstall(String efixId,
                                 Vector selectedComponents,
                                 boolean ignoreErrors)
    {
        updateEvent event;

        setUpdateId(efixId);
        setSelectedComponents(selectedComponents);
        setIgnoreErrors(ignoreErrors);

        try {
            event = baseUninstall();

        } finally {
            setSelectedComponents(null);
            setIgnoreErrors(false);
            setUpdateId(null);
        }

        return event;
    }

    protected updateEvent baseUninstall()
    {
        boolean usingAlternateBanners = usingAlternateBanners();

        if ( usingAlternateBanners )
            nextTaskInGroup( getPrepareEFixBanner() );
        else
            beginTaskGroup( getString("prepare.update.uninstall") );

        logFlush("");

        updateEvent event = createUpdateEvent();

        if ( !openLog(event) ) {
            completeEvent(event, WAS_NOT_CANCELLED);
            return event;
        }

        writeStartingLog(event);

        boolean wasCancelled;

        if ( readEFix() && readEFixDriver() && checkConfigTasks() && prepareComponents() && saveEvents(CREATE_BACKUP) ) {

            wasCancelled = uninstallComponents(event);

            // Maybe a partial uninstall: Don't remove the efix file
            // if there are parts of the efix still installed.

            if ( !wasCancelled &&
                 !(getExceptions().hasNext()) &&
                 !updateApplicationIsPresent() )
                removeEFix();

        } else {
            wasCancelled = false;
        }

        if ( usingAlternateBanners )
            nextTaskInGroup( getCompleteEFixBanner() );
        else
            nextTaskInGroup( getString("complete.update.uninstall") );


        if (configTaskMessages != null) {
           log( configTaskMessages );
           pushBanner( configTaskMessages  );
        }

		logFlush("");

        completeEvent(event, wasCancelled);

        if ( !saveEvents(OMIT_BACKUP) )
            failEvent(event);

		//Display config tasks if uninstall completed
		if ( !wasCancelled  && !event.failed() ) {
		   efixDriver efixDriver = getEFixDriver();
		   int configCount = efixDriver.getConfigTaskCount();

		   String configHeader = getString( "install.configtask.header.banner" );
		   String configRequired = getString( "install.configtask.required" );
		   String configOptional = getString( "install.configtask.optional" );
		   boolean firstUnconfigFound = false;

		   if ( configCount != 0 ) {
			  for ( int i=0; i<configCount; i++) {
				 configTask thisTask = (configTask)efixDriver.getConfigTask( i );
				 String taskName = thisTask.getUnconfigurationTaskName();
				 if ( taskName != null && !taskName.equals("") ) {
					if ( !firstUnconfigFound ) {
						log( configHeader );
						pushBanner("\n\n" + configHeader + "\n");
						firstUnconfigFound = true;
					}
					log("      " + taskName + "     " + 
									( thisTask.isUnconfigurationRequiredAsBoolean() ? configRequired : configOptional ) );
					pushBanner("      " + taskName + "     " + 
									( thisTask.isUnconfigurationRequiredAsBoolean() ? configRequired : configOptional + "\n" ) );
				 }
				 
			  }
			  if ( firstUnconfigFound ) {
				 pushBanner("\n");
			  }
		   }
		}
		logFlush("");

        writeEndingLog(event);

        if ( !usingAlternateBanners )
            completeTaskGroup();

        closeLog();

        return event;
    }

    protected String getCancelledMessage()
    {
        return getString("result.cancelled.uninstall");
    }

    protected String getSucceededMessage()
    {
        return getString("result.succeeded.uninstall");
    }

    protected boolean removeEFix()
    {
        logFlush("Removing efix and efix driver file ...");

        WPProduct useProduct = getWPProduct();
        boolean removedEFix = useProduct.removeEFix( getEFix() );

        WASHistory useHistory = getWPHistory();
        boolean removedEFixDriver = useHistory.removeEFixDriver( getEFixDriver() );

        if ( removedEFix && removedEFixDriver ) {
            log("Removing efix and efix driver file ... done");
            logFlush("");

            return true;
        }

        Iterator productExceptions = useProduct.getExceptions();
        while ( productExceptions.hasNext() ) {
            WASProductException nextException = (WASProductException)
                productExceptions.next();

            addException("WUPD0222E",
                         new String[] { getUpdateId() },
                         nextException);
        }

        Iterator historyExceptions = useHistory.getExceptions();
        while ( historyExceptions.hasNext() ) {
            WASHistoryException nextException = (WASHistoryException)
                historyExceptions.next();

            addException("WUPD0222E",
                         new String[] { getUpdateId() },
                         nextException);
        }

        log("Removing efix and efix driver file ... failed with exception");
        logFlush("");

        return false;
    }

    // Component uninstall ...
    //
    //    boolean uninstallComponents(updateEvent);
    //    updateEvent uninstallComponent(updateEvent);
    //
    //    boolean removeEFixApplied(updateEvent);
    //
    //    boolean runComponentUninstall(updateEvent, String);

    protected boolean uninstallComponents(updateEvent parentEvent)
    {
        log("Uninstalling Components ...");
        logFlush("");

        boolean failed = false;
        boolean useIgnoreErrors = getIgnoreErrors();

        boolean wasCancelled = false;

        Vector useComponentNames = getActiveComponentNames();
        int componentCount = useComponentNames.size();

        for ( int componentNo = 0;
              (!failed || useIgnoreErrors) && !wasCancelled && (componentNo < componentCount);
              componentNo++ ) {

            String nextComponentName = (String)
                useComponentNames.elementAt(componentNo);

            if ( componentNo != 0 )
                log("");

            if ( usingAlternateBanners() ) {
                nextTaskInGroup( getUninstallingEFixBanner(nextComponentName) );
            } else {
                nextTaskInGroup( getString("component.update.uninstall",
                                           new Object[] { new Integer(componentNo + 1),
                                                          new Integer(componentCount),
                                                          nextComponentName }) );
            }

            componentUpdate nextUpdate =
                getComponentUpdate(nextComponentName);
            componentApplied nextApplied =
                getWPHistory().getEFixComponentAppliedById(getUpdateId(), nextComponentName);

            if ( nextApplied == null ) {
                log("Skipping component -- no update is active.");

            } else {
                updateEvent childEvent = uninstallComponent(parentEvent, nextUpdate, nextApplied);
                enumEventResult childResult = childEvent.getResultAsEnum();

                if ( childResult == enumEventResult.FAILED_EVENT_RESULT ) {
                    // Use the presence of the exception to fail the installation.
                    addException("WUPD0223E", new String[] { nextComponentName }, null);
                    failed = true;
                } else if ( childResult == enumEventResult.CANCELLED_EVENT_RESULT ) {
                    wasCancelled = true;
                }
            }
        }

        if ( failed )
            log("Uninstalling components ... failed");
        else if ( wasCancelled )
            log("Uninstalling components ... cancelled");
        else
            log("Uninstalling components ... ok");

        logFlush("");

        return wasCancelled;
    }

    protected updateEvent uninstallComponent(updateEvent parentEvent,
                                             componentUpdate childUpdate,
                                             componentApplied childApplied)
    {
        updateEvent childEvent = createComponentEvent(parentEvent, childUpdate);

        writeStartingComponentLog(childEvent);

        boolean wasCancelled = wasCancelled();

        if ( saveEvents(OMIT_BACKUP) ) {
            wasCancelled = runComponentUninstall(childEvent, childUpdate, childApplied);

            if ( !wasCancelled && !(getExceptions().hasNext()) )
                removeEFixApplied(childEvent, childUpdate);
        }

        completeEvent(childEvent, wasCancelled);

        if ( !saveEvents(OMIT_BACKUP) )
            failEvent(childEvent);

        writeEndingComponentLog(childEvent);

        return childEvent;
    }

    // Put in an efix applied for the active component and efix;
    // Store the update-id, component-name, and starting time-stamp
    // in the applied.  Store the log and backup name for the
    // component update as well.

    protected boolean removeEFixApplied(updateEvent childEvent, componentUpdate childUpdate)
    {
        String efixId = getUpdateId();
        String componentName = childUpdate.getComponentName();

        logFlush("Clearing efix application ..." + efixId + " : " + componentName);

        WPHistory useHistory = getWPHistory();

        componentApplied anApplied =
            useHistory.getEFixComponentAppliedById(efixId, componentName);

        boolean result;

        if ( anApplied == null ) {
            result = false;
            log("Clearing efix application ... already absent");
        } else {
            result = useHistory.clearEFixApplication(efixId, anApplied);

            if ( !result ) {
                Iterator historyExceptions = useHistory.getExceptions();

                if ( !historyExceptions.hasNext() )
                    log("Strange -- no history exception!");

                while ( historyExceptions.hasNext() ) {
                    WASHistoryException nextException = (WASHistoryException)
                        historyExceptions.next();

                    addException("WUPD0224E",
                                 new String[] { getUpdateId(), componentName },
                                 nextException);
                }

                log("Clearing efix application ... failed");

            } else {
                log("Clearing efix application ... done");
            }
        }

        logFlush("");

        return result;
    }

    // Perform installation of the component.

    protected boolean runComponentUninstall(updateEvent childEvent,
                                            componentUpdate childUpdate,
                                            componentApplied childApplied)
    {
        String useEFixId = getUpdateId();
        String componentName = childUpdate.getComponentName();

        if ( bypassComponentAction() ) {
            log("Bypassing component update ( " + componentName + " ) (for undo)");
            logFlush("");
            return false;
        } else {
            log("Performing component update ( " + componentName + " ) (for undo)");
        }

        if ( !initializeLogAndBackup() )
            return false;

        Extractor extractor = new Extractor();
        extractor.setQuiet(true);
        extractor.setComponentName(componentName);
        extractor.setLOAPAR(getUpdateId());
        try {
            log("Processing extractor command line arguments (for undo).");

            String useLogLevel = Integer.toString( getLogLevel() );

            log("Backup Jar: " + childApplied.getBackupName());
            log("Log File  : ", childEvent.getLogName());
            log("Tmp Dir   : ", getWPProduct().getTmpDirName());
            logFlush("Log Level : ", useLogLevel);

            String args[] = {
                "-JarInputFile", childApplied.getBackupName(),
                "-LogFile",      childEvent.getLogName(),
                // "-TargetDir",    getWASHistory().getProductDirName(),
                "-TmpDir",       getWPProduct().getTmpDirName(),
                "-SkipVer",                   // skip the obsolete PTF processing
                "-Verbosity",    useLogLevel  // 0 .. 5  (least .. most)
            };

          if ( !extractor.processCmdLineArgs(args) ) {
              log("Failed while processing extractor command line arguments (for undo).");
              logFlush("");

              addException("WUPD0237E",
                           new String[] { useEFixId, componentName },
                           null);
          } else {
              log("Completed processing extractor command line arguments (for undo).");
              log("Performing extraction.");
              logFlush("");

              if ( !extractor.process() ) { // throws Exception ?
                  log("Failed to perform extraction.");
                  logFlush("");

                  addException("WUPD0239E",
                               new String[] { useEFixId, componentName, childEvent.getLogName() },
                               null);
              } else {
                  log("Performed extraction.");
                  logFlush("");
              }
          }

        } catch (Exception ex) {
            log("Exception while performing update (undo).");
            logFlush("");

            addException("WUPD0238E",
                         new String[] { useEFixId, componentName },
                         ex);
        }

        // TBD: Need to remove the backup jar file.

        return false;
    }
}
