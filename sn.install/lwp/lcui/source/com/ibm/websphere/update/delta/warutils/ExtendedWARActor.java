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

package com.ibm.websphere.update.delta.warutils;

/*
 *  @ (#) ExtendedWARActor.java
 *
 *  Perform WAR pre and post processing operations.
 *
 *  This actor works off of the extended update actions,
 *  accepting the command data as argument from the
 *  filter file.
 *
 *  The current operations are:
 *
 *TODO update operations . . .
 *  Pre:  
 *  Post: 
 *
 *  Additional deployment steps may be neccesary.

 *  @author     eedavis
 *  @created    March 2005
 */

import java.util.*;

/**
 *  
 */
public class ExtendedWARActor extends WARActor
{
   public final static String pgmVersion = "1.1" ;
   public final static String pgmUpdate = "4/6/05" ;

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

    // Top of the world: instantiators

    /* 
     * this constructor is scoped 'public', and we expect implementors to call
     * this version.
     * 
     * It accepts only the arguments that underlying code supports changes to.
     * 
     * Note that it does not accept input for the boolean arguments, if function
     * is added to support those options, create new public constructor(s) to accept
     * and pass those arguments to the private constructor.
     * 
     */
    public ExtendedWARActor(String installPath,
                            StringBuffer messages, 
                            StringBuffer errors,
                            String warName,
                            String warInstallPath)
    {
        this(installPath,
             messages, 
             errors,
             warName,
             warInstallPath,
             false,             //'doDeploy' is unsupported 
             false,             //'doPluginDeploy' is not applicable to WARs
             true,              //only support 'installableOnly' for WARs
             false);            //only support 'installableOnly' for WARs
    }
    
    /* 
     * this constructor is scoped 'private' to prevent inadvertantly 
     * giving an unsupported value for 'doDeploy', 'doPluginDeploy', 'installableOnly', or 'applicationOnly'. 
     * 
     * If support is added for any of these, add new non-private constructors as needed to accept the new
     * options and pass them to the private constructor.
     * 
     * The unsupported options are being retained as package 'warutils' was derived from 'earutils' and
     * the thinking is to keep method signatures as close as possible and ease the adding of extended 
     * functions simpler in the future, at some cost of passing unused arguments.
     * 
     */
    private ExtendedWARActor(String installPath,        //this is the product install location 
                             StringBuffer messages, 
                             StringBuffer errors,
                             String warName,            //name of the WAR to update
                             String warInstallPath,     //location of the WAR to update (may not be in <installPath>/installableApps)
                             boolean doDeploy, 
                             boolean doPluginDeploy,
                             boolean installableOnly, 
                             boolean applicationOnly)
    {
        super(installPath, messages, errors);

        setCommandData(warName, warInstallPath, doDeploy, doPluginDeploy, installableOnly, applicationOnly);
    }
    
    // Command access:
    //
    // protected WARCommandData commandDatum;
    // protected Vector commandData;
    //
    // protected setCommandData(String, boolean, boolean);
    // protected WARCommandData getCommandDatum();
    // protected Vector getCommandData();

    protected WARCommandData commandDatum = null;
    protected Vector commandData = null;

    protected void setCommandData(String warName,
                                  String warInstallPath,
                                  boolean doDeploy, 
                                  boolean doPluginDeploy,
                                  boolean installableOnly, 
                                  boolean applicationOnly)
    {
        commandDatum = new WARCommandData(warName,
                                          warInstallPath,
                                          doDeploy, 
                                          doPluginDeploy,
                                          installableOnly, 
                                          applicationOnly);
        
        commandData = new Vector();
        commandData.addElement(commandDatum);
    }

    /**
	 * @return  the commandDatum
	 * @uml.property  name="commandDatum"
	 */
    public WARCommandData getCommandDatum()
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
