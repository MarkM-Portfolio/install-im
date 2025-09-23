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
 * EFix Batch Updater
 *
 * History 1.1, 12/20/02
 *
 * 05-Aug-2002 Initial Version
 *
 * 19-Nov-2002 Modify undo processing to skip
 *             component events which did not complete.
 */

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.product.WASProductException;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.WASHistoryException;
import com.ibm.websphere.product.history.WPHistory;
import com.ibm.websphere.product.history.xml.efixApplied;
import com.ibm.websphere.product.history.xml.efixDriver;
import com.ibm.websphere.product.history.xml.updateEvent;
import com.ibm.websphere.product.xml.efix.efix;
import com.ibm.websphere.update.efix.prereq.EFixPrereqChecker;
import com.ibm.websphere.update.ioservices.IOService;
import com.ibm.websphere.update.ioservices.Notifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class EFixBatchUpdater
    extends BaseInstaller
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "12/20/02" ;

    // Instantor ...
    //
    // public EFixBatchUpdater(WASProduct, WASHistory, Notifier, IOService);

    public EFixBatchUpdater(WPProduct wpsProduct, WPHistory wpsHistory,
                            Notifier notifier, IOService ioService)
    {
        super(wpsProduct, wpsHistory, notifier, ioService);
    }
    
    public EFixBatchUpdater(WPProduct wpsProduct, WPHistory wpsHistory, IOService ioService)
    {
    	super(wpsProduct, wpsHistory, ioService);
    }

    // Installation ...
    //
    // public boolean prepareImages(Vector[efixImage]);
    // public Vector[updateEvent] install(Vector[efixImage]);
    //
    // int countInstallTasks(Vector[efixImage]);
    // int countInstallableComponents(efixImage);
    // String installPrefix(int, int, String);

    // The images must be prepared before proceeding;
    // the result, true or false, tells if an error
    // occurred during preparation.
    //
    // This preparation step has been set to allow
    // error handling to be collected to a single
    // point.  Otherwise, exceptions are possible
    // throughout the installation process, requiring
    // extra exception handling.
    //
    // When the result is false, preparation failed,
    // and an exception will be set.
    //
    // Preparation consists of loading the efix file for
    // the image, and loading the component images for the
    // image.

    public boolean prepareImages(Vector updateImages)
    {
        int imageCount = updateImages.size();   

        boolean failed = false;

        for ( int imageNo = 0; !failed && (imageNo < imageCount); imageNo++ ) {

            EFixImage nextImage = (EFixImage) updateImages.elementAt(imageNo);
            try {
                nextImage.prepareDriver(); // throws IOException, FactoryException
                nextImage.prepareComponents(); // throws IOException

            } catch ( Exception e ) {
                addException("WVER0230", new String[] { nextImage.getEFixId() }, e);
                failed = true;
            }
        }

        return !failed;
    }

    public boolean testInstallPrerequisites(Vector<EFixImage> updateImages,
                                            Vector<EFixImage> installOrder,
                                            Vector errors, Vector supersededInfo)
    {
        Vector<efixDriver> efixDrivers = new Vector();
        HashMap<String, EFixImage> imageMap = new HashMap<String, EFixImage>();
        int imageCount = updateImages.size();

        for ( int imageNo = 0; imageNo < imageCount; imageNo++ ) {
            EFixImage nextImage = (EFixImage) updateImages.elementAt(imageNo);

            imageMap.put(nextImage.getEFixId(), nextImage);

            efixDriver nextDriver = nextImage.getEFixDriver();

            efixDrivers.addElement(nextDriver);
        }
        // com.ibm.websphere.update.efix.prereq.EFixPrereqChecker
        EFixPrereqChecker checker =
            new EFixPrereqChecker(getWPProduct(), getWPHistory());
        Vector driverInstallOrder = new Vector();
        boolean result = false;
        if (!updateImages.isEmpty())
        {
            result = checker.testEFixInstallation(efixDrivers, driverInstallOrder, errors, supersededInfo);
//            System.out.println("driverInstallOrder: " + driverInstallOrder.size());
            
            if(result)  // if all efixes passed the test, add to installOrder list
            {
                for ( int driverNo = 0; driverNo < driverInstallOrder.size(); driverNo++ )
                {
                    efixDriver nextDriver = (efixDriver) driverInstallOrder.elementAt(driverNo);
                    String nextId = nextDriver.getId();

                    EFixImage nextImage = (EFixImage) imageMap.get(nextId);

                    installOrder.addElement(nextImage);
                }
            }
        }

//        System.out.println("installOrder: " + installOrder.size());
        for (int i = 0; i< installOrder.size(); i++)
        {
        	EFixImage n = (EFixImage)installOrder.elementAt(i);
        	
        	//System.out.println(n.getEFixId());
        }
        return result;
    }

    protected boolean sampleInstallCheck(Vector updateImages)
    {
        return testInstallPrerequisites(updateImages, new Vector(), new Vector(), new Vector());
    }

    public Vector install(Vector updateImages)
    {
        sampleInstallCheck(updateImages); // for debugging

        int imageCount = updateImages.size();

        setTaskCount( countInstallTasks(updateImages) );

        EFixInstaller installer = new EFixInstaller(getWPProduct(), getWPHistory(),
                                                    getNotifier(), getIOService());
        Vector updateEvents = new Vector();

        boolean failed = false,
                cancelled = false;

        int imageNo = 0;
        
//        System.out.println("imageCount: " + imageCount);
        
        while ( !failed && !cancelled && (imageNo < imageCount) ) {
            EFixImage nextImage = (EFixImage) updateImages.elementAt(imageNo);
            String nextEFixId = nextImage.getEFixId();
            imageNo++;
//            System.out.println("imageNO: " + imageNo);
            LCUtil.setInstallImage(nextImage.getEFixDriver());
            
            pushBanner( installPrefix(imageNo, imageCount, nextEFixId) );

            try {
                updateEvent nextEvent = installer.install(nextImage);

                updateEvents.addElement(nextEvent);

//                failed = nextEvent.failed();
//                cancelled = nextEvent.wasCancelled();

            } finally {
                popBanner();
            }
        }
        
        return updateEvents;
    }

    protected int countInstallTasks(Vector updateImages)
    {
        int taskCount = 0;

        int imageCount = updateImages.size();

        for ( int imageNo = 0; imageNo < imageCount; imageNo++ ) {
            EFixImage nextImage = (EFixImage) updateImages.elementAt(imageNo);

            // One task: a preparation step.
            taskCount++;

            // One task for each component that can be installed.
            taskCount += countInstallableComponents(nextImage);

            // One task: a completion step.
            taskCount++;
        }

        return taskCount;
    }

    protected int countInstallableComponents(EFixImage anImage)
    {
        String useEFixId = anImage.getEFixId();

        Iterator useComponentNames = anImage.getComponentNames().iterator();

        int count = 0;

        while ( useComponentNames.hasNext() ) {
            String nextComponentName = (String) useComponentNames.next();

            // Not installable if the component is absent;
            // Not installable if this component update already installed.

            if ( isEFixComponentInstallable(useEFixId, nextComponentName) )
                count++;
        }

        return count;
    }

    // (1 of 3) Installing efix PQ123456; preparing for installation
    // (1 of 3) Installing efix PQ123456; installing component 1 of 2
    // (1 of 3) Installing efix PQ123456; installing component 2 of 2
    // (1 of 3) Installing efix PQ123456; completing installation

    protected String installPrefix(int imageNo, int imageCount, String efixId)
    {
        return getString("prefix.efix.install",
                         new Object[] { new Integer(imageNo),
                                        new Integer(imageCount),
                                        efixId });
    }

    // Install reversion ...
    //
    // public static boolean shouldUndo(Vector[updateEvent]);
    //
    // public Vector[updateEvent]
    //     undoInstallation(Vector[EFixImage], Vector[updateEvent]);
    //
    // protected Vector[UndoCommand]
    //     createUndoCommands(Vector[updateImage], Vector[updateEvent]);
    //
    // static class UndoCommand;
    //
    // int countUndoInstallTasks(Vector[UndoCommand]);
    // String undoIninstallPrefix(int, int, String);

    public static boolean shouldUndo(Vector updateEvents)
    {
        return ( didFail(updateEvents) || wasCancelled(updateEvents) );
    }

    public Vector undoInstallation(Vector updateImages, Vector updateEvents)
    {
        Vector undoCommands = createUndoCommands(updateImages, updateEvents);

        setTaskCount( countUndoInstallTasks(undoCommands) );

        int commandCount = undoCommands.size();

        EFixUninstaller uninstaller =
            new EFixUninstaller(getWPProduct(), getWPHistory(),
                                getNotifier(), getIOService());

        Vector undoEvents = new Vector();

        boolean cancelled = false;
        int commandNo = 0;

        while ( !cancelled && (commandNo < commandCount) ) {
            UndoCommand nextCommand = (UndoCommand) undoCommands.elementAt(commandNo);
            String nextEFixId = nextCommand.getEFixId();
            Vector nextComponentNames = nextCommand.getComponentNames();

            commandNo++;

            pushBanner( undoInstallPrefix(commandNo, commandCount, nextEFixId) );

            try {
                // When reverting, do as much as possible.  This
                // means proceeding through all of the undo commands
                // even after a command fails.

                updateEvent nextEvent =
                    uninstaller.uninstall(nextEFixId, nextComponentNames, DO_IGNORE_ERRORS);

                undoEvents.addElement(nextEvent);

                cancelled = nextEvent.wasCancelled();

                // Note that we don't care if the reversion failed.
                // We push on through all of the commands, the goal
                // being to revert as much as possible.

            } finally {
                popBanner();
            }
        }

        return undoEvents;
    }

    // Note that events are scanned in reverse order.
    // This is important: When undoing, the operations
    // are backed out in reverse order of application.
    //
    // The update events are likely smaller than the
    // update images: Make sure to scan based on the
    // events and not on the images.
    //
    // The images and events are expected to be in
    // correspondence, that is, image[x] has its
    // update recorded in event[x].

    protected Vector createUndoCommands(Vector updateImages, Vector updateEvents)
    {
        int eventCount = updateEvents.size();

        Vector undoCommands = new Vector();

        int eventNo = eventCount;
        while ( eventNo > 0 ) {
            eventNo--;

            EFixImage nextImage = (EFixImage) updateImages.elementAt(eventNo);
            updateEvent nextEvent = (updateEvent) updateEvents.elementAt(eventNo);

            // The undo command is created by binding the image and
            // scanning the event for component updates.  A component
            // name is added for each component event, in the reverse
            // order of installation.

            UndoCommand nextUndo = new UndoCommand(nextImage, nextEvent);

            // Don't add an undo command when there were no
            // components which were successfully installed.

            if ( nextUndo.getComponentCount() > 0 )
                undoCommands.addElement(nextUndo);
        }

        return undoCommands;
    }

    // Class for recording the instructions for uninstalling an efix.
    // These instructions are tailored for backing out a failed or
    // cancelled installation.

    /**
	 *  
	 */
    static class UndoCommand
    {
        protected EFixImage EFixImage;
        protected Vector componentNames;

        // Instantor ...
        //
        // UndoCommand(EFixImage, updateEvent);
        // UndoCommand(EFixImage);

        protected UndoCommand(EFixImage EFixImage, updateEvent sourceEvent)
        {
            this.EFixImage = EFixImage;

            this.componentNames = new Vector();
            addComponentNames(sourceEvent);
        }

        // Image access ...
        //
        // EFixImage getEFixImage();
        // String getEFixId();

        /**
		 * @return  the eFixImage
		 * @uml.property  name="eFixImage"
		 */
        protected EFixImage getEFixImage()
        {
            return EFixImage;
        }

        protected String getEFixId()
        {
            return getEFixImage().getEFixId();
        }

        // Component access ...
        //
        // Vector[String] getComponentNames();
        // int getComponentCount();
        // void addComponentName(String);
        // void addComponentNames(updateEvent);

        /**
		 * @return  the componentNames
		 * @uml.property  name="componentNames"
		 */
        protected Vector getComponentNames()
        {
            return componentNames;
        }

        protected int getComponentCount()
        {
            return componentNames.size();
        }

        protected void addComponentName(String componentName)
        {
            componentNames.addElement(componentName);
        }

        // Note that components are scanned in reverse order.
        // This is important: When undoing, the operations
        // are backed out in reverse order of application.

        // Don't add the names of component which were not
        // successfully installed.

        protected void addComponentNames(updateEvent sourceEvent)
        {
            int eventNo = sourceEvent.getUpdateEventCount();

            while ( eventNo > 0 ) {
                eventNo--;

                updateEvent nextEvent = sourceEvent.getUpdateEvent(eventNo);

                if ( nextEvent.succeeded() )
                    addComponentName( nextEvent.getId() );
            }
        }
    }

    // Task processing ...

    protected int countUndoInstallTasks(Vector undoCommands)
    {
        int taskCount = 0;

        int commandCount = undoCommands.size();

        for ( int commandNo = 0; commandNo < commandCount; commandNo++ ) {
            UndoCommand nextCommand = (UndoCommand)
                undoCommands.elementAt(commandNo);

            // One task: a preparation step.
            taskCount++;

            // One task for each component to be uninstalled.
            taskCount += nextCommand.getComponentCount();

            // One task: a completion step.
            taskCount++;
        }

        return taskCount;
    }

    // (1 of 3) Reverting installation of efix PQ123456; preparing for uninstall
    // (1 of 3) Reverting installation of efix PQ123456; uninstalling component 1 of 2
    // (1 of 3) Reverting installation of efix PQ123456; uninstalling component 2 of 2
    // (1 of 3) Reverting installation of efix PQ123456; completing installation

    protected String undoInstallPrefix(int commandNo, int commandCount, String efixId)
    {
        return getString("prefix.efix.revert",
                         new Object[] { new Integer(commandNo),
                                        new Integer(commandCount),
                                        efixId });
    }

    // Uninstall ...
    //
    // public boolean prepareEFixes(Vector[String]);
    //
    // public Vector[updateEvent] uninstall(Vector[String]);
    //
    // int countUninstallTasks(Vector[String]);
    // int countUninstallTasks(String);
    // String uninstallPrefix(int, int, String);

    // The ids must be prepared before proceeding;
    // the result, true or false, tells if an error
    // occurred during preparation.
    //
    // This preparation step has been set to allow
    // error handling to be collected to a single
    // point.  Otherwise, exceptions are possible
    // throughout the uninstall process, requiring
    // extra exception handling.
    //
    // When the result is false, preparation failed,
    // and an exception will be set.
    //
    // Preparation consists of loading the efix file for
    // each efix, and loading the applieds for the efix.

    public boolean prepareEFixes(Vector efixIds)
    {
    	//System.out.println( this.getClass().getName() + "::prepareEFixes : "  );
        int idCount = efixIds.size();

        WPProduct useProduct = getWPProduct();
        WPHistory useHistory = getWPHistory();

        boolean failed = false;

        for ( int idNo = 0; !failed && (idNo < idCount); idNo++ ) {
            String nextEFixId = (String) efixIds.elementAt(idNo);

            efix nextEFix = useProduct.getEFixById(nextEFixId);
            efixDriver nextEFixDriver = useHistory.getEFixDriverById(nextEFixId);

            //System.out.println(nextEFixDriver.getProductPrereq(0).getProductId());
            
            if ( (nextEFix != null) && (nextEFixDriver != null) )
                useHistory.getEFixAppliedById(nextEFixId);

            Iterator productExceptions = useProduct.getExceptions();

            if ( productExceptions.hasNext() ) {
                failed = true;

                while ( productExceptions.hasNext() ) {
                    WASProductException nextException = (WASProductException)
                        productExceptions.next();

                    addException("WUPD0231E",
                                 new String[] { nextEFixId },
                                 nextException);
                }
            }

            Iterator historyExceptions = useHistory.getExceptions();

            if ( historyExceptions.hasNext() ) {
                failed = true;

                while ( historyExceptions.hasNext() ) {
                    WASHistoryException nextException = (WASHistoryException)
                        historyExceptions.next();

                    addException("WUPD0231E",
                                 new String[] { nextEFixId },
                                 nextException);
                }
            }
        }

        return !failed;
    }

    public boolean testUninstallPrerequisites(Vector efixIds,
                                              Vector uninstallOrder,
                                              Vector errors)
    {
        EFixPrereqChecker checker = new EFixPrereqChecker(getWPProduct(), getWPHistory());

        return checker.testEFixUninstallation(efixIds, uninstallOrder, errors);
    }

    protected boolean sampleUninstallCheck(Vector efixIds)
    {
        return testUninstallPrerequisites(efixIds, new Vector(), new Vector());
    }

    // Uninstall the listed efixes.
    //
    // Preparation must be done beforehand on the ids.  This
    // preparation makes sure that the efix files for each
    // of the listed ids can be loaded.  The uninstall cannot
    // be performed without loading the efix file.
    //
    // The uninstall will proceed even if an error occurs.
    //
    // Check the returned events to see if the uninstall was
    // cancelled or if an error occurred.

    // Need to determine the proper order of the efixes and their
    // components: They need to be uninstalled in the order in
    // which they were installed.
    //
    // This means scanning the event history and determining the
    // actual installation order, and reversing that order.

    public Vector uninstall(Vector efixIds)
    {
        sampleUninstallCheck(efixIds); // for debugging

        int idCount = efixIds.size();

        setTaskCount( countUninstallTasks(efixIds) );

        EFixUninstaller uninstaller =
            new EFixUninstaller(getWPProduct(), getWPHistory(),
                                getNotifier(), getIOService());

        Vector updateEvents = new Vector();

        boolean cancelled = false;

        int idNo = 0;

        while ( !cancelled && (idNo < idCount) ) {
            String nextEFixId = (String) efixIds.elementAt(idNo);

            idNo++;

            pushBanner( uninstallPrefix(idNo, idCount, nextEFixId) );

            try {
                updateEvent nextEvent = uninstaller.uninstall(nextEFixId);
                updateEvents.addElement(nextEvent);
                cancelled = nextEvent.wasCancelled();

            } finally {
                popBanner();
            }
        }

        return updateEvents;
    }

    protected int countUninstallTasks(Vector efixIds)
    {
        int taskCount = 0;

        int idCount = efixIds.size();

        for ( int idNo = 0; idNo < idCount; idNo++ ) {
            String nextEFixId = (String) efixIds.elementAt(idNo);

            taskCount++; // Once for prepare

            taskCount += countUninstallTasks(nextEFixId);

            taskCount++; // Once for completion
        }

        return taskCount;
    }

    protected int countUninstallTasks(String anEFixId)
    {
        efixApplied anApplied = getWPHistory().getEFixAppliedById(anEFixId);

        return ( (anApplied == null) ? 0 : anApplied.getComponentAppliedCount() );
    }

    // (1 of 3) Uninstalling efix PQ123456; preparing for uninstall
    // (1 of 3) Uninstalling efix PQ123456; uninstalling component 1 of 2
    // (1 of 3) Uninstalling efix PQ123456; uninstalling component 2 of 2
    // (1 of 3) Uninstalling efix PQ123456; completing uninstall

    protected String uninstallPrefix(int idNo, int idCount, String efixId)
    {
        return getString("prefix.efix.uninstall",
                         new Object[] { new Integer(idNo),
                                        new Integer(idCount),
                                        efixId });
    }

    // Common event introspection ...
    //
    // public static boolean didFail(Vector[updateEvent]);
    // public static boolean wasCancelled(Vector[updateEvent])
    // public static updateEvent selectFailingEvent(Vector[updateEvent]);
    // static static updateEvent selectCancelledEvent(Vector[updateEvent]);

    // Answer the first event which failed.  Answer null if
    // none of the events failed.

    public static boolean didFail(Vector updateEvents)
    {
        return ( selectFailingEvent(updateEvents) != null );
    }

    public static boolean wasCancelled(Vector updateEvents)
    {
        return ( selectCancelledEvent(updateEvents) != null );
    }

    public static updateEvent selectFailingEvent(Vector updateEvents)
    {
        int numEvents = updateEvents.size();

        updateEvent failingEvent = null;

        for ( int eventNo = 0; (failingEvent == null) && (eventNo < numEvents); eventNo++ ) {
            updateEvent nextEvent = (updateEvent) updateEvents.elementAt(eventNo);

            if ( nextEvent.failed() )
                failingEvent = nextEvent;
        }

        return failingEvent;
    }

    // Answer the first event which was cancelled.  Answer null if
    // none of the events was cancelled.

    public static updateEvent selectCancelledEvent(Vector updateEvents)
    {
        int numEvents = updateEvents.size();

        updateEvent cancelledEvent = null;

        for ( int eventNo = 0; (cancelledEvent == null) && (eventNo < numEvents); eventNo++ ) {
            updateEvent nextEvent = (updateEvent) updateEvents.elementAt(eventNo);

            if ( nextEvent.wasCancelled() )
                cancelledEvent = nextEvent;
        }

        return cancelledEvent;
    }
}
