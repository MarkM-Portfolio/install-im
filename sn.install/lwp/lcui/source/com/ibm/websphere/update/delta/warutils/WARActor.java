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
package com.ibm.websphere.update.delta.warutils;

import java.io.*;
import java.util.*;
//import java.lang.*;
//import java.net.*;

//import org.xml.sax.*;

//import com.ibm.websphere.update.ptf.*;
import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.delta.earutils.*;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;

/*
 * WARActor
 *     preProcess
 *     postProcess
 *     abstract Vector getCommandData
 * 
 *     "Abstract WAR operation class; responsible for WAR pre and post
 *      processing operations, a part of which is delegated to command
 *      data.  Responsible for providing command data."
 * 
 * UpdateAction
 *     process
 * 
 *     "Superclass for helper operations.  The helper API is expressed
 *      through the single 'process' operation."
 * 
 * WARPreActor  <= UpdateAction
 *     process
 *     abstract WARActor getActor
 * WARPostActor <= UpdateAction
 *     process
 *     abstract WARActor getActor
 * 
 *     "WAR helper classes.  Implement the 'process' operation through
 *      a delegate WAR actor.  Responsible for providing an WAR actor."
 * 
 * ExtendedWARActor <= WARActor
 *     Vector getCommandData
 * 
 *     "Extended WAR helper class; responsible for providing command
 *      data."
 * 
 * ExtendedUpdateAction <= UpdateAction
 *     process(Vector)
 * 
 *     "Extension super class for helper operations.  This extension is
 *      used when arguments are provided to the helper invocation."
 * 
 * ExtendedWARAction    <= ExtendedUpdateAction
 *     process
 *     ExtendedWARActor createActor
 *     abstract basicProcess
 * 
 *     "WAR helper class; implements the helper 'process' operation using
 *      a delegate WAR actor.  Responsible for providing an EAR actor.
 *      Responsible for providing processing details factored through an
 *      EAR actor."
 * 
 * ExtendedWARPostActor <= ExtendedWARAction
 *     basicProcess
 * ExtendedWARPreActor  <= ExtendedWARAction
 *     basicProcess
 * 
 *     "Extension classes for WAR operations.  Responsible for processing
 *      as factored through an WAR actor."
 * 
 */

public abstract class WARActor extends EARActor
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "10/4/05" ;

    public static final String debugPropertyName = "com.ibm.websphere.update.delta.warutils.debug" ;
    public static final String debugTrueValue = "true" ;

    public static boolean isDebug;

    static {
        String isDebugText = System.getProperty(debugPropertyName);

        isDebug = ( (isDebugText != null) &&
                    isDebugText.equalsIgnoreCase(debugTrueValue) );
    }

    // Top of the world: instantiator
    //
    // public WARActor(String, StringBuffer, StringBuffer);

    public WARActor(String installPath, StringBuffer messages, StringBuffer errors)
    {
        super(installPath, messages, errors);

        if ( isDebug )
            System.out.println("Creating WARActor on: " + installPath);
    }

    // Utility classes for WAR command data ...

    public static class WARCommandData
    {
        public String warName;
        public String warInstallPath;

        public boolean deploy;
        public boolean pluginDeploy;

        public boolean installableOnly;
        public boolean applicationOnly;

        public WARCommandData()
        {
            this(null, null, false, false, false, false);
        }

        public WARCommandData(String warName, String warInstallPath, boolean deploy, boolean pluginDeploy)
        {
            this(warName, warInstallPath, deploy, pluginDeploy, false, false);
        }

        public WARCommandData(String warName, String warInstallPath, boolean deploy, boolean pluginDeploy,
                              boolean installableOnly, boolean applicationOnly)
        {
            this.warName = warName;
            this.warInstallPath = warInstallPath;

            this.deploy = deploy;
            this.pluginDeploy = pluginDeploy;

            this.installableOnly = installableOnly;
            this.applicationOnly = applicationOnly;
        }
    }

    // Answer a vector of WARCommand's.
    //
    // This abstract method defines the essential structure that
    // drives the WAR actions.
    //
    // Vector<WARCommandData>
    //
    // public abstract Vector getCommandData();

    public abstract Vector getCommandData();

    // Preprocessing suite:
    //
    // public boolean preProcess();
    // protected boolean preProcess(WARCommandData);
    // protected boolean preProcessWithRule(String, boolean, boolean);
    // protected boolean preProcess(String, boolean, boolean);
    // protected boolean uncompress(String, int);

    // Perform preprocessing steps and tell if these steps were
    // successful.

    
    public boolean preProcess()
    {
        log("Performing WAR pre-processing from: " + getClass().getName());

        if ( !isEnabled() ) {
            log("WAR processing is disabled in the current environment.");
            log("Skipping WAR pre-processing.");

            return true;
        }

        Vector warCommands = getCommandData();
        int numWarCommands = warCommands.size();

        boolean stillOK = true;

        for ( int warNo = 0; stillOK && (warNo < numWarCommands); warNo++ ) {
            WARCommandData nextCommand = (WARCommandData) warCommands.elementAt(warNo);

                stillOK = preProcess(nextCommand);
        }

        return stillOK;
    }

    protected boolean preProcess(WARCommandData command)
    {
        String warName        = command.warName;
        String warInstallPath = command.warInstallPath;

        log("Preprocessing WAR: " + warName);

        boolean installableOnly = command.installableOnly;
        boolean applicationOnly = command.applicationOnly;

        if ( HelperList.HasMacro(HelperList.braceMarkers, warName) )
            return preProcessWithRule(warName, warInstallPath, installableOnly, applicationOnly);
//        else
            return preProcess(warName, warInstallPath, installableOnly, applicationOnly);
    }

    protected boolean preProcessWithRule(String warName,
                                         String warInstallPath,
                                         boolean installableOnly,
                                         boolean applicationOnly)
    {
        log("preProcessWithRule not supported for WARs.");

        boolean stillOK = false;

        return stillOK;
    }

    protected boolean preProcess(String warName,
                                 String warInstallPath,
                                 boolean installableOnly,
                                 boolean applicationOnly)
    {
        log("Performing pre processing from: " + getClass().getName());

        if ( !isEnabled() ) {
            log("WAR processing is disabled in the current environment.");
            log("Skipping WAR pre-processing.");

            return true;
        }

        boolean retval = false;
        String warNameAndPath = warInstallPath + File.separator + warName;
        
        //Use WARCmd to build EAR from the WAR
        //EARCmd(via WARCmd) expects WAS_HOME, not WP_HOME . . .
        WARCmd m_warCmd = new WARCmd( WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ), messages, errors );
        retval = m_warCmd.buildTmpEarFromWar( warNameAndPath );
        
        if ( !retval ) {
            logError( "WARActor::preProcess Failed on building EAR from WAR: " + warName );
            return false;
        }
        
        //expand EAR to tmp dir
        retval = m_warCmd.uncompress( m_warCmd.getFqTmpEarCanonical(), getUncompressedPath(WARCmd.TMP_EAR_NAME, INSTALLABLE_EAR) );
                
        //evaluate result and return
        if ( retval ) {
            log("Preprocessing WAR: " + warName + ": OK");
        } else {
            logError("Preprocessing WAR: " + warName + ": Failed");
        }
        return retval;
    }

    // Post-processing suite:
    //
    // public boolean postProcess();
    // public boolean postProcess(WARCommandData);
    // public boolean postProcess(String,String,boolean,boolean);
    //
    
    public boolean postProcess()
    {
        log("Performing WAR post-processing from: " + getClass().getName());

        if ( !isEnabled() ) {
            log("WAR processing is disabled in the current environment.");
            log("Skipping WAR pre-processing.");

            return true;
        }

        Vector warCommands = getCommandData();
        int numWarCommands = warCommands.size();

        boolean stillOK = true;

        for ( int warNo = 0; stillOK && (warNo < numWarCommands); warNo++ ) {
            WARCommandData nextCommand = (WARCommandData) warCommands.elementAt(warNo);

                stillOK = postProcess(nextCommand);
        }

        return stillOK;
    }

    protected boolean postProcess(WARCommandData command)
    {
        String warName        = command.warName;
        String warInstallPath = command.warInstallPath;

        log("Postprocessing WAR: " + warName);

        boolean installableOnly = command.installableOnly;
        boolean applicationOnly = command.applicationOnly;

        if ( HelperList.HasMacro(HelperList.braceMarkers, warName) )
            return postProcessWithRule(warName, warInstallPath, installableOnly, applicationOnly);
//        else
            return postProcess(warName, warInstallPath, installableOnly, applicationOnly);
    }

    protected boolean postProcessWithRule(String warName,
                                          String warInstallPath,
                                          boolean installableOnly,
                                          boolean applicationOnly)
    {
        log("postProcessWithRule not supported for WARs.");

        boolean stillOK = false;

        return stillOK;
    }


    
    // Perform post processing steps on the specified installation path.
    // Answer true or false, telling if processing was successful.
    // 'installableOnly' and 'applicationOnly' flags are "inherited" 
    // from parent class, but are not referenced here.

    public boolean postProcess(String warName,
            String warInstallPath,
            boolean installableOnly,
            boolean applicationOnly)
    {
        log("Performing post processing from: " + getClass().getName());

        if ( !isEnabled() ) {
            log("WAR processing is disabled in the current environment.");
            log("Skipping WAR post-processing.");

            return true;
        }

        boolean retval = false;
        String warNameAndPath = warInstallPath + File.separator + warName;
        
        //Use WARCmd to build EAR from the tmp EAR directory created during pre-processing
        //EARCmd(via WARCmd) expects WAS_HOME, not WP_HOME . . .
        WARCmd m_warCmd = new WARCmd( WPConfig.getProperty( WPConstants.PROP_WAS_PROD_HOME ), messages, errors );
        
        retval = m_warCmd.deleteTmpEar();
        if ( !retval ) {
            logError( "WARActor::postProcess Failed on deleting EAR from tmp dir" );
            return false;
        }
                
        retval = m_warCmd.compress( m_warCmd.getFqTmpEarCanonical(), getUncompressedPath(WARCmd.TMP_EAR_NAME, INSTALLABLE_EAR) );
        if ( !retval ) {
            logError( "WARActor::postProcess Failed on building EAR from tmp dir: " + getUncompressedPath(WARCmd.TMP_EAR_NAME, INSTALLABLE_EAR) );
            return false;
        }
        
        //extract WAR from tmp EAR
        retval = m_warCmd.extractWarFromTmpEar( warName );
        if ( !retval ) {
            logError( "WARActor::postProcess Failed on extracting updated WAR from tmp EAR: " + warName );
            return false;
        }
        //get canonical path to war file
        String FQ_WAR_CANONICAL = null;
        try {
            FQ_WAR_CANONICAL = (new File(warInstallPath)).getCanonicalPath();
            //protect against null FQ_WAR_CANONICAL
            if ( null == FQ_WAR_CANONICAL ){
                logError( "WARActor::postProcess Failed on resolving canonical path to WAR (null) : '" + m_warCmd.getFqTmpWorkDir() + File.separator + warName );
                return false;
            }
        } catch ( IOException ioe ) {
            logError( "WARActor::postProcess Failed on resolving canonical path to WAR (IOException) : '" + m_warCmd.getFqTmpWorkDir() + File.separator + warName );
            return false;
        }
        //copy new WAR over old
        //copyFile uses shell to copy, so dir on end is OK w/o warfilename
        //copyFile uses shell to copy, so requires canonical arguments
        retval = m_warCmd.copyFile( m_warCmd.getFqTmpWorkDir() + File.separator + warName, FQ_WAR_CANONICAL );
        if ( !retval ) {
            logError( "WARActor::postProcess Failed on copying updated WAR from tmp dir: '" + m_warCmd.getFqTmpWorkDir() + File.separator+ warName + "' to: '" + warInstallPath + "'" );
            return false;
        }
        
        //evaluate result and return
        if ( retval ) {
            log("Postprocessing WAR: " + warName + ": OK");
        } else {
            logError("Postprocessing WAR: " + warName + ": Failed");
        }
        return retval;

    }


    // Tell if WAR processing is enabled.
    //
    // For the moment, WAR processing is always enabled
    protected boolean isEnabled()
    {
        boolean retVal = true;
        
        return retVal;
    }


    protected boolean warIsPresent(String warPath)
    { 
        File warFile = new File(warPath);

        return warFile.exists();
    }

    protected String getInstallableCompressedPath(String earName)
    {        
        String WAS_USER_HOME = WPConfig.getProperty( WPConstants.PROP_WAS_USER_HOME);
        try {
            File f_wasUserHome = new File( WAS_USER_HOME );
            WAS_USER_HOME = f_wasUserHome.getCanonicalPath().toString();
        } catch ( IOException ioe ) {}
        
        return WAS_USER_HOME + File.separator + INSTALLABLE_EAR_TAIL + File.separator + earName;
    }

    protected String getUncompressedPath(String earName, int earType)
    {
        return getWarWorkArea( getEARTmpDir(), getNodeName() ).getAbsolutePath() + File.separator + earName;
    }

    protected String getNodeName() {
        return WPConfig.getProperty( WPConstants.PROP_WAS_NODE);
    }

    public static File getWarWorkArea( String tmpDir ) {
        return getWarWorkArea( tmpDir, null );
     }

     public static File getWarWorkArea( String tmpDir, String nodeName ) {
        return getWarWorkArea( tmpDir, nodeName, INSTALLABLE_EAR_TAIL );
     }

     protected static File getWarWorkArea( String tmpDir, String nodeName, String loc ) {
        File workDir = new File( tmpDir );
        if (nodeName != null ) {
            workDir = new File( workDir, nodeName );
        }
        if ( null != loc ) {
            workDir = new File( workDir, loc );
        }
        workDir.mkdirs();
        return workDir;
     }

    
    
  
}
