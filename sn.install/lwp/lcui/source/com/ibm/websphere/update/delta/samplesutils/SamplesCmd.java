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

package com.ibm.websphere.update.delta.samplesutils;

import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.delta.earutils.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class SamplesCmd
{
    public final static String pgmVersion = "1.2" ;
    public final static String pgmUpdate = "9/26/03" ;

    protected static final String
        slash = System.getProperty("file.separator");

    protected static final String
        wsAdminSlash = "/";

    public static final boolean isWindows =
        com.ibm.websphere.update.util.PlatformUtils.isWindows();

    // Answer a new samples command processor on the specified java path
    // and on the specified messages and errors buffers.
    //
    // The java path must be absolutely specified.
    //
    // Messages from JAR processing are written to the messages
    // buffer.  Error messages from JAR processing are written to
    // the errors buffer.

    public SamplesCmd(String installPath, StringBuffer messages, StringBuffer errors)
    {
        this.installPath = installPath;

        this.messages = messages;
        this.errors = errors;
    }
    
    protected String installPath;

    /**
	 * @return  the installPath
	 * @uml.property  name="installPath"
	 */
    public String getInstallPath()
    {
        return this.installPath;
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
        StringBuffer useMessages = this.getMessages();

        useMessages.append(text);
        useMessages.append(EARActor.lineSeparator);
    }

    public void log(String text1, String text2)
    {
        StringBuffer useMessages = this.getMessages();

        useMessages.append(text1);
        useMessages.append(text2);
        useMessages.append(EARActor.lineSeparator);
    }

    protected void logResults(Vector results, String prefix)
    {
        int resultLines = results.size();

        for ( int lineNo = 0; lineNo < resultLines; lineNo++ ) {
            String nextLine = (String) results.elementAt(lineNo);

            if ( prefix != null )
                log(prefix, nextLine);
            else
                log(nextLine);
        }
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
        this.getErrors().append(text + EARActor.lineSeparator);
    }

    /*
     * Here are is the list of samples that is available for 5.0.1:
     *
     *   ${ear.dir}          ..  ${ear.name}
     *   SamplesGallery      ..  SamplesGallery
     *   TechnologySamples   ..  TechnologySamples
     *   PlantsByWebSphere   ..  PlantsByWebSphere
     *   PetStore            ..  petstore          (Not changed by the PTF)
     *   MessageDrivenBeans  ..  MDBSamples
     *
     * Here is the wsadmin command per platform:
     *
     *   wsadmin.bat (Windows)
     *   wsadmin.sh  (All Unix)
     *
     * Here are the wsadmin parameters:
     *
     * Uninstall:
     *     ${wsadmin.script}
     *         -conntype none
     *         -c "$AdminApp uninstall ${ear.name}"
     *
     * Install:
     *     ${wsadmin.script}
     *         -conntype none
     *         -c
     *         "$AdminApp install ${WAS_HOME}/samples/lib/${ear.dir}/${ear.name}.ear
     *          {-appname ${ear.name} -usedefaultbindings -node ${node.name}}"
     *
     * On unix, the "$AdminApp text changes to "\$AdminApp"
     *
     */

    /*
     * The slash used in the wsAdmin command is the '/' slash, regardless of
     * platform.
     */

    public String getSamplePath(String earDir, String earName)
    {
        String samplesPath =
            getInstallPath() + wsAdminSlash +
            "samples"        + wsAdminSlash +
            "lib"            + wsAdminSlash +
            earDir           + wsAdminSlash +
            earName + ".ear";

        return samplesPath.replace(slash.charAt(0), wsAdminSlash.charAt(0));
    }

    public String getWSAdminScriptPath()
    {
        return ( getInstallPath() + slash +
                 "bin" + slash +
                 ( isWindows ? "WSAdmin.bat" : "wsadmin.sh" ) );
    }

    public String getAdminAppText()
    {
        return ( isWindows ? "$AdminApp" : "$AdminApp" );
    }

    // Special for wsadmin processing
    public String getPlatformQuote()
    {
        return ( isWindows ? "\"" : "" );
    }

    public String enquote(String text)
    {
        String platformQuote = getPlatformQuote();

        return platformQuote + text + platformQuote;
    }

    /*
     * For example:
     *
     * (Windows)
     *
     *     "$AdminApp install ${WAS_HOME}/samples/lib/${ear.dir}/${ear.name}.ear
     *      {-appname ${ear.name} -usedefaultbindings -node ${node.name}}"
     *
     *     "$AdminApp install ${WAS_HOME}/samples/lib/MessageDrivenBeans/MDBSamples.ear
     *      {-appname MDBSamples -usedefaultbindings -node CALLY-X1}"
     *
     *      "$AdminApp uninstall ${ear.name}" 
     *
     *      "$AdminApp uninstall MDBSamples"
     *
     * (Unix)
     *
     *     '$AdminApp install ${WAS_HOME}/samples/lib/${ear.dir}/${ear.name}.ear
     *      {-appname ${ear.name} -usedefaultbindings -node ${node.name}}'
     *
     *     '$AdminApp install ${WAS_HOME}/samples/lib/MessageDrivenBeans/MDBSamples.ear
     *      {-appname MDBSamples -usedefaultbindings -node CALLY-X1}'
     *
     *      '$AdminApp uninstall ${ear.name}'
     *
     *      '$AdminApp uninstall MDBSamples'
     *
     * The above, as a single step:
     *
     *      "$AdminApp install ${WAS_HOME}/samples/lib/${ear.dir}/${ear.name}.ear
     *      {-appname ${ear.name} -update}" 
     */

    public String getInstallCommand(String earDir, String earName, String nodeName)
    {
        return enquote( getAdminAppText() + " install " + getSamplePath(earDir, earName) +
                        " {" + "-appname " + earName +
                               " -usedefaultbindings" +
                               " -node " + nodeName + "}" );
    }

    public String getUninstallCommand(String earDir, String earName, String nodeName)
    {
        return enquote( getAdminAppText() + " uninstall " + earName );
    }

    public String getUpdateCommand(String earDir, String earName, String nodeName)
    {
        return enquote( getAdminAppText() + " install " + getSamplePath(earDir, earName) +
                        " { " + "-appname " + earName + " -update" + " }" );
    }

    public boolean install(String earDir, String earName, String nodeName)
    {
        return runSampleCommand(earDir, earName, nodeName,
                                "install", getInstallCommand(earDir, earName, nodeName));
    }

    public boolean uninstall(String earDir, String earName, String nodeName)
    {
        return runSampleCommand(earDir, earName, nodeName,
                                "uninstall", getUninstallCommand(earDir, earName, nodeName));
    }

    public boolean update(String earDir, String earName, String nodeName)
    {
        return runSampleCommand(earDir, earName, nodeName,
                                "update", getUpdateCommand(earDir, earName, nodeName));
    }

    public boolean runSampleCommand(String earDir, String earName, String nodeName,
                                    String commandTag, String commandText)
    {
        log(commandTag + " Sample:");
        log("    EAR Directory: " + earDir);
        log("    EAR Name     : " + earName);
        log("    Node Name    : " + nodeName);

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            getWSAdminScriptPath(),
            "-conntype", "none",
            "-c", commandText
        };

        log("Command Arguments:");
        for ( int cmdNo = 0; cmdNo < cmd.length; cmdNo++ )
            log("  [" + cmdNo + "]: " + cmd[cmdNo]);

        int resultCode;

        try {
            Vector results = new Vector();
            Vector logResults = new Vector();

            try {
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          results,
                                          logResults);
            } finally {
                logResults(results, "Result: ");
                logResults(logResults, null);
            }

        } catch ( Exception ex ) {
            logError(commandTag + " Sample Failed with Exception:");
            logError("    EAR Directory: " + earDir);
            logError("    EAR Name     : " + earName);
            logError("    Node Name    : " + nodeName);

            logError("Exception: " + ex);

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
            logError(commandTag + " Sample Failed by Result Code:");
            logError("    EAR Directory: " + earDir);
            logError("    EAR Name     : " + earName);
            logError("    Node Name    : " + nodeName);

            logError("Result Code: " + resultCode);

            return false;

        } else {
            log(commandTag + " Sample: OK");
            return true;
        }
    }
}
