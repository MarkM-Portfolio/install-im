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
 * History 1.1, 12/20/02
 *
 * 17-Dec-2002 Initial version.
 */

import com.ibm.websphere.product.history.xml.componentVersion;
import com.ibm.websphere.product.xml.component.component;
import java.util.*;

/**
 *  
 */
public class PTFInstallData
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "12/20/02" ;

    // Instantor ...
    //
    // public PTFInstallData(ptfImage, Vector[componentUpdate], Vector[efix]);

    public PTFInstallData(PTFImage targetImage)
    {
        this.targetImage = targetImage;
        this.optionalUpdates = null;
        this.efixesToRemove = null;
    }

    protected PTFImage targetImage;

    /**
	 * @return  the targetImage
	 * @uml.property  name="targetImage"
	 */
    public PTFImage getTargetImage()
    {
        return targetImage;
    }

    protected Vector optionalUpdates;

    /**
	 * @return  the optionalUpdates
	 * @uml.property  name="optionalUpdates"
	 */
    public Vector getOptionalUpdates()
    {
        return optionalUpdates;
    }

    /**
	 * @param optionalUpdates  the optionalUpdates to set
	 * @uml.property  name="optionalUpdates"
	 */
    public void setOptionalUpdates(Vector optionalUpdates)
    {
        this.optionalUpdates = optionalUpdates;
    }

    protected Vector efixesToRemove;

    public Vector getEFixesToRemove()
    {
        return efixesToRemove;
    }

    public void setEFixesToRemove(Vector efixesToRemove)
    {
        this.efixesToRemove = efixesToRemove;
    }
}
