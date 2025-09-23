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
 * EFix Installer
 *
 * History 1.5, 2/24/03
 *
 * 09-Jul-2002 Initial Version
 *
 * 01-Nov-2002 Split component install to only
 *             store the component applied when
 *             the primary content is successfully
 *             extracted.
 *
 * 03-Nov-2002 Added in quiet mode setting to extractor.
 *
 * 19-Nov-2002 Don't place an applied when a component
 *             update fails or is cancelled.
 *
 * 16-Dec-2002 Updated to mesh with PTF installation.
 *
 * 16-Jan-2003 Updated to allow ADD component updates.
 *             Defect 156574.
 */

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
public class EFixInstaller
    extends EFixBaseInstaller
{
    // Program versioning ...

    public static final String pgmVersion = "1.5" ;
    // Program versioning ...

    public static final String pgmUpdate = "2/24/03" ;

    // Instantor ...
    //
    // public EFixInstaller(WPProduct, WPHistory, Notifier, IOService);

    // Create an efix installer.  The product and history objects provide
    // the context for installation activity.  The notifier is used to
    // provide live notification of installation activity.  The IO
    // services and update refector are needed for component installation.

    public EFixInstaller(WPProduct wpsProduct, WPHistory wpsHistory,
                         Notifier notifier, IOService ioService)
    {
        super(wpsProduct, wpsHistory, notifier, ioService);

        setImage(null);
    }

    // Active image handling ...
    //
    //    void setImage(efixImage);
    //    efixImage getImage();
    //    String getUpdateId();
    //    String getUpdateJarName();
    //    boolean retrieveEFix();
    //    efix getEFix();

    // Set an active efix image.  This means that the
    // action is invalid.  If necessary, close the efix log.

    // Set during an installation, cleared after installation.
    // Means that an efix installer can only install one efix
    // at a time.

    protected EFixImage image;
    protected efixDriver activeEFixDriver;

    /**
	 * @param image  the image to set
	 * @uml.property  name="image"
	 */
    protected void setImage(EFixImage image)
    {
        this.image = image;
    }

    /**
	 * @return  the image
	 * @uml.property  name="image"
	 */
    protected EFixImage getImage()
    {
        return image;
    }

    // Answer the ID of the active efix image.

    protected String getUpdateId()
    {
        return getImage().getEFixId();
    }

    // Answer the jar of the active efix image.

    protected String getUpdateJarName()
    {
        return getImage().getJarName();
    }

    protected boolean retrieveEFixDriver()
    {
        logFlush("Retrieving efix information ...");

        activeEFixDriver = getImage().getEFixDriver();

        log("Retrieving efix information ... done");
        logFlush("");

        return ( activeEFixDriver != null );
    }

    protected efixDriver getEFixDriver()
    {
        return activeEFixDriver;
    }


    protected boolean checkPUILevel() {

       if ( activeEFixDriver != null ) {
          int count = activeEFixDriver.getCustomPropertyCount();
          for (int i=0; i<count; i++) {
             customProperty thisProp = activeEFixDriver.getCustomProperty( i );
             if (thisProp.getPropertyName().equals("requiredUpdateInstallerLevel")) {
                return checkPUILevel( thisProp, "WUPD0253E" );
             }
          }
          return true;  //If no custom prop thats OK.
       } else {
          return false;  // No Driver, this cannot be OK.
       }
    }



    // Component access ...
    //
    // void setComponentNames();
    // Vector getComponentNames();
    // boolean setActiveComponents();
    // boolean testComponent(String);
    // Vector getActiveComponentNames();
    // componentImage getActiveComponentImage(String);
    // int setComponentUpdates();
    // componentUpdate getComponentUpdate(String);
    // boolean prepareComponents();
    // boolean testComponent(String);

    protected Vector componentNames;
    protected Vector activeComponentNames;

    protected Hashtable activeComponentImages;

    protected Hashtable componentUpdates;

    protected void setComponentNames()
    {
        componentNames = getImage().getComponentNames();
    }

    /**
	 * @return  the componentNames
	 * @uml.property  name="componentNames"
	 */
    protected Vector getComponentNames()
    {
        return componentNames;
    }

    protected boolean setActiveComponents()
    {
        boolean failed = false;

        activeComponentNames = new Vector();
        activeComponentImages = new Hashtable();

        int componentCount = componentNames.size();

        EFixImage useImage = getImage();

        for ( int componentNo = 0;
              !failed && (componentNo < componentCount);
              componentNo++ ) {

            String nextComponentName = (String)
                componentNames.elementAt(componentNo);

            if ( testComponent(nextComponentName) ) {

                ComponentImage nextComponentImage =
                    useImage.getComponentImage(nextComponentName);

                if ( nextComponentImage == null ) {
                    addException("WUPD0212E", new String[] { nextComponentName }, null);
                    failed = true;

                } else {
                    activeComponentNames.addElement(nextComponentName);
                    activeComponentImages.put(nextComponentName, nextComponentImage);
                }
            }
        }

        return failed;
    }

    protected boolean testComponent(String componentName)
    {
        String logPrefix = "Testing component [ " + componentName + " ] ...";

        logFlush(logPrefix);

        boolean result = true;

        if ( isSelective() ) {
            if ( isSelected(componentName) ) {
                log(logPrefix + " explicitly selected");
            } else {
                log(logPrefix + " explicitly omitted");
                result = false;
            }
        }

        if ( result ) {
            if ( componentWasUpdatedByEFix(getUpdateId(), componentName) ) {
                log(logPrefix + " already updated");
                result = false;
            } else {
                log(logPrefix + " not yet updated");
            }
        }

        if ( result ) {
            boolean isAdd = updateIsAdd(componentName);
            boolean isPresent = componentIsPresent(componentName);

            if ( isAdd ) {
                if ( !isPresent ) {
                    log(logPrefix + " add and currently absent; allowing.");
                } else {
                    log(logPrefix + " add and already present; omitting.");
                    result = false;
                }
            } else {
                if ( isPresent ) {
                    log(logPrefix + " update and currently present; allowing.");
                } else {
                    log(logPrefix + " update and currently absent; omitting.");
                    result = false;
                }
            }
        }

        logFlush("");

        return result;
    }

    /**
	 * @return  the activeComponentNames
	 * @uml.property  name="activeComponentNames"
	 */
    protected Vector getActiveComponentNames()
    {
        return activeComponentNames;
    }

    protected ComponentImage getActiveComponentImage(String componentName)
    {
        return (ComponentImage) activeComponentImages.get(componentName);
    }

    protected int setComponentUpdates()
    {
        componentUpdates = new Hashtable();

        efixDriver useEFixDriver = getEFixDriver();
        int numUpdates = useEFixDriver.getComponentUpdateCount();

        for ( int compNo = 0; compNo < numUpdates; compNo++ ) {
            componentUpdate nextUpdate = useEFixDriver.getComponentUpdate(compNo);
            String nextComponentName = nextUpdate.getComponentName();

            componentUpdates.put(nextComponentName, nextUpdate);
        }

        return numUpdates;
    }

    protected componentUpdate getComponentUpdate(String componentName)
    {
        return (componentUpdate) componentUpdates.get(componentName);
    }

    protected boolean updateIsAdd(String componentName)
    {
        componentUpdate selectedUpdate = getComponentUpdate(componentName);

        if ( selectedUpdate == null )
            return false;

        return ( selectedUpdate.getUpdateTypeAsEnum() ==
                 enumUpdateType.ADD_UPDATE_TYPE );
    }

    protected boolean prepareComponents()
    {
        // TBD: Should match up the efix components with the image
        // TBD: components.
        //
        // TBD: For active components which are external, should
        // TBD: make sure that the external information is available.
        // TBD: Need to record the external root, to be placed in
        // TBD: the applied file.

        log("Preparing component information ...");
        logFlush("");

        setComponentNames();

        int numUpdates = setComponentUpdates();
        // 'setComponentUpdates' must be invoked before setting the
        // active components, as the updates are queried to test
        // components.

        boolean failed = setActiveComponents();

        if ( !failed ) {
            log("Component Statistics:");
            logDashes();
            log("Update Directories : ", Integer.toString(getComponentNames().size()));
            log("Listed Updates     : ", Integer.toString(numUpdates));
            log("Active Updates     : ", Integer.toString(getActiveComponentNames().size()));
            logDashes();
            log("");
            log("Preparing component information ... done");
        } else {
            log("Preparing component information ... failed");
        }

        logFlush("");

        return !failed;
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
            return enumUpdateAction.SELECTIVE_INSTALL_UPDATE_ACTION;
        else
            return enumUpdateAction.INSTALL_UPDATE_ACTION;
    }

    protected enumUpdateAction getComponentUpdateAction()
    {
        return enumUpdateAction.INSTALL_UPDATE_ACTION;
    }

    // Installation ...
    //
    //    public updateEvent install(EFixImage);
    //    public updateEvent install(EFixImage, Vector, boolean);
    //    updateEvent baseInstall();

    // Standard installation: No selected components, and

    public updateEvent install(EFixImage image)
    {
        return install(image, null, DO_NOT_IGNORE_ERRORS);
    }

    // Perform installation of the specified image.
    //
    // The status of the installation must be checked by
    // testing the returned update event.

    public updateEvent install(EFixImage image,
                               Vector selectedComponents,
                               boolean ignoreErrors)
    {
        updateEvent event;

        setImage(image);
        setSelectedComponents(selectedComponents);
        setIgnoreErrors(ignoreErrors);

        try {
           event = baseInstall();
        } finally {
            setSelectedComponents(null);
            setIgnoreErrors(false);
            setImage(null);
        }

        return event;
    }

    protected updateEvent baseInstall()
    {
        beginTaskGroup( getString("prepare.update.install") );
        logFlush("");

        updateEvent event = createUpdateEvent();

        if ( !openLog(event) ) {
            completeEvent(event, WAS_NOT_CANCELLED);
            return event;
        }

        writeStartingLog(event);


        boolean wasCancelled = false;
        if ( retrieveEFixDriver() ) {
           if (!checkPUILevel()) {
              failEvent( event );
              return event;
           }
           if ( prepareComponents() && saveEvents(CREATE_BACKUP) ) {
               wasCancelled = installComponents(event);
           } else {
               wasCancelled = false;
           }

        }



        nextTaskInGroup( getString("complete.update.install") );
        logFlush("");

        completeEvent(event, wasCancelled);

        if ( !saveEvents(OMIT_BACKUP) )
            failEvent(event);

		//Display config tasks if install completed
		if ( !wasCancelled && !event.failed() ) {
		   efixDriver efixDriver = getEFixDriver();
		   int configCount = efixDriver.getConfigTaskCount();

		   String configHeader = getString( "install.configtask.header.banner" );
		   String configRequired = getString( "install.configtask.required" );
		   String configOptional = getString( "install.configtask.optional" );
		   boolean firstConfigFound = false;

		   if ( configCount != 0 ) {
			  for ( int i=0; i<configCount; i++) {
				 configTask thisTask = (configTask)efixDriver.getConfigTask( i );
				 String taskName = thisTask.getConfigurationTaskName();
				 if ( taskName != null && !taskName.equals("") ) {
					if ( !firstConfigFound ) {
						log( configHeader );
						pushBanner("\n\n" + configHeader + "\n");
						firstConfigFound = true;
					}
					log("      " + taskName + "     " + 
								 ( thisTask.isConfigurationRequiredAsBoolean() ? configRequired : configOptional ) );
					pushBanner("      " + taskName + "     " + 
								 ( thisTask.isConfigurationRequiredAsBoolean() ? configRequired : configOptional + "\n" ) );
				 }
			  }
			  if ( firstConfigFound ) {
				 pushBanner("\n");
			  }
		   }
		}
		logFlush("");

        writeEndingLog(event);

        completeTaskGroup();

        closeLog();

        return event;
    }

    protected String getCancelledMessage()
    {
        return getString("result.cancelled.install");
    }

    protected String getSucceededMessage()
    {
        return getString("result.succeeded.install");
    }

    // Component installation ...
    //
    //    boolean installComponents(updateEvent);
    //    updateEvent installComponent(updateEvent, componentUpdate);
    //
    //    boolean placeApplied(updateEvent, componentUpdate);
    //    componentApplied createApplied(updateEvent, componentUpdate);
    //
    //    boolean runComponentInstall(updateEvent, componentUpdate);

    protected boolean installComponents(updateEvent parentEvent)
    {
        log("Installing Components ...");
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

            nextTaskInGroup( getString("component.update.install",
                                       new Object[] { new Integer(componentNo + 1),
                                                      new Integer(componentCount),
                                                      nextComponentName }) );

            componentUpdate childUpdate = getComponentUpdate(nextComponentName);

            updateEvent childEvent = installComponent(parentEvent, childUpdate);

            enumEventResult childResult = childEvent.getResultAsEnum();

            if ( childResult == enumEventResult.FAILED_EVENT_RESULT ) {
                // Use the presence of the exception to fail the installation.
                addException("WUPD0215E", new String[] { nextComponentName }, null);
                failed = true;
            } else if ( childResult == enumEventResult.CANCELLED_EVENT_RESULT ) {
                wasCancelled = true;
            }
        }

        if ( failed )
            log("Installing components ... failed");
        else if ( wasCancelled )
            log("Installing components ... cancelled");
        else {
           log("Installing components ... ok");
        }

        logFlush("");

        return wasCancelled;
    }

    protected updateEvent installComponent(updateEvent parentEvent,
                                           componentUpdate childUpdate)
    {
        updateEvent childEvent = createComponentEvent(parentEvent, childUpdate);

        writeStartingComponentLog(childEvent);

        boolean wasCancelled = wasCancelled();

        if ( !wasCancelled ) {
            if ( saveEvents(OMIT_BACKUP) ) {

                List extractedFiles = prepareComponentInstall(childEvent, childUpdate);
                if ( extractedFiles != null )
                    wasCancelled = runComponentInstall(childEvent, childUpdate, extractedFiles);
            }
        }

        completeEvent(childEvent, wasCancelled);

        if ( !saveEvents(OMIT_BACKUP) )
            failEvent(childEvent);

        // Don't place the applied unless the installation was successful.

        if ( childEvent.succeeded() )
            placeApplied(childEvent, childUpdate);

        writeEndingComponentLog(childEvent);

        return childEvent;
    }

    // Put in an efix applied for the active component and efix;
    // Store the update-id, component-name, and starting time-stamp
    // in the applied.  Store the log and backup name for the
    // component update as well.

    protected boolean placeApplied(updateEvent childEvent, componentUpdate childUpdate)
    {
        logFlush("Recording efix application ...");

        // Add the driver only when an application is to be added.
        //
        // This is a difficult constraint to handle;
        //
        // Want to avoid having any efix files if the driver cannot
        // be added.
        //
        // But, want to have a driver file if any applications are
        // present.

        if ( !updateIsPresent() ) {
            logFlush("EFix is not yet present; adding.");

            if ( !placeEFixDriver() )
                return false;
        }

        componentApplied newApplied = createApplied(childEvent, childUpdate);

        WPHistory useHistory = getWPHistory();
        boolean result = useHistory.recordEFixApplication(getUpdateId(), newApplied);

        if ( !result ) {
            Iterator historyExceptions = useHistory.getExceptions();

            while ( historyExceptions.hasNext() ) {
                WASHistoryException nextException = (WASHistoryException)
                    historyExceptions.next();

                addException("WUPD0216E",
                             new String[] { getUpdateId(), childUpdate.getComponentName() },
                             nextException);
            }

            log("Recording efix application ... failed");

        } else {
            log("Recording efix application ... done");
        }

        logFlush("");

        return result;
    }

    protected componentApplied createApplied(updateEvent childEvent,
                    componentUpdate childUpdate)
    {
        componentApplied newApplied = new componentApplied();

        newApplied.setComponentName( childUpdate.getComponentName() );
        newApplied.setUpdateType( childUpdate.getUpdateType() );
        newApplied.setIsRequired( childUpdate.getIsRequired() );
        newApplied.setIsOptional( childUpdate.getIsOptional() );

        newApplied.setIsExternal( childUpdate.getIsExternal() );
        newApplied.setRootPropertyFile( childUpdate.getRootPropertyFile() );
        newApplied.setRootPropertyName(childUpdate.getRootPropertyName() );
        newApplied.setRootPropertyValue(childUpdate.getRootPropertyValue() );

        // TBD: The root property value should have been prepared earlier,
        // TBD: and the actual value placed in the applied.

        newApplied.setIsCustom( childUpdate.getIsCustom() );

        newApplied.setTimeStamp( childEvent.getStartTimeStamp() );
        newApplied.setLogName( childEvent.getLogName() );
        newApplied.setBackupName( childEvent.getBackupName() );

        return newApplied;
    }

    // Perform installation of the component.

    protected List prepareComponentInstall(updateEvent childEvent, componentUpdate childUpdate)
    {
        String componentName = childUpdate.getComponentName();

        logFlush("Preparing update component ( " + componentName + " ).");

        if ( !initializeLogAndBackup() )
            return null;

        return extractFiles(componentName);
    }

    protected List extractFiles(String componentName)
    {
        EFixImage useImage = getImage();
        String useImageJarName = useImage.getJarName();

        log("Extracting from jar ( " + useImageJarName + " )");

        ComponentImage childImage = useImage.getComponentImage(componentName);

        List entryNames = childImage.getPrimaryContentEntryName();
        int numEntryNames = entryNames.size();

        for ( int nameNo = 0; nameNo < numEntryNames; nameNo++ )
            log("  Entry ( " + (String) entryNames.get(nameNo) + " )");

        String useTmpDirName = getWPProduct().getTmpDirName();
        logFlush("Extracting into target directory ( " + useTmpDirName + " )");

        List extractedFiles = new ArrayList();
        Exception boundException = null;

        for ( int nameNo = 0;
              (boundException == null) && (nameNo < numEntryNames);
              nameNo++ ) {

            String nextEntryName = (String) entryNames.get(nameNo);
            log("  Extracting ( " + nextEntryName + " )");

            try {
                String nextTmpFileName = ioService.extractJarEntry(useImageJarName,
                                                                   nextEntryName,
                                                                   useTmpDirName);
                // 'extractJarEntry' throws IOServicesException, IOException

                extractedFiles.add(nextTmpFileName);
                log("  Extracted ( " + nextTmpFileName + " )");

            } catch ( Exception e ) {
                boundException = e;
                extractedFiles = null;
            }
        }

        if ( extractedFiles == null ) {
            logFlush("  Extration failed!");

            String allNames = "";

            for( int nameNo = 0; nameNo < numEntryNames; nameNo++ ) {
                if ( nameNo != 0 )
                    allNames += ", ";

                allNames += (String) entryNames.get(nameNo);
            }

            String[] exceptionArgs = new String[] {
                getUpdateId(),
                componentName,
                useImageJarName,
                allNames
            };

            addException("WUPD0232E", exceptionArgs, boundException);
        }

        return extractedFiles;
    }

    protected boolean runComponentInstall(updateEvent childEvent, componentUpdate childUpdate,
                                          List extractedFiles)
    {
        String componentName = childUpdate.getComponentName();

        log("Running component update ( " + componentName + " ).");

        Extractor extractor = new Extractor();
        extractor.setQuiet(true);
        extractor.setComponentName(componentName);
        extractor.setLOAPAR(getUpdateId());

        try {
            log("Processing extractor command line arguments.");

	    String useLogLevel = Integer.toString( getLogLevel() );

	    log("Jar File  : ", (String) extractedFiles.get(0));
	    log("Log File  : ", childEvent.getLogName());
	    log("Target Dir: ", getWPProduct().getProductDirName());
	    log("Backup Jar: ", childEvent.getBackupName());
	    log("Tmp Dir   : ", getWPProduct().getTmpDirName());
	    logFlush("Log Level : ", useLogLevel);

            String args[] = {
                "-JarInputFile", (String) extractedFiles.get(0) ,
                "-LogFile",      childEvent.getLogName(),
                "-TargetDir",    getWPProduct().getProductDirName(),
                "-BackupJar",    childEvent.getBackupName(),
                "-TmpDir",       getWPProduct().getTmpDirName(),
                "-SkipVer",                   // skip obsolete PTF processing
                "-Overwrite",                 // allow backup to be overwritten
                "-Verbosity",    useLogLevel  // 0 .. 5  (least .. most)
            };

          if ( !extractor.processCmdLineArgs(args) ) {
              log("Failed while processing extractor command line arguments.");
              logFlush("");

              addException("WUPD0233E",
                           new String[] { getUpdateId(), componentName },
                           null);
          } else {
              log("Completed processing extractor command line arguments.");
              log("Performing extraction.");
              logFlush("");

              if ( !extractor.process() ) { // throws Exception ?
                  log("Failed to perform extraction.");
                  logFlush("");

                  addException("WUPD0235E",
                               new String[] { getUpdateId(), componentName, childEvent.getLogName() },
                               null);
              } else {
                  log("Performed extraction.");
                  logFlush("");
              }
          }

        } catch (Exception ex) {
            log("Exception while performing update.");
            logFlush("");

            addException("WUPD0234E",
                         new String[] { getUpdateId(), componentName },
                         ex);

            ex.printStackTrace(System.out);
        }

        // TBD: Need to remove the temporary update file!
        // TBD: This won't simply work if we attempt to simply
        // TBD: delete file, as that doesn't work.  The file is
        // TBD: being left open by the extractor, or by java,
        // TBD: and cannot be deleted.

        return false;
    }

    // Driver manipulation:
    //
    //     boolean placeEFixDriver();

    protected boolean placeEFixDriver()
    {
        logFlush("Placing efix and efix driver file ...");

        if ( updateIsPresent() ) {
            log("Placing efix and efix driver file ... skipped: already present");
            logFlush("");

            return true;
        }

        WPProduct useProduct = getWPProduct();
        WPHistory useHistory = getWPHistory();

        efixDriver useEFixDriver = getEFixDriver();
        efix useEFix = useEFixDriver.cloneAsEFix();

        boolean didSave = useProduct.saveEFix(useEFix);

        if ( didSave )
            didSave = useHistory.saveEFixDriver(useEFixDriver);

        if ( !didSave ) {
            Iterator productExceptions = useProduct.getExceptions();

            while ( productExceptions.hasNext() ) {
                WASProductException nextException = (WASProductException)
                    productExceptions.next();

                addException("WUPD0214E",
                             new String[] { getUpdateId(), getUpdateJarName() },
                             nextException);
            }

            Iterator historyExceptions = useHistory.getExceptions();

            while ( historyExceptions.hasNext() ) {
                WASHistoryException nextException = (WASHistoryException)
                    historyExceptions.next();

                addException("WUPD0214E",
                             new String[] { getUpdateId(), getUpdateJarName() },
                             nextException);
            }

            log("Placing efix and efix driver file ... failed");

        } else {
            log("Placing efix and efix driver file ... done");
        }

        logFlush("");

        return didSave;
    }
}
