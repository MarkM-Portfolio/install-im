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

import com.ibm.lconn.update.util.LCUtil;
import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.xml.product.*;

import java.util.Properties;
import java.io.*;
import java.util.*;

/*
 * WPCheckFiles
 *
 * File Name, Component Name, Release
 * wps/fix/src/com/ibm/websphere/update/util/WPCheckFiles.java, wps.base.fix, wps5.fix
 *
 * @author: Steve Pritko
 * @version 1.24
 * Date: 4/5/05
 */

public class WPCheckFiles {

    // Program versioning ...
   public static final String pgmVersion = "1.24" ;
    // Program versioning ...
   public static final String pgmUpdate = "4/5/05" ;
   
   public static final String debugPropertyName = "com.ibm.websphere.update.util.debug" ;
   public static final String debugTrueValue = "true" ;
   public static final String debugFalseValue = "false" ;

   protected static boolean debug;

   static {
      String debugValue = System.getProperty(debugPropertyName);

      debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
   }
   

   private static boolean filesChecked = false;


   // Lotus Connections
   private static boolean bLCProduct = false;



   static void reset() {
      // reset checked flag
      filesChecked = false;
   }

   public static synchronized void checkFixPackFiles( String installDir, boolean wpcp ) {
      // for now nothing is different.....
      checkFiles( installDir, wpcp );
   }


   // Lotus Connections
   public static synchronized void checkFilesConnections( String installDir, boolean wpcp, boolean bLCProductIn ) 
   {
	   bLCProduct = bLCProductIn;
	   checkFiles( installDir, wpcp);
   }
   
   public static synchronized void checkFiles( String installDir, boolean wpcp ) {

      String debugValue = System.getProperty(debugPropertyName);
      debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );

      if (filesChecked) return;

      filesChecked = true;
      WPProduct wpProduct = new WPProduct( installDir );
      
      WPUpdateFiles fileUpdater = new WPUpdateFiles( wpProduct );
      loadCommonFiles( fileUpdater );

//      if (wpcp) {
//         checkWPCPStandaloneFiles( fileUpdater, installDir );
//      } else {
         checkWPFiles( fileUpdater, installDir );
//      }
//      if (updatePZNStandaloneFiles){
//          checkPZNStandaloneFiles( fileUpdater, installDir );
//      }
//      checkPlatformComponentFiles( fileUpdater );

      fileUpdater.updateFiles();

      //<d67054>  recovery from mismatched MP.product file in early versions of PUI
      //<d168072>  mismatch was for 5.x.  Portal 6.x shipped with the correct name and PUI6 is not used for Portal 5.x.  
/*      Iterator wpProductIterator = wpProduct.getProducts();
      while (wpProductIterator.hasNext()) {
          product tempProduct = (product) wpProductIterator.next();
          if (tempProduct.getId().equalsIgnoreCase("MP")) {
              if (tempProduct.getName().equalsIgnoreCase("IBM WebSphere Portal MultiPlatform")) {
                  debug("WPCheckFiles tempProduct.getId == \"" + tempProduct.getId() + "\"");
                  debug("WPCheckFiles tempProduct.getVersion == \"" + tempProduct.getVersion() + "\"");
                  debug("WPCheckFiles tempProduct.getName == \"" + tempProduct.getName() + "\"");
                  debug("WPCheckFiles found Portal Multiplatform, updating MP.product to Portal for Multiplatforms");
                  tempProduct.setName("IBM WebSphere Portal for Multiplatforms");
                  List tempList = new ArrayList(1);
                  tempList.add( tempProduct );
                  ProductWriter tempWriter = new ProductWriter();
                  // itlm.product has the same id as mp.product so do not do this!!!
                  tempWriter.emit( tempList, installDir + "/version/MP.product" );
              }
          }
      }
      //</d67054> recovery from mismatched MP.product file in early versions of PUI
      //<d168072>  mismatch was for 5.x.  Portal 6.x shipped with the correct name and PUI6 is not used for Portal 5.x.  
*/
   }

   private static void checkWPFiles( WPUpdateFiles fileUpdater, String installDir ) {
    
       //<d58815>
//       String productFile = getProductFile(installDir);
//       if ( productFile != null ) {
//           fileUpdater.addFileEntry( productFile, WPUpdateFileEntry.FILE_LOC_VERDIR );
//       }
       //</d58815>


       // Lotus Connections
       if(bLCProduct)
       {
    	   // config.component and common.component are for future need, (if needed)
    	   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/config.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/common.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
		   
    	   // lets do some checking so we only copy files for installed product
    	   if(null != LCUtil.productExist(installDir, "activities"))
    	   {
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/activities.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/productFiles/activities.product", WPUpdateFileEntry.FILE_LOC_VERDIR );
    	   }
    	   if(null != LCUtil.productExist(installDir, "blogs"))
    	   {
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/blogs.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/productFiles/blogs.product", WPUpdateFileEntry.FILE_LOC_VERDIR );
    	   }
    	   if(null != LCUtil.productExist(installDir, "communities"))
    	   {
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/communities.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/productFiles/communities.product", WPUpdateFileEntry.FILE_LOC_VERDIR );
    	   }
    	   if(null != LCUtil.productExist(installDir, "dogear"))
    	   {
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/dogear.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/productFiles/dogear.product", WPUpdateFileEntry.FILE_LOC_VERDIR );
    	   }
    	   if(null != LCUtil.productExist(installDir, "profiles"))
    	   {
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/profiles.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/productFiles/profiles.product", WPUpdateFileEntry.FILE_LOC_VERDIR );
    	   }
    	   if(null != LCUtil.productExist(installDir, "homepage"))
    	   {
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/homepage.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
    		   fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/productFiles/homepage.product", WPUpdateFileEntry.FILE_LOC_VERDIR );
    	   }
       }


   }

   private static void loadCommonFiles( WPUpdateFiles fileUpdater ) {
      // Would be nice to read these form a prop file or something.  but this willdo for now.
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/applied.dtd",               WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/applied.xsd",               WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/component.dtd",             WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/component.xsd",             WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/eventHistory.dtd",          WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/eventHistory.xsd",          WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/extension.dtd",             WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/extension.xsd",             WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/product.dtd",               WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/product.xsd",               WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/update.dtd",                WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/update.xsd",                WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/websphere.dtd",             WPUpdateFileEntry.FILE_LOC_DTDDIR );
      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/dtdFiles/websphere.xsd",             WPUpdateFileEntry.FILE_LOC_DTDDIR );

      
//      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/productFiles/platform.websphere",    WPUpdateFileEntry.FILE_LOC_VERDIR );   //<d58914/>

      
      //fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/pui.component",       WPUpdateFileEntry.FILE_LOC_VERDIR );   // <removed d63269 /*originally added d62252*//>
//      fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpproduct.component", WPUpdateFileEntry.FILE_LOC_VERDIR );   // <d62283/>
   }

   //<d58815>
   private static String getProductFile( String wpsHome ) {
   	  debug("WPCheckFiles:getProductFile(String) -- ENTER");
      Properties wpsProps = new Properties();
      try {
         FileInputStream fin = new FileInputStream( wpsHome + File.separator + "wps.properties" );
         wpsProps.load( fin );
         try { fin.close(); } catch ( Exception e ) {}
      } catch ( Exception e) {
         //e.printStackTrace();
      }
      String prod = wpsProps.getProperty( "WPFamilyName" );

      if (prod == null || prod.trim().length() == 0 ) {
      	debug("WPCheckFiles:getProductFile(String) -- prod is EMPTY or NULL");
      	try {
             //PZN v5.1.0.0
             //     - no wps.properties
             //     - no MP.product file
             //     - no PZN.product file  (do we care?)
             //     - <installDir>/pzn/v5.1/install.properties
             if (  
                   (new File(wpsHome + File.separator + "pzn" + File.separator + "v6.0" + File.separator + "install.properties" ).exists())
                   &&
                   (new File(wpsHome + File.separator + "version" + File.separator + "pzn.component").exists())
                   &&
                   !(new File(wpsHome + File.separator + "version" + File.separator + "MP.product").exists())
                 ) 
             {
                 //set flag to add additional .component file(s)
                 //updatePZNStandaloneFiles = true;
                 //we need the v5.1.0 version of the .product file . . .
             	 debug("WPCheckFiles:getProductFile(String) -- adding PZN.product");
                 return "/com/ibm/websphere/update/harness/productFiles/PZN.product";
             }
             //WBCR
             //    - no wps.properties
             //    - DOES have WBCR.product
             if ( new File(wpsHome + File.separator + "version" + File.separator + "WBCR.product").exists() )
             {
                 // nothing to do, WBCR already has .product/.component files
                 debug("WPCheckFiles:getProductFile(String) -- detected WBCR by WBCR.product file");
                 return null;
             }
             //WCS 2.5+
             //    - no wps.properties
             //    - DOES have WCS.product
             if ( new File(wpsHome + File.separator + "version" + File.separator + "WCS.product").exists() )
             {
                 // nothing to do, WCS already has .product/.component files
                 debug("WPCheckFiles:getProductFile(String) -- detected WCS by WCS.product file");
                 return null;
             }
          } catch ( Exception e) {
             //e.printStackTrace();
          }
      } else {
      	  debug("WPCheckFiles:getProductFile(String) -- prod == \"" + prod + "\"");
          //wps.properties was found . . .
          prod = prod.trim().toLowerCase();

          if (prod.equals( "multiplatform" ) ) {
          	 debug("WPCheckFiles:getProductFile(String) -- adding MP.product");
             return "/com/ibm/websphere/update/harness/productFiles/MP.product";
          } else if (prod.equals( "express" ) ) {
          	 debug("WPCheckFiles:getProductFile(String) -- adding EXPRESS.product");
             return "/com/ibm/websphere/update/harness/productFiles/EXPRESS.product";
          } else if (prod.equals( "isc" ) ) {
          	 debug("WPCheckFiles:getProductFile(String) -- adding ISC.product");
             return "/com/ibm/websphere/update/harness/productFiles/ISC.product";
          } else if (prod.equals( "toolkit" ) ) {
          	 debug("WPCheckFiles:getProductFile(String) -- adding TOOLKIT.product");
             return "/com/ibm/websphere/update/harness/productFiles/TOOLKIT.product";
          } else {
          	 debug("WPCheckFiles:getProductFile(String) -- cannot determine which .product to add.");
             return null;
          }
      }

      //don't expect to fall through to here . . .
      debug("WPCheckFiles:getProductFile(String) -- unexpectedly fell through to default return statement.");
      return null;
   }
   //</d58815>

   private static void checkPlatformComponentFiles( WPUpdateFiles fileUpdater ) {
      if ( PlatformUtils.isWindows() ) {
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpwindows.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         if ( PlatformUtils.isWin2000() ) {
            fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpwin2k.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         } else if ( PlatformUtils.isWin2003() ) {
            fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpwin2003.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         } else if ( PlatformUtils.isWinNT() ) {
            fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpwinnt.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         } else if ( PlatformUtils.isWinXP() ) {
            fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpwinxp.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         }
      } else if ( PlatformUtils.isAIX() ) {
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpunix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpaix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
      } else if ( PlatformUtils.isSolaris() ) {
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpunix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpsolaris.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
      } else if ( PlatformUtils.isLinux390() ) {
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpunix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpzlinux.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
      } else if ( PlatformUtils.isLinux() ) {
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpunix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wplinux.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
      } else if ( PlatformUtils.isHpux() ) {
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpunix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wphp.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
      } else if ( PlatformUtils.isISeries() ) {
         //fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpunix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpiseries.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
      } else if ( PlatformUtils.isZOs() ) {
         //fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpunix.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
         fileUpdater.addFileEntry( "/com/ibm/websphere/update/harness/componentFiles/wpzos.component", WPUpdateFileEntry.FILE_LOC_VERDIR );
      }
   }
   
   protected static void debug(String arg) {
       if ( !debug )
           return;

       System.out.println(arg);
   }

   protected static void debug(String arg1, String arg2) {
       if ( !debug )
           return;

       System.out.print(arg1);
       System.out.println(arg2);
   }

}
