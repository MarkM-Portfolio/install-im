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

import java.io.*;
import java.util.*;
import com.ibm.websphere.update.util.was.*;
import com.ibm.websphere.update.silent.UpdateInstaller;


public class WPConfig {

   public static final Properties WP_PROPS = new Properties();

   private static boolean initialized = false;

   public static final String debugPropertyName = "com.ibm.websphere.update.util.debug" ;
   public static final String debugTrueValue = "true" ;
   public static final String debugFalseValue = "false" ;

   // public static String pwdsToUpdate1 = "PortalAdminPwd, WasPassword";
   // public static String pwdsToUpdate2 = "release.DbPassword, customization.DbPassword, community.DbPassword," ;
   // public static String pwdsToUpdate3 = "jcr.DbPassword, wmm.DbPassword, feedback.DbPassword,";
   // public static String pwdsToUpdate4 = "feedback.DbPassword, likeminds.DbPassword, designer.DbPassword, sync.DbPassword ";

   public static String pwdsToUpdate1 = null;
   public static String pwdsToUpdate2 = null;
   public static String pwdsToUpdate3 = null;
   public static String pwdsToUpdate4 = null;



   protected static boolean debug = false;

   static {
       String debugValue = System.getProperty(debugPropertyName);
       debug = ( (debugValue != null) && debugValue.equalsIgnoreCase(debugTrueValue) );
   }

   protected static void debug(String arg)
   {
       if ( !debug )
           return;

       System.out.println(arg);
   }

   /**
    * Reset will clear the current properties and force a reinitialization of WPConfig on the next initialize call.
    * Care should be taken when calling this.
    * 
    **/
   public static void reset() {
      WP_PROPS.clear();
      initialized = false;
      WPCheckFiles.reset();
   }

   public static boolean initialize( String wpsHome ) {

      boolean lcOK = true;
      
      if (!initialized) {
    //     wpcpOK = loadConfiguration( wpsHome + File.separator + "wpcp" + File.separator + "v5.0" + File.separator +  "config" + File.separator + "wpcpconfig.properties" );
    //     wpOK   = loadConfiguration( wpsHome + File.separator + "config" + File.separator + "wpconfig.properties" );
    //     wbcrOK = loadConfiguration( wpsHome + File.separator + "version" + File.separator + "wbcrconfig.properties", "WpsInstallLocation", File.separator + "config" + File.separator + "wpconfig.properties" );
         // wcsOK  = loadConfiguration( wpsHome + File.separator + "bin" + File.separator + "LWPSet.bat", "WPS_HOME", File.separator + "config" + File.separator + "wpconfig.properties" );
   //      wcsOK  = loadConfiguration( wpsHome + File.separator + "WorkplaceServer" + File.separator + "bin" + File.separator + "LWPset.bat", "WPS_HOME", File.separator + "config" + File.separator + "wpconfig.properties" );
   //      wbseOK = loadConfiguration( wpsHome + File.separator + "version" + File.separator + "WBSEset.properties", "WPS_HOME", "config" + File.separator + "wpconfig.properties" );
         
         lcOK = loadConfiguration( wpsHome + File.separator + "version" + File.separator + "config" + File.separator + "lc.properties");
         

        initialized = lcOK;
      }
      //if (!initialized) {
      if (!lcOK)
      {
          // eg. invalid installDir
          UpdateInstaller.puiReturnCode = 9;
          //System.out.println("Failed to initialize WPConfig properties.");
          //System.out.println(wpsHome + File.separator + "version" + File.separator + "lc.properties");
      }
      /*
      else
      {
    	  System.out.println("in else");
         String true_wp_home =  WP_PROPS.getProperty( WPConstants.PROP_WP_HOME );
         //WP_PROPS.put( WPConstants.PROP_TRUE_WP_HOME, true_wp_home );
         //WP_PROPS.put( WPConstants.PROP_WP_HOME, wpsHome );   //  Store wps home in the props file.
         //setWas6ProfileInfo();
      }*/
      return initialized;
   }

   //use this one from Ant tasks, pass in project properties
   public static boolean initialize( Properties p ) {
       if (!initialized) {
           WP_PROPS.putAll( p );
           String wpsHome = WP_PROPS.getProperty( WPConstants.PROP_WP_HOME );
           if ( null != wpsHome && "" != wpsHome) {
               initialized = true;
           }
    
           //look again for the debug property, this time in WP_PROPS . . .
           String debugValue = WP_PROPS.getProperty(debugPropertyName);
           debug = ( (debugValue != null) && debugValue.equalsIgnoreCase(debugTrueValue) );
    
           if (debug) {
               System.out.println( " Listing of WPConfig::WP_PROPS" );
               dumpProperties( WP_PROPS, System.out );
           }
       }
       return initialized;
   }

   public static boolean loadConfiguration( String propname ) {
      boolean loaded = false;
      try {
    	  //System.out.println(propname);
         FileInputStream fin = new FileInputStream( propname );
         loaded = loadConfiguration( fin );
         try { fin.close(); } catch ( Exception e ) {}
      } catch ( Exception e) {
      }
      return loaded;
   }

   public static boolean loadConfiguration( InputStream instream ) {
      boolean loaded = false;

      try {
         WP_PROPS.load( instream );
         
 	    //System.out.println(System.getProperty("was.home"));
 	    //System.out.println(WP_PROPS.getProperty( WPConstants.PROP_WAS_HOME));
 	    //System.out.println(WP_PROPS.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ));
 	    //System.out.println(WP_PROPS.getProperty(WPConstants.PROP_LC_HOME ));
	    
         loaded = true;
         try { instream.close(); } catch ( Exception e ) {}
      } catch ( Exception e) {
      }
      return loaded;
   }

   
   /*
    * Use this type for products with pointers to WP
    * 
    * eg: WBCR
    *       filename = <install_path>/version/wbcrconfig.properties
    *       propname = WpInstallLocation
    *       propsuffix = /config/wpconfig.properties
    * 
    * results in <WpInstallLocation>/config/wpconfig.properties
    * being loaded into WP_PROPS
    * 
    */
   public static boolean loadConfiguration( String filename, String propname, String propsuffix ) {
       boolean loaded = false;
       
       //expecting a file to load, fail if it's missing . . .
       File pointerFile = new File(filename);
       if( !pointerFile.isFile() || !pointerFile.canRead() ) {
           debug("WPConfig.loadConfiguration(String,String,String) -- secondary file not found: " + filename);
           return false;
       }
       
       //case 0: .properties file
       if( filename.toLowerCase().endsWith(".properties")) {
           FileInputStream pointerFin = null;
           FileInputStream fin = null;
           try {
               pointerFin = new FileInputStream(filename);
               Properties pointerProps = new Properties();
               pointerProps.load(pointerFin);
               String secondaryPropsFile = pointerProps.getProperty(propname) + File.separator + propsuffix; 
               // System.out.println("secondary props file is " +   secondaryPropsFile);
               fin = new FileInputStream( secondaryPropsFile );
               loaded = loadConfiguration( fin );

               if (pointerProps.getProperty("LWP_HOME") != null) {
                   WP_PROPS.put( WPConstants.PROP_WCS_HOME, pointerProps.getProperty("LWP_HOME") );  
               }
           } catch ( Exception e) {
               //on exception, fall through and return false
               debug("WPConfig.loadConfiguration(String,String,String) -- exception reading .properties file:");
               // System.out.println("WPConfig.loadConfiguration(String,String,String) -- exception reading .properties file:");
               // System.out.println(e.toString() );

               if (debug) { e.printStackTrace(); }
           } finally {
               try { fin.close(); }        catch ( Exception e ) {}
               try { pointerFin.close(); } catch ( Exception e ) {}
           }
       } 
       //case 1: .bat/.sh env file
       else {
           BufferedReader  in  = null;
           FileInputStream fin = null;
           try {
               in = new BufferedReader(new FileReader(filename));
               String inline = in.readLine();
               boolean foundPropName = false;

               while ( null != inline && !foundPropName ) {
                   debug( "WPconfig.loadConfiguration(String,String,String)::inline == '" + inline + "'" );
                   // System.out.println( "WPconfig.loadConfiguration(String,String,String)::inline == '" + inline + "'" );
                   // System.out.flush();

                   if (
                           null != inline 
                           &&
                           !inline.trim().toLowerCase().startsWith("#")     //skip Unix shell comments
                           &&
                           !inline.trim().toLowerCase().startsWith("rem")   //skip Win shell comments
                      ) {
                       int secondaryHomeIndex = inline.indexOf(propname);
                       debug( "WPconfig.loadConfiguration(String,String,String)::secondaryHomeIndex == '" + secondaryHomeIndex + "'" );
                       // System.out.println( "WPconfig.loadConfiguration(String,String,String)::secondaryHomeIndex == '" + secondaryHomeIndex + "'" );
                       // System.out.flush();

                       if ( -1 != secondaryHomeIndex ) {
                           foundPropName = true;
                           //this is the line, so now we're expecting something like:
                           //    export WPS_HOME=/opt/IBM/WorkPlace/PortalServer
                           //    set WPS_HOME=C:\PROGRA~1\IBM\WORKPL~1\PORTAL~1
                           //we'll take the RHS . . .
                           String secondaryHome = inline.substring( inline.lastIndexOf("=") + 1 ).trim();
                           String secondaryPropsFile = secondaryHome + File.separator + propsuffix; 
                           fin = new FileInputStream( secondaryPropsFile );
                           loaded = loadConfiguration( fin );
                       }
                   }
                   //get next line
                   inline = in.readLine();
               }
               
           } catch (Exception e) {
               //fall through and return false
               debug("WPConfig.loadConfiguration(String,String,String) -- exception reading: " + filename );
               if (debug) { e.printStackTrace(); }
           } finally {
               try {  in.close(); }        catch ( Exception e ) {}
               try { fin.close(); }        catch ( Exception e ) {}
           }

       }
       return loaded;
    }


   public static String getProperty( String propName ) {
      return WP_PROPS.getProperty( propName );
   }

   public static void  setProperty( String propName, String value ) {
      WP_PROPS.setProperty( propName, value );
   }

   public static boolean hasProperty( String propName ) {
      return WP_PROPS.containsKey( propName );
   }


   public static void dumpProperties( Properties props, PrintStream pStream ){
       Iterator iterator = props.keySet().iterator();
       if (iterator.hasNext())
       {
          String[] nameList = new String[props.size()];
          int count = 0;
          while (iterator.hasNext())
          {
             String aName = (String) iterator.next();
             nameList[count++] = aName;
          }
          //-------------------------------------------------
          // Sort the property names before dumping
          //-------------------------------------------------
          sort(nameList);

          //-------------------------------------------------
          // Dump out the properties in sorted order
          //-------------------------------------------------
             pStream.println("---- Begin dump of properties ----");
             for (int index=0; index < nameList.length; ++index)
             {
                String aName = nameList[index];
                String aValue = props.getProperty(aName);
                pStream.println(aName + "=" + aValue);
             }
             pStream.println("---- End dump of properties ----");
       }
   }

  /**
   * Sort the given array of strings, using a simple bubble sort, since we don't expect
   * to sort many items here
   *
   * @param strings array of strings to sort
   **/
   public static void sort(String strings[])
   {
     boolean swapped = true;
     for (int i = strings.length; swapped && (--i >= 0); )
     {
       swapped = false;
       for (int j = 0; j < i; j++)
       {
         if (strings[j].compareTo(strings[j+1]) > 0)
         {
            String tempSwap = strings[j];
            strings[j] = strings[j+1];
            strings[j+1] = tempSwap;
            swapped = true;
         }
       }
     }
   }   

   public static void setWas6ProfileInfo()
   {
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
          while (currLine != null && fsdbScriptName == null) 
          {
          if (currLine.indexOf("properties" + File.separator + "fsdb") != -1 && currLine.indexOf("#") == -1) {
             StringTokenizer lineTokenizer = new StringTokenizer(currLine,"/\\");
             fsdbScriptName = lineTokenizer.nextToken();
             while (lineTokenizer.hasMoreTokens() && !fsdbScriptName.equals("fsdb"))
             {
               fsdbScriptName = lineTokenizer.nextToken();
             } // end while
             fsdbScriptName = lineTokenizer.nextToken();
             lineTokenizer = new StringTokenizer(fsdbScriptName,"\"");
             fsdbScriptName = lineTokenizer.nextToken();
           } // end if
             currLine = wpsCSBufferedReader.readLine();
          } //end while
        } catch (IOException e) {
           System.out.println(   "error.reading.property.file" + wpsConfigScriptName  );
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
              } // end if
                 currLine = fsdbSBufferedReader.readLine();
           } // end while
        } catch (IOException e) {
          System.out.println(  "error.reading.property.file" + fsdbScriptName  );
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
           } // end if
           currLine = wUSBufferedReader.readLine();
        }   // end while
        } catch (IOException e) {
          System.out.println(  "error.reading.property.file" + fsdbScriptName  );
          WPConfig.setProperty( WPConstants.PROP_USER_INSTALL_ROOT, wasHome );
        }
      } // end if WAS 6

       

    }

   public static boolean  checkWPPasswords()
 {

     boolean activateGetPasswordsPanel = false;

     pwdsToUpdate1 = "PlacePasswordHere";

     String wpsLocation = WPConfig.getProperty( "WpsInstallLocation" );

     String ConfigFileName = wpsLocation + File.separator + "config" + File.separator + "wpconfig.properties";

     CommentedProperties ConfigProps = readPropertiesFile(ConfigFileName);


     String PortalPassword = ConfigProps.getProperty( "PortalAdminPwd" );
     String WasPassword = ConfigProps.getProperty( "WasPassword" );



     // String PortalPassword = WPConfig.getProperty( "WasPassword" );
     // System.out.println("Portal Admin Password from wpconfig.properties : " + PortalPassword );


     // if ( (PortalPassword != null) && (PortalPassword.length() > 0)  )
     if ( (PortalPassword != null) && (PortalPassword.length() > 0) && !(PortalPassword.equals("ReplaceWithYourPwd")) )
     {
     }
     else
     {
         activateGetPasswordsPanel = true;

         if (pwdsToUpdate1.equals("PlacePasswordHere")) {
             pwdsToUpdate1 = "     PortalAdminPwd";
         } else {
             pwdsToUpdate1 = pwdsToUpdate1 + " , PortalAdminPwd";
         }
     }

     // String WasPassword = WPConfig.getProperty( "WasPassword" );
     // System.out.println("WasPassword from wpconfig.properties : " + WasPassword );


     if ( (WasPassword != null) && (WasPassword.length() > 0) && !(WasPassword.equals("ReplaceWithYourWASUserPwd")) )
     {
     }
     else
     {
         activateGetPasswordsPanel = true;

         if (pwdsToUpdate1.equals("PlacePasswordHere")) {
             pwdsToUpdate1 = "     WasPassword";
         } else {
             pwdsToUpdate1 = pwdsToUpdate1 + " , WasPassword";
         }

     }

     if (pwdsToUpdate1.equals("PlacePasswordHere")) 
       pwdsToUpdate1 = " ";


     return activateGetPasswordsPanel;



 }



   public static boolean checkDBPasswords()
{

     boolean activateGetPasswordsPanel = false;

     pwdsToUpdate2 = "PlacePasswordHere";
     pwdsToUpdate3 = "PlacePasswordHere";
     pwdsToUpdate4 = "PlacePasswordHere";

     int pwdUpdateCount = 0;


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



     String wpsLocation = WPConfig.getProperty( "WpsInstallLocation" );

     String DBfileName = wpsLocation + File.separator + "config" + File.separator + "wpconfig_dbdomain.properties";

     CommentedProperties DBprops = readPropertiesFile(DBfileName);

     // Get Database Types

     ReleaseDbType = DBprops.getProperty( "release.DbType" );
     CustomizationDbType = DBprops.getProperty( "customization.DbType" );
     CommunityDbType = DBprops.getProperty( "community.DbType" );
     JcrDbType = DBprops.getProperty( "jcr.DbType" );
     WmmDbType = DBprops.getProperty( "wmm.DbType" );
     FeedbackDbType = DBprops.getProperty( "feedback.DbType" );
     LikemindsDbType = DBprops.getProperty( "likeminds.DbType" );
     DesignerDbType = DBprops.getProperty( "designer.DbType" );
     SyncDbType = DBprops.getProperty( "sync.DbType" );

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

      
     String ReleasePassword = DBprops.getProperty( "release.DbPassword" );

     //System.out.println("Release Password from wpconfig_dbdomain.properties : " + ReleasePassword );

     if (ReleaseCloudscape == true)
     {
     }
     else if ((ReleasePassword != null) && (ReleasePassword.length() > 0)  && !(ReleasePassword.startsWith("ReplaceWithYour"))) {
     }
     else {
         activateGetPasswordsPanel = true;

         // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     release.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , release.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     release.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , release.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     release.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , release.DbPassword";
             }

          }
          // dynamic password filling block end

         
     }

     String CustomizationPassword = DBprops.getProperty( "customization.DbPassword" );

     //System.out.println("Customization Password from wpconfig_dbdomain.properties : " + CustomizationPassword );



     if (CustomizationCloudscape == true)
     {
     }
     else if ((CustomizationPassword != null) && (CustomizationPassword.length() > 0)  && !(CustomizationPassword.startsWith("ReplaceWithYour"))) {
     }
     else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     customization.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , customization.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
              pwdsToUpdate2 = pwdsToUpdate2 + ",";
              pwdsToUpdate3 = "     customization.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , customization.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     customization.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " ,customization.DbPassword";
             }

          }
          // dynamic password filling block end

     }


     String CommunityPassword = DBprops.getProperty( "community.DbPassword" );
     //System.out.println("Community Password from wpconfig_dbdomain.properties : " + CommunityPassword );
     
     if (CommunityCloudscape == true)
     {
     }
     else if ((CommunityPassword != null) && (CommunityPassword.length() > 0)  && !(CommunityPassword.startsWith("ReplaceWithYour"))) {
     }
     else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     community.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , community.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     community.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , community.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     community.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , community.DbPassword";
             }

          }
          // dynamic password filling block end

     }


     String JcrPassword = DBprops.getProperty( "jcr.DbPassword" );
     //System.out.println("Jcr Password from wpconfig_dbdomain.properties : " + JcrPassword );



    if (JcrCloudscape == true)
    {
    }
    else if ((JcrPassword != null) && (JcrPassword.length() > 0)  && !(JcrPassword.startsWith("ReplaceWithYour"))) {
    }
    else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     jcr.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , jcr.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     jcr.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , jcr.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     jcr.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , jcr.DbPassword";
             }

          }
          // dynamic password filling block end

     }


    String WmmPassword = DBprops.getProperty( "wmm.DbPassword" );
    //System.out.println("Wmm Password from wpconfig_dbdomain.properties : " + WmmPassword );
    
    if (WmmCloudscape == true)
    {
    }
    else if ((WmmPassword != null) && (WmmPassword.length() > 0)  && !(WmmPassword.startsWith("ReplaceWithYour"))) {
    }
    else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     wmm.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , wmm.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     wmm.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , wmm.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     wmm.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , wmm.DbPassword";
             }

          }
          // dynamic password filling block end

     }


    String FeedbackPassword = DBprops.getProperty( "feedback.DbPassword" );
    //System.out.println("Feedback Password from wpconfig_dbdomain.properties : " + FeedbackPassword );
    
    if (FeedbackCloudscape == true)
    {
    }
    else if ((FeedbackPassword != null) && (FeedbackPassword.length() > 0)  && !(FeedbackPassword.startsWith("ReplaceWithYour"))) {
    }
    else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     feedback.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , feedback.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     feedback.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , feedback.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     feedback.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , feedback.DbPassword";
             }

          }
          // dynamic password filling block end

     }


    String LikemindsPassword = DBprops.getProperty( "likeminds.DbPassword" );
    //System.out.println("Likeminds Password from wpconfig_dbdomain.properties : " + LikemindsPassword );
    
    if (LikemindsCloudscape == true)
    {
    }
    else if ((LikemindsPassword != null) && (LikemindsPassword.length() > 0)  && !(LikemindsPassword.startsWith("ReplaceWithYour"))) {
    }
    else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     likeminds.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , likeminds.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     likeminds.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , likeminds.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     likeminds.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , likeminds.DbPassword";
             }

          }
          // dynamic password filling block end

     }


    String DesignerPassword = DBprops.getProperty( "designer.DbPassword" );
    //System.out.println("Designer Password from wpconfig_dbdomain.properties : " + DesignerPassword );
    
   if (DesignerCloudscape == true)
   {
   }
   else if ((DesignerPassword != null) && (DesignerPassword.length() > 0)  && !(DesignerPassword.startsWith("ReplaceWithYour"))) {
   }
   else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     designer.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , designer.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     designer.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , designer.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     designer.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , designer.DbPassword";
             }

          }
          // dynamic password filling block end

     }


   String SyncPassword = DBprops.getProperty( "sync.DbPassword" );
   //System.out.println("Sync Password from wpconfig_dbdomain.properties : " + SyncPassword );

   if (SyncCloudscape == true)
   {
   }
   else if ((SyncPassword != null) && (SyncPassword.length() > 0)  && !(SyncPassword.startsWith("ReplaceWithYour"))) {
   }
   else {
         activateGetPasswordsPanel = true;
           // dynamic password filling block start 
         pwdUpdateCount++;

         if (pwdUpdateCount < 4)
         {
         
           if (pwdsToUpdate2.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = "     sync.DbPassword";
           } else {
             pwdsToUpdate2 = pwdsToUpdate2 + " , sync.DbPassword";
           }

          }
          else if (pwdUpdateCount < 7)
          {

           if (pwdsToUpdate3.equals("PlacePasswordHere")) {
             pwdsToUpdate2 = pwdsToUpdate2 + ",";
             pwdsToUpdate3 = "     sync.DbPassword";
            } else {
             pwdsToUpdate3 = pwdsToUpdate3 + " , sync.DbPassword";
            }

          }
          else
          {
            if (pwdsToUpdate4.equals("PlacePasswordHere")) {
              pwdsToUpdate3 = pwdsToUpdate3 + ",";
              pwdsToUpdate4 = "     sync.DbPassword";
             } else {
              pwdsToUpdate4 = pwdsToUpdate4 + " , sync.DbPassword";
             }

          }
          // dynamic password filling block end

   }

   if (pwdsToUpdate2.equals("PlacePasswordHere")) 
       pwdsToUpdate2 = " ";
   if (pwdsToUpdate3.equals("PlacePasswordHere")) 
      pwdsToUpdate3 = " ";
   if (pwdsToUpdate4.equals("PlacePasswordHere")) 
      pwdsToUpdate4 = " ";


           


  return activateGetPasswordsPanel;


}

  public static CommentedProperties readPropertiesFile(String fileName)
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
