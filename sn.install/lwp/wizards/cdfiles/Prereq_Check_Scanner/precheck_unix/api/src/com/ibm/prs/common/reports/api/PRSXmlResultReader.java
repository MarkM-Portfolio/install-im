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


package com.ibm.prs.common.reports.api;

import java.util.Map;
import java.util.Set;

import com.ibm.prs.common.exception.PRSApiException;

/**
 * Interface com.ibm.tivoli.prs.reports.parser.PRSXmlResultReader
 * An interface to define the query API for Prerequisite Scanner results XML file
 * 
 * @author jichen
 * @version 1.0
 *
 */

public interface PRSXmlResultReader {
    
    /** 
     *  Sets the result XML file to be read and processed.
     *  
     *  @param xmlFileName the name of the result XML file
     *  @throws PRSApiException
     */
    public void setXmlFile(String xmlFileName) throws PRSApiException;

    /** 
     *  Checks whether the result XML file is well formatted and validates
     *  it against the Prerequisite Scanner XML schema definition, <code>PRSResults.xsd</code>.
     *  
     *  @return a boolean value to indicate whether the result XML file is valid
     *  @throws PRSApiException
     */
    public boolean isValidXml() throws PRSApiException;
    
    /**
     * Returns the version of the Prerequisite Scanner XML schema definition.
     * 
     * @return a string to represent the Prerequisite Scanner XML schema version
     * @throws PRSApiException
	 * 
     */
    public String getSchemaVersion() throws PRSApiException;
    
    /**
     * Checks whether the scan result information is included in the result XML file.
     * 
     * @return a boolean value to indicate whether the result XML file contains scan result information
     * @throws PRSApiException
     */
    public boolean hasScanResult() throws PRSApiException;
    
    /**
     * Checks whether the scan is successful.
     * 
     * @return a boolean value to indicate whether the scan result is <code>PASS</code>
     * @throws PRSApiException
     */
    public boolean isScanPassed() throws PRSApiException;
    
    /**
     * Checks whether information about Prerequisite Scanner is included in the result XML file.
     * 
     * @return a boolean value to indicate whether the result XML file contains Prerequisite Scanner information
     * @throws PRSApiException
     */
    public boolean hasPRSInfo() throws PRSApiException;
    
    /**
     * Returns the Prerequisite Scanner information from the result XML file.
     * 
     * @return a map with Prerequisite Scanner information.  The key for the map can be:
     * 			<ul> 
     *          <li><code>PRSName</code> - the full name of Prerequisite Scanner</li>
     *          <li><code>PRSVersion</code> - the version of Prerequisite Scanner</li>
     *          <li><code>PRSBuild</code> - the build number of Prerequisite Scanner</li>
     *          <li><code>PRSOutputDir</code> - the output directory of Prerequisite Scanner results</li>
     *          <li><code>PRSResultXmlFile</code> - the fully qualified path to the result XML file</li>
     * 			</ul>        
     * @throws PRSApiException
     */
    public Map<String,String> getPRSInfo() throws PRSApiException;
    
    /**
     * Checks whether information about scanned target system is included in the result XML file.
     * 
     * @return a boolean value to indicate whether the result XML file contains scanned target system information
     * @throws PRSApiException
     */
    public boolean hasMachineInfo() throws PRSApiException;
        
    /**
     * Returns the scanned target system information from the result XML file.
     * 
     * @return a map with scanned target system information.  The key for the map can be: 
     * 			<ul>
     *          <li><code>MachineName</code> - the name of the scanned target system</li>
     *          <li><code>MachineSerialNumber</code> - the serial number of the scanned target system</li>
     *          <li><code>MachineOSSerial</code> - the operating system serial number of the scanned target system</li>
     *          <li><code>MachineOSName</code> - the operating system name of the scanned target system</li>
     *          </ul>
     * @throws PRSApiException
     */
    public Map<String,String> getMachineInfo() throws PRSApiException;
    
    /**
     * Checks whether information about the logged-in user is included in the result XML file.
     * 
     * @return a boolean value to indicate whether the result XML file contains user information
     * @throws PRSApiException
     */
    public boolean hasUserInfo() throws PRSApiException;
    
    /**
     * Returns the user information from the result XML file.
     * 
     * @return a map with user information.  The key for the map can be:
     * 			<ul>
     *          <li><code>UserName</code> - the user who ran Prerequisite Scanner on the target system </li>
     *          </ul>
     * @throws PRSApiException
     */
    public Map<String,String> getUserInfo() throws PRSApiException;
    
    /**
     * Checks whether information about the product is included in the result XML file.
     * 
     * @return a boolean value to indicate whether the result XML file contains product information
     * @throws PRSApiException
     */
    public boolean hasProductInfo() throws PRSApiException;
    
    /**
     * Returns the product information from the result XML file.
     * 
     * @return a set of maps containing product information.  Each map in the set contains information about a scanned product.
     * <p> 			The key for a map can be:</p>
     *  			<ul>
     *              <li><code>ProductCode</code> - the 3-letter product code</li>
     *              <li><code>ProductName</code> - the full official name of the product</li>
     *              <li><code>ProductVersion</code> - the version of the product</li>
     *              </ul>
     * @throws PRSApiException
     */
    public Set<Map<String,String>> getProductInfo() throws PRSApiException;
    
    /**
     * Checks whether scan details, such as results for each prerequisite property, are included in the result XML file.
     * 
     * @return a boolean value to indicate whether whether the result XML file contains details of the scan
     * @throws PRSApiException
     */
    public boolean hasDetailedResults() throws PRSApiException;
 
    /**
     * Returns the scan details from the result XML file.
     * 
     * @return a map with scan details. The key of the map is the product code. 
     * <p>        	The value of the map is a set of maps with the prerequisite properties.  
     *          	Each prerequisite property map contains:</p>
     *          	<ul>
     *              <li><code>PropertyName</code> - the name of the prerequisite property</li>
     *              <li><code>Result</code> - the scan result for that prerequisite property, either pass or fail</li>
     *              <li><code>Found</code> - the actual value for the prerequisite property on the scanned target system</li>
     *              <li><code>Expected</code> - the expected value for the prerequisite property, read from the configuration file</li>
     *              </ul>
     * @throws PRSApiException
     */
    public Map<String,Set<Map<String,String>>> getDetailedResults () throws PRSApiException;
    
    /**
     * Checks whether aggregated scan results from all scanned products and components are included in the result XML file.
     * 
     * @return a boolean value to indicate whether whether the result XML file contains the aggregated scan results from all products and components
     * @throws PRSApiException
     */
    public boolean hasAggregatedResults() throws PRSApiException;
    
    /**
     * Returns the aggregated scan results from all scanned products and components from the result XML file.
     * 
     * @return a set of maps containing aggregated scan results.  Each map in the set contains aggregated results for a prerequisite property. 
     * <p> 			The key for a map can be:</p>
     * 				<ul>
     *              <li><code>PropertyName</code> - the name of the prerequisite property</li>
     *              <li><code>Result</code> - the scan result for that prerequisite property, either pass or fail</li>
     *              <li><code>Found</code> - the actual value for the prerequisite property on the scanned target system</li>
     *              <li><code>Expected</code> - the expected value for the prerequisite property, read from the configuration file</li>
     *              </ul>
     * @throws PRSApiException
     */
    public Set<Map<String,String>> getAggregatedResults () throws PRSApiException;
    
    /**
     * Returns the results from failed prerequisite properties, including failed aggregated results, from the result XML file.
     * 
     * @return a map with failed scan results.
     * <p>      The key of the map is the product code or <code>AGGREGATED</code>.  
     *          The <code>AGGREGATED</code> key represents the failed aggregated scan results.
     *          The value of the map is a set of maps containing failed scan results or failed aggregated scan results. </p>
     * <p>      Each prerequisite property map contains:</p>
     *          	<ul>
     *              <li><code>PropertyName</code> - the name of the prerequisite property</li>
     *              <li><code>Result</code> - the scan result for that prerequisite property, either pass or fail</li>
     *              <li><code>Found</code> - the actual value for the prerequisite property on the scanned target system</li>
     *              <li><code>Expected</code> - the expected value for the prerequisite property, read from the configuration file</li>
     *              </ul>
     * @throws PRSApiException
     */
    public Map<String, Set<Map<String,String>>> getFailedResults () throws PRSApiException;
}
