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

package com.ibm.websphere.product.xml.efix;

/*
 * EFix Factory
 *
 * History 1.2, 9/26/03
 *
 * 25-Jun-2002 Added standard header.
 *             Added versionPrereq and platformPrereq types.
 */

import java.util.*;

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;

public class EFixHandler extends BaseHandler
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "9/26/03" ;

    // File links ...

    public static final String EFIX_FILE_EXTENSION = ".efix" ;
    // File links ...

    public static final String EFIX_APPLIED_FILE_EXTENSION = ".efixApplied" ;
    // File links ...

    public static final String PTF_FILE_EXTENSION = ".ptf" ;
    // File links ...

    public static final String PTF_APPLIED_FILE_EXTENSION = ".ptfApplied" ;

    public static final String
        EFIX_PACKAGE                = "com.ibm.websphere.product.xml.efix";

    // File links ...

    public static boolean acceptsEFixFileName(String filename)
    {
        return ( filename.endsWith(EFIX_FILE_EXTENSION) );
    }

    public static String getStandardEFixFileName(String efixId)
    {
        return efixId + EFIX_FILE_EXTENSION;
    }

    public static boolean acceptsPTFFileName(String filename)
    {
        return ( filename.endsWith(PTF_FILE_EXTENSION) );
    }

    public static String getStandardPTFFileName(String ptfId)
    {
        return ptfId + PTF_FILE_EXTENSION;
    }

    // Instantor ...

    public EFixHandler()
    {
        super();
    }

    // Class and element links ...

    public static final String EFIX_CLASS_NAME = "efix" ;
    // Class and element links ...

    public static final String EFIX_ELEMENT_NAME = "efix" ;
    // Class and element links ...

    public static final String PTF_CLASS_NAME = "ptf" ;
    // Class and element links ...

    public static final String PTF_ELEMENT_NAME = "ptf" ;
    // Class and element links ...

    public static final String COMPONENT_NAME_ELEMENT_NAME = "component-name" ;

    // efix tags ...

    public static final String ID_FIELD_TAG = "id" ;
    // efix tags ...

    public static final String SHORT_DESCRIPTION_FIELD_TAG = "short-description" ;
    // efix tags ...

    public static final String BUILD_VERSION_FIELD_TAG = "build-version" ;
    // efix tags ...

    public static final String BUILD_DATE_FIELD_TAG = "build-date" ;

    // id                (String)  [required] The id of the update.
    // short-description (String)  [required] A short description of the update.
    // build-version     (String)  [required] The build version of the update.
    // build-date        (Date)    [required] The build date of the update.

    // ptf tags ...

    // All Duplicates of efix tags

    //    public static final String
        //        ID_FIELD_TAG                = "id",
        //        SHORT_DESCRIPTION_FIELD_TAG = "short-description",
        //        BUILD_VERSION_FIELD_TAG     = "build-version",
        //        BUILD_DATE_FIELD_TAG        = "build-date";

    // id                (String)  [required] The id of the update.
    // short-description (String)  [required] A short description of the update.
    // build-version     (String)  [required] The build version of the update.
    // build-date        (Date)    [required] The build date of the update.

    // Factory operations ...

    public void startElement(String documentUri,
                             String elementName,
                             String qualifiedName,
                             Attributes attributes)
        throws SAXException
    {
        super.startElement(documentUri, elementName, qualifiedName, attributes);

        // Start capturing after setting up the new element.

        if ( elementName.equals(COMPONENT_NAME_ELEMENT_NAME) )
            startCapturing();
    }

    public void endElement(String documentUri, String elementName, String qualifiedName)
        throws SAXException
    {
        // Nab the element text while the component-name element is still
        // active.  Note that the current element is null until 'endElement'
        // is called.

        String componentName;

        if ( elementName.equals(COMPONENT_NAME_ELEMENT_NAME) )
            componentName = stopCapturing();
        else
            componentName = null;

        super.endElement(documentUri, elementName, qualifiedName); // throws SAXException

        // After ending the element, the parent product will be active.

        if ( componentName != null ) {
            Object currentElement = getCurrentElement();

            if ( currentElement instanceof efix )
                ((efix) currentElement).addComponentName(componentName);
            else if ( currentElement instanceof ptf )
                ((ptf) currentElement).addComponentName(componentName);
        }
    }

    protected Object createElement(String elementName,
                                   String parentElementName, Object parentElement,
                                   Attributes attributes)
        throws SAXParseException
    {
        Object element;

        if ( parentElement == null ) {
            if ( elementName.equals(EFIX_ELEMENT_NAME) ) {
                efix typedElement = new efix();

                typedElement.setId
                    ( getAttribute(attributes, ID_FIELD_TAG, elementName, null) );
                typedElement.setShortDescription
                    ( getAttribute(attributes, SHORT_DESCRIPTION_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion
                    ( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate
                    ( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );

                element = typedElement;

            } else if ( elementName.equals(PTF_ELEMENT_NAME) ) {
                ptf typedElement = new ptf();

                typedElement.setId
                    ( getAttribute(attributes, ID_FIELD_TAG, elementName, null) );
                typedElement.setShortDescription
                    ( getAttribute(attributes, SHORT_DESCRIPTION_FIELD_TAG, elementName, null) );
                typedElement.setBuildVersion
                    ( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
                typedElement.setBuildDate
                    ( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );

                element = typedElement;

            } else {
                throw newInvalidElementException(parentElementName, elementName);
            }

        } else if ( elementName.equals(COMPONENT_NAME_ELEMENT_NAME) ) {
            element = null;

        } else {
            throw newInvalidElementException(parentElementName, elementName);
        }

        return element;
    }
}
