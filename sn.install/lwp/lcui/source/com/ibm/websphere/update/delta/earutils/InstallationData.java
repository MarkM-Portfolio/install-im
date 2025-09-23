/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2002, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/*********************************************************
 * IBM Confidential OCO Source Material 5639-D57, 5630-A36, 5630-A37,
*/
/*
 * InstallationData -- Information for a single installation.
 *
 * History 1.3, 2/24/03
 *
 * 25-Jan-2003 Initial Version
 *
 * 16-Feb-2003 Added accessor for the distinguished instance.
 */

package com.ibm.websphere.update.delta.earutils;

import java.io.*;
import java.util.*;
import org.xml.sax.*;

/**
 *  
 */
public class InstallationData
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    public static final String debugPropertyName = "com.ibm.websphere.update.delta.earutils.debug" ;
    public static final String debugTrueValue = "true" ;

    public static boolean isDebug;

    static {
        String debugPropertyValue = System.getProperty(debugPropertyName);

        isDebug = ( (debugPropertyValue != null) &&
                    debugPropertyValue.equalsIgnoreCase(debugTrueValue) );
    }

    public static void main(String args[])
    {
        System.out.println("Scanning instance data:");

        if ( args.length == 0 ) {
            System.out.println("No root location was specified.");
            return;
        }

        String targetDir = args[0];

        System.out.println("Scanning target directory: " + targetDir);

        InstallationData data = new InstallationData(targetDir);

        try {
            if ( !data.prepare() ) {  // throws IOException, SAXException
                System.out.println("Failed to retrieve instance data.");
                return;
            }

        } catch ( Exception e ) {
            System.out.println("Exception retrieving instance data");
            e.printStackTrace(System.out);

            return;
        }

        data.display();
    }

    // Main ...

    // Create a new instance of the receiver on the
    // specified installation path.
    //
    // The installation path must be absolutely specified.

    public InstallationData(String installPath)
    {
        this.installPath = installPath;

        this.instances = new HashMap();
    }

    protected String installPath;

    /**
	 * @return  the installPath
	 * @uml.property  name="installPath"
	 */
    public String getInstallPath()
    {
        return installPath;
    }

    protected HashMap instances;

    protected void putInstance(InstanceData instanceData)
    {
        instances.put(instanceData.getNodeName(), instanceData);
    }

    protected InstanceData removeInstance(String nodeName)
    {
        return (InstanceData) instances.remove(nodeName);
    }

    public Iterator getNodeNames()
    {
        return instances.keySet().iterator();
    }

    /**
	 * @return  the instances
	 * @uml.property  name="instances"
	 */
    public Iterator getInstances()
    {
        return instances.values().iterator();
    }

    public InstanceData getInstanceDatum(String nodeName)
    {
        return (InstanceData) instances.get(nodeName);
    }

    protected static final char   slashChar     = '/';
    protected static final String slashText     = "/";

    protected static final char   altSlashChar  = '\\';
    protected static final String altSlashText  = "\\";

    protected static final String fileSlashText = System.getProperty("file.separator");
    protected static final char   fileSlashChar = fileSlashText.charAt(0);

    public String adjustSlashes(String text)
    {
        int useSize = text.length();
        if ( useSize == 0 )
            return text;

        text.replace(fileSlashChar, slashChar);

        char lastChar = text.charAt(useSize - 1);
        if ( lastChar == slashChar )
            text = text.substring(0, useSize - 1);

        return text;
    }

    public InstanceData getDistinguishedInstance()
    {
        String useInstallPath = adjustSlashes( getInstallPath() );

        InstanceData distinguishedInstance = null;
        Iterator useInstances = getInstances();

        while (  (distinguishedInstance == null) && useInstances.hasNext() ) {
            InstanceData nextInstance = (InstanceData) useInstances.next();

            String nextLocation = adjustSlashes( nextInstance.getLocation() );

            if ( nextLocation.equals(useInstallPath) )
                distinguishedInstance = nextInstance;
        }

        return distinguishedInstance;
    }

    // Debugging ...

    public static class DisplayCallback {
        public void println(String text) {
            System.out.println(text);
        }
    }

    public static final DisplayCallback debugCallback = new DisplayCallback();

    public void display()
    {
        display(debugCallback);
    }

    public void display(DisplayCallback displayCallback)
    {
        displayCallback.println("Configuration Instances:");
        displayCallback.println("  Installation Path: " + getInstallPath());

        Iterator useInstances = getInstances();

        while ( useInstances.hasNext() ) {
            InstanceData nextInstanceData = (InstanceData) useInstances.next();
            nextInstanceData.display(displayCallback);
        }
    }

    // Preparation ...

    // Prepare the receiver.
    //
    // Answer true or false, telling if the receiver was
    // successfully prepared.

    public boolean prepare()
        throws IOException, SAXException
    {
        if ( isDebug )
            System.out.println("Locating instances for: " + getInstallPath());

        if ( instanceFileExists() ) {
            if ( isDebug )
                System.out.println("Located instances file; loading.");

            return ( parseInstanceFile() && // throws IOException, SAXException
                     prepareInstances() );  // throws IOException, SAXException

        } else {
            if ( isDebug )
                System.out.println("No instances file; setting default instance.");

            // Cannot store the default instance until after the
            // instance is prepared: The default instance initially
            // has no set node name.

            InstanceData defaultInstance =
                new InstanceData(this, null, getInstallPath());

            if ( !defaultInstance.prepare(InstanceData.IS_DEFAULT_INSTANCE) ) {
                return false;

            } else {
                putInstance(defaultInstance);
                return true;
            }
        }
    }

    protected boolean prepareInstances()
        throws IOException, SAXException
    {
        Iterator useInstances = getInstances();
        Vector failingNodeNames = new Vector();

        while ( useInstances.hasNext() ) {
            InstanceData nextInstanceData = (InstanceData) useInstances.next();

            boolean didPrepare = nextInstanceData.prepare(InstanceData.IS_NOT_DEFAULT_INSTANCE);
                                                  // throws IOException, SAXException

            if ( !didPrepare )
                failingNodeNames.addElement( nextInstanceData.getNodeName() );
        }

        for ( int numFailing = 0; numFailing < failingNodeNames.size(); numFailing++ ) {
            String nextFailingNodeName = (String) failingNodeNames.elementAt(numFailing);

            if ( isDebug )
                System.out.println("Removing failing node: " + nextFailingNodeName);

            removeInstance(nextFailingNodeName);
        }

        if ( !getInstances().hasNext() ) {
            if ( isDebug )
                System.out.println("All nodes were removed.");
            return false;

        } else {
            return true;
        }
    }

    public static final String PROPERTIES_DIR = "properties" ;
    public static final String WS_INSTANCE_FILE = "wsinstance.config" ;

    protected String instanceFileName;

    /**
	 * @return  the instanceFileName
	 * @uml.property  name="instanceFileName"
	 */
    public String getInstanceFileName()
    {
        if ( instanceFileName == null ) {
            instanceFileName =
                getInstallPath() + File.separator +
                PROPERTIES_DIR   + File.separator +
                WS_INSTANCE_FILE;
        }

        return instanceFileName;
    }

    public boolean instanceFileExists()
    {
        File instanceFile = new File( getInstanceFileName() );

        return instanceFile.exists();
    }

    public boolean parseInstanceFile()
        throws IOException
    {
        Properties instanceProperties = new Properties();

        InputStream fs = new FileInputStream( getInstanceFileName() );

        try {
            instanceProperties.load(fs); // throws IOException

        } finally {
            fs.close();
        }

        Iterator nodeNames = instanceProperties.keySet().iterator();

        while ( nodeNames.hasNext() ) {
            String nextNodeName =
                (String) nodeNames.next();
            String nextLocation =
                (String) instanceProperties.getProperty(nextNodeName);

            InstanceData nextInstanceData =
                new InstanceData(this, nextNodeName, nextLocation);

            putInstance(nextInstanceData);
        }

        return true;
    }
}
