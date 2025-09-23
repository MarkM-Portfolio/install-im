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

import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.product.WPProduct;
import java.io.File;

public class WPUpdateFileEntry {

   public static final String FILE_LOC_WPHOME   = "WPHOME";
   public static final String FILE_LOC_WPCPHOME = "WPCPHOME";
   public static final String FILE_LOC_WASHOME  = "WASHOME";
   public static final String FILE_LOC_DTDDIR   = "DTD";
   public static final String FILE_LOC_VERDIR   = "VERSION";
   public static final String FILE_LOC_CONFIG   = "CONFIG";


   String resourceName;
   File   theFile;

   public WPUpdateFileEntry( WPProduct wpsProduct,  String resourceName, String loc, String fileName ) {

      this.resourceName = resourceName;
      if (fileName == null) {
         fileName = determineFileName( resourceName );
      }

      if (loc == FILE_LOC_WPHOME) {
         theFile = new File( wpsProduct.getProductDirName(), fileName );
      } else if (loc == FILE_LOC_WPCPHOME) {
         theFile = new File( wpsProduct.getProductDirName(), fileName );
      } else if (loc == FILE_LOC_WASHOME) {
         theFile = new File( WPConfig.getProperty( "WasHome" ), fileName );
      } else if (loc == FILE_LOC_DTDDIR) {
         //System.out.println( this.getClass().getName() + "::WPUpdateFileEntry : DTD - " + wpsProduct.getDTDDirName() );
         theFile = new File( wpsProduct.getDTDDirName(), fileName );
      } else if (loc == FILE_LOC_VERDIR) {
         //System.out.println( this.getClass().getName() + "::WPUpdateFileEntry : VER - " + wpsProduct.getVersionDirName() );
         theFile = new File( wpsProduct.getVersionDirName(), fileName );
      } else if (loc == FILE_LOC_CONFIG) {
          theFile = new File( wpsProduct.getProductDirName() + File.separator + "config" + File.separator + "includes", fileName );
      } 


   }

   public WPUpdateFileEntry( WPProduct wpsProduct, String resourceName, String loc ) {
      this( wpsProduct, resourceName, loc, null );
   }

   private String determineFileName( String resourceName ) {
      String name = resourceName.replace( '\\', '/' );
      int idx = name.lastIndexOf( '/' );
      if (idx != -1) {
         name = name.substring( idx+1 ).trim();
      } else {
         name = name.trim();
      }
      return name;
   }

   public File getFile() {
      return theFile;
   }

   public String getResource() {
      return resourceName;
   }

}
