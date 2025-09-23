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

import java.util.*;
import java.io.*;

//Note: th differences between the unix style getSpace is the the inclusion
//     of "P" in the df -kP, seem some platforms support it, some don't

// History 1.3, 4/29/04
//               Original Version

//scan4err log.Err(        this in support of automated error listing

public class FileSystemSpace {
   public static final String pgmVersion = "1.3" ;
   public static final String pgmUpdate = "4/29/04" ;
    protected static final String  errMsgPfx  = "Error in FileSystemSpace() v"+pgmVersion+ " -- ";
    protected static final boolean adjCmd     = true; // if to adjust the for platform idiosyncratic differences.
    protected static       boolean debug;


    public long totalSpace = 0;  // in Bytes
    public long freeSpace  = 0;  // in Bytes
    public long usedSpace  = 0;  // in Bytes

    protected Vector errorMsg;


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ             ReturnFree space                                  บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public long freeSpace(Vector eM , String path, boolean debug) {

        FileSystemSpace.debug = debug;
        errorMsg = eM;

        String osType = System.getProperty("os.name");

        if ( osType.startsWith("Windows") ) {
            if (!findSpace(path))
                return -1;

        } else if ( osType.startsWith("OS/2") ) {
            if (!findSpace(path))
                return -1;

        } else if ( osType.startsWith("AIX") ) {
            if (!getAixSpace(path))
                return -1;

        } else if (osType.startsWith("Solaris") ) {
            if (!getUnixSpace(path))
                return -1;

        } else if ( osType.startsWith("Linux") ) {
            if (!getLinuxSpace(path))
                return -1;

        } else if ( osType.startsWith("HP-UX") ) {
            if (!getHPUXSpace(path) )
                return -1;

        } else if ( osType.startsWith("SunOS") ) {
            if (!getUnixSpace(path) )
                return -1;
        }

        return freeSpace;
    }


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                          Ensure                               บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean ensure(Vector eM, String required, String path, boolean debug) {

        long requiredLong;

        try {
            requiredLong = Long.parseLong(required.trim());

        } catch (NumberFormatException ex) {
            eM.add(errMsgPfx + "The specified value ("+ required +"), is not a numeric string." );
            return false;
        }

        return ensure(eM, requiredLong, path, debug);

    }

//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                          Ensure                               บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean ensure(Vector eM, long required, String path, boolean debug) {

		FileSystemSpace.debug = debug;
        errorMsg = eM;

        String osType = System.getProperty("os.name");

        if ( osType.startsWith("Windows") ) {
            if (!getWindowsSpace(path, required))
                return false;

        } else if ( osType.startsWith("OS/2") ) {
            if (!getWindowsSpace(path, required))
                return false;

        } else if ( osType.startsWith("AIX") ) {
            if (!getAixSpace(path))
                return false;

        } else if (osType.startsWith("Solaris") ) {
            if (!getUnixSpace(path))
                return false;

        } else if ( osType.startsWith("Linux") ) {
            if (!getLinuxSpace(path))
                return false;

        } else if ( osType.startsWith("HP-UX") ) {
            if (!getHPUXSpace(path) )
                return false;

        } else if ( osType.startsWith("SunOS") ) {
            if (!getUnixSpace(path) )
                return false;

        }

        if (required > freeSpace)
            return false;

        return true;
    }



//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ       Do Windows check for required Space                     บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean getWindowsSpace(String path, long requiredSpace) {

        boolean returnValue = true;

        String filePath = path +"\\DeleteMe";

        // File deleteMe = File.createTempFile(filePath, null);

        // File deleteMe = new File(filePath);
        File deleteMe;

        File fPath = new File(path);

        try {
            deleteMe = File.createTempFile("DeleteMe", null, fPath);
        } catch (IOException ex) {
            errorMsg.add(errMsgPfx +"IOException creating tempFile "+ filePath);
            errorMsg.add( ex.getMessage());
            return false;
        }

        deleteMe.deleteOnExit();

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(deleteMe, "rw");
            long testPoint = requiredSpace;    // the required space is specified in Bytes
            try {
                raf.setLength(testPoint);
            } catch (IOException ex ) {
                returnValue = false;
            }

            try {
                raf.close();
            } catch (IOException ex ) {
                errorMsg.add(errMsgPfx +"Close failure for "+ deleteMe.getAbsolutePath());
                errorMsg.add( ex.getMessage());
                returnValue =  false;
            }

        } catch (IOException ex ) {
            errorMsg.add(errMsgPfx +"IOException for "+ deleteMe.getAbsolutePath());
            errorMsg.add( ex.getMessage());
            returnValue = false;

        } finally {
            try {
                raf.setLength(1);
            } catch (IOException ex ) {
                // System.err.println("Warning : failed to reset "+ filePath +" to size of 1" );
            }

            if (!deleteMe.delete()) {
                errorMsg.add(errMsgPfx +"The file "+ deleteMe.getAbsolutePath() +" was NOT deleted.");
                System.err.println(errMsgPfx +"The file "+ deleteMe.getAbsolutePath() +" was NOT deleted.");
            }

        }

        totalSpace = -1;
        freeSpace  = requiredSpace;  // this is not the actual free space, but rather what was requested
        usedSpace  = -1;

        return returnValue;
    }




//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     Unix free Space                           บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean getUnixSpace(String path) {

        ExecCmd xCmd   = new ExecCmd(adjCmd, debug);
        String dfOpts  = "-k";
        String special = System.getProperty("dfopts");

        if (special != null)
            dfOpts = special;


        String[] task = {"df", dfOpts, path};
        Vector lines  = new Vector();
        Vector logLines = new Vector();

        int rc = xCmd.Execute(task, debug, debug, lines, logLines);

        if (lines.size() < 2 ) {
            errorMsg.add(errMsgPfx +"df "+ dfOpts +" task failed to return data.");
            return false;
        }

        String line = (String) lines.elementAt(1);    // Skip the title line

        // PQ63672 - start
        // Jay Sartoris 08/23/2002
        // If the filesystem name is too long it drops the
        // rest of the line down to the next line.
        // this fix will grab that next line if it needs too.

        String trimmedLine = line.trim();

        if (trimmedLine.indexOf(" ") < 0) {
            if (!lines.elementAt(2).toString().trim().startsWith("/")) {
                line = line + " " + lines.elementAt(2);
            }
        }
        // PQ63672 - end

        StringTokenizer tok = new StringTokenizer(line);

        if (tok.countTokens() < 4 ) {
            errorMsg.add(errMsgPfx +"Returned line("+ line +") has insufficent tokens.");
            return false;
        }

        String mountedDevice = tok.nextToken();
        String total         = tok.nextToken();
        String used          = tok.nextToken();
        String available     = tok.nextToken();

        try {
            totalSpace = Long.parseLong(total) * 1000;


        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ total +") within line("+ line +").");
            return false;
        }


        try {
            usedSpace = Long.parseLong(used) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ used +") within line("+ line +").");
            return false;
        }


        try {
            freeSpace = Long.parseLong(available) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ available +") within line("+ line +").");
            return false;
        }


        return true;
    }




//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     AIX free Space                            บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean getAixSpace(String path) {

        ExecCmd xCmd   = new ExecCmd(adjCmd, debug);
        String dfOpts  = "-kP";
        String special = System.getProperty("dfopts");

        if (special != null)
            dfOpts = special;


        String[] task = {"df", dfOpts, path};
        Vector lines  = new Vector();
        Vector logLines = new Vector();
        
        int rc = xCmd.Execute(task, debug, debug, lines, logLines);

        if (lines.size() < 2 ) {
            errorMsg.add(errMsgPfx +"df "+ dfOpts +" task failed to return data.");
            return false;
        }

        String line = (String) lines.elementAt(1);    // Skip the title line

        // PQ63672 - start
        // Jay Sartoris 08/23/2002
        // If the filesystem name is too long it drops the
        // rest of the line down to the next line.
        // this fix will grab that next line if it needs too.

        String trimmedLine = line.trim();

        if (trimmedLine.indexOf(" ") < 0) {

            if (!lines.elementAt(2).toString().trim().startsWith("/")) {
                line = line + " " + lines.elementAt(2);
            }
        }
        // PQ63672 - end

        StringTokenizer tok = new StringTokenizer(line);

        if (tok.countTokens() < 4 ) {
            errorMsg.add(errMsgPfx +"Returned line("+ line +") has insufficent tokens.");
            return false;
        }

        String mountedDevice = tok.nextToken();
        String total         = tok.nextToken();
        String used          = tok.nextToken();
        String available     = tok.nextToken();

        try {
            totalSpace = Long.parseLong(total) * 1000;


        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ total +") within line("+ line +").");
            return false;
        }


        try {
            usedSpace = Long.parseLong(used) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ used +") within line("+ line +").");
            return false;
        }


        try {
            freeSpace = Long.parseLong(available) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ available +") within line("+ line +").");
            return false;
        }


        return true;
    }


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     Linux free Space                          บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean getLinuxSpace(String path) {

        ExecCmd xCmd   = new ExecCmd(adjCmd, debug);
        String dfOpts  = "-kP";
        String special = System.getProperty("dfopts");

        if (special != null)
            dfOpts = special;


        String[] task = {"df", dfOpts, path};
        Vector lines  = new Vector();
        Vector logLines = new Vector();

        int rc = xCmd.Execute(task, debug, debug, lines, logLines);

        if (lines.size() < 2 ) {
            errorMsg.add(errMsgPfx +"df "+ dfOpts +" task failed to return data.");
            return false;
        }

        String line = (String) lines.elementAt(1);    // Skip the title line

        // PQ63672 - start
        // Jay Sartoris 08/23/2002
        // If the filesystem name is too long it drops the
        // rest of the line down to the next line.
        // this fix will grab that next line if it needs too.

        String trimmedLine = line.trim();

        if (trimmedLine.indexOf(" ") < 0) {
            if (!lines.elementAt(2).toString().trim().startsWith("/")) {
                line = line + " " + lines.elementAt(2);
            }
        }
        // PQ63672 - end

        StringTokenizer tok = new StringTokenizer(line);

        if (tok.countTokens() < 4 ) {
            errorMsg.add(errMsgPfx +"Returned line("+ line +") has insufficent tokens.");
            return false;
        }

        String mountedDevice = tok.nextToken();
        String total         = tok.nextToken();
        String used          = tok.nextToken();
        String available     = tok.nextToken();

        try {
            totalSpace = Long.parseLong(total) * 1000;


        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ total +") within line("+ line +").");
            return false;
        }


        try {
            usedSpace = Long.parseLong(used) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ used +") within line("+ line +").");
            return false;
        }


        try {
            freeSpace = Long.parseLong(available) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ available +") within line("+ line +").");
            return false;
        }


        return true;
    }


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     HPUX free Space                           บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean getHPUXSpace(String path) {

        ExecCmd xCmd   = new ExecCmd(adjCmd, debug);
        String dfOpts  = "-kP";
        String special = System.getProperty("dfopts");

        if (special != null)
            dfOpts = special;

        String[] task = {"df", dfOpts, path};
        Vector lines  = new Vector();
        Vector logLines = new Vector();

        int rc = xCmd.Execute(task, debug, debug, lines, logLines);

        if (lines.size() < 2 ) {
            errorMsg.add(errMsgPfx +"df "+ dfOpts +" task failed to return data.");
            return false;
        }

        String line = (String) lines.elementAt(1);    // Skip the first line

        // PQ63672 - start
        // Jay Sartoris 08/23/2002
        // If the filesystem name is too long it drops the
        // rest of the line down to the next line.
        // this fix will grab that next line if it needs too.

        String trimmedLine = line.trim();

        if (trimmedLine.indexOf(" ") < 0) {
            if (!lines.elementAt(2).toString().trim().startsWith("/")) {
                line = line + " " + lines.elementAt(2);
            }
        }
        // PQ63672 - end

        StringTokenizer tok = new StringTokenizer(line);

        if (tok.countTokens() < 2 ) {
            errorMsg.add(errMsgPfx +"Returned line("+ line +") has insufficent tokens.");
            return false;
        }

        String mountedDevice = tok.nextToken();
        String total         = tok.nextToken();
        String used          = tok.nextToken();
        String available     = tok.nextToken();

        try {
            freeSpace = Long.parseLong(available) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ available +") within line("+ line +").");
            return false;
        }


        try {
            usedSpace = Long.parseLong(used) * 1000;

        } catch (NumberFormatException ex) {
            errorMsg.add(errMsgPfx +"Non numeric value ("+ used +") within line("+ line +").");
            return false;
        }

        totalSpace = freeSpace + usedSpace;

        return true;
    }



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ              Find diskSpace on windows                             บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
    public boolean findSpace(String path) {

        boolean returnValue = true;
        long    testPoint   = -1;

        String filePath;
        if ((path.endsWith("\\")) || (path.endsWith("/")))
            filePath = path +"Delete.Me";
        else
            filePath = path +"\\Delete.Me";

        if (debug)
            System.out.println("Ready to open file ("+ filePath +")");


        File deleteMe = new File(filePath);
        deleteMe.deleteOnExit();

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(deleteMe, "rw");

            if (debug)
                System.out.println(" Starting Search");

            long hwm       = Long.MAX_VALUE;   // High Water Mark
            long lwm       = 0;                // Low Water Mark
            testPoint = 1000000000;

            boolean notDone = true;

            while (notDone) {

                try {
                    raf.setLength(testPoint);

                    if (testPoint > lwm)
                        lwm = testPoint;

                    if (hwm > lwm)
                        testPoint += (hwm - lwm) / 2;

                    if ((hwm-lwm) == 1 ) {
                        notDone = false;
                    }

                } catch (IOException ex ) {
                    if (hwm > testPoint)
                        hwm = testPoint;

                    long temp = testPoint;
                    testPoint -= (hwm - lwm)/2;
                    if (testPoint == temp)
                        testPoint--;
                }
            }

            try {
                raf.close();

            } catch (IOException ex ) {
                errorMsg.add(errMsgPfx +"IOException on close of "+ deleteMe.getAbsolutePath());
                errorMsg.add(ex.getMessage());
                returnValue = false;
            }

        } catch (IOException ex ) {
            errorMsg.add(errMsgPfx +"IOException on "+ deleteMe.getAbsolutePath());
            errorMsg.add(ex.getMessage());
            returnValue = false;

        } finally {

            if (!deleteMe.delete()) {
                String msg = "Warning : The file "+ filePath +" was NOT deleted";
                System.err.println(msg);
                errorMsg.add(errMsgPfx + msg);
            }
        }

        freeSpace = testPoint;
        return returnValue;
    }

}
