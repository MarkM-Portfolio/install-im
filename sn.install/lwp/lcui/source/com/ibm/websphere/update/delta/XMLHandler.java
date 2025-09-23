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
package com.ibm.websphere.update.delta;

/*
 *  XML handler using SAX parser
 *
 *  also see XMLValidator.java
 *
 * History 1.4, 4/13/06
 *
 */
import java.util.*;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import com.ibm.websphere.product.WPProduct;

/**
 * XML content handler
 */
public class XMLHandler extends DefaultHandler
{
//    private static final String rPattern = "[a-zA-Z]";

    private static final String productprereq = "product-prereq";
    private static final String productcoreq = "product-coreq";
    private static final String productid = "product-id";
    private static final String buildversion = "build-version";
    private static final String buildlevel = "build-level";

    private String sProduct;
    private Set productList = new HashSet();
    private boolean bNoError = true;
    private boolean bValid = true;
    private boolean bEfixDriver = false;


    /**
     * tells the xml is valid or not
     * @return true or false
     */
    public boolean isbValid ()
    {
        return bNoError;
    }

   /**
     * Constructor
     */
    public XMLHandler()
    {
        // nothing to do here
    }

    /**
     * Start element, callback method
     * do what you need to do with each element here
     * currently it handles the product-id verification
     * @param uri       xml uri
     * @param local     xml local
     * @param raw       xml raw
     * @param attrs     xml attributes
     * @throws SAXException
     */
    public void startElement(String uri, String local, String raw, Attributes attrs) throws SAXException
    {
        if(raw.equalsIgnoreCase("efix-driver"))
        {
            bEfixDriver = true;
            System.out.println("driver file is for efix");
        }
        String attrQName;
        String attrValue;
        int length = attrs.getLength();

        for (int i = 0; i < length; i++)
        {
            attrQName = attrs.getQName(i);

            if(local.equalsIgnoreCase(productprereq) || local.equalsIgnoreCase(productcoreq))
            {
                if(attrQName.equalsIgnoreCase(productid))
                {
                    attrValue = attrs.getValue(i);
                    sProduct = attrValue;
                    bValid = checkProductid(attrValue);
                    // In the efixDriver only one product-prereq exist
                    // or multiple product-prereq but with a diff. version
                    // it is not allowed to have more than one product-id
                    // at the end of the xml document, we check the productList
                    // if it contains more than one product-id then the efixDriver
                    // is invalid.
                    if(local.equalsIgnoreCase(productprereq))
                    {
                        if(!productList.contains(sProduct.toLowerCase()))
                            productList.add(sProduct.toLowerCase());
                    }
                }
                else if(attrQName.equalsIgnoreCase(buildversion))
                {
                    attrValue = attrs.getValue(i);
                    bValid = checkBuildversion(attrValue);
                }
                else if(attrQName.equalsIgnoreCase(buildlevel))
                {
                    attrValue = attrs.getValue(i);
                    bValid = checkBuildlevel(attrValue);
                }
            }
            if(!bValid && bNoError)
                bNoError = false;
        }
    }


    public void endDocument()
    {
        if(bEfixDriver && productList.size() > 0 && productList.size() != 1)
        {
            bNoError = false;
            System.err.println("Error: more than one product-prereq with different product-id is detected.");
            System.out.println("\tOnly one product-prereq per efix OR");
            System.out.println("\tmultiple product-prereq with a same product-id per efix is allowed");
        }

    }
    public static final String PRODUCT_AND_PREFIX = "+";

    /**
     * checks the xml element value
     * checks product-id
     * @param value     the element value
     * @return  valid or invalid
     */
    private boolean checkProductid(String value)
    {
        boolean bValid = false;

        if(value != null)
        {
            if(value.equals("*") || value.trim().length()==0)
            {
                System.err.println("Error: asterisk (*) and empty value is not allowed for <product-id>.");
            }
            else    // check for valid products
            {
                for(int i=0; i<products.length; i++)
                {
                    // for coreq AND product
                    if(value.startsWith(PRODUCT_AND_PREFIX))
                        value = value.substring(PRODUCT_AND_PREFIX.length());

                    if(value.equalsIgnoreCase(products[i]))
                        bValid = true;
                }
                if(!bValid)
                {
                    System.err.println("Error: Invalid <product-id> value. It must be one of these products:");
                    printValidProduct();
                }
            }
        }
        return bValid;
    }

    /**
     * checks the xml element value
     * checks build-version
     * @param value     the element value
     * @return  valid or invalid
     */
    private boolean checkBuildversion(String value)
    {
        boolean bValid = false;

        if((value == null)  || (value.trim().length()==0) || value.equals("*"))
        {
            System.err.println("Error: asterisk (*) and empty value is not allowed for <build-version>.");

        }
        else
        {
            bValid = true;
            System.out.println("efix is set to install on build version "+value+" of product "+sProduct);
        }
        return bValid;
    }

    /**
     * checks the xml element value
     * checks build-level
     * @param value     the element value
     * @return  valid or invalid
     */
    private boolean checkBuildlevel(String value)
    {
        boolean bValid = false;

        if((value != null)  && (value.trim().length()!=0))
        {
            bValid = true;
            if(value.equals("*"))
            {
                System.out.println("efix is set to install on any build level of product "+sProduct);
            }
            else
            {
                 System.out.println("efix is set to install on build level "+value+" of product "+sProduct);
            }
        }
        else
        {
            System.err.println("Error: empty value is not allowed for <build-level>.");
        }
        return bValid;
    }

    /**
     * warning handler, callback method
     * @param e     SAXParseException
     * @throws SAXException
     */
    public void warning(SAXParseException e) throws SAXException
    {
        System.out.println("Warning: ");
        printInfo(e);
    }

    /**
     * error handler, callback method
     * @param e     SAXParseException
     * @throws SAXException
     */
    public void error(SAXParseException e) throws SAXException
    {
        bNoError = false;
        System.err.println("Error: ");
        printInfo(e);
    }

    /**
     * fatal error handler, callback method
     * @param e     SAXParseException
     * @throws SAXException
     */
    public void fatalError(SAXParseException e) throws SAXException
    {
        bNoError = false;
        System.err.println("Fatal error: ");
        printInfo(e);
    }

    private void printInfo(SAXParseException e)
    {
        System.out.println("   Public ID: "+e.getPublicId());
    	System.out.println("   System ID: "+e.getSystemId());
      	System.out.println("   Line number: "+e.getLineNumber());
      	System.out.println("   Column number: "+e.getColumnNumber());
      	System.out.println("   Message: "+e.getMessage());
    }

    private void printValidProduct()
    {
        System.out.print("** ");
        for(int i=0; i<products.length; i++)
            System.out.print("\"" +products[i]+"\" ");
        System.out.println("**");
    }

    // list of valid product_ids
    static String [] products = WPProduct.PRODUCT_IDS;

}
