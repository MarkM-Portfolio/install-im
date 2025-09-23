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

package com.ibm.websphere.update.silent;


import com.ibm.lconn.update.util.LCUtil;
import com.ibm.lconn.wizard.update.data.*;
import com.ibm.websphere.product.*;
import com.ibm.websphere.product.formatters.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.BaseHandlerException;
import com.ibm.websphere.product.xml.CalendarUtil;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.product.xml.extension.extension;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.product.xml.websphere.websphere;
import com.ibm.websphere.update.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.efix.*;
import com.ibm.websphere.update.efix.prereq.*;
import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;
import com.ibm.websphere.update.msg.*;
import com.ibm.websphere.update.util.CommentedProperties;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPCheckFiles;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import com.ibm.websphere.update.util.WPUpdateFileEntry;
import com.ibm.websphere.update.util.WPUpdateFiles;
import com.ibm.websphere.update.util.was.CheckWASStatus;
import com.ibm.websphere.update.util.was.WASNodeType;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.SAXParseException;

/**
 * Class: BaseInstaller.java Abstract: Base Installer Component Name: WAS.ptf Release: ASV50X File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/silent/BaseInstaller.java, wps.base.fix, wps5.fix History 1.27, 8/4/05 01-Nov-2002 Initial Version
 */
public abstract class BaseInstaller {

	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "1.27" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "8/4/05" ;

	//********************************************************
	//  Instance State
	//********************************************************
	public static final int INSTALL = 0;
	public static final int UNINSTALL = 1;
	
	protected WPProduct wpsProduct;
	protected WPHistory wpsHistory;

	// Install state helpers ...
	protected static final int TOTAL_OFFSET = 0 ;
	// Install state helpers ...
	protected static final int TOTAL_INSTALLED_OFFSET = 1 ;
	// Install state helpers ...
	protected static final int TOTAL_INSTALLABLE_OFFSET = 2 ;
	protected static final String[] componentIds = { };
	
	protected boolean didInitialize = false;
	private boolean allStopped = false;

    // Is this a IBM Connections update?
    private boolean bLCProduct = false;

    Properties props = new Properties();
    
	//********************************************************
	//  Method Definitions
	//********************************************************

	//Required initialization methods
	protected abstract void initializeRepository(String reposDir);

	protected abstract void initializeUpdaterForInstall();

	protected abstract void initializeUpdaterForUninstall();
	
	protected abstract boolean initialize();

	protected abstract boolean initializeProduct();

   // *** WARNING **
   // This code is duplicated in update.ismp.util.ValidateWPInstallLoc, if this code is change make the appropriate changes 
   //   in that version as well.
	protected boolean commonInitialize( UpdateInstallerArgs args ) {
		if (didInitialize)
			return true;

               boolean isUninstallable = true;

                if (UpdateInstallerArgs.isThisUninstall == 1 &&  UpdateInstallerArgs.isThisUninstallforFixpack == 1) {
                    isUninstallable = getIsPTFUninstallable(UpdateInstallerArgs.thisIsFixpackID, args );
                    //System.out.println("This is uninstallable " +    isUninstallable);



                }

                if (isUninstallable == false) {
                
                  UpdateInstaller.puiReturnCode = 8;
	          System.out.println( "This PTF " + UpdateInstallerArgs.thisIsFixpackID + "  can not be uninstalled because it has NOT been successfully installed. Please contact support for assistance. " );
                  return false;
                }

                //System.out.println("UpdateInstallerArgs.isThisUninstall " + UpdateInstallerArgs.isThisUninstall);
                //System.out.println("UpdateInstallerArgs.isThisUninstallforFixpack " + UpdateInstallerArgs.isThisUninstallforFixpack);
                //System.out.println("UpdateInstallerArgs.thisIsFixpackID " + UpdateInstallerArgs.thisIsFixpackID);

              

        isConnectionsInstallDir(args.installDir, args);
        // Are we updating a IBM Connections product?                
        if(!LCUtil.isLCHome(args.installDir)){
        	System.out.println(MessageFormat.format(UpdateReporter.getSilentString("error.invalid.lc.home"), new Object[]{args.installDir}));
        }
        if (null != args.featureCustomBacked){
       	 if(args.featureCustomBacked.equals("yes")){
       		 System.out.println(UpdateReporter.getSilentString("efix.featureCustomizationBackedUp.done"));
       	 }else if(args.featureCustomBacked.equals("no")){
       		 System.out.println(UpdateReporter.getSilentString("efix.no.featureCustomization"));
       	 }else{
       		 System.err.println(UpdateReporter.getSilentString("WUPD0024E", args.featureCustomBacked));
       		 return false;
       	 }
        }
	 // Do post Args processing.
	 // Read wpconfig ( maybe wpcpconfig ) properties.
	 // This readswpcpconfig and then wpconfig, so wpconfig will override.  This also then works in the wpcp standalone case.
	 if ( args.installDirInput ) {
		     //<d148969> Invalid -installDir parameter does not reveal helpful message </d148969>
             // Do some checking on the installDir, to se if its valid.
             // For non -wpcpOnly case we can check for wps.properties in root dir, for -wpcpOnly check for wpcp dir
             File wpHome = new File( args.installDir );
             if ( ! (wpHome.exists() && wpHome.isDirectory()) ) {
                printDirInvalid("installDir", args.installDir);
                return false;
             }

             if(bLCProduct)
             {
            	 //System.out.println("is a IBM Connections install");
            	 args.setInstallDir(args.installDir);
             }

           // this has no use
             else
             {
                 if (!WPConfig.initialize( args.installDir )) {
                     printDirInvalid("installDir", args.installDir);
                     return false;
                 }
             }

             //<d66861> fixpackDir is not a required parameter </d66861>
             if ( null != args.fixPackDir && !args.fixPackDir.equals("") ) {
                 File fixPackDir = new File( args.fixPackDir );
                 if ( ! (fixPackDir.exists() && fixPackDir.isDirectory()) ) {
                     printDirInvalid("fixPackDir", args.fixPackDir);
                     // fixpackDir is checked for valid fixpacks in PTFInstaller.java ...
                     return false;
                 }
             }

             //<d149969> make the change for efixDir too </d149969>
             if ( null != args.efixDir && !args.efixDir.equals("") ) {
                 File efixDir = new File( args.efixDir );
                 if ( ! (efixDir.exists() && efixDir.isDirectory()) ) {
                     printDirInvalid("efixDir", args.efixDir);
                     // efixDir is checked for valid fixes elsewhere EFixInstaller.java ...
                     return false;
                 }
             }

             

             // BKB. 02/27/2007. Enable this code at a later date.  

             /*

             if ( (UpdateInstallerArgs.thisIsFixpackID != null) && (UpdateInstallerArgs.thisIsFixpackID.length() > 0) && (UpdateInstallerArgs.thisIsFixpackID.startsWith("WP_PTF_6")) )
              {
                     System.out.println("   " + UpdateInstallerArgs.thisIsFixpackID + "Checking for Passwords ... " );
                     System.out.println("");

                     boolean wpPasswords = checkPasswordsForWPFixpack();
                     boolean dbPasswords = checkDBPasswordsForWPFixpack();

                     if (wpPasswords == true && dbPasswords == true)
                     {
                     }
                     else if (wpPasswords == false && dbPasswords == false) 
                     {
                         UpdateInstaller.puiReturnCode = 8;
                         System.out.println( "This PTF " + UpdateInstallerArgs.thisIsFixpackID + "  can not be installed/uninstalled because PortalAdminPwd, WasPassword and DbPasswords have not been entered in wpconfig.properties and wpconfig_dbdomain.properties. " );
                         return false;
                     }
                     else if (wpPasswords == false) 
                     {
                         UpdateInstaller.puiReturnCode = 8;
                         System.out.println( "This PTF " + UpdateInstallerArgs.thisIsFixpackID + "  can not be installed/uninstalled because PortalAdminPwd, WasPassword have not been entered in wpconfig.properties. " );
                         return false;
                     }
                     else
                     {
                         UpdateInstaller.puiReturnCode = 8;
                         System.out.println( "This PTF " + UpdateInstallerArgs.thisIsFixpackID + "  can not be installed/uninstalled because DbPasswords have not been entered in wpconfig_dbdomain.properties. " );
                         return false;
                     }



               }
              */             
               

             if ( bLCProduct == true ) {
                 //OK to continue
                 // Lc creates a lc.properties, we can use that if needed
                 //System.out.println("wpconfig.initialize()");
                 WPConfig.initialize( args.installDir );
             } else {
                 return false;
             }

         }

      // Check to see if args contains a properties file.  if so load it now.
      String propname = args.getConfigPropertiesName();
      if ( propname != null ) {
         if ( !WPConfig.loadConfiguration( propname ) ) {
            System.out.println(
               UpdateReporter.getSilentString("error.reading.property.file", propname));
            return false;
         }
         try {
            File propfile = new File( propname );
            propname = propfile.getAbsolutePath();
         } catch ( Exception e) {}
      } else {
         // Make sure we always have a value, so it can be placed in WPConfig.
         propname = "";
      }

      // Place propname in WPConfig so we have it later in case we need to shell out.
      WPConfig.setProperty( WPConstants.PROP_WP_PUI_CFG, propname );

	  // Do we have all the needed files for IBM Connections by PUI?
      // if not copy them over
      if (bLCProduct) {
		  WPCheckFiles.checkFilesConnections(args.installDir, false, bLCProduct);
	  }
	  else
	  {
      

		// Setup WAS_PROD_HOME to WAS_HOME this is needed so we always have the real WAS Home ( PROD_DATA on iSeries )
		WPConfig.setProperty( WPConstants.PROP_WAS_PROD_HOME, WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) );
		if (PlatformUtils.isISeries()) {
		   // On iSeries we need to remap WAS_USER_HOME to WAS Home and WAS_HOMe to WAS_PROD_HOME
		   WPConfig.setProperty( WPConstants.PROP_WAS_HOME, WPConfig.getProperty( WPConstants.PROP_WAS_USER_HOME ) );
		   WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, WPConfig.getProperty( WPConstants.PROP_WAS_USER_HOME ) );
		}

		if (args.wpcpOnly) {
		   WPConfig.setProperty( WPConstants.PROP_WPCP_ONLY, "true" ); 
		}

				
		else
		{
		  WPCheckFiles.checkFiles( args.installDir, args.wpcpOnly );
		}

		if ( !verifyWASHome() ) return false;
		if ( !verifyWPConfigData() ) return false;

	  }

      // don't need to check server status for IBM Connections
      // we may have to change this for special case, when updating the extension jars
	  if (bLCProduct) 
	  {
		if (initializeProduct())
		{
		   return true;
		} else
		{
		   return false;
		}
	  }
	  
	  else
	  {
      
		if (initializeProduct()) {
		  if (!allStopped){
			  System.out.println( "Checking the status of all Application Servers" );
			  CheckWASStatus statusChecker = new CheckWASStatus( WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ) );
			  statusChecker.setLog( System.out );
//			  allStopped = true;
			  allStopped = statusChecker.checkAllInstances( false );
			  if (!allStopped) {
                                  UpdateInstaller.puiReturnCode = 8;
				  System.out.println( "Some Application Servers are still running, all Application Servers must be stopped" );
			  }
		  }
		  // this is the only place this function can return 'true' . . .
		  return allStopped;
		} else {
			return false;
		}
      
	  }
     
   }

	protected boolean commonInitializeProduct(  UpdateInstallerArgs args  ) {
	  // We need to reaquire a WPProduct because we may have replaced some missing DTD files.

		wpsProduct = new WPProduct(args.installDir);
		String productDirName = wpsProduct.getProductDirName();
		String versionDirName = wpsProduct.getVersionDirName();
		String historyDirName = WPHistory.determineHistoryDirName(versionDirName);

		wpsHistory = new WPHistory(productDirName, versionDirName, historyDirName);

		return UpdateReporter.handleErrors(wpsProduct, wpsHistory, System.err, args.printStack);
	}

   protected boolean verifyWP5InstallDir( UpdateInstallerArgs args, File wpHome ) {
      if (args.wpcpOnly) {
         File subdir = new File( wpHome, "wpcp" );
         if (!isValidDir( subdir, "error.setup.wpcpdir.invalid", wpHome.toString() ) )  return false;

         subdir = new File( subdir, "v5.0" );
         if (!isValidDir( subdir, "error.setup.wpcpdir.invalid", wpHome.toString() ) )  return false;

      } else {
         Properties wpsProps = new Properties();
         try {
            File wpsPropFile = new File( wpHome, "wps.properties" );
            if ( ! (wpsPropFile.exists() && wpsPropFile.isFile()) ) {
               //System.out.println( "Unable to locate wps.properties in installDir (" + args.installDir + ")." );
               System.out.println( UpdateReporter.getSilentString( "error.setup.wps.props.missing", args.installDir ) );
               return false;
            }
            FileInputStream fin = new FileInputStream( wpHome + File.separator + "wps.properties" );
            wpsProps.load( fin );
            try { fin.close(); } catch ( Exception e ) {}
         } catch ( Exception e) {
            //System.out.println( "Error reading wps.properties in installDir (" + args.installDir + ") - " + e.getMessage() );
            System.out.println( UpdateReporter.getSilentString( "error.setup.wps.props.reading", args.installDir, e.getMessage() ) );
            return false;
            //e.printStackTrace();
         }
         // <defect id=63269>
         String family = wpsProps.getProperty( "WPFamilyName" );
         if (family == null || family.trim().length() == 0) {
            // No family name. make sure we at least have a .product file.
            File subdir = new File( wpHome, "version" );
            if (!isValidDir( subdir, "error.setup.wp.product.missing", wpHome.toString() ) )  return false;  // version doesn't exists, can't have product file....

            // Look for any files ending w/ .product
            final File versionDir = subdir;
            String [] productFiles = versionDir.list( new FilenameFilter() {
                  public boolean accept(File dir, String name) {
                     // Make sure file is in version dor ( not a sub dir ) and ends w/  .product
                     return ( dir.equals( versionDir ) && name.toLowerCase().endsWith( ".product" ) );
                  }
               } );
            // If we found none, its an error.
            if ( productFiles == null || productFiles.length == 0 ) {
               System.out.println( UpdateReporter.getSilentString( "error.setup.wp.product.missing", args.installDir ) );
               return false;
            }
         }
         // </defect id=63269>

         String version = wpsProps.getProperty( "version" );
         if ( !version.startsWith( "6." ) ) {
            //System.out.println( "Supplied installDir " + args.installDir + " appears to be referencing an unsupported version of WebSphere Portal version " + version + " is not 6.x." );
            System.out.println( UpdateReporter.getSilentString( "error.setup.wp.version.invalid", args.installDir, version ) );
            return false;
         }

      }
      return true;
   }

   boolean verifyPzn51InstallDir(File wpHome) {
       //clean install of v5.1.0 PZN Standalone:
   	   //  - pzn.component. 
       //  - no wps.properties
       //  - no .product files
       //
       //after WPCheckFiles has run at least once:
       //  - no wps.properties
   	   //  - PZN.product
   	   //  - pzn.component
   	   //  - possibly other components (platform markers)
       File wpsPropFile = new File( wpHome, "wps.properties" );
       if (  (wpsPropFile.exists() && wpsPropFile.isFile()) ) {
           //System.out.println( "BaseInstaller:verifyPzn51InstallDir(File) -- returning FALSE (found wps.properties)" );       	
           return false;
       }
       // 
       //checking for version dir: If not found, it's not PZN standalone
       File subdir = new File( wpHome, "version" );
       if ( !(subdir.exists() && subdir.isDirectory()) ) {
           //version dir doesn't exist, can't have component/product files, so no point in continuing . . .
       	   return false;
       }
       //
       // Evaluate .product files
       final File versionDir = subdir;
       String [] productFiles = versionDir.list( new FilenameFilter() {
             public boolean accept(File dir, String name) {
                // Make sure it is a file in the version dir ( not a sub dir ) and ends w/  .product
                return ( dir.equals( versionDir ) && name.toLowerCase().endsWith( ".product" ) );
             }
          } );

       // If we found no .product files, it might be the first time 
       // PUI has been run on PZN standalone . . .
       if ( productFiles == null || productFiles.length < 1 ) {
       	   //get the names of the .component files on the system
       	   String [] componentFiles = versionDir.list( new FilenameFilter() {
               public boolean accept(File dir, String name) {
                   // Make sure it is a file in the version dir ( not a sub dir ) and ends w/  .component
                   return ( dir.equals( versionDir ) && name.toLowerCase().endsWith( ".component" ) );
               }
           } );
       	   //
       	   //pzn.component found in the absence of any .product
       	   //files is sufficient to identify PZN standalone.
       	   for ( int i=0 ; i<componentFiles.length ; i++ ){
       	   	   if ( componentFiles[0].equalsIgnoreCase("pzn.component") ) {
       	   	   	   return true;
       	   	   }
       	   }
           //       	
       } else if ( productFiles.length > 1 ) {
           //Personalization has at most one .product file . . .
           return false;
       } else if ( productFiles[0].equalsIgnoreCase("PZN.product") ) {
           //return true if PZN.product, and only PZN.product, is found.
           return true;
       }
       return false;  //all NOC
   }

   boolean verifyWBCRInstallDir(File wpHome) {
       //WBCR has:
       //  - at least one .component file 
       //  - no wps.properties
       //  - WBCR.product file 
       File wpsPropFile = new File( wpHome, "wps.properties" );
       if (  (wpsPropFile.exists() && wpsPropFile.isFile()) ) {
           //System.out.println( "BaseInstaller:verifyWBCRInstallDir(File) -- returning FALSE (found wps.properties)" );           
           return false;
       }
       //
       // Evaluate .product files
       final File versionDir = new File( wpHome, "version" );
       String [] productFiles = versionDir.list( new FilenameFilter() {
             public boolean accept(File dir, String name) {
                // Make sure it is a file in the version dir ( not a sub dir ) and ends w/  .product
                return ( dir.equals( versionDir ) && name.toLowerCase().endsWith( ".product" ) );
             }
          } );
       //
       // If we found no .product files, it is not WBCR 
       if ( productFiles == null || productFiles.length < 1 ) {
           return false;
       } 
       //else there is at least one .product file
       for ( int i = 0; i < productFiles.length; i++ ) {
           if ( productFiles[i].equalsIgnoreCase("WBCR.product") ) {
               return true;
           }
       }

       return false;  //all NOC
   }

   
   boolean verifyWBSEInstallDir(File wpHome) {
       //WBSE has:
       //  - at least one .component file 
       //  - no wps.properties
       //  - WBSE.product file 
       File wpsPropFile = new File( wpHome, "wps.properties" );
       if (  (wpsPropFile.exists() && wpsPropFile.isFile()) ) {
           //System.out.println( "BaseInstaller:verifyWBCRInstallDir(File) -- returning FALSE (found wps.properties)" );           
           return false;
       }
       //
       // Evaluate .product files
       final File versionDir = new File( wpHome, "version" );
       String [] productFiles = versionDir.list( new FilenameFilter() {
             public boolean accept(File dir, String name) {
                // Make sure it is a file in the version dir ( not a sub dir ) and ends w/  .product
                return ( dir.equals( versionDir ) && name.toLowerCase().endsWith( ".product" ) );
             }
          } );
       //
       // If we found no .product files, it is not WBSE 
       if ( productFiles == null || productFiles.length < 1 ) {
           return false;
       } 
       //else there is at least one .product file
       for ( int i = 0; i < productFiles.length; i++ ) {
           if ( productFiles[i].equalsIgnoreCase("WBSE.product") ) {
               return true;
           }
       }

       return false;  //all NOC
   }


 

   
   // *** WARNING **
   // This code is duplicated in update.ismp.util.ValidateWPInstallLoc, if this code is change make the appropriate changes 
   //   in that version as well.
   protected boolean verifyWASHome() {
      // Make sure WasHome exists and is a dir
      // Instantiate a WASProduct and check WAS version 5.0. or above.
      //    Check for embeddedEXPRESS/EXPRESS for ISC (bobcat check )
      //    BASE is needed for all others.
      String wasHome = WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME );
      if (wasHome == null || wasHome.length() == 0 ) {
         System.out.println( UpdateReporter.getSilentString( "error.setup.washomedir.invalid", wasHome ) );
         System.out.println( "verifyWASHome ERROR_0" );
         return false;
      }
      if (!isValidDir( wasHome, "error.setup.washomedir.invalid", wasHome ) )  return false;

      WASProduct wp = new WASProduct( wasHome );
      if (wp.numExceptions() > 0) {
         System.out.println( UpdateReporter.getSilentString( "error.setup.washomedir.invalid", wasHome ) );
         System.out.println( "verifyWASHome ERROR_1" );
         return false;
      }
      Iterator iter = wp.getProducts();
      boolean propOK = false;
      while ( !propOK && iter.hasNext() ) {
         product thisProd = (product)iter.next();
         String prodName  = thisProd.getName();
         String prodID    = thisProd.getId();
         String ver       = thisProd.getVersion();
//         System.out.println( "Checking Product : " + prodName + "(" + prodID + ")" );
//         System.out.println( "Checking version : " + prodName + "(" + ver + ")" );
         if ( prodID.equals( "BASE" ) || prodID.equals("ND") ) {
            if (    ver.startsWith( "5." ) 
                 || ver.startsWith( "6." )) propOK = true;
         } else if ( prodID.equals( "embeddedEXPRESS" ) ) {
             if (    ver.startsWith( "5." ) 
                 || ver.startsWith( "6." )) propOK = true;
         } else if ( prodID.equals( "EXPRESS" ) ) {
             if (    ver.startsWith( "5." ) 
                 || ver.startsWith( "6." )) propOK = true;
         }
      }
      if ( !propOK ) {
         System.out.println( UpdateReporter.getSilentString( "error.setup.washomedir.invalid", wasHome ) );
         System.out.println( "verifyWASHome ERROR_2" );
      }

      return propOK;

   }


   // *** WARNING **
   // This code is duplicated in update.ismp.util.ValidateWPInstallLoc, if this code is change make the appropriate changes 
   //   in that version as well.
   protected boolean verifyWPConfigData() {
      /* 
      Check the following properites
        CellName
        NodeName
        WasUserid
        WasPassword
        ServerName
      If there are not present or don't appear to be valid, we report a warning, but don't fail.  They may not be 
        needed for the current processing.  Further processing will fail the action if they are needed.
      */
      
      //WAS 6 has profiles, so we need to find which profile Portal is installed on
      //Start by looking in wpconfig.bat/sh to find the fsdbscript that contains the path
      //   to the setupCmdLine for the correct profile.  If this isn't found, it's not WAS 6
      // String wpHome = WPConfig.getProperty( WPConstants.PROP_WP_HOME );
      String wpHome = WPConfig.getProperty( WPConstants.PROP_TRUE_WP_HOME );

      String scriptExtension = PlatformUtils.isWindows() ? ".bat" : ".sh";
      String wpsConfigScriptName = wpHome + File.separator + "config" + File.separator + "WPSconfig" + scriptExtension;
      File wpsConfigScript = new File(wpsConfigScriptName);
      String fsdbScriptName = null;
      try {
         FileInputStream wpsCSFileInputStream = new FileInputStream(wpsConfigScript);
         InputStreamReader wpsCSInputStreamReader = new InputStreamReader(wpsCSFileInputStream);
         BufferedReader wpsCSBufferedReader = new BufferedReader(wpsCSInputStreamReader);

         String currLine = wpsCSBufferedReader.readLine();
         while (currLine != null && fsdbScriptName == null) {
            if (currLine.indexOf("properties" + File.separator + "fsdb") != -1 && currLine.indexOf("#") == -1) {
               StringTokenizer lineTokenizer = new StringTokenizer(currLine,"/\\");
			   fsdbScriptName = lineTokenizer.nextToken();
               while (lineTokenizer.hasMoreTokens() && !fsdbScriptName.equals("fsdb")) {
                  fsdbScriptName = lineTokenizer.nextToken();
               }
               fsdbScriptName = lineTokenizer.nextToken();
               lineTokenizer = new StringTokenizer(fsdbScriptName,"\"");
               fsdbScriptName = lineTokenizer.nextToken();
            }
            currLine = wpsCSBufferedReader.readLine();
         }
      } catch (IOException e) {
         System.out.println( UpdateReporter.getSilentString( "error.reading.property.file", wpsConfigScriptName ) );
      }

      String userInstallRoot = null;
      String wasHome = WPConfig.getProperty( WPConstants.PROP_WAS_HOME );
      fsdbScriptName = wasHome + File.separator + "properties" + File.separator + "fsdb" + File.separator + fsdbScriptName;
      File fsdbScript = new File(fsdbScriptName);
      if (PlatformUtils.isISeries()) { // if ISeries, use WAS_USER_HOME
         userInstallRoot = WPConfig.getProperty( WPConstants.PROP_WAS_USER_HOME );
         WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT , userInstallRoot);
      } else if (!fsdbScript.exists()) { // if fsdbScript isn't found, it's not WAS6, use WAS_HOME
         userInstallRoot = WPConfig.getProperty( WPConstants.PROP_WAS_HOME );
         WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT , userInstallRoot);
      } else { // It's WAS 6, so we need to find the USER_INSTALL_ROOT in the WAS_USER_SCRIPT
         //Find the WAS_USER_SCRIPT inside the fsdbScript
         String wasUserScriptName = null;
         try {
            FileInputStream fsdbSFileInputStream = new FileInputStream(fsdbScript);
            InputStreamReader fsdbSInputStreamReader = new InputStreamReader(fsdbSFileInputStream);
            BufferedReader fsdbSBufferedReader = new BufferedReader(fsdbSInputStreamReader);

            String currLine = fsdbSBufferedReader.readLine();
            while (currLine != null && wasUserScriptName == null) {
               if (currLine.indexOf("WAS_USER_SCRIPT") != -1 && currLine.indexOf("#") == -1) {
                  StringTokenizer lineTokenizer = new StringTokenizer(currLine,"=");
                  lineTokenizer.nextToken();
                  wasUserScriptName = lineTokenizer.nextToken();
                  WPConfig.setProperty( WPConstants.PROP_WAS_USER_SCRIPT, wasUserScriptName );
		       }
		       currLine = fsdbSBufferedReader.readLine();
            }
         } catch (IOException e) {
            System.out.println( UpdateReporter.getSilentString( "error.reading.property.file", fsdbScriptName ) );
         }

         //Find the USER_INSTALL_ROOT using WAS_USER_SCRIPT (which is the setupCmdLine for the profile we want)
         File wasUserScript = new File(wasUserScriptName);
         try {
            FileInputStream wUSFileInputStream = new FileInputStream(wasUserScript);
            InputStreamReader wUSInputStreamReader = new InputStreamReader(wUSFileInputStream);
            BufferedReader wUSBufferedReader = new BufferedReader(wUSInputStreamReader);

            String currLine = wUSBufferedReader.readLine();
            while (currLine != null && userInstallRoot == null) {
               if (currLine.indexOf("USER_INSTALL_ROOT") != -1 && currLine.indexOf("#") == -1) {
                  StringTokenizer lineTokenizer = new StringTokenizer(currLine,"=");
                  lineTokenizer.nextToken();
                  userInstallRoot = lineTokenizer.nextToken();
                  lineTokenizer = new StringTokenizer(userInstallRoot,"\"");
                  userInstallRoot = lineTokenizer.nextToken();
                  WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, userInstallRoot );
               }
               currLine = wUSBufferedReader.readLine();
            }
         } catch (IOException e) {
            System.out.println( UpdateReporter.getSilentString( "error.reading.property.file", fsdbScriptName ) );
            WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, wasHome );
         }
      }
      
      //Get cell and node names
	  String cellName = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
      boolean cellNameEmpty = cellName == null || cellName.length() == 0;
      String nodeName = WPConfig.getProperty( WPConstants.PROP_WAS_NODE );
      boolean nodeNameEmpty = nodeName == null || nodeName.length() == 0;
      if ( cellNameEmpty || nodeNameEmpty) {
         //On WAS 6, cellName and nodeName won't be in wpconfig.properties, so we need to find it in setupCmdLine
         String setupCmdLineFileName = userInstallRoot + File.separator + "bin" + File.separator + "setupCmdLine" + scriptExtension;
         File setupCmdLineFile = new File(setupCmdLineFileName);
         try {
            FileInputStream sCLFileInputStream = new FileInputStream(setupCmdLineFile);
            InputStreamReader sCLInputStreamReader = new InputStreamReader(sCLFileInputStream);
            BufferedReader sCLBufferedReader = new BufferedReader(sCLInputStreamReader);

            String currLine = sCLBufferedReader.readLine();
            while (currLine != null && (cellNameEmpty || nodeNameEmpty)) {
               if (cellNameEmpty && currLine.indexOf("WAS_CELL") != -1 && currLine.indexOf("#") == -1) {
                  StringTokenizer lineTokenizer = new StringTokenizer(currLine,"=");
                  lineTokenizer.nextToken();
                  cellName = lineTokenizer.nextToken();
                  cellNameEmpty = false;
                  WPConfig.setProperty( WPConstants.PROP_WAS_CELL, cellName );
               }
               if (nodeNameEmpty && currLine.indexOf("WAS_NODE") != -1 && currLine.indexOf("#") == -1) {
               	  StringTokenizer lineTokenizer = new StringTokenizer(currLine,"=");
               	  lineTokenizer.nextToken();
               	  nodeName = lineTokenizer.nextToken();
               	  nodeNameEmpty = false;
               	  WPConfig.setProperty( WPConstants.PROP_WAS_NODE, nodeName );
               }
               currLine = sCLBufferedReader.readLine();
            }
         } catch (IOException e) {
            System.out.println( UpdateReporter.getSilentString( "error.reading.property.file", setupCmdLineFileName ) );
         }
      }

      boolean configOK = true;

      if ( cellNameEmpty ) {
         System.out.println( UpdateReporter.getSilentString( "warning.setup.wascell.notspecified" ) );
         cellName = null;
         configOK = true;
      }
      if ( nodeNameEmpty ) {
         System.out.println( UpdateReporter.getSilentString( "warning.setup.wasnode.notspecified" ) );
         nodeName = null;
         configOK = true;
      }

      String serverName = WPConfig.getProperty( WPConstants.PROP_WP_SERVER );
      if ( serverName == null || serverName.length() == 0 ) {
         System.out.println( UpdateReporter.getSilentString( "warning.setup.wasserver.notspecified" ) );
         serverName = null;
         configOK = true;
      }

      String prop = WPConfig.getProperty( WPConstants.PROP_WAS_USER );
      if ( prop == null || prop.length() == 0 ) {
         System.out.println( UpdateReporter.getSilentString( "warning.setup.wasuser.notspecified" ) );
      }

      prop = WPConfig.getProperty( WPConstants.PROP_WAS_PASS );
      if ( prop == null || prop.length() == 0 ) {
         System.out.println( UpdateReporter.getSilentString( "warning.setup.waspass.notspecified" ) );
      }

      File currentConfigDir = new File( userInstallRoot, "config" + File.separator + "cells" );
      if ( configOK && (cellName != null) ) {
         configOK = false;  // Asuume its false, will be set back to true if really is true
         currentConfigDir = new File( currentConfigDir, cellName );
         if ( isValidDir( currentConfigDir, "warning.setup.wascell.invalid", cellName ) ) {
            if ( nodeName != null ) {
               currentConfigDir = new File( currentConfigDir, "nodes" + File.separator + nodeName );
               if ( isValidDir( currentConfigDir, "warning.setup.wasnode.invalid", nodeName ) ) {     
                  if ( serverName != null ) {
                     currentConfigDir = new File( currentConfigDir, "servers" + File.separator + serverName );
                     if ( ! (currentConfigDir.exists() && currentConfigDir.isDirectory()) ) {
                        // Not a vlid server, ty a cluster
                        currentConfigDir = new File( userInstallRoot + "config" + File.separator + "cells" + cellName + "clusters" + serverName );
                        configOK = isValidDir( currentConfigDir, "warning.setup.wasserver.invalid", serverName );
                     } else {
                        configOK = true;
                     }
                  }
               }
            }

         }
      }
      
     // WASNodeType.isManagedNode() finds cell based on WPConfig properties, and returns true if cell is not standalone
      String usingWasDM = WPConfig.getProperty( WPConstants.PROP_WAS_DM );
      if ( usingWasDM == null || usingWasDM.equals("") ) {
         WASNodeType nodeType = new WASNodeType();
         // Java 1.3 doesn't have a static Boolean.toString(boolean), so to support WP 5.0, need to create a Boolean
         Boolean nodeTypeBool = new Boolean(nodeType.isManagedNode());
         usingWasDM = nodeTypeBool.toString();
         WPConfig.setProperty( WPConstants.PROP_WAS_DM, usingWasDM );
      }

      boolean wasDM = false;
      if ( usingWasDM != null ) {
         usingWasDM = usingWasDM.trim().toLowerCase();
         wasDM =  (usingWasDM.equals( "true" ) || usingWasDM.equals( "yes" ) || usingWasDM.equals( "1" ) );
      }

      System.out.println( "Configuration Properties:" );
      System.out.println( "\tWAS Home:"   + WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) );
      System.out.println( "\tUserInstallRoot:" + userInstallRoot);
      System.out.println( "\tCellName:"   + cellName );
      System.out.println( "\tNodeName:"   + nodeName );
      System.out.println( "\tServerName:" + serverName );

      System.out.println( "\tWAS DMgr processing:" + wasDM );

      // Right now always return true
      return configOK;

   }

   private boolean isValidDir( String checkDir, String msgId, String msgArg ) {
      return isValidDir( new File( checkDir ), msgId, msgArg );
   }

   private boolean isValidDir( File checkDir, String msgId, String msgArg ) {
      if ( ! (checkDir.exists() && checkDir.isDirectory()) ) {
         System.out.println( UpdateReporter.getSilentString( msgId, msgArg ) );
         return false;
      }
      return true;
   }

   /** The array of known product ids. */
   public static final String LC_IDS[] = {
	   LCUtil.PRODUCTID_LC_ACTIVITIES,
	   LCUtil.PRODUCTID_LC_BLOGS,
	   LCUtil.PRODUCTID_LC_COMMUNITIES,
	   LCUtil.PRODUCTID_LC_DOGEAR,
      LCUtil.PRODUCTID_LC_PROFILES,
      LCUtil.PRODUCTID_LC_WIKIS,
      LCUtil.PRODUCTID_LC_SEARCH,
      LCUtil.PRODUCTID_LC_FILES,
      LCUtil.PRODUCTID_LC_NEWS,
      LCUtil.PRODUCTID_LC_MOBILE,
      LCUtil.PRODUCTID_LC_FORUM
   };

	// do we have a IBM Connections dir?
	public boolean isConnectionsInstallDir(String installDir, UpdateInstallerArgs args)
	{
	      
		bLCProduct = false;
		
		File insDir = new File(installDir);
		File cfgEngDir = new File (installDir + File.separator + "ConfigEngine");
		
		File versionFolder = new File( installDir + File.separator + "version" );
	    File cfgFolder = new File(versionFolder.toString() + File.separator + "config");
	    File lcProps = new File(cfgFolder.toString() + File.separator + "lc.properties");
	    
		// we always have this "IBM-Connections" in the path for IBM Connections
		// so make sure this is the correct install path
	    // since we don't have any concrete proof that this is the correct path
	    // we check to see if ConfigEngine path exist or not too
	    // we can always add more checks here if needed
		
		if(insDir.exists() && insDir.isDirectory())
		{
//			System.out.println("IBM Connections installation found: " + installDir);
		}
		else
		{
//			System.out.println("IBM Connections installation not found: " + installDir);
			return false; // no install dir or install dir not a directory
		}
			
		// how about the version folder?
	    
	    if ( (versionFolder.exists() && versionFolder.isDirectory()) )
	    {
//	      	System.out.println("IBM Connections version folder found: " + versionFolder.toString());
	      	
	      	// create a lc.properties file
	      	if(!lcProps.exists())
	      	{
				try
				{
					cfgFolder.mkdir();
					lcProps.createNewFile();
				} catch (IOException e)
				{
					System.out.println(e.getMessage());
					return false;
				}
	      	}
	    }
	    else	// create the version folder/ and lc.properties
	    {	
	    	System.out.println(MessageFormat.format(UpdateReporter.getSilentString("error.no.version.folder"), new Object[]{versionFolder.getParent()}));
//	       	if(versionFolder.mkdir())
//	       		System.out.println("IBM Connections version folder created:  " + versionFolder.toString());
//	      	else
//	       	{
//	       		System.out.println("Unable to create the IBM Connections version folder: " + versionFolder.toString());
//	       		return false;
//	       	}
//	       	try
//			{
//	       		cfgFolder.mkdir();
//				lcProps.createNewFile();
//			} catch (IOException e)
//			{
//				System.out.println(e.getMessage());
//				return false;
//			}
	    }
	
	    // if this is a selected update, make sure the features in the list are valid
	    if(args.bSelectFeature)
	    {
	    	StringBuffer sb = new StringBuffer();
	    	
	    	for ( int i = 0; i < args.selectFeatureList.size(); i++)
	    	{
	    		boolean bMatch = false;
		    	String feature = (String)args.selectFeatureList.elementAt(i);
	    		
		    	for (int j = 0; j < LC_IDS.length; j++)
	    		{
	    			if(feature.equalsIgnoreCase(LC_IDS[j]))
	    			{
	    				bMatch = true;
	    				break;
	    			}
	    		}
	    	
	    		if(bMatch == false) 
	    		{
	    			System.out.println("Invalid feature specified in -updateFeature: " + feature);
	    			return false;
	    		}
	    	}
	    	
	    	props.setProperty("updateFeature", args.selectFeatureList.toString());
	    }
	    // for Extractor to succeed. And for CopyFileAction.
	    // <was_home>/bin/setupCmdLine.bat/sh needs to run first to setup some of these values
	    
	    
//	    props.setProperty( WPConstants.PROP_WAS_HOME, System.getProperty("was.home") );
	    props.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, installDir.toString() );
	    //props.setProperty( WPConstants.PROP_WP_HOME, installDir.toString() );
	    //props.setProperty( WPConstants.PROP_TRUE_WP_HOME, installDir.toString() );

	    //System.out.println(installDir.toString());
	    props.setProperty(LCUtil.PROP_LC_HOME, installDir.toString() );
	    //System.out.println(props.getProperty(WPConstants.PROP_LC_HOME));
	    
	    // WAS password
	    if(args.bWasPassword && args.wasPassword!=null)
	    	//props.setProperty(WPConstants.PROP_WAS_PASS, args.wasPassword);
	    	WPConfig.setProperty(WPConstants.PROP_WAS_PASS, args.wasPassword);

	    //	  WAS userid
	    if(args.bWasUserId && args.wasUserId!=null)
	    	//props.setProperty(WPConstants.PROP_WAS_PASS, args.wasPassword);
	    	WPConfig.setProperty(WPConstants.PROP_WAS_USER, args.wasUserId);

	    // find out which WAS profile has which feature installed
	    setupWASProfileName(installDir.toString());

	    //System.out.println("WAS Profile that has LC feature installed: ");
		  //System.out.println(props.getProperty(WPConstants.PROP_ACTIVITIES_PROFILE_NAME));
		  //System.out.println(props.getProperty(WPConstants.PROP_BLOGS_PROFILE_NAME));
		  //System.out.println(props.getProperty(WPConstants.PROP_COMMUNITIES_PROFILE_NAME));
		  //System.out.println(props.getProperty(WPConstants.PROP_DOGEAR_PROFILE_NAME));
		  //System.out.println(props.getProperty(WPConstants.PROP_PROFILES_PROFILE_NAME));
	    try
		{
			FileOutputStream fos = new FileOutputStream(lcProps);
			props.store(fos, "IBM Connections Properties \n This file is created by the installer, do not edit");
			fos.flush();
			fos.close();
			
		} catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		
		
	    bLCProduct = true;
		return true;

	}
	
	// setup <product>_profile_name macro for all the installed features
    private void setupWASProfileName(String root)
	{
    	String prodPath = "";
    	String profileName = "";
    	
	/*	if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_ACTIVITIES)))
		{	
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_ACTIVITIES_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_BLOGS)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_BLOGS_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_COMMUNITIES)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_COMMUNITIES_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_DOGEAR)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_DOGEAR_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_PROFILES)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_PROFILES_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_HOMEPAGE)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_HOMEPAGE_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_WIKIS)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_WIKIS_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_FILES)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_FILES_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_SEARCH)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_SEARCH_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_MOBILE)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_MOBILE_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_NEWS)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_NEWS_PROFILE_NAME, profileName);
		}
		if( null != (prodPath = LCUtil.productExist(root, LCUtil.PRODUCTID_LC_FORUM)))
		{
			if(null != (profileName = LCUtil.getProfileName(prodPath)))
				props.setProperty(LCUtil.PROP_FORUM_PROFILE_NAME, profileName);
		}*/
    	profileName = LCUtil.getProfileName2(root + File.separator + LCUtil.PRODUCTID_LC_CONFIGENGINE );

		props.setProperty(LCUtil.PROP_ACTIVITIES_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_BLOGS_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_COMMUNITIES_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_DOGEAR_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_PROFILES_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_HOMEPAGE_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_WIKIS_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_FILES_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_SEARCH_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_MOBILE_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_NEWS_PROFILE_NAME, profileName);
		props.setProperty(LCUtil.PROP_FORUM_PROFILE_NAME, profileName);
 
		
	}

		public void printDirInvalid(String dir, String arg) {

            // using System.out since there is no more PUI nls conversion being done (eg. System.err.println(UpdateReporter.getSilentString ...
            System.out.println( "Supplied " + dir + " \"" + arg + "\" is not a valid directory." );
            System.out.println( "    Possible problems include: " );
            System.out.println( "        - directory cannot be found as specified (check spelling and file permissions)," );
            if (dir.toString().indexOf("installDir") > -1) {
                System.out.println( "        - directory does not contain a valid installation of WebSphere Portal," );
            }
            System.out.println( "        - directory was specified with a trailing \"\\\" character (Windows)," );
            System.out.println( "        - directory was quoted with extra leading or trailing whitespace characters" );

            return;

        }


        public String getMPVersionNoDots(String version) {

                      StringTokenizer st = new StringTokenizer(version, ".");
                      StringBuffer num = new StringBuffer();
                      String intID; 

                      while (st.hasMoreTokens()) {
                                      intID = st.nextToken();
                                      num.append(intID);
                      }

                      String theString = num.toString();

                       
                      while (theString.endsWith("0") ) {
                          theString = theString.substring(0,theString.length() - 1);
                      }

                      

                      return theString;


        }

        public boolean getIsPTFUninstallable(String fpID, UpdateInstallerArgs args) {


                     String ptfNumber = "";
                     String stringPart; 


                     if (fpID.indexOf("WP_PTF_") > -1) {
                     }
                     else
                     {
                         return true;
                     }

                     StringTokenizer st = new StringTokenizer(fpID, "_");
                     
                     while (st.hasMoreTokens()) {
                                     stringPart = st.nextToken();
                                     if (stringPart.equals("WP")) {
                                     } else if (stringPart.equals("PTF")) {
                                     } else {
                                         ptfNumber = stringPart;
                                     }
                     }

                    
                     
                     String mp_version =  getVersionInMP_Product(args);

                     if (mp_version.equals("AllowUninstall") ) {
                         return true;
                     }

                     String mp_version_no_dots = getMPVersionNoDots(mp_version);

                   
                     
                     System.out.println("ptfNumber is ::: " +  ptfNumber );
                     System.out.println("mp_version_no_dots is ::: " +  mp_version_no_dots);

                     if (ptfNumber.equals(mp_version_no_dots)) {
                         return true;
                     }

                     return false;


        }


        public String getVersionInMP_Product(UpdateInstallerArgs args) {


                     WPProduct testWpsProduct;

                     testWpsProduct = new WPProduct(args.installDir);

                     String MP_version = ""; 

                     Iterator piter = testWpsProduct.getProducts();
                     while ( piter.hasNext() ) {
                         product pthisProd = (product)piter.next();
                         String pprodName  = pthisProd.getName();
                         String pprodID    = pthisProd.getId();
                         String pver       = pthisProd.getVersion();

                         
                         if (pprodID.equals("MP")) {
                             MP_version = pver;
                         }

                         
                                              }

                      System.out.println("returned MP_version " + MP_version );

                      if (MP_version == null || MP_version.length() < 1) {
                          MP_version = "AllowUninstall";
                      }

                       

                    
                      return MP_version;


        }

   protected boolean  checkPasswordsForWPFixpack()
   {




        String wpsLocation = WPConfig.getProperty( "WpsInstallLocation" );

        String MPfileName = wpsLocation + File.separator + "version" + File.separator + "MP.product";

        File checkMPProduct = new File( MPfileName );
           if ( !checkMPProduct.exists() ) {
              System.out.println(" No MP.product. Probably PZN Standalone " );
              return true;
           }


        
        boolean WPPasswordsEntered = true;

       String PortalPassword = WPConfig.getProperty( "PortalAdminPwd" );

      
       // if ( (PortalPassword != null) && (PortalPassword.length() > 0)  )
       if ( (PortalPassword != null) && (PortalPassword.length() > 0) && !(PortalPassword.equals("ReplaceWithYourPwd")) )
       {
       }
       else
       {
           WPPasswordsEntered = false;

           System.out.println("PortalAdminPwd has not been entered " );
       }

       String WasPassword = WPConfig.getProperty( "WasPassword" );

      

       if ( (WasPassword != null) && (WasPassword.length() > 0) && !(WasPassword.equals("ReplaceWithYourWASUserPwd")) )
       {
       }
       else
       {

           WPPasswordsEntered = false;

           System.out.println("WasPassword has not been entered " );

        
       }

       return  WPPasswordsEntered;

     
   }

   protected boolean  checkDBPasswordsForWPFixpack()
  {


       String wpsLocation = WPConfig.getProperty( "WpsInstallLocation" );

       String MPfileName = wpsLocation + File.separator + "version" + File.separator + "MP.product";

       File checkMPProduct = new File( MPfileName );
       if ( !checkMPProduct.exists() ) {
             System.out.println(" No MP.product. Probably PZN Standalone " );
             return true;
       }



       boolean dbPasswordsEntered = true;


       String UpdReleasePassword = null;
       String UpdCustomizationPassword = null;
       String UpdCommunityPassword = null;
       String UpdJcrPassword = null;
       String UpdWmmPassword = null;
       String UpdFeedbackPassword = null;
       String UpdLikemindsPassword = null;
       String UpdDesignerPassword = null;
       String UpdSyncPassword = null;
        
      
       String ReleaseDbType = null;
       String CustomizationDbType = null;
       String CommunityDbType = null;
       String JcrDbType = null;
       String WmmDbType = null;
       String FeedbackDbType = null;
       String LikemindsDbType = null;
       String DesignerDbType = null;
       String SyncDbType = null;

       boolean ReleaseCloudscape = false;
       boolean CustomizationCloudscape = false;
       boolean CommunityCloudscape = false;
       boolean JcrCloudscape = false;
       boolean WmmCloudscape = false;
       boolean FeedbackCloudscape = false;
       boolean LikemindsCloudscape = false;
       boolean DesignerCloudscape = false;
       boolean SyncCloudscape = false;





       String fileName = wpsLocation + File.separator + "config" + File.separator + "wpconfig_dbdomain.properties";

       CommentedProperties props = readPropertiesFile(fileName);

       // Get Database Types

       ReleaseDbType = props.getProperty( "release.DbType" );
       CustomizationDbType = props.getProperty( "customization.DbType" );
       CommunityDbType = props.getProperty( "community.DbType" );
       JcrDbType = props.getProperty( "jcr.DbType" );
       WmmDbType = props.getProperty( "wmm.DbType" );
       FeedbackDbType = props.getProperty( "feedback.DbType" );
       LikemindsDbType = props.getProperty( "likeminds.DbType" );
       DesignerDbType = props.getProperty( "designer.DbType" );
       SyncDbType = props.getProperty( "sync.DbType" );

       if ( (ReleaseDbType != null) && (ReleaseDbType.length() > 0)  && (ReleaseDbType.equals("cloudscape")) )
       {
           ReleaseCloudscape = true;
       }

       if ( (CustomizationDbType != null) && (CustomizationDbType.length() > 0)  && (CustomizationDbType.equals("cloudscape")) )
       {
          CustomizationCloudscape = true;
       }

       if ( (CommunityDbType != null) && (CommunityDbType.length() > 0)  && (CommunityDbType.equals("cloudscape")) )
       {
           CommunityCloudscape = true;
       }


       if ( (JcrDbType != null) && (JcrDbType.length() > 0)  && (JcrDbType.equals("cloudscape")) )
       {
          JcrCloudscape = true;
       }

       if ( (WmmDbType != null) && (WmmDbType.length() > 0)  && (WmmDbType.equals("cloudscape")) )
       {
          WmmCloudscape = true;
       }

       if ( (FeedbackDbType != null) && (FeedbackDbType.length() > 0)  && (FeedbackDbType.equals("cloudscape")) )
       {
         FeedbackCloudscape = true;
       }

       if ( (LikemindsDbType != null) && (LikemindsDbType.length() > 0)  && (LikemindsDbType.equals("cloudscape")) )
       {
         LikemindsCloudscape = true;
       }

       if ( (DesignerDbType != null) && (DesignerDbType.length() > 0)  && (DesignerDbType.equals("cloudscape")) )
       {
         DesignerCloudscape = true;
       }

       if ( ( SyncDbType != null) && ( SyncDbType.length() > 0)  && ( SyncDbType.equals("cloudscape")) )
       {
          SyncCloudscape = true;
       }




       // one

       if (ReleaseCloudscape == false) {
       
         
          String ReleasePassword = props.getProperty( "release.DbPassword" );

         
      
          if ( (ReleasePassword != null) && (ReleasePassword.length() > 0)  && !(ReleasePassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {

              dbPasswordsEntered = false;

              System.out.println("release.DbPassword has not been entered " );
              
          }

       }

        // two
       if (CustomizationCloudscape == false) {

         
          String CustomizationPassword = props.getProperty( "customization.DbPassword" );

                
          if ( (CustomizationPassword != null) && (CustomizationPassword.length() > 0)  && !(CustomizationPassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {
              dbPasswordsEntered = false;

              System.out.println("customization.DbPassword has not been entered " );

           
          }

       }

         // three
        
       if (CommunityCloudscape == false) {

         
          String CommunityPassword = props.getProperty( "community.DbPassword" );

           
      
          if ( (CommunityPassword != null) && (CommunityPassword.length() > 0)  && !(CommunityPassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {

              dbPasswordsEntered = false;

              System.out.println("community.DbPassword has not been entered " );


           }

       }

          // four
       if (JcrCloudscape == false) {

         
          String JcrPassword = props.getProperty( "jcr.DbPassword" );
         

      
          if ( (JcrPassword != null) && (JcrPassword.length() > 0)  && !(JcrPassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {

              dbPasswordsEntered = false;

              System.out.println("jcr.DbPassword has not been entered " );

           

          }

       }

            // five
       if (WmmCloudscape == false) {

         
          String WmmPassword = props.getProperty( "wmm.DbPassword" );

         
      
          if ( (WmmPassword != null) && (WmmPassword.length() > 0)  && !(WmmPassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {

              dbPasswordsEntered = false;

              System.out.println("wmm.DbPassword has not been entered " );

            
          }

       }

             // six
       if (FeedbackCloudscape == false) {

         
          String FeedbackPassword = props.getProperty( "feedback.DbPassword" );
        
      
          if ( (FeedbackPassword != null) && (FeedbackPassword.length() > 0)  && !(FeedbackPassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {


              dbPasswordsEntered = false;

              System.out.println("feedback.DbPassword has not been entered " );

          

          }

       }

             // seven
       if (LikemindsCloudscape == false) {

         
          String LikemindsPassword = props.getProperty( "likeminds.DbPassword" );
           

      
          if (  (LikemindsPassword != null) && ( LikemindsPassword.length() > 0)  && !( LikemindsPassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {


              dbPasswordsEntered = false;

              System.out.println("likeminds.DbPassword has not been entered " );

            

          }

       }

               // eight
         
      if (DesignerCloudscape == false) {

          String DesignerPassword = props.getProperty( "designer.DbPassword" );

         
      
          if (  (DesignerPassword != null) && ( DesignerPassword.length() > 0)  && !( DesignerPassword.startsWith("ReplaceWithYour")) )
          {
          }
          else
          {
           

              dbPasswordsEntered = false;
              System.out.println("designer.DbPassword has not been entered " );

             

          }

      }

               // nine

      if (SyncCloudscape == false) {

         
          String SyncPassword = props.getProperty( "sync.DbPassword" );
       

          if (  (SyncPassword != null) && ( SyncPassword.length() > 0)  && !( SyncPassword.startsWith("ReplaceWithYour")) )
          {
          }
         else
         {


             dbPasswordsEntered = false;
             System.out.println("sync.DbPassword has not been entered " );

            

         }

     }



      return dbPasswordsEntered;

    
  }

  

 protected CommentedProperties readPropertiesFile(String fileName)
 {
     File file = new File(fileName);

     InputStream inputFile;

     try {
         inputFile = new FileInputStream(file);

     } catch ( FileNotFoundException ex ) {
         inputFile = null;   // this is a new File
     }

     CommentedProperties props = new CommentedProperties();

     if ( inputFile != null ) {
         try {
             props.load(inputFile);

         } catch ( IOException ex ) {
             // logError(95, "Failed to load properties from [ " + fileName + " ]", ex);
             return null;

         } finally {
             try {
                 System.out.println("Closing property file");
                 inputFile.close();

             } catch (IOException ex) {
                 // logError(96, "Failed to close properties file [ " + fileName + " ]", ex);
                 return null;
             }
         }
     }

     return props;
 }






     


}