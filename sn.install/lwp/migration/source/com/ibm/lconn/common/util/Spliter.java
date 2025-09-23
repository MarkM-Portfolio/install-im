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
package com.ibm.lconn.common.util;

import java.text.MessageFormat;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class Spliter {
	private String header, tail, source;
	private String[] delim;
	private final String[] defaultDelim = { " ", "\t" };

	public Spliter(String source, String... delim) {
		set(source, delim);
	}

	private void set(String source, String... delim) {
		if (delim == null || delim.length == 0)
			delim = defaultDelim;
		this.delim = delim;

		while (true) {
			String s = source;
			for (int i = 0; i < delim.length; i++) {
				source = ObjectUtil.trimFirst(source, delim[i]);
			}
			if (s.equals(source))
				break;
		}
		this.source = source;
		if (ObjectUtil.isEmpty(source)) {
			setHeader("");
			setTail("");
		} else {
			int indexFirst = source.length();
			String delimFirst = null;
			for (int i = 0; i < delim.length; i++) {
				int index = source.indexOf(delim[i]);
				if (index != -1 && index < indexFirst) {
					indexFirst = index;
					delimFirst = delim[i];
				}
			}
			if (indexFirst == source.length()) {
				setHeader(source);
				setTail("");
			} else {
				int tailStart = indexFirst + delimFirst.length();
				setHeader(source.substring(0, indexFirst));
				setTail(source.substring(tailStart));
			}
		}
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}

	public Spliter takeTail(String... delim) {
		set(tail, delim);
		return this;
	}

	public Spliter takeHeader(String... delim) {
		set(header, delim);
		return this;
	}

	public Spliter takeTail() {
		return takeTail(delim);
	}

	public Spliter takeHeader() {
		return takeHeader(delim);
	}

	public String toString() {
		return MessageFormat.format("[{0}, {1}]", getHeader(), getTail());
	}

	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSource() {
		return source;
	}

	// public static void main(String[] args) {
	// String a = " a b c d d e f ";
	// Spliter s = new Spliter(a, " ,\t".split(","));
	// System.out.println(s.takeTail().takeTail().takeTail().takeTail().takeTail());
	// }
}
