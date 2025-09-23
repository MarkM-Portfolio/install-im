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
package com.ibm.lconn.common.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import com.ibm.lconn.common.msg.CommonMessages;
import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.util.ObjectUtil;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */

public class XMLOperator extends LogOperator {

	public String readValue(Node root, String xpath)
			throws TransformerException {
		Node node = XPathAPI.selectSingleNode(root, xpath);
		return node.getNodeValue();
	}

	private void setText(Node baseNode, String xpathLevelModify,
			List<String> readTexts, String valueTarget) {
		for (int i = 0; i < readTexts.size(); i++) {
			String paras = (String) readTexts.get(i);
			try {
				String[] parameters = paras.split("\n"); //$NON-NLS-1$
				String xpath = MessageFormat.format(xpathLevelModify,
						ObjectUtil.str2Obj(parameters));
				NodeIterator selectNodeIterator = XPathAPI.selectNodeIterator(
						baseNode, xpath);
				Node node = selectNodeIterator.nextNode();
				String targetValue = parameters[Integer.parseInt(valueTarget)];
				while (node != null) {
					// node.setTextContent(parameters[valueTarget])
					node.setNodeValue(targetValue);
					node = selectNodeIterator.nextNode();
				}
				log(CommonMessages.getString("xml.setting.import.success"),
						xpath, targetValue);
			} catch (Exception e) {
				debug(e);
				log(CommonMessages.getString("xml.setting.import.fail"),
						xpathLevelModify, valueTarget, paras);
			}
		}
	}

	public boolean execValueSetter(String dest, String xpath, String newValue) {
		Document destDoc = getDocument(dest);
		try {
			NodeIterator selectNodeIterator = XPathAPI.selectNodeIterator(
					destDoc.getDocumentElement(), xpath);
			Node node = selectNodeIterator.nextNode();
			while (node != null) {
				node.setNodeValue(newValue);
				node = selectNodeIterator.nextNode();
			}
			saveXML(dest, destDoc);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private List<String> readTexts(Node baseNode, String xpathLevel1,
			List<String> lastLevel, boolean multi) throws TransformerException {
		ArrayList<String> curLevel = new ArrayList<String>();
		if (lastLevel.size() == 0)
			lastLevel.add(""); //$NON-NLS-1$
		for (int i = 0; i < lastLevel.size(); i++) {
			String paras = (String) lastLevel.get(i);
			String xpath = MessageFormat.format(xpathLevel1, ObjectUtil
					.str2Obj(paras.split("\n"))); //$NON-NLS-1$
			if (multi) {
				NodeIterator selectNodeIterator = XPathAPI.selectNodeIterator(
						baseNode, xpath);
				Node node = selectNodeIterator.nextNode();
				while (node != null) {
					String text = node.getNodeValue();
					if ("".equals(paras)) { //$NON-NLS-1$
						curLevel.add(text);
					} else {
						curLevel.add(paras + "\n" + text); //$NON-NLS-1$
					}
					node = selectNodeIterator.nextNode();
				}
			}
		}
		return curLevel;
	}

	public void execSetValueTask(String sourceXML, String destXML,
			String xpathLevelsStr, String xpathLevelForDest,
			String targetValueIndex, String delim) {
		log(CommonMessages.getString("xml.setting.import.start"), sourceXML,
				destXML, xpathLevelsStr, xpathLevelForDest, targetValueIndex);
		Document sourceJob = getDocument(sourceXML);
		Document destJob = getDocument(destXML);
		List<String> readTexts = new ArrayList<String>();
		try {
			String[] xpathLevels = xpathLevelsStr.split(delim); //$NON-NLS-1$
			for (int i = 0; i < xpathLevels.length; i++) {
				readTexts = readTexts(sourceJob.getDocumentElement(),
						xpathLevels[i], readTexts, true);
			}
		} catch (Exception e) {
			debug(e);
			log(CommonMessages.getString("xml.setting.read.fail")); //$NON-NLS-1$
			return;
		}

		setText(destJob.getDocumentElement(), xpathLevelForDest, readTexts,
				targetValueIndex);
		try {
			saveXML(destXML, destJob);
		} catch (Exception e) {
			debug(e);
			log(CommonMessages.getString("xml.save.fail"), destXML, e
					.getMessage());
		}
		log(
				CommonMessages.getString("xml.setting.import.task.success"), destXML); //$NON-NLS-1$
	}

	public void execSetValueTask(String sourceXML, String destXML,
			String xpathLevelsStr, String xpathLevelForDest,
			String targetValueIndex) {
		execSetValueTask(sourceXML, destXML, xpathLevelsStr, xpathLevelForDest,
				targetValueIndex, "\n");
	}

	public void execReplaceElements(String sourceXML, String destXML,
			String sourceAryStr, String destAryStr, String delim) {
		execReplaceElements(sourceXML, destXML, sourceAryStr.split(delim),
				destAryStr.split(delim));
	}

	public void execReplaceElements(String sourceXML, String destXML,
			String[] sourceAry, String[] destAry) {
		Document sourceDoc = getDocument(sourceXML);
		Document destDoc = getDocument(destXML);
		if (sourceDoc == null || destDoc == null) {
			return;
		}
		log(
				CommonMessages.getString("xml.element.replace.start"), sourceXML, destXML); //$NON-NLS-1$
		for (int i = 0; i < sourceAry.length; i++) {
			String elementSourceXpath = sourceAry[i];
			String elementDestXpath = destAry[i];
			try {
				replaceElement(sourceDoc, destDoc, elementSourceXpath,
						elementDestXpath);
				log(
						CommonMessages.getString("xml.element.replace.success"), //$NON-NLS-1$
						sourceXML, elementSourceXpath, destXML,
						elementDestXpath);
			} catch (Exception e) {
				log(
						CommonMessages.getString("xml.element.replace.fail"), //$NON-NLS-1$
						sourceXML, elementSourceXpath, destXML,
						elementDestXpath, e.getMessage());
			}

		}
		try {
			saveXML(destXML, destDoc);
		} catch (Exception e) {
			debug(e);
			log(CommonMessages.getString("xml.save.fail"), destXML, e
					.getMessage());
		}
		log(
				CommonMessages.getString("xml.element.replace.task.success"), destXML); //$NON-NLS-1$

	}

	public void execAppendElement(String sourceXML, String destParentXPath,
			String elementSource, boolean overide, String idXPath) {
		Document sourceDoc = getDocument(sourceXML);
		try {
			Node parent = XPathAPI.selectSingleNode(sourceDoc
					.getDocumentElement(), destParentXPath);
			if (overide) {
				Node target = XPathAPI.selectSingleNode(parent, idXPath);
				if (target != null)
					parent.removeChild(target);
			}
			Node createdNode = createNode(elementSource);
			Node importNode = sourceDoc.importNode(createdNode, false);
			parent.appendChild(importNode);
		} catch (Exception e) {
			log(CommonMessages.getString("xml.element.append.fail", sourceXML,
					destParentXPath, elementSource, overide));
		}
		log(CommonMessages.getString("xml.element.append.success", sourceXML,
				destParentXPath, elementSource, overide));
	}

	public void replaceElement(Document sourceDoc, Document destDoc,
			String elementSourceXpath, String elementDestXpath)
			throws TransformerException {
		boolean append = elementDestXpath.startsWith("@APPEND@");
		boolean replaceChildOnly = elementDestXpath
				.startsWith("@REPLACE_CHILD_ONLY");
		if (append)
			elementDestXpath = elementDestXpath.substring(8);
		Node sourceNode = XPathAPI.selectSingleNode(sourceDoc
				.getDocumentElement(), elementSourceXpath);
		Node destNode = XPathAPI.selectSingleNode(destDoc.getDocumentElement(),
				elementDestXpath);
		if (sourceNode == null || destNode == null) {
			if (sourceNode == null)
				log(CommonMessages
						.getString("xml.element.replace.source.node.not.exist"));//$NON-NLS-1$
			if (destNode == null)
				log(CommonMessages
						.getString("xml.element.replace.dest.node.not.exist"));//$NON-NLS-1$
			return;
		}
		Node importedNode = destDoc.importNode(sourceNode, true);
		if (append) {
			destNode.appendChild(importedNode);
		} else {
			if (replaceChildOnly) {
				NodeList destChildNodes = destNode.getChildNodes();
				NodeList sourceChildNodes = importedNode.getChildNodes();

				ArrayList<Node> nodeToRemove = new ArrayList<Node>();
				for (int i = 0; i < destChildNodes.getLength(); i++) {
					Node child = destChildNodes.item(i);
					for (int j = 0; j < sourceChildNodes.getLength(); j++) {
						Node sourceChild = sourceChildNodes.item(j);
						if (child.getNodeName().equals(
								sourceChild.getNodeName())) {

							nodeToRemove.add(child);
						}
					}
				}
				for (int i = 0; i < nodeToRemove.size(); i++) {
					destNode.removeChild(nodeToRemove.get(i));
				}
				for (int i = 0; i < sourceChildNodes.getLength(); i++) {
					Node sourceChild = sourceChildNodes.item(i);
					destNode.appendChild(sourceChild);
				}
			} else {
				Node parentNode = destNode.getParentNode();
				parentNode.replaceChild(importedNode, destNode);
			}
		}
	}

	public void saveXML(String destXML, Document destDoc)
			throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(destXML);
		XMLSerializer ser = new XMLSerializer(fos, new OutputFormat(destDoc));
		ser.serialize(destDoc);
		fos.flush();
		fos.close();
	}

	public void saveXMLPretty(String destXML, Document destDoc)
			throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(destXML);
		OutputFormat formatter = new OutputFormat();
		formatter.setIndenting(true);
		formatter.setPreserveEmptyAttributes(true);
		formatter.setIndent(4);
		XMLSerializer ser = new XMLSerializer(fos, formatter);
		ser.serialize(destDoc);
		fos.flush();
		fos.close();
	}

	public Document getDocument(String xmlSource) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(xmlSource);
		} catch (Exception e) {
			debug(e);
			log(CommonMessages.getString("xml.document.read.fail"), //$NON-NLS-1$
					xmlSource, e.getMessage());
		}
		return document;
	}

	private void debug(Exception e) {
		log(e.toString());
	}

	public Node createNode(String sourceStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			File tempFile = File
					.createTempFile("xmloperatorCreateNode", ".xml");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tempFile)));
			bw.append(sourceStr);
			bw.flush();
			bw.close();
			document = builder.parse(tempFile);
			tempFile.deleteOnExit();
		} catch (Exception e) {
			debug(e);
			log(CommonMessages.getString("xml.document.read.fail"), //$NON-NLS-1$
					sourceStr, e.getMessage());
		}
		return document.getDocumentElement();
	}

	public void replace(Document sourceDoc, Document destDoc, String xpath) {
		Element sourceRoot = sourceDoc.getDocumentElement();
		Element destRoot = destDoc.getDocumentElement();
		Node destNode = null;
		try {
			destNode = XPathAPI.selectSingleNode(destRoot, xpath);
		} catch (Exception e) {
		}
		if (destNode == null) {
			try {
				appendNoneExist(sourceDoc, destDoc, xpath);

			} catch (Exception e) {
				log("Error: {0} can not be found in detination xml. ", xpath);
				return;
			}
		} else {
			if (destNode.getNodeType() == Node.ATTRIBUTE_NODE) {
				// attribute
				Attr attr = (Attr) destNode;
				try {
					Node n = XPathAPI.selectSingleNode(sourceRoot, xpath);
					attr.setNodeValue(n.getNodeValue());
				} catch (Exception e) {
					log(
							"Warning: {0} can not be found in source: source xml. ",
							xpath);
				}
			} else if (destNode.getNodeType() == Node.ELEMENT_NODE) {
				// element
				try {
					Node parent = destNode.getParentNode();

					// log old nodes to be removed
					NodeIterator destNodeIter = XPathAPI.selectNodeIterator(
							destRoot, xpath);
					Node nextNode = destNodeIter.nextNode();
					ArrayList<Node> list = new ArrayList<Node>();
					while (nextNode != null) {
						list.add(nextNode);
						nextNode = destNodeIter.nextNode();
					}

					try {
						// append source nodes
						NodeIterator sourceNodeIter = XPathAPI
								.selectNodeIterator(sourceRoot, xpath);
						nextNode = sourceNodeIter.nextNode();

						while (nextNode != null) {
							Node importNode = destDoc
									.importNode(nextNode, true);
							if (nextNode != null && parent instanceof Document) {
								parent.replaceChild(importNode, destNode);
							} else {
								parent.insertBefore(importNode, destNode);
							}
							// parent.appendChild(importNode);
							nextNode = sourceNodeIter.nextNode();
						}

						removeOldNodes(list);
					} catch (Exception e) {
						e.printStackTrace();
						log(
								"Warning: {0} can not be found in source. The element(s) in the destination document will be removed. ",
								xpath);
					}
				} catch (Exception e) {
					// no need to handle
				}
			}
		}
	}

	public void appendNoneExist(Document sourceDoc, Document destDoc,
			String xpath) throws TransformerException {
		String xpathParent = getParentXPath(xpath);
		Node destParent = XPathAPI.selectSingleNode(destDoc
				.getDocumentElement(), xpathParent);
		if (destParent == null) {
			log(
					"ERROR: The parent node({0}) of for {1} does not exist in the destination XML",
					xpathParent, xpath);
			return;
		}
		NodeIterator sourceNodeIter = XPathAPI.selectNodeIterator(sourceDoc
				.getDocumentElement(), xpath);
		ArrayList<Node> sourceNodes = new ArrayList<Node>();
		Node nextNode = sourceNodeIter.nextNode();
		if(nextNode==null){
			log("Warning: {0} does not exist in both source and destination xml files", xpath);
			return;
		}
		if(nextNode.getNodeType()==Node.ATTRIBUTE_NODE){
			Element target = (Element) destParent;
			target.setAttribute(nextNode.getNodeName(), nextNode.getNodeValue());
			return;
		}
		Node firstNode = nextNode;
		Node parent = nextNode.getParentNode();
		Node lastNode = null;
		while (nextNode != null) {
			Node importNode = destDoc.importNode(nextNode, true);
			sourceNodes.add(importNode);
			lastNode = nextNode;
			nextNode = sourceNodeIter.nextNode();
		}
		NodeList sourceChild = parent.getChildNodes();
		ArrayList<Node> previousNodes = new ArrayList<Node>();
		ArrayList<Node> afterNodes = new ArrayList<Node>();
		boolean previous = true;
		boolean after = false;
		for (int i = 0; i < sourceChild.getLength(); i++) {
			Node curChild = sourceChild.item(i);
			if(curChild.getNodeType()!=Node.ELEMENT_NODE) continue;
			if (curChild.equals(firstNode)) {
				previous = false;
				if(curChild.equals(lastNode)){
					after = true;
				}
				continue;
			}
			if (curChild.equals(lastNode)) {
				after = true;
				continue;
			}
			if (previous)
				previousNodes.add(curChild);
			if (after)
				afterNodes.add(curChild);
		}
		NodeList destChildren = destParent.getChildNodes();
		// Found any node after target node in the destination parent node.
		for (Iterator<Node> iterator = afterNodes.iterator(); iterator
				.hasNext();) {
			Node curAfter = (Node) iterator.next();
			String targetNodeName = curAfter.getNodeName();
			for (int i = 0; i < destChildren.getLength(); i++) {
				Node curChildInDest = destChildren.item(i);
				if (curAfter.getNodeType()==curChildInDest.getNodeType() && targetNodeName.equals(curChildInDest.getNodeName())) {
					insert(destDoc, destParent, curChildInDest, sourceNodes, -1);
					return;
				}
			}
		}
		// Found any node before target node in the destination parent node.
		for (int i = destChildren.getLength() - 1; i >= 0; i--) {
			Node curChildInDest = (Node) destChildren.item(i);
			String targetNodeName = curChildInDest.getNodeName();
			for (Iterator<Node> iterator = previousNodes.iterator(); iterator
					.hasNext();) {
				Node curPrevious = (Node) iterator.next();
				if (curPrevious.getNodeType()==curChildInDest.getNodeType() && targetNodeName.equals(curPrevious.getNodeName())) {
					insert(destDoc, destParent, curChildInDest, sourceNodes, 1);
					return;
				}
			}
		}
		// No related node is found in destination parent node, should be
		// inserted as first element.
		insert(destDoc, destParent, destChildren.item(0), sourceNodes, -1);
	}

	/**
	 * @param destDoc
	 * @param curChildInDest
	 * @param sourceNodes
	 * @param i
	 */
	public void insert(Document destDoc, Node destParent, Node curChildInDest,
			ArrayList<Node> sourceNodes, int i) {
		for (Iterator<Node> iterator = sourceNodes.iterator(); iterator
				.hasNext();) {
			Node sourceNode = (Node) iterator.next();
			if (i < 0) {
				// insert after
				destParent.insertBefore(sourceNode, curChildInDest);
			}
			if (i > 0) {
				destParent.appendChild(sourceNode);
			}
		}
	}

	/**
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static String getParentXPath(String xpath) {
		char[] chars = xpath.toCharArray();
		int rightNotMatched = 0;
		for (int i = chars.length - 1; i >= 0; i--) {
			switch (chars[i]) {
			case ']':
				rightNotMatched++;
				break;
			case '/':
				if (rightNotMatched == 0) {
					if (i == 0)
						return null;
					return xpath.substring(0, i);
				}
				break;
			case '[':
				rightNotMatched--;
				break;
			}
		}
		return null;
	}
	private void removeOldNodes(ArrayList<Node> list) {
		for (Iterator<Node> iterator = list.iterator(); iterator.hasNext();) {
			Node node = iterator.next();
			if(node.getParentNode()==null) continue;
			node.getParentNode().removeChild(node);
		}
	}

}
