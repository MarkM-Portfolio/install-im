/*
********************************************************************
* IBM Confidential                                                 *
*                                                                  *
* OCO Source Materials                                             *
*                                                                  *
*                                                                  *
* Copyright IBM Corp. 2003, 2015                                   *
*                                                                  *
* The source code for this program is not published or otherwise   *
* divested of its trade secrets, irrespective of what has been     *
* deposited with the U.S. Copyright Office.                        *
********************************************************************
*/
package com.ibm.websphere.update.delta.earutils;

/*
 *  @ (#) DeployCmd.java
 *
 *  Perform EAR deployment, including webui plugin processing.
 *
 *  @author     Steven Pritko
 *  @created    27-Aug-2003
 */

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.util.PlatformUtils;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class DeployCmd
{
   public final static String pgmVersion = "1.4" ;
   public final static String pgmUpdate = "1/29/04" ;

    // Answer a new deploy command on the specified
    // installation path, configuration path, and binary files
    // location.
    //
    // The paths must be absolutely specified.
    //
    // Processing messages are placed in the messages buffer.
    //
    // Processing errors are placed in the errors buffer.

    public DeployCmd(StringBuffer messages,
                     StringBuffer errors)
    {
        this.messages = messages;
        this.errors = errors;
    }
    public DeployCmd(String installPath, 
                     String configurationPath,
                     String binaryPath,
                     StringBuffer messages,
                     StringBuffer errors)
    {
       this( messages, errors );
       /*
        this.installPath = installPath;
        this.configurationPath = configurationPath;
        this.binaryPath = binaryPath;

        this.messages = messages;
        this.errors = errors;
        */
    }

/*
    protected String installPath;

    public String getInstallPath()
    {
        return this.installPath;
    }

    protected String configurationPath;

    public String getConfigurationPath()
    {
        return this.configurationPath;
    }

    protected String binaryPath;
    
    public String getBinaryPath()
    {
        return this.binaryPath;
    }
*/
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

    // Perform deployment of the specified EAR
    // into the specified server.
    //
    // Answer true or false, telling if the deployment
    // was successful.

    public boolean deploy(String earPath, 
                          String cellName,
                          String nodeName,
                          String serverName)
    {
        log("Performing deploy operation:");
        log("    EAR   : " + earPath);
        log("    Cell  : " + cellName);
        log("    Node  : " + nodeName);
        log("    Server: " + serverName);

        /*
        log("Uncompress");

        try {
        } catch (Exception ex) {
            logError("Uncompress: Failed");
            logError("Exception: " + ex);

            return false;
        }

        log("Uncompress: OK");
        */

        log("Deploying");

        try {
            ExecCmd exec = new ExecCmd(false); // Don't adjust

            String[] cmd; // TBD

            if (PlatformUtils.isWindows()) {
                cmd = new String[] {
                    "installApp", 
                    "-earFileName", earPath, 
                    "-nodeName", nodeName, 
                    "-serverName", serverName
                };
            } else {
                cmd = new String[] {
                    "./installApp.sh", 
                    "-earFileName", earPath, 
                    "-nodeName", nodeName, 
                    "-serverName", serverName
                };
            }

            Vector results = new Vector();
            Vector logResults = new Vector();

            int resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results, logResults);

            // TBD: Determine the result codes and their meanings.

            if (resultCode > 0) {
                logError("Deploying: Failed: " + earPath);
                return false;
            } else {
                log("Deploying: OK");
                return true;
            }

        } catch (Exception ex) {
            logError("Deploying: Failed: " + earPath);
            logError("Exception: " + ex);

            return false;
        }
    }

    // Perform plugin deployment of the specified module
    // into the specified server.
    //
    // Answer true or false, telling if the deployment
    // was successful.

    public boolean pluginDeploy(String modulePath, 
                                String cellName,
                                String nodeName,
                                String serverName)
    {
        log("Performing plugin deploy operation:");
        log("    Module: " + modulePath);
        log("    Cell  : " + cellName);
        log("    Node  : " + nodeName);
        log("    Server: " + serverName);

        /*
        log("Uncompress");

        try {
        } catch (Exception ex) {
            logError("Uncompress: Failed");
            logError("Exception: " + ex);

            return false;
        }

        log("Uncompress: OK");

        */

        log("Plugin-Deploy");

        try {
            ExecCmd exec = new ExecCmd(false); // Don't adjust

            String[] cmd; // TBD

            if (PlatformUtils.isWindows()) {
                cmd = new String[] {
                    "pluginProcessor.bat", 
                    "-install", modulePath
                };
            } else {
                cmd = new String[] {
                    "./pluginProcessor.sh", 
                    "-install", modulePath
                };
            }

            Vector results = new Vector();
            Vector logResults = new Vector();

            int resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results, logResults);

            // TBD: Determine the result codes and their meanings.

            if (resultCode > 0) {
                logError("Plugin-Deploy: Failed: " + modulePath);
                return false;
            } else {
                log("Plugin-Deploy: OK");
                return true;
            }

        } catch (Exception ex) {
            logError("Plugin-Deploy: Failed: " + modulePath);
            logError("Exception: " + ex);

            return false;
        }
    }
}
