/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2007, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/*
 * 5724-B88
 * CMVC Location: /wps/fix/src/com/ibm/websphere/product/ValidatePortalVersion.java, wps.base.fix, wps6.fix 
 * adapted from ui/wp/code/wp.migration.core/src/com/ibm/wps/migration/core/util/ValidatePortalVersion.java
 * Version:       1.1 
 * Last Modified: 02/07/07 
 * Revision / History
 *-----------------------------------------------------------------------------
 * CMVC ID    Date       Who      Description
 *----------------------------------------------------------------------------
 *******************************************************************************/
package com.ibm.websphere.product;


import com.ibm.websphere.product.xml.product.product;
import java.io.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *  
 */
public class ValidateProductVersion extends Task {

    // required version
    private String version = null;

    // input XML file that contains Product Xml version info
    private String inFile = null; 

    // Type of comparison to perform// 
    private String operatorType = null;

    //-----------------------------------//
    // Set Methods section               //
    //-----------------------------------//
    public void setInfile(String inFile) {
        this.inFile = inFile;
    }

    /**
	 * @param version  the version to set
	 * @uml.property  name="version"
	 */
    public void setVersion(String version) {
        this.version = version;
    }


    public void setOperatortype(String operatorType) {
        this.operatorType = operatorType;
    }   

    /**
     * main
     */
    public void execute() {

        // Validate the required inputs have been specified. 
        checkInputs();
        
        String productVersionJdkWas = null;
        String productVersionFromXmlFile = null;
        CompareProductVersion productVersionFromFile;
        int indexOfProduct = inFile.indexOf(".product");

        try {
            boolean conditionTrue = false;

            //  Get the version of the existing product 
            if(indexOfProduct > 0) {
                productVersionFromXmlFile = getVersionFromXml(inFile);
                productVersionFromFile = new CompareProductVersion(productVersionFromXmlFile);
            }
            else {
                productVersionJdkWas = productVersionFromJdkWas(inFile);
                //System.out.println("***debug: ValidateProductVersion*** productVersionJdkWas = " + productVersionJdkWas);
                productVersionFromFile = new CompareProductVersion(productVersionJdkWas);
            }

            // Perform the specifiec comparison operation
            if (operatorType.equals("==")) {
                conditionTrue = productVersionFromFile.isEqualTo(version);
            }
            else if (operatorType.equals("!=")) {
                conditionTrue = productVersionFromFile.isNotEqualTo(version);
            }           
            else if (operatorType.equals("<")) {
                conditionTrue = productVersionFromFile.isLessThan(version);
            }
            else if (operatorType.equals(">")) {
                conditionTrue = productVersionFromFile.isGreaterThan(version);
            }   
            else if (operatorType.equals("<=")) {
                conditionTrue = productVersionFromFile.isLessThanOrEqualTo(version);
            } 
            else if (operatorType.equals(">=")) {
                conditionTrue = productVersionFromFile.isGreaterThanOrEqualTo(version);
            }
            else {
                throw new UnsupportedOperationException("Specified operatortype not valid: " + operatorType);
            }


            if (conditionTrue) {
                //MigrationLogger.log( Level.INFO, "Version of Portal is supported by migration", null );

            }
            else {
                //MigrationLogger.log( Level.SEVERE, "The specified portal version is not supported by this migration opertaion: " + productVersionFromXmlFile , null );
                if(indexOfProduct > 0) {
                    throw new BuildException("The specified product version is not supported: version=" + productVersionFromXmlFile );
                }
                else {
                    throw new BuildException("The specified product version is not supported: version=" + productVersionJdkWas );
                }
            }
    
         }
        catch(SAXException e) {
            //MigrationLogger.log( Level.SEVERE, e.getMessage(), null );
            throw new BuildException("Unable to parse file: " + inFile );
        }
        catch(IOException e) {
            //MigrationLogger.log( Level.SEVERE, e.getMessage(), null );
            throw new BuildException("Unable to open InputStream for file: " + inFile );
        }
        catch (UnsupportedOperationException e) {
            //MigrationLogger.log( Level.SEVERE, e.getMessage(), null );
            throw new BuildException("The specified comparison operator is not recognized: operatorType=" + operatorType );
        }
        catch (IllegalArgumentException e) {
            //MigrationLogger.log( Level.SEVERE, e.getMessage(), null );
            if(indexOfProduct > 0) {
                throw new BuildException("The format of the product version is incorrect: version=" + productVersionFromXmlFile );
            }
            else {
                throw new BuildException("The format of the product version is incorrect: version=" + productVersionJdkWas );
            }

        }

    }

    //  Validate the required inputs have been specified  
    private void checkInputs() {

        //  If not null or empty throw build exception
        if (inFile == null || inFile.equals("")) {
            throw new BuildException("The attribute infile must be specified");
        }

        if (operatorType == null) {
            throw new BuildException("The attribute operatortype must be specified");
        }                                                                             

        if (version == null || version.equals("")) {
            throw new BuildException("The attribute version must be specified");
        }

    }

    // Retrieve the actual version from the specified Xml file
    String getVersionFromXml(String sourceFile) throws SAXException, IOException {

        //System.out.println("***debug*** sourceFile = " + sourceFile);
        InputSource xmlFileInputSource = getInputSource(sourceFile);

        // create the processing reader
        SAXParser parser = new SAXParser();


        VersionHandler handler = new VersionHandler();

        // set the handler as needed
        parser.setContentHandler( handler );

        // Parse to retrieve the build version
        parser.parse(xmlFileInputSource);

        // Retrieve the build version as a String
        String version =  handler.getProductVersionFromXmlFile();
        //System.out.println("***debug*** getVersionFromXml version = " + version);

        // Convert to an int value and return it.
        return version;

    }


    /**
     * createSource creates the InputSource from a BufferedReader.
     * @return the new InputSource 
     */
    private InputSource getInputSource( String sourceFile ) {

        FileInputStream is = null;
        try {
            File inputFile = new File( sourceFile );

            is = new FileInputStream( inputFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            //MigrationLogger.log( Level.FINE, e.getMessage(), null );
        }

        return new InputSource( is );
    }

    /**
	 * @return
	 */
    private class VersionHandler extends DefaultHandler {

	  //  Holds our Version information
        CharArrayWriter contents = new CharArrayWriter();
	boolean inVersion = false;
        String version = null;

        private final String REQUEST_ELEMENT = "version";

        public String getProductVersionFromXmlFile() {
            return version;
        }


        public void characters(char ch[], int start, int length)
        throws SAXException
        {
        	// Collect the characters if we are in the Version element.
            if (inVersion) {
            	contents.write(ch, start, length);
            }
   	}   

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {   

            String elementName = sName;		

            // Reset character array writer.
            contents.reset();

            if ("".equals(elementName)) {
                elementName = qName;
            }

            if (elementName.equalsIgnoreCase(REQUEST_ELEMENT)) {
                //version = attrs.getValue("version");
                inVersion = true;
            }
            else {
	        inVersion = false;
	    }

        }  

	// At the end of the version element save the value of the version.
	public void endElement(String namespaceURI, String elementName, String qName) throws SAXException
    	{
                        
        // Save the version value
        if(elementName.equals(REQUEST_ELEMENT))  {

            // Get the URL content
            version = contents.toString().trim();
	  }
	}

	/**
	 * @return  the version
	 * @uml.property  name="version"
	 */
	public String getVersion() {
		return version;
	}
    }

    String productVersionFromJdkWas(String version)
    {
        String currentVersion;
        int sr2;
        int indexOfJava14 = version.indexOf("1.");
        
        currentVersion = version.substring(indexOfJava14 , indexOfJava14 + 5);
        //System.out.println("***debug: productVersionFromJdkWas*** begin index = " + version.substring(indexOfJava14));
        //System.out.println("***debug: productVersionFromJdkWas*** end index = " + version.substring(indexOfJava14 + 5));
        
        // sr = no sr for WAS5 jdk that shipped with 5101
        // sr = cn142sr1w for WAS6 jdk that shipped with 5101
        // SR = SR6 for was5and6 jdk for 5105
        int indexOf_sr = version.indexOf("sr");
        int indexOf_SR = version.indexOf("SR");
        int indexOf_dot42 = version.indexOf(".4.2.");
        
        if(indexOf_sr > 0) {
            currentVersion = currentVersion + "." + version.substring(indexOf_sr + 2, indexOf_sr + 3);
            try {
                sr2 = Integer.parseInt(version.substring(indexOf_sr + 3, indexOf_sr + 4));
                currentVersion = currentVersion + version.substring(indexOf_sr + 3, indexOf_sr + 4);
            } 
            catch ( NumberFormatException e ) {
            }
            catch ( StringIndexOutOfBoundsException e ) {
            }
        }
        else {
            if(indexOf_SR > 0) {
                currentVersion = currentVersion + "." + version.substring(indexOf_SR + 2, indexOf_SR + 3);
                try {
                    sr2 = Integer.parseInt(version.substring(indexOf_SR + 3, indexOf_SR + 4));
                    currentVersion = currentVersion + version.substring(indexOf_SR + 3, indexOf_SR + 4);
                } 
                catch ( NumberFormatException e ) {
                }
                catch ( StringIndexOutOfBoundsException e ) {
                }
            }
            else {    // if neither sr or SR is found, then try to find version from indexOf_dot42
                if(indexOf_dot42 > 0) {
                    try {
                        sr2 = Integer.parseInt(version.substring(indexOf_dot42 + 5, indexOf_dot42 + 6));
                        currentVersion = currentVersion + "." + version.substring(indexOf_dot42 + 5, indexOf_dot42 + 6);
                    } 
                    catch ( NumberFormatException e ) {
                        currentVersion = currentVersion + ".0";
                        System.out.println("ValidateProductVersion: " + e);
                        return currentVersion;
                    }
                    catch ( StringIndexOutOfBoundsException e ) {
                        currentVersion = currentVersion + ".0";
                        System.out.println("ValidateProductVersion: " + e);
                        return currentVersion;
                    }
                    
                    try {
                        sr2 = Integer.parseInt(version.substring(indexOf_dot42 + 6, indexOf_dot42 + 7));
                        currentVersion = currentVersion + version.substring(indexOf_dot42 + 6, indexOf_dot42 + 7);
                    } 
                    catch ( NumberFormatException e ) {
                    }
                    catch ( StringIndexOutOfBoundsException e ) {
                    }
                }
                else {
                    currentVersion = currentVersion + ".0";
                    System.out.println("ValidateProductVersion: Could not find valid version");
                }
            }
        }
        
        return currentVersion;
    }

}
