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
package com.ibm.lconn.migration.activities;

import java.util.Properties;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.lconn.common.operator.LogOperator;
import com.ibm.lconn.common.util.Spliter;
import com.ibm.lconn.common.util.StringResolver;
import com.ibm.lconn.common.xml.XMLOperator;
/**
 * 
 * @author Jun Jing Zhang (jjzhang@cn.ibm.com)
 *
 */

public class FixNotification extends LogOperator {
	@Override
	public boolean execute(String para) {
		Spliter split = new Spliter(para);
		String source = split.getHeader();
		String dest = split.getTail();
		Properties props = getMacroProperties();
		source = StringResolver.resolveMacro(source, props);
		dest = StringResolver.resolveMacro(dest, props);
		
		XMLOperator xmlO = new XMLOperator();
		xmlO.setOutput(getOutput());
		Document sourceDoc = xmlO.getDocument(source);
		Document destDoc = xmlO.getDocument(dest);
		String xpath = "eventBroker/subscriber[@class='com.ibm.openactivities.notifications.email.ConnectionsEmailNotifier']";
		try {
			NodeList destNodes = XPathAPI.selectNodeList(destDoc.getDocumentElement(), xpath);
			NodeList sourceNodes = XPathAPI.selectNodeList(sourceDoc.getDocumentElement(), xpath);
			int i = 0;
			while(i < destNodes.getLength()){
				Node child = destNodes.item(i++);
				child.getParentNode().removeChild(child);
			}
			if(sourceNodes.getLength()>1){
				Node importNode = destDoc.importNode(sourceNodes.item(0), true);
				Node parent = XPathAPI.selectSingleNode(destDoc.getDocumentElement(), "eventBroker");
				parent.appendChild(importNode);
			}
			xmlO.saveXML(dest, destDoc);
			log("Complete Fixing notification in oa-config.xml");
			return true;
		} catch (Exception e) {
			log("Failed to fix notification in oa-config.xml, please check activities notification settings after import");
			return false;
		}
		
	}
}
