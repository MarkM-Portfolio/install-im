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

/**
 * File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/delta/adminconfig/WPWsAdminException.java, wps.base.fix, wps6.fix History 1.3, 10/25/03
 */
public class WPWsAdminException extends Exception {
   int errorCode = WPWsAdminErrorCodes.WPWSADMIN_ERROR_UNKNOWN;

   public WPWsAdminException() {
      super();
   }

   public WPWsAdminException( String msg ) {
      super( msg );
   }

   public WPWsAdminException( String msg, int errorCode ) {
      super( msg );
   }

   /**
 * @param errorCode  the errorCode to set
 * @uml.property  name="errorCode"
 */
public void setErrorCode( int ec ) {
     errorCode = ec;
   }

   /**
 * @return  the errorCode
 * @uml.property  name="errorCode"
 */
public int  getErrorCode() {
     return errorCode;
   }


}

