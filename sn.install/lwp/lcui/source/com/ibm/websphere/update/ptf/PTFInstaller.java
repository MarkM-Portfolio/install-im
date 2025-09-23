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
 * PTF Installer
 *
 * History 1.6, 8/6/04
 *
 * 16-Dec-2002 Copied from 'efixInstaller'.
 */

import com.ibm.lconn.update.util.LCUtil;
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
import com.ibm.websphere.product.xml.product.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import com.ibm.websphere.update.ptf.prereq.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class PTFInstaller extends PTFBaseInstaller {
    // Program versioning ...

	public static final String pgmVersion = "1.6" ;
    // Program versioning ...

	public static final String pgmUpdate = "8/6/04" ;

    // Instantor ...
    //
    // public PTFInstaller(WASProduct, WASHistory, Notifier, IOService);

    // Create an ptf installer.  The product and history objects provide
    // the context for installation activity.  The notifier is used to
    // provide live notification of installation activity.  The IO
    // services and update refector are needed for component installation.

    public PTFInstaller(WPProduct wpsProduct, WPHistory wpsHistory, Notifier notifier, IOService ioService) {
        super(wpsProduct, wpsHistory, notifier, ioService);

        setImage(null);
    }

    // Active image handling ...
    //
    //    void setImage(ptfImage);
    //    ptfImage getImage();
    //    String getUpdateId();
    //    String getUpdateJarName();
    //    boolean retrievePTF();
    //    ptf getPTF();

    // Set an active ptf image.  This means that the
    // action is invalid.  If necessary, close the ptf log.

    // Set during an installation, cleared after installation.
    // Means that an ptf installer can only install one ptf
    // at a time.

    protected PTFImage image;
    protected ptfDriver activePTFDriver;

    private boolean bSelectedUpdate = false;

    /**
	 * @param image  the image to set
	 * @uml.property  name="image"
	 */
    protected void setImage(PTFImage image) {
        this.image = image;
    }

    /**
	 * @return  the image
	 * @uml.property  name="image"
	 */
    protected PTFImage getImage() {
        return image;
    }

    // Answer the ID of the active ptf image.

    protected String getUpdateId() {
        return getImage().getPTFId();
    }

    // Answer the jar of the active ptf image.

    protected String getUpdateJarName() {
        return getImage().getJarName();
    }

    protected boolean retrievePTFDriver() {
        logFlush("Retrieving ptf information ...");

        activePTFDriver = getImage().getPTFDriver();

        log("Retrieving PTF information ... done");
        logFlush("");

        return (activePTFDriver != null);
    }

    protected ptfDriver getPTFDriver() {
        return activePTFDriver;
    }


    protected boolean checkPUILevel() {
       if ( activePTFDriver != null ) {
          int count = activePTFDriver.getCustomPropertyCount();
          for (int i=0; i<count; i++) {
             customProperty thisProp = activePTFDriver.getCustomProperty( i );
             if (thisProp.getPropertyName().equals("requiredUpdateInstallerLevel")) {
                return checkPUILevel( thisProp, "WUPD0254E" );
             }
          }
          return true;  //If no custom prop thats OK.
       } else {
          return false;  // No Driver, this cannot be OK.
       }
    }

    // Component access ...
    //

    protected Hashtable componentUpdates;

    protected Vector activeUpdateNames;
    protected Hashtable activeUpdateImages;

    /*
    protected componentUpdate getComponentUpdate(String componentName) {
        return (componentUpdate) componentUpdates.get(componentName);
    }
   */
    protected componentUpdate[] getComponentUpdate(String componentName) {
       List updates = (List)componentUpdates.get(componentName);
       if (updates != null ) {
          return (componentUpdate[]) updates.toArray( new componentUpdate[0] );
       } else {
          return null;
       }
    }

    /**
	 * @return  the activeUpdateNames
	 * @uml.property  name="activeUpdateNames"
	 */
    protected Vector getActiveUpdateNames() {
        return activeUpdateNames;
    }

    protected ComponentImage getActiveUpdateImage(String componentName) {
        return (ComponentImage) activeUpdateImages.get(componentName);
    }

    protected boolean prepareComponentUpdates() {
        log("Preparing component update information ...");
        logFlush("");

        boolean failed = false;

        componentUpdates = new Hashtable();

        activeUpdateNames = new Vector();
        activeUpdateImages = new Hashtable();

        ptfDriver usePTFDriver = getPTFDriver();
        PTFImage useImage = getImage();

        LCUtil.setCustomInstallSequence(usePTFDriver);
        //
        Vector errors = new Vector();
        PTFPrereqChecker checker =
               new PTFPrereqChecker( wpsProduct, wpsHistory );

        if ( !checker.testComponentPrereqs(usePTFDriver, errors) )
        {
              // Should display errors here
              int numErrors = errors.size();

              for ( int errorNo = 0; errorNo < numErrors; errorNo++ )
              {
                  String nextError = (String) errors.elementAt(errorNo);

                  System.out.println("[" + errorNo + "]: " + nextError);
              }

              System.out.println();
              // prereqSatisfied = false;
              // return;    result of -1 would be nothing to update.
         }
           //

        int numUpdates = usePTFDriver.getComponentUpdateCount();


        for (int updateNo = 0; !failed && (updateNo < numUpdates); updateNo++) {
            componentUpdate nextUpdate = usePTFDriver.getComponentUpdate(updateNo);
            String nextUpdateName = nextUpdate.getComponentName();

            List complist = (List)componentUpdates.get(nextUpdateName);
            if (complist == null) {
               complist = new LinkedList();
               componentUpdates.put(nextUpdateName, complist );
            }
            complist.add( nextUpdate );

            //componentUpdates.put(nextUpdateName, nextUpdate);

            log("PTFInstaller: Adding component update (" + updateNo + ") " + nextUpdate.getComponentName() + 
                  " with primary content " + nextUpdate.getPrimaryContent() );



            if (isSelected(nextUpdateName)) {
                ComponentImage nextComponentImage = useImage.getComponentImage(nextUpdateName);

                if (nextComponentImage == null) {
                    addException("WUPD0213E", new String[] { nextUpdateName }, null);
                    failed = true;

                } else {
                   if ( !activeUpdateNames.contains( nextUpdateName ) ) {
                      activeUpdateNames.addElement(nextUpdateName);
                   }
                    complist = (List)activeUpdateImages.get(nextUpdateName);
                    if (complist == null) {
                       complist= new LinkedList();
                       activeUpdateImages.put(nextUpdateName, complist );
                    }
                    complist.add( nextUpdate );
                    //activeUpdateImages.put(nextUpdateName, nextComponentImage);
                }
            }
        }

        reorderActiveUpdates();

        if (!failed) {
            log("Component Statistics:");
            logDashes();
            logPrefix("Potential Updates: ", Integer.toString(componentUpdates.size()));
            logPrefix("Actual Updates   : ", Integer.toString(activeUpdateNames.size()));

            logDashes();
            log("");
            log("Preparing component information ... done");

        } else {
            log("Preparing component information ... failed");
        }

        logFlush("");

        return !failed;
    }

	protected void reorderActiveUpdates()
    {
        Vector selectedComponents = getSelectedComponents();

        final HashMap useSelectionMap = new HashMap();
        int numSelected = selectedComponents.size();

        for ( int compNo = 0; compNo < numSelected; compNo++ ) {
            Object nextSelection = selectedComponents.elementAt(compNo);

            String nextName;
            if ( nextSelection instanceof String ) {
                nextName = (String) nextSelection;
            } else {
                nextName = ((ExtendedComponent) nextSelection).getBaseComponent().getName();
            }

            useSelectionMap.put(nextName, new Integer(compNo));
        }

        Comparator selectionCmp = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                String name1 = (String) obj1;
                String name2 = (String) obj2;

                Integer offset1 = (Integer) useSelectionMap.get(name1);
                Integer offset2 = (Integer) useSelectionMap.get(name2);

                if ( offset1 == null )
                    return -1;
                else if ( offset2 == null )
                    return +1;

                int actualOffset1 = offset1.intValue();
                int actualOffset2 = offset2.intValue();

                if ( actualOffset1 < actualOffset2 )
                    return -1;
                else if ( actualOffset1 == actualOffset2 )
                    return  0;
                else
                    return +1;
            }
        };
        
        Object[] useActiveNames = activeUpdateNames.toArray();

        Arrays.sort(useActiveNames, selectionCmp);

        activeUpdateNames = new Vector();

        for ( int compNo = 0; compNo < useActiveNames.length; compNo++ )
            activeUpdateNames.addElement(useActiveNames[compNo]);
    }

    // Action access ...
    //
    //    enumAction getUpdateAction();
    //    enumAction getComponentUpdateAction();

    // Answer the action for the active component.
    // Whereas ptfes may be installed, and selectively installed,
    // components may only be installed.

    // Answer the action for the current ptf.

    protected enumUpdateAction getUpdateAction() {
        if (isSelective())
            return enumUpdateAction.SELECTIVE_INSTALL_UPDATE_ACTION;
        else
            return enumUpdateAction.INSTALL_UPDATE_ACTION;
    }

    protected enumUpdateAction getComponentUpdateAction() {
        return enumUpdateAction.INSTALL_UPDATE_ACTION;
    }

    // Installation ...
    //
    //    public updateEvent install(PTFImage);
    //    public updateEvent install(PTFImage, Vector, boolean);
    //    updateEvent baseInstall();

    // Standard installation: No selected components, and

    public updateEvent install(PTFImage image) {
        return install(image, null, false, DO_NOT_IGNORE_ERRORS);
    }

    // Perform installation of the specified image.
    //
    // The status of the installation must be checked by
    // testing the returned update event.

    public updateEvent install(PTFImage image, Vector selectedComponents, boolean bSelectedUpdate, boolean ignoreErrors) {
        updateEvent event;

        this.bSelectedUpdate = bSelectedUpdate;
        
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

    protected updateEvent baseInstall() {
        nextTaskInGroup(getPreparePTFBanner());
        logFlush("");

        updateEvent event = createUpdateEvent();

        if (!openLog(event)) {
            completeEvent(event, WAS_NOT_CANCELLED);
            return event;
        }

        writeStartingLog(event);

        boolean wasCancelled = false;
        if ( retrievePTFDriver() ) {
           if (!checkPUILevel()) {
              failEvent( event );
              return event;
           }
           if ( prepareComponentUpdates() && saveEvents(CREATE_BACKUP) ) {
               wasCancelled = installComponentUpdates(event);
           } else {
               wasCancelled = false;
           }

        }

        nextTaskInGroup(getCompletePTFBanner());

		logFlush("");

        completeEvent(event, wasCancelled);

        if (!saveEvents(OMIT_BACKUP))
            failEvent(event);

		//Display config tasks if install completed
		if ( !wasCancelled  && !event.failed() ) {
		   ptfDriver ptfDriver = getPTFDriver();
		   int configCount = ptfDriver.getConfigTaskCount();

		   String configHeader = getString( "install.configtask.header.banner" );
		   String configRequired = getString( "install.configtask.required" );
		   String configOptional = getString( "install.configtask.optional" );
		   boolean firstConfigFound = false;

		   if ( configCount != 0 ) {
			  for ( int i=0; i<configCount; i++) {
				 configTask thisTask = (configTask)ptfDriver.getConfigTask( i );
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

    protected String getCancelledMessage() {
        return getString("result.cancelled.install");
    }

    protected String getSucceededMessage() {
        return getString("result.succeeded.install");
    }

    // Component installation ...
    //
    //    boolean installComponentUpdates(updateEvent);
    //    updateEvent installComponentUpdate(updateEvent, componentUpdate);
    //
    //    boolean placeApplied(updateEvent, componentUpdate);
    //    componentApplied createApplied(updateEvent, componentUpdate);
    //
    //    boolean runComponentInstall(updateEvent, componentUpdate);

    protected boolean installComponentUpdates(updateEvent parentEvent) {
        log("Installing Updates ...");
        logFlush("");

        boolean failed = false;
        boolean useIgnoreErrors = getIgnoreErrors();

        boolean wasCancelled = false;

        Vector useUpdateNames = getActiveUpdateNames();
        useUpdateNames = LCUtil.updateInstallSequence(useUpdateNames);
        int updateCount = useUpdateNames.size();

        for (int updateNo = 0;
             (!failed || useIgnoreErrors) && !wasCancelled && (updateNo < updateCount);
             updateNo++) {

            String nextUpdateName = (String) useUpdateNames.elementAt(updateNo);
            LCUtil.setInstallingFeature(nextUpdateName);
            log("PTFInstaller:installComponents: Component : " + nextUpdateName );

            if (updateNo != 0)
                log("");

            nextTaskInGroup(getInstallingPTFBanner(nextUpdateName));

            //componentUpdate childUpdate = getComponentUpdate(nextUpdateName);
            componentUpdate[] compUpdates = getComponentUpdate(nextUpdateName);
            log("PTFInstaller:installComponents: Component has " + compUpdates.length + " updates." );
            for ( int updateNum = 0; updateNum < compUpdates.length; updateNum++ ) {
               componentUpdate childUpdate = compUpdates[ updateNum ];
               log("PTFInstaller:installComponents: Installing component update (" + updateNum + ") " + childUpdate.getComponentName() + 
                     " with primary content " + childUpdate.getPrimaryContent() );

               updateEvent childEvent = installComponentUpdate(parentEvent, childUpdate);
               enumEventResult childResult = childEvent.getResultAsEnum();

               if (childResult == enumEventResult.FAILED_EVENT_RESULT) {
                   // Use the presence of the exception to fail the installation.
                   addException("WUPD0218E", new String[] { nextUpdateName }, null);
                   failed = true;

               } else if (childResult == enumEventResult.CANCELLED_EVENT_RESULT) {
                   wasCancelled = true;
               }
            }
            //Saiyu begins
            if (failed)
                log("Installing updates ... failed");
            else if (wasCancelled)
                log("Installing updates ... cancelled");
            else {
                log("Installing updates ... ok");
                writeUpdateProductFile(nextUpdateName);
            }            
            //Saiyu ends
        }
/*
        if (failed)
            log("Installing updates ... failed");
        else if (wasCancelled)
            log("Installing updates ... cancelled");
        else {
            log("Installing updates ... ok");
            writeUpdateProductFile();
        }
*/
        logFlush("");

        return wasCancelled;
    }

    protected updateEvent installComponentUpdate(updateEvent parentEvent, componentUpdate childUpdate) {
        updateEvent childEvent = createComponentEvent(parentEvent, childUpdate);
		boolean extractionFailed = false;


        writeStartingComponentLog(childEvent);

        boolean wasCancelled = wasCancelled();

        String useComponentName = childUpdate.getComponentName();
        Object useSelection = selectionMap.get(useComponentName);

        ExtendedComponent ec;

        if (useSelection instanceof ExtendedComponent) {
            log("Located extended component: " + useComponentName);
            ec = (ExtendedComponent) useSelection;
        } else {
            log("Located simple component: " + useComponentName);
            ec = null;
        }

        if (!wasCancelled) {
            if (saveEvents(OMIT_BACKUP)) {

                List extractedFiles = prepareComponentInstall(childEvent, childUpdate);
                if (extractedFiles != null && extractedFiles.size() > 0) {
                    if (childUpdate.getComponentName().equals("external.mq")) {
                        wasCancelled = runMQComponentInstall(childEvent, childUpdate, extractedFiles, ec);
                    } else if (childUpdate.getComponentName().equals("external.wemps") || childUpdate.getComponentName().equals("external.ihs")) {
                        wasCancelled = runExternalComponentInstall(childEvent, childUpdate, extractedFiles, ec);
                    } else {
                        wasCancelled = runComponentInstall(childEvent, childUpdate, extractedFiles);
                    }
                } else {
                	extractionFailed = true;	
                }
            }
        }

        completeEvent(childEvent, wasCancelled);

        if (!saveEvents(OMIT_BACKUP) || extractionFailed)
            failEvent(childEvent);

        // Don't place the applied unless the installation was successful.

        if (doHistory && childEvent.succeeded())
            placeApplied(childEvent, childUpdate);

        writeEndingComponentLog(childEvent);

        return childEvent;
    }

    protected void writeUpdateProductFile() {

        WPProduct useProduct = getWPProduct();
        WPHistory useHistory = getWPHistory();

        ptfDriver usePTFDriver = getPTFDriver();

        log("Updating product file...");
        logFlush("");

        boolean result = true;

        if (doHistory)
        {
            List componentAppliedList = new ArrayList();
            createInitialProductVersion(componentAppliedList);
    	    result = useHistory.recordPTFApplication(getUpdateId(), componentAppliedList);
        }
        
        boolean didSaveProductFile = false;
        if (result) {
            log("Recording initial product information... done");
            List productList = new ArrayList();
            createProductFile(usePTFDriver, productList);
            didSaveProductFile = useProduct.addProduct(productList);

            if (!didSaveProductFile) {
                log("Updating product file... failed");
            } else {
                log("Updating product file... done");
            }
        } else {
            log("Recording initial product information... failed");
        }
    }

    protected void writeUpdateProductFile(String productName) { //Saiyu

        WPProduct useProduct = getWPProduct();
        WPHistory useHistory = getWPHistory();

        ptfDriver usePTFDriver = getPTFDriver();

        log("Updating product file...");
        logFlush("");

        boolean result = true;

        if (doHistory)
        {
            //Saiyu List componentAppliedList = new ArrayList();
            //Saiyu createInitialProductVersion(componentAppliedList);
        	//Saiyu result = useHistory.recordPTFApplication(getUpdateId(), componentAppliedList);
        	
        	componentApplied newApplied = createInitialProductVersion(productName); //Saiyu
            result = useHistory.recordPTFApplication(getUpdateId(), newApplied); //Saiyu
        }
        boolean didSaveProductFile = false;
        if (result) {
            log("Recording initial product information... done");
            //Saiyu List productList = new ArrayList();
            //Saiyu createProductFile(usePTFDriver, productList);
            //Saiyu didSaveProductFile = useProduct.addProduct(productList);
            
            product newProduct = createProductFile(usePTFDriver, productName); //Saiyu
            didSaveProductFile = useProduct.addProduct(newProduct); //Saiyu

            if (!didSaveProductFile) {
                log("Updating product file... failed");
            } else {
                log("Updating product file... done");
            }
        } else {
            log("Recording initial product information... failed");
        }
    }

    // Put in an ptf applied for the active component and ptf;
    // Store the update-id, component-name, and starting time-stamp
    // in the applied.  Store the log and backup name for the
    // component update as well.

    protected boolean placeApplied(updateEvent childEvent, componentUpdate childUpdate) {
        logFlush("Recording ptf application ...");

        // Add the driver only when an application is to be added.
        //
        // This is a difficult constraint to handle;
        //
        // Want to avoid having any ptf files if the driver cannot
        // be added.
        //
        // But, want to have a driver file if any applications are
        // present.

        if (!updateIsPresent()) {
            logFlush("PTF is not yet present; adding.");

            if (!placePTFDriver())
                return false;
        }

        /***** record / save ptfApplied file *****/

        if(bSelectedUpdate)
        {
        	childUpdate.setSelectiveUpdate("true");
        }
        componentApplied newApplied = createApplied(childEvent, childUpdate);

        WASHistory useHistory = getWPHistory();
        boolean result = true;

        if (doHistory)
            result = useHistory.recordPTFApplication(getUpdateId(), newApplied);

        if (!result) {
            Iterator historyExceptions = useHistory.getExceptions();

            while (historyExceptions.hasNext()) {
                WASHistoryException nextException = (WASHistoryException) historyExceptions.next();

                addException("WUPD0219E", new String[] { getUpdateId(), childUpdate.getComponentName()}, nextException);
            }

            log("Recording PTF application ... failed");

        } else {
            log("Recording PTF application ... done");
        }

        logFlush("");

        /***** record / save external.component files if applicable *****/

        //indicates that this is an external component
        if (childUpdate.getComponentName().startsWith("external.")) {
            ExtendedComponent ec = (ExtendedComponent) selectionMap.get(childUpdate.getComponentName());
            component newComponent = ec.getBaseComponent();
            if (!placeExternalComponent(newComponent)) {
                result = false;
                log("Recording external component application ... failed");
            } else {
                log("Recording external component application ... done");
            }
        }

        logFlush("");

        return result;
    }

    protected boolean placeExternalComponent(component newComponent) {

        WPProduct useProduct = getWPProduct();

        boolean didSave = useProduct.addComponent(newComponent);

        if (didSave) {
            return true;
        }

        return false;

    }

    protected componentApplied createApplied(updateEvent childEvent, componentUpdate childUpdate) {
        componentApplied newApplied = new componentApplied();

        newApplied.setComponentName(childUpdate.getComponentName());
        newApplied.setSelectiveUpdate(childUpdate.getSelectiveUpdate());
        newApplied.setUpdateType(childUpdate.getUpdateType());
        newApplied.setIsRequired(childUpdate.getIsRequired());
        newApplied.setIsOptional(childUpdate.getIsOptional());

        newApplied.setIsExternal(childUpdate.getIsExternal());
        newApplied.setRootPropertyFile(childUpdate.getRootPropertyFile());
        newApplied.setRootPropertyName(childUpdate.getRootPropertyName());
        newApplied.setRootPropertyValue(childUpdate.getRootPropertyValue());

        // TBD: The root property value should have been prepared earlier,
        // TBD: and the actual value placed in the applied.

        newApplied.setIsCustom(childUpdate.getIsCustom());

        newApplied.setTimeStamp(childEvent.getStartTimeStamp());
        newApplied.setLogName(childEvent.getLogName());
        newApplied.setBackupName(childEvent.getBackupName());

        //indicates that this is an external component
        if (selectionMap.get(childUpdate.getComponentName()) instanceof ExtendedComponent) {
            ExtendedComponent ec = (ExtendedComponent) selectionMap.get(childUpdate.getComponentName());
            component newComponent = ec.getBaseComponent();
            newApplied.setInitialVersion(ec.getPriorVersion());

            componentVersion finalVersion = new componentVersion();
            finalVersion.setBuildDate(newComponent.getBuildDate());
            finalVersion.setBuildVersion(newComponent.getBuildVersion());
            finalVersion.setComponentName(newComponent.getName());
            finalVersion.setSpecVersion(newComponent.getSpecVersion());

            newApplied.setFinalVersion(finalVersion);
        }

        return newApplied;
    }

    protected void createInitialProductVersion(List componentAppliedList) {

        int productUpdateIndex = getPTFDriver().getProductUpdateCount();
        product currentProduct = null;
        boolean productUpdateFound = false;
        
       /** Must handle this special case for Bobcat (embeddedExpress and Express) 
         *  and IHS stand-alone updates
         */
        
        if(productUpdateIndex > 0)
        {
                        
            String testProduct;
            String productBaseName;
            File productFile;

        	for(int p=0; p<productUpdateIndex; p++)
            {
                testProduct = getPTFDriver().getProductUpdate(p).getProductId();
                currentProduct = getCurrentProduct(testProduct);
              
                //if embeddedExpress/Express or IHS is found
                if (currentProduct != null)
                {
                    productBaseName = testProduct + ".product";
                    productFile = new File(wpsProduct.getVersionFileName(productBaseName));

                    // if the product exists on the system being updated
                    if ( productFile.exists() )
                    {
                        componentApplied newApplied = new componentApplied();

                        log("Product file to save information for is ... " + productFile.getPath());
                        newApplied.setComponentName(currentProduct.getName());
                        newApplied.setUpdateType("patch");

                        newApplied.setTimeStamp("-");
                        newApplied.setLogName("-");
                        newApplied.setBackupName("-");

                        componentVersion initVersion = new componentVersion();
                        initVersion.setBuildDate(currentProduct.getBuildInfo().getDate());
                        initVersion.setBuildVersion(currentProduct.getVersion());
                        initVersion.setSpecVersion(currentProduct.getBuildInfo().getLevel());
                        initVersion.setComponentName(currentProduct.getId());

                        newApplied.setInitialVersion(initVersion);
                        componentAppliedList.add(newApplied);
                    }
                }
            }
        }
    }
    
    //Saiyu begins
    protected componentApplied createInitialProductVersion(String productName) { //Saiyu

    	componentApplied newApplied = null;
        int productUpdateIndex = getPTFDriver().getProductUpdateCount();
        product currentProduct = null;
        boolean productUpdateFound = false;
        
       /** Must handle this special case for Bobcat (embeddedExpress and Express) 
         *  and IHS stand-alone updates
         */
        
        if(productUpdateIndex > 0)
        {
                        
            String testProduct;
            String productBaseName;
            File productFile;

        	for(int p=0; p<productUpdateIndex; p++)
            {
                testProduct = getPTFDriver().getProductUpdate(p).getProductId();
                currentProduct = getCurrentProduct(testProduct);

                //Saiyu begins
                if (!testProduct.equals(productName))
                	continue;
                //Saiyu ends
                
                //if embeddedExpress/Express or IHS is found
                if (currentProduct != null)
                {
                    productBaseName = testProduct + ".product";
                    productFile = new File(wpsProduct.getVersionFileName(productBaseName));

                    // if the product exists on the system being updated
                    if ( productFile.exists() )
                    {
                        newApplied = new componentApplied();

                        log("Product file to save information for is ... " + productFile.getPath());
                        newApplied.setComponentName(currentProduct.getName());
                        newApplied.setUpdateType("patch");

                        newApplied.setTimeStamp("-");
                        newApplied.setLogName("-");
                        newApplied.setBackupName("-");

                        componentVersion initVersion = new componentVersion();
                        initVersion.setBuildDate(currentProduct.getBuildInfo().getDate());
                        initVersion.setBuildVersion(currentProduct.getVersion());
                        initVersion.setSpecVersion(currentProduct.getBuildInfo().getLevel());
                        initVersion.setComponentName(currentProduct.getId());

                        newApplied.setInitialVersion(initVersion);
                        //componentAppliedList.add(newApplied);
                        break;
                    }
                }
            }
        }
        
        return newApplied; //Saiyu
    }
    //Saiyu ends

    // Perform installation of the component.

    // Answer true or false, telling if the installation was cancelled.

    // Change Note: Changed return data structure to a list
    // this will allow multiple files to be extracted to the backup dir
    // for mq updates

    protected List prepareComponentInstall(updateEvent childEvent, componentUpdate childUpdate) {
        String componentName = childUpdate.getComponentName();

        logFlush("Preparing for component update ( " + componentName + " ).");

        if (!initializeLogAndBackup())
            return null;


        List extractedFiles = new ArrayList();

        PTFImage targetImage = getImage();
        ComponentImage childImage = targetImage.getComponentImage(componentName);

        // Build jar from componentUpdate.  UpdateImages doesn't handle multiple component-update for came comp very well.
        //List jarEntryNames = childImage.getPrimaryContentEntryName();
        List jarEntryNames = new ArrayList(1);
        jarEntryNames.add( childImage.getComponentEntryName() + "/" + childUpdate.getPrimaryContent() );

        int numJarEntryNames = jarEntryNames.size();


        try {
            for (int i = 0; i < numJarEntryNames; i++) {
                String aJarEntryName = (String) jarEntryNames.get(i);

                Set dirRoots = ioService.getSubDirRootNames(targetImage.getJarName(), aJarEntryName);
                if (dirRoots != null) {
                    int numDirRoots = dirRoots.size();
                    if (numDirRoots > 0) {
                        Iterator dirRootIter = dirRoots.iterator();
                        while (dirRootIter.hasNext()) {
                            String entryNameRoot = (String) dirRootIter.next();
                            Vector entries = ioService.getChildEntryNames(targetImage.getJarName(), entryNameRoot, IOService.ONLY_CHILD_FILES);

                            int numEntries = entries.size();
                            for (int j = 0; j < numEntries; j++) {
                                String aChildEntryName = (String) entries.elementAt(j);
                                String fileToExtract = entryNameRoot + File.separator + aChildEntryName;

                                String anExtractedFile =
                                    ioService.extractJarEntry(targetImage.getJarName(), fileToExtract, wpsProduct.getTmpDirName() + File.separator + componentName);

                                extractedFiles.add(anExtractedFile);
                                logFlush("Extracted file ( " + anExtractedFile + " )");

                            }
                        }
                    } else {

                        String anExtractedFile = ioService.extractJarEntry(targetImage.getJarName(), aJarEntryName, wpsProduct.getTmpDirName());

                        extractedFiles.add(anExtractedFile);
                        logFlush("Extracted file ( " + anExtractedFile + " )");

                    }
                }
            }

            // 'extractJarEntry' throws IOServicesException, IOException

            // logFlush("Extracted file ( " + extractedFile + " )");

        } catch (Exception ex) {
            //ex.printStackTrace();
            logFlush("Failed to extract entries to file.");

            String extractedFilesAsString = "";

            int numExtractedFiles = extractedFiles.size();
            if (numExtractedFiles > 0) {
                for (int i = 0; i < numExtractedFiles; i++) {
                    if (i == numExtractedFiles - 1)
                        extractedFilesAsString += (String) extractedFiles.get(i);
                    else
                        extractedFilesAsString += (String) extractedFiles.get(i) + ", ";
                }

                String[] exceptionArgs = new String[] { getUpdateId(), componentName, targetImage.getJarName(), extractedFilesAsString };

                addException("WUPD0245E", exceptionArgs, ex);
            }
        }


        return extractedFiles;
    }

    protected boolean runComponentInstall(updateEvent childEvent, componentUpdate childUpdate, List extractedFiles) {
        String componentName = childUpdate.getComponentName();

        log("Running component update ( " + componentName + " ).");

        Extractor extractor = new Extractor();
        extractor.setQuiet(true);

        try {
            log("Processing extractor command line arguments.");
            logFlush("");

            if ( extractedFiles == null )
                logFlush("Null extracted files.");

            if (extractedFiles.size() == 0)
                logFlush("Zero extracted files.");

            if (extractedFiles.size() > 0) {
		String useLogLevel = Integer.toString( getLogLevel() );

                log("Jar File  : ", (String) extractedFiles.get(0));
                log("Log File  : ", childEvent.getLogName());
                log("Target Dir: ", getWPProduct().getProductDirName());
                log("Backup Jar: ", childEvent.getBackupName());
                log("Tmp Dir   : ", getWPProduct().getTmpDirName());
		logFlush("Log Level : ", useLogLevel);

                String args[] = {
                        "-JarInputFile", (String) extractedFiles.get(0),
			"-LogFile",      childEvent.getLogName(),
                        "-TargetDir",    getWPProduct().getProductDirName(),
                        "-BackupJar",    childEvent.getBackupName(),
                        "-TmpDir",       getWPProduct().getTmpDirName(),
                        "-SkipVer",                     // skip the obsolete PTF processing
                        "-Overwrite",                   // allow backup to be overwritten
			"-Verbosity",    useLogLevel    // 0 .. 5  (least .. most)
                };

                if (!doBackup) {
                    String[] newArgs = new String[args.length + 2];
                    System.arraycopy(args, 0, newArgs, 0, args.length);
                    newArgs[args.length] = "-NoBackupJar";
                    newArgs[args.length + 1] = "ifExists*" + getWPProduct().getProductDirName();
                    args = new String[newArgs.length];
                    System.arraycopy(newArgs, 0, args, 0, newArgs.length);
                }

                if (!extractor.processCmdLineArgs(args)) {
                    log("Failed while processing extractor command line arguments.");
                    logFlush("");

                    addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                } else {
                    log("Completed processing extractor command line arguments.");
                    log("Performing extraction.");
                    logFlush("");

                    if (!extractor.process()) { // throws Exception ?
                        log("Failed to perform extraction.");
                        logFlush("");

                        addException("WUPD0248E", new String[] { getUpdateId(), componentName, childEvent.getLogName()}, null);
                    } else {
                        log("Performed extraction.");
                        logFlush("");
                    }
                }
            } else {
                log("Failed to extract update contents for component (" + componentName + ")");
                logFlush("");
                addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
            }

        } catch (Exception ex) {
            log("Exception while performing update.");
            logFlush("");

            addException("WUPD0247E", new String[] { getUpdateId(), componentName }, ex);
            ex.printStackTrace(System.out);
        }

        // TBD: Need to remove the temporary update file!
        // TBD: This won't simply work if we attempt to simply
        // TBD: delete file, as that doesn't work.  The file is
        // TBD: being left open by the extractor, or by java,
        // TBD: and cannot be deleted.

        return false;
    }

    protected boolean runMQComponentInstall(updateEvent childEvent, componentUpdate childUpdate, List extractedFiles, ExtendedComponent ec) {
        String componentName = childUpdate.getComponentName();

        log("Running component update ( " + componentName + " ).");

        PTFImage targetImage = getImage();

        RunExec runExec = new RunExec();
        boolean permissionSet = false;

        try {
            log("Processing MQ CSD Install Script");

            String csdInstallRoot =
                getWPProduct().getTmpDirName()
                    + File.separator
                    + componentName
                    + File.separator
                    + "ptfs"
                    + File.separator
                    + targetImage.getPTFId()
                    + File.separator
                    + "components"
                    + File.separator
                    + componentName;
            String csdRepository = csdInstallRoot + File.separator + "CSD";
            String setupRepository = csdInstallRoot + File.separator + "Setup";

            /** Windows script **/
            String wmService_win = setupRepository + File.separator + "wmservice.exe";

            /** Unix script **/
            String wmService_unix = csdRepository + File.separator + "wmservice";

            /** SunOS script **/
            String amqiclen_SunOS = csdRepository + File.separator + "amqiclen.SunOS";
            /** AIX script **/
            String amqiclen_Aix = csdRepository + File.separator + "amqiclen.AIX";
            /** Linux script **/
            String amqiclen_Linux = csdRepository + File.separator + "amqiclen.Linux";

            String csdRepository_unix = csdRepository + File.separator + "images";

            /** common log files **/
            //String mqPTFLog = csdInstallRoot + File.separator + "mqPtf.log";
            String mqPTFLog = childEvent.getLogName();
            String mqVerLog = csdInstallRoot + File.separator + "mqVer.properties";

            if (OSUtil.isSolaris()) {
                if (setScriptPermissions(wmService_unix, "755") && setScriptPermissions(amqiclen_SunOS, "755")) {
                    permissionSet = true;
                } else {
                    log("Failed to set script permissions");
                    logFlush("");
                    addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);

                }
            } else if (OSUtil.isAIX()) {
                if (setScriptPermissions(wmService_unix, "755") && setScriptPermissions(amqiclen_Aix, "755")) {
                    permissionSet = true;
                } else {
                    log("Failed to set script permissions");
                    logFlush("");
                    addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);

                }
            } else if (OSUtil.isLinux()) {
                if (setScriptPermissions(wmService_unix, "755") && setScriptPermissions(amqiclen_Linux, "755")) {
                    permissionSet = true;
                } else {
                    log("Failed to set script permissions");
                    logFlush("");
                    addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);

                }
            }

            if (OSUtil.isWindows()) {

                if (!runExec.execWait("\"" + wmService_win + "\" version \"" + mqPTFLog + "\" " + "\"" + csdRepository + "\" " + "\"" + mqVerLog + "\"")) {
                    log("Failed while processing MQ CSD version.");
                    logFlush("");
                    addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                } else {
                    log("Completed processing MQ CSD version information.");
                    log("Performing CSD install.");
                    logFlush("");

                    Properties versionInfo = parseVersionFile(mqVerLog);
                    String availableCSD = versionInfo.getProperty("MQCSDAvailable");
                    log("available CSD level: " + availableCSD);

                    String appliedCSD = versionInfo.getProperty("MQCSDApplied");
                    log("current applied CSD level: " + appliedCSD);
                    logFlush("");

                    //currently, if the csd available does not equal the applied level, 
                    //treat the available level as applicable
                    //else bypass

                    if ((availableCSD != null && appliedCSD != null) && (!availableCSD.equals(appliedCSD))) {
                        log("Executing Command--> " + "\""+wmService_win + "\" install \"" + mqPTFLog + "\" " + "\"" + csdRepository + "\" " + availableCSD);
                        if (!runExec.execWait("\""+wmService_win + "\" install \"" + mqPTFLog + "\" " + "\"" + csdRepository + "\" " + availableCSD)) {
                            // throws Exception ?
                            log("Failed to perform install.");
                            logFlush("");
                            addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                        } else {
                            log("Performed MQ CSD install.");
                            logFlush("");

                            //store the new version info into the backup directory
                            //this is necessary
                            log("Creating backup information.");
                            logFlush("");

                            String mqVerBackupLog =
                                getWPProduct().getBackupDirName()
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "ptfs"
                                    + File.separator
                                    + targetImage.getPTFId()
                                    + File.separator
                                    + "components"
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "mqVer.properties";

                            String mqPTFBackupLog =
                                getWPProduct().getBackupDirName()
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "ptfs"
                                    + File.separator
                                    + targetImage.getPTFId()
                                    + File.separator
                                    + "components"
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "mqPTF.log";

                            if (!runExec.execWait("\"" + wmService_win + "\" version \"" + mqPTFBackupLog + "\" " + "\"" + csdRepository + "\" " + "\"" + mqVerBackupLog + "\"")) {
                                log("Created backup information...failed");
                                logFlush("");
                                addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                            } else {
                                log("Created backup information...done");
                                logFlush("");
                            }

                        }
                    } else {
                        boolean doAddException = false;

                        log("Version information did not pass prerequisites");
                        if (availableCSD == null || appliedCSD == null) {
                            if (availableCSD == null)
                                doAddException = true;
                            log("--- CSD version information was incomplete");
                            log("------- MQCSDAvailable=" + availableCSD);
                            log("------- MQCSDApplied=" + appliedCSD);
                        }

                        if (availableCSD.equals(appliedCSD)) {
                            log("--- CSD available level already matches the applied level");
                            log("------- MQCSDAvailable=" + availableCSD);
                            log("------- MQCSDApplied=" + appliedCSD);
                        }

                        log("Bypassed the CSD installation");
                        logFlush("");

                        if (doAddException)
                            addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                    }
                }
            } else if ((OSUtil.isAIX() || OSUtil.isLinux() || OSUtil.isSolaris()) && permissionSet) {

                if (!runExec.execWait(wmService_unix + " version " + mqPTFLog + " " + csdRepository_unix + " " + mqVerLog)) {
                    log("Failed while processing MQ CSD version.");
                    logFlush("");
                    addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                } else {
                    log("Completed processing MQ CSD version information.");
                    log("Performing CSD install.");
                    logFlush("");

                    Properties versionInfo = parseVersionFile(mqVerLog);
                    String availableCSD = versionInfo.getProperty("MQCSDAvailable");
                    log("available CSD level: " + availableCSD);

                    String appliedCSD = versionInfo.getProperty("MQCSDApplied");
                    log("current applied CSD level: " + appliedCSD);
                    logFlush("");

                    //currently, if the csd available does not equal the applied level, 
                    //treat the available level as applicable
                    //else bypass

                    if ((availableCSD != null && appliedCSD != null) && (!availableCSD.equals(appliedCSD))) {
                        log("Executing Command--> " + wmService_unix + " install " + mqPTFLog  + " " + csdRepository_unix + " " + availableCSD);
                        if (!runExec.execWait(wmService_unix + " install " + mqPTFLog + " " + csdRepository_unix + " " + availableCSD)) {
                            // throws Exception ?
                            log("Failed to perform install.");
                            logFlush("");
                            addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                        } else {
                            log("Performed MQ CSD install.");
                            logFlush("");

                            //store the new version info into the backup directory
                            //this is necessary
                            log("Creating backup information.");
                            logFlush("");

                            String mqVerBackupLog =
                                getWPProduct().getBackupDirName()
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "ptfs"
                                    + File.separator
                                    + targetImage.getPTFId()
                                    + File.separator
                                    + "components"
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "mqVer.properties";

                            String mqPTFBackupLog =
                                getWPProduct().getBackupDirName()
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "ptfs"
                                    + File.separator
                                    + targetImage.getPTFId()
                                    + File.separator
                                    + "components"
                                    + File.separator
                                    + componentName
                                    + File.separator
                                    + "mqPTF.log";

                            if (!runExec.execWait(wmService_unix + " version " + mqPTFBackupLog + " " + csdRepository_unix + " " + mqVerBackupLog)) {
                                log("Created backup information...failed");
                                logFlush("");
                                addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                            } else {
                                log("Created backup information...done");
                                logFlush("");
                            }

                        }
                    } else {
                        boolean doAddException = false;

                        log("Version information did not pass prerequisites");
                        if (availableCSD == null || appliedCSD == null) {
                            if (availableCSD == null)
                                doAddException = true;
                            log("--- CSD version information was incomplete");
                            log("------- MQCSDAvailable=" + availableCSD);
                            log("------- MQCSDApplied=" + appliedCSD);
                        }

                        if (availableCSD.equals(appliedCSD)) {
                            log("--- CSD available level already matches the applied level");
                            log("------- MQCSDAvailable=" + availableCSD);
                            log("------- MQCSDApplied=" + appliedCSD);
                        }

                        log("Bypassed the CSD installation");
                        logFlush("");

                        if (doAddException)
                            addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                    }
                }
            }

        } catch (Exception ex) {
            log("Exception while performing update.");
            logFlush("");

            ex.printStackTrace(System.out);
        }

        return false;
    }

    protected boolean runExternalComponentInstall(updateEvent childEvent, componentUpdate childUpdate, List extractedFiles, ExtendedComponent ec) {
        String componentName = childUpdate.getComponentName();

        log("Running component update ( " + componentName + " )");

        Extractor extractor = new Extractor();
        extractor.setQuiet(true);

        try {
            log("Processing extractor command line arguments.");
            logFlush("");

            if ( extractedFiles == null )
                logFlush("Null extracted files.");

            if (extractedFiles.size() == 0)
                logFlush("Zero extracted files.");

            if (extractedFiles.size() > 0) {
                logFlush("Handling extractor arguments.");

                log("Jar File  : ", (String) extractedFiles.get(0));
                log("Log File  : ", childEvent.getLogName());
                log("Target Dir: ", ec.getLocation());
                log("Backup Jar: ", childEvent.getBackupName());
                log("Tmp Dir   : ", getWPProduct().getTmpDirName());

                String args[] =
                    {
                        "-JarInputFile",
                        (String) extractedFiles.get(0),
                        "-LogFile",
                        childEvent.getLogName(),
                        "-TargetDir",
                        ec.getLocation(),
                        "-BackupJar",
                        childEvent.getBackupName(),
                        "-TmpDir",
                        getWPProduct().getTmpDirName(),
                        "-SkipVer",
                        "-Overwrite",
                    // allow backup to be overwritten
                    "-Verbosity", "4" // 0 .. 5  (least .. most)
                };

                logFlush("Calling extractor to process cmd line ...");

                if (!extractor.processCmdLineArgs(args)) {
                    log("Failed while processing extractor command line arguments.");
                    logFlush("");

                    addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
                } else {
                    log("Completed processing extractor command line arguments.");
                    log("Performing extraction.");
                    logFlush("");

                    if (!extractor.process()) { // throws Exception ?
                        log("Failed to perform extraction.");
                        logFlush("");

                        addException("WUPD0248E", new String[] { getUpdateId(), componentName, childEvent.getLogName()}, null);
                    } else {
                        log("Performed extraction.");
                        logFlush("");
                    }
                }
            } else {
                log("Failed to extract update contents for component (" + componentName + ")");
                logFlush("");
                addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
            }
        } catch (Exception ex) {
            log("Exception while performing update.");
            logFlush("");

            addException("WUPD0247E", new String[] { getUpdateId(), componentName }, ex);

            ex.printStackTrace(System.out);
        }

        // TBD: Need to remove the temporary update file!
        // TBD: This won't simply work if we attempt to simply
        // TBD: delete file, as that doesn't work.  The file is
        // TBD: being left open by the extractor, or by java,
        // TBD: and cannot be deleted.

        return false;
    }

    protected Properties parseVersionFile(String file) throws IOException {
        Properties propsFile = new Properties();
        InputStream fs = new java.io.FileInputStream(file);
        propsFile.load(fs);
        fs.close();

        return propsFile;
    }

    protected void createProductFile(ptfDriver driver, List productList) {

        buildInfo b_info = new buildInfo();

        int numProducts = driver.getProductUpdateCount();

        //boolean foundProduct = false;

        String aProductId;
        String productBaseName;
        File productFile;
        
        logFlush("Getting product file name ...");

        for (int i = 0; /*!foundProduct &&*/ i < numProducts; i++) {
            productUpdate aProductUpdate = (productUpdate) driver.getProductUpdate(i);
            aProductId = aProductUpdate.getProductId();
            // if aProduct from ptfDriver is a valid WPProduct
            if (wpsProduct.getProductById(aProductId) != null) {
                productBaseName = aProductId + ".product";
                productFile = new File(wpsProduct.getVersionFileName(productBaseName));

                // if the product exists on the system being updated
                if ( productFile.exists() ) {
                    product newProduct = new product();
                    newProduct.setId(aProductId);
                    newProduct.setName(aProductUpdate.getProductName());
                    newProduct.setVersion(aProductUpdate.getBuildVersion());
                    b_info.setDate(aProductUpdate.getBuildDate());
                    b_info.setLevel(aProductUpdate.getBuildLevel());
                    newProduct.setBuildInfo(b_info);
                    productList.add(newProduct);
                    //foundProduct = true;
                    log("Product file to update is ... " + productFile.getPath());
                }
            }
        }

        logFlush("");

    }

    //Saiyu begins
    protected product createProductFile(ptfDriver driver, String productName) {

    	product newProduct = null;
        buildInfo b_info = new buildInfo();

        int numProducts = driver.getProductUpdateCount();

        //boolean foundProduct = false;

        String aProductId;
        String productBaseName;
        File productFile;
        
        logFlush("Getting product file name ...");

        for (int i = 0; /*!foundProduct &&*/ i < numProducts; i++) {
            productUpdate aProductUpdate = (productUpdate) driver.getProductUpdate(i);
            aProductId = aProductUpdate.getProductId();
            
            if (!aProductId.equals(productName))
            	continue;
            
            // if aProduct from ptfDriver is a valid WPProduct
            if (wpsProduct.getProductById(aProductId) != null) {
                productBaseName = aProductId + ".product";
                productFile = new File(wpsProduct.getVersionFileName(productBaseName));

                // if the product exists on the system being updated
                if ( productFile.exists() ) {
                    newProduct = new product();
                    newProduct.setId(aProductId);
                    newProduct.setName(aProductUpdate.getProductName());
                    newProduct.setVersion(aProductUpdate.getBuildVersion());
                    b_info.setDate(aProductUpdate.getBuildDate());
                    b_info.setLevel(aProductUpdate.getBuildLevel());
                    newProduct.setBuildInfo(b_info);
                    //productList.add(newProduct);
                    //foundProduct = true;
                    log("Product file to update is ... " + productFile.getPath());
                    break;
                }
            }
        }

        logFlush("");

        return newProduct;
    }
    //Saiyu ends
    
    // Driver manipulation:
    //
    //     boolean placePTFDriver();

    protected boolean placePTFDriver() {
        if (!doHistory)
            return true;

        logFlush("Placing ptf and ptf driver file ...");

        if (updateIsPresent()) {
            log("Placing PTF and PTF driver file ... skipped: already present");
            logFlush("");

            return true;
        }

        WPProduct useProduct = getWPProduct();
        WPHistory useHistory = getWPHistory();

        ptfDriver usePTFDriver = getPTFDriver();
        ptf usePTF = usePTFDriver.cloneAsPTF();

        boolean didSave = useProduct.savePTF(usePTF);

        if (didSave)
            didSave = useHistory.savePTFDriver(usePTFDriver);

        if (!didSave) {
            Iterator productExceptions = useProduct.getExceptions();

            while (productExceptions.hasNext()) {
                WASProductException nextException = (WASProductException) productExceptions.next();

                addException("WUPD0217E", new String[] { getUpdateId(), getUpdateJarName()}, nextException);
            }

            Iterator historyExceptions = useHistory.getExceptions();

            while (historyExceptions.hasNext()) {
                WASHistoryException nextException = (WASHistoryException) historyExceptions.next();

                addException("WUPD0214E", new String[] { getUpdateId(), getUpdateJarName()}, nextException);
            }

            log("Placing PTF and PTF driver file ... failed");

        } else {
            log("Placing PTF and PTF driver file ... done");
        }

        logFlush("");

        return didSave;
    }
}
