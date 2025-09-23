/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015  		                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.connections.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
*  Executes a command.
*    command: command to run.  Must be prefixed by "cmd /c" or "sh" if the command is a script.
*    descriptionText: displayed while the command runs.
*    errorText: logged if the command fails
*    progressWatcherClass: command-specific implementation to update the progress bar
*  Canceling the command will invoke Process.destroy() 
*  A ProgressWatcher thread updates the command progress
*  Command output is redirected to the install log
*  If command return code != 0, an error is logged (should install halt?)  
*/
public class ExternalCommandAction {

	public static final String PASSWORD_KEY = "<pwd>"; // indicates the enclosed text should not be logged
	public static final String PASSWORD_KEY_END = "</pwd>";

	protected boolean arrayCmds = false;
	protected String command = "";
	protected String[] commands;
	protected String progressWatcherClass = ""; // name of class that extends ProgressWatcher.  Now uses reflection.  Leave "" if no watcher.
	protected String[] progressWatcherArguments = { "" }; // array of strings, arguments for the progress watcher
	protected String returnCode = "";
	protected String stdOut = "";
	protected boolean storeStdOut = true;

	protected int progressUpdateInterval = 4000; // milliseconds
	protected int cancelCheckInterval = 300; // milliseconds
	protected int outputBufferClearInterval = 300; // milliseconds

	protected String commandNameResolved = "";
	protected boolean filesRemote = false;
	protected Process proc = null;
	protected boolean cancelFlag = false;
	protected boolean commandFinished = false;

	protected String logFile = ""; // used to load the log in the viewer
	public boolean isArrayCmds() {
		return arrayCmds;
	}

	public void setArrayCmds(boolean arrayCmds) {
		this.arrayCmds = arrayCmds;
	}

	public String[] getCommands() {
		return commands;
	}

	public void setCommands(String[] commands) {
		this.commands = commands;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	protected String descriptionText = "";

	public String getDescriptionText() {
		return descriptionText;
	}

	public void setDescriptionText(String descriptionText) {
		this.descriptionText = descriptionText;
	}

	protected String detailText = "";

	public String getDetailText() {
		return detailText;
	}

	public void setDetailText(String detailText) {
		this.detailText = detailText;
	}

	protected static String workingDirectory = "";

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public String getProgressWatcherClass() {
		return progressWatcherClass;
	}

	public void setProgressWatcherClass(String progressWatcherClass) {
		this.progressWatcherClass = progressWatcherClass;
	}

	public String[] getProgressWatcherArguments() {
		return progressWatcherArguments;
	}

	public void setProgressWatcherArguments(String[] progressWatcherArguments) {
		this.progressWatcherArguments = progressWatcherArguments;
	}

	public boolean isFilesRemote() {
		return filesRemote;
	}

	public void setFilesRemote(boolean filesRemote) {
		this.filesRemote = filesRemote;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getStdOut() {
		return stdOut;
	}

	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}

	public boolean getStoreStdOut() {
		return storeStdOut;
	}

	public void setStoreStdOut(boolean storeStdOut) {
		this.storeStdOut = storeStdOut;
	}

	/**
	 * Executes the command
	 */
	public void execute() {
		commandFinished = false;
		if (arrayCmds == false) {
			String commandNameResolvedWithPasskey = command;
			String commandNameResolvedWithPass = removePasskey(commandNameResolvedWithPasskey);
			commandNameResolved = hidePassword(commandNameResolvedWithPasskey);

			//        setState(0);

			String workingDirectoryRes = workingDirectory;
			File cwd = null;
			if (!workingDirectoryRes.equals("")) {
				try {
					cwd = new File(workingDirectoryRes);
					if (!cwd.isDirectory()) {

						cwd = null;
					}
				} catch (Exception e) {
//					e.printStackTrace();
					cwd = null;
				}
			}

			try {
				proc = Runtime.getRuntime().exec(commandNameResolvedWithPass, null, cwd);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		} else {
			String[] commandNameResolvedWithPasskey = commands;
			String[] commandNameResolvedWithPass = new String[commands.length];
			String[] resolved = new String[commands.length];
			for(int i=0;i<commands.length;i++) {
			  commandNameResolvedWithPass[i] = removePasskey(commandNameResolvedWithPasskey[i]);
			  resolved[i] = hidePassword(commandNameResolvedWithPasskey[i]);
			}
			
			commandNameResolved = resolved.toString();
			
			//        setState(0);

			String workingDirectoryRes = workingDirectory;
			File cwd = null;
			if (!workingDirectoryRes.equals("")) {
				try {
					cwd = new File(workingDirectoryRes);
					if (!cwd.isDirectory()) {

						cwd = null;
					}
				} catch (Exception e) {
//					e.printStackTrace();
					cwd = null;
				}
			}

			try {
				proc = Runtime.getRuntime().exec(commandNameResolvedWithPass, null, cwd);
			} catch (Exception e) {
				System.out.println(e.getMessage());
//				e.printStackTrace();
			}
		}
		

		Thread t1 = null;
		Thread t2 = null;
		if (proc != null) {
			//            (new CancelWatcher()).start();
			//            (new ProgressWatcherRunner()).start();
			t1 = (new OutputWatcher(proc.getInputStream(), "StdOut"));
			t2 = (new OutputWatcher(proc.getErrorStream(), "StdErr"));
			t1.start();
			t2.start();
			while (!t1.isAlive() || !t2.isAlive()) {
				Thread.yield();
			}
			while (!cancelFlag) {
				try {
					proc.waitFor();
					// completed
					break;
				} catch (InterruptedException ie) {
					try {
						Thread.sleep(cancelCheckInterval);
					} catch (Exception e) {
						// Continue waiting even if can't sleep
					}
				}
			}
		}
		//        checkSuspended(); // if cancel dialog is active wait until cancel dialog closes
		commandFinished = true;

		int exitCode = -1;
		if (!cancelFlag && proc != null) {
			exitCode = proc.exitValue();
		}
		setReturnCode(exitCode + "");
		//        logEvent(this, Log.MSG2, "Return code = "+getReturnCode());
		if (!cancelFlag) {
			//            setState(100);
		}

	}

	/**
	 * Updates progress bar
	 * @param percentComplete Fraction of progress bar completed
	 */
	/* protected static void setState(int percentComplete) {

	     Progress pstate = new GenericProgress();
	     pstate.setStatusDescription(resolveString(descriptionText));
	     pstate.setStatusDetail(resolveString(detailText));
	     pstate.setPercentComplete(percentComplete);
	     ProgressRenderer pr = getProgressRenderer();
	     if (pr != null) {
	         getProgressRenderer().updateProgress(pstate);
	     }
	 }*/

	/**
	 * Removes password tags
	 * For example, converts "This is my <pwd>password</pwd>" to "This is my password"
	 * @param originalText Text that contains <pwd> tag pairs
	 * @return Copy of originalText with {<pwd>, </pwd>} tags removed
	 */
	protected static String removePasskey(String originalText) {
		String copy = new String(originalText);
		//System.out.println(copy);
		while (copy.indexOf(PASSWORD_KEY) > -1) {
			copy = copy.substring(0, copy.indexOf(PASSWORD_KEY)) + copy.substring(copy.indexOf(PASSWORD_KEY) + PASSWORD_KEY
					.length());
			//System.out.println(copy);
		}
		while (copy.indexOf(PASSWORD_KEY_END) > -1) {
			copy = copy.substring(0, copy.indexOf(PASSWORD_KEY_END)) + copy
					.substring(copy.indexOf(PASSWORD_KEY_END) + PASSWORD_KEY_END.length());
			//System.out.println(copy);
		}
		return copy;
	}

	/**
	 * Hides password
	 * For example, converts "This is my <pwd>password</pwd>" to "This is my PASSWORD_REMOVED"
	 * @param originalText Text that contains <pwd> tag pairs
	 * @return Copy of originalText with {<pwd>, </pwd>} tags removed and text between those tags replaced with PASSWORD_REMOVED
	 */
	protected static String hidePassword(String originalText) {
		String copy = new String(originalText);
		//System.out.println(copy);
		while (copy.indexOf(PASSWORD_KEY) > -1) {
			copy = copy.substring(0, copy.indexOf(PASSWORD_KEY)) + "PASSWORD_REMOVED"
					+ copy.substring(copy.indexOf(PASSWORD_KEY_END) + PASSWORD_KEY_END.length());
			//System.out.println(copy);
		}
		return copy;
	}

	/** 
	 * Thread that monitors cancel button
	 * Sets flag cancelFlag to true if cancel is requested
	 */
	protected class CancelWatcher extends Thread {
		public void run() {

			while (!commandFinished) {
				try {
					//                    checkCanceled(); // throws exception when canceled
					try {
						Thread.sleep(cancelCheckInterval);
					} catch (Exception e) {
						// Continue waiting even if can't sleep
					}
				} catch (Exception e) {
					// Cancel performed

					proc.destroy();

					cancelFlag = true;
					//                    setState(0);

					// Just exit if the user cancels

					System.exit(0);

					/* 
					// Jump to error panel
					if (!cancelDestinationBean.equals("")) {
					    logEvent(this, Log.MSG2, "Jumping to bean: "+cancelDestinationBean);
					    Wizard wizard = wizardbeanevent.getWizard();
					    WizardTree wizardtree = wizard.getWizardTree();
					    WizardTreeIterator wizardtreeiterator = wizardbeanevent.getWizard().getIterator();
					    WizardBean wizardbean = wizardtree.findWizardBean(wizardtree.getRoot(), cancelDestinationBean);
					    if(wizardbean != null) {
					        wizard.setCurrentBean(wizardtreeiterator.getPrevious(wizardbean));
					    }
					    else {
					        logEvent(this, Log.ERROR, "Cannot change beans: could not find bean: " + cancelDestinationBean);
					    }
					}

					break;
					*/
				}
			}
		}
	}

	/**
	 * Thread that reads process output so the process does not block
	 * Process output is sent to the ISMP log
	 */
	protected class OutputWatcher extends Thread {

		InputStream stream = null;
		String tag;

		public OutputWatcher(InputStream stream, String tag) {
			this.stream = stream;
			this.tag = tag;
		}

		public void run() {
			//int counter = 0;
			//boolean overflow = false;
			//String[] stringLoop = new String[100];

			BufferedReader brStream = new BufferedReader(new InputStreamReader(stream));
			while (!commandFinished) {
				try {
					try {
						Thread.sleep(outputBufferClearInterval);
					} catch (Exception e) {
						// Continue even if can't sleep
					}

					String data = brStream.readLine();
					int ctr = 0;
					while (data != null) {
						/*
						stringLoop[counter] = data;
						counter++;
						if (counter == 100) {
						    overflow = true;
						    counter = 0;
						}
						*/

						// condition storage of stdOut - can be very long with
						// external calls such as config
						if (tag.equals("StdOut") && getStoreStdOut()) {
							if (ctr > 0) {
								stdOut = stdOut + "$J(line.separator)" + data;
							} else
								stdOut = data;
						}

						data = brStream.readLine();
						ctr++;
					}

				} catch (Exception e) {
					if (!System.getProperty("user.language").equals("pl")) { // fixes a problem reading on Linux pl

					}
				}
			}

			/*
			if (!overflow) {
			    for (int i = 0; i < counter; i++) {
			        logEvent(this, Log.MSG2, tag+": "+stringLoop[i]);
			    }
			}
			else {
			    for (int i = counter; i < 100; i++) {
			        logEvent(this, Log.MSG2, tag+": "+stringLoop[i]);
			    }
			    for (int i = 0; i < counter; i++) {
			        logEvent(this, Log.MSG2, tag+": "+stringLoop[i]);
			    }
			}
			*/

			try {
				while (brStream.ready()) {
					char buff[] = new char[1000];
					int read = brStream.read(buff, 0, 1000);
					String text = new String(buff, 0, read);

				}
			} catch (Exception e) {
				if (!System.getProperty("user.language").equals("pl")) { // fixes a problem reading on Linux pl

				}
			}
		}
	}

	public static void main(String[] args) {

		String profilePath = "C:\\Program Files\\IBM\\WebSphere\\AppServer1\\profiles\\Dmgr01";
		String command = "C:\\Program Files\\IBM\\WebSphere\\AppServer1\\profiles\\Dmgr01/bin/retrieveSigners.bat CellDefaultTrustStore ClientDefaultTrustStore -autoAcceptBootstrapSigner -conntype SOAP -username wasadmin -password password";
		ExternalCommandAction eca = new ExternalCommandAction();
		eca.setWorkingDirectory(profilePath);
		eca.setCommand(command);
		eca.execute();
		System.out.println(eca.getReturnCode());
	}

	
}
