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
package com.ibm.websphere.update.ptf;

import java.util.Vector;
import java.util.*;
import java.io.*;

import com.ibm.websphere.update.util.PlatformUtils;

public class OSUtil {

	public OSUtil() {
		super();
	}
	public static boolean isLinux() {
      return PlatformUtils.isLinux();
	}

	public static boolean isSolaris() {
      return PlatformUtils.isSolaris();
	}

	public static boolean isHpux() {
      return PlatformUtils.isHpux();
	}

	public static boolean isAIX() {
      return PlatformUtils.isAIX();
	}

	public static boolean isWindows() {
      return PlatformUtils.isWindows();
	}

	public static boolean isWinNT() {
      return PlatformUtils.isWinNT();
	}
} 