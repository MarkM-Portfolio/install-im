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

/*
 *  @ (#) WpsEarEARActor.java
 *
 *  Perform WpsEar EAR pre and post processing operations.
 *
 *  This class modifes the superclass defined operations to
 *  provide a wps.ear specific implementation.
 *
 *  The processing for wpsear is:
 *
 *  In order of processing steps:
 *
 *      * When a deployment is present, skip expanding the
 *        installable copy of the webui EAR.
 *
 *      * When no deployment is present, expand the installable
 *        application to a temporary location.
 *
 *      * The core update function updates the expanded installable
 *        wps.eari application (if present), and any deployed wps.ear
 *        applications (in the installed location).
 *
 *      * When a deployment is present, the post processing steps
 *        are to perform plugin-processing on the updated deployed
 *        application, then compress that application into the
 *        configuration based applications directory, then copy that
 *        compressed application into the 'installable' directory.
 *
 *      * When no deployment is present, the post processing steps
 *        are to run plugin-processing on the updated installable
 *        application, then to compress that application from the
 *        temporary working location into the installable location.
 * 
 *  Split 'not deployed' and 'deployed':
 *
 *  Deployed:
 *
 *      Pre: None
 *      Core: Update 'installed'.
 *      Post: Update 'installed';
 *            Plugin process 'installed';
 *            Compress 'installed' to 'applications';
 *            Copy 'applications' to 'installable'.
 *
 *  Not Deployed:
 *
 *      Pre: Expand 'installable' to a temporary directory.
 *      Core: Update the temporary 'installable'.
 *      Post: Plugin process the temporary 'installable';
 *            Compress the temporary 'installable to 'installable'.
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.util.WsAdminCmd;
import com.ibm.websphere.update.delta.util.WsAntCmd;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.xml.product.product;


public class WpsEarEARActor extends WPEarActor {
   public final static String pgmVersion = "1.9" ;
   public final static String pgmUpdate = "11/9/03" ;

   public WpsEarEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName,
                         boolean doDeploy, boolean doPluginDeploy) {
      this(installPath,
           messages, errors,
           earName,
           doDeploy, doPluginDeploy,
           false, false);
   }

   public WpsEarEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName,
                         boolean doDeploy, boolean doPluginDeploy,
                         boolean installableOnly, boolean applicationOnly)
   {
      super(installPath, messages, errors,
            earName,
            doDeploy, false,  // Portal wps.ear would never do a pluginDeploiy
            installableOnly, applicationOnly);
   }

   protected String getEarName() { return "wps.ear"; }
   protected String getEarAppName() { return "wps"; }
   protected String getEarInstalledLocation() { return WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + "install"; }


   protected boolean compressDMEar( String earName ) {
      // Before compressing we need to possibly replace some tokens in the web.xml file
      /*
      WsAntCmd antCmd = new WsAntCmd( getMessages(), getErrors() );
      antCmd.setAntTask( "action-update-wps-ear" );
      antCmd.setEnvVars( new String[] {
                                       "-Dwp.home=" + WPConfig.getProperty( WPConstants.PROP_WP_HOME ),
                                       "-Dtmp.ear.root=" + getUncompressedDMPath(earName),
                                       "-DWpsAppName=" + getEarAppName(),
                                       "-f",
                                       antCmd.getResourcePath() + File.separator + 
                                          "tasks" + File.separator + "pui_ear_actions.xml"
                                      } );

      antCmd.execute();
      */
        String portalHome = WPConfig.getProperty( WPConstants.PROP_WP_HOME );
         WPProduct portal = new WPProduct( portalHome );
         Iterator piter = portal.getProducts();
         boolean ppropOK = false;
         while ( !ppropOK && piter.hasNext() ) {
             product pthisProd = (product)piter.next();
             String pprodName  = pthisProd.getName();
                 String pprodID    = pthisProd.getId();
                 String pver       = pthisProd.getVersion();
                 if ( pver.startsWith( "5.0" ) ) {
                     if ( pprodID.equals( "MP" ) ) {
                                 ppropOK = true;
                                 System.out.println("MP5.0");
                     }
                 }
         }
       
       
      if (ppropOK == true) {
          WsAntCmd antCmd = new WsAntCmd( getMessages(), getErrors() );
          antCmd.setAntTask( "action-update-wps-ear" );
          antCmd.setEnvVars( new String[] {
                                           "-Dwp.home=" + WPConfig.getProperty( WPConstants.PROP_WP_HOME ),
                                           "-Dtmp.ear.root=" + getUncompressedDMPath(earName),
                                           "-DWpsAppName=" + getEarAppName(),
                                           "-f",
                                           antCmd.getResourcePath() + File.separator + 
                                              "tasks" + File.separator + "pui_ear_actions.xml"
                                          } );

          antCmd.execute();
      }
      

      return super.compressDMEar( earName );
   }

}
