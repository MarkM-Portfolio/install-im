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
package com.ibm.websphere.update.ptf;


/*
 * Update Image
 *
 * This image represents a single update -- an efix or PTF --
 * as it is shipped to a customer.
 *
 * History 1.2, 1/15/03
 *
 * 09-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
 */

/**
 * <p>Expected efix jar contents.  One or more efix may be
 * present, as follows:</p>
 * <pre>
 *     efixes/
 *
 *     efixes/efix1/
 *     efixes/efix1/efix1.efix
 *     efixes/efix1/components/
 *     efixes/efix1/components/component1/
 *     efixes/efix1/components/component1/update.jar
 *     efixes/efix1/components/component2/
 *     efixes/efix1/components/component2/update.jar
 *
 *     efixes/efix2/
 *     efixes/efix2/efix2.efix
 *     efixes/efix2/components/
 *     efixes/efix2/components/component1/
 *     efixes/efix2/components/component1/update.jar
 *     efixes/efix2/components/component2/
 *     efixes/efix2/components/component2/update.jar
 *
 *     ptfs/ptf1/
 *     ptfs/ptf1/ptf1.ptf
 *     ptfs/ptf1/components/
 *     ptfs/ptf1/components/component1/
 *     ptfs/ptf1/components/component1/update.jar
 *     ptfs/ptf1/components/component2/
 *     ptfs/ptf1/components/component2/update.jar
 *
 *     ptfs/ptf2/
 *     ptfs/ptf2/ptf2.ptf
 *     ptfs/ptf2/components/
 *     ptfs/ptf2/components/component1/
 *     ptfs/ptf2/components/component1/update.jar
 *     ptfs/ptf2/components/component2/
 *     ptfs/ptf2/components/component2/update.jar
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
abstract public class UpdateImage
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "1/15/03" ;

    // Instantor ...

    public UpdateImage(IOService ioService, String dtdDirName, String jarName, String updateId)
    {
        this.ioService  = ioService;
        this.dtdDirName = dtdDirName;

        this.jarName = jarName;
        this.updateId  = updateId;

        this.didPrepareDriver = false;
        this.boundDriver = null;

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

    protected String dtdDirName;

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

    // Update Id ...

    protected String updateId;

    /**
	 * @return  the updateId
	 * @uml.property  name="updateId"
	 */
    public String getUpdateId()
    {
        return updateId;
    }

    public abstract String getUpdatesDir();
    public abstract String getDriverExtension();

    protected String entryName;

    /**
	 * @return  the entryName
	 * @uml.property  name="entryName"
	 */
    public String getEntryName()
    {
        if ( entryName == null )
            entryName = computeEntryName(getUpdateId());

        return entryName;
    }

    protected String computeEntryName(String updateId)
    {
        return getUpdatesDir() + "/" + updateId;
    }

    public String getDriverEntryName()
    {
        return
            getEntryName() + "/" +
            getUpdateId() + getDriverExtension();
    }

    // Driver access ...

    protected boolean didPrepareDriver;
    protected Object boundDriver;

    public boolean didPrepareDriver()
    {
        return didPrepareDriver;
    }

    public void prepareDriver()
        throws IOException, BaseHandlerException
    {
        if ( !didPrepareDriver ) {
            didPrepareDriver = true;

            loadDriver(); // throws IOException, BaseHandlerException
        }
    }

    public Object getDriver()
    {
        return boundDriver;
    }

    protected void loadDriver()
        throws IOException, BaseHandlerException
    {
        String useJarName = getJarName();
        String useEntryName = getDriverEntryName();
        String compositeEntryName = useJarName + "-->" + useEntryName;	

        InputStream driverStream = getJarEntryStream(useJarName, useEntryName);
                                  // throws IOException

        try {
            InputSource driverSource = new InputSource(driverStream);

            AppliedHandler handler = new AppliedHandler();

            boundDriver = BaseFactory.loadSingleton(handler,
                                                    getDtdDirName(),
                                                    driverSource,
                                                    compositeEntryName);
            // throws BaseHandlerException

            //TBD The Factory could have warnings (getWarnings())
            //    or soft errors (getRecoverableErrors()).

        } finally {
            driverStream.close(); // throws IOException
        }
    }

    // Component access ...

    // Have, for example, efixes/efix1/components
    //                    ptfs/ptf1/components
    //
    // Need to list the directires immediately beneath, for example:
    //     efixes/efix1/components/component1
    //     efixes/efix1/components/component2
    //
    //     ptfs/ptf1/components/component1
    //     ptfs/ptf1/components/component2
    //
    // The result is the vector { "component1", "component2" }.

    public static final String COMPONENTS_DIR = "components";

    protected boolean didPrepareComponents;
    protected Vector componentNames;     // Remember their order!
    protected Hashtable componentImages;

    protected String getComponentsEntryName()
    {
        return getEntryName() + "/" + COMPONENTS_DIR;
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

    protected ComponentImage getComponentImage(String componentName)
    {
        return (ComponentImage) getComponentImages().get(componentName);
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

    protected abstract ComponentImage
        createComponentImage(UpdateImage parentImage, String componentName);


    protected void prepareComponentImages()
    {
        Vector useComponentNames = getComponentNames();
        int numComponents = componentNames.size();

//        System.out.println("useComponentNames: " + useComponentNames);
        
        IOService useIOService = getIOService();
        String useJarName      = getJarName();
        String useUpdateId     = getUpdateId();

        componentImages = new Hashtable();

        for ( int compNo = 0; compNo < numComponents; compNo++ ) {
            String nextCompName = (String) componentNames.elementAt(compNo);

            ComponentImage nextImage = createComponentImage(this, nextCompName);

            componentImages.put(nextCompName, nextImage);                       
        }
        
//        System.out.println("componentImages: " + componentImages);
    }
}
