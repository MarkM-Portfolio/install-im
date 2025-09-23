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

import com.ibm.websphere.update.delta.util.WsAdminCmd;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class WPOdcEARActor extends WPEarActor {
   public final static String pgmVersion = "1.1" ;
   public final static String pgmUpdate = "10/7/03" ;

   private String appName;
   private String earName;

   public WPOdcEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName, String appName,
                         boolean doDeploy, boolean doPluginDeploy) {
      this(installPath,
           messages, errors,
           earName, appName,
           doDeploy, doPluginDeploy,
           false, false);
   }

   public WPOdcEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName, String appName,
                         boolean doDeploy, boolean doPluginDeploy,
                         boolean installableOnly, boolean applicationOnly)
   {
      super(installPath, messages, errors,
            earName,
            doDeploy, false,  // Portal wps.ear would never do a pluginDeploiy
            installableOnly, applicationOnly);
      this.earName = earName;
      this.appName = appName;


   }

   /**
 * @return  the earName
 * @uml.property  name="earName"
 */
protected String getEarName() { return earName; }
   protected String getEarAppName() { return appName; }
   protected String getEarInstalledLocation() { return WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + "odc" + File.separator + "editors"; }

}
