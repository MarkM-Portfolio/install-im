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
 * EFix Applied Bean
 *
 * History 1.2, 9/26/03
 *
 * 01-Jul-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class efixApplied extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public efixApplied()
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

    protected String efixId;

    public void setEFixId(String efixId)
    {
        this.efixId = efixId;
    }
    
    public String getEFixId()
    {
        return efixId;
    }
    
    /**
     * Answer the standard file name for storing the receiver.
     *
     * @return String The name for storage.
     */

    public String getStandardFileName()
    {
        return AppliedHandler.getStandardEFixAppliedFileName( getEFixId() );
    }
}
