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
 * WebSphere Handler
 *
 * History 1.2, 9/26/03
 *
 * 25-Aug-2002 Initial Version
 */

import java.util.*;

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;

public class WebSphereHandler extends BaseHandler
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // File link ...

    public static final String WEBSPHERE_FILE_NAME = "platform.websphere" ;
    // File link ...

    public static final String WEBSPHERE_FILE_EXTENSION = ".websphere" ;
    
    public static boolean accepts(String filename)
    {
        return ( filename.endsWith(WEBSPHERE_FILE_EXTENSION) );
    }

    // Class link ...

    public static final String WEBSPHERE_ELEMENT_NAME = "websphere" ;
    // Class link ...

    public static final String WEBSPHERE_CLASS_NAME = "websphere" ;
    
    public static final String
        WEBSPHERE_PACKAGE        = "com.ibm.websphere.product.xml.websphere";

    // Instantor ...

    public WebSphereHandler()
    {
        super();
    }

    protected Object createElement(String elementName,
                                   String parentElementName, Object parentElement,
                                   Attributes attributes)
        throws SAXParseException
    {
        if ( !elementName.equals(WEBSPHERE_ELEMENT_NAME) )
            throw newInvalidElementException(parentElementName, elementName);

        websphere childElement = new websphere();

        setAttributes(attributes, childElement);

        return childElement;
    }

    // Attribute Handling ...

    public static final String WEBSPHERE_NAME_FIELD_TAG = "name" ;
    // Attribute Handling ...

    public static final String WEBSPHERE_VERSION_FIELD_TAG = "version" ;

    // name    (String) [required] The websphere family name.
    // version (String) [required] The current family version.

    protected void setAttributes(Attributes attributes, websphere element)
        throws SAXParseException
    {
        element.setName( getAttribute(attributes, WEBSPHERE_NAME_FIELD_TAG, WEBSPHERE_ELEMENT_NAME, null) );
        element.setVersion( getAttribute(attributes, WEBSPHERE_VERSION_FIELD_TAG, WEBSPHERE_ELEMENT_NAME, null) );

        // 'getAttributes' throws SAXParseException
    }
}
