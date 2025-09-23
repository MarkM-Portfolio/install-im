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

package com.ibm.websphere.update.delta.earutils;

/*
 *  @ (#) EARCmd.java
 *
 *  Support class to expand and collapse EAR files.
 *
 *  @author     venkataraman
 *  @created    November 20, 2002
 */

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.ptf.*;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *  
 */
public class EARCmd
{
    public final static String pgmVersion = "1.4" ;
    public final static String pgmUpdate = "4/29/04" ;

    public static final boolean isWindows =
        com.ibm.websphere.update.util.PlatformUtils.isWindows();

    public static final boolean isWindowsNT =
        ( isWindows && PlatformUtils.isWinNT() );
    public static final boolean isISeries =
        ( PlatformUtils.isISeries() );

    // Answer a new jar command processor on the specified java path
    // and on the specified messages and errors buffers.
    //
    // The java path must be absolutely specified.
    //
    // Messages from JAR processing are written to the messages
    // buffer.  Error messages from JAR processing are written to
    // the errors buffer.

    public EARCmd(String installPath, StringBuffer messages, StringBuffer errors)
    {
        this.installPath = installPath;

        this.messages = messages;
        this.errors = errors;
    }
    
    protected String installPath;

    /**
	 * @return  the installPath
	 * @uml.property  name="installPath"
	 */
    public String getInstallPath()
    {
        return this.installPath;
    }

    protected StringBuffer messages;

    /**
	 * @return  the messages
	 * @uml.property  name="messages"
	 */
    public StringBuffer getMessages()
    {
        return this.messages;
    }

    public void log(String text)
    {
        this.getMessages().append(text + EARActor.lineSeparator);
    }

    public void log(String text1, Object text2)
    {
        this.getMessages().append(text1 + text2 + EARActor.lineSeparator);
    }

    protected StringBuffer errors;

    /**
	 * @return  the errors
	 * @uml.property  name="errors"
	 */
    public StringBuffer getErrors()
    {
        return this.errors;
    }

    public void logError(String text)
    {
        this.getErrors().append(text + EARActor.lineSeparator);
    }

    public void logError(String text1, Object text2)
    {
        this.getErrors().append(text1 + text2 + EARActor.lineSeparator);
    }

    // iterate over lists of files before and after, delete if not present before and
    // has suffix .tmp
    
    protected void cleanFiles(File[] filesBefore, File[] filesAfter)
    {
        if ( (filesBefore == null) || (filesAfter == null) )
            return;

        HashMap beforeMap = new HashMap();

        for ( int beforeNo = 0; beforeNo < filesBefore.length; beforeNo++ ) {
            File nextBeforeFile = filesBefore[beforeNo];

            String nextBeforePath;

            try { 
                nextBeforePath = nextBeforeFile.getCanonicalPath();
            } catch ( IOException e ) {
                nextBeforePath = nextBeforeFile.getName();
            }

            beforeMap.put(nextBeforePath, nextBeforeFile);
        }

        for ( int afterNo = 0; afterNo < filesAfter.length; afterNo++ ) {
            File nextAfterFile = filesAfter[afterNo];
            String nextAfterPath;

            try {
                nextAfterPath = nextAfterFile.getCanonicalPath();
            } catch ( IOException e ) {
                nextAfterPath = nextAfterFile.getName();
            }

            if ( beforeMap.get(nextAfterPath) != null)
                nextAfterFile.deleteOnExit();
        }

        // catch ( IOException e )
    }

    public static final String TMP_DIR_PROPERTY_NAME = "java.io.tmpdir";

    // Provides array of files in the java.io.tmpdir temp directory.

    protected File[] listCurrentTempFiles()
    {
        return listFiles( System.getProperty(TMP_DIR_PROPERTY_NAME) );
    }

    protected File[] listFiles(String dirName)
    {
        if ( dirName == null )
            return null;

        File dir = new File(dirName);
        if ( !dir.exists() )
            return null;

        return dir.listFiles();
    }

    protected boolean setEarTempDir = false;
    protected String earTempDir = null;

    protected String getEARTempDir()
    {
        if ( !setEarTempDir ) {
            setEarTempDir = true;
            earTempDir = System.getProperty(Extractor.earTmpPropertyName);
        }

        return earTempDir;
    }

    protected void deleteEarTempFiles()
    {
        deleteOnExitRecurse( getEARTempDir() );
    }

    // Use a static for the archive index ...
    //
    // Want to keep generating new archive temp dirs,
    // across multiple EARCmd operations.
    //
    // Will use the index on top of the ear temp dir, which
    // is generated as a unique dir.

    public static final String tmpForCommonArchive = "_arch";
    public static int tmpForCommonArchiveIndex = 1;

    protected static final int maxCommonArchiveRetries = 1000;

    protected String ensureCommonArchiveTemp()
    {
        int retriesLeft = maxCommonArchiveRetries;
        String archiveTmp = null;

        while ( (archiveTmp == null) && (retriesLeft > 0) ) {
            retriesLeft--;

            tmpForCommonArchiveIndex++;
            if ( tmpForCommonArchiveIndex < 0 )
                tmpForCommonArchiveIndex = 1;

            String candidateArchiveTmp = getEARArchiveTempDir();

            File nextFile = new File(candidateArchiveTmp);

            if ( !nextFile.exists() && nextFile.mkdir() )
                archiveTmp = candidateArchiveTmp;
        }

        return archiveTmp;
    }

    protected String getEARArchiveTempDir()
    {
        String earTmpDir = getEARTempDir();

        if ( earTmpDir == null )
            return null;

        return
            earTmpDir +
            tmpForCommonArchive +
            Integer.toString(tmpForCommonArchiveIndex);
    }

    protected String[] getTmpOverrides(String tmpDirName)
    {
        return new String[] {
            "TMPDIR=" + tmpDirName,
            "TEMP="   + tmpDirName,
            "TMP="    + tmpDirName
        };
    }

    // 'getResourcePath' assumes that the receiver's class is stored
    // in a JAR file.

    protected String getResourcePath()
    {
    	String puiInstallDir = System.getProperty("pui_install_root");
    	if (puiInstallDir != null) {
    		return puiInstallDir;
    	}
 
    	
        String resourceName = getClass().getName();
        log("Retrieving resource path for: ", resourceName);

        resourceName = resourceName.replace('.', '/');
        resourceName += ".class";
        log("Target resource name: ", resourceName);

        URL resourceURL = getClass().getClassLoader().getResource(resourceName);

        String encodedURLText = resourceURL.getPath();
        log("Encoded resource path: ", encodedURLText);

        String decodedURLText  = URLDecoder.decode(encodedURLText);
        log("Decoded resource path: ", decodedURLText);

        int firstSlashLoc = decodedURLText.indexOf("/");
        int jarIndicatorLoc = decodedURLText.indexOf("!");

        if ( jarIndicatorLoc == -1 )
            jarIndicatorLoc = decodedURLText.length();

        String resourcePath = decodedURLText.substring(firstSlashLoc + 1, jarIndicatorLoc);

        int lastSlashLoc = resourcePath.lastIndexOf("/");
        resourcePath = resourcePath.substring(0, lastSlashLoc);
        resourcePath = correctPath(resourcePath);

        log("Resource path: ", resourcePath);

        return resourcePath;
    }

    protected static final String slashText    = System.getProperty("file.separator");
    protected static final char   slashChar    = slashText.charAt(0);

    protected static final String urlSlashText = "/";
    protected static final char   urlSlashChar = '/';

    protected String correctPath(String path)
    {
        return ( ( path == null ) ? null : path.replace(urlSlashChar, slashChar) );
    }

    // Perform an uncompression of the specified archive
    // to the specified directory.
    //
    // The initial contents of the directory are removed.
    //
    // Answer true or false telling if the operation was successful.

    protected String[] correctCmd(String[] cmd)
    {
        if ( !isWindows ) {
            log("No launcher.exe: Not a windows platform.");
            return cmd;

        } else if ( isWindowsNT ) {
            log("No launcher.exe: Windows NT.");
            return cmd;

        } else {
            log("Windows (not windows NT): Adding launcher.exe");

            String fullLauncherPath =
                getResourcePath() + File.separator +
                "earLauncher"     + File.separator +
                "launcher.exe";

            log("Full Launcher Path: ", fullLauncherPath);

            String[] updateCmd = new String[cmd.length + 1];

            updateCmd[0] = enquote(fullLauncherPath);

            for ( int argNo = 0; argNo < cmd.length; argNo++ )
                updateCmd[argNo + 1] = cmd[argNo];

            return updateCmd;
        }
    }

    public boolean uncompress(String compressedPath,
                              String uncompressedPath)
    {
        log("Expand:");
        log("    Source EAR (Archive)  : ", compressedPath);
        log("    Target EAR (Directory): ", uncompressedPath);

        log("    EAR Temp Dir          : ", getEARTempDir());

        String archiveTmp = ensureCommonArchiveTemp();

        if ( archiveTmp == null ) {
            logError("Expand: Unable to ensure common archive temp.");
            return false;
        } else {
            log("    Archive Temp Dir      : ", archiveTmp);
        }

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getEARScriptPath()),
            "-ear",            enquote(compressedPath),
            "-operationDir",   enquote(uncompressedPath),
            "-operation",      "expand",
            "-expansionFlags", "war" 
        };

        cmd = correctCmd(cmd);
        if ( cmd == null )
            return false;

        int resultCode;

        try {
            Vector results = new Vector();
            Vector logResults = new Vector();

            try {
                resultCode = exec.Execute(cmd,
                                          getTmpOverrides(archiveTmp),
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results,
                                          logResults);

            } finally {
                logResults(results, "Result: ");
                logResults(logResults, null);

                deleteEarTempFiles();
                deleteOnExitRecurse(archiveTmp);
            }

        } catch ( Exception ex ) {
            logError("Expand: Failed With Exception");
            logError("    Source EAR (Archive)  : ", compressedPath);
            logError("    Target EAR (Directory): ", uncompressedPath);

            logError("Exception: ", ex);

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
            logError("Expand: Failed by Result Code");
            logError("    Source EAR (Archive)  : " + compressedPath);
            logError("    Target EAR (Directory): " + uncompressedPath);

            logError("Result Code: " + resultCode);

            return false;

        } else {
            log("Expand: OK");
            return true;
        }
    }

    // Recurse down a directory path, flagging directories and files for
    // deletion.
    
    public void deleteOnExitRecurse(String fileName)
    {
        if ( fileName == null )
            return;

        deleteOnExitRecurseAbs( (new File(fileName)).getAbsolutePath() );
    }

    protected void deleteOnExitRecurseAbs(String filePath)
    {
        File fileToDelete = new File(filePath);
        fileToDelete.deleteOnExit();

        if ( fileToDelete.isDirectory() ) {
            String[] files = fileToDelete.list();

            if ( files != null ) {
                for ( int fileNo = 0; fileNo < files.length; fileNo++)
                    deleteOnExitRecurse(filePath + File.separator + files[fileNo]);
            }
        }
    }

    // Perform a compression of the specified archive
    // from the specified directory.
    //
    // The specified archive is modified.
    //
    // Answer true or false telling if the operation was successful.

    public boolean compress(String compressedPath, String uncompressedPath)
    {
        log("Collapse:");
        log("    Source EAR (Directory): ", uncompressedPath);
        log("    Target EAR (Archive)  : ", compressedPath);


        log("    EAR Temp Dir          : ", getEARTempDir());

        String archiveTmp = ensureCommonArchiveTemp();

        if ( archiveTmp == null ) {
            logError("Expand: Unable to ensure common archive temp.");
            return false;
        } else {
            log("    Archive Temp Dir      : ", archiveTmp);
        }

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getEARScriptPath()),
            "-ear",          enquote(compressedPath),
            "-operationDir", enquote(uncompressedPath),
            "-operation",    "collapse"
        };
        
        cmd = correctCmd(cmd);
        if ( cmd == null )
            return false;

        int resultCode;

        try {
            Vector results = new Vector();
            Vector logResults = new Vector();

            try {
                resultCode = exec.Execute(cmd,
                                          getTmpOverrides(archiveTmp),
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results,
                                          logResults);

            } finally {
                logResults(results, "Result: ");
                logResults(logResults, null);

                deleteEarTempFiles();
                deleteOnExitRecurse(archiveTmp);
            }

        } catch ( Exception ex ) {
            logError("Collapse: Failed With Exception");
            logError("    Source EAR (Directory): " + uncompressedPath);
            logError("    Target EAR (Archive)  : " + compressedPath);

            logError("Exception: " + ex);

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
            logError("Collapse: Failed by Result Code");
            logError("    Source EAR (Directory): " + uncompressedPath);
            logError("    Target EAR (Archive)  : " + compressedPath);

            logError("Result Code: " + resultCode);

            return false;

        } else {
            log("Collapse: OK");
            return true;
        }
    }

    /*
     * escapeBackslash
     *
     * helper function used to escape the all occurances of the
     * backslash character in a string
     *
     * param cmd: the string we want to modify
     * returns: the modified string   
     */

    public static final char escapeChar = '\\';

    public String escapeBackslash(String cmd)
    {
        StringBuffer cmdBuffer = new StringBuffer(cmd);
        Vector slashOffsets = new Vector();

        int cmdLength = cmdBuffer.length(); 

        for ( int charNo = 0; charNo < cmdLength; charNo++ ) {
            if ( cmdBuffer.charAt(charNo) == escapeChar )
                slashOffsets.add( new Integer(charNo) );
        }

        for ( int lastSlashNo = slashOffsets.size(); lastSlashNo > 0; lastSlashNo-- ) {
            Integer nextOffset = (Integer) slashOffsets.elementAt(lastSlashNo - 1);
            cmdBuffer.insert(nextOffset.intValue() + 1, escapeChar);
        }

        return ( cmdBuffer.toString() );
    }

    public boolean webuiPluginProcess(String earDir)
    {
        log("Webui Plugin-Processing:");
        log("    Raw Source EAR (Directory): ", earDir);

        String correctedEarDir = earDir;

        correctedEarDir = correctedEarDir.replace('\\', File.separatorChar); 
        correctedEarDir = correctedEarDir.replace('/',  File.separatorChar); 

        int endIndex = correctedEarDir.lastIndexOf(File.separatorChar);  
        correctedEarDir = correctedEarDir.substring(0, endIndex);

        log("    Source EAR (Directory)    : ", correctedEarDir);

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        // The plugin processing script does not accept an argument
        // to specify the target directory; that argument is passed
        // in as the 'WAS_PLPR_ROOT' environment variable.

        String[] envArray = new String[] {
            "WAS_PLPR_ROOT=" + correctedEarDir
        };

        String pluginCmd =
            getInstallPath() + File.separator +
            "bin" + File.separator + "PluginProcessor";

        if ( isWindows )
            pluginCmd += ".bat";
        else
            pluginCmd += ".sh";

        String[] cmdArray = new String[] {
            enquote(pluginCmd),
            "-restore"
        };

        cmdArray = correctCmd(cmdArray);
        if ( cmdArray == null )
            return false;

        int resultCode;

        try {
            Vector results = new Vector();
            Vector logResults = new Vector();

            try {
                resultCode = exec.Execute(cmdArray, envArray,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results,
                                          logResults);

            } finally {
                logResults(results, "Result: ");
                logResults(logResults, null);
            }

        } catch ( Exception ex ) {
            logError("Webui Plugin-Processor: Failed With Exception");
            logError("    Raw Source EAR (Directory): ", earDir);
            logError("    Source EAR (Directory)    : ", correctedEarDir);

            logError("Exception: " + ex);

            return false;
        }

        // TBD: Need the result codes and their meanings.

        log("Result Code: " + resultCode);
        return true;
    }



    protected void logResults(Vector results, String prefix)
    {
        int resultLines = results.size();

        for ( int lineNo = 0; lineNo < resultLines; lineNo++ ) {
            String nextLine = (String) results.elementAt(lineNo);

            if ( prefix != null )
                log(prefix, nextLine);
            else
                log(nextLine);
        }
    }

    public static final String BIN_DIRECTORY = "bin";

    public static final String WINDOWS_EXPANDER_SCRIPT = "EARExpander.bat" ;
    public static final String UNIX_EXPANDER_SCRIPT = "EARExpander.sh" ;
    public static final String iSERIES_EXPANDER_SCRIPT = "EARExpander" ;

    public String getEARScriptPath()
    {
        String binPath = getInstallPath() + File.separator + BIN_DIRECTORY;

        String cmdPath;

        if ( isWindows )
            cmdPath = binPath + File.separator + WINDOWS_EXPANDER_SCRIPT;
        else if (isISeries) {
            String wasProdHome = WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME);
            cmdPath = wasProdHome + File.separator+ BIN_DIRECTORY + File.separator + iSERIES_EXPANDER_SCRIPT;
        }
        else
            cmdPath = binPath + File.separator + UNIX_EXPANDER_SCRIPT;

        log("EAR Processing Script Path: ", cmdPath);

        return cmdPath;
    }

    public String getCopyCommand()
    {
        return ( isWindows ? "COPY" : "cp" );
    }

    public boolean copyFile(String sourceFile , String targetFile)
    {
        log("Copy:");
        log("    Source File: ", sourceFile);
        log("    Target File: ", targetFile);

        // When doing a straight copy (w/o the launcher),
        // the command adjustment is needed.
        //
        // Otherwise, don't do the command adjustment.

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd;

        if ( isWindows ) {
            cmd = new String[] {
                "CMD.EXE",
                "/C",
                getCopyCommand(),
                enquote(sourceFile),
                enquote(targetFile)
            };
        } else {
            cmd = new String[] {
                getCopyCommand(),
                enquote(sourceFile),
                enquote(targetFile)
            };
        }

        cmd = correctCmd(cmd);
        if ( cmd == null )
            return false;

        int resultCode;

        try {
            Vector results = new Vector();
            Vector logResults = new Vector();

            try {
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results,
                                          logResults);

            } finally {
                logResults(results, "Result: ");
                logResults(logResults, null);
            }

        } catch ( Exception ex ) {
            logError("Copy: Failed With Exception");
            logError("    Source File: " + sourceFile);
            logError("    Target File: " + targetFile);

            logError("Exception: " + ex);

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
            logError("Copy: Failed by Result Code:");
            logError("    Source File: " + sourceFile);
            logError("    Target File: " + targetFile);

            logError("Result Code: " + resultCode);

            return false;

        } else {
            log("Copy: OK");

            return true;
        }
    }

    public String getDeleteCommand()
    {
        return ( isWindows ? "DEL" : "rm" );
    }

    public boolean deleteFile(String targetFile)
    {
        log("Delete:");
        log("    Target File: ", targetFile);

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd;

        if ( isWindows ) {
            cmd = new String[] {
                "CMD.EXE",
                "/C",
                getDeleteCommand(),
                enquote(targetFile)
            };
        } else {
            cmd = new String[] {
                getDeleteCommand(),
                enquote(targetFile)
            };
        }

        cmd = correctCmd(cmd);
        if ( cmd == null )
            return false;

        int resultCode;

        try {
            Vector results = new Vector();
            Vector logResults = new Vector();

            try {
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results,
                                          logResults);

            } finally {
                logResults(results, "Result: ");
                logResults(logResults, null);
            }

        } catch ( Exception ex ) {
            logError("Delete: Failed With Exception");
            logError("    Target File: " + targetFile);

            logError("Exception: " + ex);

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
            logError("Delete: Failed by Result Code:");
            logError("    Target File: " + targetFile);

            logError("Result Code: " + resultCode);

            return false;

        } else {
            log("Delete: OK");

            return true;
        }
    }

    // Special for wsadmin processing

    public String getPlatformQuote()
    {
        return ( isWindows ? "\"" : "" );
    }

    // Taking this out ... the exec statement is said to
    // automatically enquote arguments are needed.

    public String enquote(String text)
    {
        return text;

        // String platformQuote = getPlatformQuote();
        //
        // return platformQuote + text + platformQuote;
    }
}
