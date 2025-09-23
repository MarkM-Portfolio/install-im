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
 * Extension Factory
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 */

import java.util.*;

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;

public class ExtensionHandler extends BaseHandler
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // File link ...

    public static final String
        EXTENSION_FILE_EXTENSION  = ".extension";

    public static boolean accepts(String filename)
    {
        return ( filename.endsWith(EXTENSION_FILE_EXTENSION) );
    }

    // Class link ...

    public static final String EXTENSION_CLASS_NAME = "extension" ;
    // Class link ...

    public static final String EXTENSION_ELEMENT_NAME = EXTENSION_CLASS_NAME ;
    // Class link ...

    public static final String CUSTOM_ELEMENT_CLASS_NAME = "customElement" ;

    public static final String
        EXTENSION_PACKAGE         = "com.ibm.websphere.product.xml.extension";

    // Instantor ...

    public ExtensionHandler()
    {
        super();
    }

    // Field tags ...

    public static final String EXTENSION_ID_FIELD_TAG = "id" ;
    // Field tags ...

    public static final String EXTENSION_NAME_FIELD_TAG = "name" ;

    // id   (String) [required] The id of this extension.
    // name (String) [required] The name of this extension.

    // Factory operations ...

    public void startElement(String documentUri,
                             String elementName,
                             String qualifiedName,
                             Attributes attributes)
        throws SAXException
    {
        super.startElement(documentUri, elementName, qualifiedName, attributes);

        // No matter, start capturing text on each element.

        startCapturing();
    }

    protected Object createElement(String elementName,
                                   String parentElementName, Object parentElement,
                                   Attributes attributes)
        throws SAXParseException
    {
        Object element;

        if ( parentElement == null ) {
            if ( elementName.equals(EXTENSION_ELEMENT_NAME) ) {
                extension typedElement = new extension(elementName, attributes);

                typedElement.setId( getAttribute(attributes, EXTENSION_ID_FIELD_TAG, elementName, null) );
                typedElement.setName( getAttribute(attributes, EXTENSION_NAME_FIELD_TAG, elementName, null) );

                element = typedElement;
            } else {
                throw newInvalidElementException(parentElementName, elementName);
            }

        } else {
            customElement typedElement = new customElement(elementName, attributes);

            customElement typedParentElement = (customElement) parentElement;
            typedParentElement.addCustomElement(typedElement);

            element = typedElement;
        }

        return element;
    }

    public void endElement(String documentUri, String elementName, String qualifiedName)
        throws SAXException
    {
        // Nab the captured element text and store it in the element that
        // about to be completed.

        String elementText = stopCapturing();
        customElement typedElement = (customElement) currentElement;
        typedElement.setElementText(elementText);

        // Then, complete that element.
        super.endElement(documentUri, elementName, qualifiedName); // throws SAXException
    }
}
