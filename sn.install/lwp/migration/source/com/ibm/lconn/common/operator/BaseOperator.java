/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.common.operator;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Properties;

import com.ibm.lconn.common.output.Output;
import com.ibm.lconn.common.output.OutputStreamOutput;
import com.ibm.lconn.common.output.StringBufferOutput;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.util.StringResolver;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */

public abstract class BaseOperator {

	private Output output;
	private Properties macroProperties;
	private static final String ID_CURRENT_TASK = "CURRENT_TASK";
	private String[] args;

	public String getCurrentTask() {
		String current = getMacroProperties().getProperty(ID_CURRENT_TASK);
		if (ObjectUtil.isEmpty(current))
			return getClass().getName();
		return current;
	}

	public void setCurrentTask(String taskName) {
		getMacroProperties().setProperty(ID_CURRENT_TASK, taskName);
	}

	public BaseOperator() {
		super();
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public Properties getMacroProperties() {
		if (macroProperties == null)
			return new Properties();
		return macroProperties;
	}

	public String resolve(String str) {
		return StringResolver.resolveMacro(str, getMacroProperties());
	}

	abstract public boolean execute(String para);

	public void setMacroProperties(Properties macroProperties) {
		this.macroProperties = macroProperties;
	}

	protected Output getOutput() {
		return output;
	}

	public void setOutput(StringBuffer sb) {
		if (sb != null)
			this.output = new StringBufferOutput(sb);
		else
			this.output = null;
	}

	public void setOutput(OutputStream os) {
		if (os != null)
			this.output = new OutputStreamOutput(os);
		else
			this.output = null;
	}

	public void setOutput(Output out) {
		this.output = out;
	}

	protected void log(String msg, Object... paras) {
		Output out = getOutput();
		if (out != null) {
			try {
				out.append(MessageFormat.format(msg, paras) + "\n");
			} catch (IOException e) {
				System.out.println("Log not enabled. ");
			}
		}

	}

	protected void log(Exception e) {
		try {
			output.append(e.toString() + "\n");
			StackTraceElement[] stack = e.getStackTrace();
			for (int i = 0; i < stack.length; i++)
				output.append("\tat " + stack[i] + "\n");

			StackTraceElement[] parentStack = stack;
			Throwable throwable = e.getCause();
			while (throwable != null) {
				output.append("Caused by: ");
				output.append(throwable + "\n");
				StackTraceElement[] currentStack = throwable.getStackTrace();
				int duplicates = countDuplicates(currentStack, parentStack);
				for (int i = 0; i < currentStack.length - duplicates; i++)
					output.append("\tat " + currentStack[i] + "\n");
				if (duplicates > 0) {
					output.append("\t... " + duplicates + " more" + "\n");
				}
				parentStack = currentStack;
				throwable = throwable.getCause();
			}
		} catch (Exception e2) {
			System.out.println("Log not enabled. ");
		}
	}

	private int countDuplicates(StackTraceElement[] currentStack,
			StackTraceElement[] parentStack) {
		int duplicates = 0;
		int parentIndex = parentStack.length;
		for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0;) {
			StackTraceElement parentFrame = parentStack[parentIndex];
			if (parentFrame.equals(currentStack[i])) {
				duplicates++;
			} else {
				break;
			}
		}
		return duplicates;
	}

}
