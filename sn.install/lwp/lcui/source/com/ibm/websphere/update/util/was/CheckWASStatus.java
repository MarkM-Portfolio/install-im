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


import com.ibm.websphere.update.delta.earutils.InstallationData;
import com.ibm.websphere.update.delta.earutils.InstanceData;
import com.ibm.websphere.update.util.WPConfig;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.SAXException;

/**
 * Checks to make sure that all WS servers are currently stopped.  <p>
 * @author  Steven Pritko
 */
public class CheckWASStatus {

   private String       wasHome  = null;
   private PrintStream  log      = null;
   private boolean      checkAll = false;  // This flag controls checking on non-appsever servers (jmssevrer, nodeagent, .. )

   public static final Set NON_APPSERVERS = new HashSet();
   static {
      NON_APPSERVERS.add( "jmsserver" );
      NON_APPSERVERS.add( "nodeagent" );
   };

   public CheckWASStatus( String wasHome ) {
      this.wasHome = wasHome;
   }

   /**
 * @param log  the log to set
 * @uml.property  name="log"
 */
public void setLog( PrintStream l ) {
      log = l;
   }

   public void setCheckNonAppServers( boolean check ) {
      this.checkAll = check;
   }


   public boolean checkAllInstances( boolean stopServers ) {

      boolean allStopped = true;
      List allRunningServers = new ArrayList();
      List runningServers = null;
      try {
         Iterator iteratorInstances = this.getInstances( wasHome );
         while ( allStopped && iteratorInstances.hasNext() ) {
            InstanceData instancedataThis = ( InstanceData ) iteratorInstances.next();
            runningServers = getRunningServers( instancedataThis );
            if ( stopServers ) {
               Iterator iter = runningServers.iterator();
               while (iter.hasNext()) {
                  String serverName = (String) iter.next();
                  log( "Attempting to stop server : " + serverName );
                  boolean stopped = WASServerStopper.stopServer( instancedataThis, serverName );
                  if (stopped) {
                     iter.remove();
                  } else {
                     log( "\tUnable to stop server : " + serverName );
                  }
               }
            }
            allRunningServers.addAll( runningServers );
         }
      } catch ( Exception e ) {
         //e.printStackTrace();
         // do nothing if we can't detect WAS status
      }

      allStopped  = allRunningServers.isEmpty();
      if ( !allStopped && log != null ) {
         // log the servers that are still running.
         Iterator iter = allRunningServers.iterator();
         while (iter.hasNext()) {
            log.println( "\tServer " + iter.next() + " is still running." );
         }
      }

      return allStopped;
   }


   /**
    * @return <code>true</code> if all servers for this instance are stopped, else it returns
    * 			   <code>false</code>
    */
   private List getRunningServers( InstanceData instancedataThis )  {
      boolean fAllServersStopped = true;
      Vector vServerJoin = instancedataThis.getServerJoin();

      List runningServers = new ArrayList(vServerJoin.size());

      for ( int i = 0; i < vServerJoin.size(); i++ ) {
         String[] asJoin = ( String[] )vServerJoin.elementAt( i );
         String sServerName = asJoin[ 2 ];
         if ( !checkAll && NON_APPSERVERS.contains( sServerName ) ) continue;  // Skip non appserver
         log( "Checking status of server " + sServerName );
         if ( !isServerStopped( instancedataThis, sServerName ) ) {
            runningServers.add( sServerName );
            //System.out.println( "\tnot stopped." );
         } else {
            //System.out.println( "\tstopped." );
         }
      }

      return runningServers;
   }

   /**
    * Checks whether the given server is stopped
    * <p>
    * @param instancedataThis	The current instance data
    * @param sServerName		The server name of the server to check 
    * <p>
    * @return boolean				<code>true</code> if the server is stopped
    */
   private boolean isServerStopped( InstanceData instancedataThis, String sServerName ) {
      return ServerStatusChecker.isStopped( instancedataThis, sServerName );
   }

   /**
     * Return all the config instances for the current WAS product
     * <p>
     * @param sInstallRoot 	The product install root
     * <p>
     * @return 	An iterator over the collection of instance data objects from the build
     *         		installation root.  Null if preparation failed.
     */
   private Iterator getInstances(  String sInstallRoot ) throws IOException, SAXException {
      InstallationData installationData =
      getInstallationData( sInstallRoot );

      if ( installationData == null )
         return null;
      else
         return installationData.getInstances();
   }

   /**
    * Return an InstallationData object for the given WS install
    * <p>
    * @param sInstallRoot 	The produot install root
    * <p>
    * @return An installation data instance.
    */
   private InstallationData getInstallationData( String sInstallRoot ) throws IOException, SAXException { 
      InstallationData installationData = new InstallationData( sInstallRoot );

      if ( !installationData.prepare() )
         return null;
      else
         return installationData;
   }


   void log( String msg ) {
      if (log !=null ) log.println( msg );
   }

   /**
    * @return The WASProduct object for the product selected by the
    * 			   user to be upgraded
    */
   /*
   private WASProduct getSelectedWSProduct() {
      UpdateProductSelectionPanel upspThis = ( UpdateProductSelectionPanel )
                                             this.getWizard().getWizardTree().findWizardBean( S_PROD_SEL_PANEL );

      return upspThis.getWASProduct();
   }
   */

   public static void main (String args[]) {

      WPConfig.initialize( "C:\\Program Files\\WebSphere\\PortalServer" );

      CheckWASStatus wasStatus = new CheckWASStatus( "C:\\progra~1\\WebSphere\\AppServer" );
      
      wasStatus.setLog( System.out );
      boolean allOK = wasStatus.checkAllInstances( false );

      System.out.println( "checkAllInstances returned with : " + allOK );
   }
}
