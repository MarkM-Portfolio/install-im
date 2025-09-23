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

package com.ibm.websphere.update.launch;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import com.ibm.websphere.update.silent.UpdateInstallerArgs;
import com.ibm.websphere.update.util.PlatformUtils;
import com.ibm.websphere.update.util.PuiProfileToken;



public class Launcher
{
    // Program versioning ...

    public static final String pgmVersion = "1.1" ;
    // Program versioning ...

    public static final String pgmUpdate = "10/27/02" ;

    // Debugging support ...

    public static boolean debug;
    public static PrintStream out = System.out;

    public static final String debugPropertyName = "com.ibm.websphere.update.launch.debug" ;
    public static final String debugTrueValue = "true" ;

    static {
        String debugValue = System.getProperty(debugPropertyName);
        debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
    }

    protected static void debug(String text)
    {
        if ( !debug )
            return;

        out.println(text);
    }

    protected static void debug(String text1, String text2)
    {
        if ( !debug )
            return;

        out.print(text1);
        out.println(text2);
    }

    // Main method ... wrapper for a second launch.
    // This is a stripped down copy of WSLauncher.
    // Extension class loader support was removed.
    //
    // The system propert 'ws.output.encoding'
    // can be used to override the encoding used for system.out/system.err.
    //
    // The allowed values are:
    //   file    - leave the file encoding (file.encoding) unchanged;
    //   console - change the file encoding Cp1252 to Cp850, but otherwise leave it unchanged;
    //   other   - the value is an encoding name; change to the named encoding.

    public static void main(String args[])
    {
    	/* String[] args = new String[inputArgs.length - 1];
    	 for(int i=0,j = 0; i<inputArgs.length; i++,j++){
    		 if(!"-silent".equals(inputArgs[i])){
    			 System.out.println("inputArgs[i]: " + inputArgs[i]);
    			 args[j] = inputArgs[i];
    		 }else{
    			 j--;
    		 }
    	 }*/
    	
        if ( args.length < 1 ) {
            System.err.println("No main class was specified.");
            System.err.println("Usage: Launcher <className> <arg> ...");
            System.exit(-1);
            return;
        }

        String targetClassName = args[0];

        if ( debug ) {
            debug("Launch: ", targetClassName);
            for (int argNo = 1; argNo < args.length; argNo++)
                debug("  [" + argNo + "]: ", args[argNo]);
        }                                                                              

        /*
        String[] testLauncherArgs = buildArgs(args);

        UpdateInstallerArgs LauncherArgs = new UpdateInstallerArgs();
      	LauncherArgs.parse(testLauncherArgs);

        System.out.println("test install dir " + LauncherArgs.installDir );

        // if (LauncherArgs.installDir.toString().indexOf("ProdData") > -1 ) 
        if (LauncherArgs.installDir.toString().indexOf("PortalServer") > -1 )
        {
            System.out.println("YES");
        }
        */


      

        // For Iseries switch to WAS User. 

        if (PlatformUtils.isISeries() )
        {

            String[] testLauncherArgs = buildArgs(args);

            UpdateInstallerArgs LauncherArgs = new UpdateInstallerArgs();
            LauncherArgs.parse(testLauncherArgs);

            System.out.println("ISeries install dir " + LauncherArgs.installDir );

            if (LauncherArgs.installDir.toString().indexOf("ProdData") > -1 ) 
            // if (LauncherArgs.installDir.toString().indexOf("PortalServer") > -1 )
            {
              System.out.println("ProdData fix. Will not switch to QEJBSVR");
              /*
              try 
               {

                       AS400Credential originalCredentials = null;

                       // Create an object that represents the WAS OS/400 user profile.
                       ProfileTokenCredential pt = new ProfileTokenCredential();
                       pt.setSystem(new AS400("localhost", "*CURRENT", "*CURRENT"));
                       pt.setTokenType(ProfileTokenCredential.TYPE_SINGLE_USE);
                       pt.setTimeoutInterval(60);

                       pt.setToken("QSYS", ProfileTokenCredential.PW_NOPWDCHK);

                       originalCredentials = pt.swap(true);
                       pt.destroy();

                  } catch (Exception ex) {

                       System.out.println(
                               "Switch to user profile "
                                       + "QSYS"
                                       + " failed, verify 'switch to' profile exists or the current profile has the correct authority.");
                }
                */

            }
            else
            {
 
                System.out.println("UserData fix. Please switch to QEJBSVR");
                
                PuiProfileToken.getToken();
                
                /* d186822
                try 
                {
                        AS400Credential originalCredentials = null;
	
			// Create an object that represents the WAS OS/400 user profile.
			ProfileTokenCredential pt = new ProfileTokenCredential();
			pt.setSystem(new AS400("localhost", "*CURRENT", "*CURRENT"));
			pt.setTokenType(ProfileTokenCredential.TYPE_SINGLE_USE);
			pt.setTimeoutInterval(60);
				 
                        pt.setToken("QEJBSVR", ProfileTokenCredential.PW_NOPWDCHK);
					 
			originalCredentials = pt.swap(true);
			pt.destroy();
		  
		   } catch (Exception ex) {
			 
			System.out.println(
				"Switch to WAS user profile "
					+ "QEJBSVR"
					+ " failed, verify 'switch to' profile exists or the current profile has the correct authority.");
		 }
                 */
            }
         }
        /*

         try 
         {  
           File newFile = new File("/QIBM/UserData/puitest");
           newFile.createNewFile();
         }
         catch ( Exception ex2 )
         {
                 System.out.println("iseries file create " + ex2.toString() );
         }
         */


        try {
            setEncoding(); // throws UnsupportedEncodingException
        } catch ( UnsupportedEncodingException ex ) {
            handleError(ex, "Unable to update encoding:");
            return;
        }

        Class targetClass;

        try {
            targetClass = locateTarget(targetClassName); // throws Exception
        } catch ( Exception ex ) {
            handleError(ex, "Unable to load main class " + targetClassName);
            return;
        }

        Method mainMethod;

        try {
            mainMethod = locateMain(targetClass); // throws Exception
        } catch ( Exception ex ) {
            handleError(ex, "Unable to locate main method");
            return;
        }

        // Defer this until late; don't want the overhead
        // of building the args array if there is an error.

        String[] newArgs = buildArgs(args);

        Object result;

        try {
            result = invokeMain(mainMethod, newArgs); // throws Exception
        } catch ( Exception ex ) {
            handleError(ex, "Failure during invocation");
            return;
        }

        debug("Success");
    }

    protected static void handleError(Exception ex, String text)
    {
        System.err.println(text);

        ex.printStackTrace(System.err);

        System.exit(-1);
    }

    protected static void setEncoding()
        throws UnsupportedEncodingException
    {
        String fileEncoding = System.getProperty("file.encoding");
        debug("System file encoding: ", fileEncoding);

        String encoding = System.getProperty("ws.output.encoding");

        if ( encoding == null ) {
            debug("No encoding setting; leaving preset file encoding.");
            return;
        } else {
            out.println("Set encoding: " + encoding);
        }

        if ( encoding.equals("file") ) {
            debug("File encoding set; leaving preset file encoding.");
            return;
        }

        String encodingOverride;

        if ( encoding.equals("console") ) {
            debug("Console encoding set; checking file encoding.");

            if (fileEncoding.equals("Cp1252")) {
                debug("File encoding is Cp1252; updating to Cp850.");
                encodingOverride = "Cp850";
            } else {
                debug("File encoding is not Cp1252; leaving preset encoding.");
                encodingOverride = null;
            }
        } else {
            debug("Alternate encoding set; updating encoding.");
            encodingOverride = encoding;
        }

        if ( encodingOverride == null )
            return;

        debug("Updating encoding");

        System.setOut( new NlvPrintStream(System.out, true, encodingOverride) );
        System.setErr( new NlvPrintStream(System.err, true, encodingOverride) );
        // both throw UnsupportedEncodingException
    }

    // A change from WSLauncher, which builds an extended class loader.
    // The installer doesn't need that function, so it was removed.

    protected static Class locateTarget(String className)
        throws Exception
    {
        debug("Locating target class: ", className);

        return Launcher.class.getClassLoader().loadClass(className);
        // throws Exception
    }

    protected static Method locateMain(Class targetClass)
        throws Exception
    {
        debug("Locating main method");

        return targetClass.getMethod("main", new Class[] { String[].class });
        // throws Exception
    }

    protected static String[] buildArgs(String[] args)
    {
        debug("Building proper arguments");

        String newArgs[] = new String[args.length - 1];

        System.arraycopy(args, 1, newArgs, 0, newArgs.length);

        return newArgs;
    }

    protected static Object invokeMain(Method mainMethod, String[] args)
        throws Exception
    {
        debug("Invoking main method");

        return mainMethod.invoke(null, new Object[] { args });
    }
}
