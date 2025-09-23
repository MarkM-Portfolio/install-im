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

package com.ibm.websphere.product.history.xml;

/*
 * PTF Applied Bean
 *
 * History 1.2, 9/26/03
 *
 * 01-Jul-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.WPProduct;

public class ptfApplied extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public ptfApplied()
    {
        super();

        this.componentApplieds = new ArrayList();
        this.configApplieds = new ArrayList();
    }

    // ComponentApplied access ...

    protected ArrayList componentApplieds;

    public void addComponentApplied(componentApplied componentApplied)
    {
        componentApplieds.add(componentApplied);
    }

    public componentApplied removeComponentApplied(String componentName)
    {
        int componentCount = getComponentAppliedCount();

        componentApplied selectedApplied = null;
        int compNo = 0;

        while ( (selectedApplied == null) && (compNo < componentCount) ) {
            componentApplied nextApplied = getComponentApplied(compNo);

            if ( nextApplied.getComponentName().equals(componentName) )
                selectedApplied = nextApplied;
            else
                compNo++;
        }

        if ( selectedApplied != null )
            removeComponentApplied(compNo);

        return selectedApplied;
    }

    public componentApplied getComponentApplied(int index)
    {
        return (componentApplied) componentApplieds.get(index);
    }

    public int getComponentAppliedCount()
    {
        return componentApplieds.size();
    }

    public void removeComponentApplied(int index)
    {
        componentApplieds.remove(index);
    }
    
    public componentApplied selectComponentApplied(String componentName)
    {
        int componentCount = getComponentAppliedCount();

        componentApplied selectedApplied = null;

        for ( int compNo = 0;
              (selectedApplied == null) && (compNo < componentCount);
              compNo++ ) {

            componentApplied nextApplied = getComponentApplied(compNo);

            if ( nextApplied.getComponentName().equals(componentName) )
                selectedApplied = nextApplied;
        }

        return selectedApplied;
    }

    public int GetComponentAppliedProduct()
    {
      int componentCount = getComponentAppliedCount();

      int productsUpdatedCount = 0;

      componentApplied selectedApplied = null;
      componentVersion initialProd = null;

      String productName = null;

      String[] products = WPProduct.PRODUCT_IDS;

      for ( int compNo = 0;
            compNo < componentCount;
            compNo++ ) { 

          componentApplied nextApplied = getComponentApplied(compNo);

          if ( nextApplied.getLogName().equals("-") )
          {  
              initialProd = nextApplied.getInitialVersion();
              if (initialProd != null) { 
                  productName = initialProd.getComponentName();
                  if (productName != null) {  

                      for (int idNo = 0; idNo < products.length; idNo++) { 
                          if (productName.equals(products[idNo])) {  
                              //System.out.println("Update this product at end of uninstall " + productName);
                              productsUpdatedCount++;

                          }     
                      }   

                  }  
              }  
          } 
              // productsUpdatedCount++;
      }  

      return productsUpdatedCount;
    }


    // ConfigApplied access ...

    protected ArrayList configApplieds;

    public void addConfigApplied(configApplied configApplied) {
        configApplieds.add(configApplied);
    }

    public configApplied removeConfigApplied(String configName) {
        int configCount = getConfigAppliedCount();

        configApplied selectedApplied = null;
        int compNo = 0;

        while ( (selectedApplied == null) && (compNo < configCount) ) {
            configApplied nextApplied = getConfigApplied(compNo);

            if ( nextApplied.getConfigName().equals(configName) )
                selectedApplied = nextApplied;
            else
                compNo++;
        }

        if ( selectedApplied != null )
            removeConfigApplied(compNo);

        return selectedApplied;
    }

    public configApplied getConfigApplied(int index)
    {
        return (configApplied) configApplieds.get(index);
    }

    public int getConfigAppliedCount()
    {
        return configApplieds.size();
    }

    public void removeConfigApplied(int index)
    {
        configApplieds.remove(index);
    }
    
    public configApplied selectConfigApplied(String configName)
    {
        int configCount = getConfigAppliedCount();

        configApplied selectedApplied = null;

        for ( int compNo = 0;
              (selectedApplied == null) && (compNo < configCount);
              compNo++ ) {

            configApplied nextApplied = getConfigApplied(compNo);

            if ( nextApplied.getConfigName().equals(configName) )
                selectedApplied = nextApplied;
        }

        return selectedApplied;
    }

    protected String ptfId;

    public void setPTFId(String ptfId)
    {
        this.ptfId = ptfId;
    }
    
    public String getPTFId()
    {
        return ptfId;
    }
    
    /**
     * Answer the standard file name for storing the receiver.
     *
     * @return String The name for storage.
     */

    public String getStandardFileName()
    {
        return AppliedHandler.getStandardPTFAppliedFileName( getPTFId() );
    }
}
