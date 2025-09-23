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
package com.ibm.lconn.common.task;

import java.util.Iterator;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class Arg implements Iterator<String> {
	private static final String DEFAULT_DELIM = " ";
	private String key;
	private String value;
	private String delim = DEFAULT_DELIM;

	public Arg(String arg) {
		setArg(arg, delim);
	}
	
	public Arg(String arg, String delim){
		setDelim(delim);
		setArg(arg, delim);
	}

	private void setArg(String arg, String delim) {
		arg = arg.trim();
		int delimIndex = arg.indexOf(delim);
		if (delimIndex <= 0) {
			setKey(null);
			setValue(arg);
		} else {
			setKey(arg.substring(0, delimIndex));
			setValue(arg.substring(delimIndex + delim.length()));
		}
	}

	private void setArg(String arg) {
		setArg(arg, delim);
	}

	public void setKey(String substring) {
		this.key = (substring == null ? "" : substring.trim());
	}

	public void setValue(String arg) {
		this.value = (arg == null ? "" : arg.trim());
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return toString(delim);
	}

	public String toString(String delim) {
		return key + delim + value;
	}

	public String getDelim() {
		return delim;
	}

	public void setDelim(String delim) {
		this.delim = delim;
	}

	public String nextArg() {
		if (getKey().equals("")){
			if (getValue().equals(""))
				return null;
			String v = getValue();
			setValue(null);
			return v;
		}
		String re = getKey();
		setArg(getValue());
		return re;
	}

	public static void main(String[] args) {
		String a = "a,b,c,d,e,";
		Arg g = new Arg(a);
		String nextArg = g.nextArg();
		while (nextArg != null) {
			System.out.println(nextArg);
			nextArg = g.nextArg();
		}
	}

	public boolean hasNext() {
		return !(getKey().equals("") && getValue().equals(""));
	}

	public String next() {
		return nextArg();
	}

	public void remove() {
		return;
	}

}
