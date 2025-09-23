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
 * PTF base Installer
 *
 * History 1.2, 10/30/03
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

import com.ibm.websphere.update.delta.*;

public abstract class PTFBaseInstaller
    extends ImageBaseInstaller
{
    // Program versioning ...

    public static final String pgmVersion = "1.2" ;
    // Program versioning ...

    public static final String pgmUpdate = "10/30/03" ;

    // Instantor ...
    //
    // public PTFBaseInstaller(WASProduct, WASHistory, Notifier, IOService);

    // Create a base ptf image installer/uninstaller.  This superclass provides
    // common function for ptf installation and uninstallation for a single ptf.
    //
    // The product and history objects provide the context for installation activity.
    // The notifier is used to provide live notification of installation activity.

    public PTFBaseInstaller(WPProduct wpsProduct, WPHistory wpsHistory,
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
        return ptfIsPresent( getUpdateId() );
    }

    protected boolean updateApplicationIsPresent()
    {
        return ptfApplicationIsPresent( getUpdateId() );
    }

    protected Object getUpdateById()
    {
        return getPTFById( getUpdateId() );
    }

    protected Object getUpdateDriverById()
    {
        return getPTFDriverById( getUpdateId() );
    }

    // Labelling Abstract Access ...
    // 
    // Answer a label to describe the image type; either 'EFix' or 'PTF'.
    //
    //    abstract String getUpdateTypeName();

    protected String getImageTypeName()
    {
        return "PTF";
    }

    // PTF banners ...
    //
    // String getPreparePTFBanner();
    // String getInstallingPTFBanner(String);
    // String getUninstallingPTFBanner(String);
    // String getCompletePTFBanner();

    protected String getPreparePTFBanner()
    {
        return getString("prepare.ptf.banner", getUpdateId());
    }

    protected String getInstallingPTFBanner(String componentName)
    {
        return getString("installing.ptf.banner", getUpdateId(), componentName);
    }

    protected String getUninstallingPTFBanner(String componentName)
    {
        return getString("uninstalling.ptf.banner", getUpdateId(), componentName);
    }

    protected String getCompletePTFBanner()
    {
        return getString("complete.ptf.banner", getUpdateId());
    }
    
    protected String getProductCleanupPTFBanner()
    {
        return getString("product.cleanup.ptf.banner", getUpdateId());
    }
    
    
    protected boolean setScriptPermissions(String file, String chmodValue) {
		boolean taskResult = true;

		String task = "chmod " + chmodValue + " " + file;

		Vector results = new Vector();
		Vector logBuffer = new Vector();

		ExecCmd exec = new ExecCmd();
		int rc = exec.Execute(task,
				      ExecCmd.DONT_ECHO_STDOUT, ExecCmd.DONT_ECHO_STDERR,
				      results, logBuffer);

		if (rc != 0)
			taskResult = false;

		return taskResult;
    }

    protected updateEvent createUpdateEvent()
    {
        updateEvent event = getWPHistory().getHistory().addUpdateEvent();

        event.setEventType( enumEventType.PTF_EVENT_TYPE );

        event.setId( getUpdateId() );
        event.setUpdateAction( getUpdateAction() );
        event.setUpdateType( enumUpdateType.COMPOSITE_UPDATE_TYPE );

        event.setStartTimeStamp();
        event.setStandardLogName( getWPProduct().getLogDirName() );

        return event;
    }
}
