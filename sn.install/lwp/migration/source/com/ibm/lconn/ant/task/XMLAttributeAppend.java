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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.Task;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.lconn.common.xml.XMLOperator;

public class XMLAttributeAppend extends Task {
	String destXML;
	List<AppendAttribute> appendList;
	private XMLOperator util;

	public void testExecute() throws Exception {
		String notification201 = "D:/notification-config2.xml";
		String notification2001 = "D:/notification-config1.xml";
		setDestXML(notification2001);
		AppendFromXML xml1 = createAppendFromXML();
		xml1.setAttrName("sender");
		xml1.setOverwrite(true);
		xml1.setSourceXPath("//templates/source[@name='Activities']//channel[@name='email']");

		xml1.setSourceXML(notification201);

		AppendFromXML xml2 = createAppendFromXML();
		xml2.setOverwrite(true);
		xml2.setAttrName("sender");
		xml2.setSourceXPath("//templates/source[@name='dogear']//channel[@name='email']");
		xml2.setSourceXML(notification201);

		AppendFromXML xml3 = createAppendFromXML();
		xml3.setAttrName("sender");
		xml3.setOverwrite(true);
		xml3.setSourceXPath("//templates/source[@name='Blogs']//channel[@name='email']");
		xml3.setSourceXML(notification201);

		AppendFromXML xml4 = createAppendFromXML();
		xml4.setAttrName("sender");
		xml4.setOverwrite(true);
		xml4.setSourceXPath("//templates/source[@name='Communities']//channel[@name='email']");
		xml4.setSourceXML(notification201);

		AppendFromXML xml5 = createAppendFromXML();
		xml5.setAttrName("sender");
		xml5.setOverwrite(true);
		xml5.setSourceXPath("//templates/source[@name='Profiles']//channel[@name='email']");
		xml5.setSourceXML(notification201);

		execute();
	}

	public static void main(String[] args) throws Exception {
		XMLAttributeAppend a = new XMLAttributeAppend();
		a.testExecute();
	}

	public void execute() {
		XMLOperator xmlUtil = getUtil();
		String destXML = getDestXML();
		System.out.println("Start to append attributes into " + destXML);

		try {
			Document destDoc = xmlUtil.getDocument(destXML);
			System.out.println("XMLAttributeAppend execute() getDocument");
			Iterator<AppendAttribute> strIter = getAppendList().iterator();

			while (strIter.hasNext()) {
				System.out.println("XMLAttributeAppend execute() start loop");
				XMLAttributeAppend.AppendFromXML aa = (XMLAttributeAppend.AppendFromXML) strIter.next();
				System.out.println("XMLAttributeAppend execute() AppendFromXML");
				try {
					Node rootNode = destDoc.getDocumentElement();
					Attr sourceAttr = aa.getTargetAttribute();
					NodeList nodes = XPathAPI.selectNodeList(rootNode, aa.getSourceXPath());
					if (sourceAttr == null || nodes == null) {
						System.out.println("XMLAttributeAppend execute() sourceAttr == null : " + (sourceAttr == null));
						System.out.println("XMLAttributeAppend execute() nodes == null : " + (nodes == null));
						aa.fail();
					}
					for (int i = 0; i < nodes.getLength(); i++) {
						Element node = (Element) nodes.item(i);
						NamedNodeMap attrMap = node.getAttributes();
						System.out.println("XMLAttributeAppend execute() attrMap == null : " + (attrMap == null));
						for (int ii = 0; ii < attrMap.getLength(); ++ii) {
							Attr oldAttr = (Attr) attrMap.item(ii);
							System.out.println("XMLAttributeAppend execute() oldAttr == null : " + (oldAttr == null));
							if (oldAttr.getName() != null) {
								if (!oldAttr.getName().equalsIgnoreCase(sourceAttr.getName())) {
									System.out.println("XMLAttributeAppend execute() sourceAttr.getName()1 : " + sourceAttr.getName());
									System.out.println("XMLAttributeAppend execute() ssourceAttr.getValue()1 : " + sourceAttr.getValue());
									node.setAttribute(sourceAttr.getName(), sourceAttr.getValue());
									
								} else if (aa.isOverwrite()) {
									System.out.println("XMLAttributeAppend execute() oldAttr.getName()2 : " + oldAttr.getName());
									System.out.println("XMLAttributeAppend execute() sourceAttr.getName()2 : " + sourceAttr.getName());
									System.out.println("XMLAttributeAppend execute() ssourceAttr.getValue()2 : " + sourceAttr.getValue());
									node.removeAttribute(oldAttr.getName());
									node.setAttribute(sourceAttr.getName(), sourceAttr.getValue());
								}
							}
						}
					}
				} catch (Exception e) {
					aa.fail();
				}
				aa.success();
			}
			System.out.println("XMLAttributeAppend execute() start save.");
			xmlUtil.saveXML(destXML, destDoc);
			System.out.println("XMLAttributeAppend execute() start finish.");
		} catch (Exception e) {
			System.out.println("Fail to append attributes into " + destXML);
		}
		System.out.println("Finish appending attributes into " + destXML);
	}

	private XMLOperator getUtil() {
		if (util == null)
			util = new XMLOperator();
		return util;
	}

	public String getDestXML() {
		return destXML;
	}

	public List<AppendAttribute> getAppendList() {
		if (appendList == null)
			appendList = new ArrayList<AppendAttribute>();
		System.out.println("XMLAttributeAppend getAppendList() appendList.size = " + appendList.size());
		return appendList;
	}

	public void setDestXML(String destXML) {
		this.destXML = destXML;
	}

	public void setAppendList(List<AppendAttribute> appendList) {
		this.appendList = appendList;
	}

	public void setUtil(XMLOperator util) {
		this.util = util;
	}

	public AppendFromXML createAppendFromXML() {
		AppendFromXML afs = new AppendFromXML();
		getAppendList().add(afs);
		return afs;
	}

	public abstract class AppendAttribute {
		private String attrName;
		private boolean overwrite;

		public boolean isOverwrite() {
			return overwrite;
		}

		public void setOverwrite(boolean overwrite) {
			this.overwrite = overwrite;
		}

		abstract public void fail();

		abstract public void success();

		abstract public Attr getTargetAttribute();

		public String getAttrName() {
			return attrName;
		}

		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}

	}

	public class AppendFromXML extends AppendAttribute {
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
			System.out.println("Fail to append attribute " + getSourceXPath() + " from " + getSourceXML() + " attrName " + getAttrName());
		}

		public void success() {
			System.out.println("Succeed to append attribute " + getSourceXPath() + " from " + getSourceXML() + " attrName " + getAttrName());
		}

		public Attr getTargetAttribute() {
			System.out.println("AppendFromXML getTargetAttribute() : sourceXML = " + sourceXML);
			System.out.println("AppendFromXML getTargetAttribute() : sourceXPath = " + sourceXPath);
			System.out.println("AppendFromXML getTargetAttribute() : attrName = " + getAttrName());
			System.out.println("AppendFromXML getTargetAttribute() : isOverwrite = " + isOverwrite());
			try {
				Document sourceDoc = getUtil().getDocument(getSourceXML());
				NodeList nodes = XPathAPI.selectNodeList(sourceDoc.getDocumentElement(), getSourceXPath());
				for (int i = 0; i < nodes.getLength(); i++) {
					Element node = (Element) nodes.item(i);
					NamedNodeMap attrMap = node.getAttributes();
					for (int ii = 0; ii < attrMap.getLength(); ++ii) {
						Attr attr = (Attr) attrMap.item(ii);
						if (attr.getName().equalsIgnoreCase(getAttrName())) {
							return attr;
						}
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}
	}

}
