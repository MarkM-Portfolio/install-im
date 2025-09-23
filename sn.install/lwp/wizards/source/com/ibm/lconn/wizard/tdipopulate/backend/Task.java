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
package com.ibm.lconn.wizard.tdipopulate.backend;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.command.CommandResultInfo;
import com.ibm.lconn.wizard.common.logging.LogUtil;
import com.ibm.lconn.wizard.common.msg.Messages;
import com.ibm.lconn.wizard.common.ui.CommonHelper;
import com.ibm.lconn.wizard.tdipopulate.properties.PropertyMapping;

public abstract class Task {
	public static final Logger logger = LogUtil.getLogger(Task.class);
	public static final String FS = System.getProperty("file.separator");
	public static final String WORKDIR = Constants.TDI_WORK_DIR;
	private static String commonLog = null;
	private static File commonLogFile = null;
	public static final String LOGROOT = "tdi";
	public static final String TYPE_MARK_MANAMER = Constants.LDAP_OPTIONAL_TASK_MARK_MANAGER;
	public static final String TYPE_FILL_COUNTRY = Constants.LDAP_OPTIONAL_TASK_FILL_COUNTRIES;
	public static final String TYPE_FILL_DEPARTMENT = Constants.LDAP_OPTIONAL_TASK_FILL_DEPARTMENT;
	public static final String TYPE_FILL_ORGANIZATION = Constants.LDAP_OPTIONAL_TASK_FILL_ORGANIZATION;
	public static final String TYPE_FILL_EMPLOYEETYPE = Constants.LDAP_OPTIONAL_TASK_FILL_EMPLOYEE;
	public static final String TYPE_FILL_WORKLOCATOIN = Constants.LDAP_OPTIONAL_TASK_FILL_WORK_LOCATION;
	public static final String TYPE_POPULATE_DB = Constants.TDI_DEFAULT_TASK;

	protected String osType = CommonHelper.getPlatformType();
	protected String logPath;
	protected Map<String, String> env;

	public static String CreateLogForTasks() {
		commonLog = "tdi_"
			+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
			+ ".log";
		File logDir = new File(Constants.LOG_ROOT + FS + LOGROOT);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
	
		commonLogFile = new File(Constants.LOG_ROOT + FS + LOGROOT + FS
			+ commonLog);
	
		return commonLogFile.getAbsolutePath();
	}

	public static CommandResultInfo runCommand(Properties UIProp,
			Properties sources) {
		CommandResultInfo result = new CommandResultInfo();
		Map<String, String> environment = new HashMap<String, String>();
		environment.put("TDIPATH", UIProp.getProperty("TDI.dir"));
		environment.put("TDI_CS_HOST", "localhost");
		environment.put("TDI_CS_PORT", "1527");
		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			if (!NetStore.isStarted(environment)) {
				NetStore.start(environment);
			}
		}

		try {
			if (commonLog == null) {
				commonLog = "tdi_"
						+ new SimpleDateFormat("yyyyMMdd_HHmmss")
								.format(new Date()) + ".log";
			}
			setSources(sources);
			setValues(UIProp);

			String message = "";
			int exitValue = CommandResultInfo.COMMAND_SUCCEED;
			String populate = UIProp.getProperty("populate.task.do");
			if (Constants.BOOL_TRUE.equalsIgnoreCase(populate)) {
				Task t = loadTask(TYPE_POPULATE_DB, UIProp);
				CommandResultInfo res = t.run();
				message += "\n";
				message += getTaskName(TYPE_POPULATE_DB);
				message += "\n";
				if (res.getExecState() == CommandResultInfo.COMMAND_SUCCEED) {
					message += Messages.getString("tdipopulate.tdi.output", res
							.getExitMessage());
				} else {
					message += Messages
							.getString("tdipopulate.backend.task.error");
				}
				message += "\n";
				exitValue = exitValue > res.getExecState() ? exitValue : res
						.getExecState();
			}

			String taskList = UIProp.getProperty("task.list").trim();
			String[] taskIDs = taskList.split(",");

			int l = taskIDs.length;
			for (int i = 0; i < l; i++) {
				if ("".equals(taskIDs[i].trim()))
					continue;

				if (TYPE_POPULATE_DB.equals(taskIDs[i].trim()))
					continue;

				Task t = loadTask(taskIDs[i].trim(), UIProp);

				CommandResultInfo res = t.run();
				message += "\n";
				message += getTaskName(taskIDs[i].trim());
				message += "\n";
				if (res.getExecState() == CommandResultInfo.COMMAND_SUCCEED) {
					message += Messages
							.getString("tdipopulate.backend.task.successful");
				} else {
					message += Messages
							.getString("tdipopulate.backend.task.error");
				}
				message += "\n";
				exitValue = exitValue > res.getExecState() ? exitValue : res
						.getExecState();
			}

			result.setExitMessage(message);
			result.setLogPath(new File(Constants.TDI_WORK_DIR + Constants.FS
					+ commonLog).getAbsolutePath());
			result.setExecState(exitValue);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "tdipopulate.severe.backend.exception", e);
			result.setExitMessage(Messages
					.getString("tdipopulate.backend.internal.error"));
			result.setExecState(CommandResultInfo.COMMAND_INTERRUPT);
		}

		if (Constants.OS_WINDOWS.equals(CommonHelper.getPlatformType())) {
			NetStore.stop(environment);
		}
		return result;
	}

	/*
	 * private static String parseResult(String result) { String[] results =
	 * result.split(","); int index; int length;
	 * 
	 * length = results[1].length(); index = results[1].lastIndexOf(' '); String
	 * success = results[1].substring(index, length);
	 * 
	 * length = results[2].length(); index = results[2].lastIndexOf(' '); String
	 * duplicate = results[2].substring(index, length);
	 * 
	 * length = results[3].length(); index = results[3].lastIndexOf(' '); String
	 * failure = results[3].substring(index, length);
	 * 
	 * length = results[4].length(); index = results[4].lastIndexOf(' '); String
	 * lastSuccess = results[4].substring(index, length); return
	 * Messages.getString("tdipopulate.backend.task.result", success, duplicate,
	 * failure, lastSuccess); }
	 */

	public static String getTaskName(String taskId) {
		return Messages.getString("LABEL." + taskId);
	}

	private static Task loadTask(String taskType, Properties uiProp) {
		Task task = null;
		if (TYPE_MARK_MANAMER.equals(taskType))
			task = new MarkManagerTask();

		if (TYPE_FILL_COUNTRY.equals(taskType))
			task = new FillCountryTask();

		if (TYPE_FILL_DEPARTMENT.equals(taskType))
			task = new FillDepartmentTask();

		if (TYPE_FILL_ORGANIZATION.equals(taskType))
			task = new FillOrganizationTask();

		if (TYPE_FILL_EMPLOYEETYPE.equals(taskType))
			task = new FillEmployeeTypeTask();

		if (TYPE_FILL_WORKLOCATOIN.equals(taskType))
			task = new FillWorkLocationTask();

		if (TYPE_POPULATE_DB.equals(taskType))
			task = new PopulateDatabaseTask();

		if (task != null) {
			task.createLog();
			task.prepareEvn(uiProp);
		}
		return task;
	}

	public abstract CommandResultInfo run();

	public static void setValues(Properties values) {
		if (values != null)
			PropertyMapping.mapProperties(values);
	}

	public static void setSources(Properties sources) {
		if (sources != null)
			PropertyMapping.setFromSourceProperty(sources);
	}

	protected void createLog() {
		if (commonLogFile != null) {
			this.logPath = commonLogFile.getAbsolutePath();
			return;
		}
		File logDir = new File(Constants.LOG_ROOT + FS + LOGROOT);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}

		this.logPath = new File(Constants.LOG_ROOT + FS + LOGROOT + FS
				+ commonLog).getAbsolutePath();
	}

	protected void setPlantform(String OS) {
		osType = OS;
	}

	protected void prepareEvn(Properties uiProp) {
		env = new HashMap<String, String>();
		env.put("TDIPATH", uiProp.getProperty("TDI.dir"));
		env.put("TDI_CS_HOST", "localhost");
		env.put("TDI_CS_PORT", "1527");
		env.put("TDI_SOLDIR", ".");
	}
}
