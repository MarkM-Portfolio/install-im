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
package com.ibm.websphere.update.ioservices;

/*
 * Support for comples IO operations.
 * 
 * 23-Feb-2003 1.7 Flags files created in extractJarEntry() for deleteOnExit, if
 *                 they are created in temp space.
 *
 * 19-Feb-2003 1.6 Updated to cache entry names by jar name.
 *                 Also, use cache for 'getChildEntryNames', in addition
 *                 to 'getSubDirRootNames'.
 *
 * 18-Feb-2003 1.5 Fixed getSubDirRootNames to actually query for roots in the jar.
 * 
 * 17-Feb-2003 1.4 Changed getSubDirRootNames to cache result for subsequent calls
 *
 * 27-Dec-2002 1.2
 * 
 * 12-Jul-2002 Initial Version
 */

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.jar.*;

public class IOService {
    // Program versioning ...

    public static final String pgmVersion = "1.6" ;
    // Program versioning ...

    public static final String pgmUpdate = "2/20/03" ;

    // Debug ...

    // Debugging ...

    public static final boolean isDebug;

    public static final String debugPropertyName = "com.ibm.websphere.update.ioservices.debug" ;
    public static final String debugTrueValue = "true" ;

    static {
        String debugValue = System.getProperty(debugPropertyName);

        isDebug = ((debugValue != null) && debugValue.equalsIgnoreCase(debugTrueValue));
    }

    public void debug(Object arg) {
        if ( isDebug )
            System.out.println(arg);
    }

    public void debug(Object arg1, Object arg2) {
        if ( isDebug ) {
            System.out.print(arg1);
            System.out.println(arg2);
        }
    }

    public void debug(Object arg1, Object arg2, Object arg3) {
        if ( isDebug ) {
            System.out.print(arg1);
            System.out.print(arg2);
            System.out.println(arg3);
        }
    }

    // Map <String jarName> --> Vector<String entryName>
    //
    // A mapping which, per named jar, contains the complete list of entries
    // of that jar.

    protected HashMap entryNamesTable = new HashMap();

    /**
     * Clear all cached entry names.
     */

    public void clearEntryNames() {
        if ( isDebug )
            debug("IOService: clearEntryNames");

        entryNamesTable = new HashMap();
    }

    /**
     * Clear the entry names associated with the specified jar.
     *
     * Answer the current cache entry of that jar.  (Answer null
     * if no entries were cached.)
     *
     * The caller is responsible for handling jar naming identity issues
     * (ideally, the jar name will be a full path).
     */

    public Vector clearEntryNames(String jarName) {
        if ( isDebug )
            debug("IOService: clearEntryName: ", jarName);

        return (Vector) entryNamesTable.remove(jarName);
    }

    /**
     * Answer as a vector the collection of entry names of the specified
     * jar file
     *
     * The results may be returned  from cache.  The external user is
     * responsible for handling cache consistency.
     *
     * Throw an IO exception if the loading of the entry names fails.
     *
     * The caller is responsible for handling jar naming identity issues
     * (ideally, the jar name will be a full path).
     */

    public Vector getEntryNames(String jarName) throws IOException {
        if ( isDebug )
            debug("IOService: getEntryNames: ", jarName);

        Vector entryNames = (Vector) entryNamesTable.get(jarName);

        if ( entryNames == null ) {
        if ( isDebug )
            debug("IOService: getEntryNames: ", "Entries are not cached; retrieving.");

            entryNames = basicGetEntryNames(jarName); // throws IOException
            entryNamesTable.put(jarName, entryNames);

        } else {
            if ( isDebug )
                debug("IOService: getEntryNames: ", "Entries are cached.");
        }

        if ( isDebug )
            debug("IOService: getEntryNames: ", "Returning " + entryNames.size() + " entries.");

        return entryNames;
    }

    protected Vector basicGetEntryNames(String jarName) throws IOException {
        Vector entryNames = new Vector();

        JarInputStream jis = new JarInputStream(new FileInputStream(jarName), false); // throws IOException

        try {
            JarEntry nextEntry;
            while ((nextEntry = jis.getNextJarEntry()) != null) { // throws IOException
                entryNames.add( nextEntry.getName() );
            }
        } finally {
            jis.close(); // throws IOException
        }

        return entryNames;
    }

    // Answer just the tails of the names of the matching entries.

    public static final boolean ONLY_CHILD_FILES = true ;
    // Answer just the tails of the names of the matching entries.

    public static final boolean ALL_CHILD_FILES = false ;
        
    public Vector getChildEntryNames(String jarName, String rootEntryName) throws IOException {
        return getChildEntryNames(jarName, rootEntryName, ALL_CHILD_FILES); // throws IOException
    }

    public Vector getChildEntryNames(String jarName, String rootEntryName, boolean filesOnly) throws IOException {
        if ( isDebug ) {
            debug("IOService: getChildEntryNames: Target Jar: ", jarName);
            debug("IOService: getChildEntryNames: Root Entry: ", rootEntryName);
            debug("IOService: getChildEntryNames: File Only : ", (filesOnly ? "true" : "false"));
        }

        rootEntryName = rootEntryName.replace('\\', '/');
        rootEntryName += "/";

        int rootEntryLength = rootEntryName.length();

        Vector entryList = new Vector();
        Hashtable entryTable = new Hashtable();

        Vector entryNames = getEntryNames(jarName); // throws IOException
        int numEntryNames = entryNames.size();

        for ( int entryNameNo = 0; entryNameNo < numEntryNames; entryNameNo++ ) {
            String nextEntryName = (String) entryNames.elementAt(entryNameNo);

            if ( isDebug )
                debug("IOService: getChildEntryNames: Scanning entry: ", nextEntryName);

            if ( nextEntryName.startsWith(rootEntryName) ) {
                if ( isDebug )
                    debug("IOService: getChildEntryNames: entry matches root; testing.");

                int spanOfPrefix = nextEntryName.indexOf("/", rootEntryLength);

                String prefix;

                if ( spanOfPrefix == -1 )
                    prefix = nextEntryName.substring(rootEntryLength);
                else if ( !filesOnly )
                    prefix = nextEntryName.substring(rootEntryLength, spanOfPrefix);
                else
                    prefix = null;
                        
                if ( (prefix != null) && (prefix.length() > 0) ) {
                    if ( isDebug )
                        debug("IOService: getChildEntryNames: Scanned Prefix: ", prefix);

                    if ( !entryTable.contains(prefix) ) {
                        if ( isDebug )
                            debug("IOService: getChildEntryNames: New Prefix; Adding");

                        entryTable.put(prefix, prefix);
                        entryList.add(prefix);
                    } else {
                        if ( isDebug )
                            debug("IOService: getChildEntryNames: Duplicate Prefix; Skipping");
                    }
                } else {
                    if ( isDebug )
                        debug("IOService: getChildEntryNames: No Prefix; Skipping");
                }
            } else {
                if ( isDebug )
                    debug("IOService: getChildEntryNames: entry does not match root; skipping.");
            }
        }

        if ( isDebug )
            debug("IOService: getChildEntryNames: Returning " + entryList.size() + " entries.");
        return entryList;
    }

    /**
     * This method now builds the subDirRootNames set on the initial query, and
     * returns the inital result on all subsequent calls.
     * 
     * Original design of this class appears to be of a static nature, so this
     * mechanism may be faulty in unobserved situations.
     */

    public Set getSubDirRootNames(String jarName, String rootEntryName) throws IOException
    {
        if ( isDebug ) {
            debug("IOService: getSubDirRootNames: Target Jar: ", jarName);
            debug("IOService: getSubDirRootNames: Root Name : ", rootEntryName);
        }

        Set entryList = new HashSet();

        rootEntryName = rootEntryName.replace('\\', '/');
        rootEntryName += "/";

        int rootEntryLength = rootEntryName.length();

        Vector entryNames = getEntryNames(jarName); // throws IOException

        int numEntryNames = entryNames.size();

        for ( int entryNameNo = 0; entryNameNo < numEntryNames; entryNameNo++ ) {
            String nextEntryName = (String) entryNames.elementAt(entryNameNo);

            if ( isDebug )
                debug("IOService: getSubDirRootNames: Next Entry: ", nextEntryName);

            if (nextEntryName.startsWith(rootEntryName)) {
                if ( isDebug )
                    debug("IOService: getSubDirRootNames: Matches; Adding");

                int lastSlash = nextEntryName.lastIndexOf("/");
                entryList.add( nextEntryName.substring(0, lastSlash) );

            } else {
                if ( isDebug )
                    debug("IOService: getSubDirRootNames: Does not Match; Skipping");
            }
        }

        if ( isDebug )
            debug("IOService: getSubDirRootNames: Returning " + entryList.size() + " entries.");
        return entryList;
    }

    // Answer the list of files beneath the argument directory.
    // The return type is Vector<File>.

    public Vector getChildNames(String rootDirName) throws IOServicesException {
        if ( isDebug )
            debug("IOService: getChildNames: ", rootDirName);

        Vector childFiles = new Vector();

        File rootFile = new File(rootDirName);

        if (!rootFile.exists()) {
            if ( isDebug )
                debug("IOService: getChildNames: ", "Error: Directory does not exist.");

            throw new IOServicesException("WUPD0100E", new String[] { rootDirName }, null);
        } else if (!rootFile.isDirectory()) {
            if ( isDebug )
                debug("IOService: getChildNames: ", "Error: File is not a directory.");

            throw new IOServicesException("WUPD0101E", new String[] { rootDirName }, null);
        }

        File[] childFileList = rootFile.listFiles();

        for (int childNo = 0; childNo < childFileList.length; childNo++) {
            File nextChild = childFileList[childNo];

            if ( isDebug )
                debug("IOService: getChildNames: Adding File: ", nextChild.getAbsolutePath());

            if (nextChild.isFile())
                childFiles.add(nextChild);
        }

        if ( isDebug )
            debug("IOService: getChildNames: Returning " + childFiles.size() + " files.");

        return childFiles;
    }

    // Open and return an input stream on the named entry within the named jar.

    public InputStream getJarEntryStream(String jarName, String entryName) throws IOException {
        // System.out.println("Diag #23  jarName=("+ jarName +")");
        // System.out.println("Diag #24  entryName=("+ entryName +")");

        entryName = entryName.replace('\\', '/');

        JarFile jf = new JarFile(jarName);
        ZipEntry zipEntry = jf.getEntry(entryName);

        if (zipEntry == null)
            return null;

        InputStream is = jf.getInputStream(zipEntry); // throws IOException
        return is;
    }

    // Close the argument input stream.  Although the argument type is
    // 'inputStream', this method is provided as the preferred way to close
    // an input stream returned by 'getJarEntryStream', above.

    public void closeJarEntryStream(InputStream inputStream) throws IOException {
        inputStream.close(); // throws IOException
    }

    /** Extract the requested entry from the jar file and place it in
     *  specified location.
     *
     *  Returns String of the output file name.
    */

    public static final char URL_SEP_CHAR = '/';

    public String extractJarEntry(String jarName, String entryName, String targetLocation)
        throws IOServicesException, IOException {
        // System.out.println("Diag #900   entering extractJarEntry");
        // System.out.println("Diag #900.1     jarFileName=("+ jarName +")");
        // System.out.println("Diag #900.2       entryName=("+ entryName +")");
        // System.out.println("Diag #900.3  targetLocation=("+ targetLocation +")");

        if (!targetLocation.endsWith(File.separator))
            targetLocation += File.separator;
        targetLocation += entryName;

        char sepChar = File.separator.charAt(0);

        if (sepChar != URL_SEP_CHAR)
            targetLocation = targetLocation.replace(URL_SEP_CHAR, sepChar);

        int dirEnd = targetLocation.lastIndexOf(sepChar);

        String targetDirName = targetLocation.substring(0, dirEnd),
            targetFileName = targetLocation.substring(dirEnd + 1);

        // System.out.println("Diag #900.4  targetDirName =(" + targetDirName  + ")");
        // System.out.println("Diag #900.5  targetFileName=(" + targetFileName + ")");

        // Make sure that the directory for the target entry exists.

        File targetDir = new File(targetDirName);

        if (!targetDir.exists()) {
            if (!targetDir.mkdirs())
                throw new IOServicesException("WUPD0103E", new String[] { targetDirName }, null);
        } else if (!targetDir.isDirectory()) {
            throw new IOServicesException("WUPD0104E", new String[] { targetDirName }, null);
        }

        int rc = 0;
        int bufferSize = 2048;
        byte[] IOBuf = new byte[bufferSize];

        InputStream jis = getJarEntryStream(jarName, entryName); // throws IOException

        if (jis == null)
            throw new IOServicesException("WUPD0102E", new String[] { entryName, jarName }, null);
        // else
        //     System.out.println("Diag #900.5 Non-null input stream");

        BufferedOutputStream outFile =
            new BufferedOutputStream(new FileOutputStream(targetLocation), bufferSize);
        // throws FileNotFoundException

        deleteOnExitIfTemp(targetLocation);

        // System.out.println("Diag #900.6 opened output stream");

        boolean notDone = true;
        long totRead = 0; // used for debug purposes only

        while (notDone) {
            int bytesRead = jis.read(IOBuf, 0, bufferSize); // throws IOException

            if (bytesRead > 0) {
                // System.out.println("Diag #900.7 writing: " + bytesRead);
                totRead += bytesRead;
                outFile.write(IOBuf, 0, bytesRead); // throws IOException

            } else {
                // System.out.println("Diag #900.8 complete");
                notDone = false;
            }
        }

        outFile.close(); // throws IOException
        jis.close();

        // System.out.println("Diag #900.9 returning: " + targetLocation);

        return targetLocation;
    }
    
    /**
     * Flags file for deleteOnExit if the file is in the temp directory.  Returns
     * false if there was an error or if the provided filename was not detected as
     * being in temp space.
     */
    
    private boolean deleteOnExitIfTemp(String targetLocation)
    {
        boolean success = false;
        
        try {
            File tempDirFile = new File(System.getProperty("java.io.tmpdir"));
            File targetLocationFile = new File(targetLocation);
            
            String targetCanonicalPath = targetLocationFile.getCanonicalPath();
            String tempCanonicalPath = tempDirFile.getCanonicalPath(); 
            String fileSeparator = System.getProperty("file.separator");
            
            if (fileSeparator != null
                && targetLocationFile != null
                && !targetCanonicalPath.equals(tempCanonicalPath)
                && targetCanonicalPath.startsWith(tempCanonicalPath))
            {
                int indexAfterTemp = targetCanonicalPath.indexOf(fileSeparator, tempCanonicalPath.length() + fileSeparator.length());
                
                String childOfTempToDelete = null;
                
                if ((targetCanonicalPath.length() > tempCanonicalPath.length()) && indexAfterTemp < 0) {
                    childOfTempToDelete = targetCanonicalPath;
                }
                else {
                    childOfTempToDelete = targetCanonicalPath.substring(0, indexAfterTemp);
                }
                
                deleteOnExitRecurse(targetLocation);

                success = true;
            }

        } catch (IOException ioe) {
            debug("IOService: deleteOnExitIfTemp: ", "error when flagging for deletion " + targetLocation + " " + ioe.getMessage());
        }
        
        return success;
    }
    
    // Recurse down a directory path, flagging directories and files for
    // deletion.
    
    private void deleteOnExitRecurse(String fileName)
    {
        File fileToDelete = new File(fileName);
        fileToDelete.deleteOnExit();
        
        if (fileToDelete.isDirectory()) {
            String[] files = fileToDelete.list();   

            for (int i = 0; i < files.length; i++) {
              deleteOnExitRecurse(fileToDelete.getAbsolutePath() + System.getProperty("file.separator") + files[i]);
            }
        }
    }

/*
    public static void main(String args[]) {
        IOService ios = new IOService();
        try {
            Set dirRoots =
                ios.getSubDirRootNames(
                    "C:\\WebSphere\\AppServer\\update\\ptfRepository\\ptf_mq.jar",
                    "ptfs\\ptf_mq\\components\\external.mq\\CSD");

            System.out.println(dirRoots.toString());
            System.out.println(dirRoots.size());


            if (dirRoots != null) {
                    int numDirRoots = dirRoots.size();
                    Iterator dirRootIter = dirRoots.iterator();
                    System.out.println(numDirRoots + " subRoots found");
                    while (dirRootIter.hasNext()) {
                        String entryNameRoot = (String) dirRootIter.next();
                        System.out.println("getting child entry names for : " + entryNameRoot);
                        Vector entries =
                            ios.getChildEntryNames("C:\\WebSphere\\AppServer\\update\\ptfRepository\\ptf_mq.jar", entryNameRoot, ONLY_CHILD_FILES);

                        int numEntries = entries.size();
                        for (int j = 0; j < numEntries; j++) {
                            String aChildEntryName = (String) entries.elementAt(j);
                            String fileToExtract = entryNameRoot + File.separator + aChildEntryName;
                            System.out.println(fileToExtract);

                            String anExtractedFile =
                                ios.extractJarEntry(
                                    "C:\\WebSphere\\AppServer\\update\\ptfRepository\\ptf_mq.jar",
                                    fileToExtract,
                                    "C:\\temp");

                        }
                    }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/
}
