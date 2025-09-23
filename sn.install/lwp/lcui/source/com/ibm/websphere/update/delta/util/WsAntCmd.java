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
 *
 *  modified    CD Choi  80172   03-24-04
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
public class WsAntCmd {

   public final static String pgmVersion = "1.7" ;
   public final static String pgmUpdate = "7/15/05" ;

   public static final boolean isWindows =
       com.ibm.websphere.update.util.PlatformUtils.isWindows();
   public static final boolean isWindowsNT =
       ( isWindows && PlatformUtils.isWinNT() );
   public static final boolean isISeries =
       ( PlatformUtils.isISeries() );
   private String   antTask         = null;
   private String[] envVars         = null;

   // Answer a new wsadmin command on the specified
   //
   // Processing messages are placed in the messages buffer.
   //
   // Processing errors are placed in the errors buffer.


   public WsAntCmd( StringBuffer messages, StringBuffer errors) {
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
 * @param antTask  the antTask to set
 * @uml.property  name="antTask"
 */
public void setAntTask( String a ) {
      antTask = a;
   }

   /**
 * @param envVars  the envVars to set
 * @uml.property  name="envVars"
 */
public void setEnvVars( String[] an ) {
      envVars = an;
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

   public   String getResourcePath()
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
      return new String[] {
          "WP_HOME="       + WPConfig.getProperty( WPConstants.PROP_WP_HOME ),
          "WAS_HOME="      + WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ),
      };
   }

   public static final String BIN_DIRECTORY = "bin";

   public static final String WINDOWS_WPWSADMIN_SCRIPT = "ws_ant.bat" ;
   public static final String UNIX_WPWSADMIN_SCRIPT = "ws_ant" ;

   public String getAntScriptPath()
   {
       String binPath = WPConfig.getProperty( WPConstants.PROP_WAS_HOME ) + File.separator + BIN_DIRECTORY;

       String cmdPath;

       if ( isWindows )
           cmdPath = binPath + File.separator + WINDOWS_WPWSADMIN_SCRIPT;
       else if (isISeries) {  
           String wasProdBase = System.getProperty("WAS_PROD_BASE_PATH");
           cmdPath = wasProdBase + File.separator+ BIN_DIRECTORY + File.separator + UNIX_WPWSADMIN_SCRIPT;
       }
       else
           cmdPath = binPath + File.separator + UNIX_WPWSADMIN_SCRIPT;

       log("WPWsAnt Processing Script Path: ", cmdPath);

       return cmdPath;
   }
   
   // Run the specified script/command through wsadmin.
   // connect to the DM server if requested
   //
   // Answer true or false, telling if the deployment
   // was successful.

   public int execute() {


      ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                 ExecCmd.DONT_ECHO_LOG);

      String[] cmd = new String[envVars.length + 2];
      int index = 0;
      cmd[index++] = enquote(getAntScriptPath());
      for (int i=0;i<envVars.length;i++) {
         cmd[index++] = envVars[i];
      }
      cmd[index++] = antTask;

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
          }

      } catch ( Exception ex ) {
          logError("WPWsAnt: Failed With Exception");
          logError("    Task  : ", antTask );

          logError("Exception: ", ex);

          return resultCode;
      }

      // TBD: Need the result codes and their meanings.

      if ( resultCode > 0 ) {
         logError("WPWsAnt: Failed by Result Code");
         logError("    Task  : ", antTask );

          logError("Result Code: " + resultCode);

      } else {
          log("WPWsAnt: OK");
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
