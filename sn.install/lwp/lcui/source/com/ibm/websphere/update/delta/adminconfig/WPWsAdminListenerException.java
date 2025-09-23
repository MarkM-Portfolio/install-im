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

/*
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdminListenerException.java, wps.base.fix, wps6.fix
 *
 * History 1.2, 9/26/03
 *
 */
public class WPWsAdminListenerException extends Exception {
   public static final String pgmVersion = "1.2" ;
   public static final String pgmUpdate = "9/26/03" ;
   public WPWsAdminListenerException() {
      super();
   }

   public WPWsAdminListenerException( String msg ) {
      super( msg );
   }
}

