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

package com.ibm.websphere.product.xml.websphere;

/*
 * WebSphere Bean
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class websphere extends BaseType
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public websphere()
    {
        super();

        this.name = null;
        this.version = null;
    }
    
    // Basic field access ...

    // See the field tags, above ...

    protected String name;

    /**
	 * @param name  the name to set
	 * @uml.property  name="name"
	 */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
	 * @return  the name
	 * @uml.property  name="name"
	 */
    public String getName()
    {
        return name;
    }
    
    protected String version;

    /**
	 * @param version  the version to set
	 * @uml.property  name="version"
	 */
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    /**
	 * @return  the version
	 * @uml.property  name="version"
	 */
    public String getVersion()
    {
        return version;
    }
}
