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
 * Update Component Bean
 *
 * History 1.2, 9/26/03
 *
 * 20-Aug-2002 Initial Version
 *
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class componentUpdate extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public componentUpdate()
    {
        super();

        this.componentPrereqs = new ArrayList();
        this.customProperties = new ArrayList();
    }

    // Component prerequisite access ...

    protected ArrayList componentPrereqs;

    public int getComponentPrereqCount()
    {
        return componentPrereqs.size();
    }

    public void removeComponentPrereq(int index)
    {
        componentPrereqs.remove(index);
    }

    public void addComponentPrereq(componentVersion componentPrereq)
    {
        componentPrereqs.add(componentPrereq);
    }

    public componentVersion getComponentPrereq(int index)
    {
        return (componentVersion) componentPrereqs.get(index);
    }

    // Final version access ...

    protected componentVersion finalVersion;

    /**
	 * @param finalVersion  the finalVersion to set
	 * @uml.property  name="finalVersion"
	 */
    public void setFinalVersion(componentVersion finalVersion)
    {
        this.finalVersion = finalVersion;
    }

    /**
	 * @return  the finalVersion
	 * @uml.property  name="finalVersion"
	 */
    public componentVersion getFinalVersion()
    {
        return finalVersion;
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

    protected String componentName;

    /**
	 * @param componentName  the componentName to set
	 * @uml.property  name="componentName"
	 */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }
    
    /**
	 * @return  the componentName
	 * @uml.property  name="componentName"
	 */
    public String getComponentName()
    {
        return componentName;
    }

    protected String selectiveUpdate;
    /**
	 * @param selectiveUpdate  the selectiveUpdate to set
	 */
    public void setSelectiveUpdate(String selectiveUpdate)
    {
    	this.selectiveUpdate = selectiveUpdate;
    }
    /**
	 * @return  the selectiveUpdate
	 */
    public String getSelectiveUpdate()
    {
    	return selectiveUpdate;
    }
    
    protected String updateType;

    /**
	 * @param updateType  the updateType to set
	 * @uml.property  name="updateType"
	 */
    public void setUpdateType(String updateType)
    {
        this.updateType = updateType;
    }

    public void setUpdateType(enumUpdateType updateType)
    {
        this.updateType = ( (updateType == null) ? null : updateType.toString() );
    }

    /**
	 * @return  the updateType
	 * @uml.property  name="updateType"
	 */
    public String getUpdateType()
    {
        return updateType;
    }

    public enumUpdateType getUpdateTypeAsEnum()
    {
        return ( (updateType == null) ? null : enumUpdateType.selectUpdateType(updateType) );
    }
    
    protected String isRequired;

    /**
	 * @param isRequired  the isRequired to set
	 * @uml.property  name="isRequired"
	 */
    public void setIsRequired(String isRequired)
    {
        this.isRequired = isRequired;
    }

    public void setIsRequired(boolean isRequired)
    {
        this.isRequired = booleanToString(isRequired);
    }

    /**
	 * @return  the isRequired
	 * @uml.property  name="isRequired"
	 */
    public String getIsRequired()
    {
        return isRequired;
    }

    public boolean getIsRequiredAsBoolean()
    {
        return stringToBoolean(isRequired);
    }

    protected String isOptional;
    
    /**
	 * @param isOptional  the isOptional to set
	 * @uml.property  name="isOptional"
	 */
    public void setIsOptional(String isOptional)
    {
        this.isOptional = isOptional;
    }

    public void setIsOptional(boolean isOptional)
    {
        this.isOptional = booleanToString(isOptional);
    }

    /**
	 * @return  the isOptional
	 * @uml.property  name="isOptional"
	 */
    public String getIsOptional()
    {
        return isOptional;
    }

    public boolean getIsOptionalAsBoolean()
    {
        return stringToBoolean(isOptional);
    }

    protected String isRecommended;

    /**
	 * @param isRecommended  the isRecommended to set
	 * @uml.property  name="isRecommended"
	 */
    public void setIsRecommended(String isRecommended)
    {
        this.isRecommended = isRecommended;
    }

    public void setIsRecommended(boolean isRecommended)
    {
        this.isRecommended = booleanToString(isRecommended);
    }

    /**
	 * @return  the isRecommended
	 * @uml.property  name="isRecommended"
	 */
    public String getIsRecommended()
    {
        return isRecommended;
    }

    public boolean getIsRecommendedAsBoolean()
    {
        return stringToBoolean(isRecommended);
    }



    protected String isExternal;

    /**
	 * @param isExternal  the isExternal to set
	 * @uml.property  name="isExternal"
	 */
    public void setIsExternal(String isExternal)
    {
        this.isExternal = isExternal;
    }

    public void setIsExternal(boolean isExternal)
    {
        this.isExternal = booleanToString(isExternal);
    }

    /**
	 * @return  the isExternal
	 * @uml.property  name="isExternal"
	 */
    public String getIsExternal()
    {
        return isExternal;
    }

    public boolean getIsExternalAsBoolean()
    {
        return stringToBoolean(isExternal);
    }

    protected String rootPropertyFile;

    /**
	 * @param rootPropertyFile  the rootPropertyFile to set
	 * @uml.property  name="rootPropertyFile"
	 */
    public void setRootPropertyFile(String rootPropertyFile)
    {
        this.rootPropertyFile = rootPropertyFile;
    }

    /**
	 * @return  the rootPropertyFile
	 * @uml.property  name="rootPropertyFile"
	 */
    public String getRootPropertyFile()
    {
        return rootPropertyFile;
    }

    protected String rootPropertyName;

    /**
	 * @param rootPropertyName  the rootPropertyName to set
	 * @uml.property  name="rootPropertyName"
	 */
    public void setRootPropertyName(String rootPropertyName)
    {
        this.rootPropertyName = rootPropertyName;
    }

    /**
	 * @return  the rootPropertyName
	 * @uml.property  name="rootPropertyName"
	 */
    public String getRootPropertyName()
    {
        return rootPropertyName;
    }

    protected String rootPropertyValue;

    /**
	 * @param rootPropertyValue  the rootPropertyValue to set
	 * @uml.property  name="rootPropertyValue"
	 */
    public void setRootPropertyValue(String rootPropertyValue)
    {
        this.rootPropertyValue = rootPropertyValue;
    }

    /**
	 * @return  the rootPropertyValue
	 * @uml.property  name="rootPropertyValue"
	 */
    public String getRootPropertyValue()
    {
        return rootPropertyValue;
    }

    String isCustom;

    /**
	 * @param isCustom  the isCustom to set
	 * @uml.property  name="isCustom"
	 */
    public void setIsCustom(String isCustom)
    {
        this.isCustom = isCustom;
    }

    public void setIsCustom(boolean isCustom)
    {
        this.isCustom = booleanToString(isCustom);
    }

    /**
	 * @return  the isCustom
	 * @uml.property  name="isCustom"
	 */
    public String getIsCustom()
    {
        return isCustom;
    }

    public boolean getIsCustomAsBoolean()
    {
        return stringToBoolean(isCustom);
    }

    protected String primaryContent;

    /**
	 * @param primaryContent  the primaryContent to set
	 * @uml.property  name="primaryContent"
	 */
    public void setPrimaryContent(String primaryContent)
    {
        this.primaryContent = primaryContent;
    }
    
    /**
	 * @return  the primaryContent
	 * @uml.property  name="primaryContent"
	 */
    public String getPrimaryContent()
    {
        return primaryContent;
    }
    
    public String toString(){
    	return " componentPrereqs:" + componentPrereqs + " finalVersion:" + finalVersion + " customProperties:" + customProperties + 
    	       " componentName:" + componentName + " selectiveUpdate:" + selectiveUpdate + " updateType:" + updateType +
    	       " isRequired:" + isRequired +  " isOptional:" + isOptional + 
    	       " isRecommended:" + isRecommended + " isExternal:" + isExternal + 
    	       " rootPropertyFile:" + rootPropertyFile + " isCustom:" + isCustom + 
    	       " primaryContent:" + primaryContent; 
    }
}
