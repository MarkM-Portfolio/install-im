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
 * Product Handler
 *
 * History 1.2, 9/26/03
 *
 * 24-Aug-2002 Initial Version
 */

import java.util.*;

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;

public class ProductHandler extends BaseHandler
{
    // Program versioning ...
    
   public static final String pgmVersion = "1.2" ;
    // Program versioning ...
    
   public static final String pgmUpdate = "9/26/03" ;

    // File links ...

    public static final String
        PRODUCT_FILE_EXTENSION = ".product";

    public static boolean accepts(String filename)
    {
        return ( filename.endsWith(PRODUCT_FILE_EXTENSION) );
    }

    // Class links ...

    public static final String PRODUCT_ELEMENT_NAME = "product" ;
    // Class links ...

    public static final String PRODUCT_CLASS_NAME = "product" ;
    // Class links ...

    public static final String PRODUCT_VERSION_ELEMENT_NAME = "version" ;
    // Class links ...

    public static final String PRODUCT_ID_ELEMENT_NAME = "id" ;
    // Class links ...

    public static final String PRODUCT_BUILD_INFO_ELEMENT_NAME = "build-info" ;
    // Class links ...

    public static final String PRODUCT_BUILD_INFO_CLASS_NAME = "buildInfo" ;

    public static final String
        PRODUCT_PACKAGE                 = "com.ibm.websphere.product.xml.product";

    // Field tags ...

    public static final String PRODUCT_ID_FIELD_TAG = "id" ;
    // Field tags ...

    public static final String PRODUCT_VERSION_FIELD_TAG = "version" ;
    // Field tags ...

    public static final String PRODUCT_BUILD_INFO_FIELD_TAG = "build-info" ;
    // Field tags ...

    public static final String PRODUCT_NAME_FIELD_TAG = "name" ;

    // id         (id)        [required] The id of this product.
    // name       (String)    [required] The name of this product.
    // version    (version)   [required] The version of this product.
    // build-info (buildInfo) [required] A package of build information for this product.

    // Field tags ...

    public static final String BUILD_INFO_DATE_FIELD_TAG = "date" ;
    // id         (id)        [required] The id of this product.
    // name       (String)    [required] The name of this product.
    // version    (version)   [required] The version of this product.
    // build-info (buildInfo) [required] A package of build information for this product.

    // Field tags ...

    public static final String BUILD_INFO_LEVEL_FIELD_TAG = "level" ;

    // date  (Date)   [required] (The build date of the product.)
    // level (String) [required] (The build level of the product.)

    // Instantor ...

    public ProductHandler()
    {
        super();
    }

    // Factory operations ...

    public void startElement(String documentUri,
                             String elementName,
                             String qualifiedName,
                             Attributes attributes)
        throws SAXException
    {
        super.startElement(documentUri, elementName, qualifiedName, attributes);

        // Start capturing after setting up the new element.

        if ( elementName.equals(PRODUCT_VERSION_ELEMENT_NAME) ||
             elementName.equals(PRODUCT_ID_ELEMENT_NAME) ) {
            startCapturing();
        }
    }

    protected Object createElement(String elementName,
                                   String parentElementName, Object parentElement,
                                   Attributes attributes)
        throws SAXParseException
    {
        Object element = null;
        boolean allowNull = false;

        if ( parentElement == null ) {
            if ( elementName.equals(PRODUCT_ELEMENT_NAME) ) {
                product typedElement = new product();
                typedElement.setName( getAttribute(attributes, PRODUCT_NAME_FIELD_TAG, elementName, null) );

                element = typedElement;
            }

        } else if ( parentElement instanceof product ) {
            if ( elementName.equals(PRODUCT_BUILD_INFO_ELEMENT_NAME) ) {
                buildInfo typedElement = new buildInfo();
                typedElement.setDate ( getAttribute(attributes, BUILD_INFO_DATE_FIELD_TAG, elementName, null) );
                typedElement.setLevel( getAttribute(attributes, BUILD_INFO_LEVEL_FIELD_TAG, elementName, null) );
                    
                product typedParentElement = (product) parentElement;
                typedParentElement.setBuildInfo(typedElement);

                element = typedElement;
            } else if ( elementName.equals(PRODUCT_ID_ELEMENT_NAME) ||
                        elementName.equals(PRODUCT_VERSION_ELEMENT_NAME) ) {
                allowNull = true;
            }
        }

        if ( (element == null) && !allowNull )
            throw newInvalidElementException(parentElementName, elementName);
        else
            return element;
    }

    public void endElement(String documentUri, String elementName, String qualifiedName)
        throws SAXException
    {
        // Nab the element text while the ID or VERSION element is still
        // active.  Note that the current element will be null.

        String id, version;

        if ( elementName.equals(PRODUCT_ID_ELEMENT_NAME) ) {
            id = stopCapturing();
            version = null;
        } else if ( elementName.equals(PRODUCT_VERSION_ELEMENT_NAME) ) {
            id = null;
            version = stopCapturing();
        } else {
            id = null;
            version = null;
        }

        super.endElement(documentUri, elementName, qualifiedName); // throws SAXException

        // After ending the element, the parent product will be active.

        product typedElement = (product) getCurrentElement();

        if ( id != null )
            typedElement.setId(id);
        else if ( version != null )
            typedElement.setVersion(version);
    }
}
