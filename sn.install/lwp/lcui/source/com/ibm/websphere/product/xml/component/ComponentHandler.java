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

package com.ibm.websphere.product.xml.component;

/*
 * Component Handler
 *
 * History 1.2, 9/26/03
 *
 * 24-Aug-2002 Initial Version
 */

import java.util.*;

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;

public class ComponentHandler extends BaseHandler
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/26/03" ;

    // File link ...

    public static final String
        COMPONENT_FILE_EXTENSION = ".component";

    public static boolean accepts(String filename)
    {
        return ( filename.endsWith(COMPONENT_FILE_EXTENSION) );
    }

    // Class link ...

    public static final String COMPONENT_CLASS_NAME = "component" ;
    // Class link ...

    public static final String COMPONENT_PACKAGE_NAME = "com.ibm.websphere.product.xml.component" ;

    public static final String
        COMPONENT_ELEMENT_NAME   = "component";

    // Factory operations ...

    protected Object createElement(String elementName,
                                   String parentElementName, Object parentElement,
                                   Attributes attributes)
        throws SAXParseException
    {
        // System.out.println("::: Creating element: " + elementName);
        // System.out.println("::: Parent element: " + parentElementName);

        if ( !(elementName.equals(COMPONENT_ELEMENT_NAME)) ||
             (parentElementName != null) )
            throw newInvalidElementException(parentElementName, elementName);

        component childElement = new component();

        setAttributes(attributes, childElement);

        // System.out.println(":: Returning child element.");

        return childElement;
    }

    // Attribute Handling ...

    public static final String COMPONENT_NAME_FIELD_TAG = "name" ;
    // Attribute Handling ...

    public static final String COMPONENT_SPEC_VERSION_FIELD_TAG = "spec-version" ;
    // Attribute Handling ...

    public static final String COMPONENT_BUILD_VERSION_FIELD_TAG = "build-version" ;
    // Attribute Handling ...

    public static final String COMPONENT_BUILD_DATE_FIELD_TAG = "build-date" ;

    // name          (String) [required] The name of this component.
    // spec-version  (String) [required] The specification version of this component.
    // build-version (String) [required] The build version of this component.
    // build-date    (String) [required] The build date of this component.

    protected void setAttributes(Attributes attributes, component element)
        throws SAXParseException
    {
        element.setName( getAttribute(attributes,
                                      COMPONENT_NAME_FIELD_TAG,
                                      COMPONENT_ELEMENT_NAME,
                                      null) );

        element.setSpecVersion( getAttribute(attributes,
                                             COMPONENT_SPEC_VERSION_FIELD_TAG,
                                             COMPONENT_ELEMENT_NAME,
                                             null) );

        element.setBuildVersion( getAttribute(attributes,
                                              COMPONENT_BUILD_VERSION_FIELD_TAG,
                                              COMPONENT_ELEMENT_NAME,
                                              null) );
        element.setBuildDate( getAttribute(attributes,
                                           COMPONENT_BUILD_DATE_FIELD_TAG,
                                           COMPONENT_ELEMENT_NAME,
                                           null) );

        // 'getAttributes' throws SAXParseException
    }   
}
