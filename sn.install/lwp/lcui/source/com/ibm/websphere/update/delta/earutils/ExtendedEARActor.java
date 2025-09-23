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

package com.ibm.websphere.update.delta.earutils;

/*
 *  @ (#) ExtendedEARActor.java
 *
 *  Perform EAR pre and post processing operations.
 *
 *  This actor works off of the extended update actions,
 *  accepting the command data as argument from the
 *  filter file.
 *
 *  The current operations are:
 *
 *  Pre:  Expand an EAR which is to be updated.
 *  Post: Collapse an EAR which has been updated.
 *
 *  Additional deployment steps may be neccesary.

 *  @author     venkataraman
 *  @created    November 20, 2002
 */

import java.io.*;
import java.util.*;

/**
 *  
 */
public class ExtendedEARActor extends EARActor
{
   public final static String pgmVersion = "1.2" ;
   public final static String pgmUpdate = "9/26/03" ;

    // Parsing support ...
    //
    // protected static boolean isTrue(String);
    // protected static boolean isFalse(String);

    protected static boolean isTrue(String tag)
    {
        return ( tag.equalsIgnoreCase("true") ||
                 tag.equalsIgnoreCase("yes")  ||
                 tag.equalsIgnoreCase("on")   ||
                 tag.equalsIgnoreCase("1") );
    }

    protected static boolean isFalse(String tag)
    {
        return ( tag.equalsIgnoreCase("false") ||
                 tag.equalsIgnoreCase("no")    ||
                 tag.equalsIgnoreCase("off")   ||
                 tag.equalsIgnoreCase("0") );
    }

    public static final String INSTALLABLE_ONLY_TAG = "installableOnly";

    protected static boolean isInstallableOnly(String tag)
    {
        return tag.equalsIgnoreCase(INSTALLABLE_ONLY_TAG);
    }

    public static final String APPLICATION_ONLY_TAG = "applicationOnly";

    protected static boolean isApplicationOnly(String tag)
    {
        return tag.equalsIgnoreCase(APPLICATION_ONLY_TAG);
    }

    // Top of the world: instiator
    //
    // public ExtendedEARActor(String, StringBuffer, StringBuffer,
    //                         String, boolean, boolean);
    // public ExtendedEARActor(String, StringBuffer, StringBuffer,
    //                         String, boolean, boolean, boolean, boolean);

    public ExtendedEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                            String earName,
                            boolean doDeploy, boolean doPluginDeploy)
    {
        this(installPath,
             messages, errors,
             earName,
             doDeploy, doPluginDeploy,
             false, false);
    }
    
    public ExtendedEARActor(String installPath, StringBuffer messages, StringBuffer errors,
                            String earName,
                            boolean doDeploy, boolean doPluginDeploy,
                            boolean installableOnly, boolean applicationOnly)
    {
        super(installPath, messages, errors);

        setCommandData(earName, doDeploy, doPluginDeploy, installableOnly, applicationOnly);
    }
    
    // Command access:
    //
    // protected EARCommandData commandDatum;
    // protected Vector commandData;
    //
    // protected setCommandData(String, boolean, boolean);
    // protected EARCommandData getCommandDatum();
    // protected Vector getCommandData();

    protected EARCommandData commandDatum = null;
    protected Vector commandData = null;

    protected void setCommandData(String earName,
				  boolean doDeploy, boolean doPluginDeploy,
				  boolean installableOnly, boolean applicationOnly)
    {
        commandDatum = new EARCommandData(earName,
					  doDeploy, doPluginDeploy,
					  installableOnly, applicationOnly);
        
        commandData = new Vector();
        commandData.addElement(commandDatum);
    }

    /**
	 * @return  the commandDatum
	 * @uml.property  name="commandDatum"
	 */
    public EARCommandData getCommandDatum()
    {
        return commandDatum;
    }

    /**
	 * @return  the commandData
	 * @uml.property  name="commandData"
	 */
    public Vector getCommandData()
    {
        return commandData;
    }
}
