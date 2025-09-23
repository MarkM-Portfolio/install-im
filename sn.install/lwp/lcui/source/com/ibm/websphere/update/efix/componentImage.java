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
 * Component Image
 *
 * History 1.2, 9/16/02
 *
 * 22-Jul-2002 Initial Version
 */

import com.ibm.websphere.product.history.xml.*;
import java.io.*;

/**
 *  
 */
public class componentImage
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/16/02" ;

    // Instantor ...

    public componentImage(efixImage parentImage,
                          String componentName)
    {
        this.parentImage = parentImage;

        this.componentName = componentName;

        this.didSelectComponent = false;
        this.efixComponent = null;
    }

    // EFix Image access ...

    protected efixImage parentImage;

    public efixImage getEFixImage()
    {
        return parentImage;
    }

    public String getEFixId()
    {
        return getEFixImage().getEFixId();
    }

    public efixDriver getEFixDriver()
    {
        return getEFixImage().getEFixDriver();
    }

    public String getJarName()
    {
        return getEFixImage().getJarName();
    }

    // Component access ...

    protected String componentName;

    protected boolean didSelectComponent;
    protected componentUpdate efixComponent;

    /**
	 * @return  the componentName
	 * @uml.property  name="componentName"
	 */
    public String getComponentName()
    {
        return componentName;
    }

    public boolean didSelectComponent()
    {
        return didSelectComponent;
    }

    public componentUpdate getComponent()
    {
        if ( !didSelectComponent ) {
            didSelectComponent = true;
            efixComponent = selectComponent();
        }

        return efixComponent;
    }

    public boolean getIsRequired()
    {
        componentUpdate useComponent = getComponent();

        if ( useComponent == null )
            return false;
        else
            return useComponent.getIsRequiredAsBoolean();
    }

    protected componentUpdate selectComponent()
    {
        String useComponentName = getComponentName();

        efixDriver useEFixDriver = getEFixDriver();

        componentUpdate matchingComponent = null;

        int numComponents = useEFixDriver.getComponentUpdateCount();

        for ( int compNo = 0;
              (matchingComponent == null) && (compNo < numComponents);
              compNo++ ) {

            componentUpdate nextComponent = useEFixDriver.getComponentUpdate(compNo);

            if ( nextComponent.getComponentName().equals(useComponentName) )
                matchingComponent = nextComponent;
        }

        return matchingComponent;
    }

    public String getComponentEntryName()
    {
        return getEFixImage().getComponentsEntryName() + "/" + getComponentName();
    }

    public String getPrimaryContentEntryName()
    {
        return getComponentEntryName() + "/" + getComponent().getPrimaryContent();
    }
}
