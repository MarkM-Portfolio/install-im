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
package com.ibm.lconn.wizard.common.code.parser.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.ibm.lconn.wizard.common.code.parser.Parser;
import com.ibm.lconn.wizard.common.logging.LogUtil;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class JSParser implements Parser {
	public static final Logger logger = LogUtil.getLogger(Parser.class);
	
	private Map<String, String> map = new HashMap<String, String>();

	public Map<String, String> getFunctionList(byte[] bytes) {
		logger.log(Level.INFO,"common.code.parser.entry");
		Queue<Element> queue = transferElementQueue(bytes);
		parse(queue);
		logger.log(Level.INFO,"common.code.parser.leave");
		return this.map;
	}

	private Queue<Element> transferElementQueue(byte[] bytes) {
		int size = bytes.length;
		Queue<Element> elemQue = new LinkedList<Element>();
		String element = "";
		int elemType = Element.START;
		for (int i = 0; i < size; i++) {
			if (this.isWord(bytes[i])) {
				if (elemType == Element.WORD)
					element += (char) bytes[i];
				else {
					element = "" + (char) bytes[i];
					elemType = Element.WORD;
				}
			} else {
				if (elemType == Element.WORD)
					elemQue.add(new Element(elemType, element));
			}
			if (this.isBraceleft(bytes[i])) {
				element = "" + (char) bytes[i];
				elemType = Element.BRACELEFT;
				elemQue.add(new Element(elemType, element));
			}
			if (this.isBraceright(bytes[i])) {
				element = "" + (char) bytes[i];
				elemType = Element.BRACERIGHT;
				elemQue.add(new Element(elemType, element));
			}
			if (this.isOther(bytes[i])) {
				element = "" + (char) bytes[i];
				elemType = Element.OTHER;
				elemQue.add(new Element(elemType, element));
			}
			if (this.isParenleft(bytes[i])) {
				element = "" + (char) bytes[i];
				elemType = Element.PARENLEFT;
				elemQue.add(new Element(elemType, element));
			}
		}

		return elemQue;
	}

	private void parse(Queue<Element> queue) {
		Stack<Element> paren_stack = new Stack<Element>();
		Stack<Element> brace_stack = new Stack<Element>();
		int braceleft_in_stack = 0;
		int parenleft_in_stack = 0;
		Element currentElem;
		Element nextElem;
		String funcName = "";
		String funcBody = "";
		while ((currentElem = queue.poll()) != null) {
			nextElem = queue.peek();
			if (currentElem.type == Element.WORD) {
				funcBody += currentElem.content;
				while (nextElem.content.getBytes()[0] == Constants.SPACE
						|| nextElem.content.getBytes()[0] == Constants.CR
						|| nextElem.content.getBytes()[0] == Constants.LF) {
					funcBody += nextElem.content;
					queue.poll();
					nextElem = queue.peek();
				}
				if (braceleft_in_stack == 0
						&& nextElem.type == Element.PARENLEFT) {
					funcName = currentElem.content;
				}
			}
			if (currentElem.type == Element.OTHER) {
				funcBody += currentElem.content;
			}
			if (currentElem.type == Element.BRACELEFT) {
				brace_stack.push(currentElem);
				braceleft_in_stack++;
				funcBody += currentElem.content;
			}
			if (currentElem.type == Element.BRACERIGHT) {
				brace_stack.pop();
				braceleft_in_stack--;
				funcBody += currentElem.content;
				if (braceleft_in_stack == 0) {
					map.put(funcName, funcBody);
					funcBody = "";
				}
			}
			if (currentElem.type == Element.PARENLEFT) {
				paren_stack.push(currentElem);
				parenleft_in_stack++;
				funcBody += currentElem.content;
			}
		}
	}

	private boolean isWord(byte b) {
		if ((48 <= b && b <= 57) || b == Constants.UNDERSCORE
				|| (65 <= b && b <= 90) || (97 <= b && b <= 122))
			return true;
		else
			return false;
	}

	private boolean isOther(byte b) {
		if (!this.isBraceleft(b) && !this.isBraceright(b)
				&& !this.isParenleft(b) && !this.isWord(b))
			return true;
		else
			return false;
	}

	private boolean isParenleft(byte b) {
		if (b == Constants.PARENLEFT)
			return true;
		else
			return false;
	}

	private boolean isBraceleft(byte b) {
		if (b == Constants.BRACELEFT)
			return true;
		else
			return false;
	}

	private boolean isBraceright(byte b) {
		if (b == Constants.BRACERIGHT)
			return true;
		else
			return false;
	}

	public static void main(String[] a) {
		File fl = new File("workArea/test.js");
		InputStream is;
		try {
			is = new FileInputStream(fl);
			int available = is.available();
			byte[] arr = new byte[available];
			is.read(arr);

			Parser f = new JSParser();
			Map<String, String> m = f.getFunctionList(arr);
			Iterator<String> it = m.keySet().iterator();

			while (it.hasNext()) {
				String name = it.next();
				System.out.println(name);
				System.out.println(m.get(name));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
