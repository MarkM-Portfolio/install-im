/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2009, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.prs.common.reports.api.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.prs.common.exception.PRSApiException;

/**
 * PRSXmlResultParser class provides methods to parse the Prerequisite Scanner 
 * results XML file by using the XML schema definition. It also provides 
 * methods to get the parsed information.
 * 
 * @author jichen
 * @version 1.0
 * 
 */
public class PRSXmlResultParser extends DefaultHandler{

    final private String aggKey = "AGGREGATED";
    
    private String xmlFileName = null;
    private String schemaFileName = null;
    private SAXParserFactory factory = null;
    private SAXParser saxParser = null;
    
    private StringBuffer elementValue = null;
    
    // variable to indicate whether it is processing certain section of the xml result
    private boolean processingPRSInfo = false;
    private boolean processingMachineInfo = false;
    private boolean processingUserInfo = false;
    private boolean processingProductInfo = false;
    private boolean processingDetailedResults = false;
//    private boolean processingDetailedProdResultsElement = false;
    private boolean processingAggregatedResults = false;
    private boolean processingErrorsInfo = false;
    private boolean failedResultFound = false;
    private boolean isValidXml = false;
    
    // variable to store XML result info
    private String schemaVersion = "0";
    private boolean hasOverallResult = false;
    private Map<String, String> prsInfoMap = new LinkedHashMap<String, String>(5);
    private Map<String, String> machineInfoMap = new LinkedHashMap<String, String>(5);
    private Map<String, String> userInfoMap = new LinkedHashMap<String, String>(5);
    private Set<Map<String, String>> productInfoSet = new LinkedHashSet<Map<String, String>>(5);
    private Map<String, Set<Map<String,String>>> detailedResultMap = new LinkedHashMap<String, Set<Map<String,String>>>(5);
    private Set<Map<String, String>> aggregatedResultSet = new LinkedHashSet<Map<String, String>>(5);    
    private boolean isScanPassed = false;    
//    private String usageStatement = null;
    private Set<Map<String, String>> errorSet = new LinkedHashSet<Map<String, String>>(5);
    private Map<String, Set<Map<String,String>>> failedResultMap = new LinkedHashMap<String, Set<Map<String,String>>>(5);
    
    private Map<String, String> resultElementMap = new LinkedHashMap<String, String>(5);
    private Map<String, String> productElementMap = new LinkedHashMap<String, String>(5);
    private Map<String, String> errorElementMap = new LinkedHashMap<String, String>(5);
    private Set<Map<String, String>> detailedResultSetElement = new LinkedHashSet<Map<String, String>>(13); 
    private String pCodeInDetailResult = null;
    private Set<Map<String, String>> failedResultSetElement = new LinkedHashSet<Map<String, String>>(13); 
    
    /**
     * Constructor for the <code>PRSXmlResultParser</code> class.
     * 
     * @param xmlFileName the name of the result XML file
     * @param schemaFileName the name of the XML schema definition file
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public PRSXmlResultParser (String xmlFileName, String schemaFileName) throws PRSApiException
    {
        this.xmlFileName = xmlFileName;
        this.schemaFileName = schemaFileName;

        this.factory = SAXParserFactory.newInstance();
        try {
            this.saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            System.err.println("(PRSXmlResultParser constructor)ERROR - ParserConfigurationException: "+e.getLocalizedMessage());
            e.printStackTrace();
            PRSApiException pe = new PRSApiException(e.getLocalizedMessage(), e.getCause());
            throw pe;
        } catch (SAXException e) {
            System.err.println("(PRSXmlResultParser constructor)ERROR - SAXException: "+e.getLocalizedMessage());
            e.printStackTrace();
            PRSApiException pe = new PRSApiException(e.getLocalizedMessage(), e.getCause());
            throw pe;
        }
        
        // Instantiate validator
        isValidXml = validateXml();
        if (isValidXml)
        {            
            this.parse();            
        } else
        {
            PRSApiException pe = new PRSApiException("Invalid XML: "+xmlFileName);
            throw pe;
        }
    }

    // ================= APIs =================== //
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#setXmlFile(String)
     */
    public void setXmlFile(String xmlName) throws PRSApiException{
        this.xmlFileName = xmlName;
        isValidXml = validateXml();
        if (isValidXml)
        {            
            this.parse();            
        }
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#isValidXml()
     */
    public boolean validate() throws PRSApiException    {
           
        return isValidXml;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasAggregatedResults()
     */
    public String getSchemaVersion() throws PRSApiException
    {
        return schemaVersion;
    }

    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasScanResult() 
     */
    public boolean hasScanResult() throws PRSApiException
    {
        return hasOverallResult;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#isScanPassed()
     */
    public boolean isScanPassed() throws PRSApiException
    {
        return isScanPassed;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasPRSInfo()
     */
    public boolean hasPRSInfo() throws PRSApiException
    {
        if (prsInfoMap==null || prsInfoMap.size()==0)
            return false;
        
        return true;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getPRSInfo()
     */
    public Map<String,String> getPRSInfo() throws PRSApiException
    {
        return prsInfoMap;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasMachineInfo()
     */
    public boolean hasMachineInfo() throws PRSApiException
    {
        if (machineInfoMap==null || machineInfoMap.size()==0)
            return false;
        
        return true;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getMachineInfo() 
     */
    public Map<String,String> getMachineInfo() throws PRSApiException
    {
        return machineInfoMap;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasUserInfo()
     */
    public boolean hasUserInfo() throws PRSApiException
    {
        if (userInfoMap==null || userInfoMap.size()==0)
            return false;
        
        return true;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getUserInfo()
     */
    public Map<String,String> getUserInfo() throws PRSApiException
    {
        return userInfoMap;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasProductInfo()
     */
    public boolean hasProductInfo() throws PRSApiException
    {
        if (productInfoSet==null || productInfoSet.size()==0)
            return false;
        
        return true;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getProductInfo()
	 */
    public Set<Map<String,String>> getProductInfo () throws PRSApiException
    {
        return productInfoSet; 
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasDetailedResults()
     */
    public boolean hasDetailedResults() throws PRSApiException
    {
        if (detailedResultMap==null || detailedResultMap.size()==0)
            return false;
        
        return true;
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getDetailedResults()
     */
    public Map<String,Set<Map<String,String>>> getDetailedResults () throws PRSApiException
    {
        return detailedResultMap; 
    }
    
//    /**
//     * See detail in {@link com.ibm.prs.common.reports.api.PRSXmlResultReader#hasUsageStatement() hasUsageStatement()}
//     */
//    public boolean hasUsageStatement () throws PRSApiException
//    {
//        if (usageStatement == null)
//            return false;
//        
//        return true; 
//    }
    
//    /**
//     * See detail in {@link com.ibm.prs.common.reports.api.PRSXmlResultReader#getUsageStatement() getUsageStatement()}
//     */
//    public String getUsageStatement () throws PRSApiException
//    {
//        return usageStatement; 
//    }
    
    /**
     * @see #hasError()
     */
  public boolean hasError () throws PRSApiException
    {
        if (errorSet==null || errorSet.size()==0)
            return false;
        
        return true; 
    }
    
    /**
     * @see #getErrors()
     */
    public Set<Map<String,String>> getErrors () throws PRSApiException
    {
        return errorSet; 
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasAggregatedResults()
     */
    public boolean hasAggregatedResults () throws PRSApiException
    {
        if (aggregatedResultSet==null || aggregatedResultSet.size()==0)
            return false;
        
        return true; 
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getAggregatedResults()
     */
    public Set<Map<String,String>> getAggregatedResults () throws PRSApiException
    {
        return aggregatedResultSet; 
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getFailedResults()
     */
    public Map<String,Set<Map<String, String>>> getFailedResults() 
    {
        
        return failedResultMap;
    }
    
    // ================= End of APIs =================== //
    
    // ================= DefaultHandler methods =================== //
    
    /**
     *  Overrides {@link org.xml.sax.helpers.DefaultHandler#startDocument() }
     */
    public void startDocument()
    {
//        System.out.println("(PRSXmlResultParser.startDocument)INFO: " + "start to parse " + xmlFileName);
    }
    
    /**
     *  Overrides {@link org.xml.sax.helpers.DefaultHandler#endDocument() }
     */
    public void endDocument()
    {
//        System.out.println("(PRSXmlResultParser.startDocument)INFO: " + "finish parsing " + xmlFileName);
    }

    
    /**
     * Processes the beginning of each element in the result XML file.
     * 
     * <p>Overrides {@link org.xml.sax.helpers.DefaultHandler#startElement(String, String, String, Attributes) }</p>
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {        
        if (qName.equalsIgnoreCase("Results"))
        {
            for (int i=0; i<attributes.getLength(); i++)
            {
                String attrName = attributes.getQName(i);
//                System.out.println("(PRSXmlResultParser.startElement)DEBUG: attrName=" + attrName);
                if (attrName.equalsIgnoreCase("SchemaVersion"))
                {
                    schemaVersion = attributes.getValue(i);
                    break;
                }
            }
        } else if (qName.equalsIgnoreCase("PRSInfo"))
            processingPRSInfo = true;
        else if (qName.equalsIgnoreCase("MachineInfo"))
            processingMachineInfo = true;
        else if (qName.equalsIgnoreCase("UserInfo"))
            processingUserInfo = true;
        else if (qName.equalsIgnoreCase("ProductInfo"))
            processingProductInfo = true;
        else if (qName.equalsIgnoreCase("DetailedResults"))
            processingDetailedResults = true;
//        else if (qName.equalsIgnoreCase("DetailedProductResultsElement"))
//            processingDetailedProdResultsElement = true;
        else if (qName.equalsIgnoreCase("AggregatedResults"))
            processingAggregatedResults = true;
        else if (qName.equalsIgnoreCase("Errors"))
            processingErrorsInfo = true;
        
        elementValue = new StringBuffer();
    }


    /**
     * Processes the end of each element in result XML file, building up result XML information.
     * 
     * <p>Overrides {@link org.xml.sax.helpers.DefaultHandler#endElement(String, String, String) }</p>
     */
    public void endElement(String uri, String localName, String qName)
    {        
//        System.out.println("(PRSXmlResultParser.endElement)DEBUG: element=" + qName + "; value=" + elementValue);
        
        if (processingPRSInfo==true)
        {
            if (!qName.equalsIgnoreCase("PRSInfo"))
                prsInfoMap.put(qName, elementValue.toString());
        } else if (processingMachineInfo == true)
        {
            //System.out.println("(PRSXmlResultParser.endElement)DEBUG: processing machine info - " + 
            //        qName + ":" + elementValue);
            if (!qName.equalsIgnoreCase("MachineInfo"))
                machineInfoMap.put(qName, elementValue.toString());
        } else if (processingUserInfo) {
            if (!qName.equalsIgnoreCase("UserInfo"))
                userInfoMap.put(qName, elementValue.toString());
        } else if (processingProductInfo==true)
        {
            if (qName.equalsIgnoreCase("ProductElement"))
            {
                //end of result element, adding the constructed result element map to detailed result set
                productInfoSet.add(productElementMap);
                productElementMap = new LinkedHashMap<String, String>(5);
            } else if (!qName.equalsIgnoreCase("ProductInfo"))
            {
                //processing detailed results
                //must be processing an item of a result element, construct the map
                productElementMap.put(qName, elementValue.toString());
            }
        } else if (processingDetailedResults)
        {
            if (qName.equalsIgnoreCase("productCode"))
            {
                pCodeInDetailResult = elementValue.toString();
            }
            else if (qName.equalsIgnoreCase("DetailedProductResultsElement"))
            {
                detailedResultMap.put(pCodeInDetailResult, detailedResultSetElement);
                detailedResultSetElement = new LinkedHashSet<Map<String, String>>(13);
                
                //if failed scan result included for this product scan, add it to failed result map
                if (failedResultSetElement.size()!=0 && failedResultSetElement!=null)
                {
                    failedResultMap.put(pCodeInDetailResult, failedResultSetElement);
                    failedResultSetElement = new LinkedHashSet<Map<String, String>>(13);
                }

                pCodeInDetailResult = null;
            } else if (qName.equalsIgnoreCase("ResultElement"))
            {
                //end of result element, adding the constructed result element map to detailed result set
                detailedResultSetElement.add(resultElementMap);
                //if this result element is failed, add it to failed set
                if (failedResultFound)
                {
//                    System.out.println("(PRSXmlResultParser.endElement)DEBUG: ****end of failed scan result found. add to map");
                    failedResultSetElement.add(resultElementMap);
                    failedResultFound = false;
                }
                //reset resultElementMap
                resultElementMap = new LinkedHashMap<String, String>(5);
//                processingResultElement = false;
            } else if (!qName.equalsIgnoreCase("DetailedResults"))
            {
                //processing detailed results
                //must be processing an item of a result element, construct the map
                String qValue = elementValue.toString();
                resultElementMap.put(qName, qValue);
                //mark failed result found
                if (qName.equalsIgnoreCase("Result") && qValue.equalsIgnoreCase("FAIL"))
                {
                    failedResultFound = true;
//                    System.out.println("(PRSXmlResultParser.endElement)DEBUG: **failed scan result found");
                }
            }
        } else if (processingAggregatedResults)
        {
            if (qName.equalsIgnoreCase("resultElement"))
            {
                //end of result element, adding the constructed result element map to detailed result set
                aggregatedResultSet.add(resultElementMap);
                
                //if this result element is failed, add it to failed set
                if (failedResultFound)
                {
                    failedResultSetElement.add(resultElementMap);
                    failedResultFound = false;
                }

                resultElementMap = new LinkedHashMap<String, String>(5);
//                processingResultElement = false;
            } else if (qName.equalsIgnoreCase("AggregatedResults"))
            {
                //if failed scan result included for this product scan, add it to failed result map
                if (failedResultSetElement.size()!=0 && failedResultSetElement!=null)
                {
                    failedResultMap.put(aggKey, failedResultSetElement);
                    failedResultSetElement = new LinkedHashSet<Map<String, String>>(13);
                }
            } else
            {
                //processing detailed results
                //must be processing an item of a result element, construct the map
                String qValue = elementValue.toString();
                resultElementMap.put(qName, qValue);
                //mark failed result found
                if (qName.equalsIgnoreCase("Result") && qValue.equalsIgnoreCase("FAIL"))
                {
                    failedResultFound = true;
                }
            }
        } else if (qName.equalsIgnoreCase("OverallResult"))
        {
            if (elementValue.toString().equalsIgnoreCase("PASS"))
            {
                isScanPassed = true;
                hasOverallResult = true;
            } 
            else if (elementValue.toString().equalsIgnoreCase("FAIL"))
            {
                isScanPassed = false;
                hasOverallResult = true;
            }
        } else if (processingErrorsInfo)
        {
            //processing errors
            //add current error msg to the error set
            if (qName.equalsIgnoreCase("ErrorElement"))
            {
                errorSet.add(errorElementMap);
                errorElementMap = new LinkedHashMap<String, String>(5);
            } else if (!qName.equalsIgnoreCase("Errors"))
            {
//                System.out.println("(PRSXmlResultParser.endElement)DEBUG: adding "+qName+" and " + elementValue);
                errorElementMap.put(qName, elementValue.toString());
            }
        } 
//        else if (qName.equalsIgnoreCase("Usage"))
//        {
//            usageStatement = elementValue.toString();
//        }
        
        elementValue = null;

        if (qName.equalsIgnoreCase("PRSInfo"))
            processingPRSInfo = false;
        else if (qName.equalsIgnoreCase("MachineInfo"))
            processingMachineInfo = false;
        else if (qName.equalsIgnoreCase("UserInfo"))
            processingUserInfo = false;
        else if (qName.equalsIgnoreCase("ProductInfo"))
            processingProductInfo = false;
        else if (qName.equalsIgnoreCase("DetailedResults"))
            processingDetailedResults = false;
//        else if (qName.equalsIgnoreCase("DetailedProductResultsElement"))
//            processingDetailedProdResultsElement = false;
        else if (qName.equalsIgnoreCase("AggregatedResults"))
            processingAggregatedResults = false;
        else if (qName.equalsIgnoreCase("Errors"))
            processingErrorsInfo = false;
        
    }
    
    /**
     * Adds data to a buffer from the results XML file.
	 * <p>Overrides  {@link org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)}</p>
     * 
     */
    public void characters(char buf[], int offset, int len)
        throws SAXException{
        String s = new String(buf, offset, len);
        if(elementValue !=null){
            elementValue.append(s);
        }
    }
    
    // ================= End of DefaultHandler methods =================== //
    
    // ================= Helper methods =================== //
    
    /**
     *  Parsing input results XML file
     */
    
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#isValidXml() 
     */
    private boolean validateXml() throws PRSApiException
    {
        boolean isValidated = false;
        
        // Instantiate validator
        PRSXmlResultValidator validator = new PRSXmlResultValidator(xmlFileName, schemaFileName);
        
        boolean isXmlWellFormated = false;
        boolean isXmlComplyWithSchema = false;
        isXmlWellFormated = validator.isXMLWellFormatted();
        isXmlComplyWithSchema = validator.isXmlValidatedAgainstSchema();
        
        if (isXmlWellFormated && isXmlComplyWithSchema)
            isValidated = true;
        
        return isValidated;
    }
    
    private void parse() throws PRSApiException
    {
        File xmlFile = new File(xmlFileName);
        try {
            saxParser.parse(xmlFile, this);
        } catch (SAXException e) {
            System.err.println("(PRSXmlResultParser.parse)ERROR: SAXException while parsing " + xmlFile + " - " + e.getLocalizedMessage());
            e.printStackTrace();
            PRSApiException pe = new PRSApiException(e.getLocalizedMessage(), e.getCause());
            throw pe;
        } catch (IOException e) {
            System.err.println("(PRSXmlResultParser.parse)ERROR: IOException while parsing " + xmlFile + " - " + e.getLocalizedMessage());
            e.printStackTrace();
            PRSApiException pe = new PRSApiException(e.getLocalizedMessage(), e.getCause());
            throw pe;
        }
    }
    
    /**
     * Processes and prints out input map content
     * 
     * @param aMap a map that can be printed out
     */
    private void printMapContent(Map<String, String> aMap) 
    {
        Set<String> keys = aMap.keySet();
        Iterator<String> keyIter = keys.iterator();
        while (keyIter.hasNext())
        {
            String key = (String)keyIter.next();
            System.out.println("\t" + key + ": " + aMap.get(key));
        }        
    }
    
    /**
     * Processes and prints out the input set content
     * 
     * @param aSet a map in each element of the set
     */
    private void printSetContent(Set<Map<String, String>> aSet) 
    {
        Iterator<Map<String,String>> productIter = aSet.iterator();
        for (int i=0; i<aSet.size(); i++)
        {
            System.out.println("---------Item " + (i+1) + "-----------");
            Map<String,String> product = (Map<String,String>)productIter.next();
            printMapContent(product);            
        }        
    }
    
    // ================= End of Helper methods =================== //

    //===========Main Method============//
    /**
     * Runs the <code>PRSXmlResultParser</code> utility, requiring 2 arguments to be passed to it.
     * 
     * @param args two required arguments, as follows:
     * <ul>
     * <li><code>args[0]</code> - the fully qualified path and file name for the result XML file</li>
     * <li><code>args[1]</code> - the fully qualified path and file name for the result XML schema definition</li>
     * </ul>      
     */
    public static void main(String[] args) {
        if (args.length != 2)
        {
            System.out.println("Usage: java PRSXmlResultParser <xmlFileName> <schemaFileName>");
            System.exit(-1);
        } 
        
        PRSXmlResultParser thisReader = null;
        
        try {
            thisReader = new PRSXmlResultParser(args[0], args[1]);
        } catch (PRSApiException e) {
            System.err.println("ERROR: PRSApiException during instantiation - " + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(-1);
        } 
        
        /**
         * 1) First validate the XML file, before reading and parsing it
         */
        boolean isValidated = false;
        try {
            isValidated = thisReader.validate();
        } catch (PRSApiException e) {
            System.err.println("ERROR: PRSApiException while validating xml file - " + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        if (!isValidated)
        {
            System.err.println("ERROR: " + thisReader.xmlFileName + " failed validation. Existing.");
            System.exit(-1);
        } else {
            System.out.println("INFO: " + thisReader.xmlFileName + " successfully passed validation.");
        }
        
        /**
         * 2) parsing the XML file
         */
//        thisReader.parse();

        /**
         * 3) getting XML result info
         */
        System.out.println("================= Scan Results ================");
        //a) overall scan result
        if (thisReader.hasOverallResult)
        {
            Boolean scanPassed = false;
            try {
                scanPassed = thisReader.isScanPassed();
            
                if (scanPassed)
                    System.out.println("1) Overall PRS scan result: PASS");
                else
                    System.out.println("1) Overall PRS scan result: FAIL");
            } catch (PRSApiException e) {
                e.printStackTrace();
            }
        }  else
            System.out.println("1) No overall result info found");
        
        //b) PRS info
        boolean hasPRSInfo = false;
        try {
            hasPRSInfo = thisReader.hasPRSInfo();
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        if (hasPRSInfo)
        {
            Map<String, String> prsInfo;
            try {
                prsInfo = thisReader.getPRSInfo();

                System.out.println("2) PRS information:");
                thisReader.printMapContent(prsInfo);
            } catch (PRSApiException e) {
                e.printStackTrace();
            }
        }  else
            System.out.println("2) No PRS info found");
        
        //c) Machine info
        try {
            if (thisReader.hasMachineInfo())
            {
                Map<String,String> machineInfo = thisReader.getMachineInfo();
                System.out.println("3) Scanned machine information:");
                thisReader.printMapContent(machineInfo);
            }  else
                System.out.println("3) No machine inf found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
        //d) User info
        try {
            if (thisReader.hasUserInfo())
            {
                Map<String,String> userInfo = thisReader.getUserInfo();
                System.out.println("4) User information:");
                thisReader.printMapContent(userInfo);
            }  else
                System.out.println("4) No user info found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
        //e) Product info
        try {
            if (thisReader.hasProductInfo())
            {
                Set<Map<String,String>> productInfo = thisReader.getProductInfo();
                int numOfProducts = productInfo.size();
                System.out.println("5) Product info - " + numOfProducts + " products scanned");
                thisReader.printSetContent(productInfo);
            }  else
                System.out.println("5) No product info found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
        //f) detailed results info
        try {
            if (thisReader.hasDetailedResults())
            {
                Map<String, Set<Map<String,String>>> detailedResults = thisReader.getDetailedResults();
                int numOfProps = detailedResults.size();
                System.out.println("6) Detailed results - " + numOfProps + " product(s) checked");
                Iterator<String> keyIter = detailedResults.keySet().iterator();
                while (keyIter.hasNext())
                {
                    String productCode = (String)keyIter.next();
                    System.out.println("==Product detail of " + productCode + "==");
                    Set<Map<String,String>> prodDetailResultSet = detailedResults.get(productCode);
                    System.out.println("==total properties checked for " + productCode + ": " + prodDetailResultSet.size() + "==");
                    thisReader.printSetContent(prodDetailResultSet);
                }
            } else
                System.out.println("6) No detailed results found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
        //g) aggregated results info
        try {
            if (thisReader.hasAggregatedResults())
            {
                Set<Map<String,String>> aggregatedResults = thisReader.getAggregatedResults();
                int numOfAggProps = aggregatedResults.size();
                System.out.println("7) Aggregated results - " + numOfAggProps + " properties checked");
                thisReader.printSetContent(aggregatedResults);
            } else
                System.out.println("7) No aggregated results found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
//        //h) Usage statement
//        try {
//            if (thisReader.hasUsageStatement())
//                System.out.println("8) UsageStatement info - " + thisReader.getUsageStatement());
//            else
//                System.out.println("8) No UsageStatement");
//        } catch (PRSApiException e) {
//            e.printStackTrace();
//        }
//        
        //i) Error info
        try {
            if (thisReader.hasError())
            {
                Set<Map<String, String>> reportedErrorSet = thisReader.getErrors();
                int numOfErrors = reportedErrorSet.size();
                System.out.println("8) Error info - " + numOfErrors + " errors found");
                thisReader.printSetContent(reportedErrorSet);
            } else
                System.out.println("8) No error found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
        //j) schemaVersion
        String sVersion = null;
        try {
            sVersion = thisReader.getSchemaVersion();
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        System.out.println("9) Schema version - "+sVersion);
        
        //k) failed scan results
        try {
            if (!thisReader.isScanPassed())
            {
                Map<String, Set<Map<String,String>>> failedResults = thisReader.getFailedResults();
                int numOfProps = failedResults.size();
                System.out.println("10) Failed scan results - " + numOfProps + " product(s) failed");
                Iterator<String> keyIter = failedResults.keySet().iterator();
                while (keyIter.hasNext())
                {
                    String productCode = (String)keyIter.next();
                    System.out.println("==Product detail of " + productCode + "==");
                    Set<Map<String,String>> prodfailedResultSet = failedResults.get(productCode);
                    System.out.println("==total number failed checks for this product/component - " + prodfailedResultSet.size() + "==");
                    thisReader.printSetContent(prodfailedResultSet);
                }
            } else
                System.out.println("10) No failed scan results found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
    }


}