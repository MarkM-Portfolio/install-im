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
package com.ibm.websphere.update.util;

public interface WPConstants {
   public static final String PROP_WAS_HOME          = "WasHome";
   public static final String PROP_WAS_USER_HOME     = "WasUserHome";
   public static final String PROP_WAS_PROD_HOME     = "WasProdHome";
   public static final String PROP_WAS_CELL          = "CellName";
   public static final String PROP_WAS_NODE          = "NodeName";
   public static final String PROP_WAS_USER          = "WasUserid";
   public static final String PROP_WAS_PASS          = "WasPassword";
   public static final String PROP_WAS_INSTANCE      = "Instance";
   public static final String PROP_WCS_HOME          = "WcsHome";

   public static final String PROP_WAS_MANAGED_NODE  = "com.ibm.wp.pui.was.managed.node";
   
   public static final String PROP_USER_INSTALL_ROOT = "user.install.root";
   
   //Added to support WAS 6.  Points to appropriate profile's setupCmdLine
   public static final String PROP_WAS_USER_SCRIPT   = "was.user.script";

   public static final String PROP_WAS_DM_HOME       = "WasDMHome";
   public static final String PROP_WAS_DM_SERVER     = "WasDMServer";
   public static final String PROP_WAS_DM_PORT       = "WasDMPort";
   public static final String PROP_WAS_DM            = "WasDM";

   public static final String PROP_WPCP_HOME         = "WpcpInstallLocation";
   public static final String PROP_WPCP_ONLY         = "com.ibm.wps.wpcp.only";

   public static final String PROP_WP_HOME           = "WpsInstallLocation";
   public static final String PROP_TRUE_WP_HOME      = "TrueWpsInstallLocation";
   public static final String PROP_WP_SERVER         = "ServerName";

   public static final String PROP_WP_PUI_CFG        = "com.ibm.wp.pui.configprops";
   public static final String PROP_WP_BLD_LEVEL      = "com.ibm.wp.pui.bld.level";

}
