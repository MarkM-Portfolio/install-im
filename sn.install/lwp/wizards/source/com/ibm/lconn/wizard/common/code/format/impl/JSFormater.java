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
package com.ibm.lconn.wizard.common.code.format.impl;

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

import com.ibm.lconn.wizard.common.code.format.Formater;

/**
 * @author joey (pengzsh@cn.ibm.com)
 * 
 */
public class JSFormater implements Formater {
	private Map<String, String> map = new HashMap<String, String>();

	public Map<String, String> getFunctionList(byte[] bytes) {
		Queue<Element> queue = transferElementQueue(bytes);
		format(queue);
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
			if (this.isParenright(bytes[i])) {
				element = "" + (char) bytes[i];
				elemType = Element.PARENRIGHT;
				elemQue.add(new Element(elemType, element));
			}
			if (this.isSemicolon(bytes[i])) {
				element = "" + (char) bytes[i];
				elemType = Element.SEMICOLON;
				elemQue.add(new Element(elemType, element));
			}
			if (this.isSpace(bytes[i])) {
				elemType = Element.SPACE;				
			}
		}

		return elemQue;
	}

	private void format(Queue<Element> queue) {
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
				if (braceleft_in_stack == 0
						&& nextElem.type == Element.PARENLEFT) {
					funcName = currentElem.content;
				}
				funcBody += currentElem.content;
				if (nextElem.type == Element.WORD)
					funcBody += (char) Constants.SPACE;
			}
			if (currentElem.type == Element.OTHER) {
				funcBody += currentElem.content;
			}
			if (currentElem.type == Element.BRACELEFT) {
				brace_stack.push(currentElem);
				braceleft_in_stack++;
				funcBody += currentElem.content;
				if (parenleft_in_stack == 0) {
					funcBody += (char)Constants.CR;
					funcBody += (char)Constants.LF;
					int i;
					if (nextElem.type == Element.BRACERIGHT) i = 1;
					else i = 0;
					for (; i < braceleft_in_stack; i++) {
						funcBody += Constants.FSPACE;
					}
				}
			}
			if (currentElem.type == Element.BRACERIGHT) {
				brace_stack.pop();
				braceleft_in_stack--;
				funcBody += currentElem.content;
				funcBody += (char)Constants.CR;
				funcBody += (char)Constants.LF;
				if (braceleft_in_stack == 0) {
					map.put(funcName, funcBody);
					funcBody = "";
				} else {
					int i;
					if (nextElem.type == Element.BRACERIGHT) i = 1;
					else i = 0;
					for (; i < braceleft_in_stack; i++) {
						funcBody += Constants.FSPACE;
					}
				}
			}
			if (currentElem.type == Element.PARENLEFT) {
				paren_stack.push(currentElem);
				parenleft_in_stack++;
				funcBody += currentElem.content;
			}
			if (currentElem.type == Element.PARENRIGHT) {
				paren_stack.pop();
				parenleft_in_stack--;
				funcBody += currentElem.content;
			}
			if (currentElem.type == Element.SEMICOLON) {
				funcBody += currentElem.content;
				if (parenleft_in_stack == 0) {
					funcBody += (char)Constants.CR;
					funcBody += (char)Constants.LF;
					int i;
					if (nextElem.type == Element.BRACERIGHT) i = 1;
					else i = 0;
					for (; i < braceleft_in_stack; i++) {
						funcBody += Constants.FSPACE;
					}
				}
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
				&& !this.isParenleft(b) && !this.isParenright(b)
				&& !this.isSemicolon(b) && !this.isSpace(b) && !this.isWord(b))
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

	private boolean isParenright(byte b) {
		if (b == Constants.PARENRIGHT)
			return true;
		else
			return false;
	}

	private boolean isSemicolon(byte b) {
		if (b == Constants.SEMICOLON)
			return true;
		else
			return false;
	}

	private boolean isSpace(byte b) {
		if (b == Constants.SPACE || b == Constants.LF || b == Constants.CR
				|| b == Constants.TAB)
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

			Formater f = new JSFormater();
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
