/* Portions Copyright IBM Corp. 2001, 2015   All Rights Reserved     */

/*
 * 5724-E76 and 5724-E77                                            *
*/
/* copyright statement automatically inserted on Mon Jul 07 13:45:34 EDT 2003 */

package com.ibm.websphere.update.silent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ibm.lconn.update.util.LCUtil;

/**
 * Class: UpdateInstallerArgs.java Abstract: Argument parsing engine. CMVC
 * Location:
 * wps/fix/src/com/ibm/websphere/update/silent/UpdateInstallerArgs.java,
 * wps.base.fix, wps6.fix Version: 1.5 Last Modified: 1/29/04 Revision / History
 * History 1.5, 1/29/04 01-Nov-2002 Initial Version
 */
public class UpdateInstallerArgs {

	// ********************************************************
	// Program Versioning
	// ********************************************************
	public static final String pgmVersion = "1.5";
	// ********************************************************
	// Program Versioning
	// ********************************************************
	public static final String pgmUpdate = "1/29/04";

	public boolean efix = false;
	public boolean fixPack = false;

	public boolean install = false;
	public boolean uninstall = false;
	public boolean uninstallAll = false;

	public boolean useProps = false;
	public boolean propsArgError = false;
	public String respFile = null;

	public boolean installDirInput = false;
	public String installDir = null;

	public boolean efixDirInput = false;
	public String efixDir = null;

	public boolean fixPackDirInput = false;
	public String fixPackDir = null;

	public boolean efixesInput = false;
	private Vector efixList = new Vector();
	public boolean featureCustomBackupInput = false;
	public String featureCustomBacked = null;

	private boolean pwdInput = false;
	private HashMap pwdListMap = new HashMap();

	public boolean efixJarsInput = false;
	public Vector efixJarList = new Vector();

	public boolean fixPackInput = false;
	private Vector fixPackList = new Vector();

	public boolean fixPackExtendedComps = false;
	private Vector extendedComponentList = new Vector();

	public boolean fixPackOptionalComps = false;
	private Vector optionalComponentList = new Vector();

	public boolean fixPackFeatureInput = false;
	private Vector fixPackFeatureList = new Vector();

	public boolean prereqOverride = false;
	public boolean displayEfixDetails = false;
	public boolean displayFixPackDetails = false;

	public String wpcpDir = null;
	public boolean wpcpDirInput = false;
	public boolean wpcpUpdate = true;
	public boolean wpcpOnly = false;

	public boolean pdmUpdate = true;
	public boolean pdmOnly = false;

	public boolean odcUpdate = true;
	public boolean odcOnly = false;

	public boolean wmmUpdate = true;
	public boolean wmmOnly = false;

	public boolean wtpUpdate = true;
	public boolean wtpOnly = false;

	public boolean cfgUpdate = true;
	public boolean cfgOnly = false;

	public String configProps = null;

	public boolean showHelp = false;
	public boolean showUsage = false;

	public boolean printStack = false;

	public String errorArg = null;
	public String errorCode = null;
	public String errorCodeExplained = null;
	public boolean isComplete = false;
	public static boolean skipBackup = false;
	public static boolean skipHistory = false;
	public static boolean skipLog = false;

	public boolean suppressOutput = false;
	public boolean logOutput = true;
	public PrintStream logFile = System.out;

	public static int isThisUninstall = 0;
	public static int isThisUninstallforFixpack = 0;
	public static String thisIsFixpackID = null;

	// this is for FIXPACK only.
	// we ship one fixpack that contains all 5 features
	// This variable allows the customers to specify which feature(s) they want
	// to update, if nothing selected, then all will be updated
	// only done for command parameter, will need to add to properties handling
	// also later
	public boolean bSelectFeature = false;
	public Vector selectFeatureList = new Vector();

	// WAS password
	public boolean bWasPassword = false;
	public String wasPassword = null;

	// WAS userid
	public boolean bWasUserId = false;
	public String wasUserId = null;

	// added by kent for list ifix of certain feature
	public boolean featureInput = false;
	public String  feature = null;
    
	public void setFeature(String featureName){
		featureInput = true;
		feature = featureName;
	}
	protected void setEFixUpdate() {
		efix = true;
		output(UpdateReporter.getSilentString("display.update.type.efix"));
	}

	protected void setFixPackUpdate() {
		fixPack = true;
		output(UpdateReporter.getSilentString("display.update.type.fixpack"));
		isThisUninstallforFixpack = 1;
	}

	protected void setPropertyFile(String fileName) {
		useProps = true;
		respFile = fileName;

		output(UpdateReporter.getSilentString("property.file.specified",
				respFile));
	}

	/**
	 * @param installDir
	 *            the installDir to set
	 * @uml.property name="installDir"
	 */
	protected void setInstallDir(String dirName) {
		dirName = com.ibm.lconn.common.LCUtil.getFilepath(dirName);
		installDirInput = true;
		com.ibm.lconn.common.LCUtil.setLCHome(dirName);
		installDir = dirName;
		// If we have not already set the WPCP install loc, setup default.
		if (!wpcpDirInput) {
			wpcpDir = dirName;
			wpcpDirInput = true;
		}
		// output(UpdateReporter.getSilentString("install.dir.specified",
		// installDir));
	}
	/**
	 * @param backed
	 * 			yes, the customization in feature has been backed up
	 * 			no, no customization needs to be backed up
	 */
	protected void setCustomBacked(String backed) {
		featureCustomBackupInput = true;
		featureCustomBacked = backed;
	}

	/**
	 * @param wasPassword
	 *            the WAS password to set
	 * @uml.property name="wasPassword"
	 */
	protected void setWasPassword(String password) {
		bWasPassword = true;
		wasPassword = password;
	}

	/**
	 * @param wasUserId
	 *            the WAS admin id to set
	 * @uml.property name="wasUserId"
	 */
	protected void setWasUserId(String userid) {
		bWasUserId = true;
		wasUserId = userid;
	}

	protected void setEFixDir(String dirName) {
		dirName = com.ibm.lconn.common.LCUtil.getFilepath(dirName);
		efixDirInput = true;
		efixDir = dirName;
		output(UpdateReporter.getSilentString("efix.dir.specified", efixDir));
	}

	/**
	 * @param fixPackDir
	 *            the fixPackDir to set
	 * @uml.property name="fixPackDir"
	 */
	protected void setFixPackDir(String dirName) {
		dirName = com.ibm.lconn.common.LCUtil.getFilepath(dirName);
		fixPackDirInput = true;
		fixPackDir = dirName;

		output(UpdateReporter.getSilentString("fixpack.dir.specified",
				fixPackDir));
	}

	/**
	 * @return the efixList
	 * @uml.property name="efixList"
	 */
	protected Vector getEfixList() {
		return efixList;
	}

	protected boolean isPwdListAvailable() {
		return pwdInput;
	}

	/**
	 * @return the pwdList
	 * @uml.property name="pwdList"
	 */
	protected HashMap getPwdListMap() {
		return pwdListMap;
	}

	/**
	 * @return the fixPackList
	 * @uml.property name="fixPackList"
	 */
	protected Vector getFixPackList() {
		return fixPackList;
	}

	protected Vector getExtendedComponents() {
		return fixPackFeatureList;
	}

	protected Vector getOptionalComponents() {
		return optionalComponentList;
	}

	protected boolean maybeAddEFix(String tag) {
		if (tag.indexOf("-") == 0) // Cannot start with a dash.
			return false;
		efixesInput = true;
		efixList.add(tag);

		output(UpdateReporter.getSilentString("efix.specified", tag));

		return true;
	}

	protected boolean maybeAddPwd(String tag) {
		pwdInput = true;
		String[] st = tag.split(":");

		pwdListMap.put(st[0], st[1]);

		// output(UpdateReporter.getSilentString("efix.specified", tag));

		return true;
	}

	protected boolean maybeAddEFixFromJar(String tag) {
		efixesInput = true;
		efixList.add(tag);

		output(UpdateReporter.getSilentString("efix.specified", tag));

		return true;
	}

	protected boolean maybeAddEFixJar(String tag) {
		if (tag.indexOf("-") == 0) // Cannot start with a dash.
			return false;

		efixJarsInput = true;
		efixJarList.add(tag);

		output(UpdateReporter.getSilentString("efix.jar.specified", tag)); // NEW

		return true;
	}

	protected boolean maybeAddFeature(String tag) {
		if (tag.indexOf("-") == 0) // Cannot start with a dash.
			return false;

		fixPackFeatureInput = true;
		fixPackFeatureList.add(tag);

		output(UpdateReporter.getSilentString("fixpack.feature.specified", tag));

		return true;
	}

	protected boolean maybeAddOptionalComponent(String tag) {
		if (tag.indexOf("-") == 0) // Cannot start with a dash.
			return false;

		fixPackOptionalComps = true;
		optionalComponentList.add(tag);

		output(UpdateReporter.getSilentString(
				"fixpack.optional.component.specified", tag));

		return true;
	}

	protected boolean setFixPackID(String tag) {
		fixPackInput = true;
		fixPackList.add(tag);

		output(UpdateReporter.getSilentString("fixpack.specified", tag));

		thisIsFixpackID = tag;

		return true;
	}

	protected boolean setSelectFeature(String tag) {
		if (tag.indexOf("-") == 0) // Cannot start with a dash.
			return false;

		bSelectFeature = true;
		selectFeatureList.add(tag);

		output(UpdateReporter.getSilentString("feature.specified", tag));

		return true;
	}

	protected void setEFixDisplayDetails() {
		displayEfixDetails = true;
		// output("WUPD0036I: " +
		// UpdateReporter.getSilentString("display.efix.details"));
	}

	protected void setFixPackDisplayDetails() {
		displayFixPackDetails = true;
		// output(UpdateReporter.getSilentString("display.fixpack.details"));
	}

	protected void setWPCPDir(String dirName) {
		dirName = com.ibm.lconn.common.LCUtil.getFilepath(dirName);
		wpcpDir = dirName;
		wpcpDirInput = true;
		if (!installDirInput) {
			installDirInput = true;
			installDir = dirName;
		}
		// output(UpdateReporter.getSilentString("install.dir.specified",
		// installDir));
	}

	protected String getWPCPDir() {
		return wpcpDir;
	}

	protected void setWPCPOnly() {
		wpcpOnly = true;
		output(UpdateReporter
				.getSilentString("display.wpcp.update.only.specified"));
	}

	protected void skipWPCP() {
		wpcpUpdate = false;
		output(UpdateReporter
				.getSilentString("display.wpcp.update.skip.specified"));
	}

	protected void setPDMOnly() {
		pdmOnly = true;
		output(UpdateReporter
				.getSilentString("display.pdm.update.only.specified"));
	}

	protected void skipPDM() {
		pdmUpdate = false;
		output(UpdateReporter
				.getSilentString("display.pdm.update.skip.specified"));
	}

	protected void setWMMOnly() {
		wmmOnly = true;
		output(UpdateReporter
				.getSilentString("display.wmm.update.only.specified"));
	}

	protected void skipWMM() {
		wmmUpdate = false;
		output(UpdateReporter
				.getSilentString("display.wmm.update.skip.specified"));
	}

	protected void setODCOnly() {
		odcOnly = true;
		output(UpdateReporter
				.getSilentString("display.odc.update.only.specified"));
	}

	protected void skipODC() {
		odcUpdate = false;
		output(UpdateReporter
				.getSilentString("display.odc.update.skip.specified"));
	}

	protected void setWTPOnly() {
		wtpOnly = true;
		output(UpdateReporter
				.getSilentString("display.wtp.update.only.specified"));
	}

	protected void skipWTP() {
		wtpUpdate = false;
		output(UpdateReporter
				.getSilentString("display.wtp.update.skip.specified"));
	}

	protected void setCfgOnly() {
		cfgOnly = true;
		output(UpdateReporter
				.getSilentString("display.cfg.update.only.specified"));
	}

	protected void skipCfg() {
		cfgUpdate = false;
		output(UpdateReporter
				.getSilentString("display.cfg.update.skip.specified"));
	}

	protected void skipBackup() {
		skipBackup = true;
		// TODO
		// output(UpdateReporter.getSilentString("display.backup.none.specified"));
	}

	protected void skipHistory() {
		skipHistory = true;
		// TODO
		// output(UpdateReporter.getSilentString("display.history.none.specified"));
	}

	protected void skipLog() {
		skipLog = true;
		// TODO
		// output(UpdateReporter.getSilentString("display.log.none.specified"));
	}

	protected void setToInstall() {
		install = true;
		output(UpdateReporter.getSilentString("will.install"));
	}

	protected void setToUninstall() {
		uninstall = true;
		output(UpdateReporter.getSilentString("will.uninstall"));
		isThisUninstall = 1;
	}

	protected void setToUninstallAll() {
		uninstallAll = true;

		output(UpdateReporter.getSilentString("will.uninstall.all"));
	}

	protected void doOverride() {
		prereqOverride = true;
		output(UpdateReporter.getSilentString("will.override.prereqs"));
	}

	public void setConfigPropertiesName(String s) {
		configProps = s;
	}

	public String getConfigPropertiesName() {
		return configProps;
	}

	public static boolean doLogging() {
		return !skipLog;
	}

	public static boolean doHistory() {
		return !skipHistory;
	}

	public static boolean doBackup() {
		return !skipBackup;
	}

	public void parse(String[] args) {
		int argNo = 0;

		while (!isComplete && (argNo < args.length)) {
			String nextArg = args[argNo++];

			if (nextArg.endsWith(".properties")) {
				if ((argNo == 1) && (argNo == args.length)) {
					try {
						if (parsePropertyFile(nextArg)) {
							setPropertyFile(nextArg);
							isComplete = true;
						} else {
							isComplete = true;
							errorArg = nextArg;
							errorCode = "incorrect.property.file";
						}
					} catch (IOException e) {
						isComplete = true;
						errorArg = nextArg;
						errorCode = "error.reading.property.file";
					}
				} else {
					isComplete = true;
					propsArgError = true;
				}

			} else if (nextArg.equalsIgnoreCase("-fix")
					|| nextArg.equalsIgnoreCase("-efix")) {
				setEFixUpdate();

			} else if (LCUtil.fixpackEnabled()
					&& nextArg.equalsIgnoreCase("-fixpack")) {
				setFixPackUpdate();

			} else if (nextArg.equalsIgnoreCase("-wasPassword")) {
				if (argNo < args.length) {
					setWasPassword(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "was.password.missing";
				}

			} else if (nextArg.equalsIgnoreCase("-wasUserId")) {
				if (argNo < args.length) {
					setWasUserId(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "was.userid.missing";
				}

			} else if (nextArg.equalsIgnoreCase("-installDir")) {
				if (argNo < args.length) {
					setInstallDir(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "WUPD0021E";
				}

			} else if (nextArg.equalsIgnoreCase("-fixDir")
					|| nextArg.equalsIgnoreCase("-efixDir")) {
				if (argNo < args.length) {
					setEFixDir(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "WUPD0022E";
				}

			} else if (nextArg.equalsIgnoreCase("-featureCustomizationBackedUp")) {
				if (argNo <args.length){
					setCustomBacked(args[argNo++]);
				}else{
					isComplete = true;
					errorArg = nextArg;
					errorCode = "WUPD0040E";
				}
			
			} else if (LCUtil.fixpackEnabled()
					&& nextArg.equalsIgnoreCase("-fixPackDir")) {
				if (argNo < args.length) {
					setFixPackDir(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "WUPD0031E";
				}

			} else if (nextArg.equalsIgnoreCase("-fixes")
					|| nextArg.equalsIgnoreCase("-efixes")) {
				while ((argNo < args.length) && maybeAddEFix((args[argNo])))
					argNo++;

				if (!efixesInput) {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "WUPD0023E";
				}

			} else if (nextArg.equalsIgnoreCase("-fixJars")
					|| nextArg.equalsIgnoreCase("-efixJars")) {
				while ((argNo < args.length) && maybeAddEFixJar((args[argNo])))
					argNo++;

				if (!efixJarsInput) {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "WUPD0030E";
				}

			} else if (LCUtil.fixpackEnabled()
					&& nextArg.equalsIgnoreCase("-fixpackID")) {
				if (argNo < args.length) {
					setFixPackID(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "WUPD0032E";
				}
				// Saiyu } else if (nextArg.equalsIgnoreCase("-updateFeature")
				// && fixPack && install)
			} else if (LCUtil.fixpackEnabled()
					&& nextArg.equalsIgnoreCase("-updateFeature") && fixPack) // Saiyu
			{ // set this to update specific feature(s)
				// only care about this when update is fixpack and it is for
				// install only
				while ((argNo < args.length) && setSelectFeature((args[argNo])))
					argNo++;

				if (!bSelectFeature) {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "feature.needed";
				}

			} else if (nextArg.equalsIgnoreCase("-configProperties")) {

				if (argNo < args.length) {
					setConfigPropertiesName(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "error.fixpack.configprops.specifier";
				}

			}
			/*
			 * else if (nextArg.equalsIgnoreCase("-wpcpInstallDir")) { if (argNo <
			 * args.length) { setWPCPDir(args[argNo++]); } else { isComplete =
			 * true; errorArg = nextArg; errorCode =
			 * "error.fixpack.wpcp.feature.specifier"; }
			 *  } else if (nextArg.equalsIgnoreCase("-wpcpOnly")) {
			 * setWPCPOnly(); } else if (nextArg.equalsIgnoreCase("-skipWPCP")) {
			 * skipWPCP(); }
			 */
			else if (nextArg.equalsIgnoreCase("-wpspasswords")) { // user
																	// provided
																	// pwds for
																	// WPS, DB,
																	// etc
				while ((argNo < args.length) && maybeAddPwd((args[argNo])))
					argNo++;
				/*
				 * } else if (nextArg.equalsIgnoreCase("-pdmOnly")) {
				 * setPDMOnly(); } else if
				 * (nextArg.equalsIgnoreCase("-skipPDM")) { skipPDM(); } else if
				 * (nextArg.equalsIgnoreCase("-wmmOnly")) { setWMMOnly(); } else
				 * if (nextArg.equalsIgnoreCase("-skipWMM")) { skipWMM(); } else
				 * if (nextArg.equalsIgnoreCase("-wtpOnly")) { setWTPOnly(); }
				 * else if (nextArg.equalsIgnoreCase("-skipWTP")) { skipWTP(); }
				 * else if (nextArg.equalsIgnoreCase("-odcOnly")) {
				 * setODCOnly(); } else if
				 * (nextArg.equalsIgnoreCase("-skipODC")) { skipODC(); } else if
				 * (nextArg.equalsIgnoreCase("-cfgOnly")) { setCfgOnly(); } else
				 * if (nextArg.equalsIgnoreCase("-skipCfg")) { skipCfg();
				 */
			} else if (nextArg.equalsIgnoreCase("-noBackup")) {
				skipBackup();
			} else if (nextArg.equalsIgnoreCase("-noHistory")) {
				skipHistory();
			} else if (nextArg.equalsIgnoreCase("-noLog")) {
				skipLog();

			} else if (nextArg.equalsIgnoreCase("-noOutput")) {
				suppressOutput = true;
				logOutput = false;
			} else if (nextArg.equalsIgnoreCase("-logFile")) {
				suppressOutput = true;
				try {
					if (argNo < args.length) {
						logFile = new PrintStream(new FileOutputStream(
								args[argNo++]));
						logOutput = true;
					}
				} catch (Exception e) {
					// What to do, we can log thsi error........
				}
			} else if (nextArg.equalsIgnoreCase("-includeOptional")) {
				while ((argNo < args.length)
						&& maybeAddOptionalComponent((args[argNo])))
					argNo++;

				if (!fixPackOptionalComps) {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "error.fixpack.optional.comps.specifier";
				}

			} else if (nextArg.equalsIgnoreCase("-fixDetails")
					|| nextArg.equalsIgnoreCase("-efixDetails")) {
				setEFixDisplayDetails();

			} else if (LCUtil.fixpackEnabled()
					&& nextArg.equalsIgnoreCase("-fixPackDetails")) {
				setFixPackDisplayDetails();

			} else if (nextArg.equalsIgnoreCase("-install")) {
				setToInstall();

			} else if (nextArg.equalsIgnoreCase("-uninstall")) {
				setToUninstall();

			} else if (nextArg.equalsIgnoreCase("-uninstallAll")) {
				setToUninstallAll();

			} else if (nextArg.equalsIgnoreCase("-prereqOverride")) {
				doOverride();

			} else if (nextArg.equalsIgnoreCase("-help")
					|| nextArg.equalsIgnoreCase("-?")
					|| nextArg.equalsIgnoreCase("/?")
					|| nextArg.equalsIgnoreCase("/help")) {
				isComplete = true;
				showHelp = true;

			} else if (nextArg.equalsIgnoreCase("-usage")) {
				isComplete = true;
				showUsage = true;

			} else if (nextArg.equalsIgnoreCase("-printStack")) {
				printStack = true;

			} else if (nextArg.equalsIgnoreCase("-applications")) {
				if (argNo < args.length) {// added by kent for list ifix of certain feature
					setFeature(args[argNo++]);
				} else {
					isComplete = true;
					errorArg = nextArg;
					errorCode = "application name is needed";
				}
			}	
			else {
				String returnErrorCode = LCUtil.extraInputValidation(nextArg,
						args, argNo, this);
				if (returnErrorCode != null) {
					isComplete = true;
					errorArg = nextArg;
					errorCode = returnErrorCode;
				}else{
					argNo ++;
				}
			}
		}

		if (installDirInput) {
			output(UpdateReporter.getSilentString("install.dir.specified",
					installDir));
		}
	}

	public boolean parsePropertyFile(String propertyFile) throws IOException {
		Properties propsFile = new Properties();
		boolean validated = false;

		InputStream fs = new java.io.FileInputStream(propertyFile);
		propsFile.load(fs);
		fs.close();

		// re-initialize everything in case it was set in the cmdLine previously
		efix = false;
		fixPack = false;
		install = false;
		uninstall = false;
		uninstallAll = false;
		installDirInput = false;
		installDir = null;
		efixDirInput = false;
		fixPackDirInput = false;
		efixDir = null;
		fixPackDir = null;
		wpcpDir = null;
		wpcpUpdate = true;
		wpcpOnly = false;
		pdmUpdate = true;
		pdmOnly = false;
		wmmUpdate = true;
		wmmOnly = false;
		wtpUpdate = true;
		wtpOnly = false;
		odcUpdate = true;
		odcOnly = false;
		cfgUpdate = true;
		cfgOnly = false;
		efixList = null;
		efixList = new Vector();
		pwdListMap = null;
		pwdListMap = new HashMap();
		efixJarList = null;
		efixJarList = new Vector();
		fixPackList = null;
		fixPackList = new Vector();
		efixesInput = false;
		efixJarsInput = false;
		fixPackInput = false;
		prereqOverride = false;
		displayEfixDetails = false;
		displayFixPackDetails = false;
		bSelectFeature = false;
		selectFeatureList = new Vector();
		bWasPassword = false;
		wasPassword = null;
		bWasUserId = false;
		wasUserId = null;
		featureInput = false;
		feature = null;
		featureCustomBackupInput = false;
		featureCustomBacked = null;

		String fixPropValue = propsFile.getProperty("fix");
		if (fixPropValue == null)
			fixPropValue = propsFile.getProperty("efix");

		if ((fixPropValue != null) && fixPropValue.equals("true")) {
			efix = true;
			output(UpdateReporter.getSilentString("display.update.type.efix"));
		}

		if ((propsFile.getProperty("fixpack") != null)
				&& propsFile.getProperty("fixpack").equals("true")) {
			fixPack = true;
			output(UpdateReporter
					.getSilentString("display.update.type.fixpack"));
		}

		if ((propsFile.getProperty("install") != null)
				&& propsFile.getProperty("install").equals("true")) {
			install = true;
			output(UpdateReporter.getSilentString("will.install"));
		}

		if ((propsFile.getProperty("uninstall") != null)
				&& propsFile.getProperty("uninstall").equals("true")) {
			uninstall = true;
			output(UpdateReporter.getSilentString("will.uninstall"));
		}

		String propInstallDir = propsFile.getProperty("installDir");

		if (propInstallDir != null) {
			setInstallDir(propInstallDir);
			// installDirInput = true;
			// installDir = propInstallDir;
			// output(UpdateReporter.getSilentString("install.dir.specified",
			// installDir));
		}
		// added by kent for list ifix of certain feature
		String propFeature = propsFile.getProperty("feature");
		if (propFeature != null) {
			setFeature(propFeature);
		}

		String propWasPassword = propsFile.getProperty("wasPassword");

		if (propWasPassword != null) {
			setWasPassword(propWasPassword);
		}

		String propWasUserId = propsFile.getProperty("wasUserId");

		if (propWasUserId != null) {
			setWasUserId(propWasUserId);
		}

		String propEFixDir = propsFile.getProperty("fixDir");
		if (propEFixDir == null)
			propEFixDir = propsFile.getProperty("efixDir");

		if (propEFixDir != null) {
			efixDirInput = true;
			efixDir = propEFixDir;
			output(UpdateReporter
					.getSilentString("efix.dir.specified", efixDir));
		}

		String propFixPackDir = propsFile.getProperty("fixpackDir");

		if (propFixPackDir != null) {
			fixPackDirInput = true;
			fixPackDir = propFixPackDir;
			output(UpdateReporter.getSilentString("fixpack.dir.specified",
					fixPackDir));
		}

		String efixListProps = propsFile.getProperty("fixes");
		if (efixListProps == null)
			efixListProps = propsFile.getProperty("efixes");

		if (efixListProps != null) {
			StringTokenizer st = new StringTokenizer(efixListProps, ",");
			while (st.hasMoreTokens()) {
				String efix = st.nextToken();

				efixesInput = true;
				efixList.add(efix);
				output(UpdateReporter.getSilentString("efix.specified", efix));
			}
		}
		String featureCustomBackup = propsFile.getProperty("featureCustomizationBackedUp");
		
		if (featureCustomBackup == null){
			output(UpdateReporter.getSilentString("efix.featureCustomizationBackedUp", featureCustomBackup));
		}
		
		if (featureCustomBackup != null){
			featureCustomBackupInput = true;
			featureCustomBacked = featureCustomBackup;
			output(UpdateReporter.getSilentString("efix.featureCustomizationBackedUp",
					featureCustomBackup));
		}
		String updateFeatureListProps = propsFile.getProperty("updateFeature");
		if (updateFeatureListProps != null) {
			StringTokenizer st = new StringTokenizer(updateFeatureListProps,
					",");
			while (st.hasMoreTokens()) {
				String uf = st.nextToken();

				bSelectFeature = true;
				selectFeatureList.add(uf);
				output(UpdateReporter.getSilentString("feature.specified", uf));
			}
		}
		// user provided pwds for WPS, DB, etc
		String pwdListProps = propsFile.getProperty("wpspasswords");

		if (pwdListProps != null) {
			StringTokenizer st = new StringTokenizer(pwdListProps, ",");
			while (st.hasMoreTokens()) {
				String pwd = st.nextToken();

				maybeAddPwd(pwd);
				// output(UpdateReporter.getSilentString("efix.specified",
				// efix));
			}
		}
		String optionalCompsProp = propsFile.getProperty("includeOptional");

		if (optionalCompsProp != null) {
			StringTokenizer st = new StringTokenizer(optionalCompsProp, ",");
			while (st.hasMoreTokens()) {
				String optional = st.nextToken();

				fixPackOptionalComps = true;
				optionalComponentList.add(optional);

				output(UpdateReporter.getSilentString(
						"fixpack.optional.component.specified", optional));
			}
		}

		String efixJarsListProps = propsFile.getProperty("fixJars");
		if (efixJarsListProps == null)
			efixJarsListProps = propsFile.getProperty("efixJars");

		if (efixJarsListProps != null) {
			StringTokenizer st2 = new StringTokenizer(efixJarsListProps, ",");
			while (st2.hasMoreTokens()) {
				String efixJar = st2.nextToken();

				efixJarsInput = true;
				efixJarList.add(efixJar);
				output(UpdateReporter.getSilentString("efix.jar.specified",
						efixJar));
			}
		}

		String fixPackListProp = propsFile.getProperty("fixpackID");

		if (fixPackListProp != null) {
			fixPackInput = true;
			fixPackList.add(fixPackListProp);
			output(UpdateReporter.getSilentString("fixpack.specified",
					fixPackListProp));
		}

		String configPropsFile = propsFile.getProperty("configProperties");
		if (configPropsFile != null) {
			setConfigPropertiesName(configPropsFile);
		}
		/*
		 * String wpcpInputDir = propsFile.getProperty("wpcpInstallDir");
		 * 
		 * if (wpcpInputDir != null) { setWPCPDir( wpcpInputDir );
		 * //wpcpDirInput = true; //wpcpDir = wpcpInputDir; }
		 * 
		 * if ((propsFile.getProperty("wpcpOnly") != null) &&
		 * propsFile.getProperty("wpcpOnly").equals("true")) { setWPCPOnly();
		 * //wpcpOnly = true;
		 * //output(UpdateReporter.getSilentString("display.wpcp.update.only.specified")); }
		 * if ((propsFile.getProperty("skipWPCP") != null) &&
		 * propsFile.getProperty("skipWPCP").equals("true")) { wpcpUpdate =
		 * false;
		 * output(UpdateReporter.getSilentString("display.wpcp.update.skip.specified")); }
		 * 
		 * if ((propsFile.getProperty("pdmOnly") != null) &&
		 * propsFile.getProperty("pdmOnly").equals("true")) { pdmOnly = true;
		 * output(UpdateReporter.getSilentString("display.pdm.update.only.specified")); }
		 * 
		 * if ((propsFile.getProperty("skipPDM") != null) &&
		 * propsFile.getProperty("skipPDM").equals("true")) { pdmUpdate = false;
		 * output(UpdateReporter.getSilentString("display.pdm.update.skip.specified")); }
		 * 
		 * if ((propsFile.getProperty("wmmOnly") != null) &&
		 * propsFile.getProperty("wmmOnly").equals("true")) { wmmOnly = true;
		 * output(UpdateReporter.getSilentString("display.wmm.update.only.specified")); }
		 * 
		 * if ((propsFile.getProperty("skipWMM") != null) &&
		 * propsFile.getProperty("skipWMM").equals("true")) { wmmUpdate = false;
		 * output(UpdateReporter.getSilentString("display.wmm.update.skip.specified")); }
		 * 
		 * if ((propsFile.getProperty("wtpOnly") != null) &&
		 * propsFile.getProperty("wtpOnly").equals("true")) { wtpOnly = true;
		 * output(UpdateReporter.getSilentString("display.wtp.update.only.specified")); }
		 * 
		 * if ((propsFile.getProperty("skipWTP") != null) &&
		 * propsFile.getProperty("skipWTP").equals("true")) { wtpUpdate = false;
		 * output(UpdateReporter.getSilentString("display.wtp.update.skip.specified")); }
		 * 
		 * if ((propsFile.getProperty("odcOnly") != null) &&
		 * propsFile.getProperty("odcOnly").equals("true")) { odcOnly = true;
		 * output(UpdateReporter.getSilentString("display.odc.update.only.specified")); }
		 * 
		 * if ((propsFile.getProperty("skipODC") != null) &&
		 * propsFile.getProperty("skipODC").equals("true")) { odcUpdate = false;
		 * output(UpdateReporter.getSilentString("display.odc.update.skip.specified")); }
		 * 
		 * if ((propsFile.getProperty("cfgOnly") != null) &&
		 * propsFile.getProperty("cfgOnly").equals("true")) { cfgOnly = true;
		 * output(UpdateReporter.getSilentString("display.cfg.update.only.specified")); }
		 * 
		 * if ((propsFile.getProperty("skipCfg") != null) &&
		 * propsFile.getProperty("skipCfg").equals("true")) { cfgUpdate = false;
		 * output(UpdateReporter.getSilentString("display.cfg.update.skip.specified")); }
		 */
		if ((propsFile.getProperty("noBackup") != null)
				&& propsFile.getProperty("noBackup").equals("true")) {
			skipBackup = true;
			output(UpdateReporter
					.getSilentString("display.backup.none.specified"));
		}

		if ((propsFile.getProperty("noHistory") != null)
				&& propsFile.getProperty("noHistory").equals("true")) {
			skipHistory = true;
			output(UpdateReporter
					.getSilentString("display.history.none.specified"));
		}

		if ((propsFile.getProperty("noLog") != null)
				&& propsFile.getProperty("noLog").equals("true")) {
			skipLog = true;
			output(UpdateReporter.getSilentString("display.log.none.specified"));
		}

		if ((propsFile.getProperty("noOutput") != null)
				&& propsFile.getProperty("noOutput").equals("true")) {
			suppressOutput = true;
			logOutput = false;
		}

		if (propsFile.getProperty("logFile") != null) {
			suppressOutput = true;
			try {
				logFile = new PrintStream(new FileOutputStream(propsFile
						.getProperty("logFile")));
				logOutput = true;
			} catch (Exception e) {
				// What to do, we can log thsi error........
			}
		}

		if ((propsFile.getProperty("prereqOverride") != null)
				&& propsFile.getProperty("prereqOverride").equals("true")) {
			prereqOverride = true;
			output(UpdateReporter.getSilentString("will.override.prereqs"));
		}

		if ((propsFile.getProperty("fixDetails") != null)
				&& propsFile.getProperty("fixDetails").equals("true"))
			displayEfixDetails = true;

		if ((propsFile.getProperty("fixpackDetails") != null)
				&& propsFile.getProperty("fixpackDetails").equals("true"))
			displayFixPackDetails = true;

		if (efix && featureCustomBackupInput) {
			if (install) {
				if (installDirInput
						&& (efixDirInput && !fixPackDirInput)
						&& ((!efixList.isEmpty() || !efixJarList.isEmpty()) && fixPackList
								.isEmpty()) && !uninstall)
					return true;
			} else if (uninstall) {
				if (installDirInput
						&& (!efixList.isEmpty() && fixPackList.isEmpty())
						&& !install)
					return true;
			} else if (uninstallAll) {
				if (installDirInput && !install)
					return true;
			} else {
				if (installDirInput && !fixPack) {
					return true;
				}
			}

		} else if (fixPack) {
			if (install) {
				if (installDirInput && (fixPackDirInput && !efixDirInput)
						&& (!fixPackList.isEmpty() && efixList.isEmpty())
						&& !uninstall)
					return true;
			} else if (uninstall) {
				if (installDirInput
						&& (!fixPackList.isEmpty() && efixList.isEmpty())
						&& !install)
					return true;
			} else {
				if (installDirInput && !efix) {
					return true;
				}
			}
		}

		return false;
	}

	void output(String msg) {
		if (logOutput) {
			logFile.println(msg);
		}
	}

}
