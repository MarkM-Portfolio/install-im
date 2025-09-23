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
import com.ibm.websphere.update.delta.earutils.EARActor;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class WsAdminCmd {

   public final static String pgmVersion = "1.7" ;
   public final static String pgmUpdate = "4/29/04" ;


   private String  propertiesName  = null;
   private String  profileName     = null;
   private String  scriptName      = null;
   private String  command         = null;
   private boolean executeOnDM     = false;

   // Answer a new wsadmin command on the specified
   //
   // Processing messages are placed in the messages buffer.
   //
   // Processing errors are placed in the errors buffer.

   public WsAdminCmd( boolean sendToDM,
                      StringBuffer messages,
                      StringBuffer errors)
   {
      executeOnDM = sendToDM;
      this.messages = messages;
      this.errors = errors;
   }

   public WsAdminCmd( StringBuffer messages,
                      StringBuffer errors)
   {
      this( false, messages, errors );
   }

   protected StringBuffer messages;

   /**
 * @return  the messages
 * @uml.property  name="messages"
 */
public StringBuffer getMessages()
   {
      return this.messages;
   }

   public void log(String text)
   {
      getMessages().append(text + EARActor.lineSeparator);
   }
   public void log(String text1, Object text2)
   {
       this.getMessages().append(text1 + text2 + EARActor.lineSeparator);
   }


   protected StringBuffer errors;

   /**
 * @return  the errors
 * @uml.property  name="errors"
 */
public StringBuffer getErrors()
   {
      return this.errors;
   }

   public void logError(String text)
   {
      getErrors().append(text + EARActor.lineSeparator);
   }

   /**
 * @param scriptName  the scriptName to set
 * @uml.property  name="scriptName"
 */
public void setScriptName( String script ) {
      scriptName = script;
   }

   /**
 * @param propertiesName  the propertiesName to set
 * @uml.property  name="propertiesName"
 */
public void setPropertiesName( String p ) {
      propertiesName = p;
   }

   /**
 * @param profileName  the profileName to set
 * @uml.property  name="profileName"
 */
public void setProfileName( String p ) {
      profileName = p;
   }

   public void setCommandString( String cmd ) {
      command = cmd;
   }

   /**
 * @return  the scriptName
 * @uml.property  name="scriptName"
 */
public String getScriptName() {
      return scriptName;
   }

   /**
 * @return  the propertiesName
 * @uml.property  name="propertiesName"
 */
public String getPropertiesName() {
      return propertiesName;
   }

   /**
 * @return  the profileName
 * @uml.property  name="profileName"
 */
public String getProfileName() {
      return profileName;
   }

   public String getCommandString() {
      return command;
   }

   // Run the specified script/command through wsadmin.
   // connect to the DM server if requested
   //
   // Answer true or false, telling if the deployment
   // was successful.

   public boolean execute() {

      if (scriptName == null && command == null) {
         // This is an error.
         logError("wsadmin: Failed: " + "Script name or command missing");
         return false;
      }

      String wasuser    = WPConfig.getProperty( WPConstants.PROP_WAS_USER );
      String waspass    = WPConfig.getProperty( WPConstants.PROP_WAS_PASS );

      String dmServer   = WPConfig.getProperty( WPConstants.PROP_WAS_DM_SERVER );
      String dmPort     = WPConfig.getProperty( WPConstants.PROP_WAS_DM_PORT );

      log("Performing wsadmin operation:");
      //log("    conntype   : SOAP" );

      LinkedList args = new LinkedList();
      args.add( "-conntype" );
      args.add( "SOAP" );
      //args.add( "NONE" );
      // If this isn't a DM specific operation, connect locally.
      if ( executeOnDM ) {
         if ( dmServer != null ) {
            args.add( "-host" );
            args.add( dmServer );
            log("    Host       : " + dmServer );
            if ( dmPort != null ) {
               args.add( "-port" );
               args.add( dmPort );
               log("    Port       : " + dmPort );
            }
         } else {
            log("wsadmin: Failed: " + "Requested to connect to DeploymentManager, DM server not specifid, connecting locally.");
            //return false;
         }
      }
      if ( (wasuser != null) && (waspass != null ) ) {
         args.add( "-user" );
         args.add( wasuser );
         args.add( "-password" );
         args.add( waspass );
         log("    User       : " + wasuser );
         log("    Pass       : ********" );
      }

      if ( propertiesName != null ) {
         args.add( "-p" );
         args.add( propertiesName );
         log("    Properties : " + propertiesName );
      }

      if ( profileName != null ) {
         args.add( "-profile" );
         args.add( profileName );
         log("    Profile    : " + profileName );
      }

      // Script file takes precedence
      if (scriptName != null) {
         args.add( "-f" );
         args.add( scriptName );
         log("    Script     : " + scriptName );
      } else {
         args.add( "-c" );
         args.add( command );
         log("    Command    : " + command );
      }

      log("Running");

      int resultCode = -1;
      try {
         ExecCmd exec = new ExecCmd(false); // Don't adjust

         String binPath = WPConfig.getProperty( WPConstants.PROP_USER_INSTALL_ROOT ) + File.separator + "bin";
         String cmdPath;
         if ( com.ibm.websphere.update.util.PlatformUtils.isWindows() )
            cmdPath = binPath + File.separator + "wsadmin.bat";
         else
            cmdPath = binPath + File.separator + "wsadmin.sh";

         args.addFirst( cmdPath );
         log("wsadmin Path: " + cmdPath);

         String[] cmd = (String[])args.toArray( new String[0] );

         Vector results = new Vector();
         Vector logResults = new Vector();

         try {
            resultCode = exec.Execute(cmd,
                                      ExecCmd.DONT_ECHO_STDOUT,
                                      ExecCmd.DONT_ECHO_STDERR,
                                      results, logResults);
         } finally {
            logResults(results, "Result: ");
            logResults(logResults, null);
         }


      } catch (Exception ex) {
         logError("wsadmin - Failed: " + ( (scriptName == null) ? command: scriptName ) );
         logError("Exception: " + ex);
         return false;
      }
      // TBD: Determine the result codes and their meanings.

      if (resultCode > 0) {
         logError("wsadmin - Failed: " + ( (scriptName == null) ? command: scriptName ));
         return false;
      } else {
         log("wsadmin: OK");
         return true;
      }
   }

   protected void logResults(Vector results, String prefix)
   {
       int resultLines = results.size();

       for ( int lineNo = 0; lineNo < resultLines; lineNo++ ) {
           String nextLine = (String) results.elementAt(lineNo);

           if ( prefix != null )
               log(prefix, nextLine);
           else
               log(nextLine);
       }
   }


}
