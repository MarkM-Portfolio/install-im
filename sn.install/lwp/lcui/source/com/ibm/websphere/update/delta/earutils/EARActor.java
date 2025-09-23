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
package com.ibm.websphere.update.delta.earutils;

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.ptf.*;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import org.xml.sax.*;

/*
 * EARActor
 *     preProcess
 *     postProcess
 *     abstract Vector getCommandData
 * 
 *     "Abstract EAR operation class; responsible for EAR pre and post
 *      processing operations, a part of which is delegated to command
 *      data.  Responsible for providing command data."
 * 
 * UpdateAction
 *     process
 * 
 *     "Superclass for helper operations.  The helper API is expressed
 *      through the single 'process' operation."
 * 
 * EARPreActor  <= UpdateAction
 *     process
 *     abstract EARActor getActor
 * EARPostActor <= UpdateAction
 *     process
 *     abstract EARActor getActor
 * 
 *     "EAR helper classes.  Implement the 'process' operation through
 *      a delegate EAR actor.  Responsible for providing an EAR actor."
 * 
 * ExtendedEARActor <= EARActor
 *     Vector getCommandData
 * 
 *     "Extended EAR helper class; responsible for providing command
 *      data."
 * 
 * ExtendedUpdateAction <= UpdateAction
 *     process(Vector)
 * 
 *     "Extension super class for helper operations.  This extension is
 *      used when arguments are provided to the helper invocation."
 * 
 * ExtendedEARAction    <= ExtendedUpdateAction
 *     process
 *     ExtendedEARActor createActor
 *     abstract basicProcess
 * 
 *     "EAR helper class; implements the helper 'process' operation using
 *      a delegate EAR actor.  Responsible for providing an EAR actor.
 *      Responsible for providing processing details factored through an
 *      EAR actor."
 * 
 * ExtendedEARPostActor <= ExtendedEARAction
 *     basicProcess
 * ExtendedEARPreActor  <= ExtendedEARAction
 *     basicProcess
 * 
 *     "Extension classes for EAR operations.  Responsible for processing
 *      as factored through an EAR actor."
 * 
 * ExtendedWebuiAction  <= ExtendedEARAction
 *     ExtendedEARActor createActor
 * 
 *     "Extension EAR helper class; overloads the EAR actor factory method
 *      to provide a Webui specific implementation class."
 * 
 * ExtendedWebuiPreActor  <= ExtendedWebuiAction
 *     ExtendedEArActor basicProcess
 * ExtendedWebuiPostActor <= ExtendedWebuiAction
 *     ExtendedEArActor basicProcess
 * 
 *     "Extension classes for EAR operations.  Responsible for processing
 *      as factored through an EAR actor."
 * 
 * WebuiEARActor <= ExtendedEARActor
 *     preProcess
 *     postProcess
 * 
 *     "Extension EAR helper class.  Overrides the pre and post
 *      processing to provide a Webui specific implementation."
 */

/**
 *  
 */
public abstract class EARActor
{
   public final static String pgmVersion = "1.4" ;
   public final static String pgmUpdate = "1/29/04" ;

    public static final String debugPropertyName = "com.ibm.websphere.update.delta.EARActor.debug" ;
    public static final String debugTrueValue = "true" ;

    public static final boolean isDebug;

    static {
        String isDebugText = System.getProperty(debugPropertyName);

        isDebug = ( (isDebugText != null) &&
                    isDebugText.equalsIgnoreCase(debugTrueValue) );
    }

    // Exit codes for the Pre and Post Actors

    public static final int SUCCESS_EXIT_CODE = 0 ;
    // Exit codes for the Pre and Post Actors

    public static final int ERROR_EXIT_CODE = 1 ;

    public static final String lineSeparator = System.getProperty("line.separator");

    // Top of the world: instantiator
    //
    // public EARActor(String, StringBuffer, StringBuffer);

    public EARActor(String installPath, StringBuffer messages, StringBuffer errors)
    {
        this.installPath = installPath;

        if ( isDebug )
            System.out.println("Creating EARActor on: " + installPath);

        this.messages = messages;
        this.errors = errors;

        setEARTmpDir();
    }

    // Base state access:
    //
    // protected String installPath;
    // public String getInstallPath();
    //
    // protected StringBuffer messages;
    // public StringBuffer getMessages();
    // public void log(String text);
    //
    // protected StringBuffer errors;
    // public StringBuffer getErrors();
    // public void logError(String text);

    // Create a new EARActor on the specified installation
    // path, and on the specified messages and errors buffers.
    //
    // The installation path must be absolutely specified.
    //
    // Processing messages will be written to the messages buffer.
    //
    // Processing errors will be written to the errors buffer.

    protected String installPath;

    /**
	 * @return  the installPath
	 * @uml.property  name="installPath"
	 */
    public String getInstallPath()
    {
        return this.installPath;
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
        if ( isDebug )
            System.out.println(text);

        this.getMessages().append(text + lineSeparator);
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
        if ( isDebug )
            System.out.println(text);

        this.getErrors().append(text + lineSeparator);
    }

    // EAR Temporary directory support ...
    //
    // protected String earTmpDir;
    // protected void setEARTmpDir();
    // protected String getEARTmpDir();

    // The caller is responsible for setting an EAR temporary
    // processing directory.

    protected String earTmpDir;

    protected void setEARTmpDir() {
        earTmpDir = System.getProperty( Extractor.earTmpPropertyName );
    }

    protected String getEARTmpDir()
    {
        return earTmpDir;
    }

    // Per instance state:
    //
    // Processing will iterator across configuration
    // instances.  Store each instance into the object's
    // state.
    //
    // protected InstanceData instance;
    //
    // protected void setInstance(InstanceData);
    //
    // protected String getCellName();
    // protected String getNodeName();
    // protected String getInstancePath();
    // protected Vector getServerJoin();
    // protected String getDeployment();
    // protected String getBinariesLocation();

    protected InstanceData instanceData;

    protected void setInstance(InstanceData instanceData)
    {
        this.instanceData = instanceData;

        log("Processing configuration instance" +
            ": " + instanceData.getCellName() + " : " + instanceData.getNodeName() +
            " at " + instanceData.getLocation());
    }

    protected String getCellName()
    {
        return instanceData.getCellName();
    }

    protected String getNodeName()
    {
        return instanceData.getNodeName();
    }

    protected String getServerName()
    {
        return instanceData.getServerName();
    }

    protected String getInstancePath()
    {
        return instanceData.getLocation();
    }

    protected Vector getServerJoin()
    {
        return instanceData.getServerJoin();
    }

    /*
     * getDeploymentData
     * helper function used to return the data sturcture used to represent deployment info
     * param earName: the ear for which we want to return deployment info for
     * returns: a DeploymentData object that holds the deployment information for an ear
     *          (contains info in the ear's deployment.xml)
     * PREREQS: to use this function you must call the setInstance method beforehand, passing
     *          to it an InstanceData object (the InstanceData object represents one
     *          particular configuration instance). 
     */

    protected DeploymentData getDeployment(String earName)
    {
        return instanceData.getDeploymentDatum(earName);
    }

    /*
     * getBinariesLocation
     * helper function used to return the binariesURL attribute in the deployment.xml for an ear
     * param earName: the ear for which we want to return the binariesURL for
     * returns: the binaryURL from the deployment.xml 
     * PREREQS: to use this function you must call the setInstance method beforehand, passing
     *          to it an InstanceData object (the InstanceData object represents one particular
     *          configuration instance).
     * NOTES: the string returned in generally a file name. There is no guarantee that the file
     *        separator characters are consistent in this string. 
     */

    protected String getBinariesLocation(String earName)
    {
        DeploymentData useDeploymentData = getDeployment(earName);

        if ( useDeploymentData == null )
            return null;
        else
            return useDeploymentData.getBinariesURL();
    }

    protected String getCorrectedBinariesLocation(String earName)
    {
        return correctPath( getBinariesLocation(earName) );
    }

    protected static final String slashText    = System.getProperty("file.separator");
    protected static final char   slashChar    = slashText.charAt(0);

    protected static final String urlSlashText = "/";
    protected static final char   urlSlashChar = '/';

    protected String correctPath(String path)
    {
        return ( ( path == null ) ? null : path.replace(urlSlashChar, slashChar) );
    }

    // Utility classes for EAR command data ...

    public static class EARCommandData
    {
        public String earName;

        public boolean deploy;
        public boolean pluginDeploy;

        public boolean installableOnly;
        public boolean applicationOnly;

        public EARCommandData()
        {
            this(null, false, false, false, false);
        }

        public EARCommandData(String earName, boolean deploy, boolean pluginDeploy)
        {
            this(earName, deploy, pluginDeploy, false, false);
        }

        public EARCommandData(String earName, boolean deploy, boolean pluginDeploy,
                              boolean installableOnly, boolean applicationOnly)
        {
            this.earName = earName;

            this.deploy = deploy;
            this.pluginDeploy = pluginDeploy;

            this.installableOnly = installableOnly;
            this.applicationOnly = applicationOnly;
        }
    }

    // Answer a vector of EARCommand's.
    //
    // This abstract method defines the essential structure that
    // drives the EAR actions.
    //
    // Vector<EARCommandData>
    //
    // public abstract Vector getCommandData();

    public abstract Vector getCommandData();

    // Preprocessing suite:
    //
    // public boolean preProcess();
    // protected boolean preProcess(EARCommandData);
    // protected boolean preProcessWithRule(String, boolean, boolean);
    // protected boolean preProcess(String, boolean, boolean);
    // protected boolean uncompress(String, int);

    // Perform preprocessing steps and tell if these steps were
    // successful.

    public boolean preProcess()
    {
        log("Performing EAR pre-processing from: " + getClass().getName());

        if ( !isEnabled() ) {
            log("EAR processing is disabled in the current environment.");
            log("Skipping EAR pre-processing.");

            return true;
        }

        Iterator instances = getInstances();
        if ( instances == null )
            return false;

        Vector earCommands = getCommandData();
        int numEarCommands = earCommands.size();

        boolean stillOK = true;

        while ( instances.hasNext() && stillOK ) {
            InstanceData nextInstance = (InstanceData) instances.next();
            setInstance(nextInstance);

            for ( int earNo = 0; stillOK && (earNo < numEarCommands); earNo++ ) {
                EARCommandData nextCommand =
                    (EARCommandData) earCommands.elementAt(earNo);

                stillOK = preProcess(nextCommand);
            }
        }

        return stillOK;
    }

    protected boolean preProcess(EARCommandData command)
    {
        String earName = command.earName;

        log("Preprocessing EAR: " + earName);

        boolean installableOnly = command.installableOnly;
        boolean applicationOnly = command.applicationOnly;

        if ( HelperList.HasMacro(HelperList.braceMarkers, earName) )
            return preProcessWithRule(earName, installableOnly, applicationOnly);
        else
            return preProcess(earName, installableOnly, applicationOnly);
    }

    protected boolean preProcessWithRule(String earName,
                                         boolean installableOnly,
                                         boolean applicationOnly)
    {
        log("Resolving ear name using cell/node/server substitution.");

        HashMap priorEarNames = new HashMap();

        Vector serverJoin = getServerJoin();
        int numJoins = serverJoin.size();

        boolean stillOK = true;

        for ( int joinNo = 0; stillOK && (joinNo < numJoins); joinNo++ ) {
            String[] nextJoin = (String[]) serverJoin.elementAt(joinNo);
            log("Trying cell: " + nextJoin[HelperList.CELL_NAME_OFFSET] +
                "; node: "      + nextJoin[HelperList.NODE_NAME_OFFSET] +
                "; server: "    + nextJoin[HelperList.SERVER_NAME_OFFSET]);

            String resolvedEarName = HelperList.ResolveMacro(earName, nextJoin);

            if ( priorEarNames.get(resolvedEarName) == null ) {
                log("Resolved new ear name: " + resolvedEarName);
                priorEarNames.put(resolvedEarName, earName);

                stillOK = preProcess(resolvedEarName, installableOnly, applicationOnly);
            } else {
                log("Duplicated resolution: " + resolvedEarName);
            }
        }

        return stillOK;
    }

    protected boolean preProcess(String earName,
                                 boolean installableOnly,
                                 boolean applicationOnly)
    {
        if ( (applicationOnly || uncompress(earName, INSTALLABLE_EAR)) &&
             (installableOnly || uncompress(earName, APPLICATION_EAR)) ) {
            log("Preprocessing EAR: " + earName + ": OK");
            return true;

        } else {
            logError("Preprocessing EAR: " + earName + ": Failed");
            return false;
        }
    }

    protected boolean uncompress(String earName, int earType)
    {
        log("Uncompressing EAR: " + earName);

        String compressedPath   = getCompressedPath(earName, earType);
        String uncompressedPath = getUncompressedPath(earName, earType);

        log("Compressed Path  : " + compressedPath);
        log("Uncompressed Path: " + uncompressedPath);

        if ( !earIsPresent(compressedPath) ) {
            log("EAR is absent; returning");
            return true;

        } else {
            log("EAR is present; continuing");

            // delete the uncompressedPath if exists TBD ???

            if ( performUncompressCommand(compressedPath, uncompressedPath) ) {
                log("Uncompressing EAR: " + earName + ": OK");
                return true;

            } else {
                logError("Uncompressing EAR: " + earName + ": Failed");
                logError("Compressed Path  : " + compressedPath);
                logError("Uncompressed Path: " + uncompressedPath);

                return false;
            }
        }
    } 

    // Post-processing suite:
    //
    // public boolean postProcess(boolean);
    // protected boolean postProcessCompressWithRule(String, boolean, boolean);
    // protected boolean postProcessCompress(String, boolean, boolean);
    // protected boolean postProcessCompress(EARCommandData);
    // protected boolean compress(String, int);

    // Perform post processing steps on the specified installation path.
    // Answer true or false, telling if processing was successful.
    // Deployment may or may not be performed, according the deployment
    // argument.

    public boolean postProcess(boolean doDeploy)
    {
        log("Performing post processing from: " + getClass().getName());

        if ( !isEnabled() ) {
            log("EAR processing is disabled in the current environment.");
            log("Skipping EAR post-processing.");

            return true;
        }

        return basicPostProcess(doDeploy);
    }

    protected boolean basicPostProcess(boolean doDeploy)
    {
        Iterator instances = getInstances();
        if ( instances == null ) {
            log("No instances; failing post-processing.");
            return false;
        }

        if ( !invokePluginProcessor(instances) ) {
            log("Plugin-processor failure; failing post-processing.");
            return false;
        }

        // Make sure to get a new instances iterator; the iterator
        // is used up by plugin-processing.

        if ( !postProcessCompress( getInstances() ) ) {
            log("Compression failure; failing post-processing.");
            return false;

        } else {
            log("Post-processing succeeded.");
            return true;
        }
    }

    /******************** ADMIN GUI PLUGIN PROCESSOR CODE ********************/

    /*
     * pseudocode for the plugin processor invocation: 
     *   for each reference in the filter file  {
     *      if the reference plugin deploy flag is true {
     *          for each configuration instance {
     *              get the binaries URL of the earName assc. with the reference
     *              if the binaries URL is not null {
     *                  run the pluginprocessor -restore command on the instance
     *                  <<copy metadata files to the configuration directory>>
     *              }
     *          }
     *          run the pluginprocessor -restore command for ear in installable
     *          run the pluginprocessor -restore command for ear in application
     *      }
     *  }    
     */

    /*
     * invokePluginProcessor
     *   primary controlling funtion for plugin processor invocation
     *   param instances: an iterator that points to data structure representing
     *                    configuration instances
     *  param earCommands: data structure representing the values given in the 
     *                     filter file (i.e. preprocess, postprocess, etc.)
     */

    public boolean invokePluginProcessor(Iterator instances)
    {
        Vector earCommands = getCommandData();
        int numEarCommands = earCommands.size();

        boolean stillOK = true;

        for ( int count = 0; stillOK && (count < numEarCommands); count++ ) {
            EARCommandData command = (EARCommandData) earCommands.elementAt(count); 

            if ( command.pluginDeploy ) {

                while ( stillOK && instances.hasNext() ) {
                    InstanceData nextInstance = (InstanceData) instances.next();
                    setInstance(nextInstance); 

                    stillOK =
                        invokePPOnInstalled  (command.earName) &&
                        invokePPOnInstallable(command.earName) &&
                        invokePPOnApplication(command.earName);
                }
            }
        }

        return stillOK;
    }

    /*
     * invokePPOnConfigurationInstances
     * invokes the plugin processor on the installable copy of the ear on all config instances 
     * param instances: an iterator that points to data structure representing 
     *                  configuration instances
     * earName: the ear name in the installedApps directory that we are concerned with
     */

    public boolean invokePPOnInstalled(String earName)
    {
        String wasPlprRoot = getBinariesLocation(earName);

        if ( wasPlprRoot == null ) {
            log("No deployment found; skipping plugin processor invocation.");
            return true;

        } else {
            log("Noted deployment.");
            return callPluginProcessor(wasPlprRoot); 
        }
    }

    /*
     * invokePPOnConfigurationInstances
     * invokes the plugin processor on the installable copy of the ear
     * earName: the ear name in the installableApps directory that we are concerned with
     */

    public boolean invokePPOnInstallable(String earName)
    {
        String uncompressedPath = getUncompressedPath(earName, INSTALLABLE_EAR);

        if ( !earIsPresent(uncompressedPath) ) {
            log("No temporary uncompressed EAR; skipping plugin processor invocation.");
            return true;

        } else {
            log("Noted temporary uncompressed EAR.");
            return callPluginProcessor(uncompressedPath); 
        }
    }

    /*
     * invokePPOnConfigurationInstances
     * invokes the plugin processor on the applications copy of the ear
     * earName: the ear name in the applications directory that we are concerned with
     */
    
    public boolean invokePPOnApplication(String earName)
    {
        String uncompressedPath = getUncompressedPath(earName, APPLICATION_EAR);

        if ( !earIsPresent(uncompressedPath) ) {
            log("No temporary uncompressed EAR; skipping plugin processor invocation.");
            return true;

        } else {
            log("Noted temporary uncompressed EAR.");
            return callPluginProcessor(uncompressedPath);
        }
    }

    /*
     * callPluginProcessor
     * this function does the work associated with invoking the plugin processor
     * param wasPlprRoot: the location of the ear file (adminconsole.ear) that 
     *                    we want to invoke the plugin processor on. This param 
     *                    is parsed and passed as an environment variable to the actual 
     *                    invocation of the plugin processor
     * PREREQ: The wasPlprRoot cannot be null.
     */

    // wasPlprRoot will be one of:
    // The ear path in the installable applications directory,
    // the ear path in the applications directory,
    // the ear path in the installed applications directory
    //
    // In all three cases the ear path includes the name of the
    // ear.

    public boolean callPluginProcessor(String wasPlprRoot)
    {
        EARCmd earCmd = createEARCmd();
        return earCmd.webuiPluginProcess(wasPlprRoot);
    }
 
    /******************** ADMIN GUI PLUGIN PROCESSOR CODE ********************/

    protected boolean postProcessCompress(Iterator instances)
    {
        boolean result = true;

        Vector earCommands = getCommandData();
        int numEarCommands = earCommands.size();

        while ( instances.hasNext() ) {
            InstanceData nextInstance = (InstanceData) instances.next();
            setInstance(nextInstance);

            for ( int earNo = 0; earNo < numEarCommands; earNo++ ) {
                EARCommandData nextCommand =
                    (EARCommandData) earCommands.elementAt(earNo);

                if ( !postProcessCompress(nextCommand) )
                    result = false;
            }
        }

        return result;
    }

    protected boolean postProcessCompress(EARCommandData command)
    {
        String earName = command.earName;

        log("Post-processing (compress) EAR: " + earName);

        boolean installableOnly = command.installableOnly;
        boolean applicationOnly = command.applicationOnly;

        if ( HelperList.HasMacro(HelperList.braceMarkers, earName) )
            return postProcessCompressWithRule(earName, installableOnly, applicationOnly);
        else
            return postProcessCompress(earName, installableOnly, applicationOnly);
    }

    protected boolean postProcessCompressWithRule(String earName,
                                                  boolean installableOnly,
                                                  boolean applicationOnly)
    {
        log("Resolving ear name using cell/node/server substitution.");

        HashMap priorEarNames = new HashMap();

        Vector serverJoin = getServerJoin();
        int numJoins = serverJoin.size();

        boolean stillOK = true;

        for ( int joinNo = 0; stillOK && (joinNo < numJoins); joinNo++ ) {
            String[] nextJoin = (String[]) serverJoin.elementAt(joinNo);
            log("Trying cell: " + nextJoin[HelperList.CELL_NAME_OFFSET] +
                "; node: "      + nextJoin[HelperList.NODE_NAME_OFFSET] +
                "; server: "    + nextJoin[HelperList.SERVER_NAME_OFFSET]);

            String resolvedEarName = HelperList.ResolveMacro(earName, nextJoin);

            if ( priorEarNames.get(resolvedEarName) == null ) {
                log("Resolved new ear name: " + resolvedEarName);
                priorEarNames.put(resolvedEarName, earName);

                stillOK = postProcessCompress(resolvedEarName, installableOnly, applicationOnly);

            } else {
                log("Duplicated resolution: " + resolvedEarName);
            }
        }

        return stillOK;
    }

    protected boolean postProcessCompress(String earName,
                                          boolean installableOnly,
                                          boolean applicationOnly)
    {
        log("Post-processing EAR (compress): " + earName);

        boolean stillOK = true;

        if ( !applicationOnly ) {
            log("Post-processing installable EAR");

            if ( !compress(earName, INSTALLABLE_EAR) ) {
                logError("Post-processing installable EAR (compress): " + earName + ": Failed");
                stillOK = false;
            } else {
                log("Post-processing installable EAR: OK");
            }
        }

        if ( !installableOnly ) {
            log("Post-processing application EAR");

            if ( !compress(earName, APPLICATION_EAR) ) {
                logError("Post-processing application EAR (compress): " + earName + ": Failed");
                stillOK = false;
            } else {
                log("Post-processing application EAR: OK");
            }
        }

        if ( stillOK ) {
            log("Post-processing EAR (compress): " + earName + ": OK");
            return true;
        } else {
            logError("Post-processing EAR (compress): " + earName + ": Failed");
            return false;
        }
    }

    protected boolean compress(String earName, int earType)
    {
        log("Compressing EAR: " + earName);

        String uncompressedPath = getUncompressedPath(earName, earType);
        String compressedPath   = getCompressedPath(earName, earType);

        log("Uncompressed Path: " + uncompressedPath);
        log("Compressed Path  : " + compressedPath);

        if ( !earIsPresent(compressedPath) ) {
            log("No original compressed EAR; skipping compression.");
            return true;
        } else {
            log("Noted original compressed EAR.");
        }

        if ( !earIsPresent(uncompressedPath) ) {
            log("No temporary uncompressed EAR; skipping compression.");
            return true;
        } else {
            log("Noted temporary uncompressed EAR.");
        }

        boolean stillOK = true;

        log("Removing original compressed EAR");

        if ( !removeFully(compressedPath) ) {
            logError("Unable to remove original compressed EAR: " + compressedPath);
            stillOK = false;
        } else {
            log("Removing original compressed EAR: OK");
        }

        if ( stillOK ) {
            log("Compressing EAR");

            if ( !performCompressCommand(compressedPath, uncompressedPath) ) {
                logError("Compressing EAR: " + earName + ": Failed");
                logError("Uncompressed Path: " + uncompressedPath);
                logError("Compressed Path  : " + compressedPath);
                stillOK = false;
            } else {
                log("Compressing EAR: OK");
            }
        }

        log("Removing temporary uncompressed EAR");

        if ( !removeFully(uncompressedPath) ) {
            log("Unable to remove temporary uncompressed EAR: " + uncompressedPath);

            // logError("Unable to remove temporary uncompressed EAR: " + uncompressedPath);
            // stillOK = false;

        } else {
            log("Removing temporary uncompressed EAR: OK");
        }

        return stillOK;
    }

    // Processing enablement helpers ...
    //
    // protected boolean isEnabled()
    //
    // protected ProductInterrogator productInterrogator;
    // protected ProductInterrogator getProductInterrogator();
    // protected ProductInterrogator createProductInterrogator();

    // protected boolean isEnabled()
    // protected ProductInterrogator getProductInterrogator();

    // Tell if EAR processing is enabled.
    //
    // EAR processing is disabled when the product is
    // federated, that is, when both a BASE and an ND
    // product are present.

    protected boolean isEnabled()
    {
        ProductInterrogator useProductInterrogator =
            getProductInterrogator();

        if ( useProductInterrogator.isEmbedded() )
            return false;

        return ( !(useProductInterrogator.isBase() &&
                   useProductInterrogator.isND()) );
    }

    protected ProductInterrogator productInterrogator;

    // Answer the receiver's product interrogator.

    /**
	 * @return  the productInterrogator
	 * @uml.property  name="productInterrogator"
	 */
    protected ProductInterrogator getProductInterrogator()
    {
        if ( productInterrogator == null )
            productInterrogator = createProductInterrogator();

        return productInterrogator;
    }

    // Create a product interrogator for the receiver.

    protected ProductInterrogator createProductInterrogator()
    {
        return new ProductInterrogator( getInstallPath() );
    }

    // Configuration instance helpers ...
    //
    // protected Iterator getInstances();

    protected Iterator getInstances()
    {
        InstallationData useInstallation;

        try {
            useInstallation = getInstallationData(); // throws IOException, SAXException

        } catch ( IOException e ) {
            logError("Exception retrieving installation data: " + e);
            return null;

        } catch ( SAXException e ) {
            logError("Exception retrieving installation data: " + e);
            return null;
        }

        if ( useInstallation == null ) {
            logError("Failed to retrieve configuration instance data.");
            return null;
        }

        return useInstallation.getInstances();
    }

    protected boolean setInstallation = false;
    protected InstallationData installation = null;

    protected InstallationData getInstallationData()
        throws IOException, SAXException
    { 
        if ( !setInstallation ) {
            setInstallation = true;

            InstallationData newInstallation =
                new InstallationData( getInstallPath() );

            newInstallation.prepare(); // throws IOException, SAXException

            installation = newInstallation;
        }

        return installation;
    }

    // EAR location utilities ...
    //
    // protected boolean earIsPresent(String);
    //
    // protected String getUncompressedTail(int)
    //
    // protected String getCompressedPath(String, int)
    // protected String getInstallableCompressedPath(String)
    // protected String getApplicationsCompressedPath(String)
    //
    // protected String getUncompressedPath(String, int)

    // Answer true or false telling if the named EAR is present
    // in the named configuration instance.
    
    protected boolean earIsPresent(String earPath)
    { 
        File earFile = new File(earPath);

        return earFile.exists();
    }

    // EAR path resolvers:
    //
    // Need:
    //     compressed path for an ear
    //         by ear name
    //         by type (installable, application)
    //
    //     uncompressed path for an ear
    //         by ear name
    //         by type (installable, application)
    //
    // Four patterns:
    //     compressed, installable: <configPath>/installableApps/<earName>
    //     compressed, application: <configPath>/config/cells/<cellName>/applications/<earName>/<earName>
    //
    //     uncompressed, installable: <earTmp>/installable/<nodeName>/<earName>
    //     uncompressed, application: <earTmp>/applications/<nodeName>/<earName>

    protected static final int INSTALLABLE_EAR = 0 ;
    // EAR path resolvers:
    //
    // Need:
    //     compressed path for an ear
    //         by ear name
    //         by type (installable, application)
    //
    //     uncompressed path for an ear
    //         by ear name
    //         by type (installable, application)
    //
    // Four patterns:
    //     compressed, installable: <configPath>/installableApps/<earName>
    //     compressed, application: <configPath>/config/cells/<cellName>/applications/<earName>/<earName>
    //
    //     uncompressed, installable: <earTmp>/installable/<nodeName>/<earName>
    //     uncompressed, application: <earTmp>/applications/<nodeName>/<earName>

    protected static final int APPLICATION_EAR = 1 ;

    public static final String INSTALLABLE_EAR_TAIL = "installableApps" ;
    public static final String APPLICATION_EAR_TAIL = "applications" ;

    public static final String TMP_INSTALLABLE_EAR_TAIL = "installable" ;
    // HelperList.earInstallableDir,
 public static final String TMP_APPLICATION_EAR_TAIL = "applications" ;

    protected String getUncompressedTail(int earType)
    {
        if ( earType == INSTALLABLE_EAR )
            return TMP_INSTALLABLE_EAR_TAIL;
        else
            return TMP_APPLICATION_EAR_TAIL;
    }

    protected String getCompressedPath(String earName, int earType)
    {
        if ( earType == INSTALLABLE_EAR )
            return getInstallableCompressedPath(earName);
        else
            return getApplicationsCompressedPath(earName);
    }

    protected String getInstallableCompressedPath(String earName)
    {
        return getInstancePath()    + File.separator +
               INSTALLABLE_EAR_TAIL + File.separator + earName;
    }

    public static final String CONFIG_DIR = "config" ;
    public static final String CELLS_DIR = "cells" ;

    protected String getApplicationsCompressedPath(String earName)
    {
        return getInstancePath()    + File.separator +
               CONFIG_DIR           + File.separator +
               CELLS_DIR            + File.separator +
               getCellName()        + File.separator +
               APPLICATION_EAR_TAIL + File.separator +
               earName              + File.separator + earName;
    }

    protected String getUncompressedPath(String earName, int earType)
    {
        return getEARTmpDir()               + File.separator +
               getNodeName()                + File.separator +
               getUncompressedTail(earType) + File.separator + earName;
    }

    // Utilities for file removal:
    //
    // protected boolean removeFully(String);
    // protected boolean removeFully(File);
    // protected File basicRemoveFully(File);

    // Fully remove the files along the specified path.
    // Answer true or false, telling the removal was successful.
    //
    // The argument path must be a full path.

    protected boolean removeFully(String path)
    {
        return removeFully( new File(path) );
    }

    // Fully the file or files.  Answer true or false,
    //  telling the removal was successful.

    protected boolean removeFully(File file)
    {
        log("Removing: " + file.getAbsolutePath());

        File failing = basicRemoveFully(file);

        if ( failing == null ) {
            log("Removal was successful.");
            return true;

        } else {
            log("Failed to remove: " + failing.getAbsolutePath());
            // logError("Failed to remove: " + failing.getAbsolutePath());

            return false;
        }
    }

    // Fully remove the file or files.  Answer null
    // if the removal was successful.  Otherwise,
    // answer the file which could not be removed.

    protected File basicRemoveFully(File file)
    {
        if ( !file.exists() )
            return null;

        if ( file.isDirectory() ) {
            File failing = null;
            boolean retcode = false;

            File[] children = file.listFiles();
            for ( int fileNo = 0; (failing == null) && (fileNo < children.length); fileNo++ ) {
                failing = children[fileNo];
                retcode = removeFully(children[fileNo]);
                if ( retcode == false )
                    return failing;
            }
        }

        if ( !file.delete() ) {
            return file;

        } else {
            log("Removed: " + file.getAbsolutePath());
            return null;
        }
    }

    /*
    // Java path helper ...

    public static final String JAVA_PROPERTY_NAME = "update.java.path";

    // Answer the set java path.

    protected String getJavaPath()
    {
        return System.getProperty(JAVA_PROPERTY_NAME);
    }
    */

    // Raw EAR command access ...

    protected EARCmd createEARCmd()
    {
        return new EARCmd( getInstallPath(), getMessages(), getErrors() );
    }

    // Perform an UNJAR using the path data.  Answer true
    // or false, telling if the UNJAR was successful.

    protected boolean performUncompressCommand(String compressedPath,
                                               String uncompressedPath)
    {
        EARCmd earCmd = createEARCmd();

        return earCmd.uncompress(compressedPath, uncompressedPath);
    }

    // Perform an JAR using the path data.  Answer true
    // or false, telling if the JAR was successful.

    protected boolean performCompressCommand(String compressedPath,
                                             String uncompressedPath)
    { 
        EARCmd earCmd = createEARCmd();

        return earCmd.compress(compressedPath, uncompressedPath);
    }

    // Perform a file copy operation.

    protected boolean performCopyCommand(String sourceFile, String targetFile)
    {
        EARCmd earCmd = createEARCmd();

        return earCmd.copyFile(sourceFile, targetFile);
    }

    protected boolean performDeleteCommand(String targetFile)
    {
        EARCmd earCmd = createEARCmd();

        return earCmd.deleteFile(targetFile);
    }
}
