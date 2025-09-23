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

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

/*
 * Delta -- Encode a file system difference.
 *
 * History 1.6, 3/23/06
 *
 * 26-Nov-2002 Added support for asInstalled, asApplication,
 *             and asInstallable updates.
 *
 *             Added support for perInstance updates.
 *
 *             Added support for AddOnly and ReplaceOnly.
 *
 *             Changed mapping type from Hashtable to HashMap.
 *
 *             Updated for FilterFile.FileCategory inner class.
 *
 *             Added wildcard support for deleteBeforeWrite,
 *             addOnlyFiles, perInstance, replaceOnlyFiles,
 *             asInstallable, asApplication, and asInstalled.
 *
 * 20-Dec-2002 Removed System.exit()
 *
 */

// TBD  check main attribs
// TBD  Triger jar entry copy if attribs are different
// TBD  Handle the deletion of directories
// TBD  Handle directory entries within jar files
// TBD  Add debug option to pretend we are on a different platform
// TBD  Ensure we are not reading our own output file, will loop forever

// TBD  let the newTree be a semicoma delimited list

// In -getfile add wild cards
//    -getNewFile
//    -GetReplaceFile
//    find some way to allow to delete a file or class

/**
 *  
 */
public class Delta
{
    public static final String pgmVersion = "1.6" ;
    public static final String pgmUpdate = "3/23/06" ;

    protected static final String
        slash = System.getProperty("file.separator");
    
    protected static final String earApplicationsDir = "/" + HelperList . earApplicationsDir ;
    protected static final String earInstallableDir = "/" + HelperList . earInstallableDir ;

    protected static HelperList         hl  = null;
    protected static Helper1            hc  = null;
    protected static Logger             log = null;
    protected static POProcessor        po  = null;
    protected static DeltaByteGenerator DBG = null;
    protected static FilterFile         ff  = null;

    // The url of the input jar file, if its coming from one.

    protected static URL url = null;

    protected static String inputJarFileName = null;
    protected static int verbosity;
    protected static boolean debug;
    protected static boolean fTest;

    protected static HashMap extCounts = new HashMap(50);

    protected static HashMap macs = new HashMap(10);

    // we can also do this to get the location of the efixDriver/ptfDriver file, passing in from the build.xml
    // <jvmarg value="-DefixDriver=${EFIX_DRIVER_FILE}"/>
    protected static final String efixDriverArg = System.getProperty("efixDriver");
    //    /com/ibm/websphere/update/harness/dtdFiles/applied.xsd
    //    protected static final String xsdFile = "/com/ibm/websphere/update/harness/dtdFiles/applied.xsd";

    //    /com/ibm/websphere/update/harness/dtdFiles/applied.dtd
    protected static final String dtdFile = "/com/ibm/websphere/update/harness/dtdFiles/applied.dtd";

    protected static class FileDesc
    {
        File theFile;
        String signatures;
        String componentName;
    }

    // Padding values:

    protected static final int pV1 = 18 ;
    // Padding values:

    protected static final int pV2 = 12 ;
    // Padding values:

    protected static final int pV3 = 12 ;
    // Padding values:

    protected static final int pV4 = 12 ;

    // File set keys:

    protected static final int k_Old = 0 ;
    // File set keys:

    protected static final int k_New = 1 ;

    protected static long[]    totFiles     = new long[2];
    protected static long[]    totBytes     = new long[2];
    protected static long[]    totJars      = new long[2];
    protected static long[]    totJBytes    = new long[2];

    protected static HashMap   files2delete  = new HashMap();
    protected static long      bytes2delete  = 0;
    protected static long      je2delete     = 0;
    protected static long      jebytes2del   = 0;

    protected static HashMap   files2add     = new HashMap();
    protected static long      bytes2add     = 0;
    protected static long      je2add        = 0;
    protected static long      jebytes2add   = 0;

    protected static long      filesPerInstance   = 0; // Configuration instance file counts

    protected static long      filesAsInstallable = 0; // EAR directive file counts
    protected static long      filesAsApplication = 0;
    protected static long      filesAsInstalled   = 0;
    protected static long      filesAsMetadata    = 0;
    protected static long      filesAsNameRule    = 0;

    protected static long      files2replace = 0;
    protected static long      bytes2replace = 0;
    protected static long      exceeded      = 0;  // number of times ByteDelta length exceeded maxSizePct
    protected static long      bytes2send    = 0;  // number of bytes actually
    protected static long      je2replace    = 0;
    protected static long      jebytes2rep   = 0;
    protected static long      jeexceeded    = 0;  // number of times ByteDelta length exceeded maxSizePct
    protected static long      jebytes2send  = 0;  // number of bytes actually

    protected static int       errCount      = 0;
    protected static int       sizeDelta     = 0;
    protected static int       sizeSame      = 0;
    protected static int       contentDelta  = 0;

    //  Expected a child entry name of:
    //    entry.name               -- a simple file
    //    jar.name!!entry.name     -- a child jar file entry
    //    jar.name!!!!             -- a child jar main manifest entry
    //    jar.name!!!!entry.name   -- a child jar manifest entry

    // This is used as a delimiter that would not occur in nature.
    // we also use this in StringTokenizer to collect the remainder

    protected static final String childDividor = "\u0013";

    // Argument constants, used to indicate the type of a change:

    protected static final boolean noManEntry = false ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean manEntry = true ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean deleteEntry = true ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean addEntry = false ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean relPath = false ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean absolutePath = true ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean noAbsolutePath = false ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean addOnly = true ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean noAddOnly = false ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean replaceOnly = true ;
    // Argument constants, used to indicate the type of a change:

    protected static final boolean noReplaceOnly = false ;

    protected static final File NullBDTempFile = null;

    // Operation keys for FileEntry.action:

    protected static final int k_Delete = 1 ;
    // Operation keys for FileEntry.action:

    protected static final int k_Add = 2 ;
    // Operation keys for FileEntry.action:

    protected static final int k_Update = 3 ;
    // Operation keys for FileEntry.action:

    protected static final int k_JarEntry = 5 ;
    // Operation keys for FileEntry.action:

    protected static final int k_ChmodOnly = 6 ;

    protected static class FileEntry
    {
        JarFile    jarFile;
        JarEntry   jarEntry;
        File       flatFile;
        Attributes attribs;
        long       zipTimeStamp;
        String     zipComment;
        String     components;    // One or more comma delimited component Names this entry belongs to
        byte[]     zipExtra;
        int        permissions;   // the octal unix permissions drwxrwxrwx - User - Group - World
        boolean    permisAreSet;  // true if the permissions fiels has been set
        String     relativePath;  // actual path with the given part stripped off
        boolean    delete;        // denotes to delete this file or jar entry
        boolean    manifestOnly;  // to denote a manifest only entry
        boolean    firstJarFile;  // to denote the first of a series jar file entrys
        boolean    deleteBeforeWrite; // if to delete the file before updating it
        boolean    perInstance;   // if updated in each configuration instance
        boolean    asInstallable; // if within an installable EAR
        boolean    asApplication; // if within an application EAR
        boolean    asInstalled;   // if within an installed EAR
        boolean    asMetadata;    // if metadata within an installed EAR
        String     nameRule;      // naming rule for an installed or metadata file
        boolean    noDelete;      // do not delete this file - even during restore operations
        boolean    dummyEntry;    // for use with zipUpdate Function
        boolean    addOnly;       // If this is from an addFile option
        boolean    replaceOnly;   // If this is from a ReplaceFile Option
        String     chmod;         // contains the chmod value to be applied by the Extractor
        long       fileSize;
        int        action;        // the action that put this entry here
        File       bdTempFile;    // this the output from the ByteDelta comparator
        boolean    absPath;       // if this file is an absolute path

        protected FileEntry()
        {
            jarFile           = null;
            jarEntry          = null;
            flatFile          = null;
            attribs           = null;
            zipTimeStamp      = 0;
            zipComment        = null;
            zipExtra          = null;
            permissions       = 0;      // the octal unix permissions drwxrwxrwx - User - Group - World
            permisAreSet      = false;
            relativePath      = null;   // actual path with the given part stripped off
            delete            = false;  // denotes to delete this file or jar entry
            manifestOnly      = false;  // to denote a manifest only entry
            firstJarFile      = false;  // to denote the first of a series jar file entrys
            deleteBeforeWrite = false;  // if to delete the file before updating it

            perInstance       = false;  // If using configuration instance processing
            asInstallable     = false;  // If using EAR processing
            asApplication     = false;
            asInstalled       = false;
            asMetadata        = false;
            nameRule          = null;

            noDelete          = false;  // do not delete this file - even during restore operations
            chmod             = null;   // contains the chmod value to be applied by the Extractor
            fileSize          = 0;
            action            = 0;      // the action that put this entry here
            bdTempFile        = null;   // this the output from the ByteDelta comparator
            absPath           = false;  // if this files is an absolute path
            dummyEntry        = false;
            addOnly           = false;
            replaceOnly       = false;
            components        = null;
        }

        protected String actionName()
        {
            if ( action == k_Add )
                return "Add";
            else if ( action == k_Delete )
                return "Delete";
            else if ( action == k_Update )
                return "Update";
            else
                return "unKnown";
        }

        protected void AddAttrib(String key, String value)
        {
            if ( attribs == null )
                attribs = new Attributes();

            attribs.putValue(key, value);
        }

        public static final String NONE = "**none**" ;
        public static final String PRESENT = "**present**" ;

        protected void display()
        {
            log.BothRaw("");

            log.Both("jarFile          : " + ((jarFile == null)      ? NONE : jarFile.toString()));
            log.Both("jarEntry         : " + ((jarEntry == null)     ? NONE : jarEntry.toString()));
            log.Both("attributes       : " + ((attribs == null)      ? NONE : PRESENT));
            log.Both("relativePath     : " + ((relativePath == null) ? NONE : relativePath));
            log.Both("absolutePath     : " + absPath );
            log.Both("fileSize         : " + Long.toString(fileSize));

            log.Both("delete           : " + delete);
            log.Both("manifestOnly     : " + manifestOnly);
            log.Both("firstJarFile     : " + firstJarFile);
            log.Both("deleteBeforeWrite: " + deleteBeforeWrite);
            log.Both("noDelete         : " + noDelete);
            log.Both("chmod            : " + ((chmod == null)        ? NONE : chmod));

            log.Both("perInstance      : " + perInstance);
            log.Both("asInstallable    : " + asInstallable);
            log.Both("asApplication    : " + asApplication);
            log.Both("asInstalled      : " + asInstalled);
            log.Both("asMetadata       : " + asMetadata);
            log.Both("nameRule         : " + ((nameRule == null)     ? NONE : nameRule));

            log.Both("action           : " + action);
            log.Both("bdTespFile       : " + ((bdTempFile == null)   ? NONE : bdTempFile.toString()));
        }
    }

    protected static HashMap outJarTab = new HashMap();      // Path -> FileEntry

    protected static HashMap oldFiles = new HashMap(8000);   // will contain all files
    protected static HashMap oldDirs  = new HashMap();       // will contain all directories

    protected static HashMap newFiles = new HashMap(8000);   // will contain all files
    protected static HashMap newDirs  = new HashMap();       // will contain all directories

    protected static final boolean abend = true ;
    protected static final boolean isHelper = true ;
    protected static final boolean isNotHelper = false ;

    protected static boolean f1Time = true;

    protected static int exitRC = 0;

    public static void main(String argv[])
    {
        Delta delta = new Delta();
        
		int rc = delta.process(argv);
    }

    public int process(String argv[] )
    {
        long startTime = System.currentTimeMillis();

        // Note: The following four HashTables are populated by the FindFiles method.
        //   in that form the key is the partial path with all slashes replaced by
        //   astericks, and the value is a java File object. We will replace the
        //   File object with the FileDesc object in the verifyList method.

        hl = new HelperList();

        log = new Logger("Delta.Log", true, verbosity);
        log.Open();

        hc = new Helper1(log, 3);

        if ( !ProcessOptions(argv) )
            return(8);

        if ( ff.getShowOptions() )
            showOptions();

        if ( ff.getGenBD() )
            DBG = new DeltaByteGenerator(log, verbosity, debug,
                                         ff.getChunckSize(),
                                         ff.getReSyncLen(),
                                         ff.getReSyncScan(),
                                         ff.getMaxSizePct());

        seedHelpers();      // seed the transferFile hashtables with the helper files
        processPackages();  // enter the package files, if any, into the transferFile hashtable

        // Did we specified a target and a getFile?

        if ( ff.geteFixFilesCount() > 0 )
            record_eFixFiles();

        // } else {  // here we do normal PTF Processing

        log.Both("Inspecting " + ff.getOldTree());

        boolean lDebug = false;

        if ( ff.getOldTree() != null ) {
            hc.FindFiles(ff.getOldTree() + slash + "*",
                         ff.getRecurse(),
                         oldFiles, oldDirs,
                         ff.getCaseSensitive(),
                         lDebug);

        } else {
            ff.setOldTree("");
        }

        log.Both("Inspecting " + ff.getNewTree());

        if ( ff.getNewTree() != null ) {
            hc.FindFiles(ff.getNewTree() + slash + "*",
                         ff.getRecurse(),
                         newFiles, newDirs,
                         ff.getCaseSensitive(),
                         lDebug);

        } else {
            ff.setNewTree("");
        }

        log.Both(hc.Padit(" ", pV1 + 1, false) +
                 hc.Padit("oldTree", pV2, false) +
                 hc.Padit("newTree", pV3, false));

        log.Both(hc.Padit("Directories", pV1, true) + ":" +
                 hc.FmtNum(oldDirs.size(), 0, 12, 0) +
                 hc.FmtNum(newDirs.size(), 0, 12, 0));

        log.Both(hc.Padit("Files found", pV1, true) + ":" +
                 hc.FmtNum(oldFiles.size(), 0, 12, 0) +
                 hc.FmtNum(newFiles.size(), 0, 12, 0));

        if ( !VerifyList(oldFiles, k_Old) ) {
            // log.Close();
            // return(8);
        }

        if ( !VerifyList(newFiles, k_New) ) {
            // log.Close();
            // return(8);
        }

        // Apply force updates:

        Iterator eNum = ff.getForceReplaceEnumKey();
        while ( eNum.hasNext() ) {
            String key  = (String) eNum.next();

            String frText = "ForceReplace of [" + key + "]";

            FilterFile.HTEntry hte = ff.getForceReplace(key);

            boolean inOldTree = oldFiles.containsKey(key);
            boolean inNewTree = newFiles.containsKey(key);

            if ( !inOldTree && inNewTree ) {
                log.Both(frText + " (would have been added).");

                hte.increment(k_New);
                hte.vData = ff.getNewTree();

            } else if ( inOldTree && !inNewTree ) {
                // TFB: ? Does this make sense?

                log.Both(frText + " (would have been removed).");

                File file = (File) oldFiles.get(key);
                oldFiles.remove(key);
                newFiles.put(key, file);

                hte.increment(k_Old);
                hte.vData = ff.getOldTree();

            } else if ( inOldTree && inNewTree ) {
                log.Both(frText + " (might have been replaced).");

                oldFiles.remove(key);

                hte.increment(k_New);
                hte.vData = ff.getNewTree();

            } else {
                Err(24, frText + " failed; (file is nowhere available).");
            }
        }

        // See ShowProgress class for use of this variable:

        long[] counts = { ff.getFrequencyUpdate(), 0, oldFiles.size() };

        ShowProgress sp = new ShowProgress("{0} processing file {1} of {2}  {3}% complete", counts);
        sp.setDaemon(true);
        sp.start();

        log.Log("Scanning old tree.");

        eNum = oldFiles.keySet().iterator();

        while ( eNum.hasNext() ) {
            counts[1]++;

            String key1 = (String) eNum.next();

            log.Log("Scanning old file: " + key1);

            FileDesc fd1 = (FileDesc) oldFiles.get(key1);

            boolean fMatched = false;

            CountExt(key1, k_Old, fd1.theFile);

            if ( newFiles.containsKey(key1) ) {
                log.Log("File is present in the new tree.");

                fMatched = true;
                FileDesc fd2 = (FileDesc) newFiles.get(key1);

                // Do the files have different sizes or different contents?

                if (Compare(fd1.theFile, fd2.theFile) != 0) {
                    log.Log("Noted difference");

                    File bdTempFile = null;
                    boolean equal = false;                   
                    String lcKey = key1.toLowerCase();

                    if ( ff.getWarProcessing() && isArchive(lcKey) ) {
                        log.Log("Performing archive comparison.");
                        
                        equal = compareArchive(fd1.theFile, fd2.theFile);
                        
                       /* if ( equal )
                        {
                            log.Log( "Archives are equal, removing from new file list: " + key1 );
                            newFiles.remove( key1 );
                        }
                        else
                        {
                            FileEntry fe = RecordChange(k_Update,
                                    					null,
                                    					null,
                                    					fd2.theFile,
                                    					false,
                                    					ff.getNewTree(),
                                    					false,
                                    					noManEntry,
                                    					null,
                                    					relPath);
                        }*/
                    }
                    
                    if ( ff.getParseJar() && isArchive(lcKey) ) {
                        log.Log("Handling as an archive.");

                        CompareJar(fd1.theFile, fd2.theFile, fd1.componentName);

                    } else if ( !equal ){
                        log.Log("Handling as a flat file.");

                        files2replace++;
                        bytes2replace += fd2.theFile.length();

                        if ( ff.getGenBD() ) {
                            int ciCount = -98;  // seed ciCount with an invalid value

                            try {
                                bdTempFile = File.createTempFile("BD_Temp", null);
                                bdTempFile.deleteOnExit();

                                ciCount = DBG.Generate(new BufferedInputStream(new FileInputStream(fd1.theFile),
                                                                               ff.getChunckSize()),
                                                       fd1.theFile.length(),
                                                       new BufferedInputStream(new FileInputStream(fd2.theFile),
                                                                               ff.getChunckSize()),
                                                       fd2.theFile.length(),
                                                       new FileOutputStream(bdTempFile));

                            } catch ( FileNotFoundException ex ) {
                                log.Err(59, "File Not Found: " , ex);
                                log.Close();

                                return(8);

                            } catch ( IOException ex ) {
                                log.Err(60, "IOException: " , ex);
                                log.Close();

                                return(8);
                            }

                            if ( ciCount == -1 ) {
                                log.Err(10, "GenerateByteDelta failed");
                                log.Close();

                                return(8);

                            } else if ( ciCount == 0 ) {
                                log.Log("   ByteDelta changes exceeded new file size threshold of " +
                                            ff.getMaxSizePct() + "%");

                                exceeded++;
                                bytes2send += fd2.theFile.length();
                                bdTempFile = null;

                            } else if ( ciCount == 1 ) {
                                log.Err(64, " Old and New files are identical, but they wern't a moment ago");
                                log.Close();

                                return(8);

                            } else if ( ciCount > 1 ) {
                                long pct = -1;

                                if ( fd2.theFile.length() > 0 )
                                    pct = (bdTempFile.length() * 100) / fd2.theFile.length();

                                log.Log("   " + ciCount +
                                        " changeItems generated ciSize=" + bdTempFile.length() +
                                        " FileSize=" + fd2.theFile.length() + " " + pct + " %");

                                bytes2send += bdTempFile.length();
                            }

                        } else {
                            bytes2send += fd2.theFile.length();
                        }

                        // Record that the two flat files are different:

                        FileEntry fe = RecordChange(k_Update,
                                                    null,
                                                    null,
                                                    fd2.theFile,
                                                    false,
                                                    ff.getNewTree(),
                                                    false,
                                                    noManEntry,
                                                    bdTempFile,
                                                    relPath);

                        if ( fd2.componentName != null )
                            fe.components = fd2.componentName;
                    }
                }

                CountExt(key1, k_New, fd2.theFile);
                newFiles.remove(key1);

            } else {
                // Have a file in oldTree but no matching file in newTree.
                // Schedule the file for deletion;

                log.Log("File is absent from the new tree.");

                files2delete.put(key1, fd1.theFile);
                bytes2delete += fd1.theFile.length();

                // Record that a file, flat or jar, has been deleted:

                FileEntry fe = RecordChange(k_Delete,
                                            null,
                                            null,
                                            fd1.theFile,
                                            true,
                                            ff.getOldTree(),
                                            false,
                                            noManEntry,
                                            NullBDTempFile,
                                            relPath);

                if ( fd1.componentName != null )
                    fe.components = fd1.componentName;
            }
        }

        oldFiles = new HashMap();

        log.Log("Scanning new tree.");

        eNum = newFiles.keySet().iterator();

        while ( eNum.hasNext() ) {
            String key2 = (String) eNum.next();

            log.Log("Scanning new file: " + key2);

            FileDesc fd2 = (FileDesc) newFiles.get(key2);

            String rootPath = ff.getNewTree();
            String msg = "Add (" + fd2.componentName + ") ";

            // this is to address when we have a forceReplace and we have moved it from the old tree
            if ( ff.forceReplace.containsKey(key2) ) {
                FilterFile.HTEntry hte = ff.getForceReplace(key2);
                rootPath = hte.vData;
                msg = "Addxo  ";   // show we are adding a crossover file
            }

            log.Log(msg + key2);

            files2add.put(key2, fd2.theFile);
            bytes2add += fd2.theFile.length();

            CountExt(key2, k_New, fd2.theFile);

            // Record that a file, flat or jar, has been added:

            FileEntry fe = RecordChange(k_Add,
                                        null,
                                        null,
                                        fd2.theFile,
                                        false,
                                        rootPath,
                                        false,
                                        noManEntry,
                                        NullBDTempFile,
                                        relPath);

            if ( fd2.componentName != null )
                fe.components = fd2.componentName;

        }

        // }

        log.Log("Scanning transfer files:");

        eNum = ff.getTransferFileEnumKey();    // add the special files to xfer

        while ( eNum.hasNext() ) {
            String srcFile  = (String) eNum.next();

            log.Log("Scanning transfer file: " + srcFile);

            FilterFile.HTEntry hte = ff.getTransferFile(srcFile);
            hte.increment(k_New);

            FileEntry fe = RecordChange(k_Add,
                                        null,
                                        null,
                                        new File(ResolveMacro(srcFile, 0)),
                                        false,
                                        "",
                                        false,
                                        false,
                                        new File(hte.vData),
                                        hte.absPath);

            if ( fe != null ) {  // we have some more updates to the FileEntry
                fe.addOnly     = hte.addOnly;
                fe.replaceOnly = hte.replaceOnly;
            }
        }

        processZipUpdates();  // process the zipUpdateEntries

        log.Both("Entries in output Jar " + outJarTab.size());

        if ( ff.getJarName() == null ) {
            log.Both("Output jar file name is null,  jar file creation bypassed.");
        } else {
            processImportComponents();
            processRemainingChmods();
            BuildJarFile();
        }

        log.Both(" ");

        DisplayFF(oldDirs, newDirs);   // print the Filter File Report and the extension report

        // at this point we wish to ensure the new Jar we built is Good
        if ( ff.getBuildSelfExtractor() )
            testNewJar("-Version");

        if ( parserRequired() )
            testNewJar("-ShowXMLVersion");

        if ( fTest ) {
            log.Both(" SizeDelta     = " + sizeDelta);
            log.Both(" Same          = " + sizeSame);
            log.Both(" contentDelta  = " + contentDelta);
        }

        log.Both(" ");

        if ( ff.getProductFileType() != null ) {
            log.Both("Product File Type    : " + ff.getProductFileType());
            log.Both("Product File Name    : " + ff.getProductFileName());
            log.Both("Product File VKey    : " + ff.getProductFileVKey());
            log.Both("Product Build Number : " + ff.getNewBuildNumber());
            log.Both("Product Build Date   : " + ff.getNewBuildDate());
            log.Both("Edition check Value  : " + ff.getCkEditionValue());
            log.Both("Edition check Name   : " + ff.getCkEditionName());
            log.Both("Version check value  : " + ff.getCkVersion());
        }

        log.Both("Size check value    : " + hc.FmtNum(ff.getCkSize(), 0, 0) + " Bytes");

        if ( ff.getCheck4Class() == null )
            log.Both("CheckForClass       : None");
        else
            log.Both("CheckForClass       : " + ff.getCheck4Class());

        if ( ff.getAffectedComponents() == null ) {
            log.Both("Affected Component  : None Specified");
        } else {
            String heading = "Affected Component  : ";
            StringTokenizer toks = new StringTokenizer(ff.getAffectedComponents(), ",");
            while ( toks.hasMoreTokens() ) {
                String nextToken = toks.nextToken();
                log.Both( heading + nextToken );
                heading = "                    : ";
            }

        }

        log.Both("Log File            : " + ff.getLogFileName());
        log.Both("Minor errors = " + errCount);
        log.Both("RC=" + exitRC + ",  Elapsed Time " + hc.CalcET(startTime));
        log.Both(" ");
        log.Close();

        return(exitRC);
    }

    void showOptions()
    {
        if ( ff.getComponentsSize() > 0 ) {
            log.Both("Components Identified:");

            int maxLen = 0;

            Iterator eNum = ff.getComponentsEnumKey();
            while ( eNum.hasNext() ) {
                String componentName = (String) eNum.next();
                if ( componentName.length() > maxLen )
                    maxLen = componentName.length();
            }

            eNum = ff.getComponentsEnumKey();

            while ( eNum.hasNext() ) {
                String componentName = (String) eNum.next();
                log.Both("   " + hc.Padit(componentName, maxLen, true) + "  " +  ff.getComponents(componentName));
            }

        }

        if ( ff.getRestoreOnlyCount() == 0 ) {
            log.Both("No RestoreOnly entries specified.");

        } else {
            for (int i = 0; i < ff.getRestoreOnlyCount(); i++ ) {

                log.Both( ff.getRestoreOnlyCount() + " RestoreOnly entries were specified.");

                FilterFile.HTEntry lhte = ff.getRestoreOnly(i);

                log.Log("    " + lhte.key);
                log.Log("             to");
                log.Log("    " + lhte.vData);

                if ( lhte.requiredVersion != null )
                    log.Log("    " + lhte.requiredVersion);

                log.Log(" ");
            }
        }
    }

    protected void testNewJar(String commandLineOptions)
    {
        if ( !ff.getVerifyNewJar() )
            return;

        boolean adjust4Platform = true;

        if ( ff.getJarName() == null ) {
            log.Both("Testing of output jar bypassed because name is null");
            return;
        }

        log.Both("Testing newly created jar file: " + ff.getJarName() );

        // String cmd = "java -jar \"" + ff.getJarName() + "\" " + commandLineOptions;

        String cmd = null;

        if ( ff.getJarName().indexOf(' ') != -1 ) {
            String osName = System.getProperty("os.name");

            if ( osName.equals("Linux") ||
                 osName.equals("Solaris") ||
                 osName.equals("SunOS") ||
                 osName.equals("AIX") ||
                 osName.equals("HP-UX") ||
                 osName.equals("os/390") ) {
                log.Err(135, "Cannot verify jar files containing spaces in" +
                             " the pathname for the " + osName + " platform.");
                exitRC++;
                return;
            }

            // use quotes with spaces and Windows or OS2
            cmd = "java -jar \"" + ff.getJarName() + "\" " + commandLineOptions;
        } else {
            // verification on Unix platforms will not work with quotes.
            cmd = "java -jar " + ff.getJarName() + " " + commandLineOptions;
        }

        ExecCmd exec = new ExecCmd(adjust4Platform);
        Vector results = new Vector();
        Vector logResults = new Vector();

        int rc = exec.Execute(cmd,
                              ExecCmd.DONT_ECHO_STDOUT,
                              ExecCmd.DONT_ECHO_STDERR,
                              results, logResults);

        if ( rc != 0 ) {
            Err(93, "The testing of the new jar failed with rc=" + rc);
            exitRC++;
        }

        for ( int i = 0; i < results.size(); i++ )
            log.Both("  Line #" + hc.FmtNum(i,0,2) + " " + results.elementAt(i));
    }

    protected boolean parserRequired()
    {
        String msg = "XML Parser requirement stimulated by ";

        if ( !ff.getUpdateXML() )
            return false;

        if ( ff.getUpdateXML() ) {
            log.Both(msg + HelperList.o_UpdateXML);
            return true;
        } else if ( ff.getValidating() ) {
            log.Both(msg + HelperList.o_Validating);
            return true;
        } else if ( ff.getNameSpaceAware() ) {
            log.Both(msg + HelperList.o_NameSpaceAware);
            return true;
        } else if ( ff.getDupCheck() ) {
            log.Both(msg + HelperList.o_DupCheck);
            return true;

        } else if ( ff.getPMR() != null ) {
            log.Both(msg + HelperList.o_PMR);
            return true;
        } else if ( ff.getAPAR() != null ) {
            log.Both(msg + HelperList.o_APAR);
            return true;
        } else if ( ff.getDescription() != null ) {
            log.Both(msg + HelperList.o_Description);
            return true;
        } else if ( ff.getNewVersion() != null ) {
            log.Both(msg + HelperList.o_NewVersion);
            return true;

        } else if ( (ff.getCkVersion() != null) && (!ff.getCkVersion().equals("?")) ) {
            log.Both(msg + HelperList.o_ckVersion);
            return true;

        } else if ( !ff.getCkEditionName().equals("?") ) {
            log.Both(msg + HelperList.o_CkEditionName);
            return true;

        } else if ( !ff.getCkEditionValue().equals("?") ) {
            log.Both(msg + HelperList.o_CkEditionValue);
            return true;
        }

        return false;
    }

    // here we will get the array from the HelperList and combine it with
    // any local requests

    protected void seedHelpers()
    {
        int numberOfSupportFiles;

        if ( ff.getBuildSelfExtractor() )
            numberOfSupportFiles = HelperList.SupportFiles.length;
        else
            numberOfSupportFiles = 0;

        String[] sa = new String[numberOfSupportFiles];

        String additionalSupport = ff.getSupport();

        int targetPos = 0;

        if ( additionalSupport != null ) {
            StringTokenizer toks = new StringTokenizer(additionalSupport, ",");
            sa = new String[numberOfSupportFiles + toks.countTokens()];

            while ( toks.hasMoreTokens() )
                sa[targetPos++ ] = toks.nextToken().trim();
        }

        if ( ff.getBuildSelfExtractor() )
            System.arraycopy(HelperList.SupportFiles, 0, sa, targetPos, HelperList.SupportFiles.length);

        boolean notFound = true;

        // Seed the HashMaps with the helper files:

        for ( int sf = 0; sf < sa.length; sf++ ) {
            log.Both(4, "Seeding Helper Class " + sa[sf]);

            notFound = true;
            File helperFile = null;

            for ( int listEntry = 0; listEntry < ff.getHelperDirectoriesCount(); listEntry++ ) {

                if ( !notFound )
                    continue;

                String dir = (String) ff.getHelperDirectories(listEntry);
                String adjDir = null;

                if ( dir.startsWith("jar:") ) { // if to read it from a jar File

                    if ( dir.equals("jar:") ) { // if no jar fileName was specified then assume our own
                        determineOurSource();

                        if (inputJarFileName == null) {
                            Err(101, "Helper file " + sa[sf] + ", was specified" +
                                     " to come from the same jar file as which we" +
                                     " are being executed from, however, we are not" +
                                     " be executed from a jar file.",
                                abend);

                        } else {
                            adjDir = inputJarFileName;
                        }

                    } else {
                        adjDir = dir.substring(4);  // strip off the "jar:"
                    }

                    JarFile jf = null;

                    try {
                        jf = new JarFile(adjDir);

                    } catch ( IOException ex ) {
                        Err(3, "Unable to open " + adjDir, ex);
                        return;
                    }

                    Enumeration jfEntries = jf.entries();

                    while ( jfEntries.hasMoreElements() ) {
                        JarEntry je  = (JarEntry) jfEntries.nextElement();

                        if ( je.getName().equalsIgnoreCase("META-INF/MANIFEST.MF") ) // manifest is bypassed
                            continue;

                        if ( je.getName().startsWith(sa[sf]) ) {

                            String jen = "jar:file:/" + jf.getName() + "!" + je.getName();  // Jar Entry Name
                            jen = jen.replace('\\', '/');

                            FilterFile.HTEntry hte = new FilterFile.HTEntry(isHelper);
                            hte.vData = jen;
                            ff.setTransferFile(je.getName(), hte);

                            if ( ff.getCaseSensitive() )
                                ff.setNoRestore(je.getName().replace('\\','/'), isHelper);
                            else
                                ff.setNoRestore(je.getName().toLowerCase().replace('\\','/'), isHelper);

                            notFound = false;
                        }
                    }

                } else if ( notFound ) {  // just a normal directory

                    helperFile = new File(dir + File.separator + sa[sf]);

                    if ( helperFile.canRead() )
                        log.Both("Accessing (" + helperFile.getAbsolutePath() + ")");
                    else
                        helperFile = null;

                    if ( helperFile == null ) {
                        Err(56, "The helperFile " + HelperList.SupportFiles[sf] +
                                " can not be read from any of the following directories.");

                        for ( int le = 0; le < ff.getHelperDirectoriesCount(); le++ ) {
                            log.BothRaw("        " + listEntry + ". " + ff.getHelperDirectories(le));
                        }

                    } else {
                        FilterFile.HTEntry hte = new FilterFile.HTEntry(isHelper);
                        hte.vData = helperFile.getAbsolutePath();
                        ff.setTransferFile(sa[sf], hte);

                        if ( ff.getCaseSensitive() )
                            ff.setNoRestore(sa[sf], isHelper);
                        else
                            ff.setNoRestore(sa[sf].toLowerCase(), isHelper);

                        notFound = false;
                    }
                }
            }

            if ( notFound )
                Err(103, "The helperFile " + sa[sf] + " can not be located.");
        }
    }

    protected void processPackages()
    {
        log.Both(4, "Process required packages");
        File helperFile = null;

        File packageDir = null;

        for ( int listEntry = 0; listEntry < ff.getPackageDirectoriesCount() ; listEntry++ ) {
            String dir = ff.getPackageDirectories(listEntry);

            if ( dir.toLowerCase().startsWith("jar:") ) { // here the package is within the jar file
                determineOurSource();

                String adjDir = dir.substring(4).replace('\\','/');  // strip off the "jar:"

                if (inputJarFileName == null) {
                    Err(49, "Package inclusion " + dir +
                            ", was specified to come from the same jar file" +
                            " as which we are being executed from, however," +
                            " we are not be executed from a jar file.",
                        abend);
                }

                JarFile jf = null;

                try {
                    jf = new JarFile(inputJarFileName);

                } catch ( IOException ex ) {
                    Err(102, "Unable to open " + inputJarFileName, ex);
                    return;
                }

                int packageCount = 0;

                Enumeration jfEntries = jf.entries();
                while ( jfEntries.hasMoreElements() ) {
                    JarEntry je  = (JarEntry) jfEntries.nextElement();

                    if ( je.getName().equalsIgnoreCase("META-INF/MANIFEST.MF") ) // manifest is bypassed
                        continue;

                    if ( je.getName().startsWith(adjDir) ) {
                        String jen = "jar:file:/" + jf.getName() + "!" + je.getName();  // Jar Entry Name
                        jen = jen.replace('\\', '/');

                        FilterFile.HTEntry hte = new FilterFile.HTEntry(isHelper);
                        hte.vData = jen;
                        ff.setTransferFile(je.getName(), hte);

                        if ( ff.getCaseSensitive() )
                            ff.setNoRestore(je.getName().replace('\\','/'), isHelper);
                        else
                            ff.setNoRestore(je.getName().toLowerCase().replace('\\','/'), isHelper);

                        packageCount++;
                    }
                }

                if ( packageCount > 0 )
                    log.Both(hc.FmtNum(packageCount,0,0) + " package entries for " + adjDir );
                else
                    log.Err(100, "No package entries were found for package (" + adjDir + ")");

            } else {  // here the package is a real directory
                packageDir = new File(dir);

                if ( packageDir.isDirectory() ) {
                    log.Both("Accessing Package (" + packageDir.getAbsolutePath() + ")");

                    HashMap files = new HashMap();
                    HashMap dirs  = new HashMap();
                    boolean recurse = true;

                    // hc.FindFiles(packageDir.getAbsolutePath() + slash + "*",
                    //              recurse, files, dirs, fCaseSensitive, debug);

                    hc.FindFiles(packageDir.getAbsolutePath() + slash + "*",
                                 recurse, files, dirs, true, debug);

                    log.Both(hc.FmtNum(files.size(),0,10) + " files in " +
                             hc.FmtNum(dirs.size(),0,0) + " directories.");

                    Iterator eNum = files.values().iterator();
                    while ( eNum.hasNext() ) {
                        File pfile = (File) eNum.next();

                        String[] nameParts = new String[hc.k_elCount];
                        hc.ParseFileSpec(dir, nameParts, debug);

                        String packageName =
                            pfile.toString().substring( dir.length() - nameParts[hc.k_fullname].length() );

                        FilterFile.HTEntry hte = new FilterFile.HTEntry(isHelper);
                        hte.vData = pfile.getAbsolutePath();
                        ff.setTransferFile(packageName, hte);

                        if ( ff.getCaseSensitive() )
                            ff.setNoRestore(packageName.replace('\\','/'), isHelper);
                        else
                            ff.setNoRestore(packageName.toLowerCase().replace('\\','/'), isHelper);
                    }

                } else {
                    Err(92, "The PackageInclude directory " + packageDir.getAbsolutePath() + " seems not to be a directory.");
                }
            }
        }
    }

    protected void processZipUpdates()
    {
        if ( ff.getZipEntryUpdatesCount() == 0 ) {
            log.Both("No UpdateZipEntries were noted.");
            return;
        }

        log.Both("The following UpdateZipEntries are scheduled.");

        for ( int zu = 0; zu < ff.getZipEntryUpdatesCount(); zu++ ) {
            FilterFile.zipEntryUpdateInfo zEUI =
                (FilterFile.zipEntryUpdateInfo) ff.getZipEntryUpdates(zu);

            log.Both("  Zip FileName : " + zEUI.zipFileName);
            log.Both("      Comment  : " + zEUI.zipComment);
            log.Both("      Extra    : " + zEUI.zipExtra);

            // the divider between the jarFileName and the Package within
            int sep = zEUI.zipFileName.indexOf("->");

            if (sep == -1) {
                Err(121, "ZipEntryUpdate (" + zEUI.zipFileName + ")" +
                         " is missing the required seperator \"->\"",
                    abend);
            }

            String jarFileName = zEUI.zipFileName.substring(0, sep);
            String jarPackage  = zEUI.zipFileName.substring(sep + 2);

            FileEntry zFE;

            if ( !outJarTab.containsKey(jarFileName) ) {  // if we are already updating this jar file
                // This first entry is for the jar file itself

                FileEntry fe         = new FileEntry();
                fe.action            = k_JarEntry;
                fe.relativePath      = jarFileName;
                fe.firstJarFile      = true;  // if true then relpath contains the jaronly

                fe.AddAttrib(HelperList.meJar, HelperList.meTrue);

                outJarTab.put(fe.relativePath, fe);

            } else {
                log.Both("    other activity for this jar is noted.");
            }

            FileEntry fe    = new FileEntry();
            fe.action       = k_JarEntry;
            fe.relativePath = jarFileName + childDividor + jarPackage;
            fe.zipComment   = zEUI.zipComment;
            fe.zipExtra     = zEUI.zipExtra;
            fe.dummyEntry   = true;

            outJarTab.put(fe.relativePath, fe);
        }
    }

    protected void record_eFixFiles()
    {
        //  void RecordChange(int action,  JarFile jf, JarEntry je, File flatFile,
        //                    boolean delete, String startingTree, boolean firstJarEntry,
        //                    boolean dblBang, File bdTempFile, boolean absPath) {

        String lastJar = "";

        for ( int i = 0; i < ff.geteFixFilesCount(); i++ ) {

            FilterFile.HTEntry hte = ff.geteFixFiles(i);

            if ( hte.eFixType == k_JarEntry ) {
                // Here we will enter special entries into the outJarTab as what we have are
                // jar entries that are not in a jar File. The RecordChange method assumes that
                // jar entries will come from a jar file, imagine that.

                if ( !lastJar.equals(hte.vData) ) {
                    // This first entry is for the jar file itself

                    FileEntry fe     = new FileEntry();
                    fe.action        = k_JarEntry;
                    fe.relativePath  = hte.vData;
                    fe.firstJarFile  = true;  // if true then relpath contains the jaronly

                    fe.AddAttrib(HelperList.meJar, HelperList.meTrue);

                    outJarTab.put(fe.relativePath, fe);
                    lastJar = hte.vData;
                }

                FileEntry fe    = new FileEntry();
                fe.action       = k_JarEntry;
                fe.relativePath = hte.vData + childDividor + hte.key.substring(hte.prefixLen);
                fe.bdTempFile   = new File(hte.key);

                outJarTab.put(fe.relativePath, fe);

            } else {
                RecordChange(hte.eFixType, null, null, new File(hte.vData),
                             false, "", false,
                             false, new File(hte.key), false);
            }
        }
    }

    protected boolean VerifyList(HashMap ht, int oldNew)
    {
        boolean returnValue = true;

        FilterFile.HTEntry hte = null;

        // if nothing was specified, just convert the ht entries to FileDesc
        if ( po.getString(HelperList.o_FilterFile) == null ) {

            Iterator ht_eNum = ht.keySet().iterator();

            while ( ht_eNum.hasNext() ) {
                String key     = (String) ht_eNum.next();
                File   theFile =  (File) ht.get(key);
                String signatures    = null;
                String componentName = null;

                // here we will replace the file object in the ht hashtable with a FileDesc object
                FileDesc fd      = new FileDesc();
                fd.theFile       = theFile;
                fd.signatures    = signatures;
                fd.componentName = componentName;

                ht.put(key, fd);
            }

            return returnValue;
        }

        Vector excludes = new Vector();

        Iterator ht_eNum = ht.keySet().iterator();

        while ( ht_eNum.hasNext() ) {
            String key     = (String) ht_eNum.next();
            String keyLC   = key.toLowerCase();
            File   theFile =  (File) ht.get(key);

            String signatures    = null;
            String componentName = null;

            // if any Include\Exclude statements were specified then
            // default to exclude

            boolean keeper = (ff.getIncludesCount() > 0) ? false : true;

            for ( int i = 0; i < ff.getIncludesCount(); i++ ) {
                hte = ff.getIncludes(i);

                if ( ff.getCaseSensitive() ) {
                    if ( key.startsWith(hte.key) ) {
                        hte.increment(oldNew);
                        keeper = hte.include;
                        i = ff.getIncludesCount() + 1;
                    }

                } else {
                    String hteKeyLC = hte.key.toLowerCase();

                    if ( keyLC.startsWith(hteKeyLC) ) {
                        hte.increment(oldNew);
                        keeper = hte.include;
                        i = ff.getIncludesCount() + 1;
                    }
                }
            }

            if ( !keeper ) {
                excludes.add(key);
                continue;
            }

            if ( hte == null ) {
                componentName = null;
                signatures    = null;
            } else {
                componentName = hte.componentName;
                signatures    = hte.signatures;
            }

            Iterator d2s_eNum = ff.getDirs2SkipEnumKey();    // process the Skip Directories
            while ( d2s_eNum.hasNext() ) {
                String dirName = (String) d2s_eNum.next();

                if ( key.startsWith(dirName) ) {
                    excludes.add(key);
                    keeper = false;
                    hte = ff.getDirs2Skip(dirName);
                    hte.increment(oldNew);
                }
            }

            if ( !keeper )
                continue;

            hte = ff.getFiles2Skip(key);

            if ( hte != null ) {
                hte.increment(oldNew);
                excludes.add(key);
                continue;
            }

            // here we will replace the file object in the ht hashtable with a FileDesc object
            FileDesc fd      = new FileDesc();
            fd.theFile       = theFile;
            fd.signatures    = signatures;
            fd.componentName = componentName;
            ht.put(key, fd);
        }

        int numExcludes = excludes.size();
        for ( int excludeNo = 0; excludeNo < numExcludes; excludeNo++ )
            ht.remove( (String) (excludes.elementAt(excludeNo)) );

        return returnValue;
    }

    protected void DisplayFF(HashMap oldDirs, HashMap newDirs)
    {
        Iterator eNum;
        FilterFile.HTEntry hte;

        int padN = 8;    // pad for Numerics

        if ( ff.getIncludesCount() == 0 ) {
            log.Both("Include/Exclude option not specified.");

        } else {
            log.Both(hc.Padit("oldTree", padN, false, ' ') +
                     hc.Padit("newTree", padN, false, ' ') + "     Include/Exclude Counts");

            for ( int i = 0; i < ff.getIncludesCount(); i++ ) {
                hte = ff.getIncludes(i);

                String iOrX = (hte.include) ? " Incl " : " Excl ";
                String cut  = (hte.cut    ) ? " Cut  " : "      ";

                log.Both(hc.FmtNum(hte.count[k_Old], 0, padN, 0) +
                         hc.FmtNum(hte.count[k_New], 0, padN, 0) + iOrX + cut + hte.key);
            }
        }

        Display(ff.getDirs2Skip(),         "Dirs2Skip",         padN);
        Display(ff.getFiles2Skip(),        "Files2Skip",        padN);

        Display(ff.deleteBeforeWrite,      "deleteBeforeWrite", padN);
        Display(ff.addOnlyFiles,           "addOnly",           padN);
        Display(ff.replaceOnlyFiles,       "replaceOnly",       padN);
        Display(ff.perInstance,            "perInstance",       padN);
        Display(ff.asInstallable,          "asInstallable",     padN);
        Display(ff.asApplication,          "asApplication",     padN);
        Display(ff.asInstalled,            "asInstalled",       padN);

        Display(ff.getTransferFile(),      "TransferFile",      padN);
        Display(ff.getChmod(),             "Chmod",             padN);
        Display(ff.getNoDelete(),          "NoDelete",          padN);
        Display(ff.getForceReplace(),      "ForceReplace",      padN);
        Display(ff.getNoRestore(),         "NoRestore",         padN);
        Display(ff.getReName(),            "Rename",            padN);
        Display(ff.getReSequenceJar(),     "Re-SequenceJar",    padN);

        if ( extCounts.size() == 0 ) {
            log.Both("No extension information was accumulated.");

        } else {
            log.Both(" ");
            log.Both("Counts by Extension");
            log.Both(hc.Padit("oldTree", padN, false, ' ') +
                     hc.Padit("newTree", padN, false, ' ') +
                     hc.Padit("delta"  , padN, false, ' ') + "  Extensions");

            eNum = extCounts.keySet().iterator();

            while ( eNum.hasNext() ) {
                String key = (String) eNum.next();
                hte = (FilterFile.HTEntry) extCounts.get(key);

                log.Both(hc.FmtNum(hte.count[k_Old], 0, padN, 0) +
                         hc.FmtNum(hte.count[k_New], 0, padN, 0) +
                         hc.FmtNum(hte.count[k_New] - hte.count[k_Old], 0, padN, 0) + " " + key);
            }

        }

        log.Both("");

        log.Both(hc.Padit(" ",       pV1, false) +
                 hc.Padit("oldTree", pV2, false) +
                 hc.Padit("newTree", pV3, false) +
                 hc.Padit("Delta",   pV4, false) );

        log.Both(hc.Padit("Directories", pV1, true) + ":" +
                 hc.FmtNum(oldDirs.size(), 0, 12) +
                 hc.FmtNum(newDirs.size(), 0, 12) +
                 hc.FmtNum((newDirs.size() - oldDirs.size()), 0, 12));

        log.Both(hc.Padit("Files", pV1, true) + ":" +
                 hc.FmtNum(totFiles[k_Old], 0, 12) +
                 hc.FmtNum(totFiles[k_New], 0, 12) +
                 hc.FmtNum((totFiles[k_New] - totFiles[k_Old]), 0, 12));
        log.Both(hc.Padit("File Bytes", pV1, true) + ":" +
                 hc.FmtNum(totBytes[k_Old], 0, 12) +
                 hc.FmtNum(totBytes[k_New], 0, 12) +
                 hc.FmtNum((totBytes[k_New] - totBytes[k_Old]), 0, 12));

        log.Both(hc.Padit("Jars", pV1, true) + ":" +
                 hc.FmtNum(totJars[k_Old], 0, 12) +
                 hc.FmtNum(totJars[k_New], 0, 12) +
                 hc.FmtNum((totJars[k_New] - totJars[k_Old]), 0, 12));
        log.Both(hc.Padit("Jar Bytes", pV1, true) + ":" +
                 hc.FmtNum(totJBytes[k_Old], 0, 12) +
                 hc.FmtNum(totJBytes[k_New], 0, 12) +
                 hc.FmtNum((totJBytes[k_New] - totJBytes[k_Old]), 0, 12));

        log.Both(" ");

        log.Both(hc.Padit("Files to Delete", pV1, true) + ":" +
                 hc.FmtNum(files2delete.size(), 0, 12) +
                 hc.Padit("", pV3, false) +
                 hc.FmtNum(files2delete.size(), 0, 12));
        log.Both(hc.Padit("Bytes to Delete", pV1, true) + ":" +
                 hc.FmtNum(bytes2delete, 0, 12) +
                 hc.Padit("", pV3, false) +
                 hc.FmtNum(bytes2delete, 0, 12));
        log.Both(hc.Padit("jar entries Delete", pV1, true) + ":" +
                 hc.FmtNum(je2delete, 0, 12) +
                 hc.Padit("", pV3, false) +
                 hc.FmtNum(je2delete, 0, 12));
        log.Both(hc.Padit("jar bytes Delete",   pV1, true) + ":" +
                 hc.FmtNum(jebytes2del, 0, 12) +
                 hc.Padit("", pV3, false) +
                 hc.FmtNum(jebytes2del, 0, 12));

        log.Both(" ");

        log.Both(hc.Padit("Files to Add ", pV1, true) + ":" +
                 hc.Padit("", pV2, false) +
                 hc.FmtNum(files2add.size(), 0, 12) +
                 hc.FmtNum(files2add.size(), 0, 12));
        log.Both(hc.Padit("Bytes to Add ", pV1, true) + ":" +
                 hc.Padit("", pV2, false) +
                 hc.FmtNum(bytes2add, 0, 12) +
                 hc.FmtNum(bytes2add, 0, 12));
        log.Both(hc.Padit("jar entries add", pV1, true) + ":" +
                 hc.Padit("",pV2,false) +
                 hc.FmtNum(je2add, 0, 12) +
                 hc.FmtNum(je2add, 0, 12));
        log.Both(hc.Padit("jar bytes add", pV1, true) + ":" +
                 hc.Padit("",pV2,false) +
                 hc.FmtNum(jebytes2add, 0, 12) +
                 hc.FmtNum(jebytes2add, 0, 12));

        log.Both("");

        log.Both(hc.Padit("Files to Replace", pV1, true) + ":" +
                 hc.Padit("",pV2,false) +
                 hc.FmtNum(files2replace, 0, 12) +
                 hc.FmtNum(files2replace, 0, 12));
        log.Both(hc.Padit("Bytes to Replace", pV1, true) + ":" +
                 hc.Padit("",pV2,false) +
                 hc.FmtNum(bytes2replace, 0, 12) +
                 hc.FmtNum(bytes2replace, 0, 12));
        log.Both(hc.Padit("Exceed Pct", pV1, true) + ":" +
                 hc.Padit("",pV2,false) +
                 hc.FmtNum(exceeded, 0, 12) +
                 hc.FmtNum(0, 0, 12));

        long pct = (bytes2replace > 0 ) ? ((bytes2send * 100) / bytes2replace) : -1;
        log.Both(hc.Padit("Bytes 2 send", pV1, true) + ":" +
                 hc.Padit("", pV2, false) +
                 hc.FmtNum(bytes2send, 0, 12) +
                 hc.Padit(pct + "%", 13, false));
        log.Both(hc.Padit("Jar entry Replace", pV1, true) + ":" +
                 hc.Padit("", pV2, false) +
                 hc.FmtNum(je2replace, 0, 12) +
                 hc.FmtNum(je2replace, 0, 12));
        log.Both(hc.Padit("Jar bytes Replace", pV1, true) + ":" +
                 hc.Padit("", pV2, false) +
                 hc.FmtNum(jebytes2rep, 0, 12) +
                 hc.FmtNum(jebytes2rep, 0, 12));
        log.Both(hc.Padit("jar Exceeded Pct", pV1, true) + ":" +
                 hc.Padit("", pV2, false) +
                 hc.FmtNum(jeexceeded, 0, 12) +
                 hc.FmtNum(0, 0, 12));

        pct = (jebytes2rep > 0) ? ((jebytes2send * 100) / jebytes2rep) : -1;
        log.Both(hc.Padit("Jar bytes2send", pV1, true) + ":" +
                 hc.Padit("", pV2, false) +
                 hc.FmtNum(jebytes2send, 0, 12) +
                 hc.Padit(pct + "%", 13, false));

        log.Both("");

        log.Both("Entries in output Jar " + outJarTab.size());

        // Accumulate statistics ...

        long oj_AddFile  = 0, // Total number of files to add
             oj_SizeFile = 0, // Total size of files to add
             oj_DelFile  = 0, // Total number of files to delete
             oj_AddJE    = 0, // Total number of jar entries to add
             oj_SizeJE   = 0, // Total size of jar entries to add
             oj_DelJE    = 0; // Total number of jar entries to delete

        eNum = outJarTab.values().iterator();
        while ( eNum.hasNext() ) {
            FileEntry fe = (FileEntry) eNum.next();

            if ( fe.flatFile == null ) {
                if ( fe.delete ) {
                    oj_DelJE++;
                } else {
                    oj_SizeJE += fe.fileSize;
                    oj_AddJE++;
                }

            } else {
                if ( fe.delete ) {
                    oj_DelFile++;
                } else {
                    oj_SizeFile += fe.fileSize;
                    oj_AddFile++;
                }
            }
        }

        log.Both(hc.Padit("New/Replace Files", pV1, true) + ":" + hc.FmtNum(oj_AddFile,  0, 12) );
        log.Both(hc.Padit("Delete Files",      pV1, true) + ":" + hc.FmtNum(oj_DelFile,  0, 12) );
        log.Both(hc.Padit("File Bytes",        pV1, true) + ":" + hc.FmtNum(oj_SizeFile, 0, 12));

        log.Both("");

        log.Both(hc.Padit("New/Replace entry", pV1, true) + ":" + hc.FmtNum(oj_AddJE,    0, 12) );
        log.Both(hc.Padit("Delete entries",    pV1, true) + ":" + hc.FmtNum(oj_DelJE,    0, 12) );
        log.Both(hc.Padit("Jar entry Bytes",   pV1, true) + ":" + hc.FmtNum(oj_SizeJE,   0, 12));

        log.Both("");

        log.Both(hc.Padit("Per Instance",      pV1, true) + ":" + hc.FmtNum(filesPerInstance,   0, 12) );

        log.Both("");

        log.Both(hc.Padit("Installable",  pV1, true) + ":" + hc.FmtNum(filesAsInstallable, 0, 12) );
        log.Both(hc.Padit("Application",  pV1, true) + ":" + hc.FmtNum(filesAsApplication, 0, 12) );
        log.Both(hc.Padit("Installed",    pV1, true) + ":" + hc.FmtNum(filesAsInstalled,   0, 12) );
        log.Both(hc.Padit("Metadata",     pV1, true) + ":" + hc.FmtNum(filesAsMetadata,    0, 12) );
        log.Both(hc.Padit("NameRule",     pV1, true) + ":" + hc.FmtNum(filesAsNameRule,    0, 12) );
    }

    protected void Display(FilterFile.FileCategory category, String htName, int padN)
    {
        Display(category.getExactMap(),   htName + " - Exact",   padN);
        Display(category.getInexactMap(), htName + " - Inexact", padN);
    }

    protected void Display(HashMap ht, String htName, int padN)
    {
        if ( ht.size() == 0 ) {
            log.Both(htName + ": None were specified");

        } else {
            boolean toScreen = (ht.size() > 12) ? false : true;

            if ( toScreen )
                log.Both(htName + " files: ");
            else
                log.Both(hc.FmtNum(ht.size(),0,0) + " files for \"" + htName + "\".");

            Iterator eNum = ht.keySet().iterator();

            while ( eNum.hasNext() ) {
                String key = (String) eNum.next();
                FilterFile.HTEntry hte = (FilterFile.HTEntry) ht.get(key);

                if ( toScreen ) {
                    log.Both(hc.FmtNum(hte.count[k_Old], 0, padN, 0) +
                             hc.FmtNum(hte.count[k_New], 0, padN, 0) + " " + key);
                } else {
                    log.Log(hc.FmtNum(hte.count[k_Old], 0, padN, 0) +
                            hc.FmtNum(hte.count[k_New], 0, padN, 0) + " " + key);
                }


                // if ( (hte.count[k_Old] + hte.count[k_New] ) == 0 )
                //     log.Both("Note: The " + htName + " file (" + hte.vData + ") matches no file.");

                // Err(50, " The " + htName + " file was not found.");
            }
        }
    }

    protected void Display(Vector vt, String htName, int padN)
    {
        if ( vt.size() == 0 ) {
            log.Both(htName + ": None were specified");

        } else {
            log.Both(htName + " files: ");

            for ( int i = 0; i < vt.size(); i++ ) {
                FilterFile.HTEntry hte = (FilterFile.HTEntry) vt.elementAt(i);

                log.Both(hc.FmtNum(hte.count[k_Old], 0, padN, 0) +
                         hc.FmtNum(hte.count[k_New], 0, padN, 0) + " " + hte.key);

                if ( (hte.count[k_Old] + hte.count[k_New] ) == 0 )
                    log.Log("==> Entry matches no active file.");
            }
        }
    }

    protected boolean processImportComponents()
    {
        // here we will read a text file produced by ComponentsList.java
        // which contains file names for J2EEClient and JTCClient. We will
        // search for the names in the outJarTab and record the additional
        // component names associated with the file name.

        // Iterator eNum = outJarTab.keySet().iterator();

        // while (eNum.hasNext()) {
        //   String key = (String) eNum.next();
        //   log.Both("Diag #111 key=(" + key + ")");
        // }

        Vector msgVector = new Vector();

        for ( int i = 0; i < ff.getImportComponentsCount(); i++ ) {
            FilterFile.HTEntry hte = ff.getImportComponents(i);

            log.Both("Importing " + hte.componentName + " from " + hte.key);

            Vector lines = hc.file2Vector(msgVector, hte.key);

            if ( displayVector(msgVector) ) {
                log.Err(108, "Failure reading component import file :" + hte.key);

            } else {
                int lCount  = 0;
                int matched = 0;

                for ( int lx = 0; lx < lines.size(); lx++ ) {
                    String line = ((String) lines.elementAt(lx)).trim();

                    if ( (line.startsWith("#")) || (line.startsWith("*")) )  //Skip comment Lines
                        continue;

                    lCount++;
                    log.Log("   " + line);

                    if ( outJarTab.containsKey(line) ) {
                        matched++;

                        log.Log("        matched");

                        FileEntry fe = (FileEntry) outJarTab.get(line);

                        if ( fe.components == null )
                            fe.components = hte.componentName;
                        else
                            fe.components = fe.components + "," + hte.componentName;

                    } else {
                        log.Log("        is unique to " + hte.componentName);
                    }

                }

                log.Both(hc.FmtNum(lCount, 0, 12)  + " files processed.");
                log.Both(hc.FmtNum(matched, 0, 12) + " files matched.");
            }
        }

        return true;
    }

    protected boolean displayVector(Vector msgs)
    {
        if ( msgs.size() == 0 )
            return false;

        for ( int i = 0; i < msgs.size(); i++ )
            log.Both( (String) msgs.elementAt(i));

        msgs.clear();

        return true;
    }

    protected void processRemainingChmods()
    {
        log.Both("Processing remaining chmods as chmod_only.");

        Iterator chmodKeys = ff.getChmodEnumKeys();

        while ( chmodKeys.hasNext() ) {
            String fileName = (String) chmodKeys.next();
            FilterFile.HTEntry hte = ff.getChmod(fileName);

            log.Both("Processing chmod_only of: " + fileName);
            log.Both("  Attributes: " + hte.vData);

            FileEntry fe = new FileEntry();

            // The flat file must remain null -- otherwise, this
            // will be processed as a update change.

            fe.action       = k_ChmodOnly;
            fe.relativePath = fileName;

            try {
                fe.permissions = Integer.parseInt(hte.vData);
                fe.permisAreSet = true;
                fe.manifestOnly = true;

            } catch ( NumberFormatException e ) {
                Err(80, "Non-valid permissions for " + fileName + ": " + hte.vData);

                fe.permisAreSet = false;
            }

            if ( fe.permisAreSet )
                outJarTab.put(fe.relativePath, fe);
        }

        log.Both("Processing remaining chmods as chmod_only: Done");
    }

    protected boolean BuildJarFile()
    {
        boolean returnValue = true;

        long inputSize = 0,
             outputSize = 0;

        log.Both("Building JAR file " + ff.getJarName());

        Manifest manifest  = new Manifest();
        Attributes attribs = manifest.getMainAttributes();

        // Note:  the manifest must be created before the jar stream is opened.
        //        the manifest must contain a MANIFEST-VERSION entry

        log.Both("Constructing the manifest");

        attribs.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");

        if ( ff.getBuildSelfExtractor() ) {
            attribs.putValue(Attributes.Name.MAIN_CLASS.toString(),
                             "com.ibm.websphere.update.delta.Extractor");
        }

        attribs.putValue("JDK-Level", System.getProperty("java.vm.version") + " " +
                                      System.getProperty("java.vm.vendor"));
        attribs.putValue("TimeStamp", log.CurrentTimeStamp());

        attribs.putValue(HelperList.meCkVersion,      ff.getCkVersion());
        attribs.putValue(HelperList.meCkEditionName,  ff.getCkEditionName());
        attribs.putValue(HelperList.meCkEditionValue, ff.getCkEditionValue());

        attribs.putValue(HelperList.meCkSize,     ff.getCkSizeString());
        attribs.putValue(HelperList.meChunckSize, Integer.toString(ff.getChunckSize()));
        attribs.putValue(HelperList.meDupCheck,   ff.getDupCheckString());

        // To handle these two types of entries, the extractor will
        // need to determine the EAR temporary directory.

        if ( (filesAsInstallable > 0) || (filesAsApplication > 0) || (filesAsInstalled > 0) ) {
            attribs.putValue(HelperList.mePrepareEARTmp, HelperList.meTrue);
        }

        // Configuration instance data must be loaded to handle
        // per-instance files and to handle EAR entries.

        if ( (filesPerInstance > 0) ||
             (filesAsInstallable > 0) || (filesAsApplication > 0) || (filesAsInstalled > 0) ) {
            attribs.putValue(HelperList.mePrepareInstances, HelperList.meTrue);
        }

        if ( ff.getmfClassPath() != null ) {
            // attribs.putValue(Attributes.Name.CLASS_PATH.toString(),
            //                 hc.resolveMacro(ff.getmfClassPath(), macs).trim());
        }

        String key          = "notSet";
        String lastFilespec = "";
        int    lastFileNum  = 0;
        int    findNum      = 0;

        //  Process file-update string/replace requests:

        for ( int fu = 0; fu < ff.getFileUpdateCount(); fu++ ) {
            FilterFile.FileUpdateInfo fui =  (FilterFile.FileUpdateInfo) ff.getFileUpdate(fu);

            if ( !lastFilespec.equals(fui.filespec) ) {
                log.Log("Updating " + fui.filespec);

                lastFilespec = fui.filespec;
                lastFileNum++;

                key = HelperList.meFileUp + lastFileNum;

                attribs.putValue(key, lastFilespec);

                findNum = 0;
            }

            findNum++;

            attribs.putValue(key + HelperList.meFileUpFind + findNum, fui.find);
            attribs.putValue(key + HelperList.meFileUpRepl + findNum, fui.replace);

            log.Log("  find (" + fui.find + ") replace(" + fui.replace + ")");

            if ( fui.components.length > 0 ) {
                log.Log("    " + fui.components.length + " components replace(" + fui.replace + ")");

                attribs.putValue(key + HelperList.meFileUpComponent + "0", Integer.toString(fui.components.length));

                for ( int compNum = 0; compNum < fui.components.length; compNum++ ) {
                    attribs.putValue(key + HelperList.meFileUpComponent + (compNum + 1), fui.components[compNum]);

                    log.Log("      limited to component (" + fui.components[compNum] + ")");
                }
            }
        }

        //  Process property file update requests:

        lastFilespec = "";
        lastFileNum  = 0;
        findNum      = 0;

        for ( int pu = 0; pu < ff.getPropUpdateCount(); pu++ ) {
            FilterFile.PropUpdateInfo pui =  (FilterFile.PropUpdateInfo) ff.getPropUpdate(pu);

            if ( !lastFilespec.equals(pui.propFileName) ) {
                log.Log("Updating Property File " + pui.propFileName);

                lastFilespec =  pui.propFileName;
                lastFileNum++;

                key = HelperList.mePropFile + lastFileNum;

                attribs.putValue(key, lastFilespec);  // output xtr_PropFile1 = thePropertyFileName

                findNum = 0;
            }

            findNum++;

            attribs.putValue(key + HelperList.mePropFun + findNum, pui.function);
            attribs.putValue(key + HelperList.mePropKey + findNum, pui.key);
            attribs.putValue(key + HelperList.mePropValue + findNum, pui.value);

            log.Log("   function (" + pui.function + ")  key=(" + pui.key + ")  value=(" + pui.value + ")");
        }

        // Process the NoBackUpJar specifications:

        lastFileNum = 0;

        Iterator eNum = ff.getNoBackupJarEnumKey();

        while ( eNum.hasNext() ) {
            String filespec = (String) eNum.next();
            String cond = ff.getNoBackupJar(filespec);

            lastFileNum++;

            attribs.putValue(HelperList.meNoBackUpJar + lastFileNum, cond + "*" + filespec);

            cond = (cond.equals("1")) ? " Exists " : " NotExists ";
            log.Log("NoBackUpJar if " + cond + filespec);
        }

        // Process the re-sequence jar requests:

        if ( ff.getReSequenceJarCount() > 0 ) {
            int seqNum = 0;

            eNum = ff.getReSequenceJarEnum();
            while ( eNum.hasNext() ) {
                FilterFile.HTEntry hte = (FilterFile.HTEntry) eNum.next();

                seqNum++;

                attribs.putValue(HelperList.meReSeqJar  + seqNum,  hte.key);
                attribs.putValue(HelperList.meReSeqList + seqNum,  hte.vData);
            }
        }

        // Process the XML entries for Product.XML update:

        if ( ff.getProductFileType() != null ) {
            attribs.putValue(HelperList.meProductFileType,  ff.getProductFileType());
            attribs.putValue(HelperList.meProductFile,      ff.getProductFileName());

            if ( ff.getProductFileVKey() != null )
                attribs.putValue(HelperList.mePropertiesVersionKey,  ff.getProductFileVKey());
        }

        attribs.putValue(HelperList.meXMLVersion,          ff.getXMLVersion());
        attribs.putValue(HelperList.meValidating,          ff.getValidatingString());
        attribs.putValue(HelperList.meNameSpaceAware,      ff.getNameSpaceAwareString());
        attribs.putValue(HelperList.meAddHistory,          ff.getAddHistoryString());
        attribs.putValue(HelperList.meXMLPathVersion,      ff.getXMLPathVersion());
        attribs.putValue(HelperList.meXMLPathEditionName,  ff.getXMLPathEditionName());
        attribs.putValue(HelperList.meXMLPathEditionValue, ff.getXMLPathEditionValue());
        attribs.putValue(HelperList.meXMLPathBuildDate,    ff.getXMLPathBuildDate());
        attribs.putValue(HelperList.meXMLPathBuildNumber,  ff.getXMLPathBuildNumber());
        attribs.putValue(HelperList.meXMLPathEvent,        ff.getXMLPathEvent());

        if ( ff.getEventType() != null )
            attribs.putValue(HelperList.meEventType,      ff.getEventType());

        if ( ff.getAPAR() != null )
            attribs.putValue(HelperList.meAPAR,           ff.getAPAR());

        if ( ff.getPMR() != null )
            attribs.putValue(HelperList.mePMR,            ff.getPMR());

        // if ( ff.getDeveloper() != null )
        //     attribs.putValue(hl.meDeveloper,      ff.getDeveloper());

        if ( ff.getDescription() != null )
            attribs.putValue(HelperList.meDescription,    ff.getDescription());

        if ( ff.getNewVersion() != null )
            attribs.putValue(HelperList.meNewVersion,     ff.getNewVersion());

        if ( ff.getNewBuildNumber() != null )
            attribs.putValue(HelperList.meNewBuildNumber,  ff.getNewBuildNumber());

        if ( ff.getNewBuildDate() != null )
            attribs.putValue(HelperList.meNewBuildDate,  ResolveMacro(ff.getNewBuildDate(), 0));

        storeVirtualScripts(attribs);

        // Process the forced backup jar requests
        for ( int j = 0; j < ff.getForceBackupCount(); j++ )
            attribs.putValue(HelperList.meForceBackup + j, ff.getForceBackup(j));

        if ( ff.getCheck4Class() != null )
            attribs.putValue( HelperList.meCheck4Class, ff.getCheck4Class());

        // Process the start and stop messages:

        attribs.putValue(HelperList.meStartMsg, ff.getStartMsg());
        attribs.putValue(HelperList.meEndMsg,   ff.getEndMsg());

        // if ( ff.getCall4Help() != null  )
        //     attribs.putValue(hl.meCall4Help,  ff.getCall4Help());

        if ( ff.getTargetMsg() != null )
            attribs.putValue(HelperList.meTargetMsg, ff.getTargetMsg());

        // Process the target components:

        if ( ff.getAffectedComponents() != null )
            attribs.putValue(HelperList.meAffectedComponents, ff.getAffectedComponents());

        // Process components and the their signatures:

        int seq = 0;

        eNum = ff.getComponentsEnumKey();
        while ( eNum.hasNext() ) {
            String cName = (String) eNum.next();
            String cSigs =  ff.getComponents(cName);
            attribs.putValue(HelperList.meComponentName+ ++seq, cName + "," + cSigs.trim());
        }

        // Process a spare product file:

        if ( ff.getSpareProducts() != null ) {
            for ( int i = 0; i < ff.getSpareProductsCount(); i++ ) {
                String spFileName = ff.getSpareProducts(i);
                String[] parts = new String[hc.k_elCount];

                if ( !hc.ParseFileSpec(spFileName, parts, debug) )
                    Err(130, "Helper1.ParseFileSpec() failed on (" + spFileName + ")", abend);

                attribs.putValue(HelperList.meSpareProductFile + i, parts[hc.k_fullname]);

                FileEntry fe  = new FileEntry();
                fe.flatFile   = new File(parts[hc.k_fullname]);
                fe.bdTempFile = new File(spFileName);
                fe.relativePath = parts[hc.k_fullname];
                fe.AddAttrib(HelperList.meNoRestore, HelperList.meTrue);
                outJarTab.put(fe.relativePath, fe);
            }
        }

        // Process the restoreOnly files:

        if ( ff.getRestoreOnlyCount() > 0 ) {
            for ( int i = 0; i < ff.getRestoreOnlyCount(); i++ ) {
                FilterFile.HTEntry lhte = ff.getRestoreOnly(i);

                FileEntry fe  = new FileEntry();
                fe.flatFile   = new File(lhte.vData);
                fe.bdTempFile = new File(lhte.key);
                fe.relativePath = lhte.vData;
                fe.AddAttrib(HelperList.meRestoreOnly, HelperList.meTrue);

                if ( lhte.requiredVersion != null )
                    fe.AddAttrib(HelperList.meRequiredVersion, lhte.requiredVersion);

                outJarTab.put(fe.relativePath, fe);
            }
        }

        // Go through the outJarTab and build the manifest:
        // TBD: Need to get the main attribs if its a jarEntry.

        Map map = manifest.getEntries();

        log.Both("Output table size: " + outJarTab.size());

        eNum = outJarTab.values().iterator();
        while ( eNum.hasNext() ) {
            FileEntry fe = (FileEntry) eNum.next();

            log.Log("Processing table entry: (" + fe.relativePath + ")");

            // Put the component name into the attributes:

            if ( fe.components != null )
                fe.AddAttrib(HelperList.meComponent, fe.components);

            if ( (fe.attribs != null) || fe.permisAreSet ) {
                log.Log("Attribute data was detected.");

                fe.relativePath = fe.relativePath.replace('\\' , '/' );

                if ( !(ff.getLeadingSlash()) && (fe.relativePath.startsWith("/")) )
                    fe.relativePath = fe.relativePath.substring(1);

                // if (fe.relativePath.length() == 131) {
                //
                // This is a hack:
                // Entries of a length of 131 bytes will make the manifest invalid.
                //
                //   log.Log("Diag #131 (" + fe.relativePath + ")"); // is if for some early 1.3.1 JDKs
                //   fe.relativePath = fe.relativePath + "     ";
                // }

                if ( fe.permisAreSet ) {
                    String permText = Integer.toString(fe.permissions);

                    log.Log("Recording permissions: [" + fe.relativePath + "]: [" + permText + "]");

                    fe.AddAttrib(HelperList.mePermissions, permText);

                    if ( fe.action == k_ChmodOnly )
                        fe.attribs.putValue(HelperList.meChmodOnly, HelperList.meTrue);
                }

                if ( fe.addOnly )
                    fe.attribs.putValue(HelperList.meAddFile, HelperList.meTrue);

                if ( fe.replaceOnly )
                    fe.attribs.putValue(HelperList.meReplaceFile, HelperList.meTrue);

                map.put(fe.relativePath, fe.attribs);
            }
        }

        if ( ff.getDisplayManifest() )
            DisplayManifestContent(manifest);  // for debug purpose only

        log.Both("opening Jar file (" + ff.getJarName() + ")");
        JarOutputStream jos = CreateJarStream(ff.getJarName(), manifest);

        if ( jos == null ) {
            Err(23, " Failure to create JarOutPutStream()");
            return false;
        }

        jos.setLevel(ff.getCompression());

        byte[] buffer = new byte[4096];
        int bytesRead;

        // See ShowProgress class for use of this variable.

        long[] counts = { ff.getFrequencyUpdate(), 0, outJarTab.size() };

        ShowProgress sp = new ShowProgress("{0} jaring file {1} of {2}  {3}% complete", counts);
        sp.setDaemon(true);
        sp.start();

        Comparator jarEntryNameComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((String) o1).compareTo((String) o2);
            }

            public boolean equals(Object obj) {
                return this == obj;
            }
        };

        TreeMap tm = new TreeMap(jarEntryNameComparator);
        tm.putAll(outJarTab);

        Set keySet = tm.keySet();

        log.Log("Legend; += Adding to jar, d=delete, j=first of a jar," +
                " m=manifest entry, c=byteKompress or tempFile");

        if ( debug ) {
            log.Both("Debug - Displaying entry table list:");

            Iterator debugKeyIterator = keySet.iterator();
            while ( debugKeyIterator.hasNext() ) {
                String nextKey = (String) debugKeyIterator.next();
                FileEntry fe = (FileEntry) outJarTab.get(nextKey);

                String entryValue;
                String entryIndicator;

                if ( fe.flatFile == null ) {
                    if ( (fe != null) && (fe.jarEntry != null ) ) {
                        entryValue = fe.jarEntry.toString();
                        entryIndicator = "JarEntry ";
                    } else {
                        entryValue     = "Null";
                        entryIndicator = "Null";
                    }

                } else {
                    entryValue = fe.flatFile.toString();
                    entryIndicator = "FileEntry";
                }

                String bdTemp;

                if ( fe.bdTempFile != null )
                    bdTemp = fe.bdTempFile.toString();
                else
                    bdTemp = "null";

                String deleteIndicator;

                if ( fe.delete )
                    deleteIndicator = "D";
                else
                    deleteIndicator = " ";

                String jarIndicator;

                if ( fe.firstJarFile )
                    jarIndicator = "J";
                else
                    jarIndicator = " ";

                log.Both("  " + deleteIndicator + jarIndicator +
                         " [" + nextKey + "] " + entryIndicator +
                         " [" + entryValue + "] tmp[" + bdTemp + "]");
            }

            log.Both("Done");
        }

        Iterator keyIterator = keySet.iterator();

        while ( keyIterator.hasNext() ) {
            boolean isTempFile = false;  // to indicate if this is a temp input File

            counts[1]++;

            // Nab the next change

            String newKey = (String) keyIterator.next();
            FileEntry fe  = (FileEntry) outJarTab.get(newKey);

            String delInd = ( fe.delete       ? "d"  : " " );
            String jarInd = ( fe.firstJarFile ? "j"  : " " );
            String manInd = ( fe.manifestOnly ? "m " : "  " );

            if ( fe.bdTempFile != null )
                delInd = "c";   // to indicate this is a ByteDelta type of file, can not be a delete

            if ( fTest ) {
                System.out.println("Debug -- FileEntry @ BuildJarFile()");

                fe.display();
            }

            // Open an input stream, but only if the change
            // is for a new or modified file, or a new or
            // modified jar entry.

            String physicalFile = null; // used for error messages
            InputStream eis = null;

            if ( !(fe.delete || fe.manifestOnly || fe.firstJarFile) ) {

                if ( fe.bdTempFile != null ) { // In all cases, if we have a temp file, then use it

                    if ( fe.bdTempFile.toString().toLowerCase().startsWith("jar:") ) {
                        physicalFile = fe.bdTempFile.toString().replace('\\','/');

                        int bangPos = physicalFile.indexOf("!"); // this would be the end of the jarFileName

                        if ( bangPos == -1 )
                            Err(2, "Invalid URL to open, no ! found in (" + physicalFile + ")", abend);

                        JarFile jf;

                        try {
                            // NT has slash preceeding the drive letter.  Unix dosen't.

                            if ( physicalFile.substring(11,12).equals(":") )
                                jf = new JarFile(physicalFile.substring(10, bangPos));
                            else
                                jf = new JarFile(physicalFile.substring(9, bangPos));

                        } catch ( IOException ex ) {
                            Err(95, "Unable to Open " + physicalFile.substring(9,bangPos), ex);
                            continue;
                        }

                        ZipEntry je = jf.getEntry(physicalFile.substring(bangPos + 1));

                        try {
                            eis = jf.getInputStream(je);

                        } catch ( IOException ex ) {
                            Err(96, "Unable to open jar entry " + je.toString(), ex);
                            continue;
                        }

                    } else {
                        physicalFile = fe.bdTempFile.getAbsolutePath();

                        try {
                            eis = new FileInputStream(physicalFile);
                        } catch (IOException ex) {
                            Err(65, "failure opening " + physicalFile, ex);
                            continue;
                        }
                    }


                } else if ( fe.jarFile != null ) {
                    physicalFile = fe.jarFile.getName();

                    try {
                        if ( debug )
                            log.Both("Debug Opening jar entry: " + physicalFile);

                        eis = fe.jarFile.getInputStream(fe.jarEntry);

                    } catch ( IOException ex ) {
                        Err(22, "failure opening jar entry", ex);
                        continue;
                    }

                } else if ( fe.flatFile != null ) {
                    physicalFile = fe.flatFile.getName();

                    try {
                        if ( debug )
                            log.Both("Debug Opening flat file: " + physicalFile);

                        eis = new FileInputStream(fe.flatFile);

                    } catch ( IOException ex ) {
                        Err(21, "failure opening file", ex);
                        continue;
                    }

                } else {
                    System.out.println("Diag #44 " + fe.relativePath);
                }

            } else {
                if ( debug )
                    log.Both("Debug Special entry; no input required.");
            }

            // Create a jar entry and add it to the jar:

            if ( debug )
                log.Both("Debug #51 Creating entry on: " + fe.relativePath);

            if ( !(ff.getLeadingSlash()) && fe.relativePath.startsWith("/") )
                fe.relativePath = fe.relativePath.substring(1);

            // Jar entries require forward slashes:

            JarEntry entry = new JarEntry( fe.relativePath.replace('\\', '/') );

            if ( fe.zipComment != null )
                entry.setComment(fe.zipComment);

            if ( fe.zipExtra != null )
                entry.setExtra(fe.zipExtra);

            if ( fe.zipTimeStamp != 0 )
                entry.setTime(fe.zipTimeStamp);

            try {
                jos.putNextEntry(entry);
                log.Log(" + " + jarInd + delInd + manInd + entry.toString());

            } catch ( IOException e ) {
                Err(12, "unable add entry " + entry.toString(), e);
                continue;
            }

            try {
                if ( fe.delete || fe.manifestOnly || fe.firstJarFile || fe.dummyEntry ) {
                    // Write one byte for entries to be deleted.
                    jos.write(buffer, 0, 1);

                } else {
                    // Transfer the next data (file, or jar entry).

                    // System.out.println("Diag #102 entry.toString() = " + entry.toString());

                    while ( ((eis != null) && (bytesRead = eis.read(buffer)) != -1) )
                        jos.write(buffer, 0, bytesRead);

                    if ( eis != null )
                        eis.close();

                    if ( isTempFile )
                        fe.bdTempFile.delete();
                }

                jos.closeEntry();

            } catch ( IOException e ) {
                Err(14, "IOException: " + physicalFile, e);
                returnValue = false;
                break;
            }

            if ( verbosity > 3 )
                log.Both("adding " + entry.getName());

            inputSize  += entry.getSize();
            outputSize += entry.getCompressedSize();
        }

        if ( verbosity > 3 )
            log.Both("Closing  " + ff.getJarName());

        try {
            jos.close();

        } catch ( IOException e ) {
            Err(9, "IOException closing : " + ff.getJarName(), e);
            returnValue = false;
        }

        String percent = "??%";

        if ( inputSize > 0 )
            percent = hc.FmtNum(100-((outputSize*100)/inputSize),0,0,0) + "%";

        log.Both("input  size=" + hc.FmtNum(inputSize, 0,13,0));
        log.Both("output size=" + hc.FmtNum(outputSize,0,13,0) + ",  compression=" + percent);

        return returnValue;
    }

    protected void storeVirtualScripts(Attributes attribs)
    {
        int entryNum = 0;
        int preNum = 0;
        int postNum = 0;

        int numScripts = ff.getVirtualScriptsCount();

        for ( int scriptNo = 0; scriptNo < numScripts; scriptNo++) {
            FilterFile.Script nextScript = (FilterFile.Script)
                ff.getVirtualScripts(scriptNo);

            String scriptKey;

            if ( !nextScript.preScript )
                scriptKey = HelperList.mePostScript + postNum++;
            else if ( !nextScript.entryScript )
                scriptKey = HelperList.mePreScript + preNum++;
            else
                scriptKey = HelperList.meEntryScript + entryNum++;

            attribs.putValue(scriptKey, nextScript.name);

            String cmdPrefix = scriptKey + HelperList.meCmd;

            for ( int cmdNo = 0; cmdNo < nextScript.cmds.size(); cmdNo++ ) {
                String nextCmdKey = cmdPrefix + cmdNo;
                String nextCmd = (String) nextScript.cmds.elementAt(cmdNo);

                attribs.putValue(nextCmdKey, nextCmd);

                if ( cmdNo < nextScript.cmdArgs.size() ) {

                    Vector nextArgs = (Vector) nextScript.cmdArgs.elementAt(cmdNo);
                    if ( nextArgs != null ) {
                        String nextArgPrefix = nextCmdKey + HelperList.meArg;

                        for ( int argNo = 0; argNo < nextArgs.size(); argNo++ ) {
                            String nextArgKey = nextArgPrefix + argNo;
                            String nextArg = (String) nextArgs.elementAt(argNo);

                            attribs.putValue(nextArgKey, nextArg);

                            // System.out.println("Storing arg key=" + nextArgKey);
                            // System.out.println("Storing arg value=" + nextArg);
                        }
                    }
                }
            }

            String uncmdPrefix = scriptKey + HelperList.meUnCmd;

            for ( int uncmdNo = 0; uncmdNo < nextScript.uncmds.size(); uncmdNo++ ) {
                String nextUncmdKey = uncmdPrefix + uncmdNo;
                String nextUncmd = (String) nextScript.uncmds.elementAt(uncmdNo);

                attribs.putValue(nextUncmdKey, nextUncmd);

                if ( uncmdNo < nextScript.uncmdArgs.size() ) {

                    Vector nextArgs = (Vector) nextScript.uncmdArgs.elementAt(uncmdNo);
                    if ( nextArgs != null ) {
                        String nextArgPrefix = nextUncmdKey + HelperList.meArg;
                        
                        for ( int argNo = 0; argNo < nextArgs.size(); argNo++ ) {
                            String nextArgKey = nextArgPrefix + argNo;
                            String nextArg = (String) nextArgs.elementAt(argNo);
                            
                            attribs.putValue(nextArgKey, nextArg);
                            
                            // System.out.println("Storing arg key=" + nextArgKey);
                            // System.out.println("Storing arg value=" + nextArg);
                        }
                    }
                }
            }
        }
    }

    protected JarOutputStream CreateJarStream(String jarName, Manifest manifest)
    {
        try {
            return new JarOutputStream(new FileOutputStream(jarName), manifest);

        } catch ( FileNotFoundException e ) {
            Err(17, "FileNotFound for " + jarName, e);

        } catch ( IOException e ) {
            Err(13, "IOException: " + jarName, e);
        }

        return null;
    }

    protected FileInputStream OpenInputStream(File someFile)
    {
        try {
            return new FileInputStream(someFile);

        } catch ( IOException e ) {
            Err(8, "unable to open " + someFile.toString(), e);
        }

        return null;
    }

    // Action        --  Delete or Update or Add
    // jf            --  the second of two jar files being compared (the new one), null when a flatFile
    // je            --  null when a flat File or a jar entry showing a difference
    // flatFile      --  a file showing a difference (null if a jar File)
    // delete        --  drives the xtr_delete for a jar entry or flat file
    // startingTree  --  the part of the absolute path that gets removed from flatfiles and jar files
    // firstJarEntry --  true if this is the first time in for a jar file, so we may affect the xtr_jar
    // dblBang       --  true when this entry is for a child jar entry

    // FileEntry priorEntry = outJarTab.get(fe.relativePath);

    protected FileEntry RecordChange(int action,
                                     JarFile jf,
                                     JarEntry je,
                                     File flatFile,
                                     boolean delete,
                                     String startingTree,
                                     boolean firstJarEntry,
                                     boolean dblBang,
                                     File bdTempFile,
                                     boolean absPath)
    {
        //Note:
        //   The attributes are taken from the fe.attribs not the jarEntry
        //   The existance of a bdTempFile (ByteDelta Temp FIle) implies we have generated one

        String caName;   // case adjusted Name

        // if firstJarEntry & dblBang then this is for the main attributes

        FileEntry fe    = new FileEntry();
        fe.action       = action;
        fe.jarFile      = jf;
        fe.jarEntry     = je;
        fe.flatFile     = flatFile;
        fe.delete       = delete;
        fe.relativePath = "";
        fe.firstJarFile = firstJarEntry;  // if true then relpath contains the jaronly
        fe.manifestOnly = dblBang;
        fe.bdTempFile   = bdTempFile;
        fe.absPath      = absPath;        // if this entry is an absolute Path

        if ( flatFile != null ) { // it is a flat file

            if ( fe.bdTempFile == null ) // is this a bytedelta generated file
                fe.fileSize = flatFile.length();
            else
                fe.fileSize = fe.bdTempFile.length();

            String fileName = flatFile.toString().replace('\\', '/');

            if ( fileName.startsWith(startingTree) )
                fe.relativePath = flatFile.toString().substring(startingTree.length()).replace('\\', '/');
            else
                fe.relativePath = flatFile.toString().replace('\\', '/');

            // 119736
            if( ff.getCollectPermissions() )
                fe.permissions = hc.getPermissions(flatFile, debug);
            fe.permisAreSet = true;

        } else if ( je != null ) {
            fe.fileSize     = je.getSize();
            fe.zipComment   = je.getComment();
            fe.zipExtra     = je.getExtra();
            fe.zipTimeStamp = je.getTime();

            // if ( fe.zipComment != null )
            //   log.Both("Diag #01 Zip Comment for " + je.getName() + "; " + fe.zipComment );
            //
            // if ( fe.zipExtra != null )
            //   log.Both("Diag #02 Zip Extra for " + je.getName() + "; " + fe.zipExtra.toString());

            if ( jf.getName().replace('\\', '/').startsWith(startingTree) )
                fe.relativePath = jf.getName().substring(startingTree.length()).replace('\\', '/');
            else
                fe.relativePath = jf.getName().replace('\\', '/');

            if ( firstJarEntry ) {
                if ( dblBang ) {  //  for Jar main manifest
                    fe.firstJarFile  = false;   // was set to 'firstJarEntry'

                    fe.relativePath += childDividor;
                    fe.relativePath += childDividor;

                    try {
                        Manifest jfmf = fe.jarFile.getManifest();

                        if ( jfmf == null )
                            log.Both("Note: No manifest for " +  jf.getName() );
                        else
                            fe.attribs = jfmf.getMainAttributes();

                    } catch ( IOException ex ) {
                        Err(51, "Failure getting manifest for " + jf.getName(), ex);
                    }

                } else {
                    // setting the xtr_jar
                    fe.AddAttrib(HelperList.meJar, HelperList.meTrue);
                }

            } else {
                // For jar child entries:

                fe.relativePath += childDividor;

                // Needed to indicate that this for manifest entries.

                if ( dblBang ) {
                    fe.relativePath += childDividor;

                    try {
                        fe.attribs = je.getAttributes();

                    } catch ( IOException e ) {
                        Err(32, "jarEntry.getAttibutes() failed for (" + jf + ")" +
                                " (" + fe.relativePath + ") " +
                                e.getMessage());
                    }
                }

                fe.relativePath += je.getName();
            }

        } else {
            Err(28, "Bad change record, neither flatFile nor Jar.");
            return fe;
        }

        if ( ff.getCaseSensitive() )
            caName = fe.relativePath;
        else
            caName = fe.relativePath.toLowerCase();

        if ( debug )  {
            log.Log("Debug - #50 relativePath     : " + fe.relativePath);
            log.Log("Debug - #50 action           : " + fe.action);
            log.Log("Debug - #50 jarFile          : " + fe.jarFile);
            log.Log("Debug - #50 jarEntry         : " + fe.jarEntry);
            log.Log("Debug - #50 flatFile         : " + fe.flatFile);
            log.Log("Debug - #50 delete           : " + fe.delete);
            log.Log("Debug - #50 firstJarFile     : " + fe.firstJarFile);
            log.Log("Debug - #50 deleteBeforeWrite: " + fe.deleteBeforeWrite);
            log.Log("Debug - #50 addOnly          : " + fe.addOnly);
            log.Log("Debug - #50 replaceOnly      : " + fe.replaceOnly);
            log.Log("Debug - #50 perInstance      : " + fe.perInstance);
            log.Log("Debug - #50 asInstallable    : " + fe.asInstallable);
            log.Log("Debug - #50 asApplication    : " + fe.asApplication);
            log.Log("Debug - #50 asInstalled      : " + fe.asInstalled);
            log.Log("Debug - #50 asInstalled      : " + fe.asMetadata);
            log.Log("Debug - #50 nameRule         : " + fe.nameRule);
            log.Log("Debug - #50 manifestOnly     : " + fe.manifestOnly);
            log.Log("Debug - #50 attribs          : " + fe.attribs);
            log.Log("Debug - #50 bdTempFile       : " + fe.bdTempFile);
            log.Log("Debug - #50 absPath          : " + fe.absPath);
        }

        FilterFile.HTEntry hte = ff.deleteBeforeWrite.matchOf(caName);

        if ( hte != null ) {
            fe.deleteBeforeWrite = true;
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meDeleteBeforeWrite, HelperList.meTrue);

            log.Log(" deleteBeforeWrite set for " + fe.relativePath);
        }

        hte = ff.addOnlyFiles.matchOf(caName);

        if ( hte != null ) {
            fe.addOnly = true;
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meAddFile, HelperList.meTrue);

            log.Log(" addOnly set for " + fe.relativePath);
        }

        hte = ff.replaceOnlyFiles.matchOf(caName);

        if ( hte != null ) {
            fe.replaceOnly = true;
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meReplaceFile, HelperList.meTrue);

            log.Log(" replaceOnly set for " + fe.relativePath);
        }

        hte = ff.perInstance.matchOf(caName);

        if ( hte != null ) {
            fe.perInstance = true;
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.mePerInstance, HelperList.meTrue);

            log.Log(" perInstance set for " +  fe.relativePath);

            filesPerInstance++;
        }

        hte = ff.asInstallable.matchOf(caName);

        if ( hte != null ) {
            // 'installableApps' must be used before the path
            // to distinguish this entry from 'application'
            // entries.

            // Preprocessing will expand installable EAR files
            // relative to <earTmp>/<nodeName>/installableApps.

            if ( !fe.relativePath.startsWith(earInstallableDir) ) {
                Err(127, "The installable EAR entry " + fe.relativePath +
                         " is not be located in " + earInstallableDir);
                exitRC++;
                return fe;
            }

            fe.asInstallable = true;
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meAsInstallable, HelperList.meTrue);

            log.Log(" asInstallable set for " +  fe.relativePath);

            filesAsInstallable++;
        }

        hte = (FilterFile.HTEntry) ff.asApplication.matchOf(caName);

        if ( hte != null ) {
            // 'applications' must be used before the path to distinguish
            // this entry from 'installable' entries.

            // Preprocessing will expand application EAR files relative
            // to <earTmp>/<nodeName>/applications.

            if ( !fe.relativePath.startsWith(earApplicationsDir) ) {
                Err(128, "The application EAR entry " + fe.relativePath +
                         " is not be located in " + earApplicationsDir);
                exitRC++;
                return fe;
            }

            fe.asApplication = true;
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meAsApplication, HelperList.meTrue);

            log.Log(" asApplication set for " + fe.relativePath);

            filesAsApplication++;

            if ( hte.nameRule != null ) {
                fe.nameRule = HelperList.encodeNameRule(hte.nameRule, fe.relativePath);

                filesAsNameRule++;
                fe.AddAttrib(HelperList.meNameRule, fe.nameRule);
                log.Log(" asNameRule set for " + fe.relativePath + " >> " + fe.nameRule);
            }
        }

        hte = ff.asInstalled.matchOf(caName);

        if ( hte != null ) {
            // Each update to an installed ear is per configuration instance
            // and per node of that instance.  Each node has its own
            // binary files location which will be used as the target
            // for the update.

            fe.asInstalled = true;
            filesAsInstalled++;
            fe.AddAttrib(HelperList.meAsInstalled, HelperList.meTrue);
            log.Log(" asInstalled set for " + fe.relativePath);

            if ( hte.altFlag ) {
                fe.asMetadata = true;
                filesAsMetadata++;
                fe.AddAttrib(HelperList.meAsMetadata, HelperList.meTrue);
                log.Log(" asMetadata set for " + fe.relativePath);
            }

            if ( hte.nameRule != null ) {
                fe.nameRule = HelperList.encodeNameRule(hte.nameRule, fe.relativePath);

                filesAsNameRule++;
                fe.AddAttrib(HelperList.meNameRule, fe.nameRule);
                log.Log(" asNameRule set for " + fe.relativePath + " >> " + fe.nameRule);
            }

            hte.increment(oldFiles, newFiles);
        }

        if ( fe.delete )
            fe.AddAttrib(HelperList.meDelete, HelperList.meTrue);

        if ( fe.absPath ) {
            fe.AddAttrib(HelperList.meAbsolutePath, HelperList.meTrue);

            log.Log(" Absolute path set for " + fe.relativePath );

            if ( fe.perInstance )
                log.Log(" Warning: absolute path set with per-instance processing: " + fe.relativePath);
            else if ( fe.asInstallable || fe.asApplication || fe.asInstalled ) 
                log.Log(" Warning: absolute path set with EAR processing: " + fe.relativePath);
        }

        hte = ff.getChmod(caName);

        if ( hte != null ) {
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meChmod, hte.vData);

            log.Log(" chmod " + hte.vData + " set for " +  fe.relativePath);

            ff.removeChmod(caName);
        }

        if ( fTest ) {
            // Used for debugging - to display the content of one of the hashtables.

            if ( f1Time ) {
                Iterator eNum = ff.getReNameEnum();
                while ( eNum.hasNext() ) {
                    FilterFile.HTEntry hte1 = (FilterFile.HTEntry) eNum.next();
                    log.Both("Diag #77 (" + hte1.key + ")");
                }

                f1Time = false;
            }
        }

        hte = ff.getNoRestore(caName);

        if ( hte != null ) {
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meNoRestore, HelperList.meTrue);

            if ( hte.helper )
                fe.AddAttrib(HelperList.meHelperClass, HelperList.meTrue);

            log.Log(" noRestore set for " +  fe.relativePath);
        }

        hte = (FilterFile.HTEntry) ff.getNoDelete(caName);

        if ( hte != null ) {
            hte.increment(oldFiles, newFiles);
            fe.AddAttrib(HelperList.meNoDelete, HelperList.meTrue);
            log.Log(" noDelete set for " +  fe.relativePath);
        }

        String origPath   = fe.relativePath;  // saved for error messages
        String origPathLC = origPath.toLowerCase();

        // Scan the include list to tell if the leading part of the path
        // is to be removed.

        boolean alreadyGotOne = false;

        for ( int i = 0; i < ff.getIncludesCount(); i++ ) {
            hte = (FilterFile.HTEntry) ff.getIncludes(i);

            String iKey   = hte.key;                  // iKey has astericks
            String sKey   = iKey.replace('*', '/');   // sKey has slashes
            String sKeyLC = sKey.toLowerCase();

            boolean match;

            if ( ff.getCaseSensitive() )
                match = origPath.startsWith(sKey);
            else
                match = origPathLC.startsWith(sKeyLC);

            if ( match ) {
                if ( hte.include ) {
                    if (alreadyGotOne) {
                        Err(4, "Conflicting Include/IncludeCut statements for : " + sKey);
                        hte.include = false;  // to turn off the error

                    } else {
                        if ( hte.cut ) {
                            fe.relativePath = origPath.substring(iKey.length() -1);
                            alreadyGotOne = true;
                        }
                    }
                }
            }
        }

        // Process renaming options:

        for ( int ri = 0; ri < ff.getReNameCount(); ri++ ) {
            hte = ff.getReName(ri);
            boolean equal;

            if (hte.absPath) {
                if ( ff.getCaseSensitive() )
                    equal = fe.relativePath.equals(hte.key);
                else
                    equal = fe.relativePath.equalsIgnoreCase(hte.key);

                if ( equal ) {
                    log.Log("Renaming filespec (" + fe.relativePath + ")");
                    hte.increment(oldFiles, newFiles);
                    fe.relativePath = hte.vData;
                    log.Log("   to     (" + fe.relativePath + ")");
                }

            } else {
                if ( fTest )
                    log.Both("Diag #88 " + fe.relativePath);

                if ( ff.getCaseSensitive() ) {
                    equal = fe.relativePath.startsWith(hte.key);
                } else {
                    String temp = fe.relativePath.toLowerCase();
                    equal = temp.startsWith(hte.key.toLowerCase());
                }

                if ( fe.relativePath.startsWith(hte.key) ) {
                    log.Log("Renaming Path  (" + fe.relativePath + ")");
                    hte.increment(oldFiles, newFiles);
                    fe.relativePath = hte.key + fe.relativePath.substring(hte.key.length());
                    log.Log("   to     (" + fe.relativePath + ")");
                }
            }
        }

        boolean good2go = true;
        FileEntry pfe = (FileEntry) outJarTab.get(fe.relativePath);

        if (pfe != null) {    // is this entry already there
            if ( action == k_Delete ) {
                //log.Err(40,
                log.Log("Error 40 -- Ignoring action for " + origPath +
                        ", first action was " + pfe.actionName() +
                        ", dup action is " + fe.actionName());
                good2go = false;
                errCount++;

            } else if ( action == k_Add ) {

                if ( pfe.action == k_Delete)  {
                    //log.Err(41,
                    log.Log("Error 41 -- Superseding action for " + origPath +
                            ", first action was " + pfe.actionName() +
                            ", dup action is " + fe.actionName());
                    errCount++;

                } else {
                    //log.Err(42,
                    log.Log("Error 42 -- Ignoring action for " + origPath +
                            ", first action was " + pfe.actionName() +
                            ", dup action is " + fe.actionName());
                    errCount++;
                    good2go = false;
                }

            } else if ( action == k_Update ) {
                if ( pfe.action == k_Update ) {
                    //log.Err(43,
                    log.Log("Error 43 -- Ignoring action for " + origPath +
                            ", first action was " + pfe.actionName() +
                            ", dup action is " + fe.actionName());
                    errCount++;
                    good2go = false;

                } else {
                    //log.Err(44,
                    log.Log("Error 44 -- Superseding action for " + origPath +
                            ", first action was " + pfe.actionName() +
                            ", dup action is " + fe.actionName());
                    errCount++;
                }

            } else {
                Err(45, "Programming Error: invalid action (" + action + ") for " + origPath);
            }

            //  Note: for the above errors we wish to have the entry in the log only
            //        and not on the screen
        }

        String msg1;

        if ( fe.bdTempFile == null )
            msg1 = " null ";
        else
            msg1 = fe.bdTempFile.toString();

        if ( good2go ) {
            // Using the relative path instead of the argument key
            // for the change table.

            outJarTab.put(fe.relativePath, fe);
        }

        return fe;
    }

    protected void CountExt(String fileName, int oldNew, File theFile )
    {
        String extension = null;
        String lcFileName = fileName.toLowerCase();

        int lastPeriod = lcFileName.lastIndexOf(".");

        if ( lastPeriod == -1 )
            extension = "(none)";
        else
            extension = lcFileName.substring(lastPeriod);

        FilterFile.HTEntry hte = (FilterFile.HTEntry) extCounts.get(extension);

        if ( hte == null ) {
            hte = new FilterFile.HTEntry(isNotHelper);
            extCounts.put(extension, hte);
        }

        hte.increment(oldNew);

        String lcExtension = extension.toLowerCase();

        if ( isArchive(lcExtension) ) {
            totJars[oldNew]++;
            totJBytes[oldNew] += theFile.length();

        } else {
            totFiles[oldNew]++;
            totBytes[oldNew] += theFile.length();
        }

    }

    protected boolean isArchive(String name)
    {
        return ( name.endsWith(".jar") ||
                 name.endsWith(".ear") ||
                 name.endsWith(".war") ||
                 name.endsWith(".rar") );
    }

    // Answer the position of the first different (1 based);
    // answer zero if there are no differences.

    // TFB: Compare bug -- will return zero if the files are the same
    // TFB: and differ on the first character.
    // TFB:
    // TFB: Would get a NPE if openeing bf1 fails.
    // TFB: Too many length computations.

    // TFB: 'totRead' was int, but can be as large as the smallest
    // TFB: length.

    // TFB: Return 0 when an error occurs.  (Used to return -1.)

    protected static final int COMPARE_BUFFER_SIZE = 4096;

    protected long Compare(File f1, File f2)
    {
        long f1Length = f1.length(),
             f2Length = f2.length();

        if ( f1Length != f2Length ) {
            log.Log("   Size delta " +
                    " old=" + hc.FmtNum(f1Length, 0, 0, 0) +
                    " new=" + hc.FmtNum(f2Length, 0, 0, 0));

            return ( ((f1Length > f2Length) ? f2Length : f1Length) + 1 );
        }

        long diffPosition = 0; // Default this to 0, meaning, no difference
                               // was found.  This works both as the default
                               // value for the loop, and as the value in
                               // error cases.

        BufferedInputStream bf1 = null;
        BufferedInputStream bf2 = null;

        try {
            bf1 = new BufferedInputStream(new FileInputStream(f1), COMPARE_BUFFER_SIZE);
            // throws IOException, FileNotFoundException
            bf2 = new BufferedInputStream(new FileInputStream(f2), COMPARE_BUFFER_SIZE);
            // throws IOException, FileNotFoundException

            byte[] buf1 = new byte[COMPARE_BUFFER_SIZE];
            byte[] buf2 = new byte[COMPARE_BUFFER_SIZE];

            boolean readError = false;
            long totRead = 0;

            while ( (diffPosition == 0) && !readError && (totRead < f1Length) ) {
                int numRead1 = bf1.read(buf1); // throws IOException

                if ( numRead1 == -1 ) {
                    readError = true;
                    Err(130, "   read blocked during compare" +
                             ", old=" + f1.getAbsolutePath() + " [" + totRead + "]");

                } else {
                    int numRead2 = bf2.read(buf2); // throws IOException

                    if ( numRead2 == -1 ) {
                        readError = true;
                        Err(131, "   read blocked during compare" +
                                 ", new=" + f2.getAbsolutePath() + " [" + totRead + "]");

                    } else {
                        if ( numRead1 != numRead2 ) {
                            readError = true;
                            Err(132, "   uneven read during compare" +
                                     ", old=" + f1.getAbsolutePath() + " [" + numRead1 + "]" +
                                     ", new=" + f2.getAbsolutePath() + " [" + numRead2 + "]");

                        } else {
                            for ( int offset = 0; ( diffPosition == 0) && (offset < numRead1); offset++ ) {
                                if ( buf1[offset] != buf2[offset] ) {
                                    diffPosition = offset + totRead + 1;
                                    log.Log("   file difference at " + hc.FmtNum(diffPosition, 0, 0, 0));
                                }
                            }

                            if ( diffPosition == 0 )
                                totRead += numRead1;
                        }
                    }
                }
            }

        } catch ( FileNotFoundException e ) {
            Err(25, "FileNotFound", e);

        } catch ( IOException e ) {
            Err(26, "IOException", e);

        } finally {
            try {
                bf1.close(); // throws IOException
            } catch ( IOException e ) {
                Err(27, "IOException closing file", e);
            }

            try {
                if ( bf2 != null )
                    bf2.close(); // throws IOException
            } catch ( IOException e ) {
                Err(129, "IOException closing file", e);
            }
        }

        return diffPosition;
    }
    
    /**
     * Compares two archives, ignoring the Manifest files.
     * 
     * @param file1
     * @param file2
     * @return true if the archives are equal
     */
    protected boolean compareArchive( File file1, File file2 ) {
        ArchiveTable oldJar = new ArchiveTable();
        ArchiveTable newJar = new ArchiveTable();
        
        /*
         * Because the isArchive() method only looks at the 
         * filename of the java.io.File object, it is possible
         * for a directory ending in one of the archive extensions
         * to make it this far in our comparison. We want to return
         * false in this case so that the java.io.File object will
         * be passed on and compared as a flat file. 
         * 
         */
        if ( file1.isDirectory() || file2.isDirectory() )
        {
            log.Log( file1.getName() + " is a directory! Passing on to default comparison..." );
            return false;
        }

        /*
         * We're going to build two HashMaps. The key will be the 
         * entry name in the archive and the value will be the 
         * size. The entries in any embedded archive will be stored
         * as well. The key will be the archive's entry + "*" + the 
         * embedded entry. We will ignore any Manifest.mf files in 
         * the process
         */
        populateTable( oldJar, file1, "" );
        populateTable( newJar, file2, "" );
        
        /*
         * Compare our two populated HashMaps. We will extract every
         * entry (including embedded entries) and perform a binary
         * comparison.
         */
        return CompareArchiveTables( oldJar, newJar, file1, file2 );
    }

    /**
     * Compare the entries of the two tables. A binary comparison will be 
     * performed on each.
     * 
     * @param table1
     * @param table2
     * @param file1
     * @param file2
     * @return true or false
     */
    protected boolean CompareArchiveTables( ArchiveTable table1, ArchiveTable table2, File file1, File file2 ) 
    {
        ArchiveTableIterator tbl1Iterator = table1.tableIterator();
        
        boolean same = true;
        int count = 0, multiple = 1;
        int printValue = Math.min( Math.round( ( (float) ( table1.size() * 0.2 ) ) ), 50 );
        
        if ( table1.size() != table2.size() )
        {
            log.Log( "Table sizes are different!" );
            log.Log( "Table1: " + table1 );
            log.Log( "Table2: " + table2 );
            same = false;
        }
        
        if ( printValue == 0 )
            printValue = 1;

        try {
                
            while ( tbl1Iterator.hasNext() && same )
            {
                String key1 = (String) tbl1Iterator.next();
                List entries = tbl1Iterator.getCurrentList();
                
                File tempArchive1 = null;
                File tempArchive2 = null;
                
                if ( (++count) % printValue == 0 || count == 1 || count == table1.size() )
                    System.out.println( "Comparing archive entry " + count + " of " + table1.size() + "." );

                
                /*
                 * handle root entries
                 */
                
                if ( entries == null )
                {
                    tempArchive1 = file1;
                    tempArchive2 = file2;
                    
                    if ( !table2.containsKey( key1 ) )
                    {
                        same = false;
                        log.Log( key1 + " is different! " );
                        continue;
                    }
                    
                    //System.out.println( "Processing " + key1 );
                    
                    if ( !key1.endsWith( "/" ) )
                    {    
                        
                        File tempFile1 = File.createTempFile( "extractedFile", "tmp", tempArchive1.getParentFile() );
                        File tempFile2 = File.createTempFile( "extractedFile", "tmp", tempArchive2.getParentFile() );
                        
                        JarFileUtils.inflateEntry( tempArchive1, key1, tempFile1 );
                        JarFileUtils.inflateEntry( tempArchive2, key1, tempFile2 ); 
                        
                        long diff = Compare( tempFile1, tempFile2 );
                        
                        tempFile1.delete();
                        tempFile2.delete();
                        
                        if ( diff > 0 )
                        {
                            same = false;
                            log.Log( key1 + " is different! " );
                            //break;
                            
                        }
                    }
                }
                else
                {
                    tempArchive1 = File.createTempFile( "extractedOne", "jar", file1.getParentFile() );
                    tempArchive2 = File.createTempFile( "extractedTwo", "jar", file2.getParentFile() );
                    
                    //System.out.println( "Created temp files: " + tempArchive1.getAbsolutePath() + " , " + tempArchive2.getAbsolutePath() );
                    
                    //System.out.println( "Inflating nested jar: " + key1 );
                    
                    JarFileUtils.inflateNestedEntry( file1, key1, tempArchive1 );
                    JarFileUtils.inflateNestedEntry( file2, key1, tempArchive2 );
                    
                    key1 += ArchiveTable.SEPARATOR_STR;
                    
                    for ( int c = 0; ( c < entries.size() && same ); c++ )
                    {
                        String originalKey = null;
                        String value = (String) entries.get( c );
                        
                        originalKey = key1 + value;
                        
                        //System.out.println( "Processing key \"" + originalKey + "\"..." );
                        
                        if ( !table2.containsKey( originalKey ) )
                        {
                            same = false;
                            log.Log( originalKey + " is different! " );
                            //break;
                        }
                        
                        if ( !originalKey.endsWith( "/" ) )
                        {    
                            
                            File tempFile1 = File.createTempFile( "extractedFile", "tmp", tempArchive1.getParentFile() );
                            File tempFile2 = File.createTempFile( "extractedFile", "tmp", tempArchive2.getParentFile() );
                            
                            //System.out.println( "Created temp files: " + tempFile1.getAbsolutePath() + " , " + tempFile2.getAbsolutePath() );
                            
                            JarFileUtils.inflateEntry( tempArchive1, value, tempFile1 );
                            JarFileUtils.inflateEntry( tempArchive2, value, tempFile2 ); 
                            
                            long diff = Compare( tempFile1, tempFile2 );
                            
                            //System.out.println( value + " are equal? " + ( diff == 0 ) );
                            //System.out.println( "Deleting temp files: " + tempFile1.getAbsolutePath() + " , " + tempFile2.getAbsolutePath() );
                            
                            tempFile1.delete();
                            tempFile2.delete();
                            
                            if ( diff > 0 )
                            {
                                same = false;
                                log.Log( originalKey + " is different! " );
                                //break;
                                
                            }
                        }
                        
                    }
                    
                    //System.out.println( "Deleting temp files: " + tempArchive1.getAbsolutePath() + " , " + tempArchive2.getAbsolutePath() );
                    
                    tempArchive1.delete();
                    tempArchive2.delete();
                }
                
                
            }
        }
        catch (IOException ioe ) {
            System.out.println( ioe );
            same = false;
        }

        return same;

        /*while ( table1Keys.hasNext() )
        {
            Object key1 = table1Keys.next();

            //System.out.println( "Processing key \"" + key1.toString() + "\"..." );
            
            if ( (++count) % printValue == 0 || count == 1 || count == table1.size() )
                System.out.println( "Comparing archive entry " + count + " of " + table1.size() + "." );
            
            /*
             * if table2 doesn't contain our key, then they aren't the same
             *
            if ( !table2.containsKey( key1 ) )
            {
                same = false;
                break;
            }

            Long value1 = (Long) table1.get( key1 );
            Long value2 = (Long) table2.get( key1 );
            
            if ( value2 == null || value1 == null) 
            {
                same = false;
                break;
            }

            /*
             * Perform the binary comparison if we have a file entry.
             *
            if ( !((String)key1).endsWith( "/" ) )
                same = inflateAndByteCompare( (String) key1, file1, file2 );

            if ( !same ) 
            {
                same = false;
                break;
            }
        }
        
        //System.out.println( file1.getAbsolutePath() + " and " + file2.getAbsolutePath() + " are equal? " + same );

        return same; */

    }
    
    /**
     * Inflate entries in a jar file and compare.
     * 
     * @param key
     * @param file1
     * @param file2
     * @return true or false
     */
    private boolean inflateAndByteCompare( String key, File file1, File file2 )
    {
        int index = key.indexOf( "*" );
        File tempFile1 = null;
        File tempFile2 = null;
        File jarFile1 = null;
        File jarFile2 = null;

        //System.out.println("inflateAndByteCompare() entry ==> p1="+key+"; p2="+file1+"; p3="+file2);
        
        boolean value = false;
        
        try {
        
            if ( index >= 0 ) 
            {
                //the entry is inside a jar file
                String jarName = key.substring( 0, index );
                key = key.substring( index+1 );
                
                jarFile1 = File.createTempFile( "extractedJar", "jar", file1.getParentFile() );
                JarFileUtils.inflateEntry( file1, jarName, jarFile1 );
                file1 = jarFile1;
                
                jarFile2 = File.createTempFile( "extractedJar", "jar", file2.getParentFile() );
                JarFileUtils.inflateEntry( file2, jarName, jarFile2 );
                file2 = jarFile2;
            }
            
            tempFile1 = File.createTempFile( "extractedFile", "tmp", file1.getParentFile() );
            JarFileUtils.inflateEntry( file1, key, tempFile1 );
            
            tempFile2 = File.createTempFile( "extractedFile", "tmp", file2.getParentFile() );
            JarFileUtils.inflateEntry( file2, key, tempFile2 );
            
            long result = Compare( tempFile1, tempFile2 );
            
            if ( result != 0 )
                System.out.println( key + " is different!" );
            
            //System.out.println( "Difference in bytes ( " + tempFile1.getAbsolutePath() + " , " + tempFile2.getAbsolutePath() + " ) : " + result );
            
            if ( result != 0 )
                value = false;
            else 
                value = true;
        }
        catch (IOException ioe) {
            System.out.println( "Error caught during binary comparison: " + ioe );
            return false;
        }
        finally {
            if ( tempFile1 != null )
                tempFile1.delete();
            if ( tempFile2 != null )
                tempFile2.delete();
            if ( jarFile1 != null )
                jarFile1.delete();
            if (jarFile2 != null )
                jarFile2.delete();
        }

        //System.out.println("inflateAndByteCompare() exit ==> p1="+value);
        
        return value;
    }

    /**
     * Populate the table with the entries of the specified
     * archive file.
     * 
     * @param table the table to populate
     * @param file the archive
     * @param prefix if the archive is embedded, this is the prefix to id the entry
     */
    private void populateTable( HashMap table, File file, String prefix ) {
        JarFile jf = null;

        //System.out.println( "Entry ==> p1=table; p2=" + file.getAbsolutePath() + "; p3=" + prefix );

        try {
            jf = new JarFile( file );

            /*
             * Cycle through the entries, skipping the manifest
             */
            Enumeration eNum = jf.entries();
            while ( eNum.hasMoreElements() ) {
                JarEntry je  = (JarEntry) eNum.nextElement();
                String key   = je.getName();

                if ( prefix.length() > 0 )
                {
                    key = prefix + "*" + key;
                }

                if ( (key.endsWith("META-INF/MANIFEST.MF")) ||
                     (key.endsWith("META-INF/")) ||
                     (key.endsWith("version.properties")) ||
                     (key.endsWith("/")) )
                {
                    log.Log("Note: Bypassing version-specific files or directory \"" + key + "\"" );
                    continue;
                }

                long size = je.getSize();

                if ( isJar( key ) )
                {
                    int index = key.lastIndexOf( "*" );
                    String tempkey = key;
                    
                    if ( index > 0 ) 
                    {
                        tempkey = key.substring( index + 1 );
                    }
                    
                    File tempFile = File.createTempFile( "extractedJar", "jar", file.getParentFile() );
                    JarFileUtils.inflateEntry( file, tempkey, tempFile );
                    
                    populateTable( table, tempFile, key );

                    tempFile.delete();
                }
                else {
                    //System.out.println( "Adding entry: " + key );
                    table.put( key, new Long( size ) );
                }
            }

            //jf.close();
        }
        catch ( IOException ioe ) {
            System.out.println( ioe );
            ioe.printStackTrace();
        }
        catch ( Exception ex) {
            System.out.println( "An error occurred populating the table.");
            System.out.println( "file= " + file + "\tprefix= " + prefix );
            ex.printStackTrace();
        }
        finally {
            try 
            {
                if ( jf != null )
                    jf.close();
            }
            catch (IOException ioe)
            {
                log.Both( "Unable to close jar file: " + jf.getName() );
            }
        }

        //System.out.println( "Exit ==> p1=table ; p2=" + file.getAbsolutePath() + "; p3=" + prefix );
    }

    private boolean isJar( String entry ) 
    {
        if ( entry.endsWith( "jar" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected int CompareJar(File f1, File f2, String componentName)
    {
        int returnValue = 0;

        HashMap oldJar = new HashMap();
        HashMap newJar = new HashMap();

        Manifest m1 = null,
                 m2 = null;
        JarFile  jf = null;

        try {
            jf = new JarFile(f1);
            m1 = jf.getManifest();

            Enumeration eNum = jf.entries();
            while ( eNum.hasMoreElements() ) {
                JarEntry je  = (JarEntry) eNum.nextElement();
                String key   = je.getName();

                if ( (key.equalsIgnoreCase("META-INF/MANIFEST.MF")) ||
                     (key.equalsIgnoreCase("META-INF/")) ) {
                    // log.Log("Note: manifest is bypassed  ");
                    continue;
                }

                FileEntry fe = new FileEntry();
                fe.jarFile   = jf;
                fe.jarEntry  = je;
                fe.flatFile  = null;
                fe.delete    = false;
                fe.bdTempFile= null;
                fe.fileSize  = je.getSize();

                if ( fe.fileSize == -1 ) {
                    fe.fileSize = 1;
                    log.Both("Warning:  file size could not be determined for " + je.getName());
                }

                oldJar.put(key, fe);
            }

        } catch ( IOException e ) {
            Err(18, "processing jar file " + f1.toString(), e);
            returnValue = 8;
        }

        FileEntry fe = null;

        try {
            jf = new JarFile(f2);
            m2 = jf.getManifest();

            Enumeration eNum = jf.entries();
            while ( eNum.hasMoreElements() ) {
                JarEntry je = (JarEntry) eNum.nextElement();
                String key  = je.getName();

                if ( (key.equalsIgnoreCase("META-INF/MANIFEST.MF")) ||
                     (key.equalsIgnoreCase("META-INF/")) ) {
                    // log.Log("Note: manifest is bypassed  ");
                    continue;
                }

                fe            = new FileEntry();
                fe.jarFile    = jf;
                fe.jarEntry   = je;
                fe.flatFile   = null;
                fe.delete     = false;
                fe.fileSize   = je.getSize();
                fe.bdTempFile = null;

                if ( fe.fileSize == -1 ) {
                    fe.fileSize = 1;
                    log.Both("Warning:  file size could not be determined for " + je.getName());
                }

                newJar.put(key, fe);
            }

        } catch ( IOException ex ) {
            Err(11, "processing jar file " + f2.toString(), ex);
            System.exit(8);
        }

        boolean firstJarEntry = true;
        boolean same;

        if ( (m1 == null) && (m2 == null) ) {
            log.Log("Comparing jar: " + f1.toString() + ", no manifest is present.");

            same = true;

        } else if ( (m1 == null) || (m2 == null) ) {
            log.Log("Comparing jar: " + f1.toString() + ", the whole manifest was added or removed.");

            same = false;

        } else {
            same = CompareAttribs(m1.getMainAttributes(), m2.getMainAttributes(), jf.getName(), "Main-Attribs");
        }

        if ( !same ) {
            // Record the presence of a jar file, with changes following.

            FileEntry fe1 = RecordChange(k_Update,
                                         fe.jarFile,
                                         fe.jarEntry,
                                         null,
                                         addEntry,
                                         ff.getNewTree(),
                                         firstJarEntry,
                                         noManEntry,
                                         NullBDTempFile,
                                         relPath);

            if ( componentName != null ) {
                // log.Both("Diag #101 adding Component " + componentName + " for " + f1.getAbsolutePath() );

                fe1.components = componentName;
            }

            // Record the main attributes for this jar entry:

            RecordChange(k_Update,
                         fe.jarFile,
                         fe.jarEntry,
                         null,
                         addEntry,
                         ff.getNewTree(),
                         firstJarEntry,
                         manEntry,
                         NullBDTempFile,
                         relPath);

            firstJarEntry = false;
        }

        // After we call CompareTables the oldJar will contain files that need to be removed
        // newJar will contain files that need to be coppied either because they are different or
        // because they are new files

        CompareTables(oldJar, newJar);

        // The entries here are to be deleted.
        Iterator eNum = oldJar.keySet().iterator();

        while ( eNum.hasNext() ) {
            String key = (String) eNum.next();
            fe = (FileEntry) oldJar.get(key);

            je2delete++;
            jebytes2del += fe.fileSize;

            if ( firstJarEntry ) {
                // Record the presence of a jar file, with changes following:

                FileEntry fe1 = RecordChange(k_Update,
                                             fe.jarFile,
                                             fe.jarEntry,
                                             null,
                                             addEntry,
                                             ff.getOldTree(),
                                             firstJarEntry,
                                             noManEntry,
                                             NullBDTempFile,
                                             relPath);

                if ( componentName != null ) {
                    // log.Both("Diag #102 adding Component " + componentName + " for " + f1.getAbsolutePath() );

                    fe1.components = componentName;
                }

                firstJarEntry = false;
            }


            Attributes atts = null;

            try {
                atts = fe.jarEntry.getAttributes();

            } catch ( IOException e ) {
                Err(15, "getAttribs() for " + fe.jarEntry.toString(), e);
            }

            if ( atts != null ) {
                // Record that attributes are to be deleted:

                atts.putValue(HelperList.meDelete, HelperList.meTrue);

                RecordChange(k_Update,
                             fe.jarFile,
                             fe.jarEntry,
                             null,
                             addEntry,
                             ff.getOldTree(),
                             firstJarEntry,
                             manEntry,
                             NullBDTempFile,
                             relPath);
            }

            // Record that a jar entry is to be deleted:

            FileEntry fe1 = RecordChange(k_Update,
                                         fe.jarFile,
                                         fe.jarEntry,
                                         null,
                                         deleteEntry,
                                         ff.getOldTree(),
                                         firstJarEntry,
                                         noManEntry,
                                         NullBDTempFile,
                                         relPath);

            if ( componentName != null )
                fe1.components = componentName;
        }

        // Record entry changes:

        eNum = newJar.keySet().iterator();
        while ( eNum.hasNext() ) {
            String key = (String) eNum.next();
            fe = (FileEntry) newJar.get(key);

            je2add++;
            jebytes2add += fe.fileSize;

            if ( firstJarEntry ) {
                // Record jar entry changes; start the jar:

                FileEntry fe1 = RecordChange(k_Update,
                                             fe.jarFile,
                                             fe.jarEntry,
                                             null,
                                             addEntry,
                                             ff.getNewTree(),
                                             firstJarEntry,
                                             noManEntry,
                                             NullBDTempFile,
                                             relPath);

                if ( componentName != null )
                    fe1.components = componentName;

                firstJarEntry = false;
            }

            Attributes atts = null;

            try {
                atts = fe.jarEntry.getAttributes();

            } catch ( IOException e ) {
                Err(31, "getAttribs() for " + fe.jarEntry.toString(), e);
            }

            if (atts != null) {
                // Record an 'add entry attributes" change:

                RecordChange(k_Update,
                             fe.jarFile,
                             fe.jarEntry,
                             null,
                             addEntry,
                             ff.getNewTree(),
                             firstJarEntry,
                             manEntry,
                             NullBDTempFile,
                             relPath);
            }

            // Record an 'add' change:

            RecordChange(k_Update,
                         fe.jarFile,
                         fe.jarEntry,
                         null,
                         addEntry,
                         ff.getNewTree(),
                         firstJarEntry,
                         noManEntry,
                         NullBDTempFile,
                         relPath);
        }

        return returnValue;
    }

    protected void takeOut(HashMap map, Vector keys)
    {
        for ( int keyNo = 0; keyNo < keys.size(); keyNo++ )
            map.remove( keys.elementAt(keyNo) );
    }

    // After processing, entries left in the old table are those
    // which were present only in the old table.  These entries are
    // handled with a delete action.
    //
    // After processing, entries left in the new table are either
    // completely new, or were present in the old table but have
    // changed content or attributes.

    protected void CompareTables(HashMap oldHt, HashMap newHt)
    {
        // if the files and attribs are equal we will remove them from the tables

        long matched      = 0,
             bytesMatched = 0,
             changed      = 0,
             bytesChanged = 0,
             deleted      = 0,
             bytesDeleted = 0,
             added        = 0,
             bytesAdded   = 0;

        log.Log("Comparing Trees:");
        log.Log("   Old Entries: " + oldHt.size());
        log.Log("   New Entries: " + newHt.size());

        log.Log(" ");

        log.Log("Processing old tree:");

        Vector removeFromOld = new Vector(); // These are in both trees.
        Vector removeFromNew = new Vector(); // These are in both trees, and are identical.

        Iterator eNum = oldHt.keySet().iterator();

        while ( eNum.hasNext() ) {
            String oldKey = (String) eNum.next();

            log.Log("   Testing Entry: " + oldKey);

            FileEntry oldFE = (FileEntry) oldHt.get(oldKey);
            JarFile oldJF = oldFE.jarFile;

            if ( newHt.containsKey(oldKey) ) {
                removeFromOld.add(oldKey);

                FileEntry newFE = (FileEntry) newHt.get(oldKey);
                JarFile newJF = newFE.jarFile;

                InputStream oldInputStream = null;
                InputStream newInputStream = null;

                try {
                    oldInputStream = oldJF.getInputStream(oldFE.jarEntry); // throws IOException
                    newInputStream = newJF.getInputStream(newFE.jarEntry); // throws IOException

                    boolean same = DiffEntries(oldInputStream,
                                               newInputStream,
                                               oldJF.getName(),
                                               oldFE.jarEntry.getName());

                    if ( same ) {
                        same = CompareAttribs(oldFE.jarEntry.getAttributes(), // throws IOException
                                              newFE.jarEntry.getAttributes(), // throws IOException
                                              oldJF.getName(),
                                              oldFE.jarEntry.getName());

                        if ( same )
                            log.Log("   Content and attributes are the same.");
                        else
                            log.Log("   Content is the same but attributes have changed.");

                    } else {
                        log.Log("   Content has changed.");
                    }

                    if ( same ) {
                        removeFromNew.add(oldKey);

                        matched++;
                        bytesMatched += oldFE.jarEntry.getSize();

                    } else {
                        je2replace++;
                        jebytes2rep += newFE.jarEntry.getSize();

                        changed++;
                        bytesChanged += newFE.jarEntry.getSize();
                    }

                } catch (IOException e) {
                    Err(29, "Failed to access entry: " + oldKey, e);
                    return;

                } finally {
                    if ( oldInputStream != null ) {
                        try {
                            oldInputStream.close();
                        } catch ( IOException e ) {
                        }
                    }

                    if ( newInputStream != null ) {
                        try {
                            newInputStream.close();
                        } catch ( IOException e ) {
                        }
                    }
                }

            } else {
                log.Log("   Entry not present in new.");

                deleted++;
                bytesDeleted += oldFE.jarEntry.getSize();
                log.Log("   Old entry to be deleted");
            }
        }

        takeOut(oldHt, removeFromOld);
        takeOut(newHt, removeFromNew);

        log.Log("Resulting Trees:");
        log.Log("   Old Entries (To Remove)        : " + oldHt.size());
        log.Log("   New Entries (To Add or Replace): " + newHt.size());

        eNum = newHt.values().iterator();
        while ( eNum.hasNext() ) {
            FileEntry newFE = (FileEntry) eNum.next();
            added++;
            bytesAdded += newFE.jarEntry.getSize();
        }

        added -= changed;
        bytesAdded -= bytesChanged;

        log.Log("Entry Statistics:");
        log.Log("   Matched:" + hc.FmtNum(matched, 0,12,0) + ", bytes" + hc.FmtNum(bytesMatched, 0,12,0));
        log.Log("   Changed:" + hc.FmtNum(changed, 0,12,0) + ", bytes" + hc.FmtNum(bytesChanged, 0,12,0));
        log.Log("   Deleted:" + hc.FmtNum(deleted, 0,12,0) + ", bytes" + hc.FmtNum(bytesDeleted, 0,12,0));
        log.Log("   Added  :" + hc.FmtNum(added,   0,12,0) + ", bytes" + hc.FmtNum(bytesAdded,   0,12,0));
    }

    boolean CompareAttribs(Attributes a1, Attributes a2,
                           String jarFileName, String jarEntryName)
    {
        if ( (a1 == null) && (a2 == null) )
            return true;

        if ( (a1 == null) || (a2 == null) )
            return false;

        if ( a1.size() != a2.size() )
            return false;

        String value1 = "",
               value2 = "";

        if (verbosity > 3)
            log.Both("Checking attribs for " + jarEntryName + " within " + jarFileName);

        Set a1_keySet = a1.keySet();
        Iterator iterator = a1_keySet.iterator();

        while ( iterator.hasNext() ) {
            Attributes.Name key1 = (Attributes.Name) iterator.next();
            value1 = (String) a1.get(key1);

            if ( debug )
                log.Both("Debug - key=(" + key1 + ") Value1=(" + value1 + ")");

            value2 = (String) a2.get(key1);

            if ( value2 == null )
                return false;

            if (debug)
                log.Both("Debug -                Value2=(" + value2 + ")");

            if ( value1.equals(value2) ) {
                log.Log("   Attribs match Key=(" + key1 + ") = (" + value1 + ")");

            } else {
                log.Log("   Attribs missmatch Key1=(" + key1 + ")  V1=(" + value1 + ")  V2=(" + value2 + ")" );
                return false;
            }
        }

        return true;
    }

    protected boolean DiffEntriesBd(InputStream s1, InputStream s2,
                                    String fileName,
                                    String jarEntryName,
                                    FileEntry fe)
    {
        long s2Size = 0;
        int ciCount = -98;  // seed ciCount with an invalid value
        File bdTempFile = null;

        try {
            s2Size = s2.available();

            bdTempFile = File.createTempFile("BD_Temp", null);
            bdTempFile.deleteOnExit();

            ciCount = DBG.Generate(new BufferedInputStream(s1, ff.getChunckSize()), s1.available(),
                                   new BufferedInputStream(s2, ff.getChunckSize()), s2.available(),
                                   new FileOutputStream(bdTempFile));

        } catch ( FileNotFoundException ex ) {
            log.Err(67, "File Not Found: " , ex);
            log.Close();
            System.exit(8);

        } catch ( IOException ex ) {
            log.Err(66, "IOException: " , ex);
            log.Close();
            System.exit(8);
        }

        if ( ciCount == -1 ) {
            log.Err(69, "GenerateByteDelta failed");
            log.Close();
            System.exit(8);

        } else if ( ciCount == 0 ) {
            log.Log("   ByteDelta changes exceeded new file size threshold of " + ff.getMaxSizePct() + "%");
            jeexceeded++;
            jebytes2send += s2Size;
            bdTempFile = null;

        } else if ( ciCount == 1 ) {
            log.Err(70, " Old and New files are identical, but they weren't a moment ago");
            log.Close();
            System.exit(8);

        } else if ( ciCount > 1 ) {
            long pct = -1;

            if ( s2Size > 0 )
                pct = (bdTempFile.length() * 100) / s2Size;

            log.Log("   " + ciCount + " changeItems generated ciSize=" + bdTempFile.length() +
                    " FileSize=" + s2Size + " " + pct + " %");

            bytes2send += bdTempFile.length();
        }

        return true;
    }

    protected boolean DiffEntries(InputStream s1, InputStream s2, String fileName, String jarEntryName)
    {
        boolean returnValue = false;

        int bufSize = 4096,
            numRead1 = 0,
            numRead2=0,
            totRead = 0;

        BufferedInputStream bf1 = null;
        BufferedInputStream bf2 = null;

        log.Log("  Diffing " + jarEntryName);

        try {
            int size1 = s1.available();
            int size2 = s2.available();

            if ( size1 != size2 ) {
                log.Log("    Size Delta  old=" + size1 + ", new=" + size2);
                sizeDelta++;
                return false;
            } else
                sizeSame++;

        } catch ( IOException e ) {
            Err(33, "obtaining class size:", e);
            return false;
        }

        try {
            boolean moreData = true;

            bf1  = new BufferedInputStream( s1, bufSize);
            bf2  = new BufferedInputStream( s2, bufSize);

            byte[] buf1 = new byte[bufSize];
            byte[] buf2 = new byte[bufSize];

            while ( moreData ) {
                numRead1 = bf1.read(buf1);
                numRead2 = bf2.read(buf2);

                if ( numRead1 == numRead2 ) {
                    if ( numRead1 == -1 ) {
                        returnValue = true;
                        moreData    = false;

                    } else {
                        for ( int i = 0; i < numRead1; i++ ) {
                            if ( buf1[i] != buf2[i] ) {
                                returnValue = false;
                                log.Log("   files are not the same at pos " + hc.FmtNum(totRead + i,0,0,0));
                                moreData = false;
                                contentDelta++;
                                break;
                            }
                        }
                    }

                    totRead += numRead1;

                } else {
                    log.Log("   bytes read differ old=" + numRead1 + " new=" + numRead2);
                    returnValue = false;
                    moreData = false;
                }
            }

        } catch ( FileNotFoundException e ) {
            Err(16, "FileNotFound", e);

        } catch ( IOException e ) {
            Err(19, "IOException", e);

        } finally {
            try {
                bf1.close();
                bf2.close();
            } catch ( IOException e ) {
                Err(20, "IOException closing file", e);
            }
        }

        return returnValue;
    }

    protected boolean ProcessOptions(String[] argv)
    {
        boolean returnValue = true;

        // Default values are contained in the FilterFile class
        //            KeyWord                     Type            Default_Value [UserQuestion]

        PODef[] defs = {
            new PODef(HelperList.o_Version,                "boolean",       "false"),
            new PODef(HelperList.o_Debug,                  "boolean",       "false"),
            new PODef(HelperList.o_Help,                   "BuiltinHelp",   "false"),
            new PODef(HelperList.o_LogFile,                "OutFileAny",    "Delta.Log"),
            new PODef(HelperList.o_Verbosity,              "int",           "3"),
            new PODef(HelperList.o_OldTree,                "Directory",     null),
            new PODef(HelperList.o_NewTree,                "Directory",     null),

            new PODef(HelperList.o_JarName,                "OutFileAny",    null),
            new PODef(HelperList.o_LeadingSlash,           "boolean",       "yes"),
            new PODef(HelperList.o_ShowOptions,            "boolean",       "true"),

            new PODef(HelperList.o_Compression,            "Int",           "9"),
            new PODef(HelperList.o_ParseJar,               "boolean",       "true"),
            new PODef(HelperList.o_Recurse,                "boolean",       "true"),
            new PODef(HelperList.o_FilterFile,             "InFile",        null),
            new PODef(HelperList.o_CaseSensitive,          "boolean",       "true"),
            new PODef(HelperList.o_DisplayManifest,        "boolean",       "false"),
            new PODef(HelperList.o_GenBD,                  "boolean",       "false"),
            new PODef(HelperList.o_ChunckSize,             "INT",           "4096"),
            new PODef(HelperList.o_ReSyncLen,              "int",           "4000"),
            new PODef(HelperList.o_ReSyncScan,             "INT",           "16"),
            new PODef(HelperList.o_MaxSizePct,             "int",           "99"),

            new PODef(HelperList.o_TransferFile,           "StringList",    null),
            new PODef(HelperList.o_AbsTransferFile,        "StringList",    null),
            new PODef(HelperList.o_RestoreOnly,            "StringList",    null),

            new PODef(HelperList.o_AddFile,                "StringList",    null),
            new PODef(HelperList.o_AbsAddFile,             "StringList",    null),
            new PODef(HelperList.o_ReplaceFile,            "StringList",    null),
            new PODef(HelperList.o_AbsReplaceFile,         "StringList",    null),

            new PODef(HelperList.o_Test,                   "boolean",       "false"),
            new PODef(HelperList.o_PropertyFile,           "Validating",    null),
            new PODef(HelperList.o_HelperLocation,         "StringList",    "."),
            new PODef(HelperList.o_FrequencyUpdate,        "long",          "5"),
            new PODef(HelperList.o_VerifyNewJar,           "boolean",       "true"),

            new PODef(HelperList.o_CkEditionValue,         "String",        "?"),
            new PODef(HelperList.o_CkEditionName,          "String",        "?"),
            new PODef(HelperList.o_CkSize,                 "int",           "?"),
            new PODef(HelperList.o_ckVersion,              "StringList",    "?"),
            new PODef(HelperList.o_NewVersion,             "String",        null),
            new PODef(HelperList.o_NewBuildNumber,         "String",        null),
            new PODef(HelperList.o_NewBuildDate,           "String",        null),
            new PODef(HelperList.o_DupCheck,               "Boolean",       "true"),

            new PODef(HelperList.o_ProductFileName,        "String",        null),
            new PODef(HelperList.o_ProductFileType,        "String",        null),
            new PODef(HelperList.o_ProductFileVKey,        "String",        "version"), // Version property name

            new PODef(HelperList.o_XMLPathVersion,         "String",        "#document.websphere.appserver.version"),
            new PODef(HelperList.o_XMLPathEditionName,     "String",        "#document.websphere.appserver.edition.name"),
            new PODef(HelperList.o_XMLPathEditionValue,    "String",        "#document.websphere.appserver.edition.value"),
            new PODef(HelperList.o_XMLPathBuildDate,       "String",        "#document.websphere.appserver.build.date"),
            new PODef(HelperList.o_XMLPathBuildNumber,     "String",        "#document.websphere.appserver.build.number"),
            new PODef(HelperList.o_XMLPathEvent,           "String",        "#document.websphere.appserver.history.event"),

            new PODef(HelperList.o_SpareProduct,           "InFileList",    null),

            new PODef(HelperList.o_UpdateXML,              "Boolean",       "false"),
            new PODef(HelperList.o_XMLVersion,             "String",        "Xerces 1.4.2"),
            new PODef(HelperList.o_Validating,             "Boolean",       "false"),
            new PODef(HelperList.o_NameSpaceAware,         "Boolean",       "false"),
            new PODef(HelperList.o_AddHistory,             "Boolean",       "true"),

            new PODef(HelperList.o_GetNewFile,             "inFileList",    null), // for eFix
            new PODef(HelperList.o_GetReplaceFile,         "inFileList",    null), // for eFix
            new PODef(HelperList.o_GetJarEntry,            "inFileList",    null), // for eFix
            new PODef(HelperList.o_Target,                 "String",        null), // for eFix
            new PODef(HelperList.o_CheckForClass,          "StringList",    null), // for eFix

            new PODef(HelperList.o_EventType,              "String",        null),
            new PODef(HelperList.o_APAR,                   "String",        null), // For product.xml
            new PODef(HelperList.o_PMR,                    "String",        null), // For product.xml
            new PODef(HelperList.o_Description,            "String",        null), // For product.xml
            new PODef(HelperList.o_AffectedComponents,     "StringList",    null),

            new PODef(HelperList.o_CmdDelimiter,           "String",        ";"),
            new PODef(HelperList.o_EntryScript,            "StringList",    "Null"),
            new PODef(HelperList.o_PreScript,              "StringList",    "Null"),
            new PODef(HelperList.o_PostScript,             "StringList",    "Null"),
            new PODef(HelperList.o_PackageInclude,         "DirectoryList", null),
            new PODef(HelperList.o_Call4Help,              "String",        null), // Obsolete
            new PODef(HelperList.o_StartMsg,               "String",        "Start of extraction for $(jarname)"),
            new PODef(HelperList.o_EndMsg,                 "String",        "End of extraction for $(jarname)"),
            new PODef(HelperList.o_Support,                "String",        null),
            new PODef(HelperList.o_mfClassPath,            "String",        null),

            new PODef(HelperList.o_CollectPermissions,     "boolean",       "false"), // 119736

            // The source for online help.
            new PODef(HelperList.o_DocFile,                "String",        "jar:Delta.Doc"),

            new PODef(HelperList.o_BuildSelfExtractor,     "boolean",       "false"),
            new PODef(HelperList.o_WarProcessing, 		   "boolean", 		"true"),
        };

        po = new POProcessor(defs, argv, null) ;

        // if the user provided us the efixDriver/ptfDriver with the jvm argument then we will use this
        String fefixDriver;
        if(efixDriverArg != null)
            fefixDriver = efixDriverArg;
        else    // otherwise we will take the path from the filter file and search for efixDriver in
        {       // XMLValidator later
            String ftemp = po.getString(HelperList.o_FilterFile);
            fefixDriver = new File(ftemp).getParent();
        }
        XMLValidator xmlValidator = new XMLValidator(fefixDriver, dtdFile);

        if(!xmlValidator.validate())
            return false;

        debug     = po.getBool(HelperList.o_Debug);
        fTest     = po.getBool(HelperList.o_Test);
        verbosity = po.getInt(HelperList.o_Verbosity);

        if ( debug )
            po.showProps();

        if ( po.getBool(HelperList.o_Help))  {
            OnLineHelp();
            return false;
        }

        if ( po.getBool(HelperList.o_Version))  {
            DisplayVersion();
            System.exit(0);
        }

        if ( po.keywordUsed(HelperList.o_LogFile) )
            RecycleLog();

        returnValue = readFilterFile(); // after this point in time we will get all onformation from
        // from the FilterFile class

        add2FileXfer(HelperList.o_AbsTransferFile, true);
        add2FileXfer(HelperList.o_TransferFile,   false);

        log.Both("");
        log.Both("Delta Comparator  : version " + pgmVersion );

        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < argv.length; i++ ) {
            sb.append(" ");
            sb.append(argv[i]);
        }

        log.Log( "Command Line args :" + sb.toString());

        //        Validate the command line options
        if (ff.getTarget() != null ) {

            if ( (po.keywordUsed(HelperList.o_GetNewFile)) && (po.keywordUsed(HelperList.o_GetReplaceFile)) ) {
                Err(86, "You may not specify -GetNewFile and -GetReplaceFile from the command line," +
                        " to do this you must use a -FilterFile.");
                return false;
            }

            String getFile;
            int eFixType = 0;

            if ( po.keywordUsed(HelperList.o_GetNewFile) ) {
                getFile = HelperList.o_GetNewFile;
                eFixType = k_New;
            } else {
                getFile = HelperList.o_GetReplaceFile;
                eFixType = k_Update;
            }

        } else { // if we did not specify a target then we must not have specified any getFiles
            if ( (po.keywordUsed(HelperList.o_GetNewFile)) || (po.keywordUsed(HelperList.o_GetReplaceFile)) ) {
                Err(81, "If either -GetNewFile or -GetReplaceFile is specified," +
                        " then a -Target must also be specified.");
                return false;
            }
        }

        if ( (ff.geteFixFilesCount() == 0) && (ff.getChmodCount() == 0) ) {
            if ( (ff.getOldTree() == null) && (ff.getNewTree() == null) ) {
                if ( ff.getVirtualScriptsCount() == 0) {
                    Err(1, "No input was specified, please enter \"java Delta -Help\" for online help");
                    return false; // System.exit(9);
                }
            }
        }

        if ( ff.getOldTree() != null ) {
            File temp = new File(ff.getOldTree());

            if ( !temp.isDirectory() ) {
                Err(6,"The -oldTree " + ff.getOldTree() + ", is not a directory.");
                returnValue = false;
            } else {
                ff.setOldTree(temp.getAbsolutePath());
            }
        }

        if ( ff.getNewTree() != null ) {
            File temp = new File(ff.getNewTree());

            if ( !temp.isDirectory() ) {
                Err(5, "The -newTree " + ff.getNewTree() + ", is not a directory.");
                returnValue = false;
            } else {
                ff.setNewTree(temp.getAbsolutePath());
            }
        }

        if ( !ff.getCaseSensitive() ) {
            if ( ff.getOldTree() != null )
                ff.setOldTree(ff.getOldTree().toLowerCase());
            if ( ff.getNewTree() != null )
                ff.setNewTree(ff.getNewTree().toLowerCase());
        }

        if ( (ff.getJarName() == null) && (ff.getAPAR() == null ) ) {
            Err(7, "An output -JarName must be specified. \"null\" may be specified.");
            returnValue = false;

        } else if ((ff.getJarName() == null) && (ff.getAPAR() != null ))  {
            ff.setJarName(new File(ff.getAPAR() + "_" + ff.getEventType() + ".jar").getAbsolutePath());

        } else {
            String[] nameParts = new String[hc.k_elCount];
            hc.ParseFileSpec(ff.getJarName(), nameParts, debug);

            if (nameParts[hc.k_name].equalsIgnoreCase("null"))
                ff.setJarName(null);
            else
                ff.setJarName( new File(ff.getJarName()).getAbsolutePath() );
        }

        if ( ff.getJarName() != null ) {
            String[] nameParts = new String[hc.k_elCount];
            hc.ParseFileSpec(ff.getJarName(), nameParts, debug);
            macs.put("jarname", nameParts[hc.k_fullname].trim());
        }

        if ( fTest )
            log.Both(" Test switch is acknowledged");

        for ( int i = 0; i < 2; i++ ) {
            totFiles[i]   = 0;
            totBytes[i]   = 0;
            totJars[i]    = 0;
            totJBytes[i]  = 0;
        }

        int numScripts = ff.getVirtualScriptsCount();
        for ( int scriptNo = 0; scriptNo < numScripts; scriptNo++ ) {
            FilterFile.Script nextScript = ff.getVirtualScripts(scriptNo);
            nextScript.display();
        }

        if ( ff.getEventType() != null ) {
            boolean failed = true;

            for ( int i = 0; i < HelperList.validTypes.length; i++ ) {
                if ( ff.getEventType().equals(HelperList.validTypes[i]) )
                    failed = false;
            }

            if ( failed ) {
                Err(97, "The event type specification of " + ff.getEventType() + ", is invalid, valid (case sensitive) options are:");
				HelperList.displayValidTypes(log);
                returnValue = false;
            }

        } else {
            Err(104, "An -EventType must be specified.");
            returnValue = false;
        }

        if (ff.getProductFileType() != null ) {   // ensure we have the all the parts
            if ( validateProductFileTypes(ff.getProductFileType()) ) {
                if ( ff.getProductFileName() == null ) {
                    Err(124, "When ProductFileType is specified a ProductFileName" +
                             " must be provided via the ProductFileName parameter.");
                    returnValue = false;
                } else {
                    if ( ff.getProductFileType().equals(HelperList.mePropertiesProductFile) ) {
                        if ( ff.getProductFileVKey() == null ) {
                            Err(125, "When ProductFileType of (" + HelperList.mePropertiesProductFile +
                                     ") is specified, a ProductFileVKey is also required.");
                            returnValue = false;
                        }
                    }
                }

            } else {
                Err(126, "The specified ProductFileType of (" + ff.getProductFileType() + ") is invalid.");
				HelperList.displayValidProductFileTypes(log);
                returnValue = false;
            }
        }

        if ( ff.getTargetMsg() == null ) {
            log.Log("No Target Message was provided, the default will be used.");

        } else {
            log.Log("The following Target Message was provided:");

            hl.displayMessage(log, ff.getTargetMsg(), "|");
        }

        if ( ff.getSpareProductsCount() == 0 ) {
            log.Both("No spare Product file(s) were specified.");

        } else {
            log.Both("Spare Product file(s) were specified.");

            for ( int i = 0; i  < ff.getSpareProductsCount(); i++ )
                log.Log("  " + (i + 1) + "  " +  ff.getSpareProducts(i));
        }

        if (returnValue) {
            log.Both("OldTree           : " + ff.getOldTree());
            log.Both("NewTree           : " + ff.getNewTree());
            log.Both("Output jar name   : " + ff.getJarName());
            log.Both("Compression Level : " + ff.getCompression());
            log.Both("ParseJars         : " + ff.getParseJar());
            log.Both("Generate ByteDelta: " + ff.getGenBD());
            log.Both("CkSz SyncL ScanL %: " + ff.getChunckSize() + " " +
                                              ff.getReSyncLen() + " " +
                                              ff.getReSyncScan() + " " +
                                              ff.getMaxSizePct() + "%");
            log.Both("Case Sensitivity  : " + ff.getCaseSensitive());
            log.Both("OS Name           : " + System.getProperty("os.name"));
            log.Both("EventType         : " + ff.getEventType());
            log.Both("APAR              : " + ff.getAPAR());
        }

        return returnValue;
    }

    // This method sets variables url and inputJarFileName.

    void determineOurSource()
    {
        if ( url != null ) // we only need to do this once
            return;

        Class theClass = new Delta().getClass();
        String theClassName = theClass.getName();

        ClassLoader theClassLoader = theClass.getClassLoader();

        url = theClassLoader.getResource(theClassName.replace('.', '/') + ".class");

        if ( url == null ) {
            Err(47, "Cannot find my own URL!  (I am Delta).");

        } else {
            // String theURLString = url.toString();

            String theURLString = new URLDecoder().decode(url.toString());
            log.Both("URL is: " + theURLString);

            if ( theURLString.startsWith("jar:") ) {
                int fileEnd = theURLString.indexOf("!");

                if ( fileEnd > 0 ) {
                    int start = 9; // point to start of path

                    if ( !hc.isCaseSensitive() )
                        start = 10;  // this is to bypass the leading slash for NT

                    inputJarFileName = theURLString.substring(start, fileEnd);
                    log.Both("Input jar file name is " + inputJarFileName);
                }
            }
        }
    }

    void add2FileXfer(String keyword, boolean absPath)
    {
        for ( int i=1; i < po.getCount(keyword) + 1; i++ ) {
            String temp = po.getString(keyword, i);

            FilterFile.HTEntry hte = new FilterFile.HTEntry(isNotHelper);
            hte.absPath = absPath;

            StringTokenizer toks = new StringTokenizer(temp, " + ");

            String origName = toks.nextToken();
            hte.vData = origName;

            if ( toks.hasMoreTokens() )
                ff.setTransferFile(toks.nextToken(), hte);
            else
                ff.setTransferFile(origName, hte);
        }
    }

    void RecycleLog()
    {
        log.Close();

        // true|false = if to append to an existing log

        if ( ff == null )
            log = new Logger(po.getString(HelperList.o_LogFile), false, verbosity);
        else
            log = new Logger(ff.getLogFileName(), false, verbosity);

        log.Open();

        hc  = new Helper1(log, verbosity);
    }

    protected boolean readFilterFile()
    {
        Boolean casePreference;

        if ( po.keywordUsed(HelperList.o_CaseSensitive) )
            casePreference = new Boolean(po.getBool(HelperList.o_CaseSensitive));
        else
            casePreference = null;

        // the constructor reads the filter file
        ff = new FilterFile(po.getString(HelperList.o_FilterFile), log, casePreference, debug);

        if ( Logger.errorCount > 0 )
            return false;

        // any keywords specified on the commandLine/PropertyFile override what was specified
        // in the Filter File, so we check if the keyword was used and if so we set it in the
        // filterFile object. We will hold configuration information in the filterFile object.

        if ( po.keywordUsed(HelperList.o_ShowOptions) )
            ff.setShowOptions(po.getBool(HelperList.o_ShowOptions));

        if ( po.keywordUsed(HelperList.o_LogFile) )
            ff.setLogFileName(po.getString(HelperList.o_LogFile));

        if ( po.keywordUsed(HelperList.o_Verbosity) )
            ff.setVerbosity(po.getInt(HelperList.o_Verbosity));

        if ( po.keywordUsed(HelperList.o_OldTree) )
            ff.setOldTree(po.getString(HelperList.o_OldTree));

        if ( po.keywordUsed(HelperList.o_NewTree) )
            ff.setNewTree(po.getString(HelperList.o_NewTree));

        if ( po.keywordUsed(HelperList.o_JarName) )
            ff.setJarName(po.getString(HelperList.o_JarName));

        if ( po.keywordUsed(HelperList.o_LeadingSlash) )
            ff.setLeadingSlash(po.getBool(HelperList.o_LeadingSlash));

        if ( po.keywordUsed(HelperList.o_Compression) )
            ff.setCompression(po.getInt(HelperList.o_Compression));

        if ( po.keywordUsed(HelperList.o_ParseJar) )
            ff.setParseJar(po.getBool(HelperList.o_ParseJar));
        
        if ( po.keywordUsed(HelperList.o_WarProcessing) )
            ff.setWarProcessing(po.getBool(HelperList.o_WarProcessing));

        if ( po.keywordUsed(HelperList.o_Recurse) )
            ff.setRecurse(po.getBool(HelperList.o_Recurse));

        if ( po.keywordUsed(HelperList.o_DisplayManifest) )
            ff.setDisplayManifest(po.getBool(HelperList.o_DisplayManifest));

        if ( po.keywordUsed(HelperList.o_GenBD) )
            ff.setGenBD(po.getBool(HelperList.o_GenBD));

        if ( po.keywordUsed(HelperList.o_ChunckSize) )
            ff.setChunckSize(po.getInt(HelperList.o_ChunckSize));

        if ( po.keywordUsed(HelperList.o_ReSyncLen) )
            ff.setReSyncLen(po.getInt(HelperList.o_ReSyncLen));

        if ( po.keywordUsed(HelperList.o_ReSyncScan) )
            ff.setReSyncScan(po.getInt(HelperList.o_ReSyncScan));

        if ( po.keywordUsed(HelperList.o_MaxSizePct) )
            ff.setMaxSizePct(po.getInt(HelperList.o_MaxSizePct));

        if ( po.keywordUsed(HelperList.o_TransferFile) ) {
            for ( int i = 1; i <= po.getCount(HelperList.o_TransferFile); i++ ) {
                ff.setTransferFile(po.getString(HelperList.o_TransferFile, i),
                                   null,
                                   noAbsolutePath,
                                   noAddOnly,
                                   noReplaceOnly);
            }
        }

        if ( po.keywordUsed(HelperList.o_AbsTransferFile) ) {
            for (int i=1; i <= po.getCount(HelperList.o_TransferFile); i++ ) {
                ff.setTransferFile(po.getString(HelperList.o_AbsTransferFile, i),
                                   null,
                                   absolutePath,
                                   noAddOnly,
                                   noReplaceOnly);
            }
        }

        if ( po.keywordUsed(HelperList.o_RestoreOnly))  {
            int count = po.getCount(HelperList.o_RestoreOnly);

            if ( (count != 2) && (count != 3) ) {
                Err(99, "When -RestoreOnly is specified," +
                        " a source and target must be specified as a comma delimited list.");

            } else {
                String requiredVersion = null;

                if ( count == 3 )
                    requiredVersion = po.getString(HelperList.o_RestoreOnly, 3);

                ff.setRestoreOnly(po.getString(HelperList.o_RestoreOnly, 1),
                                  po.getString(HelperList.o_RestoreOnly, 2),
                                  requiredVersion);
            }
        }


        if ( po.keywordUsed(HelperList.o_AddFile))  {
            if ( po.getCount(HelperList.o_AddFile) > 2 ) {
                Err(99, "When -AddFile is specified on the command line," +
                        " only a single file and optionally its new name may be specified.");

            } else {
                String newName = null;

                if ( po.getCount(HelperList.o_AddFile) == 2 )
                    newName = po.getString(HelperList.o_AddFile, 2);

                ff.setTransferFile(po.getString(HelperList.o_AddFile, 1),
                                   newName,
                                   noAbsolutePath,
                                   addOnly,
                                   noReplaceOnly);
            }
        }

        if ( po.keywordUsed(HelperList.o_AbsAddFile) ) {
            if ( po.getCount(HelperList.o_AbsAddFile) > 2 ) {
                Err(99, "When -AbsAddFile is specified on the command line," +
                        " only a single file and optionally its new name may be specified.");

            } else {
                String newName = null;

                if ( po.getCount(HelperList.o_AbsAddFile) == 2 )
                    newName = po.getString(HelperList.o_AbsAddFile, 2);

                ff.setTransferFile(po.getString(HelperList.o_AbsAddFile, 1),
                                   newName,
                                   absolutePath,
                                   addOnly,
                                   noReplaceOnly);
            }
        }

        if ( po.keywordUsed(HelperList.o_ReplaceFile) ) {
            if ( po.getCount(HelperList.o_ReplaceFile) > 2 ) {
                Err(99, "When -ReplaceFile is specified on the command line," +
                        " only a single file and optionally its new name may be specified.");

            } else {
                String newName = null;

                if ( po.getCount(HelperList.o_ReplaceFile) == 2 )
                    newName = po.getString(HelperList.o_ReplaceFile, 2);

                ff.setTransferFile(po.getString(HelperList.o_ReplaceFile, 1),
                                   newName,
                                   noAbsolutePath,
                                   noAddOnly,
                                   replaceOnly);
            }
        }

        if ( po.keywordUsed(HelperList.o_AbsReplaceFile) ) {
            if ( po.getCount(HelperList.o_AbsReplaceFile) > 2 ) {
                Err(99, "When -AbsReplaceFile is specified on the command line," +
                        " only a single file and optionally its new name may be specified.");

            } else {
                String newName = null;

                if ( po.getCount(HelperList.o_AbsReplaceFile) == 2 )
                    newName = po.getString(HelperList.o_AbsReplaceFile, 2);

                ff.setTransferFile(po.getString(HelperList.o_AbsReplaceFile, 1),
                                   newName,
                                   absolutePath,
                                   noAddOnly,
                                   replaceOnly);
            }
        }

        if ( po.keywordUsed(HelperList.o_HelperLocation) ) {
            for (int i=1; i <= po.getCount(HelperList.o_HelperLocation); i++ )
                ff.setHelperDirectories(po.getString(HelperList.o_HelperLocation, i));
        }

        if ( po.keywordUsed(HelperList.o_FrequencyUpdate) )
            ff.setFrequencyUpdate(po.getInt(HelperList.o_FrequencyUpdate));

        if ( po.keywordUsed(HelperList.o_VerifyNewJar) )
            ff.setVerifyNewJar(po.getBool(HelperList.o_VerifyNewJar));

        if ( po.keywordUsed(HelperList.o_CkEditionValue) )
            ff.setCkEditionValue(po.getString(HelperList.o_CkEditionValue));

        if ( po.keywordUsed(HelperList.o_CkEditionName) )
            ff.setCkEditionName(po.getString(HelperList.o_CkEditionName));

        if ( po.keywordUsed(HelperList.o_CkSize) )
            ff.setCkSize(po.getLong(HelperList.o_CkSize));

        if ( po.keywordUsed(HelperList.o_CkVersion) ) {
            for ( int i=1; i <= po.getCount(HelperList.o_CkVersion); i++ )
                ff.setCkVersion(po.getString(HelperList.o_CkVersion, i));
        }

        if ( po.keywordUsed(HelperList.o_NewVersion) )
            ff.setNewVersion(po.getString(HelperList.o_NewVersion));

        if ( po.keywordUsed(HelperList.o_NewBuildNumber) )
            ff.setNewBuildNumber(po.getString(HelperList.o_NewBuildNumber));

        if ( po.keywordUsed(HelperList.o_NewBuildDate) )
            ff.setNewBuildDate(po.getString(HelperList.o_NewBuildDate));

        if ( po.keywordUsed(HelperList.o_DupCheck) )
            ff.setDupCheck(po.getBool(HelperList.o_DupCheck));

        if ( po.keywordUsed(HelperList.o_ProductFileName) )
            ff.setProductFileName(po.getString(HelperList.o_ProductFileName));

        if ( po.keywordUsed(HelperList.o_ProductFileType) )
            ff.setProductFileType(po.getString(HelperList.o_ProductFileType));

        if ( po.keywordUsed(HelperList.o_ProductFileVKey) )
            ff.setProductFileVKey(po.getString(HelperList.o_ProductFileVKey));

        if ( po.keywordUsed(HelperList.o_XMLPathVersion) )
            ff.setXMLPathVersion(po.getString(HelperList.o_XMLPathVersion));

        if ( po.keywordUsed(HelperList.o_XMLPathEditionName) )
            ff.setXMLPathEditionName(po.getString(HelperList.o_XMLPathEditionName));

        if ( po.keywordUsed(HelperList.o_XMLPathEditionValue) )
            ff.setXMLPathEditionValue(po.getString(HelperList.o_XMLPathEditionValue));

        if ( po.keywordUsed(HelperList.o_XMLPathBuildDate) )
            ff.setXMLPathBuildDate(po.getString(HelperList.o_XMLPathBuildDate));

        if ( po.keywordUsed(HelperList.o_XMLPathBuildNumber) )
            ff.setXMLPathBuildNumber(po.getString(HelperList.o_XMLPathBuildNumber));

        if ( po.keywordUsed(HelperList.o_XMLPathEvent) )
            ff.setXMLPathEvent(po.getString(HelperList.o_XMLPathEvent));

        if ( po.keywordUsed(HelperList.o_SpareProduct) ) {
            for ( int i = 1; i <= po.getCount(HelperList.o_SpareProduct); i++ )
                ff.setSpareProducts(po.getString(HelperList.o_SpareProduct));
        }

        if ( po.keywordUsed(HelperList.o_UpdateXML) )
            ff.setUpdateXML(po.getBool(HelperList.o_UpdateXML));

        if ( po.keywordUsed(HelperList.o_XMLVersion) )
            ff.setXMLVersion(po.getString(HelperList.o_XMLVersion));

        if ( po.keywordUsed(HelperList.o_Validating) )
            ff.setValidating(po.getBool(HelperList.o_Validating));

        if ( po.keywordUsed(HelperList.o_NameSpaceAware) )
            ff.setNameSpaceAware(po.getBool(HelperList.o_NameSpaceAware));

        if ( po.keywordUsed(HelperList.o_AddHistory) )
            ff.setAddHistory(po.getBool(HelperList.o_AddHistory));

        //  hl.o_GetNewFile,             "inFileList",    null),    // for eFix
        //  hl.o_GetReplaceFile,         "inFileList",    null),    // for eFix
        //  hl.o_GetJarEntry,            "inFileList",    null),    // for eFix

        if ( po.keywordUsed(HelperList.o_Target) )
            ff.setTarget(po.getString(HelperList.o_Target));

        //  hl.o_CheckForClass,          "StringList",    null),    // for eFix

        if ( po.keywordUsed(HelperList.o_EventType) )
            ff.setEventType(po.getString(HelperList.o_EventType));

        if ( po.keywordUsed(HelperList.o_APAR) )
            ff.setAPAR(po.getString(HelperList.o_APAR));

        if ( po.keywordUsed(HelperList.o_PMR) )
            ff.setPMR(po.getString(HelperList.o_PMR));

        if ( po.keywordUsed(HelperList.o_Description) )
            ff.setDescription(po.getString(HelperList.o_Description));

        if ( po.keywordUsed(HelperList.o_AffectedComponents) ) {
            for ( int i=1; i <= po.getCount(HelperList.o_AffectedComponents); i++ )
                ff.setAffectedComponents(po.getString(HelperList.o_AffectedComponents));
        }

        if ( po.keywordUsed(HelperList.o_CmdDelimiter) )
            ff.setCmdDelimiter(po.getString(HelperList.o_CmdDelimiter));

        if ( po.keywordUsed(HelperList.o_ArgDelimiter) )
            ff.setArgDelimiter(po.getString(HelperList.o_ArgDelimiter));

        //  hl.o_EntryScript,            "StringList",  "Null"),
        //  hl.o_PreScript,              "StringList",  "Null"),
        //  hl.o_PostScript,             "StringList",  "Null"),

        if ( po.keywordUsed(HelperList.o_PackageInclude) ) {
            for (int i=1; i <= po.getCount(HelperList.o_PackageInclude); i++ )
                ff.setPackageDirectories(po.getString(HelperList.o_AffectedComponents));
        }

        if ( po.keywordUsed(HelperList.o_StartMsg) )
            ff.setStartMsg(po.getString(HelperList.o_StartMsg));

        if ( po.keywordUsed(HelperList.o_EndMsg) )
            ff.setEndMsg(po.getString(HelperList.o_EndMsg));

        if ( po.keywordUsed(HelperList.o_Support) )
            ff.setSupport(po.getString(HelperList.o_Support));

        if ( po.keywordUsed(HelperList.o_mfClassPath) )
            ff.setmfClassPath(po.getString(HelperList.o_mfClassPath));

        if ( po.keywordUsed(HelperList.o_CollectPermissions) )
            ff.setCollectPermissions(po.getBool(HelperList.o_CollectPermissions));

        if ( po.keywordUsed(HelperList.o_DocFile) )
            ff.setDocFile(po.getString(HelperList.o_DocFile));

        if ( po.keywordUsed(HelperList.o_Test) )
            ff.setTest(po.getBool(HelperList.o_Test));

        if ( po.keywordUsed(HelperList.o_BuildSelfExtractor) )
            ff.setBuildSelfExtractor(po.getBool(HelperList.o_BuildSelfExtractor));

        return true;
    }

    protected String isItADirectory(String fileName, StringBuffer msg)
    {
        File tempFile = new File(fileName).getAbsoluteFile();

        if ( !tempFile.exists() ) {
            msg.append("The directory specification, " + tempFile.toString() +
                       ", does not exist.");
            return null;

        } else if ( !tempFile.isDirectory() ) {
            msg.append("The directory specification, " + tempFile.toString() +
                       ", seems not to be a directory.");
            return null;
        }

        return tempFile.toString();
    }

    protected boolean validateProductFileTypes(String theType)
    {
        for ( int i = 0; i < HelperList.validProductFileTypes.length; i++ ) {
            if ( theType.equals(HelperList.validProductFileTypes[i]) )
                return true;
        }

        return false;
    }

    protected String ResolveMacro(String line, int lineNum)
    {
        // the string ?<> denotes a macro. where we will replace the string
        // with either an known string or from a real environment variable

        final int
            kmYYYYMMDD = 0,
            kmMMDDYYYY = 1,
            kmMMDDYY   = 2;

        String[] knownMacros = {
            "YYYYMMDD",    // replace with the current date
            "MM/DD/YYYY",  // replace with the current Date
            "MM/DD/YY" };   // replace with the current Date

        String newValue = null;

        int startPos = line.indexOf("?<");

        while ( startPos > -1 ) {
            int endPos = line.indexOf(">", startPos );

            if ( endPos > -1 ) {
                String macroName = line.substring(startPos + 2, endPos);

                for ( int km=0; km < knownMacros.length; km++ ) {  // search for knownMacros first

                    if (macroName.equals(knownMacros[km]) ) {
                        switch (km) {

                        case kmMMDDYYYY:
                            Date now = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            newValue = df.format(now);
                            break;

                        case kmMMDDYY:
                            now = new Date();
                            df = new SimpleDateFormat("MM/dd/yy");
                            newValue = df.format(now);
                            break;

                        case kmYYYYMMDD:
                            now = new Date();
                            df = new SimpleDateFormat("yyyyMMdd");
                            newValue = df.format(now);
                            break;

                        }  // no default for the switch
                    }
                }

                if ( newValue == null )   // its not a knownMacro, see if its an environment variable
                    newValue = System.getProperty(macroName);


                if ( newValue != null ) {  // if we have a new value replace it with the macro value
                    line =  line.substring(0, startPos) + newValue + line.substring(endPos + 1);
                    newValue = null;

                } else {
                    Err(38, "Unresolved macro " + line.substring(startPos, endPos + 1) +
                            " found in line #" + lineNum +
                            ", position " + startPos +
                            " within FilterFile " + po.getString(HelperList.o_FilterFile));
                    line =  line.substring(0, startPos) + macroName + line.substring(endPos + 1);
                }
            }

            if ( endPos > 0 )
                startPos = line.indexOf("?<", startPos + 1);
            else
                startPos = -2;  // to show we are done
        }

        return line;
    }

    protected void DisplayManifestContent(Manifest mf)
    {
        log.Both("Debug - Display content of manifest");

        Map mfMap = mf.getEntries();
        log.Both(mfMap.size() + " entries in the manifest.");
        DisplayAttribs(mf.getMainAttributes(), "Main Attributes");

        Set mfKeys = mfMap.keySet();
        Iterator mfKeyStepper = mfKeys.iterator();

        while ( mfKeyStepper.hasNext() ) {
            String mfKey   = (String) mfKeyStepper.next();
            // System.out.println("mfKeyStepper= (" + mfKey + ")");
            // Attributes attribs = mf.getAttributes(mfKey);
            DisplayAttribs(mf.getAttributes(mfKey) , mfKey);
        }
    }

    protected void DisplayAttribs(Attributes attribs, String description)
    {
        log.Both(description + ",   number of attributes : " + attribs.size() );

        if ( attribs.size() == 0 )
            return;

        Set attribSet = attribs.entrySet();
        Iterator iter = attribSet.iterator();

        while ( iter.hasNext() ) {
            Map.Entry  mapEntry = (Map.Entry) iter.next();
            Attributes.Name key   = (Attributes.Name) mapEntry.getKey();
            String value = (String) mapEntry.getValue();
            log.Both("  attrib  " + key + " = " + value);
        }
    }

    protected void Err(int errNum, String message)
    {
        log.Err(errNum, message);
        errCount++;
    }

    protected void Err(int errNum, String message, boolean if2Abend)
    {
        log.Err(errNum, message, if2Abend);
        errCount++;
    }

    protected void Err(int errNum, String message, Exception ex, boolean if2Abend)
    {
        log.Err(errNum, message, ex, if2Abend);
        errCount++;
    }

    protected void Err(int errNum, String message, Exception ex)
    {
        log.Err(errNum, message, ex);
        errCount++;
    }

    protected void DisplayVersion()
    {
        log.Both("System Architecture is    " + System.getProperties().getProperty("os.arch"));
        log.Both("Operating System is       " + System.getProperties().getProperty("os.name"));
        log.Both("java version is           " + System.getProperty("java.version"));
        log.Both("Delta version is          " + pgmVersion);
        log.Both("HelperList version is     " + HelperList.pgmVersion);
        log.Both("FilterFile version is     " + FilterFile.pgmVersion);
        log.Both("Helper1 version is        " + Helper1.pgmVersion);
        log.Both("Logger version is         " + Logger.pgmVersion);
        log.Both("Property Option Processor " + POProcessor.pgmVersion);
        log.Both("DeltaByteGenerator is     " + DeltaByteGenerator.pgmVersion);
        log.Both("ShowProgress  is          " + ShowProgress.pgmVersion);

        //log.Both("DomL2Spt version is       " + DomL2Spt.pgmVersion);
    }

    protected void OnLineHelp()
    {
        String docSource = po.getString(HelperList.o_DocFile);

        BufferedReader reader = null;

        if ( docSource.startsWith("jar:") ) { 
           determineOurSource();  // will set global variable inputJarFileName

            if ( inputJarFileName == null ) {
                Err(122, "Sorry, on-Line help is not currently available," +
                         " it normally resides in the Delta.jar.", abend);
            }

            JarFile jf = null;

            try {
                jf = new JarFile(inputJarFileName);

            } catch ( IOException ex ) {
                Err(117, "Unable to open " + inputJarFileName + " to access the Delta.Doc", ex);
                return;
            }

            ZipEntry ze = jf.getEntry(docSource.substring(4));

            if ( ze == null )
                Err(120, "The documentation file (" + docSource + ") could not be located:", abend);

            try {
                InputStream is = jf.getInputStream(ze);
                reader = new BufferedReader( new InputStreamReader(is) );

            } catch ( IOException ex ) {
                Err(119, "The documentation file can not be opened:", ex, abend);
            }

        } else {  // it must be a file somewhere
            try {
                reader = new BufferedReader( new InputStreamReader(new FileInputStream(docSource)) );
            } catch ( FileNotFoundException ex ) {
                Err(123, "The documentation file can not be opened:", ex, abend);
            }
        }

        System.out.println("   Delta Version " + pgmVersion) ;

        try {
            String aLine;
            while ((aLine = reader.readLine())  != null)
                System.out.println(aLine);

        } catch ( IOException ex ) {
            Err(118, "IO reading docSource:", ex, abend);
        }
    }
}
