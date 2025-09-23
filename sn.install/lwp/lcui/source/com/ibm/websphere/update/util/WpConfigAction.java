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
package com.ibm.websphere.update.util;

/*
 *  @ (#) WpConfigAction.java
 *
 *  invoke WPSconfig to run WP config ant task
 *
 *  WARNING!  use sparingly, much better to defer complex config tasks until after eFix/PTF is applied
 *  REMEMBER: WPS must be stopped during iFix/PTF
 *
 *  @author     Ernest E Davis
 *  @created    16-September-2003
 */

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.delta.earutils.EARActor;
import com.ibm.websphere.update.util.WPConfig;
import com.ibm.websphere.update.util.WPConstants;
import java.io.*;
import java.util.*;
import java.util.Vector;

/**
 *  
 */
public class WpConfigAction extends ExtendedUpdateAction
{
    public static final String pgmVersion = "1.2" ;
    public static final String pgmUpdate = "1/29/04" ;

    public WpConfigAction()
    {
        super();
    }

    // Similar to the superclass defined method, but with
    // an added arguments vector.

    public int process(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errors,
                       boolean debug,
                       Vector args)
    {
        this.messages = messages;
        this.errors = errors;

        log("Performing WPSconfig operation:");

        //LL to allow prepending bat/sh script name to args
        LinkedList argsLL = new LinkedList();

        log("Running");

        try {
           ExecCmd exec = new ExecCmd(false); // Don't adjust, cmd /c not needed on win to run WPSconfig.bat

           String binPath = WPConfig.getProperty( WPConstants.PROP_WP_HOME ) + File.separator + "config";
           String cmdPath;
           if ( com.ibm.websphere.update.util.PlatformUtils.isWindows() )
              cmdPath = binPath + File.separator + "WPSconfig.bat";
           else
              cmdPath = binPath + File.separator + "WPSconfig.sh";

           //add command to run to LL
           argsLL.addFirst( cmdPath );
           log("WPSconfig Path: " + cmdPath);

           //add arguments from filter file (through args[])
           for ( int argNo = 0; argNo < args.size(); argNo++ ){
               log("  >> [" + argNo + "]: " + args.elementAt(argNo));
               //System.out.println("  >> [" + argNo + "]: " + args.elementAt(argNo));
               argsLL.add(args.elementAt(argNo));
           }

           //run command using LL
           String[] cmd = (String[])argsLL.toArray( new String[0] );

           Vector results = new Vector();
           Vector logResults = new Vector();

           int resultCode = exec.Execute(cmd,
                                         ExecCmd.DONT_ECHO_STDOUT,
                                         ExecCmd.DONT_ECHO_STDERR,
                                         results, logResults);

           return resultCode;

        } catch (Exception ex) {
           logError("WPSconfig: Failed: " + argsLL.toString() );
           logError("Exception: " + ex);
           return -255;
        }
    }

    protected StringBuffer messages;

    /**
	 * @return  the messages
	 * @uml.property  name="messages"
	 */
    public StringBuffer getMessages()
    {
       return this.messages;
    }

    public void log(String text)
    {
       getMessages().append(text + EARActor.lineSeparator);
    }

    protected StringBuffer errors;

    /**
	 * @return  the errors
	 * @uml.property  name="errors"
	 */
    public StringBuffer getErrors()
    {
       return this.errors;
    }

    public void logError(String text)
    {
       getErrors().append(text + EARActor.lineSeparator);
    }

}
