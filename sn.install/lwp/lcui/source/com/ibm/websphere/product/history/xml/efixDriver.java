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
 * EFix Driver Bean
 *
 * History 1.2, 9/26/03
 *
 * 12-Sep-2002 Initial Version
 */

import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;
import java.util.*;

/**
 *  
 */
public class efixDriver extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public efixDriver()
    {
        super();

        this.apars = new ArrayList();
        this.componentUpdates = new ArrayList();
        this.platformPrereqs = new ArrayList();
        this.productPrereqs = new ArrayList();
        this.productCoreqs = new ArrayList();
        this.efixPrereqs = new ArrayList();
        this.configTasks = new ArrayList();
        this.customProperties = new ArrayList();
    }

    // Apars access ...

    public String getAPARNumber()
    {
        if ( getAparInfoCount() == 0 )
            return "";

        aparInfo firstApar = getAparInfo(0);
        return firstApar.getNumber();
    }

    protected ArrayList apars;

    public void addAparInfo(aparInfo apar)
    {
        apars.add(apar);
    }
    
    public aparInfo getAparInfo(int index)
    {
        return (aparInfo) apars.get(index);
    }
    
    public int getAparInfoCount()
    {
        return apars.size();
    }

    public void removeAparInfo(int index)
    {
        apars.remove(index);
    }
    
    // Component access ...

    protected ArrayList componentUpdates;

    public void addComponentUpdate(componentUpdate componentUpdate)
    {
        componentUpdates.add(componentUpdate);
    }
    
    public componentUpdate getComponentUpdate(int index)
    {
        return (componentUpdate) componentUpdates.get(index);
    }
    
    public int getComponentUpdateCount()
    {
        return componentUpdates.size();
    }

    public void removeComponentUpdate(int index)
    {
        componentUpdates.remove(index);
    }

    public Vector getComponentNames()
    {
        Vector componentNames = new Vector();

        int componentCount = getComponentUpdateCount();

        for ( int compNo = 0; compNo < componentCount; compNo++ ) {
            componentUpdate nextComponent = getComponentUpdate(compNo);
            componentNames.addElement(nextComponent.getComponentName());
        }

        return componentNames;
    }

    // Prerequisite access ...

    protected ArrayList efixPrereqs;

    public void addEFixPrereq(efixPrereq efixPrereq)
    {
        efixPrereqs.add(efixPrereq);
    }

    public efixPrereq getEFixPrereq(int index)
    {
        return (efixPrereq) efixPrereqs.get(index);
    }
    
    public int getEFixPrereqCount()
    {
        return efixPrereqs.size();
    }

    public void removeEFixPrereq(int index)
    {
        efixPrereqs.remove(index);
    }

    // Platform prerequisite access ...

    protected ArrayList platformPrereqs;

    public void addPlatformPrereq(platformPrereq platformPrereq)
    {
        platformPrereqs.add(platformPrereq);
    }
    
    public platformPrereq getPlatformPrereq(int index)
    {
        return (platformPrereq) platformPrereqs.get(index);
    }
    
    public int getPlatformPrereqCount()
    {
        return platformPrereqs.size();
    }
    
    public void removePlatformPrereq(int index)
    {
        platformPrereqs.remove(index);
    }

    // Product prerequisite access ...

    protected ArrayList productPrereqs;

    public void addProductPrereq(productPrereq productPrereq)
    {
        productPrereqs.add(productPrereq);
    }

    public productPrereq getProductPrereq(int index)
    {
        return (productPrereq) productPrereqs.get(index);
    }

    public int getProductPrereqCount()
    {
        return productPrereqs.size();
    }

    public void removeProductPrereq(int index)
    {
        productPrereqs.remove(index);
    }

    // Product prerequisite access ...

    protected ArrayList productCoreqs;

    public void addProductCoreq(productCoreq productCoreq)
    {
        productCoreqs.add(productCoreq);
    }

    public productCoreq getProductCoreq(int index)
    {
        return (productCoreq) productCoreqs.get(index);
    }

    public int getProductCoreqCount()
    {
        return productCoreqs.size();
    }

    public void removeProductCoreq(int index)
    {
        productCoreqs.remove(index);
    }

    // Config tasks access ...

    protected ArrayList configTasks;

    public void addConfgiTask(configTask configtask)
    {
        configTasks.add(configtask);
    }

    public configTask getConfigTask(int index)
    {
        return (configTask) configTasks.get(index);
    }
    
    public int getConfigTaskCount()
    {
        return configTasks.size();
    }

    public void removeConfigTask(int index)
    {
        configTasks.remove(index);
    }


    // Custom property access ...

    protected ArrayList customProperties;

    public void addCustomProperty(customProperty customProperty)
    {
        customProperties.add(customProperty);
    }

    public customProperty getCustomProperty(int index)
    {
        return (customProperty) customProperties.get(index);
    }

    public int getCustomPropertyCount()
    {
        return customProperties.size();
    }

    public void removeCustomProperty(int index)
    {
        customProperties.remove(index);
    }

    protected String id;

    /**
	 * @param id  the id to set
	 * @uml.property  name="id"
	 */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
	 * @return  the id
	 * @uml.property  name="id"
	 */
    public String getId()
    {
        return id;
    }

    protected String shortDescription;

    /**
	 * @param shortDescription  the shortDescription to set
	 * @uml.property  name="shortDescription"
	 */
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    /**
	 * @return  the shortDescription
	 * @uml.property  name="shortDescription"
	 */
    public String getShortDescription()
    {
        return shortDescription;
    }

    protected String longDescription;

    /**
	 * @param longDescription  the longDescription to set
	 * @uml.property  name="longDescription"
	 */
    public void setLongDescription(String longDescription)
    {
        this.longDescription = longDescription;
    }

    /**
	 * @return  the longDescription
	 * @uml.property  name="longDescription"
	 */
    public String getLongDescription()
    {
        return longDescription;
    }

    protected String isTrial;

    public void setIsTrial(boolean isTrial)
    {
        this.isTrial = booleanToString(isTrial);
    }

    /**
	 * @param isTrial  the isTrial to set
	 * @uml.property  name="isTrial"
	 */
    public void setIsTrial(String isTrial)
    {
        this.isTrial = isTrial;
    }

    /**
	 * @return  the isTrial
	 * @uml.property  name="isTrial"
	 */
    public String getIsTrial()
    {
        return isTrial;
    }

    public boolean getIsTrialAsBoolean()
    {
        return stringToBoolean(isTrial);
    }

    protected String expirationDate;

    public void setExpirationDate(Calendar expirationDate)
    {
        this.expirationDate = calendarToSimpleString(expirationDate);
    }

    /**
	 * @param expirationDate  the expirationDate to set
	 * @uml.property  name="expirationDate"
	 */
    public void setExpirationDate(String expirationDate)
    {
        this.expirationDate = expirationDate;
    }

    /**
	 * @return  the expirationDate
	 * @uml.property  name="expirationDate"
	 */
    public String getExpirationDate()
    {
        return expirationDate;
    }

    public Calendar getExpirationDateAsCalendar()
    {
        return simpleStringToCalendar(expirationDate);
    }

    protected String buildVersion;

    /**
	 * @param buildVersion  the buildVersion to set
	 * @uml.property  name="buildVersion"
	 */
    public void setBuildVersion(String buildVersion)
    {
        this.buildVersion = buildVersion;
    }

    /**
	 * @return  the buildVersion
	 * @uml.property  name="buildVersion"
	 */
    public String getBuildVersion()
    {
        return buildVersion;
    }

    protected String buildDate;

    public void setBuildDate(Calendar buildDate)
    {
        this.buildDate = calendarToString(buildDate);
    }

    /**
	 * @param buildDate  the buildDate to set
	 * @uml.property  name="buildDate"
	 */
    public void setBuildDate(String buildDate)
    {
        this.buildDate = buildDate;
    }

    /**
	 * @return  the buildDate
	 * @uml.property  name="buildDate"
	 */
    public String getBuildDate()
    {
        return buildDate;
    }

    public Calendar getBuildDateAsCalendar()
    {
        return stringToCalendar(buildDate);
    }

    /**
     * Answer the standard file name for storing the receiver.
     *
     * @return String The name for storage.
     */

    public String getStandardFileName()
    {
        return AppliedHandler.getStandardEFixDriverFileName( getId() );
    }

    public efix cloneAsEFix()
    {
        efix partialClone = new efix();

        partialClone.setId( getId() );
        partialClone.setShortDescription( getShortDescription() );
        partialClone.setBuildVersion( getBuildVersion() );
        partialClone.setBuildDate( getBuildDate() );

        int numComps = getComponentUpdateCount();
        
        for ( int compNo = 0; compNo < numComps; compNo++ ) {
            componentUpdate nextUpdate = getComponentUpdate(compNo);
            String nextComponentName = nextUpdate.getComponentName();
            partialClone.addComponentName(nextComponentName);
        }

        return partialClone;
    }
    
    public String toString(){
    	return " ID:" + id + " Apars:" + apars + " ComponentUpdates:" + componentUpdates + 
    	       " EfixPrereqs:" + efixPrereqs + " PlatformPrereqs:" + platformPrereqs + " ProductPrereqs:" + productPrereqs +
    	       " ProductCoreqs:" + productCoreqs +  " ConfigTasks:" + configTasks + 
    	       " CustomProperties:" + customProperties + " ShortDescription:" + shortDescription + 
    	       " LongDescription:" + longDescription + " isTrial:" + isTrial + 
    	       " expirationDate:" + expirationDate +  " buildVersion:" + buildVersion + " BuildDate:" + buildDate; 
    }
}
