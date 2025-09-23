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

package com.ibm.websphere.update.silent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import com.ibm.websphere.update.ptf.OSUtil;

/**
 * Class: UpdateInstaller.java Abstract: Command-line based silent installation for WebSphere Portal updates. File Name, Component Name, Release wps/fix/src/com/ibm/websphere/update/silent/UpdateInstaller.java, wps.base.fix, wps6.fix History 1.4, 1/15/06 01-Nov-2002 Initial Version
 */

public class UpdateInstaller {

	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmVersion = "1.4" ;
	//********************************************************
	//  Program Versioning
	//********************************************************
	public static final String pgmUpdate = "1/15/06" ;

        //********************************************************
       //  PUI Return Code
       //********************************************************
        public static int puiReturnCode = 0;


	//***********************************************************
	// Instance State
	//***********************************************************
	private UpdateInstallerArgs args;

	public UpdateInstaller(UpdateInstallerArgs args) {
		this.args = args;
	}

	//***********************************************************
	// Method Definitions
	//***********************************************************
	public static void main(String[] args) {
		main(args, true); // Will never return!
	}

	public static int main(String[] args, boolean doExit) {
		int result = process(args);
                
                if (UpdateInstaller.puiReturnCode==0 && result!=0) {
                    UpdateInstaller.puiReturnCode = result;
                }

                System.out.println("UpdateInstaller.puiReturnCode is " + UpdateInstaller.puiReturnCode );

		if (doExit)
			//System.exit(result);
                        System.exit(UpdateInstaller.puiReturnCode);

		//return result;
		return UpdateInstaller.puiReturnCode;
	}

	protected static int process(String[] cmdLineArgs) {
		UpdateInstallerArgs args = new UpdateInstallerArgs();
		args.parse(cmdLineArgs);

      if ( args.suppressOutput ) {
         // Only reset Out/Err if suppressOutput is set, this indicate a LogFile or nothing.
         System.setOut( args.logOutput ? args.logFile : new NullPrintStream() );
         System.setErr( args.logOutput ? args.logFile : new NullPrintStream() );
      }

      UpdateReporter.printCopyright();

		if (args.errorArg != null) {
			System.err.println(UpdateReporter.getSilentString(args.errorCode, args.errorArg));
			args.showUsage = true;
		}

		if(args.prereqOverride && args.fixPack) {
			System.err.println(UpdateReporter.getSilentString("WUPD0024E", "-prereqOverride"));
			args.showUsage = true;
		}

		if (args.propsArgError) {
			if(OSUtil.isWindows()) {
				System.out.println(UpdateReporter.getSilentString("update.efix.install.cmdline.usage"));
				System.out.println("");
			}
		 	else {
		 		System.out.println(UpdateReporter.getSilentString("update.efix.install.cmdline.usage"));
	 			System.out.println("");
		 	}
		 	
			return 9;
		}

		if (args.showHelp) {
			System.out.println(UpdateReporter.getSilentString("update.install.cmdline.help"));
			return 0;

		} else if (args.showUsage) {
			if(OSUtil.isWindows()) {
				System.out.println(UpdateReporter.getSilentString("update.efix.install.cmdline.usage"));
				System.out.println("");
			}
		 	else {
		 		System.out.println(UpdateReporter.getSilentString("update.efix.install.cmdline.usage"));
	 			System.out.println("");
		 	}

			if(args.errorArg != null || args.prereqOverride) return 9;
			else return 0;
		}

		//if update type is efix
		if (args.efix) {

			if (args.install) {
				if (!args.installDirInput || 
                        !args.efixDirInput || 
                        !(args.efixesInput || args.efixJarsInput) || 
                        args.uninstall || !args.featureCustomBackupInput ||
                        !args.bWasPassword ||
                        !args.bWasUserId) 
                {
					System.out.println(UpdateReporter.getSilentString("update.efix.install.cmdline.required.args"));
					return 9;
				} else {
					EFixInstaller installer = new EFixInstaller(args);
					return (installer.doInstall() ? 0 : 9);
				}

			} else if (args.uninstall) {
				if (!args.installDirInput || 
                        !args.efixesInput || 
                        args.install || !args.featureCustomBackupInput ||
                        !args.bWasPassword ||
                        !args.bWasUserId) 
                {
					System.out.println(UpdateReporter.getSilentString("update.efix.uninstall.cmdline.required.args"));
					return 9;
				} else {
					EFixInstaller installer = new EFixInstaller(args);
					return (installer.doUninstall() ? 0 : 9);
				}

			} else if (args.uninstallAll) {
				if (!args.installDirInput || 
                        args.install || !args.featureCustomBackupInput ||
                        !args.bWasPassword ||
                        !args.bWasUserId)
                {
					System.out.println(UpdateReporter.getSilentString("update.efix.uninstall.cmdline.required.args"));
					return 9;
				} else {
					EFixInstaller installer = new EFixInstaller(args);
					return (installer.doUninstall() ? 0 : 9);
				}

			} else {
				if (args.installDirInput && !args.fixPack) {
					EFixInstaller installer = new EFixInstaller(args);
					// added by kent for list ifix of certain feature
					if(args.featureInput){
						String[] features = null;
						if(args.feature.indexOf(",") != -1){
							features = args.feature.split(",");
						}else{
							features = args.feature.split(";");
						}
						System.out.println(UpdateReporter.getSilentString("label.installed.efixes"));
						for(String feature : features){
							if (!installer.doListInstalled(feature))
							return 9;						
						}
					}else{
					if (!installer.doListInstalled())
						return 9;
					}

					if (args.efixDirInput) {
						if (!installer.doListAvailable())
							return 9;
						else
							return 0;
					} else {
						return 0;
					}

				} else {
					if (args.efixDirInput)
						System.out.println(UpdateReporter.getSilentString("efix.list.installable.requires.product"));
					else
						System.out.println(UpdateReporter.getSilentString("no.operation"));

					return 9;
				}
			}

		} else if (args.fixPack) {

			if (args.install) {

				if ( !args.wpcpOnly ) {
					// Lotus Connections should always call this portion of the code
					if (!args.installDirInput || 
                            !args.fixPackDirInput || 
                            !args.fixPackInput || 
                            args.uninstall ||
                            !args.bWasPassword ||
                            !args.bWasUserId) 
                    {
						if(OSUtil.isWindows()){
							System.out.println(UpdateReporter.getSilentString("update.fixpack.install.cmdline.required.args.win"));
						}else{
							System.out.println(UpdateReporter.getSilentString("update.fixpack.install.cmdline.required.args.unix"));							
						}
						return 9;
					} else {
						if (args.wpcpUpdate && !args.wpcpDirInput) {
							if(OSUtil.isWindows()){
								System.out.println(UpdateReporter.getSilentString("update.fixpack.install.cmdline.required.args.win"));
							}else{
								System.out.println(UpdateReporter.getSilentString("update.fixpack.install.cmdline.required.args.unix"));							
							}
							return 9;
						}


						PTFInstaller installer = new PTFInstaller(args);
						return (installer.doInstall() ? 0 : 9);
					}
				} else if ( args.wpcpOnly ) {

					if ( !args.fixPackDirInput || !args.fixPackInput || args.uninstall) {
						System.out.println(UpdateReporter.getSilentString("update.fixpack.install.cmdline.required.args.wpcp.only"));
						return 9;
					} else {

						//don't allow -skipWPCP if -wpcpOnly is specified
						if ((args.wpcpUpdate && !args.wpcpDirInput) || !args.wpcpUpdate) {
							System.out.println(UpdateReporter.getSilentString("update.fixpack.install.cmdline.required.args.wpcp.only"));
							return 9;
						}

						PTFInstaller installer = new PTFInstaller(args);
						return (installer.doInstall() ? 0 : 9);
					}

				}

			} else if (args.uninstall) {

				if (!args.wpcpOnly) {
				    // Lotus Connections should always call this portion of the code
					if (!args.installDirInput || 
                            !args.fixPackInput ||
                            args.install ||
                            !args.bWasPassword ||
                            !args.bWasUserId)
                    {
						System.out.println(UpdateReporter.getSilentString("update.fixpack.uninstall.cmdline.required.args"));
						return 9;
					} else {
						PTFInstaller installer = new PTFInstaller(args);
						return (installer.doUninstall() ? 0 : 9);
					}

				} else {

					if ( !args.wpcpDirInput || !args.fixPackInput || args.install ) {
						System.out.println(UpdateReporter.getSilentString("update.fixpack.uninstall.cmdline.required.args.wpcp.only"));
						return 9;
					} else {
						PTFInstaller installer = new PTFInstaller(args);
						return (installer.doUninstall() ? 0 : 9);
					}					
					
				}

			} else {
				if (args.installDirInput && !args.efix) {
					PTFInstaller installer = new PTFInstaller(args);

					if (!installer.doListInstalled())
						return 9;

					if (args.fixPackDirInput) {
						if (!installer.doListAvailable())
							return 9;
						else
							return 0;
					} else {
						return 0;
					}

				} else {
					if (args.fixPackDirInput)
						System.out.println(UpdateReporter.getSilentString("fixpack.list.installable.requires.product"));
					else
						System.out.println(UpdateReporter.getSilentString("no.operation"));

					return 9;
				}
			}

		} else {
			if(OSUtil.isWindows()) {
				System.out.println(UpdateReporter.getSilentString("update.efix.install.cmdline.usage"));
				System.out.println("");
			}
		 	else {
		 		System.out.println(UpdateReporter.getSilentString("update.efix.install.cmdline.usage"));
	 			System.out.println("");
		 	}		 
		}

		return 0;


	}
   private static class NullPrintStream extends PrintStream {
      boolean error = false;

      NullPrintStream() {
         super( new NullOutputStream() );
      }

      public void close() {
      }
      protected void setError() {
         error = true;
      }
      public void flush() {
      }
      public void write(byte buf[], int off, int len) {
      }
      public boolean checkError() {
         return error;
      }
      public void write(int b) {
      }
      public void print(boolean b) {
      }
      public void print(int i) {
      }
      public void print(float f) {
      }
      public void print(char c) {
      }
      public void print(double d) {
      }
      public void print(char s[]) {
      }
      public void print(long l) {
      }
      public void println() {
      }
      public void println(boolean x) {
      }
      public void println(int x) {
      }
      public void println(float x) {
      }
      public void println(Object x) {
      }
      public void println(char x) {
      }
      public void println(char x[]) {
      }
      public void println(double x) {
      }
      public void write(byte b[]) throws IOException {
      }
      protected Object clone() throws CloneNotSupportedException {
         NullPrintStream ps = new NullPrintStream();
         ps.error = this.error;
         return ps;
      }

   }
   private static class NullOutputStream extends OutputStream {
      public void write(int b) throws IOException {
      }
      public void close() throws IOException {
      }
      public void write(byte b[]) throws IOException {
      }
      public void write(byte b[], int off, int len) throws IOException {
      }
      public void flush() throws IOException {
      }
   }
}
