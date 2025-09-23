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
package com.ibm.websphere.update.ptf;

/*
 * EFix Base Installer
 *
 * History 1.2, 2/12/03
 *
 * 09-Jul-2002 Initial Version
 *
 * 16-Dec-2002 Split from 'efixBaseImageInstaller'.
 *             Modified to mesh with PTF function.
 */

import java.io.*;
import java.util.*;

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.xml.*;
import com.ibm.websphere.product.xml.efix.*;

import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;

import com.ibm.websphere.update.ioservices.*;
import com.ibm.websphere.update.ioservices.standard.*;

public abstract class EFixBaseInstaller
    extends ImageBaseInstaller
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "2/12/03" ;

    // Instantor ...
    //
    // public EFixBaseInstaller(WASProduct, WASHistory, Notifier, IOService);

    // Create a base efix image installer/uninstaller.  This superclass provides
    // common function for efix installation and uninstallation for a single efix.
    //
    // The product and history objects provide the context for installation activity.
    // The notifier is used to provide live notification of installation activity.

    public EFixBaseInstaller(WPProduct wpsProduct, WPHistory wpsHistory,
                             Notifier notifier, IOService ioService)
    {
        super(wpsProduct, wpsHistory, notifier, ioService);
    }

    // Update Access (Concrete) ...
    //
    //    boolean updateIsPresent();
    //    boolean updateApplicationIsPresent();
    //
    //    Object [efix] getUpdateById();
    //    Object [efixDriver] getUpdateDriverById();

    protected boolean updateIsPresent()
    {
        return efixIsPresent( getUpdateId() );
    }

    protected boolean updateApplicationIsPresent()
    {
        return efixApplicationIsPresent( getUpdateId() );
    }

    protected Object getUpdateById()
    {
        return getEFixById( getUpdateId() );
    }

    protected Object getUpdateDriverById()
    {
        return getEFixDriverById( getUpdateId() );
    }

    // Labelling Abstract Access ...
    // 
    // Answer a label to describe the image type; either 'EFix' or 'EFix'.
    //
    //    abstract String getUpdateTypeName();

    protected String getImageTypeName()
    {
        return "EFix";
    }

    protected updateEvent createUpdateEvent()
    {
        updateEvent event = getWPHistory().getHistory().addUpdateEvent();

        event.setEventType( enumEventType.EFIX_EVENT_TYPE );

        event.setId( getUpdateId() );
        event.setUpdateAction( getUpdateAction() );
        event.setUpdateType( enumUpdateType.COMPOSITE_UPDATE_TYPE );

        event.setStartTimeStamp();
        event.setStandardLogName( getWPProduct().getLogDirName() );

        return event;
    }
}
