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

package com.ibm.websphere.update.util;

import java.util.Properties;

import java.io.*;

public class WPCPConfig {

   private static final Properties WPCP_PROPS = new Properties();
   private static boolean initialized = false;

   public static boolean initialize( String wpcpHome ) {
      if (!initialized) {
         try {
            FileInputStream fin = new FileInputStream( wpcpHome + File.separator + "v5.0" + File.separator + "config" + File.separator + "wpcpconfig.properties" );
            WPCP_PROPS.load( fin );
            initialized = true;
            try { fin.close(); } catch ( Exception e ) {}
         } catch ( Exception e) {
            //e.printStackTrace();
         }
      }
      return initialized;

   }

   public static String getProperty( String propName ) {
      return WPCP_PROPS.getProperty( propName );
   }

   public static boolean hasProperty( String propName ) {
      return WPCP_PROPS.containsKey( propName );
   }
}
