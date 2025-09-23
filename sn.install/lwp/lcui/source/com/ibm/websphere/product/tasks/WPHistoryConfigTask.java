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
import java.io.File;
import java.util.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.*;

// **********************************************************************************************************
// *
// * 
// * File Name, Component Name, Release
// * wps/fix/src/com/ibm/websphere/product/tasks/WPHistoryConfigTask.java, wps.base.fix, wps6.fix
// * 
// * History 1.6, 3/25/04
// *
// * 
// * 
// **********************************************************************************************************/



/**
 * <P> Provide the ability to interact with the ifix/fixpack history repository.    Used in conjunction with the config-task tags in the efix and ptf driver files. <p>  <h4>Parameters</h4> <table border="1" cellpadding="2" cellspacing="0"> <tr> <td valign="top"><b>Attribute</b></td> <td valign="top"><b>Description</b></td> <td align="center" valign="center"><b>Required</b></td> </tr> <tr> <td valign="top">ifixID</td> <td valign="top">Specifies the ifix ID. <td valign="top" align="center">Exactly one of ifixID or ptfID</td>                                                                                                                           </tr> <tr>                                                                                                                                                                 <td valign="top">ptfID</td>                                                                                                                                       <td valign="top">Specifies the ptf ID.                                                                                                                            <td valign="top" align="center">Exactly one of ifixID or ptfID</td>                                                                                                                           </tr>                                                                                                                                                                <tr> <td valign="top">configName</td> <td valign="top">The config task name being configured. <td valign="top" align="center">Yes</td> </tr> <tr> <td valign="top">configActive</td> <td valign="top">Sets the active state of the <CODE>configName</CODE> for the ifix identified by <code>iFixID</CODE>. This is a &quot;boolean&quot; type. <td valign="top" align="center">No</td> </tr> <tr> <td valign="top">configured</td> <td valign="top">Sets the configuration state of the <CODE>configName</CODE> for the ifix identified by <code>iFixID</CODE>. This is a &quot;boolean&quot; type. <td valign="top" align="center">No</td> </tr> <tr> <td valign="top">isConfigActive</td> <td valign="top">This is an output parameter. The value is a property name that will get updated with the current configActive state the <CODE>configName</CODE> for the ifix identified by <code>iFixID</CODE>. The returnw value will be &quot;true&quot; or &quot;false&quot;. <td valign="top" align="center">No</td> </tr> <tr> <td valign="top">isConfigured</td> <td valign="top">This is an output parameter. The value is a property name that will get updated with the current configured state the <CODE>configName</CODE> for the ifix identified by <code>iFixID</CODE>. The returnw value will be &quot;true&quot; or &quot;false&quot;. <td valign="top" align="center">No</td> </tr> <tr> <td valign="top">wpHome</td> <td valign="top">The WP install root, if not specified the value of <CODE>WpsInstallLocation</CODE> will be used.</td> <td valign="top" align="center">No</td> </tr> </table> <h4>Taskdef</h4> <pre>    &lt;taskdef name=&quot;fixConfig&quot; classname=&quot;com.ibm.websphere.product.tasks.WPHistoryConfigTask&quot; classpath=&quot;/${WpsInstallLocation}/bin/WPProduct.jar&quot;/&gt; </pre> <h4>Examples</h4> <PRE> &lt;target name=&quot;config-pq57287&quot; depends=&quot;check-pq57287&quot; unless=&quot;already-configured-pq57287&quot;&gt; &lt;echo&gt;Configuring PQ57287&lt;/echo&gt; &lt;fixConfig iFixID=&quot;PQ57287&quot; configName=&quot;config-pq57287&quot; configured=&quot;true&quot; configActive=&quot;true&quot;/&gt; &lt;fixHistory iFixID=&quot;PQ57287&quot; action=&quot;config&quot; actionType=&quot;add&quot; success=&quot;true&quot; resultMessage=&quot;Configured it !!!!&quot;/&gt; &lt;/target&gt; &lt;target name=&quot;unconfig-pq57287&quot; depends=&quot;check-pq57287&quot; if=&quot;already-configured-pq57287&quot;&gt; &lt;echo&gt;Unconfiguring PQ57287&lt;/echo&gt; &lt;fixConfig iFixID=&quot;PQ57287&quot; configName=&quot;config-pq57287&quot; configActive=&quot;false&quot;/> &lt;fixHistory iFixID=&quot;PQ57287&quot; action=&quot;unconfig&quot; actionType=&quot;remove&quot; success=&quot;true&quot; resultMessage=&quot;Unconfigured it !!!!&quot;/> &lt;/target&gt; &lt;target name=&quot;check-pq57287&quot;> &lt;fixConfig iFixID=&quot;PQ57287&quot; configName=&quot;config-pq57287&quot; isConfigured=&quot;iFix-configured&quot; isConfigurationActive=&quot;iFix-configActive&quot;/&gt; &lt;!-- Depending on how Config/Unconfig will need to work the following condition may change. --&gt; &lt;condition property=&quot;already-configured-pq57287&quot;&gt; &lt;and&gt; &lt;equals arg1=&quot;${iFix-configured}&quot; arg2=&quot;true&quot;/&gt; &lt;equals arg1=&quot;${iFix-configActive}&quot; arg2=&quot;true&quot;/&gt; &lt;/and&gt; &lt;/condition&gt; &lt;/target&gt; </PRE>
 * @author  Steven Pritko, IBM
 * @version  5.0
 */
public class WPHistoryConfigTask extends Task {
	//********************************************************
	//  Program Versioning
	//********************************************************

   /** Constants holding CMVC version information.  This information
    *  is dynamic, changing with each source update. */
	public static final String pgmVersion = "1.6" ;
	//********************************************************
	//  Program Versioning
	//********************************************************

   /** Constants holding CMVC version information.  This information
    *  is dynamic, changing with each source update. */
	public static final String pgmUpdate = "3/25/04" ;

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
   private String  configName          = null;
   private String  configured          = null;
   private String  configurationActive = null;

   private String  isConfiguredProp    = null;
   private String  isConfigActiveProp  = null;
   private boolean failonerror         = false;
   private boolean debug               = false;

   /**
    * Default task constructor 
    */
    public WPHistoryConfigTask() {
        failonerror = false;
        // Default to property setting.
        //System.out.println( this.getClass().getName() + "::WPHistoryConfigTask : " + project );
        //home = project.getProperty( "WpsInstallLocation" );
        //System.out.println( this.getClass().getName() + "::WPHistoryConfigTask : " + home );
    }

    /**
     * task execute method.  Performs requested operaton to the history repository.
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
           debug("efixSelected is: " + efixSelected);
       }
       boolean fixpackSelected = false;     //check below to prevent both efix and ptf selected
       if ( null != ptfID && ptfID.trim().length() != 0 ) {
           fixpackSelected = true;
           debug("fixpackSelected is: " + fixpackSelected);
       }
       //at least one of efixID or ptfID must be spec'd ...
       if ( !efixSelected && !fixpackSelected ){
           throw new BuildException( this.getClass().toString() + " ERROR: invalid input. Either \"eFixID\" or \"ptfID\" must be specified." );
       } 
       //... but not both efix and ptf (for simplicity of doc/use)
       if ( efixSelected && fixpackSelected ){
           throw new BuildException( this.getClass().toString() + " ERROR: invalid input. Specify only one of \"eFixID\" or \"ptfID\" properties." );
       }
       if ( null == configName || configName.trim().length() == 0 ) {
           throw new BuildException( this.getClass().toString() + " ERROR: invalid input. \"configName\" property cannot be empty or null." );
       }

       efixApplied efixApplied = null;
       ptfApplied ptfApplied = null;
       if (efixSelected) {
           efixApplied = history.getEFixAppliedById(eFixID);
           if ( efixApplied == null && efixSelected ) {
               //efix must be applied to update history
               throw new BuildException( this.getClass().toString() + " ERROR: iFix " + eFixID +" is not currently applied." );
           }
       } else {
           ptfApplied = history.getPTFAppliedById(ptfID);
           if ( ptfApplied == null && fixpackSelected ) {
               //ptf must be applied to update history
               throw new BuildException( this.getClass().toString() + " ERROR: PTF " + ptfID +" is not currently applied." );
           }
       }

       boolean save = false;
       configApplied configApplied = null;

       debug("checking configApplied for: " + configName );
       if ( efixSelected ) {
           configApplied = efixApplied.selectConfigApplied( configName );
       } else {
           configApplied = ptfApplied.selectConfigApplied( configName );
       }

       if ( null == configApplied ) {
           debug("no configApplied was found for: " + configName);
       }

       if (configApplied == null) {
          configApplied = new configApplied();
          configApplied.setConfigName( configName );
          if ( efixSelected ) {
              efixApplied.addConfigApplied( configApplied );
          } else {
              ptfApplied.addConfigApplied( configApplied );
          }
       }

       if (configured != null) {
          configApplied.setConfigured( configured );
          save = true;
          debug("configured being set to:" + configured);
       }

       if (configurationActive != null) {
          configApplied.setConfigurationActive( configurationActive );
          save = true;
          debug("configurationActive being set to:" + configurationActive);
       }

       if (save) {
          AppliedWriter writer = new AppliedWriter();
          String fileName = "";

          List appliedList = new ArrayList(1);
          if ( efixSelected ) {
              appliedList.add( efixApplied );
              fileName = history.getHistoryDirName() + File.separator + efixApplied.getStandardFileName();
          } else {
              appliedList.add( ptfApplied );
              fileName = history.getHistoryDirName() + File.separator + ptfApplied.getStandardFileName();
          }
          writer.emit( appliedList, fileName );
          debug("emitted appliedList");
       }
       if ( (isConfiguredProp != null) ) {
           String isConfiguredPropValue = configApplied.isConfigured();
           debug("setting isConfiguredProp to " + isConfiguredPropValue);
           project.setProperty( isConfiguredProp, isConfiguredPropValue );
       }
       if ( (isConfigActiveProp != null) ) {
           String isConfigActivePropValue = configApplied.isConfigurationActive();
           debug("setting isConfigActiveProp to " + isConfigActivePropValue);
           project.setProperty( isConfigActiveProp, isConfigActivePropValue );
       }
    }

    void debug (String s){
        if (debug) {
            System.out.println( this.getClass().toString() + " : " + s );
        }
    }

    /** 
     * Sets the install root.
     * 
     * @param s Portal install root dir.
     */
    public void setWPHome(String wpHome) {
        home = wpHome;
    }

    /**
	 * @param failonerror  the failonerror to set
	 * @uml.property  name="failonerror"
	 */
    public void setFailonerror(boolean flag) {
        failonerror = flag;
    }

    /**
	 * @param debug  the debug to set
	 * @uml.property  name="debug"
	 */
    public void setDebug(boolean flag) {
        debug = flag;
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
	 * Sets the ptfID 
	 * @param s  PTF id.
	 * @uml.property  name="ptfID"
	 */
    public void setPtfID(String s) {
        ptfID = s;
    }

    /**
	 * Sets the config name
	 * @param s  config task name
	 * @uml.property  name="configName"
	 */
    public void setConfigName(String s) {
        configName = s;
    }

    /** 
     * Sets the config active flag
     * 
     * @param s config active state
     */
    public void setConfigActive(String s) {
        configurationActive = s;
    }

    /**
	 * Sets the configured flag
	 * @param s  configured state
	 * @uml.property  name="configured"
	 */
    public void setConfigured(String s) {
        configured = s;
    }

    /** 
     * Queries the configured state
     * 
     * @return current configured state for the iFix/ptf 
     */
    public void setIsConfigured( String s ) {
       isConfiguredProp = s;
    }

    /** 
     * Queries the config active state
     * 
     * @return current config active state for the iFix/ptf 
     */
    public void setIsConfigurationActive( String s ) {
       isConfigActiveProp = s;
    }

}