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
 * HelperList -- constants for specifying and encoding
 *               update instructions.
 *
 * History 1.4, 8/9/05
 *
 * 15-Jan-2001 1.04 Defect 90102
 *                  Add the reSequenceJar supportFile and meReSeqJar and meReSeqList strings
 *
 * 20-Jun-2002 1.21 PQ59675; Kim Hackett
 *                  Add the meChmodOnly string
 *                  Add the Extractor$ScanData.class as support file
 *
 * 26-Nov-2002 Added support for asInstalled, asApplication,
 *             and asInstallable updates.
 *
 *             Added support for perInstance updates.
 *
 *             Added 'chunkSize' (in addition to 'chunckSize'.
 *
 *             Added support for AddOnly and ReplaceOnly.
 *
 *             Added 'wildCard' constant.
 *
 * 25-Jan-2003 Added support for EAR naming rules.
 *
 *             Added support for AltMetadata flag.
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  
 */
public class HelperList
{
   public static final String pgmVersion = "1.4" ;
   public static final String pgmUpdate = "8/9/05" ;

    protected static Logger log;

    public HelperList()
    {
    }

    public HelperList(Logger log)
    {
        HelperList.log = log;
    }

    // The support files are used by the extractor and must be included in the
    // self-extracting jar.  Some of the class are internal classes from Delta
    // and Extractor, others are helper classes.  Both Delta and Extractor
    // read this list.

    public static final String[] SupportFiles = {
        "com/ibm/websphere/update/delta/Extractor.class",
        "com/ibm/websphere/update/delta/Extractor$ChildJarEntry.class",
        "com/ibm/websphere/update/delta/Extractor$BKJattribs.class",
        "com/ibm/websphere/update/delta/Extractor$FUEntry.class",
        "com/ibm/websphere/update/delta/Extractor$FRList.class",
        "com/ibm/websphere/update/delta/Extractor$PFUpdates.class",
        "com/ibm/websphere/update/delta/Extractor$PUEvent.class",
        "com/ibm/websphere/update/delta/Extractor$ScanData.class",
        "com/ibm/websphere/update/delta/Extractor$IncompleteJarEntryValues.class",
        "com/ibm/websphere/update/delta/Extractor$UnusedJarEntryValues.class",
        "com/ibm/websphere/update/delta/ChangeItem.class",
        "com/ibm/websphere/update/delta/Helper1.class",
        "com/ibm/websphere/update/delta/Logger.class",
        "com/ibm/websphere/update/delta/POProcessor.class",
        "com/ibm/websphere/update/delta/PODef.class",
        "com/ibm/websphere/update/delta/FileSystemSpace.class",
        "com/ibm/websphere/update/delta/ExecCmd.class",
        "com/ibm/websphere/update/delta/ReSequenceJar.class",
        "com/ibm/websphere/update/delta/HelperList.class",
        "com/ibm/websphere/update/delta/HelperList$XMLHistoryEventInfo.class",
        "com/ibm/websphere/update/delta/XML_Handler.class",
        "com/ibm/websphere/update/delta/DomL2Spt.class",
        "com/ibm/websphere/update/delta/DomL2Spt$errHandler.class",
        "com/ibm/websphere/update/delta/findClassInPath.class",
        "com/ibm/websphere/update/delta/ShowProgress.class",
        "com/ibm/websphere/update/delta/LocalSystem.class",
        "com/ibm/websphere/update/delta/UnixFile.class",
        "com/ibm/websphere/update/delta/WindowsFile.class",
        "com/ibm/websphere/update/delta/ISystemFile.class",
        "com/ibm/websphere/update/delta/CallablePTFClass.class"
    };

    public static final int k_FileSize = 0 ;
    // these variables are used in conjunction with
 public static final int k_CRC = 1 ;
    // ChangeItem actions for the DeltaByteGenerator
 public static final int k_Replace = 2 ;
    // these values are duplicated in ChangeItem.java
 public static final int k_Insert = 3 ;
    public static final int k_Delete = 4 ;

    // The following manifest attributes are set for to indicate
    // particular values for the extraction operation.
    // the prefix "me" is ManifestEntry

    // Used as a value for several boolean manifest entries

    public static final String meTrue = "true" ;
    // The following manifest attributes are set for to indicate
    // particular values for the extraction operation.
    // the prefix "me" is ManifestEntry

    // Used as a value for several boolean manifest entries

    public static final String meFalse = "false" ;
    // The following manifest attributes are set for to indicate
    // particular values for the extraction operation.
    // the prefix "me" is ManifestEntry

    // Used as a value for several boolean manifest entries

    public static final String meNoSignature = "<No Signature>" ;

    public static final String
        wildCard = "*";

    // Main-Manifest entries:

    public static final String meBackupFrom = "xtr_backupFrom" ;
    // Indicates that the source jar file is a backup
 // of a previous extraction operation.  When specified
 // a number of special modifications are made to the
 // extraction operation:
 //   No backup is made;
 //   No check of available size is made;
 //   No check for a duplicate application is performed.
 // Main-Manifest entries:

    public static final String meLogFile = "xtr_LogFile" ;
    // 'xtr_LogFile' is used to provide a log file name during
 // prerequisite checking.
 // Main-Manifest entries:

    public static final String meCkSize = "xtr_ckSize" ;
    // If set, perform a prerequisite check on the size available
 // in the update target.
 // Main-Manifest entries:

    public static final String meProductFile = "xtr_ProductFile" ;
    // This is the product file to update with version
 // information.   Without this entry the version
 // information will not be updated.
 // Main-Manifest entries:

    public static final String meProductFileType = "xtr_ProductFileType" ;
    // This is the type of the product file, one of the
 // values listed below:
 // Main-Manifest entries:

    public static final String meXMLProductFile = "XML" ;
    // A value for 'meProductFileType', indicating that the
 // product file is an XML file.
 // Main-Manifest entries:

    public static final String meSpareProductFile = "xtr_SpareProductFile" ;
    // This file if present is to be layed down in case the thatge does not have one.
 // This variable has a number apended to the end as there may be a more
 // that one file, ie DTD.
 // Main-Manifest entries:

    public static final String mePropertiesProductFile = "Properties" ;
    // A value for 'meProductFileType', indicating that the
 // product file is a properties file.
 // Main-Manifest entries:

    public static final String meXMLPathVersion = "xtr_XMLPathVersion" ;
    // Main-Manifest entries:

    public static final String meXMLPathEditionName = "xtr_XMLPathEditionName" ;
    // Main-Manifest entries:

    public static final String meXMLPathEditionValue = "xtr_XMLPathEditionValue" ;
    // Main-Manifest entries:

    public static final String meXMLPathBuildDate = "xtr_XMLPathBuildDate" ;
    // Main-Manifest entries:

    public static final String meXMLPathBuildNumber = "xtr_XMLPathBuildNumber" ;
    // Main-Manifest entries:

    public static final String meXMLPathEvent = "xtr_XMLPathEvent" ;
    // Set when alternate paths are to be used when
 // accessing the various product fields.
 // The value for each of these must be a dotted list,
 // for example, "#document.websphere.appserver.version".
 // Main-Manifest entries:

    public static final String mePropertiesVersionKey = "xtr_PropertiesVersionKey" ;
    // Set when using a properties file and when either there is
 // a version prerequisite or the version is to be updated.
 // Main-Manifest entries:

    public static final String meCkEdition = "xtr_ckEdition" ;
    // If set, perform a prerequisite check on the edition set
 // in the product file.  This value is not currently in use.
 // Main-Manifest entries:

    public static final String meCkEditionName = "xtr_ckEditionName" ;
    // Main-Manifest entries:

    public static final String meCkEditionValue = "xtr_ckEditionValue" ;
    // If set, perform a prerequisite check on the edition name
 // and edition value set in the product file.  These two
 // values are are only meaningful when the product file is
 // an XML type file.
 // Main-Manifest entries:

    public static final String meXMLVersion = "xtr_XMLVersion" ;
    // This is the XML parser version we are expecting.
 // currently (2001.08.20) it is "Xerces 1.2.1".  This
 // option is only meaningful when processing an XML type
 // product file.
 // Main-Manifest entries:

    public static final String meValidating = "xtr_Validating" ;
    // This is used to supply the value for the 'validating'
 // property of the XML parser that is instantiated.
 // Valid values are 'true' and 'false'.  This value is
 // only meaningful when processing an XML type product
 // file.
 // Main-Manifest entries:

    public static final String meNameSpaceAware = "xtr_NameSpaceAware" ;
    // This is used to supply the value for the 'nameSpaceAware'
 // property of the XML parser that is instantiated.
 // Valid values are 'true' and 'false'.  This value is only
 // meaningful when processing an XML type product file.
 // Main-Manifest entries:

    public static final String meCkVersion = "xtr_ckVersion" ;
    // If set, perform a prerequisite check on the version set
 // in the product file.
 // Main-Manifest entries:

    public static final String meNewVersion = "xtr_NewVersion" ;
    // Main-Manifest entries:

    public static final String meNewBuildNumber = "xtr_NewBuildNumber" ;
    // Main-Manifest entries:

    public static final String meNewBuildDate = "xtr_NewBuildDate" ;
    // These are used to supply a new version, build number, and
 // build date for the product file.  Note that the build
 // number and build date currently apply only to the
 // WebSphere product file, which is an XML file.
 // Main-Manifest entries:

    public static final String meFileUp = "xtr_FileUp" ;
    // This option is used to list files to which special find/
 // replace operations are to be performed.  Files are listed
 // by appending digits starting with '0', for example:
 // xtr_FileUp0, xtr_FileUp1, and so on.
 // Main-Manifest entries:

    public static final String meFileUpFind = "find" ;
    // Main-Manifest entries:

    public static final String meFileUpRepl = "repl" ;
    // The find/repl string are to be appended to the xtr_FileUp
 // entries ie: xtr_FileUp1find1 and a matching xtr_FileUp1repl1.
 // Main-Manifest entries:

    public static final String meFileUpComponent = "component" ;
    // The component will be appended to the end of xtr_FileUp1
 // and the component will have the number zero append, the value
 // of this key will be the number of component entries. ie:
 //   xtr_FileUp1component0 = 2
 // then for each component there will be a
 //   xtr_FileUp1component1 = Server
 //   xtr_FileUp1component2 = Console
 // Main-Manifest entries:

    public static final String meChunckSize = "xtr_ChunckSize" ;
    // This is a main manifest entry indicating what the buffer size
 // used for ByteDelta building.  This option is not currently in
 // use.
 // Main-Manifest entries:

    public static final String meNoBackUpJar = "xtr_NoBackUpJar" ;
    // A digit, starting at 1, is appended to the xtr_NoBackUpJar.
 // The entry is set to a compound value that contains a file name
 // that if present or not present will cause the Extractor to bypass
 // the creation of a BackUp Jar, In support of a Admin-Client Only
 // installation. the compound variable look like:
 //    1*filespec
 //       or
 //    0*filespec
 // the leading 1 or 0 indicate to verify the presence or absence of
 // the specified file
 // Main-Manifest entries:

    public static final String mePropFile = "xtr_PropFile" ;
    // A numeric value is appended to the mePropFile and the value
 // associated is the name of the property file to update.
 //  ie  xtrPropFile1 = $(target)/properties/sas.client.props
 // Main-Manifest entries:

    public static final String mePropFun = "fun" ;
    // This entry is prefixed with the xtr_PropFile1 string and is
 // suffixed with a numeric digit to indicate a function.  For
 // example, "xtr_PropFile1fun1 = softadd".
 // Main-Manifest entries:

    public static final String mePropKey = "key" ;
    // Main-Manifest entries:

    public static final String mePropValue = "value" ;
    // These values are appended to the mePropFile and are suffixed
 // with a numeric digit to represent the key value pairs.
 // ie:   xtr_PropFile1key1 = FrogColor
 //       xtr_PropFile1Value1 = Green
 // Main-Manifest entries:

    public static final String meReSeqJar = "xtr_reSeqJar" ;
    // Main-Manifest entries:

    public static final String meReSeqList = "xtr_reSeqList" ;
    // These values will be found in pairs and have a numeric value
 // apended to the end starting with one. The reSeqJar is the filespec
 // of the jar file to reSequence and the reSeqList is the text file
 // that contains the the desired sequence. The reSeqList will be read
 // directly from the Delta jar file.
 // Main-Manifest entries:

    public static final String meCheck4Class = "xtr_Check4Class" ;
    // This is a comma delimited list of classes that should only appear
 // once on the customers classpath.
 // Main-Manifest entries:

    public static final String meStartMsg = "xtr_startMsg" ;
    // This is the message that is displayed as the start of the
 // extractor run.
 // Main-Manifest entries:

    public static final String meEndMsg = "xtr_endMsg" ;
    // This is the message that is displayed as the conclusion of the
 // extractor run.
 // Main-Manifest entries:

    public static final String meCall4Help = "xtr_Call4Help" ;
    // This phone number is displayed in messages produced by the Extractor.
 // Main-Manifest entries:

    public static final String meDupCheck = "xtr_DupCheck" ;
    // This is to control if the Extractor will check if this has been
 // applied before the following entries may appear on entry manifest
 // entries.
 // Main-Manifest entries:

    public static final String meForce = "xtr_Force" ;
    // This option is used to list files which are included in the
 // backup file regardless of additional process.  In particular,
 // regardless of whether or not they have been updated.  Individual
 // files are listed by appending a digit to the 'meForce' option,
 // starting at '0'.  For example, 'xtr_Force0', 'xtr_Force1', and
 // so on.
 // Main-Manifest entries:

    public static final String meAddHistory = "xtr_AddHistory" ;
    // This is used to tell if history information is to
 // be appended when updating the product file.  This
 // is currently only applicable when the product file
 // type is XML.
 // Main-Manifest entries:

    public static final String meEventType = "xtr_EventType" ;
    // The type of the packaged, updated one the values listed
 // below.  The type is stored as a part of the history
 // entry added into the product file.
 // Main-Manifest entries:

    public static final String meAPAR = "xtr_APAR" ;
    // A value for 'meEventType', indicating that the update
 // is an APAR.
 // Main-Manifest entries:

    public static final String mePMR = "xtr_PMR" ;
    // A value for 'meEventType', indicating that the update
 // is a PMR (E-Fix).
 // Main-Manifest entries:

    public static final String meDeveloper = "xtr_Developer" ;
    // Used to supply a value for the corresponding field
 // in the history event which is added to the product file.
 // Main-Manifest entries:

    public static final String meDescription = "xtr_Description" ;
    // Used to supply a value for the corresponding field
 // in the history event which is added to the product file.
 // Main-Manifest entries:

    public static final String meAffectedComponents = "xtr_AffectedComponents" ;
    // This is a comma delimited list of components to which this
 // update applies.
 // Main-Manifest entries:

    public static final String meComponentName = "xtr_ComponentName" ;
    // A sequential number is apended to this constant starting at 1.
 // The value is a comma delimited list whose first element is the
 // component name and all following elements are the signatures.
 // each signature (filespec) is preceeded by a plus (+) or a
 // minus (-) sign indicating weather to test for the presence of
 // or absence of the signature file.
 // Main-Manifest entries:

    public static final String meTargetMsg = "xtr_TargetMsg" ;
    // This or bar delimited string is presented to the user to determine
 // the target of the update.
 // Main-Manifest entries:

    public static final String meEntryScript = "xtr_EntryScript" ;
    // Main-Manifest entries:

    public static final String mePreScript = "xtr_PreScript" ;
    // Main-Manifest entries:

    public static final String mePostScript = "xtr_PostScript" ;
    // Main-Manifest entries:

    public static final String meCmd = "Cmd" ;
    // Main-Manifest entries:

    public static final String meClass = "Class" ;
    // Main-Manifest entries:

    public static final String meRCmd = "RCmd" ;
    // Main-Manifest entries:

    public static final String meUnCmd = "UnCmd" ;
    // Main-Manifest entries:

    public static final String meUnClass = "UnClass" ;
    // Main-Manifest entries:

    public static final String meArg = "Arg" ;
    // The preceeding tags are used as a part of a grammar
 // for specifying script statements.
 //
 // 'meEntryScript' is used to indicate script elements
 // which are to be executed before apply the update, and
 // before scanning for backup information.
 //
 // 'mePrescript' is used to indicate script elements which
 // are to be executed before applying the update jar proper.
 //
 // 'mePostScript' is used to indicate script elements which
 // are to be executed after applying the update jar proper.
 //
 // Number are appended to the 'mePreScript' and 'mePostScript'
 // tags to build lists of scripts, for example:
 //   'xtr_PreScript1'
 //   'xtr_PreScript2'
 //
 // The three tags 'meCmd', 'meRCmd', and 'meUnCmd', are used
 // for script elements, the particular tag is used to indicate
 // the type of each element elements.  'meCmd' indicates
 // a command to be executed during the update application.
 // 'meRCmd' indicates a command to be executed during the
 // update application, and for which an error will cause
 // an immediate abnormal exit.  'meUnCmd' indicates a command
 // which is to be executed during a restoration operation.
 //
 // One of these tags is appended to the script tag, for
 // example:
 //   'xtr_PreScript1Cmd1'
 //   'xtr_PreScript1RCmd2'
 //   'xtr_PreScript1UnCmd1'
 //
 // 'meArg' is used to prefix arguments to class commands,
 // for example:
 //
 // xtrPreScript1Cmd1 = dir
 // xtrPreScript1Class2 = EARActor
 // xtrPreScript1
 // The following entries may be found on the file entry manifests
 // Main-Manifest entries:

    public static final String meForceBackup = "xtr_forcebackup" ;
    // This option is intended to be used within virtual scripts to
 // force the backup of a file.
 // Main-Manifest entries:

    public static final String meDeltaByte = "xtr_DeltaByte" ;
    // this specification may be included in the jar entry to indicate
 // that the data contained by this entry is in deltaByte format.
 // Main-Manifest entries:

    public static final String meNoRestore = "xtr_noRestore" ;
    // 'xtr_noRestore' is for jar entries which are ignored.
 // For example, the classes that are used to perform the self
 // extraction are marked with 'xtr_noRestore'.
 // Main-Manifest entries:

    public static final String meHelperClass = "xtr_HelperClass" ;
    // if true, this class is required in the BackupJar
 // Main-Manifest entries:

    public static final String meAbsolutePath = "xtr_absolutePath" ;
    // 'xtr_absolutePath' is for jar entries which are to
 // be restored to an absolute path in liu of the relative.
 // Main-Manifest entries:

    public static final String meDeleteBeforeWrite = "xtr_deleteBeforeWrite" ;
    // 'xtr_deleteBeforeWrite' is for jar entries which are to
 // be deleted before begin overwritten.  This detail is
 // provided to handle shared object files on AIX, which may
 // be left locked.  These files must be deleted before the
 // updated file may be written.
 // Main-Manifest entries:

    public static final String meDelete = "xtr_delete" ;
    // 'xtr_delete' is for jar entries which are to be deleted.
 // This applies both to top level jar entries and to child
 // jar entries.
 // Main-Manifest entries:

    public static final String meAddFile = "xtr_addFile" ;
    // Use 'xtr_addFile' when the update is not to be performed
 // if the file already exists.
 // Main-Manifest entries:

    public static final String meReplaceFile = "xtr_replaceFile" ;
    // Use 'xtr_conditionalUpdate' when an update is to be
 // performed only when the specified file already exists.
 // Main-Manifest entries:

    public static final String meJar = "xtr_jar" ;
    // 'xtr_jar' is for jar entries which start a sequence of
 // child jar entries.  The child jar entries, as listed above,
 // specify changes to be made to a jar file.
 // Main-Manifest entries:

    public static final String meNoDelete = "xtr_noDelete" ;
    // 'xtr_noDelete' is to inhibit a file from being deleted during
 // a backup, things like uninstall.sh
 // Main-Manifest entries:

    public static final String meRestoreOnly = "xtr_restoreOnly" ;
    // This file is intended to be laid down only during a restore.
 // Main-Manifest entries:

    public static final String meRequiredVersion = "xtr_RequiredVersion" ;
        // This file operation has a required version.

    // Entry comment and extra data settings ...

    public static final String meRemoveZipComment = "xtr_RemoveZipComment" ;
        // This file operation has a required version.

    // Entry comment and extra data settings ...

    public static final String meRemoveZipExtra = "xtr_RemoveZipExtra" ;
        // The preceeding are used to complete the additional information
 // associated with zip and jar entries.  The assumption is that
 // such information need not be updated, so a non-null value must
 // be supplied with an update entry to cause an update.  This
 // means that a null will not cause an update.  The preceeding
 // two are used to cause the null to be used, effectively clearing
 // the additional field.
 // This file operation has a required version.

    // Entry comment and extra data settings ...

    public static final String meIgnoreZipContent = "xtr_IgnoreZipContent" ;
        // Used to indicate an entry which is not to be used to update
        // the contents of an existing entry.  Such an update entry is
        // used to supply a comment or extra data value.

    // Permissions related settings ...

    public static final String mePermissions = "Permissions" ;
        // this is the unix style permission as three octal string characters
 // to be used with a chmod command
 // Used to indicate an entry which is not to be used to update
        // the contents of an existing entry.  Such an update entry is
        // used to supply a comment or extra data value.

    // Permissions related settings ...

    public static final String meChmod = "xtr_chmod" ;
        // this will cause the extractor to exec a chmod on unix and
 // an attrib.exe on intel platforms. the value of the xtr_chmod
 // is the value given to the chmod.
 // Used to indicate an entry which is not to be used to update
        // the contents of an existing entry.  Such an update entry is
        // used to supply a comment or extra data value.

    // Permissions related settings ...

    public static final String meChgrp = "xtr_chgrp" ;
        // this will cause the extractor to exec a chgrp on unix and
 // nothing on intel platforms. the value of the xtr_chgrp
 // is the value given to the chgrp.
 // KH Defect PQ59675
 // Used to indicate an entry which is not to be used to update
        // the contents of an existing entry.  Such an update entry is
        // used to supply a comment or extra data value.

    // Permissions related settings ...

    public static final String meChown = "xtr_chown" ;
        // this will cause the extractor to exec a chown on unix and
 // nothing on intel platforms. the value of the xtr_chown
 // is the value given to the chown.
 // KH Defect PQ59675
 // Used to indicate an entry which is not to be used to update
        // the contents of an existing entry.  Such an update entry is
        // used to supply a comment or extra data value.

    // Permissions related settings ...

    public static final String meChmodOnly = "xtr_chmodOnly" ;
        // keeps an update from updating the contents,
        // only used with meChmod.
        // KH Defect PQ59675

    // Component directives ...

    public static final String meComponent = "xtr_Component" ;
        // These are the components that this file is a member of.
 // keeps an update from updating the contents,
        // only used with meChmod.
        // KH Defect PQ59675

    // Component directives ...

    public static final String meComponentsInstalled = "xtr_ComponentsInstalled" ;
        // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String mePrepareEARTmp = "xtr_PrepareEARTmp" ;
        // 'xtr_PrepareEARTmp' is set when the EAR temporary
 // directory must be prepared.  
 // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String mePrepareInstances = "xtr_PrepareInstances" ;
        // 'xtr_PrepareInstances' is set when there is any ear
 // processing to do, in which case the configuration
 // instances must be scanned.
 // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String mePerInstance = "xtr_perInstance" ;
        // 'xtr_perInstance' is used for entries which
 // must be replaced across the configuration instances.
 // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String meAsInstallable = "xtr_asInstallable" ;
        // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String meAsApplication = "xtr_asApplication" ;
        // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String meAsInstalled = "xtr_asInstalled" ;
        // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String meAsMetadata = "xtr_asMetadata" ;
        // Added for defect 120991 by Jay Sartoris
        // This will be used to store the installed components in the manifest
        // file when we do a restore, we may need to know what components are installed.

    // EAR processing directives:

    public static final String meNameRule = "xtr_nameRule" ;

        // 'xtr_asInstallable', 'xtr_asApplication', and 'xtr_asInstalled'
        // are for the processing of entries within EAR files;
        // during extraction, installable and application files
        // are updated relative to the EAR temporary directory.

        // 'xtr_asMetadata' is used for installed entries
        // which qualify as metadata, in which case the entries
        // may be updated relative to the configuration directory
        // as well.

    // Entries with 'xtr_asInstallable' must begin with the directory
    // 'installable', so to prevent entry conflicts.

    // Entries with 'xtr_asApplication' must begin with the directory
    // 'applications', so to prevent entry conflicts.

    public static final String earInstallableDir = "installable" ;
        // 'xtr_asInstallable', 'xtr_asApplication', and 'xtr_asInstalled'
        // are for the processing of entries within EAR files;
        // during extraction, installable and application files
        // are updated relative to the EAR temporary directory.

        // 'xtr_asMetadata' is used for installed entries
        // which qualify as metadata, in which case the entries
        // may be updated relative to the configuration directory
        // as well.

    // Entries with 'xtr_asInstallable' must begin with the directory
    // 'installable', so to prevent entry conflicts.

    // Entries with 'xtr_asApplication' must begin with the directory
    // 'applications', so to prevent entry conflicts.

    public static final String earApplicationsDir = "applications" ;

    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Version = "Version" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Debug = "Debug" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Help = "?" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_LogFile = "LogFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Verbosity = "Verbosity" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PropertyFile = "PropertyFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ShowOptions = "ShowOptions" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_FrequencyUpdate = "FrequencyUpdate" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_OldTree = "OldTree" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NewTree = "NewTree" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_JarName = "JarName" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Compression = "Compression" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_LeadingSlash = "LeadingSlash" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ParseJar = "ParseJar" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Recurse = "Recurse" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_FilterFile = "FilterFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CaseSensitive = "CaseSensitive" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_DisplayManifest = "DisplayManifest" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_GenBD = "GenBD" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ChunckSize = "ChunckSize" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ChunkSize = "ChunkSize" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ReSyncLen = "ReSyncLen" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ReSyncScan = "ReSyncScan" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_MaxSizePct = "MaxSizePct" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_TransferFile = "TransferFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AbsTransferFile = "AbsTransferFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_RestoreOnly = "RestoreOnly" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AddFile = "AddFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ReplaceFile = "ReplaceFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AbsAddFile = "AbsAddFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AbsReplaceFile = "AbsReplaceFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AddOnly = "AddOnly" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ReplaceOnly = "ReplaceOnly" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_VerifyNewJar = "VerifyNewJar" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ckVersion = "ckVersion" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NewVersion = "NewVersion" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NewBuildNumber = "NewBuildNumber" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NewBuildDate = "NewBuildDate" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CkEdition = "ckEdition" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CkEditionName = "ckEditionName" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CkEditionValue = "ckEditionValue" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CkSize = "ckSize" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_DupCheck = "DupCheck" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_GetNewFile = "GetNewFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_GetReplaceFile = "GetReplaceFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_GetJarEntry = "GetJarEntry" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Target = "Target" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ExecCmd = "ExecCmd" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_HelperLocation = "HelperLocation" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_EventType = "EventType" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AffectedComponents = "AffectedComponents" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_APAR = "Apar" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PMR = "PMR" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Description = "Description" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_UpdateXML = "UpdateXML" ;
    // Deprecated - replaced with ProductFileType
 // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ProductFileType = "ProductFileType" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ProductFileName = "ProductFileName" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ProductFileVKey = "ProductFileVKey" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_XMLPathVersion = "XMLPathVersion" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_XMLPathEditionName = "XMLPathEditionName" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_XMLPathEditionValue = "XMLPathEditionValue" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_XMLPathBuildDate = "XMLPathBuildDate" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_XMLPathBuildNumber = "XMLPathBuildNumber" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_XMLPathEvent = "XMLPathEvent" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_SpareProduct = "SpareProduct" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_XMLVersion = "XMLVersion" ;
    // Deprecated - replaced with ProdVersion
 // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ProdVersion = "ProdVersion" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Validating = "Validating" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NameSpaceAware = "NameSpaceAware" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AddHistory = "AddHistory" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_EntryScript = "EntryScript" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PreScript = "PreScript" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PostScript = "PostScript" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CmdDelimiter = "CmdDelimiter" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ArgDelimiter = "ArgDelimiter" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CheckForClass = "CheckForClass" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PackageInclude = "PackageInclude" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Call4Help = "Call4Help" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_StartMsg = "StartMsg" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_EndMsg = "EndMsg" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Support = "Support" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_mfClassPath = "mfClassPath" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_DocFile = "DocFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CollectPermissions = "CollectPermissions" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_TranslatePathPart = "TranslatePathPart" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CommentTag = "CommentTag" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_SkipFile = "SkipFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_SkipDir = "SkipDir" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_DeleteBeforeWrite = "DeleteBeforeWrite" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PerInstance = "PerInstance" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AsInstallable = "AsInstallable" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AsApplication = "AsApplication" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AsInstalled = "AsInstalled" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_AsMetadata = "AsMetadata" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NoRestore = "NoRestore" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NoDelete = "NoDelete" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ForceReplace = "ForceReplace" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Chmod = "Chmod" ;
    // KH Defect PQ59675
 // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Chgrp = "Chgrp" ;
    // KH Defect PQ59675
 // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Chown = "Chown" ;
    // KH Defect PQ59675
 // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ChmodOnly = "ChmodOnly" ;
    // KH Defect PQ59675
 // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Include = "Include" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Exclude = "Exclude" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_IncludeCut = "IncludeCut" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ImportComponent = "ImportComponent" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Signature = "Signature" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_CkVersion = "CkVersion" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_UpdateFile = "UpdateFile" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_FindReplace = "FindReplace" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Rename = "Rename" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_NoBackUpJar = "NoBackUpJar" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PropAdd = "PropAdd" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PropSoftAdd = "PropSoftAdd" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PropDelete = "PropDelete" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PropSoftDelete = "PropSoftDelete" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PropUpdate = "PropUpdate" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_PropSoftUpdate = "PropSoftUpdate" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ReSequenceJar = "ReSequenceJar" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Cmd = "Cmd" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_UnCmd = "UnCmd" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_RCmd = "RCmd" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Class = "Class" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_UnClass = "UnClass" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ForceBackup = "ForceBackup" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_TargetMsg = "TargetMsg" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_UpdateZipEntry = "UpdateZipEntry" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ZipComment = "ZipComment" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_ZipExtra = "ZipExtra" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_WarProcessing = "WarProcessing" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_BuildSelfExtractor = "BuildSelfExtractor" ;
    // See also the 'macro' definitions, later in this file.

    // When specifying an entry 'asInstalled', a naming rule may be
    // used.  For example:
    //
    // asInstalled /uddi${dot}${cell}${dot}${node}.ear/*
    //
    // The ${dot} expands to '.'.
    // The ${slash} expands to a '/' or '\\', depending
    //     on the platform.
    // The ${cell} expands to a cell name.
    // The ${node} expands to a node name.
    // The ${server} expands to a server name.
    //
    // At least one of ${cell} or ${node} is required.
    //
    // No means of escaping values is currently provided.
    //
    // When a naming rule is provide, the following modifications
    // are made to processing:
    //
    // The entry having the name with null substitutions is matched
    // in the differences.  Following the example above, differences
    // matching the pattern, below, are marked:
    //
    // /uddi.ear/*
    //
    // During application, substitution is performed per cell and
    // per node, according to the naming rule.  (Iteration is performed
    // according to the extent of the naming rule: When cell and node
    // name patterns are provided, iteration is across both cell and
    // node names; When just a cell name is provided, iteration is
    // solely across cell names; When just a node name is provided,
    // iteration is solely across node names.
    //
    // The use of a naming rule does not change how backup information
    // is generated.  Backup information is stored without 'asInstalled'
    // and with the expanded names.
    //
    // The use of a naming rule does not change how metadata is processed.

    // Option keywords used on the command line and in the filterFile:

    public final static String o_Test = "Test" ;

    // The HistoryEventData class is used to share the information needed to build
    // the history\event in Product.XML

    protected static class XMLHistoryEventInfo
    {
        String  xmlFileName;
        boolean validating;
        boolean nameSpaceAware;
        boolean addHistory;

        String  sqlTime;
        String  description;
        String  type;                      // this is for temp or real eFix
        String  containerType;
        String  targetDirName;             // target of Update
        String  backupJarName;             // UnDo script
        String  logFileName;               // Activity Log
        String  startingVersion;
        String  endingVersion;
        String  deltaJarName;              // Source
        String  status;
        String  message;                   // error Message
        String  APAR;
        String  PMR;
        String  developer;
    }

    // Following are the valid values for the 'type' field
    // when adding a history event:

    public static final String PTF = "PTF" ;
    // Following are the valid values for the 'type' field
    // when adding a history event:

    public static final String eFix = "eFix" ;
    // Following are the valid values for the 'type' field
    // when adding a history event:

    public static final String diagnostic = "Diagnostic" ;
    // Following are the valid values for the 'type' field
    // when adding a history event:

    public static final String test = "Test" ;
    // Following are the valid values for the 'type' field
    // when adding a history event:

    public static final String other = "Other" ;
    // Following are the valid values for the 'type' field
    // when adding a history event:

    public static final String backout = "BackOut" ;

    public static final String[] validTypes = {
        PTF,
        eFix,
        diagnostic,
        test,
        other,
        backout
    };

    public static void displayValidTypes(Logger log)
    {
        log.Both(" Valid run types are:");

        for (int i=0; i < validTypes.length; i++ )
            log.Both("   "+ (i+1) +".  "+ validTypes[i]);
    }

    public static final String[] validProductFileTypes = {
        meXMLProductFile,
        mePropertiesProductFile
    };

    public static void displayValidProductFileTypes(Logger log)
    {
        log.Both(" Valid Product File Types are:");

        for (int i=0; i < validProductFileTypes.length; i++ )
            log.Both("   "+ (i + 1) +".  "+ validProductFileTypes[i]);
    }

    // HL.insert.java

    public static final String parenMarkers = "$()" ;
    // HL.insert.java

    public static final String angleMarkers = "?<>" ;
    // HL.insert.java

    public static final String braceMarkers = "${}" ;

    public static final String macroDateYYYYMMDD = "YYYYMMDD" ;
    public static final String macroDateSlashMMDDYYYY = "MM/DD/YYYY" ;
    public static final String macroDateSlashMMDDYY = "MM/DD/YY" ;
    public static final String macroDot = "dot" ;
    public static final String macroSlash = "slash" ;
    public static final String macroCell = "cell" ;
    public static final String macroNode = "node" ;
    public static final String macroServer = "server" ;

    public static final SimpleDateFormat formatMMDDYYYY = new SimpleDateFormat ( "MM/dd/yyyy" ) ;
    public static final SimpleDateFormat formatMMDDYY = new SimpleDateFormat ( "MM/dd/yy" ) ;
    public static final SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat ( "yyyyMMdd" ) ;

    public static final String
        macroDotValue = ".";

    protected static final String slash =
        System.getProperty("file.separator");

    public static final int CELL_NAME_OFFSET = 0 ;
    public static final int NODE_NAME_OFFSET = 1 ;
    public static final int SERVER_NAME_OFFSET = 2 ;

    public static class MacroValues {
        public String dotValue;
        public String slashValue;

        public String[] serverJoin;
    }

    public static MacroValues defaultMacroValues;

    static {
        defaultMacroValues = new MacroValues();

        defaultMacroValues.dotValue   = "";
        defaultMacroValues.slashValue = "";

        defaultMacroValues.serverJoin = new String[] { "", "", "" };
    }

    public static MacroValues fullMacroValues;

    static {
        fullMacroValues = new MacroValues();

        fullMacroValues.dotValue   = macroDotValue;
        fullMacroValues.slashValue = slash;

        defaultMacroValues.serverJoin = new String[] { "", "", "" };
    }

    public static boolean HasMacro(String markers, String line)
    {
        if ( !markers.equals(parenMarkers) &&
             !markers.equals(angleMarkers) &&
             !markers.equals(braceMarkers) ) {
            return false;
        }

        String
            headMarker = markers.substring(0, 2),
            tailMarker = markers.substring(2);

        int headPos = 0,
            tailPos = 0;

        return ( ((headPos = line.indexOf(headMarker, headPos)) != -1) &&
                 ((tailPos = line.indexOf(tailMarker, headPos)) != -1) );
    }

    public static String ResolveMacro(String markers,
                                      String line, int lineNum, String fileName)
    {
        return ResolveMacro(markers,
                            line, lineNum, fileName,
                            defaultMacroValues);
    }

    public static String ResolveMacro(String earEntry, String[] serverJoin)
    {
        fullMacroValues.serverJoin = serverJoin;

        return ResolveMacro(braceMarkers,
                            earEntry, 1, "** EAR NAME RULE **",
                            fullMacroValues);
    }

    public static String ResolveMacro(String markers,
                                      String line, int lineNum, String fileName,
                                      MacroValues values)

    {
        if ( !markers.equals(parenMarkers) &&
             !markers.equals(angleMarkers) &&
             !markers.equals(braceMarkers) ) {
            log.Err(39, "Non-valid macro markers: " + markers);
            return line;
        }

        String
            headMarker = markers.substring(0, 2), // e.g. "${"
            tailMarker = markers.substring(2);    // e.g. "}"

        int
            headLength = headMarker.length(), // Should be 2
            tailLength = tailMarker.length(); // Should be 1

        int headPos = 0,
            tailPos = 0;

        while ( ((headPos = line.indexOf(headMarker, headPos)) != -1) &&
                ((tailPos = line.indexOf(tailMarker, headPos)) != -1) ) {

            String macroName =
                line.substring(headPos + headLength, tailPos);

            String macroValue =
                lookupMacro(macroName,
                            lineNum, headPos, fileName,
                            values);

            line = line.substring(0, headPos) +
                   macroValue +
                   line.substring(tailPos + tailLength);

            headPos = headPos + macroValue.length();
        }

        return line;
    }

    public static String lookupMacro(String macroName,
                                     int lineNum, int headPos, String fileName,
                                     MacroValues values)
    {
        String macroValue;

        if ( macroName.equals(macroDateSlashMMDDYYYY) ) {
            macroValue = formatMMDDYYYY.format( new Date() );
        } else if ( macroName.equals(macroDateSlashMMDDYY) ) {
            macroValue = formatMMDDYY.format( new Date() );
        } else if ( macroName.equals(macroDateYYYYMMDD) ) {
            macroValue = formatYYYYMMDD.format( new Date() );

        } else if ( macroName.equals(macroDot) ) {
            macroValue = values.dotValue;
        } else if ( macroName.equals(macroSlash) ) {
            macroValue = values.slashValue;

        } else if ( macroName.equals(macroCell) ) {
            macroValue = values.serverJoin[CELL_NAME_OFFSET];
        } else if ( macroName.equals(macroNode) ) {
            macroValue = values.serverJoin[NODE_NAME_OFFSET];
        } else if ( macroName.equals(macroServer) ) {
            macroValue = values.serverJoin[SERVER_NAME_OFFSET];

        } else {
            macroValue = System.getProperty(macroName);

            if ( macroValue == null ) {
                log.Err(38, "Unresolved macro ( " + macroName + " )" +
                            " found at ( line, offset )" +
                            " ( " + lineNum + ", " + headPos + " )" +
                            " within ( " + fileName + " ).");

                macroValue = macroName;
            }
        }

        // log.Both("Macro:      ( " + macroName  + " )");
        // log.Both("Resolution: ( " + macroValue + " )");

        return macroValue;
    }

    // Here we have a relative path that matched a coarse file
    // category entry, that entry having a naming rule.  The naming
    // rule must be embedded into the finer relative path.
    //
    // For example:
    //     Name Rule: \applications\myEar${dot}${node}${dot}${server}.ear\*
    //     Relative Path: \applications\myEar.ear\myWar.war\myFile.xml
    //
    //     Corrected Path: \applications\myEar${dot}${node}${dot}${server}.ear\myWar.war\myFile.xml

    public static final String
        entrySlashText = "/";

    public static final String deltaInstallablePrefix = "/installable/" ;
    public static final String deltaApplicationsPrefix = "/applications/" ;
    public static final String deltaInstalledPrefix = "/" ;

    public static String encodeNameRule(String nameRule, String relativePath)
    {
        int prefixLen;

        if ( nameRule.startsWith(deltaInstallablePrefix) )
            prefixLen = deltaInstallablePrefix.length();
        else if ( nameRule.startsWith(deltaApplicationsPrefix) )
            prefixLen = deltaApplicationsPrefix.length();
        else
            prefixLen = deltaInstalledPrefix.length();

        int ruleSuffixPos = nameRule.indexOf(entrySlashText, prefixLen);
        if ( ruleSuffixPos == -1 ) {
            log.Both("Error: malformed name rule: " + nameRule);
            return null;
        }

        int pathSuffixPos = relativePath.indexOf(entrySlashText, prefixLen);
        if ( pathSuffixPos == -1 ) {
            log.Both("Error: malformed ruled path: " + nameRule);
            return null;
        }

        String pathWithRule =
            relativePath.substring(0, prefixLen) +
            nameRule.substring(prefixLen, ruleSuffixPos) +
            relativePath.substring(pathSuffixPos);

        log.Both("Adjusting path: " + relativePath);
        log.Both("  Name Rule : " + nameRule);
        log.Both("  Ruled Path: " + pathWithRule);

        return pathWithRule;
    }

    public void displayMessage(Logger log, String message, String delimiter)
    {
        StringTokenizer toks = new StringTokenizer(message, delimiter);

        log.Both("");

        while ( toks.hasMoreTokens() ) {
            String msgLine = toks.nextToken();
            log.Both(msgLine);
        }

        log.Both("");
    }

    // Selection constants -- used to locate portions of
    // a WebSphere product file.  A WebSphere product file
    // is in XML format.  The selection constants each
    // describe a path into the product file.

    // The following are displayed in the extractor help text.
    // Update that text if these are changed.

    public static final String[] versionHierarchy = {
        "#document", "websphere", "appserver", "version"
    };

    public static final String[] editionNameHierarchy = {
        "#document", "websphere", "appserver", "edition", "name"
    };

    public static final String[] editionValueHierarchy = {
        "#document", "websphere", "appserver", "edition", "value"
    };

    public static final String[] buildNumberHierarchy = {
        "#document", "websphere", "appserver", "build", "number"
    };

    public static final String[] buildDateHierarchy = {
        "#document", "websphere", "appserver", "build", "date"
    };

    public static final String[] eventHierarchy = {
        "#document", "websphere", "appserver", "history", "event"
    };

    public static String[] parseHierarchy(String hierarchy)
    {
        Vector tokens = new Vector();

        StringTokenizer tokenizer = new StringTokenizer(hierarchy, ".");

        while ( tokenizer.hasMoreTokens() ) {
            String nextToken = tokenizer.nextToken();
            tokens.add(nextToken);
        }

        int numTokens = tokens.size();

        String[] results = new String[numTokens];

        for ( int tokenNo = 0; tokenNo < numTokens; tokenNo++ )
            results[tokenNo] = (String) tokens.elementAt(tokenNo);

        return results;
    }

    public static boolean validateProductFileTypes(String theType)
    {
        for ( int i = 0; i < validProductFileTypes.length; i++ ) {
            if ( theType.equals(validProductFileTypes[i]))
                return true;
        }

        return false;
    }

    // Comments on processing the command, extra data, and time fields
    // of jar and zip entries:
    //
    // When doing an update of a jar entry, if the comment or extra data
    // has changed, then it must be provided with the updated entry.  When
    // the comment or extra data was changed to null, a flag must be
    // specified in the entry's attributes, either meRemoveJarComment or
    // meRemoveJarExtra.
    //
    // When comparing jar entries, the contents of the jar entries may be
    // unchanged while the comment or extra data has changed.  When this is
    // the case, specify 'meIgnorejarContent', and provide a one byte dummy
    // contents value.
    //
    // Special Delta Options:
    //
    // An option exists to specify a single file which is to be added to an
    // existing jar (as opposed to entries which are picked up by comparing
    // the current and new jar files).  The option is 'getJarEntry'.  The
    // 'getJarEntry' must be able to specify up comment, extra info, and
    // time values which are to be included in the update.
    //
    // A new option is required, 'updateJarEntry', similar to
    // 'getJarEntry', which allows new comment, extra info, or time values
    // to be specified, but which operates on an existing jar entry.
    //
    // During extraction:
    //
    // No special steps are needed when creating the backup jar, as jar
    // entries are not handled individually -- each jar what is updated is
    // stored entirely in the backup jar.
    //
    // When adding a jar entry:
    //
    //     In addition to the contents, take the comment, extra
    //     information, and time of the new jar entry.
    //
    // When removing a jar entry:
    //
    //     Simply remove that entry.
    //
    // When adding a jar entry:
    //
    //     Take the time, comments, extra data, and contents
    //     of the jar entry.
    //
    // When updating a jar entry:
    //
    //     Take the time of the update entry.
    //
    //     Take the contents of the jar entry, unless 'meIgnoreJarContent'
    //     has been specified, in which case the  contents of the jar
    //     entry have not changed and must not be updated.
    //
    //     If the comment of the update entry is not null,
    //     take that comment.
    //
    //     If the comment of the update entry is null, ignore
    //     it unless 'meRemoveJarComment' is specified.  When
    //     'meRemoveJarComment' is specified the comment of
    //     the updated entry must be cleared.
    //
    //     If the extra information of the update entry is
    //     not null, take that extra information.
    //
    //     If the extra information of the update entry is null,
    //     ignore it unless 'meRemoveJarExtra' is specified.
    //     When 'meRemovejarExtra' is specified the extra
    //     information of the updated entry must be cleared.

    // Comments on handling version information, both for prerequisite
    // checking, and for updating the product file.
    //
    // Version information is used as follows:
    //
    // The product file will have an initial version, edition name,
    // edition value, build number, and build date.
    //
    // The product file may have different final values for the version,
    // build number, and build date.
    //
    // The product version, edition name, and edition value may be
    // subject to a prerequisite check.  While performing these
    // checks the initil version, edition name, and edition value
    // may be loaded.
    //
    // The product version, edition name, and edition value may be
    // loaded during prerequisite checking.
    //
    // When building the backup jar, if version checking is enabled,
    // then the same check must be signalled in the backup jar,
    // and if the version will be updated, then the final version
    // must be the version that is checked.
    //
    // When building the backup jar, if edition name checking is
    // enabled, then the same check must be signalled in the backup
    // jar.
    //
    // When building the backup jar, if edition value checking is
    // enabled, then the same check must be signalled in the backup
    // jar.
    //
    // When building the backup jar, if the version is being updated,
    // then a update must be placed in the backup jar to effect a
    // restoration of the initial version.
    //
    // When building the backup jar, if the build number is being
    // updated, then an update must be placed in the backup jar to
    // effect a restoration of the initial build number.
    //
    // When building the backup jar, if the build date is being
    // updated, then an update must be placed in the backup jar to
    // effect a restoration of the initial build number.

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
}
