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

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.AdminConstants;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementFactory;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.AppNotification;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.filetransfer.client.FileTransferClient;
import com.ibm.websphere.management.filetransfer.client.FileTransferOptions;
import com.ibm.websphere.management.filetransfer.client.TransferFailedException;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import com.ibm.websphere.update.util.was.WASNodeType;
import com.ibm.ws.management.fileservice.FileTransferFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;

/**
 * File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdmin.java, wps.base.fix, wps5.fix History 1.21, 7/15/05
 */
public abstract class WPWsAdmin  implements WPWsAdminErrorCodes {
   public final static String pgmVersion = "1.21" ;
   public final static String pgmUpdate = "7/15/05" ;


   protected AppManagement                 TheMgr        =  null;
   protected WPWsAdminConfigHelper         configHelper  = null;
   protected String                        workspace     = "wps_wpwsadmin_"+Long.toHexString(System.currentTimeMillis());
             WPWsAdminNotificationListener listener       = null;

   protected String        appName             = null;
   protected String        targetName          = null;
   protected File          targetFile          = null;

   /**
 * @uml.property  name="moduleInfo"
 * @uml.associationEnd  multiplicity="(0 -1)"
 */
protected ModuleInfo[]  moduleInfo          = null;


   public static boolean isManagedNode() {
      initProperties();
      return !WASNodeType.isStandaloneCell();
   }

   private static final int ACTION_UNKNOWN   = 0;
   private static final int ACTION_REDEPLOY  = 1;
   private static final int ACTION_EXTRACT   = 2;
   private static final int ACTION_INSTALL   = 3;            // Not used 
   private static final int ACTION_UNINSTALL = 4;            // Not Used

   private static final int ACTION_TEST      = 999;

   public static void main (String args[]) {

      WPWsAdmin wsAdmin = isManagedNode() ? ((WPWsAdmin)new WPWsAdminRemote()) : ((WPWsAdmin)new WPWsAdminLocal());
      int errorCode = WPWSADMIN_ERROR_SUCCESS;

      int    action       = ACTION_UNKNOWN;
      String appName      = null;
      String earFile      = null;
      boolean enableDebug = false;

      try {
         int idx = 0;
         while ( idx < args.length ) {
            if ( args[idx].equals( "-redeploy" ) ) {
               action = ACTION_REDEPLOY;
            } else if ( args[idx].equals( "-export" ) ) {
               action = ACTION_EXTRACT;
            } else if ( args[idx].equals( "-appname" ) ) {
               appName = args[++idx];
            } else if ( args[idx].equals( "-earfile" ) ) {
               earFile = args[++idx];
            } else if ( args[idx].equals( "-test" ) ) {
               action = ACTION_TEST;
            } else if ( args[idx].equals( "-debug" ) ) {
               enableDebug = true;
            } else {
               wsAdmin.logError( "Unknown argument : " + args[idx] );
            }
            idx++;
         }
      } catch ( Exception e) {
         wsAdmin.logError("Error processing arguments : " + e.getMessage(), e );
      }

      // <d78048> set WAS_HOME-based properties here instead of the .bat file to shorten shell command on Windows
      System.setProperty( "was.install.root", WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ) );  
      
      System.setProperty( "com.ibm.itp.location", WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) + File.separator + "bin" );

      //boolean allOK = false;
      if (action  == ACTION_UNKNOWN ||
          earFile == null ||
          appName == null ) {
         errorCode = WPWSADMIN_ERROR_INVALID_ARGS;
         wsAdmin.logError("Incorrect number of arguments, must specify -appname <appname> and -earfile <earfile>, and one of -redeploy or -export." );
      } else {
          if (enableDebug) {
              wsAdmin.DEBUG_ENABLED = true;
          }
          wsAdmin.setTargetFile( earFile );
          wsAdmin.setAppName( appName );

          switch (action) {
             case ACTION_EXTRACT:
                errorCode = wsAdmin.extractEar( earFile, appName );
                break;
             case ACTION_REDEPLOY:
                errorCode = wsAdmin.redeployEar( earFile, appName );
                break;
             case ACTION_TEST:
                errorCode = wsAdmin.runTest();
                break;
             default:
                errorCode = WPWSADMIN_ERROR_ACTION_UNK;
                // unknown;
                break;

          }
      }        

      System.exit( errorCode );
   }
 


   // Answer a new wsadmin command on the specified
   //
   // Processing messages are placed in the messages buffer.
   //
   // Processing errors are placed in the errors buffer.

   public WPWsAdmin() {
      initProperties();
   }

   public boolean DEBUG_ENABLED = false;
   private static final java.text.DateFormat DATE_FMT = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

   void debug( String txt ) {
      //StringBuffer buffer = DATE_FMT.format( new Date() );
      //buffer.append( " -- " + txt );
      //System.out.println( buffer.toString() );
      if (DEBUG_ENABLED) System.out.println( DATE_FMT.format( new java.util.Date() ) + " -- " + txt );
   }


   public void log(String text) {
      //getMessages().append(text + "\n" );
      System.out.println( text );
   }

   public void logError(String text) {
      System.err.println( text );
   }

   public void logError(String text, Exception e) {
      System.err.println( text );
      e.printStackTrace( System.err );
   }

   public void setTargetFile( String earFileName ) {
      this.targetFile = new File( earFileName );
      this.targetName = targetFile.getName();
		// set the application file so the configuration service will retrieve the right
		// deployment information
      if (configHelper != null) {
         configHelper.setTargetFile( targetFile );
      }
   }

   /**
 * @param appName  the appName to set
 * @uml.property  name="appName"
 */
public void setAppName( String appName ) {
      this.appName = appName;

		// set the application name so the configuration service will retrieve the right
		// deployment information
      if (configHelper != null) {
         configHelper.setAppName( appName );
      }
   }

   protected abstract AppManagement createApplicationManager() throws WPWsAdminException;

   protected AppManagement getApplicationManager() throws WPWsAdminException {
      if (TheMgr == null) {
         synchronized ( this ) {
            // Check again just incase, may have been gotten while syncing
            if ( TheMgr == null ) {
               TheMgr = createApplicationManager();
            }

         }
      }
      return TheMgr;
   }


   public int initAdmin() {
      return initAdmin( false );
   }

   public int initAdmin( boolean initConfigService ) {
      int errorCode = WPWSADMIN_ERROR_SUCCESS;
      try {
         getApplicationManager();
         if ( initConfigService ) {
            errorCode = getConfigService();
         }
      } catch ( WPWsAdminException e) {
         logError( "Unable to establish connection with WAS AdminClient : " + e.getMessage(), e );
         return e.getErrorCode();
      }
      return errorCode;
   }

   //public abstract int initAdmin()

   public abstract int extractEar();

   public abstract int extractEar( String toFile, String appName );

   public abstract int redeployEar( );

   public abstract int redeployEar( String fromFile, String appName );


   protected void saveModuleInfo() throws WPWsAdminException {
      try {
         Hashtable props = buildAdminProperties( false );
         AppDeploymentTask moduleTask = (AppDeploymentTask)TheMgr.listModules( appName, props, workspace );
         String[][] moduleTasks = moduleTask.getTaskData();
         if (moduleTasks != null && moduleTasks.length > 1) {
            moduleInfo = new ModuleInfo[moduleTasks.length-1];
            for ( int m=1; m<moduleTasks.length; m++ ) {
               int mindx = m-1;
               moduleInfo[mindx] = new ModuleInfo();
               moduleInfo[mindx].uri = moduleTasks[m][1];
               moduleInfo[mindx].info =  TheMgr.getModuleInfo( appName, props, moduleInfo[mindx].uri, workspace );
             }

          }
      } catch ( Exception e) {
         throw new WPWsAdminException( e.getMessage() );
      }
   }

   protected void restoreModuleInfo() throws WPWsAdminException {
      try {
         Hashtable props = buildAdminProperties( false );
         if (moduleInfo != null) {
            for (int i=0; i<moduleInfo.length; i++) {
               TheMgr.setModuleInfo( appName, props, moduleInfo[i].uri, workspace, moduleInfo[i].info );
            }
         }
      } catch ( Exception e) {
         throw new WPWsAdminException( e.getMessage() );
      }
   }

   public int runTest() {
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

      String procType = configHelper.getProcessType();
      log( "ProcessType : " + procType );


      if ( !configHelper.isApplicationDeployed() ) {
         log("Requested Applicaiton " + appName + " is not deployed." );
         return WPWSADMIN_ERROR_APPMISSING;
      }


      Hashtable props = buildAdminProperties( true );
/*
      log( "Redeployment Properties" );
      log( "-----------------------------------------------------------------------------------------------------------" );
      Enumeration enum = props.keys();
      while ( enum.hasMoreElements() ) {
         String thisKey = (String)enum.nextElement();
         log( "    " + thisKey + "  : " + props.get( thisKey ) );
      }
      log( "-----------------------------------------------------------------------------------------------------------" );

      log( "WPConfig Properties" );
      log( "-----------------------------------------------------------------------------------------------------------" );
      enum = ((Properties)WPConfig.WP_PROPS).propertyNames();
      while ( enum.hasMoreElements() ) {
         String thisKey = (String)enum.nextElement();
         log( "    " + thisKey + "  : " + WPConfig.WP_PROPS.getProperty( thisKey ) );
      }
      log( "-----------------------------------------------------------------------------------------------------------" );

*/ 

      /*
      configHelper.runTest2();
      configHelper.runTest4();
      */

      ModuleInfo[] moduleInfo = null;

      try {
         props = buildAdminProperties( false );
         AppDeploymentTask moduleTask = (AppDeploymentTask)TheMgr.listModules( appName, props, workspace );
         String[][] moduleTasks = moduleTask.getTaskData();
         if (moduleInfo != null && moduleInfo.length > 1) {
            moduleInfo = new ModuleInfo[moduleTasks.length-1];
            for ( int m=1; m<moduleTasks.length; m++ ) {
               String thisModule = moduleTasks[m][0];
               String thisURI = moduleTasks[m][1];
               Vector info =  TheMgr.getModuleInfo( appName, props, thisURI, workspace );
               moduleInfo[m-1] = new ModuleInfo( thisURI, info );

               AppDeploymentTask appTask = null;
               for ( int i=0; i<info.size(); i++) {
                  appTask = (AppDeploymentTask)info.get(i);
                  String[][] taskData = appTask.getTaskData();
                  System.out.println("Task " + appTask.getName() + "(" + i + ")" + " for " + thisModule );
                  if (taskData != null) {
                     for ( int j=0; j<taskData.length; j++ ) {
                        for (int k=0;k<taskData[j].length; k++) {
                           System.out.println( "   Data["+j+"]["+k+"]" + taskData[j][k] );
                        }
                     }
                  }
               }
             }

          }

      } catch ( Exception e) {
         e.printStackTrace();
      }


      return status;
   }



   protected  Hashtable buildAdminProperties( boolean forDeploy ) {
      return configHelper.buildAdminProperties( forDeploy );
   }


   protected abstract  int createConfigHelper();

   protected int getConfigService() {
      if (configHelper != null ) { return WPWSADMIN_ERROR_SUCCESS; } // only init once....

      configHelper = null;
      int configRC = WPWSADMIN_ERROR_SUCCESS;
      try {
         //Create a ConfigService to store config changes to
         configRC  = createConfigHelper();

         configHelper.setAppName( appName );
         configHelper.setTargetFile( targetFile );
      } catch ( Exception e) {
         logError( "Error acquiring ConfigService: " + e.getMessage(), e );
         return WPWSADMIN_ERROR_CFG_GENERAL;
      }
      return configRC;

   }

   final static int MIN_REQUEST_TIMEOUT = 60 * 45;  // secs value --> 45 minutes (45mins * 60secs)
   final static int NO_REQUEST_TIMEOUT  = 0;        // no timeout value

   private static void setMinTimeout( String prop ) {

      // We need to make sure the SOAP timeout is very large
      int iTime = MIN_REQUEST_TIMEOUT;
      String timeout = WPConfig.getProperty( prop );
      if (timeout != null) {
         try { iTime = Integer.parseInt( timeout); } catch ( Exception e ) {}
         // Make sure timeout is at least MIN_REQUEST 
         if (iTime != NO_REQUEST_TIMEOUT && iTime < MIN_REQUEST_TIMEOUT  ) {
            iTime = MIN_REQUEST_TIMEOUT;
         }
      }
      // Always update, incase value came form pui.configprops.
      System.setProperty( prop, String.valueOf( iTime ) );
   }

   /**
    *   call this method from ant taskdefs if there
    *   are properties to pass from the project . . .
    *   we permit this to be called multiple times, there could
    *   be more than one set of properties to import.
    */
   public static synchronized void initProperties( Properties p) {
       if ( null != p ) {
           WPConfig.initialize( p );
       }
       initProperties();
   }


   private static boolean initialized = false;
   private static synchronized void initProperties() {
      if (initialized) return;

      initialized = true;

      String wpHome = System.getProperty( "com.ibm.wps.home" );
      WPConfig.initialize( wpHome );

      // bkb 061105. move this here.

      // Setup WAS_PROD_HOME to WAS_HOME this is needed so we always have the real WAS Home ( PROD_DATA on iSeries )
      WPConfig.setProperty( WPConstants.PROP_WAS_PROD_HOME, WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) );
      if (PlatformUtils.isISeries()) {
         // On iSeries we need to remap WAS_USER_HOME to WAS Home and WAS_HOMe to WAS_PROD_HOME
         WPConfig.setProperty( WPConstants.PROP_WAS_HOME, WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ) );
         WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, WPConfig.getProperty( WPConstants.PROP_WAS_USER_HOME ) );
      }

      String userInstallRoot = System.getProperty( WPConstants.PROP_USER_INSTALL_ROOT );
      if ( (userInstallRoot == null || userInstallRoot.equals("")) && !PlatformUtils.isISeries() ) {
         WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) );
      } else {
         WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, userInstallRoot );
      }
      String wasHome = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT );
      System.setProperty( "server.root", WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ) );
      
      String cellName = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
      String nodeName = WPConfig.getProperty( WPConstants.PROP_WAS_NODE );
      //These aren't in wpconfig.properties when using WAS 6, so get them from command line
      //   since they're in setupCmdLine (which is run as part of wpwsadmin script)
      if ( cellName == null || cellName.length() == 0 ) {
         cellName = System.getProperty("local.cell");
         WPConfig.setProperty( WPConstants.PROP_WAS_CELL, cellName );
      }
      if ( nodeName == null || nodeName.length() == 0 ) {
         nodeName = System.getProperty("local.node");
         WPConfig.setProperty( WPConstants.PROP_WAS_NODE, nodeName );
      }
      
      String wsadminprops = System.getProperty( "com.ibm.ws.scripting.wsadminprops" );
      if (wsadminprops == null || wsadminprops.length() == 0 ) {
          //if (PlatformUtils.isISeries()) {
          //    wsadminprops = WPConfig.getProperty( WPConstants.PROP_WAS_USER_HOME ) + File.separator + "properties" + File.separator + "wsadmin.properties";
          //}
          //else {
              wsadminprops = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ) + File.separator + "properties" + File.separator + "wsadmin.properties";
          //}
      }
      WPConfig.loadConfiguration( wsadminprops );  // bring in the wsadmin properties.

      // Also bring in soap.client.
      String soapprops = System.getProperty( "com.ibm.SOAP.ConfigURL" );
      if (soapprops == null || soapprops.length() == 0) {
          if (PlatformUtils.isISeries()) {
              soapprops = WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) + File.separator + "properties" + File.separator + "soap.client.props";
          }
          else{
              soapprops = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ) + File.separator + "properties" + File.separator + "soap.client.props";
          }
      }

      try {
         URL url = new URL( soapprops );
         System.getProperties().load( url.openStream() );
      } catch ( Exception e) {}

      try {
         URL url = new URL( soapprops );
         WPConfig.loadConfiguration( url.openStream() );  // bring in the soap.client properties.
      } catch ( Exception e) {}


      // Also bring in sas.client (rmi/corba)
      soapprops = System.getProperty( "com.ibm.CORBA.ConfigURL" );
      if (soapprops == null || soapprops.length() == 0) {
          if (PlatformUtils.isISeries()) {
              soapprops = WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) + File.separator + "properties" + File.separator + "sas.client.props";
          }
          else {
              soapprops = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ) + File.separator + "properties" + File.separator + "sas.client.props";
          }
      }

      try {
         URL url = new URL( soapprops );
         System.getProperties().load( url.openStream() );
      } catch ( Exception e) {}
      try {
         URL url = new URL( soapprops );
         WPConfig.loadConfiguration( url.openStream() );  // bring in the sas.client properties.
      } catch ( Exception e) {}

      String cfgprops = System.getProperty( "com.ibm.wp.pui.configprops" );
      if ( !(cfgprops == null || cfgprops.length() == 0) ) {
         WPConfig.loadConfiguration( cfgprops );  // finally load any override props from the pui cmd line
      }

      // We need to make sure the SOAP timeout is very large
      setMinTimeout( "com.ibm.SOAP.requestTimeout" );

      // We need to make sure the RMI/CORBA timeout is very large
      setMinTimeout( "com.ibm.CORBA.requestTimeout" );


      // We will now read the deploymentService.properties to get the read Server/cluster name
      Properties deployProps = new Properties();
      try {
         FileInputStream in = new FileInputStream(  wpHome + File.separator +
                                                    "shared" + File.separator +
                                                    "app" + File.separator +
                                                    "config" + File.separator +
                                                    "services" + File.separator +
                                                    "DeploymentService.properties" );
         deployProps.load( in );
         try { in.close(); } catch ( Exception e ) {}
         String serverName = deployProps.getProperty( "wps.appserver.name" );
         if ( serverName != null ) {
            WPConfig.setProperty( WPConstants.PROP_WP_SERVER, serverName );
         }

      } catch ( Exception e) {
         System.err.println("Unable to read DeploymentSerice.properties under wp.home" );
         e.printStackTrace( System.err );
      }

   }


   protected class WPWsAdminNotificationListener implements NotificationListener, AppNotification.Listener {

      static final int STATUS_IDLE    = -1;
      static final int STATUS_RUNNING =  0;
      static final int STATUS_OK      =  1;
      static final int STATUS_FAILURE =  2;
      static final int STATUS_TIMEOUT =  3;

              ObjectName on;                        // This may be null
      private Object     lock      = new Object();
      private int        status;

      WPWsAdminNotificationListener( ObjectName on ) {
         status = STATUS_IDLE;
         this.on = on;
      }


      public void appEventReceived(AppNotification ev ) {
         //long now = System.currentTimeMillis();
         //System.out.println( "Event received at " + now + "(" + new java.util.Date( now ) + ")" );
         debug( "AppNotification event received: " + ev );
         debug( "   taskName: " + ev.taskName + "  SubTask: " + ev.subtaskName  + "  Status: " + ev.taskStatus );
         if (ev.taskName.equals(AppNotification.INSTALL) && (ev.taskStatus.equals(AppNotification.STATUS_COMPLETED))) {
            log( "Redeployment Status: " + ev.message );
            log( "   Task: " + ev.taskName + " Status : " + ev.taskStatus );
            log( "   SubTask: " + ev.subtaskName + " Status : " + ev.subtaskStatus );
            log( "Redeployment complete" );
            synchronized (lock) {
               status = STATUS_OK;
               lock.notifyAll();
            }
         } else if (ev.taskName.equals(AppNotification.INSTALL) && (ev.taskStatus.equals(AppNotification.STATUS_FAILED))) {
            logError( "Redeployment failed: " + ev.message );
            logError( "   Task: " + ev.taskName + " Status : " + ev.taskStatus );
            logError( "   SubTask: " + ev.subtaskName + " Status : " + ev.subtaskStatus );
            synchronized (lock) {
               status = STATUS_FAILURE;
               lock.notifyAll();
            }
         } else if (ev.taskName.equals(AppNotification.INSTALL) && (ev.taskStatus.equals(AppNotification.STATUS_INPROGRESS))) {
            logError( "Redeployment Status: " + ev.message );
            logError( "   Task: " + ev.taskName + " Status : " + ev.taskStatus );
            logError( "   SubTask: " + ev.subtaskName + " Status : " + ev.subtaskStatus );
         } else {
            logError( "Redeployment Status: (Non - Install)" + ev.message );
            logError( "   Task: " + ev.taskName + " Status : " + ev.taskStatus );
            logError( "   SubTask: " + ev.subtaskName + " Status : " + ev.subtaskStatus );

         }

      }



      //receive notifications
      public void handleNotification(Notification notf, Object handback) {
         AppNotification ev = (AppNotification)notf.getUserData();

         //String type = notf.getType();
         if (ev != null) {
            appEventReceived( ev );
         }

      }

      protected int waitForNotification(int timeout) {

         synchronized (lock) {
            if ( status == STATUS_IDLE ) {
               long endTime = System.currentTimeMillis() + ( timeout * 1000 );
               long timeLeft = endTime;
               status = STATUS_RUNNING;
               while ( (status == STATUS_RUNNING) && (timeLeft > 0) ) {
                   try {
                       lock.wait(timeLeft*1000);    // <-- timeout, the notification handler resets the flag if it has been called
                   } catch (InterruptedException ex) {
                       /* we can safely ignore this exception because there is no
                         * other reason to be notified than a timeout or the release of 
                         * a lock
                         */
                   }
                   timeLeft = endTime - System.currentTimeMillis(); // See hoe much longer we may need to wait
                }
                if (status == STATUS_RUNNING) {
                   status = STATUS_TIMEOUT;
                }
            }
          }

          return status;
      }

   }


   private static class ModuleInfo {
      String uri;
      Vector info;

      ModuleInfo() {
      }  

      ModuleInfo(String uri, Vector info ) {
         this.uri = uri;
         this.info = info;
      }

   };
}

