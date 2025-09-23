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
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

/**
 * Generic class to check the status of a given server in a 
 * given instance
 * <p>
 * @author Rohit V. Kapoor
 */
public class ServerStatusChecker {
   /**
    * @return 	<code>true</code> if the given server is stopped, else it
    * 			   	return <code>false</code>
    */
   public static boolean isStopped( InstanceData instancedataThis, String sServerNameThis ) {     
      String sSetupInstanceCmd = instancedataThis.getLocation() + File.separator + "bin" + 
                                 File.separator + ServerStatusChecker.getSetUpCmdLineName();

      String sServerStatusCmd = instancedataThis.getLocation() + File.separator + "bin" + 
                                File.separator + ServerStatusChecker.getServerStatusName();

      String sUserPwInfo  = getUserAuthInfo();

      String[] asServerStatusCmd = new String[ 3 ];
      asServerStatusCmd[ 0 ] = ServerStatusChecker.getShellName();
      asServerStatusCmd[ 1 ] = ServerStatusChecker.getShellTransientExecCmdOptionName();

      asServerStatusCmd[ 2 ] = sSetupInstanceCmd + 
                               getMultiCommandChar() + 
                               sServerStatusCmd + " " + 
                               sServerNameThis + 
                               " " + sUserPwInfo;       // need to add user/password if Security enabled
      
      BetterExecCmd bec = new BetterExecCmd();
      bec.executeIncomingArguments( asServerStatusCmd );

      /*
          try{
              for ( int i = 0; i < asServerStatusCmd.length; i++ ) {
                  if ( null != asServerStatusCmd[ i ] ) {
                      System.out.println( "ServerStatusChecker::asServerStatusCmd[ " + i + " ] == " + asServerStatusCmd[ i ]);
                  }
              }
          }catch (Exception e) { e.printStackTrace();  }

          System.out.println("STDOUT:");
          System.out.println(bec.getProcessStdOut());
          System.out.println("STDERR:");
          System.out.println(bec.getProcessStdOut());
          System.out.println("END");
      */    

      return ServerStatusChecker.isServerStopped( bec.getProcessStdOut() );
   }


   private static String getUserAuthInfo() {
      String authInfo = "";
      String user = WPConfig.getProperty( WPConstants.PROP_WAS_USER );
      String pass = WPConfig.getProperty( WPConstants.PROP_WAS_PASS );
      if ( (user != null) && (user.length() > 0)  && (pass != null) && (pass.length() > 0) ) {
         authInfo = "-username \"" + user + "\" -password \"" + pass + "\"";
      }
      return authInfo;

   }

   /**
    * Parses given cmd output and returns <code>true</code> if the server
    * is indicated to be stopped, else it returns <code>false</code>
    * <p>
    * @return 	<code>true</code> if the server is indicated to be stopped, else 
    * 				it returns <code>false</code>
    */
   private static boolean isServerStopped( String sCmdOut ) {
       
      boolean stopped_flag = true;
      // any sCmdOut value other than STARTED (or invalid credentials if security is enabled) will return a status of stopped
      
      /*      
      try{
          System.out.println( "ServerStatusChecker:: " + sCmdOut);
      }catch (Exception e) { e.printStackTrace();  }
      */
      
      if ( ( sCmdOut.indexOf( S_STARTED_FLAG ) > -1 ) || ( sCmdOut.indexOf( S_ACCESS_FLAG ) > -1 ) ) {
          stopped_flag = false;
          if ( ( sCmdOut.indexOf( S_ACCESS_FLAG ) > -1 ) ) {
              System.out.println( "Access denied due to insufficient or empty credentials" );
          }
      }

      return stopped_flag;
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
   private static String getServerStatusName() {
      if ( PlatformUtils.isWindows() ) {
         return "serverStatus.bat";
      } else {
         return "serverStatus.sh";
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
   
   // ADMN0022E: Access denied for the getState operation on Server MBean due to insufficient or empty credentials.
   private static final String S_ACCESS_FLAG = "ADMN0022";
}
