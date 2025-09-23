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
 * PTF Batch Updater
 *
 * History 1.18, 3/26/03
 *
 * 17-Dec-2002 Adapted from EFix batch updater.
 *
 * 11-Feb-2003 Added 'after-component' processing.
 *
 * 25-Mar-2003 Corrected infinte loop in 'reorderForInstall';
 *             defect 161873.
 */

/*
 * Updated Banners:
 *
 * For the common efix steps:
 *
 * Step 1 of 6: Preparing EFix "PQ"
 * Step 2 of 6: Uninstalling EFix "PQ", Component "activity"
 * Step 3 of 6: Completing EFix "PQ"
 *
 * While installing the PTF:
 *
 * Step 4 of 6: Preparing PTF "P1"
 * Step 5 of 6: Installing PTF "P1", Component "activity"
 * Step 6 of 6: Completing PTF "P1"
 *
 * While uninstalling the PTF:
 *
 * Step 4 of 6: Preparing PTF "P1"
 * Step 5 of 6: Uninstalling PTF "P1", Component "activity"
 * Step 6 of 6: Completing PTF "P1"
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.component.ComponentHandler;
import com.ibm.websphere.product.xml.component.ComponentWriter;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.EFixHandler;
import com.ibm.websphere.product.xml.efix.EFixWriter;
import com.ibm.websphere.product.xml.efix.efix;
import com.ibm.websphere.product.xml.efix.ptf;
import com.ibm.websphere.update.efix.prereq.*;
import com.ibm.websphere.update.ioservices.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class PTFBatchUpdater extends BaseInstaller {
    // Program versioning ...

    public static final String pgmVersion = "1.18" ;
    // Program versioning ...

    public static final String pgmUpdate = "3/26/03" ;

    // Instantor ...
    //
    // public PTFBatchUpdate(WASProduct, WASHistory, Notifier, IOService,
    //                       Vector[extendedComponent]);
    //
    // public Vector getExtendedComponents();

    public PTFBatchUpdater(WPProduct wpsProduct, WPHistory wpsHistory, Notifier notifier, IOService ioService, Vector extendedComponents) {
        super(wpsProduct, wpsHistory, notifier, ioService);

        this.extendedComponents = extendedComponents;
    }

    protected Vector extendedComponents;

    /**
	 * @return  the extendedComponents
	 * @uml.property  name="extendedComponents"
	 */
    public Vector getExtendedComponents() {
        return extendedComponents;
    }

    // Installation ...
    //
    // public PTFInstallData prepareImage(ptfImage);
    //
    // public Vector[<componentUpdate, component>]
    //     getOptionalUpdates(PTFInstallData);
    //
    // public Vector install(PTFInstallData);
    //
    // Vector[componentUpdate]
    //     selectComponentsToInstall(PTFInstallData);
    // public boolean shouldBeApplied(String, componentUpdate, HashMap);
    //
    // Installation Steps:
    // 
    //    A PTF image must be available.
    //
    //    Use 'prepareImage' to prepare the image.  This causes
    //    the image data to be loaded, which avoids errors later
    //    in the installation.
    //
    //    The result of 'prepareImage' is PTF install data, which
    //    includes the target PTF image and the list of efixes which
    //    are to be uninstalled.
    //
    //    Use 'getOptionalUpdates' to scan the image for optional
    //    component updates.  A collection of component updates
    //    selected from total collection must be placed into the
    //    PTF install data.
    //
    //    Use 'install' to install the PTF.  The result is a
    //    collection of update events.
    //
    //    Use 'shouldUndo' to tell if the update should be
    //    reverted.
    //
    //    Use 'undoInstallation' to revert the application.
    //    The result of reverting the application is an update
    //    event.  Use 'wasCancelled()' and 'failed()' on the result
    //    event to determine the reversion status.
    //
    //    Use 'preparePTF' to prepare a PTF to be uninstalled.
    //
    //    Use 'uninstall', in either of its two forms, to perform
    //    the uninstall.
    //
    //    'uninstall' returns a collection of update events.
    //    use 'didFail' on the collection to tell if the uninstall
    //    failed.

    public PTFInstallData prepareImage(PTFImage targetImage) {
        PTFInstallData seedData;

        try {
            targetImage.prepareDriver(); // throws IOException, FactoryException
            targetImage.prepareComponents(); // throws IOException

            //Vector useEFixes = getEFixes();

            seedData = new PTFInstallData(targetImage);
            //seedData.setEFixesToRemove(useEFixes);

        } catch (Exception e) {
            addException("WVER0230", new String[] { targetImage.getPTFId()}, e);

            seedData = null;
        }

        return seedData;
    }

    // Answer a vector of pairs: Vector[<componentUpdate, component>]
    //
    // Answer only those updates which are actually installable.
    // 
    // if ( !required && optional ) {
    //     if ( applied ) {
    //         skip
    //     } else { // !applied
    //         if ( present ) {
    //             ask
    //         } else { // !present
    //             if ( ADD )
    //                 ask
    //             else
    //                 skip
    //         }
    //     }
    // }

    public Vector getOptionalUpdates(PTFInstallData installData) {
        Vector optionalUpdates = new Vector();

        WASProduct useProduct = getWPProduct();

        PTFImage targetImage = installData.getTargetImage();
        ptfDriver targetDriver = (ptfDriver) targetImage.getDriver();

        int numUpdates = targetDriver.getComponentUpdateCount();

        for (int updateNo = 0; updateNo < numUpdates; updateNo++) {
            componentUpdate nextUpdate = targetDriver.getComponentUpdate(updateNo);
            String nextName = nextUpdate.getComponentName();

            if (!nextUpdate.getIsRequiredAsBoolean() && nextUpdate.getIsOptionalAsBoolean()) {
                boolean nextAsk;
                component nextComponent;

                if (componentWasUpdatedByPTF(targetImage.getPTFId(), nextName)) {
                    nextAsk = false;
                    nextComponent = null;
                } else {
                    nextComponent = useProduct.getComponentByName(nextName);

                    if (nextComponent != null) {
                        nextAsk = true;
                    } else {
                        if (nextUpdate.getUpdateTypeAsEnum() == enumUpdateType.ADD_UPDATE_TYPE)
                            nextAsk = true;
                        else
                            nextAsk = false;
                    }
                }

                if (nextAsk) {
                    Object[] updateInfo = new Object[] { nextUpdate, nextComponent };
                    optionalUpdates.addElement(updateInfo);
                }
            }

        }

        return optionalUpdates;
    }

    public Vector install(PTFInstallData installData) {
    	return install(installData, null);
    }
    
    public Vector install(PTFInstallData installData, Vector selectedCompInstallList) { //Saiyu
        Vector useEFixes = installData.getEFixesToRemove();
        int efixCount = useEFixes.size();

        boolean bSelectedUpdate = false;
        
        Vector useComponents = selectComponentsToInstall(installData);
        //Saiyu begins
        if ((selectedCompInstallList != null) && (selectedCompInstallList.size()>0)) {
        	bSelectedUpdate = true;
        	Iterator itr = useComponents.iterator();
        	while (itr.hasNext()) {
        		String item = (String)itr.next();
        		if (!selectedCompInstallList.contains(item)) {
        			itr.remove();
        		}
        	}
        }
        //Saiyu ends
        int componentCount = useComponents.size();

        setTaskCount((efixCount * 3) + (1 + componentCount + 1));
        pushBanner("");

        // try {
            Vector updateEvents = new Vector();

            EFixUninstaller useEFixUninstaller = createEFixUninstaller();

            boolean failed = false, cancelled = false;

            for (int efixNo = 0; !failed && !cancelled && (efixNo < efixCount); efixNo++) {

                efix nextEFix = (efix) useEFixes.elementAt(efixNo);
                String nextEFixId = nextEFix.getId();

                updateEvent nextEvent = useEFixUninstaller.uninstall(nextEFixId);
                updateEvents.addElement(nextEvent);

                failed = nextEvent.failed();
                cancelled = nextEvent.wasCancelled();
            }

            if (!failed && !cancelled) {
                PTFInstaller usePTFInstaller = createPTFInstaller();

                updateEvent ptfEvent = usePTFInstaller.install(installData.getTargetImage(), useComponents, bSelectedUpdate, false);

                updateEvents.addElement(ptfEvent);

                failed = ptfEvent.failed();
                cancelled = ptfEvent.wasCancelled();
            }

            return updateEvents;

        // } finally {
        //    popBanner();
        // }
    }

    protected Vector selectComponentsToInstall(PTFInstallData installData) {
        PTFImage targetImage = installData.getTargetImage();
        String targetId = targetImage.getPTFId();

        ptfDriver targetDriver = targetImage.getPTFDriver();

        HashMap optionalMap = new HashMap();

        Iterator selectedUpdates = installData.getOptionalUpdates().iterator();

        while (selectedUpdates.hasNext()) {
            Object[] selections = (Object[]) selectedUpdates.next();
            componentUpdate nextUpdate = (componentUpdate) selections[0];

            optionalMap.put(nextUpdate.getComponentName(), nextUpdate);
        }

        Vector targetUpdates = new Vector();
        HashMap updateMap = new HashMap();

        int updateCount = targetDriver.getComponentUpdateCount();
        for (int updateNo = 0; updateNo < updateCount; updateNo++) {
            componentUpdate nextUpdate = targetDriver.getComponentUpdate(updateNo);
            String nextName = nextUpdate.getComponentName();

            if (shouldBeApplied(targetId, nextUpdate, optionalMap)) {
                Object mappableUpdate; // Either a componentUpdate or an ExtendedComponent

                if (!nextUpdate.getIsExternalAsBoolean()) {
                    targetUpdates.addElement(nextName);
                    mappableUpdate = nextUpdate;
                } else {
                    // This is an mq or ihs update
                    mappableUpdate = processExternalComponents(targetUpdates, nextUpdate);
                }

                if (mappableUpdate != null)
                    updateMap.put(nextName, mappableUpdate);
            }
        }

        return reorderForInstall(targetUpdates, updateMap);
    }

    // This is a limited implementation, requiring that the
    // ordering information never link more than two elements.

    // 'updateNames' is a misnomer -- there may be
    // extended components in the list.

    protected Vector reorderForInstall(Vector updateNames, HashMap updateMap)
    {
        log("Reording updates for installation.");

        Vector updates = new Vector();
        HashSet unresolvedNames = new HashSet();

        int numUpdates = updateNames.size();
        for ( int nameNo = 0; nameNo < numUpdates; nameNo++ ) {
            Object nextNamedUpdate = updateNames.elementAt(nameNo);

            if ( nextNamedUpdate instanceof String ) {
                String componentName = (String) nextNamedUpdate;
                componentUpdate typedUpdate = (componentUpdate) updateMap.get(componentName);

                updates.addElement(typedUpdate);
                unresolvedNames.add(componentName);

            } else {
                ExtendedComponent typedUpdate = (ExtendedComponent) nextNamedUpdate;
                String componentName = typedUpdate.getBaseComponent().getName();

                updates.addElement(nextNamedUpdate); // is an extended component
                unresolvedNames.add(componentName);
            }
        }

        Vector unresolvedUpdates = (Vector) updates.clone();
        Vector resolvedUpdates   = new Vector();
        Vector resolvedNames     = new Vector();

        while ( unresolvedUpdates.size() > 0 ) {

            int updateIndex = 0;

            while ( updateIndex < unresolvedUpdates.size() ) {
                Object unresolvedUpdate = unresolvedUpdates.elementAt(updateIndex); 

                // Get the update's name, and it's prerequisite's name.  (The
                // prerequisite name may be null.)
                //
                // Get the name and prerequisite name differently depending on
                // the type of the update.

                String componentName;
                String prereqName;

                if ( unresolvedUpdate instanceof componentUpdate ) {
                    componentUpdate typedUpdate = (componentUpdate) unresolvedUpdate;
                    componentName = typedUpdate.getComponentName();
                    prereqName = getPrereqComponent(typedUpdate);

                } else {
                    componentName = ((ExtendedComponent) unresolvedUpdate).getBaseComponent().getName();
                    prereqName = null;
                }

                // If there is no prerequisite, or if the prerequisite
                // is already in the resolved list, simply transfer
                // the update from the unresolved list to the end of
                // the unresolved list.

                // TFB: Added the case to make sure that the prerequisite
                // can actually be satisfied.  Without this extra check,
                // a resumed installation can get into an infinite loop,
                // when attempting to place an update having a prereq
                // which was already installed.
                //
                // The extra check causes the update to be transferred
                // whenever the prereq is not available in the unresolved
                // updates.

                if ( (prereqName == null) ||
                     (resolvedNames.indexOf(prereqName) >= 0) ||
                     !unresolvedNames.contains(prereqName) ) { // TFB: Added this test.

                    resolvedUpdates.add(unresolvedUpdate);
                    resolvedNames.add(componentName);

                    unresolvedUpdates.remove(updateIndex);
                    unresolvedNames.remove(componentName);
                } else {
                    updateIndex++;
                }
            }
        }
        
        if ( unresolvedUpdates.size() < 1 ) {
            log("Update prerequisite resolution and sort completed successfully.");
            updates = resolvedUpdates;
        }
        else {
            log("Update prerequisite resolution and sort failed.  Updates are not sorted.");
        }
        

        log("Updates, in order: ");

        updateNames = new Vector();

        numUpdates = updates.size();

        for ( int nameNo = 0; nameNo < numUpdates; nameNo++ ) {
            Object nextNamedUpdate = updates.elementAt(nameNo);
            componentUpdate nextNamedComponentUpdate;

            String nextName;

            if ( nextNamedUpdate instanceof componentUpdate ) {
                nextName = ((componentUpdate) nextNamedUpdate).getComponentName();
                updateNames.addElement(nextName);

            } else {
                ExtendedComponent nextExtComponent = (ExtendedComponent) nextNamedUpdate;
                updateNames.addElement(nextExtComponent);

                nextName = nextExtComponent.getBaseComponent().getName();
            }

            log(" [ " + nameNo + " ]: " + nextName);
        }

        return updateNames;
    }

    public static final String prereqComponentPropertyName = "after-component";

    protected String getPrereqComponent(componentUpdate anUpdate)
    {
        int numProps = anUpdate.getCustomPropertyCount();

        String prereqName = null;

        for ( int propNo = 0; (prereqName == null) && (propNo < numProps); propNo++ ) {
            customProperty nextProp = anUpdate.getCustomProperty(propNo);

            String nextPropName = nextProp.getPropertyName();

            if ( (nextPropName != null) && nextPropName.equals(prereqComponentPropertyName) )
                prereqName = nextProp.getPropertyValue();
        }

        return prereqName;
    }

    public ExtendedComponent processExternalComponents
        (Vector targetUpdates, componentUpdate candidateUpdate)
    {
        ExtendedComponent matchingComponent = null;

        Vector extendedComponents = getExtendedComponents();
        int extendedCompSize = extendedComponents.size();
        for (int i = 0; (matchingComponent == null) && (i < extendedCompSize); i++) {
            ExtendedComponent ec = (ExtendedComponent) extendedComponents.elementAt(i);
            if (candidateUpdate.getComponentName().equals(ec.getBaseComponent().getName())) {
                targetUpdates.addElement(ec);
                matchingComponent = ec;
            }
        }

        return matchingComponent;
    }

    public boolean shouldBeApplied(String parentId, componentUpdate candidateUpdate, HashMap optionalMap) {
        String candidateName = candidateUpdate.getComponentName();

        WASProduct useWASProduct = getWPProduct();
        String contextDir = useWASProduct.getProductDirName();

        String logPrefix = "Testing component update [ " + candidateName + " ] ...";

        logFlush(logPrefix);

        boolean result;

        if (componentWasUpdatedByPTF(parentId, candidateName)) {
            log(logPrefix + " already applied: skipping");
            result = false;

        } else {
            if (componentIsPresent(candidateName)) {
                log(logPrefix + " matches an installed component: tagging");
                result = true;

            } else {
                log(logPrefix + " matches no installed component ...");

                if (candidateUpdate.getUpdateTypeAsEnum() == enumUpdateType.ADD_UPDATE_TYPE) {
                    log(logPrefix + " is an ADD type update");

                    if (candidateName.equals("prereq.jdk")) {
                        if (!getIsClient()) {
                            log(logPrefix + " is prereq.jdk && not Client: tagging");
                            result = true;
                        } else {  // client
                            if (getIsJ2EEClient()) {  // J2EE Client
                                log(logPrefix + " is prereq.jdk && J2EE Client: tagging");
                                result = true;
                            } else {  // Other types of Client
                                log(logPrefix + " is prereq.jdk && Client && not J2EE Client: skipping");
                                result = false;
                            }
                        }
                        
                        if (!isIHS(contextDir)){
                            log(logPrefix + " is prereq.jdk && not IHS-only: tagging");
                            result = true;
                        } else {
                            log(logPrefix + " is prereq.jdk && is IHS-only: skipping");
                            result = false; 
                        }
                    } else { //  other component adds
                        log(logPrefix + " not prereq.jdk : tagging");
                        result = true;
                    }

                    if (candidateName.equals("config.templates")) {
                       if (isWASPlugin(contextDir) || isWASLite(contextDir) || isIHS(contextDir)) {
                            log(logPrefix + " is config.templates && WAS Plugin Only: skipping");
                            result = false;
                       } else {
                            log(logPrefix + " is config.templates && not WAS Plugin Only: tagging");
                            result = true;                          
                       }                    
                    }

                } else {
                    log(logPrefix + " is not an ADD type update: skipping");
                    result = false;
                }
            }
        }

        if (result) {
            if (candidateUpdate.getIsOptionalAsBoolean()) {
                log(logPrefix + " an optional update ...");

                if (optionalMap.containsKey(candidateName)) {
                    log(logPrefix + " has been selected: retaining");
                } else {
                    log(logPrefix + " has not been selected: discarding");
                    result = false;
                }
            }
        }

        logFlush("");

        return result;
    }

    // Install reversion ...
    //
    // public boolean shouldUndo(Vector);
    //
    // public updateEvent undoInstallation(PTFImage, Vector)
    //
    // UndoCommand createUndoCommand(PTFImage, Vector)
    // static class UndoCommand;

    public boolean shouldUndo(Vector updateEvents) {
        return (didFail(updateEvents) || wasCancelled(updateEvents));
    }

    public updateEvent undoInstallation(PTFImage targetImage, Vector updateEvents) {
        updateEvent undoEvent = null;
        boolean cancelled;

        UndoCommand undoCommand = createUndoCommand(targetImage, updateEvents);

        if (undoCommand != null) {

            setTaskCount(1 + undoCommand.getComponentCount() + 1);
            pushBanner("");

            try {
                PTFUninstaller uninstaller = createPTFUninstaller();

                String usePTFId = undoCommand.getPTFId();
                Vector useComponentNames = undoCommand.getComponentNames();

                undoEvent = uninstaller.uninstall(usePTFId, useComponentNames, DO_IGNORE_ERRORS);
                cancelled = undoEvent.wasCancelled();

            } finally {
                popBanner();
            }
        }

        return undoEvent;
    }

    protected UndoCommand createUndoCommand(PTFImage targetImage, Vector updateEvents) {
        UndoCommand resultUndo = null;

        if (updateEvents != null) {

            for (int eventNo = 0;(resultUndo == null) && (eventNo < updateEvents.size()); eventNo++) {

                updateEvent nextEvent = (updateEvent) updateEvents.elementAt(eventNo);

                if (nextEvent.getEventTypeAsEnum() == enumEventType.PTF_EVENT_TYPE) {
                    UndoCommand nextUndo = new UndoCommand(targetImage, nextEvent);

                    if (nextUndo.getComponentCount() > 0)
                        resultUndo = nextUndo;
                }
            }
        }

        return resultUndo;
    }

    // Class for recording the instructions for uninstalling an ptf.
    // These instructions are tailored for backing out a failed or
    // cancelled installation.

    /**
	 *  
	 */
    static class UndoCommand {
        protected PTFImage ptfImage;
        protected Vector componentNames;

        // Instantor ...
        //
        // UndoCommand(PTFImage, updateEvent);

        protected UndoCommand(PTFImage ptfImage, updateEvent sourceEvent) {
            this.ptfImage = ptfImage;

            this.componentNames = new Vector();
            addComponentNames(sourceEvent);
        }

        // Image access ...
        //
        // PTFImage getPTFImage();
        // String getPTFId();

        protected PTFImage getPTFImage() {
            return ptfImage;
        }

        protected String getPTFId() {
            return getPTFImage().getPTFId();
        }

        // Component access ...
        //
        // Vector getComponentNames();
        // int getComponentCount();
        //
        // void addComponentName(String);
        // void addComponentNames(updateEvent);

        /**
		 * @return  the componentNames
		 * @uml.property  name="componentNames"
		 */
        protected Vector getComponentNames() {
            return componentNames;
        }

        protected int getComponentCount() {
            return componentNames.size();
        }

        protected void addComponentName(String componentName) {
            componentNames.addElement(componentName);
        }

        // Note that components are scanned in reverse order.
        // This is important: When undoing, the operations
        // are backed out in reverse order of application.

        // Don't add the names of component which were not
        // successfully installed.

        protected void addComponentNames(updateEvent sourceEvent) {
            int eventNo = sourceEvent.getUpdateEventCount();

            while (eventNo > 0) {
                eventNo--;

                updateEvent nextEvent = sourceEvent.getUpdateEvent(eventNo);

                if (nextEvent.succeeded())
                    addComponentName(nextEvent.getId());
            }
        }
    }

    // Uninstall ...
    //
    // public boolean preparePTF(String);
    //
    // public Vector[updateEvent] uninstall(String);
    // public Vector uninstall(String, Vector[efix]);
    //
    // int countUninstallTasks(String);

    public boolean preparePTF(String ptfId) {
        WPProduct useProduct = getWPProduct();
        WPHistory useHistory = getWPHistory();

        boolean failed = false;

        ptf usePTF = useProduct.getPTFById(ptfId);
        ptfDriver usePTFDriver = useHistory.getPTFDriverById(ptfId);

        if ((usePTF != null) && (usePTFDriver != null))
            useHistory.getPTFAppliedById(ptfId);

        Iterator productExceptions = useProduct.getExceptions();

        if (productExceptions.hasNext()) {
            failed = true;

            while (productExceptions.hasNext()) {
                WASProductException nextException = (WASProductException) productExceptions.next();

                addException("WUPD0231E", new String[] { ptfId }, nextException);
            }
        }

        Iterator historyExceptions = useHistory.getExceptions();

        if (historyExceptions.hasNext()) {
            failed = true;

            while (historyExceptions.hasNext()) {
                WASHistoryException nextException = (WASHistoryException) historyExceptions.next();

                addException("WUPD0231E", new String[] { ptfId }, nextException);
            }
        }

        return !failed;
    }

    // Uninstall the listed ptfes.
    //
    // Preparation must be done beforehand on the ids.  This
    // preparation makes sure that the ptf files for each
    // of the listed ids can be loaded.  The uninstall cannot
    // be performed without loading the ptf file.
    //
    // The uninstall will proceed even if an error occurs.
    //
    // Check the returned events to see if the uninstall was
    // cancelled or if an error occurred.

    // Need to determine the proper order of the ptfes and their
    // components: They need to be uninstalled in the order in
    // which they were installed.
    //
    // This means scanning the event history and determining the
    // actual installation order, and reversing that order.

    public Vector uninstall(String ptfId) {
        // return uninstall(ptfId, getEFixes());
        //Saiyu return uninstall(ptfId, null);
    	return uninstall(ptfId, null, null); //Saiyu
    }

    public Vector uninstall(String ptfId, Vector efixes) {
   	    return uninstall(ptfId, efixes, null);
    }
    
    public Vector uninstall(String ptfId, Vector efixes, Vector selectedCompInstallList ) { //Saiyu
        Vector updateEvents = new Vector();

        int efixCount;
        
        if(null == efixes)
            efixCount = 0;
        else
            efixCount = efixes.size();

        setTaskCount((efixCount * 3) + (1 + countUninstallTasks(ptfId) + 1));
        pushBanner("");

        try {
            EFixUninstaller useEFixUninstaller = createEFixUninstaller();

            boolean failed = false, cancelled = false;

            for (int efixNo = 0; !cancelled && (efixNo < efixCount); efixNo++) {
                efix nextEFix = (efix) efixes.elementAt(efixNo);
                String nextEFixId = nextEFix.getId();

                updateEvent nextEvent = useEFixUninstaller.uninstall(nextEFixId);
                updateEvents.addElement(nextEvent);

                failed = nextEvent.failed();
                cancelled = nextEvent.wasCancelled();
            }

            if (!cancelled) {
                PTFUninstaller usePTFUninstaller = createPTFUninstaller();

                updateEvent ptfEvent = null; //Saiyu
                if ((selectedCompInstallList != null) && (selectedCompInstallList.size()>0)) //Saiyu
                	ptfEvent = usePTFUninstaller.uninstall(ptfId, selectedCompInstallList, DO_NOT_IGNORE_ERRORS); //Saiyu
// HANK

                else //Saiyu
                	ptfEvent = usePTFUninstaller.uninstall(ptfId); //Saiyu                	
                
                updateEvents.addElement(ptfEvent);

                failed = ptfEvent.failed();
                cancelled = ptfEvent.wasCancelled();
            }

        } finally {
            popBanner();
        }

        return updateEvents;
    }

    protected int countUninstallTasks(String ptfId) {
        ptfApplied applied = getWPHistory().getPTFAppliedById(ptfId);

        return ((applied == null) ? 0 : applied.getComponentAppliedCount());
    }

    // Common event introspection ...
    //
    // public boolean didFail(Vector);
    // public boolean wasCancelled(Vector);
    //
    // public updateEvent selectFailingEvent(Vector);
    // public updateEvent selectCancelledEvent(Vector);

    public boolean didFail(Vector updateEvents) {
        return (selectFailingEvent(updateEvents) != null);
    }

    public boolean wasCancelled(Vector updateEvents) {
        return (selectCancelledEvent(updateEvents) != null);
    }

    public updateEvent selectFailingEvent(Vector updateEvents) {
        int numEvents = updateEvents.size();

        updateEvent failingEvent = null;

        for (int eventNo = 0;(failingEvent == null) && (eventNo < numEvents); eventNo++) {
            updateEvent nextEvent = (updateEvent) updateEvents.elementAt(eventNo);

            if (nextEvent.failed())
                failingEvent = nextEvent;
        }

        return failingEvent;
    }

    // Answer the first event which was cancelled.  Answer null if
    // none of the events was cancelled.

    public updateEvent selectCancelledEvent(Vector updateEvents) {
        int numEvents = updateEvents.size();

        updateEvent cancelledEvent = null;

        for (int eventNo = 0;(cancelledEvent == null) && (eventNo < numEvents); eventNo++) {
            updateEvent nextEvent = (updateEvent) updateEvents.elementAt(eventNo);

            if (nextEvent.wasCancelled())
                cancelledEvent = nextEvent;
        }

        return cancelledEvent;
    }

    // WASProduct Utility:
    //
    // public Vector getEFixes();

    public Vector getEFixes() {
        Vector resultEFixes = new Vector();

        Iterator useEFixes = getWPProduct().getEFixes();

        while (useEFixes.hasNext()) {
            efix nextEFix = (efix) useEFixes.next();
            resultEFixes.addElement(nextEFix);
        }

        return resultEFixes;
    }

    // Actor construction:
    //
    // EFixUninstaller createEFixUninstaller();
    // PTFInstaller createPTFInstaller();
    // PTFUninstaller createPTFUninstaller();

    protected EFixUninstaller createEFixUninstaller() {
        EFixUninstaller useUninstaller = new EFixUninstaller(getWPProduct(), getWPHistory(), getNotifier(), getIOService());

        useUninstaller.enableAlternateBanners(true);

        return useUninstaller;
    }

    protected PTFInstaller createPTFInstaller() {
        return new PTFInstaller(getWPProduct(), getWPHistory(), getNotifier(), getIOService());
    }

    protected PTFUninstaller createPTFUninstaller() {
        return new PTFUninstaller(getWPProduct(), getWPHistory(), getNotifier(), getIOService());
    }
}
