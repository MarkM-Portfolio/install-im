/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */

package com.ibm.websphere.update.delta.earutils;

/*
 *  @ (#) ProductInterrogator.java
 *
 *  Class to examine and report on the products which are
 *  installed.
 *
 *  @author     venkataraman
 *  @created    November 20, 2002
 */

import com.ibm.websphere.product.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class ProductInterrogator
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Answer a new product interrogator on the
    // specified installation path.
    //
    // The installation path must be absolutely specified.

    public ProductInterrogator(String installPath)
    {
        this.installPath = installPath;
    }

    // Answer the install path bound into the receiver.

    protected String installPath;
    
    /**
	 * @return  the installPath
	 * @uml.property  name="installPath"
	 */
    public String getInstallPath()
    {
        return this.installPath;
    }

    // Answer the receiver's raw product.

    protected WASProduct product;

    /**
	 * @return  the product
	 * @uml.property  name="product"
	 */
    public WASProduct getProduct()
    {
        if ( product == null )
            product = createProduct();

        return product;
    }

    // Create a new product for the receiver.

    protected WASProduct createProduct()
    {
        return new WASProduct( getInstallPath() );
    }

    // Tell if the bound product is an embedded type product.

    public boolean isEmbedded()
    {
        WASProduct useProduct = getProduct();

        return ( useProduct.productPresent(WASProduct.PRODUCTID_EMBEDDED_EXPRESS) ||
                 useProduct.productPresent(WASProduct.PRODUCTID_EXPRESS) );
    }

    // Tell if the bound product is a base type product.

    public boolean isBase()
    {
        return ( getProduct().productPresent(WASProduct.PRODUCTID_BASE) );
    }

    // Tell if the bound product is an ND type product.

    public boolean isND()
    {
        return ( getProduct().productPresent(WASProduct.PRODUCTID_ND) );
    }

    // Tell if the bound product is a PME type product.

    public boolean isPME()
    {
        return ( getProduct().productPresent(WASProduct.PRODUCTID_PME) );
    }
}
