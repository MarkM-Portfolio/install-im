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


/* 
 * ClassName: EFixInstallerOnly
 * Abstract: A commandline based installer for eFixes
 * 
 *
 * History 1.22, 12/16/02
 *
 * 30-Aug-2002 Initial Version
 *
 * 24-Oct-2002 Added test for completely installed efixes.
 *
 * 26-Oct-2002 Made .property test more precise.
 *
 * 27-Oct-2002 Overall review & update for usability.
 *
 * 28-Oct-2002 Fixed NLS lookup of 'no.efixes.available'.
 *
 * 03-Nov-2002 Changed to display update time in international format.
 *
 * 21-Nov-2002 Updated to include -uninstallAll and -efixJar options.
 *
 * 26-Nov-2002 Updated to handle '-' correctly when scanning efix ids & jar names.
 *
 * 15-Dec-2002 Defect 155567: Updated to force message ID's on particular messages,
 *             as required by EE.
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.formatters.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.BaseHandlerException;
import com.ibm.websphere.product.xml.CalendarUtil;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.product.xml.extension.extension;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.product.xml.websphere.websphere;
import com.ibm.websphere.update.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.efix.*;
import com.ibm.websphere.update.efix.prereq.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import com.ibm.websphere.update.msg.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.SAXParseException;

/**
 *  
 */
public class EFixInstallerOnly
{
    // Program versioning ...

	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "@BUILD_VERSION@" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "@BUILD_DATE@" ;

    public static final String getUpdateStamp()
    {
        return com.ibm.websphere.update.ioservices.CalendarUtil.formatCMVCDate(pgmUpdate);
    }

    // Section for retriving base installer messages ...

    public static final String baseBundleId =
    	 "com.ibm.lconn.update.msg.messages";

    public static final ResourceBundle baseMsgs;

    static {
        ResourceBundle retrievedBundle;

        try {
            retrievedBundle = ResourceBundle.getBundle(baseBundleId);
            // throws MissingResourceException

        } catch (MissingResourceException e) {
            retrievedBundle = null;
        }

        baseMsgs = retrievedBundle;
    }

    protected static ResourceBundle getBaseBundle() {
        return baseMsgs;
    }

    protected static String getBaseString(String msgCode) {

        if (baseMsgs == null)
            return baseBundleId + ":" + msgCode;

        try {
            return baseMsgs.getString(msgCode);

        } catch (MissingResourceException ex) {
            return baseBundleId + ":" + msgCode;
        }
    }

    protected static String getBaseString(String msgCode, Object arg) {

        String rawMessage = getBaseString(msgCode);
        Object[] msgArgs = new Object[] { arg };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getBaseString(String msgCode, Object arg1, Object arg2) {

        String rawMessage = getBaseString(msgCode);
        Object[] msgArgs = new Object[] { arg1, arg2 };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getBaseString(String msgCode, Object[] msgArgs) {

        String rawMessage = getBaseString(msgCode);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Section for silent installer specific messages.

    public static final String silentBundleId =
        "com.ibm.websphere.update.silent.SilentInstaller";

    public static final ResourceBundle silentMsgs;

    static {
        ResourceBundle retrievedBundle;

        try {
            retrievedBundle = ResourceBundle.getBundle(silentBundleId);
            // throws MissingResourceException

        } catch (MissingResourceException e) {
            retrievedBundle = null;
        }

        silentMsgs = retrievedBundle;
    }

    protected static ResourceBundle getSilentBundle() {
        return silentMsgs;
    }

    protected static String getDefaultedString(String msgCode, String defaultMsg)
    {
        if (silentMsgs == null)
            return defaultMsg;

        try {
            return silentMsgs.getString(msgCode);
        } catch (MissingResourceException ex) {
            return defaultMsg;
        }
    }

    protected static String getDefaultedString(String msgCode, String defaultMsg, Object[] msgArgs)
    {
        String rawMessage = getDefaultedString(msgCode, defaultMsg);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getSilentString(String msgCode)
    {
        if (silentMsgs == null)
            return silentBundleId + ":" + msgCode;

        try {
            return silentMsgs.getString(msgCode);

        } catch (MissingResourceException ex) {
            return silentBundleId + ":" + msgCode;
        }
    }

    protected static String getSilentString(String msgCode, Object arg)
    {
        String rawMessage = getSilentString(msgCode);
        Object[] msgArgs = new Object[] { arg };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getSilentString(String msgCode, Object arg1, Object arg2)
    {
        String rawMessage = getSilentString(msgCode);
        Object[] msgArgs = new Object[] { arg1, arg2 };

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getSilentString(String msgCode, Object[] msgArgs) {

        String rawMessage = getSilentString(msgCode);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    // Messaging utility ...

    protected static void printInstallerMessage(String msg, PrintStream message) {
        message.println( "[" + getBaseString("label.installing") + "] : " + msg );
    }

    protected static void printUninstallerMessage(String msg, PrintStream message) {
        message.println("[" + getBaseString("label.uninstalling") + "] : " + msg);
    }

    protected static void printInstallerErrorMessage(String errMsg, PrintStream message) {
        message.println("[" + getBaseString("label.install.failed") + "] : " + errMsg);
    }

    protected static void printUninstallerErrorMessage(String errMsg, PrintStream message) {
        message.println("[" + getBaseString("label.uninstall.failed") + "] : " + errMsg);
    }

    protected static void printInstallerMessage(String msg) {
        printInstallerMessage(msg, System.out);
    }

    protected static void printUninstallerMessage(String msg) {
        printUninstallerMessage(msg, System.out);
    }

    protected static void printInstallerErrorMessage(String errMsg) {
        printInstallerErrorMessage(errMsg, System.err);
    }

    protected static void printUninstallerErrorMessage(String errMsg) {
        printUninstallerErrorMessage(errMsg, System.err);
    }

    // Logging utility ...

    protected static void log(String msg) {
        Logger.setDebugOn(true);
        Logger.log(msg);
    }

    // Error Handling ...

    protected static void handleRecoverableErrors( Iterator recoverableErrors,
                                                 PrintStream output,
                                                 boolean printStack )
    {
        while (recoverableErrors.hasNext()) {
            SAXParseException nextError =
                (SAXParseException) recoverableErrors.next();

            String errorText = getSilentString
                ("WUPD0017E", new Object[] { nextError.getSystemId(),
                                             nextError.getPublicId(),
                                             new Integer(nextError.getLineNumber()),
                                             new Integer(nextError.getColumnNumber()),
                                             nextError.toString()});
            output.println(errorText);

            if (printStack)
                nextError.printStackTrace(output);
        }
    }

    protected static void handleWarnings(Iterator warnings,
                                       PrintStream output, boolean printStack )
    {
        while (warnings.hasNext()) {
            SAXParseException nextWarning = (SAXParseException) warnings.next();

            String warningText = getSilentString
                ("WUPD0018E", new Object[] { nextWarning.getSystemId(),
                                             nextWarning.getPublicId(),
                                             new Integer(nextWarning.getLineNumber()),
                                             new Integer(nextWarning.getColumnNumber()),
                                             nextWarning.toString()});
            output.println(warningText);

            if (printStack)
                nextWarning.printStackTrace(output);
        }
    }

    protected static int handleFatalExceptions(int errorNo,
                                             Iterator fatalExceptions,
                                             PrintStream output, boolean printStack )
    {
        while (fatalExceptions.hasNext()) {
            Exception nextFatalException = (Exception) fatalExceptions.next();
            errorNo++;

            String errorMessage = getSilentString("listing.nested.error",
                                                  Integer.toString(errorNo),
                                                  nextFatalException.toString());

            output.println(errorMessage);

            if (printStack)
                nextFatalException.printStackTrace(output);
        }

        return errorNo;
    }

    protected static boolean handleErrors(WPProduct wpsProduct, WPHistory wpsHistory,
                                        PrintStream output, boolean printStack )
    {
        int numProductErrors = wpsProduct.numRecoverableErrors(),
            numHistoryErrors = wpsHistory.numRecoverableErrors();

        if ((numProductErrors > 0) || (numHistoryErrors > 0)) {
            output.println( getSilentString("WUPD0019E") );

            handleRecoverableErrors(
                wpsProduct.getRecoverableErrors(),
                output,
                printStack);
            handleRecoverableErrors(
                wpsHistory.getRecoverableErrors(),
                output,
                printStack);
        }

        int numProductWarnings = wpsProduct.numWarnings(),
            numHistoryWarnings = wpsHistory.numWarnings();

        if ((numProductWarnings > 0) || (numHistoryWarnings > 0)) {
            output.println( getSilentString("WUPD0020E") );

            handleWarnings(wpsProduct.getWarnings(), output, printStack);
            handleWarnings(wpsHistory.getWarnings(), output, printStack);
        }

        int numProductFatalExceptions = wpsProduct.numExceptions(),
            numHistoryFatalExceptions = wpsHistory.numExceptions();

        int numFatalExceptions =
            numProductFatalExceptions + numHistoryFatalExceptions;

        if (numFatalExceptions == 0)
            return true;

        output.println( getBaseString("label.product.directory.error"));
        output.println( getSilentString((numFatalExceptions == 1) ? "WUPD0015E" : "WUPD0016E") );

        int errorNo = 0;

        errorNo =
            handleFatalExceptions(
                errorNo,
                wpsProduct.getExceptions(),
                output,
                printStack);
        errorNo =
            handleFatalExceptions(
                errorNo,
                wpsHistory.getExceptions(),
                output,
                printStack);

        return false;
    }

    // Copyright display ...

    protected static void printCopyright()
    {
        System.out.println(
            getDefaultedString(
                "websphere.copyright.statement",
                "websphere.copyright.statement: Copyright (c) IBM Corporation 2002,2012; All rights reserved.") );
        System.out.println(
            getDefaultedString(
                "websphere.version.release",
                "IBM Connections") );
        System.out.println(
            getDefaultedString(
                "websphere.install.cmdline.info",
                "websphere.install.cmdline.info: eFix Installer Version {0}, Dated {1}",
                new Object[] { pgmVersion, getUpdateStamp() }) );
        System.out.println();
    }

    public static void main(String[] args)
    {
        main(args, true); // Will never return!
    }

    public static int main(String[] args, boolean doExit)
    {
        int result = process(args);

        if (doExit)
            System.exit(result);

        return result;
    }

    protected static int process(String[] cmdLineArgs)
    {
        printCopyright();

        InstallerArgs args = new InstallerArgs();

        args.parse(cmdLineArgs);

        if (args.errorArg != null) {
            System.err.println( getSilentString(args.errorCode, args.errorArg) );
            args.showUsage = true;
        }

        if (args.showHelp) {
            System.out.println( getSilentString("efix.install.cmdline.help") );
            return 0;

        } else if (args.showUsage) {
            System.out.println( getSilentString("efix.install.cmdline.usage") );
            return 0;
        } 

        if (args.install) {
            if (!args.installDirInput || !args.efixDirInput || !(args.efixesInput || args.efixJarsInput)) {
                System.out.println( getSilentString("efix.install.cmdline.required.args") );
                return -1;
            } else {
                EFixInstallerOnly installer = new EFixInstallerOnly(args);
                return (installer.doInstall() ? 0 : -1);
            }

        } else if (args.uninstall) {
            if (!args.installDirInput || !args.efixesInput) {
                System.out.println( getSilentString("efix.uninstall.cmdline.required.args") );
                return -1;
            } else {
                EFixInstallerOnly installer = new EFixInstallerOnly(args);
                return (installer.doUninstall() ? 0 : -1);
            }
        } else if (args.uninstallAll) {
            EFixInstallerOnly installer = new EFixInstallerOnly(args);
            return (installer.doUninstall() ? 0 : -1);

        } else {
            if (args.installDirInput) {
                EFixInstallerOnly installer = new EFixInstallerOnly(args);

                if (!installer.doListInstalled())
                    return -1;

                if (args.efixDirInput) {
                    if (!installer.doListAvailable())
                        return -1;
                    else
                        return 0;
                } else {
                    return 0;
                }

            } else {
                if (args.efixDirInput)
                    System.out.println( getSilentString("efix.list.installable.requires.product") );
                else
                    System.out.println( getSilentString("no.operation") );

                return -1;
            }
        }
    }

    public EFixInstallerOnly(InstallerArgs args)
    {
        this.args = args;
    }

    protected InstallerArgs args;

    // Processing structures ...

    protected Vector prereqErrors    = new Vector();
    protected Vector supersededInfo    = new Vector();
    protected Vector missingEfixes   = new Vector();
    protected Vector alreadyComplete = new Vector();
    protected Vector componentNotPresent = new Vector();


    protected Vector images          = new Vector();

    protected HashMap idHash         = new HashMap();

    protected Vector events          = new Vector();


    // Product access ...

    protected boolean didInitialize = false;

    protected boolean initialize()
    {
        if (didInitialize)
            return true;

        return initializeProduct();
    }

    protected WPProduct wpsProduct;
    protected WPHistory wpsHistory;

    protected boolean initializeProduct()
    {
        wpsProduct = new WPProduct(args.installDir);

        String productDirName = wpsProduct.getProductDirName();
        String versionDirName = wpsProduct.getVersionDirName();
        String historyDirName = WPHistory.determineHistoryDirName(versionDirName);

        wpsHistory = new WPHistory(productDirName, versionDirName, historyDirName);

        return handleErrors(wpsProduct, wpsHistory, System.err, args.printStack);
    }

    protected HashMap componentUpdates = null;

    protected int setComponentUpdates(efixDriver aDriver) {
        componentUpdates = new HashMap();

        int numUpdates = aDriver.getComponentUpdateCount();

        for ( int compNo = 0; compNo < numUpdates; compNo++ ) {
            componentUpdate nextUpdate = aDriver.getComponentUpdate(compNo);
            String nextComponentName = nextUpdate.getComponentName();

            componentUpdates.put(nextComponentName, nextUpdate);
        }

        return numUpdates;
    }

    protected componentUpdate getComponentUpdate(String componentName) {
        return (componentUpdate) componentUpdates.get(componentName);
    }

    protected boolean updateIsAdd(String componentName) {
        componentUpdate selectedUpdate = getComponentUpdate(componentName);

        if ( selectedUpdate == null )
            return false;

        return ( selectedUpdate.getUpdateTypeAsEnum() ==
                 enumUpdateType.ADD_UPDATE_TYPE );
    }

    protected boolean updateIsPresent(String componentName) {
        return wpsProduct.componentPresent(componentName);
    }

    // Install state helpers ...

    protected static final int TOTAL_OFFSET = 0 ;
    // Install state helpers ...

    protected static final int TOTAL_INSTALLED_OFFSET = 1 ;
    // Install state helpers ...

    protected static final int TOTAL_INSTALLABLE_OFFSET = 2 ;
        
    protected int[] countInstallableComponents(efixImage anImage)
    {
        int numUpdates = setComponentUpdates( anImage.getEFixDriver() );

        int[] counts = new int[] { 0, 0, 0 };
        Vector currentComponentNotPresent = new Vector();

        String useEFixId = anImage.getEFixId();

        Iterator useComponentNames = anImage.getComponentNames().iterator();
        while (useComponentNames.hasNext()) {
            String nextComponentName = (String) useComponentNames.next();

            counts[TOTAL_OFFSET]++;

            boolean isAdd = updateIsAdd(nextComponentName);
            boolean isPresent = updateIsPresent(nextComponentName);

            if ( (isAdd && !isPresent) || (!isAdd && isPresent) ) {
                if (wpsHistory.efixComponentAppliedPresent(useEFixId, nextComponentName)) {
                    counts[TOTAL_INSTALLED_OFFSET]++;
                } else {
                    counts[TOTAL_INSTALLABLE_OFFSET]++;
                    images.add(anImage);
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

    protected boolean isFullyInstalled(String efixID, efixImage image)
    {
        Vector compNames = image.getComponentNames();

        int compCount = image.getComponentCount();
        boolean allArePresent = true;

        for (int compNo = 0; allArePresent && (compNo < compCount); compNo++) {
            String compName = (String) compNames.elementAt(compNo);
            allArePresent = wpsHistory.efixComponentAppliedPresent(efixID, compName);
        }

        return allArePresent;
    }

    // EFix image access ...

    protected efixImageRepository repository;

    protected void initializeRepository(String reposDir) {
        repository = new efixImageRepository(new IOService(),
                                             wpsProduct.getDTDDirName(),
                                             reposDir);
    }

    // Update widget access ...

    protected efixBatchUpdater updater;

    protected static final int INSTALL = 0;
    protected static final int UNINSTALL = 1;

    protected void initializeUpdaterForInstall() {
        updater = new efixBatchUpdater(wpsProduct, wpsHistory,
                                       new StandardNotifier(INSTALL),
                                       new IOService());
    }

    protected void initializeUpdaterForUninstall() {
        updater = new efixBatchUpdater(wpsProduct, wpsHistory,
                                       new StandardNotifier(UNINSTALL),
                                       new IOService());
    }


    private boolean hasImagesToInstall() {
        return (images.size() > 0);
    }
    
    // Error handling utility ...

    protected boolean displayMissing()
    {
        int numMissing = missingEfixes.size();
        if (numMissing > 0) {
            if (!hasImagesToInstall()) {
                System.out.println("");
                System.out.println(UpdateReporter.getSilentString("efix.does.not.exist"));
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

    protected boolean displayAlreadyComplete()
    {
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

    protected boolean displayPrereqErrors()
    {
        if (prereqErrors.size() == 0)
            return false;

        int errorSize = prereqErrors.size();
        for (int i = 0; i < errorSize; i++) {
            if (i > 0) {
                System.out.println();
                System.out.println();
            }

            System.out.println( (String) (prereqErrors.elementAt(i)) );
        }

        return true;
    }

    protected void showException(Exception e)
    {
        try {
            System.err.println(e.getMessage());

        } catch (Throwable th) {
            System.err.println(e.getClass().getName());
        }

        if (args.printStack)
            e.printStackTrace(System.err);
    }

    protected boolean doInstall()
    {
            if (!initialize())
            return false;

        try {
            initializeRepository(args.efixDir);
            initializeUpdaterForInstall();
            setupInstall();

        } catch (Exception e) {
            System.err.println(UpdateReporter.getSilentString("error.setup.install.efixes"));
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
                displayAlreadyComplete();
                return runInstall();
            }
        } else {
            displayMissing(); //should also display efixes without corresponding component files
            displayAlreadyComplete();
            return false;
        }
    }

    protected void setupInstall()
        throws IOException, IOServicesException, BaseHandlerException
    {
        repository.prepare();
        Hashtable imageTable = repository.getEFixImages();

        // Not the most efficient processing, since all of the
        // jars are being processed, when only a subset need to
        // be used.

        if (args.efixJarsInput) {
            Vector absoluteJarNames = new Vector();

            for (int jarNo = 0; jarNo < args.efixJarList.size(); jarNo++) {
                String jarName = (String) args.efixJarList.elementAt(jarNo);
                jarName = args.efixDir + File.separator + jarName;

                File jarFile = new File(jarName);
                jarName = jarFile.getAbsolutePath();

                absoluteJarNames.add(jarName);
            }

            Iterator images = imageTable.values().iterator();

            while (images.hasNext()) {
                efixImage nextImage = (efixImage) images.next();
                String candidateJarName = nextImage.getJarName();

                boolean found = false;

                for (int jarNo = 0; !found && (jarNo < absoluteJarNames.size()); jarNo++) {
                    String nextJarName = (String) absoluteJarNames.elementAt(jarNo);
                    found = nextJarName.equals(candidateJarName);
                }

                if (found)
                    args.maybeAddEFixFromJar(nextImage.getEFixId());
            }
        }

        int numIds = args.efixList.size();
        for (int idNo = 0; idNo < numIds; idNo++) {
            String nextId = (String) args.efixList.elementAt(idNo);

            efixImage nextImage = (efixImage) imageTable.get(nextId);

            if (nextImage == null) {
                missingEfixes.add(nextId);

            } else {
                nextImage.prepareEFixDriver();
                nextImage.prepareComponents();

                efixDriver nextDriver = nextImage.getEFixDriver();

                int[] installCounts = countInstallableComponents(nextImage);
                Object[] nextData = new Object[9];
                packageInstallData(nextData, nextDriver, nextImage);
                idHash.put(new Integer(idNo), nextData);

            }
        }

        if ((missingEfixes.size() > 0) || (alreadyComplete.size() > 0))
            return;

        if (!args.prereqOverride) {
            Vector installOrder = new Vector();

            if (updater.testInstallPrerequisites(images, installOrder, prereqErrors, supersededInfo))
                images = installOrder;
        }
    }

    protected boolean runInstall()
    {
        if (!updater.prepareImages(images))
            return false;

        events = updater.install(images);

        boolean result;
        String resultMessage;

        if (updater.wasCancelled(events)) {
            result = false;

            updateEvent updateError = updater.selectCancelledEvent(events);
            String errorLogName = updateError.getLogName();
            resultMessage = getSilentString("WUPD0025E", new Object[] { errorLogName });

        } else if (updater.didFail(events)) {
            result = false;

            updateEvent updateError = updater.selectFailingEvent(events);
            String errorLogName = updateError.getLogName();
            resultMessage = getSilentString("WUPD0026E", new Object[] { errorLogName });

        } else {
            result = true;
            resultMessage = "WUPD0038I: " + getSilentString("efix.install.cmdline.success"); // 155567
        }

        if (result)
            printInstallerMessage(resultMessage);
        else
            printInstallerErrorMessage(resultMessage);

        return result;
    }

    protected boolean doUninstall()
    {
            if (!initialize())
            return false;

        try {
            initializeUpdaterForUninstall();
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

    protected void setupUninstall()
        throws IOException, IOServicesException, BaseHandlerException
    {
        if ( args.uninstallAll ) {
            Iterator efixes = wpsProduct.getEFixes();

            while ( efixes.hasNext() ) {
                efix nextEFix = (efix) efixes.next();
                args.maybeAddEFix(nextEFix.getId());
            }
        }

        int numIds = args.efixList.size();
        for (int idNo = 0; idNo < numIds; idNo++) {
            String nextId = (String) args.efixList.elementAt(idNo);
            efixDriver nextDriver = wpsHistory.getEFixDriverById(nextId);

            if (nextDriver == null) {
                missingEfixes.add(nextId);

            } else {
                Object[] nextData = new Object[9];
                packageUninstallData(nextData, nextDriver);
                idHash.put(new Integer(idNo), nextData);

                images.add(nextId);
            }
        }

        if (missingEfixes.size() > 0)
            return;

        if (!args.prereqOverride) {
            Vector uninstallOrder = new Vector();

            if (updater.testUninstallPrerequisites(images, uninstallOrder, prereqErrors))
                images = uninstallOrder;
        }
    }

    protected boolean runUninstall()
    {
        if (!updater.prepareEFixes(args.efixList))
            return false;

        events = updater.uninstall(args.efixList);

        boolean result;
        String resultMessage;

        if (updater.didFail(events)) {
            result = false;

            updateEvent updateError = updater.selectFailingEvent(events);
            String errorLogName = updateError.getLogName();
            resultMessage = getSilentString("WUPD0027E", new Object[] { errorLogName });

        } else {
            result = true;
            resultMessage = "WUPD0039I: " + getSilentString("efix.uninstall.cmdline.success"); // 155567
        }

        if (result)
            printUninstallerMessage(resultMessage);
        else
            printUninstallerErrorMessage(resultMessage);

        return result;
    }

    // Display utility ...

    protected void displayDetails()
    {
        System.out.println("[" + getBaseString("label.efix.detail.title") + "]");

        int numEFixes = idHash.size();
        for (int efixNo = 0; efixNo < numEFixes; efixNo++) {
            Object[] nextData = (Object[]) idHash.get(new Integer(efixNo));

            String[] basicData = new String[] {
                (String) nextData[0],
                (String) nextData[1],
                (String) nextData[3],
                (String) nextData[6]
            };

            listBasicEFixData(basicData);

            System.out.println("");
        }
    }

    protected boolean doListInstalled()
    {
        boolean result;

        try {
            result = listInstalled();
            // throws IOException, IOServicesException, BaseHandlerException

        } catch (Exception e) {
            System.err.println( getSilentString("error.listing.installed.efixes") );
            showException(e);
            result = false;
        }

        return result;
    }
    protected boolean listInstalled()
    {
        if (!initialize())
            return false;

        System.out.println( getSilentString("label.installed.efixes") );

        Iterator efixes = wpsProduct.getEFixes();

        if ( !efixes.hasNext() ) {
            System.out.println( "WUPD0037E: " + getSilentString("no.installed.efixes") ); // 155567
        } else {
            while ( efixes.hasNext() ) {
                efix nextEFix = (efix) efixes.next();
                listInstalledEFix(nextEFix);
            }
        }

        return true;
    }

    protected void listInstalledEFix(efix anEFix)
    {
        listBasicEFixData( new String[] { anEFix.getId() } );
    }

    protected boolean doListAvailable()
    {
        boolean result;

        try {
            result = listAvailable();
            // throws IOException, IOServicesException, BaseHandlerException

        } catch (Exception e) {
            System.err.println( getSilentString("error.listing.available.efixes") );
            showException(e);
            result = false;
        }

        return result;
    }

    protected boolean listAvailable()
        throws IOException, IOServicesException, BaseHandlerException
    {
        if (!initialize())
            return false;

        System.out.println(getSilentString("get.available.efixes", args.efixDir));

        initializeRepository(args.efixDir);
        repository.prepare();

        Hashtable images = repository.getEFixImages();

        Enumeration imagesEnum = images.keys();

        if (!imagesEnum.hasMoreElements()) {
            System.out.println(getSilentString("no.efixes.available"));
            return true;
        }

        System.out.println(getSilentString("available.efix.count", Integer.toString(images.size())));
        System.out.println(getSilentString("available.count.key"));

        int efixCount = 0;

        while (imagesEnum.hasMoreElements()) {
            efixCount++;

            String nextEFixId = (String) imagesEnum.nextElement();
            efixImage nextImage = (efixImage) images.get(nextEFixId);
            String nextEFixJar = nextImage.getJarName();

            nextImage.prepareEFixDriver();
            nextImage.prepareComponents();

            int[] componentCounts = countInstallableComponents(nextImage);

            int total = componentCounts[TOTAL_OFFSET],
                installed = componentCounts[TOTAL_INSTALLED_OFFSET],
                installable = componentCounts[TOTAL_INSTALLABLE_OFFSET];

            String efixText = getSilentString("efix.component.state",
                                              new String[] { Integer.toString(efixCount),
                                                             nextEFixId,
                                                             Integer.toString(total),
                                                             Integer.toString(installed),
                                                             Integer.toString(installable),
                                                             nextEFixJar });
            System.out.println(efixText);
        }

        return true;
    }

    protected void listBasicEFixData(String[] efixData)
    {
        System.out.println( "WUPD0035I: " + getBaseString("label.details.efixId") + efixData[0] ); // 155567

        if (efixData.length == 1)
            return;

        System.out.println( getBaseString("label.details.build.date") + efixData[1] );
        System.out.println( getBaseString("label.details.short.description") + " " + efixData[2] );
        System.out.println( getBaseString("label.details.build.ver") + " " + efixData[3] );
    }

    protected void packageInstallData(Object efixData[], efixDriver driver, efixImage image)
    {
        packageCommonData(efixData, driver);
        efixData[2] = getInstallState(driver, image);
    }
    
    protected void packageUninstallData(Object[] efixData, efixDriver driver)
    {
        packageCommonData(efixData, driver);

        efixData[2] = getUninstallState(driver);
    }

    protected void packageCommonData(Object[] efixData, efixDriver driver)
    {
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

    protected String getInstallState(efixDriver driver, efixImage image)
    {
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

    protected String getUninstallState(efixDriver driver)
    {
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

    protected String getPrerequisiteText(efixDriver driver)
    {
        int numPrereqs = driver.getEFixPrereqCount();

        StringBuffer resultBuffer = new StringBuffer();

        String negativeEfix = getSilentString("label.negative.efix");
        String efixSeparator = getSilentString("label.efix.separator");

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

    /**
	 *  
	 */
    protected static class InstallerArgs
    {
        public boolean install = false;
        public boolean uninstall = false;

        public boolean uninstallAll = false;

        public boolean useProps = false;
        public String respFile = null;

        public boolean installDirInput = false;
        public String installDir = null;

        public boolean efixDirInput = false;
        public String efixDir = null;

        public boolean efixesInput = false;
        public Vector efixList = new Vector();

        public boolean efixJarsInput = false;
        public Vector efixJarList = new Vector();

        public boolean prereqOverride = false;
        public boolean displayEfixDetails = false;

        public boolean showHelp = false;
        public boolean showUsage = false;

        public boolean printStack = false;

        public String errorArg = null;
        public String errorCode = null;
        public boolean isComplete = false;

        protected void setPropertyFile(String fileName)
        {
            useProps = true;
            respFile = fileName;

            System.out.println( getSilentString("property.file.specified", respFile) );
        }

        /**
		 * @param installDir  the installDir to set
		 * @uml.property  name="installDir"
		 */
        protected void setInstallDir(String dirName)
        {
            installDirInput = true;
            installDir = dirName;

            System.out.println( getSilentString("install.dir.specified", installDir) );
        }

        protected void setEFixDir(String dirName)
        {
            efixDirInput = true;
            efixDir = dirName;
            System.out.println( getSilentString("efix.dir.specified", efixDir) );
        }

        protected boolean maybeAddEFix(String tag)
        {
            if (tag.indexOf("-") == 0) // Cannot start with a dash.
                return false;

            efixesInput = true;
            efixList.add(tag);

            System.out.println( getSilentString("efix.specified", tag) );

            return true;
        }

        protected boolean maybeAddEFixFromJar(String tag) {
            efixesInput = true;     
            efixList.add(tag);

            System.out.println(UpdateReporter.getSilentString("efix.specified", tag));

            return true;
        }

        protected boolean maybeAddEFixJar(String tag)
        {
            if (tag.indexOf("-") == 0) // Cannot start with a dash.
                return false;

            efixJarsInput = true;
            efixJarList.add(tag);

            System.out.println( getSilentString("efix.jar.specified", tag) ); // NEW

            return true;
        }

        protected void setDisplayDetails()
        {
            displayEfixDetails = true;

            System.out.println( "WUPD0036I: " + getSilentString("display.efix.details") ); // 155567
        }

        protected void setToInstall()
        {
            install = true;

            System.out.println( getSilentString("will.install") );
        }

        protected void setToUninstall()
        {
            uninstall = true;

            System.out.println( getSilentString("will.uninstall") );
        }

        protected void setToUninstallAll()
        {
            uninstallAll = true;

            System.out.println( getSilentString("will.uninstall.all") ); // NEW
        }

        protected void doOverride()
        {
            prereqOverride = true;

            System.out.println( getSilentString("will.override.prereqs") );
        }

        public void parse(String[] args)
        {
            int argNo = 0;
        
            while (!isComplete && (argNo < args.length)) {
                String nextArg = args[argNo++];

                if (nextArg.endsWith(".properties")) {
                    setPropertyFile(nextArg);

                    try {
                        parsePropertyFile(nextArg);
                    } catch (IOException e) {
                        isComplete = true;
                        errorArg = nextArg;
                        errorCode = "error.reading.property.file";
                    }

                } else if (nextArg.equalsIgnoreCase("-installDir")) {
                    if (argNo < args.length) {
                        setInstallDir(args[argNo++]);
                    } else {
                        isComplete = true;
                        errorArg = nextArg;
                        errorCode = "WUPD0021E";
                    }

                } else if (nextArg.equalsIgnoreCase("-efixDir")) {
                    if (argNo < args.length) {
                        setEFixDir(args[argNo++]);
                    } else {
                        isComplete = true;
                        errorArg = nextArg;
                        errorCode = "WUPD0022E";
                    }

                } else if (nextArg.equalsIgnoreCase("-efixes")) {
                    while ((argNo < args.length) && maybeAddEFix((args[argNo])))
                        argNo++;

                    if (!efixesInput) {
                        isComplete = true;
                        errorArg = nextArg;
                        errorCode = "WUPD0023E";
                    }

                } else if (nextArg.equalsIgnoreCase("-efixJars")) {
                    while ((argNo < args.length) && maybeAddEFixJar((args[argNo])))
                        argNo++;

                    if (!efixJarsInput) {
                        isComplete = true;
                        errorArg = nextArg;
                        errorCode = "WUPD0030E"; // NEW
                    }

                } else if (nextArg.equalsIgnoreCase("-efixDetails")) {
                    setDisplayDetails();

                } else if (nextArg.equalsIgnoreCase("-install")) {
                    setToInstall();

                } else if (nextArg.equalsIgnoreCase("-uninstall")) {
                    setToUninstall();

                } else if (nextArg.equalsIgnoreCase("-uninstallAll")) {
                    setToUninstallAll();
                    
                } else if (nextArg.equalsIgnoreCase("-prereqOverride")) {
                    doOverride();
                    
                } else if (nextArg.equalsIgnoreCase("-help") ||
                           nextArg.equalsIgnoreCase("-?") ||
                           nextArg.equalsIgnoreCase("/?") ||
                           nextArg.equalsIgnoreCase("/help")) {
                    isComplete = true;
                    showHelp = true;
                    
                } else if (nextArg.equalsIgnoreCase("-usage")) {
                    isComplete = true;
                    showUsage = true;
                    
                } else if (nextArg.equalsIgnoreCase("-printStack")) {
                    printStack = true;
                    
                } else {
                    isComplete = true;
                    errorArg = nextArg;
                    errorCode = "WUPD0024E";
                }
            }
        }

        public void parsePropertyFile(String propertyFile)
            throws IOException
        {
            Properties propsFile = new Properties();

            InputStream fs = new java.io.FileInputStream(respFile);
            propsFile.load(fs);
            fs.close();

            if (propsFile.getProperty("install") != null)
                install = true;

            if (propsFile.getProperty("uninstall") != null)
                uninstall = true;

            String propInstallDir = propsFile.getProperty("installDir");

            if (propInstallDir != null) {
                installDirInput = true;
                installDir = propInstallDir;
            }

            String propEFixDir = propsFile.getProperty("efixDir");

            if (propEFixDir != null) {
                efixDirInput = true;
                efixDir = propEFixDir;
            }

            String efixListProps = propsFile.getProperty("efixes");

            StringTokenizer st = new StringTokenizer(efixListProps, ",");
            while (st.hasMoreTokens()) {
                String efix = st.nextToken();
                maybeAddEFix(efix);
            }

            if (propsFile.getProperty("prereqOverride") != null)
                prereqOverride = true;

            if (propsFile.getProperty("efixDetails") != null)
                displayEfixDetails = true;
        }
    }

    /**
	 *  
	 */
    protected class StandardNotifier implements Notifier {
        protected final int action;

        public StandardNotifier(int action) {

            this.taskCount = 0;
            this.taskNumber = 0;
            this.action = action;
            this.bannerStack = new Vector();
        }

        protected int taskCount;

        /**
		 * @param taskCount  the taskCount to set
		 * @uml.property  name="taskCount"
		 */
        public void setTaskCount(int taskCount) {
            this.taskNumber = 0;
            this.taskCount = taskCount;
        }

        /**
		 * @return  the taskCount
		 * @uml.property  name="taskCount"
		 */
        public int getTaskCount() {
            return taskCount;
        }

        protected int taskNumber;

        /**
		 * @return  the taskNumber
		 * @uml.property  name="taskNumber"
		 */
        public int getTaskNumber() {
            return taskNumber;
        }

        protected int incrementTaskNumber() {
            return (++taskNumber);
        }

        protected Vector bannerStack;

        /**
		 * @return  the bannerStack
		 * @uml.property  name="bannerStack"
		 */
        protected Vector getBannerStack() {
            return bannerStack;
        }

        public void pushBanner(String banner) {
            getBannerStack().addElement(banner);
        }

        public String popBanner() {
            Vector useBannerStack = getBannerStack();
            int bannerLength = useBannerStack.size();

            if (bannerLength == 0) {
                return null;

            } else {
                String result =
                    (String) useBannerStack.elementAt(bannerLength - 1);
                useBannerStack.removeElementAt(bannerLength - 1);
                return result;
            }
        }

        public String collateBanners() {
            Vector useBannerStack = getBannerStack();
            int numBanners = useBannerStack.size();

            StringBuffer collation = new StringBuffer();

            for (int bannerNo = 0; bannerNo < numBanners; bannerNo++) {
                String nextBanner = (String) useBannerStack.elementAt(bannerNo);
                collation.append(nextBanner);
            }

            return collation.toString();
        }

        public String replaceBanner(String banner) {
            String oldBanner = popBanner();
            pushBanner(banner);
            return oldBanner;
        }

        public String beginTask() {
            incrementTaskNumber();

            return performTaskNotification(BEGIN_TASK);
        }

        public String endTask() {
            return performTaskNotification(END_TASK);
        }

        public boolean wasCancelled() {
            return false;
        }

        protected void println(String text)
        {
            if (action == INSTALL)
                printInstallerMessage(text);
            else
                printUninstallerMessage(text);
        }

        public static final boolean BEGIN_TASK = false ;
        public static final boolean END_TASK = true ;

        protected String performTaskNotification(boolean taskStatus)
        {
            StringBuffer outputBuffer = new StringBuffer();

            String msgId = ((taskStatus == BEGIN_TASK) ? "begin.task.out.of" : "end.task.out.of");
            String[] msgArgs = new String[] { Integer.toString(getTaskNumber()),
                                              Integer.toString(getTaskCount()) };
            String taskMsg = getSilentString(msgId, msgArgs);

            outputBuffer.append(taskMsg);
            outputBuffer.append(collateBanners());

            String notificationText = outputBuffer.toString();

            println(notificationText);

            return notificationText;
        }
    }
}
