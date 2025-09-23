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
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.filetransfer.client.FileTransferClient;
import com.ibm.websphere.management.filetransfer.client.FileTransferOptions;
import com.ibm.websphere.management.filetransfer.client.TransferFailedException;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import com.ibm.ws.management.fileservice.FileTransferFactory;
import com.ibm.ws.util.ImplFactory;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.Vector;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;

/**
 * wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdminRemote.java, wps.base.fix, wps6.fix History 1.3, 11/12/04
 */
public class WPWsAdminRemote extends WPWsAdmin {
   public final static String pgmVersion = "1.3" ;
   public final static String pgmUpdate = "11/12/04" ;



   private   AdminClient   TheClient           = null;

   private   boolean       dMgrProcess         = true;
                                              
   private   boolean       clusterConfig       = false;
   private   boolean       dmgrConfig          = false;

   private   boolean       forceClusterConfig  = false;
   private   boolean       forceDmgrConfig     = false;
   

   WPWsAdminNotificationListener listener = null;


   // Answer a new wsadmin command on the specified
   //
   // Processing messages are placed in the messages buffer.
   //
   // Processing errors are placed in the errors buffer.

   public WPWsAdminRemote() {
      super();
   }

   protected AppManagement createApplicationManager() throws WPWsAdminException {
      Properties props = new Properties();

      String connectType = WPConfig.getProperty( "com.ibm.ws.scripting.connectionType" );
      if(connectType == null) connectType = "SOAP";

      if ( connectType.equals( "SOAP" ) ) {
         props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
      } else if ( connectType.equals( "RMI" ) ) {
         props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_RMI);
      } else if ( connectType.equals( "JMS" ) ) {
         props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_JMS);
      } else if ( connectType.equals( "NONE" ) ) {
         // Add nothing as conn type
      } else {
         //logError( "Unsupported connection Type for AdminClient: " + connectType );
         throw new WPWsAdminException( "Unsupported connection Type for AdmiClient: " + connectType, WPWSADMIN_ERROR_CONNECT_INV );
      }


      String dmServer   = WPConfig.getProperty( WPConstants.PROP_WAS_DM_SERVER );
      if (dmServer == null) {
         dmServer = WPConfig.getProperty( "com.ibm.ws.scripting.host" );
         if (dmServer == null ) {
            try {
                dmServer = InetAddress.getLocalHost().getHostName();  // try local addr
            } catch(UnknownHostException unknownhostexception) {
               dmServer = "127.0.0.1";
            }
         }
      }
      props.setProperty(AdminClient.CONNECTOR_HOST, dmServer );

      String dmPort     = WPConfig.getProperty( WPConstants.PROP_WAS_DM_PORT );
      if (dmPort == null) {
         dmPort = WPConfig.getProperty( "com.ibm.ws.scripting.port" );
      }
      props.setProperty(AdminClient.CONNECTOR_PORT, dmPort );

      String wasuser    = WPConfig.getProperty( WPConstants.PROP_WAS_USER );
      String waspass    = WPConfig.getProperty( WPConstants.PROP_WAS_PASS );
      if ( wasuser != null ) props.setProperty(AdminClient.USERNAME, wasuser );
      if ( waspass != null ) props.setProperty(AdminClient.PASSWORD, waspass );

      if(wasuser != null || waspass != null)
         props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");

      /*
      // Following may be needed if we ever need to worry about SSL connections for SOAP/RMI.
      // Right now wsadmin doesn't support this, so we aren't going to either.

      props.setProperty("javax.net.ssl.trustStore"        , WPConfig.getProperty( "com.ibm.ssl.trustStore" ) );
      props.setProperty("javax.net.ssl.keyStore"          , WPConfig.getProperty( "com.ibm.ssl.keyStore" ) );
      props.setProperty("javax.net.ssl.trustStorePassword", WPConfig.getProperty( "com.ibm.ssl.trustStorePassword" ) );
      props.setProperty("javax.net.ssl.keyStorePassword"  , WPConfig.getProperty( "com.ibm.ssl.keyStorePassword" ) );
      props.list( System.out );


      debug( "Connecting to Admin Server instance usingthe following options" );
      debug( "  via AdminClientFactory.createAdminClient and the following properties" );



      props.list( System.out );
      */
      try {
         TheClient = AdminClientFactory.createAdminClient(props);
         TheMgr = AppManagementProxy.getJMXProxyForClient( TheClient );
         //debug( "Connected to Admin Server instance" );
      } catch ( ConnectorException ce) {
          //d96586 -- retry the connection in case security is NOT enabled . . .
          System.out.println( ce.getMessage() );
          System.out.println( "Retrying with security disabled . . ." );
          props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "false");
          try {
             TheClient = AdminClientFactory.createAdminClient(props);
             TheMgr = AppManagementProxy.getJMXProxyForClient( TheClient );
          } catch ( Exception e) {
             throw new WPWsAdminException( e.getMessage(), WPWSADMIN_ERROR_CONNECT );
          }
      } catch ( Exception e) {
         throw new WPWsAdminException( e.getMessage(), WPWSADMIN_ERROR_CONNECT );
      }

      return TheMgr;
   }

   public int initAdmin() {
      return super.initAdmin( true );
   }

   public int initAdmin( boolean forFileTransferOnly ) {
      return super.initAdmin( !forFileTransferOnly );
   }


   public int extractEar() {
      int errorCode = initAdmin();
      if ( errorCode != WPWSADMIN_ERROR_SUCCESS ) {
         return errorCode;
      }

      if (dMgrProcess) {
         return extractEar( targetFile.getAbsolutePath(), appName );
      } else {
         return extractEarStandalone();
      }
   }  

   protected int extractEarStandalone() {
      return extractEarFile( targetFile.getAbsolutePath(), appName );
   }  

   public int extractEar( String toFile, String appName ) {

      try {
         getApplicationManager();
      } catch ( WPWsAdminException e) {
         logError( "Unable to establish connection with WAS AdminClient : " + e.getMessage(), e );
         return e.getErrorCode();
      }

      int errorCode = getConfigService();
      if ( errorCode != WPWSADMIN_ERROR_SUCCESS ) {
         return errorCode;
      }

		// set the application name being deployed so the configuration service will retrieve the right
		// deployment information
		setAppName(appName);

      if ( !configHelper.isApplicationDeployed() ) {
         return WPWSADMIN_ERROR_APPMISSING;
      }

      String remoteFile = toFile;
      FileTransferClient transfer = null;
      if (dMgrProcess) {
         transfer = getFileTransferClient();
         if (transfer == null ) return WPWSADMIN_ERROR_FILETRANS;

         remoteFile = computeRemoteFile( transfer  );
         log( "Will Extract " + targetName + "(" + appName + ") to remote file " + remoteFile );
      } else {
         log( "Will Extract " + targetName + "(" + appName + ") to " + toFile );
      }

      int status = extractEarFile( remoteFile, appName );
      if ( (status == WPWSADMIN_ERROR_SUCCESS ) && dMgrProcess) {
         File localFile = downloadFile( transfer, remoteFile, true );
         removeRemoteFile( remoteFile );
         status = (localFile == null ) ? WPWSADMIN_ERROR_FILETRANS_DOWNLOAD_FAILED : WPWSADMIN_ERROR_SUCCESS;
      }

      return status;
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
      int errorCode = initAdmin();
      if ( errorCode != WPWSADMIN_ERROR_SUCCESS ) {
         return errorCode;
      }

      if (dMgrProcess) {
         return redeployEar( targetFile.getAbsolutePath(), appName );
      } else {
         return redeployEarStandalone();
      }
   }

   public int redeployEar( String fromFile, String appName ) {
      int status = WPWSADMIN_ERROR_UNKNOWN;
      debug( "Iniitalize the App Manager instance" );
      try {
         getApplicationManager();
      } catch ( WPWsAdminException e) {
         logError( "Unable to establish connection with WAS AdminClient : " + e.getMessage(), e );
         return e.getErrorCode();
      }

      debug( "Getting ConfigService" );
      status = getConfigService();
      if ( status != WPWSADMIN_ERROR_SUCCESS ) {
         return status;
      }

      // set the application name being deployed so the configuration service will retrieve the right
		// deployment information
		setAppName(appName);

      if ( !configHelper.isApplicationDeployed() ) {
         debug( "Application is not deployed." );
         return WPWSADMIN_ERROR_APPMISSING;
      }


      String remoteFile = targetFile.getAbsolutePath();
      if ( dMgrProcess ) {
         debug( "Uploading the ear file to DManager work area." );
         remoteFile = uploadFile( false );
         debug( "Uploaded the ear file to DManager work area." );
      }

      if ( remoteFile != null ) {

         debug( "Redeploying ear file." );
         status = redeployEarFile( remoteFile, appName );
         debug( "Redeployed ear file." );
         if ( dMgrProcess ) removeRemoteFile( remoteFile );
      } else {
         status = WPWSADMIN_ERROR_FILETRANS_UPLOAD_FAILED;
      }

      return status;
   }

   // Note This method should not use ConfigService, as it may not be initialized
   public String uploadFile( boolean deleteOnUpload ) {
      FileTransferClient transfer = getFileTransferClient();;
      if (transfer == null ) return null;

      String remoteFile = computeRemoteFile( transfer );
      // First upload file to DM Server
      try {
         log( "Uploading " + targetFile + " to remote file " + remoteFile );
         WPWsAdminTransferOptions opts = new WPWsAdminTransferOptions();
         opts.setDeleteSourceOnCompletion( deleteOnUpload );
         transfer.uploadFile( targetFile, targetName, opts ); 
      } catch ( TransferFailedException e) {
      //} catch ( Exception e) {
         logError( "Unable to transfer remoteFile " + remoteFile + " to " + targetFile + ": " + e.getMessage(), e );
         return null;
      }
      return remoteFile;
   }

   // Note This method should not use ConfigService, as it may not be initialized
   public File downloadFile( boolean deleteRemoteFile ) {
      FileTransferClient transfer = getFileTransferClient();
      if (transfer == null ) return null;
      String remoteFile = computeRemoteFile( transfer );
      return downloadFile( transfer, remoteFile, deleteRemoteFile );
   }

   // Note This method should not use ConfigService, as it may not be initialized
   public File downloadFile( String remoteFile, boolean deleteRemoteFile ) {
      FileTransferClient transfer = getFileTransferClient();
      if (transfer == null ) return null;
      return downloadFile( transfer, remoteFile, deleteRemoteFile );
   }

   // Note This method should not use ConfigService, as it may not be initialized
   public File downloadFile(FileTransferClient transfer, String remoteFile, boolean deleteRemoteFile ) {
      return downloadFile( transfer, new File( remoteFile ), deleteRemoteFile  );
   }

   // Note This method should not use ConfigService, as it may not be initialized
   public File downloadFile(FileTransferClient transfer, File remoteFile, boolean deleteRemoteFile ) {
      File localFile = targetFile;
      try {             
         log( "Downloading remote file " + remoteFile + " to " + targetFile );
         WPWsAdminTransferOptions opts = new WPWsAdminTransferOptions();
         opts.setDeleteSourceOnCompletion( deleteRemoteFile );
         // download file
         // When DMgr is on one platfomr and node is another, the File path parsing doesn't work properly,
         //  Specifically is DMgr is on Windows and nodes are Unix. 
         //  d65620 - SMP
         String remoteFileName = remoteFile.getName();
         if ( File.separatorChar == '/' && (remoteFileName.indexOf( '\\' ) != -1 ) ) 
             remoteFileName =  new File(remoteFileName.replace( '\\', '/' )).getName();

         transfer.downloadFile( remoteFileName, targetFile, opts); 
      //} catch ( TransferFailedException e) {
      } catch ( Exception e) {
         logError( "Unable to transfer remoteFile " + remoteFile + " to " + targetFile + ": " + e.getMessage(), e );
         localFile = null;
      }

      return localFile;
   }

   protected int redeployEarStandalone( ) {
      return redeployEarFile( targetFile.getAbsolutePath(), appName );
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

		// set the application name being deployed so the configuration service will retrieve the right
		// deployment information
		setAppName(appName);

      // the redeploy.
      try {

         debug( "Establishing Notification Listener" );
         establishNotificationListener( appName );
         Hashtable props = buildAdminProperties( true );

         //saveModuleInfo();
         //props.setProperty( AppConstants.APPDEPL_CELL, WPConfig.getProperty( WPConstants.PROP_WAS_CELL ) );
         // Ask Dm to redeploy the app
         log("Redeploying remote file " + fromFile + " for app " + appName );
         if (DEBUG_ENABLED) {
            debug( "Invoking redeployApplicaiton on the AppManagement instance." );
            debug( "   EarFile: " + fromFile );
            debug( "   AppName: " + appName );
            debug( "   Deployment Properties:" );
            java.util.Iterator iter = props.keySet().iterator();
            while (iter.hasNext()) {
               String thisKey = (String)iter.next();
               debug( "     " + thisKey + " = " + props.get( thisKey ) );
            }

            Hashtable mapping = (Hashtable)props.get( AppConstants.APPDEPL_MODULE_TO_SERVER );
            iter = mapping.keySet().iterator();
            debug( "   Server Mappings Properties:" );
            while (iter.hasNext()) {
               String thisKey = (String)iter.next();
               debug( "     " + thisKey + " = " + mapping.get( thisKey ) );
            }
            debug( "   Workspace: " + workspace );
         }

         TheMgr.redeployApplication( fromFile, appName, props, workspace );
         // Now need to wait until complete
         debug("Waiting for completion notification" );
         status = waitTilComplete();
         debug("Redeployment complete" );
         if (status == WPWsAdminNotificationListener.STATUS_OK) {
            //restoreModuleInfo();
            debug("Redeployment complete: saving workspace" );
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


   private String computeRemoteFile( FileTransferClient transfer ) {

      String remoteDir = transfer.getServerStagingLocation();

      // Get name of file on remote server (DM)
      String remoteSep = java.io.File.separator; // default. 
      if (remoteDir != null) {
         remoteSep = (remoteDir.indexOf("/") >= 0) ? "/" : "\\";
         if ( !remoteDir.endsWith(remoteSep) ) remoteDir = remoteDir + remoteSep;
      } else {
         remoteDir = remoteSep;
      }
      return remoteDir + targetName;
   }

   private FileTransferClient getFileTransferClient() {
      FileTransferClient transfer = null;
      System.out.println( "Retrieving FileTransferClient" );
      try {
         transfer = (FileTransferClient) ImplFactory.loadImplFromKey(FileTransferClient.class);
         transfer.initialize(TheClient);
      } catch ( Exception e) {
         logError( "Unable to acquire FileTransferClient: " + e.getMessage(), e );
         return null;
      }
      return transfer;
   }

   public  String getRemoteFileLocation() {
      FileTransferClient transfer = getFileTransferClient();
      if (transfer == null ) return null;
      return transfer.getServerStagingLocation();
   }

   public  String getRemoteFileLocation( FileTransferClient transfer ) {
      return transfer.getServerStagingLocation();
   }

   private void removeRemoteFile( String remoteFile ) {
      // Nothing to do.  any local files are deleted on cleanup from EAR/Fix processing.
      // remote files are deleted either on download in FTC option or EAR deplpoyment option.
      if (dMgrProcess) {
         // Currently do noting.
      }
   }

   private int  waitTilComplete() {
      int status = listener.waitForNotification( 10 * 60 );  // 10 minute timeout
      try {
         TheClient.removeNotificationListener (listener.on, listener);
      } catch ( Exception e) {
         // couldn't remove.  we can ignore this
      }
      return status;
   }

   private void establishNotificationListener( String appName )  throws WPWsAdminListenerException {
      Throwable exception = null;
      try {
          ObjectName on = new ObjectName("WebSphere:type=AppManagement,*");

          Iterator it = TheClient.queryNames(on, null).iterator(); 
          on = null;

          if ( it.hasNext() ) 
              on = (ObjectName)it.next();
          else {

             //gError( "Error registering NotificationListener, Unable to locate Objectname for WebSphere:type=AppManagement - appName=" + appName  );
             throw new WPWsAdminListenerException( "Error registering NotificationListener, Unable to locate Objectname for WebSphere:type=AppManagement - appName=" + appName  ); 
          }

          NotificationFilterSupport myFilter = new NotificationFilterSupport();
          myFilter.enableType (AppConstants.NotificationType);

          listener = new WPWsAdminNotificationListener( on );

          TheClient.addNotificationListener (on, listener, myFilter, appName);

      } catch (ConnectorException e) {
          exception = e;
      } catch (WPWsAdminListenerException e) {
         exception = e;
      } catch (Exception e) {
         exception = e;
      } catch (Throwable e) {
          e.printStackTrace(System.err);
          exception = e;
      } finally {
          if (exception != null) {
             logError( "Error registering NotificationListener: appName=" + appName + " Error is : " + exception.getMessage() );
             throw new WPWsAdminListenerException( "Error registering NotificationListener: appName=" + appName + " Error is : " + exception.getMessage() );
          }
      }  

   }

   public void setClusterConfiguration( boolean c ) {
      clusterConfig = c;
      forceClusterConfig = true;
   }

   public void setDeployManagerConfiguration( boolean c ) {
      dmgrConfig = c;
      forceDmgrConfig = true;
   }

   protected int createConfigHelper() {
      configHelper = new WPWsAdminConfigHelper( TheClient, workspace );

      String processType = configHelper.getProcessType();
      if ( processType.equals( AdminConstants.NODE_AGENT_PROCESS ) || processType.equals( AdminConstants.MANAGED_PROCESS ) ) {
         // This is an invalid Process, we need to either work w/ a Standalone process ot DMgr itself.
         logError( "Error acquiring ConfigService: The config server should be one of UnManaged Process or Deplyment Manager Process found " +  processType );
         configHelper = null;
         return WPWSADMIN_ERROR_CFG_PROC_INV;
      } else {
         dMgrProcess = processType.equals( AdminConstants.DEPLOYMENT_MANAGER_PROCESS  );
      }
      log( "appName: " + appName + "    targetFile: " + targetFile + "    ProcessType: " + processType);
      return WPWSADMIN_ERROR_SUCCESS;
   }

   public int syncNode() {
      int errorCode = initAdmin( true );
      if ( errorCode != WPWSADMIN_ERROR_SUCCESS ) {
         return errorCode;
      }

      boolean forceSync = true;

      int syncNodeResult = WPWSADMIN_ERROR_SUCCESS;
      try {

         String nodeName = WPConfig.getProperty( WPConstants.PROP_WAS_NODE );
         
         String query = "WebSphere:type=NodeSync,node=" + nodeName + ",*"; 
         ObjectName queryName = new ObjectName( query );
         Set s = TheClient.queryNames( queryName, null );
         ObjectName nodeSync = null;
         if ( s.isEmpty() ) {
            log( "Unable to locate any NodeSync objects.  Please ensure the nodeAgents are started." );
            return WPWSADMIN_ERROR_UNKNOWN;
         } else {
            nodeSync = (ObjectName)s.iterator().next();

            final String[] nullSignature = new String[0];

            debug("Adding extended NotificationListener - Sync.");
            NotificationFilterSupport myFilter = new NotificationFilterSupport();
            myFilter.enableType (AppConstants.NotificationType);
            WPWsAdminSyncNotificationListener listener = new WPWsAdminSyncNotificationListener();
            TheClient.addNotificationListenerExtended( nodeSync, listener, myFilter, this);

            Boolean result;
            result = (Boolean)TheClient.invoke( nodeSync, "isNodeSynchronized", null, null );
            debug( "The node needs to be synchronized : " + result );
            if ( forceSync || result.booleanValue() ) {
               debug( "Requesting a sync of the node" );
               result = (Boolean)TheClient.invoke( nodeSync, "sync", null, null );
               if (result.booleanValue()) {
                  debug( "Node sync accepted." );
                  Object syncResult = TheClient.invoke( nodeSync, "getSyncResult", null, null );
                  debug( "Node sync result: " + syncResult );
                  debug(" Node synchronization was successful: " + ((com.ibm.ws.management.sync.SyncResult)syncResult).isSuccessful() );
                  if ( !((com.ibm.ws.management.sync.SyncResult)syncResult).isSuccessful() ) {
                     log( "Node sync result: " + syncResult );
                     syncNodeResult =  WPWSADMIN_ERROR_UNKNOWN;
                  }
               } else {
                  log( "Node sync not accepted." );
                  syncNodeResult =  WPWSADMIN_ERROR_UNKNOWN;
               }
            }

            TheClient.removeNotificationListenerExtended( listener );

         }
      } catch ( Exception e) {
         e.printStackTrace();
         syncNodeResult =  WPWSADMIN_ERROR_UNKNOWN;
      }
      return syncNodeResult;

   }
   public int syncActiveNodes() {

      boolean forceSync = true;

      int syncNodeResult = WPWSADMIN_ERROR_SUCCESS;
      try {

         String cellName = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
         
         String query = "WebSphere:type=DeploymentManager,cell=" + cellName + ",*"; 
         ObjectName queryName = new ObjectName( query );
         Set s = TheClient.queryNames( queryName, null );
         ObjectName depMgr = null;
         if ( s.isEmpty() ) {
            log( "Unable to locate any NodeSync objects.  Please ensure the nodeAgents are started." );
            return WPWSADMIN_ERROR_UNKNOWN;
         } else {
            depMgr = (ObjectName)s.iterator().next();
            final String[] signature = { "java.lang.Boolean" };
            final Object[] params    = { Boolean.TRUE };


            NotificationFilterSupport myFilter = new NotificationFilterSupport();
            myFilter.enableType (AppConstants.NotificationType);

            WPWsAdminSyncNotificationListener listener = new WPWsAdminSyncNotificationListener();

            TheClient.addNotificationListener (depMgr, listener, myFilter, this);

            debug("Adding extended NotificationListener.");
            ObjectName allSyncNodes = new ObjectName( "WebSphere:type=NodeSync,*" );
            TheClient.addNotificationListenerExtended( allSyncNodes, listener, myFilter, this);

            Set result;
            result = (Set)TheClient.invoke( depMgr, "syncActiveNodes", params, signature );
            if (result.isEmpty()) {
               // should this be an error?  Apparently no nodes were sync'd
               debug("No nodes synchronmized." );
            } else {
               if (DEBUG_ENABLED) {
                  java.util.Iterator iter = result.iterator();
                  while (iter.hasNext()) {
                     debug("Node " + iter.next() + " has been synchronized." );
                  }
               }
            }
            TheClient.removeNotificationListener ( depMgr, listener );
            TheClient.removeNotificationListenerExtended( listener );

         }
      } catch ( Exception e) {
         e.printStackTrace();
         syncNodeResult =  WPWSADMIN_ERROR_UNKNOWN;
      }
      return syncNodeResult;

   }
   protected class WPWsAdminSyncNotificationListener implements NotificationListener {

      /*
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
      */


      /*
      public void appEventReceived(AppNotification ev ) {
         //long now = System.currentTimeMillis();
         //System.out.println( "Event received at " + now + "(" + new java.util.Date( now ) + ")" );
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
      */

      WPWsAdminSyncNotificationListener( ) {
      }

      //receive notifications
      public void handleNotification(Notification notf, Object handback) {
         System.out.println( "::handleNotification : " + notf );
         System.out.println( "   UserData - " + notf.getUserData() );
         System.out.println( "   Message  - " + notf.getMessage() );
         System.out.println( "   Type     - " + notf.getType() );

         /*
         AppNotification ev = (AppNotification)notf.getUserData();

         //String type = notf.getType();
         if (ev != null) {
            appEventReceived( ev );
         }
         */

      }

   }

}

