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

package com.ibm.websphere.product.tasks;


import com.ibm.websphere.product.WPProduct;
import com.ibm.websphere.product.history.WPHistory;
import com.ibm.websphere.product.history.xml.*;

//import com.ibm.ws.ant.utils.*;
import java.io.File;
import java.util.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.*;

// **********************************************************************************************************
// *
// * 
// * File Name, Component Name, Release
// * wps/fix/src/com/ibm/websphere/product/tasks/WPHistoryEventTask.java, wps.base.fix, wps6.fix
// * 
// * History 1.5, 3/25/04
// *
// * 
// * 
// **********************************************************************************************************/



/**
 *                     <P>                                                                                                                                                                   
 *                     Creates an config Event into the WPHistory repository.
 *                             Used in conjunction with the config-task tags in the efix and ptf driver files.
 *                     <p>                                                                                                                                                                   
 *                     <h4>Parameters</h4>                                                                                                                                                   
 *                     <table border="1" cellpadding="2" cellspacing="0">                                                                                                                    
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top"><b>Attribute</b></td>                                                                                                                            
 *                         <td valign="top"><b>Description</b></td>                                                                                                                          
 *                         <td align="center" valign="center"><b>Required</b></td>                                                                                                           
 *                       </tr>                                                                                                                                                               
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top">ifixID</td>                                                                                                                                      
 *                         <td valign="top">Specifies the ifix ID.                                                                                                                           
 *                         <td valign="top" align="center">Exactly one of ifixID or ptfID</td>                                                                                                                          
 *                       </tr>                                                                                                                                                               
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top">ptfID</td>                                                                                                                                      
 *                         <td valign="top">Specifies the ptf ID.                                                                                                                           
 *                         <td valign="top" align="center">Exactly one of ifixID or ptfID</td>                                                                                                                          
 *                       </tr>                                                                                                                                                               
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top">action</td>                                                                                                                                      
 *                         <td valign="top">The action being perform for this event.  Must be one of                                                                                         
 *                         { &quot;install&quot;, &quot;uninstall&quot;, &quot;config&quot;, or &quot;unconfig&quot; }                                                                       
 *                         <td valign="top" align="center">Yes</td>                                                                                                                          
 *                       </tr>                                                                                                                                                               
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top">actionType</td>                                                                                                                                  
 *                         <td valign="top">Type action being done.  Must be one of                                                                                                          
 *                           { &quot;add&quot;, &quot;remove&quot;, &quot;replace&quot;, or &quot;patch&quot;,                                                                               
 *                           This is a &quot;boolean&quot; type.                                                                                                                             
 *                         <td valign="top" align="center">No</td>                                                                                                                           
 *                       </tr>                                                                                                                                                               
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top">success</td>                                                                                                                                     
 *                         <td valign="top">Sets the sucess/failure state for this config event for the ifix identified by <code>iFixID</CODE>.                                              
 *                           This is a &quot;boolean&quot; type.                                                                                                                             
 *                         <td valign="top" align="center">No</td>                                                                                                                           
 *                       </tr>                                                                                                                                                               
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top">resultMessage</td>                                                                                                                               
 *                         <td valign="top">Specifes a message string to be associated with this update event.                                                                               
 *                         <td valign="top" align="center">No</td>                                                                                                                           
 *                       </tr>                                                                                                                                                               
 *                       <tr>                                                                                                                                                                
 *                         <td valign="top">wpHome</td>                                                                                                                                      
 *                         <td valign="top">The WP install root, if not specified the value of <CODE>WpsInstallLocation</CODE> will be used.</td>                                            
 *                         <td valign="top" align="center">No</td>                                                                                                                           
 *                       </tr>                                                                                                                                                               
 *                     </table>                                                                                                                                                              
 *                                                                                                                                                                                           
 *                     <h4>Taskdef</h4>                                                                                                                                                      
 *                     <pre>    &lt;taskdef name=&quot;fixHistoryg&quot;                                                                                                                     
 *         classname=&quot;com.ibm.websphere.product.tasks.WPHistoryEventTask&quot;                                                                                                          
 *         classpath=&quot;/${WpsInstallLocation}/bin/WPProduct.jar&quot;/&gt;                                                                                                               
 *                     </pre>                                                                                                                                                                
 *                                                                                                                                                                                           
 *                                                                                                                                                                                          
 *                     <h4>Examples</h4>                                                                                                                                                     
 *                     <PRE>                                                                                                                                                                 
 *     &lt;target name=&quot;config-pq57287&quot; depends=&quot;check-pq57287&quot; unless=&quot;already-configured-pq57287&quot;&gt;                                                        
 *         &lt;echo&gt;Configuring PQ57287&lt;/echo&gt;                                                                                                                                      
 *         &lt;fixConfig iFixID=&quot;PQ57287&quot; configName=&quot;config-pq57287&quot; configured=&quot;true&quot; configActive=&quot;true&quot;/&gt;                                     
 *         &lt;fixHistory iFixID=&quot;PQ57287&quot; action=&quot;config&quot; actionType=&quot;add&quot; success=&quot;true&quot; resultMessage=&quot;Configured it !!!!&quot;/&gt;        
 *     &lt;/target&gt;                                                                                                                                                                       
 *                                                                                                                                                                                           
 *     &lt;target name=&quot;unconfig-pq57287&quot; depends=&quot;check-pq57287&quot; if=&quot;already-configured-pq57287&quot;&gt;                                                          
 *         &lt;echo&gt;Unconfiguring PQ57287&lt;/echo&gt;                                                                                                                                    
 *         &lt;fixConfig iFixID=&quot;PQ57287&quot; configName=&quot;config-pq57287&quot; configActive=&quot;false&quot;/>                                                                   
 *         &lt;fixHistory iFixID=&quot;PQ57287&quot; action=&quot;unconfig&quot; actionType=&quot;remove&quot; success=&quot;true&quot; resultMessage=&quot;Unconfigured it !!!!&quot;/>     
 *     &lt;/target&gt;                                                                                                                                                                       
 *                                                                                                                                                                                          
 *     &lt;target name=&quot;check-pq57287&quot;>                                                                                                                                            
 *         &lt;fixConfig iFixID=&quot;PQ57287&quot; configName=&quot;config-pq57287&quot; isConfigured=&quot;iFix-configured&quot; isConfigurationActive=&quot;iFix-configActive&quot;/&gt;  
 *         &lt;!-- Depending on how Config/Unconfig will need to work the following condition may change. --&gt;                                                                             
 *         &lt;condition property=&quot;already-configured-pq57287&quot;&gt;                                                                                                                 
 *             &lt;and&gt;                                                                                                                                                                   
 *                &lt;equals arg1=&quot;${iFix-configured}&quot; arg2=&quot;true&quot;/&gt;                                                                                                 
 *                &lt;equals arg1=&quot;${iFix-configActive}&quot; arg2=&quot;true&quot;/&gt;                                                                                                
 *             &lt;/and&gt;                                                                                                                                                                  
 *         &lt;/condition&gt;                                                                                                                                                                
 *     &lt;/target&gt;                                                                                                                                                                       
 *                     </PRE>                                                                                                                                                                
 *  
 * 
 * @author Steven Pritko, IBM
 * @version 5.0
 */
public class WPHistoryEventTask extends Task {

	public static final String pgmVersion = "1.5";
	public static final String pgmUpdate = "3/25/04";

   private static WPHistory history = null;

   private static  void initHistory( String wpHome ) throws BuildException {
      if (history == null) {
         if (wpHome == null || wpHome.trim().length() == 0) {
            throw new BuildException( "WPHistoryTask: ERROR: invalid input. \"WpHome\" property cannot be empty or null." );
         }
         System.setProperty( "wps.install.root", wpHome );
         try {
            history = new WPHistory();
         } catch ( Exception e) {
            throw new BuildException( "WPHistoryTask: ERROR: Unable to initialize WPHistory - " + e.getMessage() );
         }
      }
   }

   private String  home;
   private String  eFixID              = null;
   private String  ptfID               = null;
   private enumUpdateAction  action    = null;
   private enumUpdateType    type      = null;
   private int     success             = 1;
   private String  resultMessage       = null;
   private boolean failonerror         = false;

   /**
    * Default task constructor 
    */
    public WPHistoryEventTask() {
        failonerror = false;
    }

    /**
     * task execute method.  Performs requested operation to the history repository.
     */
    public void execute() throws BuildException {
       if (home == null || home.trim().length() == 0) {
          // Default to property setting.
          home = project.getProperty( "WpsInstallLocation" );
       }
       //System.out.println( this.getClass().getName() + "::execute : Home:" + home  );
       initHistory( home );
       boolean efixSelected    = false;     //check below to prevent both efix and ptf selected
       if ( null != eFixID && eFixID.trim().length() != 0 ) {
           efixSelected = true;
       }
       boolean fixpackSelected = false;     //check below to prevent both efix and ptf selected
       if ( null != ptfID && ptfID.trim().length() != 0 ) {
           fixpackSelected = true;
       }
       //at least one of efixID or ptfID must be spec'd ...
       if ( !efixSelected && !fixpackSelected ){
           throw new BuildException( this.getClass().toString() + " ERROR: invalid input. Either \"eFixID\" or \"ptfID\" must be specified." );
       } 
       //... but not both efix and ptf (for simplicity of doc/use)
       if ( efixSelected && fixpackSelected ){
           throw new BuildException( this.getClass().toString() + " ERROR: invalid input. Specify only one of \"eFixID\" or \"ptfID\" properties." );
       }
       if ( null == action || null == type ) {
           throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"action\",  and \"actionType\" properties cannot be empty or null." );
       }
       updateEvent event = history.getHistory().addUpdateEvent();

       // Fill in event
       if (efixSelected) {
           event.setId( eFixID );
           event.setEventType( "efix" );
       } else {
           event.setId( ptfID );
           event.setEventType( "ptf" );
       }
       //event.setEventType( "config" );
       event.setStartTimeStamp();
       event.setEndTimeStamp();
       event.setUpdateAction( action );
       //event.setUpdateType( type );
       event.setUpdateType( enumUpdateType.selectUpdateType( enumUpdateType.COMPOSITE_VALUE ) );
       event.setResult( enumEventResult.selectEventResult( success ) );
       if ( resultMessage != null ) {
          event.setResultMessage( resultMessage );
       }
       event.setLogName( event.getStandardLogName() );

       event = event.addUpdateEvent();
       if (efixSelected) {
           event.setParentId( eFixID );
       } else {
           event.setParentId( ptfID );
       }
       event.setId( "configtask" );
       event.setEventType( "config" );
       event.setStartTimeStamp();
       event.setEndTimeStamp();
       event.setUpdateAction( action );
       event.setUpdateType( type );
       event.setResult( enumEventResult.selectEventResult( success ) );
       if ( resultMessage != null ) {
          event.setResultMessage( resultMessage );
       }
       event.setLogName( event.getStandardLogName() );

       history.save( false );
    }

    /** 
     * Sets the install root.
     * 
     * @param s Portl install root dir.
     */
    public void setWPHome(String s) {
        home = s;
    }

    public void setFailonerror(boolean flag) {
        failonerror = flag;
    }

    /** 
     * Sets the fixId 
     * 
     * @param s iFix id.
     */
    public void setIfixID(String s) {
        eFixID = s;
    }

    /** 
     * Sets the ptfId 
     * 
     * @param s ptf id.
     */
    public void setPtfID(String s) {
        ptfID = s;
    }

    /** 
     *  The action being perform for this event.  Must be one of
     * { &quot;install&quot;, &quot;uninstall&quot;, &quot;config&quot;, or &quot;unconfig&quot; }
     * 
     * @param s The action event
     */
    public void setAction(String s) {
       // Should check for one of install,uninstall (config/unconfig)
       action = enumUpdateAction.selectUpdateAction( s );
       if ( action == null ) {
          throw new BuildException( this.getClass().toString() + " ERROR: invalid value for action. Must be one of \"install\", \"uninstall\", \"config\", or \"unconfig\"." );
       }
    }
     
    /** 
     *  The action type for this event.  Must be one of
     * { &quot;add&quot;, &quot;replace&quot;, &quot;remove&quot;, or &quot;patch&quot; }
     * 
     * @param s The action type
     */
    public void setActionType(String s) {
       // Should check for one of add,replace,remove,patch
       type = enumUpdateType.selectUpdateType( s );
       if ( type == null ) {
          throw new BuildException( this.getClass().toString() + " ERROR: invalid value for actionType. Must be one of \"add\", \"remove\", \"replace\", or \"patch\"." );
       }
    }

    /** 
     *  Sets the sucess/failure flas for the event
     * 
     * @param s true is the actoin generating this event is a sucessful
     */
    public void setSuccess(boolean b) {
       success = b ? 0 : 1;
    }

    /** 
     *  Additional message associated with this event
     * 
     * @param s result message
     */
    public void setResultMessage( String s ) {
       resultMessage = s;
    }
}