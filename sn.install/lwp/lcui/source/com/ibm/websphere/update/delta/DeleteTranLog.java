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
package com.ibm.websphere.update.delta;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Description of the Class
 * @author      rohits
 * @created     December 3, 2001
 * @modified    January 17, 2001 check wheather the file exists before backing/deleting it
 */

public class DeleteTranLog extends UpdateAction
{
   // The current CMVC version of this program

   public static final String pgmVersion = "1.2" ;
   // The current CMVC version of this program

   public static final String pgmUpdate = "9/26/03" ;

    public static final String AE_Component_Id = "Admin_server";

    private final static String LOGFILEPROPERTY =
        "com.ibm.ejs.sm.adminServer.logFile";

    private String wasHome = null;
    private String[] componentInstalled = null;
    private Vector backupFiles = null;
    private boolean debug = false;
    
    public DeleteTranLog()
    {
        super();
    }
    
    /*
     *  backup :- TBD
     *  find how to log information :- TBD
     *  1. This will be passed in a) was_home 2) Array of Components if Admin_server is there then
     *  1a. provide a means of backing anything if needed from above
     *  2 find below property from was_home
     *  3 look for comma if any
     *  4 delete file
     *  com.ibm.ejs.sm.adminServer.logFile=/opt/WebSphere/AppServer/tranlog/epicurus.rch
     *  land.ibm.com_tranlog1,/opt/WebSphere/AppServer/tranlog/epicurus.rchland.ibm.com_
     *  tranlog2
     *  5 delete all <sever name>XAResources files (need example here)
     *  error recovery ???
     *  6 do we need to update admin.config (???)
     *  7 do we need to create any other file
     *  restore TBD, need to be deleted. install fail if tranlog wont
     */

    /**
     *  <p>The main program for the DeleteTranLog class.
     *  This main method is for testing only.</p>
     *
     *  <p>Parameter 1 is the WebSphere installation location.</p>
     *
     *  @param  args  The command line arguments
     */

    public static void main(String[] args)
    {
        System.out.println("Start of Testing DeleteTranLog Version " + pgmVersion);

        DeleteTranLog dtl = new DeleteTranLog();

        String[] components = {
            "Client",
            "Admin_common",
            // "Admin_server",
            "Server",
            "Samples",
            "Console",
            "Common",
            "Deploytools",
            "Plugins",
            "Samples_Common",
            "Server_Common",
            "Tools_Common",
            "J2EEClient",
            "JTCClient"
        };
        
        PODef[] defs = {
            new PODef("version", "boolean",     "false"),
            new PODef("debug",   "boolean",     "false"),
            new PODef("?",       "BuiltinHelp", "false"),
            new PODef("WasHome", "Directory",   null),
        };

        POProcessor po = new POProcessor(defs, args, null);
        
        StringBuffer msgSB = new StringBuffer();
        StringBuffer errSB = new StringBuffer();

        boolean debug = false;
        
        int rc = dtl.process(po.getString("WasHome"), components, po, msgSB, errSB, debug);
        
        System.out.println("Process ended with rc: " + rc);
        System.out.println("");
        System.out.println("MessageBuffer length: " + msgSB.length());
        System.out.println(msgSB.toString());
        
        System.out.println("");
        System.out.println("ErrorBuffer length: " + errSB.length());
        System.out.println(errSB.toString());
    }
    
    /**
	 * @param debug  the debug to set
	 * @uml.property  name="debug"
	 */
    public void setDebug(boolean newDebug)
    {
        debug = newDebug;
    }
    
    /**
	 * @return  the debug
	 * @uml.property  name="debug"
	 */
    public boolean getDebug()
    {
        return debug;
    }
    
    public void setWASHome(String newWASHome)
    {
        wasHome = newWASHome;
    }

    public String getWASHome()
    {
        return wasHome;
    }

    /**
	 * @param componentInstalled  the componentInstalled to set
	 * @uml.property  name="componentInstalled"
	 */
    public void setComponentInstalled(String[] newCI)
    {
        componentInstalled = newCI;
    }
    
    /**
	 * @return  the componentInstalled
	 * @uml.property  name="componentInstalled"
	 */
    public String[] getComponentInstalled()
    {
        return componentInstalled;
    }
    
    /**
	 * @param backupFiles  the backupFiles to set
	 * @uml.property  name="backupFiles"
	 */
    public void setBackupFiles(Vector newBackupFiles)
    {
        backupFiles = newBackupFiles;
    }
    
    /**
     * Gets the backupFiles attribute of the DeleteTranLog object.
     *
     * @return The backupFiles value
     */

    public Vector file2Backup(String root,
                              String[] components,
                              StringBuffer messages,
                              StringBuffer errorMessage,
                              boolean debug)
    {
        if ( backupFiles == null ) {
            setWASHome(root);
            setComponentInstalled(components);
            setDebug(debug);
            
            try {
                determineTranFiles(messages, errorMessage);

            } catch ( Exception e ) {
            }
        }

        return backupFiles;
    }
    
    public int process(String root,
                       String[] components,
                       POProcessor po,
                       StringBuffer messages,
                       StringBuffer errorMessage,
                       boolean debug)
    {
        // Check whether we are on AE
        
        messages.append("DeleteTranLog invoked, ");
        messages.append("version ").append(pgmVersion);
        messages.append(System.getProperty("line.separator"));

        int rc;
        
        if ( backupFiles == null ) {
            setWASHome(root);
            setComponentInstalled(components);
            setDebug(debug);

            try {
                if ( determineTranFiles(messages, errorMessage) )
                    rc = delete(messages, errorMessage);
                else
                    rc = 1;

            } catch ( Exception e ) {
                rc = 0;
            }
        } else {
	    rc = 1;
	}

        return rc;
    }
    
    /**
     *  Based on the rules above build a vector of files to be deleted
     *
     *  @return              Description of the Returned Value
     *  @exception Exception Description of Exception
     */

    private boolean determineTranFiles(StringBuffer messages,
                                       StringBuffer errorMessages)
        throws Exception
    {
        if ( !checkForAE() ) {
            messages.append(AE_Component_Id);
            messages.append(" component not detected.");
            messages.append(System.getProperty("line.separator"));

            return false;
        }
        
        if ( backupFiles == null )
            backupFiles = new Vector();
        
        if ( getWASHome() == null )
            return false;

        if ( getDebug() )
            System.out.println("WebSphere Home located at " + getWASHome());
        
        String fileName = wasHome + File.separator + "bin" + File.separator + "admin.config";
        
        if ( getDebug() )
            System.out.println("Reading Admin Config " + fileName);

        Properties prop = new Properties();
        FileInputStream is = null;
        
        try {
            is = new FileInputStream(fileName);

        } catch ( FileNotFoundException fnfe ) {
            errorMessages.append("File ");
            errorMessages.append(fileName);
            errorMessages.append(" Not Found exiting ");
            errorMessages.append(System.getProperty("line.separator"));

            throw fnfe;
        }

        if ( is == null ) {
            errorMessages.append("Resource property file ");
            errorMessages.append(fileName);
            errorMessages.append(" Not Found.");
            errorMessages.append(System.getProperty("line.separator"));

            throw new Exception("InputStream was null");
        }
        
        try {
            prop.load(is);

        } catch ( IOException ioe ) {
            errorMessages.append("IOException trying to load properties");
            errorMessages.append(System.getProperty("line.separator"));
            errorMessages.append(getStackTraceAsString(ioe));
            errorMessages.append(System.getProperty("line.separator"));

            throw ioe;
        }
        
        try {
            is.close();

        } catch ( IOException ioe ) {
            errorMessages.append("IOException trying to close IO Stream");
            errorMessages.append(System.getProperty("line.separator"));
            errorMessages.append(getStackTraceAsString(ioe));
            errorMessages.append(System.getProperty("line.separator"));

            throw ioe;
        }
        
        String logFile = (String) prop.get(LOGFILEPROPERTY);
        
        StringTokenizer st = new StringTokenizer(logFile, ",");
        
        while ( st.hasMoreTokens() ) {
            // d117752 check whether the file exist before adding it to backup list.

            String nextBackupName = (String) st.nextToken();
            
            File nextBackupFile = new File(nextBackupName);
            
            if ( nextBackupFile.exists() ) {
                backupFiles.add(nextBackupName);

                System.out.println("Backup File: " + nextBackupName);
            }
        }
        
        File aFile = new File(wasHome + File.separator + "properties");

        String[] fileList = aFile.list();
        
        for ( int i = fileList.length - 1; i >= 0; i-- ) {
            if ( fileList[i].indexOf("XAResource") != -1 ) {

                backupFiles.add( wasHome + File.separator + "properties" + File.separator + fileList[i] );

                if (getDebug())
                    System.out.println("File to backup " + fileList[i]);
            }
        }

        return true;
    }
    
    /**
     *  Delete Files which were determined earlier
     *
     *  @return    Description of the Returned Value
     */

    private int delete(StringBuffer messages, StringBuffer errorMessage)
    {
        if ( !checkForAE() ) {
            messages.append(AE_Component_Id);
            messages.append(" component not detected.");
            messages.append(System.getProperty("line.separator"));

            return 0;
        }

        Iterator i = backupFiles.iterator();

        while ( i.hasNext() ) {
            File f = (new File((String) i.next())).getAbsoluteFile();
            
            if ( getDebug() ) {
                f.renameTo(new File(f.getAbsolutePath() + "backup"));

            } else {
                // d117752

                if ( f.exists() ) {
                    if ( f.delete() ) {
                        messages.append(f.toString());
                        messages.append(" was deleted successfully");
                        messages.append(System.getProperty("line.separator"));
                    } else {
                        errorMessage.append(f.toString());
                        errorMessage.append(" was not deleted successfully, Please refer to Release notes");
                        errorMessage.append(System.getProperty("line.separator"));

                        return 0;
                    }

                } else {
                    messages.append(f.toString());
                    messages.append("does not exist, this should not happen. Please refer to Release notes");
                    messages.append(System.getProperty("line.separator"));
                }
            }
        }

        return 1;
    }
    
    private boolean checkForAE()
    {
        String[] components = getComponentInstalled();

        for ( int i = components.length - 1; i >= 0; i-- ) {
            if ( getDebug() )
                System.out.println("Debug #550 -- Component=(" + components[i] + ")");
            
            if ( components[i].equals(AE_Component_Id) )
                return true;
        }
        
        return false;
    }
}
