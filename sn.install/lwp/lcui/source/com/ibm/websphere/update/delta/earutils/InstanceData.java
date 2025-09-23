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

/*
 * Configuration Instance Data: Data for a single
 * configuration instance.
 *
 * History 1.6, 3/21/03
 *
 * 25-Jan-2003 Initial Version
 *
 * 03-Feb-2003 Added server join.
 */

package com.ibm.websphere.update.delta.earutils;

import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.util.*;
import org.xml.sax.*;

/**
 *  
 */
public class InstanceData
{
   public final static String pgmVersion = "1.4" ;
   public final static String pgmUpdate = "1/29/04" ;

    // Main ...

    public static final boolean isDebug = InstallationData.isDebug;

    public InstanceData(InstallationData parentInstallation,
                        String nodeName, String location)
    {
        this.parentInstallation = parentInstallation;

        this.nodeName = nodeName;
        this.location = location;

        this.cellName = null;

        this.nodeData       = new HashMap();
        this.deploymentData = new HashMap();
    }

    protected InstallationData parentInstallation;

    /**
	 * @return  the parentInstallation
	 * @uml.property  name="parentInstallation"
	 */
    public InstallationData getParentInstallation()
    {
        return parentInstallation;
    }

    protected String nodeName;

    /**
	 * @param nodeName  the nodeName to set
	 * @uml.property  name="nodeName"
	 */
    protected void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    /**
	 * @return  the nodeName
	 * @uml.property  name="nodeName"
	 */
    public String getNodeName()
    {
        return nodeName;
    }

    protected String serverName;

    /**
	 * @param serverName  the serverName to set
	 * @uml.property  name="serverName"
	 */
    protected void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /**
	 * @return  the serverName
	 * @uml.property  name="serverName"
	 */
    public String getServerName()
    {
        return serverName;
    }

    protected NodeData distinguishedNode;

    public NodeData getDistinguishedNodeDatum()
    {
        if ( distinguishedNode == null )
            distinguishedNode = getNodeDatum( getNodeName() );

        return distinguishedNode;
    }

    public String resolve(String unresolvedText)
    {
        return  getDistinguishedNodeDatum().resolve(unresolvedText);
    }


    protected String location;

    // Need this to be able to go back and patch the location
    // with a location with corrected slashes.

    /**
	 * @param location  the location to set
	 * @uml.property  name="location"
	 */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
	 * @return  the location
	 * @uml.property  name="location"
	 */
    public String getLocation()
    {
        return location;
    }

    protected String cellName;

    /**
	 * @param cellName  the cellName to set
	 * @uml.property  name="cellName"
	 */
    protected void setCellName(String cellName)
    {
        this.cellName = cellName;
    }

    /**
	 * @return  the cellName
	 * @uml.property  name="cellName"
	 */
    public String getCellName()
    {
        return cellName;
    }

    protected HashMap nodeData;

    protected void putNodeDatum(NodeData nodeDatum)
    {
        nodeData.put(nodeDatum.getNodeName(), nodeDatum);
    }

    protected NodeData removeNodeDatum(String nodeName)
    {
        return (NodeData) nodeData.remove(nodeName);
    }

    /**
	 * @return  the nodeData
	 * @uml.property  name="nodeData"
	 */
    public Iterator getNodeData()
    {
        return nodeData.values().iterator();
    }

    public Iterator getNodeNames()
    {
        return nodeData.keySet().iterator();
    }

    public NodeData getNodeDatum(String nodeName)
    {
        return (NodeData) nodeData.get(nodeName);
    }

    protected HashMap deploymentData;

    protected void putDeploymentDatum(DeploymentData deploymentDatum)
    {
        deploymentData.put(deploymentDatum.getEarName(), deploymentDatum);
    }

    public HashMap getDeploymentMap()
    {
        return deploymentData;
    }

    /**
	 * @return  the deploymentData
	 * @uml.property  name="deploymentData"
	 */
    public Iterator getDeploymentData()
    {
        return deploymentData.values().iterator();
    }

    public Iterator getEarNames()
    {
        return deploymentData.keySet().iterator();
    }

    public DeploymentData getDeploymentDatum(String earName)
    {
        return (DeploymentData) deploymentData.get(earName);
    }

    // Debugging ...

    public void display(InstallationData.DisplayCallback displayCallback)
    {
        displayCallback.println("  Instance: " + getCellName() + " : " + getNodeName());
        displayCallback.println("  Location: " + getLocation());

        displayCallback.println("");
        displayCallback.println("  Node Data:");

        Iterator useNodeData = getNodeData();
        while ( useNodeData.hasNext() ) {
            NodeData nextNodeData = (NodeData) useNodeData.next();
            nextNodeData.display(displayCallback);
        }

        displayCallback.println("");
        displayCallback.println("  Deployment Data:");

        Iterator useDeploymentData = getDeploymentData();
        while ( useDeploymentData.hasNext() ) {
            DeploymentData nextDeploymentData = (DeploymentData) useDeploymentData.next();
            nextDeploymentData.display(displayCallback);
        }
    }

    protected boolean prepare(boolean isDefault)
        throws IOException, SAXException
    {
        return
            prepareSetup(isDefault) &&  // throws IOException
            prepareNodeData()       &&  // throws IOException, SAXException
            prepareDeployments();       // throws IOException, SAXException
    }

    public static final boolean IS_DEFAULT_INSTANCE = true ;
    public static final boolean IS_NOT_DEFAULT_INSTANCE = false ;


    public boolean prepareSetup(boolean isDefault)
        throws IOException
    {
       // Not sure about isDefault handling here.  May not be somethign we have to worry about.
       String
           useCellName = null,
           useNodeName = null,
           useServerName = null;

       useCellName   = WPConfig.getProperty( WPConstants.PROP_WAS_CELL );
       useNodeName   = WPConfig.getProperty(  WPConstants.PROP_WAS_NODE );
       useServerName = WPConfig.getProperty(  WPConstants.PROP_WP_SERVER );
       
       if (useCellName != null) {
          setCellName( useCellName );
       }
       if (useNodeName != null) {
          setNodeName( useNodeName );
       }
       if (useServerName != null) {
          setServerName( useServerName );
       }
       return ( useCellName != null ) && ( useNodeName != null ) && ( useServerName != null );
    }

    /*
    public boolean prepareSetup(boolean isDefault)
        throws IOException
    {
        if ( isDebug )
            System.out.println("Loading local settings for path: " + getLocation() );

        String useSetupFileName = getSetupFileName();

        if ( isDebug )
            System.out.println("Setup script: " + useSetupFileName);

        String
            useCellName = null,
            useNodeName = null;

        // FileInputStream(String) throws IOException?
        FileInputStream fis = new FileInputStream(useSetupFileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);

        try {
            String nextLine;

            String
                cellTag = CELL_VARIABLE_NAME + "=",
                nodeTag = NODE_VARIABLE_NAME + "=";

            while ( ((useCellName == null) || (useNodeName == null)) &&
                    ((nextLine = bufferedReader.readLine()) != null) ) {
                // readLine() throws IOException?

                if ( useCellName == null ) {
                    int cellTagIndex = nextLine.indexOf(cellTag);

                    if ( cellTagIndex != -1 ) {
                        useCellName = nextLine.substring(cellTagIndex + cellTag.length());
                        useCellName = useCellName.trim();
                    }
                }

                if ( useNodeName == null ) {
                    int nodeTagIndex = nextLine.indexOf(nodeTag);

                    if ( nodeTagIndex != -1 ) {
                        useNodeName = nextLine.substring(nodeTagIndex + nodeTag.length());
                        useNodeName = useNodeName.trim();
                    }
                }
            }

        } finally {
            bufferedReader.close(); // throws IOException?
        }

        boolean cellIsOK;

        if ( useCellName == null ) {
            if ( isDebug )
                System.out.println("Failed to scan cell name.");

            cellIsOK = false;
        } else {
            if ( isDebug )
                System.out.println("Setting cell name from setup: " + useCellName);

            setCellName(useCellName);
            cellIsOK = true;
        }

        boolean nodeIsOK;

        if ( useNodeName == null ) {
            if ( isDebug )
                System.out.println("Failed to scan node name.");

            nodeIsOK = false;

        } else {
            if ( isDefault ) {
                if ( isDebug ) {
                    System.out.println("Preparation for default instance:" +
                                       " Setting node name: " + useNodeName);
                }

                setNodeName(useNodeName);
                nodeIsOK = true;

            } else {
                String instanceNodeName = getNodeName();

                nodeIsOK = useNodeName.equals(instanceNodeName);

                if ( isDebug ) {
                    if ( !nodeIsOK ) {
                        System.out.println("The node name from setup, " + useNodeName + ", " +
                                           " differs from the instance node name, " + useNodeName + ".");
                    } else {
                        System.out.println("Setup node name matches the instance node name: " + useNodeName);
                    }
                }
            }
        }

        return ( cellIsOK && nodeIsOK );
    }
    */

    protected boolean prepareNodeData()
        throws IOException, SAXException
    {
        if ( isDebug )
            System.out.println("Preparing node data:");

        String[] binaryNodeNames = readNodeNames();
        if ( binaryNodeNames == null ) {
           if ( isDebug )
               System.out.println("Failing: No nodes found.");
           return false;
        }

        for (int nameNo = 0; nameNo < binaryNodeNames.length; nameNo++ ) {
            String nextBinaryNodeName = binaryNodeNames[nameNo];

            if ( isDebug )
                System.out.println("  Scanned node: " + nextBinaryNodeName);

            NodeData nextNodeDatum = new NodeData(this, nextBinaryNodeName);

            putNodeDatum(nextNodeDatum);
        }

        Vector failingNodeNames = new Vector();

        Iterator useNodeData = getNodeData();

        while ( useNodeData.hasNext() ) {
            NodeData nextNodeDatum = (NodeData) useNodeData.next();

            if ( !nextNodeDatum.prepare() )  // throws IOException, SAXException
                failingNodeNames.addElement( nextNodeDatum.getNodeName() );
        }

        for ( int failingNodeNo = 0; failingNodeNo < failingNodeNames.size(); failingNodeNo++ ) {
            String nextFailingNodeName = (String) failingNodeNames.elementAt(failingNodeNo);

            if ( isDebug )
                System.out.println("Removing failing node: " + nextFailingNodeName);

            removeNodeDatum(nextFailingNodeName);
        }

        if ( !getNodeData().hasNext() ) {
            if ( isDebug )
                System.out.println("Failing: No nodes remain.");

            return false;
        }

        if ( getDistinguishedNodeDatum() == null ) {
            if ( isDebug )
                System.out.println("Failing: no distinguished node.");

            return false;
        }

        return true;
    }

    protected boolean prepareDeployments()
        //throws IOException, SAXException
    {
        String applicationsPath = getApplicationsPath();

        String[] earNames = getApplicationNames(applicationsPath);
        
        if(earNames == null) return true;

        boolean notedError = false;

        for ( int nameNo = 0; !notedError && (nameNo < earNames.length); nameNo++ ) {
            String nextEarName = earNames[nameNo];

            DeploymentData nextDeploymentData = new DeploymentData(this, nextEarName);

			try{
        	    if ( !nextDeploymentData.prepare() ) { // throws IOException, SAXException
    	            if ( isDebug )
	                    System.out.println("Failed to read deployment info.");

	                notedError = true;

            	} else {
        	        String useBinariesURL = nextDeploymentData.getBinariesURL();
    	            useBinariesURL = resolve(useBinariesURL);
	                nextDeploymentData.setBinariesURL(useBinariesURL);

	                putDeploymentDatum(nextDeploymentData);

                	if ( isDebug )
            	        System.out.println("Read deployment info: " + useBinariesURL);
           	    }
			} catch (IOException ioe) {
				if ( isDebug )
					System.out.println("Noted Exception: " + ioe);
			} catch (SAXException se) {
				if ( isDebug )
					System.out.println("Noted Exception: " + se);
			}
        }

        return !notedError;
    }

    // File utility ...

    // Setup file ...

    // Answer the local settings for the specified configuration.
    //
    // These are loaded from the setupCmdLineFile.  For example,
    // from windows:
    //
    //     SET WAS_HOME=c:\dummyWS\WebSphere\AppServer
    //     SET JAVA_HOME=c:\dummyWS\WebSphere\AppServer\java
    //     SET WAS_CELL=CALLY-PLUS
    //     SET WAS_NODE=CALLY-PLUS
    //
    // null is return if an error occurs.

    public static final String binDir = "bin" ;
    // File utility ...

    // Setup file ...

    // Answer the local settings for the specified configuration.
    //
    // These are loaded from the setupCmdLineFile.  For example,
    // from windows:
    //
    //     SET WAS_HOME=c:\dummyWS\WebSphere\AppServer
    //     SET JAVA_HOME=c:\dummyWS\WebSphere\AppServer\java
    //     SET WAS_CELL=CALLY-PLUS
    //     SET WAS_NODE=CALLY-PLUS
    //
    // null is return if an error occurs.

    public static final String setupCmdLineWindows = "setupCmdLine.bat" ;
    // File utility ...

    // Setup file ...

    // Answer the local settings for the specified configuration.
    //
    // These are loaded from the setupCmdLineFile.  For example,
    // from windows:
    //
    //     SET WAS_HOME=c:\dummyWS\WebSphere\AppServer
    //     SET JAVA_HOME=c:\dummyWS\WebSphere\AppServer\java
    //     SET WAS_CELL=CALLY-PLUS
    //     SET WAS_NODE=CALLY-PLUS
    //
    // null is return if an error occurs.

    public static final String setupCmdLineUnix = "setupCmdLine.sh" ;
    // File utility ...

    // Setup file ...

    // Answer the local settings for the specified configuration.
    //
    // These are loaded from the setupCmdLineFile.  For example,
    // from windows:
    //
    //     SET WAS_HOME=c:\dummyWS\WebSphere\AppServer
    //     SET JAVA_HOME=c:\dummyWS\WebSphere\AppServer\java
    //     SET WAS_CELL=CALLY-PLUS
    //     SET WAS_NODE=CALLY-PLUS
    //
    // null is return if an error occurs.

    public static final String setupCmdLineiSeries = "setupCmdLine" ;

    public static final String CELL_VARIABLE_NAME = "WAS_CELL" ;
    public static final String NODE_VARIABLE_NAME = "WAS_NODE" ;

    public static final int CELL_NAME_OFFSET = 0;
    public static final int NODE_NAME_OFFSET = 1;

    protected String setupFileName;

    /**
	 * @return  the setupFileName
	 * @uml.property  name="setupFileName"
	 */
    public String getSetupFileName()
    {
        if ( setupFileName == null ) {
            if ( PlatformUtils.isWindows() )
                setupFileName = setupCmdLineWindows;
            else if ( PlatformUtils.isISeries() ){
                String wasInstance = WPConfig.getProperty( WPConstants.PROP_WAS_INSTANCE);
                setupFileName = setupCmdLineiSeries + " " + wasInstance;
            }
            else
                setupFileName = setupCmdLineUnix;

            setupFileName =
                getLocation() + File.separator +
                binDir        + File.separator +
                setupFileName;
        }

        return setupFileName;
    }


    // Section on directory naming, and for retrieving
    // file lists.

    // Answer the array of node names under the specified
    // configuration path.
    //
    // These are the names of the directories under:
    //     <configPath>/config/cells/<cellName>
    //
    // For example:
    //     <configPath>/config/cells/<cellName>/node1
    //     <configPath>/config/cells/<cellName>/node2
    //
    // There are no error results from 'getNodeNames'.

    public static final String configDir = "config" ;
    // Section on directory naming, and for retrieving
    // file lists.

    // Answer the array of node names under the specified
    // configuration path.
    //
    // These are the names of the directories under:
    //     <configPath>/config/cells/<cellName>
    //
    // For example:
    //     <configPath>/config/cells/<cellName>/node1
    //     <configPath>/config/cells/<cellName>/node2
    //
    // There are no error results from 'getNodeNames'.

    public static final String cellsDir = "cells" ;
    // Section on directory naming, and for retrieving
    // file lists.

    // Answer the array of node names under the specified
    // configuration path.
    //
    // These are the names of the directories under:
    //     <configPath>/config/cells/<cellName>
    //
    // For example:
    //     <configPath>/config/cells/<cellName>/node1
    //     <configPath>/config/cells/<cellName>/node2
    //
    // There are no error results from 'getNodeNames'.

    public static final String nodesDir = "nodes" ;

    protected String nodesDirName;

    /**
	 * @return  the nodesDirName
	 * @uml.property  name="nodesDirName"
	 */
    public String getNodesDirName()
    {
        if ( nodesDirName == null ) {
            nodesDirName =
                getLocation() + File.separator +
                configDir     + File.separator +
                cellsDir      + File.separator +
                getCellName() + File.separator +
                nodesDir;
        }

        return nodesDirName;
    }

    public String[] readNodeNames()
    {
        File dirPath = new File(getNodesDirName());

        return dirPath.list();
    }

    public static final String applicationsDir = "applications";

    protected String applicationsPath;

    /**
	 * @return  the applicationsPath
	 * @uml.property  name="applicationsPath"
	 */
    public String getApplicationsPath()
    {
        if ( applicationsPath == null ) {
            applicationsPath =
                getLocation() + File.separator +
                configDir     + File.separator +
                cellsDir      + File.separator +
                getCellName() + File.separator +
                applicationsDir;
        }

        return applicationsPath;
    }

    // Answer the names of the applications which are
    // deployed in the specified cell of the specified configuration.
    //
    // These are the names of the directories under:
    //     <configPath>/config/cells/<cellName>/applications

    public String[] getApplicationNames(String applicationsPath) 
    {
        File dirPath = new File(applicationsPath);

        return dirPath.list();
    }

    public static final int JOIN_CELL_NAME_OFFSET = 0 ;
    public static final int JOIN_NODE_NAME_OFFSET = 1 ;
    public static final int JOIN_SERVER_NAME_OFFSET = 2 ;

    protected Vector serverJoin = null; // --> String[]

    /**
	 * @return  the serverJoin
	 * @uml.property  name="serverJoin"
	 */
    public Vector getServerJoin()
    {
        if ( serverJoin == null )
            serverJoin = createServerJoin();

        return serverJoin;
    }

    protected Vector createServerJoin()
    {
        Vector useJoin = new Vector();

        String useCellName = getCellName();

        Iterator useNodeData = getNodeData();
        while ( useNodeData.hasNext() ) {
            NodeData nextNodeData = (NodeData) useNodeData.next();

            String useNodeName = nextNodeData.getNodeName();

            String[] useServerNames = nextNodeData.getServerNames();

            for ( int serverNo = 0; serverNo < useServerNames.length; serverNo++ ) {
                String[] nextJoinData = new String[] {
                    useCellName, useNodeName, useServerNames[serverNo]
                };

                useJoin.addElement(nextJoinData);
            }
        }

        return useJoin;
    }
}
