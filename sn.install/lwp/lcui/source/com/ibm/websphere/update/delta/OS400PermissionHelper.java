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

package com.ibm.websphere.update.delta;

import java.io.File;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.websphere.update.delta.PermissionHelper;

/**
 * @author guminy
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class OS400PermissionHelper implements PermissionHelper {

	private String fileName = null;
	private String instName = null;
	private static final String QSYS = "QSYS";
	private static final String QHTTPSVR = "QHTTPSVR";
	private static final String QEJBSVR = "QEJBSVR";
	private static final String PUBLIC = "*PUBLIC";
	private static final String WAS_USER_DIR = "/QIBM/USERDATA/WEBAS5/BASE/";
	private boolean userData = false;

	/**
	 * @see com.ibm.websphere.update.delta.PermissionHelper#setFilename(String)
	 */
	public void setFilename(String fileName) {
		int pos = fileName.indexOf("//");
		if(pos != -1)
			fileName = fileName.substring(0, pos) + "/" + fileName.substring(pos + 2);
		this.fileName = fileName;
		File file = new File(fileName);
		if (!file.exists())
		{
			//not exist, do nothing.
			return;
		}
		
		//set teh CCSID of the file
		run400Cmd("QSH CMD('setccsid 819 " + this.fileName + "')");

		//determine if it's the deployed EAR or just ProdData
		String tempDir = fileName.toUpperCase();
		userData = tempDir.toUpperCase().startsWith(WAS_USER_DIR);

		if (userData) {

			//need to determine instance name
			int start = WAS_USER_DIR.length();

			//remove the WAS userdata directory from the path
			tempDir = tempDir.substring(start);
			//would have was_inst/installedApps/cell_name/WC_inst.ear/path
			//System.out.println("tempDir is " + tempDir);

			//find first slash
			start = tempDir.indexOf('/') + 1;
			//would have installedApps/cell_name/WC_inst.ear/path

			start = tempDir.indexOf('/', start) + 1;
			//would have cell_name/WC_inst.ear/path

			start = tempDir.indexOf('/', start) + 1;
			//would have WC_inst.ear/path

			instName =
				tempDir.substring(start + 3, tempDir.indexOf(".EAR/", start));
		}

	}

	/**
	 * @see com.ibm.websphere.update.delta.PermissionHelper#setOwner(String)
	 */
	public int setOwner(String owner) {
		if (instName == null) {
			changeOwner(fileName, QSYS);
		} else {
			changeOwner(fileName, instName);
		}
		return 0;
	}

	/**
	 * @see com.ibm.websphere.update.delta.PermissionHelper#setPermissions(String)
	 */
	public int setPermissions(String permissions) {
		if (instName == null) {
			changeFileAuth(this.fileName, QSYS, "*RWX", "*ALL");
			changeFileAuth(this.fileName, PUBLIC, "*RX", "*NONE");
		} else {
			changeFileAuth(this.fileName, instName, "*RWX", "*ALL");
			changeFileAuth(this.fileName, PUBLIC, "*RX", "*NONE");
			changeFileAuth(this.fileName, QEJBSVR, "*RX", "*ALL");
		}
		return 0;
	}

	/**
	 * @see com.ibm.websphere.update.delta.PermissionHelper#setGroup(String)
	 */
	public int setGroup(String group) {
		if (instName == null) {
			//changeOwner(fileName,QSYS);
		} else {
			changeGroup(fileName, QEJBSVR);
		}
		return 0;
	}

	/**
	 * Method changeAuth.
	 * @param Dir
	 * @param User
	 * @param DataAuth
	 * @param ObjAuth
	 * @param isOwner
	 */
	//the following added by guminy for defect 52987
	/**
	 * New methods for iSeries permission
	 */

	public static void changeAuth(
		String Dir,
		String User,
		String DataAuth,
		String ObjAuth,
		boolean isOwner) {
		if (Dir.endsWith("/")) {
			/** d55763
			char[] tmpChar = new char[Dir.length() - 1];
			Dir.getChars(0,Dir.length() - 2, tmpChar, 0);
			Dir = String.valueOf(tmpChar);
			**/
			Dir = Dir.substring(0, Dir.length() - 1);
		}

		if (isOwner) {
			changeOwner(Dir, User);
		} else {
			changeFileAuth(Dir, User, DataAuth, ObjAuth);
		}

		File sourceDir = new File(Dir);
		String[] files;
		files = sourceDir.list();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = Dir + "/" + files[i];
				File newFile = new File(fileName);
				if (newFile.isDirectory()) {
					if (isOwner) {
						changeAuth(fileName, User, DataAuth, ObjAuth, true);
					} else {
						changeAuth(fileName, User, DataAuth, ObjAuth, false);
					}
				} else {
					if (isOwner) {
						changeOwner(fileName, User);
						// defect 12483 YWH set authority too
						changeFileAuth(fileName, User, DataAuth, ObjAuth);
						// end of 12483
					} else
						changeFileAuth(fileName, User, DataAuth, ObjAuth);
				}
			}
		}
	} //AS400

	/**
	 * Method changeFileAuth.
	 * @param File
	 * @param User
	 * @param DataAuth
	 * @param ObjAuth
	 */
	public static int changeFileAuth(
		String File,
		String User,
		String DataAuth,
		String ObjAuth) {

		String cmdString =
			"CHGAUT OBJ('"
				+ File
				+ "') USER("
				+ User
				+ ") DTAAUT("
				+ DataAuth
				+ ") OBJAUT("
				+ ObjAuth
				+ ")";
		/*String Msg_Id01 = */
		return run400Cmd(cmdString);
	} //AS400

	/**
	 * Method changeFileAuth.
	 * @param system
	 * @param userId
	 * @param userPwd
	 * @param File
	 * @param User
	 * @param DataAuth
	 * @param ObjAuth
	 */
	public static int changeFileAuth(
		String system,
		String userId,
		String userPwd,
		String File,
		String User,
		String DataAuth,
		String ObjAuth) {
		if (system == null || userId == null || userPwd == null) {

			return changeFileAuth(File, User, DataAuth, ObjAuth);
		}

		String cmdString =
			"CHGAUT OBJ('"
				+ File
				+ "') USER("
				+ User
				+ ") DTAAUT("
				+ DataAuth
				+ ") OBJAUT("
				+ ObjAuth
				+ ")";
		/*String Msg_Id01 = */
		return run400Cmd(system, userId, userPwd, cmdString);
	} //AS400

	/**
	 * Method changeOwner.
	 * @param File
	 * @param User
	 */
	public static int changeOwner(String File, String User) {

		String cmdString = "CHGOWN OBJ('" + File + "') NEWOWN(" + User + ")";
		/*String Msg_Id01 = */
		return run400Cmd(cmdString);
	} //AS400

	public static int changeGroup(String File, String User) {

		String cmdString =
			"CHGPGP OBJ('" + File + "') NEWPGP(" + User + ") DTAAUT(*RX)";
		/*String Msg_Id01 = */
		return run400Cmd(cmdString);
	} //AS400

	/**
	 * Method run400Cmd.
	 * @param cmdString
	 * @return String
	 */
	public static int run400Cmd(String cmdString) {
		boolean result;
		try {

			AS400 sys = new AS400();
			CommandCall cmd = new CommandCall(sys);
			//			System.out.println(cmdString);
			result = cmd.run(cmdString);
			AS400Message[] messageList = cmd.getMessageList();
			int i;

			sys.disconnectAllServices();
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}

		if (!result) {
			return 1;
		} else {
			return 0;
		}
	} //AS400

	/**
	 * Method run400Cmd.
	 * @param system
	 * @param user
	 * @param password
	 * @param cmdString
	 * @return String
	 */
	public static int run400Cmd(
		String system,
		String user,
		String password,
		String cmdString) {
		boolean result;

		if (system == null || password == null || user == null) {
			return run400Cmd(cmdString);
		}

		try {

			AS400 sys = new AS400(system, user, password);
			CommandCall cmd = new CommandCall(sys);
			result = cmd.run(cmdString);
			//			AS400Message[] messageList = cmd.getMessageList();
			// Display messages
			//System.out.println("cmd is " + cmdString);
			/*if (messageList.length > 0) {
				for (int i=0; i<messageList.length; i++)
					System.out.println(messageList[i].getID() + ": " + messageList[i].getText());
			} */
			sys.disconnectAllServices();
		} catch (Exception e) {
//			e.printStackTrace();
			return 1;
		}

		if (!result) {
			return 1;
		} else {
			return 0;
		}
	} //AS400

	/**
	 * Method run400CmdWithOwner.
	 * @param cmdString
	 * @param username
	 * @param password
	 * @return String
	 */
	public static String run400CmdWithOwner(
		String cmdString,
		String username,
		String password) {
		try {

			//System.out.println(cmdString);

			//Class sys = Class.forName(com.ibm.as400.access.AS400).newInstance();
			//Class cmd = Class.forName(com.ibm.as400.access.CommandCall).newInstance(sys);
			AS400 sys = new AS400();
			sys.setUserId(username);
			sys.setPassword(password);
			CommandCall cmd = new CommandCall(sys);
			cmd.run(cmdString);
			AS400Message[] messageList = cmd.getMessageList();
			int i;
			if (messageList.length > 0) {
				for (i = 0; i < messageList.length; i++) {
					// Comment if you do not want to see the messages
					// System.out.println(messageList[i].getID() + ": " + messageList[i].getText());
				}
				sys.disconnectAllServices();
				return messageList[i - 1].getID();
			} else {
				sys.disconnectAllServices();
				return " ";
			}
		} catch (Exception e) {
//			e.printStackTrace();
			return "Error";
		}
	} //AS400

	public String getOwner() {
		//return a dummy value
		return "_";
	}

	public String getPermissions() {
		//return a dummy value
		return "_";
	}

	public String getGroup() {
		//return a dummy value
		return "_";
	}

}
