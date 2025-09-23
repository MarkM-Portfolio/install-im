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
 * File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/delta/adminconfig/tasks/WPWsAdminTask.java, wps.base.fix, wps6.fix History 1.8, 11/12/04
 */
public class WPWsAdminTask extends Task {

   public final static String pgmVersion = "1.8" ;
   public final static String pgmUpdate = "11/12/04" ;
   private static WPWsAdmin wsAdmin = null;

   private void initWPWsAdmin() throws BuildException {

      if ( wsAdmin == null) {
         try {

            // Get the ant project's properties to pass to WPWsAdmin.
            Hashtable   projectPropertiesHash = project.getProperties();

            //project.getProperties returns a Hashtable, but we want a Properties object,
            // so we write the keys and values from the Hashtable into a Properties object. 
            Enumeration projectPropertiesKeys = projectPropertiesHash.keys();
            Properties  projectProperties     = new Properties();

            while (projectPropertiesKeys.hasMoreElements()) {
                // We know we started with a set of properties (in the Hashtable),
                // and keys/values for Properties are strings, 
                // so cast the keys/values from the HashTable accordingly.
                String key = (String) projectPropertiesKeys.nextElement();
                projectProperties.setProperty( key, (String) projectPropertiesHash.get( key ) );
            }

            // . . . and pass the Properties obj to WPWsadmin.
            wsAdmin.initProperties( projectProperties );

            wsAdmin = WPWsAdmin.isManagedNode() ? (WPWsAdmin)new WPWsAdminRemote() : (WPWsAdmin)new WPWsAdminLocal();
         } catch ( Exception e) {
            throw new BuildException( "WPWsAdminTask: ERROR: Unable to initialize WPWsAdmin - " + e.getMessage() );
         }
      }

   }

   private String  home;
   private String  appName             = null;
   private String  earFile             = null;
   private String  node                = null;
   private String  action              = null;
   private boolean doDebug             = false;

   private String  managedNodeProp     = null;

   private boolean clusterConfig       = false;
   private boolean dmgrConfig          = false;

   private boolean forceClusterConfig  = false;
   private boolean forceDmgrConfig     = false;

   private boolean failonerror         = false;

    public WPWsAdminTask() {
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
       if (home == null || home.trim().length() == 0) {
          throw new BuildException( "WPWsAdminTask: ERROR: invalid input. \"WpHome\" property cannot be empty or null." );
       }
       System.setProperty( "com.ibm.wps.home", home );
       initWPWsAdmin();

       int allOK = 0;

       if ( action.equals("ISMANAGEDNODE") ) {
          if (null == managedNodeProp ) {
             throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"isManagedNode\" requires managedNode property." );
          }
          // handle this here.  Since its a test of node type.
          project.setProperty( managedNodeProp, WPWsAdmin.isManagedNode() ? "true" : "false" );
          return;
       } else if ( action.equals("SYNCNODE") ) {
          if ( WPWsAdmin.isManagedNode() ) {
             allOK = ((WPWsAdminRemote)wsAdmin).syncNode();
          } else {
             // for local, just return allOK.
             allOK = WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS;
             //throw new BuildException( this.getClass().toString() + " ERROR: syncNode is only valid on Managed nodes." );
             //allOK = WPWsAdminErrorCodes.WPWSADMIN_ERROR_INVALID_ARGS;
          }
          if ( allOK != WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS ) {
             throw new BuildException( this.getClass().toString() + " ERROR: Error executing \"action\" (" + action + ")." );
          }
          return;
       } else if ( action.equals("SYNCACTIVENODES") ) {
          if ( WPWsAdmin.isManagedNode() ) {
             allOK = ((WPWsAdminRemote)wsAdmin).syncActiveNodes();
          } else {
             allOK = WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS;
             //throw new BuildException( this.getClass().toString() + " ERROR: syncNode is only valid on Managed nodes." );
             //allOK = WPWsAdminErrorCodes.WPWSADMIN_ERROR_INVALID_ARGS;
          }
          if ( allOK != WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS ) {
             throw new BuildException( this.getClass().toString() + " ERROR: Error executing \"action\" (" + action + ")." );
          }
          return;
       } else {
          if ( null == appName              ||
               appName.trim().length() == 0 ||
               null == action               ||
               action.trim().length() == 0  ||
               null == earFile              ||
               earFile.trim().length() == 0 ) 
          {
              throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"action\", \"appName\" and \"earFile\" properties cannot be empty or null." );
          }
       }

       wsAdmin.setTargetFile( earFile );
       wsAdmin.setAppName( appName );
       wsAdmin.DEBUG_ENABLED = doDebug;

       if ( managedNodeProp != null ) {
          project.setProperty( managedNodeProp, WPWsAdmin.isManagedNode() ? "true" : "false" );
       }

       if ( action.equals("EXTRACT") ) {
          allOK = wsAdmin.extractEar();
       } else if ( action.equals("REDEPLOY") ) {
          allOK = wsAdmin.redeployEar();
       } else if ( action.equals("INSTALL") ) {
          throw new BuildException( this.getClass().toString() + " ERROR: \"action\" (" + action + ") is not yet supported." );
       } else if ( action.equals("UNINSTALL") ) {
          throw new BuildException( this.getClass().toString() + " ERROR: \"action\" (" + action + ") is not yet supported." );
       } else if ( action.equals("TEST") ) {
          allOK = wsAdmin.runTest();
       } else{
          throw new BuildException( this.getClass().toString() + " ERROR: invalid input. Unknown \"action\" (" + action + ")." );
       }


       if ( allOK != WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS ) {
          throw new BuildException( this.getClass().toString() + " ERROR: Error executing \"action\" (" + action + ")." );
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

    /**
	 * @param appName  the appName to set
	 * @uml.property  name="appName"
	 */
    public void setAppName(String s) {
        appName = s;
    }

    /**
	 * @param earFile  the earFile to set
	 * @uml.property  name="earFile"
	 */
    public void setEarFile(String s) {
        earFile = s;
    }

    /**
	 * @param action  the action to set
	 * @uml.property  name="action"
	 */
    public void setAction(String s) {
        action = s.toUpperCase();
    }

    public void setClusterConfiguration( boolean c ) {
       clusterConfig = c;
       forceClusterConfig = true;
    }

    public void setDeployManagerConfiguration( boolean c ) {
       dmgrConfig = c;
       forceDmgrConfig = true;
    }

    public void setManagedNode( String s) {
        managedNodeProp = s;
    }

    public void setDebug( boolean d ) {
        doDebug = d;
    }

}
