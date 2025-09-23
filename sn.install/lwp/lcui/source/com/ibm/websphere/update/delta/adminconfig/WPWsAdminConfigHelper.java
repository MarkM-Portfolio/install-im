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

import com.ibm.lconn.wizard.update.data.*;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.AdminConstants;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementFactory;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.AppNotification;
import com.ibm.websphere.management.application.client.AppDeploymentController;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentMessages;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.application.client.AppDeploymentTaskMessages;
import com.ibm.websphere.management.configservice.ConfigDataId;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceFactory;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.filetransfer.client.FileTransferClient;
import com.ibm.websphere.management.filetransfer.client.FileTransferOptions;
import com.ibm.websphere.management.filetransfer.client.TransferFailedException;
import com.ibm.websphere.product.*;
import com.ibm.websphere.product.formatters.*;
import com.ibm.websphere.product.history.*;  
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.BaseHandlerException;
import com.ibm.websphere.product.xml.CalendarUtil;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.product.xml.extension.extension;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.product.xml.websphere.websphere;
import com.ibm.websphere.update.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.efix.*;
import com.ibm.websphere.update.efix.prereq.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import com.ibm.websphere.update.msg.*;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPCheckFiles;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import com.ibm.websphere.update.util.WPUpdateFileEntry;
import com.ibm.websphere.update.util.WPUpdateFiles;
import com.ibm.websphere.update.util.was.CheckWASStatus;
import com.ibm.ws.management.fileservice.FileTransferFactory;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
 * File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdminConfigHelper.java, wps.base.fix, wps5.fix History 1.7, 2/8/05
 */
public class WPWsAdminConfigHelper  {
   public final static String pgmVersion = "1.7" ;
   public final static String pgmUpdate = "2/8/05" ;


   private   AdminClient   adminClient;
   private   ConfigService configService = null;
   private   Session       session       = null;
      
   private   String        workspace       = null;
   private   String        appName         = null;
   private   String        entAppName      = null;
   private   String        targetName      = null;
   private   File          targetFile      = null;

   private   boolean       clustered       = false;  // For now this is the same as being in a DMgr config


   // Answer a new wsadmin command on the specified
   //
   // cessing messages are placed in the messages buffer.
   //
   // cessing errors are placed in the errors buffer.

   public WPWsAdminConfigHelper( AdminClient ac, String workspace ) {
      adminClient = ac;
      this.workspace = workspace;

      if ( ac != null ) {
         // If we are in a DMgr config ( and talking to DMgr then we are clustered.  This is safe for now since
         //   portal only support DMgr if clustered.
         clustered = getProcessType().equals( AdminConstants.DEPLOYMENT_MANAGER_PROCESS );
      }

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

   boolean DEBUG_ENABLED = false;
   private static final java.text.DateFormat DATE_FMT = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

   void debug( String txt ) {
      if (DEBUG_ENABLED) System.out.println( DATE_FMT.format( new Date() ) + " -- " + txt );
   }

   /**
 * @param appName  the appName to set
 * @uml.property  name="appName"
 */
public void setAppName( String appName ) {
      this.appName    = appName;
      if (appName != null) {
         this.entAppName = appName + ".ear";
      }
   }

   /**
 * @param targetFile  the targetFile to set
 * @uml.property  name="targetFile"
 */
public void setTargetFile( File tf ) {
      this.targetFile = tf;
      if (targetFile != null) {
         this.targetName = targetFile.getName();
      }
   }

   public void save( boolean overwrite ) throws WPWsAdminException, ConnectorException {
      try {
         configService.save( session, overwrite );
      } catch ( ConfigServiceException e ) {
         throw new WPWsAdminException( e.getMessage() );
      }
   }

   public void discard() throws WPWsAdminException, ConnectorException {
      try {
         configService.discard( session );
      } catch ( ConfigServiceException e ) {
         throw new WPWsAdminException( e.getMessage() );
      }
   }

   public boolean isApplicationDeployed( ) {

      String cellName   = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
      if (!getConfigService()) {
         return false;
      }

      try {
         ObjectName[] names;
         ObjectName appON = null;
         Hashtable hashtable = new Hashtable();
         hashtable.put("_Websphere_Config_Data_Id", "cells/" + cellName + "/applications/" + entAppName +"/deployments/" + appName + "/deployment.xml");
         appON = new ObjectName( "WebSphere", hashtable );
         // We need to maintain existing module Mapping so we need to 
         // Lookup all currenbt, deployments for the earfile, and appname
         names = configService.queryConfigObjects( session, null, appON, null );
			if (names != null && names.length > 0 ) {
				log(appName + " is deployed.");
			}
         return (names != null) && (names.length > 0 );

      } catch ( Exception e) {
         logError("Error trying to resolve serverTargets :" + e.getMessage(), e );
         return false;
      }

      //return serverTargets;

   }


   /**
 * @return  the configService
 * @uml.property  name="configService"
 */
private boolean getConfigService() {
      if (adminClient == null) {
         return getConfigServiceLocal();
      }

      if (configService != null ) { return true; } // only init once....

      configService = null;
      session = null;
      try {
         //Create a ConfigService to store config changes to
         configService = new ConfigServiceProxy( adminClient );
         //create a session and therefore an own workspace
         session = new Session(workspace, true);
      } catch ( Exception e) {
         logError( "Error acquiring ConfigService: " + e.getMessage(), e );
         return false;
      }
      return true;

   }

   private boolean getConfigServiceLocal() {
      if (configService != null ) { return true; } // only init once....


      configService = null;
      session = null;
      try {
         //Create a ConfigService to store config changes to
         Properties properties = new Properties();
         properties.put("location", "local");
         configService = ConfigServiceFactory.createConfigService(true, properties);
         //create a session and therefore an own workspace
         session = new Session(workspace, true);
      } catch ( Exception e) {
         logError( "Error acquiring ConfigService: " + e.getMessage(), e );
         return false;
      }
      return true;

   }


   protected String getProcessType() {
      if (adminClient == null ) return "Local";
      
      try {
         //Get the Remote MBean for AdminService
         ObjectName connectedServer = adminClient.getServerMBean();
         //invoke the getProcessType method on the AdminService.
         return (String)adminClient.invoke(connectedServer, "getProcessType", null, null);
      } catch ( ConnectorException e) {
         logError( "Error retrieving processType, unable to locate AdminService MBean", e );
      } catch ( InstanceNotFoundException e) {
         logError( "Error retrieving processType, unable to invoke getProcessType on AdminService MBean", e );
      } catch ( Exception e) {
         logError( "Error retrieving processType", e );
      }
      return "unknown";
   }

   private   boolean isCluster() {
      return clustered;  
   }

   private   String getServerTarget() {
      String cellName   = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
      // WP_SERVER contains the clusterName if in a cluster. (defined from DeploymentService.
      String serverName = WPConfig.getProperty( WPConstants.PROP_WP_SERVER );
      String target = "";
      // in a portal cluster ?
      if (isCluster()) {
          // overwrite target
          target = "WebSphere:cell=" + cellName + ",cluster=" + serverName;
      } else { // single server, managed or not it is the same target
         String nodeName   = WPConfig.getProperty( WPConstants.PROP_WAS_NODE );
          // target for a non-cluster system
          target = "WebSphere:cell=" + cellName + ",node=" + nodeName + ",server" + "=" + serverName;
      }
		debug("getServerTarget returns " + target);
      return target;
   }

   private   Map  getServerTargets() {

      Map serverTargets = new HashMap();
      String cellName   = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
      if (!getConfigService()) {
         return serverTargets;
      }

      try {
         ObjectName[] names;
         ObjectName appON = null;
         Hashtable hashtable = new Hashtable();
         hashtable.put("_Websphere_Config_Data_Id", "cells/" + cellName + "/applications/" + entAppName +"/deployments/" + appName + "/deployment.xml");
         appON = new ObjectName( "WebSphere", hashtable );
         // We need to maintain existing module Mapping so we need to 
         // Lookup all currenbt, deployments for the earfile, and appname
         names = configService.queryConfigObjects( session, null, appON, null );
         // Only look for deploymentTargets for the app
         String[] deployAttrs = new String[] { "deploymentTargets" };
         for (int i=0; i<names.length; i++ ) {
            // Get Attributs for this deplpoymen
            AttributeList attrs = configService.getAttributes( session, names[i], deployAttrs, true );
            Iterator iter = attrs.iterator();
            while (iter.hasNext()) {
               Attribute thisAttr = (Attribute)iter.next();
               // Only care about target, this gives us our mappings.
               if (thisAttr.getName().equals("deploymentTargets")) {
						debug("getServerTargets: deploymentTargets for " + names[i] + "...");
                  ArrayList mappings = (ArrayList)thisAttr.getValue();
                  serverTargets = resolveMappings( mappings, cellName );
               }
            }
         }
         
      } catch ( Exception e) {
         logError("Error trying to resolve serverTargets :" + e.getMessage(), e );
      }

      return serverTargets;
   }

   private Map resolveMappings( ArrayList values, String cellName ) {
      Map serverTargets = new HashMap();
      Iterator depIter = values.iterator();
      Attribute thisAttr = null;
      final String defaultTarget = getServerTarget();        // SMP - Make sure we allway add a target for each module.  
      while (depIter.hasNext()) {
         AttributeList attrs = (AttributeList) depIter.next();
         Iterator depAttrIter = attrs.iterator();
         ConfigDataId depId = null;
         String depNode = null;
         String depName = null;
         String depType = null;
         boolean depIsCluster = false;
         while (depAttrIter.hasNext()) {
            thisAttr = (Attribute)depAttrIter.next();
            String thisAttrName = thisAttr.getName();
				debug("resolveMappings:        attribute = " + thisAttrName);
            if (thisAttr.getName().equals( "name" )) {
               depName = (String)thisAttr.getValue();

					debug("resolveMappings:           name = " + depName);
            } else if (thisAttr.getName().equals( "_Websphere_Config_Data_Type" ) ) {
               depType = (String)thisAttr.getValue();
					debug("resolveMappings:           type = " + depName);
            } else if (thisAttr.getName().equals( "_Websphere_Config_Data_Id" ) ) {
               depId   = (ConfigDataId)thisAttr.getValue();
					debug("resolveMappings:           ConfigDataID = " + depId);
               //log( "ConfigID:" + depId + "   contextURI:" + depId.getContextUri() + "    href:" + depId.getHref() );
            } else if (thisAttr.getName().equals( "nodeName" )) {
               depNode = (String)thisAttr.getValue();
					debug("resolveMappings:           nodeName = " + depNode);
            } 
            //System.out.println("\tName:" + thisAttr.getName() + "   Value:" + thisAttr.getValue() );
         }
         if (depName == null || depType == null || depId == null) {
            // not good. have to skip
         } else {
            String target = "WebSphere:cell=" + cellName + ",";
            if ( depType.equals("ClusteredTarget")) {
               target = target + "cluster=" + depName;
            } else if ( depType.equals("ServerTarget")) {
               if (depNode == null) {
                  // No node data, will have to skip
                  target = null;
               } else {
                  target = target + "node=" + depNode + ",server=" + depName;
               }
            } else {
               // unknown type, will skip
               target = null;
            }
            if (target== null) {                    // SMP - Make sure we allway add a target for each module.  
               target = defaultTarget;              // SMP - Make sure we allway add a target for each module.  
            }                                       // SMP - Make sure we allway add a target for each module.  
            // TODO: May need to modify  the depId.

				debug("resolveMappings:        target = " + target);
				debug("resolveMappings:        HREF = " + depId.getHref());
            if ( target != null ) {
               //serverTargets.add( target );
               serverTargets.put( depId.getHref(), target ); 
            }
         }
      }
      return serverTargets;
   }

   /**
    * This reads the ear archive to get all the war files.  and computes the module2server mapping for each war.
    *   This will build a default mapping for each war.  The default is set of current mappings at the ear level
    * @returns a hashtable that conatisn the module URI (xxx.war+WEB-INF) and the servertargets as the key.  if there
    *   are multiple targets they are separated by a "+" sign.
    **/
   Hashtable getDefaultModuleMapping() {

		debug("getDefaultModuleMappings entry");
      Hashtable module2server = new Hashtable();
      // read current data
      Hashtable props = new Hashtable();
      props.put (AppConstants.APPDEPL_LOCALE, Locale.getDefault());
      Map serverTargets = getServerTargets();
      try {
         
         // For some reason this isn't working.....
         //Vector appTasks = TheMgr.getApplicationInfo( appName, props, workspace );

         AppDeploymentController appdeploymentcontroller = AppManagementFactory.readArchive(targetFile.toString(), props);
         //AppDeploymentController appdeploymentcontroller = AppManagementFactory.readArchive( appName, props);

         AppDeploymentTask appTask = appdeploymentcontroller.getFirstTask();
			debug("getDefaultModuleMappings: iterating through tasks");
         for(int j = 1; appTask != null; j++) {
            String appTaskName = appTask.getName();
				debug("getDefaultModuleMappings: task name = " + appTaskName);
            if (appTaskName.equals("MapModulesToServers")) {
               String [][]taskData = appTask.getTaskData();
               // row 0 is the column headers
               for (int i=1; i<taskData.length; i++) {
                  // col 2 (idx 1) contans the WebModule URI, so add mapping for each web module
                  String uri = taskData[i][1].replace( ',', '+' );
                  //Iterator stIter = serverTargets.keySet().iterator();
                  StringBuffer targetBuffer = new StringBuffer();
                  Iterator stIter = serverTargets.values().iterator();
                  while (stIter.hasNext()) {
                     String thisTarget = (String)stIter.next();
                     targetBuffer.append( "+" + thisTarget );
                     //log( "Added module2server mapping : module=" + uri + " serverTarget=" + thisTarget);
                  }

						debug("getDefaultModuleMappings: target = " + targetBuffer);
                  if (targetBuffer.length() > 0) {
                     module2server.put( uri, targetBuffer.substring( 1 ) );
                  }
               }
               break; // We are all done.
            }
            appTask = appdeploymentcontroller.getNextTask();
         }

      } catch ( Exception e) {
         logError( "Error retrieving Application infor for " + appName, e );
      }
      if ( module2server.isEmpty() ) {
         logError("Didn't find any web module for module to Server binding.");
      }
      return module2server;
   }

   /**
    * Computes the module2server mapping for all war files.  This first retrieves the default mapping for all war
    *   files in the ear, then applies any current mapping to the war.  
    * @returns a hashtable that conatisn the module URI (xxx.war+WEB-INF) and the servertargets as the key.  if there
    *   are multiple targets they are separated by a "+" sign.
    **/
   Hashtable getModuleMapping( Map currentDeploymentValues ) {
		debug("getModuleMapping entry, getting default module mapping");
      Hashtable moduleMappings = getDefaultModuleMapping();
      Map currentMappings = (Map)currentDeploymentValues.get( "modules" );
      if ( currentMappings == null ) {
         currentMappings = new HashMap();
      }

		debug("getModuleMappings: current mappings = " + currentMappings);
      Iterator iter = moduleMappings.keySet().iterator();
		debug("getModuleMappings: iterating through default mappings");
      while (iter.hasNext()) {
         String thisModuleURI = (String)iter.next();

			debug("getModuleMappings:    module URI = " + thisModuleURI);
         int idx = thisModuleURI.indexOf( "+" );
         if (idx != -1) {
            String thisModule = thisModuleURI.substring( 0, idx );
				debug("getModuleMappings:       module name = " + thisModule);
            ArrayList mappings = (ArrayList)currentMappings.get( thisModule );
            if (mappings != null && !mappings.isEmpty()) {
					debug("getModuleMappings:       found module in current mappings");
               // build new list and replace in table.
               Iterator modIter = mappings.iterator();
               StringBuffer targetBuffer = new StringBuffer();
               while (modIter.hasNext()) {
                  targetBuffer.append( "+" + modIter.next() );
               }

					debug("getModuleMappings:       target = " + targetBuffer);
               moduleMappings.put( thisModuleURI, targetBuffer.substring( 1 ) );
            }
				else
					debug("getModuleMappings:       did not find module in current mappings!");
         }
      }
      return moduleMappings;
   }

	boolean deployEJB() {
        boolean result = false;
		String portalHome = WPConfig.getProperty( WPConstants.PROP_WP_HOME );
		WPProduct portal = new WPProduct( portalHome );
        
		debug("deployEJB1 " + WPConstants.PROP_WP_HOME + " : " + portalHome);
		Iterator piter = portal.getProducts();
		boolean ppropOK = false;
		while ( !ppropOK && piter.hasNext() ) {
		    product pthisProd = (product)piter.next();
		    String pprodName  = pthisProd.getName();
			String pprodID    = pthisProd.getId();
			String pver       = pthisProd.getVersion();
			debug( "Checking Product : " + pprodName + "(" + pprodID + ")" );
			if ( pver.startsWith( "5.1" ) ) {
			    if ( pprodID.equals( "MP" ) ) {
					ppropOK = true;
					System.out.println("MP5.1");
			    }
			    /*else if ( pprodID.equals( "ISC" ) ) {
			        ppropOK = true;
			        System.out.println("ISC5.1");
			    }
			    else if ( pprodID.equals( "EXPRESS" ) ) {
			        ppropOK = true;
			        System.out.println("EXP5.1");
			    }*/
			}
		    debug("deployEJB2 " + ppropOK + " : " + piter.hasNext());
		}
        
        if ( ppropOK == true ) {
		    String wasHome = WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME );
		    WASProduct wp = new WASProduct( wasHome );
		    Iterator iter = wp.getProducts();
		    boolean propOK = false;
		    while ( !propOK && iter.hasNext() ) {
		        product thisProd = (product)iter.next();
			    String prodName  = thisProd.getName();
			    String prodID    = thisProd.getId();
			    String ver       = thisProd.getVersion();                         //JEL PK15844 added ND as a product id
			    debug( "Checking Product2 : " + prodName + "(" + prodID + ")" + ver);
			    if ( prodID.equals("PME") || prodID.equals("WBI") || prodID.equals("ND")
			      || (prodID.equals("BASE") && ver.startsWith("6.")) ){
				    propOK = true;
				    result = true;
			    }
		    }
		}
		debug("deployEJB3 ");
        return result && appName.equals("wps");
	}


   Hashtable buildAdminProperties( boolean forDeploy ) {

      Hashtable props = new Hashtable();
      props.put (AppConstants.APPDEPL_LOCALE, Locale.getDefault());
      if ( forDeploy ) {
         Map currentDeploymentValues = getModuleDeploymentProperties( );

	 Set currentDeploymentValuesSet = currentDeploymentValues.entrySet();
	 Object [] currentDeploymentValuesArray = currentDeploymentValuesSet.toArray();
	 for (int i = 0; i < currentDeploymentValuesArray.length; i++) {
	     System.out.println(currentDeploymentValuesArray[i]);
	 }

//         String cellName = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );

         props.put (AppConstants.APPDEPL_MODULE_TO_SERVER, getModuleMapping( currentDeploymentValues ) );

         if ( currentDeploymentValues.containsKey(AppConstants.APPDEPL_INSTALL_DIR) ) {
            props.put( AppConstants.APPDEPL_INSTALL_DIR, currentDeploymentValues.get(AppConstants.APPDEPL_INSTALL_DIR) );
         }
         if ( currentDeploymentValues.containsKey(AppConstants.APPDEPL_CLASSLOADERPOLICY) ) {
            props.put( AppConstants.APPDEPL_CLASSLOADERPOLICY, currentDeploymentValues.get(AppConstants.APPDEPL_CLASSLOADERPOLICY) );
         } else {
            props.put (AppConstants.APPDEPL_CLASSLOADERPOLICY,  AppConstants.APPDEPL_CLASSLOADERPOLICY_MULTIPLE);
         }
         if ( currentDeploymentValues.containsKey(AppConstants.APPDEPL_CLASSLOADINGMODE) ) {
            props.put( AppConstants.APPDEPL_CLASSLOADINGMODE, currentDeploymentValues.get(AppConstants.APPDEPL_CLASSLOADINGMODE) );
         } else {
            props.put (AppConstants.APPDEPL_CLASSLOADINGMODE,  AppConstants.APPDEPL_CLASSLOADINGMODE_PARENTLAST);
         }
	     if ( currentDeploymentValues.containsKey(AppConstants.APPDEPL_EJB) ) {
            props.put( AppConstants.APPDEPL_EJB, currentDeploymentValues.get(AppConstants.APPDEPL_EJB) );
            System.out.println("Detected that APPDEPL_EJB options are needed to be set" + AppConstants.APPDEPL_EJB);
         }
         
		 debug("deployEJB0");
         if ( deployEJB() ) {
			debug("deployEJB10");
            props.put( AppConstants.APPDEPL_DEPLOYEJB_CMDARG, Boolean.TRUE );
            Hashtable ejbDeployOptions = new Hashtable();
            ejbDeployOptions.put(AppConstants.APPDEPL_DEPLOYEJB_DBTYPE_OPTION, "CLOUDSCAPE_V5");
            props.put( AppConstants.APPDEPL_DEPLOYEJB_OPTIONS, ejbDeployOptions );
			debug("Detected that APPDEPL_DEPLOYEJB_CMDARG options are needed to be set");
         }
		 debug("deployEJB11");
         
         if ( currentDeploymentValues.containsKey(AppConstants.APPDEPL_PRECOMPILE_JSP) ) {
            props.put( AppConstants.APPDEPL_PRECOMPILE_JSP, currentDeploymentValues.get(AppConstants.APPDEPL_PRECOMPILE_JSP) );
         }
         if ( currentDeploymentValues.containsKey(AppConstants.APPDEPL_RELOADENABLED) ) {
            props.put( AppConstants.APPDEPL_RELOADENABLED, currentDeploymentValues.get(AppConstants.APPDEPL_RELOADENABLED) );
         }
         if ( currentDeploymentValues.containsKey(AppConstants.APPDEPL_RELOADINTERVAL) ) {
            props.put( AppConstants.APPDEPL_RELOADINTERVAL, currentDeploymentValues.get(AppConstants.APPDEPL_RELOADINTERVAL) );
         }

         //props.put( AppConstants.APPDEPL_DEPLOYEJB_CMDARG, Boolean.TRUE );
         //props.put( AppConstants.APPDEPL_DEPLOYEJB_OPTIONS, new Hashtable() );

         props.put (AppConstants.APPDEPL_DELETE_SRC_EAR,  Boolean.TRUE);

      }

      System.out.println("Properties collected are " + props );
      return props;
   }

   Map       getModuleDeploymentProperties( ) {
      // This will read the deployment info from Config

      HashMap   currentValues = new HashMap();
      if (!getConfigService()) { return currentValues; }
      String cellName   = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
      String nodeName   = WPConfig.getProperty( WPConstants.PROP_WAS_NODE );
      // WP_SERVER contains the clusterName if in a cluster. (defined from DeploymentService.
      String serverName = WPConfig.getProperty( WPConstants.PROP_WP_SERVER );

      HashMap moduleTargets = new HashMap();
      Map serverTargets = getServerTargets();

      try {
         ObjectName cellON = null;
         ObjectName appON = null;
         Hashtable hashtable = new Hashtable();
         hashtable.put("_Websphere_Config_Data_Id", "cells/" + cellName + "/applications/" + entAppName +"/deployments/" + appName + "/deployment.xml");
         appON = new ObjectName( "WebSphere", hashtable );

         ObjectName[] names = configService.queryConfigObjects( session, null, appON, null );
         System.out.println("WPS Application deployments" );
         String[] deployAttrs = new String[] {
            "deployedObject"
         };
         for (int i=0; i<names.length; i++ ) {
            AttributeList attrs = configService.getAttributes( session, names[i], deployAttrs, true );
            Iterator iter = attrs.iterator();
            while (iter.hasNext()) {
               Attribute thisAttr = (Attribute)iter.next();
               String attrName = thisAttr.getName();
               String webModule = null;

               if (attrName.equals("deployedObject")) {
                  ArrayList values = (ArrayList)thisAttr.getValue();
                  int ai = 1;
                  Iterator depIter = values.iterator();
                  while (depIter.hasNext()) {
                     thisAttr = (Attribute) depIter.next();
                     String thisDOName = thisAttr.getName();
                     if (thisDOName.equals("classloader")) {
                        String clMode = null;
                        AttributeList clList = (AttributeList)thisAttr.getValue();
                        Iterator clIter = clList.iterator();
                        while ( clIter.hasNext()) {
                           thisAttr = (Attribute)clIter.next();
                           if (thisAttr.getName().equals( "mode" ) ) {
                              currentValues.put( AppConstants.APPDEPL_CLASSLOADINGMODE, thisAttr.getValue() );
                           }
                        }
                     } else if (thisDOName.equals("targetMappings")) {
                        // skip
                     } else if (thisDOName.equals("modules")) {
                        // For each module, collect all servertarget ids.....
                        //  This will be a hashmap key on module name, w/ value being a list of mapping ids.
                        ArrayList modList = (ArrayList)thisAttr.getValue();
                        String thisModule = null;
                        Iterator modIter = modList.iterator();
                        int modNum = 1;
                        while (modIter.hasNext()) {
                           AttributeList clList = (AttributeList)modIter.next();
                           Iterator clIter = clList.iterator();
                           ArrayList thisModuleMapping = new ArrayList();

                           while ( clIter.hasNext()) {
                              thisAttr = (Attribute)clIter.next();
                              if ( thisAttr.getName().equals( "targetMappings" ) ) {
                                 ArrayList mappings = (ArrayList)thisAttr.getValue();

                                 Iterator mapIter = mappings.iterator();
                                 thisAttr = null;
                                 int depNum = 1;
                                 while (mapIter.hasNext()) {
                                    attrs = (AttributeList) mapIter.next();
                                    Iterator mapAttrIter = attrs.iterator();
                                    while (mapAttrIter.hasNext()) {
                                       thisAttr = (Attribute)mapAttrIter.next();
                                       if (thisAttr.getName().equals( "target" ) ) {
                                          ObjectName targetON = (ObjectName)thisAttr.getValue();
                                          String cfgid =  targetON.getKeyProperty( "_Websphere_Config_Data_Id" );
                                          thisModuleMapping.add( (new ConfigDataId( cfgid ) ).getHref() );
                                       }
                                    }
                                 }
                              } else if ( thisAttr.getName().equals( "uri" ) ) {
                                 thisModule = (String)thisAttr.getValue();
                              }
                           }
                           moduleTargets.put( thisModule, thisModuleMapping );
                        }
                        currentValues.put( thisDOName, moduleTargets );
                     } else {
                        if ( !thisAttr.getName().startsWith("_") ) {
                           currentValues.put( thisAttr.getName(), thisAttr.getValue() );
                        }
                     }
                  }
               } else {
                  //System.out.println("     -- Do not know how to process " + attrName );
               }

            }
         }

         // We need to resolve the module mappings IDS to the real serverTargets.  Should be 
         //   a simple lookup of ids.

         HashMap oldModuleTargets = (HashMap)moduleTargets.clone();
         Iterator iter = oldModuleTargets.keySet().iterator();
         while (iter.hasNext()) {
            String thisMod = (String)iter.next();
            ArrayList modTargetIds = (ArrayList)oldModuleTargets.get( thisMod );
            ArrayList modTargets = new ArrayList( modTargetIds.size() );
            Iterator idIter = modTargetIds.iterator();
            while (idIter.hasNext()) {
               Object targetId = idIter.next();
               Object val = serverTargets.get( targetId );
               if (val != null ) {
                  modTargets.add( val );
               } else {
                  moduleTargets.remove( thisMod );
                  //System.out.println( "Could not find mapping for " + targetId );
               }
            }
            moduleTargets.put( thisMod, modTargets );
         }



      } catch ( Exception e) {
         e.printStackTrace();
      } finally {
         try {
            configService.discard( session );
         } catch ( Exception e) {
         }
      }

      return currentValues; 
   }

   protected void runTest2() {

      if (!getConfigService()) {
         return;
      }      

      Map modProps = getModuleDeploymentProperties( );
      Iterator iter = modProps.keySet().iterator();
      System.out.println( "Values collected about current deployment of " + appName );
      while (iter.hasNext()) {
         String thisKey = (String)iter.next();
         Object value = modProps.get( thisKey );
         if ( thisKey.equals( "modules" ) ) {
            // process special
            Iterator modIter = ((Map)value).keySet().iterator();
            while ( modIter.hasNext() ) {
               String thisMod = (String)modIter.next();
               System.out.println( "    Module " + thisMod );
               Iterator targetIter = ((List)((Map)value).get( thisMod )).iterator();
               while ( targetIter.hasNext() ) {
                  System.out.println( "        " + targetIter.next() );
               }
            }
         } else {
            System.out.println( "    " + thisKey + "=" + value );
         }
      }

      Hashtable mappings = getModuleMapping( modProps );
      iter = mappings.keySet().iterator();
      System.out.println( "\n\nModule Mappings:");
      System.out.println( "================");
      while ( iter.hasNext() ) {
         String thisMod = (String)iter.next();
         System.out.println( " ModuleURI: " + thisMod + "     Mappings:" + mappings.get( thisMod ) );
      }

      

      Hashtable adminProps =  buildAdminProperties( true );
      iter = adminProps.keySet().iterator();
      System.out.println( "\n\nAdmin Properties:");
      System.out.println( "=================");
      while (iter.hasNext()) {
         String thisName = (String)iter.next();
         if (thisName.equals(AppConstants.APPDEPL_MODULE_TO_SERVER)) {
            System.out.println( "Name: " + thisName );
            mappings = (Hashtable)adminProps.get( thisName );
            Iterator mapIter = mappings.keySet().iterator();
            while ( mapIter.hasNext() ) {
               String thisModMap = (String)mapIter.next();
               System.out.println( "    ModuleURI: " + thisModMap + "     Mappings:" + mappings.get( thisModMap ) );
            }
         } else {
            System.out.println( "Name: " + thisName + " \tValue:" + adminProps.get( thisName ) );
         }
      }
   }


   protected void runTest() {

      if (!getConfigService()) {
         return;
      }

      Hashtable props = new Hashtable();
      props.put (AppConstants.APPDEPL_LOCALE, Locale.getDefault());
      try {
         Vector v = new Vector();
         AppDeploymentController appdeploymentcontroller = AppManagementFactory.readArchiveForRedeployment(targetFile.toString(), props, v);
         //AppDeploymentController appdeploymentcontroller = AppManagementFactory.readArchive(targetFile.toString(), props);
         //AppDeploymentController appdeploymentcontroller = AppManagementFactory.readArchive( appName, props);

         String as[] = appdeploymentcontroller.getAppDeploymentTaskNames();
         for (int i=0; i<as.length; i++) {
            System.out.println( "Task :" + as[i] );
         }
         AppDeploymentTask appTask = appdeploymentcontroller.getFirstTask();
         for(int j = 1; appTask != null; j++) {
            String appTaskName = appTask.getName();
            String[] appCols = appTask.getColumnNames();
            //String[] mutCols = appTask.getMutableColumns();
            System.out.println( "AppInfo - AppDeplpoymentTask " + appTaskName);
            if ( appCols != null && appCols.length>0) {
               System.out.println( "  Columns" );
               for (int i=0; i<appCols.length; i++) {
                  System.out.println( "\t" + appCols[i] );
               }
            }
            if (appTaskName.equals("MapModulesToServers")) {
               String [][]taskData = appTask.getTaskData();
               for (int i=0; i<taskData.length; i++) {

                  System.out.println( "Module to Sevrer data (" + i + ")" );
                  for (int k=0; k<taskData[i].length; k++) {
                     System.out.println( "\t" + taskData[i][k] );
                  }
               }
            }

             appTask = appdeploymentcontroller.getNextTask();
         }

      /*
         //Vector appTasks = TheMgr.getApplicationInfo( appName, props, workspace );
         Iterator liter = appTasks.iterator();
         while (iter.hasNext()) {
            AppDeploymentTask appTask = (AppDeploymentTask)iter.next();
            String appTaskName = appTask.getName();
            String[] appCols = appTask.getColumnNames();
            //String[] mutCols = appTask.getMutableColumns();
            System.out.println( "AppInfo - AppDeplpoymentTask " + appTaskName);
            if ( appCols != null && appCols.length>0) {
               System.out.println( "  Columns" );
               for (int i=0; i<appCols.length; i++) {
                  System.out.println( "\t" + appCols[i] );
               }
            }

         }
        */
         
      } catch ( Exception e) {
         e.printStackTrace();
      }




   }

   protected void runTest3() {

      if (!getConfigService()) { return; }

      Hashtable props = new Hashtable();
      props.put (AppConstants.APPDEPL_LOCALE, Locale.getDefault());
      try {

         String[] SIGNATURE_String_Hashtable_String = new String[4];
         SIGNATURE_String_Hashtable_String[0] = "java.lang.String";
         SIGNATURE_String_Hashtable_String[1] = "java.util.Hashtable";
         SIGNATURE_String_Hashtable_String[2] = "java.lang.String";

         ObjectName mgrON = new ObjectName("WebSphere:type=AppManagement,*");
         Set set1 = adminClient.queryNames( mgrON, null);
         if(set1 != null && !set1.isEmpty()) {
            Iterator iter = set1.iterator();
            while ( iter.hasNext() ) {
               mgrON = (ObjectName)iter.next();
               System.out.println( "AppManagement ObjectName : " + mgrON );
               System.out.println( "\t PropertyListString : " + mgrON.getKeyPropertyListString() );
            }


            //mgrON = (ObjectName)set1.iterator().next();
         }


          Object aobj[] = new Object[3];
          aobj[0] = appName;
          aobj[1] = props;
          aobj[2] = workspace;
          Vector appTasks = (Vector)adminClient.invoke( mgrON, "getApplicationInfo", aobj, SIGNATURE_String_Hashtable_String);

          Iterator iter = appTasks.iterator();
          while (iter.hasNext()) {
             AppDeploymentTask appTask = (AppDeploymentTask)iter.next();
             String appTaskName = appTask.getName();
             String[] appCols = appTask.getColumnNames();
             //String[] mutCols = appTask.getMutableColumns();
             System.out.println( "AppInfo - AppDeplpoymentTask " + appTaskName);
             if ( appCols != null && appCols.length>0) {
                System.out.println( "  Columns" );
                for (int i=0; i<appCols.length; i++) {
                   System.out.println( "\t" + appCols[i] );
                }
             }
             if (appTaskName.equals("MapModulesToServers")) {
                String [][]taskData = appTask.getTaskData();
                for (int i=0; i<taskData.length; i++) {

                   System.out.println( "Module to Sevrer data (" + i + ")" );
                   for (int k=0; k<taskData[i].length; k++) {
                      System.out.println( "\t" + taskData[i][k] );
                   }
                }
             }

          }
      } catch(Exception jmexception) {
          jmexception.printStackTrace();
      }
   }

   protected void runTest4() {

      if (!getConfigService()) { return; }
      String cellName   = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
      String nodeName   = WPConfig.getProperty( WPConstants.PROP_WAS_NODE );
      // WP_SERVER contains the clusterName if in a cluster. (defined from DeploymentService.
      String serverName = WPConfig.getProperty( WPConstants.PROP_WP_SERVER );

      try {
         /*
         String[] configTypes= configService.getSupportedConfigObjectTypes();
         System.out.println("ConfigTypes");
         for (int i=0; i<configTypes.length; i++ ) {
            System.out.println("\t" + configTypes[i] );
         }

         */


         ObjectName[] names = configService.queryTemplates( session, "ServerCluster" );
         ObjectName cellON = null;
         Hashtable hashtable = new Hashtable();
         /*
         System.out.println("Template Clusters" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }

         names = configService.queryTemplates( session, "Cell" );
         System.out.println("Template Cell1" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }
         names = configService.queryTemplates( session, "ServerTarget" );
         System.out.println("Template ServerTarget" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }
         names = configService.queryTemplates( session, "MappingModule" );
         System.out.println("Template MappingModule" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }
         names = configService.queryTemplates( session, "Application" );
         System.out.println("Template Application" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }
         names = configService.queryTemplates( session, "ApplicationDeployment" );
         System.out.println("Template ApplicationDeployment" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }
         names = configService.queryTemplates( session, "ApplicationServer" );
         System.out.println("Template ApplicationServer" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }

         hashtable.put("_Websphere_Config_Data_Id", "cells/" + cellName + "/clusters/" + serverName +"/cluster.xml");
         cellON = new ObjectName( "WebSphere", hashtable );

         names = configService.queryConfigObjects( session, null, cellON, null );
         System.out.println("Clusters" );
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
         }
         */
         ObjectName appON = null;
         hashtable = new Hashtable();
         hashtable.put("_Websphere_Config_Data_Id", "cells/" + cellName + "/applications/" + entAppName +"/deployments/" + appName + "/deployment.xml");
         appON = new ObjectName( "WebSphere", hashtable );

         names = configService.queryConfigObjects( session, null, appON, null );
         System.out.println("WPS Applicaiton deployments" );
         String[] deployAttrs = new String[] {
            "deploymentTargets",
            "deployedObject",
            "deployedObjectConfig",

            "EJBCache",
            "EJBContainer",
            "EJBModuleConfiguration",
            "EJBModuleDeployment",
            "EjbNameSpaceBinding",

            "deployment"
         };
         for (int i=0; i<names.length; i++ ) {
            System.out.println("\t" + names[i] );
            /* 
            AttributeList attrs = configService.getAttributes( session, names[i], null, true ); // This gets all
            System.out.println("   Attributes" );
            Iterator iter = attrs.iterator();
            while (iter.hasNext()) {
               Attribute thisAttr = (Attribute)iter.next();
               System.out.println("\tName:" + thisAttr.getName() + "   Value:" + thisAttr.getValue() );
            }
            */
            AttributeList attrs = configService.getAttributes( session, names[i], deployAttrs, true );
            Iterator iter = attrs.iterator();
            //System.out.println("   Deployment Targets" );
            while (iter.hasNext()) {
               Attribute thisAttr = (Attribute)iter.next();
               System.out.println("   " + thisAttr.getName() );
               String attrName = thisAttr.getName();

               Object thisAttrValue = thisAttr.getValue();
               if (thisAttrValue == null) {
                  System.out.println("     Name: " + thisAttr.getName() + "   Value = null" );
               } else if ( thisAttrValue instanceof ArrayList ) {
                  System.out.println("     Name: " + thisAttr.getName() + "   Value" + thisAttrValue);
                  if (attrName.equals("deploymentTargets")) {
                     ArrayList values = (ArrayList)thisAttr.getValue();
                     int ai = 1;
                     Iterator depIter = values.iterator();
                     while (depIter.hasNext()) {
                        attrs = (AttributeList) depIter.next();
                        Iterator depAttrIter = attrs.iterator();
                        System.out.println("     Deployement :" + ai++ );
                        while (depAttrIter.hasNext()) {
                           thisAttr = (Attribute)depAttrIter.next();
                           System.out.println("\tName:" + thisAttr.getName() + "   Value:" + thisAttr.getValue() );
                        }
                     }
                  } else if (attrName.equals("deployedObject")) {
                     ArrayList values = (ArrayList)thisAttr.getValue();
                     int ai = 1;
                     Iterator depIter = values.iterator();
                     while (depIter.hasNext()) {
                        thisAttr = (Attribute) depIter.next();
                        String thisDOName = thisAttr.getName();
                        if (thisDOName.equals("classloader")) {
                           System.out.println("\tClassloader:" );
                           AttributeList clList = (AttributeList)thisAttr.getValue();
                           Iterator clIter = clList.iterator();
                           while ( clIter.hasNext()) {
                              thisAttr = (Attribute)clIter.next();
                              System.out.println("\t   Name:" + thisAttr.getName() + "   Value:" + thisAttr.getValue() );
                           }
                        } else if (thisDOName.equals("modules")) {
                           System.out.println("\tModules:" );

                           ArrayList modList = (ArrayList)thisAttr.getValue();
                           Iterator modIter = modList.iterator();
                           int modNum = 1;
                           while (modIter.hasNext()) {
                              System.out.println("\t   Module " + modNum++ );
                              AttributeList clList = (AttributeList)modIter.next();
                              Iterator clIter = clList.iterator();
                              while ( clIter.hasNext()) {
                                 thisAttr = (Attribute)clIter.next();
                                 if ( thisAttr.getName().equals( "targetMappings" ) ) {
                                    ArrayList mappings = (ArrayList)thisAttr.getValue();

                                    Iterator mapIter = mappings.iterator();
                                    thisAttr = null;
                                    int depNum = 1;
                                    while (mapIter.hasNext()) {
                                       System.out.println( "\t      Mapping " + depNum++ ); 
                                       attrs = (AttributeList) mapIter.next();
                                       Iterator mapAttrIter = attrs.iterator();
                                       while (mapAttrIter.hasNext()) {
                                          thisAttr = (Attribute)mapAttrIter.next();
                                          System.out.println("\t       Name:" + thisAttr.getName() + "   Value:" + thisAttr.getValue() );
                                       }
                                    }
                                 } else {
                                    System.out.println("\t       Name:" + thisAttr.getName() + "   Value:" + thisAttr.getValue() );
                                 }
                              }
                           }
                        } else {
                           System.out.println("\tName:" + thisAttr.getName() + "   Value:" + thisAttr.getValue() );
                        }
                     }
                  } else {
                     System.out.println("     -- Do not know how to process " + attrName );
                  }

               } else if (  thisAttrValue instanceof ConfigDataId )  {
                  System.out.println("     Name: " + thisAttr.getName() + "   ConfigDataId: " + thisAttrValue );
               } else if (  thisAttrValue instanceof String )  {
                  System.out.println("     Name: " + thisAttr.getName() + "   String: " + thisAttrValue );
               } else {
                  System.out.println("     Name: " + thisAttr.getName() + "   Unknown value type (" + thisAttrValue.getClass().getName() + ")  -- " + thisAttrValue );
               }
               
               /*
               for (int ai=0; ai<values.length; ai++) {
                  System.out.println("     Deployement :" + ai );
                  for (int aj=0; aj<values.length; aj++) {
                     System.out.println("\tName:" + values[ai][aj].getName() + "   Value:" + values[ai][aj].getValue() );
                  }
               }
               */
            }
         }

         Map serverTargets = getServerTargets();

         System.out.println("Server targets" );
         Iterator stIter = serverTargets.keySet().iterator();
         while ( stIter.hasNext() ) {
            String key = (String)stIter.next();
            System.out.println("\tKey:" + key + "   Value:" + serverTargets.get( key ) );

         }
         /*
         for ( int i=0; i<serverTargets.length; i++) {
            System.out.println("\t" + serverTargets[i] );
         }
         */

      } catch ( Exception e) {
         e.printStackTrace();
      } finally {
         try {
            configService.discard( session );
         } catch ( Exception e) {
         }
      }


   }

}

/*   Config Types
        ActivitySessionService
        AdminService
        Agent
        AllAuthenticatedUsersExt
        Application
        ApplicationConfig
        ApplicationContainer
        ApplicationDeployment
        ApplicationProfileService
        ApplicationServer
        AsynchMessageConsumerExtension
        AuthenticationTarget
        AuthMechanism
        AuthorizationConfig
        AuthorizationProvider
        AuthorizationTableExt
        AuthorizationTableImpl
        BackupCluster
        Cell
        CellManager
        Classloader
        ClusteredTarget
        ClusterMember
        CMPConnectorFactory
        CommonSecureInterop
        Component
        ConfigSynchronizationService
        ConnectionFactory
        ConnectionPool
        ConnectorModuleDeployment
        Cookie
        CORBAObjectNameSpaceBinding
        CryptoHardwareToken
        CustomAuthMechanism
        CustomService
        CustomUserRegistry
        DatabaseRepository
        DataReplication
        DataSource
        DebugService
        DeployedObject
        DeployedObjectConfig
        Deployment
        DeploymentTarget
        DeploymentTargetMapping
        DRSConnectionPool
        DRSPartition
        DRSSerialization
        DRSSettings
        DynamicCache
        EJBCache
        EJBContainer
        EJBModuleConfiguration
        EJBModuleDeployment
        EjbNameSpaceBinding
        EndPoint
        EndPointRef
        EnterpriseBeanConfig
        EveryoneExt
        ExtendedMessagingProvider
        ExtendedMessagingService
        Extension
        ExtensionMBean
        ExtensionMBeanProvider
        ExternalCacheGroup
        ExternalCacheGroupMember
        ExternallyManagedHTTPServer
        FederationRepository
        FileTransferService
        FlowContainer
        FlowModuleDeployment
        FlowModuleServerConfiguration
        FlowModuleTemplate
        ForeignCell
        GenericJMSConnectionFactory
        GenericJMSDestination
        GroupExt
        HostAlias
        HTTPConnector
        HTTPTransport
        I18NService
        IdentityAssertionLayer
        IdentityAssertionQOP
        IdentityAssertionTypeAssociation
        IIOPLayer
        IIOPSecurityProtocol
        IIOPTransport
        IndirectLookupNameSpaceBinding
        InputPort
        InstancePool
        Interceptor
        InvalidationSchedule
        J2CConnectionFactory
        J2CResourceAdapter
        J2EEResourceFactory
        J2EEResourceProperty
        J2EEResourcePropertySet
        J2EEResourceProvider
        JAASAuthData
        JAASConfiguration
        JAASConfigurationEntry
        JAASLoginModule
        JavaProcessDef
        JavaVirtualMachine
        JDBCProvider
        JMSConnectionFactory
        JMSConnector
        JMSDestination
        JMSProvider
        JMSServer
        JMSTransport
        JMXConnector
        Key
        LdapRepository
        LDAPSearchFilter
        LDAPUserRegistry
        Library
        LibraryRef
        ListenerPort
        LocalOSUserRegistry
        LookAsideRepository
        LSDConnection
        LTPA
        MailProvider
        MailSession
        ManagedObject
        MappingModule
        MemberManagerProvider
        MemberManagerService
        MemberRepository
        MessageLayer
        MessageListenerService
        MessageQOP
        MimeEntry
        ModuleConfig
        ModuleDeployment
        ModuleRef
        ModuleShare
        MonitoringPolicy
        MQQueue
        MQQueueConnectionFactory
        MQTopic
        MQTopicConnectionFactory
        MultibrokerDomain
        MultiBrokerRoutingEntry
        NameBinding
        NamedEndPoint
        NameServer
        NameSpaceBinding
        NamingContext
        Node
        NodeAgent
        NodeMap
        ObjectPool
        ObjectPoolManagerInfo
        ObjectPoolProvider
        ObjectPoolService
        ObjectRequestBroker
        ORBPlugin
        OutputPort
        OutputRedirect
        PMEDeploymentExtension
        PMEServerExtension
        PMIRequestMetrics
        PMIRMFilter
        PMIRMFilterValue
        PMIService
        ProcessDef
        ProcessExecution
        ProfileRepository
        Property
        PropertySet
        ProtocolProvider
        QualityOfProtection
        RASLoggingService
        Referenceable
        RepositoryService
        ResourceEnvEntry
        ResourceEnvironmentProvider
        RMIConnector
        RoleAssignmentExt
        RoleBasedAuthorization
        SchedulerConfiguration
        SchedulerProvider
        SchedulerService
        SecureAssociationService
        SecureSocketLayer
        Security
        SecurityProtocolConfig
        SecurityProtocolQOP
        SecurityRoleExt
        SecurityServer
        Server
        ServerCluster
        ServerComponent
        ServerEntry
        ServerExt
        ServerIdentity
        ServerIndex
        ServerTarget
        Service
        ServiceContext
        ServiceLog
        SessionBeanConfig
        SessionDatabasePersistence
        SessionManager
        SingleSignon
        SOAPConnector
        SpecialSubjectExt
        SSLConfig
        StaffPluginConfiguration
        StaffPluginProvider
        StaffService
        StatefulSessionBeanConfig
        StateManageable
        StatisticsProvider
        StreamRedirect
        StringNameSpaceBinding
        SubjectExt
        SupportedLdapEntryType
        SupportedMemberType
        SWAMAuthentication
        SystemMessageServer
        TAInterceptor
        ThreadPool
        TraceLog
        TraceService
        TransactionService
        Transport
        TransportLayer
        TransportQOP
        TrustAssociation
        TuningParams
        TypedProperty
        URL
        URLProvider
        UserExt
        UserRegistry
        VariableMap
        VariableSubstitutionEntry
        VirtualHost
        WAS40ConnectionPool
        WAS40DataSource
        WASQueue
        WASQueueConnectionFactory
        WASTopic
        WASTopicConnectionFactory
        WebContainer
        WebModuleConfig
        WebModuleDeployment
        WorkAreaService
        WorkloadManagementServer
        WorkManagerInfo
        WorkManagerProvider
        WorkManagerService
*/
