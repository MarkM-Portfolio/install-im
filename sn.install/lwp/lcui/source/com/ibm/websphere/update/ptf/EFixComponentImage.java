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
 * EFix Component Image
 *
 * History 1.1, 12/20/02
 *
 * 22-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
 */

import java.io.*;

import com.ibm.websphere.product.history.xml.*;

public class EFixComponentImage extends ComponentImage
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "12/20/02" ;

    // Instantor ...

    public EFixComponentImage(EFixImage parentImage,
                              String componentName)
    {
        super(parentImage, componentName);
    }

    // Parent Image access ...

    public EFixImage getEFixImage()
    {
        return (EFixImage) getParentImage();
    }

    public String getEFixId()
    {
        return getId();
    }

    public efixDriver getEFixDriver()
    {
        return (efixDriver) getDriver();
    }

    protected componentUpdate retrieveUpdate()
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
}
