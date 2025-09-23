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


package com.ibm.prs.common.reports.api.impl;

import java.util.Map;
import java.util.Set;

import com.ibm.prs.common.exception.PRSApiException;
import com.ibm.prs.common.reports.api.PRSXmlResultReader;
import com.ibm.prs.common.reports.api.util.PRSXmlResultParser;

/**
 * PRSXmlResultReaderImpl implements PRSXmlResultReader.
 * 
 * 
 * @author jichen
 * @version 1.0
 *
 */

public class PRSXmlResultReaderImpl implements PRSXmlResultReader {

    PRSXmlResultParser parser = null; 
    
    /**
     * Constructor for the <code>PRSXmlResultReaderImpl</code> 
     * 
     * @param xmlFileName the fully qualified path and file name for the result XML file
     * @param schemaFileName  the fully qualified path and file name for the result XML schema definition
     * @throws PRSApiException 
     */
    public PRSXmlResultReaderImpl(String xmlFileName, String schemaFileName) 
        throws PRSApiException
    {
        parser = new PRSXmlResultParser(xmlFileName, schemaFileName);
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#setXmlFile(String)
     */
    public void setXmlFile(String xmlFileName) throws PRSApiException {
        parser.setXmlFile(xmlFileName);       
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getAggregatedResults()
     */
    public Set<Map<String, String>> getAggregatedResults() throws PRSApiException
    {
        return parser.getAggregatedResults();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getDetailedResults()
     */
    public Map<String,Set<Map<String, String>>> getDetailedResults() throws PRSApiException
    {
        return parser.getDetailedResults();
    }

   /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getMachineInfo()
     */
    public Map<String, String> getMachineInfo() throws PRSApiException
    {
        return parser.getMachineInfo();
    }
    
    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getPRSInfo()
     */
    public Map<String, String> getPRSInfo() throws PRSApiException
    {
        return parser.getPRSInfo();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getProductInfo()
     */
    public Set<Map<String, String>> getProductInfo() throws PRSApiException
    {
        return parser.getProductInfo();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getUserInfo()
     */
    public Map<String, String> getUserInfo() throws PRSApiException
    {
        return parser.getUserInfo();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasAggregatedResults()
     */
    public boolean hasAggregatedResults() throws PRSApiException
    {
        return parser.hasAggregatedResults();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasDetailedResults()
     */
    public boolean hasDetailedResults() throws PRSApiException
    {
        return parser.hasDetailedResults();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasMachineInfo()
     */
    public boolean hasMachineInfo() throws PRSApiException
    {
        return parser.hasMachineInfo();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasPRSInfo() 
     */
    public boolean hasPRSInfo() throws PRSApiException
    {
        return parser.hasPRSInfo();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasProductInfo()
     */
    public boolean hasProductInfo() throws PRSApiException
    {
        return parser.hasProductInfo();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasScanResult()
     */
    public boolean hasScanResult() throws PRSApiException
    {
        return parser.hasScanResult();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#hasUserInfo()
     */
    public boolean hasUserInfo() throws PRSApiException
    {
        return parser.hasUserInfo();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#isScanPassed()
     */
    public boolean isScanPassed() throws PRSApiException
    {
        return parser.isScanPassed();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#isValidXml()
     */
    public boolean isValidXml() throws PRSApiException
    {
        return parser.validate();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getSchemaVersion()
     */ 
    public String getSchemaVersion() throws PRSApiException {
        return parser.getSchemaVersion();
    }

    /**
     * @see com.ibm.prs.common.reports.api.PRSXmlResultReader#getFailedResults()
     */
    public Map<String, Set<Map<String, String>>> getFailedResults() throws PRSApiException {
        return parser.getFailedResults();
    }
    
}
