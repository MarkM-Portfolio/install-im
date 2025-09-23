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

package com.ibm.websphere.update.delta.earutils;

/*
 *  @ (#) WPEarActor.java
 *
 *  Perform WpsEar EAR pre and post processing operations.
 *
 *  This class modifes the superclass defined operations to
 *  provide a wps.ear specific implementation.
 *
 *  The processing for wpsear is:
 *
 *  In order of processing steps:
 *
 *      * When a deployment is present, skip expanding the
 *        installable copy of the webui EAR.
 *
 *      * When no deployment is present, expand the installable
 *        application to a temporary location.
 *
 *      * The core update function updates the expanded installable
 *        wps.eari application (if present), and any deployed wps.ear
 *        applications (in the installed location).
 *
 *      * When a deployment is present, the post processing steps
 *        are to perform plugin-processing on the updated deployed
 *        application, then compress that application into the
 *        configuration based applications directory, then copy that
 *        compressed application into the 'installable' directory.
 *
 *      * When no deployment is present, the post processing steps
 *        are to run plugin-processing on the updated installable
 *        application, then to compress that application from the
 *        temporary working location into the installable location.
 * 
 *  Split 'not deployed' and 'deployed':
 *
 *  Deployed:
 *
 *      Pre: None
 *      Core: Update 'installed'.
 *      Post: Update 'installed';
 *            Plugin process 'installed';
 *            Compress 'installed' to 'applications';
 *            Copy 'applications' to 'installable'.
 *
 *  Not Deployed:
 *
 *      Pre: Expand 'installable' to a temporary directory.
 *      Core: Update the temporary 'installable'.
 *      Post: Plugin process the temporary 'installable';
 *            Compress the temporary 'installable to 'installable'.
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

import java.io.*;
import java.util.*;

//import com.ibm.websphere.update.delta.util.WsAdminCmd;
import com.ibm.websphere.update.delta.adminconfig.WPWsAdminErrorCodes;
import com.ibm.websphere.update.delta.util.WsAdminClient;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import com.ibm.websphere.update.util.was.WASNodeType;

public abstract  class WPEarActor extends ExtendedEARActor {
   public final static String pgmVersion = "1.16" ;
   public final static String pgmUpdate = "2/27/04" ;


   public static final String KEY_WPEAR_VALID_PREFIX = "com.ibm.websphere.update.delta.earutils.WPEarActor.validEar.";
   public static final String KEY_WPEAR_INVALID_PREFIX = "com.ibm.websphere.update.delta.earutils.WPEarActor.invalidEar.";

   public static final String WPEAR_VALID_STATE_OK           = "OK";
   public static final String WPEAR_VALID_STATE_MISSING      = "MISSING";
   public static final String WPEAR_VALID_STATE_NOT_DEPLOYED = "NOT DEPLOYED";
   public static final String WPEAR_VALID_STATE_ERROR        = "ERROR";


   protected static final int INSTALLED_EAR = APPLICATION_EAR + 1;

   public static final String INSTALLED_EAR_TAIL = "installedApps";

   public static final String TMP_INSTALLED_EAR_TAIL = "installed";

   public static File getEarWorkArea( String tmpDir ) {
      return getEarWorkArea( tmpDir, null );
   }

   public static File getEarWorkArea( String tmpDir, String nodeName ) {
      return getEarWorkArea( tmpDir, nodeName, INSTALLED_EAR_TAIL );
   }

   protected static File getEarWorkArea( String tmpDir, String nodeName, String loc ) {
      File workDir = new File( tmpDir + File.separator + "puiearwork" + File.separator + loc );
      if (nodeName != null ) {
         workDir = new File( workDir, nodeName );
      }
      workDir.mkdirs();
      return workDir;
   }


   protected File   earTempWorkDir;

   public WPEarActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName,
                         boolean doDeploy, boolean doPluginDeploy) {
      this(installPath,
           messages, errors,
           earName,
           doDeploy, doPluginDeploy,
           false, false);
   }

   public WPEarActor(String installPath, StringBuffer messages, StringBuffer errors,
                         String earName,
                         boolean doDeploy, boolean doPluginDeploy,
                         boolean installableOnly, boolean applicationOnly)
   {
      super(installPath, messages, errors,
            earName,
            doDeploy, false,  // Portal wps.ear would never do a pluginDeploiy
            installableOnly, applicationOnly);
   }

   protected abstract String getEarName();
   protected abstract String getEarAppName();
   protected abstract String getEarInstalledLocation();

   // Create a product interrogator for the receiver.

   protected ProductInterrogator createProductInterrogator() {
       return new ProductInterrogator( WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ) );
   }



   // Processing enablement helpers ...
   //
   // protected boolean isEnabled()
   //
   // protected ProductInterrogator productInterrogator;
   // protected ProductInterrogator getProductInterrogator();
   // protected ProductInterrogator createProductInterrogator();

   // protected boolean isEnabled()
   // protected ProductInterrogator getProductInterrogator();

   // Tell if EAR processing is enabled.
   //
   // EAR processing is disabled when the product is
   // federated, that is, when both a BASE and an ND
   // product are present.  Unless WasDM is true.

   protected boolean isEnabled() {
       ProductInterrogator useProductInterrogator =
           getProductInterrogator();

       if ( useProductInterrogator.isEmbedded() )
           return false;

       boolean enabled = processingDMDeployments();
       if (!enabled) {
          // Not doing DM processing, make sure we are not on a ND node.
          enabled = useProductInterrogator.isBase() && !useProductInterrogator.isND();
          // <d63896>
          if (!enabled) {
             // Ok need some further checking here.  When a node is unfederated, the ND.product file doesn't get
             //   removed, so not look at the {WAS}/config/cells/<cellname>/cell.xml file and see if celltype 
             //   is STANDALONE or DISTRIBUTED   

             // <d63896>
             enabled = WASNodeType.isStandaloneCell();
          }
          // </d63896>
       }

       return enabled;
   }

   protected void generateEarWorkDir( ) {
      // Each invocation may be for a different Node, so always regenrate
      earTempWorkDir = getEarWorkArea( getEARTmpDir(), getNodeName() );
   }

   protected String getCompressedDMPath( String earName) {
      //File compDir = new File( earTempWorkDir, INSTALLABLE_EAR_TAIL );
      File compDir = getEarWorkArea( getEARTmpDir(), getNodeName(), TMP_INSTALLED_EAR_TAIL );

      compDir.mkdir();
      return new File( compDir, earName ).getAbsolutePath();
   }

   protected String getUncompressedDMPath(String earName) {
      //File compDir = new File( earTempWorkDir, TMP_INSTALLABLE_EAR_TAIL );
      File compDir = getEarWorkArea( getEARTmpDir(), getNodeName(), INSTALLED_EAR_TAIL );
      compDir.mkdir();
      return new File( compDir, earName ).getAbsolutePath();
   }

   protected boolean uncompressDMEar( String compressedEarName, String earName ) {
      String compressedPath   = getCompressedDMPath(compressedEarName);
      String uncompressedPath = getUncompressedDMPath(earName);
      if ( performUncompressCommand(compressedPath, uncompressedPath) ) {
          log("Uncompressing EAR: " + earName + ": OK");
          return true;

      } else {
          logError("Uncompressing EAR: " + earName + ": Failed");
          logError("Compressed Path  : " + compressedPath);
          logError("Uncompressed Path: " + uncompressedPath);

          return false;
      }
   }

   protected boolean compressDMEar( String earName ) {

      String compressedPath   = getCompressedDMPath(earName);
      String uncompressedPath = getUncompressedDMPath(earName);


      if ( performCompressCommand(compressedPath, uncompressedPath) ) {
          log("Coompressing EAR: " + earName + ": OK");
          return true;

      } else {
          logError("Compressing EAR: " + earName + ": Failed");
          logError("Compressed Path  : " + compressedPath);
          logError("Compressed Path: " + uncompressedPath);

          return false;
      }
   }


   // Deployment helper:
   //
   // boolean isDeployed(String);

   protected boolean isDeployed(String earName)
   {
      String binariesLocation = getCorrectedBinariesLocation(earName);

      return( (binariesLocation != null) &&
              earIsPresent(binariesLocation) );
   }

   // Pre-processing overrides:
   //
   // boolean preProcessWithRule(String, boolean, boolean);
   // boolean preProcess(String, boolean, boolean);

   protected boolean preProcessWithRule(String earName,
                                        boolean installableOnly,
                                        boolean applicationOnly)
   {
      log("Strange: Noted rule on EAR " + earName + " for WPEar pre-processing.");
      return true;
   }

   protected boolean preProcess(String earName,
                                boolean installableOnly,
                                boolean applicationOnly)
   {

      boolean result = false;
      // If DM active, we should extract the wps.
      generateEarWorkDir();  // EarRedesign - moved here since this is common now
      String exportEarName = "export_" + earName;


      boolean extractedEar = false;
      if ( processingDMDeployments() ) {
         StringBuffer localErrors = new StringBuffer();
         int errorCode = extractWPEar( exportEarName, localErrors );
         if (errorCode == WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS ) {
            log("Exported " + earName + " from wsadmin : OK");
            extractedEar = result = true;
         } else if ( errorCode == WPWsAdminErrorCodes.WPWSADMIN_ERROR_APPMISSING ) {
            // We handled errors, so turn any error into a message.
            log("Exported " + earName + " from wsadmin skipped application not deployed : OK");
            log( localErrors.toString() );
            result = true;
         } else {
            // We didn't handled errors, so make sure they get recorded by Extractor
            log("Exported " + earName + " from wsadmin : Failed");
            logError( localErrors.toString() );
            result = false;
         }
      } else {
         if ( isDeployed(earName) ) {
            log("EAR " + earName + " is deployed.  Will apply changes and redeploy.");

            //EarRedesign
            //   Build wps.ear from installedApps (Run EARExpander on installedApps)
            String compressedPath   = getCompressedDMPath( exportEarName );

            String uncompressedPath = getInstancePath()    + File.separator +
                                      "installedApps"      + File.separator +
                                      getCellName()        + File.separator +
                                      earName;

            if ( performCompressCommand(compressedPath, uncompressedPath) ) {
                log("Compressing EAR: " + earName + " from " + uncompressedPath + ": OK");
                result = extractedEar = true;
            } else {
                logError("Compressing EAR: " + earName + ": Failed");
                logError("Compressed Path  : " + compressedPath);
                logError("Compressed Path: " + uncompressedPath);
                result = extractedEar = false;
            }
         } else {
            // We handled errors, so turn any error into a message.
            //log("EAR " + earName + " is not deployed. Will rebuild ear w/ changes.");
            result = true;
         }
      }
      System.out.println( this.getClass().getName() + "::preProcess : " + "EarName: " + getEarName() + "   ----    " + "EarAppName: " + getEarAppName() +  "   ----    " + "Script EarName:" + earName );
      boolean earExists = result;
      if (result){
          if (extractedEar) {
              System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName + "." + getEarAppName() , WPEAR_VALID_STATE_OK );  // Indicates EAR is OK
              System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName , WPEAR_VALID_STATE_OK );  // Indicates EAR not found
              result = uncompressDMEar( exportEarName, earName );
          } else {
              // If Not deplpoyed, we attempt copy/extract to update original EAR ( asInstallable ).
             log("EAR " + earName + " is not deployed. Will rebuild ear w/ changes.");
             earExists = performCopyCommand( getEarInstalledLocation() + File.separator + earName,
                                             getCompressedDMPath( exportEarName ) );
             if (earExists) {
                 result = uncompressDMEar( exportEarName, earName );
                 System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName + "." + getEarAppName() , result ? WPEAR_VALID_STATE_OK : WPEAR_VALID_STATE_ERROR  );  // Indicates EAR is OK
                 System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName , result ? WPEAR_VALID_STATE_NOT_DEPLOYED : WPEAR_VALID_STATE_ERROR );  // Indicates EAR not found
             } else {
                 // At this point we are OK, so clear any messgaes from copy.
                 getErrors().setLength(0);
                 getMessages().setLength(0);
                 System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName + "." + getEarAppName() , WPEAR_VALID_STATE_MISSING  );  // Indicates EAR is OK
                 System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName , WPEAR_VALID_STATE_MISSING );  // Indicates EAR not found
             }

          }
      } 
      if ( !result) {
          System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName + "." + getEarAppName() , WPEAR_VALID_STATE_ERROR );
          System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName , WPEAR_VALID_STATE_ERROR );
          log("Preprocessing WPEar EAR: " + earName + ": Failed");

      } else {
          log("Preprocessing WPEar EAR: " + earName + ": OK" );
      }
      return result;

      /*
      // Only uncompress if we are sucessful so far
      if ( earExists && result && uncompressDMEar( exportEarName, earName) ) {
         System.setProperty( KEY_WPEAR_INVALID_PREFIX + getEarName() + "." + getEarAppName(), WPEAR_VALID_STATE_OK );
         System.getProperties().remove( KEY_WPEAR_INVALID_PREFIX + earName );
         log("Preprocessing WPEar EAR: " + earName + ": OK");
         return  true;
      } else if ( result && !earExists ) {
         System.setProperty( KEY_WPEAR_INVALID_PREFIX + getEarName() + "." + getEarAppName(), WPEAR_VALID_STATE_MISSING ); // Ear not deployed
         System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName , WPEAR_VALID_STATE_MISSING );  // Indicates EAR not found
         logError("Preprocessing WpEar EAR: " + earName + ": Not Deployed and cannot rebuild ear, skipping all processing." );
         return true;
      } else {
         System.setProperty( KEY_WPEAR_INVALID_PREFIX + getEarName() + "." + getEarAppName(), WPEAR_VALID_STATE_MISSING );
         System.setProperty( KEY_WPEAR_INVALID_PREFIX + earName , "true" );
         logError("Preprocessing WpEar EAR: " + earName + ": Failed");
         return false;
      }
      */

   }

   // Post-processing overrides:
   //
   // boolean basicPostProcess(boolean);

   protected boolean basicPostProcess(boolean doDeploy)
   {
      Iterator instances = getInstances();
      if ( instances == null ) {
         log("No instances; failing post-processing.");
         return false;
      }

      Vector earCommands = getCommandData();
      int numEarCommands = earCommands.size();

      boolean stillOK = true;
      boolean result = false;

      for ( int commandNo = 0; stillOK && (commandNo < numEarCommands); commandNo++ ) {
         EARCommandData nextCommand =
         (EARCommandData) earCommands.elementAt(commandNo); 

         while ( stillOK && instances.hasNext() ) {
            InstanceData nextInstance = (InstanceData) instances.next();
            setInstance(nextInstance); 

            String nextEarName = nextCommand.earName;

            generateEarWorkDir();

            System.out.println( this.getClass().getName() + "::BasicPostProcess : " + "EarName: " + getEarName() + "   ----    " + "EarAppName: " + getEarAppName() +  "   ----    " + "Script EarName:" + nextEarName );
            String earValid  = System.getProperty( KEY_WPEAR_INVALID_PREFIX + nextEarName + "." + getEarAppName() );
            String earExists = System.getProperty( KEY_WPEAR_INVALID_PREFIX + nextEarName );

            if (earValid  == null) earValid = WPEAR_VALID_STATE_OK;
            if (earExists == null) earExists = WPEAR_VALID_STATE_OK;
            if (earValid.equals( WPEAR_VALID_STATE_OK ) ) {
               // Pre-EAR processing was OK, so we can continue here.
               stillOK =
                 compressDMEar(nextEarName) &&
                 copyToInstallable(nextEarName);

               // Redeploy - if was deployed.
               if ( stillOK ) {
                   if ( earExists.equals( WPEAR_VALID_STATE_OK ) ) {
                       int errorCode = deployWPEar( nextEarName );
                       stillOK = (errorCode == WPWsAdminErrorCodes.WPWSADMIN_ERROR_SUCCESS ) ||
                                 (errorCode == WPWsAdminErrorCodes.WPWSADMIN_ERROR_APPMISSING );
                   //} else if ( earExists.equals( WPEAR_VALID_STATE_NOT_DEPLOYED ) ) {
                   }
                   log("EAR " + nextEarName + " is not deployed.  Copy serviced EAR " + nextEarName + " back to install location.");
                   performCopyCommand( getCompressedDMPath( nextEarName ),
                                       getEarInstalledLocation() + File.separator + nextEarName
                                     );
               }
            } else if ( earValid.equals(WPEAR_VALID_STATE_MISSING) ) {
               // Ear extarct failed due to app not deployed, so skip processing now, but still OK.
               log("Skipping WPEar post processing : application " + nextEarName + " is not deployed." );
               stillOK = true;
               /*     
               // It wasn't deploy, See if the EAr was found and expanded, compress and copy back
               if ( earExists.equals( WPEAR_VALID_STATE_MISSING ) ) {
                    stillOK = compressDMEar(nextEarName) &&
                              performCopyCommand(  getCompressedDMPath( nextEarName ), 
                                                   getEarInstalledLocation() + File.separator + nextEarName );
               }

               if ( !stillOK )
                  log("Failed on WPEar post processing for installable " + nextEarName);
               */   

            } else {
               log("Failed WPEar post processing : application " + nextEarName + " failed extraction in Pre processing." );
               // Ear extarct failed ealier so fail now.
               stillOK = false;
            }
            if ( !stillOK )
               log("Failed on WPEar post processing for deployed " + nextEarName);


         }
      }

      if ( stillOK )
         log("Post-processing succeeded.");

      return stillOK;
   }

   protected boolean compressToApplications(String earName)
   {
      String uncompressedPath = getCorrectedBinariesLocation(earName);
      String compressedPath = getCompressedPath(earName, EARActor.APPLICATION_EAR);

      log("Removing original compressed EAR");

      if ( !removeFully(compressedPath) ) {
         logError("Unable to remove original compressed EAR: " + compressedPath);
         return false;

      } else {
         log("Removing original compressed EAR: OK");
      }

      return performCompressCommand(compressedPath, uncompressedPath);
   }

   protected boolean copyToInstallable(String earName) {
      String sourcePath = getCompressedDMPath( earName );
      String targetPath = getCompressedPath(earName, EARActor.INSTALLABLE_EAR);

      return performCopyCommand(sourcePath, targetPath);
   }

   protected boolean copyDelete(String sourcePath, String targetPath)
   {
      if ( !performCopyCommand(sourcePath, targetPath) )
         return false;

      return performDeleteCommand(sourcePath);
   }

   protected String asExtractedPath(String path) {
      // Make sure all backslashes are converted to forward slashes
       return ( ( path == null ) ? null : path.replace( '\\', '/') );
   }

   protected int deployWPEar( String earName ) {
      // This deplpoys through wsadmin, so it will work w/ DM.
      String targetPath = asExtractedPath(getCompressedPath(earName, EARActor.INSTALLABLE_EAR));
      log("Deploy " + earName + " from " + targetPath);
      WsAdminClient cmd = new WsAdminClient( getMessages(), getErrors() );
      cmd.setAction( cmd.ACTION_REDEPLOY );
      cmd.setEarFile( targetPath );
      cmd.setAppName( getEarAppName() );
      return cmd.execute();
   }


   protected int extractWPEar( String earName, StringBuffer localErrors ) {
      String targetEar = asExtractedPath(getCompressedDMPath( earName ) );
      log("Extracting " + earName + " to " + targetEar);
      WsAdminClient cmd = new WsAdminClient( getMessages(), localErrors );
      cmd.setAction( cmd.ACTION_EXTRACT );
      cmd.setEarFile( targetEar );
      cmd.setAppName( getEarAppName() );
      return cmd.execute();
   }

   protected boolean processingDMDeployments() {
      return (new Boolean( WPConfig.getProperty( WPConstants.PROP_WAS_DM ) ) ).booleanValue();
   }


}
