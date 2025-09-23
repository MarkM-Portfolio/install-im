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
package com.ibm.websphere.update.delta;

/*
 * Update Applicator
 *
 * History 1.27, 7/29/05
 *
 * 25-Sep-2002 Removed static and special purpose code.
 *
 * 03-Nov-2002 Added quiet mode.
 *
 * 26-Nov-2002 Added support for asInstalled, asApplication,
 *             and asInstallable updates.
 *             Added support for perInstance updates.
 *
 * 14-Jan-2003 Fixed slashes in 'getEARPath'.
 *
 * 21-Jan-2003 Continued EAR processing fixed;
 *             Updated to use deployment information for installed EARs.
 *
 * 02-Feb-2003 Updated to include EAR metadata handling.
 *
 * 12-Mar-2003 Fixed 'recycleToMark'
 *
 * 16-Mar-2003 Fixed static 'readMacroSetupProps';
 *             Defect 161114
 *
 * 20-Mar-2003 Fixed infinite loop in metadata handling;
 *             Defect 161417
 * 04-Dec-2003 cdchoi iSeries enablement
 */

import com.ibm.lconn.ifix.EFixInstaller;
import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.update.delta.earutils.*;
import com.ibm.websphere.update.delta.warutils.*;
import com.ibm.websphere.update.ptf.OSUtil;
import com.ibm.websphere.update.silent.UpdateInstallerArgs;
import com.ibm.websphere.update.util.CommentedProperties;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import org.xml.sax.SAXException;

/*
 * Extractor
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/Extractor.java, wps.base.fix, wps5.fix
 *
 * @author: Steve Pritko
 * @version 1.24
 * Date: 4/6/05
 */

/**
 *  
 */
public class Extractor
{
    // Program versioning ...

   public static final String pgmVersion = "1.24" ;
    // Program versioning ...

   public static final String pgmUpdate = "4/6/05" ;

    // PQ59675 added new class for System Processing
    protected LocalSystem localSystem;

    // Process state ...

    // Command Line arguments ...

    // Misc. information loaded from the Product.XML file.

    protected boolean    loadedVersion       = false;
    protected String     startingVersion     = null;
    protected boolean    loadedEditionName   = false;
    protected String     editionName         = null;
    protected boolean    loadedEditionValue  = false;
    protected String     editionValue        = null;

    protected boolean    loadedBuildNumber   = false;
    protected String     startingBuildNumber = null;
    protected boolean    loadedBuildDate     = false;
    protected String     startingBuildDate   = null;

    protected HashMap    affectedComponents  = null; // The key is the componentName
    protected HashMap    installedComponents = null; // The key is the componentName, value is the signature

    protected Vector     pfFileUpdate        = new Vector();     // PropertyFile Updates
    protected Hashtable  reSequenceJar       = new Hashtable();
    protected Vector     ufFileVector        = new Vector();     // For find and Replace strings
    protected Properties gProps              = new Properties(); // Global Properties

    protected Attributes mainAttribs = null; // The main Attributes from the input JarFile

    protected String     backupInstalledComponents = null;
    // defect 120991 need to know components installed during uninstall of ptf

    protected boolean    HANDLING_WAS_DM     = false;

    protected static boolean doLogging = true;


    protected static String wasInstance = null;
    protected static String wasBaseHome = null;

    public final static String WAS_PASS = "WasPassword=";

    // Process notes:

    // Inverted changes are not at the same level of detail as the
    // applied delta.  Jar entry details are handled as a simple file
    // update.  This creates a larger backup file, but allows for a
    // more robust backup.
    //
    // The backup jar is created relative to the current directory;
    // use an absolute path to place the backup jar outside of the
    // current directory.
    //
    // The log file is created relative to the current directory; use
    // an absolute path to place the log file outside of the current
    // directory.
    //
    // The HistoryEventData class is used to share the information
    // need to build the history\event in Product.XML

    // Processing status constants ...

    public static final int extractionSuccess = 0 ;
    // Process notes:

    // Inverted changes are not at the same level of detail as the
    // applied delta.  Jar entry details are handled as a simple file
    // update.  This creates a larger backup file, but allows for a
    // more robust backup.
    //
    // The backup jar is created relative to the current directory;
    // use an absolute path to place the backup jar outside of the
    // current directory.
    //
    // The log file is created relative to the current directory; use
    // an absolute path to place the log file outside of the current
    // directory.
    //
    // The HistoryEventData class is used to share the information
    // need to build the history\event in Product.XML

    // Processing status constants ...

    public static final int extractionFailure = 8 ;

    public static void main(String args[])
    {
        Extractor extractor = new Extractor();

        try {
            if ( extractor.testJavaVersion() )
                if ( extractor.processCmdLineArgs(args) )
                    extractor.process();

        } catch ( RuntimeException ex ) {
            extractor.logError(91, "Runtime exception", ex);
            ex.printStackTrace(System.out);

        } catch ( Exception ex ) {
            extractor.logError(101, "Exception", ex);
            ex.printStackTrace(System.out);
        }

        System.exit( (extractor.errorCount > 0) ? extractionFailure : extractionSuccess );
    }

    public Extractor() {
       HANDLING_WAS_DM = (new Boolean( WPConfig.getProperty( WPConstants.PROP_WAS_DM ) ) ).booleanValue();
       if ( PlatformUtils.isISeries() ) {
          wasInstance = WPConfig.getProperty( WPConstants.PROP_WAS_INSTANCE);
          wasBaseHome = WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME );
       }
    }

    // Test the current java version; the JDK version must be greater
    // than or equal to 1.3.0.  Answer true, telling if the java
    // version is OK.

    protected boolean testJavaVersion()
    {
        Helper1 useHelper = new Helper1(null, 0); // Don't open the log file yet.

        if ( useHelper.GetJavaVersion() < 130 ) {
            System.out.println("Error 102 -- Incorrect Java version.");
            System.out.println("  Current : " + System.getProperty("java.version"));
            System.out.println("  Required: 1.3.0 or greater.");
            System.out.println("JAR classes were not supported prior to JDK 1.3.0");

            // return false;
            return true;

        } else {
            return true;
        }
    }

    // All work at this level is performed with logging, including a
    // test of the error log.

    public boolean process()
    {
        if ( !initializeLogging() )
            return false;

        boolean result = true;

        logOnly(prolix4, "Beginning processing.");

        try {
            describeArgs();

            if( doLogging )
                displayVersions( logStream );

            if ( basicProcess() )
                updateProductFile();

        } catch ( NoClassDefFoundError ex ) {
            logError(114, "NoClassDefFoundError during process", ex);

        } catch ( Throwable th ) {
            logError(198, "Processing failed with throwable", th);
		}

		log("Input Jar File   : " + decodedJarName);
		log("Target Directory : " + targetDirFullName);
		log("Backup Jar File  : " + backupJarName);
		log("Warnings Issued  : " + helper.FmtNum(warnCount, 0, 0));
		log("Log File         : " + logFileFullName);
		log("");

		if ( errorCount > 0 ) {
			log("Errors were noted: " + errorCount);
			log("Extractor functionality may be compromised!");

			result = false;

		} else {
			if ( fRestore ) {
				log("Application (for restore) completed with no errors.");

			} else {
				if ( mainAttribs != null ) {
					String endMsg = mainAttribs.getValue(HelperList.meEndMsg);

					if ( endMsg == null ) {
						log("Application completed with no errors");

					} else  {
						log(Helper1.resolveMacro(endMsg, gProps) + " completed with no errors.");
						result = true;
					}
				}
			}
		}

		log("");
		log("View the log for processing details.");

		closeLogStream();
		if(LCUtil.hasError(logFileFullName)){
			result = false;
		}
        return result;
    }

    // Keywords for command line options:

    protected static final String k_Help = "?" ;
    // Keywords for command line options:

    protected static final String k_Usage = "Usage" ;
    // Keywords for command line options:

    protected static final String k_Version = "Version" ;
    // Keywords for command line options:

    protected static final String k_ShowXMLVersion = "ShowXMLVersion" ;
    // Keywords for command line options:

    protected static final String k_PropertyFile = "PropertyFile" ;
    // Keywords for command line options:

    protected static final String k_ShowProperties = "ShowProperties" ;
    // Keywords for command line options:

    protected static final String k_TargetDir = "TargetDir" ;
    // Keywords for command line options:

    protected static final String k_JarInputFile = "JarInputFile" ;
    // Keywords for command line options:

    protected static final String k_NoBackupJar = "NoBackupJar" ;
    // Keywords for command line options:

    protected static final String k_BackupJar = "BackupJar" ;
    // Keywords for command line options:

    protected static final String k_OverWriteBackupJar = "OverWriteBackupJar" ;
    // Keywords for command line options:

    protected static final String k_Compression = "Compression" ;
    // Keywords for command line options:

    protected static final String k_TmpDir = "TmpDir" ;
    // Keywords for command line options:

    protected static final String k_LogFile = "LogFile" ;
    // Keywords for command line options:

    protected static final String k_Verbosity = "Verbosity" ;
    // Keywords for command line options:

    protected static final String k_Debug = "Debug" ;
    // Keywords for command line options:

    protected static final String k_StartMsg = "StartMsg" ;
    // Keywords for command line options:

    protected static final String k_EndMsg = "EndMsg" ;
    // Keywords for command line options:

    protected static final String k_ProductFileType = "ProductFileType" ;
    // Keywords for command line options:

    protected static final String k_ProductFileName = "ProductFileName" ;
    // Keywords for command line options:

    protected static final String k_XMLVersion = "XMLVersion" ;
    // Keywords for command line options:

    protected static final String k_Validating = "Validating" ;
    // Keywords for command line options:

    protected static final String k_NameSpaceAware = "NameSpaceAware" ;
    // Keywords for command line options:

    protected static final String k_PropertiesVersionKey = "PropertiesVersionKey" ;
    // Keywords for command line options:

    protected static final String k_XMLPathVersion = "XMLPathVersion" ;
    // Keywords for command line options:

    protected static final String k_XMLPathEditionName = "XMLPathEditionName" ;
    // Keywords for command line options:

    protected static final String k_XMLPathEditionValue = "XMLPathEditionValue" ;
    // Keywords for command line options:

    protected static final String k_XMLPathBuildNumber = "XMLPathBuildNumber" ;
    // Keywords for command line options:

    protected static final String k_XMLPathBuildDate = "XMLPathBuildDate" ;
    // Keywords for command line options:

    protected static final String k_XMLPathEvent = "XMLPathEvent" ;
    // Keywords for command line options:

    protected static final String k_CheckForClass = "CheckForClass" ;
    // Keywords for command line options:

    protected static final String k_SkipVer = "SkipVer" ;
    // Keywords for command line options:

    protected static final String k_DupCheck = "DupCheck" ;
    // Keywords for command line options:

    protected static final String k_Force = "Force" ;
    // Keywords for command line options:

    protected static final String k_ckSize = "ckSize" ;
    // Keywords for command line options:

    protected static final String k_ckEditionName = "ckEditionName" ;
    // Keywords for command line options:

    protected static final String k_ckEditionValue = "ckEditionValue" ;
    // Keywords for command line options:

    protected static final String k_ckVersion = "ckVersion" ;
    // Keywords for command line options:

    protected static final String k_ComponentCheck = "ComponentCheck" ;
    // Keywords for command line options:

    protected static final String k_UpdateProductFile = "UpdateProductFile" ;
    // Keywords for command line options:

    protected static final String k_NewVersion = "NewVersion" ;
    // Keywords for command line options:

    protected static final String k_NewBuildNumber = "BuildNumber" ;
    // Keywords for command line options:

    protected static final String k_NewBuildDate = "BuildDate" ;
    // Keywords for command line options:

    protected static final String k_EventType = "EventType" ;
    // Keywords for command line options:

    protected static final String k_APAR = "APAR" ;
    // Keywords for command line options:

    protected static final String k_PMR = "PMR" ;
    // Keywords for command line options:

    protected static final String k_Description = "Description" ;
    // Keywords for command line options:

    protected static final String k_Silent = "Silent" ;

    // Display text ...

    // Displayed when no target file was specified:

    // This message is mutated during processing.

    protected String[] provideTargetMessage = {
        " This tool requires a target directory to which to apply",
        " the update information from the update jar file.  For",
        " example, 'C:\\WebSphere\\PortalServer'.",
        "",
        " Note that the contents of the target directory will be",
        " modified by the execution of this tool.",
        "",
        " A target directory was not specified on the command line nor",
        " in a property file.",
        "",
        " Please provide the name of the target directory.",
        ": "
    };

    // Displayed when no input jar file can be found:

    protected static final String[] noInputJarMessage = {
        " This tool requires an input jar file.  The path/name of the  ",
        " input jar file could not be deduced, nor was it specified or",
        " implied on the command line or property file.",
        "",
        " Please provide the path/name of the jar file containing the updates.",
        "",
        " "
    };

    // Displayed when we have a collision on the backup jar:

    protected static final String[] overwriteBackupJarMessage = {
        "  This tool creates a jar file containing a backup of files",
        "  which will be modified, replaced or deleted during procesing.",
        "",
        "  The backup jar file currently exists:",
        "",
        "",
        "  This tool will not overwrite an existing file to create",
        "  the backup jar file unless confirmation is provided.",
        "",
        "  Please reply 'yes' to overwrite the existing file.",
        "",
    };

    protected PODef[] createArgumentDefs()
    {
        return new PODef[] {
            new PODef(k_Help,                 "BuiltInHelp", HelperList.meFalse),
            new PODef(k_Usage,                "boolean",     HelperList.meFalse),
            new PODef(k_Version,              "boolean",     HelperList.meFalse),
            new PODef(k_ShowXMLVersion,       "boolean",     HelperList.meFalse),

            new PODef(k_PropertyFile,         "Validating",  null),
            new PODef(k_ShowProperties,       "boolean",     HelperList.meFalse),

            new PODef(k_TargetDir,            "Directory",   null,  provideTargetMessage),
            new PODef(k_JarInputFile,         "inFile",      null,  noInputJarMessage),
            new PODef(k_NoBackupJar,          "String",      null),
            new PODef(k_BackupJar,            "outFileAny",  null),
            new PODef(k_OverWriteBackupJar,   "boolean",     null,  overwriteBackupJarMessage),
            new PODef(k_Compression,          "int",         "9"),
            new PODef(k_TmpDir,               "Directory",   "."),

            new PODef(k_LogFile,              "OutFileAny",  "Extractor.Log"),
            new PODef(k_Verbosity,            "int",         "3"),
            new PODef(k_Debug,                "boolean",     HelperList.meFalse),

            new PODef(k_StartMsg,             "String",      null),
            new PODef(k_EndMsg,               "String",      null),

            new PODef(k_ProductFileType,      "String",      null),
            new PODef(k_ProductFileName,      "String",      null),
            new PODef(k_XMLVersion,           "String",      null),
            new PODef(k_Validating,           "Boolean",     HelperList.meTrue),
            new PODef(k_NameSpaceAware,       "Boolean",     HelperList.meTrue),
            new PODef(k_PropertiesVersionKey, "String",      null),

            new PODef(k_XMLPathVersion,        "String",     null),
            new PODef(k_XMLPathEditionName,    "String",     null),
            new PODef(k_XMLPathEditionValue,   "String",     null),
            new PODef(k_XMLPathBuildNumber,    "String",     null),
            new PODef(k_XMLPathBuildDate,      "String",     null),

            new PODef(k_CheckForClass,        "StringList",  null),

            new PODef(k_SkipVer,              "Boolean",     HelperList.meFalse),
            new PODef(k_DupCheck,             "Boolean",     HelperList.meTrue),
            new PODef(k_Force,                "Boolean",     HelperList.meFalse),
            new PODef(k_ckSize,               "Long",        "1"),
            new PODef(k_ckEditionName,        "String",      null),
            new PODef(k_ckEditionValue,       "String",      null),
            new PODef(k_ckVersion,            "String",      null),

            new PODef(k_ComponentCheck,       "Boolean",     HelperList.meTrue),

            new PODef(k_UpdateProductFile,    "Boolean",     HelperList.meTrue),

            new PODef(k_NewVersion,           "String",      null),
            new PODef(k_NewBuildNumber,       "String",      null),
            new PODef(k_NewBuildDate,         "String",      null),

            new PODef(k_EventType,            "String",      null),
            new PODef(k_APAR,                 "String",      null),
            new PODef(k_PMR,                  "String",      null),
            new PODef(k_Description,          "String",      null),
            new PODef(k_Silent,               "boolean",     "false")
        };
    }

    protected POProcessor po = null;

    public boolean processCmdLineArgs(String[] args)
        throws Exception
    {
        po = new POProcessor(createArgumentDefs(), args, null);

        if (po.getBool(k_ShowProperties))
            po.showProps();

        fDebug = po.getBool(k_Debug);
        
        
        logVerbosity = po.getInt(k_Verbosity);

        if ( po.getBool(k_Usage) ) {
            displayUsage();
            return false;

        } else if ( po.getBool(k_Help) ) {
            displayOnLineHelp();
            return false;

        } else if ( po.getBool(k_Version) ) {
            displayVersions( new Logger() );
            return false;

        } else if ( po.getBool(k_ShowXMLVersion) ) {
            displayXMLVersion();
            return false;
        }

        APAR = po.getString(k_APAR);

        logFileName = po.getString(k_LogFile);
        defaultedLogFileName = ( po.valueSource == POProcessor.k_Default );

        fSkipVer = po.getBool(k_SkipVer);

        ckEditionName = po.getString(k_ckEditionName);
        ckEditionValue = po.getString(k_ckEditionValue);
        ckVersion = po.getString(k_ckVersion);

        if ( !decipherNoBackupJarRequest( po.getString(k_NoBackupJar) ) )
            return false;

        return ( po.errorCount == 0 );
    }

    protected boolean decipherNoBackupJarRequest(String string)
    {
        if ( string == null )
            return true;

        StringTokenizer toks = new StringTokenizer(string, ",");

        while ( toks.hasMoreTokens() ) {
            String pair = toks.nextToken();

            StringTokenizer reqs = new StringTokenizer(pair, "*");

            if ( reqs.countTokens() != 2 ) {
                logError(87, "Processing " + POProcessor.srcDescriptions[po.valueSource] +
                             ": Incorrect count of tokens for noBackUpJar (" + pair + "), entry ignored.");
                return false;

            } else {
                String cond     = reqs.nextToken().trim();
                String filespec = reqs.nextToken().trim();

                if ( cond.equalsIgnoreCase("ifExists") || cond.equalsIgnoreCase("ifNotExists") ) {
                    cond = cond.equalsIgnoreCase("ifExists") ? "1" : "0";
                    noBackupJar.put(filespec, cond);

                } else {
                    logError(134, "Processing " + POProcessor.srcDescriptions[po.valueSource]);
                    logError(135, "The -NoBackupJar specification starts with \"" + cond + "\"" +
                                  "but must start with \"ifExists\" or \"ifNotExists\".");
                    return false;
                }
            }
        }

        return true;
    }

    public static final String
        WIDTH_BUFFER = "                          ";

    public static final int
        VERSION_WIDTH = WIDTH_BUFFER.length();

    public void versionFill(StringBuffer dest, String text)
    {
        dest.append(text);

        int textLen = text.length();

        if ( textLen < VERSION_WIDTH )
            dest.append( WIDTH_BUFFER.substring(0, VERSION_WIDTH - textLen) );
    }

    public void displayVersion(Logger output,
                               String className,
                               String programVersion,
                               String programUpdate)
    {
        StringBuffer outputBuffer = new StringBuffer();

        versionFill(outputBuffer, className);

        outputBuffer.append(" : ");
        outputBuffer.append(programVersion);
        outputBuffer.append(" @ ");
        outputBuffer.append(programUpdate);

        output.Both( outputBuffer.toString() );
    }

    public void displayVersion(Logger output,
                               Class  theClass )
    {

       try {
          
          displayVersion( output,
                          theClass.getName(),
                          theClass.getField( "pgmVersion" ).get( null ).toString(),
                          theClass.getField( "pgmUpdate" ).get( null ).toString() );
       } catch ( Exception e) {
          e.printStackTrace();
       }
    }

    public void displayProperty(Logger output,
                                String propertyDescription,
                                String propertyName)
    {
        StringBuffer outputBuffer = new StringBuffer();

        versionFill(outputBuffer, propertyDescription);
        outputBuffer.append(" : ");
        outputBuffer.append( System.getProperty(propertyName) );

        output.Both( outputBuffer.toString() );
    }

    public void displayVersions(Logger output)
    {
        displayProperty(output, "System Architecture", "os.arch");
        displayProperty(output, "Operating System",    "os.name");
        displayProperty(output, "OS Version",          "os.version");
        output.Both("");
        displayProperty(output, "Java Version",        "java.version");

        output.Both("");

        displayVersion(output, ChangeItem.class.getName(),
                       ChangeItem.pgmVersion,
                       ChangeItem.pgmUpdate);
        displayVersion(output, Delta.class.getName(),
                       Delta.pgmVersion,
                       Delta.pgmUpdate);
        displayVersion(output, DeltaByteGenerator.class.getName(),
                       DeltaByteGenerator.pgmVersion,
                       DeltaByteGenerator.pgmUpdate);
        displayVersion(output, DeltaByteReconstructor.class.getName(),
                       DeltaByteReconstructor.pgmVersion,
                       DeltaByteReconstructor.pgmUpdate);
        displayVersion(output, DomL2Spt.class.getName(),
                       DomL2Spt.pgmVersion,
                       DomL2Spt.pgmUpdate);
        displayVersion(output, ExecCmd.class.getName(),
                       ExecCmd.pgmVersion,
                       ExecCmd.pgmUpdate);
        displayVersion(output, ExtendedUpdateAction.class.getName(),
                       ExtendedUpdateAction.pgmVersion,
                       ExtendedUpdateAction.pgmUpdate);
        displayVersion(output, Extractor.class.getName(),
                       Extractor.pgmVersion,
                       Extractor.pgmUpdate);
        displayVersion(output, FileSystemSpace.class.getName(),
                       FileSystemSpace.pgmVersion,
                       FileSystemSpace.pgmUpdate);
        displayVersion(output, FilterFile.class.getName(),
                       FilterFile.pgmVersion,
                       FilterFile.pgmUpdate);
        displayVersion(output, Helper1.class.getName(),
                       Helper1.pgmVersion,
                       Helper1.pgmUpdate);
        displayVersion(output, HelperList.class.getName(),
                       HelperList.pgmVersion,
                       HelperList.pgmUpdate);
        displayVersion(output, ISystemFile.class.getName(),
                       ISystemFile.pgmVersion,
                       ISystemFile.pgmUpdate);
        displayVersion(output, LocalSystem.class.getName(),
                       LocalSystem.pgmVersion,
                       LocalSystem.pgmUpdate);
        displayVersion(output, Logger.class.getName(),
                       Logger.pgmVersion,
                       Logger.pgmUpdate);
        displayVersion(output, PODef.class.getName(),
                       PODef.pgmVersion,
                       PODef.pgmUpdate);
        displayVersion(output, POProcessor.class.getName(),
                       POProcessor.pgmVersion,
                       POProcessor.pgmUpdate);
        displayVersion(output, ReSequenceJar.class.getName(),
                       ReSequenceJar.pgmVersion,
                       ReSequenceJar.pgmUpdate);
        displayVersion(output, SampleAction.class.getName(),
                       SampleAction.pgmVersion,
                       SampleAction.pgmUpdate);
        displayVersion(output, SampleExtendedAction.class.getName(),
                       SampleExtendedAction.pgmVersion,
                       SampleExtendedAction.pgmUpdate);
        displayVersion(output, ShowProgress.class.getName(),
                       ShowProgress.pgmVersion,
                       ShowProgress.pgmUpdate);
        displayVersion(output, UpdateAction.class.getName(),
                       UpdateAction.pgmVersion,
                       UpdateAction.pgmUpdate);
        displayVersion(output, WindowsFile.class.getName(),
                       WindowsFile.pgmVersion,
                       WindowsFile.pgmUpdate);
        displayVersion(output, XML_Handler.class.getName(),
                       XML_Handler.pgmVersion,
                       XML_Handler.pgmUpdate);
        displayVersion(output, findClassInPath.class.getName(),
                       findClassInPath.pgmVersion,
                       findClassInPath.pgmUpdate);
        displayVersion(output, DeleteTranLog.class.getName(),
                       DeleteTranLog.pgmVersion,
                       DeleteTranLog.pgmUpdate);

        output.Both("");

        displayVersion(output, DeployCmd.class.getName(),
                       DeployCmd.pgmVersion,
                       DeployCmd.pgmUpdate);
        displayVersion(output, EARActor.class.getName(),
                       EARActor.pgmVersion,
                       EARActor.pgmUpdate);
        displayVersion(output, EARCmd.class.getName(),
                       EARCmd.pgmVersion,
                       EARCmd.pgmUpdate);
        displayVersion(output, EARPostActor.class.getName(),
                       EARPostActor.pgmVersion,
                       EARPostActor.pgmUpdate);
        displayVersion(output, EARPreActor.class.getName(),
                       EARPreActor.pgmVersion,
                       EARPreActor.pgmUpdate);
        displayVersion(output, ExtendedEARAction.class.getName(),
                       ExtendedEARAction.pgmVersion,
                       ExtendedEARAction.pgmUpdate);
        displayVersion(output, ExtendedEARActor.class.getName(),
                       ExtendedEARActor.pgmVersion,
                       ExtendedEARActor.pgmUpdate);
        displayVersion(output, ExtendedEARPostActor.class.getName(),
                       ExtendedEARPostActor.pgmVersion,
                       ExtendedEARPostActor.pgmUpdate);
        displayVersion(output, ExtendedEARPreActor.class.getName(),
                       ExtendedEARPreActor.pgmVersion,
                       ExtendedEARPreActor.pgmUpdate);
        displayVersion(output, InstallationData.class.getName(),
                       InstallationData.pgmVersion,
                       InstallationData.pgmUpdate);
        displayVersion(output, InstanceData.class.getName(),
                       InstanceData.pgmVersion,
                       InstanceData.pgmUpdate);
        displayVersion(output, NodeData.class.getName(),
                       NodeData.pgmVersion,
                       NodeData.pgmUpdate);
        displayVersion(output, DeploymentData.class.getName(),
                       DeploymentData.pgmVersion,
                       DeploymentData.pgmUpdate);
        displayVersion(output, PlatformUtils.class.getName(),
                       PlatformUtils.pgmVersion,
                       PlatformUtils.pgmUpdate);
        displayVersion(output, ProductInterrogator.class.getName(),
                       ProductInterrogator.pgmVersion,
                       ProductInterrogator.pgmUpdate);

        displayVersion( output, ExtendedWPEarAction.class );
        displayVersion( output, ExtendedWPEarPostActor.class );
        displayVersion( output, ExtendedWPEarPreActor.class );
        displayVersion( output, WPEarActor.class );

        displayVersion( output, ExtendedWpsEarAction.class );
        displayVersion( output, ExtendedWpsEarPostActor.class );
        displayVersion( output, ExtendedWpsEarPreActor.class );
        displayVersion( output, WpsEarEARActor.class );

        displayVersion( output, ExtendedWmmEarAction.class );
        displayVersion( output, ExtendedWmmEarPostActor.class );
        displayVersion( output, ExtendedWmmEarPreActor.class );
        displayVersion( output, WmmEarEARActor.class );

        displayVersion( output, ExtendedWpcpAuthoringEarAction.class );
        displayVersion( output, ExtendedWpcpAuthoringEarPostActor.class );
        displayVersion( output, ExtendedWpcpAuthoringEarPreActor.class );
        displayVersion( output, WpcpAuthoringEarActor.class );

        displayVersion( output, ExtendedWpcpRuntimeEarAction.class );
        displayVersion( output, ExtendedWpcpRuntimeEarPostActor.class );
        displayVersion( output, ExtendedWpcpRuntimeEarPreActor.class );
        displayVersion( output, WpcpRuntimeEarActor.class );

        displayVersion( output, ExtendedPdmauthorEarAction.class );
        displayVersion( output, ExtendedPdmauthorEarPostActor.class );
        displayVersion( output, ExtendedPdmauthorEarPreActor.class );
        displayVersion( output, PdmauthorEarActor.class );

        displayVersion( output, ExtendedWPODCEarAction.class );
        displayVersion( output, ExtendedWPODCEarPostActor.class );
        displayVersion( output, ExtendedWPODCEarPreActor.class );
        displayVersion( output, WPOdcEARActor.class );
        
        displayVersion( output, ExtendedWCMEarAction.class );
        displayVersion( output, ExtendedWCMEarPostActor.class );
        displayVersion( output, ExtendedWCMEarPreActor.class );
        displayVersion( output, WCMEarActor.class );


        output.Both("");

        displayVersion(output, com.ibm.websphere.update.delta.util.WsAdminCmd.class.getName(),
                       com.ibm.websphere.update.delta.util.WsAdminCmd.pgmVersion,
                       com.ibm.websphere.update.delta.util.WsAdminCmd.pgmUpdate);
    }

    public void displayXMLVersion()
    {
        XML_Handler xmlHandler = new XML_Handler(logStream, false, false);

        System.out.println("XML Handler Version       : " + XML_Handler.pgmVersion);
        System.out.println("XML Parser Version        : " + xmlHandler.getVersion(fDebug));
    }

    public static final String[] usageLines = {
        "One of:",
        "       java -jar deltaJarName <options>",
        "       java Extractor <options>",
        "",
        "Valid options are as follows:",
        "",
        "  ( -Usage ( true | false ) |                 (Defaults to false)",
        "    ( -Help | Help | -? | ? ) ( true | false ) |",
        "    -Version ( true | false ) |               (Defaults to false)",
        "    -ShowXMLVersion ( true | false ) |        (Defaults to false)",
        "",
        "    [ -PropertyFile <fileName> ]",
        "    [ -ShowProperties ( true | false ) ]      (Defaults to false)",
        "",
        "    [ -TargetDir <directoryName> ]",
        "    [ -JarInputFile <jarFileName> ]",
        "    [ -NoBackupJar ( ifExists*<fileName> | ifNotExist*<fileName> ), ... ]",
        "    [ -BackupJar <jarFileName> ]",
        "    [ -OverWriteBackupJar ( true | false ) ]  (Defaults to false)",
        "    [ -Compression < 0 .. 9 > ]               (Defaults to 9)",
        "    [ -TmpDir <directoryName> ]               (Defaults to current directory)",
        "",
        "    [ -LogFile <fileName> ]                   (Defaults to \"Extractor.log\"",
        "    [ -Debug ( true | false ) ]               (Defaults to false)",
        "    [ -Verbosity < 0 .. 5 > ]                 (Defaults to 3)",
        "",
        "    [ -StartMsg <text> ]",
        "    [ -EndMsg <text> ]",
        "",
        "    [ -ProductFileType ( XML | Properties ) ]",
        "    [ -ProductFileName <fileName> ]",
        "    [ -XMLVersion <version> ]",
        "    [ -Validating ( true | false ) ]          (Defaults to false)",
        "    [ -NameSpaceAware ( true | false ) ]      (Defaults to false)",
        "    [ -PropertiesVersionKey <key> ]",
        "",
        "    [ -XMLPathVersion <XML path> ]",
        "    [ -XMLPathEditionName <XML path> ]",
        "    [ -XMLPathEditionValue <XML path> ]",
        "    [ -XMLPathBuildNumber <XML path> ]",
        "    [ -XMLPathBuildDate <XML path> ]",
        "",
        "    [ -CheckForClass <className, ...> ]",
        "",
        "    [ -SkipVer ( true | false ) ]             (Defaults to false)",
        "    [ -DupCheck ( true | false ) ]            (Defaults to true)",
        "    [ -Force ( true | false ) ]               (Defaults to false)",
        "    [ -ckSize <byteCount> ]                   (Defaults to 1 byte)",
        "    [ -ckEditionName <name, ...> ]",
        "    [ -ckEditionValue <value, ...> ]",
        "    [ -ckVersion <version, ...> ]",
        "",
        "    [ -ComponentCheck ( yes | no ) ]          (Defaults to yes)",
        "",
        "    [ -UpdateProductFile ( true | false ) ]   (Defaults to true)",
        "",
        "    [ -NewVersion <version> ]",
        "    [ -NewBuildNumber <buildNumber> ]",
        "    [ -NewBuildDate <buildDate> ]",
        "",
        "    [ -EventType ( PTF | eFix | Diagnostic | Test | Other | BackOut ) ]",
        "    [ -APAR <aparNumber> ]",
        "    [ -PMR <pmrNumber> ] ",
        "    [ -Description <text> ] )",
        ""
    };

    public void displayUsage()
    {
        System.out.println("Extractor Version: " + pgmVersion) ;

        for ( int usageLineNo = 0; usageLineNo < usageLines.length; usageLineNo++ )
            System.out.println(usageLines[usageLineNo]);
    }

    public static final String helpLines[] = {
        "",
        "This program performs an incremental update of a target directory using an",
        "update image which was packaged into a jar file.",
        "",
        "Usually, the extraction classes are packaged along with the update image in",
        "the update jar file, in which case the command line syntax is:",
        "",
        "       java -jar <jarFile> (options)",
        "",
        "The update may also be executed with the extraction classes being present in",
        "the class path, in which case the command line syntax is:",
        "",
        "       java Extractor (options)",
        "",
        "",
        "Options are specified on the command line.  In addition, options may also be",
        "specified in a property file using the \"-PropertyFile\" option.  Command line",
        "option keywords are used as the keys in the options property file.",
        "",
        "Values specified on the command line take precedence over values specified in",
        "the property file.",
        "",
        "",
        "A number of options accept an argument a boolean value.  For default value",
        "for each such option applies when the option is omitted altogether.  When the",
        "keyword for a boolean option is specified with no argument value, that option",
        "is set to true.",
        "",
        "The keyword for each option may be abbreviated, so long as the abbreviation",
        "is the prefix of exactly one valid option keyword.",
        "",
        "Option keys are not case sensitive.",
        "",
        "A number of options provide overrides to values that are specified in the",
        "update jar file.",
        "",
        "",
        "A number of options are available which cause usage or help information to",
        "be displayed.  For each of these options, after the specified information is",
        "displayed the update operation is aborted:",
        "",
        "    -Usage           -- Show usage text",
        "    -Help or -?      -- Show help text",
        "    -Version         -- Show class versions",
        "    -ShowXMLVersion  -- Show program versions of the XML helper classes",
        "",
        "The \"-Help\" and \"-?\" options show this text.",
        "",
        "The \"-Usage\" option is used to display a terse list of options.",
        "",
        "The \"-Version\" option is used to display the versions of the classes which",
        "are provided as a part of the update operation.",
        "",
        "The \"-ShowXMLVersion\" option is used to display the versions of the XML",
        "helper classes which are provided as a part of the update operation.",
        "",
        "",
        "The following options are available for use during a usual update operation:",
        "",
        "The following option is used to specify a property file from which to",
        "read further options:",
        "",
        "    -PropertyFile <file>     -- Read options from the specified property file",
        "",
        "The following option is applicable only when a property file is specified.",
        "This option determines if properties are displayed as they are read:",
        "",
        "    -ShowProperties ( true | false )",
        "",
        "This option defaults to false.",
        "",
        "The help number may be displayed during command line parsing.",
        "",
        "The following option is used to specify the directory to which the update",
        "is to be applied:",
        "",
        "    -TargetDir <targetDir>",
        "",
        "The following option specifies the jar file to be used as the source of",
        "update information:",
        "",
        "    [ -JarInputFile <jarFileName> ]",
        "",
        "This option defaults to the execution jar file.",
        "",
        "During a backup operation, a value is provided for this option from the",
        "backup jar file.  If unspecified and if not executing from a jar file, the",
        "user will be asked to provide a value.",
        "",
        "The following option may be used to specify a number of conditions under",
        "which no backup jar file will be created:",
        "",
        "    [ -NoBackupJar ( ifExists*<fileName> | ifNotExist*<fileName> ), ... ]",
        "",
        "The argument to this option is a comma delimited list of conditions, each of",
        "which having a keyword, either \"ifExists\" or \"ifNotExist\", followed by",
        "the character \"*\", followed by a file name.  Correspondingly, the presence",
        "or absence of a specified file will case the update operation to produce",
        "no backup jar file.",
        "",
        "The following option is used to specify the name of the backup jar which is",
        "created:",
        "",
        "    [ -BackupJar <jarFileName> ]",
        "",
        "By default, the backup jar is called \"Extractor.jar\", and is placed in",
        "the current working directory.",
        "",
        "The following option, if specified, will allow the backup jar file to",
        "overwrite an existing file:",
        "",
        "    [ -OverWriteBackupJar ( true | false ) ]",
        "",
        "This option defaults to false, that is, an update will be aborted if the",
        "backup jar file would otherwise overwrite an existing file.",
        "",
        "The following option is used to specify the compression level to be used",
        "by all jar files which are created during the update operation:",
        "",
        "    [ -Compression < 0 .. 9 > ]",
        "",
        "This option applies to all jars which are created, including jar files which",
        "are processed during the update operation and including the backup jar file.",
        "",
        "For this option, larger values indicates higher levels of compression.  The",
        "default for this option is 9, that is, to use the highest level of compression.",
        "",
        "The following option is used to specify the temporary directoy to use when",
        "updating jar files and when resequencing jar files:",
        "",
        "    [ -TmpDir <directoryName> ]",
        "",
        "The specified directory must be writable.  (If the specified directory is not",
        "writable, the user will be asked to input an alternate directory.)",
        "",
        "The temporary directory defaults to the current directory.",
        "",
        "The name \"exit\" may not be used as the name of the temporary directory.",
        "",
        "The following option is used to specify an alternate name for the log file:",
        "",
        "    [ -LogFile <fileName> ]",
        "",
        "The log file name defaults to \"Extractor.log\".",
        "",
        "The following option is used to specify the level of verbosity of log messages:",
        "",
        "    [ -Verbosity < 0 .. 5 > ]",
        "",
        "The verbosity level defaults to 3.  A larger number indicates a higher",
        "verbosity level.",
        "",
        "The following option is used to enable debugging output:",
        "",
        "    [ -Debug ( true | false ) ]",
        "",
        "By default, debugging output is disabled.",
        "",
        "The following two options are used to specify messages which are to be",
        "displayed either at the beginning of the extraction operation or at the",
        "end of the extraction operation:",
        "",
        "    [ -StartMsg <text> ]",
        "    [ -EndMsg <text> ]",
        "",
        "The following option is used to specify the type of product file which is",
        "associated with product being updated:",
        "",
        "    [ -ProductFileType ( XML | Properties ) ]",
        "",
        "A product file type must be specified if version checking is to be performed",
        "or if version information is to be updated as a part of the update operation.",
        "",
        "Two product file types are currently supported:",
        "",
        "    XML         --  for an XML product file",
        "    Properties  --  for a Properties based product file",
        "",
        "The following option is used to specify the name of the product file which is",
        "to be updated:",
        "",
        "    [ -ProductFileName <fileName> ]",
        "",
        "A product file must be specified if version checking is to be performed, or if",
        "version information is to be updated as a part of the update operation.",
        "",
        "The following option is used to specify a required level of XML support when",
        "reading and updating the product file when that file is an XML file:",
        "",
        "    [ -XMLVersion <version> ]",
        "",
        "An error results when an XML version is specified and that version does not",
        "match the XML version supported by the available XML parser.",
        "",
        "By default, XML version checking is disabled.",
        "",
        "The following two options provide values which are used when instantiating the",
        "XML parser used to access the product file:",
        "",
        "    [ -Validating ( true | false ) ]",
        "    [ -NameSpaceAware ( true | false ) ]",
        "",
        "These two options relate to specific XML parsing features.  Both of these",
        "default to false.",
        "",
        "The following option is used to specify the key associated with the version",
        "field in the product file when that file is a properties file:",
        "",
        "    [ -PropertiesVersionKey <key> ]",
        "",
        "This option is required when the product file is a properties file, and",
        "either prerequisite checking is enabled or when version information is to be",
        "updated.",
        "",
        "The following options are used to specify access paths for the product file:",
        "",
        "    [ -XMLPathVersion      <XML path> ]",
        "    [ -XMLPathEditionName  <XML path> ]",
        "    [ -XMLPathEditionValue <XML path> ]",
        "    [ -XMLPathBuildNumber  <XML path> ]",
        "    [ -XMLPathBuildDate    <XML path> ]",
        "",
        "These five options are available only when the product file is an XML type",
        "file, and are used when locating particular elements within the product file.",
        "The element values may be retrieved during prerequisite checking, and may",
        "be updated as a part of the update.",
        "",
        "The argument to each of these options is a dotted list of XML element names.",
        "The following values are used by default:",
        "",
        "    Version        #document.websphere.appserver.version",
        "    Edition Name   #document.websphere.appserver.edition.name",
        "    Edition Value  #document.websphere.appserver.edition.value",
        "    Build Number   #document.websphere.appserver.build.number",
        "    Build Date     #document.websphere.appserver.build.date",
        "",
        "The following option is used to specify a list of classes which must each",
        "exist exactly once in the target directory:",
        "",
        "    [ -CheckForClass <className, ...> ]",
        "",
        "The argument to this option is a comma delimited list of class names.",
        "",
        "The following option is used to disable prerequisite checking:",
        "",
        "    [ -SkipVer ( true | false ) ]",
        "",
        "By default prerequisite checking is enabled.",
        "",
        "Prerequisite checking includes duplicate event checking, an  size check,",
        "edition name and value checks, and a version check.",
        "",
        "The following option is used to specify if the product file is to be",
        "tested for duplicate events:",
        "",
        "    [ -DupCheck ( true | false ) ]",
        "",
        "When enabled, and when the property file is an XML type file, the product",
        "will be tested to determine if the current update has already been",
        "applied.  This is test is performed by looking for the current update in",
        "the target's product file.  If a duplicate event is found, the current",
        "update is aborted.",
        "",
        "By default, when the properties file is an XML type file, duplicate checking",
        "is enabled.",
        "",
        "The following option is used to specify that the update is to proceed event",
        "a duplicate history event was noted:",
        "",
        "    [ -Force ( true | false ) ]",
        "",
        "By default, forcing is disabled.",
        "",
        "The \"-Force\" option is not valid unless the product file is an XML file.",
        "",
        "The following option is used to specify a number of bytes which must be free",
        "in the target file system for the update operation to be performed:",
        "",
        "    [ -ckSize <byteCount> ]",
        "",
        "A number of bytes must be specified as the argument to this option.",
        "",
        "By default at least one (1) byte must be available in the target file system.",
        "",
        "The following options specify particular edition values, one of each which",
        "must be present in the product file:",
        "",
        "    [ -ckEditionName <name, ...> ]",
        "    [ -ckEditionValue <value, ...> ]",
        "",
        "The first option is used to specify a list of edition names.  The second",
        "option is used to specify a list of edition values.",
        "",
        "Either or both of these options may be specified.  Both options accept a",
        "comma deimited list of values as their argument.  Neither option is valid",
        "unless an XML type product file is in use.",
        "",
        "The following option is used to specify particular versions, one of which",
        "must be present in the product file:",
        "",
        "    [ -ckVersion <version, ...> ]",
        "",
        "The argument to this option is a comma delimited list of versions.  This",
        "option may be used with both XML and Properties type product file, however,",
        "when used with a Properties type product file the",
        " \"-PropertiesVersionKey\" option must be specified.",
        "",
        "The following option is used to disable component checking:",
        "",
        "    [ -ComponentCheck ( yes | false ) ]",
        "",
        "Component checking may be preset in the update jar file.  Component checking",
        "activates two functions, one being a prerequisite check, an the second being",
        "a determination of the particular files which will be updated.",
        "",
        "By default, component checking is enabled or disabled according to component",
        "information preset in the update jar file.",
        "",
        "The following option is used to specify if the product file is to be updated",
        "as a part of the update operation:",
        "",
        "    [ -UpdateProductFile ( true | false ) ]",
        "",
        "By default, when the product file is an XML file, the product file is",
        "updated.",
        "",
        "The following option is used to specify a version value which is to be placed",
        "in the product file:",
        "",
        "    [ -NewVersion <version> ]",
        "",
        "The specified version will be used to update the version field of the product",
        "file, and will be stored in the event history if the product file is an XML",
        "type file.",
        "",
        "The \"-NewVersion\" option is available for both XML and Properties based",
        "product files, however, when used to update a Properties based product file",
        "the \"-PropertiesVersionKey\" option must also be specified.",
        "",
        "The following options are used to update build information in the product file:",
        "",
        "    [ -NewBuildNumber <buildNumber> ]",
        "    [ -NewBuildDate <buildDate> ]",
        "",
        "The values specified will be used to update the corresponding field in the",
        "product file.",
        "",
        "\"-NewBuildNumber\" and \"-NewBuildDate\" are only valid when the product file",
        "is an XML type file.",
        "",
        "The following option is used to specify the type of event which is to be",
        "recorded in the product file:",
        "",
        "    [ -EventType ( PTF | eFix | Diagnostic | Test | Other | BackOut ) ]",
        "",
        "The following event types are available:",
        "",
        "    PTF         --  This update is a PTF updat.e",
        "    eFix        --  This update is an eFix.",
        "    Test        --  This update is being used for testing purposed.",
        "    Other       --  This update has an other, unlisted, purpose.",
        "    BackOut     --  This update is a backup of a prior update.",
        "",
        "The following option is used to specify the number of the APAR which is",
        "being installed by this update:",
        "",
        "    [ -APAR <aparNumber>]",
        "",
        "The argument APAR number is a text field with particular syntax.  An APAR",
        "number is usually specified when the event type is \"PTF\".",
        "",
        "If specified, the APAR number is used to generate the name of the backup",
        "jar file which is created, and, when an XML type product file is in use,",
        "is stored in the event which is added to the product file.",
        "",
        "The following option is used to specify the number of the PMR which is",
        "being installed by this update:",
        "",
        "    [ -PMR <pmrNumber> ] ",
        "",
        "The argument PMR number is a text field with particular syntax.  A PMR",
        "number is ususally specified when the event type is \"eFix\".",
        "",
        "If specified, the PMR number is used to generate the name of the backup",
        "jar file which is created, and, when an XML type product file is in use,",
        "is stored in the event which is added to the product file.",
        "",
        "The following option is used to specify descriptive text to be stored in the",
        "event added to the product file:",
        "",
        "    [ -Description <text> ] )",
        "",
        "This option is only valid when an XML type product file is in use.",
        ""
    };

    public void displayOnLineHelp()
    {
        System.out.println("Extractor Version: " + pgmVersion) ;

        for ( int helpLineNo = 0; helpLineNo < helpLines.length; helpLineNo++ )
            System.out.println(helpLines[helpLineNo]);
    }

    protected void describeArgs()
    {
        log("Extractor version: " + pgmVersion);
        log("");

        logOnlyDebug(prolix3, "Debug request acknowledged");

        logOnly(prolix3, "Verbosity for logging : " + po.getString(k_Verbosity) +
                         "  src=" + POProcessor.srcDescriptions[po.valueSource]);

        logOnly(prolix3, "Log file : " + po.getString(k_LogFile) +
                         "  src=" + POProcessor.srcDescriptions[po.valueSource]);

        if ( targetDirName == null )
            logOnly(prolix3, "No command line target directory");
        else
            logOnly(prolix3, "Command line target directory: " + targetDirName);

        if ( deltaJarName == null )
            logOnly(prolix3, "No command line jar file name");
        else
            logOnly(prolix3, "Command line jar file name: " + deltaJarName);

        logOnly(prolix3, "Temp directory : " + tmpDirFullName +
                         "  src=" + POProcessor.srcDescriptions[po.valueSource]);
        logOnly(prolix3, "Backup jar     : " + po.getString(k_BackupJar) +
                         "  src=" + POProcessor.srcDescriptions[po.valueSource]);


        Enumeration eNum = noBackupJar.keys();

        if (eNum.hasMoreElements())
            logOnly(prolix3, "No backup jar file will be created if:");
        else
            logOnly(prolix3, "No command line criteria for bypassing BackupJar processing.");

        while ( eNum.hasMoreElements() ) {
            String file = (String) eNum.nextElement();
            String cond = (String) noBackupJar.get(file);
            cond = (cond.equals("1")) ? "   exists " : "   if not exists ";
            logOnly(prolix3, cond + file);
        }
    }

    // PQ59675: Class for data recorded during the backup scan;

    class ScanData
    {
        long scanCount = 0;                       // Count of files scanned

        Hashtable preventAdds     = new Hashtable();
        Hashtable preventReplaces = new Hashtable();

        Vector absolutePaths      = new Vector(); // Files whose name is processed absolutely
        Vector updateFiles        = new Vector(); // Files being modified by one of:
                                                  // chmod only,
                                                  // chmod + overwrite,
                                                  // overwrite only,
                                                  // delete
        Vector deleteBeforeWrites = new Vector(); // Files being overwritten, which must
                                                  // be deleted before writing the updated file
        Vector deleteFiles        = new Vector(); // Files being added; these are deleted
                                                  // during the undo operation
        Vector restoreOnlyFiles   = new Vector(); // Files which only processed during a restore

        Hashtable absolutePathsSet = new Hashtable(); // Alternate container for absolute paths;
                                                      // added for efficiency.
        Hashtable withDeleteAction = new Hashtable(); // Files being deleted; this is a hashtable;
                                                      // its entries also are in updateFiles.
        Hashtable withChmodAction  = new Hashtable(); // Files having attributes changed; this is a
                                                      // hashtable; its entries also are in
                                                      // updateFiles.
        Hashtable withUpdateAction = new Hashtable(); // Files being updated; this is a
                                                      // hashtable; its entries also are in
                                                      // updateFiles.
        Hashtable withSpecialUpdate = new Hashtable(); // Files being updated with
                                                      // find/replace or with a property
                                                      // file update, or jar resequencing.
        Hashtable asInstallable    = new Hashtable(); // List of entries which require
                                                      // relocation as installable files.
        Hashtable asInstalled      = new Hashtable(); // List of entries which require
                                                      // relocation as installed files.
        Hashtable asApplication    = new Hashtable(); // List of entries which require
                                                      // relocation as application EAR files.

        protected String nameFile(String fileName)
        {
            if ( absolutePathsSet.get(fileName) != null )
                return fileName;

            String installableName = (String) asInstallable.get(fileName);
            if ( installableName != null )
                return installableName;

            String installedName = (String) asInstalled.get(fileName);
            if ( installedName != null )
                return installedName;

            String applicationName = (String) asApplication.get(fileName);
            if ( applicationName != null )
                return applicationName;


            return targetDirName + fileName;
        }
    } // end of Scan Data class (PQ59675)

    protected String APAR = null; // Not null when processing an efix.

    protected ScanData scanData;

    protected boolean basicProcess()
    {
        localSystem = new LocalSystem(fDebug);

        if ( !establishDeltaJarName() ||
             !establishTargetDir()    ||
//             !establishWASHomeDir()   ||
             //!scanFindReplace()       || // required the target dir
             !establishTmpDir()       ||
//             !establishEARTmpDir()    ||
//             !establishInstances()    ||
             !establishBackupJar() ) {
            return false;
        }

        /* TFB: Now expect the log file name to be
         *      provided on the command line.
         */

        /*
        // Here we wish to establish or confirm the log file name if
        // its got an APAR then its an efix and we need a new log
        // name.

        if ( APAR != null ) {  // if we have an APAR then its an eFix
            if ( !po.keywordUsed(k_LogFile) ) {
                String[] nameParts = new String[helper.k_elCount];
                helper.ParseFileSpec(logFileName, nameParts, fDebug);
                logFileName = targetDirFullName + slash +
                              "eFix" + slash +
                              APAR + slash +
                              nameParts[helper.k_fullname];
                recycleLogStream();
            }
        }
        */

        logOnly(prolix1, "Operating System name is " + System.getProperty("os.name"));

        // If container types were specified the here we want to check them
        // and load the required container list.

        if ( !determineInstalledComponents() )
            return false;

        if ( !handleProductFile() )   // like the Product.xml or others
            return false;

        if ( !handlePrerequisites() )
            return false;

        if ( !completeVersionInfo() )
            return false;

        if ( !check4Class()) // Ensure we have only one copy of selected classes.
            return false;

        if ( !dupApplication() )
            return false;

        scanData = new ScanData();

        // Call this after setting up ScanData so and Entry script can provide  additonal backup files
        if ( !processVirtualScripts(HelperList.meEntryScript) )
            return false;
        if ( !createBackup() )
            return false;

        if ( !processVirtualScripts(HelperList.mePreScript) )
            return false;

        if ( !verifyJarFile() )
            return false;

        if ( !processJarFile() )
            return false;

        //scanFindReplace();                    // Need to performscan after applying files.  Since the files may be new. d62252
        if ( !findReplace() )
            return false;
        if ( !processPropertyFiles() )
            return false;
        if ( !processReSequenceJars() )
            return false;

        if ( !processVirtualScripts(HelperList.mePostScript) )
            return false;

        return true;
    }

    // Sample URL for a class file:
    //
    //     file:/D:/Pgm/InstallDoc/InstallDoc.class
    //
    // Sample URL's for a class file held within a jar file:
    //
    //     jar:file:/D:/Pgm/InstallDoc/Hold/InfoCenter_adv_es.jar!/InstallDoc.class
    //     jar:file:/usr/Pgm/InstallDoc/Hold/InfoCenter_adv_es.jar!/InstallDoc.class

    // Determine the name of the jar file which is to be processed.
    // This is available from the class loader in the self-extracting
    // jar file scenario.  Alternatively, the jar file may be
    // specified on the command line.  Answer true or false, telling
    // if a jar file could be established.

    // The follow two classes hold the information to update strings
    // within some specified files.  We have the ufFileVector vector
    // which is a list of files to update, each entry (FUEntry) is the
    // fileName and a vector which contains the string to find and
    // replace.  The components array specifies components names, one
    // of which must be present before the update may take place.

    protected static final String[] emptyFUComponents = new String[0];

    protected static class FUEntry
    {
        String   fileName;
        Vector   frList;
        String[] components;

        public FUEntry(String fileName)
        {
            this.fileName = fileName;
            this.frList = new Vector();
            this.components = emptyFUComponents;
        }
    }

    protected static class FRList
    {
        String find;
        String replace;
        int count;  // Number of times replacement occurred.

        public FRList(String findText, String replaceText)
        {
            this.find = findText;
            this.replace = replaceText;
            this.count = 0;
        }
    }

    // The following is in support of Property File updates.  The
    // pfFileUpdate is a vector which contains objects of type
    // PFUpdates which in turn contains objects of type PUEvent.

    protected static class PFUpdates
    {
        String propertyFileName;
        Vector puList;
    }

    // Property Update Event

    // Java won't let us put these inside PUEvent.

    protected static final int puInError = 0 ;
    // functions for PUEvent
 // Property Update Event

    // Java won't let us put these inside PUEvent.

    protected static final int puAdd = 1 ;
    // Property Update Event

    // Java won't let us put these inside PUEvent.

    protected static final int puSoftAdd = 2 ;
    // Property Update Event

    // Java won't let us put these inside PUEvent.

    protected static final int puDelete = 3 ;
    // Property Update Event

    // Java won't let us put these inside PUEvent.

    protected static final int puSoftDelete = 4 ;
    // Property Update Event

    // Java won't let us put these inside PUEvent.

    protected static final int puUpdate = 5 ;
    // Property Update Event

    // Java won't let us put these inside PUEvent.

    protected static final int puSoftUpdate = 6 ;

    protected static final String[] puFunctionNames = {
        "error",
        "add",
        "softAdd",
        "update",
        "softUpdate",
        "delete",
        "softDelete"
    };

    protected static class PUEvent
    {
        int function;

        String key;
        String value;

        PUEvent(String function, String key)
        {
            this.key = key;

            if ( function.equalsIgnoreCase("add") ) {
                this.function = puAdd;
                this.value = null;

            } else if ( function.equalsIgnoreCase("softAdd") ) {
                this.function = puSoftAdd;
                this.value = null;

            } else if ( function.equalsIgnoreCase("update") ) {
                this.function = puUpdate;
                this.value = null;

            } else if ( function.equalsIgnoreCase("softUpdate") ) {
                this.function = puSoftUpdate;
                this.value = null;

            } else if ( function.equalsIgnoreCase("delete") ) {
                this.function = puDelete;
                this.value = "";

            } else if ( function.equalsIgnoreCase("softDelete") ) {
                this.function = puSoftDelete;
                this.value = "";

            } else {
                this.function = puInError;
                this.value = function;
            }
        }
    }

    // Establish the input jar file.
    //
    // Perform validation of the input jar, and extract particular
    // information as a part of establishment.

    protected String deltaJarName = null;
    protected String decodedJarName = null;

    protected int inJarEntryCount = 0;

    protected boolean establishDeltaJarName()
    {
        logOnly(prolix3, "Testing for self extracting JAR file.");

        Class extractorClass = new Extractor().getClass();
        String extractorClassName = extractorClass.getName();
        logOnly(prolix3, "Current class is: " + extractorClassName);

        ClassLoader extractorLoader = extractorClass.getClassLoader();
        URL extractorURL = extractorLoader.getResource(extractorClassName + ".class");

        if ( extractorURL == null ) {
            logOnly(prolix3, "The extractor URL is not available.");

        } else {
            String extractorURLString = extractorURL.toString();
            logOnly(prolix3, "URL is: " + extractorURLString);

            if ( extractorURLString.startsWith("jar:") ) {
                int fileEnd = extractorURLString.indexOf("!");

                if ( fileEnd > 0 ) {
                    int start = 9; // point to start of path

                    if (!LocalSystem.isUnix)  // use new LocalSystem class
                        start = 10;  // this is to bypass the leading slash for NT

                    deltaJarName = extractorURLString.substring(start, fileEnd);
                    decodedJarName = URLDecoder.decode(deltaJarName);

                    logOnly(prolix4, "Established jar file name from Extractor class: " + decodedJarName);
                }
            }
        }

        if (deltaJarName != null)
            po.setDefault(k_JarInputFile, deltaJarName);

        deltaJarName = po.getString(k_JarInputFile);
        decodedJarName = URLDecoder.decode(deltaJarName);

        gProps.setProperty("jarname", deltaJarName);

        if ( deltaJarName == null ) {
            logError(4, "No input jar file was provided");
            return false;
        }

        log("Input Jar File      : " + decodedJarName +
            "  src=" + POProcessor.srcDescriptions[po.valueSource]);

        File jarFile = new File(decodedJarName);

        if ( !jarFile.canRead() ) {
            logError(5, "The input jar file cannot be read: " + decodedJarName);
            return false;
        }

        // Determine if this is a restore operation by querying the main
        // attributes for xtr_backupFrom which is set to the path from
        // which this data came.

        try {
            JarFile jf = new JarFile(jarFile);
            inJarEntryCount = jf.size();
            Manifest mf = jf.getManifest();
            mainAttribs = mf.getMainAttributes();

            if ( mainAttribs.getValue(HelperList.meStartMsg) == null )
                logOnly(3, "No Start message provided, default enabled.");
            else
                log(Helper1.resolveMacro(mainAttribs.getValue(HelperList.meStartMsg), gProps));

            if ( mainAttribs.getValue(HelperList.meTargetMsg) == null ) {
                logOnly(3, "No target message provided, default enabled.");

            } else {
                StringTokenizer toks = new StringTokenizer(mainAttribs.getValue(HelperList.meTargetMsg), "|");
                provideTargetMessage = new String[toks.countTokens()];
                po.setUserQuestionArray(k_TargetDir,  provideTargetMessage);

                for ( int ix = 0; toks.hasMoreTokens(); ix++ )
                    provideTargetMessage[ix] = toks.nextToken();
            }

            targetDirFullName = mainAttribs.getValue(HelperList.meBackupFrom);

            if ( targetDirFullName != null ) {
                fRestore = true;
                targetDirName = targetDirFullName;
                po.setDefault(k_TargetDir, targetDirName);
                log("Restore operation activated");
            }

            /* TFB: Now expect the log file name to be provided
             *      on the command line.
             */
            /*
            String attributeValue = mainAttribs.getValue(HelperList.meLogFile);
            if ( attributeValue != null ) {
                logFileName = attributeValue;
                recycleLogStream();
            }
            */

            if ( ckEditionName == null ) {
                String attributeValue = mainAttribs.getValue(HelperList.meCkEditionName);

                if ( attributeValue != null )
                    ckEditionName = attributeValue;
            }

            if ( ckEditionValue == null ) {
                String attributeValue = mainAttribs.getValue(HelperList.meCkEditionValue);

                if ( attributeValue != null )
                    ckEditionValue = attributeValue;
            }

            if ( ckVersion == null ) {
                String attributeValue = mainAttribs.getValue(HelperList.meCkVersion);

                if ( attributeValue != null )
                    ckVersion = attributeValue;
            }

            if ( ckSize == null ) {
                String attributeValue = mainAttribs.getValue(HelperList.meCkSize);

                if ( attributeValue != null )
                    ckSize = attributeValue.trim();
                else
                    ckSize = po.getString(k_ckSize).trim();
            }

            if ( !po.keywordUsed(k_Force) ) {
                String attributeValue = mainAttribs.getValue(HelperList.meForce);

                if (attributeValue != null)
                    po.setDefault(k_Force, attributeValue.trim());
            }

            if ( !po.keywordUsed(k_DupCheck) ) {
                String attributeValue = mainAttribs.getValue(HelperList.meDupCheck);

                if (attributeValue != null)
                    po.setDefault(k_DupCheck, attributeValue);
            }

            if ( !po.keywordUsed(k_APAR) ) {
                String attributeValue =  mainAttribs.getValue(HelperList.meAPAR);

                if ( attributeValue != null )
                    APAR = attributeValue;
            }

            // Check if we have any Property Files to update

            int ufNum = 1;
            int ufFR  = 0;
            String fileKey = HelperList.mePropFile + ufNum;
            String pfName = mainAttribs.getValue(fileKey);

            while ( pfName != null ) {
                PFUpdates pfu = new PFUpdates();
                pfu.propertyFileName = pfName;
                pfu.puList = new Vector();
                pfFileUpdate.add(pfu);
                ufFR = 1;
                String function = mainAttribs.getValue(fileKey + HelperList.mePropFun + ufFR);

                while ( function != null ) {
                    String key  = mainAttribs.getValue(fileKey + HelperList.mePropKey + ufFR);
                    PUEvent pue = new PUEvent(function, key);

                    if ( pue.value == null )
                        pue.value = mainAttribs.getValue(fileKey + HelperList.mePropValue + ufFR);

                    pfu.puList.add(pue);

                    ufFR++;
                    function = mainAttribs.getValue(fileKey + HelperList.mePropFun + ufFR);
                }

                ufNum++;
                fileKey = HelperList.mePropFile + ufNum;
                pfName = mainAttribs.getValue(fileKey);
            }

            // Now check if any NoBackUpJar requests were made.

            ufNum = 1;
            fileKey = HelperList.meNoBackUpJar + ufNum;
            String ufName  = mainAttribs.getValue(fileKey);

            while ( ufName != null ) {
                // the entries look like:
                //    1*SomeFileSpec

                StringTokenizer toks = new StringTokenizer(ufName, "*");

                if (toks.countTokens() != 2) {
                    logError(26, "Invalid tokenCount for noBackUpJar request (" + ufName + "), entry ignored.");

                } else {
                    String cond     = toks.nextToken();
                    String filespec = toks.nextToken();
                    noBackupJar.put(filespec, cond);
                    cond = cond.equals("1") ? " if Exists " : " if Not Exists ";
                    logOnly(prolix3, "Bypassing BackupJar file creation " + cond + filespec);
                }

                ufNum++;
                fileKey = HelperList.meNoBackUpJar + ufNum;
                ufName  = mainAttribs.getValue(fileKey);
            }

            // now check if we have any jar files to re-Sequence
            ufNum = 1;
            fileKey = HelperList.meReSeqJar + ufNum;
            pfName = mainAttribs.getValue(fileKey);

            while ( pfName != null ) {
                String jarList = mainAttribs.getValue(HelperList.meReSeqList + ufNum);
                if (jarList == null) {
                    logError(106, "Main manifest may be corrupted, " + HelperList.meReSeqList + ufNum +
                                  " seems to be missing.");
                } else {
                    reSequenceJar.put(pfName, jarList);
                }

                ufNum++;
                fileKey = HelperList.meReSeqJar + ufNum;
                pfName = mainAttribs.getValue(fileKey);
            }

        } catch ( IOException ex ) {
            logError(85, "failure processing the input jar file", ex);
            return false;
        }

        return true;
    }

    protected String getUpdateReplaceText(int updateNo, int replaceNo)
    {
        String findKey =
            HelperList.meFileUp + updateNo +
            HelperList.meFileUpRepl + replaceNo;

        return mainAttribs.getValue(findKey);
    }

    protected String getUpdateFindText(int updateNo, int replaceNo)
    {
        String findKey =
            HelperList.meFileUp + updateNo +
            HelperList.meFileUpFind + replaceNo;

        return mainAttribs.getValue(findKey);
    }

    protected String getUpdateFileComponent(int updateNo, int componentNo)
    {
        String componentKey =
            HelperList.meFileUp + updateNo +
            HelperList.meFileUpComponent + componentNo;

        return mainAttribs.getValue(componentKey);
    }

    protected String getUpdateFileName(int updateNo)
    {
        // 'xtr_FileUp1', 'xtr_FileUp2', ...

        String fileKey = HelperList.meFileUp + updateNo; 

        String updateFileName = mainAttribs.getValue(fileKey);

        if ( updateFileName == null )
            return null;

        return ResolveMacro(updateFileName);
    }

    protected boolean updateFileExists(String updateFileName)
    {
        String fullFileName = targetDirFullName + updateFileName.replace( '/', File.separatorChar );

        if ( !fileExists(fullFileName) ) {
            logOnly(prolix3, "Update file not found; skipping: " + updateFileName);
            return false;

        } else {
            logOnly(prolix3, "Noted file update: " + updateFileName);
            return true;
        }
    }

    // Establish the target directory.  This may be specified on the
    // command line.  Alternatively, the user must input the target
    // directory.  Answer true or false, telling if the target
    // directory could be established.

    // Temporary directory processing questions ...

    // Establish the temp directory.  This is set by default, and may
    // be overridden from the command line.  Answer true or false,
    // telling if the temporary directory was established.

    protected static final String[] tmpDoesNotExistQuestion(String tmpFileName) {
        return new String[] {
            " The temporary directory [ " + tmpFileName + " ] does not exist.",
            " Please perform one of the following actions:",
            "   1) Enter a different temporary directory.",
            "   2) Enter 'exit' to abort this update.",
            "   3) Verify that the directory exists then press enter.",
            " The -TmpDir option may be used to specify a temporary directory",
            " from the command line.",
            " : "
        };
    }

    protected static final String[] tmpNotADirectoryQuestion(String tmpFileName) {
        return new String[] {
            " The temporary directory [ " + tmpFileName + " ] is not a directory.",
            " Please perform one of the following actions:",
            "   1) Enter a different temporary directory.",
            "   2) Enter 'exit' to abort this update.",
            "   3) Verify that the directory exists then press enter.",
            " The -TmpDir option may be used to specify a temporary directory",
            " from the command line.",
            " : "
        };
    }

    protected static final String[] tmpNotWritableQuestion(String tmpFileName) {
        return new String[] {
            " Temporary directory [ " + tmpFileName + " ] is not writable.",
            " Please perform one of the following actions:",
            "   1) Enter a different temporary directory.",
            "   2) Enter 'exit' to abort this update.",
            "   3) Verify that the directory is writable then press enter.",
            " The -TmpDir option may be used to specify a temporary directory",
            " from the command line.",
            " : "
        };
    }

    protected static final String[] tmpCannotCreateQuestion(String tmpFileName) {
        return new String[] {
            " Temporary directory [ " + tmpFileName + " ] will not accept a file.",
            " Please perform one of the following actions:",
            "   1) Enter a different temporary directory.",
            "   2) Enter 'exit' to abort this update.",
            "   3) Verify that the directory is writable then press enter.",
            " The -TmpDir option may be used to specify a temporary directory",
            " from the command line.",
            " : "
        };
    }

    protected String targetDirName     = null;
    protected String targetDirFullName = null;

    protected boolean establishTargetDir()
    {
        // targetDirName  = RetrieveValue(o_TargetDir, null, provideTargetMessage);

        targetDirName  = po.getString(k_TargetDir);

        if ( targetDirName.length() == 0 ) {
            logError(6, "No target Directory was provided");
            return false;
        }

        targetDirName = trimSlash(targetDirName, TRIM_BOTH);

        File targetFile = new File(targetDirName);

        if ( !targetFile.isDirectory() ) {
            //TBD   Ask if we should Create it

            logError(7, "Target is not a directory: " + targetDirName);
            return false;
        }

        if ( !targetFile.canWrite() ) {
            logError(8, "Cannot write to target: " + targetDirName);
            return false;
        }

        targetDirName += slash;
        targetDirFullName = targetFile.getAbsolutePath();

        log("Target Directory    : " + targetDirFullName);

        return true;
    }

    protected String wasHomeDirName     = null;
    protected String wasHomeDirFullName = null;
    protected boolean establishWASHomeDir()
    {
        // targetDirName  = RetrieveValue(o_TargetDir, null, provideTargetMessage);

        wasHomeDirName  = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT );

        if ( wasHomeDirName.length() == 0 ) {
            logError(6, "No target Directory was provided");
            return false;
        }

        wasHomeDirName = trimSlash( wasHomeDirName, TRIM_BOTH);

        File wasHomeFile = new File(wasHomeDirName);

        if ( !wasHomeFile.isDirectory() ) {
            //TBD   Ask if we should Create it

            logError(7, "WAS Home is not a directory: " + wasHomeDirName);
            return false;
        }

        if ( !wasHomeFile.canWrite() ) {
            logError(8, "Cannot write to target: " + wasHomeDirName);
            return false;
        }

        wasHomeDirName += slash;
        wasHomeDirFullName = wasHomeFile.getAbsolutePath();

        log("WAS Home Directory    : " + wasHomeDirFullName);

        return true;
    }


    // This method requires that the scanData be initialized.
    //   and that the basic scan has been performed so we know which files are being installed/updated
    protected boolean scanFindReplace() {
        // Now see if we have any files for find a string and replace.

        logOnly(prolix3, "Processing scanFindReplace");
        String updateFileName;

        for ( int updateFileNo = 1;
              (updateFileName = getUpdateFileName(updateFileNo)) != null;
              updateFileNo++ ) {

           //System.out.println( this.getClass().getName() + "::scanFindReplace : " + updateFileName );

            if ( !updateFileExists(updateFileName) ) {
               logOnly(prolix3, "   File " + updateFileName + " doesn't currently exist, checking to see if this is a new file." );
               // We check delete files, because if its a new file, its marked as deleted in terms of backup.
               if (scanData.deleteFiles.contains( updateFileName ) ) {
                  logOnly(prolix3, "   File " + updateFileName + " is a new file - will continue." );
               } else {
                  logOnly(prolix3, "   File " + updateFileName + " not found - skipping update." );
                  continue;
               }
            }

            FUEntry updateData = new FUEntry(updateFileName);
            ufFileVector.add(updateData);

            String componentCountText = getUpdateFileComponent(updateFileNo, 0);

            if ( componentCountText != null ) {
                int componentCount;

                try {
                    componentCount = Integer.parseInt(componentCountText);

                } catch ( NumberFormatException e ) {
                    logOnly(prolix1, "Non-valid component count: " + componentCountText);
                    logOnly(prolix1, "Assuming 0 components.");
                    componentCount = 0;
                }

                if ( componentCount > 0 ) {
                    updateData.components = new String[componentCount];
                    logOnly(prolix3, "  Required component for find/replace:");

                    for ( int compNo = 0; compNo < componentCount; compNo++ ) {
                        String nextComponent = getUpdateFileComponent(updateFileNo, compNo + 1);
                        logOnly(prolix3, "      " + nextComponent);
                        
                        updateData.components[compNo] = nextComponent;
                    }
                }
            }

            String findText;

            for ( int findNo = 1;
                  (findText = getUpdateFindText(updateFileNo, findNo)) != null;
                  findNo++ ) {
                String replaceText = getUpdateReplaceText(updateFileNo, findNo);

                logOnly(prolix4, "    Replace: [ " + findText    + " ]");
                logOnly(prolix4, "    With   : [ " + replaceText + " ]");

                updateData.frList.add( new FRList(findText, replaceText) );
            }
        }

        return true;
    }

    protected boolean establishTmpDir()
    {
        boolean completed = false,
        result = false;

        String[] tmpDirQuestion = null;

        String tmpDirName = po.getString(k_TmpDir);

        while ( !completed ) {
            if ( tmpDirQuestion != null) {
                String answerFileName = getKeyBoard(tmpDirQuestion);

                if ( (answerFileName != null) && (!answerFileName.equals("")) )
                    tmpDirName = answerFileName;

                tmpDirQuestion = null;
            }

            if ( (tmpDirName != null) && (tmpDirName.equals("exit")) ) {
                log("User requested exit; aborting update.");
                completed = true;
                result = false;

            } else {
                log("Testing Temporary Directory : " + tmpDirName);

                File tmpDirFile = new File(tmpDirName);

                if ( !tmpDirFile.exists() ) {
                    log("Attention -- Temporary directory does not exist: " + tmpDirName);
                    tmpDirQuestion = tmpDoesNotExistQuestion(tmpDirName);

                } else if ( !tmpDirFile.isDirectory() ) {
                    log("Attention -- Temporary directory is not a directory: " + tmpDirName);
                    tmpDirQuestion = tmpNotADirectoryQuestion(tmpDirName);

                } else {
                    String tmpFullName = tmpDirFile.getAbsolutePath();

                    log("Full Temporary Directory : " + tmpFullName);

                    File testFile;

                    try {
                        testFile = File.createTempFile("testWrite", null, tmpDirFile);

                    } catch ( IOException ex ) {
                        testFile = null;
                        log("Attention -- Unable to create temporary file at: " + tmpFullName );
                        tmpDirQuestion = tmpCannotCreateQuestion(tmpDirName);
                    }

                    if ( testFile != null ) {
                        if ( !testFile.canWrite() ) {
                            log("Attention -- The temporary directory cannot be written: " + tmpFullName);
                            tmpDirQuestion = tmpNotWritableQuestion(tmpDirName);

                        } else {
                            log("The temporary directory is usable.");

                            tmpDirFullName = tmpFullName;
                            completed = true;
                            result = true;
                        }

                        if ( !testFile.delete() ) {
                            String testFileName = testFile.getAbsolutePath();

                            logError(165, "A temporary file could not be deleted: " + testFileName);
                            logError(166, "Review the installation and delete this file if necessary.");

                            result = false;
                        }
                    }
                }
            }
        }

        return result;
    }

    public static final String earTmpPropertyName = "delta.ear.tmp.directory" ;
    public static final String earTmpPrefix = "ear" ;

    protected String earTmpFullName = null;

    protected boolean establishEARTmpDir()
    {
        if ( mainAttribs.getValue(HelperList.mePrepareEARTmp) == null ) {
            log("No EAR temporary directory is required.");
            return true;

        } else {
            log("An EAR temporary directory is required.");

            File earFile = null;
            int mkdirCount = 0;

            for ( int fileCount = 0;
                  (fileCount < 10000) && (mkdirCount < 100) && (earFile == null);
                  fileCount++ ) {

                String nextName = "ear_" + fileCount + ".tmp";
                String nextPath = tmpDirFullName + slash + nextName;

                File nextFile = new File(nextPath);

                if ( !nextFile.exists() ) {
                    if ( nextFile.mkdir() )
                        earFile = nextFile;
                    else
                        mkdirCount++;
                }
            }

            if ( earFile == null ) {
                logError(190, "A temporary directory not be created: " + tmpDirFullName);
                return false;
            }

            earTmpFullName = earFile.getAbsolutePath();
            System.setProperty(earTmpPropertyName, earTmpFullName);

            log("Full EAR Temporary Directory: " + earTmpFullName);
            return true;
        }
    }

    protected InstallationData installation = null;

    protected boolean establishInstances()
    {
        if ( mainAttribs.getValue(HelperList.mePrepareInstances) == null ) {
            log("No configuration instance data is required.");
            return true;

        } else {
            log("Configuration instance data is required; scanning.");

            boolean didPrepare;
            //installation = new InstallationData(targetDirFullName);
            installation = new InstallationData( wasHomeDirFullName);

            try {
                didPrepare = installation.prepare(); // throws IOException, SAXException

            } catch ( Exception e ) {
                logError(195, "Exception retrieving instance data", e);
                return false;
            }

            if ( !didPrepare ) {
                logError(196, "Failed to prepare instance data.");
                return false;
            }

            correctInstances();
            displayInstances();

            return true;
        }
    }

    // Fixup the instance data, changing all of the recorded paths
    // into paths which are proper for the current platform.

    protected void correctInstances()
    {
        Iterator instances = installation.getInstances();
        while ( instances.hasNext() ) {
            InstanceData nextData = (InstanceData) instances.next();
            correctInstance(nextData);
        }
    }

    protected void correctInstance(InstanceData instanceData)
    {
        instanceData.setLocation( asPath(instanceData.getLocation()) );

        Iterator nodeData = instanceData.getNodeData();
        while ( nodeData.hasNext() ) {
            NodeData nextNodeData = (NodeData) nodeData.next();
            nextNodeData.setBinaryLocation( asPath(nextNodeData.getBinaryLocation()) );
        }

        Iterator deploymentData = instanceData.getDeploymentData();
        while ( deploymentData.hasNext() ) {
            DeploymentData nextDeploymentData = (DeploymentData) deploymentData.next();
            nextDeploymentData.setBinariesURL( asPath(nextDeploymentData.getBinariesURL()) );
        }
    }

    public void displayInstances()
    {
        InstallationData.DisplayCallback localCallback =
            new InstallationData.DisplayCallback() {
                public void println(final String text) {
                    log(text);
                }
            };

        installation.display(localCallback);
    }

    // Establish the backup jar file.  Answer true or false, telling
    // if the backup jar file could be established.  The backup jar
    // may be specified on the command line.  If not specified on the
    // command line, the backup jar is generated from the delta jar.
    // In either case, user confirmation is required before accepting
    // a backup jar that will overwrite an existing file.

    protected static final String backupJarSuffix = "_backup";

    protected boolean fRestore = false;

    protected Hashtable noBackupJar = new Hashtable();

    protected String backupJarName = null;

    protected boolean establishBackupJar()
    {
        if ( fRestore )    // if a restore operation, no backup is created
            return true;

        // check if a noBackUpJar request was specified

        Enumeration eNum = noBackupJar.keys();

        while ( eNum.hasMoreElements() ) {
            String file2check = (String) eNum.nextElement();
            String temp       = (String) noBackupJar.get(file2check);
            file2check        = ResolveMacro(file2check);
            File theFile      = new File(file2check);

            boolean actualState  = theFile.exists();
            boolean desiredState = (temp.equals("1")) ? true : false;

            if ( desiredState == actualState ) {
                log("Bypassing backup creation due to");

                if ( desiredState )
                    log(" the existance of " + file2check);
                else
                    log(" the absence of " + file2check);

                fSkipBackup = true;
                return true;
            }
        }

        // backupJarName = RetrieveValue(o_BackupJar, null, noUserQuestion);

        backupJarName = po.getString(k_BackupJar);

        if ( backupJarName == null ) {
            String[] jarPathInfo = new String[helper.k_elCount];

            if ( !helper.ParseFileSpec(decodedJarName, jarPathInfo, false) )
                return false;

            if ( APAR == null ) {   // this is not an eFix run
                backupJarName = jarPathInfo[helper.k_drive] +
                                jarPathInfo[helper.k_path]  +
                                jarPathInfo[helper.k_name]  +
                                backupJarSuffix + "."       +
                                jarPathInfo[helper.k_ext];
            } else {
                backupJarName = targetDirFullName + slash     +
                                "eFix" + slash + APAR + slash +
                                jarPathInfo[helper.k_name]    +
                                backupJarSuffix + "."         +
                                jarPathInfo[helper.k_ext];
            }

            logOnly(prolix2, "Selected backup name: " + backupJarName);
        }

        File backupFile = new File(URLDecoder.decode(backupJarName));
        backupJarName = backupFile.getAbsolutePath();

        if ( backupFile.exists() ) {
            log("Attention -- Backup jar file already exists: " + backupJarName);
            boolean overWriteBackupJar = po.getBool(k_OverWriteBackupJar);

            if ( overWriteBackupJar ) {
                log("Backup Jar File     : " + backupJarName);
                log( POProcessor.srcDescriptions[po.valueSource] + " accepts overwrite of existing backup file.");
                errorCount--;
                return true;

            } else {
                log( POProcessor.srcDescriptions[po.valueSource] +
                     " does not accept overwrite of backup jar; aborting: " + backupJarName);
                return false;
            }

        } else {
            log("Backup Jar File     : " + backupJarName);
            return true;
        }
    }

    protected boolean determineInstalledComponents()
    {
        // If affectedComponents were specified in the main manifest
        // then we will determine what components are installed.  If no
        // signature is provided then we will assume the component is
        // installed.

        if ( !po.getBool(k_ComponentCheck) ) {
            log("Component checking deactiviated by request.");
            return true;
        }

        if ( affectedComponents == null )
            affectedComponents = new HashMap();

        if ( installedComponents == null )
            installedComponents = new HashMap();

        String cList = mainAttribs.getValue(HelperList.meAffectedComponents);
        if ( cList == null ) {
            log("Component checking deactiviated, affected components entry is null.");
            return true;
        } else {
            StringTokenizer toks = new StringTokenizer(cList, "," );
            log("This update applies to the following components:" );

            while ( toks.hasMoreTokens() ) {
                String temp = toks.nextToken();
                affectedComponents.put(temp, null);
                log("    " + temp);
            }
        }

        boolean exitStatus = true;
        int cn = 0;
        log(" ");
        log("Installed Components:");

        while ( cn != -1 ) {
            String mKey = HelperList.meComponentName + (++cn);
            String list = mainAttribs.getValue(mKey);

            logDebug("Debug --  check for mKey " + mKey + "  value=" + list );

            boolean installed = true;
            String componentName = null;

            if ( list != null ) {
                StringTokenizer toks = new StringTokenizer(list, "," );
                componentName = toks.nextToken().trim();

                logDebug("Debug -- check for mKey " + mKey + "  value=" + list );

                while ( toks.hasMoreTokens() ) {
                    String temp = toks.nextToken().trim();

                    logDebug("Debug -- Signature(" + temp + ")");

                    if ( temp.equals(HelperList.meNoSignature) ) {
                        installed = true;
                        continue;
                    }

                    String cond = temp.substring(0, 1); // the first position is a plus or minus
                    String sig  = temp.substring(1);

                    if ( cond.equals("+") ) {
                        installed = testExistanceOf(sig, true);

                    } else if ( cond.equals("-") ) {
                        installed = testExistanceOf(sig, false);

                    } else {
                        logError(128, "Invalid condition (+|-) for signature " + list);
                        exitStatus = false;
                    }

                    logDebug("Debug --  installed = (" + installed + ")");

                    if ( !installed )
                        break;
                }

            } else {
                cn = -1;
            }

            if ( (installed) && (componentName != null ) ) {
                installedComponents.put(componentName, list);
                log("    " + componentName);

                // Adding list of components to manifest file for uninstall.

                if ( backupInstalledComponents == null ) {
                    backupInstalledComponents = componentName;
                } else {
                    // Defect 120991 Jay Sartoris
                    backupInstalledComponents = backupInstalledComponents + "," + componentName;
                }
            }
        }

        log(" ");

        return exitStatus;
    }

    protected boolean isAllowedByComponent(String filename, Attributes attribs)
    {
        // If the files does not have a component then we accept it
        // check if this files component is in the affectedComponents
        // list then check if the component is installed

        if ( fDebug )
            logOnlyDebug(prolix4, "isAllowedByComponent: " + filename);

        if ( attribs == null ) {
            logOnlyDebug(prolix4, "isAllowedByComponent: attribs are null");
            return true;
        }

        if ( testKeyForRestoreOnly(attribs) ) {
            logOnlyDebug(prolix4, "isAllowedByComponent: meRestoreOnly is set");

            return true;
        }

        String fileComponents = (String) attribs.getValue(HelperList.meComponent);

        if ( fileComponents == null ) {
            logOnlyDebug(prolix4, "Debug -- isAllowedByComponent: meComponent is null");

            return true;
        }

        String fileComponentName = null;
        boolean weWantIt = false;

        StringTokenizer toks = new StringTokenizer(fileComponents, ",");

        while ( toks.hasMoreTokens() ) {
            fileComponentName = toks.nextToken();

            // Are we interested in the component?

            if ( !affectedComponents.containsKey(fileComponentName) ) {
                if ( fDebug ) {
                    logOnlyDebug(prolix4, "isAllowedByComponent: fileComponent " +
                                          fileComponentName + " not in list.");
                }

            } else {
                // Is the component installed?

                if ( !installedComponents.containsKey(fileComponentName) ) {
                    if ( fDebug ) {
                        logOnlyDebug(prolix4, "isAllowedByComponent: fileComponent " +
                                              fileComponentName + " not installed.");
                    }

                } else {
                    weWantIt = true;
                }
            }
        }

        if ( fDebug )
            logOnlyDebug(prolix4, "isAllowedByComponent: fileComponent " +
                                  fileComponentName + " selected: " + weWantIt );

        return weWantIt;
    }

    protected boolean testExistanceOf(String relPath, boolean condition)
    {
        File testFile = new File(targetDirFullName + relPath );

        if ( fDebug )
            logDebug("Testing for (" + testFile.getAbsolutePath() + ")");

        return ( testFile.exists() == condition );
    }

    protected String productFileType   = null;
    protected String productVersionKey = null;
    protected String productFileName   = null;

    protected boolean handleProductFile()
    {
        boolean sawError = false;

        if ( po.keywordUsed(k_ProductFileType) )
            productFileType = po.getString(k_ProductFileType);
        else
            productFileType = mainAttribs.getValue(HelperList.meProductFileType);

        if ( productFileType != null ) {

            if ( productFileType.equals(HelperList.meXMLProductFile) ) {
                log("Product file type: XML");

            } else if ( productFileType.equals(HelperList.mePropertiesProductFile) ) {
                log("Product file type: Properties");

                if ( po.keywordUsed(k_PropertiesVersionKey) )
                    productVersionKey = po.getString(k_PropertiesVersionKey);
                else
                    productVersionKey = mainAttribs.getValue(HelperList.mePropertiesVersionKey);

                if ( productVersionKey == null )
                    log("No product version key was set.");
                else
                    log("The product version key was set to [ " + productVersionKey + " ].");

            } else {
                logError(139, "Unknown product file type: " + productFileType);
                sawError = true;
            }

        } else {
            logOnly(prolix3, "No set product file type.");
        }

        String rawProductFileName;

        if ( po.keywordUsed(k_ProductFileName) )
            rawProductFileName = po.getString(k_ProductFileName);
        else
            rawProductFileName = mainAttribs.getValue(HelperList.meProductFile);

        if ( rawProductFileName != null ) {
            logOnly(prolix3, "Raw product file [ " + rawProductFileName + " ]");

            productFileName = ResolveMacro(rawProductFileName);

            if ( !productFileName.equals(rawProductFileName) )
                log("Product file [ " + productFileName + " ]");

        } else {
            log("No set product file.");
        }

        if ( (productFileType == null) && (productFileName != null) ) {
            logError(140, "A product file (" + productFileName + ") was set but no product file type was set.");
            sawError = true;

        } else if ( (productFileType != null) && (productFileName == null) ) {
            logError(141, "A product file type (" + productFileType + ")" +
                          " was set by no product file was set.");
            sawError = true;
        }

        // check to ensure the file exists

        if ( productFileName != null ) {
            File testFile = new File(productFileName);

            if ( !testFile.exists() ) {
                if ( !check4SpareProduct(productFileName) ) {
                    logError(57, "The product file (" + productFileName + ")" +
                                 " does not exist. This process can not continue.");
                    sawError = true;
                }
            }
        }

        return( !sawError );
    }

    protected boolean check4SpareProduct(String productFileName)
    {
        if ( mainAttribs == null )
            return false;

        // Here we want to extract the path from the productFileName:

        String[] parts = new String[helper.k_elCount];
        if ( !helper.ParseFileSpec(productFileName, parts, fDebug) ) {
            logError(129, "Helper.ParseFilespec() failed parsing (" + productFileName + ")");
            return false;
        }

        if ( (installedComponents == null) || (installedComponents.size() == 0) ) {
            log("The required product file is not present, no installed components were identified.");
            log("This appears to be a corrupted install or a invalid target.");
            return false;
        }

        String path = parts[helper.k_drive] + parts[helper.k_path];

        int ndx = -1;

        do {
            String aFileName = mainAttribs.getValue(HelperList.meSpareProductFile + (++ndx));

            if ( aFileName == null ) {
                ndx = -2;  // this is to exit the do loop

            } else {  // extract the file from the jar

                String newProductFileName = path + aFileName;
                log("Installing new default productFile " + path + newProductFileName);

                String adjPath = trimSlash( asEntry(path), TRIM_ONE );

                File justThePath = new File(adjPath);

                if ( !justThePath.exists() ) {

                    if ( !justThePath.mkdirs() ) {
                        logError(138, "Unable to create path (" + justThePath.getAbsolutePath() + ")" +
                                      " in support of installing spare " + aFileName);
                        return false;
                    }
                }

                byte[] data = readJarEntry(decodedJarName, aFileName);

                FileOutputStream fos = null;

                try {
                    fos = new  FileOutputStream(path + aFileName);
                    fos.write(data);

                } catch ( IOException ex) {
                    logError(136, "Failure to write spare product file (" + newProductFileName + ")", ex);

                } finally {
                    if ( fos != null ) {
                        try {
                            fos.close();

                        } catch ( IOException ex ) {
                            logError(137, "Failure to close spare product file (" + newProductFileName + ") ", ex);
                        }
                    }
                }
            }

        } while ( ndx >= 0 );

        return true;
    }

    // Handle prerequisite verification, including both testing the
    // need to perform verficiation, and actually performing
    // verification.  Answer true or false, telling if prerequisites
    // are valid, or if prerequisites did not need to be checked.

    protected boolean fSkipVer = false;

    protected boolean handlePrerequisites()
    {
        if ( fSkipVer ) {
            logOnly(prolix3, "Size/Edition/Version verification has been bypassed by request.");
            return true;

        } else {
            return verifyPrerequisites();
        }
    }

    // Perform basic prerequisite verification.  Answer true or false,
    // telling if prerequisites are valid.

    protected boolean verifyPrerequisites()
    {
        logOnly(prolix3, "Verifying prerequisites");

        boolean didVerifySize         = verifySize();
        boolean didVerifyVersion      = verifyVersion();
        boolean didVerifyEditionName  = verifyEditionName();
        boolean didVerifyEditionValue = verifyEditionValue();

        boolean didVerify = didVerifySize &&
                            didVerifyVersion &&
                            didVerifyEditionName &&
                            didVerifyEditionValue;

        if ( didVerify )
            logOnly(prolix3, "Prerequisites are satisfied.");
        else
            logError(50, "Prerequisites were not satisified.");

        return didVerify;
    }

    protected String ckSize = null; // required available disk space

    protected boolean verifySize()
    {
        if ( fRestore ) {
            logOnly(prolix3, "Performing restore: Skipping check on available size.");
            return true;
        }

        String formattedSize;

        if ( ckSize == null ) {
            ckSize = "?";
            formattedSize = ckSize;

        } else {
            if ( ckSize.equals("?") ) {
                formattedSize = "?";

            } else {
                long actualSize;

                try {
                    actualSize = Long.parseLong(ckSize);

                } catch ( NumberFormatException ex ) {
                    logError(178, "The specific size constraint, " + ckSize + ", is not a number.");
                    return false;
                }

                formattedSize = helper.FmtNum(actualSize, 0, 0);
            }
        }

        logOnly(prolix3, "Space requirements       : " + formattedSize + " Bytes");

        if ( ckSize.equals("?") )
            return true;

        boolean didVerify = true;

        Vector errMsg = new Vector();

        FileSystemSpace fss = new FileSystemSpace();
        didVerify = fss.ensure(errMsg, ckSize, targetDirFullName, fDebug);

        if ( errMsg.size() > 0 ) {
            didVerify = false;
            logError(52, "The class FileSystemSpace() returned the following messages:");
            for ( int i = 0; i < errMsg.size(); i++ )
                log((String) errMsg.elementAt(i));
        }

        if ( !didVerify ) {
            logError(51, "The minimum space required of " + formattedSize +
                         " bytes is not available on fileSystem " + targetDirFullName);

        } else {
            logOnly(prolix3, "Verified minimum space requirements.");
        }

        return didVerify;
    }

    protected String ckVersion = null;   // required product version

    protected boolean verifyVersion()
    {
        if ( ckVersion == null )
            ckVersion = "?";

        logOnly(prolix3, "Version requirements     : " + ckVersion);

        if ( ckVersion.equals("?") ) {
            logOnly(prolix3, "Version requirement is all-inclusive; skipping version check.");
            return true;
        }

        if ( productFileType == null ) {
            logError(142, "Version requirements cannot be checked: a valid product file type is not available.");
            return false;
        }

        if ( getStartingVersion() == null ) {
            logError(143, "Failed to verify version: cannot load product version.");
            return false;
        }

        boolean didVerify = false;

        StringTokenizer toks = new StringTokenizer(ckVersion, ",");
        while ( !didVerify && toks.hasMoreTokens() ) {
            String tok = toks.nextToken().trim();
            didVerify = startingVersion.equals(tok);
        }

        if ( !didVerify ) {
            logError(115, "The current Websphere version of " + startingVersion +
                          " is incorrect for this maintenance update." +
                          "  This update is for versions " + ckVersion);
        }

        return didVerify;
    }

    protected String ckEditionName = null;   // required product edition

    protected boolean verifyEditionName()
    {
        if ( ckEditionName == null )
            ckEditionName = "?";

        logOnly(prolix3, "Edition Name requirements  : " + ckEditionName);

        if ( ckEditionName.equals("?") ) {
            logOnly(prolix3, "Edition name requirement is all-inclusive; skipping edition name check.");
            return true;
        }

        if ( productFileType == null ) {
            logError(144, "Edition name requirements cannot be checked:" +
                          " a valid product file type is not available.");
            return false;

        }
        else if ( !productFileType.equals(HelperList.meXMLProductFile) ) {
            logError(145, "Edition name requirements cannot be checked;" +
                          " the product type is " + productFileType +
                          ", but edition name checking is only valid for an XML type product file.");
            return false;
        }

        if ( getEditionName() == null ) {
            logError(146, "Failed to verify edition name: cannot load product edition name.");
            return false;
        }

        logOnly(prolix3, "Current Edition Name       : " + editionName);

        StringTokenizer toks = new StringTokenizer(ckEditionName, ",");
        boolean didVerify = false;

        while ( !didVerify && toks.hasMoreTokens() ) {
            String tok = toks.nextToken().trim();
            didVerify = editionName.equals(tok);
        }

        if ( !didVerify ) {
            logError(117, "The current WebSphere edition nameof " + editionName +
                          " is incorrect for this maintenance update, which requires " + ckEditionName + ".");
        }

        return didVerify;
    }

    protected String ckEditionValue = null;   // required product edition

    protected boolean verifyEditionValue()
    {
        if ( ckEditionValue == null )
            ckEditionValue = "?";

        logOnly(prolix3, "Edition Value requirements : " + ckEditionValue);

        if ( ckEditionValue.equals("?") ) {
            logOnly(prolix3, "Edition value requirement is all-inclusive; skipping edition value check.");
            return true;
        }

        if ( productFileType == null ) {
            logError(147, "Edition name requirements cannot be checked:" +
                          " a valid product file type is not available.");
            return false;

        } else if ( !productFileType.equals(HelperList.meXMLProductFile) ) {
            logError(148, "Edition name requirements cannot be checked;" +
                          " the product type is " + productFileType +
                          ", but edition name checking is only valid for an XML type product file.");
            return false;
        }

        if ( getEditionValue() == null ) {
            logError(149, "Failed to verify edition value: cannot load product edition value.");
            return false;
        }

        logOnly(prolix3, "Current Edition Value      : " + editionValue);
        StringTokenizer toks = new StringTokenizer(ckEditionValue, ",");

        boolean didVerify = false;

        while ( !didVerify && toks.hasMoreTokens() ) {
            String tok = toks.nextToken().trim();
            didVerify = editionValue.equals(tok);
        }

        if ( !didVerify ) {
            logError(127, "The current Websphere edition of " + editionValue +
                          " is incorrect for this maintenance update," +
                          " which requires " + ckEditionValue + ".");
        }

        return didVerify;
    }

    // The version information is needed whenever an XML event is
    // to be registered.
    //
    // The version information is needed if version or edition checking
    // must be done.

    protected boolean completeVersionInfo()
    {
        if ( productFileName == null ) {
            logOnly(prolix3, "Skipping loading of product info for backup:" +
                             " no product file was specified.");
            return true;

        } else if ( po.keywordUsed(k_UpdateProductFile) && !po.getBool(k_UpdateProductFile) ) {
            logOnly(prolix3, "Skipping loading of product info for backup:" +
                             " product file update was disabled.");
            return true;

        } else {
            logOnly(prolix3, "Loading product information for backup.");
        }

        // The starting version is always required.

        if ( getStartingVersion() == null ) {
            logError(150, "Failing: cannot load product version.");
            return false;

        } else {
            logOnly(prolix3, "Established product version.");
        }

        // The build number and build date are only required when the
        // product file is an XML file.  No requirements have been set
        // to allow build number and build date to be updatable in a
        // properties product file.

        if ( productFileType.equals(HelperList.meXMLProductFile) ) {

            // The product edition name and edition value are required
            // when version checking is enabled, but in this case they
            // are already loaded during prerequisite checking and
            // need not be loaded here.

            // The starting build number is only required when
            // updating the build number, which is only the case when
            // the ending build number is not null.

            if ( getEndingBuildNumber() != null ) {
                if ( getStartingBuildNumber() == null ) {
                    logError(151, "Failing: cannot load product build number.");
                    return false;

                } else {
                    logOnly(prolix3, "Established product build number.");
                }
            }

            // Likewise, the starting build date is only required when
            // updating the build date, which is only the case when
            // the ending build date is not null.

            if ( getEndingBuildDate() != null ) {
                if ( getStartingBuildDate() == null ) {
                    logError(152, "Failing retrieval of information for backup:" +
                                  " cannot load product build date.");
                    return false;

                } else {
                    logOnly(prolix3, "Established product build date.");
                }
            }
        }

        logOnly(prolix3, "Successfully loaded product information for backup.");

        return true;
    }

    protected boolean check4Class()
    {
        if ( po.keywordUsed(k_CheckForClass) ) {
            log("Checks for singular occurrence of selected files in progress.  src: cmdLine");

            Properties props = getSetUpCmd();
            int poCount = po.getCount(k_CheckForClass);

            for ( int i = 0; i < poCount; i++) {
                String class2Find = po.getString(k_CheckForClass, i + 1);

                if ( !c4cProcessList(class2Find, props) )
                    return false;
            }

        } else {
            String c2fList = mainAttribs.getValue(HelperList.meCheck4Class);
            if ( c2fList == null )
                return true;

            log("Checks for singular occurrence of selected files in progress.  src: manifest");
            logOnly(prolix3, "   " + c2fList);
            Properties props = getSetUpCmd();
            StringTokenizer toks = new StringTokenizer(c2fList, ",");

            while ( toks.hasMoreTokens() ) {
                String class2Find = toks.nextToken();

                if ( !c4cProcessList(class2Find, props) )
                    return false;
            }
        }

        return true;
    }

    // Displayed if the -CheckForClass finds a duplicate entry:

    protected static final String[] c4cPart1 = {
        "",
        " The above file is scheduled to be updated, however, it has been   ",
        " determined that there are several occurrences of this file on your ",
        " system in the following locations: ",
        ""
    };

    // Second part of the -CheckForClass message:

    protected static final String[] c4cPart2 = {
        "",
        " It is recommended to remove the unnecessary copies, as functionality",
        " may be compromised due to loading order. If assistance is required ",
        " in determining which copies are redundant please call the IBM support",
        " center at $(call4help).",
        "",
        " Please reply 'continue' to continue with this installation.",
        "",
    };

    protected boolean c4cProcessList(String class2Find, Properties props)
    {
        Vector          foundList = new Vector();
        Vector          hitList   = new Vector();
        StringBuffer    errMsg    = new StringBuffer();
        findClassInPath fcip      = new findClassInPath();

        log("  Searching for occurances of " + class2Find);

        // Search the current classPath Environment

        if ( systemClassPath == null )
            getSystemClassPath();

        if ( systemClassPath == null ) {
            logOnly(prolix3, "System classpath could not be obtained,  not checked for " + class2Find);

        } else {
            log("    Interrogating system classpath.");
            logOnly(prolix3, "    Interrogating system classpath: " + systemClassPath);

            int found = fcip.locate(errMsg, systemClassPath, class2Find, hitList);
            handleErrMsg(errMsg, false);

            if ( found == 0 ) {
                logOnly(prolix3, "The class " + class2Find + ", was not found on the system classpath.");

            } else if ( found == 1 ) {
                logOnly(prolix3, "The class " + class2Find + ", was found on the system classpath.");
                String tmp = asEntry( (String) hitList.elementAt(0) );
                foundList.add(tmp);

            } else {   // here we found several
                for ( int i = 0; i < hitList.size(); i++ ) {
                    String tmp = asEntry( (String) hitList.elementAt(i) );
                    foundList.add(tmp);
                }
            }

            hitList.clear();
        }

        // admin.config   A PropertyFile

        File adminConfig = new File(targetDirFullName + slash +
                                    "bin" + slash +
                                    "admin.config");

        if (adminConfig.exists()) {
            Properties aprops = new Properties();

            try {
                aprops.load(new FileInputStream(adminConfig));

            } catch ( FileNotFoundException ex ) {
                logError(121, "FileNotFoundException for " + adminConfig.getAbsolutePath(), ex);
                return false;

            } catch ( IOException ex ) {
                logError(122, "IOException on " + adminConfig.getAbsolutePath(), ex);
                return false;
            }

            String cpKey = "com.ibm.ejs.sm.adminserver.classpath";
            String cpList = aprops.getProperty(cpKey);

            if ( cpList == null ) {
                logOnly(prolix3, "The key " + cpKey + ", was not found in " + adminConfig.getAbsolutePath() );
                logOnly(prolix3, "     The admin.config classpath is being bypassed for the CheckForClass.");

            } else {
                log("    Interrogating admin.config classpath.");
                logOnly(prolix3, "      Interrogating " + adminConfig.getAbsolutePath() +
                                 " classpath, key=" + cpKey);

                int found = fcip.locate(errMsg, cpList, class2Find, hitList);

                if ( errMsg.length() > 0 ) {
                    log(" Processing " + adminConfig.getAbsolutePath()+ "  key=" + cpKey);
                    handleErrMsg(errMsg, false);
                }

                if ( found == 0 ) {
                    logOnly(prolix3, "The class " + class2Find + ", was not found on the admin.config classpath.");

                } else if ( found == 1 ) {
                    logOnly(prolix3, "The class " + class2Find + ", was found on the admin.config classpath.");

                    String tmp = asEntry( (String) hitList.elementAt(0) );

                    if (foundList.contains(tmp))
                        logOnly(prolix3, "   the entry has been registered in a prior search.");
                    else
                        foundList.add(tmp);

                } else {   // here we found several
                    for ( int i = 0; i < hitList.size(); i++ ) {
                        String tmp = asEntry( (String) hitList.elementAt(i) );

                        if ( !foundList.contains(tmp) )
                            foundList.add(tmp);
                    }
                }

                hitList.clear();
            }

        } else {
            logOnly(prolix3, adminConfig.getAbsolutePath() +
                             " was not found, CheckForClass function bypassed.");
        }

        //  here we will read .bat  or .sh Files

        // This tables contains the file name to search and the classPath variable name with in that file

        String[][] batFiles = {
            { "/bin/setenv.bat",            "CP" },
            { "/bin/adminclient.bat",       "WAS_CP" },
            { "/bin/debug/adminserver.bat", "WAS_CP" },
            { "/bin/debug/setupadmin.bat",  "WAS_CP" }
        };

        for ( int f = 0; f < batFiles.length; f++ ) {

            String cpList = c4cProcessBat(errMsg, batFiles[f], props);
            if ( cpList == null )
                continue;

            int found = fcip.locate(errMsg, cpList, class2Find, hitList);
            handleErrMsg(errMsg, false);

            if ( found == 0 ) {
                logOnly(prolix3, "The class " + class2Find +
                                 ", was not found in the " + batFiles[f][0] + " classpath.");

            } else if ( found == 1 ) {
                logOnly(prolix3, "The class " + class2Find +
                                 ", was found on the " + batFiles[f][0] + " classpath.");
                String tmp = asEntry( (String) hitList.elementAt(0) );

                if (foundList.contains(tmp))
                    logOnly(prolix3, "   the entry has been registered in a prior search.");
                else
                    foundList.add(tmp);

            } else {   // here we found several
                for ( int i = 0; i < hitList.size(); i++ ) {
                    String tmp = asEntry( (String) hitList.elementAt(0) );

                    if ( !foundList.contains(tmp) )
                        foundList.add(tmp);
                }
            }
        }

        if ( foundList.size() == 0 ) {
            logOnly(prolix3, "The object " + class2Find + ", was not found.");

        } else if (foundList.size() == 1 ) {
            logOnly(prolix3, "A single occurance of object " + class2Find +
                             ", was not found at " + foundList.elementAt(0));

        } else {
            logRaw("");
            logRaw("               " + class2Find );

            for ( int i = 0; i < c4cPart1.length; i++ ) // output the static part of the message
                logRaw(c4cPart1[i]);

            for ( int i = 0; i < foundList.size(); i++ ) // output the locations
                logRaw((i + 1) + ") " + foundList.elementAt(i));

            for (int i = 0; i < c4cPart2.length; i++ ) // output the static part of the message
                logRaw(Helper1.resolveMacro(c4cPart2[i], gProps));

            String reply = po.getKeyBoard();

            logOnly(prolix2, "Reply was " + reply);

            return reply.equalsIgnoreCase("continue");
        }

        return true;
    }

    protected String c4cProcessBat(StringBuffer errMsg, String[] fileName, Properties props)
    {
        // filename[0] = the file name;   filename[1] = the name of the classpath variable

        File theFile = new File(targetDirFullName + fileName[0]);
        if ( !theFile.exists() ) {
            logOnly(prolix3, "The file " + theFile.getAbsolutePath() + " does not exist," +
                             " CheckForClass function bypassed.");
            return null;
        }

        log("    Interrogating " + fileName[0] );
        logOnly(prolix3, "    Interrogating classpath in " + theFile.getAbsolutePath() );

        Vector vMsg = new Vector();
        Vector lines = helper.file2Vector(vMsg, targetDirFullName + fileName[0]);

        for ( int i = 0; i < vMsg.size(); i++ )
            errMsg.append((String) vMsg.elementAt(i));

        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < lines.size(); i++ ) {
            String tmp = (String) lines.elementAt(i);
            String line = tmp.trim();

            if ( line.length() < 7 )  // minimum length  Set a=b
                continue;

            String lineLC = line.toLowerCase();

            if ( lineLC.substring(0,4).equals("set ") ) {

                StringTokenizer toks = new StringTokenizer(line.substring(4), "=") ;

                if ( toks.countTokens() == 2 ) {
                    String key = toks.nextToken().trim();

                    if ( key.equalsIgnoreCase(fileName[1]) ) {
                        String pathPart = toks.nextToken().trim();

                        if ( sb.length() != 0 )
                            sb.append(";");

                        sb.append(pathPart);
                    }
                }
            }
        }

        props.setProperty(fileName[1], "");  // this is to remove the %WAS_CP% from the list

        return ( resolvePctPct(sb.toString(), props) );
    }

    protected String resolvePctPct(String data, Properties prop)
    {
        StringBuffer sb = new StringBuffer();
        int currentPos = 0;
        int startPos;

        while ( (startPos = data.indexOf('%', currentPos)) != -1 ) {

            if ( !Character.isDigit(data.charAt(startPos+1)) ) { //  bypass  %1
                int endPos = data.indexOf('%', startPos+1);

                if ( endPos != -1 ) {
                    sb.append(data.substring(currentPos, startPos));  // append the data upto the first %

                    String key = data.substring(startPos + 1, endPos);
                    String value = prop.getProperty(key);

                    currentPos = endPos + 1;   // the place where we want to start scanning again

                    if ( value == null )   // did not find the key put the %??% back in the output
                        sb.append(data.substring(startPos, currentPos));
                    else
                        sb.append(value);
                }
            }
        }

        // take The remainder
        sb.append(data.substring(currentPos));

        return sb.toString();
    }

    // Note: TBD  The File name should be passed from Delta and if no file name
    //       then no variables.

      
    // Constants for command line processing ...

    protected static final String wasHomeProp = "WAS_HOME" ;
    // Note: TBD  The File name should be passed from Delta and if no file name
    //       then no variables.

      
    // Constants for command line processing ...

    protected static final String javaHomeProp = "JAVA_HOME" ;
    // Note: TBD  The File name should be passed from Delta and if no file name
    //       then no variables.

      
    // Constants for command line processing ...

    protected static final String db2HomeProp = "DB2_HOME" ;
    // Note: TBD  The File name should be passed from Delta and if no file name
    //       then no variables.

      
    // Constants for command line processing ...

    protected static final String db2DriverProp = "DB2Driver" ;
    // Note: TBD  The File name should be passed from Delta and if no file name
    //       then no variables.

      
    // Constants for command line processing ...

    protected static final String jdbcDriverProp = "DBDRIVER_JARS" ;
    // Note: TBD  The File name should be passed from Delta and if no file name
    //       then no variables.

      
    // Constants for command line processing ...

    protected static final String dbdriverPath = "DBDRIVER_PATH" ;
    // Note: TBD  The File name should be passed from Delta and if no file name
    //       then no variables.

      
    // Constants for command line processing ...

    protected static final String wpHomeProp = "WP_HOME" ;

    protected static final String exportTag = "EXPORT " ;
    protected static final String setTag = "SET " ;
      
    protected Properties getPortalSetUpConfig() {
    	
       String userInstallRoot = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT );
       String wasHome = WPConfig.getProperty( WPConstants.PROP_WAS_HOME );
       String lcHome = WPConfig.getProperty(LCUtil.PROP_LC_HOME);
       String lcProfileName = WPConfig.getProperty(LCUtil.PROP_LC_PROFILE_NAME);
       String wasPassword = WPConfig.getProperty(WPConstants.PROP_WAS_PASS);
       String wasUserId = WPConfig.getProperty(WPConstants.PROP_WAS_USER);
       String activitiesProfileName = WPConfig.getProperty(LCUtil.PROP_ACTIVITIES_PROFILE_NAME);
       String blogsProfileName = WPConfig.getProperty(LCUtil.PROP_BLOGS_PROFILE_NAME);
       String communitiesProfileName = WPConfig.getProperty(LCUtil.PROP_COMMUNITIES_PROFILE_NAME);
       String dogearProfileName = WPConfig.getProperty(LCUtil.PROP_DOGEAR_PROFILE_NAME);
       String profilesProfileName = WPConfig.getProperty(LCUtil.PROP_PROFILES_PROFILE_NAME);
       String homepageProfileName = WPConfig.getProperty(LCUtil.PROP_HOMEPAGE_PROFILE_NAME);
       String wikisProfileName = WPConfig.getProperty(LCUtil.PROP_WIKIS_PROFILE_NAME);
       String filesProfileName = WPConfig.getProperty(LCUtil.PROP_FILES_PROFILE_NAME);
       String mobileProfileName = WPConfig.getProperty(LCUtil.PROP_MOBILE_PROFILE_NAME);
       String searchProfileName = WPConfig.getProperty(LCUtil.PROP_SEARCH_PROFILE_NAME);
       String newsProfileName = WPConfig.getProperty(LCUtil.PROP_NEWS_PROFILE_NAME);
       String forumProfileName = WPConfig.getProperty(LCUtil.PROP_FORUM_PROFILE_NAME);
       String moderationProfileName=WPConfig.getProperty(LCUtil.PROP_MODERATION_PROFILE_NAME);
       String metricsProfileName=WPConfig.getProperty(LCUtil.PROP_METRICS_PROFILE_NAME);
       String ccmProfileName=WPConfig.getProperty(LCUtil.PROP_METRICS_PROFILE_NAME);
       
       //System.out.println("lcHome = " + lcHome);

       //System.out.println(targetDirFullName);
       
       //System.out.println("in extractor.java");
       
       Properties props  = new Properties();

       // All these values are already in wpconfig.properties, so pull the values from there.
       if(null != wasHome)
       {
    	   //System.out.println("Macro was_home : " + wasHome);
    	   props.setProperty("was_home",    wasHome );
       }
       if(null != userInstallRoot)
       {
    	   //System.out.println("Macro user_install_root : " + userInstallRoot);
    	   props.setProperty("user_install_root",    userInstallRoot );
       }
       if(null != lcHome)
       {
    	   //System.out.println("Macro lc_home : " + lcHome);
    	   props.setProperty("lc_home", lcHome);
       }
       if(null != wasPassword)
       {
    	   props.setProperty("was_password", wasPassword);
       }
       if(null != wasUserId)
       {
    	   props.setProperty("was_username", wasUserId);
       }
       if(null != activitiesProfileName)
       {
    	   //System.out.println("Macro activities_profile_name : " + activitiesProfileName);
    	   props.setProperty("activities_profile_name", activitiesProfileName);
       }
       if(null != blogsProfileName)
       {
    	   //System.out.println("Macro blogs_profile_name : " + blogsProfileName);
    	   props.setProperty("blogs_profile_name", blogsProfileName);
       }
       if(null != communitiesProfileName)
       {
    	   //System.out.println("Macro communities_profile_name : " + communitiesProfileName);
    	   props.setProperty("communities_profile_name", communitiesProfileName);
       }
       if(null != dogearProfileName)
       {
    	   //System.out.println("Macro dogear_profile_name : " + dogearProfileName);
    	   props.setProperty("dogear_profile_name", dogearProfileName);
       }
       if(null != profilesProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("profiles_profile_name", profilesProfileName);
       }
       if(null != homepageProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("homepage_profile_name", homepageProfileName);
       }       
       if(null != wikisProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("wikis_profile_name", wikisProfileName);
       }       
       if(null != filesProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("files_profile_name", filesProfileName);
       }       
       if(null != searchProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("search_profile_name", searchProfileName);
       }       
       if(null != mobileProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("mobile_profile_name", mobileProfileName);
       }       
       if(null != newsProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("news_profile_name", newsProfileName);
       }
       if(null != forumProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("forum_profile_name", forumProfileName);
       }
       if(null != moderationProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("moderation_profile_name", moderationProfileName);
       }
       if(null != metricsProfileName)
       {
    	   //System.out.println("Macro profiles_profile_name : " + profilesProfileName);
    	   props.setProperty("metrics_profile_name", metricsProfileName);
       } 
       if(null != ccmProfileName)
       {
    	   props.setProperty("ccm_profile_name", ccmProfileName);
       }
       
       //props.setProperty("was_cell_name", WPConfig.getProperty( WPConstants.PROP_WAS_CELL ) );
       //props.setProperty("was_node_name", WPConfig.getProperty(  WPConstants.PROP_WAS_NODE ) );
       //props.setProperty("was_server_name", WPConfig.getProperty(  WPConstants.PROP_WP_SERVER ) );

       // May need to handle the DB vars as well.

       return props;
    }
     
    protected Properties getSetUpCmd()
    {
        Vector     errMsg = new Vector();
        Properties props  = new Properties();

        String wasHome = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT );
        String lcHome = WPConfig.getProperty(LCUtil.PROP_LC_HOME);
        
        props.setProperty("was_home",    wasHome );
        props.setProperty("was_home_fs", asEntry( wasHome ));
        props.setProperty("target",      wasHome);
        props.setProperty("target_fs",   asEntry(wasHome ));
        props.setProperty("wp_home",     targetDirFullName);
        props.setProperty("wp_home_fs",  asEntry(targetDirFullName));
        props.setProperty("lc_home", lcHome);
        
        String theFileName;


        if (LocalSystem.isUnix)  //PQ59675 use new LocalSystem
            theFileName = wasHome + "/bin/setupCmdLine.sh";
        else if (LocalSystem.isiSeries) {
            theFileName = wasBaseHome + "/bin/setupCmdLine " + wasInstance;
        } else
            theFileName = wasHome + "/bin/setupCmdLine.bat";

        File theFile = new File(theFileName);

        if ( !theFile.exists() ) {
            logOnly(prolix3, "No setUpCmdLine processing was done.");
            return props;
        }

        logOnly(prolix3, "Processing " + theFileName);

        Vector lines = helper.file2Vector(errMsg, theFileName);
        spewMsg(errMsg, false, true);

        if ( lines == null )
            return props;

        for ( int i = 0; i < lines.size(); i++ ) {
            String line = ((String) lines.elementAt(i)).trim();

            int delim = line.indexOf("=");

            if ( delim >= 0 ) {
                String key = line.substring(0, delim);
                logOnly(prolix5, "Extracted key (" + key + ").");

                String searchKey = key.toUpperCase();
                if ( searchKey.startsWith(exportTag) ) {
                    key = key.substring(exportTag.length()).trim();
                    logOnly(prolix5, "Trimmed key to (" + key + ").");

                } else if ( searchKey.startsWith(setTag) ) {
                    key = key.substring(setTag.length()).trim();
                    logOnly(prolix5, "Trimmed key to (" + key + ").");
                }

                if ( key.equals(db2HomeProp) ) {
                    key = db2DriverProp;
                    logOnly(prolix5, "Changed key to (" + key + ").");
                }

                String value = line.substring(delim + 1).trim();
                logOnly(prolix5, "Extracted value (" + value + ").");

                String resolvedValue = Helper1.resolveMacro(value, props);

                if ( !value.equals(resolvedValue) )
                    logOnly(prolix5, "Resolved value to (" + resolvedValue + ").");

                logOnly(prolix3, "  Key   (" + key + ")");
                logOnly(prolix3, "  Value (" + value + ")");

                if ( props.getProperty(key.toLowerCase()) == null ) {
                    props.setProperty(key.toLowerCase(), value);
                    props.setProperty(key.toLowerCase() + "_fs", asEntry(value));
                } else {
                    logOnly(prolix3, "Second read -- ignored.");
                }
            }
        }

        showProperty(props, wasHomeProp);
        showProperty(props, javaHomeProp);
        showProperty(props, db2DriverProp);
        showProperty(props, jdbcDriverProp);
        showProperty(props, wpHomeProp);
        showProperty(props, "lc_home");
        
        return props;
    }
      
    protected void showProperty(Properties props, String propName)
    {
        propName = propName.toLowerCase();
        
//      do not show the password
    	if(propName.equalsIgnoreCase("was_password"))
    	{
    		logOnly(prolix3, propName + "=(" + PasswordRemover.PWD_REMOVED + ")");
    	}
    	else
    		logOnly(prolix3, propName + "=(" + props.getProperty(propName) + ")");
    }

    protected void spewMsg(Vector vMsg, boolean LogOnly, boolean clearIt )
    {
        for ( int i = 0; i < vMsg.size(); i++ ) {
            if ( LogOnly )
                logOnly(prolix3, (String) vMsg.elementAt(i));
            else
                log((String) vMsg.elementAt(i));
        }

        if ( clearIt )
            vMsg.clear();
    }

    protected long errorCount = 0;
    protected long warnCount = 0;

    protected boolean handleErrMsg(StringBuffer errMsg, boolean LogOnly)
    {
        boolean returnValue = false;

        if ( errMsg.length() == 0 )
            return returnValue;  // indicate no error message

        if ( LogOnly )
            logOnly(prolix3, errMsg.toString());
        else
            log( errMsg.toString());

        if ( errMsg.toString().startsWith("Error") ) {
            errorCount++;
            returnValue = true;

        } else if ( errMsg.toString().startsWith("Warning") ) {
            warnCount++;
        }

        errMsg.delete(0, errMsg.length());

        return returnValue;
    }

    protected String systemClassPath = null;
    protected boolean adjust4Platform = true; // This is for ExecCmd

    /**
	 * @return  the systemClassPath
	 * @uml.property  name="systemClassPath"
	 */
    protected void getSystemClassPath()
    {
        log("Obtaining the system classpath.");  //KH Added log statement.

        ExecCmd exec       = new ExecCmd(adjust4Platform);
        Vector  results    = new Vector();
        Vector  logResults = new Vector();

        int resultCode;

        if ( helper.isCaseSensitive() ) {
            logDebug("The platform is Unix. Checking for system classpath.");

            resultCode = exec.Execute("echo $CLASSPATH", false, false, results, logResults);

        } else {
            logDebug("Debug --  The platform is Windows. Checking for system classpath.");

            resultCode = exec.Execute("echo %classpath%", false, false, results, logResults);
        }

        log(null, logResults, prolix3);
        log("Result: ", results, prolix3);

        if ( results.size() > 0 ) {
            systemClassPath = (String) results.elementAt(0);

            log("System classpath is: " + systemClassPath);

        } else {
            log("System classpath could not be found.");
        }
    }

    protected boolean dupApplication()
    {
        if ( fRestore ) {
            logOnly(prolix3, "Skipping duplicate application checking: Performing restore.");
            return true;
        }

        if ( !po.getBool(k_DupCheck) ) {
            log("Bypassing duplicate application checking by request.");
            return true;
        }

        if ( productFileName == null ) {
            log("Skipping duplicate application checking: No product file was specified.");
            return true;
        }

        if ( productFileType == null ) {
            log("Skipping duplicate application checking: No product file type was specified.");
            return true;
        }

        if ( !productFileType.equals(HelperList.meXMLProductFile) ) {
            log("Skipping duplicate application checking: The product file is not an XML file.");
            return true;
        }

        // If XML processing is indicated, we will never get this far if the
        // XML handler could not be prepared.

        String eventType = po.getString(k_EventType);

        if ( eventType == null )
            eventType = mainAttribs.getValue(HelperList.meEventType);

        eventType = eventType.trim();

        if ( (eventType == null) || (eventType.length() == 0) ) {
            log("Skipping duplicate application checking: No event type was specified.");
            return true;
        }

        String apar = po.getString(k_APAR);

        if ( apar == null )
            apar = mainAttribs.getValue(HelperList.meAPAR);

        if ( (apar == null) || (apar.length() == 0) ) {
            logOnly(prolix3, "This update is not an eFix.");
            apar = null;

        } else {
            logOnly(prolix3, "This update is an eFix: APAR ( " + apar + " ).");
        }

        String description = po.getString(k_Description);

        if ( description == null )
            description = mainAttribs.getValue(HelperList.meDescription);

        if ( description == null )
            description = "None";   // this should never happen
        else
            description = description.trim();

        HelperList.XMLHistoryEventInfo duplicatedEvent = null;

        String[] eventHierarchy = getEventHierarchy();
        Vector events = xmlh.getEvents(fDebug, productFileName, eventHierarchy);

        if ( events == null ) {
            log("No prior history events noted.");
            return true;
        }

        // If it is an eFix we will match on the APAR value.
        // If it is not an eFix we will match on the description.

        int numEvents = events.size();

        for ( int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            HelperList.XMLHistoryEventInfo nextEventInfo =
                (HelperList.XMLHistoryEventInfo) events.elementAt(eventNo);

            if ( apar == null ) {  // this must be a PTF
                if ( description.equals(nextEventInfo.description) ) {
                    if ( nextEventInfo.type.equals(HelperList.backout) ) {
                        duplicatedEvent = null;
                    } else if (eventType.equals(nextEventInfo.type)) {
                        duplicatedEvent = nextEventInfo;
                    }
                }

            } else {  // we are processing an eFix
                if ( apar.equals(nextEventInfo.APAR) ) {
                    // we are looking at an node with the same APAR

                    if ( nextEventInfo.type.equals(HelperList.backout) ) {
                        duplicatedEvent = null;

                    } else if ( eventType.equals(nextEventInfo.type) ) {
                        duplicatedEvent = nextEventInfo;
                    }
                }
            }
        }

        if ( duplicatedEvent == null )
            return true;

        if ( apar == null ) {
            logError(119, "The " + eventType + " update, " + duplicatedEvent.description +
                          ", was last applied on " + duplicatedEvent.sqlTime + ".");

        } else {
            logError(120, "The " + eventType + " update, APAR " + apar +
                          ", was last applied on " + duplicatedEvent.sqlTime + ".");
        }

        if ( po.getBool(k_Force) ) {
            log("Proceeding with update: The force option was specified.");
            return true;

        } else {
            log("Aborting update: A duplicate event was seen.");
            log("The update may be forced by specifying the \"-Force\" option.");

            if ( events.elementAt(numEvents - 1) == duplicatedEvent ) {
                log("Alternatively, the update may be performed after first uninstalling");
                log("the current update (using backup jar [ " + duplicatedEvent.backupJarName + " ])");
                log("and then performing this update again.");

            } else {
                log("This update may be performed after uninstalling all updates up to and");
                log("including the duplicate of the current update.");
                // log("Use the \"-ShowHistory\" option to display the product event history.");
            }

            return false;
        }
    }

    protected void displayHistory(Vector historyEvents) {

        int numEvents = historyEvents.size();

        for ( int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            HelperList.XMLHistoryEventInfo nextEventInfo =
                (HelperList.XMLHistoryEventInfo) historyEvents.elementAt(eventNo);

            if (nextEventInfo.type == HelperList.eFix) {
                log("" + nextEventInfo.sqlTime +
                    " : " + nextEventInfo.type +
                    " : " + nextEventInfo.APAR);

            } else {
                log("" + nextEventInfo.sqlTime +
                    " : " + nextEventInfo.type +
                    " : " + nextEventInfo.description);
            }
        }
    }

    // Section on creating the backup.
    //
    // Scan through the delta jar.  Any file or jar entries are converted
    // into update or delete entries in the backup jar.  Any child entries
    // are ignored.
    //
    // An entry that matches an existing file is turned into an update entry,
    // and a copy of the existing file is recorded in the backup jar.
    //
    // An entry that does not match an existing file is either ignored (if
    // a delete entry) or is turned into a delete entry (if writing down a
    // new file).
    //
    // Note that directories are not currently handled.

    protected boolean fSkipBackup;

    protected boolean createBackup()
    {
        if ( fRestore || fSkipBackup )
            return true;

        if ( !scanDeltaJar() ) {
            logError(65, "Unable to open delta jar: " + decodedJarName);
            return false;
        }

        if ( !recordScanToBackup() ) {
            logError(66, "Failed to create backup: " + backupJarName);
            return false;
        }

        // logDebug("Created backup for jar:" + deltaJarName + " in: " + backupJarName);

        return true;
    }

    // Scan the delta jar, answering a vector of files to be recorded, and
    // a vector of files to be deleted.  Answer null if the scan fails.

    protected static final boolean SCAN_FAILED = false ;
    // Scan the delta jar, answering a vector of files to be recorded, and
    // a vector of files to be deleted.  Answer null if the scan fails.

    protected static final boolean SCAN_IS_OK = true ;

    protected boolean scanDeltaJar()
    {
        deltaJarInput = openJarInput(decodedJarName);

        if ( deltaJarInput == null ) {
            logError(62, "Unable to open delta jar: " + decodedJarName);
            return false;
        }

        boolean scanIsOK = progressScanDelta();

        if ( !closeJarStream(deltaJarInput, decodedJarName) ) {
            logError(60, "Failed to close delta jar: " + decodedJarName);
            scanIsOK = false;
        }

        deltaJarInput = null;

        return scanIsOK;
    }

    // The following class is for recording the manifest attributes of the
    // jar entries of the input jar so that we may transfer them to the
    // backup jar.

    protected static class BKJattribs
    {
        String name;         // jar entry name
        Attributes attribs;  // jar entry attributes
    }

    protected JarEntry scanEntry        = null;
    protected String scanEntryName      = null;
    protected Attributes scanAttributes = null;

    protected boolean progressScanDelta()
    {
        log("Determining files to back up");

        try {
            // 'getNextJarEntry' throws an IOException
            while ( (scanEntry = deltaJarInput.getNextJarEntry()) != null ) {
                scanData.scanCount++;

                if ( !basicScanDelta() )
                    return SCAN_FAILED;

                percentComplete("scanning", scanData.scanCount, inJarEntryCount, 3000);
            }

            percentComplete("scanning", scanData.scanCount + 1, inJarEntryCount, 0);
            log("");

        } catch ( IOException ex ) {
            logError(75, "Failed to seek to next jar entry: " + decodedJarName, ex);
            return SCAN_FAILED;
        }

        scanFindReplace();  // Need to do this here. once we know which files are being installed

        log("Scanned (" + Long.toString(scanData.scanCount) + ") entries.");
        log("Completed scanning for backup information: " + decodedJarName +
            " into: " + backupJarName);

        // Record those files that will be updated by find and replace,
        // but don't record any file that has already been recorded
        // for update or for deletion.  (Which shouldn't occur.)

        for ( int updateNo = 0; updateNo < ufFileVector.size(); updateNo++ ) {
            FUEntry fue = (FUEntry) ufFileVector.elementAt(updateNo);
            String nextName = fue.fileName;

            logOnly(prolix3, "  Scanning find/replace: [ " + nextName + " ]");

            if ( fue.components.length > 0 ) {
                if ( !ensureComponentsPresent(fue.components) ) {
                    logOnly(prolix3, "  Find/replace component is not present; skipping.");
                    continue;
                }
            }

            if ( !scanData.updateFiles.contains(nextName) ) {
                if ( !scanData.deleteFiles.contains(nextName) ) {
                    logOnly(prolix3, "  Recording find/replace as an update.");
                    scanData.updateFiles.add(nextName);
                    scanData.withSpecialUpdate.put(nextName, nextName);
                } else {
                    logOnly(prolix2, "Note: Find/Replace file is new: [ " + nextName + " ]");
                }
            } else {
                logOnly(prolix3, "Note: Find/Replace intersects with simple update.");
            }
        }

        // Record those property files that will be updated,
        // but don't record any file that has already been recorded
        // for update or for deletion.  (Which shouldn't occur.)

        for ( int updateNo = 0; updateNo < pfFileUpdate.size(); updateNo++ ) {
            PFUpdates pfu = (PFUpdates) pfFileUpdate.elementAt(updateNo);
            String nextName = pfu.propertyFileName;

            logOnly(prolix3, "  Scanning property file update: [ " + nextName + " ]");

            if ( !scanData.updateFiles.contains(nextName) ) {
                if ( !scanData.deleteFiles.contains(nextName) ) {
                    logOnly(prolix3, "  Recording property file update as an update.");
                    scanData.updateFiles.add(nextName);
                    scanData.withSpecialUpdate.put(nextName, nextName);
                } else {
                    logOnly(prolix2, "Note: Property file for update is new: " + nextName);
                }

            } else {
                logOnly(prolix3, "Note: Property file update intersects with simple update.");
            }
        }

        // Now ensure any jar files we re-sequence are backed up first.
        //
        // Cannot re-sequence with EAR processing;
        //
        // Not sure if absolute file processing works with re-sequencing.

        Enumeration eNum = reSequenceJar.keys();

        while ( eNum.hasMoreElements() ) {
            String nextName = (String) eNum.nextElement();

            logOnly(prolix3, "Scanning jar resequence request: [ " + nextName + " ]");

            if ( !scanData.updateFiles.contains(nextName) ) {
                if ( !scanData.deleteFiles.contains(nextName) ) {
                    logOnly(prolix3, "Recording jar resequence request as an update.");
                    scanData.updateFiles.add(nextName);
                    scanData.withSpecialUpdate.put(nextName, nextName);
                } else {
                    logOnly(prolix2, "Note: Jar file for resequencing is new: " + nextName);
                }

            } else {
                logOnly(prolix3, "Note: Jar file requencing intersects with simple update.");
            }
        }

        String req = null;

        logOnly(prolix3, "  Scanning for forceBackup files." );
        final String MACRO_ASINSTALLED = "$<" + ASINSTALLED_MACRO_PREFIX;
        for ( int forceBackupNo = 0;
              (req = mainAttribs.getValue(HelperList.meForceBackup + forceBackupNo)) != null;
              forceBackupNo++ ) {
            String resolvedName = ResolveMacro(req);
            if ( !scanData.updateFiles.contains( resolvedName ) ) {
               logOnly(prolix3, "  Note: forced backup file - " + req + "(" + resolvedName + ")");
               // If its an asInstalled macro'd file, its absolute, but we backup as an asInstalled file.
               if ( req.startsWith( MACRO_ASINSTALLED ) ) {

                  int idx = req.indexOf( ">" );
                  String macroEarName = req.substring( MACRO_ASINSTALLED.length(), idx );

                  String backupEntryName = "/" + WPConfig.getProperty( WPConstants.PROP_WAS_NODE ) + 
                                           "/" + macroEarName + 
                                           "/" + req.substring( MACRO_ASINSTALLED.length() + macroEarName.length() + 1 );

                  // Put in asInstalled, so its marked correctly for restore.
                  scanData.asInstalled.put( backupEntryName, resolvedName );
                  //scanData.asInstalled.put( backupEntryName, backupEntryName );

               } else if ( req.startsWith( "$<" ) ) {
                  // Indicate its an absoluet path.
                  logOnly(prolix3, "  Note: Will treat " + req + " as an absolute path entry.");
                  scanData.absolutePaths.add( resolvedName );
                  scanData.absolutePathsSet.put( resolvedName, req );
               }
               scanData.withSpecialUpdate.put( resolvedName, req);
               scanData.updateFiles.add( resolvedName );
            }
        }

        return SCAN_IS_OK;
    }

    protected Vector helperClasses = new Vector();

    protected boolean basicScanDelta()
    {
        scanEntryName = scanEntry.toString();

        logOnly(prolix4, "New Entry: " + scanEntryName);

        ChildJarEntry splitEntry = splitEntry(scanEntryName);
        if ( splitEntry != null ) {
            logOnly(prolix4, "Entry for a jar entry; skipping: " + scanEntryName);
            return SCAN_IS_OK;
        }

        try {
            scanAttributes = scanEntry.getAttributes();
        } catch ( IOException ex ) {
            logError(14, "Failed to retrieve jar entry attributes: " + decodedJarName, ex);
            return SCAN_FAILED;
        }

        if ( testKeyForHelperClass(scanAttributes) )
            helperClasses.add(scanEntryName);

        if ( testKeyForNoRestore(scanAttributes) ) {
            logOnly(prolix4, "   Entry marked for no-restore; skipping: " + scanEntryName);
            return SCAN_IS_OK;
        }

        if ( testKeyForRestoreOnly(scanAttributes)) {
            String requiredVersion = getRequiredVersion(scanAttributes);

            boolean doSkip;

            if ( requiredVersion != null ) {
                if ( startingVersion == null ) {
                    doSkip = true;
                    StringBuffer wrnMsg =
                        new StringBuffer("Warning: Starting version is not available," +
                                         " RestoreOnly file not processed: " + scanEntryName);
                    handleErrMsg(wrnMsg, false);

                } else {
                    if ( startingVersion.equals(requiredVersion) ) {
                        doSkip = false;
                        logOnly(prolix4, "   Entry marked for restore-only," +
                                         " required version is OK; recording: " + scanEntryName);
                    } else {
                        doSkip = true;
                        logOnly(prolix4, "   Entry marked for restore-only," +
                                         " required version is not OK; skipping: " + scanEntryName);
                    }
                }

            } else {
                doSkip = false;
                logOnly(prolix4, "   Entry marked for restore-only; recording: " + scanEntryName);
            }

            if ( !doSkip )
                scanData.restoreOnlyFiles.add(scanEntryName);

            return SCAN_IS_OK;
        }

        if ( !isAllowedByComponent(scanEntryName, scanAttributes) ) {
            logOnly(prolix4, "   Entry not in current component; skipping: " + scanEntryName);
            return SCAN_IS_OK;
        }

        handlingPerInstance   = false;
        handlingAsInstallable = false;
        handlingAsApplication = false;
        handlingAsInstalled   = false;
        handlingAsMetadata    = false;

        if ( testKeyForPerInstance(scanAttributes) ) {
            handlingPerInstance = true;
            logOnly(prolix4, "   Entry marked for per-instance processing: " + scanEntryName);

            return scanBaseInstances();
        }

        // Scan the entry relative to the EAR temp directory,
        // for configuration instances relative to the
        // node name of each instance.

        handlingAsInstallable = testKeyForAsInstallable(scanAttributes);
        handlingAsApplication = !handlingAsInstallable && testKeyForAsApplication(scanAttributes);

        if ( handlingAsInstallable || handlingAsApplication ) {
            logOnly(prolix4, "   Entry marked for EAR processing: " + scanEntryName);

            nameRule = testKeyForNameRule(scanAttributes);
            if ( nameRule == null ) {
                return scanInstances();
            } else {
                logOnly(prolix4, "  Entry marked with name rule: " + nameRule);
                return scanInstancesRuled();
            }
        }

        if ( testKeyForAsInstalled(scanAttributes) ) {
            handlingAsInstalled = true;
            logOnly(prolix4, "   Entry marked AsInstalled: " + scanEntryName);

            handlingAsMetadata = testKeyForAsMetadata(scanAttributes);
            if ( handlingAsMetadata )
                logOnly(prolix4, "   Entry marked AsMetadata: " + scanEntryName);

            nameRule = testKeyForNameRule(scanAttributes);
            if ( nameRule == null ) {
                return scanInstalled();
            } else {
                logOnly(prolix4, "  Entry marked with name rule: " + nameRule);
                return scanInstalledRuled();
            }
        }

        return scanAllowed();
    }

    protected InstanceData scanInstance = null;
    protected String scanNodeName       = null;
    protected String scanLocation       = null;

    protected boolean scanBaseInstances()
    {
        Iterator instances = installation.getInstances();
        while ( instances.hasNext() ) {
            scanInstance = (InstanceData) instances.next();
            scanNodeName = scanInstance.getNodeName();
            scanLocation = scanInstance.getLocation();

            logOnly(prolix4, "   ==> Processing with instance: " + scanNodeName);
            logOnly(prolix4, "       Instance location: " + scanLocation);

            if ( !scanAllowed() )
                return SCAN_FAILED;
        }

        return SCAN_IS_OK;
    }

    // After parsing the ear parts, when the ear name is null, there
    // was a parse failure, however, an error is logged during the EAR
    // parsing; no new error logging is necessary.

    protected String[] scanEarParts   = null;
    protected String scanEarName      = null;
    protected String ruledScanEarName = null;

    protected boolean scanInstances()
    {
        scanEarParts = getEarParts(scanEntryName);
        scanEarName = earParts[EAR_NAME_OFFSET];
        if ( scanEarName == null ) // Bad entry name
            return SCAN_FAILED;

        Iterator instances = installation.getInstances();
        while ( instances.hasNext() ) {
            scanInstance = (InstanceData) instances.next();
            scanNodeName = scanInstance.getNodeName();
            logOnly(prolix4, "   ==> Processing with instance: " + scanNodeName);

            if ( !scanAllowed() )
                return SCAN_FAILED;
        }

        return SCAN_IS_OK;
    }

    protected boolean scanInstancesRuled()
    {
        scanEarParts = getEarParts(nameRule);
        ruledScanEarName = earParts[EAR_NAME_OFFSET];
        if ( ruledScanEarName == null ) // Bad entry name
            return SCAN_FAILED;

        Iterator instances = installation.getInstances();
        while ( instances.hasNext() ) {
            scanInstance = (InstanceData) instances.next();
            scanNodeName = scanInstance.getNodeName();
            logOnly(prolix4, "   ==> Processing name rule with instance: " + scanNodeName);

            HashMap priorResolutions = new HashMap();

            Vector nextServerJoin = scanInstance.getServerJoin();
            int joinCount = nextServerJoin.size();

            for ( int joinNo = 0; joinNo < joinCount; joinNo++ ) {
                String[] nextJoin = (String[]) nextServerJoin.elementAt(joinNo);

                scanEarName = HelperList.ResolveMacro(ruledScanEarName, nextJoin);
                scanEarParts[EAR_NAME_OFFSET] = scanEarName;
                logOnly(prolix4, "   ==> Name resolution: " + scanEarName);

                if ( priorResolutions.get(scanEarName) == null ) {
                    logOnly(prolix4, "   ==> New resolution");

                    priorResolutions.put(scanEarName, ruledScanEarName);

                    if ( !scanAllowed() )
                        return SCAN_FAILED;

                } else {
                    logOnly(prolix4, "   ==> Duplicate resolution");
                }
            }
        }

        return SCAN_IS_OK;
    }

    protected boolean scanInstalled()
    {
        scanEarParts = getEarParts(scanEntryName);
        scanEarName = scanEarParts[EAR_NAME_OFFSET];
        if ( scanEarName == null )
            return SCAN_FAILED;

        Iterator instances = installation.getInstances();
        while ( instances.hasNext() ) {
            scanInstance = (InstanceData) instances.next();
            scanNodeName = scanInstance.getNodeName();
            logOnly(prolix5, "   ==> Processing installed with instance: " + scanNodeName);

            if ( !basicScanInstalled() )
                return SCAN_FAILED;
        }

        return SCAN_IS_OK;
    }
    
    protected boolean scanInstalledRuled()
    {
        scanEarParts = getEarParts(nameRule);

        String ruledEarName = scanEarParts[EAR_NAME_OFFSET];
        if ( ruledEarName == null )
            return SCAN_FAILED;

        Iterator instances = installation.getInstances();
        while ( instances.hasNext() ) {
            scanInstance = (InstanceData) instances.next();
            scanNodeName = scanInstance.getNodeName();
            logOnly(prolix5, "   ==> Processing ruled installed with instance: " + scanNodeName);

            HashMap priorResolutions = new HashMap();

            Vector nextServerJoin = scanInstance.getServerJoin();
            int joinCount = nextServerJoin.size();

            for ( int joinNo = 0; joinNo < joinCount; joinNo++ ) {
                String[] nextJoin = (String[]) nextServerJoin.elementAt(joinNo);

                scanEarName = HelperList.ResolveMacro(ruledEarName, nextJoin);
                scanEarParts[EAR_NAME_OFFSET] = scanEarName;
                logOnly(prolix4, "   ==> Name resolution: " + scanEarName);

                if ( priorResolutions.get(scanEarName) == null ) {
                    logOnly(prolix4, "   ==> New resolution");

                    priorResolutions.put(scanEarName, ruledEarName);

                    if ( !basicScanInstalled() )
                        return SCAN_FAILED;
                } else {
                    logOnly(prolix4, "   ==> Duplicate resolution");
                }
            }
        }

        return SCAN_IS_OK;
    }

    protected String scanBinariesURL = null;

    protected boolean basicScanInstalled()
    {
        DeploymentData nextDeployment = scanInstance.getDeploymentDatum(scanEarName);
        if ( nextDeployment == null ) {
            logOnly(prolix5, "No deployment found; skipping this instance.");
            return SCAN_IS_OK;
        }

        scanBinariesURL = nextDeployment.getBinariesURL();
        logOnly(prolix5, "Located deployment: " + scanBinariesURL);

        if ( !scanAllowed() )
            return SCAN_FAILED;

        if ( !handlingAsMetadata )
            return SCAN_IS_OK;

        scanBinariesURL = nextDeployment.getAltMetadataPath();
        logOnly(prolix5, "Located metadata deployment: " + scanBinariesURL);
 
        return scanAllowed();
    }

    // After the initial processing, 'scanEntryName' holds the
    // entry name that will be used in the backup jar,
    // while 'scanEntryPath' holds the full path to the
    // entry as a file.  'isAbsolutePath' is used to specify
    // if the backup entry is to be marked as an absolute entry.

    protected boolean scanAllowed()
    {
        boolean isAbsolutePath;
        String scanEntryPath;

        if ( handlingPerInstance ) {
            logOnly(prolix4, "Scanning per instance with location: " + scanLocation);

            // Need to update the entry name to be the full path for
            // the entry.  'isAbsolute' is necessary, as the configuration
            // paths are not relative to the installation path.

            isAbsolutePath = true;
            scanEntryPath = scanLocation + asPath(scanEntryName);
            scanEntryName = asEntry(scanEntryPath);

        } else if ( handlingAsApplication ) {
            logOnly(prolix4, "Scanning application.");

            // Need to update the entry name to include the node name.
            // The node name is the primary node of a configuration instance,
            // not a node associated with a binary files location.

            // The path is relative to the ear TMP directory.  Later,
            // the entry will be marked as for ear TMP processing,
            // which requires a relative path.  (During backup processing,
            // the entry path will be again taken relative to the
            // ear TMP directory.

            // The update entry has '/installable' or '/application',
            // but does not have the node name.  This must be prepended
            // to find the EAR as it was expanded.

            String earPath =
                earTmpFullName + slash + scanNodeName + 
                asPath(earParts[EAR_PREFIX_OFFSET]) + scanEarName;

            // Don't proceed if the ear directory is absent;
            // there are processing cases which normally would be applied
            // when the directory is absent, but in the case of EAR processing,
            // do not do this processing when the ear as a whole is absent.

            if ( !earDirExists(earPath) ) {
                logOnly(prolix4, "Skipping: EAR not found: " + earPath);
                return SCAN_IS_OK;
            }

            isAbsolutePath = false;
            scanEntryName = entrySlash + scanNodeName +
                            scanEarParts[EAR_PREFIX_OFFSET] + scanEarName +
                            scanEarParts[EAR_TAIL_OFFSET];
            scanEntryPath = earPath + asPath(scanEarParts[EAR_TAIL_OFFSET]);

        } else if ( handlingAsInstallable ) {
            logOnly(prolix4, "Scanning installable.");

            // Need to update the entry name to include the node name.
            // The node name is the primary node of a configuration instance,
            // not a node associated with a binary files location.

            // The path is relative to the ear TMP directory.  Later,
            // the entry will be marked as for ear TMP processing,
            // which requires a relative path.  (During backup processing,
            // the entry path will be again taken relative to the
            // ear TMP directory.

            // The update entry has neither '/installableApps/' nor the node
            // name.  These must be added to find the EAR as it was expanded.

            String earPath =
                earTmpFullName + slash + 
                scanNodeName + slash +
                "installableApps" + slash +
                asPath(earParts[EAR_NAME_OFFSET]) + slash +
                asPath(earParts[EAR_TAIL_OFFSET]);

            // Don't proceed if the ear directory is absent;
            // there are processing cases which normally would be applied
            // when the directory is absent, but in the case of EAR processing,
            // do not do this processing when the ear as a whole is absent.

            if ( !earDirExists(earPath) ) {
                logOnly(prolix4, "Skipping: EAR not found: " + earPath);
                return SCAN_IS_OK;
            }

            isAbsolutePath = false;
            scanEntryName = entrySlash + scanNodeName +
                            scanEarParts[EAR_PREFIX_OFFSET] + scanEarName +
                            scanEarParts[EAR_TAIL_OFFSET];
            scanEntryPath = earPath + asPath(scanEarParts[EAR_TAIL_OFFSET]);

        } else if ( handlingAsInstalled ) {
            // Need to update the entry name to be the full path for
            // the entry.  'isAbsolute' is necessary, as the deployment
            // location is not relative to the installation path.

            logOnly(prolix4, "Scanning installed.");


            String earPath = scanBinariesURL;
            // With EarRedesign - Always work form a temp/work dir.
            earPath = WPEarActor.getEarWorkArea( earTmpFullName, scanNodeName ).getAbsolutePath() +  slash + scanEarName;

            if ( !earDirExists( earPath ) ) {
                log("Warning: Failed to find deployed EAR: " + earPath );
                return SCAN_IS_OK;
            }

            // We don't want asInstalled to be an absolute path.  needs to be relative, since its now in the work area
            isAbsolutePath = false;
            scanEntryName = entrySlash + scanNodeName +
                            scanEarParts[EAR_PREFIX_OFFSET] + scanEarName +
                            scanEarParts[EAR_TAIL_OFFSET];
            scanEntryPath = earPath + asPath(scanEarParts[EAR_TAIL_OFFSET]);

        } else {
            // Use the entry name as given.

            //<d63342> appeared to expect xtr_absolutePath in iFix/PTF manifiest, but flag is never written.
            //         Perhaps there's also an error in the logic of creating the eFix/PTF manifest, if so
            //         this is still safe, the first test would succeed anyway.
            isAbsolutePath = testKeyForAbsolutePath(scanAttributes) || ( -1 != scanEntryName.indexOf("$<") );

            if ( isAbsolutePath )
                scanEntryPath = asPath( ResolveMacro(scanEntryName) );
            else
                scanEntryPath = targetDirName + asPath(scanEntryName);
        }

        logOnly(prolix4, "   Entry Name: " + scanEntryName);
        logOnly(prolix4, "   Entry Path: " + scanEntryPath);

        if ( isAbsolutePath )
            logOnly(prolix4, "   Entry has an absolute path.");

        File scanEntryFile = new File(scanEntryPath);

        boolean prevent = false;

        if ( scanEntryFile.exists() ) {
            if ( testKeyForAddFile(scanAttributes) ) {
                logOnly(prolix3, "   Skipping add-only entry which would update" +
                                 " an existing file: " + scanEntryPath);

                scanData.preventAdds.put(scanEntryName, scanEntryPath);
                prevent = true;

            } else {
                scanData.updateFiles.addElement(scanEntryName);

                if ( testKeyForDeleteBeforeWrite(scanAttributes, scanEntryName) ) {
                    logOnly(prolix4, "   Entry will delete an existing file: " + scanEntryName);
                    scanData.withDeleteAction.put(scanEntryName, scanEntryPath);

                } else {
                    logOnly(prolix4, "   Entry will update an existing file: " + scanEntryName);

                    if ( testKeyForChmod(scanAttributes) ) {
                        scanData.withChmodAction.put(scanEntryName, scanEntryName);

                        if ( !testKeyForChmodOnly(scanAttributes) ) {
                            logOnly(prolix4, "   Entry has update and chmod actions");
                            scanData.withUpdateAction.put(scanEntryName, scanEntryName);
                        }  else {
                            logOnly(prolix4, "   Entry has chmod-only action");
                        }

                    } else {
                        logOnly(prolix4, "   Entry has update-only action");
                        scanData.withUpdateAction.put(scanEntryName, scanEntryName);
                    }

                    if ( testKeyForDeleteBeforeWrite(scanAttributes, scanEntryName) ) {
                        logOnly(prolix4, "   Preserving delete-before-write setting: " + scanEntryName);
                        scanData.deleteBeforeWrites.addElement(scanEntryName);
                    }
                }

                if ( isAbsolutePath ) {
                    logOnly(prolix4, "   Preserving absolute-path setting: " + scanEntryName);
                    scanData.absolutePaths.addElement(scanEntryName);
                    scanData.absolutePathsSet.put(scanEntryName, scanEntryName);
                }
            }

        } else { // File does not currently exist

            if ( testKeyForDelete(scanAttributes) ) {
                logOnly(prolix4, "   Skipping entry to delete non-existent file: " + scanEntryName);
                prevent = true;

            } else if ( testKeyForNoDelete(scanAttributes) ) {
                logOnly(prolix4, "   Skipping no-delete entry for non-existent file: " + scanEntryName);
                prevent = true;

            } else if ( testKeyForReplaceFile(scanAttributes) ) {
                logOnly(prolix3, "   Skipping update-only entry" +
                                 " which would add a new file: " + scanEntryPath);
                scanData.preventReplaces.put(scanEntryName, scanEntryPath);
                prevent = true;

            } else {
                logOnly(prolix4, "   Entry to place new file; adding delete entry: " + scanEntryName);
                scanData.deleteFiles.addElement(scanEntryName);

                if ( isAbsolutePath ) {
                    logOnly(prolix4, "   Preserving absolute-path setting: " + scanEntryName);
                    scanData.absolutePaths.addElement(scanEntryName);
                    scanData.absolutePathsSet.put(scanEntryName, scanEntryName);
                }
            }
        }

        // Need to remember that these are installable or application
        // updates.  During recovery, they are again processed relative
        // to the EAR temporary directory.

        if ( !prevent ) {
            if ( handlingAsInstallable ) {
                logOnly(prolix4, "   Preserving AsInstallable: " + scanEntryName);
                scanData.asInstallable.put(scanEntryName, scanEntryPath);
            } else if ( handlingAsInstalled ) {
                logOnly(prolix4, "   Preserving AsInstalled: " + scanEntryName);
                scanData.asInstalled.put(scanEntryName, scanEntryPath);
            } else if ( handlingAsApplication ) {
                logOnly(prolix4, "   Preserving AsApplication: " + scanEntryName);
                scanData.asApplication.put(scanEntryName, scanEntryPath);
            }
        }

        return SCAN_IS_OK;
    }

    protected long time2Display = 0;

    protected void percentComplete(String text, long runningValue, int totValue, int delay)
    {
        if ( !doLogging )
            return;

        if ( delay == 0 )       // to force a display right now
            time2Display = 0;

        if ( System.currentTimeMillis() > time2Display ) {
            long pct;

            if ( totValue == 0 )
                pct = 0;
            else
                pct = (runningValue * 100) / totValue;

            logStream.Both(text + " " +
                           pad(runningValue, Integer.toString(totValue).length()) +
                           " of " + Integer.toString(totValue) +
                           "  " + pad(pct, 3) + "% complete");

            time2Display = System.currentTimeMillis() + delay;
        }
    }

    // Copy scanned delta information into the backup.  First, build a
    // manifest with delete entries.  The delete entries must be placed
    // into the manifest early since the manifest is required when creating
    // the jar file.  Then place delete and update entries into the backup.

    // Need to record extra settings for EAR temporary files:
    // These are set in 'asInstallable', 'asInstalled' and 'asApplication'.
    // In these cases, use the replacement entry name as specified
    // in the hashtable, and mark 'asInstallable' or 'asApplication'.

    protected JarOutputStream backupJarOutput = null;

    protected boolean recordScanToBackup()
    {
        Manifest backupManifest   = createManifest();
        Attributes mainAttributes = backupManifest.getMainAttributes();
        Map backupMap             = backupManifest.getEntries();

        // Record required main manifest information.

        mainAttributes.putValue(Attributes.Name.MAIN_CLASS.toString(), "Extractor");

        mainAttributes.putValue("JDK-Level",
                                System.getProperty("java.vm.version") + " " +
                                System.getProperty("java.vm.vendor"));

        mainAttributes.putValue("TimeStamp", logStream.CurrentTimeStamp());

        // Record the root directory against which the extraction is performed.

        mainAttributes.putValue(HelperList.meBackupFrom, targetDirFullName);

        // Defect 120991: Record the installed components for the backup
        //                to know in case it needs them.

        mainAttributes.putValue(HelperList.meComponentsInstalled, backupInstalledComponents);

        // Record the log file which was used during the extraction.

        mainAttributes.putValue(HelperList.meLogFile, logFileFullName);

        // Record EFix and APAR information:

        maybeTransfer(mainAttribs, mainAttributes, HelperList.meDescription);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meAPAR);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.mePMR);

        mainAttributes.putValue(HelperList.meEventType, HelperList.backout);

        // Record XML product file processing information:

        maybeTransfer(mainAttribs, mainAttributes, HelperList.meProductFileType);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.mePropertiesVersionKey);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meProductFile);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meValidating);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meNameSpaceAware);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meAddHistory);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meXMLPathVersion);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meXMLPathEditionName);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meXMLPathEditionValue);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meXMLPathBuildDate);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meXMLPathBuildNumber);
        maybeTransfer(mainAttribs, mainAttributes, HelperList.meXMLPathEvent);

        // When there is EAR processing in the EAR temporary directory
        // during application there will be such processing again during
        // recovery.

        maybeTransfer(mainAttribs, mainAttributes, HelperList.mePrepareEARTmp);

        // Configuration instance preparation is not transferred.

        recordVersionInfo(mainAttributes);

        recordScripts(mainAttribs, mainAttributes);

        // Mark 'no-restore' on all helper classes.

        Attributes supportAttribs = new Attributes();

        supportAttribs.putValue(HelperList.meNoRestore, HelperList.meTrue);
        supportAttribs.putValue(HelperList.meHelperClass, HelperList.meTrue);

        for ( int supportNo = 0; supportNo < helperClasses.size(); supportNo++ ) {
            String supportName = (String) helperClasses.elementAt(supportNo);
            logOnly(prolix3, "Recording helper class entry into manifest: " + supportName);

            backupMap.put(supportName, supportAttribs);
        }

        // Mark those files which are marked with an absolute Path.

        for ( int abspNo = 0; abspNo < scanData.absolutePaths.size(); abspNo++ ) {
            String abspName = (String) scanData.absolutePaths.elementAt(abspNo);
            logOnly(prolix3, "Recording absolute-path entry into manifest: " + abspName);

            updateMap(backupMap, abspName, HelperList.meAbsolutePath, HelperList.meTrue);
        }

        // Mark those files which are deleted when restoring.
        // Mark these first, so that deletes are done first when restoring.

        for ( int deleteNo = 0; deleteNo < scanData.deleteFiles.size(); deleteNo++ ) {
            String deleteName = (String) scanData.deleteFiles.elementAt(deleteNo);
            logOnly(prolix3, "Recording delete entry into manifest: " + deleteName);

            updateMap(backupMap, deleteName, HelperList.meDelete, HelperList.meTrue);
        }

        // Mark those files which are marked for delete before write.
        // (Applies to files which are recorded for update.)

        for ( int deleteNo = 0; deleteNo < scanData.deleteBeforeWrites.size(); deleteNo++ ) {
            String deleteName = (String) scanData.deleteBeforeWrites.elementAt(deleteNo);
            logOnly(prolix3, "Recording delete-before-write entry into manifest: " + deleteName);

            updateMap(backupMap, deleteName, HelperList.meDeleteBeforeWrite, HelperList.meTrue);
        }

        // Mark all files which are only to be restored.

        for ( int restoreOnlyNo = 0; restoreOnlyNo < scanData.restoreOnlyFiles.size(); restoreOnlyNo++ ) {
            String restoreName = (String) scanData.restoreOnlyFiles.elementAt(restoreOnlyNo);
            logOnly(prolix3, "Recording restore-only entry into manifest: " + restoreName);

            updateMap(backupMap, restoreName, HelperList.meRestoreOnly, HelperList.meTrue);
        }

        // Mark all files which are AsInstallable.

        Iterator installableEntries = scanData.asInstallable.keySet().iterator();

        while ( installableEntries.hasNext() ) {
            String installableName = (String) installableEntries.next();
            logOnly(prolix3, "Recording AsInstallable manifest: " + installableName);

            updateMap(backupMap, installableName, HelperList.meAsInstallable, HelperList.meTrue);
        }

        // Mark all files which are AsInstalled.

        Iterator installedEntries = scanData.asInstalled.keySet().iterator();

        while ( installedEntries.hasNext() ) {
            String installedName = (String) installedEntries.next();
            logOnly(prolix3, "Recording AsInstalled manifest: " + installedName);

            updateMap(backupMap, installedName, HelperList.meAsInstalled, HelperList.meTrue);
        }

        // Mark all files which are as-application.

        Iterator applicationEntries = scanData.asApplication.keySet().iterator();

        while ( applicationEntries.hasNext() ) {
            String applicationName = (String) applicationEntries.next();
            logOnly(prolix3, "Recording as-application entry into manifest: " + applicationName);

            updateMap(backupMap, applicationName, HelperList.meAsApplication, HelperList.meTrue);
        }

        // Scan update data:

        for ( int updateNo = 0; updateNo < scanData.updateFiles.size(); updateNo++ ) {
            String updateName = (String) scanData.updateFiles.elementAt(updateNo);
            String fullUpdateName = scanData.nameFile(updateName);

            logOnly(prolix3, "Update file: " + updateName);
            logOnly(prolix3, "Full name:   " + fullUpdateName);

            if ( scanData.withDeleteAction.get(updateName) != null ) {
                log("Delete before update");

                // PQ59675: record permissions into Manifest from local system
                // PQ59675: for delete before update files

                ISystemFile updateFile = localSystem.newFile(fullUpdateName);

                String permissions = updateFile.getPermissions();
                String group = updateFile.getGroup();
                String owner = updateFile.getOwner();

                log("  Permissions: " + permissions);
                log("  Group:       " + group);
                log("  Owner:       " + owner);

                updateMap(backupMap, updateName, HelperList.meChmod, permissions);
                updateMap(backupMap, updateName, HelperList.meChgrp, group);
                updateMap(backupMap, updateName, HelperList.meChown, owner);

                // PQ59675: end record permissions into Manifest from local system

            } else if ( scanData.withChmodAction.get(updateName) != null ) {
                log("Update file");

                // PQ59675: record permissions into Manifest from local system
                // PQ59675: for update files

                ISystemFile updateFile = localSystem.newFile(fullUpdateName);

                String permissions = updateFile.getPermissions();
                String group       = updateFile.getGroup();
                String owner       = updateFile.getOwner();

                log("  Permissions: " + permissions);
                log("  Group:       " + group);
                log("  Owner:       " + owner);

                updateMap(backupMap, updateName, HelperList.meChmod, permissions);
                updateMap(backupMap, updateName, HelperList.meChgrp, group);
                updateMap(backupMap, updateName, HelperList.meChown, owner);

                // PQ59675: end record permissions into Manifest from local system

                if ( scanData.withUpdateAction.get(updateName) == null ) {
                    logOnly(prolix3, "Chmod only file");

                    updateMap(backupMap, updateName, HelperList.meChmodOnly, HelperList.meTrue);
                }

            } else {
                logOnly(prolix3, "Plain update");
            }
        }
        
        // Open the backup jar, incidentally writing out the manifest
        // that has been constructed.

        backupJarOutput = openJarOutput(backupJarName, backupManifest);

        if ( backupJarOutput == null ) {
            logError(11, "Unable to open backup jar: " + backupJarName);
            return false;
        }

        // Can't return anymore without first closing the backup jar file.
        // Use 'goodStatus' as a guard against further processing.

        // Now copy the helper classes from the input jar to the backup jar

        boolean goodStatus = true;

        JarFile jf;

        try {
            jf = new JarFile(decodedJarName);

        } catch ( IOException ex ) {
            logError(2, "Failed to open delta jar: " + decodedJarName, ex);
            jf = null;
            goodStatus = false;
        }

        if ( goodStatus ) {
            for ( int helperNo = 0; goodStatus && (helperNo < helperClasses.size()); helperNo++ ) {
                String helperName = (String) helperClasses.elementAt(helperNo);
                goodStatus = addRawEntry(helperName, jf);
            }

            try {
                jf.close();
            } catch ( IOException ex ) {
                logError(69, "Failed to close delta jar: " + decodedJarName, ex);
                goodStatus = false;
            }
        }

        // Next, record any files which must be backed up.

        if ( goodStatus && !basicRecordScan() ) {
            logError(61, "Failed to record scan to backup jar: " + backupJarName);
            goodStatus = false;
        }

        if ( !closeJarStream(backupJarOutput, backupJarName) ) {
            logError(84, "Failed to close backup jar: " + backupJarName);
            goodStatus = false;
        }

        backupJarOutput = null;

        logOnly(prolix4, "Backup Completed: " + backupJarName);

        return goodStatus;
    }

    protected void maybeTransfer(Attributes source, Attributes target, String key)
    {
        String sourceValue = source.getValue(key);

        if ( sourceValue != null )
            target.putValue(key, sourceValue);
    }

    protected void recordVersionInfo(Attributes attributes)
    {
        if ( productFileName == null ) {
            logOnly(prolix3, "No product file; disabling product handling in backup.");
            return;
        }

        // Turn on version checking in the backup to the same level
        // as the update.

        if ( !fSkipVer ) {
            logOnly(prolix3, "Version checking enabled; enabling version checking in backup.");

            // Be careful -- the version may have been updated.
            // Store the <<ending>> version value as the value to check.

            if ( !ckVersion.equals("?") ) {
                String endingVersion = getEndingVersion();
                if ( endingVersion == null )
                    endingVersion = startingVersion;

                logOnly(prolix3, "  Enabling version prerequisite [ " + endingVersion + " ]");
                attributes.putValue(HelperList.meCkVersion, endingVersion);
            }

            if ( !ckEditionName.equals("?") ) {
                logOnly(prolix3, "Enabling edition name prerequisite [ " + editionName + " ]");
                attributes.putValue(HelperList.meCkEditionName, editionName);
            }

            if ( !ckEditionValue.equals("?") ) {
                logOnly(prolix3, "Enabling edition value prerequisite [ " + editionValue + " ]");
                attributes.putValue(HelperList.meCkEditionValue, editionValue);
            }

        } else {
            logOnly(prolix3, "No version checking; disabling version checking in backup.");
        }

        if ( productFileName == null ) {
            logOnly(prolix3, "Product file update is not active;" +
                    " omitting starting version information from backup.");
            return;

        } else if ( po.keywordUsed(k_UpdateProductFile) && !po.getBool(k_UpdateProductFile) ) {
            logOnly(prolix3, "Product file update was disabled;" +
                             " omitting starting version information from backup.");
            return;

        } else {
            logOnly(prolix3, "Storing original version information to backup.");
        }

        // Doing an XML update could be seen as requiring that prerequisite
        // be performed when performing a backup, but such checks are not
        // currently added.  However, a the version and build number updates
        // which were performed during the XML update must be reversed.

        if ( versionChanged() ) {
            logOnly(prolix3, "  Storing starting version [ " + startingVersion + " ]");
            attributes.putValue(HelperList.meNewVersion, startingVersion);
        }

        if ( buildNumberChanged() ) {
            logOnly(prolix3, "  Storing starting build number [ " + startingBuildNumber + " ]");
            attributes.putValue(HelperList.meNewBuildNumber, startingBuildNumber);
        }

        if ( buildDateChanged() ) {
            logOnly(prolix3, "  Storing starting build date [ " + startingBuildDate + " ]");
            attributes.putValue(HelperList.meNewBuildDate, startingBuildDate);
        }
    }

    protected void updateMap(Map map, String mapKey, String attribKey, String attribValue)
    {
        Attributes attributes = (Attributes) map.get(mapKey);

        if ( attributes == null ) {
            attributes = new Attributes();
            map.put(mapKey, attributes);
        }

        attributes.putValue(attribKey, attribValue);
    }

    // Record virtual scripts; changing the un-commands for both
    // pre and post scripts into cmmands.
    //
    // Leave the scripts in their same order; the expectation is that
    // un-commands have been put into their proper (reversed) order by
    // the filter file builder.
    //
    // Scripts are stored as:
    //
    //     xtr_PreScript<scriptNo> --> <preScriptName>
    //     xtr_PreScript<scriptNo>Cmd<cmdNo> --> <command>
    //     xtr_PreScript<scriptNo>UnCmd<cmdNo> --> <command>
    //
    //     xtr_PostScript<scriptNo> --> <postScriptName>
    //     xtr_PostScript<scriptNo>Cmd<cmdNo> --> <command>
    //     xtr_PostScript<scriptNo>UnCmd<cmdNo> --> <command>
    //
    // Scripts are numbered consecutively starting at zero.
    // For each script, commands (and un-commands) are number consecutively
    // starting at zero.
    
    protected void recordScripts(Attributes applyAttributes,
                                 Attributes restoreAttributes)
    {
        recordScripts(applyAttributes, restoreAttributes, HelperList.meEntryScript);
        recordScripts(applyAttributes, restoreAttributes, HelperList.mePreScript);
        recordScripts(applyAttributes, restoreAttributes, HelperList.mePostScript);
    }

    protected void recordScripts(Attributes applyAttributes,
                                 Attributes restoreAttributes,
                                 String scriptType)
    {
        int scriptNo = 0;
        String scriptKey = scriptType + scriptNo;

        String scriptName;

        while ( (scriptName = applyAttributes.getValue(scriptKey)) != null ) {
            restoreAttributes.putValue(scriptKey, scriptName);

            String unCmdPrefix = scriptKey + HelperList.meUnCmd;
            String cmdPrefix   = scriptKey + HelperList.meCmd;

            int commandNo = 0;
            String nextCommand;

            while ( (nextCommand = applyAttributes.getValue(unCmdPrefix + commandNo)) != null ) {
                String cmdKey = cmdPrefix + commandNo;
                restoreAttributes.putValue(cmdKey, nextCommand);

                String argPrefix = unCmdPrefix + commandNo + HelperList.meArg;

                int argNo = 0;
                String nextArg;

                while ( (nextArg = applyAttributes.getValue(argPrefix + argNo)) != null ) {
                    String argKey = cmdKey + HelperList.meArg + argNo;
                    restoreAttributes.putValue(argKey, nextArg);

                    argNo++;
                }

                commandNo++;
            }

            scriptNo++;
            scriptKey = scriptType + scriptNo;
        }
    }

    // Used to be 'addHelperClass', but this is now used for more than
    // just helper classes.

    protected boolean addRawEntry(String rawEntryName, JarFile jf)
    {
        logOnly(prolix5, "Adding raw entry " + rawEntryName + " to backup Jar");

        JarEntry ze = jf.getJarEntry(rawEntryName);
        if ( ze == null ) {
            logError(63, "Raw Entry not found:" + rawEntryName);
            return false;
        }

        InputStream is;

        try {
            is = jf.getInputStream(ze);

        } catch ( IOException ex ) {
            logError(67, "jarFile.getInputStream() failed", ex);
            return false;
        }

        boolean result = true;

        if ( transferJarEntry(is, backupJarOutput, rawEntryName, ze) == -1 )
            result = false;

        try {
            is.close();

        } catch ( IOException ex ) {
            logError(68, "close of inputStream failed", ex);
            result = false;
        }

        return result;
    }

    // Now, actual copy the backup entries into the jar.  Put the delete
    // entries first, as this minimizes size requirements.

    protected boolean basicRecordScan()
    {
        // logDebug("Backing up jar entries: " + backupJarName);

        int numUpdateFiles = scanData.updateFiles.size();
        int numDeleteFiles = scanData.deleteFiles.size();
        int numRestoreOnlyFiles = scanData.restoreOnlyFiles.size();

        int tot2backup = numUpdateFiles + numDeleteFiles + numRestoreOnlyFiles;
        int backupCount = 0;

        for ( int deleteNo = 0; deleteNo < numDeleteFiles; deleteNo++ ) {
            String fileName = (String) scanData.deleteFiles.elementAt(deleteNo);

            logOnly(prolix5, "Recording delete entry for: " + fileName + " into jar.");

            JarEntry deleteEntry = new JarEntry(fileName);

            try {
                backupJarOutput.putNextEntry(deleteEntry);

            } catch ( IOException ex ) {
                logError(78, "Failed to prepare new backup entry: " + fileName, ex);
                return false;
            }

            if ( !transferDummyByte(backupJarOutput) ) {
                logError(71, "Failed to fill delete entry.");
                return false;
            }

            logOnly(prolix3, "adding delete entry for: " + fileName );
            backupCount++;
        }

        for ( int updateNo = 0; updateNo < numUpdateFiles; updateNo++ ) {
            String fileName = (String) scanData.updateFiles.elementAt(updateNo);

            logOnly(prolix3, "Recording update entry for: " + fileName + " into jar.");

            JarEntry updateEntry = new JarEntry(fileName);
            // Maintain file time stamp on backup.
            String fullFileName = scanData.nameFile(fileName);
            File   fullFileNameFile = new File( fullFileName );
            if ( fullFileNameFile.exists() ) {
               updateEntry.setTime( fullFileNameFile.lastModified() );
            }

            try {
                backupJarOutput.putNextEntry(updateEntry);

            } catch ( IOException ex ) {
                logError(77, "Failed to prepare new backup entry: " + fileName, ex);
                return false;
            }

            // PQ59675: Check if the entry is not a delete and not an update:
            // PQ59675: In this case, the file contents are not being modified;
            // PQ59675: Just transfer in a dummy byte.
            // PQ59675:
            // PQ59675: In the update code, make sure to avoid modifying the file contents.
            // PQ59675:
            // PQ59675: In the manifest, make sure the entry is marked as chmod only.

            if ( (scanData.withDeleteAction.get(fileName) == null) &&
                 (scanData.withUpdateAction.get(fileName) == null) &&
                 (scanData.withSpecialUpdate.get(fileName) == null) ) {

                if ( !transferDummyByte(backupJarOutput) ) {
                    logError(186, "Failed to fill chmod only entry.");
                    return false;
                }

            } else {
                if ( transferIn(backupJarOutput, fullFileName) == -1 ) {
                    logError(76, "Failed to fill backup entry: " + fileName);
                    // return false;   // make this a soft error per defect 90231

                } else {
                    logOnly(prolix3, "Backup " + fileName);
                    backupCount++;
                }
            }

            percentComplete("Backing Up", backupCount, tot2backup, 4000);
        }

        if ( numRestoreOnlyFiles > 0 ) {
            JarFile jf;

            try {
                jf = new JarFile(decodedJarName);

            } catch ( IOException ex ) {
                logError(183, "Failed to open jar to record restore-only entries " + decodedJarName, ex);
                return false;
            }

            // RestoreOnly applies to files which are only processed during
            // a restore operation.  No recording is needed.

            boolean transferFailed = false;

            for ( int restoreOnlyNo = 0;
                  !transferFailed && (restoreOnlyNo < numRestoreOnlyFiles);
                  restoreOnlyNo++ ) {

                String restoreOnlyName = (String) scanData.restoreOnlyFiles.elementAt(restoreOnlyNo);

                logOnly(prolix3, "Recording restore-only entry: " + restoreOnlyName + " into jar.");

                if ( !addRawEntry(restoreOnlyName, jf) ) {
                    logError(184, "Failed to place restore-only entry: " + restoreOnlyName);
                    transferFailed = true;

                } else {
                    logOnly(prolix3, "Transferred " + restoreOnlyName);
                    backupCount++;
                    percentComplete("Backing Up", backupCount, tot2backup, 4000);
                }
            }

            try {
                jf.close();

            } catch ( IOException ex ) {
                logError(185, "Failed to close jar to record restore-only entries " + decodedJarName, ex);
                return false;
            }

            if ( transferFailed )
                return false;
        }

        percentComplete("Backing Up", backupCount, tot2backup, 0);
        logOnly(prolix4, "Recorded backup jar entries: " + backupJarName);

        return true;
    }

    // Verify the jar file, and display diagnostic output.

    protected int entryCount = 0;
    protected String strEntryCount = "0";

    protected boolean verifyJarFile()
    {
        logOnly(prolix4, "Verifying jar file entries.");

        try {
            JarFile jar = new JarFile(decodedJarName, false);

            // Subtract one to skip the manifest entry,
            // which can never be retrieved.

            entryCount = jar.size() - 1;
            strEntryCount = Integer.toString(entryCount);
            logOnly(prolix3, "Entries in the jar file: " + strEntryCount);

            displayJarManifest(jar);

        } catch ( IOException ex ) {
            logError(9, "IOException verifying jar file entries", ex);
            return false;
        }

        log("");
        logOnly(prolix3, "Scanned " + strEntryCount + " update entries.");

        return true;
    }

    // Section for extracting and applying delta information.
    // This is a wrapper method which is responsible for opening
    // and closing the jar file, and for providing wrapper logging.

    protected JarInputStream deltaJarInput = null;

    protected boolean processJarFile()
    {
        logOnly(prolix3, "Processing jar: " + decodedJarName);

        deltaJarInput = openJarInput(decodedJarName);

        if ( deltaJarInput == null ) {
            logError(73, "Unable to open delta jar: " + decodedJarName);
            return false;
        }

        boolean result = false;

        try {
            basicProcessJarFile();
            result = true;

        } finally {
            if ( deltaJarInput != null ) {
                boolean didClose = closeJarStream(deltaJarInput, decodedJarName);
                deltaJarInput = null;

                if ( !didClose ) {
                    logError(64, "Failed to close delta jar: " + decodedJarName);
                    result = false;
                }
            }
        }

        if ( result )
            logOnly(prolix3, "Completed processing jar: " + decodedJarName);

        return result;
    }

    // Section for extracting and applying delta information.
    // This is the process loop.  Set the initial and final process
    // times, and handle the main entry loop.
    //
    // The process time updates occur in 'fetchEntry'.
    //
    // Use 'fetch' and 'consume' to allow look-ahead on jar entries.
    // This is required during child jar processing, which must loop
    // until there are no more child jar entries.

    // P0: Fetched, consumed
    // P1: Fetched, not consumed;
    //
    // Always: P0 -> P1 -> P0

    protected long processCount;

    protected void basicProcessJarFile()
    {
        processCount = 0;

        resetInstances();

        fetchEntry();       // P0 -> P1

        while ( nextJarEntry != null ) {
            consumeEntry(); // P1 -> P0
            handleEntry();  // P0 -> P0, P1
            fetchEntry();   // P0, P1 -> P1
        }

        percentComplete("Applying entry", processCount, entryCount, 0);
    }

    // Overall section of fetching jar entries, and associated
    // information.
    //
    // Need to be able to seek-ahead the see if the next jar
    // entry is for a child jar file.
    //

    // Special data for EAR processing:

    // Need to be able to iterate across configuration instances
    // and across nodes.  (There will always be at least one
    // node per configuration instance.)
    //
    // When a node is selected, set the current binary location
    // to be the binary location for that node.
    //
    // See inner class 'InstanceData', and the 'instances',
    // hashtable, both earlier in this file.

    protected Iterator instanceIterator     = null; // () --> InstanceData

    protected InstanceData previousInstance = null;
    protected InstanceData currentInstance  = null;

    protected String currentLocation        = null;
    protected String currentNodeName        = null;
    protected String currentCellName        = null;

    protected String currentEarNodeTmp      = null;

    protected boolean cycleAsMetadata       = false;
    protected boolean afterAsMetadata       = false;

    // When moving onto a new instance, 'cycleAsMetadata'
    // is set to true.
    //
    // When cycling through config instances;
    // When handling 'asInstalled' entries;
    // When handling an 'asMetadata' entry;
    // Do an inner cycle, once on the deployment,
    // and once on the config based metadata.

    // Instance cycling:

    protected void resetInstances()
    {
        if ( !fRestore && (installation != null) ) {
            logOnly(prolix5, "Resetting instances and nodes");

            resetInstanceIterator();
            acceptNextInstance();
        }
    }

    protected void resetInstanceIterator()
    {
        instanceIterator = installation.getInstances();
    }

    protected void acceptNextInstance()
    {
        currentInstance = (InstanceData) instanceIterator.next();

        if ( currentInstance == previousInstance ) {
            logOnly(prolix5, "Cycling onto same instance.");
            return;
        }

        currentLocation = currentInstance.getLocation();
        currentNodeName = currentInstance.getNodeName();
        currentCellName = currentInstance.getCellName();

        currentEarNodeTmp = earTmpFullName + slash + currentNodeName;

        cycleAsMetadata = false;
        afterAsMetadata = false;

        logOnly(prolix5, "Accepted Instance:");
        logOnly(prolix5, "  Location: " + currentLocation);
        logOnly(prolix5, "  Node    : " + currentNodeName);
        logOnly(prolix5, "  Cell    : " + currentCellName);
        logOnly(prolix5, "  EAR Temp: " + currentEarNodeTmp);
        logOnly(prolix5, "  Meta Cycle is enabled");
    }

    protected boolean anotherInstance()
    {
        boolean stillAnother = instanceIterator.hasNext();

        if ( !stillAnother )
            resetInstanceIterator();

        acceptNextInstance();

        return stillAnother;
    }

    protected Vector currentJoin         = null;
    protected int currentJoinCount       = -1;

    protected int currentJoinNo          =  0;
    protected String[] currentServerJoin = null;

    protected void resetJoin()
    {
        if ( !fRestore && (installation != null) ) {
            logOnly(prolix5, "Resetting join data");

            resetJoinList();
            acceptNextJoin();
        }
    }

    protected void resetJoinList()
    {
        currentJoin = currentInstance.getServerJoin();
        currentJoinCount = currentJoin.size();

        currentJoinNo = 0;
    }

    protected void acceptNextJoin()
    {
        currentServerJoin = (String[]) currentJoin.elementAt(currentJoinNo);
        currentJoinNo++;

        logOnly(prolix5, "  Next Join Cell  : " +
                         currentServerJoin[InstanceData.JOIN_CELL_NAME_OFFSET]);;
        logOnly(prolix5, "  Next Join Node  : " +
                         currentServerJoin[InstanceData.JOIN_NODE_NAME_OFFSET]);;
        logOnly(prolix5, "  Next Join Server: " +
                         currentServerJoin[InstanceData.JOIN_SERVER_NAME_OFFSET]);;
    }

    protected boolean anotherJoin()
    {
        boolean stillAnother = (currentJoinNo < currentJoinCount);

        if ( !stillAnother )
            resetJoinList();
 
        acceptNextJoin();

        return stillAnother;
    }

    protected boolean anotherJoinInInstance()
    {
        boolean stillAnotherJoin = anotherJoin();

        if ( !stillAnotherJoin ) {
            stillAnotherJoin = anotherInstance();
            resetJoin();
        }

        return stillAnotherJoin;
    }

    // For example:
    //
    //     currentEarPrefix -- currentEarName -- currentEarTail
    //         currentEarPrefixPath
    //         currentEarPath
    //
    //     '/installable/myEar.ear/myFile.xml' ==>
    //     '/installable/'  -- 'myEar.ear'    -- '/myFile.xml'
    //         '<earTmp>/node'
    //         '<earTmp>/node/installable/myEar.ear'
    //
    //     '/application/myEar.ear/myFile.xml' ==>
    //     '/application/'  -- 'myEar.ear'    -- '/myFile.xml'
    //         '<earTmp>/node'
    //         '<earTmp>/node/application/myEar.ear'
    //
    //     '/myEar.ear/myFile.xml' ==>
    //     '/'              -- 'myEar.ear'    -- '/myFile.xml'
    //         '<deployment>' (wo) '/myEar.ear'
    //         '<deployment>'
    //

    // EAR Processing constants ...

    protected String currentEarPrefix          = null;
    protected String currentEarName            = null;
    protected String currentEarTail            = null;

    protected DeploymentData currentDeployment = null;
    protected String currentBinariesURL        = null;

    protected String currentEarPrefixPath      = null;
    protected String currentEarPath            = null;

    protected String currentEarMetaPath        = null;
    protected String currentEarMetaPrefixPath  = null;

    protected boolean acceptEntryForEar(String entryName)
    {
        logOnly(prolix5, "Examining entry for EAR: " + entryName);

        String[] earParts = getEarParts(entryName);

        currentEarName = earParts[EAR_NAME_OFFSET];
        if ( currentEarName == null ) {
            logOnly(prolix2, "Malformed EAR entry");
            return false;
        }

        currentEarPrefix = earParts[EAR_PREFIX_OFFSET];
        currentEarTail   = earParts[EAR_TAIL_OFFSET];

        return reuseEarEntry();
    }

    protected boolean reuseEarEntry()
    {
        if ( handlingAsInstalled ) {
            currentDeployment = currentInstance.getDeploymentDatum(currentEarName);

            if ( currentDeployment == null ) {
                logOnly(prolix4, "Skipping: No deployment is available for " + currentEarName );

                currentEarPath = null;
                currentEarPrefixPath = null;

                return false;
            }

            // d65352 - In DM case need to check for existance of System property to see if ear extraction was OK.
            // d77272 - Check for new Ear states, If EarStatus is OK or NOT_DEPLOYED, we still process the ear.
            String earStatus = (String)System.getProperties().get( WPEarActor.KEY_WPEAR_INVALID_PREFIX + currentEarName );
            if ( !(earStatus == null || earStatus == WPEarActor.WPEAR_VALID_STATE_OK || earStatus == WPEarActor.WPEAR_VALID_STATE_NOT_DEPLOYED) ) {
               logOnly(prolix4, "Skipping: The Ear (" + currentEarName + ") is not currently deployed.");

               currentEarPath = null;
               currentEarPrefixPath = null;

               return false;
            }
            

            currentBinariesURL = currentDeployment.getBinariesURL();

            int prefixLength =
                currentBinariesURL.length() -
                ( currentEarPrefix.length() + currentEarName.length() );

            currentEarPrefixPath = currentBinariesURL.substring(0, prefixLength);
            logOnly(prolix5, "EAR prefix: " + currentEarPrefixPath);

            currentEarPath = currentBinariesURL;
            logOnly(prolix5, "Path to EAR: " + currentEarPath);

            if ( handlingAsMetadata ) {
                currentEarMetaPath = currentDeployment.getAltMetadataPath();
                logOnly(prolix5, "Path to Meta EAR: " + currentEarMetaPath);

                int metaPrefixLength =
                    currentEarMetaPath.length() -
                    currentDeployment.getInnerEarName().length();

                currentEarMetaPrefixPath = currentEarMetaPath.substring(0, metaPrefixLength);
                logOnly(prolix5, "EAR Meta Prefix: " + currentEarMetaPath);
            }

        } else {
            currentEarPrefixPath = currentEarNodeTmp;
            logOnly(prolix5, "EAR prefix: " + currentEarPrefixPath);

            currentEarPath = currentEarNodeTmp + asPath(currentEarPrefix) + currentEarName;
            logOnly(prolix5, "Path to EAR: " + currentEarPath);
        }

        return earDirExists(currentEarPath);
    }

    // EAR processing with a name rule modifies the ear processing by
    // using the name rule instead of the set entry name.
    //
    // When accepting the next entry, the currentEarName is set by
    // performing a substitution with the current join on the ruled
    // ear name.

    // We assume that the name rule applies to the ear name, and to no
    // other part of the entry.  This allows the substitution to be
    // limited to the ear name itself.  Substitution is not performed
    // on the prefix or tail ear parts.

    protected String currentRuledEarName       = null;

    protected boolean acceptRuledEntryForEar(String entryName)
    {
        logOnly(prolix5, "Examining ruled entry for EAR: " + entryName);
        logOnly(prolix5, "Using name rule in place of entry name: " + nameRule);

        String[] earParts = getEarParts(nameRule);

        currentRuledEarName = earParts[EAR_NAME_OFFSET];
        if ( currentRuledEarName == null ) {
            logOnly(prolix2, "Malformed EAR entry");
            return false;
        }

        currentEarPrefix = earParts[EAR_PREFIX_OFFSET];
        currentEarTail   = earParts[EAR_TAIL_OFFSET];

        return reuseRuledEarEntry();
    }

    protected boolean reuseRuledEarEntry()
    {
        currentEarName = HelperList.ResolveMacro(currentRuledEarName, currentServerJoin);

        return reuseEarEntry();
    }

    // Entry scanning ...

    protected boolean consumedEntry    = true;

    protected JarEntry   nextJarEntry              = null;
    protected String     nextJarEntryName          = null;
    protected Attributes nextEntryAttributes       = null;
    protected boolean    nextEntryAttributesFailed = false;

    protected String     nextJarEntryPath          = null;

    protected boolean    nextEntryIsMasked         = false;

    protected void clearEntry()
    {
        consumedEntry             = false;

        nextJarEntry              = null;
        nextJarEntryName          = null;
        nextEntryAttributes       = null;
        nextEntryAttributesFailed = false;

        nextJarEntryPath          = null;

        nextEntryIsMasked         = false;

        logOnly(prolix5, "Cleared entry data");
    }

    protected void consumeEntry()
    {
        consumedEntry = true;
    }

    // Need to remember is per-instance processing is active.

    protected boolean handlingPerInstance = false;

    // Need to remember if EAR processing is active.

    protected boolean handlingAsInstallable = false;
    protected boolean handlingAsApplication = false;
    protected boolean handlingAsInstalled   = false;
    protected boolean handlingAsMetadata    = false;

    protected String  nameRule = null;

    // 'nextChildPath' holds the fully qualified name of the file
    // associated with the next entry.
    //
    // 'nextChildData' holds the split data from the next entry,
    // when the next entry is for a child jar file.

    protected String nextChildPath = null;
    protected ChildJarEntry nextChildData = null;

    // Special case: A file entry which not a jar entry may reuse the
    // entry data as previously retrieved.  This optimization allows
    // iteration across configuration instances for those
    // cases in which there is one update entry to apply to each
    // instance.  The alternative is to recycle the entries.
    //
    // A file entry which is a jar entry is expected to have
    // a batch of entries; direct iteration is not possible.

    protected void fetchEntry()
    {
        if ( !consumedEntry ) {
            log("Skipping fetch: already performed during jar processing.");
            return;
        }

        logOnly(prolix3, "Fetching entry ...");

        if ( !fRestore ) {
            // At this point, 'nextChildData' and 'nextEntryAttributes'
            // are for the entry which was previously processed.

            // Did we <not> just process an entry for a jar entry?
            // And, did we <not> just process an entry for a jar?

            if ( (nextChildData == null) && !testKeyForJar(nextEntryAttributes) ) {
                if ( handlingPerInstance ) {

                    // Unlike EAR processing, which is disabled when the EAR is not
                    // present, per-instance handling always proceeds for each instance.

                    if ( anotherInstance() ) {
                        nextChildPath = currentLocation + nextJarEntryPath;
                        logOnly(prolix3, "  Continuing per-instance handling: " + nextChildPath);

                        recycleToMark();
                        return;

                    } else {
                        logOnly(prolix3, "  Unable to continue per-instance handling.");
                        handlingPerInstance = false;

                        clearEntryMark();
                        clearPrior();
                    }

                } else if ( handlingAsInstallable || handlingAsApplication || handlingAsInstalled ) {
                    // When handling EAR metadata, which is only possible when
                    // handling installed EAR data, processing proceeds twice
                    // for each usable instance.  Once for the installed copy
                    // of the EAR data, and once for the meta copy of the EAR
                    // data.

                    if ( handlingAsMetadata && cycleAsMetadata ) {
                        cycleAsMetadata = false;
			               afterAsMetadata = true;

                        nextChildPath = currentEarMetaPath + currentEarTail;
                        logOnly(prolix3, "  Cycling with Meta EAR handling: " + nextChildPath);

                        recycleToMark();
                        return;
                    }

                    if ( nameRule != null ) {
                        boolean reuse = false;

                        while ( anotherJoinInInstance() && !(reuse = reuseRuledEarEntry()) );

                        if ( reuse ) {
                            nextChildPath = currentEarPath + currentEarTail;
                            logOnly(prolix3, "  Continuing EAR handling: " + nextChildPath);

                            recycleToMark();
                            return;
                        }
                    } else {
                        boolean reuse = false;

                        // Have to make sure to keep trying as long as there are more
                        // instances.  Just because the entry doesn't work in one
                        // entry doesn't mean that it can't be used in the next.
                        
                        while ( anotherInstance() && !(reuse = reuseEarEntry()) );
                        
                        if ( reuse ) {
                            nextChildPath = currentEarPath + nextJarEntryPath;
                            logOnly(prolix3, "  Continuing EAR handling: " + nextChildPath);
                            
                            recycleToMark();
                            return;
                        }
                    }

                    logOnly(prolix3, "  Unable to continue EAR handling: " + nextJarEntryName);
                            
                    handlingAsInstallable = false;
                    handlingAsApplication = false;
                    handlingAsInstalled   = false;
		              handlingAsMetadata    = false;

                    nameRule = null;

                    clearEntryMark();
                    clearPrior();

                } else {
                    logOnly(prolix5, "  No EAR handling is active.");
                }
            }
        }

        // Now we are starting out on a fresh entry.  Clear out the
        // immediate entry data, and clear out the child data.

        // Note that the handling settings may still be set; this can only be
        // the case when previous handling an entry for a jar or when previously
        // handling an entry for a jar entry.

        clearEntry();
        nextChildData = null;

        try {
            nextJarEntry = deltaJarInput.getNextJarEntry();

        } catch ( IOException ex ) {
            logError(12, "Failed to retrieve entry in: " + decodedJarName, ex);
            return;
        }

        if ( nextJarEntry == null ) {
            logOnly(prolix2, "Reached the end of entries.");
            return;
        }

        percentComplete("Applying entry", ++processCount, entryCount, 4000);

        // Pull the name and attributes out of the entry.

        exposeEntry();

        boolean isAbsolute = testKeyForAbsolutePath(nextEntryAttributes);

        // Do not allow absolute file processing for child jar entries.

        if ( !isAbsolute )
            nextChildData = splitEntry(nextJarEntryName);
        else
            nextChildData = null;

        if ( fRestore )
            setChildPathForRestore(isAbsolute);
        else
            setChildPathForApply(isAbsolute);
    }

    protected void exposeEntry()
    {
        nextJarEntryName = nextJarEntry.toString();

        try {
            nextEntryAttributes = nextJarEntry.getAttributes(); // might return null
            nextEntryAttributesFailed = false;

        } catch ( IOException ex ) {
            logError(22, "Failed to retrieve jar entry attributes: " + nextChildPath, ex);

            nextEntryAttributes = null;
            nextEntryAttributesFailed = true;
        }

        nextJarEntryPath = asPath(nextJarEntryName);
    }

    // No iteration across instances or nodes when within restoration;
    // however, the installable and application marks may be set,
    // in which case the application must be targetted against the
    // EAR temporary directory instead of the target director.

    protected void setChildPathForRestore(boolean isAbsolute)
    {
        logOnly(prolix3, "Preprocessing entry (restore): ");

        if ( isAbsolute ) {
            logOnly(prolix3, "Handling with an absolute path.");

            handlingAsInstallable = false;
            handlingAsApplication = false;
            handlingAsInstalled   = false;

            nextChildPath = asPath( ResolveMacro(nextJarEntryName) );

            return;
        }

        // Note that seeding is turned off for restore operations:
        // There is no looping over entries during a restore.

        if ( testKeyForAsInstallable(nextEntryAttributes) ) {
            logOnly(prolix3, "AsInstallable noted.");
            handlingAsInstallable = true;
            handlingAsInstalled   = false;
            handlingAsApplication = false;

        } else if ( testKeyForAsInstalled(nextEntryAttributes) ) {
            logOnly(prolix3, "AsInstalled noted.");
            handlingAsInstalled   = true;
            handlingAsApplication = false;
            handlingAsInstallable = false;

        } else if ( testKeyForAsApplication(nextEntryAttributes) ) {
            logOnly(prolix3, "AsApplication noted.");
            handlingAsInstallable = false;
            handlingAsInstalled   = false;
            handlingAsApplication = true;

        } else {
            logOnly(prolix3, "No EAR processing noted.");
            handlingAsInstallable = false;
            handlingAsInstalled   = false;
            handlingAsApplication = false;
        }

        // When processing for a restore, the entry name
        // is prefixed with the node name for the instance
        // to be updated.
        //
        // Installed EAR updates were marked as absolute updates,
        // and the ASInstalled mark was omitted.


        if ( handlingAsApplication )
            nextChildPath = earTmpFullName + nextJarEntryPath;
        else if ( handlingAsInstallable ) {
            // The entry will already have the nodeName, so we do not want any node as part of the 
            nextChildPath = WPEarActor.getEarWorkArea( earTmpFullName ).getAbsolutePath() + File.separator + nextJarEntryPath;
        } else if ( handlingAsInstalled ) {
           // The entry will already have the nodeName, so we do not want any node as part of the 
            nextChildPath = WPEarActor.getEarWorkArea( earTmpFullName ).getAbsolutePath() + File.separator + nextJarEntryPath;
        } else
            nextChildPath = targetDirName + nextJarEntryPath;

        logOnly(prolix3, "Next entry name: " + nextJarEntryName);
        logOnly(prolix3, "     entry path: " + nextChildPath);
    }

    // When the next child data is null, the motion has carried past
    // the current batch of entries.
    //
    // If doing installable or application EAR processing, if there is
    // another instance, recycle to the marked instance.  The handling
    // settings are unchanged.
    //
    // Likewise for installed EAR processing, except that iteration is
    // at the node level.
    //
    // Otherwise, there was no iteration, or iteration is complete.
    // Process the next entry as usual.
    //
    // Note that the special case in 'fetchEntry' prevents any
    // handling to be active at this point unless handling is active
    // for a jar file.  This guarentees that a mark is set.
    //
    // When the split entry is non-null, the entry is a part of a
    // batch for a jar; simply proceed with the current handling
    // settings.

    protected void setChildPathForApply(boolean isAbsolute)
    {
        logOnly(prolix3, "Preprocessing entry (apply): ");

        if ( nextChildData == null ) {
            logOnly(prolix3, "Entry applies to a file.");
            logOnly(prolix5, "Ended per-instance and EAR processing batch");

            if ( (handlingPerInstance ||
                  (handlingAsInstallable || handlingAsApplication || handlingAsInstalled)) ) {
                logOnly(prolix3, "Per-instance or EAR processing; recycling on new instance.");

            } else {
                logOnly(prolix3, "No per-instance or EAR processing; no recycling.");

                clearEntryMark();
                clearPrior();

                if ( isAbsolute ) {
                    logOnly(prolix3, "Handling with an absolute path.");
                    nextChildPath = asPath( ResolveMacro(nextJarEntryName) );

                    logOnly(prolix3, "Next entry name: " + nextJarEntryName);
                    logOnly(prolix3, "     entry path: " + nextChildPath);

                    return;
                }

                boolean handlingEar;

                if ( testKeyForPerInstance(nextEntryAttributes) ) {
                    logOnly(prolix3, "Per-instance noted");
                    handlingEar = false;
                    handlingPerInstance = true;
                    seedPrior();

                } else {
                    if ( testKeyForAsInstallable(nextEntryAttributes) ) {
                        logOnly(prolix3, "AsInstallable noted");
                        handlingEar = true;
                        handlingAsInstallable = true;

                    } else if ( testKeyForAsApplication(nextEntryAttributes) ) {
                        logOnly(prolix3, "AsApplication noted");
                        handlingEar = true;
                        handlingAsApplication = true;

                    } else if ( testKeyForAsInstalled(nextEntryAttributes) ) {
                        logOnly(prolix3, "AsInstalled noted");
                        handlingEar = true;
                        handlingAsInstalled = true;

                        if ( testKeyForAsMetadata(nextEntryAttributes) ) {
                            logOnly(prolix3, "AsMetadata noted");
                            handlingAsMetadata = true;
                        }

                    } else {
                        handlingEar = false;
                    }

                    if ( handlingEar ) {
                        seedPrior();

                        // The naming rule is only supported for ear processing.

                        nameRule = testKeyForNameRule(nextEntryAttributes);
                        if ( nameRule != null )
                            logOnly(prolix3, "Noted naming rule: " + nameRule);
                    }
                }

                if ( !handlingPerInstance && !handlingEar )
                    logOnly(prolix3, "No per-instance or EAR processing mark.");
            }

        } else {
            logOnly(prolix3, "Entry applies to a jar entry.");
        }

        // Can get here for plain entry processing.
        //
        // Can get here because the entry is a part of a jar batch,
        // so the settings for the jar file are used;
        //
        // Can get here for per-instance processing, and a new instance
        // is being handled.
        //
        // Can get here because EAR processing is enabled, and a new
        // instance is being handled.

        if ( handlingPerInstance ) {
            logOnly(prolix3, "Per-instance (config) is active");
            nextChildPath = currentLocation + nextJarEntryPath;

        } else if ( handlingAsApplication || handlingAsInstalled ) {
            logOnly(prolix3, "Finding application of EAR processing for: " + nextJarEntryName);

            if ( nameRule == null )
                locateInstanceForEar();
            else
                locateRuledInstanceForEar();

            if (handlingAsInstalled) {
               nextChildPath = WPEarActor.getEarWorkArea( earTmpFullName, scanNodeName ).getAbsolutePath() +  slash + nextJarEntryPath;
            }

        } else if ( handlingAsInstallable ) {

            logOnly(prolix3, "Finding installableApp EAR processing for: " + nextJarEntryName);
            nextChildPath = WARActor.getWarWorkArea( earTmpFullName, scanNodeName ).getAbsolutePath() +  slash + nextJarEntryPath;
            
        } else {
            logOnly(prolix3, "Normal processing");
            nextChildPath = ResolveMacro( nextJarEntryPath );
            if ( !nextJarEntryPath.startsWith( "$<" ) ) {
               // This is not an absolute path spec, so prepend targetDir to path
               nextChildPath = targetDirName + nextChildPath;;
            }
        }

        logOnly(prolix3, "Next entry name: " + nextJarEntryName);
        logOnly(prolix3, "     entry path: " + nextChildPath);
    }

    protected void locateInstanceForEar()
    {
        boolean moreInstances = true;

        while ( moreInstances && !acceptEntryForEar(nextJarEntryName) )
            moreInstances = anotherInstance();

        if ( !moreInstances ) {
            logOnly(prolix3, "Found no instance to use EAR entry: " + nextJarEntryName);
            clearEarHandling();

        } else {
            logOnly(prolix3, "Found instance to use EAR entry: " + nextJarEntryName);
            nextChildPath = currentEarPath + currentEarTail;
        }
    }

    protected void locateRuledInstanceForEar()
    {
        resetJoin();

        boolean moreJoins = true;

        while ( moreJoins && !acceptRuledEntryForEar(nextJarEntryName) )
            moreJoins = anotherJoinInInstance();

        if ( !moreJoins ) {
            logOnly(prolix3, "Found no ruled instance to use EAR entry: " + nextJarEntryName);
            clearEarHandling();

        } else {
            logOnly(prolix3, "Found ruled instance to use EAR entry: " + nextJarEntryName);
            nextChildPath = currentEarPath + currentEarTail;
        }
    }

    protected void clearEarHandling()
    {
        nextChildPath = "***MASKED***";

        handlingAsInstallable = false;
        handlingAsApplication = false;
        handlingAsInstalled   = false;
        handlingAsMetadata    = false;

        nameRule = null;

        nextEntryIsMasked = true;
    }

    // Need to be able to recycle entries to a marked entry.

    // Recycling is attempted whenever moving to a new instance
    // or EAR case, even when no nested jar processing is active.
    //
    // Modify recycling to simply return in this case.
    //
    // This means that the entry mark must be cleared when done
    // with instance or EAR cases.  Otherwise, a stale mark will
    // be used, leading to an error.
    //
    // When there is no entry mark, and cycling is performed,
    // the update will proceed to a case in which a prior target
    // is available.
    //
    // Note: This will fail if 'replaceOnly' or 'addOnly' is specified
    // for an entry, as the prior target need not be set correctly
    // on the first pass through the instance cases or EAR cases.

    protected String markedEntryName = null;

    protected boolean isCycleMarked()
    {
        return ( markedEntryName != null );
    }

    protected void clearEntryMark()
    { 
        markedEntryName = null;
    }

    protected void setEntryMark(String entryMark)
    {
        markedEntryName = entryMark;
    }

    protected boolean recycleToMark()
    {
        if ( markedEntryName == null ) {
            logOnly(prolix4, "Skipping cycle request; no marked entry is available.");
            return false;

        } else {
            logOnly(prolix4, "Cycling to entry: " + markedEntryName);

            Object[] cycleData = cycleJarInput(deltaJarInput, decodedJarName, markedEntryName);
            if ( cycleData == null ) {
                logError(188, "Failed to recycle on jar: " + decodedJarName);

                deltaJarInput = null;
                nextJarEntry  = null;

                return false;

            } else {
                logOnly(prolix4, "Cycling to entry; success");

                deltaJarInput = (JarInputStream) cycleData[JAR_STREAM_OFFSET];
                nextJarEntry  = (JarEntry) cycleData[JAR_ENTRY_OFFSET];

                return true;
            }
        }
    }

    protected boolean seedPrior = false;
    protected String priorTarget = null;
    protected boolean priorTargetError = false;

    protected void seedPrior()
    {
        seedPrior = true;
    }

    /**
	 * @return  the seedPrior
	 * @uml.property  name="seedPrior"
	 */
    protected boolean getSeedPrior()
    {
        return seedPrior;
    }

    /**
	 * @return  the priorTarget
	 * @uml.property  name="priorTarget"
	 */
    protected String getPriorTarget()
    {
        return priorTarget;
    }

    /**
	 * @param priorTarget  the priorTarget to set
	 * @uml.property  name="priorTarget"
	 */
    protected void setPriorTarget(String fileName)
    {
        priorTarget = fileName;
    }

    protected void setPriorTargetError()
    {
        priorTargetError = true;
    }

    /**
	 * @return  the priorTargetError
	 * @uml.property  name="priorTargetError"
	 */
    protected boolean getPriorTargetError()
    {
        return priorTargetError;
    }

    protected void clearPrior()
    {
        seedPrior = false;

        priorTarget = null;

        priorTargetError = false;
    }


    // Code to split child jar entries.  See the top level
    // comment for child entry syntax.

    // There is a generic split method, plus other split
    // methods which couple the next jar entry with the generic
    // split method.

    // Entry syntax constants ...

    // Expected a child entry name of:
    //
    //    entry.name               -- a simple file
    //    jar.name!!entry.name     -- a child jar file entry
    //    jar.name!!!!             -- a child jar main manifest entry
    //    jar.name!!!!entry.name   -- a child jar manifest entry
    //
    // "!!" represents the value of the hex 01g character.

    protected static final String childDividor = "\u0013";

    protected final ChildJarEntry commonJarEntry = new ChildJarEntry();

    // The class holding parsed information from a child entry.

    protected static class ChildJarEntry
    {
        protected String parentJarName = null;
        protected String entryName     = null; // null for the main mainifest entry

        protected boolean isFileEntry  = true; // false for a manifest entry

        protected boolean isMain()
        {
            return( !isFileEntry && (entryName.length() == 0) );
        }
    }

    protected ChildJarEntry splitEntry(String childEntryName)
    {
        int firstDividorLoc = childEntryName.indexOf(childDividor);

        // When there is a dividor, the entry is for something inside a jar.

        if ( firstDividorLoc == -1 )
            return null;

        // Note that preceeding entries for jar contents is a file entry
        // having the name of the jar, and having an 'xtr_jar' setting.

        commonJarEntry.parentJarName = childEntryName.substring(0, firstDividorLoc);

        // When there is just one dividor, the entry maps to a jar entry.
        // When there are two dividors, the entry maps to a jar manifest entry.
        // The entry name will be null when the manifest entry is for the
        // main manifest.

        int secondDividorLoc = childEntryName.indexOf(childDividor, firstDividorLoc + 1);

        if ( secondDividorLoc == -1 ) {
            // A simple file within the jar.
            commonJarEntry.isFileEntry = true;
            commonJarEntry.entryName = childEntryName.substring(firstDividorLoc + 1);

        } else {
            // A manifest entry within the jar.
            commonJarEntry.isFileEntry = false;
            commonJarEntry.entryName = childEntryName.substring(secondDividorLoc + 1);
        }

        return commonJarEntry;
    }

    // Inner process method.  Test the entry, selecting the appropriate
    // handler.  Note that directory entries cannot be jar entries, and
    // cannot be child jar entries, so this top level directory check is
    // safe.

    // Container processing currently is limited to top level entries.

    // Enter P0

    protected void handleEntry()
    {
        boolean isConsumed;

        logOnly(prolix4, "Entering handleEntry: " + nextJarEntryName);

        if ( nextEntryIsMasked ) {
            logOnly(prolix4, "Entry was explicitly masked.");
            handleMaskedEntry();   // P0 -> P0

        } else if ( !isAllowedByComponent(nextJarEntryName, nextEntryAttributes) ) {
            logOnly(prolix4, "Entry was masked by component processing.");
            handleMaskedEntry();   // P0 -> P0

        } else {
            if ( nextJarEntry.isDirectory() )
                handleDirEntry();  // P0 -> P0
            else
                handleFileEntry(); // P0 -> P0, P1

            if ( handlingAsMetadata && !afterAsMetadata )
		cycleAsMetadata = true;
        }
    } // P0, P1

    // Enter P0

    protected void handleMaskedEntry()
    {
        logOnly(prolix3, "Skipping masked entry: " + nextChildPath);

        nextEntryIsMasked = false;

        if ( testKeyForJar(nextEntryAttributes) ) {
            fetchEntry();          // P0 -> P1
            consumeChildEntries(); // P1 -> P1
        }
    }

    // Handle dir entries.  Currently, dir entries cannot be deleted,
    // meaning that the dir entry must a create request.

    // Enter P0

    protected void handleDirEntry()
    {
        logOnly(prolix4, "Handling entry as directory: " + nextChildPath);

        ensureDirectory(nextChildPath);

        // Ignore the return code from 'ensureDirectory'; there is
        // no more processing to be done on this entry.
    }

    // Handle a file entry.  If the file entry had child data,
    // there is an error -- all child entries should follow a jar
    // entry and be consumed with that jar entry.

    // Enter P0

    protected void handleFileEntry()
    {
        logOnly(prolix4, "Handling entry as file: " + nextChildPath);

        if ( nextChildData != null ) {
            logError(13, "Unexpected child jar entry: " + nextJarEntryName);
            return; // P0
        }

        if ( nextEntryAttributesFailed ) {
            logError(74, "Failed to retrieve jar entry attributes: " + nextChildPath);
            return; // P0
        }

        if ( testKeyForRestoreOnly(nextEntryAttributes) ) {
            if ( fRestore ) {
                logOnly(prolix3, "   Mark for restore only: " + nextChildPath);

            } else {
                logOnly(prolix4, "   Marked for restore only; ignoring: " + nextChildPath);
                return; // P0
            }
        }

        if ( testKeyForNoRestore(nextEntryAttributes) ) {
            logOnly(prolix4, "   Marked for no restore; ignoring: " + nextChildPath);
            return; // P0
        }

        if ( testKeyForJar(nextEntryAttributes) ) {
            applyJarEntryJar(); // P0 -> P1
            return; // P1
        }

        if ( testKeyForDelete(nextEntryAttributes) ) {
            if ( !testKeyForNoDelete(nextEntryAttributes ) )
                deleteJarEntryFile();
            else
                logOnly(prolix3, "  Marked for delete and no delete; ignoring: " + nextChildPath);

            return; // P0
        }

        String preventAddFullName = (String) scanData.preventAdds.get(nextJarEntryName);

        if ( preventAddFullName != null ) {
            logOnly(prolix3, "  Skipping: Add only file exists: " + preventAddFullName);
            return; // P0
        }

        String preventReplaceFullName = (String) scanData.preventReplaces.get(nextJarEntryName);

        if ( preventReplaceFullName != null ) {
            logOnly(prolix3, "  Skipping: Update only file does not exist: " + preventReplaceFullName);
            return; // P0
        }

        // PQ59675: Do not extract if chmod only.
        // PQ59675: Added extra check, and code to pass in 'preserveAttributes'.

        // System.out.println("Diag #99 nextChildPath=(" + nextChildPath + " nextJarEntryName=(" + nextJarEntryName);

        if ( !testKeyForChmodOnly(nextEntryAttributes) ) {
            boolean deleteBeforeWrite = testKeyForDeleteBeforeWrite(nextEntryAttributes, nextJarEntryName);
            boolean preserveAttributes = !testKeyForChmod(nextEntryAttributes);

            extractJarEntryFile(deleteBeforeWrite, preserveAttributes);
        }

        if ( nextEntryAttributes == null )
            return; // P0

        // PQ59675: Set the permissions for the file as recorded in the jar

        String permissions = (String) nextEntryAttributes.getValue(HelperList.meChmod);
        String owner       = (String) nextEntryAttributes.getValue(HelperList.meChown);
        String group       = (String) nextEntryAttributes.getValue(HelperList.meChgrp);

        boolean didSet = updatePermissions(nextChildPath, permissions, owner, group); // Toss the result

        // PQ59675: Set the permissions for the file as recorded in the jar
    }

    protected boolean updatePermissions(String destPath,
                                        String permissions, String owner, String group)
    {
        logOnly(prolix4, "Updating permissions for " + destPath);

        if ( (permissions == null) && (owner == null) && (group == null) ) {
            logOnly(prolix4, "  No update is required.");
            return true;
        }

        boolean returnStatus = true;

        ISystemFile chmodFile = localSystem.newFile(destPath);
        
        if ( (permissions != null) ) {
            logOnly(prolix4, "  Permissions: " + permissions);

            if ( chmodFile.setPermissions(permissions) != 0 ) {
                logError(186, "Failed to set permissions ( " + permissions + " ) of " + destPath);
                returnStatus = false;
            }
        }
        
        if ( owner != null ) {
            logOnly(prolix4, "  Owner:       " + owner);

            if ( chmodFile.setOwner(owner) != 0 ) {
                logError(187, "Failed to set owner ( " + owner + " ) of " + destPath);
                returnStatus = false;
            }
        }
        
        if ( group != null ) {
            logOnly(prolix4, "  Group:       " + group);

            if ( chmodFile.setGroup(group) != 0 ) {
                logError(188, "Failed to set group ( " + group + " ) of " + destPath);
                returnStatus = false;
            }
        }

        return returnStatus;
    }

    protected void deleteJarEntryFile()
    {
        enforceFileAbsence(nextChildPath);

        // Ignore the return code; there is no more processing
        // on this entry.
    }

    // PQ59675: Added code to pass in 'preserveAttributes'.
    // PQ59675: 'preserveAttributes' is usually true --
    // PQ59675: it will only be false when chmodding afterwards
    // PQ59675  Added use of UnixFile and Windows File
    // D65216   Restore  file Date/Time form jar Entry

    protected void extractJarEntryFile(boolean deleteBeforeWrite,
                                       boolean preserveAttributes)
    {
        logOnly(prolix4, "Extracting file: " + nextChildPath);

        ISystemFile workFile = localSystem.newFile(nextChildPath);

        String permissions;
        String owner;
        String group;

        long lastModified = 0;
        if ( workFile.getFile().exists() ) {
           lastModified = workFile.getFile().lastModified();
        }


        if ( preserveAttributes ) {
            logOnly(prolix4, "Preserve attributes is active");

            if ( workFile.getFile().exists() ) {
                permissions = workFile.getPermissions();
                owner = workFile.getOwner();
                group = workFile.getGroup();

                logOnly(prolix4, "  Permissions: " + permissions);
                logOnly(prolix4, "  Owner:       " + owner);
                logOnly(prolix4, "  Group:       " + group);

            } else {
                logOnly(prolix4, "  Cannot preserve attributes: file does not yet exist.");

                preserveAttributes = false;

                permissions = null;
                owner = null;
                group = null;
            }
            
        } else {
            logOnly(prolix4, "Preserve attributes is inactive");

            permissions = null;
            owner = null;
            group = null;
        }
        
        String absPath = workFile.getFile().getAbsolutePath();
        int lastSlash = absPath.lastIndexOf(slash);
        String pathOnly = absPath.substring(0, lastSlash);

        if ( !ensureDirectory(pathOnly) ) {
            logError(17, "Unable to create directory for extraction: " + nextChildPath);
            return;
        }

        if ( deleteBeforeWrite ) {
            logOnly(prolix4, "Deleting file before extraction: " + nextChildPath);

            if ( !enforceFileAbsence(nextChildPath) ) {
                logError(93, "Unable to delete file before extraction: " + nextChildPath);
                return;

            } else {
                logOnly(prolix4, "Deleted file before extraction: " + nextChildPath);
            }
        }

        // A note about the prior target: JAR file processing doesn't allow
        // backing up in the JAR, so instead of backing up and extracting,
        // the prior extracted file is copied over instead.

        boolean useSeedPrior = getSeedPrior();
        String usePriorTarget = getPriorTarget();
        boolean usePriorTargetError = getPriorTargetError();

        long result;

        if ( usePriorTargetError ) {
            logError(192, "Prior extraction failed and cannot be reused: " +
                          priorTarget + " ==> " + nextChildPath);
            result = -1;

        } else if ( priorTarget != null ) {
            try {
                result = binaryTransfer(priorTarget, nextChildPath, false);
                // throws FileNotFoundException, IOException
            } catch ( FileNotFoundException e ) {
                result = -1;
            } catch ( IOException e ) {
                result = -1;
            }

            if ( result != -1 ) {
                logOnly(prolix4, "Reused file for extraction: " +
                                 priorTarget + " ==> " + nextChildPath);
            } else {
                logError(191, "Reuse of file for extraction failed: " +
                              priorTarget + " ==> " + nextChildPath);
            }
                
        } else {
            result = transferOut(deltaJarInput, nextChildPath);

            if ( result != -1 ) {
                logOnly(prolix4, "Extracted file: " + nextJarEntryName + " ==> " + nextChildPath);

                if ( useSeedPrior )
                    setPriorTarget(nextChildPath);

            } else {
                logError(18, "Extraction failed: " + nextJarEntryName + " ==> " + nextChildPath);

                if ( useSeedPrior ) {
                    setPriorTarget(nextChildPath);
                    setPriorTargetError();
                }
            }
        }

        if ( result != -1 ) {
           // Set lastModified Date to that of the JarEntry date/time - D65216
           File nexTChildFile = new File( nextChildPath );
           nexTChildFile.setLastModified(  nextJarEntry.getTime() );

           if ( preserveAttributes )
              updatePermissions(nextChildPath, permissions, owner, group); // Toss the result
        }
    }

    // Jar file processing:
    //
    // Test for the old jar:
    //     If the jar does not exist
    //         Create the jar, including parent directory structure
    //
    // Open the old jar:
    //     Load the old main attributes;
    //     Build a table of the old entry attributes;
    //
    // While attributes are available in the main delta jar:
    //    Fetch the next attributes;
    //    if 'main' attributes:
    //        if <delete>, clear the old main atributes;
    //        otherwise, replace the old main attributes;
    //    If <delete>, clear the matching old entry attributes;
    //    Otherwise, replace/add the matching old entry attributes;
    //
    // Build a new manifest with the updated main attributes and entry attributes;
    //
    // Calculate a name for a temp jar file;
    // Create the temp jar file using the new attributes;
    //
    // Categories of entries:
    //    Delete
    //    ==> Remember; skip
    //
    //    Update (Contents, Comment, Extra)
    //     ==> Transfer updated entry
    //
    //    Update (Contents, X Comment, Extra)
    //    ==> Transfer updated entry
    //    ==> Store Incomplete Entry, needing the comment
    //
    //    Update (Contents, Comment, X Extra)
    //    ==> Transfer updated entry
    //    ==> Store Incomplete Entry, needing extra data
    //
    //    Update (Contents, X Comment, X Extra)
    //    ==> Transfer updated entry
    //    ==> Store incomplete entry, needing comment and extra data
    //
    //    Update (X Contents, Comment, Extra)
    //    ==> Store unused entry, with comment, extra data, and time
    //
    //    Update (X Contents, X Comment, Extra)
    //    ==> Store unused entry, with extra data and time
    //
    //    Update (X Contents, Comment, X Extra)
    //    ==> Store unused entry, with comment and time
    //
    //    Update (X Contents, X Comment, X Extra)
    //           Should not occur -- warning -- ignore
    //
    //    While child file entries are available in the main delta jar:
    //      If the next delta entry is marked for deletion, remember, then skip it.
    //      If the next delta entry has contents, transfer it, including any
    //        extra fields.  If the entry did not completely replace the
    //        extra fields, add an incomplete entry indicating which
    //        extra fields must be retrieved from the original entry.
    //      If the next delta entry has no contents, store an unsed entry
    //        with the extra fields.
    //      Otherwise, skip the entry
    //
    //    While there are more entries in the old jar:
    //      If the next old entry is remembers as deleted, skip it.
    //      If the next old entry is rememberd as updated,
    //        If the old entry has unused extra data, transfer
    //          the old entry but use the unused extra data.
    //      If the next old entry is remember as updated,
    //        If the next old entry has an incomplete entry
    //          If there is non-null data in the old entry,
    //            put that data into the incomplete entry.
    //          If there is no non-null data in the old entry,
    //            remove the incomplete entry.
    //      Otherwise, transfer the next old entry, including
    //        any extra values.
    //
    //    If there are remaining incomplete entries:
    //
    //    Create a new temporary jar, transferring the manifest
    //    intact from the initial temporary jar.
    //
    //    While there are more entries in the temporary jar:
    //      If the next temporary entry has an incomplete entry,
    //        transfer the temporary entry with the values from
    //        incomplete entry.
    //      Otherwise, transfer the temporary entry, including
    //        any extra values.
    //
    // The original processing loop:
    //
    // OBSOLETE: While child file entries are available in the main delta jar:
    // OBSOLETE:   If the next delta entry is marked for deletion, remember, then skip it.
    // OBSOLETE:   If the next delta entry is marked for update, remember, then transfer it.
    // OBSOLETE:   Otherwise, skip it.
    // OBSOLETE:
    // OBSOLETE: While child file entries are available in the main delta jar:
    // OBSOLETE:
    // OBSOLETE: While there are more entries in the old jar:
    // OBSOLETE:   If the next old entry is remembered as deleted, skip it.
    // OBSOLETE:   If the next old entry is remembered as updated, skip it.
    // OBSOLETE:   Otherwise, transfer the next old entry.
    //
    // Delete the old jar file;
    // Copy the temp jar file onto the new jar file;
    // Delete the temp jar file.

    // Move the tmp file info outside of the basic apply
    // method as there are too many error cases to cleanly
    // handle the final removal of the temporary files in
    // the apply method.

    protected File tmpJarFile;
    protected String tmpJarName;

    protected File secondTmpJarFile;
    protected String secondTmpJarName;

    // Entry P0

    protected void applyJarEntryJar()
    {
        // When processing a jar file, mark the first entry;
        // if EAR processing is set, the entries may need to
        // be cycled.

        setEntryMark(nextJarEntryName);

        initializeTmpFiles();

        try {
            basicApplyJarEntryJar(); // P0 -> P1
        } finally {
            removeTmpFiles();
        }

        // In case an error has occurred, always consume any
        // left-over child entries.

        consumeChildEntries(); // P1 -> P1
    } // P1

    protected void initializeTmpFiles()
    {
        tmpJarFile = null;
        tmpJarName = null;

        secondTmpJarFile = null;
        secondTmpJarName = null;
    }

    protected void removeTmpFiles()
    {
        // logDebug("Delete temporary jar file.");

        if ( tmpJarFile != null ) {
            if ( !tmpJarFile.delete() )
                logError(35, "Failed to delete temporary jar file: " + tmpJarName);
            else
                logOnly(prolix3, "Deleted temporary jar file: " + tmpJarName);
        }

        if ( secondTmpJarFile != null ) {
            if ( !secondTmpJarFile.delete() )
                logError(153, "Failed to delete second temporary jar file: " + secondTmpJarName);
            else
                logOnly(prolix3, "Deleted second temporary jar file: " + secondTmpJarName);
        }

        // logDebug("Applied special jar handling: " + childJarName);
    }

    // While scanning the delta jar, if an entry is
    // encountered which has no contents, the entry
    // cannot yet be added into the updated jar.  However,
    // the entry must be remembered, as it will not be
    // seen again.
    //
    // Use the unused information when processing
    // the source jar.

    protected static class UnusedJarEntryValues
    {
        String updatedComment;
        boolean removeComment;

        byte[] updatedExtra;
        boolean removeExtra;

        long time;
    };

    // While scanning the delta jar, entries which have
    // contents must be transferred, but may not provide
    // completely new extra values.  The new contents
    // must be transferred, but the extra values from
    // the source will have to be added into the updated
    // jar.  This is accomplished by remember which entries
    // need to have their extra values recovered, and
    // recording the extra values when processing the
    // source jar.  If there turns out to be values
    // to be recovered, the updated jar will be processed
    // an extra time, adding in the recovered values.

    // During the processing of the delta entries, the boolean
    // flags are set.
    //
    // During the processing of the source entries, the
    // original values are set.
    //
    // During the reprocessing of the temporary jar, the
    // original values are consumed.

    protected static class IncompleteJarEntryValues
    {
        boolean needOriginalComment;
        String originalComment;

        boolean needOriginalExtra;
        byte[] originalExtra;
    };

    protected static final String tmpJarPrefix = "EX_W_" ;
    protected static final String jarSuffix = ".jar" ;

    protected String tmpDirFullName = null;

    // Enter P0

    protected void basicApplyJarEntryJar()
    {
        logOnly(prolix3, "Applying jar handling: " + nextChildPath);

        // logDebug("Skipping leading mark entry.");

        // Cycle off the jar marker entry before any possible returns.
        // After returning any unprocessed child entries are skipped.
        // Need to get past the jar marker for this to work.

        // 'nextChildPath' is destroyed when the next entry is
        // fetched, but the current value is needed when to copy
        // the temporary jar file.

        String childJarName = nextChildPath;
        fetchEntry(); // P0 -> P1

        // logDebug("Skipped leading mark entry.");

        // Make sure that the jar directory is available;
        // if it isn't, create it.  The jar directory must
        // be available to continue.  The jar itself doesn't
        // need to be available (although an error will logged
        // in this case).

        // logDebug("Ensure jar directory is available: " + childJarName);

        if ( !ensureJarDirectory(childJarName) ) {
            logError(19, "Unable to ensure jar directory: " + childJarName);
            return; // P1
        }

        // Load and convert the manifest; generate an empty manifest
        // if the jar isn't available.

        // logDebug("Loading manifest: " + childJarName);

        Manifest oldManifest;
        boolean loadedOldJar;

        if ( !testForJar(childJarName) ) {
            logError(20, "Jar file does not exist: " + childJarName);
            log("Generating empty jar manifest.");

            oldManifest = createManifest();
            loadedOldJar = false;

        } else {
            logOnly(prolix3, "Jar file exists: " + childJarName);

            oldManifest = loadManifest(childJarName);

            if ( oldManifest == null ) {
                logError(21, "Unable to load existing jar manifest: " + childJarName);

                // Java compiler dataflow analysis isn't smart enough
                // to allow us to get by without the following assignment.
                loadedOldJar = false;

                return; // P1

            } else {
                // logDebug("Loaded manifest: " + childJarName);

                loadedOldJar = true;
            }
        }

        // Cycle through the entries mapped to attributes, applying
        // them to the manifest information which was loaded above.

        // logDebug("Processing delta manifest entries.");

        Object[] manifestInfo = convertManifest(oldManifest);

        Attributes oldMainAttributes = (Attributes) manifestInfo[manifest_info_main];
        Hashtable oldAttributesTable = (Hashtable) manifestInfo[manifest_info_table];

        while ( (nextChildData != null) && !nextChildData.isFileEntry ) {
            consumeEntry(); // P1 -> P0

            logOnly(prolix4, "Processing attribute entry: " + nextChildPath);

            String childEntryName = nextChildData.entryName;

            if ( !nextEntryAttributesFailed ) {
                if ( nextEntryAttributes != null ) {
                    if ( testKeyForDelete(nextEntryAttributes) ) {

                        if ( nextChildData.isMain() ) {
                            oldMainAttributes = new Attributes();
                            logOnly(prolix4, "Deleted main attributes.");

                        } else {
                            if ( oldAttributesTable.remove(childEntryName) == null )
                                logOnly(prolix4, "No old entry to remove.");
                            else
                                logOnly(prolix4, "Removed old entry.");
                        }

                    } else {
                        if ( nextChildData.isMain() ) {
                            oldMainAttributes = nextEntryAttributes;
                            logOnly(prolix4, "Replaced main attributes.");

                        } else {
                            if ( oldAttributesTable.put(childEntryName, nextEntryAttributes) == null )
                                logOnly(prolix4, "Added attributes.");
                            else
                                logOnly(prolix4, "Replaced attributes.");
                        }
                    }

                } else {
                    logError(23, "Detected no jar entry attributes (maybe entry-length-131 bug): " +
                                 nextChildPath);
                }

            } else {
                // We had an exception retrieving the attributes; ignore this entry
                // and continue.
            }

            fetchEntry(); // P0 -> P1
        }

        // logDebug("Completed processing delta manifest entries.");

        // The manifest is fully prepared; now build the updated jar.
        //
        // Need to first create and open a temporary jar, consuming the
        // updated manifest information in the process.

        // logDebug("Create temporary jar file.");

        Manifest newManifest = buildManifest(oldMainAttributes, oldAttributesTable);

        try {
            tmpJarFile = File.createTempFile(tmpJarPrefix,
                                             jarSuffix,
                                             new File(tmpDirFullName));
            tmpJarName = tmpJarFile.toString();
            logOnly(prolix3, "Selected temporary jar file name: " + tmpJarName);

        } catch ( IOException ex ) {
            logError(24, "IOException selecting temporary jar file name", ex);
            return; // P1
        }

        // logDebug("Created temporary jar file.");

        // logDebug("Opening temporary jar file: " + tmpJarName);

        JarOutputStream tmpJarOutput;

        try {
            FileOutputStream tmpJarStream = new FileOutputStream(tmpJarFile);
            tmpJarOutput = new JarOutputStream(tmpJarStream, newManifest);
            logOnly(prolix3, "Opened temporary jar file: " + tmpJarName);

        } catch ( IOException ex ) {
            logError(25, "IOException opening temporary jar file: " + tmpJarName, ex);
            return; // P1
        }

        // logDebug("Opened temporary jar file: " + tmpJarName);

        // Then process the delta entries.  Only file delta entries should
        // be left for the current jar.
        //
        // From here on, make sure to close the temporary jar output stream.
        //
        // Have to know what to delete before processing the old jar, so
        // the delta jar entries must be processed first.
        //
        // Remember any delta which is a delete or an update -- these are to
        // be skipped when processing the old jar.
        //
        // Note any transfer failure; don't update the old jar if there are
        // any transfer failures.

        // Start with the remaining delta entries in the file.

        // logDebug("Process and remember delta file entries.");

        boolean transferFailed = false;

        Hashtable deletedEntries = new Hashtable();
        Hashtable updatedEntries = new Hashtable();

        Hashtable unusedEntryData = new Hashtable();
        Hashtable incompleteEntryData = new Hashtable();

        while ( (nextChildData != null) && nextChildData.isFileEntry ) {
            consumeEntry(); // P1 -> P0

            String childEntryName = nextChildData.entryName;
            logOnly(prolix4, "Processing next delta entry: " + childEntryName);

            if ( !nextEntryAttributesFailed ) {
                if ( testKeyForDelete(nextEntryAttributes) ) {
                    logOnly(prolix3, "Recording entry as deleted: " + childEntryName);
                    deletedEntries.put(childEntryName, childEntryName);

                } else {
                    logOnly(prolix4, "Transferring entry data: " + childEntryName);

                    long transferBytes =
                        transferUpdatedEntry(deltaJarInput,
                                             tmpJarOutput,
                                             childEntryName,
                                             nextJarEntry,
                                             nextEntryAttributes,
                                             unusedEntryData,
                                             incompleteEntryData);

                    if ( transferBytes == -1 ) {
                        logError(27, "Transfer failed: " + childEntryName);
                        transferFailed = true;

                    } else {
                        logOnly(prolix4, "Recording entry as updated: " + childEntryName);
                        updatedEntries.put(childEntryName, childEntryName);
                    }
                }
            }

            fetchEntry(); // P0 -> P1
        }

        // logDebug("Completed processing of  delta file entries.");

        // Continue with the entries in the old jar.
        //
        // Don't process the old jar if it wasn't available, or if any
        // of the transfers, above, failed.
        //
        // Transfer jar entries out of the old jar file, skipping any
        // that were deleted or updated.

        if ( loadedOldJar && !transferFailed ) {

            // logDebug("Opening old jar: " + childJarName);

            JarInputStream oldJarInput = null;

            try {
                FileInputStream oldJarInputStream = new FileInputStream(childJarName);
                oldJarInput = new JarInputStream(oldJarInputStream, false);

            } catch ( IOException ex ) {
                logError(28, "Failed to open existing jar input stream: " + childJarName, ex);
                transferFailed = true;
            }

            if ( !transferFailed ) {
                logOnly(prolix4, "Old jar is open; processing entries.");

                // Note that the 'try ... catch' block is outside of the
                // while loop: Skip processing if an exception occurs.

                String tmpJarEntryName = null;

                try {
                    JarEntry tmpJarEntry;

                    while ( (tmpJarEntry = oldJarInput.getNextJarEntry()) != null ) {
                        tmpJarEntryName = tmpJarEntry.toString();

                        if ( (deletedEntries.get(tmpJarEntryName) != null) ) {
                            logOnly(prolix4, "   Skipping jar entry marked for deletion: " +
                                    tmpJarEntryName);

                        } else if ( updatedEntries.get(tmpJarEntryName) != null ) {
                            logOnly(prolix4, "   Special processing for jar entry marked for replacement: " +
                                    tmpJarEntryName);

                            // Maybe transfer, with extra values from the update;
                            // Maybe fill in incomplete extra values;
                            // Maybe ignore the source entry.

                            transferSourceEntry(oldJarInput,
                                                tmpJarOutput,
                                                tmpJarEntryName,
                                                tmpJarEntry,
                                                unusedEntryData,
                                                incompleteEntryData);

                            // Toss the count of bytes transferred.

                        } else if ( tmpJarEntryName.equalsIgnoreCase(JarFile.MANIFEST_NAME) ) {
                            logOnly(prolix4, "   Skipping jar entry for manifest: " + tmpJarEntryName);

                        } else {
                            logOnly(prolix4, "   Transferring jar entry: " + tmpJarEntryName);

                            transferJarEntry(oldJarInput,
                                             tmpJarOutput,
                                             tmpJarEntryName,
                                             tmpJarEntry);

                            // Toss the count of bytes transferred.
                        }

                        tmpJarEntryName = null;
                    }

                } catch ( IOException ex ) {
                    if ( tmpJarEntryName == null ) {
                        logError(29, "IOException seeking next entry: " + childJarName, ex);
                    } else {
                        logError(30, "IOException transferring from: " + childJarName +
                                     " to: " + tmpJarEntryName, ex);
                    }

                    transferFailed = true;
                }
            }

            // logDebug("Close out old and temporary jar files.");

            if ( oldJarInput != null ) {
                try {
                    oldJarInput.close();

                } catch ( IOException ex ) {
                    logError(31, "Failed to close existing jar input stream: " + childJarName, ex);
                }

                oldJarInput = null;
            }

        } else {
            // logDebug("Old jar was absent; skipping scan of: " + childJarName);
        }

        if ( tmpJarOutput != null ) {
            try {
                tmpJarOutput.close();

            } catch ( IOException ex ) {
                logError(32, "Failed to close temporary jar: " + tmpJarName, ex);
            }

            tmpJarOutput = null;
        }

        // logDebug("Old and temporary jar files have been closed.");

        if ( transferFailed ) {
            log("Failure during initial transfer: Skipping temporary jar reprocessing");
            return;
        }

        // logDebug("Reprocessing temporary jar file.");

        if ( unusedEntryData.size() != 0 )
            log("Failed to make use of all extra values while updating jar " + childJarName);

        if ( incompleteEntryData.size() != 0 ) {
            logOnly(prolix4, "Applying recovered extra values to jar " + childJarName);

            Manifest tmpJarManifest = loadManifest(tmpJarName);

            if ( tmpJarManifest == null ) {
                logError(154, "Unable to load existing jar manifest: " + tmpJarName);
                return; // P1
            }

            // logDebug("Creating second temporary jar file.");

            try {
                secondTmpJarFile = File.createTempFile(tmpJarPrefix,
                                                       jarSuffix,
                                                       new File(tmpDirFullName));
                secondTmpJarName = tmpJarFile.toString();

                logOnly(prolix3, "Selected second temporary jar file name: " + secondTmpJarName);

            } catch ( IOException ex ) {
                logError(155, "IOException selecting second temporary jar file name", ex);
                return; // P1
            }

            // logDebug("Opening second temporary jar file: " + secondTmpJarName);

            JarOutputStream secondTmpJarOutput;

            try {
                FileOutputStream secondTmpJarStream = new FileOutputStream(secondTmpJarFile);
                secondTmpJarOutput = new JarOutputStream(secondTmpJarStream, tmpJarManifest);
                logOnly(prolix3, "Opened second temporary jar file: " + secondTmpJarName);

            } catch (IOException ex ) {
                logError(156, "IOException opening second temporary jar file: " + secondTmpJarName, ex);
                return; // P1
            }

            boolean tmpTransferFailed = false;

            JarInputStream tmpJarInput = null;

            try {
                FileInputStream tmpJarInputStream = new FileInputStream(tmpJarName);
                tmpJarInput = new JarInputStream(tmpJarInputStream, false);

            } catch ( IOException ex ) {
                logError(157, "Failed to open temporary jar input stream: " + tmpJarName, ex);
                tmpTransferFailed = true;
            }

            if ( !tmpTransferFailed ) {
                logOnly(prolix4, "Temporary jar is open; processing entries.");

                // Note that the 'try ... catch' block is outside of the
                // while loop: Skip processing if an exception occurs.

                String tmpJarEntryName = null;

                try {
                    JarEntry tmpJarEntry;

                    while ( (tmpJarEntry = tmpJarInput.getNextJarEntry()) != null ) {
                        tmpJarEntryName = tmpJarEntry.toString();

                        transferRecoveredValues(tmpJarInput, secondTmpJarOutput,
                                                tmpJarEntryName, tmpJarEntry,
                                                incompleteEntryData);
                        // Toss the count of bytes transferred.
                    }

                } catch ( IOException ex ) {
                    if ( tmpJarEntryName == null ) {
                        logError(158, "IOException seeking next entry: " + tmpJarName, ex);
                    }
                    else {
                        logError(159, "IOException transferring from: " + tmpJarName +
                                      " to: " + tmpJarEntryName, ex);
                    }

                    tmpTransferFailed = true;
                }
            }

            if ( incompleteEntryData.size() != 0 )
                logOnly(prolix1, "Unprocessed recovery requests for jar " + childJarName);

            // logDebug("Close out temporary and second temporary jar files.");

            if ( tmpJarInput != null ) {
                try {
                    tmpJarInput.close();

                } catch ( IOException ex ) {
                    logError(160, "Failed to close temporary jar input stream: " + tmpJarName, ex);
                }

                tmpJarInput = null;
            }

            if ( secondTmpJarOutput != null ) {
                try {
                    secondTmpJarOutput.close();

                } catch ( IOException ex ) {
                    logError(170, "Failed to close second temporary jar: " + secondTmpJarName, ex);
                }

                secondTmpJarOutput = null;
            }

            if ( tmpTransferFailed ) {
                log("Failure during recovery transfer: Skipping final copy of jar: " + childJarName);
                return; // P1
            }
        }

        String finalTmpJarName;

        if ( secondTmpJarName != null )
            finalTmpJarName = secondTmpJarName;
        else
            finalTmpJarName = tmpJarName;

        logOnly(prolix3, "Transferring " + finalTmpJarName + " to " + childJarName);

        try {
            binaryTransfer(finalTmpJarName, childJarName, false, true);
            // ignore the count of bytes transferred

            logOnly(prolix3, "Temporary jar file has been copied.");

        } catch ( FileNotFoundException ex ) {
            logError(33, "File not file transferring temporary jar: " + finalTmpJarName +
                         " to: " + childJarName, ex);

        } catch ( IOException ex ) {
            logError(34, "IOException transferring temporary jar: " + finalTmpJarName +
                         " to: " + childJarName, ex);
        }
    } // P1

    // Enter P1

    protected void consumeChildEntries()
    {
        while ( nextChildData != null ) {
            consumeEntry(); // P1 -> P0
            logOnly(prolix4, "Skipping child jar entry: " + nextChildPath);

            fetchEntry(); // P0 -> P1
        }
    } // P1

    // Jar helper methods ...

    protected static final int JAR_STREAM_OFFSET = 0 ;
    // Jar helper methods ...

    protected static final int JAR_ENTRY_OFFSET = 1 ;

    protected Object[] cycleJarInput(JarInputStream jarStream, String jarName,
                                     String targetEntryName)
    {
        boolean didClose = closeJarStream(jarStream, jarName);
        if ( !didClose ) {
            logError(192, "Failed to close delta jar for recycling: " + jarName);
            return null;
        }

        jarStream = openJarInput(jarName);
        if ( jarStream == null ) {
            logError(193, "Failed to open delta jar for recycling: " + jarName);
            return null;
        }

        JarEntry nextEntry = null;

        try {
            while ( (nextEntry == null) &&
                    ((nextEntry = jarStream.getNextJarEntry()) != null)) { // throws IOException

                String scanEntryName = nextEntry.toString();

                if ( !scanEntryName.equals(targetEntryName) ) {
                    logOnly(prolix4, "Skipping entry: " + scanEntryName);
                    nextEntry = null;
                } else {
                    logOnly(prolix4, "Recycled");
                }
            }

        } catch ( IOException e ) {
            logError(194, "Exception recycling: " + jarName +
                          " to entry: " + targetEntryName, e);
            nextEntry = null;

        } finally {
            if ( nextEntry == null )
                closeJarStream(jarStream, jarName);
        }

        if ( nextEntry == null)
            return null;
        else
            return new Object[] { jarStream, nextEntry };
    }

    protected static final int manifest_info_main = 0 ;
    protected static final int manifest_info_table = 1 ;

    protected Object[] convertManifest(Manifest manifest)
    {
        // logDebug("Converting manifest.");

        Attributes mainAttributes = manifest.getMainAttributes();
        Hashtable attributesTable = new Hashtable();

        Map entryMap = manifest.getEntries();
        Set entryKeySet = entryMap.keySet();
        Iterator entryKeys = entryKeySet.iterator();

        while ( entryKeys.hasNext() ) {
            String entryKey = (String) entryKeys.next();
            Attributes entryAttributes = manifest.getAttributes(entryKey);

            attributesTable.put(entryKey, entryAttributes);
        }

        return new Object[] { mainAttributes, attributesTable};
    }

    protected Manifest loadManifest(String jarName)
    {
        JarFile jar;

        try {
            jar = new JarFile(jarName, false);

        } catch ( IOException ex ) {
            logError(36, "IOException opening jar: " + jarName, ex);
            return null;
        }

        Manifest manifest;

        try {
            manifest = jar.getManifest();

            if ( manifest == null ) // no manifest; create a new empty manifest
                manifest = createManifest();

        } catch ( IOException ex ) {
            logError(37, "Failed to read manifest of jar: " + jarName, ex);
            manifest = null;
        }

        try {
            jar.close();

        } catch ( IOException ex ) {
            logError(38, "Failed to close jar: " + jarName, ex);
            manifest = null;
        }

        jar = null;

        return manifest;
    }

    protected static final String manifestVersion = "1.0";

    protected Manifest createManifest()
    {
        Manifest manifest = new Manifest();

        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.putValue(Attributes.Name.MANIFEST_VERSION.toString(), manifestVersion);

        return manifest;
    }

    protected Attributes createMainAttributes()
    {
        Attributes mainAttributes = new Attributes();
        mainAttributes.putValue(Attributes.Name.MANIFEST_VERSION.toString(), manifestVersion);

        return mainAttributes;
    }

    protected JarInputStream openJarInput(String jarName)
    {
        JarInputStream jarInput;

        try {
            FileInputStream jarStream = new FileInputStream(URLDecoder.decode(jarName));
            //String filename = jarName;
            //try { filename = URLDecoder.decode( jarName, "UTF-8"); } catch ( Exception e) {}
            //FileInputStream jarStream = new FileInputStream( filename );

            jarInput = new JarInputStream(jarStream, false);

        } catch ( IOException ex ) {
            logError(82, "IOException opening jar file: " + jarName, ex);
            jarInput = null;
        }

        return jarInput;
    }

    protected boolean closeJarStream(JarInputStream jarStream, String jarName)
    {
        boolean result;

        try {
            jarStream.close();
            result = true;

        } catch ( IOException ex ) {
            logError(86, "IOException closing jar input stream: " + jarName, ex);
            result = false;
        }

        jarStream = null;

        return result;
    }

    protected JarOutputStream openJarOutput(String jarName, Manifest manifest)
    {
        JarOutputStream jarOutput;

        try {
            FileOutputStream jarStream = new FileOutputStream(jarName);
            jarOutput = new JarOutputStream(jarStream, manifest);
            jarOutput.setLevel(po.getInt(k_Compression));

        } catch ( IOException ex ) {
            logError(10, "IOException opening jar output stream: " + jarName, ex);
            jarOutput = null;
        }

        return jarOutput;
    }

    protected boolean closeJarStream(JarOutputStream jarStream, String jarName)
    {
        if ( jarStream == null )
            logDebug("Error: jar stream is null!");

        boolean result;

        try {
            jarStream.close();
            result = true;

        } catch ( IOException ex ) {
            logError(83, "IOException closing jar output stream: " + jarName, ex);
            result = false;
        }

        jarStream = null;

        return result;
    }

    protected boolean createJar(File fileForJar)
    {
        JarOutputStream jarStream;

        try {
            FileOutputStream streamForJar = new FileOutputStream(fileForJar);
            jarStream = new JarOutputStream(streamForJar);

        } catch ( IOException ex ) {
            logError(41, "IOException create jar: " + fileForJar, ex);
            return false;
        }

        boolean result;

        try {
            jarStream.close();
            result = true;

        } catch ( IOException ex ) {
            logError(42, "IOException closing new jar: " + fileForJar, ex);
            result = false;
        }

        jarStream = null;

        return true;
    }

    protected boolean testForJar(String jarName)
    {
        File fileForJar = new File(jarName);

        if ( !fileForJar.exists() ) {
            // logDebug("Jar file does not exist: " + jarName);
            return false;

        } else {
            // logDebug("Jar file already exists: " + jarName);
            return true;
        }
    }

    protected boolean ensureJarDirectory(String jarName)
    {
        String[] jarPathInfo = new String[helper.k_elCount];

        if ( !helper.ParseFileSpec(jarName, jarPathInfo, false) ) {
            logError(72, "Bad jar file spec: " + jarName);
            return false;
        }

        String jarDir = jarPathInfo[helper.k_drive] + jarPathInfo[helper.k_path];

        if ( !ensureDirectory(jarDir) ) {
            logError(40, "Unable to create directory for jar: " + jarName);
            return false;
        }

        return true;
    }

    // Read the bytes of the jar entry.  Up to MAX_READABLE
    // bytes may be read.  Answer the array of read bytes.
    // Answer null if the jar entry cannot be read.

    protected static long MAX_READABLE = 20 * 1024 * 1024;

    // 'MAX_READABLE' could theoretically be as large
    // as Integer.MAX_VALUE (2147483647), but the size has been
    // deliberatly limited to 20M.

    protected byte[] readJarEntry(String jarName, String entryName)
    {
        logOnly(prolix5, "Reading jar entry " + entryDescriptor(jarName, entryName));

        JarFile jarFile = null;

        try {
            jarFile = new JarFile(jarName); // throws IOException

        } catch ( IOException ex ) {
            logError(171, "Unable to open [" + jarName + "]", ex);
            return null;
        }

        byte[] contents = null;

        try {
            contents = readJarEntry(jarName, jarFile, entryName);

        } finally {
            try {
                jarFile.close(); // throws IOException

            } catch ( IOException ex ) {
                logError(172, "Unable to close [" + jarName + "]", ex);
            }
        }

        return contents;
    }

    protected static final long MAX_READ_DURATION = 5 * 60 * 1000; // 5 minutes

    protected byte[] readJarEntry(String jarName, JarFile jarFile, String entryName)
    {
        String descriptor = entryDescriptor(jarName, entryName);

        logOnly(prolix3, "Reading bytes for jar entry " + descriptor);

        ZipEntry zipEntry = jarFile.getEntry(entryName);

        if ( zipEntry == null ) {
            logError(173, "Failed to find entry " + descriptor);
            return null;
        }

        long entrySize = zipEntry.getSize();

        if ( entrySize == -1 ) {
            logError(174, "Unable to determine size of entry " + descriptor);
            return null;

        } else if ( entrySize > MAX_READABLE ) {
            logError(175, "Attempting to read " + entrySize +
                          " bytes but the limit is " + MAX_READABLE + " bytes.");
            log(" Reading " + descriptor);
            return null;
        }

        int useEntrySize = (int) entrySize;

        logOnly(prolix3, "Entry is listed as having: " + useEntrySize + " bytes.");

        InputStream inputStream = null;

        try {
            inputStream = jarFile.getInputStream(zipEntry); // throws IOException

        } catch ( IOException ex ) {
            logError(176, "Cannot open entry " + descriptor, ex);
            return null;
        }

        byte[] inputBuffer = null;

        try {
            inputBuffer = new byte[useEntrySize];

            boolean ranOutOfTime = false;

            long startingTime =
                System.currentTimeMillis(),
                usedTime = 0;

            IOException boundException = null;

            int totalRead = 0,
            remaining = useEntrySize,
            nextRead = 0;

            while ( !ranOutOfTime &&
                    (boundException == null) &&
                    (nextRead != -1) &&
                    (remaining > 0) ) {

                usedTime = System.currentTimeMillis() - startingTime;

                if ( usedTime > MAX_READ_DURATION ) {
                    ranOutOfTime = true;

                } else {
                    try {
                        nextRead = (int) inputStream.read(inputBuffer, totalRead, remaining); // throws IOException

                        logOnly(prolix5, "Read " + nextRead + " bytes.");

                        if ( nextRead != -1 ) {
                            totalRead += nextRead;
                            remaining -= nextRead;
                        }

                    } catch ( IOException ex ) {
                        boundException = ex;
                    }
                }
            }

            if ( boundException != null ) {
                inputBuffer = null;
                logError(163, "Error reading bytes for entry " + descriptor, boundException);

            } else if ( ranOutOfTime ) {
                inputBuffer = null;
                logError(167, "Ran out of time reading " + descriptor + ": Used " + usedTime +
                              " milliseconds but only " + MAX_READ_DURATION + " are allowed.");

            } else if ( nextRead == -1 ) {
                inputBuffer = null;
                logError(168, "Reached end of entry " + descriptor +
                              " before all " + entrySize +
                              " bytes were read.  Only " + totalRead +
                              " bytes were read.");

            } else if (totalRead < entrySize) {
                inputBuffer = null;
                logError(169, "Could not read all bytes for entry " + descriptor +
                              " expecting " + entrySize + " but read " + totalRead + ".");
            }

        } finally {
            try {
                inputStream.close(); // throws IOException

            } catch ( IOException ex ) {
                inputBuffer = null;
                logError(177, "Cannot close entry " + descriptor, ex);
            }
        }

        return inputBuffer;
    }

    // Helper for logging and error handling.

    protected String entryDescriptor(String jarName, String entryName)
    {
        return "[ " + jarName + " : " + entryName + " ]";
    }

    // Attribute entry translation ...

    protected boolean testKeyForHelperClass(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meHelperClass, HelperList.meTrue) );
    }

    protected boolean testKeyForJar(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meJar, HelperList.meTrue) );
    }

    protected boolean testKeyForNoRestore(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meNoRestore, HelperList.meTrue) );
    }

    protected static boolean isAIX = OSUtil.isAIX();
     
    protected boolean testKeyForDeleteBeforeWrite(Attributes attributes, String entryName)
    {                      
        if (testKey(attributes, HelperList.meDeleteBeforeWrite, HelperList.meTrue) )
            return true;
        
        if (!isAIX)
            return false;
        
        entryName = entryName.toLowerCase();
        return (entryName.endsWith(".so") ||
                entryName.endsWith(".a")  ||
                entryName.endsWith(".lil")); // Special case for WEMPS
    }

    protected boolean testKeyForDeleteBeforeWrite(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meDeleteBeforeWrite, HelperList.meTrue) );
    }

    protected boolean testKeyForPerInstance(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.mePerInstance, HelperList.meTrue) );
    }

    protected boolean testKeyForAsInstallable(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meAsInstallable, HelperList.meTrue) );
    }

    protected boolean testKeyForAsApplication(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meAsApplication, HelperList.meTrue) );
    }

    protected boolean testKeyForAsInstalled(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meAsInstalled, HelperList.meTrue) );
    }

    protected boolean testKeyForAsMetadata(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meAsMetadata, HelperList.meTrue) );
    }

    protected String testKeyForNameRule(Attributes attributes)
    {
        return ( (String) attributes.getValue(HelperList.meNameRule) );
    }

    protected boolean testKeyForDelete(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meDelete, HelperList.meTrue) );
    }

    protected boolean testKeyForNoDelete(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meNoDelete, HelperList.meTrue) );
    }

    protected boolean testKeyForAbsolutePath(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meAbsolutePath, HelperList.meTrue) );
    }

    protected boolean testKeyForIgnoreZipContent(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meIgnoreZipContent, HelperList.meTrue) );
    }

    protected boolean testKeyForRemoveZipComment(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meRemoveZipComment, HelperList.meTrue) );
    }

    protected boolean testKeyForRemoveZipExtra(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meRemoveZipExtra, HelperList.meTrue) );
    }

    protected boolean testKeyForAddFile(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meAddFile, HelperList.meTrue) );
    }

    protected boolean testKeyForReplaceFile(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meReplaceFile, HelperList.meTrue) );
    }

    protected boolean testKeyForRestoreOnly(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meRestoreOnly, HelperList.meTrue) );
    }

    // TFB: Added testKeyForChmodOnly and testKeyForChmod
    // KH: Added 'HelperList.meChmodOnly' to HelperList class

    protected boolean testKeyForChmodOnly(Attributes attributes)
    {
        return ( testKey(attributes, HelperList.meChmodOnly, HelperList.meTrue) );
    }

   protected boolean testKeyForChmod(Attributes attributes)
    {
        String chmodValue = retrieveKey(attributes, HelperList.meChmod);

        return ( chmodValue != null );
    }

    protected String getRequiredVersion(Attributes attributes)
    {
        String key = HelperList.meRequiredVersion;

        if ( attributes == null ) {
            logDebug("Attributes are null for " + key);
            return null;
        }

        String retrievedValue = (String) attributes.getValue(key);

        if ( retrievedValue == null )
            logDebug("Key not found: " + key);
        else
            logDebug("Retrieved " + key + " value: " + retrievedValue);

        return(retrievedValue);
    }

    // TFB: START

    // TFB: Split out 'retrieveKey' to allow sharing of debugging statements
    // TFB: of this operation.

    protected boolean testKey(Attributes attributes, String key, String value)
    {
        String retrievedValue = retrieveKey(attributes, key);

        return ( (retrievedValue != null) && retrievedValue.equals(value) );
    }

    protected String retrieveKey(Attributes attributes, String key)
    {
        if ( attributes == null ) {
            logDebug("Attributes are null for " + key);
            return null;
        }

        String retrievedValue = (String) attributes.getValue(key);

        if ( retrievedValue == null )
            logDebug("Key not found: " + key);
        else
            logDebug("Retrieved " + key + " value: " + retrievedValue);

        return retrievedValue;
    }

    // TFB: END

    // Jar helpers ...

    // The table is an hashtable mapping String to Attributes.

    protected Manifest buildManifest(Attributes setMainAttributes,
                                     Hashtable setTable)
    {
        Manifest manifest = new Manifest();

        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.putAll(setMainAttributes);

        Map entryMap = manifest.getEntries();

        Enumeration setKeysList = setTable.keys();

        while ( setKeysList.hasMoreElements() ) {
            String setKey = (String) setKeysList.nextElement();
            Attributes setAttributes = (Attributes) setTable.get(setKey);

            entryMap.put(setKey, setAttributes);
        }

        return manifest;
    }

    protected static final int jarBufferSize = 10 * 2048;

    protected byte[] IOBuf = new byte[jarBufferSize];

    // Update the transfer code to handle extra values.
    // There are four cases:
    //
    //   'unusedEntryData' and 'incompleteExtra' are null.
    //   ==> Simply transfer the entry and its extra values; no special processing
    //
    //   'unusedEntryData' is null and 'incompleteExtra' is not null.
    //   ==> Transfer the entry and its extra values, but values from
    //
    //   'incompleteExtra' take precedence over the source values.
    //   'unusedEntryData' and 'incompleteExtra' are not null;
    //
    //     'fromUpdateJar' is true
    //     ==> Special processing
    //
    //     'fromUpdateJar' is false
    //     ==> Special processing

    // Merged transfer method ... this method has been split
    // to reduce coupling (and method complexity!)

    // Simplest transfer method.  Simply copy the entry along with
    // its extra values.

    protected long transferJarEntry(InputStream jis,
                                    JarOutputStream jos,
                                    String entryName,
                                    JarEntry sourceEntry)
    {
        JarEntry transferEntry = new JarEntry(entryName);

        String sourceComment = sourceEntry.getComment();
        if (sourceComment != null)
            transferEntry.setComment(sourceComment);

        byte[] sourceExtra = sourceEntry.getExtra();
        if ( sourceExtra != null )
            transferEntry.setExtra(sourceExtra);

        long sourceTime = sourceEntry.getTime();
        transferEntry.setTime(sourceTime);

        return transferContents(jis, jos, entryName, transferEntry);
    }

    // Transfer an updated entry.  There are two cases:
    //   The entry has contents
    //   The entry has no contents
    //     (the entry only extra value updates)

    protected long transferUpdatedEntry(InputStream jis,
                                        JarOutputStream jos,
                                        String entryName,
                                        JarEntry sourceEntry,
                                        Attributes sourceAttributes,
                                        Hashtable unusedEntryData,
                                        Hashtable incompleteEntryData)
    {
        if ( testKeyForIgnoreZipContent(sourceAttributes) ) {
            transferUpdatedExtra(entryName,
                                 sourceEntry,
                                 sourceAttributes,
                                 unusedEntryData);
            return 0;

        } else {
            return transferUpdatedContents(jis, jos,
                                           entryName,
                                           sourceEntry,
                                           sourceAttributes,
                                           incompleteEntryData);
        }
    }

    // Don't transfer an entry -- create and store an unused
    // entry.  Will have to wait until the original jar is
    // being processed to update the extra values.

    protected void transferUpdatedExtra(String entryName,
                                        JarEntry sourceEntry,
                                        Attributes sourceAttributes,
                                        Hashtable unusedEntryData)
    {
        UnusedJarEntryValues unusedValues = new UnusedJarEntryValues();

        boolean sawUnusedValue = false;

        String sourceComment = sourceEntry.getComment();

        if ( sourceComment != null ) {
            logOnly(prolix5, "Recording unused comment for entry " + entryName +
                    "[" + sourceComment + "]");
            unusedValues.updatedComment = sourceComment;
            sawUnusedValue = true;

        } else if ( testKeyForRemoveZipComment(sourceAttributes) ) {
            logOnly(prolix5, "Recording unused comment removal for entry " + entryName);
            unusedValues.removeComment = true;
            sawUnusedValue = true;
        }

        byte[] sourceExtra = sourceEntry.getExtra();

        if ( sourceExtra != null ) {
            logOnly(prolix5, "Recording unused extra data for entry " + entryName +
                    sourceExtra.length + " bytes");
            unusedValues.updatedExtra = sourceExtra;
            sawUnusedValue = true;

        } else if ( testKeyForRemoveZipExtra(sourceAttributes) ) {
            logOnly(prolix5, "Recording unused extra data removal for entry " + entryName);
            unusedValues.removeExtra = true;
            sawUnusedValue = true;
        }

        long sourceTime = sourceEntry.getTime();
        unusedValues.time = sourceTime;

        if ( sawUnusedValue ) {
            unusedEntryData.put(entryName, unusedValues);

        } else {
            logError(125, "entry " + entryName +
                          " requesting extra value update, but supplied no extra values");
        }
    }

    // Transfer the entry, but check for extra values which should be
    // preserved.  These will be noted when processing the original
    // jar, and if any are noted, a second pass will be made across
    // the updated jar and the original values will be added in.

    protected long transferUpdatedContents(InputStream jis,
                                           JarOutputStream jos,
                                           String entryName,
                                           JarEntry sourceEntry,
                                           Attributes sourceAttributes,
                                           Hashtable incompleteEntryData)
    {
        JarEntry transferEntry = new JarEntry(entryName);

        IncompleteJarEntryValues incompleteValues = null;

        String sourceComment = sourceEntry.getComment();

        if ( sourceComment != null ) {
            transferEntry.setComment(sourceComment);

        } else if ( testKeyForRemoveZipComment(sourceAttributes) ) {
            // Don't have to set the comment -- it starts out null.

        } else {
            logOnly(prolix5, "Recording need to recover comment of " + entryName);

            incompleteValues = new IncompleteJarEntryValues();
            incompleteValues.needOriginalComment = true;
        }

        byte[] sourceExtra = sourceEntry.getExtra();

        if ( sourceExtra != null ) {
            transferEntry.setExtra(sourceExtra);

        } else if ( testKeyForRemoveZipExtra(sourceAttributes) ) {
            // Don't have to set the extra data -- it starts out null.

        } else {
            logOnly(prolix5, "Recording need to recover extra data of " + entryName);

            if ( incompleteValues == null )
                incompleteValues = new IncompleteJarEntryValues();

            incompleteValues.needOriginalExtra = true;
        }

        long sourceTime = sourceEntry.getTime();
        transferEntry.setTime(sourceTime);

        if ( incompleteValues != null )
            incompleteEntryData.put(entryName, incompleteValues);

        return transferContents(jis, jos, entryName, transferEntry);
    }

    // Processing the original jar; note any incomplete or unused entry
    // data for special processing.

    protected long transferSourceEntry(InputStream jis,
                                       JarOutputStream jos,
                                       String entryName,
                                       JarEntry sourceEntry,
                                       Hashtable unusedEntryData,
                                       Hashtable incompleteEntryData)
    {
        UnusedJarEntryValues unusedValues =
            (UnusedJarEntryValues) unusedEntryData.get(entryName);

        if ( unusedValues != null ) {
            unusedEntryData.remove(entryName);

            return transferAndPatchEntry(jis,
                                         jos,
                                         entryName,
                                         sourceEntry,
                                         unusedValues);
        }

        IncompleteJarEntryValues incompleteValues =
            (IncompleteJarEntryValues) incompleteEntryData.get(entryName);

        if ( incompleteValues != null ) {
            // Don't transfer an entry -- one has already been placed
            // with the updated contents.

            boolean isReallyIncomplete =
            completeEntryValues(entryName, sourceEntry, incompleteValues);

            if ( !isReallyIncomplete ) {
                logOnly(prolix5, "Found no original data to recover for " + entryName);
                incompleteEntryData.remove(entryName);
            }

            return 0;
        }

        return 0;
    }

    protected long transferAndPatchEntry(InputStream jis,
                                         JarOutputStream jos,
                                         String entryName,
                                         JarEntry sourceEntry,
                                         UnusedJarEntryValues patchValues)
    {
        JarEntry transferEntry = new JarEntry(entryName);

        if ( patchValues.updatedComment != null ) {
            logOnly(prolix5, "Consuming unused comment for " + entryName +
                             " [" + patchValues.updatedComment + "]");

            transferEntry.setComment(patchValues.updatedComment);

        } else if ( patchValues.removeComment ) {
            logOnly(prolix5, "Consuming removal of comment for " + entryName);
            // Don't have to set the comment -- it starts out null.

        } else {
            String sourceComment = sourceEntry.getComment();
            if ( sourceComment != null )
                transferEntry.setComment(sourceComment);
        }

        if ( patchValues.updatedExtra != null ) {
            logOnly(prolix5, "Consumed unused extra data for " + entryName +
                             " [" + patchValues.updatedExtra.length + "]");

            transferEntry.setExtra(patchValues.updatedExtra);

        } else if ( patchValues.removeExtra ) {
            logOnly(prolix5, "Consuming removal of extra data for " + entryName);
            // Don't have to set the extra data -- it starts out null.

        } else {
            byte[] sourceExtra = sourceEntry.getExtra();
            if ( sourceExtra != null )
                transferEntry.setExtra(sourceExtra);
        }

        transferEntry.setTime(patchValues.time);

        return transferContents(jis, jos, entryName, transferEntry);
    }

    protected boolean completeEntryValues(String entryName,
                                          JarEntry sourceEntry,
                                          IncompleteJarEntryValues incompleteValues)
    {
        boolean isReallyIncomplete = false;

        if ( incompleteValues.needOriginalComment ) {
            String sourceComment = sourceEntry.getComment();

            if ( sourceComment != null ) {
                logOnly(prolix5, "Recovering original comment for " + entryName +
                                 " as [" + sourceComment + "]");

                incompleteValues.originalComment = sourceComment;
                isReallyIncomplete = true;

            } else {
                // There is no original comment to recover.

                // Make sure to avoid using the null original comment
                // in the incomplete values.

                incompleteValues.needOriginalComment = false;
            }
        }

        if ( incompleteValues.needOriginalExtra ) {
            byte[] sourceExtra = sourceEntry.getExtra();

            if ( sourceExtra != null ) {
                logOnly(prolix5, "Recovering original extra data for " + entryName +
                                 " with " + sourceExtra.length + " bytes");

                incompleteValues.originalExtra = sourceExtra;
                isReallyIncomplete = true;

            } else {
                // There is no original extra data to recover.

                // Make sure to avoid using the null original extra data
                // in the incomplete values.

                incompleteValues.needOriginalExtra = false;
            }
        }

        return isReallyIncomplete;
    }

    // A transfer between the temporary jars, to put
    // back in recovered extra values.  The contents
    // are always transferred, but transfer a mix of
    // extra values.

    protected long transferRecoveredValues(InputStream jis,
                                           JarOutputStream jos,
                                           String entryName,
                                           JarEntry sourceEntry,
                                           Hashtable completedEntryData)
    {
        JarEntry transferEntry = new JarEntry(entryName);

        IncompleteJarEntryValues completedValues =
            (IncompleteJarEntryValues) completedEntryData.get(entryName);

        if (completedValues != null)
            completedEntryData.remove(entryName);

        if ( (completedValues != null) && (completedValues.needOriginalComment) ) {
            logOnly(prolix4, "Transferring recovered comment for " + entryName);

            transferEntry.setComment(completedValues.originalComment);

        } else {
            String sourceComment = sourceEntry.getComment();
            if ( sourceComment != null )
                transferEntry.setComment(sourceComment);
        }

        if ((completedValues != null) && (completedValues.needOriginalExtra)) {
            logOnly(prolix4, "Transferring recovered extra data for " + entryName);

            transferEntry.setExtra(completedValues.originalExtra);

        } else {
            byte[] sourceExtra = sourceEntry.getExtra();

            if ( sourceExtra != null )
                transferEntry.setExtra(sourceExtra);
        }

        long sourceTime = sourceEntry.getTime();
        transferEntry.setTime(sourceTime);

        return transferContents(jis, jos, entryName, transferEntry);
    }

    protected long transferContents(InputStream jis,
                                    JarOutputStream jos,
                                    String entryName,
                                    JarEntry transferEntry)
    {
        try {
            jos.putNextEntry(transferEntry); // kick off the new entry

        } catch ( IOException ex ) {
            logError(43, "IOException setting new jar entry: " + entryName, ex);
            return -1;
        }

        long totRead = 0;

        try {
            int bytesRead = jarBufferSize;

            while ( bytesRead > 0 ) {
                bytesRead = jis.read(IOBuf, 0, jarBufferSize);

                if ( bytesRead > 0 ) {
                    totRead += bytesRead;
                    jos.write(IOBuf, 0, bytesRead);
                }
            }

        } catch ( IOException ex ) {
            logError(44, "IOException transferring jar entry: " + entryName, ex);
            return -1;
        }

        logOnly(prolix4, "Transferred " + helper.FmtNum(totRead,0,7,0) +
                         " bytes of jar entry: " + entryName);

        return totRead;
    }

    protected boolean transferDummyByte(JarOutputStream jos)
    {
        try {
            jos.write(IOBuf, 0, 1);
            return true;

        } catch ( IOException ex ) {
            logError(70, "Failed to record dummy byte", ex);
            return false;
        }
    }

    protected long transferIn(JarOutputStream jos, String sourcePath)
    {
        // logDebug("Jarring file: " + sourcePath);

        BufferedInputStream sourceStream;

        try {

           //System.out.println( this.getClass().getName() + "::transferIn : " + ResolveMacro(sourcePath) );

            FileInputStream inputStream = new FileInputStream( ResolveMacro(sourcePath) );

            sourceStream = new BufferedInputStream(inputStream, jarBufferSize);

        } catch ( IOException ex ) {
            ex.printStackTrace();
            if ( sourcePath.endsWith("properties/java.policy") ) {
                logOnly(prolix2, "Note: special handling for " + sourcePath +
                                 ", not found, bypassed.");  // Defect 117019

            } else {
                logError(45, "Unable to open source file: " + sourcePath, ex);
            }

            return -1;
        }

        long totRead = 0;
        long result;

        try {
            int bytesRead = jarBufferSize;

            while ( bytesRead > 0 ) {
                bytesRead = sourceStream.read(IOBuf, 0, jarBufferSize);

                if ( bytesRead > 0 ) {
                    totRead += bytesRead;
                    jos.write(IOBuf, 0, bytesRead);
                }
            }

        } catch ( IOException ex ) {
            logError(46, "IOException jarring into: " + sourcePath, ex);
            result = -1;

        } finally {
            try {
                sourceStream.close();
                result = totRead;

            } catch ( IOException ex ) {
                logError(47, "IOException closing source: " + sourcePath, ex);
                result = -1;
            }

            sourceStream = null;
        }

        // logDebug("Jarred " + totRead + " bytes from: " + sourcePath);

        return result;
    }

    protected long transferOut(JarInputStream jis, String destPath)
    {
        BufferedOutputStream destStream;

        try {
            FileOutputStream outputStream = new FileOutputStream(destPath);
            destStream = new BufferedOutputStream(outputStream, jarBufferSize);

        } catch ( IOException ex ) {
            logError(79, "Unable to open destination file: " + destPath, ex);
            return -1;
        }

        long totRead = 0;
        long result;

        try {
            int bytesRead = jarBufferSize;

            while ( bytesRead > 0 ) {
                bytesRead = jis.read(IOBuf, 0, jarBufferSize);

                if ( bytesRead > 0 ) {
                    totRead += bytesRead;
                    destStream.write(IOBuf, 0, bytesRead);
                }
            }

        } catch ( IOException ex ) {
            logError(80, "IOException unjarring onto: " + destPath, ex);
            result = -1;

        } finally {
            try {
                destStream.close();
                result = totRead;

            } catch ( IOException ex ) {
                logError(81, "IOException closing destination: " + destPath, ex);
                result = -1;
            }

            destStream = null;
        }

        logOnly(prolix3, "Unjarred " + helper.FmtNum(totRead,0,7,0) + " bytes onto: " + destPath);
        return result;
    }

    // Input helper ...

    // File system helpers ...

    protected boolean enforceFileAbsence(String fileName)
    {
        logOnly(prolix4, "Deleting file: " + fileName);

        File workFile = new File(fileName);

        if ( !workFile.exists() ) {
            logOnly(prolix3, "File already does not exist: " + fileName);
            return true;
        }

        if ( workFile.isDirectory() ) {
            logError(15, "File is a directory; skipping: " + fileName);
            return false;
        }

        if ( workFile.delete() ) {
            logOnly(prolix3, "Deleted file: " + fileName);
            return true;
        }
        else {
            logError(16, "File could not be deleted: " + fileName);
            return false;
        }
    }

    protected long binaryTransfer(String sourceFile,
                                  String destFileName,
                                  boolean append,
                                  boolean preserveAttributes)
        throws FileNotFoundException, IOException
    {
        String permissions;
        String owner;
        String group;

        // When appending, the attributes are not changed;
        // there is no need to restore them.

        if ( append || !preserveAttributes ) {
            permissions = null;
            owner = null;
            group = null;

        } else {
            //PQ59675 get permissions

            ISystemFile destFile = localSystem.newFile(destFileName);

            if ( destFile.getFile().exists() ){
                permissions = destFile.getPermissions();
                owner = destFile.getOwner();
                group = destFile.getGroup();

            } else {
               permissions = null;
               owner = null;
               group = null;
            }

            //PQ59675 end get permissions
        }

        long bytesTransferred = binaryTransfer(sourceFile, destFileName, append);
        // throws FileNotFoundException, IOException

        ISystemFile newFile = localSystem.newFile(destFileName);

        //PQ59675 restore permissions after binary transfer
        
        if ( (permissions != null) && newFile.getFile().exists() ) {
            if (newFile.setPermissions(permissions) != 0)
                logError(313, "binaryTransfer:cannot set perms (" + permissions + ") for " + destFileName);

            if ( owner != null ) {
                if (newFile.setOwner(owner) != 0)
                    logError(314, "binaryTransfer:cannot set owner (" + owner + ") for " + nextChildPath);
            }

            if ( group != null ) {
                if ( newFile.setGroup(group) != 0 )
                    logError(315, "binaryTransfer:cannot set group (" + group + ") for " + nextChildPath);
            }
        }

        //PQ59675 end restore permissions after binary transfer

        return bytesTransferred;
    }

    // Perform a binary transfer of a file, possibly,
    // appending onto the destination file.

    protected static final int transferBufferSize = 10 * 2048;
    protected final byte[] transferBuf = new byte[transferBufferSize];

    protected long binaryTransfer(String sourceFile,
                                  String destFile, boolean append)
        throws FileNotFoundException, IOException
    {
        int numRead = 0;
        long totRead = 0;

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            input = new BufferedInputStream(inputStream, transferBufferSize);

            FileOutputStream outputStream = new FileOutputStream(destFile, append);
            output = new BufferedOutputStream(outputStream, transferBufferSize);

            while ( (numRead = input.read(transferBuf)) != -1 ) {
                totRead += numRead;
                output.write(transferBuf, 0, numRead);
            }

        } finally {
            if ( input != null ) {
                try {
                    input.close();

                } catch ( IOException ex ) {
                    logError(49, "Error closing input transfer file: " + sourceFile, ex);
                }

                input = null;
            }

            if ( output != null ) {
                try {
                    output.close();

                } catch ( IOException ex ) {
                    logError(92, "Error closing output transfer file: " + destFile, ex);
                }

                output = null;
            }
        }

        logOnly(prolix3, "Transferred " + helper.FmtNum(totRead, 0, 7, 0) +
                         " bytes from: " + sourceFile +
                         (append ? ", appending to: " : ", replacing: " + destFile));

        return totRead;
    }

    public static final boolean DO_APPEND = true ;
    public static final boolean DO_NOT_APPEND = false ;

    protected boolean findReplace()
    {
    	 for ( int fileNum = 0; fileNum < ufFileVector.size(); fileNum++ ) {
             FUEntry fue = (FUEntry) ufFileVector.elementAt(fileNum);
             if(!findReplace(fue)){
             	return false;
             }
         }
         
         return true;
    }

    protected boolean findReplace(FUEntry fue)
    {
        logOnly(prolix3, "SR-Updating [ " + fue.fileName + " ]");

        if ( fue.components.length > 0 ) {
            if ( !ensureComponentsPresent(fue.components) ) {
                logOnly(prolix3, "  Skipping update; the file's updates match no active component.");
                return true;
            }
        }

        for ( int srNum = 0; srNum < fue.frList.size(); srNum++ ) {
            FRList frl = (FRList) fue.frList.elementAt(srNum);

            String initialFind = frl.find;
            frl.find = ResolveMacro(frl.find);

            logOnly(prolix4, "  Performing replacement on find text:");
            logOnly(prolix4, "    Initial Find Text: [ " + initialFind + " ]");
            logOnly(prolix4, "    Final Find Text:   [ " + frl.find + " ]");
        }

        String useFileName = targetDirFullName + fue.fileName;
        logOnly(prolix3, "  Unresolved file name: [ " + useFileName + " ]");

        useFileName = ResolveMacro(useFileName);
        logOnly(prolix3, "  Resolved file name:   [ " + useFileName + " ]");

        if ( !fileExists(useFileName) ) {
            logOnly(prolix3, "File not found; skipping.");
            return true;
        }

        Vector errorMsgs = new Vector();
        Vector fileLines = helper.file2Vector(errorMsgs, useFileName);

        if ( fileLines == null ) {
            logOnly(prolix1, "Error reading file lines:");

            for ( int msgNo = 0; msgNo < errorMsgs.size(); msgNo++ )
                logError(99, (String) errorMsgs.elementAt(msgNo));

            return false;
        }

        boolean fileModified = false;

        int numLines        = fileLines.size();
        int numReplacements = fue.frList.size();

        for ( int lineNum = 0; lineNum < numLines; lineNum++ ) {
            String workLine = (String) fileLines.elementAt(lineNum);

            for ( int srNum = 0; srNum < numReplacements; srNum++ ) {
                FRList frl = (FRList) fue.frList.elementAt(srNum);

                int frlOffset = workLine.indexOf(frl.find);
                if ( frlOffset == -1 )
                    continue; // Try another replacement

                fileModified = true;
                frl.count++;

                if ( !specialReplaceProcessing(fileLines, frl, lineNum) ) {
                    logOnly(prolix4, "  Initial Line:      [ " + workLine + " ]");

                    logOnly(prolix4, "  Found Replacement: [ " + frl.find + " ]");
                    logOnly(prolix4, "  At Line/Column:    [ " + lineNum + " / " + frlOffset + " ]");

                    String leadingText  = workLine.substring(0, frlOffset);
                    String trailingText = workLine.substring(frlOffset + frl.find.length());

                    logOnly(prolix4, "  Leading Fragment:  [ " + leadingText + " ]");
                    logOnly(prolix4, "  Trailing Fragment: [ " + trailingText + " ]");

                    frl.replace = ResolveMacro(frl.replace);

                    logOnly(prolix4, "  Replacement Text:  [ " + frl.replace + " ]");

                    workLine = leadingText + frl.replace + trailingText;

                    logOnly(prolix4, "  Final Line:        [ " + workLine + " ]");

                    fileLines.setElementAt(workLine, lineNum);

                } else {
                    logOnly(prolix4, "  Triggered *<> style replacement.");
                }
            }
        }

        if ( !fileModified ) {
            logOnly(prolix3, "  File was not modified.");
            return true;
        }

        logOnly(prolix3, "Noted modification.");

        ISystemFile useFile = localSystem.newFile(useFileName);

        String permissions = useFile.getPermissions();
        String owner       = useFile.getOwner();
        String group       = useFile.getGroup();

        boolean rc = helper.vector2File(errorMsgs, fileLines, useFileName, DO_NOT_APPEND);

        if ( !rc ) {
            logError(89, " writing " + useFileName);

            for ( int msgNo = 0; msgNo < errorMsgs.size(); msgNo++ )
                logError(105, (String) errorMsgs.elementAt(msgNo));

            return false;
        }

        logOnly(prolix3, "  File was modified and was rewritten.");

        if ( useFile.setPermissions(permissions) != 0 ) {
            logError(309, "findAndReplace: Failed to set perms (" + permissions + ")" +
                          " for " + useFileName);
			return false;
        }

        if ( useFile.setOwner(owner) != 0 ) {
            logError(310, "findAndReplace: Failed to set owner (" + owner + ")" +
                          " for " + useFileName);
			return false;
        }

        if ( useFile.setGroup(group) != 0 ) {
            logError(311, "findAndReplace: Failed to set group (" + group + ")" +
                          " for " + useFileName);
			return false;
        }

        for ( int srNum = 0; srNum < fue.frList.size(); srNum++ ) {
            FRList frl = (FRList) fue.frList.elementAt(srNum);

            logOnly(prolix4, "Find text [ " + frl.find + " ]" +
                             " was replaced [ " + frl.count + " ] times.");
        }
		return true;
    }

    protected boolean ensureComponentsPresent(String[] requiredComponents)
    {
        boolean result = false;

        for ( int i = 0; !result && (i < requiredComponents.length); i++ ) {
            result = installedComponents.containsKey(requiredComponents[i]);
        }

        return result;
    }

    protected boolean specialReplaceProcessing(Vector lines, FRList frl, int lineNum)
    {
        if ( !frl.replace.startsWith("*") )
            return false;   // not a special replace string

        if ( frl.replace.startsWith("*<InsertPostJava>") ) {
            insertPostJava(lines, frl, lineNum);
            return true;
        }

        if ( frl.replace.startsWith("*<reorderClassPath>") ) {
            reorderClassPath(lines, frl, lineNum);
            return true;
        }

        return false;
    }

    protected boolean fileExists(String filePath)
    {
        logOnly(prolix4, "File exists test: [ " + filePath + " ].");

        File useFile = new File(filePath);

        if ( !useFile.exists() ) {
            logOnly(prolix4, "Failing file exists test: file does not exist.");
            return false;
        }

        if ( useFile.isDirectory() ) {
            logOnly(prolix4, "Failing file exists test: file is a directory.");
            return false;
        }

        logOnly(prolix4, "Passed file exists test.");
        return true;
    }

    protected void insertPostJava(Vector lines, FRList frl, int lineNum)
    {
        logOnly(prolix3, "   InsertPostJava processing");

        // Sample Line
        // %JAVA_HOME%\bin\java
        //     -Xminf0.15 -Xmaxf0.25
        //     -classpath %WAS_CP% %CLIENTSAS%
        //     -Dcom.ibm.CORBA.principalName=%COMPUTERNAME%/AdminClient
        //     -Dserver.root=%WAS_HOME%
        //     com.ibm.ejs.sm.client.ui.EJSConsole %DEST% %DESTPORT% %DEBUGOPTS% %QUALIFYNAMES%

        // First make sure that the replacement variable is not already present.

        String line = (String) lines.elementAt(lineNum);
        String replaceValue = frl.replace.substring(17);  // remove the *<InsertPostJava> part

        int pos = line.indexOf(replaceValue);
        if ( pos > -1 ) {
            logOnly(prolix3, "   Value " + replaceValue + " is already present, update ignored.");
            return;
        }

        // If the replacement starts with -Xms, then ensure we do not already have an -Xms.

        if ( replaceValue.startsWith("-Xms") ) {
            if ( (line.indexOf("-Xms")) != -1 ) {
                logOnly(prolix3, "   A confilcting -Xms specification is present, update ignored.");
                return;
            }
        }

        // Now locate the end of the first  /java

        String lcLine = line.toLowerCase();

        int targetPos = -1;
        pos = lcLine.indexOf("java ");  // look for java blank
        if ( pos > -1 ) {
            targetPos = pos + 4;          // this is where we want to insert the new stuff
        } else {
            pos = lcLine.indexOf("javaw ");  // now look for javaw
            if ( pos > -1 )
                targetPos = pos + 5;           // this is where we want to insert the new stuff
        }

        if ( targetPos == -1 ) {
            logOnly(prolix3, "   Did not find \"java \" or \"javaw \", update ignored.");

        } else {
            String part1, part2;

            if ( targetPos == 0 )
                part1 = "";
            else
                part1 = line.substring(0, targetPos);

            part2 = line.substring(targetPos + 1);
            line = part1 + " " + replaceValue + " " + part2;

            String oldEle = (String) lines.set(lineNum, line);
            logOnly(prolix3, "   line " + lineNum + " updated with " + replaceValue);
        }

        return;
    }

    protected void reorderClassPath(Vector lines, FRList frl, int lineNum)
    {
        logError(1, " The Reorder_Class_Path function is still under construction.");
    }

     protected boolean processPropertyFiles()
    {
        for ( int propertyFileNo = 0; propertyFileNo < pfFileUpdate.size(); propertyFileNo++ ) {
            logOnly(prolix3, "Processing property file update # " + propertyFileNo);

            PFUpdates updateInfo = (PFUpdates) pfFileUpdate.elementAt(propertyFileNo);

            // Need to ResolveMacros first.
            logOnly(prolix3, "Processing property file [ " + updateInfo.propertyFileName + " ]");
            String propertyFileName = ResolveMacro(updateInfo.propertyFileName);
            // If name begins w/ a macro its will be alsolute, otherwise its relative to targetDir
            if ( !updateInfo.propertyFileName.startsWith( "$<" ) ) {
               propertyFileName = (new File( targetDirFullName, propertyFileName )).getAbsolutePath();
            }
            logOnly(prolix3, "Property file resolved to [ " + propertyFileName + " ]");

            if ( UpdatePropertyFile(propertyFileName, updateInfo.puList) )
                logOnly(prolix3, "The property file update successful");
            else
                logOnly(prolix3, "The property file update failed");
        }
		        return true;
    }

    // The toDoList Vector contains PUEvent objects.

    protected boolean UpdatePropertyFile(String fileName, Vector toDoList )
    {
        // PQ59675 Get permissions

        ISystemFile file = localSystem.newFile(fileName);

        if ( !file.getFile().canWrite() ) {
            logError(94, "Failing property file update: Property file [ " + fileName + " ] is not writable.");
            return false;
        }

        String permissions = file.getPermissions();
        String owner = file.getOwner();
        String group = file.getGroup();

        // PQ59675 end get permissions

        // Use a CommentedProperties object so that the ordering and comments in the file are preserved.
        CommentedProperties props = readPropertiesFile(fileName);

        if ( props == null ) {
            logError(162, "Failing property file update: Unable to read property file [ " + fileName + " ]");
            return false;
        }

        for ( int updateNumber = 0; updateNumber < toDoList.size(); updateNumber++ ) {
            PUEvent nextUpdateEvent = (PUEvent) toDoList.elementAt(updateNumber);
            applyUpdateEvent(updateNumber, nextUpdateEvent, props);
        }

        if ( !writePropertiesFile(fileName, props) ) {
            logError(164, "Failing property file update: Unable to write property file [ " + fileName + " ]");
            return false;

        } else {
            // PQ59675 Write permissions to local system

            if ( file.setPermissions(permissions) != 0 )
                logError(310, "UpdatePropertyFile:cannot set perms (" + permissions + ") for " + fileName);

            if ( file.setOwner(owner) != 0 )
                logError(311, "UpdatePropertyFile:cannot set owner (" + owner + ") for " + nextChildPath);

            if (file.setGroup(group) != 0)
                logError(312, "UpdatePropertyFile:cannot set group (" + group + ") for " + nextChildPath);

            // PQ59675 End write permissions to local system
        }

        return true;
    }

    protected CommentedProperties readPropertiesFile(String fileName)
    {
        File file = new File(fileName);

        InputStream inputFile;

        try {
            inputFile = new FileInputStream(file);

        } catch ( FileNotFoundException ex ) {
            inputFile = null;   // this is a new File
        }

        CommentedProperties props = new CommentedProperties();

        if ( inputFile != null ) {
            try {
                props.load(inputFile);

            } catch ( IOException ex ) {
                logError(95, "Failed to load properties from [ " + fileName + " ]", ex);
                return null;

            } finally {
                try {
                    inputFile.close();

                } catch (IOException ex) {
                    logError(96, "Failed to close properties file [ " + fileName + " ]", ex);
                    return null;
                }
            }
        }

        return props;
    }

    protected boolean writePropertiesFile(String fileName, CommentedProperties props)
    {
        File file = new File(fileName);

        FileOutputStream fos;

        try {
            fos = new FileOutputStream(file);

        } catch ( IOException ex ) {
            logError(103, "Failed to open property file for output [ " + fileName + " ]", ex);
            return false;
        }

        try {
            //props.store(fos, "PTF Installer");
            props.store(fos);  // No Header, Since maintaining comments and such, we don't need a header

        } catch ( IOException ex ) {
            logError(104, "Failed to write property file [ " + fileName + " ]", ex);
            return false;

        } finally {
            try {
                fos.close();

            } catch ( IOException ex ) {
                logError(111, "Failed to close property file [ " + fileName + " ]", ex);
                return false;
            }
        }

        return true;
    }

    protected void applyUpdateEvent(int eventNo, PUEvent event, CommentedProperties props)
    {
        String propertyKey = event.key;

        int function = event.function;
        String functionName = puFunctionNames[function];

        String rawValue = event.value;

        String newValue;

        if ( function != puInError ) {
            if ( rawValue != null )
                newValue = ResolveMacro(rawValue);
            else
                newValue = null;

        } else {
            newValue = null;
        }

        String oldValue = props.getProperty(propertyKey);

        logOnly(prolix3, "Property Update # " + eventNo);
        logOnly(prolix3, "  Function: " + function + "  : " + functionName);
        logOnly(prolix3, "  Property Name: " + propertyKey);
        logOnly(prolix3, "  Initial Value: " + oldValue);

        if ( (rawValue != null) && !rawValue.equals(newValue) ) {
            logOnly(prolix3, "  Final Value (Raw)     : " + rawValue);
            logOnly(prolix3, "  Final Value (Resolved): " + newValue);

        } else {
            logOnly(prolix3, "  Final Value  : " + newValue);
        }

        if ( function == puInError ) {
            logError(97, "Invalid function in property update request");
            logError(98, "Illegal function [ " + newValue + " ] specified for key [ " + propertyKey + " ]");

        } else if ( (function == puSoftUpdate) || (function == puUpdate) ) {
            if ( oldValue == null ) {
                if ( function == puSoftUpdate ) {
                    logOnly(prolix3, "Update finds no initial property.  Adding new property.");
                } else {
                    logError(90, "Error during the update of [ " + propertyKey +
                                 " ] in property file [ " + productFileName +
                                 " ]: The target property was not found.  Adding a new property.");
                }
            }

            props.setProperty(propertyKey, newValue);

        } else if ( (function == puSoftAdd) || (function == puAdd) ) {
            if ( oldValue != null ) {
                if ( function == puSoftAdd ) {
                    logOnly(prolix3, "Add finds an initial property.  Updating that property.");
                } else {
                    logError(100, "Error during the addition of [ " + propertyKey +
                                  " ] to property file [ " + productFileName +
                                  " ]: An initial property was found.  Updating that property.");
                }
            }

            props.setProperty(propertyKey, newValue);

        } else if ( (function == puSoftDelete) || (function == puDelete) ) {
            if ( oldValue == null ) {
                if ( function == puSoftDelete ) {
                    logOnly(prolix3, "Delete finds no initial property.  Ignoring delete event.");
                } else {
                    logError(102, "Error during the removal of [ " + propertyKey +
                                  " ] from property file [ " + productFileName +
                                  " ]: No initial property was found.  Ignoring delete event.");
                }

            } else {
                props.removeProperty(propertyKey);
            }
        }
    }

    protected boolean processReSequenceJars()
    {
        if ( reSequenceJar.size() == 0 ) {
            log("No Re-Sequencing of jar files was noted.");
            return true;
        } else {
            log("Re-sequencing of jar files was noted.");
        }

        JarFile deltaJarFile;

        try {
            deltaJarFile = new JarFile(decodedJarName);
        } catch ( IOException ex ) {
            logError(107, "Failed to open delta jar (to load resequencing data): " + decodedJarName, ex);
            return false;
        }

               boolean rcReSeq = true;
        try {
        	rcReSeq = performReSequencing(deltaJarFile);

        } finally {
            try {
                deltaJarFile.close();

            } catch ( IOException ex ) {
                logError(183, "Failed to close delta jar" +
                              " (opened to load resequencing data): " + decodedJarName,
                         ex);
                return false;
            }
        }
        return rcReSeq;
    }

        protected boolean performReSequencing(JarFile deltaJarFile)
    {
        Vector messages = new Vector();
        ReSequenceJar resequencer = new ReSequenceJar(messages);

        int compression = po.getInt(k_Compression);

        Enumeration resequenceJarNames = reSequenceJar.keys();
        while ( resequenceJarNames.hasMoreElements() ) {
            String relativeJarName = (String) resequenceJarNames.nextElement();
            String fullJarName = targetDirFullName + relativeJarName;

            log("Re-sequencing jar file: " + relativeJarName);
            log("Re-sequencing will full path: " + fullJarName);

            String resequenceListEntryName = (String) reSequenceJar.get(relativeJarName);
            ZipEntry resequenceListEntry = deltaJarFile.getEntry(resequenceListEntryName);

            if ( resequenceListEntry == null ) {
                logError(109, "No re-sequence list is stored under entry: " + resequenceListEntryName);
                 return false;
            }

            InputStream resequenceListStream;

            try {
                resequenceListStream = deltaJarFile.getInputStream(resequenceListEntry);

            } catch ( IOException ex ) {
                logError(110, "Unable to obtain inputStream jar entry " + resequenceListEntry.toString(),
                         ex);
                return false;
            }

            // PQ59675 Save permissions from local system
            // Nab these before doing the resequencing proper.

            ISystemFile resequenceFile = localSystem.newFile(fullJarName);

            String
                permissions = resequenceFile.getPermissions(),
                owner       = resequenceFile.getOwner(),
                group       = resequenceFile.getGroup();

            // PQ59675 End permissions from local system

            try {
                if ( resequencer.reSequence(fullJarName, resequenceListStream, compression) ) {
                    log("   Re-sequencing was successful. ");
                } else {
                    logError(108, "Re-sequencing failed: " + fullJarName);
                    for ( int msgNo = 0; msgNo < messages.size(); msgNo++ )
                        log( (String) messages.elementAt(msgNo));
					return false;
                }

            } finally {
                messages.clear();

                // PQ59675 restore permissions after resequence
                // Don't know how far the re-sequencing got; be pessemistic,
                // and assume that the permissions were changed.

                updatePermissions(fullJarName, permissions, owner, group); // Toss the result

                // PQ59675 end restore permissions after resequence

                try {
                    resequenceListStream.close();

                } catch ( IOException ex ) {
                    logError(184, "Failed to close resequence list stream: " + resequenceListEntryName, ex);
					return false;
                }
            }
        }
		return true;
    }

    // Defect 161114: Used to be static, but
    // that keeps 'macroSetupProps' from being loaded
    // in Extractor instances after one loads the
    // setup properties.  The guard variable remains
    // set, while the properties are cleared for the
    // new instance.

    protected boolean readMacroSetupProps = false;
    protected Properties macroSetupProps = null;

    // Cache these -- don't want too many reads during macro
    // processing.
    //
    // But, read the properties at least once to get the
    // latest possible values.

    /**
	 * @return  the macroSetupProps
	 * @uml.property  name="macroSetupProps"
	 */
    protected Properties getMacroSetupProps()
    {
        if ( !readMacroSetupProps ) {
            readMacroSetupProps = true;
            //macroSetupProps = getSetUpCmd();  // We wil get the WPConfig values instead.
            macroSetupProps = getPortalSetUpConfig();
        }

        return macroSetupProps;
    }

    protected String getMacroSetupProp(String propName)
    {
        Properties props = getMacroSetupProps();

        String propValue;

        if ( props != null ) {
            propValue = props.getProperty(propName);

            if ( propValue == null ) {
                propValue = propName;

                logError(132, "No " + propName + " value in setupCmdLine");
                logOnly(prolix3, "Using property name " + propName + " as the macro value.");

                Enumeration eNum = props.keys();
                while ( eNum.hasMoreElements() ) {
                    String key   = (String) eNum.nextElement();
                    String value = (String) props.get(key);
                }

            } else {
            	// do not show the password
            	if(propName.equalsIgnoreCase("was_password"))
            	{
            		logOnly(prolix3, propName + "=(" + PasswordRemover.PWD_REMOVED + ")");
            	}
            	else
            		logOnly(prolix3, propName + "=(" + propValue + ")");
            }

        } else {
            propValue = propName;

            logError(133, "Cannot resolve " + propName + ": no setupCmdLine properties");
            logOnly(prolix3, "Using property name " + propName + " as the macro value.");
        }

        return propValue;
    }


    final String[] knownMacros = {  // you may add to, but do not rearrange the order
        "unused",
        "target",            // replace with the fullTargetName
        "target_fs",         // replace with the fulltargetName and all backslashes xlated to foward slashes
        "was_home",          // replace with WAS_HOME from setupCmdLine
        "was_home_fs",       // replace with WAS_HOME from setupCmdLine and all backslashes xlated to foward slashes
        "java_home",         // replace with JAVA_HOME from setupCmdLine
        "java_home_fs",      // replace with JAVA_HOME from setupCmdLine and all backslashes xlated to foward slashes
        "jdbc_driver",       // replace with DBDRIVER_JARS from setupCmdLine
        "jdbc_driver_fs",    // replace with DBDRIVER_JARS from setupCmdLine and all backslashes xlated to foward slashes
        "dbdriver_path",     // replace with DBDRIVER_PATH from setupCmdLine
        "dbdriver_path_fs",  // replace with DBDRIVER_PATH from setupCmdLine and all backslashes xlated to foward slashes
        "wp_home",           // replace with WP_HOME from setupCmdLine
        "wp_home_fs",        // replace with WP_HOME from setupCmdLine and all backslashes xlated to foward slashes
        "was_cell_name",     // <d67184>
        "was_node_name",     // <d67184>
        "was_server_name",   // <d67184>
        "iseries_profile_name",
        "lc_home",
        "activities_profile_name",
        "blogs_profile_name",
        "communities_profile_name",
        "dogear_profile_name",
        "profiles_profile_name",
        "homepage_profile_name",
        "wikis_profile_name",
        "files_profile_name",
        "search_profile_name",
        "mobile_profile_name",
        "news_profile_name",
        "forum_profile_name",
        "was_password",
        "was_username",
        "activitiesWasAdminUser",
        "activitiesWasPassword",
        "blogsWasAdminUser",
        "blogsWasPassword",
        "communitiesWasAdminUser",
        "communitiesWasPassword",
        "dogearWasAdminUser",
        "dogearWasPassword",
        "profilesWasAdminUser",
        "profilesWasPassword",
        "homepageWasAdminUser",
        "homepageWasPassword",
        "wikisWasAdminUser",
        "wikisWasPassword",
        "filesWasAdminUser",
        "filesWasPassword",
        "newsWasAdminUser",
        "newsWasPassword",
        "searchWasAdminUser",
        "searchWasPassword",
        "mobileWasAdminUser",
        "mobileWasPassword",
        "forumWasAdminUser",
        "forumWasPassword",
        "moderationWasAdminUser",
        "moderationWasPassword",
        "metricsWasAdminUser",
        "metricsWasPassword",
        "ccmWasAdminUser",
        "ccmWasPassword"
    };
    final String ASINSTALLED_MACRO_PREFIX = "asInstalled_";

    public String ResolveMacro(String line)
    {
        // the string $<> denotes a macro. where we will replace the string
        // with either an known string or from a real environment variable


        Properties props = null;
        String newValue = null;

        int startPos = line.indexOf("$<");

        while ( startPos > -1 ) {
            int endPos = line.indexOf(">", startPos );

            if ( endPos > -1 ) {
                String macroName = line.substring(startPos + 2, endPos);
                String matchingMacro = null;
                //2.0 chage
                newValue = LCUtil.resolveMarcro(macroName);
                // search for knownMacros first
                for ( int km = 0; (newValue==null) && (matchingMacro == null) && (km < knownMacros.length); km++ ) {
                    String nextMacro = knownMacros[km];

                    if ( macroName.equals(nextMacro) ) {
                        matchingMacro = macroName;

                        newValue = getMacroSetupProp(macroName);

                        if ( macroName.endsWith("_fs") )
                            newValue = asEntry(newValue);
                    }
                }

                // Its not a knownMacro, see if its an environment variable.

                if ( newValue == null )
                    newValue = System.getProperty(macroName);

                if ( macroName.startsWith( ASINSTALLED_MACRO_PREFIX ) ) {
                   // This is an Ear file Macro.  So we need to resolve to the 
                   //  temp location for the ear.
                   String macroEarName = macroName.substring( ASINSTALLED_MACRO_PREFIX.length() );
                   String tempEarLoc = "";
//                   if ( HANDLING_WAS_DM ) {
                      tempEarLoc = earTmpFullName + slash + "dmwork" + slash + WPConfig.getProperty( WPConstants.PROP_WAS_NODE ) + slash + "installable" +  slash + macroEarName;
/*
                   } else {
                      tempEarLoc = WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) + "/" +
                                   "installedApps"                                   + "/" +
                                   WPConfig.getProperty( WPConstants.PROP_WAS_CELL ) + "/" +
                                   macroEarName;
                   }
*/
                   // force the name to an entry name syntax
                   newValue = asEntry(tempEarLoc);
                }

                // If we have a new value replace it with the macro value.

                if ( newValue != null ) {
                    line =  line.substring(0, startPos) + newValue + line.substring(endPos + 1);
                    newValue = null;
                }
            }

            if ( endPos > 0 )
                startPos = line.indexOf("$<", startPos+1);
            else
                startPos = -2;  // to show we are done
        }

        return line;
    }

    protected String getKeyBoard(String[] text)
    {
        if ( text == null )
            return null;

        int textCount = text.length;
        String keyBoardData;

        InputStreamReader inputReader = new InputStreamReader(System.in);
        BufferedReader keyboard = new BufferedReader(inputReader);

        System.out.println("\07\00\00\00\00\00\00\00\00\00\00\00\00\00\00\00\00\07");

        for ( int qNo = 0; qNo < textCount; qNo++ )
            System.out.println(text[qNo]);

        try {
            keyBoardData = keyboard.readLine();

        } catch ( IOException ex ) {
            logError(48, "Keyboard IOException", ex);
            keyBoardData = null;
        }

        return keyBoardData;
    }

    // Virtual Script Examples:
    //
    // EntryScript1         == Expand EAR Files
    // EntryScript1Cmd1     == EARPreActor=0Class
    // EntryScript1Cmd1Arg1 == myEar.EAR
    // EntryScript1Cmd2     == EARPreActor=0Class
    // EntryScript1Cmd2Arg1 == myUtil.EAR
    //
    // EntryScript1           == Expand EAR Files
    // EntryScript1UnCmd1     == EARPreActor=0Class
    // EntryScript1UnCmd1Arg1 == myEar.EAR
    // EntryScript1UnCmd2     == EARPreActor=0Class
    // EntryScript1UnCmd2Arg1 == myUtil.EAR
    //
    // PreScript1             == Directory Script
    // PreScript1Cmd1         == cd installableApps=0
    // PreScript1Cmd2         == dir=0
    //
    // PostScript1            == Collapse EAR Files
    // PostScript1Cmd1        == EARPostActor=0Class
    // PostScript1Cmd1Arg1    == myEar.EAR
    // PostScript1Cmd2        == EARPostActor=0Class
    // PostScript1Cmd2Arg1    == myUtil.EAR
    //
    // PostScript1            == Collapse EAR Files
    // PostScript1UnCmd1      == EARPostActor=0Class
    // PostScript1UnCmd1Arg1  == myEar.EAR
    // PostScript1UnCmd2      == EARPostActor=0Class
    // PostScript1UnCmd2Arg1  == myUtil.EAR

        protected boolean processVirtualScripts(String scriptType)
    {
        if ( mainAttribs == null )  // if we don't have main attribs something went terribly wrong
            return true;

        boolean moreScripts = true;
        for ( int scriptIndex = 0; moreScripts; scriptIndex++ ){
        	String scriptId = scriptType + scriptIndex; // For example, PreScript0
            String scriptName = mainAttribs.getValue(scriptId);
            if ( scriptName == null ){
            	//no such script, no more script
                break;
            }else{            
	        	if (!processVirtualScript(scriptType, scriptIndex)){
	            	return false;
	            }
            }
        }
        
        return true;
    }

    protected boolean processVirtualScript(String scriptType, int scriptIndex)
    {
        String scriptId = scriptType + scriptIndex; // For example, PreScript0
        String scriptName = mainAttribs.getValue(scriptId);

        if ( scriptName == null )
            return false;

        log("Processing virtual script " + scriptName);

        String cmdPrefix = scriptId + HelperList.meCmd;

        boolean moreCommands = true;

        for ( int cmdIndex = 0; moreCommands; cmdIndex++ )
            // If an Entry Script we allow for backup files.
            moreCommands = processCommand(cmdPrefix, cmdIndex, scriptType == HelperList.meEntryScript);

        return true;
    }
    
    protected String componentName =null;
    public void setComponentName(String componentName){
    	this.componentName = componentName;
    }
    public String getComponentName(){
    	return this.componentName;
    }
    public String LOAPAR =null;
    public void setLOAPAR(String apar){
    	this.LOAPAR = apar;
    }
    public String getLOAPAR(){
    	return this.LOAPAR;
    }
    protected boolean processCommand(String cmdPrefix, int cmdIndex, boolean allowBackup)
    {
        String cmdKey = cmdPrefix + cmdIndex;

        String rawCmd = mainAttribs.getValue(cmdKey);
        if ( rawCmd == null )
            return false;

        String cmd = validatePlatform(rawCmd);
        if ( cmd == null )
            return true; // Skip just this one command.

        int equalsIndex = cmd.lastIndexOf("=");
        if ( equalsIndex == -1 ) {
            logError(53, "Missing return code from " + cmd + ", command ignored.");
            return true; // Skip just this one command.
        }

        String stringReturnCode = cmd.substring(equalsIndex + 1).trim();
        boolean requiredCmd = false;
        boolean isClass     = false;

        if ( stringReturnCode.endsWith(HelperList.meRCmd) ) {
            requiredCmd = true;

            int codeLength = stringReturnCode.length() - HelperList.meRCmd.length();
            stringReturnCode = stringReturnCode.substring(0, codeLength);

        } else if ( stringReturnCode.endsWith(HelperList.meClass) ) {
            isClass = true;

            int codeLength = stringReturnCode.length() - HelperList.meClass.length();
            stringReturnCode = stringReturnCode.substring(0, codeLength);
        }

        boolean testReturnCode;
        int expectedReturnCode;

        if ( stringReturnCode.equals("?") ) {
            testReturnCode = false;
            expectedReturnCode = 0;

        } else {
            testReturnCode = true;

            try {
                expectedReturnCode = Integer.parseInt(stringReturnCode);

            } catch ( NumberFormatException ex ) {
                expectedReturnCode = 0;
                logError(54, "RC for " + cmd + " is not numeric, command ignored.");
                return true; // Ignore just this one command.
            }
        }

        cmd = cmd.substring(0, equalsIndex);
        cmd = ResolveMacro(cmd);

        // make sure we don't display any passwords in plain text
        String securedCmd = PasswordRemover.removePassword(cmd, WAS_PASS);
        
        logOnly(prolix3, " Command=" + securedCmd);

        Vector args;

        if ( isClass ) {
            String argPrefix = cmdKey + HelperList.meArg;

            args = new Vector();
            boolean moreArgs = true;

            for ( int argIndex = 0; moreArgs; argIndex++ )
                moreArgs = fetchArgument(argPrefix, argIndex, args);

            if ( args.size() == 0 )
                args = null;

        } else {
            args = null;
        }

        int returnCode;

        if ( isClass ) {
            returnCode = processUserClass(cmd, args, allowBackup);
            logOnly(prolix3, "    RC=" + returnCode);

        } else {
            ExecCmd exec = new ExecCmd(adjust4Platform, ExecCmd.DONT_ECHO_STDOUT);
            Vector results    = new Vector();
            Vector logResults = new Vector();

            // for the -wpspasswords parameters
            String pwdList = WPConfig.getProperty("pwdList");
            if(pwdList != null)
            {
                String props = "";
                //System.out.println(pwdList);
                String[] t = pwdList.split(",");
                for(int i = 0; i< t.length; i++)
                {
                   //System.out.println(t[i]);
                    props += (" -D"+t[i]+"="+WPConfig.getProperty(t[i]));
                }
                cmd += props;
                //System.out.println("calling this: "+cmd);

            }

//            returnCode = exec.Execute(cmd, ExecCmd.DONT_ECHO_STDOUT, false, results, logResults);
            //added by songzj@cn.ibm.com for ouputing process log info in time
            logOnly(prolix3, " Executed EAR and Provision Web Resources Update");
            EFixInstaller efixInstaller = new EFixInstaller(logStream);
            String lc_home = com.ibm.lconn.common.LCUtil.getLCHome();
            String was_username = com.ibm.lconn.update.util.LCUtil.resolveMarcro("was_username");
            String was_password = com.ibm.lconn.update.util.LCUtil.resolveMarcro("was_password");
            logOnly(prolix3, "lc_home=" + lc_home);
            logOnly(prolix3, "was_username=" +was_username);
            logOnly(prolix3, "was_password=" + "XXXXXX");
            
            efixInstaller.setEFixMetaDat(lc_home , this.componentName, this.LOAPAR, was_username, was_password);
            
            logOnly(prolix3, "Begin to update......, please waiting for a while");
            boolean executeResult = false;
            if (cmd !=null && cmd.indexOf("uninstall")>0){

            	executeResult = efixInstaller.doUninstall();
            }else{

            	executeResult = efixInstaller.doInstall();
            }
            if(executeResult){
            	returnCode = 0;
                logOnly(prolix3, "BUILD SUCCESSFULLY");
            }
            else{
            	returnCode = 1;
            	logOnly(prolix3, "BUILD FAILED");
            }
            
            
            //logOnly(prolix3, " Executed Command=" + cmd);

            //returnCode = exec.Execute(cmd, ExecCmd.DONT_ECHO_STDOUT, false, logStream);

//            log(null, logResults, prolix3);
//            log("Result: ", results, prolix3);
        }

        if ( testReturnCode ) {
            if ( returnCode != expectedReturnCode ) {
                logError(112, "Return code (" + returnCode +
                              ") differs from the expected code (" + expectedReturnCode + ").");

                /*
                  TFB: Don't allow this in the new installer.
                if ( requiredCmd ) {
                    logError(130, "Required Command; Exiting");
                    closeLogStream();
                    System.exit(8);
                }
                */
				return false; 
            }

        } else {
            logOnly(prolix2, "Ignoring return code (" + returnCode + ").");
        }

        return true;
    }

	protected boolean fetchArgument(String argPrefix, int argIndex, Vector args)
    {
        String argKey = argPrefix + argIndex;

        String nextArg = mainAttribs.getValue(argKey);

        if ( nextArg != null ) {
            args.addElement(nextArg);
            logOnly(prolix2, "Argument (" + argKey + ")=" + nextArg);
            return true;

        } else {
            return false;
        }
    }

    protected int processUserClass(String userClassName, Vector args, boolean driveBackup )
    {
        Set keyset = installedComponents.keySet();
        String[] components = new String[ keyset.size() ];
        components = (String[]) keyset.toArray(components);

        String[] backupComponents = null;

        // this is a hack
        // during a restore we do not have any components to give the userCass
        // so we will provide what we know

        if ( fRestore && (components.length == 0) ) {
            // components = new String[] {
            //     "AE_admin_client",
            //     "AE_admin_common",
            //     "AE_admin_server",
            //     "AE_server",
            //     "AE_samples",
            //     "Console",
            //     "Common",
            //     "Deploytools",
            //     "Plugins",
            //     "Samples_Common",
            //     "Server_Common",
            //     "Tools_Common",
            //     "J2EEClient",
            //     "JTCClient"
            // };

            // The folowing was added for defect 120991 because some standalone classes
            // may need to know what components are installed during a restore.

            String tmpBackupComponentsInstalled = mainAttribs.getValue(HelperList.meComponentsInstalled);

            if ( tmpBackupComponentsInstalled != null ) {
                StringTokenizer tmpComponents = new StringTokenizer(tmpBackupComponentsInstalled, ",");
                int numTokens = tmpComponents.countTokens();

                backupComponents = new String[numTokens];
                for ( int tokenNo = 0; tokenNo < numTokens; tokenNo++ )
                    backupComponents[tokenNo] = tmpComponents.nextToken();
            }
        }

        // for ( int i = 0; i < components.length; i++ )
        //     System.out.println("Diag #88 " + i + " component=(" + components[i] + ")");
        //
        // System.out.println("Diag #22 inputJarName=(" + deltaJarName + ")");
        // System.out.println("Diag #23 userClassName=(" + userClassName + ")");

        URL deltaURL;

        try {
            File file = new File(deltaJarName);
            deltaURL = file.toURL();

        } catch ( MalformedURLException ex ) {
            logError(183, "Malformed URL (" + deltaJarName + ")", ex);
            return -1;
        }

        // System.out.println("Diag #24 URL is=(" + deltaURL + ")");

        URL[] urls = new URL[] { deltaURL };
        URLClassLoader urlClassLoader = new URLClassLoader(urls);

        Class userClass;

        try {
            boolean initializeClass = true;
            userClass = Class.forName(userClassName, initializeClass, urlClassLoader);

        } catch ( ClassNotFoundException ex ) {
            logError(180, "Dynamic loading of class " + userClassName + " failed", ex);
            return -1;
        }

        UpdateAction updateAction = null;
        ExtendedUpdateAction extendedAction = null;

        try {
            if ( args == null )
                updateAction = (UpdateAction) userClass.newInstance();
            else
                extendedAction = (ExtendedUpdateAction) userClass.newInstance();

        } catch ( InstantiationException ex ) {
           logError(181, "Failed to instantiate " + userClassName, ex);
           return -1;

        } catch ( IllegalAccessException ex ) {
           logError(182, "Unable to access " + userClassName, ex);
           return -1;
        }

        StringBuffer msgSB = new StringBuffer();
        StringBuffer errSB = new StringBuffer();

        // if statement added for defect 120991 because some standalone classes may need to know
        // what components are installed during a restore.

        String[] componentsArg;

        if ( fRestore && (components.length == 0) )
            componentsArg = backupComponents;
        else
            componentsArg = components;

        // System.out.println("Performing action with target directory: " + targetDirFullName);

        int rc;

        Vector backupFiles = null;
        if ( args == null ) {
            if (driveBackup) {
                backupFiles = updateAction.file2Backup( targetDirFullName, componentsArg, po, msgSB, errSB, fDebug );
            }
            rc =   updateAction.process(targetDirFullName, componentsArg, po, msgSB, errSB, fDebug);
        } else {
            if (driveBackup) {
                backupFiles = extendedAction.file2Backup( targetDirFullName, componentsArg, po, msgSB, errSB, fDebug, args );
            }
            rc = extendedAction.process(targetDirFullName, componentsArg, po, msgSB, errSB, fDebug, args);
        }
        if (backupFiles != null) {
            // Iterate Vector and add files to ScanData
            scanData.absolutePaths.addAll( backupFiles );
            scanData.updateFiles.addAll( backupFiles );
            for ( int i=0; i<backupFiles.size(); i++ ) {
                String name = (String)backupFiles.get( i );
                scanData.absolutePathsSet.put( name, name );
                scanData.withSpecialUpdate.put( name, name );
            }
        }

        if ( msgSB.length() > 0 )
            log(msgSB.toString());

        if ( errSB.length() > 0 ) {
            logError(59, "Execution of " + userClassName + " returned an error message.");
            log(errSB.toString());
        }

        log("   " + userClassName + ".class rc=" + rc);

        return rc;
    }

    protected String validatePlatform(String incomingString)
    {
        String adjustedString = null;

        if ( incomingString != null ) {

            if ( incomingString.trim().startsWith("[") ) {
                int startingPos = incomingString.indexOf("[");
                int terminator = incomingString.indexOf("]");

                if ( terminator == -1) {
                    logError(131, "malformed platform sensitive specification," +
                                  " missing closing bracket in (" + incomingString + "), line ignored.");
                    return null;
                }

                String platformList = incomingString.substring(startingPos+1, terminator);
                logOnly(prolix3, "   Validating platform for (" + platformList + ")");

                String osName = System.getProperty("os.name");
                System.out.println("os.name=" + osName);
                System.out.println("platformList=" + platformList);
                if ( osName.startsWith( "Windows" ) ) {
                    if ( platformList.startsWith( "Windows" ) ) {
                        if ( platformList.equalsIgnoreCase( "Windows" ) ) {
                            adjustedString = incomingString.substring(terminator + 1);
                        }
                        else {
                StringTokenizer toks = new  StringTokenizer(platformList, ",");
                while ( toks.hasMoreTokens() ) {
                    String tok = toks.nextToken();

                                if ( tok.trim().equals(osName) ) {
                                    adjustedString = incomingString.substring(terminator + 1);
                                    break;
                                }
                            }
                        }
                    }
                }
                else {
                    if ( !platformList.startsWith( "Windows" ) ) {
                        if ( platformList.equalsIgnoreCase( "NonWindows" ) ) {
                            adjustedString = incomingString.substring(terminator + 1);
                        }
                        else {
                            StringTokenizer toks = new  StringTokenizer(platformList, ",");
                            while ( toks.hasMoreTokens() ) {
                                String tok = toks.nextToken();

                                if ( tok.trim().equals(osName) ) {
                                    adjustedString = incomingString.substring(terminator + 1);
                                    break;
                                }
                                
//                              special check for z/OS
//                                if(System.getProperty("os.name").equalsIgnoreCase("z/OS") || (System.getProperty("os.name").equalsIgnoreCase("OS/390"))){
//                                	if(tok.trim().equalsIgnoreCase("z/OS")
//                                    		|| tok.trim().equalsIgnoreCase("OS/390")){
//                                		adjustedString = incomingString.substring(terminator + 1);
//                                        break;
//                                    }
//                                	
//                                }
                            }
                        }

                    }
                }
                if ( adjustedString == null )
                    logOnly(prolix3, "      platform check is negative.");

            } else {
                adjustedString = incomingString;  // just return what was sent in
            }
        }

        return adjustedString;   // return the string with the [] stuff removed or null
    }

    protected boolean preppedProductHandling = false;
    protected boolean isProductHandlingOK = false;

    protected boolean prepProductFileHandling()
    {
        if ( preppedProductHandling ) {
            if ( !isProductHandlingOK )
                logOnly(prolix3, "Failing product file preparation because of a previous failure.");

            return isProductHandlingOK;
        }

        preppedProductHandling = true;

        if ( productFileType == null ) {
            logError(118, "Unable to prepare handling of product file: No set product file type.");
            isProductHandlingOK = false;

        } else if ( productFileType.equals(HelperList.mePropertiesProductFile) ) {
            if ( productVersionKey == null ) {
                logError(161, "Unable to prepare handling of product file: No set product version key.");
                isProductHandlingOK = false;

            } else {
                isProductHandlingOK = true;
            }

        } else if ( productFileType.equals(HelperList.meXMLProductFile) ) {
            isProductHandlingOK = prepXMLHandling();
        }

        if ( isProductHandlingOK )
            logOnly(prolix3, "Successfully prepared property handling.");

        return isProductHandlingOK;
    }

    protected boolean validating = false;
    protected boolean nameSpaceAware = false;
    protected XML_Handler xmlh = null;

    protected boolean prepXMLHandling()
    {
        logOnly(prolix3, "Preparing for XML Handling");

        // If we don't have any main attribs, something went terribly wrong.

        if ( mainAttribs == null ) {
            logError(113, "Failing XML preparation: No main attributes.");
            return false;
        }

        if ( po.keywordUsed(k_Validating) )
            validating = po.getBool(k_Validating);
        else
            validating = mainAttribs.getValue(HelperList.meValidating).equals(HelperList.meTrue);

        logOnly(prolix2, "XML Parsing Validating Setting: " + validating);

        if ( po.keywordUsed(k_NameSpaceAware) )
            nameSpaceAware = po.getBool(k_NameSpaceAware);
        else
            nameSpaceAware = mainAttribs.getValue(HelperList.meNameSpaceAware).equals(HelperList.meTrue);

        logOnly(prolix2, "XML Parsing NameSpaceAware Setting: " + nameSpaceAware);

        xmlh = new XML_Handler(logStream, validating, nameSpaceAware);

        String actualXMLVersion = xmlh.getVersion(fDebug);

        logOnly(prolix2, "Instantiated XML parser with version: " + actualXMLVersion);

        String expectedXMLVersion;

        if ( po.keywordUsed(k_XMLVersion) )
            expectedXMLVersion = po.getString(k_XMLVersion);
        else
            expectedXMLVersion = mainAttribs.getValue(HelperList.meXMLVersion);

        if ( expectedXMLVersion != null ) {
            logOnly(prolix2, "Required XML parser version: " + expectedXMLVersion);

            if ( !actualXMLVersion.equals(expectedXMLVersion) ) {
                logError(39, "Failing XML preparation: Parser version mismatch");
                return false;

            } else {
                logOnly(prolix3, "The XML Parser has the required version.");
            }

        } else {
            logOnly(prolix3, "No required XML parser version was set; skipping parser version check.");
        }

        return true;
    }

    protected void updateProductFile()
    {
        if ( productFileName == null ) {
            logOnly(prolix3, "Product File Update is not active; skipping update step.");
            return;

        } else if ( po.keywordUsed(k_UpdateProductFile) && !po.getBool(k_UpdateProductFile) ) {
            logOnly(prolix3, "Product file update was disabled; skipping update step.");
            return;

        } else {
            logOnly(prolix3, "Performing update of product file [ " + productFileName +
                             " ] of type [ " + productFileType + " ].");
        }

        // Special code to set the ending version --
        // the set ending version may be null, in which
        // case the starting version is used for the
        // history entry.  Otherwise, the non-null
        // ending version is used in the history entry.

        String historyEndingVersion;

        if ( versionChanged() ) {
            historyEndingVersion = getEndingVersion();
            logOnly(prolix3, "Updating product version from [ " + startingVersion +
                             " ] to [ " + historyEndingVersion + " ]");
            updateVersion();

        } else {
            logOnly(prolix3, "The product version remains unchanged at [ " + startingVersion + " ]");
            historyEndingVersion = startingVersion;
        }

        if ( productFileType.equals(HelperList.meXMLProductFile) ) {

            if ( buildNumberChanged() ) {
                logOnly(prolix3, "Updating product build number from [ " + startingBuildNumber +
                        " ] to [ " + getEndingBuildNumber() + " ]");
                updateBuildNumber();

            } else {
                logOnly(prolix3, "The product build number remains unchanged at [ " + startingBuildNumber + " ]");
            }

            if ( buildDateChanged() ) {
                logOnly(prolix3, "Updating product build date from [ " + startingBuildDate +
                        " ] to [ " + getEndingBuildDate() + " ]");
                updateBuildDate();

            } else {
                logOnly(prolix3, "The product build date remains unchanged at [ " + startingBuildDate + " ]");
            }
        }

        if ( productFileType.equals(HelperList.meXMLProductFile) ) {
            updateXMLHistory(historyEndingVersion);

        } else if ( productFileType.equals(HelperList.mePropertiesProductFile) ) {
            updatePropertiesHistory(historyEndingVersion);
        }

        logOnly(prolix3, "Completed update of XML file: " + productFileName);
    }

    protected void updateXMLHistory(String historyEndingVersion)
    {
        String cdInstalledComponents = null;

        if ( installedComponents == null ) {
            cdInstalledComponents = "n/a";

        } else {
            Set set = installedComponents.keySet();
            Iterator iterator = set.iterator();

            if ( iterator.hasNext() ) {
                String delim = "";
                cdInstalledComponents = "";
                while ( iterator.hasNext() ) {
                    String key = (String) iterator.next();
                    cdInstalledComponents =  cdInstalledComponents + delim + key;
                    delim = ",";
                }

            } else {
                cdInstalledComponents = "unknown";
            }
        }

        HelperList.XMLHistoryEventInfo historyInfo = new HelperList.XMLHistoryEventInfo();

        String addHistory    = mainAttribs.getValue(HelperList.meAddHistory).trim();
        boolean doAddHistory = addHistory.equalsIgnoreCase(HelperList.meTrue);

        historyInfo.validating      = validating;
        historyInfo.nameSpaceAware  = nameSpaceAware;
        historyInfo.xmlFileName     = productFileName;
        historyInfo.addHistory      = doAddHistory;
        historyInfo.description     = mainAttribs.getValue(HelperList.meDescription);
        historyInfo.type            = mainAttribs.getValue(HelperList.meEventType);
        historyInfo.containerType   = cdInstalledComponents;
        historyInfo.targetDirName   = targetDirName;
        historyInfo.backupJarName   = backupJarName;
        historyInfo.logFileName     = logFileFullName;
        historyInfo.startingVersion = startingVersion;
        historyInfo.endingVersion   = historyEndingVersion;
        historyInfo.deltaJarName    = decodedJarName;
        historyInfo.status          = ((errorCount > 0 ) ? "Failed" : "Successful");
        historyInfo.message         = Long.toString(errorCount) + " errors were noted";
        historyInfo.APAR            = mainAttribs.getValue(HelperList.meAPAR);
        historyInfo.PMR             = mainAttribs.getValue(HelperList.mePMR);
        historyInfo.developer       = "";

        String[] historyPath = getEventHierarchy();

        xmlh.addXMLEvent(fDebug, historyInfo, historyPath);

        if ( !xmlh.writeFile(fDebug, productFileName) ) {
            log("An error was encountered while writing the XML file.");
            errorCount++;
        }
    }

    protected void updatePropertiesHistory(String historyEndingVersion)
    {
        log("Skipping history update: The product file is a properties file.");
    }

    // Product information helpers ...

    // Answer null if there is some error retrieving
    // the starting version.

    /**
	 * @return  the startingVersion
	 * @uml.property  name="startingVersion"
	 */
    protected String getStartingVersion()
    {
        if ( loadedVersion )
            return startingVersion;

        loadedVersion = true;

        if ( prepProductFileHandling() ) {
            if ( productFileType.equals(HelperList.meXMLProductFile) )
                startingVersion = getXMLStartingVersion();
            else
                startingVersion = getPropertiesStartingVersion();
        } else {
            startingVersion = null;
        }

        if ( startingVersion == null )
            logError(123, "Failed to read product version.");
        else
            logOnly(prolix3, "Read product version: " + startingVersion);

        return startingVersion;
    }

    protected String getXMLStartingVersion()
    {
        return xmlh.query(fDebug, productFileName,  getVersionHierarchy());
    }

    protected String getPropertiesStartingVersion()
    {
        CommentedProperties productProperties = readPropertiesFile(productFileName);

        if ( productProperties == null )
            return null;

        return productProperties.getProperty(productVersionKey);
    }

    // Answer the version which will be stored in the end.
    // Answer null if the version is not to be updated.

    protected String getEndingVersion() {
        String endingVersion = po.getString(k_NewVersion);

        if ( endingVersion == null )
            endingVersion = mainAttribs.getValue(HelperList.meNewVersion);

        return endingVersion;
    }

    protected boolean versionChanged()
    {
        String endingVersion = getEndingVersion();
        return (endingVersion != null) && !startingVersion.equals(endingVersion);
    }

    // Should never get here if the ending version is null.

    protected void updateVersion()
    {
        if ( productFileType.equals(HelperList.meXMLProductFile) )
            updateXMLVersion();
        else
            updatePropertiesVersion();
    }

    protected void updateXMLVersion()
    {
        xmlh.update(fDebug, productFileName, getVersionHierarchy(), getEndingVersion());
    }

    protected void updatePropertiesVersion()
    {
        PUEvent versionEvent = new PUEvent(puFunctionNames[puUpdate], getEndingVersion());

        Vector versionEvents = new Vector();
        versionEvents.add(versionEvent);

        UpdatePropertyFile(productFileName, versionEvents);
    }

    // Answer null if there is some error retrieving the build number.
    //
    // Should only ever be called when the product file type is XML.

    /**
	 * @return  the startingBuildNumber
	 * @uml.property  name="startingBuildNumber"
	 */
    protected String getStartingBuildNumber()
    {
        loadedBuildNumber = true;

        startingBuildNumber = xmlh.query(fDebug, productFileName, getBuildNumberHierarchy());

        if ( startingBuildNumber == null )
            logError(88, "Failed to read product build number.");
        else
            logOnly(prolix3, "Read product build number: " + startingBuildNumber);

        return startingBuildNumber;
    }

    // Answer null if the build number is not to be updated.

    protected String getEndingBuildNumber()
    {
        String endingBuildNumber = po.getString(k_NewBuildNumber);

        if ( endingBuildNumber == null )
            endingBuildNumber = mainAttribs.getValue(HelperList.meNewBuildNumber);

        return endingBuildNumber;
    }

    protected boolean buildNumberChanged()
    {
        String endingBuildNumber = getEndingBuildNumber();
        return(endingBuildNumber != null) && !startingBuildNumber.equals(endingBuildNumber);
    }

    // Should never get here if the ending build number is null.

    protected void updateBuildNumber()
    {
        xmlh.update(fDebug, productFileName, getBuildNumberHierarchy(), getEndingBuildNumber());
    }

    // Answer null if there is an error loading the build date.
    //
    // Should only ever be called when the product file type is XML.

    /**
	 * @return  the startingBuildDate
	 * @uml.property  name="startingBuildDate"
	 */
    protected String getStartingBuildDate()
    {
        loadedBuildDate = true;

        startingBuildDate = xmlh.query(fDebug, productFileName, getBuildDateHierarchy());

        if ( startingBuildDate == null )
            logError(116, "Failed to read product build date.");
        else
            logOnly(prolix3, "Read product build number: " + startingBuildDate);

        return startingBuildDate;
    }

    // Answer null if the build date is not to be updated.

    protected String getEndingBuildDate()
    {
        String endingBuildDate = po.getString(k_NewBuildDate);

        if ( endingBuildDate == null )
            endingBuildDate = mainAttribs.getValue(HelperList.meNewBuildDate);

        return endingBuildDate;
    }

    protected boolean buildDateChanged()
    {
        String endingBuildDate = getEndingBuildDate();
        return(endingBuildDate != null) && !startingBuildDate.equals(endingBuildDate);
    }

    // Should never get here if the ending build date is null.

    protected void updateBuildDate()
    {
        xmlh.update(fDebug, productFileName, getBuildDateHierarchy(), getEndingBuildDate());
    }

    // Answer null if there is an error loading the edition name.

    // Should only ever be called when the product file type is XML.

    /**
	 * @return  the editionName
	 * @uml.property  name="editionName"
	 */
    protected String getEditionName()
    {
        if ( loadedEditionName )
            return editionName;

        loadedEditionName = true;

        if ( prepProductFileHandling() )
            editionName = xmlh.query(fDebug, productFileName,  getEditionNameHierarchy());
        else
            editionName = null;

        if ( editionName == null )
            logError(124, "Failed to read product edition name.");
        else
            logOnly(prolix3, "Loaded product edition name: " + editionName);

        return editionName;
    }

    // Answer null if there is an error loading the edition value.

    // Should only ever be called when the product file type is XML.

    /**
	 * @return  the editionValue
	 * @uml.property  name="editionValue"
	 */
    protected String getEditionValue()
    {
        if ( loadedEditionValue )
            return editionValue;

        loadedEditionValue = true;

        if ( prepProductFileHandling() )
            editionValue = xmlh.query(fDebug, productFileName,  getEditionValueHierarchy());
        else
            editionValue = null;

        if ( editionValue == null )
            logError(126, "Failed to read product edition value.");
        else
            logOnly(prolix3, "Loaded product edition value: " + editionValue);

        return editionValue;
    }

    protected String[]  versionHierarchy = null;

    /**
	 * @return  the versionHierarchy
	 * @uml.property  name="versionHierarchy"
	 */
    protected String[] getVersionHierarchy()
    {
        if ( versionHierarchy != null )
            return versionHierarchy;

        String usePath = po.getString(k_XMLPathVersion);

        if ( usePath == null )
            usePath = mainAttribs.getValue(HelperList.meXMLPathVersion);

        if ( usePath != null )
            versionHierarchy = HelperList.parseHierarchy(usePath);
        else
            versionHierarchy = HelperList.versionHierarchy;

        return versionHierarchy;
    }

    protected String[]  editionNameHierarchy = null;

    /**
	 * @return  the editionNameHierarchy
	 * @uml.property  name="editionNameHierarchy"
	 */
    protected String[] getEditionNameHierarchy()
    {
        if ( editionNameHierarchy != null )
            return editionNameHierarchy;

        String usePath = po.getString(k_XMLPathEditionName);

        if ( usePath == null )
            usePath = mainAttribs.getValue(HelperList.meXMLPathEditionName);

        if ( usePath != null )
            editionNameHierarchy = HelperList.parseHierarchy(usePath);
        else
            editionNameHierarchy = HelperList.editionNameHierarchy;

        return editionNameHierarchy;
    }

    protected String[]  editionValueHierarchy = null;

    /**
	 * @return  the editionValueHierarchy
	 * @uml.property  name="editionValueHierarchy"
	 */
    protected String[] getEditionValueHierarchy()
    {
        if ( editionValueHierarchy != null )
            return editionValueHierarchy;

        String usePath = po.getString(k_XMLPathEditionValue);

        if ( usePath == null )
            usePath = mainAttribs.getValue(HelperList.meXMLPathEditionValue);

        if ( usePath != null )
            editionValueHierarchy = HelperList.parseHierarchy(usePath);
        else
            editionValueHierarchy = HelperList.editionValueHierarchy;

        return editionValueHierarchy;
    }

    protected String[]  buildNumberHierarchy = null;

    /**
	 * @return  the buildNumberHierarchy
	 * @uml.property  name="buildNumberHierarchy"
	 */
    protected String[] getBuildNumberHierarchy()
    {
        if ( buildNumberHierarchy != null )
            return buildNumberHierarchy;

        String usePath = po.getString(k_XMLPathBuildNumber);

        if ( usePath == null )
            usePath = mainAttribs.getValue(HelperList.meXMLPathBuildNumber);

        if ( usePath != null )
            buildNumberHierarchy = HelperList.parseHierarchy(usePath);
        else
            buildNumberHierarchy = HelperList.buildNumberHierarchy;

        return buildNumberHierarchy;
    }

    protected String[]  buildDateHierarchy = null;

    /**
	 * @return  the buildDateHierarchy
	 * @uml.property  name="buildDateHierarchy"
	 */
    protected String[] getBuildDateHierarchy()
    {
        if ( buildDateHierarchy != null )
            return buildDateHierarchy;

        String usePath = po.getString(k_XMLPathBuildDate);

        if ( usePath == null )
            usePath = mainAttribs.getValue(HelperList.meXMLPathBuildDate);

        if ( usePath != null )
            buildDateHierarchy = HelperList.parseHierarchy(usePath);
        else
            buildDateHierarchy = HelperList.buildDateHierarchy;

        return buildDateHierarchy;
    }

    protected String[] getEventHierarchy()
    {

        String[] pathArray = null;
        String usePath = po.getString(k_XMLPathEvent);

        if ( usePath == null )
            usePath = mainAttribs.getValue(HelperList.meXMLPathEvent);

        if ( usePath != null )
            pathArray = HelperList.parseHierarchy(usePath);
        else
            pathArray = HelperList.eventHierarchy;

        return pathArray;
    }

    // PQ59675.. added to LocalSystem.java REMOVE THIS LATER

    protected boolean ensureDirectory(String dirName)
    {
        File childDir = new File(dirName);

        if ( !childDir.exists() ) {
            if ( !childDir.mkdirs() ) {
                logError(55, "Unable to create target directory: " + dirName);
                return false;

            } else {
                logOnly(prolix4, "Created directory: " + dirName);
                return true;
            }

        } else {
            logOnly(prolix4, "Directory already exists: " + dirName);
            return true;
        }
    }

    // String handling helpers ...

    protected String pad(String padString, int padCount)
    {
        return helper.Padit(padString, padCount, false, ' ');

        // false for flush left;
        // true for flush right (say, for numbers).
    }

    protected String pad(int padNumber, int padCount)
    {
        return pad(Integer.toString(padNumber), padCount);
    }

    protected String pad(long padNumber, int padCount)
    {
        return pad(Long.toString(padNumber), padCount);
    }

    // Complex log helpers ...

    protected void displayJarManifest(JarFile jar)
    {
        Manifest manifest;

        try {
            manifest = jar.getManifest();

        } catch ( IOException ex ) {
            logError(56, "IOException retrieving jar manifest", ex);
            return;
        }

        Map entries = manifest.getEntries();

        // logDebug("Entries in the manifest: " + entries.size());

        displayAttributes(manifest.getMainAttributes(), "Main Attributes");

        if ( fDebug ) {
            Set entryKeySet = entries.keySet();
            Iterator entryKeys = entryKeySet.iterator();

            while ( entryKeys.hasNext() ) {
                String entryKey = (String) entryKeys.next();
                Attributes entryAttributes = manifest.getAttributes(entryKey);
                displayAttributes(entryAttributes, entryKey);
            }
        }
    }

    protected void displayAttributes(Attributes attribs, String description)
    {
        // logDebug(description + " (" + attribs.size() + " total attributes):");

        Set attribSet = attribs.entrySet();
        Iterator attribIter = attribSet.iterator();

        while ( attribIter.hasNext() ) {
            Map.Entry mapEntry = (Map.Entry) attribIter.next();

            Attributes.Name key = (Attributes.Name) mapEntry.getKey();
            String value = (String) mapEntry.getValue();

            // logDebug("    " + key + " = " + value);
        }
    }

    // Basic log helpers ...

    protected boolean defaultedLogFileName;
    protected String logFileName;
    protected String logFileFullName;

    protected int logVerbosity;
    protected boolean quiet = Logger.BE_QUIET;

    protected Logger logStream = null;
    protected Helper1 helper = null;

    /**
	 * @return  the quiet
	 * @uml.property  name="quiet"
	 */
    public boolean isQuiet()
    {
        return quiet;
    }

    /**
	 * @param quiet  the quiet to set
	 * @uml.property  name="quiet"
	 */
    public void setQuiet(boolean quiet)
    {
        this.quiet = quiet;
    }

    protected boolean initializeLogging()
    {
        File logFile = new File(logFileName);
        logFileFullName = logFile.getAbsolutePath();

        // Append, don't truncate.
        logStream = new Logger(logFileName, true, logVerbosity, isQuiet());

        boolean didOpen = doLogging && openLogStream();
        if ( !didOpen )
            return false;

        helper = new Helper1(logStream, logVerbosity);

        if ( doLogging && isQuiet() )
            System.out.println("Quiet Mode: Logging to " + logFileFullName);

        return true;
    }

    protected boolean openLogStream()
    {
        boolean didOpen;

        if ( !ensureDir(logFileFullName) ) {
            logError(0, "Failed to ensure directory for log: " + logFileFullName);
            didOpen = false;

        } else if ( !logStream.Open() ) {
            logError(58, "Failed to open log: " + logFileFullName);
            didOpen = false;

        } else {
            didOpen = true;
        }

        return didOpen;
    }

    protected void closeLogStream()
    {
        logOnly(prolix3, "Closing Log");

        // Need to guard against an error within 'recyclyLogStream'
        // after the log had been closed and before the new log stream
        // is assigned.

        if ( logStream != null ) {
            logStream.Close();
            logStream = null;
        }
    }

    protected void recycleLogStream()
    {
        closeLogStream();
        initializeLogging();
    }

    protected boolean fDebug = false;

    protected void logDebug(String debugStatement)
    {
        if ( !doLogging )
            return;

        if ( logStream == null )
            initializeLogging();

        if ( fDebug )
            logStream.Both("Debug: " + debugStatement);
    }

    protected void log(String logStatement)
    {
        if ( !doLogging )
            return;

        if ( logStream == null )
            initializeLogging();

        logStream.Both(logStatement);
    }

    protected void logRaw(String logStatement)
    {
        if ( !doLogging )
            return;

        if ( logStream == null )
            initializeLogging();

        logStream.BothRaw(logStatement);
    }

    // Particular verbosity values; use these when producing log output.
    // Compare against the user set verbosity to check if the log output
    // is actually written.
    //
    // A high verbosity number from the user turns on more output.
    //
    // A high prolix number adds additional restrictions to output.

    protected static final int prolix0 = 0 ;
    // Particular verbosity values; use these when producing log output.
    // Compare against the user set verbosity to check if the log output
    // is actually written.
    //
    // A high verbosity number from the user turns on more output.
    //
    // A high prolix number adds additional restrictions to output.

    protected static final int prolix1 = 1 ;
    // Particular verbosity values; use these when producing log output.
    // Compare against the user set verbosity to check if the log output
    // is actually written.
    //
    // A high verbosity number from the user turns on more output.
    //
    // A high prolix number adds additional restrictions to output.

    protected static final int prolix2 = 2 ;
    // Particular verbosity values; use these when producing log output.
    // Compare against the user set verbosity to check if the log output
    // is actually written.
    //
    // A high verbosity number from the user turns on more output.
    //
    // A high prolix number adds additional restrictions to output.

    protected static final int prolix3 = 3 ;
    // Particular verbosity values; use these when producing log output.
    // Compare against the user set verbosity to check if the log output
    // is actually written.
    //
    // A high verbosity number from the user turns on more output.
    //
    // A high prolix number adds additional restrictions to output.

    protected static final int prolix4 = 4 ;
    // Particular verbosity values; use these when producing log output.
    // Compare against the user set verbosity to check if the log output
    // is actually written.
    //
    // A high verbosity number from the user turns on more output.
    //
    // A high prolix number adds additional restrictions to output.

    protected static final int prolix5 = 5 ;

    protected void log(String prefix, Vector statements, int prolix)
    {
        if ( !doLogging )
            return;

        if ( logStream == null )
            initializeLogging();

        int numStatements = statements.size();

        for ( int statementNo = 0; statementNo < numStatements ;statementNo++ ) {
            String nextStatement = (String) statements.elementAt(statementNo);

            if ( prefix != null )
                nextStatement = prefix + nextStatement;

            if ( fDebug )
                logStream.Both(nextStatement);
            else
                logStream.Log(prolix, nextStatement);
        }
    }

    protected void logOnlyDebug(int prolix, String logStatement)
    {
        if ( !doLogging )
            return;

        if ( fDebug )
            logOnly(prolix, "Debug: " + logStatement);
    }

    protected void logOnly(int prolix, String logStatement)
    {
        if ( !doLogging )
            return;

        if ( logStream == null )
            initializeLogging();

        if ( fDebug )
            logStream.Both(logStatement);
        else
            logStream.Log(prolix, logStatement);
        logStream.flush();

        // else, ignore the argument log statement
    }

    protected void logError(int errorNumber, String errorStatement)
    {
        errorCount++;

        if ( logStream != null )
            logStream.Err(errorNumber, errorStatement);
        else
            System.out.println("Error -- " + errorNumber + ": " + errorStatement);
    }

    protected void logError(int errorNumber, String errorStatement, Throwable th)
    {
        errorCount++;

        if ( logStream != null ) {
            logStream.Err(errorNumber, errorStatement, th);

        } else {
            System.out.println("Error -- " + errorNumber +
                               ": " + errorStatement +
                               ": " + th);
        }

        th.printStackTrace(System.out);
    }

    protected boolean ensureDir(String fileName)
    {
        Helper1 useHelper = new Helper1(null, 0); // Don't open the log file yet.

        String[] nameParts = new String[useHelper.k_elCount];

        useHelper.ParseFileSpec(fileName, nameParts, fDebug);

        File dirFile = new File(nameParts[useHelper.k_drive] + nameParts[useHelper.k_path]);

        if ( !dirFile.exists() ) {

            if ( !dirFile.mkdirs() ) {
                logError(179, "Failed to create directory for: " + fileName);
                return false;

            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public boolean earDirExists(String earPath)
    {
        return new File(earPath).exists();
    }

    // For example an entry name typically contains the following:
    //    /installable/earName.ear/aFile.ext
    //    /applications/earName.ear/aFile.ext
    //    /earName.ear/aFile.ext

    public static final String installablePrefix = "/installable/" ;
    // For example an entry name typically contains the following:
    //    /installable/earName.ear/aFile.ext
    //    /applications/earName.ear/aFile.ext
    //    /earName.ear/aFile.ext

    public static final String applicationsPrefix = "/applications/" ;
    // For example an entry name typically contains the following:
    //    /installable/earName.ear/aFile.ext
    //    /applications/earName.ear/aFile.ext
    //    /earName.ear/aFile.ext

    public static final String installedPrefix = "/" ;

    public String getEarPrefix()
    {
        if      ( handlingAsInstallable )
            return installedPrefix;
          //eedavis: delta does not permit building "/installable/" into path in efix
          //return installablePrefix;
        else if ( handlingAsApplication )
            return applicationsPrefix;
        else
            return installedPrefix;
    }

    public static int EAR_PREFIX_OFFSET = 0 ;
    public static int EAR_NAME_OFFSET = 1 ;
    public static int EAR_TAIL_OFFSET = 2 ;
        
    protected String[] earParts = new String[] {
        null,
        null,
        null
    };

    // Caution!  'getEarParts' uses a shared array, and cannot be
    // used synchronously.

    // For example:
    //
    //  '/installed/myEar.EAR/myFile.xml'
    //    ==>
    //      { '/installed/',
    //        'myEar.EAR',
    //        'myFile.xml' }

    public String[] getEarParts(String entryName)
    {
        String useEarPrefix = getEarPrefix();

        int nameStart = useEarPrefix.length();
        int tailStart = entryName.indexOf(entrySlashText, nameStart);

        String useEarName;
        String useEarTail;

        if ( tailStart == -1 ) {
            logError(197, "Failed to parse EAR entry: " + entryName);

            useEarName = null;
            useEarTail = null;

        } else {
            useEarName = entryName.substring(nameStart, tailStart);
            useEarTail = entryName.substring(tailStart);
        }

        earParts[EAR_PREFIX_OFFSET] = useEarPrefix;
        earParts[EAR_NAME_OFFSET]   = useEarName;
        earParts[EAR_TAIL_OFFSET]   = useEarTail;

        return earParts;
    }

    protected static final String slash     = System.getProperty("file.separator");
    protected static final char   slashChar = slash.charAt(0);

    protected static final String entrySlashText = "/";
    protected static final char   entrySlash     = '/';

    protected static final String altEntrySlashText = "\\";
    protected static final char   altEntrySlash     = '\\';

    protected String asPath(String pathText)
    {
        return ( (slashChar == entrySlash) ? pathText : pathText.replace(entrySlash, slashChar) );
    }

    protected String asEntry(String entryText)
    {
        return ( (slashChar == entrySlash) ? entryText : entryText.replace(slashChar, entrySlash) );
    }

    public static final boolean TRIM_ONE = false ;
    public static final boolean TRIM_BOTH = true ;

    public String trimSlash(String entryText, boolean both)
    {
        int useSize = entryText.length();

        if ( useSize == 0 )
            return entryText;

        useSize--;

        char lastChar = entryText.charAt(useSize);

        if ( (lastChar == entrySlash) ||
             (both && (lastChar == altEntrySlash)) ) {
            return entryText.substring(0, useSize);

        } else {
            return entryText;
        }
    }

    // When 'xtr_asInstallable', 'xtr_asApplication', or 'xtr_asInstalled'
    // are specified, the entries must be handled:
    //     relative to the temporary directory;
    //     relative to each configuration instance
    //     relative to the specific directory which is to be
    //     handled;
    // The three EAR settings may be applied in relation to a file
    // or JAR file.  When applied to a JAR file the settings apply
    // to all entries in the file.
    //
    // At the beginning of processing, the temporary directory
    // is noted, and a unique name is set for creating subdirectory
    // a subdirectory.
    //
    // While scanning, if any of the EAR settings is noted, configuration
    // instances are scanned for the target installation.  Then, for each
    // entry with an EAR setting, that setting is taken to apply to
    // each configuration instance, and relative to the appropriate directory.
    // Here 'appropriate' means:
    //
    // <temp>/<genTemp>/<instanceName>/<subDir>/<entryPath>
    //
    // Where:
    //     <temp> is the temporary directory
    //     <genTemp> is the unique name generated for processing
    //     <instanceName> is the name of the configuration instance
    //     <subDir> is the specific directory to EARS
    //     <entryPath> is the actual path for the entry
    //
    // <subDir> will be one of the paths in the configuration instance.
    //
    // Typically:
    //     <instanceRoot>/installableApps
    //     <instanceRoot>/config/cells/<cellName>/nodes/<nodeName>/applications
    //     <instanceRoot>/installedApps
    //
    // However, for pre and post processing, these paths are shortened,
    // respectively, to:
    //
    //     installableApps
    //     applications
    //     installedApps
    //
    // The recording of backup information is as before; the EAR setting
    // is propogated into the backup information.  However, the path that
    // is stored into the backup information includes the instance name
    // and subdirectory name.
    //
    // While processing entries for restoration, a modification is made.
    // Configuration instances are not scanned, rather, the accumulated
    // backup entries are processed as they appear.  Since all backup
    // entries are explicitly specified, for each configuration instance,
    // iteration across configuration instances is implicit in the backup
    // information.
    //
    // Two modifications of processing are neccesary to handle EAR entries.
    // These modifications are to allow iterative processing of file
    // and JAR files, both during the backup process, and during the
    // application process.
    //
    // The modification is, when performing direct application (not backup
    // application) to iterate across configuration instances for each
    // file and JAR entry.
    //
    // For processing a JAR, this requires that the jar file be reopened
    // and scanned to the point at which the EAR entry was noted, with
    // processing continuing for the next configuration.

    // Perform basic processing.  This assumes that logging has been initialized and
    // that all command line arguments have been processed.
    //
    // Complete any required arguments, then perform any required preprocessing
    // of the delta information, then proceed with the actual extraction and
    // application of the delta information.
}
