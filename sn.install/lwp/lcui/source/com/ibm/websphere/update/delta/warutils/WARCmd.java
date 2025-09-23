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
 *  @ (#) WARCmd.java
 *
 *  Support class to expand and collapse WAR files.
 *
 *  @author     eedavis
 *  @created    Feb 25, 2005
 */

import java.io.*;
import java.util.Vector;
import java.util.zip.*;
//import java.net.*;

//import com.ibm.websphere.update.delta.*;
import com.ibm.websphere.update.delta.ExecCmd;
import com.ibm.websphere.update.delta.earutils.EARCmd;
//import com.ibm.websphere.update.util.WPConfig;
//import com.ibm.websphere.update.util.WPConstants;
//import com.ibm.websphere.update.util.PlatformUtils;
//import com.ibm.websphere.update.util.WPConfig;
//import com.ibm.websphere.update.util.WPConstants;
//import com.ibm.websphere.update.util.ZipUtils;

public class WARCmd extends EARCmd
{
    public final static String pgmVersion = "1.3" ;
    public final static String pgmUpdate = "10/4/05" ;
    
    public final String DIR_META_INF    = "META-INF";
    public final static String TMP_EAR_NAME_PREFIX = "PUI_WAR";
    public final static String TMP_EAR_NAME_SUFFIX = ".ear";
    public final static String TMP_EAR_NAME        = TMP_EAR_NAME_PREFIX + TMP_EAR_NAME_SUFFIX;
    
    private String FQ_TMP_EAR           = null;
    private String FQ_TMP_WORK_DIR      = null;
    private String FQ_TMP_EAR_CANONICAL = null;
    private String FQ_WAR_CANONICAL     = null;


    public static final String debugPropertyName = "com.ibm.websphere.update.delta.warutils.debug" ;
    public static final String debugTrueValue = "true" ;

    public static boolean debug;
    static {
        String debugText = System.getProperty(debugPropertyName);
    
        debug = ( (debugText != null) &&
                    debugText.equalsIgnoreCase(debugTrueValue) );
    }
    
    public WARCmd(String installPath, StringBuffer messages, StringBuffer errors)
    {
        //NOTE: EARCmd is expecting WAS_HOME, not WP_HOME for installPath . . .
        super(installPath, messages, errors);
        
        FQ_TMP_WORK_DIR = System.getProperty("java.io.tmpdir") + File.separator + "puiwarworkarea";
        FQ_TMP_EAR = FQ_TMP_WORK_DIR + File.separator + TMP_EAR_NAME;
        
        if ( debug ) {
            System.out.println("Creating WARCmd on: " + installPath);
            System.out.println("    \"debug\" is ON.");
            log("Creating WARCmd on: " + installPath);
            log("    \"debug\" is ON.");
        }

    }
    
    public boolean buildTmpEarFromWar ( String warNameAndPath ){
        
        // make certain we've been given a .war name . . .
        if ( !warNameAndPath.endsWith( ".war" ) ) {
            logError( this.toString() + " : warNameAndPath does not end in \".war\": " + warNameAndPath );
            return false;
        }
        //      . . . make certain named .war exists . . .
        File m_warFile = new File( warNameAndPath );
        if ( !m_warFile.exists() ) {
            logError( this.toString() + " : file not found: " + warNameAndPath );
            return false;
        }
        // . . . make certain runway is clear for tmp EAR . . .
        if( !deleteTmpEar() )
            return false;

        // . . . EARExpander requires canonical path . . .
        FQ_TMP_EAR_CANONICAL = getFqTmpEarCanonical();
        if( null == FQ_TMP_EAR_CANONICAL ){
            return false;
        }
        //get a tmp dir
        final String DIR_TMP_EAR = System.getProperty("java.io.tmpdir") + File.separator + TMP_EAR_NAME_PREFIX;
        String DIR_FQ_TMP_EAR_CANONICAL = ""; 
        File m_tmpDir = new File( DIR_TMP_EAR );
        if ( m_tmpDir.exists() ) {
            if ( !m_tmpDir.delete() ) {
                logError( this.toString() + " : cannot delete: " + m_tmpDir.toString() );
                return false;
            }
        }
        log( this.toString() + " : creating temp directory : " + DIR_TMP_EAR );
        m_tmpDir.mkdirs();
        try {
            DIR_FQ_TMP_EAR_CANONICAL = m_tmpDir.getCanonicalPath();    
        } catch ( IOException ioe ) {
            logError( this.toString() + " : ERROR: failed conversion to canonical path: " + m_tmpDir.toString() );
            return false;
        }
        //resolve supplied WAR path and file to canonical for copy
        try {
            FQ_WAR_CANONICAL = (new File( warNameAndPath )).getCanonicalPath();
        } catch ( IOException ioe_war ){
            logError( this.toString() + " : ERROR: failed conversion to canonical path: " + warNameAndPath );
            return false;
        }

        //Return variable. Errors above return 'false'.
        boolean retval = true;
        //lay out files to build EAR
        if ( retval )
            retval = writeApplicationXml( m_tmpDir );
        if ( retval )
            retval = writeWasPolicy( m_tmpDir );
        if ( retval )
            retval = copyFile( FQ_WAR_CANONICAL , DIR_TMP_EAR);  //copyFile uses shell to copy, so dir on end is OK w/o warfilename
        //call EARExpander to create EAR
        if ( retval )
            retval = compress( FQ_TMP_EAR_CANONICAL, DIR_FQ_TMP_EAR_CANONICAL );

        // mark tmp dir for deletion
        // NOTE: Do this AFTER EARExpander operations, or it will fail (no knowledge of the files in the dir)
        if ( !debug ) {
            deleteOnExitRecurse( DIR_TMP_EAR );
        }


        return retval;
    }
    
    public String getCanonicalPathForFile( File file ){
        try {
            return file.getCanonicalPath().toString();    
        } catch ( IOException ioe ) {
            logError( this.toString() + " : ERROR: failed conversion to canonical path: " + file.toString() );
            return null;
        }
    }
    
    protected boolean deleteTmpEar(){
        File tmpEar = new File( FQ_TMP_EAR );
        if ( tmpEar.exists() ) {
            if ( !tmpEar.delete() ) {
                logError( this.toString() + " : cannot delete: " + tmpEar.toString() );
                return false;
            }
        }
        return true;
    }
    
    protected boolean writeApplicationXml( File tmpDir ){
        
        final String FILE_APPLICATION_XML = "application.xml";
        
        if ( !tmpDir.exists() ) {
            logError( this.toString() + " : Temp directory does not exist: " + tmpDir );
            return false;
        } else if ( !tmpDir.isDirectory() ) {
            logError( this.toString() + " : Specified File obj is not a directory: " + tmpDir );
            return false;
        }
        
        File dirMetaInf = new File ( tmpDir, DIR_META_INF );
        dirMetaInf.mkdirs();
        
        File applicationXml = new File( dirMetaInf, FILE_APPLICATION_XML );
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter (new FileOutputStream (applicationXml));

            /*  ---  BEGIN contents of file  ---  */
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<!DOCTYPE application PUBLIC \"-//Sun Microsystems, Inc.//DTD J2EE Application 1.3//EN\" \"http://java.sun.com/dtd/application_1_3.dtd\"> ");
            writer.println();
            writer.println("   <application id=\"Application_ID\">");
            writer.println("      <display-name>appearance_war</display-name>");
            writer.println("      <module id=\"WebModule_1111478863697\">");
            writer.println("         <web>");
            writer.println("            <web-uri>appearance.war</web-uri>");
            writer.println("            <context-root>/wps/PA_1_0_3H</context-root>");
            writer.println("         </web>");
            writer.println("      </module>");
            writer.println("   </application>");
            writer.println();   //leave one empty line at the end of the file . . .
            /*  ---  END contents of file  ---  */
            
        } catch ( IOException ioe ) {
            logError( this.toString() + " : Error writing " + FILE_APPLICATION_XML + " : " + ioe.toString() );
            ioe.printStackTrace();
            return false;
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }
        
        // errors above return "false", so if we made it this far . . .
        return true;
    }
    
    protected boolean writeWasPolicy( File tmpDir ){
        
        final String FILE_WAS_POLICY = "was.policy";
        
        if ( !tmpDir.exists() ) {
            logError( this.toString() + " : Temp directory does not exist: " + tmpDir );
            return false;
        } else if ( !tmpDir.isDirectory() ) {
            logError( this.toString() + " : Specified File obj is not a directory: " + tmpDir );
            return false;
        }
        
        File dirMetaInf = new File ( tmpDir, DIR_META_INF );
        dirMetaInf.mkdirs();
        
        File wasPolicy = new File( dirMetaInf, FILE_WAS_POLICY );
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter (new FileOutputStream (wasPolicy));

            /*  ---  BEGIN contents of file  ---  */
            writer.println("//");
            writer.println("// Template policy file for enterprise application.");
            writer.println("// Extra permissions can be added if required by the enterprise application.");
            writer.println("//");
            writer.println("// NOTE: Syntax errors in the policy files will cause the enterprise application FAIL to start.");
            writer.println("//       Extreme care should be taken when editing these policy files. It is advised to use");
            writer.println("//       the policytool provided by the JDK for editing the policy files");
            writer.println("//       (WAS_HOME/java/jre/bin/policytool).");
            writer.println("//");
            writer.println();
            writer.println("grant codeBase \"file:${application}\" {");
            writer.println("};");
            writer.println();
            writer.println("grant codeBase \"file:${jars}\" {");
            writer.println("};");
            writer.println();
            writer.println("grant codeBase \"file:${connectorComponent}\" {");
            writer.println("};");
            writer.println();
            writer.println("grant codeBase \"file:${webComponent}\" {");
            writer.println("};");
            writer.println();
            writer.println("grant codeBase \"file:${ejbComponent}\" {");
            writer.println("};");
            writer.println();   //leave one empty line at the end of the file . . .
            /*  ---  END contents of file  ---  */
            
        } catch ( IOException ioe ) {
            logError( this.toString() + " : Error writing " + FILE_WAS_POLICY + " : " + ioe.toString() );
            ioe.printStackTrace();
            return false;
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }
        
        // errors above return "false", so if we made it this far . . .
        return true;
    }
    
    protected boolean extractWarFromTmpEar( String warName ){
        
        ZipInputStream m_zis = null;
        BufferedOutputStream m_bos = null;
        
        try {
            m_zis = new ZipInputStream( new FileInputStream(getFqTmpEar()));
            ZipEntry m_ze;
            
            log( "WARCmd.extractWarFromTmpEar() -- Extracting: '" + warName + "' from: '" + getFqTmpEar() + "'" );
            
            while ( null != (m_ze = m_zis.getNextEntry()) ) {
                if ( debug ) {
                    log( "WARCmd.extractWarFromTmpEar(): " + m_ze.getName() + " .equals() " + warName + " ?: " + m_ze.getName().equals(warName) );    
                }
                if ( m_ze.getName().equals(warName) ) {
                    
                    File newWar = new File( FQ_TMP_WORK_DIR, warName );
                    
                    log( "WARCmd.extractWarFromTmpEar() -- Extracting: '" + newWar + "' from: '" + getFqTmpEar() + "'" );
                    
                    m_bos = new BufferedOutputStream( new FileOutputStream(newWar) );
                    boolean doneReadingInputStream = false;
                    int ioBufferSize = 2048;
                    byte[] ioBuffer = new byte[ioBufferSize];
                    
                    // remove existing WAR, will write new version in its place . . .
                    if ( newWar.exists() ) {
                        newWar.delete();
                    }
                    
                    while (!doneReadingInputStream ) {
                        int bytesReadThisTrip = m_zis.read( ioBuffer, 0, ioBufferSize );
                        
                        if ( 0 < bytesReadThisTrip ) {
                            m_bos.write( ioBuffer, 0, bytesReadThisTrip );
                            if ( debug ) {
                                log( "WARCmd.extractWarFromTmpEar() -- Writing: '" + bytesReadThisTrip + "' bytes to: '" + newWar + "'" );
                            }
                        } else {
                            doneReadingInputStream = true;
                        }
                    }
                }
            }

        } catch ( IOException ioe ) {
            logError( this.toString() + " : Error creating extracting " + warName + " from: " + getFqTmpEar() + " : " + ioe.toString() );
            errors.append( this.toString() + " : Error creating extracting " + warName + " from: " + getFqTmpEar() + " : " + ioe.toString() );
            return false;
        } finally {
            if ( null != m_zis ) try { m_zis.close(); } catch (IOException ioe ) {}
            if ( null != m_bos ) try { m_bos.close(); } catch (IOException ioe ) {}
        }
        
        return true;  //NOC
    }

    public String getFqTmpEar() {
        return FQ_TMP_EAR;
    }
    
    public String getFqTmpEarCanonical() {
        File tmpEar = new File( FQ_TMP_EAR );
        return getCanonicalPathForFile( tmpEar );
    }
    
    public String getFqTmpWorkDir() {
        return FQ_TMP_WORK_DIR;
    }
    
}
