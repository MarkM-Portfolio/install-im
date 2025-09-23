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
package com.ibm.lconn.wizard.common.command;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;
import com.ibm.lconn.wizard.common.DataPool;
import com.ibm.lconn.wizard.common.ui.CommonHelper;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class CommandGenerator {
	private static ResourceBundle rc = ResourceBundle
			.getBundle("com.ibm.lconn.wizard.cluster.command.format");

	@SuppressWarnings("unchecked")
	public static List<String> gen(String wizard_id, String task_id) {
		List<String> command;
		String postfix;
		String os = CommonHelper.getPlatformType();
		if (Constants.OS_WINDOWS.equals(os)) {
			postfix = "win";
		} else if (Constants.OS_AIX.equals(os)) {
			postfix = "aix";
		} else {
			postfix = "lin";
		}
		String rawComm = rc.getString(task_id + ".formation." + postfix);

		Object[] values = new Object[0];
		if (containKey(rc.getKeys(), task_id + ".variable." + postfix)) {
			String rawVariables = rc
					.getString(task_id + ".variable." + postfix);

			if (ClusterConstant.INPUT_clusterCommandVariables
					.equals(rawVariables.trim())) {
				List<String> variables = (List<String>) DataPool
						.getComplexData(ClusterConstant.WIZARD_ID_CLUSTER,
								ClusterConstant.INPUT_clusterCommandVariables);
				values = new String[variables.size()];
				int i = 0;
				for (String v : variables) {
					values[i] = v;
					i++;
				}
			} else {

				String[] vars = rawVariables.split(",");
				int length = vars.length;
				values = new String[length];
				for (int i = 0; i < length; i++) {
					String VAR = vars[i].trim();

					//values[i] = DataPoolForTest.getValue(VAR);

					values[i] = DataPool.getValue(
							ClusterConstant.WIZARD_ID_CLUSTER, VAR);

				}
			}
		}

		String[] commArr = rawComm.split(",");
		command = new ArrayList<String>();

		for (String str : commArr) {
			str = str.trim();
			if (!"".equals(str)) {
				String cmdStr = MessageFormat.format(str, values);
				command.add(cmdStr);
			}
		}

		return command;
	}

	private static boolean containKey(Enumeration<String> enumration, String key) {
		while (enumration.hasMoreElements()) {
			String k = enumration.nextElement();
			if (key.equals(k))
				return true;
		}

		return false;
	}
}
