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
import java.util.*;

/*
 * WindowsFile extends java.io.File implements ISystemFile
 *
 * @author: Kim Hackett
 * History 1.2, 9/26/03
 *
 */

class WindowsFile extends File implements ISystemFile{
   public static final String pgmVersion = "1.2" ;
   public static final String pgmUpdate = "9/26/03" ;

   private boolean debug = false;
   private String absoluteFileName;
   private String filePermissions = null;
   private String fileGroup = "root";
   private String fileOwner = "root";

   /* WindowsFile Constructor
   *
   * @param String fileName; absolute path to file
   * @param boolean debug
   * @return void
   */
   WindowsFile(String absoluteFileName){
      super(absoluteFileName);

   } // end Constructor

   /* Get file instance
   * getPermissions()
   * @param void
   * @return int permissions
   */
   public File getFile() {
      if (debug) {
            System.out.println("Enter WindowsFile:getFile()");
      }
      return this;
   }

   /* Get windows style file permissions
   * getPermissions()
   * @param void
   * @return int permissions
   */
   public String getPermissions() {
      if (debug) {
            System.out.println("Enter WindowsFile:getPermissions(File: "+ absoluteFileName +")");
      }
      if (filePermissions != null) {
         return filePermissions;
      }
      int v0 = 0;   // Symbolic link value is either a one or a zero
      int v1 = 0;   // User Part
      int   v2 = 0;   // Group part
      int v3 = 0;   // World Part
      if (this.isHidden()) {  // we can not do anything
         v1 = 0;
         v2 = 0;
         v3 = 0;
         } else if (this.canWrite()) {  // if we can write we can do everything
         v1 = 7;
         v2 = 7;
         v3 = 7;
         } else {  // this would be read and Execute
         v1 = 5;
         v2 = 5;
         v3 = 5;
         }
      String permissions = ""+(v0 * 1000) + (v1 * 100) + (v2 * 10) + v3+"";

      if (debug){
            System.out.println("Exit WindowsFile:getPermissions() - returning "+ permissions);
      }
      return permissions;
   } // end getPermissions

   public String getGroup() {
      return fileGroup;
   }

   public String getOwner() {
      return fileOwner;
   }

   public int setPermissions(String chmodValue) {
      return 0;
    }

   public int setGroup(String groupValue) {
      return 0;
    }

   public int setOwner(String ownerValue) {
      return 0;
    }

   public boolean isWritable() {
      return this.canWrite();
    }

   public int setWritable() {
       int rc = 0;
       // check the permissions
       if (!this.canWrite()) {
           boolean adjust4Platforms = false;

           boolean showLaunchMsg    = debug;
           boolean displayStdOut    = debug;
           boolean displayStdErr    = debug;

           Vector msgBuffer = new Vector();
           Vector logBuffer = new Vector();

           String[] cmd = { "attrib", "-r", absoluteFileName };
           ExecCmd exCmd = new ExecCmd(adjust4Platforms, showLaunchMsg);
           rc = exCmd.Execute(cmd, displayStdOut, displayStdErr, msgBuffer, logBuffer);

           if ( rc != 0 ) {
               System.out.println("attrib Command failure!");
           }
       }
       return rc;
   }

} // end of WindowsFile class
