/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */
package com.ibm.websphere.update.delta;

import java.io.*;
import java.util.*;

/*
 * FilterFile -- Encoding of a filter file 
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/FilterFile.java, wps.base.fix, wps6.fix
 *
 * History 1.7, 8/9/05
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
 *             Changed mapping type from Hashtable to HashMap.
 *
 *             Introduced FileCategory inner class.
 *
 *             Added wildcard support for deleteBeforeWrite,
 *             addOnlyFiles, perInstance, replaceOnlyFiles,
 *             asInstallable, asApplication, and asInstalled.
 *
 * 25-Jan-2003 Added EAR naming and AltMetadata support.
 *
 * 05-Mar-2003 Updated to convert file category keys only if
 *             case sensitive is false.  Defect 160016.
 */

/**
 *  
 */
class FilterFile
{
   public static final String pgmVersion = "1.7" ;
   public static final String pgmUpdate = "8/9/05" ;

    protected static final String
        slash = System.getProperty("file.separator");

    // 'childDivider' is used as a separator in jar entry names,
    // the character can never occur as a file name.

    protected static final String
        childDivider = "\u0013";

    // Control contants for various methods:

    public static final int k_Delete = 1 ;
    // Control contants for various methods:

    public static final int k_Add = 2 ;
    // Control contants for various methods:

    public static final int k_Update = 3 ;
    // Control contants for various methods:

    public static final int k_JarEntry = 5 ;

    public static final int k_Old = 0 ;
    public static final int k_New = 1 ;

    public static final boolean abend = true ;
    public static final boolean isHelper = true ;

    public static void main(String args[])
    {
        // parm 1 is the filter file name

        System.out.println("Start of FilterFile Tester");

        boolean if2AppendLog = false,
                debug        = false;

        Logger log = new Logger("FilterFile.log", if2AppendLog, 3);

        Boolean casePreference = new Boolean(true);

        FilterFile ff = new FilterFile(args[0], log, casePreference, debug);

        // to test continuation lines
        log.Both(" affectedComponents=(" + ff.getAffectedComponents() + ")");

        log.Both("There are " + ff.getIncludesCount() + " include statements");

        for ( int i = 0; i < ff.getIncludesCount(); i++ ) {
            HTEntry hte = ff.getIncludes(i);
            log.Both(" Component " + hte.componentName + " " + hte.key);
        }

        log.Both("There are " + ff.getFileUpdateCount() + " SearchReplace specifications.");

        for ( int i = 0; i < ff.getFileUpdateCount(); i++ ) {
            FileUpdateInfo fui = ff.getFileUpdate(i);
            log.Both("  Filespec   : " + fui.filespec );
            log.Both("  find       : " + fui.find);
            log.Both("  Replace    : " + fui.replace);
            log.Both("  Components : " + fui.components.length);

            for ( int j = 0; j < fui.components.length; j++ )
                log.Both("    " + j + " " + fui.components[j]);

            log.Both(" ");
        }

        log.Both(" ");
        ff.deleteBeforeWrite.display();

        log.Both(" ");
        ff.addOnlyFiles.display();
        log.Both(" ");
        ff.replaceOnlyFiles.display();

        log.Both(" ");
        ff.perInstance.display();

        log.Both(" ");
        ff.asInstallable.display();
        log.Both(" ");
        ff.asApplication.display();
        log.Both(" ");
        ff.asInstalled.display();

        log.Both(" ");
        log.Both("Files to Skip");

        Iterator skipEnum = ff.getFiles2SkipEnumKey();
        while ( skipEnum.hasNext() ) {
            String key = (String) skipEnum.next();
            FilterFile.HTEntry hte = ff.getFiles2Skip(key);
            log.Both("   Key=(" + key + ") (" + hte.key + ")");
        }

        skipEnum = ff.getFiles2SkipEnumKey();
        while ( skipEnum.hasNext() ) {
            String key = (String) skipEnum.next();
            FilterFile.HTEntry hte = ff.getFiles2Skip(key);
            log.Both("   Key=(" + key + ") (" + hte.key + ")");
        }

        log.Both("os.name (" + System.getProperty("os.name") + ")");
        log.Both("Error Count is " + Logger.errorCount);
    }

    protected static class PropUpdateInfo
    {
        String propFileName;
        String function;
        String key;
        String value;
    }

    protected static class FileUpdateInfo
    {
        String   filespec;
        String   find;
        String   replace;
        String[] components;
    }

    protected static class zipEntryUpdateInfo
    {
        String zipFileName;
        String zipComment;
        byte[] zipExtra;

        public zipEntryUpdateInfo(String zipFileName)
        {
            this.zipFileName = zipFileName;
            this.zipComment = null;
            this.zipExtra = null;
        }
    }

    // the script class describes commands to be executed

    protected static class Script
    {
        String  name;

        boolean preScript;
        boolean entryScript;  // Meaningful only if preScript is true.

        Vector  cmds;         // Vector<String>
        Vector  cmdArgs;      // Vector<null | Vector<String>>

        Vector  uncmds;       // Vector<String>
        Vector  uncmdArgs;    // Vector<null | Vector<String>>

        public Script(String name, boolean preScript, boolean entryScript)
        {
            this.preScript   = preScript;
            this.entryScript = entryScript;

            this.name        = name;

            this.cmds        = new Vector();
            this.cmdArgs     = new Vector();

            this.uncmds      = new Vector();
            this.uncmdArgs   = new Vector();
        }

        public void display()
        {
            log.Both(" ");

            String scriptName;

            if ( !preScript )
                scriptName = "PostScript";
            else if ( !entryScript )
                scriptName = "PreScript";
            else
                scriptName = "EntryScript";

            log.Both(scriptName + ": " + name);

            log.Both("  Commands: ");

            for ( int cmdNo = 0; cmdNo < cmds.size(); cmdNo++ ) {
                log.Both("  [" + cmdNo + "]: " + cmds.elementAt(cmdNo));

                if ( cmdNo < cmdArgs.size() ) {
                    Vector args = (Vector) cmdArgs.elementAt(cmdNo);
                    if ( args != null ) {
                        for ( int argNo = 0; argNo < args.size(); argNo++ )
                            log.Both("     [" + argNo + "]: " + args.elementAt(argNo));
                    }
                }
            }

            log.Both("   Undo-Commands: ");

            for ( int cmdNo = 0; cmdNo < uncmds.size(); cmdNo++ ) {
                log.Both("  [" + cmdNo + "]: " + uncmds.elementAt(cmdNo));

                if ( cmdNo < uncmdArgs.size() ) {
                    Vector args = (Vector) uncmdArgs.elementAt(cmdNo);
                    if ( args != null ) {
                        for ( int argNo = 0; argNo < args.size(); argNo++ )
                            log.Both("     [" + argNo + "]: " + args.elementAt(argNo));
                    }
                }
            }
        }
    }

    protected static class HTEntry
    {
        boolean   include;     // Used for include / exclude
        boolean   cut;         // If to cut the the specified part of the path
        boolean   absPath;     // If the specified path is absolute
        boolean   helper;      // If this is a helper class needed in the backupJar
        boolean   addOnly;     // If this is a addFile option
        boolean   replaceOnly; // If this is a replace file option
        boolean   altFlag;     // If an alt metadata installed entry
        String    nameRule;    // A name rule for an installed entry
        int       eFixType;    // The Type of eFix operation: add delete replace update
        int[]     count;       // A count of how many for Old and New
        int       prefixLen;   // This is the length of the provided path
        HashMap   translate;   // Provide a method to translate a portion of the tree path
        String    signatures;  // A comma delimited List of signatures
        String    componentName;
        String    requiredVersion;
        String    key;         // Same as the key in the hashtable, or the key in the Vector
        String    vData;       // Variable data, depending on which table
                               // chmod        -  chmod Value
                               // transferFile - new path
                               // eFixFiles    - target location

        public HTEntry(boolean helperStatus)
        {
            include         = true;
            cut             = false;
            absPath         = false;
            helper          = helperStatus;
            addOnly         = false;
            replaceOnly     = false;
            altFlag         = false;
            nameRule        = null;

            count           = new int[] { 0, 0 };
            prefixLen       = 0;
            translate       = new HashMap(0);
            signatures      = null;
            componentName   = null;
            requiredVersion = null;
            key             = "";
            vData           = "";
        }

        public void increment(int oldNew)
        {
            count[oldNew]++;
        }

        public void increment(HashMap oldFiles, HashMap newFiles)
        {
            if ( oldFiles.containsKey(key) )
                count[k_Old]++;

            if ( newFiles.containsKey(key) )
                count[k_New]++;
        }
    }

    protected boolean debug;

    protected String logFileName  = "Delta.Log";
    protected int verbosity       = 3;
    protected int frequencyUpdate = 5; // TFB: ??

    protected static Logger log;

    protected static Helper1 hc;
    protected static HelperList hl;

    protected boolean caseSensitive;
    protected boolean caseSensitiveM; // M = has been modified from the default

    protected StringBuffer errSB = new StringBuffer();
    protected boolean parseJar = false; // 157855: Change default to false.
    protected boolean recurse = true;
    protected boolean warProcessing = true;

    protected boolean collectPermissions = false;

    protected String docFile = "jar:Delta.Doc";

    protected boolean showOptions = true;

    protected String jarName = null;
    protected String mfClassPath = null;
    protected int compression = 9;
    protected boolean verifyNewJar = true;
    protected boolean leadingSlash = true;
    protected String support = null;

    protected String startMsg = "Start of extraction for $(jarname)";
    protected String endMsg   = "End of extraction for $(jarname)";

    protected String oldTree   = null;
    protected String newTree   = null;

    protected String eventType = null;

    protected String affectedComponents = null; // Comma delimited list of components.

    protected long ckSize            = 0;
    protected String ckVersion      = "?"; // Comma delimited list of accepted versions.
    protected String ckEditionValue = "?"; // Comma delimited list of accepted edition values.
    protected String ckEditionName  = "?"; // Comma delimited list of accepted edition names.

    protected boolean displayManifest = false;
    protected boolean test            = false;

    protected boolean updateXML      = false;
    protected boolean nameSpaceAware = false;
    protected boolean validating     = false;
    protected boolean addHistory     = true;
    protected String newBuildNumber  = null;
    protected String newBuildDate    = null;
    protected String newVersion      = null;

    protected boolean dupCheck = true;

    protected String XMLVersion          = "Xerces 1.4.2";
    protected String XMLPathEvent        = "#document.websphere.appserver.history.event";
    protected String XMLPathVersion      = "#document.websphere.appserver.version";
    protected String XMLPathEditionName  = "#document.websphere.appserver.edition.name";
    protected String XMLPathEditionValue = "#document.websphere.appserver.edition.value";
    protected String XMLPathBuildDate    = "#document.websphere.appserver.build.date";
    protected String XMLPathBuildNumber  = "#document.websphere.appserver.build.number";

    protected String productFileVKey = "version";
    protected String productFileType = null;

    protected String productFileName = "$<target>/properties/com/ibm/websphere/product.xml";

    protected boolean buildSelfExtractor = false;

    protected String description = null;
    protected String APAR = null;
    protected String PMR = null;

    protected String check4Class = null;

    protected String targetMsg = null;

    protected String target = null;

    protected String cmdDelimiter = ";";
    protected String argDelimiter = ",";

    protected boolean genBD  = false;
    protected int maxSizePct = 99;
    protected int reSyncLen  = 4000;
    protected int reSyncScan = 16;
    protected int chunkSize  = 4096;

    protected Vector spareProducts        = new Vector(8);

    protected HashMap components          = new HashMap(32); // componentName --> signature

    protected Vector forceBackup          = new Vector(10, 2);

    protected Vector packageDirectories   = new Vector();
    protected Vector helperDirectories    = new Vector();
    protected HashMap noBackupJar         = new HashMap(10);

    protected Vector propUpdate           = new Vector();
    protected Vector fileUpdate           = new Vector();
    protected Vector zipEntryUpdates      = new Vector();
    protected Vector virtualScripts       = new Vector();
    protected Vector restoreOnly          = new Vector(5);
    protected Vector includes             = new Vector(64, 2);

    protected Vector importComponents     = new Vector(2);     // Vector<HTEntry>
    protected HashMap transferFile        = new HashMap(20);

    protected Vector eFixFiles            = new Vector(10, 2);
    protected Vector reSequenceJar        = new Vector(10,2);
    protected Vector reName               = new Vector(10,2);
    protected HashMap noRestore           = new HashMap(600);
    protected HashMap forceReplace        = new HashMap(16);
    protected HashMap noDelete            = new HashMap(10);

    protected HashMap chmod               = new HashMap(10);

    // When adding an entry to a file category,
    //
    //    If the entry has '*' at the end, the entry
    //    is an inexact entry.
    //
    //    Otherwise, the entry is an exact entry.
    //
    //    The entry may have a naming rule
    //    (an EAR naming substitution pattern).
    //
    //    In this case, add the entry with empty
    //    substitutions, and, add a mapping from the
    //    resulting entry to the original entry.

    /**
	 *  
	 */
    public class FileCategory
    {
        String name     = null;
        String fullName = null;

        HashMap exact = new HashMap();    // entryName ==> HTEntry
        HashMap inexact = new HashMap();  // entryName ==> HTEntry

        public FileCategory(String name)
        {
            this.name = name;
            this.fullName = "FileCategory: " + name;
        }


        public HTEntry putAltEntry(String key)
        {
            HTEntry newEntry = putEntry(key);

            newEntry.altFlag = true;

            return newEntry;
        }

        // FileCategory entries are normalized to
        // all lower case, to forward slashes,
        // and to have a leading slash.

        // A name rule, if provided, is normalized to
        // forward slashes, and to have a leading slash,
        // but is not converted to lower case.

        public HTEntry putEntry(String key)
        {
            key = key.replace('\\', '/');
            if ( !key.startsWith("/") )
                key = "/" + key;

            String resolvedKey =
                HelperList.ResolveMacro(HelperList.braceMarkers,
                                        key,
                                        0,         // Simulate a line and offset
                                        fullName); // Simulate a file name

            HTEntry newEntry = basicPutEntry(resolvedKey);

            if ( !resolvedKey.equals(key) ) {
                log.Both("Noted naming rule:");
                log.Both("  Key:  [ " + resolvedKey + " ]");
                log.Both("  Rule: [ " + key + " ]");

                newEntry.nameRule = key;
            }

            return newEntry;
        }

        public HTEntry basicPutEntry(String key)
        {
            if ( !getCaseSensitive() )
                key = key.toLowerCase();

            HTEntry newEntry = new HTEntry(!isHelper);
            newEntry.key = key;

            if ( key.endsWith(HelperList.wildCard) ) {
                key = key.substring(0, key.length() - HelperList.wildCard.length());
                inexact.put(key, newEntry);
            } else {
                exact.put(key, newEntry);
            }

            return newEntry;
        }

        public HashMap getExactMap()
        {
            return exact;
        }

        /**
		 * @return  the exact
		 * @uml.property  name="exact"
		 */
        public Iterator getExact()
        {
            return exact.keySet().iterator();
        }

        public Iterator getExactValues()
        {
            return exact.values().iterator();
        }

        public HashMap getInexactMap()
        {
            return inexact;
        }

        /**
		 * @return  the inexact
		 * @uml.property  name="inexact"
		 */
        public Iterator getInexact()
        {
            return inexact.keySet().iterator();
        }

        public Iterator getInexactValues()
        {
            return inexact.values().iterator();
        }

        public HTEntry getExact(String key)
        {
            return (HTEntry) exact.get(key);
        }

        public HTEntry getInexact(String key)
        {
            return (HTEntry) inexact.get(key);
        }

        public HTEntry matchOf(String key)
        {
            // System.out.println("FilterFile: matchOf: " + key);

            HTEntry match = getExact(key);
            if ( match != null ) {
                // System.out.println("FilterFile: matchOf: " + key + " Returns Exact: " + match.key);
                return match;
            }

            if ( inexact.size() == 0 )
                return match;

            Iterator useInexact = inexact.entrySet().iterator();

            while ( (match == null) && useInexact.hasNext() ) {
                Map.Entry nextEntry = (Map.Entry) useInexact.next();

                String nextKey = (String) nextEntry.getKey();

                if ( key.startsWith(nextKey) )
                    match = (HTEntry) nextEntry.getValue();
            }

            /*
            if ( match != null )
                System.out.println("FilterFile: matchOf: " + key + " Returns InExact: " + match.key);
            else
                System.out.println("FilterFile: matchOf: " + key + " Returns null");
            */

            return match;
        }

        public void display()
        {
            log.Both("Content of " + name + " Table");
            
            if ( exact.size() == 0 ) {
                log.Both("  No exact entries are present.");

            } else {
                log.Both("  Exact entries:");
                Iterator useExact = getExactValues();

                while ( useExact.hasNext() ) {
                    HTEntry exactEntry = (HTEntry) useExact.next();
                    log.Both("  [ " + exactEntry.key + " ]");

                    if ( exactEntry.nameRule != null )
                        log.Both("    >> [ " + exactEntry.nameRule + " ]");
                }
            }

            if ( inexact.size() == 0 ) {
                log.Both("  No inexact entries are present.");

            } else {
                log.Both("  Inexact entries:");

                Iterator useInexact = getInexactValues();

                while ( useInexact.hasNext() ) {
                    HTEntry nextEntry = (HTEntry) useInexact.next();
                    log.Both("  Inexact [ " + nextEntry.key + " ]");

                    if ( nextEntry.nameRule != null )
                        log.Both("    >> [ " + nextEntry.nameRule + " ]");
                }
            }
        }
    }

    public FileCategory deleteBeforeWrite = new FileCategory("DeleteBeforeWrite");
    public FileCategory addOnlyFiles      = new FileCategory("AddOnly");
    public FileCategory replaceOnlyFiles  = new FileCategory("ReplaceOnly");
    public FileCategory perInstance       = new FileCategory("PerInstance");
    public FileCategory asInstallable     = new FileCategory("AsInstallable");
    public FileCategory asApplication     = new FileCategory("AsApplication");
    public FileCategory asInstalled       = new FileCategory("AsInstalled");

    protected HashMap dirs2skip           = new HashMap(16);
    protected HashMap files2skip          = new HashMap(50);

    protected static boolean inErrorState;

    public FilterFile(String filterFile, Logger log, Boolean caseOverride, boolean debug)
    {
        this.debug = debug;
		FilterFile.log = log;

		FilterFile.hc = new Helper1(log, 3);
		FilterFile.hl = new HelperList(log);

        if ( caseOverride == null ) {
            //this.caseSensitive = hc.isCaseSensitive();
            this.caseSensitive = true;  // d63134 - consistent caseSensiteve bevahiour, isCaseSesitrive is platform dependant
            this.caseSensitiveM = false;

        } else {
            this.caseSensitive = caseOverride.booleanValue();
            this.caseSensitiveM = true;
        }

		FilterFile.inErrorState = readTheFilterFile(filterFile, log);
    }

    /**
	 * @return  the debug
	 * @uml.property  name="debug"
	 */
    public boolean getDebug()
    {
        return debug;
    }

    /**
	 * @param debug  the debug to set
	 * @uml.property  name="debug"
	 */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
	 * @return  the logFileName
	 * @uml.property  name="logFileName"
	 */
    public String getLogFileName()
    {
        return logFileName;
    }

    /**
	 * @param logFileName  the logFileName to set
	 * @uml.property  name="logFileName"
	 */
    public void setLogFileName(String logFileName)
    {
        this.logFileName = logFileName.trim();
    }

    /**
	 * @return  the verbosity
	 * @uml.property  name="verbosity"
	 */
    public int getVerbosity()
    {
        return verbosity;
    }

    /**
	 * @param verbosity  the verbosity to set
	 * @uml.property  name="verbosity"
	 */
    public void setVerbosity(int verbosity)
    {
        this.verbosity = verbosity;
    }

    /**
	 * @return  the frequencyUpdate
	 * @uml.property  name="frequencyUpdate"
	 */
    public int getFrequencyUpdate()
    {
        return frequencyUpdate;
    }

    /**
	 * @param frequencyUpdate  the frequencyUpdate to set
	 * @uml.property  name="frequencyUpdate"
	 */
    public void setFrequencyUpdate(int frequencyUpdate)
    {
        this.frequencyUpdate = frequencyUpdate;
    }

    /**
	 * @return  the caseSensitive
	 * @uml.property  name="caseSensitive"
	 */
    public boolean getCaseSensitive()
    {
        return caseSensitive;
    }

    /**
	 * @return  the caseSensitiveM
	 * @uml.property  name="caseSensitiveM"
	 */
    public boolean getCaseSensitiveM()
    {
        return caseSensitiveM;
    }

    /**
	 * @param caseSensitive  the caseSensitive to set
	 * @uml.property  name="caseSensitive"
	 */
    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
        this.caseSensitiveM = true;
    }

    /**
	 * @return  the parseJar
	 * @uml.property  name="parseJar"
	 */
    public boolean getParseJar()
    {
        return parseJar;
    }

    /**
	 * @param parseJar  the parseJar to set
	 * @uml.property  name="parseJar"
	 */
    public void setParseJar(boolean parseJar)
    {
        this.parseJar = parseJar;
    }

    /**
	 * Returns the value for warProcessing.
	 * @return  Returns the warProcessing.
	 * @uml.property  name="warProcessing"
	 */
    public boolean getWarProcessing() {
        return this.warProcessing;
    }
    
    /**
	 * Sets warProcessing.
	 * @param warProcessing  The warProcessing to set.
	 * @uml.property  name="warProcessing"
	 */
    public void setWarProcessing(boolean warProcessing) {
        this.warProcessing = warProcessing;
    }
    
    /**
	 * @return  the recurse
	 * @uml.property  name="recurse"
	 */
    public boolean getRecurse()
    {
        return recurse;
    }

    /**
	 * @param recurse  the recurse to set
	 * @uml.property  name="recurse"
	 */
    public void setRecurse(boolean recurse)
    {
        this.recurse = recurse;
    }

    /**
	 * @return  the collectPermissions
	 * @uml.property  name="collectPermissions"
	 */
    public boolean getCollectPermissions()
    {
        return collectPermissions;
    }

    /**
	 * @param collectPermissions  the collectPermissions to set
	 * @uml.property  name="collectPermissions"
	 */
    public void setCollectPermissions(boolean collectPermissions)
    {
        this.collectPermissions = collectPermissions;
    }

    /**
	 * @return  the docFile
	 * @uml.property  name="docFile"
	 */
    public String getDocFile()
    {
        return docFile;
    }

    /**
	 * @param docFile  the docFile to set
	 * @uml.property  name="docFile"
	 */
    public void setDocFile(String docFile)
    {
        this.docFile = docFile.trim();
    }

    /**
	 * @return  the showOptions
	 * @uml.property  name="showOptions"
	 */
    public boolean getShowOptions()
    {
        return showOptions;
    }

    /**
	 * @param showOptions  the showOptions to set
	 * @uml.property  name="showOptions"
	 */
    public void setShowOptions(boolean showOptions)
    {
        this.showOptions = showOptions;
    }

    /**
	 * @return  the jarName
	 * @uml.property  name="jarName"
	 */
    public String getJarName()
    {
        return jarName;
    }

    /**
	 * @param jarName  the jarName to set
	 * @uml.property  name="jarName"
	 */
    public void setJarName(String jarName)
    {
        this.jarName = jarName.trim();
    }

    public String getmfClassPath()
    {
        return mfClassPath;
    }

    public void setmfClassPath(String mfClassPath)
    {
        this.mfClassPath = mfClassPath.trim();
    }

    /**
	 * @return  the compression
	 * @uml.property  name="compression"
	 */
    public int getCompression()
    {
        return compression;
    }

    /**
	 * @param compression  the compression to set
	 * @uml.property  name="compression"
	 */
    public void setCompression(int compression)
    {
        this.compression = compression;
    }

    /**
	 * @return  the verifyNewJar
	 * @uml.property  name="verifyNewJar"
	 */
    public boolean getVerifyNewJar()
    {
        return verifyNewJar;
    }

    /**
	 * @param verifyNewJar  the verifyNewJar to set
	 * @uml.property  name="verifyNewJar"
	 */
    public void setVerifyNewJar(boolean verifyNewJar)
    {
        this.verifyNewJar = verifyNewJar;
    }

    /**
	 * @return  the leadingSlash
	 * @uml.property  name="leadingSlash"
	 */
    public boolean getLeadingSlash()
    {
        return leadingSlash;
    }

    /**
	 * @param leadingSlash  the leadingSlash to set
	 * @uml.property  name="leadingSlash"
	 */
    public void setLeadingSlash(boolean leadingSlash)
    {
        this.leadingSlash = leadingSlash;
    }

    /**
	 * @return  the support
	 * @uml.property  name="support"
	 */
    public String getSupport()
    {
        return support;
    }

    /**
	 * @param support  the support to set
	 * @uml.property  name="support"
	 */
    public void setSupport(String support)
    {
        this.support = support.trim();
    }

    /**
	 * @return  the startMsg
	 * @uml.property  name="startMsg"
	 */
    public String getStartMsg()
    {
        return startMsg;
    }

    /**
	 * @param startMsg  the startMsg to set
	 * @uml.property  name="startMsg"
	 */
    public void setStartMsg(String startMsg)
    {
        this.startMsg = startMsg.trim();
    }

    /**
	 * @return  the endMsg
	 * @uml.property  name="endMsg"
	 */
    public String getEndMsg()
    {
        return endMsg;
    }

    /**
	 * @param endMsg  the endMsg to set
	 * @uml.property  name="endMsg"
	 */
    public void setEndMsg(String endMsg)
    {
        this.endMsg = endMsg.trim();
    }

    /**
	 * @return  the oldTree
	 * @uml.property  name="oldTree"
	 */
    public String getOldTree()
    {
        return oldTree;
    }

    /**
	 * @param oldTree  the oldTree to set
	 * @uml.property  name="oldTree"
	 */
    public void setOldTree(String oldTree)
    {
        this.oldTree = oldTree.replace('\\','/').trim();
    }

    /**
	 * @return  the newTree
	 * @uml.property  name="newTree"
	 */
    public String getNewTree()
    {
        return newTree;
    }

    /**
	 * @param newTree  the newTree to set
	 * @uml.property  name="newTree"
	 */
    public void setNewTree(String newTree)
    {
        this.newTree = newTree.replace('\\','/').trim();
    }

    /**
	 * @return  the eventType
	 * @uml.property  name="eventType"
	 */
    public String getEventType()
    {
        return eventType;
    }

    /**
	 * @param eventType  the eventType to set
	 * @uml.property  name="eventType"
	 */
    public void setEventType(String eventType)
    {
        this.eventType = eventType.trim();
    }

    /**
	 * @return  the affectedComponents
	 * @uml.property  name="affectedComponents"
	 */
    public String getAffectedComponents()
    {
        return affectedComponents;
    }

    /**
	 * @param affectedComponents  the affectedComponents to set
	 * @uml.property  name="affectedComponents"
	 */
    public void setAffectedComponents(String affectedComponents)
    {
        affectedComponents = affectedComponents.trim();

        if ( this.affectedComponents == null )
            this.affectedComponents = affectedComponents;
        else
            this.affectedComponents += "," + affectedComponents;
    }

    /**
	 * @return  the ckSize
	 * @uml.property  name="ckSize"
	 */
    public long getCkSize()
    {
        return ckSize;
    }

    public String getCkSizeString()
    {
        return Long.toString(ckSize);
    }

    /**
	 * @param ckSize  the ckSize to set
	 * @uml.property  name="ckSize"
	 */
    public void setCkSize(long ckSize)
    {
        this.ckSize = ckSize;
    }

    /**
	 * @return  the ckVersion
	 * @uml.property  name="ckVersion"
	 */
    public String getCkVersion()
    {
        return ckVersion;
    }

    /**
	 * @param ckVersion  the ckVersion to set
	 * @uml.property  name="ckVersion"
	 */
    public void setCkVersion(String ckVersion)
    {
        if ( this.ckVersion.equals("?") )
            this.ckVersion = ckVersion.trim();
        else
            this.ckVersion = this.ckVersion.trim() + "," + ckVersion.trim();
    }

    /**
	 * @return  the ckEditionValue
	 * @uml.property  name="ckEditionValue"
	 */
    public String getCkEditionValue()
    {
        return ckEditionValue;
    }

    /**
	 * @param ckEditionValue  the ckEditionValue to set
	 * @uml.property  name="ckEditionValue"
	 */
    public void setCkEditionValue(String ckEditionValue)
    {
        this.ckEditionValue = ckEditionValue.trim();
    }

    /**
	 * @return  the ckEditionName
	 * @uml.property  name="ckEditionName"
	 */
    public String getCkEditionName()
    {
        return ckEditionName;
    }

    /**
	 * @param ckEditionName  the ckEditionName to set
	 * @uml.property  name="ckEditionName"
	 */
    public void setCkEditionName(String ckEditionName)
    {
        this.ckEditionName = ckEditionName.trim();
    }

    /**
	 * @return  the displayManifest
	 * @uml.property  name="displayManifest"
	 */
    public boolean getDisplayManifest()
    {
        return displayManifest;
    }

    /**
	 * @param displayManifest  the displayManifest to set
	 * @uml.property  name="displayManifest"
	 */
    public void setDisplayManifest(boolean displayManifest)
    {
        this.displayManifest = displayManifest;
    }

    /**
	 * @return  the test
	 * @uml.property  name="test"
	 */
    public boolean getTest()
    {
        return test;
    }

    /**
	 * @param test  the test to set
	 * @uml.property  name="test"
	 */
    public void setTest(boolean test)
    {
        this.test = test;
    }

    /**
	 * @return  the updateXML
	 * @uml.property  name="updateXML"
	 */
    public boolean getUpdateXML()
    {
        return updateXML;
    }

    /**
	 * @param updateXML  the updateXML to set
	 * @uml.property  name="updateXML"
	 */
    public void setUpdateXML(boolean updateXML)
    {
        this.updateXML = updateXML;
    }

    /**
	 * @return  the nameSpaceAware
	 * @uml.property  name="nameSpaceAware"
	 */
    public boolean getNameSpaceAware()
    {
        return nameSpaceAware;
    }

    public String getNameSpaceAwareString()
    {
        return bool2String(nameSpaceAware);
    }

    /**
	 * @param nameSpaceAware  the nameSpaceAware to set
	 * @uml.property  name="nameSpaceAware"
	 */
    public void setNameSpaceAware(boolean nameSpaceAware)
    {
        this.nameSpaceAware = nameSpaceAware;
    }

    /**
	 * @return  the validating
	 * @uml.property  name="validating"
	 */
    public boolean getValidating()
    {
        return validating;
    }

    public String getValidatingString()
    {
        return bool2String(validating);
    }

    /**
	 * @param validating  the validating to set
	 * @uml.property  name="validating"
	 */
    public void setValidating(boolean validating)
    {
        this.validating = validating;
    }

    /**
	 * @return  the addHistory
	 * @uml.property  name="addHistory"
	 */
    public boolean getAddHistory()
    {
        return addHistory;
    }

    public String getAddHistoryString()
    {
        return bool2String(addHistory);
    }

    /**
	 * @param addHistory  the addHistory to set
	 * @uml.property  name="addHistory"
	 */
    public void setAddHistory(boolean addHistory)
    {
        this.addHistory = addHistory;
    }

    /**
	 * @return  the newBuildNumber
	 * @uml.property  name="newBuildNumber"
	 */
    public String getNewBuildNumber()
    {
        return newBuildNumber;
    }

    /**
	 * @param newBuildNumber  the newBuildNumber to set
	 * @uml.property  name="newBuildNumber"
	 */
    public void setNewBuildNumber(String newBuildNumber)
    {
        this.newBuildNumber = newBuildNumber.trim();
    }

    /**
	 * @return  the newBuildDate
	 * @uml.property  name="newBuildDate"
	 */
    public String getNewBuildDate()
    {
        return newBuildDate;
    }

    /**
	 * @param newBuildDate  the newBuildDate to set
	 * @uml.property  name="newBuildDate"
	 */
    public void setNewBuildDate(String newBuildDate)
    {
        this.newBuildDate = newBuildDate.trim();
    }

    /**
	 * @return  the newVersion
	 * @uml.property  name="newVersion"
	 */
    public String getNewVersion()
    {
        return newVersion;
    }

    /**
	 * @param newVersion  the newVersion to set
	 * @uml.property  name="newVersion"
	 */
    public void setNewVersion(String newVersion)
    {
        this.newVersion = newVersion.trim();
    }

    /**
	 * @return  the dupCheck
	 * @uml.property  name="dupCheck"
	 */
    public boolean getDupCheck()
    {
        return dupCheck;
    }

    public String getDupCheckString()
    {
        return bool2String(dupCheck);
    }

    /**
	 * @param dupCheck  the dupCheck to set
	 * @uml.property  name="dupCheck"
	 */
    public void setDupCheck(boolean dupCheck)
    {
        this.dupCheck = dupCheck;
    }

    /**
	 * @return  the xMLVersion
	 * @uml.property  name="xMLVersion"
	 */
    public String getXMLVersion()
    {
        return XMLVersion;
    }

    /**
	 * @param xMLVersion  the xMLVersion to set
	 * @uml.property  name="xMLVersion"
	 */
    public void setXMLVersion(String XMLVersion)
    {
        this.XMLVersion = XMLVersion.trim();
    }

    /**
	 * @return  the xMLPathEvent
	 * @uml.property  name="xMLPathEvent"
	 */
    public String getXMLPathEvent()
    {
        return XMLPathEvent;
    }

    /**
	 * @param xMLPathEvent  the xMLPathEvent to set
	 * @uml.property  name="xMLPathEvent"
	 */
    public void setXMLPathEvent(String XMLPathEvent)
    {
        this.XMLPathEvent = XMLPathEvent.trim();
    }

    /**
	 * @return  the xMLPathVersion
	 * @uml.property  name="xMLPathVersion"
	 */
    public String getXMLPathVersion()
    {
        return XMLPathVersion;
    }

    /**
	 * @param xMLPathVersion  the xMLPathVersion to set
	 * @uml.property  name="xMLPathVersion"
	 */
    public void setXMLPathVersion(String XMLPathVersion)
    {
        this.XMLPathVersion = XMLPathVersion.trim();
    }

    /**
	 * @return  the xMLPathEditionName
	 * @uml.property  name="xMLPathEditionName"
	 */
    public String getXMLPathEditionName()
    {
        return XMLPathEditionName;
    }

    /**
	 * @param xMLPathEditionName  the xMLPathEditionName to set
	 * @uml.property  name="xMLPathEditionName"
	 */
    public void setXMLPathEditionName(String XMLPathEditionName)
    {
        this.XMLPathEditionName = XMLPathEditionName.trim();
    }

    /**
	 * @return  the xMLPathEditionValue
	 * @uml.property  name="xMLPathEditionValue"
	 */
    public String getXMLPathEditionValue()
    {
        return XMLPathEditionValue;
    }

    /**
	 * @param xMLPathEditionValue  the xMLPathEditionValue to set
	 * @uml.property  name="xMLPathEditionValue"
	 */
    public void setXMLPathEditionValue(String XMLPathEditionValue)
    {
        this.XMLPathEditionValue = XMLPathEditionValue.trim();
    }

    /**
	 * @return  the xMLPathBuildDate
	 * @uml.property  name="xMLPathBuildDate"
	 */
    public String getXMLPathBuildDate()
    {
        return XMLPathBuildDate;
    }

    /**
	 * @param xMLPathBuildDate  the xMLPathBuildDate to set
	 * @uml.property  name="xMLPathBuildDate"
	 */
    public void setXMLPathBuildDate(String XMLPathBuildDate)
    {
        this.XMLPathBuildDate = XMLPathBuildDate.trim();
    }

    /**
	 * @return  the xMLPathBuildNumber
	 * @uml.property  name="xMLPathBuildNumber"
	 */
    public String getXMLPathBuildNumber()
    {
        return XMLPathBuildNumber;
    }

    /**
	 * @param xMLPathBuildNumber  the xMLPathBuildNumber to set
	 * @uml.property  name="xMLPathBuildNumber"
	 */
    public void setXMLPathBuildNumber(String XMLPathBuildNumber)
    {
        this.XMLPathBuildNumber = XMLPathBuildNumber.trim();
    }

    /**
	 * @return  the productFileVKey
	 * @uml.property  name="productFileVKey"
	 */
    public String getProductFileVKey()
    {
        return productFileVKey;
    }

    /**
	 * @param productFileVKey  the productFileVKey to set
	 * @uml.property  name="productFileVKey"
	 */
    public void setProductFileVKey(String productFileVKey)
    {
        this.productFileVKey = productFileVKey.trim();
    }

    /**
	 * @return  the productFileType
	 * @uml.property  name="productFileType"
	 */
    public String getProductFileType()
    {
        return productFileType;
    }

    /**
	 * @param productFileType  the productFileType to set
	 * @uml.property  name="productFileType"
	 */
    public void setProductFileType(String productFileType)
    {
        this.productFileType = productFileType.trim();
    }

    /**
	 * @return  the productFileName
	 * @uml.property  name="productFileName"
	 */
    public String getProductFileName()
    {
        return productFileName;
    }

    /**
	 * @param productFileName  the productFileName to set
	 * @uml.property  name="productFileName"
	 */
    public void setProductFileName(String productFileName)
    {
        this.productFileName = productFileName.trim();
    }

    /**
	 * @return  the buildSelfExtractor
	 * @uml.property  name="buildSelfExtractor"
	 */
    public boolean getBuildSelfExtractor()
    {
        return buildSelfExtractor;
    }

    /**
	 * @param buildSelfExtractor  the buildSelfExtractor to set
	 * @uml.property  name="buildSelfExtractor"
	 */
    public void setBuildSelfExtractor(boolean buildSelfExtractor)
    {
        this.buildSelfExtractor = buildSelfExtractor;
    }

    /**
	 * @return  the description
	 * @uml.property  name="description"
	 */
    public String getDescription()
    {
        return description;
    }

    /**
	 * @param description  the description to set
	 * @uml.property  name="description"
	 */
    public void setDescription(String description)
    {
        this.description = description.trim();
    }

    /**
	 * @return  the aPAR
	 * @uml.property  name="aPAR"
	 */
    public String getAPAR()
    {
        return APAR;
    }

    /**
	 * @param aPAR  the aPAR to set
	 * @uml.property  name="aPAR"
	 */
    public void setAPAR(String APAR)
    {
        this.APAR = APAR.trim();
    }

    /**
	 * @return  the pMR
	 * @uml.property  name="pMR"
	 */
    public String getPMR()
    {
        return PMR;
    }

    /**
	 * @param pMR  the pMR to set
	 * @uml.property  name="pMR"
	 */
    public void setPMR(String PMR)
    {
        this.PMR = PMR.trim();
    }

    /**
	 * @return  the check4Class
	 * @uml.property  name="check4Class"
	 */
    public String getCheck4Class()
    {
        return check4Class;
    }

    /**
	 * @param check4Class  the check4Class to set
	 * @uml.property  name="check4Class"
	 */
    public void setCheck4Class(String check4Class)
    {
        this.check4Class= check4Class.trim();
    }

    /**
	 * @return  the targetMsg
	 * @uml.property  name="targetMsg"
	 */
    public String getTargetMsg()
    {
        return targetMsg;
    }

    /**
	 * @param targetMsg  the targetMsg to set
	 * @uml.property  name="targetMsg"
	 */
    public void setTargetMsg(String targetMsg)
    {
        this.targetMsg = targetMsg.trim();
    }

    /**
	 * @return  the target
	 * @uml.property  name="target"
	 */
    public String getTarget()
    {
        return target;
    }

    /**
	 * @param target  the target to set
	 * @uml.property  name="target"
	 */
    public void setTarget(String target)
    {
        this.target = target.trim();
    }

    /**
	 * @return  the cmdDelimiter
	 * @uml.property  name="cmdDelimiter"
	 */
    public String getCmdDelimiter()
    {
        return cmdDelimiter;
    }

    /**
	 * @param cmdDelimiter  the cmdDelimiter to set
	 * @uml.property  name="cmdDelimiter"
	 */
    public void setCmdDelimiter(String cmdDelimiter)
    {
        this.cmdDelimiter = cmdDelimiter;
    }

    /**
	 * @return  the argDelimiter
	 * @uml.property  name="argDelimiter"
	 */
    public String getArgDelimiter()
    {
        return argDelimiter;
    }

    /**
	 * @param argDelimiter  the argDelimiter to set
	 * @uml.property  name="argDelimiter"
	 */
    public void setArgDelimiter(String argDelimiter)
    {
        this.argDelimiter = argDelimiter;
    }

    /**
	 * @return  the genBD
	 * @uml.property  name="genBD"
	 */
    public boolean getGenBD()
    {
        return genBD;
    }

    /**
	 * @param genBD  the genBD to set
	 * @uml.property  name="genBD"
	 */
    public void setGenBD(boolean genBD)
    {
        this.genBD = genBD;
    }

    /**
	 * @return  the maxSizePct
	 * @uml.property  name="maxSizePct"
	 */
    public int getMaxSizePct()
    {
        return maxSizePct;
    }

    /**
	 * @param maxSizePct  the maxSizePct to set
	 * @uml.property  name="maxSizePct"
	 */
    public void setMaxSizePct(int maxSizePct)
    {
        this.maxSizePct = maxSizePct;
    }

    /**
	 * @return  the reSyncLen
	 * @uml.property  name="reSyncLen"
	 */
    public int getReSyncLen()
    {
        return reSyncLen ;
    }

    /**
	 * @param reSyncLen  the reSyncLen to set
	 * @uml.property  name="reSyncLen"
	 */
    public void setReSyncLen(int reSyncLen)
    {
        this.reSyncLen = reSyncLen;
    }

    /**
	 * @return  the reSyncScan
	 * @uml.property  name="reSyncScan"
	 */
    public int getReSyncScan()
    {
        return reSyncScan ;
    }

    /**
	 * @param reSyncScan  the reSyncScan to set
	 * @uml.property  name="reSyncScan"
	 */
    public void setReSyncScan(int reSyncScan)
    {
        this.reSyncScan = reSyncScan;
    }

    public int getChunckSize()
    {
        return chunkSize;
    }

    public void setChunckSize(int chunkSize)
    {
        this.chunkSize = chunkSize;
    }

    /**
	 * @return  the chunkSize
	 * @uml.property  name="chunkSize"
	 */
    public int getChunkSize()
    {
        return chunkSize;
    }

    /**
	 * @param chunkSize  the chunkSize to set
	 * @uml.property  name="chunkSize"
	 */
    public void setChunkSize(int chunkSize)
    {
        this.chunkSize = chunkSize;
    }

    /**
	 * @return  the spareProducts
	 * @uml.property  name="spareProducts"
	 */
    public Vector getSpareProducts()
    {
        return spareProducts;
    }

    public int getSpareProductsCount()
    {
        return spareProducts.size();
    }

    public String getSpareProducts(int element)
    {
        return (String) spareProducts.elementAt(element);
    }

    public void setSpareProducts(String productFile)
    {
        spareProducts.add(productFile.trim());
    }

    /**
	 * @return  the components
	 * @uml.property  name="components"
	 */
    public HashMap getComponents()
    {
        return components;
    }

    public int getComponentsSize()
    {
        return components.size();
    }

    public Iterator getComponentsEnumKey()
    {
        return components.keySet().iterator();
    }

    public String getComponents(String key)
    {
        return (String) components.get(key);
    }

    /**
	 * @return  the forceBackup
	 * @uml.property  name="forceBackup"
	 */
    public Vector getForceBackup()
    {
        return forceBackup;
    }

    public int getForceBackupCount()
    {
        return forceBackup.size();
    }

    public String getForceBackup(int element)
    {
        return (String) forceBackup.elementAt(element);
    }

    public void setForceBackup(String forceBackupFile)
    {
        forceBackup.add(forceBackupFile.trim());
    }

    // this is a list of additional package files we need

    /**
	 * @return  the packageDirectories
	 * @uml.property  name="packageDirectories"
	 */
    public Vector getPackageDirectories()
    {
        return packageDirectories;
    }

    public int getPackageDirectoriesCount()
    {
        return packageDirectories.size();
    }

    public String getPackageDirectories(int element)
    {
        return (String) packageDirectories.elementAt(element);
    }

    public void setPackageDirectories(String packageDirectory)
    {
        packageDirectories.add(packageDirectory.trim());
    }

    // this is a list of directories from where to load the helpers

    /**
	 * @return  the helperDirectories
	 * @uml.property  name="helperDirectories"
	 */
    public Vector getHelperDirectories()
    {
        return helperDirectories;
    }

    public int getHelperDirectoriesCount()
    {
        return helperDirectories.size();
    }

    public String getHelperDirectories(int element)
    {
        return (String) helperDirectories.elementAt(element);
    }

    public void setHelperDirectories(String location)
    {
        String errMsg = processHelperSrc(location.trim(), helperDirectories);

          if ( errMsg != null )
            log.Err(91, "Processing " + HelperList.o_HelperLocation + " " + location + "; " + errMsg);
    }

    /**
	 * @return  the noBackupJar
	 * @uml.property  name="noBackupJar"
	 */
    public HashMap getNoBackupJar()
    {
        return noBackupJar;
    }

    public int getNoBackupJarSize()
    {
        return noBackupJar.size();
    }

    public Iterator getNoBackupJarEnumKey()
    {
        return noBackupJar.keySet().iterator();
    }

    public String getNoBackupJar(String key)
    {
        return (String) noBackupJar.get(key);
    }

    /**
	 * @return  the propUpdate
	 * @uml.property  name="propUpdate"
	 */
    public Vector getPropUpdate()
    {
        return propUpdate;
    }

    public int getPropUpdateCount()
    {
        return propUpdate.size();
    }

    public PropUpdateInfo getPropUpdate(int element)
    {
        return (PropUpdateInfo) propUpdate.elementAt(element);
    }

    public void setPropUpdate(PropUpdateInfo pui)
    {
        propUpdate.add(pui);
    }

    /**
	 * @return  the fileUpdate
	 * @uml.property  name="fileUpdate"
	 */
    public Vector getFileUpdate()
    {
        return fileUpdate;
    }

    public int getFileUpdateCount()
    {
        return fileUpdate.size();
    }

    public FileUpdateInfo getFileUpdate(int element)
    {
        return (FileUpdateInfo) fileUpdate.elementAt(element);
    }

    public void setFileUpdate(FileUpdateInfo fui)
    {
        fileUpdate.add(fui);
    }

    /**
	 * @return  the zipEntryUpdates
	 * @uml.property  name="zipEntryUpdates"
	 */
    public Vector getZipEntryUpdates()
    {
        return zipEntryUpdates;
    }

    public int getZipEntryUpdatesCount()
    {
        return zipEntryUpdates.size();
    }

    public zipEntryUpdateInfo getZipEntryUpdates(int element)
    {
        return (zipEntryUpdateInfo) zipEntryUpdates.elementAt(element);
    }

    public void setZipEntryUpdates(zipEntryUpdateInfo zeui)
    {
        zipEntryUpdates.add(zeui);
    }

    /**
	 * @return  the virtualScripts
	 * @uml.property  name="virtualScripts"
	 */
    public Vector getVirtualScripts()
    {
        return virtualScripts;
    }

    public int getVirtualScriptsCount()
    {
        return virtualScripts.size();
    }

    public Script getVirtualScripts(int element)
    {
        return (Script) virtualScripts.elementAt(element);
    }

    public void setVirtualScripts(PropUpdateInfo sc)
    {
        virtualScripts.add(sc);
    }

    /**
	 * @return  the restoreOnly
	 * @uml.property  name="restoreOnly"
	 */
    public Vector getRestoreOnly()
    {
        return restoreOnly;
    }

    public int getRestoreOnlyCount()
    {
        return restoreOnly.size();
    }

    public HTEntry getRestoreOnly(int element)
    {
        return (HTEntry) restoreOnly.elementAt(element);
    }

    public void setRestoreOnly(String source, String target, String version)
    {
        HTEntry lhte = new HTEntry(!isHelper);
        lhte.key = HelperList.ResolveMacro("?<>", source.trim(), -1, "Command Line");

        if ( canRead(lhte.key, errSB) == null )
            log.Err(133, "The -RestoreOnly file (" + lhte.key + ") can not be read: " + errSB) ;

        lhte.vData = HelperList.ResolveMacro("?<>", target.trim(), -1, "Command Line");
        lhte.requiredVersion = version;

        restoreOnly.add(lhte);
    }

    /**
	 * @return  the includes
	 * @uml.property  name="includes"
	 */
    public Vector getIncludes()
    {
        return includes;
    }

    public int getIncludesCount()
    {
        return includes.size();
    }

    public HTEntry getIncludes(int element)
    {
        return (HTEntry) includes.elementAt(element);
    }

    /**
	 * @return  the importComponents
	 * @uml.property  name="importComponents"
	 */
    public Vector getImportComponents()
    {
        return importComponents;
    }

    public int getImportComponentsCount()
    {
        return importComponents.size();
    }

    public HTEntry getImportComponents(int element)
    {
        return (HTEntry) importComponents.elementAt(element);
    }

    /**
	 * @return  the transferFile
	 * @uml.property  name="transferFile"
	 */
    public HashMap getTransferFile()
    {
        return transferFile;
    }

    public HTEntry getTransferFile(String key)
    {
        return (HTEntry) transferFile.get(key);
    }

    public Iterator getTransferFileEnumKey()
    {
        return transferFile.keySet().iterator();
    }

    public void setTransferFile(String key, HTEntry hte)
    {
        transferFile.put(key, hte);
    }

    // Constants for use as arguments to 'setTransferFile' and 'add2TransferFile'.

    protected static final boolean absolutePath = true ;
    // Constants for use as arguments to 'setTransferFile' and 'add2TransferFile'.

    protected static final boolean addOnly = true ;
    // Constants for use as arguments to 'setTransferFile' and 'add2TransferFile'.

    protected static final boolean replaceOnly = true ;

    public void setTransferFile(String fileName,
                                String newFileName,
                                boolean absolutePath,
                                boolean addOnly,
                                boolean replaceOnly)
    {
        HTEntry hte     = new HTEntry(!isHelper);
        hte.absPath     = absolutePath;
        hte.addOnly     = addOnly;
        hte.replaceOnly = replaceOnly;
        hte.vData       = fileName;

        if ( newFileName == null )
            transferFile.put(fileName, hte);
        else
            transferFile.put(newFileName, hte);
    }

    protected void add2TransferFile(int tokenCount, StringTokenizer tok,
                                    boolean absolutePath,
                                    boolean addOnly,
                                    boolean replaceOnly)
    {
        String fileName = tok.nextToken();

        HTEntry hte     = new HTEntry(!isHelper);
        hte.absPath     = absolutePath;
        hte.addOnly     = addOnly;
        hte.replaceOnly = replaceOnly;
        hte.vData       = fileName;
    
        String transferKey;

        if ( tokenCount >= 3 )
            transferKey = tok.nextToken();
        else
            transferKey = fileName;

        transferFile.put(transferKey, hte);
    }

    public Vector geteFixFiles()
    {
        return eFixFiles;
    }

    public int geteFixFilesCount()
    {
        return eFixFiles.size();
    }

    public HTEntry geteFixFiles(int element)
    {
        return (HTEntry) eFixFiles.elementAt(element);
    }

    /**
	 * @return  the reSequenceJar
	 * @uml.property  name="reSequenceJar"
	 */
    public Vector getReSequenceJar()
    {
        return reSequenceJar;
    }

    public int getReSequenceJarCount()
    {
        return reSequenceJar.size();
    }

    public Iterator getReSequenceJarEnum()
    {
        return reSequenceJar.iterator();
    }

    public HTEntry getReSequenceJar(int element)
    {
        return (HTEntry) reSequenceJar.elementAt(element);
    }

    /**
	 * @return  the reName
	 * @uml.property  name="reName"
	 */
    public Vector getReName()
    {
        return reName;
    }

    public int getReNameCount()
    {
        return reName.size();
    }

    public Iterator getReNameEnum()
    {
        return reName.iterator();
    }

    public HTEntry getReName(int element)
    {
        return (HTEntry) reName.elementAt(element);
    }

    /**
	 * @return  the noRestore
	 * @uml.property  name="noRestore"
	 */
    public HashMap getNoRestore()
    {
        return noRestore;
    }

    public HTEntry getNoRestore(String key)
    {
        return (HTEntry)  noRestore.get(key);
    }

    public HTEntry setNoRestore(String key, boolean ifHelper)
    {
        HTEntry hte = new HTEntry(ifHelper);

        noRestore.put(key, hte);

        return hte;
    }

    /**
	 * @return  the forceReplace
	 * @uml.property  name="forceReplace"
	 */
    public HashMap getForceReplace()
    {
        return forceReplace;
    }

    public HTEntry getForceReplace(String key)
    {
        return (HTEntry) forceReplace.get(key);
    }

    public Iterator getForceReplaceEnumKey()
    {
        return forceReplace.keySet().iterator();
    }

    public HTEntry setForceReplace(String key, boolean ifHelper)
    {
        HTEntry hte = new HTEntry(ifHelper);

        forceReplace.put(key, hte);

        return hte;
    }

    /**
	 * @return  the noDelete
	 * @uml.property  name="noDelete"
	 */
    public HashMap getNoDelete()
    {
        return noDelete;
    }

    public HTEntry getNoDelete(String key)
    {
        return (HTEntry) noDelete.get(key);
    }

    public Iterator getNoDeleteEnumKey()
    {
        return noDelete.keySet().iterator();
    }

    /**
	 * @return  the chmod
	 * @uml.property  name="chmod"
	 */
    public HashMap getChmod()
    {
        return noDelete;
    }

    public HTEntry getChmod(String key)
    {
        return (HTEntry) chmod.get(key);
    }

    public Iterator getChmodEnumKeys()
    {
        return chmod.keySet().iterator();
    }

    public HTEntry setChmod(String fileName, String chmodValue)
    {
        HTEntry hte = new HTEntry(!isHelper);
        hte.key = fileName;
        hte.vData = chmodValue;

        chmod.put(fileName, hte);

        return hte;
    }

    public void removeChmod(String fileName)
    {
        chmod.remove(fileName);
    }

    public int getChmodCount()
    {
        return chmod.size();
    }

    public HashMap getDirs2Skip()
    {
        return dirs2skip;
    }

    public HTEntry getDirs2Skip(String key)
    {
        return (HTEntry) dirs2skip.get(key);
    }

    public Iterator getDirs2SkipEnumKey()
    {
        return dirs2skip.keySet().iterator();
    }

    public HTEntry setDirs2Skip(String fileName, boolean ifHelper)
    {
        HTEntry hte = new HTEntry(ifHelper);
        hte.key = fileName;
        
        dirs2skip.put(fileName, hte);

        return hte;
    }

    public HashMap getFiles2Skip()
    {
        return files2skip;
    }

    public HTEntry getFiles2Skip(String key)
    {
        return (HTEntry) files2skip.get(key);
    }

    public Iterator getFiles2SkipEnumKey()
    {
        return files2skip.keySet().iterator();
    }

    public HTEntry setFiles2Skip(String fileName, boolean ifHelper)
    {
        HTEntry hte = new HTEntry(ifHelper);
        hte.key = fileName;

        files2skip.put(fileName, hte);

        return hte;
    }

    protected String filterFileName = "<filterFileName not set>";
    protected int lineNum = -1;

    public boolean readTheFilterFile(String filterFile, Logger log)
    {

	String testFile = new File(filterFile).getAbsolutePath();
	String lastSlash = testFile.substring(0, testFile.lastIndexOf(File.separatorChar));
	lastSlash = lastSlash.substring(0, lastSlash.lastIndexOf(File.separatorChar));
	
        if ( debug )
            log.Both("Debug - Entering readTheFilterFile(" + filterFile + ")");

        String msg;
        String location = "";
        int[] lineNumber = { 0 };        // to receive the real line number form gnll

        boolean returnValue     = true;
        boolean correctPlatform = true;  // we assume the correct platform until otherwise indicated

        String updateFile       = null;  // for use with serach and replace string Updates
        String updateDelimiter  = null;
        String[] componentLimitations = null;
        String propFile         = null;  // for use with property files updates
        String propDelimiter    = null;
        String softError        = "";

        zipEntryUpdateInfo zEUI = null;  // Jar/Zip file to update with comment or Extra entries

        String getFile          = null;
        int    eFixType         = 0;

        Script script      = null;  // this is the current script class, for both Pre and Post

        HTEntry hte             = null;
        boolean nullHte         = true;

        filterFileName = filterFile;

        String workLine = null;

        while ( (workLine = hc.getNextLogicalLine(lineNumber, filterFile)) != null ) {
            lineNum = lineNumber[0];
            location = "At line #" + lineNum + " within file " + filterFileName + ", ";
            errSB.setLength(0);  // clear out any old error messages

            if ( debug )
                log.Both("Line# " + lineNum + " (" + workLine + ")");

            if ( workLine.length() == 0 )      // skip lines of zero length
                continue;

            workLine = HelperList.ResolveMacro("?<>", workLine, lineNum, filterFileName).trim();

            if ( workLine.startsWith("//") || // Test for a comment line
                 workLine.startsWith("#")  ||
                 workLine.startsWith("!")  ||
                 workLine.startsWith("*") )
                continue;

            // Platform filtering:
            // [osName, osName,...]
            // Spaces are trimmed out.

            if ( workLine.startsWith("[") ) {
                log.Both(6, "Debug Identified platform filter (" + workLine + ") on Line #" + lineNum);

                if (workLine.indexOf("]") == 0) {
                    log.Err(46, "processing line #" + lineNum +
                                " within " + filterFile +
                                ", no closing square bracket found.");
                }

                workLine = workLine.replace('[', ' ');
                workLine = workLine.replace(']', ' ');
                workLine = workLine.trim();

                String osName = System.getProperty("os.name");

                StringTokenizer platforms = new StringTokenizer(workLine, ",");
                correctPlatform = (platforms.countTokens() == 0);

                while ( !correctPlatform && platforms.hasMoreElements() ) {
                    String os = (String) platforms.nextToken().trim();

                    log.Both(6, "Debug -- Inspecting platform filter (" + os + ")" +
                                " on Line #" + lineNum);

                    if ( os.equalsIgnoreCase(osName) )
                        correctPlatform = true;
                }

                log.Both(6, "Debug -- Platform filter state is (" + correctPlatform + ")" +
                            " on Line #" + lineNum);
                continue;
            }

            StringTokenizer tok = new StringTokenizer(workLine);
            int tokenCount = tok.countTokens();
            String statement = tok.nextToken();

            // Null the entry for every thing except
            // include, includeCut and Signatures.

            if ( nullHte )
                hte = null;

            if ( correctPlatform )
                nullHte = true;

            if ( correctPlatform ) {
                String tok2, tok3;

                // Debug
                if ( statement.equalsIgnoreCase(HelperList.o_Debug) ) {
                    debug = true;

                } else if ( statement.equalsIgnoreCase(HelperList.o_CaseSensitive) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        if ( !getCaseSensitiveM() )
                            setCaseSensitive(test4Boolean(tok.nextToken(), "caseSensitive"));
                        else
                            log.Both("Overriding filter file case sensitivity.");
                    }

                // SkipFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_SkipFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setFiles2Skip(PrepareTokFmt1(tok.nextToken()), !isHelper);

                // SkipDir <dirName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_SkipDir) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setDirs2Skip(PrepareTokFmt1(tok.nextToken()), !isHelper);

                // DeleteBeforeWrite <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_DeleteBeforeWrite) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        deleteBeforeWrite.putEntry(tok.nextToken());

                // AddOnly <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AddOnly) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        addOnlyFiles.putEntry(tok.nextToken());

                // ReplaceOnly <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ReplaceOnly) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        replaceOnlyFiles.putEntry(tok.nextToken());

                // PerInstance <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_PerInstance) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        perInstance.putEntry(tok.nextToken());

                // AsInstallable <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AsInstallable) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        asInstallable.putEntry(tok.nextToken());

                // AsApplication <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AsApplication) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        asApplication.putEntry(tok.nextToken());

                // AsInstalled <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AsInstalled) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        asInstalled.putEntry(tok.nextToken());

                // AsMetadata <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AsMetadata) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        asInstalled.putAltEntry(tok.nextToken());

                // NoRestore <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_NoRestore) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        noRestore.put(PrepareTokFmt4(tok.nextToken()), new HTEntry(!isHelper));

                // NoDelete <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_NoDelete) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        noDelete.put(PrepareTokFmt4(tok.nextToken()), new HTEntry(!isHelper));

                // ForceReplace <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ForceReplace) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setForceReplace(PrepareTokFmt1( tok.nextToken()), !isHelper);

                // Chmod <fileName> <permissions>
                } else if ( statement.equalsIgnoreCase(HelperList.o_Chmod) ) {
                    if ( ValidateTokenCount(tokenCount, 3) ) {
                        tok2 = PrepareTokFmt4(tok.nextToken());
                        setChmod(tok2, tok.nextToken() );
                    }

                // TransferFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_TransferFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        add2TransferFile(tokenCount, tok, !absolutePath, !addOnly, !replaceOnly);

                // AddFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AddFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        add2TransferFile(tokenCount, tok, !absolutePath, addOnly, !replaceOnly);

                // ReplaceFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ReplaceFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        add2TransferFile(tokenCount, tok, !absolutePath, !addOnly, replaceOnly);

                // AbsTransferFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AbsTransferFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        add2TransferFile(tokenCount, tok, absolutePath, !addOnly, !replaceOnly);

                // AbsAddFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AbsAddFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        add2TransferFile(tokenCount, tok, absolutePath, addOnly, !replaceOnly);

                // AbsReplaceFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AbsReplaceFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        add2TransferFile(tokenCount, tok, absolutePath, !addOnly, replaceOnly);

                // Exclude <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_Exclude) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        hte = new HTEntry(!isHelper);
                        hte.include = false;
                        hte.key = PrepareTokFmt2(tok.nextToken());
                        includes.add(hte);
                    }

                // Include <path> [componentName]
                } else if ( statement.equalsIgnoreCase(HelperList.o_Include) ) {
                    hte = new HTEntry(!isHelper);
                    hte.include = true;

                    String treePath = tok.nextToken();

                    if ( tokenCount > 2 ) {
                        hte.componentName = tok.nextToken();
                        if ( !components.containsKey(hte.componentName) )
                            components.put(hte.componentName, HelperList.meNoSignature);
                    }

                    hte.key = PrepareTokFmt2(treePath);
                    includes.add(hte);
                    nullHte = false;   // this will accept a signature

                // IncludeCut <path> [componentName]
                } else if ( statement.equalsIgnoreCase(HelperList.o_IncludeCut) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        hte = new HTEntry(!isHelper);
                        hte.include = true;
                        hte.cut = true;

                        String treePath = tok.nextToken();

                        if ( tokenCount > 2 ) {
                            hte.componentName = tok.nextToken();
                            if ( !components.containsKey(hte.componentName) )
                                components.put(hte.componentName, HelperList.meNoSignature);
                        }

                        hte.key = PrepareTokFmt2(treePath);
                        includes.add(hte);
                        nullHte = false;   // this will accept a signature
                    }

                // ImportComponent <listFileName> [ component [ path ] ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_ImportComponent) ) {
                    if ( ValidateTokenCount(tokenCount, 3) ) {
                        hte = new HTEntry(!isHelper);
                        hte.include = true;
                        hte.cut = false;

                        String treePath = tok.nextToken();

                        if ( tokenCount > 2 ) {
                            hte.componentName = tok.nextToken();

                            if ( !components.containsKey(hte.componentName) )
                                components.put(hte.componentName, HelperList.meNoSignature);

                            if ( tokenCount > 3 )
                                hte.vData = tok.nextToken();
                        }

                        hte.key = treePath;
                        importComponents.add(hte);
                        nullHte = false;   // this will accept a signature
                    }

                // TranslatePathPart <fieldA> <fieldB> TFB: ???
                } else if ( statement.equalsIgnoreCase(HelperList.o_TranslatePathPart) ) {
                    if ( ValidateTokenCount(tokenCount, 3) ) {
                        if ( hte == null ) {
                            log.Err(1, "The TranslatePathPart specification " + location +
                                       ", is out of place, an importComponent statement" +
                                       " needs to preceed it.");

                        } else {
                            String fldA = tok.nextToken();
                            String fldB = tok.nextToken();

                            hte.translate.put(fldA.trim(), fldB.trim() );
                        }

                        nullHte = false;  // here we may want to accept several signature files
                    }
                    
                // Signature <signaturesFile> TFB: ???
                } else if ( statement.equalsIgnoreCase(HelperList.o_Signature) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        if ( hte == null ) {
                            log.Err(128, "The Signature specification at line #" + lineNum +
                                         " within " + filterFile +
                                         " is out of place, an Include statement needs to preceed it.");
                            
                        } else {
                            hte.signatures = tok.nextToken(childDivider);
                            components.put(hte.componentName, hte.signatures.trim());
                        }
                        
                        nullHte = false;  // here we may want to accept several signature files
                    }

                // XMLPathVersion <xml.path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_XMLPathVersion) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setXMLPathVersion(tok.nextToken(childDivider));
                    
                // XMLPathEditionName <xml.path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_XMLPathEditionName) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setXMLPathEditionName(tok.nextToken(childDivider));
                    
                // XMLPathEditionValue <xml.path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_XMLPathEditionValue) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setXMLPathEditionValue(tok.nextToken(childDivider));
                    
                // XMLPathBuildDate <xml.path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_XMLPathBuildDate) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setXMLPathBuildDate(tok.nextToken(childDivider));
                    
                // XMLPathBuildNumber <xml.path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_XMLPathBuildNumber) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setXMLPathBuildNumber( tok.nextToken(childDivider));
                    
                // XMLPathEvent <xml.path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_XMLPathEvent) ) {
                    if (ValidateTokenCount(tokenCount, 2))
                        setXMLPathEvent(tok.nextToken(childDivider));
                    
                // ckSize <size>
                } else if ( statement.equalsIgnoreCase(HelperList.o_CkSize) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setCkSize(hc.test4numerics(tok.nextToken(childDivider), 0, errSB));

                    if ( errSB.length() > 0 ) {
                        log.Err(54, location + " CkSize value, " + errSB.toString());
                        returnValue = false;
                    }
                    
                // ckVersion <version>
                } else if ( statement.equalsIgnoreCase(HelperList.o_CkVersion) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setCkVersion(tok.nextToken(childDivider));
                    
                // NewVersion <version>
                } else if ( statement.equalsIgnoreCase(HelperList.o_NewVersion) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setNewVersion(tok.nextToken(childDivider));
                    
                // NewBuildNumber <buildNumber>
                } else if ( statement.equalsIgnoreCase(HelperList.o_NewBuildNumber) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setNewBuildNumber(tok.nextToken(childDivider));
                    
                // NewBuildDate <buildDate>
                } else if ( statement.equalsIgnoreCase(HelperList.o_NewBuildDate) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setNewBuildDate(tok.nextToken(childDivider));
                    
                // CkEditionValue <editionValue>
                } else if ( statement.equalsIgnoreCase( HelperList.o_CkEditionValue) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setCkEditionValue(tok.nextToken(childDivider));
                
                // CkEditionName <editionName>
                } else if ( statement.equalsIgnoreCase(  HelperList.o_CkEditionName) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setCkEditionName(tok.nextToken(childDivider));
                    
                // UpdateFile <fileName> [component, .. 

                } else if ( statement.equalsIgnoreCase( HelperList.o_UpdateFile) ) {
                    if ( ValidateTokenCount(tokenCount, 3) ) {
                        updateFile = PrepareTokFmt4(tok.nextToken());
                        updateDelimiter = tok.nextToken();
                        
                        if ( tokenCount > 3 ) {
                            componentLimitations =
                                resolveComponentLimitations(tok.nextToken(childDivider), location);
                        } else {
                            componentLimitations = new String[0];
                        }
                    }
                    
                // FindReplace ... TFB: ????
                } else if ( statement.equalsIgnoreCase( HelperList.o_FindReplace) ) {
                    if ( ValidateTokenCount(tokenCount, 1) ) {
                        if ( updateDelimiter == null ) {
                            log.Err(30, "An UpdateFile statement must be specified" +
                                        " before FindReplace statements.");
                            
                        } else {
                            StringTokenizer fr =
                                new StringTokenizer(tok.nextToken(childDivider), updateDelimiter);
                            if ( fr.countTokens() < 2 ) {
                                log.Err(55, "The FindReplace statement seems not" +
                                            " to have the needed delimiter (" + updateDelimiter +
                                            ") on line#" + lineNum);
                                continue;

                            } else {
                                FileUpdateInfo fui = new FileUpdateInfo();
                                fui.filespec   = updateFile;
                                fui.find       = fr.nextToken().trim();
                                fui.replace    = fr.nextToken().trim();
                                fui.components = componentLimitations;

                                setFileUpdate(fui);
                            }
                        }
                    }

                // Rename <oldFileName> <newFileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_Rename) ) {
                    if ( ValidateTokenCount(tokenCount, 3) ) {
                        hte = new HTEntry(!isHelper);
                        hte.key = PrepareTokFmt3(tok.nextToken());  // OldPath
                        hte.vData = PrepareTokFmt3(tok.nextToken());  // New Path

                        if ( (hte.key.endsWith("\\")) || (hte.key.endsWith("/")) )
                            hte.absPath = false;   // indicate this is a partial path rename
                        else
                            hte.absPath = true;    // indicate this is a full rename

                        reName.add(hte);
                    }

                // ChunkSize <size>
                // ChunckSize <size>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ChunckSize) ||
                            statement.equalsIgnoreCase(HelperList.o_ChunkSize) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        setChunkSize(hc.test4numerics(tok.nextToken(), 0, errSB));

                        if ( errSB.length() > 0 ) {
                            log.Err(54, location + " ChunkSize value, " + errSB.toString());
                            returnValue = false;
                        }
                    }

                // ReSyncLen <length>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ReSyncLen) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        setReSyncLen(hc.test4numerics(tok.nextToken(), 0, errSB));
                        
                        if ( errSB.length() > 0 ) {
                            log.Err(54, location + " reSyncLen value, " + errSB.toString());
                            returnValue = false;
                        }
                    }
                    
                // ReSyncScan <length>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ReSyncScan) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        setReSyncScan(hc.test4numerics(tok.nextToken(), 0, errSB));
                        
                        if ( errSB.length() > 0 ) {
                            log.Err(54, location + " reSyncScan value, " + errSB.toString());
                            returnValue = false;
                        }
                    }
                    
                // MaxSizePct <size>
                } else if ( statement.equalsIgnoreCase(HelperList.o_MaxSizePct) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        setMaxSizePct(hc.test4numerics(tok.nextToken(), 0, errSB));
                        
                        if ( errSB.length() > 0 ) {
                            log.Err(63, location + " MaxSizePct value, " + errSB.toString());
                            returnValue = false;
                        }
                    }
                    
                // NoBackUpJar [ifExists|ifNotExists] <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_NoBackUpJar) ) {
                    if ( ValidateTokenCount(tokenCount, 3) ) {
                        String cond = tok.nextToken();
                        String filespec = tok.nextToken();

                        if ( cond.equalsIgnoreCase("ifExists") || cond.equalsIgnoreCase("ifNotExists") ) {
                            cond = (cond.equalsIgnoreCase("ifExists")) ? "1" : "0";
                            noBackupJar.put(filespec, cond);
                        } else {
                            log.Err(68, "On line #" + lineNum +
                                        ", The second token of NoBackupJar must be \"ifExists\"" +
                                        " or \"ifNotExists\", found was " + cond);
                        }
                    }

                // PropertyFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_PropertyFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        propFile = PrepareTokFmt4(tok.nextToken());
                        propDelimiter = tok.nextToken();
                    }

                // [ PropAdd | PropSoftAdd ] ... TFB: ??
                } else if ( statement.equalsIgnoreCase(HelperList.o_PropAdd) ||
                            statement.equalsIgnoreCase(HelperList.o_PropSoftAdd) ) {

                    if ( statement.equalsIgnoreCase(HelperList.o_PropSoftAdd) )
                        softError = "soft";

                    if ( ValidateTokenCount(tokenCount, 1) ) {
                        if ( propDelimiter == null ) {
                            log.Err(71, "A PropertyFile statement must be specified before propAdd statements.");

                        } else {
                            StringTokenizer fr = new StringTokenizer(tok.nextToken(childDivider), propDelimiter);
                            if ( fr.countTokens() < 2 ) {
                                log.Err(72, "The PropAdd statement seems not to have" +
                                        " the needed delimiter (" + propDelimiter + ") on line#" + lineNum);
                                continue;

                            } else {
                                PropUpdateInfo pui = new PropUpdateInfo();
                                pui.propFileName = propFile;
                                pui.key          = fr.nextToken().trim();
                                pui.value        = fr.nextToken().trim();
                                pui.function     = softError.concat("Add");

                                setPropUpdate(pui);

                                softError = "";
                            }
                        }
                    }


                // [ PropDelete | PropSoftDelete ] ... TFB: ???
                } else if ( (statement.equalsIgnoreCase(HelperList.o_PropDelete)) ||
                            (statement.equalsIgnoreCase(HelperList.o_PropSoftDelete)) ) {

                    if ( statement.equalsIgnoreCase(HelperList.o_PropSoftDelete) )
                        softError = "soft";

                    if ( ValidateTokenCount(tokenCount, 1) ) {
                        
                        if ( propDelimiter == null ) {
                            log.Err(73, "A PropertyFile statement must be specified" +
                                        " before propDelete statements.");

                        } else {
                            StringTokenizer fr = new StringTokenizer(tok.nextToken(childDivider), propDelimiter);
                            if ( fr.countTokens() < 1 ) {
                                log.Err(74, "The PropDelete statement seems not to have" +
                                        " a needed key to delete on line#" + lineNum);
                                continue;
                                
                            } else {
                                PropUpdateInfo pui = new PropUpdateInfo();
                                pui.propFileName = propFile;
                                pui.key          = fr.nextToken().trim();
                                pui.value        = "null";
                                pui.function     = softError.concat("Delete");
                                
                                propUpdate.add(pui);
                                
                                softError = "";
                            }
                        }
                    }
                    
                // [ PropUpdate | PropSoftUpdate ] ... TFB: ???
                } else if ( statement.equalsIgnoreCase(HelperList.o_PropUpdate) ) {
                    if ( statement.equalsIgnoreCase(HelperList.o_PropSoftUpdate) )
                        softError = "soft";

                    if ( ValidateTokenCount(tokenCount, 1) ) {

                        if ( propDelimiter == null ) {
                            log.Err(75, "A PropertyFile statement must be specified before" +
                                        " propUpdate statements.");

                        } else {
                            StringTokenizer fr = new StringTokenizer(tok.nextToken(childDivider), propDelimiter);
                            if ( fr.countTokens() < 2 ) {
                                log.Err(76, "The PropUpdate statement seems not to" +
                                        " have the needed delimiter (" + propDelimiter + ") on line#" + lineNum);
                                continue;

                            } else {
                                PropUpdateInfo pui = new PropUpdateInfo();
                                pui.propFileName = propFile;
                                pui.key          = fr.nextToken().trim();
                                pui.value        = fr.nextToken().trim();
                                pui.function     = softError.concat("Update");
                                propUpdate.add(pui);
                                softError = "";
                            }
                        }
                    }

                // ReSequenceJar <jarFileName> <sequenceListFile>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ReSequenceJar) ) {
                    if ( ValidateTokenCount(tokenCount, 3) ) {
                        hte = new HTEntry(!isHelper);
                        String jarName = PrepareTokFmt3(tok.nextToken());  // Jar File Name
                        String seqList = PrepareTokFmt4(tok.nextToken());  // Perfered sequence list

                        File tempFile = new File(seqList);   // ensure the file exists
                        if ( tempFile.exists() )
                            hte.increment(k_New);

                        hte.key = jarName;
                        hte.vData = seqList;
                        reSequenceJar.add(hte);

                        // Add the Perfered sequence list to the file transfer table:

                        hte = new HTEntry(!isHelper);
                        hte.key = seqList;
                        hte.vData = seqList;
                        transferFile.put(seqList, hte);

                        // Now add the Perfered sequence list to the no restore list:

                        if ( caseSensitive )
                            noRestore.put(seqList, new HTEntry(!isHelper));
                        else
                            noRestore.put(seqList.toLowerCase(), new HTEntry(!isHelper));
                    }

                // UpdateXML [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_UpdateXML) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setUpdateXML(test4Boolean(tok.nextToken(), HelperList.o_UpdateXML));
                    
                // DupCheck [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_DupCheck) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setDupCheck(test4Boolean(tok.nextToken(), HelperList.o_DupCheck));
                    
                // ProductFileType [ XML | Properties ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_ProductFileType) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {

                        String pType = tok.nextToken();
                        
                        if ( HelperList.validateProductFileTypes(pType) ) {
                            setProductFileType(pType);
                        } else {
                            log.Err(99, "The ProductFileType statement on line#" + lineNum + ", is invalid.");
							HelperList.displayValidProductFileTypes(log);
                        }
                    }
                    
                // ProductFileName <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ProductFileName) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setProductFileName(tok.nextToken());
                    
                // ProductFileVKey <versionKey>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ProductFileVKey) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        productFileVKey = tok.nextToken();
                    
                // Validating [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_Validating) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        validating = test4Boolean(tok.nextToken(), HelperList.o_Validating );
                    
                // NameSpaceAware [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_NameSpaceAware) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setNameSpaceAware(test4Boolean(tok.nextToken(), HelperList.o_NameSpaceAware));
                    
                // AddHistory [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_AddHistory) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setAddHistory(test4Boolean(tok.nextToken(), HelperList.o_AddHistory));
                    
                // EventType <type> TFB: ???
                } else if ( statement.equalsIgnoreCase(HelperList.o_EventType) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setEventType(tok.nextToken(childDivider).trim());
                 
                // APAR <aparId>
                } else if ( statement.equalsIgnoreCase(HelperList.o_APAR) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        APAR = tok.nextToken(childDivider).trim();
                    
                // PMR <pmrId>
                } else if ( statement.equalsIgnoreCase(HelperList.o_PMR) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        PMR = tok.nextToken(childDivider);
                
                // Description <text>
                } else if ( statement.equalsIgnoreCase(HelperList.o_Description) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setDescription(tok.nextToken(childDivider));

                // EntryScript <name> [ (cmdDelimiter) [ (argDelimiter) ] ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_EntryScript) ) {
                    if ( script != null )
                        virtualScripts.add(script);

                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        script = new Script(tok.nextToken(), true, true);

                        if ( tok.hasMoreTokens() ) // check if there is a cmdDelimiter specified
                            cmdDelimiter = checkDelimiter(tok.nextToken(), lineNum, filterFile);

                        if ( tok.hasMoreTokens() )
                            argDelimiter = checkDelimiter(tok.nextToken(), lineNum, filterFile);

                    } else {
                        log.Err(77, "The EntryScript statement needs to have a name following, " + location);
                    }

                // PreScript <name> [ (cmdDelimiter) [ (argDelimiter) ] ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_PreScript) ) {
                    if ( script != null )
                        virtualScripts.add(script);

                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        script = new Script(tok.nextToken(), true, false);

                        if ( tok.hasMoreTokens() )     // check if there is a cmdDelimiter specified
                            cmdDelimiter = checkDelimiter(tok.nextToken(), lineNum, filterFile);
                        
                        if ( tok.hasMoreTokens() )
                            argDelimiter = checkDelimiter(tok.nextToken(), lineNum, filterFile);

                    } else {
                        log.Err(77, "The PreScript statement needs to have a name following, " + location);
                    }
                    
                // PostScript <scriptName> [ (cmdDelimiter) [ (argDelimiter) ] ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_PostScript) ) {
                    if ( script != null )
                        virtualScripts.add(script);

                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        script = new Script(tok.nextToken(), false, false);
                        
                        if ( tok.hasMoreTokens() )   // looking if there was a Command Delimiter Specified
                            cmdDelimiter = checkDelimiter(tok.nextToken(), lineNum, filterFile);

                        if ( tok.hasMoreTokens() )
                            argDelimiter = checkDelimiter(tok.nextToken(), lineNum, filterFile);
                        
                    } else {
                        log.Err(78, "The PostScript statement needs to have a name following, " + location);
                    }
                    
                // Cmd statement=rc [ !statement=rc ]*
                } else if ( statement.equalsIgnoreCase(HelperList.o_Cmd) ) {
                    msg = parseCmd(workLine.substring("cmd ".length()), script, false, false, null);

                    if ( msg != null )
                        log.Err(79, msg + ": line #" + lineNum + " in " + filterFile);
                    
                // Class <className> [ , arg ]* = rc
                } else if ( statement.equalsIgnoreCase(HelperList.o_Class) ) {
                    msg = parseCmd(workLine.substring("class ".length()), script, true, false, HelperList.meClass);

                    if ( msg != null )
                        log.Err(131, msg + ": line #" + lineNum + " in " + filterFile);

                // UnCmd statement=rc [ !statement=rc ]*
                } else if ( statement.equalsIgnoreCase(HelperList.o_UnCmd) ) {
                    msg = parseCmd(workLine.substring("uncmd ".length()), script, false, true, null);

                    if ( msg != null )
                        log.Err(80, msg + ": line #" + lineNum + " in " + filterFile);

                // UnClass <className> [ arg, [, arg]* ] = rc
                } else if ( statement.equalsIgnoreCase(HelperList.o_UnClass) ) {
                    msg = parseCmd(workLine.substring("unclass ".length()), script, true, true, HelperList.meClass);

                    if ( msg != null )
                        log.Err(132, msg + ": line #" + lineNum + " in " + filterFile);

                // RCmd <commandText>
                } else if ( statement.equalsIgnoreCase(HelperList.o_RCmd) ) {
                    msg = parseCmd(workLine.substring("cmd ".length()), script, false, false, HelperList.meRCmd);

                    if ( msg != null )
                        log.Err(112, msg + ": line #" + lineNum + " in " + filterFile);

                // GetNewFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_GetNewFile) ) {
                    
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        if ( getFile != null ) {
                            log.Err(89, "In processing line #" + lineNum + " in " + filterFile +
                                        ", seems a prior GetNewFile or GetReplaceFile did not" +
                                        " have a matching target.");
                            
                        } else {
                            getFile = tok.nextToken(childDivider);
                            eFixType = k_Add;
                        }
                    }
                    
                // GetReplaceFile <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_GetReplaceFile) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        if ( getFile != null  ) {
                            log.Err(84, "In processing line #" + lineNum + " in " + filterFile +
                                        ", seems a prior GetNewFile, GetReplaceFile," +
                                        " or getJarEntry did not have a matching target.");
                            
                        } else {
                            getFile = tok.nextToken(childDivider);
                            eFixType = k_Update;
                        }
                    }
                    
                // GetJarEntry <entryPath>
                } else if (statement.equalsIgnoreCase(HelperList.o_GetJarEntry)) {
                    if (ValidateTokenCount(tokenCount, 2)) {
                        if ( getFile != null ) {
                            log.Err(94, "In processing line #" + lineNum + " in " + filterFile +
                                    ", seems a prior GetNewFile, GetReplaceFile or GetJarEntry" +
                                    " did not have a matching target.");

                        } else {
                            getFile = tok.nextToken(childDivider);
                            eFixType = k_JarEntry;
                        }
                    }

                //  Target <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_Target) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        if ( getFile == null ) {
                            log.Err(90, "In processing line #" + lineNum + " in " + filterFile +
                                        ", seems we have encountered a target specification with" +
                                        " out a prior GetNewFile, GetReplaceFile or GetJarEntry.");
                            
                        } else {
                            String errMsg = parseGetFile(getFile, eFixType, tok.nextToken());
                            
                            if ( errMsg != null  )
                                log.Err(85, "In processing line #" + lineNum + " in " + filterFile + "; " + errMsg);
                            
                            getFile = null;
                        }
                    }
                    
                // JarName <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_JarName) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setJarName(tok.nextToken(childDivider).trim());
                    
                // LeadingSlash [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_LeadingSlash) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setLeadingSlash(test4Boolean(tok.nextToken(childDivider), HelperList.o_LeadingSlash));
                    
                // ParseJar [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_ParseJar) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setParseJar(test4Boolean(tok.nextToken(childDivider), HelperList.o_ParseJar));
                    
                // WarProcessing [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_WarProcessing) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setWarProcessing(test4Boolean(tok.nextToken(childDivider), HelperList.o_WarProcessing));
                // HelperLocation <path, ...>    
                } else if ( statement.equalsIgnoreCase(HelperList.o_HelperLocation) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        helperDirectories = new Vector();
                        String errMsg = processHelperSrc(tok.nextToken(childDivider), helperDirectories);

                        if ( errMsg != null )
                            log.Err(91, "In processing line #" + lineNum + " in " + filterFile + "; " + errMsg);
                    }

                // PackageInclude <path, ...>
                } else if ( statement.equalsIgnoreCase(HelperList.o_PackageInclude) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        String errMsg = processHelperSrc(tok.nextToken(childDivider), packageDirectories);
                        
                        if ( errMsg != null )
                            log.Err(88, "In processing line #" + lineNum + " in " + filterFile + "; " + errMsg);
                    }
                    
                // ForceBackup <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ForceBackup) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setForceBackup(tok.nextToken());
                    
                // CheckForClass <className>
                } else if ( statement.equalsIgnoreCase(HelperList.o_CheckForClass) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setCheck4Class(tok.nextToken(childDivider));
                    
                // OldTree <path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_OldTree) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
			String ott = tok.nextToken();
			String ot = isItADirectory(lastSlash, ott, errSB);
			setOldTree(ot);
			//                        setOldTree(isItADirectory(tok.nextToken(), errSB));
                        
                        if ( oldTree == null )
                            log.Err(98, "OldTree " + errSB);
                    }
                    
                // NewTree <path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_NewTree) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        setNewTree(isItADirectory(lastSlash, tok.nextToken(), errSB));

                        if ( newTree == null )
                            log.Err(127, "NewTree " + errSB);
                    }
                    
                // StartMsg <messageText>
                } else if ( statement.equalsIgnoreCase(HelperList.o_StartMsg) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        StringBuffer sb = new StringBuffer();
                        
                        while ( tok.hasMoreTokens() )
                            sb.append(" ").append(tok.nextToken());
                        
                        setStartMsg(sb.toString().trim());
                    }
                    
                // EndMsg <messageText>
                } else if ( statement.equalsIgnoreCase(HelperList.o_EndMsg) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        StringBuffer sb = new StringBuffer();
                        
                        while ( tok.hasMoreTokens() )
                            sb.append(" ").append(tok.nextToken());
                        
                        setEndMsg(sb.toString().trim());
                    }
                    
                // Support <path>
                } else if ( statement.equalsIgnoreCase(HelperList.o_Support) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setSupport(tok.nextToken(childDivider));
                    
                // mfClassPath <classPath>
                } else if ( statement.equalsIgnoreCase(HelperList.o_mfClassPath) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        mfClassPath = tok.nextToken(childDivider);
                    
                // XMLVersion <version>
                } else if ( statement.equalsIgnoreCase(HelperList.o_XMLVersion) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        XMLVersion = tok.nextToken(childDivider);
                    
                // AffectedComponents <component, ...>
                } else if ( statement.equalsIgnoreCase(HelperList.o_AffectedComponents) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        while (tok.hasMoreTokens())
                            setAffectedComponents(tok.nextToken(","));
                    }
                    
                // TargetMsg { <messageText>
                // messageText ...
                // }

                } else if ( statement.equalsIgnoreCase(HelperList.o_TargetMsg) ) {
                    
                    StringBuffer sb = new StringBuffer();
                    String temp;
                    int endPos;
                    int startPos = workLine.indexOf("{");
                    
                    while ( startPos == -1 ) {            // find the starting position
                        workLine = hc.getNextLogicalLine(lineNumber, filterFile);
                        
                        if ( workLine == null )
                            startPos = -2;
                        else
                            startPos = workLine.indexOf("{");
                    }
                    
                    startPos++; // to bump around the brace
                    
                    // now we must blank out the "Targetmsg {" part so it looks like the other lines
                    
                    char[] ca = workLine.toCharArray();
                    
                    for ( int i = 0; i < startPos; i++ )
                        ca[i] = ' ';
                    
                    workLine = new String(ca);
                    
                    while ( workLine != null ) {  // now find the end of the line
                        
                        if ( (endPos = workLine.indexOf("}")) == -1 ) {
                            
                            if ( workLine.length() >= startPos ) {
                                if ( (workLine.substring(0, startPos).trim().length()) == 0 )
                                    temp = workLine.substring(startPos);   // we have no data ahead of the startpos
                                else
                                    temp = workLine.trim();  // take it all, so we don't lose any thing
                                
                            } else {   // here the line is shorter than the startPos
                                temp = workLine.trim();
                            }
                            
                        } else {  // here we have the end
                            
                            if ( workLine.length() > startPos ) {
                                
                                if ( (workLine.substring(0, startPos).trim().length()) == 0 )
                                    temp = workLine.substring(startPos, endPos);   // we have no data ahead of the startpos
                                else
                                    temp = workLine.substring(startPos, endPos ).trim();
                                
                            } else {
                                temp = workLine.substring(0, endPos).trim();
                            }
                        }
                        
                        if ( temp.length() == 0 )        // if its a blank line we must add a blank
                            sb.append(" ").append("|");
                        else
                            sb.append(temp).append("|");
                        
                        if ( endPos == -1 )
                            workLine = hc.getNextLogicalLine(lineNumber, filterFile);
                        else
                            workLine = null;
                    }
                    
                    setTargetMsg(sb.toString());
                    
                // VerifyNewJar <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_VerifyNewJar) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setVerifyNewJar(test4Boolean(tok.nextToken(), HelperList.o_VerifyNewJar));
                    
                // UpdateZipEntry <entry>
                } else if ( statement.equalsIgnoreCase(HelperList.o_UpdateZipEntry) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        String zipPackage = tok.nextToken(childDivider).trim();
                        
                        if ( zipPackage.indexOf("->") == -1 ) {
                            log.Err(111, "In processing line #" + lineNum + " in " + filterFile +
                                         "; following the UpdateZipEntry should be a zip/jar" +
                                         " filenam->packageName.");
                        } else {
                            zEUI = new zipEntryUpdateInfo(zipPackage);
                            setZipEntryUpdates(zEUI);
                        }
                    }
                    
                // ZipCommand <commentText>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ZipComment) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        String zipComment = tok.nextToken(childDivider).trim();
                        
                        if ( zEUI == null ) {
                            log.Err(113, "In processing line #" + lineNum + " in " + filterFile +
                                         "; prior to specifing a zipComment, a UpdateZipEntry" +
                                         " must be specified.");
                        } else {
                            if ( zEUI.zipComment == null )
                                zEUI.zipComment = zipComment;
                            else
                                log.Err(114, "In processing line #" + lineNum + " in " + filterFile +
                                             "; A zipComment has already been specified for" +
                                             " the UpdateZipEntry, the entry is ignored.");
                        }
                    }
                    
                // ZipExtra <bytes>
                } else if ( statement.equalsIgnoreCase(HelperList.o_ZipExtra) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        String zipExtra = tok.nextToken(childDivider).trim();

                        if ( zEUI == null ) {
                            log.Err(115, "In processing line #" + lineNum + " in " + filterFile +
                                    "; prior to specifing a zipEntry, a UpdateZipEntry must be specified.");
                        } else {
                            if ( zEUI.zipExtra == null ) {
                                zEUI.zipExtra = zipExtra.getBytes();
                            } else {
                                log.Err(116, "In processing line #" + lineNum + " in " + filterFile +
                                        "; A zipEntry has already been specified for the" +
                                        " UpdateZipEntry, the entry is ignored.");
                            }
                        }
                    }

                // CollectPermissions [ true | false ]
                } else if ( statement.equalsIgnoreCase(HelperList.o_CollectPermissions) ) {
                    if ( ValidateTokenCount(tokenCount, 2) )
                        setCollectPermissions(test4Boolean(tok.nextToken(childDivider), HelperList.o_CollectPermissions));
                    break;
                    
                // SpareProduct <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_SpareProduct) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        while ( tok.hasMoreTokens() ) {
                            String tFileName = HelperList.ResolveMacro("?<>", tok.nextToken(",").trim(), lineNum, filterFile);

                            if ( canRead(tFileName, errSB) == null ) {
                                log.Err(129,"In processing line #" + lineNum + " in " + filterFile + "; " + errSB) ;

                            } else {
                                if ( spareProducts == null )
                                    spareProducts = new Vector();

                                spareProducts.add(tFileName);
                            }
                        }
                    }

                // RestoreOnly <fileName>
                } else if ( statement.equalsIgnoreCase(HelperList.o_RestoreOnly) ) {
                    
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        HTEntry lhte = new HTEntry(!isHelper);
                        
                        if ( tok.hasMoreTokens() ) {
                            lhte.key = HelperList.ResolveMacro("?<>", tok.nextToken(",").trim(), lineNum, filterFile);
                            
                            if ( canRead(lhte.key, errSB) == null )
                                log.Err(133,"In processing line #" + lineNum + " in " + filterFile + "; " +  errSB) ;
                            
                            if ( tok.hasMoreTokens() ) {  // and this would be the target
                                lhte.vData = HelperList.ResolveMacro("?<>", tok.nextToken(",").trim(), lineNum, filterFile);
                                
                                if ( tok.hasMoreTokens() )  // and this would be the requiredVersion
                                    lhte.requiredVersion = tok.nextToken(",").trim();
                                
                                restoreOnly.add(lhte);
                                
                            } else {
                                log.Err(134, "In processing line #" + lineNum + " in " + filterFile +
                                        "; No Target was specified for RestoreOnly.");
                            }
                        }
                    }
                    
                // FrequencyUpdate <count>
                } else if ( statement.equalsIgnoreCase(HelperList.o_FrequencyUpdate) ) {
                    if ( ValidateTokenCount(tokenCount, 2) ) {
                        setFrequencyUpdate(hc.test4numerics(tok.nextToken(), 5, errSB));
                        
                        if ( errSB.length() > 0 ) {
                            log.Err(55, location + " FrequencyUpdate value, " + errSB.toString());
                            returnValue = false;
                        }
                    }

                } else {
                    log.Err(37, "processing line #" + lineNum + " within " + filterFile +
                                ", token #1 (" + statement + ") is not a valid statement.");
                    returnValue = false;
                }
            }
        }

        log.Both(lineNum + " lines processed from " + filterFile );
    
        if ( script != null )
            virtualScripts.add(script);
    
        if ( debug ) {
            deleteBeforeWrite.display();

            addOnlyFiles.display();
            replaceOnlyFiles.display();

            perInstance.display();

            asInstallable.display();
            asApplication.display();
            asInstalled.display();

            log.Both("Debug - Content of chmod Table");

            Iterator chmods = chmod.values().iterator();
            while ( chmods.hasNext() ) {
                String key = (String) chmods.next();
                log.Both("  chmod key = (" + key + ")");
            }
        }
    
        return true;
    }

    protected boolean ValidateTokenCount(int tokenCount, int needed)
    {
        if ( needed > tokenCount ) {
            log.Err(36, "processing line #" + lineNum +
                        " within " + filterFileName +
                        ", this line has less than " + needed + " tokens.");

            return false;
        }

        return true;
    }

    // Replace all slashes (in both directions) to asterisks.
    //
    // Make sure there is an asterisk at the beginning of the
    // result token.
    //
    // If not case sensitive, convert to lower case.

    protected String PrepareTokFmt1(String token)
    {
        token = token.replace('\\', '*');
        token = token.replace('/' , '*');

        if ( !token.startsWith("*") )
            token = "*" + token;

        if ( !caseSensitive )
            token = token.toLowerCase();
    
        return token;
    }

    // Replace all slashes (in both directions) to asterisks.
    //
    // Make sure there is an asterisk at the beginning and at
    // the end of the result token.
    //
    // If not case sensitive, convert to lower case.

    protected String PrepareTokFmt2(String token)
    {
        token = token.replace('\\', '*');
        token = token.replace('/' , '*');

        if ( !token.startsWith("*") )
            token = "*" + token;

        if ( !token.endsWith("*") )
            token += "*";

        if ( !caseSensitive )
            token = token.toLowerCase();

        return token;
    }

    // Replace all slashes (in both directions) to forward slashes.
    // Make sure there is a forward slash at the beginning of the
    // result token.
    //
    // If not case sensitive, convert to lower case.

    protected String PrepareTokFmt3(String token)
    {
        token = token.replace('\\', '/');

        if ( !token.startsWith("/") )
            token = "/" + token;

        if ( !caseSensitive )
            token = token.toLowerCase();
        
        return token;
    }

    // Replace all slashes (in both directions) to forward slashes.
    //
    // If not case sensitive, convert to lower case.

    protected String PrepareTokFmt4(String token)
    {
        token = token.replace('\\', '/');
    
        if ( !caseSensitive )
            token = token.toLowerCase();

        return token;
    }

    // The delimiter should be a single character within parentheses:
    // For example: (#)

    protected String checkDelimiter(String token, int lineNum, String filterName)
    {
        String temp = token.trim();

        if ( temp.startsWith("(") && temp.endsWith(")") ) {
            temp = token.replace('(', ' ').replace(')', ' ').trim();

            if ( temp.length() != 1 )
                log.Err(82, "The command delimiter specification, " + token +
                            ", may only be one character in length," +
                            " on line " + lineNum + " in " + filterName);
        } else {
            log.Err(83, "The command delimiter specification, " + token +
                        ", must be enclosed in parenthesis," +
                        " on line " + lineNum + " in " + filterName);
        }

        return temp;
    }

    protected String[] resolveComponentLimitations(String spec, String location)
    {
        String work = spec.trim().toLowerCase();

        if ( !work.startsWith("forcomponent") ) {
            log.Err(89, location + ", was expecting forComponent(), found " + spec);
            return new String[0];
        }

        int openParen = work.indexOf("(");

        int closeParen;

        if ( openParen > 0 )
            closeParen = work.indexOf(")", openParen);
        else
            closeParen = -1;

        if ( (openParen == -1) || (closeParen == -1) ) {
            log.Err(90, location + ", forComponent specification " +
                        "has malformed parentheses: " + spec);
            return new String[0];
        }

        work = work.substring(openParen + 1, closeParen);
        StringTokenizer toks = new StringTokenizer(work, ",");

        String[] array = new String[toks.countTokens()];
    
        for ( int i = 0; toks.hasMoreTokens(); i++)
            array[i] = toks.nextToken().trim();

        return array;
    }

    // Answer null if parsing completed with no errors.
    // Otherwise, the return value is an error message.

    // The syntax is:
    //   <command>=<returnCode>(<delimiter><command>=<returnCode>)*
    //
    // <command> is a shell command.
    // <returnCode> is a numeric return code, or a '?'.
    //
    // The command, when executed, must return the specified
    // return code, or the command is taken to have failed.
    //
    // For example:
    //   Copy fileA fileB = 0; Copy FileC FileD = 0

    // Parse the line into the command vector:
    //
    //    <command>=<returnCode><isRequired>
    //    ...

    // For example
    // EntryScript unpack ! ,
    // Cmd cd fred = 0 ! dir = 0 ! cd .. = 0
    // Class unpack, arg1, arg2, arg3 = 3

    protected String parseCmd(String line,
                              Script script,
                              boolean isClass, boolean isUndo,
                              String cmdType)
    {
        // log.Both("Command line: [ " + line + " ]");

        StringTokenizer toks = new StringTokenizer(line.trim(), getCmdDelimiter());

        String errorMessage = null;

        while ( toks.hasMoreTokens() && (errorMessage == null) ) {
            String nextCmdText = toks.nextToken().trim();
            errorMessage = parseNextCmd(nextCmdText, script, isClass, isUndo, cmdType);
        }

        return errorMessage;
    }

    protected String parseNextCmd(String cmdText,
                                  Script script,
                                  boolean isClass, boolean isUndo,
                                  String cmdType)
    {
        // log.Both("Next Command: [ " + cmdText + " ]");

        int equalsOffset = cmdText.lastIndexOf("=");

        if ( equalsOffset < 1 )
            return ( "Command (" + cmdText + ") has no return code (\"= code\") specified." );

        String returnCode = cmdText.substring(equalsOffset + 1).trim();
        String cmd = cmdText.substring(0, equalsOffset).trim();

        // log.Both("Return Code: [ " + returnCode + " ]");
        // log.Both("Base Command: [ " + cmd + " ]");

        if ( !validateReturnCode(returnCode) ) {
            return ( "Command (" + cmd + ")" +
                     " has an empty or non-numeric return code (" + returnCode + ")." );
        }

        Vector cmdArgs;

        if ( isClass ) {
            cmdArgs = new Vector();

            StringTokenizer argToks = new StringTokenizer(cmd, getArgDelimiter());
            cmd = null;

            while ( argToks.hasMoreTokens() ) {
                String nextArg = argToks.nextToken().trim();

                if ( cmd == null ) {
                    cmd = nextArg;
                } else {
                    cmdArgs.add(nextArg);
                    // log.Both("  >> Arg: [ " + nextArg + " ]");
                }
            }

        } else {
            cmdArgs = null;
        }

        cmd += "=" + returnCode;
        if ( cmdType != null )
            cmd += cmdType;

        if ( isUndo ) {
            script.uncmds.add(cmd);
            if ( isClass )
                script.uncmdArgs.add(cmdArgs);

        } else {
            script.cmds.add(cmd);
            if ( isClass )
                script.cmdArgs.add(cmdArgs);
        }

        return null;
    }

    protected boolean validateReturnCode(String returnCode)
    {
        if ( returnCode.equals("?") )
            return true;

        // Make sure its numeric

        char[] codeDigits = returnCode.toCharArray();

        if ( codeDigits.length == 0 )
            return false;

        for ( int charNo = 0; charNo < codeDigits.length; charNo++ ) {
            if ( !Character.isDigit(codeDigits[charNo]) )
                return false;
        }

        return true;
    }

    // The syntax is:
    //     fileSpec;filespec

    protected String parseGetFile(String list, int type, String target)
    {
        StringBuffer errorBuffer = new StringBuffer();  // to hold an error message

        StringTokenizer toks = new StringTokenizer(list.trim(), ";");
    
        while ( toks.hasMoreTokens() ) {
            String token = toks.nextToken().trim();
            String abName = null;

            log.Both("Processing " + token);

            if ( type == k_JarEntry ) {

                abName = isItADirectory(null, token, errorBuffer);
		System.out.println("abName: " + abName);

                if ( abName == null )   // Not a directory
                    return errorBuffer.toString();

                HashMap files = new HashMap();
                HashMap dirs = new HashMap();
                boolean recurse = true;

                hc.FindFiles(abName + slash + "*", recurse, files, dirs, caseSensitive, debug);

                log.Both(hc.FmtNum(files.size(), 0, 10) + " files in " +
                         hc.FmtNum(dirs.size(),  0,  0) + " directories.");

                Iterator eNum = files.values().iterator();
                while ( eNum.hasNext() ) {
                    File pfile = (File) eNum.next();

                    // Backup one level so it may be included in the path.
                    String[] nameParts = new String[hc.k_elCount];
                    hc.ParseFileSpec(abName, nameParts, debug);

                    HTEntry hte   = new HTEntry(!isHelper);
                    hte.key       = pfile.getAbsolutePath();
                    hte.vData     = target;
                    hte.eFixType  = type;
                    hte.prefixLen = nameParts[hc.k_drive].length() + nameParts[hc.k_path].length();

                    eFixFiles.add(hte);
                }
            
            } else {  // Its a Flat File of some sort
                abName = canRead(token, errorBuffer);
                
                if ( abName == null )
                    return errorBuffer.toString();  // this contains an error message
                
                HTEntry hte  = new HTEntry(!isHelper);
                hte.eFixType = type;
                hte.key      = abName;
                
                // here we need to determine what the target is, a Directory or a File Name
                String adjTarget;
                
                String[] tParts = new String[hc.k_elCount];
                hc.ParseFileSpec(target, tParts, debug);
                
                // if nameParts[] == "" then it was a directory that was provided:

                if ( tParts[hc.k_name].length() == 0 ) {
                    // so here we need to strip the name from the source file:

                    String[] sParts = new String[hc.k_elCount];
                    
                    hc.ParseFileSpec(abName, sParts, debug);    // and append it to the target directory
                    adjTarget = tParts[hc.k_drive] + tParts[hc.k_path] + sParts[hc.k_fullname];
                    
                } else {
                    adjTarget = target;  // this must be a jar entry
                }
            
                hte.vData = adjTarget;
                eFixFiles.add(hte);
            }
        }
    
        return null;  // returning null indicates no error messages
    }

    protected String canRead(String fileName, StringBuffer msg)
    {
        File tempFile = new File(fileName).getAbsoluteFile();
        
        fileName = tempFile.toString();
        
        if ( !tempFile.exists() ) {
            msg.append("The file [" + fileName + "] does not exist.");
            fileName = null;

        } else if ( !tempFile.canRead() ) {
            msg.append("The file [" + fileName + "] is not readable.");
            fileName = null;
        }
    
        return fileName;
    }

    protected String isItADirectory(String path, String fileName, StringBuffer msg)
    {
	File tempFile = null;
	if (path == null) {
	    tempFile = new File(fileName).getAbsoluteFile();
	    fileName = tempFile.toString();
	}
	else { 
	    tempFile = new File(path + File.separator + fileName);
	    fileName = tempFile.toString();
	}

        if ( !tempFile.exists() ) {
            msg.append("The file [" + fileName + "] does not exist.");
            fileName = null;

        } else if ( !tempFile.isDirectory() ) {
            msg.append("The file [" + fileName + "] is not a directory.");
            fileName = null;
        }

        return fileName;
    }

    protected boolean test4Boolean(String value, String errorMessage)
    {
        boolean state;

        value = value.trim();
    
        if ( value.equalsIgnoreCase("true") ||
             value.equalsIgnoreCase("yes")  ||
             value.equals("1")              ||
             value.equals("on") ) {

            state = true;

        } else if ( value.equalsIgnoreCase("false") ||
                    value.equalsIgnoreCase("no") ||
                    value.equals("0") ||
                    value.equals("off") ) {

            state = false;

        } else {
            log.Err(22, "The boolean value (" + value + ") is invalid for " + errorMessage);

            state = false;
        }
    
        return state;
    }

    protected String bool2String(boolean value)
    {
        if ( value )
            return HelperList.meTrue;
        else
            return HelperList.meFalse;
    }

    // If we pass back a null then everythin is OK, otherwise we'll
    // pass back an error message.
    
    protected String processHelperSrc(String list, Vector vector)
    {
        StringBuffer errorBuffer = new StringBuffer();

        StringTokenizer toks = new StringTokenizer(list.trim(), ";");

        while ( toks.hasMoreTokens() ) {
            String token = toks.nextToken().trim();
            String abName = null;

            if ( token.toLowerCase().startsWith("jar:") ) {
                abName = token;
            } else {
                abName = isItADirectory(null, HelperList.ResolveMacro("?<>", token, lineNum, filterFileName ), errorBuffer);

                if ( abName == null )
                    return errorBuffer.toString();
            }

            vector.add(abName);
        }
    
        return null;
    }
}
