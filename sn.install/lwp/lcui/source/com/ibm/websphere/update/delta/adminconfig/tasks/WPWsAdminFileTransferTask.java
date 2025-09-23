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

package com.ibm.websphere.update.delta.adminconfig.tasks;

import com.ibm.websphere.update.delta.adminconfig.*;
import java.io.File;
import java.util.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.*;

/**
 * File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/delta/adminconfig/tasks/WPWsAdminFileTransferTask.java, wps.base.fix, wps6.fix History 1.3, 1/29/04
 */
public class WPWsAdminFileTransferTask extends Task { 

   public final static String pgmVersion = "1.3" ;
   public final static String pgmUpdate = "1/29/04" ;
   private static WPWsAdminRemote wsAdmin = null;

   private static  void initWPWsAdmin( String wpHome ) throws BuildException {

      if ( wsAdmin == null) {
         if (wpHome == null || wpHome.trim().length() == 0) {
            throw new BuildException( "WPWsADminFileTransferTask: ERROR: invalid input. \"WpHome\" property cannot be empty or null." );
         }
         System.setProperty( "com.ibm.wps.home", wpHome );
         try {
            // FileTransfer can only be used w/ Remote.  May want to ensure its a DMgr controlled Node
            wsAdmin = new WPWsAdminRemote();
            if ( wsAdmin.initAdmin( true ) != WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS ) {
               throw new BuildException( "WPWsADminFileTransfer: ERROR: Unable to initialize WPWsAdmin." );
            }
         } catch ( Exception e) {
            throw new BuildException( "WPWsADminFileTransfer: ERROR: Unable to initialize WPWsAdmin - " + e.getMessage() );
         }
      }
   }

   private String  home;
   private String  localFile           = null;
   private String  remoteFile          = null;
   private String  action              = null;
   private String  resultFileProp      = null;
   private boolean deleteRemote        = false;

   private boolean failonerror         = false;

    public WPWsAdminFileTransferTask() {
        failonerror = false;
        // Default to property setting.
        //System.out.println( this.getClass().getName() + "::WPHistoryConfigTask : " + project );
        //home = project.getProperty( "WpsInstallLocation" );
        //System.out.println( this.getClass().getName() + "::WPHistoryConfigTask : " + home );
    }

    public void execute() throws BuildException {
       if (home == null || home.trim().length() == 0) {
          // Default to property setting.
          home = project.getProperty( "WpsInstallLocation" );
       }

       if ( null == action || action.trim().length() == 0 ) {
          throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"action\" cannot be empty or null." );
       } else if (( action.equals( "UPLOAD" ) || action.equals("DOWNLOAD") ) &&
                  ( null == localFile || localFile.trim().length() == 0 ) ) {
           throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"localFile\" cannot be empty or null for " + action + "." );
       }

       // Make sure we initialize WSAdmin.
       initWPWsAdmin( home );


       boolean allOK = true;
       String resultFile = null;
       if ( action.equals("UPLOAD") ) {
          wsAdmin.setTargetFile( localFile );
          resultFile = wsAdmin.uploadFile( deleteRemote );
       } else if ( action.equals("DOWNLOAD") ) {
          if ( remoteFile == null ) {
             throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"remoteFile\" property cannot be empty or null for a download operation." );
          }
          wsAdmin.setTargetFile( localFile );
          File fileResultFile = wsAdmin.downloadFile( remoteFile, deleteRemote );
          if (fileResultFile != null) resultFile = fileResultFile.getAbsolutePath();
       } else if ( action.equals("GETREMOTELOCATION") ) {
          if (resultFileProp == null) {
             throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"resultFile\" property cannot be empty or null for getRemoteLocation operation." );
          }
          resultFile = wsAdmin.getRemoteFileLocation();
       } else{
          throw new BuildException( this.getClass().toString() + " ERROR: invalid input. Unknown \"action\" (" + action + ")." );
       }

       if ( resultFile == null ) {
          throw new BuildException( this.getClass().toString() + " ERROR: Error executing \"action\" (" + action + ")." );
       }
       if (resultFileProp != null) {
          project.setProperty( resultFileProp, resultFile );
       }

    }

    public void setWPHome(String s) {
        home = s;
    }

    /**
	 * @param failonerror  the failonerror to set
	 * @uml.property  name="failonerror"
	 */
    public void setFailonerror(boolean flag) {
        failonerror = flag;
    }

    public void setResultFile(String s) {
        resultFileProp = s;
    }

    /**
	 * @param localFile  the localFile to set
	 * @uml.property  name="localFile"
	 */
    public void setLocalFile(String s) {
        localFile = s;
    }

    /**
	 * @param remoteFile  the remoteFile to set
	 * @uml.property  name="remoteFile"
	 */
    public void setRemoteFile(String s) {
        remoteFile = s;
    }

    /**
	 * @param action  the action to set
	 * @uml.property  name="action"
	 */
    public void setAction(String s) {
        action = s.toUpperCase();
    }

    public void setDeleteRemoteFile( boolean d ) {
       deleteRemote = d;
    }

}