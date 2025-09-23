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

package com.ibm.websphere.product.xml.product;

/*
 * Product Bean
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
public class product extends BaseType
{
    // Program versioning ...
    
   public static final String pgmVersion = "1.2" ;
    // Program versioning ...
    
   public static final String pgmUpdate = "9/26/03" ;
    
    // Instantor ...

    public product()
    {
        super();

        this.id = null;
        this.name = null;
        this.version = null;
        this.buildInfo = null;
    }

    // Basic field access ...

    // See the field tags, above ...
    
    protected String id;

    /**
	 * @param id  the id to set
	 * @uml.property  name="id"
	 */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
	 * @return  the id
	 * @uml.property  name="id"
	 */
    public String getId()
    {
        return id;
    }

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

    protected buildInfo buildInfo;

    /**
	 * @param buildInfo  the buildInfo to set
	 * @uml.property  name="buildInfo"
	 */
    public void setBuildInfo(buildInfo buildInfo)
    {
        this.buildInfo = buildInfo;
    }
    
    /**
	 * @return  the buildInfo
	 * @uml.property  name="buildInfo"
	 */
    public buildInfo getBuildInfo()
    {
        return buildInfo;
    }
}
