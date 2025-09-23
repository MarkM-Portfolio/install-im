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
 * Component Writer
 *
 * History 1.2, 9/26/03
 *
 * 25-Aug-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class ComponentWriter extends BaseWriter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public ComponentWriter()
    {
        super();
    }

    public String getDefaultDocTypeString(List rootElements)
    {
        return "<!DOCTYPE component SYSTEM \"component.dtd\">";
    }

    public void emitComponent(component component)
    {
        beginDocument();

        printIndent();
        beginElementOpening(ComponentHandler.COMPONENT_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(ComponentHandler.COMPONENT_NAME_FIELD_TAG, component.getName());
        emitAttributeOnLine(ComponentHandler.COMPONENT_SPEC_VERSION_FIELD_TAG, component.getSpecVersion());
        emitAttributeOnLine(ComponentHandler.COMPONENT_BUILD_VERSION_FIELD_TAG, component.getBuildVersion());

        printIndent();
        emitAttribute(ComponentHandler.COMPONENT_BUILD_DATE_FIELD_TAG, component.getBuildDate());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void baseEmit(List rootElements)
    {
        component rootComponent = (component) rootElements.iterator().next();

        emitComponent(rootComponent);
    }
}

