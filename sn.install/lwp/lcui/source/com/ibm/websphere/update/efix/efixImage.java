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
package com.ibm.websphere.update.efix;

/*
 * eFix Install Image
 *
 * This eFixImage object represents a single eFix as it
 * is shipped to a customer.
 *
 * History 1.2, 9/16/02
 *
 * 09-Jul-2002 Initial Version
 */

/**
 * <p>Expected efix jar contents.  One or more efix may be
 * present, as follows:</p>
 * <pre>
 *     efixes\\
 *
 *     efixes\\efix1\\
 *     efixes\\efix1\\efix1.efix
 *     efixes\\efix1\\components\\
 *     efixes\\efix1\\components\\component1\\
 *     efixes\\efix1\\components\\component1\\update.jar
 *     efixes\\efix1\\components\\component2\\
 *     efixes\\efix1\\components\\component2\\update.jar
 *
 *     efixes\\efix2\\
 *     efixes\\efix2\\efix2.efix
 *     efixes\\efix2\\components\\
 *     efixes\\efix2\\components\\component1\\
 *     efixes\\efix2\\components\\component1\\update.jar
 *     efixes\\efix2\\components\\component2\\
 *     efixes\\efix2\\components\\component2\\update.jar
 *
 */

import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.update.*;
import com.ibm.websphere.update.ioservices.*;
import java.io.*;
import java.util.*;
import org.xml.sax.InputSource;

/**
 *  
 */
public class efixImage
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/16/02" ;

    // Instantor ...

    public efixImage(IOService ioService, String dtdDirName, String jarName, String eFixId)
    {
        this.ioService  = ioService;
        this.dtdDirName = dtdDirName;

        this.jarName = jarName;
        this.efixId  = eFixId;

        this.didPrepareEFixDriver = false;
        this.boundEFixDriver = null;

        this.didPrepareComponents = false;
        this.componentNames = null;
        this.componentImages = null;
    }

    // IO Service details ...

    protected IOService ioService;

    public IOService getIOService()
    {
        return ioService;
    }

    protected InputStream getJarEntryStream(String jarName, String entryName)
        throws IOException
    {
        return getIOService().getJarEntryStream(jarName, entryName);
                              // throws IOException
    }

    protected Vector getChildEntryNames(String jarName, String rootName)
        throws IOException
    {
        return getIOService().getChildEntryNames(jarName, rootName);
    }

    // DTD Directory

    protected String dtdDirName;         // location of "efix.dtd", among other dtds

    /**
	 * @return  the dtdDirName
	 * @uml.property  name="dtdDirName"
	 */
    public String getDtdDirName()
    {
        return dtdDirName;
    }

    // Jar name ...

    protected String jarName;

    /**
	 * @return  the jarName
	 * @uml.property  name="jarName"
	 */
    public String getJarName()
    {
        return jarName;
    }

    // EFix Id ...

    protected String efixId;             // e.g. "efix1"

    public String getEFixId()
    {
        return efixId;
    }

    // EFix Entry Identification ...

    public static final String EFIXES_DIR     = "efixes";

    protected String efixEntryName;      // e.g. "efixes\\efix1"

    public String getEFixEntryName()
    {
        if ( efixEntryName == null )
            efixEntryName = computeEFixEntryName(getEFixId());

        return efixEntryName;
    }

    public String computeEFixEntryName(String eFixId)
    {
        return EFIXES_DIR +"/"+ eFixId;
    }

    public String getEFixFileEntryName()
    {
        return
            getEFixEntryName() + "/" +
            getEFixId() + AppliedHandler.EFIX_DRIVER_FILE_EXTENSION ;
    }

    // EFix access ...

    protected boolean didPrepareEFixDriver;
    protected efixDriver boundEFixDriver; // e.g., from "efixes\\efix1\\efix1.efix", XML data

    public boolean didPrepareEFixDriver()
    {
        return didPrepareEFixDriver;
    }

    public void prepareEFixDriver()
        throws IOException, BaseHandlerException
    {
        if ( !didPrepareEFixDriver ) {
            didPrepareEFixDriver = true;

            loadEFixDriver(); // throws IOException, BaseHandlerException
        }
    }

    public efixDriver getEFixDriver()
    {
        return boundEFixDriver;
    }

    protected void loadEFixDriver()
        throws IOException, BaseHandlerException
    {
        String useJarName = getJarName();
        String useEntryName = getEFixFileEntryName();
        String compositeEntryName = useJarName + "-->" + useEntryName;

        InputStream efixStream = getJarEntryStream(useJarName, useEntryName);
                                 // throws IOException

        try {
            InputSource efixSource = new InputSource(efixStream);

            AppliedHandler handler = new AppliedHandler();

            boundEFixDriver = (efixDriver) BaseFactory.loadSingleton(handler,
								     getDtdDirName(),
								     efixSource,
								     compositeEntryName);
            // throws BaseHandlerException

            //TBD The Factory could have warnings (getWarnings())
            //    or soft errors (getRecoverableErrors()).

        } finally {
            efixStream.close(); // throws IOException
        }
    }

    // Component access ...

    // Have, for example, efixes\\efix1\\components
    // Need to list the directires immediately beneath, for example:
    //     efixes\\efix1\\components\\component1
    //     efixes\\efix1\\components\\component2
    // The result is the vector { "component1", "component2" }.

    public static final String COMPONENTS_DIR = "components";

    protected boolean didPrepareComponents;
    protected Vector componentNames;     // Remember their order!
    protected Hashtable componentImages;

    public String getComponentsEntryName()
    {
        return getEFixEntryName() + "/" + COMPONENTS_DIR;
    }

    public boolean didPrepareComponents()
    {
        return didPrepareComponents;
    }

    public void prepareComponents()
        throws IOException
    {
        if ( !didPrepareComponents ) {
            didPrepareComponents = true;

            prepareComponentNames();  // throws IOException
            prepareComponentImages(); // throws IOException
        }
    }

    /**
	 * @return  the componentNames
	 * @uml.property  name="componentNames"
	 */
    public Vector getComponentNames()
    {
        return componentNames;
    }

    public int getComponentCount()
    {
        return getComponentNames().size();
    }

    public componentImage getComponentImage(String componentName)
    {
        return (componentImage) getComponentImages().get(componentName);
    }

    /**
	 * @return  the componentImages
	 * @uml.property  name="componentImages"
	 */
    protected Hashtable getComponentImages()
    {
        return componentImages;
    }

    protected void prepareComponentNames()
        throws IOException
    {
        componentNames = getChildEntryNames(getJarName(), getComponentsEntryName());
    }

    protected void prepareComponentImages()
    {
        Vector useComponentNames = getComponentNames();
        int numComponents = componentNames.size();

        IOService useIOService = getIOService();
        String useJarName      = getJarName();
        String useEFixId       = getEFixId();

        componentImages = new Hashtable();

        for ( int compNo = 0; compNo < numComponents; compNo++ ) {
            String nextCompName = (String) componentNames.elementAt(compNo);

            componentImage nextImage =
                new componentImage(this, nextCompName);

            componentImages.put(nextCompName, nextImage);
        }
    }
}
