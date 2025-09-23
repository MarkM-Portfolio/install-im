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

//  XML DOM Level 2 Support Methods

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import javax.xml.parsers.*;
import org.apache.xerces.dom.*;
import org.apache.xerces.jaxp.*;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;


/** Support methods for XML DOM Level 2.
 * Provides for reading and outputing a DOM Level 2 object.
 * Special support for fetching a value, updating a value, locating a specific node, adding a
 * Event element in the Product.xml file. */
public class DomL2Spt {

 /** Version of this program. */
   public static final String pgmVersion = "1.3" ;
 /** Version of this program. */
   public static final String pgmUpdate = "4/29/04" ;

 /** Document Root Name. Root name of the last document loaded. */
 public static String documentRootName = null;

 /** Document System ID. System ID of the last document loaded,
  * also the DTD name specified in the <!DOCTYPE statement. */
 public static String documentSystemID = null;

 /** Document Public ID. Public ID of the last document loaded. */
 public static String documentPublicID = null;

 private static boolean validating     = true;
 private static boolean nameSpaceAware = true;


 //  options used during serialization
 /** If indenting should be used during output serialization, default=true. */
 public static boolean indenting          = true;

 /** Indent value used by the serialization process,  default: 5. */
 public static int     indent             = 5;

 /** The line seperator to use, default= System.getProperty("line.separator");*/
 public static String  lineSeparator      = System.getProperty("line.separator");

 /** If to preserve space during output serialization, default=true. */
 public static boolean preserveSpace      = true;

 /** If to omit comments during output serialization, default=false. */
 public static boolean omitComments       = false;

 /** If to omit document type during output serialization, default=false. */
 public static boolean omitDocumentType   = false;

 /** If to omit XML deceleration during output serialization, default=false. */
 public static boolean omitXMLDeclaration = true;

 /** Debug option, defaults to false.
  * Set by the constructor, but may altered any time by the calling program. */
 public static boolean debug = false;

 static Vector msgVector = null;

/** Passed in is a vector to accumulate messages and a debug option.
 * The message vector may be null, in which case any error messages will
 * be sent to STDERR.  If a vector is provided, the calling program is
 * responsible for printing or otherwise displaying the error messages.
 * The debug option, if true, causes verbose output to be sent to STDOUT. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                         Constructor                                    บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
 public DomL2Spt(Vector msgVector, boolean validating, boolean nameSpaceAware, boolean debug) {
	DomL2Spt.msgVector      = msgVector;
	DomL2Spt.validating     = validating;
	DomL2Spt.nameSpaceAware = nameSpaceAware;
	DomL2Spt.debug          = debug;
 }


/** Get the version of the XML parser. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Get Parser Version                               บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String getVersion() {

 // return org.apache.xerces.framework.Version.fVersion;
 return org.apache.xerces.impl.Version.fVersion;

}



/** Load an XML file into  DOM Level 2 document.
 * Null is returned if an error occurs and an associated message should be
 * placed in the msgVector or sent to STDERR. */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                            Load a document                             บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public Document loadDoc(String xmlFileName) {


 if ((xmlFileName == null) || (xmlFileName.length() == 0 )) {
   error(1, "input xmlFileName is null or has a length of zero.");
   return null;
 }

 Document doc = null;
 // DocumentBuilder parser = null;
 DocumentBuilderImpl parser = null; ;

 // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

 factory.setValidating(validating);
 factory.setNamespaceAware(nameSpaceAware);

 // try {
   // parser = factory.newDocumentBuilder();
   parser.setErrorHandler(new errHandler(this));

 // } catch (ParserConfigurationException ex) {
 //  error(2, "ParserConfigurationException : "+ ex.getMessage());
 //  return null;
 // }


 try {
   File tempFile = new File(xmlFileName);
   doc = parser.parse(tempFile.toURL().toString());

 } catch (SAXException ex) {
   error(3, "SAXException on "+ xmlFileName);
   return null;

 } catch (IOException ex) {
   error(4, "IOException on " + xmlFileName + " : " + ex.getMessage());
   return null;
 }


 DocumentType docType =  doc.getDoctype();

 if (docType == null) {
   error(20, "Failed to obtain DOCTYPE, DTD could be missing.");
   return null;
 }

 documentRootName = docType.getName();
 documentSystemID = docType.getSystemId();
 documentPublicID = docType.getPublicId();

 return doc;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       The Error Handler                                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
/**
 *  
 */
private class errHandler extends HandlerBase {

  DomL2Spt outerDom;

  public errHandler(DomL2Spt outerDom) {
    this.outerDom = outerDom;
  }

  // treat validation errors as fatal
  public void error (SAXParseException ex) throws SAXParseException {
    displayInfo(ex, "Error     ");
    throw ex;
  }

  public void warning (SAXParseException ex) throws SAXParseException {
    displayInfo(ex, "Warning   ");
  }


  public void fatalError (SAXParseException ex) throws SAXParseException {
    displayInfo(ex, "FatalError");
  }

  void displayInfo(SAXParseException ex, String errorType) {
    outerDom.error(5, errorType +" on line " + ex.getLineNumber() +", Column " + ex.getColumnNumber());
    outerDom.error(21, "     Message  : " + ex.getMessage());
    outerDom.error(22, "     SystemID : " + ex.getSystemId());
    outerDom.error(23, "     PublicID : " + ex.getPublicId());
   return;
  }

}


/** Output a DOM Level 2 Document
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                           Output a Document                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public boolean writeDoc(Document doc, String xmlFileName) {

 try {
   OutputFormat format = new OutputFormat( doc );   //Serialize DOM

   format.setIndent(indent);
   format.setIndenting(indenting);
   format.setLineSeparator(lineSeparator);
   format.setPreserveSpace(preserveSpace);
   format.setOmitComments(omitComments);
   format.setOmitDocumentType(omitDocumentType);
   format.setOmitXMLDeclaration(omitXMLDeclaration);


   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFileName)));
   writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
   writer.newLine();
   writer.newLine();

//   DocumentType dt = doc.getDoctype();
//
//   if (dt.getInternalSubset() == null) {
//     writer.write("<!DOCTYPE "+ dt.getName() +" SYSTEM \""+ dt.getSystemId() +"\" >");
//     writer.newLine();
//
//   } else {
//     writer.write("<!DOCTYPE "+ dt.getName() +" [");
//     writer.write(dt.getInternalSubset());
//     writer.write("]>");
//     writer.newLine();
//   }


   XMLSerializer serial = new XMLSerializer( writer, format );
   serial.asDOMSerializer();                            // As a DOM Serializer
   serial.serialize( doc.getDocumentElement() );

 } catch (FileNotFoundException ex) {
   error(6, "FileNotFoundException for "+ xmlFileName +" : "+ ex.getMessage());
 } catch (IOException ex) {
   error(7, "IOException on "+ xmlFileName +" : "+ ex.getMessage());
 }

 // InsertDocType(xmlFileName, documentRootName, documentSystemID);

 return true;
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                    Insert !DOCTYPE  Statement             DOM Level 2  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
private void InsertDocType(String xmlFile, String docName, String systemID ) {

 // Here we will read the XML file writen by the DOM serializer, scan for
 // the "<?xml version"  statement and insert the "<!DOCTYPE " statement.
 // Seems DOM Level 2 does not store the DTD nor schema, there is no node
 // created for this information as there was in DOM Level 1, although
 // the DOM Level 2 APIs still reference DTD information they do not work.
 // DOM Level 3 should correct this short comming.  Peter Jerkewitz

 if ((docName == null) || (systemID == null))
   return;

 Vector xmlLines = File2Vector(xmlFile);

 if (xmlLines != null) {

   for (int ele=0; ele < xmlLines.size(); ele++) {
     String workLine = (String) xmlLines.elementAt(ele);

     if (workLine.indexOf("<?xml version") > -1 ) {  // find the <?xml version statment
       String dtd = "<!DOCTYPE " + docName + " SYSTEM  \"" + systemID +"\">";
       xmlLines.insertElementAt(dtd, ele+1);
       break;
     }

   }

   if (Vector2File(xmlLines, xmlFile) < xmlLines.size())
     error(8, "Not all lines written to " + xmlFile);

 } else
   error(9, "unable to read " + xmlFile);

 return;
}


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ            read a file into a vector                          บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
private Vector File2Vector(String sourceFileName) {

 Vector sourceLines = new Vector();
 BufferedReader sourceFile;

 try {
   sourceFile = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFileName)));
 } catch ( FileNotFoundException ex ) {
   error(10, "Unable to open " + sourceFileName +"; "+ ex.getMessage());
   return null;
 }

 try {
   String workLine;
   while ( (workLine = sourceFile.readLine()) != null )
     sourceLines.addElement(workLine);

 } catch ( IOException ex ) {
   error(11, "IOException reading " + sourceFileName +" : "+ ex.getMessage());
   return null;

 } finally {
   try {
     sourceFile.close();
   } catch ( IOException ex ) {
     error(12, "IOException closing " + sourceFileName +" : "+ ex.getMessage());
     return null;
   }
   sourceFile = null;
 }


 return sourceLines;
}


//ษอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ            Write a file from a vector                         บ
//ศอออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
private int Vector2File(Vector sourceLines, String destFileName) {

 final boolean appendOption = false;
 String crlf = System.getProperty("line.separator");

 int totalLines = sourceLines.size();

 PrintWriter destWriter = null;

 try {
   destWriter = new PrintWriter(new FileWriter(destFileName, appendOption));
 } catch ( IOException ex ) {
   error(13, "Unable to create " + destFileName +" "+ ex.getMessage());
   return 0;
 }

 int lineNo = 0;

 for ( lineNo = 0; lineNo < totalLines; lineNo++ )
   destWriter.write(sourceLines.elementAt(lineNo) + crlf);

 destWriter.close();
 destWriter = null;

 return lineNo;
}



/** Find the first occurrence of a node.
 * Example:
 *   String[] element2Find = {"#document", "websphere", "appserver", "history" };
 *
 * May return a null.
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ           Find the first occurrence of a specific Node                 บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public Node findFirstNode(Node rootnode, String[] node2Find) {

 Node node = findNode(rootnode, node2Find);

 return node;
}


/** Add an event to the Product.xml file.
  * If addHistory is true then if the history node is not present it will be added.
  * The event elements array must contain 12 elements.
  * The time element will be calculated as the current time
  * All elements will have backslashes translated to foward slashs
*/
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                Add an Event to the Product.xml file                    บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public boolean addEvent(Node rootnode, String[] eventEleVars, boolean addHistory, String[] eventHierarchy) {
 // the eventHierarchy should look like:
 //
 //  "#document", "websphere", "appserver", "history". "event" };

 String[] eventEleNames = { "description",
                            "type",
                            "containerType",
                            "installPath",
                            "unInstallScript",
                            "activityLog",
                            "startingVersion",
                            "endingVersion",
                            "source",
                            "status",
                            "errorMessage",
                            "apar",
                            "pmr",
                            "developer"
                           };

 Node n;       // just a working node
 Element ele;  // just a working element

 if (eventEleNames.length != eventEleVars.length) {
   error(14, "in addEvent() --  eventElements array must contain "+ eventEleNames.length +" elements, the passed array contained "+ eventEleVars.length +" elements.");
   return false;
 }

 // String[] elements2Find = {"#document", "websphere", "appserver", "history" };
 String[] elements2Find = new String[ eventHierarchy.length - 1];
 for (int i=0; i < elements2Find.length; i++ ) {
   elements2Find[i] = eventHierarchy[i];
 }



 Node historyNode = findNode(rootnode, elements2Find);

 if (historyNode == null) {

   if (addHistory) {
     historyNode = addHistoryElement(rootnode, eventHierarchy);

     if (historyNode == null) {
       error(16,"in addEvent() --  History element could not added.");
       return false;
     }

   } else {
     error(15,"in addEvent() --  History element could not be located.");
     return false;
   }
 }

 Document doc = historyNode.getOwnerDocument();

 Element eventEle = doc.createElement("event");
 addCR(historyNode,6);
 historyNode.appendChild(eventEle);

 Date Now = new Date();
 SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

 ele = addElement(eventEle, "sqlTime", 8);
 addTextNode(ele, df.format(Now) );

 for (int ndx=0; ndx < eventEleNames.length; ndx++) {
   ele = addElement(eventEle, eventEleNames[ndx], 8);
   addTextNode(ele, eventEleVars[ndx]);
 }

 addCR(eventEle, 6);
 addCR(historyNode,4);

 return true;
}



/** Add a generic event to the an xml file.
  * If addHistory is true then if the history node is not present it will be added.
  * The event elements array is a two dimensional array containing the name of the element
  * and the the data.
  * All elements will have backslashes translated to foward slashs
  * The string "{sqltime}" in any of the Level one elements will be replaced with
  * the current date time in SQL format.
  */

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                Add an Event to the Product.xml file                    บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public boolean addGenericEvent(Node rootnode, String[] elements2Find, String[][] eventEleVars, boolean addHistory) {


 // String[] elements2Find = {"#document", "websphere", "appserver", "history" };

 Node n;       // just a working node
 Element ele;  // just a working element


 Node historyNode = findNode(rootnode, elements2Find);

 if (historyNode == null) {

   if (addHistory) {
     historyNode =  addHistoryElement(rootnode, elements2Find);

     if (historyNode == null) {
       error(17,"in addEvent() --  History element could not added.");
       return false;
     }

   } else {
     error(18,"in addEvent() --  History element could not be located.");
     return false;
   }
 }

 Document doc = historyNode.getOwnerDocument();

 Element eventEle = doc.createElement("event");
 addCR(historyNode,6);
 historyNode.appendChild(eventEle);


 for (int ndx=0; ndx < eventEleVars.length; ndx++) {
   ele = addElement(eventEle, eventEleVars[ndx][0], 8);

   if (eventEleVars[ndx][1].equals("{sqltime}")) {

     Date Now = new Date();
     SimpleDateFormat df  = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
     eventEleVars[ndx][1] = df.format(Now);

   }


   addTextNode(ele, eventEleVars[ndx][1]);
 }

 addCR(eventEle, 6);
 addCR(historyNode,4);

 return true;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                        Add an Element                                บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public Element addElement(Node n, String elementName, int spaces) {

 Document doc = n.getOwnerDocument();
 Element newElement = doc.createElement(elementName);

 if (spaces > 0)
   addCR(n, spaces);

 n.appendChild(newElement);

 return newElement;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     Add a History Element                            บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
private Element addHistoryElement(Node rootNode, String[] elements2Find) {

 //  elements2find should look like "#document", "websphere", "appserver", "history", "event"
 // String[] elements2Find = {"#document", "websphere", "appserver"};

 // here we want to drop off the last two elements

 String[] shortList;

 if (elements2Find.length > 2 ) {
   shortList = new String[elements2Find.length - 2];
   for (int i=0; i < elements2Find.length -2; i++) {
     shortList[i] =  elements2Find[i];
   }
 } else
   shortList = elements2Find;

 Node appServerNode = findNode(rootNode, shortList);

 if (appServerNode == null) {
   StringBuffer sb = new StringBuffer();
   for (int i=0; i < shortList.length; i++ ) {
     sb.append(elements2Find[i]).append(".");
   }

   error(19, "in addHistoryElement() -- Elements "+ sb.toString() +"  was not found.");
   return null;
 }

 Element historyEle =  addElement(appServerNode, "history", 2);
 addCR(appServerNode,2);

 return historyEle;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Add a Text Element                             บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public void addTextNode(Node n, String text){

 Document doc = n.getOwnerDocument();
 Text textEle = doc.createTextNode(text);
 n.appendChild(textEle);

 return;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                Add a Carriage Return and some spaces                 บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public void addCR(Node n, int spaces) {

 char[] oa = {10};  // 0x0A   cr

 String filler = new String(oa);
 StringBuffer sb = new StringBuffer(filler);

 while (spaces > 0) {
   sb.append(" ");
   spaces--;
 }
 filler = sb.toString();

 Document doc = n.getOwnerDocument();
 Text textEle = doc.createTextNode(filler);
 n.appendChild(textEle);

 return;
}


/** Return a #text value.
 * returns null if not found */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                     Reterieve a value                                  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String getValue(Node root, String[] elementNames) {

 String value = null;   // the return string

 if (debug)
   System.out.println("Debug -- entering getValue()");

 Node n = findNode(root, elementNames);

 if (n != null)
   n = n.getFirstChild();  // this should be String value node

 if (n != null)
   value = n.getNodeValue();

 return value;
}


/** Update a #text value.
 * returns null if not found,
 * returns the old value if found.
 */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                        Update a value                                  บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public String updateValue(Node root, String[] elementNames, String newValue) {
 String value = null;   // the old value

 if (debug)
   System.out.println("Debug -- entering updateValue()");

 Node n = findNode(root, elementNames);
 n = n.getFirstChild();  // this should be String value node

 if (n != null) {
   value = n.getNodeValue();
   n.setNodeValue(newValue);
 }

 return value;
}

/** Locate and return a specific node */
//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                         Find a Node                                    บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public Node findNode(Node root, String[] elementNames ) {

 if (debug) {
   System.out.println("Debug -- hierarchy");
   for (int i=0; i < elementNames.length; i++)
     System.out.println("Debug -- "+ i +"  ("+ elementNames[i] +")" );
   System.out.println("");
 }

 Node foundNode = null;

 TreeWalker tw = ((DocumentTraversal) root).createTreeWalker(root, NodeFilter.SHOW_ALL, null, true);
 Node n = tw.getCurrentNode();

 for (int i=0; i < elementNames.length; i++) {
   if (debug )
     System.out.println("Debug --   Searching for "+ elementNames[i]);

   boolean notFound = true;

   while (notFound) {

     if (debug)
       System.out.println("Debug --     inspecting "+ n.getNodeName());

     if (elementNames[i].equals(n.getNodeName())) {
       notFound = false;

       if (debug)
         System.out.println("Debug --       got a match");

       if (i == elementNames.length - 1) {   // are we done?
         foundNode = n;
         // System.out.println("Diag #345 we are Done");
       } else {
         n = tw.firstChild();
         // System.out.println("Diag #346 Child is "+ n);
       }

     }
     n = tw.nextSibling();

     if (n == null)
       return null;
       // notFound = false;
   }
 }

 return foundNode;
}



//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                        Display format attributes                       บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public void DisplayDocAttribs(OutputFormat format, String title) {

 System.out.println("\n" + title);

 System.out.println("getDoctypePublic()     " + format.getDoctypePublic());
 System.out.println("getDoctypeSystem()     " + format.getDoctypeSystem());
 System.out.println("getEncoding()          " + format.getEncoding());
 System.out.println("getIndent()            " + format.getIndent());
 System.out.println("getIndenting()         " + format.getIndenting());
 System.out.println("getLastPrintable()     " + format.getLastPrintable());
 System.out.println("getLineWidth()         " + format.getLineWidth());
 System.out.println("getMediaType()         " + format.getMediaType());
 System.out.println("getMethod()            " + format.getMethod());
 System.out.println("getOmitComments()      " + format.getOmitComments());
 System.out.println("getOmitDocumentType()  " + format.getOmitDocumentType());
 System.out.println("getOmitXMLDeclaration()" + format.getOmitXMLDeclaration());
 System.out.println("getPreserveSpace()     " + format.getPreserveSpace());
 System.out.println("getStandalone()        " + format.getStandalone());
 System.out.println("getVersion()           " + format.getVersion());

 return;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                         Print Node Information                         บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
public void printNode(Node node, int indentValue) {

 // Helper1 HC1 = new Helper1(null, 5);
 boolean removeCR = true,
         noChange = false;

 short  nodeType = node.getNodeType();

 StringBuffer sb = new StringBuffer(indent);

 for (int i=0; i < indentValue; i++) {
   sb.append(" ");
 }

 String filler = sb.toString();

 Document doc = null;
 Element  ele = null;
 NodeList nl  = null;

 // System.out.println("nodeType=" + nodeType +"  "+ getNodeType(node, nodeType));

 nl = node.getChildNodes();
 System.out.println(filler + nodeType + getNodeType(node, nodeType) +" name=" + node.getNodeName() + "  Kids=" + nl.getLength());

 switch (nodeType) {

   case org.w3c.dom.Node.DOCUMENT_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.ELEMENT_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.ATTRIBUTE_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     break;

   case org.w3c.dom.Node.TEXT_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, removeCR);
     break;

   case org.w3c.dom.Node.CDATA_SECTION_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.ENTITY_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     ProcessingInstruction pi =  (ProcessingInstruction) node;
     System.out.println("   Data=" + pi.getData() +", target="+ pi.getTarget());

     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.COMMENT_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     break;

   case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
     DocumentType dt = (DocumentType) node;

     System.out.println(filler +"SysId="+ dt.getSystemId() +" PublicID="+ dt.getPublicId() +" DTD="+ dt.getName());

     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   case org.w3c.dom.Node.NOTATION_NODE:
     displayNodeAttribs(filler, node);
     displayNodeValue(node, filler, noChange);
     processKids(filler, indentValue, nl);
     break;

   default:
     System.out.println("Error --" + filler + nodeType + "-Unknown document type ");
 }

 return;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Process the Children                           บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
String getNodeType(Node node, short nodeType) {

 switch (nodeType) {

   case org.w3c.dom.Node.DOCUMENT_NODE:               return "-DOCUMENT_NODE ";
   case org.w3c.dom.Node.ELEMENT_NODE:                return "-ELEMENT_NODE ";
   case org.w3c.dom.Node.ATTRIBUTE_NODE:              return "-ATTRIBUTE_NODE ";
   case org.w3c.dom.Node.TEXT_NODE:                   return "-TEXT_NODE ";
   case org.w3c.dom.Node.CDATA_SECTION_NODE:          return "-CDATA_SECTION_NODE ";
   case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:       return "-ENTITY_REFERENCE_NODE ";
   case org.w3c.dom.Node.ENTITY_NODE:                 return "-ENTITY_NODE ";
   case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE: return "-PROCESSING_INSTRUCTION_NODE ";
   case org.w3c.dom.Node.COMMENT_NODE:                return "-COMMENT_NODE ";
   case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:          return "-DOCUMENT_TYPE_NODE ";
   case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE:      return "-DOCUMENT_FRAGMENT_NODE ";
   case org.w3c.dom.Node.NOTATION_NODE:               return "-NOTATION_NODE ";
   default:
     return "-Unknown node type ";

 }

}

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Process the Children                           บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
private void processKids(String filler, int indentValue, NodeList nl) {

 for (int i=0; i < nl.getLength(); i++) {

   System.out.println(filler + "Child #" + (i+1) +" of " + nl.getLength());
   Node n = nl.item(i);
   printNode(n, indentValue + 4);
 }
 return;
}

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                         Display Node Value                           บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void displayNodeValue(Node node, String filler, boolean removeCR) {

 char oa = 10;  // 0x0A   cr

 String value = node.getNodeValue();

 if (removeCR) {
   value = value.replace(oa, '.');  // remove CR
 }

 System.out.println(filler + " Value=(" + value + ")");

 return;
}

//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Display Node attributes                        บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void displayNodeAttribs(String filler, Node node) {

 if (node.hasAttributes()) {

   NamedNodeMap attribs =  node.getAttributes();

   for (int i=0; i < attribs.getLength(); i++ ) {
     Node n = attribs.item(i);
     System.out.println(filler + "Att#"+i+ " " + n.getNodeName() + " = " + n.getNodeValue());
   }
 } else {
   System.out.println(filler + " No Attributes");
 }

 return;
}


//ษออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออป
//บ                       Error                                          บ
//ศออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออออผ
void error(int errNum, String msg) {

 String errMsg;

 if (errNum == 0) // errNum of zero denotes not to insert error prefix stuff
   errMsg = msg;
 else
   errMsg = "Error "+ errNum +" in Dom2Spt -- "+ msg;

if (msgVector == null)
  System.err.println(errMsg);
else
  msgVector.add(errMsg);

 return;
}


}
