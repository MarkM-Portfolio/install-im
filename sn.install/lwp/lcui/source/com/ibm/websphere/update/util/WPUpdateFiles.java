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

import com.ibm.websphere.product.WPProduct;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class WPUpdateFiles {

   private List entries = new LinkedList();
   private WPProduct wpsProduct;

   byte[] buffer = null;

   public WPUpdateFiles( WPProduct wpsProduct ) {
      this.wpsProduct = wpsProduct;
   }

   public void addFileEntry( WPUpdateFileEntry e ) {
      entries.add( e );
   }

   public void addFileEntry( String res, String loc, String name  ) {
      entries.add( new WPUpdateFileEntry( wpsProduct, res, loc, name ) );
   }

   public void addFileEntry( String res, String loc ) {
      entries.add( new WPUpdateFileEntry( wpsProduct, res, loc ) );
   }

   public void updateFiles() {
      buffer = new byte[4096];
      Iterator iter = entries.iterator();
      while ( iter.hasNext() ) {
         WPUpdateFileEntry e = (WPUpdateFileEntry)iter.next();
         File theFile = e.getFile();
         if ( !theFile.exists()) {
            try {
               writeFile( theFile, e.getResource() );
            } catch ( Exception exc ) {
               exc.printStackTrace();
            }
         }
      }
      buffer = null;
   }

   private void writeFile( File file, String resourceName ) throws IOException {
      InputStream in = WPUpdateFiles.class.getResourceAsStream( resourceName );
      if ( in == null ) {
         throw new FileNotFoundException( resourceName );
      }
      //System.out.println( this.getClass().getName() + "::writeFile : " + file );
      file.getParentFile().mkdirs();
      OutputStream out = new FileOutputStream( file );
      int bytesRead;
      while ( (bytesRead = in.read( buffer )) != -1 ) {
         out.write( buffer, 0, bytesRead );
      }
      out.flush();
      try { out.close(); } catch ( Exception e ) {}
      try { in.close(); } catch ( Exception e ) {}
   }

}
