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
package com.ibm.websphere.update.efix.prereq;

import com.ibm.lconn.wizard.common.Constants;

/*
 *  Common prereq check used by both efix and ptf installer
 *
 * History 1.2, 4/27/06
 *
 */

public class CommonPrereqChecker
{
    // list of valid product_ids
     static String [] products = com.ibm.websphere.product.WPProduct.PRODUCT_IDS;

     private static void printValidProduct()
     {
         System.out.print("** ");
         for(int i=0; i<products.length; i++)
             System.out.print("\"" +products[i]+"\" ");
         System.out.println("**");
     }

    public static final String PRODUCT_AND_PREFIX = "+";

      /**
      * checks the xml element value
      * checks product-id
      * @param value     the element value
      * @return  valid or invalid
      */
     public static boolean checkProductid(String value)
     {
         boolean bValid = false;

         if(value != null)
         {
             if(value.equals("*") || value.trim().length()==0)
             {
                 System.err.println(EFixPrereqException.getString("error.title") + ": " +
                            EFixPrereqException.getString("attribute.value.not.allowed", value, "<product-id>"));
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
                     System.err.println(EFixPrereqException.getString("error.title") + ": " +
                             EFixPrereqException.getString("attribute.invalid.value", "<product-id>", value));
                 //    printValidProduct();
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
     public static boolean checkBuildversion(String value)
     {
         boolean bValid = true;

         if(null == value  || value.trim().length()==0 || value.equals("*"))
         {
             bValid = false;
             System.err.println(EFixPrereqException.getString("error.title") + ": " +
                     EFixPrereqException.getString("attribute.value.required", "<build-version>"));
             return bValid;
         }
         if(Constants.VERSION_TOP.compareTo(value) < 0){
        	 bValid = false;
        	 return bValid;
         }
         
         return bValid;
     }

     /**
      * checks the xml element value
      * checks build-level
      * @param value     the element value
      * @return  valid or invalid
      */
     public static boolean checkBuildlevel(String value)
     {
         boolean bValid = true;

         if((null == value)  || (value.trim().length()==0))
         {
             bValid = false;
             System.err.println(EFixPrereqException.getString("error.title") + ": " +
                     EFixPrereqException.getString("attribute.value.required", "<build-level>"));
         }
         return bValid;
     }

}
