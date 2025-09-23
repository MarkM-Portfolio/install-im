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

package com.ibm.websphere.update.util.was;
import java.io.File;

import com.ibm.websphere.update.delta.earutils.BetterExecCmd;
import com.ibm.websphere.update.delta.earutils.InstanceData;


import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import com.ibm.websphere.update.util.PlatformUtils;

/**
 * Generic class to check the status of a given server in a 
 * given instance
 * <p>
 * @author Steven Pritko
 */
public class WASServerStopper {
   /**
    * @return 	<code>true</code> if the given server is stopped, else it
    * 			   	return <code>false</code>
    */
   public static boolean stopServer( InstanceData instancedataThis, String sServerNameThis ) {     
      String sSetupInstanceCmd = instancedataThis.getLocation() + File.separator + "bin" + 
                                 File.separator + WASServerStopper.getSetUpCmdLineName();

      String sServerStopCmd = instancedataThis.getLocation() + File.separator + "bin" + 
                              File.separator + WASServerStopper.getServerStopName();

      String sUserPwInfo  = getUserAuthInfo();


      String[] asServerStopCmd = new String[ 3 ];
      asServerStopCmd[ 0 ] = getShellName();
      asServerStopCmd[ 1 ] = getShellTransientExecCmdOptionName();
      asServerStopCmd[ 2 ] = sSetupInstanceCmd + 
                             getMultiCommandChar() + 
                             sServerStopCmd + " " + 
                             sUserPwInfo +       // need to add user/password if Security enabled
                             sServerNameThis;

      BetterExecCmd bec = new BetterExecCmd();
      bec.executeIncomingArguments( asServerStopCmd );

      return ServerStatusChecker.isStopped( instancedataThis, sServerNameThis );
   }


   private static String getUserAuthInfo() {
      String authInfo = "";
      String user = WPConfig.getProperty( WPConstants.PROP_WAS_USER );
      String pass = WPConfig.getProperty( WPConstants.PROP_WAS_PASS );
      if ( (user != null) && (user.length() > 0)  && (pass != null) && (pass.length() > 0) ) {
         authInfo = "-username \"" + user + "\" -password \"" + pass + "\" ";
      }
      return authInfo;

   }

   /**
   * @return The execution shell name
   */
   private static String getShellName() {
      if ( PlatformUtils.isWindows() ) {
         return "cmd.exe";
      } else {
         return "sh";
      }

   }

   /**
   * @return The multi-command seperator string for the OS shell
   */
   private static String getMultiCommandChar() {
      if ( PlatformUtils.isWindows() ) {
         return "&";
      } else {
         return ";";
      }

   }

   /**
   * @return The transient command execution flag for the OS shell
   */
   private static String getShellTransientExecCmdOptionName() {
      if ( PlatformUtils.isWindows() ) {
         return "/c";
      } else {
         return "-c";
      }

   }

   /**
    * @return The WSAdmin shell script's name
    */
   private static String getServerStopName() {
      if ( PlatformUtils.isWindows() ) {
         return "stopServer.bat";
      } else {
         return "stopServer.sh";
      }

   }

   /**
    * @return The setupCmdLine shell script's name
    */
   private static String getSetUpCmdLineName() {
      if ( PlatformUtils.isWindows() ) {
         return "setupCmdLine.bat";
      } else {
         return "setupCmdLine.sh";
      }

   }

   /** Class constants follow */
   private static final String S_STARTED_FLAG = "STARTED";
}
