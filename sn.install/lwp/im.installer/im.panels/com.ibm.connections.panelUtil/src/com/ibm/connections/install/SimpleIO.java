/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleIO {
	String userInput = null;

	public boolean getUserInput(String defaultInput) {
		userInput = null;
		System.out.print("----->");
		if (defaultInput != null && defaultInput.length() != 0)
			System.out.print("[" + defaultInput + "]");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			userInput = br.readLine();
			//block any null input
			if (userInput == null) {
				return false;
			}
			userInput = userInput.trim();
			if (userInput.length() == 0 && defaultInput != null)
				userInput = defaultInput;
			else if (userInput.length() == 0)
				return false;
		} catch (IOException e) {
			TextCustomPanelUtils.logInput("error", e.getMessage());
			System.exit(0);
		}
		return true;
	}

	/** get any user input */
	public void getInputNull() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			br.readLine();
		} catch (IOException e) {
			TextCustomPanelUtils.logInput("error", e.getMessage());
			System.exit(0);
		}
	}

	public String getString() {
		return userInput;
	}

	public void display(String out) {
		System.out.println(out);
	}

	public void display(String format, int num) {
		System.out.printf(format, num);
	}

	public void display(String format, String text) {
		System.out.printf(format, text);
	}
}