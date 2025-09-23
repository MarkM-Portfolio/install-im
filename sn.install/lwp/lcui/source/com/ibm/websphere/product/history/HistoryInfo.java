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

package com.ibm.websphere.product.history;

/*
 * Version Information Command Line Access and Reporting
 *
 * History 1.5.1.1, 7/29/05
 *
 * 01-Jul-2002 Initial Version
 * 01-Oct-2002 Added javadoc.
 * 25-Oct-2002 Fixed selective uninstall tag selection.
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.formatters.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.CalendarUtil;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.SAXParseException;

/**
 *  
 */
public class HistoryInfo
{
    // Program versioning ...

    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

   public static final String pgmVersion = "1.5.1.1" ;
    // Program versioning ...

    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

   public static final String pgmUpdate = "7/29/05" ;

    // Message access ...

    protected static String getDefaultedString(String msgCode, String defaultMsg)
    {
        try {
            return WPHistory.msgs.getString(msgCode);

        } catch ( MissingResourceException ex ) {
            return defaultMsg;
        }
    }

    protected static String getDefaultedString(String msgCode, String defaultMsg,
                                               Object[] msgArgs)
    {
        String rawMessage = getDefaultedString(msgCode, defaultMsg);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getString(String msgCode)
    {
        return WPHistory.getString(msgCode);
    }

    protected static String getString(String msgCode, Object msgArg)
    {
        return WPHistory.getString(msgCode, msgArg);
    }

    protected static String getString(String msgCode, Object msgArg1, Object msgArg2)
    {
        return WPHistory.getString(msgCode, msgArg1, msgArg2);
    }

    protected static String getString(String msgCode, Object[] msgArgs)
    {
        return WPHistory.getString(msgCode, msgArgs);
    }

    // Command line access ...

    /**
     * <p>Constants used to select an output format.</p>
     */

    public static final String TEXT_MODE = "text" ;
    // Command line access ...

    /**
     * <p>Constants used to select an output format.</p>
     */

    public static final String HTML_MODE = "html" ;
    // Command line access ...

    /**
     * <p>Constants used to select an output format.</p>
     */

    public static final String PORTLET_MODE = "portlet" ;
    // Command line access ...

    /**
     * <p>Constants used to select an output format.</p>
     */

    public static final String DEFAULT_REPORT_MODE = TEXT_MODE ;

    /**
     * <p>Print a copyright statement to standard output.</p>
     */

    public static void printCopyright()
    {
        System.out.println(getDefaultedString("WVER0210I",
                                              "WVER0210I: Copyright (c) IBM Corporation 2002,2006; All rights reserved."));
        System.out.println(getDefaultedString("WVER0211I",
                                              "WVER0211I: WebSphere Application Server Release 6.0"));
        System.out.println(getDefaultedString("WVER0212I",
                                              "WVER0212I: HistoryInfo Reporter Version {0}, Dated {1}",
                                              new Object[] { pgmVersion, pgmUpdate }));
        System.out.println();
    }

    /* <p>Command line access.  This method will eventually call 'System.exit()'
     * with the integer result of processing.</p>
     *
     * @param args The array of arguments from the command line.
     */

    public static void main(String[] args)
    {
        main(args, true);
    }

    /* <p>Main access, for command line access and for programmatic access.
     * When 'allowExit' is true, this method never returns, having given
     * up control to 'System.exit()' with an integer result code.</p>
     *
     * @param args The array of arguments for processing.
     * @param allowExit A flag which determines if the integer result code
     *                  is to be returned or sent tyhrough 'System.exit()'.
     *
     * @return int The integer result of processing; 0 is returned on success;
     *             -1 is returned if there was an error.
     */

    public static int main(String[] args, boolean allowExit) 
    {
        printCopyright();

        boolean isComplete = false;

        String errorArg = null;
        String errorCode = null;

        boolean showHelp = false;
        boolean showUsage = false;

        String format = DEFAULT_REPORT_MODE;
        String filename = null;

        boolean printStack = false;

        String updateId = null;
        String componentName = null;

        for ( int argNo = 0; !isComplete && (argNo < args.length); argNo++ ) {
            String nextArg = args[argNo];

            if ( nextArg.equalsIgnoreCase("-format") ) {
                argNo++;

                if ( argNo < args.length ) {
                    nextArg = args[argNo];

                    if ( nextArg.equalsIgnoreCase(TEXT_MODE) ||
                         nextArg.equalsIgnoreCase(HTML_MODE) ) {
                        format = nextArg;
                    } else {
                        isComplete = true;
                        errorArg = nextArg;
                        errorCode = "WVER0226E";
                    }
                } else {
                    isComplete = true;
                    errorArg = nextArg;
                    errorCode = "WVER0225E";
                }

            } else if ( nextArg.equalsIgnoreCase("-file") ) {
                argNo++;

                if ( argNo < args.length ) {
                    filename = args[argNo];
                } else {
                    isComplete = true;
                    errorArg = nextArg;
                    errorCode = "WVER0225E";
                }

            } else if ( nextArg.equalsIgnoreCase("-updateId") ) {
                argNo++;

                if ( argNo < args.length ) {
                    updateId = args[argNo];
                } else {
                    isComplete = true;
                    errorArg = nextArg;
                    errorCode = "WVER0225E";
                }

            } else if ( nextArg.equalsIgnoreCase("-component") ) {
                argNo++;

                if ( argNo < args.length ) {
                    componentName = args[argNo];
                } else {
                    isComplete = true;
                    errorArg = nextArg;
                    errorCode = "WVER0225E";
                }

            } else if ( nextArg.equalsIgnoreCase("-help") ||
                        nextArg.equalsIgnoreCase("-?") ||
                        nextArg.equalsIgnoreCase("/?") ||
                        nextArg.equalsIgnoreCase("/help") ) {
                isComplete = true;
                showHelp = true;

            } else if ( nextArg.equalsIgnoreCase("-usage") ) {
                isComplete = true;
                showUsage = true;

            } else if ( nextArg.equalsIgnoreCase("-printStack") ) {
                printStack = true;

            } else {
                isComplete = true;
                errorArg = nextArg;
                errorCode = "WVER0228E";
            }
        }

        if ( showUsage ) {
            System.out.println( getString("WVER0230I") );

            if ( allowExit )
                System.exit(0);
            else
                return 0;

        } else if ( showHelp ) {
            System.out.println( getString("WVER0231I") );

            if ( allowExit )
                System.exit(0);
            else
                return 0;

        } else if ( errorArg != null ) {
            System.out.println( getString(errorCode, errorArg) );

            if ( allowExit )
                System.exit(-1);
            else
                return -1;

        } else {
            Throwable boundError = null;

            try {
                HistoryInfo hi = new HistoryInfo();
                hi.setPrintStack(printStack);
                hi.runReport(format, filename, updateId, componentName);
            } catch ( FileNotFoundException e ) {
                boundError = e;
            } catch ( IOException e ) {
                boundError = e;
            } catch ( Throwable th ) {
                boundError = th;
            }

            if ( boundError != null ) {
                String useFilename =
                    (filename == null) ? getString("label.standard.output") : filename;

                String errorMessage;
                try {
                    String exceptionText = boundError.getMessage();
                    errorMessage = getString("WVER0240E", useFilename, exceptionText);

                } catch ( Throwable th ) {
                    errorMessage = getString("WVER0241E",
                                             new Object[] { useFilename,
                                                            boundError.getClass().getName(),
                                                            th.getClass().getName() });
                }

                System.err.println(errorMessage);

                if ( printStack )
                    boundError.printStackTrace();

                if ( allowExit )
                    System.exit(-1);
                else
                    return -1;

            } else {
                if ( allowExit )
                    System.exit(0);
                else
                    return 0;
            }
        }

        return -1; // Java doesn't understand that 'System.exit' causes
                   // an immediate exit.
    }

    // Transition to instance ...

    /**
     * <p>Standard constructor.</p>
     */

    public HistoryInfo()
    {
        setPrintStack(false);

        clearSource();

        clearFormatter();

        clearUpdateId();
        clearComponentName();
    }

    // Instance state ...

    protected boolean printStack;

    /**
	 * @param printStack  the printStack to set
	 * @uml.property  name="printStack"
	 */
    protected void setPrintStack(boolean printStack)
    {
        this.printStack = printStack;
    }

    /**
	 * @return  the printStack
	 * @uml.property  name="printStack"
	 */
    protected boolean getPrintStack()
    {
        return printStack;
    }

    protected WPProduct wasProduct;
    protected WPHistory wasHistory;
    protected PrintFormatter formatter;

    String updateId;
    String componentName;

    protected boolean setSource()
    {
        wasProduct = new WPProduct();
        try {
           wasHistory = new WPHistory();
        } catch ( Exception e) {
           List excList = new ArrayList(1);
           excList.add( e );
           // Note this skips the handling of erros for WPProdcT
           VersionInfo.handleFatalExceptions( 0, excList.iterator(), System.err, getPrintStack());
           return false;
        }

        return VersionInfo.handleErrors( wasProduct, wasHistory, System.err, getPrintStack() );
    }

    protected WPProduct getProduct()
    {
        return wasProduct;
    }

    protected WPHistory getHistory()
    {
        return wasHistory;
    }

    protected void clearSource()
    {
        wasHistory = null;
        wasProduct = null;
    }

    protected void setFormatter(String format, PrintWriter out)
    {
        if ( ( format == null ) ||
             ( !format.equalsIgnoreCase(TEXT_MODE) &&
               !format.equalsIgnoreCase(HTML_MODE) &&
               !format.equalsIgnoreCase(PORTLET_MODE) ) )
            format = DEFAULT_REPORT_MODE;

        if ( format.equalsIgnoreCase(HTML_MODE) )
            formatter = new HTMLPrintFormatter();
        else if ( format.equalsIgnoreCase(PORTLET_MODE) )
            formatter = new PortletPrintFormatter();
        else
            formatter = new TextPrintFormatter();

        formatter.setPrintWriter(out);
    }

    /**
	 * @return  the formatter
	 * @uml.property  name="formatter"
	 */
    protected PrintFormatter getFormatter()
    {
        return formatter;
    }

    protected void clearFormatter()
    {
        formatter = null;
    }

    /**
	 * @param updateId  the updateId to set
	 * @uml.property  name="updateId"
	 */
    protected void setUpdateId(String updateId)
    {
        this.updateId = updateId;
    }

    /**
	 * @return  the updateId
	 * @uml.property  name="updateId"
	 */
    protected String getUpdateId()
    {
        return updateId;
    }

    protected void clearUpdateId()
    {
        updateId = null;
    }

    /**
	 * @param componentName  the componentName to set
	 * @uml.property  name="componentName"
	 */
    protected void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    /**
	 * @return  the componentName
	 * @uml.property  name="componentName"
	 */
    protected String getComponentName()
    {
        return componentName;
    }

    protected void clearComponentName()
    {
        componentName = null;
    }

    protected boolean eventIsOfInterest(updateEvent event)
    {
        String useUpdateId = getUpdateId();
        String useComponentName = getComponentName();

        if ( useUpdateId != null ) {
            if ( !event.getId().equals(useUpdateId) ) {
                // System.out.println("Event does not match set update id");
                return false;
            }
        }

        if ( useComponentName != null) {
            boolean sawMatch = false;

            int numSubEvents = event.getUpdateEventCount();

            for ( int subEventNo = 0; !sawMatch && (subEventNo < numSubEvents); subEventNo++ ) {
                updateEvent nextSubEvent = event.getUpdateEvent(subEventNo);
                sawMatch = componentEventIsOfInterest(nextSubEvent);
            }

            if ( !sawMatch ) {
                // System.out.println("Event has no component events matching the component name.");
                return false;
            }
        }

        // System.out.println("Event is of interest");

        return true;
    }

    protected boolean componentEventIsOfInterest(updateEvent event)
    {
        String useComponentName = getComponentName();

        boolean result = ( (useComponentName == null) ||
                           (event.getId().equals(useComponentName)) );

        // if ( result )
        //     System.out.println("Component event is of interest.");
        // else
        //     System.out.println("Component event does not match the component name.");

        return result;
    }

    // Reporting ...

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the specified file.  The
     * additional argument specify if a restricted report is to
     * be generated:  When an update id is specified, only events
     * pertaining to the installation or removal of the update
     * having the specified id are included in the report;  When a
     * component name is specified, only events which affect the named
     * component are included in the report.</p>
     *
     * <p>A null filename may be specified, in which case the report
     * is written to standard output.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param filename The name of the file to which to write the report.
     *                 When a null filename is used, output is written
     *                 to standard output.
     *
     * @param updateId The ID of an update.  This value may be null, in
     *                 which case the report is unrestricted.  When a
     *                 non-null ID is specified, only events pertaining
     *                 to the update having that ID are included in the
     *                 report.
     *
     * @param componentName The name of a component.  This value may be
     *                      null, in which case the report is unrestricted.
     *                      When a non-null component name is specified,
     *                      only events pertaining to the named component
     *                      are included in the report.
     */

    public void runReport(String format, final String filename,
                          String updateId, String componentName)
        throws FileNotFoundException, IOException
    {
        if ( filename == null ) {
            runReport(format, System.out, updateId, componentName);

        } else {
            FileOutputStream out = new FileOutputStream(filename);
            // throws FileNotFoundException, IOException

            try {
                runReport(format, out, updateId, componentName);

            } finally {
                out.close();
            }
        }
    }

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the argument output stream.  The
     * additional argument specify if a restricted report is to
     * be generated.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param filename The name of the file to which to write the report.
     *                 When a null filename is used, output is written
     *                 to standard output.
     *
     * @param updateId The ID of an update.  When specified, only
     *                 events pertaining to this update are included in
     *                 report.
     *
     * @param componentName The name of a component.  When specified, only
     *                      events pertaining to the named component are
     *                      included in the report.
     */

    public void runReport(String format, OutputStream out,
                          String updateId, String componentName)
    {
        PrintWriter writer = new PrintWriter(out);

        try {
            runReport(format, writer, updateId, componentName);

        } finally {
            writer.flush();
        }
    }

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the argument print writer.  The
     * additional argument specify if a restricted report is to
     * be generated.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param filename The name of the file to which to write the report.
     *                 When a null filename is used, output is written
     *                 to standard output.
     *
     * @param updateId The ID of an update.  When specified, only
     *                 events pertaining to this update are included in
     *                 report.
     *
     * @param componentName The name of a component.  When specified, only
     *                      events pertaining to the named component are
     *                      included in the report.
     */

    public void runReport(String format, PrintWriter out,
                          String updateId, String componentName)
    {
        setFormatter(format, out);

        setUpdateId(updateId);
        setComponentName(componentName);

        try {
            if ( setSource() ) {

                try {
                    printReport();

                    VersionInfo.handleErrors(getProduct(), getHistory(), System.err, getPrintStack());

                } finally {
                    clearSource();
                }
            }

        } finally {
            clearComponentName();
            clearUpdateId();
            clearFormatter();
        }
    }

    // Raw reporting ...

    // Header
    //
    // Report at date and time <date>
    //
    // Installation
    // ============
    // Version Directory  <dir>
    // History Directory  <dir>
    // DTD Directory      <dir>
    // Log Directory      <dir>
    // Backup Directory   <dir>
    // TMP Directory      <dir>
    //
    ////    // Installation Event (...)
    // ==================
    // ( EFix ID                 <id> |
    //   PTF ID                  <id> )
    // Action                  ( install | uninstall |
    //                           selective install | selective uninstall )
    // Start Time              <timeStamp>
    // End Time                <timeStamp>
    // Log File Name           <logName>
    // Result                  <result>
    // Result Message          <resultMessage>
    //
    //    Component Initialization Event (...)
    //    ==============================
    //    ( EFix ID                 <id> |
    //      PTF ID                  <id> )
    //    Component Name          <name>
    //    Action                  ( install | uninstall )
    //    Is Custom               ( true | false )
    //    Primary Content         <content>
    //    Update Action           ( add | replace | remove | patch )
    //    Is External             ( true | false )
    //    [ Root Property File      <name>
    //      Root Property Name      <name>
    //      Root Property Value     <name> ]
    //    Log File Name           <logName>
    //    Backup File Name        <backupName>
    //    Start Time              <timeStamp>
    //    End Time                <timeStamp>
    //    Result                  <result>
    //    Result Message          <resultMessage>
    //    [ Initial Version         <version>
    //      Initial Specification   <specVersion>
    //      Initial Build Date      <date> ]
    //    [ Final Version           <version>
    //      Final Specification     <specVersion>
    //      Final Build Date        <date> ]

    protected void printReport()
    {
        PrintFormatter useFormatter = getFormatter();

        String useUpdateId = getUpdateId();
        String useComponentName = getComponentName();

        String headerText;

        if ( (useUpdateId == null) && (useComponentName == null) )
            headerText = getString("report.header");
        else if ( useUpdateId == null )
            headerText = getString("report.header.component", useComponentName);
        else if ( useComponentName == null )
            headerText = getString("report.header.update", useUpdateId);
        else
            headerText = getString("report.header.update.component", useUpdateId, useComponentName);

        useFormatter.printHeader(headerText);

        printTimeStamp();
        printSource();

        int numOfInterest = printHistory(getHistory().getHistory());

        if ( numOfInterest == 0 ) {
            String warningText;

            if ( (useUpdateId == null) && (useComponentName == null) )
                warningText = getString("warning.no.events");
            else if ( useUpdateId == null )
                warningText = getString("warning.no.events.for.component", useComponentName);
            else if ( useComponentName == null )
                warningText = getString("warning.no.events.for.update", useUpdateId);
            else
                warningText = getString("warning.no.events.for.update.component", useUpdateId, useComponentName);

            useFormatter.println(warningText, true);
        }

        useFormatter.blankLine();
        useFormatter.printFooter(getString("report.footer"));
    }

    protected void printTimeStamp()
    {
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.report.on", getTimeStamp()));
    }

    // A time stamp formatted according to the current locale.

    protected String getTimeStamp()
    {
        return CalendarUtil.getTimeStampAsString();
    }

    protected String unsub(String innerText, String outerText, String subVariable)
    {
        int subLoc = outerText.indexOf(innerText);

        if ( subLoc == -1 ) {
            return outerText;
        } else {
            return
                outerText.substring(0, subLoc) +
                subVariable +
                outerText.substring(subLoc + innerText.length());
        }
    }

    protected void printSource()
    {
        WPProduct useProduct = getProduct();
        WPHistory useHistory = getHistory();

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.source"), true);
        useFormatter.separator();

        String
            productSub      = getString("product.substitution"),
            versionSub      = getString("version.substitution"),
            historySub      = getString("history.substitution");

        String
            productDirName  = useProduct.getProductDirName(),
            versionDirName  = useProduct.getVersionDirName(),
            historyDirName  = useHistory.getHistoryDirName();

        //   String
        //   historyFileName = unsub(historyDirName, useHistory.getHistoryFileName(), historySub);

        // Be sure to unsub using the history dir before unsubbing the history dir!

        //historyDirName  = unsub(versionDirName, historyDirName, versionSub);

        String

            // the calls to unsub insert the version.dir back into the directory strings
            // this produces incorrect output when running WPVersionInfo
//            dtdDirName      = unsub(versionDirName, useProduct.getDTDDirName(),      versionSub),
//            logDirName      = unsub(versionDirName, useProduct.getLogDirName(),      versionSub),
//            backupDirName   = unsub(versionDirName, useProduct.getBackupDirName(),   versionSub),
//            tmpDirName      = unsub(versionDirName, useProduct.getTmpDirName(),      versionSub);
            // Be sure to unsub using the version dir before unsubbing the version dir!
//            versionDirName  = unsub(productDirName, versionDirName, productSub);

            dtdDirName     =  useProduct.getDTDDirName(),
            logDirName     =  useProduct.getLogDirName(),
            backupDirName  =  useProduct.getBackupDirName(),
            tmpDirName     =  useProduct.getTmpDirName(),
            historyFileName = useHistory.getHistoryFileName();

        Vector table = new Vector();

        table.add(new String[] { getString("label.product.dir"),          productDirName });
        table.add(new String[] { getString("label.version.dir"),          versionDirName });
        table.add(new String[] { getString("label.version.dtd.dir"),      dtdDirName });
        table.add(new String[] { getString("label.version.log.dir"),      logDirName });
        table.add(new String[] { getString("label.version.backup.dir"),   backupDirName });
        table.add(new String[] { getString("label.version.tmp.dir"),      tmpDirName });
        table.add(new String[] { getString("label.version.history.dir"),  historyDirName });
        table.add(new String[] { getString("label.version.history.file"), historyFileName });

        int[] allocations = new int[] {
            PrintFormatter.KEY_ALLOCATION + 5,
            PrintFormatter.VALUE_ALLOCATION - 5
        };

        useFormatter.printTable(allocations, table, 0);
    }

    protected int printHistory(eventHistory history)
    {
        int numOfInterest = 0;

        int numEvents = history.getUpdateEventCount();

        // System.out.println("Number of events: " + numEvents);

        for ( int eventNo = 0; eventNo < numEvents; eventNo++ ) {
            updateEvent nextEvent = history.getUpdateEvent(eventNo);

            if ( eventIsOfInterest(nextEvent) ) {
                printEvent(nextEvent);
                numOfInterest++;
            }
        }

        return numOfInterest;
    }

    protected static final int[] tableWidths = { 20, 75 };

    protected String consumeNull(String text)
    {
        return ( (text == null) ? "" : text );
    }

    protected void printEvent(updateEvent event)
    {
        PrintFormatter useFormatter = getFormatter();

        useFormatter.println("");
        useFormatter.println(getString("info.event"), true);
        useFormatter.separator();

        Vector table = new Vector();

        String eventIdPrefix = fillEventFields(event, table);

        useFormatter.printTable(tableWidths, table, 0);

        printComponentEvents(eventIdPrefix, event);
    }

    protected void printComponentEvents(String eventIdPrefix, updateEvent event)
    {
        int numSubEvents = event.getUpdateEventCount();

        for ( int subEventNo = 0; subEventNo < numSubEvents; subEventNo++ ) {
            updateEvent nextSubEvent = event.getUpdateEvent(subEventNo);

            if ( componentEventIsOfInterest(nextSubEvent) )
                printComponentEvent(eventIdPrefix, nextSubEvent);
        }
    }

    protected void printComponentEvent(String eventIdPrefix, updateEvent event)
    {
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();

        useFormatter.println(getString("info.component.event"), 1, true);
        useFormatter.separator(1);

        Vector table = new Vector();

        fillComponentEventFields(eventIdPrefix, event, table);

        useFormatter.printTable(tableWidths, table, 1);
    }

    protected String fillEventFields(updateEvent event, Vector table)
    {
        String eventIdPrefix = getEventIdPrefix(event);
        table.add(new Object[] { eventIdPrefix, event.getId() });

        table.add(new String[] { getString("label.action"), getEventActionText(event) });

        table.add(new String[] { getString("label.log.file.name"), event.getLogName() });

        table.add(new String[] { getString("label.start.time.stamp"), event.getStartTimeStamp() });
        table.add(new String[] { getString("label.end.time.stamp"), event.getStartTimeStamp() });

        table.add(new String[] { getString("label.result"), event.getResult() });

        String message = event.getResultMessage();
        if ( message == null )
            message = "";

        table.add(new String[] { getString("label.result.message"), message });

        return eventIdPrefix;
    }

    protected void fillComponentEventFields(String eventIdPrefix, updateEvent event, Vector table)
    {
        table.add(new Object[] { eventIdPrefix, event.getParentId() });
        table.add(new String[] { getString("label.component.name"), event.getId() });

        table.add(new String[] { getString("label.action"), getEventActionText(event) });

        boolean isCustom = event.getIsCustomAsBoolean();
        table.add(new String[] { getString("label.is.custom"), booleanText(isCustom) });
        table.add(new String[] { getString("label.primary.content"), event.getPrimaryContent() });

        table.add(new Object[] { getString("label.update.type"), getUpdateTypeText(event) });

        boolean isExternal = event.getIsExternalAsBoolean();
        table.add(new String[] { getString("label.is.external"), booleanText(isExternal) });

        if ( isExternal ) {
            table.add(new String[] { getString("label.root.property.file"),
                                     event.getRootPropertyFile() });
            table.add(new String[] { getString("label.root.property.name"),
                                     event.getRootPropertyName() });
            table.add(new String[] { getString("label.root.property.value"),
                                     event.getRootPropertyValue() });
        }

        table.add(new String[] { getString("label.log.file.name"), event.getLogName() });
        table.add(new String[] { getString("label.backup.file.name"), event.getBackupName() });

        table.add(new String[] { getString("label.start.time.stamp"), event.getStartTimeStamp() });
        table.add(new String[] { getString("label.end.time.stamp"), event.getStartTimeStamp() });

        table.add(new String[] { getString("label.result"), getEventResultText(event) });

        String message = event.getResultMessage();
        if ( message == null )
            message = "";

        table.add(new String[] { getString("label.result.message"), message });

        componentVersion initialVersion = event.getInitialVersion();

        if ( initialVersion != null ) {
            table.add(new String[] { getString("label.initial.spec.version"),
                                     initialVersion.getSpecVersion() });
            table.add(new String[] { getString("label.initial.build.version"),
                                     initialVersion.getBuildVersion() });
            table.add(new String[] { getString("label.initial.build.date"),
                                     initialVersion.getBuildDate() });
        }

        componentVersion finalVersion = event.getFinalVersion();

        if ( finalVersion != null ) {
            table.add(new String[] { getString("label.final.spec.version"),
                                     finalVersion.getSpecVersion() });
            table.add(new String[] { getString("label.final.build.version"),
                                     finalVersion.getBuildVersion() });
            table.add(new String[] { getString("label.final.build.date"),
                                     finalVersion.getBuildDate() });
        }
    }

    protected String getEventIdPrefix(updateEvent event)
    {
        enumEventType eventType = event.getEventTypeAsEnum();

        String labelTag;

        if ( eventType == enumEventType.EFIX_EVENT_TYPE )
            labelTag = "label.efix.id";
        else if ( eventType == enumEventType.PTF_EVENT_TYPE )
            labelTag = "label.ptf.id";
        else
            labelTag = "label.unknown.id";

        return getString("label.efix.id");
    }

    protected String booleanText(boolean value)
    {
        return getString( value ? "label.true" : "label.false" );
    }

    protected String getEventActionText(updateEvent event)
    {
        enumUpdateAction action = event.getUpdateActionAsEnum();

        String actionTag =
            ( ( action == enumUpdateAction.INSTALL_UPDATE_ACTION )             ? "label.install.action" :
            ( ( action == enumUpdateAction.UNINSTALL_UPDATE_ACTION)            ? "label.uninstall.action" :
            ( ( action == enumUpdateAction.SELECTIVE_INSTALL_UPDATE_ACTION )   ? "label.selective.install.action" :
            ( ( action == enumUpdateAction.SELECTIVE_UNINSTALL_UPDATE_ACTION ) ? "label.selective.uninstall.action" :
                                                                                 "label.unknown.action" ) ) ) );

        // System.out.println("Event Update Action: " + event.getUpdateAction());
        // System.out.println("Action Label: " + actionTag);

        String result = getString(actionTag);

        // System.out.println("Action Text: " + result);

        return result;
    }

    protected String getUpdateTypeText(updateEvent event)
    {
        enumUpdateType updateType = event.getUpdateTypeAsEnum();

        String updateTypeTag =
            ( ( updateType == enumUpdateType.ADD_UPDATE_TYPE )       ? "label.update.add.tag" :
            ( ( updateType == enumUpdateType.REPLACE_UPDATE_TYPE)    ? "label.update.replace.tag" :
            ( ( updateType == enumUpdateType.REMOVE_UPDATE_TYPE )    ? "label.update.remove.tag" :
            ( ( updateType == enumUpdateType.PATCH_UPDATE_TYPE )     ? "label.update.patch.tag" :
            ( ( updateType == enumUpdateType.COMPOSITE_UPDATE_TYPE ) ? "label.update.composite.tag" :
                                                                       "label.update.unknown.tag" ) ) ) ) );
        return getString(updateTypeTag);
    }

    protected String getEventResultText(updateEvent event)
    {
        enumEventResult eventResult = event.getResultAsEnum();

        String eventResultTag =
            ( ( eventResult == enumEventResult.SUCCEEDED_EVENT_RESULT ) ? "label.result.succeeded.tag" :
            ( ( eventResult == enumEventResult.FAILED_EVENT_RESULT)     ? "label.result.failed.tag" :
            ( ( eventResult == enumEventResult.CANCELLED_EVENT_RESULT ) ? "label.result.cancelled.tag" :
                                                                          "label.result.unknown.tag" ) ) );
        return getString(eventResultTag);
    }
}
