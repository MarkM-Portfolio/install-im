/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.util.List;
import java.util.Map;
import com.ibm.cic.agent.core.api.ILogger;

public class TextCustomPanelUtils {
	private static final SimpleIO io = new SimpleIO();

	public static void showTitle(String commonConfig, String curConfig) {
		showText("\n" + commonConfig + " > " + curConfig + "\n");
	}

	public static void showNotice(String notice) {
		showText("\n**** " + notice + " ****");
	}

	public static void showSubTitle1(String subtitle) {
		showText("\n===== " + subtitle + " =====");
	}

	public static void showSubTitle2(String subtitle2) {
		showText("\n=== " + subtitle2 + " ===");
	}

	public static void showSubTitle3(String subtitle3) {
		showText("\n= " + subtitle3 + " =");
	}

	public static void showText(String text) {
		io.display(text);
	}

	public static void showProgress(String curProgress) {
		showText(curProgress + "...");
	}

	public static void showWarning(String text) {
		showText("Warning: " + text);
	}

	public static void showError(String text) {
		showText("Error: " + text);
	}

	public static void showErrorList(List<String> text) {
		for (int i = 0; i < text.size(); i++)
			showError(text.get(i));
	}

	/** receive input with any value */
	public static String getInput(String title, String defaultValue) {
		showText("\n" + title);
		io.getUserInput(defaultValue);
		if (io.getString() == null || io.getString().trim().length() == 0) {
			showError(Messages.INVALID_INPUT_ERROR);
			return getInput(title, defaultValue);
		}
		return io.getString().trim();
	}

	public static String getInputWithoutValidation(String title, String defaultValue) {
		showText("\n" + title);
		io.getUserInput(defaultValue);
		return io.getString();
	}
	/** get user 'enter' */
	public static void getInputNull(String title) {
		showText("\n" + title);
		io.getInputNull();
	}
	
	public static void showHaltMessage(String title) {
		showText("\n" + title);
	}

	/** receive input with the specified accept values */
	public static String getInput(String title, String defaultValue,
			String[] acceptValues) {
		showText("\n" + title);
		io.getUserInput(defaultValue);
		for (int i = 0; i < acceptValues.length; i++) {
			if (io.getString() == null)
				return "";
			if (io.getString().trim().equalsIgnoreCase(acceptValues[i]))
				return io.getString().trim();
		}
		showError(Messages.INVALID_INPUT_ERROR);
		return getInput(title, defaultValue, acceptValues);
	}

	/**
	 * Receive a numeric value within the range of options or a text value
	 * matching extra options. <br>
	 * Receive extra options beyond the options list with <i>otherOpt</i>. <br>
	 * If input is within range of the normal options: return the chosen index; <br>
	 * If input matches one of the extra options: return-(index+1) of that
	 * option. <br>
	 * Display extra information <i>otherInfo</i> between the title and the
	 * options
	 */
	public static int singleSelect(String title, String[] options,
			int defaultOption, String[] otherOpt, String otherInfo) {
		showText("\n" + title);
		for (int i = 0; i < options.length; i++) {
			io.display("%2d. " + options[i] + "\n", i + 1);
		}
		// display other information below the options
		showText(Messages.SINGLE_CHOICE_INFO);
		if (otherInfo != null)
			showText(Messages.NOTE + otherInfo);
		if (defaultOption != 0)
			io.getUserInput(defaultOption + "");
		else
			io.getUserInput(null);
		String curInput = io.getString();
		if (otherOpt != null)
			for (int i = 0; i < otherOpt.length; i++) {
				if (curInput != null
						&& curInput.trim().equalsIgnoreCase(otherOpt[i]))
					return -(i + 1);
			}
		if (curInput == null || curInput.trim().length() == 0
				|| !isInt(curInput) || !inRange(curInput, options.length)) {
			showError(Messages.INVALID_INPUT_ERROR);
			return singleSelect(title, options, defaultOption, otherOpt,
					otherInfo);
		}
		return Integer.parseInt(curInput.trim());
	}

	/**
	 * Receive a numeric value within the range of options. Receive a text value
	 * matching extra options with <i>otherOpt</i>. <br>
	 * Maintain an option list with values like {0,1,1,0}. '1' means the option
	 * at that position is selected, and '0' means the option at that position
	 * is not selected. <br>
	 * If input is within the range of the normal options: update the option
	 * list accordingly; <br>
	 * If input matches one of the extra options: return-(index+1) of that
	 * option. <br>
	 * If user inputs nothing: user has confirmed his/her multiple selection and
	 * the function will return the option list with user¡¯s options
	 */
	public static int[] multiSelect(String title, String[] options,
			int[] defaultOpts, String[] otherOpt, String otherInfo) {
		showText("\n" + title);
		int[] defaults = defaultOpts;
		if (defaults == null) {
			defaults = new int[options.length];
			for (int i = 0; i < defaults.length; i++) {
				defaults[i] = 0;
			}
		}
		for (int i = 0; i < options.length; i++) {
			String checked = "[ ]";
			if (defaults[i] == 1)
				checked = "[X]";
			io.display("%2d. " + checked + options[i] + "\n", i + 1);
		}
		showText(Messages.MULTI_CHOICE_INFO);
		if (otherInfo != null)
			showText(otherInfo);
		io.getUserInput(null);
		String curInput = io.getString();
		if (curInput == null || curInput.trim().length() == 0)
			return null;
		if (otherOpt != null)
			for (int i = 0; i < otherOpt.length; i++) {
				if (curInput.trim().equalsIgnoreCase(otherOpt[i])) {
					return new int[] { -(i + 1) };
				}
			}
		if (!isInt(curInput) || !inRange(curInput, options.length)) {
			showError(Messages.INVALID_INPUT_ERROR);
			return multiSelect(title, options, defaults, otherOpt, otherInfo);
		}
		int selection = Integer.parseInt(curInput.trim()) - 1;
		int[] returnOpts = defaults;
		returnOpts[selection] = returnOpts[selection] == 0 ? 1 : 0;
		return returnOpts;
	}

	/** Receive 'Y' or 'N' or any other input within otherOpt. */
	public static String showYorN(String title, int defaultOption,
			String[] otherOpt) {
		String cur = defaultOption == 0 ? "Y" : "N";
		showText("\n" + title + "(Y/N)");
		io.getUserInput(cur);
		if (io.getString() == null)
			return showYorN(title, defaultOption, otherOpt);
		cur = io.getString().trim().toUpperCase();
		if (cur.equals("Y"))
			return "Y";
		else if (cur.equals("N"))
			return "N";
		else
			for (int i = 0; i < otherOpt.length; i++)
				if (cur.equals(otherOpt[i]))
					return cur;
		showError(Messages.INVALID_INPUT_ERROR);
		return showYorN(title, defaultOption, otherOpt);
	}

	public static void printTitleRow(String[] titles, int[] lengths) {
		StringBuffer line1 = new StringBuffer("|==|");
		for (int i = 0; i < titles.length; i++) {
			for (int j = 0; j < lengths[i]; j++)
				line1.append("=");
			line1.append("|");
		}
		StringBuffer line2 = new StringBuffer("|  |");
		for (int i = 0; i < titles.length; i++) {
			line2.append(titles[i]);
			for (int j = titles[i].length(); j < lengths[i]; j++)
				line2.append(" ");
			line2.append("|");
		}
		showText(line1.toString());
		showText(line2.toString());
		showText(line1.toString());
	}

	/** print a single line of table elements without '-' */
	public static void printSingleLineRowMiddle(int index, String[] items,
			int[] lengths) {
		StringBuffer line1 = new StringBuffer("|");
		String str = index + "|";
		if (index == 0)
			str = "  |";
		if (str.length() == 2)
			str = " " + str;
		line1.append(str);
		for (int i = 0; i < items.length; i++) {
			line1.append(items[i]);
			for (int j = items[i].length(); j < lengths[i]; j++)
				line1.append(" ");
			line1.append("|");
		}
		StringBuffer line2 = new StringBuffer("|  |");
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < lengths[i]; j++)
				line2.append(" ");
			line2.append("|");
		}
		showText(line1.toString());
		showText(line2.toString());
	}

	public static void printSingleLineRow(int index, String[] items,
			int[] lengths) {
		StringBuffer line1 = new StringBuffer("|");
		String str = index + "|";
		if (index == 0)
			str = "  |";
		if (str.length() == 2)
			str = " " + str;
		line1.append(str);
		for (int i = 0; i < items.length; i++) {
			line1.append(items[i]);
			for (int j = items[i].length(); j < lengths[i]; j++)
				line1.append(" ");
			line1.append("|");
		}
		StringBuffer line2 = new StringBuffer("|--|");
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < lengths[i]; j++)
				line2.append("-");
			line2.append("|");
		}
		showText(line1.toString());
		showText(line2.toString());
	}

	/** print a table row without '-' in the bottom */
	public static void printSingleLineRowNoBottom(int index, String[] items,
			int[] lengths) {
		StringBuffer line1 = new StringBuffer("|");
		String str = index + "|";
		if (str.length() == 2)
			str = " " + str;
		line1.append(str);
		for (int i = 0; i < items.length; i++) {
			line1.append(items[i]);
			for (int j = items[i].length(); j < lengths[i]; j++)
				line1.append(" ");
			line1.append("|");
		}
		StringBuffer line2 = new StringBuffer("|  |");
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < lengths[i]; j++)
				line2.append(" ");
			line2.append("|");
		}
		showText(line1.toString());
		showText(line2.toString());
	}

	/** log instance */
	private static ILogger log = null;
	private static String panelName = "";

	/** set the log instance and panel name for log */
	public static void setLogPanel(ILogger instance, String panel) {
		log = instance;
		panelName = panel;
	}

	public static void logInput(String item, String value) {
		log.info(panelName + "::" + item + " = " + value);
	}

	public static void logError(String error) {
		showError(error);
		log.error(panelName + "::" + error);
	}

	public static void logUserData(String item, String value) {
		log.info(panelName + "::SetUserData::" + item + " = " + value);
	}

	/** get the max length of the string elements */
	public static int getMaxLength(String[] strs) {
		int l = strs[0].length();
		for (int i = 1; i < strs.length; i++) {
			if (strs[i] != null && strs[i].length() > l)
				l = strs[i].length();
		}
		return l;
	}

	private static boolean isInt(String input) {
		for (int i = 0; i < input.length(); i++)
			if (input.charAt(i) < '0' || input.charAt(i) > '9')
				return false;
		return true;
	}

	private static boolean inRange(String input, int range) {
		int cur = Integer.parseInt(input);
		if (cur < 1 || cur > range)
			return false;
		return true;
	}

	/** convert password into corresponding '*' */
	public static String convertPassword(String password) {
		if (password == null || password.length() == 0)
			return "";
		String str = "";
		for (int i = 0; i < password.length(); i++)
			str += "*";
		return str;
	}

	public static boolean containsIgnoreCase(List<String> all, String subj) {
		for (String cur : all) {
			if (subj.equalsIgnoreCase(cur))
				return true;
		}
		return false;
	}

	public static boolean containsKeyIgnoreCase(Map<String, ?> all, String subj) {
		for (String cur : all.keySet()) {
			if (cur.equalsIgnoreCase(subj))
				return true;
		}
		return false;
	}

	/**
	 * get value from map ignoring key case. e.g. map.get("a") is equal to
	 * map.get("A")
	 */
	public static Object getValueIgnoreCase(Map<String, ?> all, String key) {
		for (String cur : all.keySet()) {
			if (cur.equalsIgnoreCase(key))
				return all.get(cur);
		}
		return null;
	}

	/** get the original form of the key */
	public static String getOriginalKey(Map<String, ?> all, String key) {
		for (String cur : all.keySet()) {
			if (cur.equalsIgnoreCase(key))
				return cur;
		}
		return key;
	}

}
