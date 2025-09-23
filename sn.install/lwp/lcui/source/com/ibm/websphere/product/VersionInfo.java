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

package com.ibm.websphere.product;

/*
 * Version Information Command Line Access and Reporting
 *
 * History 1.10.1.1, 7/29/05
 *
 * 25-Jun-2002 Added standard header.
 *             Messaging and error handling update.
 *
 * 01-Oct-2002 Added javadoc
 */

import com.ibm.websphere.product.formatters.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.CalendarUtil;
import com.ibm.websphere.product.xml.component.component;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.product.xml.extension.extension;
import com.ibm.websphere.product.xml.product.product;
import com.ibm.websphere.product.xml.websphere.websphere;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.xml.sax.SAXParseException;

/**
 * <p>VersionInfo -- WebSphere Product Version Reporting.</p>
 */

public class VersionInfo 
{
    // Program versioning ...

    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

    public static final String pgmVersion = "1.10.1.1" ;
    // Program versioning ...

    /** Constants holding CMVC version information.  This information
     *  is dynamic, changing with each source update. */

    public static final String pgmUpdate = "7/29/05" ;

    // Message access ...

    protected static String getDefaultedString(String msgCode, String defaultMsg)
    {
        if ( WPProduct.msgs == null )
            return defaultMsg;

        try {
            return WPProduct.msgs.getString(msgCode);

        } catch ( MissingResourceException ex ) {
            return defaultMsg;
        }
    }

    protected static String getDefaultedString(String msgCode, String defaultMsg, Object[] msgArgs)
    {
        String rawMessage = getDefaultedString(msgCode, defaultMsg);

        return MessageFormat.format(rawMessage, msgArgs);
    }

    protected static String getString(String msgCode)
    {
        return WPProduct.getString(msgCode);
    }

    protected static String getString(String msgCode, Object arg)
    {
        return WPProduct.getString(msgCode, arg);
    }

    protected static String getString(String msgCode, Object arg1, Object arg2)
    {
        return WPProduct.getString(msgCode, arg1, arg2);
    }

    protected static String getString(String msgCode, Object[] args)
    {
        return WPProduct.getString(msgCode, args);
    }

    // Error Reporting ...

    protected static void handleRecoverableErrors(Iterator recoverableErrors,
                                                  PrintStream output, boolean printStack)
    {
        while ( recoverableErrors.hasNext() ) {
            SAXParseException nextError = (SAXParseException)
                recoverableErrors.next();

            String errorText = getString("WVER0022E",
                                         new Object[] { nextError.getSystemId(),
                                                        nextError.getPublicId(),
                                                        new Integer(nextError.getLineNumber()),
                                                        new Integer(nextError.getColumnNumber()),
                                                        nextError.toString() });
            output.println(errorText);

            if ( printStack )
                nextError.printStackTrace(output);
        }
    }

    protected static void handleWarnings(Iterator warnings,
                                         PrintStream output, boolean printStack)
    {
        while ( warnings.hasNext() ) {
            SAXParseException nextWarning = (SAXParseException) warnings.next();

            String warningText = getString("WVER0024E",
                                           new Object[] { nextWarning.getSystemId(),
                                                          nextWarning.getPublicId(),
                                                          new Integer(nextWarning.getLineNumber()),
                                                          new Integer(nextWarning.getColumnNumber()),
                                                          nextWarning.toString() });
            output.println(warningText);

            if ( printStack )
                nextWarning.printStackTrace(output);
        }
    }

    //Needed by HistoryInfo
    public    static int handleFatalExceptions(int errorNo,
                                               Iterator fatalExceptions,
                                               PrintStream output, boolean printStack)
    {
        while ( fatalExceptions.hasNext() ) {
            Exception nextFatalException = (Exception) fatalExceptions.next();
            errorNo++;

            String errorMessage = getString("listing.nested.error",
                                            Integer.toString(errorNo),
                                            nextFatalException.toString());

            output.println(errorMessage);

            if ( printStack )
                nextFatalException.printStackTrace(output);
        }

        return errorNo;
    }

    /**
     * <p>Handle the errors registered in the argument product and
     * history objects.  Each error, warning, and fatal exception
     * is displayed to the argument output stream, with NLS enabled
     * annotation.  Stack trace for each of the errors, warnings,
     * and fatal exceptions is displayed exactly when the 'printStack'
     * argument is true.</p>
     *
     * <p>Answer true or false, telling if no fatal errors were present.</p>
     *
     * @param wasProduct A WAS product instance to scan.
     * @param wasHistory A WAS history instance to scan.
     * @param output A print stream to which to display noted errors, warnings,
     *               and fatal exceptions.
     * @param printStack A flag telling if stack traces are to be displayed
     *                   for the noted errors, warnings, and fatal exceptions.
     *
     * @return Whether or not no fatal errors were present.  (Answer true
     *         if there were no fatal errors.)
     */

    public static boolean handleErrors(WPProduct wasProduct, WPHistory wasHistory,
                                       PrintStream output, boolean printStack)
    {
        int numProductErrors = wasProduct.numRecoverableErrors(),
            numHistoryErrors = wasHistory.numRecoverableErrors();

        if ( (numProductErrors  > 0) || (numHistoryErrors > 0) ) {
            output.println( getString("WVER0021E") );

            handleRecoverableErrors( wasProduct.getRecoverableErrors(), output, printStack );
            handleRecoverableErrors( wasHistory.getRecoverableErrors(), output, printStack );
        }

        int numProductWarnings = wasProduct.numWarnings(),
            numHistoryWarnings = wasHistory.numWarnings();

        if ( (numProductWarnings > 0) || (numHistoryWarnings > 0) ) {
            output.println( getString("WVER0023E") );

            handleWarnings( wasProduct.getWarnings(), output, printStack );
            handleWarnings( wasHistory.getWarnings(), output, printStack );
        }

        int numProductFatalExceptions = wasProduct.numExceptions(),
            numHistoryFatalExceptions = wasHistory.numExceptions();

        int numFatalExceptions = numProductFatalExceptions + numHistoryFatalExceptions;

        if ( numFatalExceptions == 0 )
            return true;

        String errorMessage = getString( (numFatalExceptions == 1) ? "WVER0015E" : "WVER0016E" );
        output.println(errorMessage);

        int errorNo = 0;

        errorNo = handleFatalExceptions( errorNo, wasProduct.getExceptions(), output, printStack );
        errorNo = handleFatalExceptions( errorNo, wasHistory.getExceptions(), output, printStack );

        return false;
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
        System.out.println(getDefaultedString("WVER0010I",
                                              "WVER0010I: Copyright (c) IBM Corporation 2002,2006; All rights reserved."));
        System.out.println(getDefaultedString("WVER0011I",
                                              "WVER0011I: WebSphere Application Server Release 6.0"));
        System.out.println(getDefaultedString("WVER0012I",
                                              "WVER0012I: VersionInfo Reporter Version {0}, Dated {1}",
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
        main(args, true); // Will never return!
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

        String format = null;
        String filename = null;

        boolean displayEFixes = false;
        boolean displayEFixDetail = false;

        boolean displayPTFs = false;
        boolean displayPTFDetail = false;

        boolean displayComponents = false;
        boolean displayComponentDetail = false;

        boolean printStack = false;

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
                        errorCode = "WVER0006E";
                    }
                } else {
                    isComplete = true;
                    errorArg = nextArg;
                    errorCode = "WVER0005E";
                }

            } else if ( nextArg.equalsIgnoreCase("-file") ) {
                argNo++;

                if ( argNo < args.length ) {
                    filename = args[argNo];
                } else {
                    isComplete = true;
                    errorArg = nextArg;
                    errorCode = "WVER0005E";
                }

            } else if ( nextArg.equalsIgnoreCase("-efixes") ) {
                displayEFixes = true;

            } else if ( nextArg.equalsIgnoreCase("-efixDetail") ) {
                displayEFixes = true;
                displayEFixDetail = true;

            } else if ( nextArg.equalsIgnoreCase("-fixes") ) {
                displayEFixes = true;

            } else if ( nextArg.equalsIgnoreCase("-fixDetail") ) {
                displayEFixes = true;
                displayEFixDetail = true;

            } else if ( nextArg.equalsIgnoreCase("-ptfs") ) {
                displayPTFs = true;

            } else if ( nextArg.equalsIgnoreCase("-ptfDetail") ) {
                displayPTFs = true;
                displayPTFDetail = true;

            } else if ( nextArg.equalsIgnoreCase("-components") ) {
                displayComponents = true;

            } else if ( nextArg.equalsIgnoreCase("-componentDetail") ) {
                displayComponents = true;
                displayComponentDetail = true;

            } else if ( nextArg.equalsIgnoreCase("-long") ) {
                displayEFixes = true;
                displayEFixDetail = true;

                displayPTFs = true;
                displayPTFDetail = true;

                displayComponents = true;
                displayComponentDetail = true;

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
                errorCode = "WVER0007E";
            }
        }

        if ( showHelp ) {
            System.out.println(getString("WVER0003I"));

            if ( allowExit )
                System.exit(0);
            else
                return 0;

        } else if ( showUsage ) {
            System.out.println(getString("WVER0008I"));

            if ( allowExit )
                System.exit(0);
            else
                return 0;

        } else if ( errorArg != null ) {
            System.err.println(getString(errorCode, errorArg));

            if ( allowExit )
                System.exit(-1);
            else
                return -1;

        } else {
            Throwable boundError = null;

            try {
                VersionInfo vi = new VersionInfo();
                vi.setPrintStack(printStack);
                vi.runReport(format, filename,
                             displayEFixes, displayEFixDetail,
                             displayPTFs, displayPTFDetail,
                             displayComponents, displayComponentDetail);
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
                    String exceptionText = boundError.toString();
                    errorMessage = getString("WVER0004E", useFilename, exceptionText);

                } catch ( Throwable th ) {
                    errorMessage = getString("WVER0009E",
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

    public VersionInfo()
    {
        setPrintStack(false);

        clearProduct();
        clearPrintMode();
        clearFormatter();
    }

    // Instance state ...

    protected boolean printStack;

    protected WPProduct wasProduct;
    protected WPHistory wasHistory;

    protected PrintFormatter formatter;

    protected boolean displayEFixes;
    protected boolean displayEFixDetail;

    protected boolean displayPTFs;
    protected boolean displayPTFDetail;

    protected boolean displayComponents;
    protected boolean displayComponentDetail;

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

    protected boolean setProduct()
    {
        wasProduct = new WPProduct();
        try {
           wasHistory = new WPHistory();
        } catch ( Exception e) {
           List excList = new ArrayList(1);
           excList.add( e );
           // Note this skips the handling of erros for WPProdct
           handleFatalExceptions( 0, excList.iterator(), System.err, getPrintStack());
           return false;
        }

        return handleErrors(wasProduct, wasHistory, System.err, getPrintStack());
    }

    protected WPProduct getProduct()
    {
        return wasProduct;
    }

    protected WPHistory getHistory()
    {
        return wasHistory;
    }

    protected void clearProduct()
    {
        wasProduct = null;
        wasHistory = null;
    }

    protected boolean isText;

    /**
	 * @return
	 * @uml.property  name="isText"
	 */
    protected boolean isText()
    {
        return isText;
    }

    protected void setFormatter(String format, PrintWriter out)
    {
        if ( (format == null) ||
             !(format.equalsIgnoreCase(TEXT_MODE) ||
               format.equalsIgnoreCase(HTML_MODE) ||
               format.equalsIgnoreCase(PORTLET_MODE)) )
            format = DEFAULT_REPORT_MODE;

        if ( format.equalsIgnoreCase(HTML_MODE) ) {
            isText = false;
            formatter = new HTMLPrintFormatter();
        } else if ( format.equalsIgnoreCase(PORTLET_MODE) ) {
            isText = false;
            formatter = new PortletPrintFormatter();
        } else {
            isText = true;
            formatter = new TextPrintFormatter();
        }

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

    protected void setPrintMode(boolean displayEFixes,
                                boolean displayEFixDetail,
                                boolean displayPTFs,
                                boolean displayPTFDetail,
                                boolean displayComponents,
                                boolean displayComponentDetail)
    {
        this.displayEFixes = displayEFixes;
        this.displayEFixDetail = displayEFixDetail;

        this.displayPTFs = displayPTFs;
        this.displayPTFDetail = displayPTFDetail;

        this.displayComponents = displayComponents;
        this.displayComponentDetail = displayComponentDetail;
    }

    protected boolean displayEFixes()
    {
        return displayEFixes;
    }

    protected boolean displayEFixDetail()
    {
        return displayEFixDetail;
    }

    protected boolean displayPTFs()
    {
        return displayPTFs;
    }

    protected boolean displayPTFDetail()
    {
        return displayPTFDetail;
    }

    protected boolean displayComponents()
    {
        return displayComponents;
    }

    protected boolean displayComponentDetail()
    {
        return displayComponentDetail;
    }

    protected void clearPrintMode()
    {
        this.displayEFixes = false;
        this.displayEFixDetail = false;

        this.displayPTFs = false;
        this.displayPTFDetail = false;

        this.displayComponents = false;
        this.displayComponentDetail = false;
    }

    // Reporting ...

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the specified file.</p>
     *
     * <p>This reporting method defaults the display options to
     * false, resulting in a minimal report.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param filename The name of the file to which to write the report.
     *                 When a null filename is used, output is written
     *                 to standard output.
     */

    public void runReport(String format, final String filename)
        throws FileNotFoundException, IOException
    {
        runReport(format, filename, false, false, false, false, false, false);
        // throws FileNotFoundException, IOException
    }

    /* <p>Main API: Run a product version report, with output in the
     * specified format, and output to the specified file.  The
     * extra arguments determine the level of detail which is put
     * in the report.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param filename The name of the file to which to write the report.
     *                 When a null filename is used, output is written
     *                 to standard output.
     *
     * @param displayEFixes A flag specifying if efixes are to be displayed.
     * @param displayEFixDetail A flag specifying if detailed efix information
     *                          is to be displayed.
     * @param displayPTFs A flag specifying if PTFs are to be displayed.
     * @param displayPTFDetail A flag specifying if detailed PTFinformation
     *                         is to be displayed.
     * @param displayComponents A flag specifying if components are to be displayed.
     * @param displayComponentDetail A flag specifying if component detail is
     *                               to be displayed.
     *
     * @exception FileNotFoundException An exception thrown when the argument
     *                                  named file cannot be opened to be written.
     * @exception IOException An exception thrown when the argument
     *                        named file cannot be opened to be written.
     */

    public void runReport(String format, final String filename,
                          boolean displayEFixes, boolean displayEFixDetail,
                          boolean displayPTFs, boolean displayPTFDetail,
                          boolean displayComponents, boolean displayComponentDetail)
        throws FileNotFoundException, IOException
    {
        if ( filename == null ) {
            runReport(format, System.out,
                      displayEFixes, displayEFixDetail,
                      displayPTFs, displayPTFDetail,
                      displayComponents, displayComponentDetail);

        } else {
            FileOutputStream out = new FileOutputStream(filename);
            // throws FileNotFoundException, IOException

            try {
                runReport(format, out,
                          displayEFixes, displayEFixDetail,
                          displayPTFs, displayPTFDetail,
                          displayComponents, displayComponentDetail);

            } finally {
                out.close();
            }
        }
    }

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the specified output stream.</p>
     *
     * <p>This reporting method defaults the display options to
     * false, resulting in a minimal report.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param out The output stream to which to write the report.
     */

    public void runReport(String format, OutputStream out)
    {
        runReport(format, out, false, false, false, false, false, false);
    }

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the specified output stream.
     * The extra arguments determine the level of detail which is put
     * in the report.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param out The output stream to which to write the report.
     *
     * @param displayEFixes A flag specifying if efixes are to be displayed.
     * @param displayEFixDetail A flag specifying if detailed efix information
     *                          is to be displayed.
     * @param displayPTFs A flag specifying if PTFs are to be displayed.
     * @param displayPTFDetail A flag specifying if detailed PTFinformation
     *                         is to be displayed.
     * @param displayComponents A flag specifying if components are to be displayed.
     * @param displayComponentDetail A flag specifying if component detail is
     *                               to be displayed.
     */

    public void runReport(String format, OutputStream out,
                          boolean displayEFixes, boolean displayEFixDetail,
                          boolean displayPTFs, boolean displayPTFDetail,
                          boolean displayComponents, boolean displayComponentDetail)
    {
        PrintWriter writer = new PrintWriter(out);

        try {
            runReport(format, writer,
                      displayEFixes, displayEFixDetail,
                      displayPTFs, displayPTFDetail,
                      displayComponents, displayComponentDetail);

        } finally {
            writer.flush();
        }
    }

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the specified print writer.</p>
     *
     * <p>This reporting method defaults the display options to
     * false, resulting in a minimal report.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param out The print writer to which to write the report.
     */

    public void runReport(String format, PrintWriter out)
    {
        runReport(format, out, false, false, false, false, false, false);
    }

    /**
     * <p>Main API: Run a product version report, with output in the
     * specified format, and output to the specified print writer.
     * The extra arguments determine the level of detail which is put
     * in the report.</p>
     *
     * @param format The format of the report, either 'TEXT' or 'HTML'.
     * @param out The print writer to which to write the report.
     *
     * @param displayEFixes A flag specifying if efixes are to be displayed.
     * @param displayEFixDetail A flag specifying if detailed efix information
     *                          is to be displayed.
     * @param displayPTFs A flag specifying if PTFs are to be displayed.
     * @param displayPTFDetail A flag specifying if detailed PTFinformation
     *                         is to be displayed.
     * @param displayComponents A flag specifying if components are to be displayed.
     * @param displayComponentDetail A flag specifying if component detail is
     *                               to be displayed.
     */

    public void runReport(String format, PrintWriter out,
                          boolean displayEFixes, boolean displayEFixDetail,
                          boolean displayPTFs, boolean displayPTFDetail,
                          boolean displayComponents, boolean displayComponentDetail)
    {
        setFormatter(format, out);
        setPrintMode(displayEFixes, displayEFixDetail,
                     displayPTFs, displayPTFDetail,
                     displayComponents, displayComponentDetail);

        try {
            if ( setProduct() ) {

                try {
                    printReport();
                    handleErrors(getProduct(), getHistory(), System.err, getPrintStack());

                } finally {
                    clearProduct();
                }
            }

        } finally {
            clearPrintMode();
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
    // Product Directory  <dir>
    // Version Directory  <dir>
    // DTD Directory      <dir>
    // Log Directory      <dir>
    // Backup Directory   <dir>
    // TMP Directory      <dir>
    //
    // Installation Platform
    // =====================
    // Name          <name>
    // Version       <version>
    //
    // Technology List
    // ===============
    // [ embeddedEXPRESS  <installed> ]
    // [ EXPRESS          <installed> ]
    // [ BASE             <installed> ]
    // [ ND               <installed> ]
    // [ XD               <installed> ]
    // [ PME              <installed> ]
    // [ CLIENT           <installed> ]
    //
    // Installed Product (...)
    // =================
    // Name          <name>
    // Version       <version>
    // ID            <id>
    // Build Level   <level>
    // Build Date    <date>
    //
    // Installed Extension (...)
    // ===================
    // Name          <name>
    // ID            <id>
    //
    // [START >>> 'displayComponents' must be specified for this section to be shown]
    //
    // Installed Component (...)
    // ===================
    // Name          <name>
    // Spec Version  <version>
    // Build Version <version>
    // Build Date    <date>
    //
    // [START >>> 'displayComponentDetail' must be specified for this section to be shown]
    //
    // Installed Component Update (...) +
    // ==========================
    // Component Name        <name>
    // Update Type           <efix|PTF>
    // ( EFix ID               <efix-id> |
    //   PTF ID                <ptf-id> )
    // Update Effect         <add component|replace component|remove component|patch component|unknown>
    //
    // Is Required           <true|false>
    // Is Optional           <true|false>
    //
    // Is External           <true|false>
    // [ Root Property File    <fileName>
    // Root Property Name    <name>
    // Root Property Value   <fileName> ]
    //
    // Is Custom             <true|false>
    // Log File Name         <logName>
    // Backup File Name      <backupName>
    // Install Date          <timeStamp>
    //
    // [ Initial Spec Version   <version>
    // Initial Build Date    <date>
    // Initial Build Version <version> ]
    //
    // [ Final Spec Version    <version>
    // Final Build Date      <date>
    // Final Build Version   <version> ]
    //
    // ['displayComponentDetail' <<< END]
    //
    // ['displayComponents' <<< END]
    //
    // [START >>> 'displayEFixes' must be specified for this section to be shown]
    //
    // Installed EFix (...)
    // ==============
    // ID              <id>
    // Description     <description>
    // Build Date      <date>
    // Build Version   <version>
    //
    // [START >>> 'displayEFixDetail' must be specified for this section to be shown]
    //
    // Is Trial        (true | false)
    // Expiration Date <date>
    // Exposition    <details>   XXX
    //
    //    APAR (...)
    //    ====
    //    Number          <number>
    //    Date            <date>
    //    description     <description>
    //    exposition      <exposition>
    //
    //    Supported Platforms
    //    ===================
    //    <architecture>  <platform>  <version> (...)
    //
    //    Supported Products
    //    ==================
    //    <product-id> <build-version> <build-date> <build-level> (...)
    //
    //    Prerequisite EFixes
    //    ===================
    //    <id> (is-negative|is-positive) [install-index] (...)
    //
    //    Component Updates
    //    =================
    //    <componentName> (add|replace|remove|patch) (required|if-possible|optional|recommended) (...)
    //       (standard|custom) <primary-content>
    //       (installed on <date>|absent)
    //       external <rootPropertyFile>:<rootPropertyName> = <rootPropertyValue>
    //       requires <componentName> v <version> ...
    //       becomes <componentName> v <version>
    //
    //    Custom Property
    //    ===============
    //    <propertyName> <propertyType> <propertyValue> (...)
    //
    // ['displayEFixDetail' <<< END]
    //
    // ['displayEFixes' <<< END]
    //
    // [START >>> 'displayPTFs' must be specified for this section to be shown]
    //
    // Installed PTF (...)
    // =============
    // ID            <id>
    // Description   <description>
    // Build Date    <date>
    // Build Version <version>
    //
    // [START >>> 'displayPTFDetail' must be specified for this section to be shown]
    //
    // Exposition    <details>   XXX
    //
    //    Prerequisite Platforms
    //    ======================
    //    <architecture>  <platform>  <version> (...)
    //
    //    Prerequisite Products
    //    =====================
    //    <product-id> <build-version> <build-date> <build-level> (...)
    //
    //    Included EFixes
    //    ===============
    //    <efix-id> (...)
    //
    //    Component Updates
    //    =================
    //    <componentName> (add|replace|remove|patch) (required|if-possible|optional|recommended)
    //       (standard|custom) <primary-content>
    //       (installed on <date>|absent)
    //       external <rootPropertyFile>:<rootPropertyName> = <rootPropertyValue>
    //       requires <componentName> v <version> ...
    //       [ becomes spec <version>, build <version> on <date> ]
    //
    //    Custom Property
    //    ===============
    //    <propertyName> <propertyType> <propertyValue> (...)
    //
    // ['displayPTFDetail' <<< END]
    //
    // ['displayPTFs' <<< END]

    // Trailer


    // A time stamp formatted according to the current locale.

    protected String getTimeStamp()
    {
        return CalendarUtil.getTimeStampAsString();
    }

    protected void printReport()
    {
        WPProduct useProduct = getProduct();
        PrintFormatter useFormatter = getFormatter();

        useFormatter.printHeader(getString("report.header"));

        printTimeStamp();

        printSource();

        printPlatform(useProduct.getPlatform());

        printChecklist();

        Iterator products = useProduct.getProducts();
        while ( products.hasNext() )
            printProduct( (product) products.next() );

        Iterator extensions = useProduct.getExtensions();
        while ( extensions.hasNext() )
            printExtension( (extension) extensions.next() );

        if ( displayComponents() ) {
            Iterator components = useProduct.getComponents();
            while ( components.hasNext() )
                printComponent( (component) components.next() );
        }

        if ( displayEFixes() ) {
            Iterator eFixes = useProduct.getEFixes();
            while ( eFixes.hasNext() )
                printEFix( (efix) eFixes.next() );
        }

        if ( displayPTFs() ) {
            Iterator ptfs = useProduct.getPTFs();
            while ( ptfs.hasNext() )
                printPTF( (ptf) ptfs.next() );
        }

        useFormatter.blankLine();

        useFormatter.printFooter(getString("report.footer"));
    }

    protected void separator(int indent)
    {
        if ( isText() )
            getFormatter().separator(indent);
    }

    protected void separator()
    {
        if ( isText() )
            getFormatter().separator();
    }

    protected void printTimeStamp()
    {
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.report.on", getTimeStamp()));
    }

    protected String unsub(String innerText, String outerText, String subVariable)
    {
        int subLoc = outerText.indexOf(innerText);

        if ( subLoc == -1 ) {
            return outerText;
        } else {
            return outerText.substring(0, subLoc) +
                   subVariable +
                   outerText.substring(subLoc + innerText.length());
        }
    }

    protected void printSource()
    {
        WPProduct useProduct = getProduct();

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.source"), true);
        separator();

        String
            productSub     = getString("product.substitution"),
            versionSub     = getString("version.substitution");

        String
            productDirName = useProduct.getProductDirName(),
            versionDirName = useProduct.getVersionDirName();
    
        // Be sure to unsub using the version dir before unsubbing the version dir!

        String 
        // the calls to unsub insert the version.dir back into the directory strings
        // this produces incorrect output when running WPVersionInfo
        //dtdDirName     = unsub(versionDirName, useProduct.getDTDDirName(),    versionSub),
        //logDirName     = unsub(versionDirName, useProduct.getLogDirName(),    versionSub),
        //backupDirName  = unsub(versionDirName, useProduct.getBackupDirName(), versionSub),
        //tmpDirName     = unsub(versionDirName, useProduct.getTmpDirName(),    versionSub);
        //versionDirName = unsub(productDirName, versionDirName, productSub);

        dtdDirName     =  useProduct.getDTDDirName(),
        logDirName     =  useProduct.getLogDirName(),
        backupDirName  =  useProduct.getBackupDirName(),
        tmpDirName     =  useProduct.getTmpDirName(); 

        Vector table = new Vector();

        table.add(new String[] { getString("label.product.dir"),        productDirName });
        table.add(new String[] { getString("label.version.dir"),        versionDirName });
        table.add(new String[] { getString("label.version.dtd.dir"),    dtdDirName });
        table.add(new String[] { getString("label.version.log.dir"),    logDirName });
        table.add(new String[] { getString("label.version.backup.dir"), backupDirName });
        table.add(new String[] { getString("label.version.tmp.dir"),    tmpDirName });

        int[] allocations = new int[] {
            PrintFormatter.KEY_ALLOCATION + 5,
            PrintFormatter.VALUE_ALLOCATION - 5
        };

        useFormatter.printTable(allocations, table, 0);
    }

    protected void printPlatform(websphere platform)
    {
        if ( platform == null )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.platform"), true);
        separator();

        Vector table = new Vector();

        table.add(new String[] { getString("label.name"),    platform.getName() });
        table.add(new String[] { getString("label.version"), platform.getVersion() });

        useFormatter.printTable(table);
    }

    protected void printChecklist()
    {
        WPProduct useProduct = getProduct();
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.technology"), true);
        separator();

        Vector table = new Vector();

        String labelInstalled = getString("label.installed");

        for ( int productNo = 0; productNo < WPProduct.PRODUCT_IDS.length; productNo++ ) {
            String nextProductId = WPProduct.PRODUCT_IDS[productNo];

            // Only show the products
            if ( getProduct().productPresent(nextProductId) )
                table.add(new String[] { nextProductId, labelInstalled });
        }

        useFormatter.printTable(table);
    }

    protected void printProduct(product prod)
    {
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.product"), true);
        separator();

        Vector table = new Vector();

        table.add(new String[] { getString("label.name"),        prod.getName() });
        table.add(new String[] { getString("label.version"),     prod.getVersion() });
        table.add(new String[] { getString("label.id"),          prod.getId() });
        table.add(new String[] { getString("label.build.level"), prod.getBuildInfo().getLevel() });
        table.add(new String[] { getString("label.build.date"),  prod.getBuildInfo().getDate() });

        useFormatter.printTable(table);
    }

    protected void printComponent(component comp)
    {
        String componentName = comp.getName();

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.component"), true);
        separator();

        Vector table = new Vector();

        table.add(new String[] { getString("label.component.name"), componentName });
        table.add(new String[] { getString("label.spec.version"),   comp.getSpecVersion() });
        table.add(new String[] { getString("label.build.version"),  comp.getBuildVersion() });
        table.add(new String[] { getString("label.build.date"),     comp.getBuildDate() });

        useFormatter.printTable(table);

        if ( displayComponentDetail() )
            printComponentDetail(comp);
    }

    protected void printComponentDetail(component comp)
    {
        String componentName = comp.getName();

        Vector matchingComponentApplieds = selectApplications(componentName);

        int matchCount = matchingComponentApplieds.size();

        if ( matchCount == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.update.on.component"), 1, true);
        separator(1);

        Vector appliedRows = new Vector();

        for ( int matchNo = 0; matchNo < matchCount; matchNo++ ) {
            Object[] applicationData = (Object[]) matchingComponentApplieds.elementAt(matchNo);

            printApplication(applicationData);
        }
    }

    protected Vector selectApplications(String componentName)
    {
        Vector matchingComponentApplieds = new Vector();

        Iterator parentEFixApplieds = getHistory().getEFixApplieds();

        while ( parentEFixApplieds.hasNext() ) {
            efixApplied parentApplied = (efixApplied) parentEFixApplieds.next();

            int childCount = parentApplied.getComponentAppliedCount();

            for ( int childNo = 0; childNo < childCount; childNo++ ) {
                componentApplied childApplied = parentApplied.getComponentApplied(childNo);

                if ( childApplied.getComponentName().equals(componentName) )
                    matchingComponentApplieds.add(new Object[] { parentApplied, childApplied });
            }
        }

        Iterator parentPTFApplieds = getHistory().getPTFApplieds();

        while ( parentPTFApplieds.hasNext() ) {
            ptfApplied parentApplied = (ptfApplied) parentPTFApplieds.next();

            int childCount = parentApplied.getComponentAppliedCount();

            for ( int childNo = 0; childNo < childCount; childNo++ ) {
                componentApplied childApplied = parentApplied.getComponentApplied(childNo);

                if ( childApplied.getComponentName().equals(componentName) )
                    matchingComponentApplieds.add(new Object[] { parentApplied, childApplied });
            }
        }

        return matchingComponentApplieds;
    }

    protected void printApplication(Object[] applicationData)
    {
        componentApplied nextComponentApplied = (componentApplied) applicationData[1];

        Vector appliedRows = new Vector();

        appliedRows.add(new String[] { getString("label.component.name"),
                                       nextComponentApplied.getComponentName() } );

        if ( applicationData[0] instanceof efixApplied ) {
            efixApplied nextEFixApplied = (efixApplied) applicationData[0];

            appliedRows.add(new String[] { getString("label.update.type"),
                                           getString("label.update.type.efix") } );
            appliedRows.add(new String[] { getString("label.efix.id"),
                                           nextEFixApplied.getEFixId() } );

        }  else {
            ptfApplied nextPTFApplied = (ptfApplied) applicationData[0];

            appliedRows.add(new String[] { getString("label.update.type"),
                                           getString("label.update.type.ptf") } );
            appliedRows.add(new String[] { getString("label.ptf.id"),
                                           nextPTFApplied.getPTFId() } );
        }

        enumUpdateType updateType = nextComponentApplied.getUpdateTypeAsEnum();
        String updateEffectText = updateEffectText(updateType);

        appliedRows.add(new String[] { getString("label.update.effect"),
                                       updateEffectText } );

        boolean nextIsRequired = nextComponentApplied.getIsRequiredAsBoolean();
        String isRequiredText = booleanText(nextIsRequired);

        appliedRows.add(new String[] { getString("label.is.required"),
                                       isRequiredText } );

        boolean nextIsOptional = nextComponentApplied.getIsOptionalAsBoolean();
        String isOptionalText = booleanText(nextIsOptional);

        appliedRows.add(new String[] { getString("label.is.optional"),
                                       isOptionalText } );

        boolean nextIsExternal = nextComponentApplied.getIsExternalAsBoolean();
        String isExternalText = booleanText(nextIsExternal);
        
        appliedRows.add(new String[] { getString("label.is.external"),
                                       isExternalText } );

        if ( nextIsExternal ) {
            appliedRows.add(new String[] { getString("label.root.property.file"),
                                           nextComponentApplied.getRootPropertyFile() } );
            appliedRows.add(new String[] { getString("label.root.property.name"),
                                           nextComponentApplied.getRootPropertyName() } );
            appliedRows.add(new String[] { getString("label.root.property.value"),
                                           nextComponentApplied.getRootPropertyValue() } );
        }

        boolean nextIsCustom = nextComponentApplied.getIsCustomAsBoolean();
        String isCustomText = booleanText(nextIsCustom);

        appliedRows.add(new String[] { getString("label.is.custom"),
                                       isCustomText } );

        appliedRows.add(new String[] { getString("label.log.file.name"),
                                       nextComponentApplied.getLogName() } );
        appliedRows.add(new String[] { getString("label.backup.file.name"),
                                       nextComponentApplied.getBackupName() } );
        appliedRows.add(new String[] { getString("label.install.date"),
                                       nextComponentApplied.getTimeStamp() } );

        componentVersion initialVersion = nextComponentApplied.getInitialVersion();

        if ( initialVersion != null ) {
            appliedRows.add(new String[] { getString("label.initial.spec.version"),
                                           initialVersion.getSpecVersion() } );
            appliedRows.add(new String[] { getString("label.initial.build.version"),
                                           initialVersion.getBuildVersion() } );
            appliedRows.add(new String[] { getString("label.initial.build.date"),
                                           initialVersion.getBuildDate() } );
        }

        componentVersion finalVersion = nextComponentApplied.getFinalVersion();

        if ( finalVersion != null ) {
            appliedRows.add(new String[] { getString("label.final.spec.version"),
                                           finalVersion.getSpecVersion() } );
            appliedRows.add(new String[] { getString("label.final.build.version"),
                                           finalVersion.getBuildVersion() } );
            appliedRows.add(new String[] { getString("label.final.build.date"),
                                           finalVersion.getBuildDate() } );
        }

        getFormatter().printTable(appliedRows, 1);
    }

    protected void printExtension(extension ext)
    {
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();      
        useFormatter.println(getString("info.extension"), true);
        separator();

        Vector table = new Vector();

        table.add(new String[] { getString("label.name"), ext.getName() });
        table.add(new String[] { getString("label.id"),   ext.getId() });

        useFormatter.printTable(table);
    }

    ///

    protected void printEFix(efix fix)
    {
        efixDriver driver = printEFixHeader(fix);

        if ( driver == null )
            return;

        printEFixApars(driver);
        printEFixPlatformPrereqs(driver);
        printEFixProductPrereqs(driver);
        printEFixPrereqs(driver);

        printEFixComponents(driver);

        printEFixConfigTasks(driver);

        printEFixCustomProperties(driver);
    }

    protected efixDriver printEFixHeader(efix fix)
    {
        PrintFormatter useFormatter = getFormatter();
        WPProduct useProduct = getProduct();

        useFormatter.blankLine();
        useFormatter.println(getString("info.efix"), true);
        separator();

        Vector table = new Vector();

        table.add(new String[] { getString("label.id"), fix.getId() });
        table.add(new String[] { getString("label.short.description"), fix.getShortDescription() });

        table.add(new String[] { getString("label.build.date"), fix.getBuildDate() });
        table.add(new String[] { getString("label.build.version"), fix.getBuildVersion() });

        efixDriver driver;

        if ( displayEFixDetail() ) {
            driver = getHistory().getEFixDriverById( fix.getId() );

            boolean isTrial = driver.getIsTrialAsBoolean();
            String isTrialText = booleanText(isTrial);

            table.add(new String[] { getString("label.is.trial"), isTrialText });
            table.add(new String[] { getString("label.expiration.date"), driver.getExpirationDate() });

            table.add(new String[] { getString("label.long.description"), driver.getLongDescription() });

        } else {
            driver = null;
        }

        useFormatter.printTable(table);

        return driver;
    }

    protected void printEFixApars(efixDriver driver)
    {
        int numApars = driver.getAparInfoCount();

        if ( numApars == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.apar"), 1, true);
        separator(1);

        Vector aparRows = new Vector();

        for ( int aparNo = 0; aparNo < numApars; aparNo++ ) {
            aparInfo nextApar = driver.getAparInfo(aparNo);

            aparRows.add(new String[] { getString("label.apar.number"), nextApar.getNumber() });
            aparRows.add(new String[] { getString("label.apar.date"), nextApar.getDate() });
            aparRows.add(new String[] { getString("label.apar.short.description"), nextApar.getShortDescription() });
            aparRows.add(new String[] { getString("label.apar.long.description"), nextApar.getLongDescription() });
        }

        useFormatter.printTable(aparRows, 1);
    }

    protected void printEFixPlatformPrereqs(efixDriver driver)
    {
        int numPlatformPrereqs = driver.getPlatformPrereqCount();

        if ( numPlatformPrereqs == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.supported.platforms"), 1, true);
        separator(1);

        Vector platformPrereqs = new Vector();

        for ( int platformPrereqNo = 0; platformPrereqNo < numPlatformPrereqs; platformPrereqNo++ ) {
            platformPrereq nextPlatformPrereq = driver.getPlatformPrereq(platformPrereqNo);

            Object[] nextRow = new Object[] {
                nextPlatformPrereq.getArchitecture(),
                nextPlatformPrereq.getOSPlatform(),
                nextPlatformPrereq.getOSVersion()
            };

            platformPrereqs.add(nextRow);
        }

        int[] weights = new int[] { 15, 15, 70 };
        useFormatter.printTable(weights, platformPrereqs, 1);
    }

    protected void printEFixProductPrereqs(efixDriver driver)
    {
        int numProductPrereqs = driver.getProductPrereqCount();

        if ( numProductPrereqs == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.supported.products"), 1, true);
        separator(1);

        Vector productPrereqs = new Vector();

        for ( int productPrereqNo = 0; productPrereqNo < numProductPrereqs; productPrereqNo++ ) {
            productPrereq nextProductPrereq = driver.getProductPrereq(productPrereqNo);

            Object[] nextRow = new Object[] {
                nextProductPrereq.getProductId(),
                nextProductPrereq.getBuildVersion(),
                nextProductPrereq.getBuildDate(),
                nextProductPrereq.getBuildLevel()
            };

            productPrereqs.add(nextRow);
        }

        int[] weights = new int[] { 15, 15, 15, 15, 40 };
        useFormatter.printTable(weights, productPrereqs, 1);
    }

    protected void printEFixPrereqs(efixDriver driver)
    {
        int numEFixPrereqs = driver.getEFixPrereqCount();

        if ( numEFixPrereqs == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.println(getString("label.efix.efix.prereqs"), 1, true);
        separator(1);

        Vector efixPrereqs = new Vector();

        for ( int efixPrereqNo = 0; efixPrereqNo < numEFixPrereqs; efixPrereqNo++ ) {
            efixPrereq nextEFixPrereq = driver.getEFixPrereq(efixPrereqNo);

            boolean nextIsNegative = nextEFixPrereq.getIsNegativeAsBoolean();
            String isNegativeText = isNegativeText(nextIsNegative);

            String installIndex = nextEFixPrereq.getInstallIndex();
            if ( installIndex == null )
                installIndex = "";

            Object[] nextRow = new Object[] {
                nextEFixPrereq.getEFixId(),
                isNegativeText,
                installIndex
            };

            efixPrereqs.add(nextRow);
        }

        int[] weights = new int[] { 15, 15, 15, 55 };
        useFormatter.printTable(weights, efixPrereqs, 1);
    }

    protected void printEFixConfigTasks(efixDriver efixdriver) {
       int i = efixdriver.getConfigTaskCount();
       if (i == 0)
          return;
       PrintFormatter printformatter = getFormatter();
       printformatter.println(getString("label.configtasks"), 1, true);
       separator(1);
       Vector vector = new Vector();
       Object blanks[] = { "", ""};
       for (int j = 0; j < i; j++) {
          if ( i>1 && j>0 ) {
             // insert a blank line before config/unconfig names for each config-task
             vector.add( blanks );
          }
          configTask task = efixdriver.getConfigTask(j);
          Object values[] = {
             task.getConfigurationTaskName(),
             isRequiredText( task.isConfigurationRequiredAsBoolean(), true, task.isConfigurationRequiredAsBoolean() ),
          };
          vector.add( values );
          String unconfigName =  task.getUnconfigurationTaskName();
          if ( (unconfigName != null) && (unconfigName.trim().length() != 0) ) {
             values = new Object[] {
                unconfigName,
                isRequiredText( task.isUnconfigurationRequiredAsBoolean(), true, task.isUnconfigurationRequiredAsBoolean()  )
             };
             vector.add( values );
          }
       }

       int ai[] = {
          25, 15
       };
       printformatter.printTable(ai, vector, 1);
    }


    protected void printEFixComponents(efixDriver driver)
    {
        int componentCount = driver.getComponentUpdateCount();

        if ( componentCount == 0 )
            return;

        WPHistory useHistory = getHistory();
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.component.updates"), 1, true);
        separator(1);

        int[] componentWeights = new int[] { 15, 15, 15, 55 };
        int[] contentWeights   = new int[] {  5, 15, 15, 65 };
        int[] nestedWeights    = new int[] {  5, 95,  0,  0 };

        Vector allocations   = new Vector();
        Vector componentRows = new Vector();

        for ( int compNo = 0; compNo < componentCount; compNo++ ) {
            componentUpdate nextUpdate = driver.getComponentUpdate(compNo);

            enumUpdateType updateType = nextUpdate.getUpdateTypeAsEnum();
            String updateEffectText = updateEffectText(updateType);

            String isRequiredText = isRequiredText(nextUpdate.getIsRequiredAsBoolean(),
                                                   nextUpdate.getIsOptionalAsBoolean(),
                                                   nextUpdate.getIsRecommendedAsBoolean());
            Object[] nextRow = new Object[] {
                nextUpdate.getComponentName(),
                updateEffectText,
                isRequiredText
            };

            allocations.add(componentWeights);
            componentRows.add(nextRow);

            boolean nextIsCustom = nextUpdate.getIsCustomAsBoolean();
            String isCustomText = isCustomText(nextIsCustom);

            allocations.add(contentWeights);
                
            Object[] contentRow = new Object[] {
                "",
                isCustomText,
                nextUpdate.getPrimaryContent()
            };

            componentRows.add(contentRow);

            componentApplied nextApplied =
                useHistory.getEFixComponentAppliedById(driver.getId(),
                                                       nextUpdate.getComponentName());
            String isInstalledText = isInstalledText(nextApplied);

            Object[] installedRow = new Object[] { "", isInstalledText, "", "" };

            allocations.add(nestedWeights);
            componentRows.add(installedRow);

            int prereqCount = nextUpdate.getComponentPrereqCount();

            for ( int prereqNo = 0; prereqNo < prereqCount; prereqNo++ ) {
                componentVersion nextPrereq = nextUpdate.getComponentPrereq(prereqNo);

                String requirementText = getString("label.component.requires", 
                                                   nextPrereq.getComponentName(),
                                                   nextPrereq.getSpecVersion());

                Object[] nextPrereqRow = new Object[] { "", requirementText, "", "" };

                allocations.add(nestedWeights);
                componentRows.add(nextPrereqRow);
            }

            componentVersion finalVersion = nextUpdate.getFinalVersion();

            if ( finalVersion != null ) {
                Object[] becomesData = new Object[] {
                    finalVersion.getSpecVersion(),
                    finalVersion.getBuildVersion(),
                    finalVersion.getBuildDate()
                };

                String becomesText = getString("label.becomes", becomesData);

                Object[] nextBecomesRow = new Object[] { "", becomesText, "", "" };

                allocations.add(nestedWeights);
                componentRows.add(nextBecomesRow);
            }
            
            useFormatter.printTable(allocations, componentRows, 1);
        }
    }

    protected void printEFixCustomProperties(efixDriver driver)
    {
        int numProperties = driver.getCustomPropertyCount();

        if ( numProperties == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.custom.properties"), 1, true);
        separator(1);

        Vector propertyData = new Vector();

        for ( int propertyNo = 0; propertyNo < numProperties; propertyNo++ ) {
            customProperty nextProperty = driver.getCustomProperty(propertyNo);

            Object[] nextPropertyData = new Object[] {
                nextProperty.getPropertyName(),
                nextProperty.getPropertyType(),
                nextProperty.getPropertyValue()
            };
                    
            propertyData.addElement(nextPropertyData);
        }
                
        int[] weights = new int[] { 20, 20, 60 };       
        getFormatter().printTable(weights, propertyData, 1);
    }

    ///

    protected void printPTF(ptf aPTF)
    {
        ptfDriver driver = printPTFHeader(aPTF);

        if ( driver == null )
            return;

        printPTFPlatformPrereqs(driver);
        printPTFProductPrereqs(driver);

        printPTFIncludedEFixes(driver);

        printPTFComponents(driver);

        printPTFConfigTasks(driver);

        printPTFCustomProperties(driver);
    }

    protected ptfDriver printPTFHeader(ptf aPTF)
    {
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("info.ptf"), true);
        separator();

        Vector table = new Vector();

        table.add(new String[] { getString("label.id"), aPTF.getId() });
        table.add(new String[] { getString("label.short.description"), aPTF.getShortDescription() });
        table.add(new String[] { getString("label.build.date"), aPTF.getBuildDate() });
        table.add(new String[] { getString("label.build.version"), aPTF.getBuildVersion() });

        ptfDriver driver;

        if ( displayPTFDetail() ) {
            driver = getHistory().getPTFDriverById( aPTF.getId() );
            table.add(new String[] { getString("label.long.description"), driver.getLongDescription() });
        } else {
            driver = null;
        }

        useFormatter.printTable(table);

        return driver;
    }

    protected void printPTFPlatformPrereqs(ptfDriver driver)
    {
        int numPlatformPrereqs = driver.getPlatformPrereqCount();

        if ( numPlatformPrereqs == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.supported.platforms"), 1, true);
        separator(1);

        Vector platformPrereqs = new Vector();

        for ( int platformPrereqNo = 0; platformPrereqNo < numPlatformPrereqs; platformPrereqNo++ ) {
            platformPrereq nextPlatformPrereq = driver.getPlatformPrereq(platformPrereqNo);

            Object[] nextRow = new Object[] {
                nextPlatformPrereq.getArchitecture(),
                nextPlatformPrereq.getOSPlatform(),
                nextPlatformPrereq.getOSVersion()
            };

            platformPrereqs.add(nextRow);
        }

        int[] weights = new int[] { 15, 15, 70 };
        useFormatter.printTable(weights, platformPrereqs, 1);
    }

    protected void printPTFProductPrereqs(ptfDriver driver)
    {
        int numProductPrereqs = driver.getProductPrereqCount();

        if ( numProductPrereqs == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.supported.products"), 1, true);
        separator(1);

        Vector productPrereqs = new Vector();

        for ( int productPrereqNo = 0; productPrereqNo < numProductPrereqs; productPrereqNo++ ) {
            productPrereq nextProductPrereq = driver.getProductPrereq(productPrereqNo);

            Object[] nextRow = new Object[] {
                nextProductPrereq.getProductId(),
                nextProductPrereq.getBuildVersion(),
                nextProductPrereq.getBuildDate(),
                nextProductPrereq.getBuildLevel()
            };

            productPrereqs.add(nextRow);
        }

        int[] weights = new int[] { 15, 15, 15, 15, 40 };
        useFormatter.printTable(weights, productPrereqs, 1);
    }
    
    protected void printPTFIncludedEFixes(ptfDriver driver)
    {
        int numIncludedEFixes = driver.getIncludedEFixCount();

        if ( numIncludedEFixes == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.println(getString("label.included.efixes"), 1, true);
        separator(1);

        Vector includedEFixs = new Vector();

        for ( int includedEFixNo = 0; includedEFixNo < numIncludedEFixes; includedEFixNo++ ) {
            includedEFix nextIncludedEFix = driver.getIncludedEFix(includedEFixNo);

            Object[] nextRow = new Object[] { nextIncludedEFix.getEFixId() };

            includedEFixs.add(nextRow);
        }

        int[] weights = new int[] { 100 };
        useFormatter.printTable(weights, includedEFixs, 1);
    }

    protected void printPTFConfigTasks( ptfDriver ptfdriver) {
       int i = ptfdriver.getConfigTaskCount();
       if (i == 0)
          return;
       PrintFormatter printformatter = getFormatter();
       printformatter.println(getString("label.configtasks"), 1, true);
       separator(1);
       Vector vector = new Vector();
       for (int j = 0; j < i; j++) {
          configTask task = ptfdriver.getConfigTask(j);
          Object values[] = {
             task.getConfigurationTaskName(),
             isNegativeText( task.isConfigurationRequiredAsBoolean() ),
             task.getUnconfigurationTaskName(),
             isNegativeText( task.isUnconfigurationRequiredAsBoolean() )
          };
          vector.add( values );
       }

       int ai[] = {
          35, 15, 35, 15
       };
       printformatter.printTable(ai, vector, 1);
    }


    protected void printPTFComponents(ptfDriver driver)
    {
        int componentCount = driver.getComponentUpdateCount();

        if ( componentCount == 0 )
            return;

        WPHistory useHistory = getHistory();
        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.component.updates"), 1, true);
        separator(1);

        int[] componentWeights = new int[] { 15, 15, 15, 55 };
        int[] contentWeights   = new int[] {  5, 15, 15, 65 };
        int[] nestedWeights    = new int[] {  5, 95,  0,  0 };

        Vector allocations = new Vector();
        Vector componentRows = new Vector();

        for ( int compNo = 0; compNo < componentCount; compNo++ ) {
            componentUpdate nextUpdate = driver.getComponentUpdate(compNo);

            String nextComponentName = nextUpdate.getComponentName();

            enumUpdateType updateType = nextUpdate.getUpdateTypeAsEnum();
            String updateEffectText = updateEffectText(updateType);

            String isRequiredText = isRequiredText(nextUpdate.getIsRequiredAsBoolean(),
                                                   nextUpdate.getIsOptionalAsBoolean(),
                                                   nextUpdate.getIsRecommendedAsBoolean());

            Object[] nextRow = new Object[] {
                nextComponentName,
                updateEffectText,
                isRequiredText
            };

            allocations.add(componentWeights);
            componentRows.add(nextRow);

            boolean nextIsCustom = nextUpdate.getIsCustomAsBoolean();
            String isCustomText = isCustomText(nextIsCustom);
            String nextContent = nextUpdate.getPrimaryContent();

            allocations.add(contentWeights);
                
            Object[] customRow = new Object[] {
                "",
                isCustomText,
                nextContent
            };

            componentApplied nextApplied =
                useHistory.getPTFComponentAppliedById(driver.getId(), nextComponentName);
            String isInstalledText = isInstalledText(nextApplied);

            Object[] installedRow = new Object[] { "", isInstalledText, "", "" };

            allocations.add(nestedWeights);
            componentRows.add(installedRow);

            int prereqCount = nextUpdate.getComponentPrereqCount();

            for ( int prereqNo = 0; prereqNo < prereqCount; prereqNo++ ) {
                componentVersion nextPrereq = nextUpdate.getComponentPrereq(prereqNo);

                String prereqId = nextPrereq.getComponentName();
                String prereqVersion = nextPrereq.getSpecVersion();

                String requirementText = getString("label.component.requires", prereqId, prereqVersion);
                
                Object[] nextPrereqRow = new Object[] { "", requirementText, "", "" };

                allocations.add(nestedWeights);
                componentRows.add(nextPrereqRow);
            }

            componentVersion finalVersion = nextUpdate.getFinalVersion();

            if ( finalVersion != null ) {
                Object[] becomesData = new Object[] {
                    finalVersion.getSpecVersion(),
                    finalVersion.getBuildVersion(),
                    finalVersion.getBuildDate()
                };

                String becomesText = getString("label.becomes", becomesData);

                Object[] nextBecomesRow = new Object[] { "", becomesText, "", "" };

                allocations.add(nestedWeights);
                componentRows.add(nextBecomesRow);
            }
            
            useFormatter.printTable(allocations, componentRows, 1);
        }
    }

    protected void printPTFCustomProperties(ptfDriver driver)
    {
        int numProperties = driver.getCustomPropertyCount();

        if ( numProperties == 0 )
            return;

        PrintFormatter useFormatter = getFormatter();

        useFormatter.blankLine();
        useFormatter.println(getString("label.custom.properties"), 1, true);
        separator(1);

        Vector propertyData = new Vector();

        for ( int propertyNo = 0; propertyNo < numProperties; propertyNo++ ) {
            customProperty nextProperty = driver.getCustomProperty(propertyNo);

            Object[] nextPropertyData = new Object[] {
                nextProperty.getPropertyName(),
                nextProperty.getPropertyType(),
                nextProperty.getPropertyValue()
            };
                    
            propertyData.addElement(nextPropertyData);
        }
                
        int[] weights = new int[] { 20, 20, 60 };
        useFormatter.printTable(weights, propertyData, 1);
    }

    //

    protected String booleanText(boolean value)
    {
        return (getString( value ? "label.true" : "label.false" ));
    }

    protected String isInstalledText(componentApplied anApplied)
    {
        if ( anApplied == null ) {
            return getString("label.is.absent");
        } else {
            String installDate = anApplied.getTimeStamp();
            return getString("label.is.installed", installDate);
        }
    }

    protected String updateEffectText(enumUpdateType updateType)
    {
        String tagText;

        if ( updateType == enumUpdateType.ADD_UPDATE_TYPE )
            tagText = "label.update.effect.add";
        else if ( updateType == enumUpdateType.REPLACE_UPDATE_TYPE )
            tagText = "label.update.effect.replace";
        else if ( updateType == enumUpdateType.REMOVE_UPDATE_TYPE )
            tagText = "label.update.effect.remove";
        else if ( updateType == enumUpdateType.PATCH_UPDATE_TYPE )
            tagText = "label.update.effect.patch";
        else
            tagText = "label.update.effect.unknown";

        return getString(tagText);
    }

    protected String isNegativeText(boolean isNegative)
    {
        return getString( isNegative ? "label.is.negative" : "label.is.positive" );
    }

    protected String isRequiredText(boolean isRequired, boolean isOptional, boolean isRecommended)
    {
        if ( isRequired )
            return getString("label.is.required.tag");

        if ( !isOptional )
            return getString("label.if.possible.tag");

        if ( !isRecommended )
            return getString("label.is.optional.tag");
        else
            return getString("label.is.recommended.tag");
    }

    protected String isCustomText(boolean isCustom)
    {
        return getString( isCustom ? "label.is.custom.tag" : "label.is.standard.tag" );
    }
}
