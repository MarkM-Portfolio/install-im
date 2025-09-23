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
package com.ibm.websphere.update.delta.util;

/*
 *  @ (#) WsAdminCmd.java
 *
 *  invoke wsadmin to run command/script
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.delta.adminconfig.WPWsAdminErrorCodes;
import com.ibm.websphere.update.delta.earutils.EARActor;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.net.*;
import java.util.*;

//import com.ibm.websphere.management.*;

/**
 *  
 */
public class WsAdminClient {

   public final static String pgmVersion = "1.9" ;
   public final static String pgmUpdate = "7/15/05" ;
      
   public static final int ACTION_UNKNOWN  = 0;
   public static final int ACTION_EXTRACT  = 1;
   public static final int ACTION_REDEPLOY = 2;


   public static final boolean isWindows =
       com.ibm.websphere.update.util.PlatformUtils.isWindows();
   public static final boolean isWindowsNT =
       ( isWindows && PlatformUtils.isWinNT() );
   public static final boolean isISeries =
       ( PlatformUtils.isISeries() );

   private String  appName         = null;
   private String  earFile         = null;
   private int     action          = ACTION_UNKNOWN;

   // Answer a new wsadmin command on the specified
   //
   // Processing messages are placed in the messages buffer.
   //
   // Processing errors are placed in the errors buffer.


   public WsAdminClient( StringBuffer messages, StringBuffer errors) {
      this.messages = messages;
      this.errors = errors;
   }

   protected StringBuffer messages;

   /**
 * @return  the messages
 * @uml.property  name="messages"
 */
public StringBuffer getMessages() {
      return this.messages;
   }

   public void log(String text) {
      getMessages().append(text + EARActor.lineSeparator);
   }

   public void log(String text1, Object text2) {
       this.getMessages().append(text1 + text2 + EARActor.lineSeparator);
   }

   protected StringBuffer errors;

   /**
 * @return  the errors
 * @uml.property  name="errors"
 */
public StringBuffer getErrors() {
      return this.errors;
   }

   public void logError(String text) {
      getErrors().append(text + EARActor.lineSeparator);
   }

   public void logError(String text1, Object text2){
       this.getErrors().append(text1 + text2 + EARActor.lineSeparator);
   }

   protected void logResults(Vector results, String prefix) {
       int resultLines = results.size();

       for ( int lineNo = 0; lineNo < resultLines; lineNo++ ) {
           String nextLine = (String) results.elementAt(lineNo);

           if ( prefix != null )
               log(prefix, nextLine);
           else
               log(nextLine);
       }
   }

   /**
 * @param action  the action to set
 * @uml.property  name="action"
 */
public void setAction( int a ) {
      action = a;
   }

   /**
 * @param appName  the appName to set
 * @uml.property  name="appName"
 */
public void setAppName( String an ) {
      appName = an;
   }

   /**
 * @param earFile  the earFile to set
 * @uml.property  name="earFile"
 */
public void setEarFile( String ef ) {
      earFile = ef;
   }


   protected static final String slashText    = System.getProperty("file.separator");
   protected static final char   slashChar    = slashText.charAt(0);

   protected static final String urlSlashText = "/";
   protected static final char   urlSlashChar = '/';

   protected String correctPath(String path) {
       return ( ( path == null ) ? null : path.replace(urlSlashChar, slashChar) );
   }

   // Perform an uncompression of the specified archive
   // to the specified directory.
   //
   // The initial contents of the directory are removed.
   //
   // Answer true or false telling if the operation was successful.

   protected String[] correctCmd(String[] cmd) {
       if ( !isWindows ) {
           log("No launcher.exe: Not a windows platform.");
           return cmd;

       } else if ( isWindowsNT ) {
           log("No launcher.exe: Windows NT.");
           return cmd;

       } else {
           log("Windows (not windows NT): Adding launcher.exe");

           String fullLauncherPath =
               getResourcePath() + File.separator +
               "earLauncher"     + File.separator +
               "launcher.exe";

           log("Full Launcher Path: ", fullLauncherPath);

           String[] updateCmd = new String[cmd.length + 1];

           updateCmd[0] = enquote(fullLauncherPath);

           for ( int argNo = 0; argNo < cmd.length; argNo++ )
               updateCmd[argNo + 1] = cmd[argNo];

           return updateCmd;
       }
   }

   // 'getResourcePath' assumes that the receiver's class is stored
   // in a JAR file.

   protected String getResourcePath()
   {
		String puiInstallDir = System.getProperty("pui_install_root");
		if (puiInstallDir != null) {
			return puiInstallDir;
		}
 

       String resourceName = getClass().getName();
       log("Retrieving resource path for: ", resourceName);

       resourceName = resourceName.replace('.', '/');
       resourceName += ".class";
       log("Target resource name: ", resourceName);

       URL resourceURL = getClass().getClassLoader().getResource(resourceName);

       String encodedURLText = resourceURL.getPath();
       log("Encoded resource path: ", encodedURLText);

       String decodedURLText  = URLDecoder.decode(encodedURLText);
       log("Decoded resource path: ", decodedURLText);

       int firstSlashLoc = decodedURLText.indexOf("/");
       int jarIndicatorLoc = decodedURLText.indexOf("!");

       if ( jarIndicatorLoc == -1 )
           jarIndicatorLoc = decodedURLText.length();

       String resourcePath = decodedURLText.substring(firstSlashLoc + 1, jarIndicatorLoc);

       int lastSlashLoc = resourcePath.lastIndexOf("/");
       resourcePath = resourcePath.substring(0, lastSlashLoc);
       resourcePath = correctPath(resourcePath);

       // Make sure resourcePath starts with a / on non-windows platforms. (d60304)
       if ( !isWindows && !resourcePath.startsWith("/") ) {
          resourcePath = "/" + resourcePath;
       }

       log("Resource path: ", resourcePath);

       return resourcePath;
   }

   protected String[] getEnvSettings() {
      int size = 4;
      if ( isWindows || isISeries ) size++;
      if ( WPConfig.getProperty( WPConstants.PROP_WAS_USER_SCRIPT ) != null) size++;
      String[] envvars = new String[size];
      int idx = 0;
      envvars[idx++] = "WP_HOME="       + WPConfig.getProperty( WPConstants.PROP_WP_HOME );
      envvars[idx++] = "WP_PUI_CONFIG=" + WPConfig.getProperty( WPConstants.PROP_WP_PUI_CFG );
      envvars[idx++] = "ADMIN_EXIT_CMD=YES";  // Make sure the scirpt does an exit w/ return code form java
      if ( WPConfig.getProperty( WPConstants.PROP_WAS_USER_SCRIPT ) != null) {
          envvars[idx++] = "WAS_USER_SCRIPT=" + WPConfig.getProperty( WPConstants.PROP_WAS_USER_SCRIPT );
      }
      //for iSeries
      if ( isISeries ){
          envvars[idx++] = "WAS_INSTANCE=" + WPConfig.getProperty( WPConstants.PROP_WAS_INSTANCE );
          envvars[idx++] = "WAS_HOME="      + WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME );
      }
      else {
          envvars[idx++] = "WAS_HOME="      + WPConfig.getProperty( WPConstants.PROP_WAS_HOME );
      }
      if ( isWindows ) {
         envvars[idx++] = "SystemRoot=" + System.getProperty( "com.ibm.wp.pui.systemroot" );
      }
      return envvars;
   }

   public static final String BIN_DIRECTORY = "bin";

   public static final String WINDOWS_WPWSADMIN_SCRIPT = "wpwsadmin.bat" ;
   public static final String UNIX_WPWSADMIN_SCRIPT = "wpwsadmin.sh" ;

   public String getAdminScriptPath()
   {
       String binPath = getResourcePath() + File.separator + BIN_DIRECTORY;

       String cmdPath;

       if ( isWindows )
           cmdPath = binPath + File.separator + WINDOWS_WPWSADMIN_SCRIPT;
       else
           cmdPath = binPath + File.separator + UNIX_WPWSADMIN_SCRIPT;

       log("WPWsAdmin Processing Script Path: ", cmdPath);

       return cmdPath;
   }



   // Run the specified script/command through wsadmin.
   // connect to the DM server if requested
   //
   // Answer true or false, telling if the deployment
   // was successful.

   private static final String[] EMPTY_STRING_ARRAY = new String[0];

   public int execute() {


      ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                 ExecCmd.DONT_ECHO_LOG);

      ArrayList list = new ArrayList();
      list.add( enquote(getAdminScriptPath()) );
      list.add( (action == ACTION_EXTRACT) ? "-export" : "-redeploy" );
      list.add( "-appname" );
      list.add( appName );
      list.add( "-earfile" );
      list.add( enquote(earFile) );

      if ( EARActor.isDebug ) {
          // If EAR debugging is enabled, pass it along to the wpwsadmin
          list.add( "-debug" );
      }
      String[] cmd = (String[])list.toArray( EMPTY_STRING_ARRAY );

      cmd = correctCmd(cmd);
      if ( cmd == null )
          return WPWsAdminErrorCodes.WPWSADMIN_ERROR_UNKNOWN;

      int resultCode = WPWsAdminErrorCodes.WPWSADMIN_ERROR_UNKNOWN;

      try {
          Vector results = new Vector();
          Vector logResults = new Vector();

          try {
              resultCode = exec.Execute(cmd,
                                        getEnvSettings(),
                                        ExecCmd.DONT_ECHO_STDOUT,
                                        ExecCmd.DONT_ECHO_STDERR,
                                        results,
                                        logResults);

          } finally {
              logResults(results, "Result: ");
              logResults(logResults, null);

              //deleteEarTempFiles();
              //deleteOnExitRecurse(archiveTmp);
          }

      } catch ( Exception ex ) {
          logError("WPWsAdmin: Failed With Exception");
          logError("    Action  : ", (action == ACTION_EXTRACT) ? "Export" : "Redeploy" );
          logError("    AppName : ", appName);
          logError("    EarFile : ", earFile);

          logError("Exception: ", ex);

          return resultCode;
      }

      // TBD: Need the result codes and their meanings.

      if ( resultCode > 0 ) {
         logError("WPWsAdmin: Failed by Result Code");
         logError("    Action  : ", (action == ACTION_EXTRACT) ? "Export" : "Redeploy" );
         logError("    AppName : ", appName);
         logError("    EarFile : ", earFile);

          logError("Result Code: " + resultCode);

      } else {
          log("WPWsAdmin: OK");
      }
      return resultCode;

   }

   // Taking this out ... the exec statement is said to
   // automatically enquote arguments are needed.

   public String enquote(String text)
   {
       return text;

       // String platformQuote = getPlatformQuote();
       //
       // return platformQuote + text + platformQuote;
   }
}
