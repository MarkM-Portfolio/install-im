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
 * Event Factory
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 *
 * 09-Oct-2002 Defect 147224: Not reading the update action field for parent events.
 *
 */

import java.util.*;

import org.xml.sax.*;

import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.history.xml.componentVersion;

public class EventHandler extends BaseHandler
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Class and element links ...

    public static final String EVENT_HISTORY_CLASS_NAME = "eventHistory" ;
    // Class and element links ...

    public static final String EVENT_HISTORY_ELEMENT_NAME = "event-history" ;
    // Class and element links ...

    public static final String UPDATE_EVENT_CLASS_NAME = "updateEvent" ;
    // Class and element links ...

    public static final String UPDATE_EVENT_ELEMENT_NAME = "update-event" ;
    // Class and element links ...

    public static final String INITIAL_VERSION_ELEMENT_NAME = "initial-version" ;
    // Class and element links ...

    public static final String FINAL_VERSION_ELEMENT_NAME = "final-version" ;

    // Package link ...

    public static final String
        HISTORY_PACKAGE_NAME = "com.ibm.websphere.product.history.xml";

    // Field tags ...

    public static final String EVENT_TYPE_FIELD_TAG = "event-type" ;
    // Field tags ...

    public static final String PARENT_ID_FIELD_TAG = "parent-id" ;
    // Field tags ...

    public static final String ID_FIELD_TAG = "id" ;
    // Field tags ...

    public static final String IS_REQUIRED_FIELD_TAG = "is-required" ;
    // Field tags ...

    public static final String IS_CUSTOM_FIELD_TAG = "is-custom" ;
    // Field tags ...

    public static final String PRIMARY_CONTENT_FIELD_TAG = "primary-content" ;
    // Field tags ...

    public static final String UPDATE_ACTION_FIELD_TAG = "update-action" ;
    // Field tags ...

    public static final String UPDATE_TYPE_FIELD_TAG = "update-type" ;
    // Field tags ...

    public static final String IS_EXTERNAL_FIELD_TAG = "is-external" ;
    // Field tags ...

    public static final String ROOT_PROPERTY_FILE_FIELD_TAG = "root-property-file" ;
    // Field tags ...

    public static final String ROOT_PROPERTY_NAME_FIELD_TAG = "root-property-name" ;
    // Field tags ...

    public static final String ROOT_PROPERTY_VALUE_FIELD_TAG = "root-property-value" ;
    // Field tags ...

    public static final String LOG_NAME_FIELD_TAG = "log-name" ;
    // Field tags ...

    public static final String BACKUP_NAME_FIELD_TAG = "backup-name" ;
    // Field tags ...

    public static final String START_TIME_STAMP_FIELD_TAG = "start-time-stamp" ;
    // Field tags ...

    public static final String END_TIME_STAMP_FIELD_TAG = "end-time-stamp" ;
    // Field tags ...

    public static final String RESULT_FIELD_TAG = "result" ;
    // Field tags ...

    public static final String RESULT_MESSAGE_FIELD_TAG = "result-message" ;
    // Field tags ...

    public static final String COMPONENT_NAME_FIELD_TAG = "component-name" ;
    // Field tags ...

    public static final String SPEC_VERSION_FIELD_TAG = "spec-version" ;
    // Field tags ...

    public static final String BUILD_VERSION_FIELD_TAG = "build-version" ;
    // Field tags ...

    public static final String BUILD_DATE_FIELD_TAG = "build-date" ;
    
    public static final String SELECTIVE_UPDATE_TAG = "selective-update" ;

    // event-type         (enum)       [required] The type of event: install, uninstall, selective
    // parent-id          (String)     [optional] An optional parent id.
    // id                 (String)     [required] The id of the update.

    // is-required        (boolean)    [optional] Tells if the event is for a required update.
    // is-custom          (boolean)    [optional] Tells if the event is a custom operation.

    // primary-content    (String)     [optional] Tells the content used to perform the update.
    // update-action      (enum)       [optional] One of add, replace, remove, or patch

    // is-external        (boolean)    [optional] Tells if the update applies to an external component

    // root-property-file (String)     [optional] The name of the file holding the component root.
    // root-property-name (String)     [optional] The name of the property holding the component root.
    // root-property-value (String)    [optional] The component root.

    // log-name           (String)     [required] The name of the log file.
    // backup-name        (String)     [optional] The name of the file containing backup info.

    // start-time-stamp   (TimeStamp)  [required] The starting date and time of this event.
    // end-time-stamp     (TimeStamp)  [optional] An optional ending date and time for this event.

    // result             (enum)       [optional] The result of the update.
    // result-message     (String)     [optional] A Message provided with status.

    // initialVersion     (complex)    [optional] The initial version.
    // finalVersion       (complex)    [optional] The final version.

    public EventHandler()
    {
        super();
    }

    // Factory operations ...

    protected Object createElement(String elementName,
                                   String parentElementName, Object parentElement,
                                   Attributes attributes)
        throws SAXParseException
    {
        Object element = null; // Default case: This is an error.

        if ( parentElement == null ) {
            if ( elementName.equals(EVENT_HISTORY_ELEMENT_NAME) ) {
                eventHistory typedElement = new eventHistory();
                element = typedElement;
            }

        } else if ( parentElementName.equals(EVENT_HISTORY_ELEMENT_NAME) ) {
            eventHistory typedParentElement = (eventHistory) parentElement;

            updateEvent typedElement = new updateEvent();

            typedElement.setEventType( getAttribute(attributes, EVENT_TYPE_FIELD_TAG, elementName, null) );
            typedElement.setId( getAttribute(attributes, ID_FIELD_TAG, elementName, null) );

            typedElement.setUpdateAction( getAttribute(attributes, UPDATE_ACTION_FIELD_TAG, elementName, null) );

            // Defect 147224: Start: Need to read and set the update-type attribute.
            typedElement.setUpdateType( getAttribute(attributes, UPDATE_TYPE_FIELD_TAG, elementName, null) );
            // Defect 147224: End

            typedElement.setLogName( getAttribute(attributes, LOG_NAME_FIELD_TAG, elementName, "") );
                    
            typedElement.setStartTimeStamp( getAttribute(attributes, START_TIME_STAMP_FIELD_TAG, elementName, null) );
            typedElement.setEndTimeStamp( getAttribute(attributes, END_TIME_STAMP_FIELD_TAG, elementName, "") );
                    
            typedElement.setResult( getAttribute(attributes, RESULT_FIELD_TAG, elementName, "") );
            typedElement.setResultMessage( getAttribute(attributes, RESULT_MESSAGE_FIELD_TAG, elementName, "") );
            
            typedParentElement.addUpdateEvent(typedElement);
            element = typedElement;

        } else if ( parentElementName.equals(UPDATE_EVENT_ELEMENT_NAME) ) {
            updateEvent typedParentElement = (updateEvent) parentElement;

            if ( elementName.equals(UPDATE_EVENT_ELEMENT_NAME) ) {
                updateEvent typedElement = new updateEvent();

                typedElement.setEventType( getAttribute(attributes, EVENT_TYPE_FIELD_TAG, elementName, null) );
                typedElement.setParentId( getAttribute(attributes, PARENT_ID_FIELD_TAG, elementName, null) );
                typedElement.setId( getAttribute(attributes, ID_FIELD_TAG, elementName, null) );

                typedElement.setIsRequired( getAttribute(attributes, IS_REQUIRED_FIELD_TAG, elementName, null) );
                typedElement.setIsCustom( getAttribute(attributes, IS_CUSTOM_FIELD_TAG, elementName, null) );

                typedElement.setPrimaryContent( getAttribute(attributes, PRIMARY_CONTENT_FIELD_TAG, elementName, null) );
                    
                typedElement.setUpdateAction( getAttribute(attributes, UPDATE_ACTION_FIELD_TAG, elementName, null) );
                typedElement.setUpdateType( getAttribute(attributes, UPDATE_TYPE_FIELD_TAG, elementName, null) );

                typedElement.setIsExternal( getAttribute(attributes, IS_EXTERNAL_FIELD_TAG, elementName, null) );
                typedElement.setRootPropertyFile( getAttribute(attributes, ROOT_PROPERTY_FILE_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyName( getAttribute(attributes, ROOT_PROPERTY_NAME_FIELD_TAG, elementName, "") );
                typedElement.setRootPropertyValue( getAttribute(attributes, ROOT_PROPERTY_VALUE_FIELD_TAG, elementName, "") );
                    
                typedElement.setLogName( getAttribute(attributes, LOG_NAME_FIELD_TAG, elementName, "") );
                typedElement.setBackupName( getAttribute(attributes, BACKUP_NAME_FIELD_TAG, elementName, "") );
                    
                typedElement.setStartTimeStamp( getAttribute(attributes, START_TIME_STAMP_FIELD_TAG, elementName, null) );
                typedElement.setEndTimeStamp( getAttribute(attributes, END_TIME_STAMP_FIELD_TAG, elementName, "") );
                    
                typedElement.setResult( getAttribute(attributes, RESULT_FIELD_TAG, elementName, "") );
                typedElement.setResultMessage( getAttribute(attributes, RESULT_MESSAGE_FIELD_TAG, elementName, "") );

                typedParentElement.addUpdateEvent(typedElement);
                element = typedElement;

            } else if ( elementName.equals(INITIAL_VERSION_ELEMENT_NAME) ) {
                componentVersion typedElement = new componentVersion();
                setAttributes(typedElement, elementName, attributes);
                typedParentElement.setInitialVersion(typedElement);
                element = typedElement;

            } else if ( elementName.equals(FINAL_VERSION_ELEMENT_NAME) ) {
                componentVersion typedElement = new componentVersion();
                setAttributes(typedElement, elementName, attributes);
                typedParentElement.setFinalVersion(typedElement);
                element = typedElement;
            }
        }

        if ( element == null )
            throw newInvalidElementException(parentElementName, elementName);
        else
            return element;
    }

    protected void setAttributes(componentVersion componentVersion,
                                 String elementName,
                                 Attributes attributes)
        throws SAXParseException
    {
        componentVersion.setComponentName( getAttribute(attributes, COMPONENT_NAME_FIELD_TAG, elementName, null) );
        componentVersion.setSpecVersion( getAttribute(attributes, SPEC_VERSION_FIELD_TAG, elementName, null) );
        componentVersion.setBuildVersion( getAttribute(attributes, BUILD_VERSION_FIELD_TAG, elementName, null) );
        componentVersion.setBuildDate( getAttribute(attributes, BUILD_DATE_FIELD_TAG, elementName, null) );
    }
}
