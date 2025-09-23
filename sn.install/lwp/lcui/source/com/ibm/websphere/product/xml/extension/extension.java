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

package com.ibm.websphere.product.xml.extension;

/*
 * Extension Bean
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;
import org.xml.sax.*;

/**
 *  
 */
public class extension extends customElement
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public extension(String elementName, Attributes attributes)
    {
        super(elementName, attributes);

        this.id = null;
        this.name = null;
    }
    
    // Basic field access ...

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
}
