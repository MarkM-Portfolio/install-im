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
 * Component Applied Bean
 *
 * History 1.2, 9/26/03
 *
 * 01-Jul-2002 Initial Version
 */

import com.ibm.websphere.product.xml.*;
import java.util.*;

/**
 *  
 */
public class componentApplied extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public componentApplied()
    {
        super();
    }

    protected componentVersion initialVersion;

    /**
	 * @param initialVersion  the initialVersion to set
	 * @uml.property  name="initialVersion"
	 */
    public void setInitialVersion(componentVersion initialVersion)
    {
        this.initialVersion = initialVersion;
    }

    /**
	 * @return  the initialVersion
	 * @uml.property  name="initialVersion"
	 */
    public componentVersion getInitialVersion()
    {
        return initialVersion;
    }

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
        this.updateType = (( updateType == null ) ? null : updateType.toString() );
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
        return enumUpdateType.selectUpdateType(updateType);
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

    protected String logName;

    /**
	 * @param logName  the logName to set
	 * @uml.property  name="logName"
	 */
    public void setLogName(String logName)
    {
        this.logName = logName;
    }

    /**
	 * @return  the logName
	 * @uml.property  name="logName"
	 */
    public String getLogName()
    {
        return logName;
    }

    protected String backupName;

    /**
	 * @param backupName  the backupName to set
	 * @uml.property  name="backupName"
	 */
    public void setBackupName(String backupName)
    {
        this.backupName = backupName;
    }

    /**
	 * @return  the backupName
	 * @uml.property  name="backupName"
	 */
    public String getBackupName()
    {
        return backupName;
    }

    String timeStamp;

    public void setTimeStamp(Calendar timeStamp)
    {
        this.timeStamp = calendarToString(timeStamp);
    }

    /**
	 * @param timeStamp  the timeStamp to set
	 * @uml.property  name="timeStamp"
	 */
    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public Calendar getTimeStampAsCalendar()
    {
        return stringToCalendar(timeStamp);
    }

    /**
	 * @return  the timeStamp
	 * @uml.property  name="timeStamp"
	 */
    public String getTimeStamp()
    {
        return timeStamp;
    }
}
