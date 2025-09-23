/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import com.ibm.lconn.common.output.Output;
import com.ibm.lconn.common.output.OutputStreamOutput;
import com.ibm.lconn.common.output.StringBufferOutput;
import com.ibm.lconn.common.util.StringResolver;
import com.ibm.lconn.common.util.Util;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class CommandExec {

	public static boolean executeCommand(String cmd, String[] input,
			Output output, Properties macroProps) throws IOException,
			InterruptedException {
		if (input == null)
			input = new String[] {};
		String[] delimStr = Util.delimStr(cmd);
		for (int i = 0; i < delimStr.length; i++) {
			delimStr[i] = StringResolver.resolveMacro(delimStr[i], macroProps);
		}
		if("true".equals(System.getProperty("isDebug"))){
			System.out.println(getCommand(delimStr));
		}
		  
//		LogUtil.getLogger(CommandExec.class).log(Level.INFO, getCommand(delimStr));
		ProcessBuilder pb = new ProcessBuilder(delimStr);
		pb.redirectErrorStream(true);
		Process p;
		boolean succeed = false;
		p = pb.start();
		InputStream is = p.getInputStream();
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p
				.getOutputStream()));
		for (String in : input) {
			bw.write(in);
			bw.newLine();
		}
		bw.flush();
		bw.close();

		String line = null;
		while ((line = lnr.readLine()) != null) {
			output.append(line+"\n");
		}
		p.waitFor();
		if (p.exitValue() == 0) {
			succeed = true;
		}
		return succeed;
	}

	public static String getCommand(String[] cmd) {
		String re = null;
		for (int i = 0; i < cmd.length; i++) {
			if(re==null) re = cmd[i];
			else re += " " + cmd[i];
		}
		return re;
	}

	public static boolean executeCommand(String cmd, String[] input,
			StringBuffer output, Properties macroProps) throws IOException,
			InterruptedException {
		return executeCommand(cmd, input, new StringBufferOutput(output),
				macroProps);
	}
	
	public static boolean executeCommand(String cmd, String[] input,
			OutputStream output, Properties macroProps) throws IOException,
			InterruptedException {
		return executeCommand(cmd, input, new OutputStreamOutput(output),
				macroProps);
	}

}
