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
 * Extension Writer
 *
 * History 1.2, 9/26/03
 *
 * 25-Aug-2002 Initial Version
 */

import java.util.*;

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;

public class ExtensionWriter extends BaseWriter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public ExtensionWriter()
    {
        super();
    }

    public String getDefaultDocTypeString(List rootElements)
    {
        return "<!DOCTYPE extension SYSTEM \"extension.dtd\">";
    }

    public void emitExtension(extension extension)
    {
        beginDocument();

        emitCustomElement(extension, IS_A_ROOT_ELEMENT);
    }

    public static final boolean IS_A_ROOT_ELEMENT = true ;
    public static final boolean NOT_A_ROOT_ELEMENT = false ;

    // <extension name="x" id="y"/>
    //
    // <extension name="x" id="y"
    //     extra1="a"
    //     extra2="b"/>
    //
    // <elementName
    //     extra1="a"
    //     extra2="b"/>
    //
    // <extension name="x" id="y">
    //     elementText
    //     <elementName ...
    //     <elementName ...
    // </elementName>
    //
    // <extension name="x" id="y"
    //     extra1="a"
    //     extra2="b">
    //     <elementName ...
    //     <elementName ...
    // </elementName>
    //
    // <elementName
    //     extra1="a"
    //     extra2="b">
    //     <elementName ...
    //     <elementName ...
    // </elementName>

    public void emitCustomElement(customElement element, boolean isRoot)
    {
        printIndent();

        String elementName;

        if ( isRoot )
            elementName = ExtensionHandler.EXTENSION_ELEMENT_NAME;
        else
            elementName = element.getElementName();

        beginElementOpening(elementName);

        if ( isRoot ) {
            extension extension = (extension) element;

            print(" ");
            emitAttribute(ExtensionHandler.EXTENSION_NAME_FIELD_TAG, extension.getName());
            print(" ");
            emitAttribute(ExtensionHandler.EXTENSION_ID_FIELD_TAG, extension.getId());
        }

        Attributes customAttributes = element.getAttributes();

        int numAttributes = customAttributes.getLength();

        if ( numAttributes > 0 ) {
            for ( int attrNo = 0; attrNo < numAttributes; attrNo++ ) {
                String attributeName = customAttributes.getLocalName(attrNo);
                String attributeValue = customAttributes.getValue(attrNo);

                if ( !isRoot ||
                     ( !attributeName.equals(ExtensionHandler.EXTENSION_NAME_FIELD_TAG) &&
                       !attributeName.equals(ExtensionHandler.EXTENSION_ID_FIELD_TAG) ) ) {
                    println();
                    printIndent();
                    emitAttribute(attributeName, attributeValue);
                }
            }
        }

        String customText = element.getElementText();
        Iterator customElements = element.getCustomElements().iterator();

        if ( (customText != null) || (customElements.hasNext()) ) {
            endElementOpening(CLOSE_PARTIALLY);
            println();

            indentIn();

            if ( customText != null ) {
                printIndent();
                println(customText);
            }

            if ( customElements.hasNext() ) {
                while ( customElements.hasNext() ) {
                    customElement nextElement = (customElement) customElements.next();

                    emitCustomElement(nextElement, NOT_A_ROOT_ELEMENT);
                }
            }

            indentOut();

            printIndent();
            emitElementClosure(elementName);
            println();

        } else {
            endElementOpening(CLOSE_WHOLLY);
            println();
        }
    }

    public void baseEmit(List rootElements)
    {
        extension rootExtension = (extension) rootElements.iterator().next();

        emitExtension(rootExtension);
    }
}
