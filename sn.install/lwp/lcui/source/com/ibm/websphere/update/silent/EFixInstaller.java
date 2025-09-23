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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.ibm.websphere.product.history.xml.efixApplied;
import com.ibm.websphere.product.history.xml.efixDriver;
import com.ibm.websphere.product.history.xml.efixPrereq;
import com.ibm.websphere.product.history.xml.updateEvent;
import com.ibm.websphere.product.xml.BaseHandlerException;
import com.ibm.websphere.product.xml.efix.efix;
import com.ibm.websphere.update.ioservices.IOService;
import com.ibm.websphere.update.ioservices.IOServicesException;
import com.ibm.websphere.update.ioservices.StandardIOServiceFactory;
import com.ibm.websphere.update.ptf.EFixBatchUpdater;
import com.ibm.websphere.update.ptf.EFixImage;
import com.ibm.websphere.update.ptf.ImageRepository;
import com.ibm.websphere.update.util.WPConfig;

/**
 * Class: EFixInstaller.java Abstract: Installs EFixes Component Name: WAS.ptf Release: ASV50X File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/silent/EFixInstaller.java, wps.base.fix, wps6.fix History 1.9, 1/15/06 01-Nov-2002 Initial Version
 */
public class EFixInstaller extends BaseInstaller {

	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "1.9" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "1/15/06" ;

	//***********************************************************
	// Instance State
	//***********************************************************
	private UpdateInstallerArgs args;

	private Vector prereqErrors = new Vector();
	private Vector supersededInfo = new Vector();
	private Vector missingEfixes = new Vector();
	private Vector alreadyComplete = new Vector();
	private Vector images = new Vector();
	private Vector events = new Vector();
	private Vector componentNotPresent = new Vector();
	private HashMap idHash = new HashMap();

    private Hashtable imageTable;

	private ImageRepository repository;
	private EFixBatchUpdater updater;

	public EFixInstaller(UpdateInstallerArgs args) {
		this.args = args;
	}

	//***********************************************************
	// Method Definitions
	//***********************************************************

	//********* BEGIN: required abstract method implementations **********/
	protected void initializeRepository(String reposDir) {
		repository = new ImageRepository(new StandardIOServiceFactory(), wpsProduct.getDTDDirName(), reposDir, ImageRepository.EFIX_IMAGES);
	}

	protected void activateRepository(ImageRepository repository) throws IOException, IOServicesException, BaseHandlerException {
		repository.prepare();
		imageTable = repository.getEFixImages();
	}

	protected void initializeUpdaterForInstall() {
		updater =
			new EFixBatchUpdater(wpsProduct, wpsHistory, new StandardNotifier(INSTALL), new IOService());
	}

	protected void initializeUpdaterForUninstall() {
		updater =
			new EFixBatchUpdater(wpsProduct, wpsHistory, new StandardNotifier(UNINSTALL), new IOService());
	}

	protected boolean initialize() {
      return commonInitialize( args );
	}

	protected boolean initializeProduct() {
      return commonInitializeProduct( args );
	}
	//********* END: required abstract method implementations **********/

	private boolean hasImagesToInstall() {
        if(null == images)
            return false;
        else if(images.size() > 0)
            return true;
    	else
            return false;
	}

	private int[] countInstallableComponents(EFixImage anImage) {
		int[] counts = new int[] { 0, 0, 0 };
		Vector currentComponentNotPresent = new Vector();

		String useEFixId = anImage.getEFixId();

		Iterator useComponentNames = anImage.getComponentNames().iterator();
		while (useComponentNames.hasNext()) {
			String nextComponentName = (String) useComponentNames.next();

			counts[TOTAL_OFFSET]++;

			if (wpsProduct.componentPresent(nextComponentName)) {
				if (wpsHistory.efixComponentAppliedPresent(useEFixId, nextComponentName)) {
					counts[TOTAL_INSTALLED_OFFSET]++;
				} else {
					counts[TOTAL_INSTALLABLE_OFFSET]++;
					if(!images.contains(anImage)) images.add(anImage);
				}
			} else {
				currentComponentNotPresent.add(useEFixId);
				componentNotPresent.add(useEFixId);
			}
		}

		if ((counts[TOTAL_INSTALLED_OFFSET] == counts[TOTAL_OFFSET])
			&& currentComponentNotPresent.size() == 0) {
			alreadyComplete.add(useEFixId);
		}

		return counts;
	}

	private boolean isFullyInstalled(String efixID, EFixImage image) {
		Vector compNames = image.getComponentNames();

		int compCount = image.getComponentCount();
		boolean allArePresent = true;

		for (int compNo = 0; allArePresent && (compNo < compCount); compNo++) {
			String compName = (String) compNames.elementAt(compNo);
			allArePresent = wpsHistory.efixComponentAppliedPresent(efixID, compName);
		}

		return allArePresent;
	}

	// Error handling utility ...
	private boolean displayMissing() {

		int numMissing = missingEfixes.size();
		if (numMissing > 0) {
			if (!hasImagesToInstall()) {
				System.out.println("");
				System.out.println(UpdateReporter.getSilentString("efix.does.not.exist"));
                                UpdateInstaller.puiReturnCode = 9;  // eg. invalid efix name
                        } else {
				System.out.println("");
				if (args.install) {
					System.out.println(
						UpdateReporter.getSilentString("efix.does.not.exist.but.continue.install"));
				} else if (args.uninstall) {
					System.out.println(
						UpdateReporter.getSilentString("efix.does.not.exist.but.continue.uninstall"));
				}
			}

			for (int missingNo = 0; missingNo < numMissing; missingNo++) {
				String nextMissing = (String) missingEfixes.elementAt(missingNo);
				System.out.println("  " + nextMissing);
			}
		}

		int numMissingComp = componentNotPresent.size();
		if (numMissingComp > 0) {
			if (!hasImagesToInstall()) {
				System.out.println("");
				System.out.println(UpdateReporter.getSilentString("efix.component.not.available"));
			} else {
				System.out.println("");
				if (args.install) {
					System.out.println(
						UpdateReporter.getSilentString("efix.component.not.available.but.continue.install"));
				} else if (args.uninstall) {
					System.out.println(
						UpdateReporter.getSilentString(
							"efix.component.not.available.but.continue.uninstall"));
				}
			}

			for (int compMissingNo = 0; compMissingNo < numMissingComp; compMissingNo++) {
				String nextMissing = (String) componentNotPresent.elementAt(compMissingNo);
				System.out.println("  " + nextMissing);

			}
		}

		return true;
	}

	private boolean displayAlreadyComplete() {
		if (alreadyComplete.size() == 0)
			return false;

		if (!hasImagesToInstall()) {
			System.out.println(UpdateReporter.getSilentString("listing.efixes.already.installed"));
			System.out.println("");
		} else {
			System.out.println(
				UpdateReporter.getSilentString("listing.efixes.already.installed.but.continue"));
			System.out.println("");
		}

		int numComplete = alreadyComplete.size();
		for (int completeNo = 0; completeNo < numComplete; completeNo++) {
			System.out.println("  " + (String) alreadyComplete.elementAt(completeNo));
		}

		return true;
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
        System.out.println(UpdateReporter.getSilentString("fix.error", "efix"));
        System.out.println();
        return true;
	}
	
	private boolean displaySupersededInfo(){
		if (supersededInfo.size() == 0)
			return false;
		System.out.println();
		int errorSize = supersededInfo.size();
        for (int i = 0; i < errorSize; i++) {
			System.out.println((String) (supersededInfo.elementAt(i)));
		}
        System.out.println();
        return true;
	}

	protected void showException(Exception e) {
		try {
			System.err.println(e.getMessage());

		} catch (Throwable th) {
			System.err.println(e.getClass().getName());
		}

		if (args.printStack)
			e.printStackTrace(System.err);
	}

	public boolean doInstall() {
		if (!initialize())
			return false;

		try {
			initializeRepository(args.efixDir);
			activateRepository(repository);
		} catch (Exception e){
			System.out.println("");
			System.err.println(UpdateReporter.getSilentString("efix.dir.specified.incorrectly", args.efixDir));
			//showException(e);
			return false;
		}

		try {
			initializeUpdaterForInstall();
			setupInstall();
		} catch (Exception e) {
			System.err.println(UpdateReporter.getSilentString("error.setup.install.efixes"));
            e.printStackTrace();
			showException(e);
			return false;
		}

		if (args.displayEfixDetails)
			displayDetails();

        if(displayPrereqErrors()) // efix prereqs not met
            return false;
        else if (hasImagesToInstall())
        {
        	
        		if(displaySupersededInfo()){
        			return false;
        		}
				displayMissing(); //should also display efixes without corresponding component files
				displayAlreadyComplete();
				return runInstall();
		} else {
            System.out.println("Nothing to install" );
			displayMissing(); //should also display efixes without corresponding component files
			displayAlreadyComplete();
			return false;
		}
	}

	private void setupInstall() throws IOException, IOServicesException, BaseHandlerException {
		// Not the most efficient processing, since all of the
		// jars are being processed, when only a subset need to
		// be used.
        //System.out.println( this.getClass().getName() + "::setupInstall : "  );

		if (args.efixJarsInput) {
         //System.out.println( this.getClass().getName() + "::setupInstall : Jars Input"  );
			Vector absoluteJarNames = new Vector();

			for (int jarNo = 0; jarNo < args.efixJarList.size(); jarNo++) {
				String jarName = (String) args.efixJarList.elementAt(jarNo);
				jarName = args.efixDir + File.separator + jarName;

				File jarFile = new File(jarName);
				jarName = jarFile.getAbsolutePath();
				absoluteJarNames.add(jarName);
            //System.out.println( "Adding -  " + jarName );
			}

			Iterator images = imageTable.values().iterator();

			while (images.hasNext()) {
				EFixImage nextImage = (EFixImage) images.next();
				String candidateJarName = nextImage.getJarName();

            //System.out.println( "Candidate -  " + candidateJarName );

				boolean found = false;

				for (int jarNo = 0; !found && (jarNo < absoluteJarNames.size()); jarNo++) {
					String nextJarName = (String) absoluteJarNames.elementAt(jarNo);
					found = nextJarName.equals(candidateJarName);

               //System.out.println( "Checking -  " + nextJarName );
               //if (found) {
               //   System.out.println( this.getClass().getName() + "::setupInstall : found match -  " + nextJarName );
               //}
				}

				if (found)
					args.maybeAddEFixFromJar(nextImage.getEFixId());

			}
		}

		int numIds = args.getEfixList().size();
		for (int idNo = 0; idNo < numIds; idNo++) {
			String nextId = (String) args.getEfixList().elementAt(idNo);

			EFixImage nextImage = (EFixImage) imageTable.get(nextId);

			if (nextImage == null) {
				missingEfixes.add(nextId);
			} else {

				nextImage.prepareDriver();
				nextImage.prepareComponents();

				efixDriver nextDriver = nextImage.getEFixDriver();

				int[] installCounts = countInstallableComponents(nextImage);
				Object[] nextData = new Object[9];
				packageInstallData(nextData, nextDriver, nextImage);
				idHash.put(new Integer(idNo), nextData);
			}
		}

		if (images.size() > 0) {
			if (!args.prereqOverride) {
				Vector installOrder = new Vector();
            	if(!updater.testInstallPrerequisites(images, installOrder, prereqErrors, supersededInfo))
                    images = null;
                else
                    images = installOrder;
                }
			}

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
   }

	private boolean runInstall() {
            // System.out.println( this.getClass().getName() + "::runInstall : "  );
		if (!updater.prepareImages(images))
			return false;

/*
      if (true) {
         System.out.println( this.getClass().getName() + "::runInstall : " + "Skipping install" );
         return false;
      }
*/
		events = updater.install(images);

		boolean result;
		String resultMessage;

		if (updater.wasCancelled(events)) {
			result = false;

			updateEvent updateError = updater.selectCancelledEvent(events);
			String errorLogName = updateError.getLogName();
			resultMessage = UpdateReporter.getSilentString("WUPD0025E", new Object[] { errorLogName });

		} else if (updater.didFail(events)) {
			result = false;

			updateEvent updateError = updater.selectFailingEvent(events);
			String errorLogName = updateError.getLogName();
			resultMessage = UpdateReporter.getSilentString("WUPD0026E", new Object[] { errorLogName });
                        UpdateInstaller.puiReturnCode = 9;

		} else {
			result = true;
			resultMessage = UpdateReporter.getSilentString("efix.install.cmdline.success");
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

		//System.out.println("in silent.efixinstaller.java, doUninstall()");
		try {
			initializeUpdaterForUninstall();
			//System.out.println("passed initialize");
			setupUninstall();

		} catch (Exception e) {
			System.err.println(UpdateReporter.getSilentString("error.setup.uninstall.efixes"));
			showException(e);
			return false;
		}

		if (args.displayEfixDetails)
			displayDetails();

		if (hasImagesToInstall()) {
			if (displayPrereqErrors()) {
				return false;
			} else {
				displayMissing(); //should also display efixes without corresponding component files
				return runUninstall();
			}
		} else {
			displayMissing(); //should also display efixes without corresponding component files
			return false;
		}
	}

	private void setupUninstall() throws IOException, IOServicesException, BaseHandlerException, Exception {

		//System.out.println( this.getClass().getName() + "::setupUninstall : "  );
		
		if (args.uninstallAll) {
			Iterator efixes = wpsProduct.getEFixes();

			while (efixes.hasNext()) {
				efix nextEFix = (efix) efixes.next();
				args.maybeAddEFix(nextEFix.getId());
			}
		}

		 
		int numIds = args.getEfixList().size();
		//System.out.println("numIds = " + Integer.toString(numIds));
		
// Begin, Han 06182007
		
		// Do we have efix with multiple product-prereq in Lotus Connections?
		// maybe not, but lets assume it does anyway, because it will take care
		// of both multiple and single product-prereq
		
		// we want to make sure when multiple efixes are entered as a list for
		// uninstall, they all have at least one product-id in common
		// so take the product-id(s) from the first valid efix in the list
		// and use that to compare to the rest of the efixes in the list
		// 
		efixDriver eDriver = null;
		for (int j = 0; j < numIds; j++)
		{
			String fixId = (String) args.getEfixList().elementAt(j);
			eDriver = wpsHistory.getEFixDriverById(fixId);
		
			if(eDriver != null)
				break;
		}
		// if eDriver still is null, that means all efixes in the list do not exist.
		if(eDriver == null)
		{
			throw new Exception(UpdateReporter.getSilentString("efix.does.not.exist.all"));
		}
		int prodPrereqCount = eDriver.getProductPrereqCount();
		ArrayList prodIdList = new ArrayList();
		
		for(int i = 0; i < prodPrereqCount; i++)
		{
			String prodId = eDriver.getProductPrereq(i).getProductId();
			// any efixes in the list must match one of the product-id in this id list.
			prodIdList.add(prodId);
		}
		String proIdsInList = prodIdList.toString();
		
		for (int idNo = 0; idNo < numIds; idNo++) {
			String nextId = (String) args.getEfixList().elementAt(idNo);
			efixDriver nextDriver = wpsHistory.getEFixDriverById(nextId);

			if (nextDriver == null) {
				missingEfixes.add(nextId);
				continue;
			} else {
				Object[] nextData = new Object[9];
				packageUninstallData(nextData, nextDriver);
				idHash.put(new Integer(idNo), nextData);

				images.add(nextId);
			}
			//System.out.println("productprereq count = " + nextDriver.getProductPrereqCount());
			//System.out.println("productprereq product-id = " + nextDriver.getProductPrereq(0).getProductId());
			// test the current efix in the uninstall list, make sure it has at least one of the
			// required produc-id
			int currentProdCount = nextDriver.getProductPrereqCount();
			boolean match = false;
			
			for (int j = 0; j < currentProdCount; j++)
			{
				String currentProdId = nextDriver.getProductPrereq(j).getProductId();
				
				if( proIdsInList.indexOf(currentProdId) > -1)
				{
					match = true;
					continue;
				}
			}
			
			if(!match)
			{
//				need to uninstall mutiple fixes for mutiple application once time  Hongjun added at 0819.2010
//				throw new Exception(UpdateReporter.getSilentString("LC.efix.list.error"));
			}
// end, Han 06182007
			

		}

		if (missingEfixes.size() > 0)
			return;

		if (!args.prereqOverride) {
			Vector uninstallOrder = new Vector();

			// calling com.ibm.websphere.update.ptf.EFixBatchUpdater.testUninstallPrerequisites(Vector efixIds, Vector uninstallOrder, Vector errors)
			if (updater.testUninstallPrerequisites(images, uninstallOrder, prereqErrors))
				images = uninstallOrder;
		}
	}

	private boolean runUninstall() {
	        // comment 10/06/06 
                /*
		if (!updater.prepareEFixes(args.getEfixList()))
			return false;

		events = updater.uninstall(args.getEfixList());
                */
		//System.out.println("in silent.EfixInstaller.java, runUninstall()");
                //add 100606 to use install order and reverse it. Before it used order that user provided on command line.

                Vector reverseImage = new Vector();


                int imageSize = images.size();
                for (int i = imageSize - 1; i >= 0; i--) {
                  Object thisImage = (Object) images.elementAt(i);
                  reverseImage.addElement( thisImage );
                }
                // calling boolean com.ibm.websphere.update.ptf.EFixBatchUpdater.prepareEFixes(Vector efixIds)
                if (!updater.prepareEFixes(reverseImage))
                    return false;
                // calling Vector com.ibm.websphere.update.ptf.EFixBatchUpdater.uninstall(Vector efixIds)
                events = updater.uninstall(reverseImage);

                //stop add 100606


		boolean result;
		String resultMessage;

		if (updater.didFail(events)) {
			result = false;
			//System.out.println("in silent.EfixInstaller.java, runUninstall(), didFail()");
			updateEvent updateError = updater.selectFailingEvent(events);
			String errorLogName = updateError.getLogName();
			resultMessage = UpdateReporter.getSilentString("WUPD0027E", new Object[] { errorLogName });
                        UpdateInstaller.puiReturnCode = 6;   // eg. locked file

		} else {
			result = true;
			resultMessage = UpdateReporter.getSilentString("efix.uninstall.cmdline.success");
		}

		if (result)
			UpdateReporter.printUninstallerMessage(resultMessage);
		else
			UpdateReporter.printUninstallerErrorMessage(resultMessage);

		return result;
	}

	// Display utility ...
	private void displayDetails() {

		int numEFixes = idHash.size();
		if (numEFixes > 0) {
			System.out.println("[" + UpdateReporter.getBaseString("label.efix.detail.title") + "]");
			for (int efixNo = 0; efixNo < numEFixes; efixNo++) {
				Object[] nextData = (Object[]) idHash.get(new Integer(efixNo));

				String[] basicData =
					new String[] {
						(String) nextData[0],
						(String) nextData[1],
						(String) nextData[3],
						(String) nextData[6] };

				listBasicEFixData(basicData);

				System.out.println("");
			}
		} else {
			System.out.println(
				"[" + UpdateReporter.getBaseString("label.efix.detail.unavailable.title") + "]");
		}
	}

	public boolean doListInstalled() {
		boolean result;

		try {
			result = listInstalled();
			// throws IOException, IOServicesException, BaseHandlerException

		} catch (Exception e) {
			System.err.println(UpdateReporter.getSilentString("error.listing.installed.efixes"));
			//showException(e);
			result = false;
		}

		return result;
	}
    // added by kent for list ifix of certain feature
	public boolean doListInstalled(String feature) {
		boolean result;

		try {
			result = listInstalled(feature);
			// throws IOException, IOServicesException, BaseHandlerException

		} catch (Exception e) {
			System.err.println(UpdateReporter.getSilentString("error.listing.installed.efixes"));
			//showException(e);
			result = false;
		}

		return result;
	}
	// added by kent for list ifix of certain feature
	private boolean listInstalled(String feature) {
		if (!initialize())
			return false;


		Iterator efixes = wpsProduct.getEFixes();
		System.out.println("For application :" + feature);
		if (!efixes.hasNext()) {
			System.out.println(UpdateReporter.getSilentString("no.installed.efixes"));
		} else {
			int i = 0;
			while (efixes.hasNext()) {
				efix nextEFix = (efix) efixes.next();
				String component = nextEFix.getComponentName(0);
				if(feature.equalsIgnoreCase(component)){
				  i++;
				  listInstalledEFix(nextEFix);
				}
			}
			if (i == 0){
				System.out.println(UpdateReporter.getSilentString("no.installed.efixes"));
			}
		}

		return true;
	}

	private boolean listInstalled() {
		if (!initialize())
			return false;

		System.out.println(UpdateReporter.getSilentString("label.installed.efixes"));

		Iterator efixes = wpsProduct.getEFixes();

		if (!efixes.hasNext()) {
			System.out.println(UpdateReporter.getSilentString("no.installed.efixes"));
		} else {
			while (efixes.hasNext()) {
				efix nextEFix = (efix) efixes.next();
				listInstalledEFix(nextEFix);
			}
		}

		return true;
	}

	private void listInstalledEFix(efix anEFix) {
		listBasicEFixData(new String[] { anEFix.getId()});
	}

	public boolean doListAvailable() {
		boolean result;

		try {
			result = listAvailable();
			// throws IOException, IOServicesException, BaseHandlerException

		} catch (Exception e) {
			System.out.println("");
			System.err.println(UpdateReporter.getSilentString("efix.dir.unable.to.locate", args.efixDir));
			//showException(e);
			result = false;
		}

		return result;
	}

	private boolean listAvailable() throws IOException, IOServicesException, BaseHandlerException {
		if (!initialize())
			return false;

		System.out.println("");
		System.out.println(UpdateReporter.getSilentString("get.available.efixes", args.efixDir));

		initializeRepository(args.efixDir);
		activateRepository(repository);

		Enumeration imagesEnum = imageTable.keys();

		if (!imagesEnum.hasMoreElements()) {
			System.out.println(UpdateReporter.getSilentString("no.efixes.available"));
			return true;
		}

		System.out.println(
			UpdateReporter.getSilentString("available.efix.count", Integer.toString(images.size())));
		System.out.println(UpdateReporter.getSilentString("available.count.key"));

		int efixCount = 0;

		while (imagesEnum.hasMoreElements()) {
			efixCount++;

			String nextEFixId = (String) imagesEnum.nextElement();
			EFixImage nextImage = (EFixImage) imageTable.get(nextEFixId);
			String nextEFixJar = nextImage.getJarName();

			nextImage.prepareDriver();
			nextImage.prepareComponents();

			int[] componentCounts = countInstallableComponents(nextImage);

			int total = componentCounts[TOTAL_OFFSET],
				installed = componentCounts[TOTAL_INSTALLED_OFFSET],
				installable = componentCounts[TOTAL_INSTALLABLE_OFFSET];

			String efixText =
				UpdateReporter.getSilentString(
					"efix.component.state",
					new String[] {
						Integer.toString(efixCount),
						nextEFixId,
						Integer.toString(total),
						Integer.toString(installed),
						Integer.toString(installable),
						nextEFixJar });
			System.out.println(efixText);
		}

		return true;
	}

	private void listBasicEFixData(String[] efixData) {
		System.out.println("  " + UpdateReporter.getBaseString("label.details.efixId") + efixData[0]);

		if (efixData.length == 1)
			return;

		System.out.println("  " + UpdateReporter.getBaseString("label.details.build.date") + efixData[1]);
		System.out.println(
			"  " + UpdateReporter.getBaseString("label.details.build.ver") + " " + efixData[3]);
		System.out.println(
			"  " + UpdateReporter.getBaseString("label.details.short.description") + " " + efixData[2]);
	}

	private void packageInstallData(Object efixData[], efixDriver driver, EFixImage image) {
		packageCommonData(efixData, driver);
		efixData[2] = getInstallState(driver, image);
	}

	private void packageUninstallData(Object[] efixData, efixDriver driver) {
		packageCommonData(efixData, driver);

		efixData[2] = getUninstallState(driver);
	}

	private void packageCommonData(Object[] efixData, efixDriver driver) {
		efixData[0] = driver.getId();
		efixData[1] = driver.getBuildDate();
		// efixData[2] = installState;
		efixData[3] = driver.getShortDescription();
		efixData[4] = driver.getLongDescription();
		efixData[5] = driver.getAPARNumber();
		efixData[6] = driver.getBuildVersion();
		efixData[7] = ""; // pmrNum
		efixData[8] = getPrerequisiteText(driver);
	}

	private String getInstallState(efixDriver driver, EFixImage image) {
		String installState;

		String efixID = driver.getId();

		if (wpsHistory.efixAppliedPresent(efixID)) {
			if (isFullyInstalled(efixID, image))
				installState = "installed";
			else
				installState = "partially_installed";

		} else {
			installState = "not_installed";
		}

		return installState;
	}

	private String getUninstallState(efixDriver driver) {
		String installState;

		efixApplied applied = wpsHistory.getEFixAppliedById(driver.getId());

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
		int numPrereqs = driver.getEFixPrereqCount();

		StringBuffer resultBuffer = new StringBuffer();

		String negativeEfix = UpdateReporter.getSilentString("label.negative.efix");
		String efixSeparator = UpdateReporter.getSilentString("label.efix.separator");

		for (int prereqNo = 0; prereqNo < numPrereqs; prereqNo++) {
			efixPrereq nextPrereq = driver.getEFixPrereq(prereqNo);

			if (prereqNo > 0)
				resultBuffer.append(efixSeparator);

			if (nextPrereq.getIsNegativeAsBoolean())
				resultBuffer.append(negativeEfix);

			resultBuffer.append(nextPrereq.getEFixId());
		}

		return resultBuffer.toString();
	}

}
