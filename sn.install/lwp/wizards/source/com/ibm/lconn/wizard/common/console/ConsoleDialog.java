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
package com.ibm.lconn.wizard.common.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.ibm.lconn.wizard.common.ui.CommonHelper;

public class ConsoleDialog {
	/**
	 * [0] OK
	 */
	private static final String BUTTON_FORMAT = "[{0}] {1}";
	/**
	 * [ IMAGE | TITLE ]
	 */
	private static final String TITLE_PATTERN = "[ {0} | {1} ]";
	/*
	 * [0]
	 */
	private static final String BUTTON_DEFAULT_FORMAT = "[{0}]";
	private String title, message;
	private String[] buttonLabels;
	private String image = "";
	private String[] dialogButtonLabels;
	private int defaultButtonIndex;
	private PrintStream out = System.out;
	private BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	public ConsoleDialog(String title, String message, int dialogImageType,
			String[] dialogButtonLabels, int defaultButtonIndex) {
		this.title = title;
		this.message = message;
		this.image = getImage(dialogImageType);
		this.dialogButtonLabels = dialogButtonLabels;
		this.defaultButtonIndex = defaultButtonIndex;
	}

	private String getImage(int dialogImageType) {
		switch (dialogImageType) {
		case MessageDialog.ERROR: {
			this.image = getErrorImage();
			break;
		}
		case MessageDialog.INFORMATION: {
			this.image = getInfoImage();
			break;
		}
		case MessageDialog.QUESTION: {
			this.image = getQuestionImage();
			break;
		}
		case MessageDialog.WARNING: {
			this.image = getWarningImage();
			break;
		}
		}
		return "MESSAGE";
	}

	public String getWarningImage() {
		return "WARNING";
	}

	public String getQuestionImage() {
		return "QUESTION";
	}

	public String getInfoImage() {
		return "INFORMATION";
	}

	public String getErrorImage() {
		return "ERROR";
	}

	public int open() {
		printTitle();
		printBody();
		printButton(out);
		String input;
		try {
			input = readLine();
			if (CommonHelper.equals("", input)) {
				return (defaultButtonIndex == 0 ? Window.OK : Window.CANCEL);
			} else {
				try {
					int a = Integer.parseInt(input);
					if (a > 0 && a < buttonLabels.length) {
						return (a == 0 ? Window.OK : Window.CANCEL);
					} else {
						return open();
					}
				} catch (Exception e) {
					return open();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		}
		return Window.CANCEL;

	}

	private void printBody() {
		out.println(message);
		out.println();
	}

	private void printTitle() {
		out.println(MessageFormat.format(TITLE_PATTERN, image, title));
		out.println();
	}

	private String readLine() throws IOException {
		return in.readLine();
	}

	private void printButton(PrintStream out) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dialogButtonLabels.length; i++) {
			sb.append(MessageFormat.format(BUTTON_FORMAT, i,
					dialogButtonLabels[i]));
			sb.append(", ");
		}
		sb.setLength(sb.length() - 2);
		sb.append(MessageFormat.format(BUTTON_DEFAULT_FORMAT, defaultButtonIndex));
		out.print(sb.toString());
	}

}
