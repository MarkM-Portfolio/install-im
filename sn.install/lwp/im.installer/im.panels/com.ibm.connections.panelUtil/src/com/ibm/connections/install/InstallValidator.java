/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited. 2007, 2022                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osgi.util.NLS;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;

public class InstallValidator {
	// private static final Logger _logger =
	// Logger.getLogger(com.ibm.connections.install.InstallValidator.class);
	final String _className = "InstallValidator";
	private String message = "";
	private final ILogger log = IMLogger.getLogger(InstallValidator.class);
	private String HostAddress;
	// These should be escaped or prevented for CE to process
	public static String XML_SPL_CHARS = "&\"<>'";
	// These should be avoided for OS scripts for WAS config
	public static String SCRIPT_SPL_CHARS = ";|+/%^";
	public static String INVALID_PWD_CHARS = XML_SPL_CHARS + SCRIPT_SPL_CHARS;

	public static final String DB_DB2 = "db2";
	public static final String DB_ORACLE = "oracle";
	public static final String DB_SQLSERVER = "sqlserver";
	//public static final String JDBC_DB2_LIB = "db2jcc.jar";
	public static final String JDBC_DB2_LIB = "db2jcc4.jar";
	public static final String JDBC_DB2_LICENSE = "db2jcc_license_cu.jar";
	public static final String JDBC_ORACLE_LIB = "ojdbc14.jar";
	public static final String JDBC_SQLSERVER_LIB = "sqljdbc.jar";

	public String getHostAddress() {
		return HostAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		message = msg;
	}

	// check if string contains a space
	public boolean containsSpace(String str) {
		try {
			if ((str == null) || "".equals(str)) {
				return false;
			}
			String invalidSpaces = " ";
			char invalidSpace = invalidSpaces.charAt(0);
			if (str.indexOf(invalidSpace) >= 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// check for invalid characters
	public boolean containsInvalidChar(String str) throws Exception {
		try {
			if ((str == null) || "".equals(str)) {
				return false;
			}
			String invalidChars = "{};*?\"<>|%,=+&'#^!`$[]/\\";
			for (int i = 0; i < invalidChars.length(); i++) {
				char invalidChar = invalidChars.charAt(i);
				if (str.indexOf(invalidChar) >= 0) {
					// if the string contains one of these characters,
					return true;
				}
			}
			// if we don't find one, return false
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean containsInvalidCharForClusterName(String str) {
		try {
			if ((str == null) || "".equals(str)) {
				return false;
			}
			String invalidChars = ";*?\"<>|,=+&'#$";
			for (int i = 0; i < invalidChars.length(); i++) {
				char invalidChar = invalidChars.charAt(i);
				if (str.indexOf(invalidChar) >= 0) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	// check for invalid characters
	public boolean containsInvalidChar1(String str) throws Exception {
		try {
			if ((str == null) || "".equals(str)) {
				return false;
			}
			String invalidChars = "{}*?\"<>|%,=+&'#^!`$[].";
			for (int i = 0; i < invalidChars.length(); i++) {
				char invalidChar = invalidChars.charAt(i);
				if (str.indexOf(invalidChar) >= 0) {
					// if the string contains one of these characters,
					return true;
				}
			}
			// if we don't find one, return false
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	// check for invalid characters
	public boolean containsInvalidChar2(String str) throws Exception {
		try {
			if ((str == null) || "".equals(str)) {
				return false;
			}
			String invalidChars = "{};*?\"<>|%+&'#^!`$[]";
			for (int i = 0; i < invalidChars.length(); i++) {
				char invalidChar = invalidChars.charAt(i);
				if (str.indexOf(invalidChar) >= 0) {
					// if the string contains one of these characters,
					return true;
				}
			}
			// if we don't find one, return false
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean containsInvalidPassword(String str) {

		if ((str == null) || "".equals(str)) {
			return false;
		}

		for (int i = 0; i < INVALID_PWD_CHARS.length(); i++) {
			char invalidChar = INVALID_PWD_CHARS.charAt(i);
			if (str.indexOf(invalidChar) >= 0) {
				// if the string contains one of these characters,
				return true;
			}
		}
		// if we don't find one, return false
		return false;

	}

	public boolean validateEmailAddress(String str) throws Exception {
		String expression = "^([_A-Za-z0-9\\!\\#$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\`{\\|\\}\\~]+\\.)+"
				+ "[A-Za-z0-9\\!\\#$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\`{\\|\\}\\~]+"
				+ "@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		CharSequence inputStr = str;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	public boolean validateEmailAddress1(String str) throws Exception {
		String expression = "^[_A-Za-z0-9\\!\\#$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\`{\\|\\}\\~]+"
				+ "(\\.[A-Za-z0-9\\!\\#$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\`{\\|\\}\\~]+)*";

		CharSequence inputStr = str;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	public boolean validateEmailAddress2(String str) throws Exception {
		String expression = "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		CharSequence inputStr = str;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	// returns true if the string given is an IP address
	public boolean isIPAddress(String hostName) {
		final String _methodName = "isIPAddress";

		// _logger.debug(_className, _methodName);

		// check that this string is not an IP address by comparing the original
		// string to getHostAddress
		try {
			InetAddress HostInet = InetAddress.getByName(hostName);

			if (hostName.equalsIgnoreCase(HostInet.getHostAddress())) {
				// what was entered was an IP address that was resolved to the
				// same IP address
				return true;
			} else {
				return false;
			}
		} catch (UnknownHostException uhe) {

			// _logger.info(_className, _methodName);
			return false;
		}

	}

	// validate port number
	public boolean portNumValidate(String pn) {

		int PortInt = -1;
		if (pn.length() == 0) {
			setMessage(Messages.warning_port_empty);
			return false;
		}
		try {
			PortInt = Integer.parseInt(pn.trim());
			// check that the port number is within a valid range
			if ((1 > PortInt) || (PortInt > 65535)) {
				setMessage(Messages.warning_port_invalid);
				return false;
			}
		} catch (NumberFormatException nfe) {
			setMessage(Messages.warning_port_invalid);
			return false;
		}

		return true;
	}

	public boolean hostNameValidate(String hn) {

		// if (hn.length() == 0) {
		// setMessage(Messages.warning_host_empty);
		// return false;
		// }
		//
		// int index = hn.indexOf("."); // not work for ipv6
		// try {
		// if (containsInvalidChar(hn) || containsSpace(hn)) { // || index == -1
		// ) {
		// setMessage(Messages.warning_host_invalid);
		// return false;
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return false;
		// }
		// return true;

		if (hn == null || "".equals(hn.trim())) {
			setMessage(Messages.warning_host_empty);
			return false;
		}
		if (hn.length() > 63) {
			setMessage(Messages.warning_host_toolong);
			return false;
		}

		if (validIP(hn))
			return true;

		if (hn.endsWith(".")) {
			setMessage(Messages.warning_host_invalid);
			return false;
		}

		// The FQDN must have at least one "dot" or period according to the
		// Internet standard
		if (hn.indexOf('.') == -1) {
			setMessage(Messages.warning_host_invalid);
			return false;
		}
		String evalStr = hn;
		int gradeNum = 0;
		while (evalStr.length() != 0) {
			int dotIndex = evalStr.indexOf('.');
			if (dotIndex == -1) {
				boolean valid = validHost(evalStr, false);
				if (!valid) {
					setMessage(Messages.warning_host_invalid);
					return false;
				} else {
					return true;
				}
			} else {
				boolean valid = validHost(evalStr.substring(0, dotIndex),
						gradeNum == 0);
				if (!valid) {
					setMessage(Messages.warning_host_invalid);
					return false;
				}
				evalStr = evalStr.substring(dotIndex + 1);
				gradeNum++;
			}
			// System.out.println("SDFA");
		}
		return true;

	}
	public boolean hostNameValidateForNotificationPanel(String hn) {

		if (hn == null || "".equals(hn.trim())) {
			setMessage(Messages.warning_host_empty);
			return false;
		}
		if (hn.length() > 63) {
			setMessage(Messages.warning_host_toolong);
			return false;
		}


		if (hn.endsWith(".")) {
			setMessage(Messages.warning_host_invalid);
			return false;
		}

		// The FQDN must have at least one "dot" or period according to the
		// Internet standard
		if (hn.indexOf('.') == -1) {
			setMessage(Messages.warning_host_invalid);
			return false;
		}
		String evalStr = hn;
		int gradeNum = 0;
		while (evalStr.length() != 0) {
			int dotIndex = evalStr.indexOf('.');
			if (dotIndex == -1) {
				boolean valid = validHost(evalStr, false);
				if (!valid) {
					setMessage(Messages.warning_host_invalid);
					return false;
				} else {
					return true;
				}
			} else {
				boolean valid = validHost(evalStr.substring(0, dotIndex),
						gradeNum == 0);
				if (!valid) {
					setMessage(Messages.warning_host_invalid);
					return false;
				}
				evalStr = evalStr.substring(dotIndex + 1);
				gradeNum++;
			}
		}
		return true;

	}
	public boolean hostNameValidateForWasPanel(String hn) {

		// if (hn.length() == 0) {
		// setMessage(Messages.warning_host_empty);
		// return false;
		// }
		//
		// int index = hn.indexOf("."); // not work for ipv6
		// try {
		// if (containsInvalidChar(hn) || containsSpace(hn)) { // || index == -1
		// ) {
		// setMessage(Messages.warning_host_invalid);
		// return false;
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return false;
		// }
		// return true;

		if (hn == null || "".equals(hn.trim())) {
			setMessage(Messages.warning_host_empty);
			return false;
		}
		if (hn.length() > 63) {
			setMessage(Messages.warning_host_toolong);
			return false;
		}

		// if (validIP(hn))
		// return true;

		if (hn.endsWith(".")) {
			setMessage(Messages.warning_host_invalid);
			return false;
		}

		// The FQDN must have at least one "dot" or period according to the
		// Internet standard
		if (hn.indexOf('.') == -1) {
			setMessage(Messages.warning_host_invalid);
			return false;
		}
		String evalStr = hn;
		int gradeNum = 0;
		while (evalStr.length() != 0) {
			int dotIndex = evalStr.indexOf('.');
			if (dotIndex == -1) {
				boolean valid = validHost(evalStr, false);
				if (!valid) {
					setMessage(Messages.warning_host_invalid);
					return false;
				} else {
					return true;
				}
			} else {
				boolean valid = validHost(evalStr.substring(0, dotIndex),
						gradeNum == 0);
				if (!valid) {
					setMessage(Messages.warning_host_invalid);
					return false;
				}
				evalStr = evalStr.substring(dotIndex + 1);
				gradeNum++;
			}
			// System.out.println("SDFA");
		}
		return true;

	}

	public static boolean validHost(String host, boolean isFirstGrade) {
		if (isFirstGrade == true && host.matches("\\d*"))
			return false;
		return host.matches("[a-zA-Z0-9][a-zA-Z0-9\\-]*");
	}

	public static boolean validIP(String ip) {
		String IP_VALID = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		return ip.matches(IP_VALID);
	}

	public boolean isHostResolvable(String hostName) {
		final String methodName = "isHostResolvable";

		// _logger.debug("Enter.." + _className);

		// check if it is IP address
		if (isIPAddress(hostName)) {
			setMessage(Messages.warning_ip_host);
			return false;
		}

		// check that we can detect the host name
		try {
			InetAddress[] HostInets = InetAddress.getAllByName(hostName);

			try {
				// wait 5 seconds
				int timeout = 5000;

				for (int i = 0; i < HostInets.length; i++) {
					if (HostInets[i].isReachable(timeout)) {
						/*
						 * if ( HostInets[i] instanceof Inet6Address){
						 * HostAddress = "[" + HostInets[i].getHostAddress() +
						 * "]";
						 * 
						 * } else { HostAddress = HostInets[i].getHostAddress();
						 * 
						 * // For spr#TZHU7TP8PL should use hostname instead of
						 * IP // * use hostname to connect DB2/LDAP may cause
						 * error // * the hostname could lead to the wrong IP
						 * HostAddress = hostName; }
						 */

						// For SPR#MHEG7T5MD8, only support host name
						HostAddress = hostName;

						return true;
					}
				}
			} catch (Exception e) {
				setMessage(Messages.warning_serverConnect);
				e.getStackTrace();
				return false;
			}
		} catch (UnknownHostException uhe) {
			String errorms = NLS.bind(Messages.warning_serverUnknow, hostName);
			setMessage(errorms);
			uhe.getStackTrace();
			return false;
		}
		setMessage(Messages.warning_serverTimeout);
		return false;
	}

	public File getLogFile(String _logFile) {

		File log = null;
		File tmpLog = log;
		// _logger.info("logFile specified - " + _logFile);
		if (_logFile != null && _logFile.length() > 0) {

			try {
				String tmpDir = getTempDir();
				tmpLog = new File(tmpDir, _logFile);
				// _logger.info("temp logFile - " + tmpLog.getPath());

				// success - so use the tmp log
				if (tmpLog != null)
					// delete old copy, if present
					if (tmpLog.exists()) {
						tmpLog.delete();
					}
				log = tmpLog;
			} catch (Exception ioe) {

				// use existing (cwd) fall back - log
				// _logger.info("Error creating temp logFile  - " +
				// ioe.getLocalizedMessage());
				// ioe.printStackTrace();
			}
		}
		return log;
	}

	public String getTempDir() {
		String tempdir = System.getProperty("java.io.tmpdir");

		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\")))
			tempdir = tempdir + System.getProperty("file.separator");

		return tempdir;

	}

	public String escapeChars(String str) {
		String retVal = str;

		if (str == null) {
			return retVal;
		}

		if (str.contains("&amp;") || str.contains("&quot;")
				|| str.contains("&lt;") || str.contains("&gt;")
				|| str.contains("&apos;")) {
			// already escaped string
			return retVal;
		}
		for (int i = 0; i < XML_SPL_CHARS.length(); i++) {
			String expr = XML_SPL_CHARS.substring(i, i + 1);
			// System.out.println("here - " + expr);
			String escExpr = expr;
			switch (expr.charAt(0)) {
			case '&':
				escExpr = "&amp;";
				break;
			case '\"':
				escExpr = "&quot;";
				break;
			case '<':
				escExpr = "&lt;";
				break;
			case '>':
				escExpr = "&gt;";
				break;
			case '\'':
				escExpr = "&apos;";
				break;
			}

			// replace all occurences of each spl chars
			// to orig string
			str = str.replaceAll(expr, escExpr);

		}
		retVal = str;
		// if we don't find one, return false
		return retVal;

	}

	public boolean detectFileExists(String path) {
		File f = new File(path);
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	public boolean detectFileExists(String name, String folder) {
		File f = new File(folder, name);
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	// validate the directory
	private boolean detectDirectoryWritable(String dir) {
		boolean flag = true;
		File file = new File(dir);
		if (file.exists() && !isDirectoryWritable(file))
			return false;

		List lDirs = new ArrayList();
		String curFolder = dir;
		while (null != curFolder && !file.exists()) {
			lDirs.add(curFolder);
			curFolder = file.getParent();
			if (curFolder != null)
				file = new File(curFolder);
		}

		// push the non-exist directory into the list
		// try to see if exception to create those directory that doesn't
		// exist
		for (int i = lDirs.size() - 1; i >= 0; i--) {
			File tempFile = new File((String) lDirs.get(i));
			flag = tempFile.mkdir();
			if (flag == false)
				break;
		}

		// delete those directories which are created temoporarily
		for (int i = 0; i < lDirs.size(); i++) {
			File tempFile = new File((String) lDirs.get(i));
			flag = tempFile.delete();
			if (flag == false)
				break;
		}

		return flag;
	}

	private boolean isDirectoryWritable(String dirpath) {
		String tempPath = dirpath + File.separator
				+ (int) (Math.random() * 10000000D) + ".tmp";
		File tempFile = new File(tempPath);
		return tempFile.mkdir() && tempFile.delete();
	}

	private boolean isDirectoryWritable(File file) {
		String filePath = file.getAbsolutePath();
		return isDirectoryWritable(filePath);
	}

	public boolean validatePath(String path) {
		if (null == path || "".equals(path)) {
			setMessage(Messages.PATH_NOT_VALIDATE);
			return false;
		}

		path = path.trim();

		boolean formatCheckFlag = validateFilePath(path)
				|| validateNetworkPath(path);
		log.info("Format Check Flag is:" + formatCheckFlag + ", message is:"
				+ getMessage());
		if (formatCheckFlag) {
			// if (!detectFileExists(path)) {
			// setMessage(Messages.PATH_NOT_FOUND + path);
			// return false;
			// }
			if (!detectDirectoryWritable(path)) {
				setMessage(Messages.PATH_NOT_WRITABLE + path);
				return false;
			}
		} else
			return false;
		return true;
	}

	public boolean validateFilePath(String filePath) {
		if (null == filePath || "".equals(filePath)) {
			setMessage(Messages.PATH_NOT_VALIDATE);
			return false;
		}

		if (getOSType().indexOf("win") > -1) {
			if (filePath
					.matches("^[a-zA-Z]:([\\\\|/][^/\\\\:*?\"<>|]+)+[\\\\|/]?$")) {
				return true;
			}
		} else {
			if (filePath.matches("^(/[^/]+)+[/]?$")) {
				return true;
			}
		}

		setMessage(Messages.PATH_CONTAIN_INVALID_CHA);
		return false;
	}

	public boolean validateFilePathIgnorePlatform(String filePath) {
		if (null == filePath || "".equals(filePath)) {
			setMessage(Messages.PATH_NOT_VALIDATE);
			return false;
		}

		if (filePath
				.matches("^[a-zA-Z]:([\\\\|/][^/\\\\:*?\"<>|]+)+[\\\\|/]?$")) {
			return true;
		} else if (filePath.matches("^(/[^/]+)+[/]?$")) {
			return true;
		}

		setMessage(Messages.PATH_CONTAIN_INVALID_CHA);
		return false;
	}

	public boolean validateNetworkPath(String networkPath) {
		if (null == networkPath || "".equals(networkPath)) {
			setMessage(Messages.PATH_NOT_VALIDATE);
			return false;
		}

		if (getOSType().indexOf("win") > -1) {
			return validateUNCFormat(networkPath);
		} else {
			return validateNFSFormat(networkPath);
		}
	}

	public boolean validateUNCFormat(String path) {
		if (null == path || "".equals(path)) {
			setMessage(Messages.PATH_NOT_VALIDATE);
			return false;
		}

		String s1 = path.substring(path.indexOf("\\\\") + 2);
		if (s1.indexOf("\\") == -1) {
			return false;
		}
		String s2 = s1.substring(s1.indexOf("\\"));
		String s3 = s2.substring(s2.indexOf("\\") + 1);
		if (path.startsWith("\\\\") && !s1.startsWith("\\")
				&& s2.startsWith("\\") && !"".equals(s3)
				&& !s3.startsWith("\\"))
			return true;
		return false;
	}

	public boolean validateNFSFormat(String path) {
		if (null == path || "".equals(path)) {
			setMessage(Messages.PATH_NOT_VALIDATE);
			return false;
		}
		String s1 = path.substring(path.indexOf("//") + 2);
		if (s1.indexOf("/") == -1) {
			return false;
		}
		String s2 = s1.substring(s1.indexOf("/"));
		String s3 = s2.substring(s2.indexOf("/") + 1);
		if (path.startsWith("//") && !s1.startsWith("/") && s2.startsWith("/")
				&& !"".equals(s3) && !s3.startsWith("/"))
			return true;
		return false;
	}

	public String getOSType() {
		return System.getProperty("os.name").toLowerCase();
	}

	public static void main(String args[]) {
		InstallValidator iv = new InstallValidator();

	}
}
