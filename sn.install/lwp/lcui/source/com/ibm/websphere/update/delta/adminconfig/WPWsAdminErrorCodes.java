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

/**********************************************************************************************************
 * 
 * 
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdminErrorCodes.java, wps.base.fix, wps6.fix
 * 
 * History 1.1, 10/25/03
 *
 **********************************************************************************************************/
public interface WPWsAdminErrorCodes  {
   public final static String pgmVersion = "1.1" ;
   public final static String pgmUpdate = "10/25/03" ;

   public static final int  WPWSADMIN_ERROR_SUCCESS = 0;

   public static final int  WPWSADMIN_ERROR_ACTION_UNK   = 2;
   public static final int  WPWSADMIN_ERROR_INVALID_ARGS = 3;

   public static final int  WPWSADMIN_ERROR_LOGIN        = 11;
   public static final int  WPWSADMIN_ERROR_CONNECT      = 12;
   public static final int  WPWSADMIN_ERROR_CONNECT_INV  = 13;

   public static final int  WPWSADMIN_ERROR_CFG_GENERAL  = 21;
   public static final int  WPWSADMIN_ERROR_CFG_PROC_INV = 22;

   public static final int  WPWSADMIN_ERROR_APPMISSING   = 31;

   public static final int  WPWSADMIN_ERROR_FILETRANS    = 41;
   public static final int  WPWSADMIN_ERROR_FILETRANS_DOWNLOAD_FAILED  = 42;
   public static final int  WPWSADMIN_ERROR_FILETRANS_UPLOAD_FAILED    = 43;
   //public static final int  WPWSADMIN_ERROR_FILETRANS    = 41;

   public static final int  WPWSADMIN_ERROR_REDEPLOY     = 51;
   public static final int  WPWSADMIN_ERROR_EXPORT_APP   = 62;


   public static final int  WPWSADMIN_ERROR_UNKNOWN      = 101;
}

