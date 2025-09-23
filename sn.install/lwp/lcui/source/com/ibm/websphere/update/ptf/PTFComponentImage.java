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
 * PTF Component Image
 *
 * History 1.2, 1/15/03
 *
 * 22-Jul-2002 Initial Version
 *
 * 25-Nov-2002 Branch for PTF processing.
 */

import java.io.*;

import com.ibm.websphere.product.history.xml.*;

public class PTFComponentImage extends ComponentImage
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "1/15/03" ;

    // Instantor ...

    public PTFComponentImage(PTFImage parentImage,
                             String componentName)
    {
        super(parentImage, componentName);
    }

    // Parent Image access ...

    public PTFImage getPTFImage()
    {
        return (PTFImage) getParentImage();
    }

    public String getPTFId()
    {
        return getId();
    }

    public ptfDriver getPTFDriver()
    {
        return (ptfDriver) getDriver();
    }

    protected componentUpdate retrieveUpdate()
    {
        String useComponentName = getComponentName();

        ptfDriver usePTFDriver = getPTFDriver();

        componentUpdate matchingComponent = null;

        int numComponents = usePTFDriver.getComponentUpdateCount();

        for ( int compNo = 0;
              (matchingComponent == null) && (compNo < numComponents);
              compNo++ ) {

            componentUpdate nextComponent = usePTFDriver.getComponentUpdate(compNo);

            if ( nextComponent.getComponentName().equals(useComponentName) )
                matchingComponent = nextComponent;
        }

        return matchingComponent;
    }
}
