/*
********************************************************************
* IBM Confidential                                                 *
*                                                                  *
* OCO Source Materials                                             *
*                                                                  *
*                                                                  *
* Copyright IBM Corp. 2003, 2015                                   *
*                                                                  *
* The source code for this program is not published or otherwise   *
* divested of its trade secrets, irrespective of what has been     *
* deposited with the U.S. Copyright Office.                        *
********************************************************************
*/
package com.ibm.websphere.update.delta.earutils;

import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

/*
 *  @ (#) ExtendedWebuiAction.java
 *
 *  Overload superclass 'ExtendedWPCPAuthorPostAction' to redefine
 *  'createActor' to return a WPCPAuthorPostActor.
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.delta.util.WsAdminClient;
import com.ibm.websphere.update.delta.util.WsAdminCmd;
import com.ibm.websphere.update.delta.adminconfig.WPWsAdminErrorCodes;

public class ExtendedWPCPAuthorPostAction extends ExtendedWPEarAction {
   public final static String pgmVersion = "1.5" ;
   public final static String pgmUpdate = "4/29/04" ;

   public ExtendedWPCPAuthorPostAction() {
       super();
   }

   public ExtendedEARActor createActor(String installPath,
                                       StringBuffer messages,
                                       StringBuffer errors,
                                       String earName,
                                       boolean doDeploy,
                                       boolean doPluginDeploy,
                                       boolean installableOnly,
                                       boolean applicationOnly) {
      return new WPCPAuthorPostActor( getWasHomeDir(),
                              messages, errors,
                              earName,
                              doDeploy, doPluginDeploy,
                              installableOnly, applicationOnly);
   }


   protected boolean basicProcess(ExtendedEARActor actor) {
      return actor.postProcess( getDeploymentFlag() );
   }

   private class WPCPAuthorPostActor extends ExtendedEARActor  {
      public WPCPAuthorPostActor(String installPath, StringBuffer messages, StringBuffer errors,
                            String earName,
                            boolean doDeploy, boolean doPluginDeploy) {
         this(installPath,
              messages, errors,
              earName,
              doDeploy, doPluginDeploy,
              false, false);
      }

      public WPCPAuthorPostActor(String installPath, StringBuffer messages, StringBuffer errors,
                            String earName,
                            boolean doDeploy, boolean doPluginDeploy,
                            boolean installableOnly, boolean applicationOnly) {
         super(installPath, messages, errors,
               earName,
               doDeploy, false,  // Portal wps.ear would never do a pluginDeploiy
               installableOnly, applicationOnly);
      }


      // Post-processing overrides:
      //
      // boolean basicPostProcess(boolean);

      protected boolean basicPostProcess(boolean doDeploy) {

         Iterator instances = getInstances();
         if ( instances == null ) {
            log("No instances; failing post-processing.");
            return false;
         }

         boolean allOK = deployWPEar( commandDatum.earName );

         if ( allOK )
            log("Post-processing succeeded.");

         return allOK;
      }


      protected boolean deployWPEar( String earName ) {
         // This deplpoys through wsadmin, so it will work w/ DM.
         String targetPath = getEarInstalledLocation() + File.separator + earName;
         log("Deploy " + earName + " from " + targetPath);
         //System.out.println( this.getClass().getName() + "::deployWPEar : " + "Deploy " + earName + " from " + targetPath);
         if ( processingDMDeployments() ) {
            // redeploy through the DMgr.
            WsAdminClient cmd = new WsAdminClient( getMessages(), getErrors() );
            cmd.setAction( WsAdminClient.ACTION_REDEPLOY );
            cmd.setEarFile( targetPath );
            cmd.setAppName( getEarAppName() );
            return cmd.execute() == WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS;
         } else { 
            // Local mode. Deploy though wsadmin.
            //System.out.println( this.getClass().getName() + "::deployWPEar : " +  "$AdminApp install " + targetPath + " {-update -appname " + getEarAppName() + "}" );
            WsAdminCmd cmd = new WsAdminCmd( getMessages(), getErrors() );
            cmd.setCommandString( "$AdminApp install {" + normalizePath(targetPath) + "} {-update -appname " + getEarAppName() + "};" + 
                                  "$AdminConfig save" );
            return cmd.execute();
         }
      }

      protected String getEarName() { return "WPCP_Authoring.ear"; }
      protected String getEarAppName() { return "WPCP_Authoring"; }
      protected String getEarInstalledLocation() { return WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + "wpcp" + File.separator + "v5.0" + File.separator + "author"+ File.separator + "installableApps"; }

      protected boolean processingDMDeployments() {
         return (new Boolean( WPConfig.getProperty( WPConstants.PROP_WAS_DM ) ) ).booleanValue();
      }

      protected String normalizePath(String path) {
         // Make sure all backslashes are converted to forward slashes
          return ( ( path == null ) ? null : path.replace( '\\', '/') );
      }
   }

}
