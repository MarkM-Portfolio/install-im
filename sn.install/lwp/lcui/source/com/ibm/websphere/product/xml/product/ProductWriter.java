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
 * Product Writer
 *
 * History 1.2, 9/26/03
 *
 * 25-Aug-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class ProductWriter extends BaseWriter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public ProductWriter()
    {
        super();
    }

    public String getDefaultDocTypeString(List rootElements)
    {
        return "<!DOCTYPE product SYSTEM \"product.dtd\">";
    }

    public void emitProduct(product product)
    {
        beginDocument();

        printIndent();
        beginElementOpening(ProductHandler.PRODUCT_ELEMENT_NAME);
        print(" ");
        emitAttribute(ProductHandler.PRODUCT_NAME_FIELD_TAG, product.getName());
        endElementOpening(CLOSE_PARTIALLY);
        println();

        indentIn();

        emitElement(ProductHandler.PRODUCT_ID_FIELD_TAG, product.getId());
        emitElement(ProductHandler.PRODUCT_VERSION_FIELD_TAG, product.getVersion());
        emitBuildInfo(product.getBuildInfo());

        indentOut();

        emitElementClosure(ProductHandler.PRODUCT_ELEMENT_NAME);
        println();
    }

    public void emitBuildInfo(buildInfo buildInfo)
    {
        printIndent();
        beginElementOpening(ProductHandler.PRODUCT_BUILD_INFO_FIELD_TAG);
        println();

        indentIn();

        emitAttributeOnLine(ProductHandler.BUILD_INFO_DATE_FIELD_TAG, buildInfo.getDate());

        printIndent();
        emitAttribute(ProductHandler.BUILD_INFO_LEVEL_FIELD_TAG, buildInfo.getLevel());
        endElementOpening(CLOSE_WHOLLY);
        println();

        indentOut();
    }

    public void baseEmit(List rootElements)
    {
        product rootProduct = (product) rootElements.iterator().next();

        emitProduct(rootProduct);
    }
}
