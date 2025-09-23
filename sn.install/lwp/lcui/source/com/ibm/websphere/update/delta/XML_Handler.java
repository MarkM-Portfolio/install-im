/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */
package com.ibm.websphere.update.delta;

// Extractor XML Handler

// This class is separated from the Extractor because it has references to
// Xerces APSs, and we want to be able to load the extractor without Xerces
// being present.

import java.util.*;
import org.w3c.dom.*;

/**
 *  
 */
class XML_Handler {

   public static final String pgmVersion = "1.3" ;
   public static final String pgmUpdate = "4/29/04" ;

 protected static Logger   logStream       = null;
 protected static Document dom             = null;
 protected static String   lastXmlFileName = null;
 protected static DomL2Spt domL2           = null;
 protected static Vector   errMsg          = new Vector();

 protected static boolean validating;
 protected static boolean nameSpaceAware;



//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                  Class Constructors                           บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 public XML_Handler(Logger logStream, boolean validating, boolean nameSpaceAware) {

	XML_Handler.validating     = validating;
	XML_Handler.nameSpaceAware = nameSpaceAware;
	XML_Handler.logStream      = logStream;

 }


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Get the Parser Version                          บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String getVersion(boolean debug) {

 if (domL2 == null)
   domL2 =  new DomL2Spt(errMsg, validating, nameSpaceAware, debug);    // XML Helper class

 spewMsg();  // domL2 may enter error messages here
 return domL2.getVersion();

}


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ               Load the document if necessary                  บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
boolean loadDoc(boolean debug, String xmlFileName) {

 if (debug) {
   log("Loading "+ xmlFileName);

   logOnly("Parser options: "+
                   ((validating) ? "Validating, " : "Non-Validating, ")             +
                   ((nameSpaceAware) ? "NameSpaceAware, " : "Non-NameSpaceAware, "));
 }

 if (domL2 == null) {
   domL2 =  new DomL2Spt(errMsg, validating, nameSpaceAware, debug);    // XML Helper class
   spewMsg();
 }

 if ((dom == null) || (xmlFileName.equals(lastXmlFileName) == false)) {
   dom = domL2.loadDoc(xmlFileName);  // Load the XML file into a DOM L2
   spewMsg();  // domL2 may enter error messages here
   lastXmlFileName = xmlFileName;

   if (dom == null) {
     logErr(151, "Failed to load xml document ("+ xmlFileName +")");
     return false;
   }
 }

 return true;
}



//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     Query an XML File                         บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String query(boolean debug, String xmlFileName, String[] hierarchy) {


 if (debug) {
   log("Querying "+ xmlFileName);
   log(" hierarchy" );

   for (int i=0; i < hierarchy.length; i++) {
     StringBuffer sb = new StringBuffer();
     for (int p=0; p < i*2; p++ )
       sb.append(" ");

     log(sb.toString() + hierarchy[i]);
   }

   logOnly("Parser options: "+
                   ((validating) ? "Validating, " : "Non-Validating, ")             +
                   ((nameSpaceAware) ? "NameSpaceAware, " : "Non-NameSpaceAware, "));
 }

 String variable = null;

 if (loadDoc(debug, xmlFileName))
   variable = domL2.getValue(dom, hierarchy);

 spewMsg();   // domL2 may enter error messages here
 return variable;
}



//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                 Build Vector of events                        บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
Vector getEvents(boolean debug, String xmlFileName, String[] node2Find) {
 // here we will construct a Vector of the History/events, this is done
 // here to keep all all XML types out of the Extractor, so the Extractor
 // may still operate without a parser

 if (!loadDoc(debug, xmlFileName))
   return null;

 // String[] node2Find = {"#document", "websphere", "appserver", "history", "event" };

 Node eventNode = domL2.findFirstNode(dom, node2Find);
 if (eventNode == null) {
   spewMsg();   // domL2 may record error messages here
   return null;
 }

 Vector eventVector = new Vector();  // this vector will contain HelperList.HistoryEventInfo objects

 while (eventNode != null) {   // chain through the event nodes

   NodeList kids = eventNode.getChildNodes();

   HelperList.XMLHistoryEventInfo hei = new HelperList.XMLHistoryEventInfo();
   boolean goodEvent = false;

   for (int kid=0; kid < kids.getLength(); kid++) {
     goodEvent = true;

     Node dataNode    =  kids.item(kid);
     String nodeName  = dataNode.getNodeName();

     String nodeValue;

     Node firstChild = dataNode.getFirstChild();
     if (firstChild == null)
       nodeValue ="(No Data)";
     else
       nodeValue = firstChild.getNodeValue();

          if (nodeName.equalsIgnoreCase("sqlTime"))         hei.sqlTime         = nodeValue;
     else if (nodeName.equalsIgnoreCase("description"))     hei.description     = nodeValue;
     else if (nodeName.equalsIgnoreCase("type"))            hei.type            = nodeValue;
     else if (nodeName.equalsIgnoreCase("containertype"))   hei.containerType   = nodeValue;
     else if (nodeName.equalsIgnoreCase("installPath"))     hei.targetDirName   = nodeValue;
     else if (nodeName.equalsIgnoreCase("uninstallScript")) hei.backupJarName   = nodeValue;
     else if (nodeName.equalsIgnoreCase("activityLog"))     hei.logFileName     = nodeValue;
     else if (nodeName.equalsIgnoreCase("startingVersion")) hei.startingVersion = nodeValue;
     else if (nodeName.equalsIgnoreCase("endingVersion"))   hei.endingVersion   = nodeValue;
     else if (nodeName.equalsIgnoreCase("source"))          hei.deltaJarName    = nodeValue;
     else if (nodeName.equalsIgnoreCase("status"))          hei.status          = nodeValue;
     else if (nodeName.equalsIgnoreCase("errorMessage"))    hei.message         = nodeValue;
     else if (nodeName.equalsIgnoreCase("apar"))            hei.APAR            = nodeValue;
     else if (nodeName.equalsIgnoreCase("pmr"))             hei.PMR             = nodeValue;
     else if (nodeName.equalsIgnoreCase("developer"))       hei.developer       = nodeValue;

     if (debug)
       System.out.println("Debug -- #03 NodeName=" + nodeName +",  Value="+ nodeValue);
   }


   if (goodEvent)
     eventVector.add(hei);

   if (debug)
     System.out.println("Debug -- Next event");

   eventNode = eventNode.getNextSibling();
 }

 return eventVector;
}


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ   Update a field in a XML File,   returns old value           บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String update(boolean debug,  String xmlFileName, String[] hierarchy, String newValue) {

 if (debug) {
   log("Updating "+ xmlFileName);

   logOnly("Parser options: "+
                   ((validating) ? "Validating, " : "Non-Validating, ")             +
                   ((nameSpaceAware) ? "NameSpaceAware, " : "Non-NameSpaceAware, "));
 }

 String variable = null;

 if (loadDoc(debug, xmlFileName))
   variable = domL2.updateValue(dom, hierarchy, newValue);
 spewMsg();

 return variable;
}

//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                Add and History Event                          บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void addXMLEvent(boolean debug, HelperList.XMLHistoryEventInfo hei, String[] eventHistory) {

 log("Updating "+ hei.xmlFileName);

 logOnly("Parser options: "+
                   ((hei.validating) ? "Validating, " : "Non-Validating, ")             +
                   ((hei.nameSpaceAware) ? "NameSpaceAware, " : "Non-NameSpaceAware, ") +
                   ((hei.addHistory) ? "Add History Element if needed" : "do not add History Element") );

 if (loadDoc(debug, hei.xmlFileName)) {

   // String[] eleHierarchy = new String[] {"#document", "websphere", "appserver", "version"};
   // String currentVersion = domL2.getValue(dom, eleHierarchy);

   String[] eventItems = new String[14];
   eventItems[0]  = hei.description;
   eventItems[1]  = hei.type;
   eventItems[2]  = hei.containerType;
   eventItems[3]  = hei.targetDirName;             // target of Update
   eventItems[4]  = hei.backupJarName;             // UnDo script
   eventItems[5]  = hei.logFileName;               // Activity Log
   eventItems[6]  = hei.startingVersion;           // endingVersion
   eventItems[7]  = hei.endingVersion;             // endingVersion
   eventItems[8]  = hei.deltaJarName;              // Input Source
   eventItems[9]  = hei.status;                    // Status
   eventItems[10] = hei.message;
   eventItems[11] = hei.APAR;
   eventItems[12] = hei.PMR;
   eventItems[13] = hei.developer;

   if (! domL2.addEvent(dom, eventItems, hei.addHistory, eventHistory)) {
     spewMsg();
     logErr(52, "failed to add event node");
     return;
   }
 }

 return;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ            Write the Current File,  if any                             บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
boolean writeFile(boolean debug, String xmlFileName) {

 if (dom != null) {

   if (!domL2.writeDoc(dom, xmlFileName)) {
     spewMsg();
     return false;

   } else
     logOnly( xmlFileName +" was successfully written.");

   dom = null;
 }

 return true;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ           Log and Display the content of a Vector                      บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void spewMsg() {

 for (int i=0; i < errMsg.size(); i++) {
   log( (String) errMsg.elementAt(i));
 }

 errMsg.clear();
 return;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                        Display Mesasges                                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void log(String msg) {

 if (logStream == null)
   System.out.println("XML_Handler -- "+ msg);
 else
   logStream.Both(msg);
}


void logOnly(String msg) {

 if (logStream == null)
   System.out.println("XML_Handler -- "+ msg);
 else
   logStream.Log(msg);
}


void logErr(int errNum, String msg) {

 if (logStream == null)
   System.out.println("Error "+ errNum +" in XML_Handler -- "+ msg);
 else
   logStream.Err(errNum,  msg);

}


}
