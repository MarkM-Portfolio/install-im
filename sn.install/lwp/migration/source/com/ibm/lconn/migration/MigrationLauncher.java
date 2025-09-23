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
package com.ibm.lconn.migration;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;

import com.ibm.lconn.common.feature.LCInfo;
import com.ibm.lconn.common.file.FileUtil;
import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.operator.OperatorFactory;
import com.ibm.lconn.common.output.Output;
import com.ibm.lconn.common.util.ObjectUtil;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class MigrationLauncher extends LogOperator {

	private static final String TASK_GENERAL = "migration overall";
	public static final String MIGRATION_BASE = "migrationData";
	// private static final String COM_IBM_LCONN_MIGRATION_BACKUP_20_FILTER =
	// "/com/ibm/lconn/migration/backup_20.filter";//$NON-NLS-1$

	private static final String EXPORT102 = "export102"; //$NON-NLS-1$
	private static final String IMPORT20 = "import20"; //$NON-NLS-1$
	private static final String IMPORT201 = "import201"; //$NON-NLS-1$
	private static final String IMPORT2_0BETA = "import20beta";
	private static final String IMPORT2_0BETA2 = "import20beta2";
	private static final String EXPORT20 = "export20"; //$NON-NLS-1$
	private static final String EXPORT201= "export201"; //$NON-NLS-1$
	private static final String EXPORT20Beta1 = "export20beta1"; //$NON-NLS-1$
	private static final String COM_IBM_LCONN_MIGRATION_IMPORT102_20_BETA_FILTER = "/com/ibm/lconn/migration/import102_20beta.filter";//$NON-NLS-1$
	private static final String COM_IBM_LCONN_MIGRATION_IMPORT102_20_FILTER = "/com/ibm/lconn/migration/import102_20.filter";//$NON-NLS-1$
	private static final String COM_IBM_LCONN_MIGRATION_IMPORT201_FILTER = "/com/ibm/lconn/migration/import201.filter";//$NON-NLS-1$
	private static final String COM_IBM_LCONN_MIGRATION_IMPORT20Beta2_FILTER = "/com/ibm/lconn/migration/import20beta2.filter";//$NON-NLS-1$

	private static final String COM_IBM_LCONN_MIGRATION_EXPORT102_FILTER = "/com/ibm/lconn/migration/export102.filter";//$NON-NLS-1$
	private static final String COM_IBM_LCONN_MIGRATION_EXPORT20_FILTER = "/com/ibm/lconn/migration/export20.filter";//$NON-NLS-1$
	private static final String COM_IBM_LCONN_MIGRATION_EXPORT20Beta1_FILTER = "/com/ibm/lconn/migration/export20beta1.filter";//$NON-NLS-1$

	private static final String MIGRATION_RESULT_FAIL = "Failed to execute the migration task with input: \n\tlc_home={0}\n\ttype={1}\n";
	private static final String MIGRATION_RESULT_SUCCEED = "Succeeded to execute the migration task with input: \n\tlc_home={0}\n\ttype={1}\n";

	private ArrayList<String> errorList = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		MigrationLauncher launcher = new MigrationLauncher();
		launcher.execute(args);
	}

	private void execute(String[] args) throws IOException {
		if (args.length != 2) {
			help();
		}
		String lcHome = args[0];
		String type = args[1];

		boolean isImport = isImport(type);

		LCInfo lc = new LCInfo(lcHome);
		if (!lc.isValidLCHome()) {
			System.out.println(MessageFormat.format(MigrationMessages
					.getString("MigrationLauncher.lc.home.invalid"), //$NON-NLS-1$
					lcHome));
			exit(-1);
		}
		if (isImport) {
			if (!new File(MIGRATION_BASE).isDirectory()) { //$NON-NLS-1$
				System.out
						.println(MigrationMessages
								.getString("MigrationLauncher.error.should.run.export.first")); //$NON-NLS-1$
				exit(-1);
			}
		}
		execute(lcHome, type);
		if(!isImport(type)) 
			writeMigrationInfo(lcHome, type);
		int returnCode = 0;
		if (errorList.size() > 0) {

			String msg = MessageFormat.format(MIGRATION_RESULT_FAIL, lcHome,
					type);
			System.out.println(msg);
			for (int i = 0; i < errorList.size(); i++) {
				System.out.println("\t" + errorList);
			}
			returnCode = 2;
		} else {
			String msg = MessageFormat.format(MIGRATION_RESULT_SUCCEED, lcHome,
					type);
			System.out.println(msg);
		}
		exit(returnCode);
	}

	private boolean isImport(String type) {
		return IMPORT20.equals(type) || IMPORT2_0BETA.equals(type)
				|| IMPORT2_0BETA2.equals(type) || IMPORT201.equals(type);
	}

	private void writeMigrationInfo(String lcHome, String type) {
		Properties props = new Properties();
		String exportVersion = getExportVersion(type);
		props.setProperty("migration.export.version", exportVersion);

		try {
			PrintStream ps = new PrintStream(new File(MIGRATION_BASE,
					"migration.properties"));
			props.store(ps, null);
			ps.flush();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getExportVersion(String type) {
		String exportVersion = "UNKNOWN";
		if (EXPORT102.equals(type))
			exportVersion = "1.0.2";
		else if (EXPORT20.equals(type))
			exportVersion = "2.0.0";
		else if (EXPORT201.equals(type))
			exportVersion = "2.0.1";
		else if (EXPORT20Beta1.equals(type))
			exportVersion = "2.0.0beta1";
		return exportVersion;
	}

	private void exit(int i) {
		System.exit(i);
	}

	private void help() {
		System.out.println(MigrationMessages
				.getString("MigrationLauncher.usage")); //$NON-NLS-1$
		exit(2);
	}

	private void execute(String lcHome, String type) throws IOException {
		OperatorFactory operatorFactory = setLCProperties(lcHome, type);
		operatorFactory.setOutput(new Output() {
			public void append(String str) throws IOException {
				appendLog(str);
			}
		});
		if(isImport(type)){
			Properties loadProperties = ObjectUtil.loadProperties(new File(MIGRATION_BASE, "migration.properties").getAbsolutePath());
			ObjectUtil.copyProperties(loadProperties, operatorFactory.getMacroProperties(), "", "");
		}
		if (EXPORT102.equals(type)) {
			operatorFactory
					.executeFromResource(COM_IBM_LCONN_MIGRATION_EXPORT102_FILTER); //$NON-NLS-1$
			return;
		}
		if (EXPORT201.equals(type) || EXPORT20.equals(type)) {
			operatorFactory
					.executeFromResource(COM_IBM_LCONN_MIGRATION_EXPORT20_FILTER); //$NON-NLS-1$
			return;
		}
		if (EXPORT20Beta1.equals(type)) {
			operatorFactory
					.executeFromResource(COM_IBM_LCONN_MIGRATION_EXPORT20Beta1_FILTER); //$NON-NLS-1$
			return;
		}
		if (IMPORT20.equals(type)) {
			operatorFactory
					.executeFromResource(COM_IBM_LCONN_MIGRATION_IMPORT102_20_FILTER); //$NON-NLS-1$
		}
		if (IMPORT201.equals(type)) {
			operatorFactory
					.executeFromResource(COM_IBM_LCONN_MIGRATION_IMPORT201_FILTER); //$NON-NLS-1$
		}
		if (IMPORT2_0BETA.equals(type)) {
			operatorFactory
					.executeFromResource(COM_IBM_LCONN_MIGRATION_IMPORT102_20_BETA_FILTER); //$NON-NLS-1$
		}
		if (IMPORT2_0BETA2.equals(type)) {
			operatorFactory
					.executeFromResource(COM_IBM_LCONN_MIGRATION_IMPORT20Beta2_FILTER); //$NON-NLS-1$
		}
		
	}

	private OperatorFactory setLCProperties(String lc_home, String type) {
		lc_home = FileUtil.getAbsoluteFile(lc_home).getAbsolutePath();
		LCInfo lc = new LCInfo(lc_home);
		Properties props = lc.getLCProperties();
		this.setMacroProperties(props);
		OperatorFactory operatorFactory = new OperatorFactory();
		Properties env = MigrationPrepare.buildEnv();
		ObjectUtil.copyProperties(env, props, "", "");
		props.setProperty("migration.export.version", getExportVersion(type));
		operatorFactory.setMacroProperties(props);
		return operatorFactory;
	}

	private void appendLog(String msg) {
		System.out.append(msg);
		System.out.flush();
		String str = msg.toLowerCase();
		if (str.indexOf("fail") != -1 || str.indexOf("error") != -1
				|| str.indexOf("exception") != -1) {
			String currentTask = getCurrentTask();
			String errMsg = MessageFormat
					.format(
							"There''s some error during task ({0}), the message is: {1}",
							currentTask, msg);
			errorList.add(errMsg);
		}
	}

	@Override
	public String getCurrentTask() {
		String currentTask = super.getCurrentTask();
		if (getClass().getName().equals(currentTask)) {
			currentTask = TASK_GENERAL;
		}
		return currentTask;
	}
}
