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
package com.ibm.websphere.update.delta.earutils;


import java.io.File;

import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;


/*
 * Created on Feb 22, 2005
 *
 * @author smp
 * 
 */
public class WCMEarActor extends WPEarActor
{
    public final static String pgmVersion = "1.0" ;
    public final static String pgmUpdate = "02/22/05" ;

    public WCMEarActor(String installPath, StringBuffer messages, StringBuffer errors,
                          String earName,
                          boolean doDeploy, boolean doPluginDeploy) {
       this(installPath,
            messages, errors,
            earName,
            doDeploy, doPluginDeploy,
            false, false);
    }

    public WCMEarActor(String installPath, StringBuffer messages, StringBuffer errors,
                          String earName,
                          boolean doDeploy, boolean doPluginDeploy,
                          boolean installableOnly, boolean applicationOnly)
    {
       super(installPath, messages, errors,
             earName,
             doDeploy, false,  // Portal wps.ear would never do a pluginDeploiy
             installableOnly, applicationOnly);
    }

    protected String getEarName() { return "wcm.ear"; }
    protected String getEarAppName() { return "wcm"; }
    protected String getEarInstalledLocation() { return WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + "wcm" + File.separator + "installableApps"; }



}
