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

package com.ibm.lconn.wizard.cluster.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ibm.lconn.wizard.cluster.backend.CommandExec;
import com.ibm.lconn.wizard.cluster.ui.ClusterConstant;
import com.ibm.lconn.wizard.common.Constants;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class LocalProfileDetector extends AbstractDetector {
	private static String output = Constants.OUTPUT_ROOT + Constants.FS
			+ "localProfiles.txt";

	public String[] getResult() {
		CommandExec ce = CommandExec.create(ClusterConstant.TASK_LIST_PROFILES);
		if (ce.execute() != 0)
			return null;

		File outputFile = new File(output);
		if (!outputFile.exists())
			return null;

		try {
			List<String> list = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(outputFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (Pattern.compile("\\[.*\\]").matcher(line).matches()) {
					int length = line.length();
					line = line.substring(1, length - 1);
					String[] temp = line.split(",");
					for (String s : temp)
						list.add(s.trim());
					break;
				}
			}
			reader.close();
			if (list.size() == 0)
				return null;
			return list.toArray(new String[0]);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
