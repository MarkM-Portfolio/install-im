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
 * Configuration Instance Node Data: Data for a single node.
 *
 * History 1.4, 3/12/03
 *
 * 25-Jan-2003 Initial Version
 *
 * 24-Feb-2003 Made variables handling more robust.
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
public class NodeData
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    public static final boolean isDebug = InstallationData.isDebug;

    // Main ...

    public NodeData(InstanceData parentInstance, String nodeName)
    {
        this.parentInstance = parentInstance;

        this.nodeName = nodeName;

        this.binaryLocation = null;
        this.serverNames    = null;
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

    public String getNodesDirName()
    {
        return getParentInstance().getNodesDirName();
    }

    protected String nodeName;

    /**
	 * @return  the nodeName
	 * @uml.property  name="nodeName"
	 */
    public String getNodeName()
    {
        return nodeName;
    }

    protected String binaryLocation;

    // Need this so to be able to correct the binary location
    // with one with the right slashes.

    /**
	 * @param binaryLocation  the binaryLocation to set
	 * @uml.property  name="binaryLocation"
	 */
    public void setBinaryLocation(String binaryLocation)
    {
        this.binaryLocation = binaryLocation;
    }

    /**
	 * @return  the binaryLocation
	 * @uml.property  name="binaryLocation"
	 */
    public String getBinaryLocation()
    {
        return binaryLocation;
    }

    protected String[] serverNames;

    /**
	 * @return  the serverNames
	 * @uml.property  name="serverNames"
	 */
    public String[] getServerNames()
    {
        return serverNames;
    }

    /**
	 * @param serverNames  the serverNames to set
	 * @uml.property  name="serverNames"
	 */
    protected void setServerNames(String[] serverNames)
    {
        this.serverNames = serverNames;
    }

    // Debugging ...

    public void display(InstallationData.DisplayCallback displayCallback)
    {
        displayCallback.println("      " + getNodeName() + " --> " + getBinaryLocation());

        String[] useServerNames = getServerNames();

        for ( int serverNo = 0; serverNo < useServerNames.length; serverNo++ ) {
            String nextServerName = serverNames[serverNo];
            displayCallback.println("         [ " + serverNo + " ]: " + nextServerName);
        }
    }

    // Preparation ...

    protected boolean prepare()
        throws IOException, SAXException
    {
        if ( isDebug )
            System.out.println("Preparing node datum:");

        return
            prepareVariables()      &&  // throws IOException, SAXException
            prepareBinaryLocation() &&
            prepareServerNames();
    }

    // Answer the location of binary files for the specified configuration.
    // The cell and node node have been previously determined.
    //
    // The configuration path must be absolutely specified.
    //
    // The binary files location is absolutely specified.
    //
    // This refers to the APP_INSTALL_ROOT in variables.xml;
    // variables.xml is expected in cells\<cellName>\node\<nodeName>.

    // Answer the variables of the specified cell and node in the
    // specified configuration path.
    //
    // A variables file is parsed, an error during which may throw
    // an exception.

    public static final String
        variablesElementName = "entries";

    protected HashMap variables;

    protected boolean prepareVariables()
        throws IOException, SAXException
    {
        String useVarFileName = getVariablesPath();

        File useVarFile = new File(useVarFileName);

        if ( !useVarFile.exists() ) {
            variables = new HashMap();

            if ( isDebug )
                System.out.println("No variables file!");

            return false;
        }

        if ( isDebug )
            System.out.println("Preparing node variables: " + useVarFileName);

        DOMParser parser = new DOMParser();

        // InputSource inputSource = new InputSource(new FileReader(useVarFile));
        FileInputStream fis = new FileInputStream(useVarFile);
        InputStreamReader isr = new InputStreamReader(fis, "UTF8");
        BufferedReader bufferedReader = new BufferedReader(isr);
        InputSource inputSource = new InputSource(bufferedReader);
        parser.parse(inputSource); // throws SAXException, IOException

        Document doc = parser.getDocument();
        NodeList elements = doc.getElementsByTagName(variablesElementName);

        if ( elements == null ) {
            if ( isDebug )
                System.out.println("No variables defined");

            return false;
        }

        variables = new HashMap();

        for ( int elementNo = 0; elementNo < elements.getLength() ; elementNo++ ) {
            Element nextElement = (Element) elements.item(elementNo);

            String nextName = nextElement.getAttribute("symbolicName");
            String nextValue = nextElement.getAttribute("value");

            variables.put(nextName, nextValue);
        }

        return true;
    }

    /**
	 * @return  the variables
	 * @uml.property  name="variables"
	 */
    public HashMap getVariables()
    {
        return variables;
    }

    public String getVariable(String variableName)
    {
        return (String) getVariables().get(variableName);
    }

    // Perform substituion on the argument value text, using substitutions
    // from the specified table.
    //
    // Answer the resolved value.
    //
    // This method does not guard against recursive definitions.

    public String resolve(String valueText)
    {
        int start = valueText.indexOf("${");
        int end;
        if ( start != -1 ) {
            end = valueText.indexOf("}");
        } else {
            start = valueText.indexOf("$(");
            if ( start != -1 )
                end = valueText.indexOf(")");
            else
                end = -1;
        }

        while ( (start != -1) && (end != -1) ) {
            String head = valueText.substring(0, start);
            String tail = valueText.substring(end + 1);

            String substitutionName = valueText.substring(start + 2, end);
            String substitutionValue = getVariable(substitutionName);

            if ( substitutionValue == null )
                substitutionValue = "";

            valueText = head + substitutionValue + tail;

            start = valueText.indexOf("${");
            if ( start != -1 ) {
                end = valueText.indexOf("}");
            } else {
                start = valueText.indexOf("$(");
                if ( start != -1 )
                    end = valueText.indexOf(")");
                else
                    end = -1;
            }
        }

        return valueText;
    }

    // File the binary files location using values from the specified
    // table.
    //
    // Answer true or false, telling if the location was successfully
    // resolved.

    public static final String
        APP_INSTALL_ROOT   = "APP_INSTALL_ROOT";

    protected boolean prepareBinaryLocation()
    {
        String locationString = (String) getVariable(APP_INSTALL_ROOT);

        if ( locationString == null ) {
            if ( isDebug )
                System.out.println("No APP_INSTALL_ROOT variable.");

            return false;

        } else {
            if ( isDebug )
                System.out.println("APP_INSTALL_ROOT value: " + locationString);

            locationString = locationString.trim();

            setBinaryLocation( resolve(locationString) );

            return true;
        }
    }

    protected boolean prepareServerNames()
    {
        setServerNames( readServerNames() );

        return true;
    }

    public static final String serversDir = "servers" ;
    public static final String variablesFile = "variables.xml" ;

    public String getVariablesPath()
    {
        return
            getNodesDirName() + File.separator +
            nodeName          + File.separator +
            variablesFile;
    }

    protected String serversDirName;

    /**
	 * @return  the serversDirName
	 * @uml.property  name="serversDirName"
	 */
    public String getServersDirName()
    {
        if ( serversDirName == null ) {
            serversDirName =
                getNodesDirName() + File.separator +
                getNodeName()     + File.separator +
                serversDir;
        }

        return serversDirName;
    }

    public String[] readServerNames()
    {
        File dirPath = new File(getServersDirName());

        return dirPath.list();
    }
}
