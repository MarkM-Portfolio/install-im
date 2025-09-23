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
 * Event History Bean
 *
 * History 1.2, 9/26/03
 *
 * 30-Jun-2002 Initial Version
 */

import java.util.*;

import com.ibm.websphere.product.xml.*;

public class eventHistory extends BaseType
{
    // Program versioning ...

   public static final String pgmVersion = "1.2" ;
    // Program versioning ...

   public static final String pgmUpdate = "9/26/03" ;

    // Instantor ...

    public eventHistory()
    {
        super();

        this.updateEvents = new ArrayList();
    }

    // Event access ...

    protected ArrayList updateEvents;

    public Iterator getComponentEvents(String componentName)
    {
        ArrayList componentEvents = new ArrayList();

        int numEvents = getUpdateEventCount();

        for ( int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            updateEvent nextEvent = getUpdateEvent(eventNo);

            int numCEvents = nextEvent.getUpdateEventCount();

            for ( int cEventNo = 0; numCEvents < cEventNo; cEventNo++ ) {
                updateEvent nextCEvent = nextEvent.getUpdateEvent(cEventNo);

                if (nextCEvent.getId().equals(componentName))
                    componentEvents.add(nextCEvent);
            }
        }

        return componentEvents.iterator();
    }

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
}
