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

/* @copyright module */

package com.ibm.websphere.update.delta.samplesutils;

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.delta.earutils.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.xml.sax.*;

/**
 *  
 */
public class SamplesAction extends ExtendedUpdateAction
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

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

    public static final int SOFT_ERROR_EXIT_CODE = 1 ;
    // Exit codes for the Pre and Post Actors

    public static final int ERROR_EXIT_CODE = 2 ;

    public static final String lineSeparator;

    static {
        String useSeparator = System.getProperty("line.separator");

        lineSeparator = ( (useSeparator == null) ? "" : useSeparator );
    }

    // Perform processing.

    // Perform processing.  This is delegated to the actor.

    public int process(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errors,
                       boolean debug,
                       Vector args)
    {
        this.installPath = root;
        this.messages = messages;
        this.errors = errors;

        String prefix = "Performing samples processing from: " + getClass().getName() + " ... ";
        log(prefix);

        int numArgs = args.size();
        if ( numArgs == 0 ) {
            logError("No Samples Arguments; failing");
            return ERROR_EXIT_CODE;
        }

        if ( ((numArgs / 2) * 2) != numArgs ) {
            logError("An even number of samples arguments is required; failing:");

            for ( int argNo = 0; argNo < numArgs; argNo++ )
                logError("  [" + argNo + " ]: " + args.elementAt(argNo));

            return ERROR_EXIT_CODE;
        }

        scanArgs(args);

        if ( !scanInstallation() ) {
            log("Error scanning instances; skipping samples processing.");

            // Don't break the PTF installation because of this.
            return SOFT_ERROR_EXIT_CODE;
        }

        if ( setDistinguishedInstance() == null ) {
            log("Located no distinguished instance; skipping samples processing.");

            // Don't break the PTF installation because of this.
            return SOFT_ERROR_EXIT_CODE;
        }

        boolean result = doProcess();

        if ( !result ) {
            log("Failure during samples processing; skipping remaining samples processing.");
            return SOFT_ERROR_EXIT_CODE;

        } else {
            log(prefix + "complete");
            return SUCCESS_EXIT_CODE;
        }
    }

    public boolean doProcess()
    {
        if ( !isEnabled() ) {
            log("Federated node; skipping samples processing.");
            return true;
        }

        Vector useSamplesData = getSamplesData();
        int numSamples = useSamplesData.size();

        boolean stillOK = true;

        for ( int sampleNo = 0; stillOK && (sampleNo < numSamples); sampleNo++ ) {
            SamplesDatum nextSampleDatum =
                (SamplesDatum) useSamplesData.elementAt(sampleNo);

            stillOK = process(nextSampleDatum);
        }

        return stillOK;
    }

    public boolean process(SamplesDatum samplesDatum)
    {
        String useEarDir  = samplesDatum.earDir;
        String useEarName = samplesDatum.earName;

        InstanceData useInstance = getInstanceDatum();
        String useNodeName = useInstance.getNodeName();

        log("Processing sample ...");
        log("  EAR Dir  : " + useEarDir);
        log("  EAR Name : " + useEarName);
        log("  Node Name: " + useNodeName);

        String fullEarName = useEarName + ".ear";

        DeploymentData deployment = useInstance.getDeploymentDatum(fullEarName);

        if ( deployment == null ) {
            log("The sample was not found as a deployed application; skipping.");
            return true;
        }

        SamplesCmd samplesCmd = new SamplesCmd(getInstallPath(), getMessages(), getErrors());

        if ( !samplesCmd.update(useEarDir, useEarName, useNodeName) ) {
            log("Update of the sample failed.");
            return false;
        } else {
            log("Processing sample ... complete");
            return true;
        }

        /*
        if ( !samplesCmd.uninstall(useEarDir, useEarName, useNodeName) ) {
            log("Uninstallation of the sample failed.");
            return false;
        }

        if ( !samplesCmd.install(useEarDir, useEarName, useNodeName) ) {
            log("Installation of the sample failed.");
            return false;
        }
        */
    }

    // Install path access:
    //
    // public String getInstallPath();

    protected String installPath;

    /**
	 * @return  the installPath
	 * @uml.property  name="installPath"
	 */
    public String getInstallPath()
    {
        return this.installPath;
    }

    // Logging:
    //
    // protected StringBuffer messages;
    // public StringBuffer getMessages();
    // public void log(Object arg);
    //
    // protected StringBuffer errors;
    // public StringBuffer getErrors();
    // public void logError(String text);

    protected StringBuffer messages;

    /**
	 * @return  the messages
	 * @uml.property  name="messages"
	 */
    public StringBuffer getMessages()
    {
        return this.messages;
    }

    public void log(Object arg)
    {
        if ( isDebug )
            System.out.println(arg);

        this.getMessages().append(arg + lineSeparator);
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

    // Samples data:
    //
    // protected void scanArgs(Vector);
    // public Vector getSamplesData();

    public static class SamplesDatum
    {
        public String earDir;
        public String earName;

        public SamplesDatum(String earDir, String earName)
        {
            this.earDir = earDir;
            this.earName = earName;
        }
    }

    protected Vector samplesData = null;

    protected void scanArgs(Vector args)
    {
        samplesData = new Vector();

        int numSamples = args.size() / 2;

        int argOffset = 0;

        for ( int sampleNo = 0; sampleNo < numSamples; sampleNo++ ) {
            String nextEarDir  = (String) args.elementAt(argOffset++);
            String nextEarName = (String) args.elementAt(argOffset++);

            SamplesDatum nextDatum = new SamplesDatum(nextEarDir, nextEarName);

            samplesData.addElement(nextDatum);
        }
    }

    /**
	 * @return  the samplesData
	 * @uml.property  name="samplesData"
	 */
    public Vector getSamplesData()
    {
        return samplesData;
    }

    // Per instance state:
    //
    // Processing will iterator across configuration
    // instances.  Store each instance into the object's
    // state.
    //
    // protected boolean scanInstallation();
    //
    // protected InstanceData setDistinguishedInstance();
    // protected InstanceData getInstanceDatum();

    protected InstallationData installationDatum;

    protected boolean scanInstallation()
    {
        installationDatum = new InstallationData( getInstallPath() );

        try {
            installationDatum.prepare(); // throws IOException, SAXException

        } catch ( Exception e ) {
            log("Exception scanning installation data:");
            e.printStackTrace(System.out);

            return false;
        }

        return true;
    }

    /**
	 * @return  the installationDatum
	 * @uml.property  name="installationDatum"
	 */
    protected InstallationData getInstallationDatum()
    {
        return installationDatum;
    }

    protected InstanceData instanceDatum;

    protected InstanceData setDistinguishedInstance()
    {
        instanceDatum = getInstallationDatum().getDistinguishedInstance();

        if ( instanceDatum != null ) {
            log("Selected configuration instance" +
                ": " + instanceDatum.getCellName() +
                " : " + instanceDatum.getNodeName() +
                " at " + instanceDatum.getLocation());
        } else {
            log("Unable to select configuration instance.");
        }

        return instanceDatum;
    }

    /**
	 * @return  the instanceDatum
	 * @uml.property  name="instanceDatum"
	 */
    protected InstanceData getInstanceDatum()
    {
        return instanceDatum;
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
}
