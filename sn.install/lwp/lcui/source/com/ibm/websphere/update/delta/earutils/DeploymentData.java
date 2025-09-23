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

/*
 * Configuration Instance Data: Deployment data for a single
 * configuration instance.
 *
 * History 1.4, 3/12/03
 *
 * 25-Jan-2003 Initial Version
 */

package com.ibm.websphere.update.delta.earutils;

import java.io.*;
import java.util.*;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.xml.sax.*;

/**
 *  
 */
public class DeploymentData
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    public static final boolean
        isDebug = InstallationData.isDebug;

    // Main ...

    public DeploymentData(InstanceData parentInstance, String earName)
    {
        this.parentInstance = parentInstance;

        this.earName = earName;

        this.binariesURL  = null;
        this.metadataFlag = false;
    }

    protected InstanceData parentInstance;

    /**
	 * @return  the parentInstance
	 * @uml.property  name="parentInstance"
	 */
    public InstanceData getParentInstance()
    {
        return parentInstance;
    }

    public String getParentLocation()
    {
        return getParentInstance().getLocation();
    }

    public String getApplicationsPath()
    {
        return getParentInstance().getApplicationsPath();
    }

    // This ear name has '.ear' at the end.

    protected String earName;

    /**
	 * @return  the earName
	 * @uml.property  name="earName"
	 */
    public String getEarName()
    {
        return earName;
    }

    // This usually requires variable substitution,
    // and is usually a part of the deployment location.

    public String binariesURL;

    // Need this to be able to go back and patch the binaries URL
    // with corrected slashes.

    /**
	 * @param binariesURL  the binariesURL to set
	 * @uml.property  name="binariesURL"
	 */
    public void setBinariesURL(String binariesURL)
    {
        this.binariesURL = binariesURL;
    }

    /**
	 * @return  the binariesURL
	 * @uml.property  name="binariesURL"
	 */
    public String getBinariesURL()
    {
        return binariesURL;
    }

    protected boolean metadataFlag;

    /**
	 * @param metadataFlag  the metadataFlag to set
	 * @uml.property  name="metadataFlag"
	 */
    protected void setMetadataFlag(boolean metadataFlag)
    {
        this.metadataFlag = metadataFlag;
    }

    /**
	 * @return  the metadataFlag
	 * @uml.property  name="metadataFlag"
	 */
    public boolean getMetadataFlag()
    {
        return metadataFlag;
    }

    // Debugging ...

    public void display(InstallationData.DisplayCallback displayCallback)
    {
        String nextFlagValue =
            getMetadataFlag() ? trueMetadataValue : falseMetadataValue;

        displayCallback.println("      " +
                                getEarName() + " --> " +
                                getBinariesURL() + " : " +
                                nextFlagValue);
    }

    // Read the deployment info, the binaries path and the alternate
    // metadata flag, using the specified application installation
    // root and the deployment path.
    //
    // The application installation root is as read from the variables
    // configuration file of the primary node of the parent cell of
    // the target EAR, while the deployment path is the path to
    // the deployment.xml for the target ear.
    //
    // Answer null if there is an error retrieving the deployment info.
    //
    // The resulting values are not yet fully expanded using variable
    // substitution.
    //
    // Basic XML parsing may cause an exception to be thrown.
    //
    // Here is a stripped down sample deployment file:
    //
    // <appdeployment:Deployment
    //     xmi:id="Deployment_1">
    // 
    //   <deployedObject
    //       xmi:type="appdeployment:ApplicationDeployment"
    //       xmi:id="ApplicationDeployment_1"
    //       binariesURL="$(APP_INSTALL_ROOT)/CALLY-PLUS/adminconsole.ear"
    //       useMetadataFromBinaries="false">
    // 
    //     <targetMappings
    //         xmi:id="DeploymentTargetMapping_1"
    //         target="ServerTarget_1"/>
    // 
    //   </deployedObject>
    // 
    //   <deploymentTargets
    //       xmi:type="appdeployment:ServerTarget"
    //       xmi:id="ServerTarget_1"
    //       name="server1"
    //       nodeName="CALLY-PLUS"/>
    // 
    // </appdeployment:Deployment>

    public static final String deployedElementTag = "deployedObject" ;
    // Read the deployment info, the binaries path and the alternate
    // metadata flag, using the specified application installation
    // root and the deployment path.
    //
    // The application installation root is as read from the variables
    // configuration file of the primary node of the parent cell of
    // the target EAR, while the deployment path is the path to
    // the deployment.xml for the target ear.
    //
    // Answer null if there is an error retrieving the deployment info.
    //
    // The resulting values are not yet fully expanded using variable
    // substitution.
    //
    // Basic XML parsing may cause an exception to be thrown.
    //
    // Here is a stripped down sample deployment file:
    //
    // <appdeployment:Deployment
    //     xmi:id="Deployment_1">
    // 
    //   <deployedObject
    //       xmi:type="appdeployment:ApplicationDeployment"
    //       xmi:id="ApplicationDeployment_1"
    //       binariesURL="$(APP_INSTALL_ROOT)/CALLY-PLUS/adminconsole.ear"
    //       useMetadataFromBinaries="false">
    // 
    //     <targetMappings
    //         xmi:id="DeploymentTargetMapping_1"
    //         target="ServerTarget_1"/>
    // 
    //   </deployedObject>
    // 
    //   <deploymentTargets
    //       xmi:type="appdeployment:ServerTarget"
    //       xmi:id="ServerTarget_1"
    //       name="server1"
    //       nodeName="CALLY-PLUS"/>
    // 
    // </appdeployment:Deployment>

    public static final String binariesURLTag = "binariesURL" ;
    // Read the deployment info, the binaries path and the alternate
    // metadata flag, using the specified application installation
    // root and the deployment path.
    //
    // The application installation root is as read from the variables
    // configuration file of the primary node of the parent cell of
    // the target EAR, while the deployment path is the path to
    // the deployment.xml for the target ear.
    //
    // Answer null if there is an error retrieving the deployment info.
    //
    // The resulting values are not yet fully expanded using variable
    // substitution.
    //
    // Basic XML parsing may cause an exception to be thrown.
    //
    // Here is a stripped down sample deployment file:
    //
    // <appdeployment:Deployment
    //     xmi:id="Deployment_1">
    // 
    //   <deployedObject
    //       xmi:type="appdeployment:ApplicationDeployment"
    //       xmi:id="ApplicationDeployment_1"
    //       binariesURL="$(APP_INSTALL_ROOT)/CALLY-PLUS/adminconsole.ear"
    //       useMetadataFromBinaries="false">
    // 
    //     <targetMappings
    //         xmi:id="DeploymentTargetMapping_1"
    //         target="ServerTarget_1"/>
    // 
    //   </deployedObject>
    // 
    //   <deploymentTargets
    //       xmi:type="appdeployment:ServerTarget"
    //       xmi:id="ServerTarget_1"
    //       name="server1"
    //       nodeName="CALLY-PLUS"/>
    // 
    // </appdeployment:Deployment>

    public static final String useMetadataTag = "useMetadataFromBinaries" ;
    // Read the deployment info, the binaries path and the alternate
    // metadata flag, using the specified application installation
    // root and the deployment path.
    //
    // The application installation root is as read from the variables
    // configuration file of the primary node of the parent cell of
    // the target EAR, while the deployment path is the path to
    // the deployment.xml for the target ear.
    //
    // Answer null if there is an error retrieving the deployment info.
    //
    // The resulting values are not yet fully expanded using variable
    // substitution.
    //
    // Basic XML parsing may cause an exception to be thrown.
    //
    // Here is a stripped down sample deployment file:
    //
    // <appdeployment:Deployment
    //     xmi:id="Deployment_1">
    // 
    //   <deployedObject
    //       xmi:type="appdeployment:ApplicationDeployment"
    //       xmi:id="ApplicationDeployment_1"
    //       binariesURL="$(APP_INSTALL_ROOT)/CALLY-PLUS/adminconsole.ear"
    //       useMetadataFromBinaries="false">
    // 
    //     <targetMappings
    //         xmi:id="DeploymentTargetMapping_1"
    //         target="ServerTarget_1"/>
    // 
    //   </deployedObject>
    // 
    //   <deploymentTargets
    //       xmi:type="appdeployment:ServerTarget"
    //       xmi:id="ServerTarget_1"
    //       name="server1"
    //       nodeName="CALLY-PLUS"/>
    // 
    // </appdeployment:Deployment>

    public static final String trueMetadataValue = "true" ;
    // Read the deployment info, the binaries path and the alternate
    // metadata flag, using the specified application installation
    // root and the deployment path.
    //
    // The application installation root is as read from the variables
    // configuration file of the primary node of the parent cell of
    // the target EAR, while the deployment path is the path to
    // the deployment.xml for the target ear.
    //
    // Answer null if there is an error retrieving the deployment info.
    //
    // The resulting values are not yet fully expanded using variable
    // substitution.
    //
    // Basic XML parsing may cause an exception to be thrown.
    //
    // Here is a stripped down sample deployment file:
    //
    // <appdeployment:Deployment
    //     xmi:id="Deployment_1">
    // 
    //   <deployedObject
    //       xmi:type="appdeployment:ApplicationDeployment"
    //       xmi:id="ApplicationDeployment_1"
    //       binariesURL="$(APP_INSTALL_ROOT)/CALLY-PLUS/adminconsole.ear"
    //       useMetadataFromBinaries="false">
    // 
    //     <targetMappings
    //         xmi:id="DeploymentTargetMapping_1"
    //         target="ServerTarget_1"/>
    // 
    //   </deployedObject>
    // 
    //   <deploymentTargets
    //       xmi:type="appdeployment:ServerTarget"
    //       xmi:id="ServerTarget_1"
    //       name="server1"
    //       nodeName="CALLY-PLUS"/>
    // 
    // </appdeployment:Deployment>

    public static final String falseMetadataValue = "false" ;

    protected boolean prepare()
        throws IOException, SAXException        
    {
        String useEarName = getEarName();
        String useDeploymentPath = getDeploymentPath();

        if ( isDebug ) {
            System.out.println("Reading deployment info for: " + useEarName +
                               " : " + useDeploymentPath);
        }

        if ( useDeploymentPath == null ) {
            if ( isDebug )
                System.out.println("Unable to determine path to deployment file.");

            return false;
        }

        FileInputStream fis = new FileInputStream(useDeploymentPath);
        InputStreamReader isr = new InputStreamReader(fis, "UTF8");
        BufferedReader bufferedReader = new BufferedReader(isr);
        InputSource inputSource = new InputSource(bufferedReader);
        DOMParser parser = new DOMParser();

        parser.parse(inputSource); // throws SAXException, IOException

        Document doc = parser.getDocument();
        NodeList elements = doc.getElementsByTagName(deployedElementTag);

        if ( elements == null ) {
            if ( isDebug )
                System.out.println("No deployment element");

            return false;
        }

        if ( elements.getLength() != 1 ) {
            if ( isDebug )
                System.out.println("Did not find only one deployment.");

            return false;
        }

        Element deploymentElement = (Element) elements.item(0);

        setBinariesURL( deploymentElement.getAttribute(binariesURLTag) );

        String metadataText = deploymentElement.getAttribute(useMetadataTag);
        setMetadataFlag( metadataText.equalsIgnoreCase(trueMetadataValue) );

        return true;
    }

    // Answer the full path to the deployment file of the specified ear
    // the specified applications directory.
    //
    // For example:
    // 
    //    <configRoot>/cells/<primaryCellName>/
    //        applications/<earName>.ear/
    //            deployments/<earName>/deployment.xml
    //
    // Or,
    //
    //    <applicationsPath>/earName/
    //        deployments/<innerEarName>/deployment.xml
    //
    // Note that the 'earName' argument has the extension '.ear',
    // which is not in the inner ear name.
    //
    // There is only one inner ear name which is expected so simply
    // nab that single file name.  Don't bother reprocessing the
    // argument ear name.

    public static final String deploymentsDir = "deployments" ;
    // Answer the full path to the deployment file of the specified ear
    // the specified applications directory.
    //
    // For example:
    // 
    //    <configRoot>/cells/<primaryCellName>/
    //        applications/<earName>.ear/
    //            deployments/<earName>/deployment.xml
    //
    // Or,
    //
    //    <applicationsPath>/earName/
    //        deployments/<innerEarName>/deployment.xml
    //
    // Note that the 'earName' argument has the extension '.ear',
    // which is not in the inner ear name.
    //
    // There is only one inner ear name which is expected so simply
    // nab that single file name.  Don't bother reprocessing the
    // argument ear name.

    public static final String deploymentFileName = "deployment.xml" ;

    protected boolean setBaseDeploymentPath = false;

    protected String innerEarName;
    protected String baseDeploymentPath;

    /**
	 * @return  the innerEarName
	 * @uml.property  name="innerEarName"
	 */
    public String getInnerEarName()
    {
        if ( !setBaseDeploymentPath ) {
            setBaseDeploymentPath = true;
            assignBaseDeploymentPath();
        }

        return innerEarName;
    }

    /**
	 * @return  the baseDeploymentPath
	 * @uml.property  name="baseDeploymentPath"
	 */
    public String getBaseDeploymentPath()
    {
        if ( !setBaseDeploymentPath ) {
            setBaseDeploymentPath = true;
            assignBaseDeploymentPath();
        }

        return baseDeploymentPath;
    }

    protected void assignBaseDeploymentPath()
    {
        String earPath =
            getApplicationsPath() + File.separator +
            getEarName()          + File.separator +
            deploymentsDir;

        if ( isDebug )
            System.out.println("Path to deployments: " + earPath);

        File dirPath = new File(earPath);
        String[] names = dirPath.list();

        if ( names == null ) {
            if ( isDebug )
                System.out.println("No entries!");

            innerEarName = null;
            baseDeploymentPath = null;

        } else if ( names.length != 1 ) {
            if ( isDebug )
                System.out.println("Found multiple entries.");

            innerEarName = null;
            baseDeploymentPath = null;

        } else {
            innerEarName = names[0];
            baseDeploymentPath = earPath + File.separator + innerEarName;

            if ( isDebug ) {
                System.out.println("Base EAR Name: " + innerEarName);
                System.out.println("Base Deployment path: " + baseDeploymentPath);
            }
        }
    }

    protected boolean setDeploymentPath = false;
    protected String deploymentPath;

    /**
	 * @return  the deploymentPath
	 * @uml.property  name="deploymentPath"
	 */
    public String getDeploymentPath()
    {
        if ( !setDeploymentPath ) {
            setDeploymentPath = true;

            deploymentPath = getBaseDeploymentPath();

            if ( deploymentPath != null )
                deploymentPath += File.separator + deploymentFileName;
        }

        return deploymentPath;
    }

    // For example:
    //    <instanceRoot>/config/cells/<cellName>/
    //        applications/<earName>.ear/<deployments>/<earName>

    public String getAltMetadataPath()
    {
        return getBaseDeploymentPath();
    }
}
