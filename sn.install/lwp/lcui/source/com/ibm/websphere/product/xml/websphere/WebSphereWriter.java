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
 * WebSphere Writer
 *
 * History 1.2, 9/26/03
 *
 * 25-Aug-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class WebSphereWriter extends BaseWriter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public WebSphereWriter()
    {
        super();
    }

    public String getDefaultDocTypeString(List rootElements)
    {
        return "<!DOCTYPE websphere SYSTEM \"websphere.dtd\">";
    }

    public void emitWebSphere(websphere websphere)
    {
        beginDocument();

        printIndent();
        beginElementOpening(WebSphereHandler.WEBSPHERE_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(WebSphereHandler.WEBSPHERE_NAME_FIELD_TAG, websphere.getName());

        printIndent();
        emitAttribute(WebSphereHandler.WEBSPHERE_VERSION_FIELD_TAG, websphere.getVersion());

        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void baseEmit(List rootElements)
    {
        websphere rootWebsphere = (websphere) rootElements.iterator().next();

        emitWebSphere(rootWebsphere);
    }
}
