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
 * Event Handler
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 */

import java.util.*;

import org.xml.sax.InputSource;

import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.history.xml.componentVersion;

public class EventWriter extends BaseWriter
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    public String getDefaultDocTypeString(List rootElements)
    {
        return "<!DOCTYPE event-history SYSTEM \"eventHistory.dtd\">";
    }

    public void baseEmit(List rootElements)
    {
        eventHistory rootHistory = (eventHistory) rootElements.iterator().next();

        emitEventHistory(rootHistory);
    }

    public void emitEventHistory(eventHistory history)
    {
        beginDocument();

        printIndent();
        beginElementOpening(EventHandler.EVENT_HISTORY_ELEMENT_NAME);
        endElementOpening(IS_INCOMPLETE);
        println();

        indentIn();

        int numEvents = history.getUpdateEventCount();
        for (int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            updateEvent nextEvent = history.getUpdateEvent(eventNo);
            emitUpdateEvent(nextEvent);
        }

        indentOut();

        printIndent();
        emitElementClosure(EventHandler.EVENT_HISTORY_ELEMENT_NAME);
        println();
    }

    public void emitUpdateEvent(updateEvent event)
    {
        printIndent();
        beginElementOpening(EventHandler.UPDATE_EVENT_ELEMENT_NAME);
        println();

        indentIn();

        emitAttributeOnLine(EventHandler.EVENT_TYPE_FIELD_TAG, event.getEventType());
        emitAttributeOnLine(EventHandler.PARENT_ID_FIELD_TAG, event.getParentId());
        emitAttributeOnLine(EventHandler.ID_FIELD_TAG, event.getId());

        emitAttributeOnLine(EventHandler.IS_REQUIRED_FIELD_TAG, event.getIsRequired());

        emitAttributeOnLine(EventHandler.IS_CUSTOM_FIELD_TAG, event.getIsCustom());
        emitAttributeOnLine(EventHandler.PRIMARY_CONTENT_FIELD_TAG, event.getPrimaryContent());

        emitAttributeOnLine(EventHandler.UPDATE_ACTION_FIELD_TAG, event.getUpdateAction());
        emitAttributeOnLine(EventHandler.UPDATE_TYPE_FIELD_TAG, event.getUpdateType());

        emitAttributeOnLine(EventHandler.IS_EXTERNAL_FIELD_TAG, event.getIsExternal());

        emitAttributeOnLine(EventHandler.ROOT_PROPERTY_FILE_FIELD_TAG, event.getRootPropertyFile());
        emitAttributeOnLine(EventHandler.ROOT_PROPERTY_NAME_FIELD_TAG, event.getRootPropertyName());
        emitAttributeOnLine(EventHandler.ROOT_PROPERTY_VALUE_FIELD_TAG, event.getRootPropertyValue());

        emitAttributeOnLine(EventHandler.LOG_NAME_FIELD_TAG, event.getLogName());
        emitAttributeOnLine(EventHandler.BACKUP_NAME_FIELD_TAG, event.getBackupName());

        emitAttributeOnLine(EventHandler.START_TIME_STAMP_FIELD_TAG, event.getStartTimeStamp());
        emitAttributeOnLine(EventHandler.END_TIME_STAMP_FIELD_TAG, event.getEndTimeStamp());

        emitAttributeOnLine(EventHandler.RESULT_FIELD_TAG, event.getResult());

        printIndent();
        emitAttribute(EventHandler.RESULT_MESSAGE_FIELD_TAG, event.getResultMessage());
        endElementOpening(IS_INCOMPLETE);
        println();

        int numEvents = event.getUpdateEventCount();
        for (int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            updateEvent nextEvent = event.getUpdateEvent(eventNo);
            emitUpdateEvent(nextEvent);
        }

        componentVersion initialVersion = event.getInitialVersion();
        if ( initialVersion != null )
            emitInitialVersion(initialVersion);

        componentVersion finalVersion = event.getFinalVersion();
        if ( finalVersion != null )
            emitFinalVersion(finalVersion);

        indentOut();

        printIndent();
        emitElementClosure(EventHandler.UPDATE_EVENT_ELEMENT_NAME);
        println();
    }

    public void emitInitialVersion(componentVersion initialVersion)
    {
        emitComponentVersion(EventHandler.INITIAL_VERSION_ELEMENT_NAME, initialVersion);
    }

    public void emitFinalVersion(componentVersion finalVersion)
    {
        emitComponentVersion(EventHandler.INITIAL_VERSION_ELEMENT_NAME, finalVersion);
    }

    public void emitComponentVersion(String elementName,
                                     componentVersion componentVersion)
    {
        printIndent();
        beginElementOpening(elementName);
        println();

        indentIn();

        emitAttributeOnLine(EventHandler.COMPONENT_NAME_FIELD_TAG, componentVersion.getComponentName());
        emitAttributeOnLine(EventHandler.SPEC_VERSION_FIELD_TAG, componentVersion.getSpecVersion());
        emitAttributeOnLine(EventHandler.BUILD_VERSION_FIELD_TAG, componentVersion.getBuildVersion());

        printIndent();
        emitAttribute(EventHandler.BUILD_DATE_FIELD_TAG, componentVersion.getBuildDate());
        endElementOpening(IS_COMPLETE);
        println();

        indentOut();
    }
}
