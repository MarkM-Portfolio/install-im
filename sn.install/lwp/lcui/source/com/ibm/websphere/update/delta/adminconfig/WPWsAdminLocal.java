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

package com.ibm.websphere.update.delta.adminconfig;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

// NOTE: the WPWsAdmintask jar only include the following file from util, and the entire adminconfig package.
//   if additional classes are needed be sure to add the classes to the jar
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.AdminConstants;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.AppNotification;
import com.ibm.websphere.management.application.AppManagementFactory;
import com.ibm.websphere.management.application.AppManagementBaseFactory;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.ws.management.fileservice.FileTransferFactory;
import com.ibm.websphere.management.filetransfer.client.FileTransferClient;
import com.ibm.websphere.management.filetransfer.client.FileTransferOptions;
import com.ibm.websphere.management.filetransfer.client.TransferFailedException;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.NotificationFilterSupport;
import javax.management.InstanceNotFoundException;

/**********************************************************************************************************
 * 
 * 
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdminLocal.java, wps.base.fix, wps5.fix
 * 
 * History 1.6, 3/23/04
 *
 **********************************************************************************************************/
public class WPWsAdminLocal extends WPWsAdmin {
   public final static String pgmVersion = "1.6" ;
   public final static String pgmUpdate = "3/23/04" ;

                                              

   // Answer a new wsadmin command on the specified
   //
   // Processing messages are placed in the messages buffer.
   //
   // Processing errors are placed in the errors buffer.

   public WPWsAdminLocal() {
      super();
      // Setup System properties needed by AppMgmt
      //System.setProperty( "user.install.root", WPConfig.getProperty( WPConstants.PROP_WAS_USER_HOME ) );
      System.setProperty( "was.install.root", WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ) );
      System.setProperty( "was.repository.root", WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ) + File.separator + "config" );
      System.setProperty( "server.root", WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ) );
      System.setProperty( "local.cell", WPConfig.getProperty( WPConstants.PROP_WAS_CELL ) );
      System.setProperty( "local.node",WPConfig.getProperty( WPConstants.PROP_WAS_NODE ) );

   }

   protected AppManagement createApplicationManager() throws WPWsAdminException {
      return AppManagementBaseFactory.createLocalAppManagementImpl();
   }


   public int extractEar() {
      return extractEarFile( targetFile.getAbsolutePath(), appName );
   }  

   public int extractEar( String toFile, String appName ) {
      return extractEarFile( toFile, appName );
   }

   protected int extractEarFile( String remoteFile, String appName ) {
      try {
         getApplicationManager();
      } catch ( WPWsAdminException e) {
         logError( "Unable to establish connection with WAS AdminClient : " + e.getMessage(), e );
         return e.getErrorCode();
      }

      int status = getConfigService();
      if ( status != WPWSADMIN_ERROR_SUCCESS ) {
         return status;
      }


      if ( !configHelper.isApplicationDeployed() ) {
         return WPWSADMIN_ERROR_APPMISSING;
      }
      try {
         // Add our locale to properties.   //d62430
         Hashtable props = new Hashtable();
         props.put (AppConstants.APPDEPL_LOCALE, Locale.getDefault());
         // Ask Dm to export app
         TheMgr.exportApplication( appName, remoteFile, props, workspace );
         // Now need to wait until complete
      } catch ( Exception e) {
         logError( "Error exporting " + appName + ": " + e.getMessage(), e );
         return WPWSADMIN_ERROR_EXPORT_APP;
      }

      return WPWSADMIN_ERROR_SUCCESS;
   }

   public int redeployEar( ) {
      return redeployEarFile( targetFile.getAbsolutePath(), appName );
   }

   public int redeployEar( String fromFile, String appName ) {
      return redeployEarFile( fromFile, appName );
   }

   protected int redeployEarFile( String fromFile, String appName ) {
      int status = 0;

      try {
         getApplicationManager();
      } catch ( WPWsAdminException e) {
         logError( "Unable to establish connection with WAS AdminClient : " + e.getMessage(), e );
         return e.getErrorCode();
      }

      status = getConfigService();
      if ( status != WPWSADMIN_ERROR_SUCCESS ) {
         return status;
      }

      if ( !configHelper.isApplicationDeployed() ) {
         return WPWSADMIN_ERROR_APPMISSING;
      }
		// set the application name being deployed so the configuration service will retrieve the right
		// deployment information
		setAppName(appName);

      // the redeploy.
      try {

         createNotificationListener();
         Hashtable props = buildAdminProperties( true );
         //props.setProperty( AppConstants.APPDEPL_CELL, WPConfig.getProperty( WPConstants.PROP_WAS_CELL ) );
         // Ask Dm to redeploy the app
         // For local mode we don't want the file deleted.
         props.put (AppConstants.APPDEPL_DELETE_SRC_EAR,  Boolean.FALSE);
         //saveModuleInfo();
         log("Redeploying remote file " + fromFile + " for app " + appName );
         TheMgr.redeployApplicationLocal( fromFile, appName, props, listener, workspace );
         // Now need to wait until complete
         status = waitTilComplete();
         if (status == WPWsAdminNotificationListener.STATUS_OK) {
            //restoreModuleInfo();
            configHelper.save( true );
            log("Successfully saved configuration changes." );
         } else {
            configHelper.discard();
         }
      } catch ( WPWsAdminException e) {
         logError( "Error exporting " + appName + ": " + e.getMessage(), e );
         return e.getErrorCode();
      } catch ( ConnectorException e) {
         logError( "Error exporting " + appName + ": " + e.getMessage(), e );
         return WPWSADMIN_ERROR_CONNECT;
      } catch ( WPWsAdminListenerException e) {
         logError( "Error exporting " + appName + ": " + e.getMessage(), e );
         return WPWSADMIN_ERROR_REDEPLOY;
      } catch ( AdminException e) {
         logError( "Error exporting " + appName + ": " + e.getMessage(), e );
         return WPWSADMIN_ERROR_REDEPLOY;
      }

      return (status == WPWsAdminNotificationListener.STATUS_OK) ? WPWSADMIN_ERROR_SUCCESS : WPWSADMIN_ERROR_REDEPLOY;
   }


   private int  waitTilComplete() {
      return listener.waitForNotification( 10 * 60 );  // 10 minute timeout
   }

   private void  createNotificationListener( )  throws WPWsAdminListenerException {
      listener = new WPWsAdminNotificationListener( null );
   }

   protected int createConfigHelper() {
      configHelper = new WPWsAdminConfigHelper( null, workspace );
      log( "appName: " + appName + "    targetFile: " + targetFile + "    ProcessType: Local" );
      return WPWSADMIN_ERROR_SUCCESS;
   }

}

