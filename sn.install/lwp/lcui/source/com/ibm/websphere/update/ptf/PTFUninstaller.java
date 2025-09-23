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
 * PTF Uninstaller
 *
 * History "1.5", "8/6/04"
 *
 * 09-Jul-2002 Initial Version
 *
 * 03-Nov-2002 Added in quiet mode setting to extractor.
 *
 * 16-Dec-2002 Updated to mesh with PTF installation.
 */

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.product.WASProductException;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.WASHistory;
import com.ibm.websphere.product.history.WASHistoryException;
import com.ibm.websphere.product.history.WPHistory;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.ptf;
import com.ibm.websphere.product.xml.product.buildInfo;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.update.delta.Extractor;
import com.ibm.websphere.update.ioservices.IOService;
import com.ibm.websphere.update.ioservices.Notifier;
import com.ibm.websphere.update.util.WPConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *  
 */
public class PTFUninstaller extends PTFBaseInstaller {
	// Program versioning ...

   public final static String pgmVersion = "1.5" ;
	// Program versioning ...

   public final static String pgmUpdate = "8/6/04" ;

	// Instantor ...
	//
	// public PTFUninstaller(WASProduct, WASHistory, Notifier, IOService);

	// Create an ptf uninstaller.  The product and history objects provide
	// the context for installation activity.  The notifier is used to
	// provide live notification of installation activity.

	public PTFUninstaller(WPProduct wpsProduct, WPHistory wpsHistory, Notifier notifier, IOService ioService) {
		super(wpsProduct, wpsHistory, notifier, ioService);

		setPTFId(null);

    }

	// Active ptf handling ...
	//
	//    void setPTFId(String);
	//    String getUpdateId();
	//    String getUpdateJarName();
	//    ptf getPTF();
	//    boolean readPTF();
	//    ptfDriver getPTFDriver();
	//    boolean readPTFDriver();

	// Set an active ptf image.  This means that the
	// action is invalid.  If necessary, close the ptf log.

	// Set during an installation, cleared after installation.
	// Means that an ptf installer can only install one ptf
	// at a time.

	protected String ptfId;

    protected product initialProduct;   // not using

    protected List initialProducts;     // holds the product object

	protected ptf activePTF;
	protected ptfDriver activePTFDriver;

	protected void setPTFId(String ptfId) {
		this.ptfId = ptfId;

		this.activePTF = null;
		this.activePTFDriver = null;
	}

	protected String getUpdateId() {
		return ptfId;
	}

	protected String getUpdateJarName() {
		return null;
	}

	protected ptf getPTF() {
		return activePTF;
	}

	protected boolean readPTF() {
		logFlush("Reading ptf file ...");

		activePTF = (ptf) getUpdateById();

		if (activePTF != null) {
			log("Reading ptf file ... done");
			logFlush("");

			return true;
		}

		String usePTFId = getUpdateId();

		Iterator productExceptions = getWPProduct().getExceptions();

		if (!productExceptions.hasNext()) {
			addException("WUPD0240E", new String[] { usePTFId }, null);

			log("Reading PTF file ... PTF is absent");
			logFlush("");

			return false;
		}

		while (productExceptions.hasNext()) {
			WASProductException nextException = (WASProductException) productExceptions.next();

			addException("WUPD0241E", new String[] { usePTFId }, nextException);
		}

		log("Reading PTF file ... failed with exception");
		logFlush("");

		return false;
	}

	protected ptfDriver getPTFDriver() {
		return activePTFDriver;
	}

	protected boolean readPTFDriver() {
		logFlush("Reading ptf driver file ...");

		activePTFDriver = (ptfDriver) getUpdateDriverById();

		if (activePTFDriver != null) {
			log("Reading PTF driver file ... done");
			logFlush("");

			return true;
		}

		String usePTFId = getUpdateId();

		Iterator historyExceptions = getWPHistory().getExceptions();

		if (!historyExceptions.hasNext()) {
			addException("WUPD0220E", new String[] { usePTFId }, null);

			log("Reading PTF file ... PTF is absent");
			logFlush("");

			return false;
		}

		while (historyExceptions.hasNext()) {
			WASHistoryException nextException = (WASHistoryException) historyExceptions.next();

			addException("WUPD0221E", new String[] { usePTFId }, nextException);
		}

		log("Reading PTF file ... failed with exception");
		logFlush("");

		return false;
	}

	// Component access ...
	//
	// Vector getComponentNames();
	// Vector getActiveComponentNames();
	// componentUpdate getComponentUpdate(String);
	// boolean prepareComponentUpdates();
	// boolean testComponentUpdate(String);

	protected Vector componentNames;
	protected Vector activeComponentNames;
	protected Hashtable componentUpdates;

	/**
	 * @return  the componentNames
	 * @uml.property  name="componentNames"
	 */
	protected Vector getComponentNames() {
		return componentNames;
	}

	/**
	 * @return  the activeComponentNames
	 * @uml.property  name="activeComponentNames"
	 */
	protected Vector getActiveComponentNames() {
		return activeComponentNames;
	}

	protected static boolean skipInitialProductUpdate = false;
        protected static int productCount = 0;

	protected void setBypassInitialProductUpdate(boolean skipInitialProductUpdate) {
		this.skipInitialProductUpdate = skipInitialProductUpdate;
    }

	protected boolean getBypassInitialProductUpdate() {
		return skipInitialProductUpdate;
	}

	// The active update names are not in the order as set in the selection
	// list.  Put them in this order.

	protected Vector getComponentsFromHistory() {
		Vector components = new Vector();

		WASHistory useHistory = getWPHistory();

		eventHistory useEventHistory = useHistory.getHistory();

		int eventCount = useEventHistory.getUpdateEventCount();

		// assume the top-most event is the current uninstall event, so skip
		eventCount--;

		String usePTFId = getUpdateId();

		log("Scanning " + eventCount + " events.");

		boolean keepScanning = true;

		while (keepScanning && (eventCount > 0)) {
			eventCount--;

			updateEvent nextEvent = useEventHistory.getUpdateEvent(eventCount);

			enumEventType nextEventType = nextEvent.getEventTypeAsEnum();

			if ((nextEventType != null) && (nextEventType == enumEventType.PTF_EVENT_TYPE)) {
				String nextId = nextEvent.getId();

				if ((nextId != null) && nextId.equals(usePTFId)) {
					enumUpdateAction nextAction = nextEvent.getUpdateActionAsEnum();

					if ((nextAction != null)
						&& ((nextAction == enumUpdateAction.SELECTIVE_INSTALL_UPDATE_ACTION) || (nextAction == enumUpdateAction.INSTALL_UPDATE_ACTION))) {

						log("Scanning event with id: " + nextId);
						addEventComponentNames(nextEvent, components);

					} else {
						log("Halting scan on non-install update with id: " + nextId);
						keepScanning = false;
					}
				}
			}
		}

		return components;
	}

	protected void addEventComponentNames(updateEvent useEvent, Vector useComponents) {
		int eventCount = useEvent.getUpdateEventCount();

		while (eventCount > 0) {
			eventCount--;

			updateEvent nextChildEvent = useEvent.getUpdateEvent(eventCount);

			String componentName = nextChildEvent.getId();
			useComponents.addElement(componentName);

			log("Noted historical component installation: " + componentName);
		}
	}

	protected void reorderActiveUpdates() {
		log("Reording active component to selection order ...");

		final HashMap useSelectionMap = new HashMap();

		Vector selectedComponents = getComponentsFromHistory();

		if (selectedComponents == null) {
			log("No historical information; using default order.");
			return;
		} else {
			log("Read historical ordering.");
		}

		int numSelected = selectedComponents.size();

		for (int compNo = 0; compNo < numSelected; compNo++) {
			String nextSelection = (String) selectedComponents.elementAt(compNo);
			log("Noted component: ", nextSelection);

			if (useSelectionMap.get(nextSelection) != null)
				log("Skipping component which was already added: " + nextSelection);

			useSelectionMap.put(nextSelection, new Integer(compNo));
		}

		Comparator selectionCmp = new Comparator() {
			public int compare(Object obj1, Object obj2) {
				String name1 = (String) obj1;
				String name2 = (String) obj2;

				Integer offset1 = (Integer) useSelectionMap.get(name1);
				Integer offset2 = (Integer) useSelectionMap.get(name2);

				if (offset1 == null)
					return -1;
				else if (offset2 == null)
					return +1;

				int actualOffset1 = offset1.intValue();
				int actualOffset2 = offset2.intValue();

				if (actualOffset1 < actualOffset2)
					return -1;
				else if (actualOffset1 == actualOffset2)
					return 0;
				else
					return +1;
			}
		};

		Object[] useActiveNames = activeComponentNames.toArray();

		Arrays.sort(useActiveNames, selectionCmp);

		activeComponentNames = new Vector();

		// for (int compNo = 0; compNo < useActiveNames.length; compNo++)
		//	activeComponentNames.addElement(useActiveNames[compNo]);

                if (numSelected == 0) {
                

		  for (int compNo = useActiveNames.length - 1; compNo > -1; compNo--)
                  {
                
			activeComponentNames.addElement(useActiveNames[compNo]);
                        log("activComponentNames No History " + compNo + useActiveNames[compNo] );
                  }

                }
                else
                {
                

                  for (int compNo = 0; compNo < useActiveNames.length; compNo++)
                  {
                
			activeComponentNames.addElement(useActiveNames[compNo]);
                        log("activComponentNames " + compNo + useActiveNames[compNo] );
                  }

                }


		log("Reording active component to selection order ... done");
	}

	protected componentUpdate getComponentUpdate(String componentName) {
		return (componentUpdate) componentUpdates.get(componentName);
	}

	protected boolean prepareComponentUpdates() {
		log("Preparing component information ...");
		logFlush("");

		componentNames = getPTFDriver().getComponentNames();

		activeComponentNames = new Vector();
		componentUpdates = new Hashtable();

		int componentCount = componentNames.size();

		for (int componentNo = 0; componentNo < componentCount; componentNo++) {
			String nextComponentName = (String) componentNames.elementAt(componentNo);

			if (testComponentUpdate(nextComponentName))
				activeComponentNames.addElement(nextComponentName);
		}

		ptfDriver usePTFDriver = getPTFDriver();

		int numUpdates = usePTFDriver.getComponentUpdateCount();

		for (int compNo = 0; compNo < numUpdates; compNo++) {
			componentUpdate nextUpdate = usePTFDriver.getComponentUpdate(compNo);
			String nextComponentName = nextUpdate.getComponentName();

			componentUpdates.put(nextComponentName, nextUpdate);
		}

		reorderActiveUpdates(); // Need to put them in their correct order,
		// which is as they were set in the selection list.

		log("Component Statistics:");
		logDashes();
		log("Potential Updates: ", Integer.toString(componentCount));
		log("Actual Updates   : ", Integer.toString(activeComponentNames.size()));
		log("PTF Updates      : ", Integer.toString(componentUpdates.size()));
		logDashes();
		log("");
		log("Preparing component information ... done");
		logFlush("");

		return true;
	}

	// Different than EFix processing:
	//     PTF updates may be ADD or REMOVE, hence
	//     the check for the component is not useful.

	protected boolean testComponentUpdate(String candidateName) {
		String logPrefix = "Testing update [ " + candidateName + " ] ...";

		logFlush(logPrefix);

		boolean result;

		if (isSelective()) {
			if (isSelected(candidateName)) {
				log(logPrefix + " explicitly selected: retaining");
				result = true;
			} else {
				log(logPrefix + " explicitly omitted: discarding");
				result = false;
			}

		} else {
			if (componentWasUpdatedByPTF(getUpdateId(), candidateName)) {
				log(logPrefix + " applied: retaining");
				result = true;
			} else {
				log(logPrefix + " not applied: discarding");
				result = false;
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
	// Whereas ptfes may be installed, and selectively installed,
	// components may only be installed.

	// Answer the action for the current ptf.

	protected enumUpdateAction getUpdateAction() {
		if (isSelective())
			return enumUpdateAction.SELECTIVE_UNINSTALL_UPDATE_ACTION;
		else
			return enumUpdateAction.UNINSTALL_UPDATE_ACTION;
	}

	protected enumUpdateAction getComponentUpdateAction() {
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
	//    ptf removePTF(ptf);

	public updateEvent uninstall(String ptfId) {
		// return uninstall(ptfId, null, DO_IGNORE_ERRORS);
                return uninstall(ptfId, null, DO_NOT_IGNORE_ERRORS);
	}

	public updateEvent uninstall(String ptfId, Vector selectedComponents, boolean ignoreErrors) {
		updateEvent event;

		setPTFId(ptfId);
		setSelectedComponents(selectedComponents);
		setIgnoreErrors(ignoreErrors);

		try {
			event = baseUninstall();

		} finally {
			setSelectedComponents(null);
			setIgnoreErrors(false);
			setPTFId(null);
		}

		return event;
	}

	protected updateEvent baseUninstall() {
		nextTaskInGroup(getPreparePTFBanner());
		logFlush("");

		updateEvent event = createUpdateEvent();

		if (!openLog(event)) {
			completeEvent(event, WAS_NOT_CANCELLED);
			return event;
		}

		writeStartingLog(event);

		boolean wasCancelled;

		if (readPTF() && readPTFDriver() && prepareComponentUpdates() && saveEvents(CREATE_BACKUP)) {

			wasCancelled = uninstallComponents(event);

			// Maybe a partial uninstall: Don't remove the ptf file
			// if there are parts of the ptf still installed.
			if (!wasCancelled && !(getExceptions().hasNext()) && !updateApplicationIsPresent()){
                            removePTF();
                        } else { 
                            log( "PTFUninstaller is NOT removing PTF marker files..." );
                            if (getExceptions().hasNext()){
                                log( "Exceptions have been returned to PTFUninstaller:");
                                for (Iterator errIter = getExceptions(); errIter.hasNext(); ) {
                                    log( errIter.next().toString() );
                                }
                            }
                            log( "updateApplicationIsPresent() returned: \"" + updateApplicationIsPresent() + "\"" );
                            log( "wasCancelled is set to:                \"" + wasCancelled + "\"" );
                        }

		} else {
			wasCancelled = false;
		}

		nextTaskInGroup(getCompletePTFBanner());
		
		logFlush("");

		logFlush("");

		completeEvent(event, wasCancelled);

		if (!saveEvents(OMIT_BACKUP))
			failEvent(event);

		//Display config tasks if uninstall completed
		if ( !wasCancelled  && !event.failed() ) {
		   ptfDriver ptfDriver = getPTFDriver();
		   int configCount = ptfDriver.getConfigTaskCount();

		   String configHeader = getString( "install.configtask.header.banner" );
		   String configRequired = getString( "install.configtask.required" );
		   String configOptional = getString( "install.configtask.optional" );
		   boolean firstUnconfigFound = false;

		   if ( configCount != 0 ) {
			  for ( int i=0; i<configCount; i++) {
				 configTask thisTask = (configTask)ptfDriver.getConfigTask( i );
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
		
		completeTaskGroup();

		closeLog();

		return event;
	}

	protected String getCancelledMessage() {
		return getString("result.cancelled.uninstall");
	}

	protected String getSucceededMessage() {
		return getString("result.succeeded.uninstall");
	}

    protected void setInitialProductInformation(componentApplied initialProduct) {

        product initialP = new product();
		initialP.setName(initialProduct.getComponentName());
		initialP.setId(initialProduct.getInitialVersion().getComponentName());
		initialP.setVersion(initialProduct.getInitialVersion().getBuildVersion());
		buildInfo b_info = new buildInfo();
		b_info.setDate(initialProduct.getInitialVersion().getBuildDate());
		b_info.setLevel(initialProduct.getInitialVersion().getSpecVersion());
		initialP.setBuildInfo(b_info);

		//this.initialProduct = initialP;
        initialProducts.add(initialP);

    }

   // not using
    protected product getInitialProductInformation() {
		return initialProduct;
	}

	protected boolean removePTF() {
		logFlush("Removing ptf and ptf driver file ...");

		WPProduct useProduct = getWPProduct();
		boolean removedPTF = useProduct.removePTF(getPTF());

		WASHistory useHistory = getWPHistory();
		boolean removedPTFDriver = useHistory.removePTFDriver(getPTFDriver());

		if (removedPTF && removedPTFDriver) {
			log("Removing ptf and ptf driver file ... done");
			logFlush("");

			log("Restoring initial product information ...");
			
			if (!getBypassInitialProductUpdate()) {
				if (updateProductInformation()) {
					log("Restoring initial product information ...done");
				} else {
					log("Restoring initial product information ...failed");
				}
			} else {
				log("Restoring initial product information ...skipped as an unnecessary step");
			}

			logFlush("");

			return true;
		}

		Iterator productExceptions = useProduct.getExceptions();
		while (productExceptions.hasNext()) {
			WASProductException nextException = (WASProductException) productExceptions.next();

			addException("WUPD0242E", new String[] { getUpdateId()}, nextException);
		}

		Iterator historyExceptions = useHistory.getExceptions();
		while (historyExceptions.hasNext()) {
			WASHistoryException nextException = (WASHistoryException) historyExceptions.next();

			addException("WUPD0242E", new String[] { getUpdateId()}, nextException);
		}

		log("Removing ptf and ptf driver file ... failed with exception");
		logFlush("");

		return false;
	}

	// Component uninstall ...
	//
	//    boolean uninstallComponents(updateEvent);
	//    updateEvent uninstallComponent(updateEvent);
	//
	//    boolean removePTFApplied(updateEvent);
	//
	//    boolean runComponentUninstall(updateEvent, String);

	protected boolean uninstallComponents(updateEvent parentEvent) {
		log("Uninstalling Components ...");
		logFlush("");

		boolean failed = false;
		boolean useIgnoreErrors = getIgnoreErrors();

		boolean wasCancelled = false;

		Vector useComponentNames = getActiveComponentNames();
		int componentCount = useComponentNames.size();

		for (int componentNo = 0;(!failed || useIgnoreErrors) && !wasCancelled && (componentNo < componentCount); componentNo++) {

			String nextComponentName = (String) useComponentNames.elementAt(componentNo);
			LCUtil.setInstallingFeature(nextComponentName);
			if (componentNo != 0)
				log("");

			nextTaskInGroup(getUninstallingPTFBanner(nextComponentName));

			componentUpdate nextUpdate = getComponentUpdate(nextComponentName);
			componentApplied nextApplied = getWPHistory().getPTFComponentAppliedById(getUpdateId(), nextComponentName);

			if (nextApplied == null) {
				log("Skipping component -- no update is active.");

			} else {
				updateEvent childEvent = uninstallComponent(parentEvent, nextUpdate, nextApplied);
				enumEventResult childResult = childEvent.getResultAsEnum();

				if (childResult == enumEventResult.FAILED_EVENT_RESULT) {
					// Use the presence of the exception to fail the installation.
					addException("WUPD0243E", new String[] { nextComponentName }, null);
					failed = true;
				} else if (childResult == enumEventResult.CANCELLED_EVENT_RESULT) {
					wasCancelled = true;
				}
			}
		}

		if (failed)
			log("Uninstalling components ... failed");
		else if (wasCancelled)
			log("Uninstalling components ... cancelled");
		else
			log("Uninstalling components ... ok");

		logFlush("");

		return wasCancelled;
	}

	protected updateEvent uninstallComponent(updateEvent parentEvent, componentUpdate childUpdate, componentApplied childApplied) {
		updateEvent childEvent = createComponentEvent(parentEvent, childUpdate);

		writeStartingComponentLog(childEvent);

		boolean wasCancelled = wasCancelled();

		if (saveEvents(OMIT_BACKUP)) {

			if (childUpdate.getComponentName().equals("external.mq")) {
				wasCancelled = runMQComponentUninstall(childEvent, childUpdate, childApplied);
			} else {
				wasCancelled = runComponentUninstall(childEvent, childUpdate, childApplied);
			}

			if (!wasCancelled && !(getExceptions().hasNext()))
				removePTFApplied(childEvent, childUpdate);
		}

		completeEvent(childEvent, wasCancelled);

		if (!saveEvents(OMIT_BACKUP))
			failEvent(childEvent);

		writeEndingComponentLog(childEvent);

		return childEvent;
	}

	protected boolean updateExternalComponent(component initialComponent) {

		WPProduct useProduct = getWPProduct();

		boolean didSave = useProduct.addComponent(initialComponent);

		if (!didSave) {
			return false;
		}

		return true;

	}

	protected boolean updateProductInformation() {
		boolean result = true;

        int count = initialProducts.size();
        if(count > 0)
        {
            product initialP;
            for(int i =0; i < count; i++)
            {
                initialP = (product)initialProducts.get(i);
                log("Restoring initial product information - "+initialP.getName());

		    	if(!wpsProduct.addProduct(initialP))
                {
                    log("error updating product file "+initialP.getName());
                    logFlush("");
                    result = false;
                }
            }
        }
		return result;
	}

	// Put in an ptf applied for the active component and ptf;
	// Store the update-id, component-name, and starting time-stamp
	// in the applied.  Store the log and backup name for the
	// component update as well.

	protected boolean removePTFApplied(updateEvent childEvent, componentUpdate childUpdate) {
        boolean removeInitialProductInfo = false;
		String usePTFId = getUpdateId();
		String componentName = childUpdate.getComponentName();

		logFlush("Clearing PTF application ..." + usePTFId + " : " + componentName);

		WASHistory useHistory = getWPHistory();

		componentApplied anApplied = useHistory.getPTFComponentAppliedById(usePTFId, componentName);

		boolean result;

		if (anApplied == null) {
			result = false;
			log("Clearing ptf application ... already absent");
		} else {

                    
			result = useHistory.clearPTFApplication(usePTFId, anApplied);

			if (!result) {
				Iterator historyExceptions = useHistory.getExceptions();

				if (!historyExceptions.hasNext())
					log("Strange -- no history exception!");

				while (historyExceptions.hasNext()) {
					WASHistoryException nextException = (WASHistoryException) historyExceptions.next();

					addException("WUPD0244E", new String[] { getUpdateId(), componentName }, nextException);
				}

				log("Clearing ptf application ... failed");

			} else {
				log("Clearing ptf application ... done");
			}
		}

		logFlush("");

		/***** update external.component files to reflect prior version *****/
		//indicates that this is an external component
		if (childUpdate.getComponentName().startsWith("external.")) {
			componentVersion initialVersion = anApplied.getInitialVersion();

			component updateComponent = new component();
			updateComponent.setBuildDate(initialVersion.getBuildDate());
			updateComponent.setBuildVersion(initialVersion.getBuildVersion());
			updateComponent.setName(initialVersion.getComponentName());
			updateComponent.setSpecVersion(initialVersion.getSpecVersion());

			if (!updateExternalComponent(updateComponent)) {
				result = false;
				log("Updating external component application ... failed");
			} else {
				log("Updating external component application ... done");
			}
		}

		logFlush("");

        //only clear the special initial product flag if it is the last remaining applied
		ptfApplied appliedFile = useHistory.getPTFAppliedById(getUpdateId());

		int productUpdateIndex = getPTFDriver().getProductUpdateCount();
        int componentRemain = appliedFile.getComponentAppliedCount();

        if (productCount == 0)
        {
           productCount = appliedFile.GetComponentAppliedProduct();
           //System.out.println("Product Count from AppliedFile is " + productCount);
        }


        product currentProduct = null;

        // if (productCount == 0) 
        //  get_ProductCount();

                 
        //Saiyu boolean processProducts = false;

        //Saiyu if (componentRemain == productCount) {
        //Saiyu    processProducts = true;
        //Saiyu }


	    if (appliedFile != null)
        {
            // if productupdate count and componentremain count is the same
            // this indicates that all we have is product-update
            // if(productUpdateIndex == componentRemain) {
		
            //Saiyu if( (productUpdateIndex >= componentRemain) && processProducts ) {


                //Perform check to see if the remaining componentApplied is a reference
				//to the initial product information
				//IF the installation was cancelled, this information is NOT recorded
                String testProduct;
                String productBaseName;
                File productFile;
                initialProducts = new ArrayList(); // set here and use in setInitialProductInformation

                for (int p = 0; p < productUpdateIndex; p++)
                {
					testProduct = getPTFDriver().getProductUpdate(p).getProductId();
					currentProduct = getCurrentProduct(testProduct);
					
					//Saiyu begins
					if (!testProduct.equals(componentName))
						continue;
					//Saiyu ends

                    //System.out.println("currentProduct ID = "+currentProduct.getId()+ " name = "+currentProduct.getName());
                    //if embeddedExpress/Express or IHS is found
					if (currentProduct != null)
                    {
                        productBaseName = testProduct + ".product";
                        productFile = new File(wpsProduct.getVersionFileName(productBaseName));

                        // if the product exists on the system being updated
                        if ( productFile.exists())
                        {
                            //productUpdateFound = true;
                            log("Found initial product information for file ... " + productFile.getPath());

                            componentApplied component = useHistory.getPTFComponentAppliedById(usePTFId, currentProduct.getName());

                            if (component == null)
                                log("Failed to find product name match when comparing ptfDriver with product file");
                            else
                                log("Add initial product information ...");
                            logFlush("");
   			                if (useHistory.clearPTFApplication(usePTFId, component))
                                log("Preparing initial product information ...done");
                            else
			                    log("Preparing initial product information ...failed");

                            //Saiyu setInitialProductInformation(component);
   			                //Saiyu begins
   			                product initialP = new product();
   			  		        initialP.setName(component.getComponentName());
   			  		        initialP.setId(component.getInitialVersion().getComponentName());
   			  		        initialP.setVersion(component.getInitialVersion().getBuildVersion());
   			  		        buildInfo b_info = new buildInfo();
   			  		        b_info.setDate(component.getInitialVersion().getBuildDate());
   			  		        b_info.setLevel(component.getInitialVersion().getSpecVersion());
   			  		        initialP.setBuildInfo(b_info);
   			  		        if (!wpsProduct.addProduct(initialP))
   	                        {
   	                            log("error updating product file "+initialP.getName());
   	                            logFlush("");
   	                            result = false;
   	                        }
   			  		        break;
   			                //Saiyu ends
   			                
                            /*
                            else
                            {
                                // this should never be called
                                setBypassInitialProductUpdate(true);
                                log("Bypassing preparation of initial product information ...");
                                log("initial product information not recorded in the applied records due to cancellation");
                                logFlush("");
                                logFlush("Why?");
                                logFlush("useHistory.getPTFComponentAppliedById(getUpdateId(), currentProduct.getName()) == " + useHistory.getPTFComponentAppliedById(getUpdateId(), currentProduct.getName()) );
                                logFlush("getUpdateId() == " + getUpdateId() );
                                logFlush("currentProduct.getName() == " + currentProduct.getName() );
                                logFlush("");
                            } */
                        }
                    }
                }
			//Saiyu}
		} else {
			log("No ptfApplied file located...product file doesn't require revertion");
			logFlush("");
		}

		logFlush("");

		return result;
	}

	// Perform installation of the component.

	protected boolean runMQComponentUninstall(updateEvent childEvent, componentUpdate childUpdate, componentApplied childApplied) {
		String usePTFId = getUpdateId();
		String componentName = childUpdate.getComponentName();

		log("Running component update ( " + componentName + " ).");

		RunExec runExec = new RunExec();
		boolean permissionSet = false;

		try {
			log("Processing MQ CSD Uninstall Script");

			String csdInstallRoot =
				getWPProduct().getBackupDirName()
					+ File.separator
					+ componentName
					+ File.separator
					+ "ptfs"
					+ File.separator
					+ getUpdateId()
					+ File.separator
					+ "components"
					+ File.separator
					+ componentName;
			String setupRepository = csdInstallRoot + File.separator + "Setup";
			String csdRepository = csdInstallRoot + File.separator + "CSD";

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

			String mqVerLog = csdInstallRoot + File.separator + "mqVer.properties";
			//String mqPTFLog = csdInstallRoot + File.separator + "mqPtf.log";
			String mqPTFLog = childEvent.getLogName();

			if (OSUtil.isWindows()) {
				log("Processing MQ CSD version information.");

				if (new File(mqVerLog).exists()) {

					Properties versionInfo = parseVersionFile(mqVerLog);
					String availableCSD = versionInfo.getProperty("MQCSDApplied");

					log("Performing CSD uninstall.");
					logFlush("");

					if (availableCSD != null) {
						if (!runExec.execWait("\"" + wmService_win + "\" uninstall \"" + mqPTFLog + "\" " + availableCSD)) {
							// throws Exception ?
							log("Failed to perform uninstall.");
							logFlush("");
							addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
						} else {
							log("Performed MQ CSD uninstall.");
							logFlush("");
						}
					} else {
						log("Failed to locate available MQ CSD");
						logFlush("");
						addException("WUPD0250E", new String[] { usePTFId, componentName }, null);
					}
				} else {
					log("Bypassing CSD Uninstallation due to absence of versioning module");
					log("Reason: component external.mq was bypassed during PTF installation");
					logFlush("");
				}
			} else if ((OSUtil.isAIX() || OSUtil.isLinux() || OSUtil.isSolaris()) && permissionSet) {
				log("Processing MQ CSD version information.");

				if (new File(mqVerLog).exists()) {
					Properties versionInfo = parseVersionFile(mqVerLog);
					String availableCSD = versionInfo.getProperty("MQCSDApplied");

					log("Performing CSD uninstall.");
					logFlush("");

					if (availableCSD != null) {

						if (!runExec.execWait(wmService_unix + " uninstall " + mqPTFLog + " " + availableCSD)) {
							// throws Exception ?
							log("Failed to perform uninstall.");
							logFlush("");
							addException("WUPD0246E", new String[] { getUpdateId(), componentName }, null);
						} else {
							log("Performed MQ CSD uninstall.");
							logFlush("");
						}

					} else {
						log("Failed to locate available MQ CSD");
						logFlush("");
						addException("WUPD0250E", new String[] { usePTFId, componentName }, null);
					}
				} else {
					log("Bypassing CSD Uninstallation due to absence of versioning module");
					log("Reason: component external.mq was bypassed during PTF installation");
					logFlush("");
				}
			}

		} catch (Exception ex) {
			log("Exception while performing update.");
			logFlush("");

			ex.printStackTrace(System.out);
		}

		return false;

	}

	protected Properties parseVersionFile(String file) throws IOException {
		Properties propsFile = new Properties();
		InputStream fs = new java.io.FileInputStream(file);
		propsFile.load(fs);
		fs.close();

		return propsFile;
	}

	protected boolean runComponentUninstall(updateEvent childEvent, componentUpdate childUpdate, componentApplied childApplied) {
		String usePTFId = getUpdateId();
		String componentName = childUpdate.getComponentName();

		if (bypassComponentAction()) {
			log("Bypassing component update ( " + componentName + " ) (for undo)");
			logFlush("");
			return false;
		} else {
			log("Performing component update ( " + componentName + " ) (for undo)");
		}

		if (!initializeLogAndBackup())
			return false;

		Extractor extractor = new Extractor();
		extractor.setQuiet(true);

		try {
			log("Processing extractor command line arguments (for undo).");

			String useLogLevel = Integer.toString(getLogLevel());

			log("Backup Jar: " + childApplied.getBackupName());
			log("Log File  : ", childEvent.getLogName());
			log("Tmp Dir   : ", getWPProduct().getTmpDirName());
			logFlush("Log Level : ", useLogLevel);

			String args[] = { "-JarInputFile", childApplied.getBackupName(), "-LogFile", childEvent.getLogName(),
				// "-TargetDir",    getWASHistory().getProductDirName(),
				"-TmpDir", getWPProduct().getTmpDirName(), "-SkipVer", // skip the obsolete PTF processing
				"-Verbosity", useLogLevel // 0 .. 5  (least .. most)
			};

			if (!extractor.processCmdLineArgs(args)) {
				log("Failed while processing extractor command line arguments (for undo).");
				logFlush("");

				addException("WUPD0250E", new String[] { usePTFId, componentName }, null);
			} else {
				log("Completed processing extractor command line arguments (for undo).");
				log("Performing extraction.");
				logFlush("");

				if (!extractor.process()) { // throws Exception ?
					log("Failed to perform extraction.");
					logFlush("");

					addException("WUPD0252E", new String[] { usePTFId, componentName, childEvent.getLogName()}, null);
				} else {
					log("Performed extraction.");
					logFlush("");

					log("flagging file for deletion: " + childApplied.getBackupName());
					new File(childApplied.getBackupName()).deleteOnExit();
				}
			}

		} catch (Exception ex) {
			log("Exception while performing update (undo).");
			logFlush("");

			addException("WUPD0251E", new String[] { usePTFId, componentName }, ex);
		}

		// TBD: Need to remove the backup jar file.

		return false;
	}

        public int get_ProductCount() {

             String wpsLocation = WPConfig.getProperty( "WpsInstallLocation" );
                     
             WPProduct testWpsProduct;

             testWpsProduct = new WPProduct(wpsLocation);

             Iterator piter = testWpsProduct.getProducts();

             while ( piter.hasNext() ) {
                 product pthisProd = (product)piter.next();
                 productCount++;
             }

             System.out.println("returned product count " + productCount );

             return productCount;


        }

}
