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

package com.ibm.websphere.update.silent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.xml.componentApplied;
import com.ibm.websphere.product.history.xml.componentUpdate;
import com.ibm.websphere.product.history.xml.componentVersion;
import com.ibm.websphere.product.history.xml.efixDriver;
import com.ibm.websphere.product.history.xml.enumUpdateType;
import com.ibm.websphere.product.history.xml.productPrereq;
import com.ibm.websphere.product.history.xml.productUpdate;
import com.ibm.websphere.product.history.xml.ptfApplied;
import com.ibm.websphere.product.history.xml.ptfDriver;
import com.ibm.websphere.product.history.xml.updateEvent;
import com.ibm.websphere.product.xml.BaseHandlerException;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.efix;
import com.ibm.websphere.product.xml.efix.ptf;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.update.harness.WPCPUpdateHarnessManager;
import com.ibm.websphere.update.harness.WPUpdateHarnessController;
import com.ibm.websphere.update.harness.WPUpdateHarnessManager;
import com.ibm.websphere.update.ioservices.IOService;
import com.ibm.websphere.update.ioservices.IOServicesException;
import com.ibm.websphere.update.ioservices.StandardIOServiceFactory;
import com.ibm.websphere.update.ptf.ExtendedComponent;
import com.ibm.websphere.update.ptf.ImageRepository;
import com.ibm.websphere.update.ptf.PTFBatchUpdater;
import com.ibm.websphere.update.ptf.PTFFilter;
import com.ibm.websphere.update.ptf.PTFImage;
import com.ibm.websphere.update.ptf.PTFInstallData;
import com.ibm.websphere.update.ptf.prereq.PTFPrereqChecker;
import com.ibm.websphere.update.transforms.UpdateFileSearch;
import com.ibm.websphere.update.util.WPConfig;

/**
 * Class: PTFInstaller.java Abstract: Installs FixPacks Component Name: wps.base.fix Release: wps5.fix File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/silent/PTFInstaller.java, wps.base.fix, wps5.fix History 1.13, 4/5/05 01-Nov-2002 Initial Version 04-Dec-2003 cdchoi iSeries enablement
 */
public class PTFInstaller extends BaseInstaller {

        //********************************************************
        //  Program Versioning
        //********************************************************
        public static final String pgmVersion = "1.13" ;
        //********************************************************
        //  Program Versioning
        //********************************************************
        public static final String pgmUpdate = "4/5/05" ;

        //***********************************************************
        // Instance State
        //***********************************************************
        private UpdateInstallerArgs args;

        private Vector prereqErrors = new Vector();
        private Vector missingFixPacks = new Vector();
        private Vector notAvailable = new Vector();
        private Vector alreadyComplete = new Vector();
        private Vector extendedComponents = new Vector();
        private Vector skippedExtendedComponents = new Vector();
        private Vector optionalComponents = new Vector();
        private Vector componentNotPresent = new Vector();
        private Set skippedOptionalComponents = new HashSet();
        private Vector images = new Vector();
        private Vector events = new Vector();
        private HashMap idHash = new HashMap();
        private HashMap extendedComponentsMap = new HashMap();

        private Hashtable imageTable;

        private String wasCell;
        private String wasNode;

        private boolean prereqSatisfied = true;

        private boolean initializedUpdaterForInstall = false;

        private ImageRepository repository;
        private PTFBatchUpdater updater;
        private PTFInstallData installData;


    private Set productIdSet;     // list of product-id in the fixpack's <product-prereq> and satisfied <product-coreq> tags

    	// these two are for selective update, command-line parameter "-updateFeature"
    	private boolean bSelectedInstall = false;
    	private Vector selectedInstallList = new Vector();
    	
        public PTFInstaller(UpdateInstallerArgs args) {
                this.args = args;
                if(args.bSelectFeature)
                {
                	bSelectedInstall = true;
                	for (int i = 0; i < args.selectFeatureList.size(); i++)
                	{
                		selectedInstallList.add(args.selectFeatureList.elementAt(i));
                	}
                }
                /*
                System.out.println(" in PTFInstaller ");
                System.out.println(bSelectedInstall);
                for (int i = 0; i < selectedInstallList.size(); i++)
                {
                	System.out.println("selectedInstallList " + selectedInstallList.elementAt(i));
                }
                */
        }
    

        //***********************************************************
        // Debugging Utility
        //***********************************************************
        public static final String debugPropertyName = "com.ibm.websphere.update.ptf.debug" ;
        //***********************************************************
        // Debugging Utility
        //***********************************************************
        public static final String debugTrueValue = "true" ;
        //***********************************************************
        // Debugging Utility
        //***********************************************************
        public static final String debugFalseValue = "false" ;

        protected static boolean debug;

        static {
                String debugValue = System.getProperty(debugPropertyName);

                debug = ((debugValue != null) && debugValue.equals(debugTrueValue));
        }

        /**
		 * @return  the debug
		 * @uml.property  name="debug"
		 */
        public static boolean isDebug() {
                return debug;
        }

        public static void debug(String arg) {
                if (!debug)
                        return;

                System.out.println(arg);
        }

        public static void debug(String arg1, String arg2) {
                if (!debug)
                        return;

                System.out.print(arg1);
                System.out.println(arg2);
        }

        //***********************************************************
        // Method Definitions
        //***********************************************************

        //********* BEGIN: required abstract method implementations **********/
        protected void initializeRepository(String reposDir) {
                repository = new ImageRepository(new StandardIOServiceFactory(), wpsProduct.getDTDDirName(), reposDir, ImageRepository.PTF_IMAGES);
        }

        protected void initializeUpdaterForInstall() {
                updater = new PTFBatchUpdater(wpsProduct, wpsHistory, new StandardNotifier(INSTALL), new IOService(), getInstallableExtendedComponents());

                setInitializedUpdaterForInstall(true);

        }

        protected void initializeUpdaterForUninstall() {
                updater = new PTFBatchUpdater(wpsProduct, wpsHistory, new StandardNotifier(UNINSTALL), new IOService(), getInstalledExtendedComponents());
        }

        protected boolean initialize( ) {
        boolean retval = false;
        retval = commonInitialize( args );
        return retval;
        }

        protected boolean initializeProduct() {

      boolean strategyOK = true;
                if (!args.wpcpOnly) {
         WPUpdateHarnessController uhc = new WPUpdateHarnessController(args.installDir);
         strategyOK = uhc.performStrategy();
      } else {
                        WPUpdateHarnessManager uhm = new WPCPUpdateHarnessManager(args.getWPCPDir());
                        strategyOK = uhm.harness();
      }
      if (strategyOK) {
         strategyOK = commonInitializeProduct( args );
      }
      return strategyOK;

        }

        //********* END: required abstract method implementations **********/
        private void activateRepository(ImageRepository repository) throws IOException, IOServicesException, BaseHandlerException {
                repository.prepare();
                imageTable = repository.getPTFImages();
        }

        /**
		 * @param initializedUpdaterForInstall  the initializedUpdaterForInstall to set
		 * @uml.property  name="initializedUpdaterForInstall"
		 */
        private void setInitializedUpdaterForInstall(boolean val) {
                initializedUpdaterForInstall = val;
        }

        private boolean hasInitializedUpdaterForInstall() {
                return initializedUpdaterForInstall;
        }

        private Vector getInstallableExtendedComponents() {
                return extendedComponents;
        }

        private void setInstallableExtendedComponents(Vector extendedComponents) {
                this.extendedComponents = extendedComponents;
        }

        private Vector getInstalledExtendedComponents() {
                return new Vector();
        }

        private boolean hasImagesToInstall() {
                return (images.size() > 0);
        }

        private int[] countInstallableComponents(PTFImage anImage) {
                int[] counts = new int[] { 0, 0, 0 };
                Vector currentComponentNotPresent = new Vector();

                //System.out.println(this.getClass().getName() + " :: countInstallableComponents");
                ptfDriver driverFile = anImage.getPTFDriver();
                String usePTFId = anImage.getPTFId();

                //Iterator useComponentNames = anImage.getComponentNames().iterator();
                int componentUpdateCount = driverFile.getComponentUpdateCount();
                //System.out.println("componentUpdatecount = "+componentUpdateCount);
                for(int i=0; i<componentUpdateCount; i++){
                        componentUpdate compUpdate = (componentUpdate) driverFile.getComponentUpdate(i);
                        String nextComponentName = compUpdate.getComponentName();

                        //System.out.println("nextComponentName " + nextComponentName);
                        counts[TOTAL_OFFSET]++;
                        //System.out.println("total_offset " + counts[TOTAL_OFFSET]);
                        if (wpsProduct.componentPresent(nextComponentName)) {
                                if (wpsHistory.ptfComponentAppliedPresent(usePTFId, nextComponentName)) {
                                        counts[TOTAL_INSTALLED_OFFSET]++;
                                } else {
                                        counts[TOTAL_INSTALLABLE_OFFSET]++;
                                        //System.out.println(counts[TOTAL_INSTALLABLE_OFFSET]);
                                        if (!images.contains(anImage))
                                                images.add(anImage);

                                }
                        } else {
                                if (compUpdate.getUpdateTypeAsEnum() == enumUpdateType.ADD_UPDATE_TYPE) {
                                        if (!images.contains(anImage))
                                                images.add(anImage);
                                } else {
                                        currentComponentNotPresent.add(usePTFId);
                                        componentNotPresent.add(nextComponentName);
                                }
                        }
                }

                if ((counts[TOTAL_INSTALLED_OFFSET] == counts[TOTAL_OFFSET]) && currentComponentNotPresent.size() == 0) {
                        alreadyComplete.add(usePTFId);
                }

                return counts;
        }

        private PTFInstallData prepareOptionalComponents(PTFInstallData installData, Vector optionalComponentsInput) {

                Vector useOptionalComponents = updater.getOptionalUpdates(installData);
                Vector optionalComponentsId = new Vector();


                int numUseOptionalComps = useOptionalComponents.size();
                int numOptionalComps = optionalComponentsInput.size();
                for (int i = 0; i < numOptionalComps; i++) {
                        String optionalComponentId = (String) optionalComponentsInput.elementAt(i);

                        for (int j = 0; j < numUseOptionalComps; j++) {
                                Object[] componentUpdateMap = (Object[]) useOptionalComponents.elementAt(j);
                                componentUpdate compUpdate = (componentUpdate) componentUpdateMap[0];
                                String compUpdateId = compUpdate.getComponentName();
                                if (optionalComponentId.equals(compUpdateId)) {
                                        optionalComponents.add(componentUpdateMap);
                                        optionalComponentsId.add(compUpdateId);
                                }
                        }

                        if (!optionalComponentsId.contains(optionalComponentId))
                            skippedOptionalComponents.add(optionalComponentId);
                }

                installData.setOptionalUpdates(optionalComponents);

                return installData;
        }

        private boolean isFullyInstalled(String ptfID, PTFImage image) {
                Vector compNames = image.getComponentNames();

                int compCount = image.getComponentCount();
                boolean allArePresent = true;

                for (int compNo = 0; allArePresent && (compNo < compCount); compNo++) {
                        String compName = (String) compNames.elementAt(compNo);
                        allArePresent = wpsHistory.ptfComponentAppliedPresent(ptfID, compName);
                }

                return allArePresent;
        }

        // Error handling utility ...
        // display miss, unavailable and selected components
        private boolean displayMissing() {

                int numMissing = missingFixPacks.size();
                if (numMissing > 0) {
                        if (!hasImagesToInstall()) {
                                System.out.println("");
                                System.out.println(UpdateReporter.getSilentString("fixpack.does.not.exist"));
                                UpdateInstaller.puiReturnCode = 9;     // eg. invalid PTF name
                        } else {
                                System.out.println("");
                                if (args.install) {
                                        System.out.println(UpdateReporter.getSilentString("fixpack.does.not.exist.but.continue.install"));
                                } else if (args.uninstall) {
                                        System.out.println(UpdateReporter.getSilentString("fixpack.does.not.exist.but.continue.uninstall"));
                                }
                        }

                        for (int missingNo = 0; missingNo < numMissing; missingNo++) {
                                String nextMissing = (String) missingFixPacks.elementAt(missingNo);
                                System.out.println("  " + nextMissing);
                        }
                }

                int numMissingComp = componentNotPresent.size();
                if (numMissingComp > 0) {
                        if (!hasImagesToInstall()) {
                                System.out.println("");
                                System.out.println(UpdateReporter.getSilentString("listing.fixpacks.already.installed"));
                                //System.out.println("");
                                //System.out.println(UpdateReporter.getSilentString("fixpack.component.not.available"));
                        } else {
                                System.out.println("");
                                if (args.install) {
                                        System.out.println(UpdateReporter.getSilentString("fixpack.component.not.available.but.continue.install"));
                                } else if (args.uninstall) {
                                        System.out.println(UpdateReporter.getSilentString("fixpack.component.not.available.but.continue.uninstall"));
                                }
                        }

                        for (int compMissingNo = 0; compMissingNo < numMissingComp; compMissingNo++) {
                                String nextMissing = (String) componentNotPresent.elementAt(compMissingNo);
                                System.out.println("  " + nextMissing);

                        }

                }
                // display selected feature update message
                if(args.bSelectFeature)
                {
                	System.out.println(UpdateReporter.getSilentString("fixpack.component.select.update"));
                	for (int sfCount = 0; sfCount < args.selectFeatureList.size(); sfCount++)
                	{
                		System.out.println((String)args.selectFeatureList.elementAt(sfCount));
                	}
                }
                
                return true;
        }

        private boolean displayAlreadyComplete() {
                if (alreadyComplete.size() == 0)
                        return false;

                if (!hasImagesToInstall()) {
                        System.out.println(UpdateReporter.getSilentString("listing.fixpacks.already.installed"));
                        System.out.println("");
                }

                int numComplete = alreadyComplete.size();
                for (int completeNo = 0; completeNo < numComplete; completeNo++) {
                        System.out.println("  " + (String) alreadyComplete.elementAt(completeNo));
                }

                return true;
        }

        private boolean hasPrereqErrors() {
                if (!prereqSatisfied)
                        return true;

                return false;
        }

        private boolean displayOptionalComponentsSkipped() {
                if (skippedOptionalComponents.size() == 0)
                        return false;

                System.out.println("");
                System.out.println(UpdateReporter.getSilentString("listing.fixpack.optional.components.skipped"));

                Iterator optionalIter = skippedOptionalComponents.iterator();
                while (optionalIter.hasNext()) {
                        System.out.println("  " + (String) optionalIter.next());
                }

                return true;
        }

        private boolean displayFeaturesUnavailable() {
                if (skippedExtendedComponents.size() == 0)
                        return false;

                System.out.println("");

                if (hasImagesToInstall()) {
                        System.out.println(UpdateReporter.getSilentString("listing.fixpack.features.skipped.but.continue.install"));
                } else {
                        System.out.println(UpdateReporter.getSilentString("listing.fixpack.features.skipped"));
                }

                int numSkipped = skippedExtendedComponents.size();
                for (int i = 0; i < numSkipped; i++) {
                        Object[] skippedComp = (Object[]) skippedExtendedComponents.elementAt(i);
                        String skipReason = (String) skippedComp[1];
                        if (skipReason.equals("level")) {
                                System.out.println("  " + (String) skippedComp[0] + ": " + UpdateReporter.getSilentString("fixpack.feature.filtered.by.level"));
                        } else {
                                System.out.println("  " + (String) skippedComp[0] + ": " + UpdateReporter.getSilentString("fixpack.feature.filtered.by.availability"));
                        }
                }

                return true;
        }

        protected void showException(Exception e) {
                try {
                        String msgText = e.getMessage();
                        if ((msgText == null) || (msgText.length() == 0))
                                msgText = e.toString();

                        System.err.println(msgText);

                } catch (Throwable th) {
                        try {
                                System.err.println(e.getClass().getName() + ":");
                                System.err.println(e.toString());
                        } catch (Throwable th1) {
                        }
                }

                if (args.printStack)
                        e.printStackTrace(System.err);
        }

// Han, replaced with getInstalledProducts()
/*        
        private void getPtfDriverProducts() {     
        	ptfDriver nextDriver = wpsHistory.getPTFDriverById(UpdateInstallerArgs.thisIsFixpackID);
	        // at this point prereqcheck is satisfied so we can save the product-id(s) to a list
	        productIdSet = new HashSet();
	        int ip = nextDriver.getProductPrereqCount();
	        int ipp;
	
	        // save product-prereq
	        // the actual count should be one, cause prereq is always OR condition
	        for(ipp=0; ipp<ip; ipp++)
	        {
	            productPrereq p = nextDriver.getProductPrereq(ipp);
	            String id = p.getProductId().toLowerCase();
	                
	            if(!productIdSet.contains(id))
	           		productIdSet.add(id);
	        }
        }
*/
        // this replaces the getPtfDriverProducts()
        private void getInstalledProducts() 
        {     
        	// lets go thru the ptfApplied file to see which
        	// feature(s) is installed and we only want to run the
        	// uninstall on those features, this way, efixes installed 
        	// and do not belong to any of the will-be uninstalled 
        	// features won't get removed.

        	ptfApplied applied = wpsHistory.getPTFAppliedById(UpdateInstallerArgs.thisIsFixpackID);
        	        	
        	productIdSet = new HashSet();
        	int ip = applied.getComponentAppliedCount();
        	int ipp;
        		
        	for(ipp=0; ipp<ip; ipp++)
        	{
            	componentApplied p = applied.getComponentApplied(ipp);
            	String id = p.getComponentName().toLowerCase();
            	
            	// we want to check both the short-name and long-name of the product
            	// because in the ptfApplied file contains two commponents for the same
            	// feature, one with long-name and one with short-name and we only need
            	// the short-name and need it once
            	// using hashSet, because it doesn't allow duplicate elements.
            	if(-1 != (LCUtil.PRODUCTID_LC_ACTIVITIES.toLowerCase()).indexOf(id) ||
            			-1 != (LCUtil.PRODUCTID_LC_FULL_ACTIVITIES.toLowerCase()).indexOf(id))
            	{
            		productIdSet.add(LCUtil.PRODUCTID_LC_ACTIVITIES);
            	}
            	if(-1 != (LCUtil.PRODUCTID_LC_BLOGS.toLowerCase()).indexOf(id) ||
            			-1 != (LCUtil.PRODUCTID_LC_FULL_BLOGS.toLowerCase()).indexOf(id))
            	{
            		productIdSet.add(LCUtil.PRODUCTID_LC_BLOGS);
            	}
            	if(-1 != (LCUtil.PRODUCTID_LC_COMMUNITIES.toLowerCase()).indexOf(id) ||
            			-1 != (LCUtil.PRODUCTID_LC_FULL_COMMUNITIES.toLowerCase()).indexOf(id))
            	{
            		productIdSet.add(LCUtil.PRODUCTID_LC_COMMUNITIES);
            	}
            	if(-1 != (LCUtil.PRODUCTID_LC_DOGEAR.toLowerCase()).indexOf(id) ||
            			-1 != (LCUtil.PRODUCTID_LC_FULL_DOGEAR.toLowerCase()).indexOf(id))
            	{
            		productIdSet.add(LCUtil.PRODUCTID_LC_DOGEAR);
            	}
            	if(-1 != (LCUtil.PRODUCTID_LC_PROFILES.toLowerCase()).indexOf(id) ||
            			-1 != (LCUtil.PRODUCTID_LC_FULL_RPOFILES.toLowerCase()).indexOf(id))
            	{
            		productIdSet.add(LCUtil.PRODUCTID_LC_PROFILES);
            	}
            }
        

        }


        private Vector getEfixesToRemove() {
                Vector uninstallableEfixes = new Vector();

                Iterator useEFixes = wpsProduct.getEFixes();

                // if it is selective update, then reset the productIdSet to
                // contain only the selected feature(s)
                if(bSelectedInstall)
                {
                	productIdSet.clear();
                	productIdSet.addAll(selectedInstallList);
                }
                
                /*
                Iterator it = productIdSet.iterator();
                while (it.hasNext()) {
                    // Get element
                    String id = (String)it.next();
                    System.out.println("productIdSet contains " + id);
                }
                */
                // walk-thru all the efixes one-by-one to determine if it it needs to get removed or not.
                while (useEFixes.hasNext())
                {
                    efix nextEFix = (efix) useEFixes.next();

                   System.out.println("Testing efix = "+nextEFix.getId());

                    efixDriver efd = (efixDriver) wpsHistory.getEFixDriverById(nextEFix.getId());
                    int ip = efd.getProductPrereqCount();

                    boolean bRemove = false;  // default is not to remove the efix
                    boolean bCheck = true;

                    if (ip > 0)
                    {
                        int ipp;
                        for(ipp=0; ipp<ip; ipp++)
                        {
                            productPrereq p = efd.getProductPrereq(ipp);
                            String pid = p.getProductId().toLowerCase();
                            
                            System.out.println("Current product-id: "+pid);
                            
                            if (!(bRemove = productIdSet.contains(pid)))
                            {
                                // System.out.println(pid + " not in productIdSet.");
                                if (bCheck) bCheck = false;
                            }

                            // if we want to just remove the efix when we got a match
                            // break out at first match, this will add the efix to the
                            // uninstall. We don't need to bCheck boolean for this
                            //if((bRemove = productIdSet.contains(pid)))
                            //    break;

                        }
                    }
                    if (bRemove && bCheck){
                        uninstallableEfixes.addElement(nextEFix);
                        //System.out.println("efix: "+nextEFix.getId()+" added for removal");
                    }
                }
                return uninstallableEfixes;

        }
        
        private void processPTFInstallData(PTFInstallData installData) {
                installData.setEFixesToRemove(getEfixesToRemove());
                if (args.getOptionalComponents().size() > 0)
                        prepareOptionalComponents(installData, args.getOptionalComponents());
                else
                        installData.setOptionalUpdates(new Vector());
        }

        protected boolean isWindows() {
                boolean isWindows = false;
                String windowsId = System.getProperty("os.name");

                if (windowsId != null && windowsId.indexOf("Windows") >= 0)
                        isWindows = true;

                return isWindows;
        }

        protected boolean isSolaris() {
                boolean isSolaris = false;
                String solarisId = System.getProperty("os.name");

                if (solarisId != null && solarisId.equals("SunOS"))
                        isSolaris = true;

                return isSolaris;
        }

        private void setPrimaryWASCell(String wasCell) {
                this.wasCell = wasCell;
        }

        private void setPrimaryWASNode(String wasNode) {
                this.wasNode = wasNode;
        }

        private String getPrimaryWASCell() {
                return wasCell;
        }

        private String getPrimaryWASNode() {
                return wasNode;
        }

        private void loadWASEnv() {
                String envValue = "";

      /*
                WASEnvPropsHandler wasPropsHandle = null;

                if (isWindows()) {
                        wasPropsHandle = new WASEnvPropsHandler(wpsProduct.getProductDirName() + File.separator + "bin" + File.separator + "setupCmdLine.bat");
                } else {
                        wasPropsHandle = new WASEnvPropsHandler(wpsProduct.getProductDirName() + File.separator + "bin" + File.separator + "setupCmdLine.sh");
                }

                wasPropsHandle.loadConfigFile();
      */
                
        }

        private String getWPCPLocation() {
                String wpcpResult = "";

                loadWASEnv();

                UpdateFileSearch fileSearcher = new UpdateFileSearch(wpsProduct.getProductDirName());
                fileSearcher.setSearchFilter(UpdateFileSearch.FILES_ONLY);

                String wasCell = getPrimaryWASCell();
                String wasNode = getPrimaryWASNode();

                String searchQuery =
                        "config" + File.separator + "cells" + File.separator + wasCell + File.separator + "nodes" + File.separator + wasNode + File.separator + "variables.xml";

                List result = fileSearcher.search(searchQuery);
                String variables_xml = ((File) result.get(0)).getAbsolutePath();
       
                return wpcpResult;

        }

        private String getWTPLocation(String wpsLocation) {
                String wtpLocation = "";
      wtpLocation = wtpLocation + File.separator + "WTP";
      /*
                if (OSUtil.isWindows()) {
                        wempsLocation = wtpLocation + File.separator + "WTP";
                } else if (OSUtil.isLinux()) {
                        wempsLocation = File.separator + "opt" + File.separator + "wemps";
                } else if (OSUtil.isSolaris()) {
                        wempsLocation = File.separator + "opt" + File.separator + "wemps";
                } else if (OSUtil.isAIX()) {
                        wempsLocation = File.separator + "usr" + File.separator + "opt" + File.separator + "wemps";
                }
      */
                if (!new File(wtpLocation).exists())
                        wtpLocation = "";

                return wtpLocation;

        }

        private int getVersionAsNum(String version) {
                StringTokenizer st = new StringTokenizer(version, ".");
                StringBuffer num = new StringBuffer();

                while (st.hasMoreTokens()) {
                        num.append(st.nextToken());
                }

                return new Integer(num.toString()).intValue();
        }

        private void filterExtendedComponents(ptfDriver driver, Map extendedComponentsMap) {
                Vector filteredComponents = new Vector();
                Set foundComponents = new HashSet();

                if (extendedComponentsMap.size() > 0) {
                        Set compKeys = extendedComponentsMap.keySet();
                        Iterator compKeyIter = compKeys.iterator();
                        while (compKeyIter.hasNext()) {
                                String compName = (String) compKeyIter.next();

                                //parse the driver file to retrieve the availability in FixPack
                                int componentUpdateCount = driver.getComponentUpdateCount();
                                for (int i = 0; i < componentUpdateCount; i++) {
                                        componentUpdate compUpdate = driver.getComponentUpdate(i);

                                        if (compUpdate.getComponentName().equals(compName)) {

                                                foundComponents.add(compName);

                                                //further filter by level
                                                // if a .component file is available
                                                //   use the current version listed
                                                // else
                                                //   use the current default version
                                                //   and compare w/ the version in the ptfDriver
                                                ExtendedComponent ec = (ExtendedComponent) extendedComponentsMap.get(compName);
                                                componentVersion compVersion = ec.getPriorVersion();

                                                //the current base version
                                                int currentVersion = getVersionAsNum(compVersion.getBuildVersion());

                                                //the version-to-be after update is applied - denoted by finalVersion?
                                                int compFinalVersion = getVersionAsNum(compUpdate.getFinalVersion().getBuildVersion());

                                                if (currentVersion < compFinalVersion) {
                                                        filteredComponents.add(extendedComponentsMap.get(compName));
                                                } else {
                                                        Object[] skippedComp = { compName, "level" };
                                                        skippedExtendedComponents.add(skippedComp);
                                                }
                                        }

                                }

                                if (!foundComponents.contains(compName)) {
                                        Object[] skippedComp = { compName, "availability" };
                                        skippedExtendedComponents.add(skippedComp);
                                }

                        }
                }

                setInstallableExtendedComponents(filteredComponents);
        }

        private componentVersion getMQCompVersion() {
                component useMQComp = wpsProduct.getComponentByName(componentIds[1]);

                String currentVersion = "";
                String currentDate = "";
                String currentSpecLevel = "";
                if (useMQComp != null) {
                        currentVersion = useMQComp.getBuildVersion();
                        currentDate = useMQComp.getBuildDate();
                        currentSpecLevel = useMQComp.getSpecVersion();
                } else {
                        //set a default value
                        currentVersion = "5.3.0";
                        currentDate = "01/14/03";
                        currentSpecLevel = "5.0";
                }

                componentVersion mqCompVersion = new componentVersion();
                mqCompVersion.setComponentName(componentIds[1]);
                mqCompVersion.setBuildVersion(currentVersion);
                mqCompVersion.setBuildDate(currentDate);
                mqCompVersion.setSpecVersion(currentSpecLevel);

                return mqCompVersion;
        }

        private component getMQComp() throws IOException, IOServicesException, BaseHandlerException {
                String finalVersion = "";
                String finalDate = "";
                String finalSpecLevel = "";

                int numIds = args.getFixPackList().size();
                for (int idNo = 0; idNo < numIds; idNo++) {
                        String nextId = (String) args.getFixPackList().elementAt(idNo);

                        PTFImage nextImage = (PTFImage) imageTable.get(nextId);

                        if (nextImage != null) {
                                nextImage.prepareDriver();
                                nextImage.prepareComponents();

                                ptfDriver ptfDriver = nextImage.getPTFDriver();

                                boolean finalVersionFound = false;
                                int componentUpdateCount = ptfDriver.getComponentUpdateCount();
                                for (int i = 0; !finalVersionFound && i < componentUpdateCount; i++) {
                                        componentUpdate compUpdate = ptfDriver.getComponentUpdate(i);
                                        if (compUpdate.getComponentName().equals(componentIds[1])) {
                                                finalVersion = compUpdate.getFinalVersion().getBuildVersion();
                                                finalDate = compUpdate.getFinalVersion().getBuildDate();
                                                finalSpecLevel = compUpdate.getFinalVersion().getSpecVersion();
                                                finalVersionFound = true;
                                        }
                                }

                        }
                }

                component mqComp = new component();
                mqComp.setName(componentIds[1]);
                mqComp.setBuildVersion(finalVersion);
                mqComp.setBuildDate(finalDate);
                mqComp.setSpecVersion(finalSpecLevel);

                return mqComp;
        }

        private componentVersion getIHSCompVersion() {
                component useIHSComp = wpsProduct.getComponentByName(componentIds[0]);

                String currentVersion = "";
                String currentDate = "";
                String currentSpecLevel = "";
                if (useIHSComp != null) {
                        currentVersion = useIHSComp.getBuildVersion();
                        currentDate = useIHSComp.getBuildDate();
                        currentSpecLevel = useIHSComp.getSpecVersion();
                } else {
                        //set a default value
                        currentVersion = "1.3.26";
                        currentDate = "01/14/03";
                        currentSpecLevel = "5.0";
                }

                componentVersion ihsCompVersion = new componentVersion();
                ihsCompVersion.setComponentName(componentIds[0]);
                ihsCompVersion.setBuildVersion(currentVersion);
                ihsCompVersion.setBuildDate(currentDate);
                ihsCompVersion.setSpecVersion(currentSpecLevel);

                return ihsCompVersion;
        }

        private component getIHSComp() throws IOException, IOServicesException, BaseHandlerException {
                String finalVersion = "";
                String finalDate = "";
                String finalSpecLevel = "";

                int numIds = args.getFixPackList().size();
                for (int idNo = 0; idNo < numIds; idNo++) {
                        String nextId = (String) args.getFixPackList().elementAt(idNo);

                        PTFImage nextImage = (PTFImage) imageTable.get(nextId);

                        if (nextImage != null) {
                                nextImage.prepareDriver();
                                nextImage.prepareComponents();

                                ptfDriver ptfDriver = nextImage.getPTFDriver();

                                boolean finalVersionFound = false;
                                int componentUpdateCount = ptfDriver.getComponentUpdateCount();
                                for (int i = 0; !finalVersionFound && i < componentUpdateCount; i++) {
                                        componentUpdate compUpdate = ptfDriver.getComponentUpdate(i);
                                        if (compUpdate.getComponentName().equals(componentIds[0])) {
                                                finalVersion = compUpdate.getFinalVersion().getBuildVersion();
                                                finalDate = compUpdate.getFinalVersion().getBuildDate();
                                                finalSpecLevel = compUpdate.getFinalVersion().getSpecVersion();
                                                finalVersionFound = true;
                                        }
                                }

                        }
                }

                component ihsComp = new component();
                ihsComp.setName(componentIds[0]);
                ihsComp.setBuildVersion(finalVersion);
                ihsComp.setBuildDate(finalDate);
                ihsComp.setSpecVersion(finalSpecLevel);

                return ihsComp;
        }

        private componentVersion getWEMPSCompVersion() {
                component useWEMPSComp = wpsProduct.getComponentByName(componentIds[2]);

                String currentVersion = "";
                String currentDate = "";
                String currentSpecLevel = "";
                if (useWEMPSComp != null) {
                        currentVersion = useWEMPSComp.getBuildVersion();
                        currentDate = useWEMPSComp.getBuildDate();
                        currentSpecLevel = useWEMPSComp.getSpecVersion();
                } else {
                        //set a default value
                        currentVersion = "5.0.0";
                        currentDate = "01/14/03";
                        currentSpecLevel = "5.0";
                }

                componentVersion wempsCompVersion = new componentVersion();
                wempsCompVersion.setComponentName(componentIds[2]);
                wempsCompVersion.setBuildVersion(currentVersion);
                wempsCompVersion.setBuildDate(currentDate);
                wempsCompVersion.setSpecVersion(currentSpecLevel);

                return wempsCompVersion;
        }

        private component getWEMPSComp() throws IOException, IOServicesException, BaseHandlerException {
                String finalVersion = "";
                String finalDate = "";
                String finalSpecLevel = "";

                int numIds = args.getFixPackList().size();
                for (int idNo = 0; idNo < numIds; idNo++) {
                        String nextId = (String) args.getFixPackList().elementAt(idNo);

                        PTFImage nextImage = (PTFImage) imageTable.get(nextId);

                        if (nextImage != null) {
                                nextImage.prepareDriver();
                                nextImage.prepareComponents();

                                ptfDriver ptfDriver = nextImage.getPTFDriver();

                                boolean finalVersionFound = false;
                                int componentUpdateCount = ptfDriver.getComponentUpdateCount();
                                for (int i = 0; !finalVersionFound && i < componentUpdateCount; i++) {
                                        componentUpdate compUpdate = ptfDriver.getComponentUpdate(i);
                                        if (compUpdate.getComponentName().equals(componentIds[2])) {
                                                finalVersion = compUpdate.getFinalVersion().getBuildVersion();
                                                finalDate = compUpdate.getFinalVersion().getBuildDate();
                                                finalSpecLevel = compUpdate.getFinalVersion().getSpecVersion();
                                                finalVersionFound = true;
                                        }
                                }

                        }
                }

                component wempsComp = new component();
                wempsComp.setName(componentIds[2]);
                wempsComp.setBuildVersion(finalVersion);
                wempsComp.setBuildDate(finalDate);
                wempsComp.setSpecVersion(finalSpecLevel);

                return wempsComp;
        }

        private boolean verifyProductFeatures() throws IOException, IOServicesException, BaseHandlerException {

                if (!getCurrentProductType().contains(WPProduct.PRODUCT_IDS[0]) && !getCurrentProductType().contains(WPProduct.PRODUCT_IDS[1])) {

                        if (args.wpcpUpdate)
                                System.out.println(UpdateReporter.getSilentString("fixpack.verifying.features.specified"));

                        if (args.wpcpUpdate) {

                                String wpcpLocation = "";

            // TODO: Special process for WPCP in Express.....
                                //System.out.println( "TODO: handle anything special for WPCP (embedded) in Express/Express Plus" );

                        }
                }

                System.out.println("");
                return true;

        }

        public String getCurrentProduct() {
                String currentProduct = "";
                boolean foundProductType = false;

                //reorganize the precedence
                String[] products =WPProduct.PRODUCT_IDS;

                for (int idNo = 0; idNo < products.length; idNo++) {
                        product nextProduct = wpsProduct.getProductById(products[idNo]);

                        //scan for product file existence
                        if (nextProduct != null) {
                                currentProduct = nextProduct.getId();
                        }
                }

        debug( "PTFInstaller.getCurrentProduct is returning: " + currentProduct );
                return currentProduct;
        }

        public ArrayList getCurrentProductType() {
                ArrayList productBreeds = new ArrayList();
                boolean foundProductType = false;

                //reorganize the precedence
                String[] products = WPProduct.PRODUCT_IDS;


                for (int idNo = 0; idNo < products.length; idNo++) {
                        product nextProduct = wpsProduct.getProductById(products[idNo]);

                        //scan for product file existence
                        if (nextProduct != null) {
                                productBreeds.add(nextProduct.getId());
                        }
                }

                return productBreeds;
        }

        public boolean doInstall() {
                if (!initialize())
                        return false;

                try {
                        initializeRepository(args.fixPackDir);
                        activateRepository(repository);
                } catch (Exception e) {
                        System.out.println("");
                        System.err.println(UpdateReporter.getSilentString("fixpack.dir.specified.incorrectly", args.fixPackDir));
                        return false;
                }

                try {
                        if (!verifyProductFeatures())
                                return false;
                        setupInstall();
                } catch (Exception e) {
                        System.err.println(UpdateReporter.getSilentString("error.setup.install.fixpack"));
                        if (!debug)
                            e.printStackTrace();
                        showException(e);
                        return false;
                }

                if (args.displayFixPackDetails)
                        displayDetails();

                if (hasPrereqErrors())
                {
                    displayPrereqErrors();
                    return false;
                }
                if (hasImagesToInstall()){

                    //Check for Montevista Linux., Disk Space Checking should be bypassed.
                    if (!new File(File.separator + "etc" + File.separator + "mvl-release").exists()) {
                         return runInstall();                
                    } else {
                        displayMissing(); //should also display efixes without corresponding component files and selected feature update
                        displayAlreadyComplete();
                        displayFeaturesUnavailable();
                        displayOptionalComponentsSkipped();
                        System.out.println("");
                        return runInstall();
                    }

                } else {
                        displayMissing(); //should also display efixes without corresponding component files
                        displayAlreadyComplete();
                        displayFeaturesUnavailable();
                        displayOptionalComponentsSkipped();
                        System.out.println("");
                        return false;
                }

        }


    	private boolean displayPrereqErrors() {
            if (prereqErrors.size() == 0)
                return false;

            int errorSize = prereqErrors.size();
            System.out.println();
            for (int i = 0; i < errorSize; i++) {
                System.out.println((String) (prereqErrors.elementAt(i)));
            }
            System.out.println();
            System.out.println(UpdateReporter.getSilentString("fix.error", "fixpack"));
            System.out.println();
            return true;
	    }

        private void setupInstall() throws IOException, IOServicesException, BaseHandlerException {
                PTFImage imageToInstall = null;
                ptfDriver nextDriver = null;

                //set up the password properties for later use
                if(args.isPwdListAvailable())
                {
                    HashMap hm = args.getPwdListMap();
                    StringBuffer pwdList = new StringBuffer();
                    for (Iterator it=hm.keySet().iterator(); it.hasNext(); )
                    {
                        String key = (String)it.next();
                        String value = (String)hm.get(key);
                        WPConfig.setProperty(key, value);
                        pwdList.append(key);
                        pwdList.append(",");
                    }
                    // we need this because we have no way to know what password has been saved
                    // so we need to store all the keys in the pwdList so we can retrieve them later
                    WPConfig.setProperty("pwdList",pwdList.toString());
                }
            
                int numIds = args.getFixPackList().size();
                
                for (int idNo = 0; idNo < numIds; idNo++) {
                        String nextId = (String) args.getFixPackList().elementAt(idNo);

                        PTFImage nextImage = (PTFImage) imageTable.get(nextId);
                        if (nextImage == null) {
                                missingFixPacks.add(nextId);
                        } else {

                                nextImage.prepareDriver();

                                nextImage.prepareComponents();
                                
                                //System.out.println("componentNames " + nextImage.getComponentNames().toString());
                                
                                //nextImage.prepareExtendedComponentImages(wpsProduct, "external.wpcp");

                                nextDriver = nextImage.getPTFDriver();

                            // ptfDriver validation
                                PTFPrereqChecker checker = new PTFPrereqChecker( wpsProduct, wpsHistory );
                                if ( !checker.testPTFInstallation(nextDriver, prereqErrors ) )
                                {
                                    prereqSatisfied = false;
                                    return;
                                }

                                // at this point prereqcheck is satisfied so we can save the product-id(s) to a list
                                productIdSet = new HashSet();
                                int ip = nextDriver.getProductPrereqCount();
                                int ipp;
/*                                
                                // if selected update, then only add the selected product to the id list
                                if(bSelectedInstall)
                                {
                                	for (int i = 0; i < selectedInstallList.size(); i++)
                                	{
                                		productIdSet.add(selectedInstallList.elementAt(i));
                                	}
                                }
                                else
                                {
*/ 	                                // save product-prereq
	                                // the actual count should be one, cause prereq is always OR condition
	                                for(ipp=0; ipp<ip; ipp++)
	                                {
	                                    productPrereq p = nextDriver.getProductPrereq(ipp);
	                                    String id = p.getProductId().toLowerCase();
	                                    if(!productIdSet.contains(id))
	                                        productIdSet.add(id);
	                                }
//                                }
                                // save product-coreq
                                Vector andCoreqs = checker.getAndCoreqs();
                                if(!andCoreqs.isEmpty())
                                {
                                    ip = andCoreqs.size();
                                    for(ipp=0; ipp<ip; ipp++)
                                    {
                                        productIdSet.add(andCoreqs.get(ipp));
                                    }
                                }
                                Vector orCoreqs = checker.getOrCoreqs();
                                if(!orCoreqs.isEmpty())
                                {
                                    ip = orCoreqs.size();
                                    for(ipp=0; ipp<ip; ipp++)
                                    {
                                        productIdSet.add(orCoreqs.get(ipp));
                                    }
                                }

                                int[] installCounts = countInstallableComponents(nextImage);
                                Object[] nextData = new Object[9];
                                packageInstallData(nextData, nextDriver, nextImage);
                                idHash.put(new Integer(idNo), nextData);

                                //if there is to be an update for either feature
                                //filter for update availability
                                if (args.wpcpUpdate || args.pdmUpdate || args.wmmUpdate || args.wtpUpdate || args.odcUpdate || args.cfgUpdate ) {
                                        filterExtendedComponents(nextDriver, extendedComponentsMap);
                                        //if feature updates are skipped
                                        //set an empty extendedComponents collection
                                } else {
                                        setInstallableExtendedComponents(new Vector());
                                }

                        }
                }

                if (images.size() > 0) {

                        PTFFilter ptfFilter = new PTFFilter(wpsProduct, wpsHistory);
                        ptfFilter.setProductBreeds(getCurrentProductType());

                        PTFImage fixPackToInstall = (PTFImage) images.get(0);

                        if (!ptfFilter.isFuture(fixPackToInstall)) {
                                prereqSatisfied = false;
                                System.out.println(UpdateReporter.getSilentString("fixpack.level.mismatch", fixPackToInstall.getPTFId()));
                                return;
                        }

                        if (!ptfFilter.satisfiesProduct(fixPackToInstall)) {

		                int numUpdates = nextDriver.getProductUpdateCount();
		                product installedProduct = null;
		                
		                for (int i=0; installedProduct == null && i<numUpdates; i++) {
		                    // try and determine which product is installed that can be updated
		                    productUpdate prodUpdate = nextDriver.getProductUpdate( i );
		                    installedProduct = wpsProduct.getProductById( prodUpdate.getProductId() );
		                }
		                if ( installedProduct == null ) {
		                    // Non of the products to be updated are installed, pick first return installed product for message
		                    installedProduct = (product)wpsProduct.getProducts().next();
		                }
		
		                                prereqSatisfied = false;
		                                System.out.println(
		                                        UpdateReporter.getSilentString(
		                                                "fixpack.product.mismatch",
		                                                fixPackToInstall.getPTFId(),
		                                                installedProduct.getName() + " " + installedProduct.getVersion() ) );
		                                return;
		                        }
		
		                        if (!ptfFilter.satisfiesPlatform(fixPackToInstall)) {
		                                prereqSatisfied = false;
		                                System.out.println(UpdateReporter.getSilentString("fixpack.platform.mismatch", fixPackToInstall.getPTFId()));
		                                return;
		                        }
		
		
		
		
		         // <SMP>
		         /* Need to do component prereq checking here.  Need to remove any component-updates that do not apply.
		
		         */
					if (!args.prereqOverride) {
		
					System.out.println( "Checking Component pre-reqs " + fixPackToInstall.getPTFDriver().getId() );
		            Vector errors = new Vector();
		            PTFPrereqChecker checker =
		                new PTFPrereqChecker( wpsProduct, wpsHistory );
		            if ( !checker.testComponentPrereqs(fixPackToInstall.getPTFDriver(), errors) ) {
		               // Should display errors here
		               int numErrors = errors.size();
		
		               for ( int errorNo = 0; errorNo < numErrors; errorNo++ ) {
		                   String nextError = (String) errors.elementAt(errorNo);
		
		                   System.out.println("[" + errorNo + "]: " + nextError);
		               }
		
		               System.out.println();
		               prereqSatisfied = false;
		               return;
		            }
					} else {
						System.out.println( "Skipping  Component pre-reqs checks" );
		         }
		
				// </SMP>

		            initializeUpdaterForInstall();
		            PTFBatchUpdater pbu = new PTFBatchUpdater(wpsProduct, wpsHistory, new StandardNotifier(INSTALL), new IOService(), null);
		            installData = pbu.prepareImage((PTFImage) images.elementAt(0));
		            processPTFInstallData(installData);
                }

      // <SMP>
               /*
                                        if (updater.testInstallPrerequisites(images, installOrder, prereqErrors))
                                                images = installOrder;
               */
       // </SMP>

        }

        private boolean runInstall() {
                if (installData == null)
                        return false;

                events = updater.install(installData, (bSelectedInstall ? selectedInstallList : null));

                boolean result;
                String resultMessage;

                if (updater.wasCancelled(events)) {
                        result = false;

                        updateEvent updateError = updater.selectCancelledEvent(events);
                        String errorLogName = updateError.getLogName();
                        resultMessage = UpdateReporter.getSilentString("WUPD0033E", new Object[] { errorLogName });

                } else if (updater.didFail(events)) {
                        result = false;

                        updateEvent updateError = updater.selectFailingEvent(events);
                        String errorLogName = updateError.getLogName();
                        resultMessage = UpdateReporter.getSilentString("WUPD0034E", new Object[] { errorLogName });
                        UpdateInstaller.puiReturnCode = 9;

                } else {
                        result = true;
                        resultMessage = UpdateReporter.getSilentString("fixpack.install.cmdline.success");
                }

                if (result)
                        UpdateReporter.printInstallerMessage(resultMessage);
                else
                        UpdateReporter.printInstallerErrorMessage(resultMessage);

                return result;

        }

        public boolean doUninstall() {
                if (!initialize())
                        return false;

                try {
                        initializeUpdaterForUninstall();
                        setupUninstall();

                } catch (Exception e) {
                        System.err.println(UpdateReporter.getSilentString("error.setup.uninstall.fixpack"));
                        //showException(e);
                        return false;
                }

                if (args.displayFixPackDetails)
                        displayDetails();

                if (hasImagesToInstall()) {
                        if (hasPrereqErrors()) {
                                return false;
                        } else {
                                displayMissing();
                                System.out.println("");
                                return runUninstall();
                        }
                } else {
                        displayMissing();
                        System.out.println("");
                        return false;
                }
        }

        private void setupUninstall() throws IOException, IOServicesException, BaseHandlerException {
                int numIds = args.getFixPackList().size();
                for (int idNo = 0; idNo < numIds; idNo++) {
                        String nextId = (String) args.getFixPackList().elementAt(idNo);
                        ptfDriver nextDriver = wpsHistory.getPTFDriverById(nextId);

                        if (nextDriver == null) {
                                missingFixPacks.add(nextId);

                        } else {
                                Object[] nextData = new Object[6];
                                packageUninstallData(nextData, nextDriver);
                                idHash.put(new Integer(idNo), nextData);

                                images.add(nextId);
                        }
                }

                if (missingFixPacks.size() > 0)
                        return;

                /*
                                if (!args.prereqOverride) {
                                        Vector uninstallOrder = new Vector();

                                        if (updater.testUninstallPrerequisites(images, uninstallOrder, prereqErrors))
                                                images = uninstallOrder;
                                }
                */
        }

        private boolean runUninstall() {

                String ptfImage = (String) images.get(0);
                updater.preparePTF(ptfImage);

                // Han
                //getPtfDriverProducts();
                // replace the above with this
                getInstalledProducts();
                
                // if this is a selected uninstall, we want to make sure the
                // removal process is LIFO
                if(bSelectedInstall)
                {
                	// get each of the install time of the selected feature(s)
                	// compare each of the install time of the non-selected feature(s)
                	// if there is a time from the non-selected feature(s) newer than the
                	// selected feature, which means it was installed after that feature
                	// then this is not a LIFO, stop here and return false
                	// HANK
                }
                 //Saiyu Vector updateEvents = updater.uninstall(ptfImage, getEfixesToRemove());
                Vector updateEvents = updater.uninstall(ptfImage, getEfixesToRemove(), (bSelectedInstall ? selectedInstallList : null)); //Saiyu
        //Vector updateEvents = updater.uninstall(ptfImage);
                boolean result;
                String resultMessage;

                if (updater.didFail(events)) {
                        result = false;

                        updateEvent updateError = updater.selectFailingEvent(events);
                        String errorLogName = updateError.getLogName();
                        resultMessage = UpdateReporter.getSilentString("WUPD0035E", new Object[] { errorLogName });
                        UpdateInstaller.puiReturnCode = 6;     // eg. locked file

                } else {
                        result = true;
                        resultMessage = UpdateReporter.getSilentString("fixpack.uninstall.cmdline.success");
                }

                if (result)
                        UpdateReporter.printUninstallerMessage(resultMessage);
                else
                        UpdateReporter.printUninstallerErrorMessage(resultMessage);

                return result;
        }

        // Display utility ...
        private void displayDetails() {

                int numFixPacks = idHash.size();
                if (numFixPacks > 0) {
                        System.out.println("[" + UpdateReporter.getBaseString("label.update.fixpack.detail.title") + "]");
                        for (int fixPackNo = 0; fixPackNo < numFixPacks; fixPackNo++) {
                                Object[] nextData = (Object[]) idHash.get(new Integer(fixPackNo));

                                String[] basicData = {(String) nextData[0], (String) nextData[1], (String) nextData[3], (String) nextData[4], (String) nextData[5] };

                                listBasicPTFData(basicData);

                                System.out.println("");
                        }
                } else {
                        System.out.println("[" + UpdateReporter.getBaseString("label.update.detail.unavailable.title") + "]");
                }
        }

        public boolean doListInstalled() {
                boolean result;

                try {
                        result = listInstalled();
                        // throws IOException, IOServicesException, BaseHandlerException

                } catch (Exception e) {
                        System.err.println(UpdateReporter.getSilentString("error.listing.installed.fixpacks"));
                        //showException(e);
                        result = false;
                }

                return result;
        }

        private boolean listInstalled() {
                if (!initialize())
                        return false;

                System.out.println(UpdateReporter.getSilentString("label.installed.fixpacks"));

                Iterator fixPacks = wpsProduct.getPTFs();

                if (!fixPacks.hasNext()) {
                        System.out.println("  " + UpdateReporter.getSilentString("no.installed.fixpacks"));
                } else {
                        while (fixPacks.hasNext()) {
                                ptf nextFixPack = (ptf) fixPacks.next();
                                listInstalledPTF(nextFixPack);
                        }
                }

                return true;
        }

        private void listInstalledPTF(ptf aFixPack) {
                listBasicPTFData(new String[] { aFixPack.getId()});
        }

        public boolean doListAvailable() {
                boolean result;

                try {
                        result = listAvailable();
                        // throws IOException, IOServicesException, BaseHandlerException

                } catch (Exception e) {
                        System.out.println("");
                        System.err.println(UpdateReporter.getSilentString("fixpack.dir.unable.to.locate", args.fixPackDir));
                        //System.err.println(UpdateReporter.getSilentString("error.listing.available.fixpacks"));
                        //showException(e);
                        result = false;
                }

                return result;
        }

        private boolean listAvailable() throws IOException, IOServicesException, BaseHandlerException {
                if (!initialize())
                        return false;

                System.out.println(UpdateReporter.getSilentString("get.available.fixpacks", args.fixPackDir));

                initializeRepository(args.fixPackDir);
                activateRepository(repository);

                Enumeration imagesEnum = imageTable.keys();

                if (!imagesEnum.hasMoreElements()) {
                        System.out.println(UpdateReporter.getSilentString("no.fixpacks.available"));
                        return true;
                }

                System.out.println(UpdateReporter.getSilentString("available.fixpack.count", Integer.toString(imageTable.size())));
                System.out.println(UpdateReporter.getSilentString("available.fixpack.count.key"));

                int fixPackCount = 0;

                while (imagesEnum.hasMoreElements()) {
                        fixPackCount++;

                        String nextFixPackId = (String) imagesEnum.nextElement();
                        PTFImage nextImage = (PTFImage) imageTable.get(nextFixPackId);

                        nextImage.prepareDriver();
                        nextImage.prepareComponents();

                        int[] componentCounts = countInstallableComponents(nextImage);

                        int total = componentCounts[TOTAL_OFFSET], installed = componentCounts[TOTAL_INSTALLED_OFFSET], installable = componentCounts[TOTAL_INSTALLABLE_OFFSET];

                        String fixPackText =
                                UpdateReporter.getSilentString(
                                        "fixpack.component.state",
                                        new String[] { Integer.toString(fixPackCount), nextFixPackId, Integer.toString(total), Integer.toString(installed), Integer.toString(installable)});
                        System.out.println(fixPackText);
                }

                return true;
        }

        private void listBasicPTFData(String[] ptfData) {
                System.out.println("  " + UpdateReporter.getBaseString("label.update.details.fixPackId") + " " + ptfData[0]);

                if (ptfData.length == 1)
                        return;

                System.out.println("  " + UpdateReporter.getBaseString("label.details.build.date") + " " + ptfData[1]);
                System.out.println("  " + UpdateReporter.getBaseString("label.details.build.ver") + " " + ptfData[4]);
                System.out.println("  " + UpdateReporter.getBaseString("label.update.fixpack.description") + " " + ptfData[2]);
        }

        private void packageInstallData(Object ptfData[], ptfDriver driver, PTFImage image) {
                packageCommonData(ptfData, driver);
                ptfData[2] = getInstallState(driver, image);
        }

        private void packageUninstallData(Object[] ptfData, ptfDriver driver) {
                packageCommonData(ptfData, driver);

                ptfData[2] = getUninstallState(driver);
        }

        private void packageCommonData(Object[] ptfData, ptfDriver driver) {
                ptfData[0] = driver.getId();
                ptfData[1] = driver.getBuildDate();
                // ptfData[2] = installState;
                ptfData[3] = driver.getShortDescription();
                ptfData[4] = driver.getLongDescription();
                ptfData[5] = driver.getBuildVersion();
                //ptfData[6] = driver.getIncludedEFixIds();
        }

        private String getInstallState(ptfDriver driver, PTFImage image) {
                String installState;

                String fixPackID = driver.getId();

                if (wpsHistory.ptfAppliedPresent(fixPackID)) {
                        if (isFullyInstalled(fixPackID, image))
                                installState = "installed";
                        else
                                installState = "partially_installed";

                } else {
                        installState = "not_installed";
                }

                return installState;
        }

        private String getUninstallState(ptfDriver driver) {
                String installState;

                ptfApplied applied = wpsHistory.getPTFAppliedById(driver.getId());

                if (applied != null) {
                        if (applied.getComponentAppliedCount() < driver.getComponentUpdateCount()) {
                                installState = "installed";
                        } else {
                                installState = "partially_installed";
                        }
                } else {
                        installState = "not_installed";
                }

                return installState;
        }

        private String getPrerequisiteText(efixDriver driver) {
                //NOT YET AVAILABLE
                return "";
        }

}
