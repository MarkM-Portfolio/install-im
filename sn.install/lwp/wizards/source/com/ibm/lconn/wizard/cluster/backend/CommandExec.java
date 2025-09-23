/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.wizard.cluster.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.command.CommandExecuter;
import com.ibm.lconn.wizard.common.command.CommandGenerator;
import com.ibm.lconn.wizard.common.command.CommandLogger;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class CommandExec {
	private String taskId;
	private List<String> command;
	private Map<String, String> environment;
	private String workDir;
	private String output;

	private Pattern[] remove = null;
	private String[] replace = null;

	public static CommandExec create(String taskId) {
		return new CommandExec(taskId);
	}

	public int execute() {
		this.command = CommandGenerator.gen(ClusterConstant.WIZARD_ID_CLUSTER,
				this.taskId);
		CommandLogger.setLogPath(this.output, true);
		CommandLogger.setRemove(remove);
		CommandLogger.setReplace(replace);
		int exitValue;
		if (ClusterConstant.TASK_VALIDATE_DM.equals(this.taskId)
				|| ClusterConstant.TASK_DETECT_MEMBERNAME.equals(taskId)
				|| ClusterConstant.TASK_FULL_SYNC_NODES.equals(taskId)
				|| ClusterConstant.TASK_RESTART_CLUSTER.equals(taskId)
				|| ClusterConstant.TASK_GET_WS_VARAIBLE_FROM_DM.equals(taskId)) {
			List<String> input = new ArrayList<String>();
			input.add("y");
			exitValue = CommandExecuter.run(this.command, this.workDir,
					this.environment, this.output, input);
		} else {
			exitValue = CommandExecuter.run(this.command, this.workDir,
					this.environment, this.output);
		}
		CommandLogger.setRemove(null);
		CommandLogger.setReplace(null);

		return exitValue;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setEnvironment(Map<String, String> env) {
		this.environment = env;
	}

	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	private CommandExec(String taskId) {
		this.taskId = taskId;
	}

	public Pattern[] getRemove() {
		return remove;
	}

	public void setRemove(Pattern[] remove) {
		this.remove = remove;
	}

	public String[] getReplace() {
		return replace;
	}

	public void setReplace(String[] replace) {
		this.replace = replace;
	}
}
