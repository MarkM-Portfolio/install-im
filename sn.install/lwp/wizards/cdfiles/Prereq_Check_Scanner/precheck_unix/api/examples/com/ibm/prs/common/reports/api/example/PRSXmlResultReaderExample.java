/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.prs.common.reports.api.example;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ibm.prs.common.exception.PRSApiException;
import com.ibm.prs.common.reports.api.PRSXmlResultReader;
import com.ibm.prs.common.reports.api.impl.PRSXmlResultReaderImpl;

/**
 * A class serves as an exmaple on how to process and query PRS XML results, 
 * by using PRS xml result query API - PRSXmlResultReader
 * 
 * @author jichen
 * @version 1.0
 */

public class PRSXmlResultReaderExample {

    private String xmlFileName = null;
    private String schemaFileName = null;
    
    private PRSXmlResultReader prsXmlApi = null;
    
    /**
     * Main method of PRSXmlResultReaderExample to demonstrate how to process and query PRS XML results
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
            System.out.println("Usage: java PRSXmlResultReader <xmlFileName> <schemaFileName>");
            System.exit(-1);
        }
        
        PRSXmlResultReaderExample example = new PRSXmlResultReaderExample();
        example.xmlFileName = args[0].trim();
        example.schemaFileName = args[1].trim();
        
        try {
            example.init();
        } catch (PRSApiException e) {
            System.err.println("ERROR: PRSApiException during instantiation - " + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(-1);
        } 
        
        /*
         * 1) First validate the XML file, before reading and parsing it
         */
        example.validateXml();

        /*
         * 2) Get XML schema version
         */
        example.getSchemaVersion();
        
        /**
         * 2) getting XML result info
         */
        System.out.println("================= Scan Results ================");
        //a) overall scan result
        example.getOverallScanResult();
       
        //b) PRS info
        example.getPRSinfo();
        
        //c) Scanned machine info
        example.getScannedMachineInfo();
        
        //d) User info
        example.getUserInfo();
        
        //e) Scanned prodcut(s) info
        example.getProductInfo();
        
        //f) detailed results info
        example.getDetailedScanResults();
        
        //g) aggregated results info
        example.getAggregatedResults();
        
        //g) aggregated results info
        example.getFailedResults();
                
        System.out.println("================= End of Scan Results ================");
    }

    private void getFailedResults() {
        try {
            if (!prsXmlApi.isScanPassed())
            {
                Map<String, Set<Map<String,String>>> failedResults = prsXmlApi.getFailedResults();
                int numOfFailedProd = failedResults.size();
                System.out.println("8) Failed scan results - " + numOfFailedProd + " product(s) failed");
                Iterator<String> keyIter = failedResults.keySet().iterator();
                while (keyIter.hasNext())
                {
                    String productCode = (String)keyIter.next();
                    System.out.println("==Failed in product/component - " + productCode + "==");
                    Set<Map<String,String>> prodfailedResultSet = failedResults.get(productCode);
                    System.out.println("==total number failed checks for this product/component - " + prodfailedResultSet.size() + "==");  
                    printSetContent(prodfailedResultSet);
                }
            } else
                System.out.println("8) No failed scan results found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * get and print out aggregated scan result from XML results
     */
    private void getAggregatedResults() {
        try {
            if (prsXmlApi.hasAggregatedResults())
            {
                Set<Map<String,String>> aggregatedResults = prsXmlApi.getAggregatedResults();
                int numOfAggProps = aggregatedResults.size();
                System.out.println("7) Aggregated results - " + numOfAggProps + " properties checked");
                printSetContent(aggregatedResults);
            } else
                System.out.println("7) No aggregated results found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get and print out detailed scan result from XML results
     */
    private void getDetailedScanResults() {
        try {
            if (prsXmlApi.hasDetailedResults())
            {
                Map<String, Set<Map<String,String>>> detailedResults = prsXmlApi.getDetailedResults();
                int numOfProduct = detailedResults.size();
                System.out.println("6) Detailed results - " + numOfProduct + " product(s) checked");
                Iterator<String> keyIter = detailedResults.keySet().iterator();
                while (keyIter.hasNext())
                {
                    String productCode = (String)keyIter.next();
                    System.out.println("==Product detail of " + productCode + "==");
                    Set<Map<String,String>> prodDetailResultSet = detailedResults.get(productCode);
                    System.out.println("==total properties checked for " + productCode + ": " + prodDetailResultSet.size() + "==");
                    printSetContent(prodDetailResultSet);
                }
            } else
                System.out.println("6) No detailed results found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Get and print user info from XML results
     */
    private void getUserInfo() {
        try {
            if (prsXmlApi.hasUserInfo())
            {
                Map<String,String> userInfo = prsXmlApi.getUserInfo();
                System.out.println("4) User information:");
                printMapContent(userInfo);
            }  else
                System.out.println("4) No user info found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
    }
     
    /**
     * Get and print scanned product(s) info from XML results 
     */
    private void getProductInfo()
    {
        try {
            if (prsXmlApi.hasProductInfo())
            {
                Set<Map<String,String>> productInfo = prsXmlApi.getProductInfo();
                int numOfProducts = productInfo.size();
                System.out.println("5) Product info - " + numOfProducts + " products scanned");
                printSetContent(productInfo);
            }  else
                System.out.println("5) No product info found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Get and print scanned machine info from XML results 
     */
    private void getScannedMachineInfo() {
        try {
            if (prsXmlApi.hasMachineInfo())
            {
                Map<String,String> machineInfo = prsXmlApi.getMachineInfo();
                System.out.println("3) Scanned machine information:");
                printMapContent(machineInfo);
            }  else
                System.out.println("3) No machine inf found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Get and print out PRS information from XML result file
     */
    private void getPRSinfo() {
        try {
            if (prsXmlApi.hasPRSInfo())
            {
                Map<String,String> prsInfo = prsXmlApi.getPRSInfo();
                System.out.println("2) PRS information:");
                printMapContent(prsInfo);
            }  else
                System.out.println("2) No PRS info found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Get and print out overall PRS scan result from XML result file
     */
    private void getOverallScanResult() {
        try {
            if (prsXmlApi.hasScanResult())
            {
                Boolean scanPassed = prsXmlApi.isScanPassed();
                if (scanPassed)
                    System.out.println("1) Overall PRS scan result: PASS");
                else
                    System.out.println("1) Overall PRS scan result: FAIL");
            }  else
                System.out.println("1) No overall result info found");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Get and print out XML schema version from XML result file
     */
    private void getSchemaVersion() {
        String sVersion;
        try {
            sVersion = prsXmlApi.getSchemaVersion();
            System.out.println("Schema version - "+sVersion+"\n");
        } catch (PRSApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validate the input XML with schema file 
     */
    private void validateXml() {
        boolean isValidated;
        try {
            isValidated = prsXmlApi.isValidXml();
            if (!isValidated)
            {
                System.err.println("ERROR: " + this.xmlFileName + " failed validation. Existing.");
                System.exit(-1);
            } else {
                System.out.println("INFO: " + this.xmlFileName + " successfully passed validation.");
            }  

        } catch (PRSApiException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initiate PRS xml result reader api  
     * 
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private void init() throws PRSApiException
    {     

        prsXmlApi = new PRSXmlResultReaderImpl(this.xmlFileName, this.schemaFileName);
    }

    
    //-------------------Helper Methods-----------------//
    /**
     * Process and print out input map content
     * 
     * @param aMap
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
     * Process and print out input set content
     * 
     * @param aSet each element of the set is a map
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

}
