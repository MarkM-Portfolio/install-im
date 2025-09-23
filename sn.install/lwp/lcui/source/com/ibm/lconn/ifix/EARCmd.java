/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited 2015, 2021                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.ifix;


import java.io.*;
import java.util.*;

import com.ibm.websphere.update.delta.Logger;

/**
 *  
 */
public class EARCmd
{

	public static final boolean isWindows = PlatformUtils.isWindows();
	public static final boolean isLinux = PlatformUtils.isLinux();
	public static final boolean isAIX = PlatformUtils.isAIX();
	public static final boolean isLinux390 = PlatformUtils.isLinux390();
	public static final boolean isISseries = PlatformUtils.isISeries();
	protected Logger logStream;


    public Logger getLogStream() {
		return logStream;
	}


	public void setLogStream(Logger logStream) {
		this.logStream = logStream;
	}

    public EARCmd(String installPath)
    {
        this.DMGRPath = installPath;
    }
    
    protected String DMGRPath;

    public String getDMGRPath()
    {
        return this.DMGRPath;
    }


    public boolean uncompress(String compressedPath,
                              String uncompressedPath, String type)
    {
        logStream.Both("Expand:");
        logStream.Both("    Source EAR (Archive)  : " +  compressedPath);
        logStream.Both("    Target EAR (Directory): " + uncompressedPath);


        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getEARScriptPath()),
            "-ear",            enquote(compressedPath),
            "-operationDir",   enquote(uncompressedPath),
            "-operation",      "expand",
            "-expansionFlags",  type 
        };


        int resultCode;

        try {
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          this.logStream);


        } catch ( Exception ex ) {
        	logStream.Both("Expand: Failed With Exception");
        	logStream.Both("    Source EAR (Archive)  : " +  compressedPath);
            logStream.Both("    Target EAR (Directory): " + uncompressedPath);

            ex.printStackTrace();

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
        	logStream.Both("Expand: Failed by Result Code");
        	logStream.Both("    Source EAR (Archive)  : " + compressedPath);
            logStream.Both("    Target EAR (Directory): " + uncompressedPath);

            logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Expand: OK");
            return true;
        }
    }

    public boolean compress(String compressedPath, String uncompressedPath)
    {
    	logStream.Both("Collapse:");
    	logStream.Both("    Source EAR (Directory): " + uncompressedPath);
    	logStream.Both("    Target EAR (Archive)  : "+ compressedPath);


        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getEARScriptPath()),
            "-ear",          enquote(compressedPath),
            "-operationDir", enquote(uncompressedPath),
            "-operation",    "collapse"
        };
        

        int resultCode;

        try {

                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          this.logStream);

        } catch ( Exception ex ) {
        	logStream.Both("Collapse: Failed With Exception");
        	logStream.Both("    Source EAR (Directory): " + uncompressedPath);
        	logStream.Both("    Target EAR (Archive)  : " + compressedPath);

        	ex.printStackTrace();

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
        	logStream.Both("Collapse: Failed by Result Code");
        	logStream.Both("    Source EAR (Directory): " + uncompressedPath);
        	logStream.Both("    Target EAR (Archive)  : " + compressedPath);

        	logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Collapse: OK");
            return true;
        }
    }
    public boolean uncompress(String compressedPath, String uncompressedPath, String pyFile, String username, String password){
    	

    	compressedPath = compressedPath.replace('\\', '/');
    	uncompressedPath = uncompressedPath.replace('\\', '/');
    	
        logStream.Both("Expand:");
        logStream.Both("    Source EAR (Archive)  : " + compressedPath);
        logStream.Both("    Target EAR (Directory): " + uncompressedPath);


        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getWSADMINScriptPath()),
            "-lang",         "jython",
            "-username",  username,
            "-password",    password,
            "-f",        pyFile,
            compressedPath,     uncompressedPath
            
        };
        

        int resultCode =0;

        try {
               
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          this.logStream);
                
        } catch ( Exception ex ) {
        	logStream.Both("Expand: Failed With Exception");
        	logStream.Both("    Source EAR (Archive)  : " + compressedPath);
            logStream.Both("    Target EAR (Directory): "+  uncompressedPath);

            ex.printStackTrace();

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
        	logStream.Both("Expand: Failed by Result Code");
        	logStream.Both("    Source EAR (Archive)  : " + compressedPath);
            logStream.Both("    Target EAR (Directory): " + uncompressedPath);

            logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Expand: OK");
            return true;
        }
    	
    }

    public boolean updateApp(File updatePackage, String appName, String pyFile, String username, String password){
    	
    	String packagePath = updatePackage.getAbsolutePath();
    	packagePath = packagePath.replace('\\', '/');
       	logStream.Both("Update Application:");
    	logStream.Both("    update EAR package: "+ packagePath);
    	logStream.Both("    update EAR name  : " + appName);


        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getWSADMINScriptPath()),
            "-lang",         "jython",
            "-username",  username,
            "-password",    password,
            "-f",        pyFile,
            appName,     packagePath
            
        };
        

        int resultCode =0;

        try {
               
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          this.logStream);
                
        } catch ( Exception ex ) {
        	ex.printStackTrace();

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
        	logStream.Both("Update Application: Failed by Result Code");
        	logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Update Application:OK ("+appName+")");
            return true;
        }
    	
    }

    public boolean exportApp(String exportFileName, String appName, String pyFile, String username, String password){
    	
    	exportFileName = exportFileName.replace('\\', '/');
       	logStream.Both("Export Application:");
    	logStream.Both("    export EAR filename: "+ exportFileName);
    	logStream.Both("    export APP name  : " + appName);


        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getWSADMINScriptPath()),
            "-lang",         "jython",
            "-username",  username,
            "-password",    password,
            "-f",        pyFile,
            appName,     exportFileName
            
        };
        
        int resultCode =0;

        try {
               
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          this.logStream);
                
        } catch ( Exception ex ) {
        	ex.printStackTrace();

            return false;
        }

        if ( resultCode > 0 ) {
        	logStream.Both("Export Application: Failed by Result Code");
        	logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Export Application:OK ("+appName+")");
            return true;
        }
    }

    public boolean postUpdate(String action, String pyFile, String username, String password){
    	
      	pyFile = pyFile.replace('\\', '/');
       	logStream.Both("Post Update :");
    	logStream.Both("    update action: "+ action);
    	logStream.Both("    py file: "+ pyFile);


        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd = new String[] {
            enquote(getWSADMINScriptPath()),
            "-lang",         "jython",
            "-username",  username,
            "-password",    password,
            "-f",        pyFile,
            action
            
        };
        

        int resultCode =0;

        try {
               
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          this.logStream);
                
        } catch ( Exception ex ) {
        	ex.printStackTrace();

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
        	logStream.Both("Post Update: Failed by Result Code");
        	logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Post Update:OK");
            return true;
        }
    	
    }

   public boolean postUpdate(String action, String ic_home, String pyFile, String username, String password){
   	
     	pyFile = pyFile.replace('\\', '/');
      	logStream.Both("Post Update :");
   	logStream.Both("    update action: "+ action);
   	logStream.Both("    py file: "+ pyFile);


       ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                  ExecCmd.DONT_ECHO_LOG);

       String[] cmd = new String[] {
           enquote(getWSADMINScriptPath()),
           "-lang",         "jython",
           "-username",  username,
           "-password",    password,
           "-f",        pyFile,
           action,      ic_home,
           getDMGRPath()
           
       };
       

       int resultCode =0;

       try {
              
               resultCode = exec.Execute(cmd,
                                         ExecCmd.DONT_ECHO_STDOUT,
                                         ExecCmd.DONT_ECHO_STDERR,
                                         this.logStream);
               
       } catch ( Exception ex ) {
       	ex.printStackTrace();

           return false;
       }

       // TBD: Need the result codes and their meanings.

       if ( resultCode > 0 ) {
       	logStream.Both("Post Update: Failed by Result Code");
       	logStream.Both("Result Code: " + resultCode);

           return false;

       } else {
       	logStream.Both("Post Update:OK");
           return true;
       }
   	
   }

    public static final char escapeChar = '\\';

    public String escapeBackslash(String cmd)
    {
        StringBuffer cmdBuffer = new StringBuffer(cmd);
        Vector<Integer> slashOffsets = new Vector<Integer>();

        int cmdLength = cmdBuffer.length(); 

        for ( int charNo = 0; charNo < cmdLength; charNo++ ) {
            if ( cmdBuffer.charAt(charNo) == escapeChar )
                slashOffsets.add( new Integer(charNo) );
        }

        for ( int lastSlashNo = slashOffsets.size(); lastSlashNo > 0; lastSlashNo-- ) {
            Integer nextOffset = (Integer) slashOffsets.elementAt(lastSlashNo - 1);
            cmdBuffer.insert(nextOffset.intValue() + 1, escapeChar);
        }

        return ( cmdBuffer.toString() );
    }


    public static final String BIN_DIRECTORY = "bin";

    public static final String WINDOWS_EXPANDER_SCRIPT = "EARExpander.bat" ;
    public static final String UNIX_EXPANDER_SCRIPT = "EARExpander.sh" ;
    public static final String iSERIES_EXPANDER_SCRIPT = "EARExpander" ;
    public static final String WINDOWS_WSADMIN_SCRIPT = "wsadmin.bat" ;
    public static final String UNIX_WSADMIN_SCRIPT = "wsadmin.sh" ;
    public static final String iSERIES_WSADMIN_SCRIPT = "wsadmin" ;

    
    public String getEARScriptPath()
    {
        String binPath = getDMGRPath() + File.separator + BIN_DIRECTORY;

        String cmdPath = binPath + File.separator + UNIX_EXPANDER_SCRIPT;

        if ( isWindows )
            cmdPath = binPath + File.separator + WINDOWS_EXPANDER_SCRIPT;
        
        if(isLinux || isAIX || isLinux390)
            cmdPath = binPath + File.separator + UNIX_EXPANDER_SCRIPT;
        
        if(isISseries)
            cmdPath = binPath + File.separator + iSERIES_EXPANDER_SCRIPT;

        logStream.Both("EAR Processing Script Path: " + cmdPath);

        return cmdPath;
    }
    
    public String getWSADMINScriptPath()
    {
        String binPath = getDMGRPath() + File.separator + BIN_DIRECTORY;

        String cmdPath = binPath + File.separator + UNIX_WSADMIN_SCRIPT;

        if ( isWindows )
            cmdPath = binPath + File.separator + WINDOWS_WSADMIN_SCRIPT;
        if(isLinux || isAIX || isLinux390)
            cmdPath = binPath + File.separator + UNIX_WSADMIN_SCRIPT;
        if(isISseries)
            cmdPath = binPath + File.separator + iSERIES_WSADMIN_SCRIPT;


        logStream.Both("EAR Processing Script Path: "+ cmdPath);

        return cmdPath;
    }
    public String getCopyCommand()
    {
        return ( isWindows ? "COPY" : "cp" );
    }

    public boolean copyFile(String sourceFile , String targetFile)
    {
    	logStream.Both("Copy:");
    	logStream.Both("    Source File: "+ sourceFile);
    	logStream.Both("    Target File: "+ targetFile);

        // When doing a straight copy (w/o the launcher),
        // the command adjustment is needed.
        //
        // Otherwise, don't do the command adjustment.

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd;

        if ( isWindows ) {
            cmd = new String[] {
                "CMD.EXE",
                "/C",
                getCopyCommand(),
                enquote(sourceFile),
                enquote(targetFile)
            };
        } else {
            cmd = new String[] {
                getCopyCommand(),
                enquote(sourceFile),
                enquote(targetFile)
            };
        }

        int resultCode;

        try {

            try {
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                          this.logStream);

            } finally {

            }

        } catch ( Exception ex ) {
        	logStream.Both("Copy: Failed With Exception");
            logStream.Both("    Source File: " + sourceFile);
            logStream.Both("    Target File: " + targetFile);

            ex.printStackTrace();

            return false;
        }

        // TBD: Need the result codes and their meanings.

        if ( resultCode > 0 ) {
        	logStream.Both("Copy: Failed by Result Code:");
        	logStream.Both("    Source File: " + sourceFile);
        	logStream.Both("    Target File: " + targetFile);

        	logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Copy: OK");

            return true;
        }
    }

    public String getDeleteCommand()
    {
       // return ( isWindows ? "DEL" : "rm" );
    	return ( isWindows ? "RD/S/Q" : "rm" );
    }

    public boolean deleteFile(String targetFile)
    {
    	logStream.Both("Delete:");
    	logStream.Both("    Target File: "+ targetFile);

        ExecCmd exec = new ExecCmd(ExecCmd.DONT_ADJUST_FOR_PLATFORM,
                                   ExecCmd.DONT_ECHO_LOG);

        String[] cmd;

        if ( isWindows ) {
            cmd = new String[] {
                "CMD.EXE",
                "/C",
                getDeleteCommand(),
                enquote(targetFile)
            };
        } else {
            cmd = new String[] {
                getDeleteCommand(),
                enquote(targetFile)
            };
        }

        int resultCode;

        try {

            try {
                resultCode = exec.Execute(cmd,
                                          ExecCmd.DONT_ECHO_STDOUT,
                                          ExecCmd.DONT_ECHO_STDERR,
                                         this.logStream);

            } finally {

            }

        } catch ( Exception ex ) {
        	logStream.Both("Delete: Failed With Exception");
        	logStream.Both("    Target File: " + targetFile);

        	logStream.Both("Exception: " + ex);

            return false;
        }

        if ( resultCode > 0 ) {
        	logStream.Both("Delete: Failed by Result Code:");
        	logStream.Both("    Target File: " + targetFile);

        	logStream.Both("Result Code: " + resultCode);

            return false;

        } else {
        	logStream.Both("Delete: OK");

            return true;
        }
    }


    public String getPlatformQuote()
    {
        return ( isWindows ? "\"" : "" );
    }


    public String enquote(String text)
    {
        return text;

    }
    public static void main(String[] args)throws Exception{  
        System.out.println(System.getProperty("java.io.tmpdir") );
        System.out.println(System.getProperty("file.separator") );

    	String dmgrPath = "C:\\Program Files (x86)\\IBM\\WebSphere\\AppServer\\profiles\\Dmgr01";
         EARCmd earCmd = new EARCmd(dmgrPath);
         String compressedPath = "C:\\Activities.ear";
         String uncompressedPath ="C:\\ifix\\work";
         earCmd.uncompress(compressedPath, uncompressedPath, "war");
   }   
}
