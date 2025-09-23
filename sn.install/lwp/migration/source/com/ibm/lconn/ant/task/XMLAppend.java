/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* 5724-S68                                                          */
/* 5724-S68                                                          */
package com.ibm.lconn.ant.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.ibm.lconn.common.xml.XMLOperator;

/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 * 
 */
public class XMLAppend extends Task {
	String destXML;
	List<XMLAction> appendList;
	private XMLOperator util;
	private Document doc;

	public AttributeRemove createAttributeRemove() {
		AttributeRemove attrRemoval = new AttributeRemove();
		getAppendList().add(attrRemoval);
		return attrRemoval;
	}

	public class AttributeRemove implements XMLAction {
		private String xPath;
		private boolean removeAll;
		private int removed;

		public String getXPath() {
			return xPath;
		}

		public void setXPath(String path) {
			xPath = path;
		}

		public boolean isRemoveAll() {
			return removeAll;
		}

		public void setRemoveAll(boolean removeAll) {
			this.removeAll = removeAll;
		}

		public void execute() throws Exception {
			logI("Removing attribute {0}: ", getXPath());
			Document doc = getDocument();
			NodeIterator iter = XPathAPI.selectNodeIterator(doc.getDocumentElement(), getXPath());
			Node node = iter.nextNode();
			while (node != null) {
				Attr attr = (Attr) node;
				attr.getOwnerElement().removeAttributeNode(attr);
				removed++;
				if (!isRemoveAll())
					return;
				node = iter.nextNode();
			}
		}

		public void fail() {
			logI("Failed to remove.");
		}

		public void success() {
			logI("Removed {0}. ", removed);
		}
	}
	
	private static void logI(String msg, Object... para) {
		System.out.println(MessageFormat.format(msg, para));
	}

	public void testExecuteAppend() throws Exception {
		setDestXML("d:/IBM/WebSphere/AppServer/profiles/AppSrv01/config/cells/IBM-ZJJNode01Cell/IBM-Connections/global-config.xml");
		AppendFromXML xml = createAppendFromXML();
		xml.setIdXPath("exposeEmail");
		xml.setOverwrite(true);
		xml.setParentXPath("/");
		xml.setSourceXML("d:/temp/config/common/global-config.xml");
		xml.setSourceXPath("exposeEmail");

		execute();
	}

	public static void main(String[] args) throws Exception {
		XMLAppend a = new XMLAppend();
		a.testExecute();
	}

	private void testExecute() {
		setDestXML("d:/temp/global-config.xml.xml");
		AttributeRemove ar = createAttributeRemove();
		ar.setXPath("serviceReference/@sso_ltpa_token_enabled");
		ar = createAttributeRemove();
		ar.setXPath("serviceReference/@cache_index_capacity");
		execute();
	}

	@Override
	public void execute() throws BuildException {
		System.out.println("Start to append elements into " + destXML);
		try {
			getDocument();
			Iterator<XMLAction> strIter = getAppendList().iterator();
			while (strIter.hasNext()) {
				XMLAppend.XMLAction action = strIter.next();
				try {
					action.execute();
					action.success();
				} catch (Exception e) {
					action.fail();
				}
			}
			save();
		} catch (Exception e) {
			System.out.println("Fail to append elements into " + destXML);
		}
		System.out.println("Finish appending elements into " + destXML);

	}

	private Document getDocument() {
		if (doc == null)
			doc = getUtil().getDocument(getDestXML());
		return doc;
	}

	private void save() throws FileNotFoundException, IOException {
		getUtil().saveXML(getDestXML(), getDocument());
	}

	private XMLOperator getUtil() {
		if (util == null)
			util = new XMLOperator();
		return util;
	}

	public AppendFromString createAppendFromString() {
		AppendFromString afs = new AppendFromString();
		getAppendList().add(afs);
		return afs;
	}

	public AppendFromXML createAppendFromXML() {
		AppendFromXML afs = new AppendFromXML();
		getAppendList().add(afs);
		return afs;
	}

	public String getDestXML() {
		return destXML;
	}

	public void setDestXML(String destXML) {
		this.destXML = destXML;
	}

	public interface XMLAction {
		public void execute() throws Exception;

		public void fail();

		public void success();
	}

	public abstract class AppendElement implements XMLAction {
		private String parentXPath, idXPath, relativeXPath;
		private boolean overwrite, insertAfter;

		public String getParentXPath() {
			return parentXPath;
		}

		public void execute() throws Exception {
			Document destDoc = getDocument();
			Node rootNode = destDoc.getDocumentElement();
			Node parent = null;
			String parentXPath = this.getParentXPath();
			if (null == parentXPath || "/".equals(parentXPath.trim())) {
				parent = rootNode;
			} else {
				parent = XPathAPI.selectSingleNode(rootNode, parentXPath);
			}
			Node createdNode = this.getTargetNode();
			Node importNode = destDoc.importNode(createdNode, true);
			if (this.isOverwrite()) {
				Node target = XPathAPI.selectSingleNode(parent, this
						.getIdXPath());
				if (target != null)
					parent.removeChild(target);
			}
			String relativeXPath = this.getRelativeXPath();
			Node relativeNode = null;
			if (relativeXPath != null) {
				try {
					relativeNode = XPathAPI.selectSingleNode(rootNode,
							relativeXPath);
				} catch (Exception e) {
					System.out.println("Warn: relative node: " + relativeXPath
							+ " in " + destXML + " not found. ");
				}
			}
			if (!this.isInsertAfter()) {
				parent.insertBefore(importNode, relativeNode);
			} else {
				if (relativeNode == null) {
					parent.appendChild(importNode);
				} else {
					try {
						Node nextSibling = relativeNode.getNextSibling();
						parent.insertBefore(importNode, nextSibling);
					} catch (Exception e) {
						parent.appendChild(importNode);
					}
				}
			}
		}

		public void setParentXPath(String parentXPath) {
			this.parentXPath = parentXPath;
		}

		public String getIdXPath() {
			return idXPath;
		}

		public void setIdXPath(String idXPath) {
			this.idXPath = idXPath;
		}

		public boolean isOverwrite() {
			return overwrite;
		}

		public void setOverwrite(boolean overwrite) {
			this.overwrite = overwrite;
		}

		abstract public Node getTargetNode();

		public String getRelativeXPath() {
			return relativeXPath;
		}

		public void setRelativeXPath(String relativeXPath) {
			this.relativeXPath = relativeXPath;
		}

		public boolean isInsertAfter() {
			return insertAfter;
		}

		public void setInsertAfter(boolean insertAfter) {
			this.insertAfter = insertAfter;
		}

	}

	public class AppendFromString extends AppendElement {
		String elementSource;

		public String getElementSource() {
			return elementSource;
		}

		public void setElementSource(String elementSource) {
			this.elementSource = elementSource;
		}

		@Override
		public Node getTargetNode() {
			Node createdNode = getUtil().createNode(getElementSource());
			return createdNode;
		}

		public void fail() {
			System.out.println("Fail to append element " + getElementSource());

		}

		public void success() {
			System.out.println("Succeed to append element "
					+ getElementSource());
		}

	}

	public class AppendFromXML extends AppendElement {
		String sourceXML, sourceXPath;

		public String getSourceXML() {
			return sourceXML;
		}

		public void setSourceXML(String sourceXML) {
			this.sourceXML = sourceXML;
		}

		public String getSourceXPath() {
			return sourceXPath;
		}

		public void setSourceXPath(String sourceXPath) {
			this.sourceXPath = sourceXPath;
		}

		public void fail() {
			System.out.println("Fail to append element " + getSourceXPath()
					+ " from " + getSourceXML());

		}

		public void success() {
			System.out.println("Succeed to append element " + getSourceXPath()
					+ " from " + getSourceXML());
		}

		@Override
		public Node getTargetNode() {
			try {
				Document sourceDoc = getUtil().getDocument(getSourceXML());
				Node readNode = XPathAPI.selectSingleNode(sourceDoc
						.getDocumentElement(), getSourceXPath());
				return readNode;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public List<XMLAction> getAppendList() {
		if (appendList == null)
			appendList = new ArrayList<XMLAction>();
		return appendList;
	}

}
