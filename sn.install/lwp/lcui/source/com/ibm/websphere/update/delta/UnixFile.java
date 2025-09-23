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
 * Unix file utility extension
 *
 * History 1.7, 8/15/05
 *
 * 26-Jun-2002 Initial Version
 *
 * 18-Oct-2002 Corrected owner & group retrieval;
 *             made debugging conditional.
 *
 * 19-Oct-2002 Changed final creation of the permissions string
 *             in 'getPermissions()', which had precedence problems
 *             in converting the individual permissions to a string.
 *
 * 13-Jan-2002 Updated widths for getting group and owner;
 *             changed to cache 'ls' only once;
 *             added additional debugging statements.
 * 
 * 31-Jan-2003 Updated with ExtFile JNI library to handle native file
 *             system queries.
 * 
 * 20-Nov-2003 handle spaces in paths
 *
 */ 

import java.io.*;
import java.util.*;

// import com.ibm.Copyright;
import com.ibm.io.file.NativeFile;
import com.ibm.io.file.exception.*;

public class UnixFile extends File implements ISystemFile
{
   public static final String pgmVersion = "1.7", pgmUpdate  = "8/15/05";

    public static final String
        debugPropertyName = "com.ibm.websphere.update.delta.debug",
        debugTrueValue    = "true",
        debugFalseValue   = "false";

    protected static boolean debug;

    static {
        String debugValue = System.getProperty(debugPropertyName);

        debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
    }

	private PermissionHelper permHelper = null;

	private PermissionHelper getPermissionHelper() {
	
		//get the helper class
		String theHelperClass = System.getProperty("permissionHelper");
		if (theHelperClass == null) return null;
	
		try {
			//try to create an instance of it
			Class c = Class.forName(theHelperClass);
			return (PermissionHelper)c.newInstance();
		}
		catch (Exception e) {
			//if error, return null
			System.out.println(e);
			return null;
		}
	
	
	}
	
	
    protected static void debug(String arg)
    {
        if ( !debug )
            return;

        System.out.println(arg);
    }

    protected static void debug(String arg1, String arg2)
    {
        if ( !debug )
            return;

        System.out.print(arg1);
        System.out.println(arg2);
    }

    protected String absoluteFileName;

    protected boolean didSetLSResult;
    protected String lsResult;

    protected String filePermissions;
    protected String fileGroup;
    protected String fileOwner;

    protected NativeFile nativeFileInst;

    public UnixFile(String absoluteFileName)
    {
        super(absoluteFileName);

        this.absoluteFileName = absoluteFileName;

        this.didSetLSResult = false;
        this.lsResult = null;

        this.filePermissions = null;
        this.fileGroup = null;
        this.fileOwner = null;

        this.nativeFileInst = new NativeFile(absoluteFileName);
        debug("native extfile functionality available? " + NativeFile.isNativeFileFunctionalityAvailable());
        
        //added by guminy
        this.permHelper = getPermissionHelper();
		if (this.permHelper != null) 
			{ 
				this.permHelper.setFilename(absoluteFileName); 
			}
       //done
    }

    public File getFile()
    {
        debug("Enter UnixFile:getFile");

        return this;
    }

    protected String getLSResult()
    {
        if ( !didSetLSResult ) {
            didSetLSResult = true;

            boolean adjust4Platforms = false;

            boolean showLaunchMsg    = debug;
            boolean displayStdOut    = debug;
            boolean displayStdErr    = debug;

            Vector msgBuffer = new Vector();
	    Vector logBuffer = new Vector();

            String[] cmd = { "ls", "-l", absoluteFileName };
            ExecCmd exCmd = new ExecCmd(adjust4Platforms, showLaunchMsg);
            int rc = exCmd.Execute(cmd, displayStdOut, displayStdErr, msgBuffer, logBuffer);

            if ( rc != 0 ) {
                lsResult = null;
                debug("LS Command failure!");

            } else if ( msgBuffer.size() == 0 ) {
                lsResult = null;
                debug("Empty LS result buffer!");

            } else {
                lsResult = (String) msgBuffer.elementAt(0);
                debug("LS Result: ", lsResult);
            }
        }

        return lsResult;
    }

    // A sample LS result; taken from AIX:
    //
    // "-rw-r--r--   1 root     system          266 Jan 13 01:31 WAS_EJBDeploy"
    //  0123456789012345678901234567890123456789
    //                 15       24
    //                 substring(15, 24);
    //                          substring(24, 33)
    //
    // A sample from a non-AIX system:
    //
    // "-rwxrwxrwx   1 owner    group        16967459 Jan  9 10:07 doit.out"
    //
    // Permissions     owner    group
    //

    public String getPermissions()
    {
        debug("Enter UnixFile:getPermissions: ", absoluteFileName);

		//guminy
		if (permHelper != null) {
			return permHelper.getPermissions();
		}
		
        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            debug("UnixFile:getPermissions: NativeFile.isNativeFileFunctionalityAvailable() == TRUE");
            try {
                int uPerm = nativeFileInst.getUserPermissions();
                int gPerm = nativeFileInst.getGroupPermissions();
                int wPerm = nativeFileInst.getWorldPermissions();
                int isSym = nativeFileInst.isSymLink() ? 1 : 0;
                
                filePermissions =
                    Integer.toString(isSym * 1000 + uPerm * 100 + gPerm * 10 + wPerm);
            }
            catch (AccessDeniedException e) {
                debug(e.getMessage());
            }
            catch (com.ibm.io.file.exception.FileNotFoundException e) {
                debug(e.getMessage());
            }
            catch (NativeFileIOException e) {
                debug(e.getMessage());
            }

        }

        if (!NativeFile.isNativeFileFunctionalityAvailable() ||
        filePermissions == null ) {
            debug("UnixFile:getPermissions: NativeFile.isNativeFileFunctionalityAvailable() == FALSE");
            debug("Fetching LS result for permissions.");

            int v0 = 0;   // Symbolic link value is either a one or a zero
            int v1 = 0;   // User Part
            int v2 = 0;   // Group part
            int v3 = 0;   // World Part

            String line = getLSResult();

            if ( line != null ) {
                debug("Parsing LS result for permissions: ", line);

                if (line.length() > 11) {
                    char[] chars = line.toCharArray();
                    // the line should look like  drwxrwxrwx
                    v0 = (chars[0] == 'l') ?    1 :  0;    // to indicate if this is a link

                    v1 = (chars[1] == 'r') ?    4 :  0;    // User Part
                    v1 = (chars[2] == 'w') ? v1+2 : v1;
                    v1 = (chars[3] == 'x') ? v1+1 : v1;

                    v2 = (chars[4] == 'r') ?    4 :  0;    // Group Part
                    v2 = (chars[5] == 'w') ? v2+2 : v2;
                    v2 = (chars[6] == 'x') ? v2+1 : v2;

                    v3 = (chars[7] == 'r') ?    4 :  0;    // World Part
                    v3 = (chars[8] == 'w') ? v3+2 : v3;
                    v3 = (chars[9] == 'x') ? v3+1 : v3;
                }

            } else {
                debug("Unable to parse LS result: no LS result!");
            }

            filePermissions = Integer.toString( (v0 * 1000) + (v1 * 100) + (v2 * 10) + v3 );
        }

        debug("Exit UnixFile:getPermissions: ", filePermissions);

        return filePermissions;
    }

    // "-rw-r--r--   1 root     system          266 Jan 13 01:31 WAS_EJBDeploy"
    // "-rwxrwxrwx   1 owner    group         6292453 Jan  6  4:52 installer.jar"
    // "-rwxrwxrwx   1 owner    group           12010 Jan  8  0:05 MrBuild_process"
    // "-rw-r--r--   1 root     system       235523 Jan 10 13:38 smit.log"
    // "-rw-r--r--   1 root     system         3145 Jan 03 11:42 smit.script"
    // "drwxrwxr-x   2 root     system          512 Aug 13 15:05 tftpboot"
    // "-rw-r--r--   1 root root  17233 Jan 29 20:15 vpd.properties"             (SLES9)
    // "-rw-r--r--    1 root     root            0 Nov 17 19:20 vpd.properties"  (SuSE 8.2)
    //
    //  0123456789012345678901234567890123456789
    //                 15       24
    //                 substring(15, 24)
    //                          substring(24, 33)

    public static final int
        OWNER_START = 15,
        GROUP_START = 24,
        GROUP_END   = 33;

    public String getOwner()
    {
        debug("Enter UnixFile:getOwner: ", absoluteFileName);
        
        //guminy
        if (permHelper != null ) {
        	return permHelper.getOwner();
        }

        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            try {
                fileOwner = nativeFileInst.getOwner();
            }
            catch (AccessDeniedException e) {
                debug(e.getMessage());
            }
            catch (com.ibm.io.file.exception.FileNotFoundException e) {
                debug(e.getMessage());
            }
            catch (NativeFileIOException e) {
                debug(e.getMessage());
            }
        }

        if (!NativeFile.isNativeFileFunctionalityAvailable() ||
            fileOwner == null ) {
            debug("Fetching LS result for owner.");

            String line = getLSResult();

            if ( line != null ) {
                debug("Parsing LS result for owner: ", line);

                // "-rw-r--r--   1  root root  17233 Jan  29 20:15 vpd.properties"
                //  /---0----/  /1/ /2-/ /3-/  /-4-/ /5/ /6/ /-7-/ /-----8------/
                fileOwner = getToken(line, 2);
                    
                if ( null != fileOwner )
                {
                	debug("Retrieved owner: " + fileOwner);
                }
                else
                {
                	debug("UnixFile:getOwner() received NULL from UnixFile:getToken()");
                }

            } else {
                debug("Unable to parse LS result for owner: No LS result!");
            }
        }

        debug("Exit UnixFile:getOwner: ", fileOwner);

        return fileOwner;
    }
    
    public String getGroup()
    {
        debug("Enter UnixFile:getGroup: ", absoluteFileName);
        
        //guminy
        if (permHelper != null) {
        	return permHelper.getGroup();
        }

        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            try {
                fileGroup = nativeFileInst.getGroup();
            }
            catch (AccessDeniedException e) {
                debug(e.getMessage());
            }
            catch (com.ibm.io.file.exception.FileNotFoundException e) {
                debug(e.getMessage());
            }
            catch (NativeFileIOException e) {
                debug(e.getMessage());
            }       
        }

        if (!NativeFile.isNativeFileFunctionalityAvailable() ||
            fileGroup == null ) {
            debug("Fetching LS result for group.");

            String line = getLSResult();

            if ( line != null ) {
                debug("Parsing LS result for group: ", line);

                // "-rw-r--r--   1  root root  17233 Jan  29 20:15 vpd.properties"
                //  /---0----/  /1/ /2-/ /3-/  /-4-/ /5/ /6/ /-7-/ /-----8------/
                fileGroup = getToken(line, 3);
                    
                if ( null != fileOwner )
                {
                	debug("Retrieved group: " + fileGroup);
                }
                else
                {
                	debug("UnixFile:getGroup() received NULL from UnixFile:getToken()");
                }
                
            } else {
                debug("Unable to parse LS result for group: no LS result!");
            }
        }

        debug("Exit UnixFile:getGroup: ", fileGroup);

        return fileGroup;
    }

    public int setPermissions(String chmodValue)
    {
        debug("Enter UnixFile:setPermissions: ", absoluteFileName);
        
        //guminy
        if (permHelper != null) {
        	return permHelper.setPermissions(chmodValue);
        }

        int rc = 1;

        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            char[] charArray = chmodValue.toCharArray();
            int wPerms = Integer.parseInt(String.valueOf(charArray[chmodValue.length() - 1]));
            int gPerms = Integer.parseInt(String.valueOf(charArray[chmodValue.length() - 2]));
            int uPerms = Integer.parseInt(String.valueOf(charArray[chmodValue.length() - 3]));
            try {
                nativeFileInst.setWorldPermissions(wPerms);
                nativeFileInst.setGroupPermissions(gPerms);
                nativeFileInst.setUserPermissions(uPerms);
                
                rc = 0;
            }
            catch (AccessDeniedException e) {
                debug(e.getMessage());
            }
            catch (com.ibm.io.file.exception.FileNotFoundException e) {
                debug(e.getMessage());
            }
            catch (NativeFileIOException e) {
                debug(e.getMessage());
            }

        }

        if (rc != 0) {

            Vector results = new Vector();
            Vector logResults = new Vector();
    
            boolean adjust4Platforms = false;
            boolean showLaunchMsg    = debug;
            boolean displayStdOut    = debug;
            boolean displayStdErr    = debug;
    
            String[] cmd = { "chmod", chmodValue, absoluteFileName };
            ExecCmd exCmd = new ExecCmd(adjust4Platforms, showLaunchMsg);
            rc = exCmd.Execute(cmd, displayStdOut, displayStdErr, results, logResults);
    
            if ( rc != 0 )
                debug("Command failure");
            else
                filePermissions = String.valueOf(chmodValue);
        }
        debug("Exit UnixFile:setPermissions: ", filePermissions);

        return rc;
    }

    public int setGroup(String groupValue)
    {
        debug("Enter UnixFile:setGroup: ", absoluteFileName);
        
        //guminy
        if(permHelper != null) {
        	return permHelper.setGroup(groupValue);
        }
        
        int rc = 1;

        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            try {
                nativeFileInst.setGroup(groupValue);
                rc = 0;
            }
            catch (AccessDeniedException e) {
                debug(e.getMessage());
            }
            catch (com.ibm.io.file.exception.FileNotFoundException e) {
                debug(e.getMessage());
            }
            catch (NativeFileIOException e) {
                debug(e.getMessage());
            }
        }

        if (rc != 0) {

            Vector results = new Vector();
            Vector logResults = new Vector();
    
            boolean adjust4Platforms = false;
            boolean showLaunchMsg    = debug;
            boolean displayStdOut    = debug;
            boolean displayStdErr    = debug;
    
            String[] cmd = { "chgrp", groupValue, absoluteFileName };
            ExecCmd exCmd = new ExecCmd(adjust4Platforms, showLaunchMsg);
            rc = exCmd.Execute(cmd, displayStdOut, displayStdErr, results, logResults);
    
            if ( rc != 0 )
                debug("Command failure");
            else
                fileGroup = groupValue;
        }

        debug("Exit UnixFile:setGroup: ", fileGroup);

        return rc;
    }

    public int setOwner(String ownerValue)
    {
        debug("Enter UnixFile:setOwner: ", absoluteFileName);
        
        //guminy
        if (permHelper != null) {
        	return permHelper.setOwner(ownerValue);
        }

        int rc = 1;
        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            try {
                nativeFileInst.setOwner(ownerValue);
                rc = 0;
            }
            catch (AccessDeniedException e) {
                debug(e.getMessage());
            }
            catch (com.ibm.io.file.exception.FileNotFoundException e) {
                debug(e.getMessage());
            }
            catch (NativeFileIOException e) {
                debug(e.getMessage());
            }

        }

        if (rc != 0) {

            Vector results = new Vector();
            Vector logResults = new Vector();
    
            boolean adjust4Platforms = false;
            boolean showLaunchMsg    = debug;
            boolean displayStdOut    = debug;
            boolean displayStdErr    = debug;
    
            String[] cmd = { "chown", ownerValue, absoluteFileName };
            ExecCmd exCmd = new ExecCmd(adjust4Platforms, showLaunchMsg);
            rc = exCmd.Execute(cmd, displayStdOut, displayStdErr, results, logResults);
    
            if ( rc != 0 )
                debug("Command failure");
            else
                fileOwner = ownerValue;
        }

        debug("Exit UnixFile:setOwner: ", fileOwner);

        return rc;
    }

    public int setWritable() {
        // check the permissions
        char[] chArr = getPermissions().toCharArray();
        char auth;
        int pos, rc = 0;
        String newPermission;
        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            auth = chArr[1];
            pos  = 1;
        } else {
            auth = chArr[0];
            pos = 0;
        }

        switch(auth) {
        case '0':
            chArr[pos] = '2';
            break;
        case '1':
            chArr[pos] = '3';
            break;
        case '4':
            chArr[pos] = '6';
            break;
        case '5':
            chArr[pos] = '7';
            break;
        }
        newPermission = chArr.toString();
        rc = setPermissions(newPermission);
        return rc;
    }

    public boolean isWritable() {

        char[] chArr = getPermissions().toCharArray();
        char auth;

        if (NativeFile.isNativeFileFunctionalityAvailable()) {
            auth = chArr[1];
        } else {
            auth = chArr[0];
        }

        // check if it is not writable
        if (auth == '0' ||  auth == '1' || auth == '4' || auth == '5') {
            return false;
        }
        // writable return true
        return true;
    }

    public String getToken( String line, int position )
    {
    	if ( null == line || line.equals("") )
    	{
    		debug("UnixFile:getToken @param line is NULL or empty.");
    		return null;
    	}
    	
    	if ( 0 > position )
    	{
    		debug("UnixFile:getToken cannot retrieve @param position < 0");
    		return null;
    	}
    	
    	String retVal = null;
    	
        StringTokenizer st = new StringTokenizer(line);
        // "-rw-r--r--   1  root root  17233 Jan  29 20:15 vpd.properties"
        //  /---0----/  /1/ /2-/ /3-/  /-4-/ /5/ /6/ /-7-/ /-----8------/
        //  countTokens() == 9
        //  max position  == 8
        //  test: position < countTokens() 
        if ( position < st.countTokens() ) 
        {
        	int count = 0;
        	while ( st.hasMoreTokens() ) {
        		String token = st.nextToken();
        		debug("UnixFile:getToken() -- Token #" + count + " == \"" + token + "\"" );
        		if ( position == count ) {
        			// give the user what they requested . . .
        			return token;
        		}
        		count++;
        	}
        } 
        else
        {
        	debug("UnixFile:getToken() -- Not enough tokens in string! _" + st.countTokens() + "_ found, _" + position + "_ requested.", line);
        	return null;
        }
        
        debug("UnixFile:getToken unexpectedly returning fallthrough retVal . . .");
        return retVal;
    }

    
}
