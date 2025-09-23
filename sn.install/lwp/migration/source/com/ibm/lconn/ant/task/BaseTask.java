/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.ant.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Replace;

import com.ibm.lconn.common.output.Output;
import com.ibm.lconn.common.output.OutputStreamOutput;
import com.ibm.lconn.common.output.StringBufferOutput;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.util.StringResolver;

/**
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class BaseTask extends Task {
	private Output output;
	private Properties macroProperties;
	private static final String ID_CURRENT_TASK = "CURRENT_TASK";
	private String[] args;
	private String prefix = "";
	private boolean failonerror = false;

	public boolean isFailonerror() {
		return failonerror;
	}
	
	protected void setTask(Task task){
		task.setProject(getProject());
		task.setTaskName(getTaskName());
		if(task instanceof BaseTask){
			BaseTask baseTask = (BaseTask) task;
			baseTask.setFailonerror(isFailonerror());
			baseTask.setCurrentTask(getCurrentTask());
			baseTask .setOutput(getOutput());
			baseTask.setMacroProperties(getMacroProperties());
		}
		
	}

	public void setFailonerror(boolean failonerror) {
		this.failonerror = failonerror;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCurrentTask() {
		String current = getMacroProperties().getProperty(ID_CURRENT_TASK);
		if (ObjectUtil.isEmpty(current))
			return getClass().getName();
		return current;
	}

	public void setCurrentTask(String taskName) {
		getMacroProperties().setProperty(ID_CURRENT_TASK, taskName);
	}

	protected void copyFile(File source, File dest, String token, String value,
			boolean overwrite) {
		try {
			FileInputStream fis = new FileInputStream(source);
			FileOutputStream fos = new FileOutputStream(dest);
			byte[] bt = new byte[1024];
			int len = fis.read(bt);
			while (len != -1) {
				fos.write(bt, 0, len);
				bt = new byte[1024];
				len = fis.read(bt);
			}
			fos.flush();
			fis.close();
			fos.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try{
			Replace cp = new Replace();
			cp.setProject(getProject());
			cp.setTaskName(getTaskName());
			cp.setFile(dest);
			cp.setToken(token);
			cp.setValue(value);
			cp.execute();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public BaseTask() {
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
		String msg2 = MessageFormat.format(msg, paras);
		log(msg2);
		Output out = getOutput();
		if (out != null) {
			try {
				out.append(msg2 + "\n");
			} catch (IOException e) {
				System.out.println("Log not enabled. ");
			}
		}

	}

	protected void log(Exception e) {
		StringBuilder output = new StringBuilder();
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
			log(output.toString());
		} catch (Exception e2) {
			log("Log not enabled. ");
		}
	}

	protected void fail(String msg, String para) {
		log(msg, para);
		if (isFailonerror()) {
			throw new BuildException();
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

	protected boolean notSet(String propertyName) {
		return null == propertyName
				|| (propertyName.startsWith("${") && propertyName.endsWith("}"));
	}

	public void setProperty(String key, String value, boolean needRemove) {
		if (key == null || value == null) {
			log("Setting property: {0}={1}, skipped", key, value);
			return;
		}
		getProject().setProperty(key, value);
		if (needRemove)
			value = "VALUE_REMOVED";
		log("Setting property: {0}={1}", key, value);
	}

	public void setProperty(String key, String value) {
		setProperty(key, value, false);
	}
}
