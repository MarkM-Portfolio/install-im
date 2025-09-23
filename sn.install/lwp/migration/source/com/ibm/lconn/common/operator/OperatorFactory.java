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
package com.ibm.lconn.common.operator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Properties;

import com.ibm.lconn.common.file.FileOperator;
import com.ibm.lconn.common.output.Output;
import com.ibm.lconn.common.task.Arg;
import com.ibm.lconn.common.util.ObjectUtil;
import com.ibm.lconn.common.util.Spliter;
import com.ibm.lconn.common.util.StringResolver;
import com.ibm.lconn.common.was.WasVariableLoader;
import com.ibm.lconn.common.xml.XMLOperator;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class OperatorFactory extends LogOperator {
	private static final String CONDITION = "CONDITION";
	private static final String LOAD_WAS_VARIABLE = "LOAD_WAS_VARIABLE";
	private static final String LOG = "LOG";
	private static final String LOAD_PROPERTY = "LOAD_PROPERTY";
	private static final String CLASS_LOAD_PROPERTY = "com.ibm.lconn.common.operator.PropertyLoadOperator";
	private static final Object SET_TASK = "SET_TASK";
	private static final Object CONDITION_EXEC = "CONDITION_EXEC";
	private FileOperator op = new FileOperator();
	private XMLOperator xml = new XMLOperator();
	private WasVariableLoader wasLoader = new WasVariableLoader();

	private final String EXPORT_IMPORT = "EXPORT_IMPORT";
	private final String FILE_COPY = "FILE_COPY";
	private final String REPLACE_ELEMENT = "REPLACE_ELEMENT";
	private final String CLASS = "CLASS";

//	private Properties macroProps = new Properties();
	private boolean enable = true;

	public OperatorFactory() {
		setOutput(System.out);
	}

	public void setOutput(Output output) {
		op.setOutput(output);
		xml.setOutput(output);
		super.setOutput(output);
		wasLoader.setOutput(output);
	}

	public int execute(String id, String... para) {
		int len = para.length;
		if (CONDITION.equals(id)) {
			eval(para);
			return 0;
		}
		if (!enable)
			return 0;
		if(CONDITION_EXEC.equals(id)){
			Spliter sp = new Spliter(para[0]);
			String props = sp.getHeader();
			String val = sp.takeTail().getHeader();
			if(eval(new String[] {props + " " + val})){
				String line = sp.takeTail().getSource();
				return execute(line)? 0: -1;
			}
			return 0;
			
		}
		if(SET_TASK.equals(id)){
			this.setCurrentTask(para[0]);
		}
		if (FILE_COPY.equals(id)) {
			return copyFile(id, len, para);
		}

		if (LOG.equals(id)) {
			log(para[0]);
			return 0;
		}

		if (REPLACE_ELEMENT.equals(id)) {
			return replaceElement(id, len, para);
		}

		if (EXPORT_IMPORT.equals(id)) {
			return exportImport(id, len, para);
		}

		if (LOAD_PROPERTY.equals(id)) {
			boolean result = executeClass(CLASS_LOAD_PROPERTY, para[0]);
			if (result)
				return 0;
			else
				return -1;
		}

		if (LOAD_WAS_VARIABLE.equals(id)) {
			return loadWasVariable(id, len, para);
		}
		return -1;
	}

	private int loadWasVariable(String id, int len, String[] para) {
		try {
			switch (len) {
			case 1:
				Arg arg = new Arg(para[0]);
				return execute(id, arg.next(), arg.next(), arg.next(), arg
						.next(), arg.next());
			case 5:
				try {
					resolvePara(len, para);
					Properties wasVariables = wasLoader.loadWasVariables(
							para[0], para[1], para[2], para[3], para[4]);
					ObjectUtil.copyProperties(wasVariables, getMacroProperties(), null,
							null);
					return 0;
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private boolean eval(String[] para) {
		if (para.length < 2 && "NULL".equals(para[0])) {
			enable = true;
			return true;
		}
		Arg arg = new Arg(para[0]);
		String key = arg.next();
		String value = arg.next();
		if ("NULL".equals(key))
			enable = true;
		else {
			if (enable) {
				enable = ObjectUtil.equals(getMacroProperties().get(key), value);
			}
		}
		return enable;
	}

	private int exportImport(String id, int len, String... para) {
		try {
			switch (len) {
			case 6:
				resolvePara(len, para);
				xml.execSetValueTask(para[0], para[1], para[2], para[3],
						para[4], para[5]);
				return 0;
			case 1:
				Arg arg = new Arg(para[0]);
				execute(id, arg.next(), arg.next(), arg.next(), arg.next(), arg
						.next(), arg.next());
			default:
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private int replaceElement(String id, int len, String... para) {
		try {
			switch (len) {
			case 5:
				resolvePara(len, para);
				xml.execReplaceElements(para[0], para[1], para[2], para[3],
						para[4]);
				return 0;
			case 4:
				execute(id, para[0], para[1], para[2], para[3], ",");
				return 0;
			case 1:
				Arg arg = new Arg(para[0]);
				String source = arg.next();
				String dest = arg.next();
				while (arg.hasNext()) {
					String sourceXPath = arg.next();
					if (arg.hasNext()) {
						String destXPath = arg.next();
						String delim = arg.next();
						if (delim == null)
							delim = ",";
						execute(id, source, dest, sourceXPath, destXPath, delim);
					}
				}
				return 0;

			default:
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private int copyFile(String id, int len, String... para) {
		switch (len) {
		case 4:
			if (para[3] == null || para[3].equals("")) {
				return execute(id, para[0], para[1], para[2]);
			}
			resolvePara(len, para);
			return op.copy(para[0], para[1], ObjectUtil.evalBool(para[2]),
					ObjectUtil.evalBool(para[3]));
		case 3:
			if (para[2] == null || para[2].equals(""))
				return execute(id, para[0], para[1]);
			resolvePara(len, para);
			return op.copy(para[0], para[1], ObjectUtil.evalBool(para[2]),
					false);
		case 2:
			resolvePara(len, para);
			return op.copy(para[0], para[1], true, false);
		case 1:
			Arg arg = new Arg(para[0], ">>");
			return execute(id, arg.next(), arg.next(), arg.next(), arg.next());
		default:
			return -1;
		}
	}

	private void resolvePara(int len, String... para) {
		for (int i = 0; i < len; i++) {
			if (para[i] == null)
				continue;
			Properties macroProps = getMacroProperties();
			if (macroProps  != null) {
				para[i] = StringResolver.resolveMacro(para[i], macroProps);
			}
		}
	}

	public boolean execute(String line) {
		Spliter s = new Spliter(line);
		String key = s.getHeader();
		String para = s.getTail();
		if (CLASS.equals(key)) {
			try {
				s.takeTail();
				String className = s.getHeader();
				para = s.getTail();
				return executeClass(className, para);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return 0 == execute(key, para);
		}
	}

	public boolean executeClass(String className, String para) {
		try {
			Object newInstance = ObjectUtil.loadObject(className);
			BaseOperator operator = (BaseOperator) newInstance;
			operator.setOutput(getOutput());
			operator.setMacroProperties(getMacroProperties());
			return operator.execute(para);
		} catch (Exception e) {
			log("Execute class {0} fail with parameter {1}, exception message {2}", className, para, e.getMessage());
			return false;
		}
	}

	public void executeFromFile(String filepath) throws IOException {
		execute(new FileInputStream(filepath));
	}

	public void executeFromResource(String path) throws IOException {
		InputStream resourceAsStream = getClass().getResourceAsStream(path);
		execute(resourceAsStream);
	}

	public void execute(InputStream resourceAsStream) throws IOException {
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
				resourceAsStream));
		String line = lnr.readLine();
		while (line != null) {
			execute(line);
			line = lnr.readLine();
		}
	}
	

}
