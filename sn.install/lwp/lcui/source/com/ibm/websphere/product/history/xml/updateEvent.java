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
 * Update Event Bean
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 */

import com.ibm.websphere.product.history.xml.componentVersion;
import com.ibm.websphere.product.history.xml.enumUpdateType;
import com.ibm.websphere.product.xml.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class updateEvent extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public updateEvent()
    {
        super();

        this.updateEvents = new ArrayList();
    }

    // Component event access ...

    protected ArrayList updateEvents;

    public updateEvent addUpdateEvent()
    {
        updateEvent newEvent = new updateEvent();

        addUpdateEvent(newEvent);

        return newEvent;
    }

    public void addUpdateEvent(updateEvent updateEvent)
    {
        updateEvents.add(updateEvent);
    }

    public updateEvent getUpdateEvent(int index)
    {
        return (updateEvent) updateEvents.get(index);
    }
    
    public int getUpdateEventCount()
    {
        return updateEvents.size();
    }
    
    public void removeUpdateEvent(int index)
    {
        updateEvents.remove(index);
    }   

    // Basic field access ...

    // See the field tags, above ...

    protected String eventType;

    /**
	 * @param eventType  the eventType to set
	 * @uml.property  name="eventType"
	 */
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public void setEventType(enumEventType eventType)
    {
        setEventType( (eventType == null) ? null : eventType.toString() );
    }

    /**
	 * @return  the eventType
	 * @uml.property  name="eventType"
	 */
    public String getEventType()
    {
        return eventType;
    }

    public enumEventType getEventTypeAsEnum()
    {
        return enumEventType.selectEventType(getEventType());
    }

    protected String parentId;

    /**
	 * @param parentId  the parentId to set
	 * @uml.property  name="parentId"
	 */
    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    /**
	 * @return  the parentId
	 * @uml.property  name="parentId"
	 */
    public String getParentId()
    {
        return parentId;
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

    protected String isRequired;

    public void setIsRequired(boolean isRequired)
    {
        this.isRequired = booleanToString(isRequired);
    }

    /**
	 * @param isRequired  the isRequired to set
	 * @uml.property  name="isRequired"
	 */
    public void setIsRequired(String isRequired)
    {
        this.isRequired = isRequired;
    }

    public boolean getIsRequiredAsBoolean()
    {
        return stringToBoolean(isRequired);
    }

    /**
	 * @return  the isRequired
	 * @uml.property  name="isRequired"
	 */
    public String getIsRequired()
    {
        return isRequired;
    }

    protected String isCustom;

    public void setIsCustom(boolean isCustom)
    {
        this.isCustom = booleanToString(isCustom);
    }

    /**
	 * @param isCustom  the isCustom to set
	 * @uml.property  name="isCustom"
	 */
    public void setIsCustom(String isCustom)
    {
        this.isCustom = isCustom;
    }

    public boolean getIsCustomAsBoolean()
    {
        return stringToBoolean(isCustom);
    }

    /**
	 * @return  the isCustom
	 * @uml.property  name="isCustom"
	 */
    public String getIsCustom()
    {
        return isCustom;
    }

    protected String startTimeStamp;

    public void setStartTimeStamp(Calendar startTimeStamp)
    {
        this.startTimeStamp = calendarToString(startTimeStamp);
    }

    public void setStartTimeStamp()
    {
        setStartTimeStamp( CalendarUtil.getTimeStamp() );
    }

    /**
	 * @param startTimeStamp  the startTimeStamp to set
	 * @uml.property  name="startTimeStamp"
	 */
    public void setStartTimeStamp(String startTimeStamp)
    {
        this.startTimeStamp = startTimeStamp;
    }

    public Calendar getStartTimeStampAsCalendar()
    {
        return stringToCalendar(startTimeStamp);
    }

    /**
	 * @return  the startTimeStamp
	 * @uml.property  name="startTimeStamp"
	 */
    public String getStartTimeStamp()
    {
        return startTimeStamp;
    }

    protected String endTimeStamp;

    public void setEndTimeStamp()
    {
        setEndTimeStamp( CalendarUtil.getTimeStamp() );
    }

    public void setEndTimeStamp(Calendar endTimeStamp)
    {
        this.endTimeStamp = calendarToString(endTimeStamp);
    }

    /**
	 * @param endTimeStamp  the endTimeStamp to set
	 * @uml.property  name="endTimeStamp"
	 */
    public void setEndTimeStamp(String endTimeStamp)
    {
        this.endTimeStamp = endTimeStamp;
    }

    public Calendar getEndTimeStampAsCalendar()
    {
        return stringToCalendar(endTimeStamp);
    }

    /**
	 * @return  the endTimeStamp
	 * @uml.property  name="endTimeStamp"
	 */
    public String getEndTimeStamp()
    {
        return endTimeStamp;
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

    protected String updateAction;

    /**
	 * @param updateAction  the updateAction to set
	 * @uml.property  name="updateAction"
	 */
    public void setUpdateAction(String updateAction)
    {
        this.updateAction = updateAction;
    }

    public void setUpdateAction(enumUpdateAction updateAction)
    {
        setUpdateAction( (updateAction == null) ? null : updateAction.toString() );
    }

    /**
	 * @return  the updateAction
	 * @uml.property  name="updateAction"
	 */
    public String getUpdateAction()
    {
        return updateAction;
    }

    public enumUpdateAction getUpdateActionAsEnum()
    {
        return enumUpdateAction.selectUpdateAction( getUpdateAction() );
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
        setUpdateType( (updateType == null) ? null : updateType.toString() );
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
        return enumUpdateType.selectUpdateType( getUpdateType() );
    }

    protected String isExternal;

    public void setIsExternal(boolean isExternal)
    {
        this.isExternal = booleanToString(isExternal);
    }

    /**
	 * @param isExternal  the isExternal to set
	 * @uml.property  name="isExternal"
	 */
    public void setIsExternal(String isExternal)
    {
        this.isExternal = isExternal;
    }

    public boolean getIsExternalAsBoolean()
    {
        return stringToBoolean(isExternal);
    }

    /**
	 * @return  the isExternal
	 * @uml.property  name="isExternal"
	 */
    public String getIsExternal()
    {
        return isExternal;
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

    // File naming constants ...

    public static final String LOG_FILE_EXTENSION = ".log" ;
    // File naming constants ...

    public static final String BACKUP_FILE_EXTENSION = "_undo.jar" ;

    protected String logName;

    public void setStandardLogName(String logDirName)
    {
        setLogName(logDirName + File.separator + getStandardLogName());
    }

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

    public String getStandardLogName()
    {
        String
            useStartTimeStamp = getStartTimeStamp(),
            fileFragment = CalendarUtil.fileFormat(useStartTimeStamp);

        String
            useParentId = getParentId();

        if ( useParentId == null )
            useParentId = "";
        else
            useParentId = useParentId + "_";

        return
            fileFragment + "_" +
            useParentId +
            getId() + "_" +
            getUpdateActionAsEnum().toString() +
            LOG_FILE_EXTENSION;
    }

    protected String backupName;

    public String getStandardBackupName()
    {
        String
            useStartTimeStamp = getStartTimeStamp(),
            fileFragment = CalendarUtil.fileFormat(useStartTimeStamp);

        String
            useParentId = getParentId();

        if ( useParentId == null )
            useParentId = "";
        else
            useParentId = useParentId + "_";

        return
            fileFragment + "_" +
            useParentId +
            getId() +
            BACKUP_FILE_EXTENSION;
    }

    public void setStandardBackupName(String backupDirName)
    {
        setBackupName(backupDirName + File.separator + getStandardBackupName());
    }

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

    protected String result;
    
    public void setSucceeded()
    {
        setResult(enumEventResult.SUCCEEDED_EVENT_RESULT);
    }

    public void setFailed()
    {
        setResult(enumEventResult.FAILED_EVENT_RESULT);
    }

    public void setCancelled()
    {
        setResult(enumEventResult.CANCELLED_EVENT_RESULT);
    }

    public void setResult(enumEventResult result)
    {
        setResult( (result == null) ? null : result.toString() );
    }

    /**
	 * @param result  the result to set
	 * @uml.property  name="result"
	 */
    public void setResult(String result)
    {
        this.result = result;
    }

    public boolean succeeded()
    {
        return ( getResultAsEnum() == enumEventResult.SUCCEEDED_EVENT_RESULT );
    }

    public boolean failed()
    {
        return ( getResultAsEnum() == enumEventResult.FAILED_EVENT_RESULT );
    }

    public boolean wasCancelled()
    {
        return ( getResultAsEnum() == enumEventResult.CANCELLED_EVENT_RESULT );
    }

    public enumEventResult getResultAsEnum()
    {
        return enumEventResult.selectEventResult(getResult());
    }

    /**
	 * @return  the result
	 * @uml.property  name="result"
	 */
    public String getResult()
    {
        return result;
    }

    protected String resultMessage;

    /**
	 * @param resultMessage  the resultMessage to set
	 * @uml.property  name="resultMessage"
	 */
    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    /**
	 * @return  the resultMessage
	 * @uml.property  name="resultMessage"
	 */
    public String getResultMessage()
    {
        return resultMessage;
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
    
    public String toString(){
    	return "eventType: " + eventType + " parentId: " + parentId + " Id: " + id + " isRequired: " + isRequired + " isCustom: " + isCustom +
    	
    	" startTimeStamp: " + startTimeStamp + " endTimeStamp: " + endTimeStamp  + " primaryContent: " + primaryContent + " updateAction: " + updateAction + 
    	" updateType: " + updateType + " isExternal: " + isExternal  + " rootPropertyFile: " + rootPropertyFile + " logName: " + logName + " backupName: " + backupName + 
    	" result: " + result + " resultMessage: " + resultMessage + " initialVersion: " + initialVersion + 
    	" finalVersion: " +  finalVersion 
    	;
    }
    
}







