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
 * EFix and PTF Writer
 *
 * History 1.2, 9/26/03
 *
 * 25-Aug-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class EFixWriter extends BaseWriter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public EFixWriter()
    {
        super();
    }

    public String getDefaultDocTypeString(List rootElements)
    {
        Iterator roots = rootElements.iterator();
        if ( !roots.hasNext() )
            return null;

        Object firstRootElement = roots.next();

        if ( firstRootElement instanceof efix )
            return getDefaultEFixDocTypeString();
        else if ( firstRootElement instanceof ptf )
            return getDefaultPTFDocTypeString();
        else
            return null;
    }

    public String getDefaultEFixDocTypeString()
    {
        return "<!DOCTYPE efix SYSTEM \"update.dtd\">";
    }

    public String getDefaultPTFDocTypeString()
    {
        return "<!DOCTYPE ptf SYSTEM \"update.dtd\">";
    }

    public void emitEFix(efix efix)
    {
        beginDocument();

        printIndent();
        beginElementOpening(EFixHandler.EFIX_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(EFixHandler.ID_FIELD_TAG, efix.getId());
        emitAttributeOnLine(EFixHandler.SHORT_DESCRIPTION_FIELD_TAG, efix.getShortDescription());
        emitAttributeOnLine(EFixHandler.BUILD_VERSION_FIELD_TAG, efix.getBuildVersion());

        printIndent();
        emitAttribute(EFixHandler.BUILD_DATE_FIELD_TAG, efix.getBuildDate());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        int numNames = efix.getComponentNameCount();
        for (int nameNo = 0; nameNo < numNames; nameNo++ ) {
            String nextName = efix.getComponentName(nameNo);
            emitElement(EFixHandler.COMPONENT_NAME_ELEMENT_NAME, nextName);
        }

        indentOut();

        printIndent();
        emitElementClosure(EFixHandler.EFIX_ELEMENT_NAME);
        println();
    }

    public void emitPTF(ptf ptf)
    {
        beginDocument();

        printIndent();
        beginElementOpening(EFixHandler.PTF_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(EFixHandler.ID_FIELD_TAG, ptf.getId());
        emitAttributeOnLine(EFixHandler.SHORT_DESCRIPTION_FIELD_TAG, ptf.getShortDescription());
        emitAttributeOnLine(EFixHandler.BUILD_VERSION_FIELD_TAG, ptf.getBuildVersion());

        printIndent();
        emitAttribute(EFixHandler.BUILD_DATE_FIELD_TAG, ptf.getBuildDate());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        int numNames = ptf.getComponentNameCount();
        for (int nameNo = 0; nameNo < numNames; nameNo++ ) {
            String nextName = ptf.getComponentName(nameNo);
            emitElement(EFixHandler.COMPONENT_NAME_ELEMENT_NAME, nextName);
        }

        indentOut();

        printIndent();
        emitElementClosure(EFixHandler.PTF_ELEMENT_NAME);
        println();
    }

    public void baseEmit(List rootElements)
    {
        Object root = rootElements.iterator().next();

        if ( root instanceof efix )
            emitEFix((efix) root);
        else if ( root instanceof ptf )
            emitPTF((ptf) root);
    }
}
